package com.suse.saltstack.netapi.client;

/**
 * Constants used in SaltStack API.
 */
public interface SaltStackAPIConstants {
	/** Known values for 'eauth' parameter when logging in */
	String LOGIN_EAUTH_AUTO = "auto";
	String LOGIN_EAUTH_PAM = "pam";

	String TGT = "tgt";
	String FUN = "fun";
	String ARG = "arg";

	int HTTP_PORT = 80;
	String HTTP = "http://";
	String HTTPS = "https://";
	String USER_NAME = "username";
	String PASS_WORD = "password";
	String E_AUTH = "eauth";
	String LOGIN = "/login";
	String LOGOUT = "/logout";
	String MINIONS = "/minions";

	/**
	 * Logger messages
	 */
	String CLIENT_INITIALIZED = "Stack client initialized url:";
	String PROXY_SETTINGS_ADDED = "Proxy settings are added for client";
	String INVALID_PORT_RANGE = "Invalid port entered!";
	String INVALID_URL = "Invalid url!";
	String INVALID_PROXY_SETTINGS = "Invalid proxy settings!";
	String INVALID_USERNAME = "Invalid username!";
	String INVALID_PASSWORD = "Invalid password!";
	String INVALID_TARGET = "Invalid target!";
	String INVALID_RESPONSE = "No response received!";
	String INVALID_FUNCTION = "Invalid function!";
	String LOGGED_IN = "User has logged in!";
	String LOGGED_OUT = "User has logged out!";
	String COMMAND_EXECUTED = "Command(s) succesfully executed";
	String COULDNT_CONNECT = "System couldnt create connection";
}
