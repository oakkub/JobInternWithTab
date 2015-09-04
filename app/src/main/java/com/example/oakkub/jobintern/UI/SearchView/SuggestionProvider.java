package com.example.oakkub.jobintern.UI.SearchView;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by OaKKuB on 8/27/2015.
 */
public class SuggestionProvider extends SearchRecentSuggestionsProvider {

    public static final String AUTHORITY = "com.example.oakkub.jobintern.UI.SearchView.SuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
