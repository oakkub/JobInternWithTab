package com.example.oakkub.jobintern.Service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.oakkub.jobintern.Network.InternetManager;
import com.example.oakkub.jobintern.Utilities.Util;

/**
 * Created by OaKKuB on 8/7/2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (!sharedPreferences.contains(Util.CONNECTIVITY_CHANGE)) {
            setConnectivityChange(editor, true);
        }

        if(InternetManager.isNetworkAvailable(context) &&
                sharedPreferences.getBoolean(Util.PREF_CHECK_BOX_NOTIFICATION, true) &&
                sharedPreferences.contains(Util.PREF_USERNAME) &&
                !sharedPreferences.getString(Util.PREF_USERNAME, "").equals("") &&
                sharedPreferences.getBoolean(Util.CONNECTIVITY_CHANGE, true)) {

            setConnectivityChange(editor, false);

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

        } else {
            setConnectivityChange(editor, true);
        }
    }

    private void setConnectivityChange(SharedPreferences.Editor editor, boolean isConnected) {
        editor.putBoolean(Util.CONNECTIVITY_CHANGE, isConnected);
        editor.apply();
    }
}
