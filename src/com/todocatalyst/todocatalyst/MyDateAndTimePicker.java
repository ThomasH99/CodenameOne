/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import com.todocatalyst.todocatalyst.MyForm.GetVal;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Thomas
 */
public class MyDateAndTimePicker extends Picker implements SwipeClear {

//    private String DEFAULT_ZERO_VALUE_PATTERN = "0";
    private String zeroValuePattern;
    private GetVal getDefaultValue;
    private boolean inputValidated = true; //true by default, until pressed and a default value is set, then true again if the user explicitely validated the value by pressing Done?
//    private Button clearButton = null;

    MyDateAndTimePicker() {
        super();
        setUIID("ScreenItemEditableValue");
        zeroValuePattern = "";
        setType(Display.PICKER_TYPE_DATE_AND_TIME);
        setShowMeridiem(false); //TODO!!!! depend on locale!!
        setMinuteStep(MyPrefs.dateTimePickerMinuteStep.getInt());
        setFormatter(new SimpleDateFormat() {
            public String format(Object value) {
                Date date = ((Date) value);
                if ((date == null || date.getTime() == 0) && zeroValuePattern != null) {
                    return zeroValuePattern;
                }
                return MyDate.formatDateTimeNew(date); //
            }
        });
        addActionListener((e) -> {
//            Log.p("ActionListener called on MyDateAndTimePicker, actionEvent=" + e);
            inputValidated = true;
        });
    }

    MyDateAndTimePicker(Date date, String zeroValuePatternVal) {
        this();
        if (zeroValuePatternVal != null) {
            zeroValuePattern = zeroValuePatternVal;
        }
        if (date != null && date.getTime() != 0) {
            this.setDate(date);
        } else {
            setDate(new MyDate(0)); //UI: default date is undefined
        }
    }

    MyDateAndTimePicker(Date date) {
        this(date, "");
    }

    /**
     * use getDefaultValue to get a default value to use in case the time is
     * zero/undefined
     *
     * @param getDefaultValue
     */
    MyDateAndTimePicker(GetVal getDefaultValue) {
        this();
        this.getDefaultValue = getDefaultValue;
    }

//    @Override
    protected void updateValue() {
        Date date = (Date) getValue();
        if ((date == null || date.getTime() == 0) && zeroValuePattern != null) {
            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
        } else {
            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setClearButton(Button clearButton) {
//        this.clearButton = clearButton;
//    }
//    @Override
//    public void setDateXX(Date date) {
//        if (clearButton != null) {
////            clearButton.setHidden(date.getTime() == 0);
//            clearButton.setVisible(date.getTime() != 0);
//        }
//        super.setDate(date);
//        for (Object al : getListeners()) {
//            if (al instanceof MyActionListener) {
//                ((MyActionListener) al).actionPerformed(null);
//            }
//        }
//    }

//    MyDateAndTimePicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
//        this(null, parseIdMap, get, set);
//    }
//    MyDateAndTimePicker(String zeroValuePatternVal, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
////            this.setFormatter(new MySimpleDateFormat(this.getFormatter().toPattern(), zeroValuePattern)); //reuse default formatter pattern, only override for 0 value
//        this(get.get(), zeroValuePatternVal);
////            parseIdMap.put(parseId,() -> parseObject.put(parseId, this.getDate()));
//        parseIdMap.put(this,
//                () -> set.accept(this.getDate()));
//    }
//</editor-fold>
    private void setToDefaultValue() {
        if (getValue() == null || ((Date) getValue()).getTime() == 0) {
            inputValidated = false;
//                setDate(new Date());
            if (getValue() == null) {
                setDate(new MyDate(0));
            }
            if (getDefaultValue != null) { //                getDate().setTime(((Date) getDefaultValue.getVal()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
//                setDate(((Date) getDefaultValue.getVal())); //use this instead of setDate to set date to avoid updating label before showing picker
                ((Date) getValue()).setTime(((Date) getDefaultValue.getVal()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
            } else {
//                getDate().setTime(MyDate.roundUpToNextMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
                //                getDate().setTime(MyDate.roundToNearestMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
//                setDate(MyDate.roundToNearestMinute(new MyDate())); //use this instead of setDate to set date to avoid updating label before showing picker
                ((Date) getValue()).setTime(MyDate.roundToNearestMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
            }
        }
    }

    @Override
    public void pressed() {
        //set date to Now if empty when button is clicked
        if (false) {
            if (getValue() == null || ((Date) getValue()).getTime() == 0) {
                inputValidated = false;
//                setDate(new Date());
                if (getValue() == null) {
                    setDate(new MyDate(0));
                }
                if (getDefaultValue != null) { //                getDate().setTime(((Date) getDefaultValue.getVal()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
//                setDate(((Date) getDefaultValue.getVal())); //use this instead of setDate to set date to avoid updating label before showing picker
                    ((Date) getValue()).setTime(((Date) getDefaultValue.getVal()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
                } else { //                getDate().setTime(MyDate.roundUpToNextMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
                    //                getDate().setTime(MyDate.roundToNearestMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
//                setDate(MyDate.roundToNearestMinute(new MyDate())); //use this instead of setDate to set date to avoid updating label before showing picker
                    ((Date) getValue()).setTime(MyDate.roundToNearestMinute(new MyDate()).getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
                }
            }
        }
        setToDefaultValue();
        super.pressed();
    }

    /**
     * set date and notify listeners like if the picker had been used manually
     *
     * @param date
     */
    public void setDateAndNotify(Date date) {
        setDate(date);
//        notifyMyActionListeners();
//        fireClicked();
        fireActionEvent(-99, -99); //-99 used in CN1 Picker to ignore built-in action listener
    }

    public void swipeClear() {
        setDateAndNotify(new MyDate(0));
    }

    @Override
    public void clearFieldValue() {
        swipeClear();
    }

//    @Override
//    public Object getValueXXX() {
//        if (inputValidated) {
//            return super.getValue();
//        } else {
//            return null;
//        }
//    }
    public Date getDateOLD() {
        if (inputValidated) {
            return super.getDate();
        } else {
            //set date to Now if empty when button is clicked
            setToDefaultValue();
            return null;
        }
    }

    @Override
    public Date getDate() {
        if (!inputValidated) {
            setToDefaultValue();
        }
        return super.getDate();
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
