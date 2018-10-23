/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.Map;

/**
 *
 * @author Thomas
 */
class MyDurationPicker extends Picker implements SwipeClear {

    private String zeroValuePattern;
    private int defaultValueInMinutes;
//        String title;
//        String parseId;
//    @Override
//    public void pressed() {
//        //set date to Now if empty when button is clicked
//        if (getTime() == 0) { //TODO!! getTime() tries to cast the default set Date() to an integer!!
////                setDate(new Date());
//            setTime(defaultValueInMinutes); //use this instead of setDate to set date to avoid updating label before showing ticker
//        }
//        super.pressed();
//    }
//
//    @Override
//    protected void updateValue() {
//        if (getTime() == 0 && zeroValuePattern != null) {
//            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
//        } else {
//            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
//        }
//    }

    @Override
    protected void updateValue() {
//        if (getType()==Display.PICKER_TYPE_TIME && getTime() != 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
//        if (getType()==Display.PICKER_TYPE_TIME && getTime() == 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
        if (getType() == Display.PICKER_TYPE_DURATION && getTime() == 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
        } else {
            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    MyDurationPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
        this(null, parseIdMap, getDurationInMinutes, setDurationInMinutes);
    }

    MyDurationPicker(String zeroValuePatternVal, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
        this(zeroValuePatternVal, 0, parseIdMap, getDurationInMinutes, setDurationInMinutes);
    }

    MyDurationPicker(int defaultValueInMinutes) {
        this("", defaultValueInMinutes, null, null, null);
    }

    MyDurationPicker(String zeroValuePatternVal, int defaultValueInMinutes, Map<Object, MyForm.UpdateField> parseIdMap,
            MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
        super();
//        setUIID("Button");
        setUIID("LabelValue");
//            this.title = title;
//            this.parseId = parseId;
//        this.setType(Display.PICKER_TYPE_TIME);
        this.setType(Display.PICKER_TYPE_DURATION);
        this.zeroValuePattern = zeroValuePatternVal;
        if (this.zeroValuePattern == null) {
            this.zeroValuePattern = ""; // "<set>";
        }
        this.defaultValueInMinutes = defaultValueInMinutes;
//        Integer i = (getDurationInMinutes != null ? getDurationInMinutes.get() : this.defaultValueInMinutes);
        int i = (getDurationInMinutes != null ? getDurationInMinutes.get() : this.defaultValueInMinutes);
        if (i != 0) {
//            this.setTime(i);
            this.setDuration(((long) i) * MyDate.MINUTE_IN_MILLISECONDS);
        } else {
            //
        }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getTime()));
        if (parseIdMap != null) {
            parseIdMap.put(this, () -> setDurationInMinutes.accept(this.getTime()));
        }
//        setFormatter((s)->{return MyDate.formatTimeDuration(s);});
        setFormatter(new SimpleDateFormat() {
            public String format(Object value) {
                return MyDate.formatTimeDuration((Long) value);
            }
        });
        setMinuteStep(1);
    }

    MyDurationPicker() {
        super();
        setUIID("LabelValue");
        this.setType(Display.PICKER_TYPE_DURATION);
        this.zeroValuePattern = ""; // "<set>";
        this.defaultValueInMinutes = 0;
//        setFormatter(new SimpleDateFormat() {
//            public String format(Object value) {
//                return MyDate.formatTimeDuration((Long) value);
//            }
//        });
        setMinuteStep(MyPrefs.durationPickerMinuteStep.getInt()); //TODO!! setting to select interval of 1/5 minutes
    }

    public void setDurationMinutes(int minutes) {
        this.setDuration(((long) minutes) * MyDate.MINUTE_IN_MILLISECONDS);
    }

    public int getDurationMinutes() {
        return (int) getDuration() / MyDate.MINUTE_IN_MILLISECONDS;
    }

    private Button clearButton = null;

//    public void setClearButton(Button clearButton) {
//        this.clearButton = clearButton;
//    }
    @Override
    public int getTime() { //return time in Minutes
        return (int) (getDuration() / MyDate.MINUTE_IN_MILLISECONDS);
    }

    @Override
    public void setTime(int timeInMinutes) {
        if (clearButton != null) {
//            clearButton.setHidden(time == 0); //hide clear button when field is cleared
            clearButton.setVisible(timeInMinutes != 0); //hide clear button when field is cleared
        }
//        super.setTime(timeInMinutes);
        super.setDuration(((long) timeInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
        //inform my own MyActionListeners when the field is changed directly via setTime()
        for (Object al : getListeners()) {
            if (al instanceof MyActionListener) {
                ((MyActionListener) al).actionPerformed(null);
            }
        }
    }

    void swipeClear() {
        setTime(0);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    Component makeContainerWithClearButtonXXX() {
////        Button clearButton = new Button();
////        timePicker.setClearButton(clearButton);
////        clearButton = new Button();
////        Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//        clearButton = new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            setTime(0); //will hide the button
////            for (Object al : getListeners()) {
////                if (al instanceof MyActionListener) {
////                    ((MyActionListener) al).actionPerformed(null);
////                }
////            }
//        }));
////        clearButton.setCommand(clear);
////        clearButton.setHidden(getTime() == 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        clearButton.setVisible(getTime() != 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        //listener to hide/show clear button when field is empty/not empty
//        addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                setTime(0);
////                clearButton.setHidden(getTime() == 0); //NO, done in setTime()
////                clearButton.setHidden(getTime() == 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//                clearButton.setVisible(getTime() != 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//            }
//        });
////        return LayeredLayout.encloseIn(this, FlowLayout.encloseRightMiddle(clearButton));
//        return FlowLayout.encloseRightMiddle(this, clearButton);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    static Component create(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set) {
//        return create(parseIdMap, get, set, true);
//    }
//
//    static Component create(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt get, MyForm.PutInt set, boolean addClearButton) {
//        Button clearButton = new Button();
//
//        MyDurationPicker p = new MyDurationPicker(parseIdMap, get, set) {
//            @Override
//            public void setTime(int time) {
//                super.setTime(time); //To change body of generated methods, choose Tools | Templates.
//                if (clearButton != null) {
//                    clearButton.setHidden(getTime() == 0);
//                }
////                    for (Object al : MyDurationPicker.this.getListeners()) {
//                for (Object al : getListeners()) {
//                    if (al instanceof MyActionListener) {
//                        ((MyActionListener) al).actionPerformed(null);
//                    }
//                }
//            }
//        };
//
//        if (addClearButton) {
//            Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//                p.setTime(0);
//            });
//            clearButton.setCommand(clear);
//            clearButton.setHidden(p.getTime() == 0);
////<editor-fold defaultstate="collapsed" desc="comment">
////listener to hide/show clear button when field is empty/not empty
////                addActionListener(new MyActionListener() {
////                    @Override
////                    public void actionPerformed(ActionEvent evt) {
////                        clearButton.setHidden(getTime() == 0);
////                    }
////                });
////</editor-fold>
//            return LayeredLayout.encloseIn(p, FlowLayout.encloseRightMiddle(clearButton));
//        }
//        return p;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDurationPicker(String title, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//            super();
//            this.title = title;
//            this.parseId = parseId;
//            this.setType(Display.PICKER_TYPE_TIME);
//            Integer i = parseObject.getInt(parseId);
//            if (i != null) {
//                this.setTime(i);
//            }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getTime()));
//        }
//        String getTitle() {
//            return title;
//        }
//</editor-fold>
    @Override
    public void clearFieldValue() {
        swipeClear();
    }
};
