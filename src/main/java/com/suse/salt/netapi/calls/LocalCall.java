package com.suse.salt.netapi.calls;

import static com.suse.salt.netapi.utils.ClientUtils.parameterizedType;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.calls.runner.Jobs;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Batch;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.target.SSHTarget;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.errors.GenericError;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.event.EventStream;
import com.suse.salt.netapi.event.JobReturnEvent;
import com.suse.salt.netapi.event.RunnerReturnEvent;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.Return;
import com.suse.salt.netapi.results.SSHResult;
import com.suse.salt.netapi.utils.ClientUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class representing a function call of a salt execution module.
 *
 * @param <R> the return type of the called function
 */
public class LocalCall<R> extends AbstractCall<R> {

    private final Optional<List<?>> arg;
    private final Optional<Map<String, ?>> kwarg;
    private final Optional<?> metadata;
    private final Optional<Integer> timeout;
    private final Optional<Integer> gatherJobTimeout;

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType,
            Optional<?> metadata, Optional<Integer> timeout,
            Optional<Integer> gatherJobTimeout) {
        super(functionName, returnType);
        this.arg = arg;
        this.kwarg = kwarg;
        this.metadata = metadata;
        this.timeout = timeout;
        this.gatherJobTimeout = gatherJobTimeout;
    }

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType,
            Optional<Integer> timeout, Optional<Integer> gatherJobTimeout) {
        this(functionName, arg, kwarg, returnType, Optional.empty(),
                timeout, gatherJobTimeout);
    }

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType,
            Optional<?> metadata) {
        this(functionName, arg, kwarg, returnType, metadata, Optional.empty(),
                Optional.empty());
    }

    public LocalCall(String functionName, Optional<List<?>> arg,
            Optional<Map<String, ?>> kwarg, TypeToken<R> returnType) {
        this(functionName, arg, kwarg, returnType, Optional.empty());
    }

    public LocalCall<R> withMetadata(Object metadata) {
        return new LocalCall<>(getFunction(), arg, kwarg, getReturnType(),
                Optional.of(metadata), timeout, gatherJobTimeout);
    }

    public LocalCall<R> withoutMetadata() {
        return new LocalCall<>(getFunction(), arg, kwarg, getReturnType(),
                Optional.empty(), timeout, gatherJobTimeout);
    }

    public LocalCall<R> withTimeouts(Optional<Integer> timeout,
            Optional<Integer> gatherJobTimeout) {
        return new LocalCall<>(getFunction(), arg, kwarg, getReturnType(), metadata,
                timeout, gatherJobTimeout);
    }

    public LocalCall<R> withoutTimeouts() {
        return new LocalCall<>(getFunction(), arg, kwarg, getReturnType(), metadata,
                Optional.empty(), Optional.empty());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getPayload() {
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("fun", getFunction());
        arg.ifPresent(arg -> payload.put("arg", arg));
        kwarg.ifPresent(kwarg -> payload.put("kwarg", kwarg));
        metadata.ifPresent(m -> payload.put("metadata", m));
        timeout.ifPresent(timeout -> payload.put("timeout", timeout));
        gatherJobTimeout.ifPresent(gatherJobTimeout -> payload.put("gather_job_timeout",
                gatherJobTimeout));
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
     * @param auth authentication credentials to use
     * @return information about the scheduled job
     */
    public CompletionStage<LocalAsyncResult<R>> callAsync(final SaltClient client, Target<?> target, AuthMethod auth) {
        return client.call(
                this, Client.LOCAL_ASYNC, Optional.of(target), Collections.emptyMap(),
                new TypeToken<Return<List<LocalAsyncResult<R>>>>(){}, auth)
                .thenApply(wrapper -> {
                    LocalAsyncResult<R> result = wrapper.getResult().get(0);
                    result.setType(getReturnType());
                    return result;
                });
    }

    /**
     * Calls this salt call via the async client and returns the results
     * as they come in via the event stream.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param events the event stream to use
     * @param cancel future to cancel the action
     * @param auth authentication credentials to use
     * @return a map from minion id to future of the result.
     */
    public CompletionStage<Map<String, CompletionStage<Result<R>>>> callAsync(
            SaltClient client,
            Target<?> target,
            AuthMethod auth,
            EventStream events,
            CompletionStage<GenericError> cancel) {
        return callAsync(
                localCall -> localCall.callAsync(client, target, auth),
                runnerCall -> runnerCall.callAsync(client, auth),
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
     */
    public CompletionStage<Map<String, CompletionStage<Result<R>>>> callAsync(
            Function<LocalCall<R>, CompletionStage<LocalAsyncResult<R>>> localAsync,
            Function<RunnerCall<Map<String, R>>,
                                CompletionStage<RunnerAsyncResult<Map<String, R>>>> runnerAsync,
            EventStream events,
            CompletionStage<GenericError> cancel) {

        return localAsync.apply(this).thenApply(lar -> {
            TypeToken<R> returnTypeToken = this.getReturnType();
            Type result = ClientUtils.parameterizedType(null,
                    Result.class, returnTypeToken.getType());
            @SuppressWarnings("unchecked")
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
                public void eventStreamClosed(int code, String phrase) {
                    Result<R> error = Result.error(
                            new GenericError(
                                    "EventStream closed with reason "
                                            + phrase));
                    futures.values().forEach(f -> f.complete(error));
                }
            };

            CompletableFuture<Void> allResolves = CompletableFuture.allOf(
                    futures.entrySet().stream().map(entry ->
                            // mask errors since CompletableFuture.allOf resolves on first error
                            entry.getValue().<Integer>handle((v, e) -> 0)
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
        });
    }


    /**
     * Calls a execution module function on the given target and synchronously
     * waits for the result. Authentication is done with the token therefore you
     * have to login prior to using this function.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param auth authentication credentials to use
     * @return a map containing the results with the minion name as key
     */
    public CompletionStage<Map<String, Result<R>>> callSync(final SaltClient client, Target<?> target,
            AuthMethod auth) {
        return callSyncHelperNonBlock(client, target, auth, Optional.empty())
                .thenApply(r -> r.get(0));
    }

    /**
     * Calls a execution module function on the given target with batching and
     * synchronously waits for the result. Authentication is done with the token
     * therefore you have to login prior to using this function.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param batch  the batch specification
     * @param auth authentication credentials to use
     * @return A list of maps with each list representing each batch, and maps containing
     * the results with the minion names as keys.
     */
    public CompletionStage<List<Map<String, Result<R>>>> callSync(final SaltClient client, Target<?> target,
            AuthMethod auth, Batch batch) {
        return callSyncHelperNonBlock(client, target, auth, Optional.of(batch));
    }

    /**
     * Helper to call an execution module function on the given target for batched or
     * unbatched while also providing an option to use the given credentials or to use a
     * prior created token. Synchronously waits for the result.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param batch the batch parameter, empty for unbatched
     * @param auth authentication credentials to use
     * @return A list of maps with each list representing each batch, and maps containing
     * the results with the minion names as keys. The first list is the entire
     * output for unbatched input.
     */
    private CompletionStage<List<Map<String, Result<R>>>> callSyncHelperNonBlock(
            final SaltClient client, Target<?> target, AuthMethod auth, Optional<Batch> batch) {
        Map<String, Object> customArgs = new HashMap<>();
        customArgs.putAll(getPayload());
        customArgs.putAll(target.getProps());
        batch.ifPresent(v -> customArgs.put("batch", v.toString()));

        Client clientType = batch.isPresent() ? Client.LOCAL_BATCH : Client.LOCAL;

        Type xor = parameterizedType(null, Result.class, getReturnType().getType());
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Return.class, listType);
        TypeToken<Return<List<Map<String, Result<R>>>>> typeToken =
                (TypeToken<Return<List<Map<String, Result<R>>>>>) TypeToken.get(wrapperType);

        return client.call(this,
                clientType,
                Optional.of(target),
                customArgs,
                typeToken,
                auth).thenApply(Return::getResult);
    }

    /**
     * Call an execution module function on the given target via salt-ssh and synchronously
     * wait for the result.
     *
     * @param client SaltClient instance
     * @param target the target for the function
     * @param cfg Salt SSH configuration object
     * @param auth authentication credentials to use
     * @return a map containing the results with the minion name as key
     */
    public CompletionStage<Map<String, Result<SSHResult<R>>>> callSyncSSH(final SaltClient client,
            SSHTarget<?> target, SaltSSHConfig cfg, AuthMethod auth) {
        Map<String, Object> args = new HashMap<>();
        args.putAll(getPayload());
        args.putAll(target.getProps());

        SaltSSHUtils.mapConfigPropsToArgs(cfg, args);

        Type xor = parameterizedType(null, Result.class,
                parameterizedType(null, SSHResult.class, getReturnType().getType()));
        Type map = parameterizedType(null, Map.class, String.class, xor);
        Type listType = parameterizedType(null, List.class, map);
        Type wrapperType = parameterizedType(null, Return.class, listType);

        return client.call(this,
                Client.SSH,
                Optional.of(target),
                args,
                (TypeToken<Return<List<Map<String, Result<SSHResult<R>>>>>>)
                        TypeToken.get(wrapperType), auth)
                .thenApply(wrapper -> wrapper.getResult().get(0));
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
}
