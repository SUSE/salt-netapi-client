package com.suse.salt.netapi.calls.modules;

import com.suse.salt.netapi.calls.LocalCall;

import com.google.gson.reflect.TypeToken;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * salt.modules.schedule
 */
public class Schedule {

    private Schedule() {
    }

    /**
     * Common result structure for scheduling functions
     */
    public static class Result {

        private String comment;
        private boolean result;

        /**
         * Construct a new Result
         * @param commentIn Human readable comment
         * @param resultIn boolean indicating success
         */
        public Result(String commentIn, boolean resultIn) {
            this.comment = commentIn;
            this.result = resultIn;
        }

        /**
         * boolean indicating success
         * @return boolean indicating success
         */
        public boolean getResult() {
            return result;
        }

        /**
         * Human readable comment
         * @return Human readable comment
         */
        public String getComment() {
            return comment;
        }

    }

    /**
     * Delete a schedule entry
     * @param name job name
     * @return the result
     */
    public static LocalCall<Result> delete(String name) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("name", name);
        return new LocalCall<>("schedule.delete", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() { });
    }

    /**
     * Schedule a salt call for later execution on the minion
     * @param name job name
     * @param call salt call schedule
     * @param once when to execute it once
     * @param metadata additional metadata
     * @return call object to execute via the client
     */
    public static LocalCall<Result> add(String name, LocalCall<?> call,
            LocalDateTime once, Map<String, ?> metadata) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        Map<String, Object> payload = call.getPayload();
        args.put("function", payload.get("fun"));
        args.put("job_args", payload.get("arg"));
        args.put("job_kwargs", payload.get("kwarg"));

        args.put("name", name);
        args.put("once", once.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        args.put("metadata", metadata);
        return new LocalCall<>("schedule.add", Optional.empty(), Optional.of(args),
                new TypeToken<Result>() { });
    }

    /**
     * List scheduled jobs
     *
     * @param show_all if true display all the tasks including
     * those with "return_job":false
     * @return call object to execute via the client
     */
    public static LocalCall<Map<String, Map<String, Object>>> list(boolean show_all) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("show_all", show_all);
        args.put("return_yaml", false);
        return new LocalCall<>("schedule.list", Optional.empty(), Optional.of(args),
              new TypeToken<Map<String, Map<String, Object>>>(){});
    }
}
