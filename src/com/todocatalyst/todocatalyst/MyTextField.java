/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.TextArea;
import static com.codename1.ui.TextArea.setAutoDegradeMaxSize;
import com.codename1.ui.TextComponent;
import com.codename1.ui.TextField;
import static com.todocatalyst.todocatalyst.MyForm.COLUMNS_FOR_STRING;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
class MyTextField extends TextField {

    MyTextField(String hint, ParseIdMap2 parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, COLUMNS_FOR_STRING, TextArea.ANY | TextArea.INITIAL_CAPS_SENTENCE, parseIdMap, getValue, setValue);
    }

    MyTextField(String hint, int columns, int constraint, ParseIdMap2 parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, columns, 128, constraint, parseIdMap, getValue, setValue); //UI: 128 = default max size of a text field //TODO: make a preference or PRO feature
    }

    MyTextField(String hint, int columns, int maxTextSize, int constraint, ParseIdMap2 parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, columns, maxTextSize, constraint, parseIdMap, getValue, setValue, TextField.LEFT);
    }

    MyTextField(String hint, int columns, int maxTextSize, int constraint, ParseIdMap2 parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue, int alignment) {
        this(hint, columns, 1, 1, maxTextSize, constraint, parseIdMap, getValue, setValue, alignment);
    }
//            MyTextArea(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint) {

    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint) {
        this(hint, columns, rows, maxRows, maxTextSize, constraint, null, null, null, TextField.LEFT);
    }

    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint, ParseIdMap2 parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue, int alignment) {
        super("", hint, columns, constraint);
        if (false) {
            TextComponent description = new TextComponent().label("Description").multiline(true);
        }
//        setIgnorePointerEvents(true);xxx;
        setBlockLead(false);//xxx;
        if(false)putClientProperty("iosHideToolbar", Boolean.TRUE); //TRUE will hide the toolbar and only show Done button

        setAlignment(alignment);
        setAutoDegradeMaxSize(true);
        if (rows > maxRows)
            rows = maxRows;
//            if (false) 
        setRows(rows);
        setGrowByContent(rows < maxRows);
        setSingleLineTextArea(maxRows <= 1);
        setGrowLimit(maxRows);
        if (false && rows > 0) {
//                setRows(rows);
            if (maxRows > rows)
                setGrowLimit(maxRows);
        }
//            setGrowLimit(maxRows);
        setHint(hint);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
        setMaxSize(maxTextSize);
        if (getValue != null)
            setText(getValue.get());
        if (parseIdMap != null && setValue != null)
            parseIdMap.put(this, () -> setValue.accept(getText()));
    }

}

//<editor-fold defaultstate="collapsed" desc="comment">
//class MyTextField extends TextField {
//
////            String title;
////            String parseId;
//    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint,
//            Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
//        super("", hint, columns, constraint);
////            if (rows != 1) {
////                setRows(rows);
////            }
//        setGrowByContent(true);
//        setAutoDegradeMaxSize(true);
//        setGrowLimit(maxRows);
//        setHint(hint);
////            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
//        setMaxSize(maxTextSize);
//        setText(getValue.get());
//        parseIdMap.put(this, () -> setValue.accept(getText()));
//    }
//
//    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint) {
//        super("", hint,  columns, constraint);
////            if (rows != 1) {
////                setRows(rows);
////            }
//        setGrowByContent(true);
//        setAutoDegradeMaxSize(true);
//        setGrowLimit(maxRows);
//        setHint(hint);
////            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
//        setMaxSize(maxTextSize);
//    }
//
//    MyTextField(String hint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
//        this(hint, COLUMNS_FOR_STRING, TextArea.ANY, parseIdMap, getValue, setValue);
//    }
//
//    MyTextField(String hint, int columns, int constraint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
//        this(hint, columns, 1, 1, 128, constraint, parseIdMap, getValue, setValue); //UI: 128 = default max size of a text field //TODO: make a preference or PRO feature
//    }
//};
//</editor-fold>
