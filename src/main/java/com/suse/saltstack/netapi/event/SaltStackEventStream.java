package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.listener.SaltEventListener;

/**
 * Concrete implementations of this interface must provide implementations for the
 * methods defined below.
 */
public interface SaltStackEventStream {
    /**
     * Add a {@link SaltEventListener} object to the list of objects to
     * be notified on new event stream events.
     * @param listener Reference to the class that implements {@link SaltEventListener}.
     */
    void addEventListener(SaltEventListener listener);

    /**
     * Allows a listener to remove itself from being notified new event stream events.
     */
    void close();
}
