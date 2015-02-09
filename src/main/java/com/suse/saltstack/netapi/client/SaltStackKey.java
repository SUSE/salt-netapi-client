package com.suse.saltstack.netapi.client;

/**
 * Represents a key from a Salt minion or master.
 */
public class SaltStackKey {
    private String keyFinger;
    private String minionId;

    public SaltStackKey(String keyFinger, String minionId) {
        this.keyFinger = keyFinger;
        this.minionId = minionId;
    }

    public String geFingerprint() {
        return keyFinger;
    }

    public String getMinionId() {
        return minionId;
    }
}
