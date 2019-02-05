package com.suse.salt.netapi.results;

/**
 * Result structure as returned by cmd.exec_code_all, cmd.run_all, cmd.script to be parsed from event data.
 */
public class CmdArtifacts {

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

    @Override
    public String toString() {
        return "CmdArtifacts(pid=" + pid + ", retcode=" + retcode + ", stderr=\"" + stderr + "\", stdout=\"" + stdout + "\")";
    }
}
