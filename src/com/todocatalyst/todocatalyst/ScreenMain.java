/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Image;
import com.codename1.ui.StickyHeader;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
//import static com.todocatalyst.todocatalyst.DAO.TOUCHED;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.ALL_TASKS;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.INBOX;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.NEXT;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.OVERDUE;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.TODAY;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.ALARMS;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.CATEGORIES;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.COMPLETION_LOG;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.CREATION_LOG;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.LISTS;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.PROJECTS;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.STATISTICS;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.TEMPLATES;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.TOUCHED;
//import static com.todocatalyst.todocatalyst.MyForm.ScreenType.WORKSLOTS;
import static com.todocatalyst.todocatalyst.MyForm.ScreenType.*;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Thomas
 */
public class ScreenMain extends MyForm {

    //TODO change Log and Log Book so that filter and sort are not modifiable/visible
    //TODO define home screen - show Lists, Categories, Settings (or show last active screen?)
//    private Resources theme;
    private static final String SCREEN_MAIN_NAME = "TodoCatalyst";
    Container menuContainer;

    public ScreenMain(MyForm previousScreen) { //throws ParseException, IOException {
        super(SCREEN_MAIN_NAME, previousScreen, () -> {
        });
        setUniqueFormId("ScreenMain");
        String countStr = "";
        if (false) {
            int totalCount = DAO.getInstance().getItemCount(false);
            int countDone = DAO.getInstance().getItemCount(true);
            countStr = " " + (totalCount - countDone) + " [" + totalCount + "]";
        }
        setTitle("TodoCatalyst" + countStr);
        expandedObjectsInit("ScreenMain");

//        Image img = theme.getImage(item.getImageName());
//        InputStream stream = FileSystemStorage.getInstance().openInputStream("paul-gilmore-mqO0Rf-PUMs-unsplash.jpg");
//OutputStream out = Storage.getInstance().createOutputStream("MyImage");
        try {
            InputStream stream = Storage.getInstance().createInputStream("paul-gilmore-mqO0Rf-PUMs-unsplash.jpg");
            getAllStyles().setBgImage(Image.createImage(stream));
        } catch (IOException ex) {
            Log.p(ex.getMessage());
        }

//        this.theme = theme;
//        Toolbar toolbar = new Toolbar();
//        setToolbar(toolbar);
//        toolbar.setScrollOffUponContentPane(true);
//        setLayout(new BorderLayout());
//        setLayout(BoxLayout.y());
        if (true) {
//            setLayout(new BorderLayout());
//        getContentPane().setScrollableY(true);
//            menuContainer = new Container(BoxLayout.y());
            menuContainer = new ContainerScrollY(BoxLayout.y());
            menuContainer.setScrollableY(true);
            menuContainer.setAlwaysTensile(false);
            add(BorderLayout.CENTER, menuContainer);
        } else {
//            setLayout(new TableLayout(50, 2));
            menuContainer = getContentPane();
            menuContainer.setScrollableY(true);
//            contentContainer.setAlwaysTensile(false);
        }

//        addCommandsToToolbar(getToolbar(), getContentPane());//, theme);
        if (true) {
            addCommandsToToolbar(getToolbar());//, theme);
        }//        addCommandsToToolbar(new Toolbar(), getContentPane());//, theme); //new Toolbar() hack to hide the toolbar
        if (false) {
            getToolbar().setUIID("Container");
            getToolbar().hideToolbar();
        }
        setUIID("MainForm");
        getContentPane().setUIID("MainContentPane");
//        setScrollable(false); //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers

//        Style iconStyle = UIManager.getInstance().getComponentStyle("SideCommandIcon");
        if (Config.TEST) {
            Log.p("STATUSBAR: getUIManager().isThemeConstant(\"paintsTitleBarBool\", false)=="
                    + (getUIManager().isThemeConstant("paintsTitleBarBool") == null ? "<null>" : "" + getUIManager().isThemeConstant("paintsTitleBarBool")));
        }

        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//         getContentPane().removeAll();
//         buildContentPane(getContentPane());
        if (false) {
            restoreKeepPos();
        }
        setKeepPos(); //store previous scroll (to re
//        addCommandsToToolbar(getToolbar(), menuContainer);//, theme);
        buildContentPane(this, menuContainer);

        super.refreshAfterEdit();
    }

//    private void makeAndAddButtons(Command cmd, Toolbar toolbar, Container cont) {
//        makeAndAddButtons(cmd, toolbar, cont, "");
//    }
    private void makeAndAddButtonsXXX(Command cmd, Toolbar toolbar, Container cont, String helpText) {
//        Component titleButton = makeHelpButton(cmd.getCommandName(), helpText);
        Button titleButton = new MyButtonLongPress(cmd, Command.create(null, null, (e) -> {
            showToastBar(helpText);
        }));
        titleButton.setGap(Display.getInstance().convertToPixels((float) 1.5));
        titleButton.setUIID("MainMenuCommand"); //avoid any style
//        titleButton.setTextPosition(Button.RIGHT);
//        Button editButton = new Button(cmd) {
//            public void long
//        };
//        editButton.setCommand(cmd);
//        editButton.setIcon(Icons.iconEditPropertiesLabelStyle);
//        editButton.setText("");
//        editButton.setUIID("Container");
//        Container c = BorderLayout.centerEastWest(null, editButton, titleButton);
//        c.setUIID("Button");
//        c.setLeadComponent(titleButton);
        cont.add(titleButton);
    }

    private static Button makeAndAddButtons(Command cmd, String helpText) {
//        Component titleButton = makeHelpButton(cmd.getCommandName(), helpText);
        Button titleButton = new MyButtonLongPress(cmd, Command.create(null, null, (e) -> {
            showToastBar(helpText);
        }));
        titleButton.setGap(Display.getInstance().convertToPixels((float) 1.5));
        titleButton.setUIID("MainMenuCommand"); //avoid any style
//        titleButton.setTextPosition(Button.RIGHT);
//        Button editButton = new Button(cmd) {
//            public void long
//        };
//        editButton.setCommand(cmd);
//        editButton.setIcon(Icons.iconEditPropertiesLabelStyle);
//        editButton.setText("");
//        editButton.setUIID("Container");
//        Container c = BorderLayout.centerEastWest(null, editButton, titleButton);
//        c.setUIID("Button");
//        c.setLeadComponent(titleButton);
//        cont.add(titleButton);
        return titleButton;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component makeMainButton(Command cmd) {
//        return makeMainButton(cmd, "help");
//    }
//    private Component makeMainButton(Command cmd, String helpText) {
//
////        Button titleButton = makeHelpButton(cmd.getCommandName(), helpText);
//        Button titleButton = new MyButtonLongPress(cmd, Command.create(null, null, (e) -> {
//            showToastBar(helpText);
//        }));
//        titleButton.setUIID("MainMenuCommand");
////        titleButton.setTextPosition(Button.RIGHT);
//        Button editButton = new Button(cmd);
//        editButton.setCommand(cmd);
////        editButton.setIcon(Icons.iconEditPropertiesLabelStyle);
//        editButton.setMaterialIcon(Icons.iconEdit);
//        editButton.setText("");
//        editButton.setUIID("Container");
//        Container c = BorderLayout.centerEastWest(null, editButton, titleButton);
//        c.setLeadComponent(titleButton);
//        c.setUIID("Button");
////        c.setLeadComponent(titleButton);
////        cont.add(c);
//        return c;
//    }
//    private Component addMainMenuButton(Component cmd, Toolbar toolbar, Container cont, String helpText) {
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void makeAndAddMainMenuButtonOLD(Command cmd, Toolbar toolbar, Container cont, String helpText) {
//        if (toolbar != null) {
//            toolbar.addCommandToSideMenu(cmd);
//        }
////         cont.add(new Button(cmd));
////         if (icon!=null) cmd.setIcon(icon);
////        Button titleButton = new Button(cmd);
//        Component titleButton = makeHelpButton(cmd.getCommandName(), helpText);
//        titleButton.setUIID("Container"); //avoid any style
////        titleButton.setTextPosition(Button.RIGHT);
//        Button editButton = new Button();
//        editButton.setCommand(cmd);
//        editButton.setIcon(Icons.iconEditPropertiesLabelStyle);
//        editButton.setText("");
//        editButton.setUIID("Container");
//        Container c = MyBorderLayout.centerEastWest(null, editButton, titleButton);
//        c.setUIID("Button");
////        c.setLeadComponent(titleButton);
//        cont.add(c);
//    }
//</editor-fold>
    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

//        TableLayout.Constraint span2 = new TableLayout.Constraint().horizontalSpan(2).ha(Component.CENTER);
//        TableLayout.Constraint w40 = new TableLayout.Constraint().widthPercentage(40);
//        TableLayout.Constraint right = new TableLayout.Constraint().horizontalAlign(Component.RIGHT);
        TableLayout.Constraint span2 = new TableLayout.Constraint().horizontalSpan(2).ha(Component.CENTER);

        toolbar.addCommandToOverflowMenu(makeCommandNewItemSaveToInbox());
        toolbar.addCommandToOverflowMenu(makeInterruptCommand(true));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.create("MainAllSettings", ScreenSettings.SCREEN_TITLE, Icons.iconSettings, (e) -> {
//                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
            new ScreenSettings(ScreenMain.this).show();
        }
        ));
//        makeAndAddButtons(settings, toolbar, cont, ScreenSettings.SCREEN_HELP);

        if (Config.TEST) {

            toolbar.addCommandToOverflowMenu(MyReplayCommand.create("Repair", ScreenRepair.SCREEN_TITLE, Icons.iconRepair, (e) -> {
//                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
                new ScreenRepair(ScreenMain.this).show();
            }
            ));
//            makeAndAddButtons(repair, toolbar, cont, "**");
        }

//        toolbar.addCommandToOverflowMenu(MyReplayCommand.create("HomePage", "Home page"/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainWeb, (e) -> {
//Don't use Replay here, blocks the UI if restarted
        toolbar.addCommandToOverflowMenu(Command.createMaterial("Home page"/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainWeb, (e) -> {
                    Display.getInstance().execute("https://todocatalyst.com");
                }
        ));
    }

    @Override
    protected boolean isDragAndDropEnabled() {
        return true; //!optionDisableDragAndDrop && !isSortOn(); //
    }

    static public void buildContentPane(MyForm myForm, Container cont) { //, Resources theme) {

//        TableLayout.Constraint span2 = new TableLayout.Constraint().horizontalSpan(2).ha(Component.CENTER);
//        TableLayout.Constraint w40 = new TableLayout.Constraint().widthPercentage(40);
//        TableLayout.Constraint right = new TableLayout.Constraint().horizontalAlign(Component.RIGHT);
        TableLayout.Constraint span2 = new TableLayout.Constraint().horizontalSpan(2).ha(Component.CENTER);

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        Button listOfAlarmsOLD = makeAndAddButtons(MyReplayCommand.create("Alarms", ALARMS.getTitle(), ALARMS.getIcon(), ALARMS.getFont()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                (e) -> {
////                new ScreenListOfAlarms().show();
////                   if (false) 
////                    ScreenListOfAlarms.getInstance().show(ScreenMain.this);
//                    new ScreenListOfAlarms(myForm).show();
//                }
//        ), ALARMS.getHelpText());
        int nbAlarms = AlarmHandler.getInstance().getUnprocessedAlarms().size();
        Component listOfAlarms = new MainItemListButton(ALARMS.getTitle(), ALARMS.getIcon(), ALARMS.getFont(),
                "", nbAlarms > 0 ? "" + nbAlarms : "0", (e) -> {
//                    setKeepPos();
                    new ScreenListOfAlarms(myForm).show();
                }, "Alarms");
//        listOfAlarms.setUIID("Tree");
        listOfAlarms.setUIID("MainMenuButton");

//        makeAndAddButtons(listOfAlarms, toolbar, cont, "See past reminders that you have not cancelled or changed");
//        makeAndAddButtons(listOfAlarms, toolbar, cont, ScreenListOfAlarms.screenHelp);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            if (toolbar != null) {
//                toolbar.addCommandToSideMenu( MyReplayCommand.create("Test Login2"/*FontImage.create(" \ue838 ", iconStyle)*/, null,(e)->{
//                        Resources res;
////                res = Resources.getSystemResource();
////                 res = UIManager.getInstance().getSystemResource();
////                        new ScreenLogin2(TodoCatalystParse.theme).go(true);
//                        new ScreenLogin2().go(true);
//                        //                        | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
//                    })
//                });
//            }
//            makeAndAddButtons(listOfAlarms, toolbar, cont);
//        }
//</editor-fold>
//        Command overdue = MyReplayCommand.create(SCREEN_OVERDUE_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainOverdueCust, Icons.myIconFont, (e) -> {
//        Button overdue = makeAndAddButtons(MyReplayCommand.create("Overdue", OVERDUE.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                OVERDUE.getIcon(), OVERDUE.getFont(), (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_DUE_DATE,
////                            FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, true, false); //FilterSortDef.FILTER_SHOW_DONE_TASKS
////                    new ScreenListOfItems(SCREEN_OVERDUE_TITLE, () -> new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterSort, true), ScreenMain.this, (i) -> {
////                    new ScreenListOfItems(SCREEN_OVERDUE_TITLE, () -> new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getNamedItemList(DAO.OVERDUE, SCREEN_OVERDUE_TITLE), filterSort, true), ScreenMain.this, (i) -> {
////                    new ScreenListOfItems(OVERDUE, "No overdue tasks the last " + MyPrefs.overdueLogInterval.getInt() + " days",
//            new ScreenListOfItems(OVERDUE,
//                    //                            () -> DAO.getInstance().getNamedItemList(DAO.OVERDUE, SCREEN_OVERDUE_TITLE, filterSort),
//                    //                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_OVERDUE, OVERDUE.getTitle(),
//                    //                            DAO.getInstance().getSystemFilterSortFromParse(OVERDUE.name(), ItemList.getSystemDefaultFilter(OVERDUE))),
//                    () -> DAO.getInstance().getNamedItemList(OVERDUE),
//                    myForm, null,
//                    OVERDUE.getOptions()
//            ).show();
//            //                        | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
//        }
//        ), OVERDUE.getHelpText());
//        makeAndAddButtons(overdue, toolbar, cont, SCREEN_OVERDUE_HELP);
//        Button today = makeAndAddButtons(MyReplayCommand.create("Today", TODAY.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                TODAY.getIcon(), TODAY.getFont(), (e) -> {
//            //TODO!!!!! FilterSort currently works on Items, but today view also show workslots                    
////                    FilterSortDef filterSort = null; //new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false); //FilterSortDef.FILTER_SHOW_DONE_TASKS
////                    MyForm myForm = new ScreenListOfItems(SCREEN_TODAY_TITLE, "Looks like you have no tasks to deal with today. Enjoy!",
//            MyForm myForm2 = new ScreenListOfItems(TODAY,
//                    //                            () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getTodayDueAndOrWaitingOrWorkSlotsItems(true, true), filterSort, true),
//                    //                            () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getToday(), filterSort, true),
//                    //                            () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getNamedItemList(DAO.TODAY, SCREEN_TODAY_TITLE), filterSort, true),
////                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_TODAY, TODAY.getTitle(), null),
//                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_TODAY),
//                    myForm, null,
//                    //                new ScreenListOfItems(SCREEN_TODAY_TITLE, new ItemList(DAO.getInstance().getTodayDueAndOrWaitingOrWorkSlotsItems(false, false), true), ScreenMain.this, (i) -> {
//                    //                        ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP
//                    //                        new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS, false), //FilterSortDef.FILTER_SHOW_DONE_TASKS
//                    TODAY.getOptions(), (i) -> null /*prevent stickyHeader*/);
////                    myForm.setTextToShowIfEmptyList("Looks like you have no tasks today. Enjoy!");
//            myForm2.show();
//        }
//        ), TODAY.getHelpText());
//        makeAndAddButtons(today, toolbar, cont, SCREEN_TODAY_HELP);
        //TODO!!! add support for help text on these commands
//        Button next = makeAndAddButtons(MyReplayCommand.create("Next", NEXT.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/, NEXT.getIcon(), NEXT.getFont(), (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS
////                            + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false, false);
////                    MyForm myForm = new ScreenListOfItems(SCREEN_NEXT_TITLE, () -> new ItemList(SCREEN_NEXT_TITLE, DAO.getInstance().getCalendar(), filterSort, true), ScreenMain.this, (i) -> {
////                    MyForm myForm = new ScreenListOfItems(SCREEN_NEXT_TITLE, () -> new ItemList(SCREEN_NEXT_TITLE, DAO.getInstance().getNamedItemList(DAO.NEXT, SCREEN_NEXT_TITLE), filterSort, true), 
////                    MyForm myForm = new ScreenListOfItems(SCREEN_NEXT_TITLE, "No tasks due the next " + MyPrefs.nextInterval.getInt() + " days",
//            MyForm myForm2 = new ScreenListOfItems(NEXT,
//                    //                            () -> DAO.getInstance().getNamedItemList(DAO.NEXT, SCREEN_NEXT_TITLE, filterSort),
//                    //                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_NEXT, NEXT.getTitle(),
//                    //                            DAO.getInstance().getSystemFilterSortFromParse(NEXT.name(), ItemList.getSystemDefaultFilter(NEXT))),
//                    () -> DAO.getInstance().getNamedItemList(NEXT),
//                    myForm, null, NEXT.getOptions()
//            );
////                    myForm.setShowIfEmptyList("No tasks the next month");
//            myForm2.show();
//        }
//        ), NEXT.getHelpText());
//        makeAndAddButtons(next, toolbar, cont, SCREEN_NEXT_HELP);
//        Command inbox = MyReplayCommand.create(SCREEN_INBOX_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                    new ScreenListOfItems(SCREEN_INBOX_TITLE, () -> new ItemList(SCREEN_INBOX_TITLE, DAO.getInstance().getAllItemsWithoutOwners(), true), ScreenMain.this, (i) -> {
//                    }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
//                }
//        );
//        Button inbox = makeAndAddButtons(MyReplayCommand.create("Inbox", INBOX.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                INBOX.getIcon(), INBOX.getFont(), (e) -> {
////                    new ScreenListOfItems(SCREEN_INBOX_TITLE, () -> new ItemList(SCREEN_INBOX_TITLE, Inbox.getInstance(), true), ScreenMain.this, (i) -> {
//            MyForm myForm2 = new ScreenListOfItems(INBOX, () -> Inbox.getInstance(), myForm, null,
//                    INBOX.getOptions());
////                    myForm.setShowIfEmptyList("Your Inbox is empty. Add tasks using (+)"); //NO, show inline cont in Inbox
//            myForm2.show();
//        }
//        ), INBOX.getHelpText());
//        makeAndAddButtons(inbox, toolbar, cont, SCREEN_INBOX_HELP);
//        Button lists = makeAndAddButtons(MyReplayCommand.create("AllLists", LISTS.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                LISTS.getIcon(), LISTS.getFont(), (e) -> {
////                new ScreenListOfItemLists("Lists", new ItemList(DAO.getInstance().getAllItemLists()), ScreenMain.this, (i)->{}).show();                     //null: do nothing, lists are saved if edited
////                new ScreenListOfItemLists(SCREEN_LISTS_TITLE, DAO.getInstance().getAllItemLists(), ScreenMain.this, (i) -> {
////                    new ScreenListOfItemLists(SCREEN_LISTS_TITLE, DAO.getInstance().getItemListList(), ScreenMain.this, (i) -> {
////                    new ScreenListOfItemLists(ScreenType.LISTS.getTitle(), ItemListList.getInstance(), myForm, null, ScreenType.LISTS.getHelpText()).show();                     //null: do nothing, lists are saved if edited
//            new ScreenListOfItemLists(ItemListList.getInstance(), myForm, null).show();                     //null: do nothing, lists are saved if edited
//        }
//        ), LISTS.getHelpText());
//        makeAndAddButtons(lists, toolbar, cont, SCREEN_LISTS_HELP);
//        Button categories = makeAndAddButtons(MyReplayCommand.create("Categories", CATEGORIES.getTitle(), /*FontImage.create(" \ue838 ", iconStyle)*/
//                CATEGORIES.getIcon(), CATEGORIES.getFont(), (e) -> {
////                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
////                new ScreenListOfCategories(DAO.getInstance().getAllCategories(), ScreenMain.this, (i)->{}).show();
//            new ScreenListOfCategories(CategoryList.getInstance(), myForm, null).show();
//        }
//        ), CATEGORIES.getHelpText());
//        makeAndAddButtons(categories, toolbar, cont, ScreenListOfCategories.SCREEN_HELP);
//        if (true || Config.TEST) {
//        Button allTasks = makeAndAddButtons(MyReplayCommand.create("AllTasks", ALL_TASKS.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                ALL_TASKS.getIcon(), ALL_TASKS.getFont(), (e) -> {
////            FilterSortDef allTasksSystemFilter = DAO.getInstance().getSystemFilterSortFromParse(ALL_TASKS.toString(), FilterSortDef.getDefaultItemListFilter());
//            new ScreenListOfItems(ALL_TASKS,
//                    //                    () -> new ItemList(ALL_TASKS.getTitle(), DAO.getInstance().getAllItems(false, false, true, false, false), allTasksSystemFilter, true),
//                    () -> DAO.getInstance().getNamedItemList(ALL_TASKS),
//                    myForm, null, ALL_TASKS.getOptions()).show();
//        }
//        ), ALL_TASKS.getHelpText());
//            makeAndAddButtons(allTasks, toolbar, cont, SCREEN_ALL_TASKS_HELP);
//        }
//        Button projects = makeAndAddButtons(MyReplayCommand.create("AllProjects", ALL_PROJECTS.getTitle(),
//                ALL_PROJECTS.getIcon(), ALL_PROJECTS.getFont(), (e) -> {
//            MyForm myForm2 = new ScreenListOfItems(ALL_PROJECTS, "No projects", () -> new ItemList(DAO.getInstance().getAllProjects()),
//                    myForm, null,
//                    ALL_PROJECTS.getOptions()
//            );
////            myForm.setShowIfEmptyList("You don't have any projects");
//            myForm2.show();
//        }
//        ), ALL_PROJECTS.getHelpText());
//<editor-fold defaultstate="collapsed" desc="comment">
//        makeAndAddButtons(projects, toolbar, cont, SCREEN_PROJECTS_HELP);
//        MyReplayCommand workSlots = new MyReplayCommand(ScreenListOfWorkSlots.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//        Button workSlotsOLDXXX = makeAndAddButtons((MyReplayCommand) MyReplayCommand.create("WorkSlots", WORKSLOTS.getTitle(),
//                WORKSLOTS.getIcon(), WORKSLOTS.getFont(), (e) -> {
////                super.actionPerformed(e);
////            new ScreenListOfWorkSlots("", DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())), null, ScreenMain.this, (i) -> {
////            new ScreenListOfWorkSlots("", DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())), null, ScreenMain.this, (i) -> {
////if (true){
////            ItemList tempWorkSlotOwnerList = new ItemList("Future " + WorkSlot.WORKSLOT + "s", true);
//            ItemList tempWorkSlotOwnerList = new ItemList("", true);
////            tempWorkSlotOwnerList.setWorkSlotList(new WorkSlotList(tempWorkSlotOwnerList, DAO.getInstance().getWorkSlots(new MyDate(MyDate.currentTimeMillis())), true));
////            tempWorkSlotOwnerList.setWorkSlotsInParse(DAO.getInstance().getWorkSlotsAsItemList(new MyDate(MyDate.currentTimeMillis())));
//            tempWorkSlotOwnerList.setWorkSlotsInParse(DAO.getInstance().getActiveWorkSlotsAsItemList());
////} else{
////WorkSlotList tempWorkSlotList = new WorkSlotList(DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())));
////}
////            new ItemList().setWorkSlotList(DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())));
////            new ScreenListOfWorkSlots(tempWorkSlotOwnerList, ScreenMain.this, null, (obj) -> DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())).getWorkSlots(), true).show();
////            new ScreenListOfWorkSlots(tempWorkSlotOwnerList, ScreenMain.this, null, (obj) -> DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())), true).show();
////            new ScreenListOfWorkSlots(tempWorkSlotOwnerList, ScreenMain.this, null,
//            new ScreenListOfWorkSlots(tempWorkSlotOwnerList, myForm,
//                    () -> {/*no need to removeFromCache workslotlist from DAO since it will be updated within the screen in a consistent way with the parse server list */
//                    }, true, false).show();
//        }
//        ), ScreenType.WORKSLOTS.getHelpText());
//        workSlotsOLDXXX.setUIID("MainMenuButton");
//
//        Component workSlotsXXX = new MainItemListButton(WORKSLOTS.getTitle(), WORKSLOTS.getIcon(), WORKSLOTS.getFont(),
//                "", "", (e) -> {
//                    ItemList tempWorkSlotOwnerList = new ItemList("", true);
////                    tempWorkSlotOwnerList.setWorkSlotsInParse(DAO.getInstance().getWorkSlotsAsItemList(new MyDate(MyDate.currentTimeMillis()), true));
//                    tempWorkSlotOwnerList.setWorkSlotsInParse(DAO.getInstance().getActiveWorkSlotsAsItemList());
//                    new ScreenListOfWorkSlots(tempWorkSlotOwnerList, myForm,
//                            () -> {
//                            }, true, false).show();
//                }, "WorkSlots");
////        makeAndAddButtons(workSlots, toolbar, cont, ScreenListOfWorkSlots.SCREEN_HELP);
//
//        Button templatesXXX = makeAndAddButtons(MyReplayCommand.create("Templates", TEMPLATES.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                TEMPLATES.getIcon(), TEMPLATES.getFont(), (e) -> {
////                new ScreenListOfItems("Templates", new ItemList(DAO.getInstance().getAllTemplates()), ScreenMain.this, (i) -> {}, null, false, true).show();
////                    new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, DAO.getInstance().getTemplateList(), ScreenMain.this, (i) -> {
//            new ScreenListOfItems(TEMPLATES, "No templates defined", () -> TemplateList.getInstance(), myForm, null,
//                    TEMPLATES.getOptions() // | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
//            ).show();
////                new ScreenListOfItems("Templates", new ItemList(DAO.getInstance().getAllTemplates()), ScreenMain.this, (i) -> {
////                }).show();
//        }
//        ), TEMPLATES.getHelpText());
////        makeAndAddButtons(templates, toolbar, cont, SCREEN_TEMPLATES_HELP);
//
//        //ACHIEVEMENTS
//        //TODO!!! add support for help text on these commands
//        Button statisticsOLD = makeAndAddButtons(MyReplayCommand.create("Statistics", STATISTICS.getTitle()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                STATISTICS.getIcon(), STATISTICS.getFont(), (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, false);
//            MyForm myForm2 = new ScreenStatistics2(myForm, () -> {
//            });
////                    myForm.setTextToShowIfEmptyList("No completed tasks to show statistics for yet");
//            myForm2.show();
//        }
//        ), STATISTICS.getHelpText());
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        Component statistics = new MainItemListButton(STATISTICS.getTitle(), STATISTICS.getIcon(), STATISTICS.getFont(),
//                "", "", (e) -> new ScreenStatistics2(myForm, () -> {
//                }).show(), "Statistics");
//        
//        statisticsOLD.setUIID("MainMenuButton");
////        makeAndAddButtons(statistics, toolbar, cont, SCREEN_STATISTICS);
//
//        //ACHIEVEMENTS
//        //TODO!!! add support for help text on these commands
//        if (false) {
//            Button improve = makeAndAddButtons(MyReplayCommand.create("Improve", SCREEN_IMPROVE/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainImprove, (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, false);
////                    MyForm myForm = new ScreenImprove(SCREEN_IMPROVE, ScreenMain.this, () -> {
////                    });
////                    myForm.setTextToShowIfEmptyList("No completed tasks to show statistics for yet");
////                    myForm.show();
//                    }
//            ), SCREEN_IMPROVE_HELP);
//        }
////        makeAndAddButtons(improve, toolbar, cont, SCREEN_IMPROVE_HELP);
//
//        //Log
//        //TODO!!! add support for help text on these commands
////        Button completionLog = makeAndAddButtons(MyReplayCommand.create("Completion",ScreenType.COMPLETION_LOG.getTitle(), Icons.iconMainCompletionLog, (e) -> {
//        Button completionLog = makeAndAddButtons(MyReplayCommand.create("Completion", COMPLETION_LOG.getTitle(),
//                COMPLETION_LOG.getIcon(), COMPLETION_LOG.getFont(), (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, false, false);
////            FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_ALL, true, false); //showAll enough since query only gets done tasks
////                    MyForm myForm = new ScreenListOfItems(SCREEN_COMPLETION_LOG_TITLE, () -> new ItemList(SCREEN_COMPLETION_LOG_TITLE, DAO.getInstance().getCompletionLog(), filterSort, true), ScreenMain.this, (i) -> {
////                    MyForm myForm = new ScreenListOfItems(SCREEN_COMPLETION_LOG_TITLE, () -> new ItemList(SCREEN_COMPLETION_LOG_TITLE, DAO.getInstance().getNamedItemList(DAO.LOG, SCREEN_COMPLETION_LOG_TITLE), filterSort, true),
////            MyForm myForm = new ScreenListOfItems(SCREEN_COMPLETION_LOG_TITLE, "No tasks completed the last " + MyPrefs.completionLogInterval.getInt() + " days",
//            MyForm myForm2 = new ScreenListOfItems(COMPLETION_LOG,
//                    //                    () -> DAO.getInstance().getNamedItemList(DAO.LOG, SCREEN_COMPLETION_LOG_TITLE, filterSort),
////                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_LOG, COMPLETION_LOG.getTitle(), ItemList.getSystemDefaultFilter(COMPLETION_LOG)),
//                    () -> DAO.getInstance().getNamedItemList(SYSTEM_LIST_LOG),
//                    myForm, null,
//                    COMPLETION_LOG.getOptions()
//            );
////            myForm.setTextToShowIfEmptyList("No completed tasks the last month");
//            myForm2.show();
//        }
//        ), COMPLETION_LOG.getHelpText());
////        makeAndAddButtons(completionLog, toolbar, cont, SCREEN_COMPLETION_LOG_HELP);
//
//        //diary
////        Button creationLog = makeAndAddButtons(MyReplayCommand.create("Creation", ScreenType.CREATION_LOG.getTitle(), Icons.iconMainCreationLog, (e) -> {
//        Button creationLogXXX = makeAndAddButtons(MyReplayCommand.create("Creation", CREATION_LOG.getTitle(), CREATION_LOG.getIcon(), CREATION_LOG.getFont(), (e) -> {
////            FilterSortDef filterSort = new FilterSortDef(Item.PARSE_CREATED_AT, FilterSortDef.FILTER_SHOW_ALL, true, false);
////                    MyForm myForm = new ScreenListOfItems(SCREEN_CREATION_LOG_TITLE, () -> new ItemList(SCREEN_CREATION_LOG_TITLE, DAO.getInstance().getCreationLog(), filterSort, true), ScreenMain.this, (i) -> {
////            MyForm myForm = new ScreenListOfItems(SCREEN_CREATION_LOG_TITLE, "No tasks created the last " + MyPrefs.creationLogInterval.getInt() + " days",
//            MyForm myForm2 = new ScreenListOfItems(CREATION_LOG,
//                    //                    () -> DAO.getInstance().getNamedItemList(DAO.DIARY, SCREEN_CREATION_LOG_TITLE, filterSort),
////                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_DIARY, SCREEN_CREATION_LOG_TITLE, ItemList.getSystemDefaultFilter(CREATION_LOG)),
//                    () -> DAO.getInstance().getNamedItemList(SYSTEM_LIST_DIARY),
//                    myForm, null,
//                    CREATION_LOG.getOptions()
//            );
////            myForm.setTextToShowIfEmptyList("No tasks created the last month");
//            myForm2.show();
//        }
//        ), ScreenType.COMPLETION_LOG.getHelpText());
////        makeAndAddButtons(creationLog, toolbar, cont, SCREEN_CREATION_LOG_HELP);
//
////        Button touched = makeAndAddButtons(MyReplayCommand.create("Touched", TOUCHED.getTitle(), TOUCHED.getIcon(), (e) -> {
//        Button touched = makeAndAddButtons(MyReplayCommand.create("Touched", TOUCHED.getTitle(), TOUCHED.getIcon(), (e) -> {
////                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_UPDATED_AT, FilterSortDef.FILTER_SHOW_ALL, true, false); //true => show most recent first
////                    MyForm myForm = new ScreenListOfItems(SCREEN_TOUCHED, () -> new ItemList(SCREEN_TOUCHED, DAO.getInstance().getTouchedLog(), filterSort, true), ScreenMain.this, (i) -> {
////                    MyForm myForm = new ScreenListOfItems(SCREEN_TOUCHED, "No tasks changed the last " + MyPrefs.touchedLogInterval.getInt() + " days",
//            MyForm myForm2 = new ScreenListOfItems(TOUCHED,
//                    //                            () -> new ItemList(SCREEN_TOUCHED, DAO.getInstance().getNamedItemList(DAO.TOUCHED, SCREEN_TOUCHED), filterSort, true),
//                    //                            () -> DAO.getInstance().getNamedItemList(DAO.TOUCHED, SCREEN_TOUCHED, filterSort),
////                    () -> DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_TOUCHED, SCREEN_TOUCHED_TITLE, ItemList.getSystemDefaultFilter(TOUCHED)),
//                    () -> DAO.getInstance().getNamedItemList(SYSTEM_LIST_TOUCHED),
//                    myForm, null,
//                    TOUCHED.getOptions()
//            );
////                    myForm.setTextToShowIfEmptyList("No tasks have been changed the last month");
//            myForm2.show();
//        }
//        ), TOUCHED.getHelpText());
////        touched.getAllStyles().setBgColor(TOUCHED.getColor());
//        if (false) {
//            setIconLabelColor(touched, TOUCHED.getColor());
//            touched.setMaterialIcon(TOUCHED.getIcon());
//        }
////        makeAndAddButtons(touched, toolbar, cont, SCREEN_TOUCHED_HELP);
//
//        if (false && Config.TEST) {
//            Button touched24h = makeAndAddButtons(MyReplayCommand.create("Touched24h", SCREEN_TOUCHED_24H/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainTouched, (e) -> {
//                        FilterSortDef filterSort = new FilterSortDef(Item.PARSE_UPDATED_AT, FilterSortDef.FILTER_SHOW_ALL, true, false); //true => show most recent first
//                        new ScreenListOfItems(SCREEN_TOUCHED_24H, "No tasks changed the last 24 hours", () -> new ItemList(SCREEN_TOUCHED_24H,
//                        DAO.getInstance().getTouched24hLog(), filterSort, true), myForm, null,
//                                ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                                | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_WORK_TIME
//                                | ScreenListOfItems.OPTION_NO_NEW_FROM_TEMPLATE | ScreenListOfItems.OPTION_NON_EDITABLE_LIST
//                        ).show();
//                    }
//            ), "");
////            makeAndAddButtons(touched24h, toolbar, cont, "**");
//        }
//
//        if (false && Config.TEST) {
//            Button allTasksWithoutOwner = makeAndAddButtons(MyReplayCommand.create("Tasks without owner**"/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                        new ScreenListOfItems("Tasks without owner**",
//                                () -> new ItemList("Tasks without owner", DAO.getInstance().getAllItems(false, false, true, false), true),
//                                myForm, null, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
//                    }
//            ), "");
////            makeAndAddButtons(allTasksWithoutOwner, toolbar, cont, "**");
//        }
//        if (false) {
//            Button tutorial = makeAndAddButtons(MyReplayCommand.create("Tutorial", SCREEN_TUTORIAL/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainTutorial, (e) -> {
//                        new ScreenListOfItems(SCREEN_TUTORIAL, () -> new ItemList(SCREEN_TUTORIAL, DAO.getInstance().getAllItems(), true), myForm, null,
//                                ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
//                    }
//            ), SCREEN_TUTORIAL_HELP);
//        }
////        makeAndAddButtons(tutorial, toolbar, cont, SCREEN_TUTORIAL_HELP);
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            Command editRepeatRule = new MyReplayCommand("Edit RepeatRule2"/*FontImage.create(" \ue838 ", iconStyle)*/,null,(e)->{
////                    RepeatRuleParseObject repeatRule = new RepeatRuleParseObject();
////                    repeatRule.setSpecifiedStartDate(new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 48).getTime());
////                    new ScreenRepeatRule("test", repeatRule, new Item("taskX", 15, new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 24)), ScreenMain.this, () -> {
////                    }, true, new Date()).show();
////                }
////            );
////            makeAndAddButtons(editRepeatRule, toolbar, cont, "**");
////        }
////        if (false) {
////            Command testRepeatRule = new MyReplayCommand("Test RepeatRule"/*FontImage.create(" \ue838 ", iconStyle)*/) {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
////                    RepeatRuleParseObject repeatRule = new RepeatRuleParseObject();
////                    repeatRule.testRepeatRules();
////                }
////            };
////            makeAndAddButtons(testRepeatRule, toolbar, cont, "**");
////        }
////</editor-fold>
//        if (false) {
//            Button inspirationLists = makeAndAddButtons(MyReplayCommand.create("Inspiration", ScreenInspirationalLists.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, Icons.iconMainInspirationLists, (e) -> {
//                        new ScreenInspirationalLists(myForm).show();
//                    }
//            ), "");
//        }
////        makeAndAddButtons(inspirationLists, toolbar, cont, "**");
////<editor-fold defaultstate="collapsed" desc="comment">
//
////        if (false) {
////            Command statisticsList = new MyReplayCommand(ScreenStatisticsLists.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
////                        new ScreenStatisticsLists(ScreenMain.this).show();
////                    }
////            );
////            if (toolbar != null) {
////                toolbar.addCommandToSideMenu(statisticsList);
////            }
////            cont.add(new Button(statisticsList));
////        }
////</editor-fold>
//        if (false && Config.TEST) {
////        Command cleanTemplates = MyReplayCommand.create("Clean up templates", Icons.get().iconSettingsLabelStyle, (e) -> {
//            Button cleanTemplates = makeAndAddButtons(Command.createMaterial("Clean up templates", Icons.iconSettings, (e) -> {
//                DAO.getInstance().cleanUpTemplateListInParse(true);
//            }
//            ), "");
////            makeAndAddButtons(cleanTemplates, toolbar, cont, "**");
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            cont.add(listOfAlarms);
//            cont.add(overdue);
//
//            cont.add(span2, today);
//
//            cont.add(next);
//            cont.add(inbox);
//
//            cont.add(lists);
//            cont.add(categories);
//
//            cont.add(span2, workSlotsXXX);
//            cont.add(span2, statistics);
//
//            cont.add(completionLog);
//            cont.add(creationLog);
//
//            cont.add(allTasks);
//            cont.add(projects);
//
//            cont.add(templates);
//            cont.add(touched);
//        } else if (false) {
//            cont.addAll(
//                    GridLayout.encloseIn(2, listOfAlarms, overdue),
//                    GridLayout.encloseIn(1, today),
//                    GridLayout.encloseIn(2, next, inbox),
//                    GridLayout.encloseIn(2, lists, categories),
//                    GridLayout.encloseIn(1, workSlotsXXX),
//                    GridLayout.encloseIn(1, statistics),
//                    GridLayout.encloseIn(2, completionLog, creationLog),
//                    GridLayout.encloseIn(2, allTasks, projects),
//                    GridLayout.encloseIn(2, templates, touched));
//        } else if (false) {
//            cont.add(ComponentGroup.enclose(new Label("Work"),
//                    GridLayout.encloseIn(2, listOfAlarms, overdue),
//                    GridLayout.encloseIn(1, today)));
//            cont.addAll(
//                    GridLayout.encloseIn(2, next, inbox),
//                    GridLayout.encloseIn(2, lists, categories),
//                    GridLayout.encloseIn(1, workSlotsXXX),
//                    GridLayout.encloseIn(1, statistics),
//                    GridLayout.encloseIn(3, creationLog, touched, completionLog),
//                    GridLayout.encloseIn(2, allTasks, projects),
//                    GridLayout.encloseIn(1, templates));
//        } else if (false) {
//            ComponentGroup compGrp1 = ComponentGroup.enclose(new Label("PLAN"), //"Prepare"
//                    GridLayout.encloseIn(1, inbox),
//                    GridLayout.encloseIn(2, lists, categories),
//                    GridLayout.encloseIn(2, workSlotsXXX, templates)
//            );
//            ComponentGroup compGrp2 = ComponentGroup.enclose(new Label("DO"), //"Execute"
//                    GridLayout.encloseIn(2, listOfAlarms, overdue),
//                    GridLayout.encloseIn(2, today, next)
//            );
//            ComponentGroup compGrp3 = ComponentGroup.enclose(new Label("CHECK"), //"Review"
//                    GridLayout.encloseIn(1, statistics),
//                    GridLayout.encloseIn(1, completionLog),
//                    GridLayout.encloseIn(2, creationLog, touched),
//                    GridLayout.encloseIn(2, allTasks, projects)
//            );
//            compGrp1.setElementUIID("MainMenu");
//            compGrp2.setElementUIID("MainMenu");
//            compGrp3.setElementUIID("MainMenu");
//            compGrp1.setUIID("MainMenuGroup");
//            compGrp2.setUIID("MainMenuGroup");
//            compGrp3.setUIID("MainMenuGroup");
//            cont.add(compGrp1);
//            cont.add(compGrp2);
//            cont.add(compGrp3);
//        } else if (true) {
//</editor-fold>
        cont.removeAll();
//            cont.add(new ExpandableContainer("Plan",inbox, lists, categories, workSlots, templates));
//            cont.add(new ExpandableContainer("Do",listOfAlarms, overdue,today, next));
//            cont.add(new ExpandableContainer("Check",statistics, completionLog, creationLog, touched, allTasks, projects));
//            cont.addAll(new StickyHeader("Plan","MainStickyHeader",true),inbox, lists, categories, workSlots, templates);
        cont.addAll(new StickyHeader("Plan", "MainStickyHeader", true),
                //                    ScreenListOfItemLists.buildItemListContainer(ItemListList.getInstance(), null,false,expandedObjects),
                //                    new MyTree2(this, Inbox.getInstance(), true, false),
                new MyTree2(myForm, Inbox.getInstance(), true),
                //                    new MyTree2(this, ItemListList.getInstance(), true, false),
                new MyTree2(myForm, ItemListList.getInstance()),
                //                    new MyTree2(this, CategoryList.getInstance(), true, false),
                new MyTree2(myForm, CategoryList.getInstance()),
                //                    new MyTree2(this, DAO.getInstance().getWorkSlots(new MyDate(MyDate.currentTimeMillis())), true, false),
                //                    workSlots,
                //                    new MyTree2(myForm, DAO.getInstance().getWorkSlots(new MyDate(), true)),
                //                    new MyTree2(myForm, DAO.getInstance().getActiveWorkSlotsAsItemList()),
//                new MyTree2(myForm, DAO.getInstance().getNamedItemList(WORKSLOTS)),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(WORKSLOTS), null,true,false,false,true),
                //                    new MyTree2(this, TemplateList.getInstance(), true, false) //,                    inbox, lists, categories, workSlots, templates);
                new MyTree2(myForm, TemplateList.getInstance(), null, true, false, false, true) //,                    inbox, lists, categories, workSlots, templates);
        );

        cont.addAll(new StickyHeader("Do", "MainStickyHeader", true),
                //                    listOfAlarms, overdue, 
                //                    today, next,
                listOfAlarms,
                //                    new MyTree2(this,
                //                            AlarmHandler.getInstance().getExpiredAlarmsItemList(),
                //                            true, false),
                //                    next,
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(OVERDUE), true, false),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(OVERDUE), true),
                //                    new MyTree2(myForm, DAO.getInstance().getNamedItemList(TODAY), true, false),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(TODAY), true),
                //                    new Button("Timer",Icons.iconLaunchTimer ,"MainMenuButton"),
                new MainItemListButton("Timer", Icons.iconTimerLaunch, null,
                        "", TimerStack2.getActiveTimers() != null ? "" + TimerStack2.getActiveTimers().size() : "",
                        (e)
                        -> new ScreenTimer7(myForm)
                                .show(),
                        "MainMenuLaunchTimer"),
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(NEXT), true, false)
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(NEXT))
        );
//            cont.addAll(new StickyHeader("Do", "MainStickyHeader", true), 
//                    new MyTree2(this, AlaInbox.getInstance(),  true, false),
//                    new MyTree2(this,DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_OVERDUE, OVERDUE.getTitle(),
//                            DAO.getInstance().getSystemFilterSortFromParse(OVERDUE.name(), ItemList.getSystemDefaultFilter(OVERDUE))),  true, false),
//                    listOfAlarms, overdue, today, next);

        cont.addAll(new StickyHeader("Check", "MainStickyHeader", true),
                new MainItemListButton(STATISTICS.getTitle(), STATISTICS.getIcon(), STATISTICS.getFont(),
                        "", ""+DAO.getInstance().getNamedItemList(DAO.SYSTEM_LIST_STATISTICS).size(), (e) -> new ScreenStatistics2(myForm, () -> {
                }).show(), "Statistics"),
                //                    completionLog, creationLog, touched, 
                //                    allTasks, projects,
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(COMPLETION_LOG), true, false),
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(CREATION_LOG), true, false),
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(TOUCHED), true, false),
//                new MyTree2(myForm, DAO.getInstance().getNamedItemList(COMPLETION_LOG), true),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(COMPLETION_LOG), null,true,false,false,true),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(CREATION_LOG), true),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(EDITED), true),
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(ALL_TASKS),  true, false),
                //                    new MyTree2(this, new ItemList(ALL_TASKS.getTitle(), DAO.getInstance().getAllItems(false, false, true, false, false), DAO.getInstance().getSystemFilterSortFromParse(ALL_TASKS.toString(), FilterSortDef.getDefaultFilter()), true), true, false),
                //                    new MyTree2(this, new ItemList(ALL_TASKS.getTitle(), DAO.getInstance().getAllItems(false, false, true, false, false), DAO.getInstance().getSystemFilterSortFromParse(ALL_TASKS.toString(), FilterSortDef.getDefaultFilter()), true)),
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(ALL_TASKS), true),
                //                    new MyTree2(this, DAO.getInstance().getNamedItemList(ALL_PROJECTS),  true, false)
                //                    new MyTree2(this, new ItemList(ALL_PROJECTS.getTitle(), DAO.getInstance().getAllProjects(), true), true, false)
                //                    new MyTree2(this, new ItemList(ALL_PROJECTS.getTitle(), DAO.getInstance().getAllProjects(), true))
                new MyTree2(myForm, DAO.getInstance().getNamedItemList(ALL_PROJECTS), true)
        );
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else {
////            ItemList plan = new ItemList(Arrays.asList(Inbox.getInstance(), ItemListList.getInstance(), CategoryList.getInstance()), false);
//            Button listOfAlarms2 = makeAndAddButtons(MyReplayCommand.create("Alarms", ALARMS.getTitle(), ALARMS.getIcon(), ALARMS.getFont()/*FontImage.create(" \ue838 ", iconStyle)*/,
//                    (e) -> {
////                new ScreenListOfAlarms().show();
////                   if (false)
////                        ScreenListOfAlarms.getInstance().show(myForm);
//                        new ScreenListOfAlarms(myForm).show();
//                    }
//            ), ALARMS.getHelpText());
//            cont.addAll(new ExpandableContainer("Plan",
//                    BoxLayout.encloseY(
//                            //                            ScreenListOfItemLists.buildItemListContainer(Inbox.getInstance(), null),
//                            inbox,
//                            ScreenListOfItemLists.buildItemListContainer(myForm, ItemListList.getInstance()),
//                            ScreenListOfItemLists.buildItemListContainer(myForm, CategoryList.getInstance()),
//                            ScreenListOfItemLists.buildItemListContainer(myForm, TemplateList.getInstance())
//                    )),
//                    new ExpandableContainer("Do",
//                            BoxLayout.encloseY(
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, Inbox.getInstance()),
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, ItemListList.getInstance()),
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, CategoryList.getInstance())
//                            )),
//                    new ExpandableContainer("Check",
//                            BoxLayout.encloseY(
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, Inbox.getInstance()),
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, ItemListList.getInstance()),
//                                    ScreenListOfItemLists.buildItemListContainer(myForm, CategoryList.getInstance())
//                            )));
//        }
//        makeAndAddButtons(homePage, toolbar, cont, "**");
//</editor-fold>

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private ItemList generateListOfListsXXX() {
//        HashMap<ItemList, ActionListener> launchScreen = new HashMap(); //actions to launch edit Event ('>') on specific list
//        ItemList list = new ItemList(); //list of lists to show in home screen
//        ItemList next; //list of lists to show in home screen
////        FilterSortDef filterSortDef;
//
//        //OVERDUE
//        FilterSortDef filterOverdue = new FilterSortDef(Item.PARSE_DUE_DATE,
//                FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, true); //FilterSortDef.FILTER_SHOW_DONE_TASKS
//        next = new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterOverdue, true);
//        list.add(next);
//        launchScreen.put(next, (e) -> {
//            new ScreenListOfItems(SCREEN_OVERDUE_TITLE, () -> {
//                return new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterOverdue, true);
//            }, this, (i) -> {
//            },
//                    ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_WORK_TIME
//            ).show();
//        });
//
//        //TODAY
//        FilterSortDef filterToday = new FilterSortDef(FilterSortDef.FILTER_SORT_TODAY_VIEW, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false); //FilterSortDef.FILTER_SHOW_DONE_TASKS
//        next = new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterToday, true);
//        list.add(next);
//        launchScreen.put(next, (e) -> {
//            new ScreenListOfItems(SCREEN_TODAY_TITLE, () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getTodayDueAndOrWaitingOrWorkSlotsItems(true, true), filterToday, true), ScreenMain.this, (i) -> {
//            },
//                    ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                    | ScreenListOfItems.OPTION_NO_WORK_TIME).show();
//        });
//
//        return list;
//    }
//</editor-fold>
//    public void show() {
//        super.show();
//    }
}
