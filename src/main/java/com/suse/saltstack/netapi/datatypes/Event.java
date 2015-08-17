package com.suse.saltstack.netapi.datatypes;

import java.util.HashMap;

/**
 * Parse events into objects.
 */
public class Event {

    private String tag;
    private HashMap<String, Object> data;

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
    public HashMap<String, Object> getData() {
        return data;
    }
}
