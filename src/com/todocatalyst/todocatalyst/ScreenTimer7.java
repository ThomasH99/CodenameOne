package com.todocatalyst.todocatalyst;

import com.codename1.compat.java.util.Objects;
import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.Switch;
import com.codename1.io.Log;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Shows a timer. UI:
 *
 * @author Thomas
 */
public class ScreenTimer7 extends MyForm {//implements ActionListener {

    //TODO!!! get owner project / list from owner field of timed task (to ensure that even if started from e.g. Overdue, it will show!
    //TODO Imp: rearrange Timer to show most useful/needed first: show task and timer first, then Notes, then estimates.
    //TODO Imp: when storing time, round of to seconds to avoid that a few miliseconds suddenly makes a difference between actual and total actual
    //TODO implement pop up to validate Remaining time on Done/Next/Waiting + setting to activate/deactivate (+option to deactivate in pop-up)
    //TODO Imp: add option to always continue Timer with previous ActualEffort value (instead of showing previous Actual separately and use Timer for the ongoing work now). PRO option
    //TODO Feature: use app.start/stop() to save timer state (instead of doing it regularly when updating timer screen), http://stackoverflow.com/questions/28225954/how-can-i-load-a-specific-codenameone-form-every-time-the-app-is-open?rq=1
    //TODO Imp: change the timer symbol shown everywhere when timer is running (e.g. to inverse/black frame)
    //TODO Imp: move buttons from Timer titlebar to bottom toolbar to get a cleaner look
    //TODO Imp: add visual cues to indicate when totalActual is above EffortEstimate, or when timer elapsed time is above Remaining
    //DONE Imp: add setting button (or in ScreenTimerSettings) to show seconds in timer (or just keep as default?!)
    //TODO Imp: make button COmpleted green and two rows high
    //TODO Imp: make buttons Exit/Wait look like buttons on iPhone, give them a color
    //TODO Imp: pop-up Toastbar every x minutes when Timer is running + setting "Reminder message when timer is running" - as an alternative to buzzer
    //TODO Feature: log start/stop of timer to capture exactly when work was done on it, amount of interrupts etc
    //TODO Imp: activate timer in list screens (works, remain to do in all appropriate screens)
    //TODO Imp: add small Timer container to other screens (and/or change Timer symbol to show it is running - no, best to be cclearly reminded about what task is running to not loose focus)
    //TODO Imp: timer lable is not updated while overflow menu is shown
    //DONE Imp: create and implement Setting to adjust buzzer interval
    //TODO!!! Imp: buzz 'warning' pattern or sound when Remaining (Estimate) is passed (or make this an adjustable interval, e.g. warn 5min/0min/10% before reaching estimate). 
    //TODO Feature(?): add followup task when setting a task Waiting (instead of setting a wait until date and/or alarm)
    //TODO Feature: Pomodoro timer (to take breaks regularly): set interval, turn on/off, break interval, text/buzz pattern on start/stop. 
    //TODO Feature: Integrate Timer as a 'music' player on phone: Music title=Task, Album=List/Project, Play/Pause=Start/Pause timer, Played time=Actual+timer, Remaining time=Remaining-actual-timer, Next >>=Done (or Next?? - make this an option). Volume==50%=>buzzer/>80%=>buzzer+sound.
    //TODO Feature: Discrete mode = 2 modes: incognito mode w. black screen unless pushes button/touches screen, discrete mode: small font task title + time on black screen. Full mode (working alone): all text, big timer, lots of info, e.g. going over time, etc etc. Also see Word doc
    //TODO should it be possible to start the timer on a Done task (yes, if you realize there was a bit more work to do, so also update the CompletedBy date when updating Actual - make it an option of setActual(boolean updateCompletedDateWhenChangingActualForADoneItem) so you can also update the Actual while editing the Item if it was wrong)
    //TODO Imp: find a way to avoid that StartedOn date is set if timer starts automatically when entering the Timer but you didn't intend to work on it (but: why would you enter the timer if you don't intend to work on the task??)
    //DONE set task checkbox symbol to Ongoing as soon as the timer starts!!
    //DONE!!!! error: when rentering Timer for running task after an Interrupt and the timer is not starting automatically, the elapsed time is not updated (until you start the timer) AND the Pause icon is shown instead of the Play icon
    //DONE when setting Actual manually, it is not saved till next time
    //DONE handle situation when user starts Timer on the same item that is already being timed (already working OK?) -> nothing is done if Timer is already running on (any) item
    //DONE handle situation when user exits Timer with Back and reenters with timer symbol (especially when it is the same task in which case it shouldn't do anything)
    //DONE error: when doing Back from Timer to previous screen and then reenter Timer+Continue, the Timer doesn't continue but exits to previous screen
    //DONE when end of itemList is reached and pressing, the same popup as when starting Timer on an empty list should be shown (to ensure simple and consistent behavior)
    //DONE error: when reaching the end of a list, the "Next: yyy" shows the already timed task yyy. Should show "No more tasks"
    //DONE menu when Timer on list is not same as menu on single item
    //DONE error: continues with same subtask in a project
    //DONE error: when interrupting a task, actual is updated&saved, but timer continuous from where it was before, meaning time before interrupt is counted DOUBLE!
    //DONE popup dialog to ask for waiting date when setting a task Waiting
    //DONE error: when marking an interrupt task Done, remaining is not set to 0 (really??)
    //DONE ensure that edited fields are updated on interrupt - before showing the popup dialog showing the previously running task's name (only a problem when popup dialog asking if Keep/Save etc)
    //DONE update pop-up when starting an interrupt task from within the Timer (you pushed the interrupt button so you know a task is already running)
    //DONE ensure consistency btw the Picker for elapsed time and the display (e.g. show seconds separate? or change Picker to support seconds?)
    //DONE show project context (mothertasks) when starting Timer on a subtask within a project
    //DONE on app.stop() (or app.destroy()-same thing), save item (to reduce the need for 
    //DONE test that Timer starts up correctly on previous task after shutdown
    //DONE add dialog to ask if restart timer after app shutdown (or simply start up in the timer view with Timer paused to let the user decide what to do?) -> simply continues with Timer
    //DONE Done (or set status) = long press on checkbox
    //DONE the Total should show the sum of the previous Actual and the Timer (and be updated as the timer is)
    //
//    final static String TIMER_REPLAY = "StartTimer-";
//    private TimerInstance timerInstance;//= new TimerStack();
    Command backCommand = null; //not private since accessed from TimerStack to exit Full screen timer
    private final static String MSG_NO_SOURCE_SET = "<No list selected>"; //"<No source>" "<Select list to time>"
    private final static String MSG_NEW_TASK_DEFAULT_TEXT = "New Task";
    private final static String MSG_QUIT_TIMER = "Quit timer"; //"Quit Timer"; "Quit"-> clearer if adding 'Timer'
    private final static String MSG_QUIT_TIMER_SMALL_TIMER = "Quit";
    private final static String MSG_STOP_TIMER = "Stop"; //"Quit Timer"; "Quit"-> clearer if adding 'Timer'
    private final static String MSG_SET_WAITING = "Wait"; //"Set Waiting"; -> too long w 3 buttons (Quit+Wait+Next)
    private final static String MSG_NEXT_TASK_LABEL = "Next: ";
    private final static String MSG_SOURCE_LABEL = "Source: ";
    private final static String MSG_NEXT_TASK = "Next";
    private final static String MSG_NEXT_TASK_SMALL_TIMER = "Next";
    private final static String MSG_SELECT_SOURCE_SCREEN_TITLE = "Select for Timer";
    private final static String MSG_NEW_TASK_CMD = "New task";
    private final static String MSG_NEW_SUBTASK_CMD = "New subtask";
    private final static String MSG_AUTO_START_BUTTON = "Auto-start";
    private final static String MSG_AUTO_START_BUTTON_HELP = "Auto-start";
    private final static String MSG_AUTO_NEXT_BUTTON = "Auto-next";
    private final static String MSG_AUTO_NEXT_BUTTON_HELP = "Auto-next";
//    protected static String FORM_UNIQUE_ID = "ScreenTimer"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//    private Container timerContentainer = new Container(BoxLayout.y());
//    private TimerStackEntry entry;

//    private CheckBox startTimerAutomaticallyForNextTask; //should be unique for the Timer
//    private Command continueCommand; //make global to be able to test return value; 
//    ScreenTimer7(MyForm previousScreen, TimerInstance2 timerInstance) {
//        this(previousScreen, timerInstance, null);
//    }
    ScreenTimer7(MyForm previousScreen) {
//        this(previousScreen, TimerStack2.getInstance().getCurrentTimerInstanceN(), null);
//    }
//
//    ScreenTimer7(MyForm previousScreen, TimerInstance2 timerInstance, SaveEditedValuesLocally previousValues) {//,  Map<Object, UpdateField> parseIdMap2) {
//        super(MyForm.SCREEN_TIMER_TITLE + TimerStack2.getTimerStackSizeAsStr(), previousScreen, null, MyForm.SCREEN_TIMER_HELP);
        super(MyForm.SCREEN_TIMER_TITLE, previousScreen, null, MyForm.SCREEN_TIMER_HELP);
        setUniqueFormId("ScreenTimer");
        setUIID("BigTimerForm");
//        if (false && timerInstance != null) {
//            timerInstance.setFullScreen(true); //save full screen state
////            timerInstance.saveMe();
//            DAO.getInstance().saveToParseNow(timerInstance);
//        }

        //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setScrollableY(true); //since the size of the timer may overflow
        setAlwaysTensile(false); //only make scrollable when bigger than screen //TODO!!! not working!

        addCommandsToToolbar(getToolbar()); //no commands depend on the task or itemList
//        if(Config.TEST) ASSERT.that(getContentPane().getLayout() instanceof BorderLayout);
//        setLayout(BoxLayout.y());
        if (Config.TEST) {
//            ASSERT.that(getContentPane().getLayout() instanceof BoxLayout);
            ASSERT.that(getContentPane().getLayout() instanceof BorderLayout);
        }
//        Item timedItem = TimerStack2.getTimedItemN();
//        if (timedItem != null) {
//            timedItem.addActionListener(this); //add when timedItem is fetched from Parse/DAO
//        }
//        buildContentPane(getContentPane(), this, true);
//        TimerStack2.getInstance().addActionListener(this); //listen to updates to Timers
        refreshAfterEdit();
    }

    //****************** UI *********************
    //
    public void addCommandsToToolbar(Toolbar toolbar) {

        super.addCommandsToToolbar(toolbar);
        backCommand = addStandardBackCommand();
        addDoneUpdateAction(() -> {
//            TimerStack2.getInstance().removeActionListener(this);
//            if (false) { //shouldn't be necessary since when adding the smallTimer, this (big) timer should automatically be removed
//                TimerStack2.getInstance().setActionListener(null); //remove any listeners when exiting big timer
//            }
            if (Display.getInstance().isScreenSaverDisableSupported()) {
                boolean screenAlwaysOnInAppButOffInTimer = MyPrefs.keepScreenAlwaysOnInApp.getBoolean() && !MyPrefs.timerKeepScreenAlwaysOnInTimer.getBoolean();
                Display.getInstance().setScreenSaverEnabled(!MyPrefs.keepScreenAlwaysOnInApp.getBoolean()); //turn screenSaver back on if it was only disable by the Timer
            }
        });

        toolbar.addCommandToOverflowMenu(makeInterruptCommand(true)); //left like all other screens

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("TimerSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsTimer(this, null).show();
        }
        ));
        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("TimerQuitAllTimers", MSG_QUIT_TIMER, Icons.iconTimerQuitTimer,
                (e) -> {
                    TimerStack2.getInstance().stopAllTimers();
                    TimerStack2.getInstance().fireChangedEvent();
                    DAO.getInstance().triggerParseUpdate();
                }
        )); //"Exit"/"Kill"/"Quit"/"Exit"/"Close Timer"/"Stop Timer"/"Quit Timer"/"Exit Timer"
    }

    @Override
    public void refreshAfterEdit() {
        buildContentPane(getContentPane(), this, true);
        super.refreshAfterEdit();
//        revalidateWithAnimationSafety();
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public void refreshAfterEditXXX() {
////        getContentPane().removeAll(); //clear existing contentPane
////        buildContentPane(getTimedItemN(), itemList, getContentPane()); //rebuild for new values of item etc
////        timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
////        Item timedItem = TimerStack.getInstance().getCurrentlyTimedItemN();
////        TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
//        TimerInstance2 timerInstance = TimerStack2.getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
////        Item timedItem = TimerStack.getInstance().getCurrentlyTimedItemN();
//            Item timedItem = timerInstance.getTimedItemN();
//            ReplayLog.getInstance().clearSetOfScreenCommandsNO_EFFECT(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
////        TimerStack.buildContentPane(timerInstance.getTimerContainer(), timerInstance, true, previousValues); //also removes previous content of contentPane
//            //clear previous edited values
//            if (previousValues != null) {
//                previousValues.deleteFile();
//            }
////        if (timerInstance != null) {
//            if (timedItem != null) {
//                timerInstance.setFullScreen(true); //set full screen state of this timer
//                timerInstance.saveMe();
////            previousValues = new SaveEditedValuesLocally("Timer-" + timerInstance.getTimedItemN().getObjectIdP());
////            previousValues = new SaveEditedValuesLocally("Timer-" + timerInstance.getTimedItemN().getObjectIdP());
////            previousValues = new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());
////            initLocalSaveOfEditedValues("Timer-" + timedItem.getObjectIdP());
//                ASSERT.that(timedItem.getObjectIdP() != null, "Timer should never be called on a not saved task");
//                this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + timedItem.getObjectIdP());
////        TimerStack.buildContentPaneFullScreen(ScreenTimer6.this, timerContentainer,  previousValues); //also removes previous content of contentPane
//                Container contentPane = getContentPane();
//                contentPane.removeAll();
//                contentPane.add(BorderLayout.CENTER, TimerStack.buildContentPaneFullScreen(ScreenTimer7.this, previousValues)); //also removes previous content of contentPane
//                if (false) {
//                    super.refreshAfterEdit(); //WILL cause infinite loop when updating ScreenTimer6 via refreshOrShowTimerUI
//                }
//                revalidateWithAnimationSafety();
////            revalidate();
////            revalidateWithAnimationSafety();
//            }
//        }
//    }
    /**
     * create the appropriate hierarchy container for a task. Specifically: if a
     * Category is being timed, use that as top-level followed by the subtask
     * owner hierarchy (exluding the top-level List). If not a category, then if
     * a list is being timed, use that as top-level, followed by the subtask
     * owner hierarchy. If an interrupt is being timed, just show
     * INTERRUPT/INSTANT. Otherwise, (if none of the above applies, e.g. Timer
     * was started directly on a single task somewhere), extract both subtask
     * owner
     *
     * @param itemList
     * @param timedItem
     * @return
     */
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private static Container makeTaskHierarchyContainer(ItemList itemList, Item timedItem) {
//        //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
//        Container contentPane = new Container(BoxLayout.y());
//        String listName = null; //"TEMP - Name of the list";
//
//        if (itemList != null) {
//            listName = itemList instanceof Category ? "Category: " : "List: " + itemList.getText(); //source is an ItemList
//        } else if (timedItem.isInteruptOrInstantTask()) {
//            listName = timedItem.getTaskInterrupted() == null ? "INSTANT TASK" : "INTERRUPT TASK";
//        } else {//else: listName remains null, e.g. if only a single task is timed
//            ItemList topLevelList = timedItem.getOwnerTopLevelList();
//            if (topLevelList != null) {
//                listName = "List: " + topLevelList.getText();
//            }
//        }
//
//        String subtaskProjectHierarchyStr = timedItem.getOwnerHierarchyAsString();
//
//        if (listName == null) { //if no list, nor interrupt task, use project hierarchy as top-level context
//            listName = subtaskProjectHierarchyStr;
//            subtaskProjectHierarchyStr = null;
//        }
//
//        if (subtaskProjectHierarchyStr != null) {
//            SpanLabel itemHierarchyContainer = new SpanLabel("Project: " + subtaskProjectHierarchyStr, "BigTimerListTitle");
//            itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //initial state of visibility
////                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//            Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowDownChevron : Icons.iconShowUpChevron);
//            buttonShowItemHierarchy.addActionListener((e) -> {
//                MyPrefs.timerAlwaysExpandListHierarchy.flipBoolean();
////                itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
//                itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //inverse visibility
////                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
//                buttonShowItemHierarchy.setMaterialIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowUpChevron : Icons.iconShowDownChevron); //switch icon
////                buttonShowItemHierarchy.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
//                buttonShowItemHierarchy.getComponentForm().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
//            });
//
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle")))
//                    .add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));
//
//            return contentPane;
//        } else if (listName != null) {
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle"))));
//            return contentPane;
//        } //else: no context to show, show nothing
//        return null;
//    }
//
//    private static Container makeTaskHierarchyContainerNew(ItemAndListCommonInterface itemList, Item timedItem) {
//        //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
//        SpanButton pickTimerSource = new SpanButton();
//        List locallyEditedOwner = new ArrayList(Arrays.asList(itemList));
//        pickTimerSource.setCommand(CommandTracked.create("", null, ev
//                -> //         ScreenObjectPicker2.makePickPrjLstCat("Select Timer source" , true, itemList, pickTimerSource.getComponentForm(), () -> {
//                ScreenObjectPicker2.makePickPrjLstCat("Timer select", true, locallyEditedOwner, pickTimerSource.getComponentForm(), () -> {
//                    if (locallyEditedOwner.size() > 0) { //if >0, first element cannot be null!
//                        ItemAndListCommonInterface selectedOwner = locallyEditedOwner.get(0); //even if multiple should be selected (shouldn't be possible), only use first
//                        workSlot.setOwner(selectedOwner);
//                    }
//                    pickTimerSource.setText(Item.getOwnerHierarchyAsString(itemList.getLeafTaskPath(timedItem, true, null), false);
//                }).show()));
//
//        pickTimerSource.setText(Item.getOwnerHierarchyAsString(itemList.getLeafTaskPath(timedItem, true, null), false));
//
//        Container contentPane = new Container(BoxLayout.y());
//        String listName = null; //"TEMP - Name of the list";
//
//        if (itemList != null) {
//            listName = itemList instanceof Category ? "Category: " : "List: " + itemList.getText(); //source is an ItemList
//        } else if (timedItem.isInteruptOrInstantTask()) {
//            listName = timedItem.getTaskInterrupted() == null ? "INSTANT TASK" : "INTERRUPT TASK";
//        } else {//else: listName remains null, e.g. if only a single task is timed
//            ItemList topLevelList = timedItem.getOwnerTopLevelList();
//            if (topLevelList != null) {
//                listName = "List: " + topLevelList.getText();
//            }
//        }
//
//        String subtaskProjectHierarchyStr = timedItem.getOwnerHierarchyAsString();
//
//        if (listName == null) { //if no list, nor interrupt task, use project hierarchy as top-level context
//            listName = subtaskProjectHierarchyStr;
//            subtaskProjectHierarchyStr = null;
//        }
//
//        if (subtaskProjectHierarchyStr != null) {
//            SpanLabel itemHierarchyContainer = new SpanLabel("Project: " + subtaskProjectHierarchyStr, "BigTimerListTitle");
//            itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //initial state of visibility
////                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//            Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowDownChevron : Icons.iconShowUpChevron);
//            buttonShowItemHierarchy.addActionListener((e) -> {
//                MyPrefs.timerAlwaysExpandListHierarchy.flipBoolean();
////                itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
//                itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //inverse visibility
////                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
//                buttonShowItemHierarchy.setMaterialIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowUpChevron : Icons.iconShowDownChevron); //switch icon
////                buttonShowItemHierarchy.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
//                buttonShowItemHierarchy.getComponentForm().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
//            });
//
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle")))
//                    .add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));
//
//            return contentPane;
//        } else if (listName != null) {
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle"))));
//            return contentPane;
//        } //else: no context to show, show nothing
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * get the hierarchy above the timed item. If timing a list or a project, it
     * is simply the owner hierarchy, if timing a category, it is the project
     * hierarchy up to the item in the category (e.g. cat1 has
     * proj1/subprj2/leaftask3)
     *
     * @param itemList the list/project/category on which the timer was started
     * @param timedItem
     * @return
     */
//    private static Container makeTaskHierarchyContainerNew(ItemList itemList, Item timedItem) {
//        //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
//        Container contentPane = new Container(BoxLayout.y());
//        String listName = null; //"TEMP - Name of the list";
//
//        if (itemList != null) {
//            listName = itemList instanceof Category ? "Category: " : "List: " + itemList.getText(); //source is an ItemList
//        } else if (timedItem.isInteruptOrInstantTask()) {
//            listName = timedItem.getTaskInterrupted() == null ? "INSTANT TASK" : "INTERRUPT TASK";
//        } else {//else: listName remains null, e.g. if only a single task is timed
//            ItemList topLevelList = timedItem.getOwnerTopLevelList();
//            if (topLevelList != null) {
//                listName = "List: " + topLevelList.getText();
//            }
//        }
//
//        String subtaskProjectHierarchyStr = timedItem.getOwnerHierarchyAsString();
//
//        if (listName == null) { //if no list, nor interrupt task, use project hierarchy as top-level context
//            listName = subtaskProjectHierarchyStr;
//            subtaskProjectHierarchyStr = null;
//        }
//
//        if (subtaskProjectHierarchyStr != null) {
//            SpanLabel itemHierarchyContainer = new SpanLabel("Project: " + subtaskProjectHierarchyStr, "BigTimerListTitle");
//            itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //initial state of visibility
////                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//            Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowDownChevron : Icons.iconShowUpChevron);
//            buttonShowItemHierarchy.addActionListener((e) -> {
//                MyPrefs.timerAlwaysExpandListHierarchy.flipBoolean();
////                itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
//                itemHierarchyContainer.setHidden(!MyPrefs.timerAlwaysExpandListHierarchy.getBoolean()); //inverse visibility
////                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
//                buttonShowItemHierarchy.setMaterialIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowUpChevron : Icons.iconShowDownChevron); //switch icon
////                buttonShowItemHierarchy.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
//                buttonShowItemHierarchy.getComponentForm().animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT);
//            });
//
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle")))
//                    .add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));
//
//            return contentPane;
//        } else if (listName != null) {
//            contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName, "BigTimerListTitle"))));
//            return contentPane;
//        } //else: no context to show, show nothing
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowSmallTimerUIXXX(Form currentForm) {
//        /**
//         * different situations to account for: No (more) timers, remove any
//         * shown (remove smallTimer or Back from bigTimer). Replay (with show
//         * smallTimer or not) - do not show timer until last screen. Refresh
//         * (either small or big timer, which is already shown) when timer has
//         * changed. Show timer for first timer = add smallTimer or show
//         * BigTimer.
//         */
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//        MyForm myCurrentForm = null;
//        if (currentForm instanceof MyForm) {
//            myCurrentForm = (MyForm) currentForm;
//        } else {
//            ASSERT.that(false, "Form not instanceof MyForm: " + currentForm);
//            return;
//        }
//
//        if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
//            if (!(myCurrentForm instanceof ScreenTimer6)) {
//                myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
//            };
//        } else { //there is an active timer
//            if (!(myCurrentForm instanceof ScreenTimer6)) { //other form than ScreenTimer6
//                myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
////                if (!(MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
//////                        new ScreenTimer6(previousForm, timerInstance).show();
////                    timerInstance.setFullScreen(true); //set true in case we moved to full screen because of the settings
////                    new ScreenTimer6(myCurrentForm, timerInstance).show();
////                } else {//if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //TODO is this necessary if smallTimer is in South container??
//                    Container smallTimer = buildContentPaneSmallN(myCurrentForm); //refresh the small container
//                    if (smallTimer != null) {
//                        myCurrentForm.addSmallTimerCont(smallTimer);
////                        if (false && smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null) {
////                            myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
////                        }
//                    }
//                }
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * update the UI based on current timer (if any). Either add (or remove!) a
     * small timer to current screen or launch the full screen timer UI
     */
//    void refreshOrShowTimerUI() {
//        refreshOrShowTimerUI(null);
//    }
//
//    void refreshOrShowTimerUI(MyForm previousForm) {
//        refreshOrShowTimerUI(previousForm, false);
//    }
//
//    private void addOrRefreshSmallTimerContainer() {
//        removeTimerSmallContainer();
//        if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//            Container small = buildContentPaneSmall(myCurrentForm); //refresh the small container
//            addSmallTimerWindowIfTimerIsRunning(myCurrentForm, small);
//            myCurrentForm.setStartEditingAsyncTextArea((TextArea) small.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//            myCurrentForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
//        } else { //show full screen timer
//            new ScreenTimer6(myCurrentForm, timerInstance).show();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUINEW(MyForm previousForm, boolean interruptOrInstantTask) {
//    void refreshOrShowUIOnTimerChangeXXX() {
//        if (Display.getInstance().getCurrent() instanceof MyForm) {
//            refreshOrShowTimerUI((MyForm) Display.getInstance().getCurrent());
//        } else {
//            ASSERT.that(false, "refreshOrShowTimerUI() called when showing none-MyForm form=" + Display.getInstance().getCurrent());
//        }
//    }
//    void refreshOrShowUIOnTimerChangeXX() {
////        refreshFormOnTimerUpdate();
//        Form form = Display.getInstance().getCurrent();
//        if (form instanceof MyForm) { //don't do when showing full screen timer
//            if (form instanceof ScreenTimer6) {
//                refreshOrShowTimerUI((MyForm) Display.getInstance().getCurrent());
//            } else {
//                ((MyForm) form).refreshAfterEdit();
//            }
//        }
////        if (Display.getInstance().getCurrent() instanceof MyForm) {
////            refreshOrShowTimerUI((MyForm) Display.getInstance().getCurrent());
////        } else {
////            ASSERT.that(false, "refreshOrShowTimerUI() called when showing none-MyForm form=" + Display.getInstance().getCurrent());
////        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowUIOnTimerChangeOLD() {//, boolean showBigTimer) {
//        /**
//         * different situations to account for: No (more) timers, remove any
//         * shown (remove smallTimer or Back from bigTimer). Replay (with show
//         * smallTimer or not) - do not show timer until last screen. Refresh
//         * (either small or big timer, which is already shown) when timer has
//         * changed. Show timer for first timer = add smallTimer or show
//         * BigTimer.
//         */
//        Form form = Display.getInstance().getCurrent();
//        if (form instanceof MyForm) {
//            MyForm myCurrentForm = (MyForm) form;
//            TimerInstance timerInstance = getCurrentTimerInstanceN();
//            if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
//                if ((myCurrentForm instanceof ScreenTimer6)) {
//                    //if (false) myCurrentForm.showPreviousScreen(true); //exit to previous screen //DON'T exit here, will be done by commands in Big Timer
////                    myCurrentForm.showPreviousScreen(true); //exit to previous screen
//                    ((ScreenTimer6) myCurrentForm).backCommand.actionPerformed(null);
//                } else {
//                    myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
//                    myCurrentForm.refreshAfterEdit();
//                }
//            } else { //there is an active timer
//                if (myCurrentForm instanceof ScreenTimer6) { //if full screen Timer is active, refresh it
//                    if (true) {
//                        myCurrentForm.refreshAfterEdit(); //already done in ScreenTimer6
//                    }
//                } else { //other form than ScreenTimer6
//                    if (timerInstance.isFullScreen()) { //if already (previously) full screen, then show full screen again
////                        timerInstance.setFullScreen(true); //NOW donw in ScreenTimer6 //set true in case we moved to full screen because of the settings
////                        timerInstance.saveMe();
////                    DAO.getInstance().saveInBackground(timerInstance);
//                        if (false) {
//                            new ScreenTimer6(myCurrentForm, timerInstance).show();
//                        }
//                        Command bigTimerCmd = myCurrentForm.getBigTimerReplayCmd();
//                        if (true || bigTimerCmd != null) {
//                            myCurrentForm.getBigTimerReplayCmd().actionPerformed(null);
//                        }
//                    } else if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) { //if small is default
//                        timerInstance.setFullScreen(false); //set true in case we moved to full screen because of the settings
//                        timerInstance.saveMe();
//                        myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                        myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                        Container smallTimer = buildContentPaneSmallN(myCurrentForm); //refresh the small container
//                        if (smallTimer != null) {
//                            myCurrentForm.addSmallTimerCont(smallTimer);
////                            if (false && smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null) {
////                                myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
////                            }
//                        }
//                        myCurrentForm.refreshAfterEdit();
//                    } else { //small not default, so show big and save as default
////                 if (timerInstance.isFullScreen()) {
//                        timerInstance.setFullScreen(true); //NOW donw in ScreenTimer6 //set true in case we moved to full screen because of the settings
//                        timerInstance.saveMe();
////                    DAO.getInstance().saveInBackground(timerInstance);
//                        if (false) {
//                            new ScreenTimer6(myCurrentForm, timerInstance).show();
//                        }
//                        Command bigTimerCmd = myCurrentForm.getBigTimerReplayCmd();
//                        if (true || bigTimerCmd != null) {
//                            bigTimerCmd.actionPerformed(null);
//                        }
//                    }
//                }
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowUIOnTimerChange() {//, boolean showBigTimer) {
//        /**
//         * different situations to account for: No (more) timers, remove any
//         * shown (remove smallTimer or Back from bigTimer). Replay (with show
//         * smallTimer or not) - do not show timer until last screen. Refresh
//         * (either small or big timer, which is already shown) when timer has
//         * changed. Show timer for first timer = add smallTimer or show
//         * BigTimer.
//         */
//        Form form = Display.getInstance().getCurrent();
//        if (form instanceof ScreenTimer7) {
//            MyForm myCurrentForm = (MyForm) form;
//            TimerInstance timerInstance = getCurrentTimerInstanceN();
//            if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
//                if ((myCurrentForm instanceof ScreenTimer6)) {
//                    //if (false) myCurrentForm.showPreviousScreen(true); //exit to previous screen //DON'T exit here, will be done by commands in Big Timer
////                    myCurrentForm.showPreviousScreen(true); //exit to previous screen
//                    ((ScreenTimer6) myCurrentForm).backCommand.actionPerformed(null);
//                } else {
//                    myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
//                    myCurrentForm.refreshAfterEdit();
//                }
//            } else { //there is an active timer
//                if (myCurrentForm instanceof ScreenTimer6) { //if full screen Timer is active, refresh it
//                    if (true) {
//                        myCurrentForm.refreshAfterEdit(); //already done in ScreenTimer6
//                    }
//                } else { //other form than ScreenTimer6
//                    if (timerInstance.isFullScreen()) { //if already (previously) full screen, then show full screen again
////                        timerInstance.setFullScreen(true); //NOW donw in ScreenTimer6 //set true in case we moved to full screen because of the settings
////                        timerInstance.saveMe();
////                    DAO.getInstance().saveInBackground(timerInstance);
//                        if (false) {
//                            new ScreenTimer6(myCurrentForm, timerInstance).show();
//                        }
//                        Command bigTimerCmd = myCurrentForm.getBigTimerReplayCmd();
//                        if (true || bigTimerCmd != null) {
//                            myCurrentForm.getBigTimerReplayCmd().actionPerformed(null);
//                        }
//                    } else if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) { //if small is default
//                        timerInstance.setFullScreen(false); //set true in case we moved to full screen because of the settings
//                        timerInstance.saveMe();
//                        myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                        myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                        Container smallTimer = buildContentPaneSmallN(myCurrentForm); //refresh the small container
//                        if (smallTimer != null) {
//                            myCurrentForm.addSmallTimerCont(smallTimer);
////                            if (false && smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null) {
////                                myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
////                            }
//                        }
//                        myCurrentForm.refreshAfterEdit();
//                    } else { //small not default, so show big and save as default
////                 if (timerInstance.isFullScreen()) {
//                        timerInstance.setFullScreen(true); //NOW donw in ScreenTimer6 //set true in case we moved to full screen because of the settings
//                        timerInstance.saveMe();
////                    DAO.getInstance().saveInBackground(timerInstance);
//                        if (false) {
//                            new ScreenTimer6(myCurrentForm, timerInstance).show();
//                        }
//                        Command bigTimerCmd = myCurrentForm.getBigTimerReplayCmd();
//                        if (true || bigTimerCmd != null) {
//                            bigTimerCmd.actionPerformed(null);
//                        }
//                    }
//                }
//            }
//        }
//    }
//</editor-fold>
    // ************************** START TIMER ************************
//<editor-fold defaultstate="collapsed" desc="comment">
    //
//    static void showPreviousScreenOrDefaultXXX(MyForm previousForm, boolean callRefreshAfterEdit) {
//        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
//            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
//        }
////        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//        MyForm.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//    }
//    void showPreviousScreenXXX(boolean callRefreshAfterEdit) {
//        //now done (correctly) in Back command
//        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
//            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
//        }
////        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//        super.showPreviousScreen(callRefreshAfterEdit); //need to refreshTimersFromParseServer whenever returning from Timer since tasks may have been closed
//    }
//    static void showPreviousScreenOrDefault(boolean callRefreshAfterEdit) {
//        Form f = Display.getInstance().getCurrent();
//        if (f instanceof MyForm) {
//            showPreviousScreenOrDefault((MyForm) f, callRefreshAfterEdit);
//        }
//    }
//</editor-fold>
    @Override
    public void show() {
        //only show if an item is (still) timed, otherwisw showback to return to previous screen, check since show can be called during Replay
//        if (false && TimerStack2.getInstance().getCurrentlyTimedItemN() == null) {
//            //TODO could do sth more fancy here, like show a message that timer was stopped on othr device...
//            backCommand.actionPerformed(null);
//        } else {
        super.show();
//        }
    }

    //**************** BUILD THE UI ****************
    private static String makeUIID(String prefix, boolean isRunning, boolean fullScreenTimer) {
        return prefix + (isRunning ? "" : "Paused") + (fullScreenTimer ? "" : "Small");
    }

    /**
     * build the user interface of the Timer
     *
     * @param timerContainer either container in which to place smallTimer's
     * components, or contentPane of BigTimer form
     * @param myForm
     * @param fullScreenTimer
     * @param formPreviousValues
     * @return null if no active timer
     */
//    private static Container buildContentPane(Form form, Container contentPane, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {
    static void buildContentPane(Container timerContainer, MyForm myForm, boolean fullScreenTimer) {
        buildContentPane(timerContainer, myForm, fullScreenTimer, false);
    }

    static void buildContentPane(Container timerContainer, MyForm myForm, boolean fullScreenTimer, boolean getSmallTimerCont) {
        if (Config.TEST) {
            Log.p("Building TimerContainer for form=" + myForm.getUniqueFormId() + "; fullScreen=" + fullScreenTimer);
        }
        ASSERT.that(myForm != null, "form cannot be null since it's needed for the UITimer");
        if ((timerContainer == null && !getSmallTimerCont) || (myForm instanceof ScreenTimer7 && !fullScreenTimer) || myForm.isHideSmallTimer()) {
//            ASSERT.that(timerContainer != null, "timerContainer cannot be null since it's needed for the UITimer");
            Log.p("ScreenTimer7.buildContentPane called with timerContainer null from screen " + myForm);
            return;
        }
        if (timerContainer == null && getSmallTimerCont) {
            timerContainer = myForm.getAndMakeSmallTimerContN();
        }
        if (timerContainer == null) { //can be null eg in ScreenObjectPicker where smallTimer is not shown
            return;
        }

        timerContainer.removeAll();
//        TimerStack2 model = TimerStack2.getInstance();
//        TimerInstance2 timerInstanceN = TimerStack2.getInstance().getCurrentTimerInstanceN();
        TimerInstance2 timerInstanceN = TimerStack2.getTimerInstanceN();
        if (timerInstanceN == null) {
//            return;
            if (Config.TEST) {
                Log.p("Calling Timer with no active Timer");
            }
        }

//        Item timedItem = model.getCurrentlyTimedItemN(); //currEntry.timedItem;
//        Item timedItem = TimerStack2.getTimedItemN(); //currEntry.timedItem;
//        Item timedItem = TimerStack2.getInstance().getCurrentlyTimedItemN(); //currEntry.timedItem;
//        Item timedItem = TimerStack2.getTimerInstanceN().getCurrentlyTimedItemN(); //currEntry.timedItem;
//        Item timedItem = TimerStack2.getTimedItemN() != null ? TimerStack2.getTimedItemN() : new Item(); //currEntry.timedItem;
//        Item timedItemN = TimerStack2.getTimedItemN(); //currEntry.timedItem;
        Item timedItemN = TimerStack2.getInstance().getCurrentlyTimedItemN(); //currEntry.timedItem;
//        if (timedItem == null) {
////            return;
//            if (Config.TEST) {
//                Log.p("Calling Timer with no Item to time (null)");
//            }
//        }

        //create a new Item if timer was started 'empty' (no item/source), first edit of item info should create and 
//        ActionListener createNewItem = (e) -> {
//            if (timedItemN == null) {
//                Item newItem = new Item();
//                TimerStack2.getInstance().startTimer(newItem, null, myForm, true, true, true);
//                if (false) { //startTimer above should send event to timer screen
//                    buildContentPane(timerContainer, myForm, fullScreenTimer);
//                }
//            }
//        };
//        boolean isRunning = timerInstanceN != null && timerInstanceN.isRunning();
//        ItemAndListCommonInterface timerSourceN = timerInstanceN != null ? timerInstanceN.getTimerSourceN() : null;
        ItemAndListCommonInterface timerSourceN = timerInstanceN != null ? timerInstanceN.getTimerSourceN() : (timedItemN != null ? timedItemN.getOwner() : null); //use owner as source if no other specific source is indicated (works eg when creating a new task which is added to Inbox)

        Button elapsedTimeButton = new Button("", (fullScreenTimer ? "TimerStartStop" : "TimerStartStopSmall"));

        MyDurationPicker hiddenElapsedTimePicker = new MyDurationPicker();
        hiddenElapsedTimePicker.setFormatter(new SimpleDateFormat("")); //hiddenElapsedTimePicker must be added to a Form, but we don't want to show it, only activate it via a longpress on the timer button
        hiddenElapsedTimePicker.setTraversable(false); //avoid that a user can tab to activiate this field
        hiddenElapsedTimePicker.setHidden(true); //hiddenElapsedTimePicker must be added to a Form, but we don't want to show it, only activate it via a longpress on the timer button
        hiddenElapsedTimePicker.setUIID("Container"); //Container to avoid overwritng content
        hiddenElapsedTimePicker.addPointerPressedListener((e) -> {
            if (timerInstanceN == null) {
//                createNewItem.actionPerformed(null);
//                Item newItem = new Item("New task");
//                hiddenElapsedTimePicker.setDuration(TimerStack2.getTimerInstanceN().getElapsedTimeToDisplay()); //must edit total elapsed time since that is what is shown
//                TimerStack2.getInstance().startTimer(newItem, null, myForm, true, true, true); //will save both new timerInstance and new item
                hiddenElapsedTimePicker.setDuration(0);
            } else //TODO: if running, stop timer while editing its value. NO, no reason
            {
                hiddenElapsedTimePicker.setDuration(timerInstanceN.getElapsedTimeToDisplay()); //must edit total elapsed time since that is what is shown
            }
        });

        hiddenElapsedTimePicker.addActionListener((e) -> {
//            TimerStack2.getInstance().updateCurrentlyTimedItemWithEditedElapsedTime(timerInstance, hiddenElapsedTimePicker.getDuration(), false);
            if (timerInstanceN == null) { //timer time was set when no timed item exists
                Item newItem = new Item(true, MSG_NEW_TASK_DEFAULT_TEXT, timerSourceN, Inbox.getInstance());
                newItem.setActualForTaskItself(hiddenElapsedTimePicker.getDuration(), true);
                TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true); //will save both new timerInstance and new item
//                TimerStack2.getInstance().fireChangedEvent();
//                DAO.getInstance().triggerParseUpdate();
            } else {
                timerInstanceN.updateCurrentlyTimedItemWithEditedElapsedTime(hiddenElapsedTimePicker.getDuration(), false);
            }
        });

        Label timerHoursMinBack = new Label("88:88", makeUIID("TimerHoursMinBack", true, fullScreenTimer));
        Label timerSecondsBack = new Label(":88", makeUIID("TimerSecondsBack", true, fullScreenTimer));
        Label timerHoursMinTop = new Label("88:88", makeUIID("TimerHoursMinTop", timerInstanceN != null && timerInstanceN.isRunning(), fullScreenTimer));
        Label timerSecondsTop = new Label("", makeUIID("TimerSecondsTop", timerInstanceN != null && timerInstanceN.isRunning(), fullScreenTimer));

        Button timerStartStopButton = new Button(Command.create("", null,
                (e) -> {
//                    if (TimerStack2.getTimedItemN() == null) {
                    if (timedItemN == null) {
//                        createNewItem.actionPerformed(null);
                        Item newItem = new Item(true, MSG_NEW_TASK_DEFAULT_TEXT, timerSourceN, Inbox.getInstance());
                        TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, false); //will save both new timerInstance and new item
//                        TimerStack2.getInstance().fireChangedEvent();
//                        DAO.getInstance().triggerParseUpdate();
                    } else { //if timedItem was created just above, still flip state since by default it is off
                        TimerStack2.getInstance().flipRunningTimerState();
                        TimerStack2.getInstance().fireChangedEvent();
//                        DAO.getInstance().saveToParseLater(TimerStack2.getTimerInstanceN());
                        DAO.getInstance().saveToParseLater(timerInstanceN);
                        DAO.getInstance().triggerParseUpdate();
                    }
                }));
        if (Config.TEST) {
            timerStartStopButton.setName("timerStartStopButton");
        }
        timerStartStopButton.setFontIcon(Icons.myIconFont, timerInstanceN != null && timerInstanceN.isRunning() ? Icons.iconTimerPauseCust : Icons.iconTimerStartCust);
        timerStartStopButton.setUIID(makeUIID("TimerStartStop", timerInstanceN != null && timerInstanceN.isRunning(), fullScreenTimer));

//        timerStartStopButton.addActionListener((e) -> {
//            TimerStack2.getInstance().flipRunningTimerState(); //model will refresh UI
//        });
        Container timerTimeContainer = new Container(new LayeredLayout(), "TimerContainer" + (fullScreenTimer ? "" : "Small"));
        if ((fullScreenTimer && MyPrefs.timerShowSecondsInTimer.getBoolean()) || (!fullScreenTimer && MyPrefs.timerShowSecondsInSmallTimer.getBoolean())) {
            timerTimeContainer.addAll(BoxLayout.encloseXNoGrow(timerHoursMinBack, timerSecondsBack), BoxLayout.encloseXNoGrow(timerHoursMinTop, timerSecondsTop), hiddenElapsedTimePicker);
        } else {
            timerTimeContainer.addAll(BoxLayout.encloseXNoGrow(timerHoursMinBack), BoxLayout.encloseXNoGrow(timerHoursMinTop), hiddenElapsedTimePicker);
        }
        timerTimeContainer.setLeadComponent(hiddenElapsedTimePicker);

        MyDurationPicker estimate = new MyDurationPicker("BigTimerEstimate", "Button", Icons.myIconFont, Icons.iconEstimateCust, 0, true);
        estimate.setDuration(timedItemN != null ? timedItemN.getEstimateTotal() : 0);

        MyDurationPicker totalActualEffort = new MyDurationPicker("BigTimerTotalActual", "Button", Icons.myIconFont, Icons.iconActualCurrentCust, 0, true);
        totalActualEffort.addActionListener((e) -> {
//            if (TimerStack2.getTimedItemN() == null) {
            if (timedItemN == null) {
//                createNewItem.actionPerformed(null);
                Item newItem = new Item(true, MSG_NEW_TASK_DEFAULT_TEXT, timerSourceN, Inbox.getInstance());
                newItem.setRemainingForTaskItself(totalActualEffort.getDuration(), true); //true:auto-update other fields
                TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true); //will save both new timerInstance and new item       
//                TimerStack2.getInstance().fireChangedEvent();
//                DAO.getInstance().triggerParseUpdate();
//                TimerStack2.getTimerInstanceN().updateCurrentlyTimedItemWithEditedElapsedTime(totalActualEffort.getDuration(), true);
            } else { //NB! when editing the total, we may BOTH change the current Timer value AND reduce the previously recorded Actual for the timed item
                //            TimerStack2.updateCurrentlyTimedItemWithEditedElapsedTime(timerInstance, totalActualEffort.getDuration(), true);
                timerInstanceN.updateCurrentlyTimedItemWithEditedElapsedTime(totalActualEffort.getDuration(), true);
                TimerStack2.getInstance().fireChangedEvent();
                DAO.getInstance().saveToParseLater(timerInstanceN);
                DAO.getInstance().triggerParseUpdate();
            }
        });

        MyDurationPicker remainingEffort = new MyDurationPicker("BigTimerEffort", "Button", Icons.myIconFont, Icons.iconRemainingCust, 0, true);
//            remainingEffort.setDuration(timedItem.getRemainingEffort());
//        remainingEffort.setDuration(timedItemN != null ? timedItemN.getRemainingForTaskItselfFromParse() : 0);
        remainingEffort.setDuration(timedItemN != null ? timedItemN.getRemainingForTaskItself(): 0);

        //BUZZER timer
        final int BUZZER_DURATION = 300;
        MyUITimer buzzerTimer = new MyUITimer(() -> {
            if (Config.TEST) {
                Log.p("buzzerTimer started " + new MyDate() + ", for Form=" + myForm);
            }

            if (Display.getInstance().getCurrent() != myForm) {
                return; //only update for the displayed form
            }
            Display.getInstance().vibrate(BUZZER_DURATION); //ignored on iOS (uses system-set duration)
            if (Config.TEST) {
                Log.p("Buzz");
            }
        });

        //
        //        ActionListener refreshElapsedTime = (e) -> {
        Runnable refreshElapsedTime = () -> {
            long time = timerInstanceN != null ? timerInstanceN.getElapsedTimeToDisplay() : 0;
//            if (false && time >= MyPrefs.timerMaxDurationMillis.getInt()) { //false: disable for now, probably not useful
//                //force stop when max is reached (or over-reached)
//                timerInstanceN.stopTimer();
//                timerInstanceN.setElapsedTime(MyPrefs.timerMaxDurationMillis.getInt());
//            }
            String hoursMinutes = MyDate.formatDurationStd(time, false);
            if (hoursMinutes.length() < 5) { //a bit of a hack
                hoursMinutes = "0" + hoursMinutes;
            }
            int sec = ((int) (time % MyDate.MINUTE_IN_MILLISECONDS)) / MyDate.SECOND_IN_MILLISECONDS;
            timerSecondsTop.setText(":" + (sec < 10 ? "0" + sec : sec)); //always update seconds, even if not shown (simpler, faster)
            if ((sec / 2) * 2 != sec) { //show on even (starting with zero!)
                hoursMinutes = hoursMinutes.replace(':', ' ');
            }
            timerHoursMinTop.setText(hoursMinutes);

            //refresh totalEffort
            long totalEffort = timerInstanceN != null ? timerInstanceN.getElapsedTotalTime() : 0;
            if (false) {
                totalActualEffort.setText(MyDate.formatDurationStd(timerInstanceN != null ? timerInstanceN.getElapsedTotalTime() : 0, false)); //false=don't show seconds in Total
            } else {
                totalActualEffort.setDuration(timerInstanceN != null ? timerInstanceN.getElapsedTotalTime() : 0); //false=don't show seconds in Total
            }
            totalActualEffort.repaint();

            timerTimeContainer.repaint();//this is enough to update the value on the screen
        };

        //TASK TIMER
//        MyUITimer timerTimer = new MyUITimer(updateTimerDisplayForCurrentFormXXX);
        refreshElapsedTime.run();//refresh timer before 1st display

        MyUITimer timerTimer = new MyUITimer(refreshElapsedTime);
        if (timerInstanceN != null && timerInstanceN.isRunning()) {
            ASSERT.that(myForm != null);
            timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, myForm); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
            if (MyPrefs.timerBuzzerActive.getBoolean()) {
                buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt() != 0 ? MyPrefs.timerBuzzerInterval.getInt() * MyDate.MINUTE_IN_MILLISECONDS : Integer.MAX_VALUE, true, myForm); //Integer.MAX_VALUE=25days so little risk of unexpceted buzz
            }
        }

        ActionListener startFormUpdateTimers = (e) -> {
            ASSERT.that(myForm != null);
            timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, myForm); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
            if (MyPrefs.timerBuzzerActive.getBoolean()) {
                buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt() != 0 ? MyPrefs.timerBuzzerInterval.getInt() * MyDate.MINUTE_IN_MILLISECONDS : Integer.MAX_VALUE, true, myForm); //Integer.MAX_VALUE=25days so little risk of unexpceted buzz
            }
        };

        //TIMER to update item status to ongoing
        if (timedItemN != null) {
//            MyUITimer setTimedItemOngoing = new MyUITimer(() -> {
//                if (timedItemN.getStatus() == ItemStatus.CREATED
//                        && MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() > 0
//                        && timerInstanceN.getElapsedTotalTime() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//                    timedItemN.setStatus(ItemStatus.ONGOING, false); //update status, timer will be updated on callback
//                }
//            });
//            if (false && timedItemN.getStatus() == ItemStatus.CREATED
//                    && timerInstanceN.getElapsedTotalTime() < MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) { //
//                setTimedItemOngoing.schedule((int) (MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS - timedItemN.getActualTotal() + 100), false, myForm); //+100: ensure timer only triggers *after* the threshold is passed
//            }
            if (timedItemN.getStatus() == ItemStatus.CREATED
                    && MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() > 0 //only set to ongoing if mimimum is set (!=0)
                    && timerInstanceN.getElapsedTotalTime() < MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                MyUITimer setTimedItemToOngoing = new MyUITimer(() -> {
                    if (timedItemN.getStatus() == ItemStatus.CREATED
                            && timerInstanceN.getElapsedTotalTime() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                        timedItemN.setStatus(ItemStatus.ONGOING, false); //update status, timer will be updated on callback
                    }
                });
//                setTimedItemToOngoing.schedule(((int) ((long) MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt()) * MyDate.SECOND_IN_MILLISECONDS
//                        - timedItemN.getActualTotal() + 100), //+100: ensure timer only triggers *after* the threshold is passed
                setTimedItemToOngoing.schedule((MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS
                        - (int) timedItemN.getActualTotal() + 100), //+100: ensure timer only triggers *after* the threshold is passed
                        false, myForm);
            }
        }

        ActionListener stopUITimers = (e) -> {
            if (timerTimer != null && timerTimer.isScheduled()) {
                timerTimer.cancel(); //the timers may have been stopped manually when calling the commands
            }
            if (buzzerTimer != null && buzzerTimer.isScheduled()) {
                buzzerTimer.cancel();
            }
        };

        if (Display.getInstance().isScreenSaverDisableSupported() // && !MyPrefs.keepScreenAlwaysOnInApp.getBoolean()
                && MyPrefs.timerKeepScreenAlwaysOnInTimer.getBoolean()) {
            //NB. previous state is defined by keepScreenAlwaysOnInApp
            Display.getInstance().setScreenSaverEnabled(false);
        }

        // ********************************* COMMON elements **************************
        //Set UITimer to updated 
//        MyCheckBox status = new MyCheckBox(timedItemN != null ? timedItemN.getStatus() : ItemStatus.CREATED);
        MyCheckBox status = new MyCheckBox(timedItemN);
        status.setUIID(fullScreenTimer ? "BigTimerItemStatus" : "SmallTimerItemStatus");
        //TODO!!! create a new task if status is clicked when timer is started without a task

//Show next task
        //TODO optimization: only construct nextTask containers etc if there is one and it is shown
        //TODO show button to select auto-start Timer on task or not
        TextField description = new MyTextField(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
        description.setText(timedItemN != null ? timedItemN.getText() : "");
        StringBuffer lastDescriptionText = new StringBuffer(description.getText());
        description.putClientProperty("iosHideToolbar", Boolean.TRUE); //hide toolbar and only show Done button for ios virtual keyboard -> AVOID making Picker pop up?!
        description.setGrowByContent(fullScreenTimer);
        description.setCommitTimeout(300); //default is 1000, probably too long if you enter text and immediately swipe to another app
        description.setEditable(true); //true=editable (but will look like a label until clicked), false=not editable in small container
        description.setUIID(fullScreenTimer ? "BigTimerItemText" : "SmallTimerItemText");
        description.setTraversable(fullScreenTimer); //false=not travesable in small Timer, avoid that tapping Next in a PinchInsert container will activate this field
        description.setActAsLabel(true);
        description.setSingleLineTextArea(!fullScreenTimer); //single line in small timer

//        description.addActionListener((e) -> {
        ActionListener itemTextListener = (e) -> {
//            String lastDescriptionText=null;
//            if (timedItemN == null) {
//            if (TimerStack2.getTimedItemN() == null) {
//            if (timedItemN == null || !Objects.equals(timedItemN.getText(), description.getText())) { //avoid triggering listner twice (on action and Done)
//            if ( !description.getText().isEmpty()) { //avoid triggering listner twice (on action and Done)
//            if (timedItemN == null || !Objects.equals(description.getText(), timedItemN.getText())) { //avoid triggering listner twice (on action and Done)
//            if (lastDescriptionText == null || !Objects.equals(description.getText(), lastDescriptionText)) { //avoid triggering listner twice (on action and Done)
            if (!Objects.equals(description.getText(), lastDescriptionText.toString())) { //avoid triggering listner twice (on action and Done)
                lastDescriptionText.setLength(0); //hack to save the string in the lambda function
                lastDescriptionText.append(description.getText());
                if (timedItemN == null) {
//                createNewItem.actionPerformed(null);
                    Item newItem = new Item(true, description.getText(), timerSourceN, Inbox.getInstance());
//                timedItemN = new Item(true, description.getText(), timerSourceN, Inbox.getInstance());
                    TimerStack2.getInstance().startTimerAndSave(newItem, Inbox.getInstance(), myForm, false, true, true); //will save both new timerInstance and new item
//                TimerStack2.getInstance().fireChangedEvent();
//                DAO.getInstance().triggerParseUpdate();
//                TimerStack2.getTimedItemN().setText(description.getText());
//                DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
                } else {
//                if (!description.getText().equals(TimerStack2.getTimedItemN().getText())) { //check if text is different since actionListener may be called twice in a row
//                    if (!description.getText().equals(timedItemN.getText())) { //check if text is different since actionListener may be called twice in a row
                    if (!Objects.equals(description.getText(), timedItemN.getText())) { //check if text is different since actionListener may be called twice in a row
//                    TimerStack2.getTimedItemN().setText(description.getText());
                        timedItemN.setText(description.getText());
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
//                    DAO.getInstance().saveToParseLater(TimerStack2.getTimedItemN());
                        DAO.getInstance().saveToParseLater(timedItemN);
                        TimerStack2.getInstance().fireChangedEvent();
                        DAO.getInstance().triggerParseUpdate();
                    }
                }
                if (true) {
//                    lastDescriptionText.setLength(0); //hack to save the string in the lambda function
//                    lastDescriptionText.append(description.getText());
                } else {
//                    lastDescriptionText=description.getText();
                }
            }
        };
        description.addActionListener(itemTextListener);
        description.setDoneListener(itemTextListener);
        //TODO!!! do NOT use item.isInteruptTask() since we may later continue working on a task that was originally created as an interrupt but after that is just treated as a normal task
        if (false && timedItemN.isInteruptOrInstantTask() && description.getText().equals("")) { //false=NOT good UI on phones since keyboard will pop up!
            myForm.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
        }

        Button editItemButton = new Button(MyReplayCommand.create("EditItemFromTimer-" + (timedItemN != null ? timedItemN.getReplayId() : "New"), "", Icons.iconEdit,
                (e) -> {
//                    if (TimerStack2.getTimedItemN() == null) {
                    if (timedItemN == null) {
//                        createNewItem.actionPerformed(null);
//                        Item newItem = new Item(true);
                        Item newItem = new Item(true, "", timerSourceN, Inbox.getInstance());
//                        TimerStack2.getInstance().startTimer(newItem, null, myForm, true, true, true); //will save both new timerInstance and new item
                        new ScreenItem2(newItem, myForm, () -> {
                            if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                                TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true);
                            }
                        }, //start timer only *after* editing the task
                                false, null).show(); //will this work?? (return to right form after new item is created?)
                    } else {
//                        ScreenItem2 screenItem = new ScreenItem2(timedItemN, (MyForm) myForm, () -> {
//                        new ScreenItem2(TimerStack2.getTimedItemN(), (MyForm) myForm, () -> {
                        ScreenItem2 editItemScr = new ScreenItem2(timedItemN, (MyForm) myForm, () -> {
//                        if (false) {
//                            description.setText(timedItem.getText());
//                            status.setStatus(timedItem.getStatus());
//                            comment.setText(timedItem.getComment());
//                            estimate.setDuration(timedItem.getEstimateTotal());
//                        }
//                        remainingEffort.setDuration((int) timedItem.getRemainingForTaskItselfFromParse()); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
//                        refreshTotalActualEffort.actionPerformed(null); //refresh screen
//                        ((MyForm) myForm).refreshAfterEdit();
//                        }, false, null).show(); //previousValues: pass locally edited value to ScreenItem
                        }, false, null); //previousValues: pass locally edited value to ScreenItem
                        editItemScr.setHideSmallTimer(true);
                        editItemScr.show(); //previousValues: pass locally edited value to ScreenItem
//                        screenItem.show();
                    }
                }
        ));

        if (!fullScreenTimer) {
            //SMALL TIMER container
//            boolean interruptTask = timedItemN!=null?timedItemN.isInteruptOrInstantTask():null;

//            Container topContainer = new Container(new MyBorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER, MyBorderLayout.SIZE_EAST_BEFORE_WEST));
            Container topContainer = new Container(new MyBorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE, MyBorderLayout.SIZE_EAST_BEFORE_WEST));
            Container swipeButtonsCont = new Container(BoxLayout.x());
            SwipeableContainer swipeable = new SwipeableContainer(null, swipeButtonsCont, topContainer);

            if (Config.TEST) {
                swipeable.setName("SmallTimerSwipeable");
            }

            if (false) {
                swipeable.setGrabsPointerEvents(true);
            }
            swipeable.setGrabsPointerEvents(false);

            Button nextTask = new Button(CommandTracked.create(MSG_NEXT_TASK_SMALL_TIMER, Icons.iconTimerNextTask,
                    (e) -> {
                        TimerStack2.getInstance().goToNextTimedItem();
                        swipeable.close();
                        TimerStack2.getInstance().fireChangedEvent();
//                        myForm.animateMyForm();
//                        myForm.refreshAfterEdit();
                        DAO.getInstance().triggerParseUpdate();
                    },
                    "TimerCmdStopTimerAndGotoNextTaskOrExit")); //, Icons.iconTimerNextTask);
            nextTask.setUIID("SmallTimerSwipeNext");
            nextTask.setName("SmallTimerSwipeNext");

            Button stopCurrentTimer = new Button(CommandTracked.create(MSG_STOP_TIMER, Icons.iconTimerStopTimer, //"Exit" "End" "Quit" "Stop" "Kill" "Halt" "Leave" "Terminate", "Finish timing", "Finish Timer", "End Timer", "Quit Timer", "Close Timer", "Stop timing"
                    (e) -> {
                        TimerStack2.getInstance().stopCurrentTimerAndGotoNext();
                        swipeable.close();
//                        myForm.animateMyForm();
                        TimerStack2.getInstance().fireChangedEvent();
//                        myForm.refreshAfterEdit();
                        DAO.getInstance().triggerParseUpdate();
                    }, //stopping timer->callback to update container
                    "TimerCmdSaveAndExitSmallTimer")); //, Icons.iconTimerNextTask);
            stopCurrentTimer.setUIID("SmallTimerSwipeExit");
            stopCurrentTimer.setName("SmallTimerSwipeExit");

            swipeButtonsCont.addAll(stopCurrentTimer, nextTask);

            //TIMER FULL SCREEN BUTTON ('>')
            Button fullScreenTimerButton = new Button(MyReplayCommand.createMaterial("", Icons.iconEdit, (e) -> {
//                if (false && timerInstanceN != null) {
//                    timerInstanceN.setFullScreen(true); //save full screen state
//                    DAO.getInstance().saveToParseNow(timerInstanceN);
//                } //can still show full screen if nothing is timed
//                if (false) {
//                    new ScreenTimer7(myForm).show();
//                }
                if (myForm.getBigTimerReplayCmd() != null) {
                    myForm.getBigTimerReplayCmd().actionPerformed(null);
                } else {
                    if (!(myForm instanceof ScreenTimer7)) {
                        ASSERT.that("myForm.getBigTimerReplayCmd() == null??!!");
                    }
                }
            }));
            fullScreenTimerButton.setUIID("SmallTimerEditItem");

//            SmallTimerContainer timerContainer = new SmallTimerContainer() {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
//                }
//            };
//            topContainer.setLayout(new MyBorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER, MyBorderLayout.SIZE_EAST_BEFORE_WEST));
//            topContainer.add(BorderLayout.CENTER, BoxLayout.encloseX(description));
//            topContainer.add(BorderLayout.WEST, status);
//            topContainer.add(BorderLayout.WEST, BoxLayout.encloseXNoGrow(status, description));
            topContainer.add(BorderLayout.WEST, status).add(BorderLayout.CENTER, description);
            topContainer.setUIID("ContainerOpaque");


            /*East*/
            Container east = BorderLayout.centerAbsolute(BoxLayout.encloseXNoGrow(timerTimeContainer, timerStartStopButton, fullScreenTimerButton));
            topContainer.add(BorderLayout.EAST, east);

            timerContainer.add(swipeable);
        } else {
            //BIG TIMER 
            if (timedItemN != null && timedItemN.isInteruptOrInstantTask() && description.getText().equals("")) {
//                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
//                myForm.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                description.startEditingAsync();
            }

            MyButton addNewTask = null;
            MyButton addNewSubtask = null;

            if (fullScreenTimer) {
                if ((timedItemN instanceof Item && timedItemN.getOwner() != null)) { //if a task is being timed, and it has an owner, then we can add add'l tasks/subtasks

                    Command addNewTaskCommand = CommandTracked.create(MSG_NEW_TASK_CMD, null, (e) -> { //"Add task" "
                        //add a new task after the current
//                        ItemAndListCommonInterface listToAddTaskTo = timerSourceN;
                        Item newTask = new Item(true);
                        //if currently timedItem is
                        if (Objects.equals(timedItemN.getOwner(), timerSourceN)) { //we're timing an item directly in source, so we simply add new task after the currently timed
                            timerSourceN.addToList(newTask, timedItemN, true); //UI:add new task *after* currently timed (so it becomes the next to be timed)
                        } else {
                            if (timedItemN.getOwner() instanceof Item) {
                                if (timerSourceN instanceof Category && timedItemN.getCategories().contains(timerSourceN)) { //if we're timing an item in the shown category (and NOT a subtask of a project in the category)
                                    ((Category) timerSourceN).addItemToCategory(newTask, timedItemN, true, true); //add new task to category, *after* currently timed item
                                    //NB. newTask will automatically be added to Inbox on save
                                } else { //we're timing a subtask in a project
                                    ASSERT.that(Objects.equals(timedItemN.getOwnerTopLevelList(), timerSourceN)
                                            || (timerSourceN instanceof Category && timedItemN.getOwnerTopLevelProject().getCategories().contains(timerSourceN)), "when timing a subtask, the prject should always belong to the source");
                                    if (MyPrefs.timerAddNewTaskToTimedListInsteadOfTimedProject.getBoolean()) {
                                        timerSourceN.addToList(newTask, timedItemN, true);
                                    } else {
                                        ItemAndListCommonInterface owner = timedItemN.getOwner();
                                        owner.addToList(newTask, timedItemN, true); //UI:add new task *after* currently timed (so it becomes the next to be timed)
                                    }
                                }
                            }
                        }
                        TimerStack2.getInstance().goToNextTimedItem(); //stop and save a currently timed item
                        TimerStack2.getInstance().fireChangedEvent();
                        TimerStack2.getInstance().saveAll();
                        DAO.getInstance().triggerParseUpdate();
//                        TimerStack2.getInstance().(status, fullScreenTimer);
                    }, "TimerAddNewTask");
                    addNewTask = new MyButton(addNewTaskCommand);
                    addNewTask.setLongPressHelp("Add new task to \"" + timedItemN.getOwner().getText() + "\" after current task and start timing it");

                    Command addNewSubtaskCommand = CommandTracked.create(MSG_NEW_SUBTASK_CMD, null, (e) -> { //"Add subtask"
                        //add a new task after the current
                        Item newTask = new Item(true);
//                        ItemAndListCommonInterface owner = timedItemN.getOwner();
                        timedItemN.addToList(newTask, false); //UI:false=>always add new subtask to start of subtasks (so it becomes the next to the timed)
                        TimerStack2.getInstance().goToNextTimedItem();
                        TimerStack2.getInstance().fireChangedEvent();
                        DAO.getInstance().triggerParseUpdate();
//                        TimerStack2.getInstance().saveAll();
                    }, "TimerAddNewSubtaskToOwner");
                    addNewSubtask = new MyButton(addNewSubtaskCommand);
                    addNewSubtask.setLongPressHelp("Add new subtask to \"" + timedItemN.getText() + "\" and start timing it");
                } else if ((timerInstanceN != null && timerInstanceN.getTimerSourceN() != null)) { //otherwise, if a source is defined, we can add new tasks to that:

                    Command addNewTaskCommand = CommandTracked.create(MSG_NEW_TASK_CMD, null, (e) -> { //"Add task" "
                        //add a new task after the current
                        Item newTask = new Item(true);
                        ItemAndListCommonInterface source = timerInstanceN.getTimerSourceN();
//                        source.addToList(newTask, timedItemN, true);
                        source.addToList(newTask);
                        TimerStack2.getInstance().goToNextTimedItem();
                        TimerStack2.getInstance().fireChangedEvent();
                        DAO.getInstance().triggerParseUpdate();
//                        TimerStack2.getInstance().saveAll();
                    }, "TimerAddNewTaskToSource");
                    addNewTask = new MyButton(addNewTaskCommand);
                }
            }

            //LAYOUT the Timer
//            Container timerBigTimerTopLevelContainer = new Container(new BorderLayout()); //top-level container for TimerScreen
//            Container timerContainer = timerContainer; //top-level container for TimerScreen
//            timerBigTimerTopLevelContainer.setScrollableY(true); //since the size of the timer may overflow
            Container timeContainer = new Container(BoxLayout.y());
            timeContainer.setScrollableY(true);
//            timerContainer=timeContainer;

//            Container interruptedTimersContainer = new Container(BoxLayout.y());
            if (TimerStack2.getTimerStackSize() > 1) {
                List<TimerInstance2> activeTimers = TimerStack2.getActiveTimers();
//                 interruptedTimers =new MySpanButton("Interrupted timers "+TimerStack2.getTimerStackSizeAsStr()+":"); //"Paused tiemrs" "Pending timers" "Waiting timers"
//                interruptedTimers.setMaterialIcon(Icons.iconShowMore);
//                interruptedTimersContainer.add(interruptedTimers); //"Paused tiemrs" "Pending timers" "Waiting timers"
                Container interruptedList = new Container(BoxLayout.y());
                for (int i = 0, size = activeTimers.size() - 1; i < size; i++) {
                    TimerInstance2 interruptedTimerInstance = activeTimers.get(i);
                    Item itm = interruptedTimerInstance.getTimedItemN();
//                    interruptedTimersContainer.add(new SpanLabel(itm.getOwnerHierarchyAsString(interruptedTimerInstance.getTimerSourceN()))); 
//                    interruptedTimersContainer.add(new SpanLabel(ItemAndListCommonInterface.getHierarchyAsStringN(interruptedTimerInstance.getTimerSourceN(), itm, true, true)));
//                    interruptedList.add(new SpanLabel(ItemAndListCommonInterface.getHierarchyAsStringN(interruptedTimerInstance.getTimerSourceN(), itm, true, true)));
                    SpanLabel intTxtLabel = new SpanLabel(
                            ItemAndListCommonInterface.getHierarchyAsStringN(interruptedTimerInstance.getTimerSourceN(), itm, true, true, "\"\"") //"<no text>"
                            + " " + MyDate.formatDurationStd(interruptedTimerInstance.getElapsedTimeToDisplay()));
                    if (itm != null && itm.isInteruptOrInstantTask()) {
                        intTxtLabel.setMaterialIcon(Icons.iconTimerLaunch);
                        intTxtLabel.setTextPosition(Component.LEFT);
                    }
//                    intTxtLabel.setUIID("ExpandableContainerElt");
                    intTxtLabel.setTextUIID("ExpandableContainerElt");
                    interruptedList.add(intTxtLabel);
                }
//                ExpandableContainer interruptedTimers = new ExpandableContainer("Interrupted timers " + TimerStack2.getTimerStackSizeAsStr() + ":", interruptedList); //"Paused tiemrs" "Pending timers" "Waiting timers"
                ExpandableContainer interruptedTimers
                        = new ExpandableContainer(TimerStack2.getTimerStackLabelTxt(), interruptedList,
                                () -> !MyPrefs.timerShowInterruptedTimerListExpanded.getBoolean(),
                                (b) -> MyPrefs.setBoolean(MyPrefs.timerShowInterruptedTimerListExpanded, !b)); //"Paused tiemrs" "Pending timers" "Waiting timers"
//                interruptedTimersContainer.add(makeSpacer());
                timerContainer.add(BorderLayout.NORTH, interruptedTimers);
            }

            //TIMER
            Container timerButtonsContainer = new Container(BoxLayout.y());

//            timerContainer.add(BorderLayout.NORTH, interruptedTimersContainer);
            timerContainer.add(BorderLayout.CENTER, timeContainer);
            timerContainer.add(BorderLayout.SOUTH, timerButtonsContainer);

            remainingEffort.setShowZeroValueAsZeroDuration(true); //show "0:00"
            estimate.setShowZeroValueAsZeroDuration(true); //show "0:00"

            estimate.addActionListener((e) -> {
//                if (TimerStack2.getTimedItemN() == null) {
                if (timedItemN == null) {
//                    createNewItem.actionPerformed(null);
                    Item newItem = new Item(true, MSG_NEW_TASK_DEFAULT_TEXT, timerSourceN, Inbox.getInstance());
                    newItem.setEstimateForTask(estimate.getDuration(), true); //true:auto-update other fields
                    TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true); //will save both new timerInstance and new item     
//                    TimerStack2.getTimedItemN().setEstimateForTask(estimate.getDuration(), false); //saved immediately on edit
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
//                    TimerStack2.getInstance().fireChangedEvent();
//                    DAO.getInstance().triggerParseUpdate();
                } else {
//                    TimerStack2.getTimedItemN().setEstimateForTask(estimate.getDuration(), false); //saved immediately on edit
                    timedItemN.setEstimateForTask(estimate.getDuration(), false); //saved immediately on edit
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
//                    DAO.getInstance().saveToParseLater(TimerStack2.getTimedItemN());
                    DAO.getInstance().saveToParseLater(timedItemN);
                    TimerStack2.getInstance().fireChangedEvent();
                    DAO.getInstance().triggerParseUpdate();
                }
            });

            remainingEffort.addActionListener((e) -> {
//                Item timedItemN =TimerStack2.getTimedItemN();
                if (timedItemN == null) {
//                    createNewItem.actionPerformed(null);
                    Item newItem = new Item(true, MSG_NEW_TASK_DEFAULT_TEXT, timerSourceN, Inbox.getInstance());
                    newItem.setRemainingForTaskItself(remainingEffort.getDuration(), true); //true:auto-update other fields
                    TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true); //will save both new timerInstance and new item     
//                    TimerStack2.getInstance().fireChangedEvent();
//                    DAO.getInstance().triggerParseUpdate();//                    TimerStack2.getTimedItemN().setRemainingForTaskItself(remainingEffort.getDuration(), false); //saved immediately on edit
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
                } else {
//                    TimerStack2.getTimedItemN().setRemainingForTaskItself(remainingEffort.getDuration(), false); //saved immediately on edit
                    timedItemN.setRemainingForTaskItself(remainingEffort.getDuration(), false); //saved immediately on edit
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
                    DAO.getInstance().saveToParseLater(timedItemN);
                    TimerStack2.getInstance().fireChangedEvent();
                    DAO.getInstance().triggerParseUpdate();
                }
            });

            final Container estimateContainer = new Container(new GridLayout(3));
            Button showEffortDetailsButton = new Button("", MyPrefs.timerShowEffortEstimateDetails.getBoolean() ? Icons.iconShowUpChevron : Icons.iconShowDownChevron, "ShowEffortDetailsButton");
            showEffortDetailsButton.addActionListener((e) -> {
                MyPrefs.flipBoolean(MyPrefs.timerShowEffortEstimateDetails);
                showEffortDetailsButton.setMaterialIcon(MyPrefs.timerShowEffortEstimateDetails.getBoolean() ? Icons.iconShowUpChevron : Icons.iconShowDownChevron);
                estimateContainer.setHidden(!MyPrefs.timerShowEffortEstimateDetails.getBoolean());
                estimateContainer.getParent().getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
            });
            estimateContainer.add(estimate).add(totalActualEffort).add(remainingEffort);  //!!: reuse same strings as from ScreenItem!
            estimateContainer.setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowEffortEstimateDetails)); //hide initially
            timeContainer.add(BorderLayout.centerAbsoluteEastWest(timerTimeContainer, timerStartStopButton, showEffortDetailsButton));
            //TODO make the effort Pickers small (size as the time, not as the cell) and centered (and center the labels above again)
            timeContainer.add(BorderLayout.center(estimateContainer));

            //LIST/PROJECT HIERARCHY
            String hierarchyStr;
            if (timerSourceN != null && timedItemN != null
                    && (hierarchyStr = timedItemN.getOwnerHierarchyAsString(timerSourceN)) != null
                    && !hierarchyStr.isEmpty()) {
                SpanLabel pickTimerSource = new SpanLabel();
                List<ItemAndListCommonInterface> locallyEditedSource = new ArrayList(Arrays.asList(timerSourceN));
                Button editSourceButton = new Button(CommandTracked.createMaterial("", Icons.iconEdit, ev -> {//         ScreenObjectPicker2.makePickPrjLstCat("Select Timer source" , true, itemList, pickTimerSource.getComponentForm(), () -> {
                    //                    ScreenObjectPicker2.makePickPrjLstCat("Timer select", true, locallyEditedOwner, (MyForm) pickTimerSource.getComponentForm(), () -> {
                    ScreenObjectPicker2.makePickPrjLstCat(MSG_SELECT_SOURCE_SCREEN_TITLE, true, locallyEditedSource, (MyForm) pickTimerSource.getComponentForm(), () -> {
                        if (locallyEditedSource.size() > 0) { //if >0, first element cannot be null!
                            ItemAndListCommonInterface selectedTimerSource = locallyEditedSource.get(0); //even if multiple should be selected (shouldn't be possible), only use first
                            if (!Objects.equals(selectedTimerSource, timerInstanceN.getTimerSourceN())) {
                                //if a task was already being timed, stop timer if needed and save time
//                                TimerStack2.getInstance().getTimerInstanceN().setTimerSourceN(selectedTimerSource);
                                timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save currently timed item (if any) but not timerInstance
//                                timerInstanceN.setTimerSource(selectedTimerSource, true, true);
                                timerInstanceN.setTimerSource(selectedTimerSource, true); //true:update timedItem
                                if (timerInstanceN.isAutoStartTimer()) {
                                    timerInstanceN.startTimer(true, true);
                                }
                                TimerStack2.getInstance().fireChangedEvent();
                                DAO.getInstance().saveToParseLater(timerInstanceN);
                                DAO.getInstance().triggerParseUpdate();
                            }
                        }
                    }, false).show();
                }));

//            pickTimerSource.setText(Item.getOwnerHierarchyAsString(timerSource.getLeafTaskPath(timedItem, true, null), false));
//                pickTimerSource.setText(ItemAndListCommonInterface.getHierarchyAsStringN(timerSource));
                pickTimerSource.setText(MSG_SOURCE_LABEL + hierarchyStr);
//                pickTimerSource.setMaterialIcon(Icons.iconEdit);
//                pickTimerSource.setTextPosition(Component.RIGHT);
//                timeContainer.add(pickTimerSource);
                timeContainer.add(BorderLayout.west(pickTimerSource).add(BorderLayout.EAST, editSourceButton));
            } else { //no source defined
//                ASSERT.that(timerInstanceN == null);
                SpanLabel pickTimerSource = new SpanLabel();
                List<ItemAndListCommonInterface> locallyEditedSource = new ArrayList(); //empty list
                Button editSourceButton = new Button(CommandTracked.createMaterial("", Icons.iconEdit, ev -> {//         ScreenObjectPicker2.makePickPrjLstCat("Select Timer source" , true, itemList, pickTimerSource.getComponentForm(), () -> {
                    ScreenObjectPicker2.makePickPrjLstCat(MSG_SELECT_SOURCE_SCREEN_TITLE, true, locallyEditedSource, (MyForm) pickTimerSource.getComponentForm(), () -> {
                        if (locallyEditedSource.size() > 0) { //if >0, first element cannot be null!
                            ItemAndListCommonInterface selectedTimerSource = locallyEditedSource.get(0); //even if multiple should be selected (shouldn't be possible), only use first
                            TimerStack2.getInstance().startTimerAndSave(null, selectedTimerSource, myForm, false, false, false); //if a task was already being timed, stop timer if needed and save time
                            TimerStack2.getInstance().fireChangedEvent();
//                            DAO.getInstance().saveToParseLater(TimerStack2.getTimerInstanceN());
//                            DAO.getInstance().triggerParseUpdate();
                        }
                    }).show();
                }));
                pickTimerSource.setText(MSG_NO_SOURCE_SET);
                timeContainer.add(BorderLayout.west(pickTimerSource).add(BorderLayout.EAST, editSourceButton));
            }

            //TASK DESCRIPTION
//            timeContainer.add(BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton));
//            timeContainer.add(BorderLayout.west(BoxLayout.encloseXNoGrow(status, description)).add(BorderLayout.EAST, editItemButton));
            Container swipe1 = BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton);
//            swipe1.setUIID("ContainerOpaque");
            timeContainer.add(swipe1);
            timeContainer.setUIID("BigTimerTaskContainer");
            //TASK COMMENT
            MyTextField comment = new MyTextField(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
            comment.setUIID("BigTimerComment");
            comment.putClientProperty("iosHideToolbar", Boolean.TRUE); //hide toolbar and only show Done button for ios virtual keyboard -> AVOID making Picker pop up?!
            comment.setSingleLineTextArea(false);
            comment.addActionListener((e) -> {
//                if (TimerStack2.getTimedItemN() == null) {
                if (timedItemN == null) {
//                    createNewItem.actionPerformed(null);
                    Item newItem = new Item(true);
                    newItem.setComment(comment.getText());
                    TimerStack2.getInstance().startTimerAndSave(newItem, null, myForm, false, true, true); //will save both new timerInstance and new item     
//                    TimerStack2.getTimedItemN().setComment(comment.getText());
//                    DAO.getInstance().saveToParseNow(timedItemN);
                } else {
//                    TimerStack2.getTimedItemN().setComment(comment.getText());
                    timedItemN.setComment(comment.getText());
//                    DAO.getInstance().saveToParseNow(TimerStack2.getTimedItemN());
                    TimerStack2.getInstance().fireChangedEvent();
//                    DAO.getInstance().saveToParseLater(TimerStack2.getTimedItemN());
                    DAO.getInstance().saveToParseLater(timedItemN);
                    DAO.getInstance().triggerParseUpdate();
                }
            });
            if (timedItemN != null) {
                MyForm.initField(Item.PARSE_COMMENT, comment,
                        () -> timedItemN.getComment(),
                        (t) -> timedItemN.setComment((String) t),
                        //                    () -> comment.getText(), (t) -> comment.setText((String) t), null, parseIdMap2
                        () -> comment.getText(),
                        (t) -> comment.setText((String) t), null, null, null, null, null //parseIdMap2=null, since everything is saved to ParseServer on edit, so no point in saving on exit as well
                );
            }
            Container commentContainer = ScreenItem2.makeCommentContainer(comment);
//            timerContainer.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes
//            timerBigTimerTopLevelContainer.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes
            timeContainer.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes

            //Action buttons
            //Show interrupted tasks
            int textPos = Button.RIGHT; //BOTTOM;

            //add listeners
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                timedItemN.addActionListener((e) -> {
//                    Object actionSource = e.getSource();
//                    int keyEvent = e.getKeyEvent(); //use the keyEvent to carry the actual event
//
//                    if (actionSource == TimerStack2.getInstance()) {
//                        //rebuild the UI with potentially a new source/item
//
//                    } else if (actionSource == timedItemN) {
//                        //refresh the Item info
//                        status.setStatus(timedItemN.getStatus());
//                        description.setText(description.getText());
//                        comment.setText(timedItemN.getText());
//                        estimate.setDuration(timedItemN.getEstimateTotal());
//                        remainingEffort.setDuration(timedItemN.getRemainingTotal());
//                    }
//                    if (actionSource == timerInstanceN.getTimerSourceN()) {
//                        if (keyEvent == ItemList.ACTION_EVENT_CHANGED) {
//                        } else if (keyEvent == ItemList.ACTION_EVENT_REMOVED) {
//
//                        }
//                    }
//                });
//            }
//</editor-fold>
            //EXIT button
            Button exitAndStopTimerButton = new Button(CommandTracked.create(MSG_STOP_TIMER, Icons.iconTimerStopTimer,
                    (e) -> {
                        TimerStack2.getInstance().stopCurrentTimerAndGotoNext();
                        TimerStack2.getInstance().fireChangedEvent();
                        DAO.getInstance().triggerParseUpdate(); //trigger save of all changes after user interaction
                    },
                    "TimerCmdSaveAndExitTimerScreen")); //"Exit"/"Kill"/"Quit"/"Exit"/"Close Timer"/"Stop Timer"/"Quit Timer"/"Exit Timer"
            exitAndStopTimerButton.setTextPosition(textPos);
//            exitAndStopTimerButton.setUIID("BigTimerExit");

            //set WAITING button
            Button setWaitingButton = null;
            if (timedItemN != null && !timedItemN.isDone() && !timedItemN.isWaiting()) {
//                setWaitingButton = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"), 
                setWaitingButton = new Button(CommandTracked.create(MSG_SET_WAITING, Icons.iconItemStatusWaiting, (e) -> { //"Wait" / "->Waiting"
//                    timedItem.setStatus(ItemStatus.WAITING, true, true, true, true);
                    timedItemN.setStatus(ItemStatus.WAITING);
//                    DAO.getInstance().saveToParseNow(timedItemN);
                    timedItemN.fireChangeEvent();
                    DAO.getInstance().triggerParseUpdate(); //trigger save of all changes after user interaction
                }, "TimerCmdSetTaskWaitingAndGotoNextTaskOrExit")); //"Wait"), 
                setWaitingButton.setTextPosition(textPos);
            }

            //NEXT task button
//            Item nextComingItem = TimerStack2.getNextComingTimerItemN();
            Item nextComingItem = TimerStack2.getInstance().getNextComingItemN();

//            SpanButton gotoNextTaskButtonWithItemText = null;
            SpanLabel nextTaskLabel = null;
            if (nextComingItem != null && MyPrefs.timerShowNextTask.getBoolean()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                gotoNextTaskButtonWithItemText = new SpanButton();
////            if (false) {
////                gotoNextTaskButtonWithItemText.setCommand(cmdStartNextTask); //UI: confusing to make this an active button when there is already NEXT?!
////            }
//                gotoNextTaskButtonWithItemText.setText("Next: \"" + nextComingItem.getText() + "\""
//                        + (MyPrefs.timerShowRemainingForNextTask.getBoolean() && nextComingItem.getRemainingForTaskItself() > 0
//                        ? (" [" + MyDate.formatDurationShort(nextComingItem.getRemainingForTaskItself()) + "]") : ""));
//                if (true || MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                    gotoNextTaskButtonWithItemText.setUIID("BigTimerNextItemWithText");
//                }
//</editor-fold>
//                gotoNextTaskButtonWithItemText = new SpanButton(MSG_NEXT_TASK_LABEL + "\"" + nextComingItem.getText() + "\""
                nextTaskLabel = new SpanLabel(MSG_NEXT_TASK_LABEL + "\"" + nextComingItem.getText() + "\""
                        + (MyPrefs.timerShowRemainingForNextTask.getBoolean() && nextComingItem.getRemainingForTaskItself() > 0
                        ? (" [" + MyDate.formatDurationShort(nextComingItem.getRemainingForTaskItself()) + "]") : ""), "BigTimerNextItemWithText");
            }

            Button nextComingButton = null;
            if (nextComingItem != null) {
                nextComingButton = new Button(CommandTracked.create(MSG_NEXT_TASK, Icons.iconTimerNextTask, (e) -> { //"Stop", "Next", "Start next", "Next task", "Start next task" "Go to next" "Go to next task"
//                    stopUITimers.actionPerformed(null);
                    TimerStack2.getInstance().goToNextTimedItem();
                    TimerStack2.getInstance().fireChangedEvent();
                    DAO.getInstance().triggerParseUpdate(); //trigger save of all changes after user interaction
                }, "TimerCmdStopTimerAndGotoNextTaskOrExit")); //"Stop", "Next", 
                nextComingButton.setTextPosition(textPos);
            }

            ArrayList<Button> buttons = new ArrayList<Button>();
            for (Button button : Arrays.asList(exitAndStopTimerButton, setWaitingButton, nextComingButton)) {
                if (button != null) {
                    buttons.add(button);
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (timerInstanceN != null) {
//                Container autoStartNextCont = new Container(new GridLayout(2));
//                autoStartNextCont.add(ScreenSettingsCommon.makeEditBooleanSetting(null, MyPrefs.timerAutomaticallyStartTimer,
//                        ()
//                        -> timerInstanceN.setAutoStartTimer(MyPrefs.timerAutomaticallyStartTimer.getBoolean()),
//                        "Auto-Start"));
//                autoStartNextCont.add(ScreenSettingsCommon.makeEditBooleanSetting(null, MyPrefs.timerAutomaticallyGotoNextTask,
//                        ()
//                        -> timerInstanceN.setAutoGotoNextTask(MyPrefs.timerAutomaticallyGotoNextTask.getBoolean()),
//                        "Auto-next"));
////            timerButtonsContainer.add(GridLayout.encloseIn(2,
////                    MyBorderLayout.centerEastWest(null, new Switch(), new SpanLabel("Auto-next")),
////                    MyBorderLayout.centerEastWest(null, new Switch(), new SpanLabel("Auto-start"))
////            ));
//                timerButtonsContainer.add(autoStartNextCont);
//            }
//</editor-fold>
            //AUTO-NEXT/START SWITCHES & ADD TASKS BUTTONS
            Container addNewTasksCont = new Container(new GridLayout(2));
            if (addNewTask != null && addNewSubtask != null) {
                addNewTasksCont.add(addNewTask).add(addNewSubtask);
//                timerButtonsContainer.add(addNewTasksCont);
            }

            if (timerInstanceN != null) {
                addNewTasksCont.setHidden(timerInstanceN.isAutoGotoNextTask());
                Container autoOptionsCont = new Container(new GridLayout(2));

                autoOptionsCont.add(ScreenSettingsCommon.makeEditBoolean(null, MSG_AUTO_START_BUTTON,
                        () -> timerInstanceN.isAutoStartTimer(), (b) -> timerInstanceN.setAutoStartTimer(b), () -> DAO.getInstance().saveToParseNow(timerInstanceN), MSG_AUTO_START_BUTTON_HELP));
//                autoOptionsCont.add(ScreenSettingsCommon.makeEditBoolean(MSG_AUTO_NEXT_BUTTON, MSG_AUTO_NEXT_BUTTON_HELP, () -> timerInstanceN.isAutoGotoNextTask(), (b) -> timerInstanceN.setAutoGotoNextTask(b)));

                autoOptionsCont.add(ScreenSettingsCommon.makeEditBoolean(null, MSG_AUTO_NEXT_BUTTON,
                        () -> timerInstanceN.isAutoGotoNextTask(), (b) -> timerInstanceN.setAutoGotoNextTask(b),
                        () -> { //when auto-next is on, hide the buttons
                            addNewTasksCont.setHidden(true);
                            addNewTasksCont.getComponentForm().animateLayout(ANIMATION_TIME_DEFAULT);
                            DAO.getInstance().saveToParseNow(timerInstanceN);
                        },
                        () -> {
                            addNewTasksCont.setHidden(false);
                            addNewTasksCont.getComponentForm().animateLayout(ANIMATION_TIME_DEFAULT);
                            DAO.getInstance().saveToParseNow(timerInstanceN);
                        },
                        MSG_AUTO_NEXT_BUTTON_HELP));
//            timerButtonsContainer.add(GridLayout.encloseIn(2,
//                    MyBorderLayout.centerEastWest(null, new Switch(), new SpanLabel("Auto-next")),
//                    MyBorderLayout.centerEastWest(null, new Switch(), new SpanLabel("Auto-start"))
//            ));
                timerButtonsContainer.add(autoOptionsCont);
                timerButtonsContainer.add(addNewTasksCont);
            }

            //TIMER BUTTONS
            timerButtonsContainer.add(GridLayout.encloseIn(buttons.size(), buttons.toArray(new Button[buttons.size()])));

            //COMPLETED BUTTON
            Button completedButton = null;
            if (timedItemN != null && !timedItemN.isDone()) {
                completedButton = new Button(CommandTracked.create("Completed", Icons.iconItemStatusDone,
                        (e) -> {
                            timedItemN.setStatus(ItemStatus.DONE);
                            timedItemN.fireChangeEvent(); //prefer fireChangeEvent to DAO.save to avoid a double save (status chg often impacts timer which triggers a save of item)
                            DAO.getInstance().triggerParseUpdate(); //trigger save of all changes after user interaction
                        },
                        "TimerCmdCompletedAndGotoNextTaskOrExit"));
                completedButton.setUIID("BigTimerCompleted");
                completedButton.setTextPosition(textPos);
                timerButtonsContainer.add(GridLayout.encloseIn(1, completedButton));
            }
            //SHOW NEXTCOMING TASK
            if (nextTaskLabel != null) {
                timerButtonsContainer.add(GridLayout.encloseIn(1, nextTaskLabel));
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//            ActionListener refreshFullScreenTimer = (e) -> {
////                TimerStack2.getInstance().removeActionListener(refreshFullScreenTimer);
//                timerContainer.removeAll(); //clear timerContainer
//                buildContentPaneN(timerContainer, myForm, fullScreenTimer); //rebuild timerContainer
//            };
//            TimerStack2.getInstance().setActionListener(refreshFullScreenTimer);
//            TimerStack2.getInstance().setActionListener((e) -> {
//                timerContainer.removeAll(); //clear timerContainer
//                buildContentPaneN(timerContainer, myForm, fullScreenTimer); //rebuild timerContainer
//            });
//</editor-fold>
        }
        if (false) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            TimerStack2.getInstance().setActionListener((evt) -> {
//                Object actionSource = evt.getSource();
//                int keyEvent = evt.getKeyEvent(); //use the keyEvent to carry the actual event
//
//                if (actionSource == TimerStack2.getInstance()) {
//                    //rebuild the UI with potentially a new source/item
//                    if (keyEvent == TimerStack2.ACTION_EVENT_CHANGED) {
//                        if (TimerStack2.getTimedItemN() == null) { //nothing more to time
//                            if (fullScreenTimer) {
//                                ((MyForm) timerContainer.getComponentForm()).showPreviousScreen(true);
//                            } else {
////                            if(myForm.smallTimer==timerContainer)
////                                myForm.smallTimer=null;
//                                if (myForm.getSmallTimerContainer() == timerContainer) {
//                                    myForm.setSmallTimerContainer(null);
//                                }
//                                timerContainer.remove();
////                            myForm.animateHierarchy(TOP);
//                                myForm.animateMyForm();
//                                myForm.animateHierarchy(ANIMATION_TIME_DEFAULT);
//                            }
//                        } else { //refresh timer
//                            if (fullScreenTimer) {
//                                buildContentPane(myForm.getContentPane(), myForm, fullScreenTimer);
////                            myForm.animateMyForm();
//                                myForm.animateHierarchy(ANIMATION_TIME_DEFAULT);
//                            } else {
//                                buildContentPane(timerContainer, myForm, fullScreenTimer); //refresh small timer
////                            myForm.animateMyForm();
//                                myForm.animateHierarchy(ANIMATION_TIME_DEFAULT);
//                            }
//                        }
//                    } else {
//                        ASSERT.that("Unexpected action keyEvent=" + keyEvent);
//                    }
//                } else {
//                    ASSERT.that("Unexpected actionSource=" + actionSource);
//                }
//            });
//</editor-fold>
        } else if (true) {
            TimerStack2.getInstance().setActionListener((evt) -> {
                Object actionSource = evt.getSource();
                int keyEvent = evt.getKeyEvent(); //use the keyEvent to carry the actual event
                MyForm currentForm = myForm;//(MyForm) timerContainer.getComponentForm();
                ASSERT.that(currentForm != null, "currentForm shouldn't be null<");
                if (Config.TEST) {
                    Log.p("currentForm=" + currentForm);
                }
                if (actionSource == TimerStack2.getInstance()) {
                    //rebuild the UI with potentially a new source/item
                    if (keyEvent == TimerStack2.ACTION_EVENT_CHANGED) {
                        if (true) {
//                            if (TimerStack2.getTimedItemN() == null && currentForm instanceof ScreenTimer7) { //nothing more to time, exit bigTimer
                            if (TimerStack2.getInstance().getCurrentlyTimedItemN() == null && currentForm instanceof ScreenTimer7) { //nothing more to time, exit bigTimer
                                currentForm.showPreviousScreen(true);
                            } else {
                                currentForm.refreshAfterEdit();
                            }
                        } else {
//                        if (TimerStack2.getTimedItemN() == null) { //nothing more to time
//                            if (TimerStack2.getTimedItemN() == null) { //nothing more to time
                            if (TimerStack2.getInstance().getCurrentlyTimedItemN() == null) { //nothing more to time
                                if (currentForm instanceof ScreenTimer7) {
                                    if (false && ((ScreenTimer7) currentForm).parentForm != null) { //false=not necessary since now refreshAfterEdit will remove small timer if needed
                                        ((ScreenTimer7) currentForm).parentForm.removeSmallTimerContainer();
                                    }
                                    currentForm.showPreviousScreen(true);
                                } else {
                                    if (false) {
                                        currentForm.removeSmallTimerContainer();
                                    }
                                    currentForm.refreshAfterEdit();
                                }
                            } else { //refresh timer
                                if (currentForm instanceof ScreenTimer7) {
                                    currentForm.refreshAfterEdit(); //refresh big timer
                                } else {
                                    buildContentPane(currentForm.getAndMakeSmallTimerContN(), currentForm, false); //refresh small timer
                                    currentForm.refreshAfterEdit();
                                }
                            }
                        }
                    } else {
                        ASSERT.that("Unexpected action keyEvent=" + keyEvent);
                    }
                } else {
                    ASSERT.that("Unexpected actionSource=" + actionSource);
                }
            });
        }
//        TimerStack2.getInstance().setActionListener((e) -> {
//            timerContainer.removeAll(); //clear timerContainer
//            buildContentPaneN(timerContainer, myForm, fullScreenTimer); //rebuild timerContainer
//        });
    }

//    @Override
//    public void actionPerformed(ActionEvent evt) {
//        if (TimerStack2.getInstance().isTimerStackEmpty()) { //remove smallTimer or exit TimerScreen
//        } else {
//            refreshOrShowUIOnTimerChange();
//        }
//    }
}
