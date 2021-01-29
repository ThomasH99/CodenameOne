package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Container;
import com.codename1.ui.Label;

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
public class ScreenSettingsListOfItems extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsListOfItems(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
//        super(mainScreen.SCREEN_TITLE + " settings", mainScreen, doneAction);
        super("task lists", mainScreen, doneAction);
        setUniqueFormId("ScreenListOfItemsSettings");
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
        ScreenType screenType = parentForm.getScreenType();
//       if (screenType)
        switch (screenType) {
            case TODAY:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingBoolean(content, parseIdMap2, MyPrefs.todayViewIncludeStartingToday);
                addSettingBoolean(content, parseIdMap2, MyPrefs.todayViewIncludeWaitingExpiringToday);
                addSettingBoolean(content, parseIdMap2, MyPrefs.todayViewIncludeAlarmsExpiringToday);
                addSettingBoolean(content, parseIdMap2, MyPrefs.todayViewIncludeWorkSlotsCoveringToday);
                addSettingBoolean(content, parseIdMap2, MyPrefs.todayViewShowLeafTasksInsteadOfProjects);
                addSettingInt(content, parseIdMap2, MyPrefs.todayViewIncludeOverdueFromThisManyPastDays, 0, 60, 1); //UI: max 60 days of overdue
                content.add(makeSpacer());
                break;
            case OVERDUE:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingInt(content, parseIdMap2, MyPrefs.overdueLogInterval, 1, 365, 1);
                content.add(makeSpacer());
                break;
            case CREATION_LOG:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingInt(content, parseIdMap2, MyPrefs.creationLogInterval, 1, 365, 1);
                content.add(makeSpacer());
                break;
            case COMPLETION_LOG:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingInt(content, parseIdMap2, MyPrefs.completionLogInterval, 1, 365, 1);
                content.add(makeSpacer());
                break;
            case TOUCHED:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingInt(content, parseIdMap2, MyPrefs.touchedLogInterval, 1, 365, 1);
                content.add(makeSpacer());
                break;
            case NEXT:
                addSettingTitle(content, Format.f("Setings for {0}:", screenType.getTitle()));
                addSettingInt(content, parseIdMap2, MyPrefs.nextInterval, 1, 365, 1); //UI: max 60 days of overdue
                content.add(makeSpacer());
                break;
            case NOT_INIT:
        }
        if (Config.TEST) {
            addSettingInt(content, parseIdMap2, MyPrefs.pinchAdjustUpper, 0, 10, 1);
            addSettingInt(content, parseIdMap2, MyPrefs.pinchAdjustLower, 0, 10, 1);
        }
        addSettingTitle(content, "Settings shared for all task lists:");

        addSettingBoolean(content, parseIdMap2, MyPrefs.showDetailsForAllTasks);
//        cont.setScrollableY(true);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowActualIfNonZeroEvenIfNotDone);
        if (Config.TEST) {
            addSettingExplanation(content, "Test text to show a fairly long explanation for an individual setting");
        }
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowRemainingEvenIfZero);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListHideRemainingWhenDefaultValue);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowEffortEstimateEvenIfZero);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowEarnedValuePerHourEvenIfZero);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowEarnedValueIfEarnedValuePerHourIsZero);
        addSettingInt(content, parseIdMap2, MyPrefs.earnedValueDecimals, 0, 2, 1);

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAlwaysShowHideUntilDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAlwaysShowStartByDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListExpiresByDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListWaitingTillDate);

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.dragDropAsSubtaskEnabled);
        addSettingBoolean(content, parseIdMap2, MyPrefs.dragDropAsSupertaskEnabled);
        addSettingInt(content, parseIdMap2, MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask, 5, 25, 1);
        content.add(makeSpacer());
        addSettingInt(content, parseIdMap2, MyPrefs.useSmartdatesForThisManyDaysOverdueDueOrFinishDates, 0, 60, 1);

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.hideStickyHeadersForSortedLists);
        addSettingBoolean(content, parseIdMap2, MyPrefs.scrollToolbarOffScreenOnScrollingUp);
        addSettingBoolean(content, parseIdMap2, MyPrefs.titleAutoSize);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropLeftDropZoneWidth, 0, 30, 1);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropRightDropZoneWidth, 0, 30, 1);
    }
}
