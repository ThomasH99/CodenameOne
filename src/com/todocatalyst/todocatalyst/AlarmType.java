/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author Thomas
 */
enum AlarmType {
    notification("-[ALARM]"), //normal alarm/reminder
    waiting("-[WAITING]"), //Waiting alarm/reminder
    notificationRepeat("-[ALARM-REP]"), //repeat of normal alarm/reminder
    waitingRepeat("-[WAITING-REP]"), //repeat of Waiting alarm/reminder
    //    snooze("-[ALM-SNOOZE]"), //NOT USED?!snooze of normal alarm/reminder
    snoozedNotif("-[ALM-SNOOZE]"), //snooze of normal alarm/reminder
    snoozedWaiting("-[WAI-SNOOZE]"); //snooze of Waiting alarm/reminder
    String text; //the text (eg "-[ALARM]") added to objectIds to get notificationId

    private AlarmType(String text) {
        this.text = text;
    }

    boolean isSnooze() {
        return this == snoozedNotif || this == snoozedWaiting;
    }

    boolean isReminder() {
        return this == notification || this == notificationRepeat;
    }

    boolean isWaitingReminder() {
        return this == waiting || this == waitingRepeat;
    }

    /**
     * returns the NotificationType corresponding to the String representation
     * of the the type. For example str="er82kn?,kjsfd-[ALARM]" will return
     * NotificationType.notification.
     *
     * @param str
     * @return
     */
    static AlarmType getTypeContainedInStr(String str) {
//            if (str.indexOf(notification.text)!=-1) return notification;
//            if (str.indexOf(waiting.text)!=-1) return waiting;
//            if (str.indexOf(notificationRepeat.text)!=-1) return notificationRepeat;
//            if (str.indexOf(waitingRepeat.text)!=-1) return waitingRepeat;
//            if (str.indexOf(snooze.text)!=-1) return snooze;
        if (str.endsWith(notification.text)) {
            return notification;
        }
        if (str.endsWith(waiting.text)) {
            return waiting;
        }
        if (str.endsWith(notificationRepeat.text)) {
            return notificationRepeat;
        }
        if (str.endsWith(waitingRepeat.text)) {
            return waitingRepeat;
        }
//        if (str.endsWith(snooze.text)) {
//            return snooze;
//        }
        if (str.endsWith(snoozedWaiting.text)) {
            return snoozedWaiting;
        }
        if (str.endsWith(snoozedNotif.text)) {
            return snoozedNotif;
        }
        return null;
    }

    /**
     * return the right type of snooze for a given alarm type
     *
     * @param alarm
     * @return
     */
    static AlarmType getSnoozedN(AlarmType alarm) {
        if (alarm == notification || alarm == notificationRepeat) {
            return snoozedNotif;
        } else if (alarm == waiting || alarm == waitingRepeat) {
            return snoozedWaiting;
        } else if (alarm == snoozedNotif || alarm == snoozedWaiting) {
            return alarm; //return the same type for already snoozed alarm
        } else {
            return null;
        }
    }

    static AlarmType getAlarmTypeFromSnoozedN(AlarmType snoozed) {
        if (snoozed == snoozedNotif) {
            return notification;
        } else if (snoozed == snoozedWaiting) {
            return waiting;
        } else {
            return null;
        }
    }

    static String getObjectIdStrWithoutTypeStr(String str, AlarmType type) {
        int index = -1;
        if ((index = str.indexOf(type.text)) != -1) {
//                return str.substring(0, index - 1);
            return str.substring(0, index);
        } else {
            return str;
        }
    }

    private static int getNotifIdStrIndex(String notifId) {
        int temp;
        if ((temp = notifId.indexOf(notification.text)) != -1) {
            return temp;
        }
        if ((temp = notifId.indexOf(waiting.text)) != -1) {
            return temp;
        }
        if ((temp = notifId.indexOf(notificationRepeat.text)) != -1) {
            return temp;
        }
        if ((temp = notifId.indexOf(waitingRepeat.text)) != -1) {
            return temp;
        }
//        if ((temp = notifId.indexOf(snooze.text)) != -1) {
//            return temp;
//        }
        if ((temp = notifId.indexOf(snoozedNotif.text)) != -1) {
            return temp;
        }
        if ((temp = notifId.indexOf(snoozedWaiting.text)) != -1) {
            return temp;
        }
        return -1;
    }

    static String getObjectIdStrWithoutTypeStr(String notifId) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            int index = -1;
//            int temp;
//            if ((temp = str.indexOf(notification.text)) != -1) {
//                index = temp;
//            }
//            if ((temp = str.indexOf(waiting.text)) != -1) {
//                index = temp;
//            }
//            if ((temp = str.indexOf(notificationRepeat.text)) != -1) {
//                index = temp;
//            }
//            if ((temp = str.indexOf(waitingRepeat.text)) != -1) {
//                index = temp;
//            }
//            if ((temp = str.indexOf(snooze.text)) != -1) {
//                index = temp;
//            }
//</editor-fold>
        int index = getNotifIdStrIndex(notifId);
        if (index >= 0) {
//                return notifId.substring(index, notifId.length()-1);
            return notifId.substring(0, index);
        } else {
            return null;
        }
    }

    /**
     * add the String for this type to the String str. For example
     * str="er82kn?,kjsfd", type==notification will return
     * "er82kn?,kjsfd-[ALARM]"
     *
     * @param str
     * @return
     */
    String addTypeStrToStr(String str) {
//            return str + this.toString();
        return str + text;
    }

}
