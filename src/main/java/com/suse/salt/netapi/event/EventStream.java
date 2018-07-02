package com.suse.salt.netapi.event;

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

import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.exception.MessageTooBigException;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.parser.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private final int defaultBufferSize = 0x400;

    /**
     * Maximum message length in characters
     */
    private final int maxMessageLength;

    /**
     * Buffer for partial messages.
     */
    private final StringBuilder messageBuffer = new StringBuilder(defaultBufferSize);

    /**
     * The {@link WebSocketContainer} object for a @ClientEndpoint implementation.
     */
    private final WebSocketContainer websocketContainer =
            ContainerProvider.getWebSocketContainer();

    /**
     * The WebSocket {@link Session}.
     */
    private Session session;

    /**
     * Constructor used to create an event stream: open a websocket connection and start
     * event processing.
     *
     * @param listeners event listeners to be added before stream initialization
     * @throws SaltException in case of an error during stream initialization
     */
    public EventStream(URI uri, Token token, long sessionIdleTimeout, long idleTimeout,
                       int maxMsgSize, EventListener... listeners)
            throws SaltException {
        maxMessageLength = maxMsgSize > 0 ?
                maxMsgSize : Integer.MAX_VALUE;
        Arrays.asList(listeners).forEach(this::addEventListener);
        initializeStream(uri, token, sessionIdleTimeout, idleTimeout);
    }

    /**
     * Connect the WebSocket to the server pointing to /ws/{token} to receive events.
     *
     * @throws SaltException in case of an error during stream initialization
     */
    private void initializeStream(URI uri, Token token, long sessionIdleTimeout, long idleTimeout)
            throws SaltException {
        try {
            uri = new URI(uri.getScheme() == "https" ? "wss" : "ws",
                    uri.getSchemeSpecificPart(), uri.getFragment())
                    .resolve("/ws/" + token.getToken());
            websocketContainer.setDefaultMaxSessionIdleTimeout(sessionIdleTimeout);

            // Initiate the websocket handshake
            synchronized (websocketContainer) {
                session = websocketContainer.connectToServer(this, uri);
                session.setMaxIdleTimeout(idleTimeout);
            }
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new SaltException(e);
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
     *
     * @throws IOException in case of an error when closing the session
     */
    @Override
    public void close() throws IOException {
        close(new CloseReason(CloseCodes.GOING_AWAY,
                "The listener has closed the event stream"));
    }

    /**
     * Close the WebSocket {@link Session} with a given close reason.
     *
     * @param closeReason the reason for the websocket closure
     * @throws IOException in case of an error when closing the session
     */
    public void close(CloseReason closeReason) throws IOException {
        if (!isEventStreamClosed()) {
            session.close(closeReason);
        }
    }

    /**
     * On handshake completed, get the WebSocket Session and send
     * a message to ServerEndpoint that WebSocket is ready.
     * http://docs.saltstack.com/en/latest/ref/netapi/all/salt.netapi.rest_cherrypy.html#ws
     *
     * @param session The just started WebSocket {@link Session}.
     * @param config The {@link EndpointConfig} containing the handshake informations.
     * @throws IOException if something goes wrong sending message to the remote peer
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
     * @throws MessageTooBigException in case the message is longer than maxMessageLength
     */
    @OnMessage
    public void onMessage(String partialMessage, boolean last)
            throws MessageTooBigException {
        if (partialMessage.length() > maxMessageLength - messageBuffer.length()) {
            throw new MessageTooBigException(maxMessageLength);
        }

        if (last) {
            String message;
            if (messageBuffer.length() == 0) {
                message = partialMessage;
            } else {
                messageBuffer.append(partialMessage);
                message = messageBuffer.toString();

                // Reset the size to the defaultBufferSize and empty the buffer
                messageBuffer.setLength(defaultBufferSize);
                messageBuffer.trimToSize();
                messageBuffer.setLength(0);
            }

            // Notify all registered listeners
            if (!message.equals("server received message")) {
                // Salt API adds a "data: " prefix that we need to ignore
                Event event = JsonParser.EVENTS.parse(message.substring(6));
                synchronized (listeners) {
                    List<EventListener> collect = listeners.stream()
                            .collect(Collectors.toList());
                    collect.forEach(listener -> listener.notify(event));
                }
            }
        } else {
            messageBuffer.append(partialMessage);
        }
    }

    /**
     * On error, convert {@link Throwable} into {@link CloseReason} and close the session.
     *
     * @param throwable The Throwable object received on the current error.
     * @throws IOException in case of an error when closing the session
     */
    @OnError
    public void onError(Throwable throwable) throws IOException {
        close(new CloseReason(throwable instanceof MessageTooBigException ?
                CloseCodes.TOO_BIG : CloseCodes.CLOSED_ABNORMALLY, throwable.getMessage()));
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
            listeners.stream().forEach(listener -> listener.eventStreamClosed(closeReason));

            // Clear out the listeners
            listeners.clear();
        }
    }
}
