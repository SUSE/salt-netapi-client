package com.suse.saltstack.netapi.parser;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.suse.saltstack.netapi.client.SaltStackKeyResult;
import com.suse.saltstack.netapi.exception.SaltStackParsingException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * Implements a parser for public key fingerprints (from endpoint /keys)
 */
public class SaltStackKeyParser implements ISaltStackResultParser {

    private SaltStackKeyResult keyResult = new SaltStackKeyResult();


    @Override
    public <T> T parse(Type resultType, InputStream inputStream) throws SaltStackParsingException {
        if ( !keyResult.getClass().getTypeName()
                .equals(resultType.getTypeName())) {
            throw new SaltStackParsingException("Only SaltStackKeyResult can be parsed with this class");
        }
        Reader inputStreamReader = new InputStreamReader(inputStream);
        Reader streamReader = new BufferedReader(inputStreamReader);

        JsonParser parser = new JsonParser();
        try {
            JsonElement resultElement = parser.parse(streamReader);
            JsonObject resultObject = resultElement.getAsJsonObject();
            JsonObject returnObject = resultObject.getAsJsonObject("return");
            for (Map.Entry<String, JsonElement> elementEntry : returnObject.entrySet()) {
                if ("local".equals(elementEntry.getKey())) {
                    // parse master keys
                    parseKeys(elementEntry);
                } else if ("minions".equals(elementEntry.getKey())) {
                    //parse minionkeys
                    parseKeys(elementEntry);
                }
            }
        } catch (NullPointerException | JsonSyntaxException e) {
            throw new SaltStackParsingException("SaltStackKeyParser: Illegal return values");
        }

        return (T)keyResult;
    }

    private void parseKeys(Map.Entry<String, JsonElement> elementEntry) {
        JsonObject minionKeys = elementEntry.getValue().getAsJsonObject();
        for (Map.Entry<String, JsonElement> keyEntry : minionKeys.entrySet()) {
            String minionName = keyEntry.getKey();
            String minionKey = keyEntry.getValue().getAsString();
            keyResult.addKey(minionKey, minionName);
        }
    }
}
