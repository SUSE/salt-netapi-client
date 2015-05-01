package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.event.impl.JerseyServerSentEvents;

/**
 * Factory class that allows for the creation of
 */
public class EventStreamFactory {

    /**
     * Constant defining the Glassfish Jersey SSE event implementation
     */
    public static final Integer JERSEY_SSE_TYPE_STREAM = 1;

    /**
     *
     * @param config
     * @param streamType
     * @return
     */
    public static SaltStackEventStream create(ClientConfig config, Integer streamType) {
        switch(streamType) {
            default: {
                return (SaltStackEventStream)new JerseyServerSentEvents(config);
            }
        }
    }
}
