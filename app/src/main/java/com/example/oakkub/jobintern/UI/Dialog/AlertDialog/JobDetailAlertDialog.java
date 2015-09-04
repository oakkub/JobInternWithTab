package com.example.oakkub.jobintern.UI.Dialog.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Utilities.UtilMethod;

import java.text.DecimalFormat;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by OaKKuB on 9/2/2015.
 */
public class JobDetailAlertDialog extends DialogFragment {

    private static final String ADVANCE_NO = "advanceNo";
    private static final String JOB_NO = "jobNo";
    private static final String ADVANCE_DATE = "advanceDate";
    private static final String ADVANCE_TOTAL = "advanceTotal";
    private static final String ADVANCE_STATUS = "advanceStatus";
    private static final String ADVANCE_APPROVE_DATE = "advanceApproveDate";
    private static final String ADVANCE_APPROVE_BY = "advanceApproveBy";
    private static final String ADVANCE_CANCEL_BY = "advanceCancelBy";
    private static final String STATUS_CONDITION = "status";

    @Bind(R.id.advance_no_dialog)
    TextView advanceNo;
    @Bind(R.id.job_no_dialog)
    TextView jobNo;
    @Bind(R.id.advance_date_dialog)
    TextView advanceDate;
    @Bind(R.id.advance_total_dialog)
    TextView advanceTotal;
    @Bind(R.id.advance_status_dialog)
    TextView advanceStatus;
    @Bind(R.id.advance_approve_date_dialog)
    TextView approveDate;
    @Bind(R.id.advance_approve_by_dialog)
    TextView approveBy;
    @Bind(R.id.advance_cancel_by_dialog)
    TextView cancelBy;

    private Context context;

    public JobDetailAlertDialog() {
        super();
    }

    public static JobDetailAlertDialog getInstance(Context context, JobAdvance jobAdvance) {

        Bundle args = new Bundle();
        args.putString(ADVANCE_NO, jobAdvance.getAdvanceNo());
        args.putString(JOB_NO, jobAdvance.getJobNo());
        args.putString(ADVANCE_DATE, UtilMethod.getPreferredDateFormat(jobAdvance.getAdvanceDate()));
        args.putDouble(ADVANCE_TOTAL, jobAdvance.getAdvanceTotal());
        args.putString(ADVANCE_STATUS, UtilMethod.capitalize(jobAdvance.getAdvanceStatusReadableFormat(context)));
        args.putSerializable(STATUS_CONDITION, jobAdvance.getStatusAvailable());
        args.putString(ADVANCE_APPROVE_DATE, UtilMethod.getPreferredDateFormat(jobAdvance.getAdvanceApproveDate()));
        args.putString(ADVANCE_APPROVE_BY, jobAdvance.getAdvanceApproveBy());
        args.putString(ADVANCE_CANCEL_BY, jobAdvance.getAdvanceCancelBy());

        JobDetailAlertDialog jobDetailAlertDialog = new JobDetailAlertDialog();
        jobDetailAlertDialog.setArguments(args);

        return jobDetailAlertDialog;
    }

    private void getContextFromFragment() {

        try {
            context = getTargetFragment().getContext();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        getContextFromFragment();

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View rootView = layoutInflater.inflate(R.layout.job_detail_dialog, null);
        Bundle args = getArguments();

        ButterKnife.bind(this, rootView);

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

        setInfo(args, decimalFormat);

        return new AlertDialog.Builder(context)
                .setView(rootView)
                .setTitle(context.getString(R.string.job_detail))
                .setNegativeButton(context.getString(R.string.ok), null)
                .create();
    }

    private void setInfo(Bundle args, DecimalFormat decimalFormat) {

        final String job = context.getString(R.string.job);
        final String jobAdvanceNo = context.getString(R.string.job_advance_no);
        final String no = context.getString(R.string.no);
        final String advDate = context.getString(R.string.advance_date);
        final String notApplicable = context.getString(R.string.not_applicable);
        final String advTotal = context.getString(R.string.advance_total);
        final String advStatus = context.getString(R.string.advance_status);
        final String advApproveDate = context.getString(R.string.advance_approve_date);
        final String advApproveBy = context.getString(R.string.advance_approve_by);
        final String advCancelBy = context.getString(R.string.advance_cancel_by);

        advanceNo.setText(String.format("%s: %s", jobAdvanceNo, args.getString(ADVANCE_NO)));
        jobNo.setText(String.format("%s %s: %s", job, no, args.getString(JOB_NO).equals("") ? notApplicable : args.getString(JOB_NO)));
        advanceDate.setText(String.format("%s: %s", advDate, args.getString(ADVANCE_DATE)));
        advanceTotal.setText(String.format("%s: %s", advTotal, decimalFormat.format(args.getDouble(ADVANCE_TOTAL))));
        advanceStatus.setText(String.format("%s: %s", advStatus, args.getString(ADVANCE_STATUS)));

        HashMap<String, Boolean> allStatus = (HashMap<String, Boolean>) args.getSerializable(STATUS_CONDITION);

        if (allStatus.get(JobAdvance.DISAPPROVED)) {

            cancelBy.setVisibility(View.VISIBLE);
            cancelBy.setText(String.format("%s: %s", advCancelBy, args.getString(ADVANCE_CANCEL_BY)));

        } else if (allStatus.get(JobAdvance.APPROVED)) {

            approveDate.setVisibility(View.VISIBLE);
            approveBy.setVisibility(View.VISIBLE);

            approveDate.setText(String.format("%s: %s", advApproveDate, args.getString(ADVANCE_APPROVE_DATE)));
            approveBy.setText(String.format("%s: %s", advApproveBy, args.getString(ADVANCE_APPROVE_BY)));

        }

    }

}
