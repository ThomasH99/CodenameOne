/*
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.messaging.Message;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.util.EasyThread;
import com.parse4cn1.ParseACL;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.ParseUser;
import static com.todocatalyst.todocatalyst.MyUtil.cleanEmail;
//import com.codename1.admob;
//import com.codename1.nui.*;
//import com.codename1.nui.NTextField;

/**
 * The newsfeed form
 *
 * @author Shai Almog
 */
//public class ScreenLogin extends BaseForm {
public class ScreenLogin extends MyForm {
    //https://uxplanet.org/designing-ux-login-form-and-process-8b17167ed5b9
    //Sign Up Free
    //Log In

    /*
    final static String INTRO_TEXT = "Welcome to TodoCatalyst. A todo list like no other."
            + " Manage your projects, manage your time, manage your priorities. Be efficient."
            + " Achieve personal efficiency. "
            + " Be efficient"
            + " Be efficient"
            + " Most features are free. "
            + " Unique powerful features available for Pro and Advanced subscribers. "
            + " Learn more here. "
            + " Learn more here. ";
    
     */
//    private final static String welcome1 = "TodoCatalyst - a todo list like no other";
//    private final static String welcome1 = "TodoCatalyst - probably the best Todo app in the world. \nMost ToDo lists remind you have a lot of work. TodoCatalyst lets you know when you'll be done";
//    private final static String welcome1 = "TodoCatalyst\n\nProbably the best Todo app in the world.\n\nMost ToDo lists remind you have too much to do. TodoCatalyst lets you know when you can be done. \n\nSwipe to see more"; // Unparalleded features
//    private final static String welcome2 = "For people with more work than time, self-managed, wanting to highly professional/reliable/predictable.";
//    private final static String welcome2 = "For demanding users who need the features no other todo apps offer. See how new tasks impacts your deadlines or commitments. Be efficient.";
//    private final static String welcome3 = "Master priorities. Master time. \nTime-saving features like templates, copy-paste, multiple selections, ...";
//    private final static String welcome4 = "Time-saving features like templates, copy-paste, multiple selections, ...";
    private final String FORGOT_PASSWORD = "Forgot password";
    TextField email;

    TextField password;
    private boolean test;

    public ScreenLogin(MyForm previousForm, boolean forTesting) {
        //TODO change login screen to show 2 text/ad screens, swipe them left to get to login fields (like ?? app)
//        super("Login", previousForm, () -> {
//        super("Welcome to TodoCatalyst", previousForm, () -> {
//        super("Get started with TodoCatalyst", previousForm, () -> {
        super("Get started", previousForm, () -> {
        });
        test = forTesting;
//         setTitle("Welcome to TodoCatalyst");
//        setLayout(BoxLayout.y());
        getContentPane().setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
//        makeContainerBoxY();
//        setScrollableY(false);
//        container = new Container(BoxLayout.y());
//        container.setScrollableY(true);
//        getContentPane().addComponent(BorderLayout.SOUTH, container);
//Container buttonContainer = new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
//        buttonContainer.setScrollableY(true);
//        getContentPane().addComponent(BorderLayout.CENTER, container);

        if (test) {
            addStandardBackCommand();
        }
//        setupLoginScreen(container);
//        setupLoginScreen(container);
        setupLoginScreen();
//        AdMobManager ad;
//        NTextField n;
    }

    public ScreenLogin() {
        //TODO change login screen to show 2 text/ad screens, swipe them left to get to login fields (like ?? app)
//        this("Login", null, () -> {
//        });
        this(null, false);
//        AdMobManager ad;
//        NTextField n;
    }

    private void startUpXXX(boolean refreshDataInBackground) {
        EasyThread thread = EasyThread.start("cacheUpdate");
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (true) {
//                thread.run(() -> {
//                    DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                });
//            } else {
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            }
//            boolean refreshDataInBackground = true;
//        if (false && refreshDataInBackground) {
//            thread.run((success) -> {
////                if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(), true)) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////                if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(),
////                        MyPrefs.reloadChangedDataInBackground.getBoolean())) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                if (DAO.getInstance().cacheLoadDataChangedOnServer(false)) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                    success.onSucess(null);
//                }
//                thread.kill();
////                success.onSucess(success); //CN1 Support: is there an error in CN1 for the run(r,t) call?!!!
//            }, (notUsed) -> {
//                Display.getInstance().callSerially(() -> {
////                if (newDataLoaded) {
//                    Form f = Display.getInstance().getCurrent();
//                    //don't removeFromCache: ScreenLogin (only shown on startup), ScreenMain (no item data shown), ScreenItem (could overwrite manually edited values - not with new version!)
//                    if (f instanceof MyForm && !(f instanceof ScreenLogin) && !(f instanceof ScreenMain)) { // && !(f instanceof ScreenItem)) {
//                        //TODO!!! show "Running" symbyl after like 2 seconds
////                    Display.getInstance().Log.p("refreshing Screen: "+((MyForm) f).getTitle());
//                        ((MyForm) f).refreshAfterEdit(); //to show any new data which may have been loaded
//                        Log.p("Screen " + getComponentForm().getTitle() + " refreshed after loading new data from network");
//                    }
////                    thread.kill();
////                }
//                });
//            });
//        } else {
//            Dialog ip = new InfiniteProgress().showInfiniteBlocking(); //DONE in DAO.cacheLoadDataChangedOnServer
//TODO!!!! show waiting symbol "loading your tasks..."
//            DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(), true); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            DAO.getInstance().cacheLoadDataChangedOnServer(true || MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(),
//                    false && MyPrefs.reloadChangedDataInBackground.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//</editor-fold>
        DAO.getInstance().cacheLoadDataChangedOnServer(false); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            ip.dispose();
//        }
        //ALARMS - initialize
//        AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
        AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(); //TODO!!!! optimization: do in background
//<editor-fold defaultstate="collapsed" desc="comment">

//TIMER - was running when app was moved to background? - now done with ReplayCommand
//            if (!ScreenTimer.getInstance().isTimerActive()) {
//                new ScreenMain().show(); //go directly to main screen if user already has a session
//            } else {
//                if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//                }
//            }
//</editor-fold>
        new ScreenMain(this).show(); //if pb with Timer relaunch, go to main screen instead
    }

//    public void go() {
//        go(false);
//    }
    public static boolean isAlreadyLoggedIn(boolean forceLaunchForTest) {
        //Check if already logged in, if so, removeFromCache cache
        //if not logged in, show window to create account or log in
//        ParseUser parseUser = getLastUserSessionFromStorage();
//        Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
        ParseUser parseUser = ParseUser.getCurrent();

        if (!forceLaunchForTest && parseUser != null) { //already logged in
            setDefaultACL(parseUser); //TODO needed??
            return true;
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                int count = -1;
//                ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//                try {
//                    count = query.count();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//                Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//            }

//            startUp(true);
//            startUp(MyPrefs.reloadChangedDataInBackground.getBoolean()); //for now, deactivate
//            //            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//            EasyThread thread = EasyThread.start("cacheUpdate");
////            if (true) {
////                thread.run(() -> {
////                    DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////                });
////            } else {
////                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////            }
//            boolean refreshDataInBackground = true;
//            if (refreshDataInBackground) {
//                thread.run((success) -> {
//                    if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean())) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                        success.onSucess(null);
//                    }
//                    thread.kill();
////                success.onSucess(success); //CN1 Support: is there an error in CN1 for the run(r,t) call?!!!
//                }, (notUsed) -> {
//                    Display.getInstance().callSerially(() -> {
////                if (newDataLoaded) {
//                        Form f = Display.getInstance().getCurrent();
//                        //don't removeFromCache: ScreenLogin (only shown on startup), ScreenMain (no item data shown), ScreenItem (could overwrite manually edited values)
//                        if (f instanceof MyForm && !(f instanceof ScreenLogin) && !(f instanceof ScreenMain) && !(f instanceof ScreenItem)) {
//                            //TODO!!! show "Running" symbyl after like 2 seconds
////                    Display.getInstance().Log.p("refreshing Screen: "+((MyForm) f).getTitle());
//                            ((MyForm) f).refreshAfterEdit();
//                            Log.p("Screen " + getComponentForm().getTitle() + " refreshed after loading new data from network");
//                        }
////                    thread.kill();
////                }
//                    });
//                });
//            } else {
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            }
//            //ALARMS - initialize
//            AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
//
//            //TIMER - was running when app was moved to background? - now done with ReplayCommand
////            if (!ScreenTimer.getInstance().isTimerActive()) {
////                new ScreenMain().show(); //go directly to main screen if user already has a session
////            } else {
////                if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
////                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
////                }
////            }
//            new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//        } else {
////            setupLoginScreen();
//////                new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//////            new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
////            show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//        }
//</editor-fold>
        }
        return false;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static void go(boolean forceLaunchForTest) {
//        //Check if already logged in, if so, removeFromCache cache
//        //if not logged in, show window to create account or log in
////        ParseUser parseUser = getLastUserSessionFromStorage();
////        Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
//        ParseUser parseUser = ParseUser.getCurrent();
//
//        if (!forceLaunchForTest && parseUser != null) { //already logged in
//            setDefaultACL(parseUser); //TODO needed??
//            if (false) {
//                int count = -1;
//                ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//                try {
//                    count = query.count();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//                Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//            }
//
////            startUp(true);
//            startUp(MyPrefs.reloadChangedDataInBackground.getBoolean()); //for now, deactivate
////<editor-fold defaultstate="collapsed" desc="comment">
////            //            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
////            EasyThread thread = EasyThread.start("cacheUpdate");
//////            if (true) {
//////                thread.run(() -> {
//////                    DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//////                });
//////            } else {
//////                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//////            }
////            boolean refreshDataInBackground = true;
////            if (refreshDataInBackground) {
////                thread.run((success) -> {
////                    if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean())) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////                        success.onSucess(null);
////                    }
////                    thread.kill();
//////                success.onSucess(success); //CN1 Support: is there an error in CN1 for the run(r,t) call?!!!
////                }, (notUsed) -> {
////                    Display.getInstance().callSerially(() -> {
//////                if (newDataLoaded) {
////                        Form f = Display.getInstance().getCurrent();
////                        //don't removeFromCache: ScreenLogin (only shown on startup), ScreenMain (no item data shown), ScreenItem (could overwrite manually edited values)
////                        if (f instanceof MyForm && !(f instanceof ScreenLogin) && !(f instanceof ScreenMain) && !(f instanceof ScreenItem)) {
////                            //TODO!!! show "Running" symbyl after like 2 seconds
//////                    Display.getInstance().Log.p("refreshing Screen: "+((MyForm) f).getTitle());
////                            ((MyForm) f).refreshAfterEdit();
////                            Log.p("Screen " + getComponentForm().getTitle() + " refreshed after loading new data from network");
////                        }
//////                    thread.kill();
//////                }
////                    });
////                });
////            } else {
////                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////            }
////            //ALARMS - initialize
////            AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
////
////            //TIMER - was running when app was moved to background? - now done with ReplayCommand
//////            if (!ScreenTimer.getInstance().isTimerActive()) {
//////                new ScreenMain().show(); //go directly to main screen if user already has a session
//////            } else {
//////                if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//////                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//////                }
//////            }
////            new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
////</editor-fold>
//
//        } else {
//            setupLoginScreen();
////                new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
////            new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//            show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//        }
//    }
//</editor-fold>
//    private void setupLoginScreen(Container container) {
    private void setupLoginScreen() {

        getContentPane().setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));
//        makeContainerBoxY();
        container = new Container(BoxLayout.y());
        container.setScrollableY(false);
//        container.setScrollableY(true);
        getContentPane().addComponent(BorderLayout.SOUTH, container);

        //TODO intro quiz: you tired of running into limitations in (miss features) in other ToDO apps, your tasks tend to pile up endlessly?, it is important for to be fully control/appear professional?
//        super("Welcome to TodoCatalyst", BoxLayout.y());
//        setTitle("Welcome to TodoCatalyst");
////        setLayout(BoxLayout.y());
//        makeContainerBoxY();
        if (test) {
            addStandardBackCommand();
        }

//        getTitleArea().setUIID("Container");
//        setTitle("TodoCatalyst");
//        getContentPane().setScrollVisible(false);
//        getContentPane().setAlwaysTensile(false); //only scroll if needed(???)
        //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
//        getToolbar().setUIID("Container");
//        getToolbar().hideToolbar();
        email = new TextField("", "Email", 20, TextArea.EMAILADDR);
//        NTextField email = new NTextField(TextArea.USERNAME); //does USERNAME remember login (where EMAILADDR doesn't seem to)?
//        TextField email = new TextField(TextArea.USERNAME); //does USERNAME remember login (where EMAILADDR doesn't seem to)?
        if (MyPrefs.loginStoreEmail.getBoolean()) {
            email.setText(MyPrefs.loginEmail.getString());
        }

        password = new TextField("", "Password", 20, TextArea.PASSWORD);
        password.addDataChangedListener((type, index) -> {
            String passWordStrength = PasswordGenerator.calculatePassword(password.getText());
            password.setUIID("Password"+passWordStrength);
            password.repaint();
        });
        password.setHidden(true); //hide by default
//        NTextField password = new NTextField(TextArea.PASSWORD); //https://www.codenameone.com/blog/native-controls.html,         new NTextField(TextField.PASSWORD)
//        TextComponentPassword password = new TextComponentPassword(); //https://www.codenameone.com/blog/native-controls.html,         new NTextField(TextField.PASSWORD)
//        password.constraint(TextArea.PASSWORD);
//        NTextField password = new NTextField(TextArea.PASSWORD); //https://www.codenameone.com/blog/native-controls.html,         new NTextField(TextField.PASSWORD)
//        TextField password = new TextField(TextArea.PASSWORD); //https://www.codenameone.com/blog/native-controls.html,         new NTextField(TextField.PASSWORD)
        if (false) {
            password.setUIID("TextField");
        }

//        NTextField password = new NTextField( TextArea.PASSWORD); //https://www.codenameone.com/blog/native-controls.html,         new NTextField(TextField.PASSWORD)
//        BorderLayout b1 = new BorderLayout();
//        b1.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
        Button signUp = new Button("", "BigButton");
        Button createAccount = new Button("", "BigButton");
//        Component createAccountWithRandomPassword = ScreenSettingsCommon.makeEditBoolean("Send a generated password to my email",
        Component createAccountWithRandomPassword = ScreenSettingsCommon.makeEditBoolean("Auto-generate a password and send it to my email",
                "", () -> password.isHidden(), (b) -> {
                    password.setHidden(b);
                    password.getComponentForm().animateHierarchy(300);
                });
        SpanLabel createAccountHelp = new SpanLabel("Sign up back up your tasks", "ButtonHelpText");
        Button createAccountLater = new Button("", "BigButton");
        SpanLabel createAccountLaterHelp = new SpanLabel("Start with a trial account and sign up later to start backing up your tasks", "ButtonHelpText");
        Button connect = new Button("", "BigButton");
        Button login = new Button("", "Button");
//        Button showHelp = new MyOnOffSwitch(null, e->MyPrefs.addCommentWhenRemaningIsSetToZeroWhenTaskBecomesProject);
        Button backToSignupSignIn = new Button("", "SmallButton");
//        Button forgottenPassword = new Button();
        Button forgottenPassword = new Button("", "SmallButton"); //Forgot your password?

        if (false) {
//            SpanLabel introText = new SpanLabel("This is TodoCatalyst. \nProbably the world's most useful Todo app. \nLet's get you started...\n");
            SpanLabel introText = new SpanLabel("Get TodoCatalyst. \nThe most powerful Todo and time manager. \n");
            introText.setTextBlockAlign(Component.CENTER);

            addComponent(introText);
        }
//        addComponent(backToSignupSignIn);

        container.addComponent(signUp);
        container.addComponent(email);
        container.addComponent(connect);
        container.addComponent(createAccount);
        container.addComponent(createAccountWithRandomPassword);
        container.addComponent(password);
        container.addComponent(createAccountHelp);
        container.addComponent(createAccountLater);
        container.addComponent(createAccountLaterHelp);
        getContentPane().addComponent(BorderLayout.NORTH, BoxLayout.encloseXRight(login));
        container.addComponent(forgottenPassword);

        container.addComponent(backToSignupSignIn);

        //hide everything except the two first buttons to chose Signin or SIgnUp
        email.setHidden(true);
        password.setHidden(true);
        connect.setHidden(true);
        createAccount.setHidden(true);
        createAccountWithRandomPassword.setHidden(true);
        createAccountHelp.setHidden(true);
        forgottenPassword.setHidden(true);
        backToSignupSignIn.setHidden(true);

        backToSignupSignIn.setCommand(Command.createMaterial("Back", Icons.iconBackToPreviousScreen, (e2) -> {
            signUp.setHidden(false);
            createAccountLater.setHidden(false);
            createAccountLaterHelp.setHidden(false);
//            signUp.setUIID("BigButton");
            login.setHidden(false);
//            login.setUIID("BigButton");
            login.setHidden(false);
            connect.setHidden(true);
            createAccount.setHidden(true);
            createAccountWithRandomPassword.setHidden(true);
            createAccountHelp.setHidden(true);
            email.setHidden(true);
            password.setHidden(true);
            backToSignupSignIn.setHidden(true);
            forgottenPassword.setHidden(true);
//            animateHierarchy(ANIMATION_TIME_DEFAULT);
            animateLayout(ANIMATION_TIME_DEFAULT);
        }));

        signUp.setCommand(Command.createMaterial("Sign up", Icons.iconPersonNew, (e2) -> {
            signUp.setHidden(true);
            createAccountLater.setHidden(true);
            createAccountLaterHelp.setHidden(true);
//            signUp.setUIID("BigButtonTitle");
            login.setHidden(true);

            connect.setHidden(true);

            createAccount.setHidden(false);
            createAccountWithRandomPassword.setHidden(false);
            createAccountHelp.setHidden(true);

            email.setHidden(false);
            password.setHidden(true);
            forgottenPassword.setHidden(true);
            backToSignupSignIn.setHidden(false);
//            animateHierarchy(ANIMATION_TIME_DEFAULT);
            animateLayout(ANIMATION_TIME_DEFAULT);
            email.startEditingAsync();
        }));

        login.setCommand(Command.createMaterial("Log in", Icons.iconPerson, (ev) -> { //Start/login**
            signUp.setHidden(true);
            login.setHidden(true);
//            login.setUIID("BigButtonTitle");

            createAccount.setHidden(true);
            createAccountWithRandomPassword.setHidden(true);
            createAccountHelp.setHidden(true);
            createAccountLater.setHidden(true);
            createAccountLaterHelp.setHidden(true);
            connect.setHidden(false);
            email.setHidden(false);
            //TODO!!! cn1 support setEditOnShow(email); //startup editor in email field
            password.setHidden(false);

            forgottenPassword.setHidden(false);
            backToSignupSignIn.setHidden(false);
            if (email.getText().isEmpty()) {
                email.startEditingAsync();
            } else {
                //if email is already filled out (re-login) then jump to password field
                password.startEditingAsync();
            }
            animateHierarchyFade(ANIMATION_TIME_DEFAULT, 0x88);
//            ScreenLogin.this.getContentPane().animateLayout(300);
        }));

        String selectYourPasswordText = "Set my password now"; // (instead of having a random password emailed to me)";
        String selectYourPasswordHelp = "To make signing up easy, you can start with an automatically generated password that is mailed to you. "
                + "You can always change it later";

        createAccount.setCommand(Command.createMaterial("Create my account", Icons.iconPersonNew, (e2) -> {
            String errorMsg;
            String cleanEmail = cleanEmail(email.getText());
            if ((errorMsg = createAccount(cleanEmail)) == null) {
                MyPrefs.setString(MyPrefs.loginEmail, cleanEmail); //store email for future use
                MyPrefs.setBoolean(MyPrefs.loginStoreEmail, true); //store email for future use
                DAO.getInstance().startUp(false);
                new ScreenMain(this).show();
            } else {
                Dialog.show("", errorMsg, "OK", null);
            }
//            ScreenLogin.this.getContentPane().animateLayout(300);
        }));

        //"Use with non-assigned account" "Use with anonymous account" "Use with trial account"
        String trialAccountMessage = "A trial account lets you get started without providing an email. "
                + "If you decide to continue using TodoCatalyst, you can add your email later. "
                + "Be aware that since the account is anonymous, you will not"
                + "be able to log in on other devices and if you log out of TodoCatalyst on this device, your data will be lost.";
//        createAccountLater.setCommand(Command.createMaterial("Sign up laterStart with trial account", Icons.iconPersonAnonymous, (e2) -> {
//        createAccountLater.setCommand(Command.createMaterial("Sign up later", Icons.iconPersonAnonymous, (e2) -> {
        createAccountLater.setCommand(Command.createMaterial("Use now, sign up later", Icons.iconPersonAnonymous, (e2) -> {
            String errorMsg;
            String temporaryRandomLogin = PasswordGenerator.getInstance().generate("Trial-", 10, true, true, false, false); //avoid punctuation during testing
            String temporaryRandomPassword = PasswordGenerator.getInstance().generate("", 10, true, true, false, false);

            if ((errorMsg = createAccount("", temporaryRandomLogin, temporaryRandomPassword)) == null) {
//                MyPrefs.setString(MyPrefs.loginEmail, temporaryRandomLogin); //store email for future use
//                MyPrefs.setBoolean(MyPrefs.loginStoreEmail, true); //store email for future use
                DAO.getInstance().startUp(false);
                new ScreenMain(this).show();
            } else {
                Dialog.show("", errorMsg, "OK", null);
            }
//            ScreenLogin.this.getContentPane().animateLayout(300);
        }));

//        connect.setCommand(Command.create("Connect", Icons.iconPerson, (ev) -> { //Start/login**
        connect.setCommand(Command.createMaterial("Log in", Icons.iconPerson, (ev) -> { //Start/login**
            String errorMsg;
            String cleanEmail = cleanEmail(email.getText());
            if ((errorMsg = loginUser(cleanEmail, null, password.getText())) == null) {
                if (MyPrefs.loginEmail.getString().length() == 0) {
                    MyPrefs.setString(MyPrefs.loginEmail, cleanEmail); //store email for future use
                    MyPrefs.setBoolean(MyPrefs.loginStoreEmail, true); //store email for future use
                }
                DAO.getInstance().startUp(false);
                new ScreenMain(this).show();
            } else {
                Dialog.show("Log in unsuccesful", errorMsg, "OK", null);
//                new Dialog("Log in unsuccesful", errorMsg, "OK", null);
            }
//            ScreenLogin.this.getContentPane().animateLayout(ANIMATION_TIME_DEFAULT);
            container.animateLayout(ANIMATION_TIME_DEFAULT);
        }));

        forgottenPassword.setCommand(Command.create(FORGOT_PASSWORD, null, (ev) -> {
            String cleanEmail = cleanEmail(email.getText());
            String errorMsg = validEmail(cleanEmail);
            if (errorMsg == null) {
                if (Dialog.show("Reset password", "Select OK to receive an email to reset your password", "OK", "Not now")) {
                    try {
                        ParseUser.requestPasswordReset(cleanEmail); //TODO read password - copy Amazon usability
                    } catch (ParseException ex) {
                        Log.e(ex);
                    }
                } else {
                    Dialog.show("", errorMsg, "OK", null);
                }
            } else {
                Dialog.show("Enter your email", "Please enter a correct email like myname@gmail.com", "OK", null);
            }
        }));

        revalidate(); //ensure correct size of all components
//        email.startEditingAsync(); //always start editing email field

    }

//////<editor-fold defaultstate="collapsed" desc="comment">
//////        addButton(res.getImage("news-item-1.jpg"), "Morbi per tincidunt tellus sit of amet eros laoreet.", false, 26, 32);
//////        addButton(res.getImage("news-item-2.jpg"), "Fusce ornare cursus masspretium tortor integer placera.", true, 15, 21);
//////        addButton(res.getImage("news-item-3.jpg"), "Maecenas eu risus blanscelerisque massa non amcorpe.", false, 36, 15);
//////        addButton(res.getImage("news-item-4.jpg"), "Pellentesque non lorem diam. Proin at ex sollicia.", false, 11, 9);
//////        add(emailCont);
//////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
////            }
////        }
//    }
//    private void initAlarmsXXX() {
//        //ALARMS - initialize
//        AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
//    }
//
//    private void initCacheAndStorageXXX() {
//        //            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//        DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//
//    }
//
//    private void initLastActiveTimerXXX() {
//        //ALARMS - initialize
//        //TIMER - was running when app was moved to background?
//        if (!ScreenTimer.getInstance().isTimerActive()) {
//            new ScreenMain().show(); //go directly to main screen if user already has a session
//        } else {
//            if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//                new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//            }
//        }
//    }
//    private void initializeXXX() {
//        if (MyPrefs.loginFirstTimeLogin.getBoolean()) { //very first login
//            MyPrefs.setBoolean(MyPrefs.loginFirstTimeLogin, false);
//            new ScreenLogin(theme).show();
//        } else {
//            int count = 0;
//            ParseUser parseUser = ScreenLogin.getLastUserSessionFromStorage();
//            Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
//            if (parseUser == null) {
//                new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//            } else {
//                ScreenLogin.setDefaultACL(parseUser); //TODO needed??
////                try {
////                    count = query.count();
////                } catch (ParseException ex) {
////                    Log.e(ex);
////                }
//
//                Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//
////            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//
//                //ALARMS - initialize
//                AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
//
//                //TIMER - was running when app was moved to background?
//                if (!ScreenTimer.getInstance().isTimerActive()) {
//                    new ScreenMain().show(); //go directly to main screen if user already has a session
//                } else {
//                    if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//                        new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//                    }
//                }
//            }
//        }
//    }
//    private void updateArrowPosition(Button b, Label arrow) {
//        arrow.getUnselectedStyle().setMargin(LEFT, b.getX() + b.getWidth() / 2 - arrow.getWidth() / 2);
//        arrow.getParent().repaint();
//
//    }
//
//    private void addTab(Tabs swipe, Image img, Label spacer, String text) {
//        int size = Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());
//        if (img.getHeight() < size) {
//            img = img.scaledHeight(size);
//        }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        Label likes = new Label(likesStr);
////        Style heartStyle = new Style(likes.getUnselectedStyle());
////        heartStyle.setFgColor(0xff2d55);
////        FontImage heartImage = FontImage.createMaterial(FontImage.MATERIAL_FAVORITE, heartStyle);
////        likes.setIcon(heartImage);
////        likes.setTextPosition(RIGHT);
//
////        Label comments = new Label(commentsStr);
////        FontImage.setMaterialIcon(comments, FontImage.MATERIAL_CHAT);
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (img.getHeight() > Display.getInstance().getDisplayHeight() / 2) {
//            img = img.scaledHeight(Display.getInstance().getDisplayHeight() / 2);
//        }
//        ScaleImageLabel image = new ScaleImageLabel(img);
//        image.setUIID("Container");
//        image.setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
//        Label overlay = new Label(" ", "ImageOverlay");
//
//        Container page1 = LayeredLayout.encloseIn(image, overlay, BorderLayout.south(BoxLayout.encloseY(new SpanLabel(text, "LargeWhiteText"), spacer)));
//
//        swipe.addTab("", page1);
//    }
//
//    private void addTabOLD(Tabs swipe, Image img, Label spacer, String likesStr, String commentsStr, String text) {
//        int size = Math.min(Display.getInstance().getDisplayWidth(), Display.getInstance().getDisplayHeight());
//        if (img.getHeight() < size) {
//            img = img.scaledHeight(size);
//        }
//        Label likes = new Label(likesStr);
//        Style heartStyle = new Style(likes.getUnselectedStyle());
//        heartStyle.setFgColor(0xff2d55);
//        FontImage heartImage = FontImage.createMaterial(FontImage.MATERIAL_FAVORITE, heartStyle);
//        likes.setIcon(heartImage);
//        likes.setTextPosition(RIGHT);
//
//        Label comments = new Label(commentsStr);
//        FontImage.setMaterialIcon(comments, FontImage.MATERIAL_CHAT);
//        if (img.getHeight() > Display.getInstance().getDisplayHeight() / 2) {
//            img = img.scaledHeight(Display.getInstance().getDisplayHeight() / 2);
//        }
//        ScaleImageLabel image = new ScaleImageLabel(img);
//        image.setUIID("Container");
//        image.setBackgroundType(Style.BACKGROUND_IMAGE_SCALED_FILL);
//        Label overlay = new Label(" ", "ImageOverlay");
//
//        Container page1
//                = LayeredLayout.encloseIn(
//                        image,
//                        overlay,
//                        BorderLayout.south(
//                                BoxLayout.encloseY(
//                                        new SpanLabel(text, "LargeWhiteText"),
//                                        FlowLayout.encloseIn(likes, comments),
//                                        spacer
//                                )
//                        )
//                );
//
//        swipe.addTab("", page1);
//    }
//    private String CURRENT_USER_STORAGE_ID = "parseCurrentUser";
//    private String CURRENT_USER_USER_NAME = "parseUserName";
//    private String CURRENT_USER_USER_EMAIL = "parseUserEmail";
//</editor-fold>
    private final static String CURRENT_USER_SESSION_TOKEN = "parseUserSessionToken";
//    private String CURRENT_USER_PASSWORD = "parseUserPsWd";

    static boolean saveCurrentUserSessionToStorage(String sessionToken) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        HashMap h = new HashMap();
//        h.put(CURRENT_USER_USER_NAME, parseUser.getUsername());
//        h.put(CURRENT_USER_USER_EMAIL, parseUser.getEmail());
//        h.put(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
//        h.put(CURRENT_USER_PASSWORD, parseUser.getUsername());
//        return Storage.getInstance().writeObject(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
//</editor-fold>
        return Storage.getInstance().writeObject(CURRENT_USER_SESSION_TOKEN, sessionToken);
    }

    static String fetchCurrentUserSessionFromStorage() {
        String sessionTokenN = (String) Storage.getInstance().readObject(CURRENT_USER_SESSION_TOKEN);
        return sessionTokenN;
    }

//    private boolean saveCurrentUserToStorage(ParseUser parseUser) {
    private boolean saveCurrentUserSessionToStorage() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        HashMap h = new HashMap();
//        h.put(CURRENT_USER_USER_NAME, parseUser.getUsername());
//        h.put(CURRENT_USER_USER_EMAIL, parseUser.getEmail());
//        h.put(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
//        h.put(CURRENT_USER_PASSWORD, parseUser.getUsername());
//        return Storage.getInstance().writeObject(CURRENT_USER_SESSION_TOKEN, parseUser.getSessionToken());
//</editor-fold>
//        return saveCurrentUserSessionToStorage(parseUser.getSessionToken());
        return saveCurrentUserSessionToStorage(ParseUser.getCurrent().getSessionToken());
    }

    static public ParseUser getLastUserSessionFromStorage() {
//        String sessionTokenN = (String) Storage.getInstance().readObject(CURRENT_USER_SESSION_TOKEN);
        String sessionTokenN = fetchCurrentUserSessionFromStorage();
        Log.p("Retrieved Sessiontoken=" + sessionTokenN != null ? sessionTokenN : "<null>");
        if (sessionTokenN == null || sessionTokenN.equals("")) {
            return null;
        } else {
            try {
                //        ParseUser parseUser = new ParseUser();
//        HashMap h = (HashMap) Storage.getInstance().readObject(CURRENT_USER_STORAGE_ID);
//        return new ParseUser().set(ParseUser) Storage.getInstance().readObject(CURRENT_USER_STORAGE_ID);
//            return ParseUser.fetchBySession((String) Storage.getInstance().readObject(CURRENT_USER_SESSION_TOKEN));
                ParseUser parseUser = ParseUser.fetchBySession(sessionTokenN);
                return parseUser;
            } catch (ParseException ex) {
//                Log.e(ex); //TODO!!!!!: "your session has expired, please log in again"
                return null;
            }
        }
//        return null;
    }

    static public void deleteLastUserSessionFromStorage() {
        Storage.getInstance().deleteStorageFile(CURRENT_USER_SESSION_TOKEN);
        Log.p("Deleted Sessiontoken");
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

    static String validEmail(String email) {
        Constraint checkEmail = RegexConstraint.validEmail();
        if (email == null || email.length() == 0 || !checkEmail.isValid(email)) {
//            Dialog.show("", "Please enter your email to reset your password.", "OK", null);
//            Dialog.show("INFO", errorMsg + "\nPlease enter an email address in the format xxx@yyy.zzz", "OK", null);
//            return false;
            return "Please enter an email address in the format xxx@yyy.zzz";
        }
        return null;
    }

    static String validPassword(String password) {
        if (password == null || password.length() == 0) {
//            return "Password missing, please enter a password and try again";
            return "Password missing. Please enter your password or change it if you forgot it**";
        } else {
            return null;
        }
    }

    private String returnErrorString(int errCode) {
        String errorMsg = "";
        switch (errCode) {
            //http://parseplatform.org/Parse-SDK-dotNET/api/html/T_Parse_ParseException_ErrorCode.htm
            case ParseException.EMAIL_TAKEN:
                //            Dialog.show("ERROR", "Your email already exists (or other error)", "Try again", null);
                errorMsg = "An account already exists for this email";
                break;
//                case ParseException.ACCOUNT_ALREADY_LINKED:
            //Error code indicating that an an account being linked is already linked to another user.
            case ParseException.INVALID_EMAIL_ADDRESS:
            case ParseException.USERNAME_TAKEN: // UsernameTaken	202	Error code indicating that the username has already been taken.
                //EIther the email used as or the randomly generated userId duplicated a previous one
                errorMsg = "An account already exists for this email";
                break;
//                case ParseException.USERNAME_MISSING: //UsernameMissing	200	Error code indicating that the username is missing or empty.
//                case ParseException.PASSWORD_MISSING: //PasswordMissing	201	Error code indicating that the password is missing or empty.                    
            //pb with login email or 
            case ParseException.CLOUD_ERROR:
            case ParseException.CONNECTION_FAILED: // 100	Error code indicating the connection to the Parse servers failed.
                errorMsg = "Cannot connect to the TodoCatalyst server. Please check your network connection.";
                break;
            //internal error, code NNN, please try again (or rather auto-retry)
            case ParseException.OBJECT_NOT_FOUND: //<=> login failed == ObjectNotFound	101	Error code indicating the specified object doesn't exist.
            //shouldn't happen during account creation
            case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP: // 	MustCreateUserThroughSignup	207	Error code indicating that a user can only be created through signup.
            case ParseException.EXCEEDED_QUOTA: // ExceededQuota	140	Error code indicating that an application quota was exceeded. Upgrade to resolve.
//                case ParseException.INCORRECT_TYPE: // Error code indicating that a field was set to an inconsistent type.
            case ParseException.INTERNAL_SERVER_ERROR: // 	1	Error code indicating that something has gone wrong with the server. If you get this error code, it is Parse's fault. Please report the bug to https://parse.com/help.
//                case ParseException.NOT_INITIALIZED: // NotInitialized	109	You must call Parse.initialize before using the Parse library.
            case ParseException.OTHER_CAUSE: //OtherCause	-1	Error code indicating that an unknown error or an error unrelated to Parse occurred.
            default:
                errorMsg = "Unknown problem occurred, please contact TodoCatalyst Support and provide this number: \"" + errCode + "\" and your email";
                break;
            //internal error, please provide your email to be notified when the problem is resolved, or tray again later (or automatically send me an email)
            }
        return errorMsg;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private String createAccountXXX(String validEmail, String validUserId, String password) {
//
//        String errorMsg;
//        boolean emailDefined = (errorMsg = validEmail(validEmail)) == null;
//        if (!emailDefined) {
//            return errorMsg;
//        }
//
//        boolean userIdDefined = validUserId != null && validUserId.length() > 0;
//        if (!userIdDefined) {
//            if (emailDefined) { //TODO check if email is valid here (or before)?
//                validUserId = validEmail;
//            } else {
//                validUserId = PasswordGenerator.getInstance().generateNumbers("Incognito", 8); //"Incognito53976245"
//            }
//        }
//
//        if (password == null || password.length() == 0) {
//            if (validPassword(password) != null) {
//                password = PasswordGenerator.getInstance().generate(12);
//            }
//            try {
//                ParseUser parseUser = ParseUser.create(validUserId, password);
////            assert emailDefined; //should always be true - NO not for Incognito
//                if (emailDefined) {
//                    parseUser.setEmail(validEmail); //
//                }
//                setDefaultACL(parseUser);
//                parseUser.signUp();//perform sign up / account creation
//                saveCurrentUserSessionToStorage();
//                return null;
//            } catch (ParseException ex) {
//                Log.p("***login failed***");
//                Log.p(ex.getMessage());
//                Log.e(ex);
//
//                return returnErrorString(ex.getCode());
//            }
//        }
//        return null;
//    }
//</editor-fold>
    /**
     * create account and sign in
     *
     * @param emptyOrValidEmail
     * @param validUserId if empty, email is used
     * @param password if empty a temporary one is automatically generated
     * @return
     */
    private String createAccount(String emptyOrValidEmail, String userName, String password) {
        ASSERT.that(userName != null && !userName.isEmpty());
        try {
            ParseUser parseUser = ParseUser.create(userName, password);
            if (Config.TEST_STORE_PASSWORD_FOR_USER) {
                parseUser.put("visiblePassword", password); //will this save the password (or rather, will the below signUp()?)?
            }
            if (emptyOrValidEmail != null && !emptyOrValidEmail.isEmpty()) {
                parseUser.setEmail(emptyOrValidEmail);
            }
//            setDefaultACL(parseUser); //NB cannot set ACL for user with a null id
            parseUser.signUp(); //perform sign up / account creation
            setDefaultACL(parseUser); //NB cannot set ACL for user with a null id
            saveCurrentUserSessionToStorage();
//            Message msg = new Message();
            Display.getInstance().sendMessage(new String[]{emptyOrValidEmail}, "Your TodoCatalyst account login info (" + emptyOrValidEmail + ")",
                    new Message("\n\nTodoCatalyst login/email: " + emptyOrValidEmail + "\n\nYour auto-generated password (please change in TodoCatalyst app): " + password));
            //No cache/memory setup needed for new account
            return null;
        } catch (ParseException ex) {
            Log.p("***account creation failed***");
            Log.p(ex.getMessage());
            Log.e(ex);
            return returnErrorString(ex.getCode());
        }
    }

    private String createAccount(String validEmail) {

        String errorMsg = validEmail(validEmail);
        if (errorMsg != null) {
            return errorMsg;
        }
        String password = PasswordGenerator.getInstance().generate(12);
        if (Config.TEST) {
            if (Config.TEST_STORE_PASSWORD_FOR_USER) {
                password = PasswordGenerator.getInstance().generate("", 6, true, true, true, false); //avoid punctuation during testing
            }
            if (validEmail.equals("thomas.hjelm@email.com")) {
                password = "ItsThomas";
            }
        }

        String result = createAccount(validEmail, validEmail, password);
        return result;
    }

    private String createTempAccount() {
        String randomUserName = PasswordGenerator.getInstance().generate("temp-", 6, true, false, true, false); //6=>35^ = 1 838 265 625
        String password = PasswordGenerator.getInstance().generate(8);
        if (Config.TEST) {
            if (Config.TEST_STORE_PASSWORD_FOR_USER) {
                password = PasswordGenerator.getInstance().generate("", 6, true, true, true, false); //avoid punctuation during testing
            }
        }
        String result = createAccount(null, randomUserName, password);
        return result;
    }

    /**
     * returns null if successful or string describing the error and corrective
     * action to take (for end-user display) if not
     *
     * @param validEmail
     * @param validUserId
     * @param password
     * @return null if success, otherwise String with error message for end user
     */
    private String loginUser(String validEmail, String validUserId, String password) {

//        boolean passwordDefined = (password == null || password.length() == 0);
        String errorMsg;

        if (Config.TEST && validEmail.equals("*")) {
            validEmail = "thomas.hjelm@email.com";
            password = "ItsThomas";
        }
        if ((errorMsg = validPassword(password)) != null) {
            return errorMsg;
        }

        boolean emailDefined; // = validEmail != null && validEmail.length() > 0;
        if ((errorMsg = validEmail(validEmail)) != null) {
            return errorMsg;
        } else {
            emailDefined = true;
        }

        boolean userIdDefined = validUserId != null && validUserId.length() > 0;
        if (!userIdDefined && emailDefined) { //TODO check if email is valid here (or before)?
            validUserId = validEmail;
        }

        try {
            ParseUser parseUser = ParseUser.create(validUserId, password);
            parseUser.login();//perform sign up / account creation
//            setDefaultACL(parseUser); //TODO shoudn't be necessary, only at creation?!
            saveCurrentUserSessionToStorage();
//            startUp(false); //load existing data in foreground
            return null;
        } catch (ParseException ex) {
//<editor-fold defaultstate="collapsed" desc="Process error message">
            Log.p("***login failed***");
            Log.p(ex.getMessage());
            Log.e(ex);

            int errCode = ex.getCode();
            switch (errCode) {
                //http://parseplatform.org/Parse-SDK-dotNET/api/html/T_Parse_ParseException_ErrorCode.htm
                case ParseException.OBJECT_NOT_FOUND: //<=> login failed == ObjectNotFound	101	Error code indicating the specified object doesn't exist.
                case ParseException.EMAIL_TAKEN:
//                    errorMsg = "Your email or password are not correct. Please check and try again (or reset your password if you forgot it)";
                    errorMsg = "Your email or password are not correct or do not correspond to an existing account. \n¶nPlease try again. \n\nIf you forgot your password, you can reset it using [" + FORGOT_PASSWORD + "] below.";
                    break;
//                case ParseException.EMAIL_TAKEN:
//                    //            Dialog.show("ERROR", "Your email already exists (or other error)", "Try again", null);
//                    errorMsg = "An account already exists for this email. If it is yours, please log in. ";
//                    break;
//                case ParseException.ACCOUNT_ALREADY_LINKED:
                //Error code indicating that an an account being linked is already linked to another user.
                case ParseException.INVALID_EMAIL_ADDRESS:
                    //EIther the email used as or the randomly generated userId duplicated a previous one
                    errorMsg = "Something seems to be wrong with your email, please correct and try again";
                    break;
//                case ParseException.USERNAME_MISSING: //UsernameMissing	200	Error code indicating that the username is missing or empty.
//                case ParseException.PASSWORD_MISSING: //PasswordMissing	201	Error code indicating that the password is missing or empty.
                //pb with login email or
                case ParseException.CLOUD_ERROR:
                case ParseException.CONNECTION_FAILED: // 100	Error code indicating the connection to the Parse servers failed.
                    errorMsg = "Cannot connect to the TodoCatalyst server. Please check your network connection.";
                    break;
                //internal error, code NNN, please try again (or rather auto-retry)
                //internal error, please provide your email to be notified when the problem is resolved, or tray again later (or automatically send me an email)
                case ParseException.PASSWORD_MISSING: // 	PasswordMissing	201	Error code indicating that the password is missing or empty.
                //shoulnd't happen since we check password before passing it
                case ParseException.USERNAME_TAKEN: // UsernameTaken	202	Error code indicating that the username has already been taken.
                case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP: // 	MustCreateUserThroughSignup	207	Error code indicating that a user can only be created through signup.
                case ParseException.EXCEEDED_QUOTA: // ExceededQuota	140	Error code indicating that an application quota was exceeded. Upgrade to resolve.
//                case ParseException.INCORRECT_TYPE: // Error code indicating that a field was set to an inconsistent type.
                case ParseException.INTERNAL_SERVER_ERROR: // 	1	Error code indicating that something has gone wrong with the server. If you get this error code, it is Parse's fault. Please report the bug to https://parse.com/help.
//                case ParseException.NOT_INITIALIZED: // NotInitialized	109	You must call Parse.initialize before using the Parse library.
                case ParseException.OTHER_CAUSE: //OtherCause	-1	Error code indicating that an unknown error or an error unrelated to Parse occurred.
                default:
                    errorMsg = "Unknown problem occurred, please contact TodoCatalyst Support and provide this number: \"" + errCode + "\" and your email";
                    break;
            }
//            Dialog.show("ERROR", err, "OK", null);
            return errorMsg;
//</editor-fold>
        }
    }

//    private void test() {
//        try {
//            if (false) {
//                ParseUser parseUser = ParseUser.getCurrent();
//                parseUser.logout();
//            }
//            ScreenLogin.logoutCurrentUser();
//            DAO.getInstance().clearAllCacheAndStorage(false);
//            showPreviousScreen(false); //back to Main screen
//            if (parentForm.parentForm != null) {
//                if (!Config.TEST && parentForm.parentForm instanceof ScreenLogin) { //keep mail+password during testing
//                    if (((ScreenLogin) parentForm.parentForm).email != null) {
//                        ((ScreenLogin) parentForm.parentForm).email.setText("");
//                    }
//                    if (((ScreenLogin) parentForm.parentForm).password != null) {
//                        ((ScreenLogin) parentForm.parentForm).password.setText("");
//                    }
//                }
//                parentForm.showPreviousScreen(false); //back out to Login screen
//            }
//        } catch (ParseException ex) {
//            Log.p(ex.getMessage());
//            Log.e(ex);
//        }
//
//    }
    public static String parseExceptionToString(int errCode) {
        String errorMsg;
        switch (errCode) {
            //http://parseplatform.org/Parse-SDK-dotNET/api/html/T_Parse_ParseException_ErrorCode.htm
            //pb with login email or 
            case ParseException.CLOUD_ERROR:
            case ParseException.CONNECTION_FAILED: // 100	Error code indicating the connection to the Parse servers failed.
                errorMsg = "Cannot connect to the TodoCatalyst server. Please check your network connection.";
                break;
            case ParseException.OBJECT_NOT_FOUND: //<=> login failed == ObjectNotFound	101	Error code indicating the specified object doesn't exist.
//                    errorMsg = "Your email or password are not correct. Please check and try again (or reset your password if you forgot it)";
//                    break;
            case ParseException.EMAIL_TAKEN:
            //            Dialog.show("ERROR", "Your email already exists (or other error)", "Try again", null);
//                    errorMsg = "An account already exists for this email";
//                    break;
//                case ParseException.ACCOUNT_ALREADY_LINKED:
            //Error code indicating that an an account being linked is already linked to another user.
            case ParseException.INVALID_EMAIL_ADDRESS:
            //EIther the email used as or the randomly generated userId duplicated a previous one
//                    errorMsg = "Something seems to be wrong with your email, please correct and try again";
//                    break;
//                case ParseException.USERNAME_MISSING: //UsernameMissing	200	Error code indicating that the username is missing or empty.
//                case ParseException.PASSWORD_MISSING: //PasswordMissing	201	Error code indicating that the password is missing or empty.                    
            //internal error, code NNN, please try again (or rather auto-retry)
            //internal error, please provide your email to be notified when the problem is resolved, or tray again later (or automatically send me an email)
            case ParseException.PASSWORD_MISSING: // 	PasswordMissing	201	Error code indicating that the password is missing or empty.
            //shoulnd't happen since we check password before passing it
            case ParseException.USERNAME_TAKEN: // UsernameTaken	202	Error code indicating that the username has already been taken.
            case ParseException.MUST_CREATE_USER_THROUGH_SIGNUP: // 	MustCreateUserThroughSignup	207	Error code indicating that a user can only be created through signup.
            case ParseException.EXCEEDED_QUOTA: // ExceededQuota	140	Error code indicating that an application quota was exceeded. Upgrade to resolve.
//                case ParseException.INCORRECT_TYPE: // Error code indicating that a field was set to an inconsistent type.
            case ParseException.INTERNAL_SERVER_ERROR: // 	1	Error code indicating that something has gone wrong with the server. If you get this error code, it is Parse's fault. Please report the bug to https://parse.com/help.
//                case ParseException.NOT_INITIALIZED: // NotInitialized	109	You must call Parse.initialize before using the Parse library.
            case ParseException.OTHER_CAUSE: //OtherCause	-1	Error code indicating that an unknown error or an error unrelated to Parse occurred.
            default:
                errorMsg = "Unknown problem occurred, please contact TodoCatalyst Support and provide this number: \"" + errCode + "\" and your email";
                break;
        }
        return errorMsg;
    }

    public static String logoutCurrentUser(boolean deleteAllData) {
        ParseUser currentUser = ParseUser.getCurrent();
        if (currentUser != null) {
            try {
                currentUser.logout();
//                if (false) {
//                    getLastUserSessionFromStorage();
//                }
//                deleteLastUserSessionFromStorage();
            } catch (ParseException ex) {
                Log.e(ex);
                int errCode = ex.getCode();
                String errorMsg = parseExceptionToString(errCode);
                return errorMsg;
            }
            DAO.getInstance().clearAllCacheAndStorage(false);
            return null;
        } else {
            return "Unknown problem occurred, logout did not succeed (currentUser==null), please contact TodoCatalyst Support with error code 999**";
        }
    }

    @Override
    public void refreshAfterEdit() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        super.refreshAfterEdit();
    }

}
