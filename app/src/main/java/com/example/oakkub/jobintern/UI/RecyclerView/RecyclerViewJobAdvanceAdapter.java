package com.example.oakkub.jobintern.UI.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.oakkub.jobintern.Objects.JobAdvance;
import com.example.oakkub.jobintern.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by OaKKuB on 7/27/2015.
 */
public class RecyclerViewJobAdvanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_PROGRESS_BAR = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_NO_ITEM = 2;

    private List<JobAdvance> jobAdvances;
    private JobAdvanceClickListener jobAdvanceClickListener;
    private String fetchCondition;

    private DecimalFormat advanceTotalDecimalFormat;
    private boolean isFooterEnabled = true, isEndOfResult = false;

    public RecyclerViewJobAdvanceAdapter(String fetchCondition) {

        this.fetchCondition = fetchCondition;

        jobAdvances = new ArrayList<>();
        advanceTotalDecimalFormat = new DecimalFormat("0.00");
    }

    public void setJobAdvanceClickListener(JobAdvanceClickListener jobAdvanceClickListener) {
        this.jobAdvanceClickListener = jobAdvanceClickListener;
    }

    /**
     * Enable or disable footer (Default is true)
     *
     * @param isFooterEnabled boolean to turn on or off footer.
     */
    public void setFooterEnabled(boolean isFooterEnabled) {
        this.isFooterEnabled = isFooterEnabled;
    }

    public void setEndOfResult(boolean isEndOfResult) {
        this.isEndOfResult = isEndOfResult;
    }

    public boolean isFooterEnabled() {
        return isFooterEnabled;
    }

    @Override
     public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;

        if(viewType == VIEW_TYPE_ITEM) {

            View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.main_card_view, viewGroup, false);
            viewHolder = new JobDetailsViewHolder(view, this);

        } else if(viewType == VIEW_TYPE_PROGRESS_BAR) {

            View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.progress_dialog, viewGroup, false);
            viewHolder = new ProgressBarViewHolder(view);

        } else {

            View view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.no_data_card_view, viewGroup, false);
            viewHolder = new EndOfResultViewHolder(view);

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(jobAdvances.size() > 0 && position < jobAdvances.size()) {

            JobAdvance jobAdvance = jobAdvances.get(position);

            ((JobDetailsViewHolder) viewHolder).advanceNoTextView.setText("Job Advance No: " + jobAdvance.getAdvanceNo());
            ((JobDetailsViewHolder) viewHolder).advanceTotalTextView.setText(String.valueOf(advanceTotalDecimalFormat.format(jobAdvance.getAdvanceTotal())));

        } else {

            if(isEndOfResult) ((EndOfResultViewHolder) viewHolder).endOfResultTextView.setVisibility(View.VISIBLE);
            else ((ProgressBarViewHolder) viewHolder).progressBar.setIndeterminate(true);

        }

    }

    public int getActualSize() {
        return jobAdvances.size();
    }

    @Override
    public int getItemCount() {
        return isFooterEnabled ? jobAdvances.size() + 1 : jobAdvances.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(isFooterEnabled && position >= jobAdvances.size()) {
            if(isEndOfResult) return VIEW_TYPE_NO_ITEM;
            else return VIEW_TYPE_PROGRESS_BAR;
        } else {
            return VIEW_TYPE_ITEM;
        }

        //return (isFooterEnabled && position >= jobAdvances.size()) ? VIEW_TYPE_PROGRESS_BAR : VIEW_TYPE_ITEM ;
    }

    public JobAdvance getItem(int position) {
        return jobAdvances.get(position);
    }

    public int getItemSize() {
        return jobAdvances.size();
    }

    public void addItem(JobAdvance jobAdvance) {

        jobAdvances.add(jobAdvance);
        notifyItemInserted(getItemCount() - 1);
    }

    public void removeItem(int position) {

        jobAdvances.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAllItem() {

        int size = jobAdvances.size();
        if(size == 0) return;

        jobAdvances.removeAll(jobAdvances);
        notifyItemRangeRemoved(0, size);

    }

    public void addAllItem(List<JobAdvance> jobAdvances) {

        int sizeBeforeAdd = getItemCount();
        this.jobAdvances.addAll(jobAdvances);
        notifyItemRangeInserted(sizeBeforeAdd, jobAdvances.size());
    }

    public static class EndOfResultViewHolder extends RecyclerView.ViewHolder {

        private TextView endOfResultTextView;

        public EndOfResultViewHolder(View itemView) {
            super(itemView);

            endOfResultTextView = (TextView) itemView.findViewById(R.id.noDataCardViewTextView);
        }
    }

    private static class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;

        public ProgressBarViewHolder(View itemView) {
            super(itemView);

            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
        }
    }

    private static class JobDetailsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView advanceNoTextView, advanceTotalTextView;
        private Button approveJobAdvanceButton, cancelJobAdvanceButton, postponeJobAdvanceButton;

        private RecyclerViewJobAdvanceAdapter parent;

        public JobDetailsViewHolder(View itemView, RecyclerViewJobAdvanceAdapter parent) {
            super(itemView);

            this.parent = parent;

            advanceNoTextView = (TextView) itemView.findViewById(R.id.advanceNoCardTextView);
            advanceTotalTextView = (TextView) itemView.findViewById(R.id.advanceTotalCardTextView);
            approveJobAdvanceButton = (Button) itemView.findViewById(R.id.approveJobAdvanceCardButton);
            postponeJobAdvanceButton = (Button) itemView.findViewById(R.id.postponeJobAdvanceCardButton);
            cancelJobAdvanceButton = (Button) itemView.findViewById(R.id.disapproveJobAdvanceCardButton);

            switch (parent.fetchCondition) {

                case "Approved":
                case "Disapproved":

                    approveJobAdvanceButton.setVisibility(View.GONE);
                    postponeJobAdvanceButton.setVisibility(View.GONE);
                    cancelJobAdvanceButton.setVisibility(View.GONE);

                    break;

                case "Postponed":

                    postponeJobAdvanceButton.setVisibility(View.GONE);

                    break;

            }

            if(approveJobAdvanceButton.getVisibility() != View.GONE) approveJobAdvanceButton.setOnClickListener(this);
            if(postponeJobAdvanceButton.getVisibility() != View.GONE) postponeJobAdvanceButton.setOnClickListener(this);
            if(cancelJobAdvanceButton.getVisibility() != View.GONE) cancelJobAdvanceButton.setOnClickListener(this);

        }

        @Override
        public void onClick(final View view) {

            switch(view.getId()) {

                case R.id.postponeJobAdvanceCardButton:

                    parent.jobAdvanceClickListener.postponeClick(getAdapterPosition());
                    return;

                case R.id.disapproveJobAdvanceCardButton:

                    parent.jobAdvanceClickListener.disapproveClick(getAdapterPosition());
                    return;

                case R.id.approveJobAdvanceCardButton:

                    parent.jobAdvanceClickListener.approveClick(getAdapterPosition());
                    return;

            }

        }


    }

    public interface JobAdvanceClickListener {

        void approveClick(int position);
        void postponeClick(int position);
        void disapproveClick(int position);

    }

}
