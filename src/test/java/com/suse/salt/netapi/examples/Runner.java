package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.runner.Event;
import com.suse.salt.netapi.calls.runner.Manage;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Example code calling runner functions.
 */
public class Runner {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";

    public static void main(String[] args) throws SaltException {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL));

        // Send a custom event with some data (salt.runners.event)
        Map<String, Object> data = new HashMap<>();
        data.put("foo", "bar");
        Result<Boolean> result = Event.send("my/custom/event", Optional.of(data))
                .callSync(client, USER, PASSWORD, AuthModule.AUTO);
        System.out.println("event.send: " + result);

        // List all minions that are up (salt.runners.manage)
        Result<List<String>> resultUp = Manage.present()
                .callSync(client, USER, PASSWORD, AuthModule.AUTO);
        System.out.println("manage.present: " + resultUp);
    }
}
