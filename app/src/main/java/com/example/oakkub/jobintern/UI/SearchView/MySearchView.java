package com.example.oakkub.jobintern.UI.SearchView;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.SearchView;

import java.util.List;

/**
 * Created by OaKKuB on 8/22/2015.
 */
public class MySearchView extends SearchView {

    public MySearchView(Context context) {
        super(context);
    }

    public MySearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Parcelable onSaveInstanceState() {

        /*if(!isIconified()) {
            searchQuery = getQuery().toString();
            hasQuery = true;
            isIconified = false;
        } else {
            hasQuery = false;
            isIconified = true;
        }*/

        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.searchQuery = getQuery().toString();
        savedState.isIconified = isIconified() ? 0 : 1;

        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        /*if(hasQuery && !isIconified) {

            if(!searchQuery.equals("")) {
                setQuery(searchQuery, false);
                setIconified(false);
            } else {
                setIconified(false);
            }

            hasQuery = false;
            isIconified = true;
            searchQuery = "";
        }*/

        Log.i("SEARCH VIEW", "onRestoreInstanceState");


        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());

        if(savedState.isIconified == 1) {
            setIconified(false);
            setQuery(savedState.searchQuery, false);
        }

        Log.i("SEARCH VIEW", "onRestoreInstanceState:" + savedState.isIconified + ", " + savedState.searchQuery);

    }

    static class SavedState extends BaseSavedState {

        private String searchQuery;
        private int isIconified;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel source) {
            super(source);
            searchQuery = source.readString();
            isIconified = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(searchQuery);
            dest.writeInt(isIconified);
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
