package com.suse.salt.netapi.parser;

import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class Adapters {

    public static final TypeAdapter<Double> DOUBLE = new TypeAdapter<Double>() {
        @Override
        public Double read(JsonReader in) throws IOException {
            return in.nextDouble();
        }
        @Override
        public void write(JsonWriter out, Double value) throws IOException {
            if (value == null) {
                throw new JsonParseException("null is not a valid value for double");
            } else {
                out.value(value);
            }
        }
    };

    public static final TypeAdapter<Long> LONG = new TypeAdapter<Long>() {
        @Override
        public Long read(JsonReader in) throws IOException {
            return in.nextLong();
        }
        @Override
        public void write(JsonWriter out, Long value) throws IOException {
            if (value == null) {
                throw new JsonParseException("null is not a valid value for long");
            } else {
                out.value(value);
            }
        }
    };

    public static final TypeAdapter<Integer> INTEGER = new TypeAdapter<Integer>() {
        @Override
        public Integer read(JsonReader in) throws IOException {
            return in.nextInt();
        }
        @Override
        public void write(JsonWriter out, Integer value) throws IOException {
            if (value == null) {
                throw new JsonParseException("null is not a valid value for int");
            } else {
                out.value(value);
            }
        }
    };

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
