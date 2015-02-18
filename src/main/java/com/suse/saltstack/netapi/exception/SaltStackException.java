package com.suse.saltstack.netapi.exception;

import com.suse.saltstack.netapi.client.SaltStackAPIConstants;

/**
 * Exception to be thrown in case of problems with SaltStack.
 */
public class SaltStackException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Predefined exception for invalid URL
	 */
	public static final SaltStackException INVALID_URL_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_URL);

	/**
	 * Predefined exception for invalid PORT
	 */
	public static final SaltStackException INVALID_PORT_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_PORT_RANGE);

	/**
	 * Predefined exception for invalid proxy settings
	 */
	public static final SaltStackException INVALID_PROXY_SETTINGS_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_PROXY_SETTINGS);

	/**
	 * Predefined exception for invalid username
	 */
	public static final SaltStackException INVALID_USERNAME_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_USERNAME);

	/**
	 * Predefined exception for invalid password
	 */
	public static final SaltStackException INVALID_PASSWORD_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_PASSWORD);

	/**
	 * Predefined exception for empty result list
	 */
	public static final SaltStackException INVALID_RESPONSE_EXCEPTION = new SaltStackException(
									SaltStackAPIConstants.INVALID_RESPONSE);
	/**
	 * Constructor expecting a custom cause.
	 *
	 * @param cause
	 *            the cause
	 */
	public SaltStackException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructor expecting a custom message.
	 *
	 * @param message
	 *            the message
	 */
	public SaltStackException(String message) {
		super(message);
	}
	
	/**
	 * Constructor expecting a custom cause.
	 *
	 * @param cause
	 *            the cause
	 */
	public SaltStackException(Throwable cause, String message) {
		super(message, cause);
	}
}
