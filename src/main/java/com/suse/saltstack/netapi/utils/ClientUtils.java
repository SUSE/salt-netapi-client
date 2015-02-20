package com.suse.saltstack.netapi.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.suse.saltstack.netapi.client.SaltStackClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Utilities for {@link SaltStackClient}.
 */
public class ClientUtils {

    /**
     * Quietly close a given stream, suppressing exceptions.
     *
     * @param stream Stream to close
     */
    public static void closeQuietly(InputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            stream.close();
        } catch (IOException e) {
        }
    }

    /**
    * Convert a given {@link String} to an {@link InputStream}.
    *
    * @param s a string
    * @return an input stream on the string
    */
    public static InputStream stringToStream(String s) {
        return new ByteArrayInputStream(s.getBytes());
    }

    /**
    * Convert a given {@link InputStream} to a {@link String}.
    *
    * @param inputStream an input stream
    * @return the string in the input stream
    */
    public static String streamToString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream);
        String ret = scanner.useDelimiter("\\A").next();
        scanner.close();
        return ret;
    }

    /**
     * Helper for constructing json object from kwargs and args.
     *
     * @return JsonObject filled with kwargs and args.
     */
    public static JsonObject makeJsonData(Map<String, String> kwargs, List<String> args) {
        final JsonObject json = new JsonObject();

        if (kwargs != null) {
            for (Map.Entry<String, String> kwEntry : kwargs.entrySet()) {
                json.addProperty(kwEntry.getKey(), kwEntry.getValue());
            }
        }

        if (args != null) {
            JsonArray argsArray = new JsonArray();
            for (String arg : args) {
                argsArray.add(new JsonPrimitive(arg));
            }
            json.add("arg", argsArray);
        }

        return json;
    }
}
