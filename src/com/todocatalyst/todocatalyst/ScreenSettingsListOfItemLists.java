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
public class ScreenSettingsListOfItemLists extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsListOfItemLists(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
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
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowNumberUndoneTasks);
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowNumberDoneTasks);
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowRemainingEstimate);
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowTotalTime);
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowWorkTime);
        addSettingBoolean(content, parseIdMap2, MyPrefs.listOfItemListsShowTotalNumberOfLeafTasks);

//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropLeftDropZoneWidth, 0, 30, 1);
//        addSettingInt(content, parseIdMap2, MyPrefs.dragDropRightDropZoneWidth, 0, 30, 1);
    }
}
