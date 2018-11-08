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
public abstract class Config {
    /**
     * set to true during testing. Setting false will remove test code from the app. 
     */
    public static final boolean TEST = true; //false;
    public static final boolean DEBUG_LOGGING = true; //logs only enabled during 
    public static final boolean WORKTIME_TEST = false; //
    public static final boolean WORKTIME_DETAILED_LOG = false; //NB! these logs are very time-consuming!!
    public static final boolean ENABLELOGGING = true;
    public static final boolean PARSE_OFFLINE = false;
    public static final boolean INLINE_WORKSHOP_TESTCASE = true;
}
