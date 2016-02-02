package com.suse.salt.netapi.event;

import com.suse.salt.netapi.datatypes.Event;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an event fired when a minion connects to the salt master
 */
public class MinionStartEvent {

    private static final Pattern PATTERN =
            Pattern.compile("^salt/minion/([^/]+)/start$");

    private final String minionId;
    private final Map<String, Object> data;

    /**
     * Creates a new MinionStartEvent
     * @param minionId the id of the minion sending the event
     * @param data data containing more information about this event
     */
    public MinionStartEvent(String minionId, Map<String, Object> data) {
        this.minionId = minionId;
        this.data = data;
    }

    /**
     * The id of the minion that started
     *
     * @return the minion id
     */
    public String getMinionId() {
        return minionId;
    }

    /**
     * The event data containing more information about this event
     *
     * @return the event data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Utility method to parse e generic event to a more specific one
     * @param event the generic event to parse
     * @return an option containing the parsed value or non if it could not be parsed
     */
    public static Optional<MinionStartEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        if (matcher.matches()) {
            MinionStartEvent result = new MinionStartEvent(matcher.group(1),
                    event.getData());
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
