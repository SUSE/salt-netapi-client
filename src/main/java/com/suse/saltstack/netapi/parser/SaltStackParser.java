package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;
import com.suse.saltstack.netapi.results.SaltStackJobResult;
import com.suse.saltstack.netapi.results.SaltStackStringResult;
import com.suse.saltstack.netapi.results.SaltStackTokenResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;


public class SaltStackParser<T> {

    public static final SaltStackParser<SaltStackStringResult> STRING = new SaltStackParser<>(SaltStackStringResult.class);
    public static final SaltStackParser<SaltStackTokenResult> TOKEN = new SaltStackParser<>(SaltStackTokenResult.class);
    public static final SaltStackParser<SaltStackJobResult> JOB = new SaltStackParser<>(SaltStackJobResult.class);

    private final Class<T> type;

    public SaltStackParser(Class<T> type){
        this.type = type;
    }

    /**
     * Parses a Json response that has a direct representation as Java classes.
     * @param inputStream result stream to parse.
     * @return
     */
    public T parse(InputStream inputStream) throws SaltStackParsingException {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        // Parse result type from the returned JSON
        return new Gson().fromJson(streamReader, type);
    }
}
