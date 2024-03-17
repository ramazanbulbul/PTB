package com.orbteknoloji.ptb.helpers;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.main.OnlineControlFragment;

public class FragmentHelper {
    public static void setFragmentTransaction(FragmentManager fragmentManager, int fragmentElement, BaseFragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentElement, fragment);
        transaction.addToBackStack(null); // Geri tuşuyla geri dönülebilmesini sağlar
        transaction.commit();
    }
}
