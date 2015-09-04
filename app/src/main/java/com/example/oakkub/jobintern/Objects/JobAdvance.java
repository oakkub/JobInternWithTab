package com.example.oakkub.jobintern.Objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.oakkub.jobintern.R;

import java.util.HashMap;

/**
 * Created by OaKKuB on 7/28/2015.
 */
public class JobAdvance implements Parcelable {

    public static final String REQUESTED = "com.example.oakkub.jobintern.Objects.JobAdvance.REQUESTED";
    public static final String POSTPONED = "com.example.oakkub.jobintern.Objects.JobAdvance.POSTPONED";
    public static final String DISAPPROVED = "com.example.oakkub.jobintern.Objects.JobAdvance.DISAPPROVED";
    public static final String APPROVED = "com.example.oakkub.jobintern.Objects.JobAdvance.APPROVED";
    public static final Parcelable.Creator<JobAdvance> CREATOR = new Parcelable.Creator<JobAdvance>() {
        @Override
        public JobAdvance createFromParcel(Parcel source) {
            return new JobAdvance(source);
        }

        @Override
        public JobAdvance[] newArray(int size) {
            return new JobAdvance[size];
        }
    };
    private int advanceId;
    private String advanceNo, advanceStatus, advanceDate, advanceApproveDate, advanceApproveBy, advanceCancelBy, jobNo;
    private double advanceTotal;

    private JobAdvance(Parcel source) {

        advanceId = source.readInt();
        advanceNo = source.readString();
        advanceStatus = source.readString();
        advanceDate = source.readString();
        advanceApproveDate = source.readString();
        advanceApproveBy = source.readString();
        advanceCancelBy = source.readString();
        jobNo = source.readString();
        advanceTotal = source.readDouble();

    }

    public int getAdvanceId() {
        return advanceId;
    }

    public String getAdvanceNo() {
        return advanceNo;
    }

    public String getAdvanceStatus() {

        return advanceStatus;

    }

    public boolean isStatusRequested() {
        return advanceStatus.equalsIgnoreCase("R");
    }

    public boolean isStatusPostponed() {
        return advanceStatus.equalsIgnoreCase("PP");
    }

    public boolean isStatusDisapproved() {
        return advanceStatus.equalsIgnoreCase("D");
    }

    public boolean isStatusApproved() {
        return advanceStatus.equalsIgnoreCase("A");
    }

    public HashMap<String, Boolean> getStatusAvailable() {

        HashMap<String, Boolean> allStatus = new HashMap<>();
        allStatus.put(REQUESTED, isStatusRequested());
        allStatus.put(POSTPONED, isStatusPostponed());
        allStatus.put(DISAPPROVED, isStatusDisapproved());
        allStatus.put(APPROVED, isStatusApproved());

        return allStatus;
    }

    public String getAdvanceStatusReadableFormat(Context context) {

        String[] jobTypes = context.getResources().getStringArray(R.array.entries_job_type_setting);

        if (isStatusRequested()) return jobTypes[0].toLowerCase();
        else if (isStatusPostponed()) return jobTypes[1].toLowerCase();
        else if (isStatusDisapproved()) return jobTypes[2].toLowerCase();
        else if (isStatusApproved()) return jobTypes[3].toLowerCase();
        else return "";

    }

    public String getJobNo() {
        return jobNo;
    }

    public double getAdvanceTotal() {
        return advanceTotal;
    }

    public String getAdvanceApproveDate() {
        return advanceApproveDate;
    }

    public String getAdvanceApproveBy() {
        return advanceApproveBy;
    }

    public String getAdvanceCancelBy() {
        return advanceCancelBy;
    }

    public String getAdvanceDate() {
        return advanceDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(advanceId);
        dest.writeString(advanceNo);
        dest.writeString(advanceStatus);
        dest.writeString(advanceDate);
        dest.writeString(advanceApproveDate);
        dest.writeString(advanceApproveBy);
        dest.writeString(advanceCancelBy);
        dest.writeString(jobNo);
        dest.writeDouble(advanceTotal);
    }

}
