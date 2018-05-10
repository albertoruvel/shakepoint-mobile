package com.shakepoint.mobile.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shakepoint.mobile.fragment.NutritionalDataFragment;
import com.shakepoint.mobile.fragment.ProductDetailsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose.rubalcaba on 02/13/2018.
 */

public class ProductDetailsAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;

    public ProductDetailsAdapter(FragmentManager fm, String productId) {
        super(fm);
        fragments = new ArrayList();
        fragments.add(ProductDetailsFragment.newInstance(productId));
        fragments.add(NutritionalDataFragment.newInstance());
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
        switch(position){
            case 0: return "Detalles";
            case 1: return "Nutrientes";
            default: return "";
        }
    }
}
