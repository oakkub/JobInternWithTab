package com.example.oakkub.jobintern.Network.Retrofit;

import android.content.Context;

import com.example.oakkub.jobintern.Utilities.UtilString;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by OaKKuB on 7/29/2015.
 */
public class RestClient {

    private final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
    private final int MAX_AGE = 60; // 60 seconds
    private final int MAX_STALE = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
    private final int CONNECT_TIMEOUT = 10; // 10 seconds
    private final int READ_TIMEOUT = 10; // 10 seconds
    private final String CACHE_NAME = "http";

    private static RestClient restClient;
    private ApiService apiService;

    public static RestClient getInstance(Context context) {
        if(restClient == null) restClient = new RestClient(context);
        return restClient;
    }

    private RestClient(Context context) {
        setupRestClient(context);
    }

    private void setupRestClient(Context context) {

        // create cache
        Cache cache = new Cache(new File(context.getCacheDir(), CACHE_NAME), SIZE_OF_CACHE);

        // create okHttp client
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);

        // add cache
        okHttpClient.setCache(cache);

        RestAdapter restAdapter = new RestAdapter.Builder()
                        .setEndpoint(UtilString.HOST)
                        .setClient(new OkClient(okHttpClient))
                        .setRequestInterceptor(new RequestInterceptor() {
                            @Override
                            public void intercept(RequestFacade request) {
                                request.addHeader("Cache-Control", String.format("public, max-age=%d", MAX_AGE));
                            }
                        })
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build();

        apiService = restAdapter.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }

}
