package com.example.oakkub.jobintern.Utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by OaKKuB on 8/27/2015.
 */
public class OrientationDetector {

    public static int getOrientation(Context context) {

        Point size = new Point();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        display.getSize(size);

        return size.x > size.y ? Configuration.ORIENTATION_LANDSCAPE : Configuration.ORIENTATION_PORTRAIT;

    }

}
