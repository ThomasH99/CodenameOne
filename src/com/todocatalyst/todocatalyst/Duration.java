/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 * stores a duration in seconds as an int, but returns it as a long in milliseconds for . in Milliseconds: Integer.MAX==2147483647 = 596hours = 24 days. In Seconds = 24000 days == 65 years
 * @author Thomas
 */
public class Duration {

//    /** duration in seconds */
    /** duration in milliseconds */
    long duration;

    Duration(long duration) {
        this.duration = duration;
    }

    /** creates a duraction based on the ducation in milliseconds */
//    Duration(long durationInMilliSeconds) {
//        this.duration = (int) (durationInMilliSeconds / MyDate.SECOND_IN_MILLISECONDS);
////        ASSERT.that(this.duration*MyDate.SECOND_IN_MILLISECONDS+durationInMilliSeconds%MyDate.SECOND_IN_MILLISECONDS == durationInMilliSeconds, "Loss of value when converting long " + duration + " to int " + this.duration);
//        ASSERT.that(this.duration*MyDate.SECOND_IN_MILLISECONDS+durationInMilliSeconds%MyDate.SECOND_IN_MILLISECONDS == durationInMilliSeconds, "Long value "+durationInMilliSeconds+" too large to by converted to int in seconds, int = "+this.duration);
//    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
//        return duration * MyDate.SECOND_IN_MILLISECONDS;
        return duration;
    }

//    public void setDurationInMilliSeconds(long durationInMilliSeconds) {
//        this.duration = (int) durationInMilliSeconds / MyDate.SECOND_IN_MILLISECONDS;
//    }

    public void setDurationInSeconds(int durationInSeconds) {
        this.duration = ((long)durationInSeconds) * MyDate.SECOND_IN_MILLISECONDS;
    }

    public void setDuration(int hours, int minutes) {
        this.duration = ((long)hours)*MyDate.HOUR_IN_MILISECONDS + ((long)minutes) * MyDate.MINUTE_IN_MILLISECONDS;
    }

    public void setDurationInHoursMinutes(int hours, int minutes, int seconds) {
        this.duration = ((long)hours)*MyDate.HOUR_IN_MILISECONDS + ((long)minutes) * MyDate.MINUTE_IN_MILLISECONDS + ((long)seconds) * MyDate.SECOND_IN_MILLISECONDS;
    }

    /**
     * returns the number of days in the duration. Use getHours24 adn getMinutes60 to get the remaining hours and minutes
     * @return 
     */
    public int getDays() {
        return (int)(duration / MyDate.DAY_IN_MILLISECONDS);
    }

    /**
     * returns the hours up to 24, beyond that is discarded.
     * Useful to get the number of hours left after getting the number of days
     * @return 
     */
    public int getHours24() {
//        return (int)((duration % MyDate.DAY_IN_MILLISECONDS) / 60);
        return (int)((duration % MyDate.DAY_IN_MILLISECONDS) / MyDate.HOUR_IN_MILISECONDS);
    }

    public int getMinutes60() {
//        return (int)((duration % MyDate.HOUR_IN_MILISECONDS) / 60);
        return (int)((duration % MyDate.HOUR_IN_MILISECONDS) / MyDate.MINUTE_IN_MILLISECONDS);
    }

    public void addMinutes(int minutes) {
        duration += (((long)minutes)*MyDate.MINUTE_IN_MILLISECONDS);
    }

    public long getSeconds60() {
//        return (duration % MyDate.MINUTE_IN_MILLISECONDS) / 60;
        return (duration % MyDate.MINUTE_IN_MILLISECONDS) / MyDate.MINUTE_IN_MILLISECONDS;
    }

    /**
     * returns the full number of hours, even if bigger than 24. Drops hours and minutes
     * @return 
     */
    public long getDurationInHoursOnly() {
        return  (duration / MyDate.HOUR_IN_MILISECONDS);
    }

    /**
     * returns the full number of minutes, even if bigger than 60. Drops the seconds
     * @return 
     */    public long getDurationInMinutesOnly() {
        return duration / MyDate.MINUTE_IN_MILLISECONDS;
    }

    public long getDurationInSecondsOnly() {
        return duration / MyDate.SECOND_IN_MILLISECONDS;
    }

    static String formatDuration(long duration, boolean shortFormat) { //Date date) {
        int minutes = (int)(duration % MyDate.HOUR_IN_MILISECONDS) / MyDate.MINUTE_IN_MILLISECONDS;
        int hours = (int)(duration / MyDate.HOUR_IN_MILISECONDS);
        //TODO!!!: handle long durations (eg Projects) where days are needed, eg 1d12:40
//        int days = duration/MyDate.DAY_IN_MILLISECONDS;
//        return (days>0?days+"d ":"")+ duration / MyDate.HOUR_IN_MILISECONDS + ":" + (minutes < 10 ? "0" + minutes : ""+minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
//        return shortFormat&&hours==0?MyDate.format2(minutes)+"m":hours + ":" + MyDate.format2(minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
        return hours + ":" + MyDate.format2(minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
//            :hours + ":" + MyDate.format2(minutes < 10 ? "0" + minutes : "" + minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
    }
    static String formatDuration(long duration) { //Date date) {
        return formatDuration(duration, false);
//        int minutes = (duration % MyDate.HOUR_IN_MILISECONDS) / MyDate.MINUTE_IN_MILLISECONDS;
//        int days = duration/MyDate.DAY_IN_MILLISECONDS;
//        return (days>0?days+"d ":"")+ duration / MyDate.HOUR_IN_MILISECONDS + ":" + (minutes < 10 ? "0" + minutes : ""+minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
//        return duration / MyDate.HOUR_IN_MILISECONDS + ":" + (minutes < 10 ? "0" + minutes : "" + minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
    }
    public String formatDuration() { //Date date) {
        return formatDuration(duration, false);
//        int minutes = (duration % MyDate.HOUR_IN_MILISECONDS) / MyDate.MINUTE_IN_MILLISECONDS;
//        int days = duration/MyDate.DAY_IN_MILLISECONDS;
//        return (days>0?days+"d ":"")+ duration / MyDate.HOUR_IN_MILISECONDS + ":" + (minutes < 10 ? "0" + minutes : ""+minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
//        return duration / MyDate.HOUR_IN_MILISECONDS + ":" + (minutes < 10 ? "0" + minutes : "" + minutes); //TODO!!: gather all formatting in one place to ensure consistent presentation!
    }
}
