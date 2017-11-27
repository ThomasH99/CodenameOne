package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.parse4cn1.ParseException;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
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
public class ScreenAccount extends ScreenSettingsCommon {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Settings";

//    MyForm mainScreen;
    ScreenAccount(MyForm mainScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, null, () -> {
        });
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {

        if (MyPrefs.loginIncognitoMode.getBoolean()) {
            content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));
        }

        content.add(layout("Account email", new SpanLabel(ParseUser.getCurrent().getEmail(), "Button"), true));

        Button logoutButton = new Button(new Command("Log out") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                String errorMsg = ScreenLogin2.logoutCurrentUser();
                if (errorMsg!=null) {
                    Dialog.show("", errorMsg, "OK", null);
                }
            }
        });

        if (!MyPrefs.loginIncognitoMode.getBoolean()) { //DON'T enable logging out in Incognito mode since that would lose the login
            content.add(layout("Log out from my account", logoutButton, "After you logout, you must enter your email and password to access your data again.", true));

            Button remindPsWdButton = new Button(new Command("Reset password") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    try {
                        ParseUser.requestPasswordReset(ParseUser.getCurrent().getEmail()); //TODO read password - copy Amazon usability
//                    ParseUser.getCurrent().logout(); //needed to explicitly log out?
                    } catch (ParseException ex) {
                        Log.e(ex);
                    }
                }
            });
//        content.add(remindPsWdButton).add(new SpanLabel("You will receive an email with a link to reset your password. NB. This will log you out until you have entered a new password."));
            content.add(layout("Reset my password", remindPsWdButton, "You will receive an email with a link to reset your password. NB. This will log you out until you have entered a new password."));
        }

        if (false) { //TODO really necessary? Not if uninstalling, but maybe if starting from a fresh after playing with data??
            Button deleteAllData = new Button(new Command("DELETE data") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //TODO add WARNING: this will delete xx tasks, yy lists, zz categories as well as work time, finished tasks etc. Do NOT use this unless you have a backup of your data or really want to erase all your data in Sharper 
                    DAO.getInstance().deleteAllUserDataCannotBeUndone();
                }
            });
            content.add(layout("Delete ALL data (tasks etc) permanently", deleteAllData, "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
        }

        if (false) {
            Button deleteMyAccountButton = new Button(new Command("DELETE account") {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //clear cache
                    //delete user with user.delete() BUT data:
//http://stackoverflow.com/questions/31351168/parse-com-delete-a-user-account-including-related-objects, 
//background task: http://stackoverflow.com/questions/28366161/parse-remove-user-and-its-related-records
                }
            });
            content.add(layout("Delete my account and all my data", deleteMyAccountButton, "**"));
        }

    }
}
