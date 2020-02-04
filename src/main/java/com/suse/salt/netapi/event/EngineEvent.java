package com.suse.salt.netapi.event;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.parser.JsonParser;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an event fired by engines
 */
public class EngineEvent {
    private static final Pattern PATTERN = Pattern.compile("^salt/engines/([^/]+)/(.*)$");

    private static final Gson GSON = JsonParser.GSON;

    private final String engine;
    private final String timestamp;
    private final String additional;
    private final Optional<String> minionId;
    private final JsonElement data;

    /**
     * Creates a new EngineEvent
     *
     * @param engine the engine name
     * @param additional additional information depending on the engine
     * @param timestamp datetime of the event
     * @param minionId minion id if the event comes from a minion,
     *                 empty it comes from the master
     * @param data data containing more information about this event
     */
    private EngineEvent(String engine, String additional, String timestamp,
                        Optional<String> minionId, JsonElement data) {
        this.engine = engine;
        this.additional = additional;
        this.timestamp = timestamp;
        this.minionId = minionId;
        this.data = data;
    }

    /**
     * Returns the engine name.
     *
     * @return the engine name
     */
    public String getEngine() {
        return engine;
    }

    /**
     * Provides additional information from the tag depending on the type of engine
     *
     * @return additional information
     */
    public String getAdditional() {
        return additional;
    }

    /**
     * Returns the timestamp of the event
     *
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the id of the minion that triggered the engine event
     *
     * @return the minion id
     */
    public Optional<String> getMinionId() {
        return minionId;
    }

    /**
     * Return the event data parsed into the given type.
     * @param type type token to parse data
     * @param <R> type to parse the data into
     * @return the event data
     */
    public <R> R getData(TypeToken<R> type) {
        return GSON.fromJson(data, type.getType());
    }

    /**
     * Return this event's data parsed into the given type.
     * @param type class to parse data
     * @param <R> type to parse the data into
     * @return the data
     */
    public <R> R getData(Class<R> type) {
        return GSON.fromJson(data, type);
    }

    /**
     * Return event data as Map
     * @return event data as map
     */
    public Map<String, Object> getData() {
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {};
        return getData(typeToken);
    }

    /**
     * Utility method to parse e generic event to a more specific one
     * @param event the generic event to parse
     * @return an option containing the parsed value or non if it could not be parsed
     */
    public static Optional<EngineEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        JsonElement data = event.getData(JsonElement.class);

        if (matcher.matches() && data.isJsonObject()) {
            Optional<String> minionId = Optional.empty();
            JsonObject obj = data.getAsJsonObject();
            String timestamp = obj.get("_stamp").getAsString();

            if (obj.has("data") && obj.has("id")) {
                minionId = Optional.of(obj.get("id").getAsString());
                data = obj.get("data");
            } else {
                obj.remove("_stamp");
            }

            EngineEvent result = new EngineEvent(matcher.group(1), matcher.group(2), timestamp, minionId, data);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
