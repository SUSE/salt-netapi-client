package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.calls.modules.Grains;
import com.suse.salt.netapi.calls.modules.Test;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.SSHResult;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Example code calling salt modules via ssh.
 */
public class SaltSSH {

    private static final String SALT_API_URL = "http://localhost:8000";

    public static void main(String[] args) throws SaltException {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL));

        // Ping all minions using a glob matcher
        Target<String> globTarget = new Glob("*");
        Map<String, Result<SSHResult<Boolean>>> minionResults = Test.ping().callSyncSSH(
                client, globTarget, Optional.of("/tmp/susemanager-roster"));

        System.out.println("--> Ping results:\n");
        minionResults.forEach((minion, result) -> {
            System.out.println(minion + " -> " + result.fold(
                    error -> "Error: " + error.toString()
                    ,
                    res -> res.getReturn().orElse(false)
            ));
        });

        // Get grains from a list of minions
        Target<List<String>> minionList = new MinionList("my.future.minion");
        Map<String, Result<SSHResult<Map<String, Object>>>> grainResults = Grains.
                items(false).callSyncSSH(client, minionList, Optional.of("/tmp/my-roster"));

        grainResults.forEach((minion, grains) -> {
            System.out.println("\n--> Listing grains for '" + minion + "':\n");
            String grainsOutput = grains.fold(
                    error -> "Error: " + error.toString(),
                    grainsMap -> {
                            grainsMap.getReturn().ifPresent(gmap -> gmap.entrySet().stream()
                                    .map(e -> e.getKey() + ": " + e.getValue())
                                    .collect(Collectors.joining("\n")));
                            return "Minion did not return: " + minion;
                    }
            );
            System.out.println(grainsOutput);
        });
    }
}
