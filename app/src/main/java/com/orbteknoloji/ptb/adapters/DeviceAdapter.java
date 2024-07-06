package com.orbteknoloji.ptb.adapters;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.models.DeviceModel;
import com.orbteknoloji.ptb.models.PlanModel;
import com.orbteknoloji.ptb.services.BluetoothService;
import com.orbteknoloji.ptb.services.SQLLiteService;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import kotlinx.coroutines.CompletionHandler_commonKt;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private Context context;
    private List<DeviceModel> deviceList;
    public DeviceAdapter(Context context, List<DeviceModel> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
    }
    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview_device, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        DeviceModel device = deviceList.get(position);
        holder.txtDeviceName.setText(device.getDeviceName());
        holder.txtDeviceAddress.setText(device.getDeviceBtMacAddress());

        holder.imgBtIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!holder.txtDeviceName.isEnabled()) {
                    boolean isConnected = BluetoothService.connectToDevice(holder.itemView.getContext(), holder.txtDeviceAddress.getText().toString());
                    if (!isConnected){
                        AlertHelper.ShowAlertDialog(view.getContext(), AlertType.ERROR, "Bluetooth Bağlantısı", "Bağlanmak istediğiniz cihaz bulunamadı!", "Tamam", null);
                    }else{
                        AlertHelper.ShowAlertDialog(view.getContext(), AlertType.INFO, "Bluetooth Bağlantısı", "Bağlantı Başarılı!", "Tamam", null);
                        RecyclerView recyclerView = (RecyclerView) holder.imgConnect.getParent().getParent();
                        for (int i=0;i < recyclerView.getChildCount(); i++){
                            LinearLayout linearLayout = (LinearLayout) recyclerView.getChildAt(i);
                            ImageView imgConn = linearLayout.findViewById(R.id.imgConnect);
                            imgConn.setVisibility(View.GONE);
                        }
                        holder.imgConnect.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.txtDeviceName.isEnabled()) {
                    SQLLiteService db = new SQLLiteService(view.getContext());
                    db.updPTBDevice(holder.txtDeviceName.getText().toString(), holder.txtDeviceAddress.getText().toString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.imgEdit.setImageResource(holder.txtDeviceName.isEnabled() ? R.drawable.ic_edit : R.drawable.ic_save);
                    holder.txtDeviceName.setEnabled(!holder.txtDeviceName.isEnabled());
                    if (holder.txtDeviceName.isEnabled()) holder.txtDeviceName.requestFocus();
                }
            }
        });
//        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertHelper.ShowAlertDialog(view.getContext(), AlertType.QUESTION, "Emin misiniz?", "Seçili cihazı silmek istediğinize emin misiniz?", "Evet", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        ProgressDialog pd = new ProgressDialog(view.getContext());
//                        pd.setMessage("PTB kaldırılıyor..");
//                        pd.create();
//                        pd.show();
//                        SQLLiteService db = new SQLLiteService(view.getContext());
//                        db.delPTBDevice(holder.txtDeviceAddress.getText().toString());
//                        holder.itemView.setVisibility(View.GONE);
//
//                        Set<BluetoothDevice> pairedDevices = BluetoothService.getBluetoothAdapter().getBondedDevices();
//                        for (BluetoothDevice device : pairedDevices) {
//                            if (device.getAddress().equals(holder.txtDeviceAddress.getText())) {
//                                try {
//                                    Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
//                                    Method removeBondMethod = btClass.getMethod("removeBond");
//                                    Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                                break;
//                            }
//                        }
//                        pd.hide();
//                    }
//                }, "Hayır", null);
//            }
//        });
        if (BluetoothService.getConnectedDeviceAddress().equals(device.getDeviceBtMacAddress())){
            holder.imgConnect.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public static class DeviceViewHolder extends RecyclerView.ViewHolder {
        EditText txtDeviceName;
        EditText txtDeviceAddress;
        ImageView imgConnect;
        ImageView imgEdit;
//        ImageView imgDelete;
        ImageView imgBtIcon;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDeviceName = itemView.findViewById(R.id.txtDeviceName);
            txtDeviceAddress = itemView.findViewById(R.id.txtDeviceAddress);
            imgConnect = itemView.findViewById(R.id.imgConnect);
            imgEdit = itemView.findViewById(R.id.imgEdit);
            imgBtIcon = itemView.findViewById(R.id.imgBtIcon);
//            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
