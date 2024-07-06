package com.orbteknoloji.ptb;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;

import com.orbteknoloji.ptb.services.SQLLiteService;

public class BaseFragment extends Fragment {
    public Context _context = null;
    public Activity _activity = null;
    public SQLLiteService _db;
    public BaseFragment() {

    }
}
