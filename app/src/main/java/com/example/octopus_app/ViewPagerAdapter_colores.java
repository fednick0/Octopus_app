package com.example.octopus_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter_colores extends FragmentStatePagerAdapter {

    ArrayList<Fragment> fragmentList = new ArrayList<>();
    ArrayList<String> tabTitle = new ArrayList<>();

    public ViewPagerAdapter_colores(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle.get(position);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void setPageTitle(ArrayList<String> tabTitle_) {
        tabTitle = tabTitle_;
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    public void removeFragment() {
        fragmentList.clear();
        tabTitle.clear();
    }
}
