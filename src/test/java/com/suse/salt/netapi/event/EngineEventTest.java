package com.suse.salt.netapi.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.google.gson.JsonElement;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.parser.JsonParser;

import org.junit.Test;

/**
 * EngineEvent test class
 */
public class EngineEventTest {

    @Test
    public void testParseMasterEvent() {
        String message = "{\"tag\": \"salt/engines/libvirt_events/some/more/data\", " +
                          "\"data\": {\"_stamp\": \"2015-05-05T18:33:07.408179\", \"key\": \"value\"}}";
        Event event = JsonParser.EVENTS.parse(message);

        EngineEvent actual = EngineEvent.parse(event).get();
        assertEquals("libvirt_events", actual.getEngine());
        assertFalse(actual.getMinionId().isPresent());
        assertEquals("some/more/data", actual.getAdditional());
        assertEquals("2015-05-05T18:33:07.408179", actual.getTimestamp());

        JsonElement data = actual.getData(JsonElement.class);
        assertEquals("value", data.getAsJsonObject().get("key").getAsString());
    }

    @Test
    public void testParseMinionEvent() {
        String message = "{\"tag\": \"salt/engines/libvirt_events/some/more/data\", " +
                           "\"data\": {\"_stamp\": \"2015-05-05T18:33:07.408179\", \"_cmd\": \"_minion_event\"," +
                                     " \"data\": {\"key\": \"value\"}, \"id\": \"172.16.1.121\"}}";
        Event event = JsonParser.EVENTS.parse(message);

        EngineEvent actual = EngineEvent.parse(event).get();
        assertEquals("libvirt_events", actual.getEngine());
        assertEquals("172.16.1.121", actual.getMinionId().get());
        assertEquals("some/more/data", actual.getAdditional());
        assertEquals("2015-05-05T18:33:07.408179", actual.getTimestamp());

        JsonElement data = actual.getData(JsonElement.class);
        assertEquals("value", data.getAsJsonObject().get("key").getAsString());
    }
}
