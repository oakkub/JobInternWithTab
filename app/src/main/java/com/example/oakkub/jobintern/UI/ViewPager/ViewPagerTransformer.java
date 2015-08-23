package com.example.oakkub.jobintern.UI.ViewPager;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

/**
 * Created by OaKKuB on 8/9/2015.
 */
public class ViewPagerTransformer implements ViewPager.PageTransformer {

    public static final int ALPHA = 1;
    public static final int SHRINK = 2;
    public static final int DEPTH = 3;

    private static final float MIN_SCALE = 0.85f;
    private static final float MIN_ALPHA = 0.5f;

    private int type;

    @Override
    public void transformPage(View page, float position) {

        slideAlpha(page, position);
    }

    private void slideAlpha(View page, float position) {
        page.setAlpha(position > 1 || position < -1 ? 0 : 1 - (Math.abs(position) * 1.05f));
    }

    private void slideShrinkAlpha(View page, float position) {

        if(position > 1 || position < - 1) page.setAlpha(0);
        else if(position <= 1) {

            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float verticalMargin = pageHeight * (1 - scaleFactor) / 2;
            float horizontalMargin = pageWidth * (1 - scaleFactor) / 2;

            if(position < 0) page.setTranslationX(horizontalMargin - verticalMargin / 2);
            else page.setTranslationY(-horizontalMargin + verticalMargin / 2);

            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        }

    }

    private void slideDepth(View page, float position) {

        int pageWidth = page.getWidth();

        if(position > 1 || position < - 1) page.setAlpha(0);
        else if(position <= 0) { // when slide to left
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);
        } else if(position <= 1) {

            page.setAlpha(1 - position);
            page.setTranslationX(pageWidth * -position);

            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            Log.i("SCALE FACTOR", String.valueOf(scaleFactor));
        }

    }

}
