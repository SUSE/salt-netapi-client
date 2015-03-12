package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.datatypes.cherrypy.Stats;
import com.suse.saltstack.netapi.datatypes.cherrypy.Applications;
import com.suse.saltstack.netapi.datatypes.cherrypy.HttpServer;
import com.suse.saltstack.netapi.results.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
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
            new JsonParser<>(new TypeToken<Result<String>>() { });

    public static final JsonParser<Result<List<Token>>> TOKEN =
            new JsonParser<>(new TypeToken<Result<List<Token>>>() { });

    public static final JsonParser<Result<List<Job>>> JOB =
            new JsonParser<>(new TypeToken<Result<List<Job>>>() { });

    public static final JsonParser<Result<List<Map<String, Object>>>> RETVALS =
            new JsonParser<>(new TypeToken<Result<List<Map<String, Object>>>>() { });

    public static final JsonParser<Stats> STATS =
            new JsonParser<>(new TypeToken<Stats>() { });

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
                .registerTypeAdapter(Date.class, new SaltStackDateDeserializer())
                .registerTypeAdapter(Stats.class, new StatsDeserializer())
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
     * Deserializer for date representation received from the API
     * (which represents it as a (floating) number of seconds since the Epoch).
     */
    private class SaltStackDateDeserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type type,
                JsonDeserializationContext jsonDeserializationContext)
                throws JsonParseException {
            try {
                double dateMiliSecs = jsonElement.getAsDouble() * 1000;
                return new Date((long) dateMiliSecs);
            } catch (NumberFormatException e) {
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
}
