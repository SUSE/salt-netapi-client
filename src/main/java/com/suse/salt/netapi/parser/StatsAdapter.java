package com.suse.salt.netapi.parser;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suse.salt.netapi.datatypes.cherrypy.Applications;
import com.suse.salt.netapi.datatypes.cherrypy.HttpServer;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;

import java.io.IOException;

/**
 * Json TypeAdapter for the Stats object received from the API.
 */
public class StatsAdapter extends TypeAdapter<Stats> {

    private static final String CP_APPLICATIONS = "CherryPy Applications";
    private static final String CP_SERVER_PREFIX = "CherryPy HTTPServer ";

    @Override
    public void write(JsonWriter jsonWriter, Stats stats) throws IOException {
        throw new UnsupportedOperationException("Writing JSON not supported.");
    }

    @Override
    public Stats read(JsonReader jsonReader) throws IOException {
        Applications app = null;
        HttpServer server = null;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals(CP_APPLICATIONS)) {
                app = JsonParser.GSON.fromJson(jsonReader, Applications.class);
            } else if (name.startsWith(CP_SERVER_PREFIX)) {
                server = JsonParser.GSON.fromJson(jsonReader, HttpServer.class);
            } else {
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return new Stats(app, server);
    }
}
