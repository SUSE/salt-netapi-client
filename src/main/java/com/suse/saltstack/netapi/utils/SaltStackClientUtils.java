package com.suse.saltstack.netapi.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.suse.saltstack.netapi.client.SaltStackClient;

/**
 * Utilities for {@link SaltStackClient}.
 */
public class SaltStackClientUtils {

	/**
	 * Quietly close a given stream, suppressing exceptions.
	 *
	 * @param stream
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
	 * @param s
	 *            a string
	 * @return an input stream on the string
	 */
	public static InputStream stringToStream(String s) {
		return new ByteArrayInputStream(s.getBytes());
	}

	/**
	 * Convert a given {@link InputStream} to a {@link String}.
	 *
	 * @param inputStream
	 *            an input stream
	 * @return the string in the input stream
	 */
	public static String streamToString(InputStream inputStream) {
		Scanner scanner = new Scanner(inputStream);
		String ret = scanner.useDelimiter("\\A").next();
		scanner.close();
		return ret;
	}

	/**
	 * Creates a new JsonArray instance and adds params by given list
	 * 
	 * @param args {@link List} of {@link String}
	 * @return {@link JsonArray}
	 */
	public static JsonArray parseJsonArray(List<String> args) {
		JsonArray argsArray = new JsonArray();
		for (String arg : args) {
			argsArray.add(new JsonPrimitive(arg));
		}
		return argsArray;
	}
}
