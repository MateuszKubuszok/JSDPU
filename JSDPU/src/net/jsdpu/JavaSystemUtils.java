/**
 * Copyright 2012-2013 Mateusz Kubuszok
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at</p> 
 * 
 * <p>http://www.apache.org/licenses/LICENSE-2.0</p>
 *
 * <p>Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.</p>
 */
package net.jsdpu;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.util.regex.Pattern.compile;
import static net.jsdpu.logger.Logger.getLogger;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.jsdpu.logger.Logger;
import net.jsdpu.process.executors.MultiCaller;

/**
 * Contains some methods useful in dealing with running new Java processes.
 */
public class JavaSystemUtils {
    private static final Logger logger = getLogger(JavaSystemUtils.class);

    private static String java;

    /**
     * Static class.
     */
    private JavaSystemUtils() {
    }

    /**
     * Returns path to current Java VM executable.
     * 
     * @return path to java executable
     */
    public static String getJavaExecutablePath() {
        if (java == null) {
            java = System.getProperty("java.home");
            if (java.endsWith("/") || java.endsWith("\\"))
                java = java.substring(0, java.length() - 2);
            java += separator + "bin" + separator + "java";
        }
        return java;
    }

    /**
     * Returns path to current Java VM executable (without console output).
     * 
     * @return path to javaw executable
     */
    public static String getJavawExecutablePath() {
        if (java == null) {
            java = System.getProperty("java.home");
            if (java.endsWith("/") || java.endsWith("\\"))
                java = java.substring(0, java.length() - 2);
            java += separator + "bin" + separator + "javaw";
        }
        return java;
    }

    /**
     * Returns path to .class file for a given Class.
     * 
     * @param clazz
     *            Class<?> instance
     * @return path to .class file
     */
    public static String getPathToClass(Class<?> clazz) {
        URL classUrl = MultiCaller.class.getResource(MultiCaller.class.getSimpleName() + ".class");
        try {
            logger.detailedTrace("Calculates path to " + clazz.getSimpleName() + ".class");
            String tmpPath = URLDecoder.decode(classUrl.toString(), "utf-8");
            return new File(tmpPath).getAbsolutePath();
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generates command that will run class' main method in child process
     * 
     * @param clazz
     *            class to be run
     * @param arguments
     *            argument to pass to a new process
     * @return command for a ProcessBuilder
     */
    public static String[] createCommandToRunMain(Class<?> clazz, List<String> arguments) {
        try {
            clazz.getDeclaredMethod("main", new Class[] { String[].class });
        } catch (NoSuchMethodException | SecurityException e) {
            throw new IllegalArgumentException("Class " + clazz.getSimpleName()
                    + " hasn't got main(String[]) method");
        }
        logger.trace("Preparation of " + clazz.getSimpleName() + " run: " + arguments);
        List<String> command = new ArrayList<String>();
        command.add(getJavaExecutablePath());
        command.add("-cp");
        command.add(getClassPathForClass(clazz));
        command.add(clazz.getName());
        command.addAll(arguments);
        logger.detailedTrace(clazz.getSimpleName() + " command: " + command);
        return command.toArray(new String[0]);
    }

    /**
     * Returns ClassPath that will be needed to run main(String[]) method.
     * 
     * @param clazz
     *            class which ClassPath should be obtained
     * 
     * @return ClassPath
     */
    private static String getClassPathForClass(Class<?> clazz) {
        String path = getPathToClass(clazz);
        String classPath;
        if (isRunAsJar(path)) {
            logger.debug(clazz.getSimpleName() + " classpath: " + path);
            Matcher matcher = compile("jar:(file:/)?([^!]+)!.+").matcher(path);
            if (!matcher.find())
                throw new IllegalStateException("Invalid class path");
            classPath = matcher.group(2);
            logger.detailedTrace(clazz.getSimpleName() + " returned classpath: " + classPath);
        } else {
            classPath = getProperty("java.class.path", null);
            logger.debug(clazz.getSimpleName() + " calculated classpath: " + classPath);
        }
        return classPath;
    }

    /**
     * Finds out whether class should be run as JAR or from byte code.
     * 
     * @param path
     *            path to class
     * @return true if class is run from JAR, false otherwise
     */
    private static boolean isRunAsJar(String path) {
        return path.startsWith("jar:");
    }
}
