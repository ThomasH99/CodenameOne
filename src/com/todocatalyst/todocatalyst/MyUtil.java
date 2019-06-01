/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class MyUtil {

    /**
    clean up text for tasks, itemlists or workslots to avoid starting or ending with newlines or spaces (would
    make it easier to have duplicate names without realizing)
    @param inputStr
    @return 
     */
    public static String removeTrailingPrecedingSpacesNewLinesEtc(String inputStr) {
        //remove spaces in start of string:
        if (inputStr == null || inputStr.length() == 0) {
            return inputStr;
        }
        StringBuilder s = new StringBuilder(inputStr);
        int idx;
        while (s.length() > 0 && (idx = s.toString().indexOf('\n')) != -1) {
//            inputStr = inputStr.substring(0, idx) + inputStr.substring(idx + 1, inputStr.length());
            s.deleteCharAt(idx);
        }
        while (s.length() > 0 && s.toString().charAt(0) == ' ') {
//            inputStr = inputStr.substring(1);
            s.deleteCharAt(0);
        }
//        while (inputStr.charAt(inputStr.length()-1)==' ' || inputStr.charAt(inputStr.length()-1)=='\n')
        while (s.length() > 0 && s.toString().charAt(s.length() - 1) == ' ') {
//            inputStr = inputStr.substring(0, inputStr.length() - 2);
            s.deleteCharAt(s.length() - 1);
        }
//        inputStr = inputStr.replace("\n",""); //remove 
//        inputStr = inputStr.replace('\n',""); //remove 
        return s.toString();
    }

    /**
  /Users/thomashjelm/NetBeansProjects/todocatalyst/src/com/todocatalyst/todocatalyst/TodoCatalystParse.java:538: error: cannot find symbol
    @param inputStr
    @return string roughly limited to 150chars (Google Analytics max length)
     */
    public static String keepMethodCallInStackTrace(String inputStr) {
        return inputStr;
//<editor-fold defaultstate="collapsed" desc="comment">
//         StringBuilder s = new StringBuilder(inputStr);
//         StringBuilder result = new StringBuilder();
//        String line;
//        int idx;
//        while (result.length()<150) {
//        //for each line:
//        idx = s.toString().indexOf('\n');
//        if (idx!=-1){
//
//        line = s.subSequence(0, idx).toString();
//        s.
//        s.delete(0, idx+1);
//        }
//        //remove spaces in start of string:
//        if (inputStr == null || inputStr.length() == 0) {
//            return inputStr;
//        }
//        int idx;
//        while ((idx = s.toString().indexOf('\n')) != -1) {
////            inputStr = inputStr.substring(0, idx) + inputStr.substring(idx + 1, inputStr.length());
//            s.deleteCharAt(idx);
//        }
//        while (s.toString().charAt(0) == ' ') {
////            inputStr = inputStr.substring(1);
//            s.deleteCharAt(0);
//        }
////        while (inputStr.charAt(inputStr.length()-1)==' ' || inputStr.charAt(inputStr.length()-1)=='\n')
//        while (s.toString().charAt(inputStr.length() - 1) == ' ') {
////            inputStr = inputStr.substring(0, inputStr.length() - 2);
//            s.deleteCharAt(s.length()-1);
//        }
////        inputStr = inputStr.replace("\n",""); //remove
////        inputStr = inputStr.replace('\n',""); //remove
//        return s.toString();
//</editor-fold>
    }

    public static String removeTrailingPrecedingSpacesNewLinesEtcXXX(String inputStr) {
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

    /**
    clean a string so it can for example be used with Google Analytics
    @param inputStr
    @return 
     */
    public static String cleanToSingleLineNoSpacesString(String inputStr) {
        //remove spaces in start of string:

        if (inputStr == null || inputStr.length() == 0) {
            return inputStr;
        }
        StringBuilder s = new StringBuilder(inputStr);
        int idx;
        //delete newlines
        while (s.length() > 0 && (idx = s.toString().indexOf('\n')) != -1) {
            s.deleteCharAt(idx);
        }
        //delete inline spaces
        while (s.length() > 0 && (idx = s.toString().indexOf(' ')) != -1) {
            s.deleteCharAt(idx);
        }
        return s.toString();
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

    public static boolean eql(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }

    public static boolean neql(Object a, Object b) {
        return !eql(a, b); //optimize
    }

    /**
    return true for an empty or single-element list, and if all elements are smaller than their successor based on comp
    @param list
    @param comp
    @return 
    */
    public static boolean isSorted(List list, Comparator comp) {
        Object oPrev = null;
        for (Object o : list) {
            if (oPrev == null)
                oPrev = o; //on first iteration, simply store first element in list
            else {
//                if (!(comp.compare(oPrev, o) > 0)) return false;
                if ((comp.compare(oPrev, o) < 0)) return false;
                oPrev = o;
            }
        }
        return true;
    }

}
