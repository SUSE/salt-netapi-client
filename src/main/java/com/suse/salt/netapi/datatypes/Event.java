package com.suse.salt.netapi.datatypes;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

import static com.suse.salt.netapi.parser.JsonParser.GSON;

/**
 * Parse events into objects.
 */
public class Event {

    private String tag;
    private JsonElement data;

    /**
     * Return this event's tag.
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Return this event's data.
     * @param dataType type token to parse data
     * @param <R> type to parse the data into
     * @return the data
     */
    public <R> R getData(TypeToken<R> dataType) {
        return GSON.fromJson(data, dataType.getType());
    }

    /**
     * Return this event's data parsed into the given type.
     * @param dataType class to parse data
     * @param <R> type to parse the data into
     * @return the data
     */
    public <R> R getData(Class<R> dataType) {
        return GSON.fromJson(data, dataType);
    }

    /**
     * Return this event's data as a Map
     * @return the data
     */
    public Map<String, Object> getData() {
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {};
        return GSON.fromJson(data, typeToken.getType());
    }
}
