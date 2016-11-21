package com.suse.salt.netapi.event;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.parser.JsonParser;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of job return events fired each time a minion returns data for a job.
 */
public class RunnerReturnEvent {

    private static final Pattern PATTERN =
            Pattern.compile("^salt/run/(\\d+)/ret$");

    private final String jobId;
    private final Data data;

    private static final Gson GSON = JsonParser.GSON;

    /**
     * Data object of the job return event
     */
    public static class Data {
        @SerializedName("_stamp")
        private String timestamp;
        private String fun;
        private String jid;
        @SerializedName("return")
        private JsonElement result;
        private String user;
        private boolean success = false;

        public String getTimestamp() {
            return timestamp;
        }

        public String getFun() {
            return fun;
        }

        public String getJid() {
            return jid;
        }

        public String getUser() {
            return user;
        }

        public boolean isSuccess() {
            return success;
        }

        public Object getResult() {
            return GSON.fromJson(result, Object.class);
        }

        public <R> R getResult(Class<R> dataType) {
            return GSON.fromJson(result, dataType);
        }

        public <R> R getResult(TypeToken<R> dataType) {
            return GSON.fromJson(result, dataType.getType());
        }
    }

    /**
     * Creates a new JobReturnEvent
     *
     * @param jobIdIn    the id of the job
     * @param dataIn     data containing more information about this event
     */
    private RunnerReturnEvent(String jobIdIn, Data dataIn) {
        this.jobId = jobIdIn;
        this.data = dataIn;
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
     * @return the event data
     */
    public Data getData() {
        return data;
    }

    /**
     * Utility method to parse a generic event into a more specific one.
     *
     * @param event the generic event to parse
     * @return an option containing the parsed value or non if it could not be parsed
     */
    public static Optional<RunnerReturnEvent> parse(Event event) {
        Matcher matcher = PATTERN.matcher(event.getTag());
        if (matcher.matches()) {
            Data data = event.getData(Data.class);
            RunnerReturnEvent result = new RunnerReturnEvent(matcher.group(1), data);
            return Optional.of(result);
        } else {
            return Optional.empty();
        }
    }
}
