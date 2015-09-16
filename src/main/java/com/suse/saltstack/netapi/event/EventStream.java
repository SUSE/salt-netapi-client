package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.datatypes.Event;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.JsonParser;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Event stream implementation based on a {@link ClientEndpoint} WebSocket.
 * It is used to connect the WebSocket to a {@link ServerEndpoint}
 * and receive messages from it; for each message a bunch of {@link EventListener}
 * will be recalled and notified with it.
 */
@ClientEndpoint
public class EventStream implements AutoCloseable {

    /**
     * Listeners that are notified of a new events.
     */
    private final List<EventListener> listeners = new ArrayList<>();

    /**
     * Default message buffer size in characters.
     */
    private final int defaultBufferSize = 1024;

    /**
     * Buffer for partial messages.
     */
    private final StringBuilder partialMessageBuffer = new StringBuilder(defaultBufferSize);

    /**
     * The {@link WebSocketContainer} object for a @ClientEndpoint implementation.
     */
    private final WebSocketContainer websocketContainer =
            ContainerProvider.getWebSocketContainer();

    /**
     * The WebSocket {@link Session}.
     */
    public Session session;

    /**
     * Constructor used to create this object.
     * Automatically open a WebSocket and start event processing.
     *
     * @param config Contains the necessary details such as EndPoint URL and
     * authentication token required to create the WebSocket.
     */
    public EventStream(ClientConfig config) {
        try {
            initializeStream(config);
        } catch (SaltStackException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method initiates the WebSocket handshake and its life.
     * This method is intended for use during unit testing only!
     * End users should not call this methods directly.
     *
     * @param uri WebSocket URI
     * @throws DeploymentException If annotatedEndpoint instance is not valid.
     * @throws IOException If WebSocket connection to remote server fails.
     */
    public void processEvents(URI uri, ClientConfig config)
            throws DeploymentException, IOException {
        synchronized (websocketContainer) {
            this.session = websocketContainer.connectToServer(this, uri);
            this.session.setMaxIdleTimeout(
                    config.get(ClientConfig.SOCKET_TIMEOUT));
        }
    }

    /**
     * Connect the WebSocket to the server pointing to /ws/{token} to receive events.
     */
    private void initializeStream (ClientConfig config) throws SaltStackException {
        try {
            URI uri = config.get(ClientConfig.URL);
            uri = new URI(uri.getScheme() == "https" ? "wss" : "ws",
                    uri.getSchemeSpecificPart(), uri.getFragment())
                    .resolve("/ws/" + config.get(ClientConfig.TOKEN));
            websocketContainer.setDefaultMaxSessionIdleTimeout(
                    config.get(ClientConfig.SOCKET_TIMEOUT));
            processEvents(uri, config);
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new SaltStackException(e);
        }
    }

    /**
     * Implementation of {@link EventStream#addEventListener(EventListener)}
     *
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    public void addEventListener(EventListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Implementation of {@link EventStream#removeEventListener(EventListener)}.
     *
     * @param listener Reference to the class that implements {@link EventListener}.
     */
    public void removeEventListener(EventListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Helper method that returns the current number of subscribed listeners.
     *
     * @return The current number listeners.
     */
    public int getListenerCount() {
        synchronized (listeners) {
            return listeners.size();
        }
    }

    /**
     * Helper method to check if the WebSocket Session exists and is open.
     *
     * @return A flag indicating the {@link EventStream}
     * WebSocket {@link Session} state.
     */
    public boolean isEventStreamClosed() {
        return this.session == null || !this.session.isOpen();
    }

    /**
     * Close the WebSocket {@link Session}.
     */
    @Override
    public void close() {
        if (!isEventStreamClosed()) {
            try {
                this.session.close(new CloseReason(CloseCodes.GOING_AWAY,
                        "The listener has closed the event stream"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * On handshake completed, get the WebSocket Session and send
     * a message to ServerEndpoint that WebSocket is ready.
     * http://docs.saltstack.com/en/latest/ref/netapi/all/salt.netapi.rest_cherrypy.html#ws
     *
     * @param session The just started WebSocket {@link Session}.
     * @param config The {@link EndpointConfig} containing the handshake informations.
     * @throws IOException Exception thrown if something goes wrong sending message
     * to the remote peer.
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        this.session = session;
        session.getBasicRemote().sendText("websocket client ready");
    }

    /**
     * Notify listeners on each event received on the websocket and buffer partial messages.
     *
     * @param partialMessage partial message received on this websocket
     * @param last indicate the last part of a message
     */
    @OnMessage
    public void onMessage(String partialMessage, boolean last) {
        if (last) {
            String message;
            if (partialMessageBuffer.length() == 0) {
                message = partialMessage;
            } else {
                partialMessageBuffer.append(partialMessage);
                message = partialMessageBuffer.toString();
            }

            // Notify all registered listeners
            if (!message.equals("server received message")) {
                // Salt API adds a "data: " prefix that we need to ignore
                Event event = JsonParser.EVENTS.parse(message.substring(6));
                synchronized (listeners) {
                    listeners.stream().forEach(l -> l.notify(event));
                }
            }

            // Empty the buffer (and reset size to the defaultBufferSize)
            partialMessageBuffer.setLength(0);
            partialMessageBuffer.trimToSize();
            partialMessageBuffer.ensureCapacity(defaultBufferSize);
        } else {
            partialMessageBuffer.append(partialMessage);
        }
    }

    /**
     * On error, close all objects of this class.
     *
     * @param t The Throwable object received on the current error.
     */
    @OnError
    public void onError(Throwable t) {
        this.close();
        t.printStackTrace();
    }

    /**
     * On closing the websocket, refresh the session and notify all subscribed listeners.
     * Upon exit from this method, all subscribed listeners will be removed.
     *
     * @param session the websocket {@link Session}
     * @param closeReason the {@link CloseReason} for the websocket closure
     */
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        this.session = session;

        // Notify all the listeners and cleanup
        synchronized (listeners) {
            listeners.stream().forEach(l -> l.eventStreamClosed(closeReason));

            // Clear out the listeners
            listeners.clear();
        }
    }
}
