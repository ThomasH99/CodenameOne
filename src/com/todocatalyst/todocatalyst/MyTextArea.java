/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.TextArea;
import static com.codename1.ui.TextArea.setAutoDegradeMaxSize;
import static com.todocatalyst.todocatalyst.MyForm.COLUMNS_FOR_STRING;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
class MyTextArea extends TextArea {

//            String title;
//            String parseId;
    MyTextArea(String hint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, COLUMNS_FOR_STRING, TextArea.ANY, parseIdMap, getValue, setValue);
    }

    MyTextArea(String hint, int columns, int constraint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        this(hint, columns, 1, 1, 128, constraint, parseIdMap, getValue, setValue); //UI: 128 = default max size of a text field //TODO: make a preference or PRO feature
    }
//        MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, Consumer<String> setValue, Supplier<String> getValue, ParseObject parseObject, String parseId) {

    MyTextArea(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetString getValue, MyForm.PutString setValue) {
        super("", rows, columns, constraint);
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

    MyTextArea(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint) {
        super("", rows, columns, constraint);
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

//        MyTextArea(String hint, int columns, int constraint, Map<Object, UpdateField> parseIdMap, GetDouble getValue, PutDouble setValue) {
//            super("", 1, columns, constraint);
//            setHint(hint);
//            if (getValue.get() != 0) {
//                this.setText(getValue.get() + "");
//            }
////            this.set //TODO how to ensure cursor is positioned at end of entered text and not beginning?
//            parseIdMap.put(this, () -> setValue.accept(getText().equals("") ? 0 : Double.valueOf(getText())));
//        }
//            MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//                super("", hint, columns, constraint);
////                this.title = title;
////                this.parseId = parseId;
//                setText(parseObject.getString(parseId));
////                parseIdMap.put(parseId, this);
////            parseIdMap.put(parseId, () -> getText());
//                parseIdMap.put(parseId, () -> parseObject.put(parseId, getText()));
//            }
};
