package com.orbteknoloji.ptb.database;


import android.provider.BaseColumns;

public class TableInfo {

    public static final class PTBInformation implements BaseColumns {
        public static String TABLE_NAME = "PTBInformation";
        public static String DEVICE_ID = "DEVICE_ID";
        public static String DEVICE_NAME = "DEVICE_NAME";
        public static String BT_MAC_ADDRESS = "BT_MAC_ADDRESS";
        public static String CREATE_DATE = "CREATE_DATE";
    }

}