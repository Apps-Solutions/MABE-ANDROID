package com.sellcom.tracker;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juanc.jimenez on 26/06/14.
 */
public class FragmentInventory extends Fragment {
    static Context context;
    public static final String TAG = "inventory";

    static ViewPagerAdapter mAdapter;
    static ViewPager mPager;
    static String[] titles;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(NavigationDrawerFragment.INVENTORY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = getResources().getStringArray(R.array.inventory_titles);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_inventory, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new ViewPagerAdapter(getChildFragmentManager());

        mPager = (ViewPager) getView().findViewById(R.id.view_pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
        tabs.setViewPager(mPager);
    }

    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        FragmentManager fragmentManager;
        List<Fragment> fragmentList = new ArrayList<Fragment>();

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragmentManager = fm;
            //fragmentList.add(FragmentInventoryInOut.newInstance(true));
            /* Se deja comentada la parte de SALIDAS de inventario, para solo mostrar las entradas...

            fragmentList.add(FragmentInventoryInOut.newInstance(false));
            */
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public Fragment getItem(int num) {
            return fragmentList.get(num);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
