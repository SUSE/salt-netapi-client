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
import com.suse.salt.netapi.errors.FunctionNotAvailable;
import com.suse.salt.netapi.errors.GenericSaltError;
import com.suse.salt.netapi.errors.ModuleNotSupported;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.SSHResult;
import com.suse.salt.netapi.utils.Xor;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link TypeAdapterFactory} for creating type adapters for parsing wrapped
 * Result&lt;SSHResult&gt; objects.
 */
public class ResultSSHResultTypeAdapterFactory implements TypeAdapterFactory {

    private static final Pattern FN_UNAVAILABLE =
            Pattern.compile("'([^']+)' is not available.");
    private static final Pattern MODULE_NOT_SUPPORTED =
            Pattern.compile("'([^']+)' __virtual__ returned False");

    @Override
    @SuppressWarnings("unchecked")
    public <A> TypeAdapter<A> create(Gson gson, TypeToken<A> typeToken) {
        boolean isResult = typeToken.getRawType() == Result.class;
        Type type = typeToken.getType();
        boolean isSSHResult = isResultSSHResult(type);

        if (isResult && isSSHResult) {
            Type parameterType = ((ParameterizedType) type).getActualTypeArguments()[0];
            TypeAdapter<SSHResult<A>> sshResultAdapter = (TypeAdapter<SSHResult<A>>)
                    gson.getAdapter(TypeToken.get(parameterType));
            return (TypeAdapter<A>) resultAdapter(sshResultAdapter);
        } else {
            return null;
        }
    }

    public static boolean isResultSSHResult(Type type) {
        boolean isParametrized = type instanceof ParameterizedType;

        if (!isParametrized) {
            return false;
        }

        Type[] innerType = ((ParameterizedType) type).getActualTypeArguments();

        return isParametrized && innerType.length > 0 &&
                innerType[0] instanceof ParameterizedType &&
                ((ParameterizedType) innerType[0]).getRawType() == SSHResult.class;
    }

    private <R> TypeAdapter<Result<SSHResult<R>>> resultAdapter(
            TypeAdapter<SSHResult<R>> innerAdapter) {
        return new TypeAdapter<Result<SSHResult<R>>>() {
            @Override
            public Result<SSHResult<R>> read(JsonReader in) throws IOException {
                JsonElement json = TypeAdapters.JSON_ELEMENT.read(in);
                try {
                    SSHResult<R> value = innerAdapter.fromJsonTree(json);
                    if (!value.getReturn().isPresent() && value.getRetcode() != 0) {
                        throw new NullPointerException("No salt ssh return value," +
                                " return code: " + value.getRetcode());
                    }
                    return new Result<>(Xor.right(value));
                } catch (Throwable e) {
                    if (json.isJsonObject() && json.getAsJsonObject().has("stderr") &&
                            json.getAsJsonObject().get("stderr").isJsonPrimitive() &&
                            json.getAsJsonObject().get("stderr")
                                    .getAsJsonPrimitive().isString()) {
                        String string = json.getAsJsonObject().get("stderr")
                                    .getAsJsonPrimitive().getAsString();
                        Matcher fnuMatcher = FN_UNAVAILABLE.matcher(string);
                        Matcher mnsMatcher = MODULE_NOT_SUPPORTED.matcher(string);
                        if (fnuMatcher.find()) {
                            String fn = fnuMatcher.group(1);
                            return new Result<>(Xor.left(new FunctionNotAvailable(fn)));
                        } else if (mnsMatcher.find()) {
                            String module = mnsMatcher.group(1);
                            return new Result<>(Xor.left(new ModuleNotSupported(module)));
                        } else {
                            return new Result<>(Xor.left(new GenericSaltError(json, e)));
                        }
                    } else {
                        return new Result<>(Xor.left(new GenericSaltError(json, e)));
                    }
                }
            }

            @Override
            public void write(JsonWriter out, Result<SSHResult<R>> xor) throws IOException {
                throw new JsonParseException("Writing Xor is not supported");
            }
        };
    }

}


