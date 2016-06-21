package com.suse.salt.netapi.results;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Wrapper class for salt-ssh results.
 *
 * @param <T> The type of the value this result holds.
 */
public class SSHResult<T> {

    private String fun;
    @SerializedName("fun_args")
    private List<String> funArgs;
    private String id;
    private String jid;
    private int retcode;
    @SerializedName("return")
    private T returnAttribute;

    public String getFun() {
        return fun;
    }

    public List<String> getFunArgs() {
        return funArgs;
    }

    public String getId() {
        return id;
    }

    public String getJid() {
        return jid;
    }

    public int getRetcode() {
        return retcode;
    }

    public T getReturn() {
        return returnAttribute;
    }
}
