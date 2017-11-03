/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import com.codename1.ui.spinner.TimeSpinner;

/**
 * edits a duration in hours:minutes. 
 * TODO: support predefined intervals, e.g. {0,2,5,7,10,15,20,25,30,40,50,60,1:15, 1:30, 2:00, 2:30, 3:00}
 * @author Thomas
 */
public class MyDurationSpinner extends TimeSpinner {
    
    MyDurationSpinner(int duration) {
        super();
//        setDurationMode(true);
        setMinuteStep(1);
        setSmoothScrolling(true);
        setShowMeridiem(false);
    }

    void setDuration(int duration) {
        setCurrentHour(duration/MyDate.HOUR_IN_MILISECONDS);
        setCurrentMinute(duration%MyDate.HOUR_IN_MILISECONDS);
    }

    long getDuration() {
        return getCurrentHour()*MyDate.HOUR_IN_MILISECONDS
        +getCurrentMinute()*MyDate.HOUR_IN_MILISECONDS;
    }
}


