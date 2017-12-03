/*
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
import com.codename1.ui.validation.Constraint;
import com.codename1.ui.validation.RegexConstraint;
import com.parse4cn1.ParseACL;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.ParseUser;

/**
 * The newsfeed form
 *
 * @author Shai Almog
 */
//public class ScreenLogin2 extends BaseForm {
public class ScreenLogin2 extends MyForm {
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
    public ScreenLogin2() {
        //TODO change login screen to show 2 text/ad screens, swipe them left to get to login fields (like ?? app)
        super("Login", null, () -> {
        });
    }

    public void go() {
        go(false);
    }

    public void go(boolean forceLaunchForTest) {
        ParseUser parseUser = getLastUserSessionFromStorage();
        Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));

        if (!forceLaunchForTest && parseUser != null) { //already logged in
            int count = -1;
            setDefaultACL(parseUser); //TODO needed??
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
            try {
                count = query.count();
            } catch (ParseException ex) {
                Log.e(ex);
            }
            Log.p("Count of Item in Parse = " + count, Log.DEBUG);
            //            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
            DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and refresh as data comes in

            //ALARMS - initialize
            AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background

            //TIMER - was running when app was moved to background? - now done with ReplayCommand
//            if (!ScreenTimer.getInstance().isTimerActive()) {
//                new ScreenMain().show(); //go directly to main screen if user already has a session
//            } else {
//                if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//                }
//            }
                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead

        } else {
            setupLoginScreen();
//                new ScreenLogin2(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//            new ScreenLogin2(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
            show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
        }

    }

    private void setupLoginScreen() {
        //TODO intro quiz: you tired of running into limitations in (miss features) in other ToDO apps, your tasks tend to pile up endlessly?, it is important for to be fully control/appear professional?
//        super("Welcome to TodoCatalyst", BoxLayout.y());
        setTitle("Welcome to TodoCatalyst");
        setLayout(BoxLayout.y());

//        getTitleArea().setUIID("Container");
//        setTitle("TodoCatalyst");
//        getContentPane().setScrollVisible(false);
//        getContentPane().setAlwaysTensile(false); //only scroll if needed(???)
        //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
//        getToolbar().setUIID("Container");
//        getToolbar().hideToolbar();
        TextField email = new TextField("", "Email", 20, TextArea.EMAILADDR);
        if (MyPrefs.loginStoreEmail.getBoolean()) {
            email.setText(MyPrefs.loginEmail.getString());
        }

        TextField password = new TextField("", "Password", 20, TextArea.PASSWORD);

//        BorderLayout b1 = new BorderLayout();
//        b1.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
        Button signUp = new Button();
        Button createAccount = new Button();
        Button connect = new Button();
        Button login = new Button();
        Button backToSignupSignIn = new Button();
//        Button forgottenPassword = new Button();
        Button forgottenPassword = new Button(""); //Forgot your password?

        SpanLabel introText = new SpanLabel("This is TodoCatalyst. \nProbably the world's most useful Todo app. \nLet's get you started...\n");
        introText.setTextBlockAlign(Component.CENTER);

        addComponent(introText);
        addComponent(signUp);
        addComponent(email);
        addComponent(password);
        addComponent(connect);
        addComponent(createAccount);
        addComponent(login);
        addComponent(forgottenPassword);
        addComponent(backToSignupSignIn);
        revalidate(); //ensure correct size of all components

        //hide everything except the two first buttons to chose Signin or SIgnUp
        email.setHidden(true);
        password.setHidden(true);
        connect.setHidden(true);
        createAccount.setHidden(true);
        forgottenPassword.setHidden(true);
        backToSignupSignIn.setHidden(true);

        backToSignupSignIn.setCommand(Command.create("Back", Icons.iconBackToPrevFormToolbarStyle, (e2) -> {
            signUp.setHidden(false);
            login.setHidden(false);

            connect.setHidden(true);
            createAccount.setHidden(true);
            email.setHidden(true);
            password.setHidden(true);
            backToSignupSignIn.setHidden(true);
            forgottenPassword.setHidden(true);
            animateHierarchy(300);
        }));

        signUp.setCommand(Command.create("Sign up Free", Icons.iconPersonNew, (e2) -> {
            signUp.setHidden(true);
            login.setHidden(true);

            connect.setHidden(true);
            createAccount.setHidden(false);
            email.setHidden(false);
            password.setHidden(true);
            forgottenPassword.setHidden(true);
            backToSignupSignIn.setHidden(false);
            animateHierarchy(300);
        }));

        login.setCommand(Command.create("Log in", Icons.iconPerson, (ev) -> { //Start/login**
            signUp.setHidden(true);
            login.setHidden(true);

            createAccount.setHidden(true);
            connect.setHidden(false);
            email.setHidden(false);
            password.setHidden(false);
            forgottenPassword.setHidden(false);
            backToSignupSignIn.setHidden(false);
            animateHierarchy(300);
//            ScreenLogin2.this.getContentPane().animateLayout(300);
        }));

        createAccount.setCommand(Command.create("Create my account", Icons.iconPersonNew, (e2) -> {
            String errorMsg;
            if ((errorMsg = createAccount(email.getText())) == null) {
                MyPrefs.setString(MyPrefs.loginEmail, email.getText()); //store email for future use
                MyPrefs.setBoolean(MyPrefs.loginStoreEmail, true); //store email for future use
                new ScreenMain().show();
            } else {
                Dialog.show("", errorMsg, "OK", null);
            }
//            ScreenLogin2.this.getContentPane().animateLayout(300);
        }));

        connect.setCommand(Command.create("Connect", Icons.iconPerson, (ev) -> { //Start/login**
            String errorMsg;
            if ((errorMsg = loginUser(email.getText(), null, password.getText())) == null) {
                if (MyPrefs.loginEmail.getString().length() == 0) {
                    MyPrefs.setString(MyPrefs.loginEmail, email.getText()); //store email for future use
                    MyPrefs.setBoolean(MyPrefs.loginStoreEmail, true); //store email for future use
                }
                new ScreenMain().show();
            } else {
                Dialog.show("", errorMsg, "OK", null);
            }
            ScreenLogin2.this.getContentPane().animateLayout(300);
        }));

        forgottenPassword.setCommand(Command.create("Forgot password", null, (ev) -> {
            String errorMsg = validEmail(email.getText());
            if (errorMsg == null) {
                if (Dialog.show("Reset password", "Select OK to receive an email to reset your password", "OK", "Not now")) {
                    try {
                        ParseUser.requestPasswordReset(email.getText()); //TODO read password - copy Amazon usability
                    } catch (ParseException ex) {
                        Log.e(ex);
                    }
                } else {
                    Dialog.show("", errorMsg, "OK", null);
                }
            } else {
                Dialog.show("Incorrect email", "Please enter a correct email like name@domain.xx", "OK", null);
            }
        }));

    }

//    public ScreenLogin2(int xxx, Resources res) {
////        if (MyPrefs.loginFirstTimeLogin.getBoolean()) { //very first login
////            MyPrefs.setBoolean(MyPrefs.loginFirstTimeLogin, false);
////
////            ParseUser parseUser = getLastUserSessionFromStorage();
////            Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
////            if (parseUser != null) { //already logged in
////                setDefaultACL(parseUser); //TODO needed??
//////                try {
//////                    count = query.count();
//////                } catch (ParseException ex) {
//////                    Log.e(ex);
//////                }
//////            Log.p("Count of Item in Parse = " + count, Log.DEBUG);
////            } else {
//////                new ScreenLogin2(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
////                new ScreenLogin2(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
////
////                //TODO intro quiz: you tired of running into limitations in (miss features) in other ToDO apps, your tasks tend to pile up endlessly?, it is important for to be fully control/appear professional?
//////        super("Welcome to TodoCatalyst", BoxLayout.y());
////                super("", BoxLayout.y());
//////        setTitleComponent(null);
////                if (false) {
////                    Toolbar tb = new Toolbar(true);
////                    setToolbar(tb);
////                    tb.addSearchCommand(e -> {
////                    });
////                }
////                getTitleArea().setUIID("Container");
//////        setTitle("TodoCatalyst");
////                getContentPane().setScrollVisible(false);
////                getContentPane().setAlwaysTensile(false); //only scroll if needed(???)
////
////                //hide titlebar: http://stackoverflow.com/questions/42871223/how-do-i-hide-get-rid-the-title-bar-on-a-form-codename-one
////                getToolbar().setUIID("Container");
////                getToolbar().hideToolbar();
////
//////        super.addSideMenu(res);
////                Tabs swipe = new Tabs();
////
////                Label spacer1 = new Label();
////                Label spacer2 = new Label();
////                Label spacer3 = new Label();
////                Label spacer4 = new Label();
//////        addTab(swipe, res.getImage("news-item.jpg"), spacer1, "15 Likes  ", "85 Comments", "Integer ut placerat purued non dignissim neque. ");
//////        addTab(swipe, res.getImage("dog.jpg"), spacer2, "100 Likes  ", "66 Comments", "Dogs are cute: story at 11");
//////        addTab(swipe, res.getImage("todocatpng.png"), spacer1, Format.f("Integer ut placerat purued non dignissim neque. "));
////                Image background = res.getImage("firewater.jpg"); //TODO: attribution: http://all-free-download.com/free-vector/download/fire-and-water-swirl_311642.html
//////        addTab(swipe, res.getImage("todocatpng.png"), spacer1, Format.f(welcome1));
//////        addTab(swipe, res.getImage("todocat.jpg"), spacer2, Format.f(welcome2));
//////        addTab(swipe, res.getImage("todocat3.jpg"), spacer3, Format.f(welcome3));
//////        addTab(swipe, res.getImage("todocat3.jpg"), spacer4, Format.f(welcome4));
////                addTab(swipe, background, spacer1, Format.f(welcome1));
////                addTab(swipe, background, spacer2, Format.f(welcome2));
////                addTab(swipe, background, spacer3, Format.f(welcome3));
////                addTab(swipe, background, spacer4, Format.f(welcome4));
////
////                swipe.setUIID("Container");
////                swipe.getContentPane().setUIID("Container");
////                swipe.hideTabs();
////
////                ButtonGroup bg = new ButtonGroup();
////                int size = Display.getInstance().convertToPixels(1);
////                Image unselectedWalkthru = Image.createImage(size, size, 0);
////                Graphics g = unselectedWalkthru.getGraphics();
////                g.setColor(0xffffff);
////                g.setAlpha(100);
////                g.setAntiAliased(true);
////                g.fillArc(0, 0, size, size, 0, 360);
////                Image selectedWalkthru = Image.createImage(size, size, 0);
////                g = selectedWalkthru.getGraphics();
////                g.setColor(0xffffff);
////                g.setAntiAliased(true);
////                g.fillArc(0, 0, size, size, 0, 360);
////                RadioButton[] rbs = new RadioButton[swipe.getTabCount()];
////                FlowLayout flow = new FlowLayout(CENTER);
////                flow.setValign(BOTTOM);
////                Container radioContainer = new Container(flow);
////                for (int iter = 0; iter < rbs.length; iter++) {
////                    rbs[iter] = RadioButton.createToggle(unselectedWalkthru, bg);
////                    rbs[iter].setPressedIcon(selectedWalkthru);
////                    rbs[iter].setUIID("Label");
////                    radioContainer.add(rbs[iter]);
////                }
////
////                rbs[0].setSelected(true);
////                swipe.addSelectionListener((i, ii) -> {
////                    if (!rbs[ii].isSelected()) {
////                        rbs[ii].setSelected(true);
////                    }
////                });
////
////                Component.setSameSize(radioContainer, spacer1, spacer2, spacer3);
////
//////        SpanLabel newUserInfo = new SpanLabel("Please enter your email to start using [TODOCATALYST]. You will receive a confirmation email that also lets you set your password.");
////                SpanLabel newUserInfo = new SpanLabel("Enter your email to sign up and receive an email to validate your account and set your password.");
////                newUserInfo.setHidden(false);
////
//////        SpanLabel incognitoWarning = new SpanLabel("As an incognito user, you cannot login from other devices. If you lose this device or log out** your data will be lost. You can register anytime by entering your email in [SETTINGS].");
////                SpanLabel incognitoWarning = new SpanLabel(Format.f("Incognito users cannot login from other devices. If you lose this device your data will be lost. You can register anytime by entering your email in [SETTINGS]."));
////                incognitoWarning.setHidden(true);
////
////                if (false) {
////                    Label emailLabel = new Label("Email");
////                    FontImage.setMaterialIcon(emailLabel, FontImage.MATERIAL_EMAIL);
////                    Label passwordLabel = new Label("Password");
////                    FontImage.setMaterialIcon(passwordLabel, FontImage.MATERIAL_LOCK);
////                }
////
////                TextField email = new TextField("", "Email", 20, TextArea.EMAILADDR);
////                if (MyPrefs.loginStoreEmail.getBoolean()) {
////                    email.setText(MyPrefs.loginEmail.getString());
////                }
////
////                MyOnOffSwitch keepEmail = new MyOnOffSwitch(null, () -> {
////                    return MyPrefs.loginStoreEmail.getBoolean();
////                }, (b) -> {
////                    MyPrefs.setBoolean(MyPrefs.loginStoreEmail, b);
////                });
////                Container storeEmailCont = BorderLayout.centerEastWest(null, keepEmail, new Label("Keep my email for next time"));
////
////                TextField password = new TextField("", "Password", 20, TextArea.PASSWORD);
////
//////        MyOnOffSwitch stayLoggiedIn = new MyOnOffSwitch(null, () -> {
//////            return MyPrefs.loginStayLoggedIn.getBoolean();
//////        }, (b) -> {
//////            MyPrefs.setBoolean(MyPrefs.loginStayLoggedIn, b);
//////        });
////                BorderLayout b1 = new BorderLayout();
////                b1.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
////                Container emailCont = new Container(b1);
//////        emailCont.add(BorderLayout.NORTH, emailLabel);
////                emailCont.add(BorderLayout.EAST, email);
////
////                b1 = new BorderLayout();
////                b1.defineLandscapeSwap(BorderLayout.NORTH, BorderLayout.WEST);
////                Container passwordCont = new Container(b1);
//////        passwordCont.add(BorderLayout.NORTH, passwordLabel);
////                passwordCont.add(BorderLayout.EAST, password);
//////        passwordCont.setHidden(true);
////
////                SpanButton forgottenPassword = new SpanButton(""); //Forgot your password?
//////        forgottenPassword.setUIID("ForgottenPassword");
//////        forgottenPassword.setUIID("Button");
////                forgottenPassword.setHidden(true);
////
////                Button startButton = new Button("Start");
////                FontImage.setMaterialIcon(startButton, FontImage.MATERIAL_ARROW_FORWARD);
////
////                ActionListener newUserActionListerner;
////                ButtonGroup barGroup = new ButtonGroup();
////                RadioButton newUser = RadioButton.createToggle("New user", barGroup);
////                newUser.setUIID("SelectBar");
////                newUser.addActionListener(newUserActionListerner = (e) -> {
////                    emailCont.setHidden(false);
////                    storeEmailCont.setHidden(false);
////                    passwordCont.setHidden(true);
//////            startButton.setText("Start");
////                    newUserInfo.setHidden(false);
////                    incognitoWarning.setHidden(true);
////                    forgottenPassword.setHidden(true);
////                    startButton.setCommand(Command.create("Start/new user**", selectedWalkthru, (ev) -> {
////                        String errorMsg;
////                        if ((errorMsg = createAccount(email.getText(), null, null)) == null) {
////                            MyPrefs.setBoolean(MyPrefs.loginStoreEmail, keepEmail.isValue());
////                            if (keepEmail.isValue()) {
////                                MyPrefs.setString(MyPrefs.loginEmail, email.getText());
////                            }
////                            MyPrefs.setBoolean(MyPrefs.loginIncognitoMode, false);
////                            new ScreenMain().show();
////                        } else {
////                            Dialog.show("", errorMsg, "OK", null);
////                        }
////                    }));
//////            ScreenLogin2.this.animateHierarchy(300);
//////            ScreenLogin2.this.getContentPane().animateHierarchy(300);
////                    ScreenLogin2.this.getContentPane().animateLayout(300);
////                });
////
////                RadioButton login = RadioButton.createToggle("Login", barGroup);
////                login.setUIID("SelectBar");
////                login.addActionListener((e) -> {
////                    emailCont.setHidden(false);
////                    storeEmailCont.setHidden(false);
////                    passwordCont.setHidden(false);
//////            startButton.setText("Log in");
////                    newUserInfo.setHidden(true);
////                    incognitoWarning.setHidden(true);
////                    forgottenPassword.setHidden(false);
////                    startButton.setCommand(Command.create("Log in", selectedWalkthru, (ev) -> { //Start/login**
////                        String errorMsg;
////                        if ((errorMsg = loginUser(email.getText(), null, password.getText())) == null) {
////                            MyPrefs.setBoolean(MyPrefs.loginStoreEmail, keepEmail.isValue());
////                            if (keepEmail.isValue()) {
////                                MyPrefs.setString(MyPrefs.loginEmail, email.getText());
////                            }
////                            MyPrefs.setBoolean(MyPrefs.loginIncognitoMode, false);
////                            new ScreenMain().show();
////                        } else {
////                            Dialog.show("", errorMsg, "OK", null);
////                        }
////                        ScreenLogin2.this.getContentPane().animateLayout(300);
////                    }));
////                });
////
////                ActionListener incognitoActionListerner;
////                RadioButton incognito = RadioButton.createToggle("Incognito", barGroup);
////                incognito.setUIID("SelectBar");
////                incognito.addActionListener(incognitoActionListerner = (e) -> {
////                    emailCont.setHidden(true);
////                    storeEmailCont.setHidden(true);
////                    passwordCont.setHidden(true);
//////            startButton.setText("Start");
////                    newUserInfo.setHidden(true);
////                    incognitoWarning.setHidden(false);
////                    forgottenPassword.setHidden(true);
////                    startButton.setCommand(Command.create("Start/incognito", selectedWalkthru, (ev) -> {
////                        String errorMsg;
////                        if ((errorMsg = createAccount(null, null, null)) == null) {
////                            MyPrefs.setBoolean(MyPrefs.loginIncognitoMode, true);
////                            new ScreenMain().show();
////                        } else {
////                            Dialog.show("", errorMsg, "OK", null);
////                        }
////                    }));
////                    ScreenLogin2.this.getContentPane().animateLayout(300);
////                });
////
//////        forgottenPassword.setCommand(Command.create("Forgot you password?", selectedWalkthru, (ev) -> {
////                forgottenPassword.setCommand(Command.create("Forgot password", selectedWalkthru, (ev) -> {
////                    String errorMsg = validEmail(email.getText());
//////            if (validEmail(email.getText(), "Enter your email to reset your password.")
////                    if (errorMsg == null) {
////                        if (Dialog.show("", "Reset your password? You will receive an email with a link to change your password.", "OK", "Not now")) {
////                            try {
////                                ParseUser.requestPasswordReset(email.getText()); //TODO read password - copy Amazon usability
////                            } catch (ParseException ex) {
////                                Log.e(ex);
////                            }
////                        } else {
//////                    Dialog.show("", "Reset your password? You will receive an email with a link to change your password.", "OK", "Not now")) {
////                            Dialog.show("", errorMsg, "OK", null);
////                        }
////                    }
////                }));
//////        RadioButton myFavorite = RadioButton.createToggle("My Favorites", barGroup);
//////        myFavorite.setUIID("SelectBar");
////
////                add(LayeredLayout.encloseIn(swipe, radioContainer));
////
////                Label arrow = new Label(res.getImage("news-tab-down-arrow.png"), "Container");
////                add(LayeredLayout.encloseIn(
////                        //                GridLayout.encloseIn(4, all, featured, popular, myFavorite),
////                        GridLayout.encloseIn(3, newUser, login, incognito),
////                        FlowLayout.encloseBottom(arrow)
////                ));
////
////                add(newUserInfo);
////                add(incognitoWarning);
////                add(emailCont);
////                add(storeEmailCont);
////                add(passwordCont);
////                add(forgottenPassword);
////                add(startButton);
////
////                newUser.setSelected(true);
////                newUserActionListerner.actionPerformed(null); //trigger the selection of fields
//////        newUser.fire
////                arrow.setVisible(false);
////                addShowListener(e -> {
////                    arrow.setVisible(true);
////                    updateArrowPosition(newUser, arrow);
////                });
////                bindButtonSelection(newUser, arrow);
////                bindButtonSelection(login, arrow);
////                bindButtonSelection(incognito, arrow);
//////        bindButtonSelection(myFavorite, arrow);
////
////                // special case for rotation
////                addOrientationListener(e -> {
////                    updateArrowPosition(barGroup.getRadioButton(barGroup.getSelectedIndex()), arrow);
////                });
////
//////<editor-fold defaultstate="collapsed" desc="comment">
//////        addButton(res.getImage("news-item-1.jpg"), "Morbi per tincidunt tellus sit of amet eros laoreet.", false, 26, 32);
//////        addButton(res.getImage("news-item-2.jpg"), "Fusce ornare cursus masspretium tortor integer placera.", true, 15, 21);
//////        addButton(res.getImage("news-item-3.jpg"), "Maecenas eu risus blanscelerisque massa non amcorpe.", false, 36, 15);
//////        addButton(res.getImage("news-item-4.jpg"), "Pellentesque non lorem diam. Proin at ex sollicia.", false, 11, 9);
//////        add(emailCont);
//////</editor-fold>
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
//        DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and refresh as data comes in
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
//            new ScreenLogin2(theme).show();
//        } else {
//            int count = 0;
//            ParseUser parseUser = ScreenLogin2.getLastUserSessionFromStorage();
//            Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
//            if (parseUser == null) {
//                new ScreenLogin2(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//            } else {
//                ScreenLogin2.setDefaultACL(parseUser); //TODO needed??
////                try {
////                    count = query.count();
////                } catch (ParseException ex) {
////                    Log.e(ex);
////                }
//
//                Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//
////            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and refresh as data comes in
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
    private static String CURRENT_USER_SESSION_TOKEN = "parseUserSessionToken";
//    private String CURRENT_USER_PASSWORD = "parseUserPsWd";

    private boolean saveCurrentUserSessionToStorage(String sessionToken) {
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

    private String validEmail(String email) {
        Constraint checkEmail = RegexConstraint.validEmail();
        if (email == null || email.length() == 0 || !checkEmail.isValid(email)) {
//            Dialog.show("", "Please enter your email to reset your password.", "OK", null);
//            Dialog.show("INFO", errorMsg + "\nPlease enter an email address in the format xxx@yyy.zzz", "OK", null);
//            return false;
            return "Please enter an email address in the format xxx@yyy.zzz";
        }
        return null;
    }

    private String validPassword(String password) {
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

    /**
     * create account and sign in
     *
     * @param validEmail
     * @param validUserId if empty, email is used
     * @param password if empty a temporary one is automatically generated
     * @return
     */
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
    private String createAccount(String validEmail) {

        String errorMsg = validEmail(validEmail);
        if (errorMsg != null) {
            return errorMsg;
        }

        String password = PasswordGenerator.getInstance().generate(12);
        try {
            ParseUser parseUser = ParseUser.create(validEmail, password);
            parseUser.setEmail(validEmail);
            setDefaultACL(parseUser);
            parseUser.signUp(); //perform sign up / account creation
            saveCurrentUserSessionToStorage();
            //No cache/memory setup needed for new account
            return null;
        } catch (ParseException ex) {
            Log.p("***login failed***");
            Log.p(ex.getMessage());
            Log.e(ex);
            return returnErrorString(ex.getCode());
        }
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
            return null;
        } catch (ParseException ex) {
            Log.p("***login failed***");
            Log.p(ex.getMessage());
            Log.e(ex);

            int errCode = ex.getCode();
            switch (errCode) {
                //http://parseplatform.org/Parse-SDK-dotNET/api/html/T_Parse_ParseException_ErrorCode.htm
                case ParseException.OBJECT_NOT_FOUND: //<=> login failed == ObjectNotFound	101	Error code indicating the specified object doesn't exist.
                    errorMsg = "Your email or password are not correct. Please check and try again (or reset your password if you forgot it)";
                    break;
                case ParseException.EMAIL_TAKEN:
                    //            Dialog.show("ERROR", "Your email already exists (or other error)", "Try again", null);
                    errorMsg = "An account already exists for this email";
                    break;
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
        }
    }

    public static String logoutCurrentUser() {
        ParseUser currentUser = ParseUser.getCurrent();
        if (currentUser != null) {
            try {
                currentUser.logout();
                getLastUserSessionFromStorage();
                deleteLastUserSessionFromStorage();
                return null;
            } catch (ParseException ex) {
                Log.e(ex);
                int errCode = ex.getCode();
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
//            Dialog.show("ERROR", err, "OK", null);
                return errorMsg;

            }
        } else {
            return "Unknown problem occurred, logout did not succeed (currentUser==null), please contact TodoCatalyst Support with error code 999**";
        }
    }

    @Override
    public void refreshAfterEdit() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
