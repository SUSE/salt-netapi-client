package com.suse.salt.netapi.event;

import com.suse.salt.netapi.config.ClientConfig;
import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Before;

import javax.websocket.DeploymentException;
import java.net.URI;
import java.net.URISyntaxException;

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
    protected URI wsUri;

    /**
     * Client configuration used in every test
     */
    protected ClientConfig clientConfig;

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

        wsUri = new URI("ws://" + MOCK_WEBSOCKET_HOST + ":" + MOCK_WEBSOCKET_PORT)
                .resolve(MOCK_WEBSOCKET_PATH);

        clientConfig = new ClientConfig();
        clientConfig.put(ClientConfig.TOKEN, "token");
        clientConfig.put(ClientConfig.URL, wsUri);
    }

    @After
    public void cleanup() {
        serverEndpoint.stop();
    }

    public abstract Class<?> config();
}
