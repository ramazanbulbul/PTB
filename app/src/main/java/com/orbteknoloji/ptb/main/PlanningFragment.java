package com.orbteknoloji.ptb.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orbteknoloji.ptb.BaseFragment;
import com.orbteknoloji.ptb.R;
import com.orbteknoloji.ptb.adapters.PlanAdapter;
import com.orbteknoloji.ptb.database.TempDatabase;

public class PlanningFragment extends BaseFragment {
    FloatingActionButton btnAddPlan;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_planning, container, false);
        _context = root.getContext();

        btnAddPlan = root.findViewById(R.id.add_plan);
        btnAddPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AddPlanActivity.class);
                startActivity(intent);
            }
        });

        PlanAdapter adapter_items = new PlanAdapter(TempDatabase.plans);

        RecyclerView recycler_view = root.findViewById(R.id.plans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);

        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);
        recycler_view.setNestedScrollingEnabled(false);
        recycler_view.setAdapter(adapter_items);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        return root;
    }
}
