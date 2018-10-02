package com.suse.salt.netapi.utils;

import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;

/**
 * Helpers for creating an async http client
 */
public class HttpClientUtils {

    /**
     * Creates a simple default http client
     * @return HttpAsyncClient
     */
    public static CloseableHttpAsyncClient defaultClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(0)
                .setConnectTimeout(10000)
                .setSocketTimeout(20000)
                .setCookieSpec(CookieSpecs.STANDARD)
                .build();
        HttpAsyncClientBuilder httpClientBuilder = HttpAsyncClients.custom();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);

        CloseableHttpAsyncClient asyncHttpClient = httpClientBuilder.build();
        asyncHttpClient.start();
        return asyncHttpClient;
    }

}
