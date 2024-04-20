package com.orbteknoloji.ptb.main;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.orbteknoloji.ptb.BaseActivity;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.helpers.StringHelper;
import com.orbteknoloji.ptb.models.PlanModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AddPlanActivity extends BaseActivity {
    ArrayList<String> days = new ArrayList<>(java.util.Arrays.asList("Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_plan);
        _context = this;
        _activity = this;
        Spinner spinDays = findViewById(R.id.spinDays);

        CheckBox cbChannel1 = findViewById(R.id.cbChannel1);
        CheckBox cbChannel2 = findViewById(R.id.cbChannel2);
        CheckBox cbChannel3 = findViewById(R.id.cbChannel3);
        CheckBox cbChannel4 = findViewById(R.id.cbChannel4);

        EditText editPlanName = findViewById(R.id.editPlanName);
        EditText editTimeStart = findViewById(R.id.editTimeStart);
        EditText editTimeEnd = findViewById(R.id.editTimeEnd);
        Button btnSave = findViewById(R.id.btnSave);

        editPlanName.setText((TempDatabase.plans.size()+1) +". Plan");
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
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0")  + ":" + StringHelper.repeatString(minute, 2, "0");
                                editTimeStart.setText(selectedTime);
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
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0")  + ":" + StringHelper.repeatString(minute, 2, "0");
                                editTimeEnd.setText(selectedTime);
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        ArrayAdapter daysAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDays.setAdapter(daysAdapter);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlanModel model = new PlanModel();
                model.setPlanName(String.valueOf(editPlanName.getText()));

                model.setChannel1(cbChannel1.isChecked());
                model.setChannel2(cbChannel2.isChecked());
                model.setChannel3(cbChannel3.isChecked());
                model.setChannel4(cbChannel4.isChecked());

                model.setDate(days.get(spinDays.getSelectedItemPosition()));

                model.setStartTime(String.valueOf(editTimeStart.getText()));
                model.setEndTime(String.valueOf(editTimeEnd.getText()));
                TempDatabase.plans.add(model);

                Intent intent = new Intent(_activity.getApplicationContext(), MainActivity.class);
                intent.putExtra("TAB", "1");
                startActivity(intent);

            }
        });
    }
}
