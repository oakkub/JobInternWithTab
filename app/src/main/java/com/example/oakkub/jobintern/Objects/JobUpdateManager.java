package com.example.oakkub.jobintern.Objects;

import android.content.Context;

import com.example.oakkub.jobintern.R;

/**
 * Created by OaKKuB on 8/29/2015.
 */
public class JobUpdateManager {

    /*
    -1 = cannot updated such as internet problem
    0 = cannot updated such as job has already been proceeded to other status
    1 = updated successful
    */

    private int updateStatus;
    private String proceededStatus;

    public boolean cannotUpdate() {
        return updateStatus <= -1 || updateStatus > 1;
    }

    public boolean noUpdate() {
        return updateStatus == 0;
    }

    public boolean updateSuccessful() {
        return updateStatus == 1;
    }

    public String getStatusPlainText(Context context) {

        String[] jobTypes = context.getResources().getStringArray(R.array.entries_job_type_setting);

        switch (proceededStatus) {

            case "R":
                return jobTypes[0].toLowerCase();
            case "PP":
                return jobTypes[1].toLowerCase();
            case "D":
                return jobTypes[2].toLowerCase();
            case "A":
                return jobTypes[3].toLowerCase();

        }

        return "";
    }

}
