package com.suse.salt.netapi.calls;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.Return;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Class representing a function call of a salt runner module.
 *
 * @param <R> the return type of the called function
 */
public class RunnerCall<R> extends AbstractCall<R> {

    private final Optional<Map<String, ?>> kwargs;

    public RunnerCall(String functionName, Optional<Map<String, ?>> kwargs,
            TypeToken<R> returnType) {
        super(functionName, returnType);
        this.kwargs = kwargs;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPayload() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("fun", getFunction());
        kwargs.ifPresent(kwargs -> payload.put("kwarg", kwargs));
        return payload;
    }

    /**
     * Calls a runner module function on the master asynchronously and
     * returns information about the scheduled job that can be used to query the result.
     * Authentication is done with the token therefore you have to login prior
     * to using this function.
     *
     * @param client SaltClient instance
     * @return information about the scheduled job
     */
    public CompletionStage<RunnerAsyncResult<R>> callAsync(final SaltClient client) {
        return client.call(this, Client.RUNNER_ASYNC, "/",
                new TypeToken<Return<List<RunnerAsyncResult<R>>>>(){}).thenApply(wrapper -> {
                    RunnerAsyncResult<R> result = wrapper.getResult().get(0);
                    result.setType(getReturnType());
                    return result;
                });
    }

    /**
     * Calls a runner module function on the master asynchronously and
     * returns information about the scheduled job that can be used to query the result.
     * Authentication is done with the given credentials no session token is created.
     *
     * @param client SaltClient instance
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @return information about the scheduled job
     */
    public CompletionStage<RunnerAsyncResult<R>> callAsync(final SaltClient client, String username,
            String password, AuthModule authModule) {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("username", username);
        customArgs.put("password", password);
        customArgs.put("eauth", authModule.getValue());

        return client.call(
                this, Client.RUNNER_ASYNC, "/run",
                Optional.of(customArgs),
                new TypeToken<Return<List<RunnerAsyncResult<R>>>>(){}).thenApply(wrapper -> {
                    RunnerAsyncResult<R> result = wrapper.getResult().get(0);
                    result.setType(getReturnType());
                    return result;
                });
    }

    /**
     * Calls a runner module function on the master and synchronously
     * waits for the result. Authentication is done with the given credentials
     * no session token is created.
     *
     * @param client SaltClient instance
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @return the result of the called function
     */
    public CompletionStage<Result<R>> callSync(final SaltClient client, String username, String password,
            AuthModule authModule) {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("username", username);
        customArgs.put("password", password);
        customArgs.put("eauth", authModule.getValue());
        Type xor = parameterizedType(null, Result.class, getReturnType().getType());
        Type listType = parameterizedType(null, List.class, xor);
        Type wrapperType = parameterizedType(null, Return.class, listType);

        @SuppressWarnings("unchecked")
        CompletionStage<Result<R>> resultCompletionStage =
                client.call(
                        this, Client.RUNNER, "/run", Optional.of(customArgs),
                        (TypeToken<Return<List<Result<R>>>>) TypeToken.get(wrapperType))
                        .thenApply(wrapper -> wrapper.getResult().get(0));
        return resultCompletionStage;
    }

    /**
     * Calls a runner module function on the master and synchronously
     * waits for the result. Authentication is done with the token therefore you
     * have to login prior to using this function.
     *
     * @param client SaltClient instance
     * @return the result of the called function
     */
    public CompletionStage<Result<R>> callSync(final SaltClient client) {
        Type xor = parameterizedType(null, Result.class, getReturnType().getType());
        Type listType = parameterizedType(null, List.class, xor);
        Type wrapperType = parameterizedType(null, Return.class, listType);

        @SuppressWarnings("unchecked")
        CompletionStage<Result<R>> resultCompletionStage =
                client.call(this, Client.RUNNER, "/",
                        (TypeToken<Return<List<Result<R>>>>) TypeToken.get(wrapperType))
                        .thenApply(wrapper -> wrapper.getResult().get(0));
        return resultCompletionStage;
    }

}
