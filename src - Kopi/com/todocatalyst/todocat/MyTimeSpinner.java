/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.spinner.TimeSpinner;

/**
 * TODO: 
 * @author Thomas
 */
public class MyTimeSpinner extends TimeSpinner {
    
    MyTimeSpinner() {
        super();
    }

    MyTimeSpinner(long date) {
        this(new MyDate(date));
//        setDate(date);
    }

    MyTimeSpinner(MyDate date) {
        this();
        setDate(date);
    }

    void setDate(MyDate date, boolean useNowIfDateIsZero) {
        if (useNowIfDateIsZero && date.getTime() == 0) {
            date.setTime(MyDate.getNowTimeOnly());
        }
        setCurrentHour(date.getHour24());
        setCurrentMinute(date.getMinute());
//        setCurrentMeridiem(false); //TODO!!!! support am/pm format
    }

    void setDate(MyDate date) {
        setDate(date, true);
    }

    void setDate(long date) {
        if (date == 0) {
            date = MyDate.getNowDateOnly();
        }
        setDate(new MyDate(date));
    }

    long getTime() {
        return new MyDate(getCurrentHour(), getCurrentMinute()).getTime();
    }
}

