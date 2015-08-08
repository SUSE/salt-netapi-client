package com.suse.saltstack.netapi.calls;

import com.google.gson.reflect.TypeToken;

/**
 * Common class representing a scheduled job
 * @param <R> the return type of the called function
 */
public class ScheduledJob<R> {

    private String jid;
    private TypeToken<R> type;

    public String getJid() {
        return jid;
    }

    public TypeToken<R> getType() {
        return type;
    }

    public void setType(TypeToken<R> type) {
        this.type = type;
    }
}
