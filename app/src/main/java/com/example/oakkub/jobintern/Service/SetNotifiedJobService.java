package com.example.oakkub.jobintern.Service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.oakkub.jobintern.Network.InternetManager;
import com.example.oakkub.jobintern.Network.Retrofit.RestClient;

import retrofit.RetrofitError;

/**
 * Created by OaKKuB on 8/7/2015.
 */
public class SetNotifiedJobService extends IntentService {

    public static String ACTION_SET_NOTIFIED = "com.example.oakkub.jobintern.Service.SetNotifiedJobService.ACTION_SET_NOTIFIED";
    public static int REQUEST_CODE_SET_NOTIFIED = 777;

    public SetNotifiedJobService() {
        super(SetNotifiedJobService.class.getName());
    }

    public SetNotifiedJobService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        // IntentService is run in background.

        setJobNotified(intent);

    }

    private void setJobNotified(Intent intent) {

        if (intent.getAction().equals(ACTION_SET_NOTIFIED)) {

            if(!InternetManager.isInternetConnected(this)) return;

            try {
                RestClient.getInstance(this).getApiService().setNotifiedJobAdvance();
            } catch (RetrofitError error) {
                Log.i("Network error", error.toString());
            }

        }

    }
}
