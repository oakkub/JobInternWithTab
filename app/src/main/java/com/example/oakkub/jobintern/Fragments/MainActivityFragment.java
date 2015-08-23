package com.example.oakkub.jobintern.Fragments;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String FRAGMENT_TYPE = "fragmentPosition";
    private static final String RECYCLE_STATE = "recyclerView";
    private static final String ALERT_DIALOG_STATE = "jobAlertDialog";
    private static final String NETWORK_ALERT_DIALOG_STATE = "networkAlertDialog";

    public static final int LOAD_AMOUNT = 10;

    private View rootView;
    @Bind(R.id.mainRecyclerView) RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshMainLayout) SwipeRefreshLayout swipeRefreshLayout;

    private AlertDialog networkAlertDialog;

    private RecyclerViewEditedJobAdvanceAdapter recyclerViewEditedJobAdvanceAdapter;
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
        if(username.equals("")) username = sharedPreferences.getString(UtilString.PREF_USERNAME, "");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_main, viewGroup, false);

        ButterKnife.bind(this, rootView);

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
        recyclerViewEditedJobAdvanceAdapter.removeAllItem();
        recyclerViewEditedJobAdvanceAdapter.setEndOfResult(false);
        loadJobAdvance(true);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(RECYCLE_STATE, recyclerView.getLayoutManager().onSaveInstanceState());

        if(recyclerViewJobAdvanceClickListener.getAlertDialog() != null) {

            outState.putBundle(ALERT_DIALOG_STATE, recyclerViewJobAdvanceClickListener.getAlertDialog().onSaveInstanceState());
            recyclerViewJobAdvanceClickListener.dismissDialog();
        }

        if(networkAlertDialog != null) {
            if(networkAlertDialog.isShowing()) {
                outState.putBundle(NETWORK_ALERT_DIALOG_STATE, networkAlertDialog.onSaveInstanceState());
            }
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        if (savedInstanceState != null) {

            recyclerView.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLE_STATE));

            if(recyclerViewJobAdvanceClickListener.getAlertDialog() != null) {
                recyclerViewJobAdvanceClickListener.setView(rootView);

                if(savedInstanceState.containsKey(ALERT_DIALOG_STATE)) {
                    recyclerViewJobAdvanceClickListener.getAlertDialog().onRestoreInstanceState(savedInstanceState.getBundle(ALERT_DIALOG_STATE));
                }
            }

            if(networkAlertDialog != null) {

                if(savedInstanceState.containsKey(NETWORK_ALERT_DIALOG_STATE)) {
                    networkAlertDialog.onRestoreInstanceState(savedInstanceState.getBundle(NETWORK_ALERT_DIALOG_STATE));
                }
            }

        }

        super.onViewStateRestored(savedInstanceState);
    }

    private void initRecyclerView() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        if(recyclerViewEditedJobAdvanceAdapter == null) {

            recyclerViewEditedJobAdvanceAdapter = new RecyclerViewEditedJobAdvanceAdapter(fetchCondition);
            recyclerViewJobAdvanceClickListener = new RecyclerViewJobAdvanceClickListener(getActivity(), swipeRefreshLayout, rootView, recyclerViewEditedJobAdvanceAdapter, username);
            recyclerViewEditedJobAdvanceAdapter.setJobAdvanceClickListener(recyclerViewJobAdvanceClickListener);

            // add progress bar
            recyclerViewEditedJobAdvanceAdapter.addItem(null);
            loadJobAdvance(true);
        }
        endlessRecyclerViewOnScrollListener = new EndlessRecyclerViewOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

                loadJobAdvance(false);
            }
        };
        recyclerView.addOnScrollListener(endlessRecyclerViewOnScrollListener);
        recyclerView.setAdapter(recyclerViewEditedJobAdvanceAdapter);

    }

    private void createNetworkProblemAlertDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Network problem")
                .setMessage("Please check you internet connection, and come back.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getActivity().finish();
                    }
                })
                .setCancelable(false);

        networkAlertDialog = builder.create();
        networkAlertDialog.show();

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
                                recyclerViewEditedJobAdvanceAdapter.removeAllItem();
                            } else {
                                // remove progress bar
                                recyclerViewEditedJobAdvanceAdapter.removeLast();
                            }

                            recyclerViewEditedJobAdvanceAdapter.addAllItem(jobAdvancesFromServer);

                            startFetchPosition += LOAD_AMOUNT;

                            recyclerViewEditedJobAdvanceAdapter.addItem(null);

                        } else {

                            // remove progress bar
                            recyclerViewEditedJobAdvanceAdapter.removeLast();
                            recyclerViewEditedJobAdvanceAdapter.setEndOfResult(true);
                            // add end result view
                            recyclerViewEditedJobAdvanceAdapter.addItem(null);

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
