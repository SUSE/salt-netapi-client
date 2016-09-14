package com.suse.salt.netapi.results;

/**
 * Wrapper class for salt-ssh results.
 *
 */
public class SSHRawResult {

    public SSHRawResult() {

    }

    public SSHRawResult(int retcode, String stdout, String stderr) {
        this.retcode = retcode;
        this.stdout = stdout;
        this.stderr = stderr;
    }

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

    @Override
    public boolean equals(Object obj) {
        SSHRawResult other = (SSHRawResult) obj;
        return other.getRetcode() == retcode && other.getStderr().equals(stderr)
                && other.getStdout().equals(stdout);
    }
}
