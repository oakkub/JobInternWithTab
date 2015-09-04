package com.example.oakkub.jobintern.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.UI.Dialog.AlertDialog.AlertDialogFragment;
import com.example.oakkub.jobintern.UI.RecyclerView.EndlessRecyclerViewOnScrollListener;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewJobAdvanceAdapter;
import com.example.oakkub.jobintern.UI.RecyclerView.RecyclerViewJobAdvanceClickListener;
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
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AlertDialogFragment.YesNoListener {

    public static final int LOAD_AMOUNT = 20;
    private static final String FRAGMENT_TYPE = "fragmentPosition";
    private static final String RECYCLE_STATE = "recyclerView";

    @Bind(R.id.mainRecyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshMainLayout) SwipeRefreshLayout swipeRefreshLayout;

    private View rootView;
    private RecyclerViewJobAdvanceAdapter recyclerViewJobAdvanceAdapter;
    private RecyclerViewJobAdvanceClickListener recyclerViewJobAdvanceClickListener;
    private EndlessRecyclerViewOnScrollListener endlessRecyclerViewOnScrollListener;

    private String username = "", fetchCondition = "";
    private int startFetchPosition = 0;

    public static MainActivityFragment getInstance(String type) {

        MainActivityFragment fragment = new MainActivityFragment();
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TYPE, type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // tells that this fragment has menu
        setHasOptionsMenu(true);
        // retain this fragment when activity is re-initialized
        setRetainInstance(true);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (username.equals("")) username = sharedPreferences.getString(Util.PREF_USERNAME, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, viewGroup, false);

        ButterKnife.bind(this, rootView);

        // getArguments will get the bundle that was set by method setArguments()
        Bundle args = getArguments();
        fetchCondition = args.getString(FRAGMENT_TYPE);

        // set swipe refresh layout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

        // create RecyclerView
        initRecyclerView();

        return rootView;
    }

    @Override
    public void onRefresh() {

        startFetchPosition = 0;
        endlessRecyclerViewOnScrollListener.reset();
        recyclerViewJobAdvanceAdapter.removeAllItem();
        recyclerViewJobAdvanceAdapter.setEndOfResult(false);
        loadJobAdvance(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(RECYCLE_STATE, recyclerView.getLayoutManager().onSaveInstanceState());

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLE_STATE));

        }

        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onYes(String tag) {
        recyclerViewJobAdvanceClickListener.onYes(tag);
    }

    @Override
    public void onNo(String tag) {
        recyclerViewJobAdvanceClickListener.onNo(tag);
    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if (recyclerViewJobAdvanceAdapter == null) {

            recyclerViewJobAdvanceAdapter = new RecyclerViewJobAdvanceAdapter(getActivity(), fetchCondition);

            // add progress bar
            recyclerViewJobAdvanceAdapter.addItem(null);
            loadJobAdvance(true);
        }
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

                loadJobAdvance(false);
            }
        };

        recyclerViewJobAdvanceClickListener = new RecyclerViewJobAdvanceClickListener(this, getActivity(), getActivity().getSupportFragmentManager(), swipeRefreshLayout, rootView, recyclerViewJobAdvanceAdapter, username);
        recyclerViewJobAdvanceAdapter.setJobAdvanceClickListener(recyclerViewJobAdvanceClickListener);

        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
        recyclerView.setAdapter(recyclerViewJobAdvanceAdapter);

    }

    private void loadJobAdvance(final boolean isRefresh) {

        RestClient.getInstance(getActivity()).getApiService().getJobAdvance(fetchCondition, startFetchPosition, LOAD_AMOUNT,
                new Callback<List<JobAdvance>>() {
                    @Override
                    public void success(List<JobAdvance> jobAdvancesFromServer, Response response) {

                        final int size = jobAdvancesFromServer.size();

                        if (size > 0) {

                            if (isRefresh) {
                                // reset item
                                swipeRefreshLayout.setRefreshing(false);
                                recyclerViewJobAdvanceAdapter.removeAllItem();
                            } else {
                                // remove progress bar
                                recyclerViewJobAdvanceAdapter.removeLast();
                            }

                            recyclerViewJobAdvanceAdapter.addAllItem(jobAdvancesFromServer);

                            startFetchPosition += LOAD_AMOUNT;

                            recyclerViewJobAdvanceAdapter.addItem(null);

                        } else {

                            // remove progress bar
                            recyclerViewJobAdvanceAdapter.removeLast();
                            recyclerViewJobAdvanceAdapter.setEndOfResult(true);
                            // add end result view
                            recyclerViewJobAdvanceAdapter.addItem(null);

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Log.i("FETCH ERROR", "Success: " + String.valueOf(error.getSuccessType()));
                        Log.i("FETCH ERROR", "Response: " + String.valueOf(error.getResponse()));
                        Log.i("FETCH ERROR", "Url: " + String.valueOf(error.getUrl()));
                        Log.i("FETCH ERROR", "Kind: " + String.valueOf(error.getKind()));
                        Log.i("FETCH ERROR", "Error Message: " + String.valueOf(error.getMessage()));

                        swipeRefreshLayout.setRefreshing(false);

                    }
                });

    }

}
