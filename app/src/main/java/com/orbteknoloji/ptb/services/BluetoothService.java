package com.orbteknoloji.ptb.services;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static BluetoothSocket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;
    private static BluetoothDevice connectedDevice;
    private static BluetoothAdapter bluetoothAdapter;

    static int counter = 0;

    public static boolean connectToDevice(Context _context, BluetoothDevice device) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bluetooh yetkisi verilmeden devam edilemez!");
                    return false;
                }
            }

            if (getConnectedDeviceAddress().equalsIgnoreCase(device.getName())) socket = null;

            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
//            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası 1", e.getMessage());
            if (counter < 5) {
                connectToDevice(_context, device);
            }else {
                return false;
            }
            counter++;
        }
        connectedDevice = device;
        return true;
    }
    public static boolean connectToDevice(Context _context, String btMacAddress){
        if (BluetoothService.getBluetoothAdapter().isEnabled()) {
            Set<BluetoothDevice> pairedDevices = BluetoothService.getBluetoothAdapter().getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                Log.d("connectToDevice", btMacAddress);
                if (device.getAddress().equals(btMacAddress)) {
                    return BluetoothService.connectToDevice(_context, device);
                }
            }
        }
        return false;
    }

    public static boolean sendData(Context _context, String data) {
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, byte[] data) {
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!" + e.getMessage());
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, byte data) {
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, int data) {
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }

    public static int receiveData(Context _context) {
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            if (inputStream == null) return -1;
            return inputStream.read();
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi alımı sırasında bir problemle karışlaşıldı!");
            return -1;
        }
    }
    public static String receiveData(Context _context, int bufferSize) {
        String receivedMessage = "";
        try {
            if (!isConnected()) connectToDevice(_context, connectedDevice);
            if (inputStream == null) return "false";
            for (int i = 0; i < bufferSize; i++) {
                receivedMessage += Integer.toHexString(inputStream.read())+ " ";
            }
            return receivedMessage;
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi alımı sırasında bir problemle karışlaşıldı!");
            return "false";
        }
    }

    public static boolean closeConnection(Context _context) {
        try {
            socket.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }
    public static boolean isConnected(){
        return socket != null ? socket.isConnected() : false;
    }

    public static BluetoothDevice getConnectedDevice() {
        return connectedDevice;
    }
    public static String getConnectedDeviceAddress() {
        return connectedDevice != null ? connectedDevice.getAddress() : "";
    }


    public static BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public static void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        BluetoothService.bluetoothAdapter = bluetoothAdapter;
    }
}
