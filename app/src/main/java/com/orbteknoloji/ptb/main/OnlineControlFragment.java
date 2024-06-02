package com.orbteknoloji.ptb.main;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.enums.RepeatStringType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.helpers.DateHelper;
import com.orbteknoloji.ptb.helpers.IntegerHelper;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.services.BluetoothService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class OnlineControlFragment extends BaseFragment {
    Switch sChannel1, sChannel2, sChannel3, sChannel4;
    TextView btDate;
    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 10001;
    int REQUEST_BLUETOOTH_CONNECT = 10002;
    private Handler handler;
    private Handler handlerDate;
    private Runnable runnable;
    private Runnable runnableDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_online_control, container, false);
        _context = root.getContext();
        _activity = getActivity();

        ProgressDialog progressDialog = new ProgressDialog(_context);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.create();
        progressDialog.show();

        sChannel1 = root.findViewById(R.id.switchKanal1);
        sChannel2 = root.findViewById(R.id.switchKanal2);
        sChannel3 = root.findViewById(R.id.switchKanal3);
        sChannel4 = root.findViewById(R.id.switchKanal4);

        btDate = root.findViewById(R.id.btDate);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
        }

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                boolean isConnected = BluetoothService.isConnected();
                if (!isConnected) {
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    for (BluetoothDevice device : pairedDevices) {
                        if (device.getName().equals("HC-05")) {
                            BluetoothService.connectToDevice(_context, device);
                            break;
                        }
                    }
                }
                isConnected = BluetoothService.isConnected();
                if (isConnected) {
                    sChannel1.setEnabled(true);
                    sChannel2.setEnabled(true);
                    sChannel3.setEnabled(true);
                    sChannel4.setEnabled(true);

                    getChannelsStatus();

                    handlerDate = new Handler();
                    runnableDate = new Runnable() {
                        @Override
                        public void run() {
                            getDateTime();
                            handlerDate.postDelayed(this, 30000);
                        }
                    };
                    handlerDate.postDelayed(runnableDate, 0);

                    sChannel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BluetoothService.sendData(_context, sChannel1.isChecked() ? "A".getBytes() : "a".getBytes());
                        }
                    });
                    sChannel2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BluetoothService.sendData(_context, sChannel2.isChecked() ? "B".getBytes() : "b".getBytes());
                        }
                    });
                    sChannel3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BluetoothService.sendData(_context, sChannel3.isChecked() ? "C".getBytes() : "c".getBytes());
                        }
                    });
                    sChannel4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BluetoothService.sendData(_context, sChannel4.isChecked() ? "D".getBytes() : "d".getBytes());
                        }
                    });
                    btDate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String sendHexData = "FD";
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                            sendHexData = "F8";
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                            String receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase(sendHexData)){
                                Calendar calendar = Calendar.getInstance();

                                //year
                                sendHexData = String.valueOf(calendar.get(Calendar.YEAR)).substring(2,4);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }
                                //month
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.MONTH)+1), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }
                                //day
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }

                                //day of week
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)-1), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }

                                //HOUR OF DAY
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }

                                //day of week
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.MINUTE)), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }

                                //seconds
                                sendHexData =  StringHelper.repeatString(String.valueOf(calendar.get(Calendar.SECOND)), 2,"0", RepeatStringType.BEFORE);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));
                                receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                                if (!receiveHexData.equalsIgnoreCase(sendHexData)){
                                    AlertHelper.ShowAlertDialog(_context, "Hata", "hata: Integer.toHexString(sendData):" + sendHexData + " receiveHexData:" + receiveHexData);
                                }
                                sendHexData =  "F7";
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(sendHexData));

                                getDateTime();
                                getChannelsStatus();
                            }else {
                                AlertHelper.ShowAlertDialog(_context, "Hata", "Veri alım Hatası!");
                            }
                        }
                    });
                    progressDialog.hide();
                    progressDialog.dismiss();
                } else {
                    progressDialog.hide();
                    progressDialog.dismiss();
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR,
                            "Cihaz Hatası", "Doğru cihaza bağlantı kurduğunuza emin olun!",
                            "Çık",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    getActivity().finish();
                                }
                            }, true, null);
                }
            }
        };
        handler.postDelayed(runnable, 100); // 1000 milisaniye = 1 saniye

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) handler.removeCallbacks(runnable);
        if (handlerDate != null) handlerDate.removeCallbacks(runnableDate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata", "Bluetooth açma isteği reddedilmiştir!");
            }
        }
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (resultCode != Activity.RESULT_OK) {
                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata", "Gerekli izinler verilmeden devam edilemez!");
                ActivityCompat.requestPermissions(_activity, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            }
        }

    }

    public void getDateTime(){
        BluetoothService.sendData(_context, 0xfc);
        String[] btDateText = BluetoothService.receiveData(_context, 6).split(" ");
        String dateFormatted = DateHelper.getDateByBluetooth(btDateText);
        btDate.setText(dateFormatted);
    }
    public void getChannelsStatus(){
        BluetoothService.sendData(_context,0xfd);
        BluetoothService.sendData(_context,0xff);
        int receiveFF =  BluetoothService.receiveData(_context);
        char[] receiveFFBinary = (StringHelper.repeatString("0", 4 - Integer.toBinaryString(receiveFF).length()) + Integer.toBinaryString(receiveFF)).toCharArray();
        if (receiveFFBinary.length < 4) {
            AlertHelper.ShowAlertDialog(_context, "Hata", "Bağlantı Hatası!");
        } else {
            if (receiveFFBinary[3] == '1') {
                sChannel1.setChecked(true);
            }
            if (receiveFFBinary[2] == '1') {
                sChannel2.setChecked(true);
            }
            if (receiveFFBinary[1] == '1') {
                sChannel3.setChecked(true);
            }
            if (receiveFFBinary[0] == '1') {
                sChannel4.setChecked(true);
            }
        }
    }

}
