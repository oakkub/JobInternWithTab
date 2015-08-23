package com.example.oakkub.jobintern.UI.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.CheckServerStatus;
import com.example.oakkub.jobintern.Objects.JobAdvance;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OaKKuB on 8/15/2015.
 */
public class RecyclerViewJobAdvanceClickListener implements RecyclerViewEditedJobAdvanceAdapter.JobAdvanceClickListener {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private View rootView;

    private RecyclerViewEditedJobAdvanceAdapter recyclerViewEditedJobAdvanceAdapter;

    private String username;

    public RecyclerViewJobAdvanceClickListener(Context context, View rootView, RecyclerViewEditedJobAdvanceAdapter recyclerViewEditedJobAdvanceAdapter, String username) {

        this.context = context;
        this.rootView = rootView;
        this.recyclerViewEditedJobAdvanceAdapter = recyclerViewEditedJobAdvanceAdapter;
        this.username = username;

        prepareBuilderAlertDialog();
    }

    public RecyclerViewJobAdvanceClickListener(Context context, SwipeRefreshLayout swipeRefreshLayout, View rootView, RecyclerViewEditedJobAdvanceAdapter recyclerViewEditedJobAdvanceAdapter, String username) {

        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.rootView = rootView;
        this.recyclerViewEditedJobAdvanceAdapter = recyclerViewEditedJobAdvanceAdapter;
        this.username = username;

        prepareBuilderAlertDialog();
    }

    private void prepareBuilderAlertDialog() {

        builder = new AlertDialog.Builder(context)
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismissDialog();
                        }
                    });

    }

    @Override
    public void approveClick(final int position) {

        if(swipeRefreshLayout != null) if(swipeRefreshLayout.isRefreshing()) return;

        final JobAdvance jobAdvance = recyclerViewEditedJobAdvanceAdapter.getItem(position);

        builder.setTitle("Job approval")
                .setMessage("Are you sure to approve job " + jobAdvance.getAdvanceNo() + "?")
               .setPositiveButton("Approve", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       approveJobAdvance(jobAdvance, position, username);
                       dismissDialog();
                   }
               });
        alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void postponeClick(final int position) {

        if(swipeRefreshLayout != null) if(swipeRefreshLayout.isRefreshing()) return;

        final JobAdvance jobAdvance = recyclerViewEditedJobAdvanceAdapter.getItem(position);

        builder.setTitle("Job postponement")
                .setMessage("Are you sure to postpone job " + jobAdvance.getAdvanceNo() + "?")
                .setPositiveButton("Postpone", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        postponeJobAdvance(jobAdvance, position);
                        dismissDialog();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public void disapproveClick(final int position) {

        if(swipeRefreshLayout != null) if(swipeRefreshLayout.isRefreshing()) return;

        final JobAdvance jobAdvance = recyclerViewEditedJobAdvanceAdapter.getItem(position);

        builder.setTitle("Job disapproval")
                .setMessage("Are you sure to disapprove job " + jobAdvance.getAdvanceNo() + "?")
                .setPositiveButton("Disapprove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        disapproveJobAdvance(jobAdvance, position, username);
                        dismissDialog();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();

    }

    private void approveJobAdvance(final JobAdvance jobAdvance, final int position, String username) {

        RestClient.getInstance(context).getApiService().approveJobAdvance(String.valueOf(jobAdvance.getAdvanceId()),
                username, new Callback<CheckServerStatus>() {

                    @Override
                    public void success(CheckServerStatus checkServerStatus, Response response) {

                        if (checkServerStatus.isProgressOK()) {

                            recyclerViewEditedJobAdvanceAdapter.removeItem(position);
                            Snackbar.make(rootView, "Job No." +
                                    jobAdvance.getAdvanceNo() +
                                    " has been approved.", Snackbar.LENGTH_LONG).show();


                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, "Cannot approved, please try again.",
                                Snackbar.LENGTH_LONG).show();

                    }
                });

    }

    private void postponeJobAdvance(final JobAdvance jobAdvance, final int position) {

        RestClient.getInstance(context).getApiService().postponeJobAdvance(String.valueOf(jobAdvance.getAdvanceId()),
                new Callback<CheckServerStatus>() {

                    @Override
                    public void success(CheckServerStatus checkServerStatus, Response response) {

                        if (checkServerStatus.isProgressOK()) {

                            recyclerViewEditedJobAdvanceAdapter.removeItem(position);

                            Snackbar.make(rootView, "Job No." +
                                    jobAdvance.getAdvanceNo() +
                                    " has been postponed.", Snackbar.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, "Cannot postponed, please try again.",
                                Snackbar.LENGTH_LONG).show();

                    }
                });

    }

    private void disapproveJobAdvance(final JobAdvance jobAdvance, final int position, String username) {

        RestClient.getInstance(context).getApiService().cancelJobAdvance(String.valueOf(jobAdvance.getAdvanceId()), username,
                new Callback<CheckServerStatus>() {

                    @Override
                    public void success(CheckServerStatus checkServerStatus, Response response) {

                        if (checkServerStatus.isProgressOK()) {

                            recyclerViewEditedJobAdvanceAdapter.removeItem(position);
                            Snackbar.make(rootView, "Job No." +
                                    jobAdvance.getAdvanceNo() +
                                    " has been disapproved.", Snackbar.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, "Cannot disapproved, please try again.",
                                Snackbar.LENGTH_LONG).show();
                    }
                });

    }

    public void setView(View rootView) {
        this.rootView = rootView;
    }

    public void dismissDialog() {
        if(alertDialog != null) if(alertDialog.isShowing()) alertDialog.dismiss();
    }

    public AlertDialog getAlertDialog() {
        return alertDialog;
    }
}
