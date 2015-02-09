package com.suse.saltstack.netapi.client;

import com.suse.saltstack.netapi.exception.SaltStackException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

@Ignore("disable tests that require a running netapi instance")
public class SaltStackClientTest {

    private static Properties config = new Properties();
    public static String TESTCONFIG_FILENAME = "/credentials.properties";

    public SaltStackClientTest() {
        InputStream inputStream = getClass().getResourceAsStream(TESTCONFIG_FILENAME);
        if (inputStream != null) {
            try {
                config.load(inputStream);
            } catch (IOException e) {
                System.out.format("unable to read %s", TESTCONFIG_FILENAME);
            }
        } else {
            System.out.format("cannot find %s", TESTCONFIG_FILENAME);
        }
    }

    public String getMastrUrl() {
        return String.format("https://%s", config.getProperty("salt-api-url"));
    }

    @Test
    public void testUrlConstructor() throws SaltStackException {
        SaltStackClient client = new SaltStackClient(getMastrUrl());
        assertTrue("Url based instantiation failed", client != null);
    }

    @Test
    public void testLoginLogout() throws Exception {
        SaltStackClient client = new SaltStackClient(getMastrUrl());
        SaltStackToken token = client.login(config.getProperty("user"), config.getProperty("password"),
                config.getProperty("eauth"));
        assertNotNull("Unable to obtain Token", token);
        assertTrue("No token returned", token.getToken() != null && token.getToken().length() > 0);
        SaltStackStringResult result = client.logout();
        assertEquals("Logout failed", "Your token has been cleared", result.getResult());
        assertNull("Token not cleared", client.getConfig().getToken());
    }

    @Test
    public void testMinions() throws Exception {
        SaltStackClient client = new SaltStackClient(getMastrUrl());
        SaltStackToken token = client.login(config.getProperty("user"), config.getProperty("password"),
                config.getProperty("eauth"));
        SaltStackJob job =  client.minions("*", "status.diskusage", new ArrayList<String>(), new HashMap<String, String>());
        assertNotNull(job);
    }

    @Test
    public void testKeys() throws Exception {
        SaltStackClient client = new SaltStackClient(getMastrUrl());
        SaltStackToken token = client.login(config.getProperty("user"), config.getProperty("password"),
                config.getProperty("eauth"));
        SaltStackKeyResult keyResult =  client.keys("*");
        assertNotNull(keyResult);
        assertNotNull(keyResult.getLocal());
        assertTrue("no local keys found", keyResult.getLocal().size() > 0);
        assertNotNull(keyResult.getMinions());
        assertTrue("no minion keys found", keyResult.getMinions().size() > 0);
    }
}