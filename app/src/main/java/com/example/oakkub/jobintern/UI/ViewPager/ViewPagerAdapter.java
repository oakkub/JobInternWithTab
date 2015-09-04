package com.example.oakkub.jobintern.UI.ViewPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.oakkub.jobintern.Fragments.MainActivityFragment;
import com.example.oakkub.jobintern.R;

/**
 * Created by OaKKuB on 8/8/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAX_TAP = 4;
    private String[] entries, values;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        entries = context.getResources().getStringArray(R.array.entries_job_type_setting);
        values = context.getResources().getStringArray(R.array.value_job_type_setting);

    }

    @Override
    public int getCount() {
        return MAX_TAP;
    }

    @Override
    public Fragment getItem(int position) {
        return MainActivityFragment.getInstance(values[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return entries[position];
    }
}
