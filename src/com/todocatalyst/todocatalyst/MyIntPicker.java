/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
    class MyIntPicker extends Picker {
        
        int intMin = Integer.MIN_VALUE;
        int intMax = Integer.MAX_VALUE;
//        int intDefault = 0;

//                MyStringPicker(String[] stringArray, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set) {
        MyIntPicker(ParseIdMap2 parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue) {
            this(parseIdMap, get, set, minValue, maxValue, 1);
        }
        
        MyIntPicker(Integer value, Integer minValue, Integer maxValue) {
            this(value, minValue, maxValue, 1);
        }
        
        MyIntPicker(Integer value, Integer minValue, Integer maxValue, Integer step) {
            super();
            if (minValue != null) {
                this.intMin = minValue;
            }
            if (maxValue != null) {
                this.intMax = maxValue;
            }
//            int defaultSelectedValue = get.get();
            assert minValue < maxValue && step != 0 && value >= minValue && value <= maxValue && (value - minValue) % step == 0 : "wrong init values";
            this.setType(Display.PICKER_TYPE_STRINGS);
            int i = minValue;
            int count = 0;
            String[] strings = new String[((maxValue - minValue) / step) + 1];
            while (i <= maxValue) {
                strings[count] = Integer.toString(i);
                count++;
                i += step;
            }
            
            this.setStrings(strings);
            this.setSelectedString(Integer.toString(value));
        }
        
        MyIntPicker(ParseIdMap2 parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue, int step) {
            this(get.get(), minValue, maxValue, step);
            if (parseIdMap != null) {
                parseIdMap.put(this, () -> {
//                    String str = this.getSelectedString();
//                    set.accept(Integer.parseInt(str));
                    set.accept(getValueInt());
                });
            }
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyIntPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, int minValue, int maxValue, int step) {
//            super();
//            int defaultSelectedValue = get.get();
//            assert minValue < maxValue && step != 0 && defaultSelectedValue >= minValue && defaultSelectedValue <= maxValue && (defaultSelectedValue - minValue) % step == 0 : "wrong init values";
//            this.setType(Display.PICKER_TYPE_STRINGS);
//            int i = minValue;
//            int count = 0;
//            String[] strings = new String[((maxValue - minValue) / step) + 1];
//            while (i <= maxValue) {
//                strings[count] = Integer.toString(i);
//                count++;
//                i += step;
//            }
//
//            this.setStrings(strings);
////        this.setSelectedString(stringArray[get.get()]);
//            this.setSelectedString(Integer.toString(defaultSelectedValue));
//            if (parseIdMap != null) {
//                parseIdMap.put(this, () -> {
//                    String str = this.getSelectedString();
//                    set.accept(Integer.parseInt(str));
//                });
//            }
//        }
//</editor-fold>
        public int getValueInt() {
//            String str = getSelectedString();
//            int interval;
//            if (str != null && str.length() > 0) {
//                interval = Integer.parseInt(str);
//                if (interval < intMin || interval > intMax) {
//                    interval = intDefault;
//                }
//            }
            return Integer.parseInt(getSelectedString());
        }
    }
