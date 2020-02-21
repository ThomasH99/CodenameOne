package com.todocatalyst.todocatalyst;

//import com.codename1.analytics.AnalyticsService;
import com.codename1.background.BackgroundFetch;
import com.codename1.components.InfiniteProgress;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.Log;
import static com.codename1.io.Log.e;
import static com.codename1.io.Log.p;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
//import com.codename1.io.MyUtil.register;
import com.codename1.l10n.L10NManager;
import com.codename1.messaging.Message;
import com.codename1.notifications.LocalNotificationCallback;
//import com.codename1.push.Push;
//import com.codename1.push.PushAction;
//import com.codename1.push.PushActionCategory;
import com.codename1.ui.*;
import static com.codename1.ui.CN.getPlatformName;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import com.codename1.util.Callback;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseACL;
import com.parse4cn1.ParseConstants;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseUser;
import com.parse4cn1.Permissions;
import com.parse4cn1.util.Logger;
import com.parse4cn1.util.ParseRegistry;
import static com.todocatalyst.todocatalyst.ScreenLogin.getLastUserSessionFromStorage;
import static com.todocatalyst.todocatalyst.ScreenLogin.setDefaultACL;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;
import net.informaticalibera.cn1.nativelogreader.NativeLogs;
//import net.informaticalibera.cn1.nativelogreader.NativeLogs;
//import net.informaticalibera.cn1.nativelogreader.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.lcdui.Display;

/**
 * @author Thomas
 */
//public class TodoMidlet extends MIDlet {
public class TodoCatalyst implements LocalNotificationCallback, BackgroundFetch {//, PushCallback {//, PushActionsProvider{

    private final int count = 0;
    final static String APP_NAME = "TodoCatalyst";
    public static Resources theme = null;
    private Form current = null;

    //TODO add startup picture (also shown as automatically generated CN1 image)
    public TodoCatalyst() {
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Form current;
//    static Command exitCommand = new Command("Exit") {
//        public void actionPerformed(ActionEvent actionEvent) {
////#mdebug
//            Log.p("Exit");
////#enddebug
////                ((TodoMidlet) (TodoMidlet.self)).exitMIDlet();
////                exitMIDlet();
////            ((TodoCatalyst2) (TodoCatalyst2.self)).exitMIDlet();
////            exitApp();
//            //xxxexitMIDlet(); TODO: find a way to exit from inside a screen
//        }
//    };
//    private boolean midletPaused = false;
//</editor-fold>
    Resources resources;

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    static MIDlet self; // = null; // used to access the MIDlet from other classes
    //Dummy classes until these classes are properly implemented.
    //public class Categories {}
    //public class Status extends BaseItem {}
    //    public class ClassAccessibility {
    //    }
    //
    //    public class LocationString {
    //    }
    //
    //    public class Recur {
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void xxsetupTodoApp() { //throws IOException {
    //        Log.l("- SVGImplementationFactory.init():");
    ////        SVGImplementationFactory.init(); //initialize the SVG factory. NB! Must be done before call to Display.init(this)
    //        Log.l("- Display.init(this):");
    //        Display.init(this);
    ////        javax.microedition.lcdui.Display.getDisplay(this).setCurrent(null); //supposedly pushes the application to the background on devices supporting this, eg Nokia: http://discussion.forum.nokia.com/forum/showthread.php?171390-Display.setCurrent-equivalent-in-LWUIT
    ////        Display.getInstance().restoreMinimizedApplication();
    ////        if (!javax.microedition.lcdui.Display.getDisplay(this).getCurrent().isShown()) {
    ////            javax.microedition.lcdui.Display.getDisplay(this).setCurrent(javax.microedition.lcdui.Display.getDisplay(this).getCurrent());
    ////        }
    ////        Display.getInstance().;
    ////#mdebug
    //        Log.l("- setupTodoApp");
    ////#enddebug
    //        Log.l("- UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true):");
    //        UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true); //Nokia style with menu on the left
    //        if (UIManager.getInstance().getLookAndFeel().getMenuRenderer() == null) {
    //            UIManager.getInstance().getLookAndFeel().setMenuRenderer(new DefaultListCellRenderer());
    //        }
    //        final ListCellRenderer menuRenderer = UIManager.getInstance().getLookAndFeel().getMenuRenderer(); //!=null?UIManager.getInstance().getLookAndFeel().getMenuRenderer():new DefaultListCellRenderer();
    //        UIManager.getInstance().getLookAndFeel().setMenuRenderer(new ListCellRenderer() {
    //            public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
    //                if (value instanceof MyCommand) {
    //                    MyCommand myCommand = (MyCommand) value;
    //                    Label label;
    //                    if (myCommand.isActive(value)) {
    //                        label = new Label(">" + myCommand.getCommandName() + "<");
    ////                        label.setSelectedStyle(null);
    //                    } else {
    //                        label = new Label("-" + myCommand.getCommandName() + "-");
    //                    }
    //                    return label;
    //                } else {
    //                    return menuRenderer.getListCellRendererComponent(list, value, index, isSelected);
    //                }
    //            }
    //
    //            public Component getListFocusComponent(List list) {
    //                return menuRenderer.getListFocusComponent(list);
    //            }
    //        }); //Nokia style with menu on the left
    //
    //        Log.l("- BaseItemDAO.getInstance().initialize():");
    ////        BaseItemDAO.getInstance().initialize(); //initialize DAO
    ////        BaseItemDAO.getInstance(); //- initialize alredy called in constructor: .initialize(); //initialize DAO
    ////#mdebug
    //        Storage.getInstance().clearStorage(); //clear everything in storage
    ////#enddebug
    //        BaseItemDAO.getInstance().initialize(); //initialize DAO
    //
    //        Log.l("- Settings.getInstance():");
    ////        Settings.getInstance().initialize(); //Initialize Settings
    //        Settings settings = Settings.getInstance(); //.initialize(); //Initialize Settings
    //        Log.l("- settings.initialize():");
    //        settings.initialize();
    //
    //        Log.l("- if (BaseItemDAO.getInstance().getBaseItem(Settings.getInstance().getFileNameMyLists())==null):");
    //        //if default logicalNames can't be found, then create all default logicalNames //-done here to avoid loops during init
    ////        if (BaseItemDAO.getInstance().getBaseItem(Settings.getInstance().getFileNameMyLists()) == null) {//TODO!!!!: find more stable solution!!
    ////            Log.l("- BaseItemDAO.getInstance().setupDefaultStorageFormatAndPredefinedLists():");
    ////            BaseItemDAO.getInstance().setupDefaultStorageFormatAndPredefinedLists();
    ////        }
    //
    //        try {
    ////            resources = Resources.open("/businessTheme13.res"); //todoTheme1.res
    ////            resources = Resources.open(Settings.getInstance().getThemesFilename()); //todoTheme1.res
    //            Log.l("open ressource file " + Settings.getInstance().getThemesFilename() + "...");
    ////            resources = Resources.open(Settings.getInstance().getThemesFilename()); //todoTheme1.res
    ////            resources = Resources.open("/theme.res"); //todoTheme1.res
    ////            resources = Resources.open("/themeAndGUI.res"); //todoTheme1.res
    ////            resources = Resources.open("/TodoCatProto.res"); //todoTheme1.res
    //            resources = Resources.open(Settings.getInstance().getThemesFilename()); //todoTheme1.res
    //            Log.l("ressource file opened, resources=" + resources);
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //
    ////            UIManager.getInstance().setResourceBundle(resources.getL10N("locale", "fr")); //str1=name of L10N resource eg "lit", str2 is locale eg "es"
    ////            UIManager.getInstance().setResourceBundle(resources.getL10N("locale", "en")); //str1=name of L10N resource eg "lit", str2 is locale eg "es"
    //
    ////            Hashtable theme = resources.getTheme(resources.getThemeResourceNames()[0]);
    ////        Hashtable theme = resources.getTheme("Theme 1");
    //        Hashtable theme = resources.getTheme("Theme 2");
    //        UIManager.getInstance().setThemeProps(theme);
    //        MyStyleServer.installMyStyles(theme); //installs my own style of metastyles (Item -> Item_Done)
    //
    //        AlarmServer.getInstance().onAppStartupCheckIfAnyActiveAlarmsShouldSoundNow();
    //
    //    }
    //    public void actionPerformed(ActionEvent arg0) {
    //        try {
    //            throw new Exception("Not supported yet.");
    //        } catch (Exception ex) {
    //            ex.printStackTrace();
    //        }
    //    }
    //    private void initialize() {
    ////#mdebug
    //        Log.l("initialize!");
    ////#enddebug
    //    }
    //
    //    public void resumeMIDlet() {
    ////#mdebug
    //        Log.l("resumeMIDlet!");
    ////#enddebug
    //    }
    //
    //    public void startMIDlet() {
    ////#mdebug
    //        Log.l("startMIDlet!");
    ////#enddebug
    //
    //    }
    //    public void exitMIDlet() {
    //        //itemList.applyFilter(false); //necessary to ensure that filtered items are saved!!
    //        //BaseItemDAO.getInstance().saveBaseItem("ItemList", itemList); //TODO: shouldn't be necessary to save here, should be done at each edit??!
    ////#mdebug
    //        Log.l("exitMIDlet!");
    ////#enddebug
    ////        BaseItemDAO.getInstance().dumpRMSstorage();
    //        //BaseItemDAO.getInstance().closeRmsFile(); // ensure all is saved before exiting //-moved to destroyApp to ensure saving even if destroyed
    ////        destroyApp(true);
    ////        notifyDestroyed();
    //        //TODO!!!! how to exit app in codenameone???
    //    }
    //    public static void xxexitApp() {
    //        //TODO!!!! how to exit app in codenameone???
    //    }
    //
    //    public void xxstartApp() {
    ////#mdebug
    ////        Log.l("startApp");
    ////        Log.l("datasync.api.version=" + System.getProperty("datasync.api.version"));
    ////        Log.l("datasync.api.supportsautostart=" + System.getProperty("datasync.api.supportsautostart"));
    ////        Log.l("datasync.api.protocols=" + System.getProperty("datasync.api.protocols"));
    //////#enddebug
    ////        if (midletPaused) {
    ////            resumeMIDlet();
    //////#mdebug
    ////            Log.l("startApp(): midletPaused - call resumeMIDlet()");
    //////#enddebug
    ////        } else {
    ////            try {
    ////                //Exit.initialize(this);
    //////                self = this;
    ////                initialize();
    ////                startMIDlet();
    //        xxsetupTodoApp();
    //        Log.l("Create mainScreen");
    //        ScreenMain mainScreen = new ScreenMain();
    //        Log.l("Display mainScreen");
    //        mainScreen.display();
    //        //displayMainScreen();
    ////            } catch (IOException ex) {
    ////                ex.printStackTrace();
    ////            }
    ////        }
    ////        midletPaused = false;
    //    }
    //    public void pauseApp() {
    //        midletPaused = true;
    ////#mdebug
    //        Log.l("pauseApp");
    ////#enddebug
    //    }
    /**
     * Doc from http://developers.sun.com/mobility/midp/articles/pushreg/:
     * Destroyed state. Release resources (connection, threads, etc). Also,
     * schedule the future launch of the MIDlet.
     *
     * @param uc If true when this method is called, the MIDlet must cleanup and
     * release all resources. If false the MIDlet may throw a
     * MIDletStateChangeException to indicate it does not want to be destroyed
     * at this time.
     * @throw MIDletStateChangeException to indicate it does not want to be
     * destroyed at this time.
     *
     * public void destroyApp(boolean uc) throws MIDletStateChangeException { //
     * Release resources ... // Set up the alarm and force the MIDlet to exit.
     * scheduleMIDlet(defaultDeltaTime); display = null; }
     *
     * @param unconditional
     */
    //    public void destroyApp(boolean unconditional) {
    ////        BaseItemDAO.getInstance().closeRmsFile(); // ensure all is saved before exiting
    ////#mdebug
    //        Log.l("destroyApp!!!");
    ////#enddebug
    //    }
    //    void pushMidletToBackground() {
    //        // according to http://forums.sun.com/thread.jspa?threadID=782941
    //        javax.microedition.lcdui.Display display = javax.microedition.lcdui.Display.getDisplay(this);
    //        display.setCurrent(null);
    //    }
    //    private void scheduleMIDlet(long deltatime) throws ClassNotFoundException, ConnectionNotFoundException, SecurityException {
    //        /*
    //        http://www.devx.com/wireless/Article/20154/1954: "It's important to remember, however, that once a MIDlet suite is running,
    //        the AMS will ignore inbound connection notifications as well as any registered alarms.
    //        It is the suite's responsibility to handle these activities while running, even if the MIDlet suite is in the paused state."
    //         */
    //
    //        String cn = this.getClass().getName();
    //        // Get the current time by calling Date.getTime()
    //        Date alarm = new Date();
    //        long t = PushRegistry.registerAlarm(cn, alarm.getTime() + deltatime);
    //    }
    //</editor-fold>
    /**
     * This is a Codename One initialization callback that is invoked when the
     * app is started.
     *
     * CN1 doc: "init(Object) - invoked when the application is started but not
     * when its restored from the background. E.g. if the app isn’t running,
     * init will be invoked. However, if the app was minimized and is then
     * restored init will not be invoked. This is a good place to put generic
     * initializations of variables and the likes. We specifically use it to
     * initialize the theme which is something we normally need to do only once
     * per application execution. The object passed to init is the native OS
     * context object, it can be null but can be ignored for 99% of the use
     * cases."
     *
     * @param context
     */
//    @Override
    public void init(Object context) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Display.getInstance().addEdtErrorHandler(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                evt.consume();
//                Log.p("Exception in AppName version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
//                Log.p("OS " + Display.getInstance().getPlatformName());
//                Log.p("Error " + evt.getSource());
//                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
//                Log.e((Throwable) evt.getSource());
//                Log.sendLog();
//            }
//        });
// Pro users - uncomment this code to get crash reports sent to you automatically
//        if (false)
//        Resources theme = UIManager.initFirstTheme("/theme");
//        Resources theme = null;
//</editor-fold>
        Log.getInstance().setFileWriteEnabled(true);
        Log.setLevel(Log.DEBUG);
        Log.setReportingLevel(Log.REPORTING_DEBUG);
        //PARSE logging:
        Logger.getInstance().setLogLevel(Log.DEBUG); //set parse4cn1 log level

        Log.p("init() starting...");

        NativeLogs.initNativeLogs();

        if (false) {
            Log.install(new Log() {
                @Override
                protected void print(String text, int i) {
//                super.print(text, level); //To change body of generated methods, choose Tools | Templates.
                    System.out.print(text + " " + i);
                }
            }
            );
        }

//<editor-fold defaultstate="collapsed" desc="register subclasses for CN1">
//        ParseRegistry.registerSubclass(ParseObjectTask.class, ParseObjectTask.CLASS_NAME);
        ParseRegistry.registerSubclass(Item.class, Item.CLASS_NAME);
//        ParseRegistry.registerParseFactory(ParseObjectTask.CLASS_NAME, new Parse.IParseObjectFactory() {
        ParseRegistry.registerParseFactory(Item.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (Item.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new Item();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerSubclass(ItemList.class, ItemList.CLASS_NAME);
//        ParseRegistry.registerParseFactory(ParseObjectTask.CLASS_NAME, new Parse.IParseObjectFactory() {
        ParseRegistry.registerParseFactory(ItemList.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (ItemList.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new ItemList();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });
        ParseRegistry.registerSubclass(Category.class, Category.CLASS_NAME);
//        ParseRegistry.registerParseFactory(ParseObjectTask.CLASS_NAME, new Parse.IParseObjectFactory() {

        ParseRegistry.registerParseFactory(Category.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (Category.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new Category();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(WorkSlot.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (WorkSlot.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new WorkSlot();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(CategoryList.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (CategoryList.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new CategoryList();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(ItemListList.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (ItemListList.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new ItemListList();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(TemplateList.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (TemplateList.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new TemplateList();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(FilterSortDef.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (FilterSortDef.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new FilterSortDef();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(RepeatRuleParseObject.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (RepeatRuleParseObject.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new RepeatRuleParseObject();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });

        ParseRegistry.registerParseFactory(TimerInstance.CLASS_NAME, new Parse.IParseObjectFactory() {

            @Override
            public <T extends ParseObject> T create(String className) {
                if (TimerInstance.CLASS_NAME.equals(className)) {
//                    return (T) new ParseObjectTask();
                    return (T) new TimerInstance();
                }
                throw new IllegalArgumentException("Unsupported class name: " + className);
            }
        });
//</editor-fold>

        MyAnalyticsService.init("UA-133276111-1", "todocatalyst.com");
        MyAnalyticsService.setAppsMode(true);

        //THEME
        if (MyPrefs.themeNameWithoutBackslash.getString().length() > 0) {
            theme = UIManager.initFirstTheme("/" + MyPrefs.getString(MyPrefs.themeNameWithoutBackslash));
        }
//            theme = UIManager.initFirstTheme("/" + MyPrefs.getString(MyPrefs.themeNameWithoutBackslash));

        //LOCALIZATION
        Resources localizationTheme = null;
        try {
            localizationTheme = Resources.openLayered("/theme-localization");
//            UIManager.getInstance().setThemeProps(localizationTheme.getTheme(localizationTheme.getThemeResourceNames()[0]));
        } catch (IOException ex) {
            Log.p(TodoCatalyst.class.getName());
        }

        String locale = "";
        if (MyPrefs.localeUserSelected.getString().length() == 0) {
            locale = L10NManager.getInstance().getLanguage();
        } else {
            locale = MyPrefs.localeUserSelected.getString();
        }
        if (localizationTheme != null) {
            UIManager.getInstance().setBundle(localizationTheme.getL10N("LocalizationBundle", locale));
        } else {
            ASSERT.that("localizationTheme is null?!");
        }
        Log.p("Locale (Display.getInstance().getLocalizationManager().getLocale()) = " + Display.getInstance().getLocalizationManager().getLocale());
        Log.p("TimeZone (TimeZone.getDefault()) = " + TimeZone.getDefault());

        Toolbar.setGlobalToolbar(true); //needed, otherwise toolbar null in other screens

//        Log.getInstance();
//<editor-fold defaultstate="collapsed" desc="** bindCrashProtection **">
        if (false) {
            Log.bindCrashProtection(false); //TODO: should probaly be true in production version (to consume errors so end-user doesn't see them)
        } else {
//            m.setUrl("https://crashreport.codenameone.com/CrashReporterEmail/sendCrashReport");
//            byte[] read = MyUtil.readInputStream(Storage.getInstance().createInputStream("CN1Log__$"));
//            m.addArgument("i", "" + Log.getUniqueDeviceId());
//            m.addArgument("u", Display.getInstance().getProperty("built_by_user", ""));
//            m.addArgument("p", Display.getInstance().getProperty("package_name", ""));
//            m.addArgument("v", Display.getInstance().getProperty("AppVersion", "0.1"));
            Display.getInstance().addEdtErrorHandler(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (Display.getInstance().isSimulator()) {
                        return;
                    }
                    if (true) {//consumeError
                        evt.consume();
                    }

                    //start using google analytics crashreports, see https://developers.google.com/analytics/devguides/collection/protocol/v1/devguide#exception
                    StringBuilder s = new StringBuilder();
//                    s.append("Exception in " + Display.getInstance().getProperty("AppName", "app"));
//                    s.append(" version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
//                    s.append("OS " + Display.getInstance().getPlatformName());
//                    s.append("Error " + evt.getSource());
                    s.append(Display.getInstance().getPlatformName());
                    s.append("|");
                    s.append(evt.getSource());
                    s.append("|");
                    s.append("Form[" + (Display.getInstance().getCurrent() != null ? Display.getInstance().getCurrent().getName() : "none") + "]");
                    byte[] read1 = new byte[]{(byte) 0xe0};//['a'];
                    String rs = "";
                    try {
                        read1 = com.codename1.io.Util.readInputStream(Storage.getInstance().createInputStream("CN1Log__$"));
                        rs = new String(read1, "BaSE64");
                    } catch (IOException ex) {
                        Log.p(TodoCatalyst.class.getName(), Log.ERROR);
                    }
                    s.append("\nLOG:\n").append(rs); //TODO!!!: shorten stack trace to only show the methods called. For now, the first, most significant, part of trace will be included

                    MyAnalyticsService.sendCrashReport((Throwable) evt.getSource(), s.toString(), false);

                    p("Exception in " + Display.getInstance().getProperty("AppName", "app") + " version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
                    p("OS " + Display.getInstance().getPlatformName());
                    p("Error " + evt.getSource());
                    if (Display.getInstance().getCurrent() != null) {
                        p("Current Form " + Display.getInstance().getCurrent().getName());
                    } else {
                        p("Before the first form!");
                    }
                    e((Throwable) evt.getSource());
                    byte[] read = new byte[]{(byte) 0xe0};//['a'];
                    try {
                        //                sendLog();
                        read = com.codename1.io.Util.readInputStream(Storage.getInstance().createInputStream("CN1Log__$"));
//                        read.toString();

                        Message m = new Message("Body of message"
                                + "DeviceId: " + Log.getUniqueDeviceId()
                                + "\nBuilt by user: " + Display.getInstance().getProperty("built_by_user", "")
                                + "\nPackage name: " + Display.getInstance().getProperty("package_name", "")
                                + "\nAppVersion: " + Display.getInstance().getProperty("AppVersion", "0.1")
                                + "\nLOG:\n---------------------------------\n"
                                //                            + Storage.getInstance().readObject("CN1Log__$")
                                //                            + new String(read)
                                //                            + MyUtil.hexStringToByteArray(read)
                                + new String(read, "BaSE64") // for UTF-8 encoding, https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa
                        );
//            m.getAttachments().put(textAttachmentUri, "text/plain");
//            m.getAttachments().put(imageAttachmentUri, "image/png");
                        Display.getInstance().sendMessage(new String[]{"crashreport@todocatalyst.com"}, "TodoCatalyst crash report", m);
                    } catch (IOException ex) {
//                        java.util.logging.Logger.getLogger(TodoCatalyst.class.getName()).log(Level.SEVERE, null, ex);
                        Log.p(TodoCatalyst.class.getName(), Log.ERROR);
                    }
                }
            });
        }
//</editor-fold>

//        Log.getInstance().setFileWriteEnabled(true);
//        Log.setLevel(Log.DEBUG);
//        Log.setReportingLevel(Log.REPORTING_DEBUG);
//        //PARSE logging:
//        Logger.getInstance().setLogLevel(Log.DEBUG); //set parse4cn1 log level
//        NativeLogs.initNativeLogs();
        Log.p("LOCALE = " + locale);

//        if (Config.PARSE_OFFLINE && !getPlatformName().equals("ios") && !getPlatformName().equals("and")) { //never run in local mode on a device !!seems to return "ios" with ios skin on simulator??
        if (Config.PARSE_OFFLINE && Display.getInstance().isSimulator()) { //never run in local mode on a device
            Parse.initialize(
                    "http://localhost:1337/parse",
                    "l0Gw4hYdg7hJDPEG11Qzxqh59Yj9F2JXDkDdbdCc",
                    "not important");
            Log.p("using server http://localhost:1337/parse (LOCAL)");
        } else {
            Parse.initialize(
                    "https://parseapi.back4app.com",
                    "TYR54TdOmVfIGSKIl3aEmpcKMPNrbg7T9zN6QciT",
                    "SqFUD7hbLleCOPtDm0RYJWsqI3syHN31NuOiCrRv");
            Log.p("using server https://parseapi.back4app.com (ONLINE)");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            Display.getInstance().addEdtErrorHandler(new ActionListener() {
//                //TODO!!!! check if keep this error handling for out of memory in production version
//                //https://www.codenameone.com/blog/handling-the-exception.html
//                //https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/memleaks002.html
//                public void actionPerformed(ActionEvent evt) {
////                evt.consume();
////                if (evt.getEventType()==ActionEvent.Type.Exception)
//                    if (((Throwable) evt.getSource()) instanceof OutOfMemoryError) {
//                        System.gc();
//                        System.gc();
//                        Log.p(APP_NAME + " ran out of memory. This can be due your device running out of free memory or due to an internal error. If you have just made changes, they may be lost. ");
//                    }
////<editor-fold defaultstate="collapsed" desc="comment">
////                Log.p("Exception in version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
////                Log.p("OS " + Display.getInstance().getPlatformName());
////                Log.p("Error " + evt.getSource());
////                Log.p("Current Form " + Display.getInstance().getCurrent().getName());
////                Log.e((Throwable) evt.getSource());
////                Log.sendLog();
////</editor-fold>
//                }
//            });
//        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//        Log.setReportingLevel(Log.REPORTING_DEBUG); //
//        DefaultCrashReporter.init(true, 2); //automatically send crash logs every 2 minutes
//        Log.getInstance().
//        try {
// loading the theme of the application
        if (true) {
//                resources = Resources.openLayered("/theme");
//                resources = Resources.open("/themeNative");
//                UIManager.getInstance().setThemeProps(resources.getTheme(resources.getThemeResourceNames()[0]));
//                resources = UIManager.initFirstTheme("/themeNative");
//                resources = UIManager.initFirstTheme("/theme");
//            Resources theme;
//            try {
//                theme = Resources.openLayered("/theme");
//                UIManager.getInstance().setThemeProps(theme.getTheme(theme.getThemeResourceNames()[0]));
//            } catch (IOException ex) {
//                Log.e(ex);
//            }
//            Resources theme = UIManager.initFirstTheme("/theme");

//                resources = UIManager.initFirstTheme("/themeBlueNative");
//                resources = UIManager.initFirstTheme("/theme");
        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="** com.codename1.io.Util.register externalizable classes **">
//        MyUtil.register(PreviouslyRunningTimerEntry.CLASS_NAME_PREVIOUSLY_RUNNING_TIMERS, PreviouslyRunningTimerEntry.class); //register Externalizable class
        com.codename1.io.Util.register(TimerStackEntry.CLASS_NAME_TIMER_STACK_ENTRY, TimerStackEntry.class); //register Externalizable class
//        MyUtil.register(AlarmData.CLASS_NAME_ALARM_DATA, AlarmData.class); //register Externalizable class
        com.codename1.io.Util.register(ParseConstants.CLASS_NAME_USER, ParseUser.class); //register Externalizable class
        com.codename1.io.Util.register(NotificationShadow.CLASS_NAME_NOTIFICATION_SHADOW, NotificationShadow.class); //register Externalizable class
        com.codename1.io.Util.register(LocalNotificationsShadowList.CLASS_NAME_NOTIFICATION_LIST, LocalNotificationsShadowList.class); //register Externalizable class
        com.codename1.io.Util.register(ExpiredAlarm.CLASS_NAME_EXPIRED_ALARM, ExpiredAlarm.class); //register Externalizable class
        com.codename1.io.Util.register(ParseACL.CLASS_NAME, ParseACL.class); //register Externalizable class
//        MyUtil.register(ParseACL.Permissions.CLASS_NAME_PARSE_PERMISSIONS, ParseACL.Permissions.class); //register Externalizable class
        com.codename1.io.Util.register(Permissions.CLASS_NAME_PARSE_PERMISSIONS, Permissions.class); //register Externalizable class
        com.codename1.io.Util.register(Item.CLASS_NAME, Item.class); //register Externalizable class
        com.codename1.io.Util.register(ItemList.CLASS_NAME, ItemList.class); //register Externalizable class
        com.codename1.io.Util.register(ItemListList.CLASS_NAME, ItemListList.class); //register Externalizable class
        com.codename1.io.Util.register(TemplateList.CLASS_NAME, TemplateList.class); //register Externalizable class
        com.codename1.io.Util.register(Category.CLASS_NAME, Category.class); //register Externalizable class
        com.codename1.io.Util.register(CategoryList.CLASS_NAME, CategoryList.class); //register Externalizable class
        com.codename1.io.Util.register(RepeatRuleParseObject.CLASS_NAME, RepeatRuleParseObject.class); //register Externalizable class
        com.codename1.io.Util.register(FilterSortDef.CLASS_NAME, FilterSortDef.class); //register Externalizable class
        com.codename1.io.Util.register(WorkSlot.CLASS_NAME, WorkSlot.class); //register Externalizable class
//        com.codename1.io.Util.register(ListSelector.CLASS_NAME, ListSelector.class); //register Externalizable class
//</editor-fold>

        Display.getInstance().setLongPointerPressInterval(400); //UI: 600 too long, 700 is maybe a bit long, 650 a bit too long. set delay for activating LongPress (default 800 is too fast??)
//<editor-fold defaultstate="collapsed" desc="comment">
//add a 'theme layer' on top of an existing theme
//            UIManager.getInstance().addThemeProps(theme.getTheme("NameOfLayerTheme"));
// the specification requires that only portrait would be supported
//            Display.getInstance().lockOrientation(true);
// We load the images that are stored in the resource file here so we can use them later
//            placeholder = (EncodedImage) theme.getImage("placeholder");
//            favoriteSel = theme.getImage("favorite_sel");
//            favoriteUnsel = theme.getImage("favorite_unsel");
//
//            // We initialize the lists of favorites and recent entries here, we load them from storage and save
//            // them whenever we change them
//            favoritesList = (java.util.List<Map<String, Object>>) Storage.getInstance().readObject("favoritesList");
//            if (favoritesList == null) {
//                favoritesList = new ArrayList<Map<String, Object>>();
//            }
//            recentSearchesList = (java.util.List<Map<String, String>>) Storage.getInstance().readObject("recentSearches");
//            if (recentSearchesList == null) {
//                recentSearchesList = new ArrayList<Map<String, String>>();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//</editor-fold>
        Icons.get(); //Init singleton for icons

        if (!Config.PARSE_OFFLINE) {
            NetworkManager.getInstance().addErrorListener((e) -> {
                Log.p("NetworkManager error=" + e);
                //"There was a network error, would you like to retry?"
                //"There is no network connection, please retry when the network is available again (your changes will be lost if the app is stopped before the network is available)"
//            if (Dialog.show("Network Error", "There was a network error, would you like to retry?", "Retry", "Cancel")) {
//            if (Dialog.show("Network Error", "There is no network connection, please retry when the network is available again. The just made changes will be lost unless you retry successfully before the exiting app.", "Retry", null)) {
                if (Dialog.show("Network Error", "No network connection. Please Retry when available again. "
                        + "\n\nIf you exit the app before a successful Retry, any changes just made will be lost.", "Retry", null)) {
                    e.consume();
                    ConnectionRequest conReq = e.getConnectionRequest();
                    conReq.retry();
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (Dialog.show("Network problem. Try again?", "OK", "No")) {
//                NetworkManager.getInstance().
//            } else {
//
//            }
//            e.consume();
//</editor-fold>
            });
        }

        // will return true for desktops as well...
//        if (Display.getInstance().isTablet()) { //TODO!!!! is not working
//            Toolbar.setPermanentSideMenu(true); //https://www.codenameone.com/blog/permanent-sidemenu-getAllStyles-scrollbar-and-more.html
        Toolbar.setPermanentSideMenu(Display.getInstance().isTablet()); //https://www.codenameone.com/blog/permanent-sidemenu-getAllStyles-scrollbar-and-more.html
        Display.getInstance().setPureTouch(true);
        if (Display.getInstance().isTablet()) {
            Display.getInstance().lockOrientation(true); //lock screen rotation to portrait=true, https://stackoverflow.com/questions/48712682/codenameone-rotate-display
        }//        }

//        Display d = Display.getInstance();
//        Label supported = new Label();
        //code from https://www.codenameone.com/blog/background-fetchFromCacheOnly.html
        if (Display.getInstance().isBackgroundFetchSupported() && MyPrefs.alarmsActivatedOnThisDevice.getBoolean()) {
            // This call is necessary to initialize background fetchFromCacheOnly
//<editor-fold defaultstate="collapsed" desc="comment">
//            Display.getInstance().setPreferredBackgroundFetchInterval(MyDate.HOUR_IN_MILISECONDS * 12 / 1000); //in seconds = 12hours
//            Display.getInstance().setPreferredBackgroundFetchInterval(MyPrefs.getInt(MyPrefs.backgroundFetchIntervalInSeconds)); //in seconds = 12hours
//            Display.getInstance().setPreferredBackgroundFetchInterval(MyPrefs.backgroundFetchIntervalInSeconds.getInt());

//fetch twice (/2) in each internal, should guarantee fetching during day/night
//            Display.getInstance().setPreferredBackgroundFetchInterval(Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt()/2,1)*MyDate.HOUR_IN_MILISECONDS); //max(.., 1): ensure that interval never gets 0 after division by 2
//            Display.getInstance().setPreferredBackgroundFetchInterval(Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt()* 3600 / 2, 3600/4) ); //max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
//            AlarmHandler.setPreferredBackgroundFetchInterval();
//</editor-fold>
//max(.., 1): ensure that interval never gets 0 after division by 2, 3600: sec/hour, 3600/4: never more often than every 15 minutes
//int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() * 3600 / 2, 3600 / 4);
//int fetchIntervalSeconds = Math.max(MyPrefs.alarmFutureIntervalInWhichToSetAlarmsInHours.getInt() *3600 / 2, 3600 / 4);
//            Display.getInstance().setPreferredBackgroundFetchInterval(fetchIntervalSeconds); 
            AlarmHandler.setPreferredBackgroundFetchInterval();
            Log.p("Background Fetch IS Supported");
        } else {
            Log.p("Background Fetch is NOT Supported");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            Display.getInstance().setPreferredBackgroundFetchInterval(MyDate.HOUR_IN_MILISECONDS*12/1000); //=12hours

//        //set the app icon badge count
//        if (Display.getInstance().isBadgingSupported()) {
//            Display.getInstance().setBadgeNumber(DAO.getInstance().getDueTodayCount(true));
//        }
// we show the main UI the first time around
//        showMainForm(false, null, null);
//        showMainForm(false, null, null);
//</editor-fold>
        InfiniteProgress.setDefaultMaterialDesignMode(true);

//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        new ScreenMainP(resources).show();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) { //test subtasks
//            Item testItem = new Item("Test with subtasks");
//            Item subtask1 = new Item("subtasks1");
//            Item subtask2 = new Item("subtasks1");
//            try {
//                subtask1.save();
//                subtask2.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
////            ItemList itemList = testItem.getItemList();
//            List itemList = testItem.getList();
////            itemList.addItem(subtask1);
//            itemList.add(subtask1);
////            itemList.addItem(subtask2);
//            itemList.add(subtask2);
//            testItem.setItemList(itemList);
//            try {
//                testItem.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//            Dialog.show("", "Click OK to create subtasks", "OK", "Cancel");
//        }
//</editor-fold>
        //LOGIN
//<editor-fold defaultstate="collapsed" desc="old login code">
//        if (false) { //moved to ScreenLogin
////            if (MyPrefs.loginFirstTimeLogin.getBoolean()) { //very first login
////                MyPrefs.setBoolean(MyPrefs.loginFirstTimeLogin, false);
////                new ScreenLogin(theme).show();
////            } else {
//            ParseUser parseUser = ScreenLogin.getLastUserSessionFromStorage();
//            Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
//            if (parseUser == null) {
////                new ScreenLogin(theme).show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//                new ScreenLogin().show(); //TODO!!!: optimization: don't create the ScreenMain before launching login!
//            } else {
//                ScreenLogin.setDefaultACL(parseUser); //TODO needed??
//                try {
//                    count = query.count();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//
//                Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//
////            DAO.getInstance().cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(), true); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//
//                //ALARMS - initialize
//                AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
//
//                //TIMER - was running when app was moved to background?
////                if (!ScreenTimer.getInstance().isTimerActive()) {
////                    new ScreenMain().show(); //go directly to main screen if user already has a session
////                } else {
////                    if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
////                        new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
////                    }
////                }
//            }
//        } else {
//            new ScreenLogin(theme).go();
//</editor-fold>
        Display.getInstance().setProperty("iosHideToolbar", "true"); //prevent ttoolbar over keyboard to show (Done/Next button): https://stackoverflow.com/questions/48727116/codename-one-done-button-of-ios-virtual-keyboard

        if (Display.getInstance().canForceOrientation()) {
            Display.getInstance().lockOrientation(true); //prevent screen rotation, true=portrait, but only Android, see https://stackoverflow.com/questions/48712682/codenameone-rotate-display
        }

        //Check if already logged in, if so, removeFromCache cache
        //if not logged in, show window to create account or log in. NB! Must init user before starting the login process since the init of first screen may read the TimerStack which becomes empty if user is not logged in
        ParseUser parseUser = getLastUserSessionFromStorage();
        Log.p("ParseUser=" + (parseUser == null ? "null" : parseUser));
        if (parseUser != null) {
            setDefaultACL(parseUser);
        }

        Log.p("init() - DONE - go to login screen...");

        new ScreenLogin().go();
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void initx(Object context) {
    //        try {
    //            Log.p("open ressource file " + Settings.getInstance().getThemesFilename() + "...");
    ////            resources = Resources.open("/themeAndGUI.res"); //todoTheme1.res
    //            resources = Resources.open(Settings.getInstance().getThemesFilename()); //todoTheme1.res
    //            Log.p("ressource file opened, resources=" + resources);
    //            Log.p("TheseResourceNames()= ");
    ////            Log.p(resources.getThemeResourceNames());
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //</editor-fold>
    public String getAppstoreURL() { //copied from CN1 kitchensink demo
        if (Config.ENABLE_ASK_USER_TO_RATE_APP) {
            if (getPlatformName().equals("ios")) {
                return "https://itunes.apple.com/us/app/kitchen-sink-codename-one/id635048865";
            }
            if (getPlatformName().equals("and")) {
                return "https://play.google.com/store/apps/details?id=com.codename1.demos.kitchen";
            }
        }
        return null;
    }

    /**
     * This is a callback from Codename One, its invoked when the app is started
     * and whenever it is restored from the minimized state.
     *
     * CN1 doc: "start() - the start method is invoked with every launch of the
     * app. This includes restoring a minimized application. This is very useful
     * for initializing UI’s which usually need to be refreshed both when the
     * user launches the app and when he returns to it after leaving it in the
     * background for any duration."
     */
    public void start() {
        //throws ParseException, IOException {
        Log.p("start()");
        if (getAppstoreURL() != null) {  //copied from CN1 kitchensink demo
            RatingWidget.bindRatingListener(180000, getAppstoreURL(), "support@todocatalyst.com");
        }

        //TODO!!! How to find and display previously expired alarms (corresponding to the #notificatins shown on the app badge)?
        //reset badge number when app is launched
        if (false) {
            Display.getInstance().setBadgeNumber(0);
        }
//        Form current = Display.getInstance().getCurrent();
        if (current != null) {
//            if (false && current == ScreenTimer.getInstance()) { //NOT necessary since current.show() should update the timemr
//                ScreenTimer.getInstance().refreshDisplayedTimerInfo();//repaint to update timer count (especially necessary if timer is only updating every minute or so, otherwise it will show wrong time for a long time)
//            }
            if (current instanceof Dialog) {
                ((Dialog) current).dispose();
                current = Display.getInstance().getCurrent();
            }
            current.show();
        }

        if (false) {
            Item item = new Item();
            item.setFilterSortDef(new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, true));
            item.setRepeatRule(new RepeatRuleParseObject());
            ItemList itemList = new ItemList();
            itemList.addItem(item);
            itemList.addToList(item);
            DAO.getInstance().saveNew(true, item, itemList);
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        switch (1) {
//            case 1:
//                break;
//        }
//    case 2:
//        new ScreenMain(resources).show();
//        break;
//    case 3:
//        ParseQuery<Item> query2 = ParseQuery.getQuery(Item.CLASS_NAME);
//        java.util.List<Item> results = null;
//        try {
//            results = query2.find();
//        } catch (ParseException ex) {
////                    Log.egetLogger(ScreenItemListP.class.getName()).log(Level.SEVERE, null, ex);
//Log.e(ex);
//        }
//        new ScreenItemList("ItemList", new ItemList(results), null, ScreenItemList.ITEM_TYPE_ITEM).show();
//        break;
//}
//        } catch (ParseException ex) {
//            Logger.getLogger(TodoCatalyst.class.getName()).log(Level.SEVERE, null, ex);
//        }
//</editor-fold>
    }
//<editor-fold defaultstate="collapsed" desc="comment">
    //    public void startx() {
    //        Log.p("started");
    //
    ////                setupTodoApp();
    ////                Log.l("Create mainScreen");
    ////                ScreenMain mainScreen = new ScreenMain();
    ////                Log.l("Display mainScreen");
    ////                mainScreen.display();
    ////        Log.l("- SVGImplementationFactory.init():");
    ////        SVGImplementationFactory.init(); //initialize the SVG factory. NB! Must be done before call to Display.init(this)
    //        Log.p("- Display.init(this):");
    ////        Display.init(this);
    //        Log.p("- UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true):");
    ////        if (true) {
    ////            Display.getInstance().setCommandBehavior(Display.COMMAND_BEHAVIOR_SIDE_NAVIGATION);
    ////        } else
    //        {
    //            UIManager.getInstance().getLookAndFeel().setReverseSoftButtons(true); //Nokia style with menu on the left
    //            if (UIManager.getInstance().getLookAndFeel().getMenuRenderer() == null) {
    //                UIManager.getInstance().getLookAndFeel().setMenuRenderer(new DefaultListCellRenderer());
    //            }
    //            final ListCellRenderer menuRenderer = UIManager.getInstance().getLookAndFeel().getMenuRenderer(); //!=null?UIManager.getInstance().getLookAndFeel().getMenuRenderer():new DefaultListCellRenderer();
    //            UIManager.getInstance().getLookAndFeel().setMenuRenderer(new ListCellRenderer() {
    //                public Component getListCellRendererComponent(List list, Object value, int index, boolean isSelected) {
    //                    if (value instanceof MyCommand) {
    //                        MyCommand myCommand = (MyCommand) value;
    //                        Label label;
    //                        if (myCommand.isActive(value)) {
    //                            label = new Label(">" + myCommand.getCommandName() + "<");
    ////                        label.setSelectedStyle(null);
    //                        } else {
    //                            label = new Label("-" + myCommand.getCommandName() + "-");
    //                        }
    //                        return label;
    //                    } else {
    //                        return menuRenderer.getListCellRendererComponent(list, value, index, isSelected);
    //                    }
    //                }
    //
    //                public Component getListFocusComponent(List list) {
    //                    return menuRenderer.getListFocusComponent(list);
    //                }
    //            }); //Nokia style with menu on the left
    //        }
    //        Log.p("- BaseItemDAO.getInstance().initialize():");
    ////        BaseItemDAO.getInstance().initialize(); //initialize DAO
    ////        BaseItemDAO.getInstance(); //- initialize alredy called in constructor: .initialize(); //initialize DAO
    ////#mdebug
    //        Storage.getInstance().clearStorage(); //clear everything in storage
    ////#enddebug
    ////        BaseItemDAO.getInstance().initialize(); //initialize DAO
    ////        Settings.getInstance().initialize(); //Initialize Settings
    //
    //        Log.p("- Settings.getInstance():");
    //        Settings settings = Settings.getInstance(); //.initialize(); //Initialize Settings
    //        Log.p("- settings.initialize():");
    //        settings.initialize();
    //
    ////            Hashtable theme = resources.getTheme(resources.getThemeResourceNames()[0]);
    ////            UIManager.getInstance().setResourceBundle(resources.getL10N("locale", "fr")); //str1=name of L10N resource eg "lit", str2 is locale eg "es"
    ////            UIManager.getInstance().setResourceBundle(resources.getL10N("locale", "en")); //str1=name of L10N resource eg "lit", str2 is locale eg "es"
    //        Hashtable theme = resources.getTheme("Theme 1");
    //        UIManager.getInstance().setThemeProps(theme);
    ////        MyStyleServer.installMyStyles(theme); //installs my own style of metastyles (Item -> Item_Done)
    //
    ////        Hashtable theme = resources.getTheme(resources.getThemeResourceNames()[0]);
    ////        UIManager.getInstance().setThemeProps(theme);
    ////        MyStyleServer.installMyStyles(theme); //installs my own style of metastyles (Item -> Item_Done)
    ////        AlarmServer.getInstance().onAppStartupCheckIfAnyActiveAlarmsShouldSoundNow();
    //        Log.p("Create mainScreen");
    //        ScreenMainP mainScreen = new ScreenMainP(theme);
    //        Log.p("Display mainScreen");
    //        mainScreen.display();
    //
    ////                stop();
    ////                destroy();
    ////        Form f = new Form("Hello World");
    ////        f.show();
    //        ParseObject parseObject = ParseObject.create("Task");
    //        parseObject.put("description", "My First Test, time: " + MyDate.getNow());
    //        try {
    //            parseObject.save();
    //        } catch (ParseException ex) {
    ////            Logger.getLogger(TodoCatalyst2.class.getName()).log(Level.SEVERE, null, ex);
    //            Log.p("Parse - put problem");
    //        }
    //    }
//</editor-fold>

    private void setBadgeCount() {
        //set the app icon badge count
        if (Display.getInstance().isBadgingSupported() || Display.getInstance().isSimulator()) {
            Display.getInstance().setBadgeNumber(DAO.getInstance().getBadgeCount(true, true));
//            Display.getInstance().setBadgeNumber(99);
        }
    }

    /**
     * Callback from Codename One indicating the app is minimized into
     * background CN1 doc: "stop() - stop is invoked when the user minimizes the
     * app. If you have ongoing operations (e.g. download/media) you should stop
     * them here or the operating system might kill your application due to CPU
     * usage in the background."
     */
    public void stop() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Form current = Display.getInstance().getCurrent();
////        if (current instanceof Dialog) {
////            if (false)((Dialog) current).dispose();
////            current = Display.getInstance().getCurrent();
////        }
//        if (!(current instanceof Form)) {
////            if (current instanceof Dialog)
////            current = ((Dialog)current).getComponentForm();
//            if (current instanceof Component) {
//                current = ((Component) current).getComponentForm();
//            } else {
//                ASSERT.that("on stop(), current=" + current + " (NOT Form or Component)");
//            }
//        }
//
//        ASSERT.that(current instanceof MyForm, "on stop(), current=" + current + " (NOT Form or Component)");
//        if (false && current instanceof MyForm) { //not needed anymore, now saved on each edit
//            ((MyForm) current).saveEditedValuesLocallyOnAppExitXXX(); //save any ongoing edits
//        }
//        ScreenTimer.getInstance().saveTimerStatusOnAppStop();
//        ScreenTimer.getInstance().onDestroy(); //called here because destroy doesn't seem to be called
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && !MyPrefs.loginStayLoggedIn.getBoolean()) { //DOESN'T make sense to log user out here (when switched to background)
//            try {
//                ParseUser.getCurrent().logout();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
//        setNotification(new Date(System.currentTimeMillis() + 10 * 1000));
//</editor-fold>
        current = Display.getInstance().getCurrent(); //do first in case of issues
        Log.p("stop()"); //do before updating badgeCount which calls network and may be too slow and get killed
        //set the app icon badge count
        setBadgeCount();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (Display.getInstance().isBadgingSupported()) {
//            Display.getInstance().setBadgeNumber(DAO.getInstance().getBadgeCount(true));
////            Display.getInstance().setBadgeNumber(99);
//        }
//</editor-fold>

    }

    /**
     * Callback from Codename One indicating the app is exiting.
     *
     * CN1 doc: "destroy() - this callback isn’t guaranteed since an OS might
     * kill the app and neglect to invoke this method. However, most OS’s will
     * invoke this method before completely closing the app. Since stop() will
     * be invoked first its far better to write code there."
     */
    public void destroy() {
//        ScreenTimer.getInstance().onDestroy();
        Log.p("destroy()");
    }

    /*https://www.codenameone.com/manual/push.html*/
    /**
     * This method will be called in the background by the platform. Note: This
     * only runs when the app is in the background.
     * https://www.codenameone.com/manual/push.html
     *
     * @param deadline
     * @param onComplete
     */
    @Override
    public void performBackgroundFetch(long deadline, Callback<Boolean> onComplete) {
        //https://www.codenameone.com/blog/background-fetch.html
        //https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html
//        RSSService rss = new RSSService("http://rss.slashdot.org/Slashdot/slashdotMain");
//        NetworkManager.getInstance().addToQueueAndWait(rss);
//        records = rss.getResults();
//        System.out.println(records);
        Log.p("performBackgroundFetch called, time=" + new Date() + ", deadline=" + deadline + ", date(deadline)=" + new Date(deadline));
        AlarmHandler.getInstance().updateLocalNotificationsOnBackgroundFetch();
//        System.out.println("performBackgroundFetch/deadline=" + deadline);
        onComplete.onSucess(Boolean.TRUE);
//        Log.p("performBackgroundFetch finished=");
        Log.p("performBackgroundFetch finished, time=" + new Date());
    }
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * Invoked when the push notification occurs
     * https://www.codenameone.com/manual/push.html
     *
     * @param value the value of the push notification
     */
//    @Override
//    public void pushZZZ(String value) {
//        System.out.println("Received push message: " + value);
//    }

    /**
     * Invoked when push registration is complete to pass the device ID to the
     * application. https://www.codenameone.com/manual/push.html
     *
     * @param deviceId OS native push id you should not use this value and
     * instead use <code>Push.getPushKey()</code>
     * @see Push#getPushKey()
     */
//    @Override
//    public void registeredForPushZZZ(String deviceId) {
//        System.out.println("The Push ID for this device is " + Push.getPushKey());
//        //store locally
//        //add to central list for user to be able to send push messages to other devices
//    }
    /**
     * Invoked to indicate an error occurred during registration for push
     * notification
     *
     * @param error descriptive error string
     * @param errorCode an error code
     */
//    @Override
//    public void pushRegistrationErrorZZZ(String error, int errorCode) {
//        System.out.println("An error occurred during push registration.");
//    }
//
//    private static final String PUSH_TOKEN = "********-****-****-****-*************";
//    private static final String FCM_SERVER_API_KEY = "******************-********************";
//
//    private static final String WNS_SID = "ms-app://**************************************";
//    private static final String WNS_CLIENT_SECRET = "*************************";
//
//    private static final boolean ITUNES_PRODUCTION_PUSH = false;
//
//    private static final String ITUNES_PRODUCTION_PUSH_CERT = "https://domain.com/linkToP12Prod.p12";
//    private static final String ITUNES_PRODUCTION_PUSH_CERT_PASSWORD = "ProdPassword";
//    private static final String ITUNES_DEVELOPMENT_PUSH_CERT = "https://domain.com/linkToP12Dev.p12";
//    private static final String ITUNES_DEVELOPMENT_PUSH_CERT_PASSWORD = "DevPassword";
//
//    public void sendPushToOtherDeviceZZZ(String deviceKey) {
//        String cert = ITUNES_DEVELOPMENT_PUSH_CERT;
//        String pass = ITUNES_DEVELOPMENT_PUSH_CERT_PASSWORD;
//        if (ITUNES_PRODUCTION_PUSH) {
//            cert = ITUNES_PRODUCTION_PUSH_CERT;
//            pass = ITUNES_PRODUCTION_PUSH_CERT_PASSWORD;
//        }
//        new Push(PUSH_TOKEN, "Hello World", deviceKey)
//                .apnsAuth(cert, pass, ITUNES_PRODUCTION_PUSH)
//                .gcmAuth(FCM_SERVER_API_KEY)
//                .wnsAuth(WNS_SID, WNS_CLIENT_SECRET)
//                .send();
//    }
//    @Override
//    public PushActionCategory[] getPushActionCategoriesZZZ() {
//        //https://www.codenameone.com/blog/rich-push-notification-improved.html
//
//        return new PushActionCategory[]{
//            new PushActionCategory("fo", new PushAction[]{
//                new PushAction("yes", "Yes"),
//                new PushAction("no", "No"),
//                new PushAction("maybe", "Maybe", null, "Enter reason", "Reply")
//
//})
//
//        };
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    void setNotification(Date time) {
//        LocalNotification n = new LocalNotification();
//        n.setId("demo-notification");
//        n.setAlertBody("It's time to take a break and look at me");
//        n.setAlertTitle("Break Time!");
////        n.setAlertSound("beep-01a.mp3");
//
//        Display.getInstance().scheduleLocalNotification(
//                n,
//                //                System.currentTimeMillis() + 10 * 1000, // fire date/time
//                time.getTime(), // fire date/time
//                LocalNotification.REPEAT_MINUTE // Whether to repeat and what frequency
//        );
//    }
//</editor-fold>
    /**
     * <p>
     * Callback method that is called when a local notification is received AND
     * the application is active. This won't necessarily be called when the
     * notification is received. If the app is in the background, for example,
     * the notification will manifest itself as a message to the user's task bar
     * (or equivalent). If the user then clicks on the notification message, the
     * app will be activated, and this callback method will be called.</p>
     *
     * <p>
     * <em>IMPORTANT: THIS CALLBACK IS CALLED OFF THE EDT. ANY UPDATES TO THE UI
     * WILL NEED TO OCCUR INSIDE A <code>callSerially()</code> block.</em></p>
     *
     * @param notificationId The notification ID of the notification that was
     * received.
     * @see LocalNotification
     */
    @Override
    public void localNotificationReceived(String notificationId) {
        AlarmHandler.getInstance().localNotificationReceived(notificationId);
//<editor-fold defaultstate="collapsed" desc="comment">
//        PushContent res = PushContent.get(); //won't work: see discussion here: https://www.codenameone.com/blog/rich-push-notification-improved.html
//        if (!Dialog.show(notificationId, notificationId, "Cancel", "Continue")) {
//            Display.getInstance().cancelLocalNotification("demo-notification");
//        } else {
//            Display.getInstance().setBadgeNumber(0);
////            Button clearBadgeNumber = new Button("Clear Badge");
////        clearBadgeNumber.addActionListener(new ActionListener() {
////            public void actionPerformed(ActionEvent evt) {
////                Display.getInstance().setBadgeNumber(0);
//        }
//</editor-fold>
    }

}
