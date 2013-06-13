package net.jsdpu.process.executors;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.System.*;
import static net.jsdpu.JavaSystemUtils.createCommandToRunMain;
import static net.jsdpu.logger.Logger.getLogger;
import static net.jsdpu.process.executors.Commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.jsdpu.logger.Logger;

import com.google.common.base.Function;

/**
 * Class used to replace multiple commands calls with one.
 * 
 * <p>
 * Used by some ProcessExecutors to execute multiple commands with the privilege
 * elevation.
 * </p>
 */
public class MultiCaller {
    private static final Logger logger = getLogger(MultiCaller.class);

    /**
     * Generates command that will allow to run multiple commands through
     * MultiCaller front end.
     * 
     * <p>
     * Used by some ProcessExecutors to run multiple commands with one request
     * for the privilege elevation.
     * </p>
     * 
     * @param commands
     *            commands that should be run by MultiCaller
     * @return list of commands for ProcessBulder/ProcessExecutor
     */
    static String[] prepareCommand(List<String[]> commands) {
        logger.trace("Preparation of MultiCaller run: " + commands);
        List<String> arguments = newArrayList(transform(commands, new Function<String[], String>() {
            @Override
            public String apply(String[] subCommand) {
                return wrapArgument(joinArguments(subCommand));
            }
        }));
        String[] command = createCommandToRunMain(MultiCaller.class, arguments);
        logger.detailedTrace("MultiCaller command: " + command);
        return command;
    }

    /**
     * Runs each argument as a command, treating it as if it were a console
     * line.
     * 
     * <p>
     * Results are redirected to output - both standard output and error output.
     * </p>
     * 
     * @param args
     *            commands to run
     */
    public static void main(String[] args) {
        try {
            for (String[] command : convertMultipleConsoleCommands(args)) {
                try {
                    Process process = new ProcessBuilder(command).start();

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            process.getInputStream()));
                    BufferedReader error = new BufferedReader(new InputStreamReader(
                            process.getErrorStream()));

                    String line;

                    while ((line = in.readLine()) != null)
                        out.println(line);
                    while ((line = error.readLine()) != null)
                        err.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InvalidCommandException e) {
            e.printStackTrace();
        }
    }
}
