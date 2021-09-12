package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.parse4cn1.ParseException;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.parse4cn1.ParseUser;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Account screen. user is authenticated (via account creation or login):
 * possible to change email, or delete account but not to logout or reset
 * password. Email is confirmed: possible to logout and reset password.
 *
 * What happens if you enter a wrong email and it belongs to someone else? If
 * the email holder doesn't do anything, you can change the email in TDC. If the
 * email holder confirms
 *
 * Three main states determine what can be done: anonymoius/trial user with no
 * email: only option is to set email or delete account; user is authenticated
 * (via account creation or login): possible to change email, but not to logout
 * email entered but not confirmed: only option email defined and confirmed:
 * possible to change email, logout, and reset password.
 *
 * Situatioons to handle: entered wrong email on account creation, need to
 * change email (without access to old email account, e.g. changed job).
 *
 * @author Thomas
 */
//public class ScreenSettings extends MyForm {
public class ScreenAccount extends MyForm {
    //TODO store login+password locally (or at least user key?)
    //TODO Settings option to log out
    //DONE support option to update email (and change login id since email is normally used)
    //DONE skip login screen when already logged in

    public final static String SCREEN_TITLE = "Account";
    public final static String SCREEN_HELP = "**";

    private ParseUser parseUser; //fetch password

// protected static String FORM_UNIQUE_ID = "ScreenSettings"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//    MyForm mainScreen;
    ScreenAccount(MyForm previousScreen) { // throws ParseException, IOException {
        super(SCREEN_TITLE, previousScreen, () -> {
        });
        setUniqueFormId("ScreenSettings");
        makeContainerBoxY();
        addCommandsToToolbar();

        try {
            parseUser = ParseUser.fetchBySession(ParseUser.getCurrent().getSessionToken());
        } catch (ParseException ex) {
            Log.p("error retrieving parseuser, exception=" + ex);
        }
        if (parseUser != null) {
            userAuthenticated = parseUser.isAuthenticated();
            emailSet = parseUser.getEmail() != null && !parseUser.getEmail().isEmpty() && parseUser.isAuthenticated();
            emailVerified = emailSet && parseUser.getBoolean("emailVerified") != null && parseUser.getBoolean("emailVerified");
        }

        refreshAfterEdit();
    }

    private boolean emailSet;
    private boolean emailVerified;
    private boolean userAuthenticated;

    public void refreshAfterEdit() {
        buildContentPane(container);
//        revalidate();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar() {
        Toolbar toolbar = getToolbar();
        //DONE/BACK
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
        addStandardBackCommand();

        if (Config.TEST) {
            toolbar.addCommandToOverflowMenu(Command.create("User authenticated", null, (e) -> {
                userAuthenticated = !userAuthenticated;
                refreshAfterEdit();
            }));
            toolbar.addCommandToOverflowMenu(Command.create("Email defined", null, (e) -> {
                emailSet = !emailSet;
                if (!emailSet) {
                    emailVerified = false; //cannot have a verified email if no email is set!
                }
                refreshAfterEdit();
            }));
            toolbar.addCommandToOverflowMenu(Command.create("Email verified", null, (e) -> {
                emailVerified = !emailVerified;
                refreshAfterEdit();
            }));
            toolbar.addCommandToOverflowMenu(Command.create("Parseuser= null", null, (e) -> {
                parseUser = null;
                refreshAfterEdit();
            }));
        }

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }
    }

    /**
     * This method shows the main user interface of the app
     *
     */
//    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    protected void buildContentPane(Container content) {

        content.removeAll();
        if (parseUser == null) {
            content.add(new SpanLabel("Something's gone wrong during account creation. "
                    + "If you want to keep your account please email support@todocatalyst.com and include the email you used to create the account and this text"
                    + "\n\nUSER ACCOUNT [PARSE USER IS NULL]\n\n"));

        } else if (!parseUser.isAuthenticated()) {//UI: authentication is a Parse Server pre-condition to enable updating email etc.
            content.add(new SpanLabel("Something's gone wrong during account creation. "
                    + "If you want to keep your account please email support@todocatalyst.com and include this text"
                    + "\n\nUSER ACCOUNT [" + parseUser.getUsername() + "] NOT AUTHENTICATED\n\n"));
        } else {

//        if (false) {
//            addSettingBoolean(content, parseIdMap2, MyPrefs.encryptTaskTextAndComments);
//        }
//        if (false) {
//            addSettingBoolean(content, parseIdMap2, MyPrefs.deleteLocalStorageIfRestartedQuickly);
//        }
//        addSettingInt(content, parseIdMap2, MyPrefs.deleteLocalStorageIfRestartedBeforeSeconds, 5, 120, 5); //kin, max, step
           if(Config.TEST) content.add("User auth=" + userAuthenticated + " Email set=" + emailSet + " verif=" + emailVerified);

            {
                String currentEmail = parseUser.getEmail();
//            Boolean emailVerified = parseUser.getBoolean("emailVerified") != null && parseUser.getBoolean("emailVerified");
//            boolean emailSet = currentEmail != null && !currentEmail.isEmpty() && parseUser.isAuthenticated();

                TextField emailAddressTextField = new TextField("", "Email", 20, TextArea.EMAILADDR);
                if (currentEmail != null) {
                    emailAddressTextField.setText(currentEmail);
                }

                content.add(makeSpacer());

                //CHANGE EMAIL BUTTON
                //no email entered <=> temporary account <=> enter email and get email with password
//                    content.add(layoutN("Email", emailAddressTextField, SCREEN_HELP));
                content.add("Email:");
                content.add(emailAddressTextField);

                Button updateEmailButton = new Button(CommandTracked.create("Change email", Icons.iconUpdateEmail, (e) -> {
                    String emailTxt = emailAddressTextField.getText();
                    String errorMsg = ScreenLogin.validEmail(emailTxt);
                    if (errorMsg == null && !emailTxt.equals(ParseUser.getCurrent().getEmail())) {
                        if (Dialog.show("WARNING", "Please confirm you want to change your email.\n\n"
                                + "All future communication will be send to the new email, including resetting password.", "Yes, update", "Cancel")) {
                            parseUser.setEmail(emailTxt);
                            parseUser.setUsername(emailTxt);
                            try {
                                parseUser.save();
                                emailSet = true;
                                //TODO option to enter password
                            } catch (ParseException ex) {
                                Dialog.show("ERROR", "Something went wrong when updating your account. Please check your email address is correct and try again. "
                                        + "You can also email support@todocatalyst.com and mention this error: \n\n"
                                        + "EXCEPTION WHEN SAVING ACCOUNT USERID=[" + parseUser.getUsername() + "] OLD EMAIL=[" + parseUser.getEmail() + "] NEW EMAIL=[" + emailTxt + "], EXCEPTION=[" + ex.getLocalizedMessage() + "]",
                                        "OK", null);
                            }
                        }
                    } else {
                        if (errorMsg != null) {
                            Dialog.show("ERROR", errorMsg, "OK", null);
                        } // else if(emailTxt.equals(ParseUser.getCurrent().getEmail())) { //NO reason to show an error if email is the same
                    }
                }, (emailSet ? "ChangeEmail" : "SetEmailForTrialAccount")));
                updateEmailButton.setUIID("WideButton");
                content.add(updateEmailButton);

                //TRIAL ACCOUNT WARNING
                if (!emailSet || Config.TEST) {
//            content.add("Help: Enter your mail to create an account to backup your tasks and data");
                    content.add(new SpanLabel("You are using a trial account without a defined email. "
                            + "Enter your email to create your account and enable backup.", "ButtonHelpTextWarning"));
                }

                content.add(makeSpacer());

                //VISIBLE PASSWORD FOR TESTING
                if (Config.TEST && Config.TEST_STORE_PASSWORD_FOR_USER) {
                    content.add(BorderLayout.centerEastWest(null, new Label((String) ParseUser.getCurrent().get("visiblePassword")), new Label("Password (TEST)")));
                }

                content.add(makeSpacer());

                if (emailSet || Config.TEST) { //UI: only enable resetting password once a valid email is known and confirmed by the user!!

                    if (!emailVerified || Config.TEST) {
//                            content.add(new SpanLabel("You have not yet confirmed your email address by clicking on the link in the email you have received.\n\n"
//                                    + "Please confirm your email to enable resetting your password and changing your email."));
//                            content.add(new SpanLabel("You cannot reset your password until you have confirmed you email by clicking the link in the verification email.\n\n"
//                                    + "If you have not received the email please check your spam and that email is correct."));
                        SpanLabel confirmYourEmail = new SpanLabel("You need to confirm your email by clicking the link in the verification email you received.\n\n"
                                + "This is necessary to fully enable your account, for example to reset your password.\n\n"
                                + "If you have not received the email please check your spam, check your email address is correct, or click the button below to receive the mail again.", "ButtonHelpText");
                        confirmYourEmail.setUIID("ButtonHelpText");
                        content.add(confirmYourEmail);
                        Button resendVerificationEmailButton = new Button(CommandTracked.create("Resend verification email", Icons.iconSendVerificationEmail, (e) -> {
                            parseUser.setEmail(parseUser.getEmail()); //trick: set email to same email to force a verification email
                            try {
                                parseUser.save();
                                Dialog.show("", "You will get a confirmation email shortly", "OK",null);
                            } catch (ParseException ex) {
                                Dialog.show("ERROR", "Something went wrong when updating your account. Please check your email is correct and try again. "
                                        + "You can also email support@todocatalyst.com and mention this error: \n\n"
                                        + "EXCEPTION WHEN SAVING ACCOUNT USERID=[" + parseUser.getUsername() + "] OLD EMAIL=[" + parseUser.getEmail() + ", EXCEPTION=[" + ex.getLocalizedMessage() + "]",
                                        "OK", null);
                            }
                        }, "resendVerifEmail"));
                        resendVerificationEmailButton.setUIID("WideButton");
                        content.add(resendVerificationEmailButton);

                    }
                    if (emailVerified || Config.TEST) {
                        //RESET PASSWORD
                        Button resetPassword = new Button(CommandTracked.create("Reset password", Icons.iconResetPassword, (e) -> {
                            ParseUser user = ParseUser.getCurrent();
                            ASSERT.that(ParseUser.getCurrent().getEmail() == null || ParseUser.getCurrent().getEmail().isEmpty());
                            if (ParseUser.getCurrent().getBoolean("emailVerified") != null && ParseUser.getCurrent().getBoolean("emailVerified")) {
                                Dialog.show("ERROR", "You cannot log out until you have confirmed your email by clicking on the link in the confirmation email you have received.", "OK", null);
                            } else {
                                try {
                                    user.requestPasswordReset(currentEmail);
                                } catch (ParseException ex) {
                                    Dialog.show("ERROR", "You cannot log out until you have confirmed your email by clicking on the link in the confirmation email you have received.", "OK", null);
                                    Log.p(ex.getLocalizedMessage());
                                }
                            }
                            ScreenLogin.logoutCurrentUser(true);
//                    DAO.getInstance().clearAllCacheAndStorage(false);
                            new ScreenWelcome(null, false).show();
                        }, "ResetPassword"));
                        resetPassword.setUIID("WideButton");
                        content.add(resetPassword);
//        String resetPassWordText = "Reset password. You will receive an email with a link to safely reset and define a new password";
//        String resetPassWordTextHelp = "You can only reset your password when you have confirmed your email by clicking on the link in the confirmation email you have received";
//                            content.add(layoutN(resetPassword, "You will receive an email with a link to set a new password. "
//                                    + "You can only reset your password when you have confirmed your email by clicking "
//                                    + "on the link in the confirmation email you have received"));
                        SpanLabel resetPasswordHelp = new SpanLabel("Receive an email with a link to set a new password.", "ButtonHelpText");
                        content.add(resetPasswordHelp);
                    }

                    if (emailVerified || Config.TEST) {
                        //LOG OUT
                        Button logoutButton = new Button(CommandTracked.createMaterial("Log out", Icons.iconLogout, (e) -> {
                            if (!emailVerified) {
                                Dialog.show("ERROR", "You cannot log out until you have confirmed your email by clicking on the link in the confirmation email you have received.", "OK", null);
                            } else {
                                ScreenLogin.logoutCurrentUser(true); //NB logout with this fucntion will also clear all data on the device
                                new ScreenWelcome(null, false).show();
                            }
                        }));
//                            content.add(layoutN(logoutButton,
//                                    "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
                        logoutButton.setUIID("WideButton");
                        content.add(logoutButton);
                        content.add(new SpanLabel("Log out from this device. Next time you start TodoCatalyst, you will need to log in with your email and password.",
                                "ButtonHelpText"));
                    }

//                        if (false) {
////        content.add(layoutN("Log out of your account (require you to log in next time you start)", logoutButton, ""));
//                            content.add(layoutN("Log out", logoutButton, "Log out of your account (require you to log in next time you start)"));
//                            if (true) { //TODO really necessary? Not if uninstalling, but maybe if starting from a fresh after playing with data??
//                                Button deleteAllData = new Button(new Command("Delete account, tasks and data") {
//                                    @Override
//                                    public void actionPerformed(ActionEvent evt) {
//                                        //TODO add WARNING: this will delete xx tasks, yy lists, zz categories as well as work time, finished tasks etc. Do NOT use this unless you have a backup of your data or really want to erase all your data in Sharper
//                                        DAO.getInstance().deleteAllUserDataOnParseServerCannotBeUndone(false);
////                    DAO.getInstance().resetAndDeleteAndReloadAllCachedData(); //NOT enough since it would leave all local data stored 
//                                        DAO.getInstance().clearAllCacheAndStorage(false); //NB! will also delete the login token
//                                        DAO.getInstance().resetAndDeleteAndReloadAllCachedData();
//
////                    ParseUser parseUser = ParseUser.getCurrent();
////                    parseUser.delete();
//                                    }
//                                });
//                                content.add(layoutN("Delete ALL data (tasks etc) permanently", deleteAllData,
//                                        "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
//                            }
//                        }
                    //BACKUP DATA
                    if (false) {
//                        content.add(layoutN("Backup my data", new Button(CommandTracked.create("Backup my data", Icons.iconBackupData, (e) -> {
//                            ASSERT.that(false, "Not done yet");
//                        }, "BackupData")), "Create and email a backup file with all your data"));
                        Button backUpButton = new MyButtonLongPress(CommandTracked.create("Backup my data", Icons.iconBackupData, (e) -> {
                            ASSERT.that(false, "Not done yet");
                        }, "BackupData"), "Create and email a backup file with all your data.");
                        backUpButton.setUIID("WideButton");
                        content.add(backUpButton);
                    }

                    //DELETE ACCOUNT
//            Button deleteMyAccountButton = new Button(new Command("DELETE my account") {
//            Button deleteMyAccountButton = new Button(new CommandTracked("Delete account, tasks and data", "DeleteAccount") {
                    Button deleteMyAccountButton = new Button(CommandTracked.create("Delete account", Icons.iconDeleteAccount, (e) -> {
//                      "Delete ALL data (tasks etc) permanently", "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
                        if (Dialog.show("WARNING", "This will permanently delete all your TodoCatalyst tasks and data.\n\nDelete everything?", "Yes, delete", "No")) {
                            //TODO: "please type in your email address to confirm deleting all your data permanently"
                            //clear cache //
                            //delete user with user.delete() BUT data:
//http://stackoverflow.com/questions/31351168/parse-com-delete-a-user-account-including-related-objects,
//background task: http://stackoverflow.com/questions/28366161/parse-remove-user-and-its-related-records
                            //TODO!!! ask "A few last words to help us understand why you delete your account?"
                            DAO.getInstance().deleteAllUserDataOnParseServerCannotBeUndone(true);
                            ScreenLogin.logoutCurrentUser(true); //NB logout with this fucntion will also clear all data on the device
                            new ScreenWelcome(null, false).show();
                        }
//                        }, "DeleteAccount"), "Delete my account and all my data permanently (all your data will be permanently deleted and cannot be recovered - make backup)");
                    }, "DeleteAccount"));

                    deleteMyAccountButton.setUIID("WideButtonWarning");
//                        content.add(layoutN(deleteMyAccountButton,
//                                "Delete my account and all my data permanently (all your data will be permanently deleted and cannot be recovered - make backup)"));
                    content.add(deleteMyAccountButton);
//                        content.add(new SpanLabel("Delete account and all data permanently (all your data will be permanently deleted and cannot be recovered - make backup", "ButtonHelpText"));
//                    content.add(new SpanLabel("Delete permanently account and all data (account information, tasks, lists etc). Your data cannot be recovered after this.", "ButtonHelpText"));
                    content.add(new SpanLabel("Permanently delete this account and all tasks, lists, history, everything... Your data cannot be recovered after this.", "ButtonHelpText"));
//            content.add(layoutN("Delete ALL data (tasks etc) permanently", deleteAllData,
//                            "This will permanently delete all your data (tasks, lists, categories etc) from your account. The deleted data can NOT be restored in any way afterwards (there is .")); //TODO!!! show how many tasks etc, ask to enter email to confirm, add "I confirm I delete all my data and that they cannot be restored [v]"
                }

                //ACCOUNT CREATION DATE
                content.add(layoutN("Account created", new Label(MyDate.formatDateNew(ParseUser.getCurrent().getCreatedAt())), "Date when your account was created"));
            }
        }
    }

}
