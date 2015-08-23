package com.example.oakkub.jobintern.UI.RecyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by OaKKuB on 8/3/2015.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[] { android.R.attr.listDivider };

    private Drawable dividerDrawable;

    public DividerItemDecoration(Context context) {

        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);

        dividerDrawable = styledAttributes.getDrawable(0);
        styledAttributes.recycle();

    }

    /**
     * Custom divider will be used
     * @param context
     * @param resId
     */
    public DividerItemDecoration(Context context, int resId) {

        dividerDrawable = ContextCompat.getDrawable(context, resId);

    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for(int i = 0; i < childCount; i++) {

            View child = parent.getChildAt(0);

            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + dividerDrawable.getIntrinsicHeight();

            dividerDrawable.setBounds(left, top, right, bottom);
            dividerDrawable.draw(canvas);

        }
    }
}
