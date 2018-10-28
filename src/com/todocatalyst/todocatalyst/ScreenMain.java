/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.util.Resources;
import static com.todocatalyst.todocatalyst.MyForm.SCREEN_OVERDUE_TITLE;
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author Thomas
 */
public class ScreenMain extends MyForm {

    //TODO change Log and Log Book so that filter and sort are not modifiable/visible
    //TODO define home screen - show Lists, Categories, Settings (or show last active screen?)
//    private Resources theme;
    private static final String SCREEN_MAIN_NAME = "TodoCatalyst";

    public ScreenMain() { //throws ParseException, IOException {
        super(SCREEN_MAIN_NAME, null, () -> {
        });

        String countStr = "";
        if (false) {
            int totalCount = DAO.getInstance().getItemCount(false);
            int countDone = DAO.getInstance().getItemCount(true);
            countStr = " " + (totalCount - countDone) + " [" + totalCount + "]";
        }
        setTitle("TodoCatalyst" + countStr);

//        this.theme = theme;
//        Toolbar toolbar = new Toolbar();
//        setToolbar(toolbar);
//        toolbar.setScrollOffUponContentPane(true);
//        setLayout(new BorderLayout());
        setLayout(BoxLayout.y());
        getContentPane().setScrollableY(true);
        addCommandsToToolbar(getToolbar(), getContentPane());//, theme);
//        addCommandsToToolbar(new Toolbar(), getContentPane());//, theme); //new Toolbar() hack to hide the toolbar
        if (false) {
            getToolbar().setUIID("Container");
            getToolbar().hideToolbar();
        }

//        Style iconStyle = UIManager.getInstance().getComponentStyle("SideCommandIcon");
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//         getContentPane().removeAll();
//         buildContentPane(getContentPane());
        if (false) {
            restoreKeepPos();
        }
        super.refreshAfterEdit();
    }

//    private void makeAndAddButtons(Command cmd, Toolbar toolbar, Container cont) {
//        makeAndAddButtons(cmd, toolbar, cont, "");
//    }
    private void makeAndAddButtons(Command cmd, Toolbar toolbar, Container cont, String helpText) {
        if (toolbar != null) {
            toolbar.addCommandToSideMenu(cmd);
        }
//         cont.add(new Button(cmd));
//         if (icon!=null) cmd.setIcon(icon);
//        Button titleButton = new Button(cmd);
        Component titleButton = makeHelpButton(cmd.getCommandName(), helpText);
        titleButton.setUIID("Container"); //avoid any style
//        titleButton.setTextPosition(Button.RIGHT);

        Button editButton = new Button();
        editButton.setCommand(cmd);
        editButton.setIcon(Icons.iconEditPropertiesLabelStyle);
        editButton.setText("");
        editButton.setUIID("Container");

        Container c = BorderLayout.centerEastWest(null, editButton, titleButton);
        c.setUIID("Button");
//        c.setLeadComponent(titleButton);
        cont.add(c);

    }

    public void addCommandsToToolbar(Toolbar toolbar, Container cont) { //, Resources theme) {
        toolbar.addCommandToLeftBar(newItemSaveToInboxCmd());

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
        MyReplayCommand listOfAlarms = MyReplayCommand.create(ScreenListOfAlarms.screenTitle, Icons.iconAlarmSetLabelStyle/*FontImage.create(" \ue838 ", iconStyle)*/,
                (e) -> {
//                new ScreenListOfAlarms().show();
                    ScreenListOfAlarms.getInstance().show();
                }
        );

//        makeAndAddButtons(listOfAlarms, toolbar, cont, "See past reminders that you have not cancelled or changed");
        makeAndAddButtons(listOfAlarms, toolbar, cont, "See active reminders");

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
        Command overdue = MyReplayCommand.create(SCREEN_OVERDUE_TITLE, SCREEN_OVERDUE_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, true); //FilterSortDef.FILTER_SHOW_DONE_TASKS
                    new ScreenListOfItems(SCREEN_OVERDUE_TITLE, () -> new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterSort, true), ScreenMain.this, (i) -> {
                    },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                    //                        | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
                }
        );
        makeAndAddButtons(overdue, toolbar, cont, "Overdue tasks, you probably want to deal with these before moving on to other tasks");

        Command today = MyReplayCommand.create(SCREEN_TODAY_TITLE, SCREEN_TODAY_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_DUE_DATE,
                            FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false); //FilterSortDef.FILTER_SHOW_DONE_TASKS
                    new ScreenListOfItems(SCREEN_TODAY_TITLE,
                            () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getDueAndOrWaitingTodayItems(true, true), filterSort, true),
                            ScreenMain.this, (i) -> {
//                new ScreenListOfItems(SCREEN_TODAY_TITLE, new ItemList(DAO.getInstance().getDueAndOrWaitingTodayItems(false, false), true), ScreenMain.this, (i) -> {
                            },
                            //                        ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP
                            //                        new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS, false), //FilterSortDef.FILTER_SHOW_DONE_TASKS
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            //                        | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
                            | ScreenListOfItems.OPTION_NO_WORK_TIME, (i) -> null /*prevent stickyHeader*/)
                            .show();
                }
        );

        makeAndAddButtons(today, toolbar, cont, "What is scheduled for today, due tasks, waiting tasks scheduled for today and tasks that fit today's workslots");

        //TODO!!! add support for help text on these commands
        Command next = MyReplayCommand.create(SCREEN_NEXT_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false);
                    new ScreenListOfItems(SCREEN_NEXT_TITLE, () -> new ItemList(SCREEN_NEXT_TITLE, DAO.getInstance().getCalendar(), filterSort, true), ScreenMain.this, (i) -> {
                    },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            //                        | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
                            | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                }
        );
        makeAndAddButtons(next, toolbar, cont, "Have a look at what's up the next month");

//        Command inbox = MyReplayCommand.create(SCREEN_INBOX_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                    new ScreenListOfItems(SCREEN_INBOX_TITLE, () -> new ItemList(SCREEN_INBOX_TITLE, DAO.getInstance().getAllItemsWithoutOwners(), true), ScreenMain.this, (i) -> {
//                    }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
//                }
//        );
        Command inbox = MyReplayCommand.create(SCREEN_INBOX_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                    new ScreenListOfItems(SCREEN_INBOX_TITLE, () -> new ItemList(SCREEN_INBOX_TITLE, Inbox.getInstance(), true), ScreenMain.this, (i) -> {
                    new ScreenListOfItems(SCREEN_INBOX_TITLE, () -> Inbox.getInstance(), ScreenMain.this, (i) -> {
//                    }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
                    }, 0).show();
                }
        );
        makeAndAddButtons(inbox, toolbar, cont, "**");

        Command lists = MyReplayCommand.create(SCREEN_LISTS_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                new ScreenListOfItemLists("Lists", new ItemList(DAO.getInstance().getAllItemLists()), ScreenMain.this, (i)->{}).show();                     //null: do nothing, lists are saved if edited
//                new ScreenListOfItemLists(SCREEN_LISTS_TITLE, DAO.getInstance().getAllItemLists(), ScreenMain.this, (i) -> {
//                    new ScreenListOfItemLists(SCREEN_LISTS_TITLE, DAO.getInstance().getItemListList(), ScreenMain.this, (i) -> {
                    new ScreenListOfItemLists(SCREEN_LISTS_TITLE, ItemListList.getInstance(), ScreenMain.this, (i) -> {
                    }).show();                     //null: do nothing, lists are saved if edited
                }
        );
        makeAndAddButtons(lists, toolbar, cont, "**");

        Command categories = MyReplayCommand.create(ScreenListOfCategories.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
//                new ScreenListOfCategories(DAO.getInstance().getAllCategories(), ScreenMain.this, (i)->{}).show();
                    new ScreenListOfCategories(CategoryList.getInstance(), ScreenMain.this, (i) -> {
                    }).show();
                }
        );
        makeAndAddButtons(categories, toolbar, cont, "**");

        Command projects = MyReplayCommand.create(SCREEN_PROJECTS_TITLE, null, (e) -> {
            new ScreenListOfItems(SCREEN_PROJECTS_TITLE, () -> new ItemList(DAO.getInstance().getAllProjects()), ScreenMain.this, (i) -> {
            }, 0).show();
        }
        );
        makeAndAddButtons(projects, toolbar, cont, "**");

//        MyReplayCommand workSlots = new MyReplayCommand(ScreenListOfWorkSlots.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
        MyReplayCommand workSlots = (MyReplayCommand) MyReplayCommand.create(ScreenListOfWorkSlots.SCREEN_TITLE, null, (e) -> {
//                super.actionPerformed(e);
            new ScreenListOfWorkSlots("", DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())), null, ScreenMain.this, (i) -> {

//            }, (obj) -> DAO.getInstance().getWorkSlotsN(new Date(System.currentTimeMillis()), new Date(MyDate.MAX_DATE)), true).show();
            }, (obj) -> DAO.getInstance().getWorkSlots(new Date(System.currentTimeMillis())), true).show();
        }
        );
        makeAndAddButtons(workSlots, toolbar, cont, "**");

        Command templates = MyReplayCommand.create(SCREEN_TEMPLATES_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                new ScreenListOfItems("Templates", new ItemList(DAO.getInstance().getAllTemplates()), ScreenMain.this, (i) -> {}, null, false, true).show();
//                    new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, DAO.getInstance().getTemplateList(), ScreenMain.this, (i) -> {
                    new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, () -> TemplateList.getInstance(), ScreenMain.this, (i) -> {
                    }, ScreenListOfItems.OPTION_TEMPLATE_EDIT// | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
//                new ScreenListOfItems("Templates", new ItemList(DAO.getInstance().getAllTemplates()), ScreenMain.this, (i) -> {
//                }).show();
                }
        );
        makeAndAddButtons(templates, toolbar, cont, "**");

        //TODO!!! add support for help text on these commands
        Command statistics = MyReplayCommand.create(SCREEN_STATISTICS/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, false);
                    new ScreenStatistics(ScreenMain.this, () -> {
                    }).show();
                }
        );
        makeAndAddButtons(statistics, toolbar, cont, "**");

        //Log
        //TODO!!! add support for help text on these commands
        Command completionLog = MyReplayCommand.create(SCREEN_COMPLETION_LOG_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_COMPLETED_DATE, FilterSortDef.FILTER_SHOW_DONE_TASKS, false);
                    new ScreenListOfItems(SCREEN_COMPLETION_LOG_TITLE, () -> new ItemList(SCREEN_COMPLETION_LOG_TITLE, DAO.getInstance().getCompletionLog(), filterSort, true), ScreenMain.this, (i) -> {
                    },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                }
        );
        makeAndAddButtons(completionLog, toolbar, cont, "**");

        //diary
        Command creationLog = MyReplayCommand.create(SCREEN_CREATION_LOG_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_CREATED_AT, FilterSortDef.FILTER_SHOW_ALL, false);
                    new ScreenListOfItems(SCREEN_CREATION_LOG_TITLE, () -> new ItemList(SCREEN_CREATION_LOG_TITLE, DAO.getInstance().getCreationLog(), filterSort, true), ScreenMain.this, (i) -> {
                    },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                }
        );
        makeAndAddButtons(creationLog, toolbar, cont, "**");

        Command touched = MyReplayCommand.create(SCREEN_TOUCHED/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    FilterSortDef filterSort = new FilterSortDef(Item.PARSE_UPDATED_AT, FilterSortDef.FILTER_SHOW_ALL, true); //true => show most recent first
                    new ScreenListOfItems(SCREEN_TOUCHED, () -> new ItemList(SCREEN_TOUCHED, DAO.getInstance().getTouchedLog(), filterSort, true), ScreenMain.this, (i) -> {
                    },
                            ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                            | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                }
        );
        makeAndAddButtons(touched, toolbar, cont, "**");

        Command allTasks = MyReplayCommand.create(SCREEN_ALL_TASKS_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    new ScreenListOfItems(SCREEN_ALL_TASKS_TITLE, () -> new ItemList(SCREEN_ALL_TASKS_TITLE, DAO.getInstance().getAllItems(), true), ScreenMain.this, (i) -> {
                    }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
                }
        );
        makeAndAddButtons(allTasks, toolbar, cont, "**");

        Command tutorial = MyReplayCommand.create(SCREEN_TUTORIAL/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    new ScreenListOfItems(SCREEN_TUTORIAL, () -> new ItemList(SCREEN_TUTORIAL, DAO.getInstance().getAllItems(), true), ScreenMain.this, (i) -> {
                    }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP).show();
                }
        );
        makeAndAddButtons(tutorial, toolbar, cont, "**");

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            Command editRepeatRule = new MyReplayCommand("Edit RepeatRule2"/*FontImage.create(" \ue838 ", iconStyle)*/,null,(e)->{
//                    RepeatRuleParseObject repeatRule = new RepeatRuleParseObject();
//                    repeatRule.setSpecifiedStartDate(new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 48).getTime());
//                    new ScreenRepeatRuleNew("test", repeatRule, new Item("taskX", 15, new Date(System.currentTimeMillis() + MyDate.HOUR_IN_MILISECONDS * 24)), ScreenMain.this, () -> {
//                    }, true, new Date()).show();
//                }
//            );
//            makeAndAddButtons(editRepeatRule, toolbar, cont, "**");
//        }
//        if (false) {
//            Command testRepeatRule = new MyReplayCommand("Test RepeatRule"/*FontImage.create(" \ue838 ", iconStyle)*/) {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    RepeatRuleParseObject repeatRule = new RepeatRuleParseObject();
//                    repeatRule.testRepeatRules();
//                }
//            };
//            makeAndAddButtons(testRepeatRule, toolbar, cont, "**");
//        }
//</editor-fold>
        Command inspirationLists = MyReplayCommand.create(ScreenInspirationalLists.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    new ScreenInspirationalLists(ScreenMain.this).show();
                }
        );
        makeAndAddButtons(inspirationLists, toolbar, cont, "**");
//<editor-fold defaultstate="collapsed" desc="comment">

//        if (false) {
//            Command statisticsList = new MyReplayCommand(ScreenStatisticsLists.SCREEN_TITLE/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
//                        new ScreenStatisticsLists(ScreenMain.this).show();
//                    }
//            );
//            if (toolbar != null) {
//                toolbar.addCommandToSideMenu(statisticsList);
//            }
//            cont.add(new Button(statisticsList));
//        }
//</editor-fold>
        Command cleanTemplates = MyReplayCommand.create("Clean up templates", Icons.get().iconSettingsLabelStyle, (e) -> {
            DAO.getInstance().cleanUpTemplateListInParse(true);
        }
        );
        makeAndAddButtons(cleanTemplates, toolbar, cont, "**");

        Command settings = MyReplayCommand.create(ScreenSettings.SCREEN_TITLE, Icons.get().iconSettingsLabelStyle, (e) -> {
//                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
            new ScreenSettings(ScreenMain.this).show();
        }
        );
        makeAndAddButtons(settings, toolbar, cont, "**");

        Command repair = MyReplayCommand.create(ScreenRepair.SCREEN_TITLE, Icons.get().iconSettingsLabelStyle, (e) -> {
//                new ScreenListOfCategories("Categories", new ItemList(DAO.getInstance().getAllCategories()), ScreenMain.this, (i)->{}).show();
            new ScreenRepair(ScreenMain.this).show();
        }
        );
        makeAndAddButtons(repair, toolbar, cont, "**");

        Command homePage = MyReplayCommand.create("Home page"/*FontImage.create(" \ue838 ", iconStyle)*/, null, (e) -> {
                    Display.getInstance().execute("http://todocatalyst.com");
                }
        );

        makeAndAddButtons(homePage, toolbar, cont,
                "**");

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
//            new ScreenListOfItems(SCREEN_TODAY_TITLE, () -> new ItemList(SCREEN_TODAY_TITLE, DAO.getInstance().getDueAndOrWaitingTodayItems(true, true), filterToday, true), ScreenMain.this, (i) -> {
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
