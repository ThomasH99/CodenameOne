/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

/**
 * creates a boolean togglebutton with two text labels
 * @author Thomas
 */
public class MyToggleButtonBoolean extends MyToggleButton {
    
//    MyToggleButtonBoolean(boolean value, String trueText, String falseText) {
//        super(new String[] {falseText, trueText}, new int[] {0,1}, value?1:0); //1=true
//    }
    
    MyToggleButtonBoolean(boolean value, String falseText, String trueText) {
        super(new String[] {falseText, trueText}, new int[] {0, 1}, value?1:0); //1=true
    }
    
    public boolean isTrue() {
        return super.getSelectedValue()==1;
    }
    
//    public boolean getSelectedValue() {
//        return isTrue();
//    }
    
    }
