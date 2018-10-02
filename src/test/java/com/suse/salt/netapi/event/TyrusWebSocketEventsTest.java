package com.suse.salt.netapi.event;

import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Token;
import org.junit.Assert;
import org.junit.Test;

import javax.websocket.CloseReason.CloseCodes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Salt events API WebSocket implementation test cases.
 */
public class TyrusWebSocketEventsTest extends AbstractEventsTest {

    @Override
    public Class<?> config() {
        return TyrusWebSocketEventsTestMessages.class;
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

        try (EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"), 0, 0, 0, eventCountClient)) {
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

        try (EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"),
                0, 0, 0, eventContentClient)) {
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

        try (EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"), 0, 0, 0, client1, client2)) {
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
        EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"), 0, 0, 0, eventListener);
        streamEvents.close();
        Assert.assertTrue(streamEvents.isEventStreamClosed());
        Assert.assertEquals(CloseCodes.GOING_AWAY.getCode(),
                eventListener.closeCode);
        String message = "The listener has closed the event stream";
        Assert.assertEquals(message, eventListener.closePhrase);
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

        try (EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"), 0, 0, 0, eventListener)) {
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
        CountDownLatch latch = new CountDownLatch(1);
        EventStreamClosedClient eventListener = new EventStreamClosedClient(latch);

        try (EventStream streamEvents = new WebSocketEventStream(uri, new Token("token"), 0, 0,
                maxMessageLength, eventListener)) {
            latch.await(30, TimeUnit.SECONDS);
            Assert.assertTrue(streamEvents.isEventStreamClosed());
            Assert.assertEquals(CloseCodes.TOO_BIG.getCode(),
                    eventListener.closeCode);
            String message = "Message length exceeded the configured maximum (" +
                    maxMessageLength + " characters)";
            Assert.assertEquals(message, eventListener.closePhrase);
        }
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
        public void eventStreamClosed(int code, String phrase) {
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
        public void eventStreamClosed(int code, String phrase) {
        }
    }

    /**
     * Simple Event ListenerClient
     */
    private class SimpleEventListenerClient implements EventListener {
        int closeCode;
        String closePhrase;

        @Override
        public void notify(Event event) {
        }

        @Override
        public void eventStreamClosed(int code, String phrase) {
            this.closeCode = code;
            this.closePhrase = phrase;
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventStreamClosedClient implements EventListener {
        private final CountDownLatch latch;
        int closeCode;
        String closePhrase;

        public EventStreamClosedClient(CountDownLatch latchIn) {
            this.latch = latchIn;
        }

        @Override
        public void notify(Event event) {
            this.latch.countDown();
        }

        @Override
        public void eventStreamClosed(int code, String phrase) {
            this.closeCode = code;
            this.closePhrase = phrase;
            this.latch.countDown();
        }
    }
}
