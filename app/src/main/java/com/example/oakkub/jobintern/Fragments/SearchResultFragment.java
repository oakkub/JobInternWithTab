package com.example.oakkub.jobintern.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.UI.Dialog.AlertDialog.AlertDialogFragment;
import com.example.oakkub.jobintern.UI.RecyclerView.EndlessRecyclerViewOnScrollListener;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewJobAdvanceAdapter;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewJobAdvanceClickListener;
import com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager;
import com.example.oakkub.jobintern.UI.Spinner.SpinnerInteractionListener;
import com.example.oakkub.jobintern.Utilities.Util;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchResultFragment extends Fragment implements SpinnerInteractionListener.OnClickListener, AlertDialogFragment.YesNoListener {

    public static final String SEARCH_ACTION = "com.example.oakkub.jobintern.Fragments.SearchResultFragment.SEARCH_ACTION";
    public static final String SEARCH_QUERY = "com.example.oakkub.jobintern.Fragments.SearchResultFragment.SEARCH_QUERY";

    @Bind(R.id.searchToolbar) Toolbar toolbar;
    @Bind(R.id.searchResultRecyclerView) RecyclerView recyclerView;
    @Bind(R.id.searchTypeSpinner)
    Spinner searchTypeSpinner;

    private View rootView;
    private SearchView searchView;
    private SearchViewStateManager searchViewState;

    private ArrayAdapter spinnerAdapter;
    private SpinnerInteractionListener spinnerInteractionListener;

    private RecyclerViewJobAdvanceAdapter resultAdapter;
    private RecyclerViewJobAdvanceClickListener recyclerViewJobAdvanceClickListener;
    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;

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

        if (username.equals("")) username = sharedPreferences.getString(Util.PREF_USERNAME, "");
        if (!sharedPreferences.contains(Util.PREF_SEARCH_TYPE)) {

            editor.putString(Util.PREF_SEARCH_TYPE, getString(R.string.default_value_job_type_setting));
            editor.apply();

        }

        fetchCondition = sharedPreferences.getString(Util.PREF_SEARCH_TYPE, getString(R.string.default_value_job_type_setting));

        // get types of job to fetch
        searchTypes = getActivity().getResources().getStringArray(R.array.value_job_type_setting);

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

        initSpinner();
        initRecyclerView();

        return rootView;
    }

    private String getSearchQueryFormat(String query) {
        return "'" + query + "'";
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // check if the condition is the same as the previous
        if (sharedPreferences.getString(Util.PREF_SEARCH_TYPE, "").equals(searchTypes[position]))
            return;

        editor.putString(Util.PREF_SEARCH_TYPE, searchTypes[position]);
        editor.apply();

        updateSearchResult(searchTypes[position], searchQuery);

    }

    @Override
    public void onYes(String tag) {
        recyclerViewJobAdvanceClickListener.onYes(tag);
    }

    @Override
    public void onNo(String tag) {
        recyclerViewJobAdvanceClickListener.onNo(tag);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        searchViewState.onSavedInstanceState(outState);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if(savedInstanceState != null) {

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
        searchView.setQueryRefinementEnabled(sharedPreferences.getBoolean(getString(R.string.key_recent_search_suggestion), true));
        if (!sharedPreferences.getBoolean(getString(R.string.key_recent_search_suggestion), true))
            searchView.setSuggestionsAdapter(null);

        if(searchViewState == null) searchViewState = new SearchViewStateManager(searchView);
    }

    private void initSpinner() {

        if (spinnerAdapter == null) {
            spinnerAdapter = new ArrayAdapter(getActivity(), R.layout.custom_dropdown_spinner, searchTypes);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        spinnerInteractionListener = new SpinnerInteractionListener(this);

        searchTypeSpinner.setAdapter(spinnerAdapter);
        searchTypeSpinner.setOnTouchListener(spinnerInteractionListener);
        searchTypeSpinner.setOnItemSelectedListener(spinnerInteractionListener);

        for (int i = 0, size = searchTypes.length; i < size; i++) {
            if (searchTypes[i].equalsIgnoreCase(fetchCondition)) {
                searchTypeSpinner.setSelection(i);
                break;
            }
        }
    }

    public void updateSearchResult(String searchType, String query) {

        searchQuery = query;

        toolbar.setTitle(getSearchQueryFormat(query));

        fetchCondition = searchType;
        startFetchPosition = 0;

        resultAdapter.removeAllItem();

        endlessRecyclerViewOnScrollListener.reset();
        setRecyclerViewAdapter();
        setAdapterListener();
        recyclerView.setAdapter(resultAdapter);

        resultAdapter.notifyDataSetChanged();
        // add progress bars
        resultAdapter.addItem(null);
        fetchJobAdvanceByQuery();

    }

    private void setToolbar(AppCompatActivity activity) {

        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle(getSearchQueryFormat(searchQuery));

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

        setAdapterListener();
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
        recyclerView.setAdapter(resultAdapter);
    }

    private void setRecyclerViewAdapter() {
        resultAdapter = new RecyclerViewJobAdvanceAdapter(getActivity(), fetchCondition);
    }

    private void setAdapterListener() {

        recyclerViewJobAdvanceClickListener = new RecyclerViewJobAdvanceClickListener(this, getActivity(), getFragmentManager(), rootView, resultAdapter, username);
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

    public boolean canExit() {

        if (!searchView.isIconified()) searchView.setIconified(true);
        else return true;

        return false;
    }

}
