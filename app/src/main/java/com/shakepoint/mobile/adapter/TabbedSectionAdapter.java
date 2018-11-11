package com.shakepoint.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shakepoint.mobile.fragment.ActiveCodesFragment;
import com.shakepoint.mobile.fragment.ProductsFragment;
import com.shakepoint.mobile.fragment.PurchasesFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public class TabbedSectionAdapter extends FragmentPagerAdapter {
    private static List<Fragment> fragments;

    static {
        fragments = new ArrayList<>();
        fragments.add(ProductsFragment.newInstance());
        fragments.add(PurchasesFragment.newInstance());
        fragments.add(ActiveCodesFragment.newInstance());
    }


    public TabbedSectionAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "PRODUCTOS";
            case 1:
                return "COMPRAS";
            case 2:
                return "CODIGOS ACTIVOS";
        }
        return null;
    }
}
