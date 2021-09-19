/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
public class ScreenRepeatRule extends MyForm {

    //TODO 
    //TODO add help text to repeatFromCompleted to explain that cancel/delete also counts as 'Completed'
    //TODO selectors like monthlyRepeatTypeSelectionBox should NOT allow to deselect all choices (should work as radiobuttons: exactly one is selected at all times(
    protected static String FORM_UNIQUE_ID = "ScreenEditRepeatRule"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

    /**
     * set to true if repeatrule is the simplified version used for workslots
     */
    private boolean isForWorkSlot;
    /**
     * shorthand for the container holding all the fields
     */
//    private Container container;
    private Container repeatRuleDetailsContainerXXX;
//    MyDate defaultRepeatFromDate;
    private boolean allowEditingStartDateXXX;
//    private SelectionListener refreshSelectionListener;
    private ActionListener refreshActionListener;
    private RepeatRuleParseObject repeatRuleEdited;
//    private RepeatRuleParseObject myRepeatRule;
    private MyDatePicker repeatEndDateEditButton;
    private Component repeatEndDateEditButtonCont;
//    MyDate endDate;
    private Date endDate;
    private Date itemDueDate;
    private GetVal makeStartDate;
    /**
     * holds the item that is currently being edited (from which the edited
     * repeatRule comes). Passed in display()
     */
    private RepeatRuleObjectInterface repeatRuleOwner;
    /**
     * starting date for repeat sequence. Usually Due date from Item. Can be
     * edited here?!
     */
//    MyDate repeatStartDate;
    private MyDateAndTimePicker repeatStartDatePickerXXX;
    private Label repeatStartDateLabel;
//    ComboBoxLogicalNames repeatFromDueOrCompletedField;
//    private MyToggleButton repeatFrom_NoneCompletedDue_FieldT;
    private MyComponentGroup repeatFrom_NoneCompletedDue_Field;
    private Component repeatFrom_NoneCompletedDue_FieldCont;
    private Container repeatFromDueOrCompletedFieldContainer;
//    ComboBoxOffset repeatHowManyTimesField;
//    MyIntPicker repeatHowManyTimesField;
//    private MyIntTextField repeatHowManyTimesField;
    private MyIntPicker repeatHowManyTimesField;
    private Component repeatHowManyTimesFieldCont;
//    private ComboBoxOffset intervalField;
//    private ListModelInfinite intervalFieldInfiniteList;
//    private MyIntTextField intervalField_1_NN_TextField;
    private MyIntPicker intervalField_1_NN_TextField;
    private Component intervalField_1_NN_TextFieldCont;
//    ComboBoxOffset daysAheadField;
//    private MyIntPicker daysAheadField;
//    ComboBoxLogicalMultiSelection daysInWeekField;
//    private MyToggleButton daysInWeek_MonSun_Field;
    private MyComponentGroup daysInWeek_MonSun_Field;
//    private MyComponentGroup daysInWeek_MonSun_Field;
    private Component daysInWeek_MonSun_FieldCont;
//    ComboBoxLogicalMultiSelection daysInMonthField;
//    private MyToggleButton daysInMonth_MonSunWeekdays_Field;
    private MyComponentGroup daysInMonth_MonSunWeekdays_Field;
    private Component daysInMonth_MonSunWeekdays_FieldCont;
//    ComboBoxLogicalMultiSelection weeksInMonthField;
//    private MyToggleButton weeksInMonth_1st2ndLast_Field;
    private MyComponentGroup weeksInMonth_1st2ndLast_Field;
    private Component weeksInMonth_1st2ndLast_FieldCont;
//    ComboBoxLogicalMultiSelection weekdaysInMonthField;
//    private MyToggleButton weekdaysInMonth_1st2nLast_Field;
    private MyComponentGroup weekdaysInMonth_1st2nLast_Field;
    private Component weekdaysInMonth_1st2nLast_FieldCont;
//    ComboBoxLogicalMultiSelection monthsInYearField;
//    private MyToggleButton monthsInYear_JanDec_Field;
    private MyComponentGroup monthsInYear_JanDec_Field;
    private Component monthsInYear_JanDec_FieldCont;
//    ComboBoxLogicalNames monthlyRepeatTypeSelectionBox;
//    private MyToggleButton monthlyRepeatType_DayWeekdaysWeeks_SelectionBox;
    private MyComponentGroup monthlyRepeatType_DayWeekdaysWeeks_SelectionBox;
    private Component monthlyRepeatType_DayWeekdaysWeeks_SelectionBoxCont;
//    ComboBoxLogicalNames frequencyField;
//    private MyToggleButton frequency_DailyWeekMonthYearly_Field;
    private MyComponentGroup frequency_DailyWeekMonthYearly_Field;
    private Component frequency_DailyWeekMonthYearly_FieldCont;
    private ComboBoxOffsetNew dayInMonth_1_31_Last_FieldFM;
    private Component dayInMonth_1_31_Last_FieldFMCont;
//    TextField dayInYear_1_365_Field;
    private MyIntPicker dayInYear_1_365_Field;
    private Component dayInYear_1_365_FieldCont;
//    ComboBoxOffset showNumberFutureRepeats;
    private MyIntPicker showNumberFutureRepeats;
    private Component showNumberFutureRepeatsCont;
//    ComboBoxOffset showNumberDaysAhead;
    private MyIntPicker showNumberDaysAheadZZZ;
//    ComboBoxLogicalNames repeatHowLongChoiceCombo;
//    private MyToggleButton repeatHowLong_ForeverUntilNumber_ChoiceCombo;
    private MyComponentGroup repeatHowLong_ForeverUntilNumber_ChoiceCombo;
    private Component repeatHowLong_ForeverUntilNumber_ChoiceComboCont;
//    ComboBoxLogicalNames yearlyChoiceCombo;
//    private MyToggleButton yearlyChoice_DayMonths_Combo;
    private MyComponentGroup yearlyChoice_DayMonths_Combo;
    private Component yearlyChoice_DayMonths_ComboCont;
//    private Container showHowManyContainer;
//    private Component showHowManyContainer;
//    ComboBoxLogicalNames showHowManyCombo;
//    private MyToggleButton showHowMany_InstancesDaysAhead_ComboZZZ;
    private MyComponentGroup showHowMany_InstancesDaysAhead_ComboZZZ;
    private Button showDatesButton;
    private SpanLabel internalData;
    private MyOnOffSwitch onCompletionDatedRepeatsSwitch;
    private Component onCompletionDatedRepeatsCont;

    private boolean scrollEndRepeatFieldsToVisible;
//<editor-fold defaultstate="collapsed" desc="comment">
//    MyCommand commandDeleteRepeatRule = new MyCommand("Delete Repeat") {
//        public void actionPerformed(ActionEvent evt) {
//
////            myRepeatRule.deleteRuleAndAllRepeatInstancesExceptThis(repeatRuleOwner); //delete rule and all other instances than this one (currently edited)
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(repeatRuleOwner); //delete rule and all other instances than this one (currently edited)
//            myRepeatRule = null;
////            screenArg.done(ScreenRepeatRule.this, null, true); //UI: as soon as RepeatRule is deleted, exit the ScreenRepeatRule
//            done(ScreenRepeatRule.this, null, true); //UI: as soon as RepeatRule is deleted, exit the ScreenRepeatRule
//            popMeAndDisplayPreviousForm();
//        }
//    };

//    public ScreenRepeatRule(String title, MyRepeatRule repeatRule) {
//        this(title, repeatRule, null);
//    }
//    public ScreenRepeatRule(RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOwner) {
//        this("Repeat", repeatRule, repeatRuleOwner, true);
//    }
//
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOwner) {
//        this(title, repeatRule, repeatRuleOwner, true);
//    }
//    public ScreenRepeatRule(String title, MyRepeatRule repeatRule, MyDate defaultRepeatFromDate, boolean allowEditingStartDate) {
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOwner, boolean allowEditingStartDate) {
//</editor-fold>
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm, UpdateField doneAction, boolean allowEditingStartDate) {
//        this(title, repeatRule, null, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, null);
//    }
//
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleParseObject repeatRuleEdited, RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm, UpdateField doneAction, boolean allowEditingStartDate, Date defaultStartDate) {
//        this(title, repeatRule, repeatRuleEdited, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, defaultStartDate, false);
//    }
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule,
//            RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm,
//            UpdateField doneAction, boolean allowEditingStartDate, Date defaultStartDate, boolean isForWorkSlot) {
//        this(title, repeatRule, null, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, defaultStartDate, isForWorkSlot);
//    }
//    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleParseObject repeatRuleEdited,
    /**
     *
     * @param title
     * @param repeatRule
     * @param repeatRuleOriginator
     * @param previousForm
     * @param doneAction
     * @param allowEditingStartDateXXX
     * @param itemDueDate default starting date (copy of item's due date)
     * @param makeStartDate function to make a new starting date in case it is
     * needed and none was provided by defaultStartDate
     * @param isForWorkSlot
     */
    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm,
            //            UpdateField doneAction, boolean allowEditingStartDate,  GetVal makeStartDate, boolean isForWorkSlot) {
            Runnable doneAction, boolean allowEditingStartDateXXX, Date itemDueDate, GetVal makeStartDate, boolean isForWorkSlot) {
//        super(title, repeatRule);
        super(title, previousForm, doneAction);
        setUniqueFormId("ScreenEditRepeatRule");
//        this.defaultRepeatFromDate = defaultRepeatFromDate;
//        this.repeatRuleOwner = (RepeatRuleObject) repeatRule.getOwner();
        this.repeatRuleOwner = repeatRuleOriginator;
        this.allowEditingStartDateXXX = allowEditingStartDateXXX;
//        this.myRepeatRule = (RepeatRuleParseObject) value;
        this.repeatRuleEdited = repeatRule;
        if (false && repeatRuleEdited == null) { //should never happen
            this.repeatRuleEdited = new RepeatRuleParseObject();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.repeatRuleEdited = this.repeatRuleEdited; //the editable/edited copy of the repeatRule, used to edit only the end-user edtaible fields
//        if (false) {
//            if (repeatRuleEdited == null)
//                this.repeatRuleEdited = repeatRule; //the editable/edited copy of the repeatRule, used to edit only the end-user edtaible fields
//            else
//                this.repeatRuleEdited = repeatRuleEdited; //the editable/edited copy of the repeatRule, used to edit only the end-user edtaible fields
//        }
//        if (repeatRuleEdited != null && (this.startDate = repeatRuleEdited.getSpecifiedStartDateD()).getTime() == 0 && defaultStartDate!=null) {
//            this.startDate = (Date)defaultStartDate.getVal(); //only use defaultStartDate if rule doesn't have one already
////            this.startDate = myRepeatRule.getSpecifiedStartDateD(); //use rule's existing startDate if it has one
////        } else {
////            this.startDate = defaultStartDate; //only use startDate if rule doesn't have one already
//        }
//        this.defaultStartDate = defaultStartDate == null ? new Date(0) : new Date(defaultStartDate.getTime()); //only use defaultStartDate if rule doesn't have one already
//</editor-fold>
        this.itemDueDate = itemDueDate; //now: use only to initialize pickers based on an existing due date. [only use defaultStartDate if rule doesn't have one already]
        if (false && this.itemDueDate == null) {
            this.itemDueDate = new MyDate(0); //avoid having to test for null values everywhere
        }
        this.makeStartDate = makeStartDate; //only use defaultStartDate if rule doesn't have one already
        this.isForWorkSlot = isForWorkSlot;
//        this.repeatStartDatePicker = startDatePicker;
//        mySetup();
        addCommandsToToolbar(getToolbar());//, theme);
//        buildContentPane(getContentPane());
//        setupLayoutAndFields();
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
//         getContentPane().removeAll();
//         buildContentPane(getContentPane());
        setupLayoutAndFields();
        super.refreshAfterEdit();
//        restoreKeepPos();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void mySetupXXX() {
////        setStdCancelDoneCommands();
//        setScrollableY(true);
////        setScrollable(false);
//        removeAllCommands();
////        addCommands(new Command[]{commandDeleteRepeatRule, commandCancel, commandDone});
////        addCommands(new Command[]{commandDeleteRepeatRule, commandCancel, commandBack});
//        setupLayoutAndFields();
//    }
//</editor-fold>
    public boolean checkRepeatRuleIsValid(boolean noMissingFields) {
        String errorMsg = noMissingFields ? null : "Missing selection in one or more choices";
        if (errorMsg != null) {
            Dialog.show("Error", errorMsg, "OK", null);
            return false;
        } else {
            return true;
        }
    }

    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

        super.addCommandsToToolbar(toolbar);
        //DONE
//<editor-fold defaultstate="collapsed" desc="comment">
//        Command cmd = makeDoneUpdateWithParseIdMapCommand();
//        Command cmd = new Command(null, Icons.iconBackToPrevFormToolbarStyle()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
//                if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) { //TODO optimization: inefficient to restore twice just to test the rule
//                    restoreEditedFieldsToRepeatRule(myRepeatRuleEdited);
//                    DAO.getInstance().save((ParseObject) repeatRuleOwner); //if a new Item, must save before creating repeatInstances in putEditedValues2:
//                    putEditedValues2(parseIdMap2);
////                    updateActionOnDone.update();
//                    getUpdateActionOnDone().update();
////                    showPreviousScreenOrDefault(previousForm, true); //false);
//                    showPreviousScreenOrDefault(true); //false);
//                } else {
//                    Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
//                }
//            }
//        };
//        cmd.putClientProperty("android:showAsAction", "withText");
//        toolbar.addCommandToLeftBar(cmd);
//</editor-fold>
        if (false) { //check done below in Back cmd
            setCheckIfSaveOnExit(() -> checkRepeatRuleIsValid(restoreEditedFieldsToRepeatRule(repeatRuleEdited)));
        }
//        Command cmd = makeDoneUpdateWithParseIdMapCommand(true);
//        cmd.putClientProperty("android:showAsAction", "withText");
//        toolbar.addCommandToLeftBar(cmd);
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand("", true,
//                () -> checkRepeatRuleIsValid(restoreEditedFieldsToRepeatRule(repeatRuleEdited)),
//                false)); //false: don't refresh ScreenItem when returning from Category selector
        addStandardBackCommand();
        setCheckIfSaveOnExit(() -> checkRepeatRuleIsValid(restoreEditedFieldsToRepeatRule(repeatRuleEdited)));

        //DELETE
//<editor-fold defaultstate="collapsed" desc="comment">
//TODO!! not needed, two options to delete: swipeClear or set RR to 'None'
//        if (false) {
//            if (repeatRuleEdited != null) {
//                toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
//                    repeatRuleEdited.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(repeatRuleOwner); //delete rule and all other instances than this one (currently edited)
//                    repeatRuleEdited = null;
////                    showPreviousScreenOrDefault(previousForm, false);
//                    showPreviousScreen(false);
//                });
//            }
//        }
//</editor-fold>
        //SHOW TASKS
        if (false) {
            if (repeatRuleEdited != null) {
                if (isForWorkSlot) {
                    toolbar.addCommandToOverflowMenu(CommandTracked.createMaterial("Show workslots", Icons.iconShowGeneratedTasks, (e) -> {
                        Container tasks = new Container();
                        //TODO!!! as popup dialog, show [3 completed >] task1 task2 task3
                        for (Object slot : repeatRuleEdited.getListOfUndoneInstances()) {
                            if (slot instanceof WorkSlot) {
                                tasks.add(((WorkSlot) slot).getText());
                            }
                        }
//                Command exit = Command.create("Exit", null, (evt) -> {  });
//                Command exit = Command.create("Exit", null, (evt) -> {  });
                        Dialog.show("WorkSlots", tasks, new Command("Exit"), new Command("Past " + repeatRuleEdited.getTotalNumberOfInstancesGeneratedSoFar() + " WorkSlots"));
                        Dialog.show("WorkSlots", "", "OK", "Exit");
                    }));
                } else {
                    toolbar.addCommandToOverflowMenu(CommandTracked.createMaterial("Show tasks", Icons.iconShowGeneratedTasks, (e) -> {
                        Container tasks = new Container();
                        //TODO!!! as popup dialog, show [3 completed >] task1 task2 task3
                        for (Object item : repeatRuleEdited.getListOfUndoneInstances()) {
                            if (item instanceof Item) {
                                tasks.add(((Item) item).getText());
                            }
                        }
                        Dialog.show("Tasks", tasks, new Command("Exit"), new Command("Past tasks"));
                    }));
                }
            }
        }

        //SIMULATE DATES
        toolbar.addCommandToOverflowMenu(CommandTracked.createMaterial("Show future dates", Icons.iconSimulateRepeatDates, (e) -> {
            RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
            if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
                tempMyRepeatRule.showRepeatDueDates();
            } else {
                Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
            }
        }));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("RepeatRuleInstancesOverview", "Show all instances", Icons.iconRepeatOverview, (e) -> {
            new ScreenRepeatRuleInstancesOverview(repeatRuleEdited, ScreenRepeatRule.this, () -> {
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("RepeatRuleSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsRepeatRules(ScreenRepeatRule.this, () -> {
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        //CANCEL
        if (true || MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        if (false && Config.TEST) {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ShowInternals", "Show internal data", Icons.iconSettings, (e) -> {
                MyPrefs.repeatShowInternalDataInRepeatScreen.flipBoolean();
            }
            ));
        }
        if (false && Config.TEST) {
            toolbar.addCommandToOverflowMenu("Run tests", null, (e) -> {
                RepeatRuleParseObject.testRepeatRules();
            });
        }

    }

    /**
     * repeat the number of instances to show in dropdown menues for each of the
     * frequencies, e.g. 520 for Weeks == 10 years
     */
//    private int getFreqNumberInstances(int freq) {
//        return (freq == RepeatRule.DAILY ? MyDate.DAYS_IN_YEAR * 10 : (freq == RepeatRule.WEEKLY ? MyDate.WEEKS_IN_YEAR * 10 : (freq == RepeatRule.MONTHLY ? MyDate.MONTHS_IN_YEAR * 10 : 10)));
//    }
    private final static int MONTHLY_OPTION_DAY = 0;
    private final static int MONTHLY_OPTION_WEEK_NB = 2;
    private final static int MONTHLY_OPTION_WEEKDAYS = 1;
    //{"day", "month(s)"}, new int[]{0, 1}
    private final static Integer YEAR_OPTION_DAY_OF_YEAR = 0;
    private final static Integer YEAR_OPTION_MONTHS = 1;
    //"forever", "until", "number"}, new int[]{0, 1, 2
    public final static int REPEAT_HOW_LONG_OPTION_FOREVER = 0;
    public final static int REPEAT_HOW_LONG_OPTION_UNTIL = 1;
    public final static int REPEAT_HOW_LONG_OPTION_NUMBER = 2;
    //    showHowMany_InstancesDaysAhead_Combo = new MyToggleButton(new String[]{"instances", "days ahead"}, new int[]{0, 1});
    private final static int SHOW_HOW_MANY_AHEAD_INSTANCES = 0;
    private final static int SHOW_HOW_MANY_AHEAD_DAYS_AHEAD = 1;

    boolean oldMode = false;

    protected void setupLayoutAndFields() {
//        if (!initialized) {
        parseIdMap2.parseIdMapReset();

//        container = new Container();//getContentPane();
//        container.setLayout(BoxLayout.y());
//        container.setScrollableY(true);
        makeContainerBoxY();
//        setLayout(BoxLayout.y());
//        setScrollableY(true);
//        motherContainer = getContentPane();
//        getContentPane().addComponent(BorderLayout.CENTER, container);

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (repeatRuleEdited != null && repeatRuleEdited.getListOfUndoneInstances().size() > 0) {
////                        SpanLabel itemHierarchyContainer = new SpanLabel(hierarchyStr);
//            repeatRuleDetailsContainer = new Container();
//            Container repeatRuleHideableDetailsContainer = new Container();
//
//            Button buttonRepeatRuleHistory = new Button();
//            repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean()); //UI: default hidden
//            buttonRepeatRuleHistory.setMaterialIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMore : Icons.iconShowLess); //switch icon
//            buttonRepeatRuleHistory.addActionListener((e) -> {
//                if (repeatRuleHideableDetailsContainer.getComponentCount() == 0) { //lazy evaluation
//                    List<RepeatRuleObjectInterface> list = repeatRuleEdited.getListOfUndoneInstances();
//                    long now = MyDate.currentTimeMillis(); //use a single value of now
//                    for (int i = 0, size = list.size(); i < size; i++) {
//                        RepeatRuleObjectInterface item = list.get(i);
//                        if (item instanceof Item) {
////                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer((Item) item, null, () -> false, null, false, null));
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer(ScreenRepeatRule.this, (Item) item, null, null));
//                        } else if (item instanceof WorkSlot) {
////                            repeatRuleHideableDetailsContainer.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) item, () -> refreshAfterEdit(), null));
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) item, ScreenRepeatRule.this, null, false, false, now));
//                        }
//                    }
////                    if (repeatRuleEdited.getLatestDateCompletedOrCancelled().getTime() != 0) {
////                        repeatRuleHideableDetailsContainer.add(Format.f("Total number of repeats {0}", "" + repeatRuleEdited.getTotalNumberOfInstancesGeneratedSoFar()));
////                    }
//                    if (repeatRuleEdited.getLatestDateCompletedOrCancelled().getTime() != 0) {
//                        repeatRuleHideableDetailsContainer.add(Format.f("Total number of repeats {0}", "" + repeatRuleEdited.getTotalNumberOfInstancesGeneratedSoFar()));
//                    }
////                    repeatRuleHideableDetailsContainer.add(Format.f("Due date of last completed task {0}", MyDate.formatDateNew(myRepeatRule.getLatestDateCompletedOrCancelled())));
//                    repeatRuleHideableDetailsContainer.add(Format.f(
//                            "Last Due date of all completed tasks {0}", MyDate.formatDateNew(repeatRuleEdited.getLatestDateCompletedOrCancelled())));
//                    repeatRuleHideableDetailsContainer.add(new Button(MyReplayCommand.create("Show all tasks", Format.f("Show all {0 getTotalNumberOfInstancesGeneratedSoFar} tasks", "" + repeatRuleEdited.getTotalNumberOfInstancesGeneratedSoFar()), null, (ev) -> {
////                            DAO.getInstance().getAllItemsForRepeatRule(myRepeatRule);
//                        new ScreenListOfItems("", new ItemList(DAO.getInstance().getAllItemsForRepeatRule(repeatRuleEdited), true), (MyForm) motherContainer.getComponentForm(), (i) -> {
//                            refreshAfterEdit();
//                        }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP | ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                                | ScreenListOfItems.OPTION_NO_INTERRUPT | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME).show();
//
//                    }))); //TODO!!!!
//                }
////                repeatRuleHideableDetailsContainer.setHidden(!repeatRuleHideableDetailsContainer.isHidden());
//                MyPrefs.repeatHidePreviousTasksDetails.flipBoolean();
//                repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean());
//                buttonRepeatRuleHistory.setMaterialIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMore : Icons.iconShowLess); //switch icon
//                animateLayout(ANIMATION_TIME_DEFAULT);
//            });
////            motherContainer.add(BorderLayout.center(FlowLayout.encloseCenter(new Label(myRepeatRule.getListOfUndoneRepeatInstances().size() + " tasks")))
////            repeatRuleDetailsContainer = BorderLayout.center(FlowLayout.encloseCenter(new Label(myRepeatRule.getListOfUndoneRepeatInstances().size() + " tasks")))
////            repeatRuleDetailsContainer = BorderLayout.center(FlowLayout.encloseCenter(new Label(Format.f("{0 total number repeats generated so far} tasks, {1 } active",
//            repeatRuleDetailsContainer = MyBorderLayout.center(FlowLayout.encloseCenter(new Label(Format.f("{0 total number repeats generated so far} tasks, {1 tasksCreatedNotDoneYet} active",
//                    "" + repeatRuleEdited.getTotalNumberOfInstancesGeneratedSoFar(),
//                    "" + repeatRuleEdited.getListOfUndoneInstances().size()))))
//                    .add(MyBorderLayout.EAST, buttonRepeatRuleHistory).add(MyBorderLayout.SOUTH, repeatRuleHideableDetailsContainer);
//        }
//</editor-fold>
//        daysInWeekField = new ComboBoxLogicalMultiSelection(new int[]{RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY}, new String[]{MyDate.MONDAY, MyDate.TUESDAY, MyDate.WEDNESDAY, MyDate.THURSDAY, MyDate.FRIDAY, MyDate.SATURDAY, MyDate.SUNDAY});
        //Repeat from "completion date", "due date", "today"
        if (!isForWorkSlot) {
//            repeatFromDueOrCompletedField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleTypeNumbers(), MyRepeatRule.getRepeatRuleTypeNames(), myRepeatRule.getRepeatFromDueOrCompleted()); //"completion date", "due date", "today"
//            repeatFrom_NoneCompletedDue_FieldT = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleTypeNames(),
//                    RepeatRuleParseObject.getRepeatRuleTypeNumbers(), repeatRuleEdited.getRepeatType()); //"completion date", "due date", "today"
            repeatFrom_NoneCompletedDue_Field = new MyComponentGroup(RepeatRuleParseObject.getRepeatRuleTypeNumbersAsObjects(),
                    RepeatRuleParseObject.getRepeatRuleTypeNames(),
                    //                    repeatRuleEdited.getRepeatType(), false, false);
                    repeatRuleEdited.getRepeatType());
            if (oldMode) {
//                repeatFrom_NoneCompletedDue_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_TYPE, repeatFrom_NoneCompletedDue_Field, RepeatRuleParseObject.REPEAT_RULE_TYPE_HELP, true);
                repeatFrom_NoneCompletedDue_Field.setHidden(true);
            } else {
                repeatFrom_NoneCompletedDue_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_TYPE, repeatFrom_NoneCompletedDue_Field,
                        RepeatRuleParseObject.REPEAT_RULE_TYPE_HELP);
            }
        } else { //WORKSLOT
//<editor-fold defaultstate="collapsed" desc="comment">
//            repeatFromDueOrCompletedField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleTypeNumbers(), new String[]{"inactive** date", "start date"}, myRepeatRule.getRepeatFromDueOrCompleted());
//            repeatFrom_CompletedDue_Field = new MyToggleButton(new String[]{"inactive** date", "start date"}, RepeatRuleParseObject.getRepeatRuleTypeNumbers(), myRepeatRule.getRepeatType());
//set to Due and hide the field
//            repeatFrom_NoneCompletedDue_Field = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleTypeNames(),
//                    RepeatRuleParseObject.getRepeatRuleTypeNumbers(), RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE); //"completion date", "due date", "today"
//            repeatFrom_NoneCompletedDue_Field = new MyComponentGroup(RepeatRuleParseObject.getRepeatRuleTypeNumbersAsObjects(),
//                    RepeatRuleParseObject.getRepeatRuleTypeNames(),
//                    repeatRuleEdited.getRepeatType());
//            repeatFrom_NoneCompletedDue_Field = new MyComponentGroup(RepeatRuleParseObject.getRepeatRuleTypeNumbersAsObjects(),
//                    RepeatRuleParseObject.getRepeatRuleTypeNames(),
//                    RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE); //always set to FROM_DUE for workslot repeats
//</editor-fold>
            repeatFrom_NoneCompletedDue_Field = new MyComponentGroup(
                    new Object[]{RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT, RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE},
                    new String[]{RepeatRuleParseObject.REPEAT_RULE_NO_REPEAT, RepeatRuleParseObject.REPEAT_RULE_WORKSLOT},
                    repeatRuleEdited.getRepeatType()); //always set to FROM_DUE for workslot repeats
            if (oldMode) {
//                repeatFrom_NoneCompletedDue_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_TYPE, repeatFrom_NoneCompletedDue_Field, RepeatRuleParseObject.REPEAT_RULE_TYPE_HELP);
            } else {
                repeatFrom_NoneCompletedDue_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_TYPE, repeatFrom_NoneCompletedDue_Field,
                        RepeatRuleParseObject.REPEAT_RULE_TYPE_HELP);
//                repeatFrom_NoneCompletedDue_FieldCont.setHidden(true);
            }
        }
//        repeatFrom_CompletedDue_Field.setContainer(new Container(new GridLayout(2)));

//<editor-fold defaultstate="collapsed" desc="comment">
//        refreshSelectionListener = new SelectionListener() {
//            public void selectionChanged(int oldSelected, int newSelected) {
//                if (oldSelected != newSelected) {
////                    updateFields(); //refresh the layout based on changes in choice
//                    fieldsShow();
//                }
////                if (oldSel != newSel && newSel == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE && repeatStartDatePicker != null && repeatStartDatePicker.getDate().getTime() == 0) {
////                    Dialog.show("INFO", "Please set the " + Item.DUE_DATE, "OK", null);
////                    repeatStartDatePicker.setHidden(false);
////                    animateHierarchy(300);
////                }
//            }
//        };
//</editor-fold>
        refreshActionListener = (e) -> fieldsShow();;

//        repeatStartDatePicker = new MyDateAndTimePicker(startDate, null); //"<no Start date**>"
//        repeatStartDatePicker = new MyDateAndTimePicker((Date) makeStartDate.getVal()); //"<no Start date**>"
//        repeatStartDatePicker = new MyDateAndTimePicker(makeStartDate); //makeStartDate: NOT good: will only set default value once pressed
//        repeatStartDatePicker = new MyDateAndTimePicker(repeatRuleEdited.getSpecifiedStartDateD().getTime() != 0 ? repeatRuleEdited.getSpecifiedStartDateD() : defaultStartDate); // "<no Start date**>"
        repeatStartDatePickerXXX = new MyDateAndTimePicker(repeatRuleEdited.getSpecifiedStartDateZZZ()); // "<no Start date**>"
        if (Config.TEST) {
            repeatStartDatePickerXXX.setName("repeatStartDatePicker");
        }
        repeatStartDatePickerXXX.setDate(getStartDateToEditInPicker());
//        repeatFrom_NoneCompletedDue_Field.addSelectionListener(refreshSelectionListener);
        repeatFrom_NoneCompletedDue_Field.addActionListener(refreshActionListener);
//        repeatFrom_NoneCompletedDue_Field.addSelectionListener((oldSel, newSel) -> {
//            if (oldSel != newSel && newSel == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE && repeatStartDatePicker != null && repeatStartDatePicker.getDate().getTime() == 0) {
//                Dialog.show("INFO", "Please set the " + Item.DUE_DATE, "OK", null);
//                repeatStartDatePicker.setHidden(false);
//                animateHierarchy(300);
//            }
//        });
//        repeatFromDueOrCompletedFieldContainer = createLabelAndFieldContainer("Repeat from", repeatFromDueOrCompletedField);
        if (false) {
            repeatFromDueOrCompletedFieldContainer = BoxLayout.encloseY(new Label("Repeat"), repeatFrom_NoneCompletedDue_FieldCont);
        }

//        repeatStartDatePicker = new EditField(new MyDate(repeatRuleOwner.getRepeatStartTime(false)), allowEditingStartDate);
//        repeatStartDatePicker = new EditField(repeatRuleOwner.getRepeatStartTime(false), allowEditingStartDate);
//        MyDatePicker hideUntil = new MyDatePicker("<hide task until>", parseIdMap2, () -> item.getHideUntilDateD(), (d) -> item.setHideUntilDateD(d));
//        if (false && repeatStartDatePicker == null) {
//            repeatStartDatePicker = new MyDateAndTimePicker("<hide task until>", parseIdMap2, () -> repeatRuleOwner.getRepeatStartTime(false), (d) -> repeatRuleOwner.setRepeatStartTime(d)); //OK
//            repeatStartDatePicker = new MyDateAndTimePicker(repeatRuleOwner.getRepeatStartTime(false), "<hide task until>"); //OK
//if (startDate.getTime()!=null)
//        ASSERT.that(repeatStartDatePicker.getDate().getTime() == startDate.getTime(), "Picker changes date, org=" + startDate + ", picker=" + repeatStartDatePicker.getDate());
        if (false && isForWorkSlot) {
            repeatStartDatePickerXXX.setHidden(true);
        }
//else
//        repeatStartDatePicker = new MyDateAndTimePicker(startDate, "<no Start date**>"); //OK
//        } else {
//            repeatStartDateLabel=new Label(MyDate.formatDateTimeNew(endDate));
//        }
        //FREQUENCY: DAILY/MONTHLY/ BASIS
        int frequency = repeatRuleEdited.getFrequency();
//        frequencyField = new MyToggleButton(MyRepeatRule.getRepeatRuleFrequencyNames(), MyRepeatRule.getRepeatRuleFrequencyNumbers(), frequency);
//        frequencyField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleFrequencyNumbers(), MyRepeatRule.getRepeatRuleFrequencyNames(), frequency);
//        MyToggleButton x = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleFrequencyNames(), RepeatRuleParseObject.getRepeatRuleFrequencyNumbers(), frequency);
//        frequency_DailyWeekMonthYearly_Field = new MyComponentGroup(RepeatRuleParseObject.getRepeatRuleFrequencyNumbersAsObjects(), RepeatRuleParseObject.getRepeatRuleFrequencyNames(), frequency, false);
        frequency_DailyWeekMonthYearly_Field = new MyComponentGroup(RepeatRuleParseObject.getRepeatRuleFrequencyNumbersAsObjects(), RepeatRuleParseObject.getRepeatRuleFrequencyNames(), frequency, false);
//        frequency_DailyWeekMonthYearly_Field.setContainer(new Container(new GridLayout(4)));
//        frequency_DailyWeekMonthYearly_Field.addSelectionListener(refreshSelectionListener);
        frequency_DailyWeekMonthYearly_Field.addActionListener(refreshActionListener);

        frequency_DailyWeekMonthYearly_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_INTERVAL, frequency_DailyWeekMonthYearly_Field,
                RepeatRuleParseObject.REPEAT_RULE_INTERVAL_HELP);

        //EVERY 1,2,3,.. (DAYS/WEEKS/MONTHS/YEARS)
//        intervalFieldInfiniteList = new ListModelInfinite(1, getFreqNumberInstances(frequency), "every", RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//        intervalField = new ComboBoxOffset(intervalFieldInfiniteList);
//        intervalField.setSelectedValue(myRepeatRule.getInterval());
//        intervalField_1_NN_TextField = new TextField("" + myRepeatRule.getInterval(), "<interval>", 10, TextField.NUMERIC);
//        intervalField_1_NN_TextField = new MyIntTextField(repeatRuleEdited.getInterval(), "**", 1, MyPrefs.repeatMaxInterval.getInt(), 1);
        intervalField_1_NN_TextField = new MyIntPicker(repeatRuleEdited.getInterval(), 1, MyPrefs.repeatMaxInterval.getInt(), 1);
//        intervalField_1_NN_TextFieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_FREQUENCY, intervalField_1_NN_TextField,
//                RepeatRuleParseObject.REPEAT_RULE_FREQUENCY_HELP);
        intervalField_1_NN_TextFieldCont = BoxLayout.encloseY(layoutN(RepeatRuleParseObject.REPEAT_RULE_FREQUENCY, intervalField_1_NN_TextField,
                RepeatRuleParseObject.REPEAT_RULE_FREQUENCY_HELP), makeSpacerThin());

        //Set up all fields with default selected valued
        //DAYS IN WEEK: MONDAY[ ] TUESDAY[ ]...
//        daysInWeekField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getDayInWeekNumbers(), MyDate.getWeekDayNamesMondayFirst());
//        daysInWeekField.setSelectedOredTogether(myRepeatRule.getDaysInWeek());
        Vector weeksInYear = repeatRuleEdited.getDaysInWeekAsVector().size() > 0 ? repeatRuleEdited.getDaysInWeekAsVector()
                : (itemDueDate != null ? RepeatRuleParseObject.dayInWeeksToVector(itemDueDate) : new Vector(Arrays.asList(new Integer(RepeatRule.MONDAY))));

//        daysInWeek_MonSun_Field = new MyToggleButton(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(),
//                weeksInYear, -1, false, new ComponentGroup[2], new int[]{5, 2});
//        daysInWeek_MonSun_Field = new MyToggleButton(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(),
//                weeksInYear, xxxx, false, new ComponentGroup[2], new int[]{5, 2});
        daysInWeek_MonSun_Field = new MyComponentGroup(RepeatRuleParseObject.getDayInWeekNumbers(), MyDate.getShortWeekDayNamesMondayFirst(),
                weeksInYear, false, false, true, new ComponentGroup[2], new int[]{5, 2});
//        daysInWeek_MonSun_Field = new MyComponentGroup(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(),
//                repeatRuleEdited.getDaysInWeekAsVector(), -1);
//        daysInWeek_MonSun_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_DAYS_IN_WEEK, (MyToggleButton) daysInWeek_MonSun_Field,
//        daysInWeek_MonSun_FieldCont = BoxLayout.encloseY(makeSpacerThin(), layoutN(RepeatRuleParseObject.REPEAT_RULE_DAYS_IN_WEEK, daysInWeek_MonSun_Field,
        daysInWeek_MonSun_FieldCont = BoxLayout.encloseY(layoutN(RepeatRuleParseObject.REPEAT_RULE_DAYS_IN_WEEK, daysInWeek_MonSun_Field,
                RepeatRuleParseObject.REPEAT_RULE_DAYS_IN_WEEK_HELP));
//        daysInWeekField = new MyComponentGroup(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(), myRepeatRule.getDaysInWeekAsVector(), true);
//        daysInWeek_MonSun_Field.setContainer(new Container(new GridLayout(7)));

        //1-365
//        dayInYear_1_365_Field = new ComboBoxOffset(new ListModelInfinite(1, 365, false));
//        dayInYear_1_365_Field.setSelectedValue(myRepeatRule.getDayInYear() > 0 ? myRepeatRule.getDayInYear() : 1); //TODO default should be 182 to start in the mid of the year...
//        dayInYear_1_365_Field = new TextField("" + myRepeatRule.getInterval(), "<interval>", 10, TextField.NUMERIC);
//        dayInYear_1_365_Field.setText("" + (myRepeatRule.getDayInYear() > 0 ? myRepeatRule.getDayInYear() : 1)); //TODO default should be 182 to start in the mid of the year...
//        dayInYear_1_365_Field = new MyIntTextField(myRepeatRule.getInterval(), "<day in year>", 1, 365, 1);
//        dayInYear_1_365_Field = new MyIntTextField(repeatRuleEdited.getDayInYear() > 0 ? repeatRuleEdited.getDayInYear() : 1,
//                "<day in year>", 1, 365, 1);
        int dayInYear = repeatRuleEdited.getDayInYear() != 0 ? repeatRuleEdited.getDayInYear() : (itemDueDate != null ? MyDate.getDayInYear(itemDueDate) : 1);

        dayInYear_1_365_Field = new MyIntPicker(dayInYear, 1, 366);
        dayInYear_1_365_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_DAY_IN_YEAR, dayInYear_1_365_Field,
                RepeatRuleParseObject.REPEAT_RULE_DAY_IN_YEAR_HELP);

        //Jan-Dec
//        monthsInYearField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getMonthInYearNumbers(), MyDate.getMonthNames());
        Vector monthsInYear = repeatRuleEdited.getMonthInYearAsVector().size() > 0 ? repeatRuleEdited.getMonthInYearAsVector()
                : (itemDueDate != null ? RepeatRuleParseObject.monthInYearAsVector(itemDueDate) : new Vector(Arrays.asList(new Integer(RepeatRule.JANUARY))));

//        monthsInYear_JanDec_Field = new MyToggleButton(MyDate.getShortMonthNames(), RepeatRuleParseObject.getMonthInYearNumbers(), monthsInYear, -1, false, new ComponentGroup[2], new int[]{6, 6});
        monthsInYear_JanDec_Field = new MyComponentGroup(RepeatRuleParseObject.getMonthInYearNumbers(), MyDate.getShortMonthNames(), monthsInYear, false, false,
                new ComponentGroup[2], new int[]{6, 6}, true);
//        monthsInYearField.setSelectedOredTogether(myRepeatRule.getMonthInYear());
//        monthsInYear_JanDec_FieldCont = BoxLayout.encloseY(makeSpacerThin(),layoutN(RepeatRuleParseObject.REPEAT_RULE_JAN_DEC, monthsInYear_JanDec_Field,
        monthsInYear_JanDec_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_JAN_DEC, monthsInYear_JanDec_Field,
                RepeatRuleParseObject.REPEAT_RULE_JAN_DEC_HELP);

        //Mon-Sun+Weekdays+Weekends
//        daysInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getDayInWeekNumbersInclWeekdays(), MyDate.getWeekDayNamesMondayFirstInclWeekdays());
        Vector daysInMonth = repeatRuleEdited.getDaysInWeekAsVectorInclWeekdays().size() > 0 ? repeatRuleEdited.getDaysInWeekAsVectorInclWeekdays()
                : (itemDueDate != null ? RepeatRuleParseObject.dayInWeeksToVector(itemDueDate) : new Vector(Arrays.asList(new Integer(RepeatRule.MONDAY))));

//        daysInMonth_MonSunWeekdays_Field = new MyToggleButton(MyDate.getShortWeekDayNamesMondayFirstInclWeekdays(), RepeatRuleParseObject.getDayInWeekNumbersInclWeekdays(),
//                daysInMonth, -1, false, new ComponentGroup[2], new int[]{5, 4});
        daysInMonth_MonSunWeekdays_Field = new MyComponentGroup(RepeatRuleParseObject.getDayInWeekNumbersInclWeekdays(), MyDate.getShortWeekDayNamesMondayFirstInclWeekdays(),
                daysInMonth, false, false, true, new ComponentGroup[2], new int[]{5, 4});
        daysInMonth_MonSunWeekdays_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_MON_SUN, daysInMonth_MonSunWeekdays_Field,
                RepeatRuleParseObject.REPEAT_RULE_MON_SUN_HELP);
//        daysInMonth_MonSunWeekend_Field.setContainer(new Container(new GridLayout(2, 7)));
//        daysInMonthField.setSelectedOredTogether(myRepeatRule.getDaysInWeek());

        //1-31+Last
//        dayInMonth_1_31_Last_FieldFM = new ComboBoxOffset(new ListModelInfinite(1, 32, true, "Last"));
        int dayInMonth = repeatRuleEdited.getDayInMonth() > 0 ? repeatRuleEdited.getDayInMonth()
                //                : (itemDueDate.getTime() != 0 ? MyDate.getDayInMonthNatural(itemDueDate) : 1);
                : (itemDueDate != null ? MyDate.getDayInMonthNatural(itemDueDate) : 1);

        dayInMonth_1_31_Last_FieldFM = new ComboBoxOffsetNew();
//        dayInMonth_1_31_Last_FieldFM.setSelectedValue(myRepeatRule.getDayInMonth() > 0 ? myRepeatRule.getDayInMonth() : 1);
        dayInMonth_1_31_Last_FieldFM.setSelectedValue(dayInMonth);
        dayInMonth_1_31_Last_FieldFMCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_DAY_IN_MONTH, dayInMonth_1_31_Last_FieldFM,
                RepeatRuleParseObject.REPEAT_RULE_DAY_IN_MONTH_HELP);

        //1st..5th, last..5th last (of week)
//        weeksInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getWeekInMonthNumbers(), MyRepeatRule.getWeekInMonthNames());
        Vector weeksInMonth = repeatRuleEdited.getWeekInMonthAsVector().size() > 0 ? repeatRuleEdited.getWeekInMonthAsVector()
                : (itemDueDate != null ? RepeatRuleParseObject.weekInMonthToVector(itemDueDate) : new Vector(Arrays.asList(new Integer(RepeatRule.FIRST))));

//        weeksInMonth_1st2ndLast_Field = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNamesShort(),
//                RepeatRuleParseObject.getWeekInMonthNumbersShort(), weeksInMonth, false);
        weeksInMonth_1st2ndLast_Field = new MyComponentGroup(RepeatRuleParseObject.getWeekInMonthNumbersShort(), RepeatRuleParseObject.getWeekInMonthNamesShort(),
                weeksInMonth, true, false, true);
        weeksInMonth_1st2ndLast_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_WEEKS_IN_MONTH, weeksInMonth_1st2ndLast_Field,
                RepeatRuleParseObject.REPEAT_RULE_WEEKS_IN_MONTH_HELP);
        //1st..5th+last
//        weeksInMonth_1st2ndLast_Field.setContainer(new Container(new TableLayout(2, 5)));
//        weeksInMonthField.setSelectedOredTogether(myRepeatRule.getWeekInMonth());

        //1st..5th, last..5th last (of Mon-Sun)
//        weekdaysInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getWeekInMonthNumbers(), MyRepeatRule.getWeekInMonthNames());
//        weekdaysInMonthField = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNames(), RepeatRuleParseObject.getWeekInMonthNumbers(), myRepeatRule.getWeekdaysInMonth(), true);
        Vector weekdayNbInMonth = repeatRuleEdited.getWeekdaysInMonthAsVector().size() > 0 ? repeatRuleEdited.getWeekdaysInMonthAsVector()
                : (itemDueDate != null ? RepeatRuleParseObject.dayInMonthToVector(itemDueDate) : new Vector(Arrays.asList(new Integer(RepeatRule.FIRST))));

//        MyToggleButton weekdaysInMonth_1st2nLast_FieldX = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNumbersShort(), RepeatRuleParseObject.getWeekInMonthNamesShort(), 
//                weekdayNbInMonth, false); //**
        weekdaysInMonth_1st2nLast_Field = new MyComponentGroup(RepeatRuleParseObject.getWeekInMonthNumbersShort(), RepeatRuleParseObject.getWeekInMonthNamesShort(),
                weekdayNbInMonth, true, false, true); //**
        weekdaysInMonth_1st2nLast_FieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_WEEKDAYS_IN_MONTH, weekdaysInMonth_1st2nLast_Field,
                RepeatRuleParseObject.REPEAT_RULE_WEEKDAYS_IN_MONTH_HELP);
//<editor-fold defaultstate="collapsed" desc="comment">
//        weekdaysInMonthField.setSelectedOredTogether(myRepeatRule.getWeekdaysInMonth());
//        weekdaysInMonthField.setSelectedOredTogether(myRepeatRule.getWeekdaysInMonth());

//        daysInWeekField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getDayInWeekNumbers(), MyDate.getWeekDayNamesMondayFirst());
//        daysInWeekField.setSelectedOredTogether(myRepeatRule.getDaysInWeek());
//</editor-fold>
//        if (frequency_DailyWeekMonthYearly_Field.getSelectedValue() == RepeatRule.YEARLY) {
////            monthlyRepeatTypeSelectionBox = new ComboBoxLogicalNames(new int[]{0, 1, 2}, new String[]{"Select day of month:", "Select week(s) of month:", "Select weekday(s) of month:"});
////            monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"day", "weekday(s)", "week(s)"}, new int[]{0, 2, 1});
//            monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"day", "weekdays", "week nb"}, new int[]{0, 2, 1});
//        } else {
////            monthlyRepeatTypeSelectionBox = new ComboBoxLogicalNames(new int[]{0, 1, 2}, new String[]{"Select day of month(s):", "Select week(s) of month(s):", "Select weekday(s) of month(s):"});
////            monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"day", "weekday(s)", "week(s)"}, new int[]{0, 2, 1});
//            monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"day", "weekdays", "week nb"}, new int[]{0, 2, 1});
//        }
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"Day", "Week days", "Week in month"},
//                new int[]{MONTHLY_OPTION_DAY, MONTHLY_OPTION_WEEKDAYS, MONTHLY_OPTION_WEEK_NB});
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyComponentGroup(
                new Object[]{MONTHLY_OPTION_DAY, MONTHLY_OPTION_WEEKDAYS, MONTHLY_OPTION_WEEK_NB}, new String[]{"Day", "Week days", "Week in month"}, false);
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBoxCont = BoxLayout.encloseY(makeSpacerThin(), layoutN(RepeatRuleParseObject.REPEAT_RULE_MONTHLY_TYPE, monthlyRepeatType_DayWeekdaysWeeks_SelectionBox,
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBoxCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_MONTHLY_TYPE, monthlyRepeatType_DayWeekdaysWeeks_SelectionBox,
                RepeatRuleParseObject.REPEAT_RULE_MONTHLY_TYPE_HELP);

        //show dayInMonth if either field is defined, or if weekInMonth is not defined (covers both initial state and once defined)
//        monthlyRepeatTypeSelectionBox.setSelectedIndex(((myRepeatRule.getDayInMonth() > 0 || (myRepeatRule.getWeekInMonth() == 0 && myRepeatRule.getWeekdaysInMonth() == 0)) ? 0 : ((myRepeatRule.getWeekInMonth() != 0) ? 1 : 2)));
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setSelectedIndex(((myRepeatRule.getDayInMonth() > 0 || (myRepeatRule.getWeeksInMonth() == 0 && myRepeatRule.getWeekdaysInMonth() == 0))
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setSelectedValue(((repeatRuleEdited.getDayInMonth() > 0 || (repeatRuleEdited.getWeeksInMonth() == 0 && repeatRuleEdited.getWeekdaysInMonth() == 0))
//                ? MONTHLY_OPTION_DAY : ((repeatRuleEdited.getWeeksInMonth() != 0) ? MONTHLY_OPTION_WEEK_NB : MONTHLY_OPTION_WEEKDAYS)));
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.selectValue(((repeatRuleEdited.getDayInMonth() > 0 || (repeatRuleEdited.getWeeksInMonth() == 0 && repeatRuleEdited.getWeekdaysInMonth() == 0))
                ? MONTHLY_OPTION_DAY : ((repeatRuleEdited.getWeeksInMonth() != 0) ? MONTHLY_OPTION_WEEK_NB : MONTHLY_OPTION_WEEKDAYS)));
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.addSelectionListener(refreshSelectionListener);
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.addActionListener(refreshActionListener);
//<editor-fold defaultstate="collapsed" desc="xyz">
//        if ((myRepeatRule.getDayInMonth() > 0 || (myRepeatRule.getWeekInMonth() == 0 && myRepeatRule.getWeekdaysInMonth() == 0))) {
//            monthlyRepeatTypeSelectionBox.setSelectedIndex(0);
//        } else if ((myRepeatRule.getWeekInMonth() != 0)) {
//            monthlyRepeatTypeSelectionBox.setSelectedIndex(1);
//        } else {
//            monthlyRepeatTypeSelectionBox.setSelectedIndex(2);
//        }
//        yearlyContainer.addComponent(yearlyChoice = new MyChoiceContainer(
//                new String[]{"Select day of year", "Select month(s) of year"},
//                new ComponentSelector(new Component[]{dayInYearField, yearlySelectMonthContainer}),
//                ((myRepeatRule.getDayInYear() > 0 || myRepeatRule.getMonthInYear() == 0) ? 0 : 1),
//                defaultLayout, defaultTransition));

//        yearlyChoiceCombo = new ComboBoxLogicalNames(new int[]{0, 1}, new String[]{"Select day of year", "Select month(s) of year"});
//</editor-fold>
//        yearlyChoice_DayMonths_Combo = new MyToggleButton(new String[]{"Day", "Month(s)"}, new int[]{YEAR_OPTION_DAY_OF_YEAR, YEAR_OPTION_MONTHS});
        yearlyChoice_DayMonths_Combo = new MyComponentGroup(new Object[]{YEAR_OPTION_DAY_OF_YEAR, YEAR_OPTION_MONTHS}, new String[]{"Day", "Month(s)"});
//        yearlyChoice_DayMonths_Combo.setSelectedValue(((myRepeatRule.getDayInYear() > 0 || myRepeatRule.getMonthsInYear() == 0) ? YEAR_OPTION_DAY_OF_YEAR : YEAR_OPTION_MONTHS)); //show dayInYear if either field is defined, or if monthInYear is not defined (covers both initial state and once defined)
//        yearlyChoice_DayMonths_Combo.setSelectedValue(((repeatRuleEdited.getDayInYear() > 0) ? YEAR_OPTION_DAY_OF_YEAR : YEAR_OPTION_MONTHS)); //show dayInYear if either field is defined, or if monthInYear is not defined (covers both initial state and once defined)
        yearlyChoice_DayMonths_Combo.selectValue(((repeatRuleEdited.getDayInYear() > 0) ? YEAR_OPTION_DAY_OF_YEAR : YEAR_OPTION_MONTHS)); //show dayInYear if either field is defined, or if monthInYear is not defined (covers both initial state and once defined)
//        yearlyChoice_DayMonths_Combo.addSelectionListener(refreshSelectionListener);
        yearlyChoice_DayMonths_Combo.addActionListener(refreshActionListener);
        yearlyChoice_DayMonths_ComboCont = BoxLayout.encloseY(makeSpacerThin(), layoutN(RepeatRuleParseObject.REPEAT_RULE_YEARLY_TYPE, yearlyChoice_DayMonths_Combo,
                RepeatRuleParseObject.REPEAT_RULE_YEARLY_TYPE_HELP));

//        repeatFromField = new DateField5(repeatRuleOwner.getRepeatStartTime(false));
//        repeatFromField = new DateField5(MyDate.getNowDateOnly() + MyDate.DAY_IN_MILLISECONDS * Settings.getInstance().getDefaultRepeatFromSpecifiedDateDaysAhead());
//        daysAheadField = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxRepeatInstancesDaysAhead(), "", "", 1, false, false));
//        daysAheadField = new MyIntPicker(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? 1 : myRepeatRule.getNumberOfDaysRepeatsAreGeneratedAhead(), 1, MyPrefs.repeatMaxNumberFutureDaysToGenerateAhead.getInt());
//        daysAheadField.setSelectedValue(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? 1 : myRepeatRule.getNumberOfDaysRepeatsAreGeneratedAhead()); //if 0 (==undefined), defined use 1 as minimum value to display in combobox
        //NUMBER OF REPEATS
//<editor-fold defaultstate="collapsed" desc="comment">
//        repeatHowLongChoiceCombo = new ComboBoxLogicalNames(new int[]{0, 1, 2}, new String[]{"Repeat forever", "Repeat until", "Number of repeats"});
//        repeatHowLongChoiceCombo = new MyToggleButton(new String[]{"Repeat forever", "Repeat until", "Number of repeats"}, new int[]{0, 1, 2});
//        repeatHowLongChoiceCombo = new MyToggleButton(new String[]{"forever", "until", "count"}, new int[]{0, 1, 2});
//</editor-fold>
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo = new MyToggleButton(new String[]{"Forever", "Until date", "Number"}, new int[]{REPEAT_HOW_LONG_OPTION_FOREVER, REPEAT_HOW_LONG_OPTION_UNTIL, REPEAT_HOW_LONG_OPTION_NUMBER});
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo = new MyToggleButton(new String[]{"Forever", "Until date", "Number"}, new int[]{REPEAT_HOW_LONG_OPTION_FOREVER, REPEAT_HOW_LONG_OPTION_UNTIL, REPEAT_HOW_LONG_OPTION_NUMBER});
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo = new MyToggleButton(
//                new String[]{RepeatRuleParseObject.FOREVER, RepeatRuleParseObject.UNTIL, RepeatRuleParseObject.COUNT},
//                new int[]{REPEAT_HOW_LONG_OPTION_FOREVER, REPEAT_HOW_LONG_OPTION_UNTIL, REPEAT_HOW_LONG_OPTION_NUMBER});
        repeatHowLong_ForeverUntilNumber_ChoiceCombo = new MyComponentGroup(new Object[]{REPEAT_HOW_LONG_OPTION_FOREVER, REPEAT_HOW_LONG_OPTION_UNTIL, REPEAT_HOW_LONG_OPTION_NUMBER},
                new String[]{RepeatRuleParseObject.FOREVER, RepeatRuleParseObject.UNTIL, RepeatRuleParseObject.COUNT}, false);
//        repeatHowLongChoiceCombo.setSelectedValue(myRepeatRule.useCount() ? 2 : (myRepeatRule.getEndDate() != Long.MAX_VALUE ? 1 : 0));
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo.setSelectedValue(repeatRuleEdited.useCount() ? REPEAT_HOW_LONG_OPTION_NUMBER
        repeatHowLong_ForeverUntilNumber_ChoiceCombo.selectValue(repeatRuleEdited.useCount() ? REPEAT_HOW_LONG_OPTION_NUMBER
                : (repeatRuleEdited.getEndDate() != MyDate.MAX_DATE ? REPEAT_HOW_LONG_OPTION_UNTIL : REPEAT_HOW_LONG_OPTION_FOREVER));
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo.addSelectionListener(refreshSelectionListener);
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo.addActionListener(refreshActionListener);
        repeatHowLong_ForeverUntilNumber_ChoiceCombo.addActionListener((e) -> {
            //if user select UntilDate or Times, then those fields may be hidden below the bottom of the screen, so scroll them to visible:
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_UNTIL) {
//                scrollComponentToVisible(repeatEndDateEditButtonCont);
//            } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_NUMBER) {
//                scrollComponentToVisible(repeatHowManyTimesFieldCont);
//            }
//            fieldsShow(false);
//            fieldsShow();
//</editor-fold>
            if (false) {
                refreshActionListener.actionPerformed(e); //call first to refresh fields *before* scrolling to visible
            } else {
                scrollEndRepeatFieldsToVisible = repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() != REPEAT_HOW_LONG_OPTION_FOREVER;
                fieldsShow(); //don't waste time animating
            }
            if (false) {
                if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() != REPEAT_HOW_LONG_OPTION_FOREVER) {
                    boolean animate = false;
                    if (!showNumberFutureRepeatsCont.isHidden()) { //last field on screen so if visible just show that
                        scrollComponentToVisible(showNumberFutureRepeatsCont);
                        animate = true;
                    } else if (!repeatEndDateEditButtonCont.isHidden()) {
                        scrollComponentToVisible(repeatEndDateEditButtonCont);
                        animate = true;
                    } else if (!repeatHowManyTimesFieldCont.isHidden()) {
                        scrollComponentToVisible(repeatHowManyTimesFieldCont);
                        animate = true;
                    }
                    if (false && animate) {
                        getContentPane().animateLayout(ANIMATION_TIME_DEFAULT); //shouldn't be necessary, scrollComponentToVisible should do it
                    }
//                getContentPane().animateLayout(ANIMATION_TIME_DEFAULT); //shouldn't be necessary, scrollComponentToVisible should do it
                }
            }
        });
        repeatHowLong_ForeverUntilNumber_ChoiceComboCont = BoxLayout.encloseY(makeSpacerThin(), layoutN(RepeatRuleParseObject.REPEAT_RULE_UNTIL, repeatHowLong_ForeverUntilNumber_ChoiceCombo,
                RepeatRuleParseObject.REPEAT_RULE_UNTIL_HELP));

//<editor-fold defaultstate="collapsed" desc="comment">
//        repeatEndDateEditButton = new DateField5();
//        repeatEndDateEditButton.setTime(myRepeatRule.getEndDate() != Long.MAX_VALUE ? myRepeatRule.getEndDate() : 0);
//        repeatEndDateEditButton = new ScreenDateTimePicker(myRepeatRule.getEndDate() != Long.MAX_VALUE ? new MyDate(myRepeatRule.getEndDate()) : new MyDate());
//        repeatEndDateEditButton.setDTime(myRepeatRule.getEndDate() != Long.MAX_VALUE ? myRepeatRule.getEndDate() : 0);
//        endDate = new MyDate(myRepeatRule.getEndDate() != Long.MAX_VALUE ?
//                new MyDate(myRepeatRule.getEndDate())
//                : new MyDate(MyDate.getNowDateOnly() + MyDate.DAY_IN_MILLISECONDS * Settings.getInstance().getDefaultRepeatFromSpecifiedDateDaysAhead()));
//        endDate = new Date(myRepeatRule.getEndDate() != Long.MAX_VALUE ?
//</editor-fold>
        endDate = repeatRuleEdited.getEndDate() != MyDate.MAX_DATE
                ? repeatRuleEdited.getEndDateD()
                //                : System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * Settings.getInstance().getDefaultRepeatFromSpecifiedDateDaysAhead());
                //                : (repeatStartDatePicker.getDate().getTime() != 0
                //                ? repeatStartDatePicker.getDate()
                //                : (makeStartDate != null
                //                        ? ((Date) makeStartDate.getVal())
                //                        : (new Date(MyDate.currentTimeMillis()))));
                //                : (repeatRuleEdited.getSpecifiedStartDate().getTime() != 0 //UI: if SpecifiedStartDate is defined use that as first value for endDate
                //                ? repeatRuleEdited.getSpecifiedStartDate()
                : new MyDate(MyDate.currentTimeMillis()); //UI: use now as default endRepeat date. TODO: 
//<editor-fold defaultstate="collapsed" desc="comment">
//        repeatEndDateEditButton = new EditButton(null, new MyCommand("") {
//            public String getCommandName() {
//                return endDate.formatDate();
//            }
//
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenDateTimePickerTransitionForm(endDate) {
//                    public void done(MyForm form, Object value, boolean done) {
//                        repeatEndDateEditButton.refreshLabel(); //refresh to show just edited value
//                    }
//                }.display();
//            }
//        });
//        MyDatePicker hideUntil = new MyDatePicker("<hide task until>", parseIdMap2, () -> item.getHideUntilDateD(), (d) -> item.setHideUntilDateD(d));
//</editor-fold>
//        repeatEndDateEditButton = new MyDatePicker("<last date>", parseIdMap2, () -> endDate, (d) -> {myRepeatRule.setEndDate(d.getTime() == 0 ? RepeatRuleParseObject.MAX_DATE : d.getTime());
        repeatEndDateEditButton = new MyDatePicker(endDate, "<last date>", MyDatePicker.END_OF_SELECTED_DAY); //UI: always set end of day to ensure a repeat can happen on the day itself
        repeatEndDateEditButtonCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_UNTIL_DATE, repeatEndDateEditButton,
                RepeatRuleParseObject.REPEAT_RULE_UNTIL_DATE_HELP);
//        repeatEndDateEditButton = new MyDatePicker(makeStartDate);
//        }); //OK

//        repeatHowManyTimesField = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxRepeatCount(), "", "counts**"/*times"*/, 1, false, false));
//        repeatHowManyTimesField = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxRepeatCount(), "", ""/*times"*/, 1, false, false));
//        repeatHowManyTimesField.setSelectedValue(myRepeatRule.useCount() ? myRepeatRule.getNumberOfRepeats()myRepeatRule : 1);
//        repeatHowManyTimesField = new MyIntPicker(myRepeatRule.useCount() ? myRepeatRule.getNumberOfRepeats() : 1, 1, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead.getInt());
//        repeatHowManyTimesField = new MyIntTextField(repeatRuleEdited.useCount() ? repeatRuleEdited.getNumberOfRepeats() : 1, "", 1, 999999, 1);
        repeatHowManyTimesField = new MyIntPicker(repeatRuleEdited.useCount() ? repeatRuleEdited.getNumberOfRepeats() : 1, 1, MyPrefs.repeatMaxNumberOfRepeatsAllowed.getInt());
        repeatHowManyTimesFieldCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_UNTIL_TIMES, repeatHowManyTimesField,
                RepeatRuleParseObject.REPEAT_RULE_UNTIL_TIMES_HELP);

        //SHOW HOW MANY FUTURE REPEATS
//        showHowManyCombo = new ComboBoxLogicalNames(new int[]{0, 1}, new String[]{"repeat instances", "days ahead"});
        showHowMany_InstancesDaysAhead_ComboZZZ = new MyComponentGroup(new Object[]{SHOW_HOW_MANY_AHEAD_INSTANCES, SHOW_HOW_MANY_AHEAD_DAYS_AHEAD}, new String[]{"instances", "days ahead"});
//        showHowMany_InstancesDaysAhead_Combo.setSelectedValue(myRepeatRule.getNumberFutureRepeatsToGenerateAhead() != 0 ? SHOW_HOW_MANY_AHEAD_INSTANCES : SHOW_HOW_MANY_AHEAD_DAYS_AHEAD);
//        showHowMany_InstancesDaysAhead_ComboZZZ.setSelectedValue(repeatRuleEdited.useNumberFutureRepeatsToGenerateAhead() ? SHOW_HOW_MANY_AHEAD_INSTANCES : SHOW_HOW_MANY_AHEAD_DAYS_AHEAD);
        showHowMany_InstancesDaysAhead_ComboZZZ.selectValue(repeatRuleEdited.useNumberFutureRepeatsToGenerateAhead() ? SHOW_HOW_MANY_AHEAD_INSTANCES : SHOW_HOW_MANY_AHEAD_DAYS_AHEAD);
//        showHowMany_InstancesDaysAhead_ComboZZZ.addSelectionListener(refreshSelectionListener);
        showHowMany_InstancesDaysAhead_ComboZZZ.addActionListener(refreshActionListener);
//            showHowManyContainer = createLabelAndFieldContainer("Show how many", showHowManyCombo);
//        showHowManyContainer = createLabelAndFieldContainer("Create how many future repeats", showHowManyCombo);
//        showHowManyContainer = BoxLayout.encloseY(new Label("Create several repeats"), showHowMany_InstancesDaysAhead_Combo); //"Create how many simultaneous repeats"
//        if (false) {
//            showHowManyContainer = BoxLayout.encloseY(new Label("Create multiple future repeats"), showHowMany_InstancesDaysAhead_ComboZZZ); //"Create how many simultaneous repeats"
//        } else {
//            showHowManyContainer = new Label("Create multiple future repeats"); //"Create how many simultaneous repeats"
//        }
//        showNumberFutureRepeats = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxFutureRepeatInstances(), "", "", 1, false, false));
//        showNumberFutureRepeats.setSelectedValue(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1); //use 1 as defaiæt vaæie (if NumberFutureRepeatsGeneratedAhead is not defined)
        showNumberFutureRepeats = new MyIntPicker(repeatRuleEdited.useNumberFutureRepeatsToGenerateAhead()
                //                ? repeatRuleEdited.getNumberSimultaneousRepeats() + 1 : 1, 1, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead.getInt()); //+1: adjust rel. to UI which now says "simultaneous instances"
                ? repeatRuleEdited.getNumberSimultaneousRepeats() : 1, 1, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead.getInt()); //+1: adjust rel. to UI which now says "simultaneous instances"
        showNumberFutureRepeatsCont = BoxLayout.encloseY(makeSpacerThin(), layoutN(RepeatRuleParseObject.REPEAT_RULE_NUMBER_REPEATS, showNumberFutureRepeats,
                RepeatRuleParseObject.REPEAT_RULE_NUMBER_REPEATS_HELP));

//        showNumberDaysAhead = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxFutureRepeatInstances(), "", "", 1, false, false));
//        showNumberDaysAhead.setSelectedValue(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1); //use 1 as defaiæt vaæie (if NumberFutureRepeatsGeneratedAhead is not defined)
//        showNumberDaysAhead = new MyIntPicker(myRepeatRule.useNumberFutureRepeatsToGenerateAhead()
//                ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1, 1, MyPrefs.repeatMaxNumberFutureDaysToGenerateAhead.getInt());
        showNumberDaysAheadZZZ = new MyIntPicker(!repeatRuleEdited.useNumberFutureRepeatsToGenerateAhead()
                ? repeatRuleEdited.getNumberOfDaysRepeatsAreGeneratedAhead() : 1, 1, MyPrefs.repeatMaxNumberFutureDaysToGenerateAheadZZZ.getInt());
//<editor-fold defaultstate="collapsed" desc="comment">
//            initialized = true;
//        } else { //if not initialized
//            motherContainer.removeAll(); //clean so it's empty to repopulate below
//        }
//</editor-fold>
        if (false) {
            showDatesButton = new Button(new Command("Show dates") {
                public void actionPerformed(ActionEvent evt) {
                    RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
                    if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
                        tempMyRepeatRule.showRepeatDueDates();
                    } else {
                        Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
                    }
                }
            });

            if (Config.TEST) {
                internalData = new SpanLabel(
                        "Done instances= [" + repeatRuleEdited.getListOfDoneInstances() + "]"
                        + "UnDone instances= [" + repeatRuleEdited.getListOfUndoneInstances() + "]"
                        + "Done instances= [" + repeatRuleEdited.getListOfDoneInstances() + "]"
                //                    + "\nLastGeneratedDate=" + repeatRuleEdited.getLastGeneratedDateD()
                //                    + "\nLastGeneratedDate=" + repeatRuleEdited.getLastGeneratedDateD()
                //                    + "\nLatestDateCompletedCancelled=" + repeatRuleEdited.getLatestDateCompletedOrCancelled()
                //                    + "\nTotalNumberOfDoneInstances=" + repeatRuleEdited.getTotalNumberOfDoneInstances())
                );
            }
        }

        onCompletionDatedRepeatsSwitch = new MyOnOffSwitch();
        onCompletionDatedRepeatsSwitch.setValue(repeatRuleEdited.isDatedCompletion() || isForWorkSlot);
        onCompletionDatedRepeatsSwitch.addChangeListener(refreshActionListener); //refresh on chnage
//        onCompletionDatedRepeatsCont = MyBorderLayout.centerEastWest(null, onCompletionDatedRepeats, new SpanLabel("Repeat on specific dates after completion date"));
        onCompletionDatedRepeatsCont = layoutN(RepeatRuleParseObject.REPEAT_RULE_DATED_COMPLETION, onCompletionDatedRepeatsSwitch,
                RepeatRuleParseObject.REPEAT_RULE_DATED_COMPLETION_HELP);
//        ((MyBorderLayout) onCompletionDatedRepeatsCont.getLayout()).setSizeEastWestMode(MyBorderLayout.SIZE_EAST_BEFORE_WEST); //size ofOff first //TODO: 
//        updateFields();
        fieldsAdd();
        fieldsShow();
    }

    private void addMonthlyFieldsToContainer(Container addToContainer) {//, RepeatRuleParseObject myRepeatRule, boolean forYear) {

        addToContainer.addComponent(monthlyRepeatType_DayWeekdaysWeeks_SelectionBoxCont);

        //SELECT DAY NUMBER IN MONTH, e.g. 3rd day - "Select day of month(s):"
        addToContainer.addComponent(dayInMonth_1_31_Last_FieldFMCont);
        //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
        addToContainer.addComponent(weeksInMonth_1st2ndLast_FieldCont);
        addToContainer.addComponent(daysInWeek_MonSun_FieldCont);
        //SELECT WEEKDAYS IN MONTH, e.g. 1st FRIDAY
        addToContainer.addComponent(weekdaysInMonth_1st2nLast_FieldCont);
        addToContainer.addComponent(daysInMonth_MonSunWeekdays_FieldCont);
    }

    private void fieldsAdd() {
        container.removeAll();

        if (repeatRuleDetailsContainerXXX != null) {
            container.addComponent(repeatRuleDetailsContainerXXX); //may be null if new rule
        }
//        motherContainer.addComponent(repeatFromDueOrCompletedFieldContainer);
        if (oldMode) {
            container.addComponent(repeatFrom_NoneCompletedDue_Field);
        } else {
            container.addComponent(repeatFrom_NoneCompletedDue_FieldCont);
        }
//        if (repeatFromDueOrCompletedField.getSelectedValue() == 1) //"due date
//        {
        if (false && repeatStartDatePickerXXX != null) { //false: hide for now, shouldn't be needed anymore
            container.addComponent(repeatStartDatePickerXXX);
        }
        if (repeatStartDateLabel != null) {
            container.addComponent(repeatStartDateLabel);
        }
        //Label("Create next repeat on specific dates" / "Repeat on a specific date" / 
//        motherContainer.addComponent(onCompletionDatedRepeats);
        container.addComponent(onCompletionDatedRepeatsCont);
//        }

        //FREQUENCY: DAILY/MONTHLY BASIS
        container.addComponent(frequency_DailyWeekMonthYearly_FieldCont);

        //EVERY 1,2,3,.. DAYS/WEEKS/MONTHS/YEARS
//        motherContainer.addComponent(intervalField); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...
        container.addComponent(intervalField_1_NN_TextFieldCont); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...

        //ADAPT rest according to type
//        switch (myRepeatRule.getFrequency()) {
//        int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//        intervalFieldInfiniteList.setListModelValues(1, getFreqNumberInstances(frequency), "every",
//                RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//         ** 
//        motherContainer.addComponent(daysInWeekField);
        container.addComponent(yearlyChoice_DayMonths_ComboCont);

        //Select day of year
        container.addComponent(dayInYear_1_365_FieldCont);

        container.addComponent(monthsInYear_JanDec_FieldCont);

        //Select yearly repeat on a monthly basis
//        addMonthlyFieldsToContainer(motherContainer, myRepeatRule, true);
        addMonthlyFieldsToContainer(container);//, myRepeatRule, false);

        container.addComponent(repeatHowLong_ForeverUntilNumber_ChoiceComboCont);
        //{"Repeat forever", "Repeat until", "Number of repeats"}
        container.addComponent(repeatEndDateEditButtonCont);
        container.addComponent(repeatHowManyTimesFieldCont);

        //if Repeat from DUE DATE then add option to choose how many future instances to create (if repeat from Completed date, not ossible to create future instances with right date)
//        motherContainer.addComponent(showHowManyContainer);
//Show how many REPEAT INSTANCED
        container.addComponent(showNumberFutureRepeatsCont);
//Show how many DAYS AHEAD
        if (false) {
            container.addComponent(showNumberDaysAheadZZZ);
        }

        if (false && Config.TEST && MyPrefs.repeatShowInternalDataInRepeatScreen.getBoolean()) {
            container.addComponent(showDatesButton);

            container.addComponent(internalData);
        }

        if (false) {
            this.animateLayout(400);
        }
    }

//    void showMonthlyFields(Container addToContainer, RepeatRuleParseObject myRepeatRule, boolean forYear) {
//    private void showMonthlyFields() {
//        showMonthlyFields(true);
//    }
    private void showMonthlyFields(boolean hide) {

//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setHidden(false);
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBoxCont.setHidden(hide);

        if (hide) {
            dayInMonth_1_31_Last_FieldFMCont.setHidden(true);
            weeksInMonth_1st2ndLast_FieldCont.setHidden(true);
//            daysInWeek_MonSun_Field.setHidden(true);
            daysInWeek_MonSun_FieldCont.setHidden(true);
            weekdaysInMonth_1st2nLast_FieldCont.setHidden(true);
            daysInMonth_MonSunWeekdays_FieldCont.setHidden(true);
        } else {
            switch (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt()) {
                case MONTHLY_OPTION_DAY:
                    //SELECT DAY NUMBER IN MONTH, e.g. 3rd day - "Select day of month(s):"
                    if (dayInMonth_1_31_Last_FieldFMCont.isHidden()) { //avoid unnecessary animation
                        dayInMonth_1_31_Last_FieldFMCont.setHidden(false);
                    }

                    weeksInMonth_1st2ndLast_FieldCont.setHidden(true);
                    daysInWeek_MonSun_FieldCont.setHidden(true);
                    weekdaysInMonth_1st2nLast_FieldCont.setHidden(true);
                    daysInMonth_MonSunWeekdays_FieldCont.setHidden(true);
                    break;
                case MONTHLY_OPTION_WEEK_NB:
                    //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
                    weeksInMonth_1st2ndLast_FieldCont.setHidden(false);
                    daysInWeek_MonSun_FieldCont.setHidden(false);

                    dayInMonth_1_31_Last_FieldFMCont.setHidden(true);
                    weekdaysInMonth_1st2nLast_FieldCont.setHidden(true);
                    daysInMonth_MonSunWeekdays_FieldCont.setHidden(true);
                    break;
                case MONTHLY_OPTION_WEEKDAYS:
                    //SELECT WEEKDAYS IN MONTH, e.g. 1st FRIDAY
                    weekdaysInMonth_1st2nLast_FieldCont.setHidden(false);
                    daysInMonth_MonSunWeekdays_FieldCont.setHidden(false);

                    dayInMonth_1_31_Last_FieldFMCont.setHidden(true);
                    //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
                    weeksInMonth_1st2ndLast_FieldCont.setHidden(true);
                    daysInWeek_MonSun_FieldCont.setHidden(true);
                    break;
            }
        }
    }

    private Date getStartDateToEditInPicker() {
        List<RepeatRuleObjectInterface> list = repeatRuleEdited.getListOfUndoneInstances();
        if (list.size() > 0 && list.get(0).getRepeatStartTime(false).getTime() != 0) {
            return list.get(0).getRepeatStartTime(false);  //false: not used if repeatOnCompletion, item.getRepeatStartTiem will return dueDate
        } else {
//            return new MyDate();
//            if (makeStartDate != null) { //Shouldn't happen!
            return (Date) makeStartDate.getVal(); //if no date was set by Item (yet) generate the same default value as in ScreenItem
        }
    }

    private void fieldsShow() {
        fieldsShow(true);
    }

    private void fieldsShow(boolean animate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            daysInWeek_MonSun_Field.setHidden(true);
//            yearlyChoice_DayMonths_Combo.setHidden(true);
//            dayInYear_1_365_Field.setHidden(true);
//            monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setHidden(true);
//            monthsInYear_JanDec_Field.setHidden(true);
//            repeatEndDateEditButton.setHidden(true);
//            repeatHowManyTimesField.setHidden(true);
//            showHowManyContainer.setHidden(true);
//            showNumberFutureRepeats.setHidden(true);
//            showNumberDaysAhead.setHidden(true);
//
//            intervalField_1_NN_TextField.setHidden(true);
//            dayInMonth_1_31_Last_FieldFM.setHidden(true);
//            weeksInMonth_1st2ndLast_Field.setHidden(true);
//            daysInWeek_MonSun_Field.setHidden(true);
//            weekdaysInMonth_1st2nLast_Field.setHidden(true);
//            daysInMonth_MonSunWeekend_Field.setHidden(true);
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (repeatFrom_CompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//            if (repeatStartDatePicker != null) {
//                repeatStartDatePicker.setHidden(false); //"due date
//            }
//            if (repeatStartDateLabel != null) {
//                repeatStartDateLabel.setHidden(false); //"due date
//            }
//        } else {
//            if (repeatStartDatePicker != null) {
//                repeatStartDatePicker.setHidden(true); //"due date
//            }
//            if (repeatStartDateLabel != null) {
//                repeatStartDateLabel.setHidden(true); //"due date
//            }
//        }
//</editor-fold>
//        int noneCompletedDue = repeatFrom_NoneCompletedDue_Field.getSelectedValue();
        if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT
                || (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE
                && onCompletionDatedRepeatsSwitch.isOff())) {
            showMonthlyFields(true);
            frequency_DailyWeekMonthYearly_FieldCont.setHidden(true);
            intervalField_1_NN_TextFieldCont.setHidden(true);
            daysInWeek_MonSun_FieldCont.setHidden(true);
            yearlyChoice_DayMonths_ComboCont.setHidden(true);
            dayInYear_1_365_FieldCont.setHidden(true);
            monthsInYear_JanDec_FieldCont.setHidden(true);
            repeatEndDateEditButtonCont.setHidden(true);
            repeatHowManyTimesFieldCont.setHidden(true);
            repeatStartDatePickerXXX.setHidden(true);
//            showHowManyContainer.setHidden(true);
            repeatHowLong_ForeverUntilNumber_ChoiceComboCont.setHidden(true);
            showNumberFutureRepeatsCont.setHidden(true);
            showNumberDaysAheadZZZ.setHidden(true);
            if (false) {
                showDatesButton.setHidden(true);
            }
//            onCompletionDatedRepeats.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT);
            onCompletionDatedRepeatsCont.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT
                    || isForWorkSlot);
        } else {
            boolean repeatFromCompleted = repeatFrom_NoneCompletedDue_Field.getSelectedAsInt()
                    == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE && onCompletionDatedRepeatsSwitch.isOn();
            boolean repeatFromDue = repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE;
//            onCompletionDatedRepeats.setHidden(!repeatFromCompleted);
            onCompletionDatedRepeatsCont.setHidden(!repeatFromCompleted || isForWorkSlot);
//            boolean hideDateFieldsXXX = onCompletionDatedRepeats.isOff()
//                    && repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE;
//            if (false &&onCompletionDatedRepeats.isOff()) {
//                //show nothing
//            } else {
            frequency_DailyWeekMonthYearly_FieldCont.setHidden(false);
            intervalField_1_NN_TextFieldCont.setHidden(false);
            boolean hideRepeatStartDatePicker = repeatFromCompleted || isForWorkSlot;
//            repeatStartDatePicker.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedValue() != RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE || isForWorkSlot); //"due date
            repeatStartDatePickerXXX.setHidden(hideRepeatStartDatePicker); //"due date
            if (!hideRepeatStartDatePicker && repeatStartDatePickerXXX.getDate().getTime() == 0) {//cker on first showupdate oi
//                if (defaultStartDate == null || defaultStartDate.getTime() == 0) {
////                    Dialog.show("INFO", "No Due date set for task, please set here.", "OK", null); //" + Item.DUE_DATE+"
//                    showToastBar("A Due date is needed when repeating from a date. Please set the date."); //" + Item.DUE_DATE+"
//                }//                if (makeStartDate != null && ((Date) makeStartDate.getVal()).getTime() != 0)
                if (false) {
                    if (itemDueDate != null && itemDueDate.getTime() != 0) {
                        repeatStartDatePickerXXX.setDate(new MyDate(itemDueDate.getTime())); //make copy to avoid reusing eg same value as edited in Picker in ScreenItem2
                    } else if (makeStartDate != null) {
                        repeatStartDatePickerXXX.setDate(((Date) makeStartDate.getVal()));
                    } else {
                        repeatStartDatePickerXXX.setDate(new MyDate(MyDate.currentTimeMillis()));
                    }
                } else {
//                    if (repeatStartDatePicker.getDate().getTime() != 0) //ALREADY checked above // only initialize once (but do it on show, 
                    Date startDate = getStartDateToEditInPicker();
                    repeatStartDatePickerXXX.setDate(startDate);
//                    if (defaultStartDate == null || defaultStartDate.getTime() == 0) {
                    if (startDate.getTime() == 0) {
                        //                    Dialog.show("INFO", "No Due date set for task, please set here.", "OK", null); //" + Item.DUE_DATE+"
//                        showToastBar("A Due date is needed when repeating from a date. Please set the date."); //" + Item.DUE_DATE+"
                        showToastBar("Please set the Due date for the repeating task"); //" + Item.DUE_DATE+"
                    }//                if (makeStartDate != null && ((Date) makeStartDate.getVal()).getTime() != 0)
                    animateLayout(300); //animate due date field *before*showing toastbar below
                }
//                    repeatStartDatePicker.setHidden(false);
                if (false) {
                    repeatStartDatePickerXXX.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() != RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE);
                }
//                Display.getInstance().getCurrent().animateLayout(300);
            }
            if (false) {
                showDatesButton.setHidden(!repeatFromDue); //only show for repeat from Due
            }//            repeatStartDatePicker.setHidden(isForWorkSlot);

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//            intervalFieldInfiniteList.setListModelValues(1, getFreqNumberInstances(frequency),
//                    "every", RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//        }
//
//        if (false) {
//            intervalField_1_NN_TextField.setHidden(false); //never hidden
//        }
//</editor-fold>
//        motherContainer.addComponent(monthlyRepeatTypeSelectionBox);
            switch (frequency_DailyWeekMonthYearly_Field.getSelectedAsInt()) {
                case RepeatRule.DAILY:
                    //Nothing for daily except interval
//<editor-fold defaultstate="collapsed" desc="comment">
//                dayInYear_1_365_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() == 0);
//                monthsInYear_JanDec_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != 0);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() == 0) {
//                    showMonthlyFields(true);
//                }
//</editor-fold>
                    showMonthlyFields(true);
                    daysInWeek_MonSun_FieldCont.setHidden(true);
                    yearlyChoice_DayMonths_ComboCont.setHidden(true);
                    dayInYear_1_365_FieldCont.setHidden(true);
                    monthsInYear_JanDec_FieldCont.setHidden(true);
                    break;
                case RepeatRule.WEEKLY:
                    showMonthlyFields(true);
                    daysInWeek_MonSun_FieldCont.setHidden(false);

                    yearlyChoice_DayMonths_ComboCont.setHidden(true);
                    dayInYear_1_365_FieldCont.setHidden(true);
                    monthsInYear_JanDec_FieldCont.setHidden(true);
                    break;
                case RepeatRule.MONTHLY:
                    showMonthlyFields(false);

//                daysInWeek_MonSun_Field.setHidden(true);
                    yearlyChoice_DayMonths_ComboCont.setHidden(true);
                    dayInYear_1_365_FieldCont.setHidden(true);
                    monthsInYear_JanDec_FieldCont.setHidden(true);
                    break;
                case RepeatRule.YEARLY:
                    showMonthlyFields(yearlyChoice_DayMonths_Combo.getSelectedAsInt() != YEAR_OPTION_MONTHS);
                    yearlyChoice_DayMonths_ComboCont.setHidden(false);
                    dayInYear_1_365_FieldCont.setHidden(yearlyChoice_DayMonths_Combo.getSelectedAsInt() != YEAR_OPTION_DAY_OF_YEAR);
                    monthsInYear_JanDec_FieldCont.setHidden(yearlyChoice_DayMonths_Combo.getSelectedAsInt() != YEAR_OPTION_MONTHS);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() != 0) {
//                    showMonthlyFields();
//                }

//                daysInWeek_MonSun_Field.setHidden(true);
//                showMonthlyFields(false);
                    break;
            }

            if (true) {
                repeatHowLong_ForeverUntilNumber_ChoiceComboCont.setHidden(false);
            }

            repeatEndDateEditButtonCont.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() != REPEAT_HOW_LONG_OPTION_UNTIL);
            repeatHowManyTimesFieldCont.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() != REPEAT_HOW_LONG_OPTION_NUMBER);

//        if (isForWorkSlot) {
// showHowManyContainer.setHidden(true);
//                showNumberFutureRepeats.setHidden(true);
//                showNumberDaysAhead.setHidden(true);
//        } else {
            if (true) {
                if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE || isForWorkSlot) {
//                showHowManyContainer.setHidden(false);
                    if (true || showHowMany_InstancesDaysAhead_ComboZZZ.getSelectedAsInt() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //Show how many REPEAT INSTANCED
                        showNumberFutureRepeatsCont.setHidden(false);
                        showNumberDaysAheadZZZ.setHidden(true);
                    } else {//Show how many DAYS AHEAD
                        showNumberFutureRepeatsCont.setHidden(true);
                        showNumberDaysAheadZZZ.setHidden(false);
                    }
                } else if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
//                showHowManyContainer.setHidden(true);
                    showNumberFutureRepeatsCont.setHidden(true);
                    showNumberDaysAheadZZZ.setHidden(true);
                }
            }
//        }
//            if (true) {
//                showDatesButton.setHidden(false);
//            }
        }

//        revalidateWithAnimationSafety();
        if (false) {
            if (animate) {
//            getContentPane().animateLayout(ANIMATION_TIME_DEFAULT);
//            getContentPane().animateHierarchy(ANIMATION_TIME_DEFAULT);
                getComponentForm().animateHierarchy(ANIMATION_TIME_DEFAULT);
            } else {
                getComponentForm().revalidateWithAnimationSafety();
            }
        }

        if (scrollEndRepeatFieldsToVisible) {
            getComponentForm().revalidateWithAnimationSafety();
            if (!showNumberFutureRepeatsCont.isHidden()) { //last field on screen so if visible just show that
                scrollComponentToVisible(showNumberFutureRepeatsCont);
                animate = true;
            } else if (!repeatEndDateEditButtonCont.isHidden()) {
                scrollComponentToVisible(repeatEndDateEditButtonCont);
                animate = true;
            } else if (!repeatHowManyTimesFieldCont.isHidden()) {
                scrollComponentToVisible(repeatHowManyTimesFieldCont);
                animate = true;
            }
            scrollEndRepeatFieldsToVisible = false;
            getComponentForm().animateHierarchy(ANIMATION_TIME_DEFAULT);
        } else {
            getComponentForm().animateHierarchy(ANIMATION_TIME_DEFAULT);
        }
    }

    /**
     * returns true if all necessary values are set, and false if not
     *
     * @param myRepeatRule
     * @return true if everything OK (no missing data)
     */
    private boolean restoreEditedFieldsToRepeatRule(RepeatRuleParseObject myRepeatRule) {

//        myRepeatRule.setRepeatRuleChanged(false); //reset marker
//        if (myRepeatRule == null) {
//            return;
//        }
        /**
         * is any fields missing in input (eg undefined
         */
        boolean missingData = false;
        String missingDataStr = "";
        int testVal;

        myRepeatRule.setRepeatType(repeatFrom_NoneCompletedDue_Field.getSelectedAsInt());
        if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
            return true; //!missingData;
        }

        if (true || !isForWorkSlot) {
//            if (repeatFrom_CompletedDue_Field != null) {
            if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    Log.l("setReferenceItemForDueDateAndFutureCopies(" + repeatRuleOwner + ")");
//                myRepeatRule.setReferenceItemForDueDateAndFutureCopies(repeatRuleOwner);
//                        myRepeatRule.setReferenceObjectForRepeatTime(repeatRuleOwner);
//                    myRepeatRule.setSpecifiedStartDate(repeatRuleOwner.getRepeatStartTime(false));
//                    myRepeatRule.setSpecifiedStartDate(repeatStartDate.getTime());
//                    if (repeatStartDatePicker != null) {
//                if (false) { //ignore this, RR startDate now always set to due date of originator
//                    Date editedStartDate = repeatStartDatePickerXXX.getDate();
//                    if (editedStartDate.getTime() != 0) {
//                        myRepeatRule.setSpecifiedStartDate(editedStartDate);
//                    } else {
//                        missingData = true;
//                        missingDataStr += Item.DUE_DATE;
//                    }
//                } else {
//</editor-fold>
                if (false && myRepeatRule.getSpecifiedStartDateZZZ().getTime() == 0) {//if no due date was set in the original task for the repeat rule, then use the first generated 
                    if (itemDueDate != null && itemDueDate.getTime() != 0) {
                        myRepeatRule.setSpecifiedStartDateXXXZZZ(itemDueDate); //TODO: what value to use if this case becomes relevant??
                    } else {
                        Date newStartDate = myRepeatRule.getFirstRepeatDateAfterTodayForWhenEditingRuleWithoutPredefinedDueDateN();
                        myRepeatRule.setSpecifiedStartDateXXXZZZ(newStartDate); //TODO: what value to use if this case becomes relevant??
                    }
                }
//                }
//                    } else {
//                        myRepeatRule.setSpecifiedStartDate(startDate);
//                    }
            } else if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
                Log.p("setReferenceItemForDueDateAndFutureCopies(null)");
                myRepeatRule.setDatedCompletion(onCompletionDatedRepeatsSwitch.isOn()); //store value
                myRepeatRule.setNumberSimultaneousRepeats(0);//reset a previously set value
                if (false && onCompletionDatedRepeatsSwitch.isOff()) { //false: no longer stores startDate in rule, always uses dates from (end-user visible) tasks
//                myRepeatRule.setReferenceItemForDueDateAndFutureCopies(null);
                    myRepeatRule.setSpecifiedStartDateXXXZZZ(new MyDate(0)); //reset startDate
                }
            } else { //MyRepeatRule.REPEAT_TYPE_FROM_SPECIFIED_DATE
//                    Log.p("setStartDate(" + MyDate.getNow() + ")");
                Log.p("setStartDate(" + MyDate.currentTimeMillis() + ")");
//                        myRepeatRule.setStartDate(repeatFromField.getTime());
//                    myRepeatRule.setSpecifiedStartDate(repeatFromField.getTime());
            }
//            } else {
//            }

            if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE
                    || (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE
                    && onCompletionDatedRepeatsSwitch.isOn())) {

//            if (repeatFrom_NoneCompletedDue_Field != null) {
                if (true || repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) { //test not needed, done above
//                        || repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_SPECIFIED_DATE) {
//                    if (showHowManyChoice.getSelectedIndex() == 0) //{"coming tasks", "days ahead"}
//                    if (showHowMany_InstancesDaysAhead_Combo.getSelectedIndex() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //{"coming tasks", "days ahead"}
                    if (true || showHowMany_InstancesDaysAhead_ComboZZZ.getSelectedAsInt() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //{"coming tasks", "days ahead"}
//                        Log.p("setNumberFutureRepeatsGeneratedAhead(" + showNumberFutureRepeats.getSelectedValue() + ")");
//                        myRepeatRule.setNumberFutureRepeatsToGenerateAhead(showNumberFutureRepeats.getSelectedValue());
                        Log.p("setNumberFutureRepeatsGeneratedAhead(" + showNumberFutureRepeats.getValueInt() + ")");
//                    myRepeatRule.setNumberSimultaneousRepeatsToGenerateAhead(showNumberFutureRepeats.getValueInt() - 1);
                        myRepeatRule.setNumberSimultaneousRepeats(showNumberFutureRepeats.getValueInt());
                        myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(0); //now done in setNumberSimultaneousRepeats()
                    } else if (showHowMany_InstancesDaysAhead_ComboZZZ.getSelectedAsInt() == SHOW_HOW_MANY_AHEAD_DAYS_AHEAD) { //{"coming tasks", "days ahead"}
                        // SHOW_HOW_MANY_AHEAD_DAYS_AHEAD
//                        Log.p("setNumberOfDaysRepeatsAreGeneratedAhead(" + daysAheadField.getSelectedValue() + ")");
//                        myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(daysAheadField.getSelectedValue());
//                            Log.p("setNumberOfDaysRepeatsAreGeneratedAhead(" + daysAheadField.getValue() + ")");
//                            myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(daysAheadField.getValue());
                        myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(showNumberDaysAheadZZZ.getValueInt());
                        myRepeatRule.setNumberSimultaneousRepeats(1); //now done in setNumberOfDaysRepeatsAreGeneratedAhead()
                    } else { // SHOW_HOW_MANY_AHEAD_DAYS_AHEAD
                        ASSERT.that("error");
                    }
                }
//            }
            }

            //if either not onCompleted or onCompleted AND dated is on (don't set these date fields for a undated onCompleted)
            if (repeatFrom_NoneCompletedDue_Field.getSelectedAsInt() != RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE
                    || onCompletionDatedRepeatsSwitch.isOn()) {
                int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedAsInt();
//        int frequency = frequencyChoice.getSelectedValue();

                myRepeatRule.setFrequency(frequency);
                Log.p("setFrequency(" + frequency + ")");

                if (false) {
//                myRepeatRule.setInterval(intervalField.getSelectedValue());
//                Log.p("setInterval(" + intervalField.getSelectedValue() + ")");
                }
                if (false) {
//                    String intervalStr = intervalField_1_NN_TextField.getText();
//                    if (intervalStr == null || intervalStr.length() == 0) {
//                        missingData = true;
//                        missingDataStr += true;
//                    }
//                    int interval = Integer.parseInt(intervalField_1_NN_TextField.getText());
//                    if (interval < 1) {
//                        missingData = true;
//                    }
                    int interval = intervalField_1_NN_TextField.getValueInt();
                    myRepeatRule.setInterval(interval);
                }
//                myRepeatRule.setInterval(intervalField_1_NN_TextField.getValue());
                myRepeatRule.setInterval(intervalField_1_NN_TextField.getValueInt());

                switch (frequency) {
                    case RepeatRule.DAILY:
                        break;
                    case RepeatRule.YEARLY:
//                if (yearlyChoice_DayMonths_Combo.getSelectedIndex() == YEAR_OPTION_DAY_OF_YEAR) { //"Select day of year", "Select month(s) of year"
                        if (yearlyChoice_DayMonths_Combo.getSelectedAsInt() == YEAR_OPTION_DAY_OF_YEAR) { //"Select day of year", "Select month(s) of year"
//                    myRepeatRule.setDayInYear(dayInYear_1_365_Field.getSelectedValue());
                            String dayInYearStr = dayInYear_1_365_Field.getText();
                            if (dayInYearStr == null || dayInYearStr.length() == 0) {
                                missingData = true;
                            }
                            int dayInYearInt = Integer.parseInt(dayInYear_1_365_Field.getText());
                            if (dayInYearInt < 1) {
                                missingData = true;
                            }
                            if (dayInYearInt > 365) {
                                dayInYearInt = 365; //UI: not allowed to enter day in year bigger than 365
                            }
                            myRepeatRule.setDayInYear(dayInYearInt);

                            myRepeatRule.setDayInMonth(0);
                            myRepeatRule.setWeeksInMonth(0);
                            myRepeatRule.setWeekdaysInMonth(0);
                            myRepeatRule.setDaysInWeek(0);
                            myRepeatRule.setMonthsInYear(0);
//                    break;
                        } else {
                            assert yearlyChoice_DayMonths_Combo.getSelectedAsInt() == YEAR_OPTION_MONTHS;
//                    myRepeatRule.setMonthsInYear(monthsInYearField.getSelectedOredTogether());
                            myRepeatRule.setMonthsInYear(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(monthsInYear_JanDec_Field.getSelectedValues()));
                            missingData = (testVal == 0) || missingData;
//                    if (monthlyChoiceContainer.getSelectedIndex() == 0) { //"Select day of month(s):" : "Select day of month:"
//                    if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
                            if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
//                        myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
                                myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
//                    } else if (monthlyChoiceContainer.getSelectedIndex() == 1) {
//                    } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEK_NB) {

                                myRepeatRule.setWeeksInMonth(0);
                                myRepeatRule.setWeekdaysInMonth(0);
                                myRepeatRule.setDaysInWeek(0);
                                myRepeatRule.setDayInYear(0);
                            } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_WEEK_NB) {
//                        myRepeatRule.setWeeksInMonth(weeksInMonthField.getSelectedOredTogether());
                                myRepeatRule.setWeeksInMonth(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(weeksInMonth_1st2ndLast_Field.getSelectedValues()));
                                missingData = (testVal == 0) || missingData;
//                        myRepeatRule.setDaysInWeek(daysInWeekField.getSelectedOredTogether());
                                myRepeatRule.setDaysInWeek(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(daysInWeek_MonSun_Field.getSelectedValues()));
                                missingData = (testVal == 0) || missingData;

                                myRepeatRule.setWeekdaysInMonth(0);
                                myRepeatRule.setDayInMonth(0);
                                myRepeatRule.setDayInYear(0);
//                        myRepeatRule.setMonthsInYear(0);

//                    } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEKDAYS) {
                            } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_WEEKDAYS) {
//                        myRepeatRule.setWeekdaysInMonth(weekdaysInMonthField.getSelectedOredTogether());
                                myRepeatRule.setWeekdaysInMonth(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(weekdaysInMonth_1st2nLast_Field.getSelectedValues()));
                                missingData = (testVal == 0) || missingData;
//                        myRepeatRule.setDaysInWeek(daysInMonthField.getSelectedOredTogether());
                                myRepeatRule.setDaysInWeek(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(daysInMonth_MonSunWeekdays_Field.getSelectedValues()));
                                missingData = (testVal == 0) || missingData;

                                myRepeatRule.setDayInMonth(0);
                                myRepeatRule.setWeeksInMonth(0);
                                myRepeatRule.setDayInYear(0);
//                        myRepeatRule.setMonthsInYear(0);
                            }
                        }
                        break;
                    //fall-through to set monthly fields:
                    case RepeatRule.MONTHLY:
//                if (monthlyChoiceContainer.getSelectedIndex() == 0) { //"Select day of month(s):" : "Select day of month:"
//                if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
                        if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
//                    myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
                            myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());

                            myRepeatRule.setWeeksInMonth(0);
                            myRepeatRule.setWeekdaysInMonth(0);
                            myRepeatRule.setDaysInWeek(0);
                            myRepeatRule.setDayInYear(0);
                            myRepeatRule.setMonthsInYear(0);

//                } else if (monthlyChoiceContainer.getSelectedIndex() == 1) {
//                } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEK_NB) { //"Select week of month(s):" 
                        } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_WEEK_NB) { //"Select week of month(s):" 
//                    myRepeatRule.setWeeksInMonth(weeksInMonthField.getSelectedOredTogether());
                            myRepeatRule.setWeeksInMonth(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(weeksInMonth_1st2ndLast_Field.getSelectedValues()));
                            missingData = (testVal == 0) || missingData;
//                    myRepeatRule.setDaysInWeek(daysInWeekField.getSelectedOredTogether());
                            myRepeatRule.setDaysInWeek(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(daysInWeek_MonSun_Field.getSelectedValues()));
                            missingData = (testVal == 0) || missingData;

                            myRepeatRule.setWeekdaysInMonth(0);
                            myRepeatRule.setDayInMonth(0);
                            myRepeatRule.setDayInYear(0);
                            myRepeatRule.setMonthsInYear(0);
//                    myRepeatRule.setMonthsInYear(monthsInYearField.getSelectedOredTogether());
//                } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEKDAYS) {
                        } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedAsInt() == MONTHLY_OPTION_WEEKDAYS) {
//                    myRepeatRule.setWeekdaysInMonth(weekdaysInMonthField.getSelectedOredTogether());
                            myRepeatRule.setWeekdaysInMonth(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(weekdaysInMonth_1st2nLast_Field.getSelectedValues()));
                            missingData = (testVal == 0) || missingData;
//                    myRepeatRule.setDaysInWeek(daysInMonthField.getSelectedOredTogether());
//                    myRepeatRule.setDaysInWeek(daysInMonthField.getSelectedOredTogether());
                            myRepeatRule.setDaysInWeek(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(daysInMonth_MonSunWeekdays_Field.getSelectedValues()));
                            missingData = (testVal == 0) || missingData;

                            myRepeatRule.setDayInMonth(0);
                            myRepeatRule.setWeeksInMonth(0);
                            myRepeatRule.setDayInYear(0);
                            myRepeatRule.setMonthsInYear(0);
                        }
                        break;
                    case RepeatRule.WEEKLY:
//                myRepeatRule.setDaysInWeek(daysInWeekField.getSelectedOredTogether());
                        myRepeatRule.setDaysInWeek(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(daysInWeek_MonSun_Field.getSelectedValues()));
                        missingData = (testVal == 0) || missingData;

                        myRepeatRule.setDayInMonth(0);
                        myRepeatRule.setWeeksInMonth(0);
                        myRepeatRule.setWeekdaysInMonth(0);
                        myRepeatRule.setDayInYear(0);
                        myRepeatRule.setMonthsInYear(0);
                        break;
                }

//        if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedIndex() == REPEAT_HOW_LONG_OPTION_FOREVER) { //{"Repeat forever", "Repeat until", "Repeat"}
                if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() == REPEAT_HOW_LONG_OPTION_FOREVER) { //{"Repeat forever", "Repeat until", "Repeat"}
//                myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE);
//                myRepeatRule.setNumberOfRepeats(0); //also sets endDate to 0
                    myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE); //also sets endDate 
                    myRepeatRule.setEndDate(new MyDate(MyDate.MAX_DATE));
//            myRepeatRule.setEndDate(0);
//        } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedIndex() == REPEAT_HOW_LONG_OPTION_UNTIL) { //Repeat until
                } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedAsInt() == REPEAT_HOW_LONG_OPTION_UNTIL) { //Repeat until
//            myRepeatRule.setEndDate(endDate.getTime() == 0 ? Long.MAX_VALUE : endDate.getTime());
//            myRepeatRule.setEndDate(endDate.getTime() == 0 ? RepeatRuleParseObject.MAX_DATE : endDate.getTime());
//                missingData = (repeatEndDateEditButton.getDate().getTime() == 0) || missingData;
                    missingData = (repeatEndDateEditButton.getDate().getTime() == 0) || missingData;
                    myRepeatRule.setEndDate(repeatEndDateEditButton.getDate().getTime() == 0 ? new MyDate(MyDate.MAX_DATE) : repeatEndDateEditButton.getDate());
                    myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE);
                } else { // REPEAT_HOW_LONG_OPTION_NUMBER
//            myRepeatRule.setNumberOfRepeats(repeatHowManyTimesField.getSelectedValue() == 0 ? Integer.MAX_VALUE : repeatHowManyTimesField.getSelectedValue());
                    myRepeatRule.setNumberOfRepeats(repeatHowManyTimesField.getValueInt() == 0 ? Integer.MAX_VALUE : repeatHowManyTimesField.getValueInt());
                    myRepeatRule.setEndDate(new MyDate(MyDate.MAX_DATE));
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//if no due date was set in the original task for the repeat rule, then use the first generated
//            if (myRepeatRule.getSpecifiedStartDate().getTime() == 0) {
//                List<Date> dates = myRepeatRule.generateListOfDates(new Date(MyDate.currentTimeMillis()), new Date(MyDate.MAX_DATE), 1, true, true); //generate first repeat date starting from now
//                ASSERT.that(dates.size() >= 1, () -> "RepeatRule did not generate a first repeat date, rule=" + myRepeatRule);
//                if (dates.size() >= 1) {
//                    Date defaultStartDate = dates.get(0);
//                    myRepeatRule.setSpecifiedStartDate(defaultStartDate);
//                }
//            }
//</editor-fold>
            }
        }
        return !missingData;
    }

//    void restoreEditedFieldsOnDoneXXX() {
////        repeatRuleOwner.setRepeatStartTime(repeatStartDatePicker.getDate()); //update due date in case it was changed while editing the repeat rule
//        if (!restoreEditedFieldsToRepeatRule(repeatRuleEdited)) {
//            Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
//        }
//    }
}

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void addMonthlyFieldsToContainerOLD(Container addToContainer, RepeatRuleParseObject myRepeatRule, boolean forYear) {
//
//        addToContainer.addComponent(monthlyRepeatType_DayWeekdaysWeeks_SelectionBox);
//
//        switch (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue()) {
//            case 0:
//                //SELECT DAY NUMBER IN MONTH, e.g. 3rd day - "Select day of month(s):"
//                addToContainer.addComponent(dayInMonth_1_31_Last_FieldFM);
//                break;
//            case 1:
//                //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
//                addToContainer.addComponent(weeksInMonth_1st2ndLast_Field);
//                addToContainer.addComponent(daysInWeek_MonSun_Field);
//                break;
//            case 2:
//                //SELECT WEEKDAYS IN MONTH, e.g. 1st FRIDAY
//                addToContainer.addComponent(weekdaysInMonth_1st2nLast_Field);
////                addToContainer.addComponent(weekdaysInMonthField);
//                addToContainer.addComponent(daysInMonth_MonSunWeekend_Field);
//                break;
//        }
//    }
//
//    private void updateFieldsOLD() {
//        motherContainer.removeAll();
//        motherContainer.addComponent(repeatFromDueOrCompletedFieldContainer);
//        if (repeatFrom_CompletedDue_Field.getSelectedValue() == 1) //"due date
//        {
//            motherContainer.addComponent(repeatStartDatePicker);
//        }
//
//        //FREQUENCY: DAILY/MONTHLY BASIS
//        motherContainer.addComponent(frequency_DailyWeekMonthYearly_Field);
//
//        //EVERY 1,2,3,.. DAYS/WEEKS/MONTHS/YEARS
//        motherContainer.addComponent(intervalField); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...
//
//        //ADAPT rest according to type
////        switch (myRepeatRule.getFrequency()) {
//        int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//        intervalFieldInfiniteList.setListModelValues(1, getFreqNumberInstances(frequency), "every", RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//
////        motherContainer.addComponent(monthlyRepeatTypeSelectionBox);
//        switch (frequency_DailyWeekMonthYearly_Field.getSelectedValue()) {
//            case RepeatRule.DAILY:
//                //Nothing for daily except interval
//                break;
//            case RepeatRule.WEEKLY:
//                motherContainer.addComponent(daysInWeek_MonSun_Field);
//                break;
//            case RepeatRule.MONTHLY:
//                addMonthlyFieldsToContainer(motherContainer, myRepeatRule, false);
//                break;
//            case RepeatRule.YEARLY:
//                motherContainer.addComponent(yearlyChoice_DayMonths_Combo);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() == YEAR_OPTION_DAY_OF_YEAR) {
//                    //Select day of year
//                    motherContainer.addComponent(dayInYear_1_365_Field);
//                } else { // YEAR_OPTION_MONTHS
//                    //Select yearly repeat on a monthly basis
//                    motherContainer.addComponent(monthsInYear_JanDec_Field);
//                    addMonthlyFieldsToContainer(motherContainer, myRepeatRule, true);
//                }
//                break;
//        }
//
//        motherContainer.addComponent(repeatHowLong_ForeverUntilNumber_ChoiceCombo);
//        //{"Repeat forever", "Repeat until", "Number of repeats"}
//        if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_UNTIL) {
////            motherContainer.addComponent(repeatFromField);
//            motherContainer.addComponent(repeatEndDateEditButton);
//        } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_NUMBER) {
//            motherContainer.addComponent(repeatHowManyTimesField);
//        }
//
//        //if Repeat from DUE DATE then add option to choose how many future instances to create (if repeat from Completed date, not ossible to create future instances with right date)
//        //{"completion date", "due date"}
//        if (false && isForWorkSlot) {
//        } else {
//            if (repeatFrom_CompletedDue_Field.getSelectedIndex() == 1) {
//                motherContainer.addComponent(showHowManyContainer);
//                if (showHowMany_InstancesDaysAhead_Combo.getSelectedValue() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //Show how many REPEAT INSTANCED
//                    motherContainer.addComponent(showNumberFutureRepeats);
//                } else {//Show how many DAYS AHEAD
//                    motherContainer.addComponent(showNumberDaysAhead);
//                }
//            }
//        }
//
//        motherContainer.addComponent(new Button(new Command("Show dates") {
//            public void actionPerformed(ActionEvent evt) {
//                RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
//                if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
//                    tempMyRepeatRule.showRepeatDates();
//                } else {
//                    Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
//                }
//            }
//        }));
//        this.animateLayout(400);
//
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void fieldsShowOLD() {
////        motherContainer.removeAll();
////        motherContainer.addComponent(repeatFromDueOrCompletedFieldContainer);
//        daysInWeek_MonSun_Field.setHidden(true);
//        yearlyChoice_DayMonths_Combo.setHidden(true);
//        dayInYear_1_365_Field.setHidden(true);
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setHidden(true);
//        monthsInYear_JanDec_Field.setHidden(true);
//        repeatEndDateEditButton.setHidden(true);
//        repeatHowManyTimesField.setHidden(true);
//        showHowManyContainer.setHidden(true);
//        showNumberFutureRepeats.setHidden(true);
//        showNumberDaysAhead.setHidden(true);
//
//        intervalField_1_NN_TextField.setHidden(true);
//        dayInMonth_1_31_Last_FieldFM.setHidden(true);
//        weeksInMonth_1st2ndLast_Field.setHidden(true);
//        daysInWeek_MonSun_Field.setHidden(true);
//        weekdaysInMonth_1st2nLast_Field.setHidden(true);
//        daysInMonth_MonSunWeekend_Field.setHidden(true);
//
//        if (repeatFrom_CompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//            if (repeatStartDatePicker != null) {
//                repeatStartDatePicker.setHidden(false); //"due date
//            }
//            if (repeatStartDateLabel != null) {
//                repeatStartDateLabel.setHidden(false); //"due date
//            }
//        } else {
////            repeatStartDatePicker.setHidden(true); //"due date
//            if (repeatStartDatePicker != null) {
//                repeatStartDatePicker.setHidden(true); //"due date
//            }
//            if (repeatStartDateLabel != null) {
//                repeatStartDateLabel.setHidden(true); //"due date
//            }
//        }//        repeatStartDatePicker.setHidden(false); //"due date
////        {
////            motherContainer.addComponent(repeatStartDatePicker);
////        }
//
//        //FREQUENCY: DAILY/MONTHLY BASIS
////        motherContainer.addComponent(frequencyField);
//        //EVERY 1,2,3,.. DAYS/WEEKS/MONTHS/YEARS
////        motherContainer.addComponent(intervalField); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...
//        //ADAPT rest according to type
////        switch (myRepeatRule.getFrequency()) {
//        int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//        intervalFieldInfiniteList.setListModelValues(1, getFreqNumberInstances(frequency),
//                "every", RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//
//        intervalField_1_NN_TextField.setHidden(false);
//
////        motherContainer.addComponent(monthlyRepeatTypeSelectionBox);
//        switch (frequency_DailyWeekMonthYearly_Field.getSelectedValue()) {
//            case RepeatRule.DAILY:
//                //Nothing for daily except interval
//                break;
//            case RepeatRule.WEEKLY:
////                motherContainer.addComponent(daysInWeekField);
//                daysInWeek_MonSun_Field.setHidden(false);
//                break;
//            case RepeatRule.MONTHLY:
////                addMonthlyFieldsToContainer(motherContainer, myRepeatRule, false);
//                showMonthlyFields();
//                break;
//            case RepeatRule.YEARLY:
////                motherContainer.addComponent(yearlyChoiceCombo);
//                yearlyChoice_DayMonths_Combo.setHidden(false);
////                if (yearlyChoiceCombo.getSelectedValue() == 0) {
////                    //Select day of year
////                    motherContainer.addComponent(dayInYearField);
////                } else {
////                    //Select yearly repeat on a monthly basis
////                    motherContainer.addComponent(monthsInYearField);
////                    addMonthlyFieldsToContainer(motherContainer, myRepeatRule, true);
////                }
//                dayInYear_1_365_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != YEAR_OPTION_DAY_OF_YEAR);
//                monthsInYear_JanDec_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != YEAR_OPTION_MONTHS);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() != YEAR_OPTION_DAY_OF_YEAR) {
//                    showMonthlyFields();
//                }
//                break;
//        }
//
////        motherContainer.addComponent(repeatHowLongChoiceCombo);
//        repeatHowLong_ForeverUntilNumber_ChoiceCombo.setHidden(false);
//        //{"Repeat forever", "Repeat until", "Number of repeats"}
////        if (repeatHowLongChoiceCombo.getSelectedValue() == 1) {
//////            motherContainer.addComponent(repeatFromField);
////            motherContainer.addComponent(repeatEndDateEditButton);
////        } else if (repeatHowLongChoiceCombo.getSelectedValue() == 2) {
////            motherContainer.addComponent(repeatHowManyTimesField);
////        }
//        repeatEndDateEditButton.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() != REPEAT_HOW_LONG_OPTION_UNTIL);
//        repeatHowManyTimesField.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() != REPEAT_HOW_LONG_OPTION_NUMBER);
//
//        //if Repeat from DUE DATE then add option to choose how many future instances to create (if repeat from Completed date, not ossible to create future instances with right date)
//        //{"completion date", "due date"}
////        if (!isForWorkSlot && repeatFromDueOrCompletedField.getSelectedIndex() == 1) {
////            motherContainer.addComponent(showHowManyContainer);
////            if (showHowManyCombo.getSelectedValue() == 0) { //Show how many REPEAT INSTANCED
////                motherContainer.addComponent(showNumberFutureRepeats);
////            } else {//Show how many DAYS AHEAD
////                motherContainer.addComponent(showNumberDaysAhead);
////            }
////        }
////        if (!isForWorkSlot && repeatFrom_CompletedDue_Field.getSelectedIndex() == 1) {
//        if (false && !isForWorkSlot) {
//
//        } else {
//            if (repeatFrom_CompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//                showHowManyContainer.setHidden(false);
//                if (showHowMany_InstancesDaysAhead_Combo.getSelectedValue() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //Show how many REPEAT INSTANCED
//                    showNumberFutureRepeats.setHidden(false);
//                } else {//Show how many DAYS AHEAD
//                    showNumberDaysAhead.setHidden(false);
//                }
//            }
//        }
//
////        motherContainer.addComponent(new Button(new Command("Show dates") {
////            public void actionPerformed(ActionEvent evt) {
////                RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
////                if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
////                    tempMyRepeatRule.showRepeatDates();
////                } else {
////                    Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
////                }
////            }
////        }));
////        motherContainer.addComponent(new Button(new Command("Show dates") {
////            public void actionPerformed(ActionEvent evt) {
////                RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
////                if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
////                    tempMyRepeatRule.showRepeatDates();
////                } else {
////                    Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
////                }
////            }
////        }));
//        showDatesButton.setHidden(false);
//
////        this.animateLayout(300);
//        getContentPane().animateLayout(300);
//    }
//</editor-fold>

