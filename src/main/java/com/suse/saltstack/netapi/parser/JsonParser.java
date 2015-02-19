package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.results.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;


/**
 * Parser for Saltstack responses.
 *
 * @param <T> The result type this parser produces.
 */
public class JsonParser<T> {

    public static final JsonParser<SaltStackResult<String>> STRING =
        new JsonParser<>(new TypeToken<SaltStackResult<String>>(){});
    public static final JsonParser<SaltStackResult<List<SaltStackToken>>> TOKEN =
        new JsonParser<>(new TypeToken<SaltStackResult<List<SaltStackToken>>>(){});
    public static final JsonParser<SaltStackResult<List<SaltStackJob>>> JOB =
        new JsonParser<>(new TypeToken<SaltStackResult<List<SaltStackJob>>>(){});
    public static final JsonParser<SaltStackResult<List<Map<String,Object>>>> RETVALS =
        new JsonParser<>(new TypeToken<SaltStackResult<List<Map<String,Object>>>>(){});

    private final TypeToken<T> type;

    /**
     * Created a new JsonParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     */
    public JsonParser(TypeToken<T> type){
        this.type = type;
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
        return new Gson().fromJson(streamReader, type.getType());
    }
}
