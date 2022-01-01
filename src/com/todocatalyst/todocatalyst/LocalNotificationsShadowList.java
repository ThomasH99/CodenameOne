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
import com.codename1.notifications.LocalNotification;
import com.codename1.ui.Display;
import static com.todocatalyst.todocatalyst.AlarmHandler.alarmLog;
import static com.todocatalyst.todocatalyst.AlarmType.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
    private final static String NOTIF_LIST_FILE_ID = "notificationList";
//    private AlarmHandler alarmHandler; //reference back to alarmhandler
    private int maxNumberLocalNotifications;
//    private Set<String> localNotifs = new HashSet<>();
    private ArrayList<NotificationShadow> localNotifsActiveSorted;
    private AlarmInAppAlarmHandler inAppTimer = new AlarmInAppAlarmHandler(); // = new AlarmInAppAlarmHandler(notificationList);

//    public LocalNotificationsShadowList(AlarmHandler alarmHandler) {
    public LocalNotificationsShadowList() {
//        this.alarmHandler=alarmHandler;
//        setAlarmHandler(alarmHandler);
        super();
        if (Storage.getInstance().exists(NOTIF_LIST_FILE_ID)) {
            //TOD: catch any reading/format problems and recreate the file
            localNotifsActiveSorted = (ArrayList<NotificationShadow>) Storage.getInstance().readObject(NOTIF_LIST_FILE_ID);
        }
        if (localNotifsActiveSorted == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
            localNotifsActiveSorted = new ArrayList<NotificationShadow>(); //create if none existed before
//                save(); //DON'T save before something is added
        }
        updateWithFutureAlarms(new MyDate(), false);
//        saveLocalNotificationShadow(); //done in updateWith
//        inAppTimer = new AlarmInAppAlarmHandler();
//        inAppTimer.updateInAppTimerOnNextcomingAlarm(localNotifsActiveSorted); //DONE in updateWithFutureAlarms() above
    }

//    private List<NotificationShadow> getLocalNotifsActiveSortedXXX() {
//        while(localNotifsActiveSorted!=null&&localNotifsActiveSorted.size()>0) {
//            if(localNotifsActiveSorted.get(0).alarmTime.getTime()<MyDate.getCurrentTimeShift())
//                localNotifsActiveSorted.remove(0); //remove expired notifications
//        }
//    }
    /**
     * remove expired localNotifs before each access to the variable
     */
    private void removeExpiredLocalNotifsActive() {
//        while (localNotifsActiveSorted != null && localNotifsActiveSorted.size() > 0 && localNotifsActiveSorted.get(0).alarmTime.getTime() < MyDate.getCurrentTimeShift()) {
        if (localNotifsActiveSorted != null) {
            Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
            while (it.hasNext()) {
                NotificationShadow n = it.next();
//            if(localNotifsActiveSorted.get(0).alarmTime.getTime()<MyDate.getCurrentTimeShift())
                if (n.alarmTime.getTime() < MyDate.currentTimeMillis()) { //            localNotifsActiveSorted.remove(0); //remove expired notifications
                    it.remove(); //remove expired notifications
                }
            }
        }
    }

    public List<NotificationShadow> getUpdatedListOfAlarmRecords() {
        removeExpiredLocalNotifsActive();
        return localNotifsActiveSorted;
    }

    /**
     * sets a local notification
     *
     * @param item
     * @param notificationText
     * @param alarmDate
     */
    private static void scheduleLocalNotification(NotificationShadow notificationShadow, String titleText, String bodyText) {
        scheduleLocalNotification(notificationShadow.notificationId, titleText, bodyText, notificationShadow.alarmTime);
    }

    private static void scheduleLocalNotification(String notificationId, String titleText, String bodyText, Date alarmDate) {
        Display.getInstance().cancelLocalNotification(notificationId); //always cancel any previous set local notifs, just in case
        alarmLog("Schedule local notification " + notificationId + " alarm=" + MyDate.formatDateTimeNew(alarmDate) + " for \"" + titleText + "\" (" + bodyText + ")");
        if (alarmDate.getTime() < MyDate.currentTimeMillis()) {
            return;
        }
        LocalNotification n = new LocalNotification();
        n.setId(notificationId);
        n.setAlertTitle(titleText);
        n.setAlertBody(bodyText);
//        n.setAlertSound("/" + MyPrefs.alarmSoundFileLocalNotif.getString());
//        n.setAlertSound("/" + MyPrefs.alarmSoundFile.getString());
        n.setAlertSound("/" + MyPrefs.alarmSoundFileLocalNotif.getString());
        Display.getInstance().scheduleLocalNotification(n, alarmDate.getTime(), LocalNotification.REPEAT_NONE);
    }

    private List<NotificationShadow> makeNotifShadows(List<AlarmRecord> futureAlarms) {
        List<NotificationShadow> list = new ArrayList<NotificationShadow>();
        for (AlarmRecord r : futureAlarms) {
            list.add(new NotificationShadow(r));
        }
        return list;
    }

    /**
     * call with updated list of future alarms to update local notifications and
     * inAppTimer
     *
     * @param futureAlarms
     */
//    public void updateWithFutureAlarms(List<AlarmRecord> futureAlarms) {
//    public void updateWithFutureAlarms(boolean forceReload) {
    public void updateWithFutureAlarms(Date now, boolean forceReload) {
        List<AlarmRecord> futureAlarms = DAO.getInstance().fetchFutureAlarmRecords(now, forceReload);
        //optimization: compare futureAlarms to previously set alarms and only update if changes, and only for the changes to make less calls to the OS
        if (Config.TEST) {
            List<NotificationShadow> added = makeNotifShadows(futureAlarms);
            List<NotificationShadow> removed = new ArrayList(localNotifsActiveSorted);
            removed.removeAll(added);
            added.removeAll(localNotifsActiveSorted);
        }
        cancelAndRemoveAllAvailableLocalNotifications();
        insertAndSetAllAlarmsAndUpdateInAppTimer(futureAlarms);
//        for (AlarmRecord alarmRecord : futureAlarms) {
//            insertAndSetSingleBaseAlarmSorted(alarmRecord.item.getGuid(), alarmRecord.type, alarmRecord, NOTIF_LIST_FILE_ID, NOTIF_LIST_FILE_ID, true)
//        }
    }

    static final int MAX_NUMBER_LOCAL_NOTIFICATIONS = 64;

    private void saveLocalNotificationShadowAndUpdateInAppTimer() {
        saveLocalNotificationShadowAndUpdateInAppTimer(new MyDate());
    }

    private void saveLocalNotificationShadowAndUpdateInAppTimer(Date now) {
//TODO: risk that now is later than the now used when setting the local notifs=> localNotif activated but not saved (but consequence will simply be that an alarm expiring almost 'now' cannot be cancelled later
//List<NotificationShadow> localNotifsActiveSorted =getLocalNotifsActiveSorted();
        removeExpiredLocalNotifsActive();
//        while (false&&localNotifsActiveSorted.size() > 0 && localNotifsActiveSorted.get(0).alarmTime.getTime() < now.getTime()) {
//            NotificationShadow notif = localNotifsActiveSorted.remove(0); //remove expired notifs
//            if (Config.TEST) {
//                ASSERT.that("an expired alarm left in localNotifsActiveSorted=" + notif);
//            }
//        }
        inAppTimer.updateInAppTimerOnNextcomingAlarm(localNotifsActiveSorted);
        //TOD: catch any reading/format problems and recreate the file
        Storage.getInstance().writeObject(NOTIF_LIST_FILE_ID, localNotifsActiveSorted);
    }

    /**
     * insert a new notification. ??If maximum number is surpassed, remove from
     * list and cancel notification in system, just in case.??
     */
//        void addAlarm(NotificationShadow notif, String titleText, String bodyText) {
    private void insertAndSetAllAlarmsAndUpdateInAppTimer(List<AlarmRecord> alarmRecords) {
        for (AlarmRecord alarmRecord : alarmRecords) {
            insertAndSetSingleBaseAlarmSorted(alarmRecord, false);
        }
        saveLocalNotificationShadowAndUpdateInAppTimer();
    }

    private void insertAndSetSingleBaseAlarmSorted(String guid, AlarmType type, Date alarmTime, String titleText, String bodyText, boolean saveNotifList) {
        ASSERT.that(alarmTime.getTime() > System.currentTimeMillis(), () -> "setting alarm notification in the past!, guid=" + guid + "; type=" + type + "; alarmTime=" + alarmTime);
        //first remove any possibly previously set alarms (may also free up space for a new notification)
        removeAlarmNotifications(guid, type.isWaitingReminder());
        NotificationShadow notif = new NotificationShadow(type, guid, alarmTime);

//        do {
        /**
         * the index of the search key, if it is contained in the list;
         * otherwise, (-(insertion point) - 1). The insertion point is defined
         * as the point at which the key would be inserted into the list: the
         * index of the first element greater than the key, or list.size() if
         * all elements in the list are less than the specified key. Note that
         * this guarantees that the return value will be >= 0 if and only if the
         * key is found.
         */
        removeExpiredLocalNotifsActive();
        int idx = Collections.binarySearch(localNotifsActiveSorted, notif, (v1, v2) -> {
            return FilterSortDef.compareLong(v1.alarmTime.getTime(), v2.alarmTime.getTime());
        });
        int insertIndex = idx < 0 ? -(idx + 1) : idx;
        if (insertIndex < MAX_NUMBER_LOCAL_NOTIFICATIONS - (MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt() > 0 ? 2 : 1)) {
            //we can schedule the new alarm but may need to remove excess ones
            localNotifsActiveSorted.add(insertIndex, notif); //returns -1 for empty list
            if (saveNotifList) {
                saveLocalNotificationShadowAndUpdateInAppTimer();
            }
            scheduleLocalNotification(notif, titleText, bodyText);
            //if there are too many local notifications set now, then remove excess ones (removing the last (both notif AND repeat) = the latest in time)
            while (localNotifsActiveSorted.size() >= MAX_NUMBER_LOCAL_NOTIFICATIONS) {
                removeAlarmNotifications(
                        localNotifsActiveSorted.get(localNotifsActiveSorted.size() - 1).getGuidStr(),
                        localNotifsActiveSorted.get(localNotifsActiveSorted.size() - 1).getAlarmType().isWaitingReminder());
            }
//<editor-fold defaultstate="collapsed" desc="comment">

////             if (MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt() > 0 && (type != AlarmType.snoozedNotif && type != AlarmType.snoozedWaiting)) {
//                if (false && MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt() > 0) {
////                this.insertAndSetSingleBaseAlarmSorted(guid, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
////                        new MyDate(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
////                        titleText, bodyText);
//                    notif = new NotificationShadow(type, guid, new MyDate(alarmTime.getTime() + MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt() * MyDate.MINUTE_IN_MILLISECONDS));
//                } else {
//                    notif = null;
//                }
//            } else {
//                //do nothing, we already have max many noti
//                notif = null;
//            }
//        } while (false&&notif != null);
//</editor-fold>
        }
    }

    private void insertAndSetSingleBaseAlarmSorted(AlarmRecord alarmRecord) {
        insertAndSetSingleBaseAlarmSorted(alarmRecord.item.getGuid(), alarmRecord.type, alarmRecord.alarmTime,
                alarmRecord.item.makeNotificationTitleText(alarmRecord.type), alarmRecord.item.makeNotificationBodyText(alarmRecord.type), true);
    }

    private void insertAndSetSingleBaseAlarmSorted(AlarmRecord alarmRecord, boolean saveNotifList) {
        insertAndSetSingleBaseAlarmSorted(alarmRecord.item.getGuid(), alarmRecord.type, alarmRecord.alarmTime,
                alarmRecord.item.makeNotificationTitleText(alarmRecord.type), alarmRecord.item.makeNotificationBodyText(alarmRecord.type), saveNotifList);
    }

    private void insertAndSetSingleBaseAlarmSorted(String guid, AlarmType type, Date alarmTime, String titleText, String bodyText) {
        insertAndSetSingleBaseAlarmSorted(guid, type, alarmTime, titleText, bodyText, true);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void addAlarmAndRepeatXXX(Item item) {
//        List<AlarmRecord> alarmRecords = item.getAllFutureAlarmRecordsSorted(); //sort to ensure first-coming alarm is set before a later one
//        for (AlarmRecord alarmRecord : alarmRecords) {
//            String titleText = item.makeNotificationTitleText(alarmRecord.type);
//            String bodyText = item.makeNotificationBodyText(alarmRecord.type);
////            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt();
//
////            ASSERT.that(type == AlarmType.notification || type == AlarmType.waiting || type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting, () -> "wrong alarmType=" + type);
//            insertAndSetSingleBaseAlarmSorted(item.getGuid(), alarmRecord.type, alarmRecord.alarmTime, titleText, bodyText);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (repeatInterval > 0 && (type != AlarmType.snoozedNotif && type != AlarmType.snoozedWaiting)) {
////                this.insertAndSetSingleBaseAlarmSorted(guid, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
////                        new MyDate(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
////                        titleText, bodyText);
////            }
////            scheduleLocalNotification(titleText, titleText, bodyText, alarmDate);
////</editor-fold>
//        }
//    }
//</editor-fold>
    private void removeAllAlarmNotifications(String guid) {
        removeAlarmNotifications(guid, false);
        removeAlarmNotifications(guid, true);
    }

    public void updateAlarmNotifications(Item item) {
        List<AlarmRecord> alarmRecords = item.getAllFutureAlarmRecordsSorted(); //sort to ensure first-coming alarm is set before a later one
        if (alarmRecords.isEmpty()) { //will be empty if eg task was Completed/Cancelled/Deleted
            removeAllAlarmNotifications(item.getGuid());
        } else {
            for (AlarmRecord alarmRecord : alarmRecords) {
//                String titleText = item.makeNotificationTitleText(alarmRecord.type);
//                String bodyText = item.makeNotificationBodyText(alarmRecord.type);
//            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt();
//            ASSERT.that(type == AlarmType.notification || type == AlarmType.waiting || type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting, () -> "wrong alarmType=" + type);
//                insertAndSetSingleBaseAlarmSorted(item.getGuid(), alarmRecord.type, alarmRecord.alarmTime, titleText, bodyText);
                insertAndSetSingleBaseAlarmSorted(alarmRecord, false); //false: don't save for every update but once below
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (repeatInterval > 0 && (type != AlarmType.snoozedNotif && type != AlarmType.snoozedWaiting)) {
//                this.insertAndSetSingleBaseAlarmSorted(guid, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
//                        new MyDate(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
//                        titleText, bodyText);
//            }
//            scheduleLocalNotification(titleText, titleText, bodyText, alarmDate);
//</editor-fold>
            }
//            if (alarmRecords.size() > 0) {
            saveLocalNotificationShadowAndUpdateInAppTimer();
//            }
        }
    }

    static void cancelLocalNotification(String notificationId) {
        AlarmHandler.alarmLog("Local notifiation cancelled: \"" + notificationId + "\"");
        Display.getInstance().cancelLocalNotification(notificationId);
    }

    /**
     * cancel and remove all previously set local notifications
     */
    void cancelAndRemoveAllAvailableLocalNotifications() {
        removeExpiredLocalNotifsActive();
        for (int i = 0, size = localNotifsActiveSorted.size(); i < size; i++) {
//            Display.getInstance().cancelLocalNotification(list.get(i).notificationId);
//            AlarmHandler.getInstance().cancelLocalNotification(localNotifsActiveSorted.get(i).notificationId);
            cancelLocalNotification(localNotifsActiveSorted.get(i).notificationId);
        }
        localNotifsActiveSorted.clear();
    }

    void removeAlarmAndRepeatNotification(Item item, boolean waitingAlarm) {
        removeAlarmNotifications(item.getGuid(), waitingAlarm);
    }

    /**
     * will remove both normal notification AND repeat notification of the given
     * type (normal alarm or waiting alarm)
     *
     * @param guid
     * @param waitingAlarm
     */
    void removeAlarmNotifications(String guid, boolean waitingAlarm) {
        removeExpiredLocalNotifsActive();
        Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
        while (it.hasNext()) {
            NotificationShadow notif = it.next();
//            if (notif.notificationId.startsWith(guid)&&waitingAlarm?getTypeContainedInStr(notif.notificationId)) { //on very first save objectId may be null, so no previous alarm to remove
            if (isForItem(notif.notificationId, guid)) {
                if ((!waitingAlarm && isNotifAlarm(notif.notificationId)) || (waitingAlarm && isWaitingAlarm(notif.notificationId))) {
                    AlarmHandler.alarmLog("Local notification cancelled: \"" + notif.notificationId + "\"");
                    Display.getInstance().cancelLocalNotification(notif.notificationId);
                    it.remove();
                }
            }
        }
    }

    int size() {
        removeExpiredLocalNotifsActive();
        return localNotifsActiveSorted.size();
    }

    NotificationShadow get(int i) {
//        updateLocalNotifsActiveSorted(); //recipe for disaster
        return localNotifsActiveSorted.get(i);
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {
        removeExpiredLocalNotifsActive();
        writeObject(localNotifsActiveSorted, out);
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        localNotifsActiveSorted = (ArrayList) readObject(in);
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME_NOTIFICATION_LIST;
    }

    //////////////////////////////////////////////////////////
//    /**
//     * called when updating an item update (or add) the main and waiting alarms
//     * and the corresponding repeat alarms. And cancel any previous alarms or
//     * snoozed alarms. If newAlarm is null, simply cancels previous alarms of
//     * type.
//     */
//    void addOrUpdateOrDeleteAlarmAndRepeatXXX(String guid, Date newAlarm, AlarmType type, String titleText, String bodyText) {
//        //find and delete current alarm (and repeat alarms if any)
//        //insert new alarm (if any, and within interval), and if so, find and set repeat alarms
//
//        this.removeAlarmAndRepeatAlarm(guid, type); //alwlays remove (eg if newAlarm is null)
////        removeAlarmAndRepeatAlarm(objectId, repeatType); //always remove repeatAlarm (eg if newAlarm is null)
//
////        if (newAlarm != null && newAlarm.getTime() > System.currentTimeMillis() //!= 0
//        if (newAlarm != null && newAlarm.getTime() > MyDate.currentTimeMillis() //!= 0
//                && newAlarm.getTime() < getLastAlarmTime(type).getTime()) {
//            int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt();
////            if (getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)) {
//            this.insertAndSetSingleBaseAlarmSorted(guid, type, newAlarm, titleText, bodyText); //will cancel excess alarms
//
//            if (repeatInterval > 0) {
//                AlarmType repeatType = (type == AlarmType.notification) ? AlarmType.notificationRepeat : AlarmType.waitingRepeat;
////                    removeAlarmAndRepeatAlarm(objectId, NotificationType.notificationRepeat);
//                this.insertAndSetSingleBaseAlarmSorted(guid, repeatType,
//                        new MyDate(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
//                        titleText, bodyText);
//            }
////            }
//        }
//    }
//
//    /**
//     * returns next (first) entry, null if list is empty
//     *
//     * @return
//     */
//    NotificationShadow getNextFutureAlarmNXXX() {
////        long now = System.currentTimeMillis();
////        for (int i = 0, size = list.size(); i < size; i++) {
////            if (list.get(i).alarmTime.getTime() > now) {
////                return list.get(i);
////            }
////        }
////        return null; 
////        return localNotifsActiveSorted.isEmpty() ? null : localNotifsActiveSorted.get(0);
//        ItemList futureAlarms = DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_FUTURE_ALARM_ITEMS);
//        return futureAlarms.isEmpty() ? null : futureAlarms.get(0);
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////    private int getNotificationIndexXXX(String notificationId) {
//////        NotificationShadow notif;
////        for (int i = 0, size = activeLocalNotifsSorted.size(); i < size; i++) {
//////            notif = list.get(i);
//////            if (notif.notificationId.equals(notificationId)) {
////            if (activeLocalNotifsSorted.get(i).notificationId.equals(notificationId)) {
////                return i;
////            }
//////            break;
////        }
////        return -1;
////    }
////</editor-fold>
//    /**
//     * return the notification details for the notification with notificationId.
//     * Used eg to obtain the details on an expired local notification. Returns
//     * null if notificationId is not in list.
//     *
//     * @param notificationId
//     * @return
//     */
//    NotificationShadow getNotificationXXX(String notificationId
//    ) {
////        return list.get(getNotificationIndex(notificationId));
//        for (int i = 0, size = localNotifsActiveSorted.size(); i < size; i++) {
////            notif = list.get(i);
////            if (notif.notificationId.equals(notificationId)) {
//            if (localNotifsActiveSorted.get(i).notificationId.equals(notificationId)) {
//                return localNotifsActiveSorted.get(i);
//            }
////            break;
//        }
//        return null;
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////    NotificationShadow removeAlarmAndCancelLocalNotificationImpl(int index) {
//////            cancelLocalNotification(list.get(index).notificationId);
//////        Display.getInstance().cancelLocalNotification(list.get(index).notificationId);
////        AlarmHandler.getInstance().cancelLocalNotification(list.get(index).notificationId);
////        return list.remove(index);
////    }
////</editor-fold>
////        void removeAlarm(String id, NotificationType type) {
//    /**
//     * remove the specific alarm with notificationId from the list and cancel
//     * local notification
//     *
//     * @param notificationId
//     */
//    void removeAlarmAndCancelLocalNotificationXXX(String notificationId
//    ) {
////        NotificationShadow notif;
////        for (int i = 0, size = list.size(); i < size; i++) {
////            notif = list.get(i);
//        NotificationShadow notif;
//        Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
//        while (it.hasNext()) {
//            notif = it.next();
////                if (notif.notificationId.equals(id) && notif.type == type) {
//            if (notif.notificationId.equals(notificationId)) {
////                    list.remove(i);
////                    cancelLocalNotification(list.get(i));
////                LocalNotificationsShadowList.this.removeAlarmAndCancelLocalNotification(i);
////                removeAlarmAndCancelLocalNotificationImpl(i);
////                Display.getInstance().cancelLocalNotification(notif.notificationId);
////                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
//                AlarmHandler.cancelLocalNotification(notif.notificationId);
//                it.remove();
//            }
//            break;
//        }
//    }
//
//    /**
//     * remove both the main alarm and/or any associated repeat alarm (and cancel
//     * the corresponding local notifications). Called when user cancels an
//     * alarm. No need to remove snoozes since they .
//     *
//     * @param objectId
//     * @param mainType
//     */
//    void removeAlarmAndRepeatAlarmXXX(String notificationId) {
//        AlarmType type = AlarmType.getAlarmTypeFromNotifStr(notificationId);
//        String guid = AlarmType.getGuidStrWithoutTypeStr(notificationId, type);
//        this.removeAlarmAndRepeatAlarm(guid, type);
//    }
//
//    /**
//     * remove both the main alarm and/or any associated repeat alarm (and cancel
//     * the corresponding local notifications). No need to remove snoozes since
//     * they are mutually exclusive (when snoozing, other alarms are removed and
//     * when updating an alarm, snooze alarms are removed???)???. Called when
//     * user cancels an alarm
//     *
//     * @param guid
//     * @param mainType
//     */
//    void removeAlarmAndRepeatAlarmXXX(String guid, AlarmType mainType) {
////        for (int i = 0, size = list.size(); i < size; i++) {
//        NotificationShadow notif;
//        Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
//        while (it.hasNext()) {
//            notif = it.next();
////            notif = list.get(i);
//            if (guid != null && notif.notificationId.startsWith(guid)) { //on very first save objectId may be null, so no previous alarm to remove
//
//                if (notif.type == mainType
//                        || (mainType == notification && notif.type == notificationRepeat)
//                        || (mainType == waiting && notif.type == waitingRepeat) //                        
//                        || mainType == snoozedNotif //remove snooze just in case?!
//                        || mainType == snoozedWaiting //remove snooze just in case?!
//                        ) {
////                        list.remove(i);
////                        cancelLocalNotification(notif.notificationId); //cancel the corresponding alarm
////                    removeAlarmAndCancelLocalNotification(objectId);
////                    removeAlarmAndCancelLocalNotificationImpl(i);
////                    AlarmHandler.alarmLog("Local notification cancelled: \"" + notif.notificationId + "\"");
//                    AlarmHandler.alarmLog("Local notification cancelled: \"" + notif.notificationId + "\"");
//
//                    Display.getInstance().cancelLocalNotification(notif.notificationId);
//                    it.remove();
////        return list.remove(index);                }
////            cancelLocalNotification(list.get(i));
//                    //optimization: if Repeat is activated break loop after xxRepeat is found (since always later than main alarm)
//                } //else?? what are the other options
//            }
////            break;
//        }
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////    void removeAlarmAndRepeatAlarmOLD(String objectId, AlarmType mainType) {
////        NotificationShadow notif;
////        for (int i = 0, size = list.size(); i < size; i++) {
////            notif = list.get(i);
////            if (notif.notificationId.startsWith(objectId)) {
////                if (notif.type == mainType
////                        || (mainType == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
////                        || (mainType == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)) {
//////                        list.remove(i);
//////                        cancelLocalNotification(notif.notificationId); //cancel the corresponding alarm
//////                    removeAlarmAndCancelLocalNotification(objectId);
////                    removeAlarmAndCancelLocalNotificationImpl(i);
////                }
//////            cancelLocalNotification(list.get(i));
////                //optimization: if Repeat is activated break loop after xxRepeat is found (since always later than main alarm)
////            }
//////            break;
////        }
////    }
////</editor-fold>
//    /**
//     * remove and cancels *all* alarms with objectId (for the Item with
//     * objectId). Alarm, Waiting, Snooze. Called if the item is deleted.
//     *
//     * @param guid
//     */
//    void removeALLAlarmsForItemXXX(String guid
//    ) {
////        for (int i = 0, size = list.size(); i < size; i++) {
//// NotificationShadow notif;
//        Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
//        while (it.hasNext()) {
////            notif = list.get(i);
////            notif = it.next();
////            NotificationShadow notif = list.get(i);
//            NotificationShadow notif = it.next();
////            if (notif.notificationId.indexOf(objectId) == 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
//            if (notif.isForItem(guid)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////                String notifId = list.remove(i).notificationId; //remove entry
////                String notifId = it.remove().notificationId; //remove entry
////                AlarmHandler.getInstance().cancelAlarm(notif.notificationId); //and cancel the corresponding alarm
//                alarmHandler.cancelAlarmXXX(notif.notificationId); //and cancel the corresponding alarm
//            }
//        }
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////    void removeALLAlarmsForItemOLD(String objectId) {
////        for (int i = 0, size = list.size(); i < size; i++) {
////            NotificationShadow notif = list.get(i);
//////            if (notif.notificationId.indexOf(objectId) == 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////            if (notif.isForItem(objectId)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////                String notifId = list.remove(i).notificationId; //remove entry
////                AlarmHandler.getInstance().cancelAlarm(notifId); //and cancel the corresponding alarm
////            }
////        }
////    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////    void removeAllAlarmsForItemXXX(String objectId, boolean removeAlarm, boolean removeRepeat, boolean removeSnooze) {
////        for (int i = 0, size = list.size(); i < size; i++) {
////            NotificationShadow notif = list.get(i);
////            if (notif.notificationId.indexOf(objectId) >= 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////                String notifId = list.remove(i).notificationId; //remove entry
////                AlarmHandler.getInstance().cancelAlarm(notifId); //and cancel the corresponding alarm
////            }
////        }
////    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////    /**
////     * remove any alarms for the item that comes before the snoozeTime
////     *
////     * @param objectId
////     * @param type
////     * @param snoozeTime
////     */
//////        private void removeRepeatAlarmsBeforeSnoozeTime(String objectId, NotificationType type, Date snoozeTime) {
////    private void removeRepeatAlarmsBeforeSnoozeTime(String objectId, Date snoozeTime) {
////        //DONE!!!!!
//////        assert false : "TOD";
////        for (int i = 0, size = list.size(); i < size; i++) {
////            NotificationShadow notif = list.get(i);
//////                if (notif.notificationId.startsWith(objectId) && notif.type == type) {
////            //remove any alarms for the item that comes before the snoozeTime
////            //UI: snoozing an alarm for an item means no more alarms for that item until end of snooze time
////            if (notif.notificationId.startsWith(objectId) && notif.alarmTime.getTime() <= snoozeTime.getTime()) {
//////                    removeAlarm(notif);
////                LocalNotificationsShadowList.this.removeAlarmAndCancelLocalNotificationImpl(i);
////            }
////            if (notif.alarmTime.getTime() > snoozeTime.getTime()) { //stop iterating through the list at first alarm time *after* the snooze time
////                break;
////            }
////        }
////    }
////</editor-fold>
//    /**
//     * snooze, or re-snooze, an alarm. Remove the alarm, as well as any repeat.
//     * But removing a notification alarm will not remove a waiting alarm fo rthe
//     * same item. Any existing snooze for the object will also be removed, no
//     * matter if a notification or waiting was nsoozed. This means that there
//     * can only be one single snoozed alarm for a task, you cannot at the same
//     * time snooze a normal notification alarm and a waiting alarm, whichever is
//     * snoozed last will remove other snoozes. However this is unlikely to
//     * create problems since you would rarely have a normal and a waiting alarm
//     * to close to each other than you would snooze one for so long, or so many
//     * times, that it is still snoozed when the other is triggered and may be
//     * snoozed. It could also be confusing and annoying for the user to snooze
//     * one alarm and then have the snoozed alarm to appear.
//     *
//     * //TODO!!! If an alarm is snoozed so long that the next one is triggered,
//     * eg a notification alarm is snoozed until a waiting alarm is triggered,
//     * then should the snoozed alarm be removed? Probably yes otherwise again
//     * confusing/annoying with two alarms repeating for the same task (and it
//     * will be removed anyway if you snooze the waiting alarm).
//     *
//     * @param notificationId
//     * @param type
//     * @param snoozeExpireTime
//     * @param snoozeTitleText
//     * @param snoozeBodyText
//     */
//    void snoozeAlarmXXX(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
////        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
//        String guidId = AlarmType.getGuidStrWithoutTypeStr(notificationId);
//        AlarmType type = AlarmType.getAlarmTypeFromNotifStr(notificationId);
////        NotificationShadow notif;
////        //run through list and remove 
////        for (int i = 0, size = list.size(); i < size; i++) {
////            notif = list.get(i);
//        NotificationShadow notif;
//        Iterator<NotificationShadow> it = localNotifsActiveSorted.iterator();
//        while (it.hasNext()) {
//            notif = it.next();
//            if (notif.notificationId.equals(notificationId)) {
////                removeAlarmAndCancelLocalNotificationImpl(i); //remove alarm itself (could be a snooze)
////                Display.getInstance().cancelLocalNotification(notif.notificationId);
////                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
//                AlarmHandler.cancelLocalNotification(notif.notificationId);
//                it.remove();
//            } else if (notif.notificationId.startsWith(guidId)
//                    //remove any repeat but only of same type as alarm itself (if alarm is re-snoozed, the snoze is remove above) 
//                    && ((type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting) //DONE: how to avoid removing a snooze for a waiting alarm?
//                    || (type == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
//                    || (type == AlarmType.notificationRepeat && notif.type == AlarmType.notification)
//                    || (type == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)
//                    || (type == AlarmType.waitingRepeat && notif.type == AlarmType.waiting))) {
////                removeAlarmAndCancelLocalNotificationImpl(i);
////                Display.getInstance().cancelLocalNotification(notif.notificationId);
////                AlarmHandler.getInstance().cancelLocalNotification(notif.notificationId);
//                AlarmHandler.cancelLocalNotification(notif.notificationId);
//                it.remove();
//            } //else nothing
//        }
//        insertAndSetSingleBaseAlarmSorted(guidId, AlarmType.getSnoozedN(type), snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
////        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////    void snoozeAlarmOLD(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
//////        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
////        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId);
////        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
////        NotificationShadow notif;
////        //run through list and remove
////        for (int i = 0, size = list.size(); i < size; i++) {
////            notif = list.get(i);
////            if (notif.notificationId.equals(notificationId)) {
////                removeAlarmAndCancelLocalNotificationImpl(i); //remove alarm itself (could be a snooze)
////            } else //remove any repeat but only of same type as alarm itself (if alarm is re-snoozed, the snoze is remove above)
////            if (notif.notificationId.startsWith(objId)
////                    && ((type == AlarmType.snooze) //how to avoid removing a snooze for a waiting alarm?
////                    || (type == AlarmType.notification && notif.type == AlarmType.notificationRepeat)
////                    || (type == AlarmType.notificationRepeat && notif.type == AlarmType.notification)
////                    || (type == AlarmType.waiting && notif.type == AlarmType.waitingRepeat)
////                    || (type == AlarmType.waitingRepeat && notif.type == AlarmType.waiting))) {
////                removeAlarmAndCancelLocalNotificationImpl(i);
////            }
////        }
////        insertAndSetSingleBaseAlarmSorted(objId, AlarmType.snooze, snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
//////        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
////    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////    void snoozeAlarmOLD(String notificationId, Date snoozeExpireTime, String snoozeTitleText, String snoozeBodyText) {
////        removeAlarmAndCancelLocalNotification(notificationId); //remove previous (now snoozed) alarm, can be an already snoozed alarm!
//////            addAlarm(new NotificationShadow(notificationId, snoozeExpireTime, NotificationType.snooze), titleText, bodyText); //Create new snoozed alarm
////        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notificationId);
//////            String newNotifId = NotificationType.snooze.addTypeStrToStr(NotificationType.getNotifIdStrWithoutTypeStr(notificationId));
//////            addAlarm(new NotificationShadow(newNotifId, snoozeExpireTime, NotificationType.snooze), titleText, bodyText); //Create new snoozed alarm
////        insertAndSetSingleBaseAlarmSorted(objId, NotificationType.snooze, snoozeExpireTime, snoozeTitleText, snoozeBodyText); //Create new snoozed alarm
////        removeRepeatAlarmsBeforeSnoozeTime(objId, snoozeExpireTime); //if snoozing an alarm till *after* the corresponding repeat alarm, then cancel the repeat alarm
////    }
////</editor-fold>
//    /**
//     * called when adding a new alarm (and repeat alarm
//     */
//    void addAlarmAndRepeatNotificationXXX(Item item, AlarmRecord alarm) {
//        String titleText = item.makeNotificationTitleText(alarm.type);
//        String bodyText = item.makeNotificationBodyText(alarm.type);
////        addAlarmAndRepeat(item.getObjectIdP(), alarm.alarmTime, alarm.type, titleText, bodyText);
//        addAlarmAndRepeat(item.getGuid(), alarm.alarmTime, alarm.type, titleText, bodyText);
////<editor-fold defaultstate="collapsed" desc="comment">
////        int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
////
////        ASSERT.that(alarm.type == AlarmType.notification || alarm.type == AlarmType.waiting);
////        ASSERT.that((getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)));
////
//////        insertAndSetSingleBaseAlarmSorted(objectId, type, newAlarm, titleText, bodyText);
////        insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type, alarm.alarmTime, titleText, bodyText);
////
////        if (repeatInterval > 0) {
//////            insertAndSetSingleBaseAlarmSorted(objectId, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
////            insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type,
////                    new Date(alarm.alarmTime.getTime() + repeatInterval * MyDate.MINUTE_IN_MILLISECONDS),
////                    titleText, bodyText);
////        }
////</editor-fold>
//    }
//
//    void addAlarmAndRepeatXXX(Item item, AlarmRecord alarm) {
//        String titleText = item.makeNotificationTitleText(alarm.type);
//        String bodyText = item.makeNotificationBodyText(alarm.type);
////        addAlarmAndRepeat(item.getObjectIdP(), alarm.alarmTime, alarm.type, titleText, bodyText);
//        addAlarmAndRepeat(item.getGuid(), alarm.alarmTime, alarm.type, titleText, bodyText);
////<editor-fold defaultstate="collapsed" desc="comment">
////        int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes.getInt();
////
////        ASSERT.that(alarm.type == AlarmType.notification || alarm.type == AlarmType.waiting);
////        ASSERT.that((getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)));
////
//////        insertAndSetSingleBaseAlarmSorted(objectId, type, newAlarm, titleText, bodyText);
////        insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type, alarm.alarmTime, titleText, bodyText);
////
////        if (repeatInterval > 0) {
//////            insertAndSetSingleBaseAlarmSorted(objectId, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
////            insertAndSetSingleBaseAlarmSorted(item.getObjectIdP(), alarm.type,
////                    new Date(alarm.alarmTime.getTime() + repeatInterval * MyDate.MINUTE_IN_MILLISECONDS),
////                    titleText, bodyText);
////        }
////</editor-fold>
//    }
//
//    void addAlarmAndRepeatXXX(Item item) {
//        addAlarmAndRepeat(item, item.getNextcomingAlarmRecordN());
//    }
//
//    void addAlarmAndRepeatOLD(String guid, Date newAlarm, AlarmType type, String titleText, String bodyText) {
//        int repeatInterval = MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt();
//
//        ASSERT.that(type == AlarmType.notification || type == AlarmType.waiting || type == AlarmType.snoozedNotif || type == AlarmType.snoozedWaiting, () -> "wrong alarmType=" + type);
////        ASSERT.that((getNumberAvailableLocalNotificationSlots() >= (repeatInterval > 0 ? 2 : 1)));
//
//        this.insertAndSetSingleBaseAlarmSorted(guid, type, newAlarm, titleText, bodyText);
//
//        if (repeatInterval > 0 && (type != AlarmType.snoozedNotif && type != AlarmType.snoozedWaiting)) {
//            this.insertAndSetSingleBaseAlarmSorted(guid, type == AlarmType.notification ? AlarmType.notificationRepeat : AlarmType.waitingRepeat,
//                    new MyDate(newAlarm.getTime() + ((long) repeatInterval) * MyDate.MINUTE_IN_MILLISECONDS),
//                    titleText, bodyText);
//        }
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        /**
////         * updates any previously set local notifications with alarm times or
////         * new text. Also updates any repeat alarms set, as well as snoozed
////         * alarms?
////         */
////        void updateAlarmsXXX(String objectId, Date newAlarm, Date newWaitingAlarm, String titleText, String bodyText) {
////            Date latestSetAlarm = null;
////            if (newAlarm != null && newAlarm.getTime() != 0) {
////                latestSetAlarm = getLastAlarmTime(NotificationType.)
////            }
////            for (int i = 0, size = list.size(); i < size; i++) {
////                NotificationShadow notif = list.get(i);
////                //TOD optimization: should use startsWith since less costly than indexOf
//////                if (notif.notificationId.indexOf(objectId) >= 0) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////                if (notif.notificationId.startsWith(objectId)) { //TOD should use ==0 since notifId starts with objId, but fragile if every the notifId format is changed
////                    NotificationType type = NotificationType.getTypeContainedInStr(notif.notificationId);
////                    cancelAlarm(notif.notificationId); //cancel the corresponding alarm
////                    //if new alarms is less than the highest currently set alarm, set a new one
////                    if (type == NotificationType.notification && newAlarm != null && newAlarm.getTime() != 0
////                            && newAlarm.getTime() < getLastAlarmTime(type).getTime()) {
////                        addAlarm(new NotificationShadow(type, objectId, newAlarm), titleText, bodyText);
////                    }
////                    String notifId = list.remove(i).notificationId; //remove entry
////                    cancelAlarm(notifId); //and cancel the corresponding alarm
////                }
////            }
////            list.add(insertIndex, notif);
////            setLocalNotification(WAITING_KEY, WAITING_KEY, titleText, bodyText);
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        void removeSurplusAlarmsXXX() {//NOT needed, instead always test that there is space before inserting new alarms
////            int i = list.size();
////            while (i >= 0 && list.size() > MAX_NUMBER_LOCAL_NOTIFICATIONS) {
////                AlarmHandler.NotificationShadow notif = list.get(i);
////                if (notif.type == NotificationType.snooze) {
////                    i--; //if too many alarms, don't remove a snoozed alarm
////                } else {
////                    //if we remove a Repeat alarm, also remove the original (which may later be used to test for the latest alarm set)
////                    if (notif.type == NotificationType.notificationRepeat) {
////                        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type);
//////                        removeAlarm(NotificationType.notification.addTypeStrToStr(objId), NotificationType.notification);
////                        removeAlarm(NotificationType.notification.addTypeStrToStr(objId));
////                    } else if (notif.type == NotificationType.waitingRepeat) {
////                        String objId = NotificationType.getObjectIdStrWithoutTypeStr(notif.notificationId, notif.type);
//////                        removeAlarm(NotificationType.waiting.addTypeStrToStr(objId), NotificationType.waiting);
////                        removeAlarm(NotificationType.waiting.addTypeStrToStr(objId));
////                    }
////                    //remove the alarm itself
//////                    removeAlarm(notif.notificationId, notif.type);
////                    removeAlarm(notif.notificationId);
//////                    removeAllAlarmsOtherThanSnooze(notif.notificationId); //if we need to remove a repat alarm, also remove the
//////                    cancelLocalNotifications(list.get(i).);
////                    list.remove(i);
////                }
////            }
////        }
////</editor-fold>
//    /**
//     * return the latest time of notifications of type type. If no such type,
//     * returns Date(MyDate.MAX_DATE) to signal that any new notification of this
//     * type can be inserted
//     *
//     * @param type
//     * @return
//     */
//    Date getLastAlarmTime(AlarmType type) {
//        int i = localNotifsActiveSorted.size() - 1;
//        while (i >= 0 && localNotifsActiveSorted.get(i).type != type) {
//            i--;
//        }
//        if (i >= 0) {
//            return localNotifsActiveSorted.get(i).alarmTime;
//        } else {
////                return null;
//            return new MyDate(MyDate.MAX_DATE); //if 
//        }
//    }
//
//    int getNumberAvailableLocalNotificationSlots() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        long now = System.currentTimeMillis();
////        int firstFutureSlot = 0;
////        for (int i = 0, size = list.size(); i < size; i++) {
////            if (list.get(i).alarmTime.getTime() > now) {
////                firstFutureSlot = i;
////                break;
////            }
////        }
////        return AlarmHandler.MAX_NUMBER_LOCAL_NOTIFICATIONS - list.size() + firstFutureSlot;
////</editor-fold>
//        return maxNumberLocalNotifications - localNotifsActiveSorted.size();
//    }
//
//    void setAlarmHandlerXXX(AlarmHandler alarmHandler) {
//        this.alarmHandler = alarmHandler;
//        maxNumberLocalNotifications = AlarmHandler.MAX_NUMBER_LOCAL_NOTIFICATIONS;
//    }
//
//    public String toString() {
//        String str = "[";
//        String sep = "";
//        for (NotificationShadow s : localNotifsActiveSorted) {
//            str += str + s + sep;
//            sep = ";";
//        }
//        return str + "]";
//    }
//
//    boolean isEmpty() {
//        return localNotifsActiveSorted.isEmpty();
//    }
//
//
//    NotificationShadow remove(int i) {
//        return localNotifsActiveSorted.remove(i);
//    }
//
//    /**
//     * return true if possible to add another local notification. If alarms
//     * repeat, we need 2 empty slots, otherwise just 1
//     *
//     * @return
//     */
//    private boolean isPossibleToSetAnotherNotificationXXX() {
//        return localNotifsActiveSorted.size()
//                < MAX_NUMBER_LOCAL_NOTIFICATIONS - (MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutesXXX.getInt() > 0 ? 2 : 1);
//    }
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
//</editor-fold>
}
