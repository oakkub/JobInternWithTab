package com.example.oakkub.jobintern.Service;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.oakkub.jobintern.Network.InternetManager;
import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.CheckServerStatus;
import com.example.oakkub.jobintern.Utilities.UtilString;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OaKKuB on 8/11/2015.
 */
public class ActionOnNotificationJobService extends BroadcastReceiver {

    public static final int APPROVE_REQUEST_CODE = 1122;
    public static final int DISAPPROVE_REQUEST_CODE = 1121;

    public static final String APPROVE_ACTION = "com.example.oakkub.jobintern.Service.APPROVE_ACTION";
    public static final String DISAPPROVE_ACTION = "com.example.oakkub.jobintern.Service.DISAPPROVE_ACTION";


    @Override
    public void onReceive(Context context, Intent intent) {

        if(!InternetManager.isNetworkAvailable(context)) return;

        String username = intent.getStringExtra(UtilString.PREF_USERNAME);
        String jobAdvanceId = String.valueOf(intent.getIntExtra(UtilString.DB_JOB_ADVANCE_ID, 0));

        switch (intent.getAction()) {

            case APPROVE_ACTION:

                approve(context, username, jobAdvanceId);
                dismissNotification(context);

                break;

            case DISAPPROVE_ACTION:

                disapprove(context, username, jobAdvanceId);
                dismissNotification(context);

                break;

        }

    }

    private void approve(final Context context, String username, String jobAdvanceId) {

        RestClient.getInstance(context).getApiService().approveJobAdvance(jobAdvanceId, username, new Callback<CheckServerStatus>() {
            @Override
            public void success(CheckServerStatus checkServerStatus, Response response) {

                if(checkServerStatus.isProgressOK()) {
                    Toast.makeText(context, "Job has been approved.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(context, "Cannot approve job, please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void disapprove(final Context context, String username, String jobAdvanceId) {

        RestClient.getInstance(context).getApiService().cancelJobAdvance(jobAdvanceId, username, new Callback<CheckServerStatus>() {
            @Override
            public void success(CheckServerStatus checkServerStatus, Response response) {

                if (checkServerStatus.isProgressOK()) {
                    Toast.makeText(context, "Job has been disapproved.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void failure(RetrofitError error) {

                Toast.makeText(context, "Cannot disapprove job, please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void dismissNotification(Context context) {

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(CheckJobReceiver.ALERT_NEW_JOB_NOTIFICATION);

    }
}
