package com.suse.salt.netapi.utils;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Utility functions for tests
 */
public class TestUtils {

    public static CloseableHttpAsyncClient defaultClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(0)
                .setConnectTimeout(0)
                .setSocketTimeout(0)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        CloseableHttpAsyncClient asyncHttpClient = httpClientBuilder.build();
        asyncHttpClient.start();
        return asyncHttpClient;
    }

}
