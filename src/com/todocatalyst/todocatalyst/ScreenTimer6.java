package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.io.Log;
//import com.codename1.io.Preferences;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.l10n.SimpleDateFormat;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.Toolbar;
import com.codename1.ui.animations.FlipTransition;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.UITimer;
//import com.todocatalyst.todocatalyst.MyDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static com.todocatalyst.todocatalyst.MyForm.dialogSetWaitingDateAndAlarm;
import static com.todocatalyst.todocatalyst.MyForm.dialogUpdateRemainingTime;
import java.util.Map;
//import java.util.function.*;

/**
 * Shows a timer. UI:
 *
 * @author Thomas
 */
public class ScreenTimer6 extends MyForm {

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
    final static String SCREEN_TITLE = "Timer";
    final static String TIMER_REPLAY = "StartTimer-";

    private TimerStack timerStack;//= new TimerStack();
//    private TimerStackEntry entry;

    private UITimer timer;
    private UITimer buzzerTimer;
//    private UITimer saveElapsedTimeTimer;

    private final static int INTERVAL_UPDATE_TIMER_MILLIS = 1000;
    private final static int BUZZER_DURATION = 100; //300 milliseconds

    /**
     * used to keep track of when to save a backup copy of elapsed to local
     * storage
     */
    private boolean forceTimerStartOnLeafTasksWithAnyStatus; //tested in isSuitableItemForTimerFct, if true, an item will be timed no matter what its Status
    private Item.Condition isSuitableItemForTimerFct = item -> isSuitableItemForTimer(item); //TODO simplify: replace condition by it lamda function

    //UI elements
    private Button timerStartStopButton;
    SpanButton gotoNextTaskButtonWithItemText;
//    private boolean showSeconds; //
    private Label elapsedTimeButton;
    private Picker elapsedTimePicker;

//    private MyTextField description;
    private MyTextArea description;
    private MyCheckBox status;
//    private Button status;
    private MyTextArea comment;
//    private Container commentCont;
    private MyDurationPicker remainingEffort;
    private MyDurationPicker effortEstimate;
    private Label totalActualEffort;
    private Button editItemButton;

    TimerStackEntry currEntry;
//    TimerInstance timerInstance = null;

    SimpleDateFormat myFormatter = new SimpleDateFormat() {
        @Override
        public String formatXXX(Object source) {
//                return MyDate.formatTime(((Date) source).getTime(), showSeconds) + "X"; //To change body of generated methods, choose Tools | Templates.
            //if (source instanceof Integer)
//            return MyDate.formatTime(((Integer) source).longValue(), MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer)) + "X"; //To change body of generated methods, choose Tools | Templates.
//            return MyDate.formatTime(((Long) source).longValue(), 
//            return MyDate.formatTime(((Long) source).longValue(),
            if (source instanceof Integer) {
                return MyDate.formatTimeDuration((Integer) source, MyPrefs.timerShowSecondsInTimer.getBoolean()); //To change body of generated methods, choose Tools | Templates.
//                return MyDate.formatTime((Integer) source, MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer), false, false, true); //To change body of generated methods, choose Tools | Templates.
            } else if (source instanceof Long) {
                return MyDate.formatTimeDuration((Long) source, MyPrefs.timerShowSecondsInTimer.getBoolean()); //To change body of generated methods, choose Tools | Templates.
            } else {
                return "ERROR";
            }
        }
    };

//    private CheckBox startTimerAutomaticallyForNextTask; //should be unique for the Timer
//    private Command continueCommand; //make global to be able to test return value; 
    ScreenTimer6(MyForm previousScreen, TimerInstance timerInstance, SaveEditedValuesLocally previousValues,  Map<Object, UpdateField> parseIdMap2) {
        super(SCREEN_TITLE, null, () -> {
        });
        setLayout(new BorderLayout());
//        setScrollable(false);

        //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setScrollable(true); //since the size of the timer may overflow

//        initLocalSaveOfEditedValues();
        addCommandsToToolbar(getToolbar()); //no commands depend on the task or itemList
        //updateActionOnDone cannot be used in Timer due to interrupt tasks and since they cannot be pushed onto the stack
        this.updateActionOnDone = () -> {
//            DAO.getInstance().save(getTimedItemXXX());
//            DAO.getInstance().save(getEntry().timedItem);
//            setTimedItemXXX(null); //remove previous item when exiting the timer
        };
//        boolean valuesRestored = restoreLocallyEditedValuesOnAppExit();
    }

    @Override
    public void refreshAfterEdit() {
//        getContentPane().removeAll(); //clear existing contentPane
//        buildContentPane(getTimedItem(), itemList, getContentPane()); //rebuild for new values of item etc
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        buildContentPane(); //also removes previous content of contentPane
        super.refreshAfterEdit();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private static ScreenTimer6 INSTANCE;
//
//    public static ScreenTimer6 getInstance() {
//        if (INSTANCE == null) {
//            INSTANCE = new ScreenTimer6();
//        }
//        return INSTANCE;
//    }
//</editor-fold>
    // ************************** START TIMER ************************
    //
    static void showPreviousScreenOrDefault(MyForm previousForm, boolean callRefreshAfterEdit) {
        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
        }
//        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refresh whenever returning from Timer since tasks may have been closed
        MyForm.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refresh whenever returning from Timer since tasks may have been closed
    }

    private void showNoMoreTasksDialogWhenRelevant(Item item, ItemList itemList) {
        if ((item != null && item.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = item != null ? item.getText() : itemList.getText();
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("´Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
        }
    }

    private void launchTimerImpl(Item item, ItemList orgItemList, MyForm previousForm, boolean interruptOrInstantTask, boolean forceTimerStartOnLeafTasksWithAnyStatus) {

//        if (ScreenTimer6.isTimerActive() && ScreenTimer6.relaunchTimerOnAppRestart()) {
        if (isTimerActive() && relaunchTimerOnAppRestart()) {

        } else {

            assert item != null || orgItemList != null : "either item or itemList must be defined";
            this.forceTimerStartOnLeafTasksWithAnyStatus = forceTimerStartOnLeafTasksWithAnyStatus;
//            if (previousForm != ScreenTimer6.this) {
            if (previousForm != this) {
                //UI: always store last form to return to when using BACK button (except when Timer launched from Timer, e.g. for interrupts)
                this.previousForm = previousForm; //if Interrupt comes from Timer, do not save the ScreenTimer as previous form, but keep whatever previous form the user come from when first starting the timer
            }

            //if Timer is launched on a new Item/ItemList while aleready running, simply ignore the Item/ItemList and show existing timer
            if (currEntry != null && currEntry.timedItem != null && !interruptOrInstantTask) { //UI: if Timer launched on new item while running, show timer on existing item 
                //TODO Imp: popup "you want to stop running timer on XX and start on YY?"
//        if (timerAlreadyActive()) {
                show();
                return;
            }
            if (interruptOrInstantTask
                    && currEntry != null && currEntry.timedItem != null) { //Real INTERRUPT of already time entry (if timedItem==null then just an 'instant task'
                //if timer is already active, and new item is an intertupt
                boolean timerRunning = currEntry.isTimerRunningNow();
                //stop previous timer if needed 
                stopTimer(); //stop timer before pushing
                currEntry.setWasTimerRunningWhenInterrupted(timerRunning); //DONE review/simplify logic for entry.running (here it's stored to push the right running value onto the stack)

                item.setInteruptOrInstantTask(true); //UI: only set interruptTask=true when a timed task was actually interrupted
                //UI: don't store interrupted task when it was itself an interrupt task (also because an interupt is not stored in Parse so creates error when existing the interrupting interrupt task)
                if (!currEntry.timedItem.isInteruptOrInstantTask()) {
                    item.setTaskInterrupted(currEntry.timedItem); //store which Item this interrupt interrupted (or nothing if null - won't happen since then its simply an instant-task)
                }
                putEditedValues2(parseIdMap2, currEntry.timedItem);
                DAO.getInstance().save(currEntry.timedItem); //UI: interrupted tasks are saved (in case app is stopped during interrupt, to avoid loosing edited values) so after an interrupt, Cancel doesn't cancel any edits done before the interrupt
                assert orgItemList == null : "itemList should always be null for an interrupt";
            } //else { //new Item or ItemList
            //interrupt may be true here, but if no task interrupted, it is just considered an instant-task
            pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, orgItemList, interruptOrInstantTask, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem 
            if (currEntry.timedItem == null) {
                //if no Item given directly, or no suitable sub-tasks, nor any suitable Item found in ItemList, let the user know and exit (stay in previous Form)
//            showNoMoreTasksDialogWhenRelevant(currEntry.sourceItemOrProject, currEntry.orgItemList);
                showNoMoreTasksDialogWhenRelevant(currEntry.sourceItemOrProject, currEntry.itemList);
                popCurrentTimerEntry(); //remove the just added entry
            } else {
                //timedItem now set to correct Item, so launch timer on timedItem
                buildContentPane();
//            boolean valuesRestored = restoreLocallyEditedValuesOnAppExit(); //restore any previously saved values
                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)) { //launch *after* building contentPane
                    reStartTimer();
                }
                show();
//            }
            }
        }
    }

    /**
     * starts an interrupt timer for item. If timer is already running, the
     * previous item is pushed and will continue afterwards, otherwise the timer
     * will be started normally on item (not as an interrupt)
     *
     * @param item
     * @param previousForm
     * @param doneAction
     */
    public void startInterrupt(Item item, MyForm previousForm) {
        //stop and push previously timed item+context
//        startInterrupt(item, previousForm, true, true);
//        launchTimerImpl(item, null, null, previousForm, true);
        launchTimerImpl(item, null, previousForm, true, false);
    }

    public void startTimerOnItem(Item item, MyForm previousForm, boolean forceTimerStartOnLeafTasksWithAnyStatus) {
        //if timer already running, update time for (already stored) Item
        launchTimerImpl(item, null, previousForm, false, forceTimerStartOnLeafTasksWithAnyStatus);
    }
//

//    public void startTimerOnItemList(ItemList orgItemList, FilterSortDef filter, MyForm previousForm) {
    public void startTimerOnItemList(ItemList orgItemList, MyForm previousForm) {
        //TODO!!! remove filter from arguments and get it directly from orgItemList
        //if timer already running, update time for (already stored) Item
//        launchTimerImpl(null, orgItemList, filter, previousForm, false);
        launchTimerImpl(null, orgItemList, previousForm, false, false);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus) {
//        processCurrentItemAndLaunchNextOrExit(newItemStatus, false, false, false, false);
//    }
//
//    /**
//     *
//     * @param newItemStatus
//     * @param forceExitAfterUpdatingItem
//     */
//    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem) {
//        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, false);
//    }
//
//    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask) {
//        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, cancelCurrentTask, false);
//    }
//
//    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask, boolean manuallySelectedGotoNextTask) {
//        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, cancelCurrentTask, manuallySelectedGotoNextTask, false);
//    }
//</editor-fold>
    boolean timerIsUpdatingItemStatus;

    /**
     *
     * @param newItemStatus
     * @param forceExitAfterUpdatingItem indicates that the Timer should exit
     * after updating the currently timed item
     * @param cancelCurrentTask
     * @param manuallySelectedGotoNextTask
     * @param timerBeingStoppedRemotely Timer is being stopped by anohter means
     * than Timer UI, e.g. stopped because the timed tasks is marked
     * Done/Cancelled or Deleted (//TODO!!!! will this work?)
     */
    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask,
            boolean manuallySelectedGotoNextTask, boolean timerBeingStoppedRemotely) {
//        TimerStackEntry entry = getEntry();
        assert !timerIsUpdatingItemStatus || timerBeingStoppedRemotely : "timerIsUpdatingItemStatus not reset back to false correctly";

        if (!timerIsUpdatingItemStatus && timerBeingStoppedRemotely) {
            stopTimer();
            putEditedValues2(parseIdMap2, currEntry.timedItem); //update any values edited on the Timer screen (includes handling of whether timer shows elapsed or total actual time)
            DAO.getInstance().save(currEntry.timedItem); //save //done in putEditedValues2 (NO, only initially for previously unsaved items)
            //NO MORE ITEMS in this timerStack entry (e.g. interrupt, single task or itemList with no more items
            popCurrentTimerEntry(); //when stopping timer remotely, always pop the current
            return;
        }

        timerIsUpdatingItemStatus = true;
        //if moving manually to next tasks is activated, then only store the status selected (user may press different buttons several times
        if (!MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) && !manuallySelectedGotoNextTask && !forceExitAfterUpdatingItem) {
            stopTimer();
            if (gotoNextTaskButtonWithItemText != null) {
                gotoNextTaskButtonWithItemText.setHidden(false); //show the button to go to next
            }
            if (newItemStatus != null) {
                parseIdMap2.put(status, () -> currEntry.timedItem.setStatus(newItemStatus)); //if newItemStatus is defined, then use that to override the value previously manually set using status checkbox //TODO a bit complicated UI:
                status.setStatusIcon(newItemStatus);
                status.repaint();
            }
            repaint(); //update for invisible button

        } else {

            stopTimer();
            //UPDATE/SAVE current task, move to next AND start the timer
            //set Status (NB. This many override a manually set value? - no, not if setting the state saves the task)
            if (cancelCurrentTask) {
                //when cancelling, no update of any edited values or elapsed time
                //if we cancel a temporary item (e.g an interrupt task) then we need to delete the version stored in Parse for backup
                if (currEntry.timedItemSavedLocallyInTimer && currEntry.timedItem.getObjectId() != null) {
                    DAO.getInstance().delete(currEntry.timedItem);
                }
            } else {
                //if NOT Cancelled, update the values and save
                if (newItemStatus != null) {
                    parseIdMap2.put(status, () -> currEntry.timedItem.setStatus(newItemStatus)); //if newItemStatus is defined, then use that to override the value previously manually set using status checkbox //TODO a bit complicated UI:
                }
                if (newItemStatus != ItemStatus.CANCELLED && newItemStatus != ItemStatus.DONE
                        && MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem.getBoolean()) { //don't ask when settng Done or cancelled
//                    dialogUpdateRemainingTime(currEntry.timedItem, parseIdMap2).show();
                    dialogUpdateRemainingTime(remainingEffort).show();
                }
                putEditedValues2(parseIdMap2, currEntry.timedItem); //update any values edited on the Timer screen (includes handling of whether timer shows elapsed or total actual time)
                DAO.getInstance().save(currEntry.timedItem); //save //done in putEditedValues2 (NO, only initially for previously unsaved items)
            }

            assert size() <= 1 || currEntry.timedItem.isInteruptOrInstantTask() //                    : "size can only be bigger than one if last entry is an interrupt, size=" + size() + ", interrupt=" + currEntry.interruptOrInstantTask;
                    : "size can only be bigger than one if last entry is an interrupt, size=" + size() + ", interrupt=" + currEntry.timedItem.isInteruptOrInstantTask();

            if (forceExitAfterUpdatingItem) {
                popCurrentTimerEntry();
                timerIsUpdatingItemStatus = false;
                showPreviousScreenOrDefault(previousForm, true);
//                return; //don't exit here to ensure timerIsUpdatingItemStatus is set false when finishing the update
            } else {
                boolean finishedTaskWasAnInterrupt = currEntry.timedItem.isInteruptOrInstantTask();
                assert !finishedTaskWasAnInterrupt || currEntry.nextItem == null :
                        "normally nextItem should be null for an interrupt, nextItem=" + currEntry.nextItem.getText()
                        + ", entry.interrupt=" + currEntry.timedItem.isInteruptOrInstantTask();
                if (currEntry.nextItem == null) {
                    //NO MORE ITEMS in this timerStack entry (e.g. interrupt, single task or itemList with no more items
                    popCurrentTimerEntry();
                }

                if (timerEntryExists()) { //an entry already exists
                    if (finishedTaskWasAnInterrupt) {
                        assert !forceExitAfterUpdatingItem : "forcing an exit from an interrupt that interrupted a previously running task should not be possible";
                        buildContentPane(); //build timer content paner for new timedItem
                        if (currEntry.isWasTimerRunningWhenInterrupted()) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                            reStartTimer();
                        }
                        setTransitionOutAnimator(new FlipTransition(-1, 200)); //not working, YES, working on iPhone, 300 a bit slow, 
                        show();
                    } else { //else continue with interrupted task
                        currEntry.findNextTimerItem(isSuitableItemForTimerFct);
                        if (currEntry.timedItem != null) {
                            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) || manuallySelectedGotoNextTask) {
                                buildContentPane(); //build timer content paner for new timedItem
                                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                                    reStartTimer();
                                }
                                this.animateLayout(300);
                            }
                        } else {
                            showNoMoreTasksDialogWhenRelevant(currEntry.sourceItemOrProject, currEntry.itemList);
                            popCurrentTimerEntry();
                            timerIsUpdatingItemStatus = false;
                            showPreviousScreenOrDefault(previousForm, true);
                        }
                    }
                } else {
                    timerIsUpdatingItemStatus = false;
                    showPreviousScreenOrDefault(previousForm, true);
                }
            }
        }
        timerIsUpdatingItemStatus = false;
    }

//    updateCurrentTaskAndGotoNextTask
    public boolean relaunchTimerOnAppRestart() {
//        boolean timerRunningForPushedEntry = currEntry.wasTimerRunningWhenInterrupted;
//        if (timerRunningForPushedEntry) {

        assert timerStack != null : "timerStack not read correctly";
        if (timerStack != null && currEntry != null && currEntry.timedItem != null
                && (currEntry.itemList != null || currEntry.sourceItemOrProject != null)) {

            if (currEntry.itemList != null
                    && currEntry.itemListIndex > currEntry.itemList.size() - 1) {
                currEntry.itemListIndex = currEntry.itemList.size() - 1; //adjust index if list has shortened since (eg edited on another device)
            }
//        boolean timerRunningForPushedEntry = currEntry.isWasTimerRunningWhenInterrupted();
//        if (timerRunningForPushedEntry) { //Timer was stopped correctly on app Pause/Exit
            boolean timerRunningForPushedEntry = currEntry.isTimerRunningNow(); //check if Timer was running when app was exited

            buildContentPane();

//            boolean valuesRestored = restoreEditedValuesSavedLocallyOnAppExitXXX(); //restore any previously saved values
//            deleteEditedValuesSavedLocallyOnAppExit();
            if (timerRunningForPushedEntry) { //Timer was running when app was Paused/Exited
                currEntry.updateAndStoreElapseTimerDurationOnPause(); //force it to pause so timer can be re-started normally
//            currEntry.updateTimerStartTimeOnTimerStart();//force it to pause so timer can be re-started normally
                reStartTimer();
            }
//        if (timerRunningForPushedEntry) { //Timer was stopped correctly an app Pause/Exit
//            reStartTimer();
//        }
//        else { //in case Timer was brutally stopped so 
//            reStartTimer();
            show();
            return true;
        } else { //bad data in storage, delete it completely
//            ScreenTimer.deleteTimerInfoInStorage();
//            deleteTimerInfoInStorage();
        }
        return false;
    }

    /**
     * is used in app.start() to test if Timer was active when app was stopped
     *
     * @return
     */
    public boolean isTimerActive() {
//        return size() >= 1; //at least one item is saved/active 
        return timerEntryExists(); //at least one item is saved/active 
    }

    /**
     * stops the timer if it is currently running for this item. Tests if the
     * current item is being set to DONE/CANCELLED from within the timer to
     * update infinite loops if the timer is setting the item to Done Updates
     * the item actuals with the timer value. Does not start the timer on a next
     * item (like normally done when within the timer. Does everything normally
     * done when exiting the timer (eg pops the timer stack).
     *
     * DOES currently NOT store any other values edited in the timer What
     * happens next time the timer is called??
     */
    public void stopTimerIfRunningOnThisItem(Item item) { //boolean continueWithNextItem // 
        if (item == null) {
            return;
        }
        if (timerStack != null && currEntry != null && item.equals(currEntry.timedItem) && isTimerRunning()) {
            processCurrentItemAndLaunchNextOrExit(null, false, false, false, true);
        }
    }

    /**
     * returns true if the timer is currently running
     *
     * @return
     */
    boolean isTimerRunning() {
//        ASSERT.that(timer == null , "Normal timer should not be running if item is null");
//        ASSERT.that(buzzerTimer == null , "Buzzer timer should not be running if item is null");
        return timer != null; // || entry.running; //timerRunning should only be true if item!=null
    }

    /**
     * returns true if Timer max time (specifically Picker max time of 23:59) is
     * passed
     *
     * @param timerShowsActualTotal
     * @param timerValueMillis
     * @param itemActualEffort
     * @return
     */
    private boolean maxTimerTimePassed(boolean timerShowsActualTotal, long timerValueMillis, long itemActualEffort) {
        //TODO make max timer time a setting? Why? 
        long ABSOLUTE_MAX_TIMER_VALUE = MyDate.DAY_IN_MILLISECONDS - MyDate.MINUTE_IN_MILLISECONDS; //==23:59
        long MAX_TIMER_VALUE = Math.min(ABSOLUTE_MAX_TIMER_VALUE, MyPrefs.timerMaxTimerDurationInHoursXXX.getInt() * MyDate.HOUR_IN_MILISECONDS); //==23:59
//        return ((timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS)
//                            || (!timerShowsActualTotal && timerValueMillis + itemActualEffort + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS));
        return ((timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS >= ABSOLUTE_MAX_TIMER_VALUE)
                || (timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS - itemActualEffort >= MAX_TIMER_VALUE)
                || (!timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS >= MAX_TIMER_VALUE));
    }

    /**
     * returns true if the minimum Timer threshold for when to update status and
     * save time is passed
     *
     * @return
     */
    private boolean isMinimumThresholdPassed() {
        return isMinimumThresholdPassed(currEntry.getTimerDurationInMillis());
    }

    private boolean isMinimumThresholdPassed(long timerValue) {
//        return status.getStatus() != ItemStatus.CREATED //if status already other than created we store any time -NO, since this will also later create a timer Start/Stop recording
//                && currEntry.getTimerDurationInMillis() >= MyPrefs.getInt(MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActuals) * MyDate.SECOND_IN_MILLISECONDS;
        return timerValue >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
    }

    /**
     * called on regular updates of the timer screen, as well as on app relaunch
     */
    public void refreshDisplayedTimerInfo() {
//        setTaskStatusOngoingWhenMinimumThresholdPassed();
        if (status.getStatus() == ItemStatus.CREATED
                && currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//DON'T revert a cancelled task to ongoing just because time is spent on it
            status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
            parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> currEntry.timedItem.setStartedOnDate(System.currentTimeMillis()
                    - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
        }
        long timerValueMillis = currEntry.getTimerDurationInMillis();
        //UI: currently Picker time is limited to 23h59m so need to save time if that 
//        if (maxTimerTimePassed(currEntry.timerShowsActualTotal, timerValueMillis, currEntry.timedItem.getActualEffort(false))) {
        if (maxTimerTimePassed(currEntry.timerShowsActualTotal, timerValueMillis, currEntry.timedItem.getActualEffortProjectTaskItself())) {
            //TODO!!!! how to handle when timer reaches max value?? Stop at 23h59, show popup when max value reached. Alternatively, save elapsed time and start over (omplex). Best option: create own timer Picker with no maximum value (99h59m, or 99999d23h59m)
            stopTimer(); //if max value reached for Total, stop timer. Necessary to stop when total
        }
        elapsedTimeButton.setText(myFormatter.format(timerValueMillis));
//        elapsedTimePicker.animate(); //this is enough to update the value on the screen
        elapsedTimeButton.repaint(); //this is enough to update the value on the screen
//                    updateTotalActualEffort(currEntry.timedItem.getActualEffort(false), timerValueMillis);
        refreshTotalActualEffort();
//        totalActualEffort.animate();
//        totalActualEffort.repaint();
    }

    /**
     * //UI: when Timer has passed minimum threshold, task status is set to
     * Ongoing (only if Created <=> except if Waiting or Ongoing or Completed or
     * Cancelled)
     */
    private void setTaskStatusOngoingWhenMinimumThresholdPassed() {
//        if (isMinimumThresholdPassed() && status.getStatus() == ItemStatus.CREATED) {
        if (status.getStatus() == ItemStatus.CREATED
                && currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//DON'T revert a cancelled task to ongoing just because time is spent on it
            status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
            parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> currEntry.timedItem.setStartedOnDate(System.currentTimeMillis()
                    - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
        }
    }

    private void reStartTimer() {

    }

    private void reStartTimer(Container timerContainer) {
        assert timer == null && buzzerTimer == null : "all timers should be null whenever restarted, timer=" + timer + " buzzerTimer=" + buzzerTimer;
        elapsedTimeButton.setText(myFormatter.format(currEntry.getTimerDurationInMillis())); //update picker immediately when starting Timer
        elapsedTimeButton.repaint();//this is enough to update the value on the screen

        if (timer == null && !maxTimerTimePassed(currEntry.timerShowsActualTotal,
                currEntry.getTimerDurationInMillis(), currEntry.timedItem.getActualEffortProjectTaskItself())) {
            elapsedTimeButton.setEnabled(false); //disable while running
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
            setTaskStatusOngoingWhenMinimumThresholdPassed();
//            currEntry.updateTimerStartTimeOnTimerStart();
            timerInstance.startTimer();
            //create new timer and store (to cancel)
            (timer = new UITimer(new Runnable() {
                public void run() {
                    refreshDisplayedTimerInfo();

                }
            }
            )).schedule(MyPrefs.timerUpdateInterval.getInt() * MyDate.SECOND_IN_MILLISECONDS, true, timerContainer.getComponentForm()); //update every second. TODO change to every minute when timer>60s
            timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
            timerStartStopButton.repaint(); //this is enough to update the value on the screen

            //Start Buzzer
            if (MyPrefs.timerBuzzerInterval.getInt() != 0) {
                (buzzerTimer = new UITimer(new Runnable() {
                    public void run() {
                        Display.getInstance().vibrate(BUZZER_DURATION);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    try {
//                        wait(BUZZER_PAUSE);
//                        Util.wait(this, BUZZER_PAUSE);
//                    } catch (InterruptedException ex) {
//                        Log.e(ex);
//                    }
//                        Display.getInstance().vibrate(BUZZER_DURATION);
//</editor-fold>
                    }
                }
                )).schedule(MyPrefs.timerBuzzerInterval.getInt(), true, ScreenTimer6.this); //update every second. TODO change to every minute when timer>60s
            }
        }
    }

    private void stopTimer() {
        if (timer != null) {
            currEntry.updateAndStoreElapseTimerDurationOnPause(); //save elapsed time *before* stopping the timer
            timerInstance.stopTimer();
            timer.cancel(); //stop timer as first thing
            timer = null; //null <=> timer is not running
            timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
            elapsedTimeButton.setEnabled(true); //enable the picker when timer is paused
            timerStartStopButton.repaint(); //this is enough to update the value on the screen
        }
        if (buzzerTimer != null) {
            buzzerTimer.cancel();
            buzzerTimer = null;
        }
    }

    //****************** UI *********************
    //
    private void addCommandsToToolbar(Toolbar toolbar) {

//        toolbar.setBackCommand(showPreviousScreenCommand, Toolbar.BackCommandPolicy.ALWAYS); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
//        toolbar.setBackCommand(showPreviousScreenCommand); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
//        toolbar.setBackCommand(showPreviousScreenCommand); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
        toolbar.setBackCommand(makeDoneCommandWithNoUpdate()); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html

        //Create an interrupt task and start the timer on it
        toolbar.addCommandToRightBar(makeInterruptCommand());

        toolbar.addCommandToRightBar(MyReplayCommand.create("TimerSettings", null, Icons.iconSettingsLabelStyle, (e) -> {
            new ScreenSettingsTimer(ScreenTimer6.this, () -> {
                refreshAfterEdit();
            }).show();
        }
        ));
    }

    /**
     * returns true if this task is suitable to start the timer on (e.g. no Done
     * or Cancelled)
     *
     * @param item
     * @return
     */
    private boolean isSuitableItemForTimer(Item item) {
//        Item.ItemStatus itemStatus = item.getStatus();
//        return (itemStatus != Item.ItemStatus.STATUS_DONE && itemStatus != Item.ItemStatus.STATUS_CANCELLED);
        return (!item.isDone() && !item.isWaiting())
                || (item.isWaiting() && MyPrefs.getBoolean(MyPrefs.timerIncludeWaitingTasks))
                || (forceTimerStartOnLeafTasksWithAnyStatus && !item.isProject()); //UI: Timer is not picking done items, or waiting items until the WaitingTill date has been reached
    }

    private static long calcTotalEffortInMillis(long previousActualEffortMillis, long elapsedTimerTimeInMillis, boolean timerShowsTotalActual) {
        if (timerShowsTotalActual) {
            return (int) ((elapsedTimerTimeInMillis));
        } else {
//            return (item.getActualEffort(false) + elapsedTimerTimeInMillis) / MyDate.MINUTE_IN_MILLISECONDS;
            return (int) ((previousActualEffortMillis + elapsedTimerTimeInMillis));
        }
    }

    private void refreshTotalActualEffort() {
//        updateTotalActualEffortImpl(currEntry.timedItem.getActualEffortProjectTaskItself(), currEntry.getTimerDurationInMillis());
//    }
//
//    private void updateTotalActualEffortImpl(long previousActualEffortMillis, long timerElapsedTimeMillis) {
        long previousActualEffortMillis = currEntry.timedItem.getActualEffortProjectTaskItself();
        long timerElapsedTimeMillis = currEntry.getTimerDurationInMillis();
//<editor-fold defaultstate="collapsed" desc="comment">
//        totalActualEffort.setText(MyDate.formatTime(calcTotalEffortInMinutes(previousActualEffort, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), MyPrefs.getBoolean((MyPrefs.timerShowSecondsInTimer))));
//        totalActualEffort.setText(MyDate.formatTime(calcTotalEffortInMinutes(previousActualEffort, timerElapsedTime, MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), MyPrefs.getBoolean((MyPrefs.timerShowSecondsInTimer))));
//        totalActualEffort.setText(MyDate.formatTime(calcTotalEffortInMillis(previousActualEffortMillis, timerElapsedTimeMillis, MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), MyPrefs.getBoolean((MyPrefs.timerShowSecondsInTimer))));
//        totalActualEffort.setText(MyDate.formatTime(calcTotalEffortInMillis(previousActualEffortMillis, timerElapsedTimeMillis, MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), false)); //false=don't show seconds in Total
//</editor-fold>
        int totalEffort;
        if (MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))) {
            totalEffort = (int) ((timerElapsedTimeMillis));
        } else {
//            return (item.getActualEffort(false) + elapsedTimerTimeInMillis) / MyDate.MINUTE_IN_MILLISECONDS;
            totalEffort = (int) ((timerElapsedTimeMillis + previousActualEffortMillis));
        }
//       totalActualEffort.setText(myFormatter.format(calcTotalEffortInMillis(previousActualEffortMillis, timerElapsedTimeMillis, MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))))); //false=don't show seconds in Total
        totalActualEffort.setText(myFormatter.format(totalEffort)); //false=don't show seconds in Total
//        totalActualEffort.setText(MyDate.formatTime(calcTotalEffortInMillis(previousActualEffortMillis, timerElapsedTimeMillis, MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), false, false, true)); //false=don't show seconds in Total
        totalActualEffort.repaint();
    }

    private void refreshElapsedTimeDisplayed() {
//        return MyDate.formatTimeDuration((Integer) source, MyPrefs.timerShowSecondsInTimer.getBoolean()); 
        elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean(), true)); //true=show leading zero for hours
        elapsedTimeButton.repaint();
    }

    private static void updateCurrentTimedItem(ItemStatus newItemStatus) {
//        if (newItemStatus == null) {
//            newItemStatus = status.getStatus();
//        }
        Item currentItem = timerInstance.getTimedItem();
        timerInstance.stopTimer(true);
        if (newItemStatus != ItemStatus.CANCELLED && newItemStatus != ItemStatus.DONE
                && MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem.getBoolean()) { //don't ask when settng Done or cancelled
            dialogUpdateRemainingTime(remainingEffort).show();
        }
        putEditedValues2(); //update item with edited/changed values
        DAO.getInstance().save(currentItem);
    }

    private static void updateTimer(ItemStatus newItemStatus) {
        if (MyPrefs.timerAutomaticallyGotoNextTask.getBoolean()) {
//                    cmdStartNextTask.actionPerformed(null); //
            startNextTask(contentPane, fullScreenTimer);
        } else if (gotoNextTaskButtonWithItemText != null) { //if there's a next task button (and a next task), then show it
            status.setStatusIcon(newItemStatus);
            status.repaint();
            gotoNextTaskButtonWithItemText.setHidden(false); //show the button to go to next
            revalidate(); //update for invisible button
        } else {
            //TODO!!!! show popup; no more tasks to time
            //exit, show previous screen
            showPreviousScreenOrDefault(true);
        }
    }

    private void startNextTask(Container currentTimerContainer, boolean fullScreenTimer) {
        //approach: clean current container, updated & add all buttons, refresh 
        Item nextItem = timerInstance.findNext();
        if (nextItem != null) {
            buildContentPane(currentTimerContainer, fullScreenTimer);
            currentTimerContainer.repaint();
//                    new ScreenTimer6(nextItem.getText(), previousForm, timerInstance).show();
            currentTimerContainer.revalidate();
            if (timerInstance.isAutostart()) { //NOT needed, automatically started when building new timer pane

            }
        } else {
            TimerStack.getInstance().showNext();
        }
        startTimerRefresh();
        refreshTimerWindow();
    }

    /**
     * build the user interface of the Timer
     */
    private Container buildContentPane(Container contentPane, TimerInstance timerInstance, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {

        assert currEntry != null : "entry must always be defined";
        Item timedItem = timerInstance.getTimedItem(); //currEntry.timedItem;
        ItemList itemList = timerInstance.getItemList();

        Label elapsedTimeButton;
        MyTextArea description;
        MyCheckBox status = new MyCheckBox(timedItem.getStatus());
//    private Button status;
        MyTextArea comment;
//    private Container commentCont;
        MyDurationPicker remainingEffort = new MyDurationPicker();
        MyDurationPicker effortEstimate;
        Label totalActualEffort;
        Button editItemButton;
        Button timerStartStopButton;
        SpanButton gotoNextTaskButtonWithItemText;
//        UITimer timer;
//        UITimer buzzerTimer;

        SaveEditedValuesLocally previousValues;

        if (Display.getInstance().isScreenSaverDisableSupported()) {
            Display.getInstance().setScreenSaverEnabled(!MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
        }

        // ********************************* COMMANDS **************************
        //
        Command cmdStartNextTask = new Command("StartNextTask", null) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                startNextTask(contentPane, fullScreenTimer);
            }
        };
//        Command cmdSaveAndExit = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                processCurrentItemAndLaunchNextOrExit(null, true, false, false, false);
//            }
//        };
        Command cmdSaveAndExit = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
//                processCurrentItemAndLaunchNextOrExit(null, true, false, false, false);
//update current entry
                timerInstance.stopTimer();
                updateCurrentTimedItem(status.getStatus());

                TimerStack.getInstance().removeTimer(timerInstance);  //remove this timerInstance
                Form form = contentPane.getComponentForm();
                if (form instanceof ScreenTimer6) {
                    assert fullScreenTimer; //TODO we should also be able to test on this simply
                    showPreviousScreenOrDefault(((ScreenTimer6) form).previousForm, fullScreenTimer);
                } else {
                    Container parent = contentPane.getParent();
                    if (parent != null) {
                        parent.removeComponent(contentPane);
                        parent.animateLayout(300);
                    }
                }
//                return; //don't exit here to ensure timerIsUpdatingItemStatus is set false when finishing the update
//                }
            }
        };

        Command cmdSetCompletedAndGotoNextTaskOrExit = new Command("Completed", Icons.iconCheckboxDone) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //update/save current task, move to next AND start the timer
//                processCurrentItemAndLaunchNextOrExit(ItemStatus.DONE, false, false, false, false);
                //stop this timer, save item, 
//                ItemStatus newItemStatus = ItemStatus.DONE;
//                updateCurrentTimedItem(newItemStatus);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.DONE);
//                        Item currentItem = timerInstance.getTimedItem();
//        if (newItemStatus != ItemStatus.CANCELLED && newItemStatus != ItemStatus.DONE
//                && MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem.getBoolean()) { //don't ask when settng Done or cancelled
//            dialogUpdateRemainingTime(remainingEffort).show();
//        }
                putEditedValues2(); //update item with edited/changed values
                DAO.getInstance().save(timedItem);

                updateTimer(newItemStatus);
            }
        };

        /**
         * update/save current task (ask if update Remaining) and either move to
         * next task, and start the timer, or exit if no more tasks
         */
        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                processCurrentItemAndLaunchNextOrExit(null, false, false, false, false);
            }
        };

        Command cmdSetTaskWaitingAndGotoNextTaskOrExit = new Command("Wait", Icons.iconCheckboxWaiting) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                processCurrentItemAndLaunchNextOrExit(ItemStatus.WAITING, false, false, false, false);
//                ItemStatus newItemStatus = ItemStatus.WAITING;
//                updateCurrentTimedItem(newItemStatus);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.WAITING);
                showDialogUpdateRemainingTime(remainingEffort);
                putEditedValues2(); //update item with edited/changed values
                DAO.getInstance().save(timedItem);

                updateTimer(newItemStatus);
            }
        };

        Command cmdSetTaskCancelledAndGotoNextTaskOrExit = new Command("Cancel", Icons.iconCheckboxCancelled) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                processCurrentItemAndLaunchNextOrExit(ItemStatus.WAITING, false, false, false, false);
//                ItemStatus newItemStatus = ItemStatus.CANCELLED;
//                updateCurrentTimedItem(newItemStatus);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.CANCELLED);
                putEditedValues2(); //update item with edited/changed values
                DAO.getInstance().save(timedItem);

                updateTimer(newItemStatus);
            }
        };

        status.setStatusChangeHandler((oldStatus, newStatus) -> {/*//TODO call commands*/
            switch (newStatus) {
                case DONE:
                    cmdSetCompletedAndGotoNextTaskOrExit.actionPerformed(null);
                    break;
                case WAITING:
                    cmdSetTaskWaitingAndGotoNextTaskOrExit.actionPerformed(null);
                    break;
                case CANCELLED:
                    processCurrentItemAndLaunchNextOrExit(null, false, true, false, false); //TODO!!! when cancelling a task, should we still store the time stored?
                    cmdSetTaskCancelledAndGotoNextTaskOrExit.actionPerformed(null);
                    break;
                case ONGOING:
                //do nothing - should already be set to Ongoing when starting timer
                case CREATED:
                //do nothing - user forces status back to empty checkbox
            }
        });

        if (formPreviousValues == null) {
            previousValues = new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());
        }

        Map<Object, UpdateField> parseIdMap2 = new HashMap<Object, UpdateField>(); //create a new hashmap for this item

        contentPane.removeAll(); //clear before rebuilding

        Container cont = new Container(BoxLayout.y());
        cont.setScrollableY(true);
        contentPane.add(BorderLayout.CENTER, cont);

        if (fullScreenTimer) {
            //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
            String listName = null; //"TEMP - Name of the list";
            if (itemList != null) {
                listName = itemList instanceof Category ? "Category: " : "List: " + itemList.getText(); //source is an ItemList 
            } else if (timedItem.isInteruptOrInstantTask()) {
                listName = timedItem.getTaskInterrupted() == null ? "INSTANT TASK" : "INTERRUPT TASK";
            } //else: listName remains null, e.g. if only a single task is timed

            String hierarchyStr = timedItem.getOwnerHierarchyAsString();
            if (listName == null) { //if no list, nor interrupt task, use project hierarchy as top-level context
                listName = hierarchyStr;
                hierarchyStr = null;
            }

            if (hierarchyStr != null) {
                SpanLabel itemHierarchyContainer = new SpanLabel(hierarchyStr);
                itemHierarchyContainer.setHidden(!MyPrefs.getBoolean((MyPrefs.timerAlwaysExpandListHierarchy))); //initial state of visibility

                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
                buttonShowItemHierarchy.addActionListener((e) -> {
                    itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
                    this.animateLayout(300);
                });

                cont.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))).add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));
            } else if (listName != null) {
                cont.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))));
            } //else: no context to show, show nothing
        }

        if (fullScreenTimer) {
            //TODO!!! do NOT use item.isInteruptTask() since we may later continue working on a task that was originally created as an interrupt but after that is just treated as a normal task
            description = new MyTextArea(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
                @Override
                public void longPointerPress(int x, int y) {
                    Log.p("longPointerPress on Timer text area");
                    //TODO!!! call "Regular tasks"
                }
            };
            description.setColumns(100);
            description.setActAsLabel(true);
            makeField(Item.PARSE_TEXT, description, () -> timedItem.getText(),
                    (t) -> timedItem.setText((String) t), () -> description.getText(), (t) -> description.setText((String) t));
            if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
                setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
            }
        } else {
            description = new MyTextArea(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
            description.setColumns(100);
            description.setActAsLabel(true);
            description.setEditable(false); //not editable in small container
            description.setText(timedItem.getText());
        }

        makeField(Item.PARSE_STATUS, status, () -> timedItem.getStatus(), (t) -> timedItem.setStatus((ItemStatus) t),
                () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t));

        editItemButton = new Button(MyReplayCommand.create("EditItem", "", Icons.iconEditSymbolLabelStyle, (e) -> {
            putEditedValues2(parseIdMap2, timedItem); //first update Item with any values changed in Timer
            ScreenItem2 screenItem = new ScreenItem2(timedItem, this, () -> {
                //TODO!!!!!! if item values like description or comment were edited in Timer, they must be shown when editing the item. Simply pass previousValues?!
                DAO.getInstance().save(timedItem);
                description.setText(timedItem.getText());
                status.setStatus(timedItem.getStatus());
                comment.setText(timedItem.getComment());
                effortEstimate.setTime((int) timedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
                remainingEffort.setTime((int) timedItem.getRemainingEffort(false) / MyDate.MINUTE_IN_MILLISECONDS);
                refreshTotalActualEffort();
                ScreenTimer6.this.revalidate();
            }, false, previousValues); //previousValues: pass locally edited value to ScreenItem
            screenItem.show();
        }
        ));
        cont.add(BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton));

        elapsedTimeButton = new Button();
        elapsedTimeButton.setUIID("TimerTimer");
        elapsedTimePicker = new Picker();
        elapsedTimePicker.setHidden(true);
        Container timerContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));

        timerContainer.setLeadComponent(elapsedTimePicker); //ensure that the button behaves like a Picker
        timerContainer.add(elapsedTimeButton, elapsedTimePicker);
//        elapsedTimePicker.setUIID("TimerTimer");
        elapsedTimePicker.setType(Display.PICKER_TYPE_TIME);
        elapsedTimePicker.setTime((int) currEntry.getTimerDurationInMillis() / MyDate.MINUTE_IN_MILLISECONDS);
        elapsedTimePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //whenever elsapsedTimePicker is edited manually (only possible when timer is Paused), update elapsed time afterwards
//                currEntry.setTimerDurationInMillis(((long) elapsedTimeButton.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + currEntry.savedSecondsFromTimerInMillis);
//                timerInstance.setElapsedTime(((long) elapsedTimeButton.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + currEntry.savedSecondsFromTimerInMillis);
                timerInstance.setElapsedTime(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS); //UI: seconds are 'lost' if editing time - makes sense since otherwise the new time set is not really set
                refreshElapsedTimeDisplayed(); //update elapsedTimeButton
                refreshTotalActualEffort();
            }
        });
        parseIdMap2.put("ElapsedTime", () -> {
            timedItem.setActualEffort(timerInstance.isTimerShowActualTotal()
                    ? timerInstance.getElapsedTime()
                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
        });

        Runnable updateTimerDisplay = () -> {
//                new Runnable() {   public void run() {
            // refreshDisplayedTimerInfo();  called on regular updates of the timer screen, as well as on app relaunch
            // setTaskStatusOngoingWhenMinimumThresholdPassed();
            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
                    && currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
                status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> currEntry.timedItem.setStartedOnDate(System.currentTimeMillis()
                        - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            }
            elapsedTimeButton.setText(myFormatter.format(timerInstance.getElapsedTime()));
            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
            refreshTotalActualEffort();
        };
        //If go to nexttask

        UITimer timer = new UITimer(updateTimerDisplay);
        UITimer buzzerTimer = new UITimer(() -> {
            Display.getInstance().vibrate(BUZZER_DURATION);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    try {
//                        wait(BUZZER_PAUSE);
//                        Util.wait(this, BUZZER_PAUSE);
//                    } catch (InterruptedException ex) {
//                        Log.e(ex);
//                    }
//                        Display.getInstance().vibrate(BUZZER_DURATION);
//</editor-fold>
        });

        timerStartStopButton = new Button();
        timerStartStopButton.setCommand(Command.create(null, null, (e) -> {
//            if (timer == null) {
            if (!timerInstance.isRunning()) {                    //start Timer
                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                    setTaskStatusOngoingWhenMinimumThresholdPassed(); //done when updating the display
                timerInstance.startTimer();
                elapsedTimeButton.setEnabled(false); //disable while running
                timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
                timerStartStopButton.repaint(); //this is enough to update the value on the screen

                timer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, contentPane.getComponentForm()); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
                }
//    }
            } else {
                timerInstance.stopTimer();
                timer.cancel(); //stop timer as first thing
                elapsedTimeButton.setEnabled(true); //enable the picker when timer is paused
                timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
                timerStartStopButton.repaint(); //this is enough to update the value on the screen
                buzzerTimer.cancel();
            }
        }));

        timerStartStopButton.setIcon(timerInstance.isRunning() ? Icons.iconTimerPauseLabelStyle : Icons.iconTimerStartLabelStyle);

        if (fullScreenTimer) {
//            remainingEffort = new MyDurationPicker();
            remainingEffort.setShowZeroValueAsZeroDuration(true); //show "0:00"

            makeField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
                    () -> currEntry.timedItem.getRemainingEffort(false), (l) -> currEntry.timedItem.setRemainingEffort((long) l),
                    () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l));

            effortEstimate = new MyDurationPicker();
            effortEstimate.setShowZeroValueAsZeroDuration(true); //show "0:00"
            makeField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate, () -> currEntry.timedItem.getEffortEstimate(), (l) -> currEntry.timedItem.setEffortEstimate((long) l),
                    () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l));

            totalActualEffort = new Label(); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");

            refreshTotalActualEffort();

            //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set. 
            ActionListener estimateAndRemainingUpdater = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //update estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
                    if (remainingEffort.getTime() != 0 && currEntry.timedItem.getEffortEstimate() == 0 && effortEstimate.getTime() == 0) { //if remaining is changed AND effortEstimate is not...
                        effortEstimate.setTime(remainingEffort.getTime());
                        effortEstimate.repaint();
                    }
                    //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                    if (effortEstimate.getTime() != 0 && currEntry.timedItem.getRemainingEffort(false) == 0 && remainingEffort.getTime() == 0) {
                        remainingEffort.setTime(effortEstimate.getTime());
                        remainingEffort.repaint();
                    }
                }
            };
            remainingEffort.addActionListener(estimateAndRemainingUpdater);
            effortEstimate.addActionListener(estimateAndRemainingUpdater);

            TableLayout tl = new TableLayout(2, 3);
            final Container estimateTable = new Container(tl);
            Container effortDetailsCont = null;
            Button showEffortDetailsButton = new Button(Icons.iconShowMoreLabelStyle);

            showEffortDetailsButton.addActionListener(
                    (e) -> {
                        showEffortDetailsButton.setIcon(showEffortDetailsButton.getIcon() == Icons.iconShowMoreLabelStyle ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle);
                        estimateTable.setHidden(!estimateTable.isHidden());
                        MyPrefs.flipBoolean(MyPrefs.timerShowEffortEstimateDetails);
                        animateLayout(300);
                    }
            );
            effortDetailsCont = LayeredLayout.encloseIn(
                    FlowLayout.encloseRightMiddle(showEffortDetailsButton), //!!: reuse same strings as from ScreenItem!
                    GridLayout.encloseIn(3, new Label(""), elapsedTimeButton, FlowLayout.encloseIn(timerStartStopButton))
            );

            cont.add(effortDetailsCont);

            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_ESTIMATE_SHORT)); //"Estimate")); //leftalign labels (like the Tickers)
            estimateTable.add(tl.createConstraint().widthPercentage(34).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_TOTAL_SHORT)); //"Total"));
            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_REMAINING_SHORT)); //"Remaining"));
            //TODO make the effort Pickers small (size as the time, not as the cell) and centered (and center the labels above again)
            estimateTable.add(effortEstimate).add(totalActualEffort).add(remainingEffort);  //!!: reuse same strings as from ScreenItem!
            estimateTable.setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowEffortEstimateDetails)); //hide initially
            cont.add(BorderLayout.center(estimateTable));

            comment = new MyTextArea(Item.COMMENT, 20, 2, 4,
                    MyPrefs.commentMaxSizeInChars.getInt(),
                    TextArea.ANY);
            makeField(Item.PARSE_COMMENT, comment, () -> currEntry.timedItem.getComment(), (t) -> timedItem.setComment((String) t), () -> comment.getText(), (t) -> comment.setText((String) t));
            Container commentContainer = ScreenItem.makeCommentContainer(comment);

            cont.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes

            //Action buttons
            //Show interrupted tasks
//        if (currEntry.interruptOrInstantTask) {
            int textPos = Button.RIGHT; //BOTTOM;
            if (timedItem.isInteruptOrInstantTask()) {
                Item interruptedItem = timedItem.getTaskInterrupted();
                if (interruptedItem != null) { //a task was interrupted
                    //UI: not possible to Exit Timer when an interrupted task is pending, must first deal w ith interrupt and then chose an action on the interrupted task
                    Button c1 = new Button(cmdStopTimerAndGotoNextTaskOrExit);//"Next"),
                    c1.setTextPosition(textPos);
                    Button c2 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
                    c2.setTextPosition(textPos);
                    Button c3 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                    c3.setTextPosition(textPos);
                    cont.add(GridLayout.encloseIn(2, c1, c2)); //autofit
                    cont.add(GridLayout.encloseIn(1, c3)); //autofit
                    //UI: as long as there is interrupted task(s!) show only those (not next tasks)
                    cont.add(new SpanLabel("Interrupted: " + interruptedItem.getText()));
                } else {
//                    assert size() == 1 : "timerStack should always be size==1 when no tasks was interrupted";
//                    Button c4 = new Button(cmdExitTimer); //"Stop"),
                    Button c4 = new Button(cmdSaveAndExit); //"Stop"),
                    c4.setTextPosition(textPos);
                    Button c5 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
                    c5.setTextPosition(textPos);
                    Button c6 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                    c6.setTextPosition(textPos);
                    cont.add(GridLayout.encloseIn(2, c4, c5));
                    cont.add(GridLayout.encloseIn(1, c6));
                }
            } else if (currEntry.nextItem == null) {
                //DONE!!! use task checkbox to mark task Done/Waiting? Only keep Exit/Next to leave it ongoing
                Button c7 = new Button(cmdSaveAndExit); //"Exit"),
                c7.setTextPosition(textPos);
                Button c8 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit);
                c8.setTextPosition(textPos);
                Button c9 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                c9.setTextPosition(textPos);
                cont.add(GridLayout.encloseIn(2, c7, c8));
                cont.add(GridLayout.encloseIn(1, c9));
            } else {
                Button c10 = new Button(cmdSaveAndExit); //"Exit"),
                c10.setTextPosition(textPos);
                Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"), 
                c11.setTextPosition(textPos);
                Button c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next", 
                c12.setTextPosition(textPos);
                Button c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
                c13.setTextPosition(textPos);
                cont.add(GridLayout.encloseIn(3, c10, c11, c12));
                cont.add(GridLayout.encloseIn(1, c13));
                //Show next tasks
                //TODO optimization: only construct nextTask containers etc if there is one and it is shown
                //TODO show button to select auto-start Timer on task or not

                gotoNextTaskButtonWithItemText = new SpanButton("Next task: \"" + currEntry.nextItem.getText() + "\""
                        + (MyPrefs.timerShowNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(currEntry.nextItem.getRemainingEffort()) + "]") : "")); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
                gotoNextTaskButtonWithItemText.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (!MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) { //disable action if automatically going to next action
                            processCurrentItemAndLaunchNextOrExit(null, false, false, true, false);
                        }
                    }
                });
                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                gotoNextTaskButtonWithItemText.setUIID("Label");
                    gotoNextTaskButtonWithItemText.setTextUIID("Label");
                }
                gotoNextTaskButtonWithItemText.setHidden(!MyPrefs.timerShowNextTask.getBoolean());
//                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
                cont.add(gotoNextTaskButtonWithItemText);
            }
        } else { //small timer container
            contentPane.setUIID("SmallTimerContainer");
            boolean interruptTask = false;
            contentPane.add(BorderLayout.centerCenterEastWest(
                    /*Center*/interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description,
                    /*East*/ status,
                    /*West*/ BoxLayout.encloseXNoGrow(timerContainer, new Button(cmdStopTimerAndGotoNextTaskOrExit), editItemButton)
            ));
        }

        return contentPane;
    }

}
