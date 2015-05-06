package com.suse.saltstack.netapi.event;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.suse.saltstack.netapi.client.SaltStackClient;
import com.suse.saltstack.netapi.event.impl.JerseyServerSentEvents;
import com.suse.saltstack.netapi.utils.ClientUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

/**
 * SaltStack events API Jersey SSE implementation test cases.
 */
public class JerseyServerSentEventsTest {

    private static final int MOCK_HTTP_PORT = 8888;

    /**
     * Note: {@link org.glassfish.jersey.media.sse.EventInput} expects the raw event
     * stream to be \n\n delimited.  From the source code "SSE event chunk parser - SSE
     * chunks are delimited with a fixed "\n\n" delimiter in the response stream."  The
     * events_stream.txt file below is in the correct format.  If modifying the file to
     * add additional events etc, please take note.
     */
    static final String TEXT_EVENT_STREAM_RESPONSE = ClientUtils.streamToString(
            JerseyServerSentEventsTest.class.getResourceAsStream("/events_stream.txt"));

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(MOCK_HTTP_PORT);

    private SaltStackClient client;

    @Before
    public void init() {
        URI uri = URI.create("http://localhost:" + Integer.toString(MOCK_HTTP_PORT));
        client = new SaltStackClient(uri);
    }

    /**
     * Tests: listener insertion, listener notification, stream closed,
     * stream content
     */
    @Test
    public void shouldFireNotifyMultipleTimes() {
        stubFor(get(urlEqualTo("/events"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "text/event-stream")
                .withHeader("Connection", "keep-alive")
                .withBody(TEXT_EVENT_STREAM_RESPONSE)));

        Client jerseyClient = ClientBuilder.newClient();
        JerseyServerSentEvents jerseyServerSentEvents = new JerseyServerSentEvents(
                client.getConfig(), jerseyClient);

        EventCountClient eventCountClient = new EventCountClient(6);
        jerseyServerSentEvents.addEventListener(eventCountClient);

        jerseyServerSentEvents.processEvents();
    }

    /**
     * Tests: stream event content
     */
    @Test
    public void testEventMessageContent() {
        stubFor(get(urlEqualTo("/events"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "text/event-stream")
                .withHeader("Connection", "keep-alive")
                .withBody(TEXT_EVENT_STREAM_RESPONSE)));

        Client jerseyClient = ClientBuilder.newClient();
        JerseyServerSentEvents jerseyServerSentEvents = new JerseyServerSentEvents(
                client.getConfig(), jerseyClient);

        EventContentClient eventContentClient = new EventContentClient();
        jerseyServerSentEvents.addEventListener(eventContentClient);

        jerseyServerSentEvents.processEvents();
    }

    /**
     * Tests: listener management - count: add/add/add/minus/minus/add == 2
     */
    @Test
    public void testListenerManagement() {
        SimpleEventListenerClient client1 = new SimpleEventListenerClient();
        SimpleEventListenerClient client2 = new SimpleEventListenerClient();
        SimpleEventListenerClient client3 = new SimpleEventListenerClient();
        SimpleEventListenerClient client4 = new SimpleEventListenerClient();

        stubFor(get(urlEqualTo("/events"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "text/event-stream")
                .withHeader("Connection", "keep-alive")
                .withBody(TEXT_EVENT_STREAM_RESPONSE)));

        Client jerseyClient = ClientBuilder.newClient();
        JerseyServerSentEvents jerseyServerSentEvents = new JerseyServerSentEvents(
                client.getConfig(), jerseyClient);

        jerseyServerSentEvents.addEventListener(client1);
        jerseyServerSentEvents.addEventListener(client2);
        jerseyServerSentEvents.addEventListener(client3);
        jerseyServerSentEvents.removeEventListener(client2);
        jerseyServerSentEvents.removeEventListener(client3);
        jerseyServerSentEvents.addEventListener(client4);

        Assert.assertTrue(jerseyServerSentEvents.getListenerCount() == 2);

        jerseyServerSentEvents.close();
    }

    /**
     * Tests: event processing state flag not started
     */
    @Test
    public void testEventProcessingStateStopped() {
        stubFor(get(urlEqualTo("/events"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "text/event-stream")
                .withHeader("Connection", "keep-alive")
                .withBody(TEXT_EVENT_STREAM_RESPONSE)));

        Client jerseyClient = ClientBuilder.newClient();
        JerseyServerSentEvents jerseyServerSentEvents = new JerseyServerSentEvents(
                client.getConfig(), jerseyClient);

        EventContentClient eventContentClient = new EventContentClient();
        jerseyServerSentEvents.addEventListener(eventContentClient);

        jerseyServerSentEvents.processEvents();

        Assert.assertFalse(jerseyServerSentEvents.isEventProcessingStarted());
    }

    /**
     * Tests: event stream closed flag not closed
     */
    @Test
    public void testEventStreamClosed() {
        stubFor(get(urlEqualTo("/events"))
                .willReturn(aResponse()
                .withHeader("Content-Type", "text/event-stream")
                .withHeader("Connection", "keep-alive")
                .withBody(TEXT_EVENT_STREAM_RESPONSE)));

        Client jerseyClient = ClientBuilder.newClient();
        JerseyServerSentEvents jerseyServerSentEvents = new JerseyServerSentEvents(
                client.getConfig(), jerseyClient);

        EventStreamClosedClient eventStreamClosedClient =
                new EventStreamClosedClient(jerseyServerSentEvents);
        jerseyServerSentEvents.addEventListener(eventStreamClosedClient);

        jerseyServerSentEvents.processEvents();
    }

    /**
     * Event listener client used for testing.
     */
    private class EventCountClient implements EventListener {
        private int targetCount;
        private int counter = 0;

        public EventCountClient(int targetCount) {
            this.targetCount = targetCount;
        }

        @Override
        public void notify(String event) {
            // do something
            counter++;
        }

        @Override
        public void eventStreamClosed() {
            // do something
            Assert.assertTrue(counter == targetCount);
        }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventContentClient implements EventListener {
        List<String> events = new ArrayList<>();

        @Override
        public void notify(String event) {
            events.add(event);
        }

        @Override
        public void eventStreamClosed() {
            Assert.assertTrue(events.get(2).contains("\"jid\": \"20150505113307407682\""));
        }
    }

    /**
     * Simple Event ListenerClient
     */
    private class SimpleEventListenerClient implements EventListener {
        @Override
        public void notify(String event) { }

        @Override
        public void eventStreamClosed() { }
    }

    /**
     * Event listener client used for testing.
     */
    private class EventStreamClosedClient implements EventListener {
        JerseyServerSentEvents eventSource;

        public EventStreamClosedClient(JerseyServerSentEvents eventSource) {
            this.eventSource = eventSource;
        }

        @Override
        public void notify(String event) {
            Assert.assertFalse(eventSource.isEventStreamClosed());
        }

        @Override
        public void eventStreamClosed() {
        }
    }
}
