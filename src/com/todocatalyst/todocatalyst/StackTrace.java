package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author thomashjelm
 */
public class StackTrace extends ParseObject {

    public static String CLASS_NAME = "StackTrace";

    private static String PARSE_STACKTRACE = "trace";
    private static String PARSE_LOST_DATA = "lostData"; //use to store data which could not be saved correctly, notably if an 

    StackTrace() {
        super(CLASS_NAME);
    }

    StackTrace(String stackTrace) {
        super(CLASS_NAME);
        put(PARSE_STACKTRACE, stackTrace);
    }

    StackTrace(ParseObject parseObject) {
        super(CLASS_NAME);
        put(PARSE_LOST_DATA, parseObject);
    }

    public static void test(String stackTrace) {
        StackTrace stackTr = new StackTrace(stackTrace);
        try {
            stackTr.save();
        } catch (ParseException ex) {
            Log.p(ex.toString());
        }
    }

    static public void saveStackTrace(String stackContent) {
        StackTrace stackTrace = new StackTrace(stackContent);
        try {
            stackTrace.save();
        } catch (ParseException ex) {
            Log.p(ex.toString());
        }
    }

    /**
     * save the data in a parseObject that for some reason cannot be saved to
     * Parse server, notably if there are references to unsaved objects which
     * break batch save
     *
     * @param lostParseObject
     */
    static public void saveParseObjectAsCSV(Item lostParseObject) {
        StringBuilder strBuilder = new StringBuilder();
        lostParseObject.csvSaveToStringBuilder(strBuilder);
        StackTrace stackTr = new StackTrace();
        stackTr.put(PARSE_LOST_DATA, strBuilder.toString());
        try {
            stackTr.save();
        } catch (ParseException ex) {
            Log.p(ex.toString());
        }
    }

//    static public void saveParseObjectAsString(ParseObject lostParseObject) {
////        byte[] encoded;
////        OutputStream o=new ByteArrayBuffer();
//        ByteArrayBuffer byteArray = new ByteArrayBuffer();
//        DataOutputStream outputStream = new DataOutputStream(byteArray);
//        try {
//            lostParseObject.externalize(outputStream);
//            outputStream.close();
//        } catch (IOException ex) {
//            Log.p(ex.toString());
//        }
//        
//        StackTrace stackTr = new StackTrace();
//        stackTr.put(PARSE_LOST_DATA, byteArray.getRawData());
//        try {
//            stackTr.save();
//        } catch (ParseException ex) {
//            Log.p(ex.toString());
//        }
//    }
}
