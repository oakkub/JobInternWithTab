package com.example.oakkub.jobintern.UI.SearchView;

import android.os.Bundle;
import android.widget.SearchView;

/**
 * Created by OaKKuB on 8/22/2015.
 */
public class SearchViewStateManager {

    private static final String SEARCH_VIEW_QUERY = "com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager.SEARCH_VIEW_QUERY";
    private static final String SEARCH_VIEW_ICONIFIED = "com.example.oakkub.jobintern.UI.SearchView.SearchViewStateManager.SEARCH_VIEW_ICONIFIED";

    private SearchView searchView;

    private String searchQuery;
    private boolean hasFocus;

    public SearchViewStateManager(SearchView searchView) {
        this.searchView = searchView;
    }

    public void onSavedInstanceState(Bundle outState) {
        if(!searchView.isIconified()) {
            outState.putString(SEARCH_VIEW_QUERY, searchView.getQuery().toString());
            outState.putBoolean(SEARCH_VIEW_ICONIFIED, searchView.isIconified());
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            if(savedInstanceState.containsKey(SEARCH_VIEW_QUERY)) {
                searchQuery = savedInstanceState.getString(SEARCH_VIEW_QUERY);
                hasFocus = !savedInstanceState.getBoolean(SEARCH_VIEW_ICONIFIED);
            }
        }
    }

    public void setPreviousState(SearchView searchView) {
        if(hasFocus) {
            searchView.setQuery(searchQuery, false);
            searchView.setIconified(false);

            searchQuery = "";
            hasFocus = false;
        }
        this.searchView = searchView;
    }

}
