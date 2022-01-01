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

    private Timer inAppTimerInst = null;
    private Date inAppTimeTEST;
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
//    public void startInAppTimerOnNextcomingAlarm(NotificationShadow notif) {
    public void updateInAppTimerOnNextcomingAlarm(List<NotificationShadow> localNotifsActiveSorted) {
//            NotificationShadow notif = AlarmHandler.getInstance().getNextFutureAlarmN(); //get (but don't remove) next notif);
        //cancel timer if already running (even if localNotifsActiveSorted is empty9
        if (inAppTimerInst != null) {
            inAppTimerInst.cancel();
            inAppTimerInst = null;
        }
        if (localNotifsActiveSorted != null && localNotifsActiveSorted.size() > 0) {
            NotificationShadow notif = localNotifsActiveSorted.get(0); //get (but don't remove) next notif);
            inAppTimerInst = new Timer();
            inAppTimerInst.schedule(new TimerTask() {
                @Override
                public void run() {
//                    Display.getInstance().callSerially(() -> {
//                    inAppTimer = null;
                    if (inAppTimerInst != null) {
                        inAppTimerInst.cancel();
                        inAppTimerInst = null;
                    }
//                    Display.getInstance().callSerially(()
//                            -> // nothing should remove the first entry while inApptimer is running (except maybe a run condition where a new notif gets added just as the timer expires, but highly unlikely since the just added should basically expire 'now'?!)");
//                            AlarmHandler.getInstance().processExpiredAlarm(notif.notificationId, false)); //callSerially done inside processExpiredAlarm()!
                    AlarmHandler.getInstance().processExpiredAlarm(notif.notificationId, false); //callSerially done inside processExpiredAlarm()!
                }
            }, notif.alarmTime); //schedule for appearing at Date given by lastTimeForAppTimer
            inAppTimeTEST = notif.alarmTime;
        }
    }

//    public void startInAppTimerOnNextcomingAlarmXXX() {
//        startInAppTimerOnNextcomingAlarm(AlarmHandler.getInstance().getNextFutureAlarmN()); //get (but don't remove) next notif);
//    }
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
