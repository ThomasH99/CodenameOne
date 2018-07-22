/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/*
 * Copyright 2008-2009 Daniel Cachapa <cachapa@gmail.com>
 *
 * This program is distributed under the terms of the GNU General Public Licence Version 3
 * The licence can be read in its entirety in the LICENSE.txt file accompaning this source code,
 * or at: http://www.gnu.org/copyleft/gpl.html
 *
 * This file is part of WeightWatch.
 *
 * WeightWatch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * WeightWatch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the WeightWatch source code. If not, see
 * <http://www.gnu.org/licenses/>.
 */
//package net.cachapa.weightwatch.util;
//import java.text.SimpleDateFormat;
import com.codename1.l10n.DateFormat;
import com.codename1.l10n.L10NManager;
import com.codename1.l10n.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
//import java.util.Locale;

public class MyDate extends Date {

    protected long timeWithFlags;
    final static long dateSetBit = 0x8000000000000000L;
    final static long timeSetBit = 0x4000000000000000L;
//    final static long timeSetBit = dateSetBit / 2;
    /* mask matching all flags (use eg. like timeNoFlags = timeWithFlags from the time by using '&' operator */
    final static long flagMask = timeSetBit | dateSetBit;
    /* mask matching the time bits. Use eg. like timeNoFlags = timeWithFlags & timeMask */
    final static long timeMask = ~flagMask;
    final static long topmostSignbit = (flagMask >> 1) & ~flagMask; //find the topmost unused bit (e.g. flags=11000, topnost unused: 100)
    //long.MAX= 9,223,372,036,854,775,807 == 9223372036854775807
//    static final String JANUARY = "January";
//    static final String FEBRUARY = "February";
//    static final String MARCH = "March";
//    static final String APRIL = "April";
//    static final String MAY = "May";
//    static final String JUNE = "June";
//    static final String JULY = "July";
//    static final String AUGUST = "August";
//    static final String SEPTEMBER = "September";
//    static final String OCTOBER = "October";
//    static final String NOVEMBER = "November";
//    static final String DECEMBER = "December";
    static final String JANUARY_SHORT = "Jan";
    static final String FEBRUARY_SHORT = "Feb";
    static final String MARCH_SHORT = "Mar";
    static final String APRIL_SHORT = "Apr";
    static final String MAY_SHORT = "May";
    static final String JUNE_SHORT = "Jun";
    static final String JULY_SHORT = "Jul";
    static final String AUGUST_SHORT = "Aug";
    static final String SEPTEMBER_SHORT = "Sep";
    static final String OCTOBER_SHORT = "Oct";
    static final String NOVEMBER_SHORT = "Nov";
    static final String DECEMBER_SHORT = "Dec";
//    private static final String[] MONTH_NAMES = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
//    static final String[] MONTH_NAMES = {JANUARY, FEBRUARY, MARCH, APRIL, MAY, JUNE, JULY, AUGUST, SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER};
//    private static final String[] SHORT_MONTH_NAMES = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    static final String[] MONTH_NAMES_SHORT = {JANUARY_SHORT, FEBRUARY_SHORT, MARCH_SHORT, APRIL_SHORT, MAY_SHORT, JUNE_SHORT, JULY_SHORT, AUGUST_SHORT, SEPTEMBER_SHORT, OCTOBER_SHORT, NOVEMBER_SHORT, DECEMBER_SHORT};
    static final String SUNDAY = "Sunday";
    static final String MONDAY = "Monday";
    static final String TUESDAY = "Tuesday";
    static final String WEDNESDAY = "Wednesday";
    static final String THURSDAY = "Thursday";
    static final String FRIDAY = "Friday";
    static final String SATURDAY = "Saturday";
    static final String WEEKDAYS = "Weekday"; //"Workday"
    static final String WEEKENDS = "Weekend"; //"Weekend day"
    static final String SUNDAY_SHORT = "Sun";
    static final String MONDAY_SHORT = "Mon";
    static final String TUESDAY_SHORT = "Tue";
    static final String WEDNESDAY_SHORT = "Wed";
    static final String THURSDAY_SHORT = "Thu";
    static final String FRIDAY_SHORT = "Fri";
    static final String SATURDAY_SHORT = "Sat";
    static final String WEEKDAYS_SHORT = "Weekday";
    static final String WEEKENDS_SHORT = "Weekend";
//    static final String[] DAY_NAMES = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    static final String[] DAY_NAMES = {SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY};

    static final String[] DAY_NAMES_MONDAY_FIRST = {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY};
    static final String[] DAY_NAMES_MONDAY_FIRST_INCL_WEEKDAYS = {MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY, WEEKDAYS, WEEKENDS};

    static final String[] DAY_NAMES_MONDAY_FIRST_SHORT = {MONDAY_SHORT, TUESDAY_SHORT, WEDNESDAY_SHORT, THURSDAY_SHORT, FRIDAY_SHORT, SATURDAY_SHORT, SUNDAY_SHORT};
    static final String[] DAY_NAMES_MONDAY_FIRST_INCL_WEEKDAYS_SHORT = {MONDAY_SHORT, TUESDAY_SHORT, WEDNESDAY_SHORT, THURSDAY_SHORT, FRIDAY_SHORT, SATURDAY_SHORT, SUNDAY_SHORT, WEEKDAYS_SHORT, WEEKENDS_SHORT};

//    private static final String[] SHORT_DAY_NAMES = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    static final String[] DAY_NAMES_SHORT = {SUNDAY_SHORT, MONDAY_SHORT, TUESDAY_SHORT, WEDNESDAY_SHORT, THURSDAY_SHORT, FRIDAY_SHORT, SATURDAY_SHORT};
    static final int SECOND_IN_MILLISECONDS = 1000;
    static final int MINUTE_IN_MILLISECONDS = 60 * SECOND_IN_MILLISECONDS;
    static final int HOUR_IN_MILISECONDS = 60 * MINUTE_IN_MILLISECONDS; //3600000;
    static final long DAY_IN_MILLISECONDS = 24 * (long) HOUR_IN_MILISECONDS; //86400000;
    static final int DAYS_IN_YEAR = 365; //TODO: what about skudår??
    static final int WEEKS_IN_YEAR = 52; //TODO: what about skudår??
    static final int MONTHS_IN_YEAR = 12;

    final static long MAX_DATE = 253370764800000L; //Human time (GMT): Fri, 01 Jan 9999 00:00:00 GMT, using http://www.epochconverter.com/. NEeded to avoid overflow of json dates
    final static long MIN_DATE = -93692592000000L; //Human time (GMT): Thu, 01 Jan -999 00:00:00 GMT, using http://www.epochconverter.com/. NEeded to avoid overflow of json dates

    /**
     * create a new date initialized to getNow()
     */
    public MyDate() {
//        timeWithFlags = MyDate.getNow();
        setTime(MyDate.getNow()); //set date
    }

///**
// * create a new Date. 
// * @param useDefaultDate initialize date to current date. If false, set Jan 1st, 1970 (so only time of day is set)
// * @param useDefaultTimeOfDay initialize date to default settings for hour/minute of day getDefaultHourOfDay(). If false, use current date from now()
// */    
//    public MyDate(boolean useDefaultDate, boolean useDefaultTimeOfDay) {
////        timeWithFlags = MyDate.getNow();
//        if (useDefaultDate) {
////            setTime(MyDate.getNow()); //set date
//            setTime(Settings.getInstance().getDefaultHourOfDay()); //set date
//        }
//        if (useDefaultTimeOfDay) {
//            setTime(Settings.getInstance().getDefaultHourOfDay(), Settings.getInstance().getDefaultMinuteOfDay());
//        }
//    }
    /**
     * create a new Date.
     *
     * @param useDefaultTimeOfDay initialize date to current time. If false, use
     * default settings for hour/minute of day getDefaultHourOfDay().
     */
//    public MyDate(boolean useDefaultTimeOfDay) {
////        this(false, useDefaultTimeOfDay);
//        this();
//        if (useDefaultTimeOfDay) {
//            setTime(Settings.getInstance().defaultHourOfDay.getInt(), Settings.getInstance().defaultMinuteOfDay.getInt());
//        }
//    }
    public MyDate(long time) {
//        this.timeWithFlags = time;
        setTime(time);
    }

//    public MyDate(MyDate date) {
//        this.time = date.time;
//        this.time = date.getTime();
//    }
    public MyDate(Date date) {
//        this.timeWithFlags = date.getTime();
        setTime(date.getTime());
    }

    /**
     * creates a new date with day, month, year, and hours/minutes set to
     * midnight
     */
//    public MyDate(int day, int month, int year) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(0)); //also sets hour/minute/second/milliesecond to 0
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.YEAR, year);
//        //set time of day to midnight
////        calendar.set(Calendar.HOUR_OF_DAY, 0);
////        calendar.set(Calendar.MINUTE, 0);
////        calendar.set(Calendar.SECOND, 0);
////        calendar.set(Calendar.MILLISECOND, 0);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//    public MyDate(int hour24, int minutes) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(0));
////        calendar.set(Calendar.DAY_OF_MONTH, day);
////        calendar.set(Calendar.MONTH, month - 1);
////        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.HOUR_OF_DAY, hour24);
//        calendar.set(Calendar.MINUTE, minutes);
////        calendar.set(Calendar.SECOND, 0);
////        calendar.set(Calendar.MILLISECOND, 0);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//    public MyDate(int day, int month, int year, int hour, int minute, int seconds) {
////        setDate(day, month, year, hour, minute, seconds);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(0));
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, seconds);
//        setTimeAndFlags(calendar.getTime().getTime(), true, true);
////        time = calendar.getTime().getTime();
//    }
//    public MyDate(int day, int month, int year, int hour, int minute, int seconds, int amPm) {
////        setDate(day, month, year, hour, minute, seconds, amPm);
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(0));
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.YEAR, year);
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, seconds);
//        calendar.set(Calendar.AM_PM, amPm);
//        setTimeAndFlags(calendar.getTime().getTime(), true, true);
////        time = calendar.getTime().getTime();
//    }
    public long getTime() {
//        return time;
//        return (timeWithFlags & topmostSignbit) == 0 ? (timeWithFlags & timeMask) : (timeWithFlags & timeMask) | flagMask; //filter out all bit flags from long time. If topmost negative bit set, then number is assumed negative and the topmost bits are all set to one to recreate original negative number
//        return (timeWithFlags & topmostSignbit) == 0 ? (timeWithFlags & timeMask) : ((timeWithFlags & timeMask) | flagMask); //filter out all bit flags from long time. If topmost negative bit set, then number is assumed negative and the topmost bits are all set to one to recreate original negative number
        return (timeWithFlags); //filter out all bit flags from long time. If topmost negative bit set, then number is assumed negative and the topmost bits are all set to one to recreate original negative number
    }

//    private long getTimeWithFlags() {
//        return timeWithFlags;
//    }
    public void setTime(long time) {
//        this.timeWithFlags = time | timeSetBit | dateSetBit; //if setting time directly like this, we don't know if only date or time is set, so set both flags
        this.timeWithFlags = time;
    }

    /**
     * set time to value, keep flags. Used internally to update the time
     * variable w/o changing the time/date bit flags
     */
    private void setTimeKeepFlags(long value) {
        timeWithFlags = (value & timeMask) | (timeWithFlags & flagMask); // & timeMask to remove any time information that might erroneously set some flags
    }

    /**
     * sets time to value, AND sets time bit and/or date bit. Used to easily
     * update time and set appropriate bits. Does NOT reset the bits to zero if
     * setTimeBit or setDateBit are false!
     */
    private void setTimeAndFlags(long value, boolean setTimeBit, boolean setDateBit) {
        setTimeKeepFlags(value);
        timeWithFlags |= (setTimeBit ? timeSetBit : 0) | (setDateBit ? dateSetBit : 0);
    }

    /**
     * add value to time, keep flags
     */
    private void addToTimeKeepFlags(long value) {
        timeWithFlags = ((timeWithFlags & timeMask) + value) | (timeWithFlags & flagMask);
    }

    /**
     * adds value to time, AND sets time bit and or date bit. Used to easily
     * update time and set appropriate bits. Does NOT reset the bits to zero if
     * setTimeBit or setDateBit are false!
     */
//    private void addToTimeSetFlags(long value, boolean setTimeBit, boolean setDateBit) {
//        addToTimeKeepFlags(value);
//        timeWithFlags = timeWithFlags | (setTimeBit ? timeSetBit : 0) | (setDateBit ? dateSetBit : 0);
//    }
//
//    void setTimeBit(boolean set) {
//        if (set) {
//            timeWithFlags |= timeSetBit; //set bit flag for time is set to 1
//        } else {
//            timeWithFlags &= ~timeSetBit; //reset bit flag for time is set to 0
//        }
//    }
//
//    private void setDateBit(boolean set) {
//        if (set) {
//            timeWithFlags |= dateSetBit; //set bit flag for time is set to 1
//        } else {
//            timeWithFlags &= ~dateSetBit; //reset bit flag for time is set to 0
//        }
//    }
    /**
     * sets both time and date bits - as a convinient shortcut
     */
//    void setTimeAndDateBit(boolean timeSet, boolean dateSet) {
//        setTimeBit(timeSet);
//        setDateBit(dateSet);
//    }
    /**
     * time is set either if the timebit is set, OR time is defined (!=0), and
     * both time bit and date bit are zero (meaning that time was set without
     * using the time/date bits)
     */
//    public boolean isTimeSet() {
////        long time = getTime();
////        return ((time & timeSetBit) != 0) || (time != 0 && (time & timeSetBit) == 0 && (time & dateSetBit) == 0);
////        return ((timeWithFlags & timeSetBit) != 0) || (timeWithFlags != 0 && (timeWithFlags & timeSetBit) == 0 && (timeWithFlags & dateSetBit) == 0);
//        return ((timeWithFlags & timeSetBit) != 0); // || (timeWithFlags != 0 && (timeWithFlags & timeSetBit) == 0 && (timeWithFlags & dateSetBit) == 0);
//    }
//
//    /**
//     * date is set either if the datebit is set, OR time is defined (!=0), and
//     * both time bit and date bit are zero (meaning that time was set without
//     * using the time/date bits)
//     */
//    public boolean isDateSet() {
////        long time = getTime();
////        return ((time & dateSetBit) != 0) || (time != 0 && (time & timeSetBit) == 0 && (time & dateSetBit) == 0);
////        return ((timeWithFlags & dateSetBit) != 0) || (timeWithFlags != 0 && (timeWithFlags & timeSetBit) == 0 && (timeWithFlags & dateSetBit) == 0);
//        return ((timeWithFlags & dateSetBit) != 0); // || (timeWithFlags != 0 && (timeWithFlags & timeSetBit) == 0 && (timeWithFlags & dateSetBit) == 0);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public String toString() {
//        if (!isDateSet()) {
//            return "";
//        } else {
//            //TODO!!!! support locales in this format!!
////            SimpleDateFormat format = new SimpleDateFormat();
////            return Formatter.formatDate(this);
////            return formatDateXXX();
////            Calendar cal = new Calendar();
////            cal.setTime(this);
////            return cal.getDisplayName(FORMAT_LONG, FORMAT_LONG, Locale.FRENCH)
////            return cal.getDisplayName(FORMAT_LONG, FORMAT_LONG, Locale.FRENCH)
////        return getShortDayName() + " " + getDay() + "/" + getMonth() + "/" + getYear()
////                //                + " " + getHour24() + ":" + (getMinute()>10?""+getMinute():"0"+getMinute()) + ":" + (getSeconds()>10?""+getSeconds():"0"+getSeconds()) + " [" + getMilliSeconds() + "] =" + time;
////                //                + " " + getHour24() + ":" + (getMinute() > 10 ? "" + getMinute() : "0" + getMinute()) + ":" + (getSeconds() > 10 ? "" + getSeconds() : "0" + getSeconds()) + ":" + getMilliSeconds();
////                + " " + getHour24() + ":" + format2(getMinute()) + ":" + format2(getSeconds()) + "," + getMilliSeconds();
//        }
//    }
//</editor-fold>
//    public String xtoString() {
//        String dateString = "";
//        dateString += this.getDay() + "-";
//        dateString += this.getMonthName() + "-";
//        dateString += this.getYear();
//        return dateString;
//    }
//    public void setDay(int day) {
////        day = day < 1 ? 1 : (day>31? m 32; //force datinto legal interval 1-days in month
//        day = day < 1 ? 1 : (day > 31 ? 31 : day); //force datinto legal interval 1-days in month
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * month: 1-12
//     */
//    public void setMonth(int month) {
//        month = month < 1 ? 1 : (month > 12 ? 12 : month); // - 1) % 12; //force months into legal interval 0-11
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
////        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.MONTH, month - 1);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * adjusts year to legal value. ??? Should force
//     */
////    public int yearAdjust(int year, boolean add2000IfBelow100) {
//////        return = year < 100 && add2000IfBelow100 ? year+2000 : (year <1971 ? 1970 > 2200 ? 2200 : year);
////        return 0;
////    }
//    public void setYear(int year) {
////        year = year < 100 ? year + 2000 : (year > 2200 ? 2200 : year);
////        year = year < 1970 ? 1970 : year; //UI: years below 1971 not allowed(since top bits are used for time/date flags, and will be used by 'negative' dates before 1970
//        year = year < 0 ? 0 : year; //UI: years below 1971 not allowed(since top bits are used for time/date flags, and will be used by 'negative' dates before 1970
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.YEAR, year);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setYear(int year, boolean add2000IfBelow100) {
//        setYear(year < 100 && add2000IfBelow100 ? year + 2000 : year);
//    }
//
//    public void setHour24(int hour) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setHour12(int hour, int amPm) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.AM_PM, amPm);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setHour12(int hour) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setAmPm(int amPm) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.AM_PM, amPm);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setMinute(int minute) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.MINUTE, minute);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    public void setSeconds(int seconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.SECOND, seconds);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
    /**
     * resets both date and time to 0 (and resets date and time set flags as
     * well)
     */
//    public void resetDateAndTime() {
////        setTime(0);
//        timeWithFlags = 0;
//    }
//
//    /**
//     * resets time of day *only*, but keeps date
//     */
//    public void resetTime() {
//        setTime(0, 0);
//        setTimeBit(false);
//    }
//
//    /**
//     * sets the time of the given date. Leaves the date untouched. Resets
//     * seconds and milliseconds to 0
//     */
//    public void setTime(int hour, int minute) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, 0);
//        calendar.set(Calendar.MILLISECOND, 0);
////        calendar.set(Calendar.SECOND, seconds);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * sets the time of the given date. Leaves the date untouched
//     */
//    public void setTime(int hour, int minute, int seconds) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, seconds);
//        calendar.set(Calendar.MILLISECOND, 0);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * sets the time of the given date. Leaves the date untouched. amPm should
//     * be Calendar.AM or Calendar.PM
//     */
//    public void setTime(int hour, int minute, int seconds, int amPm) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
////        calendar.set(Calendar.HOUR_OF_DAY, hour);
//        calendar.set(Calendar.HOUR, hour);
//        calendar.set(Calendar.MINUTE, minute);
//        calendar.set(Calendar.SECOND, seconds);
//        calendar.set(Calendar.AM_PM, amPm);
//        calendar.set(Calendar.MILLISECOND, 0);
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * resets (removes any date information == 1/1/1970) date *only*, but keeps
//     * time of day
//     */
//    public void resetDate() {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(0)); //create a 'zero' date
//        //transfer the old time of day to it
//        calendar.set(Calendar.HOUR_OF_DAY, getHour24());
//        calendar.set(Calendar.MINUTE, getMinute());
//        calendar.set(Calendar.SECOND, getSeconds());
//        calendar.set(Calendar.MILLISECOND, getMilliSeconds());
//        setTimeAndFlags(calendar.getTime().getTime(), true, false);
////        time = calendar.getTime().getTime();
//    }
//
//    /**
//     * sets the date. Leaves the time of day untouched
//     */
//    public void setDate(int day, int month, int year) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new MyDate(getTime()));
//        calendar.set(Calendar.DAY_OF_MONTH, day);
//        calendar.set(Calendar.MONTH, month - 1); //0=January, ...
//        calendar.set(Calendar.YEAR, year);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void xxsetDate(int day, int month, int year, int hour, int minute, int seconds) {
//        setDate(day, month, year);
//        setTime(hour, minute, seconds);
////        Calendar calendar = Calendar.getInstance();
////        calendar.setTime(new MyDate(time));
////        calendar.set(Calendar.DAY_OF_MONTH, day);
////        calendar.set(Calendar.MONTH, month - 1);
////        calendar.set(Calendar.YEAR, year);
////        calendar.set(Calendar.HOUR_OF_DAY, hour);
////        calendar.set(Calendar.MINUTE, minute);
////        calendar.set(Calendar.SECOND, seconds);
////        time = calendar.getTime().getTime();
//    }
//    public void xxsetDate(int day, int month, int year, int hour, int minute, int seconds, int amPm) {
//        setDate(day, month, year);
//        setTime(hour, minute, seconds, amPm);
////        Calendar calendar = Calendar.getInstance(); //new Date(0L)); //Calendar.getInstance();
////        calendar.setTime(new MyDate(time));
////        calendar.set(Calendar.DAY_OF_MONTH, day);
////        calendar.set(Calendar.MONTH, month - 1);
////        calendar.set(Calendar.YEAR, year);
////        calendar.set(Calendar.HOUR_OF_DAY, hour);
////        calendar.set(Calendar.MINUTE, minute);
////        calendar.set(Calendar.SECOND, seconds);
////        calendar.set(Calendar.AM_PM, amPm);
////        time = calendar.getTime().getTime();
//    }// </editor-fold>
    /**
     * returns day in month, 1..31
     */
//    public static int getDay(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(date.getTime()));
//        return cal.get(Calendar.DAY_OF_MONTH);
//    }
//
//    /**
//     * returns day in week, SUNDAY==1, MONDAY==2 etc.
//     */
//    public static int getDayOfWeek(Date date) {
//        Calendar cal = Calendar.getInstance();
////        cal.setTime(new Date(date.getTime()));
//        cal.setTime(date);
//        return cal.get(Calendar.DAY_OF_WEEK); //SUNDAY==1, MONDAY==2 etc.
//    }
//
//    /**
//     * returns day in year, Jan 1st == 1, 31st Dec == 365 or 366
//     */
//    public static int getDayOfYear(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(date.getTime()));
//        cal.set(Calendar.MONTH, Calendar.JANUARY);
//        cal.set(Calendar.DAY_OF_MONTH, 1);
//        long jan1st = cal.getTime().getTime(); //get jan 1st of this year
////        cal.setTime(new Date(date.getTime()));
//        cal.setTime(date);
////        Calendar cal2 = Calendar.getInstance();
//        return (int) ((date.getTime() - jan1st) / DAY_IN_MILLISECONDS) + 1;
//    }
//
//    /**
//     * returns month, January==1
//     */
//    public int getMonth(Date date) {
//        Calendar cal = Calendar.getInstance();
////        cal.setTime(new Date(date.getTime()));
//        cal.setTime(date);
//        return cal.get(Calendar.MONTH) + 1;
//    }
//
//    public int getYear(Date date) {
//        Calendar cal = Calendar.getInstance();
////        cal.setTime(new Date(date.getTime()));
//        cal.setTime(date);
//        return cal.get(Calendar.YEAR);
//    }
//
//    /**
//     * returns hour of day, 0-23
//     */
//    public int getHour24() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.HOUR_OF_DAY);
//    }
//
//    /**
//     * returns hour of day, 0-11, use getAmPm() to get am or pm
//     */
//    public int getHour12() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.HOUR);
//    }
//
//    public int getMinute() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.MINUTE);
//    }
//
//    public int getSeconds() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.SECOND);
//    }
//
//    public int getMilliSeconds() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.MILLISECOND);
//    }
//
//    public int getAmPm() {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date(getTime()));
//        return cal.get(Calendar.AM_PM);
//    }
    /**
     * returns the week number within the year (according to ISO definition).
     * See definition overview:
     * http://en.wikipedia.org/wiki/Week_number#Week_numbering
     * http://en.wikipedia.org/wiki/Talk:ISO_week_date#Algorithms: Determine its
     * Day of Week, D Use that to move to the nearest Thursday (-3..+3 days)
     * Note the year of that date, Y Obtain January 1 of that year Get the
     * Ordinal Date of that Thursday, DDD of YYYY-DDD Then W is 1 + (DDD-1) div
     * 7
     *
     * @return
     */
//    public static int getWeekOfYear(Date date) {
//        if (Settings.getInstance().useISOWeekNumbering()) {
//            Calendar cal = Calendar.getInstance();
//            cal.setTime(new Date(date.getTime()));
//            long dateTmp = date.getTime();
//            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK); //SUNDAY==1, MONDAY==2 etc.
//            int diffToThursday = dayOfWeek - Calendar.THURSDAY;
//            //unless this day is Sunday, then the nearest thursday is found by adding/subtracting the different in days between current day and THURSDAY, e.g. today==SAT==7, THURSDAY==5, 7-5=2, nearest thursday is then today-2
//            if (dayOfWeek == Calendar.SUNDAY) {
//                dateTmp -= DAY_IN_MILLISECONDS * 3;
//            } else {
//                dateTmp -= DAY_IN_MILLISECONDS * diffToThursday;
//            }
//            cal.setTime(new Date(dateTmp));
//            dateTmp = cal.getTime().getTime();
//            //get janaruy first of same year as that Thursday (which could be in the previous year)
//            cal.set(Calendar.MONTH, Calendar.JANUARY);
//            cal.set(Calendar.DAY_OF_MONTH, 1);
//            long janFirst = cal.getTime().getTime();
//            int dDD = (int) ((dateTmp - janFirst) / DAY_IN_MILLISECONDS);
//            int week = 1 + (dDD - 1) / 7;
//            return week;
//        } else { //US week numbering, starting Sunday, 1st week starts on January 1st
////http://stackoverflow.com/questions/274861/how-do-i-calculate-the-week-number-given-a-date
////int julian = getDayOfYear(myDate)  // Jan 1 = 1, Jan 2 = 2, etc...
////int dow = getDayOfWeek(myDate)     // Sun = 0, Mon = 1, etc...
////int dowJan1 = getDayOfWeek("1/1/" + thisYear)   // find out first of year's day
////int weekNum = (julian / 7) + 1     // Get our week#
////if (dow < dowJan1)                 // adjust for being after Saturday of week #1
////    ++weekNum;
////return (weekNum)
//            int julian = getDayOfYear(date);  // Jan 1 = 1, Jan 2 = 2, etc...
//            int dow = getDayOfWeek(date) - Calendar.SUNDAY;     // Sun = 0, Mon = 1, etc...
//            MyDate jan1st = new MyDate(date.getTime());
//            jan1st.setMonth(1);
//            jan1st.setDay(1);
//            int dowJan1 = jan1st.getDayOfWeek(date);   // find out first of year's day
//            int weekNum = (julian / 7) + 1;     // Get our week#
//            if (dow < dowJan1) // adjust for being after Saturday of week #1
//            {
//                ++weekNum;
//            }
//            return (weekNum);
//        }
//    }
    static Date getStartOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        return calendar.getTime();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static Date getStartOfWeekXXX(Date date) {
//        //NOT WORKING since CN1 doesn't implement cal.getFirstDayOfWeek()
//        //http://stackoverflow.com/questions/2937086/how-to-get-the-first-day-of-the-current-week-and-month
//        // get today and clear time of day
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//
//        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
////        cal.clear(Calendar.MINUTE);
////        cal.clear(Calendar.SECOND);
////        cal.clear(Calendar.MILLISECOND);
//        cal.set(Calendar.MINUTE,0);
//        cal.set(Calendar.SECOND,0);
//        cal.set(Calendar.MILLISECOND,0);
//
//// get start of this week in milliseconds
//        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
//        return cal.getTime();
//    }
//</editor-fold>
    static Date getEndOfWeek(Date date) {
        return new Date(getStartOfWeek(date).getTime() + MyDate.DAY_IN_MILLISECONDS * 7 - 1); //end of week is start of week + 7 days -1ms to get the last millisecond in the week
    }

    static Date getStartOfMonth(Date date) {
        //http://stackoverflow.com/questions/2937086/how-to-get-the-first-day-of-the-current-week-and-month
// get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//        cal.clear(Calendar.MINUTE);
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

// get start of the month
        cal.set(Calendar.DAY_OF_MONTH, 1);

        return cal.getTime();
    }

    static Date getEndOfMonth(Date date) {
        //http://stackoverflow.com/questions/2937086/how-to-get-the-first-day-of-the-current-week-and-month
// get today and clear time of day
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0); // ! clear would not reset the hour of day !
//        cal.clear(Calendar.MINUTE);
//        cal.clear(Calendar.SECOND);
//        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

// get start of the next month
        cal.add(Calendar.MONTH, 1);
//        return cal.getTime();
        return new Date(cal.getTime().getTime() - 1); //end of month is start of next month -1 millisecond 
    }

    /**
     * returns the number of this day in the week, taking into account if weeks
     * start on Mondays (Europe) or Sundays (US). E.g. Europe: Monday==1,
     * Sunday==7. US: Monday==2, Sunday==1
     */
    int getDayNumberInWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(getTime()));
        return (Settings.getInstance().weeksStartOnMondays() ? (cal.get(Calendar.DAY_OF_WEEK) == 1 ? 7 : cal.get(Calendar.DAY_OF_WEEK) - 1) : cal.get(Calendar.DAY_OF_WEEK)); //SUNDAY==1, MONDAY==2 etc.
//        return cal.get(Calendar.DAY_OF_WEEK)+(Settings.getInstance().weeksStartOnMondays()?); //SUNDAY==1, MONDAY==2 etc.
    }

//    public void setWeekAndDayXX(int week, int day) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new MyDate(getTime()));
//    }
    /**
     * returns how many days the parameters date is before/after this date. If
     * the given date is yesterday (compared to this date) returns -1, if today
     * 0, if tomorrow 1, etc.
     *
     * @param date
     * @return
     */
//    public int differenceInDays(MyDate date) {
//        // First we create an auxiliary date and set it to the same miliseconds
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(getTime()));
//        // Then we set the day, month and year on the auxiliary date
//        calendar.set(Calendar.DAY_OF_MONTH, date.getDay(date));
//        calendar.set(Calendar.MONTH, date.getMonth(date) - 1);
//        calendar.set(Calendar.YEAR, date.getYear(date));
//        // This way we can make the following calculation without having to worry about rounding errors
//        return (int) ((calendar.getTime().getTime() - getTime()) / DAY_IN_MILLISECONDS);
//    }
    public void addDays(int days) {
//        time += days * DAY_IN_MILLISECONDS;
//        setTime(getTimeWithFlags() + days * DAY_IN_MILLISECONDS);
        addToTimeKeepFlags(days * DAY_IN_MILLISECONDS);
    }

//    public void subtractDays(int days) {
//        time -= days * DAY_IN_MILLISECONDS;
//    }
    public void addMinutes(int minutes) {
//        time += minutes * MINUTE_IN_MILLISECONDS;
//        setTime(getTime() + minutes * MINUTE_IN_MILLISECONDS);
        addToTimeKeepFlags(minutes * MINUTE_IN_MILLISECONDS);
    }

//    public void subtractMinutes(int minutes) { //-use addMinutes(-3)
//        time -= minutes * MINUTE_IN_MILLISECONDS;
//    }
    public void addHours(int hours) {
//        time += hours * HOUR_IN_MILISECONDS;
//        setTime(getTime() + hours * HOUR_IN_MILISECONDS);
        addToTimeKeepFlags(hours * HOUR_IN_MILISECONDS);
    }

//    public void subtractHours(int hours) {
//        time -= hours * HOUR_IN_MILISECONDS;
//    }
    /**
     * returns the highest valid day in the given month/year, eg
     * getValidDayOfMonth(31,2,2010) will return 28. If dayOfMonth is already
     * valid, the value itself is returned (no change). If dayOfMonth is <1, the
     * same value is returned.
     */
    public int getValidDayOfMonth(int dayOfMonth, int month, int year) {
        int daysInMonth = getDaysInMonth(month, year);
        while (dayOfMonth > daysInMonth && dayOfMonth > 1) {
            dayOfMonth--; //reduce date until we reach a valid one for the given month/year
        }
        return dayOfMonth;
    }

    /**
     * moves date one month back. If setToFirstInMonth then also sets day of
     * that month to the 1st day of the month
     */
//    public void retreatMonth(Date date, boolean setToFirstInMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(getTime()));
//
//        int year = getYear(date);
//        int month = getMonth(date) - 1;
//        if (month < 1) {
//            year--;
//            month = 12;
//        }
//
//        if (setToFirstInMonth) {
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//        } else {
//            calendar.set(Calendar.DAY_OF_MONTH, getValidDayOfMonth(getDay(date), month, year));
//        }
//        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.YEAR, year);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
    /**
     * moves date one month forward. If setToFirstInMonth then also sets day of
     * that month to the 1st day of the month
     */
//    public void advanceMonth(Date date, boolean setToFirstInMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(getTime()));
//
//        int year = getYear(date);
//        int month = getMonth(date) + 1;
//        if (month > 12) {
//            year++;
//            month = 1;
//        }
//
//        if (setToFirstInMonth) {
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//        } else {
//            calendar.set(Calendar.DAY_OF_MONTH, getValidDayOfMonth(getDay(date), month, year));
//        }
//        calendar.set(Calendar.MONTH, month - 1);
//        calendar.set(Calendar.YEAR, year);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//    public void retreatYear(Date date, boolean setToFirstInMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(getTime()));
//
//        if (setToFirstInMonth) {
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//        }
//        calendar.set(Calendar.MONTH, getMonth(date) - 1);
//        calendar.set(Calendar.YEAR, getYear(date) - 1);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
//    public void advanceYear(Date date, boolean setToFirstInMonth) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date(getTime()));
//
//        if (setToFirstInMonth) {
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//        }
//        calendar.set(Calendar.MONTH, getMonth(date) - 1);
//        calendar.set(Calendar.YEAR, getYear(date) + 1);
//        setTimeAndFlags(calendar.getTime().getTime(), false, true);
////        time = calendar.getTime().getTime();
//    }
    /**
     * returns list of display text strings indicating week in month (FIRST,
     * THIRD, LAST, ...)
     */
//    static String[] getMonthNames() {
//        return MONTH_NAMES;
//    }
    static String[] getShortMonthNames() {
        return MONTH_NAMES_SHORT;
    }

//    public static String getMonthName(int month) {
//        return Localize.localize(MONTH_NAMES[month - 1]);
//    }
//    public String getMonthName(Date date) {
//        return Localize.localize(MONTH_NAMES[getMonth(date) - 1]);
//    }
//
//    public static String getShortMonthName(int month) {
//        return Localize.localize(MONTH_NAMES_SHORT[month - 1]);
//    }
//
//    public String getShortMonthName(Date date) {
//        return Localize.localize(DAY_NAMES_SHORT[getDay(date) - 1]);
//    }
//
//    public static String getDayName(int dayOfWeek) {
//        return Localize.localize(DAY_NAMES[dayOfWeek - 1]);
//    }
//
//    public String getDayName(Date date) {
////        return L.l(DAY_NAMES[getDayOfWeek() - 1]);
//        return getDayName(getDayOfWeek(date));
//    }
//    public static String getShortDayName(int dayOfWeek) {
//        return Localize.localize(DAY_NAMES_SHORT[dayOfWeek - 1]);
//    }
//    public String getShortDayName(Date date) {
////        return L.l(SHORT_DAY_NAMES[getDay() - 1]);
//        return getShortDayName(getDayOfWeek(date));
//    }
//    public boolean isBefore(MyDate other) {
//        return getTime() < other.getTime();
//    }
    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                return year % 4 == 0 && year % 100 != 0 ? 29 : 28;
            default:
                return 31;
        }
    }

    public static int getDaysInYear(int year) {
        return year % 4 == 0 && year % 100 != 0 ? 366 : 365;
    }

    /**
     * returns list of display text strings indicating week in month (FIRST,
     * THIRD, LAST, ...)
     */
    static String[] getWeekDayNamesMondayFirst() {
        return DAY_NAMES_MONDAY_FIRST;
    }

    static String[] getShortWeekDayNamesMondayFirst() {
        return DAY_NAMES_MONDAY_FIRST_SHORT;
    }

    static String[] getWeekDayNamesMondayFirstInclWeekdays() {
        return DAY_NAMES_MONDAY_FIRST_INCL_WEEKDAYS;
    }

    static String[] getShortWeekDayNamesMondayFirstInclWeekdays() {
        return DAY_NAMES_MONDAY_FIRST_INCL_WEEKDAYS_SHORT;
    }

    /**
     * returns list of display text strings indicating week in month (FIRST,
     * THIRD, LAST, ...)
     */
    static String[] getWeekDayNames() {
        return DAY_NAMES;
    }

    /**
     * returns week day (Calendar.SUNDAY==0, Calendar.MONDAY==1) of the given
     * date (day, month, year)
     */
    public static int getWeekday(int day, int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * returns week day
     */
//    int getWeekday(Date date) {
//        return getWeekday(this.getDay(date), this.getMonth(date), this.getYear(date));
//    }
//    public String toShortString() {
//        String dateString = "";
//        dateString += this.getDay() + ".";
//        dateString += this.getShortMonthName() + ".";
//        dateString += String.valueOf(this.getYear()).substring(2, 4);
//        return dateString;
//    }
    /**
     * returns true if other is the same date (day, month, year)
     *
     * @param other
     * @return
     */
//    public boolean equalsDate(Date date, MyDate other) {
//        return (getDay(date) == getDay(other) && getMonth(date) == getMonth(other) && getYear(date) == getYear(other));
////        if (this.getDay() == other.getDay() && this.getMonth() == other.getMonth() && this.getYear() == other.getYear()) {
////            return true;
////        }
////        return false;
//    }
    /**
     * returns true if other is the same time (hour, minutes)
     *
     * @param other
     * @return
     */
//    public boolean equalsHourMinutes(MyDate other) {
//        return (getHour24() == other.getHour24() && getMinute() == other.getMinute());
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
//    final static int ONE_DAY = 0;
//    final static int TWO_DAYS = 0;
//    final static int SEVEN_DAYS = 0;
//    final static int THIRTY_DAYS = 0;
//    final static int ONE_YEAR = 0;
//    public boolean isToday(long timeNow) {
//        MyDate dateNow = new MyDate(timeNow);
//        return (getDay()==dateNow.getDay() && getMonth()==dateNow.getMonth() && getYear()==dateNow.getYear());
//    }
//
//    public boolean isTomorrow(long timeNow) {
//        MyDate dateNow = new MyDate(timeNow);
//        return (getDay()==dateNow.getDay()+1 && getMonth()==dateNow.getMonth() && getYear()==dateNow.getYear()
//                || getDay()==dateNow.getDay()+1 && getMonth()==dateNow.getMonth() && getYear()==dateNow.getYear());
//    }
//
//    public boolean isNextSevenDays(long timeNow) {
//        MyDate dateNow = new MyDate(timeNow);
//        return (getDay()<=dateNow.getDay()+7 && getMonth()==dateNow.getMonth() && getYear()==dateNow.getYear());
//    }// </editor-fold>
    public static String format2(int i) {
        return i > 9 ? "" + i : "0" + i;
    }

    public static String format4(int i) {
        return i > 1000 ? "" + i : " " + i;
    }

    /**
     * formats the date according to locale
     *
     * @return
     */
//    public String formatDate() {
//        return formatDate(true);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private String formatDateXXX() { //boolean returnEmptyStringIfZero) {
////        Calendar calendar = Calendar.getInstance();
////        return formatDate(calendar.getTime().getTime()); //TODO!!!!: doesn't seem to work under Java ME SDK 3.0 (gives 0)
////        return formatDate(Calendar.getInstance().getTime().getTime()); //TODO!!!!: doesn't seem to work under Java ME SDK 3.0 (gives 0)
////        return formatDate(MyDate.getNow());
//        if (getTime() == 0) {
//            return "";
//        } else {
//            return formatDate(null, false);
//        }
//    }
//
//    private String formatDateXXX(boolean casualFormat) {
//        Calendar calendar = Calendar.getInstance();
//        return formatDateXXX(calendar.getTime().getTime(), casualFormat);
//    }
//
//    private String formatDateXXX(long referenceDate) {
//        return formatDateXXX(referenceDate, true);
//    }
//
//    private String formatDateXXX(long referenceDate, boolean casualFormat) {
//        return formatDate(new MyDate(referenceDate), casualFormat);
//    }
//    final static int FORMAT_CASUAL = 1;
//    final static int FORMAT_LONG = 2;
//    final static int FORMAT_SHORT = 3;
//    final static int FORMAT_FULL = 4;
//    final static int FORMAT_DEFAULT = 5;
//    final static int FORMAT_TIME_ONLY_24HOURS = 6;
//    final static int FORMAT_WORK_INTERVAL = 7;
//    final static int FORMAT_DDMM_HHMM = 8;
//    final static int FORMAT_DATE_ONLY_DDMMMYYYY = 9;
//
//    private String formatDateNaturalXXX(int dateFormatId) {
//        return MyDate.this.formatDateNaturalXXX(new MyDate(), dateFormatId);
//    }
//
//    private String formatDateNaturalXXX(MyDate referenceDate, int dateFormatId) {
////        long interval = getTime() - today.getTime();
////        int diffInDays = date2.differenceInDays(todaysDate);
////        int diffInDays = (int) ((todaysDate.getTime() - refDate.getTime()) / DAY_IN_MILLISECONDS);
////        int diffInDays = todaysDate.differenceInDays(refDate);
//        MyDate todaysDate = new MyDate(getDay(), getMonth(), getYear()); //sets date to the default time of day, eg always midnight, in this difference
//        MyDate refDate = new MyDate(referenceDate.getDay(), referenceDate.getMonth(), referenceDate.getYear()); //ditto
//        int diffInDays = refDate.differenceInDays(todaysDate);
//        switch (dateFormatId) {
//            case FORMAT_CASUAL:
//                if (diffInDays < -1) {
//                    return "OVERDUE";
//                }
//                if (diffInDays < 0) {
//                    return "yesterday";
//                } else if (diffInDays < 1) {
//                    return "today";
//                } else if (diffInDays < 2) {
//                    return "tomorrow";
//                } else if (diffInDays < 7) { //this week: use day names
//                    return getDayName().substring(0, Settings.getInstance().getCharsInShortDates()); //2 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
//                } else if (diffInDays < getDaysInMonth(getMonth(), getYear())) { //within current month: use day name + day of month
//                    return getDayName().substring(0, Settings.getInstance().getCharsInShortDates()) + getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
//                } else if (diffInDays < 365) { //within this year:
////                return getDay() + getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()); //within next year: day of month + first two letters of monht, e.g. 12Ju,
//                    return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + getDay(); //within next year: day of month + first two letters of monht, e.g. 12Ju,NO: Jun12
//                } else if (diffInDays < 730) {
//                    return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                } else {
//                    return "" + getYear(); //beyond 2 years: year, e.g. 2013, 2017
//                }
////            break;
//            case FORMAT_LONG:
//                Settings.getInstance().getDefaultDateAndTimeFormat();
//                //TODO!!!!: use the same date formatter in DateField5 and here to ensure consistency!!!!
////            return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                return getShortDayName() + " " + getDay() + "/" + getMonth() + "/" + getYear()
//                        + " " + getHour24() + ":" + format2(getMinute()) + ":" + format2(getSeconds()) + ":" + getMilliSeconds();
//            case FORMAT_DATE_ONLY_DDMMMYYYY:
//                Settings.getInstance().getDefaultDateAndTimeFormat();
//                //TODO!!!!: use the same date formatter in DateField5 and here to ensure consistency!!!!
////            return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                return getShortDayName() + " " + getDay() + "/" + getMonth() + "/" + getYear();
//            case FORMAT_DEFAULT:
//            case FORMAT_FULL:
//                return getShortDayName() + " " + getDay() + "/" + getMonth() + "/" + getYear()
//                        + " " + getHour24() + ":" + format2(getMinute());
//            case FORMAT_TIME_ONLY_24HOURS:
//                return getHour24() + ":" + format2(getMinute());
//            case FORMAT_SHORT:
//                if (referenceDate.getTime() != 0) {
//                    if (diffInDays < 1) {
//                        return getHour24() + ":" + format2(getMinute());
//                    } else if (diffInDays < 7) {
//                        return getShortDayName() + " " + getHour24() + ":" + format2(getMinute());
//                    }
//                } else {
//                    return getShortDayName() + getDay() + "/" + getMonth() + " " + getHour24() + ":" + format2(getMinute());
//                }
////            case FORMAT_DEFAULT: //TODO!!!!!: always use default format for dates (NB. Also need to centralize in EditDate...)
////                Settings.getInstance().getDefaultDateAndTimeFormat();
////                return "***";
//            case FORMAT_DDMM_HHMM:
//                if (referenceDate.getTime() != 0) {
////                    if (diffInDays < 7) {
////                        return getShortDayName() + " " + getHour24() + ":" + format2(getMinute());
////                    }
////                } else {
//                    return format2(getDay()) + "/" + format2(getMonth()) + " " + format2(getHour24()) + ":" + format2(getMinute());
//                }
////            case FORMAT_DEFAULT: //TODO!!!!!: always use default format for dates (NB. Also need to centralize in EditDate...)
////                Settings.getInstance().getDefaultDateAndTimeFormat();
////                return "***";
//            case FORMAT_WORK_INTERVAL:
//                if (referenceDate.getTime() != 0) {
//                    if (diffInDays < 1) {
//                        return getHour24() + ":" + format2(getMinute());
//                    } else if (diffInDays < 7) {
//                        return getShortDayName() + getHour24() + ":" + format2(getMinute());
//                    }
//                } else {
//                    return getShortDayName() + getDay() + "/" + getMonth() + getHour24() + ":" + format2(getMinute());
//                }
//            default:
//                return toString();
//        }
//    }
//
//    /**
//     *
//     * @param date
//     * @param referenceDate
//     * @param dateFormatId
//     * @return
//     */
//    private static String formatDateNaturalXXX(MyDate date, MyDate referenceDate, int dateFormatId) {
//        return formatDateNaturalXXX(date, referenceDate, dateFormatId, true);
//    }
//
//    private static String formatDateNaturalXXX(MyDate date, MyDate referenceDate, int dateFormatId, boolean pastDatesAsOVERDUE) {
////        long interval = getTime() - today.getTime();
////        int diffInDays = date2.differenceInDays(todaysDate);
////        int diffInDays = (int) ((todaysDate.getTime() - refDate.getTime()) / DAY_IN_MILLISECONDS);
////        int diffInDays = todaysDate.differenceInDays(refDate);
//        MyDate todaysDate = new MyDate(date.getDay(), date.getMonth(), date.getYear()); //sets date to the default time of day, eg always midnight, in this difference
//        MyDate refDate = new MyDate(referenceDate.getDay(), referenceDate.getMonth(), referenceDate.getYear()); //ditto
//        int diffInDays = refDate.differenceInDays(todaysDate);
//        switch (dateFormatId) {
//            case FORMAT_CASUAL:
//                if (date.getTime() == 0) {
//                    return "No date"; //"NONE"
//                }
//                if (diffInDays < -1) {
//                    if (pastDatesAsOVERDUE) {
//                        return "OVERDUE";
//                    }
//                } else if (date.getYear() == refDate.getYear()) {//this year, only Jun12 (could be DD/MM?)
//                    return date.getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + date.getDay();
//                } else { //earlier years DD/MM/YY
//                    return date.getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + date.getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                }
//                if (diffInDays < 0) {
//                    return "yesterday";
//                } else if (diffInDays < 1) {
//                    return "today";
//                } else if (diffInDays < 2) {
//                    return "tomorrow";
//                } else if (diffInDays < 7) { //this week: use day names
//                    return date.getDayName().substring(0, Settings.getInstance().getCharsInShortDates()); //3 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
//                } else if (diffInDays < getDaysInMonth(date.getMonth(), date.getYear())) { //within current month: use day name + day of month
//                    return date.getDayName().substring(0, Settings.getInstance().getCharsInShortDates()) + date.getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
//                } else if (diffInDays < 365) { //within this year:
////                return getDay() + getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()); //within next year: day of month + first two letters of monht, e.g. 12Ju,
//                    return date.getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + date.getDay(); //within next year: day of month + first two letters of monht, e.g. 12Ju,NO: Jun12
//                } else if (diffInDays < 730) {
//                    return date.getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + date.getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                } else {
//                    return "" + date.getYear(); //beyond 2 years: year, e.g. 2013, 2017
//                }
////            break;
//
//            case FORMAT_LONG:
//                Settings.getInstance().getDefaultDateAndTimeFormat();
//                //TODO!!!!: use the same date formatter in DateField5 and here to ensure consistency!!!!
////            return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                return date.getShortDayName() + " " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear()
//                        + " " + date.getHour24() + ":" + format2(date.getMinute()) + ":" + format2(date.getSeconds()) + ":" + date.getMilliSeconds();
//            case FORMAT_DATE_ONLY_DDMMMYYYY:
//                Settings.getInstance().getDefaultDateAndTimeFormat();
//                //TODO!!!!: use the same date formatter in DateField5 and here to ensure consistency!!!!
////            return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//                return date.getShortDayName() + " " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear();
//            case FORMAT_DEFAULT:
//            case FORMAT_FULL:
//                return date.getShortDayName() + " " + date.getDay() + "/" + date.getMonth() + "/" + date.getYear()
//                        + " " + date.getHour24() + ":" + format2(date.getMinute());
//            case FORMAT_TIME_ONLY_24HOURS:
//                return date.getHour24() + ":" + format2(date.getMinute());
//            case FORMAT_SHORT:
//                if (referenceDate.getTime() != 0) {
//                    if (diffInDays < 1) {
//                        return date.getHour24() + ":" + format2(date.getMinute());
//                    } else if (diffInDays < 7) {
//                        return date.getShortDayName() + " " + date.getHour24() + ":" + format2(date.getMinute());
//                    }
//                } else {
//                    return date.getShortDayName() + date.getDay() + "/" + date.getMonth() + " " + date.getHour24() + ":" + format2(date.getMinute());
//                }
////            case FORMAT_DEFAULT: //TODO!!!!!: always use default format for dates (NB. Also need to centralize in EditDate...)
////                Settings.getInstance().getDefaultDateAndTimeFormat();
////                return "***";
//            case FORMAT_DDMM_HHMM:
//                if (referenceDate.getTime() != 0) {
////                    if (diffInDays < 7) {
////                        return getShortDayName() + " " + getHour24() + ":" + format2(getMinute());
////                    }
////                } else {
//                    return format2(date.getDay()) + "/" + format2(date.getMonth()) + " " + format2(date.getHour24()) + ":" + format2(date.getMinute());
//                }
////            case FORMAT_DEFAULT: //TODO!!!!!: always use default format for dates (NB. Also need to centralize in EditDate...)
////                Settings.getInstance().getDefaultDateAndTimeFormat();
////                return "***";
//            case FORMAT_WORK_INTERVAL:
//                if (referenceDate.getTime() != 0) {
//                    if (diffInDays < 1) {
//                        return date.getHour24() + ":" + format2(date.getMinute());
//                    } else if (diffInDays < 7) {
//                        return date.getShortDayName() + date.getHour24() + ":" + format2(date.getMinute());
//                    }
//                } else {
//                    return date.getShortDayName() + date.getDay() + "/" + date.getMonth() + date.getHour24() + ":" + format2(date.getMinute());
//                }
//            default:
//                return date.toString();
//        }
//    }
//
//    private static String formatDateCasualXXX(long date) {
//        return formatDateNaturalXXX(new MyDate(date), new MyDate(), FORMAT_CASUAL);
//    }
//
//    private static String formatDateCasualXXX(Date date) {
//        return formatDateNaturalXXX(new MyDate(date), new MyDate(), FORMAT_CASUAL);
//    }
//</editor-fold>
    private static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    /**
     * returns a short format string for dates, which varies according to the
     * distance to today. Uesterday/Today/Tomorrow. Next 7 days: Mon/Tue/Wed.
     * 7-30 days: Mon12/Tue13. Rest of year: Jun13/Aug28. Other years ??. Past
     * dates this year: Apr12. Other years: xxx. Use for due dates, waiting
     * dates.
     *
     * Options: shortestPossible, useYesterdayTodayTomorrow
     *
     * @param referenceDate
     * @param useYesterdayTodayTomorrow, useYesterdayTodayTomorrow,
     * @return
     */
    static public String formatDateNew(long date) {
        return formatDateNew(new Date(date));
    }

    static public String formatDateNew(Date date) {
        return formatDateNew(date, false, true, false, false, false);
    }

    static public String formatDateTimeNew(Date date) {
        return formatDateNew(date, false, true, true, false, false);
    }

    static public String formatDateTimeNew(long date) {
        return formatDateNew(new Date(date), false, true, true, false, false);
    }

    static public String formatTimeNew(Date date) {
        return formatDateNew(date, false, false, true, false, false);
    }

    private static String formatAsYesterdayTodayTomorrow(Date date) {
        //TODO Internationalize
        Calendar cal = Calendar.getInstance(); //set to now
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long lastMidnight = cal.getTime().getTime();
        long lengthOfDay = HOUR_IN_MILISECONDS * 24;
        long time = date.getTime();
        String str = "";
//        if (date.getTime() == 0) {
//            str = "No date"; //"NONE"
//        } else 
        if (time >= lastMidnight && time < lastMidnight + lengthOfDay) {
            str = "Today";
        } else if (time >= lastMidnight - lengthOfDay && time < lastMidnight) {
            str = "Yesterday";
        } else if (time >= lastMidnight + lengthOfDay && time < lastMidnight + lengthOfDay * 2) {
            str = "Tomorrow";
        }
        return str;
    }

    static String formatDateNew(Date date, boolean useYesterdayTodayTomorrow, boolean includeDate,
            boolean includeTimeOfDay, boolean includeDayOfWeek, boolean useUSformat) {
        if (date.getTime() == 0) {
            return "No date"; //"NONE"
        }
        java.util.Calendar cal = java.util.Calendar.getInstance();
//        TimeZone tz = cal.getTimeZone();
//        cal.setTime(new Date(System.currentTimeMillis() - tz.getRawOffset()));
//        cal.setTime(new Date(date.getTime() - tz.getRawOffset()));
//        cal.setTime(new Date(date.getTime() ));
        cal.setTime(date);
        com.codename1.l10n.DateFormat dtfmt;
//        com.codename1.l10n.DateFormat timeFmt;
        String str;
        //SimpleDateFormat("EEE, yyyy-MM-dd KK:mm a"); //http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
        if (useUSformat) {
            if (useYesterdayTodayTomorrow) {
                str = formatAsYesterdayTodayTomorrow(date);
                if (includeTimeOfDay) {
//                    dtfmt = new SimpleDateFormat(" KK:mm a");
                    dtfmt = new SimpleDateFormat("KK:mm a");
//                    str += dtfmt.format(cal.getTime());
                    str = str + (str.length() != 0 ? " " : "") + dtfmt.format(cal.getTime()); //add space before time if not first string
                }
            } else {
                dtfmt = new SimpleDateFormat((includeDayOfWeek ? "EEE " : "") + (includeDate ? "MM/dd/yyyy" : "")
                        + (includeDate && includeTimeOfDay ? " " : "")
                        + (includeTimeOfDay ? "KK:mm a" : ""));
                str = dtfmt.format(cal.getTime());
            }
        } else {
            if (useYesterdayTodayTomorrow) {
                str = formatAsYesterdayTodayTomorrow(date);
                if (includeTimeOfDay) {
                    dtfmt = new SimpleDateFormat("HH:mm");
                    str = str + (str.length() != 0 ? " " : "") + dtfmt.format(cal.getTime()); //add space before time if not first string
                }
            } else {
                dtfmt = new SimpleDateFormat((includeDayOfWeek ? "EEE " : "")
                        + (includeDate ? "dd/MM/yyyy" : "")
                        + (includeDate && includeTimeOfDay ? " " : "")
                        + (includeTimeOfDay ? "HH:mm" : "")); //TODO if not using date, will add a space too much between EEE and HH:mm
                str = dtfmt.format(cal.getTime());
            }
        }
        return str;
    }

    static public String formatDateSmart(Date date) {
        long now = System.currentTimeMillis();
        long diff = date.getTime() - now;
//        long dateTime = date.getTime();
        //overdue
        if (diff < 0) {
            if (diff > getStartOfToday().getTime() - MyDate.DAY_IN_MILLISECONDS) {
//            return "Overdue";
                return "Yesterday";
            } else {
                diff = -diff; //else use same distance from today to determine formatting??
            }
        }
        //within today(before midnight/*next 24h*?/till 5 in the morning for night owls?!): "13h14" / "1h14am"
//        if (dateTime<=MyDate.getEndOfDay(new Date(dateTime+MyDate.DAY_IN_MILLISECONDS)).getTime())
        if (diff <= MyDate.DAY_IN_MILLISECONDS) {
            return new SimpleDateFormat("HH'h'mm").format(date);
        }
        //within next 7 days: "Mon13h"
        if (diff <= MyDate.DAY_IN_MILLISECONDS * 7) {
            return new SimpleDateFormat("EEEHH'h'").format(date);
        }
        //within next 365 days: "Jun11"
        if (diff <= MyDate.DAY_IN_MILLISECONDS * 365) {
            return new SimpleDateFormat("MMMdd").format(date);
        }
        //beyond 365 days: "Jun'18"

        return new SimpleDateFormat("MMM''yy").format(date);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private static String formatDateNewXX(MyDate date, MyDate referenceDate) { //, boolean useYesterdayTodayTomorrow) {
////        if (useYesterdayTodayTomorrow) {
//        MyDate todaysDate = new MyDate(date.getDay(), date.getMonth(), date.getYear()); //sets date to the default time of day, eg always midnight, in this difference
//        MyDate refDate = new MyDate(referenceDate.getDay(), referenceDate.getMonth(), referenceDate.getYear()); //ditto
//        int diffInDays = refDate.differenceInDays(todaysDate);
//        //TODO!!!! simply create one ref date for midnight and then compare by adding 24h inMillis to it�, or compare dates directly (eg. first and last day in this year at midnight)
//        if (date.getTime() == 0) {
//            return "No date"; //"NONE"
//        } else if (isBetween(diffInDays, -1, 0)) {
//            return "Yesterday";
//        } else if (isBetween(diffInDays, 0, 1)) {
//            return "Today";
//        } else if (isBetween(diffInDays, 1, 2)) {
//            return "Tomorrow";
//        } else if (isBetween(diffInDays, 2, 7)) { //next 7 days: use day names
//            return date.getDayName().substring(0, Settings.getInstance().getCharsInShortDates()); //2 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
////            } else if (diffInDays < getDaysInMonth(getMonth(), getYear())) { //within current month: use day name + day of month
//        } else if (isBetween(diffInDays, 7, date.getDaysInMonth(date.getMonth(), date.getYear()))) { //within current month: use day name + day of month
//            return date.getDayName().substring(0, Settings.getInstance().getCharsInShortDates()) + date.getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
//        } else {
//            return date.getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + date.getDay(); //within next year: day of month + first two letters of monht, e.g. 12Ju,NO: Jun12
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private String formatDate(MyDate referenceDate, boolean casualFormat) {
////        long interval = getTime() - today.getTime();
////        int diffInDays = date2.differenceInDays(todaysDate);
////        int diffInDays = (int) ((todaysDate.getTime() - refDate.getTime()) / DAY_IN_MILLISECONDS);
////        int diffInDays = todaysDate.differenceInDays(refDate);
//        if (casualFormat) {
//            MyDate todaysDate = new MyDate(getDay(), getMonth(), getYear()); //sets date to the default time of day, eg always midnight, in this difference
//            MyDate refDate = new MyDate(referenceDate.getDay(), referenceDate.getMonth(), referenceDate.getYear()); //ditto
//            int diffInDays = refDate.differenceInDays(todaysDate);
//            if (diffInDays < -1) {
//                return "OVERDUE";
//            }
//            if (diffInDays < 0) {
//                return "yesterday";
//            } else if (diffInDays < 1) {
//                return "today";
//            } else if (diffInDays < 2) {
//                return "tomorrow";
//            } else if (diffInDays < 7) { //this week: use day names
//                return getDayName().substring(0, Settings.getInstance().getCharsInShortDates()); //2 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
//            } else if (diffInDays < getDaysInMonth(getMonth(), getYear())) { //within current month: use day name + day of month
//                return getDayName().substring(0, Settings.getInstance().getCharsInShortDates()) + getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
//            } else if (diffInDays < 365) { //within this year:
////                return getDay() + getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()); //within next year: day of month + first two letters of monht, e.g. 12Ju,
//                return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + getDay(); //within next year: day of month + first two letters of monht, e.g. 12Ju,NO: Jun12
//            } else if (diffInDays < 730) {
//                return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//            } else {
//                return "" + getYear(); //beyond 2 years: year, e.g. 2013, 2017
//            }
//        } else {
//            Settings.getInstance().getDefaultDateAndTimeFormat();
//            //TODO!!!!: use the same date formatter in DateField5 and here to ensure consistency!!!!
////            return getMonthName().substring(0, Settings.getInstance().getCharsInShortMonths()) + "'" + (("" + getYear()).substring(2, 4)); //within year after this: month + ' + last two numbers of year, e.g. Jun'13
//            return getShortDayName() + " " + getDay() + "/" + getMonth() + "/" + getYear()
//                    //                + " " + getHour24() + ":" + (getMinute()>10?""+getMinute():"0"+getMinute()) + ":" + (getSeconds()>10?""+getSeconds():"0"+getSeconds()) + " [" + getMilliSeconds() + "] =" + time;
//                    //                + " " + getHour24() + ":" + (getMinute() > 10 ? "" + getMinute() : "0" + getMinute()) + ":" + (getSeconds() > 10 ? "" + getSeconds() : "0" + getSeconds()) + ":" + getMilliSeconds();
//                    + " " + getHour24() + ":" + format2(getMinute());
////            return toString();
//        }
////        if (isToday(now)) {
////            return "today";
////        } else if (isTomorrow(now) {
////            return "tomorrow";
////        } else if (isThisWeek(now)) {
////            return getDayName().substring(0, 1); //2 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
////        } else if (isThisMonth(now)) {
////            return getDayName().substring(0, 1) + getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
////        } else if (isThisYear(now)) {
////            return getDay() + getMonthName().substring(0, 1); //day of month + first two letters of monht, e.g. 12Ju,
////        } else {
////            return getMonthName().substring(0, 1) + "'" + ("" + getYear()).substring(2, 3); //first two letters of month + year, e.g. Jun'11, Sep'12
////        }
////        if (interval < 0) {
////            return "OVERDUE";
////        } else if (interval < ONE_DAY) {
////            return "today";
////        } else if (interval < TWO_DAYS) {
////            return "tomorrow";
////        } else if (interval < SEVEN_DAYS) {
////            return getDayName().substring(0, 1); //2 first letters of weekday, e.g. Mo, Tu, We, Th, Fr, Sa, Su
////        } else if (interval < THIRTY_DAYS) {
////            return getDayName().substring(0, 1) + getDay(); //2 first letters of weekday+day of month, e.g. Mo7, Tu23, We31, Th1, Fr, Sa, Su
////        } else if (interval < ONE_YEAR) {
////            return getDay() + getMonthName().substring(0, 1); //day of month + first two letters of monht, e.g. 12Ju,
////        } else {
////            return getMonthName().substring(0, 1) + "'" + ("" + getYear()).substring(2, 3); //first two letters of month + year, e.g. Jun'11, Sep'12
////        }
////            return "";
//    }
//</editor-fold>
    static String formatTimeOfDay(long hoursMinutesInMilliSeconds) {
        return MyDate.formatTimeOfDay(hoursMinutesInMilliSeconds, false, true, false);
    }

    static String formatTimeOfDay(long hoursMinutesInMilliSeconds, boolean showSeconds) {
        return MyDate.formatTimeOfDay(hoursMinutesInMilliSeconds, true, true, false);
    }

//    static String formatTime(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean roundUpMinutes, boolean showLeadingZeroForHour) {
    static String formatTimeOfDay(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean showLeadingZeroForHour, boolean useUSFormat) {
        return MyDate.formatTimeOfDay(hoursMinutesInMilliSeconds, showSeconds, showLeadingZeroForHour, useUSFormat, false);
    }

    static String formatTimeOfDay(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean showLeadingZeroForHour, boolean useUSFormat, boolean noTimeZoneCorrection) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
//        cal.setTime(new Date(System.currentTimeMillis() - tz.getRawOffset()));
        cal.setTime(new Date(hoursMinutesInMilliSeconds - (noTimeZoneCorrection ? 0 : tz.getRawOffset())));
        DateFormat dtfmt;
        if (useUSFormat) {
            dtfmt = new SimpleDateFormat((showLeadingZeroForHour ? "KK" : "K") + (showSeconds ? "mm:ss a" : "mm a"));
        } else {
            dtfmt = new SimpleDateFormat((showLeadingZeroForHour ? "HH" : "H") + ":mm" + (showSeconds ? ":ss" : ""));
        }
        return dtfmt.format(cal.getTime());
    }

    static String formatTimeDuration(long hoursMinutesInMilliSeconds) {
        return formatTimeDuration(hoursMinutesInMilliSeconds, false);
    }

    static String formatTimeDuration(long hoursMinutesInMilliSeconds, boolean showSeconds) {
        return formatTimeDuration(hoursMinutesInMilliSeconds, showSeconds, false);
    }

    static String formatTimeDuration(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean showLeadingZeroForHour) {
        return formatTimeDuration(hoursMinutesInMilliSeconds, showSeconds, false, showLeadingZeroForHour);
    }

    private static String formatTimeDuration(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean roundUpMinutes, boolean showLeadingZeroForHour) {
        return formatTimeDuration(hoursMinutesInMilliSeconds, showSeconds, roundUpMinutes, showLeadingZeroForHour, true);
    }

    private static String formatTimeDuration(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean roundUpMinutes, boolean showLeadingZeroForHour, boolean showHBtwHoursAndMinutes) {
        return formatTimeDuration(hoursMinutesInMilliSeconds, showSeconds, roundUpMinutes, showLeadingZeroForHour, showHBtwHoursAndMinutes, true);
    }

    /**
     * format duration as e.g.
     *
     * @param hoursMinutesInMilliSeconds
     * @param showSeconds
     * @param roundUpMinutes
     * @param showLeadingZeroForHour
     * @param showHBtwHoursAndMinutes
     * @param dontShowZeroHours
     * @return
     */
    private static String formatTimeDuration(long hoursMinutesInMilliSeconds, boolean showSeconds, boolean roundUpMinutes, boolean showLeadingZeroForHour, boolean showHBtwHoursAndMinutes, boolean dontShowZeroHours) {
//        boolean SHOW_SECONDS = false;
//        boolean SHOW_LEADING_ZERO_FOR_HOUR = true;
        String s; // = "";
        int hours = (int) hoursMinutesInMilliSeconds / MyDate.HOUR_IN_MILISECONDS;//3600000;
        long restAfterHours = (int) hoursMinutesInMilliSeconds % MyDate.HOUR_IN_MILISECONDS;
//        if (hours > 0) {
        int minutes = (int) restAfterHours / MyDate.MINUTE_IN_MILLISECONDS; //60000;
//        boolean showLeadingZeroForMinutes = minutes < 10 && !(hours == 0 && dontShowZeroHours) ; // hours != 0 || !dontShowZeroHours
//            return "" + hours + "h" + (minutes < 10 ? "0" + minutes : "" + minutes);
//            s = hours + ":" + (minutes != 0 ? ((minutes < 10 ? "0" + minutes : "" + minutes)) : "");
        s = (dontShowZeroHours && hours == 0 ? "" : (showLeadingZeroForHour && hours < 10 ? "0" : "") + hours + (showHBtwHoursAndMinutes ? "h" : ":"))
                //                + (minutes < 10 && (hours != 0 || !dontShowZeroHours) ? "0" + minutes : "" + minutes + (dontShowZeroHours ? "'" : "")); //don't show '0' for 3 min, e.g. "3m" instead of "0h03"
                //https://english.stackexchange.com/questions/114205/english-notation-for-hour-minutes-and-seconds says: minutes *can* be 3', but 3m is more common
                + (minutes < 10 && (hours != 0 || !dontShowZeroHours) ? "0" + minutes : "" + minutes
                        + (dontShowZeroHours && !showSeconds ? "m" : "")); //don't show '0' for 3 min, e.g. "3m" instead of "0h03"
        int seconds = (int) (restAfterHours % MyDate.MINUTE_IN_MILLISECONDS) / MyDate.SECOND_IN_MILLISECONDS; //1000;
        if (roundUpMinutes && seconds >= 30) {
            minutes++;
        }
        if (showSeconds) {
            s += ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else {
//            int minutes = (int) restAfterHours / MyDate.MINUTE_IN_MILLISECONDS; //60000;
//            int restAfterMinutes = (int) restAfterHours % MyDate.MINUTE_IN_MILLISECONDS; //60000;
////            return (minutes < 10 ? "0" + minutes : "" + minutes) + ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
////            s = (minutes < 10 ? "0" + minutes : "" + minutes) + ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
//            if (showSeconds) {
//                int seconds = (int) restAfterMinutes / MyDate.SECOND_IN_MILLISECONDS; //1000;
//                s = ((SHOW_LEADING_ZERO && minutes < 10) ? "0" + minutes : "" + minutes) + ":" + (seconds < 10 ? "0" + seconds : "" + seconds);
//            } else {
////                s = (minutes < 10 ? "0" + minutes : "" + minutes) + "m";
//                s = minutes + "m";
//            }
//        }
//</editor-fold>
        return s;
    }

    public static Date makeDate(int secondsFromNow) {
        Date date = new Date();
        date.setTime(date.getTime() + secondsFromNow * SECOND_IN_MILLISECONDS);
        return date;
    }

    private static String formatDateL10NShort(long timeInMilliSeconds, boolean dateOnlyNoTime) {
        return formatDateL10NShort(timeInMilliSeconds); //TODO!!!! write code to return date without time
    }

    private static String formatDateL10NShort(long timeInMilliSeconds) {
//        return L10NManager.getInstance().formatDateShortStyle(new Date(timeInMilliSeconds));
        return formatDateL10NShort(new Date(timeInMilliSeconds));
    }

    private static String formatDateL10NShort(Date date) {
        return L10NManager.getInstance().formatDateShortStyle(date);
    }

    public static String addNthPostFix(String str) {
        char lastChiffer = str.charAt(str.length() - 1);
        if (Settings.getInstance().getLocale().equals("en")) {
            if (str.equals("11") || str.equals("12") || str.equals("13")) {
                str += "th";
            } else {
                str += (lastChiffer == '1' ? "st" : (lastChiffer == '2' ? "nd" : (lastChiffer == '3' ? "rd" : "th"))); //TODO!!! (internationalize)
            }
        }
        return str;
    }

    /**
     * returns the time now (System.currentTimeMillis()). Provide to make it
     * possible to override this function, e.g. for test purposes.
     */
    static long getNow() {
        return System.currentTimeMillis();
    }

    /**
     * returns today's date with hour/minute/second/millesecond set to zero
     */
//    static long getNowDateOnly(Date date) {
//        MyDate today = new MyDate();
//        today = new MyDate(today.getDay(date), today.getMonth(date), today.getYear(date));
//        return today.getTime();
//    }
    /**
     * returns today's date with hour/minute/second/millesecond set to zero
     */
//    static long getNowTimeOnly() {
//        MyDate today = new MyDate();
//        today = new MyDate(today.getHour24(), today.getMinute());
//        return today.getTime();
//    }
    /**
     * returns time adjusted to 'midnight' to today's date with
     * hour/minute/second/millisecond set to zero
     */
    static Date setDateToMidnight(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 1); //set hour to 1 to avoid pbs with daylight saving changes
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 1); //ensure it's after midnight //TODO!!!! is 0 the right value??
        return cal.getTime();
    }

    /**
     * returns time adjusted to 'midnight' to today's date with
     * hour/minute/second/millesecond set to zero
     */
    static Date setDateToDefaultTimeOfDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 9); //set 9 in the morning
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * returns time adjusted to end of the day (1 millisecond before midnight).
     * Use this when actually
     */
    static Date setEndOfDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 23); //set hour to 1 to avoid pbs with daylight saving changes
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999); //ensure it's after midnight //TODO!!!! is 0 the right value??
        return cal.getTime();
    }

    /**
     * returns the date of time adjusted to the date of newTime, leaving the
     * time of day of 'time' unchanged
     */
    static Date setDateToNewDateKeepTime(Date time, Date newTime) {
//        Date today = new Date();
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(newTime);
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.DATE, cal2.get(Calendar.DATE));
        cal.set(Calendar.MONTH, cal2.get(Calendar.MONTH));
        cal.set(Calendar.YEAR, cal2.get(Calendar.YEAR));
        return cal.getTime();
    }

    static Date setDateToTodayKeepTime(Date time) {
        return setDateToNewDateKeepTime(time, new Date());
    }

    /**
     * returns the first time (when the day starts) of the date in time
     *
     * @param time
     * @return
     */
    static Date getStartOfDay(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hour to 1 to avoid pbs with daylight saving changes
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0); //ensure it's after midnight //TODO!!!! is 0 the right value??
        return cal.getTime();
    }

    /**
     * set time to start of minute. E.g. 22:31:47 is rounded down to 22:31:00.
     * Used eg to ensure that a snooze alarm starts right at the minute seen by
     * user and not 45s later.
     *
     * @param time
     * @return
     */
    static Date getStartOfMinute(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0); //ensure it's after midnight //TODO!!!! is 0 the right value??
        return cal.getTime();
    }

    /**
     * returns the time at which Today started (midnight)
     *
     * @return
     */
    static Date getStartOfToday() {
        return getStartOfDay(new Date());
    }

    static boolean isToday(Date date) {
        return date.getTime() >= getStartOfToday().getTime() && date.getTime() < getStartOfToday().getTime() + DAY_IN_MILLISECONDS;
    }

    /**
     * returns true if date1 is the same day (midnight-midnight) as date2.
     * Returns false if either of the dates is null.
     *
     * @param date1
     * @param date2
     * @return
     */
    static boolean isSameDate(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.getTime() >= getStartOfDay(date2).getTime() && date1.getTime() <= getEndOfDay(date2).getTime();
    }

    static boolean isSameWeekAndYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date1);
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(date2);
//        return cal.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR) && cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR); //NB!! Calendar.WEEK_OF_YEAR not implemented in CN1
        SimpleDateFormat dtfmt = new SimpleDateFormat("w yyyy");
        return dtfmt.format(date1).equals(dtfmt.format(date2));
    }

    static String getWeekAndYear(Date date1) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date1);
//        return "Week " + cal.get(Calendar.WEEK_OF_YEAR) + " " + cal.get(Calendar.YEAR);
        SimpleDateFormat dtfmt = new SimpleDateFormat("w yyyy"); //should give "Week 51 2017"
        return "Week " + dtfmt.format(date1);

    }

    static String getMonthAndYear(Date date1) {
        //SimpleDateFormat("EEE, yyyy-MM-dd KK:mm a"); //http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
//        SimpleDateFormat dtfmt = new SimpleDateFormat("M yyyy"); //gives "7 2017"
        SimpleDateFormat dtfmt = new SimpleDateFormat("MMMM yyyy"); //should give "July 2017"
        return dtfmt.format(date1);
    }

    static boolean isSameMonthAndYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) && cal.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    /**
     * returns the last time (when the day ends) of the date in time
     *
     * @param time
     * @return
     */
    static Date getEndOfDay(Date time) {
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(time);
//        cal.set(Calendar.HOUR_OF_DAY, 23); //set hour to 1 to avoid pbs with daylight saving changes
//        cal.set(Calendar.MINUTE, 59);
//        cal.set(Calendar.SECOND, 59);
//        cal.set(Calendar.MILLISECOND, 999); //ensure it's after midnight //TODO!!!! is 0 the right value??
//        return cal.getTime();
        return new Date(getStartOfDay(time).getTime() + DAY_IN_MILLISECONDS - 1);
    }
// <editor-fold defaultstate="collapsed" desc="comment">
//        / ================================================== ==
//Method: Get the desired Date format for the date
//Developed By: Sandip Waghole [29-Jan-2010]
//================================================== == /
//    public String xxxgetWeekNo(String strDate) {
//// input Date Format : M/dd/yyyy
//        int weekNo = 0, i = 0;
//        String strWeekNo = null;
//
//        int noOfDaysInTheYear = 365;
//
//        int WEEK_STARTS_ON = 1; // Define the day on which week starts Sunday/Monday 1:Sunday 2:Monday
//        int firstDayNoInFirstWeekOfPresentYear = 0; // Inititalize teh day on which week is starting in present year
//        int firstDayOfPresentYear = 0; // Inititlize the 1st day of the present year whether Sunday/Monday/.....
//        int[] monthDaysArray = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}; // Define array of the days as per months
//
//        int todaysDayNoInPresentYear = 0;
//        int daysLateByFirstWeekStartedAfterYearStarted = 0;
//
//        int intTemp = 0;
//
////strDate="08/24/2000"; // For test purpose
//        StringTokenizer strDateTok = new StringTokenizer(strDate, "/ ");
//
//        int month = Integer.parseInt(strDateTok.nextToken());
//        int day = Integer.parseInt(strDateTok.nextToken());
//        int year = Integer.parseInt(strDateTok.nextToken());
//
//        GregorianCalendar cal = new GregorianCalendar();
//
//// Check if present year is leap year
//        boolean boolIsLeapYear = cal.isLeapYear(year);
//
//// If it is boolean year then add 1 to total days in the year & add one more day to february
//        if (boolIsLeapYear) {
//            noOfDaysInTheYear = noOfDaysInTheYear + 1;
//            monthDaysArray[1] = monthDaysArray[1] + 1;
//        }
//
//// Find the 1st day of this year
//        Calendar calObj = new GregorianCalendar(year, Calendar.JANUARY, 1);
//        firstDayOfPresentYear = calObj.get(Calendar.DAY_OF_WEEK);
//
//        int intRemoveNoOfDaysFromWeek = 0;
//
//// # Find the day no of prsent day
//
//        for (i = 0; i < month; i ++) // get no of days till present year
//                 {
//            intTemp = intTemp+ monthDaysArray[i];
//        }
//
//        todaysDayNoInPresentYear = intTemp - (monthDaysArray[month - 1] - day);
//
//
//        if (firstDayOfPresentYear == 6 || firstDayOfPresentYear == 7) // If first Day is Friday or Saturday then it is week
//        {
//// Identify the the day no on which 1st week of present year is starting
//            firstDayNoInFirstWeekOfPresentYear = 7 - firstDayOfPresentYear; // WEEK_STARTS_ON  1;
//
//// Find delay in the 1st week start after r=the year start
//daysLateByFirstWeekStartedAfterYearStarted = firstDayNoInFirstWeekOfPresentYear - 1;
//
//// Now week is starting from Sunday
//            weekNo = (Integer) ((todaysDayNoInPresentYear - daysLateByFirstWeekStartedAfterYearStarted) / 7);
//
//// Find the day no of today
//            intTemp = (todaysDayNoInPresentYear - daysLateByFirstWeekStartedAfterYearStarted) % 7;
//
//            if (intTemp > 0) {
//                weekNo = weekNo + 1;
//            } else {
//                weekNo = weekNo;
//            }
//
//        } else {
//// 1st week is starting on 1st Of January
//            firstDayNoInFirstWeekOfPresentYear = firstDayOfPrese ntYear;
//
//// Remove no. of days from the 1st week as week is starting from odd Sunday/Monday/Tuesday/Wednesday/Thursday
//            intRemoveNoOfDaysFromWeek = 7 - firstDayOfPresentYear               1; // 1 added as include start day also
//
//// So one week will be added in no. of weeks
//
//weekNo = (Integer)((todaysDayNoInPresentYear-intRemoveNoOfDaysFromWeek)/7);
//
//// Find the day no of today
//            intTemp = (todaysDayNoInPresentYear - intRemoveNoOfDaysFromWeek) % 7;
//
//            weekNo = weekNo + 1; // As 1st weeks days are reduced from the todays day no in the year
//
//            if (intTemp > 0) {
//                weekNo = weekNo + 1;
//            } else {
//                weekNo = weekNo;
//            }
//
//// Remove the no. of days from the week 1
//        }
//        strWeekNo = Integer.toString(weekNo);
//
//        return strWeekNo;
//
//    }
//// Any issues please mail on sandip.waghole@gmail.com// </editor-fold>
}
