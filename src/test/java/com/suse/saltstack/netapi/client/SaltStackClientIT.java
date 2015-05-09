package com.suse.saltstack.netapi.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.suse.saltstack.netapi.AuthModule;
import com.suse.saltstack.netapi.datatypes.ScheduledJob;
import com.suse.saltstack.netapi.datatypes.Token;
import com.suse.saltstack.netapi.exception.SaltStackException;
import com.suse.saltstack.netapi.results.Result;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by matei2 on 5/9/2015.
 */
public class SaltStackClientIT {

    SaltStackClient client;

    @Before
    public void setUp() {
        URI uri = URI.create("https://localhost:8000");
        client = new SaltStackClient(uri);

    }

    @Test
    public void test() throws SaltStackException {

        Token token = client.login("saltuser", "salt", AuthModule.PAM);

        ScheduledJob job = client.startCommand("*", "test.ping", null, null);
        Map<String, Object> result = client.getJobResult(job);

        System.out.println(result);
    }

    @Test
    public void testHook() throws SaltStackException {
        Token token = client.login("saltuser", "salt", AuthModule.PAM);

        JsonObject obj = new JsonObject();
        obj.addProperty("foo", "bar");
        obj.addProperty("boolean", true);

        boolean success = client.hook(null, obj.toString());

        System.out.println("success=" + success);
    }

    @Test
    public void testHookAsync() throws SaltStackException, ExecutionException, InterruptedException {
        Token token = client.login("saltuser", "salt", AuthModule.PAM);

        JsonObject obj = new JsonObject();
        obj.addProperty("foo", "bar");
        obj.addProperty("boolean", true);

        Future<Boolean> result = client.hookAsync("my/event", obj.toString());
        System.out.println("Before future");
        System.out.println("success=" + result.get());
    }

}
