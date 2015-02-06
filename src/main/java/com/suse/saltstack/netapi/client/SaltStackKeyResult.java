package com.suse.saltstack.netapi.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rolf on 2/5/15.
 */
public class SaltStackKeyResult {

    private List<SaltStackKey> local = new ArrayList<>();
    private List<SaltStackKey> minions = new ArrayList<>();

    public List<SaltStackKey> getLocal() {
        return local;
    }
    public List<SaltStackKey> getMinions() {
        return minions;
    }

    public void addKey(String keyFinger, String minion) {
        if (minion.matches("^master.*")) {
            local.add(new SaltStackKey(keyFinger, minion));
        } else {
            minions.add(new SaltStackKey(keyFinger, minion));
        }
    }
}
