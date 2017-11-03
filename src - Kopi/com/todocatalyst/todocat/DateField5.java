/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.FlowLayout;
import java.util.Calendar;
import java.util.Vector;

/**
 * Can be used to edit Date/Time, Date only, or Time of day only, or Duration hh:mm up to 99:59
 *
 * Format:
 * D/d: day of month with preceeding 0 (07), d: day of month (7)
 * M/m: month January/Jan
 * L/l: month 09/9
 * W/w: weekday Monday/Mon
 * Y/y: Year 2009, 09
 * H: Hour 08
 * h: Hour 12h (am/pm)
 * a: am/pm, e.g. "h:n a" will give 11:30 pm
 * N/n: Minute 07
 * S: seconds 08
 * K/k: duration in hours (00-99, 0-99), combine with N minutes, S seconds
 * I: Integer, positive/negative
 *
 * See full set of chars in formatLabel() and getFormatField()
 *
 * Behaviour:
 * Fire = enter edit mode and give first time field focus.
 * If a defaultValue has been defined, AND the time == useDefaultForValue, then the defaultValue is preset.
 * It can be removed by pressing '#' or Del.
 * Fire again = quit editing (and move to next field like if down arrow pressed?!)
 * inside a time field:
 * arrow left/right: move to next/prev field.
 *    Inside last field right arrow exit edit mode and move to next editable field in form.
 *    Inside first field left arrow exit edit mode and move to previous editable field in form.
 * arrow up/down: increase/drecrease the value.
 * pressing one number 0-9: set field to this value
 * pressing two numbers 0-9: set field to this value if legal (otherwise only use last number?!) and move to next field
 * Year field: Pressing four numbers: use as year. pressing two numbers + Fire or Right arrow: add 2000. Exit edit mode.
 * TODO: allow editing fields in any order
 * TODO: support locale so formats are automatically selected (where to get local standard from??)
 * TODO: extend to cover editing time hh:mm alone (e.g. for time estimates). And editing time alone (no time hh:mm field).
 * TODO!!!: show '0' dates as empty
 * @author Thomas
 */
public class DateField5 extends Container implements ActionListener, DataChangedListener {

    public void dataChanged(int type, int index) {
    }

//    private class MyForm extends Form {
//        void setNextFieldFocused() {
//            focusDownSequence;
//        }
//    }
    private class MyLabel extends Label { //Button {

        public MyLabel(String text) {
            super(text);
//            setUIID("Button");
            setUIID("Date");
//            Style style = getStyle();
//            Style selectedStyle = getSelectedStyle();

//            style.setBorder(null);
//            style.setPadding(Component.LEFT, 1);
//            style.setPadding(Component.RIGHT, 1);
//            style.setPadding(Component.TOP, 0);
//            style.setPadding(Component.BOTTOM, 0);
//            style.setMargin(Component.LEFT, 0);
//            style.setMargin(Component.RIGHT, 0);
//            style.setMargin(Component.TOP, 0);
//            style.setMargin(Component.BOTTOM, 0);
//
//            selectedStyle.setPadding(Component.LEFT, 2);
//            selectedStyle.setPadding(Component.RIGHT, 2);
//            selectedStyle.setPadding(Component.TOP, 0);
//            selectedStyle.setPadding(Component.BOTTOM, 0);
//            selectedStyle.setMargin(Component.LEFT, 0);
//            selectedStyle.setMargin(Component.RIGHT, 0);
//            selectedStyle.setMargin(Component.TOP, 0);
//            selectedStyle.setMargin(Component.BOTTOM, 0);

//            selectedStyle.setBorder(Border.)

            setTickerEnabled(false);
            setEndsWith3Points(false);
            //getStyle().setPadding(0, 0, 0, 0);
        }

        public void keyPressed(int keyCode) {
            handleKey(keyCode);
            super.keyPressed(keyCode);
            /*if (!handlesInput()) {
            //Log.l("MyLabel keyPressed [!handlesInput()]: super.keyPressed(keyCode=" + keyCode + ");");
            super.keyPressed(keyCode);
            } else {
            //Log.l("MyLabel keyPressed [handlesInput()]: super.keyPressed(keyCode=" + keyCode + ");");
            //Log.l("MyLabel keyPressed: handleKey(keyCode);");
            handleKey(keyCode);
            if (!handlesInput()) {
            super.keyPressed(keyCode);
            }
            }*/
        }

        public void keyReleased(int keyCode) {
            super.keyReleased(keyCode);
            /*if (!handlesInput()) {
            //Log.l("MyLabel keyReleased: super.keyReleased(keyCode=" + keyCode + ");");
            super.keyReleased(keyCode);
            }*/
        }
    }
//    int hourIdx, minuteIdx, dayIdx, monthIdx, yearIdx, weekdayIdx;
//    private final static int UNKNOWN = 0;
    private final static int DAY = 1;
    private final static int MONTH = 2;
    private final static int YEAR = 3;
    private final static int HOUR24 = 4;
    private final static int HOUR12 = 5;
    private final static int MINUTE = 6;
    private final static int SECOND = 7;
    private final static int AMPM = 8;
    private final static int HOUR_DURATION = 9;
    private final static int WEEKDAY = 10;
    /** size of array needed to hold all the types */
//    private final static int TYPE_COUNT = WEEKDAY + 1;
//    final static String DEFAULT_DATE_FORMAT = "w D m Y"; //-now in Settings
    /** the edtiable field that is active by default when entering this editor. 0 = left-most field. */
    private final static int DEFAULT_ACTIVE_FIELD = 0;
    private final static int MILLISECONDS_PER_HOUR = 3600000;
    private final static int MILLISECONDS_PER_MINUTE = 60000;
    private final static int MILLISECONDS_PER_SECOND = 1000;
    /** stores the formatStr that the Date editor was initialized with */
//    private String formatStr;
    /** field of type Date (time + time of day) */
    private final static int UNDEFINED = 0;
    private final static int DATE = 1;
    /** field of type duration: integer with number of milliseconds */
    private final static int DURATION = 2;
    /** field of type integer: integer with number of milliseconds */
    private final static int INTEGER = 3;
    private final static int BOOLEAN = 4;
    /** stores the type of field being edited (DATE, DURATION, INTEGER) */
    private int editFieldType;
    /** stores the characters entered for the current field */
    private String str = "";
    /** stores the editable FormatFields (fixed labels are just placed and don't need to be accessed) */
    private Vector fieldList = new Vector();
    private int initActiveField = DEFAULT_ACTIVE_FIELD; // default value when entering editing // 1;//0;
    /** the currently active field index in fieldList (0==leftmost, fieldList.size()-1 == rightmost */
    private int activeField = initActiveField;
//    FlowLayout layout;
    private Vector actionListeners = null;
    /** stores the original time (before editing) */
    private long time;
    /** stores the original duration (before editing) */
//    private int duration;
    private boolean editable = true;

    /** stores the index of a field of a given type for quick access via e.g. ((FormatField)fieldList.elementAt(fieldIndex[MONTH])).value */
//    private int[] fieldIndex = new int[TYPE_COUNT]; //TODO: replace this by a simple search through the defined editable fields in fieldList
    /**
     *
     * @param format
     * @param time
     * @param initFieldIsTypeId if true, then initField should be DateField5.DAY/MONTH etc., otherwise it should be absolute field positin, eg 0 is leftmost
     * @param initField see previous parameter. If illegal value: TODO!!!
     */
    public DateField5(String format, int initField, boolean initFieldIsTypeId) {
        super(); //also initializes style?!
        //setStyle(UIManager.getInstance().getComponentStyle(getUIID()));
//        setStyle(UIManager.getInstance().getComponentStyle("Button"));
//        setUIID("Label");
//        setUIID("TextField");
        setUIID("Button"); //make the container look like a Button (TODO: or some other standard format for all input fields??!)
//        setUIID("Date"); //make the container look like a Button (TODO: or some other standard format for all input fields??!)
        //this.getStyle().setBorder(new Border());

        setOrientation(Component.CENTER);

//        initFormat("D/M/Y");
        initFormat(format);

//        setSubFieldsFocusedState(false);

        this.setFocusable(true); //make this date editor focusable (the container with all the labels inside)
//        this.setFocusPainted(true); //ensure that focus is painted for container

        if (initFieldIsTypeId) {
            initActiveField = getFieldIndex(initField); //fieldIndex[initField];
        } else {
            initActiveField = initField;
        }
    }

    public DateField5(String format, long time, int initField, boolean initFieldIsTypeId) {
        this(format, initField, initFieldIsTypeId);
        editFieldType = DATE;
        if (time == 0) {
            setAllFieldsVisible(false);
        }
        setTime(time);
    }

    /**
     *
     * @param time
     * @param initField set the field which is active for edting when entering (DAY, MONTH, ...) can improve ergonomics
     * by e.g. setting the most likely to be edited field like DAY active.
     */
    public DateField5(long time, int initField, boolean initFieldIsTypeId) {
        this(Settings.getInstance().getDefaultDateFormat(), time, initField, initFieldIsTypeId);
    }

//TODO!!!: update so that time==0 is shown as spaces (or some other way) and when edting: current time is used (or current + settings.something - set this when field is activated!!)
    //TODO!!!: make it possible to delete time (check mark?)
    /** edit time in format */
    public DateField5(String format, long time) {
        this(format, time, DEFAULT_ACTIVE_FIELD, false); //0: start in first (left-most) editable field
    }

    /** edit time in default date format */
    public DateField5(long time) {
        this(Settings.getInstance().getDefaultDateFormat(), time, DEFAULT_ACTIVE_FIELD, false);

    }

    /** edit current time in this format */
    DateField5(String format) {
        this(format, MyDate.getNow(), DEFAULT_ACTIVE_FIELD, false);
    }

    /** edit current date in default format */
    public DateField5() {
//        this(Settings.getInstance().getDefaultDateFormat(), MyDate.getNow(), DEFAULT_ACTIVE_FIELD, false);
        this(Settings.getInstance().getDefaultDateFormat());
    }

    /** edit duration initValue in format
     * @param duration
     * @param format
     */
    public DateField5(int duration, String format) {
        this(format, DEFAULT_ACTIVE_FIELD, false);
        editFieldType = DURATION;
        setDuration(duration);
    }

    public DateField5(int duration) {
        this("K:N", DEFAULT_ACTIVE_FIELD, false); //'K': hh, 'k': h
        editFieldType = DURATION;
        setDuration(duration);
    }

    /** initialize with a integer value
     * @param initValue
     * @param minValue
     * @param maxValue
     */
    public DateField5(int initValue, int minValue, int maxValue) { //, int fieldLength) {
        this("I", 0, false);
        editFieldType = INTEGER;
        FormatField formatField = getFormatField(getFieldIndex(INTEGER));
        formatField.fieldLength = Math.max(Integer.toString(minValue).length(), Integer.toString(maxValue).length()); //setting fieldLength ensures that field is automatically exited when entered the max amount of characters
        formatField.min = minValue;
        formatField.max = maxValue;
        formatField.setValue(initValue);
    }

//    public DateField5(boolean bool) {
//        this("B", 0, false);
//        //TODO!!! edit boolean values, in a nice graphical way?!
//    }
    //TODO!!!: implementation won't work:
//    public DateField5(boolean bool, String falseLabel, String trueLabel) {
//        this("B", 0, false);
//        //TODO!!! edit boolean values, in a nice graphical way?!
//        ComboBox boolBox = new ComboBox(new String[]{falseLabel, trueLabel});
//        fieldList.addElement(boolBox);
//    }
    /** set orientation of edit field, Component.CENTER, Component.LEFT, Component.RIGHT
     * @param orientation
     */
    public void setOrientation(int orientation) {
        if (orientation == Component.CENTER || orientation == Component.LEFT || orientation == Component.RIGHT) {
//            layout = new FlowLayout(orientation);
//            setLayout(layout);
            setLayout(new FlowLayout(orientation));
        }
    }

    /** returns the index in fieldList of a given type, e.g. getFieldIndex(MONTH).
     * Returns -1 if type is not defined with this formatStr */
    private int getFieldIndex(int type) {
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            if (getFormatField(i).type == type) {
                return i;
            }
        }
        return -1;
    }

    /** returns the editable formatField with index i. Returns null if none defined.
     * value of a given type, e.g. getTypeValue(MONTH). Returns -1 if type is not defined with this formatStr */
    private FormatField getFormatField(int i) {
        if (i >= 0 && i < fieldList.size()) {
            return ((FormatField) fieldList.elementAt(i));
        } else {
            return null;
        }
    }

    /** returns the editable field with index i */
    private FormatField getActiveFormatField() {
        return getFormatField(activeField);
    }

    /** returns the value of a given type, e.g. getTypeValue(MONTH). Returns 0 if type is not defined with this formatStr */
    private int getDateFieldValue(int type) {
//        int value = ((FormatField) fieldList.elementAt(fieldIndex[type])).getValue();
        int idx = getFieldIndex(type);
        if (idx != -1) {
//            int value = ((FormatField) fieldList.elementAt(idx)).getValue();
            return ((FormatField) fieldList.elementAt(idx)).getValue();
        } else {
            return 0;
        }
//        return value >= 0 ? value : -1;
//        return value >= 0 ? value : 0; //'0' is required to ensure that when editing a duration, that year/month/day are not set to negative values
    }

    /** sets the value of a given type, e.g. getTypeValue(MONTH). Does nothing if type is not defined with this formatStr */
    private void setDateFieldValue(int type, int value) {
//        ((FormatField) fieldList.elementAt(fieldIndex[type])).setValue(value);
        int fidx = getFieldIndex(type);
        if (fidx != -1) {
            ((FormatField) fieldList.elementAt(fidx)).setValue(value);
        } else {
            //do nothing
            Log.p("WARNING in DateField5: setDateFieldValue(type=" + type + ", value=" + value + ")");
        }
    }

    /**
     * set all defined time labels to the indicated time
     * @param time
     */
    public void setDate(MyDate date) {
//        MyDate time = new MyDate(time);
//        this.time = time;
//        long time = time.getTime();

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
                case DAY:
                    field.setValue(date.getDay());
                    break;
                case MONTH:
                    field.setValue(date.getMonth());
                    break;
                case YEAR:
                    field.setValue(date.getYear());
                    break;
                case WEEKDAY:
                    field.setValue(date.getWeekday());
                    break;
                case HOUR24:
                    field.setValue(date.getHour24());
                    break;
                case HOUR12:
                    field.setValue(date.getHour12());
                    break;
                case HOUR_DURATION:
                    field.setValue(date.getHour24());
                    break;
                case MINUTE:
                    field.setValue(date.getMinute());
                    break;
                case SECOND:
                    field.setValue(date.getSeconds());
                    break;
                case AMPM:
                    field.setValue(date.getAmPm());
                    break;

                default:
                //do nothing
            }
        }
    }

    public MyDate getDate() {
//        MyDate time = new MyDate(time);
        MyDate updatedDate = new MyDate(time);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
//            FormatField oldDate = (FormatField) fieldList.elementAt(i);
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
                case DAY:
                    updatedDate.setDay(field.value);
                    break;
                case MONTH:
                    updatedDate.setMonth(field.value);
                    break;
                case YEAR:
                    updatedDate.setYear(field.value);
                    break;
//                case WEEKDAY:
//                    oldDate.setValue(newDate.getWeekday());
//                    break;
                case HOUR24:
                    updatedDate.setHour24(field.value);
                    break;
                case HOUR12:
                    updatedDate.setHour12(field.value);
                    break;
//                case HOUR_DURATION:
//                    oldDate.setValue(newDate.getHour24());
//                    break;
                case MINUTE:
                    updatedDate.setMinute(field.value);
                    break;
                case SECOND:
                    updatedDate.setSeconds(field.value);
                    break;
                case AMPM:
                    updatedDate.setAmPm(field.value);
                    break;
                default:
                //do nothing
            }
        }
        return updatedDate;
    }

    public void xupdateDate(MyDate oldDate, MyDate newDate) {
//        MyDate time = new MyDate(time);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
//            FormatField oldDate = (FormatField) fieldList.elementAt(i);
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
                case DAY:
                    oldDate.setDay(newDate.getDay());
                    break;
                case MONTH:
                    oldDate.setMonth(newDate.getMonth());
                    break;
                case YEAR:
                    oldDate.setYear(newDate.getYear());
                    break;
//                case WEEKDAY:
//                    oldDate.setValue(newDate.getWeekday());
//                    break;
                case HOUR24:
                    oldDate.setHour24(newDate.getHour24());
                    break;
                case HOUR12:
                    oldDate.setHour12(newDate.getHour12());
                    break;
//                case HOUR_DURATION:
//                    oldDate.setValue(newDate.getHour24());
//                    break;
                case MINUTE:
                    oldDate.setMinute(newDate.getMinute());
                    break;
                case SECOND:
                    oldDate.setSeconds(newDate.getSeconds());
                    break;
                case AMPM:
                    oldDate.setAmPm(newDate.getAmPm());
                    break;
                default:
                //do nothing
            }
        }
    }

    public MyDate xgetDate() {
        if (getFieldIndex(AMPM) == -1) {
            return new MyDate(getDateFieldValue(DAY), getDateFieldValue(MONTH), getDateFieldValue(YEAR), getDateFieldValue(HOUR24), getDateFieldValue(MINUTE), getDateFieldValue(SECOND));
        } else {
            return new MyDate(getDateFieldValue(DAY), getDateFieldValue(MONTH), getDateFieldValue(YEAR), getDateFieldValue(HOUR12), getDateFieldValue(MINUTE), getDateFieldValue(SECOND), getDateFieldValue(AMPM));
        }
    }

    public void setTime(long time) {
        this.time = time; //store the exact input time to ensure no 'rounding' errors or lost milleseconds during editing
        setDate(new MyDate(time));
    }

    public long getTime() {
//        return new MyDate(getTypeValue(DAY), getTypeValue(MONTH), getTypeValue(YEAR), getTypeValue(HOUR), getTypeValue(MINUTE)).getTime();
        return getDate().getTime();
    }

    /**
     * set all defined duration labels to the indicated duration. NB! Current implementation drops any milliseconds less than one second.
     * @param duration
     */
    public void setDuration(long duration) {
//        MyDate time = new MyDate(time);
//        this.duration = duration;

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
//                case HOUR:
                case HOUR_DURATION:
                    int hours = ((int)duration / MILLISECONDS_PER_HOUR);
                    field.setValue(hours);
                    break;
                case MINUTE:
                    int minutes = ((int)(duration % MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE);//(duration - hours*MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE;
                    field.setValue(minutes);
                    break;
                case SECOND:
                    int seconds = ((int)((duration % MILLISECONDS_PER_HOUR) % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND);
                    field.setValue(seconds);
                    break;
                default:
                    Log.p("Error in SetDuration, field.type=" + field.type + " duration=" + duration);
                //do nothing
            }
        }
        refresh();
//        Log.l("setDuration milliseconds (dropped during edit)=" + duration % MILLISECONDS_PER_SECOND);
    }

    /** updates oldDuration with the hour, minute, second values of newDuration*/
    public int getDuration() {
//        int updatedDuration = duration;
        int updatedDuration = 0;
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
//                case HOUR:
                case HOUR_DURATION:
//                    updatedDuration = field.value * MILLISECONDS_PER_HOUR + (updatedDuration % MILLISECONDS_PER_HOUR);
                    updatedDuration += field.value * MILLISECONDS_PER_HOUR;
                    break;
                case MINUTE:
//                    int newDurationMinutes = (newDuration %MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE;
//                    updatedDuration = (updatedDuration/MILLISECONDS_PER_HOUR)*MILLISECONDS_PER_HOUR + newDurationMinutes*MILLISECONDS_PER_MINUTE + (updatedDuration % MILLISECONDS_PER_MINUTE);
//                    updatedDuration = ((updatedDuration / MILLISECONDS_PER_HOUR) * MILLISECONDS_PER_HOUR) + (field.value * MILLISECONDS_PER_MINUTE) + (updatedDuration % MILLISECONDS_PER_MINUTE);
                    updatedDuration += field.value * MILLISECONDS_PER_MINUTE;
                    break;
                case SECOND:
//                    int newDurationSeconds = (newDuration % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND;
//                    updatedDuration = (updatedDuration/MILLISECONDS_PER_MINUTE)*MILLISECONDS_PER_MINUTE + newDurationSeconds*MILLISECONDS_PER_SECOND + (updatedDuration % MILLISECONDS_PER_SECOND);
//                    updatedDuration = ((updatedDuration / MILLISECONDS_PER_MINUTE) * MILLISECONDS_PER_MINUTE) + (field.value * MILLISECONDS_PER_SECOND) + (updatedDuration % MILLISECONDS_PER_SECOND);
                    updatedDuration += field.value * MILLISECONDS_PER_SECOND;
                    break;
                default:
                    Log.p("Error in getDuration, field.type=" + field.type);
                //do nothing
            }
        }
        return updatedDuration;
    }

    /** updates oldDuration with the hour, minute, second values of newDuration*/
    public int xupdateDuration(int oldDuration, int newDuration) {
//        MyDate time = new MyDate(time);
        int updatedDuration = oldDuration;
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            FormatField field = (FormatField) fieldList.elementAt(i);
            switch (field.type) {
//                case HOUR:
                case HOUR_DURATION:
                    updatedDuration = (newDuration / MILLISECONDS_PER_HOUR) * MILLISECONDS_PER_HOUR + (updatedDuration % MILLISECONDS_PER_HOUR);
                    break;
                case MINUTE:
//                    int newDurationMinutes = (newDuration %MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE;
//                    updatedDuration = (updatedDuration/MILLISECONDS_PER_HOUR)*MILLISECONDS_PER_HOUR + newDurationMinutes*MILLISECONDS_PER_MINUTE + (updatedDuration % MILLISECONDS_PER_MINUTE);
                    updatedDuration = (updatedDuration / MILLISECONDS_PER_HOUR) * MILLISECONDS_PER_HOUR + ((newDuration % MILLISECONDS_PER_HOUR) / MILLISECONDS_PER_MINUTE) * MILLISECONDS_PER_MINUTE + (updatedDuration % MILLISECONDS_PER_MINUTE);
                    break;
                case SECOND:
//                    int newDurationSeconds = (newDuration % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND;
//                    updatedDuration = (updatedDuration/MILLISECONDS_PER_MINUTE)*MILLISECONDS_PER_MINUTE + newDurationSeconds*MILLISECONDS_PER_SECOND + (updatedDuration % MILLISECONDS_PER_SECOND);
                    updatedDuration = (updatedDuration / MILLISECONDS_PER_MINUTE) * MILLISECONDS_PER_MINUTE + ((newDuration % MILLISECONDS_PER_MINUTE) / MILLISECONDS_PER_SECOND) * MILLISECONDS_PER_SECOND + (updatedDuration % MILLISECONDS_PER_SECOND);
                    break;
                default:
                    Log.p("Error in SetDuration, field.type=" + field.type + " duration=" + newDuration);
                //do nothing
            }
        }
        return updatedDuration;
    }

    /**
     * returns the value of the edited duration. NB! Current implementation drops any milliseconds less than one second.
     * @return
     */
//    public int getDuration() {
//        return getDateFieldValue(HOUR_DURATION) * MILLISECONDS_PER_HOUR + getDateFieldValue(MINUTE) * MILLISECONDS_PER_MINUTE + getDateFieldValue(SECOND) * MILLISECONDS_PER_SECOND;
//    }
    /**
     * set defined integer labels to the indicated integer.
     * @param intValue
     */
    public void setInt(int intValue) {
//        MyDate time = new MyDate(time);
        FormatField field = (FormatField) fieldList.elementAt(getFieldIndex(INTEGER));
        field.setValue(intValue);
    }

    /**
     * returns the value of the edited duration. NB! Current implementation drops any milliseconds less than one second.
     * @return
     */
    public int getInt() {
        return getDateFieldValue(INTEGER);
    }

    public void setBool(boolean boolValue) {
//        MyDate time = new MyDate(time);
        FormatField field = (FormatField) fieldList.elementAt(getFieldIndex(BOOLEAN));
        field.setValue(boolValue ? 1 : 0);
    }

    /**
     * returns the value of the edited duration. NB! Current implementation drops any milliseconds less than one second.
     * @return
     */
    public boolean getBool() {
        return getDateFieldValue(BOOLEAN) == 0 ? false : true;
    }

    class FormatField {

        private int value, min, max, fieldLength;
        int type; //DAY, MONTH etc.
        MyLabel label;
        char formatChar;
        boolean editable;

        /**
         *
         * @param type of field, e.g. DateField5.DAY
         * @param min minimum numerical value of field
         * @param max maximum value of field
         * @param fieldLength length of field in chars
         * @param label the corresponding label
         * @param formatChar the read formatChar
         * @param editable is field editable or not?
         */
        FormatField(int type, int min, int max, int fieldLength, MyLabel label, char formatChar, boolean editable) {
            this.type = type;
            this.min = min;
            this.max = max;
            this.fieldLength = fieldLength;
            this.label = label;
            this.formatChar = formatChar;
            this.editable = editable;

            value = min; //0;
        }

        /** convert string str to integer and set the value and update label */
        void setValue(String str) {
            setValue(Integer.parseInt(str));
        }

        /** set value to int value (restrict to be between min and max) and update label */
//        void setValue(int value) {
//            this.value = value < min ? min : (value > max ? max : value);
//            label.setText(formatLabel(this.value), );
//        }
        void setValue(int value) { //, boolean zeroTime) {
//            if (zeroTime) {
//                label.setText(formatLabel(this.value, zeroTime));
//            } else {
//                label.setText(formatLabel(this.value, zeroTime));
//                this.value = value < min ? min : (value > max ? max : value);
//            }
            this.value = value < min ? min : (value > max ? max : value);
            label.setText(formatLabel(this.value));
        }

        int getValue() {
            return value;
        }

        /** increase value and update label */
        void incValue() {
            setValue(++value > max ? min : value);
            //TODO!!!: if Day incremented to '1' then also increment Month; if Month incremented to '1' also increment Year; if minute incremented to '0', increment Hour, if Hour incremented to '0'/'0am'(?) then increment Day
        }

        /** decrease value and update label */
        void decValue() {
            setValue(--value < min ? max : value);
        }

        boolean maxFieldLengthReached(String str) {
            return str.length() == fieldLength;
        }
        final static String legalFormatChars = "dDmMlLyYwWhHnNSkKaIB";

        /**
         * returns a String containing the value formatted as a indicated by the formatChar (e.g. formatLabel('y', 7) -> "07")
         * @param formatChar
         * @param value
         * @return
         */
        private String formatLabel(int value) { //, boolean zeroValue) {
            if (editFieldType == DATE && time == 0) {
                switch (formatChar) {
                    case 'd': //day 8 or 31
                    case 'k': // 8h
//                    return (value + "") + value;
                    case 'l': //month 1, 8, 12
//                    return value + "";
                    case 'I': //integer psotive/negative
//                    return (value < 10 ? "0" : "") + value + "";
//                    return value + ""; //TODO: format with leading zeroes
                        return "-";

                    case 'D': //day 08 or 31
                    case 'w': //weekday (Mon, Tue, ...)
//                    return MyDate.getShortDayName(value);
                    case 'H': //08h
//                    return (value < 10 ? "0" : "") + value;
                    case 'y': //09
//                    return (value < 100 ? "0" : "") + value;
                    case 'L': //month 01, 08, 12
//                    return (value < 10 ? "0" : "") + value;
                    case 'N': //minutes 58
//                    return (value < 10 ? "0" : "") + value;
                    case 'S': //seconds 03, 56
//                    return (value < 10 ? "0" : "") + value;
                    case 'a': //am/pm
//                    return value == Calendar.AM ? "am" : "pm";
                    case 'n': //minutes 58 am/pm
//                    return (value < 10 ? "0" : "") + value;
                    case 'K': //08h
//                    return (value < 10 ? "0" : "") + value;
                        return "--";

                    case 'm': //month Jan, Aug
//                    return MyDate.getShortMonthName(value);
                        return "   ";
                    case 'M': //month January, August
//                    return MyDate.getMonthName(value);
                    case 'W': //weekday (Monday, Tuesday, ...)
//                    return MyDate.getDayName(value);
                    case 'B': //boolean
//                    return value == 0 ? "False" : "True";
                        return "      ";

                    case 'Y': //2009
//                    return value + "";
                        return "----";
                    case 'h': //am/pm
//                    return (value < 10 ? "0" : "") + value;// + "ap/pm";

                    default:
                        return "";
                }
            } else {
                switch (formatChar) {
                    case 'd': //day 8 or 31
                        return value + "";
                    case 'D': //day 08 or 31
                        return (value < 10 ? "0" : "") + value;
                    case 'm': //month Jan, Aug
                        return MyDate.getShortMonthName(value);
                    case 'M': //month January, August
                        return MyDate.getMonthName(value);
                    case 'l': //month 1, 8, 12
                        return value + "";
                    case 'L': //month 01, 08, 12
                        return (value < 10 ? "0" : "") + value;
                    case 'y': //09
                        return (value < 100 ? "0" : "") + value;
                    case 'Y': //2009
                        return value + "";
                    case 'w': //weekday (Mon, Tue, ...)
                        return MyDate.getShortDayName(value);
                    case 'W': //weekday (Monday, Tuesday, ...)
                        return MyDate.getDayName(value);
                    case 'h': //am/pm
//                    return (value < 10 ? "0" : "") + value;// + "ap/pm";
                    case 'H': //08h
                        return (value < 10 ? "0" : "") + value;
                    case 'n': //minutes 58 am/pm
//                    return (value < 10 ? "0" : "") + value;
                    case 'N': //minutes 58
                        return (value < 10 ? "0" : "") + value;
                    case 'S': //seconds 03, 56
                        return (value < 10 ? "0" : "") + value;
                    case 'a': //am/pm
                        return value == Calendar.AM ? "am" : "pm";
                    case 'k': // 8h
                        return (value + "") + value;
                    case 'K': //08h
                        return (value < 10 ? "0" : "") + value;
                    case 'I': //integer psotive/negative
//                    return (value < 10 ? "0" : "") + value + "";
                        return value + ""; //TODO: format with leading zeroes
                    case 'B': //boolean
                        return value == 0 ? "False" : "True";

                    default:
                        return "";
                }
            }
        }
    }

    /**
     * return the appropriate FormatField for this format character.
     * @param
     * @return
     */
    private FormatField getFormatField(char formatChar) {
        switch (formatChar) {
            case 'd':
            case 'D':
                return new FormatField(DAY, 1, 31, 2, new MyLabel(""), formatChar, true);
            case 'm':
            case 'M':
            case 'l':
            case 'L':
                return new FormatField(MONTH, 1, 12, 2, new MyLabel(""), formatChar, true);
            case 'y':
            case 'Y':
                return new FormatField(YEAR, 0, 9999, formatChar == 'y' ? 2 : 4, new MyLabel(""), formatChar, true);
            case 'w':
            case 'W':
                return new FormatField(WEEKDAY, 1, 7, 0, new MyLabel(""), formatChar, false); //TODO: what fieldLength to set for fields that are calculated and cannot be edited??
            case 'h':
                return new FormatField(HOUR12, 0, 12, 2, new MyLabel(""), formatChar, true);
            case 'H':
//                return new FormatField(HOUR24, 0, formatChar == 'h' ? 12 : 23, 2, new MyLabel(""), formatChar, true);
                return new FormatField(HOUR24, 0, 23, 2, new MyLabel(""), formatChar, true);
            case 'N':
            case 'n':
                return new FormatField(MINUTE, 0, 59, 2, new MyLabel(""), formatChar, true);
            case 'S':
                return new FormatField(SECOND, 0, 59, 2, new MyLabel(""), formatChar, true);
            case 'a':
                //Calendar.AM==0, Calendar.PM==1, see http://java.sun.com/javame/reference/apis/jsr118/constant-values.html#java.util.Calendar.AM
                return new FormatField(AMPM, Calendar.AM, Calendar.PM, 1, new MyLabel(""), formatChar, true);
            case 'k': //duration in hours, e.g. 2, 8, 12, 39, 99h
                return new FormatField(HOUR_DURATION, 0, 99, 1, new MyLabel(""), formatChar, true);
            case 'K': //duration in hours, e.g. 02, 08, 12, 39, 99h
//                return new FormatField(HOUR_DURATION, 0, 99, 2, new MyLabel(""), formatChar, true);
                return new FormatField(HOUR_DURATION, 0, 99, 2, new MyLabel(""), formatChar, true);
            case 'I': //duration in hours, e.g. 02, 08, 12, 39, 99h
//                return new FormatField(HOUR_DURATION, 0, 99, 2, new MyLabel(""), formatChar, true);
                return new FormatField(INTEGER, Integer.MIN_VALUE, Integer.MAX_VALUE, 11, new MyLabel(""), formatChar, true); //11 because INT_MIN == -2147483648, INT_MAX==2,147,483,647
            case 'B':
                return new FormatField(BOOLEAN, 0, 1, 1, new MyLabel(""), formatChar, true); //TODO: what fieldLength to set for fields that are calculated and cannot be edited??
            default:
                try {
                    throw new Exception("Unknown formatchar in DateField5");
                    //                return new FormatField(UNKNOWN, 0, 0, 0, new MyLabel(""), formatChar, true);
                    //                        throw new Exception("Illegal time format character");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                return null;
//                return new FormatField(UNKNOWN, 0, 0, 0, new MyLabel(""), formatChar, true);
//                        throw new Exception("Illegal time format character");
        }
    }

    /**
     * create the labels from the formatStr
     * @param formatStr
     */
    private void initFormat(String formatStr) {
//            String formatStr = "w D/M Y, H:n";
        for (int i = 0, size = formatStr.length(); i < size; i++) {
            char formatChar = formatStr.charAt(i);
            //for format characters, add the appropriate field
            if (FormatField.legalFormatChars.indexOf(formatStr.charAt(i)) != -1) {
                FormatField f = getFormatField(formatChar);
                fieldList.addElement(f);
                addComponent(f.label);
//                fieldIndex[f.type] = fieldList.size() - 1; //store record index for the field type for quick access
            } else {
                //for other characters, create a string and add that as a (fixed) label
                int ix = i; //ix points to the *last* that is NOT a format char
                while (ix + 1 < size /*formatStr.length()*/ && FormatField.legalFormatChars.indexOf(formatStr.charAt(ix + 1)) == -1) {
//                    fixedLabelText += formatStr.charAt(ix);
                    ix++;
                }
                String fixedLabelText = formatStr.substring(i, ix + 1); //+1 since endindex is exclusive //new String(formatStr.substring(i, i));
                i = ix;
                Label fixedTextLabel = new MyLabel(fixedLabelText);
                fixedTextLabel.setFocusable(false); // to avoid that the label can get focus
                addComponent(fixedTextLabel);
            }
        }
    }

    private void setSubFieldsFocusedState(boolean state) {
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            MyLabel label = ((FormatField) fieldList.elementAt(i)).label;
            label.setFocusable(state);
            label.setHandlesInput(state);
        }
    }

    private void setAllFieldsVisible(boolean visible) {
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            ((FormatField) fieldList.elementAt(i)).label.setVisible(visible);
        }
    }

    /** requests focus for activeField */
    private void focusActiveField() {
        getActiveFormatField().label.requestFocus(); //focus next label
    }

    /** move to next field in sequence and request focus for it. 
     * Return true if there was a next field, false if there is next editable field.
     * TODO: will loop indefinitely if all fields are only uneditable */
    private boolean nextField() {
        do {
            activeField++;
            if (activeField >= fieldList.size()) {
                activeField--;  //adjust back again to keep in legal range
                return false;
            }
        } while (!getActiveFormatField().editable); // skip fields that are not editable, weekday notably
        refresh();
        str = "";
        focusActiveField();
        return true;
    }

    /** move to previous next field in sequence and request focus for it.
     * Return true if there was a previous field, false if there is no previous editable field.
     * TODO: will loop indefinitely if all fields are only uneditable */
    private boolean prevField() {
        do {
            activeField--;
            if (activeField < 0) {
                activeField++; //adjust back again to keep in legal range
                return false;
            }
        } while (!getActiveFormatField().editable); // skip fields that are not editable, weekday notably
        refresh();
        str = "";
        focusActiveField();
        return true;
    }

    private void enterDateField(String initString) {
        setSubFieldsFocusedState(true);
        setHandlesInput(true);
        //if time is 0, then use current time
        if (editFieldType == DATE && time == 0) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new Date(MyDate.getNow()));
//            setTime(cal.getTime().getTime());
            setTime(MyDate.getNow());
            setAllFieldsVisible(true);
        }
        activeField = initActiveField;
        // if first active field is not edtiable, then find the first that is (e.g. if formatStr="W ...")
        while (!getActiveFormatField().editable && activeField < fieldList.size()) {
            activeField++; //if
        }
        if (initString.length() != 0) {
//            str = initString;
//            getActiveFormatField().setValue(str);
            getActiveFormatField().setValue(initString);
            refresh();
        }
        focusActiveField();
    }

    private void exitDateField() { //boolean exitOnFire, boolean previous) {
        forceDateLegal();
        refresh();

        setSubFieldsFocusedState(false);
        setHandlesInput(false);
        this.requestFocus(); //ensures that this container is in focus when the up/dn/right/left key press is processed
//        if (exitOnFire) {
//            setFocusToNextField();
//
//        } else {
//            this.requestFocus(); //ensures that this container is in focus when the up/dn/right/left key press is processed
//        }
    }

    /**
     * used when exiting on Fire to ensure that focus is moved to next field.
     * NOT EASY to implement. Maybe by assigning an action to FIRE for the sourrounding container??
     */
//    private void setFocusToNextField() {
//        Form parentForm = getComponentForm(); // get parent form
//
////            int myPos = parentForm.getFocusPosition(this);
//        Component nextComponentDown = parentForm.getNextFocusDown();
//        parentForm.setFocused(nextComponentDown);
//    }
    /** check if the currently set values are a legal date. Only thing that can be illegal in a date is that there are too many days in the given month, eg 30 Feb, or 31 April */
    private boolean isLegal() {
        int maxDays = MyDate.getDaysInMonth(getDateFieldValue(MONTH), getDateFieldValue(YEAR));
        return (getDateFieldValue(DAY) <= maxDays);
    }

    /** force the date to be legal. Should only be done when exiting the editing, since you may set e.g. the day
     * in month before setting the appropriate month - if you then correct the day before setting the month the
     * use would experience a forced change of the day he just set */
    private void forceDateLegal() {
        if (!isLegal()) {
            int maxDays = MyDate.getDaysInMonth(getDateFieldValue(MONTH), getDateFieldValue(YEAR));
            setDateFieldValue(DAY, maxDays);
        }
    }

    /**
     * update labels that are  not automatically updated when their values are changes (currently only weekday)
     */
    private void updateLabels() {
        if (editFieldType == DATE) {
            setDateFieldValue(WEEKDAY, getDate().getWeekday());
        }
    }

    /**
     * updates the labels each time a value has changed.
     */
    private void refresh() {
        if (editFieldType == DATE) {
            if (getActiveFormatField().type == YEAR) {
//            adjustYear();
                /** adjust years in 0..99 to be 2000..2099 */
                if (getActiveFormatField().type == YEAR && str.length() <= 2 && getDateFieldValue(YEAR) < 100) {
                    setDateFieldValue(YEAR, getDateFieldValue(YEAR) + 2000);
                }
            }
            //check if legal date and then update
            if (isLegal()) {
                updateLabels();
            }
        }
        this.repaint();
    }

//    /** adjust years in 0..99 to be 2000..2099 */
//    private void adjustYear() {
//    /** adjust years in 0..99 to be 2000..2099 */
//        if (getActiveFormatField().type == YEAR && str.length() <= 2 && getTypeValue(YEAR) < 100) {
//            setTypeValue(YEAR, getTypeValue(YEAR) + 2000);
//        }
//    }
    /**
     * handles key inputs in labels
     * @param keyCode
     */
    private void handleKey(int keyCode) {
        int k = Display.getInstance().getGameAction(keyCode);
        switch (k) {
            //increase value of current field by one, e.g. 23 -> 24
            case Display.GAME_UP: //TODO!!!: change so that if DAY passes value of current month, month is increased! and vice-versa fr GAME_DOWN
                getActiveFormatField().incValue();
                refresh();
                break;

            case Display.GAME_DOWN:
                getActiveFormatField().decValue();
                refresh();
                break;

            //stop editing the date and exit
            case Display.GAME_FIRE:
                exitDateField(); //true, false);
                break;

            //move to previous field in sequence (skipping text labels), exit if on last field
            case Display.GAME_RIGHT:
                if (!nextField()) {
                    exitDateField(); //false, false);
                }

                break;

            case Display.GAME_LEFT:
                if (!prevField()) {
                    exitDateField(); //false, true);
                }

                break;
//                    case Display.**delete_key: // doesn't seem to be possible to get this via Display.??!!
//                        time.setTime(0);
//                        break;
            default:
                switch (keyCode) {
                    case '0': //..'9': //'0' - '9':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        str += (char) keyCode;
                        if (!str.equals("0")) { // avoid refreshing the field if an initial '0' is entered, e.g. when entering july as '07'
                            getActiveFormatField().setValue(str);
//                            refresh(); //- not here to avoid updating e.g. year from '08' to '2008' while typing
//                            setValue();
                        } else {
                        }
                        if (getActiveFormatField().maxFieldLengthReached(str)) {
                            if (!nextField()) {
                                exitDateField(); //move to next field, if at last field, then exit
                            }
                        }
                        break;
                    case '-':
                        if (editFieldType == INTEGER) {
                            setInt(0 - getInt()); //flip minus sign
                        }
                        break;
                    case '#': //TODO!!!: detect long-press and deduct 1 week?
                        int fieldType = getActiveFormatField().type;
                        if (editFieldType == DATE) {
                            if (fieldType == DAY || fieldType == MONTH || fieldType == YEAR) {
                                //add one week to current time
                                MyDate newDate = getDate();
                                newDate.addDays(7);
                                setDate(newDate);
                            } else if (fieldType == HOUR24 || fieldType == MINUTE) {
                                //add 15 minutes to current time
                                MyDate newDate = getDate();
                                newDate.addMinutes(15);
                                setDate(newDate);
                            }
                        } else if (editFieldType == DURATION && (fieldType == HOUR_DURATION || fieldType == HOUR_DURATION || fieldType == MINUTE)) {
                            setDuration(getDuration() + 15 * MILLISECONDS_PER_MINUTE);
                        } else if (editFieldType == INTEGER) {
                            setInt(getInt() + 100);
                        }
//                        int fieldType = getActiveFormatField().type;
//                        if (editFieldType == DATE) {
//                            if (fieldType == DAY || fieldType == MONTH || fieldType == YEAR) {
//                                //add one day to current time
//                                MyDate newDate2 = getDate();
//                                newDate2.addDays(1);
//                                setDate(newDate2);// add a convenient interval to the current time, e.g. one week since this will typically require a cumbersome change of both Day and Month + some calculation to get it right
//                            } else if (fieldType == HOUR24 || fieldType == HOUR_DURATION || fieldType == MINUTE) {
//                                //add 5 minutes to current time
//                                MyDate newDate = getDate();
//                                newDate.addMinutes(5);
//                                setDate(newDate);
//                            }
//                        } else if (editFieldType == DURATION && (fieldType == HOUR_DURATION || fieldType == MINUTE)) {
//                            setDuration(getDuration() + 5 * MILLISECONDS_PER_MINUTE);
//                        } else if (editFieldType == INTEGER) {
//                            setInt(getInt() + 10);
//                        }
                        break;
                    case '*': //TODO!!! lauch calendar picker
                        com.codename1.ui.Calendar cal = new com.codename1.ui.Calendar(time);
                        Command cmd = new Command("OK");

                        if (Dialog.show("Select date", cal, new Command[]{cmd}) == cmd) {
                            setTime(cal.getDate().getTime());
                        }
                        //open Calendar in Dialog window, support commands OK and Cancel (or do this vai a defined button??!!)
//                        Dialog calendarDialog = new Dialog();
//                        Calendar calendar = new Calendar();
//                        calendar.setDate(getDate());
//                        calendarDialog.setAutoAdjustDialogSize(true);
//                        calendarDialog.set
//                        calendarDialog.shshow("Select Date", calendar, cmds, k, null);
//                        setDate(calendar.getDate());
                        break;
                }
        }
        refresh();
    }

    /**
    //    public void xkeyPressed(int keyCode) {
    //        // scrolling events are in keyPressed to provide immediate feedback
    //        if (!handlesInput()) {
    //            return;
    //        }
    //        int k = Display.getInstance().getGameAction(keyCode);
    //        if ((k == Display.GAME_FIRE)) {
    //            enterDateField("");
    //        } else if (keyCode >= '0' && keyCode <= '9') {
    //            enterDateField("" + (char) keyCode);
    //        }
    //        super.keyPressed(keyCode);
    //    }
     */
    public void keyPressed(int keyCode) {
        int k = Display.getInstance().getGameAction(keyCode);
        if (!isEditable()) return;

        if ((!handlesInput() && k == Display.GAME_FIRE)) { //TODO: allow to enter by pressing right key when positioned on the field?
            enterDateField("");
        } else if (keyCode >= '0' && keyCode <= '9') {
            enterDateField("" + ((char) keyCode));
        } else if (keyCode == '*' || keyCode == '#') {
            enterDateField("");
            handleKey(keyCode);
        }
        super.keyPressed(keyCode);
    }

//    public void keyReleased(int keyCode) {
//        /*int k = Display.getInstance().getGameAction(keyCode);
//        if (!handlesInput() && k == Display.GAME_FIRE) {
//        enterDateField();
//        } else {
//        }*/
//        super.keyReleased(keyCode);
//    }

//    protected void longKeyPress(int keyCode) {
//        if (keyCode == '*' && editFieldType == DATE) {
//            setTime(0);
//        }
//    }

    /** implements ActionListener: */
    public void actionPerformed(ActionEvent evt) {
    }

    /**
     * Add an action listener which is invoked when the text area was modified not during
     * modification. A text <b>field</b> might never fire an action event if it is edited
     * in place and the user never leaves the text field!
     *
     * @param a actionListener
     */
    public void addActionListener(ActionListener a) {
        if (actionListeners == null) {
            actionListeners = new Vector();
        }
        if (!actionListeners.contains(a)) {
            actionListeners.addElement(a);
        }
    }

    /**
     * Removes an action listener
     *
     * @param a actionListener
     */
    public void removeActionListener(ActionListener a) {
        if (actionListeners == null) {
            actionListeners = new Vector();
        }
        actionListeners.removeElement(a);
    }

    /**
     * Notifies listeners of a change to the text area
     */
    void fireActionEvent() {
        if (actionListeners != null) {
            ActionEvent evt = new ActionEvent(this);
            for (int iter = 0; iter < actionListeners.size(); iter++) {
                ActionListener a = (ActionListener) actionListeners.elementAt(iter);
                a.actionPerformed(evt);
            }
        }
    }

    boolean isEditable() {
        return editable;
    }
    void setEditable(boolean editable) {
        this.editable=editable;
    }
}
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void xkeyReleased(int keyCode) {
//        /*int k = Display.getInstance().getGameAction(keyCode);
//        if (!handlesInput() && k == Display.GAME_FIRE) {
//        enterDateField();
//        } else {
//        }*/
//        super.keyReleased(keyCode);
//    }
//                    switch (formatChar) {
//                        case 'd': //day 8 or 31
//                        case 'D': //day 08 or 31
//                            fieldList.addElement(new FormatField(0, 31, 2, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            dayIdx = fieldList.size();
//                            break;
//                        case 'm': //month Jan, Aug
//                        case 'M': //month January, August
//                        case 'l': //month 1, 8, 12
//                        case 'L': //month 01, 08, 12
//                            fieldList.addElement(new FormatField(1, 12, 2, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            monthIdx = fieldList.size();
//                            break;
//                        case 'y': //09
//                        case 'Y': //2009
//                            fieldList.addElement(new FormatField(0, 9999, formatChar == 'y' ? 2 : 4, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            hourIdx = fieldList.size();
//                            break;
//                        case 'w': //weekday (Mon, Tue, ...)
//                        case 'W': //weekday (Monday, Tuesday, ...)
//                            fieldList.addElement(new FormatField(1, 7, 0, new MyLabel(""), formatChar)); //TODO: what fieldLength to set for fields that cannot be edited??
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            weekdayIdx = fieldList.size();
//                            break;
//                        case 'h': //am/pm
//                        case 'H': //08h
//                            fieldList.addElement(new FormatField(0, formatChar == 'h' ? 12 : 23, 2, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            hourIdx = fieldList.size();
//                            break;
//                        case 'K': //duration in hours, e.g. 08, 12, 39, 99h
//                            fieldList.addElement(new FormatField(0, 99, 2, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            hourIdx = fieldList.size();
//                            break;
//                        case 'N': //minutes 58
//                        case 'n': //minutes 58am/pm
//                            fieldList.addElement(new FormatField(0, 59, 2, new MyLabel(""), formatChar));
//                            addComponent(((FormatField) (fieldList.lastElement())).label);
//                            minuteIdx = fieldList.size();
//                            break;
//                        default:
//                            String fixedLabelText = new String(formatStr.substring(i, i));
//                            int ix = i;
//                            while (ix < formatStr.length() && legalFormatChars.indexOf(legalFormatChars, formatStr.charAt(ix)) != -1) {
//                                fixedLabelText = fixedLabelText + formatStr.charAt(ix);
//                            }
//                            i = ix;
//                            addComponent(new MyLabel(fixedLabelText));
//                    }
//                }
//            }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    /** move to next field in sequence and request focus for it. TODO: will loop indefinitely if all fields are only uneditable */
//    private void nextField() {
//        do {
//            activeField++;
//            if (activeField >= fieldList.size()) {
//                activeField = 0; //UI: moving to next field when positioned on last field wraps around to first field (TODO: or should it exit the date editor??)
//            }
//        } while (!getActiveFormatField().editable); // skip fields that are not editable, weekday notably
//        refresh();
//        str = "";
//        focusActiveField();
//    }
//
//    /** move to previous next field in sequence and request focus for it. TODO: will loop indefinitely if all fields are only uneditable */
//    private void prevField() {
//        do {
//            activeField--;
//            if (activeField < 0) {
//                activeField = fieldList.size() - 1; //UI: moving to prev field when positioned on first field wraps around to last field (TODO: or should it exit the date editor??)
//            }
//        } while (!getActiveFormatField().editable); // skip fields that are not editable, weekday notably
//        refresh();
//        str = "";
//        focusActiveField();
//    }
//// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    private void xsetFocusToNextField(boolean previous) {
//        if (this.getNextFocusRight() != null) {
//            this.getNextFocusRight().requestFocus();
//            return;
//        } else if (this.getNextFocusDown() != null) {
//            this.getNextFocusDown().requestFocus();
//            return;
//        } else {
//            Container contentContainer = this.getParent();
//            int componentCount = contentContainer.getComponentCount();
//            int myIndex = contentContainer.getComponentIndex(this);
//            int nextIndex = (myIndex + 1) % componentCount;
//            /*if (nextIndex > componentCount) {
//            nextIndex = 0;
//            }*/
//            contentContainer.getComponentAt(nextIndex).requestFocus();
//        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    private void xxxsetFocusToNextField(boolean previous) {
//        if (previous) {
//            if (this.getNextFocusLeft() != null) {
//                this.getNextFocusLeft().requestFocus();
//                return;
//            } else if (this.getNextFocusUp() != null) {
//                this.getNextFocusUp().requestFocus();
//                return;
//            }
//        } else {
//            if (this.getNextFocusRight() != null) {
//                this.getNextFocusRight().requestFocus();
//                return;
//            } else if (this.getNextFocusDown() != null) {
//                this.getNextFocusDown().requestFocus();
//                return;
//            }
//        }
//        Container contentContainer = this.getParent();
//        int componentCount = contentContainer.getComponentCount();
//        int myIndex = contentContainer.getComponentIndex(this);
//        int nextIndex = 0;
//        if (previous) {
//            nextIndex = myIndex - 1;
//            if (nextIndex < 0) {
//                nextIndex = componentCount;
//            }
//        } else {
//            nextIndex = myIndex + 1;
//            if (nextIndex > componentCount) {
//                nextIndex = 0;
//            }
//        }
//        contentContainer.getComponentAt(nextIndex).requestFocus();
//    }// </editor-fold>

