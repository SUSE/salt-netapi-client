package com.suse.saltstack.netapi.parser;

import com.google.gson.Gson;
import com.suse.saltstack.netapi.parser.ISaltStackResultParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

/**
 * Parses a simple result json that can be mapped to a java class directly.
 */
public class SaltStackSimpleParser implements ISaltStackResultParser {
    @Override
    public <T> T parse(Type resultType, InputStream inputStream) {
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        // Parse result type from the returned JSON
        Gson gson = new Gson();
        T result = gson.fromJson(streamReader, resultType);
        return result;
    }
}
