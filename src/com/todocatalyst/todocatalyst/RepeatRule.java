/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/*
 *
 *
 * Portions Copyright  2000-2007 Sun Microsystems, Inc. All Rights
 * Reserved.  Use is subject to license terms.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */
 /*
 * Copyright (C) 2002-2003 PalmSource, Inc.  All Rights Reserved.
 */
//package javax.microedition.pim;
//import com.sun.kvem.midp.pim.PIMHandler;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;
//import javax.microedition.pim.FieldEmptyException;
//import javax.microedition.pim.PIMItem;

/**
 * This class is defined by the JSR-75 specification
 * <em>PDA Optional Packages for the J2ME™ Platform</em>
 * Source taget fra: http://jcs.mobile-utopia.com/jcs/27067_RepeatRule.java
 */
// JAVADOC COMMENT ELIDED
public class RepeatRule {

    private static class PIMItem {
//        source taget fra: http://jcs.mobile-utopia.com/jcs/5193_PIMItem.java
        // JAVADOC COMMENT ELIDED
//    public static final int BINARY = 0;
        // JAVADOC COMMENT ELIDED
//    public static final int BOOLEAN = 1;
        // JAVADOC COMMENT ELIDED

        public static final int DATE = 2;
        // JAVADOC COMMENT ELIDED
        public static final int INT = 3;
        // JAVADOC COMMENT ELIDED
//    public static final int STRING = 4;
        // JAVADOC COMMENT ELIDED
//    public static final int STRING_ARRAY = 5;
        // JAVADOC COMMENT ELIDED
//    public static final int ATTR_NONE = 0;
        // JAVADOC COMMENT ELIDED
//    public static final int EXTENDED_FIELD_MIN_VALUE = 0x1000000;
        // JAVADOC COMMENT ELIDED
//    public static final int EXTENDED_ATTRIBUTE_MIN_VALUE = 0x1000000;
        // used here in RepeatRule:
//        private static int INT;
//        private static int DATE;

        public PIMItem() {
        }
    }

    public String toString() {
        String s = "";
        String sep = "";
        int[] fields = getFields();
        long longVal = 0;
        int intVal = 0;
        for (int i = 0, size = fields.length; i < size; i++) {
            s += sep;
            sep = " "; //" | ";
            switch (fields[i]) {
                case RepeatRule.FREQUENCY:
                case RepeatRule.DAY_IN_MONTH:
                case RepeatRule.DAY_IN_WEEK:
                case RepeatRule.DAY_IN_YEAR:
                case RepeatRule.MONTH_IN_YEAR:
                case RepeatRule.WEEK_IN_MONTH:
                case RepeatRule.WEEKDAYS_IN_MONTH:
                case RepeatRule.COUNT:
                case RepeatRule.INTERVAL:
                    intVal = getInt(fields[i]);
                    break;
                case RepeatRule.END:
                    longVal = getDate(fields[i]);
                    break;
            }
            switch (fields[i]) {
                case RepeatRule.FREQUENCY:
                    s += "FREQUENCY=" + RepeatRuleParseObject.getFreqText(intVal, false, false, true);
                    break;
                case RepeatRule.DAY_IN_MONTH:
                    s += "DAY_IN_MONTH=" + intVal;
                    break;
                case RepeatRule.DAY_IN_WEEK:
                    s += "DAY_IN_WEEK=" + RepeatRuleParseObject.getDaysInWeekAsString(intVal, "+");
                    break;
                case RepeatRule.DAY_IN_YEAR:
                    s += "DAY_IN_YEAR=" + intVal;
                    break;
                case RepeatRule.MONTH_IN_YEAR:
                    s += "MONTH_IN_YEAR=" + RepeatRuleParseObject.getMonthsInYearAsString(intVal);
                    break;
                case RepeatRule.WEEK_IN_MONTH:
                    s += "WEEK_IN_MONTH=" + RepeatRuleParseObject.getWeeksInMonthAsString(intVal);
                    break;
                case RepeatRule.WEEKDAYS_IN_MONTH:
                    s += "WEEKDAYS_IN_MONTH=" + RepeatRuleParseObject.getWeeksInMonthAsString(intVal);
                    break;
                case RepeatRule.COUNT:
                    s += "COUNT=" + intVal;
                    break;
                case RepeatRule.END:
//                    s += "END=" + new MyDate(longVal);
                    s += "END=" + new Date(longVal);
                    break;
                case RepeatRule.INTERVAL:
                    s += "INTERVAL=" + intVal;
                    break;
                default:
                    s += "**UNKNOWN FIELD=" + fields[i] + "**";
            }
        }
        return s;
    }
//#mdebug

//    public String xxxtoString() {
//        String s = "";
//        String sep = "";
//        int[] fields = getFields();
//        for (int i = 0, size = fields.length; i < size; i++) {
//            s += sep;
//            sep = "|";
//            switch (fields[i]) {
//                case FREQUENCY:
//                    s += "FREQUENCY";
//                    break;
//                case DAY_IN_MONTH:
//                    s += "DAY_IN_MONTH";
//                    break;
//                case DAY_IN_WEEK:
//                    s += "DAY_IN_WEEK";
//                    break;
//                case DAY_IN_YEAR:
//                    s += "DAY_IN_YEAR";
//                    break;
//                case MONTH_IN_YEAR:
//                    s += "MONTH_IN_YEAR";
//                    break;
//                case WEEK_IN_MONTH:
//                    s += "WEEK_IN_MONTH";
//                    break;
//                case COUNT:
//                    s += "COUNT";
//                    break;
//                case END:
//                    s += "END";
//                    break;
//                case INTERVAL:
//                    s += "INTERVAL";
//                    break;
//                default:
//                    s += "**UNKNOWN FIELD=" + fields[i] + "**";
//            }
//            s += "=";
//            if (fields[i] == END) {
////                s += "" + new MyDate(getDate(fields[i])).formatDate();
//                s += "" + MyDate.formatDateNew(new Date(getDate(fields[i])));
//            } else if (fields[i] == COUNT || fields[i] == INTERVAL || fields[i] == DAY_IN_YEAR) {
//                s += getInt(fields[i]);
//            } else if (fields[i] == FREQUENCY) {
//                switch (getInt(fields[i])) {
//                    case DAILY:
//                        s += "DAILY";
//                        break;
//                    case WEEKLY:
//                        s += "WEEKLY";
//                        break;
//                    case MONTHLY:
//                        s += "MONTHLY";
//                        break;
//                    case YEARLY:
//                        s += "YEARLY";
//                        break;
//                    default:
//                        s += "**UNKNOWN VALUE=" + getInt(fields[i]) + "**";
//                }
//            } else {
//                switch (getInt(fields[i])) {
//                    case FIRST:
//                        s += "FIRST";
//                        break;
//                    case SECOND:
//                        s += "SECOND";
//                        break;
//                    case THIRD:
//                        s += "THIRD";
//                        break;
//                    case FOURTH:
//                        s += "FOURTH";
//                        break;
//                    case FIFTH:
//                        s += "FIFTH";
//                        break;
//                    case LAST:
//                        s += "LAST";
//                        break;
//                    case SECONDLAST:
//                        s += "SECONDLAST";
//                        break;
//                    case THIRDLAST:
//                        s += "THIRDLAST";
//                        break;
//                    case FOURTHLAST:
//                        s += "FOURTHLAST";
//                        break;
//                    case FIFTHLAST:
//                        s += "FIFTHLAST";
//                        break;
//                    case SATURDAY:
//                        s += "SATURDAY";
//                        break;
//                    case FRIDAY:
//                        s += "FRIDAY";
//                        break;
//                    case THURSDAY:
//                        s += "THURSDAY";
//                        break;
//                    case WEDNESDAY:
//                        s += "WEDNESDAY";
//                        break;
//                    case TUESDAY:
//                        s += "TUESDAY";
//                        break;
//                    case MONDAY:
//                        s += "MONDAY";
//                        break;
//                    case SUNDAY:
//                        s += "SUNDAY";
//                        break;
//                    case JANUARY:
//                        s += "JANUARY";
//                        break;
//                    case FEBRUARY:
//                        s += "FEBRUARY";
//                        break;
//                    case MARCH:
//                        s += "MARCH";
//                        break;
//                    case APRIL:
//                        s += "APRIL";
//                        break;
//                    case MAY:
//                        s += "MAY";
//                        break;
//                    case JUNE:
//                        s += "JUNE";
//                        break;
//                    case JULY:
//                        s += "JULY";
//                        break;
//                    case AUGUST:
//                        s += "AUGUST";
//                        break;
//                    case SEPTEMBER:
//                        s += "SEPTEMBER";
//                        break;
//                    case OCTOBER:
//                        s += "OCTOBER";
//                        break;
//                    case NOVEMBER:
//                        s += "NOVEMBER";
//                        break;
//                    case DECEMBER:
//                        s += "DECEMBER";
//                        break;
//                    default:
//                        s += "**UNKNOWN VALUE=" + getInt(fields[i]) + "**";
//                }
//            }
//        }
//        return s;
//    }
//#enddebug
    //source taget fra: http://jcs.mobile-utopia.com/jcs/2038_FieldEmptyException.java
    // JAVADOC COMMENT ELIDED
    public class FieldEmptyException extends java.lang.RuntimeException {

        /**
         * Field responsible for the exception.
         */
        private int offending_field;

        // JAVADOC COMMENT ELIDED
        public FieldEmptyException() {
            super();
            offending_field = 0;
        }

        // JAVADOC COMMENT ELIDED
        public FieldEmptyException(String detailMessage) {
            super(detailMessage);
            offending_field = 0;
        }

        // JAVADOC COMMENT ELIDED
        public FieldEmptyException(String detailMessage, int field) {
            super(detailMessage);
            offending_field = field;
        }

        // JAVADOC COMMENT ELIDED
        public int getField() {
            return offending_field;
        }
    }
    /**
     * Fields in the rule.
     */
    private Hashtable fields = new Hashtable();

    /**
     * Exceptions caused by the rule.
     */
//    private Vector exceptions = new Vector();
    // JAVADOC COMMENT ELIDED
    public RepeatRule() {
    }
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the frequency of the event. This value can be YEARLY, MONTHLY,
     * WEEKLY, DAILY.
     */
    public static final int FREQUENCY = 0;
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the day in the month that this event will occur. This index is
     * 1-based. In other words, the value 15 represents the 15th day of the
     * month.
     */
    public static final int DAY_IN_MONTH = 1;
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the day in the week that this event will occur. This index is
     * 1-based. In other words, the value 2 represents the 2nd day of the week.
     * Values can be OR'd together to indicate a multple day event.
     */
    public static final int DAY_IN_WEEK = 2;
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the day in the year that this event will occur. This index is
     * 1-based. In other words, the value 200 represents the 200th day of the
     * month.
     */
    public static final int DAY_IN_YEAR = 4;
    /**
     * Represents the month in the year that this event will occur. This index
     * is 1-based. In other words, the value 6 represents the 6th month of the
     * year. Values can be OR'd together to set multple months.
     */
    public static final int MONTH_IN_YEAR = 8;
    /**
     * Represents the week in the month that this event will occur. This index
     * is 1-based. In other words, the value 2 represents the 2nd week of the
     * month. Values can be OR'd together to set multple weeks.
     */
    public static final int WEEK_IN_MONTH = 16;
    /**
     * THJ: Represents the weekdays of the weeks of the month that this event
     * will occur. Represents the day in the week that this event will occur.
     * This index is 1-based. In other words, the value 2 represents the 2nd day
     * of the week. Values can be OR'd together to indicate a multple day event.
     */
    public static final int WEEKDAYS_IN_MONTH = 256;

    public static final int DAYS_IN_MONTH = 512; //THJ to select several days in month (e.g. 2nd, 7th and 29th), like iOS Reminders!

    // JAVADOC COMMENT ELIDED
    /**
     * Represents the number of times the event will occur. This value is
     * determined using Event.START and RepeatRule.END
     */
    public static final int COUNT = 32;
    /**
     * Represents the end date of an event.
     */
    public static final int END = 64;
    /**
     * The interval represents the amount of time between events, or the number
     * of times the frequency repeats between events. For example, to schedule
     * an event to occur every other day, the FREQUENCY is DAILY and the
     * INTERVAL is 2, thus every 2nd DAILY occurence is considered an occurence
     * of the event.
     */
    public static final int INTERVAL = 128;

    // JAVADOC COMMENT ELIDED
    /**
     * Indicates that the frequency of the event occurs daily.
     */
    public static final int DAILY = 0x10; //16
    // JAVADOC COMMENT ELIDED
    /**
     * Indicates that the frequency of the event occurs weekly.
     */
    public static final int WEEKLY = 0x11; //17
    // JAVADOC COMMENT ELIDED
    /**
     * Indicates that the frequency of the event occurs monthly.
     */
    public static final int MONTHLY = 0x12; //18
    // JAVADOC COMMENT ELIDED
    /**
     * Indicates that the frequency of the event occurs yearly.
     *
     */
    public static final int YEARLY = 0x13; //19
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the first week of the month.
     *
     */
    public static final int FIRST = 0x1;
    // JAVADOC COMMENT ELIDED
    public static final int SECOND = 0x2;
    // JAVADOC COMMENT ELIDED
    public static final int THIRD = 0x4;
    // JAVADOC COMMENT ELIDED
    public static final int FOURTH = 0x8;
    // JAVADOC COMMENT ELIDED
    public static final int FIFTH = 0x10;
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the last week of the month.
     */
    public static final int LAST = 0x20;
    // JAVADOC COMMENT ELIDED
    /**
     * Represents the second last week of the month.
     */
    public static final int SECONDLAST = 0x40;
    // JAVADOC COMMENT ELIDED
    public static final int THIRDLAST = 0x80;
    // JAVADOC COMMENT ELIDED
    public static final int FOURTHLAST = 0x100;
    // JAVADOC COMMENT ELIDED
    public static final int FIFTHLAST = 0x200;
    // JAVADOC COMMENT ELIDED
    public static final int SATURDAY = 0x400;
    // JAVADOC COMMENT ELIDED
    public static final int FRIDAY = 0x800;
    // JAVADOC COMMENT ELIDED
    public static final int THURSDAY = 0x1000;
    // JAVADOC COMMENT ELIDED
    public static final int WEDNESDAY = 0x2000;
    // JAVADOC COMMENT ELIDED
    public static final int TUESDAY = 0x4000;
    // JAVADOC COMMENT ELIDED
    public static final int MONDAY = 0x8000;
    // JAVADOC COMMENT ELIDED
    public static final int SUNDAY = 0x10000;
    // JAVADOC COMMENT ELIDED
    public static final int JANUARY = 0x20000;
    // JAVADOC COMMENT ELIDED
    public static final int FEBRUARY = 0x40000;
    // JAVADOC COMMENT ELIDED
    public static final int MARCH = 0x80000;
    // JAVADOC COMMENT ELIDED
    public static final int APRIL = 0x100000;
    // JAVADOC COMMENT ELIDED
    public static final int MAY = 0x200000;
    // JAVADOC COMMENT ELIDED
    public static final int JUNE = 0x400000;
    // JAVADOC COMMENT ELIDED
    public static final int JULY = 0x800000;
    // JAVADOC COMMENT ELIDED
    public static final int AUGUST = 0x1000000;
    // JAVADOC COMMENT ELIDED
    public static final int SEPTEMBER = 0x2000000;
    // JAVADOC COMMENT ELIDED
    public static final int OCTOBER = 0x4000000;
    // JAVADOC COMMENT ELIDED
    public static final int NOVEMBER = 0x8000000;
    // JAVADOC COMMENT ELIDED
    public static final int DECEMBER = 0x10000000;
    /**
     * THJ: to get the first working day of a month
     */
    public static final int WEEKDAYS = 0x20000000;
    /**
     * THJ: to get the first weekend day of a month
     */
    public static final int WEEKENDS = 0x40000000;
    /**
     * Months of the year.
     */
    private static final int[] MONTHS = {
        JANUARY, FEBRUARY, MARCH, APRIL,
        MAY, JUNE, JULY, AUGUST,
        SEPTEMBER, OCTOBER, NOVEMBER, DECEMBER
    };
    /**
     * DAY_INCREMENT = 86400000l.
     */
//    private static final long DAY_INCREMENT = 86400000l;
    private static final long DAY_INCREMENT = 86400000L;
    /*THHJ: seems correct value is 864000000 (unless the extra 1 serves some purpose in the calculations?!) */

    /**
     * DAY_IN_WEEK_MASK = 0x1fc00l.
     */
//    private static final long DAY_IN_WEEK_MASK = 0x1fc00l;
    private static final long DAY_IN_WEEK_MASK = SUNDAY | MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY | SATURDAY | WEEKDAYS | WEEKENDS;
    /*THHJ: apparently an error in the mask (the '1' in '001'). Added WEEKDAYS and WEEKENDS  */

    /**
     * WEEK_IN_MONTH_MASK = 0x3ffl.
     */
//    private static final long WEEK_IN_MONTH_MASK = FIRST|SECOND|THIRD|FOURTH|FIFTH|LAST|SECONDLAST|THIRDLAST|FOURTHLAST|FIFTHLAST; //THHJ
    private static final long WEEK_IN_MONTH_MASK = 0x3ffl;
    /**
     * MONTH_IN_YEAR_MASK = 0x1ffe0000.
     */
    private static final long MONTH_IN_YEAR_MASK = 0x1ffe0000;
    /**
     * Days of the week.
     */
    private static final int[] DAYS = {
        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY /*THHJ: SATURDAY was left out of list:*/, SATURDAY
//        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    };
    private static final int[] DAYS_START_MONDAY = { //THHJ
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY /*THHJ: SATURDAY was left out of list:*/, SATURDAY, SUNDAY
//        SUNDAY, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY
    };
    /**
     * DAY_LENGTH = 86400000L.
     */
    private static final long DAY_LENGTH = 86400000L;
    /**
     * THHJ: if true, all weeks are starting from Mondays instead of default (US
     * standard) Sundays
     */
//    private static boolean weekStartsMonday = Settings.getInstance().weeksStartOnMondays();
    private static boolean weekStartsMonday = MyPrefs.weeksStartOnMondays.getBoolean();
    private static int ALL_WEEKDAYS = MONDAY | TUESDAY | WEDNESDAY | THURSDAY | FRIDAY;
    private static int ALL_WEEKEND = SATURDAY | SUNDAY;

    // JAVADOC COMMENT ELIDED
    /**
     * Returns an Enumeration of dates on which an Event would occur. A start
     * date is specified form which the repeating rule is applied to generate
     * dates. Then a beginning date and a start date is also provided to return
     * only a subset of all possible occurrences of an Event within the given
     * timeframe. The sequence of the items is by date. Exceptional dates are
     * not included in the returned Enumeration. For example, an Event may
     * happen every Monday during a year starting on January 1st. However, one
     * wants to know occurrences of the Event during the month of June only. The
     * startDate parameter specifies the anchor point for the Event from which
     * it begins repeating, and the subsetBeginning and subsetEnding parameters
     * would limit the Events returned to those only in June in this example.
     * Parameters: startDate - the start date for the sequence, from which the
     * repeat rule is applied to generate possible occurrence dates. This value
     * must be expressed in the same long value format as java.util.Date, which
     * is milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     * subsetBeginning - the beginning date of the period for which events
     * should be returned. This value must be expressed in the same long value
     * format as java.util.Date, which is milliseconds since the epoch (00:00:00
     * GMT, January 1, 1970). subsetEnding - the end date of the period for
     * which events should be returned. This value must be expressed in the same
     * long value format as java.util.Date, which is milliseconds since the
     * epoch (00:00:00 GMT, January 1, 1970). Returns: an Enumeration of dates
     * for the given parameters, with the Enumeration containing java.util.Date
     * instances. Throws: java.lang.IllegalArgumentException - if beginning is
     * greater than ending.
     */
//    public Vector<Date> datesAsVector(long startDate, long subsetBeginning, long subsetEnding) {
    public Vector<Date> datesAsVector(Date startDateD, Date subsetBeginningD, Date subsetEndingD) {
        long startDate = startDateD.getTime();
        long subsetBeginning = subsetBeginningD.getTime();
        long subsetEnding = subsetEndingD.getTime();

        if (subsetBeginning > subsetEnding) {
            throw new IllegalArgumentException("Bad range: "
                    //                    + new MyDate(subsetBeginning).formatDate(false) + "("
                    + new Date(subsetBeginning) + "("
                    //                    + PIMHandler.getInstance().composeDateTime(subsetBeginning)
                    //                    + ") to " + new MyDate(subsetEnding).formatDate(false) + "(" //                    + PIMHandler.getInstance().composeDateTime(subsetEnding)
                    + ") to " + new Date(subsetEnding) + "(" //                    + PIMHandler.getInstance().composeDateTime(subsetEnding)
            );
        }
        Calendar calendar = Calendar.getInstance();
        Date dateObj = new Date(startDate);
        calendar.setTime(dateObj);
        Vector dates = new Vector();
        long date = startDate;
        Integer frequency = (Integer) getField(FREQUENCY, null);
        int interval = ((Integer) getField(INTERVAL,
                new Integer(1))).intValue();
        int count = ((Integer) getField(COUNT, new Integer(Integer.MAX_VALUE))).intValue();
//        long end = ((Long) getField(END, new Long(Long.MAX_VALUE))).longValue();
        long end = ((Long) getField(END, new Long(MyDate.MAX_DATE))).longValue();
        Integer dayInWeek = (Integer) getField(DAY_IN_WEEK, null);
        Integer dayInMonth = (Integer) getField(DAY_IN_MONTH, null);
        Integer dayInYear = (Integer) getField(DAY_IN_YEAR, null);
        Integer weekInMonth = (Integer) getField(WEEK_IN_MONTH, null);
        Integer weekdaysInMonth = (Integer) getField(WEEKDAYS_IN_MONTH, null); //THHJ
        Integer monthInYear = (Integer) getField(MONTH_IN_YEAR, null);
        // set defaults, based on starting date
        if (dayInMonth == null && weekInMonth == null && weekdaysInMonth == null) {
            dayInMonth = new Integer(calendar.get(Calendar.DAY_OF_MONTH));
        }
        if (dayInWeek == null) {
            switch (calendar.get(Calendar.DAY_OF_WEEK)) {
                case Calendar.SUNDAY:
                    dayInWeek = new Integer(SUNDAY);
                    break;
                case Calendar.MONDAY:
                    dayInWeek = new Integer(MONDAY);
                    break;
                case Calendar.TUESDAY:
                    dayInWeek = new Integer(TUESDAY);
                    break;
                case Calendar.WEDNESDAY:
                    dayInWeek = new Integer(WEDNESDAY);
                    break;
                case Calendar.THURSDAY:
                    dayInWeek = new Integer(THURSDAY);
                    break;
                case Calendar.FRIDAY:
                    dayInWeek = new Integer(FRIDAY);
                    break;
                case Calendar.SATURDAY:
                    dayInWeek = new Integer(SATURDAY);
                    break;
            }
        }
        long rangeStart = Math.max(subsetBeginning, startDate);
        long rangeEnd = Math.min(subsetEnding, end);
//        for (int i = 0; date <= subsetEnding && date <= end && i < count; i++) {//THJ: error: i<count means that if date<subsetEnding, counting will be done from the original start date, use dates.size() instead to count actually generated dates
        for (int i = 0; date <= subsetEnding && date <= end && dates.size() < count; i++) {
            if (frequency == null) {
                // no repetitions
                storeDate(dates, startDate, rangeStart, rangeEnd);
                break;
            }
            switch (frequency.intValue()) {
                case DAILY:
                    storeDate(dates, date, rangeStart, rangeEnd);
                    date += DAY_INCREMENT * interval;
                    dateObj.setTime(date);
                    calendar.setTime(dateObj);
                    break;
                case WEEKLY:
                    if (dayInWeek == null) {
                        storeDate(dates, date, rangeStart, rangeEnd);
                    } else {
                        // shift date to the beginning of the week
                        date -= DAY_INCREMENT
                                * (calendar.get(Calendar.DAY_OF_WEEK)
                                //                                - Calendar.SUNDAY);
                                - (weekStartsMonday ? Calendar.MONDAY : Calendar.SUNDAY));
                        dateObj.setTime(date);
                        calendar.setTime(dateObj);
                        storeDays(dates, date, rangeStart, rangeEnd,
                                dayInWeek.intValue());
                    }
                    // increment the week
                    date += DAY_INCREMENT * 7 * interval;
                    /*THHJ: added " * interval" otherwise weekly frequency repeats every week!*/
                    dateObj.setTime(date);
                    calendar.setTime(dateObj);
                    break;
                case MONTHLY: {
                    storeDaysByMonth(dates, date, rangeStart, rangeEnd,
                            dayInWeek, dayInMonth, weekInMonth, weekdaysInMonth/*THHJ*/);
                    // increment the month
                    int currentMonth = calendar.get(Calendar.MONTH);
                    /*THHJ:vvvvvvvvvvvvvvvvvvvv^vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv*/
                    int currentMonths = currentMonth + interval; //interval can be > 12, currentMonth+interval can become bigger than 11 (DECEMBER)
                    int currentYear = calendar.get(Calendar.YEAR);
//                    calendar.set(Calendar.YEAR, currentYear + (currentMonths + 1) / 12); // need to +1 since JAN==0, DEC==11; dividing by 12 gives us the number of years to increment
                    calendar.set(Calendar.YEAR, currentYear + (currentMonths) / 12); // need to +1 since JAN==0, DEC==11; dividing by 12 gives us the number of years to increment
//                    calendar.set(Calendar.MONTH, ((currentMonths + 1) % 12) - 1); //also here: need to adjust +1 before doing %12, then adjust -1 to get it down to interval 0-11
                    calendar.set(Calendar.MONTH, ((currentMonths) % 12)); //also here: need to adjust +1 before doing %12, then adjust -1 to get it down to interval 0-11
                    calendar.set(Calendar.DAY_OF_MONTH, 1); //THHJ: Needed to avoid that a reperation stops if day of last month corresponds to end date (not a problem to change DAY_OF_MONTH since anyway storeDaysByMonth resets day to first day of month)
                    /*THHJ:^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^*/
//                    if (currentMonth == Calendar.DECEMBER) {
//                        int currentYear = calendar.get(Calendar.YEAR);
//                        calendar.set(Calendar.YEAR, currentYear + 1);
//                        calendar.set(Calendar.MONTH, Calendar.JANUARY);
//                    } else {
//                        calendar.set(Calendar.MONTH, currentMonth + 1);
//                    }
                    dateObj = calendar.getTime();
                    date = dateObj.getTime();
                    break;
                }
                case YEARLY: {
                    if (monthInYear == null && dayInYear == null) {
                        storeDate(dates, date, rangeStart, rangeEnd);
                    } else {
                        // shift to January
                        calendar.set(Calendar.MONTH, Calendar.JANUARY);
                        dateObj = calendar.getTime();
                        date = dateObj.getTime();
                        if (monthInYear != null) {
                            int months = monthInYear.intValue();
                            for (int m = 0; m < MONTHS.length; m++) {
                                if ((months & MONTHS[m]) != 0) {
                                    calendar.set(Calendar.MONTH, m);
                                    storeDaysByMonth(dates,
                                            calendar.getTime().getTime(),
                                            rangeStart, rangeEnd,
                                            dayInWeek, dayInMonth, weekInMonth, weekdaysInMonth/*THHJ*/);
                                }
                            }
                        } else {
                            // dayInYear is non-null
                            // shift to the first of January
                            calendar.set(Calendar.DAY_OF_MONTH, 1);
                            dateObj = calendar.getTime();
                            date = dateObj.getTime();
                            storeDate(dates,
                                    date + (dayInYear.intValue() - 1) * DAY_INCREMENT,
                                    rangeStart,
                                    rangeEnd);
                        }
                    }
                    // increment the year
                    calendar.set(Calendar.YEAR,
                            calendar.get(Calendar.YEAR) + 1 * interval /*THHJ*/);
                    dateObj = calendar.getTime();
                    date = dateObj.getTime();
                    break;
                }
                default:
                    throw new IllegalArgumentException(
                            "Unrecognized value for frequency: " + frequency);
            } // end switch
        } // end for
//        return dates.elements();
        return dates;
    }

//    public Enumeration datesXXX(long startDate, long subsetBeginning, long subsetEnding) {
//        return datesAsVector(startDate, subsetBeginning, subsetEnding).elements();
//    }
    /**
     * Stores a date.
     *
     * @param dates array to extend
     * @param date to be stored
     * @param rangeStart beginning of range
     * @param rangeEnd end of range
     */
    private void storeDate(Vector dates, long date, long rangeStart, long rangeEnd) {
        storeDate(dates, date, rangeStart, rangeEnd, true);
    }

    private void storeDate(Vector dates, long date, long rangeStart, long rangeEnd, boolean addSorted) {
//        Log.l()
        if (date >= rangeStart && date <= rangeEnd) {
//        if (date > rangeStart && date <= rangeEnd) { //'<' no good since it makes us lose eg 1st date in a range
            Date dateObj = new Date(date); //THHJ: support for exceptions removed
//            if (!exceptions.contains(dateObj)) { //THHJ
            if (addSorted) {
                if (dates.isEmpty() || date > ((Date) dates.lastElement()).getTime()) { //check this first since most likely case
                    dates.addElement(dateObj); //THHJ: insert sorted!! to cover eg case where unclear if LAST week or FOURTH week comes first
                } else {
                    for (int i = dates.size() - 2; i >= 0; i--) { //search from second last element in list since new date is most likely bigger than previous dates
                        if (date > ((Date) dates.elementAt(i)).getTime()) {
//                        dates.addElement(new Date(date)); //THHJ: TODO insert sorted!! to cover eg case where unclear if LAST week or FOURTH week comes first
                            dates.insertElementAt(dateObj, i + 1); //date at position i is smaller than new date, so insert after it
                            return; //element inserted, we're done
                        }
                    }
                    dates.insertElementAt(dateObj, 0); //no smaller date was found (or dates list empty), insert at head of list
                }
            } else {
                dates.addElement(dateObj); //THHJ: TODO insert sorted!! to cover eg case where unclear if LAST week or FOURTH week comes first
            }//            }
        }
    }

    /**
     * Store days.
     *
     * @param dates array to extend
     * @param date to be stored
     * @param rangeStart beginning of range
     * @param rangeEnd end of range
     * @param days filter by specific days
     */
    private void storeDays(Vector dates, long date, long rangeStart, long rangeEnd, int days) {
        // shift date back to Sunday, if it is not already Sunday
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - (weekStartsMonday ? Calendar.MONDAY : Calendar.SUNDAY); //THHJ: dayShift = #days after Sunday, eg TUES => 2
        int dayShift;
        if (weekStartsMonday) {
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dayShift = 6;
            } else {
                dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
            }
        } else {
            dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
        }
        date -= dayShift * DAY_INCREMENT; //THHJ: date adjusted back to first Sunday/Monday before date
//        long dateNextWeek = dayShift + DAY_INCREMENT * 7; //THHJ: eg 2+7 => same day next week (==date + 7*DAY_INCREMENT??!!)
        long dateNextWeek = date + DAY_INCREMENT * 7; //THHJ: ERROR: 'date', not 'dayshift', should be eg 2+7 => same day next week (==date + 7*DAY_INCREMENT??!!)
        for (int i = 0; i < DAYS.length; i++) {
//            if ((days & DAYS[i]) != 0) {
            if ((days & (weekStartsMonday ? DAYS_START_MONDAY[i] : DAYS[i])) != 0) {
//                long targetDate = (dayShift > i) ? dateNextWeek : date; //for all days < org. date, use day in next week
//                long targetDate = (i < dayShift) ? dateNextWeek : date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
                long targetDate = date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
                storeDate(dates,
                        targetDate + DAY_INCREMENT * i, //THHJ: add
                        rangeStart, rangeEnd);
            }
        }
    }

    /**
     * THHJ: store the first coming week days on or after date
     */
//    private void storeWeekDays(Vector dates, long date, long rangeStart, long rangeEnd, int days) {
//        storeWeekDays(dates, date, rangeStart, rangeEnd, days, false);
//    }
    /**
     *
     * @param dates
     * @param date
     * @param rangeStart
     * @param rangeEnd
     * @param days
     * @param findFromEnd when finding individual days, e.g. MONDAY, or TUESDAY
     * it doesn't matter which order we search in, but to find the last weekday
     * in a month
     */
    private void storeWeekDays(Vector dates, long date, long rangeStart, long rangeEnd, int days) {
        // shift date back to Sunday, if it is not already Sunday
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - (weekStartsMonday ? Calendar.MONDAY : Calendar.SUNDAY); //THHJ: dayShift = #days after Sunday, eg TUES => 2
        int dayShift; //how much to add to idx to get right weekday?
        if (weekStartsMonday) {
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dayShift = 6;
            } else {
                dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
            }
        } else {
            dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
        }
//        date -= dayShift * DAY_INCREMENT; //THHJ: date adjusted back to first Sunday before date
        for (int i = DAYS.length - 1; i >= 0; i--) {
//            if ((days & DAYS[i]) != 0) {
//            int idx = (((i + dayShift) < DAYS.length) ? (i + dayShift) : i);
            int idx = (i + dayShift) % DAYS.length; //modulus to ensure idx 'wraps around' to stay within the days array
//            if ((days & (weekStartsMonday?DAYS_START_MONDAY[i+dayShift]:DAYS[i+dayShift])) != 0) {
            if ((days & WEEKDAYS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKDAYS) != 0) { //we're looking for a weekday
                storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                i = 0; //only add a single day
            } else if ((days & WEEKENDS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKEND) != 0) { //we're looking for a weekend day
                storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                i = 0; //only add a single day
            } else if ((days & (weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx])) != 0) {
//                long targetDate = (dayShift > i) ? dateNextWeek : date; //for all days < org. date, use day in next week
//                long targetDate = (i < dayShift) ? dateNextWeek : date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
//                long targetDate = date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
                storeDate(dates,
                        //                        targetDate + DAY_INCREMENT * i, //THHJ: add
                        date + DAY_INCREMENT * i, //THHJ: add
                        rangeStart, rangeEnd);
            }
        }
    }

    /**
     * finds the first/last weekdays, or weekend days of the month
     *
     * @param dates
     * @param date
     * @param rangeStart
     * @param rangeEnd
     * @param days
     * @param findFromEnd
     */
    private void storeFirstLastDaysInMonth(Vector dates, long date, long rangeStart, long rangeEnd, int days, int weekDaysInMonth) {
        int dayNumberInMonth = 0;
        switch (weekDaysInMonth) {
            case FIRST:
                dayNumberInMonth = 1;
                break;
            case SECOND:
                dayNumberInMonth = 2;
                break;
            case THIRD:
                dayNumberInMonth = 3;
                break;
            case FOURTH:
                dayNumberInMonth = 4;
                break;
            case FIFTH:
                dayNumberInMonth = 5;
                break;
            case LAST:
                dayNumberInMonth = -1;
                break;
            case SECONDLAST:
                dayNumberInMonth = -2;
                break;
            case THIRDLAST:
                dayNumberInMonth = -3;
                break;
            case FOURTHLAST:
                dayNumberInMonth = -4;
                break;
            case FIFTHLAST:
                dayNumberInMonth = -5;
                break;
        }
//        final int CalendarWeekdays = Calendar.MONDAY | Calendar.TUESDAY | Calendar.WEDNESDAY | Calendar.THURSDAY | Calendar.FRIDAY;
//        final int CalendarWeekendDays = Calendar.SATURDAY | Calendar.SUNDAY;
        // find the end of the month, assuming no month is longer
        // than 31 days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        long monthEnd = date + DAY_INCREMENT * 31;
        calendar.setTime(new Date(monthEnd));
        while (calendar.get(Calendar.DAY_OF_MONTH) > 1) {
            monthEnd -= DAY_INCREMENT;
            calendar.setTime(new Date(monthEnd));
        }
        monthEnd -= DAY_INCREMENT; //move back another day to end of previous month
        calendar.setTime(new Date(monthEnd));

        int nbDaysInMonth = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
        int matchingDaysFound = 0;

        if (dayNumberInMonth > 0) {
            for (int i = 0; i < nbDaysInMonth; i++) {
                cal.set(Calendar.DAY_OF_MONTH, 1 + i); //set day of month, starting with first day
//                if (((days & WEEKDAYS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) & CalendarWeekdays) != 0)
//                        || ((days & WEEKENDS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) & CalendarWeekendDays) != 0)) {
                if (((days & WEEKDAYS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && cal.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY))
                        || ((days & WEEKENDS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))) {
                    matchingDaysFound++;
                    if (matchingDaysFound == dayNumberInMonth) {
                        storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                        return;
                    }
                }
            }
        } else if (dayNumberInMonth < 0) {
            for (int i = 0; i < nbDaysInMonth; i++) {
                cal.set(Calendar.DAY_OF_MONTH, nbDaysInMonth - i); //set day of month, starting with last day
//                if (((days & WEEKDAYS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) & CalendarWeekdays) != 0)
//                        || ((days & WEEKENDS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) & CalendarWeekendDays) != 0)) {
                if (((days & WEEKDAYS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) >= Calendar.MONDAY && cal.get(Calendar.DAY_OF_WEEK) <= Calendar.FRIDAY))
                        || ((days & WEEKENDS) != 0 && (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))) {
                    matchingDaysFound--;
                    if (matchingDaysFound == dayNumberInMonth) {
                        storeDate(dates, monthEnd - DAY_INCREMENT * i, rangeStart, rangeEnd);
                        return;
                    }
                }
            }
        }
    }

    private void xxstoreWeekDays(Vector dates, long date, long rangeStart, long rangeEnd, int days, boolean findFromEnd) {
        // shift date back to Sunday, if it is not already Sunday
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(date));
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
//        int dayShift = cal.get(Calendar.DAY_OF_WEEK) - (weekStartsMonday ? Calendar.MONDAY : Calendar.SUNDAY); //THHJ: dayShift = #days after Sunday, eg TUES => 2
        int dayShift; //how much to add to idx to get right weekday?
        if (weekStartsMonday) {
            if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                dayShift = 6;
            } else {
                dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
            }
        } else {
            dayShift = cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY; //THHJ: dayShift = #days after Sunday, eg TUES => 2
        }
//        date -= dayShift * DAY_INCREMENT; //THHJ: date adjusted back to first Sunday before date
        if (findFromEnd) {
            for (int i = DAYS.length - 1; i >= 0; i--) {
//            if ((days & DAYS[i]) != 0) {
//            int idx = (((i + dayShift) < DAYS.length) ? (i + dayShift) : i);
                int idx = (i + dayShift) % DAYS.length; //modulus to ensure idx 'wraps around' to stay within the days array
//            if ((days & (weekStartsMonday?DAYS_START_MONDAY[i+dayShift]:DAYS[i+dayShift])) != 0) {
                if ((days & WEEKDAYS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKDAYS) != 0) { //we're looking for a weekday
                    storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                    i = 0; //only add a single day
                } else if ((days & WEEKENDS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKEND) != 0) { //we're looking for a weekend day
                    storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                    i = 0; //only add a single day
                } else if ((days & (weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx])) != 0) {
//                long targetDate = (dayShift > i) ? dateNextWeek : date; //for all days < org. date, use day in next week
//                long targetDate = (i < dayShift) ? dateNextWeek : date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
//                long targetDate = date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
                    storeDate(dates,
                            //                        targetDate + DAY_INCREMENT * i, //THHJ: add
                            date + DAY_INCREMENT * i, //THHJ: add
                            rangeStart, rangeEnd);
                }
            }
        } else {
            for (int i = 0; i < DAYS.length; i++) {
//            if ((days & DAYS[i]) != 0) {
//            int idx = (((i + dayShift) < DAYS.length) ? (i + dayShift) : i);
                int idx = (i + dayShift) % DAYS.length; //modulus to ensure idx 'wraps around' to stay within the days array
//            if ((days & (weekStartsMonday?DAYS_START_MONDAY[i+dayShift]:DAYS[i+dayShift])) != 0) {
                if ((days & WEEKDAYS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKDAYS) != 0) { //we're looking for a weekday
                    storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                    i = DAYS.length; //only add a single day
                } else if ((days & WEEKENDS) != 0 && ((weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx]) & ALL_WEEKEND) != 0) { //we're looking for a weekend day
                    storeDate(dates, date + DAY_INCREMENT * i, rangeStart, rangeEnd);
                    i = DAYS.length; //only add a single day
                } else if ((days & (weekStartsMonday ? DAYS_START_MONDAY[idx] : DAYS[idx])) != 0) {
//                long targetDate = (dayShift > i) ? dateNextWeek : date; //for all days < org. date, use day in next week
//                long targetDate = (i < dayShift) ? dateNextWeek : date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
//                long targetDate = date; //for all days < org. date, use *following* Sunday (Sunday next week) as reference
                    storeDate(dates,
                            //                        targetDate + DAY_INCREMENT * i, //THHJ: add
                            date + DAY_INCREMENT * i, //THHJ: add
                            rangeStart, rangeEnd);
                }
            }
        }
    }

    /**
     * returns true if dayInMonth is selected as in daysInMonth
     * @param daysInMonth bit pattern with selected days in month, right-most bit = day 1.
     * @param dayInMonth 1..31
     * @return 
     */
    private boolean containsDay(int daysInMonth, int dayInMonth) {
        com.codename1.ui.Calendar cal; 
//use Month class in com.codename1.ui.Calendar as inspiration (basically just a grid
        
        int dayInMonthBit = 1 << (dayInMonth-1);
        if ((dayInMonthBit & dayInMonthBit) != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Store days by month.
     *
     * @param dates array to be extended
     * @param date date to be added.
     * @param rangeStart beginning of range
     * @param rangeEnd end of range
     * @param dayInWeek filter for day in the week
     * @param dayInMonth filter for day in the month
     * @param weekInMonth filter for week in the month
     */
//    private void storeDaysByMonth(Vector dates, long date, long rangeStart, long rangeEnd, Integer dayInWeek, Integer dayInMonth, Integer weekInMonth) {
    private void storeDaysByMonth(Vector dates, long date, long rangeStart, long rangeEnd, Integer dayInWeek, Integer dayInMonth, Integer weekInMonth, Integer weekDaysInMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        Integer daysInMonth = null;//0;
//        Integer weekDaysInMonth=null; //THHJ
        // move date to the first of the month
        date -= DAY_INCREMENT
                * (calendar.get(Calendar.DAY_OF_MONTH) - 1);
        if (daysInMonth != null) {
//            if(containsDay(daysInMonth,date)){
//                
//            }
        } else if (dayInMonth != null) {
            /*THHJvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv*/
            //check if day of month is higher than actual month last day, if so, change to last day (fixes eg that currently 31/2 actually becomes 3/3
            //use eg 32 (or higher) as "last day of month" so correct that down to last day of month
            int day = dayInMonth.intValue();
            int nbDaysInMonth = MyDate.getDaysInMonth(calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.YEAR)); //+1 since MyDate uses 1..12
//            if (day>31 || dayInMonth.intValue()>MyDate.getDaysInMonth(calendar.get(Calendar.MONTH+1), calendar.get(Calendar.YEAR))) {
            //find the last day in this specific month
            while (day > nbDaysInMonth) {
                day--;
            }
            /*THHJ^^^^^^^^^^^^^^^^^^^^^^^^^^*/
            storeDate(dates,
                    //                    date + DAY_INCREMENT * (dayInMonth.intValue() - 1),
                    date + DAY_INCREMENT * (day - 1), /*THHJ: changed to "(day -1)"*/
                    rangeStart, rangeEnd);
        } else if (weekInMonth != null) {
            // get a limited range, containing only this month.
            long monthRangeStart = Math.max(rangeStart, date);
            // find the end of the month, assuming no month is longer
            // than 31 days
            long monthEnd = date + DAY_INCREMENT * 31;
            calendar.setTime(new Date(monthEnd));
            while (calendar.get(Calendar.DAY_OF_MONTH) > 1) {
                monthEnd -= DAY_INCREMENT;
                calendar.setTime(new Date(monthEnd));
            }
            monthEnd -= DAY_INCREMENT;
            long monthRangeEnd = Math.min(rangeEnd, monthEnd);
            int weeks = weekInMonth.intValue();
            if ((weeks & FIFTHLAST) != 0) {
//                storeDays(dates, monthEnd - DAY_INCREMENT * 34,
                storeDays(dates, monthEnd - DAY_INCREMENT * 28,
                        monthRangeStart, monthRangeEnd,
                        dayInWeek.intValue());
            }
            if ((weeks & FIRST) != 0) {
                storeDays(dates, date, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
            }
            if ((weeks & FOURTHLAST) != 0) {
//                storeDays(dates, monthEnd - DAY_INCREMENT * 27,
                storeDays(dates, monthEnd - DAY_INCREMENT * 21,
                        monthRangeStart, monthRangeEnd,
                        dayInWeek.intValue());
            }
            if ((weeks & SECOND) != 0) {
                storeDays(dates, date + DAY_INCREMENT * 7, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
            }
            if ((weeks & THIRDLAST) != 0) {
//                storeDays(dates, monthEnd - DAY_INCREMENT * 20,
                storeDays(dates, monthEnd - DAY_INCREMENT * 14,
                        monthRangeStart, monthRangeEnd,
                        dayInWeek.intValue());
            }
            if ((weeks & THIRD) != 0) {
                storeDays(dates, date + DAY_INCREMENT * 14, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
            }
            if ((weeks & SECONDLAST) != 0) { //THHJ: moved before LAST to ensure
//                storeDays(dates, monthEnd - DAY_INCREMENT * 13,
                storeDays(dates, monthEnd - DAY_INCREMENT * 7,
                        monthRangeStart, monthRangeEnd,
                        dayInWeek.intValue());
            }
            if ((weeks & FOURTH) != 0) {
                storeDays(dates, date + DAY_INCREMENT * 21, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
            }
            if ((weeks & FIFTH) != 0) {
                storeDays(dates, date + DAY_INCREMENT * 28, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
            }
            //THHJ: apparently by error the code for LAST is repeated twice in the source...
//            if ((weeks & LAST) != 0) {
//                storeDays(dates, monthEnd - DAY_INCREMENT * 6,
//                        monthRangeStart, monthRangeEnd,
//                        dayInWeek.intValue());
//            }
            if ((weeks & LAST) != 0) {
//                storeDays(dates, monthEnd - DAY_INCREMENT * 6,
                storeDays(dates, monthEnd, //THHJ: pick week of the last day of the month
                        monthRangeStart, monthRangeEnd,
                        dayInWeek.intValue());
            }
        } else if (weekDaysInMonth != null) {
            // get a limited range, containing only this month.
            long monthRangeStart = Math.max(rangeStart, date);
            // find the end of the month, assuming no month is longer
            // than 31 days
            long monthEnd = date + DAY_INCREMENT * 31;
            calendar.setTime(new Date(monthEnd));
            while (calendar.get(Calendar.DAY_OF_MONTH) > 1) {
                monthEnd -= DAY_INCREMENT;
                calendar.setTime(new Date(monthEnd));
            }
            monthEnd -= DAY_INCREMENT;
            long monthRangeEnd = Math.min(rangeEnd, monthEnd);
            int weekDays = weekDaysInMonth.intValue();
//                boolean searchWeekDaysFromBehind = (dayInWeek.intValue() & WEEKDAYS) != 0 || (dayInWeek.intValue() & WEEKENDS) != 0; //only search from behind if searching for WEEKDAYS/WEEKENDS
            if ((dayInWeek.intValue() & WEEKDAYS) != 0 || (dayInWeek.intValue() & WEEKENDS) != 0) {
                storeFirstLastDaysInMonth(dates, date, monthRangeStart, monthRangeEnd, dayInWeek.intValue(), weekDays);
            } else {
                if ((weekDays & FIFTHLAST) != 0) {
                    storeWeekDays(dates, monthEnd - DAY_INCREMENT * 34, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & FIRST) != 0) {
                    storeWeekDays(dates, date, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & FOURTHLAST) != 0) {
                    storeWeekDays(dates, monthEnd - DAY_INCREMENT * 27, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & SECOND) != 0) {
                    storeWeekDays(dates, date + DAY_INCREMENT * 7, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & THIRDLAST) != 0) {
                    storeWeekDays(dates, monthEnd - DAY_INCREMENT * 20, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & THIRD) != 0) {
                    storeWeekDays(dates, date + DAY_INCREMENT * 14, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & SECONDLAST) != 0) {
                    storeWeekDays(dates, monthEnd - DAY_INCREMENT * 13, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & FOURTH) != 0) {
                    storeWeekDays(dates, date + DAY_INCREMENT * 21, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & FIFTH) != 0) {
                    storeWeekDays(dates, date + DAY_INCREMENT * 28, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
                if ((weekDays & LAST) != 0) {
                    storeWeekDays(dates, monthEnd - DAY_INCREMENT * 6, monthRangeStart, monthRangeEnd, dayInWeek.intValue());
                }
            }
        }
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Add a Date for which this RepeatRule should not occur. This value may be
     * rounded off to the date only from a date time stamp if the underlying
     * platform implementation only supports date fields with dates only and not
     * date time stamps. Parameters: date - the date to add to the list of
     * except dates, expressed in the same long value format as java.util.Date,
     * which is milliseconds since the epoch (00:00:00 GMT, January 1, 1970).
     *
     * @param field
     * @return
     */
//    public void addExceptDate(long date) {
//        exceptions.addElement(new Date(date));
//    }
    // JAVADOC COMMENT ELIDED
    /**
     * Remove a Date for which this RepeatRule should not occur. If the date was
     * in the list of except dates, it is removed. Parameters: date - the date
     * to remove from the list of except dates expressed in the same long value
     * format as java.util.Date, which is milliseconds since the epoch (00:00:00
     * GMT, January 1, 1970).
     *
     * @param field
     * @return
     */
//    public void removeExceptDate(long date) {
//        exceptions.removeElement(new Date(date));
//    }
//
//    // JAVADOC COMMENT ELIDED
    /**
     * Returns the Dates for which this RepeatRule should not occur. Returns: an
     * Enumeration of dates for which this RepeatRule should not occur, with the
     * Enumeration containing java.util.Date instances.
     *
     * @param field
     * @return
     */
//    public Enumeration getExceptDates() {
//        Vector results = new Vector();
//        for (Enumeration e = exceptions.elements();
//                e.hasMoreElements();) {
//            Date date = (Date) e.nextElement();
//            results.addElement(new Date(date.getTime()));
//        }
//        return results.elements();
//    }
    // JAVADOC COMMENT ELIDED
    /**
     * Retrieves an integer field. The field values can be one of COUNT,
     * DAY_IN_MONTH, FREQUENCY, INTERVAL, MONTH_IN_YEAR, WEEK_IN_MONTH,
     * DAY_IN_WEEK, DAY_IN_YEAR. getFields() should be checked prior to invoking
     * the method to ensure the field has a value associated with it.
     * Parameters: field - The field to get, for example COUNT. Returns: an int
     * representing the value of the field. Throws:
     * java.lang.IllegalArgumentException - if field is not one of the the valid
     * RepeatRule fields for this method. FieldEmptyException - if the field
     * does is a valid integer field but does not have any data values assigned
     * to it.
     *
     * @param field
     * @return
     */
    public int getInt(int field) {
        validateDataType(field, PIMItem.INT);
        return ((Integer) getField(field, NO_DEFAULT)).intValue();
    }
    /**
     * NO_DEFAULT = "".
     */
    private static final Object NO_DEFAULT = "";

    /**
     * Gets the requested field contents.
     *
     * @param field identifier for the requested field
     * @param defaultValue value to return if field is not found
     * @return requetsed field contents
     */
    private Object getField(int field, Object defaultValue) {
        Integer fieldKey = new Integer(field);
        Object fieldValue = fields.get(fieldKey);
        if (fieldValue == null) {
            if (defaultValue == NO_DEFAULT) {
                throw new FieldEmptyException();
            } else {
                return defaultValue;
            }
        }
        return fieldValue;
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Sets an integer field. The field value can be one of COUNT, DAYNUMBER,
     * FREQUENCY, INTERVAL, MONTH_IN_YEAR, WEEK_IN_MONTH, DAY_IN_WEEK,
     * DAY_IN_YEAR. Parameters: field - The field to set, for example COUNT.
     * value - The value to set the field to. Throws:
     * java.lang.IllegalArgumentException - if field is not one of the the valid
     * RepeatRule fields for this method, or the value provided is not a valid
     * value for the given field.
     *
     * @param field
     * @param value
     */
    public void setInt(int field, int value) {
        validateDataType(field, PIMItem.INT);
        boolean isValid;
        switch (field) {
            case COUNT:
                isValid = (value >= 1);
                break;
            case DAY_IN_MONTH:
                isValid = (value >= 1 && value <= 31);
                break;
            case DAY_IN_WEEK:
                isValid = (value & ~DAY_IN_WEEK_MASK) == 0;
                break;
            case FREQUENCY:
                switch (value) {
                    case DAILY:
                    case WEEKLY:
                    case MONTHLY:
                    case YEARLY:
                        isValid = true;
                        break;
                    default:
                        isValid = false;
                }
                break;
            case INTERVAL:
                isValid = (value >= 1);
                break;
            case MONTH_IN_YEAR:
                isValid = (value & ~MONTH_IN_YEAR_MASK) == 0;
                break;
            case WEEK_IN_MONTH:
                isValid = (value & ~WEEK_IN_MONTH_MASK) == 0;
                break;
            case WEEKDAYS_IN_MONTH:
                isValid = (value & ~WEEK_IN_MONTH_MASK) == 0; //THHJ: reuse same values
                break;
            case DAY_IN_YEAR:
                isValid = (value >= 1 && value <= 366);
                break;
            default:
                isValid = false;
        }
        if (!isValid) {
            throw new IllegalArgumentException("Field=" + field + ": value " + value + " is invalid");
        }
        Integer fieldKey = new Integer(field);
        fields.put(fieldKey, new Integer(value));
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Retrieves a Date field. The field value is currently limited to END.
     * getFields() should be checked prior to invoking the method to ensure the
     * field has a value associated with it. Parameters: field - The field to
     * get. Returns: a Date representing the value of the field, expressed in
     * the same long value format as java.util.Date, which is milliseconds since
     * the epoch (00:00:00 GMT, January 1, 1970). Throws:
     * java.lang.IllegalArgumentException - if field is not one of the the valid
     * RepeatRule fields for this method. FieldEmptyException - if the field
     * does is a valid date field but does not have any data values assigned to
     * it.
     *
     * @param field
     * @return
     */
    public long getDate(int field) {
        validateDataType(field, PIMItem.DATE);
        return ((Long) getField(field, NO_DEFAULT)).longValue();
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Sets a Date field. The field value is currently limited to END. This
     * field may be rounded off to the date only from a date time stamp if the
     * underlying platform implementation only supports date fields with dates
     * only and not date time stamps. Parameters: field - The field to set.
     * value - The value to set the field to, expressed in the same long value
     * format as java.util.Date, which is milliseconds since the epoch (00:00:00
     * GMT, January 1, 1970). Throws: java.lang.IllegalArgumentException - if
     * field is not one of the the valid RepeatRule fields for this method.
     *
     * @param field
     * @param value
     */
    public void setDate(int field, long value) {
        validateDataType(field, PIMItem.DATE);
        Integer fieldKey = new Integer(field);
        fields.put(fieldKey, new Long(value));
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Returns a list of fields that currently have values assigned to it. If a
     * field is not "set", the field is not included in the return value.
     * Returns: an array of fields that have values currently assigned to them.
     * If no fields have values set, an array of zero length is returned.
     *
     * @return
     */
    public int[] getFields() {
        int[] result = new int[fields.size()];
        int i = 0;
        for (Enumeration e = fields.keys(); e.hasMoreElements();) {
            Integer fieldKey = (Integer) e.nextElement();
            result[i++] = fieldKey.intValue();
        }
        return result;
    }

    // JAVADOC COMMENT ELIDED
    /**
     * Compares this RepeatRule with a given RepeatRule for content equality.
     * For RepeatRules, dates are considered equal if one or both of the dates
     * compared contains a date only with no timestamp and the date values are
     * equal regardless of the time qualifier. This rule accounts for platform
     * dependent rounding off of dates from date time stamps to dates only. For
     * example, a date value of 3/14/03 with no time stamp is considered equal
     * to a date value of 3/14/03 with a time stamp. If the application requires
     * that dates be exactly equal, comparisons should be made explicitly
     * outside of this method. Overrides: equals in class java.lang.Object
     * Parameters: obj - another RepeatRule object to compare against Returns:
     * true if the contents of the RepeatRules are equivalent, false otherwise.
     *
     * @param obj
     * @return
     */
//    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RepeatRule)) {
            return false;
        }
        RepeatRule rule = (RepeatRule) obj;
        Calendar cal = Calendar.getInstance();
        int[] ruleFields = rule.getFields();
        for (int i = 0; i < ruleFields.length; i++) {
            int field = ruleFields[i];
            Object value = fields.get(new Integer(field));
            if (value == null) {
                // field in other rule is defined, but in this rule is not
                return false;
            }
            switch (getDataType(field)) {
                case PIMItem.INT: {
                    int iValue = ((Integer) value).intValue();
                    if (rule.getInt(field) != iValue) {
                        return false;
                    }
                    break;
                }
                case PIMItem.DATE: {
                    // dates match if they are on the same day
                    long thisDate = ((Long) value).longValue();
                    long ruleDate = rule.getDate(field);
                    if (thisDate == ruleDate) {
                        return true;
                    }
                    if (Math.abs(thisDate - ruleDate) >= DAY_LENGTH) {
                        return false;
                    }
                    cal.setTime(new Date(thisDate));
                    int day = cal.get(Calendar.DATE);
                    cal.setTime(new Date(ruleDate));
                    if (day != cal.get(Calendar.DATE)) {
                        return false;
                    }
                    break;
                }
                default:
                    return false; // unreachable
            }

        }
        // see if this rule defines any fields that the other rule does not
        for (Enumeration e = fields.keys(); e.hasMoreElements();) {
            Integer fieldKey = (Integer) e.nextElement();
            int field = fieldKey.intValue();
            boolean match = false;
            for (int i = 0; i < ruleFields.length && !match; i++) {
                if (ruleFields[i] == field) {
                    match = true;
                }
            }
            if (!match) {
                return false;
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
// check exception dates
// normalize the list of exception dates to represent only the date
// and not the time of day
//        int[] exceptionDates = new int[exceptions.size()];
//        for (int i = 0; i < exceptionDates.length; i++) {
//            Date date = (Date) exceptions.elementAt(i);
//            cal.setTime(date);
//            exceptionDates[i] = cal.get(Calendar.DAY_OF_MONTH)
//                    + 100 * cal.get(Calendar.MONTH)
//                    + 10000 * cal.get(Calendar.YEAR);
//        }
//        boolean[] matchedExceptionDates = new boolean[exceptionDates.length];
//        for (Enumeration e = rule.getExceptDates(); e.hasMoreElements();) {
//            Date date = (Date) e.nextElement();
//            cal.setTime(date);
//            int day = cal.get(Calendar.DAY_OF_MONTH)
//                    + 100 * cal.get(Calendar.MONTH)
//                    + 10000 * cal.get(Calendar.YEAR);
//            boolean match = false;
//            for (int i = 0; i < exceptionDates.length && !match; i++) {
//                if (exceptionDates[i] == day) {
//                    match = true;
//                    matchedExceptionDates[i] = true;
//                }
//            }
//            if (!match) {
//                return false;
//            }
//        }
//        // are there unmatched exception dates?
//        for (int i = 0; i < matchedExceptionDates.length; i++) {
//            if (!matchedExceptionDates[i]) {
//                // make sure this isn't a duplicate of another date
//                boolean duplicate = false;
//                for (int j = 0; j < i && !duplicate; j++) {
//                    duplicate = exceptionDates[j] == exceptionDates[i];
//                }
//                if (!duplicate) {
//                    return false;
//                }
//            }
//        }
//</editor-fold>
        return true;
    }

    /**
     * Checks that data type is valid.
     *
     * @param field identifier of requested field
     * @param dataType type of data to be checked
     * @throws IllegalArgumentException if type is not appropriate
     */
    private void validateDataType(int field, int dataType) {
        int correctDataType = getDataType(field);
        if (dataType != correctDataType) {
            throw new IllegalArgumentException("Invalid field type");
        }
    }

    /**
     * Gets the data type for the requested field.
     *
     * @param field identifier of requested field
     * @return data type of requested field
     */
    private int getDataType(int field) {
        switch (field) {
            case COUNT:
            case DAY_IN_MONTH:
            case DAY_IN_WEEK:
            case DAY_IN_YEAR:
            case FREQUENCY:
            case INTERVAL:
            case MONTH_IN_YEAR:
            case WEEK_IN_MONTH:
            case WEEKDAYS_IN_MONTH:
                return PIMItem.INT;
            case END:
                return PIMItem.DATE;
            default:
                throw new IllegalArgumentException("Unrecognized field: "
                        + field);
        }
    }
}
