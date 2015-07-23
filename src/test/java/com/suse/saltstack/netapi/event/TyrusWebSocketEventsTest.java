package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.client.SaltStackClient;
import com.suse.saltstack.netapi.config.ClientConfig;

import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.DeploymentException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * SaltStack events API WebSocket implementation test cases.
 */
public class TyrusWebSocketEventsTest {

    private static final int MOCK_HTTP_PORT = 8888;
    private static final String MOCK_HTTP_HOST = "localhost";
    private static final String WEBSOCKET_PATH = "/ws";
    private static final String WEBSOCKET_ENDPOINT = "/token";

    /**
     * An instance of a {@link Server}
     */
    private Server serverEndpoint;

    /**
     * The WebSocket URI path to connect to the server.
     */
    private URI ws_uri;

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
        URI uri = URI.create("http://" + MOCK_HTTP_HOST + ":"
                + Integer.toString(MOCK_HTTP_PORT));
        SaltStackClient client = new SaltStackClient(uri);

        serverEndpoint = new Server(MOCK_HTTP_HOST, MOCK_HTTP_PORT, WEBSOCKET_PATH,
                null, WebSocketServerSalt.class);
        serverEndpoint.start();

        ClientConfig config = client.getConfig();
        ws_uri = new URI(config.get(ClientConfig.URL).toString().replace("http", "ws")
                + WEBSOCKET_PATH + WEBSOCKET_ENDPOINT);
    }

    /**
     * Tests: listener insertion, listener notification, stream closed, stream content.
     * Source file contains 6 events: asserts listener notify method is called 6 times.
     *
     * @throws IOException Exception creating the {@link EventStream}
     * @throws DeploymentException Exception connecting WebSocket to {@link Server}
     * @throws InterruptedException Exception using {@link CountDownLatch}
     */
    @Test
    public void shouldFireNotifyMultipleTimes()
            throws IOException, DeploymentException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        int target = 6;

        try (EventStream streamEvents = new EventStream()) {
            EventCountClient eventCountClient = new EventCountClient(target, latch);
            streamEvents.addEventListener(eventCountClient);

            streamEvents.processEvents(ws_uri);
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue(eventCountClient.counter == target);
            streamEvents.close();
        }
    }

    /**
     * Tests: stream event content
     *
     * @throws IOException Exception creating the {@link EventStream}
     * @throws DeploymentException Exception connecting WebSocket to {@link Server}
     * @throws InterruptedException Exception using {@link CountDownLatch}
     */
    @Test
    public void testEventMessageContent()
            throws IOException, DeploymentException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        try (EventStream streamEvents = new EventStream()) {
            EventContentClient eventContentClient = new EventContentClient(latch);
            streamEvents.addEventListener(eventContentClient);

            streamEvents.processEvents(ws_uri);
            latch.await(30, TimeUnit.SECONDS);
            synchronized (eventContentClient.events) {
                Assert.assertTrue(eventContentClient.events.get(1)
                        .contains("\"jid\": \"20150505113307407682\""));
            }
            streamEvents.close();
        }
    }

    /**
     * Tests: listener management - count: +1 +1 +1 -1 -1 +1 == 2
     *
     * @throws IOException Exception creating the {@link EventStream}
     */
    @Test
    public void testListenerManagement() throws IOException {
        SimpleEventListenerClient client1 = new SimpleEventListenerClient();
        SimpleEventListenerClient client2 = new SimpleEventListenerClient();
        SimpleEventListenerClient client3 = new SimpleEventListenerClient();
        SimpleEventListenerClient client4 = new SimpleEventListenerClient();

        try (EventStream streamEvents = new EventStream()) {
            streamEvents.addEventListener(client1);
            streamEvents.addEventListener(client2);
            streamEvents.addEventListener(client3);
            streamEvents.removeEventListener(client2);
            streamEvents.removeEventListener(client3);
            streamEvents.addEventListener(client4);

            Assert.assertTrue(streamEvents.getListenerCount() == 2);
        }
    }

    /**
     * Tests: event processing WebSocket session not open
     *
     * @throws IOException Exception creating the {@link EventStream}
     */
    @Test
    public void testEventProcessingStateStopped() throws IOException {
        try (EventStream streamEvents = new EventStream()) {
            SimpleEventListenerClient eventListener = new SimpleEventListenerClient();
            streamEvents.addEventListener(eventListener);

            Assert.assertTrue(streamEvents.isEventStreamClosed());
        }
    }

    /**
     * Tests: event stream WebSocket session not closed
     *
     * @throws IOException Exception creating the {@link EventStream}
     * @throws DeploymentException Exception connecting WebSocket to {@link Server}
     * @throws InterruptedException Exception using {@link CountDownLatch}
     */
    @Test
    public void testEventStreamClosed()
            throws IOException, DeploymentException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        try (EventStream streamEvents = new EventStream()) {
            EventStreamClosedClient eventStreamClosedClient =
                    new EventStreamClosedClient(latch);
            streamEvents.addEventListener(eventStreamClosedClient);

            streamEvents.processEvents(ws_uri);
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertFalse(streamEvents.isEventStreamClosed());
            streamEvents.close();
        }
    }

    /**
     * At the end of the test {@link Server} stops
     * and release its address for other test execution.
     */
    @After
    public void stop() {
        serverEndpoint.stop();
    }

    /**
     * Event listener client used for testing.
     */
    private class EventCountClient implements EventListener {
        private int targetCount;
        private int counter = 0;
        private CountDownLatch latch;

        public EventCountClient(int targetCount, CountDownLatch latchIn) {
            this.targetCount = targetCount;
            this.latch = latchIn;
        }

        @Override
        public void notify(String event) {
            counter++;
            if (counter == targetCount)
                latch.countDown();
        }

        @Override
        public void eventStreamClosed() {
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventContentClient implements EventListener {
        List<String> events = new ArrayList<>();
        CountDownLatch latch;

        public EventContentClient(CountDownLatch latchIn) {
            this.latch = latchIn;
        }

        @Override
        public void notify(String event) {
            synchronized (events) {
                events.add(event);
                if (events.size() > 2)
                    latch.countDown();
            }
        }

        @Override
        public void eventStreamClosed() {
        }
    }

    /**
     * Simple Event ListenerClient
     */
    private class SimpleEventListenerClient implements EventListener {
        @Override
        public void notify(String event) {
        }

        @Override
        public void eventStreamClosed() {
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventStreamClosedClient implements EventListener {
        private CountDownLatch latch;

        public EventStreamClosedClient(CountDownLatch latchIn) {
            this.latch = latchIn;
        }

        @Override
        public void notify(String event) {
            this.latch.countDown();
        }

        @Override
        public void eventStreamClosed() {
        }
    }
}
