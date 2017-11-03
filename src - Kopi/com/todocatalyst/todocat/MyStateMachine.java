/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.util.Resources;
import userclasses.StateMachine;

/**
 *
 * @author Thomas
 */
public class MyStateMachine { //extends StateMachine {
//    static String resFile;
    static Resources resources; 
    
//    MyStateMachine(String resFile) {
//        this();
////        super(resFile);
//        this.resFile = resFile;
//    }
    
    private static StateMachine INSTANCE;

    protected MyStateMachine() {
        try {
//            resources = Resources.open(Settings.getInstance().getThemesFilename()); //todoTheme1.res
//            resources = Resources.open("/leather.res");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static StateMachine getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new StateMachine(resFile);
            INSTANCE = new StateMachine(Settings.getInstance().getThemesFilename());
        }
        return INSTANCE;
    }
    

    
}
