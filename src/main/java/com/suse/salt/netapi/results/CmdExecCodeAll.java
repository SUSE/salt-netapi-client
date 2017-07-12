package com.suse.salt.netapi.results;

/**
 * Result structure as returned by cmd.exec_code_all to be parsed from event data.
 */
public class CmdExecCodeAll {

    private long pid;
    private int retcode;
    private String stderr;
    private String stdout;

    public long getPid() {
        return pid;
    }

    public int getRetcode() {
        return retcode;
    }

    public String getStderr() {
        return stderr;
    }

    public String getStdout() {
        return stdout;
    }
}
