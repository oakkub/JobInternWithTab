package com.example.oakkub.jobintern.UI.ViewPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;

import com.example.oakkub.jobintern.Fragments.MainActivityFragment;
import com.example.oakkub.jobintern.R;

/**
 * Created by OaKKuB on 8/8/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int MAX_TAP = 4;
    private String[] entries, values;
    private SparseArray<Fragment> fragments;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        entries = context.getResources().getStringArray(R.array.entries_job_type_setting);
        values = context.getResources().getStringArray(R.array.value_job_type_setting);

        fragments = new SparseArray<>(MAX_TAP);

    }

    @Override
    public int getCount() {
        return MAX_TAP;
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = fragments.get(position);

        if (fragment == null) {

            switch (position) {

                case 0:
                    fragment = MainActivityFragment.getInstance(values[0]);
                    break;
                case 1:
                    fragment = MainActivityFragment.getInstance(values[1]);
                    break;
                case 2:
                    fragment = MainActivityFragment.getInstance(values[2]);
                    break;
                case 3:
                    fragment = MainActivityFragment.getInstance(values[3]);
                    break;
                default:
                    fragment = null;

            }

        }

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return entries[position];
    }
}
