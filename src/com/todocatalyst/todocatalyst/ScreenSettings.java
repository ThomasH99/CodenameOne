package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Label;
import com.parse4cn1.ParseException;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseUser;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
//public class ScreenSettings extends MyForm {
public class ScreenSettings extends ScreenSettingsCommon {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Settings";
    public final static String SCREEN_HELP = "Settings";
// protected static String FORM_UNIQUE_ID = "ScreenSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

//    MyForm mainScreen;
    ScreenSettings(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, mainScreen, () -> {
        });
        setUniqueFormId("ScreenSettings");
    }
    
    protected static Component layoutN(String fieldLabelTxt, Button editSetting, String help) { //normal edit field with [>]e
        editSetting.setUIID("SettingMenu");
        Button editFieldButton = new Button("",Icons.iconEdit,"IconEdit");
//        Button editFieldButton = new Button("","IconEdit");
        editFieldButton.setMaterialIcon(Icons.iconEdit);
        Container cont = BorderLayout.centerEastWest(null, editFieldButton, editSetting);
        cont.setUIID("SettingMenuField");
        cont.setLeadComponent(editSetting);
        return cont;
    }
    

    /**
     * This method shows the main user interface of the app
     *
     * @param content
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    @Override
    protected void buildContentPane(Container content) {

        content.add(layoutN("", new Button(MyReplayCommand.createMaterial("Global Settings", Icons.iconSettings, (e) -> {
            new ScreenGlobalSettings(this).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Account settings", Icons.iconEditAccount, (e) -> {
            new ScreenAccount(this).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Today view settings", Icons.iconMainToday, (e) -> {
            new ScreenSettingsToday(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Tasks", Icons.iconMainAllTasksCust, Icons.myIconFont, (e) -> {
            new ScreenSettingsItem(null, this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Task list settings", Icons.iconMainListsCust, Icons.myIconFont, (e) -> {
            new ScreenSettingsListOfItems(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("List settings", Icons.iconMainListsCust, Icons.myIconFont, (e) -> {
            new ScreenSettingsListOfItemLists(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Category settings", Icons.iconMainCategories, (e) -> {
            new ScreenSettingsListOfCategories(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Reminder settings", Icons.iconMainAlarms, (e) -> {
            new ScreenSettingsAlarms(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Timer settings", Icons.iconTimerLaunch, (e) -> {
            new ScreenSettingsTimer(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Template settings", Icons.iconMainTemplates, (e) -> {
            new ScreenSettingsTemplates(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Repeat settings", Icons.iconRepeat, (e) -> {
            new ScreenSettingsRepeatRules(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create(WorkSlot.WORKSLOT+" settings", Icons.iconMainWorkSlots, (e) -> {
            new ScreenSettingsWorkSlot(this, null).show();
        })), "**"));

        content.add(layoutN("", new Button(MyReplayCommand.create("Help", Icons.iconHelp, (e) -> {
            new ScreenGettingStarted(this, true).show();
        })), "**"));

    }
}
