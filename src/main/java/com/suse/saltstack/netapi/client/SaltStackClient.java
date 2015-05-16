package com.suse.saltstack.netapi.client;

import com.google.gson.JsonArray;
import com.suse.saltstack.netapi.AuthModule;
import com.suse.saltstack.netapi.client.impl.HttpClientConnectionFactory;
import com.suse.saltstack.netapi.config.ClientConfig;
import com.suse.saltstack.netapi.config.ProxySettings;
import com.suse.saltstack.netapi.datatypes.Job;
import com.suse.saltstack.netapi.datatypes.Keys;
import com.suse.saltstack.netapi.datatypes.ScheduledJob;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.datatypes.cherrypy.Stats;
import com.suse.saltstack.netapi.event.EventStream;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.JsonParser;
import com.suse.saltstack.netapi.results.Result;
import com.suse.saltstack.netapi.utils.ClientUtils;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * SaltStack API client.
 */
public class SaltStackClient {

    /** The configuration object */
    private final ClientConfig config = new ClientConfig();

    /** The connection factory object */
    private final ConnectionFactory connectionFactory;

    /** The executor for async operations */
    private final ExecutorService executor;

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the SaltStack URL
     */
    public SaltStackClient(URI url) {
        this(url, new HttpClientConnectionFactory());
    }

    /**
     * Constructor for connecting to a given URL using a specific connection factory.
     *
     * @param url the SaltStack URL
     * @param connectionFactory Connection Factory implementation
     */
    public SaltStackClient(URI url, ConnectionFactory connectionFactory) {
        this(url, connectionFactory, Executors.newCachedThreadPool());
    }

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the SaltStack URL
     * @param executor Executor for async operations
     */
    public SaltStackClient(URI url, ExecutorService executor) {
        this(url, new HttpClientConnectionFactory(), executor);
    }

    /**
     * Constructor for connecting to a given URL using a specific connection factory.
     *
     * @param url the SaltStack URL
     * @param connectionFactory Connection Factory implementation
     * @param executor Executor for async operations
     */
    public SaltStackClient(URI url, ConnectionFactory connectionFactory,
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
     * Configure to use a proxy when connecting to the SaltStack API.
     *
     * @param settings proxy settings
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
     * Perform login and return the token. Allows specifying the eauth parameter.
     *
     * POST /login
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @return authentication token as {@link Token}
     * @throws SaltStackException if anything goes wrong
     */
    public Token login(final String username, final String password, final AuthModule eauth)
            throws SaltStackException {
        Map<String, String> props = new LinkedHashMap<String, String>() {
            {
                put("username", username);
                put("password", password);
                put("eauth", eauth.getValue());
            }
        };
        Result<List<Token>> result = connectionFactory
                .create("/login", JsonParser.TOKEN, config)
                .getResult(ClientUtils.makeJsonData(props, null, null).toString());

        // For whatever reason they return a list of tokens here, take the first
        Token token = result.getResult().get(0);
        config.put(ClientConfig.TOKEN, token.getToken());
        return token;
    }

    /**
     * Asynchronously perform login and return a Future with the token.
     * Allows specifying the eauth parameter.
     *
     * POST /login
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @return Future containing an authentication token as {@link Token}
     */
    public Future<Token> loginAsync(final String username, final String password,
            final AuthModule eauth) {
        Callable<Token> callable = new Callable<Token>() {
            @Override
            public Token call() throws SaltStackException {
                return login(username, password, eauth);
            }
        };
        return executor.submit(callable);
    }

    /**
     * Perform logout and clear the session token from the config.
     *
     * POST /logout
     *
     * @throws SaltStackException if anything goes wrong
     */
    public Result<String> logout() throws SaltStackException {
        Result<String> result = connectionFactory
                .create("/logout", JsonParser.STRING, config).getResult(null);
        config.remove(ClientConfig.TOKEN);
        return result;
    }

    /**
     * Asynchronously perform logout and clear the session token from the config.
     *
     * POST /logout
     *
     */
    public Future<Result<String>> logoutAsync() {
        Callable<Result<String>> callable = new Callable<Result<String>>() {
            @Override
            public Result<String> call() throws SaltStackException {
                return logout();
            }
        };
        return executor.submit(callable);
    }

    /**
     * Query for all minions and immediately return a map of minions
     * keyed by minion id
     *
     * GET /minions
     *
     * @return map containing maps representing minions, keyed by minion id
     * @throws SaltStackException if anything goes wrong
     */
    public Map<String, Map<String, Object>> getMinions() throws SaltStackException {
        return connectionFactory.create("/minions", JsonParser.RETMAPS, config)
                .getResult().getResult().get(0);
    }

    /**
     * Generic interface to start any execution command and immediately return an object
     * representing the scheduled job.
     *
     * POST /minions
     *
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return object representing the scheduled job
     * @throws SaltStackException if anything goes wrong
     */
    public ScheduledJob startCommand(final String target, final String function,
            List<String> args, Map<String, String> kwargs) throws SaltStackException {
        Map<String, String> props = new LinkedHashMap<String, String>() {
            {
                put("tgt", target);
                put("fun", function);
            }
        };

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(ClientUtils.makeJsonData(props, kwargs, args));

        // Connect to the minions endpoint and send the above lowstate data
        Result<List<ScheduledJob>> result = connectionFactory
                .create("/minions", JsonParser.SCHEDULED_JOB,  config)
                .getResult(jsonArray.toString());

        // They return a list of tokens here, we take the first
        return result.getResult().get(0);
    }

    /**
     * Asynchronously start any execution command and immediately return an object
     * representing the scheduled job.
     *
     * POST /minions
     *
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return Future containing the scheduled job {@link ScheduledJob}
     */
    public Future<ScheduledJob> startCommandAsync(final String target,
            final String function, final List<String> args,
            final Map<String, String> kwargs) {
        Callable<ScheduledJob> callable = new Callable<ScheduledJob>() {
            @Override
            public ScheduledJob call() throws SaltStackException {
                return startCommand(target, function, args, kwargs);
            }
        };
        return executor.submit(callable);
    }

    /**
     * Query for result of supplied job.
     *
     * GET /job/<job-id>
     *
     * @param job {@link ScheduledJob} object representing scheduled job
     * @return Map key: minion id, value: command result from that minion
     * @throws SaltStackException if anything goes wrong
     */
    public Map<String, Object> getJobResult(final ScheduledJob job)
            throws SaltStackException {
        return getJobResult(job.getJid());
    }

    /**
     * Query for result of supplied job.
     *
     * GET /job/<job-id>
     *
     * @param job String representing scheduled job
     * @return Map key: minion id, value: command result from that minion
     * @throws SaltStackException if anything goes wrong
     */
    public Map<String, Object> getJobResult(final String job) throws SaltStackException {
        Result<List<Map<String, Object>>> result = connectionFactory
                .create("/jobs/" + job, JsonParser.RETVALS, config)
                .getResult();

        // A list with one element is returned, we take the first
        return result.getResult().get(0);
    }

    /**
     * Get previously run jobs.
     * @return map containing run jobs keyed by job id.
     * @throws SaltStackException if anything goes wrong
     */
    public Map<String, Job> getJobs() throws SaltStackException {
        Result<List<Map<String, Job>>> result = connectionFactory
                .create("/jobs", JsonParser.JOBS, config)
                .getResult();
        return result.getResult().get(0);
    }

    /**
     * Get previously run jobs.
     * @return future with a map containing run jobs keyed by job id.
     */
    public Future<Map<String, Job>> getJobsAsync() {
        Callable<Map<String, Job>> callable = new Callable<Map<String, Job>>() {
            @Override
            public Map<String, Job> call() throws SaltStackException {
                return getJobs();
            }
        };
        return executor.submit(callable);
    }

    /**
     * Generic interface to start any execution command bypassing normal session handling.
     *
     * POST /run
     *
     * @param username the username
     * @param password the password
     * @param eauth the eauth type
     * @param client the client
     * @param target the target
     * @param function the function to execute
     * @param args list of non-keyword arguments
     * @param kwargs map containing keyword arguments
     * @return Map key: minion id, value: command result from that minion
     * @throws SaltStackException if anything goes wrong
     */
    public Map<String, Object> run(final String username, final String password,
            final AuthModule eauth, final String client, final String target,
            final String function, List<String> args, Map<String, String> kwargs)
            throws SaltStackException {
        Map<String, String> props = new LinkedHashMap<String, String>() {
            {
                put("username", username);
                put("password", password);
                put("eauth", eauth.getValue());
                put("client", client);
                put("tgt", target);
                put("fun", function);
            }
        };

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(ClientUtils.makeJsonData(props, kwargs, args));

        Result<List<Map<String, Object>>> result = connectionFactory
                .create("/run", JsonParser.RETVALS, config)
                .getResult(jsonArray.toString());

        // A list with one element is returned, we take the first
        return result.getResult().get(0);
    }

    /**
     * Asynchronously start any execution command bypassing normal session handling.
     *
     * POST /run
     *
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
    public Future<Map<String, Object>> runAsync(final String username,
            final String password, final AuthModule eauth, final String client,
            final String target, final String function, final List<String> args,
            final Map<String, String> kwargs) {
        Callable<Map<String, Object>> callable = new Callable<Map<String, Object>>() {
            @Override
            public Map<String, Object> call() throws SaltStackException {
                return run(username, password, eauth, client,
                        target, function, args, kwargs);
            }
        };
        return executor.submit(callable);
    }

    /**
     * Query statistics from the CherryPy Server.
     *
     * GET /stats
     *
     * @return The {@link Stats} object.
     * @throws SaltStackException if anything goes wrong
     */
    public Stats stats() throws SaltStackException {
        return connectionFactory.create("/stats", JsonParser.STATS, config).getResult();
    }

    /**
     * Asynchronously query statistics from the CherryPy Server.
     *
     * GET /stats
     *
     * @return Future containing the {@link Stats} object.
     */
    public Future<Stats> statsAsync() {
        Callable<Stats> callable = new Callable<Stats>() {
            @Override
            public Stats call() throws SaltStackException {
                return stats();
            }
        };
        return executor.submit(callable);
    }

    /**
     * Query general key information.
     *
     * Required permissions: @wheel
     *
     * GET /keys
     *
     * @return The {@link Keys} object.
     * @throws SaltStackException if anything goes wrong
     */
    public Keys keys() throws SaltStackException {
        return connectionFactory.create("/keys", JsonParser.KEYS, config).getResult()
                .getResult();
    }

    /**
     * Asynchronously query general key information.
     *
     * Required permissions: @wheel
     *
     * GET /keys
     *
     * @return Future containing the {@link Keys} object.
     */
    public Future<Keys> keysAsync() {
        Callable<Keys> callable = new Callable<Keys>() {
            @Override
            public Keys call() throws SaltStackException {
                return keys();
            }
        };
        return executor.submit(callable);
    }

    /**
     * Returns a server-sent events (SSE) stream object.  The stream object supports the
     * {@link EventStream} interface which allows the caller to register/unregister
     * for stream event notifications as well as close the event stream.
     * Note: {@link SaltStackClient#login(String, String, AuthModule)} or
     * {@link SaltStackClient#loginAsync(String, String, AuthModule)} must be called prior
     * to calling this method.
     *
     * GET /events
     *
     * @return {@link EventStream} object
     */
    public EventStream events() {
        return new EventStream(config);
    }

    /**
     * Trigger an event in Salt with the specified tag and data.
     *
     * POST /hook
     *
     * @param eventTag the event tag
     * @param eventData the event data. Must be valid JSON.
     * @return the boolean value returned by Salt. If true the event was triggered
     * successfully.
     * A value of false is returned only if Salt itself returns false; it does not mean a
     * communication failure.
     * @throws SaltStackException if anything goes wrong
     */
    public boolean sendEvent(String eventTag, String eventData) throws SaltStackException {
        String tag = eventTag != null ? eventTag : "";
        Map<String, Object> result = connectionFactory
                .create("/hook/" + tag, JsonParser.MAP, config)
                .getResult(eventData);
        return Boolean.TRUE.equals(result.get("success"));
    }

    /**
     * Asynchronously trigger an event in Salt with the specified tag and data.
     *
     * POST /hook
     *
     * @param eventTag the event tag
     * @param eventData the event data. Must be valid JSON.
     * @return Future containing a boolean value indicating the success of failure of the
     * event triggering .
     */
    public Future<Boolean> sendEventAsync(final String eventTag, final String eventData) {
        Callable<Boolean> callable = new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return sendEvent(eventTag, eventData);
            }
        };
        return executor.submit(callable);
    }
}
