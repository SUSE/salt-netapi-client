package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.calls.modules.Git;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.Token;
import com.suse.salt.netapi.datatypes.target.MinionList;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.HttpClientUtils;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

public class GitModule {

    // salt netapi parameters
    private static final String SALT_API_URL = "http://192.168.1.12:8000";
    private static final String USER = "gitUser";
    private static final String PASSWORD = "gitUser";
    // git.clone parameters
    private static final String MINION_ID = "my_minion_id";
    private static final String GIT_URL = "https://github.com/SUSE/salt-netapi-client.git";
    private static final String CWD = "/home/saltuser/dev/";
    private static final String NAME = "java_netapi";
    private static final String HTTPS_USER = "saltgit";
    private static final String HTTPS_PASS = "saltgit";

    public static void main(String[] args) {
		// Init the client
    	SaltClient client =
                new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientImpl(HttpClientUtils.defaultClient()));
        Token token = client.login(USER, PASSWORD, AuthModule.PAM).toCompletableFuture().join();
        AuthMethod tokenAuth = new AuthMethod(token);

        // Cloning a repository via https with user name and password
        LocalCall<Boolean> call = Git.clone(
                CWD,
                GIT_URL,
                Optional.of(NAME),
                "",
                "",
                Optional.empty(), Optional.of(HTTPS_USER), Optional.of(HTTPS_PASS));
                // substitute above line for the below line of no user and password
                // Optional.empty(), Optional.empty(), Optional.empty());

    	Map<String, Result<Boolean>> results =
                call.callSync(client, new MinionList(MINION_ID), tokenAuth).toCompletableFuture().join();

        System.out.println("Response from minions:");
        results.forEach((minion, result) -> {
            System.out.println(minion + " -> " + result.result().orElse(Boolean.FALSE));
            result.error().ifPresent(err -> System.out.println(result.error()));
        });
    }
}
