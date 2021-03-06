package com.suse.salt.netapi.event;

import com.suse.salt.netapi.datatypes.Event;

/**
 * Defines a client notification interface for events stream.
 */
public interface EventListener {

    /**
     * Notify the listener of a new event. Returned data is a {@link Event} object.
     * @param event object representation of the latest stream event
     */
    void notify(Event event);

    /**
     * Notify the listener that the backing event stream was closed.  Listener may
     * need to recreate the event stream or take other actions.
     * @param code an integer code to represent the reason for closing
     * @param phrase a String representation of code
     */
    void eventStreamClosed(int code, String phrase);
}
