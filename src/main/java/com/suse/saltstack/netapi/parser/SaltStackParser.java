package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;
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
public class SaltStackParser<T> {

    public static final SaltStackParser<SaltStackResult<String>> STRING =
        new SaltStackParser<>(new TypeToken<SaltStackResult<String>>(){});
    public static final SaltStackParser<SaltStackResult<List<SaltStackToken>>> TOKEN =
        new SaltStackParser<>(new TypeToken<SaltStackResult<List<SaltStackToken>>>(){});
    public static final SaltStackParser<SaltStackResult<List<SaltStackJob>>> JOB =
        new SaltStackParser<>(new TypeToken<SaltStackResult<List<SaltStackJob>>>(){});
    public static final SaltStackParser<SaltStackResult<List<Map<String,Object>>>> RETVALS =
        new SaltStackParser<>(new TypeToken<SaltStackResult<List<Map<String,Object>>>>(){});

    private final TypeToken<T> type;

    /**
     * Created a new SaltStackParser for the given type.
     *
     * @param type A TypeToken describing the type this parser produces.
     */
    public SaltStackParser(TypeToken<T> type){
        this.type = type;
    }

    /**
     * Parses a Json response that has a direct representation as a Java class.
     * @param inputStream result stream to parse.
     * @return The parsed value.
     */
    public T parse(InputStream inputStream) throws SaltStackParsingException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        // Parse result type from the returned JSON
        return new Gson().fromJson(streamReader, type.getType());
    }
}
