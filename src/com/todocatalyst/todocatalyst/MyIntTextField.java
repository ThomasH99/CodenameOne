/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.TextArea;
import static com.codename1.ui.TextArea.setAutoDegradeMaxSize;
import com.codename1.ui.TextField;
import static com.todocatalyst.todocatalyst.MyForm.COLUMNS_FOR_INT;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
    class MyIntTextField extends TextField {
        
        int intMin = Integer.MIN_VALUE;
        int intMax = Integer.MAX_VALUE;
        int intDefault = 0;
        
        MyIntTextField(Integer initialValue, String hint, Integer intMin, Integer intMax, Integer defaultValue) {
            super("", hint, COLUMNS_FOR_INT, TextArea.NUMERIC);
            if (intMin != null) {
                this.intMin = intMin;
            }
            if (intMax != null) {
                this.intMax = intMax;
            }
            if (defaultValue != null) {
                this.intDefault = defaultValue;
            }
            setGrowByContent(false);
            setAutoDegradeMaxSize(true);
            if (initialValue != null) {
                this.setText(initialValue + "");
            }
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
        }
        
        public int getValue() {
            String str = getText();
            int interval = intDefault;
            if (str != null && str.length() > 0) {
                interval = Integer.parseInt(str);
                if (interval < intMin || interval > intMax) {
                    interval = intDefault;
                }
            }
            return interval;
        }
        
        MyIntTextField(String hint, ParseIdMap2 parseIdMap, MyForm.GetInt getValue, MyForm.PutInt setValue, Integer intMin, Integer intMax, Integer defaultValue) {
//            super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
            this(getValue.get(), hint, intMin, intMax, defaultValue);
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
            parseIdMap.put(this, () -> setValue.accept(getValue()));
        }
    }
    
