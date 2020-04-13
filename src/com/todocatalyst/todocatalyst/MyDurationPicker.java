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

    private final static String DEFAULT_ZERO_VALUE_PATTERN = "";
//    private String zeroValuePattern = DEFAULT_ZERO_VALUE_PATTERN;
    private String zeroValuePattern = null;
//    private int defaultValueInMinutes = 0;
    private long preserveSecondsAndMillis = 0; //preserve seconds and milliseconds so if the picker is used eg for Actuals, and closed with Done, but value not changed, that the value won't dhange and trigger an errenous update of the item
    private boolean preserveSecondsWhenEditing = false; //preserve seconds and milliseconds so if the picker is used eg for Actuals, and closed with Done, but value not changed, that the value won't dhange and trigger an errenous update of the item
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">

////    @Override
//    protected void updateValueXXX() {
////        if (getType()==Display.PICKER_TYPE_TIME && getTime() == 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
////        if (getType()==Display.PICKER_TYPE_TIME && getTime() != 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
////        if (getType() == Display.PICKER_TYPE_DURATION && getTime() == 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
//        if (getType() == Display.PICKER_TYPE_DURATION && getDuration() == 0 && zeroValuePattern != null) { //getType()==Display.PICKER_TYPE_TIME needed since updateValue is called in constructor before value is set to an Integer
//            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
//        } else {
//            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
//        }
//    }
//    MyDurationPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
//        this(null, parseIdMap, getDurationInMinutes, setDurationInMinutes);
//    }
//    MyDurationPicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetLong getDurationInMillis, MyForm.PutLong setDurationInMillis) {
//        this(null, parseIdMap, getDurationInMillis, setDurationInMillis);
//    }

//    MyDurationPicker(String zeroValuePatternVal, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
//        this(zeroValuePatternVal, 0, parseIdMap, getDurationInMinutes, setDurationInMinutes);
//    }
//    private MyDurationPicker(String zeroValuePatternVal, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetLong getDurationInMillis, MyForm.PutLong setDurationInMillis) {
//        this(zeroValuePatternVal, 0, parseIdMap, getDurationInMillis, setDurationInMillis);
//    }
//    private MyDurationPicker(String zeroValuePatternVal, MyForm.GetLong getDurationInMillis, MyForm.PutLong setDurationInMillis) {
//        this(zeroValuePatternVal, 0, null, getDurationInMillis, setDurationInMillis);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    MyDurationPicker(int defaultValueInMinutes) {
//        this("", defaultValueInMinutes, null, null, null);
//    }
//    MyDurationPicker(int defaultValueInMillis) {
//        this("", defaultValueInMillis, null, null, null);
//    }
//</editor-fold>
    MyDurationPicker(long durationInMillis, String zeroValuePatternVal) {
        super();
        this.setType(Display.PICKER_TYPE_DURATION);
        if (zeroValuePatternVal != null) {
            this.zeroValuePattern = zeroValuePatternVal;
        }
        setDuration(durationInMillis);
        setUIID("ScreenItemEditableValue");
//        this.zeroValuePattern = ""; // "<set>";
        setFormatter(new SimpleDateFormat() {
            public String format(Object durationInMillis) {
                long valInMillis = ((Long) durationInMillis).longValue();
                if (valInMillis == 0 && zeroValuePattern != null) {
                    return zeroValuePattern;
                }
                if (true) {
                    long seconds = (valInMillis % MyDate.MINUTE_IN_MILLISECONDS) / MyDate.SECOND_IN_MILLISECONDS;
                    if (seconds != 0 && MyPrefs.durationPickerShowSecondsIfLessThan1Minute.getBoolean()) {
                        return MyDate.formatDurationStd((Long) durationInMillis, true); //if any seconds in time, show them
                    } else {
                        return MyDate.formatDurationStd((Long) durationInMillis);
                    }
                } else
                    return MyDate.formatDurationStd((Long) durationInMillis);
            }
        });
        setMinuteStep(MyPrefs.durationPickerMinuteStep.getInt()); //TODO!! setting to select interval of 1/5 minutes
    }

    MyDurationPicker(long durationInMillis) {
        this(durationInMillis, DEFAULT_ZERO_VALUE_PATTERN);
    }

    MyDurationPicker(long durationInMillis, boolean useDefaultZeroPattern) {
        this(durationInMillis, DEFAULT_ZERO_VALUE_PATTERN);
    }

    MyDurationPicker() {
        this(0, DEFAULT_ZERO_VALUE_PATTERN);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    MyDurationPicker(String zeroValuePatternVal, int defaultValueInMinutes, Map<Object, MyForm.UpdateField> parseIdMap,
//            MyForm.GetInt getDurationInMinutes, MyForm.PutInt setDurationInMinutes) {
//    private MyDurationPicker(String zeroValuePatternVal, int defaultValueInMinutes,
//            MyForm.GetLong getDurationInMillis) {
//        this(zeroValuePatternVal, defaultValueInMinutes, null, getDurationInMillis, null);
//    }

//    private MyDurationPicker(String zeroValuePatternVal, int defaultValueInMinutes, Map<Object, MyForm.UpdateField> parseIdMap,
//            MyForm.GetLong getDurationInMillis, MyForm.PutLong setDurationInMillis) {
//    private MyDurationPicker(String zeroValuePatternVal, int defaultValueInMinutes) {
//        this( defaultValueInMinutes * MyDate.MINUTE_IN_MILLISECONDS);
//        if (zeroValuePatternVal != null) {
//            this.zeroValuePattern = zeroValuePatternVal;
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        this.defaultValueInMinutes = defaultValueInMinutes;
////        Integer i = (getDurationInMinutes != null ? getDurationInMinutes.get() : this.defaultValueInMinutes);
////        long l = (getDurationInMillis != null ? getDurationInMillis.get() : this.defaultValueInMinutes * MyDate.MINUTE_IN_MILLISECONDS);
////        if (l != 0) {
////            this.setDuration(l);
////        }
////</editor-fold>
////        if (parseIdMap != null) {
////            parseIdMap.put(this, () -> setDurationInMillis.accept(this.getDuration()));
////        }
//    }
//    public void setDurationMinutesXXX(int minutes) {
//        this.setDuration(((long) minutes) * MyDate.MINUTE_IN_MILLISECONDS);
//    }
//    private void notifyMyActionListenersXXX() {
//        for (Object al : getListeners()) {
//            if (al instanceof MyActionListener) {
//                ((MyActionListener) al).actionPerformed(null);
//            }
//        }
//    }
//</editor-fold>
    /**
    set the duration
    @param timeInMillis 
     */
    @Override
    public void setDuration(long timeInMillis) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (clearButton != null) {
////            clearButton.setHidden(time == 0); //hide clear button when field is cleared
//            clearButton.setVisible(timeInMillis != 0); //hide clear button when field is cleared, show if value is set
//        }
//        long millis = timeInMillis % MyDate.MINUTE_IN_MILLISECONDS;
//        if (true || millis != 0) {
//            preserveSeconds = millis; //always save milliseconds
//        }
//</editor-fold>
        if (preserveSecondsWhenEditing)
            preserveSecondsAndMillis = timeInMillis % MyDate.MINUTE_IN_MILLISECONDS;; //always save seconds
//        super.setDuration(((long) minutes) * MyDate.MINUTE_IN_MILLISECONDS);
        super.setDuration(timeInMillis);
//        notifyMyActionListeners();
    }

    /**
    set duration and notify listeners like if the picker had been used manually
    @param timeInMillis 
     */
    public void setDurationAndNotify(long timeInMillis) {
        setDuration(timeInMillis);
//        notifyMyActionListeners();
//        fireClicked();
        fireActionEvent(-99, -99); //-99 used in CN1 Picker to ignore built-in action listener
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setDurationXXX(long timeInMinutes) {
////        if (clearButton != null) {
//////            clearButton.setHidden(time == 0); //hide clear button when field is cleared
////            clearButton.setVisible(timeInMinutes != 0); //hide clear button when field is cleared
////        }
////        super.setTime(timeInMinutes);
//        super.setDuration(((long) timeInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
//        //inform my own MyActionListeners when the field is changed directly via setTime()
//        for (Object al : getListeners()) {
//            if (al instanceof MyActionListener) {
//                ((MyActionListener) al).actionPerformed(null);
//            }
//        }
//    }
//</editor-fold>
    @Override
    public long getDuration() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        long millis = editedMillis % 1000;
//        if (millis == 0 && preserveSeconds != null) { //if milliseconds were removed during editing, and were preserved before editing, then add back again
//            editedMillis += preserveSeconds; //add the missing millis back again
//            preserveSeconds = null;
//        }
//</editor-fold>
        long editedMillis;
        if (preserveSecondsWhenEditing) {
            preserveSecondsAndMillis = 0;
            editedMillis = super.getDuration() + preserveSecondsAndMillis;
        } else {
            editedMillis = super.getDuration();
        }
//            preserveSeconds = 0;  //DON'T reset, since same picker may be activated multiple times
        return editedMillis;
    }

//    public int getDurationMinutesXXX() {
//        return (int) getDuration() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
    /**
    pattern show when zero value, in most screens where there is an edit button, "" is the best pattern, but for example in Timer, "0:00" (or similar localized version) shows that there is an editable/clickable
    @param zeroValuePattern 
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setZeroValuePattern(String zeroValuePattern) {
//        this.zeroValuePattern = zeroValuePattern;
//    }
//</editor-fold>
    public void setShowZeroValueAsZeroDuration(boolean showZeroValuePattern) {
        MyDate d;
        if (showZeroValuePattern) {
            this.zeroValuePattern = "0:00"; //MyDate.formatTimeDuration(0, 0); //TODO!!!! localize zero duration
        } else {
            this.zeroValuePattern = DEFAULT_ZERO_VALUE_PATTERN;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button clearButton = null;
//    public void setClearButton(Button clearButton) {
//        this.clearButton = clearButton;
//    }
//    @Override
//    public int getTime() { //return time in Minutes
//        return (int) (getDuration() / MyDate.MINUTE_IN_MILLISECONDS);
//    }
//    @Override
//    public void setTimeXXX(int timeInMinutes) {
////        if (clearButton != null) {
//////            clearButton.setHidden(time == 0); //hide clear button when field is cleared
////            clearButton.setVisible(timeInMinutes != 0); //hide clear button when field is cleared
////        }
////        super.setTime(timeInMinutes);
//        super.setDuration(((long) timeInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
//        //inform my own MyActionListeners when the field is changed directly via setTime()
//        for (Object al : getListeners()) {
//            if (al instanceof MyActionListener) {
//                ((MyActionListener) al).actionPerformed(null);
//            }
//        }
//    }
//</editor-fold>
    void swipeClear() {
        setDurationAndNotify(0L); //will notify myActionListeners
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void fireClicked() { //needed to give access to 'click' the button programmatically
//        super.fireClicked();
//    }
//</editor-fold>
};
