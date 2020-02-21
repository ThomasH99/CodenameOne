package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
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
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.table.TableLayout;
//import static com.todocatalyst.todocatalyst.MyForm.putEditedValues2;
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
    ItemAndListCommonInterface owner;
    static String SCREEN_TITLE = "Workslot";
    private String FILE_LOCAL_EDITED_WORKSLOT = "ScreenWorkSlot-EditedItem";
//    private RepeatRuleParseObject locallyEditedRepeatRule;
//    private RepeatRuleParseObject repeatRuleCopyBeforeEdit;
    protected static String FORM_UNIQUE_ID = "ScreenEditWorkSlot"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    private static String REPEAT_RULE_DELETED_MARKER = "REPEAT_RULE_DELETED";

//    MyDateAndTimePicker startByDate;
//    MyDurationPicker duration;
//    private UpdateField updateActionOnDone;
//    ScreenWorkSlot(WorkSlot workSlot, MyForm previousForm) { //throws ParseException, IOException {
//        this(workSlot, previousForm, null);
//    }
    ScreenWorkSlot(WorkSlot workSlot, ItemAndListCommonInterface owner, MyForm previousForm, Runnable doneAction) { //throws ParseException, IOException {
        super(SCREEN_TITLE, previousForm, doneAction);
        setUniqueFormId("ScreenEditWorkSlot");
//        ScreenItemP.item = item;
        this.workSlot = workSlot;
        this.owner = owner;
//        if (previousValues != null) {
//            this.previousValues = previousValues;
//        } else {
//        this.previousValues = new SaveEditedValuesLocally(getUniqueFormId("-" + this.workSlot.getObjectIdP()));
//        initLocalSaveOfEditedValues(getUniqueFormId() + "-" + this.workSlot.getObjectIdP());
        previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + this.workSlot.getObjectIdP());
//        }
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
//        setCheckOnExit(()->checkWorkSlotIsValidForSaving(this.workSlot, owner));
//        buildContentPane(getContentPane());
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
        buildContentPane(getContentPane());
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

    /**
     * validates a workSlot before saving, checks that both startDate and
     * duration are defined and that it doesn't overlap with other of the
     * owner's existing workslots. Returns null if no error, otherwise an error
     * message string to display.
     */
    public static boolean checkWorkSlotIsValidForSaving(ItemAndListCommonInterface owner, WorkSlot orgUneditedWorkSlot, Date startByDate, long duration) {
//    public static boolean checkWorkSlotIsValidForSaving(WorkSlot workSlot, ItemAndListCommonInterface owner) {//, Date startByDate, int duration) {
        List<WorkSlot> overlapping;
        String errorMsg = null;
        if (startByDate.getTime() == 0 || duration == 0) { // ^ XOR - if one and only one is true
//        if (workSlot.getStartTimeD().getTime() == 0 || workSlot.getDurationInMillis() == 0) {
//            if ((startByDate.getDate().getTime() == 0 || startByDate.getDate().getTime() == now) ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
            errorMsg = "Both " + WorkSlot.START_TIME + " and " + WorkSlot.DURATION + " must be defined";
        } else if ((overlapping = owner.getOverlappingWorkSlots(new WorkSlot(startByDate, duration))) != null
                && !(overlapping.size() == 1 && overlapping.contains(orgUneditedWorkSlot))) { //to avoid error if an edited workSlot overlaps with its previous values
//        } else if (owner!=null && (overlapping = owner.getOverlappingWorkSlots(workSlot)) != null) {
            errorMsg = ("This workslot overlaps with \n"
                    + getListAsSeparatedString(overlapping,
                            (ws)
                            -> //WorkSlot.WORKSLOT +" "+
                            MyDate.formatDateTimeNew(((WorkSlot) ws).getStartTimeD())
                            + " " + MyDate.formatDurationShort(((WorkSlot) ws).getDurationInMillis()), "\n", 2)
                    + (overlapping.size() > 2 ? "and " + (overlapping.size() - 2) + " more..." : "")
                    + "\nPlease change " + WorkSlot.START_TIME + " or " + WorkSlot.DURATION + ", or Cancel");
        }
        if (errorMsg != null) {
            Dialog.show("Error", errorMsg, "OK", null);
            return false;
        } else {
            return true;
        }
    }

    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);
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
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand(true)); //, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            List<WorkSlot> overlapping;
////            if (startByDate.getDate().getTime() == 0 ^ duration.getDuration() == 0) { // ^ XOR - if one and only one is true
//            if (startByDate.getDate().getTime() == 0 || duration.getDuration() == 0) {
////            if ((startByDate.getDate().getTime() == 0 || startByDate.getDate().getTime() == now) ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
//                return "Both " + WorkSlot.START_TIME + " and " + WorkSlot.DURATION + " must be defined";
//            } else if ((overlapping = owner.getOverlappingWorkSlots(new WorkSlot(startByDate.getDate(),duration.getDuration()))) != null) {
//                return ("This workslot overlaps with \n"
//                        + getListAsSeparatedString(overlapping,
//                                (ws)
//                                -> //WorkSlot.WORKSLOT +" "+
//                                MyDate.formatDateNew(((WorkSlot) ws).getStartTimeD())
//                                +" "+ MyDate.formatDurationShort(((WorkSlot) ws).getDurationInMillis()), "\n", 2)
//                        + (overlapping.size() > 2 ? "and "+(overlapping.size()-2)+" more..." : "")
//                        +"\nPlease change " + WorkSlot.START_TIME + " or " + WorkSlot.DURATION + ", or Cancel");
//            } else
//                return (String) null;
//            return checkWorkSlotIsValidForSaving(new WorkSlot(startByDate.getDate(), duration.getDuration()), owner);
//        }));
//</editor-fold>

        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) { //        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> { //DONE!! replace with default Cancel command MyForm.makeCancelCommand()??
//<editor-fold defaultstate="collapsed" desc="comment">
//            Log.p("Clicked");
////            item.revert(); //forgetChanges***/refresh
////            previousForm.showBack(); //drop any changes
//            previousForm.revalidate();
//            previousForm.show(); //drop any changes
//        });
//</editor-fold>
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        //DELETE
        toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            Log.p("Clicked");
//            item.revert(); //forgetChanges***/refresh
//            previousForm.showBack(); //drop any changes
//            DAO.getInstance().delete(workSlot);
//            previousForm.refreshAfterEdit();
////            previousForm.revalidate();
//            previousForm.showBack(); //drop any changes
//</editor-fold>
//            workSlot.softDelete();
            DAO.getInstance().delete(workSlot, false, true);
            showPreviousScreen(true);
        });

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("WorkSlotSettings", "Settings", Icons.iconSettingsLabelStyle, (e) -> {
            new ScreenSettingsWorkSlot(ScreenWorkSlot.this, () -> {
                refreshAfterEdit();
            }).show();
        }
        ));

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
        parseIdMap2.parseIdMapReset();
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
        long now = MyDate.currentTimeMillis();
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDateAndTimePicker startByDate = new MyDateAndTimePicker("<start work on this date>", parseIdMap2,
//                () -> workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()
//                ? new Date(now) : workSlot.getStartTimeD(),
//                (d) -> workSlot.setStartTime(d));
//</editor-fold>
//        Date defaultDate = (MyPrefs.workSlotDefaultStartDateIsNow.getBoolean() ? MyDate.getStartOfMinute(new Date(now)) : new Date(0));
        GetVal makeDefaultWorkSlotStartDate = () -> (MyPrefs.workSlotDefaultStartDateIsNow.getBoolean() ? MyDate.getStartOfMinute(new Date(now)) : new Date(0));
        MyDateAndTimePicker startByDate = new MyDateAndTimePicker();
        initField(WorkSlot.PARSE_START_TIME, startByDate,
                //                () -> ((workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()) ? 
                //                        new Date(now) 
                //                        : workSlot.getStartTimeD()),
                () -> workSlot.getStartTimeD(),
                (d) -> workSlot.setStartTime((Date) d),
                () -> startByDate.getDate(),
                (d) -> startByDate.setDate((Date) d),
                new Date(0), //round to nearest minute to avoid seconds showing
                makeDefaultWorkSlotStartDate);
//        content.add(new Label("Start by")).add(startByDate);
//        content.add(layout("Start by",startByDate, "**"));
        content.add(layoutN(WorkSlot.START_TIME, startByDate, WorkSlot.START_TIME_HELP));

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDurationPicker duration = new MyDurationPicker(parseIdMap2,
//                () -> (workSlot.getDurationInMinutes() == 0 && MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0)
//                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() : (int) workSlot.getDurationInMinutes(), //UI: use default workSlot duration
//                (i) -> workSlot.setDurationInMinutes((int) i));
//</editor-fold>
        MyDateAndTimePicker endByDate = new MyDateAndTimePicker();
        MyDurationPicker duration = new MyDurationPicker();
        duration.addActionListener(e -> {
            endByDate.setDate(new Date(startByDate.getDate().getTime() + duration.getDuration()));
        });
//        Long defaultDuration = new Long((MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0
//                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS
//                : 0));
        GetVal makeDefaultDuration = () -> new Long((MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0
                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS
                : 0));
//<editor-fold defaultstate="collapsed" desc="comment">
//           initField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
//                () -> item.getRemainingEffort(false), (l) -> item.setRemaining((long) l, false),
//                () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDurationInMillis((long) l));
//</editor-fold>
        initField(WorkSlot.PARSE_DURATION, duration,
                //                () -> ((workSlot.getDurationInMinutes() == 0
                //                && MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0)
                //                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS
                //                : workSlot.getDurationInMillis()),
                () -> workSlot.getDurationInMillis(),
                (l) -> workSlot.setDurationInMillis((long) l),
                () -> duration.getDuration(),
                (l) -> duration.setDuration((long) l),
                new Long(0),
                makeDefaultDuration);
        duration.setMinuteStep(MyPrefs.workSlotDurationStepIntervalInMinutes.getInt());

        content.add(layoutN(WorkSlot.DURATION, duration, WorkSlot.DURATION_HELP));

        endByDate.addActionListener(e -> {
            if (endByDate.getDate().getTime() < startByDate.getDate().getTime()) {
                endByDate.setDate(new Date(startByDate.getDate().getTime()));
            } else {
                duration.setDuration(endByDate.getDate().getTime() - startByDate.getDate().getTime());
            }
        });
        initField("RandomUnusedKey", endByDate, //random key because the value is never used directly, only in the UI to set Duration
                //                () -> ((workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()) ? 
                //                        new Date(now) 
                //                        : workSlot.getStartTimeD()),
                () -> workSlot.getEndTimeD(),
                (d) -> {
                },
                () -> null,
                (d) -> endByDate.setDate((Date) d),
                new Date(0),
                () -> {
                    long defaultEndTime = startByDate.getDate().getTime() != 0 ? startByDate.getDate().getTime() : ((Date) makeDefaultWorkSlotStartDate.getVal()).getTime()
                    + duration.getTime() != 0 ? duration.getTime() : ((Date) makeDefaultDuration.getVal()).getTime();
                    return new Date(defaultEndTime);
                });
//        content.add(new Label("Start by")).add(startByDate);
//        content.add(layout("Start by",startByDate, "**"));
        content.add(layoutN(WorkSlot.END_TIME, endByDate, WorkSlot.END_TIME_HELP));

//REPEAT RULE
        WrapButton repeatRuleButton = new WrapButton();
        repeatRuleButton.setName("RepeatBut");
        repeatRuleButton.getTextComponent().setName("RepeatBut");

        //set text for edit-RR button
        ActionListener refreshRepeatRuleButton = e -> {
            RepeatRuleParseObject locallyEditedRepeatRule1 = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);
            String repeatRuleButtonStr;
            if (locallyEditedRepeatRule1 == null) { //no edits
                if (workSlot.getRepeatRule() != null) {
                    repeatRuleButtonStr = workSlot.getRepeatRule().getText();
                } else {
                    repeatRuleButtonStr = "";
                }
            } else if (locallyEditedRepeatRule1.equals(REPEAT_RULE_DELETED_MARKER)) {
                repeatRuleButtonStr = "";
            } else { //if (editedRepeatRule instanceof RepeatRuleParseObject) { //NB instanceof RepeatRuleParseObject is only option possible
                assert locallyEditedRepeatRule1 instanceof RepeatRuleParseObject;
                repeatRuleButtonStr = locallyEditedRepeatRule1.getText();
//                ((RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE)).setSpecifiedStartDate(startByDate.getDate()); //update RR to same startTime as set for the WorkSlot
            }
            repeatRuleButton.setText(repeatRuleButtonStr);
        };

        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRule-ScreenWorkSlot", "", null, (e) -> {

            if (workSlot.getRepeatRule() != null && !workSlot.getRepeatRule().canRepeatRuleBeEdited(workSlot)) {
                Dialog.show("INFO", Format.f("Once a repeating task has been set {0 DONE} or {1 CANCELLED} the {2 REPEAT_RULE} definition cannot be edited from this task anymore",
                        ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), Item.REPEAT_RULE), "OK", null);
                return;
            }
            //only allow editing RR if startDate is set
//            if (workSlot.getStartTimeD().getTime() == 0) {
            if (startByDate.getDate().getTime() == 0) {
                Dialog.show("INFO", Format.f("Please set {0 start date} before editing the {1 REPEAT_RULE} definition",
                        WorkSlot.START_TIME, Item.REPEAT_RULE), "OK", null);
                return;
            }

            RepeatRuleParseObject locallyEditedRepeatRuleCopy;
            RepeatRuleParseObject localRR = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);

            if (localRR != null) {// || previousValues.get(Item.PARSE_REPEAT_RULE).equals(REPEAT_RULE_DELETED_MARKER)) {
                locallyEditedRepeatRuleCopy = localRR;
            } else {
//                locallyEditedRepeatRule = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE); //fetch previously edited instance/copy of the repeat Rule
                if (workSlot.getRepeatRule() == null) {
                    locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(); //create a fresh RR
//                    locallyEditedRepeatRuleCopy.addOriginatorToRule(workSlot); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                } else {
                    locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(workSlot.getRepeatRule()); //create a copy if getRepeatRule returns a rule, if getRepeatRule() returns null, creates a fresh RR
                    ASSERT.that(workSlot.getRepeatRule() == null || (workSlot.getRepeatRule().equals(locallyEditedRepeatRuleCopy) && locallyEditedRepeatRuleCopy.equals(workSlot.getRepeatRule())), "problem in cloning repeatRule");
                }
                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy);
            }

            new ScreenRepeatRule(Item.REPEAT_RULE + " " + WorkSlot.WORKSLOT, locallyEditedRepeatRuleCopy, workSlot, ScreenWorkSlot.this, () -> {
                if (false) {
                    if (locallyEditedRepeatRuleCopy.equals(workSlot.getRepeatRule())) {
                        previousValues.remove(Item.PARSE_REPEAT_RULE);
                    } else if (locallyEditedRepeatRuleCopy.getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
                        previousValues.put(Item.PARSE_REPEAT_RULE, REPEAT_RULE_DELETED_MARKER);
                    } else {
                        previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy);
                    }
                }
                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy); //store edited rule (otherwise not persisted in local memory)

            }, false, startByDate.getDate(), makeDefaultWorkSlotStartDate, true).show(); //TODO false<=>editing startdate not allowed - correct???
        }
        );

        repeatRuleButton.setCommand(repeatRuleEditCmd);
        refreshRepeatRuleButton.actionPerformed(null);

        parseIdMap2.put(REPEAT_RULE_KEY, () -> {
            Object editedRule = previousValues.get(Item.PARSE_REPEAT_RULE);
            if (editedRule instanceof RepeatRuleParseObject) { //only defined if the RR has really been edited
                ((RepeatRuleParseObject) editedRule).addOriginatorToRule(workSlot); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                workSlot.setRepeatRule((RepeatRuleParseObject) editedRule);
            }
        });

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
        ItemAndListCommonInterface ownerObj = workSlot.getOwner();
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
//        checkDataIsCompleteBeforeExit = () -> {
//        setCheckOnExit( () -> {
//            if (startByDate.getDate().getTime() == 0 ^ duration.getDuration() == 0) { // ^ XOR - if one and only one is true
////            if ((startByDate.getDate().getTime() == 0 || startByDate.getDate().getTime() == now) ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
//                return "Both " + WorkSlot.START_TIME + " and " + WorkSlot.DURATION + " must be defined";
//            }
//            return null;
//        });

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
        if (workSlot.getObjectIdP() != null) { //don't show for a new created workslot
//        content.add(layoutN("Unallocated time", new Label(MyDate.formatDurationStd(workSlot.getRemainingAvailableTime(now))), "How much of this work slot is still free",
            content.add(layoutN("Available time", new Label(MyDate.formatDurationStd(workSlot.getRemainingAvailableTime(now))), "How much of this work slot is still free",
                    true, true, false));
        }

        if (Config.WORKTIME_TEST) {
            content.add(layoutN("WorkTimeAllocator (TEST)", new Label(workSlot.getWorkSlotAllocationsAsStringForTEST()), "**",
                    true, true, false));
        }

        if ((Config.TEST || MyPrefs.showObjectIdsInEditScreens.getBoolean()) && workSlot.getObjectIdP() != null) {
            Label itemObjectId = new Label(workSlot.getObjectIdP() == null ? "<set on save>" : workSlot.getObjectIdP(), "LabelFixed");
            content.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true));
        }

        //ORIGINAL SOURCE
        if (Config.TEST) { //hide source except when testing since purpose has to be clarified  
            if (workSlot.getSource() != null && MyPrefs.showSourceWorkSlotInEditScreens.getBoolean()) { //don't show unless defined
                //TODO!! what happens if source is set to template or other item, then saved locally on app exit and THEN recreated via Replay???
//            Label sourceLabel = new Label(itemLS.getSource() == null ? "" : item.getSource().getText(), "LabelFixed");
                WorkSlot workSlotSource = (WorkSlot) workSlot.getSource();
                String text = workSlotSource.getText();
                if (text == null || text.isEmpty()) {
                    text = MyDate.formatDateTimeNew(workSlotSource.getStartTimeD())
                            + " " + MyDate.formatDuration(workSlotSource.getDurationInMillis(), true)
                            + " [" + workSlotSource.getObjectIdP() + "]";
                }
                SpanLabel sourceLabel = new SpanLabel(text, "LabelFixed");
//            statusCont.add(new Label(Item.SOURCE)).add(source); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, source, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, sourceLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
                content.add(layoutN(Item.SOURCE, sourceLabel, Item.SOURCE_HELP, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            }
        }

//        MyTextField workSlotName = new MyTextField("Description", parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s));
        MyTextField workSlotName = new MyTextField(WorkSlot.DESCRIPTION_HINT, 20, 100, 0, parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s), TextField.RIGHT);
        workSlotName.addActionListener((e) -> {
            String text = workSlotName.getText();
            Item.EstimateResult estim = Item.getEffortEstimateFromTaskText(text);
            if (estim != null) {
                text = estim.cleaned;
                workSlotName.setText(text);
                if (estim.minutes != 0) {
                    duration.setDurationAndNotify(estim.minutes * MyDate.MINUTE_IN_MILLISECONDS); //notify to save local values!
                }
            }
            setTitle(SCREEN_TITLE + (text != null && text.length() > 0 ? " " + text : ""));
        }
        ); //update the form title when text is changed
//        content.add(new Label("Description")).add(workSlotName);
//        content.add(layoutN(WorkSlot.DESCRIPTION, workSlotName, WorkSlot.DESCRIPTION_HELP, null, false, false, false, true));
//        content.add(layoutN("", workSlotName, WorkSlot.DESCRIPTION_HELP, null, false, false, false, true));
        initField(Item.PARSE_TEXT, workSlotName,
                () -> workSlot.getText(),
                (t) -> workSlot.setText((String) t),
                () -> workSlotName.getText(),
                (t) -> workSlotName.setText((String) t));
        workSlotName.setUIID("ScreenItemTaskText");
        content.add(workSlotName);
        workSlotName.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!

//        setEditOnShow(workSlotName); //UI: start editing this field, NO
//        MyTextField comment = new MyTextField("Description", parseIdMap2, () -> workSlot.getComment(), (s) -> workSlot.setComment(s));
//        content.add(new Label("Description")).add(comment);
        setCheckIfSaveOnExit(() -> checkWorkSlotIsValidForSaving(ownerObj, workSlot, startByDate.getDate(), duration.getDuration())); //TODO: when owner can be edited, use new/edited one
        return content;
    }

}
