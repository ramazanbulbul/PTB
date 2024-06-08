package com.orbteknoloji.ptb.adapters;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.orbteknoloji.ptb.listeners.CustomClickListener;
import com.orbteknoloji.ptb.models.PlanModel;

import java.util.Calendar;
import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cvPlan;
        public CheckBox cbChannel1;
        public CheckBox cbChannel2;
        public CheckBox cbChannel3;
        public CheckBox cbChannel4;
        public TextView cvPlanName;
        public TextView cvStartDate;
        public TextView cvEndDate;
        public Spinner cvDate;
        public ImageButton btnClose;


        public ViewHolder(View view) {
            super(view);

            cvPlan = (CardView) view.findViewById(R.id.cvPlan);
            cbChannel1 = (CheckBox) view.findViewById(R.id.cbChannel1);
            cbChannel2 = (CheckBox) view.findViewById(R.id.cbChannel2);
            cbChannel3 = (CheckBox) view.findViewById(R.id.cbChannel3);
            cbChannel4 = (CheckBox) view.findViewById(R.id.cbChannel4);
            cvPlanName = (TextView) view.findViewById(R.id.cvPlanName);
            cvStartDate = (TextView) view.findViewById(R.id.cvStartDate);
            cvEndDate = (TextView) view.findViewById(R.id.cvEndDate);
            cvDate = (Spinner) view.findViewById(R.id.cvDate);
            btnClose = (ImageButton) view.findViewById(R.id.btnClose);
        }
    }

    List<PlanModel> plans;
    CustomClickListener customClickListener;

    public PlanAdapter(List<PlanModel> plans) {

        this.plans = plans;
    }
    public void addItem(PlanModel newItem) {
        plans.add(newItem);
        notifyItemInserted(plans.size() - 1);
    }
    public void removeItem(int position) {
        plans.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, plans.size());
    }
    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_plan, parent, false);
        final PlanAdapter.ViewHolder view_holder = new PlanAdapter.ViewHolder(v);
        return view_holder;
    }


    @Override
    public void onBindViewHolder(final PlanAdapter.ViewHolder holder, final int position) {

        ArrayAdapter daysAdapter = new ArrayAdapter(holder.cvDate.getContext(), android.R.layout.simple_spinner_item, TempDatabase.days);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.cvDate.setAdapter(daysAdapter);
        holder.cvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(holder.cvEndDate.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                holder.cvStartDate.setText(selectedTime);
                                if (!holder.cvEndDate.getText().toString().isEmpty()) {
                                    try {
                                        int starHour = Integer.parseInt(holder.cvStartDate.getText().toString().split(":")[0]);
                                        int endHour = Integer.parseInt(holder.cvEndDate.getText().toString().split(":")[0]);
                                        int startMin = Integer.parseInt(holder.cvStartDate.getText().toString().split(":")[1]);
                                        int endMin = Integer.parseInt(holder.cvEndDate.getText().toString().split(":")[1]);
                                        if (endHour < starHour) {
                                            holder.cvEndDate.setText("");
                                        } else if (endHour == starHour) {
                                            if (endMin <= startMin) {
                                                holder.cvEndDate.setText("");
                                            }
                                        }
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });
        holder.cvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(holder.cvEndDate.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String selectedTime = StringHelper.repeatString(hourOfDay, 2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(minute, 2, "0", RepeatStringType.BEFORE);
                                holder.cvEndDate.setText(selectedTime);
                                if (!holder.cvStartDate.getText().toString().isEmpty()) {
                                    try {
                                        int starHour = Integer.parseInt(holder.cvStartDate.getText().toString().split(":")[0]);
                                        int endHour = Integer.parseInt(holder.cvEndDate.getText().toString().split(":")[0]);
                                        int startMin = Integer.parseInt(holder.cvStartDate.getText().toString().split(":")[1]);
                                        int endMin = Integer.parseInt(holder.cvEndDate.getText().toString().split(":")[1]);
                                        if (endHour < starHour) {
                                            holder.cvEndDate.setText("");
                                            AlertHelper.ShowAlertDialog(holder.cvEndDate.getContext(), AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                        } else if (endHour == starHour) {
                                            if (endMin <= startMin) {
                                                holder.cvEndDate.setText("");
                                                AlertHelper.ShowAlertDialog(holder.cvEndDate.getContext(), AlertType.ERROR, "Giriş Hatası", "Bitiş saati başlangıç saatinden küçük olamaz!");
                                            }
                                        }
                                    } catch (Exception ex) {

                                    }
                                }
                            }
                        }, hour, minute, true);

                timePickerDialog.show();
            }
        });


        holder.btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertHelper.ShowAlertDialog(view.getContext(), AlertType.QUESTION, "Emin misiniz?", "Seçili planı silmek istediğinize emin misiniz?", "Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pos = holder.getAdapterPosition();
                        PlanModel removedItem = plans.get(pos);
                        removeItem(pos);

                        Snackbar snackbar = Snackbar
                                .make(holder.itemView, "Plan Silindi.", Snackbar.LENGTH_LONG)
                                .setAction("Geri Al", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        plans.add(pos, removedItem);
                                        notifyItemInserted(pos);
                                    }
                                });
                        snackbar.show();
                    }
                }, "Hayır",null);
            }
        });

        holder.cvPlan.setTag(plans.get(position).getPlanId());
        holder.cvPlanName.setText(plans.get(position).getPlanName());
        holder.cvDate.setSelection(plans.get(position).getDate());
        holder.cvStartDate.setText(plans.get(position).getStartTime());
        holder.cvEndDate.setText(plans.get(position).getEndTime());
        holder.cbChannel1.setChecked(plans.get(position).isChannel1());
        holder.cbChannel2.setChecked(plans.get(position).isChannel2());
        holder.cbChannel3.setChecked(plans.get(position).isChannel3());
        holder.cbChannel4.setChecked(plans.get(position).isChannel4());
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
