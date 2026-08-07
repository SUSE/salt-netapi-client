package com.suse.salt.netapi.results;

public class FullCmdResult {
    private int retcode;
    private String ret;
    private String jid;

    public int getRetcode() {
        return retcode;
    }

    public void setRetcode(int retcode) {
        this.retcode = retcode;
    }

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public String getJid() {
        return jid;
    }

    public void setJid(String jid) {
        this.jid = jid;
    }

    @Override
    public String toString() {
        return "FullCmdResult(retcode=" + retcode +
                ", ret=\"" + ret +
                "\", jid=" + jid + ")";
    }
}
