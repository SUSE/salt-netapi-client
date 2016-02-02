package com.suse.salt.netapi.calls.runner;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

import com.suse.salt.netapi.calls.Data;
import com.suse.salt.netapi.calls.LocalAsyncResult;
import com.suse.salt.netapi.calls.RunnerAsyncResult;
import com.suse.salt.netapi.calls.RunnerCall;
import com.suse.salt.netapi.calls.WheelAsyncResult;
import com.suse.salt.netapi.datatypes.StartTime;
import com.suse.salt.netapi.results.Result;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

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
        private Map<String, Result<R>> result;

        public String getFunction() {
            return function;
        }

        public String getJid() {
            return jid;
        }

        public Date getStartTime(TimeZone tz) {
            return startTime == null ? null : startTime.getDate(tz);
        }

        public Date getStartTime() {
            return startTime == null ? null : startTime.getDate();
        }

        public List<Object> getArguments() {
            return arguments;
        }

        public Set<String> getMinions() {
            return minions;
        }

        public String getUser() {
            return user;
        }

        public String getTarget() {
            return target;
        }

        public Map<String, Result<R>> getResult() {
            return result;
        }

        public Optional<R> resultOf(String minionKey) {
            return Optional.ofNullable(result).flatMap(
                r -> Optional.ofNullable(r.get(minionKey))
            ).map(Result::getResult);
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
