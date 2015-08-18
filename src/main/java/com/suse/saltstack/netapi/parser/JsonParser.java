package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suse.saltstack.netapi.datatypes.Arguments;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.Keys;
import com.suse.saltstack.netapi.datatypes.ScheduledJob;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.datatypes.cherrypy.Applications;
import com.suse.saltstack.netapi.datatypes.cherrypy.HttpServer;
import com.suse.saltstack.netapi.datatypes.cherrypy.Stats;
import com.suse.saltstack.netapi.exception.ParsingException;
import com.suse.saltstack.netapi.results.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public static final JsonParser<Result<List<Map<String, Object>>>> RETVALS =
            new JsonParser<>(new TypeToken<Result<List<Map<String, Object>>>>(){});
    public static final JsonParser<Result<List<Map<String, Map<String, Object>>>>> RETMAPS =
            new JsonParser<>(
            new TypeToken<Result<List<Map<String, Map<String, Object>>>>>(){});
    public static final JsonParser<Stats> STATS =
            new JsonParser<>(new TypeToken<Stats>(){});
    public static final JsonParser<Result<Keys>> KEYS =
            new JsonParser<>(new TypeToken<Result<Keys>>(){});
    public static final JsonParser<Map<String, Object>> MAP =
            new JsonParser<>(new TypeToken<Map<String, Object>>(){});

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
                .registerTypeAdapter(Stats.class, new StatsDeserializer())
                .registerTypeAdapter(Arguments.class, new ArgumentsDeserializer())
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
     * Deserializer for the Stats object received from the API.
     */
    private class StatsDeserializer implements JsonDeserializer<Stats> {

        private static final String CP_APPLICATIONS = "CherryPy Applications";
        private static final String CP_SERVER_PREFIX = "CherryPy HTTPServer ";

        @Override
        public Stats deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            try {
                JsonObject stats = jsonElement.getAsJsonObject();
                Applications app = gson.fromJson(stats.get(CP_APPLICATIONS),
                        Applications.class);
                HttpServer server = null;
                for (Map.Entry<String, JsonElement> entry : stats.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(CP_SERVER_PREFIX)) {
                        server = gson.fromJson(entry.getValue(), HttpServer.class);
                        break;
                    }
                }
                return new Stats(app, server);
            } catch (Exception e) {
                throw new JsonParseException(e);
            }
        }
    }

    /**
     * Deserializer for Arguments class.
     * Breaks the incoming arguments into args and kwargs parts
     * and fills a new Arguments instance.
     */
    private class ArgumentsDeserializer implements JsonDeserializer<Arguments> {

        private static final String KWARG_KEY = "__kwarg__";

        @Override
        public Arguments deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            Arguments result = new Arguments();

            if (json != null && json.isJsonArray()) {
                for (JsonElement jsonElement : json.getAsJsonArray()) {
                    fillArgs(result, jsonElement);
                }
            }

            return result;
        }

        /**
         * Fills args/kwargs to given Arguments instance based on JSON data
         * from jsonElement.
         *
         * @param result Arguments to be filled
         * @param jsonElement input JSON data
         */
        private void fillArgs(Arguments result, JsonElement jsonElement) {
            if (isKwarg(jsonElement)) {
                filterKwarg(jsonElement);
                fillKwargsFromObject(result, jsonElement.getAsJsonObject());
            } else {
                result.getArgs().add(gson.fromJson(jsonElement, Object.class));
            }
        }

        /**
         * Fills kwargs from given jsonObject key/values to given Arguments instance.
         *
         * @param result Arguments to be filled
         * @param jsonObject input json data
         */
        private void fillKwargsFromObject(Arguments result, JsonObject jsonObject) {
            for (Map.Entry<String, JsonElement> kwItem : jsonObject.entrySet()) {
                result.getKwargs().put(kwItem.getKey(),
                        gson.fromJson(kwItem.getValue(),
                        Object.class));
            }
        }

        /**
         * Checks whether element is kwarg or arg.
         * Element is a kwarg if it's an object and contains __kwarg__ property set to true.
         *
         * @param jsonElement element to be tested
         * @return true if element is kwarg
         */
        private boolean isKwarg(JsonElement jsonElement) {
            if (!jsonElement.isJsonObject()) {
                return false;
            }

            JsonElement kwarg = jsonElement.getAsJsonObject().get(KWARG_KEY);
            return kwarg != null
                    && kwarg.isJsonPrimitive()
                    && kwarg.getAsJsonPrimitive().isBoolean()
                    && kwarg.getAsBoolean();
        }

        /**
         * Filters __kwarg__ flag from given element.
         *
         * @param jsonElement
         */
        private void filterKwarg(JsonElement jsonElement) {
            if (jsonElement.isJsonObject()
                    && jsonElement.getAsJsonObject().has(KWARG_KEY)) {
                jsonElement.getAsJsonObject().remove(KWARG_KEY);
            }
        }
    }

    /**
     * Json adapter to handle the Job.StartTime date format given by netapi
     */
    public class JobStartTimeJsonAdapter extends TypeAdapter<Date> {
        @Override
        public synchronized void write(JsonWriter out, Date value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            String dateFormatAsString = Job.START_TIME_FORMAT.format(value);
            out.value(dateFormatAsString);
        }

        @Override
        public synchronized Date read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull();
                return null;
            }
            try {
                String dateStr = in.nextString();
                // Remove microseconds because java Date does not support it
                dateStr = dateStr.substring(0, dateStr.length() - 3);
                return Job.START_TIME_FORMAT.parse(dateStr);
            } catch (ParseException e) {
                throw new ParsingException(e);
            }
        }
    }
}
