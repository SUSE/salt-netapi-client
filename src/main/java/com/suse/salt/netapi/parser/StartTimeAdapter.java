package com.suse.salt.netapi.parser;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.suse.salt.netapi.datatypes.StartTime;

import java.io.IOException;

/**
 * Json adapter to handle the Job.StartTime date format given by netapi
 */
public class StartTimeAdapter extends TypeAdapter<StartTime> {

    @Override
    public void write(JsonWriter jsonWriter, StartTime date) throws IOException {
        if (date == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(date.toString());
        }
    }

    @Override
    public StartTime read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        }

        String dateStr = jsonReader.nextString();
        // Remove microseconds because java Date does not support it
        String subStr = dateStr.substring(0, dateStr.length() - 3);
        return new StartTime(subStr);
    }
}
