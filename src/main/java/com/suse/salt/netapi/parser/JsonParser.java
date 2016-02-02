package com.suse.salt.netapi.parser;

import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.datatypes.Arguments;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Job;
import com.suse.salt.netapi.datatypes.ScheduledJob;
import com.suse.salt.netapi.datatypes.StartTime;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Applications;
import com.suse.salt.netapi.datatypes.cherrypy.HttpServer;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.ResultInfoSet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Parser for Saltstack responses.
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

    /**
     * Created a new JsonParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     */
    public JsonParser(TypeToken<T> type) {
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateAdapter().nullSafe())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeISOAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeISOAdapter())
                .registerTypeAdapter(StartTime.class, new StartTimeAdapter().nullSafe())
                .registerTypeAdapter(Stats.class, new StatsAdapter())
                .registerTypeAdapter(Arguments.class, new ArgumentsAdapter())
                .registerTypeAdapterFactory(new OptionalTypeAdapterFactory())
                .create();
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

    /**
     * TypeAdapter for date representation received from the API
     * (which represents it as a (floating) number of seconds since the Epoch).
     */
    private class DateAdapter extends TypeAdapter<Date> {

        @Override
        public void write(JsonWriter jsonWriter, Date date) throws IOException {
            throw new UnsupportedOperationException("Writing JSON not supported.");
        }

        @Override
        public Date read(JsonReader jsonReader) throws IOException {
            try {
                double dateMilliseconds = jsonReader.nextDouble() * 1000;
                return new Date((long) dateMilliseconds);
            } catch (NumberFormatException | IllegalStateException e) {
                throw new JsonParseException(e);
            }
        }
    }

    /**
     * TypeAdaptorFactory creating TypeAdapters for Optional
     */
    private class OptionalTypeAdapterFactory implements TypeAdapterFactory {

        @Override
        @SuppressWarnings("unchecked")
        public <A> TypeAdapter<A> create(Gson gson, TypeToken<A> typeToken) {
            Type type = typeToken.getType();
            boolean isOptional = typeToken.getRawType() == Optional.class;
            boolean isParameterized = type instanceof ParameterizedType;
            if (isOptional && isParameterized) {
                Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
                TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
                return (TypeAdapter<A>) optionalAdapter(elementAdapter);
            } else {
                return null;
            }
        }

        private <A> TypeAdapter<Optional<A>> optionalAdapter(TypeAdapter<A> innerAdapter) {
            return new TypeAdapter<Optional<A>>() {
                @Override
                public Optional<A> read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return Optional.empty();
                    } else {
                        A value = innerAdapter.read(in);
                        return Optional.of(value);
                    }
                }

                @Override
                public void write(JsonWriter out, Optional<A> optional) throws IOException {
                    innerAdapter.write(out, optional.orElse(null));
                }
            };
        }
    }

    /**
     * Json TypeAdapter for the Stats object received from the API.
     */
    private class StatsAdapter extends TypeAdapter<Stats> {

        private static final String CP_APPLICATIONS = "CherryPy Applications";
        private static final String CP_SERVER_PREFIX = "CherryPy HTTPServer ";

        @Override
        public void write(JsonWriter jsonWriter, Stats stats) throws IOException {
            throw new UnsupportedOperationException("Writing JSON not supported.");
        }

        @Override
        public Stats read(JsonReader jsonReader) throws IOException {
            Applications app = null;
            HttpServer server = null;
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String name = jsonReader.nextName();
                if (name.equals(CP_APPLICATIONS)) {
                    app = gson.fromJson(jsonReader, Applications.class);
                } else if (name.startsWith(CP_SERVER_PREFIX)) {
                    server = gson.fromJson(jsonReader, HttpServer.class);
                } else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            return new Stats(app, server);
        }
    }

    /**
     * Json TypeAdapter for Arguments class.
     * Breaks the incoming arguments into args and kwargs parts
     * and fills a new Arguments instance.
     */
    private class ArgumentsAdapter extends TypeAdapter<Arguments> {

        private static final String KWARG_KEY = "__kwarg__";

        @Override
        public void write(JsonWriter jsonWriter, Arguments args) throws IOException {
            throw new UnsupportedOperationException("Writing JSON not supported.");
        }

        @Override
        public Arguments read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                throw new JsonParseException("null is not a valid value for Arguments");
            }
            Arguments result = new Arguments();
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                    Map<String, Object> arg = readObjectArgument(jsonReader);
                    if (isKwarg(arg)) {
                        arg.remove(KWARG_KEY);
                        result.getKwargs().putAll(arg);
                    } else {
                        result.getArgs().add(arg);
                    }
                } else {
                    result.getArgs().add(gson.fromJson(jsonReader, Object.class));
                }
            }
            jsonReader.endArray();
            return result;
        }

        /**
         * Reads a generic object argument from the given JsonReader.
         *
         * @param jsonReader JsonReader expecting an object next
         * @return Map representing a generic object argument
         */
        private Map<String, Object> readObjectArgument(JsonReader jsonReader)
                throws IOException {
            Map<String, Object> arg = new LinkedHashMap<>();
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                arg.put(jsonReader.nextName(), gson.fromJson(jsonReader, Object.class));
            }
            jsonReader.endObject();
            return arg;
        }

        /**
         * Checks whether an object argument is kwarg.
         * Object argument is kwarg if it contains __kwarg__ property set to true.
         *
         * @param arg object argument to be tested
         * @return true if object argument is kwarg
         */
        private boolean isKwarg(Map<String, Object> arg) {
            Object kwarg = arg.get(KWARG_KEY);
            return kwarg != null
                    && kwarg instanceof Boolean
                    && ((Boolean) kwarg);
        }
    }

    /**
     * Json adapter to handle the Job.StartTime date format given by netapi
     */
    private class StartTimeAdapter extends TypeAdapter<StartTime> {

        @Override
        public void write(JsonWriter jsonWriter, StartTime date) throws IOException {
            if (date == null) {
                jsonWriter.nullValue();
            } else {
                jsonWriter.value(date.toString());
            }
        }

        @Override
        public StartTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            }

            String dateStr = jsonReader.nextString();
            // Remove microseconds because java Date does not support it
            String subStr = dateStr.substring(0, dateStr.length() - 3);
            return new StartTime(subStr);
        }
    }


    /**
     * Adapter to convert an ISO formatted string to LocalDateTime
     */
    private class LocalDateTimeISOAdapter extends TypeAdapter<LocalDateTime> {

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime date) throws IOException {
            if (date == null) {
                throw new JsonParseException("null is not a valid value for LocalDateTime");
            } else {
                jsonWriter.value(date.toString());
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                throw new JsonParseException("null is not a valid value for LocalDateTime");
            }
            String dateStr = jsonReader.nextString();
            return LocalDateTime.parse(dateStr);
        }
    }

    /**
     * Adapter to convert an ISO formatted string to ZonedDateTime
     */
    private class ZonedDateTimeISOAdapter extends TypeAdapter<ZonedDateTime> {

        @Override
        public void write(JsonWriter jsonWriter, ZonedDateTime date) throws IOException {
            if (date == null) {
                throw new JsonParseException("null is not a valid value for ZonedDateTime");
            } else {
                jsonWriter.value(date.toString());
            }
        }

        @Override
        public ZonedDateTime read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
                throw new JsonParseException("null is not a valid value for ZonedDateTime");
            }
            String dateStr = jsonReader.nextString();
            return ZonedDateTime.parse(dateStr);
        }
    }
}
