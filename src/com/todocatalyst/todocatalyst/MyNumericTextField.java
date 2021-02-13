/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Component;
import com.codename1.ui.Display;
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    MyNumericTextField(String hint, ParseIdMap2 parseIdMap, MyForm.GetDouble getValue, MyForm.PutDouble setValue) {
//        super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
////                        super("", 1, columns, constraint);
////            setHint(hint);
//        putClientProperty("iosHideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
//        putClientProperty("ios.hideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
//        putClientProperty("ios.HideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
//        setGrowByContent(false);
//        setAlignment(Component.RIGHT);
//        setAutoDegradeMaxSize(true);
////            setSameWidth(new Label("9999"));
////            setGrowLimit(maxRows);
////            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
////            setMaxSize(maxTextSize);
//        Double val = getValue.get();
//        if (val != 0) {
////                this.setText(getValue.get() + "");
////                DecimalFormat df2 = new DecimalFormat(".##");
////                String s = df2.format(val);
//            String s = L10NManager.getInstance().format(val, 2);
////                this.setText(val..get() + "");
//            this.setText(s);
//        }
////            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
//        if (parseIdMap != null) {
//            parseIdMap.put(this, () -> setValue.accept(getText().equals("") ? 0 : Double.valueOf(getText())));
//        }
//    }
//</editor-fold>

    MyNumericTextField(String hint) {
        super("", hint, COLUMNS_FOR_INT, TextArea.DECIMAL);
        if (false&&Config.TEST) {
            putClientProperty("iosHideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
            putClientProperty("ios.hideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
            putClientProperty("ios.HideToolbar", Boolean.FALSE); //TRUE will hide the toolbar and only show Done button
            setDoneListener((e) -> Log.p("setDoneListener: Done on MyNumericTextField=" + this));
            addLongPressListener((e) -> Log.p("addLongPressListener: LongPress on MyNumericTextField=" + this));
            Display.getInstance().setProperty("ios.doneButtonColor", String.valueOf(0xff0000));
        }
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
