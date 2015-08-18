package com.suse.saltstack.netapi.calls.runner;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.calls.Data;
import com.suse.saltstack.netapi.calls.LocalAsyncResult;
import com.suse.saltstack.netapi.calls.RunnerAsyncResult;
import com.suse.saltstack.netapi.calls.RunnerCall;
import com.suse.saltstack.netapi.calls.ScheduledJob;
import com.suse.saltstack.netapi.calls.WheelAsyncResult;
import com.suse.saltstack.netapi.parser.JsonParser;
import com.suse.saltstack.netapi.results.Result;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


import static com.suse.saltstack.netapi.utils.ClientUtils.parameterizedType;

/**
 * salt.runners.jobs
 */
public class Jobs {

    /**
     *
     * @param <R>
     */
    public static class Info<R> {
        @SerializedName("Function")
        private String function;

        private String jid;

        @SerializedName("StartTime")
        @JsonAdapter(JsonParser.JobStartTimeJsonAdapter.class)
        private Date startTime;

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

        public Date getStartTime() {
            return startTime;
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

    public static RunnerCall<Info<Object>> listJob(String jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid);
        return new RunnerCall<>("jobs.list_job", Optional.of(args),
                new TypeToken<Info<Object>>(){});
    }

    @SuppressWarnings("unchecked")
    public static <R> RunnerCall<Info<R>> listJob(ScheduledJob<R> jid) {
        Type type = parameterizedType(null, Info.class, jid.getType().getType());
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid.getJid());
        return new RunnerCall<>("jobs.list_job", Optional.of(args),
                (TypeToken<Info<R>>) TypeToken.get(type));
    }

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

    public static RunnerCall<Map<String, Info<?>>> printJob(String jid) {
        LinkedHashMap<String, Object> args = new LinkedHashMap<>();
        args.put("jid", jid);
        return new RunnerCall<>("jobs.print_job", Optional.of(args),
                new TypeToken<Map<String, Info<?>>>(){});
    }
}
