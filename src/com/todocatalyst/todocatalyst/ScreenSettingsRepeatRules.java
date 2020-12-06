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
public class ScreenSettingsRepeatRules extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsRepeatRules(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
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
        addSettingBoolean(content, parseIdMap2, MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator);
        addSettingBoolean(content, parseIdMap2, MyPrefs.repeatReuseAlreadyGeneratedInstancesWhenUpdatingARepeatRule);
        addSettingBoolean(content, parseIdMap2, MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances);
        if (false) {
            addSettingInt(content, parseIdMap2, MyPrefs.maxNumberRepeatInstancesToDeleteWithoutConfirmation, 1, 1000, 1);
            addSettingBoolean(content, parseIdMap2, MyPrefs.repeatHidePreviousTasksDetails);
            addSettingBoolean(content, parseIdMap2, MyPrefs.insertNewRepeatInstancesInStartOfLists);
//            addSettingInt(content, parseIdMap2, MyPrefs.repeatMaxNumberOfRepeatsToGenerate, 0, 52, 1);
            addSettingInt(content, parseIdMap2, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead, 0, 30, 1);
            addSettingInt(content, parseIdMap2, MyPrefs.repeatMaxInterval, 0, 731, 1);
            addSettingInt(content, parseIdMap2, MyPrefs.repeatMaxNumberFutureDaysToGenerateAheadZZZ, 0, 62, 1);
        }
    }
}
