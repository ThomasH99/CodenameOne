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

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container cont) {
//        cont.setScrollableY(true);
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice).addActionListener((e) -> {
//            AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(); //enable/disable all alarms
//        });
        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice,
                () -> AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(), //enable/disable all alarms
                () -> AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled() //enable/disable all alarms
        );
        addSettingInt(cont, parseIdMap2, MyPrefs.alarmDefaultSnoozeTimeInMinutes, 0, 120, 1);
        addSettingInt(cont, parseIdMap2, MyPrefs.alarmIntervalBetweenAlarmsRepeatsMillisInMinutes, 0, 120, 1);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmShowDueTimeAtEndOfNotificationText);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmReuseIndividuallySetSnoozeDurationForLongPress);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmReuseIndividuallySetSnoozeDurationForNormalSnooze);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.alarmPlayBuiltinAlarmSound);

        if (false) {
            //Examples:
            addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowEffortEstimateDetails);
            addSettingInt(cont, parseIdMap2, MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds, 0, 30, 1);
            addSettingTimeInMinutes(cont, parseIdMap2, MyPrefs.timerBuzzerInterval); //disable until buzzer can run in background
        }

    }
}
