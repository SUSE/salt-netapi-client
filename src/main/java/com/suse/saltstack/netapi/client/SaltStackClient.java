package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.config.SaltStackProxySettings;
import com.suse.saltstack.netapi.exception.SaltStackException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * SaltStack API client.
 */
public class SaltStackClient {

    /** The configuration object */
    private final SaltStackClientConfig config = new SaltStackClientConfig();

    /** The connection factory object */
    private SaltStackConnectionFactory connectionFactory;

    /**
     * Constructor for connecting to a given URL.
     *
     * @param url the SaltStack URL
     * @throws SaltStackException
     */
    public SaltStackClient(String url) throws SaltStackException {
        this(url, new SaltStackJDKConnectionFactory());
    }

    /**
     * Constructor for connecting to a given URL using a specific connection factory.
     *
     * @param url the SaltStack URL
     * @param connectionFactory Connection Factory implementation
     * @throws SaltStackException
     */
    public SaltStackClient(String url, SaltStackConnectionFactory connectionFactory)
            throws SaltStackException {
        // Put the URL in the config
        if (url != null) {
            try {
                config.setUrl(url);
            } catch (URISyntaxException e) {
                throw new SaltStackException(e);
            }
        }
        this.connectionFactory = connectionFactory;
    }

    /**
     * Directly access the configuration.
     *
     * @return the configuration object
     */
    public SaltStackClientConfig getConfig() {
        return config;
    }

    /**
     * Configure to use a proxy when connecting to the SaltStack API.
     *
     * @param settings proxy settings
     */
    public void setProxy(SaltStackProxySettings settings) {
        if (settings.getHostname() != null) {
            config.put(SaltStackClientConfig.PROXY_HOSTNAME, settings.getHostname());
            config.put(SaltStackClientConfig.PROXY_PORT, String.valueOf(settings.getPort()));
        }
        if (settings.getUsername() != null) {
            config.put(SaltStackClientConfig.PROXY_USERNAME, settings.getUsername());
            if (settings.getPassword() != null) {
                config.put(SaltStackClientConfig.PROXY_PASSWORD, settings.getPassword());
            }
        }
    }

    /**
     * Perform login and return the token.
     *
     * POST /login
     *
     * @return authentication token as {@link SaltStackToken}
     * @throws SaltStackException if anything goes wrong
     */
    public SaltStackToken login(String username, String password)
            throws SaltStackException {
        return login(username, password, SaltStackAPIConstants.LOGIN_EAUTH_AUTO);
    }

    /**
     * Perform login and return the token. Allows specifying the eauth parameter.
     *
     * POST /login
     *
     * @return authentication token as {@link SaltStackToken}
     * @throws SaltStackException if anything goes wrong
     */
    public SaltStackToken login(String username, String password, String eauth)
            throws SaltStackException {
        JsonObject json = new JsonObject();
        json.addProperty("username", username);
        json.addProperty("password", password);
        json.addProperty("eauth", eauth);
        SaltStackTokenResult result = connectionFactory.create("/login", config).
                getResult(SaltStackTokenResult.class, json.toString());

        // For whatever reason they return a list of tokens here, take the first
        SaltStackToken token = result.getResult().get(0);
        config.put(SaltStackClientConfig.TOKEN, token.getToken());
        return token;
    }

    /**
     * Perform logout and clear the session token from the config.
     *
     * POST /logout
     *
     * @throws SaltStackException if anything goes wrong
     */
    public SaltStackStringResult logout() throws SaltStackException {
        SaltStackStringResult result = connectionFactory.create("/logout", config).
                getResult(SaltStackStringResult.class, null);
        config.remove(SaltStackClientConfig.TOKEN);
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
    public SaltStackJob minions(String target, String function, List<String> args,
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
        SaltStackJobResult result = connectionFactory.create("/minions", config).
                getResult(SaltStackJobResult.class, jsonArray.toString());

        // They return a list of tokens here, we take the first
        return result.getJobs().get(0);
    }
}
