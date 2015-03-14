package com.suse.saltstack.netapi.datatypes;

import java.util.List;

/**
 * Representation of a scheduled job and a list of minions associated with it.
 */
public class JobMinions {

    private String jid;
    private List<String> minions;

    public String getJid() {
        return jid;
    }

    public List<String> getMinions() {
        return minions;
    }
}
