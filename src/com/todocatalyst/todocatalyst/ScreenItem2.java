package com.todocatalyst.todocatalyst;

import com.codename1.compat.java.util.Objects;
import com.codename1.components.InfiniteProgress;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.Button;
import com.codename1.ui.CheckBox;
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
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.table.TableLayout;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.Item.COPY_EXCLUDE_CATEGORIES;
import static com.todocatalyst.todocatalyst.MyForm.REPEAT_RULE_KEY;
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
    private Item itemOrg;
    private Item itemCopy;

    MyTextField description;
    MyCheckBox status;
    Button starred;
    MyTextField comment;

    MyDateAndTimePicker alarmDate;
    MyDateAndTimePicker dueDate;
    MyDateAndTimePicker startByDate;
    MyDateAndTimePicker startedOnDate;
    MyDatePicker waitingTill;
    MyDatePicker expireByDate;
    MyDateAndTimePicker waitingAlarm;
    MyDateAndTimePicker dateSetWaitingDate;
    MyDateAndTimePicker completedDate;
    MyDatePicker hideUntil;

    MyDurationPicker actualEffort;
    MyComponentGroup challenge;
    MyComponentGroup dreadFun;
    MyNumericTextField earnedValue;
    MyDurationPicker effortEstimate;
    MyDurationPicker remainingEffort;
    MyComponentGroup priority;
    MyComponentGroup importance;
    MyComponentGroup urgency;
    MyOnOffSwitch interruptTask;

//    private Item item; //FromLocalStorage, read from local storage if app was stopped with unsaved edits to Item, otherwise set to edited item
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
    Date dueDateEditedInRepeatRuleScreen = null;
    
    protected static int callDepth; //use to ensure unique names to locallyStored data
    private int getCallDepth(){
        return callDepth;
    }
    private void setCallDepth(int depth){
         callDepth=depth;
    }
    
    

//    ScreenItem(Item item, MyForm previousForm) { //throws ParseException, IOException {
//        this(item, previousForm, ()->{});
//    }
//    ScreenItem2(Item item, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
//        this(item, previousForm, doneAction, false);
//    }
    private static String getScreenTitle(boolean isTemplate, String title) {
        return (isTemplate ? "TEMPLATE: " : "") + title;
    }

    ScreenItem2(Item item, MyForm previousForm, Runnable doneAction, boolean templateEditMode, SaveEditedValuesLocally previousValues) { //throws ParseException, IOException {
        this(item, previousForm, doneAction, null, templateEditMode, previousValues);
    }
//    ScreenItem2(Item item, MyForm previousForm, UpdateField doneAction, boolean templateEditMode) { //throws ParseException, IOException {
//        this(item, previousForm, doneAction, templateEditMode, null);
//    }

    ScreenItem2(Item item, MyForm previousForm, Runnable doneAction, Runnable cancelAction, boolean templateEditMode, SaveEditedValuesLocally previousValuesN) { //throws ParseException, IOException {
//        super("Task", previousForm, doneAction);
//        super((item.isTemplate() ? "TEMPLATE: " : "") + item.getText(), previousForm, doneAction);
//        super(getScreenTitle(item.isTemplate(), item.getText()), previousForm, doneAction);
        super((item.isTemplate() ? "TEMPLATE: " : "") + item.getText(), previousForm, doneAction, cancelAction);
        setUniqueFormId("ScreenEditItem");
//        FILE_LOCAL_EDITED_ITEM= getTitle()+"- EDITED ITEM";
        if (false) {
            ASSERT.that(item.isDataAvailable(), () -> "Item \"" + item + "\" data not available");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (previousValues == null) {
////            this.previousValues = new SaveEditedValuesLocally(FORM_UNIQUE_ID + "-" + item.getObjectIdP());
//            this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + item.getObjectIdP());
//        } else {
//            if (this.previousValues != null && previousValues != null) {
//                this.previousValues.addAndOverwrite(previousValues);
//            } else
//                this.previousValues = previousValues;
//        }
//</editor-fold>
        this.itemOrg = item;
        String prevValId = itemOrg.getObjectIdP() != null ? itemOrg.getObjectIdP() : ("From-" + previousForm.getUniqueFormId());
//        this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + item.getObjectIdP());
        this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + prevValId);
        this.previousValues.addAndOverwrite(previousValuesN);

        this.templateEditMode = itemOrg.isTemplate() || templateEditMode; //
        getTitleComponent().setEndsWith3Points(true);
//        ScreenItemP.item = item;
//        initLocalSaveOfEditedValues(getUniqueFormId() + item.getObjectIdP());
//        previousValues = new SaveEditedValuesLocally( getUniqueFormId() + item.getObjectIdP());
//        expandedObjects = new HashSet();
//        expandedObjects = new ExpandedObjects(FORM_UNIQUE_ID,this.item);
        if (false) {
//            expandedObjects = new ExpandedObjects(getUniqueFormId() + this.itemOrg.getObjectIdP()); //NOT used in this screen
            expandedObjectsInit(this.itemOrg.getObjectIdP()); //NOT used in this screen
        }
        try {
            //        DAO.getInstance().deleteCategoryFromAllItems(cat);
            if (this.itemOrg != null) {
                this.itemOrg.fetchIfNeeded();
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//RESTORE locally edited value (if stored on app pause/exit)
//        itemLS = (Item) restoreLocallyEditedValuesOnAppExit();
//        boolean valuesRestored = restoreEditedValuesSavedLocallyOnAppExitXXX();
//        if (itemLS != null && this.item.getObjectIdP() == null) {
//        if (valuesRestored && this.item.getObjectIdP() == null) {
//            this.item = itemLS; //if item is a new item, then we completely ignore that Item and continue with the previously locally saved values
//        } else {
//            itemLS = this.item; //if no locally saved edits, then use item to 'feed' the edits fields
//        }
//        item = this.item; //quick hack. TODO!! replace all references to itemLS with item
//</editor-fold>
        itemCopy = new Item(false); //create a coppy to allow changing the owner to update inherited values
        itemOrg.copyMeInto(itemCopy, Item.CopyMode.COPY_ALL_FIELDS, COPY_EXCLUDE_CATEGORIES | Item.COPY_EXCLUDE_SUBTASKS | Item.COPY_EXCLUDE_REPEAT_RULE); //don't copy categories since that will add the itemCopy to the categories and create ObjId reference errors when saving categories
//        itemCopy.setSource(item); //DONE in copyMeInto. Use the item that was copied, the original source can be found recursively if needed! NO: force source back to org. item's source (if any)

//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
        setScrollable(false);
//        setToolbar(new Toolbar());
//        addCommandsToToolbar(getToolbar());//, theme);
//        buildContentPane(getContentPane());
        setUpdateActionOnCancel(() -> {
            List<Item> editedSubtasks = previousValues.getItemsN();

            //on cancel, delete any added elements
            List addedSubtasks = null;
            if (editedSubtasks != null) {
                addedSubtasks = new ArrayList(previousValues.getItemsN());
                addedSubtasks.removeAll(itemOrg.getListFull());
            }
            DAO.getInstance().deleteAll(addedSubtasks, false, false);

            //on cancel, UN-delete any deleted subtasks/subprojects
            //using softDeleted date is not enough, but if capturing date when ScreenItem2 is launched and then undeleting any deleted *after* that might work?!
            List<Item> subtasks = itemOrg.getListFull();
            Date undeleteAfter = getEditSessionStartTime();
            for (Item i : subtasks) {
//                if (i.getSoftDeletedDateN().getTime() > undeleteAfter.getTime()) { //checked in undelete()
                i.undelete(null, getEditSessionStartTime());
            }
        });
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
     * validates a Item before saving. Returns null if no error, otherwise an
     * error message string to display.
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
        } else {
            return false;
        }
    }

    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

        super.addCommandsToToolbar(toolbar);
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

        //NEW TASK to Inbox
        toolbar.addCommandToOverflowMenu(makeCommandNewItemSaveToInbox());

//<editor-fold defaultstate="collapsed" desc="comment">
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(() -> item.hasSaveableData())); //TEST is no good since hasSaveable tests the item, and not the values entered in the screen!!
//        Command backCommand = makeDoneUpdateWithParseIdMapCommand();
////        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
//        toolbar.setBackCommand(backCommand);
//       Command backCommand= addStandardBackCommand();
//</editor-fold>
        addStandardBackCommand();

        setCheckIfSaveOnExit(() -> itemOrg.hasSaveableData());
//<editor-fold defaultstate="collapsed" desc="comment">
//        Command exitScreenItemAndUpdateAndSave = new Command("", Icons.iconBackToPrevFormToolbarStyle()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                if (getCheckIfSaveOnExit() == null || getCheckIfSaveOnExit().check()) {
//
//                    Runnable repeatRule = parseIdMap2.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)
//
//                    for (Object parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                        parseIdMap2.get(parseId).run();
//                    }
//                    if (getUpdateActionOnDone() != null)
//                        getUpdateActionOnDone().run();
//
//                    if (repeatRule != null) {
//                        if (item != null && item.getObjectIdP() == null)
//                            DAO.getInstance().saveInBackground(item); //if not saved
//                        repeatRule.run();
//                    }
////                    putEditedValues2(parseIdMap2);
//                    showPreviousScreen(true);
//                }
//            }
//        };
//        Command exitScreenItemAndUpdateAndSave = backCommand;
//        exitScreenItemAndUpdateAndSave.putClientProperty("android:showAsAction", "withText");

        //TIMER
//        Command timerCmd = makeTimerCommand(title, iconNew, itemList);
//        toolbar.addCommandToLeftBar(cmd);
//</editor-fold>
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
            new ScreenListOfWorkSlots(itemOrg, ScreenItem2.this, null, //(iList) -> {
                    //                    itemList.setWorkSLotList(iList); //NOT necessary since each slot will be saved individually
                    //                    refreshAfterEdit(); //TODO CURRENTLY not needed since workTime is not shown (but could become necessary if we show subtasks and their finish time 
                    false).show();
//            }
        }));
//        }

        //TEMPLATE
        if (!templateEditMode) {
            toolbar.addCommandToOverflowMenu(CommandTracked.create("Save as template", Icons.iconSaveAsTemplate, (e) -> {
//                Dialog ip = new InfiniteProgress().showInfiniteBlocking();
                //TODO add setting to let user edit template after creation
                //TODO enable user to select which fields to exclude
//                putEditedValues2(parseIdMap2, item); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
//                putEditedValues2(parseIdMap2); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
                parseIdMap2.update(); //put any already edited values before saving as template (=> no Cancel possible on edits on item itself)
                Item newTemplate = new Item(false);
                itemOrg.copyMeInto(newTemplate, Item.CopyMode.COPY_TO_TEMPLATE);
                DAO.getInstance().saveNew(newTemplate, false);
//                TemplateList templateList = DAO.getInstance().getTemplateList();
                TemplateList templateList = TemplateList.getInstance();
                if (MyPrefs.insertNewItemsInStartOfLists.getBoolean()) {
                    templateList.add(0, newTemplate);
                } else {
                    templateList.add(newTemplate);
                }
//                DAO.getInstance().saveNew((ParseObject) templateList, true);
                DAO.getInstance().saveNew(newTemplate, templateList);
                DAO.getInstance().saveNewExecuteUpdate();
//                ip.dispose();
//                if (Dialog.show("INFO", "Do you want to edit the template now? You can find and edit it later under Templates.", "Yes", "No")) {
//                    new ScreenItem(template, ScreenItem.this, () -> {
//                        DAO.getInstance().save(template);
//                    }).show();
//                }
//                new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, DAO.getInstance().getTemplateList(), ScreenItem.this, (i) -> {
                if (MyPrefs.showTemplateListAfterCreatingNewTemplateFromExistingProject.getBoolean()) {
                    new ScreenListOfItems(SCREEN_TEMPLATES_TITLE, () -> TemplateList.getInstance(), ScreenItem2.this, (i) -> {
                    }, ScreenListOfItems.OPTION_TEMPLATE_EDIT// | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                    ).show();
                }
            }, "SaveAsTemplate"));
        }

        if (true || !templateEditMode) { //UI: KEEP for templates to allow inserting another template as a sub-hierarcy under a template
            //INSERT A TEMPLATE INTO AN ITEM (merge top-level project and add subtasks)
            toolbar.addCommandToOverflowMenu(CommandTracked.create("Merge in template", Icons.iconAddFromTemplate, (e) -> { //"Insert template"
                //TODO!! Add "don't show again + setting to all these info popups
                if (!MyPrefs.askBeforeInsertingTemplateIntoAndUnderAnAlreadyCreatedItem.getBoolean()
                        || Dialog.show("INFO", "Inserting a template into a task will add the values and subtasks from the template to the task. It will not overwrite any fields already defined manually in the task", "OK", "Cancel")) {
                    //TODO enable user to select which fields to exclude
//                    putEditedValues2(parseIdMap2, item); //save any already edited values before inserting the template
//                    putEditedValues2(parseIdMap2); //save any already edited values before inserting the template to avoid overwriting values only entered into the screen but not stored in the Item itself (yet)
                    if (false)parseIdMap2.update(); //save any already edited values before inserting the template to avoid overwriting values only entered into the screen but not stored in the Item itself (yet)
//                    Item template = pickTemplateOLD(); //TODO!!!! make this a full Screen picker like CategorySelector
                    List selectedTemplates = new ArrayList();
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (false) { //shouldn't be necessary
//                        Form f = Display.getInstance().getCurrent(); //tip from CN1: close dialog *before* showing next form
//                        if (f instanceof Dialog) {
//                            ((Dialog) f).dispose();
//                        }
//                    }
//</editor-fold>
//                    new ScreenObjectPicker(SCREEN_TEMPLATE_PICKER, DAO.getInstance().getTemplateList(), selectedTemplates, ScreenItem.this, () -> {

                    new ScreenObjectPicker(SCREEN_TEMPLATE_PICKER, TemplateList.getInstance(), null, selectedTemplates, ScreenItem2.this, () -> {
                        if (selectedTemplates.size() >= 1) {
                            Item template = (Item) selectedTemplates.get(0);
//                            Dialog ip = new InfiniteProgress().showInfiniteBlocking();
//                            template.copyMeInto(item, Item.CopyMode.COPY_FROM_TEMPLATE);
                            if (true) {
                                addTemplateToPickers(itemOrg, template);
                            } else {
                                template.copyMeInto(itemCopy, Item.CopyMode.COPY_FROM_TEMPLATE_TO_TASK,
                                        Item.COPY_EXCLUDE_CATEGORIES
                                        | //categories handled below
                                        Item.COPY_EXCLUDE_SUBTASKS
                                        | Item.COPY_EXCLUDE_REPEAT_RULE);
//                            if (template.getCategories().size()>0) {
//                            if (previousValues.get(Item.PARSE_CATEGORIES) != null)//if categories already set
                                Item.addCatObjectIdsListToCategoryList(((List<String>) previousValues.get(Item.PARSE_CATEGORIES)), template.getCategories()); //*add* any additional categories in the template
//                            else
//                                previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(template.getCategories())); //set the edited categories to those of the template

                                if (previousValues.get(Item.PARSE_REPEAT_RULE) == null && template.getRepeatRuleN() != null) {
                                    previousValues.put(Item.PARSE_REPEAT_RULE, new RepeatRuleParseObject(template.getRepeatRuleN()));
                                }

                                List<ParseObject> newSubtasks = new ArrayList();
                                for (Item tempSubtask : (List<Item>) template.getListFull()) { //full list, filter has no meaning for a template
//                                item.addToList(subtask); //UI: template subtasks are permanently (no Cancel possible) added to item
                                    Item subtaskCopy = new Item(false);
                                    tempSubtask.copyMeInto(subtaskCopy, Item.CopyMode.COPY_FROM_TEMPLATE_TO_TASK, 0);
                                    itemOrg.addToList(subtaskCopy); //UI: template subtasks are permanently (no Cancel possible) added to item
                                    newSubtasks.add(subtaskCopy);//save each new templateCopy in a list and save them
                                }
                                DAO.getInstance().saveNew(newSubtasks);
//<editor-fold defaultstate="collapsed" desc="comment">
//                            if (false) {
//                                parseIdMap2.put("SaveSubtasks", () -> DAO.getInstance().saveInBackground(item)); //NECESSARY since if no other edits
//                            }
//                            if (false && template.getList().size() > 0) {
//                                DAO.getInstance().saveInBackground(item); //NECESSARY since if item not saved, or Cancel, the updated subtask list will linger and be saved later
//                            }
//</editor-fold>
                                if (false && itemOrg.getSource() == null) { //source could already be set, e.g. if repeat copy
                                    itemCopy.setSource(template);
                                    parseIdMap2.put("SetSource", () -> itemOrg.setSource(template)); //NECESSARY since if no other edits
                                }
                            }
//<editor-fold defaultstate="collapsed" desc="comment">
//                            DAO.getInstance().saveTemplateCopyWithSubtasksInBackground(item);
//                            DAO.getInstance().saveTemplateCopyWithSubtasksInBackground(item);
//                            DAO.getInstance().saveProjectInBackground(item);
//                            if (false) {
//                                DAO.getInstance().saveInBackground(item);
//                            }
//                            locallyEditedCategories = null; //HACK needed to force update of locallyEditedCategories (which shouldn't be refreshed when eg editing subtasks to avoid losing the edited categories)
//                            ip.dispose();
//                            refreshAfterEdit(); //DONE on Back from ScreenObjectPicker
//</editor-fold>
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
                    }, 0, 1, true, false, false).show(); //0: Ok to not select any template => nothing inserted
//                    if (template != null) {
                };
            }, "CreateFromTemplate"));

            toolbar.addCommandToOverflowMenu(makeEditFilterSortCommand(itemOrg));
        }

        //DELETE
        toolbar.addCommandToOverflowMenu(CommandTracked.create("Delete", Icons.iconDelete, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//            item.delete();
//            DAO.getInstance().delete(item);
//</editor-fold>
//            item.softDelete();
            DAO.getInstance().delete(itemOrg, false, true);
//<editor-fold defaultstate="collapsed" desc="comment">
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//            showPreviousScreenOrDefault(previousForm, true);
//</editor-fold>
            showPreviousScreen(true);
        }, "DeleteItem"));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ItemSettings", "Task settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsItem("Settings tasks", ScreenItem2.this, () -> {
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        toolbar.addCommandToOverflowMenu(CommandTracked.create("Cancel", Icons.iconCancel, (e) -> {
            //TODO!!! popup to say Cancel is not implemented yet
            showPreviousScreen(true);
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
//        // SUBTASKS
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
//            // SUBTASK LIST
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
    public static Date makeDefaultDueDate() {
        long defaultDue = MyDate.currentTimeMillis() + MyPrefs.itemDueDateDefaultDaysAheadInTime.getInt() * MyDate.DAY_IN_MILLISECONDS;
        return MyDate.roundDownToFullMinutes(new MyDate(defaultDue));
    }

    public static Date makeDefaultAlarmDate(Date dueDate) {
        if (dueDate == null || dueDate.getTime() == 0) {
            dueDate = new MyDate(makeDefaultDueDate().getTime());
        }
        long defaultAlarm = Math.max(dueDate.getTime() - MyPrefs.itemDefaultAlarmTimeBeforeDueDateInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS,
                MyDate.currentTimeMillis() + MyPrefs.itemDefaultAlarmTimeBeforeDueDateInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //UI: when setting an alarm for a past due date, use now as reference
        return MyDate.roundDownToFullMinutes(new MyDate(defaultAlarm)); //round off to remove seconds/milliseconds
//        return new Date(defaultAlarm); //round off to remove seconds/milliseconds (now done in makeDefaultDueDate())
    }

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
//        comment.setUIID("Comment");
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
//            comment.setText(Item.addTimeToComment(comment.getText()));
////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
////comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
//        }));
//</editor-fold>
        if (false) {
            comment.getAllStyles().setMarginRight(0);
            comment.getAllStyles().setPaddingRight(0);
        }

        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(comment);
        if (false) {
            addTimeStampToComment.getAllStyles().setMarginLeft(0);
            addTimeStampToComment.getAllStyles().setPaddingLeft(0);
            addTimeStampToComment.getAllStyles().setMarginRight(0);
            addTimeStampToComment.getAllStyles().setPaddingRight(0);
        }
//}
//        mainCont.add(new Label(Item.COMMENT)).add(comment);
//        mainCont.add(new Label(Item.COMMENT)).add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment));
//        mainCont.add(FlowLayout.encloseIn(makeHelpButton(Item.COMMENT, "**"), addTimeStampToComment));
        Container ts = FlowLayout.encloseRight(addTimeStampToComment);
//        Container all = MyBorderLayout.centerEastWest(comment, ts, null);
        Container all = LayeredLayout.encloseIn(comment, ts);
        if (false) {
            all.setUIID("TextArea");
        }
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
     *
     * @param item
     * @param prevOwner this is the current (possibly already edited) owner
     * @param newOwner
     */
    private void updateInheritedPickers(Item item, Item prevOwner, Item newOwner) { //if one of owners is no longer an Item, then use null and do nothing with value
        //DATE FIELDS
        if (item.isDueDateInheritanceOn() && Objects.equals(dueDate.getDate(), prevOwner.getDueDateD())) {
            dueDate.setDate(newOwner.getDueDateD());
        }
        if (item.isWaitingTillInheritanceOn() && Objects.equals(waitingTill.getDate(), prevOwner.getWaitingTillDate())) {
            waitingTill.setDate(newOwner.getWaitingTillDate());
        }
        if (item.isHideUntilDateInheritanceOn() && Objects.equals(hideUntil.getDate(), prevOwner.getHideUntilDateD())) {
            hideUntil.setDate(newOwner.getHideUntilDateD());
        }
        if (item.isStartByInheritanceOn() && Objects.equals(startByDate.getDate(), prevOwner.getStartByDateD())) {
            startByDate.setDate(newOwner.getStartByDateD());
        }

        //INTEGER FIELDS
        if (item.isPriorityInheritanceOn() && Objects.equals(priority.getSelectedIndex() + 1, prevOwner.getPriority())) {
            priority.selectIndex(newOwner.getPriority() - 1);
        }

        //ENUM FIELDS
        if (item.isImportanceInheritanceOn() && Objects.equals(importance.getSelected(), prevOwner.getImportanceN())) {
            importance.selectValue(newOwner.getImportanceN());
        }
        if (item.isUrgencyInheritanceOn() && Objects.equals(urgency.getSelected(), prevOwner.getUrgencyN())) {
            urgency.selectValue(newOwner.getUrgencyN());
        }
        if (item.isChallengeInheritanceOn() && Objects.equals(challenge.getSelected(), prevOwner.getChallengeN())) {
            challenge.selectValue(newOwner.getChallengeN());
        }
        if (item.isDreadFunInheritanceOn() && Objects.equals(dreadFun.getSelected(), prevOwner.getDreadFunValueN())) {
            dreadFun.selectValue(newOwner.getDreadFunValueN());
        }
//        //<editor-fold defaultstate="collapsed" desc="comment">

//            if (previousValues.get(Item.PARSE_URGENCY) != null) { //a value has been edited by user
//                if (MyUtil.eql(HighMediumLow.valueOf((String) previousValues.get(Item.PARSE_URGENCY)), prevOwner.getUrgencyN()) //if current picker value is inherited from the 'active' owner (whether existing or recently edited)
//        if (item.isUrgencyInheritanceOn()) {
//            if (previousValues.get(Item.PARSE_URGENCY) != null) { //a value has been edited by user
//                if (MyUtil.eql(HighMediumLow.valueOf((String) previousValues.get(Item.PARSE_URGENCY)), prevOwner.getUrgencyN()) //if current picker value is inherited from the 'active' owner (whether existing or recently edited)
//                        && MyUtil.neql(item.getUrgencyN(), newOwner.getUrgencyN()) //if new owner actually changes the value
//                        //                        ||                         item.isUrgencyInherited((HighMediumLow.valueOf((String)previousValues.get(Item.PARSE_URGENCY)))
//                        //                        ||                         item.isUrgencyInherited(prevOwner.getUrgencyN())
//                        ) {
//                    if (newOwner.getUrgencyN() == null) {
//                        previousValues.put(Item.PARSE_URGENCY, newOwner.getUrgencyN()); //if new value is null (where the old value was not-null), put null explicitly to remove previous value
//                    } else {
//                        previousValues.remove(Item.PARSE_URGENCY); //if new value is not null but equal to old one, then store nothing since no update is needed
//                    }
//                }
//            } else { //no value edited
//                if (item.isUrgencyInherited()) {
////                    previousValues.put(Item.PARSE_URGENCY, newOwner.getUrgencyN());
//                    if (MyUtil.neql(item.getUrgencyN(), newOwner.getUrgencyN())) {
//                        previousValues.put(Item.PARSE_URGENCY, newOwner.getUrgencyN()); //if new value is null (where the old value was not-null), put null explicitly to remove previous value
//                    } else {
//                        previousValues.remove(Item.PARSE_URGENCY); //if new value is not null but equal to old one, then store nothing since no update is needed
//                    }
//                }
//            }
//        }
//        if (item.isUrgencyInheritanceOn()) {
//            if (previousValues.get(Item.PARSE_URGENCY) != null) { //a value has been edited by user
//                if (MyUtil.eql(HighMediumLow.valueOf((String) previousValues.get(Item.PARSE_URGENCY)), prevOwner.getUrgencyN()) //if current picker value is inherited from the 'active' owner (whether existing or recently edited)
//                        && MyUtil.neql(item.getUrgencyN(), newOwner.getUrgencyN())) { //and if new owner actually changes the value
//                    if (newOwner.getUrgencyN() == null) {
//                        previousValues.put(Item.PARSE_URGENCY, newOwner.getUrgencyN()); //if new value is null (where the old value was not-null), put null explicitly to remove previous value
//                    } else {
//                        previousValues.remove(Item.PARSE_URGENCY); //if new value is not null but equal to old one, then store nothing since no update is needed
//                    }
//                }
//            } else { //no value edited
//                if (item.isUrgencyInherited()) {
//                    if (MyUtil.neql(item.getUrgencyN(), newOwner.getUrgencyN())) {
//                        previousValues.put(Item.PARSE_URGENCY, newOwner.getUrgencyN()); //if new value is null (where the old value was not-null), put null explicitly to remove previous value
//                    } else {
//                        previousValues.remove(Item.PARSE_URGENCY); //if new value is not null but equal to old one, then store nothing since no update is needed
//                    }
//                }
//            }
//        }
//</editor-fold>
    }

    private void addTemplateToPickers(Item item, Item template) {
        //if the user has not yet set a due date and the template has fields depending on due date, ask for a due date
        if (template.getDueDateD().getTime() != 0) {

            boolean templFieldsDependOnDue = template.getAlarmDate().getTime() != 0 || template.getStartByDateD().getTime() != 0
                    || template.getExpiresOnDate().getTime() != 0 || template.getHideUntilDateD().getTime() != 0;
            if (dueDate.getDate().getTime() == 0 && templFieldsDependOnDue) {
//                Dialog.show(SUBTASK_KEY, this, cmds);
                assert false;
            }
            String popup = "The template has fields that are set relative to due date but no due date is set for. If you set a due date now, the dependent fields will be updated, otherwise they will be ignored. Next time you use this template you can set a due date before inserting the template";
            //MOST FIELDS are only set if not defined (or if user deleted the value in the input field (Pickers etc)
            if (false && dueDate.getDate().getTime() == 0) { //NB. Due date NEVER set based on due date in template
                //TODO: make a setting for how far ahead a default due date should be set
                dueDate.setDate(template.getDueDateD());
            }
            long templDueDateAdj = dueDate.getDate().getTime() - template.getDueDateD().getTime(); //newDueDate-oldDueDate determine how much all depending fields should be moved ahead
            //CERTAIN DATES are set RELATIVE to the DUE DATE:
            if (true || templDueDateAdj != 0) { //NB. difference could be zero if same due date as 
                if (alarmDate.getDate() == null && template.getAlarmDate().getTime() != 0) {
                    alarmDate.setDate(new MyDate(template.getAlarmDate().getTime() + templDueDateAdj));
                }
                if (startByDate.getDate() == null && template.getStartByDateD().getTime() != 0) {
                    startByDate.setDate(new MyDate(template.getStartByDateD().getTime() + templDueDateAdj));
                }
                if (expireByDate.getDate() == null && template.getExpiresOnDate().getTime() != 0) {
                    expireByDate.setDate(new MyDate(template.getExpiresOnDate().getTime() + templDueDateAdj));
                }
                if (hideUntil.getDate() == null && template.getHideUntilDateD().getTime() != 0) {
                    hideUntil.setDate(new MyDate(template.getHideUntilDateD().getTime() + templDueDateAdj));
                }
            }
        }

        if (!isStarredSelected()) {
            setStarredSelected(template.isStarred());
        }

        if (priority.getSelectedIndex() == -1) {
            importance.selectIndex(template.getPriority() - 1);
        }

        if (importance.getSelected() == null) {
            importance.selectValue(template.getImportanceN()!=null?template.getImportanceN().name():null);
        }
        if (urgency.getSelected() == null) {
            urgency.selectValue(template.getUrgencyN().name()!=null?template.getUrgencyN().name():null);
        }
        if (challenge.getSelected() == null) {
            challenge.selectValue(template.getChallengeN()!=null?template.getChallengeN().name():null);
        }
        if (dreadFun.getSelected() == null) {
            dreadFun.selectValue(template.getDreadFunValueN()!=null?template.getDreadFunValueN().name():null);
        }

        if (getEarnedValueAsDouble(earnedValue.getText()) == 0) {
            earnedValue.setText(setEarnedValueAsString(template.getEarnedValue()));
        }

        if (!template.isProject() && (remainingEffort.getDuration() == 0 || Item.isRemainingDefaultValue(remainingEffort.getDuration()))) {
            remainingEffort.setDuration(template.getRemaining());
        }
        if (!template.isProject() && (effortEstimate.getDuration() == 0 || Item.isRemainingDefaultValue(effortEstimate.getDuration()))) {
            effortEstimate.setDuration(template.getEstimate());
        }
        //SOME FIELDS DO NOT HAVE A SIMPLE INPUT FIELD EDITOR
        //RepeatRule
        if (previousValues.get(Item.PARSE_REPEAT_RULE) == null && template.getRepeatRuleN() != null) {
            previousValues.put(Item.PARSE_REPEAT_RULE, new RepeatRuleParseObject(template.getRepeatRuleN()));
        }

        //SPECIAL CASES
        description.setText(description.getText().length() == 0 || MyPrefs.addTemplateTaskTextToExistingTaskText.getBoolean()
                ? description.getText() + template.getText() : template.getText()); //UI: add template's comment to the end(?!) of the comment, with a newline
        //Template Comments are ??
        comment.setText(comment.getText().length() > 0 ? comment.getText() + "\n" + template.getComment() : template.getComment()); //UI: add template's comment to the end(?!) of the comment, with a newline

        //Templates Categories are merged (only new categories in template are added - no cuplicates!)
//        Item.addCatObjectIdsListToCategoryList(((List<String>) previousValues.get(Item.PARSE_CATEGORIES)), template.getCategories()); //*add* any additional categories in the template
        List templateCategories = new ArrayList(template.getCategories());
        templateCategories.removeAll(previousValues.getCategories());
        templateCategories.addAll(previousValues.getCategories());
        previousValues.putCategories(templateCategories);
//                         
        //Template Subtasks are merged
        List<Item> subtasks = previousValues.getItemsN();
        List<ParseObject> newSubtasks = new ArrayList();
        for (Item tempSubtask : (List<Item>) template.getListFull()) { //full list, filter has no meaning for a template
            Item newSubtask = new Item(false);
            tempSubtask.copyMeInto(newSubtask, Item.CopyMode.COPY_FROM_TEMPLATE_TO_TASK, 0);
//            itemOrg.addToList(subtaskCopy); //UI: template subtasks are permanently (no Cancel possible) added to item
            subtasks.add(newSubtask); //UI: template subtasks are permanently (no Cancel possible) added to item
            newSubtask.setOwner(item);
            newSubtasks.add(newSubtask);//save each new templateCopy in a list and save them
        }
        DAO.getInstance().saveNew(newSubtasks); //save new subtasks (will be deleted again if Cancel
        DAO.getInstance().triggerParseUpdate(); //save new subtasks (will be deleted again if Cancel
        previousValues.putItems((List) subtasks);

        //SOME fields are NOT AFFECTED by a template:
        //Actual effort
        //Owner
        //CreatedOn
        //Wait until
        //Waiting reminder
        //Waiting since date
    }

    private void setStarredSelected(boolean selected) {
        boolean setStarActive = starred.getMaterialIcon() == Icons.iconStarUnselected; //icon is used to store state of starred changes in screen
        starred.setUIID(setStarActive ? "ScreenItemStarredActive" : "ScreenItemStarredNotActive");
        starred.setMaterialIcon(setStarActive ? Icons.iconStarSelected : Icons.iconStarUnselected);
    }

    private boolean isStarredSelected() {
        return starred.getMaterialIcon() == Icons.iconStarSelected; //
    }

    private String setEarnedValueAsString(double earnedVal) {
//        earnedValue.setText(L10NManager.getInstance().format(earnedVal, (earnedVal > 0) ? 2 : 0));
        return L10NManager.getInstance().format(earnedVal, (earnedVal > 0) ? 2 : 0);
    }

    private double getEarnedValueAsDouble(String s) {
        return L10NManager.getInstance().parseDouble(earnedValue.getText());
    }

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
        parseIdMap2.parseIdMapReset();
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
        cont.add(BorderLayout.CENTER, tabs);

        startedOnDate = new MyDateAndTimePicker();
//        MyTextArea description;
//        MyTextField description;
//        MyDurationPicker effortEstimate;
//        MyDurationPicker remainingEffort;

        //TAB MAIN
//        Container mainTabCont = new Container(new MyBorderLayout());
        Container mainCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        if (Config.TEST) {
            mainCont.setName("MainTab");
        }
        mainCont.setScrollableY(true);
//<editor-fold defaultstate="collapsed" desc="comment">
//        mainTabCont.add(MyBorderLayout.CENTER, mainCont);
//        if (false)
//        Container mainTabCont = BorderLayout.north(mainCont);
//        Container mainTabCont = BorderLayout.north(new Container(BoxLayout.y()));
//        if (false) {
//            mainCont.add(ScreenListOfItems.makeMyTree2ForSubTasks(ScreenItem.this, item, expandedObjects));
//        }
//        tabs.addTab("Main", Icons.iconMainTab, TAB_ICON_SIZE_IN_MM, mainTabCont);
//</editor-fold>
        tabs.addTab("Main", Icons.iconMainTab, TAB_ICON_SIZE_IN_MM, mainCont);
        //<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
        description = new MyTextField(Item.DESCRIPTION_HINT, 20, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
            @Override
            public void longPointerPress(int x, int y) {
                Log.p("longPointerPress on text area");
                //TODO!!! call templatePicker
            }
        };
        if (false) {
            description.putClientProperty("iosHideToolbar", Boolean.TRUE); //TRUE will hide the toolbar and only show Done button
        }
        boolean testMoveTextFieldsToOtherTab = false;

//        initField(Item.DESCRIPTION, Item.DESCRIPTION_HELP, description, Item.PARSE_TEXT, () -> item.getText(), (t) -> item.setText((String) t),
//                () -> description.getText(), (t) -> description.setText((String) t), null);
        initField(Item.PARSE_TEXT, description,
                () -> itemCopy.getText(),
                (t) -> itemOrg.setText((String) t),
                () -> description.getText(),
                (t) -> description.setText((String) t));

        //https://stackoverflow.com/questions/34531047/how-to-add-donelistener-to-textarea-in-codename-one: "putClientProperty("searchField", true);, putClientProperty("sendButton", true);and putClientProperty("goButton", true); would place a button on the keyboard"
        description.putClientProperty("goButton", true);
//        description.setUIID("ScreenItemTaskText");
        description.setUIID(itemCopy.isStarred() ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
        description.getHintLabel().setUIID("ScreenItemTaskTextHint");
        description.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
        if (description.getText().length() == 0) {
            setEditOnShow(description); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        }

        //need to declare already here to use in actionListener below
        effortEstimate = new MyDurationPicker();
//        initField(Item.EFFORT_ESTIMATE, Item.EFFORT_ESTIMATE_HELP, effortEstimate, Item.PARSE_EFFORT_ESTIMATE, () -> item.getEffortEstimate(), (l) -> item.setEstimate((long) l),
//                () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l), null);
        initField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate,
                () -> itemCopy.getEstimateForProjectTaskItself(),
                (l) -> itemOrg.setEstimate((long) l, false),
                () -> effortEstimate.getDuration(),
                (l) -> effortEstimate.setDuration((long) l));

//get the effort for the project task itself:
        remainingEffort = new MyDurationPicker();
//        initField(Item.EFFORT_REMAINING, Item.EFFORT_REMAINING_HELP, effortEstimate, Item.PARSE_REMAINING_EFFORT, () -> item.getRemainingEffort(), (l) -> item.setRemaining((long) l),
//                () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l), null);
        initField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
                //                () -> item.getRemaining(false), 
                () -> itemCopy.getRemainingForProjectTaskItselfFromParse(),
                (l) -> itemOrg.setRemaining((long) l, false),
                () -> remainingEffort.getDuration(),
                (l) -> remainingEffort.setDuration((long) l));

        //DESCRIPTION
        description.addActionListener((e) -> {
//            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
            Item.EstimateResult res = Item.getEffortEstimateFromTaskText(description.getText(), false);
            //TODO!!! create a function that will determine when to any of the user setting baesd on the values in description string
            if (res.minutes != 0) { //UI: alwyas use value in text to override previous value
                //TODO!!!!! call the same actionListener as when EsitmatePicker is changed 
                //UI: entering an estimate in the text of an item is used to set remaining effort (and not effort estimate) since this is more useful, e.g. as an easy way to update remaining while editing the item
//                remainingEffort.setTime(res.minutes); //will set remainingEffort, even if text is changed multiple times. However, manually changing effortEstimate later on won't change remainingEffort. 
//                remainingEffort.setDuration(res.minutes * MyDate.MINUTE_IN_MILLISECONDS); //will set remainingEffort, even if text is changed multiple times. However, manually changing effortEstimate later on won't change remainingEffort. 
                remainingEffort.setDurationAndNotify(res.minutes * MyDate.MINUTE_IN_MILLISECONDS); //will set remainingEffort, even if text is changed multiple times. However, manually changing effortEstimate later on won't change remainingEffort. 
                remainingEffort.repaint();
                description.setText(res.cleaned); //update text after estimate is removed 
                description.repaint();
            }
//            setTitle(getScreenTitle(item.isTemplate(), description.getText()));
            setTitle((itemOrg.isTemplate() ? "TEMPLATE: " : "") + description.getText());
        }); //update the form title when text is changed

        mainCont.add(makeSpacerThin());

        mainCont.add(description);
//        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(this, description, item, 5000, () -> item.setText(description.getText())); //normal that this appear as non-used!
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(this, description, null, null); //normal that this appear as non-used!

        mainCont.add(makeSpacerThin());
        //COMMENT / NOTES
//        MyTextField comment = new MyTextField(Item.COMMENT_HINT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment = new MyTextField(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
        comment.setSingleLineTextArea(false);
        comment.setUIID("ScreenItemComment");
        comment.getHintLabel().setUIID("ScreenItemCommentHint");
        Container commentField = makeCommentContainer(comment);
//        AutoSaveTimer commentSaveTimer = new AutoSaveTimer(this, comment, item, 5000, () -> item.setComment(comment.getText())); //NORMAL that appear as non-used since running in background!!
        AutoSaveTimer commentSaveTimer = new AutoSaveTimer(this, comment, null, null); //NORMAL that appear as non-used since running in background!!

//        mainCont.add(initField(Item.COMMENT, Item.COMMENT_HELP, comment, Item.PARSE_COMMENT, () -> item.getComment(), (t) -> item.setComment((String) t),
        initField(Item.PARSE_COMMENT, comment, () -> itemCopy.getComment(), (t) -> itemOrg.setComment((String) t), () -> comment.getText(), (t) -> comment.setText((String) t));
////<editor-fold defaultstate="collapsed" desc="comment">
//        comment.putClientProperty("goButton", true);
//        comment.setUIID("Comment");
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

        mainCont.add(makeSpacerThin());

        boolean hide = MyPrefs.hideIconsInEditTaskScreen.getBoolean();

        //STATUS
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyCheckBox status = new MyCheckBox(itemLS.getStatus(), (oldStatus, newStatus) -> {        }); //, null);
//        initField(Item.STATUS, Item.STATUS_HELP, status, Item.PARSE_STATUS, () -> item.getStatus(), (t) -> item.setStatus((ItemStatus) t),
//                () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t), null);
//</editor-fold>
        status = new MyCheckBox(itemCopy.getStatus()); //, null);
        initField(Item.PARSE_STATUS, status,
                () -> itemCopy.getStatus().toString(),
                (enumStr) -> itemOrg.setStatus((ItemStatus.valueOf((String) enumStr)), false), //item.setStatus((ItemStatus) t, false),
                () -> status.getStatus().toString(), //status.getStatus(), 
                (enumStr) -> status.setStatus(ItemStatus.valueOf((String) enumStr)));
//        mainCont.add(layoutN(Item.STATUS, status, Item.STATUS_HELP, null, false, false, false, true));
        mainCont.add(layoutN(Item.STATUS, status, Item.STATUS_HELP, null, false, false, false, true, false, hide ? null : Icons.iconItemStatusCreated));

//<editor-fold defaultstate="collapsed" desc="comment">
//        parseIdMap2.put(status, () -> {
//            if (!item.getStatus().equals(status.getStatus())) {
//                item.setStatus(status.getStatus());
//            }
//        });
//        Container taskCont = new Container(new BoxLayout(BoxLayout.X_AXIS));
//</editor-fold>
//        if (false)Container taskCont = new Container(new MyBorderLayout());
        //STARRED
//        CheckBox starredcb = new CheckBox(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        Button starred = new Button(itemLS.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        Button starred = new RadioButton(); //TODO change to use RadionButton and automatically switch icon when selected/unselected --> RB no good
        starred = new Button("", ""); //TODO change to use RadionButton and automatically switch icon when selected/unselected
//        starred.setUIID("ScreenItemStarred");
        ActionListener flipStarredIconsAndUIID = (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//on very first init, use value from itemCopy
//            if (starred.getUIID().equals("")) {
//                starred.setUIID(itemCopy.isStarred() ? "ScreenItemStarred" : "ScreenItemStarredNotActive");
//                starred.setMaterialIcon(itemCopy.isStarred() ? Icons.iconStarSelected : Icons.iconStarUnselected);
//            } else {
//</editor-fold>
            //if not already active, then set active
//            boolean setStarActive = starred.getMaterialIcon() == Icons.iconStarUnselected; //icon is used to store state of starred changes in screen
//            starred.setUIID(setStarActive ? "ScreenItemStarredActive" : "ScreenItemStarredNotActive");
//            starred.setMaterialIcon(setStarActive ? Icons.iconStarSelected : Icons.iconStarUnselected);
            setStarredSelected(!isStarredSelected());

//            description.setUIID(setStarActive ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
            description.setUIID(isStarredSelected() ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
            description.repaint();
//            }
//            updateUIIDForInherited(starred, itemCopy.isStarInheritedFrom(starred.getMaterialIcon() == Icons.iconStarSelected));
            updateUIIDForInherited(starred, itemCopy.isStarInheritedFrom(isStarredSelected()));
        };
//<editor-fold defaultstate="collapsed" desc="comment">
//        starred.setUIID(itemCopy.isStarInheritedFrom(starred.getMaterialIcon() == Icons.iconStarUnselected) ? "ScreenItemStarredInherited" : "ScreenItemStarred");
//        starred.addActionListener((e) -> starred?setIcon(starred.getIcon() == Icons.iconStarUnselectedLabelStyle
//                ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle));
//        starred.addActionListener((e) -> {
//            boolean setStarActive = starred.getMaterialIcon() == Icons.iconStarUnselected;
//            starred.setMaterialIcon(setStarActive ? Icons.iconStarSelected : Icons.iconStarUnselected);
//            description.setUIID(setStarActive ? "ScreenItemTaskTextStarred" : "ScreenItemTaskText");
//            description.repaint();
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (item.isStarInheritedFrom(starActive))
////                starred.setUIID(starActive ? "ScreenItemStarredActive" : "ScreenItemStarred");
////            else
////                starred.setUIID(starActive ? "ScreenItemStarredActive" : "ScreenItemStarred");
////</editor-fold>
//            starred.setUIID(itemCopy.isStarInheritedFrom(setStarActive) ? "ScreenItemStarredInherited" : "ScreenItemStarred");
//        });
//</editor-fold>
        starred.addActionListener(flipStarredIconsAndUIID);
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
                () -> itemCopy.isStarred(),
                (b) -> itemOrg.setStarred((boolean) b),
                //                () -> starred.getIcon().equals(Icons.iconStarSelectedLabelStyle),
                //                () -> starred.getIcon().equals(Icons.iconStarSelectedLabelStyle),
                //                (b) -> starred.setIcon((boolean) b ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle),
                () -> isStarredSelected(), //starred.getMaterialIcon() == Icons.iconStarSelected,
                (b) -> setStarredSelected((boolean) b), //starred.setMaterialIcon((boolean) b ? Icons.iconStarSelected : Icons.iconStarUnselected),
                () -> itemCopy.isStarInheritedFrom(isStarredSelected()) //starred.getMaterialIcon() == Icons.iconStarSelected)
        //                (b) -> starred.setIcon((boolean) b ?  Icons.iconStarUnselectedLabelStyle:Icons.iconStarSelectedLabelStyle )
        //                    starred.repaint();
        ); //add taskCont just to avoid creating an unnecessary field container
//        updateStarredUIID.actionPerformed(null); //update starred icon
//        if (false)taskCont.add(MyBorderLayout.CENTER, description);
        mainCont.add(layoutN(Item.STARRED, starred, Item.STARRED_HELP, null, false, false, false, true, true, Icons.iconStarLabel));
        starred.setUIID(itemCopy.isStarred() ? "ScreenItemStarredActive" : "ScreenItemStarredNotActive");
//        updateUIIDForInherited(starred, itemCopy.isStarInheritedFrom(starred.getMaterialIcon() == Icons.iconStarSelected));
        updateUIIDForInherited(starred, itemCopy.isStarInheritedFrom(isStarredSelected()));
//<editor-fold defaultstate="collapsed" desc="comment">
//        taskCont.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, starred);
//        taskCont.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, starred);
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
//        if (false) {
//            mainCont.add(makeHelpButton(Item.TASK, Item.TASK_HELP));
//        }
//        mainCont.add(taskCont);
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyTextField comment = new MyTextField("Details", "Comments", 20, TextArea.ANY, parseIdMap, item, Item.PARSE_COMMENT);
//        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
//                () -> itemLS.getComment(), (s) -> item.setComment(s));
//        MyTextArea comment = new MyTextArea(Item.COMMENT_HINT, 20, 1, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        mainCont.add(comment);

//        MyDateAndTimePicker dueDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getDueDateD(), (d) -> {
//            item.setDueDate(d);
//            //TODO!!? dialog to ask if repeatRule startDate should be updated to match due date? But not meaningful if it's just a repeatInstance with its own duedate
////            if (locallyEditedRepeatRule!=null) {
////
////            }
//        }); //"<click to set a due date>"
//</editor-fold>
        mainCont.add(makeSpacerThin());

        dueDate = new MyDateAndTimePicker(() -> makeDefaultDueDate());

        //<editor-fold defaultstate="collapsed" desc="comment">
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap, item, Item.PARSE_ALARM_DATE);
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<click to set an alarm>", parseIdMap2, () -> item.getAlarmDateD(), (d) -> item.setAlarmDate(d));
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("<set>", parseIdMap2,
//        MyDateAndTimePicker alarmDate = new MyDateAndTimePicker("", parseIdMap2,
//                () -> itemLS.getAlarmDateD(),
//                (d) -> item.setAlarmDate(d));
//</editor-fold>
        alarmDate = new MyDateAndTimePicker(() -> makeDefaultAlarmDate(dueDate.getDate()));
//<editor-fold defaultstate="collapsed" desc="comment">
//        mainCont.add(new Label(Item.ALARM_DATE)).add(addDatePickerWithClearButton(alarmDate));
//        mainCont.add(new Label(Item.ALARM_DATE)).add(alarmDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.ALARM_DATE, new SwipeClearContainer(BoxLayout.encloseXNoGrow(alarmDate), ()->alarmDate.setDate(new Date(0))), Item.ALARM_DATE_HELP));
//        if (false)mainCont.add(layout(Item.ALARM_DATE, alarmDate.makeContainerWithClearButton(), Item.ALARM_DATE_HELP));
//</editor-fold>
//        mainCont.add(layout(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)), true));
        initField(Item.PARSE_ALARM_DATE,
                alarmDate,
                () -> itemCopy.getAlarmDate(),
                (d) -> itemOrg.setAlarmDate((Date) d),
                () -> alarmDate.getDate(),
                (d) -> alarmDate.setDate((Date) d));
//                new Date(0),
//                ()->makeDefaultAlarmDate(dueDate.getDate()));
//        mainCont.add(layoutN(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, () -> alarmDate.setDate(new Date(0)))); //, true, false, false));
//        mainCont.add(layoutN(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP)); //, true, false, false));
        mainCont.add(layoutN(Item.ALARM_DATE, alarmDate, Item.ALARM_DATE_HELP, hide ? null : Icons.iconAlarmDate)); //, true, false, false));
//        int remainingIndex = mainCont.getComponentCount() - 1; //store the index at which to insert remainingEffort

//        mainCont.add(makeSpacerThin());
        //        MyDateAndTimePicker startByDate = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getStartByDateD(), (d) -> item.setStartByDate(d)); // "<start task on this date>", 
        startByDate = new MyDateAndTimePicker(); // "<start task on this date>", 
        initField(Item.PARSE_START_BY_DATE, startByDate,
                () -> itemCopy.getStartByDateD(),
                (t) -> itemOrg.setStartByDate((Date) t),
                () -> startByDate.getDate(),
                (d) -> startByDate.setDate((Date) d),
                () -> itemCopy.isStartByDateInherited(startByDate.getDate()));
        mainCont.add(layoutN(Item.START_BY_TIME, startByDate, Item.START_BY_TIME_HELP, hide ? null : Icons.iconStartByDate));
        updateUIIDForInherited(startByDate, itemCopy.isStartByDateInherited(startByDate.getDate()));

//        timeCont.add(new Label(Item.START_BY_TIME)).add(addDatePickerWithClearButton(startByDate));
//        timeCont.add(new Label(Item.START_BY_TIME)).add(startByDate.makeContainerWithClearButton());
//        timeCont.add(layout(Item.START_BY_TIME, startByDate.makeContainerWithClearButton(), "**"));
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(new Label("Due")).add(dueDate);
//        mainCont.add(new Label("Due")).add(LayeredLayout.encloseIn(dueDate, FlowLayout.encloseRightMiddle(new Button(Command.create(null,Icons.iconCloseCircle,(e)->{dueDate.setDate(new Date(0));})))));
//        mainCont.add(new Label(Item.DUE_DATE)).add(addDatePickerWithClearButton(dueDate));
//        mainCont.add(new Label(Item.DUE_DATE)).add(dueDate.makeContainerWithClearButton());
//        mainCont.add(layout(Item.DUE_DATE, dueDate.makeContainerWithClearButton(), "**"));
//        mainCont.add(initField(Item.DUE_DATE, Item.DUE_DATE_HELP, dueDate, Item.PARSE_DUE_DATE, () -> item.getDueDateD(), (t) -> item.setDueDate((Date) t),
//</editor-fold>
        initField(Item.PARSE_DUE_DATE, dueDate,
                () -> itemCopy.getDueDateD(),
                //<editor-fold defaultstate="collapsed" desc="comment">
                //                () -> {
                //                    if (dueDateEditedInRepeatRuleScreen != null) {
                //                        Date temp = dueDateEditedInRepeatRuleScreen;
                //                        dueDateEditedInRepeatRuleScreen = null;
                //                        return temp;
                //                    } else return item.getDueDateD();
                //                },
                //</editor-fold>
                (d) -> itemOrg.setDueDate((Date) d),
                () -> dueDate.getDate(),
                (d) -> dueDate.setDate((Date) d),
                () -> itemCopy.isDueDateInherited(dueDate.getDate()));
//                new Date(0),
//                ()->makeDefaultDueDate());
        mainCont.add(layoutN(Item.DUE_DATE, dueDate, Item.DUE_DATE_HELP, hide ? null : Icons.iconSetDueDateToTodayFontImageMaterial));
        updateUIIDForInherited(dueDate, itemCopy.isDueDateInherited(dueDate.getDate())); //NB! MUST do *after* layoutN() which sets the UIID

//        hi.add(LayeredLayout.encloseIn(settingsLabel, FlowLayout.encloseRight(close))) //https://github.com/codenameone/CodenameOne/wiki/Basics---Themes,-Styles,-Components-&-Layouts#layered-layout
        //FINISH_TIME
//        WorkTimeSlices workTime = item.getAllocatedWorkTimeN();
        if (!itemOrg.isDone() || Config.WORKTIME_TEST) { //TEST: show the artificial/helper workSlice allocated to done tasks
//            WorkTimeSlices workTime = item.getAllocatedWorkTimeN();
            Date finishTime = itemOrg.getFinishTimeD();
            if (finishTime.getTime() != MyDate.MAX_DATE) {//|| Config.WORKTIME_TEST) {
                Button showWorkTimeDetails = new Button(MyReplayCommand.create("ShowWorkTimeDetails", MyDate.formatDateTimeNew(finishTime), null, (e) -> {
//                    new ScreenListOfWorkTime(item.getText(), item.getAllocatedWorkTimeN(), ScreenItem.this).show();
//                    new ScreenListOfWorkTime(item.getText(), item.getAllocatedWorkTimeN(), ScreenItem2.this).show();
                    new ScreenListOfWorkTime(itemOrg, itemOrg.getAllocatedWorkTimeN(), ScreenItem2.this).show();
                }));
//                mainCont.add(layoutN(Item.FINISH_WORK_TIME, showWorkTimeDetails, Item.FINISH_WORK_TIME_HELP, null, true, true, true));
                mainCont.add(layoutN(Item.FINISH_WORK_TIME, showWorkTimeDetails, Item.FINISH_WORK_TIME_HELP, null, true, true, true, hide ? null : Icons.iconFinishDateMaterial));

//                mainCont.add(initField(Item.FINISH_WORK_TIME, Item.FINISH_WORK_TIME_HELP, showWorkTimeDetails, "finishTime", () -> item.getFinishTime(), null,
                initField("finishTime", showWorkTimeDetails,
                        () -> itemOrg.getFinishTimeD(), null,
                        //                        () -> dueDate.getDate(), (d) -> dueDate.setDate((Date) d)); //WTF??
                        null, null);
            }
        }
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
        mainCont.add(makeSpacerThin());

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
//        WrapButton categoriesButton = new WrapButton(previousValues.get(Item.PARSE_CATEGORIES) != null
//                ? getCategoriesAsCommaSeparatedString(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)))
//                : getCategoriesAsCommaSeparatedString(item.getCategories()));
//        WrapButton categoriesButton = new WrapButton(getCategoriesAsCommaSeparatedString((List<Category> )previousValues.get(Item.PARSE_CATEGORIES,item.getCategories())));
//</editor-fold>
        WrapButton categoriesButton = new WrapButton();
        ActionListener refreshCategoriesButton = (e) -> {
            String commaSeparatedCategories = getCategoriesAsCommaSeparatedString(previousValues.get(Item.PARSE_CATEGORIES) != null
                    //                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
                    ? DAO.getInstance().convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
                    : itemOrg.getCategories());
            categoriesButton.setText(commaSeparatedCategories);
//            Container parentToRefresh=categoriesButton.getParent();
//            while(parentToRefresh!=null && !(parentToRefresh instanceof ))
//            categoriesButton.getParent().getParent().getParent().revalidate();
//            mainCont.revalidate();
            categoriesButton.revalidate();
            tabs.revalidate();
        };
        //categoriesButton.getTextComponent().setRTL(true);
//<editor-fold defaultstate="collapsed" desc="comment">
//        WrapButton editOwnerButton = new WrapButton(getListAsCommaSeparatedString(editedCats));

//        Command categoryEditCmd = new MyReplayCommand("PickCategories", "") { //"<click to set categories>"
//        if (false) {
//            String commaSeparatedCategories = getCategoriesAsCommaSeparatedString(previousValues.get(Item.PARSE_CATEGORIES) != null
//                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
//                    : item.getCategories());
//        }
//        Command categoryEditCmd = new Command(getCategoriesAsCommaSeparatedString((List<String>) previousValues.get(Item.PARSE_CATEGORIES, item.getCategories()))) { //"<click to set categories>"
//        Command categoryEditCmd = new Command(commaSeparatedCategories) { //"<click to set categories>"
//        Command categoryEditCmd = new Command("") { //"<click to set categories>"
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//</editor-fold>
        Command categoryEditCmd = MyReplayCommand.create("EditCategories", null, null, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(), locallyEditedCategories, ScreenItem2.this);
//                if (previousValues.get(Item.PARSE_CATEGORIES) != null) {
//</editor-fold>
            List<Category> catList = (previousValues.get(Item.PARSE_CATEGORIES) != null
                    //                    ? Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)) //if previous edited value exists, use that
                    ? DAO.getInstance().convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)) //if previous edited value exists, use that
                    : new ArrayList(itemOrg.getCategories())); //make a copy to be able to compare the edited version to the orginal list from Item after editing
            ScreenCategoryPicker screenCatPicker = new ScreenCategoryPicker(CategoryList.getInstance(),
                    catList,
                    ScreenItem2.this, () -> {
//                    categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "")); //"<click to set categories>"
                        if (catList.equals(itemOrg.getCategories())) {
                            previousValues.remove(Item.PARSE_CATEGORIES); //remove previos/edited value (nothing to store)
//                            if (false) {
//                                categoriesButton.setText(getCategoriesAsCommaSeparatedString(itemOrg.getCategories())); //"<click to set categories>"
//                            }
                        } else {
//                            previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(catList));
                            previousValues.put(Item.PARSE_CATEGORIES, ItemAndListCommonInterface.convListToObjectIdList((List) catList));
//                            if (false) {
//                                categoriesButton.setText(getCategoriesAsCommaSeparatedString(catList)); //Button.text now updated based on previousValues! "<click to set categories>"
//                            }
                        }
//                            categoriesButton.revalidate(); //layout new list of categories, working??
//                        if (false) {
//                            categoriesButton.getComponentForm().revalidate(); //layout new list of categories, working??
//                        }
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
//        categoriesButton.revalidate();
        parseIdMap2.put(Item.PARSE_CATEGORIES, () -> {
            if (previousValues.get(Item.PARSE_CATEGORIES) != null) {
//                item.setCategories(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)));
//                List modifiedCats = itemOrg.updateCategories(Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)));
                List modifiedCats = itemOrg.updateCategories(DAO.getInstance().convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES)));
                DAO.getInstance().saveNew(modifiedCats);
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
        mainCont.add(layoutN(true, Item.CATEGORIES, categoriesButton, Item.CATEGORIES, hide ? null : Icons.iconCategory));

//        mainCont.add(makeSpacerThin());
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
        int numberUndoneSubtasks = itemOrg.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
        int totalNumberSubtasks = itemOrg.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
//        int numberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks;

        if (true) { //experimental
            Button editSubtasks = new Button();
            initField(Item.PARSE_SUBTASKS, editSubtasks,
                    () -> itemCopy.getListFull(),
                    (subtasks) -> itemOrg.setList((List) subtasks),
                    () -> previousValues.getItemsN(), //editSubtasks.getDate(), //return what to .equal() with original value => list of subtasks (new/deleted/added)
                    //                    (subtasks) -> previousValues.putItems((List<ItemAndListCommonInterface>) subtasks) //return value to put directly in previousValues => list of suttask objIds
                    (subtasks) -> previousValues.putItems((List) subtasks)//put(Item.PARSE_SUBTASKS, subtasks) //return value to put directly in previousValues => list of suttask objIds
            ); //() -> previousValues.getItemsN());

            String subtaskStr = totalNumberSubtasks == 0 ? "" : "" + numberUndoneSubtasks + "/" + totalNumberSubtasks;
            editSubtasks.setCommand(MyReplayCommand.create("EditSubtasks", subtaskStr, null, (e) -> {
                ItemList tempSubtasks = new ItemList(previousValues.getItemsN());
                new ScreenListOfItems(description.getText() + " subtasks", () -> tempSubtasks, ScreenItem2.this, (item2) -> {
                    int numberUndoneSubtasks2 = item2.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
                    int totalNumberSubtasks2 = item2.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
                    editSubtasks.setText(totalNumberSubtasks2 == 0 ? "" : "" + numberUndoneSubtasks2 + "/" + totalNumberSubtasks2);
                    previousValues.putItems(tempSubtasks);
                }, ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                ).show();
            }
            ));
            mainCont.add(layoutN(Item.SUBTASKS, editSubtasks, Item.SUBTASKS_HELP, false, hide ? null : Icons.iconSubTasks));

            parseIdMap2.put(SUBTASK_KEY, () -> {
                itemOrg.setList(previousValues.getItemsN());
            });
        } else {

            //HEADER - EDIT LIST IN FULL SCREEN MODE
            Button editSubtasksFullScreen = new Button();
//        String subtaskStr = (totalNumberSubtasks == 0
//                ? "Add subtasks" : ("" + totalNumberSubtasks + " subtasks" + (numberUndoneSubtasks == 0 ? "" : (", " + numberUndoneSubtasks + " remaining"))));
            String subtaskStr = totalNumberSubtasks == 0 ? "" : "" + numberUndoneSubtasks + "/" + totalNumberSubtasks;
            editSubtasksFullScreen.setCommand(MyReplayCommand.create("EditSubtasks", subtaskStr, null, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            ItemList subtaskList = item.getItemList();
//            List<Item> subtaskList = item.getListFull();
//            new ScreenListOfItems("Subtasks of " + item.getText(), () -> new ItemList(item.getListFull(),true), previousForm, (iList) -> {
//</editor-fold>
//            new ScreenListOfItems("Subtasks of " + item.getText(), () -> item, ScreenItem2.this, (item) -> {
                new ScreenListOfItems(description.getText() + " subtasks", () -> itemOrg, ScreenItem2.this, (item2) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                item.setItemList(subtaskList);
//                item.setList(subtaskList);
//                item.setList(iList.getListFull());
//                if (false)
//                    item.setList((iList); //probably not necessary since all operations on the list (insert, D&D, ...) should update the list on each change
//                DAO.getInstance().saveInBackground(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//</editor-fold>
                    int numberUndoneSubtasks2 = item2.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
                    int totalNumberSubtasks2 = item2.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project

                    editSubtasksFullScreen.setText(totalNumberSubtasks2 == 0 ? "" : "" + numberUndoneSubtasks2 + "/" + totalNumberSubtasks2);
//                parseIdMap2.put(SUBTASK_KEY, () -> DAO.getInstance().saveTemplateCopyWithSubtasksInBackground((Item) item));
//                parseIdMap2.put(SUBTASK_KEY, () -> DAO.getInstance().saveProjectInBackground((Item) item));
                    if (false) { //no need to do anything here, subtask list is updated and project saved in ScreenListOfItems
                        parseIdMap2.put(SUBTASK_KEY, () -> {
                            if (false && itemOrg.getObjectIdP() != null) { //only save it owner project is already saved, otherwise will be saved once project is saved first time
//                            DAO.getInstance().saveNew((Item) item2, true);
                                DAO.getInstance().saveNew((Item) item2);
                                DAO.getInstance().saveNewExecuteUpdate();
                            } else {
                                DAO.getInstance().saveNew(item2.getListFull()); //attempt to save all subtasks, only new or dirty will be processes
                            }
                        });
                    }
                    if (false) {
                        previousForm.refreshAfterEdit(); //necessary to update sum of subtask effort
                    }
                }, ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME
                ).show();
            }
            ));
//        mainCont.add(layoutN(Item.SUBTASKS, editSubtasksFullScreen, Item.SUBTASKS_HELP));
            mainCont.add(layoutN(Item.SUBTASKS, editSubtasksFullScreen, Item.SUBTASKS_HELP, false, hide ? null : Icons.iconSubTasks));
        }
//        mainCont.add(makeSpacerThin());
        //REPEAT RULE
        WrapButton repeatRuleButton = new WrapButton();
        //set text for edit-RR button
        ActionListener refreshRepeatRuleButtonText = e -> {
            RepeatRuleParseObject editedRepeatRule = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);
            String repeatRuleButtonStr;
            if (editedRepeatRule == null) { //no ongoing/previous (locally stored) edits
                if (itemOrg.getRepeatRuleN() != null) {
                    repeatRuleButtonStr = itemOrg.getRepeatRuleN().getText();
                } else {
                    repeatRuleButtonStr = "";
                }
            } else if (editedRepeatRule.equals(REPEAT_RULE_DELETED_MARKER)) {
                repeatRuleButtonStr = "";
            } else { //stored a locally edited RR
                assert editedRepeatRule instanceof RepeatRuleParseObject;
                repeatRuleButtonStr = editedRepeatRule.getText();
            }
            repeatRuleButton.setText(repeatRuleButtonStr);
        };

        //Cmd for editing RR
        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRule-ScreenEditItem", "", null, (e) -> {
            //check if OK to edit RR, return if not
//            if (item.getRepeatRule() != null && !locallyEditedRepeatRuleCopy.isRepeatInstanceInListOfActiveInstances(item)) {
            if (itemOrg.getRepeatRuleN() != null && !itemOrg.getRepeatRuleN().canRepeatRuleBeEdited(itemOrg)) {
                Dialog.show("INFO", Format.f("Once a repeating {0 task or workslot} has been set {1 DONE} or {2 CANCELLED} the {3 REPEAT_RULE} definition cannot be edited from this task anymore",
                        Item.TASK, ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), Item.REPEAT_RULE), "OK", null);
                return;
            }

            RepeatRuleParseObject locallyEditedRepeatRuleCopy; //NB - must set locallyEditedRepeatRule like it's done to allow use in lambda fct below
            RepeatRuleParseObject localRR = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);

            if (localRR != null) {
                locallyEditedRepeatRuleCopy = localRR;
            } else {
                if (itemOrg.getRepeatRuleN() == null) {
                    locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(); //create a fresh RR
//                    locallyEditedRepeatRuleCopy.addOriginatorToRule(item); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                } else {
                    locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(itemOrg.getRepeatRuleN()); //create a copy if getRepeatRule returns a rule, if getRepeatRule() returns null, creates a fresh RR
                }
                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy); //save new value locally
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//            new ScreenRepeatRule(Item.REPEAT_RULE, locallyEditedRepeatRuleCopy, itemOrg, ScreenItem2.this, () -> {
//                //if a startDate was set in the RR, and none is set for the item, use RR startDate as due date. *unless* the dueDate was reset to 0 (hence the test on item.getDueDateD())
//                if (dueDate.getDate().getTime() == 0 && locallyEditedRepeatRuleCopy.getSpecifiedStartDateXXXZZZ().getTime() != 0) { //NO, always use repeatRule startDate as dueDate and vice-versa (necessary when editing a rule with existing instances)
//                    previousValues.put(Item.PARSE_DUE_DATE, new MyDate(locallyEditedRepeatRuleCopy.getSpecifiedStartDateXXXZZZ().getTime())); //replace/set locally edited value for Due so when ScreenItem2 is refreshed this value is used to set the picker
//                }
//                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy); //store edited rule (otherwise not persisted in local memory)
//            }, true, dueDate.getDate(), () -> makeDefaultDueDate(), false).show(); //TODO false<=>editing startdate not allowed - correct???
//</editor-fold>
            new ScreenRepeatRule(Item.REPEAT_RULE, locallyEditedRepeatRuleCopy, itemOrg, ScreenItem2.this, () -> {
                //if a startDate was set in the RR, and none is set for the item, use RR startDate as due date. *unless* the dueDate was reset to 0 (hence the test on item.getDueDateD())
                if (false && dueDate.getDate().getTime() == 0 && locallyEditedRepeatRuleCopy.getSpecifiedStartDateXXXZZZ().getTime() != 0) { //NO, always use repeatRule startDate as dueDate and vice-versa (necessary when editing a rule with existing instances)
                    previousValues.put(Item.PARSE_DUE_DATE, new MyDate(locallyEditedRepeatRuleCopy.getSpecifiedStartDateXXXZZZ().getTime())); //replace/set locally edited value for Due so when ScreenItem2 is refreshed this value is used to set the picker
                }
                //if no due date is already set, automatically set the duePicker to the first repeatdate generated
                if (dueDate.getDate().getTime() == 0) {
                    previousValues.put(Item.PARSE_DUE_DATE, locallyEditedRepeatRuleCopy.getFirstRepeatDateAfterTodayForWhenEditingRuleWithoutPredefinedDueDateN()); //replace/set locally edited value for Due so when ScreenItem2 is refreshed this value is used to set the picker
                }
                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy); //store edited rule (otherwise not persisted in local memory)
            }, true, dueDate.getDate().getTime() != 0 ? dueDate.getDate() : null, () -> makeDefaultDueDate(), false).show(); //TODO false<=>editing startdate not allowed - correct???
        }
        );
        repeatRuleButton.setCommand(repeatRuleEditCmd);
        refreshRepeatRuleButtonText.actionPerformed(null);

        /*
Meaning of previousValues.get(Item.PARSE_REPEAT_RULE):
-> undefined/null: no change of org. RR (or no RR originally)
-> RR: modified RR (*only* defined if the RR has been edited!)
-> "DELETED": the RR has been deleted
         */
//        parseIdMap2.put(Item.PARSE_REPEAT_RULE, () -> {
        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
            Object editedRule = previousValues.get(Item.PARSE_REPEAT_RULE);
            if (editedRule instanceof RepeatRuleParseObject) { //only defined if the RR has really been edited
                if (itemOrg.getRepeatRuleN() == null) { //if new rule (no previous rule exists), then add item to lists (done *before* calculation of repeats)
                    ((RepeatRuleParseObject) editedRule).addOriginatorToRule(itemOrg); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                }
                itemOrg.setRepeatRule((RepeatRuleParseObject) editedRule);
            }
        });

//        Component repeatRuleContainer = layoutN(true, Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP);
        Component repeatRuleContainer = layoutN(true, Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP, hide ? null : Icons.iconRepeat);
//        if (false && (editedRepeatRule == null || editedRepeatRule.getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT)) {
//            repeatRuleContainer.setHidden(true); //hide as long as no due date is set (like Apple Reminders)
//        }
        mainCont.add(repeatRuleContainer);

        dueDate.addActionListener(e -> {
            if (false) { //de-activate for now since repeatOnCompletion need this
                boolean hideDue = dueDate.getDate().getTime() == 0;
                repeatRuleContainer.setHidden(hideDue);
                animateMyForm();
            }
        });

//        if (false) mainCont.add(new SubtaskContainerSimple(item, ScreenItem2.this, templateEditMode, parseIdMap2)); //edit subtasks
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
        if (Config.TEST) {
            timeCont.setName("TimeTab");
        }
        timeCont.setScrollableY(true);

        tabs.addTab("Time", Icons.iconTimeTab, TAB_ICON_SIZE_IN_MM, timeCont);

        if (testMoveTextFieldsToOtherTab) {
            timeCont.add(layoutN(Item.CATEGORIES, categoriesButton, Item.CATEGORIES));
            timeCont.add(layoutN(Item.REPEAT_RULE, repeatRuleButton, Item.REPEAT_RULE_HELP));
        }

        boolean isProject = itemOrg.isProject();

        //REMAINING************
        if (isProject) {
//            mainCont.addComponent(remainingIndex, layoutN(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getRemainingEffort()), "LabelFixed"),
            timeCont.addComponent(layoutN(Item.EFFORT_REMAINING_SUBTASKS, new Label(MyDate.formatDurationStd(itemOrg.getRemainingForSubtasks()), "ScreenItemValueUneditable"),
                    Item.EFFORT_REMAINING_SUBTASKS_HELP, true, true, false, hide ? null : Icons.iconRemainingEffort)); //hack to insert after alarmDate field
        }
//TODO: makes no sense to show remaining for project itself, just confusing??
        String remainingTxt = isProject ? Item.EFFORT_REMAINING_PROJECT : Item.EFFORT_REMAINING;
        String remainingHelpTxt = isProject ? Item.EFFORT_REMAINING_PROJECT_HELP : Item.EFFORT_REMAINING_HELP;
        timeCont.add(layoutN(remainingTxt, remainingEffort, remainingHelpTxt, hide ? null : (isProject ? Icons.iconEffortProject : Icons.iconRemainingEffort)));
        updateUIIDForInherited(remainingEffort, Item.isRemainingDefaultValue(remainingEffort.getDuration()));
//                MyPrefs.useEstimateDefaultValueForZeroEstimatesInMinutes.getBoolean());
//                && remainingEffort.getDuration() == MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //NB! MUST do *after* layoutN() which sets the UIID

        //ACTUAL************
        //if project, show actual for subtasks
        if (isProject) { //true: makes sense if work was done on project *before* subtasks were added! false: makes no sense to show actual for project itself, just confusing
            timeCont.add(layoutN(Item.EFFORT_ACTUAL_SUBTASKS, new Label(MyDate.formatDurationStd(itemOrg.getActualForSubtasks()), "ScreenItemValueUneditable"),
                    Item.EFFORT_ACTUAL_SUBTASKS_HELP, true, true, false, hide ? null : Icons.iconActualEffort));
        }

        //if single task, show picker with text for single task, if project show picker w text for ProjectTaskItself
        String actualTxt = isProject ? Item.EFFORT_ACTUAL_PROJECT_TASK_ITSELF : Item.EFFORT_ACTUAL;
        String actualHelpTxt = isProject ? Item.EFFORT_ACTUAL_PROJECT_TASK_ITSELF_HELP : Item.EFFORT_ACTUAL_HELP;
//        MyDurationPicker actualEffort;
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
                if (startedOnDate.getDate().getTime() == 0) {
                    startedOnDate.setDate(new MyDate());
                }
            } else { // actual effort set to 0
                if (itemOrg.isProject() && itemOrg.areAnySubtasksOngoingOrDone()) { //if some subtasks are ongoing or done
                    status.setStatus(ItemStatus.ONGOING, false);
                    if (startedOnDate.getDate().getTime() == 0) {
                        startedOnDate.setDate(new MyDate());
                    }
                } else {
                    status.setStatus(ItemStatus.CREATED, false); //if setting actual to 0, set status back to Created
                    startedOnDate.setDate(new MyDate(0));
                }
            }
            status.repaint();
        });

        initField(Item.PARSE_ACTUAL_EFFORT, actualEffort,
                () -> itemCopy.getActualForProjectTaskItself(),
                (l3) -> itemOrg.setActual((long) l3, false),
                () -> actualEffort.getDuration(), (ms) -> actualEffort.setDuration((long) ms));

        timeCont.add(layoutN(actualTxt, actualEffort, actualHelpTxt, hide ? null : (isProject ? Icons.iconEffortProject : Icons.iconActualEffort)));

        //ESTIMATE************
        if (isProject) { //true: makes sense if work was done on project *before* subtasks were added! false: makes no sense to show actual for project itself, just confusing
//            timeCont.add(layoutN(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatTimeDuration(itemLS.getEffortEstimateForSubtasks() / MyDate.MINUTE_IN_MILLISECONDS), "LabelFixed"),
            timeCont.add(layoutN(Item.EFFORT_ESTIMATE_SUBTASKS, new Label(MyDate.formatDurationStd(itemOrg.getEstimateForSubtasks()), "ScreenItemValueUneditable"),
                    Item.EFFORT_ESTIMATE_SUBTASKS_HELP, true, true, false, Icons.iconActualEffort));
        }
        String estimateTxt = isProject ? Item.EFFORT_ESTIMATE_PROJECT : Item.EFFORT_ESTIMATE;
        String estimateHelpTxt = isProject ? Item.EFFORT_ESTIMATE_PROJECT_HELP : Item.EFFORT_ESTIMATE_HELP;
        timeCont.add(layoutN(estimateTxt, effortEstimate, estimateHelpTxt, hide ? null : (isProject ? Icons.iconEffortProject : Icons.iconEstimateMaterial)));

        timeCont.add(makeSpacerThin());

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
        waitingTill = new MyDatePicker(); //"<wait until this date>",
//        timeCont.add(new Label(Item.WAIT_DATE)).add(addDatePickerWithClearButton(waitingTill));
//        timeCont.add(new Label(Item.WAIT_DATE)).add(waitingTill.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAIT_UNTIL_DATE, waitingTill.makeContainerWithClearButton(), "**"));
        initField(Item.PARSE_WAITING_TILL_DATE, waitingTill,
                () -> itemCopy.getWaitingTillDate(),
                (t) -> itemOrg.setWaitingTillDate((Date) t),
                () -> waitingTill.getDate(),
                (d) -> waitingTill.setDate((Date) d),
                () -> itemCopy.isWaitingTillInherited(waitingTill.getDate()));
        timeCont.add(layoutN(Item.WAIT_UNTIL_DATE, waitingTill, Item.WAIT_UNTIL_DATE_HELP, hide ? null : Icons.iconWaitingDateMaterial));
        updateUIIDForInherited(waitingTill, itemCopy.isWaitingTillInherited(waitingTill.getDate()));

//        MyDateAndTimePicker waitingAlarm = new MyDateAndTimePicker(parseIdMap2, () -> itemLS.getWaitingAlarmDateD(), (d) -> item.setWaitingAlarmDate(d)); //"<waiting reminder this date>", 
        waitingAlarm = new MyDateAndTimePicker(); //"<waiting reminder this date>", 
        initField(Item.PARSE_WAITING_ALARM_DATE, waitingAlarm,
                () -> itemCopy.getWaitingAlarmDate(),
                (t) -> itemOrg.setWaitingAlarmDate((Date) t),
                () -> waitingAlarm.getDate(),
                (d) -> waitingAlarm.setDate((Date) d));

//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(addDatePickerWithClearButton(waitingAlarm));
//        timeCont.add(new Label(Item.WAITING_ALARM_DATE)).add(waitingAlarm.makeContainerWithClearButton());
//        timeCont.add(layout(Item.WAITING_ALARM_DATE, waitingAlarm.makeContainerWithClearButton(), "**"));
        timeCont.add(layoutN(Item.WAITING_ALARM_DATE, waitingAlarm, Item.WAITING_ALARM_DATE_HELP, hide ? null : Icons.iconWaitingAlarm));

//        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("<set date>", parseIdMap2, () -> item.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
//        MyDateAndTimePicker dateSetWaitingDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getDateWhenSetWaitingD(), (d) -> item.setDateWhenSetWaiting(d));
        dateSetWaitingDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.DATE_WHEN_SET_WAITING)).add(dateSetWaitingDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is set Waiting"));
//        statusCont.add(layout(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate.makeContainerWithClearButton(), "Set automatically when a task is set Waiting"));
//        statusCont.add(layout(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate, Item.DATE_WHEN_SET_WAITING_HELP));
//</editor-fold>
        initField(Item.PARSE_DATE_WHEN_SET_WAITING, dateSetWaitingDate, () -> itemOrg.getDateWhenSetWaiting(), (s) -> itemOrg.setDateWhenSetWaiting((Date) s),
                () -> dateSetWaitingDate.getDate(), (s) -> dateSetWaitingDate.setDate((Date) s));

        timeCont.add(layoutN(Item.DATE_WHEN_SET_WAITING, dateSetWaitingDate, Item.DATE_WHEN_SET_WAITING_HELP, hide ? null : Icons.iconSetWaitingDateMaterial));

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
                    dateSetWaitingDate.setDate(new MyDate());
                }
            } else { //clearing the waiting date => replace the Waiting status by whatever is appropriate
                if (status.getStatus() == ItemStatus.WAITING) { //only update if created or ongoing (not cancelled, Done)
                    status.setStatus(actualEffort.getDuration() != 0 ? ItemStatus.ONGOING : ItemStatus.CREATED); //UI: if deleting setWaitingDate, then reset status to whatever it was before
                }
            }
            noAutoUpdateOnStatusChange = false;
        });

        timeCont.add(makeSpacerThin());

//        MyDatePicker hideUntil = new MyDatePicker(parseIdMap2, () -> itemLS.getHideUntilDateD(), (d) -> item.setHideUntilDate(d)); //"<hide task until>", 
        hideUntil = new MyDatePicker(); //"<hide task until>", 
        initField(Item.PARSE_HIDE_UNTIL_DATE, hideUntil,
                () -> itemCopy.getHideUntilDateD(),
                (t) -> itemOrg.setHideUntilDate((Date) t),
                () -> hideUntil.getDate(),
                (d) -> hideUntil.setDate((Date) d),
                () -> itemCopy.isHideUntilDateInherited(hideUntil.getDate()));

//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(addDatePickerWithClearButton(hideUntil));
//        timeCont.add(new Label(Item.HIDE_UNTIL)).add(hideUntil.makeContainerWithClearButton());
//        timeCont.add(layout(Item.HIDE_UNTIL, hideUntil.makeContainerWithClearButton(), "**"));
        timeCont.add(layoutN(Item.HIDE_UNTIL, hideUntil, Item.HIDE_UNTIL_HELP, hide ? null : Icons.iconHideUntilDate));

        if (true) {
//            MyDatePicker expireByDate = new MyDatePicker(parseIdMap2, () -> itemLS.getExpiresOnDateD(), (d) -> item.setExpiresOnDateD(d)); // "<auto-cancel on date>", 
            expireByDate = new MyDatePicker(); // "<auto-cancel on date>", 
            initField(Item.PARSE_EXPIRES_ON_DATE, expireByDate, () -> itemCopy.getExpiresOnDate(), (t) -> itemOrg.setExpiresOnDate((Date) t),
                    () -> expireByDate.getDate(), (d) -> expireByDate.setDate((Date) d));

//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(addDatePickerWithClearButton(expireByDate));
//            timeCont.add(new Label(Item.AUTOCANCEL_BY)).add(expireByDate.makeContainerWithClearButton());
//            timeCont.add(layout(Item.AUTOCANCEL_BY, expireByDate.makeContainerWithClearButton(), "**"));
            timeCont.add(layoutN(Item.AUTOCANCEL_BY, expireByDate, Item.AUTOCANCEL_BY_HELP, hide ? null : Icons.iconAutoCancelByDate));
        }

        //TAB PRIO
        Container prioCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));

        prioCont.setScrollableY(true);
        if (Config.TEST) {
            prioCont.setName("PrioTab");
        }

        tabs.addTab("Prio", Icons.iconPrioTab, TAB_ICON_SIZE_IN_MM, prioCont);

//        MyStringPicker priority = new MyStringPicker(new String[]{"None", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2, () -> item.getPriority(), (i) -> item.setPriority(i));
//        cont.add(new Label("Priority")).add(priority);
//        prioCont.add(Item.PRIORITY).add(priority);
//                () -> itemLS.getPriority() == 0 ? "" : itemLS.getPriority() + "",
//        MyComponentGroup priority = new MyComponentGroup(new String[]{"-", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, parseIdMap2,
//                (s) -> item.setPriority(Integer.parseInt(s.length() == 0 ? "0" : s)));
//        MyComponentGroup priority = new MyComponentGroup(new String[]{"-", "1", "2", "3", "4", "5", "6", "7", "8", "9"}, true);
        priority = new MyComponentGroup(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"}, true);
        initField(Item.PARSE_PRIORITY, priority,
                () -> itemCopy.getPriority(),
                (t) -> itemOrg.setPriority(t != null ? (int) t : 0),
                () -> priority.getSelectedIndex() + 1,
                (i) -> priority.selectIndex(((int) i) - 1),
                () -> itemCopy.isPriorityInherited(priority.getSelectedIndex() + 1)
        );
//        prioCont.add(layoutN(Item.PRIORITY, priority, Item.PRIORITY_HELP));//, null, true, false, false, true));
        prioCont.add(layoutN(Item.PRIORITY, priority, Item.PRIORITY_HELP, hide ? null : Icons.iconPriority));//, null, true, false, false, true));
        updateUIIDForInherited(priority, itemCopy.isPriorityInherited(priority.getSelectedIndex()));
//        prioCont.add(layout(Item.PRIORITY, priority, Item.PRIORITY_HELP, true, false, true));

        prioCont.add(makeSpacerThin());

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyComponentGroup importance = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getImportanceN() == null ? "" : itemLS.getImportanceN().getDescription(),
//                (s) -> item.setImportance(Item.HighMediumLow.getValue(s)));
//        MyComponentGroup importance = new MyComponentGroup(HighMediumLow.getDescriptionList(), true);
////        initField(Item.PARSE_IMPORTANCE, importance, () -> item.getImportanceN(), (t) -> item.setImportance((Item.HighMediumLow.getValue((String) t))),
//        initField(Item.PARSE_IMPORTANCE, importance,
//                () -> itemCopy.getImportanceN() != null ? itemCopy.getImportanceN().toString() : null,
//                //                (t) -> item.setImportance((Item.HighMediumLow.getValue((String) t))),
//                (enumStr) -> itemOrg.setImportance(enumStr != null ? (HighMediumLow.valueOf((String) enumStr)) : null),
//                //                () -> importance.getSelectedString(),
//                () -> importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()).toString() : null,
//                //                (i) -> importance.select(i != null ? (String) i.toString() : null));
//                //                (i) -> importance.select(i != null ? HighMediumLow.getValue( (String)i).getDescription(): null));
//                //                (enumStr) -> importance.select(enumStr != null ? HighMediumLow.valueOf((String)enumStr).getDescription(): null));
//                (enumStr) -> importance.select(enumStr != null ? HighMediumLow.valueOf((String) enumStr).getDescription() : null),
//                () -> itemCopy.isImportanceInherited(importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()) : null)
//        );
//</editor-fold>
        importance = new MyComponentGroup(HighMediumLow.getNameList(), HighMediumLow.getDescriptionList(), true);
//        initField(Item.PARSE_IMPORTANCE, importance, () -> item.getImportanceN(), (t) -> item.setImportance((Item.HighMediumLow.getValue((String) t))),
        initField(Item.PARSE_IMPORTANCE, importance,
                () -> (itemCopy.getImportanceN() != null ? itemCopy.getImportanceN().name() : ""),
                (enumName) -> itemOrg.setImportance(enumName != null ? HighMediumLow.valueOf((String) enumName) : null),
                () -> importance.getSelected(),
                (enumName) -> importance.selectValue(enumName),
                () -> itemCopy.isImportanceInherited(importance.getSelected() != null ? HighMediumLow.valueOf((String) importance.getSelected()) : null)
        );
//                (i) -> importance.select(i != null ?  i.toString() : null));

//        prioCont.add(Item.IMPORTANCE).add(FlowLayout.encloseCenterMiddle(importance));
//        prioCont.add(layout(Item.IMPORTANCE, FlowLayout.encloseCenterMiddle(importance), "**"));
//        prioCont.add(layout(Item.IMPORTANCE, importance, Item.IMPORTANCE_HELP, true, false, true));
        prioCont.add(layoutN(Item.IMPORTANCE, importance, Item.IMPORTANCE_HELP, hide ? null : Icons.iconImportance));//, null, false, false, true, true));
//        updateUIIDForInherited(importance, itemCopy.isImportanceInherited(importance.getSelectedString() != null ? HighMediumLow.getValue(importance.getSelectedString()) : null));
        updateUIIDForInherited(importance, itemCopy.isImportanceInherited(importance.getSelected() != null ? HighMediumLow.valueOf((String) importance.getSelected()) : null));

//        MyComponentGroup urgency = new MyComponentGroup(Item.HighMediumLow.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getUrgencyN() == null ? "" : itemLS.getUrgencyN().getDescription(),
//                (s) -> item.setUrgency(Item.HighMediumLow.getValue(s)));
        urgency = new MyComponentGroup(HighMediumLow.getNameList(), HighMediumLow.getDescriptionList(), true);
//        cont.add(new Label("Urgency")).add(urgency);
//        prioCont.add(Item.URGENCY).add(FlowLayout.encloseMiddle(urgency));
//        prioCont.add(layout(Item.URGENCY, FlowLayout.encloseMiddle(urgency), "**"));
//        prioCont.add(layout(Item.URGENCY, urgency, Item.URGENCY_HELP, true, false, true));
//        initField(Item.PARSE_URGENCY, urgency,
//                () -> itemCopy.getUrgencyN() != null ? itemCopy.getUrgencyN().toString() : null,
//                (enumStr) -> itemOrg.setUrgency(enumStr != null ? (HighMediumLow.valueOf((String) enumStr)) : null),
//                //                () -> urgency.getSelectedString(), (i) -> urgency.select(i != null ? (String) i.toString() : null));
//                () -> urgency.getSelectedString() != null ? HighMediumLow.getValue(urgency.getSelectedString()).toString() : null,
//                (enumStr) -> urgency.select(enumStr != null ? HighMediumLow.valueOf((String) enumStr).getDescription() : null),
//                () -> itemCopy.isUrgencyInherited(urgency.getSelectedString() != null ? HighMediumLow.getValue(urgency.getSelectedString()) : null)
//        );
        initField(Item.PARSE_URGENCY, urgency,
                () -> itemCopy.getUrgencyN() != null ? itemCopy.getUrgencyN().name() : "",
                (enumName) -> itemOrg.setUrgency(enumName != null ? HighMediumLow.valueOf((String) enumName) : null),
                //                () -> urgency.getSelectedString(), (i) -> urgency.select(i != null ? (String) i.toString() : null));
                () -> urgency.getSelected(),
                (enumName) -> urgency.selectValue(enumName),
                () -> itemCopy.isUrgencyInherited(urgency.getSelected() != null ? HighMediumLow.valueOf((String)urgency.getSelected()) : null)
        );
        prioCont.add(layoutN(Item.URGENCY, urgency, Item.URGENCY_HELP, hide ? null : Icons.iconUrgency));//, null, false, false, true, true));
        updateUIIDForInherited(urgency, itemCopy.isUrgencyInherited(urgency.getSelected() != null ? HighMediumLow.valueOf((String) urgency.getSelected()) : null));

        prioCont.add(makeSpacerThin());

//        MyComponentGroup challenge = new MyComponentGroup(Item.Challenge.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getChallengeN() == null ? "" : itemLS.getChallengeN().getDescription(),
//                (s) -> item.setChallenge(Item.Challenge.getValue(s)));
        MyComponentGroup challenge1 = new MyComponentGroup(Challenge.getNameList(), Challenge.getDescriptionList(), true);
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
        challenge = challenge1.getPreferredW() < Display.getInstance().getDisplayWidth() ? challenge1
                : new MyComponentGroup(Challenge.getDescriptionList(true), true);
        initField(Item.PARSE_CHALLENGE, challenge,
                () -> itemCopy.getChallengeN() != null ? itemCopy.getChallengeN().name() : "",
                (enumName) -> itemOrg.setChallenge(enumName != null ? (Challenge.valueOf((String) enumName)) : null),
                //                () -> challenge.getSelectedString(), (s) -> challenge.select(s != null ? (String) s.toString() : null));
                () -> challenge.getSelected(),
                (enumName) -> challenge.selectValue(enumName),
                () -> itemCopy.isChallengeInherited((challenge.getSelected() != null ? Challenge.valueOf((String) challenge.getSelected()) : null))
        );
//<editor-fold defaultstate="collapsed" desc="comment">
//        prioCont.add(new Label(Item.CHALLENGE)).add(FlowLayout.encloseCenterMiddle(challenge));
//        prioCont.add(new Label("TEST")).add(FlowLayout.encloseCenterMiddle(new Label("11111"),new Label("22222"),new Label("33333"),new Label("44444"),new Label("55555")));
//        prioCont.add(layout(Item.CHALLENGE, FlowLayout.encloseCenterMiddle(challenge), "**", true));
//        prioCont.add(layout(Item.CHALLENGE, challenge, Item.CHALLENGE_HELP, true, false, true));
//</editor-fold>
        prioCont.add(layoutN(Item.CHALLENGE, challenge, Item.CHALLENGE_HELP, hide ? null : Icons.iconChallengeHard));//, null, false, false, true, true));
        updateUIIDForInherited(challenge, itemCopy.isChallengeInherited(challenge.getSelected() != null ? Challenge.valueOf((String) challenge.getSelected()) : null));

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyComponentGroup dreadFun = new MyComponentGroup(Item.DreadFunValue.getDescriptionList(), parseIdMap2,
//                () -> itemLS.getDreadFunValueN() == null ? "" : itemLS.getDreadFunValueN().getDescription(),
//                (s) -> item.setDreadFunValue(Item.DreadFunValue.getValue(s)));
//        prioCont.add(new Label(Item.FUN_DREAD)).add(dreadFun);
//        prioCont.add(layout(Item.FUN_DREAD, FlowLayout.encloseCenterMiddle(dreadFun), Item.FUN_DREAD_HELP));
//        prioCont.add(layout(Item.FUN_DREAD, dreadFun, Item.FUN_DREAD_HELP, true, false, true));
//</editor-fold>
        dreadFun = new MyComponentGroup(DreadFunValue.getNameList(),DreadFunValue.getDescriptionList(), true);
        initField(Item.PARSE_DREAD_FUN_VALUE, dreadFun,
                () -> itemCopy.getDreadFunValueN() != null ? itemCopy.getDreadFunValueN().name() : null,
                (enumName) -> itemOrg.setDreadFunValue(enumName != null ? (DreadFunValue.valueOf((String) enumName)) : null),
                //                () -> dreadFun.getSelectedString(), (s) -> dreadFun.select(s != null ? (String) s.toString() : null));
                () -> dreadFun.getSelected(),
                (enumName) -> dreadFun.selectValue(enumName),
                () -> itemCopy.isDreadFunInherited((dreadFun.getSelected() != null ? DreadFunValue.valueOf((String) dreadFun.getSelected()) : null))
        );
//        initField(Item.PARSE_DREAD_FUN_VALUE, dreadFun,
//                () -> item.getDreadFunValueN() != null ? item.getDreadFunValueN().toString() : null,
//                (enumStr) -> item.setDreadFunValue(enumStr != null ? (DreadFunValue.valueOf((String) enumStr)) : null),
//                //                () -> dreadFun.getSelectedString(), (s) -> dreadFun.select(s != null ? (String) s.toString() : null));
//                () -> dreadFun.getSelectedString() != null ? DreadFunValue.getValue(dreadFun.getSelectedString()).toString() : null,
//                (enumStr) -> dreadFun.select(enumStr != null ? DreadFunValue.valueOf((String) enumStr).getDescription() : null),
//                () -> item.isDreadFunInherited((dreadFun.getSelectedString() != null ? DreadFunValue.getValue(dreadFun.getSelectedString()) : null))
//        );
        prioCont.add(layoutN(Item.FUN_DREAD, dreadFun, Item.FUN_DREAD_HELP, hide ? null : Icons.iconFun));//, null, false, false, true, true));
        updateUIIDForInherited(dreadFun, itemCopy.isDreadFunInherited(dreadFun.getSelected() != null ? DreadFunValue.valueOf((String) dreadFun.getSelected()) : null));

        prioCont.add(makeSpacerThin());

//        MyNumericTextField earnedValue = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValue(), (d) -> item.setEarnedValue(d));
        earnedValue = new MyNumericTextField("");
        earnedValue.setUIID("ScreenItemEarnedValue");
//        earnedValue.setColumns(2); //result: only shows/truncates to 2 columns when field is not edited

//        prioCont.add(new Label(Item.EARNED_VALUE)).add(earnedValue);
//        prioCont.add(layout(Item.EARNED_VALUE, earnedValue, Item.EARNED_VALUE_HELP, true, false, false));
        initField(Item.PARSE_EARNED_VALUE, earnedValue,
                //                () -> L10NManager.getInstance().format(itemCopy.getEarnedValue(), 2), 
                () -> setEarnedValueAsString(itemCopy.getEarnedValue()),
                //                (s) -> itemOrg.setEarnedValue(L10NManager.getInstance().parseDouble((String) s)),
                (s) -> itemOrg.setEarnedValue(getEarnedValueAsDouble((String) s)),
                () -> earnedValue.getText(),
                (s) -> earnedValue.setText((String) s)); //TODO!!! localize number of decimal points (2)??
//        prioCont.add(layoutN(Item.EARNED_VALUE, earnedValue, Item.EARNED_VALUE_HELP, null, false, true, false, false));
        prioCont.add(layoutN(Item.EARNED_VALUE, earnedValue, Item.EARNED_VALUE_HELP, null, false, true, false, false, false, hide ? null : Icons.iconEarnedValue));

//        MyNumericTextField earnedValuePerHour = new MyNumericTextField("<set>", parseIdMap2, () -> item.getEarnedValuePerHour(), (d) -> {
//        MyNumericTextField earnedValuePerHour = new MyNumericTextField("", parseIdMap2, () -> itemLS.getEarnedValuePerHour(), (d) -> {
//        });
////        earnedValuePerHour.setConstraint(TextArea.UNEDITABLE);
//        earnedValuePerHour.setEditable(false);
        Label earnedValuePerHour = new Label(L10NManager.getInstance().format(itemCopy.getEarnedValuePerHour(), 2));
        earnedValuePerHour.setUIID("ScreenItemValueUneditable");
//        prioCont.add(new Label(Item.EARNED_POINTS_PER_HOUR)).add(earnedValuePerHour).add(new SpanLabel("Value per hour is calculated as Value divided by the Estimate, or the sum of Remaining and Actual effort - once work has started."));
//                add(new SpanLabel(Item.EARNED_POINTS_PER_HOUR + " is calculated as " + Item.EARNED_VALUE + " divided by " + Item.EFFORT_ESTIMATE + ", and once work has started by the sum of " + Item.EFFORT_REMAINING + " and " + Item.EFFORT_ACTUAL + "."));
//        prioCont.add(layout(Item.EARNED_POINTS_PER_HOUR, earnedValuePerHour, Item.EARNED_POINTS_PER_HOUR_HELP, true, true, true));
//        prioCont.add(layoutN(Item.EARNED_VALUE_PER_HOUR, earnedValuePerHour, Item.EARNED_VALUE_PER_HOUR_HELP, true, true, false));
        prioCont.add(layoutN(Item.EARNED_VALUE_PER_HOUR, earnedValuePerHour, Item.EARNED_VALUE_PER_HOUR_HELP, true, true, false, hide ? null : Icons.iconEarnedValuePerHour));

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
            if (!remainingEffortSetManually && !remainingEffortSetAutomatically) {
                remainingEffortSetManually = true; //set on first manual set and keep it (=> no more automatic setting in this round)
            }
            if (remainingEffortSetManually && !effortEstimateSetManually
                    && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()
                    && itemOrg.getEstimate() == 0) { //UI: only allow manual setting if no value was set before
                effortEstimateSetAutomatically = true;
                effortEstimate.setDurationAndNotify(remainingEffort.getDuration() + actualEffort.getDuration()); //UI: when auto-updating estimate, any already worked time is automatically added to the estimate (since it is the remaining set *after* actual was updated) 
                effortEstimateSetAutomatically = false;
                effortEstimate.repaint();
            }
            updateUIIDForInherited(remainingEffort,
                    MyPrefs.useEstimateDefaultValueForZeroEstimatesInMinutes.getBoolean()
                    && remainingEffort.getDuration() == MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //NB! MUST do *after* layoutN() which sets the UIID
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
            if (!effortEstimateSetManually && !effortEstimateSetAutomatically) {
                effortEstimateSetManually = true; //set on first manual set and keep it (=> no more automatic setting in this round)
            }
            if (effortEstimateSetManually && !remainingEffortSetManually
                    && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean() //&& item.getRemainingEffortNoDefault() == 0
                    //                    && itemOrg.getRemainingForProjectTaskItselfFromParse() == 0) { //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                    //only update Remaining to new Estimate if not changed manually and equal to zero or default value:
                    //                    && (remainingEffort.getDuration() == itemOrg.getRemainingForProjectTaskItselfFromParse()
                    //                    && (remainingEffort.getDuration() == 0
                    //                    || remainingEffort.getDuration() == MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS)
                    ) { //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                remainingEffortSetAutomatically = true;
                remainingEffort.setDurationAndNotify(effortEstimate.getDuration() - actualEffort.getDuration()); //UI: when auto-updating remaining, any already worked time is automatically deducted from the estimate
                remainingEffortSetAutomatically = false;
                remainingEffort.repaint();
            }
            remainingEffort.repaint(); //TEST: moved outside if to see if picker updates on iPhone then!
        });

        //TAB STATUS FIELDS
        Container statusCont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        if (Config.TEST) {
            statusCont.setName("StatusTab");
        }
        statusCont.setScrollableY(true);
        tabs.addTab("Status", Icons.iconStatusTab, TAB_ICON_SIZE_IN_MM, statusCont);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getCreatedDate())));
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()),"LabelFixed");
//        Label createdDate = new Label(item.getCreatedDate() == 0 ? "<date set when saved>" : MyDate.formatDateNew(item.getCreatedDate()));
//</editor-fold>
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
        //OWNER
//        MySpanButton editOwnerButton = new MySpanButton();
//        SpanButton editOwnerButton = new SpanButton();
        MySpanButton editOwnerButton = new WrapButton();

        ActionListener refreshOwnerButton = (e) -> {
            String ownerStr
                    //                    = (previousValues != null && previousValues.get(Item.PARSE_OWNER_ITEM) != null && ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0)
                    //                    ? DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)).getText()
                    //                    : (itemOrg.getOwner() != null ? itemOrg.getOwner().getText() : "");
                    = (previousValues != null && previousValues.getOwnersN() != null)
                    ? (previousValues.getOwnersN().size() > 0 ? previousValues.getOwnersN().get(0).getText() : "")
                    : (itemOrg.getOwner() != null ? itemOrg.getOwner().getText() : "");
            editOwnerButton.setText(ownerStr);
        };
//<editor-fold defaultstate="collapsed" desc="comment">
//            final Command editOwnerCmd = Command.create(item.getOwner().getText(), null, (e) -> {
//        Command editOwnerCmd = new Command(item.getOwner() == null ? "<no owner>" : item.getOwner().getText()) {
//        Command editOwnerCmd = MyReplayCommand.create("EditOwner", item.getOwner() == null ? "" : item.getOwner().getText(), null, (e) -> {
//        Command editOwnerCmd = Command.create(item.getOwner() == null ? "" : item.getOwner().getText(), null, (e) -> {
//</editor-fold>
        Command editOwnerCmd = MyReplayCommand.create("EditOwner", null, null, (e) -> {
            List projects = DAO.getInstance().getAllProjects(false); //TODO optimization: slow to fetch all projects each time!
            projects.remove(itemOrg); //Must not be possible to select the item itself as its own owner. NB to modify the list since not used elsewhere (new created in DAO)

            //cconvert list of ObjectId to list of actual owners (well, 0 or 1 owner)
            List<ItemAndListCommonInterface> locallyEditedOwner
                    //                    = previousValues != null && previousValues.get(Item.PARSE_OWNER_ITEM) != null && ((List) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0
                    //                    ? new ArrayList(Arrays.asList(DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)))) //fetch the actual owner 
                    //                    : new ArrayList(Arrays.asList(itemOrg.getOwner()));
                    //                    = previousValues != null && previousValues.getOwner() != null
                    //                    ? previousValues.getOwner() //fetch the actual owner 
                    //                    : itemOrg.getOwner();
                    = (previousValues != null && previousValues.getOwnersN() != null)
                    ? previousValues.getOwnersN() //use previous selection (either new owner or previous owner was unselected
                    : new ArrayList(Arrays.asList(itemOrg.getOwner())); //fetch the actual owner 

//            List<ItemAndListCommonInterface> locallyEditedOwnerAsList = new ArrayList(Arrays.asList(locallyEditedOwner)); //needed for easy access to selection in 
            ItemAndListCommonInterface previousOwner = locallyEditedOwner.get(0);
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
                    locallyEditedOwner,
                    ScreenItem2.this,
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
                            ItemAndListCommonInterface selectedOwner = locallyEditedOwner.get(0); //even if multiple should be selected (shouldn't be possible), only use first
//                            if ((newOwner==null&&item.getOwner()==null)||newOwner.equals(item.getOwner())) {
                            if (selectedOwner.equals(itemOrg.getOwner())) {
//                                        previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList()); //store empty list (e.g. if previous owner was selected again)
//                                previousValues.remove(Item.PARSE_OWNER_ITEM); //store empty list (e.g. if previous owner was selected agagin)
                                previousValues.removeOwners(); //store empty list (e.g. if previous owner was selected agagin)
//                                editOwnerButton.setText(item.getOwner()==null?"":item.getOwner().getText());  //set back to old Owner
//                                if (false) {
//                                    editOwnerButton.setText(itemOrg.getOwner().getText());  //set back to old Owner
//                                }
                            } else { //new owner (or no owner if unselected)
                                //ensure all pickers take their value from current owner if none is defined for the item.
//<editor-fold defaultstate="collapsed" desc="comment">
//                                itemCopy.setOwner(newOwner, true);
//                                if (false) {
//                                    itemCopy.removeValuesInheritedFromOwner(locallyEditedOwner.get(0)); //remove old owner's values, nothing's done if oldOwner is null
//                                    itemCopy.updateValuesInheritedFromOwner(newOwner);
//                                }
//                                previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(((ItemAndListCommonInterface) newOwner).getObjectIdP())); //store objectId of new owner
//                                previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(((ItemAndListCommonInterface) newOwner).getObjectIdP()))); //store objectId of new owner
//</editor-fold>
                                previousValues.putOwners(locallyEditedOwner); //store objectId of new owner
                                if (false) {
                                    refreshAfterEdit(); //update eg inherited values
                                }//                                if (false) {
//                                    editOwnerButton.setText(selectedOwner.getText());
//                                }
                            }
//                            itemCopy.removeValuesInheritedFromOwner(locallyEditedOwner.get(0)); //remove old owner's values, nothing's done if oldOwner is null
                            itemCopy.removeValuesInheritedFromOwner(previousOwner); //remove old owner's values, nothing's done if oldOwner is null
                            itemCopy.updateValuesInheritedFromOwner(selectedOwner);
                        } else { //locallyEditedOwner.size()==0 => no selected owner (either old one was deleted, or a previously new one was removed, or simply none was chosen)
                            //SHOULD never happen (current parameters of ScreenObjectPicker imposes exactly when owner selected
                            if (itemOrg.getOwner() == null) {
//                                previousValues.remove(Item.PARSE_OWNER_ITEM); //remove previousValue, e.g. no owner before, none selected now
//                                previousValues.putOwners(locallyEditedOwners); //remove previousValue, e.g. no owner before, none selected now
                                previousValues.removeOwners(); //remove previousValue, e.g. no owner before, none selected now
                            } else {
//                                previousValues.put(Item.PARSE_OWNER_ITEM, new ArrayList()); //store empty list (e.g. if previous owner was deselected)
//                                previousValues.putOwners(null); //store empty list (e.g. if previous owner was deselected)
                                previousValues.putOwners(locallyEditedOwner); //store empty list (e.g. if previous owner was deselected)
                            }
//                            if (false) {
//                                editOwnerButton.setText(""); //"<no owner>"
//                            }
                        }
                    }, null, 1, 1, true, true, false); //MUST select exactly ONE owner (no element has no owner)
            ownerPicker.show();
        }
        );
        editOwnerButton.setCommand(editOwnerCmd);
        refreshOwnerButton.actionPerformed(null); //set button text *after* setting command
        parseIdMap2.put(Item.PARSE_OWNER_ITEM, () -> {
//            if (previousValues.get(Item.PARSE_OWNER_ITEM) != null) {
//                if (((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).size() > 0) {
            List<ItemAndListCommonInterface> newOwnersN = previousValues.getOwnersN();
            if (newOwnersN != null) { //a new owner is selected (or previous owner unselected
                if (newOwnersN.size() > 0) { //a new owner is selected
                    ItemAndListCommonInterface newOwner = newOwnersN.get(0);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    item.setOwner(DAO.getInstance().fetchItem(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0)));
//                    ItemAndListCommonInterface oldOwner = item.getOwner();
//                    if (oldOwner != null) {
//                        oldOwner.removeFromList(item);
//                    }
//                    ItemAndListCommonInterface newOwner; // = null;
//                    if (true) {
//</editor-fold>
                    ItemAndListCommonInterface oldOwner = itemOrg.removeFromOwner();
//                        ItemAndListCommonInterface newOwner = DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0));
//                        ItemAndListCommonInterface newOwner = previousValues.getOwner();
                    newOwner.addToList(itemOrg);
                    DAO.getInstance().saveNew(false, itemOrg, (ParseObject) oldOwner, (ParseObject) newOwner);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    } else {
////                        ItemAndListCommonInterface newOwner = DAO.getInstance().fetchItemOwner(((List<String>) previousValues.get(Item.PARSE_OWNER_ITEM)).get(0));
//                        itemOrg.setOwner(newOwner, false); //false: don't update inherited values from owner, they should have been set in this screen
//                    }
//                    item.setOwner();
//</editor-fold>
                } else { //previousValues.getOwner()==null meaning either no owner was selected or a prevoious owner was unselected (and should be removed)
//<editor-fold defaultstate="collapsed" desc="comment">
//                    ItemAndListCommonInterface oldOwner = item.getOwner();
//                    if (oldOwner != null) {
//                        oldOwner.removeFromList(item);
//                    }
//</editor-fold>
                    ItemAndListCommonInterface oldOwner = itemOrg.removeFromOwner();
                    DAO.getInstance().saveNew((ParseObject) oldOwner, false);
//                    item.setOwner(null);
                }
            } //else: no change made to original owner so nothing to do!
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
//        statusCont.add(layout(Item.BELONGS_TO, editOwnerButton, Item.BELONGS_TO_HELP, true, false, false)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//</editor-fold>
        statusCont.add(layoutN(Item.BELONGS_TO, editOwnerButton, Item.BELONGS_TO_HELP, false, hide ? null : Icons.iconOwner)); //.add(new SpanLabel("Click to move task to other projects or lists"));

        statusCont.add(makeSpacerThin());

        Label createdDate = new Label(itemOrg.getCreatedDate() == 0 ? "" : MyDate.formatDateTimeNew(itemOrg.getCreatedDate())); //NOT use itemLS since CreatedDate is not saved locally
//        statusCont.add(new Label(Item.CREATED_DATE)).add(createdDate);
//        statusCont.add(layout(Item.CREATED_DATE, createdDate, "**", true, true, true));
        statusCont.add(layoutN(Item.CREATED_DATE, createdDate, "**", true, hide ? null : Icons.iconCreatedDate));

        if (itemOrg.isProject()) {
            long lastModifiedSubtasks = itemOrg.getLastModifiedDateProjectOrSubtasks().getTime();
            Label lastModifiedDateSubtasks = new Label(lastModifiedSubtasks == 0 ? "" : MyDate.formatDateTimeNew(lastModifiedSubtasks));
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//            statusCont.add(layout(Item.UPDATED_DATE_SUBTASKS, lastModifiedDateSubtasks, "**", true, true, true));
//            statusCont.add(layout(Item.UPDATED_DATE, lastModifiedDateSubtasks, "**", true, true, true));
//</editor-fold>
            statusCont.add(layoutN(Item.UPDATED_DATE, lastModifiedDateSubtasks, "**", true, hide ? null : Icons.iconModifiedDate));
        } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateShortStyle(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : L10NManager.getInstance().formatDateTimeShort(new Date(item.getLastModifiedDate())));
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()), "LabelFixed");
//        Label lastModifiedDate = new Label(item.getLastModifiedDate() == 0 ? "<date when modified>" : MyDate.formatDateNew(item.getLastModifiedDate()));
//</editor-fold>
            Label lastModifiedDate = new Label(itemOrg.getLastModifiedDate() == 0 ? "" : MyDate.formatDateTimeNew(itemOrg.getLastModifiedDate()));
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//            statusCont.add(layout(Item.UPDATED_DATE, lastModifiedDate, "**", true, true, true));
            statusCont.add(layoutN(Item.UPDATED_DATE, lastModifiedDate, Item.UPDATED_DATE_HELP, true, hide ? null : Icons.iconModifiedDate));
        }
        
        statusCont.add(makeSpacerThin());

//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getStartedOnDateD(), (d) -> item.setStartedOnDate(d));
//        MyDateAndTimePicker startedOnDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(addDatePickerWithClearButton(startedOnDate)).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when using the timer"));
//        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate.makeContainerWithClearButton(), "Set automatically when using the timer")); //"click to set date when started"
//        statusCont.add(layout(Item.STARTED_ON_DATE, startedOnDate, Item.STARTED_ON_DATE_HELP)); //"click to set date when started"
//</editor-fold>
        if (itemOrg.isProject()) {
            Label startedOnDateLabel = new Label(itemOrg.getStartedOnDateD().getTime() == 0 ? "" : MyDate.formatDateTimeNew(itemOrg.getStartedOnDateD()));
            statusCont.add(layoutN(Item.STARTED_ON_DATE_SUBTASKS, startedOnDateLabel, Item.STARTED_ON_DATE_HELP, true, hide ? null : Icons.iconStartedOnDate));
        } else {
            initField(Item.PARSE_STARTED_ON_DATE, startedOnDate, () -> itemOrg.getStartedOnDateD(), (s) -> itemOrg.setStartedOnDate((Date) s, true),
                    () -> startedOnDate.getDate(), (s) -> startedOnDate.setDate((Date) s));
            statusCont.add(layoutN(Item.STARTED_ON_DATE, startedOnDate, Item.STARTED_ON_DATE_HELP, hide ? null : Icons.iconStartedOnDate)); //"click to set date when started"
        }
//        statusCont.add(new Label(Item.STARTED_ON_DATE)).add(startedOnDate.)).add(new SpanLabel("Set automatically when using the timer"));

//        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("<set>", parseIdMap2, () -> item.getCompletedDateD(), (d) -> item.setCompletedDate(d));
//        MyDateAndTimePicker completedDate = new MyDateAndTimePicker("", parseIdMap2, () -> itemLS.getCompletedDateD(), (d) -> item.setCompletedDate(d));
        completedDate = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(addDatePickerWithClearButton(completedDate)).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(new Label(Item.COMPLETED_DATE)).add(completedDate.makeContainerWithClearButton()).add(new SpanLabel("Set automatically when a task is completed"));
//        statusCont.add(layout(Item.COMPLETED_DATE, completedDate.makeContainerWithClearButton(), "Set automatically when a task is completed")); //"click to set a completed date"
//        statusCont.add(layout(Item.COMPLETED_DATE, completedDate, Item.COMPLETED_DATE_HELP)); //"click to set a completed date"
//</editor-fold>
        initField(Item.PARSE_COMPLETED_DATE, completedDate, () -> itemOrg.getCompletedDateD(), (s) -> itemOrg.setCompletedDate((Date) s),
                () -> completedDate.getDate(), (s) -> completedDate.setDate((Date) s));

        statusCont.add(layoutN(Item.COMPLETED_DATE, completedDate, Item.COMPLETED_DATE_HELP, hide ? null : Icons.iconCompletedDate)); //"click to set a completed date"

        status.setStatusChangeHandler((oldStatus, newStatus) -> {
            //if status is set Ongoing and startedOnDate is not set and has not been set explicitly 
            //if status is changed
            //TODO!!! move this logic into Item as static method (or ensure consistent with changes made there), OR, at least check it is consistent with logic elsewhere (eg. if a project is set complete when last subtasks is completed, or in screenItemList)
//            ItemStatus newStatus = status.getStatus();
            if (newStatus == oldStatus || noAutoUpdateOnStatusChange) {
                return;
            }

            if (newStatus == ItemStatus.CREATED && itemOrg.getActual() > 0) {
                newStatus = ItemStatus.ONGOING;
            }
            Date now = new MyDate();
            Date zero = new MyDate(0);
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
                if (completedDate.getDate().getTime() != 0) {
                    completedDate.setDate(new MyDate(0));
                }
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
                startedOnDate.setDateAndNotify(new MyDate(completedDate.getDate().getTime())); //UI: if setting a completeDate, then if no startedOn date was set, it will also be set to same time
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

        statusCont.add(makeSpacerThin());

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
        if (itemOrg.getSource() != null && MyPrefs.enableShowingSystemInfo.getBoolean() && MyPrefs.showSourceItemInEditScreens.getBoolean()) { //don't show unless defined
            //TODO!! what happens if source is set to template or other item, then saved locally on app exit and THEN recreated via Replay???
//            Label sourceLabel = new Label(itemLS.getSource() == null ? "" : item.getSource().getText(), "LabelFixed");
            Label sourceLabel = new Label(itemCopy.getSource().getText(), "ScreenItemValueUneditable");
//            statusCont.add(new Label(Item.SOURCE)).add(source); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, source, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, sourceLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            statusCont.add(layoutN(Item.SOURCE, sourceLabel, Item.SOURCE_HELP, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            sourceLabel.setUIID();
        }

        //INTERRUPT
//        MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> itemLS.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
        interruptTask = new MyOnOffSwitch();
//                statusCont.add(new Label(Item.INTERRUPT_TASK)).add(interruptTask).add(new SpanLabel("This task interrupted another task"));
//        statusCont.add(layout(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task"));
        initField(Item.PARSE_INTERRUPT_OR_INSTANT_TASK, interruptTask, () -> itemCopy.isInteruptOrInstantTask(), (b) -> itemOrg.setInteruptOrInstantTask((boolean) b),
                () -> interruptTask.isValue(), (b) -> interruptTask.setValue((boolean) b));
//        statusCont.add(layoutN(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task", true));
        statusCont.add(layoutN(Item.INTERRUPT_TASK, interruptTask, "This task interrupted another task", true, hide ? null : Icons.iconInterrupt));
//        if (item.isInteruptOrInstantTask()) {
//            MyOnOffSwitch interruptTask = new MyOnOffSwitch(parseIdMap2, () -> item.isInteruptOrInstantTask(), (b) -> item.setInteruptOrInstantTask(b));
        //INTERRUPTED TASK
        if (itemOrg.getTaskInterrupted() != null) {
            //TODO!!! enable deleting the taskInterrupted (in case wrong or meaningless)
//            statusCont.add(layout(Item.INTERRUPT_TASK_INTERRUPTED,
//                    new SpanLabel("\"" + itemLS.getTaskInterrupted() != null ? itemLS.getTaskInterrupted().getText() + "\"" : "<none>"), "**", true, false, true)); //TODO capture and save the task that was interrupted
            statusCont.add(layoutN(Item.INTERRUPT_TASK_INTERRUPTED,
                    //                    new SpanLabel("\"" + itemLS.getTaskInterrupted() != null ? itemLS.getTaskInterrupted().getText() + "\"" : "<none>"),
                    new SpanLabel(itemOrg.getTaskInterrupted().getText()),
                    Item.INTERRUPT_TASK_INTERRUPTED_HELP, true)); //TODO capture and save the task that was interrupted
//            } else {
//                statusCont.add(new Label(Item.INTERRUPT_OR_INSTANT_TASK)).add(interruptTask);
        }
//        }

        if (MyPrefs.enableShowingSystemInfo.getBoolean() && MyPrefs.showObjectIdsInEditScreens.getBoolean()) {
//        Label itemObjectId = new Label(item.getObjectIdP() == null ? "<created when saved>" : item.getObjectIdP(), "LabelFixed");
            Label itemObjectId = new Label(itemOrg.getObjectIdP() == null ? "<set on save>" : itemOrg.getObjectIdP(), "ScreenItemValueUneditable");
//        statusCont.add(new Label(Item.MODIFIED_DATE)).add(lastModifiedDate);
//        statusCont.add(layout(Item.OBJECT_ID, itemObjectId, "**", true, true, true));
            statusCont.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true, hide ? null : Icons.iconObjectId));
        }

        if (true) {
//            Container statusCont=null;

            TextField textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
            textEntryField.setUIID("ListPinchInsertTextField");
            textEntryField.setHint("MyTextField2/InlineInsert");
            textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
            statusCont.add(textEntryField);

            statusCont.add("ENormal TextField from InlineInsert:");
            TextField textEntryField2 = new TextField(""); //TODO!!!! need field to enter edit mode
            textEntryField2.setUIID("ListPinchInsertTextField");
            textEntryField2.setHint("Normal TextField from InlineInsert");
            textEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
            statusCont.add(textEntryField2);

            statusCont.add("Normal TextField, noCaps:");
            TextField textEntryField3 = new TextField(""); //TODO!!!! need field to enter edit mode
            textEntryField3.setUIID("ListPinchInsertTextField");
            textEntryField3.setHint("Normal TextField from InlineInsert ");
            textEntryField3.setConstraint(TextField.ANY); //UI: automatically set caps sentence (first letter uppercase)
            statusCont.add(textEntryField3);

            statusCont.add("Enter a decimal value:");
            TextField textField = new TextField();
            textField.setConstraint(TextArea.DECIMAL);
            statusCont.add(textField);

            TextArea textField2 = new TextArea("TextArea", 3, 80);
            textField2.setConstraint(TextArea.ANY);
            statusCont.add(textField2);
            TextArea textField5 = new TextArea("TextArea, no constraint", 3, 80);
//                textField5.setConstraint(TextArea.ANY);
            statusCont.add(textField5);

            TextField textField1 = new TextField("TextField", "TextField", 3, 80);
            textField1.setConstraint(TextArea.ANY);
            statusCont.add(textField1);

            MyTextField textField3 = new MyTextField("Simple MyTextField", 3, 80);
            statusCont.add(textField3);

//                  MyTextField(String hint, int columns, int rows, int maxRows, int maxTextSize, int constraint,  int alignment) {
            MyTextField textField4 = new MyTextField("Normal MyTextField", 80, 3, 5, 200, TextArea.DECIMAL);
            statusCont.add(textField4);
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
//            tabs.setSelectedIndex((int) previousValues.get(LAST_TAB_SELECTED), MyPrefs.itemEditEnableSwipeBetweenTabs.getBoolean()); //keep same tab selected even if regenerating the screen
            tabs.setSelectedIndex((int) previousValues.get(LAST_TAB_SELECTED)); //keep same tab selected even if regenerating the screen
        } else {
            tabs.setSelectedIndex(0); //default to main index (this also ensures the tab is refreshed so fields like Categories/Repeat are sized correctly??)
        }
        setCheckIfSaveOnExit(() -> {
            if (itemOrg.getObjectIdP() != null) {
                return true; //don't ask to save an already saved item
            } else {
                return checkItemIsValidForSaving(description.getText(), comment.getText(), dueDate.getDate(),
                        actualEffort.getDuration(),
                        remainingEffort.getDuration(),
                        (((List) previousValues.get(Item.PARSE_CATEGORIES)) != null
                        ? ((List) previousValues.get(Item.PARSE_CATEGORIES)).size()
                        : 0),
                        itemOrg.getListFull().size());
            }
        }); //item.getListFull().size() sinze subtasks are stored 
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
