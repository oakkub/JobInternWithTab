package com.example.oakkub.jobintern.UI.ProgressDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.oakkub.jobintern.R;

/**
 * Created by OaKKuB on 7/11/2015.
 */
public class MyProgressDialog extends ProgressDialog {

    public MyProgressDialog(Context context) {
        super(context);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // use custom progress dialog layout
        setContentView(R.layout.progress_dialog);

        // set dim screen to 10%
        /*WindowManager.LayoutParams windowManagerLayoutParams = getWindow().getAttributes();
        windowManagerLayoutParams.dimAmount = 0.1f;*/

    }

}
