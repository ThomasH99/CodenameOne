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
public class ScreenSettingsTimer extends ScreenSettingsCommon {

    ScreenSettingsTimer(MyForm mainScreen, UpdateField doneAction) { // throws ParseException, IOException {
//        super(mainScreen.SCREEN_TITLE + " settings", mainScreen, doneAction);
          super(mainScreen, doneAction);
  }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container cont) {
//        cont.setScrollableY(true);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerAutomaticallyStartTimer);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowNextTask);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerAutomaticallyGotoNextTask);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowEffortEstimateDetails);
        addSettingInt(cont, parseIdMap2, MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds, 0, 30, 1);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowSecondsInTimer);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerShowTotalActualInTimer);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerKeepScreenAlwaysOnInTimer);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting);
        if (false) {
            addSettingTimeInMinutes(cont, parseIdMap2, MyPrefs.timerBuzzerInterval); //disable until buzzer can run in background
        }
        addSettingInt(cont, parseIdMap2, MyPrefs.timerUpdateInterval, 1, 60, 1);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerIncludeWaitingTasks);
        addSettingBoolean(cont, parseIdMap2, MyPrefs.timerAlwaysExpandListHierarchy);
        addSettingInt(cont, parseIdMap2, MyPrefs.timerMaxTimerDurationInHours, 0, 12, 1);
    }
}
