package com.orbteknoloji.ptb.database;

import com.orbteknoloji.ptb.models.PlanModel;

import java.util.ArrayList;
import java.util.List;

public class TempDatabase {
    public static ArrayList<String> days = new ArrayList<>(java.util.Arrays.asList("","Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"));
    public static List<PlanModel> plans= new ArrayList<PlanModel>();
    public static void addPlan(PlanModel plan){
        plans.add(plan);
    }
    public static void setPlan(PlanModel oldPlan, PlanModel newPlan){
        plans.remove(oldPlan);
        plans.add(newPlan);
    }

    public static void removePlan(PlanModel plan){
        plans.remove(plan);
    }
    public static void clearPlan(){
        plans.clear();
    }
    public static PlanModel getPlanByPlanId(int planId){
        for(PlanModel plan : plans){
            if (plan.getPlanId() == planId) return plan;
        }
        return null;
    }
    public static int getMaxPlanId(){
        return TempDatabase.plans.size() == 0 ? 1 : TempDatabase.plans.get(TempDatabase.plans.size() - 1).getPlanId() + 1;
    }
}
