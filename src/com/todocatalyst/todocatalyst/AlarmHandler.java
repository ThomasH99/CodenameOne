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

    static final int MAX_NUMBER_LOCAL_NOTIFICATIONS = 64;

//    private final static String WAITING_KEY = "-[WAITING]";     // postfix to identify a waiting alarm
//    private final static String ALARM_KEY = "-[ALARM]"; // postfix to identify an alarm
//    private final static String WAITING_ALARM_TEXT = "WAITING: ";// reminder for: \n"; "[Waiting]"
//    private final static String ALARM_TEXT = "";//Reminder for: \n";
//    private final static String ALARM_REPEAT_STR = "-REP";//Used to create a separate notification ID for a repeat reminder (must also be used to cancel)
    /**
     * keep a copy of all local notifications set
     */
    private LocalNotificationsShadowList notificationList; // = new LocalNotificationsShadowList();
    /**
     * list of already expired alarms not yet processed by the end-user, to show
     * in AlarmScreen to snooze, cancel etc
     */
    private List<ExpiredAlarm> expiredAlarms;
    private AlarmInAppAlarmHandler inAppTimer; // = new AlarmInAppAlarmHandler(notificationList);

    private AlarmHandler() {
        if (true || Storage.getInstance().exists(NOTIF_LIST_FILE_ID)) { //readObject below return null if no file exists
            //TODO: catch any reading/format problems and recreate the file
            notificationList = (LocalNotificationsShadowList) Storage.getInstance().readObject(NOTIF_LIST_FILE_ID);
        } else {
            notificationList = null;
        }
        if (notificationList == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
//            notificationList = new LocalNotificationsShadowList(this); //create if none existed before
            notificationList = new LocalNotificationsShadowList(); //create if none existed before
//                save(); //DON'T save before something is added
        }

        notificationList.setAlarmHandler(this);

        if (Storage.getInstance().exists(EXPIRED_ALARMS_FILE_ID)) {
            //TODO: catch any reading/format problems and recreate the file
            expiredAlarms = (List<ExpiredAlarm>) Storage.getInstance().readObject(EXPIRED_ALARMS_FILE_ID);
        }
        if (expiredAlarms == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
            expiredAlarms = new ArrayList<ExpiredAlarm>(); //create if none existed before
//                save(); //DON'T save before something is added
        }
//        inAppTimer = new AlarmInAppAlarmHandler(notificationList);
        inAppTimer = new AlarmInAppAlarmHandler();
//        inAppTimer.startInAppTimerOnNextcomingAlarm(notificationList.getNextFutureAlarmN());
//        inAppTimer.updateInAppTimerOnNextcomingAlarm();
    }

    private static AlarmHandler INSTANCE;

    public static AlarmHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlarmHandler();
            INSTANCE.inAppTimer.updateInAppTimerOnNextcomingAlarm(); //MUST do here and not in singleton instantiation of AlarmHandler() above to avoid infinite loop
        }
        return INSTANCE;
    }

    private void expiredAlarmSave() {
        Storage.getInstance().writeObject(EXPIRED_ALARMS_FILE_ID, expiredAlarms);
    }

    static void alarmLog(String s) {
        Log.p("ALARM: " + s);
    }

    /**
     * sets a local notification
     *
     * @param item
     * @param notificationText
     * @param alarmDate
     */
    static void scheduleLocalNotification(String notificationId, String titleText, String bodyText, Date alarmDate) {
        Display.getInstance().cancelLocalNotification(notificationId); //always cancel any previous set local notifs, just in case
        alarmLog("Set local notification " + notificationId + " alarm=" + MyDate.formatDateTimeNew(alarmDate) + " for \"" + titleText + "\" (" + bodyText + ")");
        if (alarmDate.getTime() < MyDate.currentTimeMillis()) {
            return;
        }
        LocalNotification n = new LocalNotification();
        n.setId(notificationId);
        n.setAlertTitle(titleText);
        n.setAlertBody(bodyText);
        n.setAlertSound("/" + MyPrefs.alarmSoundFileLocalNotif.getString());
        Display.getInstance().scheduleLocalNotification(n, alarmDate.getTime(), LocalNotification.REPEAT_NONE);
    }

    static void cancelLocalNotification(String notificationId) {
        AlarmHandler.alarmLog("Local notifiation cancelled: \"" + notificationId + "\"");
        Display.getInstance().cancelLocalNotification(notificationId);
    }

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
    public void updateLocalNotificationsOnBackgroundFetch() {
        if (false && !MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) { //DONE: optimization: rather not *schedule* this if setting is deactivate and avoid need to reload preferences
            return;
        }

        //optimization: check if there are active alarms within the next 60min (fetch interval) and if yes, waith with fetching till next time
        //TODO!!!! how to set alarms for alarms edited on other devices? Still via push?!
//        int maxNbFreeNotifications = notificationList.getNumberAvailableLocalNotificationSlots();
//        List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(LocalNotificationsShadowList.MAX_NUMBER_LOCAL_NOTIFICATIONS);
        List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS);
        if (newAlarmsList != null && newAlarmsList.size() > 0) { //only do something if we successfully got the list (avoid cancelling alarms if anything went wrong with the fetch)

            notificationList.cancelAndRemoveAllAvailableLocalNotifications();
            Log.p("updateLocalNotificationsOnBackgroundFetch():cancelling local notifications");

            Item item;
            while (notificationList.getNumberAvailableLocalNotificationSlots() >= 2 && !(newAlarmsList.isEmpty())) {
                item = newAlarmsList.remove(0);
                if (item.getNextcomingAlarmRecordN() == null) {
                    Log.p("updateLocalNotificationsOnBackgroundFetch(): getNextcomingAlarmRecordN()==null!!! for item=" + item);
                } else {
                    Log.p("updateLocalNotificationsOnBackgroundFetch(): setting notification for Item=" + item + "; alarmTime=" + MyDate.formatDateNew(item.getNextcomingAlarmRecordN().alarmTime));
                }
                notificationList.addAlarmAndRepeat(item, item.getNextcomingAlarmRecordN());
            }
            refreshInAppTimerAndSaveNotificationList();
        } else 
                    Log.p("updateLocalNotificationsOnBackgroundFetch(): DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS) returned empty list!");
    }

    /**
     * Called on app start or if alarms are globally enabled or disabled (via
     * settings)
     */
    public void updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled() {
        if (!MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
            //disable all
            notificationList.cancelAndRemoveAllAvailableLocalNotifications();
            refreshInAppTimerAndSaveNotificationList();
            return;
        } else {
            //update, or set from scratch, all alarms
//        int maxNbFreeNotifications = notificationList.getNumberAvailableLocalNotificationSlots();
//            List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(LocalNotificationsShadowList.MAX_NUMBER_LOCAL_NOTIFICATIONS);
            List<Item> newAlarmsList = DAO.getInstance().getItemsWithNextcomingAlarms(MAX_NUMBER_LOCAL_NOTIFICATIONS);
            if (newAlarmsList != null && newAlarmsList.size() > 0) { //only do something if we successfully got the list (avoid cancelling alarms if anything went wrong with the fetch)
                notificationList.cancelAndRemoveAllAvailableLocalNotifications();
                Item item;
                while (notificationList.getNumberAvailableLocalNotificationSlots() >= 2 && !(newAlarmsList.isEmpty())) {
                    item = newAlarmsList.remove(0);
//                    notificationList.addAlarmAndRepeat(item, item.getNextcomingAlarmRecordN());
                    notificationList.addAlarmAndRepeat(item);
                }
                refreshInAppTimerAndSaveNotificationList();
            }
        }
    }

    private void saveNotificationList() {
        Storage.getInstance().writeObject(AlarmHandler.NOTIF_LIST_FILE_ID, notificationList);
    }

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
    private void refreshInAppTimerAndSaveNotificationList() {
        inAppTimer.updateInAppTimerOnNextcomingAlarm();
//        notificationList.save();
        saveNotificationList();
    }

    private void updateAlarmsOrTextForItem(Item item) {
//        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getObjectIdP(), item.getAlarmDate(), AlarmType.notification,
        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getGuid(), item.getAlarmDate(), AlarmType.notification,
                item.makeNotificationTitleText(AlarmType.notification), item.makeNotificationBodyText(AlarmType.notification));

//        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getObjectIdP(), item.getWaitingAlarmDate(), AlarmType.waiting,
        notificationList.addOrUpdateOrDeleteAlarmAndRepeat(item.getGuid(), item.getWaitingAlarmDate(), AlarmType.waiting,
                item.makeNotificationTitleText(AlarmType.waiting), item.makeNotificationBodyText(AlarmType.waiting));

        if (false) {
            removeExpiredAlarm(null);
        }
//        inAppTimer.refreshInAppTimer();
//        notificationList.save();
        refreshInAppTimerAndSaveNotificationList();
    }

    public void updateOnItemChange(Item item) {
        if (item.isDone()) {
            deleteAllAlarmsForItem(item); //remove any future alarms for a Done/Cancelled task
        } else {
            updateAlarmsOrTextForItem(item);
            cancelExpiredAlarmsOnItemUpdate(item);
        }
    }

    //NORMAL (REMINDER) ALARMS
    /**
     * called when user cancels an alarm in the popup dialog. Will also cancel
     * any repeat alarm
     *
     * @param notificationId
     */
    public void cancelAlarm(String notificationId) {
//        String objId = NotificationType.getNotifIdStrWithoutTypeStr(notificationId);
//        NotificationType type = NotificationType.getTypeContainedInStr(objId);
//        notificationList.removeAlarm(notificationId);
//        //if the user cancels the first alarm, we must cancel any repeat alarms
//        if (type == NotificationType.notification || type == NotificationType.waiting) {
//            notificationList.removeAlarm(type.addTypeStrToStr(objId));
//        }
//        inAppTimer.refreshInAppTimer();
        notificationList.removeAlarmAndRepeatAlarm(notificationId);
//        inAppTimer.refreshInAppTimer();
//        notificationList.save();
        refreshInAppTimerAndSaveNotificationList();
    }

    /**
     * deletes all the alarms (both local notifications and app alarms and
     * snooze) that may have been set for an Item, e.g. if the item is cancelled
     * or deleted.
     *
     * @param item
     */
    public void deleteAllAlarmsForItem(Item item) {
        //delete all possible alarms set for this item
//        notificationList.removeAllAlarms(item.getObjectId());
//        notificationList.removeALLAlarmsForItem(item.getObjectIdP());
        notificationList.removeALLAlarmsForItem(item.getGuid());
        cancelAllExpiredAlarms(item); //remove any already expired alarms
//        inAppTimer.refreshInAppTimer();
//        notificationList.save();
        refreshInAppTimerAndSaveNotificationList();
    }

    /**
     * initialization of the alarm handling. Called on each start up of the app.
     * Will update local notifications. Also launches timer to handle in-app
     * (while app is running) alarms. NB! Initiating the background
     * fetchFromCacheOnly task to update local notifications regularly even if
     * app is not activated is done in the main start().
     */
    public void setupAlarmHandlingOnAppStart() {
        updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled();
        //remove any snoozed alarms in the past (since they have been handled by local notifications)
//        long now = System.currentTimeMillis();
//        if (false) {
//            notificationList.removeExpiredAlarmsXXX(); //ScreenListOfAlarms will show expired alarms until they're dealt with
//        }//        inAppTimer.refreshInAppTimer();
//        notificationList.save();
        if (false) {
            refreshInAppTimerAndSaveNotificationList(); // done in updateLocalNotificationsOnBackgroundFetchOrAppStart()
        }//        while (alarmData.getSnoozeTimeForItemWithSmallestTime()!=null && alarmData.getSnoozeTimeForItemWithSmallestTime().getTime() < now) {
//            alarmData.removeSnoozeForItemWithSmallestTime();
//        }
//        setInAppTimerForItemsWithAlarmOrSnoozed();
    }

    /**
     *
     * @param expiredAlarm
     * @param snoozeExpireTime
     */
//    public void snoozeAlarm(String notificationId, Date snoozeExpireTime) {
    public void snoozeAlarm(ExpiredAlarm expiredAlarm, Date snoozeExpireTime) {
//        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
//        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId, type);
        Item item = DAO.getInstance().fetchItemN(expiredAlarm.guid);
        expiredAlarms.remove(expiredAlarm);
        expiredAlarmSave();
        item.setSnoozeAlarmRecord(new AlarmRecord(snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type))); //Update *before* creating reminder to get right snooze time
        notificationList.removeAlarmAndRepeatAlarm(expiredAlarm.guid, expiredAlarm.type); //on snooze, remove any still active alarms of the same type as the snoozed one
//        notificationList.snoozeAlarm(notificationId, snoozeExpireTime, item.makeNotificationTitleText(type), item.makeNotificationBodyText(type));
        notificationList.addAlarmAndRepeat(expiredAlarm.guid, snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type),
                item.makeNotificationTitleText(expiredAlarm.type), item.makeNotificationBodyText(expiredAlarm.type)); //UI: snooze also has localNotification repeat, NO, finally disabled
//        notificationList.save();
        refreshInAppTimerAndSaveNotificationList();
        //save snooze time
//        item.setSnoozeDate(snoozeExpireTime);
//        item.setSnoozeAlarmRecord(new AlarmRecord(snoozeExpireTime, AlarmType.getSnoozedN(expiredAlarm.type)));
//        DAO.getInstance().saveNew(item, true);
//        DAO.getInstance().saveNew(item);
//        DAO.getInstance().saveItem3(item);xxx;
//        DAO.getInstance().saveNewTriggerUpdate3();
        DAO.getInstance().saveToParseNow(item);
    }

    public void snoozeAllExpiredAlarms(Date snoozeExpireTime) {
        while (!expiredAlarms.isEmpty()) {
            ExpiredAlarm expiredAlarm = expiredAlarms.get(0); //get() here, since snoozeAlarm() will remove it once snoozed
            snoozeAlarm(expiredAlarm, snoozeExpireTime);
        }
        expiredAlarmSave();
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
     * called when user canels an expired alarm
     *
     * @param expiredAlarm
     */
//    public void removeExpiredAlarm(String objId, AlarmType type) {
    public void removeExpiredAlarm(ExpiredAlarm expiredAlarm) {
//        for (int i=0, size=expiredAlarms.size(); i<size; i++) {
//            if (expiredAlarms.get(i).objectId.equals(objId)&&expiredAlarms.get(i).type==type) {
//                expiredAlarms.re
//            }
//        }
        expiredAlarms.remove(expiredAlarm);
        expiredAlarmSave();
    }

    public void cancelAllExpiredAlarms() {
//                    AlarmHandler.getInstance().removeExpiredAlarm(expired);
        while (!expiredAlarms.isEmpty()) {
            expiredAlarms.remove(0);
        }
        expiredAlarmSave();
    }

    private void cancelAllExpiredAlarms(Item item) {
        Iterator<ExpiredAlarm> it = expiredAlarms.iterator();
        while (it.hasNext()) {
            ExpiredAlarm expiredAlarm = it.next();
//            if (item.getObjectIdP().equals(expiredAlarm.objectId)) {
            if (item.getGuid().equals(expiredAlarm.guid)) {
//                expiredAlarms.remove(expiredAlarm);
                it.remove();
            }
        }
        expiredAlarmSave();
    }

    private void cancelExpiredAlarmsOnItemUpdate(Item item) {
//        if (item.getObjectIdP() != null) { //can't have any alarms for an not yet saved item
        long now = MyDate.currentTimeMillis();
        boolean normalAlarmUpdatedToFuture = item.getAlarmDate().getTime() > now;
        boolean waitingAlarmUpdatedToFuture = item.getWaitingAlarmDate().getTime() > now;
//        AlarmType normalAlarmUpdated = item.getAlarmDate().getTime()>now;
//        boolean waitingAlarmUpdated = item.getWaitingAlarmDate().getTime()>now;
        Iterator<ExpiredAlarm> it = expiredAlarms.iterator();
        while (it.hasNext()) {
            ExpiredAlarm expiredAlarm = it.next();
//                if (item.getObjectIdP().equals(expiredAlarm.objectId)) {
            if (item.getGuid().equals(expiredAlarm.guid)) {
                if (((expiredAlarm.type == AlarmType.notification || expiredAlarm.type == AlarmType.snoozedNotif) && normalAlarmUpdatedToFuture)
                        || ((expiredAlarm.type == AlarmType.waiting || expiredAlarm.type == AlarmType.snoozedWaiting) && waitingAlarmUpdatedToFuture)) {
                    it.remove();
                }
            }
        }
        expiredAlarmSave();
//        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">

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
     * @param localNotificationReceived a local (system/iOS) notification was
     * received, otherwise, if false, call was made for an inApp timer
     */
    void processExpiredAlarm(String notificationId, boolean localNotificationReceived) {
        AlarmHandler.alarmLog("processExpiredAlarm called with: \"" + notificationId + "\"");
//        Display.getInstance().callSerially(() -> {
//        Display.getInstance().callSeriallyOnIdle(() -> { //onIdle may ensure that the alarm only gets processed after Replay and load of data
        Display.getInstance().callSerially(() -> { //onIdle may ensure that the alarm only gets processed after Replay and load of data
            if (localNotificationReceived) {
                AlarmHandler.alarmLog("Local notifiation received: \"" + notificationId + "\"");
            } else {
                AlarmHandler.alarmLog("InApp alarm expired: \"" + notificationId + "\"");
            }
            if (notificationList != null && !notificationList.isEmpty()) { //shouldn't happen, but in case desync between notificationList and iOS notifications can happen
                NotificationShadow notif = notificationList.getNotification(notificationId);
                if (notif != null) {
                    ASSERT.that(notif != null, "ALARM: NotifShadow==null for notificationId=" + notificationId + " notificationList=" + notificationList);
//        AlarmType type = AlarmType.getTypeContainedInStr(notificationId);
//        String objId = AlarmType.getObjectIdStrWithoutTypeStr(notificationId, type);
                    notificationList.removeAlarmAndRepeatAlarm(notificationId);
//                    notificationList.save();
                    saveNotificationList();
//        expiredAlarms.add(0, new ExpiredAlarm(notif.getObjectIdStr(), notif.alarmTime, notif.type));
                    expiredAlarms.add(0, new ExpiredAlarm(notif)); //TODO!!!!! only add each item ONCE, to avoid seeing multiple expired alarms/snoozes?
                    expiredAlarmSave();

                    inAppTimer.updateInAppTimerOnNextcomingAlarm();
//        if (notif != null) {
//        Display.getInstance().callSerially(() -> {
                    Display.getInstance().callSerially(() -> playAlarm()); //try to play 
//                    ScreenListOfAlarms.getInstance().show(); //will check if already visible
                    MyForm myForm = MyForm.getCurrentFormAfterClosingDialogOrMenu();
//                    if ((Config.PRODUCTION_RELEASE && myForm != null) || !Config.PRODUCTION_RELEASE)
                    if (myForm instanceof ScreenListOfAlarms) {
                        myForm.refreshAfterEdit();
                    } else if ((!Config.PRODUCTION_RELEASE || myForm != null) && (myForm != null && myForm.getMyShowAlarmsReplayCmd() != null)) { //don't risk crash in production release
                        myForm.getMyShowAlarmsReplayCmd().actionPerformed(null);
                    }
                }
            }
        });
//        }
        //get next alarm in line and schedule it //UI: will only schedule one alarm at a time (not relevant for user?!)
    }

    /**
     * show the dialog when clicking on a received local notification. Options
     * normal Alarm: snooze, snoozeManuallySetTime (longPress?!, select either
     * hours/minutes, or new alarmDate), cancelNotification, startTimer,
     * editItem (to eg manually edit a new alarm). Options WaitingAlarm: snooze,
     * cancel, startTimer+cancelAlarm (assuming whatevery was waited for got
     * done), editItem.
     */
//<editor-fold defaultstate="collapsed" desc="comment">
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
    public void localNotificationReceived(String notificationId) {
//        Display.getInstance().callSerially(() -> {
//            AlarmHandler.alarmLog("Local notifiation received: \"" + notificationId + "\"");
//        });
//        Display.getInstance().callSerially(() -> {
        //DONE show as modal dialog to ensure user deals with the alarm (not Toastbar which may disappear)
//            showNotificationReceivedDialog(notificationId);
//            ScreenListOfAlarms.getInstance().show();

        processExpiredAlarm(notificationId, true);
//        });
    }

    /**
     * return list of already expired alarms (but not yet processed by the
     * end-user)
     *
     * @return
     */
    public List<ExpiredAlarm> getExpiredAlarms() {
        return expiredAlarms;
    }

    public ItemList getExpiredAlarmsItemList() {
        return new ItemList(getExpiredAlarms()); //need a copy of the list to avoid java.util.ConcurrentModificationException in CancellAll/SnoozeAll loops below

    }

    public LocalNotificationsShadowList getLocalNotificationsTEST() {
        return notificationList;
    }

    NotificationShadow getNextFutureAlarmN() {
//        return notificationList.getNextFutureAlarm();
        NotificationShadow notif;
        Long now = MyDate.currentTimeMillis();
        //remove expired alarms and add them to expiredAlarms
        if (false) { //now done in processExpiredAlarm()
            while ((notif = notificationList.getNextFutureAlarmN()) != null && notif.alarmTime.getTime() <= now) {
                notificationList.removeAlarmAndRepeatAlarm(notif.notificationId);
                expiredAlarms.add(0, new ExpiredAlarm(notif)); //UI: add most recent alarms to start
            }
//            notificationList.save();
            saveNotificationList();
            expiredAlarmSave();
        } else {
            notif = notificationList.getNextFutureAlarmN();
        }
//        return notificationList.getNextFutureAlarm();
        return notif;
    }

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

    public static void setPreferredBackgroundFetchInterval() {
//max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
//int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4);
        int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4);
        Display.getInstance().setPreferredBackgroundFetchInterval(fetchIntervalSeconds); //max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
    }

    public void receivePushUpdate() {
        switch ("") {
            case "alarmSnoozed": //snooze expiredAlarm (move expired alarm back to snoozed)
            case "alarmCancelled": //cancel expiredAlarm (cancel local alarm and remove from expiredAlarms)
            case "changedItem": //refresh alarms (update alarms as if item was edited locally)
            case "newItem": //update with alarms (ditto)
            case "itemDeletedCancelled": //cancel alarms (ditto)
        }
    }

     void playAlarm() {
        if (false && MyPrefs.alarmPlayBuiltinAlarmSound.getBoolean()) {
            Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ALARM); //work-around but sound doesn't seem to work on iOS, nor on simulator
        } else {
            // https://www.codenameone.com/manual/files-storage-networking.html#_file_system
            // CN1 doc: https://www.codenameone.com/javadoc/com/codename1/media/Media.html
            // https://www.codenameone.com/blog/open-file-rendering.html
            // https://www.codenameone.com/manual/components.html#sharebutton-section
            // https://stackoverflow.com/questions/48076190/codename-one-play-a-sound
            FileSystemStorage fs = FileSystemStorage.getInstance();
            String soundDir = fs.getAppHomePath(); // + "recordings/"; on simulator="file://home/" 
//        fs.mkdir(soundDir);
//        Media media = MediaManager.createBackgroundMedia("file://"+);
            try {
//            Media m = MediaManager.createMedia(soundDir + "/" + MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//            Media m = MediaManager.createMedia("file://" + "/" + MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//            Media m = MediaManager.createMedia( MyPrefs.getString(MyPrefs.alarmSoundFile), false);
//                Media m = MediaManager.createMedia(soundDir + MyPrefs.getString(MyPrefs.alarmSoundFile), false); //in SImulator: put mp3 file in .cn1!
                //https://stackoverflow.com/questions/48076190/codename-one-play-a-sound
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/notification_sound_bell.mp3")), "audio/mpeg");
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/"+MyPrefs.alarmSoundFile)), "audio/mpeg"); //doesn't work in static
//                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(), "/notification_sound_bell.mp3")), "audio/mpeg");
                Media m = MediaManager.createMedia((Display.getInstance().getResourceAsStream(getClass(),"/" + MyPrefs.getString(MyPrefs.alarmSoundFile))), "audio/mpeg"); //in SImulator: put mp3 file in .cn1!
        
                m.play();
            } catch (Exception err) {
                if (true) {
                    Log.e(err);
                }
//                Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_INFO);
                Display.getInstance().playBuiltinSound(Display.SOUND_TYPE_ALARM);
            }
        }
    }
}
