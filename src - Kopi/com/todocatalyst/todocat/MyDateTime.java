/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocat;

import java.util.Date;

/**
 * use the top-most 2nd and 3rd bit to store information about whether date and/or time has been set. This means loosing a bit of range, but not a practical problem.
 * Makes it explicit whether eg time has been set for Due date, or if only a date was set (saves having an additional boolean flag.
 * Can simply be wrapped around a normal long time.
 * @author Thomas
 */
public class MyDateTime {

    static long dateSetBit = 0xF;
    static long timeSetBit = 0xF;
    long dateAndTime;
    long date;
    long time;
    boolean isDateSet; //temporary solution, using simple boolean flags during development/testing
    boolean isTimeSet;
    MyDate tempDate = new MyDate();
    boolean isRelative;
    MyDateTime relativeReference; //relative is counted as *before* the reference, meaning we deduct
    boolean isDuration; //==isTimeSet && !isDateSet?? Time vs Duration is mainly a question of how it is displayed (or constraints, e.g. duration hours>24
    /** instead of adjusting universal date/time to local time zone (meaning eg. a date may become the day before if you're in earlier timezone), use the same time, but locally.
     * Not obvious that this is the good solution since if a task is due at a certain time in a certain zone, it is still due at this specific time, no matter where on earth you are right now. */
    boolean adjustDateAndTimeToLocalTimeZone;

    boolean isDateSet() {
        return isDateSet;
    }

    boolean isTimeSet() {
        return isTimeSet;
    }

    void clearTime() {
        isTimeSet = false;
    }

    void clearDate() {
        isDateSet = false;
    }

    /**
     * returns the date and time (as normal Java long time)
     * @return
     */
    long getDateAndTime() {
//        tempDate.setTime(date);
//        return new MyDate(tempDate.getDay(), tempDate.getMonth(), tempDate.getYear()).getTime();
        if (isDateSet) {
            if (isRelative)
                return relativeReference.getDateAndTime()+getDateAndTime();
            else
            return date;
        } else {
            return 0;
        }
    }

    /**
     * returns the date (as normal Java long time), without the set time.
     * @return
     */
    long getDate() {
//        tempDate.setTime(date);
//        return new MyDate(tempDate.getDay(), tempDate.getMonth(), tempDate.getYear()).getTime();
        if (isDateSet) {
            if (isRelative)
                return relativeReference.getDate()+getDate();
            else
            return date;
        } else {
            return 0;
        }
    }

    long getTime() {
//        tempDate.setTime(time);
//        return new MyDate(tempDate.getHour24(), tempDate.getMinute()).getTime();
        if (isTimeSet) {
            if (isRelative)
                return relativeReference.getTime()+getTime();
            else
            return time;
        } else {
            return 0;
        }

    }

    void setDateAndTime(long date) {
        setDate(date);
        setTime(date);
    }

    void setDate(long date) {
        tempDate.setTime(date);
        date = new MyDate(tempDate.getDay(), tempDate.getMonth(), tempDate.getYear()).getTime();
        isDateSet = true;
    }

    void setDate(int day, int month, int year) {
        setDate(new MyDate(day, month, year).getTime());
    }

    void setTime(long time) {
        tempDate.setTime(time);
        new MyDate(tempDate.getHour24(), tempDate.getMinute()).getTime();
        isTimeSet = true;
    }

    void setTime(int hour24, int minutes) {
        setTime(new MyDate(hour24, minutes).getTime());
    }
}
