/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.spinner.DateSpinner;

/**
 * 
 * @author Thomas
 */
public class MyDateSpinner extends DateSpinner {
    
    MyDateSpinner() {
        
    }

    MyDateSpinner(long date) {
        setDate(date);
    }

    MyDateSpinner(MyDate date) {
        setDate(date);
    }

    void setDate(MyDate date, boolean useNowIfDateIsZero) {
        if (useNowIfDateIsZero && date.getTime() == 0) {
            date.setTime(MyDate.getNowDateOnly());
        }
        setCurrentDay(date.getDay());
        setCurrentMonth(date.getMonth());
        setCurrentYear(date.getYear());
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
        return new MyDate(getCurrentDay(), getCurrentMonth(), getCurrentYear()).getTime();
    }
}
