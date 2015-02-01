package com.suse.saltstack.netapi.client;

import org.junit.Test;
import org.junit.Rule;
import org.junit.Before;

import static org.junit.Assert.*;

import com.github.restdriver.clientdriver.ClientDriverRule;
import com.github.restdriver.clientdriver.ClientDriverRequest.Method;
import com.github.restdriver.clientdriver.ClientDriverRequest;

import static com.github.restdriver.clientdriver.RestClientDriver.giveResponse;
import static com.github.restdriver.clientdriver.RestClientDriver.onRequestTo;

import com.github.restdriver.clientdriver.exception.ClientDriverFailedExpectationException;

import com.suse.saltstack.netapi.exception.SaltStackException;

public class SaltStackClientMockTests {

    private SaltStackClient client;

    @Rule
    public ClientDriverRule driver = new ClientDriverRule(9999);

    @Before
    public void setUp() throws SaltStackException {
        client = new SaltStackClient("http://localhost:9999");
    }

    @Test
    public void LoginSuccessful() {

        SaltStackToken authToken = null;

	String expectedRequestBody = "{\"username\":\"user\",\"password\":\"pass\",\"eauth\":\"auto\"}";
        String responseBody = "{\"return\": [{\"perms\": [\".*\"], \"start\": 1422790143.013817, \"token\":" +
                              " \"7f966bcf66ffc36ca168b7d330fcb0321645a801\", \"expire\": 1422833343.013818, \"user\": \"user\", \"eauth\": \"auto\"}]}";


        try {
	    driver.addExpectation(
	        onRequestTo("/login").withMethod(Method.POST).withBody(expectedRequestBody, "application/json"), 
                giveResponse(responseBody));

            authToken = client.login("user", "pass");
	    
	} catch (Exception e) {
	    fail ("Exception thrown, message: " + e.getMessage());
	}	

        assertNotNull(authToken);
    }

}
