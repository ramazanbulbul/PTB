package com.orbteknoloji.ptb.main;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.adapters.PlanAdapter;
import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.enums.RepeatStringType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.helpers.IntegerHelper;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.listeners.CustomClickListener;
import com.orbteknoloji.ptb.models.PlanModel;
import com.orbteknoloji.ptb.services.BluetoothService;

import java.util.Calendar;
import java.util.Set;

public class PlanningFragment extends BaseFragment {
    Button btnAddPlan;
    FloatingActionButton btnSendPlan;
    private Handler handler;
    private Runnable runnable;
    BluetoothAdapter bluetoothAdapter;
    int REQUEST_ENABLE_BT = 10001;
    int REQUEST_BLUETOOTH_CONNECT = 10002;

    boolean _isUpdate = false;
    PlanAdapter planAdapter;
    RecyclerView rvPlans;
    private SwipeRefreshLayout swipeRefreshLayout;


    public PlanningFragment(boolean isUpdate) {
        _isUpdate = isUpdate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_planning, container, false);
        _context = root.getContext();
        _activity = getActivity();
        btnAddPlan = root.findViewById(R.id.btnAddPlan);
        btnSendPlan = root.findViewById(R.id.btnSendPlan);
        planAdapter = new PlanAdapter(TempDatabase.plans);
        rvPlans = root.findViewById(R.id.plans);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
        btnSendPlan.setVisibility(View.GONE);

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
        btnAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TempDatabase.plans.size() >= 25){
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Sınıra ulaşıldı", "Maksimum plan sayısına ulaştınız!");
                }
                PlanModel planModel = new PlanModel();
                planModel.setPlanName(planAdapter.getItemCount() + 1 + ". Plan");
                planModel.setDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                planAdapter.addItem(planModel);
                rvPlans.scrollToPosition(planAdapter.getItemCount() - 1);
            }
        });
        btnSendPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TempDatabase.clearPlan();
                for (int i = 0; i < rvPlans.getChildCount(); i++) {
                    View v = rvPlans.getChildAt(i);
                    if (((Spinner)v.findViewById(R.id.cvDate)).getSelectedItemPosition() == 0){
                        AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi(" + (i+1) + ". Plan)", "Gün bilgisi boş bırakılamaz!", "Tamam", null);
                        return;
                    }
                    if (((EditText) v.findViewById(R.id.cvStartDate)).getText().toString().isEmpty()){
                        AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi(" + (i+1) + ". Plan)", "Başlangıç saati bilgisi boş bırakılamaz!", "Tamam", null);
                        return;
                    }
                    if (((EditText) v.findViewById(R.id.cvEndDate)).getText().toString().isEmpty()){
                        AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi(" + (i+1) + ". Plan)", "Başlangıç saati bilgisi boş bırakılamaz!", "Tamam", null);
                        return;
                    }
                    if (!(((CheckBox) v.findViewById(R.id.cbChannel1)).isChecked() ||
                        ((CheckBox) v.findViewById(R.id.cbChannel2)).isChecked() ||
                        ((CheckBox) v.findViewById(R.id.cbChannel3)).isChecked() ||
                        ((CheckBox) v.findViewById(R.id.cbChannel4)).isChecked())){
                        AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi(" + (i+1) + ". Plan)", "Plan yapılacak kanal bilgisi boş bırakılamaz!", "Tamam", null);
                        return;
                    }

                    PlanModel planModel = new PlanModel();
                    planModel.setPlanId(i+1);
                    planModel.setPlanName(planModel.getPlanId() + ". Plan");
                    planModel.setDate(((Spinner) v.findViewById(R.id.cvDate)).getSelectedItemPosition());
                    planModel.setStartTime(((EditText) v.findViewById(R.id.cvStartDate)).getText().toString());
                    planModel.setEndTime(((EditText) v.findViewById(R.id.cvEndDate)).getText().toString());
                    planModel.setChannel1(((CheckBox) v.findViewById(R.id.cbChannel1)).isChecked());
                    planModel.setChannel2(((CheckBox) v.findViewById(R.id.cbChannel2)).isChecked());
                    planModel.setChannel3(((CheckBox) v.findViewById(R.id.cbChannel3)).isChecked());
                    planModel.setChannel4(((CheckBox) v.findViewById(R.id.cbChannel4)).isChecked());
                    TempDatabase.plans.add(planModel);
                }
                BluetoothService.sendData(_context, 0xf9);
                String receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                if (receiveHexData.equalsIgnoreCase("F9")) {
                    ProgressDialog progressDialog = new ProgressDialog(_context);
                    progressDialog.setMessage("Yükleniyor...");
                    progressDialog.setCancelable(false);
                    progressDialog.create();
                    progressDialog.show();
                    for (PlanModel model : TempDatabase.plans) {
                        String day = String.valueOf(model.getDate());
                        String channels = IntegerHelper.binaryToHex((model.isChannel4() ? "1" : "0") + (model.isChannel3() ? "1" : "0") + (model.isChannel2() ? "1" : "0") + (model.isChannel1() ? "1" : "0"));
                        String startHour = model.getStartTime().split(":")[0];
                        String startMin = model.getStartTime().split(":")[1];
                        String endHour = model.getEndTime().split(":")[0];
                        String endMin = model.getEndTime().split(":")[1];

                        Integer sendData = 0;
                        sendData = Integer.parseInt(day + channels, 16);
                        BluetoothService.sendData(_context, sendData);
                        receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                        if (!receiveHexData.equalsIgnoreCase(Integer.toHexString(sendData))) {
                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            BluetoothService.sendData(_context, 0xf9);
                            BluetoothService.sendData(_context, 0xfd);
                            break;
                        }
                        sendData = Integer.parseInt(startHour, 16);
                        BluetoothService.sendData(_context, sendData);
                        receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                        if (!receiveHexData.equalsIgnoreCase(Integer.toHexString(sendData))) {
                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            BluetoothService.sendData(_context, 0xf9);
                            BluetoothService.sendData(_context, 0xfd);
                            break;
                        }
                        sendData = Integer.parseInt(startMin, 16);
                        BluetoothService.sendData(_context, sendData);
                        receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                        if (!receiveHexData.equalsIgnoreCase(Integer.toHexString(sendData))) {
                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            BluetoothService.sendData(_context, 0xf9);
                            BluetoothService.sendData(_context, 0xfd);
                            break;
                        }
                        sendData = Integer.parseInt(endHour, 16);
                        BluetoothService.sendData(_context, sendData);
                        receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                        if (!receiveHexData.equalsIgnoreCase(Integer.toHexString(sendData))) {
                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            BluetoothService.sendData(_context, 0xf9);
                            BluetoothService.sendData(_context, 0xfd);
                            break;
                        }
                        sendData = Integer.parseInt(endMin, 16);
                        BluetoothService.sendData(_context, sendData);
                        receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                        if (!receiveHexData.equalsIgnoreCase(Integer.toHexString(sendData))) {
                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            BluetoothService.sendData(_context, 0xf9);
                            BluetoothService.sendData(_context, 0xfd);
                            break;
                        }
                    }
                    progressDialog.hide();
                    AlertHelper.ShowAlertDialog(_context, AlertType.INFO,  "Aktarım İşlemi", "Planlarınız başarıyla aktarıldı!", "Tamam", null);
                } else {
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                    BluetoothService.sendData(_context, 0xf9);
                }
                BluetoothService.sendData(_context, 0xfd);
                setPlanAdapter(rvPlans, planAdapter);
            }
        });

        handler = new Handler(Looper.getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                refreshContent();
            }
        };
        handler.postDelayed(runnable, 0);
        setPlanAdapter(rvPlans, planAdapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) handler.removeCallbacks(runnable);
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

    public void setPlanAdapter(RecyclerView rvPlans, PlanAdapter planAdapter) {
        LinearLayoutManager llManager = new LinearLayoutManager(_context);
        llManager.setOrientation(LinearLayoutManager.VERTICAL);
        llManager.scrollToPosition(0);
        rvPlans.setLayoutManager(llManager);
        rvPlans.setHasFixedSize(true);
        rvPlans.setNestedScrollingEnabled(false);
        rvPlans.setAdapter(planAdapter);
        rvPlans.setItemAnimator(new DefaultItemAnimator());
    }
    private void refreshContent() {
        if (!swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(true);
        }
        boolean isConnected = BluetoothService.isConnected();
        if (!isConnected) {
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("PTB")) {
                    BluetoothService.connectToDevice(_context, device);
                    break;
                }
            }
        }
        isConnected = BluetoothService.isConnected();
        if (isConnected) {
            if (!_isUpdate) {
                TempDatabase.clearPlan();
                BluetoothService.sendData(_context, 0xfd);
                BluetoothService.sendData(_context, 0xfe);
                String receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
                if (receiveHexData.equalsIgnoreCase("FE")) {
                    BluetoothService.sendData(_context, 0xfb);
                    receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                    if (!receiveHexData.equalsIgnoreCase("FA")) {
                        while (!receiveHexData.equalsIgnoreCase("FA")) {
                            PlanModel plan = new PlanModel();
                            plan.setPlanId(TempDatabase.getMaxPlanId());
                            plan.setPlanName(TempDatabase.getMaxPlanId() + ". Plan");
                            plan.setDate(IntegerHelper.hexToInt(receiveHexData.split("")[0]));

                            int receiveChannels = IntegerHelper.hexToInt(receiveHexData.split("")[1]);
                            char[] receiveChannelsBinary = (StringHelper.repeatString("0", 4 - Integer.toBinaryString(receiveChannels).length()) + Integer.toBinaryString(receiveChannels)).toCharArray();
                            if (receiveChannelsBinary.length < 4) {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                                return;
                            } else {
                                if (receiveChannelsBinary[3] == '1') {
                                    plan.setChannel1(true);
                                }
                                if (receiveChannelsBinary[2] == '1') {
                                    plan.setChannel2(true);
                                }
                                if (receiveChannelsBinary[1] == '1') {
                                    plan.setChannel3(true);
                                }
                                if (receiveChannelsBinary[0] == '1') {
                                    plan.setChannel4(true);
                                }
                            }

                            //startTime
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase("FB") || receiveHexData.equalsIgnoreCase("FA")) {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                                continue;
                            }
                            String startHour = receiveHexData;
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase("FB") || receiveHexData.equalsIgnoreCase("FA")) {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                                continue;
                            }
                            String startMin = receiveHexData;
                            plan.setStartTime(startHour + ":" + startMin);

                            //endTime
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase("FB") || receiveHexData.equalsIgnoreCase("FA")) {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                                continue;
                            }
                            String endHour = receiveHexData;
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase("FB") || receiveHexData.equalsIgnoreCase("FA")) {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                                continue;
                            }
                            String endMin = receiveHexData;
                            plan.setEndTime(endHour + ":" + endMin);
                            BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                            if (receiveHexData.equalsIgnoreCase("FB")) {
                                TempDatabase.addPlan(plan);
                                BluetoothService.sendData(_context, IntegerHelper.hexToInt(receiveHexData));
                            } else {
                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata","Veri aktarımında bir hata oluştu! Lütfen tekrar deneyiniz!", "Tamam", null);
                            }
                            receiveHexData = StringHelper.repeatString(Integer.toHexString(BluetoothService.receiveData(_context)), 2, "0", RepeatStringType.BEFORE);
                        }
                        setPlanAdapter(rvPlans, planAdapter);
                    }
//                            else {
//                                receiveHexData = Integer.toHexString(BluetoothService.receiveData(_context));
//                            }
                } else {
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Cihaz Hatası", "Doğru cihaza bağlantı kurduğunuza emin olun!", "Çık", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getActivity().finish();
                        }
                    }, true, null);
                }
            }

            btnSendPlan.setVisibility(View.VISIBLE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }
}
