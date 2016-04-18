package com.suse.salt.netapi.calls;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.results.SaltError;
import com.suse.salt.netapi.utils.Xor;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class representing a function call of a salt execution module.
 *
 * @param <R> the return type of the called function
 */
public class LocalCall<R> implements Call<R> {

    private final String functionName;
    private final Optional<List<?>> arg;
    private final Optional<Map<String, ?>> kwarg;
    private final TypeToken<R> returnType;

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType) {
        this.functionName = functionName;
        this.arg = arg;
        this.kwarg = kwarg;
        this.returnType = returnType;
    }

    public TypeToken<R> getReturnType() {
        return returnType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPayload() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("fun", functionName);
        arg.ifPresent(arg -> payload.put("arg", arg));
        kwarg.ifPresent(kwarg -> payload.put("kwarg", kwarg));
        return payload;
    }

    /**
     * Calls a execution module function on the given target asynchronously and
     * returns information about the scheduled job that can be used to query the result.
     * Authentication is done with the token therefore you have to login prior
     * to using this function.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @return information about the scheduled job
     * @throws SaltException if anything goes wrong
     */
    public LocalAsyncResult<R> callAsync(final SaltClient client, Target<?> target)
            throws SaltException {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("tgt", target.getTarget());
        customArgs.put("expr_form", target.getType());

        Result<List<LocalAsyncResult<R>>> wrapper = client.call(
                this, Client.LOCAL_ASYNC, "/",
                Optional.of(customArgs),
                new TypeToken<Result<List<LocalAsyncResult<R>>>>(){});
        LocalAsyncResult<R> result = wrapper.getResult().get(0);
        result.setType(getReturnType());
        return result;
    }

    /**
     * Calls a execution module function on the given target asynchronously and
     * returns information about the scheduled job that can be used to query the result.
     * Authentication is done with the given credentials no session token is created.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @return information about the scheduled job
     * @throws SaltException if anything goes wrong
     */
    public LocalAsyncResult<R> callAsync(final SaltClient client, Target<?> target,
            String username, String password, AuthModule authModule)
            throws SaltException {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("username", username);
        customArgs.put("password", password);
        customArgs.put("eauth", authModule.getValue());
        customArgs.put("tgt", target.getTarget());
        customArgs.put("expr_form", target.getType());

        Result<List<LocalAsyncResult<R>>> wrapper = client.call(
                this, Client.LOCAL_ASYNC, "/run",
                Optional.of(customArgs),
                new TypeToken<Result<List<LocalAsyncResult<R>>>>(){});
        LocalAsyncResult<R> result = wrapper.getResult().get(0);
        result.setType(getReturnType());
        return result;
    }

    /**
     * Calls a execution module function on the given target and synchronously
     * waits for the result. Authentication is done with the token therefore you
     * have to login prior to using this function.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @return a map containing the results with the minion name as key
     * @throws SaltException if anything goes wrong
     */
    public Map<String, Xor<SaltError, R>> callSync(final SaltClient client, Target<?> target)
            throws SaltException {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.put("tgt", target.getTarget());
        customArgs.put("expr_form", target.getType());

        Type xor = parameterizedType(null, Xor.class, SaltError.class, getReturnType().getType());
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Result.class, listType);

        @SuppressWarnings("unchecked")
        Result<List<Map<String, Xor<SaltError, R>>>> wrapper = client.call(this, Client.LOCAL, "/",
                Optional.of(customArgs),
                (TypeToken<Result<List<Map<String, Xor<SaltError, R>>>>>) TypeToken.get(wrapperType));
        return wrapper.getResult().get(0);
    }

    /**
     * Calls a execution module function on the given target and synchronously
     * waits for the result. Authentication is done with the given credentials
     * no session token is created.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @return a map containing the results with the minion name as key
     * @throws SaltException if anything goes wrong
     */
    public Map<String, Xor<SaltError, R>> callSync(final SaltClient client, Target<?> target,
            String username, String password, AuthModule authModule)
            throws SaltException {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("username", username);
        customArgs.put("password", password);
        customArgs.put("eauth", authModule.getValue());
        customArgs.put("tgt", target.getTarget());
        customArgs.put("expr_form", target.getType());

        Type xor = parameterizedType(null, Xor.class, SaltError.class, getReturnType().getType());
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Result.class, listType);

        @SuppressWarnings("unchecked")
        Result<List<Map<String, Xor<SaltError, R>>>> wrapper = client.call(this, Client.LOCAL, "/run",
                Optional.of(customArgs),
                (TypeToken<Result<List<Map<String, Xor<SaltError, R>>>>>) TypeToken.get(wrapperType));
        return wrapper.getResult().get(0);
    }
}
