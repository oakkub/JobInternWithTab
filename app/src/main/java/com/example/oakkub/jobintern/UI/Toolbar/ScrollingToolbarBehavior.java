package com.example.oakkub.jobintern.UI.Toolbar;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by OaKKuB on 8/14/2015.
 */
public class ScrollingToolbarBehavior extends CoordinatorLayout.Behavior<Toolbar> {

    public ScrollingToolbarBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, Toolbar toolbar, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, Toolbar toolbar, View dependency) {

        if(dependency instanceof AppBarLayout) {

            CoordinatorLayout.LayoutParams params = ((CoordinatorLayout.LayoutParams) toolbar.getLayoutParams());

            int toolbarMarginTop = params.topMargin;
            int distanceToScroll = toolbar.getHeight() + toolbarMarginTop;
            float ratio = dependency.getY() / toolbar.getHeight();
            toolbar.setTranslationY(-distanceToScroll * ratio);
        }

        return true;
    }
}
