package com.todocatalyst.todocat;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.list.DefaultListCellRenderer;
import com.codename1.ui.list.DefaultListModel;
import com.codename1.ui.list.ListCellRenderer;
import com.codename1.ui.list.MultiList;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.util.Resources;
import java.io.IOException;
import java.util.Hashtable;
import com.parse4cn1.Parse;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseUser;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.util.ParseRegistry;
import java.util.ArrayList;
import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.lcdui.Display;

/**
 * @author Thomas
 */
//public class TodoMidlet extends MIDlet {
public class TodoCatalystParse {

    public TodoCatalystParse() {
        Log.install(new Log() {
            @Override
            protected void print(String text, int i) {
//                super.print(text, level); //To change body of generated methods, choose Tools | Templates.
                System.out.print(text + " " + i);
            }
        }
        );
        Log.setLevel(Log.DEBUG);
        Parse.initialize(
                "TYR54TdOmVfIGSKIl3aEmpcKMPNrbg7T9zN6QciT",
                "SqFUD7hbLleCOPtDm0RYJWsqI3syHN31NuOiCrRv");
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
    }

    private Form current;

    static Command exitCommand = new Command("Exit") {
        public void actionPerformed(ActionEvent actionEvent) {
//#mdebug
            Log.p("Exit");
//#enddebug
//                ((TodoMidlet) (TodoMidlet.self)).exitMIDlet();
//                exitMIDlet();
//            ((TodoCatalyst2) (TodoCatalyst2.self)).exitMIDlet();
//            exitApp();
            //xxxexitMIDlet(); TODO: find a way to exit from inside a screen
        }
    };
//    private boolean midletPaused = false;
    //ItemRenderer itemCellRenderer;
//    ItemList itemList; //= new ItemListFiltered();
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
//    public TodoCatalyst2() {
//    }
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
     * app is started
     */
    public void init(Object context) {
        try {
            // loading the theme of the application
            resources = Resources.openLayered("/theme");
            UIManager.getInstance().setThemeProps(resources.getTheme(resources.getThemeResourceNames()[0]));
            Resources theme = UIManager.initFirstTheme("/theme");
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
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * called by Codename One on startup
     *
     * @param context
     */
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
    /**
     * This is a callback from Codename One, its invoked when the app is started
     * and whenever it is restored from the minimized state
     *
     * @throws com.parse4cn1.ParseException
     * @throws java.io.IOException
     */
    public void start() throws ParseException, IOException {
        if (current != null) {
            current.show();
            return;
        }
        // we show the main UI the first time around
//        showMainForm(false, null, null);
//        showMainForm(false, null, null);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        int count = query.count();

        Log.p("Count of Item in Parse = " + count, Log.DEBUG);
//        new ScreenMainP(resources).show();
        if (false) { //test subtasks
            Item testItem = new Item("Test with subtasks");
            Item subtask1 = new Item("subtasks1");
            Item subtask2 = new Item("subtasks1");
            subtask1.save();
            subtask2.save();
            ItemList itemList = testItem.getItemList();
            itemList.addItem(subtask1);
            itemList.addItem(subtask2);
            testItem.setItemList(itemList);
            testItem.save();
            Dialog.show("", "Click OK to create subtasks", "OK", "Calcel");
        }

        switch (1) {
            case 1:
                new ScreenLogin(new ScreenMain(resources)).show();
                break;
            case 2:
                new ScreenMain(resources).show();
                break;
            case 3:
                ParseQuery<Item> query2 = ParseQuery.getQuery(Item.CLASS_NAME);
                java.util.List<Item> results = null;
                try {
                    results = query2.find();
                } catch (ParseException ex) {
//                    Log.egetLogger(ScreenItemListP.class.getName()).log(Level.SEVERE, null, ex);
                    Log.e(ex);
                }
                new ScreenItemList("ItemList", new ItemList(results), null, ScreenItemList.ITEM_TYPE_ITEM).show();
                break;
        }
    }

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
    /**
     * Callback from Codename One indicating the app is minimized into
     * background
     */
    public void stop() {
        current = Display.getInstance().getCurrent();
        Log.p("stopped");
    }

    /**
     * Callback from Codename One indicating the app is exiting
     */
    public void destroy() {
        Log.p("destroyed");
    }

}
