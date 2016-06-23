package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.calls.modules.Grains;
import com.suse.salt.netapi.calls.modules.Test;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.results.SSHResult;

import java.net.URI;
import java.util.Map;
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
        Map<String, Result<SSHResult<Boolean>>> minionResults =
                Test.ping().callSyncSSH(client, globTarget);

        System.out.println("--> Ping results:\n");
        minionResults.forEach((minion, result) -> {
            System.out.println(minion + " -> " + result.fold(
                    error -> "Error: " + error.toString(),
                    res -> res.getReturn().orElse(false)
            ));
        });

        // Get grains from all minions
        Map<String, Result<SSHResult<Map<String, Object>>>> grainResults =
                Grains.items(false).callSyncSSH(client, globTarget);

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
