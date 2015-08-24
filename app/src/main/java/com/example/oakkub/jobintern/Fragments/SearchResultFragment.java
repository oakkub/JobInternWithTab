package com.example.oakkub.jobintern.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.UI.RecyclerView.EndlessRecyclerViewOnScrollListener;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewEditedJobAdvanceAdapter;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewJobAdvanceClickListener;
import com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager;
import com.example.oakkub.jobintern.Utilities.UtilString;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchResultFragment extends Fragment implements DialogInterface.OnClickListener {

    public static final String SEARCH_ACTION = "com.example.oakkub.jobintern.Fragments.SEARCH_ACTION";
    public static final String SEARCH_QUERY = "com.example.oakkub.jobintern.Fragments.SEARCH_QUERY";
    private static final String SEARCH_TYPE_DIALOG = "com.example.oakkub.jobintern.Fragments.SEARCH_TYPE_DIALOG";
    private static final String JOB_ALERT_DIALOG_STATE = "com.example.oakkub.jobintern.Fragments.JOB_ALERT_DIALOG_STATE";
    @Bind(R.id.searchToolbar) Toolbar toolbar;
    @Bind(R.id.searchResultRecyclerView) RecyclerView recyclerView;
    private View rootView;
    private SearchView searchView;
    private SearchViewStateManager searchViewState;

    private RecyclerViewEditedJobAdvanceAdapter resultAdapter;
    private RecyclerViewJobAdvanceClickListener recyclerViewJobAdvanceClickListener;
    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;

    private AlertDialog searchTypeAlertDialog;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String searchQuery = "", username = "", fetchCondition = "";
    private String[] searchTypes;
    private int startFetchPosition = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();

        if(username.equals("")) username = sharedPreferences.getString(UtilString.PREF_USERNAME, "");
        if(!sharedPreferences.contains(UtilString.PREF_SEARCH_TYPE)) {

            editor.putString(UtilString.PREF_SEARCH_TYPE, getString(R.string.default_value_job_type_setting));
            editor.apply();

        }

        fetchCondition = sharedPreferences.getString(UtilString.PREF_SEARCH_TYPE, getString(R.string.default_value_job_type_setting));

        // get types of job to fetch
        searchTypes = getActivity().getResources().getStringArray(R.array.entries_job_type_setting);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Intent searchResultIntent = getActivity().getIntent();

        if(!searchResultIntent.getAction().equals(SEARCH_ACTION)) getActivity().finish();
        else if(searchQuery.equals("")) searchQuery = searchResultIntent.getStringExtra(SEARCH_QUERY).trim();

        rootView = inflater.inflate(R.layout.fragment_search_result, container, false);

        ButterKnife.bind(this, rootView);

        setToolbar((AppCompatActivity) getActivity());

        initRecyclerView();

        return rootView;
    }

    private void initSearchTypeAlertDialog() {

        int checkedPosition = 0;
        for(int i = 0, size = searchTypes.length; i < size; i++) {
            if(searchTypes[i].equalsIgnoreCase(fetchCondition)) checkedPosition = i;
        }

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setSingleChoiceItems(searchTypes, checkedPosition, this)
                   .setTitle(getString(R.string.SearchType))
                .setNegativeButton(getString(R.string.cancel), null);

        searchTypeAlertDialog = alertDialog.create();
        searchTypeAlertDialog.show();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if(searchTypeAlertDialog != null) {
            if(searchTypeAlertDialog.isShowing()) {
                outState.putBundle(SEARCH_TYPE_DIALOG, searchTypeAlertDialog.onSaveInstanceState());
                searchTypeAlertDialog.dismiss();
            }
        }

        if(recyclerViewJobAdvanceClickListener.getAlertDialog() != null) {
            if(recyclerViewJobAdvanceClickListener.getAlertDialog().isShowing()) {
                outState.putBundle(JOB_ALERT_DIALOG_STATE, recyclerViewJobAdvanceClickListener.getAlertDialog().onSaveInstanceState());
                recyclerViewJobAdvanceClickListener.dismissDialog();
            }
        }

        searchViewState.onSavedInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if(savedInstanceState != null) {

            if(searchTypeAlertDialog != null) {

                if(savedInstanceState.containsKey(SEARCH_TYPE_DIALOG)) {
                    searchTypeAlertDialog.onRestoreInstanceState(savedInstanceState.getBundle(SEARCH_TYPE_DIALOG));
                }
            }

            if(recyclerViewJobAdvanceClickListener.getAlertDialog() != null) {

                if(savedInstanceState.containsKey(JOB_ALERT_DIALOG_STATE)) {

                    recyclerViewJobAdvanceClickListener.setView(rootView);
                    recyclerViewJobAdvanceClickListener.getAlertDialog().onRestoreInstanceState(savedInstanceState.getBundle(JOB_ALERT_DIALOG_STATE));
                }
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
        inflater.inflate(R.menu.menu_search_result, menu);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_type_result_settings).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        if(searchViewState == null) searchViewState = new SearchViewStateManager(searchView);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.search_type_settings:

                initSearchTypeAlertDialog();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int position) {

        editor.putString(UtilString.PREF_SEARCH_TYPE, searchTypes[position]);
        editor.apply();

        // check if the condition is the same as the previous
        if(sharedPreferences.getString(UtilString.PREF_SEARCH_TYPE, "").equals(fetchCondition)) return;

        dialogInterface.dismiss();

        updateSearchResult(searchTypes[position], searchQuery);

    }

    public void updateSearchResult(String searchType, String query) {

        searchQuery = query;

        toolbar.setTitle(searchType + ": '" + query + "'");

        fetchCondition = searchType;
        startFetchPosition = 0;

        resultAdapter.removeAllItem();

        endlessRecyclerViewOnScrollListener.reset();
        setRecyclerViewAdapter();
        recyclerView.setAdapter(resultAdapter);

        resultAdapter.notifyDataSetChanged();
        // add progress bars
        resultAdapter.addItem(null);
        fetchJobAdvanceByQuery();

    }

    private void setToolbar(AppCompatActivity activity) {

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(fetchCondition + ": '" + searchQuery + "'");

    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        if(resultAdapter == null) {

            setRecyclerViewAdapter();
            fetchJobAdvanceByQuery();
            // add progress bar
            resultAdapter.addItem(null);
        }
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

                fetchJobAdvanceByQuery();
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
        recyclerView.setAdapter(resultAdapter);
    }

    private void setRecyclerViewAdapter() {

        resultAdapter = new RecyclerViewEditedJobAdvanceAdapter(fetchCondition);
        recyclerViewJobAdvanceClickListener = new RecyclerViewJobAdvanceClickListener(getActivity(), rootView, resultAdapter, username);
        resultAdapter.setJobAdvanceClickListener(recyclerViewJobAdvanceClickListener);

    }

    private void fetchJobAdvanceByQuery() {

        RestClient.getInstance(getActivity()).getApiService().getSearchJobAdvance(fetchCondition, searchQuery, startFetchPosition, MainActivityFragment.LOAD_AMOUNT,
                new Callback<List<JobAdvance>>() {
                    @Override
                    public void success(List<JobAdvance> jobAdvancesFromServer, Response response) {

                        resultAdapter.removeLast();

                        if (jobAdvancesFromServer.size() > 0) {

                            // remove progress bar
                            resultAdapter.addAllItem(jobAdvancesFromServer);

                            startFetchPosition += MainActivityFragment.LOAD_AMOUNT;

                        } else {

                            resultAdapter.setEndOfResult(true);
                        }

                        // add progress bar or end of result (if endOfResult is true)
                        resultAdapter.addItem(null);

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("FETCH JOB ADVANCE ERROR", String.valueOf(error.getKind()));

                        //Toast.makeText(getActivity(), "Cannot fetch job, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public SearchView getSearchView() {
        return searchView;
    }

}
