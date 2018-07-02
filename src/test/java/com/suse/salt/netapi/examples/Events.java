package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientConnection;
import com.suse.salt.netapi.datatypes.Event;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.event.EventListener;
import com.suse.salt.netapi.event.EventStream;
import com.suse.salt.netapi.utils.TestUtils;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import java.net.URI;
import javax.websocket.CloseReason;

/**
 * Example code listening for events on salt's event bus.
 */
public class Events {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";

    public static void main(String[] args) {
        // Init client and set the timeout to infinite
        SaltClient client = new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientConnection(TestUtils.defaultClient()));

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(0)
                .setConnectTimeout(0)
                .setSocketTimeout(0)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        CloseableHttpAsyncClient asyncHttpClient = httpClientBuilder.build();
        asyncHttpClient.start();

        try {
            // Get a login token
            Token token = client.login(USER, PASSWORD, AuthModule.AUTO).toCompletableFuture().join();
            System.out.println("Token: " + token.getToken());

            // Init the event stream with a basic listener implementation
            EventStream eventStream = client.events(
                    token, 0, 0, 0,
                    new EventListener() {
                        @Override
                        public void notify(Event e) {
                            System.out.println("Tag  -> " + e.getTag());
                            System.out.println("Data -> " + e.getData());
                        }

                        @Override
                        public void eventStreamClosed(CloseReason closeReason) {
                            System.out.println("Event stream closed: " +
                                    closeReason.getReasonPhrase());
                        }
                    });

            // Wait for events and close the event stream after 30 seconds
            System.out.println("-- Waiting for events --");
            Thread.sleep(30000);
            eventStream.close();
            System.out.println("-- Stop waiting for events --");
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
    }
}
