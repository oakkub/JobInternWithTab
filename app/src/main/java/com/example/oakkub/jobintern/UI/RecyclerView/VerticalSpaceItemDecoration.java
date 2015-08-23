package com.example.oakkub.jobintern.UI.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by OaKKuB on 8/1/2015.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int VERTICAL_SPACE_HEIGHT;

    public VerticalSpaceItemDecoration(int VERTICAL_SPACE_HEIGHT) {

        this.VERTICAL_SPACE_HEIGHT = VERTICAL_SPACE_HEIGHT;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = VERTICAL_SPACE_HEIGHT;
    }
}
