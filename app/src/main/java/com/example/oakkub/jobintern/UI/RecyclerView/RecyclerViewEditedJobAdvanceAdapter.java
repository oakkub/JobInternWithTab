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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RecyclerViewEditedJobAdvanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_PROGRESS_BAR = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_END_RESULT = 2;

    private final List<JobAdvance> jobAdvances;
    private JobAdvanceClickListener jobAdvanceClickListener;
    private String fetchCondition;

    private final DecimalFormat advanceTotalDecimalFormat;
    private boolean isEndOfResult = false;

    public RecyclerViewEditedJobAdvanceAdapter(String fetchCondition) {

        this.fetchCondition = fetchCondition;

        jobAdvances = new ArrayList<>();
        advanceTotalDecimalFormat = new DecimalFormat("0.00");
    }

    public void setJobAdvanceClickListener(JobAdvanceClickListener jobAdvanceClickListener) {
        this.jobAdvanceClickListener = jobAdvanceClickListener;
    }

    public void setFetchCondition(String fetchCondition) {
        this.fetchCondition = fetchCondition;
    }

    public void setEndOfResult(boolean isEndOfResult) {
        this.isEndOfResult = isEndOfResult;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;

        switch(viewType) {

            case VIEW_TYPE_ITEM:

                viewHolder = new JobDetailsViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.main_card_view, viewGroup, false), this);

                break;

            case VIEW_TYPE_PROGRESS_BAR:

                viewHolder = new ProgressBarViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.progress_dialog, viewGroup, false));

                break;

            case VIEW_TYPE_END_RESULT:

                viewHolder = new EndOfResultViewHolder(LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.no_data_card_view, viewGroup, false));
                break;

        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        if(jobAdvances.get(position) != null) {

            JobAdvance jobAdvance = jobAdvances.get(position);

            ((JobDetailsViewHolder) viewHolder).textViews.get(0).setText("Job Advance No: " + jobAdvance.getAdvanceNo());
            ((JobDetailsViewHolder) viewHolder).textViews.get(1).setText(String.valueOf(advanceTotalDecimalFormat.format(jobAdvance.getAdvanceTotal())));

        } else {

            if(isEndOfResult) ((EndOfResultViewHolder) viewHolder).endOfResultTextView.setVisibility(View.VISIBLE);
            else ((ProgressBarViewHolder) viewHolder).progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return jobAdvances.size();
    }

    @Override
    public int getItemViewType(int position) {

        // we gonna add null item, if we want the progress bar or end result text.
        if(jobAdvances.get(position) != null) return VIEW_TYPE_ITEM;
        else return isEndOfResult ? VIEW_TYPE_END_RESULT : VIEW_TYPE_PROGRESS_BAR;
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

    public void removeLast() {
        if(getItemCount() > 0) removeItem(getItemCount() - 1);
    }

    public void removeAllItem() {

        int size = jobAdvances.size();
        jobAdvances.clear();
        notifyItemRangeRemoved(0, size);

    }

    public void addAllItem(List<JobAdvance> jobAdvances) {

        int sizeBeforeAdd = getItemCount();
        this.jobAdvances.addAll(jobAdvances);
        notifyItemRangeInserted(sizeBeforeAdd, jobAdvances.size());
    }

    public boolean isEndOfResult() { return isEndOfResult; }

    static class EndOfResultViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.noDataCardViewTextView) TextView endOfResultTextView;

        public EndOfResultViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.progressBar) ProgressBar progressBar;

        public ProgressBarViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    static class JobDetailsViewHolder extends RecyclerView.ViewHolder {

        @Bind({R.id.advanceNoCardTextView, R.id.advanceTotalCardTextView})
        List<TextView> textViews;
        @Bind({R.id.approveJobAdvanceCardButton, R.id.postponeJobAdvanceCardButton, R.id.disapproveJobAdvanceCardButton})
        List<Button> buttons;

        private final RecyclerViewEditedJobAdvanceAdapter parent;

        public JobDetailsViewHolder(View itemView, RecyclerViewEditedJobAdvanceAdapter parent) {
            super(itemView);

            this.parent = parent;

            ButterKnife.bind(this, itemView);

            switch (parent.fetchCondition) {

                case "Approved":
                case "Disapproved":

                    for(int i = 0, size = buttons.size(); i < size; i ++) buttons.get(i).setVisibility(View.GONE);

                    break;

                case "Postponed":

                    buttons.get(1).setVisibility(View.GONE);

                    break;

            }

        }

        @OnClick(R.id.approveJobAdvanceCardButton)
        public void onApproveClick() {
            parent.jobAdvanceClickListener.approveClick(getAdapterPosition());
        }

        @OnClick(R.id.postponeJobAdvanceCardButton)
        public void onPostponeClick() {
            parent.jobAdvanceClickListener.postponeClick(getAdapterPosition());
        }

        @OnClick(R.id.disapproveJobAdvanceCardButton)
        public void onDisapproveClick() {
            parent.jobAdvanceClickListener.disapproveClick(getAdapterPosition());
        }

    }

    public interface JobAdvanceClickListener {

        void approveClick(int position);
        void postponeClick(int position);
        void disapproveClick(int position);

    }

}
