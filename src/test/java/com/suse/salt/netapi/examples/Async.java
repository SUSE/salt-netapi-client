package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.io.IOException;
import java.net.URI;

/**
 * Example code using HttpAsyncClient.
 */
public class Async {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";

    public static void main(String[] args) {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL));
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        httpclient.start();

        Runnable cleanup = () -> {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Perform a non-blocking login
        client.loginNonBlocking(httpclient, USER, PASSWORD, AuthModule.AUTO)
                .thenAccept(t -> System.out.println("Token -> " + t.getToken()))
                .thenRun(cleanup);
    }
}
