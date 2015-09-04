package com.example.oakkub.jobintern.Fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.oakkub.jobintern.Activities.LoginActivity;
import com.example.oakkub.jobintern.Activities.SearchResultActivity;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Service.CheckJobReceiver;
import com.example.oakkub.jobintern.Service.SetNotifiedJobService;
import com.example.oakkub.jobintern.Settings.SettingsActivity;
import com.example.oakkub.jobintern.UI.Dialog.AlertDialog.AlertDialogFragment;
import com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager;
import com.example.oakkub.jobintern.UI.SearchView.SuggestionProvider;
import com.example.oakkub.jobintern.UI.ViewPager.ViewPagerAdapter;
import com.example.oakkub.jobintern.Utilities.OrientationDetector;
import com.example.oakkub.jobintern.Utilities.Util;

import java.util.Calendar;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class TabMainActivityFragment extends Fragment implements SearchView.OnQueryTextListener, ViewPager.OnPageChangeListener, SearchView.OnSuggestionListener, AlertDialogFragment.YesNoListener {

    private static final String LOGOUT_TAG = "logoutAlertDialog";
    private static final long TIME_INTERVAL_CHECK_JOB_TO_NOTIFY = 60 * 1000 * 10; // 10 minutes
    private static final int SETTINGS_REQUEST_CODE = 121;

    @Bind(R.id.mainToolbar)
    Toolbar toolbar;
    @Bind(R.id.mainTabLayout)
    TabLayout tabLayout;
    @Bind(R.id.mainViewPager)
    ViewPager viewPager;

    private AlertDialogFragment logoutDialog;

    private ViewPagerAdapter viewPagerAdapter;

    private SearchView searchView;
    private SearchViewStateManager searchViewState;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

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

        initToolbar((AppCompatActivity) getActivity());

        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new ViewPagerAdapter(getFragmentManager(), getActivity()));
        viewPager.addOnPageChangeListener(this);
        tabLayout.setupWithViewPager(viewPager);

        if (OrientationDetector.getOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE) {

            tabLayout.setTabMode(TabLayout.MODE_FIXED);
            tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        }

        return rootView;
    }

    private void initToolbar(AppCompatActivity activity) {

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setHomeButtonEnabled(true);

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
        gregorianCalendar.add(Calendar.MINUTE, 10);

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
        if (intent.hasExtra(Util.PREF_USERNAME)) {
            if (username.equals("")) username = intent.getStringExtra(Util.PREF_USERNAME);
        } else {
//            username = sharedPreferences.getString(Util.PREF_USERNAME, "");
            username = sharedPreferences.getString(Util.PREF_USERNAME, "");
        }

        // put username to preference, for check login
        if (editor == null) editor = sharedPreferences.edit();

        // put username into SharedPreference for later use
        editor.putString(Util.PREF_USERNAME, username);
        editor.apply();

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (searchViewState != null) {
            searchViewState.onSavedInstanceState(outState);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

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
        searchView.setOnSuggestionListener(this);
        searchView.setQueryRefinementEnabled(true);
        if (!isRecentSuggestionEnabled()) searchView.setSuggestionsAdapter(null);

        if (searchViewState == null) searchViewState = new SearchViewStateManager(searchView);

    }

    @Override
    public boolean onSuggestionSelect(int position) {
        return false;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        return sendSearchResult(searchView.getSuggestionsAdapter().getCursor().getString(2));
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        if (query.trim().isEmpty()) return false;
        if (sendSearchResult(query)) {

            if (isRecentSuggestionEnabled()) {

                SearchRecentSuggestions searchRecentSuggestions =
                        new SearchRecentSuggestions(getActivity(),
                                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                searchRecentSuggestions.saveRecentQuery(query, null);
            }

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

            if (isRecentSuggestionEnabled()) {
                SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            } else {
                searchView.setSuggestionsAdapter(null);
            }

        }

    }

    private boolean isRecentSuggestionEnabled() {
        return sharedPreferences.getBoolean(getString(R.string.key_recent_search_suggestion), true);
    }

    private boolean sendSearchResult(String query) {

        Intent resultIntent = new Intent(getActivity(), SearchResultActivity.class);
        resultIntent.setAction(SearchResultFragment.SEARCH_ACTION);
        resultIntent.putExtra(SearchResultFragment.SEARCH_QUERY, query);

        if (resultIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            searchView.setQuery("", false);
            searchView.setIconified(true);

            startActivity(resultIntent);
            getActivity().overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);

            return true;
        }

        return false;
    }

    private boolean isNotificationChecked() {
        return sharedPreferences.getBoolean(Util.PREF_CHECK_BOX_NOTIFICATION, true);
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

        logoutDialog =
                AlertDialogFragment.getInstance(getString(R.string.logout_title),
                        getString(R.string.logout_message),
                        getString(R.string.logout_title));
        logoutDialog.setTargetFragment(this, 0);
        logoutDialog.show(getFragmentManager(), LOGOUT_TAG);

    }

    @Override
    public void onYes(String tag) {

        switch (tag) {

            case LOGOUT_TAG:

                editor.remove(Util.PREF_USERNAME);
                editor.apply();

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                getActivity().overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom);

                break;

        }

    }

    @Override
    public void onNo(String tag) {

    }

    public boolean canExit() {

        if (!searchView.isIconified()) searchView.setIconified(true);
        else if (viewPager.getCurrentItem() > 0) viewPager.setCurrentItem(0);
        else return true;

        return false;
    }
}
