/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.CN;
import com.codename1.ui.Display;
import com.codename1.ui.util.UITimer;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * handles the alarms and snoozing while the app is running
 *
 * @author Thomas
 */
public class AlarmInAppAlarmHandler {

    private Timer inAppTimer = null;
//    private UITimer inAppUITimer = null; // new UITimer(()->{                            AlarmHandler.getInstance().processExpiredAlarm(notif.notificationId, false);                });

    /**
     * set to now whenever app is initialized, to only get app alarms for not
     * yet expired alarms. No need to persist it since it will always be
     * reinitialized when (re-)starting the app. Set to Is updated to always
     */
//    private Date lastTimeForInAppTimer = new Date(); //initiate with now so any alarms in the past will be ignored
//    AlarmLocalNotificationHandler localNotifHandler = null;
//    LocalNotificationsShadowList notificationList;
//    AlarmInAppAlarmHandler(AlarmLocalNotificationHandler localNotifHandler) {
//    AlarmInAppAlarmHandler(LocalNotificationsShadowList notificationList) {
    AlarmInAppAlarmHandler() {
//        this.localNotifHandler = localNotifHandler;
//        this.notificationList = notificationList;startInAppTimerOnNextcomingAlarm();
//        startInAppTimerOnNextcomingAlarm(); //CAUSES infinite loop
    }

    /**
     * set appTimer for next Item (if any). Must be called *after* the local
     * notification has been scheduled. starts the app timer (in-app timer used
     * while the app is active and running) and stores the appNotificationId.
     * Does nothing if alarmTime is null.
     *
     * @param appNotificationId
     * @param alarmTime
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void startInAppTimer(String appNotificationId, Date alarmTime) {
//    private void startInAppTimer(Date alarmTime) {
//    public void startInAppTimerOnNextcomingAlarmXXX() { //not possible to use UITimer since bound to a Form and alarms may expire in any form across the app
//        //cancel timer if already running
//        if (inAppUITimer != null) {
//            inAppUITimer.cancel();
//            inAppUITimer = null;
//        }
//
//        NotificationShadow notif = AlarmHandler.getInstance().getNextFutureAlarmN(); //get (but don't remove) next notif
//        if (notif != null) {
//            inAppUITimer = new UITimer(()->{
//                            inAppUITimer.cancel();
//                            AlarmHandler.getInstance().processExpiredAlarm(notif.notificationId, false);
//                });
//            inAppUITimer.schedule((int)(notif.alarmTime.getTime()-MyDate.currentTimeMillis()),true,CN.getCurrentForm()); //schedule for appearing at Date given by lastTimeForAppTimer
//        }
//    }
//</editor-fold>
    public void startInAppTimerOnNextcomingAlarm(NotificationShadow notif) {
        //cancel timer if already running
        if (inAppTimer != null) {
            inAppTimer.cancel();
            inAppTimer = null;
        }

        if (notif != null) {
            inAppTimer = new Timer();
            inAppTimer.schedule(new TimerTask() {
                @Override
                public void run() {
//                    Display.getInstance().callSerially(() -> {
//                    inAppTimer = null;
                    if (inAppTimer != null) {
                        inAppTimer.cancel();
                        inAppTimer = null;
                    }
                    // nothing should remove the first entry while inApptimer is running (except maybe a run condition where a new notif gets added just as the timer expires, but highly unlikely since the just added should basically expire 'now'?!)");
//<editor-fold defaultstate="collapsed" desc="comment">
//                    alarmData.removeSnoozeForItemWithSmallestTime(); //don't remove smallest until expired (since another snoozeTime may be launched meanwhile)
//                    List<NotificationShadow> list = notificationList.getNext();
//                    List<NotificationShadow> list = notificationList.removeAndReturnAllAlarmsForTime(firstAlarmTime);
//                    while (!list.isEmpty()) { //show dialog for every alarm expired at this time (one after the other)
//                        ASSERT.that(notif == AlarmHandler.getInstance().getNextFutureAlarm(), "notif=" + notif
//                                + "; notificationList.getNext()=" + AlarmHandler.getInstance().getNextFutureAlarm()
//                                + "  nothing should remove the first entry while inApptimer is running (except maybe a run condition where a new notif gets added just as the timer expires, but highly unlikely since the just added should basically expire 'now'?!)");
//                        NotificationShadow notif = AlarmHandler.getInstance().getNextFutureAlarm();
//                        ASSERT.that(notif.alarmTime.getTime() < System.currentTimeMillis(), "triggered alarm not in the past, notif=" + notif);
//                        do {
////                        NotificationShadow notif = list.remove(0);
////                        AlarmHandler.showNotificationReceivedDialog(appNotificationId);
////                        AlarmHandler.getInstance().showNotificationReceivedDialog(notif.notificationId);
////                        NotificationShadow notif = notificationList.getNext();
////                            notif = notificationList.getNextFutureAlarm();
//                            notif = AlarmHandler.getInstance().getNextFutureAlarm();
//                            ASSERT.that(notif != null);
//                            if (notif != null) { //defensive coding, shouldn't happen but just in case
////                                AlarmHandler.getInstance().showNotificationReceivedDialog(notif.notificationId);
//                                ScreenListOfAlarms.getInstance().show();
////                                notificationList.removeNext(); //remove just processed notif //NO, done in dialog!!
//                            }
//                        } while (notif != null
////                                && notificationList.getNextFutureAlarm() != null
//                                && AlarmHandler.getInstance().getNextFutureAlarm() != null
////                                && notif.alarmTime.getTime() == notificationList.getNextFutureAlarm().alarmTime.getTime()); //repeat show dialog for all notifs with same alarm time
//                                && notif.alarmTime.getTime() == AlarmHandler.getInstance().getNextFutureAlarm().alarmTime.getTime()); //repeat show dialog for all notifs with same alarm time
//                        ScreenListOfAlarms.getInstance().show();
//                        Display.getInstance().callSerially(() -> {
//</editor-fold>
                    AlarmHandler.getInstance().processExpiredAlarm(notif.notificationId, false); //callSerially done inside processExpiredAlarm()!
////<editor-fold defaultstate="collapsed" desc="comment">
//                        });
//                        if (notif != null) {
//                            new ScreenListOfAlarms().show();
//                        }
//                        //get next alarm in line and schedule it //UI: will only schedule one alarm at a time (not relevant for user?!)
////                    setInAppTimerForItemsWithAlarmOrSnoozed();
////                    AlarmLocalNotificationHandler.NotificationShadow nextNotif = notificationList.getFirstAlarmTimeOfAnyType();
////                    Date nextAlarmTime = notificationList.getFirstAlarmTimeOfAnyType();
////                    startInAppTimer(nextAlarmTime);
//                        startInAppTimerOnNextcomingAlarm(); //start timer on next alarm
//                    });
////</editor-fold>
                }
            }, notif.alarmTime); //schedule for appearing at Date given by lastTimeForAppTimer
        }
    }

    public void startInAppTimerOnNextcomingAlarm() {
        startInAppTimerOnNextcomingAlarm(AlarmHandler.getInstance().getNextFutureAlarmN()); //get (but don't remove) next notif);
    }

    /**
     * will set the inApp timer for the next item with alarmTime. If called
     * while the timer is already running, will cancel the running timer and
     * schedule for new Item. If called with item==null will cancel the timer.
     *
     * @param item
     * @param alarmTime
     */
//    public void launchInAppTimer(Item item, Date alarmTime) {
//    public void refreshInAppTimer() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (inAppTimer != null) {
////            inAppTimer.cancel();
////        }
////        Date nextAlarmTime = notificationList.getFirstAlarmTimeOfAnyType();
////</editor-fold>
//        startInAppTimerOnNextcomingAlarm();
//    }
}
