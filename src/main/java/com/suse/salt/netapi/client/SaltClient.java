package com.suse.salt.netapi.client;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.Call;
import com.suse.salt.netapi.calls.Client;
import com.suse.salt.netapi.calls.SaltSSHConfig;
import com.suse.salt.netapi.calls.SaltSSHUtils;
import com.suse.salt.netapi.client.impl.HttpAsyncClientConnectionFactory;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.config.ProxySettings;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.event.EventStream;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.parser.JsonParser;
import com.suse.salt.netapi.results.SSHRawResult;
import com.suse.salt.netapi.results.Result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * Salt API client.
 */
public class SaltClient implements AutoCloseable {

    /** The configuration object */
    private final ClientConfig config;

    /** The async connection factory object */
    private final AsyncConnectionFactory asyncConnectionFactory;

    private final Gson gson = new GsonBuilder().create();

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the Salt API URL
     */
    public SaltClient(URI url) {
        this(url, new ClientConfig());
    }

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the Salt API URL
     * @param config client config
     */
    public SaltClient(URI url, ClientConfig config) {
        this.config = config;
        // Put the URL in the config
        config.put(ClientConfig.URL, url);

        // TODO: Replace connectionFactory with this
        this.asyncConnectionFactory = new HttpAsyncClientConnectionFactory(config);
    }

    /**
     * Directly access the configuration.
     *
     * @return the configuration object
     */
    public ClientConfig getConfig() {
        return config;
    }

    /**
     * Configure to use a proxy when connecting to the Salt API.
     *
     * @param settings the proxy settings
     */
    public void setProxy(ProxySettings settings) {
        if (settings.getHostname() != null) {
            config.put(ClientConfig.PROXY_HOSTNAME, settings.getHostname());
            config.put(ClientConfig.PROXY_PORT, settings.getPort());
        }
        if (settings.getUsername() != null) {
            config.put(ClientConfig.PROXY_USERNAME, settings.getUsername());
            if (settings.getPassword() != null) {
                config.put(ClientConfig.PROXY_PASSWORD, settings.getPassword());
            }
        }
    }

    /**
     * Non-blocking version of login() returning a CompletionStage with the token.
     * <p>
     * {@code POST /login}
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @return CompletionStage holding the authentication token
     */
    public CompletionStage<Token> login(final String username,
            final String password, final AuthModule eauth) {
        Map<String, String> props = new LinkedHashMap<>();
        props.put("username", username);
        props.put("password", password);
        props.put("eauth", eauth.getValue());

        String payload = gson.toJson(props);

        CompletionStage<Token> result = asyncConnectionFactory
                .create("/login", JsonParser.TOKEN)
                .post(payload)
                .thenApply(r -> {
                    // They return a list of tokens here, take the first one
                    Token token = r.getResult().get(0);
                    config.put(ClientConfig.TOKEN, token.getToken());
                    return token;
                });

        return result;
    }

    /**
     * Perform logout and clear the session token from the config.
     * <p>
     * {@code POST /logout}
     *
     * @return true if the logout was successful, otherwise false
     */
    public CompletionStage<Boolean> logout() {
        return asyncConnectionFactory
                .create("/logout", JsonParser.STRING)
                .post("")
                .thenApply(s -> "Your token has been cleared".contentEquals(s.getResult()));
    }

    /**
     * Generic interface to start any execution command bypassing normal session handling.
     * <p>
     * {@code POST /run}
     *
     * @param <T> type of the tgt property for this command
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @param client the client
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return Map key: minion id, value: command result from that minion
     */
    public <T> CompletionStage<Map<String, Object>> run(final String username, final String password,
            final AuthModule eauth, final String client, final Target<T> target,
            final String function, List<Object> args, Map<String, Object> kwargs) {
        Map<String, Object> props = new HashMap<>();
        props.put("username", username);
        props.put("password", password);
        props.put("eauth", eauth.getValue());
        props.put("client", client);
        props.putAll(target.getProps());
        props.put("fun", function);
        props.put("arg", args);
        props.put("kwarg", kwargs);

        List<Map<String, Object>> list =  Collections.singletonList(props);

        String payload = gson.toJson(list);

        CompletionStage<Map<String, Object>> result = asyncConnectionFactory
                .create("/run", JsonParser.RUN_RESULTS)
                .post(payload)
                .thenApply(s -> s.getResult().get(0));
        return result;
    }

    /**
     * Calls salt-ssh with a command in raw shell mode (commands bypass Salt and
     * gets executed as shell commands).
     *
     * @param <T> type of the tgt property for this command
     * @param command to be executed
     * @param target glob type, targets to be reached by the command
     * @param cfg SaltSSH config holder
     * @return a map in which every key is a host associated to the result of the
     * raw command
     */
    public <T> CompletionStage<Map<String, Result<SSHRawResult>>> runRawSSHCommand(final String command,
            final Target<T> target, SaltSSHConfig cfg) {
        Map<String, Object> props = new HashMap<>();
        props.put("client", Client.SSH.getValue());
        props.putAll(target.getProps());
        props.put("fun", command);
        props.put("raw_shell", true);

        SaltSSHUtils.mapConfigPropsToArgs(cfg, props);

        List<Map<String, Object>> list = Collections.singletonList(props);

        String payload = gson.toJson(list);

        CompletionStage<Map<String, Result<SSHRawResult>>> result = asyncConnectionFactory
                .create("/run", JsonParser.RUNSSHRAW_RESULTS)
                .post(payload)
                .thenApply(r -> r.getResult().get(0));

        return result;
    }

    /**
     * Query statistics from the CherryPy Server.
     * <p>
     * {@code GET /stats}
     *
     * @return the stats
     */
    public CompletionStage<Stats> stats() {
        return asyncConnectionFactory.create("/stats", JsonParser.STATS).get();
    }

    /**
     * Returns a WebSocket @ClientEndpoint annotated object connected
     * to the /ws ServerEndpoint.
     * <p>
     * The stream object supports the {@link EventStream} interface which allows the caller
     * to register/unregister for stream event notifications as well as close the event
     * stream.
     * <p>
     * Note: {@link SaltClient#login(String, String, AuthModule)} or must be called prior
     * to calling this method.
     * <p>
     * {@code GET /events}
     *
     * @param listeners event listeners to be added before the stream is initialized
     * @return the event stream
     * @throws SaltException in case of an error during websocket stream initialization
     */
    public EventStream events(EventListener... listeners) throws SaltException {
        return new EventStream(config, listeners);
    }

    /**
     * Generic interface to make a {@link Call} to an endpoint using a given {@link Client}.
     *
     * @param <R> the object type that will be returned
     * @param call the call
     * @param client the client to use
     * @param endpoint the endpoint
     * @param custom map of arguments
     * @param type return type as a TypeToken
     * @return the result of the call
     */
    public <R> CompletionStage<R> call(Call<?> call, Client client, String endpoint, Optional<Map<String,
            Object>> custom, TypeToken<R> type) {
        Map<String, Object> props = new HashMap<>();
        props.putAll(call.getPayload());
        props.put("client", client.getValue());
        custom.ifPresent(props::putAll);

        List<Map<String, Object>> list = Collections.singletonList(props);
        String payload = gson.toJson(list);

        CompletionStage<R> result = asyncConnectionFactory
                .create(endpoint, new JsonParser<>(type))
                .post(payload);

        return result;
    }

    /**
     * Convenience method to make a call without arguments.
     *
     * @param <R> the object type that will be returned
     * @param call the call
     * @param client the client to use
     * @param endpoint the endpoint
     * @param type return type as a TypeToken
     * @return the result of the call
     */
    public <R> CompletionStage<R> call(Call<?> call, Client client, String endpoint, TypeToken<R> type) {
        return call(call, client, endpoint, Optional.empty(), type);
    }

    @Override
    public void close() throws Exception {
        asyncConnectionFactory.close();
    }
}
