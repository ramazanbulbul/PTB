package com.orbteknoloji.ptb.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;

public class PlanningFragment extends BaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_planning, container, false);

        return root;
    }
}
