package com.orbteknoloji.ptb.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.models.PlanModel;

import java.util.List;

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public CardView planCardview;
            public ImageView cvChannel1;
            public ImageView cvChannel2;
            public ImageView cvChannel3;
            public ImageView cvChannel4;
            public TextView cvPlanName;
            public TextView cvStartDate;
            public TextView cvEndDate;
            public TextView cvDescription;


            public ViewHolder(View view) {
                super(view);

                planCardview = (CardView)view.findViewById(R.id.planCardview);
                cvChannel1 = (ImageView)view.findViewById(R.id.cvChannel1);
                cvChannel2 = (ImageView)view.findViewById(R.id.cvChannel2);
                cvChannel3 = (ImageView)view.findViewById(R.id.cvChannel3);
                cvChannel4 = (ImageView)view.findViewById(R.id.cvChannel4);
                cvPlanName = (TextView)view.findViewById(R.id.cvPlanName);
                cvStartDate = (TextView)view.findViewById(R.id.cvStartDate);
                cvEndDate = (TextView)view.findViewById(R.id.cvEndDate);
                cvDescription = (TextView)view.findViewById(R.id.cvDescription);


            }
        }

        List<PlanModel> plans;
        public PlanAdapter(List<PlanModel> plans) {

            this.plans = plans;
        }
        @Override
        public PlanAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_plan, parent, false);
            final PlanAdapter.ViewHolder view_holder = new PlanAdapter.ViewHolder(v);
            v.setTag(view_holder.getPosition());
            return view_holder;
        }


        @Override
        public void onBindViewHolder(final PlanAdapter.ViewHolder holder, final int position) {
            holder.cvPlanName.setText(plans.get(position).getPlanName());
            holder.cvStartDate.setText(plans.get(position).getStartDate());
            holder.cvEndDate.setText(plans.get(position).getEndDate());
            holder.cvDescription.setText(plans.get(position).getDescription());
            holder.cvChannel1.setImageResource(plans.get(position).isChannel1() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
            holder.cvChannel2.setImageResource(plans.get(position).isChannel2() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
            holder.cvChannel3.setImageResource(plans.get(position).isChannel3() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
            holder.cvChannel4.setImageResource(plans.get(position).isChannel4() ? R.drawable.ic_switch_on : R.drawable.ic_switch_off);
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
