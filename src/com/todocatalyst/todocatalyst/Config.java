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
     * set to true during testing. Setting false will remove test code from the
     * app.
     */
    public static final boolean PRODUCTION_RELEASE = false; //if true will disable all error flags
    public static final boolean PROD_LOG = true || PRODUCTION_RELEASE; //log to keep in production
    public static final boolean ENABLELOGGING = true && !PRODUCTION_RELEASE;

    public static final boolean TEST = true && !PRODUCTION_RELEASE; //false;
    
    public static final boolean FULLY_LOCAL_MODE = false && !PRODUCTION_RELEASE; //used without Internet connection so disable access to remote server (use local laptop) and Analytics
    public static final boolean PARSE_DB_OFFLINE = (true || FULLY_LOCAL_MODE) && !PRODUCTION_RELEASE; //used without Internet connection so disable access to remote server (use local laptop) and Analytics
    public static final boolean ANALYTICS_DISABLED = (false || FULLY_LOCAL_MODE) && !PRODUCTION_RELEASE; //used without Internet connection so disable access to remote server (use local laptop) and Analytics

    public static final boolean TEST_CACHE = false && !PRODUCTION_RELEASE; //false;
    public static final boolean TEST_SCROLL_Y = true && !PRODUCTION_RELEASE; //false;
    public static final boolean TEST_STORE_PASSWORD_FOR_USER = true && !PRODUCTION_RELEASE; //false;
    public static final boolean TEST_BACKGR = false && !PRODUCTION_RELEASE; //false;
    public static final boolean CHECK_OWNERS = false && !PRODUCTION_RELEASE; //false;
    public static final boolean TEST_DRAG_AND_DROP = true && !PRODUCTION_RELEASE; //false;
    public static final boolean TEST_PINCH = true && !PRODUCTION_RELEASE; //false;
    public static final int TEST_PINCH_SCR_WIDTH_PERCENT = 10; //% of right-hand side of screen where a touch is programatically converted to a pinch

    public static final boolean TEST_SHOW_ITEM_TEXT_AS_OBJECTID = false && !PRODUCTION_RELEASE; //false;
    public static final boolean DEBUG_LOGGING = true && !PRODUCTION_RELEASE; //logs only enabled during 
    public static final boolean WORKTIME_TEST = false && !PRODUCTION_RELEASE; //
    public static final boolean WORKTIME_DETAILED_LOG = false && !PRODUCTION_RELEASE; //NB! these logs are very time-consuming!!

    public static final boolean INLINE_WORKSHOP_TESTCASE = false && !PRODUCTION_RELEASE;
    public static final boolean REFRESH_EVEN_THOUGH_DONE_IN_BACK = false && !PRODUCTION_RELEASE; //true <=> assumes that when going back to a screen, refresh is done in back. This removes the opportunity to optimize by avoiding unnecessary refresh if going back from screens which don't alter the content and therefore don't require refresh
    public static final boolean ENABLE_ASK_USER_TO_RATE_APP = false && !PRODUCTION_RELEASE; //ask users to rate the app, requires url to appstore/android store
}
