package com.suse.salt.netapi.results;

/**
 * Wrapper class for salt-ssh results.
 *
 */
public class SSHRawResult {

    private int retcode;
    private String stderr;
    private String stdout;

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
