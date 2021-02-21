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
public class ScreenSettingsItem extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsItem(String title, MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
//        super(mainScreen.SCREEN_TITLE + " settings", mainScreen, doneAction);
        super(mainScreen, doneAction);
        setUniqueFormId("ScreenItemSettings");
        setTitle(title);
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
//        cont.setScrollableY(true);
//        addSettingInt(content, parseIdMap2, MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes, 0, 60, 1);
        addSettingBoolean(content, parseIdMap2, MyPrefs.useEstimateDefaultValueForZeroEstimatesInMinutes); //TODO!!!! below setting to depend on this one
        addSettingInt(content, parseIdMap2, MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes, 0, 120, 1);

        addSettingBoolean(content, parseIdMap2, MyPrefs.itemEditEnableSwipeBetweenTabs);
        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddTimedEntriesWithDateANDTime);
        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddToBeginningOfComment);
        addSettingBoolean(content, parseIdMap2, MyPrefs.hideIconsInEditTaskScreen);
        addSettingInt(content, parseIdMap2, MyPrefs.itemDueDateDefaultDaysAheadInTime, 0, 31, 1);
        addSettingTimeInMinutes(content, parseIdMap2, MyPrefs.itemDefaultAlarmTimeBeforeDueDateInMinutes);

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.askToEnterActualIfMarkingTaskDoneOutsideTimer);
        addSettingBoolean(content, parseIdMap2, MyPrefs.askToEnterActualIfMarkingTaskWaitingOutsideTimer);
        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.showTemplateListAfterCreatingNewTemplateFromExistingProject);

        addSettingInt(content, parseIdMap2, MyPrefs.itemMaxNbSubTasksToChangeStatusForWithoutConfirmation, 0, 10, 1);
        addSettingBoolean(content, parseIdMap2, MyPrefs.checkBoxShowStatusMenuOnSingleClickInsteadOfLongPress);

        content.add(makeSpacer());
        Component inheritChallenge = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectChallenge);
        Component inheritStarred = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectStarred);
        Component inheritPriority = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectPriority);
        Component inheritDreadFun = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectDreadFun);
        Component inheritImportance = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectImportance);
        Component inheritUrgency = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectUrgency);
        Component inheritDueDate = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectDueDate);
        Component inheritStartBy = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectStartByDate);
        Component inheritWaitingTill = settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectWaitUntilDate);
        Component inheritDoneSubtasksInherit = settingBoolean(parseIdMap2, MyPrefs.itemInheritEvenDoneSubtasksInheritOwnerValues);

        content.add(settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectProperties, true,
                inheritStarred, inheritPriority, inheritImportance, inheritUrgency, inheritChallenge, inheritDreadFun, 
                inheritDueDate, inheritStartBy, inheritWaitingTill, inheritDoneSubtasksInherit        ));
        content.addAll(
                inheritStarred,
                inheritPriority,
                inheritImportance,
                inheritUrgency,
                inheritChallenge,
                inheritDreadFun,
                inheritDueDate,
                inheritStartBy,
                inheritWaitingTill,
                inheritDoneSubtasksInherit);
        //TODO!!! only show below settings when MyPrefs.itemInheritOwnerProjectProperties is true or just set true
//        addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectChallenge);
        if (false) {
            content.add(settingBoolean(parseIdMap2, MyPrefs.itemInheritOwnerProjectChallenge));
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectStarred);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectPriority);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectDreadFun);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectImportance);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectUrgency);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectDueDate);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectStartByDate);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritOwnerProjectWaitUntilDate);
            addSettingBoolean(content, parseIdMap2, MyPrefs.itemInheritEvenDoneSubtasksInheritOwnerValues);
        }

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.testPickersOnDevice);

    }
}
