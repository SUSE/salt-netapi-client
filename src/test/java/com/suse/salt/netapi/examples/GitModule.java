package com.suse.salt.netapi.examples;

import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.modules.Git;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.client.impl.HttpAsyncClientImpl;
import com.suse.salt.netapi.datatypes.AuthMethod;
import com.suse.salt.netapi.datatypes.PasswordAuth;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.results.GitResult;
import com.suse.salt.netapi.results.Result;
import com.suse.salt.netapi.utils.HttpClientUtils;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * Example code calling salt modules using the generic interface.
 */
public class GitModule {

    private static final String SALT_API_URL = "http://localhost:8080";
    private static final String USER = "saltuser";
    private static final String PASSWORD = "saltpass";
    private static final String REPOSITORY = "/home/ravega/dev/eclipse-workspace/salt-netapi-client";
    static final AuthMethod AUTH = new AuthMethod(new PasswordAuth(USER, PASSWORD, AuthModule.PAM));

    public static void main(String[] args) {
        // Init the client
        SaltClient client = new SaltClient(URI.create(SALT_API_URL),
                new HttpAsyncClientImpl(HttpClientUtils.defaultClient()));

        // Use a glob matcher
        Target<String> globTarget = new Glob("*");
        Map<String, Result<GitResult>> results = Git.status(REPOSITORY, Optional.empty()).callSync(
                client, globTarget, AUTH).toCompletableFuture().join();

        System.out.println("--> Git status results:\n");
        results.forEach((minion, result) -> System.out.println(minion + " -> " + result.result().get().toString()));

        // Create a branch
        Map<String, Result<Boolean>> branchResults = Git.branch(REPOSITORY, "feature_branch", "", "", Optional.empty()).callSync(
                client, globTarget, AUTH).toCompletableFuture().join();
        
        System.out.println("--> Git branch results:\n");
        branchResults.forEach((minion, result) -> System.out.println(minion + " -> " + result.toString()));

        // Delete branch.
        branchResults = Git.branch(REPOSITORY, "feature_branch", "-d", "", Optional.empty()).callSync(
                client, globTarget, AUTH).toCompletableFuture().join();
        
        System.out.println("--> Git branch results:\n");
        branchResults.forEach((minion, result) -> System.out.println(minion + " -> " + result.toString()));
    }  
}
