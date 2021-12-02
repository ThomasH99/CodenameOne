package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionListener;

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
        super("Task lists", mainScreen, doneAction);
        setUniqueFormId("ScreenListOfItemsSettings");
    }

    public static String SETTINGS_MENU_TEXT = Format.f(MyForm.SETTINGS_MENU_TEXT_BASE, MyForm.SCREEN_TASK_LIST_TITLE);

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {
        ScreenType screenType = parentForm.getScreenType();
//       if (screenType)
        switch (screenType) {
            case TODAY: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                Runnable update = ()->DAO.getInstance().reloadToday();
                Runnable update = () -> DAO.getInstance().reloadSystemList(screenType.getSystemName());
                content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeStartingToday, update));
                content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeWaitingExpiringToday, update));
                content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeAlarmsExpiringToday, update));
                content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewIncludeWorkSlotsCoveringToday, update));
                content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.todayViewShowLeafTasksInsteadOfProjects, update));
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.todayViewIncludeOverdueFromThisManyPastDays, 0, 60, 1, (e) -> update.run())); //UI: max 60 days of overdue
                content.add(makeSpacer());
                break;
            }
            case OVERDUE: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.overdueLogInterval, 1, 365, 1,(e) -> DAO.getInstance().reloadOverdue()));
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.overdueLogInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadSystemList(screenType.getSystemName())));
                content.add(makeSpacer());
                break;
            }
            case CREATION_LOG: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.creationLogInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadCreationLog()));
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.creationLogInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadSystemList(screenType.getSystemName())));
                content.add(makeSpacer());
                break;
            }
            case COMPLETION_LOG: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.completionLogInterval, 1, 365, 1,(e) -> DAO.getInstance().reloadCompletionLog()));
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.completionLogInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadSystemList(screenType.getSystemName())));
                content.add(makeSpacer());
                break;
            }
            case TOUCHED: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.touchedLogInterval, 1, 365, 1,(e) -> DAO.getInstance().reloadTouched()));
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.touchedLogInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadSystemList(screenType.getSystemName())));
                content.add(makeSpacer());
                break;
            }
            case NEXT: {
                content.add(makeSectionTitle(Format.f("Settings for {0}:", screenType.getTitle())));
//                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.nextInterval, 1, 365, 1,(e) -> DAO.getInstance().reloadNext())); //UI: max 60 days of overdue
                content.add(makeEditIntSetting(parseIdMap2, MyPrefs.nextInterval, 1, 365, 1, (e) -> DAO.getInstance().reloadSystemList(screenType.getSystemName()))); //UI: max 60 days of overdue
                content.add(makeSpacer());
                break;
            }
            case NOT_INIT:
            default:
        }
        if (Config.TEST) {
            content.add(makeEditIntSetting(parseIdMap2, MyPrefs.pinchAdjustUpper, 0, 10, 1));
            content.add(makeEditIntSetting(parseIdMap2, MyPrefs.pinchAdjustLower, 0, 10, 1));
        }
        content.add(makeSectionTitle("Settings shared for all task lists:"));

        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.showDetailsForAllTasks));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.scrollToolbarOffScreenInTaskLists));
//        cont.setScrollableY(true);

        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowActualEvenIfZero));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowActualIfNonZeroEvenIfNotDone));

        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowRemainingEvenIfZero));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListHideRemainingWhenDefaultValue));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowEffortEstimateEvenIfZero));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowEarnedValuePerHourEvenIfZero));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowEarnedValueIfEarnedValuePerHourIsZero));
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.earnedValueDecimals, 0, 2, 1));

        content.add(makeSpacer());
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListAlwaysShowHideUntilDate));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListAlwaysShowStartByDate));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListExpiresByDate));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListWaitingTillDate));

        content.add(makeSpacer());
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.dragDropAsSubtaskEnabled));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.dragDropAsSupertaskEnabled));
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask, 5, 25, 1));
        content.add(makeSpacer());
        content.add(makeEditIntSetting(parseIdMap2, MyPrefs.useSmartdatesForThisManyDaysOverdueDueOrFinishDates, 0, 60, 1));

        content.add(makeSpacer());
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.hideStickyHeadersForSortedLists));
        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.scrollToolbarOffScreenOnScrollingUp));
        }
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.titleAutoSize));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemListShowIconForDueDate));
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropLeftDropZoneWidth, 0, 30, 1);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropRightDropZoneWidth, 0, 30, 1);
    }
}
