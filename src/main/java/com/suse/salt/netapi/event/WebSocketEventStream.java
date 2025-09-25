package com.suse.salt.netapi.event;

import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.CloseReason.CloseCodes;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.DeploymentException;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import jakarta.websocket.server.ServerEndpoint;

import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.exception.MessageTooBigException;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.parser.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Event stream implementation based on a {@link ClientEndpoint} WebSocket.
 * It is used to connect the WebSocket to a {@link ServerEndpoint}
 * and receive messages from it.
 */
@ClientEndpoint
public class WebSocketEventStream extends AbstractEventStream {

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
     * @param uri salt api url
     * @param listeners event listeners to be added before stream initialization
     * @param idleTimeout idle timeout to pass to the http client config
     * @param maxMsgSize maximum event data size to accept
     * @param sessionIdleTimeout session idle timeout to pass to the http client config
     * @param token salt session token to use for authentication
     * @throws SaltException in case of an error during stream initialization
     */
    public WebSocketEventStream(URI uri, Token token, long sessionIdleTimeout, long idleTimeout,
                                int maxMsgSize, EventListener... listeners)
            throws SaltException {
        maxMessageLength = maxMsgSize > 0 ?
                maxMsgSize : Integer.MAX_VALUE;
        Arrays.asList(listeners).forEach(this::addEventListener);
        initializeStream(uri.resolve("/"), token, sessionIdleTimeout, idleTimeout);
    }

    /**
     * Connect the WebSocket to the server pointing to /ws/{token} to receive events.
     *
     * @throws SaltException in case of an error during stream initialization
     */
    private void initializeStream(URI uri, Token token, long sessionIdleTimeout, long idleTimeout)
            throws SaltException {
        try {
            URI adjustedURI = new URI(uri.getScheme() == "https" ? "wss" : "ws",
                    uri.getSchemeSpecificPart(), uri.getFragment())
                    .resolve("ws/" + token.getToken());
            websocketContainer.setDefaultMaxSessionIdleTimeout(sessionIdleTimeout);

            // Initiate the websocket handshake
            synchronized (websocketContainer) {
                session = websocketContainer.connectToServer(this, adjustedURI);
                session.setMaxIdleTimeout(idleTimeout);
            }
        } catch (URISyntaxException | DeploymentException | IOException e) {
            throw new SaltException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEventStreamClosed() {
        return this.session == null || !this.session.isOpen();
    }

    /**
     * {@inheritDoc}
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
    private void close(CloseReason closeReason) throws IOException {
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
                notifyListeners(event);
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

        clearListeners(closeReason.getCloseCode().getCode(), closeReason.getReasonPhrase());
    }
}
