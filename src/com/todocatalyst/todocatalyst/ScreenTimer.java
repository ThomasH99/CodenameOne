package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
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

/**
 * Shows a timer. UI:
 *
 * @author Thomas
 */
public class ScreenTimer extends MyForm {

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

    /**
     * entry always points to the current stack entry, is updated when
     * popping/pushing new entries on the stack
     */
//    private TimerStackEntry timerStack.currEntry;
//    private TimerStackEntry getEntry() {
////        return new TimerStackEntry(new Item("test"));
//        if (timerStack != null && timerStack.size() >= 1) {
//            return timerStack.getLastItemEntry();
//        } else {
//            return null;
//        }
//    }
    private TimerStack timerStack = new TimerStack();
//    private TimerStackEntry entry;

    private UITimer timer;
    private UITimer buzzerTimer;
//    private UITimer saveElapsedTimeTimer;

    private final static String TIMER_STACK_ID = "TodoCatalystStoredTimers";
    private final static int INTERVAL_SAVE_TIMER_TO_LOCAL_STORAGE_MILLIS = 60000; //60000 = one minute
    private final static int INTERVAL_UPDATE_TIMER_MILLIS = 1000;
    private final static boolean TIMER_REPEAT_UPDATE = true;
    //TODO make buzzer interval a setting
    private final static int BUZZER_INTERVAL = 1000 * 30; //TODO: change test value of 30 seconds to 5 minutes = 300000
    private final static int BUZZER_DURATION = 100; //300 milliseconds
    private final static int BUZZER_PAUSE = 100; //300 milliseconds

    /**
     * used to keep track of when to save a backup copy of elapsed to local
     * storage
     */
    private long lastTimeSavedTimerStackMillis = 0;

    private boolean forceTimerStartOnLeafTasksWithAnyStatus; //tested in isSuitableItemForTimerFct, if true, an item will be timed no matter what its Status
    private Item.Condition isSuitableItemForTimerFct = item -> isSuitableItemForTimer(item); //TODO simplify: replace condition by it lamda function

    //UI elements
    private Button timerStartStopButton;
    SpanButton gotoNextTaskButtonWithItemText;
//    private boolean showSeconds; //
    private Picker elapsedTimePicker;

//    private MyTextField description;
    private MyTextArea description;
    private MyCheckBox status;
//    private Button status;
    private MyTextArea comment;
    private Container commentCont;
    private MyDurationPicker remainingEffort;
    private MyDurationPicker effortEstimate;
    private Label totalActualEffort;
    private Button editItemButton;

    SimpleDateFormat myFormatter = new SimpleDateFormat() {
        @Override
        public String format(Object source) {
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
    private ScreenTimer() {
        super(SCREEN_TITLE, null, () -> {
        });
        setLayout(new BorderLayout());
//        setScrollable(false);

        //https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setScrollable(true); //since the size of the timer may overflow

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
    }

    private static ScreenTimer INSTANCE;

    public static ScreenTimer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScreenTimer();
        }
        return INSTANCE;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setTimedItemXXX(Item newItem) {
//        ASSERT.that(newItem == null || getEntry().previousItem != newItem, "old item=" + getEntry().timedItem + ", newItem=" + newItem);
//        getEntry().previousItem = getEntry().timedItem;
//        getEntry().timedItem = newItem;
//    }
    /**
     * returns the current Item of the Timer (whether running or paused). null
     * if no item is set
     *
     * @return
     */
//    Item getTimedItemXXX() {
//        return getEntry().timedItem;
//    }
//    private Item getPreviousTimedItem() {
//        return getEntry().previousItem;
//    }
//</editor-fold>
    private class TimerStack {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Item getPreviousItemXXX() {
//            Item item = null;
//            if (previouslyRunningTimers.size() > 1) {
//                PreviouslyRunningTimerEntry prev = previouslyRunningTimers.get(previouslyRunningTimers.size() - 2);
//                item = DAO.getInstance().getItem(prev.previouslyRunningTimerObjectId);
//            }
//            return item;
//        }

        /**
         * returns last (current) entry in TimerStack
         *
         * @return
         */
//        TimerStackEntry getLastItemEntry() {
//        void getLastItemEntryXXX() {
////            if (previouslyRunningTimers.size()>0)
//////            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
////            entry=previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            updateEntry();
//        }
//        void updateEntry() {
//            if (previouslyRunningTimers.size() > 0) //            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            {
//                entry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            } else {
//                entry = null;
//            }
//        }
//</editor-fold>
        private List<TimerStackEntry> previouslyRunningTimers;
        TimerStackEntry currEntry;

        private TimerStack() {
            if (Storage.getInstance().exists(TIMER_STACK_ID)) {
                previouslyRunningTimers = (ArrayList) Storage.getInstance().readObject(TIMER_STACK_ID); //read in when initializing the Timer - from here on it is only about saving updates
                if (previouslyRunningTimers.size() >= 1) {
                    currEntry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
                } else {
                    currEntry = null;
                }
            }
            if (previouslyRunningTimers == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
                previouslyRunningTimers = new ArrayList(); //create if none existed before
                currEntry = null;
//                save(); //DON'T save before something is added
            }
//            updateEntry();
        }

        void pushCurrentTimerEntryAndCalcNextTask(TimerStackEntry newEntry) {
            previouslyRunningTimers.add(newEntry);
            currEntry = newEntry;
//            updateEntry();
            save();
        }

        void popCurrentTimerEntry() {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (previouslyRunningTimers.size() > 0) {
//                TimerStackEntry entry = previouslyRunningTimers.remove(previouslyRunningTimers.size() - 1);
//                save();
//                return entry;
//            } else {
//                return null;
//            }
//</editor-fold>
            previouslyRunningTimers.remove(previouslyRunningTimers.size() - 1);
            if (previouslyRunningTimers.size() >= 1) {
                currEntry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
            } else {
                currEntry = null;
            }
//            updateEntry();
            save();
        }

//<editor-fold defaultstate="collapsed" desc="comment">
        /**
         * removes all entries from TimerStack. Used when exiting Timer to clean
         * up
         */
//        void removeAllEntries() {
//            for (Object o : previouslyRunningTimers) {
//                previouslyRunningTimers.remove(o);
//            }
//            entry=null;
//            save();
//        }
        /**
         * saves an already running item (if there is one, otherwise nothing
         * happens)
         */
//        void pushItemXXX(Item item, long elapsedTime) {
//            pushItemXXX(item, elapsedTime, false);
//        }
//        void pushItemXXX(Item item, long elapsedTime, boolean running) {
//            boolean savedLocally = false;
//            if (item.getObjectId() == null) { //item has not been saved before (e.g. interrupt task)
//                DAO.getInstance().save(item);
//                savedLocally = true;
//            }
//            previouslyRunningTimers.add(new PreviouslyRunningTimerEntry(item.getObjectId(), elapsedTime, running, savedLocally));
//            save();
//        }
        /**
         * save new item
         */
//        void pushNewItemWithElapsedTimeAndRunningStateXXX(Item item) {
//            pushItemXXX(item, getTimerElapsedTimeMillis(), isTimerRunning());
//        }
//</editor-fold>
        private int size() {
            return previouslyRunningTimers.size();
        }

        boolean timerEntryExists() {
            return previouslyRunningTimers.size() >= 1;
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        Item getPreviousItemXXX() {
//            Item item = null;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (previouslyRunningTimers.size() > 1) {
//                PreviouslyRunningTimerEntry prev = previouslyRunningTimers.get(previouslyRunningTimers.size() - 2);
//                item = DAO.getInstance().getItem(prev.previouslyRunningTimerObjectId);
//            }
//            return item;
//        }
        /**
         * returns last (current) entry in TimerStack
         *
         * @return
         */
//        TimerStackEntry getLastItemEntry() {
//        void getLastItemEntryXXX() {
////            if (previouslyRunningTimers.size()>0)
//////            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
////            entry=previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            updateEntry();
//        }
//        void updateEntry() {
//            if (previouslyRunningTimers.size() > 0) //            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            {
//                entry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            } else {
//                entry = null;
//            }
//        }
//</editor-fold>
        /**
         * returns last (current) entry in TimerStack
         *
         * @return
         */
        Item getInterruptedItem() {
//            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 2); //entry before last
            if (previouslyRunningTimers.size() >= 2) {
                return previouslyRunningTimers.get(previouslyRunningTimers.size() - 2).timedItem; //entry before last
            } else {
                return null;
            }
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        PreviouslyRunningTimerEntry getLastItemEntryXXX() {
//            if (previouslyRunningTimers.size() > 0) {
//                PreviouslyRunningTimerEntry prev = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//                return prev;
//            }
//            return null;
//        }
        /**
         * removes the last item in the list (once it has been dealt with)
         *
         * @return //
         */
//        PreviouslyRunningTimerEntry popLastItemXXX() {
//            PreviouslyRunningTimerEntry lastEntry = previouslyRunningTimers.remove(previouslyRunningTimers.size() - 1);
//            save();
//            return lastEntry;
//        }
//        void setIsRunningStateLastItemXXX(boolean isRunning) {
//            previouslyRunningTimers.get(previouslyRunningTimers.size() - 1).running = isRunning;
//            save();
//        }
//        void setElapsedTimeForLastItemXXX(long elapsedTime) {
//            if (previouslyRunningTimers.size() > 0) {
//                previouslyRunningTimers.get(previouslyRunningTimers.size() - 1).previouslyRunningTimersCountSoFarMillis = elapsedTime;
//                save();
//            }
//        }
//</editor-fold>
        void save() {
            Storage.getInstance().writeObject(TIMER_STACK_ID, previouslyRunningTimers);
        }
    }

    void deleteTimerInfoInStorage() {
        if (Storage.getInstance().exists(TIMER_STACK_ID)) {
            Storage.getInstance().deleteStorageFile(TIMER_STACK_ID);
        }
    }

    // ************************** START TIMER ************************
    //
    /**
     * re-display Timer (with an existing Item whether timer is running or
     * paused). To be called from the small panel shown in other views
     *
     * @param previousForm
     */
//    private void redisplayAlreadyRunningTimerXXX(MyForm previousForm) {
//        this.previousForm = previousForm; //save the screen from where the Timer was re-shown
//        show();
//    }
    /**
     * ensure that whenever we exit the Timer, and the screen is kept on, we
     * disable this.
     *
     * @param previousForm
     */
//    @Override
    static void showPreviousScreenOrDefault(MyForm previousForm, boolean callRefreshAfterEdit) {
        if (Display.getInstance().isScreenSaverDisableSupported() && MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer)) {
            Display.getInstance().setScreenSaverEnabled(true); //true enable normal screensaver, false keeps screen on all the time
        }
//        super.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refresh whenever returning from Timer since tasks may have been closed
        MyForm.showPreviousScreenOrDefault(previousForm, callRefreshAfterEdit); //need to refresh whenever returning from Timer since tasks may have been closed
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void refreshScreen() {
//        getContentPane().removeAll(); //clear existing contentPane
////        buildContentPane(getTimedItem(), getEntry().itemList, getContentPane()); //rebuild for new values of item etc
//        TimerStackEntry entry = null;
//        buildContentPane(entry, getContentPane()); //rebuild for new values of item etc
//    }
//</editor-fold>
    /**
     * prepares the timer for a new item (called from startTimer() and commands)
     *
     * @param newIitem
     * @param startTimerImmediately
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setupAndInitializeForNewItemXXX(Item newIitem, boolean startTimerImmediately) {
////        setTimedItem(newIitem); //DONE in setupAndInitializeForNewOrOldItem
//        timerStack.pushNewItemWithElapsedTimeAndRunningStateXXX(newIitem); //always push new Item to TimerStack //NB push before setting up for item to ensure size of timerstack is updated
//        setupAndInitializeForNewOrOldItemImplXXX(newIitem, startTimerImmediately, 0);
//    }
//    private void setupAndInitializeForOldItemXXX(Item item, boolean startTimerImmediately, long alreadyElapsedMillis) {
//        setupAndInitializeForNewOrOldItemImplXXX(item, startTimerImmediately, alreadyElapsedMillis);
//    }
//    private void setupAndInitializeForNewOrOldItemImplXXX(Item item, boolean startTimerImmediately, long alreadyElapsedMillis) {
//        //update with new item and
//        setTimedItemXXX(item);
//        getEntry().startTime = 0;
//        getEntry().lastTimeTimerValueSaved = 0;
//        getEntry().timerElapsedTimeSavedDuringPauseMillis = alreadyElapsedMillis;
//
//        refreshScreen(); //setup before starting timer in next line
//        if (startTimerImmediately) {
//            reStartTimer();
//        }
//
//        show();
//    }
//</editor-fold>
//    private void showNoMoreTasksDialog(String itemOrListName) {
    private void showNoMoreTasksDialogWhenRelevant(Item item, ItemList itemList) {
        if ((item != null && item.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = item != null ? item.getText() : itemList.getText();
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
        }
//        previousForm.showBack(); //enough to exit the Timer correctly??
//        showPreviousScreen();
    }

    /**
     * dialog to ask user what to do if timer is launched on a new Item while an
     * existing item is already active (whether running or not).
     *
     * @param item
     * @param elapsedTimer
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Dialog dialogTimerAlreadyRunningXXX(String previousTaskDescription, boolean isTimerRunning) { //Item item) {
//        Dialog dia = new Dialog();
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setTitle("Timer running");
//        dia.setBlurBackgroundRadius(10);
//        dia.addComponent(new TextArea("Timer already active for:\n\"" + previousTaskDescription + "\"", 3, 30));
//
//        dia.add(new Button(Command.create("Save", null, (e) -> {
//            stopTimerSaveAndPopItemXXX();
//        })));
//
//        //Keep / Set aside / Push, / ?? / Continue afterwards / Return to afterwards / "Keep to automatically continue previous task"
//        dia.add(new Button(Command.create("Keep", null, (e) -> {
//            stopTimer();
//            updateAndSaveCurrentItemXXX();
//            timerStack.setIsRunningStateLastItemXXX(isTimerRunning);
//            timerStack.setElapsedTimeForLastItemXXX(getTimerElapsedTimeMillis());
//        })));
//        dia.add(new SpanLabel("[Keep] lets you keep the previous task in the Timer and go back to it once you're done with the new one"));
//
//        dia.add(new Button(continueCommand = Command.create("Continue", null, (e) -> {
//            reStartTimer(); //if user cancels the new task, restart Timer which is stopped when showing the dialog
//            show();
//        })));
//
//        //Skip / Drop / Trash / Cancel / Abandon / Trash
//        dia.add(new Button(Command.create("Cancel Timer", null, (e) -> {
//            removeCurrentItemFromTimerStackXXX();
//        })));
//
//        return dia;
//    }
//</editor-fold>
    /**
     * start timer on this item or on first item in itemList. If called when an
     * item/itemList are already active in the timer, the new item/itemList are
     * just ignored and the ScreenTimer shown ( Save any previously running
     * timers. PreviousForm is updated (to support case where you call Timer
     * from another screen, to stop the currently running task, and then
     *
     * @param item
     */
//    private void startTimerImpl(Item item, ItemList itemList, MyForm previousForm, boolean startTimerImmediately, boolean keepPreviousItemWithoutPrompt, boolean interrupt) {
//    private void launchTimerImpl(Item item, ItemList orgItemList, FilterSortDef filter, MyForm previousForm, boolean interruptOrInstantTask) {
    private void launchTimerImpl(Item item, ItemList orgItemList, MyForm previousForm, boolean interruptOrInstantTask) {
//        launchTimerImpl(item, orgItemList, filter, previousForm, interruptOrInstantTask, false);
        launchTimerImpl(item, orgItemList, previousForm, interruptOrInstantTask, false);
    }

//    private void launchTimerImpl(Item item, ItemList orgItemList, FilterSortDef filter, MyForm previousForm, boolean interruptOrInstantTask, boolean forceTimerStartOnLeafTasksWithAnyStatus) {
    private void launchTimerImpl(Item item, ItemList orgItemList, MyForm previousForm, boolean interruptOrInstantTask, boolean forceTimerStartOnLeafTasksWithAnyStatus) {

        if (ScreenTimer.getInstance().isTimerActive() && ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {

        } else {

            assert item != null || orgItemList != null : "either item or itemList must be defined";
            this.forceTimerStartOnLeafTasksWithAnyStatus = forceTimerStartOnLeafTasksWithAnyStatus;
            if (previousForm != ScreenTimer.this) {
                //UI: always store last form to return to when using BACK button (except when Timer launched from Timer, e.g. for interrupts)
                this.previousForm = previousForm; //if Interrupt comes from Timer, do not save the ScreenTimer as previous form, but keep whatever previous form the user come from when first starting the timer
            }

            //if Timer is launched on a new Item/ItemList while aleready running, simply ignore the Item/ItemList and show existing timer
            if (timerStack.currEntry != null && timerStack.currEntry.timedItem != null && !interruptOrInstantTask) { //UI: if Timer launched on new item while running, show timer on existing item 
                //TODO Imp: popup "you want to stop running timer on XX and start on YY?"
//        if (timerAlreadyActive()) {
                getInstance().show();
                return;
            }
            if (interruptOrInstantTask && timerStack.currEntry != null && timerStack.currEntry.timedItem != null) {
                //Real INTERRUPT of already time entry (if timedItem==null then just an 'instant task'
                //if timer is already active, and new item is an intertupt
//            boolean timerRunning = timerStack.currEntry.running;
                boolean timerRunning = timerStack.currEntry.isTimerRunningNow();
                //stop previous timer if needed 
                stopTimer(); //stop timer before pushing
//            timerStack.currEntry.wasTimerRunningWhenInterrupted = timerRunning; //DONE review/simplify logic for entry.running (here it's stored to push the right running value onto the stack)
                timerStack.currEntry.setWasTimerRunningWhenInterrupted(timerRunning); //DONE review/simplify logic for entry.running (here it's stored to push the right running value onto the stack)

                item.setInteruptOrInstantTask(true); //UI: only set interruptTask=true when a timed task was actually interrupted
                //UI: don't store interrupted task when it was itself an interrupt task (also because an interupt is not stored in Parse so creates error when existing the interrupting interrupt task)
                if (!timerStack.currEntry.timedItem.isInteruptOrInstantTask()) {
                    item.setTaskInterrupted(timerStack.currEntry.timedItem); //store which Item this interrupt interrupted (or nothing if null - won't happen since then its simply an instant-task)
                }
                putEditedValues2(parseIdMap2, timerStack.currEntry.timedItem);
                DAO.getInstance().save(timerStack.currEntry.timedItem); //UI: interrupted tasks are saved (in case app is stopped during interrupt, to avoid loosing edited values) so after an interrupt, Cancel doesn't cancel any edits done before the interrupt
                assert orgItemList == null : "itemList should always be null for an interrupt";
//            timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, null, null, interruptOrInstantTask, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem //TODO!! needed/meaningful to pass isSuitableItemForTimerFct??
            } //else { //new Item or ItemList
//        timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, null, null, interruptOrInstantTask, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem //TODO!! needed/meaningful to pass isSuitableItemForTimerFct??
            //interrupt may be true here, but if no task interrupted, it is just considered an instant-task
//        timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, orgItemList, filter, interruptOrInstantTask, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem 
            timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, orgItemList, interruptOrInstantTask, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem 
            if (timerStack.currEntry.timedItem == null) {
                //if no Item given directly, or no suitable sub-tasks, nor any suitable Item found in ItemList, let the user know and exit (stay in previous Form)
//            showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.sourceItemOrProject, timerStack.currEntry.orgItemList);
                showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.sourceItemOrProject, timerStack.currEntry.itemList);
                timerStack.popCurrentTimerEntry(); //remove the just added entry
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        //////////////////////////////////////// OLD STUFF ////////////////////////////
//        if (interrupt && timerAlreadyActive()) {
//        } else if (timerAlreadyActive()) {
//        } else {
//
//            boolean timerIsRunning = isTimerRunning();
//            if (interrupt) {
//                stopTimer(); //stop timer before pushing
//                //TODO push current item
//                updateAndSaveCurrentItem(); //item);
////                timerStack.setIsRunningStateLastItem(getEntry().timerIsRunning);
//                timerStack.setIsRunningStateLastItem(timerIsRunning);
//                timerStack.setElapsedTimeForLastItem(getTimerElapsedTimeMillis());
//            }
//            if (keepPreviousItemWithoutPrompt) {
//                updateAndSaveCurrentItem(); //item);
//                timerStack.setIsRunningStateLastItem(timerIsRunning);
//                timerStack.setElapsedTimeForLastItem(getTimerElapsedTimeMillis());
//            } else {
//                //DIALOG to deal with previous item (save, cancel/skip, keep)
//                Dialog dialogTimerAlreadyRunning = dialogTimerAlreadyRunning(getTimedItem().getText(), timerIsRunning); //create dialog with previous Item
////            Command selectedCmd = dialogTimerAlreadyRunning.getCommand(2); //[Continue] command (=ignore new task) should be index 2 //NO - no commands since only Buttons are used
//                Command selectedCmd = dialogTimerAlreadyRunning.showDialog(); //[Continue] command (=ignore new task) should be index 2
//                if (selectedCmd == continueCommand) {
//                    //don't set up for Item if Continue command was chosen, just show the existing Timer again
//                    show();
//                    return;
//                };
//            }
//        }
//        if (previousForm != ScreenTimerNew.this) {
//            this.previousForm = previousForm; //if Interrupt comes from Timer, do not save the ScreenTimer as previous form, but keep whatever previous form the user come from when first starting the timer
//        }
//        if (itemList != null) /*if started via an Interrupt (<=> no ItemList) do not overwrite old itemList will null */ {
//            getEntry().itemList = itemList;
//            getEntry().itemListIndex = 0;
//        }
//        //if called without a defined item, check if there is one in itemList
//        if (item == null) {
//            item = getNextTimerItemFromItemList(null); //get
//        }
//        //if no Item given directly, nour any suitable Item found in ItemList, let the user know and exit
//        if (item == null) {
//            Dialog.show("Timer", "No tasks to work on, click OK to return", "OK", null);
//            previousForm.showBack(); //enough to exit the Timer correctly??
//        } else {
//            setupAndInitializeForNewItem(item, startTimerImmediately);
//        }
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void launchTimerImplOrg(Item item, ItemList orgItemList, FilterSortDef filter, MyForm previousForm, boolean interruptOrInstantTask) {
//        assert item != null || orgItemList != null : "either item or itemList must be defined";
////        boolean startTimerImmediately = MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer);
//        if (previousForm != ScreenTimer.this) {
//            //UI: always store last form to return to when using BACK button (except when Timer launched from Timer, e.g. for interrupts)
//            this.previousForm = previousForm; //if Interrupt comes from Timer, do not save the ScreenTimer as previous form, but keep whatever previous form the user come from when first starting the timer
//        }
//
////        TimerStackEntry entry = getEntry();
//        //if Timer is launched on a new Item/ItemList while aleready running, simply ignore the Item/ItemList and show existing timer
//        if (timerStack.currEntry != null && timerStack.currEntry.timedItem != null && !interruptOrInstantTask) {
////        if (timerAlreadyActive()) {
//            getInstance().show();
//            return;
//        }
//        if (interruptOrInstantTask && timerStack.currEntry != null && timerStack.currEntry.timedItem != null) {
//            //Real INTERRUPT of already time entry (if timedItem==null then just an 'instant task'
//            //stop previous timer if needed and push previous Item to stack
//            //if timer is already active, and new item is an intertupt
//            //stop previous timer if needed and push previous Item to stack
////            if (entry != null && entry.timedItem != null) {
////                entry.running = isTimerRunning(); //NOW updated&saved done each time Timer is stopped/started
//            boolean timerRunning = timerStack.currEntry.wasTimerRunningWhenInterrupted;
//            stopTimer(); //stop timer before pushing
//            timerStack.currEntry.wasTimerRunningWhenInterrupted = timerRunning; //TODO review/simplify logic for entry.running
//
//            item.setInteruptTask(true); //UI: only set interruptTask=true when a timed task was actually interrupted
//            //UI: don't store interrupted task when it was itself an interrupt task (also because an interupt is not stored in Parse so creates error when existing the interrupting interrupt task)
//            if (!timerStack.currEntry.timedItem.isInteruptTask()) {
//                item.setTaskInterrupted(timerStack.currEntry.timedItem); //store which Item this interrupt interrupted (or nothing if null - won't happen since then its simply an instant-task)
//            }
//            putEditedValues2(parseIdMap2);
//            DAO.getInstance().save(timerStack.currEntry.timedItem); //UI: interrupted tasks are saved (in case app is stopped during interrupt, to avoid loosing edited values) so after an interrupt, Cancel doesn't cancel any edits done before the interrupt
////            }
////                timerStack.pushItemXXX(item, BASELINE);
////                entry = new TimerStackEntry(item, itemList, interrupt);
//            assert orgItemList == null : "itemList should always be null for an interrupt";
////            entry = new TimerStackEntry(item, null, true, condition);
////            entry.findNextTimerItem(condition);
////            entry.findNextTimerItem(condition);
////            entry.initializeTimedItems(condition);
//            //TODO push new interrupt item:
//            timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, null, null, true, isSuitableItemForTimerFct)); //TODO!! needed/meaningful to push condition??
////                updateAndSaveCurrentItem(); //item);
////                timerStack.setIsRunningStateLastItem(getEntry().timerIsRunning);
////                timerStack.setIsRunningStateLastItemXXX(timerIsRunning);
////                timerStack.setElapsedTimeForLastItemXXX(getTimerElapsedTimeMillis());
//        } else //new Item or ItemList
//        //            assert !interrupt : "interrupt should always be false here";
//        //            entry = new TimerStackEntry(item, itemList, interrupt, condition);
//        //            timerStack.pushCurrentTimerEntry(new TimerStackEntry(item, orgItemList, filter, false, condition)); //initialize this entry and find both first timedItem and nextItem
//        //if no Item given directly, nour any suitable Item found in ItemList, let the user know and exit
//        //interrupt may be true, but if no task interrupted, it is just considered an instant-task
//         if (timerStack.currEntry.timedItem == null) {
//                assert false : "this seems to be dead code";
//                showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.item, timerStack.currEntry.orgItemList);
//////                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
////                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
////                previousForm.showBack(); //enough to exit the Timer correctly??
////                timerStack.popCurrentTimerEntry();
////                previousForm.showBack();
//                return;
//            } else {
//                timerStack.pushCurrentTimerEntryAndCalcNextTask(new TimerStackEntry(item, orgItemList, filter, false, isSuitableItemForTimerFct)); //initialize this entry and find both first timedItem and nextItem
//                //                setupAndInitializeForNewItemXXX(getEntry().timedItem, startTimerImmediately);
//                //            }
//
//                //timedItem now set to correct Item, so launch timer on timedItem
//                buildContentPane();
//                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)) { //launch *after* building contentPane
//                    reStartTimer();
//                }
//                show();
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////        //////////////////////////////////////// OLD STUFF ////////////////////////////
////        if (interrupt && timerAlreadyActive()) {
////        } else if (timerAlreadyActive()) {
////        } else {
////
////            boolean timerIsRunning = isTimerRunning();
////            if (interrupt) {
////                stopTimer(); //stop timer before pushing
////                //TODO push current item
////                updateAndSaveCurrentItem(); //item);
//////                timerStack.setIsRunningStateLastItem(getEntry().timerIsRunning);
////                timerStack.setIsRunningStateLastItem(timerIsRunning);
////                timerStack.setElapsedTimeForLastItem(getTimerElapsedTimeMillis());
////            }
////            if (keepPreviousItemWithoutPrompt) {
////                updateAndSaveCurrentItem(); //item);
////                timerStack.setIsRunningStateLastItem(timerIsRunning);
////                timerStack.setElapsedTimeForLastItem(getTimerElapsedTimeMillis());
////            } else {
////                //DIALOG to deal with previous item (save, cancel/skip, keep)
////                Dialog dialogTimerAlreadyRunning = dialogTimerAlreadyRunning(getTimedItem().getText(), timerIsRunning); //create dialog with previous Item
//////            Command selectedCmd = dialogTimerAlreadyRunning.getCommand(2); //[Continue] command (=ignore new task) should be index 2 //NO - no commands since only Buttons are used
////                Command selectedCmd = dialogTimerAlreadyRunning.showDialog(); //[Continue] command (=ignore new task) should be index 2
////                if (selectedCmd == continueCommand) {
////                    //don't set up for Item if Continue command was chosen, just show the existing Timer again
////                    show();
////                    return;
////                };
////            }
////        }
////        if (previousForm != ScreenTimerNew.this) {
////            this.previousForm = previousForm; //if Interrupt comes from Timer, do not save the ScreenTimer as previous form, but keep whatever previous form the user come from when first starting the timer
////        }
////        if (itemList != null) /*if started via an Interrupt (<=> no ItemList) do not overwrite old itemList will null */ {
////            getEntry().itemList = itemList;
////            getEntry().itemListIndex = 0;
////        }
////        //if called without a defined item, check if there is one in itemList
////        if (item == null) {
////            item = getNextTimerItemFromItemList(null); //get
////        }
////        //if no Item given directly, nour any suitable Item found in ItemList, let the user know and exit
////        if (item == null) {
////            Dialog.show("Timer", "No tasks to work on, click OK to return", "OK", null);
////            previousForm.showBack(); //enough to exit the Timer correctly??
////        } else {
////            setupAndInitializeForNewItem(item, startTimerImmediately);
////        }
////</editor-fold>
//    }
//</editor-fold>
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
        launchTimerImpl(item, null, previousForm, true);
    }

//    public void startInterrupt(Item item, MyForm previousForm, boolean startTimerImmediately, boolean keepPreviousItemWithoutPrompt) {
//        startTimerImpl(item, getEntry().itemList, previousForm, startTimerImmediately, keepPreviousItemWithoutPrompt, true);
//    }
//
    public void startTimerOnItem(Item item, MyForm previousForm) {
        startTimerOnItem(item, previousForm, false);
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
        launchTimerImpl(null, orgItemList, previousForm, false);
    }
//    final Image iconTimerStartTimer = FontImage.createMaterial(FontImage.MATERIAL_PLAY_ARROW, new Label().getStyle());
//    final Image iconTimerPauseTimer = FontImage.createMaterial(FontImage.MATERIAL_PAUSE, new Label().getStyle());
    // ********************************* HELPER FUNCTIONS **************************
    //
//    private long getTimerElapsedTimeMillis() {

    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus) {
        processCurrentItemAndLaunchNextOrExit(newItemStatus, false);
    }

    /**
     *
     * @param newItemStatus
     * @param forceExitAfterUpdatingItem indicates that the Timer should exit
     * after updating the currently timed item
     */
    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem) {
        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, false);
    }

    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask) {
        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, cancelCurrentTask, false);
    }

    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask, boolean manuallySelectedGotoNextTask) {
        processCurrentItemAndLaunchNextOrExit(newItemStatus, forceExitAfterUpdatingItem, cancelCurrentTask, manuallySelectedGotoNextTask, false);
    }

    boolean timerIsUpdatingItemStatus;

    /**
     *
     * @param newItemStatus
     * @param forceExitAfterUpdatingItem
     * @param cancelCurrentTask
     * @param manuallySelectedGotoNextTask
     * @param timerBeingStoppedRemotely Timer is being stopped by anohter means
     * than Timer UI, e.g. stopped because the timed tasks is marked
     * Done/Cancelled or Deleted (//TODO!!!! will this work?)
     */
    private void processCurrentItemAndLaunchNextOrExit(ItemStatus newItemStatus, boolean forceExitAfterUpdatingItem, boolean cancelCurrentTask, boolean manuallySelectedGotoNextTask, boolean timerBeingStoppedRemotely) {
//        TimerStackEntry entry = getEntry();
        assert !timerIsUpdatingItemStatus || timerBeingStoppedRemotely : "timerIsUpdatingItemStatus not reset back to false correctly";

        if (!timerIsUpdatingItemStatus && timerBeingStoppedRemotely) {
            stopTimer();
            putEditedValues2(parseIdMap2, timerStack.currEntry.timedItem); //update any values edited on the Timer screen (includes handling of whether timer shows elapsed or total actual time)
            DAO.getInstance().save(timerStack.currEntry.timedItem); //save //done in putEditedValues2 (NO, only initially for previously unsaved items)
            //NO MORE ITEMS in this timerStack entry (e.g. interrupt, single task or itemList with no more items
            timerStack.popCurrentTimerEntry(); //when stopping timer remotely, always pop the current
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
                parseIdMap2.put(status, () -> timerStack.currEntry.timedItem.setStatus(newItemStatus)); //if newItemStatus is defined, then use that to override the value previously manually set using status checkbox //TODO a bit complicated UI:
                status.setStatusIcon(newItemStatus);
                status.repaint();
            }
            repaint(); //update for invisible button

        } else {

            stopTimer();
            //UPDATE/SAVE current task, move to next AND start the timer
            //set Status (NB. This many override a manually set value? - no, not if setting the state saves the task)
//        if (newItemStatus != null) { //null => no change of status, e.g. when exiting the timer
//            status.setStatus(newItemStatus); //DON'T - triggers command to start next task
//        }
            if (cancelCurrentTask) {
                //when cancelling, no update of any edited values or elapsed time
//            timerStack.popCurrentTimerEntry(); //remove the cancelled entry (the current one just being cancelled)
                //if we cancel a temporary item (e.g an interrupt task) then we need to delete the version stored in Parse for backup
                if (timerStack.currEntry.timedItemSavedLocallyInTimer && timerStack.currEntry.timedItem.getObjectId() != null) {
//                Item tempItem = DAO.getInstance().getItem(cancelledItemsEntry.getObjectId());
//                    timerStack.currEntry.timedItem.delete();
                    DAO.getInstance().delete(timerStack.currEntry.timedItem);
                }
            } else {
                //if NOT Cancelled, update the values and save
                if (newItemStatus != null) {
                    parseIdMap2.put(status, () -> timerStack.currEntry.timedItem.setStatus(newItemStatus)); //if newItemStatus is defined, then use that to override the value previously manually set using status checkbox //TODO a bit complicated UI:
                }
                if (newItemStatus != ItemStatus.CANCELLED && newItemStatus != ItemStatus.DONE
                        && MyPrefs.timerAlwaysShowDialogToAskToUpdateRemainingTimeAterTimingAnItem.getBoolean()) { //don't ask when settng Done or cancelled
//                    dialogUpdateRemainingTime(timerStack.currEntry.timedItem, parseIdMap2).show();
                    dialogUpdateRemainingTime(remainingEffort).show();
                }
                putEditedValues2(parseIdMap2, timerStack.currEntry.timedItem); //update any values edited on the Timer screen (includes handling of whether timer shows elapsed or total actual time)
                DAO.getInstance().save(timerStack.currEntry.timedItem); //save //done in putEditedValues2 (NO, only initially for previously unsaved items)
            }

//            assert timerStack.size() <= 1 || timerStack.currEntry.interruptOrInstantTask : "timerStack.size can only be bigger than one if last entry is an interrupt, size=" + timerStack.size() + ", interrupt=" + timerStack.currEntry.interruptOrInstantTask;
            assert timerStack.size() <= 1 || timerStack.currEntry.timedItem.isInteruptOrInstantTask() //                    : "timerStack.size can only be bigger than one if last entry is an interrupt, size=" + timerStack.size() + ", interrupt=" + timerStack.currEntry.interruptOrInstantTask;
                    : "timerStack.size can only be bigger than one if last entry is an interrupt, size=" + timerStack.size() + ", interrupt=" + timerStack.currEntry.timedItem.isInteruptOrInstantTask();

            if (forceExitAfterUpdatingItem) {
                timerStack.popCurrentTimerEntry();
//                showPreviousScreen();
                timerIsUpdatingItemStatus = false;
                showPreviousScreenOrDefault(previousForm, true);
//                return; //don't exit here to ensure timerIsUpdatingItemStatus is set false when finishing the update
            } else {
//                boolean finishedTaskWasAnInterrupt = timerStack.currEntry.interruptOrInstantTask;
                boolean finishedTaskWasAnInterrupt = timerStack.currEntry.timedItem.isInteruptOrInstantTask();
//                assert !timerStack.currEntry.interruptOrInstantTask || timerStack.currEntry.nextItem == null : "normally nextItem should be null for an interrupt, nextItem=" + timerStack.currEntry.nextItem.getText() + ", entry.interrupt=" + timerStack.currEntry.interruptOrInstantTask;
                assert !finishedTaskWasAnInterrupt || timerStack.currEntry.nextItem == null :
                        "normally nextItem should be null for an interrupt, nextItem=" + timerStack.currEntry.nextItem.getText()
                        + ", entry.interrupt=" + timerStack.currEntry.timedItem.isInteruptOrInstantTask();
                if (timerStack.currEntry.nextItem == null) {
                    //NO MORE ITEMS in this timerStack entry (e.g. interrupt, single task or itemList with no more items
                    timerStack.popCurrentTimerEntry();
                }

//                if (timerStack.size() >= 1) { //an entry already exists
                if (timerStack.timerEntryExists()) { //an entry already exists
                    if (finishedTaskWasAnInterrupt) {
                        assert !forceExitAfterUpdatingItem : "forcing an exit from an interrupt that interrupted a previously running task should not be possible";
                        buildContentPane(); //build timer content paner for new timedItem
//                        if (timerStack.currEntry.wasTimerRunningWhenInterrupted) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
//                        if (timerStack.currEntry.isWasTimerRunningWhenInterrupted()) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                        if (timerStack.currEntry.isWasTimerRunningWhenInterrupted()) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                            reStartTimer();
                        }
//                    revalidate(); //TODO: necessary to call revalidate?
                        setTransitionOutAnimator(new FlipTransition(-1, 200)); //not working, YES, working on iPhone, 300 a bit slow, 
                        show();
                    } else { //else continue with interrupted task
                        timerStack.currEntry.findNextTimerItem(isSuitableItemForTimerFct);
                        if (timerStack.currEntry.timedItem != null) {
                            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) || manuallySelectedGotoNextTask) {
                                buildContentPane(); //build timer content paner for new timedItem
//                            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer) || entry.running) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
                                    reStartTimer();
                                }
//                            revalidate();
//                            show();
                                animateLayout(300);
                            }
                        } else {
//                            showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.sourceItemOrProject, timerStack.currEntry.orgItemList);
                            showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.sourceItemOrProject, timerStack.currEntry.itemList);
                            timerStack.popCurrentTimerEntry();
//                            showPreviousScreen();
                            timerIsUpdatingItemStatus = false;
                            showPreviousScreenOrDefault(previousForm, true);
//                        return;
                        }
                    }
                } else {
//                showNoMoreTasksDialogWhenRelevant(entry.item, entry.orgItemList);
//                timerStack.popCurrentTimerEntry();
//                    showPreviousScreen();
                    timerIsUpdatingItemStatus = false;
                    showPreviousScreenOrDefault(previousForm, true);
//                return;
                }
            }
        }
        timerIsUpdatingItemStatus = false;
//<editor-fold defaultstate="collapsed" desc="comment">
//////////////////////////////////////////// OLD /////////////////
////        if (getEntry().interrupt) {
//        if (timerStack.size() >= 2) { //first continue any previously pushed item (after an interrupt task)
//            assert entry.interrupt : "if timerStack.size>=2 last entry should always be created by an interrupt";
//            //timed task is an interrupt
//            //and actually interrupted another task
//            timerStack.popCurrentTimerEntry();
////            entry = getEntry(); //get next entry
////                nextItem = getEntry().timedItem; //continue with the same task that was interrupted
//            //do nothing since entry.timedItem is already set to correct value (the interrupted task)
//        } else {
//            //and did NOT interrupt another task
//            entry.findNextTimerItem(condition);
//            //now entry.timedItem is set to next item to start timer on (or null if not more entries)
//        }
////        entry.findNextTimerItem(condition);
////        entry.resetValues();
//
//        if (entry.timedItem != null && !forceExitAfterUpdatingItem) {
//            //NEXT TASK
//            //continue with next item (project subtask or item in itemList)
//            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                buildContentPane(); //build timer content paner for new timedItem
//                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer) || entry.running) { //UI: entry.running: if timer was running when interrupted, it continues running after the interrupt task is dealt with
//                    reStartTimer();
//                }
//                revalidate();
////                show();
//            }
//        } else {
//            //EXIT
//            //entry.timedItem == null || forceExitAfterUpdatingItem
//            if (!forceExitAfterUpdatingItem) { //do not show dialog if user has chosen Exit (only if no more subtasks/tasks in list)
////                String itemOrListName = entry.item != null ? entry.item.getText() : entry.itemList.getText();
//                showNoMoreTasksDialogWhenRelevant(entry.item, entry.orgItemList);
//            }
//            timerStack.popCurrentTimerEntry();
//            showPreviousScreen();
//            return;
//        }
//</editor-fold>
    }

    //
    /**
     * called when exiting the app, stores the latest status of the timer. Saves
     * the edited item so values can be shown next time.
     */
//    public void saveTimerStatusOnAppStop() {
//// NOT necessary since Timer will save automatically on every significant change (launch, Timer start/pause)        
////        if (timerStack != null && timerStack.currEntry != null) {
////            timerStack.currEntry.setWasTimerRunningWhenInterrupted(timerStack.currEntry.isTimerRunningNow());
////            timerStack.save(); //save status (e.g. of running)
////        }
//    }
    /**
     * called on destroy
     */
//    public void onDestroy() {
////        if (timerStack != null && timerStack.currEntry != null && timerStack.currEntry.timedItem != null) {
////            putEditedValues2(parseIdMap2, timerStack.currEntry.timedItem); //update any values edited on the Timer screen (includes handling of whether timer shows elapsed or total actual time)
////            DAO.getInstance().save(timerStack.currEntry.timedItem); //!!
//////            DAO.getInstance().save(timerStack.currEntry.timedItem); //save
////        }
//    }
    /**
     * called when restarting the timer after app was exited.
     *
     * @return true if Timer successfully relaunched, false if something went
     * wrong
     */
    public boolean relaunchTimerOnAppRestart() {
//        boolean timerRunningForPushedEntry = timerStack.currEntry.wasTimerRunningWhenInterrupted;
//        if (timerRunningForPushedEntry) {

        assert timerStack != null : "timerStack not read correctly";
        if (timerStack != null && timerStack.currEntry != null && timerStack.currEntry.timedItem != null
                && (timerStack.currEntry.itemList != null || timerStack.currEntry.sourceItemOrProject != null)) {

            if (timerStack.currEntry.itemList != null
                    && timerStack.currEntry.itemListIndex > timerStack.currEntry.itemList.size() - 1) {
                timerStack.currEntry.itemListIndex = timerStack.currEntry.itemList.size() - 1; //adjust index if list has shortened since (eg edited on another device)
            }
//        boolean timerRunningForPushedEntry = timerStack.currEntry.isWasTimerRunningWhenInterrupted();
//        if (timerRunningForPushedEntry) { //Timer was stopped correctly on app Pause/Exit
            boolean timerRunningForPushedEntry = timerStack.currEntry.isTimerRunningNow(); //check if Timer was running when app was exited

            buildContentPane();

            boolean valuesRestored = restoreEditedValuesSavedLocallyOnAppExit(); //restore any previously saved values
            deleteEditedValuesSavedLocallyOnAppExit();

            if (timerRunningForPushedEntry) { //Timer was running when app was Paused/Exited
                timerStack.currEntry.updateAndStoreElapseTimerDurationOnPause(); //force it to pause so timer can be re-started normally
//            timerStack.currEntry.updateTimerStartTimeOnTimerStart();//force it to pause so timer can be re-started normally
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
            ScreenTimer.getInstance().deleteTimerInfoInStorage();
        }
        return false;
    }

    /**
     * is used in app.start() to test if Timer was active when app was stopped
     *
     * @return
     */
    public boolean isTimerActive() {
//        return timerStack.size() >= 1; //at least one item is saved/active 
        return timerStack.timerEntryExists(); //at least one item is saved/active 
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
        if (timerStack != null && timerStack.currEntry != null && item.equals(timerStack.currEntry.timedItem) && isTimerRunning()) {
            processCurrentItemAndLaunchNextOrExit(null, false, false, false, true);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * restart timer
     */
//    public void restartTimer() {
////        if (getEntry().running) {
//        if (entry.running) {
//            show();
//        }
//    }
//    private long getTimerElapsedTimeMillis() {
////        if (isTimerRunning()) {
//////            return (System.currentTimeMillis() - getEntry().startTimeMillis);
////            return (System.currentTimeMillis() - timerStack.currEntry.virtualTimerStartTimeMillis);
////        } else {
//////            return getEntry().timerElapsedTimeSavedDuringPauseMillis;
////            return timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis;
////        }
//        return timerStack.currEntry.getTimerDurationInMillis();
//    }
    /**
     * returns true if Timer already has an Item (whether it is running or not)
     *
     * @return
     */
//    private boolean timerAlreadyActiveXXX() {
//        return getTimedItemXXX() != null;
//    }
//</editor-fold>
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
        long MAX_TIMER_VALUE = Math.min(ABSOLUTE_MAX_TIMER_VALUE, MyPrefs.timerMaxTimerDurationInHours.getInt() * MyDate.HOUR_IN_MILISECONDS); //==23:59
//        return ((timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS)
//                            || (!timerShowsActualTotal && timerValueMillis + itemActualEffort + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS));
        return ((timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS >= ABSOLUTE_MAX_TIMER_VALUE)
                || (timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS - itemActualEffort >= MAX_TIMER_VALUE)
                || (!timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS >= MAX_TIMER_VALUE));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * does everything to finish and save for the current Item. Update values,
     * save Item, pop from stack
     */
//    private void updateCurrentItemWithoutUpdatingActualXXX() {
//        Item item = getTimedItemXXX();
//        if (item != null) {
//            putEditedValues2(parseIdMap2); //save any values edited on the screen
//        }
//    }
//    private void updateCurrentItemXXX() { //Item item) {
//        Item item = getTimedItemXXX();
//        if (item != null) {
////            putEditedValues2(parseIdMap2); //save any values edited on the screen
////            updateCurrentItemWithoutUpdatingActual();
//            putEditedValues2(parseIdMap2); //save any values edited on the screen
////            Item.ItemStatus newItemStatus = item.getStatus();
////            if (MyPrefs.getBoolean(MyPrefs.timerShowTotalActualInTimer)) {
////                item.setActualEffort(item.getActualEffort() + getTimerElapsedTimeMillis());
////            }
//        }
//    }
//    private void updateAndSaveCurrentItemXXX() { //Item item) {
//        updateCurrentItemXXX();
//        DAO.getInstance().save(getTimedItemXXX()); //save
//    }
//</editor-fold>
    /**
     * returns true if the minimum Timer threshold for when to update status and
     * save time is passed
     *
     * @return
     */
    private boolean isMinimumThresholdPassed() {
//        return status.getStatus() != ItemStatus.CREATED //if status already other than created we store any time -NO, since this will also later create a timer Start/Stop recording
//                && timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.getInt(MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActuals) * MyDate.SECOND_IN_MILLISECONDS;
        return timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
    }

    /**
     * //UI: when Timer has passed minimum threshold, task status is set to
     * Ongoing (only if Created <=> except if Waiting or Ongoing or Completed or
     * Cancelled)
     */
    private void setTaskStatusOngoingWhenMinimumThresholdPassed() {
        if (isMinimumThresholdPassed() && status.getStatus() == ItemStatus.CREATED) {
//DON'T revert a cancelled task to ongoing just because time is spent on it
            status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
            parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> timerStack.currEntry.timedItem.setStartedOnDate(System.currentTimeMillis()
                    - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
        }

    }

    /**
     * called on regular updates of the timer screen, as well as on app relaunch
     * to ensure
     */
    public void refreshDisplayedTimerInfo() {
        setTaskStatusOngoingWhenMinimumThresholdPassed();
        long timerValueMillis = timerStack.currEntry.getTimerDurationInMillis();
        //UI: currently Picker time is limited to 23h59m so need to save time if that 
//        if (maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, timerValueMillis, timerStack.currEntry.timedItem.getActualEffort(false))) {
        if (maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, timerValueMillis, timerStack.currEntry.timedItem.getActualEffortProjectTaskItself())) {
            //TODO!!!! how to handle when timer reaches max value?? Stop at 23h59, show popup when max value reached. Alternatively, save elapsed time and start over (omplex). Best option: create own timer Picker with no maximum value (99h59m, or 99999d23h59m)
            stopTimer(); //if max value reached for Total, stop timer. Necessary to stop when total
        }
        elapsedTimePicker.setText(myFormatter.format(timerValueMillis));
//        elapsedTimePicker.animate(); //this is enough to update the value on the screen
        elapsedTimePicker.repaint(); //this is enough to update the value on the screen
//                    updateTotalActualEffort(timerStack.currEntry.timedItem.getActualEffort(false), timerValueMillis);
        updateTotalActualEffort();
//        totalActualEffort.animate();
        totalActualEffort.repaint();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * once a pushed task has been saved, it can be removed from the stack
     */
//    private void removeCurrentItemFromTimerStackXXX() {
//        //only clear stored values once we're sure the item has been saved
//        timerStack.popLastItemXXX();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void reStartTimerOLD() {
//        assert timer == null && buzzerTimer == null : "all timers should be null whenever restarted, timer=" + timer + " buzzerTimer=" + buzzerTimer;
////        TimerStackEntry entry = getEntry();
//
////        elapsedTimePicker.setText(myFormatter.format((int) getTimerElapsedTimeMillis())); //update picker immediately when starting Timer
////        elapsedTimePicker.setText(myFormatter.format(getTimerElapsedTimeMillis())); //update picker immediately when starting Timer
//elapsedTimePicker.setText(myFormatter.format(timerStack.currEntry.getTimerDurationInMillis())); //update picker immediately when starting Timer
//elapsedTimePicker.animate(); //this is enough to update the value on the screen
//
//if (timer == null && !maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, timerStack.currEntry.getTimerDurationInMillis(), timerStack.currEntry.timedItem.getActualEffort(false))) {
//    elapsedTimePicker.setEnabled(false); //disable while running
////            if (status.getStatus() == ItemStatus.CREATED || status.getStatus() == ItemStatus.CANCELLED) {
////            if (status.getStatus() == ItemStatus.CREATED
////                    && timerStack.currEntry.getTimerDurationInMillis()>=MyPrefs.getInt(MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActuals)*MyDate.SECOND_IN_MILLISECONDS) {
//////DON'T revert a cancelled task to ongoing just because time is spent on it
////                status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
////                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
////            }
//setTaskStatusOngoingWhenMinimumThresholdPassed();
////            timerStack.currEntry.virtualTimerStartTimeMillis = System.currentTimeMillis() - timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis; //deduct timerCountSoFar to include previous timer count
//timerStack.currEntry.updateTimerStartTimeOnTimerStart();
////            if (MyPrefs.getBoolean(MyPrefs.timerShowTotalActualInTimer)) {
////                getEntry().startTime -= getEntry().timedItem.getActualEffort(false); //if
////            }
////            lastTimeSavedTimerStackMillis = entry.startTimeMillis;
//lastTimeSavedTimerStackMillis = System.currentTimeMillis();
////            timerStack.currEntry.wasTimerRunningWhenInterrupted = true;
//
////create new timer and store (to cancel)
//(timer = new UITimer(new Runnable() {
//    public void run() {
////                    long timerValueMillis = getTimerElapsedTimeMillis();
//setTaskStatusOngoingWhenMinimumThresholdPassed();
////                    elapsedTimeButton.setText(formatTime(timerValue));
////UI: currently Picker time is limited to 23h59m so need to save time if that
////                    if (timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS) {
////                    if ((entry.timerShowsActualTotal && timerValueMillis + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS) || (!entry.timerShowsActualTotal && timerValueMillis + entry.timedItem.getActualEffort(false) + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS)) {
//long timerValueMillis = timerStack.currEntry.getTimerDurationInMillis();
//if (maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, timerValueMillis, timerStack.currEntry.timedItem.getActualEffort(false))) {
////                        if (timerValueMillis + entry.timedItem.getActualEffort(false) + INTERVAL_UPDATE_TIMER_MILLIS > MyDate.DAY_IN_MILLISECONDS) {
////if max value reached for Total, stop timer. Necessary to stop when total
////TODO!!!! how to handle when timer reaches max value?? Stop at 23h59, show popup when max value reached. Alternatively, save elapsed time and start over (omplex). Best option: create own timer Picker with no maximum value (99h59m, or 99999d23h59m)
//stopTimer();
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (entry.timerShowsActualTotal) {
////                            entry.timedItem.setActualEffort(timerValue);
////                        } else {
////                            entry.timedItem.addToActualEffort(timerValue);
////                        }
////                        timerValue=0; //start timer all over
////</editor-fold>
//}
////<editor-fold defaultstate="collapsed" desc="comment">
////                    elapsedTimePicker.setText(MyDate.formatTime(timerValue, MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer)));
////                    int minutes = (int)timerValueMillis / MyDate.MINUTE_IN_MILLISECONDS;
////                    elapsedTimePicker.setTime(timerValueMillis / MyDate.MINUTE_IN_MILLISECONDS);
////                    elapsedTimePicker.setTime(minutes); //DON'T update actual value in picker until timer is stopped
////                    elapsedTimePicker.setText(myFormatter.format((int) timerValueMillis));
////</editor-fold>
//elapsedTimePicker.setText(myFormatter.format(timerValueMillis));
//elapsedTimePicker.animate(); //this is enough to update the value on the screen
//updateTotalActualEffort(timerStack.currEntry.timedItem.getActualEffort(false), timerValueMillis);
//totalActualEffort.animate();
////                    ScreenTimer.this.revalidate();
////save the latest value of the elapsed time at least every 15seconds
////                    if (timerValueMillis > lastTimeSavedTimerStackMillis + INTERVAL_SAVE_TIMER_TO_LOCAL_STORAGE_MILLIS) {
//if (System.currentTimeMillis() >= lastTimeSavedTimerStackMillis + INTERVAL_SAVE_TIMER_TO_LOCAL_STORAGE_MILLIS) {
//    //'hi-jack' the timer update to save the elapsed time to local storage
////                        entry.previouslyRunningTimersCountSoFarMillis = timerValueMillis;
////                        timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis = timerValueMillis; //
//lastTimeSavedTimerStackMillis = System.currentTimeMillis();
//timerStack.save();
//}
//    }
//}
//)).schedule(INTERVAL_UPDATE_TIMER_MILLIS, TIMER_REPEAT_UPDATE, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
////            timerStartStopButton.setIcon(iconTimerPauseTimer);
//timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
//timerStartStopButton.animate(); //this is enough to update the value on the screen
////            ScreenTimer.this.revalidate();
//
////Start Buzzer
//(buzzerTimer = new UITimer(new Runnable() {
//    public void run() {
//        Display.getInstance().vibrate(BUZZER_DURATION);
////                    try {
////                        wait(BUZZER_PAUSE);
//Util.wait(this, BUZZER_PAUSE);
////                    } catch (InterruptedException ex) {
////                        Log.e(ex);
////                    }
//Display.getInstance().vibrate(BUZZER_DURATION);
//    }
//}
//)).schedule(BUZZER_INTERVAL, true, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
//timerStack.save(); //save status (e.g. of running)
//}
//    }
//</editor-fold>
    private void reStartTimer() {
        assert timer == null && buzzerTimer == null : "all timers should be null whenever restarted, timer=" + timer + " buzzerTimer=" + buzzerTimer;
        elapsedTimePicker.setText(myFormatter.format(timerStack.currEntry.getTimerDurationInMillis())); //update picker immediately when starting Timer
        elapsedTimePicker.animate(); //this is enough to update the value on the screen

        if (timer == null && !maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, 
//                timerStack.currEntry.getTimerDurationInMillis(), timerStack.currEntry.timedItem.getActualEffort(false))) {
                timerStack.currEntry.getTimerDurationInMillis(), timerStack.currEntry.timedItem.getActualEffortProjectTaskItself())) {
            elapsedTimePicker.setEnabled(false); //disable while running
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
            setTaskStatusOngoingWhenMinimumThresholdPassed();
            timerStack.currEntry.updateTimerStartTimeOnTimerStart();
            lastTimeSavedTimerStackMillis = System.currentTimeMillis();
            //create new timer and store (to cancel)
            (timer = new UITimer(new Runnable() {
                public void run() {
                    if (false) {
                        setTaskStatusOngoingWhenMinimumThresholdPassed();
                        long timerValueMillis = timerStack.currEntry.getTimerDurationInMillis();
                        //UI: currently Picker time is limited to 23h59m so need to save time if that 
                        if (maxTimerTimePassed(timerStack.currEntry.timerShowsActualTotal, timerValueMillis, 
//                                timerStack.currEntry.timedItem.getActualEffort(false))) {
                                timerStack.currEntry.timedItem.getActualEffortProjectTaskItself())) {
                            //TODO!!!! how to handle when timer reaches max value?? Stop at 23h59, show popup when max value reached. Alternatively, save elapsed time and start over (omplex). Best option: create own timer Picker with no maximum value (99h59m, or 99999d23h59m)
                            stopTimer(); //if max value reached for Total, stop timer. Necessary to stop when total
                        }
                        elapsedTimePicker.setText(myFormatter.format(timerValueMillis));
                        elapsedTimePicker.animate(); //this is enough to update the value on the screen
//                    updateTotalActualEffort(timerStack.currEntry.timedItem.getActualEffort(false), timerValueMillis);
                        updateTotalActualEffort();
                        totalActualEffort.animate();
                    }
                    refreshDisplayedTimerInfo();
//                    ScreenTimer.this.revalidate();
                    //save the latest value of the elapsed time at least every 15seconds
                    if (System.currentTimeMillis() >= lastTimeSavedTimerStackMillis + INTERVAL_SAVE_TIMER_TO_LOCAL_STORAGE_MILLIS) {
                        //'hi-jack' the timer update to save the elapsed time to local storage
                        lastTimeSavedTimerStackMillis = System.currentTimeMillis();
                        timerStack.save();
                    }
                }
            }
            //            )).schedule(INTERVAL_UPDATE_TIMER_MILLIS, TIMER_REPEAT_UPDATE, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
            )).schedule(MyPrefs.timerUpdateInterval.getInt() * MyDate.SECOND_IN_MILLISECONDS, TIMER_REPEAT_UPDATE, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
            timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
            timerStartStopButton.animate(); //this is enough to update the value on the screen

            //Start Buzzer
            if (MyPrefs.timerBuzzerInterval.getInt() != 0) {
                (buzzerTimer = new UITimer(new Runnable() {
                    public void run() {
                        Display.getInstance().vibrate(BUZZER_DURATION);
//                    try {
//                        wait(BUZZER_PAUSE);
                        Util.wait(this, BUZZER_PAUSE);
//                    } catch (InterruptedException ex) {
//                        Log.e(ex);
//                    }
                        Display.getInstance().vibrate(BUZZER_DURATION);
                    }
                }
                //            )).schedule(BUZZER_INTERVAL, true, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
                )).schedule(MyPrefs.timerBuzzerInterval.getInt(), true, ScreenTimer.this); //update every second. TODO change to every minute when timer>60s
            }

            timerStack.save(); //save status (e.g. of running)
        }
    }

    private void stopTimer() {
        //stop timer if running
        if (timer != null) {
//            TimerStackEntry entry = getEntry();
//            timerStack.currEntry.wasTimerRunningWhenInterrupted = false;
//            timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis = getTimerElapsedTimeMillis(); //save elapsed time *before* stopping the timer
            timerStack.currEntry.updateAndStoreElapseTimerDurationOnPause(); //save elapsed time *before* stopping the timer
            timer.cancel(); //stop timer as first thing
            timer = null; //null <=> timer is not running
//            timerStack.setElapsedTimeForLastItemXXX(getEntry().timerElapsedTimeSavedDuringPauseMillis); //save latest when stopping/pausing (in case of crash/exit app)
            timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
//            timerStack.currEntry.secondsInMillis = (timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis % MyDate.MINUTE_IN_MILLISECONDS); // / MyDate.SECOND_IN_MILLISECONDS;
            timerStack.currEntry.savedSecondsFromTimerInMillis = (timerStack.currEntry.getTimerDurationInMillis() % MyDate.MINUTE_IN_MILLISECONDS); // / MyDate.SECOND_IN_MILLISECONDS;
//            elapsedTimePicker.setTime((int) ((entry.timerElapsedTimeSavedDuringPauseMillis / MyDate.MINUTE_IN_MILLISECONDS) + ((entry.secondsInMillis >= MyDate.MINUTE_IN_MILLISECONDS / 2) ? 1 : 0))); //convert time to minutes, round up based on seconds (any rounded up value is removed when saving the time)
//            elapsedTimePicker.setTime((int) ((timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis / MyDate.MINUTE_IN_MILLISECONDS))); //convert time to minutes, round up based on seconds (any rounded up value is removed when saving the time)
            elapsedTimePicker.setTime((int) ((timerStack.currEntry.getTimerDurationInMillis() / MyDate.MINUTE_IN_MILLISECONDS))); //convert time to minutes, round up based on seconds (any rounded up value is removed when saving the time)
//            elapsedTimePicker.setTime((int) ((entry.timerElapsedTimeSavedDuringPauseMillis))); //covert time to minutes, round up based on seconds (any rounded up value is removed when saving the time)
            elapsedTimePicker.setEnabled(true); //enable the picker when timer is paused
//            ScreenTimer.this.revalidate();
            timerStartStopButton.animate(); //this is enough to update the value on the screen

            if (buzzerTimer != null) {
                buzzerTimer.cancel();
                buzzerTimer = null;
            }
            timerStack.save(); //save status (e.g. of running)
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveAndPopItemXXX() {
//        updateAndSaveCurrentItemXXX(); //item);
//        removeCurrentItemFromTimerStackXXX();
//    }
//    private void stopTimerSaveAndPopItemXXX() {
//        stopTimer();
//        saveAndPopItemXXX();
//    }
//    private void stopTimerSaveAndPopItemAndCleanUpTimerBeforeExitingXXX() {
//        stopTimerSaveAndPopItemXXX();
////        setTimedItem(null);
//        cleanUpWhenExitingTimerXXX();
//    }
//</editor-fold>
    // ********************************* COMMANDS **************************
    //
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * //UI: will set the status of the currently timed task to Cancelled
     */
//    private Command cmdCancelTimerXXX = new Command("Cancel") {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
////            PreviouslyRunningTimerEntry cancelledItemsEntry = timerStack.popLastItemXXX(); //remove the cancelled entry (the current one just being cancelled)
////            stopTimer();
////            timerStack.popCurrentTimerEntry(); //remove the cancelled entry (the current one just being cancelled)
////            //if we cancel a temporary item (e.g an interrupt task) then we need to delete the version stored in Parse for backup
////            if (entry.timedItemSavedLocallyInTimer && entry.timedItem.getObjectId() != null) {
//////                Item tempItem = DAO.getInstance().getItem(cancelledItemsEntry.getObjectId());
////                entry.timedItem.delete();
////            }
////            removeCurrentItemFromTimerStackXXX();
////            setTimedItemXXX(null);
////            previousForm.refreshAfterEdit();
////            previousForm.showBack();
////            showPreviousScreen(); //NO, cancel should only exit if there are no interrupted tasks to continue with
//            processCurrentItemAndLaunchNextOrExit(null, false, true);
//        }
//    };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void showPreviousScreen() {
////        if (previousForm!=null) {
////        previousForm.refreshAfterEdit();
////        previousForm.showBack();
////        } else {
////
////        }
//        showPreviousScreenOrDefault(previousForm);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * done command doesn't change the Timer but leaves it running and simply
     * displays the previous form (from the Timer was opened)
     */
//    private Command showPreviousScreenCommandXXX = new Command("", Icons.iconBackToPrevFormToolbarStyle) {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            //the Timer's Done command should not execute the doneActions since it lets the timer continue running and only shows the previous screen from where the Timer was called
////            showPreviousScreen();
//            showPreviousScreenOrDefault(previousForm);
//        }
//    };
//</editor-fold>
    private Command cmdSaveAndExit = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
        @Override
        public void actionPerformed(ActionEvent evt) {
//            stopTimer();
            processCurrentItemAndLaunchNextOrExit(null, true);
//            timerStack.popCurrentTimerEntry(); //ALREADY done in processCurrentItemAndLaunchNextOrExit
//            showPreviousScreen();
//            showPreviousScreenOrDefault(previousForm); //NOT necessary since already done inside processCurrentItemAndLaunchNextOrExit when exiting
        }
    };

    private Command cmdSetCompletedAndGotoNextTaskOrExit = new Command("Completed", Icons.iconCheckboxDone) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            //update/save current task, move to next AND start the timer
//            stopTimer();
//            dialogSetWaitingDateAndAlarm(getEntry().timedItem, parseIdMap2);
            //update/save current task, move to next AND start the timer
            processCurrentItemAndLaunchNextOrExit(ItemStatus.DONE);
        }
    };

    /**
     * only called when setting task checkbox to Cancelled manually
     */
    private Command cmdSetCancelledAndGotoNextTaskOrExitXXX = new Command("Cancel", Icons.iconCheckboxDone) {
        @Override
        public void actionPerformed(ActionEvent evt) {
            //update/save current task, move to next AND start the timer
//            stopTimer();
//            dialogSetWaitingDateAndAlarm(getEntry().timedItem, parseIdMap2);
            //update/save current task, move to next AND start the timer
            processCurrentItemAndLaunchNextOrExit(ItemStatus.CANCELLED);
        }
    };

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * Find and setup Timer on next item, start Timer is the option to do so it
     * active. Will try to find a next Item to start Timer on: first check
     * Timerstack for interrupts, then see if there are subtasks in a project or
     * more tasks in an itemList. the see if there is a suitable item in
     * ItemList, otherwise stop the timer and exit. Implementation must be the
     * same as ** Special case: an interrupt task where the user has manually
     * created subtasks. Precondition: previously timed item is completely dealt
     * with before calling here.
     */
//    private void findAndStartNextTaskOrExitTimerIfNoMoreXXX() {
//        Item nextItem;
//        //set up for next task
//        if (timerStack.size() >= 2) { //first continue any previously pushed item (after an interrupt task)
////            PreviouslyRunningTimerEntry entry = timerStack.getLastItemEntry();
//            DAO.getInstance().save(getEntry().timedItem); //save interrupt
//            timerStack.popCurrentTimerEntry(); //skip interrupt entry
////            nextItem = DAO.getInstance().getItem(getEntry().previouslyRunningTimerObjectId);
//            setupAndInitializeForOldItemXXX(nextItem, MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer), getEntry().previouslyRunningTimersCountSoFarMillis);
//        } else if ((nextItem = getNextTimerItemFromItemList(getPreviousTimedItem())) != null) {
//            getEntry().timerElapsedTimeSavedDuringPauseMillis = 0;
//            setupAndInitializeForNewItemXXX(nextItem, MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer));
//        } else {
//            //no more tasks
//            cleanUpWhenExitingTimerXXX();
//            showPreviousScreen();
//        }
//    }
//</editor-fold>
    /**
     * update/save current task (ask if update Remaining) and either move to
     * next task, and start the timer, or exit if no more tasks
     */
    private Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
        @Override
        public void actionPerformed(ActionEvent evt) {
//            stopTimer();
            processCurrentItemAndLaunchNextOrExit(null);
        }
    };

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * update/save current task (ask if update Remaining) and either move to
     * next task, and start the timer, or exit if no more tasks
     */
//    private Command cmdGotoNextTaskXXX = new Command("Next task", null) {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
////            stopTimer();
//            processCurrentItemAndLaunchNextOrExit(null, false);
//        }
//    };
//</editor-fold>
    private Command cmdExitTimer = new Command("Exit", Icons.iconCheckboxOngoing) {
        @Override
        public void actionPerformed(ActionEvent evt) {
//            stopTimer();
            processCurrentItemAndLaunchNextOrExit(null, true);
        }
    };

    /**
     * update/save current task (ask if update Remaining) and either move to
     * next task, and start the timer, or exit if no more tasks
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command gotoNextTaskOrExitIfNoneXXX = new Command("Next - temp", null) {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            stopTimerSaveAndPopItemXXX(); //finish ongoing task, remove current task
//            findAndStartNextTaskOrExitTimerIfNoMoreXXX();
//        }
//    };
//    private void cleanUpWhenExitingTimerXXX() {
//        setTimedItemXXX(null);
//        getEntry().lastTimeTimerValueSaved = 0;
//        getEntry().startTime = 0;
//        getEntry().timerElapsedTimeSavedDuringPauseMillis = 0;
//    }
//    private Command exitTimerWhileWorkingOnItemListXXX = new Command("Exit Timer", null) {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            stopTimerSaveAndPopItemAndCleanUpTimerBeforeExitingXXX(); //finish ongoing task, remove current task
//            showPreviousScreen();
//        }
//    };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    } else if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) && getEntry().nextItem != null) {
//            //there's another task to start
//
//        } else {
//            cleanUpWhenExitingTimerXXX();
//            showPreviousScreen();
//        }
//
//        //Find and launch next item
//            setupAndInitializeForOldItemXXX(nextItem, MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer), getEntry().previouslyRunningTimersCountSoFarMillis);
//        } else if ((nextItem = getNextTimerItemFromItemList(getPreviousTimedItem())) != null) {
//            getEntry().timerElapsedTimeSavedDuringPauseMillis = 0;
//            setupAndInitializeForNewItemXXX(nextItem, MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer));
//        } else {
//            //no more tasks
//            cleanUpWhenExitingTimerXXX();
//            showPreviousScreen();
//        }
//</editor-fold>
//    }
    /**
     * mark task completed (also sets Remaining to 0), save it and either move
     * to next task, and start the timer, or exit if no more tasks
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command cmdMarkDoneAndStartNextTaskOrExitIfNoneXXX = new Command("Completed", null) {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            //update/save current task, move to next AND start the timer
//            stopTimer();
////            check
//            updateCurrentItemXXX();
//            removeCurrentItemFromTimerStackXXX();
//            getTimedItemXXX().setStatus(ItemStatus.DONE);
//            DAO.getInstance().save(getTimedItemXXX()); //save
//            findAndStartNextTaskOrExitTimerIfNoMoreXXX();
//            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) && getEntry().nextItem != null) {
//
//            } else {
//
//            }
//        }
//    };
//</editor-fold>
    private Command cmdSetTaskWaitingAndGotoNextTaskOrExit = new Command("Wait", Icons.iconCheckboxWaiting) {
        @Override
        public void actionPerformed(ActionEvent evt) {
//            stopTimer();
//            if (MyPrefs.waitingAskToSetWaitingDateWhenMarkingTaskWaiting.getBoolean()) {
//                dialogSetWaitingDateAndAlarm(timerStack.currEntry.timedItem, parseIdMap2).show();
//            }
//                dialogSetWaitingDateAndAlarm(timerStack.currEntry.timedItem, parseIdMap2);
            if (false) {
                dialogSetWaitingDateAndAlarm(timerStack.currEntry.timedItem); //Cancel won't work here, but not necessary since only called when exiting the task --> NOW done when changing status on item
            }            //update/save current task, move to next AND start the timer
            processCurrentItemAndLaunchNextOrExit(ItemStatus.WAITING);
        }
    };

    //****************** TIMER COMMANDS *********************
    //
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command startTimerCmd = new Command("") {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            reStartTimer();
//        }
//    };
//
//    private Command pauseTimerCmd = new Command("") {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            stopTimer();
//        }
//    };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Command startPauseTimerCmdXXX = new Command("") {
//        @Override
//        public void actionPerformed(ActionEvent evt) {
//            if (timer == null) {
////                startTimerCmd.actionPerformed(null);
//                reStartTimer();
//            } else {
////                pauseTimerCmd.actionPerformed(null);
//                stopTimer();
//            }
//        }
//    };
//    private Command autoStartOnOffCmdXXX = new Command("Auto-start Timer " + (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer) ? "OFF" : "ON"), Icons.iconTimerAutoStartTimerOnNextTaskToolbarStyle) { //"Auto-start next task "
//        @Override
//        public void actionPerformed(ActionEvent evt) {
////            MyPrefs.setBoolean(MyPrefs.timerAutomaticallyStartTimer, !MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)); //invert and save
//            MyPrefs.flipBoolean(MyPrefs.timerAutomaticallyStartTimer); //invert and save
//            this.setCommandName("Auto-start next task " + (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer) ? "OFF" : "ON")); //update command labe
////            updateNextButtonsXXX();
//
//        }
//    };
//    private Command autoStartOnOffCmdIconOnlyXXX = new Command(null, Icons.iconTimerAutoStartTimerOnNextTaskToolbarStyle) { //"Auto-start next task "
//        @Override
//        public void actionPerformed(ActionEvent evt) {
////            MyPrefs.setBoolean(MyPrefs.timerAutomaticallyStartTimer, !MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer)); //invert and save
//            MyPrefs.flipBoolean(MyPrefs.timerAutomaticallyStartTimer); //invert and save
//            this.setCommandName("Auto-start next task " + (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer) ? "OFF" : "ON")); //update command labe
////            updateNextButtonsXXX();
//
//        }
//    };
//</editor-fold>
    //****************** UI *********************
    //
    private void addCommandsToToolbar(Toolbar toolbar) {

//        showPreviousScreenCommand.putClientProperty("android:showAsAction", "withText");
//        toolbar.addCommandToLeftBar(showPreviousScreenCommand);
//        toolbar.setBackCommand(showPreviousScreenCommand, Toolbar.BackCommandPolicy.ALWAYS); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
//        toolbar.setBackCommand(showPreviousScreenCommand); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
//        toolbar.setBackCommand(showPreviousScreenCommand); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html
        toolbar.setBackCommand(makeDoneCommandWithNoUpdate()); //make an Android back command https://www.codenameone.com/blog/toolbar-back-easier-material-icons.html

        //Create an interrupt task and start the timer on it
        toolbar.addCommandToRightBar(makeInterruptCommand());

//<editor-fold defaultstate="collapsed" desc="comment">
//        toolbar.addCommandToOverflowMenu(makeCancelCommand());
//        toolbar.addCommandToOverflowMenu(autoStartOnOffCmd);
//        toolbar.addCommandToLeftBar(autoStartOnOffCmdIconOnly);
//        toolbar.addCommandToOverflowMenu(cmdCancelTimer);
//        if (Display.getInstance().isScreenSaverDisableSupported()) {
//            toolbar.addCommandToOverflowMenu(Command.create("Screen always " + MyPrefs.pick(MyPrefs.timerKeepScreenAlwaysOnInTimer, "OFF", "ON"),
//                    Icons.iconTimerScreenAlwaysOnLabelStyle, (e) -> {
//                        MyPrefs.flipBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer);
//                        Display.getInstance().setScreenSaverEnabled(MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
//                        this.setName("Screen always " + MyPrefs.pick(MyPrefs.timerKeepScreenAlwaysOnInTimer, "OFF", "ON"));
////                    Command.this.setIcon("Screen always " + MyPrefs.pick(MyPrefs.timerKeepScreenAlwaysOnInTimer, "OFF", "ON"));
//                    }));
//        }
//</editor-fold>
//        toolbar.addCommandToOverflowMenu(new Command("Timer settings", Icons.iconSettingsLabelStyle) {
        toolbar.addCommandToRightBar(MyReplayCommand.create("TimerSettings", null, Icons.iconSettingsLabelStyle, (e) -> {
            new ScreenSettingsTimer(ScreenTimer.this, () -> {
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

//<editor-fold defaultstate="collapsed" desc="comment">
    /* get the next-coming item to be able to show it in the screen
    *
    * @param prevItem
    * @return
     */
//    private Item getNextTimerItemToShowFromItemListXXX(Item prevItem) {
//        int tempIndex = getEntry().itemListIndex; //make a copy to be able to restore the original value after getNextTimerItemFromItemList changes it
//        Item foundItem = getNextTimerItemFromItemList(prevItem);
//        getEntry().itemListIndex = tempIndex;
//        return foundItem;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Item getNextTimerItemFromItemListXXX(Item previousItem) {
//        Item tempItem = null;
//        //first check if timer is already running for some Item, if so, restore it (if the current item came from the stack, it should already have been removed in stop...)
//
//        //if we have a list, get next available (if any)
//        if (getEntry().itemList != null) {
//            while (getEntry().itemListIndex < getEntry().itemList.getSize()) {
//                tempItem = (Item) getEntry().itemList.getItemAt(getEntry().itemListIndex); //TODO: support different algorithms for picking the first (or simply use the sorting order from the previous screen?!)
////                Item.ItemStatus itemStatus = tempItem.getStatus();
////                if (itemStatus != Item.ItemStatus.STATUS_DONE && itemStatus != Item.ItemStatus.STATUS_CANCELLED) {
//                if (isSuitableItemForTimer(tempItem)) {
//                    if (tempItem.isProject()) {
//                        Item leafItem = tempItem.getNextLeafItem(previousItem, false);
//                        //If a leaf item is found, use it
//                        if (leafItem != null) {
//                            return leafItem;
//                        }
//                    } else { //not done and not a project <=> we've found an appropriate item to continue on
//                        return tempItem;
//                    }
//                } else {
//                    //try next Item
//                    getEntry().itemListIndex++;
//                }
//            }
//        }
//        return null; //null if no items found
//    }
//</editor-fold>
    //****************** DIALOGS *********************
    //
    /**
     * dialog to ask about updating Remaining effort
     *
     * @param item
     * @param elapsedTimer
     * @return
     */
//    private Dialog dialogSaveAndUpdateItem(Item item, long elapsedTimer) {
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Dialog dialogValidateRemainingForNotCompletedTaskxxx(Item item, long elapsedTimer) {
//        //TODO implement dialogValidateRemainingForNotCompletedTask, verfy useful to remind about updating, add a preference to enable
//        Dialog dia = new Dialog();
//        dia.setTitle("Remaining effort?");
//        dia.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.setCommandsAsButtons(true);
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        dia.add(cont);
//
//        cont.add(new SpanLabel(item.getText(), "Label"));
//        MyDurationPicker remainingTimePicker = new MyDurationPicker(parseIdMap2, () -> (int) item.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS, (i) -> {
//        });
//        if (false) {
//            cont.add(new Label("Remaining")).add(new Label(MyDate.formatTimeDuration(item.getRemainingEffortNoDefault(), MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer))));
//        }
//        if (false) {
////            cont.add(new Label("Elapsed")).add(new Label(MyDate.formatTime(getTimerElapsedTimeMillis(), MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer))));
//            cont.add(new Label("Elapsed")).add(new Label(MyDate.formatTimeDuration(timerStack.currEntry.getTimerDurationInMillis(), MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer))));
//        }
//        cont.add(new Label("New Remaining")).add(remainingTimePicker);
//        dia.addComponent(new Button(Command.create("OK", null, (e) -> {
////            stopTimer();
//            if (remainingTimePicker.getTime() != (int) item.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS) {
//                item.setRemainingEffortXXX(remainingTimePicker.getTime() / MyDate.MINUTE_IN_MILLISECONDS);
//            }
//            dia.dispose(); //close dialog
//        })));
//        return dia;
//    }
//</editor-fold>
    /**
     * return a small container to display the running timer in other views.
     * Container: "[Task text] [05:25] >" - press task to open Timer, press time
     * to pause/continue timer. Returns null if no timer is running.
     *
     * @return
     */
//    public Container buildSmallTimerContainerXXX() {
//        Container cont = null;
//        if (isTimerRunning()) {
//            cont = new Container(new BoxLayout(BoxLayout.X_AXIS));
////            cont.add(new Button(new Command(getTimedItemXXX().getText()) {
//            cont.add(new Button(new Command(timerStack.currEntry.timedItem.getText()) {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    ScreenTimer.getInstance().show();
//                }
//            }));
////            cont.add(new Button(startPauseTimerCmdXXX));
//        }
//        return cont;
//    }
    /**
     * return an icon to be shown in toolbar to indicate that timer is running.
     * Clicking it will open the timer screen. E.g. a watch icon where the hand
     * is rotating every second.
     *
     * @return
     */
//    public Container buildAnimatedIconForToolbarXXX() {
//        // TODO Possible to reuse the Start-Timer icon button so it animates if timer is already running, and not if no timer is activated?
//        Container cont = null;
//        return cont;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Button completedNextItemButton;
//    private Button nextItemButton;
//    private Button elapsedTimeButton = new Button();
//    private SpanLabel nextTaskLabel;
//    private void updateNextButtonsXXX() {
//        boolean autoStartNext = MyPrefs.getBoolean(MyPrefs.timerAutomaticallyStartTimer);
//        if (completedNextItemButton != null) {
//            completedNextItemButton.setText((autoStartNext ? "Completed + Start Next" : "Completed + Next"));
//            completedNextItemButton.animate();
//        }
//        if (nextItemButton != null) {
//            nextItemButton.setText(autoStartNext ? "Stop + Start Next" : "Stop + Next");
//            nextItemButton.animate();
//        }
//    }
//</editor-fold>
    private void refreshItemEditFields(Item updatedItem) {
        description.setText(updatedItem.getText());
//        description.setColumns(100);
//        description.setActAsLabel(true);
//        status.setSelected(updatedItem.isDone());
//        status.setSelected(updatedItem.isDone());
        status.setStatus(updatedItem.getStatus());
        comment.setText(updatedItem.getComment());
        effortEstimate.setTime((int) updatedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
        remainingEffort.setTime((int) updatedItem.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS);
//        remainingEffort.setTime(updatedItem.getRemainingEffortInMinutes()); //DOES not change
//        effortEstimate.setTime(updatedItem.getRemainingEffortInMinutes()); //DOES not change
//        updateTotalActualEffort(updatedItem.getActualEffort(false), elapsedTimePicker.getTime());
        updateTotalActualEffort();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private int calcTotalEffortInMinutesXXX(long previousActualEffortMillis, long elapsedTimerTimeInMillis, boolean timerShowsTotalActual, boolean roundUpMinutes) {
//        long resultMillis = calcTotalEffortInMillis(previousActualEffortMillis, elapsedTimerTimeInMillis, timerShowsTotalActual);
//        int result;
//        result = (int) ((resultMillis / MyDate.MINUTE_IN_MILLISECONDS));
//        if (resultMillis % MyDate.MINUTE_IN_MILLISECONDS >= MyDate.MINUTE_IN_MILLISECONDS / 2) {
//            result++;
//        }
////        if (timerShowsTotalActual) {
////            result = (int) ((elapsedTimerTimeInMillis / MyDate.MINUTE_IN_MILLISECONDS));
////            if (elapsedTimerTimeInMillis % MyDate.MINUTE_IN_MILLISECONDS >= MyDate.MINUTE_IN_MILLISECONDS / 2) {
////                result++;
////            }
////        } else {
//////            return (item.getActualEffort(false) + elapsedTimerTimeInMillis) / MyDate.MINUTE_IN_MILLISECONDS;
////            result = (int) ((previousActualEffortMillis + elapsedTimerTimeInMillis) / MyDate.MINUTE_IN_MILLISECONDS);
////            if (elapsedTimerTimeInMillis % MyDate.MINUTE_IN_MILLISECONDS >= MyDate.MINUTE_IN_MILLISECONDS / 2) {
////                result++;
////            }
////        }
//        return result;
//    }
//</editor-fold>
    private static long calcTotalEffortInMillis(long previousActualEffortMillis, long elapsedTimerTimeInMillis, boolean timerShowsTotalActual) {
        if (timerShowsTotalActual) {
            return (int) ((elapsedTimerTimeInMillis));
        } else {
//            return (item.getActualEffort(false) + elapsedTimerTimeInMillis) / MyDate.MINUTE_IN_MILLISECONDS;
            return (int) ((previousActualEffortMillis + elapsedTimerTimeInMillis));
        }
    }

    private void updateTotalActualEffort() {
        updateTotalActualEffortImpl(timerStack.currEntry.timedItem.getActualEffortProjectTaskItself(), timerStack.currEntry.getTimerDurationInMillis());
    }

    private void updateTotalActualEffortImpl(long previousActualEffortMillis, long timerElapsedTimeMillis) {
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
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * gets the next item to launch the timer on. Either a subtask of a project,
     *
     * @param currentItem
     * @return
     */
//    private Item getNextItemToStartTimerOnXXX(Item currentItem) {
//        Item nextItemFromItemList = getNextTimerItemToShowFromItemListXXX(currentItem);
//        Item nextItemFromTimerStack = timerStack.getPreviousItemXXX();
//        Item nextItem = nextItemFromTimerStack != null ? nextItemFromTimerStack : nextItemFromItemList;
//        return nextItem;
//    }
//</editor-fold>
    /**
     * build the user interface of the Timer
     */
//    private Container buildContentPane(TimerStackEntry entry, Item item, ItemList itemList, Container content) {
//    private Container buildContentPane(TimerStackEntry entry, Container content) {
    private Container buildContentPane() {

        assert timerStack.currEntry != null : "entry must always be defined";

        if (Display.getInstance().isScreenSaverDisableSupported()) {
            Display.getInstance().setScreenSaverEnabled(!MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
        }

//        TimerStackEntry entry = getEntry();
//        Item item = entry.item;
//        Item item = entry.timedItem;
        if (MyPrefs.getBoolean(MyPrefs.timerShowTotalActualInTimer)) { //on
            if (!timerStack.currEntry.timerShowsActualTotal) {
//            getEntry().startTime -= getEntry().timedItem.getActualEffort(false); //if 
//            timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis = (int) timerStack.currEntry.timedItem.getActualEffort(false); //TODO!!! fix possible problems due to Picker not being able to handle long
//            if (!isTimerRunning()) {
//                timerStack.currEntry.setTimerDurationInMillis(timerStack.currEntry.timedItem.getActualEffort(false)); //TODO!!! fix possible problems due to Picker not being able to handle long
//            }
                timerStack.currEntry.setTimerDurationInMillis(timerStack.currEntry.getTimerDurationInMillis() + timerStack.currEntry.timedItem.getActualEffortProjectTaskItself()); //TODO!!! fix possible problems due to Picker not being able to handle long
            }
            timerStack.currEntry.timerShowsActualTotal = true;
        } else { //off
            if (timerStack.currEntry.timerShowsActualTotal) { //was on before, now turned off, so remove item.actual
                timerStack.currEntry.setTimerDurationInMillis(timerStack.currEntry.getTimerDurationInMillis() - timerStack.currEntry.timedItem.getActualEffortProjectTaskItself()); //TODO!!! fix possible problems due to Picker not being able to handle long
            }
            timerStack.currEntry.timerShowsActualTotal = false;
        }

//        ItemList itemList = entry.itemList;
//        boolean showSeconds = MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer);
        parseIdMap2 = new HashMap<Object, UpdateField>(); //create a new hashmap for this item

        Container contentPane = getContentPane();
        contentPane.removeAll(); //clear before rebuilding
//        content.setLayout(new BorderLayout());

        Container cont = new Container(BoxLayout.y());
        cont.setScrollableY(true);
        contentPane.add(BorderLayout.CENTER, cont);

//        cont.add(LayeredLayout.encloseIn(
////                FlowLayout.encloseCenterMiddle(new Label("ItemList name")),
//                new Label("ItemList name"),
//                FlowLayout.encloseRightMiddle(new Button(Icons.iconShowMoreLabelStyle))));
        //find and show hierarchy context for the item (List to which it belongs, and if a subtask, the (hierarchical) project context
        String listName = null; //"TEMP - Name of the list";
//        if (false) { //DONE!!!! update this to work with various items etc
//        if (timerStack.currEntry.orgItemList != null) {
        if (timerStack.currEntry.itemList != null) {
//            listName = "List: \"" + timerStack.currEntry.orgItemList.getText() + "\""; //source is an ItemList 
            listName = "List: \"" + timerStack.currEntry.itemList.getText() + "\""; //source is an ItemList 
        } else if (timerStack.currEntry.sourceItemOrProject != null) {
            if (timerStack.currEntry.sourceItemOrProject.isProject()) {
//            if (entry.item.getOwnerItem() == null) {
                if (timerStack.currEntry.sourceItemOrProject.getOwner() == null) {
                    if (!(timerStack.currEntry.sourceItemOrProject.getOwner() instanceof Item)) { //no owner (or owner not an Item)
                        listName = "Project: \"" + timerStack.currEntry.sourceItemOrProject.getText() + "\"";
                    } else {
                        listName = "Sub project: \"" + timerStack.currEntry.sourceItemOrProject.getText() + "\"";
                    }
                }
            }
//        } else if (timerStack.currEntry.interruptOrInstantTask) {
        } else if (timerStack.currEntry.timedItem.isInteruptOrInstantTask()) {
            if (timerStack.currEntry.timedItem.getTaskInterrupted() == null) { //                listName = entry.timedItem.getText();
                listName = "INSTANT TASK";
            } else {
                listName = "INTERRUPT TASK";
            }
        }
//            else {
//                listName=null;
//            }
//        }
        if (listName != null) { // && !listName.equals("")) {
//            List hierarchyList = new ArrayList(); //TODO implement support for showing the project hierarchy (the levels of subtasks) of a leaf task
            List projectHierarchyList = timerStack.currEntry.timedItem.getOwnerHierarchy(); //DONE implement support for showing the project hierarchy (the levels of subtasks) of a leaf task
            //projectHierarchyList format "directOnwer / nextLevelOwner / Top-levelProject
//            if (projectHierarchyList.size() == 0) {
            if (projectHierarchyList.size() == 1) {
                cont.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))));
            } else {
//                Container itemHierarchyContainer = new Container(BoxLayout.y());
                String hierarchyStr = "";
                String sep = "";
//                for (int i = 0, size = projectHierarchyList.size(); i < size; i++) {
                for (int i = 1, size = projectHierarchyList.size(); i < size; i++) { //1 since we skip the project itself
//                    hierarchy = hierarchy + sep + ((Item) hierarchyList.get(i)).getText();
                    hierarchyStr = "\"" + ((Item) projectHierarchyList.get(i)).getText() + "\"" + sep + hierarchyStr;
                    sep = " / ";
                    //TODO indent margin (i*15)
                }
                if (projectHierarchyList.size() <= 1) {
                    hierarchyStr = "Project: " + hierarchyStr;
                } else {
                    hierarchyStr = "Project hierarchy: " + hierarchyStr; //format "directOnwer / nextLevelOwner / Top-levelProject
                }
//                itemHierarchyContainer.add(new SpanLabel(hierarchy));
                SpanLabel itemHierarchyContainer = new SpanLabel(hierarchyStr);
//                itemHierarchyContainer.add(new SpanLabel(hierarchy));
//                itemHierarchyContainer.setHidden(true);
                itemHierarchyContainer.setHidden(MyPrefs.getBoolean((MyPrefs.timerAlwaysExpandListHierarchy)));
                Button buttonShowItemHierarchy = new Button();
                buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle() : Icons.iconShowLessLabelStyle); //switch icon
                buttonShowItemHierarchy.addActionListener((e) -> {
                    itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden());
//                    buttonShowItemHierarchy.setIcon(buttonShowItemHierarchy.getIcon() == Icons.iconShowMoreLabelStyle ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle); //switch icon
                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
//                revalidate();
//                    animateHierarchy(300);
                    animateLayout(300);
                });
                cont.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))).add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));
            }
        }

        //TODO!!! do NOT use item.isInteruptTask() since we may later continue working on a task that was originally created as an interrupt but after that is just treated as a normal task
//        description = new MyTextField(item.isInteruptTask() ? "INTERRUPT task" : "Task description", 20, TextArea.ANY, parseIdMap2, () -> item.getText(), (s) -> item.setText(s));
//        description = new MyTextField(Item.DESCRIPTION, 20, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> timerStack.currEntry.timedItem.getText(), (s) -> timerStack.currEntry.timedItem.setText(s));
        description = new MyTextArea(Item.DESCRIPTION, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2, () -> timerStack.currEntry.timedItem.getText(), (s) -> timerStack.currEntry.timedItem.setText(s));
        description.setColumns(100);
//               description.setSingleLineTextArea(false);
//               description.setRows(3);
//               description.setRows(3);
        description.setActAsLabel(true);
//        if (timerStack.currEntry.interruptOrInstantTask && description.getText().equals("")) {
        if (timerStack.currEntry.timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
            setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
        }
//        status = new MyCheckBox(null, parseIdMap2, () -> item.isDone(), (b) -> item.setDone(b));
//        status = ItemContainer.createCheckbox(item, false);
//        status = new MyCheckBox(item, false);
        status = new MyCheckBox(timerStack.currEntry.timedItem.getStatus(), (oldStatus, newStatus) -> {/*//TODO call commands*/
//            if (newStatus != status.getStatus()) {
//            if (newStatus != oldStatus) {
//                this.setstatus = newStatus;
            switch (newStatus) {
                case DONE:
                    cmdSetCompletedAndGotoNextTaskOrExit.actionPerformed(null);
                    break;
                case WAITING:
                    cmdSetTaskWaitingAndGotoNextTaskOrExit.actionPerformed(null);
                    break;
                case CANCELLED:
//                    cmdCancelTimer.actionPerformed(null);
                    processCurrentItemAndLaunchNextOrExit(null, false, true); //TODO!!! when cancelling a task, should we still store the time stored?
                    break;
                case ONGOING:
                //do nothing - should already be set to Ongoing when starting timer
                case CREATED:
                //do nothing - user forces status back to empty checkbox
            }
//            }
        }, () -> {
            return timerStack.currEntry.timedItem.getActualEffort() > 0;
        });
        parseIdMap2.put(status, () -> timerStack.currEntry.timedItem.setStatus(status.getStatus()));

        editItemButton = new Button(MyReplayCommand.create("EditItem", "", Icons.iconEditSymbolLabelStyle, (e) -> {
            putEditedValues2(parseIdMap2, timerStack.currEntry.timedItem); //first update Item with any values changed in Timer
            ScreenItem screenItem = new ScreenItem(timerStack.currEntry.timedItem, ScreenTimer.this, () -> {
                DAO.getInstance().save(timerStack.currEntry.timedItem);
                refreshItemEditFields(timerStack.currEntry.timedItem);
                ScreenTimer.this.revalidate();
            });
            screenItem.show();
        }
        ));
        cont.add(BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton));

        remainingEffort = new MyDurationPicker(parseIdMap2, () -> (int) timerStack.currEntry.timedItem.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS,
                (i) -> timerStack.currEntry.timedItem.setRemainingEffort(((long) i) * MyDate.MINUTE_IN_MILLISECONDS));
//        remainingEffort.setFormatter(myFormatter); //don't format with seconds

        //If go to nexttask
//        timerStartStopButton.setCommand(startPauseTimerCmd); //TODO change font for timer
//        timerStartStopButton = new Button();
        timerStartStopButton = new Button(Command.create(null, null, (e) -> {
            if (timer == null) {
                reStartTimer();
            } else {
                stopTimer();
            }
        }));
//        timerStartStopButton.setIcon(iconTimerPauseTimer);
        if (isTimerRunning()) {
            timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
        } else {
            timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle);
        }
        elapsedTimePicker = new Picker();
        elapsedTimePicker.setUIID("TimerTimer");
        elapsedTimePicker.setType(Display.PICKER_TYPE_TIME);
//        elapsedTimePicker.setTime((int) timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis / MyDate.MINUTE_IN_MILLISECONDS);
        elapsedTimePicker.setTime((int) timerStack.currEntry.getTimerDurationInMillis() / MyDate.MINUTE_IN_MILLISECONDS);
        elapsedTimePicker.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                //whenever elsapsedTimePicker is edited manually (only possible when timer is Paused), update elapsed time afterwards
//                timerStack.currEntry.timerElapsedTimeSavedDuringPauseMillis = elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS + timerStack.currEntry.secondsInMillis;
                timerStack.currEntry.setTimerDurationInMillis(((long) elapsedTimePicker.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + timerStack.currEntry.savedSecondsFromTimerInMillis);
//                updateTotalActualEffort(timerStack.currEntry.timedItem.getActualEffort(false), elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS);
                updateTotalActualEffort();
            }
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {

//            if (MyPrefs.getBoolean(MyPrefs.timerShowSecondsInTimer)) {
//                elapsedTimePicker.setFormatter(new SimpleDateFormat("HH:MM:ss"));
//            } else {
//                elapsedTimePicker.setFormatter(new SimpleDateFormat("HH:MM"));
//            }
//        elapsedTimePicker.setFormatter(myFormatter); //only use myFormatter when updating the picker manually
//        }
//        boolean useTotalInTimer = MyPrefs.getBoolean(MyPrefs.timerShowTotalActualInTimer);
//</editor-fold>
        parseIdMap2.put(elapsedTimePicker, () -> {
            if (timerStack.currEntry.timerShowsActualTotal) {
//                entry.timedItem.setActualEffort(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS + entry.secondsInMillis - entry.secondsInMillis >= MyDate.MINUTE_IN_MILLISECONDS / 2 ? 1 : 0); //remove rounded up minutes when saving
                if (isMinimumThresholdPassed()) {
                    timerStack.currEntry.timedItem.setActualEffort(((long) elapsedTimePicker.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + timerStack.currEntry.savedSecondsFromTimerInMillis); //remove rounded up minutes when saving
                }
            } else //                entry.timedItem.addToActualEffort(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS + entry.secondsInMillis - entry.secondsInMillis >= MyDate.MINUTE_IN_MILLISECONDS / 2 ? 1 : 0); //remove rounded up minutes when saving
            {
                if (isMinimumThresholdPassed()) {
//                    timerStack.currEntry.timedItem.addToActualEffort(((long) elapsedTimePicker.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + timerStack.currEntry.savedSecondsFromTimerInMillis); //no round up minutes when saving, but add seconds even when not shown in picker
long addlEffort = ((long) elapsedTimePicker.getTime()) * MyDate.MINUTE_IN_MILLISECONDS + timerStack.currEntry.savedSecondsFromTimerInMillis;
                    timerStack.currEntry.timedItem.setActualEffort(timerStack.currEntry.timedItem.getActualEffortProjectTaskItself()+addlEffort); //no round up minutes when saving, but add seconds even when not shown in picker
                }
            }
        });

        effortEstimate = new MyDurationPicker(parseIdMap2, () -> (int) timerStack.currEntry.timedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS,
                (i) -> timerStack.currEntry.timedItem.setEffortEstimate(((long) i) * MyDate.MINUTE_IN_MILLISECONDS));
//        totalEffort = new MyDurationPicker(parseIdMap2, () -> (int) item.getEffortEstimateInMinutes(), (i) -> item.setEffortEstimateInMinutes((int) i));
//        totalActualEffort = new Label("", "TextField"); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");
        totalActualEffort = new Label(); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");

//        updateTotalActualEffort(timerStack.currEntry.timedItem.getActualEffort(false), elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS);
        updateTotalActualEffort();

        //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set. 
        ActionListener estimateAndRemainingUpdater = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //update estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
                if (remainingEffort.getTime() != 0 && timerStack.currEntry.timedItem.getEffortEstimate() == 0 && effortEstimate.getTime() == 0) {
                    effortEstimate.setTime(remainingEffort.getTime());
                    effortEstimate.repaint();
                }
                //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                if (effortEstimate.getTime() != 0 && timerStack.currEntry.timedItem.getRemainingEffortNoDefault() == 0 && remainingEffort.getTime() == 0) {
                    remainingEffort.setTime(effortEstimate.getTime());
                    remainingEffort.repaint();
                }
            }
        };
        remainingEffort.addActionListener(estimateAndRemainingUpdater);
        effortEstimate.addActionListener(estimateAndRemainingUpdater);

//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(FlowLayout.encloseCenter(elapsedTimePicker, timerStartStopButton));
//        cont.add(tl.createConstraint().verticalSpan(2), BoxLayout.encloseY(new Label("Remaining time"), remainingEffort));  //TODO!!: reuse same strings as from ScreenItem!
//        cont.add(BorderLayout.west(BoxLayout.encloseY(new Label("Remaining time"), remainingEffort)).add(BorderLayout.EAST, FlowLayout.encloseCenter(elapsedTimePicker, timerStartStopButton)));  //!!: reuse same strings as from ScreenItem!
//        cont.add(LayeredLayout.encloseIn(BorderLayout.center(elapsedTimePicker).add(BorderLayout.EAST, timerStartStopButton), FlowLayout.encloseRightMiddle(new Button(Icons.iconShowMoreLabelStyle))));  //!!: reuse same strings as from ScreenItem!
//        TableLayout tlt = new TableLayout(1, 3);
//        Container timerTable = new Container(tlt);
//        timerTable.add(tlt.createConstraint().widthPercentage().horizontalAlign(Component.CENTER), new Label("Estimate"));
//          BorderLayout timerLayout = new BorderLayout();
//          timerLayout.setCenterBehavior(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE);
//          Container timerCont = new Container(timerLayout);
//          timerCont.setC.west(BoxLayout.encloseY(new Label("Remaining time"), remainingEffort)).add(BorderLayout.EAST, FlowLayout.encloseCenter(elapsedTimePicker, timerStartStopButton)));  //!!: reuse same strings as from ScreenItem!
//        cont.add(timerCont);
//</editor-fold>
        TableLayout tl = new TableLayout(2, 3);
        final Container estimateTable = new Container(tl);
        Container effortDetailsCont = null;
//        Button showEffortDetailsButton = new Button(Command.create(null, Icons.iconShowMoreLabelStyle, (e)-> {
        Button showEffortDetailsButton = new Button(Icons.iconShowMoreLabelStyle);

        showEffortDetailsButton.addActionListener(
                (e) -> {
                    showEffortDetailsButton.setIcon(showEffortDetailsButton.getIcon() == Icons.iconShowMoreLabelStyle ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle);
                    estimateTable.setHidden(!estimateTable.isHidden());
                    MyPrefs.flipBoolean(MyPrefs.timerShowEffortEstimateDetails);
//                repaint(); //DOESN'T SUFFICE
//            revalidate();
//                    animateHierarchy(300);
                    animateLayout(300);
                }
        );
        effortDetailsCont = LayeredLayout.encloseIn(
                FlowLayout.encloseRightMiddle(showEffortDetailsButton), //!!: reuse same strings as from ScreenItem!
                GridLayout.encloseIn(3, new Label(""), elapsedTimePicker, FlowLayout.encloseIn(timerStartStopButton))
        );

        cont.add(effortDetailsCont);

//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(GridLayout.encloseIn(3, FlowLayout.encloseCenterMiddle(new Label("Estimate")), new Label("Total"), new Label("Remaining"), effortEstimate, totalEffort, remainingEffort));  //!!: reuse same strings as from ScreenItem!
//        estTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label("Estimate"));
//        estTable.add(tl.createConstraint().widthPercentage(34).horizontalAlign(Component.CENTER), new Label("Total"));
//        estTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label("Remaining"));
//</editor-fold>
        estimateTable.add(tl.createConstraint().widthPercentage(33).verticalAlign(Component.CENTER), new Label(Item.EFFORT_ESTIMATE_SHORT)); //"Estimate")); //leftalign labels (like the Tickers)
        estimateTable.add(tl.createConstraint().widthPercentage(34).verticalAlign(Component.CENTER), new Label(Item.EFFORT_TOTAL_SHORT)); //"Total"));
        estimateTable.add(tl.createConstraint().widthPercentage(33).verticalAlign(Component.CENTER), new Label(Item.EFFORT_REMAINING_SHORT)); //"Remaining"));
        //TODO make the effort Pickers small (size as the time, not as the cell) and centered (and center the labels above again)
        estimateTable.add(effortEstimate).add(totalActualEffort).add(remainingEffort);  //!!: reuse same strings as from ScreenItem!
        estimateTable.setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowEffortEstimateDetails)); //hide initially
        cont.add(BorderLayout.center(estimateTable));

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (true || itemList != null) {
//            cont.add(
//                    LayeredLayout.encloseIn(
//                            //                    BoxLayout.encloseX(new Label("List/Project"), new Label(itemList.getText())),
//                            BorderLayout.west(new Label("List/Project")).add(BorderLayout.EAST, new Label("Project task descrp")),
//                            FlowLayout.encloseBottom(new Button(Command.create(null, Icons.iconShowMoreLabelStyle, (e) -> {
//                            })))));
//        }
//        comment = new MyTextField(Item.COMMENT, 20, TextArea.ANY, parseIdMap2, () -> item.getComment(), (s) -> item.setComment(s)); //DONE make notes field two rows tall and set MaxSize for PRO subscribers!!
//</editor-fold>
        comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY, parseIdMap2,
                () -> timerStack.currEntry.timedItem.getComment(), (s) -> timerStack.currEntry.timedItem.setComment(s));
        Container commentContainer = ScreenItem.makeCommentContainer(comment);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container taskCont = BorderLayout.center(description);
//        taskCont.add(BorderLayout.EAST, BoxLayout.encloseX(status, editItemButton));
//        BorderLayout.center(description).add(BorderLayout.EAST, BoxLayout.encloseX(status, editItemButton));
//        Button addTimeStampToComment = new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {
//            comment.setText(Item.addTimeToComment(comment.getText()));
////                    comment.setstartEditing(); //TODO how to position cursor at end of text (if not done automatically)?
////comment.setCursor //only on TextField, not TextArea
//            comment.startEditing(); //TODO in CN bug db #1827: start using startEditAsync() is a better approach
//        }));
//</editor-fold>
//        Button addTimeStampToComment = makeAddTimeStampToCommentAndStartEditing(comment);

//        cont //DONE!!: reuse same label strings as from ScreenItem!
//                .add(FlowLayout.encloseIn(new Label(Item.COMMENT), addTimeStampToComment))
//                //                .add(BorderLayout.center(LayeredLayout.encloseIn(comment, FlowLayout.encloseRightMiddle(new Button(Command.create(null, Icons.iconAddTimeStampToCommentLabelStyle, (e) -> {})))))
//                //                .add(BorderLayout.center(comment).add(BorderLayout.EAST, new Button(Icons.iconEditSymbolLabelStyle)))); //TODO add full screen edit for Notes
//                .add(BorderLayout.center(comment)); //TODO add full screen edit for Notes
        cont.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes

        //Action buttons
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.add(tl.createConstraint().horizontalAlign(2), new Button("Completed"));
//        cont.add(tl.createConstraint().horizontalAlign(2), BorderLayout.center(BoxLayout.encloseX(new Button("Exit"), new Button("Wait"), new Button("Stop"))));
//        cont.add(BorderLayout.center(BoxLayout.encloseX(new Button("Exit"), new Button("Wait"), new Button("Stop"))));
//        cont.add(new Button("Completed"));
//</editor-fold>
        //Show interrupted tasks
//        if (timerStack.currEntry.interruptOrInstantTask) {
        int textPos = Button.RIGHT; //BOTTOM;
        if (timerStack.currEntry.timedItem.isInteruptOrInstantTask()) {
            Item interruptedItem = timerStack.getInterruptedItem();
//            if (timerStack.size() >= 2) { //a task was interrupted
            if (interruptedItem != null) { //a task was interrupted
                //UI: not possible to Exit Timer when an interrupted task is pending, must first deal w ith interrupt and then chose an action on the interrupted task
                Button c1 = new Button(cmdStopTimerAndGotoNextTaskOrExit);//"Next"),
                c1.setTextPosition(textPos);
                Button c2 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
                c2.setTextPosition(textPos);
                Button c3 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                c3.setTextPosition(textPos);
//                cont.add(GridLayout.encloseIn(3, c1, c2, c3));
                cont.add(GridLayout.encloseIn(2, c1, c2)); //autofit
                cont.add(GridLayout.encloseIn(1, c3)); //autofit
                //UI: as long as there is interrupted task(s!) show only those (not next tasks)
//                cont.add(new SpanLabel("Interrupted: " + timerStack.getInterruptedEntry().timedItem.getText()));
                cont.add(new SpanLabel("Interrupted: " + interruptedItem.getText()));
            } else {
                assert timerStack.size() == 1 : "timerStack should always be size==1 when no tasks was interrupted";
                Button c4 = new Button(cmdExitTimer); //"Stop"),
                c4.setTextPosition(textPos);
                Button c5 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
                c5.setTextPosition(textPos);
                Button c6 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                c6.setTextPosition(textPos);
//                cont.add(GridLayout.encloseIn(3, c4, c5, c6));
//                cont.add(GridLayout.encloseIn(c4, c5, c6));
                cont.add(GridLayout.encloseIn(2, c4, c5));
                cont.add(GridLayout.encloseIn(1, c6));
//<editor-fold defaultstate="collapsed" desc="comment">
//no task was interrupted
//                cont.add(GridLayout.encloseIn(3,
////                    new Button("Stop&Exit"), //UI: not possible to exit Timer when an interrupted task is pending, must first deal w ith interrupt and then chose an action on the interrupted task
//                        new Button(cmdStopTimerAndGotoNextTaskOrExit), //"Next"/"Stop"),
//                        new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit), //"Wait"),
//                        new Button(cmdMarkDoneAndGotoNextTaskOrExit))); //"Completed")));
//</editor-fold>
            }
//        } else if (entry.nextItem == null || !MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
        } else if (timerStack.currEntry.nextItem == null) {
            //DONE!!! use task checkbox to mark task Done/Waiting? Only keep Exit/Next to leave it ongoing
//            cont.add(GridLayout.encloseIn(3, new Button("Exit"), new Button("Wait"), new Button("Stop"), new Button("Completed"))); //TODO enclose in auto-sizing layout (TableLayout won't expand the colums to fill the screen)
            Button c7 = new Button(cmdSaveAndExit); //"Exit"),
            c7.setTextPosition(textPos);
            Button c8 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit);
            c8.setTextPosition(textPos);
            Button c9 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
            c9.setTextPosition(textPos);
//            cont.add(GridLayout.encloseIn(3, c7, c8, c9));
//            cont.add(GridLayout.encloseIn(c7, c8, c9));
            cont.add(GridLayout.encloseIn(2, c7, c8));
            cont.add(GridLayout.encloseIn(1, c9));
//            cont.add(new SpanLabel("Exit: stop and save Timer, then exit. Wait: set current task Waiting. Stop: stop Timer. All: Stop Timer and save elapsed time. Tip: use task checkbox"));
        } else {
//        entry.nextItem != null //MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask) && 
            //nextItem!=null AND 
            Button c10 = new Button(cmdSaveAndExit); //"Exit"),
            c10.setTextPosition(textPos);
            Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"), 
            c11.setTextPosition(textPos);
            Button c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next", 
            c12.setTextPosition(textPos);
            Button c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
            c13.setTextPosition(textPos);
//            cont.add(GridLayout.encloseIn(4, c10, c11, c12, c13));
//            cont.add(GridLayout.encloseIn(c10, c11, c12, c13));
            cont.add(GridLayout.encloseIn(3, c10, c11, c12));
            cont.add(GridLayout.encloseIn(1, c13));
//            cont.add(new SpanLabel("Exit: save elapsted time and exit Timer. \nWait: set current task Waiting and start next task. Stop: stop Timer and start next task. Completed: mark task as completed and start next task. All: Stop Timer and save elapsed time. Tip: use task checkbox"));
            //Show next tasks
            //TODO optimization: only construct nextTask containers etc if there is one and it is shown
            //TODO show button to select auto-start Timer on task or not

//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                Container nextTaskCont = new Container(BoxLayout.y());
//                OnOffSwitch showNextTaskOnOff = new OnOffSwitch();
//                showNextTaskOnOff.setValue(MyPrefs.getBoolean(MyPrefs.timerShowNextTask));
//
//                OnOffSwitch autoGotoNextTaskOnOff = new OnOffSwitch();
//                autoGotoNextTaskOnOff.setValue(MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask));
////            Button gotoNextTaskButton = new Button(cmdGotoNextTask); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
//                SpanLabel nextTaskLabel = new SpanLabel("Next: \"" + timerStack.currEntry.nextItem.getText() + "\"");
//                Button gotoNextTaskButtonWithItemText = new Button("Next: \"" + timerStack.currEntry.nextItem.getText() + "\""); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
//                gotoNextTaskButtonWithItemText.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
//                        processCurrentItemAndLaunchNextOrExit(null, false, false, true);
//                    }
//                });
//                Button gotoNextTaskButton = new Button("Next task"); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
//                gotoNextTaskButton.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
//                        processCurrentItemAndLaunchNextOrExit(null, false, false, true);
//                    }
//                });
//
////            nextTaskCont.add("Next: \"" + entry.nextItem.getText() + "\"");
//                nextTaskCont.add(nextTaskLabel);
////            nextTaskCont.setHidden(!showNextTaskOnOff.isValue());
//                nextTaskCont.add(gotoNextTaskButton);
//                nextTaskCont.add(gotoNextTaskButtonWithItemText);
////            gotoNextTaskButton.setHidden(showNextTaskOnOff.isValue());
////            gotoNextTaskButtonWithItemText.setHidden(autoGotoNextTaskOnOff.isValue());
//
//                ActionListener updateNextTaskButtonsLabelsListener = new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
//                        //   FALSE  ShowNextTask  TRUE           AutoGotoNext
//                        //   [NextTask]           [Task text]       FALSE
//                        //       -                Task text         TRUE
//                        nextTaskLabel.setHidden(true);
//                        gotoNextTaskButtonWithItemText.setHidden(true);
//                        gotoNextTaskButton.setHidden(true);
//                        if (showNextTaskOnOff.isValue()) {
//                            if (autoGotoNextTaskOnOff.isValue()) {
//                                nextTaskLabel.setHidden(false);
//                            } else {
//                                gotoNextTaskButtonWithItemText.setHidden(false);
//                            }
//                        } else if (autoGotoNextTaskOnOff.isValue()) {
//                            //don't show anything
//                        } else {
//                            gotoNextTaskButton.setHidden(false);
//                        }
//                    }
//                };
//                updateNextTaskButtonsLabelsListener.actionPerformed(null); //set to correct initial values
//
//                showNextTaskOnOff.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
////                    MyPrefs.setBoolean(MyPrefs.timerShowNextTask, !showNextTaskOnOff.isValue()); //always save settings
//                        MyPrefs.flipBoolean(MyPrefs.timerShowNextTask); //always save settings
////                    nextTaskCont.setHidden(!nextTaskCont.isHidden());
////                    nextTaskCont.setHidden(!showNextTask.isValue());
////                    gotoNextTaskButton.setHidden(showNextTask.isValue());
////                    gotoNextTaskButtonWithItemText.setHidden(!showNextTask.isValue());
//                        updateNextTaskButtonsLabelsListener.actionPerformed(null);
////                    revalidate();
//                        animateHierarchy(300);
////                gotoNextTask.setValue(!gotoNextTask.isValue());
//                    }
//                });
//
//                autoGotoNextTaskOnOff.addActionListener(new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
////                    MyPrefs.setBoolean(MyPrefs.timerAutomaticallyGotoNextTask, autoGotoNextTaskOnOff.isValue()); //always save settings
//                        MyPrefs.flipBoolean(MyPrefs.timerAutomaticallyGotoNextTask); //always save settings
////                    nextTaskCont.setHidden(!autoGotoNextTask.isValue()); //hide next task
////                    gotoNextTaskButton.setHidden(!gotoNextTaskButton.isHidden());
////                    nextTaskLabel.setHidden(!autoGotoNextTask.isValue()); //show next tasks as a button
////                    gotoNextTaskButton.setHidden(autoGotoNextTask.isValue()); //show next tasks as a button
//                        updateNextTaskButtonsLabelsListener.actionPerformed(null);
//                        animateHierarchy(300);
//                    }
//                });
//
////            cont.add(BorderLayout.west(new Label("Continue with next task"))
////            cont.add(BorderLayout.north(new Label("Next task"))
//                cont.add(BorderLayout.north(nextTaskCont)
//                        .add(BorderLayout.WEST, FlowLayout.encloseIn(new Label("Show next"), showNextTaskOnOff))
//                        .add(BorderLayout.EAST, FlowLayout.encloseIn(new Label("Auto next"), autoGotoNextTaskOnOff))) //                    .add(BorderLayout.CENTER, BoxLayout.encloseY(FlowLayout.encloseIn(new Label("Show next"), showNextTask),
//                        //                            FlowLayout.encloseIn(new Label("Goto next"), autoGotoNextTask))) //"Goto next", "Auto next"
//                        ;
//            } else
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (MyPrefs.getBoolean(MyPrefs.timerShowNextTask)) {
////                    Container nextTaskCont = new Container(BoxLayout.y());
//                String itemText = timerStack.currEntry.nextItem.getText();
//                if (itemText.length() == 0) {
//                    itemText = "<no text>";
//                }
//                String nextTaskString = "Next: \"" + timerStack.currEntry.nextItem.getText() + "\"";
//
//                if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                    //show as a label
//                    SpanLabel nextTaskLabel = new SpanLabel(nextTaskString);
////                        nextTaskCont.add(nextTaskLabel);
//                    cont.add(nextTaskLabel);
//                } else {
//show as a button to manually go to next task
//                    SpanButton gotoNextTaskButtonWithItemText = new SpanButton(nextTaskString); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
//                    gotoNextTaskButtonWithItemText = new SpanButton(nextTaskString); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
//</editor-fold>
            gotoNextTaskButtonWithItemText = new SpanButton("Next task: \"" + timerStack.currEntry.nextItem.getText() + "\""
                    + (MyPrefs.timerShowNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(timerStack.currEntry.nextItem.getRemainingEffort()) + "]") : "")); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
            gotoNextTaskButtonWithItemText.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (!MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) { //disable action if automatically going to next action
                        processCurrentItemAndLaunchNextOrExit(null, false, false, true);
                    }
                }
            });
            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                gotoNextTaskButtonWithItemText.setUIID("Label");
                gotoNextTaskButtonWithItemText.setTextUIID("Label");
            }
            gotoNextTaskButtonWithItemText.setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowNextTask));
//                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
            cont.add(gotoNextTaskButtonWithItemText);
//<editor-fold defaultstate="collapsed" desc="comment">
//                }
//            } //                cont.add(BorderLayout.north(nextTaskCont));
//            nextTaskCont.add(new Label("next task"));
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            Item nextItem = getNextItemToStartTimerOnXXX(item);
//            //show interrupted tasks if any
//            //if no interrupted tasks, show next task if any
//            if (false) {
//                nextTaskLabel = new SpanLabel();
//                if (nextItem != null) {
//                    cont.add(completedNextItemButton = new Button(cmdMarkDoneAndStartNextTaskOrExitIfNoneXXX))
//                            .add(nextItemButton = new Button(exitTimerWhileWorkingOnItemList))
//                            .add(nextItemButton = new Button(gotoNextTaskOrExitIfNoneXXX));
//                    updateNextButtonsXXX(); //ensure right labels wrt setting for Auto-start next
//                    nextTaskLabel.setText("Next: \"" + nextItem.getText() + "\"");
//                    cont.add(nextTaskLabel);
//                } else {
//                    //only a sinle task
//                    cont.add(new Button(cmdMarkDoneAndGotoNextTask))
//                            .add(FlowLayout.encloseCenter(new Button(setTaskWaiting), new Button(cmdSaveAndExit)));
//                    if (itemList != null) { //there are no more items to work on in the current list
//                        nextTaskLabel.setText("No more tasks in \"" + itemList.getText() + "\"");
//                        cont.add(nextTaskLabel);
//                    }
//                }
//            }
//        }
//</editor-fold>
        }
        return contentPane;
    }

    @Override
    public void saveEditedValuesLocallyOnAppExit() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (item.getObjectIdP() == null) { //new item, save everything locally and restore next time
////            Storage.getInstance().writeObject(SCREEN_TITLE + "- EDITED ITEM", item); //save date
//            Storage.getInstance().writeObject(FILE_LOCAL_EDITED_ITEM, item); //save
//
//        } else { //edited item, update item but only save locally, then restore edit fields based on locally saved values
//            putEditedValues2(parseIdMap2);
//        }
//        localSave = true;
//        putEditedValues2(parseIdMap2);
//        description.setText(updatedItem.getText());
//        status.setStatus(updatedItem.getStatus());
//        comment.setText(updatedItem.getComment());
//        effortEstimate.setTime((int) updatedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
//        remainingEffort.setTime((int) updatedItem.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS);
//</editor-fold>
        if (!description.getText().equals(timerStack.currEntry.timedItem.getText())) {
            Storage.getInstance().writeObject("ScreenTimerLocalEdit.description", description.getText()); //save 
        }
        if (!comment.getText().equals(timerStack.currEntry.timedItem.getComment())) {
            Storage.getInstance().writeObject("ScreenTimerLocalEdit.comment", comment.getText()); //save 
        }
        if (!status.getStatus().equals(timerStack.currEntry.timedItem.getStatus())) {
            Storage.getInstance().writeObject("ScreenTimerLocalEdit.statusStr", status.getStatus().toString()); //save 
        }
        if (effortEstimate.getTime() != (timerStack.currEntry.timedItem.getEffortEstimate())) {
            Storage.getInstance().writeObject("ScreenTimerLocalEdit.effortEstimate", effortEstimate.getTime()); //save 
        }
        if (remainingEffort.getTime() != (timerStack.currEntry.timedItem.getRemainingEffort())) {
            Storage.getInstance().writeObject("ScreenTimerLocalEdit.remainingEffort", remainingEffort.getTime()); //save 
        }
//        localSave = false;
    }

    @Override
    public boolean restoreEditedValuesSavedLocallyOnAppExit() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Item itemLS = null;
//        //if editing of item was ongoing when app was stopped, then recover saved item
//        ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM) || ReplayLog.getInstance().isReplayInProgress()); //local item => replay must/should be Ongoing
//        if (ReplayLog.getInstance().isReplayInProgress() && Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM)) {
//            itemLS = (Item) Storage.getInstance().readObject(FILE_LOCAL_EDITED_ITEM); //read in when initializing the Timer - from here on it is only about saving updates
//        } else {
////            itemLS = this.item; //it no locally saved edits, then use item to 'feed' the edits fields
//            ASSERT.that(!Storage.getInstance().exists(FILE_LOCAL_EDITED_ITEM));
//            deleteLocallyEditedValuesOnAppExit();
//        }
//        return itemLS;

//        putEditedValues2(parseIdMap2);
//        description.setText(updatedItem.getText());
//        status.setStatus(updatedItem.getStatus());
//        comment.setText(updatedItem.getComment());
//        effortEstimate.setTime((int) updatedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
//        remainingEffort.setTime((int) updatedItem.getRemainingEffortNoDefault() / MyDate.MINUTE_IN_MILLISECONDS);
//</editor-fold>
        boolean savedValues = false;
        Object r = Storage.getInstance().readObject("ScreenTimerLocalEdit.description"); //save 
        if (r != null) {
            description.setText((String) r); //save 
            savedValues = true;
        }
        r = Storage.getInstance().readObject("ScreenTimerLocalEdit.comment"); //save 
        if (r != null) {
            comment.setText((String) r);
            savedValues = true;
        }
        r = Storage.getInstance().readObject("ScreenTimerLocalEdit.statusStr"); //save 
        if (r != null) {
            status.setStatus((String) r);
            savedValues = true;
        }
        r = Storage.getInstance().readObject("ScreenTimerLocalEdit.effortEstimate"); //save 
        if (r != null) {
            effortEstimate.setTime((int) r);
            savedValues = true;
        }
        r = Storage.getInstance().readObject("ScreenTimerLocalEdit.remainingEffort"); //save 
        if (r != null) {
            remainingEffort.setTime(((int) r));
            savedValues = true;
        }
        return savedValues;
    }

    @Override
    public void deleteEditedValuesSavedLocallyOnAppExit() {
//        Storage.getInstance().deleteStorageFile(FILE_LOCAL_EDITED_ITEM); //delete in case one was 
        Storage.getInstance().deleteStorageFile("ScreenTimerLocalEdit.description"); //save 
        Storage.getInstance().deleteStorageFile("ScreenTimerLocalEdit.comment"); //save 
        Storage.getInstance().deleteStorageFile("ScreenTimerLocalEdit.statusStr"); //save 
        Storage.getInstance().deleteStorageFile("ScreenTimerLocalEdit.effortEstimate"); //save 
        Storage.getInstance().deleteStorageFile("ScreenTimerLocalEdit.remainingEffort"); //save 

    }

}
