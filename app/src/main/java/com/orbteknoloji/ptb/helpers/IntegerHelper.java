package com.orbteknoloji.ptb.helpers;

public class IntegerHelper {
    public static Integer hexToInt(String hexString){
        if (hexString.equalsIgnoreCase("00")) hexString = "0";
        return Integer.parseInt(hexString, 16);
    }
    public static String binaryToHex(String binaryString){
        int decimal = Integer.parseInt(binaryString, 2);
        return Integer.toHexString(decimal);
    }
    public static String hexToBinary(String hex) {
        int decimal = Integer.parseInt(hex, 16);
        return Integer.toBinaryString(decimal);
    }

}
