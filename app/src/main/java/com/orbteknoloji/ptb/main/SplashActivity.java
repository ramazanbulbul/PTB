package com.orbteknoloji.ptb.main;

import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_FINISHED;
import static android.bluetooth.BluetoothAdapter.ACTION_DISCOVERY_STARTED;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.orbteknoloji.ptb.BaseActivity;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.services.BluetoothService;

import java.util.Set;

public class SplashActivity extends BaseActivity {
//    private final int SPLASH_DISPLAY_LENGTH = 1000;
//    private static final int REQUEST_BLUETOOTH = 10000;
    private static final int REQUEST_ENABLE_BT = 10001;
//    private static final int REQUEST_COARSE_LOCATION = 10002;
//    private static final int REQUEST_BLUETOOTH_CONNECT = 10003;
//    private static final int REQUEST_BLUETOOTH_SCAN = 10004;
//    private static final int REQUEST_FINE_LOCATION = 10005;
//    private static final int REQUEST_ENABLE_LOCATION = 10006;
//    private static final int REQUEST_BOND = 999999;
//
//    BluetoothAdapter bluetoothAdapter;
//    boolean isPTBBonded = false;
//    private BroadcastReceiver discoveryReceiver;
//    Handler handler;
//    Runnable runnable;


    TextView etSplashDescr;
    Handler handler;
    Runnable runnable;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.splash_activity);
        _context = getApplicationContext();
        _activity = this;

        etSplashDescr = findViewById(R.id.splashDescr);
        etSplashDescr.setText("PTB Yükleniyor..");
        BluetoothService.setBluetoothAdapter(BluetoothAdapter.getDefaultAdapter());

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                if (BluetoothService.getBluetoothAdapter().isEnabled()){
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }else{
                    if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                       enableBluetooth();
                    }
                }
            }
        };

        handler.postDelayed(runnable, 1000);


//        checkAndRequestPermissions();
//
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        if (bluetoothAdapter == null) {
//            Toast.makeText(this, "Cihazınız Bluetooth'u desteklemiyor.", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//            enableBluetooth();
//        } else {
//            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
//        }
//        if (!checkLocationEnabled()) {
//            promptEnableLocationServices();
//        }
//
//        handler = new Handler(Looper.getMainLooper());
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                if (bluetoothAdapter.isEnabled()){
//                    boolean isConnected = BluetoothService.isConnected();
//                    if (!isConnected) {
//                        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
//                        isPTBBonded = false;
//                        for (BluetoothDevice device : pairedDevices) {
//                            if ("PTB".equals(device.getName())) {
//                                BluetoothService.connectToDevice(SplashActivity.this, device);
//                                isPTBBonded = BluetoothService.isConnected();
//                                break;
//                            }
//                        }
//                        if (!isPTBBonded) {
//                            startBluetoothDiscovery();
//                        }
//                    }
//                    if (isPTBBonded) {
//                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
//                        finish();
//                    } else {
//                        etSplashDescr.setText("PTB'ye bağlanılıyor..");
//                        handler.postDelayed(runnable, 100);
//                    }
//                }else {
//                    etSplashDescr.setText("Bluetooth açılıyor..");
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                            if (!bluetoothAdapter.isEnabled()) enableBluetooth();
//                        } else {
//                            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
//                        }
//                    }else {
//                        if (!bluetoothAdapter.isEnabled()) enableBluetooth();
//                    }
//
//                    handler.postDelayed(runnable, 2000);
//                }
//            }
//        };
//        handler.postDelayed(runnable, SPLASH_DISPLAY_LENGTH);
    }

//    private void checkAndRequestPermissions() {
//        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH);
//        }
//        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
//            }
//        }
//        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
//            }
//        }
//        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
//        }
//        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
//        }
//    }
//
    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }
//
//    private boolean checkLocationEnabled() {
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
//                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//    }
//
//    private void promptEnableLocationServices() {
//        AlertHelper.ShowAlertDialog(this, AlertType.QUESTION,
//                "Konum Servisleri Kapalı",
//                "Bluetooth taraması yapabilmek için konum servislerinin açık olması gerekmektedir. Lütfen konum servislerini açın.",
//                "Ayarlar",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_LOCATION);
//                    }
//                },
//                "İptal",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                }
//        );
//    }
//
//    private void startBluetoothDiscovery() {
//        discoveryReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    String deviceName = device.getName();
//                    if ("PTB".equals(deviceName)) {
//                        try {
//                            device.setPin("1234".getBytes());
//                            device.createBond();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                } else if (ACTION_PAIRING_REQUEST.equals(action)) {
//                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                    if ("PTB".equals(device.getName())) {
//                        try {
//                            device.setPin("1234".getBytes());
//                            device.createBond();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                if (isPTBBonded) unregisterReceiver(this);
//            }
//        };
//
//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(ACTION_PAIRING_REQUEST);
//        registerReceiver(discoveryReceiver, filter);
//
//        bluetoothAdapter.startDiscovery();
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            switch (requestCode) {
//                case REQUEST_BLUETOOTH:
//                case REQUEST_COARSE_LOCATION:
//                case REQUEST_FINE_LOCATION:
//                case REQUEST_BLUETOOTH_CONNECT:
//                case REQUEST_ENABLE_LOCATION:
//                case REQUEST_BLUETOOTH_SCAN:
//                case REQUEST_BOND:
//                    checkAndRequestPermissions();
//                    break;
//                case REQUEST_ENABLE_BT:
//                    enableBluetooth();
//                    break;
//            }
//        }
//    }
//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_ENABLE_BT:
                    enableBluetooth();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            } else {
                enableBluetooth();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
