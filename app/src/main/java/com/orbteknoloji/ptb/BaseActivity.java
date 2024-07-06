package com.orbteknoloji.ptb;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.orbteknoloji.ptb.services.SQLLiteService;

public class BaseActivity extends AppCompatActivity {
    public Context _context;
    public Activity _activity;
    boolean doubleBackToExitPressedOnce = false;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    public SQLLiteService _db;
    public BaseActivity() {
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (doubleBackToExitPressedOnce){
            finishAffinity();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Çıkmak için tekrar geri tuşuna basın", Toast.LENGTH_SHORT).show();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        };

        mHandler.postDelayed(mRunnable, 2000); // 2 saniye boyunca bekle
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
