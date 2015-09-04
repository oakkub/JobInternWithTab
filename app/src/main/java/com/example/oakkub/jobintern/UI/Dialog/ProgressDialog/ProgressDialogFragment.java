package com.example.oakkub.jobintern.UI.Dialog.ProgressDialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by OaKKuB on 8/31/2015.
 */
public class ProgressDialogFragment extends DialogFragment {

    public ProgressDialogFragment() {
        super();
    }

    public static ProgressDialogFragment getInstance() {

        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();

        return progressDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        setCancelable(false);

        return MyProgressDialog.getInstance(getActivity());
    }
}
