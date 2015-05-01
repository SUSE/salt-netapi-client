package com.suse.saltstack.netapi.listener;

/**
 * Defines an empty implementation of {@link SaltEventListener}, the notification
 * interface for different implementations of SSE stream events
 */
public class SaltEventAdapter implements SaltEventListener {

    @Override
    public void notify(String event) {}

    @Override
    public void eventStreamClosed() {}
}
