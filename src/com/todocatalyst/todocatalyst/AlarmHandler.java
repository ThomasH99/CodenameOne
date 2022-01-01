/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates       
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.notifications.LocalNotification;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.animations.CommonTransitions;
import com.parse4cn1.ParseObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class AlarmHandler {
    //DONE when changing the repeat alarm interval, removeFromCache all already set alarms (or cancel all set repeat alarms if deactivating repeat alarms)
    //DONE enough with only one snooze allowed for a task (eg are separate snoozes needed for normal alarm and waiting alarm??). Yes, unlikely there'll be overlap, and more than one snooze reminder would be confusing/annoying
    //TODO 

//    private final static String STORAGE_FILE_NAME = "AlarmHandler";
//    final static String CLASS_NAME_ALARM_DATA = "alarmData";
    private final static String NOTIF_LIST_FILE_ID = "$$notificationList";
    private final static String EXPIRED_ALARMS_FILE_ID = "$$expiredAlarmsList";

//    static final int MAX_NUMBER_LOCAL_NOTIFICATIONS = 64;
//<editor-fold defaultstate="collapsed" desc="comment">
//    private final static String WAITING_KEY = "-[WAITING]";     // postfix to identify a waiting alarm
//    private final static String ALARM_KEY = "-[ALARM]"; // postfix to identify an alarm
//    private final static String WAITING_ALARM_TEXT = "WAITING: ";// reminder for: \n"; "[Waiting]"
//    private final static String ALARM_TEXT = "";//Reminder for: \n";
//    private final static String ALARM_REPEAT_STR = "-REP";//Used to create a separate notification ID for a repeat reminder (must also be used to cancel)
//</editor-fold>
    /**
     * list of already expired alarms not yet processed by the end-user, to show
     * in AlarmScreen to snooze, cancel etc
     */
//    private List<ExpiredAlarm> expiredAlarms; //saved in file $$expiredAlarmsList
//    private List<AlarmRecord> expiredAlarms; //saved in file $$expiredAlarmsList
    /**
     * latest list of future alarms
     */
//    private List<AlarmRecord> futureAlarms; // = new LocalNotificationsShadowList();
    /**
     * keep a copy of all local notifications set. Needed to be able to
     * cancel/update local notifications.
     */
//    private LocalNotificationsShadowList notificationList; // = new LocalNotificationsShadowList();
//    private List<String> notificationList; // = new LocalNotificationsShadowList();
    private LocalNotificationsShadowList notificationList; // = new LocalNotificationsShadowList();
//    private AlarmInAppAlarmHandler inAppTimer; // = new AlarmInAppAlarmHandler(notificationList);

    private Media alarmSound;
    private boolean multipleUpdatesOngoing; //prevent multiple refresh if snoozing/cancelling more than one alarm at once

    private AlarmHandler() {
//        notificationList = readActiveLocaleNotifications();
//        ItemList expiredItems = DAO.getInstance().fetchDynamicNamedItemList(DAO.SYSTEM_LIST_UNPROCESSED_ALARM_ITEMS);
//        List<Item> l = expiredItems.getListFull();
//        for (int i = 0, size = l.size(); i < size; i++) {
//            expiredAlarms.addAll(l.get(i).getAllFutureAlarmRecordsUnsorted());
//        }
//        Item.sortAlarmRecords(expiredItems); //show most recently expired alarm first
//        expiredAlarms = DAO.getInstance().getUnprocessedAlarmRecords(false);

//        ItemList futureItems = DAO.getInstance().fetchDynamicNamedItemList(DAO.SYSTEM_LIST_FUTURE_ALARM_ITEMS);
//        l = futureItems.getListFull();
//        for (int i = 0, size = l.size(); i < size; i++) {
//            futureAlarms.addAll(l.get(i).getAllFutureAlarmRecordsUnsorted());
//        }
//        Item.sortAlarmRecords(futureAlarms);
//        futureAlarms = DAO.getInstance().fetchFutureAlarmRecords(false);
        notificationList = new LocalNotificationsShadowList(); //initialize inAppTimer and setup/refresh local notifications
    }

    private static AlarmHandler INSTANCE;

    public static AlarmHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlarmHandler();
//            INSTANCE.inAppTimer.updateInAppTimerOnNextcomingAlarm(); //MUST do here and not in singleton instantiation of AlarmHandler() above to avoid infinite loop
        }
        return INSTANCE;
    }

//    public void setListOfItemWithFutureAlarms(List<Item> items) {
//        futureAlarms = new ArrayList();
//        Date now = new MyDate();
//        for (Item i : items) {
//            List<AlarmRecord> alarmRecords = i.getAllAlarmRecords(now, false);
//            for (AlarmRecord alarmRecord : alarmRecords) {
//                futureAlarms.add(alarmRecord);
//            }
//        }
//        Item.sortAlarmRecords(futureAlarms);
//    }
//    public void setListOfItemsWithUncancelledAlarms(List<Item> items) {
//        expiredAlarms = new ArrayList();
//        Date now = new MyDate();
//        for (Item i : items) {
//            List<AlarmRecord> alarmRecords = i.getUnprocessedAlarms();
//            for (AlarmRecord alarmRecord : alarmRecords) {
//                expiredAlarms.add(alarmRecord);
//            }
//        }
//        Item.sortAlarmRecords(expiredAlarms);
//    }
    /**
     * read from local storage
     *
     * @return
     */
//    private List<String> readActiveLocaleNotifications() {
//        List<String> notificationList;
//        notificationList = (List<String>) Storage.getInstance().readObject(NOTIF_LIST_FILE_ID);
//        if (notificationList == null) {
//            notificationList = new ArrayList<String>();
//        }
//        return notificationList;
//    }
//
//    private void saveActiveLocaleNotifications(List<String> notificationList) {
//        Storage.getInstance().writeObject(NOTIF_LIST_FILE_ID, notificationList);
//    }
//    private void expiredAlarmSaveXXX() {
//        Storage.getInstance().writeObject(EXPIRED_ALARMS_FILE_ID, expiredAlarms);
//    }
    static void alarmLog(String s) {
        if (Config.TEST) {
            Log.p("ALARM: " + s);
        }
    }

//    static void cancelLocalNotificationXXX(String notificationId) {
//        AlarmHandler.alarmLog("Local notifiation cancelled: \"" + notificationId + "\"");
//        Display.getInstance().cancelLocalNotification(notificationId);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static void setPreferredBackgroundFetchIntervalXXX() {
//        Display.getInstance().setPreferredBackgroundFetchInterval(Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4)); //max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
//    }
//</editor-fold>
    /**
     * Called regularly, eg daily, to set up local notifications for an
     * additional period, e.g. another day. May (also) be called in background
     * fetchFromCacheOnly, so must be completely self-reliant and not eg assume
     * that cache or other things have been loaded. Assumes that any changes in
     * the time period for local notifications have already been setup are
     * (???).
     */
//    private void setAlarmsForNewIntervalBackgroundFetch() {
    public void updateLocalNotificationsOnChange() { //MOVE to DAO. Refresh if items edited on another device, or say 24h before the last previously fetched alarmRecord
        updateLocalNotificationsOnChange(new MyDate(), false);
    }

    public void updateLocalNotificationsOnChange(Date now) { //MOVE to DAO. Refresh if items edited on another device, or say 24h before the last previously fetched alarmRecord
        updateLocalNotificationsOnChange(now, false);
    }

    public void updateLocalNotificationsOnChange(Date now, boolean forceRefresh) { //MOVE to DAO. Refresh if items edited on another device, or say 24h before the last previously fetched alarmRecord
        if (multipleUpdatesOngoing) {
            return;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && !MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) { //DONE: optimization: rather not *schedule* this if setting is deactivate and avoid need to reload preferences
//            return;
//        }
//
//        //optimization: check if there are active alarms within the next 60min (fetch interval) and if yes, waith with fetching till next time
//        //TODO!!!! how to set alarms for alarms edited on other devices? Still via push?!
////        int maxNbFreeNotifications = notificationList.getNumberAvailableLocalNotificationSlots();
////        List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(LocalNotificationsShadowList.MAX_NUMBER_LOCAL_NOTIFICATIONS);
//        List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS);
//        if (newAlarmsList != null && newAlarmsList.size() > 0) { //only do something if we successfully got the list (avoid cancelling alarms if anything went wrong with the fetch)
//
//            notificationList.cancelAndRemoveAllAvailableLocalNotifications();
//            Log.p("updateLocalNotificationsOnBackgroundFetch():cancelling local notifications");
//
//            Item item;
//            while (notificationList.getNumberAvailableLocalNotificationSlots() >= 2 && !(newAlarmsList.isEmpty())) {
//                item = newAlarmsList.remove(0);
//                if (item.getNextcomingAlarmRecordN() == null) {
//                    Log.p("updateLocalNotificationsOnBackgroundFetch(): getNextcomingAlarmRecordN()==null!!! for item=" + item);
//                } else {
//                    Log.p("updateLocalNotificationsOnBackgroundFetch(): setting notification for Item=" + item + "; alarmTime=" + MyDate.formatDateNew(item.getNextcomingAlarmRecordN().alarmTime));
//                }
//                notificationList.addAlarmAndRepeat(item, item.getNextcomingAlarmRecordN());
//            }
//            refreshInAppTimerAndSaveNotificationList();
//        } else {
//            Log.p("updateLocalNotificationsOnBackgroundFetch(): DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS) returned empty list!");
//        }
//</editor-fold>
        notificationList.updateWithFutureAlarms(now, forceRefresh);
    }

    /**
     * Called on app start or if alarms are globally enabled or disabled (via
     * settings)
     */
//    public void updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabledXXX() {
//        if (!MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
//            //disable all
//            notificationList.cancelAndRemoveAllAvailableLocalNotifications();
//            refreshInAppTimerAndSaveNotificationList();
//            return;
//        } else {
//            //update, or set from scratch, all alarms
////        int maxNbFreeNotifications = notificationList.getNumberAvailableLocalNotificationSlots();
////            List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(LocalNotificationsShadowList.MAX_NUMBER_LOCAL_NOTIFICATIONS);
//            List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS);
//            if (newAlarmsList != null && newAlarmsList.size() > 0) { //only do something if we successfully got the list (avoid cancelling alarms if anything went wrong with the fetch)
//                notificationList.cancelAndRemoveAllAvailableLocalNotifications();
//                Item item;
//                while (notificationList.getNumberAvailableLocalNotificationSlots() >= 2 && !(newAlarmsList.isEmpty())) {
//                    item = newAlarmsList.remove(0);
////                    notificationList.addAlarmAndRepeat(item, item.getNextcomingAlarmRecordN());
//                    notificationList.addAlarmAndRepeat(item);
//                }
//                refreshInAppTimerAndSaveNotificationList();
//            }
//        }
//    }
//    private void saveNotificationListXXX() {
//        Storage.getInstance().writeObject(AlarmHandler.NOTIF_LIST_FILE_ID, notificationList);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateLocalNotificationsOnBackgroundFetchOrAppStartOLD() {
//        if (!MyPrefs.getBoolean(MyPrefs.alarmsActivatedOnThisDevice)) {
//            return;
//        }
//        int maxNbFreeNotifications = notificationList.getNumberAvailableLocalNotificationSlots();
//        Date dateOfLastAlarmNotificationSet = notificationList.getLastAlarmTime(AlarmType.notification);
//        Date dateOfLastWaitingNotificationSet = notificationList.getLastAlarmTime(AlarmType.waiting);
//
////        List<Item> newAlarmsList = DAO.getInstance().getItemsWithNormalAlarms(maxNbFreeNotifications, dateOfLastAlarmNotificationSet);
//        List<Item> newAlarmsList = DAO.getItemsWithNormalAlarms(maxNbFreeNotifications, dateOfLastAlarmNotificationSet);
////        List<Item> newWaitingList = DAO.getInstance().getItemsWithWaitingAlarms(maxNbFreeNotifications, dateOfLastWaitingNotificationSet);
//        List<Item> newWaitingList = DAO.getItemsWithWaitingAlarms(maxNbFreeNotifications, dateOfLastWaitingNotificationSet);
//
//        Item item;
////        while (notificationList.getNumberFreeSlots() >= 2 && (!newAlarmsList.isEmpty() || !newWaitingList.isEmpty())) {
//        while (notificationList.getNumberAvailableLocalNotificationSlots() >= 2 && !(newAlarmsList.isEmpty() && newWaitingList.isEmpty())) {
//            if (newWaitingList.isEmpty() || (!newAlarmsList.isEmpty() && newAlarmsList.get(0).getAlarmDateD().getTime() <= newWaitingList.get(0).getAlarmDateD().getTime())) {
//                item = newAlarmsList.remove(0);
//                notificationList.addAlarmAndRepeat(item.getObjectIdP(), item.getAlarmDateD(), AlarmType.notification,
//                        item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));
//
//            } else {
//                item = newWaitingList.remove(0);
//                notificationList.addAlarmAndRepeat(item.getObjectIdP(), item.getWaitingAlarmDateD(), AlarmType.waiting,
//                        item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (newWaitingList.isEmpty()) {
////                item = newAlarmsList.remove(0);
////                notificationList.addAlarm(item.getObjectIdP(), item.getAlarmDateD(), NotificationType.notification,
////                        item.makeNotificationTitleText(NotificationType.notification), item.makeNotificationBodyText(NotificationType.notification));
////            } else if (newAlarmsList.isEmpty()) {
////                item = newWaitingList.remove(0);
////                notificationList.addAlarm(item.getObjectIdP(), item.getAlarmDateD(), NotificationType.waiting,
////                        item.makeNotificationTitleText(NotificationType.waiting), item.makeNotificationBodyText(NotificationType.waiting));
////            } else {
////                if (newAlarmsList.get(0).getAlarmDateD().getTime() <= newWaitingList.get(0).getAlarmDateD().getTime()) {
////                    item = newAlarmsList.remove(0);
////                    notificationList.addAlarm(item.getObjectIdP(), item.getAlarmDateD(), NotificationType.notification,
////                            item.makeNotificationTitleText(NotificationType.notification), item.makeNotificationBodyText(NotificationType.notification));
////                } else {
////                    item = newWaitingList.remove(0);
////                    notificationList.addAlarm(item.getObjectIdP(), item.getAlarmDateD(), NotificationType.waiting,
////                            item.makeNotificationTitleText(NotificationType.waiting), item.makeNotificationBodyText(NotificationType.waiting));
////                }
////            }
////</editor-fold>
//        }
////        inAppTimer.refreshInAppTimer();
////        notificationList.save();
//        refreshInAppTimerAndSaveNotificationList();
//    }
//</editor-fold>
    /**
     * called from performBackgroundFetch(long deadline, Callback<Boolean>
     * onComplete) to fetchFromCacheOnly additional alarms (in the time interval
     * between the last deadline used and end of the interval used, e.g. now+8
     * days) and schedule the OS/device local notifications for it.
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateLocalNotificationsOnBackgroundFetchXXX() {
////        final interface Boolean updater = (b) -> {
////            if (alarmData.lastUpdateIntervalEndTime.getTime() == 0) {
//        updateLocalNotificationsOnBackgroundFetchOrAppStart();  //set next notifications
////                performBackgroundFetch(getNextUpdateTime(), updater); //schedule next update
////            }
////        };
////        performBackgroundFetch(getNextUpdateTime(), updater);
//    }
//</editor-fold>
//    private void refreshInAppTimerAndSaveNotificationList() {
//        inAppTimer.updateInAppTimerOnNextcomingAlarm();
////        notificationList.save();
//        saveNotificationListXXX();
//    }
//    private void updateAlarmsOrTextForItemXXX(Item item) {
////        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getObjectIdP(), item.getAlarmDate(), AlarmType.notification,
//        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getGuid(), item.getAlarmDate(), AlarmType.notification,
//                item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));
//
////        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getObjectIdP(), item.getWaitingAlarmDate(), AlarmType.waiting,
//        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getGuid(), item.getWaitingAlarmDate(), AlarmType.waiting,
//                item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));
//
////        if (false) {
////            removeExpiredAlarm(null);
////        }
////        inAppTimer.refreshInAppTimer();
////        notificationList.save();
//        refreshInAppTimerAndSaveNotificationList();
//    }
//    public void updateOnItemChangeXXX(Item item) {
//        if (item.isDone() || item.isSoftDeleted()) {
//            deleteAllAlarmsForItemXXX(item); //remove any future alarms for a Done/Cancelled task
//        } else {
//            updateAlarmsOrTextForItemXXX(item);
//            cancelExpiredAlarmsOnItemUpdateXXX(item);
//        }
//    }
    //NORMAL (REMINDER) ALARMS
    /**
     * called when user cancels an alarm in the popup dialog. Will also cancel
     * any repeat alarm
     *
     * @param notificationId
     */
//    public void cancelAlarmXXX(String notificationId) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        String objId = NotificationType.getNotifIdStrWithoutTypeStr(notificationId);
////        NotificationType type = NotificationType.getTypeContainedInStr(objId);
////        notificationList.removeAlarm(notificationId);
////        //if the user cancels the first alarm, we must cancel any repeat alarms
////        if (type == NotificationType.notification || type == NotificationType.waiting) {
////            notificationList.removeAlarm(type.addTypeStrToStr(objId));
////        }
////        inAppTimer.refreshInAppTimer();
////</editor-fold>
//        notificationList.removeAlarmAndRepeatAlarm(notificationId);
////        inAppTimer.refreshInAppTimer();
////        notificationList.save();
//        refreshInAppTimerAndSaveNotificationList();
//    }
    /**
     * deletes all the alarms (both local notifications and app alarms and
     * snooze) that may have been set for an Item, e.g. if the item is
     * completed, cancelled or deleted.
     *
     * @param item
     */
//    public void deleteAllAlarmsForItemXXX(Item item) {
//        //delete all possible alarms set for this item
////        notificationList.removeAllAlarms(item.getObjectId());
////        notificationList.removeALLAlarmsForItem(item.getObjectIdP());
//        notificationList.removeALLAlarmsForItemXXX(item.getGuid());
//        cancelAllExpiredAlarms(item); //remove any already expired alarms
////        inAppTimer.refreshInAppTimer();
////        notificationList.save();
//        refreshInAppTimerAndSaveNotificationList();
//    }
    /**
     * initialization of the alarm handling. Called on each start up of the app.
     * Will update local notifications. Also launches timer to handle in-app
     * (while app is running) alarms. NB! Initiating the background
     * fetchFromCacheOnly task to update local notifications regularly even if
     * app is not activated is done in the main start().
     */
//    public void setupAlarmHandlingOnAppStart() {
//        updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled();
//        //remove any snoozed alarms in the past (since they have been handled by local notifications)
////<editor-fold defaultstate="collapsed" desc="comment">
////        long now = System.currentTimeMillis();
////        if (false) {
////            notificationList.removeExpiredAlarmsXXX(); //ScreenListOfAlarms will show expired alarms until they're dealt with
////        }//        inAppTimer.refreshInAppTimer();
////        notificationList.save();
////</editor-fold>
//        if (false) {
//            refreshInAppTimerAndSaveNotificationList(); // done in updateLocalNotificationsOnBackgroundFetchOrAppStart()
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        while (alarmData.getSnoozeTimeForItemWithSmallestTime()!=null && alarmData.getSnoozeTimeForItemWithSmallestTime().getTime() < now) {
////            alarmData.removeSnoozeForItemWithSmallestTime();
////        }
////        setInAppTimerForItemsWithAlarmOrSnoozed();
////</editor-fold>
//    }
    /**
     * called whenever item has changed to update inAppTimer and local
     * notifications
     *
     * @param item
     */
    public void update(Item item) {
        notificationList.updateAlarmNotifications(item);
        Form f = MyForm.getCurrentFormAfterClosingDialogOrMenu();
        if (f instanceof MyForm) {
            ((MyForm) f).refreshAfterEdit();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (item.getUnprocessedAlarms().isEmpty()) {
//            cancelExpiredAlarm(expiredAlarm)
//
//        }
//        notificationList.add(item.get);
//        refreshInAppTimerAndSaveNotificationList();
//        //save snooze time
//        DAO.getInstance().saveToParseNow(item);
//</editor-fold>
    }

    /**
     *
     * @param expiredAlarm
     * @param snoozeExpireTime
     */
//    public void snoozeAlarm(String notificationId, Date snoozeExpireTime) {
//    public void snoozeAlarmOLD(ExpiredAlarm expiredAlarm, Date snoozeExpireTime) {
////        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
////        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId, type);
//        Item item = DAO.getInstance().fetchItemN(expiredAlarm.guid);
//        expiredAlarms.remove(expiredAlarm);
//        expiredAlarmSaveXXX();
//        item.setSnoozeAlarmRecord(new AlarmRecord(snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type))); //Update *before* creating reminder to get right snooze time
//        notificationList.removeAlarmAndRepeatAlarm(expiredAlarm.guid, expiredAlarm.type); //on snooze, remove any still active alarms of the same type as the snoozed one
////        notificationList.snoozeAlarm(notificationId, snoozeExpireTime, item.makeNotificationTitleText(type), item.makeNotificationBodyText(type));
//        notificationList.addAlarmAndRepeat(expiredAlarm.guid, snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type),
//                item.makeNotificationTitleText(expiredAlarm.type), item.makeNotificationBodyText(expiredAlarm.type)); //UI: snooze also has localNotification repeat, NO, finally disabled
////        notificationList.save();
//        refreshInAppTimerAndSaveNotificationList();
//        //save snooze time
////<editor-fold defaultstate="collapsed" desc="comment">
////        item.setSnoozeDate(snoozeExpireTime);
////        item.setSnoozeAlarmRecord(new AlarmRecord(snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type)));
////        DAO.getInstance().saveNew(item, true);
////        DAO.getInstance().saveNew(item);
////        DAO.getInstance().saveItem3(item);xxx;
////        DAO.getInstance().saveNewTriggerUpdate3();
////</editor-fold>
//        DAO.getInstance().saveToParseNow(item);
//    }
    public void snoozeAlarm(AlarmRecord expiredAlarm, Date snoozeExpireTime) {
        snoozeAlarm(expiredAlarm, snoozeExpireTime, true);
    }

    public void snoozeAlarm(AlarmRecord expiredAlarm, Date snoozeExpireTime, boolean save) {
        snoozeAlarm(expiredAlarm.item, expiredAlarm.type, snoozeExpireTime, save);
    }

    public void snoozeAlarm(Item item, AlarmType alarmType, Date snoozeExpireTime) {
        snoozeAlarm(item, alarmType, snoozeExpireTime, true);
    }

    public void snoozeAlarm(Item item, AlarmType alarmType, Date snoozeExpireTime, boolean saveImmediately) {
//        Item item = DAO.getInstance().fetchItemN(expiredAlarm.guid);
        if (Config.TEST) {
            ASSERT.that(item.getAlarmDate().getTime() < System.currentTimeMillis(), "trying to snooze alarm NOT in the past");
        }
//        notificationList.remove(expiredAlarm.getNotificationId()); //now done in AlarmHandler.update()
        switch (alarmType) {
            case notification:
            case snoozedNotif:
                item.setSnoozeAlarm(snoozeExpireTime);
                break;
            case waiting:
            case snoozedWaiting:
                item.setSnoozeWaitingAlarm(snoozeExpireTime);
                break;
            case notificationRepeat:
            case waitingRepeat:
            default:
                if (Config.TEST) {
                    ASSERT.that("Unhandled alarmType=" + alarmType);
                }
        }
        if (saveImmediately) {
            DAO.getInstance().saveToParseNow(item);
        } else {
            DAO.getInstance().saveToParseLater(item);
        }
    }

    public void snoozeAllExpiredAlarms(Date snoozeExpireTime) {
        Date now = new MyDate();
        List<AlarmRecord> expired = DAO.getInstance().getUnprocessedAlarmRecords(now, false);
        if (expired.size() > 1) {
            multipleUpdatesOngoing = true;
        }
        for (AlarmRecord alarmRecord : expired) {
            snoozeAlarm(alarmRecord, snoozeExpireTime, false);
        }
        if (multipleUpdatesOngoing) {
            multipleUpdatesOngoing = false;
            updateLocalNotificationsOnChange(now);
        }
        DAO.getInstance().triggerParseUpdate();
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void snoozeAllExpiredAlarmsOLD(Date snoozeExpireTime) {
////        List<Item> itemsToSave = new ArrayList();
//        List<ParseObject> itemsToSave = new ArrayList();
//        while (!expiredAlarms.isEmpty()) {
//            ExpiredAlarm expiredAlarm = expiredAlarms.get(0);
//            Item item = DAO.getInstance().fetchItem(expiredAlarm.objectId);
//            expiredAlarms.remove(expiredAlarm);
//            notificationList.addAlarmAndRepeat(expiredAlarm.objectId, snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type), item.makeNotificationTitleText(expiredAlarm.type), item.makeNotificationBodyText(expiredAlarm.type)); //UI: snooze also has localNotification repeat, NO, finally disabled
////            item.setSnoozeDate(snoozeExpireTime);
//            item.setSnoozeAlarmRecord(new AlarmRecord(snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type)));
////            DAO.getInstance().save(item);
//            itemsToSave.add(item);
//        }
//        refreshInAppTimerAndSaveNotificationList();
//        expiredAlarmSave();
//        DAO.getInstance().saveBatch(itemsToSave);
//    }
//</editor-fold>

    /**
     * called when user cancels an expired alarm
     *
     * @param expiredAlarm
     */
//    public void removeExpiredAlarm(String objId, AlarmType type) {
//    public void cancelExpiredAlarm(ExpiredAlarm expiredAlarm) {
    public void cancelExpiredAlarm(AlarmRecord expiredAlarm) {
        cancelExpiredAlarm(expiredAlarm, true);
    }

    public void cancelExpiredAlarm(AlarmRecord expiredAlarm, boolean saveImmediately) {
//        Item expiredItem = DAO.getInstance().fetchItemN(expiredAlarm.guid);
        Item expiredItem = expiredAlarm.item;
//        if (expiredAlarm.type == AlarmType.notification || expiredAlarm.type == AlarmType.notificationRepeat) {
//        if (expiredAlarm.type == AlarmType.notification) {
        if (expiredAlarm.type.isReminder()) {
            expiredItem.cancelOrSnoozeAlarm(true);
//        } else if (expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.waitingRepeat) {
//        } else if (expiredAlarm.type == AlarmType.waiting) {
        } else if (expiredAlarm.type.isWaitingReminder()) {
            expiredItem.cancelOrSnoozeWaitingAlarm(true);
        }
        if (saveImmediately) {
            DAO.getInstance().saveToParseNow(expiredItem);
        } else {
            DAO.getInstance().saveToParseLater(expiredItem);
        }
    }

    public void cancelAllExpiredAlarms() {
//                    AlarmHandler.getInstance().removeExpiredAlarm(expired);
        Date now = new MyDate();
        List<AlarmRecord> expired = DAO.getInstance().getUnprocessedAlarmRecords(now, false);
        if (expired.size() > 1) {
            multipleUpdatesOngoing = true;
        }
//        List<AlarmRecord> expiredAlarms = DAO.getInstance().getUnprocessedAlarmRecords(false);
        while (!expired.isEmpty()) {
            cancelExpiredAlarm(expired.remove(0), false);
//            processExpiredAlarm(NOTIF_LIST_FILE_ID, true);
        }
        if (multipleUpdatesOngoing) {
            updateLocalNotificationsOnChange(now);
            multipleUpdatesOngoing = false;
        }
        DAO.getInstance().triggerParseUpdate();
//        expiredAlarmSaveXXX();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void cancelAllExpiredAlarms(Item item) {
//        Iterator<ExpiredAlarm> it = expiredAlarms.iterator();
//        while (it.hasNext()) {
//            ExpiredAlarm expiredAlarm = it.next();
////            if (item.getObjectIdP().equals(expiredAlarm.objectId)) {
//            if (item.getGuid().equals(expiredAlarm.guid)) {
////                expiredAlarms.remove(expiredAlarm);
//                it.remove();
//            }
//        }
//        expiredAlarmSaveXXX();
//    }
//    private void cancelExpiredAlarmsOnItemUpdateXXX(Item item) {
////        if (item.getObjectIdP() != null) { //can't have any alarms for an not yet saved item
//        long now = MyDate.currentTimeMillis();
//        boolean normalAlarmUpdatedToFuture = item.getAlarmDate().getTime() > now;
//        boolean waitingAlarmUpdatedToFuture = item.getWaitingAlarmDate().getTime() > now;
////        AlarmType normalAlarmUpdated = item.getAlarmDate().getTime()>now;
////        boolean waitingAlarmUpdated = item.getWaitingAlarmDate().getTime()>now;
//        Iterator<ExpiredAlarm> it = expiredAlarms.iterator();
//        while (it.hasNext()) {
//            ExpiredAlarm expiredAlarm = it.next();
////                if (item.getObjectIdP().equals(expiredAlarm.objectId)) {
//            if (item.getGuid().equals(expiredAlarm.guid)) {
//                if (((expiredAlarm.type == AlarmType.notification || expiredAlarm.type == AlarmType.snoozedNotif) && normalAlarmUpdatedToFuture)
//                        || ((expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.snoozedWaiting) && waitingAlarmUpdatedToFuture)) {
//                    it.remove();
//                }
//            }
//        }
//        expiredAlarmSaveXXX();
////        }
//    }
    /**
     * eg when an Item is Done/Cancelled
     */
//    public void cancelAllFutureAlarms(Item item) {
//        List<AlarmRecord> futureAlarms = item.getAllFutureAlarmRecordsSorted();
//        for (AlarmRecord alarmRecord: futureAlarms) {
//            cancelAlarm(NOTIF_LIST_FILE_ID);
//        }
//        while (!expiredAlarms.isEmpty()) {
//            expiredAlarms.remove(0);
//        }
//        expiredAlarmSave();
//    }
    /**
     * call eg if alarms are turned off or on on the devices
     */
//    public void cancelAllAlarmsXXX() {
////                    AlarmHandler.getInstance().removeExpiredAlarm(expired);
//        while (!expiredAlarms.isEmpty()) {
//            expiredAlarms.remove(0);
//        }
//        expiredAlarmSave();
//    }
//</editor-fold>
    /**
     * called by inApp timer or from local notification when an alarm expires
     *
     * @param notificationId
     * @param localOSNotificationReceived a local (system/iOS) notification was
     * received, otherwise, if false, call was made for an inApp timer
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    void processExpiredAlarmOLD(String notificationId, boolean localNotificationReceived) {
//        alarmLog("processExpiredAlarm called with: \"" + notificationId + "\"");
////        Display.getInstance().callSerially(() -> {
////        Display.getInstance().callSeriallyOnIdle(() -> { //onIdle may ensure that the alarm only gets processed after Replay and load of data
//        Display.getInstance().callSerially(() -> { //onIdle may ensure that the alarm only gets processed after Replay and load of data
//            if (localNotificationReceived) {
//                alarmLog("Local notifiation received: \"" + notificationId + "\"");
//            } else {
//                alarmLog("InApp alarm expired: \"" + notificationId + "\"");
//            }
//            if (notificationList != null && !notificationList.isEmpty()) { //shouldn't happen, but in case desync between notificationList and iOS notifications can happen
//                NotificationShadow notif = notificationList.getNotification(notificationId);
//                if (notif != null) {
//                    ASSERT.that(notif != null, "ALARM: NotifShadow==null for notificationId=" + notificationId + " notificationList=" + notificationList);
////        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
////        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId, type);
//                    notificationList.removeAlarmAndRepeatAlarm(notificationId);
////                    notificationList.save();
//                    saveNotificationListXXX();
////        expiredAlarms.add(0, new ExpiredAlarm(notif.getObjectIdStr(), notif.alarmTime, notif.type));
//                    expiredAlarms.add(0, new ExpiredAlarm(notif)); //TODO!!!!! only add each item ONCE, to avoid seeing multiple expired alarms/snoozes?
//                    expiredAlarmSaveXXX();
//
//                    inAppTimer.updateInAppTimerOnNextcomingAlarm();
////        if (notif != null) {
////        Display.getInstance().callSerially(() -> {
//                    Display.getInstance().callSerially(() -> playAlarm()); //try to play
////                    ScreenListOfAlarms.getInstance().show(); //will check if already visible
//                    MyForm myForm = MyForm.getCurrentFormAfterClosingDialogOrMenu();
////                    if ((Config.PRODUCTION_RELEASE && myForm != null) || !Config.PRODUCTION_RELEASE)
//                    if (myForm instanceof ScreenListOfAlarms) {
//                        myForm.refreshAfterEdit();
//                    } else if ((!Config.PRODUCTION_RELEASE || myForm != null) && (myForm != null && myForm.getMyShowAlarmsReplayCmd() != null)) { //don't risk crash in production release
//                        myForm.getMyShowAlarmsReplayCmd().actionPerformed(null);
//                    }
//                }
//            }
//        });
////        }
//        //get next alarm in line and schedule it //UI: will only schedule one alarm at a time (not relevant for user?!)
//    }
//</editor-fold>
    void processExpiredAlarm(String notificationId, boolean localOSNotificationReceived) {
        alarmLog("processExpiredAlarm called with: \"" + notificationId + "\"");
        if (localOSNotificationReceived) {
            alarmLog("Local notifiation received: \"" + notificationId + "\"");
        } else {
            alarmLog("InApp alarm expired: \"" + notificationId + "\"");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false&&notificationList != null && !notificationList.isEmpty()) { //shouldn't happen, but in case desync between notificationList and iOS notifications can happen
//            NotificationShadow notif = notificationList.getNotification(notificationId);
//            if (notif != null) {
////                ASSERT.that(notif != null, "ALARM: NotifShadow==null for notificationId=" + notificationId + " notificationList=" + notificationList);
//                notificationList.removeAlarmAndRepeatAlarm(notificationId);
//                saveNotificationListXXX();
//                expiredAlarms.add(0, new ExpiredAlarm(notif)); //TODO!!!!! only add each item ONCE, to avoid seeing multiple expired alarms/snoozes?
//                expiredAlarmSaveXXX();
//
//                inAppTimer.updateInAppTimerOnNextcomingAlarm();
//                Display.getInstance().callSerially(() -> playAlarm()); //try to play
////                    ScreenListOfAlarms.getInstance().show(); //will check if already visible
//            }
//        }
//</editor-fold>
        if (!localOSNotificationReceived) {
            Display.getInstance().callSerially(() -> playAlarm()); //try to play 
//            playAlarm(); //make sure it plays immediately, not e.g. after displaying the alarmscreen
        }
//        notificationList.updateInAppTimerOnNextcomingAlarm(); 
        MyForm myForm = MyForm.getCurrentFormAfterClosingDialogOrMenu();
//                    if ((Config.PRODUCTION_RELEASE && myForm != null) || !Config.PRODUCTION_RELEASE)
        if (myForm instanceof ScreenListOfAlarms) {
            myForm.refreshAfterEdit();
        } else if ((!Config.PRODUCTION_RELEASE || myForm != null) && (myForm != null && myForm.getMyShowAlarmsReplayCmd() != null)) { //don't risk crash in production release
            myForm.getMyShowAlarmsReplayCmd().actionPerformed(null);
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * show the dialog when clicking on a received local notification. Options
     * normal Alarm: snooze, snoozeManuallySetTime (longPress?!, select either
     * hours/minutes, or new alarmDate), cancelNotification, startTimer,
     * editItem (to eg manually edit a new alarm). Options WaitingAlarm: snooze,
     * cancel, startTimer+cancelAlarm (assuming whatevery was waited for got
     * done), editItem.
     */
//    void showNotificationReceivedDialogXXX(String notificationId) {
//        showNotificationReceivedDialogXXX(notificationList.getNotification(notificationId));
//    }
//
//    private void showNotificationReceivedDialogXXX(NotificationShadow notif) {
////    void showNotificationReceivedDialog(NotificationShadow notif) {
//        //DONE in CN1.Dialog.java: if(getUIManager().isThemeConstant("dlgCommandGridBool", true)) then creates a FlowLayout for commands
//        //DONE!! add a TimePicker with default snooze time, so it can easily be changed (set an actionlistener on picker that will set snooze with new time so user doesn't have to press snooze button after editign the snooze time)
//
//        String objectId = AlarmType.getObjectIdStrWithoutTypeStr(notif.notificationId);
//        Item item = DAO.getInstance().fetchItem(objectId); //can we assume app is started here?
//        assert item != null : "";
//
//        //get current form
////<editor-fold defaultstate="collapsed" desc="comment">
////        Form current = Display.getInstance().getCurrent();
////        if (current instanceof Dialog) {
////            ((Dialog) current).dispose(); //UI: any open dialog is automatically closed on a reminder notification
////            current = Display.getInstance().getCurrent();
////        } else if (current != null && current.getClientProperty("cn1$sideMenuParent") != null
////                && current.getClientProperty("cn1$sideMenuParent") instanceof SideMenuBar) {
////            //TOD!!!!: HACK to check if menu is open (if this property disappears, the alarm dialog should simply return to main form)
////            ((SideMenuBar) current.getClientProperty("cn1$sideMenuParent")).closeMenu();
////            current = Display.getInstance().getCurrent();
////        }
////</editor-fold>
//        Form current = MyForm.getCurrentFormAfterClosingDialogOrMenu();
////        if (current instanceof MyForm) {
////            lastForm = (MyForm) current; //need to store the last 'real' form before showing the dialog
////        } else if (current instanceof Form && ((Form) current).getParentForm() instanceof MyForm)
////            lastForm = (MyForm)((SideMenuBar) current).getParentForm(); //need to store the last 'real' form before showing the dialog
//        MyForm lastForm = current instanceof MyForm ? (MyForm) current : null; //need to store the last 'real' form before showing the dialog
//
//        //CANCEL any pending alarms (alarms, repeats, snooze) for this (repeat alarms have no meaning when app is running and should be cancelled once an alarm has been dealt with in the dialog)
//        notificationList.removeAlarmAndRepeatAlarm(notif.notificationId); //cancel any set repeats //UI: once you click on a notification, it will not repeat
////        NotificationShadow notif = notificationList.getNotification(notificationId);
//
//        Dialog snoozeDialog = new Dialog();
//
//        Command cmdSnooze = Command.create(null, Icons.iconSnoozeLabelStyle, (e) -> { //"Snooze"
//            //HANDLE LOCAL NOTIFICATIONS
//            Date snoozeExpireTimeInMillis = new Date(System.currentTimeMillis() + MyPrefs.alarmDefaultSnoozeTimeInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //UI: snooze interval always from the moment you activate snooze
////            if (notificationId.endsWith(ALARM_KEY)) {
//            if (notif.type == AlarmType.notification) {
////                setOrUpdateAlarmAndCancelPrevious(item, snoozeExpireTimeInMillis, AlarmHandlerXXX.AlarmType.reminderAlarm, false); //no matter when sooze expires, it should trigger a local notification
//                notificationList.snoozeAlarm(notif.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));
////            } else if (notificationId.endsWith(WAITING_KEY)) {
//            } else if (notif.type == AlarmType.waiting) {
////                setOrUpdateWaitingAlarmCancelPrevious(item, new Date(System.currentTimeMillis() + MyPrefs.getInt(MyPrefs.alarmDefaultSnoozeTimeInMinutes) * MyDate.MINUTE_IN_MILLISECONDS));
////                setOrUpdateAlarmAndCancelPrevious(item, snoozeExpireTimeInMillis, AlarmHandlerXXX.AlarmType.waitingAlarm, false);
//                notificationList.snoozeAlarm(notif.notificationId, snoozeExpireTimeInMillis, item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));
//            }
//            //HANDLE APP NOTIFICATIONS
////            getInstance().setInAppTimerForItemsWithAlarmOrSnoozed(notificationId, snoozeExpireTimeInMillis); //check if snoozeTime should replace currently scheduled, otherwise add to snooze list
//            refreshInAppTimerAndSaveNotificationList();
//            snoozeDialog.dispose();
//        });
//        Command cmdCancelAlarm = Command.create(null, Icons.iconAlarmOffLabelStyle, (e) -> { //"Cancel"
////            cancelNotification(notificationId); //cancel any set repeats
////            getInstance().setInAppTimerForItemsWithAlarmOrSnoozed(); //update
//            notificationList.removeAlarmAndRepeatAlarm(notif.notificationId); //update
//            refreshInAppTimerAndSaveNotificationList();
//            snoozeDialog.dispose();
//        });
//        Command cmdStartTimer = Command.create(null, Icons.iconTimerSymbolLabelStyle, (e) -> { //"Start Timer"
////            getInstance().setInAppTimerForItemsWithAlarmOrSnoozed(); //update
//            refreshInAppTimerAndSaveNotificationList();
//            snoozeDialog.dispose();
//            ScreenTimer.getInstance().startInterrupt(item, lastForm); //edit the item
//        });
//        Command cmdEditItem = Command.create(null, Icons.iconEditSymbolLabelStyle, (e) -> { //"Edit"
//            //editing the item will automatically set/cancel any edited alarms
////            lastForm = (MyForm) Display.getInstance().getCurrent(); //need to store the last 'real' form before showing the dialog
//            //TOD!!! any risk that whatever was ongoing on the current form (the active one when notification is shown) can be lost? Eg does ScreenItem return to previous form as it was?
////            getInstance().setInAppTimerForItemsWithAlarmOrSnoozed(); //update
//            refreshInAppTimerAndSaveNotificationList();
//            snoozeDialog.dispose();
////            Display.getInstance().callSeriallyAndWait(()->{
//            new ScreenItem(item, lastForm, () -> {
//                //UI: if editing the item is cancelled, do nothing (leave any repeats)
//            }).show();
////            });
//        });
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        String prefixText;
////        if (notificationId.endsWith(ALARM_KEY)) {
////            prefixText = ALARM_TEXT;
////        } else {
////            prefixText = WAITING_ALARM_TEXT;
////        }
////        prefixText = notif.type == NotificationType.notification ? ALARM_TEXT : WAITING_ALARM_TEXT;
////        Display.getInstance().callSerially(new Runnable() { // https://groups.google.com/forum/#!topic/codenameone-discussions/Z_4vn2MhYV8
////            public void run() {
////                Dialog.show("Reminder", new SpanLabel(prefixText + item.getText()), null, new Command[]{cmdSnooze, cmdCancelAlarm, cmdStartTimer, cmdEditItem}, Dialog.TYPE_ALARM, null, 0, null);
////            }
////        });
////</editor-fold>
//        snoozeDialog.setDialogType(Dialog.TYPE_ALARM);
//        if (notif.type == AlarmType.waiting || notif.type == AlarmType.waitingRepeat) {
//            snoozeDialog.setTitle("WAITING");
//        } else {
//            snoozeDialog.setTitle("Reminder");
//        }
//        //Dialog title + body
//        snoozeDialog.addComponent(new SpanLabel(item.makeNotificationTitleText(notif.type) + "\n" + item.makeNotificationBodyText(notif.type)));
//        snoozeDialog.setTransitionOutAnimator(CommonTransitions.createCover(CommonTransitions.SLIDE_VERTICAL, true, 300)); //TOD!!! make it pop out from center or drop down from top... to make it clear this is coming from somewhere else than normal dialogs
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        dialog.setTransitionOutAnimator(transition);
////        snoozeDialog.addCommand(cmdSnooze);
////        snoozeDialog.addCommand(cmdCancelAlarm);
////        snoozeDialog.addCommand(cmdStartTimer);
////        snoozeDialog.addCommand(cmdEditItem);
////</editor-fold>
//        snoozeDialog.placeButtonCommands(new Command[]{cmdSnooze, cmdCancelAlarm, cmdStartTimer, cmdEditItem});
//        snoozeDialog.addComponent(new SpanLabel(item.getText()));
//        snoozeDialog.setAutoDispose(true);
//        snoozeDialog.setBlurBackgroundRadius(8);
//
//        Display.getInstance().callSerially(() -> { // https://groups.google.com/forum/#!topic/codenameone-discussions/Z_4vn2MhYV8
////                snoozeDialog = Dialog.("Reminder", new SpanLabel(prefixText + item.getText()), null, new Command[]{cmdSnooze, cmdCancelAlarm, cmdStartTimer, cmdEditItem}, Dialog.TYPE_ALARM, null, 0, null);
////            Dialog.show("Reminder", new SpanLabel(prefixText + item.getText()), null, new Command[]{cmdSnooze, cmdCancelAlarm, cmdStartTimer, cmdEditItem}, Dialog.TYPE_ALARM, null, 0, null);
//            snoozeDialog.show();
//        });
//    }
//</editor-fold>
//    public void localNotificationReceivedXXX(String notificationId) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        Display.getInstance().callSerially(() -> {
////            AlarmHandler.alarmLog("Local notifiation received: \"" + notificationId + "\"");
////        });
////        Display.getInstance().callSerially(() -> {
////DONE show as modal dialog to ensure user deals with the alarm (not Toastbar which may disappear)
////            showNotificationReceivedDialog(notificationId);
////            ScreenListOfAlarms.getInstance().show();
////</editor-fold>
//        processExpiredAlarm(notificationId, true);
////        });
//    }
    /**
     * return list of already expired alarms (but not yet processed by the
     * end-user). Used eg by ScreenMain to show the number of expired alarms and
     * by SCreenAlarm to get the alarms to display
     *
     * @return
     */
    public List<AlarmRecord> getUnprocessedAlarms() {
        return getUnprocessedAlarms(new MyDate());
    }

    public List<AlarmRecord> getUnprocessedAlarms(Date now) {
//        return expiredAlarms;
        return DAO.getInstance().getUnprocessedAlarmRecords(now, false);
    }

//    public ItemList getExpiredAlarmsItemList() {
//        return new ItemList(getExpiredAlarms()); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below
//    }
//    public LocalNotificationsShadowList getLocalNotificationsTEST() {
    public List<NotificationShadow> getLocalNotificationsTEST() {
//        updateLocalNotificationsOnChange();
//        notificationList.updateWithFutureAlarms(false);
//        return notificationList;
        return notificationList.getUpdatedListOfAlarmRecords();
    }
////<editor-fold defaultstate="collapsed" desc="comment">
//    NotificationShadow getNextFutureAlarmN() {
////        return notificationList.getNextFutureAlarm();
//        NotificationShadow notif;
//        Long now = MyDate.currentTimeMillis();
////remove expired alarms and add them to expiredAlarms
////        if (false) { //now done in processExpiredAlarm()
////            while ((notif = notificationList.getNextFutureAlarmN()) != null && notif.alarmTime.getTime() <= now) {
////                notificationList.removeAlarmAndRepeatAlarm(notif.notificationId);
////                expiredAlarms.add(0, new ExpiredAlarm(notif)); //UI: add most recent alarms to start
////            }
//////            notificationList.save();
////            saveNotificationList();
////            expiredAlarmSave();
////        } else {
////            notif = notificationList.getNextFutureAlarmN();
//        notif = getNotificationList().getNextFutureAlarmN();
////        }
////        return notificationList.getNextFutureAlarm();
//        return notif;
//    }
////</editor-fold>

    public void simulateNotificationReceived_TEST(int secondsFromNow) {
        String notificationId;
        Item testItem = new Item("test local notification", 20, new MyDate(MyDate.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * 48));
        testItem.setAlarmDate(new MyDate(MyDate.currentTimeMillis() + secondsFromNow * 1000)); //alarm in 10s from now
//        DAO.getInstance().saveNew(testItem, true);
//        DAO.getInstance().saveNew(testItem);
//        DAO.getInstance().saveNewTriggerUpdate3();
        DAO.getInstance().saveToParseNow(testItem);
    }

    public void simulateNotificationReceived_TEST(String taskText, Date due, Date alarm, Date waiting) {
        Item testItem = new Item(taskText, 7, due);
        testItem.setAlarmDate(alarm);
        testItem.setWaitingAlarmDate(waiting);
//        DAO.getInstance().saveNew(testItem, true);
//        DAO.getInstance().saveNew(testItem);
//        DAO.getInstance().saveNewTriggerUpdate3();
        DAO.getInstance().saveToParseNow(testItem);
    }

    public void simulateNotificationReceived_TEST(String taskText, Date due, int alarmInSecondsFromNow, int waitingAlarmInSecondsFromNow) {
        simulateNotificationReceived_TEST(taskText, due, new MyDate(MyDate.currentTimeMillis() + alarmInSecondsFromNow * 1000), new MyDate(MyDate.currentTimeMillis() + waitingAlarmInSecondsFromNow * 1000));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static void setPreferredBackgroundFetchIntervalXXX() {
////max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
////int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4);
//        int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4);
//        Display.getInstance().setPreferredBackgroundFetchInterval(fetchIntervalSeconds); //max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
//    }
//    public void receivePushUpdateXXX() {
//        switch ("") {
//            case "alarmSnoozed": //snooze expiredAlarm (move expired alarm back to snoozed)
//            case "alarmCancelled": //cancel expiredAlarm (cancel local alarm and remove from expiredAlarms)
//            case "changedItem": //refresh alarms (update alarms as if item was edited locally)
//            case "newItem": //update with alarms (ditto)
//            case "itemDeletedCancelled": //cancel alarms (ditto)
//        }
//    }
//</editor-fold>

    private String mediaToString( Media alarmSound) {
        String s="dur(ms)="+alarmSound.getDuration()+"; time(s)="+alarmSound.getTime()+"; vol(%)="+alarmSound.getVolume();
        return s;
    }
    
    /*static*/ void playAlarm() {
        if (false && MyPrefs.alarmPlayBuiltinAlarmSound.getBoolean()) {
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ALARM); //work-around but sound doesn't seem to work on iOS, nor on simulator
        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
// https://www.codenameone.com/manual/files-storage-networking.html#_file_system
// CN1 doc: https://www.codenameone.com/javadoc/com/codename1/media/Media.html
// https://www.codenameone.com/blog/open-file-rendering.html
// https://www.codenameone.com/manual/components.html#sharebutton-section
// https://stackoverflow.com/questions/48076190/codename-one-play-a-sound
//</editor-fold>
            alarmLog("alarmSound.play() start");
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String soundDir = fs.getAppHomePath(); // + "recordings/"; on simulator="file://home/" 
//<editor-fold defaultstate="collapsed" desc="comment">
//        fs.mkdir(soundDir);
//        Media media = MediaManager.createBackgroundMedia("file://"+);
//            Media m = MediaManager.createMedia(soundDir + "/" + MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//            Media m = MediaManager.createMedia("file://" + "/" + MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//            Media m = MediaManager.createMedia( MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//                Media m = MediaManager.createMedia(soundDir + MyPrefs.getString(MyPrefs.alarmSoundFile), false); //in SImulator: put mp3 file in .cn1!
//https://stackoverflow.com/questions/48076190/codename-one-play-a-sound
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/notification_sound_bell.mp3")), "audio/mpeg");
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/"+MyPrefs.alarmSoundFile)), "audio/mpeg"); //doesn't work in static
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/notification_sound_bell.mp3")), "audio/mpeg");
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(),"/" + MyPrefs.getString(MyPrefs.alarmSoundFile))), "audio/mpeg"); //in SImulator: put mp3 file in .cn1!
//</editor-fold>
            //https://stackoverflow.com/questions/48076190/codename-one-play-a-sound
            try {
                if (alarmSound == null) {
//                    alarmSound = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/" + MyPrefs.getString(MyPrefs.alarmSoundFileXXX))), "audio/mpeg"); //in SImulator: put mp3 file in .cn1!
                    alarmLog("alarmSound==null, calling createMedia()");
                    alarmSound = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/" + MyPrefs.getString(MyPrefs.alarmSoundFile))), "audio/mpeg"); //in SImulator: put mp3 file in .cn1!
                }
                if (!alarmSound.isPlaying()) { //uI: two alarms at almost same time will only play if first is finished, avoid overlapping/interrupting sounds
                    alarmLog("alarmSound.play() - " + "/" + MyPrefs.getString(MyPrefs.alarmSoundFile)+"; alarmSound="+mediaToString(alarmSound));
                    alarmSound.play();
                    alarmLog("alarmSound.play() finished ");
                } else {
                    alarmLog("alarmSound ALREADY playing - IGNORED");
                }
            } catch (Exception err) {
                if (true) {
//                    Log.e(err);
                    ASSERT.that("\"ALARM: Exception in MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), \"/\" + MyPrefs.getString(MyPrefs.alarmSoundFile))), \"audio/mpeg\"). Exception=" + err);
                }
//                Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_INFO);
                if (false) {
                    Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ALARM);
                }
            }
            alarmLog("alarmSound.play() end");
        }
    }
}
