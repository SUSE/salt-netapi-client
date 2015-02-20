package com.suse.saltstack.netapi.utils;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling logger instances and printing parsed messages according to caller method<br/>
 * Logger instances will be created using reflection but the performance effect will be minimum since<br/>
 * instances handled by local cache. <br/>
 * <br/>
 * <b>Usage:</b><br/>
 *       In your class simply call <b><code>LoggingUtils.debug("hello logger");</code></b><br/>
 * Simply edit log4j.properties file from project folder to handle logging level.<br/>
 * @author Burak Sarac, burak at linux dot com, 20/02/2015 00:47
 * 
 * License: Free of use!
 *
 */
public class LogUtils {

	/**
	 * Local cache to hold logger instances instead of Factory call to get rid of additional conditional checks
	 */
	private static final Map<String, Logger> LOGGER_INSTANCES = new HashMap<String, Logger>();
	/**
	 * predefined logger messages
	 */
	private static final String START = "start";
	private static final String END = "end";
	private static final String CONSTRUCTED = "constructed";

	/**
	 * Local caches to check if related logging level activated<br/>
	 * @see http://logging.apache.org/log4j/2.x/performance.html
	 */
	private static boolean isTraceEnabled = false;
	private static boolean isDebugEnabled = false;
	private static boolean isInfoEnabled = false;
	private static boolean isWarnEnabled = false;
	private static boolean isErrorEnabled = false;

	/**
	 * Get logger instance for the caller class
	 * @return {@link Logger}
	 */
	public static final Logger getLogger() {

		return getLoggerInstance(getCallerClassName());
	}

	/**
	 * Prints debug message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 */
	protected static final void debug(Logger logger, String message) {

		if (isDebugEnabled) {
			logger.debug(getCallerMethodName() + "() " + message);
		}
	}

	/**
	 * Prints trace message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 */
	protected static final void trace(Logger logger, String message) {

		if (isTraceEnabled) {
			logger.trace(getCallerMethodName() + "() " + message);
		}
	}

	/**
	 * Prints warn message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 */
	protected static final void warn(Logger logger, String message) {

		if (isWarnEnabled) {
			logger.warn(getCallerMethodName() + "() " + message);
		}
	}

	/**
	 * Prints info message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 */
	protected static final void info(Logger logger, String message) {

		if (isInfoEnabled) {
			logger.info(getCallerMethodName() + "() " + message);
		}
	}

	/**
	 * Prints error message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 */
	protected static final void error(Logger logger, String message) {

		if (isErrorEnabled) {
			logger.error(getCallerMethodName() + "() " + message);
		}
	}

	/**
	 * Prints debug message by given logger<br/>
	 * Caller method name will be appended into starting of message
	 * 
	 * @param logger {@link Logger} instance
	 * @param message {@link String} message
	 * @param throwable {@link Throwable} throwable
	 */
	protected static final void error(Logger logger, String message, Throwable throwable) {

		if (isErrorEnabled) {
			logger.error(getCallerMethodName() + "() " + message, throwable);
		}
	}

	/**
	 * Prints predefined <b>start</b> message by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 */
	public static final void debugStart() {
		debug(getLoggerInstance(getCallerClassName()), START);
	}

	/**
	 * Prints predefined <b>end</b> message by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 */
	public static final void debugEnd() {
		debug(getLoggerInstance(getCallerClassName()), END);
	}

	/**
	 * Prints predefined <b>Constructed</b> message by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 */
	public static final void debugConstructed() {
		debug(getLoggerInstance(getCallerClassName()), CONSTRUCTED);
	}

	/**
	 * Prints given message in debug level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * 
	 */
	public static final void debug(String message) {
		debug(getLoggerInstance(getCallerClassName()), message);
	}

	/**
	 * Prints given message in info level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * 
	 */
	public static final void info(String message) {
		info(getLoggerInstance(getCallerClassName()), message);
	}

	/**
	 * Prints given message in warn level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * 
	 */
	public static final void warn(String message) {
		warn(getLoggerInstance(getCallerClassName()), message);
	}

	/**
	 * Prints given message in error level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * 
	 */
	public static final void error(String message) {
		error(getLoggerInstance(getCallerClassName()), message);
	}

	/**
	 * Prints given message in trace level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * 
	 */
	public static final void trace(String message) {
		trace(getLoggerInstance(getCallerClassName()), message);
	}

	/**
	 * Prints given message in error level by a logger<br/>
	 * instance of caller class. Caller class and method name will be<br/>
	 * appended into message. Logger instance will be created by reflections<br/>
	 * if not created in previous calls.
	 * 
	 * @param message {@link String} message
	 * @param throwable {@link Throwable} throwable
	 * 
	 */
	public static final void error(String message, Throwable throwable) {
		error(getLoggerInstance(getCallerClassName()), message, throwable);
	}

	/**
	 * Returns a new {@link Logger} instance for given class name.<br/>
	 * If logger instance already exist in cache returns existing one
	 * 
	 * @param callerCLass
	 * @return
	 */
	protected final static Logger getLoggerInstance(String callerCLass) {

		//check if we have logger already
		if (LOGGER_INSTANCES.containsKey(callerCLass)) {
			return LOGGER_INSTANCES.get(callerCLass);
		}
		
		//if we reach here we dont have yet, so create one
		Logger logger = LoggerFactory.getLogger(callerCLass);

		//predefine log levels
		isTraceEnabled = logger.isTraceEnabled();
		isDebugEnabled = logger.isDebugEnabled();
		isInfoEnabled = logger.isInfoEnabled();
		isWarnEnabled = logger.isWarnEnabled();
		isErrorEnabled = logger.isErrorEnabled();

		//add into cache
		LOGGER_INSTANCES.put(callerCLass, logger);

		//return
		return logger;

	}

	/**
	 * Returns caller class name using reflections/stacktrace
	 * 
	 * @return {@link String} className
	 */
	protected final static String getCallerClassName() {
		return Thread.currentThread().getStackTrace()[3].getClassName();
	}

	/**
	 * Returns caller method name using reflections/stacktrace
	 * 
	 * @return {@link String} methodname
	 */
	protected final static String getCallerMethodName() {
		return Thread.currentThread().getStackTrace()[4].getMethodName();
	}
}
