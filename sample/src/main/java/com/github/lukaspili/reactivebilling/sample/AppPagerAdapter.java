package com.github.lukaspili.reactivebilling.sample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.github.lukaspili.reactivebilling.sample.inventory.InventoryFragment;
import com.github.lukaspili.reactivebilling.sample.shop.ShopFragment;

public class AppPagerAdapter extends FragmentStatePagerAdapter {
    public AppPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ShopFragment();
            case 1:
                return new InventoryFragment();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Shop";
            case 1:
                return "Inventory";
            default:
                throw new IllegalArgumentException();
        }
    }
}