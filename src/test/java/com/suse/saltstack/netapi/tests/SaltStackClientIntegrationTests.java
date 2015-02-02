package com.suse.saltstack.netapi.tests;

import com.suse.saltstack.netapi.client.SaltStackClient;
import com.suse.saltstack.netapi.client.SaltStackToken;
import com.suse.saltstack.netapi.client.SaltStackRunResults;

public class SaltStackClientIntegrationTests {

    public static void main(String[] args) {

	if (args.length != 3) {
            System.out.println("Usage:\n\tjava com.suse.saltstack.netapi.tests.SaltStackClientIntegrationTests <URI> <username> <password>");
            System.exit(1);
        }

	String URI      = args[0];
        String username = args[1];
        String password = args[2];

        try {
            SaltStackClient client = new SaltStackClient(URI);
            SaltStackToken authToken = client.login(username, password, "pam");

            System.out.println("Token: " + authToken.getToken());

            SaltStackRunResults results = client.run(username, password, "pam", "local", "*", "test.ping", null, null);

	    System.out.println("Results: " + results.getResults().toString());
	

        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }

}
