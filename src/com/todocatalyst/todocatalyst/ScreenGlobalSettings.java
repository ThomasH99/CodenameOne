package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
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
public class ScreenGlobalSettings extends ScreenSettingsCommon {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Global Settings";
    public final static String SCREEN_HELP = "Global Settings";
// protected static String FORM_UNIQUE_ID = "ScreenSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

//    MyForm mainScreen;
    ScreenGlobalSettings(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, mainScreen, () -> {
        });
        setUniqueFormId("ScreenSettings");
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {

        if (false) {
            content.add(new Button(Command.create("Reload theme**", null, (ev) -> {
                Resources theme;
                if (MyPrefs.themeNameWithoutBackslash.getString().length() > 0) {
                    theme = UIManager.initFirstTheme("/" + MyPrefs.getString(MyPrefs.themeNameWithoutBackslash));
                }
            })));
        }

//        if (MyPrefs.loginIncognitoMode.getBoolean()) {
//            content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));
//            ;
//        }
        if (!Config.PRODUCTION_RELEASE) { //keep visible even if TEST is set to false
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.productionView, () -> Config.TEST = !MyPrefs.productionView.getBoolean()));
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.fingerTracking));
        }
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.keepScreenAlwaysOnInApp));

        if (Display.getInstance().canForceOrientation()) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.screenEnableDisplayRotationAwayFromPortrait,
                    //                    () -> Display.getInstance().lockOrientation(false));
                    () -> Display.getInstance().unlockOrientation(),
                    () -> Display.getInstance().lockOrientation(true)));
        }
        if (false) {
            //Safe area on/off
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.enableSafeArea,
                    () -> {
                        parentForm.setSafeArea(true);
                        parentForm.setSafeAreaChanged();
                    },
                    () -> {
                        parentForm.setSafeArea(false);
                        parentForm.setSafeAreaChanged();
                    }));
        }

        if (true) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.hideStatusBar, () -> showStatusBar(false), () -> showStatusBar(true)));
        }

        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.encryptTaskTextAndComments));
        }
        if (false) {
            content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.deleteLocalStorageIfRestartedQuickly));
        }
        if (Config.TEST) {
            content.add(makeEditIntSetting(parseIdMap2, MyPrefs.deleteLocalStorageIfRestartedBeforeSeconds, 5, 120, 5)); //kin, max, step
        }
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.itemRemoveTrailingPrecedingSpacesAndNewlines));

        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.showSourceItemInEditScreens));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.pinchInsertEnabled));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.pinchInsertActivateEditing));
        content.add(makeSpacer());
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.enableShowingSystemInfo));
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.showObjectIdsInEditScreens));
        content.add(makeSpacer());
        content.add(makeEditBooleanSetting(parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice, () -> AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled()));
//        addSettingInt(content, parseIdMap2, MyPrefs.alarmDefaultSnoozeTimeInMinutes, 0, 120, 1);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.alarmShowDueTimeAtEndOfNotificationText);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddToBeginningOfComment);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddTimedEntriesWithDateANDTime);

//<editor-fold defaultstate="collapsed" desc="comment">
//        content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));
//
//
//        if (!MyPrefs.loginIncognitoMode.getBoolean()) { //DON'T enable logging out in Incognito mode since that would lose the login
//            content.add(layout("Log out from my account", logoutButton, "After you logout, you must enter your email and password to access your data again.", true));
//
//            Button remindPsWdButton = new Button(new Command("Reset password") {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    try {
//                        ParseUser.requestPasswordReset(ParseUser.getCurrent().getEmail()); //TODO read password - copy Amazon usability
////                    ParseUser.getCurrent().logout(); //needed to explicitly log out?
//                    } catch (ParseException ex) {
//                        Log.e(ex);
//                    }
//                }
//            });
////        content.add(remindPsWdButton).add(new SpanLabel("You will receive an email with a link to reset your password. NB. This will log you out until you have entered a new password."));
//            content.add(layout("Reset my password", remindPsWdButton, "You will receive an email with a link to reset your password. NB. This will log you out until you have entered a new password."));
//        }
//
//try {
//    Resources theme = Resources.openLayered("/theme");
//    UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
//} catch(IOException e){
//    e.printStackTrace();
//}
//                Resources resource = xx;
//                String[] themeNames = resource.getResourceNames();
//                UIManager.initNamedTheme(SCREEN_MAIN_NAME, SCREEN_MAIN_NAME)
//                String log = Log.getLogContent();
//                if (Dialog.show("Send log", log, "OK", "Cancel")) {
//                    Log.sendLog();
//                }
//            }
//        });
//</editor-fold>
        if (false) { //NO support for changing themes for now
            if (false) {
                content.add(new SpanLabel(MyPrefs.themeNameWithoutBackslash.helpText));
            }
//        addSettingBoolean(content, parseIdMap2, new SpanLabel(MyPrefs.themeNameWithoutBackslash.helpText));
            Resources res = null;
            try {
                res = Resources.openLayered("/theme");
            } catch (IOException ex) {
                Log.e(ex);
            }
//    ; res.getResourceNames();
            String[] files = res.getResourceNames();
            ArrayList resFiles = new ArrayList();
            for (String s : files) {
                if (s.endsWith(".res")) {
                    resFiles.add(s);
                }
            }

            MyStringPicker resFilePicker = new MyStringPicker((String[]) resFiles.toArray(new String[]{}), MyPrefs.getString(MyPrefs.themeNameWithoutBackslash));
            resFilePicker.addActionListener((e) -> {
                String newTheme = resFilePicker.getSelectedString();
                if (newTheme != null && !newTheme.equals("") && !newTheme.equals(MyPrefs.getString(MyPrefs.themeNameWithoutBackslash))) {
                    MyPrefs.setString(MyPrefs.themeNameWithoutBackslash, newTheme);
                    Resources theme = null;
                    try {
                        theme = Resources.openLayered("/" + newTheme);
                    } catch (IOException ex) {
                        Log.e(ex);
                    }
                    if (theme != null) {
//                if (theme.getThemeResourceNames().length>1)
                        Log.p("Themes in theme file \"" + newTheme + "\": " + theme.getThemeResourceNames());
                        UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
                    }
                }
            });
        }
//        return content;
    }
}
