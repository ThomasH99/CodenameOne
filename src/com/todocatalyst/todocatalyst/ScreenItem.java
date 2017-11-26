package com.todocatalyst.todocatalyst;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.Button;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories -
 *
 * @author Thomas
 */
public class ScreenItem extends MyForm {
    //TODO 
    //TODO!!! update project's remaining time when adding subtasks (via listener?!)
    //TODO!!! Show visually which tab is selected (OK on device?) 
    //TODO change 'comment' to 'notes'
    //TODO auto-wrap all input fields if too wide for screen
    //TODO!!! check if ComponentGroup with Difficulty picker becomes wider than screen
    //TODO add icons to Easy/Difficult, Dread/Fun
    //TODO!! pop-up screen for full screen editing of comments/notes
    //TODO find a way to show the task text when editing in other tabs (show editable field at top of form?) -> make task text a permanent field above the tabs!
    //TODO show list tasks belongs to when editing it (to know/be reminded of the context)
    //TODO subtask tab should add Filter
    //TODO remove subtask tab for non-project tasks, and add a AddSubtask command instead(?? more difficult to discover subtasks, byt may less clicks to add subtasks)
    //TODO capture location where task was created
    //TODO assign tasks to Locations
    //TODO list of locations to assign tasks to
    //TODO Template: dialog to choose name of new template, exclusion options  
    //TODO add button to start Timer from within editItem screen (should start on sub-tasks if there are) -> really a good idea?? I guess yes (use case: you create a task and start working on it at the same time as you edit its details)
    //TODO see or edit categories People 
    //TODO add a way to add subtasks directly in list view (eg left-swipe?, long-press, ...?)
    //TODO don't update/save value of effort for Projects (to avoid unnecessary update)???
    //TODO!!! HELP: in ScreenItem, tab Subtasks, for an empty subtask list: add "<click [+] to add subtasks to this task (make it a project)>
    //DONE find a way to remove the '.' when editing value (show "0", not "0.0", to make editing the value easier first time). OR: simply add a 'clear' (x) button to the field
    //TODO Settings Help(?)
    //DONE after adding 'timestamp' button to Notes and entering edit text, place cursor to the rigth of text, not left (how's behaviour on device?)
    //NOPE add hide/show button to explanatory labels -> not necessary with Toastbar help messages
    //DONE Show #subtasks in Subtask tab as [3]
    //DONE subtask tab should support manual Move (drag&drop)
    //DONE show selected categories in same order as in categorylist (but rare that more than a few selected so not a big issue)
    //DONE make category field wrap
    //NOPE make labels with explanations nice (smaller font) -> now shown using Toastbar
    //DONE disable editing remaining time for projects (since sum of subtasks)
    //DONE !!! add popup to ask for WaitingTillDate when setting waiting (add to ScreenItem, ScreenItemList, Timer) + add filter to only show waiting tasks where date has been reached
    //DONE add timed entry button to comment field (put after title 'Notes'?)
    //NOPE Center all ButtonGroups/ComponentGroups
    //DONE Rigth-justify all input fields
    //DONE handle text wrap in long texts (task description or Notes)
    //DONE only show 'clear' button on the right-hand side of dates, when a date is actually defined (now shown for empty fields with only explanation text)
    //DONE!!!! add fields for WaitingDate
    //DONE!!! Template: when creating subtask for a template, make sure they're saved as a template
    //DONE Template: allow any level of subtasks to be saved as template or only toplevel (==no OnwerItem)?? YES, allow, makes sense to create template from sub-projects, or even individual tasks for that matter
    //DONE Template: option in list of templates to add a new one
    //DONE Template: ScreenItem - show when editing a Template, e.g. in title or different color titlebar
    //DONE Template: ScreenItem adapt to Template, e.g. hide fields that don't make sense
    //DONE add 'timestamp' button to Notes
    //DONE move Remaining time to first tab
    //DONE use the same way to build containers in subTask tab as in ScreenListOfItems, to ensure items look the same and has same edit functionality
    //DONE error: cannot create a subtask to a subtask (says the "Persistable object must be saved before being set of a parseobject")
    //DONE remove [+] (add new task) from ScreenItem - strange to create a task when you're already creating another task - seems it's not normal to see the [+] outside add subtasks
    //DONE enable editing subtasks directly in subtask tab
    //DONE replace normal container by Tree when subtasks are added while editing a task

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
//    Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>();
//    MyForm previousForm;
    private Item item;
    private Item itemLS; //FromLocalStorage, read from local storage if app was stopped with unsaved edits to Item
//    Set locallyEditedCategories;
    private List locallyEditedCategories = null;
//    private ItemAndListCommonInterface locallyEditedOwner = null;
    private List<ItemAndListCommonInterface> locallyEditedOwner = null;
    private RepeatRuleParseObject orgRepeatRule; //same instance as the item's repeatRule, must use item.setRepeatRule to ensure it is stored?
    private RepeatRuleParseObject locallyEditedRepeatRule;
//    UpdateField updateActionOnDone;
//    private MyTree2 subTaskTree;
    private int lastTabSelected = -1;
    private boolean templateEditMode;
    private String FILE_LOCAL_EDITED_ITEM = "ScreenItem-EditedItem";
    private String FILE_LOCAL_EDITED_OWNER = "ScreenItem-EditedOwner";
    private String FILE_LOCAL_EDITED_CATEGORIES = "ScreenItem-EditedCategories";
    private String FILE_LOCAL_EDITED_REPEAT_RULE = "ScreenItem-EditedRepeatRule";
    private boolean localSave; //true when save of item is only local (on app pause/exit)

//    ScreenItem(Item item, MyForm previousForm) { //throws ParseException, IOException {
//        this(item, previousForm, ()->{});
//    }
    ScreenItem(Item item, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
        this(item, previousForm, doneAction, false);
    }

    private static String getScreenTitle(boolean isTemplate, String title) {
        return (isTemplate ? "TEMPLATE: " : "") + title;
    }

    ScreenItem(Item item, MyForm previousForm, UpdateField doneAction, boolean templateEditMode) { //throws ParseException, IOException {
//        super("Task", previousForm, doneAction);
//        super((item.isTemplate() ? "TEMPLATE: " : "") + item.getText(), previousForm, doneAction);
        super(getScreenTitle(item.isTemplate(), item.getText()), previousForm, doneAction);
//        FILE_LOCAL_EDITED_ITEM= getTitle()+"- EDITED ITEM";
        if (false) {
            ASSERT.that(item.isDataAvailable(), "Item \"" + item + "\" data not available");
        }

        this.templateEditMode = item.isTemplate() || templateEditMode; //
        getTitleComponent().setEndsWith3Points(true);
//        ScreenItemP.item = item;
        this.item = item;
        expandedObjects = new HashSet();
        try {
            //        DAO.getInstance().deleteCategoryFromAllItems(cat);
            if (item != null) {
                item.fetchIfNeeded();
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }

        //RESTORE locally edited value (if stored on app pause/exit)
        itemLS = (Item) restoreOnAppExit();
        if (item.getObjectIdP() == null) {
            this.item = itemLS; //if item is a new item, then we completely ignore that Item and continue with the previously locally saved values
        } else {
            itemLS = this.item; //it no locally saved edits, then use item to 'feed' the edits fields
        }
        //restore locally edited Owner
        locallyEditedOwner = restoreNewOwner();
        locallyEditedCategories = restoreNewCategories();
        locallyEditedRepeatRule = restoreNewRepeatRule();

//        ScreenItemP.previousForm = previousForm;
//        this.previousForm = previousForm;
//        this.updateActionOnDone = doneAction;
        // we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
        // we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollable(false);
//        setToolbar(new Toolbar());
        addCommandsToToolbar(getToolbar());//, theme);
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        //NOT needed to refresh everything when a subtask has been added
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll(); //clear old content pane
        buildContentPane(getContentPane()); //rebuild and refresh
        revalidate(); //refresh form
        //TODO!!!! restore scroll position in expanded list of subtasks 
//       super();
    }

    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
//        Command cmd = Command.create("", icon, (e) -> {
//            putEditedValues2(parseIdMap2);
////            try {
////                item.save();
////                if (updateActionOnDone != null) {
//            updateActionOnDone.update();
////                }
////            } catch (ParseException ex) {
////                Logger.getLogger(ScreenItemP.class.getName()).log(Level.SEVERE, null, ex);
//            Log.p(ScreenItem.class.getName(), Log.ERROR); //TODO: add dialog/popup info when save does not succeed
////            }
////            previousForm.showBack();
//            previousForm.refreshAfterEdit();
//            previousForm.showBack();
//        });
//        cmd.putClientProperty("android:showAsAction", "withText");
        //DONE
//        Command cmd = makeDoneUpdateWithParseIdMapCommand();
//        toolbar.addCommandToLeftBar(cmd);
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        //TIMER
//        Command timerCmd = makeTimerCommand(title, iconNew, itemList);
//        toolbar.addCommandToLeftBar(cmd);
        //CANCEL
        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }
//        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
//            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh, notably categories
////            previousForm.showBack(); //drop any changes
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//        });

        //EDIT WORKSLOTS
//        if (!optionTemplateEditMode && !optionNoWorkTime) {
        toolbar.addCommandToOverflowMenu(new Command("Work time", Icons.iconSettingsApplicationLabelStyle) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                new ScreenListOfWorkSlots(item.getText(), item.getWorkSlotList(), item, ScreenItem.this, (iList) -> {
//                    itemList.setWorkSLotList(iList); //NOT necessary since each slot will be saved individually
//                    refreshAfterEdit(); //TODO CURRENTLY not needed since workTime is not shown (but could become necessary if we show subtasks and their finish time 
                }).show();
            }
        });
//        }

        //TEMPLATE
        if (!templateEditMode) {
            toolbar.addCommandToOverflowMenu("Save as template", null, (e) -> {
                Dialog ip = new InfiniteProgress().showInifiniteBlocking();
                //TODO add option to let user edit template after creation
                //TODO enable user to select which fields to exclude
                putEditedValues2(parseIdMap2, item); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
                Item template = new Item();
                item.copyMeInto(template, Item.CopyMode.COPY_TO_TEMPLATE);
                DAO.getInstance().save(template);
                TemplateList templateList = DAO.getInstance().getTemplateList();
                if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
                    templateList.add(0, template);
                } else {
                    templateList.add(template);
                }
                DAO.getInstance().save(templateList);
                ip.dispose();
//                if (Dialog.show("INFO", "Do you want to edit the template now? You can find and edit it later under Templates.", "Yes", "No")) {
//                    new ScreenItem(template, ScreenItem.this, () -> {
//                        DAO.getInstance().save(template);
//                    }).show();
//                }
                new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, DAO.getInstance().getTemplateList(), ScreenItem.this, (i) -> {
                }, ScreenListOfItems.OPTION_TEMPLATE_EDIT// | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                ).show();
            });
        }

        if (true || !templateEditMode) { //UI: KEEP for templates to allow inserting another template as a sub-hierarcy under a template
            toolbar.addCommandToOverflowMenu("Insert template", null, (e) -> {
                //TODO!! Add "don't show again + setting to all these info popups
                if (Dialog.show("INFO", "Inserting a template into a task will add the values and subtasks from the template to the task. It will not overwrite any fields already defined manually in the task", "OK", "Cancel")) {
                    //TODO enable user to select which fields to exclude
                    putEditedValues2(parseIdMap2, item); //save any already edited values before inserting the template
//                    Item template = pickTemplateOLD(); //TODO!!!! make this a full Screen picker like CategorySelector
                    List selectedTemplates = new ArrayList();
                    if (false) { //shouldn't be necessary
                        Form f = Display.getInstance().getCurrent(); //tip from CN1: close dialog *before* showing next form
                        if (f instanceof Dialog) {
                            ((Dialog) f).dispose();
                        }
                    }
                    new ScreenObjectPicker(SCREEN_TEMPLATE_PICKER, DAO.getInstance().getTemplateList(), selectedTemplates, ScreenItem.this, () -> {
                        if (selectedTemplates.size() >= 1) {
                            Item template = (Item) selectedTemplates.get(0);
                            Dialog ip = new InfiniteProgress().showInifiniteBlocking();
                            template.copyMeInto(item, Item.CopyMode.COPY_FROM_TEMPLATE);
                            locallyEditedCategories = null; //HACK needed to force update of locallyEditedCategories (which shouldn't be refreshed when eg editing subtasks to avoid losing the edited categories) 
                            ip.dispose();
                            refreshAfterEdit();
                        }
//                        else {
//                            Dialog.show("INFO", "No templates yet. \n\nGo to " + ScreenMain.SCREEN_TEMPLATES_TITLE + " to create templates or save existing tasks or projects as templates", "OK", null);
//                        }
                    }, (obj) -> {
                        if (obj instanceof Item) {
                            return ((Item) obj).getText();
                        } else {
                            return obj.toString();
                        }
                    }, 1, true, false, false).show();
//                    if (template != null) {
                };
            });
        }

        //DELETE
        toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//            item.delete();
            DAO.getInstance().delete(item);
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
            showPreviousScreenOrDefault(previousForm, true);
        });

//<editor-fold defaultstate="collapsed" desc="comment">
//TASK STATUS
//        String[] statusStrings = {ItemStatus.CREATED.toString(), ItemStatus.ONGOING.toString(), ItemStatus.WAITING.toString(), ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()};
//        toolbar.addCommandToOverflowMenu("Set status", null, (e) -> {
//                    MyStringPicker statusPicker = new MyStringPicker(statusStrings, parseIdMap2, ()-> {return item.getStatus().toString();}, (s)->
//                    {
//
//                    })).show();
//    });
//</editor-fold>
    }

    /**
     * @param content
     * @return
     */
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    protected Container buildItemContainer(Item item) { //), ItemList itemList, ScreenListOfItems thisScreen) {
    ////        Container cont = new Container();
    ////        cont.setLayout(new BorderLayout());
    ////        Container c = new Container();
    ////        MyDropContainer contx = new MyDropContainer();
    ////        MyDropContainer2 cont2 = new MyDropContainer2();
    //
    //        MyDropContainer cont = new MyDropContainer(item, item.getItemList(), null, new BorderLayout());
    ////        MyDropContainer cont2 = new MyDropContainer();
    ////        MyDropContainer cont3 = new MyDropContainer(new BorderLayout(), itemList, item);
    //
    ////        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
    //        //EDIT Item in list
    //        Button editItemButton = new Button();
    //        Command editCmd = new Command(item.getText()) {
    //            @Override
    //            public void actionPerformed(ActionEvent evt) {
    //                Item item = (Item) editItemButton.getClientProperty("item");
    //                new ScreenItem(item, ScreenItem.this, () -> {
    //
    //                }).show();
    ////                new ScreenItem(item, thisScreen).show();
    //            }
    //        };
    //        editItemButton.setCommand(editCmd);
    //        editItemButton.putClientProperty("item", item);
    //        editItemButton.setUIID("Label");
    //        cont.addComponent(BorderLayout.CENTER, editItemButton);
    //
    //        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    //        if (item.getPriority() != 0) {
    //            west.add(new Label(item.getPriority() + ""));
    //        } else {
    //            west.add(new Label(" "));
    //        }
    //        cont.addComponent(BorderLayout.WEST, west);
    //
    //        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    //
    //        //EDIT subtasks in Item
    //        Button subTasksButton = new Button();
    //        if (item.getItemListSize() != 0) {
    //            Command expandSubTasks = new Command("[" + item.getItemListSize() + "]"); // {
    ////                @Override
    ////                public void actionPerformed(ActionEvent evt) {
    //////                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
    ////
    ////                }
    ////            };
    //            subTasksButton.setCommand(expandSubTasks);
    //            cont.putClientProperty("subTasksButton", subTasksButton);
    //            east.addComponent(subTasksButton);
    //        }
    //        subTasksButton.setUIID("Label");
    //
    ////        east.addComponent(new Label(SimpleDateFormat.new Date(item.getDueDate())));
    ////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(item.getDueDate()))));
    ////                        setText(L10NManager.getInstance().formatDateShortStyle((Date)value));
    //        if (item.getDueDateD().getTime() != 0) {
    //            east.addComponent(new Label(L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
    //        }
    //
    ////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
    //        cont.addComponent(BorderLayout.EAST, east);
    ////        cont.setLeadComponent(east);
    //
    //        Container bottom = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    //        bottom.add(new Button("X")); //Edit(?)
    //        bottom.add(new Button("Y")); //Create new task below
    //        bottom.add(new Button("Z")); //Postpone due date
    //        bottom.add(new Button("Z")); //See details of task?
    //
    ////        if (true) {
    ////            cont.setDraggable(true);
    ////            return cont;//ignore Swipeable for the moment
    ////        } else {
    ////            SwipeableContainer swip = new SwipeableContainer(bottom, cont);
    ////            swip.addSwipeOpenListener(new ActionListener() {
    ////                @Override
    ////                public void actionPerformed(ActionEvent evt) {
    ////                    if (swip.isOpenedToRight()) {
    ////                        item.setDone(true);
    ////                    }
    ////                }
    ////            });
    ////            swip.setDraggable(true);
    ////            return swip;
    ////        }
    //        return cont;//ignore Swipeable for the moment
    //    }
    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyTree2 createSubTaskTreeXXX(MyTreeModel treeModel) {
//        MyTree2 subTree = new MyTree2(treeModel) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> {
//                    subTaskTree.refresh();
//                    subTaskTree.revalidate();
//                }, false, //selectionMode not allowed for subtasks
//                        null); //TODO save expandedObjects even for subtasks
//                setIndent(cmp, depth);
//                return cmp;
//            }
//        };
//        return subTree;
//    }
//    protected Container buildContentPaneForItemListXXX(ItemList itemList) {
////        DAO.getInstance().fetchAllItemsIn(itemList, false); //fetch subtasks
////<editor-fold defaultstate="collapsed" desc="comment">
////        subTaskTree = new MyTree2(itemList) {
////            @Override
////            protected Component createNode(Object node, int depth) {
//////                Component cmp = ItemContainer.buildItemContainer((Item) node, itemList);
////                Component cmp = ScreenListOfItems.buildItemContainer((Item) node, itemList, () -> true, () -> {
////                    subTaskTree.refresh();
////                    subTaskTree.revalidate();
////                });
//////                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
////                setIndent(cmp, depth);
////                return cmp;
////            }
////        };
////</editor-fold>
//        subTaskTree = createSubTaskTreeXXX(itemList);
//
////        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        Container cont = new Container(BoxLayout.y());
//        cont.setScrollableY(true);
//        cont.add(subTaskTree);
////        cont.setDraggable(true);
//        subTaskTree.setDropTarget(true);
//        return cont;
//    }
//    private Component addDatePickerWithClearButton(MyDatePicker datePicker) {
//    private Component addDatePickerWithClearButton(Picker datePicker) {
//        return LayeredLayout.encloseIn(datePicker, FlowLayout.encloseRightMiddle(new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            datePicker.setDate(new Date(0));
//            //TODO!!! below code also pops up the picker, so filter on my listener only?
//            for (Object al : datePicker.getListeners()) {
//                if (al instanceof MyActionListener) {
//                    ((MyActionListener) al).actionPerformed(null);
//                }
//            }
//        }))));
//    }
//    private Component addDatePickerWithClearButtonOLD(Picker datePicker) {
//        return LayeredLayout.encloseIn(datePicker, FlowLayout.encloseRightMiddle(new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            datePicker.setDate(new Date(0));
//            //TODO!!! below code also pops up the picker, so filter on my listener only?
//            for (Object al : datePicker.getListeners()) {
//                if (al instanceof ActionListener) {
//                    ((ActionListener) al).actionPerformed(null);
//                }
//            }
//        }))));
//    }
//    private Component addTimePickerWithClearButtonOLD(Picker timePicker) {
//        return LayeredLayout.encloseIn(timePicker, FlowLayout.encloseRightMiddle(new Button(Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            timePicker.setTime(0);
//            //TODO!!!! how to trigger an actionevent when clearing the field instead of editing manually?
//            for (Object al : timePicker.getListeners()) {
//                if (al instanceof ActionListener) {
//                    ((ActionListener) al).actionPerformed(null);
//                }
//            }
//        }))));
//    }
//    private Component addTimePickerWithClearButton(Picker timePicker) {
//        Button clearButton = new Button();
//        Command clear = Command.create(null, Icons.iconCloseCircleLabelStyle, (e) -> {
//            timePicker.setTime(0);
////            clearButton.setHidden(true); //hide the clear button when field is empty
//            //TODO!!!! how to trigger an actionevent when clearing the field instead of editing manually?
//            for (Object al : timePicker.getListeners()) {
//                if (al instanceof MyActionListener) {
//                    ((MyActionListener) al).actionPerformed(null);
//                }
//            }
//        });
//        clearButton.setCommand(clear);
//        clearButton.setHidden(timePicker.getTime() == 0);
//        //listener to hide/show clear button when field is empty/not empty
//        timePicker.addActionListener(new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                clearButton.setHidden(timePicker.getTime() == 0);
//            }
//        });
//        return LayeredLayout.encloseIn(timePicker, FlowLayout.encloseRightMiddle(clearButton));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container createSubtaskContainer(Item item, MyForm screen, ItemList itemListOrg, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//        //SUBTASKS
//        ItemList<Item> subtasksItemList = item.getItemList();
//        boolean hasSubtasks = subtasksItemList.size() != 0;
//        boolean showSubtasksExpanded = MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean() && hasSubtasks;
//
//        Container subtaskContainer = new Container(new BorderLayout()); //main container
//
//        if (hasSubtasks) {
//            //ADD NEW SUBTASK - SOUTH
//            Container addSubtaskField = new QuickAddItemContainer("Add subtask", itemListOrg, templateEditMode);
//            subtaskContainer.add(BorderLayout.SOUTH, addSubtaskField);
//
//            //SUBTASK LIST
//            Container subtasks = new Container(BoxLayout.x());
//            subtasks.add(ScreenListOfItems.makeMyTree2ForSubTasks(screen, itemListOrg, screen.expandedObjects));
//            subtasks.setHidden(!hasSubtasks);
//
//            //HEADER
//            subtaskContainer.add(BorderLayout.CENTER, subtasks);
//            Container subtaskHeaderCont = new Container(new BorderLayout());
//            subtaskHeaderCont.setHidden(!hasSubtasks);
//            subtaskHeaderCont.add(BorderLayout.WEST, "Subtasks");
//            //expand button to show
//            Button showSubtasks = new Button();
//            showSubtasks.setCommand(Command.create(null, showSubtasksExpanded ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle, (e) -> {
//                MyPrefs.alwaysShowSubtasksExpandedInScreenItem.flipBoolean();
//                Boolean hideSubtasks = !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean();
//                addSubtaskField.setHidden(hideSubtasks);
//                subtasks.setHidden(hideSubtasks);
//                showSubtasks.setIcon(hideSubtasks ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//
//            }));
//            //HEADER - count + expand button
//            subtaskHeaderCont.add(BorderLayout.EAST, BoxLayout.encloseXNoGrow(
//                    new Label(subtasksItemList.size() + ""),
//                    new Label(MyDate.formatTimeDuration(subtasksItemList.getRemainingEffort())),
//                    showSubtasks));
//            subtaskContainer.add(BorderLayout.NORTH, subtaskHeaderCont);
//
////            addSubtaskField.setHidden(!(!hasSubtasks || MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()));
//            addSubtaskField.setHidden(hasSubtasks && !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean());
//        }
//        return subtaskContainer;
//    }
//</editor-fold>
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
    private Container buildContentPane(Container cont) {
//        Container content = new Container();
        parseIdMapReset();
        if (false) {
            TableLayout tl;
//        int spanButton = 2;
            int nbFields = 10;
            if (Display.getInstance().isTablet()) {
                tl = new TableLayout(nbFields, 2);
            } else {
                tl = new TableLayout(nbFields * 2, 1);
//            spanButton = 1;
            }
            tl.setGrowHorizontally(true);
            cont.setLayout(tl);
        }
//        Container cont;
//        cont = cont;

//        Tabs tabs = new Tabs(Component.BOTTOM);
        Tabs tabs = new Tabs();
//       tabs.setTabPlacement(Component.BOTTOM);
//       tabs.setTabPlacement(Component.BOTTOM);
        tabs.setSwipeActivated(false);
        cont.add(BorderLayout.CENTER, tabs);

        //TAB MAIN
        Container mainTabCont = new Container(new BorderLayout());
        Container mainCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        mainTabCont.add(BorderLayout.CENTER, mainCont);
        mainCont.setScrollableY(true);

//        Container mainTabCont = BorderLayout.north(mainCont);
//        Container mainTabCont = BorderLayout.north(new Container(BoxLayout.y()));
//        if (false) {
//            mainCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//        }
        tabs.addTab("Main", null, mainTabCont);
//        tabs.addTab("Main", null, mainTabCont);

//        MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//        MyTextField description = new MyTextField("Task", 20, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> item.getText(), (s) -> item.setText(s));
        MyTextField description = new MyTextField(Item.DESCRIPTION_HINT, 20, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
                () -> itemLS.getText(), (s) -> item.setText(s));
        description.setUIID("Text");
        description.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically
//        MyCheckBox status = new MyCheckBox(null, parseIdMap2, () -> item.isDone(), (b) -> item.setDone(b));
        tabs.addSelectionListener(new SelectionListener() {
            @Override
            public void selectionChanged(int oldSelected, int newSelected) {
                if (oldSelected == 0) { //main tab(??) //TODO!! why this code??
//                    setTitle(description.getText()); //update Form title in case task text has changed (why would it??)
                    setTitle(getScreenTitle(item.isTemplate(), description.getText())); //update Form title in case task text has changed (TODO why would it??)
                }
                lastTabSelected = newSelected; //keep track of lastTab
            }
        });
        if (description.getText().length() == 0) {
            setEditOnShow(description); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        }

        //need to declare already here to use in actionListener below
        MyTimePicker effortEstimate;
        if (item.isProject()) {
            effortEstimate = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getEffortEstimate(false) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setEffortEstimate(i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
        } else {
            effortEstimate = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setEffortEstimate((int) i * MyDate.MINUTE_IN_MILLISECONDS));
        }

//        description.addActionListener((e) -> setTitle(description.getText())); //update the form title when text is changed
        description.addActionListener((e) -> {
//            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
            Item.EstimateResult res = Item.getEffortEstimateFromTaskText(description.getText(), false);
            //TODO!!! create a function that will determine when to any of the user setting baesd on the values in description string
            //eg call w string and 
//            if (item.getEffortEstimate()==0 || effortEstimate.getTime() == 0||res.minutes*MyDate.MINUTE_IN_MILLISECONDS!=item.getEffortEstimate()) { //UI: use value in text if either no previous value set, or manual value entered in picker or new text value entered into text
            if (res.minutes != 0) { //UI: alwyas use value in text to override previous value
                //TODO!!!!! call the same actionListener as when EsitmatePicker is changed 
                effortEstimate.setTime(res.minutes);
                description.setText(res.cleaned); //DON'T update text while 
                description.repaint();
                effortEstimate.repaint();
            }
            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
        }); //update the form title when text is changed

//        MyTimePicker actualEffort = null;
//        Button status = ItemContainer.createCheckbox(item, false);
//        MyCheckBox status = new MyCheckBox(item, false);
        MyCheckBox status = new MyCheckBox(itemLS.getStatus(), (oldStatus, newStatus) -> {
//            if (newStatus==ItemStatus.WAITING)
        }, null);

        parseIdMap2.put(status, () -> item.setStatus(status.getStatus()));
//        Container taskCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
        Container taskCont = new Container(new BorderLayout());

        //STARRED
//        CheckBox starredcb = new CheckBox(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
        Button starred = new Button(itemLS.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
        starred.addActionListener((e) -> {
            if (false) {
                item.setStarred(!item.isStarred()); //shouldn't change item.starred until saving via parseIdMap2
                starred.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
            }
            starred.setIcon(starred.getIcon() == Icons.iconStarSelectedLabelStyle ? Icons.iconStarUnselectedLabelStyle : Icons.iconStarSelectedLabelStyle);
//            DAO.getInstance().save(item); //??
        });
        parseIdMap2.put(starred, () -> item.setStarred(starred.getIcon() == Icons.iconStarSelectedLabelStyle));

        taskCont.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, starred);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button helpTextButton = new Button(Item.TASK);
//        helpTextButton.setUIID("Label"); //show as Label
//        helpTextButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Status status = ToastBar.getInstance().createStatus();
//                status.setMessage("Describe your task here");
////status.setExpires(3000);
////                final int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 65; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
//                final int TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS = 80; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
//                final int ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR = 500; //based on needing 10s to read 3 1/2 lines of text with 45 chars each = 10s/158 ~ 0,063s
//                status.setExpires(status.getMessage().length() * TIME_REQUIRED_TO_READ_A_CHARACTER_IN_MILLIS + ADDITIONAL_TIME_REQUIRED_MAKE_TOASTBAR_APPEAR_AND_DISAPPEAR);
//                status.show();
//            }
//        });
//        mainCont.add(new Label(Item.TASK));
//</editor-fold>
        if (false) {
            mainCont.add(makeHelpButton(Item.TASK, Item.TASK_HELP));
        }
        mainCont.add(taskCont);

//        MyTextField comment = new MyTextField("Details", "Comments", 20, TextArea.ANY, parseIdMap, item, Item.PARSE_COMMENT);
        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> itemLS.getComment(), (s) -> item.setComment(s));
        comment.setUIID("Comment");
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
//            comment.setText(Item.addTimeToComment(comment.getText()));
////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
////comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
//        }));
//</editor-fold>
        comment.getAllStyles().setMarginRight(0);
        comment.getAllStyles().setPaddingRight(0);

        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(comment);
        addTimeStampToComment.getAllStyles().setMarginLeft(0);
        addTimeStampToComment.getAllStyles().setPaddingLeft(0);
        addTimeStampToComment.getAllStyles().setMarginRight(0);
        addTimeStampToComment.getAllStyles().setPaddingRight(0);

//        mainCont.add(new Label(Item.COMMENT)).add(comment);
//        mainCont.add(new Label(Item.COMMENT)).add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment));
//        mainCont.add(FlowLayout.encloseIn(makeHelpButton(Item.COMMENT, "**"), addTimeStampToComment));
        Container ts = FlowLayout.encloseRight(addTimeStampToComment);
        Container all = BorderLayout.centerEastWest(comment, ts, null);
        all.setUIID("TextArea");

//        ts.setFocusable(true); //make timeStamp grap events?
//        mainCont.add(LayeredLayout.encloseIn(comment, ts));
        mainCont.add(all);
//        mainCont.add(comment);

        MyDateAndTimePicker dueDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getDueDateD(), (d) -> {
            item.setDueDate(d);
            //TODO!!? dialog to ask if repeatRule startDate should be updated to match due date? But not meaningful if it's just a repeatInstance with its own duedate
//            if (locallyEditedRepeatRule!=null) {
//                
//            }
        }); //"<click to set a due date>"
//        cont.add(new Label("Due")).add(dueDate);
//        mainCont.add(new Label("Due")).add(LayeredLayout.encloseIn(dueDate, FlowLayout.encloseRightMiddle(new Button(Command.create(null,Icons.iconCloseCircle,(e)->{dueDate.setDate(new Date(0));})))));
//        mainCont.add(new Label(Item.DUE_DATE)).add(addDatePickerWithClearButton(dueDate));
//        mainCont.add(new Label(Item.DUE_DATE)).add(dueDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.DUE_DATE, dueDate.makeContainerWithClearButton(), "**"));
        mainCont.add(layout(Item.DUE_DATE, dueDate, Item.DUE_DATE_HELP));
//        hi.add(LayeredLayout.encloseIn(settingsLabel, FlowLayout.encloseRight(close))) //https://github.com/codenameone/CodenameOne/wiki/Basics---Themes,-Styles,-Components-&-Layouts#layered-layout

        //FINISH_TIME
        WorkTime workTime = item.getAllocatedWorkTime();
        if (workTime != null) {
            Button showWorkTimeDetails = new Button(Command.create(MyDate.formatDateTimeNew(new Date(workTime.getFinishTime())), null, (e) -> {
                new ScreenListOfWorkTime(item.getText(), item.getAllocatedWorkTime(), ScreenItem.this).show();
            }));
            mainCont.add(layout(Item.FINISH_WORK_TIME, showWorkTimeDetails, Item.FINISH_WORK_TIME_HELP, true, true, false));
        }

//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap, item, Item.PARSE_ALARM_DATE);
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap2, () -> item.getAlarmDateD(), (d) -> item.setAlarmDate(d));
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<set>", parseIdMap2,
        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("", parseIdMap2,
                () -> itemLS.getAlarmDateD(),
                (d) -> item.setAlarmDate(d));
//<editor-fold defaultstate="collapsed" desc="comment">
//        mainCont.add(new Label(Item.ALARM_DATE)).add(addDatePickerWithClearButton(alarmDate));
//        mainCont.add(new Label(Item.ALARM_DATE)).add(alarmDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.ALARM_DATE, new SwipeClearContainer(BoxLayout.encloseXNoGrow(alarmDate), ()->alarmDate.setDate(new Date(0))), Item.ALARM_DATE_HELP));
//        if (false)mainCont.add(layout(Item.ALARM_DATE, alarmDate.makeContainerWithClearButton(), Item.ALARM_DATE_HELP));
//</editor-fold>
//        mainCont.add(layout(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)), true));
        mainCont.add(layout(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)), true, false, false));
        int remainingIndex = mainCont.getComponentCount() - 1; //store the index at which to insert remainingEffort

        MyTimePicker remainingEffort;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (!item.isProject()) {
//            remainingEffort = new MyTimePicker(parseIdMap2, () -> (int) item.getRemainingEffortInMinutes(), (i) -> item.setRemainingEffortInMinutes((int) i));
////            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(addTimePickerWithClearButton(remainingEffort));
////            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(remainingEffort.makeContainerWithClearButton());
//            mainCont.add(layout(Item.EFFORT_REMAINING, remainingEffort.makeContainerWithClearButton(), "**"));
//        } else {
//            mainCont.add(layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTime(item.getRemainingEffort()), "Button"), "**"));
//            remainingEffort = new MyTimePicker(parseIdMap2, () -> (int) item.getRemainingEffort(false) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setRemainingEffort((int) i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
//        }
//</editor-fold>

        //Categories
        if (locallyEditedCategories == null) {
            locallyEditedCategories = new ArrayList(itemLS.getCategories()); //create a copy of the categories that we can edit locally in this screen; only initialize once to keep value between calling categoryPicker
        }
//        SpanButton categoriesButton = new SpanButton(); //DOESN'T WORK WITH SPANBUTTON
        WrapButton categoriesButton = new WrapButton();
        Command categoryEditCmd = new MyReplayCommand("PickCategories", "") { //"<click to set categories>"
            @Override
            public void actionPerformed(ActionEvent evt) {
                ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem.this);
                screenCatPicker.setDoneUpdater(() -> {
                    categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
                    categoriesButton.revalidate(); //layout new list of categories, working??
                    parseIdMap2.put("ItemScreen.EditedCategories", () -> {
                        if (localSave) {
                            saveNewCategories(locallyEditedCategories);
                        } else {
                            item.updateCategories(locallyEditedCategories); //TODO this won't work with Cancel - need to store the update in parsemap and only update the button text                        //DAO.getInstance().save(item); //NOT neeeded here since saved when exiting screen
                        }
                    });
                });
                screenCatPicker.show();
            }
        };
        categoriesButton.setCommand(categoryEditCmd);

//<editor-fold defaultstate="collapsed" desc="comment">
//        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(item.getCategories()), "<click to set categories>"));
//        categoriesButton.setUIID("TextField");
//        mainCont.add(new Label(Item.CATEGORIES)).add(categoriesButton);
//</editor-fold>
//        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "<set>")); //"<click to set categories>"
        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
        mainCont.add(layout(Item.CATEGORIES, categoriesButton, "**", false, false, false));

        //REPEAT RULE
//        SpanButton repeatRuleButton = new SpanButton();
        WrapButton repeatRuleButton = new WrapButton();
//        RepeatRule 
//        locallyEditedRepeatRule = item.getRepeatRule();
        Command repeatRuleEditCmd = new MyReplayCommand("EditRepeatRules", "") { //DON'T set a string since SpanButton shows both Command string and SpanLabel string
            @Override
            public void actionPerformed(ActionEvent evt) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (Display)
//                if (locallyEditedCategories == null) {
//                    locallyEditedCategories = new ArrayList(item.getCategories()); //create a copy of the categories that we can edit locally in this screen; only initialize once to keep value between calling categoryPicker
//                }
//                ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem.this);
//                screenCatPicker.setDoneUpdater(() -> {
//                    repeatRuleButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "<click to set categories>"));
//                    parseIdMap2.put("EditedCategories", () -> {
//                        item.updateCategories(locallyEditedCategories);
//                    });
//                    ScreenItem.this.revalidate();
//                });
//                screenCatPicker.show();
//                if (dueDate.getDate().getTime() == 0) {
//                    Dialog.show("INFO", "Please define " + Item.DUE_DATE + " first", "OK", null);
//                    return;
//                }
//</editor-fold>
                if (orgRepeatRule == null) {
                    orgRepeatRule = item.getRepeatRule(); //only do this the very first time
                }
                if (orgRepeatRule != null && !orgRepeatRule.isRepeatInstanceInListOfActiveInstances(item)) {
//                    Dialog.show("INFO", "Once a repeating task has been set " + ItemStatus.DONE + " or " + ItemStatus.CANCELLED + " the " + Item.REPEAT_RULE + " definition cannot be edited anymore", "OK", null);
//                    Dialog.show("INFO", Format.f("Once a repeating task has been set {0} or {1} the {2} definition cannot be edited from this task anymore", ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), Item.REPEAT_RULE), "OK", null);
                    Dialog.show("INFO", Format.f("Once a repeating task has been set [DONE] or [CANCELLED] the [REPEAT_RULE] definition cannot be edited from this task anymore"), "OK", null);
                    return;
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (orgRepeatRule == null && editedRepeatRuleCopy == null) {
//                    editedRepeatRuleCopy = new RepeatRuleParseObject(); //if no rule exists already, create a fresh one
//                } else {
////                    editedRepeatRuleCopy = item.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
//                    editedRepeatRuleCopy = new RepeatRuleParseObject(); //make a copy of the *original* repeatRule
//                    item.getRepeatRule().copyMeInto(editedRepeatRuleCopy, true); //make a full (hence 'true') copy of the *original* repeatRule
//                }//                repeatRuleCopyBeforeEdit = locallyEditedRepeatRule.cloneMe(); //used to check if the original rule has been edited
//</editor-fold>
                if (orgRepeatRule == null) { //no previous repeatRule
                    if (locallyEditedRepeatRule == null) {
                        locallyEditedRepeatRule = new RepeatRuleParseObject(); //if no rule exists already, create a fresh one
                    }
                } else {
                    if (locallyEditedRepeatRule == null) {
//                    editedRepeatRuleCopy = item.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
                        locallyEditedRepeatRule = new RepeatRuleParseObject(); //make a copy of the *original* repeatRule
                        item.getRepeatRule().copyMeInto(locallyEditedRepeatRule, true); //make a full (hence 'true') copy of the *original* repeatRule
                    }
                }//                repeatRuleCopyBeforeEdit = locallyEditedRepeatRule.cloneMe(); //used to check if the original rule has been edited
//                if (repeatRuleCopyForEdited == null && item.getRepeatRule() != null) {
//                    repeatRuleCopyForEdited = item.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
//                }
                ASSERT.that(orgRepeatRule == null
                        || (locallyEditedRepeatRule.equals(orgRepeatRule)
                        && orgRepeatRule.equals(locallyEditedRepeatRule)), "problem in cloning repeatRule");
                //                putEditedValues2(parseIdMap2);
                new ScreenRepeatRuleNew(Item.REPEAT_RULE, locallyEditedRepeatRule, item, ScreenItem.this, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (false && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited
//                        DAO.getInstance().save(locallyEditedRepeatRule); //save first to enable saving repeatInstances
//                        item.setRepeatRule(locallyEditedRepeatRule);  //TODO!! optimize and see if there's a way to check if rule was just opened in editor but not changed
////                    repeatRuleButton.setText(getDefaultIfStrEmpty(item.getRepeatRule().toString(), "<set>")); //"<click to make task/project repeat>"
////                        repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
//                    }
//</editor-fold>
                    repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(new RepeatRuleParseObject())
                            //                            ? editedRepeatRuleCopy.\toString() : null, "<set>")); //"<click to make task/project repeat>"
                            ? locallyEditedRepeatRule.toString() : null, "")); //"<click to make task/project repeat>"
                    repeatRuleButton.revalidate();
//                    if (dueDate.getDate().getTime() == 0 
//                            && locallyEditedRepeatRule.getSpecifiedStartDateD().getTime() != 0) { //NO, always use repeatRule startDate as dueDate and vice-versa (necessary when editing a rule with existing instances)
                    dueDate.setDate(locallyEditedRepeatRule.getSpecifiedStartDateD()); //set dueDate if set in RepeatRule //TODO!!!! or if due date *changed* in RepeatRule??
//                        dueDate.repaint(); //enough to refresh on screen?? NO
//                        refreshAfterEdit(); //optimize!!
                    revalidate(); //enough to update? YES
//                    }
                }, true, dueDate.getDate()).show(); //TODO false<=>editing startdate not allowed - correct???
            }
        };
//        parseIdMap2.put("REPEAT_RULE", () -> {
        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
            if (localSave) {
                saveNewRepeatRule(locallyEditedRepeatRule);
            } else {
//            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited //NO need to test here, item.setRepeatRule() will check if the rule has changed
//            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited
                if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(new RepeatRuleParseObject()) && !locallyEditedRepeatRule.equals(orgRepeatRule)) { //if rule was edited
//                DAO.getInstance().save(locallyEditedRepeatRule); //save first to enable saving repeatInstances -NOW done in setRepeatRule
                    if (orgRepeatRule != null) { //keep the original RR (a ParseObject so don't want to recreate new objects with every edit)
                        locallyEditedRepeatRule.copyMeInto(orgRepeatRule, false);
                    } else {
                        orgRepeatRule = locallyEditedRepeatRule;
                    }
                    //TODO ensure that repeatrule is not triggered when saving locally on app exit
                    item.setRepeatRule(orgRepeatRule);  //TODO!! optimize and see if there's a way to check if rule was just opened in editor but not changed
//                    repeatRuleButton.setText(getDefaultIfStrEmpty(item.getRepeatRule().toString(), "<set>")); //"<click to make task/project repeat>"
//                repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
//            }
                }
            }
        });
        repeatRuleButton.setCommand(repeatRuleEditCmd);
        repeatRuleButton.setText(getDefaultIfStrEmpty(itemLS.getRepeatRule() != null ? itemLS.getRepeatRule().toString() : null, "")); //"<set>", "<click to make task/project repeat>"
//        repeatRuleButton.setUIID("TextField");
//        mainCont.add(layout(Item.REPEAT_RULE, makeContainerWithClearButton(repeatRuleButton, () -> {
//            orgRepeatRule = null;
//            repeatRuleButton.setText("<set>"); //TODO!!!! temporary hack!
//        }), "**"));
        mainCont.add(layout(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, () -> item.setRepeatRule(null), false, false, false));
        //TODO deleting should not delete in item but delete editcopy and when saving via parseIdMap
        //<editor-fold defaultstate="collapsed" desc="comment">
        //        if (false) {
        //            ItemList<Item> subtasksItemList = item.getItemList();
        //            boolean hasSubtasks = subtasksItemList.size() != 0;
        //            boolean showSubtasksExpanded = MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean() && hasSubtasks;
        //            if (hasSubtasks) {
        //                Container addSubtaskField = null;
        //                mainTabCont.add(BorderLayout.SOUTH, addSubtaskField);
        //                Container subtaskHeaderCont = new Container(new BorderLayout());
        //                subtaskHeaderCont.add(BorderLayout.WEST, "Subtasks");
        //                Container subtaskCont = new Container(BoxLayout.x());
        //                Button showSubtasks = new Button();
        //                showSubtasks.setCommand(Command.create(null, showSubtasksExpanded ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle, (e) -> {
        //                    MyPrefs.alwaysShowSubtasksExpandedInScreenItem.flipBoolean();
        //                    Boolean hideSubtasks = !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean();
        //                    addSubtaskField.setHidden(hideSubtasks);
        //                    subtaskCont.setHidden(hideSubtasks);
        //                    showSubtasks.setIcon(hideSubtasks ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
        //
        //                }));
        //                subtaskHeaderCont.add(BorderLayout.EAST, BoxLayout.encloseXNoGrow(
        //                        new Label(subtasksItemList.size() + ""),
        //                        new Label(MyDate.formatTimeDuration(subtasksItemList.getRemainingEffort())),
        //                        showSubtasks));
        //                mainTabCont.add(BorderLayout.NORTH, subtaskHeaderCont);
        //                subtaskCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
        ////<editor-fold defaultstate="collapsed" desc="comment">
        //////                    for (Item item:subtasksItemList) {
        ////            for (int i = 0, size = subtasksItemList.size(); i < size; i++) {
        ////                Item item = (Item) subtasksItemList.getItemAt(i);
        ////                subtaskCont.add(ScreenListOfItems.buildItemContainer(ScreenItem.this, item, subtasksItemList));
        ////            }
        ////</editor-fold>
        //                subtaskHeaderCont.setHidden(!hasSubtasks);
        //                subtaskCont.setHidden(!hasSubtasks);
        ////            addSubtaskField.setHidden(!(!hasSubtasks || MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()));
        //                addSubtaskField.setHidden(hasSubtasks && !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean());
        //            }
        //
        ////        mainCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(subtasksItemList, expandedObjects));
        ////mainTabCont.North=SOUTH= subtasktree
        //            Container subtaskCont = new Container().add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
        //            mainTabCont.add(BorderLayout.SOUTH, subtaskCont);
        //
        //            int size = subtasksItemList.size();
        ////        Button expandSubtasks = new Button(Command.create((size == 0 ? Icons.iconNewToolbarStyle : null), (size != 0 ? "[" + subtasksItemList.size() + "]" : null), (ev) -> {
        //            Button expandSubtasks = new Button((size != 0 ? "[" + size + "]" : null), (size == 0 ? Icons.iconNewToolbarStyle : null));
        //            expandSubtasks.addActionListener((ev) -> {
        //                subtaskCont.setHidden(!subtaskCont.isHidden());
        //                int size2 = subtasksItemList.size();
        //                expandSubtasks.setIcon(size2 == 0 ? Icons.iconNewToolbarStyle : null);
        //                expandSubtasks.setText(size2 != 0 ? "[" + size2 + "]" : null);
        //                animateLayout(300);
        //            });
        //            mainTabCont.add(BorderLayout.CENTER, BoxLayout.encloseX(
        //                    new Label("Subtasks"),
        //                    expandSubtasks,
        //                    new Label(MyDate.formatTimeDuration(subtasksItemList.getRemainingEffort()))));
        //        }
        //</editor-fold>

//SUBTASKS
        //        mainTabCont.add(BorderLayout.SOUTH, new SubtaskContainer(item, ScreenItem.this, item, templateEditMode));
        mainTabCont
                .add(BorderLayout.SOUTH, new SubtaskContainer(item, ScreenItem.this, templateEditMode));
        //TODO!!!!! editing of subtasks should be local (and saved locally on app exit)
//        mainTabCont.add(BorderLayout.SOUTH, new SubtaskContainer(item, item, templateEditMode));

//<editor-fold defaultstate="collapsed" desc="comment">
//                new Button(Command.create((size == 0 ? Icons.iconNewToolbarStyle : null), (size!=0?"[" + subtasksItemList.size() + "]":null), (ev) -> {
//                    if (subtaskCont.getComponentCount()==0) {
//                        subtaskCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
////                        mainTabCont.add(BorderLayout.SOUTH, ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//                        animateHierarchy(300);
//                    } else {
//                        subtaskCont.setHidden(!subtaskCont.isHidden());
//                        animateLayout(300);
////                        animateHierarchy(300);
//                    }
//                })),
//                new Label(MyDate.formatTimeDuration(subtasksItemList.getRemainingEffort()))));
//</editor-fold>
//        parseIdMap2.put("subtasksItemList", () -> item.setList(subtasksItemList.getList())); //NOT needed, since all edits to subtasks are save directly
//        mainTabCont.add(BorderLayout.SOUTH, ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//        parseIdMap2.put("subtasksItemList", () -> item.setList(subtasksItemList.getList()));
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container subtasksCont = new Container(BoxLayout.y());
//        List subtasks = item.getList();
//        if (subtasks.size() == 0) {
//    private MyTree2 createSubTaskTreeXXX(MyTreeModel treeModel) {
//        MyTree2 subTree = new MyTree2(treeModel) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> {
//                    subTaskTree.refresh();
//                    subTaskTree.revalidate();
//                }, false, //selectionMode not allowed for subtasks
//                        null); //TODO save expandedObjects even for subtasks
//                setIndent(cmp, depth);
//                return cmp;
//            }
//        };
//        return subTree;
//    }
//    protected Container buildContentPaneForItemListXXX(ItemList itemList) {
////        DAO.getInstance().fetchAllItemsIn(itemList, false); //fetch subtasks
//        subTaskTree = createSubTaskTreeXXX(itemList);
//
////        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        Container cont = new Container(BoxLayout.y());
//        cont.setScrollableY(true);
//        cont.add(subTaskTree);
////        cont.setDraggable(true);
//        subTaskTree.setDropTarget(true);
//        return cont;
//    }
//        } else {
//
//        }
//        if (false) {
//            tabs.addTab("Subtasks", null, new Button(Command.create("Edit subtasks", null, (e) -> {
//                ItemList itemList = item.getItemList();
////                DAO.getInstance().fetchAllItemsIn((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                putEditedValues2(parseIdMap2);
//                new ScreenListOfItems(item.getText(), itemList, ScreenItem.this, (iList) -> {
//                    item.setItemList(itemList);
//                    DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    refreshAfterEdit(); //necessary to update sum of subtask effort
//                }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                ).show();
//            })));
//        }
//</editor-fold>
        //TAB TIME
        Container timeCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        timeCont.setScrollableY(true);
        tabs.addTab("Time", null, timeCont);

//        MyTimePicker effortEstimate;
        MyTimePicker actualEffort;
//        SpanLabel actualExplanation = new SpanLabel("Setting '" + Item.EFFORT_ACTUAL + "' will automatically set " + Item.STATUS + " to " + ItemStatus.ONGOING);
        if (itemLS.isProject()) {

//<editor-fold defaultstate="collapsed" desc="comment">
//get the effort as the sum of subtasks (no update possible)
//            mainCont.add(layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTime(item.getRemainingEffort()), "Button"), "**"));
//            mainCont.addComponent(remainingIndex, layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTimeDuration(item.getRemainingEffort()), "Button"), "**")); //hack to insert after alarmDate field
//            mainCont.addComponent(remainingIndex, layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTimeDuration(item.getRemainingEffortNoDefault()), "Button"), "**", false, true, true)); //hack to insert after alarmDate field
//</editor-fold>
            mainCont.addComponent(remainingIndex, layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getRemainingEffortNoDefault()), "LabelFixed"), "**", false, true, true)); //hack to insert after alarmDate field

//            timeCont.add(layout(Item.EFFORT_ACTUAL_SUBTASKS, new Label(MyDate.formatTimeDuration(item.getActualEffort()), "Button"), Item.EFFORT_ACTUAL_SUBTASKS_HELP, false, true, false));
            timeCont.add(layout(Item.EFFORT_ACTUAL_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getActualEffort()), "LabelFixed"), Item.EFFORT_ACTUAL_SUBTASKS_HELP, false, true, true));
//<editor-fold defaultstate="collapsed" desc="comment">
//            timeCont.add(layout(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatTimeDuration(item.getEffortEstimateInMinutes()), "Button"), "**"));
//            timeCont.add(layout(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatTimeDuration(item.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS), "Button"), Item.EFFORT_ESTIMATE_SUBTASKS, false, true, false));
//</editor-fold>
            timeCont.add(layout(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS), "LabelFixed"), Item.EFFORT_ESTIMATE_SUBTASKS, false, true, true));
            //get the effort for the project task itself:
            remainingEffort = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getRemainingEffort(false, false) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setRemainingEffort((int) i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
//            timeCont.add(layout(Item.EFFORT_REMAINING_PROJECT, remainingEffort.makeContainerWithClearButton(), "**"));
            timeCont.add(layout(Item.EFFORT_REMAINING_PROJECT, remainingEffort, Item.EFFORT_REMAINING_PROJECT_HELP, true, false, false));

            actualEffort = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getActualEffort(true) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setActualEffort((int) i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
//            timeCont.add(layout(Item.EFFORT_ACTUAL_PROJECT, actualEffort.makeContainerWithClearButton(), actualExplanation));
            timeCont.add(layout(Item.EFFORT_ACTUAL_PROJECT, actualEffort, Item.EFFORT_ACTUAL_PROJECT_HELP, true, false, false));

//            effortEstimate = new MyTimePicker(parseIdMap2, () -> (int) item.getEffortEstimate(false) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setEffortEstimate(i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
//            timeCont.add(layout(Item.EFFORT_ESTIMATE_PROJECT, effortEstimate.makeContainerWithClearButton(), "**"));
            timeCont.add(layout(Item.EFFORT_ESTIMATE_PROJECT, effortEstimate, Item.EFFORT_ESTIMATE_PROJECT_HELP, true, false, false));
        } else {
//            actualEffort = new MyTimePicker(parseIdMap2, () -> (int) item.getActualEffortInMinutes(), (i) -> item.setActualEffortInMinutes((int) i));
            actualEffort = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getActualEffort() / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setActualEffort((int) i * MyDate.MINUTE_IN_MILLISECONDS));
//<editor-fold defaultstate="collapsed" desc="comment">
//            timeCont.add(new Label(Item.EFFORT_ACTUAL)).add(addTimePickerWithClearButton(actualEffort)).add(actualExplanation);
//            timeCont.add(new Label(Item.EFFORT_ACTUAL)).add(actualEffort.makeContainerWithClearButton()).add(actualExplanation);
//            timeCont.add(layout(Item.EFFORT_ACTUAL, actualEffort.makeContainerWithClearButton(), actualExplanation));
//</editor-fold>
            timeCont.add(layout(Item.EFFORT_ACTUAL, actualEffort, Item.EFFORT_ACTUAL_HELP, true, false, false));

//            effortEstimate = new MyTimePicker(parseIdMap2, () -> (int) item.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setEffortEstimate((int) i * MyDate.MINUTE_IN_MILLISECONDS));
//            timeCont.add(new Label(Item.EFFORT_ESTIMATE)).add(addTimePickerWithClearButton(effortEstimate));
//            timeCont.add(new Label(Item.EFFORT_ESTIMATE)).add(effortEstimate.makeContainerWithClearButton());
//            timeCont.add(layout(Item.EFFORT_ESTIMATE, effortEstimate.makeContainerWithClearButton(), "**"));
            timeCont.add(layout(Item.EFFORT_ESTIMATE, effortEstimate, Item.EFFORT_ESTIMATE_HELP, true, false, false));

//            remainingEffort = new MyTimePicker(parseIdMap2, () -> (int) item.getRemainingEffortInMinutes(), (i) -> item.setRemainingEffortInMinutes((int) i));
            remainingEffort = new MyTimePicker(parseIdMap2, () -> (int) itemLS.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setRemainingEffort((int) i * MyDate.MINUTE_IN_MILLISECONDS));
//<editor-fold defaultstate="collapsed" desc="comment">
//            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(addTimePickerWithClearButton(remainingEffort));
//            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(remainingEffort.makeContainerWithClearButton());
//            timeCont.add(layout(Item.EFFORT_REMAINING,remainingEffort.makeContainerWithClearButton(),"**"));
//            mainCont.addComponent(remainingIndex, layout(Item.EFFORT_REMAINING, remainingEffort.makeContainerWithClearButton(), "**")); //hack to insert
//</editor-fold>
            mainCont.addComponent(remainingIndex, layout(Item.EFFORT_REMAINING, remainingEffort, Item.EFFORT_REMAINING_HELP, true, false, false)); //hack to insert
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        ActionEvent ae = new ActionEvent
        actualEffort.addActionListener(new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                status.setStatus(Item.updateStatusOnActualChange(item.getActualEffort(), actualEffort.getTime(), item.getStatus(), status.getStatus()));
                status.animate();
            }
        });

        if (false) {
            actualEffort.addActionListener((e) -> {
//            if (actualEffort.getTime() > 0) {
//                if (item.getStatus() != ItemStatus.ONGOING && status.getStatus() == item.getStatus()/*status not manually changed*/) {
//                    status.setStatus(ItemStatus.ONGOING);
//                }
//            } else if (item.getStatus() == ItemStatus.ONGOING && status.getStatus() == item.getStatus()) {
//                status.setStatus(ItemStatus.CREATED);
//            }
//            ItemStatus oldStatus = status.getStatus();
                status.setStatus(Item.updateStatusOnActualChange(item.getActualEffort(), actualEffort.getTime(), item.getStatus(), status.getStatus()));
                status.animate();
            });
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            actualEffort.addActionListener((e) -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (actualEffort.getTime() > 0) {
////                if (item.getStatus() != ItemStatus.ONGOING && status.getStatus() == item.getStatus()/*status not manually changed*/) {
////                    status.setStatus(ItemStatus.ONGOING);
////                }
////            } else if (item.getStatus() == ItemStatus.ONGOING && status.getStatus() == item.getStatus()) {
////                status.setStatus(ItemStatus.CREATED);
////            }
////            ItemStatus oldStatus = status.getStatus();
////</editor-fold>
//                status.setStatus(Item.updateStatusOnActualChange(item.getActualEffort(), actualEffort.getTime(), item.getStatus(), status.getStatus()));
////<editor-fold defaultstate="collapsed" desc="comment">
////            ItemStatus newStatus = status.getStatus();
////UI: dates for done, started, etc are only updated when saving the item
////TODO update dates (see above)
////            if (oldStatus != newStatus) {
////
////                if (status.getStatus() == ItemStatus.ONGOING) {
////
////                }
////            }
////</editor-fold>
//                status.animate();
//            });
//        }
//</editor-fold>
        //if actual is manually set to 0, and user clicks the status checkbox, then the status should be set to CREATED (otherwise it is set to ONGOING)
        status.setIsItemOngoing(() -> {
//            return (item.getActualEffort() > 0 && actualEffort.getTime()>0); //not ongoing if either item.actuaEeffort==0 OR actualEffort has been set to 0 by user// || actualEffort.getTime() > 0; //actualEffort.getTime() SHOULD not be needed since first setting actualEff manually and then clicking on a Done/Cancelled checkbox should be an edge case(??)
            return (actualEffort.getTime() > 0); //whatever value is currently set in UI is used (no need to check item.getActualEffort())
        });

        MyDatePicker waitingTill = new MyDatePicker(parseIdMap2, () -> itemLS.getWaitingTillDateD(), (d) -> item.setWaitingTillDate(d)); //"<wait until this date>",
//        timeCont.add(new Label(Item.WAIT_DATE)).add(addDatePickerWithClearButton(waitingTill));
//        timeCont.add(new Label(Item.WAIT_DATE)).add(waitingTill.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAIT_UNTIL_DATE, waitingTill.makeContainerWithClearButton(), "**"));
        timeCont.add(layout(Item.WAIT_UNTIL_DATE, waitingTill, Item.WAIT_UNTIL_DATE_HELP));

        MyDateAndTimePicker waitingAlarm = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getWaitingAlarmDateD(), (d) -> item.setWaitingAlarmDate(d)); //"<waiting reminder this date>", 
//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(addDatePickerWithClearButton(waitingAlarm));
//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(waitingAlarm.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAITING_ALARM_DATE, waitingAlarm.makeContainerWithClearButton(), "**"));
        timeCont.add(layout(Item.WAITING_ALARM_DATE, waitingAlarm, Item.WAITING_ALARM_DATE_HELP));

        MyDatePicker hideUntil = new MyDatePicker(parseIdMap2, () -> itemLS.getHideUntilDateD(), (d) -> item.setHideUntilDate(d)); //"<hide task until>", 
//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(addDatePickerWithClearButton(hideUntil));
//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(hideUntil.makeContainerWithClearButton());
//        timeCont.add(layout(Item.HIDE_UNTIL, hideUntil.makeContainerWithClearButton(), "**"));
        timeCont.add(layout(Item.HIDE_UNTIL, hideUntil, Item.HIDE_UNTIL_HELP));

        MyDateAndTimePicker startByDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getStartByDateD(), (d) -> item.setStartByDate(d)); // "<start task on this date>", 
//        timeCont.add(new Label(Item.START_BY_TIME)).add(addDatePickerWithClearButton(startByDate));
//        timeCont.add(new Label(Item.START_BY_TIME)).add(startByDate.makeContainerWithClearButton());
//        timeCont.add(layout(Item.START_BY_TIME, startByDate.makeContainerWithClearButton(), "**"));
        timeCont.add(layout(Item.START_BY_TIME, startByDate, Item.START_BY_TIME_HELP));

        if (true) {
            MyDatePicker expireByDate = new MyDatePicker(parseIdMap2, () -> itemLS.getExpiresOnDateD(), (d) -> item.setExpiresOnDateD(d)); // "<auto-cancel on date>", 
//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(addDatePickerWithClearButton(expireByDate));
//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(expireByDate.makeContainerWithClearButton());
//            timeCont.add(layout(Item.AUTOCANCEL_BY, expireByDate.makeContainerWithClearButton(), "**"));
            timeCont.add(layout(Item.AUTOCANCEL_BY, expireByDate, Item.AUTOCANCEL_BY_HELP));
        }

        //TAB PRIO
        Container prioCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        prioCont.setScrollableY(true);
        tabs.addTab("Prio", null, prioCont);

//        MyStringPicker priority = new MyStringPicker(new String[]{"None", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2, () -> item.getPriority(), (i) -> item.setPriority(i));
//        cont.add(new Label("Priority")).add(priority);
//        prioCont.add(Item.PRIORITY).add(priority);
        MyComponentGroup priority = new MyComponentGroup(new String[]{"-", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2,
                () -> itemLS.getPriority() == 0 ? "" : itemLS.getPriority() + "",
                (s) -> item.setPriority(Integer.parseInt(s.length() == 0 ? "0" : s)));
        prioCont.add(layout(Item.PRIORITY, priority, Item.PRIORITY_HELP, true, false, true));

        MyComponentGroup importance = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
                () -> itemLS.getImportance() == null ? "" : itemLS.getImportance().getDescription(),
                (s) -> item.setImportance(Item.HighMediumLow.getValue(s)));
//        prioCont.add(Item.IMPORTANCE).add(FlowLayout.encloseCenterMiddle(importance));
//        prioCont.add(layout(Item.IMPORTANCE, FlowLayout.encloseCenterMiddle(importance), "**"));
        prioCont.add(layout(Item.IMPORTANCE, importance, Item.IMPORTANCE_HELP, true, false, true));

        MyComponentGroup urgency = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
                () -> itemLS.getUrgency() == null ? "" : itemLS.getUrgency().getDescription(),
                (s) -> item.setUrgency(Item.HighMediumLow.getValue(s)));
//        cont.add(new Label("Urgency")).add(urgency);
//        prioCont.add(Item.URGENCY).add(FlowLayout.encloseMiddle(urgency));
//        prioCont.add(layout(Item.URGENCY, FlowLayout.encloseMiddle(urgency), "**"));
        prioCont.add(layout(Item.URGENCY, urgency, Item.URGENCY_HELP, true, false, true));

        MyComponentGroup challenge = new MyComponentGroup(Item.Challenge.getDescriptionList(), parseIdMap2,
                () -> itemLS.getChallenge() == null ? "" : itemLS.getChallenge().getDescription(),
                (s) -> item.setChallenge(Item.Challenge.getValue(s)));
//        cont.add(new Label("Difficulty")).add(challenge);
//        prioCont.add(new Label("Difficulty")).add(BorderLayout.center(Container.encloseIn(new FlowLayout(),challenge)));
        if (challenge.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, replace with the short version
            parseIdMap2.remove(challenge); //remove previous field!!
            challenge = new MyComponentGroup(Item.Challenge.getDescriptionList(true), parseIdMap2,
                    () -> itemLS.getChallenge() == null ? "" : itemLS.getChallenge().getDescription(true),
                    (s) -> item.setChallenge(Item.Challenge.getValue(s, true)));
        }
//        prioCont.add(new Label(Item.CHALLENGE)).add(FlowLayout.encloseCenterMiddle(challenge));
//        prioCont.add(new Label("TEST")).add(FlowLayout.encloseCenterMiddle(new Label("11111"),new Label("22222"),new Label("33333"),new Label("44444"),new Label("55555")));
//        prioCont.add(layout(Item.CHALLENGE, FlowLayout.encloseCenterMiddle(challenge), "**", true));
        prioCont.add(layout(Item.CHALLENGE, challenge, Item.CHALLENGE_HELP, true, false, true));

        MyComponentGroup dreadFun = new MyComponentGroup(Item.DreadFunValue.getDescriptionList(), parseIdMap2,
                () -> itemLS.getDreadFunValue() == null ? "" : itemLS.getDreadFunValue().getDescription(),
                (s) -> item.setDreadFunValue(Item.DreadFunValue.getValue(s)));
//        prioCont.add(new Label(Item.FUN_DREAD)).add(dreadFun);
//        prioCont.add(layout(Item.FUN_DREAD, FlowLayout.encloseCenterMiddle(dreadFun), Item.FUN_DREAD_HELP));
        prioCont.add(layout(Item.FUN_DREAD, dreadFun, Item.FUN_DREAD_HELP, true, false, true));

        MyNumericTextField earnedValue = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValue(), (d) -> item.setEarnedValue(d));
//        earnedValue.setColumns(2); //result: only shows/truncates to 2 columns when field is not edited

//        prioCont.add(new Label(Item.EARNED_POINTS)).add(earnedValue);
        prioCont.add(layout(Item.EARNED_POINTS, earnedValue, Item.EARNED_POINTS_HELP, true, false, false));

//        MyNumericTextField earnedValuePerHour = new MyNumericTextField("<set>", parseIdMap2, () -> item.getEarnedValuePerHour(), (d) -> {
        MyNumericTextField earnedValuePerHour = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValuePerHour(), (d) -> {
        });
//        earnedValuePerHour.setConstraint(TextArea.UNEDITABLE);
        earnedValuePerHour.setEditable(false);
        earnedValuePerHour.setUIID("LabelFixed");
//        prioCont.add(new Label(Item.EARNED_POINTS_PER_HOUR)).add(earnedValuePerHour).add(new SpanLabel("Value per hour is calculated as Value divided by the Estimate, or the sum of Remaining and Actual effort - once work has started."));
//                add(new SpanLabel(Item.EARNED_POINTS_PER_HOUR + " is calculated as " + Item.EARNED_POINTS + " divided by " + Item.EFFORT_ESTIMATE + ", and once work has started by the sum of " + Item.EFFORT_REMAINING + " and " + Item.EFFORT_ACTUAL + "."));
        prioCont.add(layout(Item.EARNED_POINTS_PER_HOUR, earnedValuePerHour,
                Item.EARNED_POINTS_PER_HOUR_HELP, true, true, true));

        //Update earnedValuePerHour by listening to actions from any of the four fields that affect it
        MyActionListener earnedValuePerHourUpdater = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //redo the calculation done by Item but manually (without updating Item since the values may be Cancelled)
                earnedValuePerHour.setText(""
                        + Item.calculateEarnedValuePerHour(
                                Item.getTotalExpectedEffort(
                                        remainingEffort.getTime() * MyDate.MINUTE_IN_MILLISECONDS,
                                        actualEffort.getTime() * MyDate.MINUTE_IN_MILLISECONDS,
                                        effortEstimate.getTime() * MyDate.MINUTE_IN_MILLISECONDS),
                                Double.valueOf(earnedValue.getText().equals("") ? "0" : earnedValue.getText())));
                earnedValuePerHour.animate(); //TODO: needed?
            }
        };
        earnedValue.addActionListener(earnedValuePerHourUpdater);
        remainingEffort.addActionListener(earnedValuePerHourUpdater);
        actualEffort.addActionListener(earnedValuePerHourUpdater);
        earnedValue.addActionListener(earnedValuePerHourUpdater);

        //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set. 
        MyActionListener estimateUpdater = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
                //update effort estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
                if (MyPrefs.getBoolean(MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem)) {
                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem));
                    if ((remainingEffort.getTime() != 0 && item.getEffortEstimate() == 0 && (effortEstimate.getTime() == 0 || forceSameValues))
                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI: 
                        effortEstimate.setTime(remainingEffort.getTime());
                        effortEstimate.repaint();
                    }
                }
            }
        };
        remainingEffort.addActionListener(estimateUpdater);

        MyActionListener remainingUpdater = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
                if (MyPrefs.getBoolean(MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem)) //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                {
                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItem));
//                    if ((effortEstimate.getTime() != 0 && item.getRemainingEffortNoDefault() == 0 && (remainingEffort.getTime() == 0 || forceSameValues))
//                            || remainingEffort.getTime()==effortEstimate.getTime()) { //UI: when to auto-update estimates
                    if ((item.getRemainingEffortNoDefault() == 0 && effortEstimate.getTime() != 0 && (remainingEffort.getTime() == 0 || forceSameValues))
                            || item.getRemainingEffortNoDefault() == 0
                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI: when to auto-update estimates
                        remainingEffort.setTime(effortEstimate.getTime() - actualEffort.getTime());
                        remainingEffort.repaint();
                    }
                }
            }
        };
        effortEstimate.addActionListener(remainingUpdater);

        //TAB STATUS FIELDS
        Container statusCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        statusCont.setScrollableY(true);
        tabs.addTab("Status", null, statusCont);

//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()),"LabelFixed");
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()));
        Label createdDate = new Label(item.getCreatedDate() == 0 ? "" : MyDate.formatDateNew(item.getCreatedDate())); //NOT use itemLS since CreatedDate is not saved locally
//        statusCont.add(new Label(Item.CREATED_DATE)).add(createdDate);
        statusCont.add(layout(Item.CREATED_DATE, createdDate, "**", true, true, true));

//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()), "LabelFixed");
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()));
        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "" : MyDate.formatDateNew(item.getLastModifiedDate()));
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        statusCont.add(layout(Item.UPDATED_DATE, lastModifiedDate, "**", true, true, true));

//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(addDatePickerWithClearButton(startedOnDate)).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate.makeContainerWithClearButton(), "Set automatically when using the timer")); //"click to set date when started"
        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate, Item.STARTED_ON_DATE_HELP)); //"click to set date when started"
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.)).add(new SpanLabel("Set automatically when using the timer"));

//        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getCompletedDateD(), (d) -> item.setCompletedDate(d));
        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getCompletedDateD(), (d) -> item.setCompletedDate(d));
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(completedDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(layout(Item.COMPLETED_DATE, completedDate.makeContainerWithClearButton(), "Set automatically when a task is completed")); //"click to set a completed date"
        statusCont.add(layout(Item.COMPLETED_DATE, completedDate, Item.COMPLETED_DATE_HELP)); //"click to set a completed date"

//        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> item.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.WAIT_WHEN_SET_WAITING_DATE)).add(dateSetWaitingDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is set Waiting"));
//        statusCont.add(layout(Item.WAIT_WHEN_SET_WAITING_DATE, dateSetWaitingDate.makeContainerWithClearButton(), "Set automatically when a task is set Waiting"));
        statusCont.add(layout(Item.WAIT_WHEN_SET_WAITING_DATE, dateSetWaitingDate, Item.WAIT_WHEN_SET_WAITING_DATE_HELP));
        dateSetWaitingDate.addActionListener((e) -> {
            if (status.getStatus() != ItemStatus.WAITING) {
                status.setStatus(ItemStatus.WAITING);
            }
        });

        MyActionListener statusListener = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //if status is set Ongoing and startedOnDate is not set and has not been set explicitly 
                //if status is changed
                //TODO!!! move this logic into Item as static method
                if (status.getStatus() != item.getStatus()) {
                    if (status.getStatus() == ItemStatus.ONGOING) {
                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                            startedOnDate.setDate(new Date());
                        }
                    } else if (status.getStatus() == ItemStatus.DONE || status.getStatus() == ItemStatus.CANCELLED) {
                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                            startedOnDate.setDate(new Date());
                        }
                        if (completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) {
                            completedDate.setDate(new Date());
                        }
                    } else if (status.getStatus() == ItemStatus.WAITING) {
                        if (dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getDateWhenSetWaiting()) {
                            dateSetWaitingDate.setDate(new Date());
                        }
                        //UI: set startedOnDate when setting Waiting (even if no effort registered)?
                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                            startedOnDate.setDate(new Date());
                        }
                    } else if (status.getStatus() == ItemStatus.CREATED) {
                        //if set back to Created, force startedOnDate and completedDate and WaitingDate and ?? back
                        //TODO!!!!
                        //set back to 0 or if the date was only changed in the UI (item.getStartedOnDate()==0)
                        if ((startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) || item.getStartedOnDate() == 0) {
                            startedOnDate.setDate(new Date(0));
                        }
                        //TODO!!!!!! check the logic for setting dates back to 0!! 
                        //TODO In general, need to have methods in Item *without* any side-effects on other fields to ensure that the fields are set to the values seen in the UI
                        //TODO extract the logic for which changed fields impact others? Add new Item.setters embedding th needed logic for updating other fields and use those in the places where the automatic updates are needed
                        if ((completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
                            completedDate.setDate(new Date(0));
                        }
                        if ((dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
                            completedDate.setDate(new Date(0));
                        }
                    }
                }
            }
        };
        status.addActionListener(statusListener);

        MyActionListener startedOnDateListener = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //if startedOnDate is set, then if status has not been set explicitly and it is Created (not Waiting), the set it 
                if (startedOnDate.getDate().getTime() != 0 && item.getStartedOnDate() == 0 && status.getStatus() == item.getStatus() && status.getStatus() == ItemStatus.CREATED) {
                    status.setStatus(ItemStatus.ONGOING);
                }
            }
        };
        startedOnDate.addActionListener(startedOnDateListener);

        MyActionListener completedOnDateListener = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                boolean completedDateSet = false;
                //if completeDate is set, then if status has not been set explicitly and it is Created/Ongoing/Waiting), the set it Completed
                if (completedDate.getDate().getTime() != 0 && item.getCompletedDate() == 0 && status.getStatus() == item.getStatus()
                        && (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.ONGOING || status.getStatus() == ItemStatus.WAITING)) {
//                    completedDateSet = true;
//                    status.setStatus(ItemStatus.DONE);
                    if (completedDate.getDate().getTime() == 0) {
                        if (actualEffort.getTime() == 0) {
                            status.setStatus(ItemStatus.CREATED);
                        } else {
                            status.setStatus(ItemStatus.ONGOING);
                        }
                    } else {
                        status.setStatus(ItemStatus.CREATED);
                    }
                    //if startedOnDate is not set, and not changed explicitly in UI, the set it to Now
                    if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                        startedOnDate.setDate(new Date());
                    }
                }

            }
        };
        completedDate.addActionListener(completedOnDateListener);

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            MyTextArea owner = new MyTextArea(Item.BELONGS_TO, 20, TextArea.ANY, parseIdMap2, () -> item.getOwnerFormatted(),
//                    (d) -> {
//                        //TODO implement editing of owner directly (~Move to another project or list)
//                    });
//            owner.setEditable(false);
//
//            Label ownerLabel = new Label(item.getOwnerFormatted(), "LabelFixed");
////        statusCont.add(new Label(Item.BELONGS_TO)).add(owner); //.add(new SpanLabel("Click to move task to other projects or lists"));
////        statusCont.add(layout(Item.BELONGS_TO, owner, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.BELONGS_TO, ownerLabel, Item.BELONGS_TO_HELP, true, true, false)); //.add(new SpanLabel("Click to move task to other projects or lists"));
////        owner.setConstraint(TextArea.UNEDITABLE); //DOESN'T WORK
//        } else {
//            if (false) {
//                Object ownerObj = item.getOwner();
//                String ownerText = ""; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO
//                if (ownerObj != null) {
//                    if (item.getOwner() instanceof Item) {
//                        ownerText = Item.PROJECT + ": " + ((Item) ownerObj).getText(); //TODO only call top-level projects for "Project"?
//                    } else if (item.getOwner() instanceof Category) {
//                        ownerText = Category.CATEGORY + ": " + ((Category) ownerObj).getText();
//                    } else if (item.getOwner() instanceof ItemList) {
//                        ownerText = ItemList.ITEM_LIST + ": " + ((ItemList) ownerObj).getText();
//                    }
//                }
//            }
//        ItemAndListCommonInterface locallyEditedOwner = item.getOwner(); //null;
//</editor-fold>
        if (locallyEditedOwner == null) {
            locallyEditedOwner = new ArrayList(); //Arrays.asList(item.getOwner())
            locallyEditedOwner.add(itemLS.getOwner());
        }
//        SpanButton editOwnerButton = new SpanButton();
        WrapButton editOwnerButton = new WrapButton();
//            final Command editOwnerCmd = Command.create(item.getOwner().getText(), null, (e) -> {
//        Command editOwnerCmd = new Command(item.getOwner() == null ? "<no owner>" : item.getOwner().getText()) {
        Command editOwnerCmd = new MyReplayCommand("EditOwner", item.getOwner() == null ? "" : item.getOwner().getText()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                List projects = DAO.getInstance().getAllProjects(false);
                projects.remove(item); //Must not be possible to select the item itself as its own owner
                ScreenObjectPicker ownerPicker
                        //<editor-fold defaultstate="collapsed" desc="comment">
                        //                        = new ScreenObjectPicker("Select " + Item.OWNER + " for " + item.getText(), DAO.getInstance().getItemListList(), locallyEditedOwner, ScreenItem.this);
                        //                ownerPicker.setDoneUpdater(() -> {
                        //                    editOwnerButton.setText(locallyEditedOwner != null ? locallyEditedOwner.getText() : "<no owner>");
                        //                    parseIdMap2.put("ItemScreen.ScreenObjectPicker", () -> item.setOwner(locallyEditedOwner));
                        //                });
                        //</editor-fold>
                        = new ScreenObjectPicker("Select " + Item.OWNER + " for " + item.getText(),
                                DAO.getInstance().getItemListList(),
                                projects,
                                locallyEditedOwner, ScreenItem.this,
                                () -> {
//                                    ItemAndListCommonInterface newOwner;
//                                    if (locallyEditedOwner.size() >= 1) {
//                                        newOwner = locallyEditedOwner.get(0);
//                                    } else {
//                                        newOwner = null;
//                                    }
                                    ItemAndListCommonInterface newOwner = locallyEditedOwner.size() >= 1 ? locallyEditedOwner.get(0) : null;
                                    editOwnerButton.setText(newOwner != null ? newOwner.getText() : ""); //"<no owner>"
//                                        editOwnerButton.revalidate(); //refresh screen?
                                    parseIdMap2.put("ItemScreen.ScreenObjectPicker", () -> {
                                        if (localSave) {
                                            saveNewOwner(newOwner);
                                        } else {
                                            item.setOwnerAndMoveFromOldOwner(newOwner);
                                        }
                                    }); //TODO!!! no need to save item setOwnerAndMoveFromOldOwner since also saved on exit from this screen
                                }, null, 0, 1, true, false, false);
                ownerPicker.show();
            }
        };
        editOwnerButton.setCommand(editOwnerCmd);
        statusCont.add(layout(Item.BELONGS_TO, editOwnerButton, Item.BELONGS_TO_HELP, true, false, false)); //.add(new SpanLabel("Click to move task to other projects or lists"));

        //DEPENDS ON
        //TODO!!!! implement DependsOn properly before enabling it
        //TODO!!! add objectPicker showing only subtasks/projects in this project
        if (false) {
            if (item.getDependingOnTask() != null) {
                MyTextArea dependsOn = new MyTextArea(Item.DEPENDS_ON, 20, TextArea.ANY, parseIdMap2, () -> {
                    Item dependingOnTask = item.getDependingOnTask();
                    return dependingOnTask == null ? "" : dependingOnTask.getText();
                },
                        (d) -> {
                            //TODO implement editing of owner directly (~Move to another project or list)
                        });
                dependsOn.setEditable(false);
            }

            Item dependingOnTask = item.getDependingOnTask();
            if (dependingOnTask != null) {
                Label dependsOnLabel = new Label(dependingOnTask == null ? "" : dependingOnTask.getText());

//        statusCont.add(new Label(Item.DEPENDS_ON)).add(dependsOn); //.add(new SpanLabel("Click to move task to other projects or lists"));
//        statusCont.add(layout(Item.DEPENDS_ON, dependsOn, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
                statusCont.add(layout(Item.DEPENDS_ON, dependsOnLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            }
        }

        //ORIGINAL SOURCE
        if (itemLS.getSource() != null) { //don't show unless defined
            if (false) {
                MyTextArea source = new MyTextArea(Item.SOURCE, 20, TextArea.ANY, parseIdMap2, () -> {
//                Item sourceTask = item.getSource();
                    return itemLS.getSource() == null ? "" : itemLS.getSource().getText();
                },
                        (d) -> {
                        });
                source.setEditable(false);
            }
            //TODO!! what happens if source is set to template or other item, then saved locally on app exit and THEN recreated via Replay???
            Label sourceLabel = new Label(itemLS.getSource() == null ? "" : item.getSource().getText(), "LabelFixed");
//            statusCont.add(new Label(Item.SOURCE)).add(source); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, source, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            statusCont.add(layout(Item.SOURCE, sourceLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            sourceLabel.setUIID();
        }

        //INTERRUPT
        MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> itemLS.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
//                statusCont.add(new Label(Item.INTERRUPT_TASK)).add(interruptTask).add(new SpanLabel("This task interrupted another task"));
        statusCont.add(layout(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task"));
//        if (item.isInteruptOrInstantTask()) {
//            MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> item.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
        if (itemLS.getTaskInterrupted() != null) {
            //TODO!!! enable deleting the taskInterrupted (in case wrong or meaningless)
            statusCont.add(layout(Item.INTERRUPT_TASK_INTERRUPTED,
                    new SpanLabel("\"" + itemLS.getTaskInterrupted() != null ? itemLS.getTaskInterrupted().getText() + "\"" : "<none>"), "**", true, false, true)); //TODO capture and save the task that was interrupted
//            } else {
//                statusCont.add(new Label(Item.INTERRUPT_OR_INSTANT_TASK)).add(interruptTask);
        }
//        }

//        Label itemObjectId = new Label(item.getObjectIdP() == null ? "<created when saved>" : item.getObjectIdP(), "LabelFixed");
        Label itemObjectId = new Label(item.getObjectIdP() == null ? "<set on save>" : item.getObjectIdP(), "LabelFixed");
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
        statusCont.add(layout(Item.OBJECT_ID, itemObjectId, "**", true, true, true));

        //TAB SUBTASKS
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont = buildContentPaneForItemList(item, itemList);
//        Container subTaskCont = buildContentPaneForItemList(item.getItemList());
//        Container subTaskCont = buildContentPaneForItemList(item.getList());
//    public MyTree(MyTreeModel model, MyTree parentTree, int depth, boolean expandThisTree, boolean expandAllLevels, boolean onlySubTree) {
//        Container subTaskCont = new MyTree(item, null, 0, true, false, true);
//        Container subTaskCont = new MyTree2(item) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Container cmp = buildItemContainer((Item) node, null, () -> true, () -> subTaskTree.refresh());
//                setIndent(cmp, depth);
//                return cmp;
//            }
//        };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.setScrollableY(true);
//        Command addNewSubTask = new Command("Test");
//        Command addNewSubTask = makeCreateNewItemCommand("", FontImage.createMaterial(FontImage.MATERIAL_ADD, getToolbar().getStyle()), ITEM_TYPE_ITEM, item.getItemList());
//        Command addNewSubTask = makeCreateNewItemCommand("", item.getItemList());
//        tabs.addTab("Subtasks", null, buildContentPaneForItemList(item.getItemList()));
//</editor-fold>
        if (false) {
            tabs.addTab("Subtasks", null, new Button(Command.create("Edit subtasks", null, (e) -> {
                ItemList itemList = item.getItemList();
//                DAO.getInstance().fetchAllItemsIn((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
                putEditedValues2(parseIdMap2);
                new ScreenListOfItems(item.getText(), itemList, ScreenItem.this, (iList) -> {
//                            itemList.setList(iList.getList());
                    item.setItemList(itemList);
                    DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                    refreshAfterEdit(); //necessary to update sum of subtask effort
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList), null); //update the container with edited content
//<editor-fold defaultstate="collapsed" desc="comment">
//                                categoryList.addItemAtIndex(category, 0);
//                                DAO.getInstance().save(categoryList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            itemList.setList(itemList.getList());
//                            DAO.getInstance().save(itemList);
//                        },
//                        (node) -> {
//                            return buildItemListContainer((ItemList) node);
//                        },
//                        (itemList) -> {
//                            ItemList newItemList = new ItemList();
//                            new ScreenItemListProperties(newItemList, ScreenListOfItemLists.this, () -> {
//                                DAO.getInstance().save(newItemList); //save before adding to itemList
//                                itemList.addItem(newItemList);
//                            }).show();
//</editor-fold>
//                }, null, false, templateEditMode
                }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                ).show();
            })));
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        else {
//            Container subTaskCont = createSubTaskTree(item);
//            tabs.addTab("Subtasks", null, subTaskCont);
//            final int subtaskTabPosition = tabs.getTabCount() - 1; //store the index of the subtask container
//            //NEW
////        toolbar.addCommandToRightBar(new Command("", iconNew) {
//            Command addNewSubTask = new Command("", Icons.iconNewToolbarStyle) {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    Item newItem = new Item();
//                    newItem.setTemplate(templateEditMode);
//                    new ScreenItem(newItem, ScreenItem.this, () -> {
////                    parseIdMap2.put(newItem, () ->{
////                            });
////                    DAO.getInstance().save(newItem); //=> need to save newItem *before* setting setOwnerTimte (due to message: "IllegalArgumentException: Persistable object must be saved before being set on a ParseObject." - TODO: check if this is really a correct Parse constraint
//                        if (item.getObjectId() == null) {
//                            item.setText(" "); //TODO remove this work-around: needed with a new item, since no other value than ACL is set, which means save() is ignored
//                            DAO.getInstance().save(item); //need to save before adding subtasks (otherwise they cannot be saved with item as owner)
//                        }
//                        newItem.setOwnerItem(item);
//                        DAO.getInstance().save(newItem); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                    ItemList itemList = item.getItemList();
////                        List itemList = item.getList();
//                        ItemList itemList = item.getItemList();
////                    itemList.addItemAtIndex(newItem, 0); //UI: add to beginning of list
////                    itemList.addItemAtIndex(newItem, itemList.getSize()); //UI: add new tasks to end of list (the natural order)
//                        itemList.add(itemList.size(), newItem); //UI: add new tasks to end of list (the natural order)
//                        item.setItemList(itemList); //item will be saved later
////                    DAO.getInstance().save(item); //Don't save here, do it later when the mother-item is saved anyway. TODO: the new subitem will be left dangling if mother-list is not saved!!
////                    subTaskCont = buildContentPaneForItemList(item.getItemList());
////                    Container newSubTaskCont = buildContentPaneForItemList(item.getItemList());
////                    Container newSubTaskCont = buildContentPaneForItemList(item.getList());
//                        //    public MyTree(MyTreeModel model, MyTree parentTree, int depth, boolean expandThisTree, boolean expandAllLevels, boolean onlySubTree) {
////                    Container newSubTaskCont = new MyTree(item, null, 0, true, false, true);
////                    Container newSubTaskCont = new MyTree2(item);
//                        Container newSubTaskCont = createSubTaskTree(item);
//
////                    tabs.replace(tabs.getTabComponentAt(tabs.getComponentIndex(subTaskCont)), newSubTaskCont, null); //replace with new subtask container to show new added tasks
////                    tabs.replace(subTaskCont, newSubTaskCont, null); //replace with new subtask container to show new added tasks
//                        Component oldSubTaskPane = tabs.getContentPane().getComponentAt(subtaskTabPosition);
//                        tabs.getContentPane().replace(oldSubTaskPane, newSubTaskCont, null); //replace with new subtask container to show new added tasks
////                    tabs.getContentPane().replace(subTaskCont, newSubTaskCont, null); //replace with new subtask container to show new added tasks
////                    subTaskCont=newSubTaskCont;
//                    }).show();
//                }
//            };
//
//            tabs.addSelectionListener(new SelectionListener() {
//                @Override
//                public void selectionChanged(int oldSelected, int newSelected) {
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                Component newSelectedComp = tabs.getTabComponentAt(newSelected);
////                if (newSelectedComp == subTaskCont) {
////                    getToolbar().addCommandToRightBar(addNewSubTask);
////                } else {
////                    ScreenItem.this.removeCommand(addNewSubTask);
//////                    addNewSubTask.remove
//////                    getToolbar().getMenuBar().removeComponent(newSelectedComp));
////                    //TODO: find a way to remove/hide the command, my question here: http://stackoverflow.com/questions/36293862/how-to-remove-a-command-added-to-the-toolbar-using-addcommandtorightbarcommand
////                }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    lastTabSelected = newSelected;
//                    if (newSelected == subtaskTabPosition) {
//                        getToolbar().addCommandToRightBar(addNewSubTask);
//                    } else {
//                        ScreenItem.this.removeCommand(addNewSubTask);
//                        //DONE: find a way to remove/hide the command, my question here: http://stackoverflow.com/questions/36293862/how-to-remove-a-command-added-to-the-toolbar-using-addcommandtorightbarcommand
//                    }
//                }
//            }
//            );
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        //Subtasks
//        String subStr = item.getItemList().toString();
//
//        if (subStr.equals("")) {
//            subStr = "<click to create subtasks>";
//        } else {
//            subStr = "Edit subtasks (" + item.getItemList().getSize() + ")";
//        }
//        Button subtasks = new Button(new Command(subStr) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                new ScreenCategoryPicker(DAO.getInstance().getCategories(), item.getCategories(), ScreenItem.this).show();
////                new ScreenItemList("Subtasks for \"" + item.getText(), item.getItemList(), ScreenItem.this, ScreenItemList.ITEM_TYPE_ITEM).show();
//                new ScreenListOfItems("Subtasks", item.getItemList(), ScreenItem.this,
//                        (itemList) -> {
//                            item.setItemList(itemList.getList());
//                            DAO.getInstance().save(item);
//                        },
//                        (node) -> {
//                            return buildItemContainer((Item) node);
//                        },
//                        (itemList) -> {
//                            Item newItem = new Item();
//                            new ScreenItem(newItem, ScreenItem.this, () -> {
//                                DAO.getInstance().save(newItem); //save before adding to itemList
//                                itemList.addItem(newItem);
//                            }).show();
//                        }
//                ).show();
//            }
//        });
//
//        subtasks.setUIID("TextField");
//        cont.add(new Label("Subtasks")).add(subtasks);
//</editor-fold>
//        TableLayout.Constraint cn = tl.createConstraint();
//        cn.setHorizontalSpan(spanButton);
//        cn.setHorizontalAlign(Component.RIGHT);
        if (lastTabSelected >= 0 && tabs.getTabCount() > 0) {
            tabs.setSelectedIndex(lastTabSelected); //keep same tab selected even if regenerating the screen
        }
        return cont;
    }

    @Override
    public void saveOnAppExit() {
//        if (item.getObjectIdP() == null) { //new item, save everything locally and restore next time
////            Storage.getInstance().writeObject(SCREEN_TITLE + "- EDITED ITEM", item); //save date
//            Storage.getInstance().writeObject(FILE_LOCAL_EDITED_ITEM, item); //save 
//
//        } else { //edited item, update item but only save locally, then restore edit fields based on locally saved values
//            putEditedValues2(parseIdMap2);
//        }
        localSave = true;
        putEditedValues2(parseIdMap2);
        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_ITEM, item); //save 
        localSave = false;
    }

    public Object restoreOnAppExit() {
        Item itemLS = null;
        //if editing of item was ongoing when app was stopped, then recover saved item
        ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM) || ReplayLog.getInstance().isReplayInProgress()); //local item => replay must/should be Ongoing
        if (ReplayLog.getInstance().isReplayInProgress() && Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM)) {
            itemLS = (Item) Storage.getInstance().readObject(FILE_LOCAL_EDITED_ITEM); //read in when initializing the Timer - from here on it is only about saving updates
        } else {
//            itemLS = this.item; //it no locally saved edits, then use item to 'feed' the edits fields
            ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM));
            deleteOnAppExit();
        }
        return itemLS;
    }

    public void deleteOnAppExit() {
        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_ITEM); //delete in case one was 
    }

    private void saveNewOwner(ItemAndListCommonInterface newOwner) {
        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_OWNER, newOwner.getObjectIdP()); //save 
    }

    private List<ItemAndListCommonInterface> restoreNewOwner() {
        List<ItemAndListCommonInterface> locallyEditedOwner = null;
//        ItemAndListCommonInterface locallyStoreOwner;
        ItemAndListCommonInterface locallyStoreOwner = (ItemAndListCommonInterface) DAO.getInstance().fetch((String) Storage.getInstance().readObject(FILE_LOCAL_EDITED_OWNER)); //save 
//        locallyStoreOwner = (ItemAndListCommonInterface) DAO.getInstance().fetchIfNeededReturnCachedIfAvail((ParseObject) locallyStoreOwner);
//        if (restoreNewOwner() != null) {
        if (locallyStoreOwner != null) {
            locallyEditedOwner = new ArrayList();
            locallyEditedOwner.add(locallyStoreOwner);
        }
        return locallyEditedOwner;
    }

    public void deleteNewOwner() {
        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_OWNER); //delete in case one was 
    }

    private void saveNewCategories(List<Category> categories) {
        List<String> catObjIds = new ArrayList();
        for (Category cat : categories) {
            catObjIds.add(cat.getObjectIdP());
        }
        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_CATEGORIES, catObjIds); //save 
    }

    private List<Category> restoreNewCategories() {
        List<String> catObjIds = (List) Storage.getInstance().readObject(FILE_LOCAL_EDITED_CATEGORIES);
        List<Category> categories = null;
        if (catObjIds != null) {
            for (String cat : catObjIds) {
                if (categories == null) {
                    categories = new ArrayList<>();
                }
                categories.add((Category) DAO.getInstance().fetch(cat));
            }
        }
        return categories;
    }

    private void saveNewRepeatRule(RepeatRuleParseObject repeatRule) {
        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_REPEAT_RULE, repeatRule); //save 
    }

    private RepeatRuleParseObject restoreNewRepeatRule() {
        return (RepeatRuleParseObject) Storage.getInstance().readObject(FILE_LOCAL_EDITED_REPEAT_RULE);
    }

}
