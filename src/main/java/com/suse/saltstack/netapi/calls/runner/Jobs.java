package com.suse.saltstack.netapi.calls.runner;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.Data;
import com.suse.saltstack.netapi.calls.LocalAsyncResult;
import com.suse.saltstack.netapi.calls.RunnerAsyncResult;
import com.suse.saltstack.netapi.calls.RunnerCall;
import com.suse.saltstack.netapi.calls.WheelAsyncResult;
import com.suse.saltstack.netapi.datatypes.StartTime;
import com.suse.saltstack.netapi.results.Result;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import static com.suse.saltstack.netapi.utils.ClientUtils.parameterizedType;

/**
 * salt.runners.jobs
 */
public class Jobs {

    /**
     * Information about a salt job as returned by 'jobs.list_job' and 'jobs.print_job'.
     *
     * @param <R> the result type of the called function
     */
    public static class Info<R> {
        @SerializedName("Function")
        private String function;

        private String jid;

        @SerializedName("StartTime")
        private StartTime startTime;

        @SerializedName("Arguments")
        private List<Object> arguments;

        @SerializedName("Minions")
        private Set<String> minions;

        @SerializedName("User")
        private String user;

        @SerializedName("Target")
        private String target;

        @SerializedName("Result")
        private final Map<String, Result<R>> rawResults = new HashMap<>();
        private transient final Map<String, R> resultsCache = new HashMap<>();

        /**
         * Returns function name.
         *
         * @return function name
         */
        public String getFunction() {
            return function;
        }

        public String getJid() {
            return jid;
        }

        /**
         * Returns start time of the job.
         *
         * @param tz - TimeZone associated with the master of this job.
         * @return job start date
         */
        public Date getStartTime(TimeZone tz) {
            return startTime == null ? null : startTime.getDate(tz);
        }

        /**
         *  Returns start time assuming default {@link TimeZone} is Salt master's timezone.
         *
         * @return Date representation of the start time.
         */
        public Date getStartTime() {
            return startTime == null ? null : startTime.getDate();
        }

        /**
         * Returns a list of arguments supplied function.
         *
         * @return list of Objects
         */
        public List<Object> getArguments() {
            return arguments;
        }

        /**
         * Returns set of minions this job was submitted to.
         *
         * @return minion set
         */
        public Set<String> getMinions() {
            return minions;
        }

        /**
         * Returns user associated with this job.
         *
         * @return job's user
         */
        public String getUser() {
            return user;
        }

        /**
         * Returns target of job submission.
         *
         * @return job submission target
         */
        public String getTarget() {
            return target;
        }

        /**
         * Returns result map of available {@link Object} associated with each minion.
         * Minions that have yet to return a value are not included in this mapping.
         *
         * @return Map&lt;String, Object&gt; of available job return values.
         */
        @SuppressWarnings("unchecked")
        public Map<String, R> getResults() {
            if (resultsCache.size() == rawResults.size()) {
                return resultsCache;
            }

            resultsCache.clear();
            resultsCache.putAll((Map<String, R>) rawResults);
            resultsCache.replaceAll(
                    (String key, R result) -> ((Result<R>) result).getResult());
            return resultsCache;
        }

        /**
         * Returns job's return value that is associated with supplied minion
         * as an {@link Optional}. If a minion has not returned a response, an empty
         * value is returned.
         *
         * @param minion - name of a minion
         * @return {@link Optional} associated with the result from a given minion.
         */
        public Optional<R> getResult(String minion) {
            return Optional
                    .ofNullable(rawResults.get(minion))
                    .map(Result::getResult);
        }

        /**
         * Returns a set of minions that have yet to return a result.
         *
         * @return set of minions that have not returned a result.
         */
        public Set<String> getPendingMinions() {
            HashSet<String> pend = new HashSet<>(minions);
            pend.removeAll(rawResults.keySet());
            return pend;
        }
    }

    private Jobs() { }

    public static RunnerCall<Map<String, Object>> lookupJid(String jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid);
        return new RunnerCall<>("jobs.lookup_jid", Optional.of(args),
                new TypeToken<Map<String, Object>>(){});
    }

    @SuppressWarnings("unchecked")
    public static <R> RunnerCall<Map<String, R>> lookupJid(LocalAsyncResult<R> jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid.getJid());
        Type type = parameterizedType(null, Map.class, String.class,
                jid.getType().getType());
        return new RunnerCall<>("jobs.lookup_jid", Optional.of(args),
                (TypeToken<Map<String, R>>) TypeToken.get(type));
    }

    @SuppressWarnings("unchecked")
    public static <R> RunnerCall<Map<String, Data<R>>> lookupJid(WheelAsyncResult<R> jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid.getJid());
        Type dataType = parameterizedType(null, Data.class, jid.getType().getType());
        Type type = parameterizedType(null, Map.class, String.class, dataType);
        return new RunnerCall<>("jobs.lookup_jid", Optional.of(args),
                (TypeToken<Map<String, Data<R>>>) TypeToken.get(type));
    }

    @SuppressWarnings("unchecked")
    public static <R> RunnerCall<Map<String, Data<R>>> lookupJid(RunnerAsyncResult<R> jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid.getJid());
        Type dataType = parameterizedType(null, Data.class, jid.getType().getType());
        Type type = parameterizedType(null, Map.class, String.class, dataType);
        return new RunnerCall<>("jobs.lookup_jid", Optional.of(args),
                (TypeToken<Map<String, Data<R>>>) TypeToken.get(type));
    }
}
