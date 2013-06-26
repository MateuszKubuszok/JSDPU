package net.jsdpu;

import net.jsdpu.process.executors.IProcessExecutor;
import net.jsdpu.process.killers.IProcessKiller;

/**
 * Interface describing used operating system, as well as classes performing
 * some system-dependent operations.
 * 
 * @see net.jsdpu.process.executors.IProcessExecutor
 * @see net.jsdpu.process.killers.IProcessKiller
 */
public interface IOperatingSystem {
    /**
     * Returns family name for current operating system.
     * 
     * @return family name
     */
    public String getFamilyName();

    /**
     * Returns path to local application data.
     * 
     * @return path to local application data
     */
    public String getLocalAppData();

    /**
     * Returns instance of ProcessExecutor suitable for current system.
     * 
     * @see net.jsdpu.process.executors.IProcessExecutor
     * 
     * @return ProcessExecutor
     */
    public IProcessExecutor getProcessExecutor();

    /**
     * Returns instance of ProcessKiller suitable for current system.
     * 
     * @see net.jsdpu.process.killers.IProcessKiller
     * 
     * @return ProcessKiller
     */
    public IProcessKiller getProcessKiller();

    /**
     * Command that tests whether or not Executor works.
     * 
     * <p>
     * Since available commands are system-dependent it has to be defined here.
     * </p>
     * 
     * @return commands that tests executor under given system
     */
    public String getTestCommand();
}
