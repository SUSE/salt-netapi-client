package com.suse.salt.netapi.event;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.parser.JsonParser;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents an event fired when a batch job starts.
 */
public class BatchStartedEvent {

    private static final Pattern PATTERN = Pattern.compile("^salt/batch/([^/]+)/start$");
    private static final Gson GSON = JsonParser.GSON;

    private String jobId;
    private Data data;

    /**
     * Creates a new BatchStartedEvent
     * @param jobIdIn the id of the job
     * @param dataIn the data containing more information about this event
     */
    public BatchStartedEvent(String jobIdIn, Data dataIn) {
        super();
        this.jobId = jobIdIn;
        this.data = dataIn;
    }

    /**
     * @return the event data
     */
    public Data getData() {
        return data;
    }

    /**
     * The id of the job
     *
     * @return job id
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Utility method to parse a generic event into a more specific one.
     *
     * @param event the generic event to parse
     * @return an option containing the parsed value or non if it could not be parsed
     */
    public static Optional<BatchStartedEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        if (matcher.matches()) {
            return Optional.of(new BatchStartedEvent(matcher.group(1), event.getData(Data.class)));
        }
        return Optional.empty();
    }

    /**
     * Data object of the batch started event
     */
    public static class Data {
        @SerializedName("_stamp")
        private String timestamp;
        @SerializedName("available_minions")
        private List<String> availableMinions;
        @SerializedName("down_minions")
        private List<String> downMinions;
        private Optional<JsonElement> metadata = Optional.empty();

        public String getTimestamp() {
            return timestamp;
        }

        public List<String> getAvailableMinions() {
            return availableMinions;
        }

        public List<String> getDownMinions() {
            return downMinions;
        }

        public Optional<Object> getMetadata() {
            return metadata.flatMap(md -> {
                try {
                    return Optional.ofNullable(GSON.fromJson(md, Object.class));
                } catch (JsonSyntaxException ex) {
                    return Optional.empty();
                }
            });
        }

        public <R> Optional<R> getMetadata(Class<R> dataType) {
            return metadata.flatMap(md -> {
                try {
                    return Optional.ofNullable(GSON.fromJson(md, dataType));
                } catch (JsonSyntaxException ex) {
                    return Optional.empty();
                }
            });
        }

        public <R> Optional<R> getMetadata(TypeToken<R> dataType) {
            return metadata.flatMap(md -> {
                try {
                    return Optional.ofNullable(GSON.fromJson(md, dataType.getType()));
                } catch (JsonSyntaxException ex) {
                    return Optional.empty();
                }
            });
        }

    }

}
