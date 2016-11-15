package ch.unibas.ccn_lite_android.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ch.unibas.ccn_lite_android.fragments.TabFragment_Chart;
import ch.unibas.ccn_lite_android.fragments.TabFragment_Chart_Month;
import ch.unibas.ccn_lite_android.fragments.TabFragment_Chart_Week;

/**
 * Created by maria on 2016-10-19.
 */

public class FragmentPagerAdapter_Chart extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Day", "Week", "Month" };
    private Context context;

    public FragmentPagerAdapter_Chart(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0)
            return TabFragment_Chart.newInstance(position);
        else if(position == 1)
            return TabFragment_Chart_Week.newInstance(position);
        else
            return TabFragment_Chart_Month.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}