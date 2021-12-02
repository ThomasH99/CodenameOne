package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.Switch;
import com.codename1.ui.Component;
import com.codename1.ui.Container;

//import com.codename1.ui.*;
//import com.codename1.ui.events.ActionEvent;
//import com.codename1.ui.layouts.BoxLayout;
//import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.Resources;
//import com.parse4cn1.ParseException;
//import java.io.IOException;
//import java.util.Map;
/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenSettingsAlarms extends ScreenSettingsCommon {

    ScreenSettingsAlarms(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
        super(mainScreen, doneAction);
    }
    
    public static String SETTINGS_MENU_TEXT = Format.f(MyForm.SETTINGS_MENU_TEXT_BASE,MyForm.SCREEN_ALARM_TITLE);

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
//        cont.setScrollableY(true);
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice).addActionListener((e) -> {
//            AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(); //enable/disable all alarms
//        });
        content.add(makeEditBooleanSetting(  parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice,
                () -> AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(), //enable/disable all alarms
                () -> AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled() //enable/disable all alarms
        ));
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.alarmDefaultSnoozeTimeInMinutes, 0, 120, 1));
        content.add(makeEditIntSetting( parseIdMap2, MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes, 0, 120, 1));
        content.add(makeEditBooleanSetting( parseIdMap2, MyPrefs.alarmShowDueTimeAtEndOfNotificationText));
        content.add(makeEditBooleanSetting( parseIdMap2, MyPrefs.alarmReuseIndividuallySetSnoozeDurationForLongPress));
        content.add(makeEditBooleanSetting( parseIdMap2, MyPrefs.alarmReuseIndividuallySetSnoozeDurationForNormalSnooze));
        content.add(makeEditBooleanSetting( parseIdMap2, MyPrefs.alarmPlayBuiltinAlarmSound));

        if (false) {
            //Examples:
            content.add(makeEditBooleanSetting( parseIdMap2, MyPrefs.timerShowEffortEstimateDetails));
            content.add(makeEditIntSetting( parseIdMap2, MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds, 0, 30, 1));
         }

    }
}
