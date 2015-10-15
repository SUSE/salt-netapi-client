package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.datatypes.Event;

import org.glassfish.tyrus.server.Server;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;

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

    /**
     * An instance of a {@link Server}
     */
    private Server serverEndpoint;

    /**
     * The WebSocket URI path to connect to the server.
     */
    private URI wsUri;

    /**
     * Client configuration used in every test
     */
    private ClientConfig clientConfig;

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
                null, WebSocketServerSalt.class);
        serverEndpoint.start();

        wsUri = new URI("ws://" + MOCK_HTTP_HOST + ":" + MOCK_HTTP_PORT)
                .resolve(WEBSOCKET_PATH);

        clientConfig = new ClientConfig();
        clientConfig.put(ClientConfig.TOKEN, "token");
        clientConfig.put(ClientConfig.URL, wsUri);
    }

    /**
     * Tests: listener insertion, listener notification, stream closed, stream content.
     * Source file contains 6 events: asserts listener notify method is called 6 times.
     *
     * @throws Exception in case of an error
     */
    @Test
    public void shouldFireNotifyMultipleTimes() throws Exception {
        int target = 6;
        CountDownLatch latch = new CountDownLatch(1);
        EventCountClient eventCountClient = new EventCountClient(target, latch);

        try (EventStream streamEvents = new EventStream(clientConfig, eventCountClient)) {
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue(eventCountClient.counter == target);
        }
    }

    /**
     * Tests: stream event content
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testEventMessageContent() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        EventContentClient eventContentClient = new EventContentClient(latch);

        try (EventStream streamEvents = new EventStream(clientConfig, eventContentClient)) {
            latch.await(30, TimeUnit.SECONDS);
            synchronized (eventContentClient.events) {
                Event event = eventContentClient.events.get(1);
                Assert.assertTrue(event.getData().containsKey("jid"));
                Assert.assertEquals("20150505113307407682", event.getData().get("jid"));
            }
        }
    }

    /**
     * Tests: listener management - count: +1 +1 +1 -1 -1 +1 == 2
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testListenerManagement() throws Exception {
        SimpleEventListenerClient client1 = new SimpleEventListenerClient();
        SimpleEventListenerClient client2 = new SimpleEventListenerClient();
        SimpleEventListenerClient client3 = new SimpleEventListenerClient();
        SimpleEventListenerClient client4 = new SimpleEventListenerClient();

        try (EventStream streamEvents = new EventStream(clientConfig, client1, client2)) {
            streamEvents.addEventListener(client3);
            streamEvents.removeEventListener(client2);
            streamEvents.removeEventListener(client3);
            streamEvents.addEventListener(client4);

            Assert.assertTrue(streamEvents.getListenerCount() == 2);
        }
    }

    /**
     * Test event processing websocket session closed by the listener.
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testEventProcessingStateStopped() throws Exception {
        SimpleEventListenerClient eventListener = new SimpleEventListenerClient();
        EventStream streamEvents = new EventStream(clientConfig, eventListener);
        streamEvents.close();
        Assert.assertTrue(streamEvents.isEventStreamClosed());
        Assert.assertEquals(CloseCodes.GOING_AWAY,
                eventListener.closeReason.getCloseCode());
        String message = "The listener has closed the event stream";
        Assert.assertEquals(message, eventListener.closeReason.getReasonPhrase());
    }

    /**
     * Tests: event stream WebSocket session not closed
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testEventStreamClosed() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        EventStreamClosedClient eventListener = new EventStreamClosedClient(latch);

        try (EventStream streamEvents = new EventStream(clientConfig, eventListener)) {
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertFalse(streamEvents.isEventStreamClosed());
        }
    }

    /**
     * Test setting a maximum message length and make sure the stream is closed.
     *
     * @throws Exception in case of an error
     */
    @Test
    public void testMaxMessageLength() throws Exception {
        int maxMessageLength = 10;
        clientConfig.put(ClientConfig.WEBSOCKET_MAX_MESSAGE_LENGTH, maxMessageLength);
        CountDownLatch latch = new CountDownLatch(1);
        EventStreamClosedClient eventListener = new EventStreamClosedClient(latch);

        try (EventStream streamEvents = new EventStream(clientConfig, eventListener)) {
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue(streamEvents.isEventStreamClosed());
            Assert.assertEquals(CloseCodes.TOO_BIG,
                    eventListener.closeReason.getCloseCode());
            String message = "Message length exceeded the configured maximum (" +
                    maxMessageLength + " characters)";
            Assert.assertEquals(message, eventListener.closeReason.getReasonPhrase());
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
        private final int targetCount;
        private int counter = 0;
        private final CountDownLatch latch;

        public EventCountClient(int targetCount, CountDownLatch latchIn) {
            this.targetCount = targetCount;
            this.latch = latchIn;
        }

        @Override
        public void notify(Event event) {
            counter++;
            if (counter == targetCount) {
                latch.countDown();
            }
        }

        @Override
        public void eventStreamClosed(CloseReason closeReason) {
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventContentClient implements EventListener {
        List<Event> events = new ArrayList<>();
        CountDownLatch latch;

        public EventContentClient(CountDownLatch latchIn) {
            this.latch = latchIn;
        }

        @Override
        public void notify(Event event) {
            synchronized (events) {
                events.add(event);
                if (events.size() > 2) {
                    latch.countDown();
                }
            }
        }

        @Override
        public void eventStreamClosed(CloseReason closeReason) {
        }
    }

    /**
     * Simple Event ListenerClient
     */
    private class SimpleEventListenerClient implements EventListener {
        CloseReason closeReason;

        @Override
        public void notify(Event event) {
        }

        @Override
        public void eventStreamClosed(CloseReason closeReason) {
            this.closeReason = closeReason;
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventStreamClosedClient implements EventListener {
        private final CountDownLatch latch;
        CloseReason closeReason;

        public EventStreamClosedClient(CountDownLatch latchIn) {
            this.latch = latchIn;
        }

        @Override
        public void notify(Event event) {
            this.latch.countDown();
        }

        @Override
        public void eventStreamClosed(CloseReason closeReason) {
            this.closeReason = closeReason;
            this.latch.countDown();
        }
    }
}
