package com.example.oakkub.jobintern.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.oakkub.jobintern.Activities.LoginActivity;
import com.example.oakkub.jobintern.Activities.SearchResultActivity;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Service.CheckJobReceiver;
import com.example.oakkub.jobintern.Service.SetNotifiedJobService;
import com.example.oakkub.jobintern.Settings.SettingsActivity;
import com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager;
import com.example.oakkub.jobintern.UI.ViewPager.ViewPagerAdapter;
import com.example.oakkub.jobintern.UI.ViewPager.ViewPagerTransformer;
import com.example.oakkub.jobintern.Utilities.UtilString;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabMainActivityFragment extends Fragment implements SearchView.OnQueryTextListener {

    private static final String ALERT_DIALOG_STATE = "com.example.oakkub.jobintern.Fragments.AlertDialog";
    private static final long TIME_INTERVAL_CHECK_JOB_TO_NOTIFY = 60 * 1000 * 10; // 10 minutes
    private static final int SETTINGS_REQUEST_CODE = 121;

    @Bind(R.id.mainToolbar)
    Toolbar toolbar;
    @Bind(R.id.mainTabLayout)
    TabLayout tabLayout;
    @Bind(R.id.mainViewPager)
    ViewPager viewPager;

    private SearchView searchView;
    private SearchViewStateManager searchViewState;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private AlertDialog logoutAlertDialog;

    private PendingIntent newJobAlertPendingIntent;

    private String username = "";
    private boolean isChecked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        // intent for new job notification
        Intent newJobAlertIntent = new Intent(getActivity(), CheckJobReceiver.class);
        newJobAlertPendingIntent = PendingIntent.getBroadcast(getActivity(),
                CheckJobReceiver.ALERT_NEW_JOB_REQUEST_CODE,
                newJobAlertIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        initSharedPreference();
        initPushNotification();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tab_main, container, false);

        ButterKnife.bind(this, rootView);

        initToolbar();

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity()));
        viewPager.setPageTransformer(true, new ViewPagerTransformer());
        //viewPager.setPageTransformer(true, new ViewPagerTransformer(ViewPagerTransformer.DEPTH));
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void initToolbar() {

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void initSetNotifiedJob() {

        Intent setNotifiedIntentService = new Intent(getActivity(), SetNotifiedJobService.class);
        setNotifiedIntentService.setAction(SetNotifiedJobService.ACTION_SET_NOTIFIED);
        if (setNotifiedIntentService.resolveActivity(getActivity().getPackageManager()) != null)
            getActivity().startService(setNotifiedIntentService);

    }

    private void initCheckJobNotification() {

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        // make AlarmManager start after 10 minutes
        gregorianCalendar.add(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                gregorianCalendar.getTimeInMillis(),
                TIME_INTERVAL_CHECK_JOB_TO_NOTIFY, newJobAlertPendingIntent);
    }

    private void initSharedPreference() {

        if (sharedPreferences == null)
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // get intent from login activity, since this class will get call from login activity
        Intent intent = getActivity().getIntent();

        // check if this intent has username
        if (intent.hasExtra(UtilString.PREF_USERNAME)) {
            if (username.equals("")) username = intent.getStringExtra(UtilString.PREF_USERNAME);
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
    public void onPause() {

        if (logoutAlertDialog.isShowing()) logoutAlertDialog.dismiss();

        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (logoutAlertDialog != null) {

            if (logoutAlertDialog.isShowing()) {

                outState.putBundle(ALERT_DIALOG_STATE, logoutAlertDialog.onSaveInstanceState());
                logoutAlertDialog.dismiss();
            }
        }

        searchViewState.onSavedInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            if (savedInstanceState.containsKey(ALERT_DIALOG_STATE)) {

                logoutAlertDialog.onRestoreInstanceState(savedInstanceState.getBundle(ALERT_DIALOG_STATE));
            }

            searchViewState.onRestoreInstanceState(savedInstanceState);
        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        searchViewState.setPreviousState(searchView);

        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        menu.clear();
        inflater.inflate(R.menu.menu_tab_main, menu);

        // init search view
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_settings).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);

        if (searchViewState == null) searchViewState = new SearchViewStateManager(searchView);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (query.trim().isEmpty()) return false;

        Intent resultIntent = new Intent(getActivity(), SearchResultActivity.class);
        resultIntent.setAction(SearchResultFragment.SEARCH_ACTION);
        resultIntent.putExtra(SearchResultFragment.SEARCH_QUERY, query);

        if (resultIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            searchView.setQuery("", false);

            startActivity(resultIntent);
            getActivity().overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
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

        switch (item.getItemId()) {

            case R.id.action_settings:

                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);

                if (settingsIntent.resolveActivity(getActivity().getPackageManager()) != null) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SETTINGS_REQUEST_CODE) {

            initPushNotification();

        }

    }

    private boolean isNotificationChecked() {
        return sharedPreferences.getBoolean(UtilString.PREF_CHECK_BOX_NOTIFICATION, true);
    }

    private void initPushNotification() {

        if (isNotificationChecked() && !isChecked) {
            // set notified of all job
            initSetNotifiedJob();

            // check new job in every 10 minutes
            initCheckJobNotification();

            isChecked = true;
        }

    }

    private void initLogoutDialog() {

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.logout_title));
        alertDialogBuilder.setMessage(getString(R.string.logout_message));
        alertDialogBuilder.setPositiveButton(getString(R.string.logout_title), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                editor.remove(UtilString.PREF_USERNAME);
                editor.apply();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        logoutAlertDialog = alertDialogBuilder.create();
        logoutAlertDialog.show();

    }

    public SearchView getSearchView() {
        return searchView;
    }

    public ViewPager getViewPager() {
        return viewPager;
    }
}
