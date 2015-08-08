package com.suse.saltstack.netapi.utils;

import com.suse.saltstack.netapi.client.SaltStackClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Scanner;

import static com.google.gson.internal.$Gson$Types.newParameterizedTypeWithOwner;

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
     *  @see com.google.gson.internal.$Gson$Types#newParameterizedTypeWithOwner
     */
    public static ParameterizedType parameterizedType(Type ownerType, Type rawType,
            Type... typeArguments) {
        return newParameterizedTypeWithOwner(ownerType, rawType, typeArguments);
    }

}
