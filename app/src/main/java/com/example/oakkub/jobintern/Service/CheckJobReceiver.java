package com.example.oakkub.jobintern.Service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.oakkub.jobintern.Activities.MainActivity;
import com.example.oakkub.jobintern.Network.InternetManager;
import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.CheckNewJobAdvance;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Utilities.UtilString;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OaKKuB on 8/6/2015.
 */
public class CheckJobReceiver extends BroadcastReceiver {

    public static final int ALERT_NEW_JOB_NOTIFICATION = 1;
    public static final int ALERT_NEW_JOB_REQUEST_CODE = 2;
    public static final String ALERT_NEW_JOB_ACTION = "com.example.oakkub.jobintern.Service.CHECK_JOB_RECEIVER";

    private NotificationCompat.Builder  newJobNotification = null;

    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        // check internet connection and username if user is logged in or not
        if(!InternetManager.isNetworkAvailable(context) ||
            !sharedPreferences.getBoolean(UtilString.PREF_CHECK_BOX_NOTIFICATION, true) ||
            !sharedPreferences.contains(UtilString.PREF_USERNAME) ||
             sharedPreferences.getString(UtilString.PREF_USERNAME, "").equals("")) return;

        Intent jobListIntent = new Intent(context, MainActivity.class);
        jobListIntent.setAction(ALERT_NEW_JOB_ACTION);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntent(jobListIntent);

        PendingIntent alertPendingIntent =
                taskStackBuilder.getPendingIntent(
                        ALERT_NEW_JOB_REQUEST_CODE,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        createNotification(context,
                    alertPendingIntent, "New jobs available",
                            "New jobs", "New jobs available");

        checkNewJob(context);

    }

    private void createNotification(Context context, PendingIntent pendingIntent, String contentTitle, String contentText, String ticker) {

        // intent when notification bar is closed by user
        Intent setNotifiedIntent = new Intent(context, SetNotifiedJobService.class);
        setNotifiedIntent.setAction(SetNotifiedJobService.ACTION_SET_NOTIFIED);

        PendingIntent setNotifiedPendingIntent = PendingIntent.getService(context,
                SetNotifiedJobService.REQUEST_CODE_SET_NOTIFIED,
                setNotifiedIntent, PendingIntent.FLAG_ONE_SHOT);

        newJobNotification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setTicker(ticker)
                .setSmallIcon(R.drawable.ic_main_notification)
                .setOnlyAlertOnce(true)
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE | NotificationCompat.PRIORITY_MAX |
                             NotificationCompat.DEFAULT_SOUND)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setDeleteIntent(setNotifiedPendingIntent)
                .setContentIntent(pendingIntent);


    }

    private void checkNewJob(final Context context) {

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Service.NOTIFICATION_SERVICE);

        RestClient.getInstance(context).getApiService().checkNewJobAdvance(new Callback<CheckNewJobAdvance>() {
            @Override
            public void success(CheckNewJobAdvance checkNewJobAdvance, Response response) {

                // 0 = New job: available (single job)
                // 1 = New jobs: available (multiple jobs)
                // 2 = No job

                switch (checkNewJobAdvance.getJobInfo()) {

                    case "0":

                        newJobNotification.setContentTitle("New Job available");
                        newJobNotification.setContentText("Job " + checkNewJobAdvance.getJobAdvno() + " is available.");
                        newJobNotification.setTicker("New Job " + checkNewJobAdvance.getJobAdvno() + " is available.");

                        // create pending intent for each action
                        Intent actionIntent = new Intent(context, ActionOnNotificationJobService.class);
                        actionIntent.putExtra(UtilString.DB_JOB_ADVANCE_ID, checkNewJobAdvance.getJobAdvId());
                        actionIntent.putExtra(UtilString.PREF_USERNAME,
                                PreferenceManager.getDefaultSharedPreferences(context)
                                        .getString(UtilString.PREF_USERNAME, ""));

                        actionIntent.setAction(ActionOnNotificationJobService.APPROVE_ACTION);
                        newJobNotification.addAction(R.drawable.ic_ok, "Approve", PendingIntent.getBroadcast(context,
                                ActionOnNotificationJobService.APPROVE_REQUEST_CODE, actionIntent,
                                PendingIntent.FLAG_ONE_SHOT));

                        actionIntent.setAction(ActionOnNotificationJobService.DISAPPROVE_ACTION);
                        newJobNotification.addAction(R.drawable.ic_close, "Disapprove", PendingIntent.getBroadcast(context,
                                ActionOnNotificationJobService.DISAPPROVE_REQUEST_CODE, actionIntent,
                                PendingIntent.FLAG_ONE_SHOT));

                        notificationManager.notify(ALERT_NEW_JOB_NOTIFICATION, newJobNotification.build());

                        break;

                    case "1":

                        newJobNotification.setContentText(checkNewJobAdvance.getJobItem() + " Jobs available.");
                        notificationManager.notify(ALERT_NEW_JOB_NOTIFICATION, newJobNotification.build());

                        break;

                    case "2":

                        Log.i("NO JOB", "NO NEW JOB AVAILABLE");

                        break;

                }

            }

            @Override
            public void failure(RetrofitError error) {

                Log.e("NOTIFICATION ERROR", error.getMessage());

            }
        });

    }
}
