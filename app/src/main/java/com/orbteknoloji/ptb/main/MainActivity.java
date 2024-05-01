package com.orbteknoloji.ptb.main;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.orbteknoloji.ptb.BaseActivity;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.adapters.PlanAdapter;
import com.orbteknoloji.ptb.adapters.ViewPagerAdapter;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.helpers.FragmentHelper;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.services.BluetoothService;

import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import me.ibrahimsn.lib.OnItemReselectedListener;
import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends BaseActivity {
    private static final int REQUEST_ENABLE_BT = 10001;
    private static final int REQUEST_COARSE_LOCATION = 10002;
    private static final int REQUEST_BLUETOOTH_CONNECT = 10003;
    private Handler headerHandler;
    private Runnable headerRunnable;
    private int selectedBottomBar = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = this;
        _activity = this;

        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_ENABLE_BT);
        }
        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
        }

        LinearLayout fragment = findViewById(R.id.fragment);
        SmoothBottomBar bottomBar = findViewById(R.id.bottomBar);

        Intent intent = getIntent();
        String TAB_PAGE = intent.getStringExtra("TAB");
        boolean isUpdate = intent.getBooleanExtra("IS_UPDATE", false);
        selectedBottomBar = Integer.parseInt(TAB_PAGE != null && !TAB_PAGE.isEmpty() ? TAB_PAGE : "0");
        bottomBar.setItemActiveIndex(selectedBottomBar);
        setTabPage(TAB_PAGE != null, isUpdate);
        bottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                selectedBottomBar = i;
                setTabPage(true, false);
                return true;
            }
        });
        RelativeLayout header = findViewById(R.id.header);
        ImageView headerIcon = findViewById(R.id.headerIcon);
        TextView headerText = findViewById(R.id.headerText);

        //      HAREKETLI HEADER
        headerHandler = new Handler();
        headerRunnable = new Runnable() {
            @Override
            public void run() {
                if (headerIcon.getVisibility() == View.INVISIBLE){

                    ObjectAnimator.ofFloat(headerText, "alpha", 1f, 0f).setDuration(1000).start();

                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            headerText.setVisibility(View.INVISIBLE);
                            headerIcon.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(headerIcon, "alpha", 0f, 1f).setDuration(1000).start();
                        }
                    }, 1000);
                }else if (headerIcon.getVisibility() == View.VISIBLE){
                    ObjectAnimator.ofFloat(headerIcon, "alpha", 1f, 0f).setDuration(1000).start();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            headerIcon.setVisibility(View.INVISIBLE);
                            headerText.setVisibility(View.VISIBLE);
                            ObjectAnimator.ofFloat(headerText, "alpha", 0f, 1f).setDuration(1000).start();
                        }
                    }, 1000);
                }
                headerHandler.postDelayed(this, 3000);
            }
        };
        headerHandler.postDelayed(headerRunnable, 2000);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTabPage(true, false);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Bluetooth izni verildi, işleme devam et
                } else {
                    AlertHelper.ShowAlertDialog(_context,
                            AlertType.ERROR,
                            "Erişim İzni Hatası",
                            "Bluetooth izini verilmeden devam edilemez!",
                            "Erişim İzni Ver",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                                }
                            },
                            "Çık",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            },
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                                }
                            });
                }
                break;
            case REQUEST_COARSE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Konum izni verildi, işleme devam et
                } else {
                    AlertHelper.ShowAlertDialog(_context,
                            AlertType.ERROR,
                            "Erişim İzni Hatası",
                            "Konum izini verilmeden devam edilemez!",
                            "Erişim İzni Ver",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                                }
                            },
                            "Çık",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            },
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
                                }
                            });
                }
                break;
            case REQUEST_BLUETOOTH_CONNECT:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Erişim İzni Hatası", "Yakındaki cihazlar izini verilmeden devam edilemez!",
                            "Erişim İzni Ver",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                                    }                                }
                            },
                            "Çık",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            },
                            true,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialogInterface) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                                    }                                }
                            });
                }
                break;

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (headerHandler != null)
            headerHandler.removeCallbacks(headerRunnable);
    }
    public void setTabPage(boolean reset, boolean isUpdate){
        if (reset){
            if (BluetoothService.isConnected()){
                BluetoothService.sendData(_context,0xfd);
            }
        }
        switch (selectedBottomBar){
            case 0:
                FragmentHelper.setFragmentTransaction(getSupportFragmentManager(), R.id.fragment, new OnlineControlFragment());
                break;
            case 1:
                FragmentHelper.setFragmentTransaction(getSupportFragmentManager(), R.id.fragment, new PlanningFragment(isUpdate));
                break;
        }
    }
}