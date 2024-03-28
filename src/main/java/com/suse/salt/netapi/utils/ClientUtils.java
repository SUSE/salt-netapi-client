package com.suse.salt.netapi.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Scanner;

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
     */
    public static ParameterizedType parameterizedType(Type ownerType, Type rawType, Type... typeArguments) {
        return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
    }

    /**
     * Extract the module and function name from the function string based on '.' .
     * In case of e.g. "test.ping", this method will return String array {'test','ping'}
     *
     * @param function string containing module and function name (e.g. "test.ping")
     * @return String array containing module name as 1st element and function name as 2nd
     * @throws IllegalArgumentException if a given function string does not contain a '.'
     */
    public static String[] splitFunction(final String function) {
        if (!function.contains(".")) {
            throw new IllegalArgumentException(function);
        }
        return function.split("\\.");
    }
}
