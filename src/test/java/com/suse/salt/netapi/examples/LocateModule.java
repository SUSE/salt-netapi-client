package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.calls.modules.Locate;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Example code calling locate module using the generic interface.
 */
public class LocateModule {

    private static final String SALT_API_URL = "http://localhost:8000";
    private static final String USER = "saltdev";
    private static final String PASSWORD = "saltdev";
    private static final AuthModule AUTH = AuthModule.AUTO;

    public static void main(String[] args) throws SaltException {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL));
        client.login(USER, PASSWORD, AUTH);

        // Ping all minions using a glob matcher
        Target<String> globTarget = new Glob();

        String pattern = "ld.*";

        LocalCall<List<String>> call = Locate.locate(pattern, Optional.empty(), Optional.empty(), Optional.empty());
        Map<String, List<String>> results = call.callSync(client, globTarget);
        System.out.println("Results without regex, no count:");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result));

        Locate.LocateOpts opts = new Locate.LocateOpts();
        opts.setRegex(true);
        opts.setCount(true);
        call = Locate.locate(pattern, Optional.empty(), Optional.empty(), Optional.of(opts));
        results = call.callSync(client, globTarget);
        System.out.println("Results setting regex and count to true:");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result));

        opts = new Locate.LocateOpts();
        opts.setRegex(true);
        call = Locate.locate(pattern, Optional.empty(), Optional.of(2), Optional.of(opts));
        results = call.callSync(client, globTarget);
        System.out.println("Results setting regex to true and limiting the results to 2:");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result));
    }
}
