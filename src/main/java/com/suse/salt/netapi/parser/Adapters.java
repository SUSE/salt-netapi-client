package com.suse.salt.netapi.parser;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class Adapters {
    public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter<Boolean>() {
        @Override
        public Boolean read(JsonReader in) throws IOException {
            return in.nextBoolean();
        }
        @Override
        public void write(JsonWriter out, Boolean value) throws IOException {
            if (value == null) {
                throw new JsonParseException("null is not a valid value for boolean");
            } else {
                out.value(value);
            }
        }
    };

    public static final TypeAdapter<String> STRING = new TypeAdapter<String>() {
        @Override
        public String read(JsonReader in) throws IOException {
            return in.nextString();
        }
        @Override
        public void write(JsonWriter out, String value) throws IOException {
            if (value == null) {
                throw new JsonParseException("null is not a valid value for string");
            } else {
                out.value(value);
            }
        }
    };
}
