package com.suse.saltstack.netapi.config;

import com.suse.saltstack.netapi.client.SaltStackClient;

/**
 * Class representing proxy settings to be used with {@link SaltStackClient}.
 */
public class ProxySettings {

    private String hostname;
    private String username;
    private String password;
    private int port;

    /**
     * Basic constructor taking proxy hostname and port.
     *
     * @param hostname proxy hostname
     * @param port proxy port
     */
    public ProxySettings(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * Extended constructor supporting proxies with authentication.
     *
     * @param hostname proxy hostname
     * @param port proxy port
     * @param username proxy username
     * @param password proxy password
     */
    public ProxySettings(String hostname, int port,
            String username, String password) {
        this(hostname, port);
        this.username = username;
        this.password = password;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the proxy port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
