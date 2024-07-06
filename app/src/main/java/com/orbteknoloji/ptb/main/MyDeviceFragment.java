package com.orbteknoloji.ptb.main;

import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothDevice.ACTION_PAIRING_REQUEST;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.adapters.DeviceAdapter;
import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.models.DeviceModel;
import com.orbteknoloji.ptb.models.PlanModel;
import com.orbteknoloji.ptb.services.BluetoothService;
import com.orbteknoloji.ptb.services.SQLLiteService;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class MyDeviceFragment extends BaseFragment {

    private static final int REQUEST_BLUETOOTH = 10000;
    private static final int REQUEST_ENABLE_BT = 10001;
    private static final int REQUEST_COARSE_LOCATION = 10002;
    private static final int REQUEST_BLUETOOTH_CONNECT = 10003;
    private static final int REQUEST_BLUETOOTH_SCAN = 10004;
    private static final int REQUEST_FINE_LOCATION = 10005;
    private static final int REQUEST_ENABLE_LOCATION = 10006;
    private static final int REQUEST_BOND = 999999;
    boolean isPTBBonded = false;
    private BroadcastReceiver discoveryReceiver;
    Handler handler;
    Runnable runnable;
    double counter = 0;
    ProgressDialog progressDialog;
    RecyclerView rvDevices;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_my_devices, container, false);
        _context = root.getContext();
        _activity = getActivity();
        _db = new SQLLiteService(_context);

        ImageView imgSearch = root.findViewById(R.id.imgRefresh);
        rvDevices = root.findViewById(R.id.rvDevices);

        Set<BluetoothDevice> pairedDevices = BluetoothService.getBluetoothAdapter().getBondedDevices();

        for (DeviceModel device : _db.getPTBDeviceList()) {
            boolean isPaired = false;
            for (BluetoothDevice d : pairedDevices) {
                if (device.getDeviceBtMacAddress().equals(d.getAddress())) {
                    isPaired = true;
                    break;
                }
            }
            if (!isPaired) _db.delPTBDevice(device.getDeviceBtMacAddress());
        }

        setListViewData(rvDevices);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(_context);
                progressDialog.setMessage("Aranıyor...");
                progressDialog.setCancelable(false);
                progressDialog.create();

                counter = 0;

                Animation rotation = AnimationUtils.loadAnimation(_context, R.anim.rotate);
                imgSearch.startAnimation(rotation);
                checkAndRequestPermissions();
                if (BluetoothService.getBluetoothAdapter() == null) {
                    Toast.makeText(_context, "Cihazınız Bluetooth'u desteklemiyor.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    enableBluetooth();
                } else {
                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                }
                if (!checkLocationEnabled()) {
                    promptEnableLocationServices();
                }else {
                    handler = new Handler(Looper.getMainLooper());

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            imgSearch.startAnimation(rotation);
                            progressDialog.show();
                            if (!BluetoothService.getBluetoothAdapter().isEnabled()) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                        if (!BluetoothService.getBluetoothAdapter().isEnabled())
                                            enableBluetooth();
                                    } else {
                                        ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
                                    }
                                } else {
                                    if (!BluetoothService.getBluetoothAdapter().isEnabled())
                                        enableBluetooth();
                                }
                            }

                            if (counter == 0) {
                                startBluetoothDiscovery();
                                handler.postDelayed(runnable, 500);
                            } else {
                                if (counter < 50) {
                                    handler.postDelayed(runnable, 500);
                                } else {
                                    progressDialog.hide();
                                    AlertHelper.ShowAlertDialog(_context, AlertType.INFO, "PTB cihazı bulunamadı!", "PTB Cihazınızın açık olup olmadığını kontrol ediniz! Aramaya devam etmek için evet'e basabilirsiniz", "Evet", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            counter = 0;
                                            handler.postDelayed(runnable, 500);
                                        }
                                    });
                                }
                            }
                            counter++;
                        }
                    };
                    handler.postDelayed(runnable, 500);
                }

//                handler = new Handler(Looper.getMainLooper());
//                runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.show();
//
//                        if (BluetoothService.getBluetoothAdapter().isEnabled()) {
//                            Set<BluetoothDevice> pairedDevices = BluetoothService.getBluetoothAdapter().getBondedDevices();
//                            isPTBBonded = false;
//                            for (BluetoothDevice device : pairedDevices) {
//                                if ("PTB".equals(device.getName()) && _db.getPTBDeviceByBtMacAddress(device.getAddress()).size() == 0) {
//                                    BluetoothService.connectToDevice(_context, device);
//                                    isPTBBonded = BluetoothService.isConnected();
//                                    break;
//                                }
//                            }
//                            if (!isPTBBonded) {
//                                startBluetoothDiscovery();
//                            }
//                            if (isPTBBonded) {
//                                if (_db.getPTBDeviceByBtMacAddress(BluetoothService.getConnectedDevice().getAddress()).size() == 0) {
//                                    DeviceModel model = new DeviceModel();
//                                    model.setDeviceName("PTB");
//                                    model.setDeviceBtMacAddress(BluetoothService.getConnectedDevice().getAddress());
//                                    _db.addPTBDevice(model);
//                                    _db.close();
//                                }
//                                progressDialog.hide();
//                                setListViewData(rvDevices);
//                            } else {
//                                if (counter < 100) {
//                                    counter++;
//                                    handler.postDelayed(runnable, 100);
//                                } else {
//                                    progressDialog.hide();
//                                    AlertHelper.ShowAlertDialog(_context, AlertType.INFO, "PTB cihazı bulunamadı!", "PTB Cihazınızın açık olup olmadığını kontrol ediniz! Aramaya devam etmek için evet'e basabilirsiniz", "Evet", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialogInterface, int i) {
//                                            counter = 0;
//                                        }
//                                    });
//                                }
//                            }
//                        } else {
////                            etSplashDescr.setText("Bluetooth açılıyor..");
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                                if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                                    if (!BluetoothService.getBluetoothAdapter().isEnabled())
//                                        enableBluetooth();
//                                } else {
//                                    ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
//                                }
//                            } else {
//                                if (!BluetoothService.getBluetoothAdapter().isEnabled())
//                                    enableBluetooth();
//                            }
//                        }
//                    }
//                };
//                handler.postDelayed(runnable, 0);
            }
        });

        return root;
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH}, REQUEST_BLUETOOTH);
        }
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
        }
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_SCAN}, REQUEST_BLUETOOTH_SCAN);
            }
        }
        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    private void enableBluetooth() {
        if (!BluetoothService.getBluetoothAdapter().isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    private boolean checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) _activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void promptEnableLocationServices() {
        AlertHelper.ShowAlertDialog(_context, AlertType.QUESTION,
                "Konum Servisleri Kapalı",
                "Bluetooth taraması yapabilmek için konum servislerinin açık olması gerekmektedir. Lütfen konum servislerini açın ve daha sonra tekrardan deneyiniz!",
                "Ayarlar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_ENABLE_LOCATION);
                    }
                },
                "İptal",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }
        );
    }

    private void startBluetoothDiscovery() {
        discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                progressDialog.setMessage("PTB taraması yapılıyor..");
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    if ("PTB".equalsIgnoreCase(device.getName()) && _db.getPTBDeviceByBtMacAddress(device.getAddress()).size() == 0){
                        progressDialog.setMessage("PTB bulundu..");
                        device.createBond();
                    }
                } else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                    try {
                        // Eşleşme isteği geldiğinde PIN kodunu ayarla
                        if ("PTB".equalsIgnoreCase(device.getName()) && _db.getPTBDeviceByBtMacAddress(device.getAddress()).size() == 0){
                            progressDialog.setMessage("PTB eşleştiriliyor..");
                            device.setPin("1234".getBytes());
                            device.setPairingConfirmation(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                    // Eşleşme durumu değiştiğinde kontrol et
                    int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        // Eşleşme işlemi tamamlandı
                        if (handler != null) {
                            handler.removeCallbacks(runnable);
                        }
                        progressDialog.setMessage("PTB bağlanılıyor..");
                        BluetoothService.connectToDevice(_context, device);

                        DeviceModel model = new DeviceModel();
                        model.setDeviceName("PTB");
                        model.setDeviceBtMacAddress(BluetoothService.getConnectedDevice().getAddress());
                        _db.addPTBDevice(model);
                        _db.close();
                        progressDialog.hide();
                        progressDialog.dismiss();
                        setListViewData(rvDevices);

                        isPTBBonded = true;
                        _activity.unregisterReceiver(this);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        _activity.registerReceiver(discoveryReceiver, filter);

        BluetoothService.getBluetoothAdapter().startDiscovery();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_BLUETOOTH:
                case REQUEST_COARSE_LOCATION:
                case REQUEST_FINE_LOCATION:
                case REQUEST_BLUETOOTH_CONNECT:
                case REQUEST_ENABLE_LOCATION:
                case REQUEST_BLUETOOTH_SCAN:
                case REQUEST_BOND:
                    checkAndRequestPermissions();
                    break;
                case REQUEST_ENABLE_BT:
                    enableBluetooth();
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        if (discoveryReceiver != null) {
            _activity.unregisterReceiver(discoveryReceiver);
        }
    }

    public void setListViewData(RecyclerView rvDevices) {
        _db = new SQLLiteService(_activity.getApplicationContext());
        List<DeviceModel> devices = _db.getPTBDeviceList();
        _db.close();

        LinearLayoutManager llManager = new LinearLayoutManager(_context);
        llManager.setOrientation(LinearLayoutManager.VERTICAL);
        llManager.scrollToPosition(0);

        DeviceAdapter deviceAdapter = new DeviceAdapter(_context, devices);

        rvDevices.setLayoutManager(llManager);
        rvDevices.setAdapter(deviceAdapter);
        rvDevices.setItemAnimator(new DefaultItemAnimator());
    }
}

