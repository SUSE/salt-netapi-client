package com.suse.salt.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * TypeAdaptorFactory creating TypeAdapters for Optional
 */
public class OptionalTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <A> TypeAdapter<A> create(Gson gson, TypeToken<A> typeToken) {
        Type type = typeToken.getType();
        boolean isOptional = typeToken.getRawType() == Optional.class;
        boolean isParameterized = type instanceof ParameterizedType;
        if (isOptional && isParameterized) {
            Type elementType = ((ParameterizedType) type).getActualTypeArguments()[0];
            TypeAdapter<?> elementAdapter = gson.getAdapter(TypeToken.get(elementType));
            return (TypeAdapter<A>) optionalAdapter(elementAdapter);
        } else {
            return null;
        }
    }

    private <A> TypeAdapter<Optional<A>> optionalAdapter(TypeAdapter<A> innerAdapter) {
        return new TypeAdapter<Optional<A>>() {
            @Override
            public Optional<A> read(JsonReader in) throws IOException {
                if (in.peek() == JsonToken.NULL) {
                    in.nextNull();
                    return Optional.empty();
                } else {
                    A value = innerAdapter.read(in);
                    return Optional.of(value);
                }
            }

            @Override
            public void write(JsonWriter out, Optional<A> optional) throws IOException {
                innerAdapter.write(out, optional.orElse(null));
            }
        };
    }
}
