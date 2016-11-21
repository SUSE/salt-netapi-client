package com.suse.salt.netapi.event;

import com.suse.salt.netapi.config.ClientConfig;
import org.glassfish.tyrus.server.Server;
import org.junit.Before;

import javax.websocket.DeploymentException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 */
public abstract class EventsInit {

    private static final int MOCK_HTTP_PORT = 8889;
    private static final String MOCK_HTTP_HOST = "localhost";
    private static final String WEBSOCKET_PATH = "/ws";

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
     * Prepare test environment: start a server on localhost
     * and prepare WebSocket parameters.
     *
     * @throws DeploymentException Exception thrown if something wrong
     * starting the {@link Server}.
     * @throws URISyntaxException Exception on wrong {@link URI} syntax.
     */
    @Before
    public void init() throws DeploymentException, URISyntaxException {
        serverEndpoint = new Server(MOCK_HTTP_HOST, MOCK_HTTP_PORT, WEBSOCKET_PATH,
                null, config());
        serverEndpoint.start();

        wsUri = new URI("ws://" + MOCK_HTTP_HOST + ":" + MOCK_HTTP_PORT)
                .resolve(WEBSOCKET_PATH);

        clientConfig = new ClientConfig();
        clientConfig.put(ClientConfig.TOKEN, "token");
        clientConfig.put(ClientConfig.URL, wsUri);
    }

    public abstract Class<?> config();
}
