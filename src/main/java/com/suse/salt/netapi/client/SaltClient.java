package com.suse.salt.netapi.client;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.Call;
import com.suse.salt.netapi.calls.Client;
import com.suse.salt.netapi.calls.SaltSSHConfig;
import com.suse.salt.netapi.calls.SaltSSHUtils;
import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.client.impl.HttpClientConnectionFactory;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.config.ProxySettings;
import com.suse.salt.netapi.datatypes.Job;
import com.suse.salt.netapi.datatypes.ScheduledJob;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.cherrypy.Stats;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.event.WebSocketEventStream;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.parser.JsonParser;
import com.suse.salt.netapi.results.Return;
import com.suse.salt.netapi.results.SSHRawResult;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.ResultInfoSet;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Salt API client.
 */
public class SaltClient {

    /** The configuration object */
    private final ClientConfig config = new ClientConfig();

    /** The connection factory object */
    private final ConnectionFactory connectionFactory;

    /** The executor for async operations */
    private final ExecutorService executor;

    private final Gson gson = new GsonBuilder().create();

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the Salt API URL
     */
    public SaltClient(URI url) {
        this(url, new HttpClientConnectionFactory());
    }

    /**
     * Constructor for connecting to a given URL using a specific connection factory.
     *
     * @param url the Salt API URL
     * @param connectionFactory ConnectionFactory implementation
     */
    public SaltClient(URI url, ConnectionFactory connectionFactory) {
        this(url, connectionFactory, Executors.newCachedThreadPool());
    }

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the Salt API URL
     * @param executor ExecutorService to be used for async operations
     */
    public SaltClient(URI url, ExecutorService executor) {
        this(url, new HttpClientConnectionFactory(), executor);
    }

    /**
     * Constructor for connecting to a given URL using a specific connection factory.
     *
     * @param url the Salt API URL
     * @param connectionFactory ConnectionFactory implementation
     * @param executor ExecutorService to be used for async operations
     */
    public SaltClient(URI url, ConnectionFactory connectionFactory,
            ExecutorService executor) {
        // Put the URL in the config
        config.put(ClientConfig.URL, url);
        this.connectionFactory = connectionFactory;
        this.executor = executor;
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
     * Perform login and return the token.
     * <p>
     * {@code POST /login}
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @return the authentication token
     * @throws SaltException if anything goes wrong
     */
    public Token login(final String username, final String password, final AuthModule eauth)
            throws SaltException {
        Map<String, String> props = new LinkedHashMap<>();
        props.put("username", username);
        props.put("password", password);
        props.put("eauth", eauth.getValue());

        String payload = gson.toJson(props);

        Return<List<Token>> result = connectionFactory
                .create("/login", JsonParser.TOKEN, config)
                .getResult(payload);

        // For whatever reason they return a list of tokens here, take the first
        Token token = result.getResult().get(0);
        config.put(ClientConfig.TOKEN, token.getToken());
        return token;
    }

    /**
     * Asynchronously perform login and return a Future with the token.
     * <p>
     * {@code POST /login}
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @return Future containing the authentication token
     */
    public Future<Token> loginAsync(final String username, final String password,
            final AuthModule eauth) {
        return executor.submit(() -> login(username, password, eauth));
    }

    /**
     * Perform logout and clear the session token from the config.
     * <p>
     * {@code POST /logout}
     *
     * @return true if the logout was successful, otherwise false
     * @throws SaltException if anything goes wrong
     */
    public boolean logout() throws SaltException {
        Return<String> stringResult = connectionFactory
                .create("/logout", JsonParser.STRING, config).getResult("");
        String logoutMessage = "Your token has been cleared";
        boolean result = logoutMessage.equals((stringResult.getResult()));
        if (result) {
            config.remove(ClientConfig.TOKEN);
        }
        return result;
    }

    /**
     * Asynchronously perform logout and clear the session token from the config.
     * <p>
     * {@code POST /logout}
     *
     * @return Future containing a boolean result, true if logout was successful
     */
    public Future<Boolean> logoutAsync() {
        return executor.submit(() -> (Boolean) this.logout());
    }

    /**
     * Query for all minions and immediately return a map of minions keyed by minion id.
     * <p>
     * {@code GET /minions}
     *
     * @return map containing maps representing minions, keyed by minion id
     * @throws SaltException if anything goes wrong
     * @see <a href="http://docs.saltstack.com/en/latest/topics/targeting/grains.html">
     *     Grains</a>
     */
    public Map<String, Map<String, Object>> getMinions() throws SaltException {
        return connectionFactory.create("/minions", JsonParser.RETMAPS, config)
                .getResult().getResult().get(0);
    }

    /**
     * Asynchronously query for all minions and return a map of minions keyed by minion id.
     * <p>
     * {@code GET /minions}
     *
     * @return Future with a map containing maps representing minions, keyed by minion id
     * @throws SaltException if anything goes wrong
     * @see <a href="http://docs.saltstack.com/en/latest/topics/targeting/grains.html">
     *     Grains</a>
     */
    public Future<Map<String, Map<String, Object>>> getMinionsAsync()
            throws SaltException {
        return executor.submit(this::getMinions);
    }

    /**
     * Query for details (grains) of the specified minion.
     * <p>
     * {@code GET /minions/<minion-id>}
     *
     * @param minionId the minion ID
     * @return Map key: grain name, value: grain value
     * @throws SaltException if anything goes wrong
     * @see <a href="http://docs.saltstack.com/en/latest/topics/targeting/grains.html">
     *     Grains</a>
     */
    public Map<String, Object> getMinionDetails(String minionId) throws SaltException {
        return connectionFactory.create("/minions/" + minionId, JsonParser.RETMAPS, config)
                .getResult().getResult().get(0).get(minionId);
    }

    /**
     * Query for details (grains) of the specified minion asynchronously.
     * <p>
     * {@code GET /minions/<minion-id>}
     *
     * @param minionId the minion ID
     * @return Future with a map containing details of the minion
     * @throws SaltException if anything goes wrong
     * @see <a href="http://docs.saltstack.com/en/latest/topics/targeting/grains.html">
     *     Grains</a>
     */
    public Future<Map<String, Object>> getMinionDetailsAsync(final String minionId)
            throws SaltException {
        return executor.submit(() -> getMinionDetails(minionId));
    }

    /**
     * Generic interface to start any execution command and immediately return an object
     * representing the scheduled job.
     * <p>
     * {@code POST /minions}
     *
     * @param <T> type of the tgt property for this command
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return object representing the scheduled job
     * @throws SaltException if anything goes wrong
     */
    public <T> ScheduledJob startCommand(final Target<T> target, final String function,
            List<Object> args, Map<String, Object> kwargs) throws SaltException {
        Map<String, Object> props = new LinkedHashMap<>();
        props.putAll(target.getProps());
        props.put("fun", function);
        props.put("arg", args);
        props.put("kwarg", kwargs);

        String payload = gson.toJson(Collections.singleton(props));

        // Connect to the minions endpoint and send the above lowstate data
        Return<List<ScheduledJob>> result = connectionFactory
                .create("/minions", JsonParser.SCHEDULED_JOB,  config)
                .getResult(payload);

        // They return a list of tokens here, we take the first
        return result.getResult().get(0);
    }

    /**
     * Asynchronously start any execution command and immediately return an object
     * representing the scheduled job.
     * <p>
     * {@code POST /minions}
     *
     * @param <T> type of the tgt property for this command
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return Future containing the scheduled job
     */
    public <T> Future<ScheduledJob> startCommandAsync(final Target<T> target,
            final String function, final List<Object> args,
            final Map<String, Object> kwargs) {
        return executor.submit(() -> startCommand(target, function, args, kwargs));
    }

    /**
     * Query for the result of a supplied job.
     * <p>
     * {@code GET /job/<job-id>}
     *
     * @param job {@link ScheduledJob} object representing scheduled job
     * @return {@link ResultInfoSet} representing result set from minions
     * @throws SaltException if anything goes wrong
     */
    public ResultInfoSet getJobResult(final ScheduledJob job) throws SaltException {
        return getJobResult(job.getJid());
    }

    /**
     * Query for the result of a supplied job.
     * <p>
     * {@code GET /job/<job-id>}
     *
     * @param job String representing scheduled job
     * @return {@link ResultInfoSet} representing result set from minions
     * @throws SaltException if anything goes wrong
     */
    public ResultInfoSet getJobResult(final String job) throws SaltException {
        return connectionFactory
                .create("/jobs/" + job, JsonParser.JOB_RESULTS, config)
                .getResult();
    }

    /**
     * Get previously run jobs.
     * <p>
     * {@code GET /jobs}
     *
     * @return map containing run jobs keyed by job id
     * @throws SaltException if anything goes wrong
     */
    public Map<String, Job> getJobs() throws SaltException {
        Return<List<Map<String, Job>>> result = connectionFactory
                .create("/jobs", JsonParser.JOBS, config)
                .getResult();
        return result.getResult().get(0);
    }

    /**
     * Asynchronously get previously run jobs.
     * <p>
     * {@code GET /jobs}
     *
     * @return Future with a map containing run jobs keyed by job id
     */
    public Future<Map<String, Job>> getJobsAsync() {
        return executor.submit(this::getJobs);
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
     * @throws SaltException if anything goes wrong
     */
    public <T> Map<String, Object> run(final String username, final String password,
            final AuthModule eauth, final String client, final Target<T> target,
            final String function, List<Object> args, Map<String, Object> kwargs)
            throws SaltException {
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

        Return<List<Map<String, Object>>> result = connectionFactory
                .create("/run", JsonParser.RUN_RESULTS, config)
                .getResult(payload);

        // A list with one element is returned, we take the first
        return result.getResult().get(0);
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
     * @throws SaltException if anything goes wrong
     */
    public <T> Map<String, Result<SSHRawResult>> runRawSSHCommand(final String command,
            final Target<T> target, SaltSSHConfig cfg)
        throws SaltException {
        Map<String, Object> props = new HashMap<>();
        props.put("client", Client.SSH.getValue());
        props.putAll(target.getProps());
        props.put("fun", command);
        props.put("raw_shell", true);

        SaltSSHUtils.mapConfigPropsToArgs(cfg, props);

        List<Map<String, Object>> list = Collections.singletonList(props);

        String payload = gson.toJson(list);

        Return<List<Map<String, Result<SSHRawResult>>>> result = connectionFactory
                .create("/run", JsonParser.RUNSSHRAW_RESULTS, config).getResult(payload);

        return result.getResult().get(0);
    }

    /**
     * Asynchronously start any execution command bypassing normal session handling.
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
     * @return Future containing Map key: minion id, value: command result from that minion
     */
    public <T> Future<Map<String, Object>> runAsync(final String username,
            final String password, final AuthModule eauth, final String client,
            final Target<T> target, final String function, final List<Object> args,
            final Map<String, Object> kwargs) {
        return executor.submit(() ->
                run(username, password, eauth, client, target, function, args, kwargs));
    }

    /**
     * Query statistics from the CherryPy Server.
     * <p>
     * {@code GET /stats}
     *
     * @return the stats
     * @throws SaltException if anything goes wrong
     */
    public Stats stats() throws SaltException {
        return connectionFactory.create("/stats", JsonParser.STATS, config).getResult();
    }

    /**
     * Asynchronously query statistics from the CherryPy Server.
     * <p>
     * {@code GET /stats}
     *
     * @return Future containing the stats
     */
    public Future<Stats> statsAsync() {
        return executor.submit(this::stats);
    }

    /**
     * Query general key information.
     * <p>
     * Required permissions: {@code @wheel}
     * <p>
     * {@code GET /keys}
     *
     * @return the keys
     * @throws SaltException if anything goes wrong
     */
    public Key.Names keys() throws SaltException {
        return connectionFactory.create("/keys", JsonParser.KEYS, config).getResult()
                .getResult();
    }

    /**
     * Asynchronously query general key information.
     * <p>
     * Required permissions: {@code @wheel}
     * <p>
     * {@code GET /keys}
     *
     * @return Future containing the keys
     */
    public Future<Key.Names> keysAsync() {
        return executor.submit(this::keys);
    }

    /**
     * Returns a WebSocket @ClientEndpoint annotated object connected
     * to the /ws ServerEndpoint.
     * <p>
     * The stream object supports the {@link WebSocketEventStream} interface which allows the caller
     * to register/unregister for stream event notifications as well as close the event
     * stream.
     * <p>
     * Note: {@link SaltClient#login(String, String, AuthModule)} or
     * {@link SaltClient#loginAsync(String, String, AuthModule)} must be called prior
     * to calling this method.
     * <p>
     * {@code GET /events}
     *
     * @param listeners event listeners to be added before the stream is initialized
     * @return the event stream
     * @throws SaltException in case of an error during websocket stream initialization
     */
    public WebSocketEventStream events(EventListener... listeners) throws SaltException {
        return new WebSocketEventStream(config, listeners);
    }

    /**
     * Trigger an event in Salt with the specified tag and data.
     * <p>
     * {@code POST /hook}
     *
     * @param eventTag the event tag
     * @param eventData the event data. Must be valid JSON.
     * @return the boolean value returned by Salt. If true the event was triggered
     * successfully.
     * A value of false is returned only if Salt itself returns false; it does not mean a
     * communication failure.
     * @throws SaltException if anything goes wrong
     */
    public boolean sendEvent(String eventTag, String eventData) throws SaltException {
        String tag = eventTag != null ? eventTag : "";
        Map<String, Object> result = connectionFactory
                .create("/hook/" + tag, JsonParser.MAP, config)
                .getResult(eventData);
        return Boolean.TRUE.equals(result.get("success"));
    }

    /**
     * Asynchronously trigger an event in Salt with the specified tag and data.
     * <p>
     * {@code POST /hook}
     *
     * @param eventTag the event tag
     * @param eventData the event data. Must be valid JSON.
     * @return Future containing a boolean value indicating the success or failure of
     * triggering the event.
     */
    public Future<Boolean> sendEventAsync(final String eventTag, final String eventData) {
        return executor.submit(() -> { return sendEvent(eventTag, eventData); });
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
     * @throws SaltException if anything goes wrong
     */
    public <R> R call(Call<?> call, Client client, String endpoint, Optional<Map<String,
            Object>> custom, TypeToken<R> type) throws SaltException {
        Map<String, Object> props = new HashMap<>();
        props.putAll(call.getPayload());
        props.put("client", client.getValue());
        custom.ifPresent(props::putAll);

        List<Map<String, Object>> list = Collections.singletonList(props);
        String payload = gson.toJson(list);

        return connectionFactory
                .create(endpoint, new JsonParser<>(type), config)
                .getResult(payload);
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
     * @throws SaltException if anything goes wrong
     */
    public <R> R call(Call<?> call, Client client, String endpoint, TypeToken<R> type)
            throws SaltException {
        return call(call, client, endpoint, Optional.empty(), type);
    }
}
