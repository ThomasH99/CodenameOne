/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.l10n.L10NManager;
import com.codename1.ui.Component;
import com.codename1.ui.TextArea;
import static com.codename1.ui.TextArea.setAutoDegradeMaxSize;
import com.codename1.ui.TextField;
import static com.todocatalyst.todocatalyst.MyForm.COLUMNS_FOR_INT;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
    
    class MyNumericTextField extends TextField {
        
        MyNumericTextField(String hint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDouble getValue, MyForm.PutDouble setValue) {
            super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
//                        super("", 1, columns, constraint);
//            setHint(hint);
            setGrowByContent(false);
            setAlignment(Component.RIGHT);
//            setSameWidth(new Label("9999"));
            setAutoDegradeMaxSize(true);
//            setGrowLimit(maxRows);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
//            setMaxSize(maxTextSize);
            Double val = getValue.get();
            if (val != 0) {
//                this.setText(getValue.get() + "");
//                DecimalFormat df2 = new DecimalFormat(".##");
//                String s = df2.format(val);
                String s = L10NManager.getInstance().format(val, 2);
//                this.setText(val..get() + "");
                this.setText(s);
            }
//            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
            if (parseIdMap != null) {
                parseIdMap.put(this, () -> setValue.accept(getText().equals("") ? 0 : Double.valueOf(getText())));
            }
        }
        
        MyNumericTextField(String hint) {
            super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
            setGrowByContent(false);
            setAlignment(Component.RIGHT);
//            setSameWidth(new Label("9999"));
            setAutoDegradeMaxSize(true);
//            setGrowLimit(maxRows);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
//            setMaxSize(maxTextSize);
        }
        
        void setVal(double val) {
            if (val != 0) {
//                DecimalFormat df2 = new DecimalFormat(".##");
//                String s = df2.format(val);
                String s = L10NManager.getInstance().format(val, 2);;
                this.setText(s);
            }
        }
    }
