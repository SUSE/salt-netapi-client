package com.suse.saltstack.netapi.event.impl;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.event.SaltStackEventStream;
import com.suse.saltstack.netapi.listener.SaltEventListener;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Glassfish Jersey SSE events implementation.
 */
public class JerseyServerSentEvents implements SaltStackEventStream {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Listeners that are notified of a new events.
     */
    private final List<SaltEventListener> listeners = new ArrayList<>();

    /**
     * The ClientConfig used to create the GET request for /events
     */
    private ClientConfig config;
    /**
     * Private reference to the Jersey SSE specific event stream
     */
    private EventInput eventInput;

    /**
     * Constructor used to create this object
     * @param config Contains the necessary details such as endpoint URL and
     *               authentication token required to create the request to obtain
     *               the {@link EventInput} event stream.
     */
    public JerseyServerSentEvents(ClientConfig config) {
        this.config = config;
        initializeStream();
    }

    /**
     * Implementation of {@link SaltStackEventStream#addEventListener(SaltEventListener)}
     * @param listener Reference to the class that implements {@link SaltEventListener}.
     */
    public void addEventListener(SaltEventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Implementation of {@link SaltStackEventStream#close()}
     */
    public void close() {
        //TODO: May need logic in here to only shutdown the executor if this was
        // the last listener.
        eventInput.close();
        executor.shutdownNow();
    }

    /**
     * Perform the REST GET call to /events and setup the event stream.
     */
    private void initializeStream() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //TODO: Need to account for possible HTTP Proxy here
                Client client = ClientBuilder.newBuilder().register(
                        new SseFeature()).build();
                WebTarget target = client.target(config.get(ClientConfig.URL) +
                        "/events");
                Invocation.Builder builder = target.request(new MediaType("text",
                        "event-stream"));
                builder.header("X-Auth-Token", config.get(ClientConfig.TOKEN));

                eventInput = builder.get(EventInput.class);
                while (!eventInput.isClosed()) {
                    final InboundEvent inboundEvent = eventInput.read();
                    if (inboundEvent == null) {
                        // connection has been closed
                        for (SaltEventListener listener : listeners) {
                            listener.eventStreamClosed();
                        }
                        break;
                    }
                    for (SaltEventListener listener : listeners) {
                        listener.notify(inboundEvent.readData(String.class));
                    }
                }
            }
        };
        executor.submit(runnable);
    }
}
