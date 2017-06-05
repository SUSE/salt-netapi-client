package com.suse.salt.netapi.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Scanner;

import static com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner;

import com.suse.salt.netapi.client.SaltClient;

/**
 * Utilities for {@link SaltClient}.
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
     * Helper for constructing parameterized types.
     *
     * @param ownerType the owner type
     * @param rawType the raw type
     * @param typeArguments the type arguments
     * @return the parameterized type object
     * @see com.google.gson.internal.$Gson$Types#newParameterizedTypeWithOwner
     */
    public static ParameterizedType parameterizedType(Type ownerType, Type rawType,
            Type... typeArguments) {
        return newParameterizedTypeWithOwner(ownerType, rawType, typeArguments);
    }

    /**
     * Get the module name from a given function. In case of e.g. "test.ping", this method
     * will return "test".
     *
     * @param function string containing module and function name (e.g. "test.ping")
     * @return the module name
     * @throws IllegalArgumentException if a given function string does not contain a '.'
     */
    public static String getModuleNameFromFunction(final String function)
            throws IllegalArgumentException {
        if (!function.contains(".")) {
            throw new IllegalArgumentException(function);
        }
        return function.split("\\.")[0];
    }

    /**
     * Extract the function name from a given function(containing module and function name).
     * In case of e.g. "test.ping", this method will return "ping".
     *
     * @param function string containing module and function name (e.g. "test.ping")
     * @return the function name
     * @throws IllegalArgumentException if a given function string does not contain a '.'
     */
    public static String getFunctionNameFromFunction(final String function)
            throws IllegalArgumentException {
        if (!function.contains(".")) {
            throw new IllegalArgumentException(function);
        }
        return function.split("\\.")[1];
    }
}
