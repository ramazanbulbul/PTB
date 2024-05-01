package com.orbteknoloji.ptb.models;

import android.icu.lang.UProperty;

import com.orbteknoloji.ptb.database.TempDatabase;
import com.orbteknoloji.ptb.helpers.StringHelper;

public class PlanModel {
    private int PlanId;
    private String PlanName;
    private int Date;
    private String StartTime;
    private String EndTime;
    private boolean Channel1;
    private boolean Channel2;
    private boolean Channel3;
    private boolean Channel4;

    public String getPlanName() {
        return PlanName;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public int getDate() {
        return Date;
    }

    public void setDate(int date) {
        Date = date;
    }

    public String getDateString() {
        return TempDatabase.days.get(getDate());
    }


    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
    public boolean isChannel1() {
        return Channel1;
    }

    public void setChannel1(boolean channel1) {
        Channel1 = channel1;
    }

    public boolean isChannel2() {
        return Channel2;
    }

    public void setChannel2(boolean channel2) {
        Channel2 = channel2;
    }

    public boolean isChannel3() {
        return Channel3;
    }

    public void setChannel3(boolean channel3) {
        Channel3 = channel3;
    }

    public boolean isChannel4() {
        return Channel4;
    }

    public void setChannel4(boolean channel4) {
        Channel4 = channel4;
    }

    public int getPlanId() {
        return PlanId;
    }

    public void setPlanId(int planId) {
        PlanId = planId;
    }
}
