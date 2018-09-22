package com.todocatalyst.todocatalyst;

import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
import static com.todocatalyst.todocatalyst.MyForm.putEditedValues2;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main screen should contain the following elements: Views - user defined views
 * Jot-list Add new item Categories - see or edit categories People - list of
 * people to assign tasks to Locations - list of locations to assign tasks to
 * Find(?) - or just a menu item in each sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenWorkSlot extends MyForm {

//    static Map<String, GetParseValue> parseIdMap = new HashMap<String, GetParseValue>() ;
//    Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>();
//    MyForm previousForm;
    WorkSlot workSlot;
    static String SCREEN_TITLE = "Workslot";
    private String FILE_LOCAL_EDITED_WORKSLOT = "ScreenWorkSlot-EditedItem";
    private RepeatRuleParseObject locallyEditedRepeatRule;
    private RepeatRuleParseObject repeatRuleCopyBeforeEdit;
//    private UpdateField updateActionOnDone;

//    ScreenWorkSlot(WorkSlot workSlot, MyForm previousForm) { //throws ParseException, IOException {
//        this(workSlot, previousForm, null);
//    }
    ScreenWorkSlot(WorkSlot workSlot, MyForm previousForm, UpdateField doneAction) { //throws ParseException, IOException {
        super(SCREEN_TITLE, previousForm, doneAction);
//        ScreenItemP.item = item;
        this.workSlot = workSlot;
        setLayout(BoxLayout.y());
        getContentPane().setScrollableY(true);
//<editor-fold defaultstate="collapsed" desc="comment">
//        ScreenItemP.previousForm = previousForm;
//        this.previousForm = previousForm;
//        this.updateActionOnDone = doneAction;
// we initialize the main form and add the favorites command so we can navigate there
//        form = new Form("TodoCatalyst");
//        form = this;
// we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setToolbar(new Toolbar());
//        setTitle(screenTitle);
//</editor-fold>
        addCommandsToToolbar(getToolbar());
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
        buildContentPane(getContentPane());
        restoreKeepPos();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

//<editor-fold defaultstate="collapsed" desc="comment">
//        Image icon = FontImage.createMaterial(FontImage.MATERIAL_ADD_BOX, toolbar.getStyle());
//        if (false) {
//            Image icon = FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle());
////        toolbar.addCommandToLeftBar("Done", icon, (e) -> Log.p("Clicked"));
////        Command.create(name, icon, ev);
////        Command cmd = Command.create("Done", icon, (e) -> {
//            Command cmd = Command.create("", icon, (e) -> {
////            Log.p("Clicked");
//////            putEditedValues(parseIdMap);
//                putEditedValues2(parseIdMap2, workSlot);
////            try {
////                workSlot.save();
////                if (updateActionOnDone != null) {
//                updateActionOnDone.update();
////                }
////            } catch (ParseException ex) {
////                Logger.getLogger(ScreenItemP.class.getName()).log(Level.SEVERE, null, ex);
////                Log.e(ex); //TODO: add dialog/popup info when save does not succeed
////            }
//                previousForm.refreshAfterEdit();
//                previousForm.revalidate();
////            previousForm.showBack();
//                previousForm.show();
//            });
//            cmd.putClientProperty("android:showAsAction", "withText");
//            toolbar.addCommandToLeftBar(cmd);
//        }
//</editor-fold>
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand());

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) //        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> { //DONE!! replace with default Cancel command MyForm.makeCancelCommand()??
        //            Log.p("Clicked");
        ////            item.revert(); //forgetChanges***/refresh
        ////            previousForm.showBack(); //drop any changes
        //            previousForm.revalidate();
        //            previousForm.show(); //drop any changes
        //        });
        {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        //DELETE
        toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
            DAO.getInstance().delete(workSlot);
            previousForm.refreshAfterEdit();
//            previousForm.revalidate();
            previousForm.showBack(); //drop any changes
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        toolbar.addCommandToSideMenu("New Task", icon, (e) -> {
//            Log.p("Clicked");
//            try {
//                new ScreenItemP(new Item(), form).getForm().show(); //edit new Item
//            } catch (ParseException | IOException ex) {
//                Log.p(ScreenItemP.class.getName()+ex, Log.ERROR );
//            }
//        });
//        toolbar.addCommandToOverflowMenu("Overflow", icon, (e) -> Log.p("Clicked"));
//        toolbar.addCommandToRightBar(new Command("", theme.getImage("synch.png")) {
//
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Display.getInstance().callSerially(new Runnable() {
//
//                    public void run() {
////                                updateScreenFromNetwork(cats, "cat");
////                                cats.revalidate();
//                    }
//                });
//            }
//        });
//</editor-fold>
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
    private Container buildContentPane(Container content) {
        parseIdMapReset();
//        Container content = new Container();
        if (false) {
            TableLayout tl;
//        int spanButton = 2;
            int nbFields = 8;
            if (Display.getInstance().isTablet()) {
                tl = new TableLayout(nbFields, 2);
            } else {
                tl = new TableLayout(nbFields * 2, 1);
//            spanButton = 1;
            }
            tl.setGrowHorizontally(true);
            content.setLayout(tl);
        }
        long now = System.currentTimeMillis();
        MyDateAndTimePicker startByDate = new MyDateAndTimePicker("<start work on this date>", parseIdMap2,
                () -> workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()
                ? new Date(now) : workSlot.getStartTimeD(),
                (d) -> workSlot.setStartTime(d));
//        content.add(new Label("Start by")).add(startByDate);
//        content.add(layout("Start by",startByDate, "**"));
        content.add(layoutN(WorkSlot.START_TIME, startByDate, WorkSlot.START_TIME_HELP));

        MyDurationPicker duration = new MyDurationPicker(parseIdMap2,
                () -> (workSlot.getDurationInMinutes() == 0 && MyPrefs.workSlotDefaultDuration.getInt() != 0)
                ? MyPrefs.workSlotDefaultDuration.getInt() : (int) workSlot.getDurationInMinutes(), //UI: use default workSlot duration
                (i) -> workSlot.setDurationInMinutes((int) i));
        content.add(layoutN(WorkSlot.DURATION, duration, WorkSlot.DURATION_HELP));

//        MyTextField workSlotName = new MyTextField("Description", parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s));
        MyTextField workSlotName = new MyTextField(WorkSlot.DESCRIPTION_HINT, 20, 100, 0, parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s), TextField.RIGHT);
        workSlotName.addActionListener((e) -> setTitle(workSlotName.getText())); //update the form title when text is changed
//        content.add(new Label("Description")).add(workSlotName);
        content.add(layoutN(WorkSlot.DESCRIPTION, workSlotName, WorkSlot.DESCRIPTION_HELP, true, false, false));
//        setEditOnShow(workSlotName); //UI: start editing this field, NO

//        MyTextField comment = new MyTextField("Description", parseIdMap2, () -> workSlot.getComment(), (s) -> workSlot.setComment(s));
//        content.add(new Label("Description")).add(comment);
//REPEAT RULE
        locallyEditedRepeatRule = workSlot.getRepeatRule();
//        SpanButton repeatRuleButton = new SpanButton();
        WrapButton repeatRuleButton = new WrapButton();
//        Command repeatRuleEditCmd = new Command("<click to set repeat>NOT SHOWN?!") {
        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRule", "", null, (e) -> {
            if (locallyEditedRepeatRule == null) {
                locallyEditedRepeatRule = new RepeatRuleParseObject();
            }

//                if (!locallyEditedRepeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
            if (!locallyEditedRepeatRule.canRepeatRuleBeEdited(workSlot)) {
                Dialog.show("INFO", "Once a repeating " + WorkSlot.WORKSLOT + " is in the past, the " + WorkSlot.REPEAT_DEFINITION + " definition cannot be edited anymore", "OK", null);
                return;
            }

            if (repeatRuleCopyBeforeEdit == null && workSlot.getRepeatRule() != null) {
                repeatRuleCopyBeforeEdit = workSlot.getRepeatRule().cloneMe(); //make a copy of the *original* repeatRule
            }
            ASSERT.that(workSlot.getRepeatRule() == null || (repeatRuleCopyBeforeEdit.equals(locallyEditedRepeatRule) && locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)), "problem in cloning repeatRule");

            new ScreenRepeatRuleNew(Item.REPEAT_RULE, locallyEditedRepeatRule, workSlot, ScreenWorkSlot.this, () -> {
                repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
                if (false) { //now done when exiting via parseIdMap2 below
                    workSlot.setRepeatRule(locallyEditedRepeatRule);
                    DAO.getInstance().save(locallyEditedRepeatRule);
                }
//                    repeatRuleButton.setText(getDefaultIfStrEmpty(workSlot.getRepeatRule().toString(), "<set>")); //"<click to make task/project repeat>"
                repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
                repeatRuleButton.revalidate();
//                }, false, startByDate.getDate(), true).show(); //TODO false<=>editing startdate not allowed - correct???
            }, false, startByDate.getDate(), true).show(); //TODO false<=>editing startdate not allowed - correct???
        }
        );

//        parseIdMap2.put("REPEAT_RULE", () -> {
        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
//            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit)) { //if rule was edited //NO need to test here, item.setRepeatRule() will check if the rule has changed
            if (locallyEditedRepeatRule != null && !locallyEditedRepeatRule.equals(repeatRuleCopyBeforeEdit) //if rule was edited
                    && !(workSlot.getRepeatRule() != null && repeatRuleCopyBeforeEdit == null)) { //test if repeatRule exists but was not edited (repeatRuleCopyBeforeEdit==null if rule was not edited)
                DAO.getInstance().save(locallyEditedRepeatRule); //save first to enable saving repeatInstances
                workSlot.setRepeatRule(locallyEditedRepeatRule);  //TODO!! optimize and see if there's a way to check if rule was just opened in editor but not changed
//                    repeatRuleButton.setText(getDefaultIfStrEmpty(item.getRepeatRule().toString(), "<set>")); //"<click to make task/project repeat>"
//                repeatRuleButton.setText(getDefaultIfStrEmpty(locallyEditedRepeatRule != null ? locallyEditedRepeatRule.toString() : null, "<set>")); //"<click to make task/project repeat>"
//            }
            }
        });

        repeatRuleButton.setCommand(repeatRuleEditCmd);
        repeatRuleButton.setText(getDefaultIfStrEmpty(workSlot.getRepeatRule() != null ? workSlot.getRepeatRule().toString() : null, "")); //"<click to make task/project repeat>"

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            MyTextArea owner = new MyTextArea(Item.BELONGS_TO, 20, TextArea.ANY, parseIdMap2, () -> {
//                Object ownerObj = workSlot.getOwner();
//                String ownerText = ""; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO
//                if (ownerObj != null) {
//                    if (ownerObj instanceof Item) {
//                        ownerText = Item.PROJECT + ": " + ((Item) ownerObj).getText(); //TODO only call top-level projects for "Project"?
//                    } else if (ownerObj instanceof Category) {
//                        ownerText = Category.CATEGORY + ": " + ((Category) ownerObj).getText();
//                    } else if (ownerObj instanceof ItemList) {
//                        ownerText = ItemList.ITEM_LIST + ": " + ((ItemList) ownerObj).getText();
//                    }
//                }
//                return ownerText;
//            },
//                    (d) -> {
//                        //TODO implement editing of owner directly (~Move to another project or list)
//                    });
//            owner.setEditable(false);
//        }
//</editor-fold>
        Object ownerObj = workSlot.getOwner();
        String ownerText = ""; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO 
        if (ownerObj != null) {
            if (ownerObj instanceof Item) {
                ownerText = Item.PROJECT + ": " + ((Item) ownerObj).getText(); //TODO only call top-level projects for "Project"? 
            } else if (ownerObj instanceof Category) {
                ownerText = Category.CATEGORY + ": " + ((Category) ownerObj).getText();
            } else if (ownerObj instanceof ItemList) {
                ownerText = ItemList.ITEM_LIST + ": " + ((ItemList) ownerObj).getText();
            }
        }
        Label ownerLabel = new Label(ownerText);

//        statusCont.add(new Label(Item.BELONGS_TO)).add(owner); //.add(new SpanLabel("Click to move task to other projects or lists"));
//        content.add(layout(Item.BELONGS_TO, owner, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
        content.add(layoutN(Item.BELONGS_TO, ownerLabel, Item.BELONGS_TO_HELP, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//        owner.setConstraint(TextArea.UNEDITABLE); //DOESN'T WORK        

//        repeatRuleButton.setUIID("TextField");
        content.add(layoutN(WorkSlot.REPEAT_DEFINITION, repeatRuleButton, WorkSlot.REPEAT_DEFINITION_HELP, true, false, true));//, true, false, false));
        checkDataIsCompleteBeforeExit = () -> {
            if (startByDate.getDate().getTime() == 0 ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
//            if ((startByDate.getDate().getTime() == 0 || startByDate.getDate().getTime() == now) ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
                return "Both " + WorkSlot.START_TIME + " and " + WorkSlot.DURATION + " must be defined";
            }
            return null;
        };

        //HEADER - EDIT LIST IN FULL SCREEN MODE
//                    Button editSubtasksFullScreen = ScreenListOfItems.makeSubtaskButton(item, null);
        List<Item> items = workSlot.getItemsInWorkSlot();
        if (items != null && items.size() > 0) {
            ItemList itemList = new ItemList(workSlot.getItemsInWorkSlot());
            Button editSubtasksFullScreen = new Button();
            editSubtasksFullScreen.setCommand(MyReplayCommand.create("ShowTasksInWorkSlot", items.size() + " tasks", null, (e) -> {
//                new ScreenListOfItems("Tasks in WorkSlot", itemList, ScreenWorkSlot.this, (iList) -> {
                new ScreenListOfItems("Tasks in WorkSlot", () -> new ItemList(workSlot.getItemsInWorkSlot()), ScreenWorkSlot.this, (iList) -> {
//                        item.setItemList(subtaskList);
//                        DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        myForm.refreshAfterEdit(); //necessary to update sum of subtask effort
                }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
                        | ScreenListOfItems.OPTION_NO_WORK_TIME | ScreenListOfItems.OPTION_NO_INTERRUPT | ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP
                        | ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_SELECTION_MODE
                ).show();
            }
            ));
//        content.add(layout(WorkSlot.REPEAT_DEFINITION, editSubtasksFullScreen, WorkSlot.REPEAT_DEFINITION_HELP, true, false, false));
            content.add(layoutN("Tasks in WorkSlot", editSubtasksFullScreen, "**", true, true, false));
        }
        content.add(layoutN("Unallocated time", new Label(MyDate.formatTimeDuration(workSlot.getUnallocatedTime())), "How much of this work slot is still free",
                true, true, false));
        
        if (Config.WORKTIME_TEST) {
            content.add(layoutN("WorkTimeAllocator (TEST)", new Label(workSlot.getWorkSlotAllocationsAsStringForTEST()), "**",
                    true, true, false));
        }

        Label itemObjectId = new Label(workSlot.getObjectIdP() == null ? "<set on save>" : workSlot.getObjectIdP(), "LabelFixed");
        content.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true));

        return content;
    }

    @Override
    public void saveEditedValuesLocallyOnAppExit() {
//        localSave = true;
//        putEditedValues2(parseIdMap2);
//        Storage.getInstance().writeObject(FILE_LOCAL_EDITED_WORKSLOT, item); //save 
//        localSave = false;
    }

    @Override
    public boolean restoreEditedValuesSavedLocallyOnAppExit() {
//        Item itemLS = null;
        boolean savedValues = false;
        //if editing of item was ongoing when app was stopped, then recover saved item
//        ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM) || ReplayLog.getInstance().isReplayInProgress()); //local item => replay must/should be Ongoing
//        if (ReplayLog.getInstance().isReplayInProgress() && Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM)) {
//            itemLS = (Item) Storage.getInstance().readObject(FILE_LOCAL_EDITED_ITEM); //read in when initializing the Timer - from here on it is only about saving updates
//            savedValues = true;
//        } else {
////            itemLS = this.item; //it no locally saved edits, then use item to 'feed' the edits fields
//            ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM));
//            deleteEditedValuesSavedLocallyOnAppExit();
//            savedValues = false;
//        }
//        return itemLS;
        return savedValues;
    }

    @Override
    public void deleteEditedValuesSavedLocallyOnAppExit() {
//        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_ITEM); //delete in case one was 
    }

}
