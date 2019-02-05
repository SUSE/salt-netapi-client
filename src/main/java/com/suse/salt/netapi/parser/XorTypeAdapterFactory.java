package com.suse.salt.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import com.suse.salt.netapi.errors.JsonParsingError;
import com.suse.salt.netapi.errors.SaltError;
import com.suse.salt.netapi.utils.SaltErrorUtils;
import com.suse.salt.netapi.utils.Xor;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

/**
 * TypeAdaptorFactory creating TypeAdapters for Xor
 */
public class XorTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <A> TypeAdapter<A> create(Gson gson, TypeToken<A> typeToken) {
        Type type = typeToken.getType();
        boolean isXor = typeToken.getRawType() == Xor.class;
        boolean isParameterized = type instanceof ParameterizedType;
        boolean isSSHResult = ResultSSHResultTypeAdapterFactory.isResultSSHResult(type);
        if (isXor && isParameterized && !isSSHResult) {
            Type leftType = ((ParameterizedType) type).getActualTypeArguments()[0];
            Type rightType = ((ParameterizedType) type).getActualTypeArguments()[1];
            TypeAdapter<?> rightAdapter = gson.getAdapter(TypeToken.get(rightType));
            if (leftType.equals(SaltError.class)) {
                return (TypeAdapter<A>) errorAdapter(rightAdapter);
            }

            TypeAdapter<?> leftAdapter = gson.getAdapter(TypeToken.get(leftType));
            return (TypeAdapter<A>) xorAdapter(leftAdapter, rightAdapter);
        } else {
            return null;
        }
    }

    /**
     * Creates a generic Xor adapter by combining two other adapters - one for each side of
     * the Xor type. It will first try to parse incoming JSON data as the right type and, if
     * that does not succeed, it will try again with the left type.
     *
     * All exceptions besides the possible parsing Exception of the left type are not
     * caught.
     *
     * @param <L> the generic type for the left side of the Xor
     * @param <R> the generic type for the right side of the Xor
     * @param leftAdapter the left adapter
     * @param rightAdapter the right adapter
     * @return the Xor adapter
     */
    private <L, R> TypeAdapter<Xor<L, R>> xorAdapter(TypeAdapter<L> leftAdapter,
            TypeAdapter<R> rightAdapter) {
        return new TypeAdapter<Xor<L, R>>() {
            @Override
            public Xor<L, R> read(JsonReader in) throws IOException {
                JsonElement json = TypeAdapters.JSON_ELEMENT.read(in);
                try {
                    R value = rightAdapter.fromJsonTree(json);
                    return Xor.right(value);
                } catch (Throwable e) {
                    L value = leftAdapter.fromJsonTree(json);
                    return Xor.left(value);
                }
            }

            @Override
            public void write(JsonWriter out, Xor<L, R> xor) throws IOException {
                throw new JsonParseException("Writing Xor is not supported");
            }
        };
    }

    /**
     * Creates a Xor adapter specifically for the case in which the left side is a
     * SaltError. This is used to catch any Salt-side or JSON parsing errors.
     *
     * @param <R> the generic type for the right side of the Xor
     * @param innerAdapter the inner adapter
     * @return the Xor type adapter
     */
    private <R> TypeAdapter<Xor<SaltError, R>> errorAdapter(TypeAdapter<R> innerAdapter) {
        return new TypeAdapter<Xor<SaltError, R>>() {
            @Override
            public Xor<SaltError, R> read(JsonReader in) throws IOException {
                JsonElement json = TypeAdapters.JSON_ELEMENT.read(in);
                try {
                    R value = innerAdapter.fromJsonTree(json);
                    return Xor.right(value);
                } catch (Throwable e) {
                    Optional<SaltError> saltError =
                            extractErrorString(json).flatMap(SaltErrorUtils::deriveError);
                    return Xor.left(saltError.orElse(new JsonParsingError(json, e)));
                }
            }

            private Optional<String> extractErrorString(JsonElement json) {
                if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
                    return Optional.of(json.getAsJsonPrimitive().getAsString());
                }
                return Optional.empty();
            }

            @Override
            public void write(JsonWriter out, Xor<SaltError, R> xor) throws IOException {
                throw new JsonParseException("Writing Xor is not supported");
            }
        };
    }
}
