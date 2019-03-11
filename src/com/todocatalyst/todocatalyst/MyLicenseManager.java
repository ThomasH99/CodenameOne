/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author thomashjelm
 */
public class MyLicenseManager {

//    private Map<String, Boolean> lice
    private static MyLicenseManager INSTANCE = null;

    public static MyLicenseManager getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new TimerInstance();
//            DAO.getInstance().getTemplateList();
            INSTANCE = new MyLicenseManager(); //load subscriptions
            //check for expired subscriptions (in background)
        }
        return INSTANCE;
    }

    private String t = "Subscribe to enable restarting the app where you left last, including any entered data you did not save";

    private MyLicenseManager() {

    }

    public boolean isSubscribed(String featureId) {
        if (false) {
            //show popup with (current!!) subscription level + benefit description (get this from the web to improve text)!
        }
        return true;
    }

}
