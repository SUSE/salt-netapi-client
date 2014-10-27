package com.suse.saltstack.netapi.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * SaltStack client configuration wrapper class.
 */
public class SaltStackClientConfig {

    // Valid keys
    public static final String TOKEN = "token";
    public static final String URL = "url";

    // Proxy settings
    public static final String PROXY_HOSTNAME = "proxy-hostname";
    public static final String PROXY_PORT = "proxy-port";
    public static final String PROXY_USERNAME = "proxy-username";
    public static final String PROXY_PASSWORD = "proxy-password";

    // Default values
    protected static final String DEFAULT_URL = "http://localhost:8000";
    private static final String DEFAULT_PROXY_PORT = "3128";

    // The properties object
    private final Properties properties;

    /**
     * Default constructor.
     */
    public SaltStackClientConfig() {
        this.properties = new Properties();
    }

    /**
     * Sets a preference given by key and value. Use one of the public key strings above.
     * NOTE: To set the URL, use method setURL instead.
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        // The URL key should be treated differently.
        if (key.equals(SaltStackClientConfig.URL)) {
            try {
                this.setUrl(key);
            } catch (URISyntaxException ex) {
                // Complain to the log for invalid URL.
                throw new RuntimeException(ex);
            }
        }
        else {
            properties.setProperty(key, value);
        }
    }

    /**
     * Removes a preference given by key.
     *
     * @param key
     */
    public void remove(String key) {
        if (properties.containsKey(key)) {
            properties.remove(key);
        }
    }

    /**
     * Set the URL parameter.
     *
     * @param url
     * @throws URISyntaxException
     */
    public void setUrl(String url) throws URISyntaxException {
        properties.setProperty(URL, new URI(url).toASCIIString());
    }

    /**
     * Returns the currently used URL.
     *
     * @return url
     */
    public String getUrl() {
        return properties.getProperty(URL, DEFAULT_URL);
    }

    /**
     * Returns the proxy hostname or null.
     *
     * @return proxy hostname
     */
    public String getProxyHostname() {
        return properties.getProperty(PROXY_HOSTNAME, null);
    }

     /**
     * Returns the configured proxy port (or 3128 as the default).
     *
     * @return proxy port
     */
    public String getProxyPort() {
        return properties.getProperty(PROXY_PORT, DEFAULT_PROXY_PORT);
    }

    /**
     * Returns the proxy username or null.
     *
     * @return proxy username
     */
    public String getProxyUsername() {
        return properties.getProperty(PROXY_USERNAME, null);
    }

    /**
     * Returns the proxy password or null.
     *
     * @return proxy password
     */
    public String getProxyPassword() {
       return properties.getProperty(PROXY_PASSWORD, null);
    }

    /**
     * Returns the session token or null.
     *
     * @return session token
     */
    public String getToken() {
       return properties.getProperty(TOKEN, null);
    }
}
