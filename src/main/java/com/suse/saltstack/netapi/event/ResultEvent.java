package com.suse.saltstack.netapi.event;

import com.suse.saltstack.netapi.datatypes.Event;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an event containing the result of some function call
 */
public class ResultEvent {

    private static final Pattern PATTERN =
            Pattern.compile("^salt/job/(\\d{20})/ret/([^/]+)$");

    private final String jid;
    private final String minionId;
    private final Map<String, Object> data;

    /**
     * Creates a new ResultEvent
     * @param jid the id of the job this event is about
     * @param minionId the id of the minion sending the event
     * @param data data containing more information about this event
     */
    public ResultEvent(String jid, String minionId, Map<String, Object> data) {
        this.jid = jid;
        this.minionId = minionId;
        this.data = data;
    }

    /**
     * The job id to which the result belongs
     *
     * @return the job id
     */
    public String getJid() {
        return jid;
    }

    /**
     * The id of the minion from which the result came
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
    public static Optional<ResultEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        if (matcher.matches()) {
            ResultEvent result = new ResultEvent(matcher.group(1),
                    matcher.group(2), event.getData());
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}

