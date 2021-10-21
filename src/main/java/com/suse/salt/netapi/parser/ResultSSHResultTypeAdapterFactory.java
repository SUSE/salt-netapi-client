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
import com.suse.salt.netapi.errors.SaltSSHError;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.Return;
import com.suse.salt.netapi.results.SSHResult;
import com.suse.salt.netapi.utils.SaltErrorUtils;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

/**
 * {@link TypeAdapterFactory} for creating type adapters for parsing wrapped
 * Result&lt;SSHResult&gt; objects.
 */
public class ResultSSHResultTypeAdapterFactory implements TypeAdapterFactory {

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
                    return Result.success(value);
                } catch (Throwable e) {
                    Type sshResultType = parameterizedType(null, SSHResult.class, JsonElement.class);
                    TypeToken<Return<List<Map<String, Result<R>>>>> typeToken =
                            (TypeToken<Return<List<Map<String, Result<R>>>>>) TypeToken.get(sshResultType);
                    SSHResult<JsonElement> result = JsonParser.GSON.fromJson(json, typeToken.getType());
                    if (result.getRetcode() != 0) {
                        return Result.error(
                                result.getStderr()
                                        .flatMap(SaltErrorUtils::deriveError)
                                        .orElse(
                                                new SaltSSHError(result.getRetcode(),
                                                        result.getStderr().orElse(""))
                                        )
                        );
                    } else {
                        return Result.error(new JsonParsingError(json, e));
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

