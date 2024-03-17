package com.orbteknoloji.ptb.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.orbteknoloji.ptb.models.FragmentPagerModel;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<FragmentPagerModel> fragmentList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager fm) {

        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position).getFragment();
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentList.get(position).getFragmentTitle();
    }

    public void setFragmentPager(Fragment fragment, String fragmentTitle) {
        FragmentPagerModel model = new FragmentPagerModel();
        model.setFragment(fragment);
        model.setFragmentTitle(fragmentTitle);
        fragmentList.add(model);
    }
}