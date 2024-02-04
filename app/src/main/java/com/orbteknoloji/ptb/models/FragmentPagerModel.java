package com.orbteknoloji.ptb.models;

import androidx.fragment.app.Fragment;

public class FragmentPagerModel {
    private Fragment fragment;
    private String fragmentTitle;

    public FragmentPagerModel() {

    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }

    public String getFragmentTitle() {
        return fragmentTitle;
    }

    public void setFragmentTitle(String fragmentTitle) {
        this.fragmentTitle = fragmentTitle;
    }
}
