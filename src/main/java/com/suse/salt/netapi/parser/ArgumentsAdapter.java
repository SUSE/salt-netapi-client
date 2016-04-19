package com.suse.salt.netapi.parser;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suse.salt.netapi.datatypes.Arguments;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Json TypeAdapter for Arguments class.
 * Breaks the incoming arguments into args and kwargs parts
 * and fills a new Arguments instance.
 */
public class ArgumentsAdapter extends TypeAdapter<Arguments> {

    private static final String KWARG_KEY = "__kwarg__";

    @Override
    public void write(JsonWriter jsonWriter, Arguments args) throws IOException {
        throw new UnsupportedOperationException("Writing JSON not supported.");
    }

    @Override
    public Arguments read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            throw new JsonParseException("null is not a valid value for Arguments");
        }
        Arguments result = new Arguments();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            if (jsonReader.peek() == JsonToken.BEGIN_OBJECT) {
                Map<String, Object> arg = readObjectArgument(jsonReader);
                if (isKwarg(arg)) {
                    arg.remove(KWARG_KEY);
                    result.getKwargs().putAll(arg);
                } else {
                    result.getArgs().add(arg);
                }
            } else {
                result.getArgs().add(JsonParser.GSON.fromJson(jsonReader, Object.class));
            }
        }
        jsonReader.endArray();
        return result;
    }

    /**
     * Reads a generic object argument from the given JsonReader.
     *
     * @param jsonReader JsonReader expecting an object next
     * @return Map representing a generic object argument
     */
    private Map<String, Object> readObjectArgument(JsonReader jsonReader)
            throws IOException {
        Map<String, Object> arg = new LinkedHashMap<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            arg.put(jsonReader.nextName(),
                    JsonParser.GSON.fromJson(jsonReader, Object.class));
        }
        jsonReader.endObject();
        return arg;
    }

    /**
     * Checks whether an object argument is kwarg.
     * Object argument is kwarg if it contains __kwarg__ property set to true.
     *
     * @param arg object argument to be tested
     * @return true if object argument is kwarg
     */
    private boolean isKwarg(Map<String, Object> arg) {
        Object kwarg = arg.get(KWARG_KEY);
        return kwarg != null
                && kwarg instanceof Boolean
                && ((Boolean) kwarg);
    }
}
