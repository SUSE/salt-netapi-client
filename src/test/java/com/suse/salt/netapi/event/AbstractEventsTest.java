package com.suse.salt.netapi.event;

import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Before;

import java.net.URI;
import java.net.URISyntaxException;
import javax.websocket.DeploymentException;

/**
 * Base class for running tests involving the WebSocket based event stream.
 */
public abstract class AbstractEventsTest {

    private static final int MOCK_WEBSOCKET_PORT = 8889;
    private static final String MOCK_WEBSOCKET_HOST = "localhost";
    private static final String MOCK_WEBSOCKET_PATH = "/ws";

    /**
     * An instance of a {@link Server}
     */
    protected Server serverEndpoint;

    /**
     * The WebSocket URI path to connect to the server.
     */
    protected URI uri;

    /**
     * For each test make sure that the server endpoint to serve the event stream is started
     * and prepare WebSocket related config parameters.
     *
     * @throws DeploymentException Exception thrown if something wrong
     * starting the {@link Server}.
     * @throws URISyntaxException Exception on wrong {@link URI} syntax.
     */
    @Before
    public void init() throws DeploymentException, URISyntaxException {
        serverEndpoint = new Server(MOCK_WEBSOCKET_HOST, MOCK_WEBSOCKET_PORT,
                MOCK_WEBSOCKET_PATH, null, config());
        serverEndpoint.start();

        uri = new URI("http://" + MOCK_WEBSOCKET_HOST + ":" + MOCK_WEBSOCKET_PORT);
    }

    @After
    public void cleanup() {
        serverEndpoint.stop();
    }

    public abstract Class<?> config();
}
