/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Display;
import com.codename1.ui.spinner.Picker;
import java.util.Date;
import java.util.Map;

/**
 *
 * @author Thomas
 */
class MyDatePicker extends Picker implements SwipeClear {

    String zeroValuePattern = "";

//    MyDatePicker(Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
//        this(null, parseIdMap, get, set);
//    }
//    MyDatePicker(String zeroValuePattern, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set) {
//        this(zeroValuePattern, parseIdMap, get, set, false);
//    }
    MyDatePicker() {
        super();
        setUIID("LabelValue");
//        this.zeroValuePattern = ""; //"<set>";
        this.setType(Display.PICKER_TYPE_DATE);
        setFormatter(new SimpleDateFormat() {
            public String format(Object value) {
                Date date = ((Date) value);
                if (date.getTime() == 0 && zeroValuePattern != null) {
                    return zeroValuePattern;
                }
                return MyDate.formatDateNew(date); //
            }
        });
    }

    MyDatePicker(Date date) {
        this();
         if (date != null && date.getTime() != 0) {
            this.setDate(date);
        } else {
            this.setDate(new Date(0)); //UI: default date is undefined
        }
    }
    MyDatePicker(Date date, String zeroValuePattern, boolean setEndOfSelectedDay) {
        this(date);
        if (zeroValuePattern != null) this.zeroValuePattern = zeroValuePattern;
       
    }


//<editor-fold defaultstate="collapsed" desc="comment">
//    MyDatePicker(String zeroValuePattern, Map<Object, MyForm.UpdateField> parseIdMap, MyForm.GetDate get, MyForm.PutDate set, boolean setEndOfSelectedDay) {
//        this(get.get(), zeroValuePattern, setEndOfSelectedDay);
//        //set the time and minuts and seconds to 0
//        parseIdMap.put(this, () -> {
//            Date editedDate = this.getDate();
//            if (editedDate.getTime() != 0) {
////                Calendar cal = Calendar.getInstance();
////                cal.setTime(editedDate);
////                cal.set(Calendar.HOUR_OF_DAY, 1); //set hour to 1 to avoid pbs with daylight saving changes
////                cal.set(Calendar.MINUTE, 0);
////                cal.set(Calendar.SECOND, 0);
////                cal.set(Calendar.MILLISECOND, 1); //ensure it's after midnight //TODO!!!! is 0 the right value??
////                set.accept(cal.getTime());
//                if (setEndOfSelectedDay) {
//                    set.accept(MyDate.setDateToMidnight(editedDate));
//                } else {
//                    set.accept(MyDate.setDateToMidnight(editedDate));
//                }
//            } else {
//                set.accept(editedDate);
//            }
//        });
//    }
//</editor-fold>
//    @Override
    protected void updateValue() {
        Date date = getDate();
        if (date != null && date.getTime() == 0 && zeroValuePattern != null) {
            setText(zeroValuePattern); // return zeroValuePattern when value of date is 0 (not defined)
        } else {
            super.updateValue(); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public void pressed() {
        //set date to Now if empty when button is clicked
        if (getDate().getTime() == 0) {
//                setDate(new Date());
            getDate().setTime(new MyDate().getTime()); //use this instead of setDate to set date to avoid updating label before showing picker
        }
        super.pressed();
    }

    /**
    set date and notify listeners like if the picker had been used manually
    @param date 
     */
    public void setDateAndNotify(Date date) {
        setDate(date);
//        notifyMyActionListeners();
//        fireClicked();
        fireActionEvent(-99, -99); //-99 used in CN1 Picker to ignore built-in action listener
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button clearButtonXXX = null;
//    @Override
//    public void setDate(Date date) {
//        if (clearButtonXXX != null) {
////            clearButton.setHidden(date.getTime() == 0);
//            clearButtonXXX.setVisible(date.getTime() != 0);
//        }
//        super.setDate(date);
//        //hack to ensure that only my own listeners (and not the dialog popup) are triggered when changing the date programmatically
//        for (Object al : getListeners()) {
//            if (al instanceof MyActionListener) {
//                ((MyActionListener) al).actionPerformed(null);
//            }
//        }
//    }
//</editor-fold>
    void swipeClear() {
        setDateAndNotify(new Date(0));
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    Component makeContainerWithClearButtonXXX() {
//        clearButtonXXX = new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            setDate(new Date(0)); //will hide the button
//        }));
////        clearButton.setHidden(getDate().getTime() == 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        clearButtonXXX.setVisible(getDate().getTime() != 0); //always set since we don't know if Picker.setTime() is already called or will be called later
//        addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                setTime(0);
////                clearButton.setHidden(getTime() == 0); //NO, done in setTime()
////                clearButton.setHidden(getDate().getTime() == 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//                clearButtonXXX.setVisible(getDate().getTime() != 0); //when Picker is changed manually, setTime() is NOT called, so need to listen to chnages here to update clearButton
//            }
//        });
////        return LayeredLayout.encloseIn(this, FlowLayout.encloseRightMiddle(clearButton));
//        return FlowLayout.encloseRightMiddle(this, clearButtonXXX);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDatePicker(String title, String zeroValuePattern, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//            super();
//            this.title = title;
//            this.parseId = parseId;
//            this.zeroValuePattern = zeroValuePattern;
//            this.setType(Display.PICKER_TYPE_DATE);
////            this.setDate(parseObject.getDate(parseId));
//            Date d = parseObject.getDate(parseId);
//            if (d != null) {
//                this.setDate(d);
//            } else {
//                setDate(new Date(0)); //UI: default date is undefined
//            }
//            parseIdMap.put(parseId, () -> parseObject.put(parseId, this.getDate()));
//        }
//
//        String getTitle() {
//            return title;
//        }
//</editor-fold>
    @Override
    public void clearFieldValue() {
        swipeClear();
    }
};
