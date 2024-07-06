package com.orbteknoloji.ptb.models;

import com.orbteknoloji.ptb.adapters.DeviceAdapter;

public class DeviceModel{
    private int DeviceId;
    private String DeviceName;
    private String DeviceBtMacAddress;
    public DeviceModel(){
    }
    public DeviceModel(int DeviceId, String DeviceName, String DeviceBtMacAddress){
        this.DeviceId = DeviceId;
        this.DeviceName = DeviceName;
        this.DeviceBtMacAddress = DeviceBtMacAddress;
    }
    public int getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(int deviceId) {
        DeviceId = deviceId;
    }

    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String deviceName) {
        DeviceName = deviceName;
    }

    public String getDeviceBtMacAddress() {
        return DeviceBtMacAddress;
    }

    public void setDeviceBtMacAddress(String deviceBtMacAddress) {
        DeviceBtMacAddress = deviceBtMacAddress;
    }
}
