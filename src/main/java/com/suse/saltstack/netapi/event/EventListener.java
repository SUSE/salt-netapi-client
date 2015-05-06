package com.suse.saltstack.netapi.event;

/**
 * Defines a client notification interface for SSE stream events.
 */
public interface EventListener {

    /**
     * Notify the listener of a new event stream event.  Returned data is a {@link String}
     * in JSON format.
     * @param event Return a JSON representation of the latest stream event.
     */
    void notify(String event);

    /**
     * Notify the listener that the backing event stream was closed.  Listener may
     * need to recreate the event stream or take other actions.
     */
    void eventStreamClosed();
}
