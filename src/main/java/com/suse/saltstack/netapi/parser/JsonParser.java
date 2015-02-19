package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
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
        new JsonParser<>(new TypeToken<Result<String>>(){});
    public static final JsonParser<Result<List<Token>>> TOKEN =
        new JsonParser<>(new TypeToken<Result<List<Token>>>(){});
    public static final JsonParser<Result<List<Job>>> JOB =
        new JsonParser<>(new TypeToken<Result<List<Job>>>(){});
    public static final JsonParser<Result<List<Map<String,Object>>>> RETVALS =
        new JsonParser<>(new TypeToken<Result<List<Map<String,Object>>>>(){});

    private final TypeToken<T> type;
    private Gson gson;

    /**
     * Created a new JsonParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     */
    public JsonParser(TypeToken<T> type){
        this.type = type;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new SaltStackDateDeserializer()).create();
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
}
