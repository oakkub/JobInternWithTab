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
    private String[] type;

    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        type = context.getResources().getStringArray(R.array.value_job_type_setting);
    }

    @Override
    public int getCount() {
        return MAX_TAP;
    }

    @Override
    public Fragment getItem(int position) {
        /*switch (position) {

            case 0:
                return RequestedFragment.getInstance(type[position]);
            case 1:
                return PostponedFragment.getInstance(type[position]);
            case 2:
                return DisapprovedFragment.getInstance(type[position]);
            case 3:
                return ApprovedFragment.getInstance(type[position]);
            default:
                return RequestedFragment.getInstance(type[position]);

        }*/
        return MainActivityFragment.getInstance(type[position]);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return type[position];
    }
}
