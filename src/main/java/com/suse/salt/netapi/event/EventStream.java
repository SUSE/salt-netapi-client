package com.suse.salt.netapi.event;

import com.suse.salt.netapi.datatypes.Event;

import java.io.IOException;

/**
 * Represents a container of listener to {@link Event}; for each message a bunch of
 * {@link EventListener} will be recalled and notified with it.
 */
public interface EventStream extends AutoCloseable {

    /**
     * Adds a listener.
     *
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    void addEventListener(EventListener listener);

    /**
     * Removes a listener.
     *
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    void removeEventListener(EventListener listener);

    /**
     * Helper method that returns the current number of subscribed listeners.
     *
     * @return The current number listeners.
     */
    int getListenerCount();

    /**
     * Helper method to check if the stream is able to receive Events.
     *
     * @return A flag indicating the Stream state.
     */
    boolean isEventStreamClosed();

    /**
     * Closes this Stream; Events will not be posted to listeners after this call.
     *
     * @throws IOException in case of an error when closing the session
     */
    @Override
    void close() throws IOException;

}
