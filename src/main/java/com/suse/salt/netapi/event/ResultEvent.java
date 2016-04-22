package com.suse.salt.netapi.event;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.parser.JsonParser;

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

    private static final Gson GSON = JsonParser.GSON;

    private final String jid;
    private final String minionId;
    private final JsonElement data;

    /**
     * Creates a new ResultEvent
     * @param jid the id of the job this event is about
     * @param minionId the id of the minion sending the event
     * @param data data containing more information about this event
     */
    public ResultEvent(String jid, String minionId, JsonElement data) {
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
     * Return event data as Map
     * @return event data as map
     */
    public Map<String, Object> getData() {
        TypeToken<Map<String, Object>> typeToken = new TypeToken<Map<String, Object>>() {};
        return getData(typeToken);
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
     * Utility method to parse e generic event to a more specific one
     * @param event the generic event to parse
     * @return an option containing the parsed value or non if it could not be parsed
     */
    public static Optional<ResultEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        if (matcher.matches()) {
            ResultEvent result = new ResultEvent(matcher.group(1),
                    matcher.group(2), event.getData(JsonElement.class));
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}

