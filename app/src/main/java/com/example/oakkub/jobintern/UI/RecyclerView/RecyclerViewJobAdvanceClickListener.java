package com.example.oakkub.jobintern.UI.RecyclerView;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.example.oakkub.jobintern.Network.Retrofit.RestClient;
import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.Objects.JobUpdateManager;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.UI.Dialog.AlertDialog.AlertDialogFragment;
import com.example.oakkub.jobintern.UI.Dialog.AlertDialog.JobDetailAlertDialog;
import com.example.oakkub.jobintern.UI.Dialog.ProgressDialog.ProgressDialogFragment;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by OaKKuB on 8/15/2015.
 */
public class RecyclerViewJobAdvanceClickListener implements RecyclerViewJobAdvanceAdapter.JobAdvanceClickListener, AlertDialogFragment.YesNoListener {

    private static final String APPROVE_TAG = "approveTag";
    private static final String POSTPONE_TAG = "postponeTag";
    private static final String DISAPPROVE_TAG = "disapproveTag";
    private static final String PROGRESS_DIALOG_TAG = "progressDialogTag";

    private Context context;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialogFragment alertDialogFragment;
    private ProgressDialogFragment progressDialog;
    private View rootView;

    private RecyclerViewJobAdvanceAdapter recyclerViewJobAdvanceAdapter;

    private JobAdvance jobAdvance;
    private String username;
    private int clickPosition;

    public RecyclerViewJobAdvanceClickListener(Fragment fragment, Context context, FragmentManager fragmentManager, View rootView, RecyclerViewJobAdvanceAdapter recyclerViewJobAdvanceAdapter, String username) {

        this.fragment = fragment;
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.rootView = rootView;
        this.recyclerViewJobAdvanceAdapter = recyclerViewJobAdvanceAdapter;
        this.username = username;

        prepareUIComponent();
    }

    public RecyclerViewJobAdvanceClickListener(Fragment fragment, Context context, FragmentManager fragmentManager, SwipeRefreshLayout swipeRefreshLayout, View rootView, RecyclerViewJobAdvanceAdapter recyclerViewJobAdvanceAdapter, String username) {

        this(fragment, context, fragmentManager, rootView, recyclerViewJobAdvanceAdapter, username);
        this.swipeRefreshLayout = swipeRefreshLayout;

    }

    private void prepareUIComponent() {

        progressDialog = ProgressDialogFragment.getInstance();

    }

    private void initAlertDialog(int title, int message, int positiveButton, final String TAG) {

        alertDialogFragment = AlertDialogFragment.getInstance(context.getString(title),
                getJobMessage(jobAdvance, message),
                context.getString(positiveButton));
        alertDialogFragment.setTargetFragment(fragment, 0);
        alertDialogFragment.show(fragmentManager, TAG);
    }

    @Override
    public void approveClick(final int position) {

        if (!checkState(position)) return;

        initAlertDialog(R.string.job_approval, R.string.approved_job_type, R.string.approved_job_type, APPROVE_TAG);

    }

    @Override
    public void postponeClick(final int position) {

        if (!checkState(position)) return;

        initAlertDialog(R.string.job_postponement, R.string.postponed_job_type, R.string.postponed_job_type, POSTPONE_TAG);

    }

    @Override
    public void disapproveClick(final int position) {

        if (!checkState(position)) return;

        initAlertDialog(R.string.job_disapproval, R.string.disapproved_job_type, R.string.disapproved_job_type, DISAPPROVE_TAG);

    }

    @Override
    public void onLongClick(int position) {

        if (!checkState(position)) return;

        JobDetailAlertDialog jobDetailAlertDialog =
                JobDetailAlertDialog.getInstance(context, jobAdvance);
        jobDetailAlertDialog.setTargetFragment(fragment, 0);
        jobDetailAlertDialog.show(fragmentManager, "jobDetailAlertDialog");
    }

    @Override
    public void onYes(final String tag) {

        if (!checkState(clickPosition)) return;

        progressDialog.show(fragmentManager, PROGRESS_DIALOG_TAG);

        switch (tag) {
            case APPROVE_TAG:
                approveJobAdvance(jobAdvance, clickPosition, username);
                break;
            case POSTPONE_TAG:
                postponeJobAdvance(jobAdvance, clickPosition);
                break;
            case DISAPPROVE_TAG:
                disapproveJobAdvance(jobAdvance, clickPosition, username);
                break;
            default:
                progressDialog.dismiss();
        }

    }

    @Override
    public void onNo(final String tag) {
    }

    private boolean checkState(int position) {

        clickPosition = position;
        jobAdvance = recyclerViewJobAdvanceAdapter.getItem(position);
        return swipeRefreshLayout != null ? !swipeRefreshLayout.isRefreshing() : true;
    }

    private void approveJobAdvance(final JobAdvance jobAdvance, final int position, String username) {

        RestClient.getInstance(context).getApiService().approveJobAdvance(String.valueOf(jobAdvance.getAdvanceId()),
                username, new Callback<JobUpdateManager>() {

                    @Override
                    public void success(JobUpdateManager progressCallback, Response response) {

                        checkJobSuccess(progressCallback, position, jobAdvance);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, errorMessage(R.string.approved_job_type),
                                Snackbar.LENGTH_LONG).show();

                        progressDialog.dismiss();
                    }
                });

    }

    private void postponeJobAdvance(final JobAdvance jobAdvance, final int position) {

        RestClient.getInstance(context).getApiService().postponeJobAdvance(String.valueOf(jobAdvance.getAdvanceId()),
                new Callback<JobUpdateManager>() {

                    @Override
                    public void success(JobUpdateManager progressCallback, Response response) {

                        checkJobSuccess(progressCallback, position, jobAdvance);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, errorMessage(R.string.postponed_job_type),
                                Snackbar.LENGTH_LONG).show();

                        progressDialog.dismiss();
                    }
                });

    }

    private void disapproveJobAdvance(final JobAdvance jobAdvance, final int position, String username) {

        RestClient.getInstance(context).getApiService().cancelJobAdvance(String.valueOf(jobAdvance.getAdvanceId()), username,
                new Callback<JobUpdateManager>() {

                    @Override
                    public void success(JobUpdateManager progressCallback, Response response) {

                        checkJobSuccess(progressCallback, position, jobAdvance);
                    }

                    @Override
                    public void failure(RetrofitError error) {

                        Snackbar.make(rootView, errorMessage(R.string.disapproved_job_type), Snackbar.LENGTH_LONG).show();

                        progressDialog.dismiss();
                    }
                });
    }

    private String getJobMessage(JobAdvance jobAdvance, int message) {
        return context.getString(R.string.are_you_sure) + " " + context.getString(message).toLowerCase() + " " + jobAdvance.getAdvanceNo() + "?";
    }

    private String errorMessage(int jobTypeId) {
        return context.getString(R.string.cannot) + " " + context.getString(jobTypeId) + ", " + context.getString(R.string.try_again) + ".";
    }

    private void checkJobSuccess(JobUpdateManager jobUpdateManager, int position, JobAdvance jobAdvance) {

        if (!jobUpdateManager.cannotUpdate()) {

            recyclerViewJobAdvanceAdapter.removeItem(position);

            String message = jobUpdateManager.getStatusPlainText(context);

            if (jobUpdateManager.noUpdate()) {
                message = context.getString(R.string.cannot_proceed) + " " + jobAdvance.getAdvanceNo() + " " + context.getString(R.string.had_been) + " " + message;
            } else if (jobUpdateManager.updateSuccessful()) {
                message = jobAdvance.getAdvanceNo() + " " + context.getString(R.string.has_been) + " " + message;
            }

            Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
        }

        progressDialog.dismiss();

    }

}
