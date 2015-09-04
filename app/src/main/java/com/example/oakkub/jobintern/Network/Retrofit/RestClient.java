package com.example.oakkub.jobintern.Network.Retrofit;

import android.content.Context;

import com.example.oakkub.jobintern.Utilities.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Created by OaKKuB on 7/29/2015.
 */
public class RestClient {

    private static RestClient restClient;
    private static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
    private static final int MAX_AGE = 86400; // 1 day
    private static final int MAX_STALE = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
    private static final int CONNECT_TIMEOUT = 10; // 10 seconds
    private static final int READ_TIMEOUT = 10; // 10 seconds
    private static final String CACHE_NAME = "http";
    private ApiService apiService;

    private RestClient(Context context) {
        setupRestClient(context);
    }

    public static RestClient getInstance(Context context) {
        if(restClient == null) restClient = new RestClient(context);
        return restClient;
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

        // create Gson for formatting date type
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Util.HOST)
                        .setClient(new OkClient(okHttpClient))
                .setRequestInterceptor(requestInterceptor)
                .setConverter(new GsonConverter(gson))
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .build();

        apiService = restAdapter.create(ApiService.class);
    }

    private RequestInterceptor requestInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {
            request.addHeader("Cache-Control", String.format("public, max-age=%d", MAX_AGE));
        }
    };

    public ApiService getApiService() {
        return apiService;
    }

}
