package com.example.oakkub.jobintern.UI.RecyclerView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by OaKKuB on 7/27/2015.
 */
public class RecyclerViewListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener onItemClickListener;
    private GestureDetector gestureDetector;

    public RecyclerViewListener(Context context,final RecyclerView recyclerView, final OnItemClickListener onItemClickListener) {

        this.onItemClickListener = onItemClickListener;

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {

                return onItemClickListener != null;
            }
        });

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {

        View childView = recyclerView
                .findChildViewUnder(e.getX(), e.getY());

        if(childView != null &&
                onItemClickListener != null &&
                gestureDetector.onTouchEvent(e)) {

            // childView: the item in recycler view
            onItemClickListener.onItemClick(childView, recyclerView.getChildAdapterPosition(childView));

            return true;
        }

        return false;

    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}


