package com.suse.salt.netapi.datatypes;

import java.util.Map;

/**
 * Parse events into objects.
 */
public class Event {

    private String tag;
    private Map<String, Object> data;

    /**
     * Return this event's tag.
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Return this event's data.
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }
}
