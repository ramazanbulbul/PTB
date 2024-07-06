package com.orbteknoloji.ptb.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.orbteknoloji.ptb.database.TableInfo;
import com.orbteknoloji.ptb.models.DeviceModel;

import java.util.ArrayList;

public class SQLLiteService extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "PTB.db";

    public static String CREATE_TABLE_PTBInformation = "CREATE TABLE PTBInformation (" +
            "DEVICE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "DEVICE_NAME TEXT, " +
            "BT_MAC_ADDRESS TEXT, " +
            "CREATE_DATE TEXT DEFAULT CURRENT_TIMESTAMP" +
            ")";
    private static final int DATABASE_VERSION = 16;
    public SQLLiteService(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PTBInformation);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS PTBInformation");
        onCreate(db);
    }

    public void addPTBDevice(DeviceModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TableInfo.PTBInformation.DEVICE_NAME, model.getDeviceName());
        cv.put(TableInfo.PTBInformation.BT_MAC_ADDRESS, model.getDeviceBtMacAddress());

        long result = db.insert(TableInfo.PTBInformation.TABLE_NAME, null, cv);

        if (result > -1)
            Log.i("DatabaseHelper", "Not başarıyla kaydedildi");
        else
            Log.i("DatabaseHelper", "Not kaydedilemedi");

        db.close();
    }

    public void updPTBDevice(String DeviceName, String BtMacAddress) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("UPDATE " + TableInfo.PTBInformation.TABLE_NAME + " SET "
                 + TableInfo.PTBInformation.DEVICE_NAME  + " = '" + DeviceName + "'" +
                " WHERE " + TableInfo.PTBInformation.BT_MAC_ADDRESS + " = '" + BtMacAddress + "'");

        db.close();
    }


    public void delPTBDevice(String BT_MAC_ADDRESS) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TableInfo.PTBInformation.TABLE_NAME, TableInfo.PTBInformation.BT_MAC_ADDRESS+ "=?", new String[]{BT_MAC_ADDRESS});
        db.close();
    }
    public ArrayList<DeviceModel> getPTBDeviceByBtMacAddress(String BtMacAddress) {
        ArrayList<DeviceModel> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM PTBInformation WHERE BT_MAC_ADDRESS = ?";
        Cursor c = db.rawQuery(query, new String[]{String.valueOf(BtMacAddress)});

        String[] projection = {
                TableInfo.PTBInformation.DEVICE_ID,
                TableInfo.PTBInformation.DEVICE_NAME,
                TableInfo.PTBInformation.BT_MAC_ADDRESS};

//        Cursor c = db.query(TableInfo.PTBInformation.TABLE_NAME, projection, null, null, null, null, null);
        while (c.moveToNext()) {
            data.add(new DeviceModel(c.getInt(c.getColumnIndex(TableInfo.PTBInformation.DEVICE_ID)), c.getString(c.getColumnIndex(TableInfo.PTBInformation.DEVICE_NAME)), c.getString(c.getColumnIndex(TableInfo.PTBInformation.BT_MAC_ADDRESS))));
        }

        c.close();
        db.close();

        return data;
    }
    public ArrayList<DeviceModel> getPTBDeviceList() {
        ArrayList<DeviceModel> data = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                TableInfo.PTBInformation.DEVICE_ID,
                TableInfo.PTBInformation.DEVICE_NAME,
                TableInfo.PTBInformation.BT_MAC_ADDRESS};

        Cursor c = db.query(TableInfo.PTBInformation.TABLE_NAME, projection, null, null, null, null, null);
        while (c.moveToNext()) {
            data.add(new DeviceModel(c.getInt(c.getColumnIndex(TableInfo.PTBInformation.DEVICE_ID)), c.getString(c.getColumnIndex(TableInfo.PTBInformation.DEVICE_NAME)), c.getString(c.getColumnIndex(TableInfo.PTBInformation.BT_MAC_ADDRESS))));
        }

        c.close();
        db.close();

        return data;
    }
}
