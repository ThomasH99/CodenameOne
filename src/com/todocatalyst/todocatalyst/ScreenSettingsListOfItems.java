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
//        cont.setScrollableY(true);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowActualIfNonZeroEvenIfNotDone);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAllowDuplicateListNames);
        addSettingBoolean(content, parseIdMap2, MyPrefs.insertNewItemsInStartOfLists);
        
        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAlwaysShowHideUntilDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAlwaysShowStartByDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListExpiresByDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListWaitingTillDate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListShowRemainingEvenIfZero);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListEffortEstimate);

        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.dragDropAsSubtaskEnabled);
        addSettingBoolean(content, parseIdMap2, MyPrefs.dragDropAsSupertaskEnabled);
        addSettingInt(content, parseIdMap2, MyPrefs.useSmartdatesForThisManyDaysOverdueDueOrFinishDates, 0, 60, 1);
        addSettingInt(content, parseIdMap2, MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask, 5, 25, 1);
        addSettingInt(content, parseIdMap2, MyPrefs.earnedValueDecimals, 0, 2, 1);
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListDontShowValueIfEarnedValuePerHourIsNonZero);
        
        content.add(makeSpacer());
        addSettingBoolean(content, parseIdMap2, MyPrefs.hideStickyHeadersForSortedLists);
        addSettingBoolean(content, parseIdMap2, MyPrefs.scrollToolbarOffScreenOnScrollingDown);
        addSettingBoolean(content, parseIdMap2, MyPrefs.titleAutoSize);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropLeftDropZoneWidth, 0, 30, 1);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropRightDropZoneWidth, 0, 30, 1);
    }
}
