package com.todocatalyst.todocatalyst;

//import com.codename1.io.Log;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseException;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.Resources;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.parse4cn1.ParseACL;
import com.parse4cn1.ParseUser;
import java.util.HashMap;

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
public class ScreenLogin extends MyForm {
    //TODO store login+password locally (or at least user key?)
    //TODO skip login screen when already logged in
    //TODO Settings option to log out

//    private MyForm previousForm;
    final static String INTRO_TEXT = "Welcome to TodoCatalyst. A todo list like no other."
            + " Manage your projects, manage your time, manage your priorities. Be efficient."
            + " Achieve personal efficiency. "
            + " Be efficient"
            + " Be efficient"
            + " Most features are free. "
            + " Unique powerful features available for Pro and Advanced subscribers. "
            + " Learn more here. "
            + " Learn more here. ";
    final static String LOGIN_TEXT = "Welcome to TodoCatalyst. A todo list like no other.";
    final static String CREATE_NEW_USER_BUTTON = "Create account";
    final static String LOGIN_BUTTON = "Log in";
    final static String START_BUTTON = "Start";
    final static String ANONYMOUS_BUTTON = "Try without account.";
    final static String ANONYMOUS_TEXT = "Try TodoCatalyst without creating an account. To log in from a new device, or from several devices, you will need to create an account with your email and password. You can add your email later in Settings.";
    final static String ANONYMOUS_BUTTON2 = "Let me try. I can create an account later.";

    ScreenLogin(MyForm mainScreen) { // throws ParseException, IOException {
        super("Login", null, () -> {
        });
        this.previousForm = mainScreen;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
        // we use border layout so the list will take up all the available space
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        form.setToolbar(new Toolbar());
//        addCommandsToToolbar(form.getToolbar(), theme);
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
    }

    private String CURRENT_USER_STORAGE_ID = "parseCurrentUser";
    private String CURRENT_USER_USER_NAME = "parseUserName";
    private String CURRENT_USER_USER_EMAIL = "parseUserEmail";
    private static String CURRENT_USER_SESSION_TOKEN = "parseUserSessionToken";
    private String CURRENT_USER_PASSWORD = "parseUserPsWd";

    private boolean saveCurrentUserToStorage(ParseUser parseUser) {
//        HashMap h = new HashMap();
//        h.put(CURRENT_USER_USER_NAME, parseUser.getUsername());
//        h.put(CURRENT_USER_USER_EMAIL, parseUser.getEmail());
//        h.put(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
//        h.put(CURRENT_USER_PASSWORD, parseUser.getUsername());
        return Storage.getInstance().writeObject(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
    }

    static public ParseUser getCurrentUserFromStorage() {
        String sessionToken = (String) Storage.getInstance().readObject(CURRENT_USER_SESSION_TOKEN);
        Log.p("Retrieved Sessiontoken=" + sessionToken);
        if (sessionToken == null || sessionToken.equals("")) {
            return null;
        }
        try {
            //        ParseUser parseUser = new ParseUser();
//        HashMap h = (HashMap) Storage.getInstance().readObject(CURRENT_USER_STORAGE_ID);
//        return new ParseUser().set(ParseUser) Storage.getInstance().readObject(CURRENT_USER_STORAGE_ID);
//            return ParseUser.fetchBySession((String) Storage.getInstance().readObject(CURRENT_USER_SESSION_TOKEN));
            ParseUser parseUser = ParseUser.fetchBySession(sessionToken);
            return parseUser;
        } catch (ParseException ex) {
            Log.e(ex); //TODO!!!!!: "your session has expired, please log in again"
        }
        return null;
    }

    static void setDefaultACL(ParseUser parseUser) {
        ParseACL defaultACL = new ParseACL();
        defaultACL.setReadAccess(parseUser, true);
        defaultACL.setWriteAccess(parseUser, true);
        defaultACL.setPublicReadAccess(false);
        defaultACL.setPublicWriteAccess(false);
        ParseACL.setDefaultACL(defaultACL, true);
        Log.p("Setting default ACL for user");
    }

    private boolean createAccount(String email, String password) {
        if (email == null || email.length() == 0) {
            email = PasswordGenerator.getInstance().generateNumbers("Incognito", 8); //"Incognito53976245"
        }

        if (password == null || password.length() == 0) {
            password = PasswordGenerator.getInstance().generate(12);
        }

        try {
            ParseUser parseUser = ParseUser.create(email, password);
            parseUser.setEmail(email); //
            parseUser.signUp();//perform login
            setDefaultACL(parseUser);
        } catch (ParseException ex) {
            Log.p(ex.getMessage());
            Log.e(ex);
        }

        try {
            //generate arbitrary password

//                    ParseUser parseUser = ParseUser.create(loginId,psword);
            ParseUser parseUser = ParseUser.create(email, password);
            parseUser.login();//perform login
        } catch (ParseException ex) {
            switch (ex.getCode()) {
                case ParseException.EMAIL_TAKEN:
                case ParseException.ACCOUNT_ALREADY_LINKED:
                case ParseException.INVALID_EMAIL_ADDRESS:
                case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP:
                case ParseException.USERNAME_TAKEN:
                case ParseException.USERNAME_MISSING:
                case ParseException.PASSWORD_MISSING:
                //pb with login email or 
                case ParseException.CLOUD_ERROR:
                case ParseException.CONNECTION_FAILED:
                //internal error, code NNN, please try again (or rather auto-retry)
                case ParseException.EXCEEDED_QUOTA:
                case ParseException.INCORRECT_TYPE:
                case ParseException.INTERNAL_SERVER_ERROR:
                case ParseException.NOT_INITIALIZED:
                case ParseException.OTHER_CAUSE:
                //internal error, please provide your email to be notified when the problem is resolved, or tray again later (or automatically send me an email)
            }
        }
        if (Dialog.show("ERROR", "Your email is already associcated with an account", "Login to try again", "I forgot my password")) {

        } else {

        };
        Dialog.show("ERROR", "Your email already exists (or other error)", "Try again", null);
        Log.p("***login failed***");
        return false;
    }

    private boolean checkValidEmail(String email) {
        Constraint checkEmail = RegexConstraint.validEmail();
        if (email.length() == 0 || !checkEmail.isValid(email)) {
            Dialog.show("INFO", "Please enter an email address in the format xxx@yyy.zzz", "OK", null);
            return false;
        }
        return true;
    }

    //    public static void addCommandsToToolbar(Toolbar toolbar, Resources theme) {
    //        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
    //    }
    /**
     * This method shows the main user interface of the app
     *
     * @param back indicates if we are currently going back to the main form
     * which will display it with a back transition
     * @param errorMessage an error message in case we are returning from a
     * search error
     * @param listings the listing of alternate spellings in case there was an
     * error on the server that wants us to prompt the user for different
     * spellings
     */
    //    private Container buildContentContainer(boolean back, String errorMessage, java.util.List<Map<String, Object>> listings) {
    private Container buildContentPane(Container content) {
        parseIdMapReset();
//        Container content = new Container();
        BorderLayout bl = new BorderLayout();
        bl.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
        Container cont = new Container(bl);

        Image icon = UIManager.getInstance().getThemeImageConstant("icon.png");
        Label welcome = new Label("WELCOME", icon); //localized
        welcome.setAutoSizeMode(true);
        welcome.setTextPosition(Label.BOTTOM);
        cont.add(BorderLayout.NORTH, welcome);

        Container fieldsCont = new Container(BoxLayout.y()); //contains all the fields
        Container optionButtons = new Container(GridLayout.autoFit()); //contains the buttons to switch login mode

//        fetchResourceFile().getImage(
//        Image icon = Resources.opgetSystemResource().getTheme().getImage("icon.png");
//        Image icon = Resources.getSystemResource().getTheme().getImage("icon.png");
//        cont.add(BorderLayout.CENTER, new Label(Icons.iconCheckboxCreated)); //TODO: load app icon and scale up
        cont.add(BorderLayout.CENTER, fieldsCont); //TODO: load app icon and scale up
        fieldsCont.add(new Label(Icons.iconCheckboxCreated)); //TODO: load app icon and scale up

        SpanLabel incognitoWarning = new SpanLabel("ANONYMOUS_WARNING");
        incognitoWarning.setHidden(true);
        SpanLabel createAccountInfo = new SpanLabel("To start using [TODOCATALYST] simply enter you email.");

        TextField email = new TextField("", "Email:", 20, TextArea.EMAILADDR);

        TextField password = new TextField("", "Password", 20, TextArea.ANY);
        password.setHidden(true); //default hidden
        Button remindPsWdButton = new Button(new Command("Forgotten password") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
                    ParseUser.requestPasswordReset(email.getText()); //TODO read password - copy Amazon usability
                } catch (ParseException ex) {
                    Log.e(ex);
                }
            }
        });
        if (false) {
            content.add(remindPsWdButton);
        }

        //Create
        SpanButton selectCreateAccount = new SpanButton();
        selectCreateAccount.setCommand(Command.create("New user", Icons.iconPersonNew, (ev) -> {
            ScreenLogin.this.animate();

        }));
        SpanButton createAccount = new SpanButton();
        createAccount.setCommand(Command.create("Start", Icons.iconPersonNew, (ev) -> {
            ScreenLogin.this.animate();

        }));

        //Login
        SpanButton login = new SpanButton();
        login.setUIID("Label");
        login.setCommand(Command.create("Login", Icons.iconPerson, (ev) -> {
            password.setHidden(false);
            createAccountInfo.setHidden(true);
            login.setHidden(true);
            createAccount.setHidden(true);
            ScreenLogin.this.animate();

        }));

        //Incognito
        SpanButton incognito = new SpanButton();
        SpanButton selectIncognito = new SpanButton();
        selectIncognito.setUIID("Label");
        selectIncognito.setCommand(Command.create("New user", Icons.iconPersonIngocnito, (ev) -> {
            Dialog.show("INFO", "Use incognito. \n" // "You will use [TODOCATALYST] with an incognito account. "
                    + "This means you cannot log in from another device. If you lose this device you cannot recreate your account and your all your data will be lost."
                    + "If you want convert this a permanent account please enter your email in [PREFERENCES]"
                    + "Your data will be lost if If you do not use your account it may be deleted after 6 months of inactivity. ",
                    "OK", null);
            incognitoWarning.setHidden(false);
            incognito.setHidden(false);
            ScreenLogin.this.animate();
        }));

        incognito.setUIID("Label");
        incognito.setCommand(Command.create("Incognito", Icons.iconPersonIngocnito, (ev) -> {
            Dialog.show("INFO", "Use incognito. \n" // "You will use [TODOCATALYST] with an incognito account. "
                    + "This means you cannot log in from another device. If you lose this device you cannot recreate your account and your all your data will be lost."
                    + "If you want convert this a permanent account please enter your email in [PREFERENCES]"
                    + "Your data will be lost if If you do not use your account it may be deleted after 6 months of inactivity. ",
                    "OK", null);
            incognitoWarning.setHidden(false);
            optionButtons.replace(this, null, null);
            ScreenLogin.this.animate();
        }));

        //START        
        SpanButton start = new SpanButton();
        start.setCommand(Command.create("Start", Icons.iconPersonNew, (ev) -> {
            Constraint checkEmail = RegexConstraint.validEmail();
            if (email.getText().length() == 0 && checkEmail.isValid(email.getText())) {
                createAccount(email.getText(), null); //null => autogenerated password
            }
            ScreenLogin.this.animate();

        }));

        fieldsCont.addAll(incognitoWarning, email, password, createAccount, optionButtons);
        Container fields = new Container(BoxLayout.y());

        cont.add(BorderLayout.SOUTH, fields); //TODO: load app icon and scale up
        fields.add(email); //TODO: load app icon and scale up

        fields.add(new Button(Command.create("Log in to my account", Icons.iconCreateSubTask, (ev) -> {
            password.setHidden(false);
            login.setHidden(false);
        })));
        password.setHidden(true);
        fields.add(password); //TODO: load app icon and scale up
        fields.add(login);

//        tl.setGrowHorizontally(true);
//        content.setLayout(tl);
        String loginId = ""; //use email as login id
        String psword = "";
        TextField login2 = new TextField(loginId, "Email address", 20, TextArea.EMAILADDR);
//        login.setSingleLineTextArea(true);
        content.add("Email").add(login2);
        setEditOnShow(login2); //UI: start editing this field
//        TextField password2 = new TextField(psword, "Password", 20, TextArea.ANY);
//        content.add("Password").add(password2);

        Validator v = new Validator();
        v.addConstraint(login2, RegexConstraint.validEmail("Please enter valid email address (xxx@yyy.zzz)"));
//        v.addConstraint(password2, new LengthConstraint(2, "Password must be at least 2 characters"));
        v.addConstraint(password, new LengthConstraint(2, "Password must be at least 2 characters"));

        Button loginButton = new Button(new Command("Login") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
                    ParseUser parseUser = ParseUser.create(login2.getText(), password.getText());
                    parseUser.login();//perform login
                } catch (ParseException ex) {
                    Log.p("***login failed***");
                    Log.e(ex);
                }
                previousForm.show();
            }
        });
        if (false) {
            content.add(loginButton);
        }

        Button loginTestButton = new Button(new Command("Test Login (min)") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ParseUser parseUser = null;
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
                    parseUser = ParseUser.create("thomas.hjelm@email.com", "ItsThomas");
                    parseUser.login();//perform login
                    saveCurrentUserToStorage(parseUser);
                } catch (ParseException ex) {
                    Log.p("***login failed***");
                    Log.e(ex);
                }
                setDefaultACL(parseUser);
//                ParseACL defaultACL = new ParseACL();
//                defaultACL.setReadAccess(parseUser, true);
//                defaultACL.setWriteAccess(parseUser, true);
//                defaultACL.setPublicReadAccess(false);
//                defaultACL.setPublicWriteAccess(false);
//                ParseACL.setDefaultACL(defaultACL, true);
//                Item test = new Item("withPassWd");
//                test.setACL(new ParseACL(parseUser));
//                DAO.getInstance().save(test);
                previousForm.show();
            }
        });
        if (false) {
            content.add(loginTestButton);
        }

        Button createUserButton = new Button(new Command("Create my account") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
//                    ParseUser parseUser = ParseUser.create(login2.getText(), password2.getText());
                    ParseUser parseUser = ParseUser.create(login2.getText(), password.getText());
                    parseUser.setEmail(login2.getText()); //
                    parseUser.signUp();//perform login
                    setDefaultACL(parseUser);
//                    parseUser.save(); //save this new user
                    //TODO: user must confirm email before syncing(?)
                } catch (ParseException ex) {
                    Log.p(ex.getMessage());
                    Log.e(ex);
                }
                previousForm.show();
            }
        });
        if (false) {
            content.add(createUserButton);
        }

        Button logOutButton = new Button(new Command("Log out from my account") {
            @Override
            public void actionPerformed(ActionEvent evt) {
                try {
//                    ParseUser parseUser = ParseUser.create(loginId,psword);
//                    ParseUser parseUser = ParseUser.create(login2.getText(), password2.getText());
                    ParseUser parseUser = ParseUser.create(login2.getText(), password.getText());
                    parseUser.setEmail(login2.getText()); //
                    parseUser.signUp();//perform login
                    parseUser.save(); //save this new user
                    //TODO: user must confirm email before syncing(?)
                } catch (ParseException ex) {
                    Log.p(ex.getMessage());
                    Log.e(ex);
                }
                previousForm.show();
            }
        });
        if (false) {
            content.add(logOutButton);
        }

//        Button createSkipButton = new Button(new Command("Skip") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                mainScreen.show();
//            }
//        });
//        content.add(createSkipButton);
//        Button remindPsWdButton = new Button(new Command("Reset my password") {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                try {
//                    ParseUser.requestPasswordReset(login2.getText()); //TODO read password - copy Amazon usability
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//            }
//        });
        if (false) {
            content.add(remindPsWdButton);
        }

        Button masterLoginButton = new Button(new Command("MasterLogin") {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
                previousForm.show();
            }
        });
//        v.addSubmitButtons(loginButton); //disable Login button until both valid 
        if (false) {
            content.add(masterLoginButton);
        }
        return content;
    }
}
