/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.TextArea;
import static com.codename1.ui.TextArea.setAutoDegradeMaxSize;
import com.codename1.ui.TextField;
import static com.todocatalyst.todocatalyst.MyForm.COLUMNS_FOR_STRING;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
class MyTextField extends TextField {

//            String title;
//            String parseId;
    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint, 
            Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        super("", hint, columns, constraint);
//            if (rows != 1) {
//                setRows(rows);
//            }
        setGrowByContent(true);
        setAutoDegradeMaxSize(true);
        setGrowLimit(maxRows);
        setHint(hint);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
        setMaxSize(maxTextSize);
        setText(getValue.get());
        parseIdMap.put(this, () -> setValue.accept(getText()));
    }

    MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint) {
        super("", hint,  columns, constraint);
//            if (rows != 1) {
//                setRows(rows);
//            }
        setGrowByContent(true);
        setAutoDegradeMaxSize(true);
        setGrowLimit(maxRows);
        setHint(hint);
//            setMaxSize(MyPrefs.getInt(MyPrefs.commentsAddTimedEntriesWithDateButNoTime));
        setMaxSize(maxTextSize);
    }

    MyTextField(String hint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, COLUMNS_FOR_STRING, TextArea.ANY, parseIdMap, getValue, setValue);
    }

    MyTextField(String hint, int columns, int constraint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, columns, 1, 1, 128, constraint, parseIdMap, getValue, setValue); //UI: 128 = default max size of a text field //TODO: make a preference or PRO feature
    }
};
