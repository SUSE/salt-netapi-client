package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.ClientConfig;
import static com.suse.saltstack.netapi.config.ClientConfig.*;
import com.suse.saltstack.netapi.config.ProxySettings;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.JsonParser;
import com.suse.saltstack.netapi.results.Job;
import com.suse.saltstack.netapi.results.Result;
import com.suse.saltstack.netapi.results.Token;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * SaltStack API client.
 */
public class SaltStackClient {

    /** The configuration object */
    private final ClientConfig config = new ClientConfig();

    /** The connection factory object */
    private ConnectionFactory connectionFactory;

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
        // Put the URL in the config
        config.put(URL, url);
        this.connectionFactory = connectionFactory;
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
            config.put(PROXY_HOSTNAME, settings.getHostname());
            config.put(PROXY_PORT, settings.getPort());
        }
        if (settings.getUsername() != null) {
            config.put(PROXY_USERNAME, settings.getUsername());
            if (settings.getPassword() != null) {
                config.put(PROXY_PASSWORD, settings.getPassword());
            }
        }
    }

    /**
     * Perform login and return the token.
     *
     * POST /login
     *
     * @return authentication token as {@link Token}
     * @throws SaltStackException if anything goes wrong
     */
    public Token login(String username, String password)
            throws SaltStackException {
        return login(username, password, APIConstants.LOGIN_EAUTH_AUTO);
    }

    /**
     * Perform login and return the token. Allows specifying the eauth parameter.
     *
     * POST /login
     *
     * @return authentication token as {@link Token}
     * @throws SaltStackException if anything goes wrong
     */
    public Token login(String username, String password, String eauth)
            throws SaltStackException {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("eauth", eauth);
        Result<List<Token>> result = connectionFactory
                .create("/login", JsonParser.TOKEN, config).getResult(json.toString());

        // For whatever reason they return a list of tokens here, take the first
        Token token = result.getResult().get(0);
        config.put(TOKEN, token.getToken());
        return token;
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
        config.remove(TOKEN);
        return result;
    }

    /**
     * Generic interface to start any execution command and immediately return the job id.
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
    public Job startCommand(String target, String function, List<String> args,
            Map<String, String> kwargs) throws SaltStackException {
        // Setup lowstate data to send as JSON
        JsonObject json = new JsonObject();
        json.addProperty("tgt", target);
        json.addProperty("fun", function);
        // Non-keyword arguments
        if (args != null) {
            JsonArray argsArray = new JsonArray();
            for (String arg : args) {
                argsArray.add(new JsonPrimitive(arg));
            }
            json.add("arg", argsArray);
        }
        // Keyword arguments
        if (kwargs != null) {
            for (String key : kwargs.keySet()) {
                json.addProperty(key, kwargs.get(key));
            }
        }
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(json);

        // Connect to the minions endpoint and send the above lowstate data
        Result<List<Job>> result = connectionFactory
                .create("/minions", JsonParser.JOB,  config).getResult(jsonArray.toString());

        // They return a list of tokens here, we take the first
        return result.getResult().get(0);
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
    public Map<String,Object> run(String username, String password, String eauth,
            String client, String target, String function, List<String> args, Map<String,
            String> kwargs) throws SaltStackException {

        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("eauth", eauth);
        json.addProperty("client", client);
        json.addProperty("tgt", target);
        json.addProperty("fun", function);

        // Non-keyword arguments
        if (args != null) {
            JsonArray argsArray = new JsonArray();
            for (String arg : args) {
                argsArray.add(new JsonPrimitive(arg));
            }
            json.add("arg", argsArray);
        }

        // Keyword arguments
        if (kwargs != null) {
            for (String key : kwargs.keySet()) {
                json.addProperty(key, kwargs.get(key));
            }
        }

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(json);

        Result<List<Map<String,Object>>> result = connectionFactory
                .create("/run", JsonParser.RETVALS, config)
                .getResult(jsonArray.toString());

        // A list with one element is returned, we take the first
        return result.getResult().get(0);
    }
}
