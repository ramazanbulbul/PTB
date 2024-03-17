package com.orbteknoloji.ptb.services;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.orbteknoloji.ptb.BaseFragment;

public class FragmentService {
    public static void setFragmentTransaction(FragmentManager fragmentManager, int fragmentElement, BaseFragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(fragmentElement, fragment);
        transaction.addToBackStack(null); // Geri tuşuyla geri dönülebilmesini sağlar
        transaction.commit();
    }
}
