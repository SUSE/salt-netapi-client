package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.WheelResult;
import com.suse.salt.netapi.calls.modules.Grains;
import com.suse.salt.netapi.calls.modules.Test;
import com.suse.salt.netapi.calls.wheel.Key;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;

import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Example code calling salt modules using the generic interface.
 */
public class Calls {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";

    public static void main(String[] args) throws SaltException {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL));

        // Ping all minions using a glob matcher
        Target<String> globTarget = new Glob("*");
        Map<String, Boolean> results = Test.ping().callSync(
                client, globTarget, USER, PASSWORD, AuthModule.AUTO);

        System.out.println("--> Ping results:\n");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result));

        // Get the grains from a list of minions
        Target<List<String>> minionList = new MinionList("minion1", "minion2");
        Map<String, Map<String, Object>> grainResults = Grains.items(false).callSync(
                client, minionList, USER, PASSWORD, AuthModule.AUTO);

        grainResults.forEach((minion, grains) -> {
            System.out.println("\n--> Listing grains for '" + minion + "':\n");
            grains.forEach((key, value) -> System.out.println(key + ": " + value));
        });

        // Call a wheel function: list accepted and pending minion keys
        WheelResult<Key.Names> keyResults = Key.listAll().callSync(
                client, USER, PASSWORD, AuthModule.AUTO);
        Key.Names keys = keyResults.getData().getResult();

        System.out.println("\n--> Accepted minion keys:\n");
        keys.getMinions().forEach(minion -> System.out.println(minion));
        System.out.println("\n--> Pending minion keys:\n");
        keys.getUnacceptedMinions().forEach(minion -> System.out.println(minion));
    }
}
