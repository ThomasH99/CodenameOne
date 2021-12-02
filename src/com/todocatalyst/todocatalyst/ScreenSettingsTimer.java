package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
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
public class ScreenSettingsTimer extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsTimer(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
//        super(mainScreen.SCREEN_TITLE + " settings", mainScreen, doneAction);
        super(mainScreen, doneAction);
        setUniqueFormId("ScreenTimerSettings");
    }
    
        public static String SETTINGS_MENU_TEXT = Format.f(MyForm.SETTINGS_MENU_TEXT_BASE,MyForm.SCREEN_TIMER_TITLE);


    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
//        cont.setScrollableY(true);
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAutomaticallyStartTimer));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowNextTask));
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowNextTaskWithRemainingTime);
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowRemainingForNextTask));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAutomaticallyGotoNextTask));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem));
        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowEffortEstimateDetails));
        }
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds, 0, 30, 1));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowSecondsInTimer));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowTotalActualInTimer));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerKeepScreenAlwaysOnInTimer));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerIncludeWaitingTasks));
        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerIncludeDoneTasks));
        }
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerSwipeStartOnlyTimesSelectedTask));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerCanBeSwipeStartedEvenOnInvalidItem));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerEnableRestartOnLists));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject));
        if (false) {
//            Component setting0 = SettingTimeInMinutes(cont, parseIdMap2, MyPrefs.timerBuzzerInterval); //disable until buzzer can run in background
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerBuzzerActive)); //disable until buzzer can run in background
            content.add(makeEditTimeInMinutesSetting(parseIdMap2, MyPrefs.timerBuzzerInterval)); //disable until buzzer can run in background
        }
        if (Config.TEST) { //too complicated, enough to chose to see seconds or not
            content.add(makeEditIntSetting(parseIdMap2, MyPrefs.timerUpdateInterval, 1, 60, 1));
        }
        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAlwaysExpandListHierarchy));
//            addSettingInt(cont, parseIdMap2, MyPrefs.timerMaxTimerDurationInHoursXXX, 0, 12, 1);
        }
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow));
        content.add(makeSpacerThin());
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerMayPauseAlreadyRunningTimer,()->);
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerAskBeforeStartingOnNewElement);
        Component setting1 = makeEditBooleanSetting(parseIdMap2, MyPrefs.timerAskBeforeStartingOnNewElement);
//        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerMayPauseAlreadyRunningTimer, () -> setting1.setHidden(false), () -> setting1.setHidden(true));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.timerMayPauseAlreadyRunningTimer, setting1));
        content.add(setting1);
    }
}
