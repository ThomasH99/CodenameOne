/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.ComponentGroup;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
//import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.parse4cn1.ParseObject;
import java.util.Date;
import java.util.List;

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
    private Container motherContainer;
    private Container repeatRuleDetailsContainer;
//    MyDate defaultRepeatFromDate;
    private boolean allowEditingStartDate;
    private SelectionListener refreshSelectionListener;
    private RepeatRuleParseObject myRepeatRule;
    private RepeatRuleParseObject myRepeatRuleEdited;
    private MyDatePicker repeatEndDateEditButton;
//    MyDate endDate;
    private Date endDate;
    private Date startDate;
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
    private MyDateAndTimePicker repeatStartDatePicker;
    private Label repeatStartDateLabel;
//    ComboBoxLogicalNames repeatFromDueOrCompletedField;
    private MyToggleButton repeatFrom_NoneCompletedDue_Field;
    private Container repeatFromDueOrCompletedFieldContainer;
//    ComboBoxOffset repeatHowManyTimesField;
//    MyIntPicker repeatHowManyTimesField;
    private MyIntTextField repeatHowManyTimesField;
//    private ComboBoxOffset intervalField;
//    private ListModelInfinite intervalFieldInfiniteList;
    private MyIntTextField intervalField_1_NN_TextField;
//    ComboBoxOffset daysAheadField;
//    private MyIntPicker daysAheadField;
//    ComboBoxLogicalMultiSelection daysInWeekField;
    private MyToggleButton daysInWeek_MonSun_Field;
//    ComboBoxLogicalMultiSelection daysInMonthField;
    private MyToggleButton daysInMonth_MonSunWeekdays_Field;
//    ComboBoxLogicalMultiSelection weeksInMonthField;
    private MyToggleButton weeksInMonth_1st2ndLast_Field;
//    ComboBoxLogicalMultiSelection weekdaysInMonthField;
    private MyToggleButton weekdaysInMonth_1st2nLast_Field;
//    ComboBoxLogicalMultiSelection monthsInYearField;
    private MyToggleButton monthsInYear_JanDec_Field;
//    ComboBoxLogicalNames monthlyRepeatTypeSelectionBox;
    private MyToggleButton monthlyRepeatType_DayWeekdaysWeeks_SelectionBox;
//    ComboBoxLogicalNames frequencyField;
    private MyToggleButton frequency_DailyWeekMonthYearly_Field;
    private ComboBoxOffsetNew dayInMonth_1_31_Last_FieldFM;
//    TextField dayInYear_1_365_Field;
    private MyIntTextField dayInYear_1_365_Field;
//    ComboBoxOffset showNumberFutureRepeats;
    private MyIntPicker showNumberFutureRepeats;
//    ComboBoxOffset showNumberDaysAhead;
    private MyIntPicker showNumberDaysAhead;
//    ComboBoxLogicalNames repeatHowLongChoiceCombo;
    private MyToggleButton repeatHowLong_ForeverUntilNumber_ChoiceCombo;
//    ComboBoxLogicalNames yearlyChoiceCombo;
    private MyToggleButton yearlyChoice_DayMonths_Combo;
    private Container showHowManyContainer;
//    ComboBoxLogicalNames showHowManyCombo;
    private MyToggleButton showHowMany_InstancesDaysAhead_Combo;
    private Button showDatesButton;
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
    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm, UpdateField doneAction, boolean allowEditingStartDate) {
        this(title, repeatRule, null, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, null);
    }

    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleParseObject repeatRuleEdited, RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm, UpdateField doneAction, boolean allowEditingStartDate, Date defaultStartDate) {
        this(title, repeatRule, repeatRuleEdited, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, defaultStartDate, false);
    }

    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule,
            RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm,
            UpdateField doneAction, boolean allowEditingStartDate, Date defaultStartDate, boolean isForWorkSlot) {
        this(title, repeatRule, null, repeatRuleOriginator, previousForm, doneAction, allowEditingStartDate, defaultStartDate, isForWorkSlot);
    }

    public ScreenRepeatRule(String title, RepeatRuleParseObject repeatRule, RepeatRuleParseObject repeatRuleEdited,
            RepeatRuleObjectInterface repeatRuleOriginator, MyForm previousForm,
            UpdateField doneAction, boolean allowEditingStartDate, Date defaultStartDate, boolean isForWorkSlot) {
//        super(title, repeatRule);
        super(title, previousForm, doneAction);
        setUniqueFormId("ScreenEditRepeatRule");
//        this.defaultRepeatFromDate = defaultRepeatFromDate;
//        this.repeatRuleOwner = (RepeatRuleObject) repeatRule.getOwner();
        this.repeatRuleOwner = repeatRuleOriginator;
        this.allowEditingStartDate = allowEditingStartDate;
//        this.myRepeatRule = (RepeatRuleParseObject) value;
        this.myRepeatRule = repeatRule;
        this.myRepeatRuleEdited = repeatRuleEdited; //the editable/edited copy of the repeatRule, used to edit only the end-user edtaible fields
        if (myRepeatRuleEdited != null && (this.startDate = myRepeatRuleEdited.getSpecifiedStartDateD()).getTime() == 0) {
            this.startDate = defaultStartDate; //only use defaultStartDate if rule doesn't have one already
//            this.startDate = myRepeatRule.getSpecifiedStartDateD(); //use rule's existing startDate if it has one
//        } else {
//            this.startDate = defaultStartDate; //only use startDate if rule doesn't have one already
        }
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
        super.refreshAfterEdit();
//         getContentPane().removeAll();
//         buildContentPane(getContentPane());
        setupLayoutAndFields();
        restoreKeepPos();
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
    public void addCommandsToToolbar(Toolbar toolbar) { //, Resources theme) {

        //DONE
//        Command cmd = makeDoneUpdateWithParseIdMapCommand();
        Command cmd = new Command(null, Icons.iconBackToPrevFormToolbarStyle()) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
                if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) { //TODO optimization: inefficient to restore twice just to test the rule
                    restoreEditedFieldsToRepeatRule(myRepeatRuleEdited);
                    DAO.getInstance().save((ParseObject) repeatRuleOwner); //if a new Item, must save before creating repeatInstances in putEditedValues2:
                    putEditedValues2(parseIdMap2);
//                    updateActionOnDone.update();
                    getUpdateActionOnDone().update();
//                    showPreviousScreenOrDefault(previousForm, true); //false);
                    showPreviousScreenOrDefault(true); //false);
                } else {
                    Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
                }
            }
        };
        cmd.putClientProperty("android:showAsAction", "withText");

        toolbar.addCommandToLeftBar(cmd);

        //CANCEL
        if (MyPrefs.getBoolean(MyPrefs.enableCancelInAllScreens)) {
            toolbar.addCommandToOverflowMenu(makeCancelCommand());
        }

        //DELETE
        //TODO!! not needed, two options to delete: swipeClear or set RR to 'None'
        if (false) {
            if (myRepeatRule != null) {
                toolbar.addCommandToOverflowMenu("Delete", null, (e) -> {
                    myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(repeatRuleOwner); //delete rule and all other instances than this one (currently edited)
                    myRepeatRule = null;
//                    showPreviousScreenOrDefault(previousForm, false);
                    showPreviousScreenOrDefault(false);
                });
            }
        }

        //SHOW TASKS
        Container tasks = new Container();
        if (myRepeatRule != null) {
            if (isForWorkSlot) {
                toolbar.addCommandToOverflowMenu("Show workslots", null, (e) -> {
                    //TODO!!! as popup dialog, show [3 completed >] task1 task2 task3
                    for (Object slot : myRepeatRule.getListOfUndoneRepeatInstances()) {
                        if (slot instanceof WorkSlot) {
                            tasks.add(((WorkSlot) slot).getText());
                        }
                    }
//                Command exit = Command.create("Exit", null, (evt) -> {  });
//                Command exit = Command.create("Exit", null, (evt) -> {  });
                    Dialog.show("WorkSlots", tasks, new Command("Exit"), new Command("Past " + myRepeatRule.getTotalNumberOfInstancesGeneratedSoFar() + " WorkSlots"));
                    Dialog.show("WorkSlots", "", "OK", "Exit");
                });
            } else {
                toolbar.addCommandToOverflowMenu("Show tasks", null, (e) -> {
                    //TODO!!! as popup dialog, show [3 completed >] task1 task2 task3
                    for (Object item : myRepeatRule.getListOfUndoneRepeatInstances()) {
                        if (item instanceof Item) {
                            tasks.add(((Item) item).getText());
                        }
                    }
                    Dialog.show("Tasks", tasks, new Command("Exit"), new Command("Past tasks"));
                });
            }
        }

        //SIMULATE DATES
        toolbar.addCommandToOverflowMenu("Simulate dates", null, (e) -> {
            RepeatRuleParseObject tempMyRepeatRule = new RepeatRuleParseObject();
            if (restoreEditedFieldsToRepeatRule(tempMyRepeatRule)) {
                tempMyRepeatRule.showRepeatDueDates();
            } else {
                Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
            }
        });

        toolbar.addCommandToOverflowMenu("Run tests", null, (e) -> {
            RepeatRuleParseObject.testRepeatRules();
        });

    }

    /**
     * repeat the number of instances to show in dropdown menues for each of the
     * frequencies, e.g. 520 for Weeks == 10 years
     */
//    private int getFreqNumberInstances(int freq) {
//        return (freq == RepeatRule.DAILY ? MyDate.DAYS_IN_YEAR * 10 : (freq == RepeatRule.WEEKLY ? MyDate.WEEKS_IN_YEAR * 10 : (freq == RepeatRule.MONTHLY ? MyDate.MONTHS_IN_YEAR * 10 : 10)));
//    }
    private final int MONTHLY_OPTION_DAY = 0;
    private final int MONTHLY_OPTION_WEEK_NB = 2;
    private final int MONTHLY_OPTION_WEEKDAYS = 1;
    //{"day", "month(s)"}, new int[]{0, 1}
    private final int YEAR_OPTION_DAY_OF_YEAR = 0;
    private final int YEAR_OPTION_MONTHS = 1;
    //"forever", "until", "number"}, new int[]{0, 1, 2
    public final static int REPEAT_HOW_LONG_OPTION_FOREVER = 0;
    public final static int REPEAT_HOW_LONG_OPTION_UNTIL = 1;
    public final static int REPEAT_HOW_LONG_OPTION_NUMBER = 2;
    //    showHowMany_InstancesDaysAhead_Combo = new MyToggleButton(new String[]{"instances", "days ahead"}, new int[]{0, 1});
    private final int SHOW_HOW_MANY_AHEAD_INSTANCES = 0;
    private final int SHOW_HOW_MANY_AHEAD_DAYS_AHEAD = 1;

    protected void setupLayoutAndFields() {
//        if (!initialized) {
        parseIdMapReset();

        setLayout(BoxLayout.y());
        setScrollableY(true);
        motherContainer = getContentPane();

        if (myRepeatRule != null && myRepeatRule.getListOfUndoneRepeatInstances().size() > 0) {
//                        SpanLabel itemHierarchyContainer = new SpanLabel(hierarchyStr);
            repeatRuleDetailsContainer = new Container();
            Container repeatRuleHideableDetailsContainer = new Container();

            Button buttonRepeatRuleHistory = new Button();
            repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean()); //UI: default hidden
            buttonRepeatRuleHistory.setIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
            buttonRepeatRuleHistory.addActionListener((e) -> {
                if (repeatRuleHideableDetailsContainer.getComponentCount() == 0) { //lazy evaluation
                    List<RepeatRuleObjectInterface> list = myRepeatRule.getListOfUndoneRepeatInstances();
                    long now = System.currentTimeMillis(); //use a single value of now 
                    for (int i = 0, size = list.size(); i < size; i++) {
                        RepeatRuleObjectInterface item = list.get(i);
                        if (item instanceof Item) {
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer((Item) item, null, () -> false, null, false, null));
                            repeatRuleHideableDetailsContainer.add(ScreenListOfItems.buildItemContainer(ScreenRepeatRule.this, (Item) item, null, null));
                        } else if (item instanceof WorkSlot) {
//                            repeatRuleHideableDetailsContainer.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) item, () -> refreshAfterEdit(), null));
                            repeatRuleHideableDetailsContainer.add(ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) item, ScreenRepeatRule.this, null, false, false, now));
                        }
                    }
                    if (myRepeatRule.getLatestDateCompletedOrCancelled().getTime() != 0) {
                        repeatRuleHideableDetailsContainer.add(Format.f("Total number of repeats {0}", "" + myRepeatRule.getTotalNumberOfInstancesGeneratedSoFar()));
                    }
//                    repeatRuleHideableDetailsContainer.add(Format.f("Due date of last completed task {0}", MyDate.formatDateNew(myRepeatRule.getLatestDateCompletedOrCancelled())));
                    repeatRuleHideableDetailsContainer.add(Format.f(
                            "Last Due date of all completed tasks {0}", MyDate.formatDateNew(myRepeatRule.getLatestDateCompletedOrCancelled())));
                    repeatRuleHideableDetailsContainer.add(new Button(MyReplayCommand.create("Show all tasks", Format.f("Show all {0 getTotalNumberOfInstancesGeneratedSoFar} tasks", "" + myRepeatRule.getTotalNumberOfInstancesGeneratedSoFar()), null, (ev) -> {
//                            DAO.getInstance().getAllItemsForRepeatRule(myRepeatRule);
                        new ScreenListOfItems("", new ItemList(DAO.getInstance().getAllItemsForRepeatRule(myRepeatRule), true), (MyForm) motherContainer.getComponentForm(), (i) -> {
                            refreshAfterEdit();
                        }, ScreenListOfItems.OPTION_DISABLE_DRAG_AND_DROP | ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                                | ScreenListOfItems.OPTION_NO_INTERRUPT | ScreenListOfItems.OPTION_NO_TIMER | ScreenListOfItems.OPTION_NO_WORK_TIME).show();

                    }))); //TODO!!!!
                }
//                repeatRuleHideableDetailsContainer.setHidden(!repeatRuleHideableDetailsContainer.isHidden());
                MyPrefs.repeatHidePreviousTasksDetails.flipBoolean();
                repeatRuleHideableDetailsContainer.setHidden(MyPrefs.repeatHidePreviousTasksDetails.getBoolean());
                buttonRepeatRuleHistory.setIcon(repeatRuleHideableDetailsContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
                animateLayout(300);
            });
//            motherContainer.add(BorderLayout.center(FlowLayout.encloseCenter(new Label(myRepeatRule.getListOfUndoneRepeatInstances().size() + " tasks")))
//            repeatRuleDetailsContainer = BorderLayout.center(FlowLayout.encloseCenter(new Label(myRepeatRule.getListOfUndoneRepeatInstances().size() + " tasks")))
//            repeatRuleDetailsContainer = BorderLayout.center(FlowLayout.encloseCenter(new Label(Format.f("{0 total number repeats generated so far} tasks, {1 } active",
            repeatRuleDetailsContainer = BorderLayout.center(FlowLayout.encloseCenter(new Label(Format.f("{0 total number repeats generated so far} tasks, {1 tasksCreatedNotDoneYet} active",
                    "" + myRepeatRule.getTotalNumberOfInstancesGeneratedSoFar(),
                    "" + myRepeatRule.getListOfUndoneRepeatInstances().size()))))
                    .add(BorderLayout.EAST, buttonRepeatRuleHistory).add(BorderLayout.SOUTH, repeatRuleHideableDetailsContainer);
        }

//        daysInWeekField = new ComboBoxLogicalMultiSelection(new int[]{RepeatRule.MONDAY, RepeatRule.TUESDAY, RepeatRule.WEDNESDAY, RepeatRule.THURSDAY, RepeatRule.FRIDAY, RepeatRule.SATURDAY, RepeatRule.SUNDAY}, new String[]{MyDate.MONDAY, MyDate.TUESDAY, MyDate.WEDNESDAY, MyDate.THURSDAY, MyDate.FRIDAY, MyDate.SATURDAY, MyDate.SUNDAY});
        int frequency = myRepeatRuleEdited.getFrequency();
        //Repeat from "completion date", "due date", "today"
        if (isForWorkSlot) {
//            repeatFromDueOrCompletedField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleTypeNumbers(), new String[]{"inactive** date", "start date"}, myRepeatRule.getRepeatFromDueOrCompleted());
//            repeatFrom_CompletedDue_Field = new MyToggleButton(new String[]{"inactive** date", "start date"}, RepeatRuleParseObject.getRepeatRuleTypeNumbers(), myRepeatRule.getRepeatType());
            //set to Due and hide the field
            repeatFrom_NoneCompletedDue_Field = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleTypeNames(), RepeatRuleParseObject.getRepeatRuleTypeNumbers(), RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE); //"completion date", "due date", "today"
            repeatFrom_NoneCompletedDue_Field.setHidden(true);
        } else {
//            repeatFromDueOrCompletedField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleTypeNumbers(), MyRepeatRule.getRepeatRuleTypeNames(), myRepeatRule.getRepeatFromDueOrCompleted()); //"completion date", "due date", "today"
            repeatFrom_NoneCompletedDue_Field = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleTypeNames(), RepeatRuleParseObject.getRepeatRuleTypeNumbers(), myRepeatRuleEdited.getRepeatType()); //"completion date", "due date", "today"
        }
//        repeatFrom_CompletedDue_Field.setContainer(new Container(new GridLayout(2)));

        refreshSelectionListener = new SelectionListener() {
            public void selectionChanged(int oldSelected, int newSelected) {
                if (oldSelected != newSelected) {
//                    updateFields(); //refresh the layout based on changes in choice
                    fieldsShow();
                }
            }
        };
        repeatFrom_NoneCompletedDue_Field.addSelectionListener(refreshSelectionListener);
        repeatFrom_NoneCompletedDue_Field.addSelectionListener((oldSel, newSel) -> {
            if (oldSel != newSel && newSel == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE && repeatStartDatePicker != null && repeatStartDatePicker.getDate().getTime() == 0) {
                Dialog.show("INFO", "Please set the " + Item.DUE_DATE, "OK", null);
            }

        });
//        repeatFromDueOrCompletedFieldContainer = createLabelAndFieldContainer("Repeat from", repeatFromDueOrCompletedField);
        repeatFromDueOrCompletedFieldContainer = BoxLayout.encloseY(new Label("Repeat"), repeatFrom_NoneCompletedDue_Field);

//        repeatStartDatePicker = new EditField(new MyDate(repeatRuleOwner.getRepeatStartTime(false)), allowEditingStartDate);
//        repeatStartDatePicker = new EditField(repeatRuleOwner.getRepeatStartTime(false), allowEditingStartDate);
//        MyDatePicker hideUntil = new MyDatePicker("<hide task until>", parseIdMap2, () -> item.getHideUntilDateD(), (d) -> item.setHideUntilDateD(d));
//        if (false && repeatStartDatePicker == null) {
//            repeatStartDatePicker = new MyDateAndTimePicker("<hide task until>", parseIdMap2, () -> repeatRuleOwner.getRepeatStartTime(false), (d) -> repeatRuleOwner.setRepeatStartTime(d)); //OK
//            repeatStartDatePicker = new MyDateAndTimePicker(repeatRuleOwner.getRepeatStartTime(false), "<hide task until>"); //OK
//if (startDate.getTime()!=null)
        repeatStartDatePicker = new MyDateAndTimePicker(startDate, null); //"<no Start date**>"
        ASSERT.that(repeatStartDatePicker.getDate().getTime() == startDate.getTime(), "Picker changes date, org=" + startDate + ", picker=" + repeatStartDatePicker.getDate());
        if (false && isForWorkSlot) {
            repeatStartDatePicker.setHidden(true);
        }
//else
//        repeatStartDatePicker = new MyDateAndTimePicker(startDate, "<no Start date**>"); //OK
//        } else {
//            repeatStartDateLabel=new Label(MyDate.formatDateTimeNew(endDate));
//        }
        //FREQUENCY: DAILY/MONTHLY/ BASIS
//        frequencyField = new MyToggleButton(MyRepeatRule.getRepeatRuleFrequencyNames(), MyRepeatRule.getRepeatRuleFrequencyNumbers(), frequency);
//        frequencyField = new ComboBoxLogicalNames(MyRepeatRule.getRepeatRuleFrequencyNumbers(), MyRepeatRule.getRepeatRuleFrequencyNames(), frequency);
        frequency_DailyWeekMonthYearly_Field = new MyToggleButton(RepeatRuleParseObject.getRepeatRuleFrequencyNames(), RepeatRuleParseObject.getRepeatRuleFrequencyNumbers(), frequency);
//        frequency_DailyWeekMonthYearly_Field.setContainer(new Container(new GridLayout(4)));
        frequency_DailyWeekMonthYearly_Field.addSelectionListener(refreshSelectionListener);

        //EVERY 1,2,3,.. (DAYS/WEEKS/MONTHS/YEARS)
//        intervalFieldInfiniteList = new ListModelInfinite(1, getFreqNumberInstances(frequency), "every", RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//        intervalField = new ComboBoxOffset(intervalFieldInfiniteList);
//        intervalField.setSelectedValue(myRepeatRule.getInterval());
//        intervalField_1_NN_TextField = new TextField("" + myRepeatRule.getInterval(), "<interval>", 10, TextField.NUMERIC);
        intervalField_1_NN_TextField = new MyIntTextField(myRepeatRuleEdited.getInterval(), "**", 1, MyPrefs.repeatMaxInterval.getInt(), 1);

        //Set up all fields with default selected valued
        //DAYS IN WEEK: MONDAY[ ] TUESDAY[ ]...
//        daysInWeekField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getDayInWeekNumbers(), MyDate.getWeekDayNamesMondayFirst());
//        daysInWeekField.setSelectedOredTogether(myRepeatRule.getDaysInWeek());
        daysInWeek_MonSun_Field = new MyToggleButton(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(),
                myRepeatRuleEdited.getDaysInWeekAsVector(), -1, true, new ComponentGroup[2], new int[]{5, 2});
//        daysInWeekField = new MyComponentGroup(MyDate.getShortWeekDayNamesMondayFirst(), RepeatRuleParseObject.getDayInWeekNumbers(), myRepeatRule.getDaysInWeekAsVector(), true);
//        daysInWeek_MonSun_Field.setContainer(new Container(new GridLayout(7)));

        //1-365
//        dayInYear_1_365_Field = new ComboBoxOffset(new ListModelInfinite(1, 365, false));
//        dayInYear_1_365_Field.setSelectedValue(myRepeatRule.getDayInYear() > 0 ? myRepeatRule.getDayInYear() : 1); //TODO default should be 182 to start in the mid of the year...
//        dayInYear_1_365_Field = new TextField("" + myRepeatRule.getInterval(), "<interval>", 10, TextField.NUMERIC);
//        dayInYear_1_365_Field.setText("" + (myRepeatRule.getDayInYear() > 0 ? myRepeatRule.getDayInYear() : 1)); //TODO default should be 182 to start in the mid of the year...
//        dayInYear_1_365_Field = new MyIntTextField(myRepeatRule.getInterval(), "<day in year>", 1, 365, 1);
        dayInYear_1_365_Field = new MyIntTextField(myRepeatRuleEdited.getDayInYear() > 0 ? myRepeatRuleEdited.getDayInYear() : 1,
                "<day in year>", 1, 365, 1);

        //Jan-Dec
//        monthsInYearField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getMonthInYearNumbers(), MyDate.getMonthNames());
        monthsInYear_JanDec_Field = new MyToggleButton(MyDate.getShortMonthNames(), RepeatRuleParseObject.getMonthInYearNumbers(), myRepeatRuleEdited.getMonthInYearAsVector(), -1, true, new ComponentGroup[2], new int[]{6, 6});
//        monthsInYearField.setSelectedOredTogether(myRepeatRule.getMonthInYear());

        //Mon-Sun+Weekdays+Weekends
//        daysInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getDayInWeekNumbersInclWeekdays(), MyDate.getWeekDayNamesMondayFirstInclWeekdays());
        daysInMonth_MonSunWeekdays_Field = new MyToggleButton(MyDate.getShortWeekDayNamesMondayFirstInclWeekdays(), RepeatRuleParseObject.getDayInWeekNumbersInclWeekdays(),
                myRepeatRuleEdited.getDaysInWeekAsVectorInclWeekdays(), -1, true, new ComponentGroup[2], new int[]{5, 4});
//        daysInMonth_MonSunWeekend_Field.setContainer(new Container(new GridLayout(2, 7)));
//        daysInMonthField.setSelectedOredTogether(myRepeatRule.getDaysInWeek());

        //1-31+Last
//        dayInMonth_1_31_Last_FieldFM = new ComboBoxOffset(new ListModelInfinite(1, 32, true, "Last"));
        dayInMonth_1_31_Last_FieldFM = new ComboBoxOffsetNew();
//        dayInMonth_1_31_Last_FieldFM.setSelectedValue(myRepeatRule.getDayInMonth() > 0 ? myRepeatRule.getDayInMonth() : 1);
        dayInMonth_1_31_Last_FieldFM.setSelectedValue(myRepeatRuleEdited.getDayInMonth() > 0 ? myRepeatRuleEdited.getDayInMonth() : 1);

        //1st..5th, last..5th last (of week)
//        weeksInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getWeekInMonthNumbers(), MyRepeatRule.getWeekInMonthNames());
        weeksInMonth_1st2ndLast_Field = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNamesShort(), RepeatRuleParseObject.getWeekInMonthNumbersShort(), myRepeatRuleEdited.getWeekInMonthAsVector(), true);
        //1st..5th+last
//        weeksInMonth_1st2ndLast_Field.setContainer(new Container(new TableLayout(2, 5)));
//        weeksInMonthField.setSelectedOredTogether(myRepeatRule.getWeekInMonth());

        //1st..5th, last..5th last (of Mon-Sun)
//        weekdaysInMonthField = new ComboBoxLogicalMultiSelection(MyRepeatRule.getWeekInMonthNumbers(), MyRepeatRule.getWeekInMonthNames());
//        weekdaysInMonthField = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNames(), RepeatRuleParseObject.getWeekInMonthNumbers(), myRepeatRule.getWeekdaysInMonth(), true);
        weekdaysInMonth_1st2nLast_Field = new MyToggleButton(RepeatRuleParseObject.getWeekInMonthNamesShort(), RepeatRuleParseObject.getWeekInMonthNumbersShort(), myRepeatRuleEdited.getWeekdaysInMonthAsVector(), true); //**
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
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox = new MyToggleButton(new String[]{"day", "weekdays", "week nb"}, new int[]{MONTHLY_OPTION_DAY, MONTHLY_OPTION_WEEKDAYS, MONTHLY_OPTION_WEEK_NB});
        //show dayInMonth if either field is defined, or if weekInMonth is not defined (covers both initial state and once defined)
//        monthlyRepeatTypeSelectionBox.setSelectedIndex(((myRepeatRule.getDayInMonth() > 0 || (myRepeatRule.getWeekInMonth() == 0 && myRepeatRule.getWeekdaysInMonth() == 0)) ? 0 : ((myRepeatRule.getWeekInMonth() != 0) ? 1 : 2)));
//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setSelectedIndex(((myRepeatRule.getDayInMonth() > 0 || (myRepeatRule.getWeeksInMonth() == 0 && myRepeatRule.getWeekdaysInMonth() == 0))
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setSelectedValue(((myRepeatRuleEdited.getDayInMonth() > 0 || (myRepeatRuleEdited.getWeeksInMonth() == 0 && myRepeatRuleEdited.getWeekdaysInMonth() == 0))
                ? MONTHLY_OPTION_DAY : ((myRepeatRuleEdited.getWeeksInMonth() != 0) ? MONTHLY_OPTION_WEEK_NB : MONTHLY_OPTION_WEEKDAYS)));
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.addSelectionListener(refreshSelectionListener);
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
        yearlyChoice_DayMonths_Combo = new MyToggleButton(new String[]{"day", "month(s)"}, new int[]{YEAR_OPTION_DAY_OF_YEAR, YEAR_OPTION_MONTHS});
//        yearlyChoice_DayMonths_Combo.setSelectedValue(((myRepeatRule.getDayInYear() > 0 || myRepeatRule.getMonthsInYear() == 0) ? YEAR_OPTION_DAY_OF_YEAR : YEAR_OPTION_MONTHS)); //show dayInYear if either field is defined, or if monthInYear is not defined (covers both initial state and once defined)
        yearlyChoice_DayMonths_Combo.setSelectedValue(((myRepeatRuleEdited.getDayInYear() > 0) ? YEAR_OPTION_DAY_OF_YEAR : YEAR_OPTION_MONTHS)); //show dayInYear if either field is defined, or if monthInYear is not defined (covers both initial state and once defined)
        yearlyChoice_DayMonths_Combo.addSelectionListener(refreshSelectionListener);

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
        repeatHowLong_ForeverUntilNumber_ChoiceCombo = new MyToggleButton(new String[]{"forever", "until", "number"}, new int[]{REPEAT_HOW_LONG_OPTION_FOREVER, REPEAT_HOW_LONG_OPTION_UNTIL, REPEAT_HOW_LONG_OPTION_NUMBER});
//        repeatHowLongChoiceCombo.setSelectedValue(myRepeatRule.useCount() ? 2 : (myRepeatRule.getEndDate() != Long.MAX_VALUE ? 1 : 0));
        repeatHowLong_ForeverUntilNumber_ChoiceCombo.setSelectedValue(myRepeatRuleEdited.useCount() ? REPEAT_HOW_LONG_OPTION_NUMBER
                : (myRepeatRuleEdited.getEndDate() != MyDate.MAX_DATE ? REPEAT_HOW_LONG_OPTION_UNTIL : REPEAT_HOW_LONG_OPTION_FOREVER));
        repeatHowLong_ForeverUntilNumber_ChoiceCombo.addSelectionListener(refreshSelectionListener);

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
        endDate = new Date(myRepeatRuleEdited.getEndDate() != MyDate.MAX_DATE
                ? myRepeatRuleEdited.getEndDate()
                //                : System.currentTimeMillis() + MyDate.DAY_IN_MILLISECONDS * Settings.getInstance().getDefaultRepeatFromSpecifiedDateDaysAhead());
                : startDate.getTime());
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
        repeatEndDateEditButton = new MyDatePicker(endDate, "<last date>", true);
//        }); //OK

//        repeatHowManyTimesField = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxRepeatCount(), "", "counts**"/*times"*/, 1, false, false));
//        repeatHowManyTimesField = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxRepeatCount(), "", ""/*times"*/, 1, false, false));
//        repeatHowManyTimesField.setSelectedValue(myRepeatRule.useCount() ? myRepeatRule.getNumberOfRepeats()myRepeatRule : 1);
//        repeatHowManyTimesField = new MyIntPicker(myRepeatRule.useCount() ? myRepeatRule.getNumberOfRepeats() : 1, 1, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead.getInt());
        repeatHowManyTimesField = new MyIntTextField(myRepeatRuleEdited.useCount() ? myRepeatRuleEdited.getNumberOfRepeats() : 1, "", 1, 999999, 1);

        //SHOW HOW MANY FUTURE REPEATS
//        showHowManyCombo = new ComboBoxLogicalNames(new int[]{0, 1}, new String[]{"repeat instances", "days ahead"});
        showHowMany_InstancesDaysAhead_Combo = new MyToggleButton(new String[]{"instances", "days ahead"}, new int[]{SHOW_HOW_MANY_AHEAD_INSTANCES, SHOW_HOW_MANY_AHEAD_DAYS_AHEAD});
//        showHowMany_InstancesDaysAhead_Combo.setSelectedValue(myRepeatRule.getNumberFutureRepeatsToGenerateAhead() != 0 ? SHOW_HOW_MANY_AHEAD_INSTANCES : SHOW_HOW_MANY_AHEAD_DAYS_AHEAD);
        showHowMany_InstancesDaysAhead_Combo.setSelectedValue(myRepeatRuleEdited.useNumberFutureRepeatsToGenerateAhead() ? SHOW_HOW_MANY_AHEAD_INSTANCES : SHOW_HOW_MANY_AHEAD_DAYS_AHEAD);
        showHowMany_InstancesDaysAhead_Combo.addSelectionListener(refreshSelectionListener);
//            showHowManyContainer = createLabelAndFieldContainer("Show how many", showHowManyCombo);
//        showHowManyContainer = createLabelAndFieldContainer("Create how many future repeats", showHowManyCombo);
        showHowManyContainer = BoxLayout.encloseY(new Label("Create how many simultaneous repeats"), showHowMany_InstancesDaysAhead_Combo);

//        showNumberFutureRepeats = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxFutureRepeatInstances(), "", "", 1, false, false));
//        showNumberFutureRepeats.setSelectedValue(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1); //use 1 as defaiæt vaæie (if NumberFutureRepeatsGeneratedAhead is not defined)
        showNumberFutureRepeats = new MyIntPicker(myRepeatRuleEdited.useNumberFutureRepeatsToGenerateAhead()
                ? myRepeatRuleEdited.getNumberFutureRepeatsToGenerateAhead() + 1 : 1, 1, MyPrefs.repeatMaxNumberFutureInstancesToGenerateAhead.getInt()); //+1: adjust rel. to UI which now says "simultaneous instances"

//        showNumberDaysAhead = new ComboBoxOffset(new ListModelInfinite(1, Settings.getInstance().getMaxFutureRepeatInstances(), "", "", 1, false, false));
//        showNumberDaysAhead.setSelectedValue(myRepeatRule.useNumberFutureRepeatsToGenerateAhead() ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1); //use 1 as defaiæt vaæie (if NumberFutureRepeatsGeneratedAhead is not defined)
//        showNumberDaysAhead = new MyIntPicker(myRepeatRule.useNumberFutureRepeatsToGenerateAhead()
//                ? myRepeatRule.getNumberFutureRepeatsToGenerateAhead() : 1, 1, MyPrefs.repeatMaxNumberFutureDaysToGenerateAhead.getInt());
        showNumberDaysAhead = new MyIntPicker(!myRepeatRuleEdited.useNumberFutureRepeatsToGenerateAhead()
                ? myRepeatRuleEdited.getNumberOfDaysRepeatsAreGeneratedAhead() : 1, 1, MyPrefs.repeatMaxNumberFutureDaysToGenerateAhead.getInt());
//<editor-fold defaultstate="collapsed" desc="comment">
//            initialized = true;
//        } else { //if not initialized
//            motherContainer.removeAll(); //clean so it's empty to repopulate below
//        }
//</editor-fold>
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

//        updateFields();
        fieldsAdd();
        fieldsShow();
    }

    private void addMonthlyFieldsToContainer(Container addToContainer) {//, RepeatRuleParseObject myRepeatRule, boolean forYear) {

        addToContainer.addComponent(monthlyRepeatType_DayWeekdaysWeeks_SelectionBox);

        //SELECT DAY NUMBER IN MONTH, e.g. 3rd day - "Select day of month(s):"
        addToContainer.addComponent(dayInMonth_1_31_Last_FieldFM);
        //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
        addToContainer.addComponent(weeksInMonth_1st2ndLast_Field);
        addToContainer.addComponent(daysInWeek_MonSun_Field);
        //SELECT WEEKDAYS IN MONTH, e.g. 1st FRIDAY
        addToContainer.addComponent(weekdaysInMonth_1st2nLast_Field);
        addToContainer.addComponent(daysInMonth_MonSunWeekdays_Field);
    }

    private void fieldsAdd() {
        motherContainer.removeAll();

        if (repeatRuleDetailsContainer != null) {
            motherContainer.addComponent(repeatRuleDetailsContainer); //may be null if new rule
        }
        motherContainer.addComponent(repeatFromDueOrCompletedFieldContainer);
//        if (repeatFromDueOrCompletedField.getSelectedValue() == 1) //"due date
//        {
        if (repeatStartDatePicker != null) {
            motherContainer.addComponent(repeatStartDatePicker);
        }
        if (repeatStartDateLabel != null) {
            motherContainer.addComponent(repeatStartDateLabel);
        }
//        }

        //FREQUENCY: DAILY/MONTHLY BASIS
        motherContainer.addComponent(frequency_DailyWeekMonthYearly_Field);

        //EVERY 1,2,3,.. DAYS/WEEKS/MONTHS/YEARS
//        motherContainer.addComponent(intervalField); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...
        motherContainer.addComponent(intervalField_1_NN_TextField); //TODO make interval show a list, e.g. every 2 weeks, every 3 weeks, or every month, every 2 months, ...

        //ADAPT rest according to type
//        switch (myRepeatRule.getFrequency()) {
//        int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//        intervalFieldInfiniteList.setListModelValues(1, getFreqNumberInstances(frequency), "every",
//                RepeatRuleParseObject.getFreqText(frequency, false, false, true), 1, false, true);
//         ** 
//        motherContainer.addComponent(daysInWeekField);
        motherContainer.addComponent(yearlyChoice_DayMonths_Combo);

        //Select day of year
        motherContainer.addComponent(dayInYear_1_365_Field);

        motherContainer.addComponent(monthsInYear_JanDec_Field);

        //Select yearly repeat on a monthly basis
//        addMonthlyFieldsToContainer(motherContainer, myRepeatRule, true);
        addMonthlyFieldsToContainer(motherContainer);//, myRepeatRule, false);

        motherContainer.addComponent(repeatHowLong_ForeverUntilNumber_ChoiceCombo);
        //{"Repeat forever", "Repeat until", "Number of repeats"}
        motherContainer.addComponent(repeatEndDateEditButton);
        motherContainer.addComponent(repeatHowManyTimesField);

        //if Repeat from DUE DATE then add option to choose how many future instances to create (if repeat from Completed date, not ossible to create future instances with right date)
        motherContainer.addComponent(showHowManyContainer);
//Show how many REPEAT INSTANCED
        motherContainer.addComponent(showNumberFutureRepeats);
//Show how many DAYS AHEAD
        motherContainer.addComponent(showNumberDaysAhead);

        motherContainer.addComponent(showDatesButton);

        if (false) {
            this.animateLayout(400);
        }
    }

//    void showMonthlyFields(Container addToContainer, RepeatRuleParseObject myRepeatRule, boolean forYear) {
//    private void showMonthlyFields() {
//        showMonthlyFields(true);
//    }
    private void showMonthlyFields(boolean show) {

//        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setHidden(false);
        monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.setHidden(!show);

        if (!show) {
            dayInMonth_1_31_Last_FieldFM.setHidden(true);
            weeksInMonth_1st2ndLast_Field.setHidden(true);
//            daysInWeek_MonSun_Field.setHidden(true);
            daysInWeek_MonSun_Field.setHidden(true);
            weekdaysInMonth_1st2nLast_Field.setHidden(true);
            daysInMonth_MonSunWeekdays_Field.setHidden(true);
        } else {
            switch (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue()) {
                case MONTHLY_OPTION_DAY:
                    //SELECT DAY NUMBER IN MONTH, e.g. 3rd day - "Select day of month(s):"
                    if (dayInMonth_1_31_Last_FieldFM.isHidden()) { //avoid unnecessary animation
                        dayInMonth_1_31_Last_FieldFM.setHidden(false);
                    }

                    weeksInMonth_1st2ndLast_Field.setHidden(true);
                    daysInWeek_MonSun_Field.setHidden(true);
                    weekdaysInMonth_1st2nLast_Field.setHidden(true);
                    daysInMonth_MonSunWeekdays_Field.setHidden(true);
                    break;
                case MONTHLY_OPTION_WEEK_NB:
                    //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
                    weeksInMonth_1st2ndLast_Field.setHidden(false);
                    daysInWeek_MonSun_Field.setHidden(false);

                    dayInMonth_1_31_Last_FieldFM.setHidden(true);
                    weekdaysInMonth_1st2nLast_Field.setHidden(true);
                    daysInMonth_MonSunWeekdays_Field.setHidden(true);
                    break;
                case MONTHLY_OPTION_WEEKDAYS:
                    //SELECT WEEKDAYS IN MONTH, e.g. 1st FRIDAY
                    weekdaysInMonth_1st2nLast_Field.setHidden(false);
                    daysInMonth_MonSunWeekdays_Field.setHidden(false);

                    dayInMonth_1_31_Last_FieldFM.setHidden(true);
                    //SELECT WEEKS IN MONTH, e.g. 2nd week, and WWEK DAYS in those weeks, e.g. MONDAY and THURSDAY
                    weeksInMonth_1st2ndLast_Field.setHidden(true);
                    daysInWeek_MonSun_Field.setHidden(true);
                    break;
            }
        }
    }

    private void fieldsShow() {
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
        if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
            showMonthlyFields(false);
            frequency_DailyWeekMonthYearly_Field.setHidden(true);
            intervalField_1_NN_TextField.setHidden(true);
            daysInWeek_MonSun_Field.setHidden(true);
            yearlyChoice_DayMonths_Combo.setHidden(true);
            dayInYear_1_365_Field.setHidden(true);
            monthsInYear_JanDec_Field.setHidden(true);
            repeatEndDateEditButton.setHidden(true);
            repeatHowManyTimesField.setHidden(true);
            repeatStartDatePicker.setHidden(true);
            showHowManyContainer.setHidden(true);
            repeatHowLong_ForeverUntilNumber_ChoiceCombo.setHidden(true);
            showNumberFutureRepeats.setHidden(true);
            showNumberDaysAhead.setHidden(true);
            showDatesButton.setHidden(true);
        } else {
            frequency_DailyWeekMonthYearly_Field.setHidden(false);
            intervalField_1_NN_TextField.setHidden(false);
            repeatStartDatePicker.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedValue() != RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE || isForWorkSlot); //"due date
            showDatesButton.setHidden(repeatFrom_NoneCompletedDue_Field.getSelectedValue() != RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE); //only show for repeat from Due
//            repeatStartDatePicker.setHidden(isForWorkSlot);

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
            switch (frequency_DailyWeekMonthYearly_Field.getSelectedValue()) {
                case RepeatRule.DAILY:
                    //Nothing for daily except interval
//<editor-fold defaultstate="collapsed" desc="comment">
//                dayInYear_1_365_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() == 0);
//                monthsInYear_JanDec_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != 0);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() == 0) {
//                    showMonthlyFields(true);
//                }
//</editor-fold>
                    showMonthlyFields(false);
                    daysInWeek_MonSun_Field.setHidden(true);
                    yearlyChoice_DayMonths_Combo.setHidden(true);
                    dayInYear_1_365_Field.setHidden(true);
                    monthsInYear_JanDec_Field.setHidden(true);
                    break;
                case RepeatRule.WEEKLY:
                    showMonthlyFields(false);
                    daysInWeek_MonSun_Field.setHidden(false);

                    yearlyChoice_DayMonths_Combo.setHidden(true);
                    dayInYear_1_365_Field.setHidden(true);
                    monthsInYear_JanDec_Field.setHidden(true);
                    break;
                case RepeatRule.MONTHLY:
                    showMonthlyFields(true);

//                daysInWeek_MonSun_Field.setHidden(true);
                    yearlyChoice_DayMonths_Combo.setHidden(true);
                    dayInYear_1_365_Field.setHidden(true);
                    monthsInYear_JanDec_Field.setHidden(true);
                    break;
                case RepeatRule.YEARLY:
                    showMonthlyFields(yearlyChoice_DayMonths_Combo.getSelectedValue() == YEAR_OPTION_MONTHS);
                    yearlyChoice_DayMonths_Combo.setHidden(false);
                    dayInYear_1_365_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != YEAR_OPTION_DAY_OF_YEAR);
                    monthsInYear_JanDec_Field.setHidden(yearlyChoice_DayMonths_Combo.getSelectedValue() != YEAR_OPTION_MONTHS);
//                if (yearlyChoice_DayMonths_Combo.getSelectedValue() != 0) {
//                    showMonthlyFields();
//                }

//                daysInWeek_MonSun_Field.setHidden(true);
//                showMonthlyFields(false);
                    break;
            }

            if (true) {
                repeatHowLong_ForeverUntilNumber_ChoiceCombo.setHidden(false);
            }

            repeatEndDateEditButton.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() != REPEAT_HOW_LONG_OPTION_UNTIL);
            repeatHowManyTimesField.setHidden(repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() != REPEAT_HOW_LONG_OPTION_NUMBER);

//        if (isForWorkSlot) {
// showHowManyContainer.setHidden(true);
//                showNumberFutureRepeats.setHidden(true);
//                showNumberDaysAhead.setHidden(true);
//        } else {
            if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE || isForWorkSlot) {
                showHowManyContainer.setHidden(false);
                if (showHowMany_InstancesDaysAhead_Combo.getSelectedValue() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //Show how many REPEAT INSTANCED
                    showNumberFutureRepeats.setHidden(false);
                    showNumberDaysAhead.setHidden(true);
                } else {//Show how many DAYS AHEAD
                    showNumberFutureRepeats.setHidden(true);
                    showNumberDaysAhead.setHidden(false);
                }
            } else if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
                showHowManyContainer.setHidden(true);
                showNumberFutureRepeats.setHidden(true);
                showNumberDaysAhead.setHidden(true);
            }
//        }
//            if (true) {
//                showDatesButton.setHidden(false);
//            }
        }
        getContentPane().animateLayout(400);
    }

    /**
     * returns true if all necessary values are set, and false if not
     *
     * @param myRepeatRule
     * @return
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

        myRepeatRule.setRepeatType(repeatFrom_NoneCompletedDue_Field.getSelectedValue());
        if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
            return !missingData;
        }

        if (true || !isForWorkSlot) {
//            if (repeatFrom_CompletedDue_Field != null) {
            if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//                    Log.l("setReferenceItemForDueDateAndFutureCopies(" + repeatRuleOwner + ")");
//                myRepeatRule.setReferenceItemForDueDateAndFutureCopies(repeatRuleOwner);
//                        myRepeatRule.setReferenceObjectForRepeatTime(repeatRuleOwner);
//                    myRepeatRule.setSpecifiedStartDate(repeatRuleOwner.getRepeatStartTime(false));
//                    myRepeatRule.setSpecifiedStartDate(repeatStartDate.getTime());
//                    if (repeatStartDatePicker != null) {
                Date editedStartDate = repeatStartDatePicker.getDate();
                if (editedStartDate.getTime() != 0) {
                    myRepeatRule.setSpecifiedStartDate(editedStartDate.getTime());
                } else {
                    missingData = true;
                    missingDataStr += Item.DUE_DATE;
                }
//                    } else {
//                        myRepeatRule.setSpecifiedStartDate(startDate);
//                    }
            } else if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_COMPLETED_DATE) {
                Log.p("setReferenceItemForDueDateAndFutureCopies(null)");
//                myRepeatRule.setReferenceItemForDueDateAndFutureCopies(null);
//                        myRepeatRule.setReferenceObjectForRepeatTime(null);
                myRepeatRule.setSpecifiedStartDate(0); //reset startDate
            } else { //MyRepeatRule.REPEAT_TYPE_FROM_SPECIFIED_DATE
//                    Log.p("setStartDate(" + MyDate.getNow() + ")");
                Log.p("setStartDate(" + System.currentTimeMillis() + ")");
//                        myRepeatRule.setStartDate(repeatFromField.getTime());
//                    myRepeatRule.setSpecifiedStartDate(repeatFromField.getTime());
            }
//            } else {
//            }

//            if (repeatFrom_NoneCompletedDue_Field != null) {
            if (repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_DUE_DATE) {
//                        || repeatFrom_NoneCompletedDue_Field.getSelectedValue() == RepeatRuleParseObject.REPEAT_TYPE_FROM_SPECIFIED_DATE) {
//                    if (showHowManyChoice.getSelectedIndex() == 0) //{"coming tasks", "days ahead"}
//                    if (showHowMany_InstancesDaysAhead_Combo.getSelectedIndex() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //{"coming tasks", "days ahead"}
                if (showHowMany_InstancesDaysAhead_Combo.getSelectedValue() == SHOW_HOW_MANY_AHEAD_INSTANCES) { //{"coming tasks", "days ahead"}
//                        Log.p("setNumberFutureRepeatsGeneratedAhead(" + showNumberFutureRepeats.getSelectedValue() + ")");
//                        myRepeatRule.setNumberFutureRepeatsToGenerateAhead(showNumberFutureRepeats.getSelectedValue());
                    Log.p("setNumberFutureRepeatsGeneratedAhead(" + showNumberFutureRepeats.getValueInt() + ")");
                    myRepeatRule.setNumberFutureRepeatsToGenerateAhead(showNumberFutureRepeats.getValueInt() - 1);
                    myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(0);
                } else // SHOW_HOW_MANY_AHEAD_DAYS_AHEAD
                if (showHowMany_InstancesDaysAhead_Combo.getSelectedValue() == SHOW_HOW_MANY_AHEAD_DAYS_AHEAD) { //{"coming tasks", "days ahead"}
//                        Log.p("setNumberOfDaysRepeatsAreGeneratedAhead(" + daysAheadField.getSelectedValue() + ")");
//                        myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(daysAheadField.getSelectedValue());
//                            Log.p("setNumberOfDaysRepeatsAreGeneratedAhead(" + daysAheadField.getValue() + ")");
//                            myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(daysAheadField.getValue());
                    myRepeatRule.setNumberOfDaysRepeatsAreGeneratedAhead(showNumberDaysAhead.getValueInt());
                    myRepeatRule.setNumberFutureRepeatsToGenerateAhead(0);
                } else { // SHOW_HOW_MANY_AHEAD_DAYS_AHEAD
                    ASSERT.that("error");
                }
            }
//            }
        }

        int frequency = frequency_DailyWeekMonthYearly_Field.getSelectedValue();
//        int frequency = frequencyChoice.getSelectedValue();

        myRepeatRule.setFrequency(frequency);
        Log.p("setFrequency(" + frequency + ")");

        if (false) {
//                myRepeatRule.setInterval(intervalField.getSelectedValue());
//                Log.p("setInterval(" + intervalField.getSelectedValue() + ")");
        }
        if (false) {
            String intervalStr = intervalField_1_NN_TextField.getText();
            if (intervalStr == null || intervalStr.length() == 0) {
                missingData = true;
                missingDataStr += true;
            }
            int interval = Integer.parseInt(intervalField_1_NN_TextField.getText());
            if (interval < 1) {
                missingData = true;
            }
            myRepeatRule.setInterval(interval);
        }
        myRepeatRule.setInterval(intervalField_1_NN_TextField.getValue());

        switch (frequency) {
            case RepeatRule.DAILY:
                break;
            case RepeatRule.YEARLY:
//                if (yearlyChoice_DayMonths_Combo.getSelectedIndex() == YEAR_OPTION_DAY_OF_YEAR) { //"Select day of year", "Select month(s) of year"
                if (yearlyChoice_DayMonths_Combo.getSelectedValue() == YEAR_OPTION_DAY_OF_YEAR) { //"Select day of year", "Select month(s) of year"
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
                    assert yearlyChoice_DayMonths_Combo.getSelectedValue() == YEAR_OPTION_MONTHS;
//                    myRepeatRule.setMonthsInYear(monthsInYearField.getSelectedOredTogether());
                    myRepeatRule.setMonthsInYear(testVal = RepeatRuleParseObject.bitOrIntegerVectorToInt(monthsInYear_JanDec_Field.getSelectedValues()));
                    missingData = (testVal == 0) || missingData;
//                    if (monthlyChoiceContainer.getSelectedIndex() == 0) { //"Select day of month(s):" : "Select day of month:"
//                    if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
                    if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
//                        myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
                        myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
//                    } else if (monthlyChoiceContainer.getSelectedIndex() == 1) {
//                    } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEK_NB) {

                        myRepeatRule.setWeeksInMonth(0);
                        myRepeatRule.setWeekdaysInMonth(0);
                        myRepeatRule.setDaysInWeek(0);
                        myRepeatRule.setDayInYear(0);
                    } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_WEEK_NB) {
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
                    } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_WEEKDAYS) {
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
                if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_DAY) { //"Select day of month(s):" : "Select day of month:"
//                    myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());
                    myRepeatRule.setDayInMonth(dayInMonth_1_31_Last_FieldFM.getSelectedValue());

                    myRepeatRule.setWeeksInMonth(0);
                    myRepeatRule.setWeekdaysInMonth(0);
                    myRepeatRule.setDaysInWeek(0);
                    myRepeatRule.setDayInYear(0);
                    myRepeatRule.setMonthsInYear(0);

//                } else if (monthlyChoiceContainer.getSelectedIndex() == 1) {
//                } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedIndex() == MONTHLY_OPTION_WEEK_NB) { //"Select week of month(s):" 
                } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_WEEK_NB) { //"Select week of month(s):" 
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
                } else if (monthlyRepeatType_DayWeekdaysWeeks_SelectionBox.getSelectedValue() == MONTHLY_OPTION_WEEKDAYS) {
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
        if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_FOREVER) { //{"Repeat forever", "Repeat until", "Repeat"}
//                myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE);
//                myRepeatRule.setNumberOfRepeats(0); //also sets endDate to 0
            myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE); //also sets endDate 
            myRepeatRule.setEndDate(new Date(MyDate.MAX_DATE));
//            myRepeatRule.setEndDate(0);
//        } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedIndex() == REPEAT_HOW_LONG_OPTION_UNTIL) { //Repeat until
        } else if (repeatHowLong_ForeverUntilNumber_ChoiceCombo.getSelectedValue() == REPEAT_HOW_LONG_OPTION_UNTIL) { //Repeat until
//            myRepeatRule.setEndDate(endDate.getTime() == 0 ? Long.MAX_VALUE : endDate.getTime());
//            myRepeatRule.setEndDate(endDate.getTime() == 0 ? RepeatRuleParseObject.MAX_DATE : endDate.getTime());
//                missingData = (repeatEndDateEditButton.getDate().getTime() == 0) || missingData;
            missingData = (repeatEndDateEditButton.getDate().getTime() == 0) || missingData;
            myRepeatRule.setEndDate(repeatEndDateEditButton.getDate().getTime() == 0 ? new Date(MyDate.MAX_DATE) : repeatEndDateEditButton.getDate());
            myRepeatRule.setNumberOfRepeats(Integer.MAX_VALUE);
        } else { // REPEAT_HOW_LONG_OPTION_NUMBER
//            myRepeatRule.setNumberOfRepeats(repeatHowManyTimesField.getSelectedValue() == 0 ? Integer.MAX_VALUE : repeatHowManyTimesField.getSelectedValue());
            myRepeatRule.setNumberOfRepeats(repeatHowManyTimesField.getValue() == 0 ? Integer.MAX_VALUE : repeatHowManyTimesField.getValue());
            myRepeatRule.setEndDate(new Date(MyDate.MAX_DATE));
        }
//        }
        return !missingData;
    }

    void restoreEditedFieldsOnDone() {
//        repeatRuleOwner.setRepeatStartTime(repeatStartDatePicker.getDate()); //update due date in case it was changed while editing the repeat rule
        if (!restoreEditedFieldsToRepeatRule(myRepeatRuleEdited)) {
            Dialog.show("Error", "Missing selection in one or more choices", "OK", null);
        }
    }

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

