/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Thomas
 */
public class MyDateAndTimePicker extends Picker implements SwipeClear{

    private String zeroValuePattern;
    private Button clearButton = null;

//        String title;
//        String parseId;
//        MyDateAndTimePicker(String title, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
    MyDateAndTimePicker(Date date, String zeroValuePatternVal) {
        super();
//        setUIID("Button");
//        setUIID("Label");
        setUIID("LabelValue");
//            this.title = title;
//            this.parseId = parseId;
        zeroValuePattern = zeroValuePatternVal;
        if (zeroValuePattern == null) {
            this.zeroValuePattern = "";// "<set>";
        }
        setType(Display.PICKER_TYPE_DATE_AND_TIME);
//            this.addActionListener(
//                    (e) -> {
//                        if (getDate().getTime() == 0) {
//                            setDate(new Date());
//                        }
//
//                    }); //set date to Now if empty when button is clicked
//            this.setDate(parseObject.getDate(parseId)); //!doesn't work for null value
//        Date d = get.get();
        if (date != null && date.getTime() != 0) {
            this.setDate(date);
        } else {
            setDate(new Date(0)); //UI: default date is undefined
        }
    }

    MyDateAndTimePicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
        this(null, parseIdMap, get, set);
    }

    MyDateAndTimePicker(String zeroValuePatternVal, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
//            this.setFormatter(new MySimpleDateFormat(this.getFormatter().toPattern(), zeroValuePattern)); //reuse default formatter pattern, only override for 0 value
        this(get.get(), zeroValuePatternVal);
//            parseIdMap.put(parseId,() -> parseObject.put(parseId, this.getDate()));
        parseIdMap.put(this,
                () -> set.accept(this.getDate()));
    }

    public void swipeClear() {
        setDate(new Date(0));
    }

    public void clearFieldValue() {
        swipeClear();
    }

    @Override
    public void pressed() {
        //set date to Now if empty when button is clicked
        if (getDate().getTime() == 0) {
//                setDate(new Date());
//TODO!! enable setting the default value in the constructor, e.g. tomorrow+5 days
            getDate().setTime(new Date().getTime()); //use this instead of setDate to set date to avoid updating label before showing ticker
        }
        super.pressed();
    }

//    @Override
    protected void updateValue() {
        Date date = getDate();
        if (date != null && date.getTime() == 0 && zeroValuePattern != null) {
            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
        } else {
            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
        }
    }

//    public void setClearButton(Button clearButton) {
//        this.clearButton = clearButton;
//    }
//    @Override
    public void setDateXX(Date date) {
        if (clearButton != null) {
//            clearButton.setHidden(date.getTime() == 0);
            clearButton.setVisible(date.getTime() != 0);
        }
        super.setDate(date);
        for (Object al : getListeners()) {
            if (al instanceof MyActionListener) {
                ((MyActionListener) al).actionPerformed(null);
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    Component makeContainerWithClearButtonXXX() {
////        Button clearButton = new Button();
////        timePicker.setClearButton(clearButton);
////        clearButton = new Button();
////        Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//        clearButton = new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            setDate(new Date(0)); //will hide the button
////            for (Object al : getListeners()) {
////                if (al instanceof MyActionListener) {
////                    ((MyActionListener) al).actionPerformed(null);
////                }
////            }
//        }));
//        //TODO!!!! reduce distance between clearButton and field, define a separate style for the button (getStyle is not the right approach)
////        clearButton.getStyle().setMarginLeft(0);
////        clearButton.setCommand(clear);
////        clearButton.setHidden(getDate().getTime() == 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        clearButton.setVisible(getDate().getTime() != 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        //listener to hide/show clear button when field is empty/not empty
//        addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                setTime(0);
////          clearButton.setHidden(getTime() == 0); //NO, done in setTime()
////                clearButton.setHidden(getDate().getTime() == 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//                clearButton.setVisible(getDate().getTime() != 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//            }
//        });
////        return LayeredLayout.encloseIn(this, FlowLayout.encloseRightMiddle(clearButton));
//        return FlowLayout.encloseRightMiddle(this, clearButton);
//    }
//</editor-fold>

}
