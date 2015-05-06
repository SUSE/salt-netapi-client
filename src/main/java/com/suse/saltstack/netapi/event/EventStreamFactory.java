package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.event.impl.JerseyServerSentEvents;

/**
 * Factory class that allows for the creation of a specific implementation
 * of {@link EventStream}.
 */
public class EventStreamFactory {

    /**
     * Instantiate and return an instance of {@link EventStream}. The passed
     * {@link ClientConfig} object must contain a valid
     * {@link com.suse.saltstack.netapi.datatypes.Token} object as authentication
     * is required prior to subscribing to the event stream.
     * @param config Client configuration to use when creating the event stream.
     * @return Instance of {@link EventStream} based on passed streamType.
     */
    public static EventStream create(ClientConfig config) {
        return new JerseyServerSentEvents(config);
    }
}
