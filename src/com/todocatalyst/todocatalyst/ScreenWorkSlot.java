package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
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
//    private String lastFieldSetManually = ""; //whenever setting a new field, duration, endDate, startDate, the 3rd one should be updated based on the current and last set
    private final static String DURATION = "duration"; //whenever setting a new field, duration, endDate, startDate, the 3rd one should be updated based on the current and last set
    private final static String END_DATE = "endDate"; //whenever setting a new field, duration, endDate, startDate, the 3rd one should be updated based on the current and last set
    private final static String START_DATE = "startDate"; //whenever setting a new field, duration, endDate, startDate, the 3rd one should be updated based on the current and last set

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
        String prevValId = this.workSlot.getObjectIdP() != null ? this.workSlot.getObjectIdP() : ("From-" + previousForm.getUniqueFormId());
        addUpdateActionOnDone(() -> {
            if (this.workSlot.hasSaveableData()) {
                if (workSlot.hasUserModifiedData()) {
                    workSlot.setEditedDateToNow();
                }
                this.owner.addWorkSlot(this.workSlot);
                DAO.getInstance().saveToParseNow(this.workSlot);
            }
        });
//        previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + this.workSlot.getObjectIdP());
        previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + prevValId);
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        setCheckOnExit(()->checkWorkSlotIsValidForSaving(this.workSlot, owner));
//        buildContentPane(getContentPane());
//        if (false) {
//            setUpdateActionOnDone(() -> {
//                //
//                RepeatRuleParseObject repeatRule = this.workSlot.getRepeatRuleN();
//                if (repeatRule != null) {
//                    repeatRule.updateWorkslotInstancesWhenWorkSlotModified(workSlot);
//                }
//            });
//        }
//</editor-fold>
        refreshAfterEdit();
    }

//    @Override
//    protected void updateOnExit() {
//        super.updateOnExit();
//        if (workSlot.hasUserModifiedData()) {xxx;
//            workSlot.setEditedDate(new MyDate());
//        }
//        DAO.getInstance().saveToParseNow(workSlot);
//    }
    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommandsNO_EFFECT(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
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
        if (startByDate.getTime() == 0 ^ duration == 0) { // ^ XOR - if one and only one is true (OK to exit if both are zero, no workshop
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
        toolbar.addCommandToLeftBar(makeDoneUpdateWithParseIdMapCommand()); //, () -> {
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

        //DELETE
        Command deleteCmd = CommandTracked.createMaterial("Delete", Icons.iconDelete, (e) -> {
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
        toolbar.addCommandToOverflowMenu(deleteCmd);

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("WorkSlotSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsWorkSlot(ScreenWorkSlot.this, () -> {
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) { //        toolbar.addCommandToOverflowMenu("Cancel", null, (e) -> { //DONE!! replace with default Cancel command MyForm.makeCancelCommand()??
            //should always be OK to cancel editing a workslot
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
//        lastFieldSetManually = "";
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container content = new Container();
//        if (false) {
//            TableLayout tl;
////        int spanButton = 2;
//            int nbFields = 8;
//            if (Display.getInstance().isTablet()) {
//                tl = new TableLayout(nbFields, 2);
//            } else {
//                tl = new TableLayout(nbFields * 2, 1);
////            spanButton = 1;
//            }
//            tl.setGrowHorizontally(true);
//            content.setLayout(tl);
//        }
//</editor-fold>

        boolean hideIcons = MyPrefs.hideIconsInEditTaskScreen.getBoolean();

        //OWNER
        ItemAndListCommonInterface ownerObj = workSlot.getOwner();
        String ownerText = ""; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO 
        String ownerLabelTxt = Item.BELONGS_TO; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO 
        if (ownerObj != null) {
            ownerText = ownerObj.getText(); //TODO only call top-level projects for "Project"? 
            if (ownerObj instanceof Item) {
//                ownerText = Item.PROJECT + ": " + ((Item) ownerObj).getText(); //TODO only call top-level projects for "Project"? 
                ownerLabelTxt = Item.BELONGS_TO_PROJECT;
            } else if (ownerObj instanceof Category) {
//                ownerText = Category.CATEGORY + ": " + ((Category) ownerObj).getText();
                ownerLabelTxt = Item.BELONGS_TO_CATEGORY;
            } else if (ownerObj instanceof ItemList) {
//                ownerText = ItemList.ITEM_LIST + ": " + ((ItemList) ownerObj).getText();
                ownerLabelTxt = Item.BELONGS_TO_LIST;
            }
        }
        SpanLabel ownerLabel = new SpanLabel(ownerText);
        //NB!! If adding support for EDITING owner, don't allow it for repeating rules!

//        statusCont.add(new Label(Item.BELONGS_TO)).add(owner); //.add(new SpanLabel("Click to move task to other projects or lists"));
//        content.add(layout(Item.BELONGS_TO, owner, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
        content.add(layoutN(ownerLabelTxt, ownerLabel, Item.BELONGS_TO_HELP, true, hideIcons ? null : Icons.iconOwner)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//        owner.setConstraint(TextArea.UNEDITABLE); //DOESN'T WORK        

        long now = MyDate.currentTimeMillis();
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDateAndTimePicker startByDate = new MyDateAndTimePicker("<start work on this date>", parseIdMap2,
//                () -> workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()
//                ? new Date(now) : workSlot.getStartTimeD(),
//                (d) -> workSlot.setStartTime(d));
//        Date defaultDate = (MyPrefs.workSlotDefaultStartDateIsNow.getBoolean() ? MyDate.getStartOfMinute(new Date(now)) : new Date(0));
//</editor-fold>
        //STARTTIME
        GetVal makeDefaultWorkSlotStartDate
                = () -> (MyPrefs.workSlotDefaultStartDateIsNow.getBoolean() ? MyDate.roundUpToNextMinute(new MyDate(now)) : new MyDate(0));
        MyDateAndTimePicker startByDatePicker = new MyDateAndTimePicker();
        initField(WorkSlot.PARSE_START_TIME, startByDatePicker,
                //                () -> ((workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()) ? 
                //                        new Date(now) 
                //                        : workSlot.getStartTimeD()),
                () -> workSlot.getStartTimeD(),
                (d) -> workSlot.setStartTime((Date) d),
                () -> startByDatePicker.getDate(),
                (d) -> startByDatePicker.setDate((Date) d),
                new MyDate(0), //round to nearest minute to avoid seconds showing
                makeDefaultWorkSlotStartDate);
//        content.add(new Label("Start by")).add(startByDate);
//        content.add(layout("Start by",startByDate, "**"));
        content.add(layoutN(WorkSlot.START_TIME, startByDatePicker, WorkSlot.START_TIME_HELP, hideIcons ? null : Icons.iconWorkSlotStartTime));
//        content.add(new SwipeableContainer(null, new Button(Command.create("Tst", null, (e) -> Log.p("Test"))), new Label("TestSwipe"))); //, true, false, false));

//<editor-fold defaultstate="collapsed" desc="comment">
//        MyDurationPicker duration = new MyDurationPicker(parseIdMap2,
//                () -> (workSlot.getDurationInMinutes() == 0 && MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0)
//                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() : (int) workSlot.getDurationInMinutes(), //UI: use default workSlot duration
//                (i) -> workSlot.setDurationInMinutes((int) i));
//</editor-fold>
        //DURATION
        MyDurationPicker durationPicker = new MyDurationPicker();
        durationPicker.setMinuteStep(MyPrefs.workSlotDurationStepIntervalInMinutes.getInt());

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
        initField(WorkSlot.PARSE_DURATION, durationPicker,
                //                () -> ((workSlot.getDurationInMinutes() == 0
                //                && MyPrefs.workSlotDefaultDurationInMinutes.getInt() != 0)
                //                ? MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS
                //                : workSlot.getDurationInMillis()),
                () -> workSlot.getDurationInMillis(),
                (l) -> workSlot.setDurationInMillis((long) l),
                () -> durationPicker.getDuration(),
                (l) -> durationPicker.setDuration((long) l),
                new Long(0),
                makeDefaultDuration);
        content.add(layoutN(WorkSlot.DURATION, durationPicker, WorkSlot.DURATION_HELP, hideIcons ? null : Icons.iconWorkSlotDuration));

        //END TIME
        MyDateAndTimePicker endByDatePicker = new MyDateAndTimePicker();
//<editor-fold defaultstate="collapsed" desc="comment">
        if (false) {
            initField("RandomUnusedKey", endByDatePicker, //random key because the value is never used directly, only in the UI to set Duration
                    //                () -> ((workSlot.getStartTimeD().getTime() == 0 && MyPrefs.workSlotDefaultStartDateIsNow.getBoolean()) ?
                    //                        new Date(now)
                    //                        : workSlot.getStartTimeD()),
                    () -> new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()), //workSlot.getEndTimeD(),
                    (d) -> { //we never use the value other than to update start or duration pickers
                    },
                    () -> null,
                    (d) -> endByDatePicker.setDate((Date) d),
                    new MyDate(0),
                    () -> {
//                    long defaultEndTime = startByDatePicker.getDate().getTime() != 0 ? startByDatePicker.getDate().getTime() : ((Date) makeDefaultWorkSlotStartDate.getVal()).getTime()
//                    + durationPicker.getTime() != 0 ? durationPicker.getTime() : ((Date) makeDefaultDuration.getVal()).getTime();
//                    return new Date(defaultEndTime);
                        return null; //should never be called?!
                    });
        }
//</editor-fold>
        if (startByDatePicker.getValue() != null && startByDatePicker.getDate().getTime() != 0) {
            endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration())); //simply set to start+duration pickers (even when zero or have locally stored values)
        } else {
            endByDatePicker.setDate(new MyDate(0)); //simply set to start+duration pickers (even when zero or have locally stored values)
        }//        content.add(new Label("Start by")).add(startByDate);
//        content.add(layout("Start by",startByDate, "**"));
        content.add(layoutN(WorkSlot.END_TIME, endByDatePicker, WorkSlot.END_TIME_HELP, hideIcons ? null : Icons.iconWorkSlotEndTime));

        //ACTION LISTENERS to ensure consistence between start time, duration and end time
        if (false) {
            durationPicker.addActionListener(e -> {
                if (durationPicker.getDuration() != 0) { //if date is set
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (lastFieldSetManually.equals(START_DATE)) {
//                    endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
//                } else if (lastFieldSetManually.equals(END_DATE) && endByDatePicker.getDate().getTime() != 0) {
//                    startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
//                } else if (startByDatePicker.getDate().getTime() != 0 && endByDatePicker.getDate().getTime() != 0) {
//                    //UI: if both start and enddate are defined, keep startdate and update enddate
//                    endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
//                }
//
//                if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean()) {
//                    if (startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
//                        //IF start is in the past and this is now allowed, move it to 'now' and update endDate to start+duration
//                        startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
//                        endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
//                    }
//                }
//
//                if (false && endByDatePicker.getDate().getTime() < startByDatePicker.getDate().getTime()) { //if already sat endDate is before new startDate
//                    //set endDate to startDate + already set duration or default duration
//                    endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime()
//                            + durationPicker.getDuration() != 0 ? durationPicker.getDuration()
//                            : MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS));
//                }
//                if (lastFieldSetManually.equals(START_DATE)) {
//                    endByDatePicker.setDate(new MyDate(0));
//                } else if (lastFieldSetManually.equals(END_DATE)) {
//                    startByDatePicker.setDate(new MyDate(0));
//                } else if (startByDatePicker.getDate().getTime() != 0) {//&& endByDatePicker.getDate().getTime() != 0) {
//                    //UI: if both start and enddate are defined, keep startdate and clear enddate
//                    endByDatePicker.setDate(new MyDate(0));
//                }
//            }
//</editor-fold>
                    if (startByDatePicker.getDate().getTime() != 0) {
                        endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
                    } else if (endByDatePicker.getDate().getTime() != 0) { //=>startDate==0
                        startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
                        if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean()) {
                            if (startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
                                //IF start is in the past and this is now allowed, move it to 'now' and update endDate to start+duration
//                            startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
                                startByDatePicker.setDateAndNotify(new MyDate(MyDate.currentTimeMillis()));
//                            endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
                                endByDatePicker.setDateAndNotify(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
                            }
                        }
                    } else if (startByDatePicker.getDate().getTime() != 0 && endByDatePicker.getDate().getTime() != 0) {
                        //UI: if both start and enddate are defined, keep startdate and update enddate
//                    endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
                        endByDatePicker.setDateAndNotify(new MyDate(startByDatePicker.getDate().getTime() + durationPicker.getDuration()));
                    }

                } else { //if duration is cleared
                    if (startByDatePicker.getDate().getTime() != 0) {//&& endByDatePicker.getDate().getTime() != 0) {
//                    endByDatePicker.setDate(new MyDate(0));
                        endByDatePicker.setDateAndNotify(new MyDate(0));
                    } //else: do nothing
                }
            });
        }

        durationPicker.addActionListener(e -> {
            if (workSlot.getStartTimeD().getTime() == 0) { //no start time set yet, set to default
                workSlot.setStartTime((Date) makeDefaultWorkSlotStartDate.getVal());
                startByDatePicker.setDate(workSlot.getStartTimeD());
            }
            workSlot.setDurationInMillis(durationPicker.getDuration());
            endByDatePicker.setDate(workSlot.getEndTimeD());
        });

        startByDatePicker.addActionListener(e -> {
            if (workSlot.getStartTimeD().getTime() != 0) { //if date is set
                if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean()) {
                    if (startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
                        startByDatePicker.setDate(new MyDate()); //force to now
                    }
                }
                workSlot.setStartTime(startByDatePicker.getDate());

                if (workSlot.getDurationInMinutes() == 0 && MyPrefs.workSlotDefaultDurationInMinutes.getInt() > 0) {
                    workSlot.setDurationInMinutes(MyPrefs.workSlotDefaultDurationInMinutes.getInt());
                }
            } else { //field was cleared, also clear endDate if set
                if (endByDatePicker.getDate().getTime() != 0) {
//                    endByDatePicker.setDate(new MyDate(0));
                    endByDatePicker.setDateAndNotify(new MyDate(0));
                }
//                lastFieldSetManually = ""; //if startDate was cleared, then leave the previously set field as lastSet
            }
            durationPicker.setDuration(workSlot.getDurationInMillis());
            endByDatePicker.setDateAndNotify(workSlot.getEndTimeD());
        }
        );

        if (false) {
            endByDatePicker.addActionListener(e -> {
                if (endByDatePicker.getDate() != null && endByDatePicker.getDate().getTime() != 0) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (lastFieldSetManually.equals(DURATION)) {
//                    startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
//                } else if (lastFieldSetManually.equals(START_DATE) && startByDatePicker.getDate().getTime() != 0) { //durationPicker.getDuration() != 0 in case it was set but then cleared
//                    durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
//                } else if (durationPicker.getDuration() != 0 && startByDatePicker.getDate().getTime() != 0) {
//                    //if startDate is in the past, adjust it to now *before* calculating duration
//                    if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
//                        startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
//                    }
//                    //adjust duration if both duration and startDate were previously set (e.g. editing an existing workslot)
//                    durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
//                } else if (MyPrefs.workSlotDefaultDurationInMinutes.getInt() > 0) {
//                    //UI: if default duration is defined, used it to derive startDate (e.g. use case: you adjust the workslot to match a start+end time like in a calendar
//                    durationPicker.setDuration(MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
//                    startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
//                }
//</editor-fold>
                    //if both startDate and duration are set, adjust duration (unintuitive to adjust startDate, even if Duration is the last set)
                    if (startByDatePicker.getDate().getTime() != 0) {//&& durationPicker.getDuration() != 0) {
                        //if end_date is in the past and not allowed, adjust it to now (>=startTime also now => duration=0)
                        if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && endByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
                            endByDatePicker.setDate(new MyDate()); //if workslots may not start in the past, adjust to now
                        }
                        //update duration (to 0 if endDate is before startDate)
                        durationPicker.setDuration(Math.max(0, endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime()));
                        //if endDate is *before* startDate, set startDate to endDate (=>duration=0)
                        if (endByDatePicker.getDate().getTime() < startByDatePicker.getDate().getTime()) {
//                        startByDatePicker.setDate(endByDatePicker.getDate());
                            startByDatePicker.setDateAndNotify(endByDatePicker.getDate());
                        }
                    } else if (durationPicker.getDuration() != 0) { //=>startDate==0
                        startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
                        //if startDate is in the past, adjust it to now *before* calculating duration
                        if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
//                        startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
                            startByDatePicker.setDateAndNotify(new MyDate(MyDate.currentTimeMillis()));
//                        durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
                            durationPicker.setDurationAndNotify(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
                        }
                    } else if (MyPrefs.workSlotDefaultDurationInMinutes.getInt() > 0) {
                        //neither startDate, nor duration were set
//                    ASSERT.that(startByDatePicker.getDate().getTime() == 0);
                        //UI: if default duration is defined, used it to derive startDate (e.g. use case: you adjust the workslot to match a start+end time like in a calendar
//                    durationPicker.setDuration(MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
                        durationPicker.setDurationAndNotify(MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS);
//                    startByDatePicker.setDate(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
                        startByDatePicker.setDateAndNotify(new MyDate(endByDatePicker.getDate().getTime() - durationPicker.getDuration()));
                        //if startDate is in the past, adjust it to now *before* calculating duration
                        if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
//                        startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
                            startByDatePicker.setDateAndNotify(new MyDate(MyDate.currentTimeMillis()));
//                        durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
                            durationPicker.setDurationAndNotify(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
                        }
                    }
//                lastFieldSetManually = END_DATE;
                } else { //endDate was cleared
                    //if endDate is cleared, then if startDate is defined, keep startDate and clear duration
                    if (startByDatePicker.getDate().getTime() != 0) {//if startDate is in the past, adjust it to now *before* calculating duration
                        if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && startByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) {
//                        startByDatePicker.setDate(new MyDate(MyDate.currentTimeMillis()));
                            startByDatePicker.setDateAndNotify(new MyDate(MyDate.currentTimeMillis()));
                        }
//                    durationPicker.setDuration(0);
                        durationPicker.setDurationAndNotify(0);
                    }
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (startByDatePicker.getDate().getTime() != 0 && durationPicker.getDuration() != 0) {
//                if (MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean()) {
//                    if (endByDatePicker.getDate().getTime() < startByDatePicker.getDate().getTime()) { //if endDate set before startDate, set endDate to startDate
//                        endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime()));
//                    } else {
//                        durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
//                    }
//                } else {
//                    if (endByDatePicker.getDate().getTime() < startByDatePicker.getDate().getTime()) { //if endDate set before startDate, set endDate to startDate
//                        endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime()));
//                    } else {
//                        durationPicker.setDuration(endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime());
//                    }
//
//                }
//            } else { //if startByDatePicker OR durationPicker not set yet, set startTime to endTime - default duration
//                if (durationPicker.getDuration() != 0) {
//                    long newStartDate = endByDatePicker.getDate().getTime() - durationPicker.getDuration();
//                    if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && newStartDate < MyDate.currentTimeMillis()) {
//                        newStartDate = MyDate.currentTimeMillis();
//                        durationPicker.setDuration(Math.max(0, endByDatePicker.getDate().getTime() - newStartDate));
//                    }
//                    startByDatePicker.setDate(new MyDate(newStartDate));
//                } else if (startByDatePicker.getDate().getTime() != 0) {
//                    long newDuration =
//                    if (!MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean() && newStartDate < MyDate.currentTimeMillis()) {
//                        if (endByDatePicker.getDate().getTime() < startByDatePicker.getDate().getTime()) { //if endDate set before startDate, set endDate to startDate
//                            endByDatePicker.setDate(new MyDate(startByDatePicker.getDate().getTime()));
//                        } else {
//                            durationPicker.setDuration(Math.max(0, endByDatePicker.getDate().getTime() - startByDatePicker.getDate().getTime()));
//                        }
//                    }
//                }
//            }
//</editor-fold>
            });
        }
        endByDatePicker.addActionListener(e -> {
            if (workSlot.getStartTimeD().getTime() == 0 && workSlot.getDurationInMillis() == 0) { //UI: if we click set end time first, prioritize setting default Duration 
                workSlot.setDurationInMillis((Long) makeDefaultDuration.getVal());
            }
            ASSERT.that(workSlot.getStartTimeD().getTime() != 0 || workSlot.getDurationInMillis() != 0);
            if (MyPrefs.workSlotsMayBeCreatedInThePast.getBoolean()) {
                workSlot.setEndTime(endByDatePicker.getDate(), true); //keep duration
            } else {
                if (endByDatePicker.getDate().getTime() < MyDate.currentTimeMillis()) { //if end date is in the past, start from now
                    workSlot.setStartTime(new MyDate());
                    endByDatePicker.setDate(workSlot.getEndTimeD()); //if workslots may not start in the past, adjust to now
                } else {
                    workSlot.setEndTime(endByDatePicker.getDate(), false); //adjust duration
                }
            }
            durationPicker.setDuration(workSlot.getDurationInMillis());
            startByDatePicker.setDate(workSlot.getStartTimeD());
        });

//REPEAT RULE
        SpanButton repeatRuleButton = new SpanButton();

        repeatRuleButton.setName("RepeatBut");
        repeatRuleButton.getTextComponent().setName("RepeatBut");

        //set text for edit-RR button
        ActionListener refreshRepeatRuleButtonXXX = e -> {
            RepeatRuleParseObject locallyEditedRepeatRule1 = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);
            String repeatRuleButtonStr;
            if (locallyEditedRepeatRule1 == null) { //no edits
                if (workSlot.getRepeatRuleN() != null) {
                    repeatRuleButtonStr = workSlot.getRepeatRuleN().getText();
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
        repeatRuleButton.setText(workSlot.getRepeatRuleN() != null ? workSlot.getRepeatRuleN().getText() : "");

        Command repeatRuleEditCmd = MyReplayCommand.create("EditRepeatRule-ScreenWorkSlot", "", null, (e) -> {
            if (false) {
                if (workSlot.getRepeatRuleN() != null && !workSlot.getRepeatRuleN().canRepeatRuleBeEdited(workSlot)) {
                    Dialog.show("INFO", Format.f("Once a repeating task has been set {0 DONE} or {1 CANCELLED} the {2 REPEAT_RULE} definition cannot be edited from this task anymore",
                            ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), Item.REPEAT_RULE), "OK", null);
                    return;
                }
            }
            //only allow editing RR if startDate is set
//            if (workSlot.getStartTimeD().getTime() == 0) {
            if (false && startByDatePicker.getDate().getTime() == 0) {
                Dialog.show("INFO", Format.f("Please set {0 start date} before editing the {1 REPEAT_RULE} definition",
                        WorkSlot.START_TIME, Item.REPEAT_RULE), "OK", null);
                return;
            }
            if (false) {
                RepeatRuleParseObject locallyEditedRepeatRuleCopy;
                RepeatRuleParseObject localRR = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE);

                if (localRR != null) {// || previousValues.get(Item.PARSE_REPEAT_RULE).equals(REPEAT_RULE_DELETED_MARKER)) {
                    locallyEditedRepeatRuleCopy = localRR;
                } else {
//                locallyEditedRepeatRule = (RepeatRuleParseObject) previousValues.get(Item.PARSE_REPEAT_RULE); //fetch previously edited instance/copy of the repeat Rule
                    if (workSlot.getRepeatRuleN() == null) {
                        locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(); //create a fresh RR
//                    locallyEditedRepeatRuleCopy.addOriginatorToRule(workSlot); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                    } else {
                        locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(workSlot.getRepeatRuleN()); //create a copy if getRepeatRule returns a rule, if getRepeatRule() returns null, creates a fresh RR
                        ASSERT.that(workSlot.getRepeatRuleN() == null || (workSlot.getRepeatRuleN().equals(locallyEditedRepeatRuleCopy) && locallyEditedRepeatRuleCopy.equals(workSlot.getRepeatRuleN())), "problem in cloning repeatRule");
                    }
                    previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy);
                }
            }
            RepeatRuleParseObject locallyEditedRepeatRuleCopy = new RepeatRuleParseObject(workSlot.getRepeatRuleN()); //create a copy if getRepeatRule returns a rule, if getRepeatRule() returns null, creates a fresh RR

            new ScreenRepeatRule(Item.REPEAT_RULE + " " + WorkSlot.WORKSLOT, locallyEditedRepeatRuleCopy, workSlot, ScreenWorkSlot.this, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    if (locallyEditedRepeatRuleCopy.equals(workSlot.getRepeatRuleN())) {
//                        previousValues.remove(Item.PARSE_REPEAT_RULE);
//                    } else if (locallyEditedRepeatRuleCopy.getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
//                        previousValues.put(Item.PARSE_REPEAT_RULE, REPEAT_RULE_DELETED_MARKER);
//                    } else {
//                        previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy);
//                    }
//                }
//</editor-fold>
//                previousValues.put(Item.PARSE_REPEAT_RULE, locallyEditedRepeatRuleCopy); //store edited rule (otherwise not persisted in local memory)

                if (workSlot.getStartTimeD().getTime() == 0) {
                    workSlot.setStartTime(locallyEditedRepeatRuleCopy.getFirstRepeatDateAfterTodayForWhenEditingRuleWithoutPredefinedDueDateN()); //replace/set locally edited value for Due so when ScreenItem2 is refreshed this value is used to set the picker
                }
                workSlot.setRepeatRule(locallyEditedRepeatRuleCopy);
                DAO.getInstance().saveToParseAndWait(locallyEditedRepeatRuleCopy); //MUST save here to itemOrg can be saved locally without ref's to unsaved objects
                previousValues.saveElementToSaveLocally();//update locally stored element to include RR

            }, false, startByDatePicker.getDate(), makeDefaultWorkSlotStartDate, true).show(); //TODO false<=>editing startdate not allowed - correct???
        }
        );

        repeatRuleButton.setCommand(repeatRuleEditCmd);

        if (false) {
            refreshRepeatRuleButtonXXX.actionPerformed(null);
        }

        if (false) {
            parseIdMap2.put(REPEAT_RULE_KEY,
                    () -> {
                        Object editedRule = previousValues.get(Item.PARSE_REPEAT_RULE);
                        if (editedRule instanceof RepeatRuleParseObject) { //only defined if the RR has really been edited
                            if (false) {
                                ((RepeatRuleParseObject) editedRule).addOriginatorToRule(workSlot); //NB! item could possibly be done (marked as Done when edited, or editing a Done item to make it repeat from now on)
                            }
                            workSlot.setRepeatRule((RepeatRuleParseObject) editedRule);
                        } else {//if RR was NOT edited, but workslot (potentially) was, updated already generated instances
                            RepeatRuleParseObject repeatRule = workSlot.getRepeatRuleN();
                            if (repeatRule != null) {
                                repeatRule.updateWorkslotInstancesWhenWorkSlotModified(workSlot);
                            }
                        }

                    }
            );
        }

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
//        repeatRuleButton.setUIID("TextField");
//        content.add(layoutN(true, WorkSlot.REPEAT_DEFINITION, repeatRuleButton, WorkSlot.REPEAT_DEFINITION_HELP, true, false, true, hide ? null : Icons.iconRepeat));//, true, false, false));
        content.add(layoutN(true, WorkSlot.REPEAT_DEFINITION, repeatRuleButton, WorkSlot.REPEAT_DEFINITION_HELP, hideIcons ? null : Icons.iconRepeat));//, true, false, false));
//        checkDataIsCompleteBeforeExit = () -> {
//        setCheckOnExit( () -> {
//            if (startByDate.getDate().getTime() == 0 ^ duration.getDuration() == 0) { // ^ XOR - if one and only one is true
////            if ((startByDate.getDate().getTime() == 0 || startByDate.getDate().getTime() == now) ^ duration.getTime() == 0) { // ^ XOR - if one and only one is true
//                return "Both " + WorkSlot.START_TIME + " and " + WorkSlot.DURATION + " must be defined";
//            }
//            return null;
//        });

        //DESCRIPTION
//        MyTextField workSlotName = new MyTextField("Description", parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s));
//        MyTextField workSlotName = new MyTextField(WorkSlot.DESCRIPTION_HINT, 20, 100, 0, parseIdMap2, () -> workSlot.getText(), (s) -> workSlot.setText(s), TextField.RIGHT);
        MyTextField workSlotName = new MyTextField(WorkSlot.DESCRIPTION_HINT, 20, 100, 0, null, () -> workSlot.getText(), (s) -> workSlot.setText(s), TextField.RIGHT);

        workSlotName.addActionListener(
                (e) -> {
                    String text = workSlotName.getText();
                    Item.EstimateResult estim = Item.getEffortEstimateFromTaskText(text);
                    if (estim != null) {
                        text = estim.cleaned;
                        workSlotName.setText(text);
                        if (estim.minutes != 0) {
                            durationPicker.setDurationAndNotify(estim.minutes * MyDate.MINUTE_IN_MILLISECONDS); //notify to save local values!
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
        workSlotName.setUIID(
                "ScreenItemTaskText");
        content.add(workSlotName);

        workSlotName.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //start with initial caps automatically - TODO!!!! NOT WORKING LIKE THIS!!

        //TASKS IN WORKSLOT
        //HEADER - EDIT LIST IN FULL SCREEN MODE
//                    Button editSubtasksFullScreen = ScreenListOfItems.makeSubtaskButton(item, null);
        List<Item> items = workSlot.getItemsInWorkSlot();
        if (items
                != null && items.size()
                > 0) {
            ItemList itemList = new ItemList(workSlot.getItemsInWorkSlot());
            Button editSubtasksFullScreen = new Button();
            editSubtasksFullScreen.setCommand(MyReplayCommand.create("ShowTasksInWorkSlot", items.size() + " tasks", null, (e) -> {
//                new ScreenListOfItems("Tasks in WorkSlot", itemList, ScreenWorkSlot.this, (iList) -> {
                new ScreenListOfItems("Tasks in WorkSlot", () -> new ItemList(workSlot.getItemsInWorkSlot()), ScreenWorkSlot.this, null, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_NEW_BUTTON | ScreenListOfItems.OPTION_NO_TIMER
                        | ScreenListOfItems.OPTION_NO_WORK_TIME | ScreenListOfItems.OPTION_NO_INTERRUPT | ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP
                        | ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_SELECTION_MODE
                ).show();
            }
            ));
//        content.add(layout(WorkSlot.REPEAT_DEFINITION, editSubtasksFullScreen, WorkSlot.REPEAT_DEFINITION_HELP, true, false, false));
//            content.add(layoutN("Tasks in WorkSlot", editSubtasksFullScreen, "**", true, true, false));
            content.add(layoutN("Tasks in WorkSlot", editSubtasksFullScreen, "**", hideIcons ? null : Icons.iconWorkSlotTasks));
        }

        Label lastEditedDate = new Label(workSlot.getEditedDate().getTime() == 0 ? "" : MyDate.formatDateTimeNew(workSlot.getEditedDate()));
        content.add(layoutN(Item.EDITED_DATE, lastEditedDate, Item.EDITED_DATE_HELP, true, hideIcons ? null : Icons.iconEditedDate, Icons.myIconFont));

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
                SpanLabel sourceLabel = new SpanLabel(text, "ScreenItemValueUneditable");
//            statusCont.add(new Label(Item.SOURCE)).add(source); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, source, "**", true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//            statusCont.add(layout(Item.SOURCE, sourceLabel, "**", true, true, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//                content.add(layoutN(Item.SOURCE, sourceLabel, Item.SOURCE_HELP, true)); //.add(new SpanLabel("Click to move task to other projects or lists"));
//                content.add(layoutN(true, Item.SOURCE, sourceLabel, Item.SOURCE_HELP, true, true, false)); //.add(new SpanLabel("Click to move task to other projects or lists"));
                content.add(layoutN(Item.SOURCE, sourceLabel, Item.SOURCE_HELP, null, true, true, false, false, true, hideIcons ? null : Icons.iconSource)); //.add(new SpanLabel("Click to move task to other projects or lists"));
            }
        }

        //CREATED
        Label createdDate = new Label(workSlot.getCreatedAt().getTime() == 0 ? "" : MyDate.formatDateTimeNew(workSlot.getCreatedAt().getTime())); //NOT use itemLS since CreatedDate is not saved locally
        content.add(layoutN(Item.CREATED_DATE, createdDate, "**", true, hideIcons ? null : Icons.iconCreatedDate));

        //MODIFIED
        Label lastModifiedDate = new Label(workSlot.getUpdatedAt().getTime() == 0 ? "" : MyDate.formatDateTimeNew(workSlot.getUpdatedAt()));
        content.add(layoutN(Item.UPDATED_DATE, lastModifiedDate, Item.UPDATED_DATE_HELP, true, hideIcons ? null : Icons.iconModifiedDate));

        //OBJECT-ID
        if ((Config.TEST
                || (MyPrefs.enableShowingSystemInfo.getBoolean()
                && MyPrefs.showObjectIdsInEditScreens.getBoolean())) && workSlot.getObjectIdP() != null) {
            Label itemObjectId = new Label(workSlot.getObjectIdP() == null ? "<set on save>" : workSlot.getObjectIdP(), "ScreenItemValueUneditable");
            content.add(layoutN(Item.OBJECT_ID, itemObjectId, Item.OBJECT_ID_HELP, true, hideIcons ? null : Icons.iconObjectId));
        }

        //WORKTIME ALLOCATOR
        if (Config.WORKTIME_TEST) {
            content.add(layoutN("WorkTimeAllocator (TEST)", new SpanLabel(workSlot.getWorkSlotAllocationsAsStringForTEST()), "**",
                    true, true, false));
        }

//        setEditOnShow(workSlotName); //UI: start editing this field, NO
//        MyTextField comment = new MyTextField("Description", parseIdMap2, () -> workSlot.getComment(), (s) -> workSlot.setComment(s));
//        content.add(new Label("Description")).add(comment);
        setCheckIfSaveOnExit(
                () -> checkWorkSlotIsValidForSaving(ownerObj, workSlot, startByDatePicker.getDate(), durationPicker.getDuration())); //TODO: when owner can be edited, use new/edited one
        return content;
    }

}
