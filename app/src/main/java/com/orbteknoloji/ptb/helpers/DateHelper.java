package com.orbteknoloji.ptb.helpers;

import com.orbteknoloji.ptb.enums.RepeatStringType;

public class DateHelper {
    private static String[] months = {"","Ocak", "Şubat", "Mart", "Nisan", "Mayıs", "Haziran",
            "Temmuz", "Ağustos", "Eylül", "Ekim", "Kasım", "Aralık"};
    private static String[] days = {"","Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi",
            "Pazar"};
    public static String getDay(int numberOfDays){
        if (numberOfDays > 7) return "";
        return days[numberOfDays];
    }
    public static String getMonth(int numberOfMonths){
        if (numberOfMonths > 12) return "";
        return months[numberOfMonths];
    }
    public static String getDateByBluetooth(String[] blutoothData){
        if (blutoothData.length < 6) return "";
        String dateFormated = "";
        dateFormated += blutoothData[2] + " " + getMonth(Integer.parseInt(blutoothData[1])) + " " + "20" + blutoothData[0] + "\n";
        dateFormated += getDay(Integer.parseInt(blutoothData[3])) + " " + StringHelper.repeatString(blutoothData[4],2, "0", RepeatStringType.BEFORE) + ":" + StringHelper.repeatString(blutoothData[5],2, "0", RepeatStringType.BEFORE);
        return dateFormated;
    }
}