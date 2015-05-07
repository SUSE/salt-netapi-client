package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.exception.SaltStackException;

import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.media.sse.EventInput;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.glassfish.jersey.media.sse.SseFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Event stream implementation based on GlassFish Jersey Server-Sent Events (SSE).
 */
public class EventStream implements AutoCloseable {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Listeners that are notified of a new events.
     */
    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * The ClientConfig used to create the GET request for /events
     */
    private ClientConfig config;

    /**
     * Internal state flag designating whether event processing loop
     * has started;
     */
    private boolean eventProcessingStarted = false;

    /**
     * Private reference to the Jersey SSE specific event stream
     */
    private EventInput eventInput;

    /**
     * Constructor used to create this object.  Automatically starts
     * event processing.
     * @param config Contains the necessary details such as endpoint URL and
     *               authentication token required to create the request to obtain
     *               the {@link EventInput} event stream.
     */
    public EventStream(ClientConfig config) {
        this.config = config;
        initializeStream();
    }

    /**
     * An alternate constructor used to create this object.  Does not
     * automatically start event processing.  This constructor is used for
     * unit testing only and allows for the injection of an existing and
     * possibly pre-configured {@link Client} object.
     * @param config Contains the necessary details such as endpoint URL and
     *               authentication token required to create the request to obtain
     *               the {@link EventInput} event stream.
     * @param client Passed client to use when configuring the event stream
     */
    public EventStream(ClientConfig config, Client client) {
        client.register(new SseFeature());
        WebTarget target = client.target(config.get(ClientConfig.URL) + "/events");
        Invocation.Builder builder = target.request(new MediaType("text",
                "event-stream"));
        builder.header("X-Auth-Token", config.get(ClientConfig.TOKEN));

        eventInput = builder.get(EventInput.class);
    }

    /**
     * This method initiates the event processing loop. This method is intended for
     * use during unit testing only!  End users should not call this methods directly.
     */
    public void processEvents() {
        if (!eventProcessingStarted) {
            if (eventInput != null && !eventInput.isClosed()) {
                eventProcessingStarted = true;
                while (!eventInput.isClosed()) {
                    final InboundEvent inboundEvent = eventInput.read();
                    if (inboundEvent == null) {
                        // connection has been closed
                        close();
                        break;
                    }
                    synchronized (listeners) {
                        for (EventListener listener : listeners) {
                            listener.notify(inboundEvent.readData(String.class));
                        }
                    }
                }
            }
        }
    }

    /**
     * Implementation of {@link EventStream#addEventListener(EventListener)}
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    public void addEventListener(EventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Implementation of {@link EventStream#removeEventListener(EventListener)}
     * @param listener
     */
    public void removeEventListener(EventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Helper method that returns the current number of subscribed
     * listeners.
     * @return The current number listeners.
     */
    public int getListenerCount() {
        return listeners.size();
    }

    /**
     * Closes the backing event stream and notifies all subscribed listeners that
     * the event stream has been closed via {@link EventListener#eventStreamClosed()}.
     * Upon exit from this method, all subscribed listeners will be removed.
     */
    public void close() {
        synchronized (listeners) {
            // notify all the listeners and cleanup
            for (EventListener listener : listeners) {
                listener.eventStreamClosed();
            }
            // clear out the listeners
            listeners.clear();
            // close the backing event stream
            eventInput.close();
            // shut down the executor
            executor.shutdownNow();
            // reset processing flag
            eventProcessingStarted = false;
        }
    }

    /**
     * Helper method to determine whether the backing event stream is closed.
     * @return Whether the {@link EventInput} stream is closed.
     */
    public boolean isEventStreamClosed() {
        return eventInput.isClosed();
    }

    /**
     * Helper method to determine whether event processing has started.
     * @return Whether event processing has started.
     */
    public boolean isEventProcessingStarted() {
        return eventProcessingStarted;
    }

    /**
     * Perform the REST GET call to /events and set up the event stream.  If
     * a proxy is configured be sure to account for it.
     */
    private void initializeStream() {
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws SaltStackException {
                org.glassfish.jersey.client.ClientConfig jerseyConfig =
                        new org.glassfish.jersey.client.ClientConfig();
                // set the connection timeout as per
                jerseyConfig.property(ClientProperties.CONNECT_TIMEOUT,
                        config.get(ClientConfig.CONNECT_TIMEOUT));

                // Configure jersey client for proxy if specified in configuration
                URI uri = config.get(ClientConfig.URL);
                String proxyHost = config.get(ClientConfig.PROXY_HOSTNAME);
                if (proxyHost != null) {
                    Integer proxyPort = config.get(ClientConfig.PROXY_PORT);
                    try {
                        URI proxyUri = new URI(uri.getScheme(), null, proxyHost,
                                proxyPort, null, null, null);
                        jerseyConfig.property(ClientProperties.PROXY_URI,
                                proxyUri.toURL());
                        jerseyConfig.property(ClientProperties.PROXY_USERNAME,
                                config.get(ClientConfig.PROXY_USERNAME));
                        jerseyConfig.property(ClientProperties.PROXY_PASSWORD,
                                config.get(ClientConfig.PROXY_PASSWORD));
                    } catch (URISyntaxException | MalformedURLException e) {
                        throw new SaltStackException(e);
                    }
                }

                Client client = ClientBuilder.newClient(jerseyConfig).register(
                        new SseFeature());
                WebTarget target = client.target(uri + "/events");
                Invocation.Builder builder = target.request(new MediaType("text",
                        "event-stream"));
                builder.header("X-Auth-Token", config.get(ClientConfig.TOKEN));

                eventInput = builder.get(EventInput.class);
                processEvents();
                return null;
            }
        };
        executor.submit(callable);
    }
}
