package com.orbteknoloji.ptb.main;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.orbteknoloji.ptb.BaseActivity;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.enums.AlertType;
import com.orbteknoloji.ptb.enums.RepeatStringType;
import com.orbteknoloji.ptb.helpers.AlertHelper;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.models.PlanModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPlanActivity extends BaseActivity {
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        _context = this;
        _activity = this;
//        Spinner spinDays = findViewById(R.id.spinDays);

        CheckBox cbChannel1 = findViewById(R.id.cbChannel1);
        CheckBox cbChannel2 = findViewById(R.id.cbChannel2);
        CheckBox cbChannel3 = findViewById(R.id.cbChannel3);
        CheckBox cbChannel4 = findViewById(R.id.cbChannel4);

        EditText editPlanName = findViewById(R.id.editPlanName);
        EditText editTimeStart = findViewById(R.id.editTimeStart);
        EditText editTimeEnd = findViewById(R.id.editTimeEnd);

        GridLayout glEdit = findViewById(R.id.glEdit);
        GridLayout glDays = findViewById(R.id.glDays);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnDelete = findViewById(R.id.btnDelete);


//        ArrayAdapter daysAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, TempDatabase.days);
//        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinDays.setAdapter(daysAdapter);
        editTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(_context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                editTimeStart.setText(selectedTime);
                                if (!editTimeEnd.getText().toString().isEmpty()){
                                    try {
                                        int starHour = Integer.parseInt(editTimeStart.getText().toString().split(":")[0]);
                                        int endHour = Integer.parseInt(editTimeEnd.getText().toString().split(":")[0]);
                                        int startMin = Integer.parseInt(editTimeStart.getText().toString().split(":")[1]);
                                        int endMin = Integer.parseInt(editTimeEnd.getText().toString().split(":")[1]);
                                        if (endHour < starHour){
                                            editTimeEnd.setText("");
                                        } else if (endHour == starHour) {
                                            if (endMin <= startMin){
                                                editTimeEnd.setText("");
                                            }
                                        }
                                    }catch (Exception ex){

                                    }
                                }
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });
        editTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(_context,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                editTimeEnd.setText(selectedTime);
                                if (!editTimeStart.getText().toString().isEmpty()){
                                    try {
                                        int starHour = Integer.parseInt(editTimeStart.getText().toString().split(":")[0]);
                                        int endHour = Integer.parseInt(editTimeEnd.getText().toString().split(":")[0]);
                                        int startMin = Integer.parseInt(editTimeStart.getText().toString().split(":")[1]);
                                        int endMin = Integer.parseInt(editTimeEnd.getText().toString().split(":")[1]);
                                        if (endHour < starHour){
                                            editTimeEnd.setText("");
                                            AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                        } else if (endHour == starHour) {
                                            if (endMin <= startMin){
                                                editTimeEnd.setText("");
                                                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                            }
                                        }
                                    }catch (Exception ex){

                                    }
                                }
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        Intent intent = getIntent();
        String planId = intent.getStringExtra("PlanId");
        if (planId != null && !planId.equalsIgnoreCase("0")) {
            PlanModel oldplan = TempDatabase.getPlanByPlanId(Integer.parseInt(planId));
            if (oldplan == null) {
                AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Hata", "Düzenlemek istediğiniz plan'a ulaşılamıyor lütfen tekrar deneyiniz!", "Geri Dmb", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(_activity.getApplicationContext(), MainActivity.class);
                        intent.putExtra("TAB", "1");
                        startActivity(intent);
                    }
                });
                return;
            }

            btnSave.setVisibility(View.GONE);
            glEdit.setVisibility(View.VISIBLE);

            editPlanName.setText(oldplan.getPlanName());
            cbChannel1.setChecked(oldplan.isChannel1());
            cbChannel2.setChecked(oldplan.isChannel2());
            cbChannel3.setChecked(oldplan.isChannel3());
            cbChannel4.setChecked(oldplan.isChannel4());

            for(int i = 0; i < glDays.getChildCount(); i++){
                CheckBox checkBox = (CheckBox) glDays.getChildAt(i);
                if (checkBox.getTag().toString().equalsIgnoreCase(String.valueOf(oldplan.getDate()))) checkBox.setChecked(true);
                checkBox.setEnabled(false);
            }

//            spinDays.setSelection(oldplan.getDate());
            editTimeStart.setText(oldplan.getStartTime());
            editTimeEnd.setText(oldplan.getEndTime());


        } else {
            editPlanName.setText(TempDatabase.getMaxPlanId() + ". Plan");
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            dayOfWeek = (dayOfWeek + 5) % 7;
            for(int i = 0; i < glDays.getChildCount(); i++){
                CheckBox checkBox = (CheckBox) glDays.getChildAt(i);
                if (checkBox.getTag().toString().equalsIgnoreCase(String.valueOf(dayOfWeek+1))) checkBox.setChecked(true);
            }
//            spinDays.setSelection(dayOfWeek);
        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedDays = 0;
                for(int i = 0; i < glDays.getChildCount(); i++){
                    CheckBox checkBox = (CheckBox) glDays.getChildAt(i);
                    if (checkBox.isChecked()) {
                        selectedDays++;
                        PlanModel model = new PlanModel();
                        model.setPlanId(TempDatabase.getMaxPlanId());
                        model.setPlanName(String.valueOf(editPlanName.getText()));
                        model.setChannel1(cbChannel1.isChecked());
                        model.setChannel2(cbChannel2.isChecked());
                        model.setChannel3(cbChannel3.isChecked());
                        model.setChannel4(cbChannel4.isChecked());
                        model.setDate(Integer.parseInt(checkBox.getTag().toString()));

                        model.setStartTime(String.valueOf(editTimeStart.getText()));
                        model.setEndTime(String.valueOf(editTimeEnd.getText()));
                        TempDatabase.addPlan(model);
                    }
                }

                if (selectedDays == 0){
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi", "Lütfen en az bir gün seçimi yapınız!");
                }else if(editTimeStart.getText().toString().isEmpty()){
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi", "Lütfen başlangıç saatini seçiniz!");
                }else if(editTimeEnd.getText().toString().isEmpty()){
                    AlertHelper.ShowAlertDialog(_context, AlertType.ERROR, "Eksik Bilgi", "Lütfen bitiş saatini seçiniz!");
                }else{
                    Intent intent = new Intent(_activity.getApplicationContext(), MainActivity.class);
                    intent.putExtra("TAB", "1");
                    intent.putExtra("IS_UPDATE", true);
                    startActivity(intent);
                }
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert planId != null;
                PlanModel oldplan = TempDatabase.getPlanByPlanId(Integer.parseInt(planId));
                for(int i = 0; i < glDays.getChildCount(); i++){
                    CheckBox checkBox = (CheckBox) glDays.getChildAt(i);
                    if (checkBox.isChecked()) {
                        PlanModel model = new PlanModel();
                        model.setPlanId(TempDatabase.getMaxPlanId());
                        model.setPlanName(String.valueOf(editPlanName.getText()));
                        model.setChannel1(cbChannel1.isChecked());
                        model.setChannel2(cbChannel2.isChecked());
                        model.setChannel3(cbChannel3.isChecked());
                        model.setChannel4(cbChannel4.isChecked());

                        model.setDate(Integer.parseInt(checkBox.getTag().toString()));

                        model.setStartTime(String.valueOf(editTimeStart.getText()));
                        model.setEndTime(String.valueOf(editTimeEnd.getText()));
                        TempDatabase.setPlan(oldplan, model);
                    }
                }
                Intent intent = new Intent(_activity.getApplicationContext(), MainActivity.class);
                intent.putExtra("TAB", "1");
                intent.putExtra("IS_UPDATE", true);
                startActivity(intent);
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assert planId != null;
                PlanModel oldplan = TempDatabase.getPlanByPlanId(Integer.parseInt(planId));
                TempDatabase.removePlan(oldplan);

                Intent intent = new Intent(_activity.getApplicationContext(), MainActivity.class);
                intent.putExtra("TAB", "1");
                intent.putExtra("IS_UPDATE", true);
                startActivity(intent);
            }
        });
    }
}
