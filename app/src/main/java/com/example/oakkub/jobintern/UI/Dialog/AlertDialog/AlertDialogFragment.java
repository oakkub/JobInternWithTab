package com.example.oakkub.jobintern.UI.Dialog.AlertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.example.oakkub.jobintern.R;

/**
 * Created by OaKKuB on 8/31/2015.
 */
public class AlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {

    private static String TITLE_KEY = "title";
    private static String MESSAGE_KEY = "message";
    private static String POSITIVE_BUTTON_KEY = "positiveButton";

    private YesNoListener yesNoListener;

    public AlertDialogFragment() {
        super();
    }

    public static AlertDialogFragment getInstance(String title, String message, String positiveButton) {

        AlertDialogFragment alertDialogFragment = new AlertDialogFragment();

        Bundle args = new Bundle();
        args.putString(TITLE_KEY, title);
        args.putString(MESSAGE_KEY, message);
        args.putString(POSITIVE_BUTTON_KEY, positiveButton);

        alertDialogFragment.setArguments(args);

        return alertDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            yesNoListener = (YesNoListener) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement YesNoListener interface");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        return new AlertDialog.Builder(getActivity())
                .setTitle(args.getString(TITLE_KEY))
                .setMessage(args.getString(MESSAGE_KEY))
                .setPositiveButton(args.getString(POSITIVE_BUTTON_KEY), this)
                .setNegativeButton(getString(R.string.cancel), this)
                .create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (yesNoListener != null) {

            switch (which) {

                case DialogInterface.BUTTON_POSITIVE:
                    yesNoListener.onYes(getTag());
                    dismiss();
                    break;
                default:
                    yesNoListener.onNo(getTag());
                    break;

            }
        }
    }

    public interface YesNoListener {
        void onYes(final String tag);

        void onNo(final String tag);
    }
}
