package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.WheelResult;
import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.PasswordAuth;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.HttpClientUtils;

import java.net.URI;
import java.util.Optional;

/**
 * Example code calling wheel functions.
 */
public class Wheel {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";

    static final AuthMethod AUTH = new AuthMethod(new PasswordAuth(USER, PASSWORD, AuthModule.AUTO));

    public static void main(String[] args) {
        // Init the client

        SaltClient client = new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientImpl(HttpClientUtils.defaultClient()));

        // List accepted and pending minion keys
        WheelResult<Result<Key.Names>> keyResults = Key.listAll().callSync(
                client, AUTH).toCompletableFuture().join();
        Result<Key.Names> resultKeys = keyResults.getData().getResult();
        Key.Names keys = resultKeys.result().get();

        System.out.println("\n--> Accepted minion keys:\n");
        keys.getMinions().forEach(System.out::println);
        System.out.println("\n--> Pending minion keys:\n");
        keys.getUnacceptedMinions().forEach(System.out::println);

        // Generate a new key pair and accept the public key
        WheelResult<Result<Key.Pair>> genResults = Key.genAccept("new.minion.id",
        		      Optional.empty()).callSync(client, AUTH).toCompletableFuture().join();
        Result<Key.Pair> resultKeyPair = genResults.getData().getResult();
        Key.Pair keyPair = resultKeyPair.result().get();

        System.out.println("\n--> New key pair:");
        System.out.println("\nPUB:\n\n" + keyPair.getPub());
        System.out.println("\nPRIV:\n\n" + keyPair.getPriv());
    }
}
