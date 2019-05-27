package com.todocatalyst.todocatalyst;

import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.Button;
import com.codename1.ui.Component;
import com.codename1.ui.Form;
import com.codename1.ui.Dialog;
import com.codename1.ui.Image;
import com.codename1.ui.RadioButton;
import com.codename1.ui.Tabs;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyForm.REPEAT_RULE_KEY;
import static com.todocatalyst.todocatalyst.MyForm.putEditedValues2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories -
 *
 * @author Thomas
 */
public class ScreenItem2 extends MyForm {
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
    private Item itemLS; //FromLocalStorage, read from local storage if app was stopped with unsaved edits to Item, otherwise set to edited item
//    Set locallyEditedCategories;
//    private List locallyEditedCategories = null;
//    private ItemAndListCommonInterface locallyEditedOwner = null;
//    private List<ItemAndListCommonInterface> locallyEditedOwner = null;
//    private RepeatRuleParseObject orgRepeatRule; //same instance as the item's repeatRule, must use item.setRepeatRule to ensure it is stored?
//    private RepeatRuleParseObject locallyEditedRepeatRule;
//    UpdateField updateActionOnDone;
//    private MyTree2 subTaskTree;
//    private int lastTabSelected = -1;
    private boolean templateEditMode;
//    private String FILE_LOCAL_EDITED_ITEMXXX = "ScreenItem-EditedItem";
//    private String FILE_LOCAL_EDITED_OWNER = "ScreenItem-EditedOwner";
//    private String FILE_LOCAL_EDITED_CATEGORIES = "ScreenItem-EditedCategories";
//    private String FILE_LOCAL_EDITED_REPEAT_RULE = "ScreenItem-EditedRepeatRule";
//    private boolean localSave; //true when save of item is only local (on app pause/exit). Hack to reuse putEditedValues2!
    private boolean remainingEffortSetManually = false; //true when reaminingEffort has been edited to a different value than the original one from item
    private boolean remainingEffortSetAutomatically = false; //true when effortEstimate has 'just' been set automatically (by a change to remainingEffort)
    private boolean effortEstimateSetManually = false; //true when effortEstimate has been edited to a different value than the original one from item
    private boolean effortEstimateSetAutomatically = false; //true when effortEstimate has 'just' been set automatically (by a change to remainingEffort)
    private boolean noAutoUpdateOnStatusChange = false; //true when effortEstimate has 'just' been set automatically (by a change to remainingEffort)
    private String LAST_TAB_SELECTED = "$$LastTabSelected";
//    protected static String FORM_UNIQUE_ID = "ScreenEditItem"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

    private static String REPEAT_RULE_DELETED_MARKER = "REPEAT_RULE_DELETED";
    private float TAB_ICON_SIZE_IN_MM = 4; //true when effortEstimate has 'just' been set automatically (by a change to remainingEffort)

//    ScreenItem(Item item, MyForm previousForm) { //throws ParseException, IOException {
//        this(item, previousForm, ()->{});
//    }
    ScreenItem2(Item item, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
        this(item, previousForm, doneAction, false);
    }

    private static String getScreenTitle(boolean isTemplate, String title) {
        return (isTemplate ? "TEMPLATE: " : "") + title;
    }

    ScreenItem2(Item item, MyForm previousForm, UpdateField doneAction, boolean templateEditMode) { //throws ParseException, IOException {
        this(item, previousForm, doneAction, templateEditMode, null);
    }

    ScreenItem2(Item item, MyForm previousForm, UpdateField doneAction, boolean templateEditMode, SaveEditedValuesLocally previousValues) { //throws ParseException, IOException {
//        super("Task", previousForm, doneAction);
//        super((item.isTemplate() ? "TEMPLATE: " : "") + item.getText(), previousForm, doneAction);
//        super(getScreenTitle(item.isTemplate(), item.getText()), previousForm, doneAction);
        super((item.isTemplate() ? "TEMPLATE: " : "") + item.getText(), previousForm, doneAction);
        setUniqueFormId("ScreenEditItem");
//        FILE_LOCAL_EDITED_ITEM= getTitle()+"- EDITED ITEM";
        if (false) {
            ASSERT.that(item.isDataAvailable(), () -> "Item \"" + item + "\" data not available");
        }

        if (previousValues != null) {
            this.previousValues = previousValues;
        } else {
//            this.previousValues = new SaveEditedValuesLocally(FORM_UNIQUE_ID + "-" + item.getObjectIdP());
            this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + item.getObjectIdP());
        }

        this.templateEditMode = item.isTemplate() || templateEditMode; //
        getTitleComponent().setEndsWith3Points(true);
//        ScreenItemP.item = item;
        this.item = item;
//        initLocalSaveOfEditedValues(getUniqueFormId() + item.getObjectIdP());
//        previousValues = new SaveEditedValuesLocally( getUniqueFormId() + item.getObjectIdP());
//        expandedObjects = new HashSet();
//        expandedObjects = new ExpandedObjects(FORM_UNIQUE_ID,this.item);
        expandedObjects = new ExpandedObjects(getUniqueFormId() + this.item.getObjectIdP());
        try {
            //        DAO.getInstance().deleteCategoryFromAllItems(cat);
            if (this.item != null) {
                this.item.fetchIfNeeded();
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }

        //RESTORE locally edited value (if stored on app pause/exit)
//        itemLS = (Item) restoreLocallyEditedValuesOnAppExit();
//        boolean valuesRestored = restoreEditedValuesSavedLocallyOnAppExitXXX();
//        if (itemLS != null && this.item.getObjectIdP() == null) {
//        if (valuesRestored && this.item.getObjectIdP() == null) {
//            this.item = itemLS; //if item is a new item, then we completely ignore that Item and continue with the previously locally saved values
//        } else {
//            itemLS = this.item; //if no locally saved edits, then use item to 'feed' the edits fields
//        }
        itemLS = this.item; //quick hack. TODO!! replace all references to itemLS with item
        //restore locally edited Owner
//        locallyEditedOwner = restoreNewOwner_N();
//        locallyEditedCategories = restoreNewCategories_N();
//        locallyEditedRepeatRule = restoreNewRepeatRule_N();

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
//        addCommandsToToolbar(getToolbar());//, theme);
//        buildContentPane(getContentPane());
        addCommandsToToolbar(getToolbar());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        //NOT needed to removeFromCache everything when a subtask has been added
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again

        getContentPane().removeAll(); //clear old content pane
        buildContentPane(getContentPane()); //rebuild and removeFromCache
        super.refreshAfterEdit();
//        revalidate(); //refresh form
//        restoreKeepPos();
        //TODO!!!! restore scroll position in expanded list of subtasks 
//       super();
    }

    /**
    validates a Item before saving.
    Returns null if no error, otherwise an error message string to display. 
     */
    public static boolean checkItemIsValidForSaving(String text, String comment, Date dueDate, long actual, long remaining, int nbSelectedCategories, int nbSubtasks) {

        boolean validData = text.length() != 0
                || comment.length() != 0
                || dueDate.getTime() != 0
                || actual > 0
                || remaining > 0
                || nbSelectedCategories > 0
                || nbSubtasks > 0;
        if (validData || Dialog.show("INFO", "No key data in this task, save anyway?", "Yes", "No")) {
            return true;
        } else return false;
    }

    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(() -> item.hasSaveableData()));

        setCheckIfSaveOnExit(() -> item.hasSaveableData());

        Command exitScreenItemAndUpdateAndSave = new Command("", Icons.iconBackToPrevFormToolbarStyle()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                if (getCheckIfSaveOnExit() == null || getCheckIfSaveOnExit().check()) {

                    UpdateField repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)

                    for (Object parseId : parseIdMap2.keySet()) {
//            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
                        parseIdMap2.get(parseId).update();
                    }
                    if (getUpdateActionOnDone() != null)
                        getUpdateActionOnDone().update();
                    if (repeatRule != null) {
                        if (item != null && item.getObjectIdP() == null)
                            DAO.getInstance().saveInBackground(item); //if not saved
                        repeatRule.update();
                    }
//                    putEditedValues2(parseIdMap2);
                    showPreviousScreenOrDefault(true);
                }
            }
        };
        exitScreenItemAndUpdateAndSave.putClientProperty("android:showAsAction", "withText");

        //TIMER
//        Command timerCmd = makeTimerCommand(title, iconNew, itemList);
//        toolbar.addCommandToLeftBar(cmd);
        //CANCEL
        if (MyPrefs.enableCancelInAllScreens.getBoolean()) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> {
//            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh, notably categories
////            previousForm.showBack(); //drop any changes
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//        });
//</editor-fold>

        //EDIT WORKSLOTS
//        if (!optionTemplateEditMode && !optionNoWorkTime) {
        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("EditWorkTime", "Work time", Icons.iconWorkSlot, (e) -> {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenListOfWorkSlots(item.getText(), item.getWorkSlotListN(), item, ScreenItem2.this, null, //(iList) -> {
            new ScreenListOfWorkSlots(item, ScreenItem2.this, null, //(iList) -> {
                    //                    itemList.setWorkSLotList(iList); //NOT necessary since each slot will be saved individually
                    //                    refreshAfterEdit(); //TODO CURRENTLY not needed since workTime is not shown (but could become necessary if we show subtasks and their finish time 
                    false).show();
//            }
        }));
//        }

        //TEMPLATE
        if (!templateEditMode) {
            toolbar.addCommandToOverflowMenu(CommandTracked.create("Save as template", Icons.iconSaveAsTemplate, (e) -> {
                Dialog ip = new InfiniteProgress().showInfiniteBlocking();
                //TODO add option to let user edit template after creation
                //TODO enable user to select which fields to exclude
//                putEditedValues2(parseIdMap2, item); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
                putEditedValues2(parseIdMap2); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
                Item template = new Item();
                item.copyMeInto(template, Item.CopyMode.COPY_TO_TEMPLATE);
                DAO.getInstance().saveInBackground(template);
//                TemplateList templateList = DAO.getInstance().getTemplateList();
                TemplateList templateList = TemplateList.getInstance();
                if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
                    templateList.add(0, template);
                } else {
                    templateList.add(template);
                }
                DAO.getInstance().saveInBackground((ParseObject) templateList);
                ip.dispose();
//                if (Dialog.show("INFO", "Do you want to edit the template now? You can find and edit it later under Templates.", "Yes", "No")) {
//                    new ScreenItem(template, ScreenItem.this, () -> {
//                        DAO.getInstance().save(template);
//                    }).show();
//                }
//                new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, DAO.getInstance().getTemplateList(), ScreenItem.this, (i) -> {
                new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, () -> TemplateList.getInstance(), ScreenItem2.this, (i) -> {
                }, ScreenListOfItems.OPTION_TEMPLATE_EDIT// | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                ).show();
            }, "SaveAsTemplate"));
        }

        if (true || !templateEditMode) { //UI: KEEP for templates to allow inserting another template as a sub-hierarcy under a template
            toolbar.addCommandToOverflowMenu(CommandTracked.create("Insert template", Icons.iconAddFromTemplate, (e) -> {
                //TODO!! Add "don't show again + setting to all these info popups
                if (!MyPrefs.askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem.getBoolean()
                        || Dialog.show("INFO", "Inserting a template into a task will add the values and subtasks from the template to the task. It will not overwrite any fields already defined manually in the task", "OK", "Cancel")) {
                    //TODO enable user to select which fields to exclude
//                    putEditedValues2(parseIdMap2, item); //save any already edited values before inserting the template
                    putEditedValues2(parseIdMap2); //save any already edited values before inserting the template to avoid overwriting values only entered into the screen but not stored in the Item itself (yet)
//                    Item template = pickTemplateOLD(); //TODO!!!! make this a full Screen picker like CategorySelector
                    List selectedTemplates = new ArrayList();
                    if (false) { //shouldn't be necessary
                        Form f = Display.getInstance().getCurrent(); //tip from CN1: close dialog *before* showing next form
                        if (f instanceof Dialog) {
                            ((Dialog) f).dispose();
                        }
                    }
//                    new ScreenObjectPicker(SCREEN_TEMPLATE_PICKER, DAO.getInstance().getTemplateList(), selectedTemplates, ScreenItem.this, () -> {
                    new ScreenObjectPicker(SCREEN_TEMPLATE_PICKER, TemplateList.getInstance(), selectedTemplates, ScreenItem2.this, () -> {
                        if (selectedTemplates.size() >= 1) {
                            Item template = (Item) selectedTemplates.get(0);
//                            Dialog ip = new InfiniteProgress().showInfiniteBlocking();
                            template.copyMeInto(item, Item.CopyMode.COPY_FROM_TEMPLATE);
//                            DAO.getInstance().saveTemplateCopyWithSubtasksInBackground(item);
                            DAO.getInstance().saveTemplateCopyWithSubtasksInBackground(item);
//                            locallyEditedCategories = null; //HACK needed to force update of locallyEditedCategories (which shouldn't be refreshed when eg editing subtasks to avoid losing the edited categories) 
//                            ip.dispose();
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
            }, "CreateFromTemplate"));
        }

        //DELETE
        toolbar.addCommandToOverflowMenu(CommandTracked.create("Delete", Icons.iconDelete, (e) -> {
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//            item.delete();
//            DAO.getInstance().delete(item);
            item.softDelete();
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//            showPreviousScreenOrDefault(previousForm, true);
            showPreviousScreenOrDefault(true);
        }, "DeleteItem"));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ItemSettings", "Task settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsItem("Settings tasks", ScreenItem2.this, () -> {
                refreshAfterEdit();
            }).show();
        }
        ));

        toolbar.addCommandToOverflowMenu(CommandTracked.create("Cancel", Icons.iconCancel, (e) -> {
            //TODO!!! popup to say Cancel is not implemented yet
            showPreviousScreenOrDefault(true);
        }, "Cancel"));

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
//                    subTaskTree.removeFromCache();
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
////                    subTaskTree.removeFromCache();
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
     * wraps a textArea for comments with a timeStamp button
     *
     * @param comment
     * @return
     */
//    public static Container makeCommentContainer(MyTextArea comment) {
    public static Container makeCommentContainer(MyTextField comment) {
//        MyTextArea commentField = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(),
//                TextArea.ANY, parseIdMap2, () -> itemLS.getComment(), (s) -> item.setComment(s));
//        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> {
//        }, (s) -> {
//        });
        comment.setUIID("Comment");
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
//            comment.setText(Item.addTimeToComment(comment.getText()));
////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
////comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
//        }));
//</editor-fold>
//if (false){
        comment.getAllStyles().setMarginRight(0);
        comment.getAllStyles().setPaddingRight(0);

        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(comment);
        addTimeStampToComment.getAllStyles().setMarginLeft(0);
        addTimeStampToComment.getAllStyles().setPaddingLeft(0);
        addTimeStampToComment.getAllStyles().setMarginRight(0);
        addTimeStampToComment.getAllStyles().setPaddingRight(0);
//}
//        mainCont.add(new Label(Item.COMMENT)).add(comment);
//        mainCont.add(new Label(Item.COMMENT)).add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment));
//        mainCont.add(FlowLayout.encloseIn(makeHelpButton(Item.COMMENT, "**"), addTimeStampToComment));
        Container ts = FlowLayout.encloseRight(addTimeStampToComment);
        Container all = MyBorderLayout.centerEastWest(comment, ts, null);
        all.setUIID("TextArea");
        return all;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static Container makeCommentField(TextArea comment, ItemAndListCommonInterface itemLS, ItemAndListCommonInterface item, Map<Object, UpdateField> parseIdMap2) {
//        MyTextArea commentField = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(),
//                TextArea.ANY, parseIdMap2, () -> itemLS.getComment(), (s) -> item.setComment(s));
////        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> {
////        }, (s) -> {
////        });
//        commentField.setUIID("Comment");
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
////            comment.setText(Item.addTimeToComment(comment.getText()));
//////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
//////comment.setCursor //only on TextField, not TextArea
////            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
////        }));
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        commentField.getAllStyles().setMarginRight(0);
//        commentField.getAllStyles().setPaddingRight(0);
//
//        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(commentField);
//        addTimeStampToComment.getAllStyles().setMarginLeft(0);
//        addTimeStampToComment.getAllStyles().setPaddingLeft(0);
//        addTimeStampToComment.getAllStyles().setMarginRight(0);
//        addTimeStampToComment.getAllStyles().setPaddingRight(0);
//
////        mainCont.add(new Label(Item.COMMENT)).add(comment);
////        mainCont.add(new Label(Item.COMMENT)).add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment));
////        mainCont.add(FlowLayout.encloseIn(makeHelpButton(Item.COMMENT, "**"), addTimeStampToComment));
//        Container ts = FlowLayout.encloseRight(addTimeStampToComment);
//        Container all = BorderLayout.centerEastWest(commentField, ts, null);
//        all.setUIID("TextArea");
//        return all;
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
        tabs.setUIID("ScreenItemTabsContainer");
//       tabs.setTabPlacement(Component.BOTTOM);
//       tabs.setTabPlacement(Component.BOTTOM);
        tabs.setSwipeActivated(MyPrefs.itemEditEnableSwipeBetweenTabs.getBoolean());
        tabs.setTabTextPosition(Tabs.RIGHT); //tabs text to the right of the icons
        tabs.setTabUIID("ScreenItemTab");
        tabs.addSelectionListener((oldSel, i) -> {
//            int i = tabs.getSelectedIndex();
            if (i == 0 || i == -1 || i == oldSel) {
                previousValues.remove(LAST_TAB_SELECTED);
            } else {
                previousValues.put(LAST_TAB_SELECTED, i);
            }
        });
        cont.add(MyBorderLayout.CENTER, tabs);

        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker();
//        MyTextArea description;
        MyTextField description;
        MyDurationPicker effortEstimate;
        MyDurationPicker remainingEffort;
        //TAB MAIN
//        Container mainTabCont = new Container(new MyBorderLayout());
        Container mainCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        if (Config.TEST) mainCont.setName("MainTab");

//        mainTabCont.add(MyBorderLayout.CENTER, mainCont);
//        if (false)
        mainCont.setScrollableY(true);

//        Container mainTabCont = BorderLayout.north(mainCont);
//        Container mainTabCont = BorderLayout.north(new Container(BoxLayout.y()));
//        if (false) {
//            mainCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//        }
//        tabs.addTab("Main", Icons.iconMainTab, TAB_ICON_SIZE_IN_MM, mainTabCont);
        tabs.addTab("Main", Icons.iconMainTab, TAB_ICON_SIZE_IN_MM, mainCont);

        //        tabs.addTab("Main", null, mainTabCont);
//        MyTextField(String title, String hint, int columns, int constraint, Map<String, ScreenItemP.GetParseValue> parseIdMap, ParseObject parseObject, String parseId) {
//        MyTextField description = new MyTextField("Task", 20, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> item.getText(), (s) -> item.setText(s));
//        MyTextField description = new MyTextField(Item.DESCRIPTION_HINT, 20, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
//                () -> itemLS.getText(), (s) -> item.setText(s));
//        description = new MyTextArea(Item.DESCRIPTION_HINT, 20, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
//                () -> itemLS.getText(), (s) -> item.setText(s)) {
//            @Override
//            public void longPointerPress(int x, int y) {
//                Log.p("longPointerPress on text area");
//                //TODO!!! call templatePicker
//            }
//        };
//        description = new MyTextArea(Item.DESCRIPTION_HINT, 20, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
//        description = new MyTextArea(Item.DESCRIPTION_HINT, 20, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
        description = new MyTextField(Item.DESCRIPTION_HINT, 20, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
            @Override
            public void longPointerPress(int x, int y) {
                Log.p("longPointerPress on text area");
                //TODO!!! call templatePicker
            }
        };

//        initField(Item.DESCRIPTION, Item.DESCRIPTION_HELP, description, Item.PARSE_TEXT, () -> item.getText(), (t) -> item.setText((String) t),
//                () -> description.getText(), (t) -> description.setText((String) t), null);
        initField(Item.PARSE_TEXT, description, () -> item.getText(), (t) -> item.setText((String) t), () -> description.getText(), (t) -> description.setText((String) t));

        //https://stackoverflow.com/questions/34531047/how-to-add-donelistener-to-textarea-in-codename-one: "putClientProperty("searchField", true);, putClientProperty("sendButton", true);and putClientProperty("goButton", true); would place a button on the keyboard"
        description.putClientProperty("goButton", true);
//        description.setUIID("ScreenItemTaskText");
        description.setUIID(item.isStarred() ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
        description.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!
//        MyCheckBox status = new MyCheckBox(null, parseIdMap2, () -> item.isDone(), (b) -> item.setDone(b));
//        tabs.addSelectionListener(new SelectionListener() {
//            @Override
//            public void selectionChanged(int oldSelected, int newSelected) {
//                if (oldSelected == 0) { //main tab(??) //TODO!! why this code??
////                    setTitle(description.getText()); //update Form title in case task text has changed (why would it??)
//                    setTitle(getScreenTitle(item.isTemplate(), description.getText())); //update Form title in case task text has changed (TODO why would it??)
//                }
//                lastTabSelected = newSelected; //keep track of lastTab
//            }
//        });
        if (description.getText().length() == 0) {
            setEditOnShow(description); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        }

        //need to declare already here to use in actionListener below
        effortEstimate = new MyDurationPicker();
//        initField(Item.EFFORT_ESTIMATE, Item.EFFORT_ESTIMATE_HELP, effortEstimate, Item.PARSE_EFFORT_ESTIMATE, () -> item.getEffortEstimate(), (l) -> item.setEstimate((long) l),
//                () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l), null);
        initField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate,
                () -> item.getEstimateForProjectTaskItself(),
                (l) -> item.setEstimate((long) l, false),
                () -> effortEstimate.getDuration(),
                (l) -> effortEstimate.setDuration((long) l));

//get the effort for the project task itself:
        remainingEffort = new MyDurationPicker();
//        initField(Item.EFFORT_REMAINING, Item.EFFORT_REMAINING_HELP, effortEstimate, Item.PARSE_REMAINING_EFFORT, () -> item.getRemainingEffort(), (l) -> item.setRemaining((long) l),
//                () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l), null);
        initField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
                //                () -> item.getRemaining(false), 
                () -> item.getRemainingForProjectTaskItselfFromParse(),
                (l) -> item.setRemaining((long) l, false),
                () -> remainingEffort.getDuration(),
                (l) -> remainingEffort.setDuration((long) l));

        description.addActionListener((e) -> {
//            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
            Item.EstimateResult res = Item.getEffortEstimateFromTaskText(description.getText(), false);
            //TODO!!! create a function that will determine when to any of the user setting baesd on the values in description string
            if (res.minutes != 0) { //UI: alwyas use value in text to override previous value
                //TODO!!!!! call the same actionListener as when EsitmatePicker is changed 
                //UI: entering an estimate in the text of an item is used to set remaining effort (and not effort estimate) since this is more useful, e.g. as an easy way to update remaining while editing the item
//                remainingEffort.setTime(res.minutes); //will set remainingEffort, even if text is changed multiple times. However, manually changing effortEstimate later on won't change remainingEffort. 
                remainingEffort.setDuration(res.minutes * MyDate.MINUTE_IN_MILLISECONDS); //will set remainingEffort, even if text is changed multiple times. However, manually changing effortEstimate later on won't change remainingEffort. 
                remainingEffort.repaint();
                description.setText(res.cleaned); //update text after estimate is removed 
                description.repaint();
            }
//            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
            setTitle((item.isTemplate() ? "TEMPLATE: " : "") + description.getText());
        }); //update the form title when text is changed
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(this, description, item, 5000, () -> item.setText(description.getText()));

        //STATUS
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyCheckBox status = new MyCheckBox(itemLS.getStatus(), (oldStatus, newStatus) -> {        }); //, null);
//        initField(Item.STATUS, Item.STATUS_HELP, status, Item.PARSE_STATUS, () -> item.getStatus(), (t) -> item.setStatus((ItemStatus) t),
//                () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t), null);
//</editor-fold>
        MyCheckBox status = new MyCheckBox(itemLS.getStatus()); //, null);
        initField(Item.PARSE_STATUS, status,
                () -> item.getStatus().toString(),
                (enumStr) -> item.setStatus((ItemStatus.valueOf((String) enumStr)), false), //item.setStatus((ItemStatus) t, false),
                () -> status.getStatus().toString(), //status.getStatus(), 
                (enumStr) -> status.setStatus(ItemStatus.valueOf((String) enumStr)));
//<editor-fold defaultstate="collapsed" desc="comment">
//        parseIdMap2.put(status, () -> {
//            if (!item.getStatus().equals(status.getStatus())) {
//                item.setStatus(status.getStatus());
//            }
//        });
//        Container taskCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
//</editor-fold>
        Container taskCont = new Container(new MyBorderLayout());

        //STARRED
//        CheckBox starredcb = new CheckBox(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        Button starred = new Button(itemLS.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        Button starred = new RadioButton(); //TODO change to use RadionButton and automatically switch icon when selected/unselected --> RB no good
        Button starred = new Button(); //TODO change to use RadionButton and automatically switch icon when selected/unselected
        starred.setUIID("ScreenItemStarred");
//        starred.addActionListener((e) -> starred.setIcon(starred.getIcon() == Icons.iconStarUnselectedLabelStyle
//                ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle));
        starred.addActionListener((e) -> {
            boolean setStarActive = starred.getMaterialIcon() == Icons.iconStarUnselected;
            starred.setMaterialIcon(setStarActive ?  Icons.iconStarSelected:Icons.iconStarUnselected);
            description.setUIID(setStarActive ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
            description.repaint();
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (item.isStarInheritedFrom(starActive))
//                starred.setUIID(starActive ? "ScreenItemStarredActive" : "ScreenItemStarred");
//            else
//                starred.setUIID(starActive ? "ScreenItemStarredActive" : "ScreenItemStarred");
//</editor-fold>
            starred.setUIID(item.isStarInheritedFrom(setStarActive) ? "ScreenItemStarredInherited" : "ScreenItemStarred");
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        starred.setToggle(true);
//        if (false) {
//        starred = new Button(itemLS.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//            starred.addActionListener((e) -> {
//                if (false) {
//                    item.setStarred(!item.isStarred()); //shouldn't change item.starred until saving via parseIdMap2
//                    starred.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//                }
//                starred.setIcon(starred.getIcon() == Icons.iconStarSelectedLabelStyle ? Icons.iconStarUnselectedLabelStyle : Icons.iconStarSelectedLabelStyle);
//                starred.repaint();
////            DAO.getInstance().save(item); //??
//            });
//            parseIdMap2.put(starred, () -> item.setStarred(starred.getIcon() == Icons.iconStarSelectedLabelStyle));
//        }
//</editor-fold>
//        Component starredComp = initField(Item.STARRED, Item.STARRED_HELP, starred, Item.PARSE_STARRED, () -> item.isStarred(), (b) -> item.setStarred((boolean) b),
        initField(Item.PARSE_STARRED, starred,
                () -> item.isStarred(),
                (b) -> item.setStarred((boolean) b),
                //                () -> starred.getIcon().equals(Icons.iconStarSelectedLabelStyle),
                //                () -> starred.getIcon().equals(Icons.iconStarSelectedLabelStyle),
                //                (b) -> starred.setIcon((boolean) b ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle),
                () -> starred.getMaterialIcon() == Icons.iconStarSelected,
                (b) -> starred.setMaterialIcon((boolean) b ? Icons.iconStarSelected : Icons.iconStarUnselected),
                () -> item.isStarInheritedFrom(starred.getMaterialIcon() == Icons.iconStarSelected)
        //                (b) -> starred.setIcon((boolean) b ?  Icons.iconStarUnselectedLabelStyle:Icons.iconStarSelectedLabelStyle )
        //                    starred.repaint();
        ); //add taskCont just to avoid creating an unnecessary field container
        taskCont.add(MyBorderLayout.CENTER, description);
        updateUIIDForInherited(item.isStarInheritedFrom(starred.getMaterialIcon() == Icons.iconStarSelected), starred);

//        taskCont.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, starred);
//        taskCont.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, starred);
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
//        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
//                () -> itemLS.getComment(), (s) -> item.setComment(s));
//        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        MyTextField comment = new MyTextField(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment.setSingleLineTextArea(false);
        Container commentField = makeCommentContainer(comment);
        AutoSaveTimer commentSaveTimer = new AutoSaveTimer(this, comment, item, 5000, () -> item.setComment(comment.getText())); //NORMAL that appear as non-used since running in background!!

//        mainCont.add(initField(Item.COMMENT, Item.COMMENT_HELP, comment, Item.PARSE_COMMENT, () -> item.getComment(), (t) -> item.setComment((String) t),
        initField(Item.PARSE_COMMENT, comment, () -> item.getComment(), (t) -> item.setComment((String) t), () -> comment.getText(), (t) -> comment.setText((String) t));
//        comment.putClientProperty("goButton", true);
//        comment.setUIID("Comment");
////<editor-fold defaultstate="collapsed" desc="comment">
////        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
////            comment.setText(Item.addTimeToComment(comment.getText()));
//////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
//////comment.setCursor //only on TextField, not TextArea
////            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
////        }));
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        comment.getAllStyles().setMarginRight(0);
//        comment.getAllStyles().setPaddingRight(0);
//
//        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(comment);
//        addTimeStampToComment.getAllStyles().setMarginLeft(0);
//        addTimeStampToComment.getAllStyles().setPaddingLeft(0);
//        addTimeStampToComment.getAllStyles().setMarginRight(0);
//        addTimeStampToComment.getAllStyles().setPaddingRight(0);
//
////        mainCont.add(new Label(Item.COMMENT)).add(comment);
////        mainCont.add(new Label(Item.COMMENT)).add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment));
////        mainCont.add(FlowLayout.encloseIn(makeHelpButton(Item.COMMENT, "**"), addTimeStampToComment));
//        Container ts = FlowLayout.encloseRight(addTimeStampToComment);
//        Container all = BorderLayout.centerEastWest(comment, ts, null);
//        all.setUIID("TextArea");
//        ts.setFocusable(true); //make timeStamp grap events?
//        mainCont.add(LayeredLayout.encloseIn(comment, ts));
//</editor-fold>
        mainCont.add(commentField);
//        mainCont.add(comment);

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDateAndTimePicker dueDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getDueDateD(), (d) -> {
//            item.setDueDate(d);
//            //TODO!!? dialog to ask if repeatRule startDate should be updated to match due date? But not meaningful if it's just a repeatInstance with its own duedate
////            if (locallyEditedRepeatRule!=null) {
////
////            }
//        }); //"<click to set a due date>"
//</editor-fold>
        mainCont.add(layoutN(Item.STATUS, status, Item.STATUS_HELP, null, false, false, false, true));
        mainCont.add(layoutN(Item.STARRED, starred, Item.STARRED_HELP, null, false, false, false, true));

        MyDateAndTimePicker dueDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(new Label("Due")).add(dueDate);
//        mainCont.add(new Label("Due")).add(LayeredLayout.encloseIn(dueDate, FlowLayout.encloseRightMiddle(new Button(Command.create(null,Icons.iconCloseCircle,(e)->{dueDate.setDate(new Date(0));})))));
//        mainCont.add(new Label(Item.DUE_DATE)).add(addDatePickerWithClearButton(dueDate));
//        mainCont.add(new Label(Item.DUE_DATE)).add(dueDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.DUE_DATE, dueDate.makeContainerWithClearButton(), "**"));
//</editor-fold>

//        mainCont.add(initField(Item.DUE_DATE, Item.DUE_DATE_HELP, dueDate, Item.PARSE_DUE_DATE, () -> item.getDueDateD(), (t) -> item.setDueDate((Date) t),
        initField(Item.PARSE_DUE_DATE, dueDate,
                () -> item.getDueDateD(),
                (d) -> item.setDueDate((Date) d),
                () -> dueDate.getDate(),
                (d) -> dueDate.setDate((Date) d),
                () -> item.isDueDateInherited(dueDate.getDate()));
        mainCont.add(layoutN(Item.DUE_DATE, dueDate, Item.DUE_DATE_HELP));
        updateUIIDForInherited(item.isDueDateInherited(dueDate.getDate()), dueDate); //NB! MUST do *after* layoutN() which sets the UIID

//        hi.add(LayeredLayout.encloseIn(settingsLabel, FlowLayout.encloseRight(close))) //https://github.com/codenameone/CodenameOne/wiki/Basics---Themes,-Styles,-Components-&-Layouts#layered-layout
        //FINISH_TIME
//        WorkTimeSlices workTime = item.getAllocatedWorkTimeN();
        if (!item.isDone() || Config.WORKTIME_TEST) { //TEST: show the artificial/helper workSlice allocated to done tasks
//            WorkTimeSlices workTime = item.getAllocatedWorkTimeN();
            Date finishTime = item.getFinishTimeD();
            if (finishTime.getTime() != MyDate.MAX_DATE || Config.WORKTIME_TEST) {
                Button showWorkTimeDetails = new Button(MyReplayCommand.create("ShowWorkTimeDetails", MyDate.formatDateTimeNew(finishTime), null, (e) -> {
//                    new ScreenListOfWorkTime(item.getText(), item.getAllocatedWorkTimeN(), ScreenItem.this).show();
//                    new ScreenListOfWorkTime(item.getText(), item.getAllocatedWorkTimeN(), ScreenItem2.this).show();
                    new ScreenListOfWorkTime(item, item.getAllocatedWorkTimeN(), ScreenItem2.this).show();
                }));
                mainCont.add(layoutN(Item.FINISH_WORK_TIME, showWorkTimeDetails, Item.FINISH_WORK_TIME_HELP, null, true, true, true));

//                mainCont.add(initField(Item.FINISH_WORK_TIME, Item.FINISH_WORK_TIME_HELP, showWorkTimeDetails, "finishTime", () -> item.getFinishTime(), null,
                initField("finishTime", showWorkTimeDetails,
                        () -> item.getFinishTimeD(), null,
                        //                        () -> dueDate.getDate(), (d) -> dueDate.setDate((Date) d)); //WTF??
                        null, null);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap, item, Item.PARSE_ALARM_DATE);
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap2, () -> item.getAlarmDateD(), (d) -> item.setAlarmDate(d));
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<set>", parseIdMap2,
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("", parseIdMap2,
//                () -> itemLS.getAlarmDateD(),
//                (d) -> item.setAlarmDate(d));
//</editor-fold>
        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        mainCont.add(new Label(Item.ALARM_DATE)).add(addDatePickerWithClearButton(alarmDate));
//        mainCont.add(new Label(Item.ALARM_DATE)).add(alarmDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.ALARM_DATE, new SwipeClearContainer(BoxLayout.encloseXNoGrow(alarmDate), ()->alarmDate.setDate(new Date(0))), Item.ALARM_DATE_HELP));
//        if (false)mainCont.add(layout(Item.ALARM_DATE, alarmDate.makeContainerWithClearButton(), Item.ALARM_DATE_HELP));
//</editor-fold>
//        mainCont.add(layout(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)), true));
        initField(Item.PARSE_ALARM_DATE, alarmDate, () -> item.getAlarmDateD(), (d) -> item.setAlarmDate((Date) d), () -> alarmDate.getDate(), (d) -> alarmDate.setDate((Date) d));
//        mainCont.add(layoutN(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)))); //, true, false, false));
        mainCont.add(layoutN(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP)); //, true, false, false));
//        int remainingIndex = mainCont.getComponentCount() - 1; //store the index at which to insert remainingEffort
        if (false)
            mainCont.add(makeSpacer());

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (!item.isProject()) {
//            remainingEffort = new MyDurationPicker(parseIdMap2, () -> (int) item.getRemainingEffortInMinutes(), (i) -> item.setRemainingEffortInMinutes((int) i));
////            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(addTimePickerWithClearButton(remainingEffort));
////            timeCont.add(new Label(Item.EFFORT_REMAINING)).add(remainingEffort.makeContainerWithClearButton());
//            mainCont.add(layout(Item.EFFORT_REMAINING, remainingEffort.makeContainerWithClearButton(), "**"));
//        } else {
//            mainCont.add(layout(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTime(item.getRemainingEffort()), "Button"), "**"));
//            remainingEffort = new MyDurationPicker(parseIdMap2, () -> (int) item.getRemainingEffort(false) / MyDate.MINUTE_IN_MILLISECONDS, (i) -> item.setRemainingEffortXXX((int) i * MyDate.MINUTE_IN_MILLISECONDS, false, true));
//        }
//</editor-fold>
        //Categories
//<editor-fold defaultstate="collapsed" desc="comment">
//        List locallyEditedCategories;
//        if (previousValues.get(Item.PARSE_CATEGORIES) != null) {
//            locallyEditedCategories = (List) previousValues.get(Item.PARSE_CATEGORIES);
//        } else {
//            locallyEditedCategories = new ArrayList(itemLS.getCategories()); //create a copy of the categories that we can edit locally in this screen; only initialize once to keep value between calling categoryPicker
//            previousValues.put(Item.PARSE_CATEGORIES, locallyEditedCategories);
//        }
//        SpanButton categoriesButton = new SpanButton(); //DOESN'T WORK WITH SPANBUTTON
//        if (previousValues.get(Item.PARSE_CATEGORIES) == null) {
//            previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(item.getCategories()));
//        }
//        List<Category> editedCats = Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES));
//</editor-fold>
//        WrapButton categoriesButton = new WrapButton(previousValues.get(Item.PARSE_CATEGORIES) != null
//                ? getCategoriesAsCommaSeparatedString(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)))
//                : getCategoriesAsCommaSeparatedString(item.getCategories()));
//        WrapButton categoriesButton = new WrapButton(getCategoriesAsCommaSeparatedString((List<Category> )previousValues.get(Item.PARSE_CATEGORIES,item.getCategories())));
        WrapButton categoriesButton = new WrapButton();
        ActionListener refreshCategoriesButton = (e) -> {
            String commaSeparatedCategories = getCategoriesAsCommaSeparatedString(previousValues.get(Item.PARSE_CATEGORIES) != null
                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
                    : item.getCategories());
            categoriesButton.setText(commaSeparatedCategories);
        };
        //categoriesButton.getTextComponent().setRTL(true);
//<editor-fold defaultstate="collapsed" desc="comment">
//        WrapButton editOwnerButton = new WrapButton(getListAsCommaSeparatedString(editedCats));

//        Command categoryEditCmd = new MyReplayCommand("PickCategories", "") { //"<click to set categories>"
//</editor-fold>
        if (false) {
            String commaSeparatedCategories = getCategoriesAsCommaSeparatedString(previousValues.get(Item.PARSE_CATEGORIES) != null
                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
                    : item.getCategories());
        }
//        Command categoryEditCmd = new Command(getCategoriesAsCommaSeparatedString((List<String>) previousValues.get(Item.PARSE_CATEGORIES, item.getCategories()))) { //"<click to set categories>"
//        Command categoryEditCmd = new Command(commaSeparatedCategories) { //"<click to set categories>"
//        Command categoryEditCmd = new Command("") { //"<click to set categories>"
//            @Override
//            public void actionPerformed(ActionEvent evt) {
        Command categoryEditCmd = MyReplayCommand.create("EditCategories", null, null, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem2.this);
//                if (previousValues.get(Item.PARSE_CATEGORIES) != null) {
//</editor-fold>
            List<Category> catList = (previousValues.get(Item.PARSE_CATEGORIES) != null
                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)) //if previous edited value exists, use that
                    : new ArrayList(item.getCategories())); //make a copy to be able to compare the edited version to the orginal list from Item after editing
            ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(),
                    catList,
                    ScreenItem2.this, () -> {
//                    categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
                        if (catList.equals(item.getCategories())) {
                            previousValues.remove(Item.PARSE_CATEGORIES); //remove previos/edited value (nothing to store)
                            categoriesButton.setText(getCategoriesAsCommaSeparatedString(item.getCategories())); //"<click to set categories>"
                        } else {
                            previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(catList));
                            categoriesButton.setText(getCategoriesAsCommaSeparatedString(catList)); //"<click to set categories>"
                        }
//                            categoriesButton.revalidate(); //layout new list of categories, working??
                        categoriesButton.getComponentForm().revalidate(); //layout new list of categories, working??
////<editor-fold defaultstate="collapsed" desc="comment">
////                                ItemAndListCommonInterface newOwner = locallyEditedOwner.size() >= 1 ? locallyEditedOwner.get(0) : null;
////                                editOwnerButton.setText(newOwner != null ? newOwner.getText() : ""); //"<no owner>"
//////                                        editOwnerButton.revalidate(); //refresh screen?
////                                parseIdMap2.put("ItemScreen.ScreenObjectPicker", () -> {
////                                    if (localSave) {
////                                        saveNewOwner(newOwner);
////                                    } else {
////                                        item.setOwnerAndMoveFromOldOwner(newOwner);
////                                    }
////                                }); //TODO!!! no need to save item setOwnerAndMoveFromOldOwner since also saved on exit from this screen
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    parseIdMap2.put("ItemScreen.EditedCategories", () -> {
//                        if (localSave) {
//                            saveNewCategories(locallyEditedCategories);
//                        } else {
//                            item.updateCategories(locallyEditedCategories); //TODO this won't work with Cancel - need to store the update in parsemap and only update the button text                        //DAO.getInstance().save(item); //NOT neeeded here since saved when exiting screen
//                        }
//                    });
//</editor-fold>
                    });
            screenCatPicker.show();
        }
        );
        categoriesButton.setCommand(categoryEditCmd);
//        categoriesButton.setText(commaSeparatedCategories);
        refreshCategoriesButton.actionPerformed(null);
        categoriesButton.revalidate();
        parseIdMap2.put(Item.PARSE_CATEGORIES, () -> {
            if (previousValues.get(Item.PARSE_CATEGORIES) != null) {
//                item.setCategories(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)));
                item.updateCategories(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)));
            }
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        initField(Item.PARSE_CATEGORIES, categoriesButton, () -> item.getCategories(), (catlist) -> item.setCategories((List) catlist),
//                () -> Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)),
//                (catlist) -> previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList((List) catlist)));

//        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(item.getCategories()), "<click to set categories>"));
//        categoriesButton.setUIID("TextField");
//        mainCont.add(new Label(Item.CATEGORIES)).add(categoriesButton);
//        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "<set>")); //"<click to set categories>"
//        categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
//        categoriesButton.setText(getDefaultIfStrEmpty(getCategoriesAsCommaSeparatedString(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))), "")); //"<click to set categories>"
//        categoriesButton.revalidate();
//        mainCont.add(layout(Item.CATEGORIES, categoriesButton, "**", false, false, false));
//</editor-fold>
//        mainCont.add(layoutN(Item.CATEGORIES, categoriesButton, "**", null, true, false, true));
        mainCont.add(layoutN(Item.CATEGORIES, categoriesButton, Item.CATEGORIES));
        if (false)
            mainCont.add(makeSpacer());

        //REPEAT RULE
        Object editedRepeatRule = previousValues.get(Item.PARSE_REPEAT_RULE);
        String repeatRuleButtonStr;
        if (editedRepeatRule == null) { //no edits
            if (item.getRepeatRule() != null) {
                repeatRuleButtonStr = item.getRepeatRule().getText();
            } else {
                repeatRuleButtonStr = "";
            }
        } else if (editedRepeatRule.equals(REPEAT_RULE_DELETED_MARKER)) {
            repeatRuleButtonStr = "";
        } else { //if (editedRepeatRule instanceof RepeatRuleParseObject) { //NB instanceof RepeatRuleParseObject is only option possible
            assert editedRepeatRule instanceof RepeatRuleParseObject;
            repeatRuleButtonStr = ((RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE)).getText();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        WrapButton repeatRuleButton = new WrapButton(repeatVal!= null && !repeatVal.equals(REPEAT_RULE_DELETED_MARKER)
//                ? ((RepeatRuleParseObject) previousValues.get(Item.REPEAT_RULE)).getText()
//                : item.getRepeatRule() != null ? item.getRepeatRule().getText() : "");
//</editor-fold>
        WrapButton repeatRuleButton = new WrapButton();

//        RepeatRule 
//        locallyEditedRepeatRule = item.getRepeatRule();
//        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRules", "", null, (e) -> {
//        Command repeatRuleEditCmd = Command.create(repeatRuleButtonStr, null, (e) -> {
        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRule-ScreenEditItem", "", null, (e) -> {
            //TODO!!!! by making this a ReplayCommand, it is also necessary to store the edited values within the screen, otherwise the user is returned, but the values are lost => annoying!
//DON'T set a string since SpanButton shows both Command string and SpanLabel string
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (Display)
//                if (locallyEditedCategories == null) {
//                    locallyEditedCategories = new ArrayList(item.getCategories()); //create a copy of the categories that we can edit locally in this screen; only initialize once to keep value between calling categoryPicker
//                }
//                ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem.this);
//                screenCatPicker.setUpdateActionOnDone(() -> {
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
//            if (orgRepeatRule == null) {
//                orgRepeatRule = item.getRepeatRule(); //only do this the very first time
//            }
            if (item.getRepeatRule() != null && !item.getRepeatRule().isRepeatInstanceInListOfActiveInstances(item)) {
//                    Dialog.show("INFO", "Once a repeating task has been set " + ItemStatus.DONE + " or " + ItemStatus.CANCELLED + " the " + Item.REPEAT_RULE + " definition cannot be edited anymore", "OK", null);
//                    Dialog.show("INFO", Format.f("Once a repeating task has been set {0} or {1} the {2} definition cannot be edited from this task anymore", ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), Item.REPEAT_RULE), "OK", null);
                Dialog.show("INFO", Format.f("Once a repeating task has been set [DONE] or [CANCELLED] the [REPEAT_RULE] definition cannot be edited from this task anymore"), "OK", null);
                return;
            }

//            RepeatRuleParseObject locallyEditedRepeatRule
//                    = previousValues.get(Item.PARSE_REPEAT_RULE) != null
//                    ? ((RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE)) //fetch previously edited instance/copy of the repeat Rule
//                    : new RepeatRuleParseObject(item.getRepeatRule()); //create a copy if getRepeatRule returns a rule, if returns null, creates a fresh RR
            RepeatRuleParseObject locallyEditedRepeatRule;
            if (previousValues.get(Item.PARSE_REPEAT_RULE) == null || previousValues.get(Item.PARSE_REPEAT_RULE).equals(REPEAT_RULE_DELETED_MARKER)) {
                locallyEditedRepeatRule = new RepeatRuleParseObject(item.getRepeatRule()); //create a copy if getRepeatRule returns a rule, if getRepeatRule() returns null, creates a fresh RR
            } else {
                locallyEditedRepeatRule = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE); //fetch previously edited instance/copy of the repeat Rule
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
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (orgRepeatRule == null) { //no previous repeatRule
//                if (locallyEditedRepeatRule == null) {
//                    locallyEditedRepeatRule = new RepeatRuleParseObject(); //if no rule exists already, create a fresh one
//                }
//            } else {
//                if (locallyEditedRepeatRule == null) {
////                    editedRepeatRuleCopy = item.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
//                    locallyEditedRepeatRule = new RepeatRuleParseObject(); //make a copy of the *original* repeatRule
//                    item.getRepeatRule().copyMeInto(locallyEditedRepeatRule, true); //make a full (hence 'true') copy of the *original* repeatRule
//                }
//            }//                repeatRuleCopyBeforeEdit = locallyEditedRepeatRule.cloneMe(); //used to check if the original rule has been edited
//                if (repeatRuleCopyForEdited == null && item.getRepeatRule() != null) {
//                    repeatRuleCopyForEdited = item.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
//                }
//            ASSERT.that(orgRepeatRule == null
//                    || (locallyEditedRepeatRule.equals(orgRepeatRule)
//                    && orgRepeatRule.equals(locallyEditedRepeatRule)), "problem in cloning repeatRule");
//                putEditedValues2(parseIdMap2);
//            new ScreenRepeatRule(Item.REPEAT_RULE, locallyEditedRepeatRule, item, ScreenItem2.this, () -> {
//</editor-fold>
//            new ScreenRepeatRule(Item.REPEAT_RULE, (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE), item, ScreenItem2.this, () -> {
//            new ScreenRepeatRule(Item.REPEAT_RULE, item.getRepeatRule(), locallyEditedRepeatRule, item, ScreenItem2.this, () -> {
            new ScreenRepeatRule(Item.REPEAT_RULE, locallyEditedRepeatRule, item, ScreenItem2.this, () -> {
                if (locallyEditedRepeatRule.equals(item.getRepeatRule())) {
                    previousValues.remove(Item.PARSE_REPEAT_RULE);
                    repeatRuleButton.setText(item.getRepeatRule().getText()); //set to old repeatRule
                } else if (locallyEditedRepeatRule.getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
                    previousValues.put(Item.PARSE_REPEAT_RULE, REPEAT_RULE_DELETED_MARKER);
                    repeatRuleButton.setText(""); //"<click to make task/project repeat>"
                } else {
                    previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRule);
                    repeatRuleButton.setText(locallyEditedRepeatRule.getText());
                }
//                    repeatRuleButton.revalidate();
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (dueDate.getDate().getTime() == 0
//                            && locallyEditedRepeatRule.getSpecifiedStartDateD().getTime() != 0) { //NO, always use repeatRule startDate as dueDate and vice-versa (necessary when editing a rule with existing instances)
//                dueDate.setDate(locallyEditedRepeatRule.getSpecifiedStartDateD()); //set dueDate if set in RepeatRule //TODO!!!! or if due date *changed* in RepeatRule??
//                        dueDate.repaint(); //enough to removeFromCache on screen?? NO
//                        refreshAfterEdit(); //optimize!!
//</editor-fold>
//                revalidate(); //enough to update? YES //needed to allow space for additional text on RR button?!
//                repeatRuleButton.revalidate(); //enough to update? NO (overwrites label text on left)
//                repeatRuleButton.getParent().revalidate(); //enough to update? NO
                if (false) mainCont.revalidate(); //enough to update? NO
//                    }
            }, true, dueDate.getDate(), false).show(); //TODO false<=>editing startdate not allowed - correct???
        }
        );
//        parseIdMap2.put("REPEAT_RULE", () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
//            if (localSave) {
//                saveNewRepeatRule(locallyEditedRepeatRule);
//            } else {
////            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited //NO need to test here, item.setRepeatRule() will check if the rule has changed
////            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited
//                if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(new RepeatRuleParseObject()) && !locallyEditedRepeatRule.equals(orgRepeatRule)) { //if rule was edited
////                DAO.getInstance().save(locallyEditedRepeatRule); //save first to enable saving repeatInstances -NOW done in setRepeatRule
//                    if (orgRepeatRule != null) { //keep the original RR (a ParseObject so don't want to recreate new objects with every edit)
//                        locallyEditedRepeatRule.copyMeInto(orgRepeatRule, false);
//                    } else {
//                        orgRepeatRule = locallyEditedRepeatRule;
//                    }
//                    //TODO ensure that repeatrule is not triggered when saving locally on app exit
//                    item.setRepeatRule(orgRepeatRule);  //TODO!! optimize and see if there's a way to check if rule was just opened in editor but not changed
////                    repeatRuleButton.setText(getDefaultIfStrEmpty(item.getRepeatRule().toString(), "<set>")); //"<click to make task/project repeat>"
////                repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
////            }
//                }
//            }
//        });
//</editor-fold>
        repeatRuleButton.setCommand(repeatRuleEditCmd);


        /*
Meaning of previousValues.get(Item.PARSE_REPEAT_RULE):
-> undefined/null: no change of org. RR (or no RR originally)
-> RR: modified RR (*only* defined if the RR has been edited!)
-> "DELETED": the RR has been deleted
         */
//        parseIdMap2.put(Item.PARSE_REPEAT_RULE, () -> {
        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
//            if (locallyEditedRepeatRule.equals(item.getRepeatRule())) {
//                    previousValues.remove(Item.PARSE_REPEAT_RULE);
//                    repeatRuleButton.setText(item.getRepeatRule().getText()); //set to old repeatRule
//                } else {
            Object repeatRuleVal = previousValues.get(Item.PARSE_REPEAT_RULE);
            if (repeatRuleVal instanceof RepeatRuleParseObject) { //only defined if the RR has really been edited
                item.setRepeatRule((RepeatRuleParseObject) repeatRuleVal);
            } else if (repeatRuleVal != null && repeatRuleVal.equals(REPEAT_RULE_DELETED_MARKER)) {
                item.setRepeatRule(null); //delete repeatRule (if any, if null before, no change)
            } //else: ==null => do nothing (either no RR was defined before/after editing, or the rule was not changed
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        initField(Item.PARSE_REPEAT_RULE, repeatRuleButton, () -> {
//            if (previousValues.get(Item.PARSE_REPEAT_RULE) == null && item.getRepeatRule() == null) {
//                return new RepeatRuleParseObject(); //if no previous RR, create a fresh one to edit
//            } else {
//                return new RepeatRuleParseObject(item.getRepeatRule()); //edit a *copy* of the item's RR
//            }
//        }, (repRule) -> item.setRepeatRule((RepeatRuleParseObject) repRule),
//                () -> previousValues.get(Item.PARSE_REPEAT_RULE), (repRule) -> previousValues.put(Item.PARSE_REPEAT_RULE, new RepeatRuleParseObject((RepeatRuleParseObject) repRule)));

//        repeatRuleButton.setText(getDefaultIfStrEmpty(itemLS.getRepeatRule() != null ? itemLS.getRepeatRule().toString() : null, "")); //"<set>", "<click to make task/project repeat>"
//        repeatRuleButton.setUIID("TextField");
//        mainCont.add(layout(Item.REPEAT_RULE, makeContainerWithClearButton(repeatRuleButton, () -> {
//            orgRepeatRule = null;
//            repeatRuleButton.setText("<set>"); //TODO!!!! temporary hack!
//        }), "**"));
//        mainCont.add(layout(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, () -> item.setRepeatRule(null), false, false, false));
//        mainCont.add(layoutN(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, () -> item.setRepeatRule(null)));
//</editor-fold>
//        mainCont.add(layoutN(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, () -> {
//            previousValues.put(Item.PARSE_REPEAT_RULE, REPEAT_RULE_DELETED_MARKER);
//            repeatRuleButton.setText("");
//        }));
//        mainCont.add(layoutN(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, null));
        mainCont.add(layoutN(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP));
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
//        if (false) {
//            mainTabCont.add(BorderLayout.SOUTH, new SubtaskContainerSimple(item, ScreenItem2.this, templateEditMode, parseIdMap2));
//        }
        int numberUndoneSubtasks = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
        int totalNumberSubtasks = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
//        int numberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks;

        //HEADER - EDIT LIST IN FULL SCREEN MODE
        Button editSubtasksFullScreen = new Button();
//        String subtaskStr = (totalNumberSubtasks == 0
//                ? "Add subtasks" : ("" + totalNumberSubtasks + " subtasks" + (numberUndoneSubtasks == 0 ? "" : (", " + numberUndoneSubtasks + " remaining"))));
        String subtaskStr = totalNumberSubtasks == 0 ? "" : "" + numberUndoneSubtasks + "/" + totalNumberSubtasks;
        editSubtasksFullScreen.setCommand(MyReplayCommand.create("EditSubtasks", subtaskStr, null, (e) -> {
//            ItemList subtaskList = item.getItemList();
//            List<Item> subtaskList = item.getListFull();
//            new ScreenListOfItems("Subtasks of " + item.getText(), () -> new ItemList(item.getListFull(),true), previousForm, (iList) -> {
            new ScreenListOfItems("Subtasks of " + item.getText(), () -> item, previousForm, (item) -> {
//                item.setItemList(subtaskList);
//                item.setList(subtaskList);
//                item.setList(iList.getListFull());
//                if (false) 
//                    item.setList((iList); //probably not necessary since all operations on the list (insert, D&D, ...) should update the list on each change
//                DAO.getInstance().saveInBackground(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                int numberUndoneSubtasks2 = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
                int totalNumberSubtasks2 = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project

                editSubtasksFullScreen.setText(totalNumberSubtasks2 == 0 ? "" : "" + numberUndoneSubtasks2 + "/" + totalNumberSubtasks2);
                parseIdMap2.put(SUBTASK_KEY, () -> DAO.getInstance().saveTemplateCopyWithSubtasksInBackground((Item) item));
                previousForm.refreshAfterEdit(); //necessary to update sum of subtask effort
            }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
            ).show();
        }
        ));
        mainCont.add(layoutN(Item.SUBTASKS, editSubtasksFullScreen, Item.SUBTASKS_HELP));

        if (false) mainCont.add(new SubtaskContainerSimple(item, ScreenItem2.this, templateEditMode, parseIdMap2)); //edit subtasks
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        parseIdMap2.put("subtasksItemList", () -> item.setList(subtasksItemList.getList())); //NOT needed, since all edits to subtasks are save directly
//        mainTabCont.add(BorderLayout.SOUTH, ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//        parseIdMap2.put("subtasksItemList", () -> item.setList(subtasksItemList.getList()));
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container subtasksCont = new Container(BoxLayout.y());
//        List subtasks = item.getList();
//        if (subtasks.size() == 0) {
//    private MyTree2 createSubTaskTreeXXX(MyTreeModel treeModel) {
//        MyTree2 subTree = new MyTree2(treeModel) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> {
//                    subTaskTree.removeFromCache();
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
        if (Config.TEST) timeCont.setName("TimeTab");
        timeCont.setScrollableY(true);

        tabs.addTab("Time", Icons.iconTimeTab, TAB_ICON_SIZE_IN_MM, timeCont);

        boolean isProject = itemLS.isProject();

        //REMAINING************
        if (isProject) {
//            mainCont.addComponent(remainingIndex, layoutN(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getRemainingEffort()), "LabelFixed"),
            timeCont.addComponent(layoutN(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatDurationStd(itemLS.getRemainingForSubtasks()), "LabelFixed"),
                    Item.EFFORT_REMAINING_SUBTASKS_HELP, true, true, false)); //hack to insert after alarmDate field
        }
//TODO: makes no sense to show remaining for project itself, just confusing??
        String remainingTxt = isProject ? Item.EFFORT_REMAINING_PROJECT : Item.EFFORT_REMAINING;
        String remainingHelpTxt = isProject ? Item.EFFORT_REMAINING_PROJECT_HELP : Item.EFFORT_REMAINING_HELP;
        timeCont.add(layoutN(remainingTxt, remainingEffort, remainingHelpTxt));

        //ACTUAL************
        //if project, show actual for subtasks
        if (isProject) { //true: makes sense if work was done on project *before* subtasks were added! false: makes no sense to show actual for project itself, just confusing
            timeCont.add(layoutN(Item.EFFORT_ACTUAL_SUBTASKS, new Label(MyDate.formatDurationStd(itemLS.getActualForSubtasks()), "LabelFixed"),
                    Item.EFFORT_ACTUAL_SUBTASKS_HELP, true, true, false));
        }

        //if single task, show picker with text for single task, if project show picker w text for ProjectTaskItself
        String actualTxt = isProject ? Item.EFFORT_ACTUAL_PROJECT_TASK_ITSELF : Item.EFFORT_ACTUAL;
        String actualHelpTxt = isProject ? Item.EFFORT_ACTUAL_PROJECT_TASK_ITSELF_HELP : Item.EFFORT_ACTUAL_HELP;
        MyDurationPicker actualEffort;
//<editor-fold defaultstate="collapsed" desc="comment">
//        actualEffort = new MyDurationPicker(parseIdMap2, () -> (int) itemLS.getActualEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS,
//                (i) -> item.setActualEffort(((long) i) * MyDate.MINUTE_IN_MILLISECONDS));
//        actualEffort = new MyDurationPicker(parseIdMap2, () -> (int) itemLS.getActualEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS,
//                (i) -> item.setActualEffort(((long) i) * MyDate.MINUTE_IN_MILLISECONDS));
//        initField(Item.PARSE_ACTUAL_EFFORT, actualEffort,
//                () -> item.getActualEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS,
//                (min) -> item.setActualEffort(((long) min) * MyDate.MINUTE_IN_MILLISECONDS),
//                () -> actualEffort.getDuration(), (ms) -> actualEffort.setDuration((long) ms));
//</editor-fold>
//        actualEffort = new MyDurationPicker( itemLS.getActualEffortProjectTaskItself() );
        actualEffort = new MyDurationPicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        initField(Item.PARSE_ACTUAL_EFFORT, actualEffort,
//                () -> item.getActualEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS,
//                (min) -> item.setActualEffort(((long) min) * MyDate.MINUTE_IN_MILLISECONDS),
//                () -> actualEffort.getDuration(), (ms) -> actualEffort.setDuration((long) ms));
//</editor-fold>
        actualEffort.addActionListener((evt) -> {
//            status.setStatus(Item.updateStatusOnActualChange(item.getActual(), actualEffort.getDuration(), item.getStatus(), status.getStatus(), item.areAnySubtasksOngoingOrDone()));
            if (actualEffort.getDuration() > 0) { //if user has changed actual
                status.setStatus(ItemStatus.ONGOING, false);
                if (startedOnDate.getDate().getTime() == 0)
                    startedOnDate.setDate(new Date());
            } else { // actual effort set to 0
                if (item.isProject() && item.areAnySubtasksOngoingOrDone()) { //if some subtasks are ongoing or done
                    status.setStatus(ItemStatus.ONGOING, false);
                    if (startedOnDate.getDate().getTime() == 0)
                        startedOnDate.setDate(new Date());
                } else {
                    status.setStatus(ItemStatus.CREATED, false); //if setting actual to 0, set status back to Created
                    startedOnDate.setDate(new Date(0));
                }
            }
            status.repaint();
        });

        initField(Item.PARSE_ACTUAL_EFFORT, actualEffort,
                () -> item.getActualForProjectTaskItself(),
                (l3) -> item.setActual((long) l3, false),
                () -> actualEffort.getDuration(), (ms) -> actualEffort.setDuration((long) ms));

        timeCont.add(layoutN(actualTxt, actualEffort, actualHelpTxt));

        //ESTIMATE************
        if (isProject) { //true: makes sense if work was done on project *before* subtasks were added! false: makes no sense to show actual for project itself, just confusing
//            timeCont.add(layoutN(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getEffortEstimateForSubtasks() / MyDate.MINUTE_IN_MILLISECONDS), "LabelFixed"),
            timeCont.add(layoutN(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatDurationStd(itemLS.getEstimateForSubtasks()), "LabelFixed"),
                    Item.EFFORT_ESTIMATE_SUBTASKS_HELP, true, true, false));
        }
        String estimateTxt = isProject ? Item.EFFORT_ESTIMATE_PROJECT : Item.EFFORT_ESTIMATE;
        String estimateHelpTxt = isProject ? Item.EFFORT_ESTIMATE_PROJECT_HELP : Item.EFFORT_ESTIMATE_HELP;
        timeCont.add(layoutN(estimateTxt, effortEstimate, estimateHelpTxt));

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            actualEffort.addActionListener((e) -> {
////            if (actualEffort.getTime() > 0) {
////                if (item.getStatus() != ItemStatus.ONGOING && status.getStatus() == item.getStatus()/*status not manually changed*/) {
////                    status.setStatus(ItemStatus.ONGOING);
////                }
////            } else if (item.getStatus() == ItemStatus.ONGOING && status.getStatus() == item.getStatus()) {
////                status.setStatus(ItemStatus.CREATED);
////            }
////            ItemStatus oldStatus = status.getStatus();
////                status.setStatus(Item.updateStatusOnActualChange(item.getActualEffort(), actualEffort.getTime(), item.getStatus(), status.getStatus(), item.areAnySubtasksOngoing()));
//                status.setStatus(Item.updateStatusOnActualChange(item.getActualEffort(), actualEffort.getDuration(), item.getStatus(), status.getStatus(), item.areAnySubtasksOngoing()));
////                status.animate();
//                status.repaint();
//            });
//        }
//</editor-fold>
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
//        status.setIsItemOngoing(() -> {
////            return (item.getActualEffort() > 0 && actualEffort.getTime()>0); //not ongoing if either item.actuaEeffort==0 OR actualEffort has been set to 0 by user// || actualEffort.getTime() > 0; //actualEffort.getTime() SHOULD not be needed since first setting actualEff manually and then clicking on a Done/Cancelled checkbox should be an edge case(??)
////            return (actualEffort.getTime() > 0); //whatever value is currently set in UI is used (no need to check item.getActualEffort())
//            return (actualEffort.getDuration() > 0); //whatever value is currently set in UI is used (no need to check item.getActualEffort())
//        });
//        MyDatePicker waitingTill = new MyDatePicker(parseIdMap2, () -> itemLS.getWaitingTillDateD(), (d) -> item.setWaitingTillDate(d)); //"<wait until this date>",
        MyDatePicker waitingTill = new MyDatePicker(); //"<wait until this date>",
//        timeCont.add(new Label(Item.WAIT_DATE)).add(addDatePickerWithClearButton(waitingTill));
//        timeCont.add(new Label(Item.WAIT_DATE)).add(waitingTill.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAIT_UNTIL_DATE, waitingTill.makeContainerWithClearButton(), "**"));
        initField(Item.PARSE_WAITING_TILL_DATE, waitingTill,
                () -> item.getWaitingTillDateD(),
                (t) -> item.setWaitingTillDate((Date) t),
                () -> waitingTill.getDate(),
                (d) -> waitingTill.setDate((Date) d),
                () -> item.isWaitingTillInherited(waitingTill.getDate()));
        timeCont.add(layoutN(Item.WAIT_UNTIL_DATE, waitingTill, Item.WAIT_UNTIL_DATE_HELP));
        updateUIIDForInherited(item.isWaitingTillInherited(waitingTill.getDate()), waitingTill);

//        MyDateAndTimePicker waitingAlarm = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getWaitingAlarmDateD(), (d) -> item.setWaitingAlarmDate(d)); //"<waiting reminder this date>", 
        MyDateAndTimePicker waitingAlarm = new MyDateAndTimePicker(); //"<waiting reminder this date>", 
        initField(Item.PARSE_WAITING_ALARM_DATE, waitingAlarm, () -> item.getWaitingAlarmDateD(), (t) -> item.setWaitingAlarmDate((Date) t),
                () -> waitingAlarm.getDate(), (d) -> waitingAlarm.setDate((Date) d));

//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(addDatePickerWithClearButton(waitingAlarm));
//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(waitingAlarm.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAITING_ALARM_DATE, waitingAlarm.makeContainerWithClearButton(), "**"));
        timeCont.add(layoutN(Item.WAITING_ALARM_DATE, waitingAlarm, Item.WAITING_ALARM_DATE_HELP));

//        MyDatePicker hideUntil = new MyDatePicker(parseIdMap2, () -> itemLS.getHideUntilDateD(), (d) -> item.setHideUntilDate(d)); //"<hide task until>", 
        MyDatePicker hideUntil = new MyDatePicker(); //"<hide task until>", 
        initField(Item.PARSE_HIDE_UNTIL_DATE, hideUntil, () -> item.getHideUntilDateD(), (t) -> item.setHideUntilDate((Date) t),
                () -> hideUntil.getDate(), (d) -> hideUntil.setDate((Date) d));

//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(addDatePickerWithClearButton(hideUntil));
//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(hideUntil.makeContainerWithClearButton());
//        timeCont.add(layout(Item.HIDE_UNTIL, hideUntil.makeContainerWithClearButton(), "**"));
        timeCont.add(layoutN(Item.HIDE_UNTIL, hideUntil, Item.HIDE_UNTIL_HELP));

//        MyDateAndTimePicker startByDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getStartByDateD(), (d) -> item.setStartByDate(d)); // "<start task on this date>", 
        MyDateAndTimePicker startByDate = new MyDateAndTimePicker(); // "<start task on this date>", 
        initField(Item.PARSE_START_BY_DATE, startByDate,
                () -> item.getStartByDateD(),
                (t) -> item.setStartByDate((Date) t),
                () -> startByDate.getDate(),
                (d) -> startByDate.setDate((Date) d),
                () -> item.isStartByDateInherited(startByDate.getDate()));
        timeCont.add(layoutN(Item.START_BY_TIME, startByDate, Item.START_BY_TIME_HELP));
        updateUIIDForInherited(item.isStartByDateInherited(startByDate.getDate()), startByDate);

//        timeCont.add(new Label(Item.START_BY_TIME)).add(addDatePickerWithClearButton(startByDate));
//        timeCont.add(new Label(Item.START_BY_TIME)).add(startByDate.makeContainerWithClearButton());
//        timeCont.add(layout(Item.START_BY_TIME, startByDate.makeContainerWithClearButton(), "**"));
        if (true) {
//            MyDatePicker expireByDate = new MyDatePicker(parseIdMap2, () -> itemLS.getExpiresOnDateD(), (d) -> item.setExpiresOnDateD(d)); // "<auto-cancel on date>", 
            MyDatePicker expireByDate = new MyDatePicker(); // "<auto-cancel on date>", 
            initField(Item.PARSE_EXPIRES_ON_DATE, expireByDate, () -> item.getExpiresOnDateD(), (t) -> item.setExpiresOnDate((Date) t),
                    () -> expireByDate.getDate(), (d) -> expireByDate.setDate((Date) d));

//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(addDatePickerWithClearButton(expireByDate));
//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(expireByDate.makeContainerWithClearButton());
//            timeCont.add(layout(Item.AUTOCANCEL_BY, expireByDate.makeContainerWithClearButton(), "**"));
            timeCont.add(layoutN(Item.AUTOCANCEL_BY, expireByDate, Item.AUTOCANCEL_BY_HELP));
        }

        //TAB PRIO
        Container prioCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        prioCont.setScrollableY(true);
        if (Config.TEST) prioCont.setName("PrioTab");

        tabs.addTab("Prio", Icons.iconPrioTab, TAB_ICON_SIZE_IN_MM, prioCont);

//        MyStringPicker priority = new MyStringPicker(new String[]{"None", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2, () -> item.getPriority(), (i) -> item.setPriority(i));
//        cont.add(new Label("Priority")).add(priority);
//        prioCont.add(Item.PRIORITY).add(priority);
//                () -> itemLS.getPriority() == 0 ? "" : itemLS.getPriority() + "",
//        MyComponentGroup priority = new MyComponentGroup(new String[]{"-", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2,
//                (s) -> item.setPriority(Integer.parseInt(s.length() == 0 ? "0" : s)));
        MyComponentGroup priority = new MyComponentGroup(new String[]{"-", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, true);
        initField(Item.PARSE_PRIORITY, priority,
                () -> item.getPriority(),
                (t) -> item.setPriority((int) t),
                () -> priority.getSelectedIndex(),
                (i) -> priority.select((int) i),
                () -> item.isPriorityInherited(priority.getSelectedIndex()));
        prioCont.add(layoutN(Item.PRIORITY, priority, Item.PRIORITY_HELP));//, null, true, false, false, true));
        updateUIIDForInherited(item.isPriorityInherited(priority.getSelectedIndex()), priority);
//        prioCont.add(layout(Item.PRIORITY, priority, Item.PRIORITY_HELP, true, false, true));

//        MyComponentGroup importance = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getImportanceN() == null ? "" : itemLS.getImportanceN().getDescription(),
//                (s) -> item.setImportance(Item.HighMediumLow.getValue(s)));
        MyComponentGroup importance = new MyComponentGroup(HighMediumLow.getDescriptionList(), true);
//        initField(Item.PARSE_IMPORTANCE, importance, () -> item.getImportanceN(), (t) -> item.setImportance((Item.HighMediumLow.getValue((String) t))),
        initField(Item.PARSE_IMPORTANCE, importance,
                () -> item.getImportanceN() != null ? item.getImportanceN().toString() : null,
                //                (t) -> item.setImportance((Item.HighMediumLow.getValue((String) t))),
                (enumStr) -> item.setImportance(enumStr != null ? (HighMediumLow.valueOf((String) enumStr)) : null),
                //                () -> importance.getSelectedString(),
                () -> importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()).toString() : null,
                //                (i) -> importance.select(i != null ? (String) i.toString() : null));
                //                (i) -> importance.select(i != null ? HighMediumLow.getValue( (String)i).getDescription(): null));
                //                (enumStr) -> importance.select(enumStr != null ? HighMediumLow.valueOf((String)enumStr).getDescription(): null));
                (enumStr) -> importance.select(enumStr != null ? HighMediumLow.valueOf((String) enumStr).getDescription() : null),
                () -> item.isImportanceInherited(importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()) : null)
        );
//                (i) -> importance.select(i != null ?  i.toString() : null));

//        prioCont.add(Item.IMPORTANCE).add(FlowLayout.encloseCenterMiddle(importance));
//        prioCont.add(layout(Item.IMPORTANCE, FlowLayout.encloseCenterMiddle(importance), "**"));
//        prioCont.add(layout(Item.IMPORTANCE, importance, Item.IMPORTANCE_HELP, true, false, true));
        prioCont.add(layoutN(Item.IMPORTANCE, importance, Item.IMPORTANCE_HELP));//, null, false, false, true, true));
        updateUIIDForInherited(item.isImportanceInherited(importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()) : null), importance);

//        MyComponentGroup urgency = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getUrgencyN() == null ? "" : itemLS.getUrgencyN().getDescription(),
//                (s) -> item.setUrgency(Item.HighMediumLow.getValue(s)));
        MyComponentGroup urgency = new MyComponentGroup(HighMediumLow.getDescriptionList(), true);
//        cont.add(new Label("Urgency")).add(urgency);
//        prioCont.add(Item.URGENCY).add(FlowLayout.encloseMiddle(urgency));
//        prioCont.add(layout(Item.URGENCY, FlowLayout.encloseMiddle(urgency), "**"));
//        prioCont.add(layout(Item.URGENCY, urgency, Item.URGENCY_HELP, true, false, true));
        initField(Item.PARSE_URGENCY, urgency,
                () -> item.getUrgencyN() != null ? item.getUrgencyN().toString() : null,
                (enumStr) -> item.setUrgency(enumStr != null ? (HighMediumLow.valueOf((String) enumStr)) : null),
                //                () -> urgency.getSelectedString(), (i) -> urgency.select(i != null ? (String) i.toString() : null));
                () -> urgency.getSelectedString() != null ? HighMediumLow.getValue(urgency.getSelectedString()).toString() : null,
                (enumStr) -> urgency.select(enumStr != null ? HighMediumLow.valueOf((String) enumStr).getDescription() : null),
                () -> item.isUrgencyInherited(urgency.getSelectedString() != null ? HighMediumLow.getValue(urgency.getSelectedString()) : null)
        );
        prioCont.add(layoutN(Item.URGENCY, urgency, Item.URGENCY_HELP));//, null, false, false, true, true));
        updateUIIDForInherited(item.isUrgencyInherited(urgency.getSelectedString() != null ? HighMediumLow.getValue(urgency.getSelectedString()) : null), urgency);

//        MyComponentGroup challenge = new MyComponentGroup(Item.Challenge.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getChallengeN() == null ? "" : itemLS.getChallengeN().getDescription(),
//                (s) -> item.setChallenge(Item.Challenge.getValue(s)));
        MyComponentGroup challenge1 = new MyComponentGroup(Challenge.getDescriptionList(), true);
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(new Label("Difficulty")).add(challenge);
//        prioCont.add(new Label("Difficulty")).add(BorderLayout.center(Container.encloseIn(new FlowLayout(),challenge)));
//        if (challenge.getPreferredW() > Display.getInstance().getDisplayWidth()) { //if too wide, replace with the short version
//            parseIdMap2.remove(challenge); //remove previous field!!
//            challenge = new MyComponentGroup(Item.Challenge.getDescriptionList(true), parseIdMap2,
//                    () -> itemLS.getChallengeN() == null ? "" : itemLS.getChallengeN().getDescription(true),
//                    (s) -> item.setChallenge(Item.Challenge.getValue(s, true)));
//        }
//</editor-fold>
        MyComponentGroup challenge = challenge1.getPreferredW() < Display.getInstance().getDisplayWidth() ? challenge1
                : new MyComponentGroup(Challenge.getDescriptionList(true), true);
        initField(Item.PARSE_CHALLENGE, challenge,
                () -> item.getChallengeN() != null ? item.getChallengeN().toString() : null,
                (enumStr) -> item.setChallenge(enumStr != null ? (Challenge.valueOf((String) enumStr)) : null),
                //                () -> challenge.getSelectedString(), (s) -> challenge.select(s != null ? (String) s.toString() : null));
                () -> challenge.getSelectedString() != null ? Challenge.getValue(challenge.getSelectedString()).toString() : null,
                (enumStr) -> challenge.select(enumStr != null ? Challenge.valueOf((String) enumStr).getDescription() : null),
                () -> item.isChallengeInherited((challenge.getSelectedString() != null ? Challenge.getValue(challenge.getSelectedString()) : null))
        );
//<editor-fold defaultstate="collapsed" desc="comment">
//        prioCont.add(new Label(Item.CHALLENGE)).add(FlowLayout.encloseCenterMiddle(challenge));
//        prioCont.add(new Label("TEST")).add(FlowLayout.encloseCenterMiddle(new Label("11111"),new Label("22222"),new Label("33333"),new Label("44444"),new Label("55555")));
//        prioCont.add(layout(Item.CHALLENGE, FlowLayout.encloseCenterMiddle(challenge), "**", true));
//        prioCont.add(layout(Item.CHALLENGE, challenge, Item.CHALLENGE_HELP, true, false, true));
//</editor-fold>
        prioCont.add(layoutN(Item.CHALLENGE, challenge, Item.CHALLENGE_HELP));//, null, false, false, true, true));
        updateUIIDForInherited(item.isChallengeInherited(challenge.getSelectedString() != null ? Challenge.getValue(challenge.getSelectedString()) : null), challenge);

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyComponentGroup dreadFun = new MyComponentGroup(Item.DreadFunValue.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getDreadFunValueN() == null ? "" : itemLS.getDreadFunValueN().getDescription(),
//                (s) -> item.setDreadFunValue(Item.DreadFunValue.getValue(s)));
//        prioCont.add(new Label(Item.FUN_DREAD)).add(dreadFun);
//        prioCont.add(layout(Item.FUN_DREAD, FlowLayout.encloseCenterMiddle(dreadFun), Item.FUN_DREAD_HELP));
//        prioCont.add(layout(Item.FUN_DREAD, dreadFun, Item.FUN_DREAD_HELP, true, false, true));
//</editor-fold>
        MyComponentGroup dreadFun = new MyComponentGroup(DreadFunValue.getDescriptionList(), true);
        initField(Item.PARSE_DREAD_FUN_VALUE, dreadFun,
                () -> item.getDreadFunValueN() != null ? item.getDreadFunValueN().toString() : null,
                (enumStr) -> item.setDreadFunValue(enumStr != null ? (DreadFunValue.valueOf((String) enumStr)) : null),
                //                () -> dreadFun.getSelectedString(), (s) -> dreadFun.select(s != null ? (String) s.toString() : null));
                () -> dreadFun.getSelectedString() != null ? DreadFunValue.getValue(dreadFun.getSelectedString()).toString() : null,
                (enumStr) -> dreadFun.select(enumStr != null ? DreadFunValue.valueOf((String) enumStr).getDescription() : null),
                () -> item.isDreadFunInherited((dreadFun.getSelectedString() != null ? DreadFunValue.getValue(dreadFun.getSelectedString()) : null))
        );
        prioCont.add(layoutN(Item.FUN_DREAD, dreadFun, Item.FUN_DREAD_HELP));//, null, false, false, true, true));
        updateUIIDForInherited(item.isDreadFunInherited(dreadFun.getSelectedString() != null ? DreadFunValue.getValue(dreadFun.getSelectedString()) : null), dreadFun);

//        MyNumericTextField earnedValue = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValue(), (d) -> item.setEarnedValue(d));
        MyNumericTextField earnedValue = new MyNumericTextField("");
//        earnedValue.setColumns(2); //result: only shows/truncates to 2 columns when field is not edited

//        prioCont.add(new Label(Item.EARNED_VALUE)).add(earnedValue);
//        prioCont.add(layout(Item.EARNED_VALUE, earnedValue, Item.EARNED_VALUE_HELP, true, false, false));
        initField(Item.PARSE_EARNED_VALUE, earnedValue, () -> L10NManager.getInstance().format(item.getEarnedValue(), 2), (s) -> item.setEarnedValue(L10NManager.getInstance().parseDouble((String) s)),
                () -> earnedValue.getText(), (s) -> earnedValue.setText((String) s)); //TODO!!! localize number of decimal points (2)??
        prioCont.add(layoutN(Item.EARNED_VALUE, earnedValue, Item.EARNED_VALUE_HELP, true, false, true));

//        MyNumericTextField earnedValuePerHour = new MyNumericTextField("<set>", parseIdMap2, () -> item.getEarnedValuePerHour(), (d) -> {
//        MyNumericTextField earnedValuePerHour = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValuePerHour(), (d) -> {
//        });
////        earnedValuePerHour.setConstraint(TextArea.UNEDITABLE);
//        earnedValuePerHour.setEditable(false);
        Label earnedValuePerHour = new Label(L10NManager.getInstance().format(itemLS.getEarnedValuePerHour(), 2));
        earnedValuePerHour.setUIID("LabelFixed");
//        prioCont.add(new Label(Item.EARNED_POINTS_PER_HOUR)).add(earnedValuePerHour).add(new SpanLabel("Value per hour is calculated as Value divided by the Estimate, or the sum of Remaining and Actual effort - once work has started."));
//                add(new SpanLabel(Item.EARNED_POINTS_PER_HOUR + " is calculated as " + Item.EARNED_VALUE + " divided by " + Item.EFFORT_ESTIMATE + ", and once work has started by the sum of " + Item.EFFORT_REMAINING + " and " + Item.EFFORT_ACTUAL + "."));
//        prioCont.add(layout(Item.EARNED_POINTS_PER_HOUR, earnedValuePerHour, Item.EARNED_POINTS_PER_HOUR_HELP, true, true, true));
        prioCont.add(layoutN(Item.EARNED_POINTS_PER_HOUR, earnedValuePerHour, Item.EARNED_POINTS_PER_HOUR_HELP, true, true, false));

        //Update earnedValuePerHour by listening to actions from any of the four fields that affect it
        MyActionListener earnedValuePerHourUpdater = new MyActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //redo the calculation done by Item but manually (without updating Item since the values may be Cancelled)
                if (earnedValue.getText().length() > 0) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    earnedValuePerHour.setText(""
//                    earnedValuePerHour.setVal(
//                            Item.calculateEarnedValuePerHour(
//                                    Item.getTotalExpectedEffort(
//                                            ((long) remainingEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS,
//                                            ((long) actualEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS,
//                                            ((long) effortEstimate.getTime()) * MyDate.MINUTE_IN_MILLISECONDS),
//                                    Double.valueOf(earnedValue.getText().equals("") ? "0" : earnedValue.getText())));
//</editor-fold>
                    earnedValuePerHour.setText(
                            L10NManager.getInstance().format(Item.calculateEarnedValuePerHour(
                                    Item.getTotalExpectedEffort(
                                            //                                            ((long) remainingEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS,
                                            //                                            ((long) actualEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS,
                                            //                                            ((long) effortEstimate.getTime()) * MyDate.MINUTE_IN_MILLISECONDS),
                                            ((long) remainingEffort.getDuration()),
                                            ((long) actualEffort.getDuration()),
                                            ((long) effortEstimate.getDuration())),
                                    Double.valueOf(earnedValue.getText().equals("") ? "0" : earnedValue.getText())), 2));
//                    earnedValuePerHour.animate(); //TODO: needed?
                    earnedValuePerHour.repaint(); //TODO: needed?
                }
            }
        };
        earnedValue.addActionListener(earnedValuePerHourUpdater);
        remainingEffort.addActionListener(earnedValuePerHourUpdater);
        actualEffort.addActionListener(earnedValuePerHourUpdater);
        earnedValue.addActionListener(earnedValuePerHourUpdater);
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener remainingEffortChangeListener = new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
////                remainingEffortSetManually = item.getRemainingEffortNoDefault() != remainingEffort.getTime(); //true;
////                if (remainingEffortSetAutomatically) {
////                    return; //do nothing if remainingEffort is set automatically
////                } else {
//                //update effort estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
//                if (!remainingEffortSetAutomatically) {
//                    remainingEffortSetManually = true;
//                    if (!effortEstimateSetManually && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()
//                            //                            && !effortEstimateSetManually&& effortEstimate.getTime()==0&& effortEstimate.getTime()==itemLS.getEffortEstimate()) ) {//&& item.getEffortEstimate() == 0 xxonly set if picker zero and org value zero (not changed picker back to zero
//                            && itemLS.getEffortEstimate() == 0) {//&& item.getEffortEstimate() == 0 xxonly set if picker zero and org value zero (not changed picker back to zero
////                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
////                    if ((remainingEffort.getTime() != 0 && item.getEffortEstimate() == 0 && (effortEstimate.getTime() == 0 || forceSameValues))
////                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI:
//                        effortEstimateSetAutomatically = true;
////                        effortEstimate.setTime(remainingEffort.getTime() + actualEffort.getTime()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated)
//                        effortEstimate.setDuration(remainingEffort.getDuration() + actualEffort.getDuration()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated)
//                        effortEstimateSetAutomatically = false;
//                        effortEstimate.repaint();
//                    }
//                } //else do nothing if remainingEffort is set automatically
//            }
////            }
//        };
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener remainingEffortChangeListenerOLD = new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
//                //update effort estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
//                if (MyPrefs.getBoolean(MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem)) {
//                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
//                    if ((remainingEffort.getTime() != 0 && item.getEffortEstimate() == 0 && (effortEstimate.getTime() == 0 || forceSameValues))
//                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI:
//                        effortEstimate.setTime(remainingEffort.getTime());
//                        effortEstimate.repaint();
//                    }
//                }
//            }
//        };
//</editor-fold>
//        remainingEffort.addActionListener(remainingEffortChangeListener);
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        remainingEffort.addActionListener((e) -> {
//            //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set.
//            //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
////                remainingEffortSetManually = item.getRemainingEffortNoDefault() != remainingEffort.getTime(); //true;
////                if (remainingEffortSetAutomatically) {
////                    return; //do nothing if remainingEffort is set automatically
////                } else {
//            //update effort estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
//            if (!remainingEffortSetAutomatically) {
//                remainingEffortSetManually = true;
//                if (!effortEstimateSetManually && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()
//                        //                            && !effortEstimateSetManually&& effortEstimate.getTime()==0&& effortEstimate.getTime()==itemLS.getEffortEstimate()) ) {//&& item.getEffortEstimate() == 0 xxonly set if picker zero and org value zero (not changed picker back to zero
//                        && itemLS.getEffortEstimate() == 0) {//&& item.getEffortEstimate() == 0 xxonly set if picker zero and org value zero (not changed picker back to zero
////                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
////                    if ((remainingEffort.getTime() != 0 && item.getEffortEstimate() == 0 && (effortEstimate.getTime() == 0 || forceSameValues))
////                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI:
//                    effortEstimateSetAutomatically = true;
////                        effortEstimate.setTime(remainingEffort.getTime() + actualEffort.getTime()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated)
//                    effortEstimate.setDurationAndNotify(remainingEffort.getDuration() + actualEffort.getDuration()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated)
////                    effortEstimate.fireClicked(); //simulate click to trigger local saving of the value
//                    effortEstimateSetAutomatically = false;
//                    effortEstimate.repaint();
//                }
//            } //else do nothing if remainingEffort is set automatically
////            }
////            }
//        });
//</editor-fold>
        remainingEffort.addActionListener((e) -> {
            //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set. 
            //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
            //update effort estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
            if (!remainingEffortSetManually && !remainingEffortSetAutomatically)
                remainingEffortSetManually = true; //set on first manual set and keep it (=> no more automatic setting in this round)

            if (remainingEffortSetManually && !effortEstimateSetManually
                    && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()
                    && itemLS.getEstimate() == 0) { //UI: only allow manual setting if no value was set before
                effortEstimateSetAutomatically = true;
                effortEstimate.setDurationAndNotify(remainingEffort.getDuration() + actualEffort.getDuration()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated) 
                effortEstimateSetAutomatically = false;
                effortEstimate.repaint();
            }
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener effortEstimateChangeListener = new MyActionListener() {
//        ActionListener effortEstimateChangeListener = new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//        ActionListener effortEstimateChangeListener = (e)-> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                effortEstimateSetManually = item.getEffortEstimate() != effortEstimate.getTime(); //UI: only set to true if actually changed (avoid that entering and leaving without changing blocks auto-updates
//                if (effortEstimateSetAutomatically) {
//                    return;
//                } else {
////</editor-fold>
//                if (!effortEstimateSetAutomatically) {
//                    effortEstimateSetManually = true;
//                    //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
//                    //only automatically update effort estimate if never defined and not changed in the current editing
//                    if (MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean() //&& item.getRemainingEffortNoDefault() == 0
//                            && !remainingEffortSetManually && itemLS.getRemainingEffortProjectTaskItself() == 0) { //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
////<editor-fold defaultstate="collapsed" desc="comment">
////                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
////                    if ((effortEstimate.getTime() != 0 && item.getRemainingEffortNoDefault() == 0 && (remainingEffort.getTime() == 0 || forceSameValues))
////                            || remainingEffort.getTime()==effortEstimate.getTime()) { //UI: when to auto-update estimates
////                    if ((item.getRemainingEffortNoDefault() == 0 && effortEstimate.getTime() != 0 && (remainingEffort.getTime() == 0 || forceSameValues))
////                            || item.getRemainingEffortNoDefault() == 0
////                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI: when to auto-update estimates
////</editor-fold>
//                        remainingEffortSetAutomatically = true;
////                        remainingEffort.setTime(effortEstimate.getTime() - actualEffort.getTime()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
//                        remainingEffort.setDuration(effortEstimate.getDuration() - actualEffort.getDuration()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
//                        remainingEffortSetAutomatically = false;
////                        remainingEffortSetManually = false; //must reset since the actionlistener on remainingEffort does not distinguish between setting via manual user input and auto-setting based on changed effortEstimate
//                        remainingEffort.repaint();
//                    }
//                }
////            }
//        };
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener effortEstimateChangeListenerOLD = new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
//                if (MyPrefs.getBoolean(MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem)) //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
//                {
//                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
////                    if ((effortEstimate.getTime() != 0 && item.getRemainingEffortNoDefault() == 0 && (remainingEffort.getTime() == 0 || forceSameValues))
////                            || remainingEffort.getTime()==effortEstimate.getTime()) { //UI: when to auto-update estimates
//                    if ((item.getRemainingEffortNoDefault() == 0 && effortEstimate.getTime() != 0 && (remainingEffort.getTime() == 0 || forceSameValues))
//                            || item.getRemainingEffortNoDefault() == 0
//                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI: when to auto-update estimates
//                        remainingEffort.setTime(effortEstimate.getTime() - actualEffort.getTime());
//                        remainingEffort.repaint();
//                    }
//                }
//            }
//        };
//</editor-fold>
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        effortEstimate.addActionListener((e) -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////                effortEstimateSetManually = item.getEffortEstimate() != effortEstimate.getTime(); //UI: only set to true if actually changed (avoid that entering and leaving without changing blocks auto-updates
////                if (effortEstimateSetAutomatically) {
////                    return;
////                } else {
////</editor-fold>
//            if (!effortEstimateSetAutomatically) {
//                effortEstimateSetManually = true;
//                //DONE!! create a Setting to make estimate and remaining follow each other every time they're edited (while no value has been set for the item) - currently the automatic setting of the other only works the first time
//                //only automatically update effort estimate if never defined and not changed in the current editing
//                if (MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean() //&& item.getRemainingEffortNoDefault() == 0
//                        && !remainingEffortSetManually && itemLS.getRemainingEffortProjectTaskItself() == 0) { //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
////<editor-fold defaultstate="collapsed" desc="comment">
////                    boolean forceSameValues = (MyPrefs.getBoolean(MyPrefs.alwaysForceSameInitialValuesForRemainingOrEstimateWhenTheOtherIsChangedAndNoValueSetForItemXXX));
////                    if ((effortEstimate.getTime() != 0 && item.getRemainingEffortNoDefault() == 0 && (remainingEffort.getTime() == 0 || forceSameValues))
////                            || remainingEffort.getTime()==effortEstimate.getTime()) { //UI: when to auto-update estimates
////                    if ((item.getRemainingEffortNoDefault() == 0 && effortEstimate.getTime() != 0 && (remainingEffort.getTime() == 0 || forceSameValues))
////                            || item.getRemainingEffortNoDefault() == 0
////                            || remainingEffort.getTime() == effortEstimate.getTime()) { //UI: when to auto-update estimates
////</editor-fold>
//                    remainingEffortSetAutomatically = true;
////                        remainingEffort.setTime(effortEstimate.getTime() - actualEffort.getTime()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
//                    remainingEffort.setDurationAndNotify(effortEstimate.getDuration() - actualEffort.getDuration()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
//                    remainingEffortSetAutomatically = false;
////                        remainingEffortSetManually = false; //must reset since the actionlistener on remainingEffort does not distinguish between setting via manual user input and auto-setting based on changed effortEstimate
//                    remainingEffort.repaint();
//                }
//            }
////            }
//        });
//</editor-fold>
        effortEstimate.addActionListener((e) -> {
            //only automatically update effort estimate if never defined and not changed in the current editing 
            if (!effortEstimateSetManually && !effortEstimateSetAutomatically)
                effortEstimateSetManually = true; //set on first manual set and keep it (=> no more automatic setting in this round)

            if (effortEstimateSetManually && !remainingEffortSetManually
                    && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean() //&& item.getRemainingEffortNoDefault() == 0
                    && itemLS.getRemainingForProjectTaskItselfFromParse() == 0) { //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                remainingEffortSetAutomatically = true;
                remainingEffort.setDurationAndNotify(effortEstimate.getDuration() - actualEffort.getDuration()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
                remainingEffortSetAutomatically = false;
                remainingEffort.repaint();
            }
        });

        //TAB STATUS FIELDS
        Container statusCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        if (Config.TEST) statusCont.setName("StatusTab");
        statusCont.setScrollableY(true);
        tabs.addTab("Status", Icons.iconStatusTab, TAB_ICON_SIZE_IN_MM, statusCont);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()),"LabelFixed");
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()));
//</editor-fold>
        Label createdDate = new Label(item.getCreatedDate() == 0 ? "" : MyDate.formatDateTimeNew(item.getCreatedDate())); //NOT use itemLS since CreatedDate is not saved locally
//        statusCont.add(new Label(Item.CREATED_DATE)).add(createdDate);
//        statusCont.add(layout(Item.CREATED_DATE, createdDate, "**", true, true, true));
        statusCont.add(layoutN(Item.CREATED_DATE, createdDate, "**", true));

        if (item.isProject()) {
            long lastModifiedSubtasks = item.getLastModifiedDateProjectOrSubtasks().getTime();
            Label lastModifiedDateSubtasks = new Label(lastModifiedSubtasks == 0 ? "" : MyDate.formatDateTimeNew(lastModifiedSubtasks));
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//            statusCont.add(layout(Item.UPDATED_DATE_SUBTASKS, lastModifiedDateSubtasks, "**", true, true, true));
//            statusCont.add(layout(Item.UPDATED_DATE, lastModifiedDateSubtasks, "**", true, true, true));
//</editor-fold>
            statusCont.add(layoutN(Item.UPDATED_DATE, lastModifiedDateSubtasks, "**", true));
        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()), "LabelFixed");
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()));
//</editor-fold>
            Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "" : MyDate.formatDateTimeNew(item.getLastModifiedDate()));
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//            statusCont.add(layout(Item.UPDATED_DATE, lastModifiedDate, "**", true, true, true));
            statusCont.add(layoutN(Item.UPDATED_DATE, lastModifiedDate, "**", true));
        }

//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(addDatePickerWithClearButton(startedOnDate)).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate.makeContainerWithClearButton(), "Set automatically when using the timer")); //"click to set date when started"
//        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate, Item.STARTED_ON_DATE_HELP)); //"click to set date when started"
//</editor-fold>
        initField(Item.PARSE_STARTED_ON_DATE, startedOnDate, () -> item.getStartedOnDateD(), (s) -> item.setStartedOnDate((Date) s, true),
                () -> startedOnDate.getDate(), (s) -> startedOnDate.setDate((Date) s));

        statusCont.add(layoutN(Item.STARTED_ON_DATE, startedOnDate, Item.STARTED_ON_DATE_HELP)); //"click to set date when started"
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.)).add(new SpanLabel("Set automatically when using the timer"));

//        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getCompletedDateD(), (d) -> item.setCompletedDate(d));
//        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getCompletedDateD(), (d) -> item.setCompletedDate(d));
        MyDateAndTimePicker completedDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(completedDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(layout(Item.COMPLETED_DATE, completedDate.makeContainerWithClearButton(), "Set automatically when a task is completed")); //"click to set a completed date"
//        statusCont.add(layout(Item.COMPLETED_DATE, completedDate, Item.COMPLETED_DATE_HELP)); //"click to set a completed date"
//</editor-fold>
        initField(Item.PARSE_COMPLETED_DATE, completedDate, () -> item.getCompletedDateD(), (s) -> item.setCompletedDate((Date) s),
                () -> completedDate.getDate(), (s) -> completedDate.setDate((Date) s));

        statusCont.add(layoutN(Item.COMPLETED_DATE, completedDate, Item.COMPLETED_DATE_HELP)); //"click to set a completed date"

//        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> item.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
//        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.DATE_WHEN_SET_WAITING)).add(dateSetWaitingDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is set Waiting"));
//        statusCont.add(layout(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate.makeContainerWithClearButton(), "Set automatically when a task is set Waiting"));
//        statusCont.add(layout(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate, Item.DATE_WHEN_SET_WAITING_HELP));
//</editor-fold>
        initField(Item.PARSE_DATE_WHEN_SET_WAITING, dateSetWaitingDate, () -> item.getDateWhenSetWaitingD(), (s) -> item.setDateWhenSetWaiting((Date) s),
                () -> dateSetWaitingDate.getDate(), (s) -> dateSetWaitingDate.setDate((Date) s));

        statusCont.add(layoutN(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate, Item.DATE_WHEN_SET_WAITING_HELP));

        if (false) { //not meaningful to change status when changing this date
            dateSetWaitingDate.addActionListener((e) -> {
                noAutoUpdateOnStatusChange = true;
                if (dateSetWaitingDate.getDate().getTime() != 0) {
                    if (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.ONGOING) { //only update if created or ongoing (not cancelled, Done)
                        status.setStatus(ItemStatus.WAITING);
//                    status.repaint();
                    }
                } else { //clearing the waiting date => replace the Waiting status by whatever is appropriate
                    if (status.getStatus() == ItemStatus.WAITING) { //only update if created or ongoing (not cancelled, Done)
                        status.setStatus(actualEffort.getDuration() != 0 ? ItemStatus.ONGOING : ItemStatus.CREATED); //UI: if deleting setWaitingDate, then reset status to whatever it was before
//                    status.repaint();
                    }
                }
                noAutoUpdateOnStatusChange = false;
            });
        }

        waitingTill.addActionListener((e) -> {
            noAutoUpdateOnStatusChange = true;
//            status.setStatus(ItemStatus.WAITING);
            if (waitingTill.getDate().getTime() != 0) {
                if (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.ONGOING) { //only update if created or ongoing (not cancelled, Done)
                    status.setStatus(ItemStatus.WAITING);
                    dateSetWaitingDate.setDate(new Date());
                }
            } else { //clearing the waiting date => replace the Waiting status by whatever is appropriate
                if (status.getStatus() == ItemStatus.WAITING) { //only update if created or ongoing (not cancelled, Done)
                    status.setStatus(actualEffort.getDuration() != 0 ? ItemStatus.ONGOING : ItemStatus.CREATED); //UI: if deleting setWaitingDate, then reset status to whatever it was before
                }
            }
            noAutoUpdateOnStatusChange = false;
        });

        status.setStatusChangeHandler((oldStatus, newStatus) -> {
            //if status is set Ongoing and startedOnDate is not set and has not been set explicitly 
            //if status is changed
            //TODO!!! move this logic into Item as static method (or ensure consistent with changes made there), OR, at least check it is consistent with logic elsewhere (eg. if a project is set complete when last subtasks is completed, or in screenItemList)
//            ItemStatus newStatus = status.getStatus();
            if (newStatus == oldStatus || noAutoUpdateOnStatusChange) return;

            if (newStatus == ItemStatus.CREATED && item.getActual() > 0) {
                newStatus = ItemStatus.ONGOING;
            }
            Date now = new Date();
            Date zero = new Date(0);
//            if (newStatus != item.getStatus()) {
            if (newStatus == ItemStatus.ONGOING) {
//                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                if (startedOnDate.getDate().getTime() == 0) {
                    startedOnDate.setDateAndNotify(now);
                    startedOnDate.repaint();
                }
                if (completedDate.getDate().getTime() != 0) {
                    completedDate.setDateAndNotify(zero);
                    completedDate.repaint();
                }
            } else if (newStatus == ItemStatus.CREATED) {
                //if set back to Created, force startedOnDate and completedDate and WaitingDate and ?? back
                //TODO!!!!
                //set back to 0 or if the date was only changed in the UI (item.getStartedOnDate()==0)
//                        if ((startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) || item.getStartedOnDate() == 0) {
                if (startedOnDate.getDate().getTime() != 0) {
                    startedOnDate.setDateAndNotify(zero);
                    startedOnDate.repaint();
                }
                //TODO!!!!!! check the logic for setting dates back to 0!! 
                //TODO In general, need to have methods in Item *without* any side-effects on other fields to ensure that the fields are set to the values seen in the UI
                //TODO extract the logic for which changed fields impact others? Add new Item.setters embedding th needed logic for updating other fields and use those in the places where the automatic updates are needed
//                        if ((completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
                if (completedDate.getDate().getTime() != 0) {
                    completedDate.setDateAndNotify(zero);
                    completedDate.repaint();
                }
//                        if ((dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
//                        if ((dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
//                            completedDate.setDate(new Date(0));
//                        }
            } else if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
//                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                if (startedOnDate.getDate().getTime() == 0) {
                    startedOnDate.setDateAndNotify(now);
                    startedOnDate.repaint();
                }
//                        if (completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) {
                if (true || completedDate.getDate().getTime() == 0) { //UI: will not change if already set ->NO, will always use latest date when set Done, e.g. if marking done by mistake
                    completedDate.setDateAndNotify(now);
                    completedDate.repaint();
                }
            } else if (newStatus == ItemStatus.WAITING) {
//                        if (dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getDateWhenSetWaiting()) {
                dateSetWaitingDate.setDateAndNotify(now);
                dateSetWaitingDate.repaint();
//                        }
                //UI: set startedOnDate when setting Waiting (even if no effort registered)?
//                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                if (startedOnDate.getDate().getTime() == 0) {
                    startedOnDate.setDate(now);
                    startedOnDate.repaint();
                }
            }
            if (newStatus != ItemStatus.WAITING && dateSetWaitingDate.getDate().getTime() != 0) {
                dateSetWaitingDate.setDateAndNotify(zero);
                dateSetWaitingDate.repaint();
            }
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//              status.addActionListener(statusListener);
//        status.addActionListener((evt) -> {
//            //if status is set Ongoing and startedOnDate is not set and has not been set explicitly
//            //if status is changed
//            //TODO!!! move this logic into Item as static method (or ensure consistent with changes made there), OR, at least check it is consistent with logic elsewhere (eg. if a project is set complete when last subtasks is completed, or in screenItemList)
//            ItemStatus newStatus = status.getStatus();
//            if (newStatus == ItemStatus.CREATED && item.getActualEffort() > 0) {
//                newStatus = ItemStatus.ONGOING;
//            }
//            Date now = new Date();
//            Date zero = new Date(0);
//            if (newStatus != item.getStatus()) {
//                if (newStatus == ItemStatus.ONGOING) {
////                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
//                    if (startedOnDate.getDate().getTime() == 0) {
//                        startedOnDate.setDateAndNotify(now);
//                        startedOnDate.repaint();
//                    }
//                    if (completedDate.getDate().getTime() != 0) {
//                        completedDate.setDateAndNotify(zero);
//                        completedDate.repaint();
//                    }
//                } else if (newStatus == ItemStatus.CREATED) {
//                    //if set back to Created, force startedOnDate and completedDate and WaitingDate and ?? back
//                    //TODO!!!!
//                    //set back to 0 or if the date was only changed in the UI (item.getStartedOnDate()==0)
////                        if ((startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) || item.getStartedOnDate() == 0) {
//                    if (startedOnDate.getDate().getTime() != 0) {
//                        startedOnDate.setDateAndNotify(zero);
//                        startedOnDate.repaint();
//                    }
//                    //TODO!!!!!! check the logic for setting dates back to 0!!
//                    //TODO In general, need to have methods in Item *without* any side-effects on other fields to ensure that the fields are set to the values seen in the UI
//                    //TODO extract the logic for which changed fields impact others? Add new Item.setters embedding th needed logic for updating other fields and use those in the places where the automatic updates are needed
////                        if ((completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
//                    if (completedDate.getDate().getTime() != 0) {
//                        completedDate.setDateAndNotify(zero);
//                        completedDate.repaint();
//                    }
////                        if ((dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
////                        if ((dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getCompletedDate()) || item.getCompletedDate() == 0) {
////                            completedDate.setDate(new Date(0));
////                        }
//                } else if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
////                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
//                    if (startedOnDate.getDate().getTime() == 0) {
//                        startedOnDate.setDateAndNotify(now);
//                        startedOnDate.repaint();
//                    }
////                        if (completedDate.getDate().getTime() == 0 && completedDate.getDate().getTime() == item.getCompletedDate()) {
//                    if (true || completedDate.getDate().getTime() == 0) { //UI: will not change if already set ->NO, will always use latest date when set Done, e.g. if marking done by mistake
//                        completedDate.setDateAndNotify(now);
//                        completedDate.repaint();
//                    }
//                } else if (newStatus == ItemStatus.WAITING) {
////                        if (dateSetWaitingDate.getDate().getTime() == 0 && dateSetWaitingDate.getDate().getTime() == item.getDateWhenSetWaiting()) {
//                    dateSetWaitingDate.setDateAndNotify(now);
//                    dateSetWaitingDate.repaint();
////                        }
//                    //UI: set startedOnDate when setting Waiting (even if no effort registered)?
////                        if (startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
//                    if (startedOnDate.getDate().getTime() == 0) {
//                        startedOnDate.setDate(now);
//                        startedOnDate.repaint();
//                    }
//                }
//                if (newStatus != ItemStatus.WAITING && dateSetWaitingDate.getDate().getTime() != 0) {
//                    dateSetWaitingDate.setDateAndNotify(zero);
//                    dateSetWaitingDate.repaint();
//                }
//
//            }
//        });
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener startedOnDateListener = new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                //if startedOnDate is set, then if status has not been set explicitly and it is Created (not Waiting), the set it
////                if (startedOnDate.getDate().getTime() != 0 && item.getStartedOnDate() == 0 && status.getStatus() == item.getStatus() && status.getStatus() == ItemStatus.CREATED) {
//                if (startedOnDate.getDate().getTime() == 0) {
//                    //TODO!! should it be allowed to remove a startdate if there is actual effort? YES, because you want to be able to edit freely the different fields of an Item! (not too much intelligence)
////                    if (actualEffort.getTime() == 0 && status.getStatus() == ItemStatus.ONGOING) { //doesn't matter
//                    if (actualEffort.getDuration() == 0 && status.getStatus() == ItemStatus.ONGOING) { //doesn't matter
//                        status.setStatus(ItemStatus.CREATED);
//                        status.repaint();
//                    }
//                } else { //startedOnDate.getDate().getTime() == 0 => startDate was set OR CHANGED(!)
//                    if (status.getStatus() == ItemStatus.CREATED) { //UI: DON't set to ONGOING if current state is WAITING/CANCELLED/ONGOING
////{// (startedOnDate.getDate().getTime() != 0) {
//                        status.setStatus(ItemStatus.ONGOING);
//                        status.repaint();
//                    }
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                    else {
////                        if (actualEffort.getTime() > 0) {
////                            status.setStatus(ItemStatus.ONGOING); //UI: if deleting setWaitingDate, then reset status to whatever it was before
//////                    } else if (completedDate.getDate().getTime()!=0){
//////                        status.setStatus(ItemStatus.DONE); //UI: if there is a completedDate set, then set status to Done
////                        } else {
////                            status.setStatus(item.getStatus()); //UI: if deleting setWaitingDate, then reset status to whatever it was before
////                        }
////                    }
////                }
////</editor-fold>
//            }
//        };
//</editor-fold>
        startedOnDate.addActionListener((evt) -> {
            //if startedOnDate is set, then if status has not been set explicitly and it is Created (not Waiting), the set it 
//                if (startedOnDate.getDate().getTime() != 0 && item.getStartedOnDate() == 0 && status.getStatus() == item.getStatus() && status.getStatus() == ItemStatus.CREATED) {
            noAutoUpdateOnStatusChange = true;
            if (startedOnDate.getDate().getTime() == 0) {
                //TODO!! should it be allowed to remove a startdate if there is actual effort? YES, because you want to be able to edit freely the different fields of an Item! (not too much intelligence)
//                    if (actualEffort.getTime() == 0 && status.getStatus() == ItemStatus.ONGOING) { //doesn't matter 
                if (status.getStatus() == ItemStatus.DONE || status.getStatus() == ItemStatus.ONGOING) { //doesn't matter 
                    status.setStatus(actualEffort.getDuration() == 0 ? ItemStatus.CREATED : ItemStatus.ONGOING);
                }
                if (completedDate.getDate().getTime() != 0)
                    completedDate.setDate(new Date(0));
//<editor-fold defaultstate="collapsed" desc="comment">
//                else
//                if (actualEffort.getDuration() == 0 && status.getStatus() == ItemStatus.ONGOING) { //doesn't matter
//                    status.setStatus(ItemStatus.CREATED);
////                    status.repaint();
//                }
//</editor-fold>
            } else { //startedOnDate.getDate().getTime() == 0 => startDate was set OR CHANGED(!)
                if (status.getStatus() == ItemStatus.CREATED) { //UI: DON't set to ONGOING if current state is WAITING/CANCELLED/ONGOING
                    status.setStatus(ItemStatus.ONGOING);
//                    status.repaint();
                }
            }
            noAutoUpdateOnStatusChange = false;
        });

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyActionListener completedOnDateListener = new MyActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                boolean completedDateSet = false;
//                //if completeDate is set, then if status has not been set explicitly and it is Created/Ongoing/Waiting), the set it Completed
////                if (completedDate.getDate().getTime() != 0 && item.getCompletedDate() == 0 && status.getStatus() == item.getStatus()
////                        && (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.ONGOING || status.getStatus() == ItemStatus.WAITING)) {
//                if (completedDate.getDate().getTime() != 0) {
//                    status.setStatus(ItemStatus.DONE);
//                } else {
////                    if (actualEffort.getTime() == 0) {
//                    if (actualEffort.getDuration() == 0) {
//                        status.setStatus(ItemStatus.CREATED);
//                    } else {
//                        status.setStatus(ItemStatus.ONGOING);
//                    }
//                }
//                status.repaint();
//                //UI: if startedOnDate is not changed explicitly in UI, the set it to (-Now-) completedDate-actual
//                if (startedOnDate.getDate().getTime() == 0) {// && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
////                    startedOnDate.setDate(new Date(completedDate.getDate().getTime() - ((long) actualEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS));
////                    startedOnDate.setDate(new Date(completedDate.getDate().getTime() - ((long) actualEffort.getDuration()))); //TODO!! define setting to use actualEffort when auto-setting startedOn date?? Probably too smart
//                    startedOnDate.setDateAndNotify(new Date(completedDate.getDate().getTime())); //UI: if setting a completeDate, then if no startedOn date was set, it will also be set to same time
//                    startedOnDate.repaint();
//                }
//            }
//        };
//            public void actionPerformed(ActionEvent evt) {
////                boolean completedDateSet = false;
//                //if completeDate is set, then if status has not been set explicitly and it is Created/Ongoing/Waiting), the set it Completed
//                if (completedDate.getDate().getTime() != 0 && item.getCompletedDate() == 0 && status.getStatus() == item.getStatus()
//                        && (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.ONGOING || status.getStatus() == ItemStatus.WAITING)) {
////                    completedDateSet = true;
////                    status.setStatus(ItemStatus.DONE);
//                    if (completedDate.getDate().getTime() == 0) { //UI: if deleting a completedDate
//                        if (actualEffort.getTime() == 0) {
//                            status.setStatus(ItemStatus.CREATED);
//                        } else {
//                            status.setStatus(ItemStatus.ONGOING);
//                        }
//                    } else {
////                        status.setStatus(ItemStatus.CREATED);
//                        status.setStatus(ItemStatus.DONE);
//                    }
//                    //UI: if startedOnDate is not set, and not changed explicitly in UI, the set it to (-Now-) completedDate-actual
////                    if (item.getStartedOnDate()==0 && startedOnDate.getDate().getTime() == 0 && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
//                    if (item.getStartedOnDate() == 0 || startedOnDate.getDate().getTime() == 0) {// && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
////                        startedOnDate.setDate(new Date());
//                        startedOnDate.setDate(new Date(completedDate.getDate().getTime() - ((long) actualEffort.getTime()) * MyDate.MINUTE_IN_MILLISECONDS));
//                    }
//                }
//
//            }
//        };
//</editor-fold>
        completedDate.addActionListener((evt) -> {
            //if completeDate is set, then if status has not been set explicitly and it is Created/Ongoing/Waiting), the set it Completed
            //UI: if startedOnDate is not changed explicitly in UI, the set it to (-Now-). Or later: completedDate-actual (see below TODO)
            //NB. update startedOnDate *before* updating status, to ensure that completedDate wins (sets to DONE)
            noAutoUpdateOnStatusChange = true;
            if (startedOnDate.getDate().getTime() == 0) {// && startedOnDate.getDate().getTime() == item.getStartedOnDate()) {
                //TODO!! define setting to use actualEffort when auto-setting startedOn date?? Probably too smart
                startedOnDate.setDateAndNotify(new Date(completedDate.getDate().getTime())); //UI: if setting a completeDate, then if no startedOn date was set, it will also be set to same time
                startedOnDate.repaint();
            }

            if (completedDate.getDate().getTime() != 0) {
                status.setStatus(ItemStatus.DONE);
            } else { //deleting completedDate, so reset status
//                    if (actualEffort.getTime() == 0) {
                if (actualEffort.getDuration() != 0 || startedOnDate.getDate().getTime() != 0) {
                    status.setStatus(ItemStatus.ONGOING);
                } else {
                    status.setStatus(ItemStatus.CREATED);
                }
            }
            status.repaint();
            noAutoUpdateOnStatusChange = false;
        });

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
//        if (locallyEditedOwner == null) {
//            locallyEditedOwner = new ArrayList(); //Arrays.asList(item.getOwner())
//            locallyEditedOwner.add(itemLS.getOwner());
//        }
//        SpanButton editOwnerButton = new SpanButton();
//        if (previousValues.get(Item.PARSE_OWNER_ITEM) == null) {
//            previousValues.put(Item.PARSE_OWNER_ITEM, item.getOwner().getObjectIdP());
//        }
//        ItemAndListCommonInterface prevOwner = DAO.getInstance().fetchItemOwner((String) previousValues.get(Item.PARSE_OWNER_ITEM));
//        WrapButton editOwnerButton = new WrapButton((previousValues != null
//                && previousValues.get(Item.PARSE_OWNER_ITEM) != null
//                && ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0)
//                ? DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)).getText()
//                : (item.getOwner() != null ? item.getOwner().getText() : ""));
//</editor-fold>
        WrapButton editOwnerButton = new WrapButton();

        ActionListener refreshOwnerButton = (e) -> {
            String ownerStr
                    = (previousValues != null && previousValues.get(Item.PARSE_OWNER_ITEM) != null && ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0)
                    ? DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)).getText()
                    : (item.getOwner() != null ? item.getOwner().getText() : "");
            editOwnerButton.setText(ownerStr);
        };
//<editor-fold defaultstate="collapsed" desc="comment">
//            final Command editOwnerCmd = Command.create(item.getOwner().getText(), null, (e) -> {
//        Command editOwnerCmd = new Command(item.getOwner() == null ? "<no owner>" : item.getOwner().getText()) {
//        Command editOwnerCmd = MyReplayCommand.create("EditOwner", item.getOwner() == null ? "" : item.getOwner().getText(), null, (e) -> {
//        Command editOwnerCmd = Command.create(item.getOwner() == null ? "" : item.getOwner().getText(), null, (e) -> {
//</editor-fold>
        Command editOwnerCmd = MyReplayCommand.create("EditOwner", null, null, (e) -> {
            List projects = DAO.getInstance().getAllProjects(false);
            projects.remove(item); //Must not be possible to select the item itself as its own owner

            //cconvert list of ObjectId to list of actual owners (well, 0 or 1 owner)
            List<ItemAndListCommonInterface> locallyEditedOwner
                    = previousValues != null && previousValues.get(Item.PARSE_OWNER_ITEM) != null && ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0
                    ? new ArrayList(Arrays.asList(DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)))) //fetch the actual owner 
                    : new ArrayList(Arrays.asList(item.getOwner()));
            //<editor-fold defaultstate="collapsed" desc="comment">
            //                        = new ScreenObjectPicker("Select " + Item.OWNER + " for " + item.getText(), DAO.getInstance().getItemListList(), locallyEditedOwner, ScreenItem.this);
            //                ownerPicker.setUpdateActionOnDone(() -> {
            //                    editOwnerButton.setText(locallyEditedOwner != null ? locallyEditedOwner.getText() : "<no owner>");
            //                    parseIdMap2.put("ItemScreen.ScreenObjectPicker", () -> item.setOwner(locallyEditedOwner));
            //                });
            //</editor-fold>
            ScreenObjectPicker ownerPicker = new ScreenObjectPicker("Select " + Item.OWNER /*+ " for " + item.getText()*/,
                    //                            DAO.getInstance().getItemListList(),
                    ItemListList.getInstance(),
                    projects,
                    locallyEditedOwner, ScreenItem2.this,
                    () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                                ItemAndListCommonInterface newOwner = locallyEditedOwner.size() >= 1 ? locallyEditedOwner.get(0) : null;
//                                editOwnerButton.setText(newOwner != null ? newOwner.getText() : ""); //"<no owner>"
////                                        editOwnerButton.revalidate(); //refresh screen?
//                                parseIdMap2.put("ItemScreen.ScreenObjectPicker", () -> {
//                                    if (localSave) {
//                                        saveNewOwner(newOwner);
//                                    } else {
//                                        item.setOwnerAndMoveFromOldOwner(newOwner);
//                                    }
//                                }); //TODO!!! no need to save item setOwnerAndMoveFromOldOwner since also saved on exit from this screen
//</editor-fold>
                        if (locallyEditedOwner.size() > 0) { //if >0, first element cannot be null!
                            ItemAndListCommonInterface newOwner = locallyEditedOwner.get(0); //even if multiple should be selected (shouldn't be possible), only use first
//                            if ((newOwner==null&&item.getOwner()==null)||newOwner.equals(item.getOwner())) {
                            if (newOwner.equals(item.getOwner())) {
//                                        previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList()); //store empty list (e.g. if previous owner was selected again)
                                previousValues.remove(Item.PARSE_OWNER_ITEM); //store empty list (e.g. if previous owner was selected agagin)
//                                editOwnerButton.setText(item.getOwner()==null?"":item.getOwner().getText());  //set back to old Owner
                                editOwnerButton.setText(item.getOwner().getText());  //set back to old Owner
                            } else { //new owner
                                previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(((ItemAndListCommonInterface) newOwner).getObjectIdP()))); //store objectId of new owner
                                editOwnerButton.setText(newOwner.getText());
                            }
                        } else { //locallyEditedOwner.size()==0 => no selected owner (either old one was deleted, or a previously new one was removed, or simply none was chosen)
                            if (item.getOwner() == null) {
                                previousValues.remove(Item.PARSE_OWNER_ITEM); //remove previousValue, e.g. no owner before, none selected now
                            } else {
                                previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList()); //store empty list (e.g. if previous owner was deselected)
                            }
                            editOwnerButton.setText(""); //"<no owner>"
                        }
                    }, null, 0, 1, true, false, false);
            ownerPicker.show();
        }
        );
        editOwnerButton.setCommand(editOwnerCmd);
        refreshOwnerButton.actionPerformed(null); //set button text *after* setting command
        parseIdMap2.put(Item.PARSE_OWNER_ITEM, () -> {
            if (previousValues.get(Item.PARSE_OWNER_ITEM) != null) {
                if (((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0) {
//                    item.setOwner(DAO.getInstance().fetchItem(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)));
//                    ItemAndListCommonInterface oldOwner = item.getOwner();
//                    if (oldOwner != null) {
//                        oldOwner.removeFromList(item);
//                    }
                    item.removeFromOwner();
                    ItemAndListCommonInterface newOwner = DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0));
                    newOwner.addToList(item);
//                    item.setOwner();
                } else {
//                    ItemAndListCommonInterface oldOwner = item.getOwner();
//                    if (oldOwner != null) {
//                        oldOwner.removeFromList(item);
//                    }
                    item.removeFromOwner();
//                    item.setOwner(null);
                }
            }
        });
        //previousValues stores the ObjectId of the owner, not the owner itself!
        //NB! Item.PARSE_OWNER_ITEM is used to index previousValues, but owner can also be Item.PARSE_OWNER_LIST, but not Item.PARSE_OWNER_TEMPLATE_LIST
//<editor-fold defaultstate="collapsed" desc="comment">
//        initField(Item.PARSE_OWNER_ITEM, editOwnerButton, () -> item.getOwner(), (o) -> item.setOwner((ItemAndListCommonInterface) o),
//                () -> {
//                    if (((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0) {
//                        return (DAO.getInstance().fetchItem((String) ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)));
//                    } else {
//                        return null;
//                    }
//                },
//                (owner2) -> {
//                    if (owner2 != null) {
////                        previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(((ItemAndListCommonInterface) s).getObjectIdP()))); //store as list of ObjectIds
//                        previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(((ItemAndListCommonInterface) owner2).getObjectIdP()))); //store as list of ObjectIds
//                    } else {
//                        previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList()); //store as list of ObjectIds
//                    }
//                });
//</editor-fold>
//        statusCont.add(layout(Item.BELONGS_TO, editOwnerButton, Item.BELONGS_TO_HELP, true, false, false)); //.add(new SpanLabel("Click to move task to other projects or lists"));
        statusCont.add(layoutN(Item.BELONGS_TO, editOwnerButton, Item.BELONGS_TO_HELP)); //.add(new SpanLabel("Click to move task to other projects or lists"));

        //DEPENDS ON
        //TODO!!!! implement DependsOn properly before enabling it
        //TODO!!! add objectPicker showing only subtasks/projects in this project
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            if (item.getDependingOnTask() != null) {
//                MyTextArea dependsOn = new MyTextArea(Item.DEPENDS_ON, 20, TextArea.ANY, parseIdMap2, () -> {
//                    Item dependingOnTask = item.getDependingOnTask();
//                    return dependingOnTask == null ? "" : dependingOnTask.getText();
//                },
//                        (d) -> {
//                            //TODO implement editing of owner directly (~Move to another project or list)
//                        });
//                dependsOn.setEditable(false);
//            }
//
//            Item dependingOnTask = item.getDependingOnTask();
//            if (dependingOnTask != null) {
//                Label dependsOnLabel = new Label(dependingOnTask == null ? "" : dependingOnTask.getText());
//
////        statusCont.add(new Label(Item.DEPENDS_ON)).add(dependsOn); //.add(new SpanLabel("Click to move task to other projects or lists"));
////        statusCont.add(layout(Item.DEPENDS_ON, dependsOn, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
////                statusCont.add(layout(Item.DEPENDS_ON, dependsOnLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//                statusCont.add(layoutN(Item.DEPENDS_ON, dependsOnLabel, Item.DEPENDS_ON_HELP)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            }
//        }
//</editor-fold>
        //ORIGINAL SOURCE
        if (item.getSource() != null && MyPrefs.showSourceItemInEditScreens.getBoolean()) { //don't show unless defined
            //TODO!! what happens if source is set to template or other item, then saved locally on app exit and THEN recreated via Replay???
//            Label sourceLabel = new Label(itemLS.getSource() == null ? "" : item.getSource().getText(), "LabelFixed");
            Label sourceLabel = new Label(item.getSource().getText(), "LabelFixed");
//            statusCont.add(new Label(Item.SOURCE)).add(source); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, source, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, sourceLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            statusCont.add(layoutN(Item.SOURCE, sourceLabel, Item.SOURCE_HELP, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            sourceLabel.setUIID();
        }

        //INTERRUPT
//        MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> itemLS.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
        MyOnOffSwitch interruptTask = new MyOnOffSwitch();
//                statusCont.add(new Label(Item.INTERRUPT_TASK)).add(interruptTask).add(new SpanLabel("This task interrupted another task"));
//        statusCont.add(layout(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task"));
        initField(Item.PARSE_INTERRUPT_OR_INSTANT_TASK, interruptTask, () -> item.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask((boolean) b),
                () -> interruptTask.isValue(), (b) -> interruptTask.setValue((boolean) b));
        statusCont.add(layoutN(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task", true));
//        if (item.isInteruptOrInstantTask()) {
//            MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> item.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
        //INTERRUPTED TASK
        if (itemLS.getTaskInterrupted() != null) {
            //TODO!!! enable deleting the taskInterrupted (in case wrong or meaningless)
//            statusCont.add(layout(Item.INTERRUPT_TASK_INTERRUPTED,
//                    new SpanLabel("\"" + itemLS.getTaskInterrupted() != null ? itemLS.getTaskInterrupted().getText() + "\"" : "<none>"), "**", true, false, true)); //TODO capture and save the task that was interrupted
            statusCont.add(layoutN(Item.INTERRUPT_TASK_INTERRUPTED,
                    //                    new SpanLabel("\"" + itemLS.getTaskInterrupted() != null ? itemLS.getTaskInterrupted().getText() + "\"" : "<none>"),
                    new SpanLabel(itemLS.getTaskInterrupted().getText()),
                    Item.INTERRUPT_TASK_INTERRUPTED_HELP, true)); //TODO capture and save the task that was interrupted
//            } else {
//                statusCont.add(new Label(Item.INTERRUPT_OR_INSTANT_TASK)).add(interruptTask);
        }
//        }

        if (MyPrefs.showObjectIdsInEditScreens.getBoolean()) {
//        Label itemObjectId = new Label(item.getObjectIdP() == null ? "<created when saved>" : item.getObjectIdP(), "LabelFixed");
            Label itemObjectId = new Label(item.getObjectIdP() == null ? "<set on save>" : item.getObjectIdP(), "LabelFixed");
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//        statusCont.add(layout(Item.OBJECT_ID, itemObjectId, "**", true, true, true));
            statusCont.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true));
        }

//<editor-fold defaultstate="collapsed" desc="comment">
        //TAB SUBTASKS
//        cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont = buildContentPaneForListOfItems(item, itemList);
//        Container subTaskCont = buildContentPaneForListOfItems(item.getItemList());
//        Container subTaskCont = buildContentPaneForListOfItems(item.getList());
//    public MyTree(MyTreeModel model, MyTree parentTree, int depth, boolean expandThisTree, boolean expandAllLevels, boolean onlySubTree) {
//        Container subTaskCont = new MyTree(item, null, 0, true, false, true);
//        Container subTaskCont = new MyTree2(item) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Container cmp = buildItemContainer((Item) node, null, () -> true, () -> subTaskTree.removeFromCache());
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
//        tabs.addTab("Subtasks", null, buildContentPaneForListOfItems(item.getItemList()));
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            tabs.addTab("Subtasks", null, new Button(Command.create("Edit subtasks", null, (e) -> {
//                ItemList itemList = item.getItemList();
////                DAO.getInstance().fetchAllItemsIn((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                putEditedValues2(parseIdMap2);
//                new ScreenListOfItems(item.getText(), itemList, ScreenItem.this, (iList) -> {
////                            itemList.setList(iList.getList());
//                    item.setItemList(itemList);
//                    DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    refreshAfterEdit(); //necessary to update sum of subtask effort
////                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList), null); //update the container with edited content
////<editor-fold defaultstate="collapsed" desc="comment">
////                                categoryList.addItemAtIndex(category, 0);
////                                DAO.getInstance().save(categoryList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                            itemList.setList(itemList.getList());
////                            DAO.getInstance().save(itemList);
////                        },
////                        (node) -> {
////                            return buildItemListContainer((ItemList) node);
////                        },
////                        (itemList) -> {
////                            ItemList newItemList = new ItemList();
////                            new ScreenItemListProperties(newItemList, ScreenListOfItemLists.this, () -> {
////                                DAO.getInstance().save(newItemList); //save before adding to itemList
////                                itemList.addItem(newItemList);
////                            }).show();
////</editor-fold>
////                }, null, false, templateEditMode
//                }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                ).show();
//            })));
//        }
//</editor-fold>
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
////                    subTaskCont = buildContentPaneForListOfItems(item.getItemList());
////                    Container newSubTaskCont = buildContentPaneForListOfItems(item.getItemList());
////                    Container newSubTaskCont = buildContentPaneForListOfItems(item.getList());
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        TableLayout.Constraint cn = tl.createConstraint();
//        cn.setHorizontalSpan(spanButton);
//        cn.setHorizontalAlign(Component.RIGHT);
//        if (lastTabSelected >= 0 && tabs.getTabCount() > 0) {
//            tabs.setSelectedIndex(lastTabSelected); //keep same tab selected even if regenerating the screen
//        }
//        if (previousValues.containsKey(LAST_TAB_SELECTED) >= 0 && tabs.getTabCount() > 0) {
//            tabs.setSelectedIndex(lastTabSelected); //keep same tab selected even if regenerating the screen
//        }
//</editor-fold>
        if (previousValues.containsKey(LAST_TAB_SELECTED)) {
            tabs.setSelectedIndex((int) previousValues.get(LAST_TAB_SELECTED)); //keep same tab selected even if regenerating the screen
        }

        setCheckIfSaveOnExit(() -> checkItemIsValidForSaving(description.getText(), comment.getText(), dueDate.getDate(), actualEffort.getDuration(),
                remainingEffort.getDuration(), (((List) previousValues.get(Item.PARSE_CATEGORIES)) != null ? ((List) previousValues.get(Item.PARSE_CATEGORIES)).size() : 0), item.getListFull().size())); //item.getListFull().size() sinze subtasks are stored 
//TODO: when owner can be edited, use new/edited one

        return cont;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    
//    public void saveEditedValuesLocallyOnAppExitXXX() {
////        if (item.getObjectIdP() == null) { //new item, save everything locally and restore next time
//////            Storage.getInstance().writeObject(SCREEN_TITLE + "- EDITED ITEM", item); //save date
////            Storage.getInstance().writeObject(FILE_LOCAL_EDITED_ITEM, item); //save
////
////        } else { //edited item, update item but only save locally, then restore edit fields based on locally saved values
////            putEditedValues2(parseIdMap2);
////        }
//        localSave = true;
//        putEditedValues2(parseIdMap2);
//        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_ITEMXXX, item); //save
//        localSave = false;
//    }
//
//    @Override
//    public boolean restoreEditedValuesSavedLocallyOnAppExitXXX() {
////        Item itemLS = null;
//        boolean savedValues;
//        //if editing of item was ongoing when app was stopped, then recover saved item
//        ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEMXXX) || ReplayLog.getInstance().isReplayInProgress()); //local item => replay must/should be Ongoing
//        if (ReplayLog.getInstance().isReplayInProgress() && Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEMXXX)) {
//            itemLS = (Item) Storage.getInstance().readObject(FILE_LOCAL_EDITED_ITEMXXX); //read in when initializing the Timer - from here on it is only about saving updates
//            savedValues = true;
//        } else {
////            itemLS = this.item; //it no locally saved edits, then use item to 'feed' the edits fields
//            ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEMXXX));
//            deleteEditedValuesSavedLocallyOnAppExit();
//            savedValues = false;
//        }
////        return itemLS;
//        return savedValues;
//    }
//
//    @Override
//    public void deleteEditedValuesSavedLocallyOnAppExit() {
//        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_ITEMXXX); //delete in case one was
//    }
//
//    private void saveNewOwner(ItemAndListCommonInterface newOwner) {
//        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_OWNER, newOwner.getObjectIdP()); //save
//    }
//
//    private List<ItemAndListCommonInterface> restoreNewOwner_N() {
//        List<ItemAndListCommonInterface> locallyEditedOwner = null;
////        ItemAndListCommonInterface locallyStoreOwner;
//        //DONE!!!!! DAO.fetchFromCacheOnly only works for Item so selecting a new ItemList as owner will crash!!
//        ItemAndListCommonInterface locallyStoreOwner = (ItemAndListCommonInterface) DAO.getInstance().fetchFromCacheOnly((String) Storage.getInstance().readObject(FILE_LOCAL_EDITED_OWNER)); //save
////        locallyStoreOwner = (ItemAndListCommonInterface) DAO.getInstance().fetchIfNeededReturnCachedIfAvail((ParseObject) locallyStoreOwner);
////        if (restoreNewOwner() != null) {
//        if (locallyStoreOwner != null) {
//            locallyEditedOwner = new ArrayList();
//            locallyEditedOwner.add(locallyStoreOwner);
//        }
//        return locallyEditedOwner;
//    }
//
//    public void deleteNewOwner() {
//        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_OWNER); //delete in case one was
//    }
//
//    private void saveNewCategories(List<Category> categories) {
//        List<String> catObjIds = new ArrayList();
//        for (Category cat : categories) {
//            catObjIds.add(cat.getObjectIdP());
//        }
//        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_CATEGORIES, catObjIds); //save
//    }
//
//    private List<Category> restoreNewCategories_N() {
//        List<String> catObjIds = (List) Storage.getInstance().readObject(FILE_LOCAL_EDITED_CATEGORIES);
//        List<Category> categories = null;
//        if (catObjIds != null) {
//            for (String cat : catObjIds) {
//                if (categories == null) {
//                    categories = new ArrayList<>();
//                }
//                categories.add((Category) DAO.getInstance().fetchFromCacheOnly(cat));
//            }
//        }
//        return categories;
//    }
//
//    private void saveNewRepeatRule(RepeatRuleParseObject repeatRule) {
//        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_REPEAT_RULE, repeatRule); //save
//    }
//
//    private RepeatRuleParseObject restoreNewRepeatRule_N() {
//        return (RepeatRuleParseObject) Storage.getInstance().readObject(FILE_LOCAL_EDITED_REPEAT_RULE);
//    }
//</editor-fold>
}
