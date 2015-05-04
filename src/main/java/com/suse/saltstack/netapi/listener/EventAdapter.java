package com.suse.saltstack.netapi.listener;

/**
 * Defines an empty implementation of {@link EventListener}, the notification
 * interface for different implementations of SSE stream events.
 */
public class EventAdapter implements EventListener {

    @Override
    public void notify(String event) { }

    @Override
    public void eventStreamClosed() { }
}
