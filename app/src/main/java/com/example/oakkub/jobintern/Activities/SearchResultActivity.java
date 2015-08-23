package com.example.oakkub.jobintern.Activities;

import android.app.SearchManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SearchView;

import com.example.oakkub.jobintern.Fragments.SearchResultFragment;
import com.example.oakkub.jobintern.R;
import com.example.oakkub.jobintern.Utilities.UtilString;

public class SearchResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        // this gets call when user pressed go button in search view
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {

            String query = intent.getStringExtra(SearchManager.QUERY).trim();
            if (query.isEmpty()) return;

            SearchResultFragment searchResultFragment = getSearchResultFragment();
            if (searchResultFragment != null) {

                searchResultFragment.updateSearchResult(
                        PreferenceManager.getDefaultSharedPreferences(this)
                                .getString(UtilString.PREF_SEARCH_TYPE,
                                            getString(R.string.default_value_job_type_setting)),
                                                query);
            }

        }

    }

    @Override
    public void onBackPressed() {

        SearchView searchView = getSearchResultFragment().getSearchView();
        if(!searchView.isIconified()) searchView.setIconified(true);
        else super.onBackPressed();
    }

    public SearchResultFragment getSearchResultFragment() {
        return (SearchResultFragment) getSupportFragmentManager().findFragmentById(R.id.searchFragment);
    }

}
