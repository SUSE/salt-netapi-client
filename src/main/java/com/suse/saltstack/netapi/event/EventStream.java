package com.suse.saltstack.netapi.event;

/**
 * Concrete implementations must implement the methods defined below.
 */
public interface EventStream extends AutoCloseable {
    /**
     * Add a {@link EventListener} object to the list of objects to
     * be notified of new event stream events.
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    void addEventListener(EventListener listener);

    /**
     * Remove a {@link EventListener} object from the list of objects to
     * be notified of event stream events.
     * @param listener Listener to be remove from stream events notifications.
     */
    void removeEventListener(EventListener listener);

    /**
     * Closes the backing event stream and notifies all subscribed listeners that
     * the event stream has been closed via {@link EventListener#eventStreamClosed()}.
     */
    void close();
}
