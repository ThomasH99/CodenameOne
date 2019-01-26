/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author Thomas
 */
public class MyUtil {
    
    public static String removeTrailingPrecedingSpacesNewLinesEtc(String inputStr) {
        //remove spaces in start of string:
        if (inputStr == null || inputStr.length() == 0) {
            return inputStr;
        }
        int pos;
        while ((pos = inputStr.indexOf('\n')) != -1) {
            inputStr = inputStr.substring(0, pos) + inputStr.substring(pos + 1, inputStr.length());
        }
        while (inputStr.charAt(0) == ' ') {
            inputStr = inputStr.substring(1);
        }
//        while (inputStr.charAt(inputStr.length()-1)==' ' || inputStr.charAt(inputStr.length()-1)=='\n')
        while (inputStr.charAt(inputStr.length() - 1) == ' ') {
            inputStr = inputStr.substring(0, inputStr.length() - 2);
        }
//        inputStr = inputStr.replace("\n",""); //remove 
//        inputStr = inputStr.replace('\n',""); //remove 
        return inputStr;
    }
    
    public static String cleanEmail(String inputStr) {
        return removeTrailingPrecedingSpacesNewLinesEtc(inputStr);
    }

    /**
    https://stackoverflow.com/questions/11208479/how-do-i-initialize-a-byte-array-in-java
    @param s
    @return 
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
}
