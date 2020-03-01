/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import static com.codename1.io.Util.readObject;
import static com.codename1.io.Util.writeObject;
import com.codename1.ui.Display;
import static com.todocatalyst.todocatalyst.AlarmType.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * keeps a shadow copy of the locally activated notifications, seems to be only
 * way to know what was activated, so the every previously set alarm can be
 * updated/cancelled etc
 *
 * @author Thomas
 */
public class LocalNotificationsShadowList implements Externalizable {
    //DONE?? if a repeat alarm triggers, any snoozed alarms for same objId should be cancelled otherwise confusing
    //TODO!!!! if the alarm for an item is snoozed till *after* the repeat alarm, then ?? (drop the snooze? drop the repeat alarm?!- yes because by snoozing the alarm, the user has dealt with the notification for this task)
    //DONE?? how to handle case where snooze interval is longer than repeat interval?
    //Invariant: the last alarm/waiting date in list (if eg a waiting alarm is postponed, it will only be set if smaller than the latest waiting alarm already set)
    //Invariant: if a alarm/waiting is changed, the corresponding repeat alarms are updated as well
    //Invariant: the time of snoozed alarms is left unchanged, unless a task is Done/Cancelled/Deleted
    //Invariant: local notifications for all alarms types are updated if the text of an item changes
    //Invariant: repeat alarms always shadow the alarm they repeat (except when the alarmhas expired)
    //Invariant: 

    final static String CLASS_NAME_NOTIFICATION_LIST = "notificationList";
//    private final static String NOTIF_LIST_FILE_ID = "notificationList";
    static final int MAX_NUMBER_LOCAL_NOTIFICATIONS = 64;

    ArrayList<NotificationShadow> activeLocalNotifsSorted = new ArrayList<>();

    public LocalNotificationsShadowList() {
//        super();
//        if (Storage.getInstance().exists(NOTIF_LIST_FILE_ID)) {
//            //TOD: catch any reading/format problems and recreate the file
//            notificationList = (LocalNotificationsShadowList) Storage.getInstance().readObject(NOTIF_LIST_FILE_ID);
//        }
//        if (notificationList == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
//            notificationList = new LocalNotificationsShadowList(); //create if none existed before
////                save(); //DON'T save before something is added
//        }
    }

    boolean isEmpty() {
        return activeLocalNotifsSorted.isEmpty();
    }

    int size() {
        return activeLocalNotifsSorted.size();
    }

    NotificationShadow get(int i) {
        return activeLocalNotifsSorted.get(i);
    }

    NotificationShadow remove(int i) {
        return activeLocalNotifsSorted.remove(i);
    }

    /**
     * insert a new notification. ??If maximum number is surpassed, remove from
     * list and cancel notification in system, just in case.??
     */
//        void addAlarm(NotificationShadow notif, String titleText, String bodyText) {
    private void insertAndSetSingleBaseAlarmSorted(String objectId, AlarmType type, Date alarmTime, String titleText, String bodyText) {
        NotificationShadow notif = new NotificationShadow(type, objectId, alarmTime);
        /**
         * the index of the search key, if it is contained in the list;
         * otherwise, (-(insertion point) - 1). The insertion point is defined
         * as the point at which the key would be inserted into the list: the
         * index of the first element greater than the key, or list.size() if
         * all elements in the list are less than the specified key. Note that
         * this guarantees that the return value will be >= 0 if and only if the
         * key is found.
         */
        int insertIndex = Collections.binarySearch(activeLocalNotifsSorted, notif, (v1, v2) -> {
            return FilterSortDef.compareLong(v1.alarmTime.getTime(), v2.alarmTime.getTime());
        });
        insertIndex = insertIndex < 0 ? -(insertIndex + 1) : insertIndex;
        activeLocalNotifsSorted.add(insertIndex, notif); //returns -1 for empty list
        AlarmHandler.getInstance().scheduleLocalNotification(notif.notificationId, titleText, bodyText, notif.alarmTime);
        //if there are too many local notifications set now, then remove exces ones (removing the last = the latest in time)
        while (activeLocalNotifsSorted.size() > MAX_NUMBER_LOCAL_NOTIFICATIONS) {
            removeAlarmAndRepeatAlarm(activeLocalNotifsSorted.get(activeLocalNotifsSorted.size() - 1).getObjectIdStr(),
                    activeLocalNotifsSorted.get(activeLocalNotifsSorted.size() - 1).type);
        }
    }

    /**
     * called when updating an item update (or add) the main and waiting alarms
     * and the corresponding repeat alarms. And cancel any previous alarms or
     * snoozed alarms. If newAlarm is null, simply cancels previous alarms of
     * type.
     */
    void addOrUpdateOrDeleteAlarmAndRepeat(String objectId, Date newAlarm, AlarmType type, String titleText, String bodyText) {
        //find and delete current alarm (and repeat alarms if any)
        //insert new alarm (if any, and within interval), and if so, find and set repeat alarms

        removeAlarmAndRepeatAlarm(objectId, type); //alwlays remove (eg if newAlarm is null)
//        removeAlarmAndRepeatAlarm(objectId, repeatType); //always remove repeatAlarm (eg if newAlarm is null)

//        if (newAlarm != null && newAlarm.getTime() > System.currentTimeMillis() //!= 0
        if (newAlarm != null && newAlarm.getTime() > MyDate.getNow() //!= 0
                && newAlarm.getTime() < getLastAlarmTime(type).getTime()) {
            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
//            if (getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)) {
            insertAndSetSingleBaseAlarmSorted(objectId, type, newAlarm, titleText, bodyText); //will cancel excess alarms

            if (repeatInterval > 0) {
                AlarmType repeatType = (type == AlarmType.notification) ? AlarmType.notificationRepeat : AlarmType.waitingRepeat;
//                    removeAlarmAndRepeatAlarm(objectId, NotificationType.notificationRepeat);
                insertAndSetSingleBaseAlarmSorted(objectId, repeatType,
                        new Date(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
                        titleText, bodyText);
            }
//            }
        }
    }

    /**
     * returns next (first) entry, null if list is empty
     *
     * @return
     */
    NotificationShadow getNextFutureAlarmN() {
//        long now = System.currentTimeMillis();
//        for (int i = 0, size = list.size(); i < size; i++) {
//            if (list.get(i).alarmTime.getTime() > now) {
//                return list.get(i);
//            }
//        }
//        return null; 
        return activeLocalNotifsSorted.isEmpty() ? null : activeLocalNotifsSorted.get(0);
    }

//    private int getNotificationIndexXXX(String notificationId) {
////        NotificationShadow notif;
//        for (int i = 0, size = activeLocalNotifsSorted.size(); i < size; i++) {
////            notif = list.get(i);
////            if (notif.notificationId.equals(notificationId)) {
//            if (activeLocalNotifsSorted.get(i).notificationId.equals(notificationId)) {
//                return i;
//            }
////            break;
//        }
//        return -1;
//    }
    /**
     * return the notification details for the notification with notificationId.
     * Used eg to obtain the details on an expired local notification. Returns
     * null if notificationId is not in list.
     *
     * @param notificationId
     * @return
     */
    NotificationShadow getNotification(String notificationId) {
//        return list.get(getNotificationIndex(notificationId));
        for (int i = 0, size = activeLocalNotifsSorted.size(); i < size; i++) {
//            notif = list.get(i);
//            if (notif.notificationId.equals(notificationId)) {
            if (activeLocalNotifsSorted.get(i).notificationId.equals(notificationId)) {
                return activeLocalNotifsSorted.get(i);
            }
//            break;
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    NotificationShadow removeAlarmAndCancelLocalNotificationImpl(int index) {
////            cancelLocalNotification(list.get(index).notificationId);
////        Display.getInstance().cancelLocalNotification(list.get(index).notificationId);
//        AlarmHandler.getInstance().cancelLocalNotification(list.get(index).notificationId);
//        return list.remove(index);
//    }
//</editor-fold>
//        void removeAlarm(String id, NotificationType type) {
    /**
     * remove the specific alarm with notificationId from the list and cancel
     * local notification
     *
     * @param notificationId
     */
    void removeAlarmAndCancelLocalNotification(String notificationId) {
//        NotificationShadow notif;
//        for (int i = 0, size = list.size(); i < size; i++) {
//            notif = list.get(i);
        NotificationShadow notif;
        Iterator<NotificationShadow> it = activeLocalNotifsSorted.iterator();
        while (it.hasNext()) {
            notif = it.next();
//                if (notif.notificationId.equals(id) && notif.type == type) {
            if (notif.notificationId.equals(notificationId)) {
//                    list.remove(i);
//                    cancelLocalNotification(list.get(i));
//                LocalNotificationsShadowList.this.removeAlarmAndCancelLocalNotification(i);
//                removeAlarmAndCancelLocalNotificationImpl(i);
//                Display.getInstance().cancelLocalNotification(notif.notificationId);
                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
                it.remove();
            }
            break;
        }
    }

    /**
     * remove both the main alarm and/or any associated repeat alarm (and cancel
     * the corresponding local notifications). Called when user cancels an
     * alarm. No need to remove snoozes since they .
     *
     * @param objectId
     * @param mainType
     */
    void removeAlarmAndRepeatAlarm(String notificationId) {
        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId, type);
        removeAlarmAndRepeatAlarm(objId, type);
    }

    /**
     * remove both the main alarm and/or any associated repeat alarm (and cancel
     * the corresponding local notifications). No need to remove snoozes since
     * they are mutually exclusive (when snoozing, other alarms are removed and
     * when updating an alarm, snooze alarms are removed???)???. Called when
     * user cancels an alarm
     *
     * @param objectId
     * @param mainType
     */
    void removeAlarmAndRepeatAlarm(String objectId, AlarmType mainType) {
//        for (int i = 0, size = list.size(); i < size; i++) {
        NotificationShadow notif;
        Iterator<NotificationShadow> it = activeLocalNotifsSorted.iterator();
        while (it.hasNext()) {
            notif = it.next();
//            notif = list.get(i);
            if (objectId != null && notif.notificationId.startsWith(objectId)) { //on very first save objectId may be null, so no previous alarm to remove

                if (notif.type == mainType
                        || (mainType == notification && notif.type == notificationRepeat)
                        || (mainType == waiting && notif.type == waitingRepeat) //                        
                        || mainType == snoozedNotif  //remove snooze just in case?!
                        || mainType == snoozedWaiting  //remove snooze just in case?!
                        ) {
//                        list.remove(i);
//                        cancelLocalNotification(notif.notificationId); //cancel the corresponding alarm
//                    removeAlarmAndCancelLocalNotification(objectId);
//                    removeAlarmAndCancelLocalNotificationImpl(i);
                    AlarmHandler.alarmLog("Local notification cancelled: \"" + notif.notificationId + "\"");

                    Display.getInstance().cancelLocalNotification(notif.notificationId);
                    it.remove();
//        return list.remove(index);                }
//            cancelLocalNotification(list.get(i));
                    //optimization: if Repeat is activated break loop after xxRepeat is found (since always later than main alarm)
                } //else?? what are the other options
            }
//            break;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void removeAlarmAndRepeatAlarmOLD(String objectId, AlarmType mainType) {
//        NotificationShadow notif;
//        for (int i = 0, size = list.size(); i < size; i++) {
//            notif = list.get(i);
//            if (notif.notificationId.startsWith(objectId)) {
//                if (notif.type == mainType
//                        || (mainType == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
//                        || (mainType == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)) {
////                        list.remove(i);
////                        cancelLocalNotification(notif.notificationId); //cancel the corresponding alarm
////                    removeAlarmAndCancelLocalNotification(objectId);
//                    removeAlarmAndCancelLocalNotificationImpl(i);
//                }
////            cancelLocalNotification(list.get(i));
//                //optimization: if Repeat is activated break loop after xxRepeat is found (since always later than main alarm)
//            }
////            break;
//        }
//    }
//</editor-fold>
    /**
     * remove and cancels *all* alarms with objectId (for the Item with
     * objectId). Alarm, Waiting, Snooze. Called if the item is deleted.
     *
     * @param objectId
     */
    void removeALLAlarmsForItem(String objectId) {
//        for (int i = 0, size = list.size(); i < size; i++) {
// NotificationShadow notif;
        Iterator<NotificationShadow> it = activeLocalNotifsSorted.iterator();
        while (it.hasNext()) {
//            notif = list.get(i);
//            notif = it.next();
//            NotificationShadow notif = list.get(i);
            NotificationShadow notif = it.next();
//            if (notif.notificationId.indexOf(objectId) == 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
            if (notif.isForItem(objectId)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//                String notifId = list.remove(i).notificationId; //remove entry
//                String notifId = it.remove().notificationId; //remove entry
                AlarmHandler.getInstance().cancelAlarm(notif.notificationId); //and cancel the corresponding alarm
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void removeALLAlarmsForItemOLD(String objectId) {
//        for (int i = 0, size = list.size(); i < size; i++) {
//            NotificationShadow notif = list.get(i);
////            if (notif.notificationId.indexOf(objectId) == 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//            if (notif.isForItem(objectId)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//                String notifId = list.remove(i).notificationId; //remove entry
//                AlarmHandler.getInstance().cancelAlarm(notifId); //and cancel the corresponding alarm
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void removeAllAlarmsForItemXXX(String objectId, boolean removeAlarm, boolean removeRepeat, boolean removeSnooze) {
//        for (int i = 0, size = list.size(); i < size; i++) {
//            NotificationShadow notif = list.get(i);
//            if (notif.notificationId.indexOf(objectId) >= 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//                String notifId = list.remove(i).notificationId; //remove entry
//                AlarmHandler.getInstance().cancelAlarm(notifId); //and cancel the corresponding alarm
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * remove any alarms for the item that comes before the snoozeTime
//     *
//     * @param objectId
//     * @param type
//     * @param snoozeTime
//     */
////        private void removeRepeatAlarmsBeforeSnoozeTime(String objectId, NotificationType type, Date snoozeTime) {
//    private void removeRepeatAlarmsBeforeSnoozeTime(String objectId, Date snoozeTime) {
//        //DONE!!!!!
////        assert false : "TOD";
//        for (int i = 0, size = list.size(); i < size; i++) {
//            NotificationShadow notif = list.get(i);
////                if (notif.notificationId.startsWith(objectId) && notif.type == type) {
//            //remove any alarms for the item that comes before the snoozeTime
//            //UI: snoozing an alarm for an item means no more alarms for that item until end of snooze time
//            if (notif.notificationId.startsWith(objectId) && notif.alarmTime.getTime() <= snoozeTime.getTime()) {
////                    removeAlarm(notif);
//                LocalNotificationsShadowList.this.removeAlarmAndCancelLocalNotificationImpl(i);
//            }
//            if (notif.alarmTime.getTime() > snoozeTime.getTime()) { //stop iterating through the list at first alarm time *after* the snooze time
//                break;
//            }
//        }
//    }
//</editor-fold>
    /**
     * snooze, or re-snooze, an alarm. Remove the alarm, as well as any repeat.
     * But removing a notification alarm will not remove a waiting alarm fo rthe
     * same item. Any existing snooze for the object will also be removed, no
     * matter if a notification or waiting was nsoozed. This means that there
     * can only be one single snoozed alarm for a task, you cannot at the same
     * time snooze a normal notification alarm and a waiting alarm, whichever is
     * snoozed last will remove other snoozes. However this is unlikely to
     * create problems since you would rarely have a normal and a waiting alarm
     * to close to each other than you would snooze one for so long, or so many
     * times, that it is still snoozed when the other is triggered and may be
     * snoozed. It could also be confusing and annoying for the user to snooze
     * one alarm and then have the snoozed alarm to appear.
     *
     * //TODO!!! If an alarm is snoozed so long that the next one is triggered,
     * eg a notification alarm is snoozed until a waiting alarm is triggered,
     * then should the snoozed alarm be removed? Probably yes otherwise again
     * confusing/annoying with two alarms repeating for the same task (and it
     * will be removed anyway if you snooze the waiting alarm).
     *
     * @param notificationId
     * @param type
     * @param snoozeExpireTime
     * @param snoozeTitleText
     * @param snoozeBodyText
     */
    void snoozeAlarm(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
//        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId);
        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
//        NotificationShadow notif;
//        //run through list and remove 
//        for (int i = 0, size = list.size(); i < size; i++) {
//            notif = list.get(i);
        NotificationShadow notif;
        Iterator<NotificationShadow> it = activeLocalNotifsSorted.iterator();
        while (it.hasNext()) {
            notif = it.next();
            if (notif.notificationId.equals(notificationId)) {
//                removeAlarmAndCancelLocalNotificationImpl(i); //remove alarm itself (could be a snooze)
//                Display.getInstance().cancelLocalNotification(notif.notificationId);
                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
                it.remove();
            } else if (notif.notificationId.startsWith(objId)
                    //remove any repeat but only of same type as alarm itself (if alarm is re-snoozed, the snoze is remove above) 
                    && ((type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting) //DONE: how to avoid removing a snooze for a waiting alarm?
                    || (type == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
                    || (type == AlarmType.notificationRepeat && notif.type == AlarmType.notification)
                    || (type == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)
                    || (type == AlarmType.waitingRepeat && notif.type == AlarmType.waiting))) {
//                removeAlarmAndCancelLocalNotificationImpl(i);
//                Display.getInstance().cancelLocalNotification(notif.notificationId);
                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
                it.remove();
            } //else nothing
        }
        insertAndSetSingleBaseAlarmSorted(objId, AlarmType.getSnoozedN(type), snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
//        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void snoozeAlarmOLD(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
////        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
//        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId);
//        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
//        NotificationShadow notif;
//        //run through list and remove
//        for (int i = 0, size = list.size(); i < size; i++) {
//            notif = list.get(i);
//            if (notif.notificationId.equals(notificationId)) {
//                removeAlarmAndCancelLocalNotificationImpl(i); //remove alarm itself (could be a snooze)
//            } else //remove any repeat but only of same type as alarm itself (if alarm is re-snoozed, the snoze is remove above)
//            if (notif.notificationId.startsWith(objId)
//                    && ((type == AlarmType.snooze) //how to avoid removing a snooze for a waiting alarm?
//                    || (type == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
//                    || (type == AlarmType.notificationRepeat && notif.type == AlarmType.notification)
//                    || (type == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)
//                    || (type == AlarmType.waitingRepeat && notif.type == AlarmType.waiting))) {
//                removeAlarmAndCancelLocalNotificationImpl(i);
//            }
//        }
//        insertAndSetSingleBaseAlarmSorted(objId, AlarmType.snooze, snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
////        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void snoozeAlarmOLD(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
//        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
////            addAlarm(new NotificationShadow(notificationId, snoozeExpireTime, NotificationType.snooze), titleText, bodyText); //Create new snoozed alarm
//        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notificationId);
////            String newNotifId = NotificationType.snooze.addTypeStrToStr(NotificationType.getNotifIdStrWithoutTypeStr(notificationId));
////            addAlarm(new NotificationShadow(newNotifId, snoozeExpireTime, NotificationType.snooze), titleText, bodyText); //Create new snoozed alarm
//        insertAndSetSingleBaseAlarmSorted(objId, NotificationType.snooze, snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
//        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
//    }
//</editor-fold>
    /**
     * called when adding a new alarm (and repeat alarm
     */
    void addAlarmAndRepeat(Item item, AlarmRecord alarm) {
        String titleText = item.makeNotificationTitleText(alarm.type);
        String bodyText = item.makeNotificationBodyText(alarm.type);
        addAlarmAndRepeat(item.getObjectIdP(), alarm.alarmTime, alarm.type, titleText, bodyText);
//        int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
//
//        ASSERT.that(alarm.type == AlarmType.notification || alarm.type == AlarmType.waiting);
//        ASSERT.that((getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)));
//
////        insertAndSetSingleBaseAlarmSorted(objectId, type, newAlarm, titleText, bodyText);
//        insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type, alarm.alarmTime, titleText, bodyText);
//
//        if (repeatInterval > 0) {
////            insertAndSetSingleBaseAlarmSorted(objectId, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
//            insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type,
//                    new Date(alarm.alarmTime.getTime() + repeatInterval * MyDate.MINUTE_IN_MILLISECONDS),
//                    titleText, bodyText);
//        }
    }

    void addAlarmAndRepeat(String objectId, Date newAlarm, AlarmType type, String titleText, String bodyText) {
        int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();

        ASSERT.that(type == AlarmType.notification || type == AlarmType.waiting || type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting, () -> "wrong alarmType=" + type);
//        ASSERT.that((getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)));

        insertAndSetSingleBaseAlarmSorted(objectId, type, newAlarm, titleText, bodyText);

        if (repeatInterval > 0 && (type != AlarmType.snoozedNotif && type != AlarmType.snoozedWaiting)) {
            insertAndSetSingleBaseAlarmSorted(objectId, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
                    new Date(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
                    titleText, bodyText);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//        /**
//         * updates any previously set local notifications with alarm times or
//         * new text. Also updates any repeat alarms set, as well as snoozed
//         * alarms?
//         */
//        void updateAlarmsXXX(String objectId, Date newAlarm, Date newWaitingAlarm, String titleText, String bodyText) {
//            Date latestSetAlarm = null;
//            if (newAlarm != null && newAlarm.getTime() != 0) {
//                latestSetAlarm = getLastAlarmTime(NotificationType.)
//            }
//            for (int i = 0, size = list.size(); i < size; i++) {
//                NotificationShadow notif = list.get(i);
//                //TOD optimization: should use startsWith since less costly than indexOf
////                if (notif.notificationId.indexOf(objectId) >= 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//                if (notif.notificationId.startsWith(objectId)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//                    NotificationType type = NotificationType.getTypeContainedInStr(notif.notificationId);
//                    cancelAlarm(notif.notificationId); //cancel the corresponding alarm
//                    //if new alarms is less than the highest currently set alarm, set a new one
//                    if (type == NotificationType.notification && newAlarm != null && newAlarm.getTime() != 0
//                            && newAlarm.getTime() < getLastAlarmTime(type).getTime()) {
//                        addAlarm(new NotificationShadow(type, objectId, newAlarm), titleText, bodyText);
//                    }
//                    String notifId = list.remove(i).notificationId; //remove entry
//                    cancelAlarm(notifId); //and cancel the corresponding alarm
//                }
//            }
//            list.add(insertIndex, notif);
//            setLocalNotification(WAITING_KEY, WAITING_KEY, titleText, bodyText);
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        void removeSurplusAlarmsXXX() {//NOT needed, instead always test that there is space before inserting new alarms
//            int i = list.size();
//            while (i >= 0 && list.size() > MAX_NUMBER_LOCAL_NOTIFICATIONS) {
//                AlarmHandler.NotificationShadow notif = list.get(i);
//                if (notif.type == NotificationType.snooze) {
//                    i--; //if too many alarms, don't remove a snoozed alarm
//                } else {
//                    //if we remove a Repeat alarm, also remove the original (which may later be used to test for the latest alarm set)
//                    if (notif.type == NotificationType.notificationRepeat) {
//                        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type);
////                        removeAlarm(NotificationType.notification.addTypeStrToStr(objId), NotificationType.notification);
//                        removeAlarm(NotificationType.notification.addTypeStrToStr(objId));
//                    } else if (notif.type == NotificationType.waitingRepeat) {
//                        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type);
////                        removeAlarm(NotificationType.waiting.addTypeStrToStr(objId), NotificationType.waiting);
//                        removeAlarm(NotificationType.waiting.addTypeStrToStr(objId));
//                    }
//                    //remove the alarm itself
////                    removeAlarm(notif.notificationId, notif.type);
//                    removeAlarm(notif.notificationId);
////                    removeAllAlarmsOtherThanSnooze(notif.notificationId); //if we need to remove a repat alarm, also remove the
////                    cancelLocalNotifications(list.get(i).);
//                    list.remove(i);
//                }
//            }
//        }
//</editor-fold>
    /**
     * return the latest time of notifications of type type. If no such type,
     * returns Date(MyDate.MAX_DATE) to signal that any new notification of this
     * type can be inserted
     *
     * @param type
     * @return
     */
    Date getLastAlarmTime(AlarmType type) {
        int i = activeLocalNotifsSorted.size() - 1;
        while (i >= 0 && activeLocalNotifsSorted.get(i).type != type) {
            i--;
        }
        if (i >= 0) {
            return activeLocalNotifsSorted.get(i).alarmTime;
        } else {
//                return null;
            return new Date(MyDate.MAX_DATE); //if 
        }
    }

    int getNumberAvailableLocalNotificationSlots() {
//        long now = System.currentTimeMillis();
//        int firstFutureSlot = 0;
//        for (int i = 0, size = list.size(); i < size; i++) {
//            if (list.get(i).alarmTime.getTime() > now) {
//                firstFutureSlot = i;
//                break;
//            }
//        }
//        return AlarmHandler.MAX_NUMBER_LOCAL_NOTIFICATIONS - list.size() + firstFutureSlot;
        return MAX_NUMBER_LOCAL_NOTIFICATIONS - activeLocalNotifsSorted.size();
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        writeObject(activeLocalNotifsSorted, out);
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        activeLocalNotifsSorted = (ArrayList) readObject(in);
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME_NOTIFICATION_LIST;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    Date getLastNormalOrWaitingAlarmTimeXXX() {
//        int i = list.size() - 1;
//        while (i >= 0 && (list.get(i).type != NotificationType.notification || list.get(i).type != NotificationType.waiting)) {
//            i--;
//        }
//        if (i >= 0) {
//            return list.get(i).alarmTime;
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * get next alarm to start eg inAppTimer on. Returns null if no more alarms
//     * in list
//     *
//     * @return
//     */
//    Date getFirstAlarmTimeOfAnyTypeXXX() {
//        if (!list.isEmpty()) {
//            return list.get(0).alarmTime;
//        }
//        return null;
//    }
//
//    NotificationShadow getFirstAlarmNotificationXXX() {
//        if (!list.isEmpty()) {
//            return list.get(0);
//        }
//        return null;
//    }
//
//    /**
//     * remove and return all alarms for the given time. Used to display all
//     * alarms that has expired on the same time (eg it two inAppTimer alarms
//     * expire on same time, then they should be shown one after another, not
//     * just one of them)
//     *
//     * @param alarmTime
//     * @return
//     */
//    List<NotificationShadow> removeAndReturnAllAlarmsForTimeXXX(Date alarmTime) {
////            int i = list.size() - 1;
//        List<NotificationShadow> result = new ArrayList<>();
//        for (int i = 0, size = list.size(); i < size; i++) { //>= 0 && list.get(i).alarmTime.getTime() == alarmTime.getTime()) {
//            if (list.get(i).alarmTime.getTime() == alarmTime.getTime()) {
////                result.add(list.get(i));
//                result.add(list.remove(i));
//            }
//            if (list.get(i).alarmTime.getTime() > alarmTime.getTime()) { //when we encounter first alarm after alarmTime, we're done
////                    return result;
//                break;
//            }
//        }
////            return new ArrayList<>();
//        return result;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * remove ALL alarms for objectId, e.g. if the Item is deleted
//     *
//     * @param objectId
//     */
//    void removeAllAlarmsXXX(String objectId) { //done by deleteAllAlarmsForItem(String objectId)
////            removeAlarm(id, NotificationType.notification);
////            removeAlarm(id, NotificationType.notificationRepeat);
////            removeAlarm(id, NotificationType.waiting);
////            removeAlarm(id, NotificationType.waitingRepeat);
////            removeAlarm(objectId, NotificationType.);
////            removeAllAlarmsOtherThanSnooze(objectId);
//        removeAlarmAndCancelLocalNotification(NotificationType.notification.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.notificationRepeat.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.waiting.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.waitingRepeat.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.snooze.addTypeStrToStr(objectId));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * use to remove all other set alarms when cancelling one of the alarms
//     * (other than snoozed which are only stored locally)
//     *
//     * @param objectId
//     */
//    void removeAllAlarmsOtherThanSnoozeXXX(String objectId) {
////            removeAlarm(id, NotificationType.notification);
////            removeAlarm(id, NotificationType.notificationRepeat);
////            removeAlarm(id, NotificationType.waiting);
////            removeAlarm(id, NotificationType.waitingRepeat);
////            removeAlarm(id, NotificationType.snooze);
//        removeAlarmAndCancelLocalNotification(NotificationType.notification.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.notificationRepeat.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.waiting.addTypeStrToStr(objectId));
//        removeAlarmAndCancelLocalNotification(NotificationType.waitingRepeat.addTypeStrToStr(objectId));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * called when updating an item update (or add) the main and waiting alarms
//     * and the corresponding repeat alarms. And cancel any previous alarms or
//     * snoozed alarms.
//     */
////        void addOrUpdateAlarmAndRepeat(String objectId, NotificationType type, Date newAlarm, Date newWaitingAlarm,
//    void addOrUpdateAlarmAndWaitingXXX(String objectId, Date newAlarm, Date newWaitingAlarm,
//            String titleText, String bodyText) {
//        //find and delete current alarm (and repeat alarms if any)
//        //insert new alarm (if any, and within interval), and if so, find and set repeat alarms
//        removeAlarmAndRepeatAlarm(objectId, NotificationType.notification); //alwlays remove (eg if newAlarm is null)
//        if (newAlarm != null && newAlarm.getTime() != 0
//                && newAlarm.getTime() < getLastAlarmTime(NotificationType.notification).getTime()) {
//            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
//            if (getNumberFreeSlots() >= (repeatInterval > 0 ? 2 : 1)) {
//                insertAndSetSingleBaseAlarmSorted(objectId, NotificationType.notification, newAlarm, titleText, bodyText);
//
//                if (repeatInterval > 0) {
////                    removeAlarmAndRepeatAlarm(objectId, NotificationType.notificationRepeat);
//                    insertAndSetSingleBaseAlarmSorted(objectId, NotificationType.notificationRepeat,
//                            new Date(newAlarm.getTime() + repeatInterval * MyDate.MINUTE_IN_MILLISECONDS),
//                            titleText, bodyText);
//                }
//            }
//        }
//
//        removeAlarmAndRepeatAlarm(objectId, NotificationType.waiting); //also remove waitingRepeat
//        if (newWaitingAlarm != null && newWaitingAlarm.getTime() != 0
//                && newWaitingAlarm.getTime() < getLastAlarmTime(NotificationType.waiting).getTime()) {
//            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
//            if (getNumberFreeSlots() >= (repeatInterval > 0 ? 2 : 1)) {
//                insertAndSetSingleBaseAlarmSorted(objectId, NotificationType.waiting, newWaitingAlarm, titleText, bodyText);
//
//                if (repeatInterval > 0) {
////                    removeAlarmAndRepeatAlarm(objectId, NotificationType.waitingRepeat);
//                    insertAndSetSingleBaseAlarmSorted(objectId, NotificationType.waitingRepeat,
//                            new Date(newWaitingAlarm.getTime() + repeatInterval * MyDate.MINUTE_IN_MILLISECONDS),
//                            titleText, bodyText);
//                }
//            }
//            //UI: when updating alarms for an item, any snoozed alarms for items are cancelled
//            removeAlarmAndCancelLocalNotification(NotificationType.snooze.addTypeStrToStr(objectId));
////                updateAlarmTime(objectId, NotificationType.notificationRepeat, );
//        }
//    }
//
//    NotificationShadow removeNextXXX() {
//        return list.remove(0);
//    }
//</editor-fold>
    /**
     * remove all expired alarms. Called eg on start up of app before remove any
     * alarms that were dealt with (as local notifications) while the app was
     * not running.
     */
//    void removeExpiredAlarmsXXX() {
//        long now = System.currentTimeMillis();
//        while (!list.isEmpty() && list.get(0).alarmTime.getTime() < now) {
//            list.remove(0);
//        }
//    }
    void save() {
        Storage.getInstance().writeObject(AlarmHandler.NOTIF_LIST_FILE_ID, LocalNotificationsShadowList.this);
    }

    /**
     * cancel and remove all previously set local notifications
     */
    void cancelAndRemoveAllAvailableLocalNotifications() {
        for (int i = 0, size = activeLocalNotifsSorted.size(); i < size; i++) {
//            Display.getInstance().cancelLocalNotification(list.get(i).notificationId);
            AlarmHandler.getInstance().cancelLocalNotification(activeLocalNotifsSorted.get(i).notificationId);
        }
        activeLocalNotifsSorted.clear();
    }

}
