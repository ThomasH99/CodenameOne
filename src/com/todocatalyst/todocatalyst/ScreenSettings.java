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
public class ScreenSettings extends ScreenSettingsCommon {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Global Settings";
// protected static String FORM_UNIQUE_ID = "ScreenSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

//    MyForm mainScreen;
    ScreenSettings(MyForm mainScreen) { // throws ParseException, IOException {
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

        content.add(new Button(Command.create("Reload theme**", null, (ev) -> {
            Resources theme;
            if (MyPrefs.themeNameWithoutBackslash.getString().length() > 0) {
                theme = UIManager.initFirstTheme("/" + MyPrefs.getString(MyPrefs.themeNameWithoutBackslash));
            }
        })));

//        if (MyPrefs.loginIncognitoMode.getBoolean()) {
//            content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));
//            ;
//        }
        addSettingBoolean(content, parseIdMap2, MyPrefs.keepScreenAlwaysOnInApp);
        if (Display.getInstance().canForceOrientation()) {
            addSettingBoolean(content, parseIdMap2, MyPrefs.screenRotationDisabled, () -> Display.getInstance().lockOrientation(true), () -> Display.getInstance().lockOrientation(false));
        }
        addSettingInt(content, parseIdMap2, MyPrefs.overdueLogInterval, 0, 365, 1);
        addSettingBoolean(content, parseIdMap2, MyPrefs.enableShowingSystemInfo);
        addSettingBoolean(content, parseIdMap2, MyPrefs.showSourceItemInEditScreens);
        addSettingBoolean(content, parseIdMap2, MyPrefs.showObjectIdsInEditScreens);
        addSettingBoolean(content, parseIdMap2, MyPrefs.pinchInsertEnabled);
        content.add(makeSpacer());
        if (Config.TEST_STORE_PASSWORD_FOR_USER) {
            try {
                ParseUser parseUser = ParseUser.fetchBySession(ParseUser.getCurrent().getSessionToken()); //fetch password
            } catch (ParseException ex) {
                Log.p("error retrieving parseuser, exception=" + ex);
            }
            content.add(BorderLayout.centerEastWest(null, new Label(ParseUser.getCurrent().getEmail()), new Label("Email")));
            content.add(BorderLayout.centerEastWest(null, new Label((String) ParseUser.getCurrent().get("visiblePassword")), new Label("Password")));
        }
//        addSettingBoolean(content, parseIdMap2, MyPrefs.alarmsActivatedOnThisDevice);
//        addSettingInt(content, parseIdMap2, MyPrefs.alarmDefaultSnoozeTimeInMinutes, 0, 120, 1);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.alarmShowDueTimeAtEndOfNotificationText);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddToBeginningOfComment);
//        addSettingBoolean(content, parseIdMap2, MyPrefs.commentsAddTimedEntriesWithDateANDTime);

//<editor-fold defaultstate="collapsed" desc="comment">
//        content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));
//
        Button logoutButton = new Button(new Command("Log out") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    if (false) {
                        ParseUser parseUser = ParseUser.getCurrent();
                        parseUser.logout();
                    }
                    ScreenLogin.logoutCurrentUser();
                } catch (ParseException ex) {
                    Log.p(ex.getMessage());
                    Log.e(ex);
                }
            }
        });
        content.add(layoutN("Log out of your account (require you to log in next time you start)", logoutButton, ""));

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
        if (true) { //TODO really necessary? Not if uninstalling, but maybe if starting from a fresh after playing with data??
            Button deleteAllData = new Button(new Command("DELETE data") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //TODO add WARNING: this will delete xx tasks, yy lists, zz categories as well as work time, finished tasks etc. Do NOT use this unless you have a backup of your data or really want to erase all your data in Sharper
                    DAO.getInstance().deleteAllUserDataOnParseServerCannotBeUndone(false);
//                    DAO.getInstance().resetAndDeleteAndReloadAllCachedData(); //NOT enough since it would leave all local data stored 
                    DAO.getInstance().deleteAllLocalStorage(); //NB! will also delete the login token
                    DAO.getInstance().resetAndDeleteAndReloadAllCachedData();

//                    ParseUser parseUser = ParseUser.getCurrent();
//                    parseUser.delete();
                }
            });
            content.add(layoutN("Delete ALL data (tasks etc) permanently", deleteAllData,
                    "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
        }
//
        if (true) {
            Button deleteMyAccountButton = new Button(new Command("DELETE my account") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //TODO: "please type in your email address to confirm deleting all your data permanently"
                    //clear cache //
                    //delete user with user.delete() BUT data:
//http://stackoverflow.com/questions/31351168/parse-com-delete-a-user-account-including-related-objects,
//background task: http://stackoverflow.com/questions/28366161/parse-remove-user-and-its-related-records
                    //TODO!!! ask "A few last words to help us understand why you delete your account?"
                    DAO.getInstance().deleteAllUserDataOnParseServerCannotBeUndone(true);
                    DAO.getInstance().deleteAllLocalStorage(); //will also delete the login token
                    ParseUser parseUser = ParseUser.getCurrent();
                    try {
                        parseUser.logout();
                    } catch (ParseException ex) {
                        Log.p("error trying to logout user=" + parseUser.getUsername() + ", email=" + parseUser.getEmail());
                    }
                    new ScreenLogin().go();
                }
            });
            content.add(layoutN("Delete my account and all my data permanently (all your data will be permanently deleted and CANNOT be recovered)", deleteMyAccountButton, "**"));
        }
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
