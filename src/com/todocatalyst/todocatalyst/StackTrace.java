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

    StackTrace(String stackTrace) {
        super(CLASS_NAME);
        put(PARSE_STACKTRACE, stackTrace);
    }

    public static void test(String stackTrace) {
        StackTrace stackTr = new StackTrace(stackTrace);
        try {
            stackTr.save();
        } catch (ParseException ex) {
            Log.p(ex.toString());
        }
    }
}
