package com.orbteknoloji.ptb.services;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService {
    private static BluetoothSocket socket;
    private static OutputStream outputStream;
    private static InputStream inputStream;

    public static boolean connectToDevice(Context _context, BluetoothDevice device) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // SPP UUID
        try {
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bluetooh yetkisi verilmeden devam edilemez!");
                return false;
            }
            socket = socket == null ? device.createRfcommSocketToServiceRecord(uuid) : socket;
            socket.connect();
            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();
        } catch (IOException e) {
//            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası 1", e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean sendData(Context _context, String data) {
        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, byte[] data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, byte data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }
    public static boolean sendData(Context _context, int data) {
        try {
            outputStream.write(data);
        } catch (IOException e) {
            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Bluetooth Hatası", "Bilgi gönderimi sırasında bir problemle karışlaşıldı!");
            return false;
        }
        return true;
    }

    public static int receiveData(Context _context) {
        try {
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
}
