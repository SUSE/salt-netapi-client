package com.suse.salt.netapi.parser;

import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.datatypes.Arguments;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Job;
import com.suse.salt.netapi.datatypes.ScheduledJob;
import com.suse.salt.netapi.datatypes.StartTime;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.ResultInfoSet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Parser for Salt API responses.
 *
 * @param <T> The result type this parser produces.
 */
public class JsonParser<T> {

    public static final JsonParser<Result<String>> STRING =
            new JsonParser<>(new TypeToken<Result<String>>(){});
    public static final JsonParser<Result<List<Token>>> TOKEN =
            new JsonParser<>(new TypeToken<Result<List<Token>>>(){});
    public static final JsonParser<Result<List<ScheduledJob>>> SCHEDULED_JOB =
            new JsonParser<>(new TypeToken<Result<List<ScheduledJob>>>(){});
    public static final JsonParser<Result<List<Map<String, Job>>>> JOBS =
            new JsonParser<>(new TypeToken<Result<List<Map<String, Job>>>>(){});
    public static final JsonParser<ResultInfoSet> JOB_RESULTS =
            new JsonParser<>(new TypeToken<ResultInfoSet>(){});
    public static final JsonParser<Result<List<Map<String, Map<String, Object>>>>> RETMAPS =
            new JsonParser<>(
            new TypeToken<Result<List<Map<String, Map<String, Object>>>>>(){});
    public static final JsonParser<Result<List<Map<String, Object>>>> RUN_RESULTS =
            new JsonParser<>(new TypeToken<Result<List<Map<String, Object>>>>(){});
    public static final JsonParser<Stats> STATS =
            new JsonParser<>(new TypeToken<Stats>(){});
    public static final JsonParser<Result<Key.Names>> KEYS =
            new JsonParser<>(new TypeToken<Result<Key.Names>>(){});
    public static final JsonParser<Map<String, Object>> MAP =
            new JsonParser<>(new TypeToken<Map<String, Object>>(){});
    public static final JsonParser<Event> EVENTS =
            new JsonParser<>(new TypeToken<Event>(){});

    private final TypeToken<T> type;
    private final Gson gson;
    public static final Gson GSON = new GsonBuilder()
    .registerTypeAdapter(String.class, Adapters.STRING)
    .registerTypeAdapter(Boolean.class, Adapters.BOOLEAN)
    .registerTypeAdapter(Date.class, new DateAdapter().nullSafe())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeISOAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeISOAdapter())
            .registerTypeAdapter(StartTime.class, new StartTimeAdapter().nullSafe())
            .registerTypeAdapter(Stats.class, new StatsAdapter())
            .registerTypeAdapter(Arguments.class, new ArgumentsAdapter())
            .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
            .registerTypeAdapterFactory(new XorTypeAdapterFactory())
            .create();

    /**
     * Created a new JsonParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     */
    public JsonParser(TypeToken<T> type) {
        this(type, GSON);
    }

    /**
     * Created a new JsonParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     * @param gson Gson instance to use for parsing.
     */
    public JsonParser(TypeToken<T> type, Gson gson) {
        this.type = type;
        this.gson = gson;
    }

    /**
     * Parses a Json response that has a direct representation as a Java class.
     * @param inputStream result stream to parse.
     * @return The parsed value.
     */
    public T parse(InputStream inputStream) {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        // Parse result type from the returned JSON
        return gson.fromJson(streamReader, type.getType());
    }

    /**
     * Parse JSON given as string.
     * @param jsonString JSON input given as string
     * @return The parsed object
     */
    public T parse(String jsonString) {
        return gson.fromJson(jsonString, type.getType());
    }


}
