package com.suse.saltstack.netapi.datatypes;

import java.util.List;

/**
 * Representation of a list of minions associated with given job.
 */
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
