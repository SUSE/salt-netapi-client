package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * ABS for result parsers. All result parsers need to inherit from this class. Generic methods
 * that apply to multiple results can be added to this class.
 */
public abstract class SaltStackParser<T> {
    public abstract T parse(InputStream inputStream) throws SaltStackParsingException;

    /**
     * Parses a Json response that has a direct representation as Java classes.
     * @param resultType class to parse into.
     * @param inputStream result stream to parse.
     * @return
     */
    protected   T parseSimple(Type resultType, InputStream inputStream) {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        // Parse result type from the returned JSON
        return new Gson().fromJson(streamReader, resultType);
    }
}
