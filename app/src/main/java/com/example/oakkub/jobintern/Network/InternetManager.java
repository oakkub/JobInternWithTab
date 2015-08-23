package com.example.oakkub.jobintern.Network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by OaKKuB on 7/28/2015.
 */
public class InternetManager {

    /**
     * Ensure that the internet is connected, must run in background thread.
     * @param context
     * @return
     */
    public static boolean isInternetConnected(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            try {
                return !InetAddress.getByName("google.com").equals("");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("NO INTERNET CONNECTION", e.getMessage());
            }
        }

        return false;
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        if(networkInfo != null) {

            for(NetworkInfo network: networkInfo) {

                if(network.getType() == ConnectivityManager.TYPE_MOBILE ||
                        network.getType() == ConnectivityManager.TYPE_WIFI) {

                    if(network.isAvailable() && network.isConnected()) return true;
                }

            }

        }

        return false;

    }

}
