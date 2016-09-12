package com.suse.salt.netapi.parser;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.suse.salt.netapi.errors.SaltError;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.Xor;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

/**
 * {@link TypeAdapterFactory} for creating type adapters for parsing {@link Result} objects.
 */
public class ResultTypeAdapterFactory implements TypeAdapterFactory {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <A> TypeAdapter<A> create(Gson gson, TypeToken<A> typeToken) {
        Type type = typeToken.getType();
        boolean isResult = typeToken.getRawType() == Result.class;
        boolean isParameterized = type instanceof ParameterizedType;
        boolean isSSHResult = ResultSSHResultTypeAdapterFactory.isResultSSHResult(type);
        if (isResult && isParameterized && !isSSHResult) {
            Type typeParam = ((ParameterizedType) type).getActualTypeArguments()[0];
            Type xorType = parameterizedType(null, Xor.class, SaltError.class, typeParam);
            TypeAdapter<Xor> xorAdapter = (TypeAdapter<Xor>) gson
                    .getAdapter(TypeToken.get(xorType));
            return (TypeAdapter<A>) wrap(xorAdapter);
        } else {
            return null;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private TypeAdapter<Result> wrap(TypeAdapter<Xor> xorTypeAdapter) {
        return new TypeAdapter<Result>() {
            @Override
            public Result read(JsonReader in) throws IOException {
                return new Result(xorTypeAdapter.read(in));
            }

            @Override
            public void write(JsonWriter out, Result result) throws IOException {
                xorTypeAdapter.write(out, result.toXor());
            }
        };
    }
}
