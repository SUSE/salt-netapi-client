package com.suse.salt.netapi.calls;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.runner.Jobs;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.Batch;
import com.suse.salt.netapi.datatypes.target.SSHTarget;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.errors.GenericError;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.event.EventStream;
import com.suse.salt.netapi.event.JobReturnEvent;
import com.suse.salt.netapi.event.RunnerReturnEvent;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.Return;
import com.suse.salt.netapi.results.SSHResult;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.utils.ClientUtils;
import com.suse.salt.netapi.utils.FunctionE;

import javax.websocket.CloseReason;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

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
    private final Optional<?> metadata;

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType,
            Optional<?> metadata) {
        this.functionName = functionName;
        this.arg = arg;
        this.kwarg = kwarg;
        this.returnType = returnType;
        this.metadata = metadata;
    }

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType) {
        this(functionName, arg, kwarg, returnType, Optional.empty());
    }

    private static <R> void onRunnerReturn(
            String jid,
            RunnerReturnEvent rre,
            TypeToken<Result<R>> tt,
            Map<String, CompletableFuture<Result<R>>> targets
    ) {
        final RunnerReturnEvent.Data data = rre.getData();
        if (data.getFun().contentEquals("runner.jobs.list_job")) {
            Jobs.Info result = data.getResult(Jobs.Info.class);
            if (result.getJid().equals(jid)) {
                targets.forEach((mid, f) -> {
                    result.getResult(mid, tt).ifPresent(f::complete);
                });
            }
        }
    }

    private static <R> void onJobReturn(
            String jid,
            JobReturnEvent jre,
            TypeToken<Result<R>> tt,
            Map<String, CompletableFuture<Result<R>>> targets
    ) {
        if (jre.getJobId().contentEquals(jid)) {
            CompletableFuture<Result<R>> f = targets.get(jre.getMinionId());
            if (f != null) {
                f.complete(jre.getData().getResult(tt));
            }
        }
    }

    public LocalCall<R> withMetadata(Object metadata) {
        return new LocalCall<>(functionName, arg, kwarg, returnType, Optional.of(metadata));
    }

    public LocalCall<R> withoutMetadata() {
        return new LocalCall<>(functionName, arg, kwarg, returnType, Optional.empty());
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
        metadata.ifPresent(m -> payload.put("metadata", m));
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

        Return<List<LocalAsyncResult<R>>> wrapper = client.call(
                this, Client.LOCAL_ASYNC, "/",
                Optional.of(customArgs),
                new TypeToken<Return<List<LocalAsyncResult<R>>>>(){});
        LocalAsyncResult<R> result = wrapper.getResult().get(0);
        result.setType(getReturnType());
        return result;
    }

    /**
     * Calls this salt call via the async client and returns the results
     * as they come in via the event stream.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @param events the event stream to use
     * @param cancel future to cancel the action
     * @return a map from minion id to future of the result.
     * @throws SaltException if anything goes wrong
     */
    public Map<String, CompletionStage<Result<R>>> callAsync(
            SaltClient client,
            Target<?> target,
            String username,
            String password,
            AuthModule authModule,
            EventStream events,
            CompletionStage<GenericError> cancel)
            throws SaltException {
        return callAsync(
                localCall -> localCall.callAsync(client, target, username,
                        password, authModule),
                runnerCall -> runnerCall.callAsync(client, username,
                        password, authModule),
                events,
                cancel
        );
    }


    /**
     * Calls this salt call via the async client and returns the results
     * as they come in via the event stream.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param events the event stream to use
     * @param cancel future to cancel the action
     * @return a map from minion id to future of the result.
     * @throws SaltException if anything goes wrong
     */
    public Map<String, CompletionStage<Result<R>>> callAsync(
            SaltClient client,
            Target<?> target,
            EventStream events,
            CompletionStage<GenericError> cancel)
            throws SaltException {
        return callAsync(
                localCall -> localCall.callAsync(client, target),
                runnerCall -> runnerCall.callAsync(client),
                events,
                cancel
        );
    }


    /**
     * Calls this salt call via the async client and returns the results
     * as they come in via the event stream.
     *
     * @param localAsync function providing callAsync for LocalCalls
     * @param runnerAsync function providing callAsync for RunnerCalls
     * @param events the event stream to use
     * @param cancel future to cancel the action
     * @return a map from minion id to future of the result.
     * @throws SaltException if anything goes wrong
     */
    public Map<String, CompletionStage<Result<R>>> callAsync(
            FunctionE<LocalCall<R>, LocalAsyncResult<R>> localAsync,
            FunctionE<RunnerCall<Map<String, R>>,
                    RunnerAsyncResult<Map<String, R>>> runnerAsync,
            EventStream events,
            CompletionStage<GenericError> cancel)
            throws SaltException {

        LocalAsyncResult<R> lar = localAsync.apply(this);
        TypeToken<R> returnTypeToken = this.getReturnType();
        Type result = ClientUtils.parameterizedType(null,
                Result.class, returnTypeToken.getType());
        TypeToken<Result<R>> typeToken = (TypeToken<Result<R>>) TypeToken.get(result);

        Map<String, CompletableFuture<Result<R>>> futures =
                lar.getMinions().stream().collect(Collectors.toMap(
                        mid -> mid,
                        mid -> new CompletableFuture<>()
                    )
                );

        EventListener listener = new EventListener() {
            @Override
            public void notify(Event event) {
                Optional<JobReturnEvent> jobReturnEvent = JobReturnEvent.parse(event);
                if (jobReturnEvent.isPresent()) {
                    jobReturnEvent.ifPresent(e ->
                            onJobReturn(lar.getJid(), e, typeToken, futures)
                    );
                } else {
                    RunnerReturnEvent.parse(event).ifPresent(e ->
                            onRunnerReturn(lar.getJid(), e, typeToken, futures)
                    );
                }
            }

            @Override
            public void eventStreamClosed(CloseReason closeReason) {
                Result<R> error = Result.error(
                        new GenericError(
                                "EventStream closed with reason "
                                        + closeReason));
                futures.values().forEach(f -> f.complete(error));
            }
        };

        CompletableFuture<Void> allResolves = CompletableFuture.allOf(
                futures.entrySet().stream().map(entry ->
                    //mask errors since CompletableFuture.allOf resolves on first error
                    entry.getValue().handle((v, e) -> 0)
                ).toArray(CompletableFuture[]::new)
        );

        allResolves.whenComplete((v, e) ->
                events.removeEventListener(listener)
        );

        cancel.whenComplete((v, e) -> {
            if (v != null) {
                Result<R> error = Result.error(v);
                futures.values().forEach(f -> f.complete(error));
            } else if (e != null) {
                futures.values().forEach(f -> f.completeExceptionally(e));
            }
        });

        events.addEventListener(listener);

        // fire off lookup to get a result event for minions that already finished
        // before we installed the listeners
        runnerAsync.apply(Jobs.lookupJid(lar));

        return futures.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> (CompletionStage<Result<R>>) e.getValue()
        ));
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

        Return<List<LocalAsyncResult<R>>> wrapper = client.call(
                this, Client.LOCAL_ASYNC, "/run",
                Optional.of(customArgs),
                new TypeToken<Return<List<LocalAsyncResult<R>>>>(){});
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
    public Map<String, Result<R>> callSync(final SaltClient client, Target<?> target)
            throws SaltException {
        return callSyncHelper(client, target, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty()).get(0);
    }

    /**
     * Calls a execution module function on the given target with batching and
     * synchronously waits for the result. Authentication is done with the token
     * therefore you have to login prior to using this function.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param batch  the batch specification
     * @return A list of maps with each list representing each batch, and maps containing
     * the results with the minion names as keys.
     * @throws SaltException if anything goes wrong
     */
    public List<Map<String, Result<R>>> callSync(final SaltClient client, Target<?> target,
            Batch batch)
            throws SaltException {
        return callSyncHelper(client, target, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(batch));
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
    public Map<String, Result<R>> callSync(
            final SaltClient client, Target<?> target,
            String username, String password, AuthModule authModule)
            throws SaltException {
        return callSyncHelper(client, target, Optional.of(username),
                Optional.of(password), Optional.of(authModule), Optional.empty()).get(0);
    }

    /**
     * Calls a execution module function on the given target with batching and
     * synchronously waits for the result. Authentication is done with the given
     * credentials no session token is created.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param username username for authentication
     * @param password password for authentication
     * @param authModule authentication module to use
     * @param batch the batch specification
     * @return A list of maps with each list representing each batch, and maps containing
     * the results with the minion names as keys.
     * @throws SaltException if anything goes wrong
     */
    public List<Map<String, Result<R>>> callSync(
            final SaltClient client, Target<?> target,
            String username, String password, AuthModule authModule, Batch batch)
            throws SaltException {
        return callSyncHelper(client, target, Optional.of(username),
                Optional.of(password), Optional.of(authModule), Optional.of(batch));
    }

    /**
     * Helper to call an execution module function on the given target for batched or
     * unbatched while also providing an option to use the given credentials or to use a
     * prior created token. Synchronously waits for the result.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param username username for authentication, empty for token auth
     * @param password password for authentication, empty for token auth
     * @param authModule authentication module to use, empty for token auth
     * @param batch the batch parameter, empty for unbatched
     * @return A list of maps with each list representing each batch, and maps containing
     * the results with the minion names as keys. The first list is the entire
     * output for unbatched input.
     * @throws SaltException
     */
    private List<Map<String, Result<R>>> callSyncHelper(
            final SaltClient client, Target<?> target,
            Optional<String> username, Optional<String> password,
            Optional<AuthModule> authModule, Optional<Batch> batch)
            throws SaltException {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.put("tgt", target.getTarget());
        customArgs.put("expr_form", target.getType());
        username.ifPresent(v -> customArgs.put("username", v));
        password.ifPresent(v -> customArgs.put("password", v));
        authModule.ifPresent(v -> customArgs.put("eauth", v.getValue()));
        batch.ifPresent(v -> customArgs.put("batch", v.toString()));

        Client clientType = batch.isPresent() ? Client.LOCAL_BATCH : Client.LOCAL;
        // We need a different endpoint for credentials vs token auth
        String endPoint = username.isPresent() ? "/run" : "/";

        Type xor = parameterizedType(null, Result.class, getReturnType().getType());
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Return.class, listType);

        @SuppressWarnings("unchecked")
        Return<List<Map<String, Result<R>>>> wrapper = client.call(this,
                clientType, endPoint,
                Optional.of(customArgs),
                (TypeToken<Return<List<Map<String, Result<R>>>>>)
                TypeToken.get(wrapperType));
        return wrapper.getResult();
    }

    /**
     * Call an execution module function on the given target via salt-ssh and synchronously
     * wait for the result.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param cfg Salt SSH configuration object
     * @return a map containing the results with the minion name as key
     * @throws SaltException if anything goes wrong
     */
    public Map<String, Result<SSHResult<R>>> callSyncSSH(final SaltClient client,
            SSHTarget<?> target, SaltSSHConfig cfg) throws SaltException {
        Map<String, Object> args = new HashMap<>();
        args.putAll(getPayload());
        args.put("tgt", target.getTarget());
        args.put("expr_form", target.getType());

        SaltSSHUtils.mapConfigPropsToArgs(cfg, args);

        Type xor = parameterizedType(null, Result.class,
                parameterizedType(null, SSHResult.class, getReturnType().getType()));
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Return.class, listType);

        @SuppressWarnings("unchecked")
        Return<List<Map<String, Result<SSHResult<R>>>>> wrapper = client.call(this,
                Client.SSH, "/run",
                Optional.of(args),
                (TypeToken<Return<List<Map<String, Result<SSHResult<R>>>>>>)
                TypeToken.get(wrapperType));
        return wrapper.getResult().get(0);
    }
}
