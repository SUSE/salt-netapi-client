package com.suse.salt.netapi.results;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import com.suse.salt.netapi.datatypes.StartTime;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the Salt API's result information structure.
 */
public class ResultInfo {

    @SerializedName("Function")
    private String function;

    @SerializedName("StartTime")
    private StartTime startTime;

    @SerializedName("Arguments")
    private List<Object> arguments;

    @SerializedName("Minions")
    private final HashSet<String> minions = new HashSet<>();

    @SerializedName("User")
    private String user;

    @SerializedName("Target")
    private String target;

    @SerializedName("Result")
    private HashMap<String, Return<Object>> rawResults;
    private transient final HashMap<String, Object> resultsCache = new HashMap<>();

    /**
     * Returns a list of arguments supplied function.
     *
     * @return list of Objects
     */
    public List<Object> getArguments() {
        return arguments;
    }

    /**
     * Returns function name.
     *
     * @return function name
     */
    public String getFunction() {
        return function;
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
     * Returns target of job submission.
     *
     * @return job submission target
     */
    public String getTarget() {
        return target;
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
     * Returns job's return value that is associated with supplied minion
     * as an {@link Optional}. If a minion has not returned a response, an empty
     * value is returned.
     *
     * @param minion - name of a minion
     * @return {@link Optional} associated with the result from a given minion.
     */
    public Optional<Object> getResult(String minion) {
        Return<Object> result;
        if (rawResults == null || (result = rawResults.get(minion)) == null) {
            return Optional.<Object>empty();
        }

        return Optional.<Object>ofNullable(result.getResult());
    }

    /**
     * Returns result map of available {@link Object} associated with each minion.
     * Minions that have yet to return a value are not included in this mapping.
     *
     * @return Map&lt;String, Object&gt; of available job return values.
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> getResults() {
        if (rawResults == null || resultsCache.size() == rawResults.size()) {
            return resultsCache;
        }

        resultsCache.clear();
        resultsCache.putAll(rawResults);
        resultsCache.replaceAll(
                (String key, Object result) -> ((Return<Object>) result).getResult());
        return resultsCache;
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
