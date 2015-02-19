package com.suse.saltstack.netapi.client;

import static com.suse.saltstack.netapi.client.SaltStackAPIConstants.*;
import static com.suse.saltstack.netapi.exception.SaltStackException.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suse.saltstack.netapi.config.Password;
import com.suse.saltstack.netapi.config.SaltStackClientConfig;
import com.suse.saltstack.netapi.config.SaltStackProxySettings;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.parser.SaltStackParser;
import com.suse.saltstack.netapi.results.SaltStackJob;
import com.suse.saltstack.netapi.results.SaltStackResult;
import com.suse.saltstack.netapi.results.SaltStackToken;
import com.suse.saltstack.netapi.utils.SaltStackClientUtils;

/**
 * SaltStack API client.
 */
public class SaltStackClient {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	/** The configuration object */
	private final SaltStackClientConfig config = new SaltStackClientConfig();

	/** The connection factory object */
	private SaltStackConnectionFactory connectionFactory;

	/**
	 * Constructor for connecting to a given URL using a specific connection
	 * factory. It will use 80 port and http protocol
	 * 
	 * @param host
	 *            host url <b>localhost</b>
	 * @throws SaltStackException
	 */
	public SaltStackClient(String host) throws SaltStackException {

		this(host, HTTP_PORT, false, new SaltStackHttpClientConnectionFactory());

	}

	/**
	 * Constructor for connecting to a given URL using a specific connection
	 * factory.It will use http protocol
	 * 
	 * @param host
	 *            host url <b>localhost</b>
	 * @param port
	 *            port to connect.
	 * @throws SaltStackException
	 */
	public SaltStackClient(String host, int port) throws SaltStackException {

		this(host, port, false, new SaltStackHttpClientConnectionFactory());

	}

	/**
	 * Constructor for connecting to a given URL using a specific connection
	 * factory.
	 * 
	 * @param host
	 *            host url <b>localhost</b>
	 * @param port
	 *            port to connect.
	 * @param isSsl
	 *            is http or https?
	 * @throws SaltStackException
	 */
	public SaltStackClient(String host, int port, boolean isSsl) throws SaltStackException {

		this(host, port, isSsl, new SaltStackHttpClientConnectionFactory());

	}

	/**
	 * Constructor for connecting to a given URL using a specific connection
	 * factory.
	 * 
	 * @param host
	 *            host url <b>localhost</b>
	 * @param port
	 *            port to connect.
	 * @param isSsl
	 *            http or https
	 * @param connectionFactory
	 *            connection factory
	 * @throws SaltStackException
	 */
	public SaltStackClient(String host, int port, boolean isSsl,
									SaltStackConnectionFactory connectionFactory)
									throws SaltStackException {
		if (logger.isTraceEnabled()) {
			logger.trace(this.getClass().getSimpleName() + "#Constructor start");
		}
		// check host empty or null
		if (StringUtils.isEmpty(host)) {
			throw INVALID_URL_EXCEPTION;
		}

		// check port range
		if (port < 0 || port > 65536) {
			throw INVALID_PORT_EXCEPTION;
		}

		// build url
		StringBuilder url = new StringBuilder(isSsl ? HTTPS : HTTP);

		url.append(host).append(":").append(port);
		try {
			// Put the URL in the config
			config.setUrl(url.toString());
		} catch (URISyntaxException e) {
			throw new SaltStackException(e);
		}

		this.connectionFactory = connectionFactory;

		if (logger.isTraceEnabled()) {
			logger.trace(CLIENT_INITIALIZED + url.toString());
		}
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
	 * @param settings
	 *            proxy settings
	 * @throws SaltStackException
	 */
	public void setProxy(SaltStackProxySettings settings) throws SaltStackException {
		if (logger.isTraceEnabled()) {
			logger.trace(this.getClass().getSimpleName() + "#setProxy start");
		}
		// check hostName
		if (StringUtils.isEmpty(settings.getHostname())) {
			throw INVALID_PROXY_SETTINGS_EXCEPTION;
		}

		// set into config
		config.put(SaltStackClientConfig.PROXY_HOSTNAME, settings.getHostname());
		config.put(SaltStackClientConfig.PROXY_PORT, String.valueOf(settings.getPort()));

		// check username
		if (StringUtils.isEmpty(settings.getUsername())) {
			throw INVALID_PROXY_SETTINGS_EXCEPTION;
		}

		// set into config
		config.put(SaltStackClientConfig.PROXY_USERNAME, settings.getUsername());

		// set password
		config.put(SaltStackClientConfig.PROXY_PASSWORD, settings.getPassword().getValue());

		if (logger.isTraceEnabled()) {
			logger.trace(PROXY_SETTINGS_ADDED + settings.getHostname() + "/"
											+ settings.getUsername());
		}
	}

	/**
	 * Perform login and return the token.
	 *
	 * POST /login
	 *
	 * @return authentication token as {@link SaltStackToken}
	 * @throws SaltStackException
	 *             if anything goes wrong
	 */
	public SaltStackToken login(String username, Password password)
									throws SaltStackException {
		return login(username, password, LOGIN_EAUTH_AUTO);
	}

	/**
	 * Perform login and return the token. Allows specifying the eauth
	 * parameter.
	 *
	 * POST /login
	 *
	 * @return authentication token as {@link SaltStackToken}
	 * @throws SaltStackException
	 *             if anything goes wrong
	 */
	public SaltStackToken login(String username, Password password, String eauth)
									throws SaltStackException {

		if (logger.isTraceEnabled()) {
			logger.trace(this.getClass().getSimpleName() + "#login start");
		}

		// check username if empty
		if (StringUtils.isEmpty(username)) {
			throw INVALID_USERNAME_EXCEPTION;
		}

		// check password if empty
		if (password.getValue().length < 1) {
			throw INVALID_PASSWORD_EXCEPTION;
		}

		// parse json object
		JsonObject json = new JsonObject();
		json.addProperty(USER_NAME, username);
		json.addProperty(PASS_WORD, String.valueOf(password.getValue()));
		json.addProperty(SaltStackAPIConstants.E_AUTH, eauth);

		SaltStackResult<List<SaltStackToken>> result;

		// try to create connection
		try {
			result = connectionFactory.create(SaltStackAPIConstants.LOGIN,
											SaltStackParser.TOKEN, config).getResult(
											json.toString());
		} catch (Exception e) {
			throw new SaltStackException(e, COULDNT_CONNECT);
		}

		// TODO BSARAC finally destroy connection if not closed

		// For whatever reason they return a list of tokens here, take the first
		SaltStackToken token = result.getResult().get(0);
		config.put(SaltStackClientConfig.TOKEN, token.getToken());

		if (logger.isTraceEnabled()) {
			logger.trace(SaltStackAPIConstants.LOGGED_IN + username);
		}
		return token;
	}

	/**
	 * Perform logout and clear the session token from the config.
	 *
	 * POST /logout
	 *
	 * @throws SaltStackException
	 *             if anything goes wrong
	 */
	public SaltStackResult<String> logout() throws SaltStackException {
		SaltStackResult<String> result;
		try {
			result = connectionFactory.create(SaltStackAPIConstants.LOGOUT,
											SaltStackParser.STRING, config).getResult(null);
		} catch (Exception e) {
			throw new SaltStackException(e, COULDNT_CONNECT);
		}
		config.remove(SaltStackClientConfig.TOKEN);
		// TODO BSARAC add destroy method for connections or queue handler
		if (logger.isTraceEnabled()) {
			logger.trace(LOGGED_OUT);
		}
		return result;
	}

	/**
	 * Generic interface to start any execution command and immediately return
	 * the job id.
	 *
	 * POST /minions
	 *
	 * @param target
	 *            the target
	 * @param function
	 *            the function to execute
	 * @param args
	 *            list of non-keyword arguments
	 * @param kwargs
	 *            map containing keyword arguments
	 * @return object representing the scheduled job
	 * @throws SaltStackException
	 *             if anything goes wrong
	 */
	public SaltStackJob startCommand(String target, String function, List<String> args,
									Map<String, String> kwargs) throws SaltStackException {

		if (logger.isTraceEnabled()) {
			logger.trace(this.getClass().getSimpleName() + "#startCommand start");
		}
		// Setup lowstate data to send as JSON
		JsonObject json = new JsonObject();
		json.addProperty(TGT, target);
		json.addProperty(FUN, function);

		if (StringUtils.isEmpty(target)) {
			throw new SaltStackException(INVALID_TARGET);
		}

		if (StringUtils.isEmpty(function)) {
			throw new SaltStackException(INVALID_FUNCTION);
		}
		// Non-keyword arguments
		if (!CollectionUtils.isEmpty(args)) {

			json.add(ARG, SaltStackClientUtils.parseJsonArray(args));
		}
		// Keyword arguments
		if (!MapUtils.isEmpty(kwargs)) {
			for (String key : kwargs.keySet()) {
				json.addProperty(key, kwargs.get(key));
			}
		}

		JsonArray jsonArray = new JsonArray();
		jsonArray.add(json);

		// Connect to the minions endpoint and send the above lowstate data
		SaltStackResult<List<SaltStackJob>> result;

		try {
			result = connectionFactory.create(MINIONS, SaltStackParser.JOB, config)
											.getResult(jsonArray.toString());
		} catch (Exception e) {
			throw new SaltStackException(e, COULDNT_CONNECT);
		}

		// TODO bsarac add finally block to close connection if opened

		if (result == null || result.getResult().isEmpty()) {
			throw INVALID_RESPONSE_EXCEPTION;
		}

		if (logger.isTraceEnabled()) {
			logger.trace(COMMAND_EXECUTED);
		}
		// They return a list of tokens here, we take the first
		return result.getResult().get(0);
	}
}
