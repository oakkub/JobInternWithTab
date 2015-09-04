package com.example.oakkub.jobintern.UI.Spinner;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by OaKKuB on 8/26/2015.
 */
public class SpinnerInteractionListener implements AdapterView.OnItemSelectedListener, View.OnTouchListener {

    private boolean isClick;
    private OnClickListener onClickListener;

    public SpinnerInteractionListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        isClick = true;
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (isClick) {

            onClickListener.onItemClick(parent, view, position, id);

            isClick = false;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        isClick = false;
    }

    public interface OnClickListener {
        void onItemClick(AdapterView<?> parent, View view, int position, long id);
    }
}
