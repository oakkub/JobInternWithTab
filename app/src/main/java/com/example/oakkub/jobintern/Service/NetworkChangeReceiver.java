package com.example.oakkub.jobintern.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.oakkub.jobintern.Network.InternetManager;
import com.example.oakkub.jobintern.Utilities.UtilString;

/**
 * Created by OaKKuB on 8/7/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static boolean firstConnect = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if(InternetManager.isNetworkAvailable(context) &&
                sharedPreferences.getBoolean(UtilString.PREF_CHECK_BOX_NOTIFICATION, true) &&
                sharedPreferences.contains(UtilString.PREF_USERNAME) &&
                !sharedPreferences.getString(UtilString.PREF_USERNAME, "").equals("") &&
                firstConnect) {

            Intent alertNewJobIntent = new Intent(context, CheckJobReceiver.class);
            PendingIntent alertPendingIntent = PendingIntent.getBroadcast(
                    context, CheckJobReceiver.ALERT_NEW_JOB_REQUEST_CODE,
                    alertNewJobIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            try {
                alertPendingIntent.send();
                Log.i("notify network change", "SEND");
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            firstConnect = false;
        } else {
            firstConnect = true;
        }

    }
}
