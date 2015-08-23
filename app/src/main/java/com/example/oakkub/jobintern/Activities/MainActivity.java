package com.example.oakkub.jobintern.Activities;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.oakkub.jobintern.Fragments.SearchResultFragment;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Service.CheckJobReceiver;
import com.example.oakkub.jobintern.Service.SetNotifiedJobService;
import com.example.oakkub.jobintern.Settings.SettingsActivity;
import com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager;
import com.example.oakkub.jobintern.UI.ViewPager.ViewPagerAdapter;
import com.example.oakkub.jobintern.UI.ViewPager.ViewPagerTransformer;
import com.example.oakkub.jobintern.Utilities.ToolbarCommunicator;
import com.example.oakkub.jobintern.Utilities.UtilString;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ToolbarCommunicator, SearchView.OnQueryTextListener {

    private static final String ALERT_DIALOG_STATE = "com.example.oakkub.jobintern.Activities.AlertDialog";
    private static final String FIRST_TIME_STATE = "com.example.oakkub.jobintern.Activities.FIRST_TIME_STATE";
    private static final long TIME_INTERVAL_CHECK_JOB_TO_NOTIFY = 60 * 1000 * 10; // 10 minutes
    private static final int SETTINGS_REQUEST_CODE = 121;

    @Bind(R.id.mainToolbar) Toolbar toolbar;
    @Bind(R.id.mainTabLayout) TabLayout tabLayout;
    @Bind(R.id.mainViewPager) ViewPager viewPager;

    private SearchView searchView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private AlarmManager alarmManager;

    private AlertDialog logoutAlertDialog = null;

    private Intent newJobAlertIntent = null;
    private PendingIntent newJobAlertPendingIntent = null;

    private String username = "";
    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initSharedPreference();

        if(savedInstanceState != null) {
            firstTime = savedInstanceState.getBoolean(FIRST_TIME_STATE);
        }

        if(firstTime) {
            // intent for new job notification
            newJobAlertIntent = new Intent(this, CheckJobReceiver.class);
            newJobAlertPendingIntent = PendingIntent.getBroadcast(this,
                    CheckJobReceiver.ALERT_NEW_JOB_REQUEST_CODE,
                    newJobAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            if(isNotificationChecked()) {
                // set notified of all job
                initSetNotifiedJob();

                // check new job in every 10 minutes
                initCheckJobNotification();
            }

            firstTime = false;
        }

        initToolbar();

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), this));
        viewPager.setPageTransformer(true, new ViewPagerTransformer());
        //viewPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.DEPTH));
        tabLayout.setupWithViewPager(viewPager);

    }

    private void initToolbar() {

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void initSetNotifiedJob() {

        Intent setNotifiedIntentService = new Intent(this, SetNotifiedJobService.class);
        setNotifiedIntentService.setAction(SetNotifiedJobService.ACTION_SET_NOTIFIED);
        if(setNotifiedIntentService.resolveActivity(getPackageManager()) != null) startService(setNotifiedIntentService);

    }

    private void initCheckJobNotification() {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        // make AlarmManager start after 10 minutes
        gregorianCalendar.add(Calendar.MINUTE, 10);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(),
                TIME_INTERVAL_CHECK_JOB_TO_NOTIFY, newJobAlertPendingIntent);
    }

    private void initSharedPreference() {

        if(sharedPreferences == null) sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // get intent from login activity, since this class will get call from login activity
        Intent intent = getIntent();

        // check if this intent has username
        if(intent.hasExtra(UtilString.PREF_USERNAME)) {
            if(username.equals("")) username = intent.getStringExtra(UtilString.PREF_USERNAME);
        } else {
            username = sharedPreferences.getString(UtilString.PREF_USERNAME, "");
        }

        // put username to preference, for check login
        if (editor == null) editor = sharedPreferences.edit();

        // put username into SharedPreference for later use
        editor.putString(UtilString.PREF_USERNAME, username);
        editor.apply();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if(logoutAlertDialog != null) {

            if(logoutAlertDialog.isShowing()) {

                outState.putBundle(ALERT_DIALOG_STATE, logoutAlertDialog.onSaveInstanceState());
                logoutAlertDialog.dismiss();
            }
        }

        outState.putBoolean(FIRST_TIME_STATE, firstTime);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState != null) {

            if(savedInstanceState.containsKey(ALERT_DIALOG_STATE)) {

                initLogoutDialog();
                logoutAlertDialog.onRestoreInstanceState(savedInstanceState.getBundle(ALERT_DIALOG_STATE));
            }

        }

    }

    @Override
    public void onBackPressed() {

        if(!searchView.isIconified()) {
            searchView.setIconified(true);
        } else if(viewPager.getCurrentItem() > 0) {
            viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setTitle(String text) {
        toolbar.setTitle(text);
    }

    @Override
    public void setVisibility(int visibility) {
        toolbar.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // init search view
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_settings).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        //searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchResultActivity.class)));
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if(query.trim().isEmpty()) return false;

        Intent resultIntent = new Intent(getApplicationContext(), SearchResultActivity.class);
        resultIntent.setAction(SearchResultFragment.SEARCH_ACTION);
        resultIntent.putExtra(SearchResultFragment.SEARCH_QUERY, query);

        if(resultIntent.resolveActivity(getPackageManager()) != null) {

            searchView.setQuery("", false);

            startActivity(resultIntent);
            overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
            return true;
        }

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()) {

            case R.id.action_settings:

                Intent settingsIntent = new Intent(this, SettingsActivity.class);

                if(settingsIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(settingsIntent, SETTINGS_REQUEST_CODE);
                }

                return true;

            case R.id.logout_settings:

                initLogoutDialog();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SETTINGS_REQUEST_CODE) {

            Log.i("resultCode", String.valueOf(resultCode));

            if(isNotificationChecked()) {

                // set notified of all job
                initSetNotifiedJob();

                // check new job in every 10 minutes
                initCheckJobNotification();

            }

        }

    }

    private boolean isNotificationChecked() {
        return sharedPreferences.getBoolean(UtilString.PREF_CHECK_BOX_NOTIFICATION, true);
    }

    private void initLogoutDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Log out");
        alertDialogBuilder.setMessage("Are you sure to log out?");
        alertDialogBuilder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                editor.remove(UtilString.PREF_USERNAME);
                editor.apply();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        logoutAlertDialog = alertDialogBuilder.create();
        logoutAlertDialog.show();

    }

}
