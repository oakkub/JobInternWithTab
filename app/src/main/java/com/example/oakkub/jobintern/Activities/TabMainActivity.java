package com.example.oakkub.jobintern.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.oakkub.jobintern.Fragments.TabMainActivityFragment;
import com.example.oakkub.jobintern.R;

public class TabMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_main);
    }

    @Override
    public void onBackPressed() {

        TabMainActivityFragment tabMainActivityFragment = (TabMainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.tab_main_fragment);
        if (tabMainActivityFragment.canExit()) super.onBackPressed();
    }


}
