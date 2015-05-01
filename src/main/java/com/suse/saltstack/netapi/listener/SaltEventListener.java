package com.suse.saltstack.netapi.listener;

/**
 * Defines a notification interface for different implementations of SSE stream events
 */
public interface SaltEventListener {

    /**
     * Notify the listener of a new event stream event.  Returned data is in JSON format.
     * @param data Return a JSON representation of the latest stream event.
     */
    void notify(String data);

    /**
     * Notify the listener that the backing event stream was closed.  Listener may
     * need to recreate the event stream or take other actions.
     */
    void eventStreamClosed();
}
