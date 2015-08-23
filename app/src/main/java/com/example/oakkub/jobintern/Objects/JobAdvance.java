package com.example.oakkub.jobintern.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * Created by OaKKuB on 7/28/2015.
 */
public class JobAdvance {

    private int advanceId;
    private String advanceNo, advanceStatus;
    private double advanceTotal;

    public int getAdvanceId() {
        return advanceId;
    }

    public String getAdvanceNo() {
        return advanceNo;
    }

    public String getAdvanceStatus() {

        return advanceStatus;

    }

    public double getAdvanceTotal() {
        return advanceTotal;
    }

    public String getReadableAdvanceStatus() {

        switch(advanceStatus) {

            case "R":

                return "Requested";

            case "PP":

                return "Postponed";

            case "D":

                return "Disapproved";

            case "A":

                return "Approved";

            default:

                return advanceStatus;

        }

    }

}
