package com.orbteknoloji.ptb.helpers;

import com.orbteknoloji.ptb.enums.RepeatStringType;

public class StringHelper {
    public static String repeatString(int str, int count, String appendStr, RepeatStringType repeatStringType) {
        StringBuilder sb = new StringBuilder();
        if (repeatStringType == RepeatStringType.AFTER) sb.append(str);
        for (int i = 0; i < count - String.valueOf(str).length(); i++) {
            sb.append(appendStr);
        }
        if (repeatStringType == RepeatStringType.BEFORE) sb.append(str);
        return sb.toString();
    }

    public static String repeatString(String str, int count, String appendStr, RepeatStringType repeatStringType) {
        StringBuilder sb = new StringBuilder();
        if (repeatStringType == RepeatStringType.AFTER) sb.append(str);
        for (int i = 0; i < count - str.length(); i++) {
            sb.append(appendStr);
        }
        if (repeatStringType == RepeatStringType.BEFORE) sb.append(str);
        return sb.toString();
    }

    public static String repeatString(int str, int count, String appendStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        for (int i = 0; i < count - sb.toString().length() - 1; i++) {
            sb.append(appendStr);
        }
        return sb.toString();
    }

    public static String repeatString(String str, int count, String appendStr) {
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        for (int i = 0; i < count - sb.toString().length() - 1; i++) {
            sb.append(appendStr);
        }
        return sb.toString();
    }

    public static String repeatString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    public static String rightTrim(String str) {
        int i = str.length() - 1;
        while (i >= 0 && Character.isWhitespace(str.charAt(i))) {
            i--;
        }
        return str.substring(0, i + 1);
    }

    public static String rightTrim(String str, char trimChar) {
        int i = str.length() - 1;
        while (i >= 0 && str.charAt(i) == trimChar) {
            i--;
        }
        return str.substring(0, i + 1);
    }

    public static String leftTrim(String str) {
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }

    public static String leftTrim(String str, char trimChar) {
        int i = 0;
        while (i < str.length() && str.charAt(i) == trimChar) {
            i++;
        }
        return str.substring(i);
    }

}
