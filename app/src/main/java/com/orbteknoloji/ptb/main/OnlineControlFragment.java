package com.orbteknoloji.ptb.main;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.helpers.DateHelper;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.services.BluetoothService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class OnlineControlFragment extends BaseFragment {
    BluetoothDevice targetDevice = null;

    private Handler handler;
    private Runnable runnable;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_online_control, container, false);
        _context = root.getContext();
        Switch sKanal1 = root.findViewById(R.id.switchKanal1);
        Switch sKanal2 = root.findViewById(R.id.switchKanal2);
        Switch sKanal3 = root.findViewById(R.id.switchKanal3);
        Switch sKanal4 = root.findViewById(R.id.switchKanal4);

        TextView btDate = root.findViewById(R.id.btDate);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                getActivity().finish();
                return root;
            }
        }
        boolean isConnected = BluetoothService.isConnected();
        if (!isConnected){
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("HC-05")) {
                    isConnected = true;
                    BluetoothService.connectToDevice(_context,device);
                    break;
                }
            }
        }
        if (isConnected) {
            sKanal1.setEnabled(true);
            sKanal2.setEnabled(true);
            sKanal3.setEnabled(true);
            sKanal4.setEnabled(true);
            BluetoothService.sendData(_context, 0Xfd);
            BluetoothService.sendData(_context,0xff);
            int receiveFF = BluetoothService.receiveData(_context);
            char[] receiveFFBinary = (StringHelper.repeatString("0", 4 - Integer.toBinaryString(receiveFF).length())+Integer.toBinaryString(receiveFF)).toCharArray();
            if (receiveFFBinary.length < 4){
                AlertHelper.ShowAlertDialog(_context, "Hata", "Bağlantı Hatası!");
            }else{
                if (receiveFFBinary[3] == '1'){
                    sKanal1.setChecked(true);
                }
                if (receiveFFBinary[2] == '1'){
                    sKanal2.setChecked(true);
                }
                if (receiveFFBinary[1] == '1'){
                    sKanal3.setChecked(true);
                }
                if (receiveFFBinary[0] == '1'){
                    sKanal4.setChecked(true);
                }
            }
            BluetoothService.sendData(_context,0xfc);
            String[] btDateText = BluetoothService.receiveData(_context, 6).split(" ");
            String dateFormated = DateHelper.getDateByBluetooth(btDateText);
            btDate.setText(dateFormated);

            handler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {
                    BluetoothService.sendData(_context,0xfc);
                    String[] btDateText = BluetoothService.receiveData(_context, 6).split(" ");
                    String dateFormated = DateHelper.getDateByBluetooth(btDateText);
                    btDate.setText(dateFormated);
                }
            };
            handler.postDelayed(runnable, 30000);

            sKanal1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothService.sendData(_context, sKanal1.isChecked() ? "A".getBytes() : "a".getBytes());
                }
            });
            sKanal2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothService.sendData(_context, sKanal2.isChecked() ? "B".getBytes() : "b".getBytes());
                }
            });
            sKanal3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothService.sendData(_context, sKanal3.isChecked() ? "C".getBytes() : "c".getBytes());
                }
            });
            sKanal4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BluetoothService.sendData(_context, sKanal4.isChecked() ? "D".getBytes() : "d".getBytes());
                }
            });

        }else {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR,
                    "Cihaz Hatası", "Doğru cihaza bağlantı kurduğunuza emin olun!",
                    "Çık",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    },false, null);
        }
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable);
    }
}
