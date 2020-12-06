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
public class ScreenSettingsItemListProperties extends ScreenSettingsCommon {

//     protected static String FORM_UNIQUE_ID = "ScreenTimerSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    ScreenSettingsItemListProperties(MyForm mainScreen, Runnable doneAction) { // throws ParseException, IOException {
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
        addSettingBoolean(content, parseIdMap2, MyPrefs.itemListAllowDuplicateListNames);
        addSettingBoolean(content, parseIdMap2, MyPrefs.insertNewItemsInStartOfLists);

//        addSettingInt(content, parseIdMap2, MyPrefs.workSlotDefaultDurationInMinutes, 0, 60*24, 5); //UI: workslots limited to 24h duration
//        addSettingInt(content, parseIdMap2, MyPrefs.workSlotDurationStepIntervalInMinutes, 0, 30, 1);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.workSlotsMayBeCreatedInThePast);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.workSlotDefaultStartDateIsNow);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.workSlotContinueAddingInlineWorkslots);
    }
}
