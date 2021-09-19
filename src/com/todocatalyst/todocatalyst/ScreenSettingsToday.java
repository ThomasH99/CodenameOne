package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
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
public class ScreenSettingsToday extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsToday(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
//        super(mainScreen.SCREEN_TITLE + " settings", mainScreen, doneAction);
        super(mainScreen, doneAction);
        setUniqueFormId("ScreenItemSettings");
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeStartingToday));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeWaitingExpiringToday));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeAlarmsExpiringToday));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeWorkSlotsCoveringToday));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewShowLeafTasksInsteadOfProjects));
//        addSettingInt(content, parseIdMap2, MyPrefs.todayViewIncludeOverdueFromThisManyPastDays, 0, 60, 1); //UI: max 60 days of overdue
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.todayViewIncludeOverdueFromThisManyPastDays, 0, 60, 1)); //UI: max 60 days of overdue

//        addSettingInt(content, parseIdMap2, MyPrefs.workSlotDefaultDurationInMinutes, 0, 60*24, 5); //UI: workslots limited to 24h duration
    }
}
