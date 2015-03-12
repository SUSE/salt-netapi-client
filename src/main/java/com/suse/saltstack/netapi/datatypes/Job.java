package com.suse.saltstack.netapi.datatypes;

import java.util.List;

public class Job {

    private String jid;

    private List<String> minions;

    public String getJid() {
        return jid;
    }

    public List<String> getMinions() {
        return minions;
    }
}
