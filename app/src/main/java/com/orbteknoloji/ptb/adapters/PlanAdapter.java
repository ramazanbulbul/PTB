package com.orbteknoloji.ptb.adapters;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
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

public class PlanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public CardView cvPlan;
        public CheckBox cbChannel1;
        public CheckBox cbChannel2;
        public CheckBox cbChannel3;
        public CheckBox cbChannel4;
        public TextView cvPlanName;
        public EditText cvStartDate;
        public EditText cvEndDate;
        public Spinner cvDate;
        public ImageButton btnClose;

        public ItemViewHolder(View view) {
            super(view);

            cvPlan = view.findViewById(R.id.cvPlan);
            cbChannel1 = view.findViewById(R.id.cbChannel1);
            cbChannel2 = view.findViewById(R.id.cbChannel2);
            cbChannel3 = view.findViewById(R.id.cbChannel3);
            cbChannel4 = view.findViewById(R.id.cbChannel4);
            cvPlanName = view.findViewById(R.id.cvPlanName);
            cvStartDate = view.findViewById(R.id.cvStartDate);
            cvEndDate = view.findViewById(R.id.cvEndDate);
            cvDate = view.findViewById(R.id.cvDate);
            btnClose = view.findViewById(R.id.btnClose);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public Button btnAddPlan;

        public FooterViewHolder(View view) {
            super(view);
            btnAddPlan = view.findViewById(R.id.btnAddPlan);
        }
    }
    public PlanAdapter() {
    }

    public void addItem(PlanModel newItem) {
        TempDatabase.plans.add(newItem);
        notifyItemInserted(TempDatabase.plans.size() - 1);
    }

    public void removeItem(int position) {
        TempDatabase.plans.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, TempDatabase.plans.size());
    }

    @Override
    public int getItemViewType(int position) {
        if (position == TempDatabase.plans.size()) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_plan, parent, false);
            return new ItemViewHolder(v);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_layout, parent, false);
            return new FooterViewHolder(v);
        }
        throw new RuntimeException("Unknown view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            PlanModel plan = TempDatabase.plans.get(position);

            ArrayAdapter daysAdapter = new ArrayAdapter(itemHolder.cvDate.getContext(), android.R.layout.simple_spinner_item, TempDatabase.days);
            daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            itemHolder.cvDate.setAdapter(daysAdapter);
            itemHolder.cvDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    TempDatabase.plans.get(position).setDate(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            itemHolder.cvStartDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(itemHolder.cvEndDate.getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                    itemHolder.cvStartDate.setText(selectedTime);
                                    if (!itemHolder.cvEndDate.getText().toString().isEmpty()) {
                                        try {
                                            int starHour = Integer.parseInt(itemHolder.cvStartDate.getText().toString().split(":")[0]);
                                            int endHour = Integer.parseInt(itemHolder.cvEndDate.getText().toString().split(":")[0]);
                                            int startMin = Integer.parseInt(itemHolder.cvStartDate.getText().toString().split(":")[1]);
                                            int endMin = Integer.parseInt(itemHolder.cvEndDate.getText().toString().split(":")[1]);
                                            if (endHour < starHour) {
                                                itemHolder.cvEndDate.setText("");
                                            } else if (endHour == starHour) {
                                                if (endMin <= startMin) {
                                                    itemHolder.cvEndDate.setText("");
                                                }
                                            }
                                        } catch (Exception ex) {
                                            // Hata işleme
                                        }
                                    }
                                    TempDatabase.plans.get(position).setStartTime(itemHolder.cvStartDate.getText().toString());
                                    TempDatabase.plans.get(position).setEndTime(itemHolder.cvEndDate.getText().toString());
                                }
                            }, hour, minute, true);

                    timePickerDialog.show();
                }
            });
            itemHolder.cvEndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(itemHolder.cvEndDate.getContext(),
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                    itemHolder.cvEndDate.setText(selectedTime);
                                    if (!itemHolder.cvStartDate.getText().toString().isEmpty()) {
                                        try {
                                            int starHour = Integer.parseInt(itemHolder.cvStartDate.getText().toString().split(":")[0]);
                                            int endHour = Integer.parseInt(itemHolder.cvEndDate.getText().toString().split(":")[0]);
                                            int startMin = Integer.parseInt(itemHolder.cvStartDate.getText().toString().split(":")[1]);
                                            int endMin = Integer.parseInt(itemHolder.cvEndDate.getText().toString().split(":")[1]);
                                            if (endHour < starHour) {
                                                itemHolder.cvEndDate.setText("");
                                                AlertHelper.ShowAlertDialog(itemHolder.cvEndDate.getContext(), AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                            } else if (endHour == starHour) {
                                                if (endMin <= startMin) {
                                                    itemHolder.cvEndDate.setText("");
                                                    AlertHelper.ShowAlertDialog(itemHolder.cvEndDate.getContext(), AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                                }
                                            }
                                        } catch (Exception ex) {
                                            // Hata işleme
                                        }
                                    }
                                    TempDatabase.plans.get(position).setStartTime(itemHolder.cvStartDate.getText().toString());
                                    TempDatabase.plans.get(position).setEndTime(itemHolder.cvEndDate.getText().toString());
                                }
                            }, hour, minute, true);

                    timePickerDialog.show();
                }
            });

            itemHolder.btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertHelper.ShowAlertDialog(view.getContext(), AlertType.QUESTION, "Emin misiniz?", "Seçili planı silmek istediğinize emin misiniz?", "Evet", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            int pos = itemHolder.getAdapterPosition();
                            PlanModel removedItem = TempDatabase.plans.get(pos);
                            removeItem(pos);

                            Snackbar snackbar = Snackbar
                                    .make(itemHolder.itemView, "Plan Silindi.", Snackbar.LENGTH_LONG)
                                    .setAction("Geri Al", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            TempDatabase.plans.remove(removedItem);
                                            notifyItemInserted(pos);
                                        }
                                    });
                            snackbar.show();
                        }
                    }, "Hayır", null);
                }
            });

            itemHolder.cbChannel1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TempDatabase.plans.get(position).setChannel1(b);
                }
            });
            itemHolder.cbChannel2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TempDatabase.plans.get(position).setChannel2(b);
                }
            });
            itemHolder.cbChannel3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TempDatabase.plans.get(position).setChannel3(b);
                }
            });
            itemHolder.cbChannel4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    TempDatabase.plans.get(position).setChannel4(b);
                }
            });

            itemHolder.cvDate.setSelection(plan.getDate());
            itemHolder.cvPlan.setTag(plan.getPlanId());
            itemHolder.cvPlanName.setText(plan.getPlanName());
            itemHolder.cvStartDate.setText(plan.getStartTime());
            itemHolder.cvEndDate.setText(plan.getEndTime());
            itemHolder.cbChannel1.setChecked(plan.isChannel1());
            itemHolder.cbChannel2.setChecked(plan.isChannel2());
            itemHolder.cbChannel3.setChecked(plan.isChannel3());
            itemHolder.cbChannel4.setChecked(plan.isChannel4());
        } else if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.btnAddPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TempDatabase.plans.size() >= 25){
                        AlertHelper.ShowAlertDialog(v.getContext(), AlertType.ERROR, "Sınıra ulaşıldı", "Maksimum plan sayısına ulaştınız!");
                    }
                    PlanModel planModel = new PlanModel();
                    planModel.setPlanName(getItemCount() + ". Plan");
                    planModel.setDate(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1 == 0 ? 7 : Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1);
                    addItem(planModel);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return TempDatabase.plans.size() + 1; // +1 footer için
    }
}
