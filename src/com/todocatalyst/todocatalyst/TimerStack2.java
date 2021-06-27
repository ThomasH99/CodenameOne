/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.ToastBar;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.util.EventDispatcher;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author thomashjelm
 */
class TimerStack2 implements ActionListener {
    //TODOlist from ScreenItem2:
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
    //DONE Imp: add small Timer container to other screens (and/or change Timer symbol to show it is running - no, best to be cclearly reminded about what task is running to not loose focus)
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
//    private final static String TIMER_STACK_ID = "TodoCatalystStoredTimers";
    private List<TimerInstance2> activeTimers; //is NOT saved since it is set by reading the list of saved TimerInstances
    Container smallContainer = null;
    final static int ACTION_EVENT_CHANGED = DataChangedListener.CHANGED;
    final static int ACTION_EVENT_REMOVEDXXX = DataChangedListener.REMOVED;

    EventDispatcher listeners;
//    boolean timeEvenInvalidItem = false;
//    private List<TimerStackEntry> previouslyRunningTimers;
//    TimerStackEntry currEntry;

//    private static List<TimerInstance> INSTANCE;
    private static TimerStack2 INSTANCE = null;
    final static String TIMER_REPLAY = "StartTimer-";
    final static String SMALL_TIMER_TEXT_AREA_TO_START_EDITING = "SmallTimerTextArea";
    final static String SMALL_TIMER_CONTAINER_ID = "$$SmallTimerContainer";
    final static long TIMER_MAX_VALUE = MyDate.HOUR_IN_MILISECONDS * 99 + MyDate.MINUTE_IN_MILLISECONDS * 59 + MyDate.SECOND_IN_MILLISECONDS * 59;

    final static String TIMER_START = "Start";
    final static String TIMER_PAUSE = "Pause";

    final static Object TIMER_LOCK = new Object(); //lock operations on Timer such as updating/saving instances or refreshing Timers from Server

    public static TimerStack2 getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new TimerInstance();
//            DAO.getInstance().getTemplateList();
            INSTANCE = new TimerStack2(DAO.getInstance().getTimerInstanceList2());
            addAsListener(INSTANCE.activeTimers);
        }
        return INSTANCE;
    }

    /**
     * show a popup about no more tasks in project or itemList (if both are
     * defined, use the name of the itemList). Depends on setting!
     *
     * @param project
     * @param itemList
     */
    static void showNoTasksToWorkOnNotificationWhenRelevantOLD(Item project, ItemList itemList) {
        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
            if ((project != null && project.isProject()) || itemList != null) { //only show if item or itemList are defined
                String itemOrListName = (itemList != null ? itemList.getText() : project.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
                //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
                Dialog.show("큈imer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
            }
        }
    }

    static void showNoTasksToWorkOnNotificationWhenRelevant(ItemAndListCommonInterface itemOrListName) {
//    static void showNoTasksToWorkOnNotificationWhenRelevant(String itemOrListName) {
        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("큈imer", "No tasks to work on in \"" + itemOrListName.getText() + "\"", "OK", null);
        }
    }

    static void showTimerSourceIsAlreadyBeingTimed(ItemAndListCommonInterface itemOrListName) {
        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("큈imer", Format.f("\"{0 timerSource}\" is already being timed", itemOrListName.getText()), "OK", null);
        }
    }

    static void showTimedItemIsAlreadyBeingTimed(Item itemOrListName) {
        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("큈imer", Format.f("\"{0 timedItem}\" is already being timed", itemOrListName.getText()), "OK", null);
        }
    }

    /**
     * don't show for Interrupt/InstantTasks
     *
     * @param project
     * @param itemList
     */
    static void showTimerAlreadyRunningOnListOrProjectCloseFirstNotification(Item project, ItemList itemList) {
//        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
        if ((project != null && project.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = (itemList != null ? itemList.getText() : project.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("큈imer", "Timer already active for \"" + itemOrListName + "\". Stop before starting on another ", "OK", null);
        }
    }

    static void showTimerWasAlreadyRunningForTaskTimeSavedNotification(Item timedItem, long elapsedTimeMillis, Item project, ItemList itemList) {
        ToastBar.Status status = ToastBar.getInstance().createStatus();
//        String itemOrListName = (itemList != null ? itemList.getText() : timedItem.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
        String itemOrListName = timedItem.getText(); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
        //TODO!! show how much time was added, consider if showing total or additional
//        status.setMessage("Timer for \"" + itemOrListName + "\" saved and time updated with "+(int)(elapsedTimeMillis/MyDate.MINUTE_IN_MILLISECONDS));
        status.setMessage("Timer saved for \"" + itemOrListName + "\"");
        status.setExpires(2000);
        status.show();
    }

//    private static void showTimerCannotBeStarted(boolean interruptInterruptingInterrupt, boolean itemOrItemListInterruptingItemOrItemList, Item timedItem) {
    static void showTimerCannotBeStarted(boolean interruptInterruptingInterrupt, Item timedItem) {
        ToastBar.Status status = ToastBar.getInstance().createStatus();
//        String itemOrListName = (itemList != null ? itemList.getText() : timedItem.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
//        String itemOrListName = timedItem.getText(); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
        //TODO!! show how much time was added, consider if showing total or additional
//        status.setMessage("Timer for \"" + itemOrListName + "\" saved and time updated with "+(int)(elapsedTimeMillis/MyDate.MINUTE_IN_MILLISECONDS));
        if (interruptInterruptingInterrupt) {
            status.setMessage("You cannot start an interrupt when another interrupt is already being timed: \"" + timedItem.getText() + "\"");
        } else {//if (itemOrItemListInterruptingItemOrItemList) {
            status.setMessage("You cannot start timing a task, project or list when another is already being timed: \"" + timedItem.getText() + "\"");
        }
//        else {
//            status.setMessage("ERROR");
//        }
        status.setExpires(2000);
        status.show();
    }

    static boolean showDialogPauseActiveTimer(TimerInstance2 timerInstance, ItemAndListCommonInterface newTimedElt) {
//        TimerInstance2 timerInstance = TimerStack2.getInstance().getCurrentTimerInstanceN();
        String timerText = "";
//        if (timerInstance.getTimedItemListN() != null) {
        if (timerInstance.getTimerSourceN() != null) {
            timerText = timerInstance.getTimerSourceN().getText();
        } else if (timerInstance.getTimedItemN() != null) {
            timerText = timerInstance.getTimedItemN().getText();
        }
        if (!timerText.equals("")) {
            timerText = " for \"" + timerText + "\"";
        }
        return Dialog.show("TIMER", "Pause timer" + timerText + " and start timing "
                + (newTimedElt instanceof Item && ((Item) newTimedElt).isInteruptOrInstantTask() ? "Interrupt" : "\"" + newTimedElt.getText() + "\"?"),
                "OK", "Cancel");
    }

    static void showNoMoreTasksDialogWhenRelevant(Item item, ItemList itemList) {
        if ((item != null && item.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = item != null ? item.getText() : itemList.getText();
            Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
        }
    }

    public static TimerInstance2 getTimerInstanceN() {
        return getInstance().getCurrentTimerInstanceN();
    }

    public static Item getTimedItemN() {
        TimerInstance2 timerInstance = getInstance().getCurrentTimerInstanceN();
        Item timedItem = timerInstance != null ? timerInstance.getTimedItemN() : null;
        return timedItem;
    }

    private TimerStack2(List<TimerInstance2> timerList) {
        activeTimers = timerList;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (Storage.getInstance().exists(TIMER_STACK_ID)) {
//            previouslyRunningTimers = (ArrayList) Storage.getInstance().readObject(TIMER_STACK_ID); //read in when initializing the Timer - from here on it is only about saving updates
//            if (previouslyRunningTimers.size() >= 1) {
//                currEntry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//            } else {
//                currEntry = null;
//            }
//        }
//        if (previouslyRunningTimers == null) { //whatever happens, if previouslyRunningTimers==null, then create a new one
//            previouslyRunningTimers = new ArrayList(); //create if none existed before
//            currEntry = null;
////                save(); //DON'T save before something is added
//        }
////            updateEntry();
//        INSTANCE = DAO.getInstance().getTimerInstanceList();
//</editor-fold>
    }

    private static void addAsListener(List<TimerInstance2> timerInstanceList) {
        for (TimerInstance2 timerInstance : timerInstanceList) {
//            if (timerInstance.getTimerSourceN() != null) {
//                timerInstance.getTimerSourceN().addActionListener(getInstance());
//            }
//            if (timerInstance.getTimedItemN() != null) {
//                timerInstance.getTimedItemN().addActionListener(getInstance());
//            }
            if (timerInstance != null) {
                timerInstance.setActionListener(getInstance());
            }
            if (timerInstance != null) {
                timerInstance.setActionListener(getInstance());
            }
        }
    }

    /**
     * only one timer can listen at a time
     *
     * @param l
     */
    public void setActionListener(ActionListener l) {
        if (listeners == null) {
            listeners = new EventDispatcher();
        }
        if (listeners.getListenerCollection() == null
                || listeners.getListenerCollection().isEmpty()
                || l != Arrays.asList(listeners.getListenerCollection()).get(0)) {
            if (listeners.getListenerCollection() != null) { //remove any previous listeners
                listeners.getListenerCollection().clear(); //works because getListenerCollection() returns the underlying listener list direcly
            }
            listeners.addListener(l);
            TimerInstance2 timerInstance = getCurrentTimerInstanceN();
            if (false && timerInstance != null && timerInstance.isRunning()) {
                fireChangedEvent(); //inform new listening screen about status of timer
            }
        } //else //do nothing if re-adding same listener
    }

    public void removeActionListener(ActionListener obj) {
        if (listeners != null) {
            listeners.removeListener(obj);
        }
    }

    private void fireChangedEvent() {
        if (listeners != null) {
            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_CHANGED));
        } else {
            ASSERT.that("TimerStack2.fireChangedEvent, but NO listeners!!");
        }
    }

    private void fireDeletedEventXXX() { //not necessary, ChangedEven is enough
        if (listeners != null) {
            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_REMOVEDXXX));
        } else {
            ASSERT.that("TimerStack2.fireDeletedEvent, but NO listeners!!");
        }
    }

    void refreshTimersFromParse() {
        INSTANCE = null;
//        getInstance(); //next call from MyFrom.refreshAfterEdit() will read in new instance
    }

    public String toString() {
        String s = "[";
        for (int i = activeTimers.size() - 1; i >= 0; i--) {
            s += activeTimers.get(i).toString() + "; ";
        }
        return s + "]";
    }

    /**
     * used to check if a found task (or project!) is valid for the timer (eg if
     * the list may contain Done/Cancelled or Waiting tasks that should be
     * skipped). Also used in TimerInstance. For a project to be valid, there
     * has to at least one valid subtask: if project is ongoing: OK. Waiting:
     * all subasks are Waiting.
     *
     *
     * @param item
     * @return
     */
//    static public boolean isValidItemForTimer(Item item) {
//        return isValidItemForTimer(item, false);
//    }
//    static public boolean isValidItemForTimer(Item item, boolean timeEvenInvalidItem) {
    static public boolean isValidItemForTimer(Item item) {
//        return !item.isDone() || MyPrefs.timerIncludeWaitingTasks.getBoolean() || MyPrefs.timerIncludeDoneTasks.getBoolean();
        if (false) {
            return (item != null
                    //                && (TimerStack.getInstance().timeEvenInvalidItem
                    && ((!item.isDone() && !item.isWaiting()) //valid if not done and not waiting (so this expr is true for all other status values!)
                    //TODO!!!! in Timer: check waiting date when skipping, or not, waiting tasks
                    || (item.isWaiting() && MyPrefs.timerIncludeWaitingTasks.getBoolean()) ////or if waiting, but settings allow to time waiting tasks
                    || (item.isDone() && MyPrefs.timerIncludeDoneTasks.getBoolean()))); //or if done, but settings allow to time done tasks
        } else {
            return item.isValidItemForTimer();
        }
    }

    TimerInstance2 getCurrentTimerInstanceN() {
        int size = activeTimers.size();
        if (size > 0) {
            TimerInstance2 t = activeTimers.get(size - 1); //return last timer
            return t; //return last timer
        }
        return null;
    }

    /**
     * the preferred way to get whatever item is being timed (will also
     * initialize the value if needed). On very first access (after starting the
     * timer), several situations: item==null&list!=null, list==null&item!=null
     * where item can be valid or invalid, project or not project.
     *
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Item getCurrentlyTimedItemNOLD() {
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//            Item timedItem = timerInstance.getTimedItemN();
//            if (timedItem == null) {// || !isValidItemForTimer(timedItem)) { //isValid only relevant if used to initialize a new list
////                goToNextTimedItem();
//                goToNextTimedItemImpl(true);
//                timerInstance = getCurrentTimerInstanceN();
//                if (timerInstance != null) {
//                    timedItem = timerInstance.getTimedItemN();
//                }
//            }
//            return timedItem;
//        }
//        return null;
//    }
//</editor-fold>
    public Item getCurrentlyTimedItemN() {
        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
        if (timerInstance != null) {
            Item timedItem = timerInstance.getTimedItemN();
            return timedItem;
        }
        if (false && Config.TEST) {
            ASSERT.that("timedItem should never be null");
        }
        return null;
    }

    public ItemAndListCommonInterface getCurrentlyTimedSourceN() {
        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
        if (timerInstance != null) {
            ItemAndListCommonInterface timedSource = timerInstance.getTimerSourceN();
            return timedSource;
        }
        return null;
    }

    public Item getNextComingItemN() {
        Item nextTimedItem = null;
        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
        if (timerInstance != null) {
            nextTimedItem = timerInstance.getNextTimedItemN();
        }
        int activeTimerIndex = activeTimers.size() - 2; //start from last-1
        while (nextTimedItem == null && activeTimerIndex >= 0) {
            nextTimedItem = activeTimers.get(activeTimerIndex).getTimedItemN(); //get the first timedItem of the next timerInstance
            activeTimerIndex--;
        }
        return nextTimedItem;
    }

//    public Item getTimedItemNXXX() {
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            return timerInstance.getTimedItemN();
//        } else {
//            return null;
//        }
//    }
//
//    public Item getNextComingTimerItemNXXX() {
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            return timerInstance.getNextTimedItemN();
//        } else {
//            return null;
//        }
//    }
    /**
     * true if Timer is already running and timing something (even if paused)
     *
     * @return
     */
    public boolean isTimerActive() {
        return getCurrentlyTimedItemN() != null;
    }

    public boolean isTimerRunning() {
        return getCurrentlyTimedItemN() != null && getCurrentTimerInstanceN().isRunning();
    }

    public boolean isTimerStackEmpty() {
        return activeTimers == null || activeTimers.isEmpty();
    }

//    public ItemList getCurrentlyTimedItemListNXXX() {
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//            ItemList timedItemList = timerInstance.getTimedItemListN();
//            return timedItemList;
//        }
//        return null;
//    }
    /**
     * find next suitable for timer and (re-)start the timer if it was running
     * when interrupted returns true if timer was started on another item.
     */
    void goToNextTimedItem() {
        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
        Item nextTimedItem = getNextComingItemN();
        while (nextTimedItem == null && !activeTimers.isEmpty()) { //if no more tasks in current timer, try earlier ones
            TimerInstance2 emptyTimerInstance = activeTimers.remove(activeTimers.size() - 1);
            emptyTimerInstance.deleteInstance(); //delete empty timerInstance
            ItemAndListCommonInterface source = emptyTimerInstance.getTimerSourceN();
            String listType = source instanceof Category ? Category.CATEGORY : (source instanceof ItemList ? ItemList.ITEM_LIST : Item.PROJECT);
            MyForm.showToastBar(Format.f("No more tasks in {0 listtype} \"{1 listname}\"", listType, source.getText()));
            if (!activeTimers.isEmpty() && (nextTimedItem = getCurrentlyTimedItemN()) != null) {
                TimerInstance2 timerInstance = getCurrentTimerInstanceN();
                if (timerInstance.isWasInterruptedWhileRunning() || MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                    timerInstance.setWasRunningWhenInterrupted(false, false); //remove runningWhenInterrupted flag (necessary? will always be set explicitly later?)
                    timerInstance.startTimer(true, true); //don't save timerInstance, save updated timedItem
                }
            }
        }
        if (nextTimedItem == null && getTimerInstanceN() != null && getTimerInstanceN().getTimerSourceN() != null) {
//            showNoTasksToWorkOnNotificationWhenRelevant(getTimerInstanceN().getTimerSourceN().getText());
            showNoTasksToWorkOnNotificationWhenRelevant(getTimerInstanceN().getTimerSourceN());
        }

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Item goToNextTimedItemImplOLD() {
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//        Item nextTimedItem = null;
//        int currentTimerStackIndex = activeTimers.size() - 1; //start with last timer
//        while (nextTimedItem == null && currentTimerStackIndex >= 0) { //if no more tasks in current timer, try earlier ones
//            TimerInstance2 timerInstance = activeTimers.get(currentTimerStackIndex);
//            timerInstance.goToNextTimerItemNEW(); //DO NOT update since this is a previous Timer where the current item was interr8pted, so simply continue with that
//            nextTimedItem = timerInstance.getTimedItemN(); //DO NOT update since this is a previous Timer where the current item was interr8pted, so simply continue with that
//            if (nextTimedItem == null) {
//                ItemAndListCommonInterface source = timerInstance.getTimerSourceN();
//                if (source != null) {
//                    String listType = source instanceof Category ? Category.CATEGORY : (source instanceof ItemList ? ItemList.ITEM_LIST : Item.PROJECT);
//                    MyForm.showToastBar(Format.f("No more tasks in {0 listtype} \"{1 listname}\"", listType, source.getText()));
//                }
//            }
//            currentTimerStackIndex--;
//        }
//        currentTimerStackIndex++; //compensate for -- in loop
//        //if update,  discard/delete all skipped (no longer having tasks to time) timers:
//        while (activeTimers.size() - 1 > currentTimerStackIndex || (nextTimedItem == null && activeTimers.size() > 0)) {
//            TimerInstance2 timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            timerInstance.deleteInstance();
//        }
//        return nextTimedItem;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Item goToNextTimedItemImplOLD(boolean update) {
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//
//        if (timerInstance == null) {
//            return null;
//        }
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
////        Item nextTimedItem = timerInstance.updateToNextTimerItem(update, update); //get next timed item if any, also update project and save
//        GetNextItemStatus nextTimedItemStatus = timerInstance.updateToNextTimerItem(update, update); //get next timed item if any, also update project and save
//        //if none found try rest of timerstack (if any)
////        int currentTimerStackIndex = activeTimers.size() - 2; //start with second-last (before current)
//        int currentTimerStackIndex = activeTimers.size() - 1; //start with last timer
////        while (nextTimedItem == null && activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
////        while (nextTimedItem == null && currentTimerStackIndex >= 0) { //if no more tasks in current timer, try earlier ones
//        while (nextTimedItemStatus.nextItem == null && currentTimerStackIndex > 0) { //if no more tasks in current timer, try earlier ones
//            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
////<editor-fold defaultstate="collapsed" desc="comment">
////            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
////            timerInstance.deleteInstance();
////            if (!activeTimers.isEmpty()) { //try with (new)  timerInstance (if any)
////                timerInstance = getCurrentTimerInstanceN();
////</editor-fold>
//            currentTimerStackIndex--;
//            timerInstance = activeTimers.get(currentTimerStackIndex);
////                if (timerInstance != null) {
////            nextTimedItem = timerInstance.updateToNextTimerItem(update, update); //also update project and save
//            nextTimedItem = timerInstance.getTimedItem(); //DO NOT update since this is a previous Timer where the current item was interr8pted, so simply continue with that
////                }
////            if (nextTimedItem == null) {
////                currentTimerStackIndex--;
////            }
//        }
//        //if update,  discard/delete all skipped (no longer having tasks to time) timers:
//        if (update) {
//            while (activeTimers.size() - 1 > currentTimerStackIndex || (nextTimedItemStatus == null && activeTimers.size() > 0)) {
//                timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//                timerInstance.deleteInstance();
//            }
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////if a next item was found
////        if (false) {
////            if (nextTimedItem != null) {
////                if (timerInstance.isInterruptedWhileRunning() || timerInstance.isAutostart()) {
////                    timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
////                    if (timerInstance.isInterruptedWhileRunning()) {
////                        timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
////                    }
////                }
////                refreshOrShowTimerUI();
////            } else { //no more tasks, show popup (or Toastbar?)
////                if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
////                    showNoMoreTasksNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
////                } //else: inbterrupt, so don't display any messages
////            }
////        }
////</editor-fold>
//        return nextTimedItemStatus;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="isAutostartXXX">
//    public boolean isAutostartXXX() {
////        Boolean autostart = getBoolean(PARSE_AUTOSTART_TIMER);
////        return (autostart == null) ? false : autostart;
//        return MyPrefs.timerAutomaticallyStartTimer.getBoolean();
//    }
//</editor-fold>
    /**
     * go to next task (if any), start Timer if needed, refresh UI. Called in
     * user commands
     *
     * @return true if a next task was found
     */
//    boolean moveToNextTask() {
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN(); //NB! Call *after* getTimedItemN() since getTimedItemN() may move to an pushed/earlier/previous timerInstance!
//        //        goToNextTimedItem();
//        goToNextTimedItem();
//        Item nextTimedItemN = getCurrentlyTimedItemN();
//        if (nextTimedItemN != null) {
//            if (timerInstance.isWasInterruptedWhileRunning() || MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
//                timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
//                if (timerInstance.isWasInterruptedWhileRunning()) {
//                    timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
//                }
//            }
//            DAO.getInstance().save //            refreshOrShowTimerUI();
//            return true;
//        } else { //no more tasks, show popup (or Toastbar?)
//            if (timerInstance != null) {
//                if (timerInstance.getTimedProject() != null || timerInstance.getTimedItemListN() != null) {
//                    showNoTasksToWorkOnNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getTimedItemListN());
//                } //else: inbterrupt, so don't display any messages
//            }
////            refreshOrShowTimerUI();
//            return false;
//        }
//    }
//    boolean moveToNextTaskOLD() {
////        goToNextTimedItem();
//        goToNextTimedItem();
//        Item nextTimedItemN = getCurrentlyTimedItemN();
//        TimerInstance2 timerInstance = getCurrentTimerInstanceN(); //NB! Call *after* getTimedItemN() since getTimedItemN() may move to an pushed/earlier/previous timerInstance!
//        if (nextTimedItemN != null) {
//            if (timerInstance.isWasInterruptedWhileRunning() || MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
//                timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
//                if (timerInstance.isWasInterruptedWhileRunning()) {
//                    timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
//                }
//            }
//            DAO.getInstance().save //            refreshOrShowTimerUI();
//            return true;
//        } else { //no more tasks, show popup (or Toastbar?)
//            if (timerInstance != null) {
//                if (timerInstance.getTimedProject() != null || timerInstance.getTimedItemListN() != null) {
//                    showNoTasksToWorkOnNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getTimedItemListN());
//                } //else: inbterrupt, so don't display any messages
//            }
////            refreshOrShowTimerUI();
//            return false;
//        }
//    }
    private void updateActuelWithElapsedTime(Item item, long elapsedTime) {
//        item.setActual(item.getActualForProjectTaskItself() + timerInstance.getElapsedTime(), false);
//        item.addElapsedTimerTimeToActual( elapsedTime); //false
        item.setActualForTaskItself(item.getActualForTaskItself() + elapsedTime, false); //false
    }

    /**
     * true if interrupt is allowed, depends on settings
     *
     * @param newTimedItem
     * @param interruptOrInstantTask
     * @return
     */
    private boolean isInterruptAllowed(Item newTimedItem, boolean interruptOrInstantTask) {
//        if (timedItemOrProject==null) return true;

        if ( //interrupting a normal task/list:
                (!newTimedItem.isInteruptOrInstantTask() && interruptOrInstantTask)
                //interrupting an interrupt enabled:
                || (newTimedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean())
                //normal item/itemList interrupt other item/itemList:
                || (!newTimedItem.isInteruptOrInstantTask() && !interruptOrInstantTask && MyPrefs.timerItemOrItemListCanInterruptExistingItemOrItemList.getBoolean())) {
            return true;
        }
        return false;
    }

    /**
     *
     * @param newTimedItemOrProject
     * @param interruptOrInstantTask
     */
    private void pauseCurrentTimerIfNeeded(TimerInstance2 alreadyRunningTimerInstanceN, Item newTimedItemOrProject, boolean interruptOrInstantTask) {
//        Item alreadyTimedItem = getCurrentlyTimedItemN();
//        TimerInstance2 alreadyRunningTimerInstanceN = getCurrentTimerInstanceN();

        if (alreadyRunningTimerInstanceN != null) {
            Item alreadyTimedItem = alreadyRunningTimerInstanceN.getTimedItemN();
            if (alreadyTimedItem != null) {
                if (true || isInterruptAllowed(newTimedItemOrProject, interruptOrInstantTask)) {
                    if (alreadyRunningTimerInstanceN.isRunning()) { //pause current timer if running
                        boolean aPreviousTaskWasInterrupted = true;
                        alreadyRunningTimerInstanceN.setWasRunningWhenInterrupted(true, true); //pause and save!
                        if (interruptOrInstantTask) {
                            newTimedItemOrProject.setTaskInterrupted(alreadyTimedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
                            DAO.getInstance().saveToParseNow(newTimedItemOrProject);
                        }
//                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
//                    MyForm.showToastBar("Timer paused for \"" + alreadyTimedItem.getText()
//                            //                            + "\"\, will continue after this "
//                            + "\" while timing this "
//                            + (interruptOrInstantTask && aPreviousTaskWasInterrupted ? "interrupt" : "instant task"));
                        MyForm.showToastBar(Format.f("Timer paused for \"{0 taskText}\" while timing this {1 taskType}",
                                alreadyTimedItem.getText(), (interruptOrInstantTask && aPreviousTaskWasInterrupted ? "interrupt" : "instant task")));
                    }
                } else { //interrupt not allowed (stop and replace current timer by new one)
                    if (alreadyTimedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
                        showTimerCannotBeStarted(alreadyTimedItem.isInteruptOrInstantTask(), alreadyTimedItem);
                        return;
                    }
                }
            }
        }
    }

    private void pauseCurrentTimerIfNeededOLD(Item newTimedItemOrProject, boolean interruptOrInstantTask) {
        Item alreadyTimedItem = getCurrentlyTimedItemN();
        TimerInstance2 alreadyRunningTimerInstance = getCurrentTimerInstanceN();

        if (alreadyRunningTimerInstance != null) {
            if (isInterruptAllowed(newTimedItemOrProject, interruptOrInstantTask)) {
                if (alreadyRunningTimerInstance.isRunning()) { //pause current timer if running
                    boolean aPreviousTaskWasInterrupted = true;
                    alreadyRunningTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
                    if (interruptOrInstantTask) {
                        newTimedItemOrProject.setTaskInterrupted(alreadyTimedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
                        DAO.getInstance().saveToParseNow(newTimedItemOrProject);
                    }
//                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
//                    MyForm.showToastBar("Timer paused for \"" + alreadyTimedItem.getText()
//                            //                            + "\"\, will continue after this "
//                            + "\" while timing this "
//                            + (interruptOrInstantTask && aPreviousTaskWasInterrupted ? "interrupt" : "instant task"));
                    MyForm.showToastBar(Format.f("Timer paused for \"{0 taskText}\" while timing this {1 taskType}", alreadyTimedItem.getText(), (interruptOrInstantTask && aPreviousTaskWasInterrupted ? "interrupt" : "instant task")));
                }
            } else { //interrupt not allowed (stop and replace current timer by new one)
                if (alreadyTimedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
                    showTimerCannotBeStarted(alreadyTimedItem.isInteruptOrInstantTask(), alreadyTimedItem);
                    return;
                }
            }
        }
    }

    /**
     * flit state of current timer between running and paused
     */
    void flipRunningTimerState() {
        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
        if (!timerInstance.isRunning()) {                    //start Timer
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
            timerInstance.startTimer(true, true);
        } else { //timer running:
            timerInstance.stopTimer();
        }
//        listeners.fireDataChangeEvent(-1, DataChangedListener.CHANGED);
        fireChangedEvent();
    }

    /**
     * is a timer earlier than the current one timing newSource?
     *
     * @param newSource
     * @return
     */
    private boolean isSourceAlreadyBeingTimedByAnEarlierTimer(ItemAndListCommonInterface newSource) {
        if (newSource == null || activeTimers.size() <= 1) {
            return false;
        }
//        for (TimerInstance2 timerInstance : activeTimers) {
        for (int i = activeTimers.size() - 2; i >= 0; i--) { //test if an *earlier* timer is timing newSource
            TimerInstance2 timerInstance = activeTimers.get(i);
            if (timerInstance.getTimerSourceN() == newSource) {
                return true;
            }
        }
        return false;
    }

    /**
     * is a timer earlier than the current one timing newSource?
     *
     * @param item
     * @return
     */
    private boolean isItemAlreadyBeingTimedByAnyTimer(Item item) {
        if (item == null || activeTimers.isEmpty()) {
            return false;
        }
//        for (TimerInstance2 timerInstance : activeTimers) {
        for (int i = activeTimers.size() - 1; i >= 0; i--) { //test if an *earlier* timer is timing newSource
            TimerInstance2 timerInstance = activeTimers.get(i);
            if (timerInstance.getTimedItemN() == item) {
                return true;
            }
        }
        return false;
    }

    private void addNewTimerInstance(TimerInstance2 newTimerInstance, boolean saveTimerInstance) {
        activeTimers.add(newTimerInstance); //add last as definitely the most recent instance
//        DAO.getInstance().saveInBackground(newTimerInstance);
        if (saveTimerInstance) {
//            DAO.getInstance().saveTimerInstanceInBackground(newTimerInstance);
            DAO.getInstance().saveToParseNow(newTimerInstance);
        }
//        DAO.getInstance().save(newTimerInstance,false);
    }

    /**
     *
     * @param timedItemOrProject can be null if starting timer on first timeable
     * element of timedItemListOrCategoryN
     * @param timedProjectItemListOrCategoryN can be null if timer just started
     * on timedItemOrProject
     * @param previousForm
     * @param interruptOrInstantTask
     * @param startedOnIndividualItemOrProject
     */
    public void startTimer(Item timedItemOrProject, ItemAndListCommonInterface timedProjectItemListOrCategoryN, MyForm previousForm,
            boolean interruptOrInstantTask, boolean startedOnIndividualItemOrProject) {

        //if no source and no item is given, just ignore
        if (timedItemOrProject == null && timedProjectItemListOrCategoryN == null) {
            ASSERT.that("startTiming called with both item and source null");
            return;
        }

        //if starting timer on the same item (and the same source, possibly null if started directly on a single item), do nothing (UI: 
        if (timedItemOrProject == getCurrentlyTimedItemN()
                && timedProjectItemListOrCategoryN == getCurrentlyTimedSourceN()) {
            return;
        }

        //Check that source is not already being timed (avoid starting timer multiple times on the same source)
        if (isSourceAlreadyBeingTimedByAnEarlierTimer(timedProjectItemListOrCategoryN)) {
//            ScreenTimer7.showTimerSourceIsAlreadyBeingTimed(timedItemListOrCategoryN.getText());
            showTimerSourceIsAlreadyBeingTimed(timedProjectItemListOrCategoryN);
            return;
        }

        //if timer started on a project, then replace timed project by its first subtask
        if (timedItemOrProject != null && timedItemOrProject.isProject()) {
            List<Item> subtasks = timedItemOrProject.getLeafTasksAsListN();
            if (!subtasks.isEmpty()) {
                if (timedProjectItemListOrCategoryN == null) { //if timer was started just on a project (no source list), then use project as source list for later subtasks
                    timedProjectItemListOrCategoryN = timedItemOrProject;
                }
                timedItemOrProject = subtasks.get(0);
            }
        }

        //if timedItemOrProject is not given, get first element from source (if one exists)
        if (timedItemOrProject == null && timedProjectItemListOrCategoryN != null) {
            List<Item> leafTasks = timedProjectItemListOrCategoryN.getLeafTasksAsListN();
            if (!leafTasks.isEmpty()) {
                timedItemOrProject = leafTasks.get(0);
            }
        }

        //if timedItem is already being timed (by earlier or current timerInstance)
        if (timedItemOrProject != null && isItemAlreadyBeingTimedByAnyTimer(timedItemOrProject)) {
//            ScreenTimer7.showTimerSourceIsAlreadyBeingTimed(timedItemListOrCategoryN.getText());
            showTimedItemIsAlreadyBeingTimed(timedItemOrProject);
            return;
        }

        //if nothing to time, show error message
        if (timedItemOrProject == null) {
//            ScreenTimer7.showNoTasksToWorkOnNotificationWhenRelevant(timedItemListOrCategoryN.getText());
//            showNoTasksToWorkOnNotificationWhenRelevant(timedItemListOrCategoryN.getText());
            showNoTasksToWorkOnNotificationWhenRelevant(timedProjectItemListOrCategoryN);
            return; //if no timeable item, simply return
        }

        //if starting timer on the same item and the same source , do nothing
//        if (timedItemOrProject == getCurrentTimerInstanceN().getTimedItemN()) {
//            if ((timedItemListOrCategoryN == null
//                    || timedItemListOrCategoryN == getCurrentTimerInstanceN().getTimerSourceN())
//                    && timedItemOrProject == getCurrentTimerInstanceN().getTimedItemN()) {
//                return;
//            }
        //timer already running and interrupting it is not allowed, then show message that timer is already running and return
        if (isTimerActive() && !MyPrefs.timerMayPauseAlreadyRunningTimer.getBoolean()) {//and setting to allow interrupt
            String timedElt = timedProjectItemListOrCategoryN != null ? timedProjectItemListOrCategoryN.getText() : timedItemOrProject.getText();
            MyForm.showToastBar("Timer already running on \"" + timedElt + "\"");
            return;
        }

        //timer already running and user does not select to interrupt, then return
        if (isTimerActive() && MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean()) {
//            if (!ScreenTimer7.showDialogPauseActiveTimer(getCurrentTimerInstanceN(), timedItemListOrCategoryN != null ? timedItemListOrCategoryN : timedItemOrProject)) {
            if (!showDialogPauseActiveTimer(getCurrentTimerInstanceN(), timedProjectItemListOrCategoryN != null ? timedProjectItemListOrCategoryN : timedItemOrProject)) {
                return;
            }
        }

        //if timedItem is not valid for timer return unless it was started directly on the invalid item and setting is true
        if (!isValidItemForTimer(timedItemOrProject) && !startedOnIndividualItemOrProject && !MyPrefs.timerCanBeSwipeStartedEvenOnInvalidItem.getBoolean()) {
            return;
        }

        //stop and push previously timed item+context
        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
//        this.timeEvenInvalidItem = timeEvenInvalidItem;
        ASSERT.that(!interruptOrInstantTask || timedProjectItemListOrCategoryN == null, "cannot have an interrupt task with a list");
        ASSERT.that(timedProjectItemListOrCategoryN != null || timedItemOrProject != null, "cannot launch timer if both list and item are null");

        //Start Timer on *new* list/item (or interrupt running timer if conditions are given)
//        if (!isTimerActive() //timer not already running
//                || ((MyPrefs.timerMayPauseAlreadyRunningTimer.getBoolean() //and setting to allow interrupt
//                && (!MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean()
//                || showDialogPauseActiveTimer(timedItemListOrCategoryN != null ? timedItemListOrCategoryN : timedItemOrProject))))) { //or setting to ask before interrupting
//
//            boolean timeEvenInvalidItemsOrProjects = startedOnIndividualItemOrProject
//                    && (timedItemOrProject != null && !isValidItemForTimer(timedItemOrProject))
//                    && MyPrefs.timerCanBeSwipeStartedEvenOnInvalidItem.getBoolean();
        TimerInstance2 timerInstance = getCurrentTimerInstanceN();
        if (timerInstance == null) {
            timerInstance = new TimerInstance2(timedItemOrProject, timedProjectItemListOrCategoryN);
            if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                timerInstance.startTimer(false, true); //saved below in addNewTimerInstance
            }
            addNewTimerInstance(timerInstance, true); //also saves newTimerInstance //NO, no need to save since startTimer will save
        } else {
            if (timedProjectItemListOrCategoryN == timerInstance.getTimerSourceN()) {
                //if starting timer on the same source but for another element, replace with new
//                newTimerInstance = getCurrentTimerInstanceN();
                timerInstance.stopTimerOnTimedItemAndUpdateActualsAndSave(false, true);
                pauseCurrentTimerIfNeeded(timerInstance, timedItemOrProject, interruptOrInstantTask);
                if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                    timerInstance.startTimer(true, true); //saved below in addNewTimerInstance
                }
                timerInstance.setTimedItem(timedItemOrProject);
//                if(MyPrefs.timerAutomaticallyStartTimer.getBoolean())
//                newTimerInstance.startTimer(true, true);
            } else {
                timerInstance.stopTimerOnTimedItemAndUpdateActualsAndSave(true, true);
                pauseCurrentTimerIfNeeded(timerInstance, timedItemOrProject, interruptOrInstantTask);
                timerInstance = new TimerInstance2(timedItemOrProject, timedProjectItemListOrCategoryN);
                if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                    timerInstance.startTimer(true, true); //saved below in addNewTimerInstance
                }
                addNewTimerInstance(timerInstance, true); //also saves newTimerInstance //NO, no need to save since startTimer will save
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        Item newTimedItem = timerInstance.getTimedItemN();
//        if (newTimedItem != null) { //if there's an item to time in the new list/item/project
//            pauseCurrentTimerIfNeeded(newTimedItem, interruptOrInstantTask); xxx;
//            ASSERT.that(!interruptOrInstantTask || newTimedItem.isInteruptOrInstantTask()); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
//            if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
//                timerInstance.startTimer(true, true); //saved below in addNewTimerInstance
//            }
//            addNewTimerInstance(timerInstance, true); //also saves newTimerInstance //NO, no need to save since startTimer will save
//        }
//</editor-fold>
        fireChangedEvent();
//            DAO.getInstance().saveToParseNow(timerInstance);
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //            //show big timer
    //            timerInstance.setFullScreen(true);
    //            timerInstance.saveMe();
    //            refreshOrShowUIOnTimerChange();
    //        } else { // starting Timer
    //
    //            boolean timeEvenInvalidItemsOrProjects = startedOnIndividualItemOrProject
    //                    && (timedItemOrProject != null && !isValidItemForTimer(timedItemOrProject))
    //                    && MyPrefs.timerCanBeSwipeStartedEvenOnInvalidItem.getBoolean();
    //            TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, timedItemList, timeEvenInvalidItemsOrProjects);
    //            Item newTimedItem = newTimerInstance.getTimedItemN();
    //            if (newTimedItem != null) { //if there's an item to time in the new list/item/project
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////            Item timedItem = getTimedItemN();
    ////            TimerInstance previousTimerInstance = getCurrentTimerInstanceN();
    ////
    ////            if (previousTimerInstance != null) {
    ////                if (isItemOrListAlreadyBeingTimed(timedItemOrProject, itemList)) { //don't allow to start timer again on an element which is already timer (and could be interrupted by later timers)
    ////                    return;
    ////                } else if (isInterruptAllowed(timedItemOrProject, interruptOrInstantTask)) {
    ////                    if (previousTimerInstance.isRunning()) { //pause current timer if running
    ////                        boolean previousTaskWasInterrupted = true;
    ////                        previousTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
    ////                        if (interruptOrInstantTask) {
    ////                            timedItemOrProject.setTaskInterrupted(timedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
    ////                            DAO.getInstance().saveInBackground(timedItemOrProject);
    ////                        }
    //////                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
    ////                        MyForm.showToastBar("Timer paused for \"" + timedItem.getText() + "\", will continue after this "
    ////                                + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
    ////                    }
    ////                } else {
    ////                    if (timedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
    ////                        showTimerCannotBeStarted(timedItem.isInteruptOrInstantTask(), timedItem);
    ////                        return;
    ////                    }
    ////                } //not a valid
    ////            }
    ////</editor-fold>
    ////            pauseCurrentTimerIfNeeded(timedItemOrProject, interruptOrInstantTask);
    //                pauseCurrentTimerIfNeeded(newTimedItem, interruptOrInstantTask);
    //
    ////            addNewTimerInstance(newTimerInstance,true); //also saves newTimerInstance //NO, no need to save since startTimer will save
    ////            assert !interruptOrInstantTask || timedItemOrProject.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
    //                assert !interruptOrInstantTask || newTimedItem.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
    //
    //                if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
    //                    newTimerInstance.startTimer(true); //saved below in addNewTimerInstance
    //                }
    //                addNewTimerInstance(newTimerInstance, true); //also saves newTimerInstance //NO, no need to save since startTimer will save
    ////            refreshOrShowTimerUI(previousForm, interruptOrInstantTask);
    //                refreshOrShowUIOnTimerChange();
    //            } else { //nothing to time
    //                //no save of new TimerInstance if not tasks to time
    //                showNoTasksToWorkOnNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getTimedItemListN());
    //            }
    //        }
    //</editor-fold>

    /**
     * starts a timer for item (possibly as an interrupt) or itemlist (. If
     * timer is already running, the previous item is pushed and will continue
     * afterwards, otherwise the timer will be started normally on item (not as
     * an interrupt).
     *
     * @param timedItemOrProject
     * @param previousForm
     * @param doneAction
     */
    private void startTimerXXX(Item timedItemOrProject, ItemList itemList, MyForm previousForm, boolean interruptOrInstantTask) {
        startTimer(timedItemOrProject, itemList, previousForm, interruptOrInstantTask, false);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void startTimerXXX(Item timedItemOrProject, ItemList itemList, MyForm previousForm, boolean interruptOrInstantTask, boolean timeEvenInvalidItem) {
//
//        if (ReplayLog.getInstance().isReplayInProgress()) {
//            refreshOrShowTimerUI();
//            return;
//        }
//
//        //stop and push previously timed item+context
//        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
////        this.timeEvenInvalidItem = timeEvenInvalidItem;
//        ASSERT.that(!interruptOrInstantTask || itemList == null, "cannot have an interrupt task with a list");
//        Item timedItem = getTimedItemN();
//        TimerInstance previousTimerInstance = getCurrentTimerInstanceN();
//
//        if (previousTimerInstance != null) {
//            if (isItemOrListAlreadyBeingTimed(timedItemOrProject, itemList)) { //don't allow to start timer again on an element which is already timer (and could be interrupted by later timers)
//                return;
//            } else if (isInterruptAllowed(timedItemOrProject, interruptOrInstantTask)) {
//                if (previousTimerInstance.isRunning()) { //pause current timer if running
//                    boolean previousTaskWasInterrupted = true;
//                    previousTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
//                    if (interruptOrInstantTask) {
//                        timedItemOrProject.setTaskInterrupted(timedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
//                        DAO.getInstance().saveInBackground(timedItemOrProject);
//                    }
////                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
//                    MyForm.showToastBar("Timer paused for \"" + timedItem.getText() + "\", will continue after this "
//                            + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
//                }
//            } else {
//                if (timedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
//                    showTimerCannotBeStarted(timedItem.isInteruptOrInstantTask(), timedItem);
//                    return;
//                }
//            } //not a valid
//        }
//
//        assert !interruptOrInstantTask || timedItemOrProject.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
//
//        TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, itemList);
//        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
//        addNewTimerInstance(newTimerInstance); //also saves newTimerInstance //NO, no need to save since startTimer will save
////        Item newTimedItem = newTimerInstance.getTimedItem();
//        Item newTimedItem = getTimedItemN();
//        if (newTimedItem != null) {
//            newTimerInstance.startTimer(true); //saved below in addNewTimerInstance
//            addNewTimerInstance(newTimerInstance); //also saves newTimerInstance //NO, no need to save since startTimer will save
//            refreshOrShowTimerUI();
//        } else { //nothing to time
//            //no save of new TimerInstance if not tasks to time
//            showNoTasksToWorkOnNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getItemList());
//        }
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private void startTimer(Item timedItemOrProject, ItemList itemList, MyForm previousForm, boolean interruptOrInstantTask) {
    //        //stop and push previously timed item+context
    ////        startInterrupt(item, previousForm, true, true);
    ////        launchTimerImpl(item, null, null, previousForm, true);
    //        ASSERT.that(!interruptOrInstantTask || itemList == null, "cannot have an interrupt task with a list");
    //        boolean previousTaskWasInterrupted = false;
    //
    //        TimerInstance previousTimerInstance = getCurrentTimerInstanceN();
    //
    //        if (previousTimerInstance != null) {
    //            if ((timedItemOrProject != null && timedItemOrProject.equals(previousTimerInstance.getTimedItemN())) //if re-launcing timer on already timed Item
    //                    || (itemList != null && itemList.equals(previousTimerInstance.getItemList()))) {//or re-launcing timer on already timed ItemList
    //                String s = previousTimerInstance.getTimedItemN() != null ? Item.TASK + " \"" + previousTimerInstance.getTimedItemN().getText() + "\""
    //                        : (previousTimerInstance.getItemList() != null ? ItemList.ITEM_LIST + " \"" + previousTimerInstance.getItemList().getText() : "");
    //                MyForm.showToastBar("Timer already running for \"" + previousTimerInstance.getTimedItemN().getText() + "\""
    //                        + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
    //                return;
    //            } else if ((!previousTimerInstance.getTimedItemN().isInteruptOrInstantTask() && interruptOrInstantTask) //interrupting a normal task/list
    //                    || (previousTimerInstance.getTimedItemN().isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) //interrupting an interrupt enable
    //                    || (!previousTimerInstance.getTimedItemN().isInteruptOrInstantTask() && !interruptOrInstantTask && MyPrefs.timerItemOrItemListCanInterruptExistingItemOrItemList.getBoolean())) { //normal item/itemList interrupt other item/itemList
    //                if (previousTimerInstance.isRunning()) { //pause current timer if running
    //                    previousTaskWasInterrupted = true;
    //                    previousTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
    //                    if (interruptOrInstantTask) {
    //                        timedItemOrProject.setTaskInterrupted(previousTimerInstance.getTimedItemN()); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
    //                        DAO.getInstance().saveInBackground(timedItemOrProject);
    //                    }
    ////                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
    //                    MyForm.showToastBar("Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
    //                            + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
    //                }
    //            } else {
    //                if (previousTimerInstance.getTimedItemN().isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
    //                    showTimerCannotBeStarted(previousTimerInstance.getTimedItemN().isInteruptOrInstantTask(), previousTimerInstance.getTimedItemN());
    //                    return;
    //                }
    //            } //not a valid
    //        }
    //
    //        assert !interruptOrInstantTask || timedItemOrProject.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
    //
    ////        TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, itemList, MyPrefs.timerAutomaticallyStartTimer.getBoolean(), MyPrefs.timerAutomaticallyGotoNextTask.getBoolean());
    //        if (false && timedItemOrProject != null) {
    //            DAO.getInstance().saveInBackground(timedItemOrProject); //save *before* saving timerInstance (otherwise parse error about unreferenced object
    //        }
    //        TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, itemList);
    //        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
    //        Item timedItem = newTimerInstance.getTimedItemN();
    //        if (timedItem != null) {
    //            newTimerInstance.startTimer(true); //saved below in addNewTimerInstance
    //            addNewTimerInstance(newTimerInstance); //also saves newTimerInstance //NO, no need to save since startTimer will save
    //            refreshOrShowTimerUI();
    //        } else { //nothing to time
    //            //no save of new TimerInstance if not tasks to time
    //            showNoMoreTasksNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getItemList());
    //        }
    //    }
    //</editor-fold>
//    public void startTimerOnItem(Item timedItem, MyForm previousForm, boolean startedOnIndividualItemOrProject) {
    public void startTimerOnItemXXX(Item timedItem, MyForm previousForm) {
        startTimer(timedItem, null, previousForm, false, true);
    }

    public void startTimerOnItemListXXX(ItemList itemList, MyForm previousForm) {
        startTimer(null, itemList, previousForm, false, false);
        //TODO set autostart!
    }

    public void startTimerOnItemInItemListXXX(Item item, ItemList itemList, MyForm previousForm) {
        startTimer(item, itemList, previousForm, false, true);
        //TODO set autostart!
    }

    public void startTimerOnItemOrItemListXXX(ItemAndListCommonInterface itemOrItemList, MyForm previousForm) {
        if (itemOrItemList instanceof Item) {
            startTimerOnItemXXX((Item) itemOrItemList, previousForm);
        } else if (itemOrItemList instanceof ItemList) {
            startTimerOnItemListXXX((ItemList) itemOrItemList, previousForm);
        }
    }

    public void startInterruptOrInstantTask(Item interruptOrInstantTask, MyForm previousForm) {
        ASSERT.that(interruptOrInstantTask.isInteruptOrInstantTask());
        startTimer(interruptOrInstantTask, null, previousForm, true, false);
        //TODO set autostart!
    }

    /**
     * check if item is currently timed, and if so, if it (now) has a status
     * that means the timer should be stopped (CANCELLED/DONE/WAITING). Called
     * every time an item is saved
     *
     * @param item
     * @param startTimingNextItem
     * @param saveItem
     */
//    public void updateTimerOnItemStatusChangeXXX(Item item, boolean startTimingNextItem, boolean saveItem) {
//        TimerInstance2 timerInstanceN = getCurrentTimerInstanceN();
//        if (timerInstanceN != null && timerInstanceN.getTimedItemN() == item) { // && newStatus != oldStatus) {
//            Item timedItemN = getCurrentlyTimedItemN();
////            if (isValidItemForTimer(timedItemN)&&newStatus == ItemStatus.CANCELLED || newStatus == ItemStatus.DONE || newStatus == ItemStatus.WAITING) {
////            if (!isValidItemForTimer(timedItemN)) {
//            ItemStatus newStatus = item.getStatus();
//            if ((newStatus == ItemStatus.CANCELLED || newStatus == ItemStatus.DONE || newStatus == ItemStatus.WAITING)
//                    && !timerInstanceN.isTimeEvenInvalidItemOrProjects()) {
////                timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveItem); //stop this timer, save item, 
//                timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(saveItem, false); //stop this timer, save item (saveItem will normally be false because Item is already in the process of being saved), false=don't save Timer
//                if (startTimingNextItem) {
//                    moveToNextTask();
//                }
//            } else { //status==CREATED/ONGOING
//                //nothing
//            }
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="startInterruptOrInstantTask">
//    public void startInterruptOrInstantTask(Item interruptOrInstantTask, MyForm previousForm) {
//        //stop and push previously timed item+context
////        startInterrupt(item, previousForm, true, true);
////        launchTimerImpl(item, null, null, previousForm, true);
//        TimerInstance currentTimer = getCurrentTimerInstanceN();
//        if (currentTimer != null) {
//            if (currentTimer.isRunning()) { //pause current timer if running
//                currentTimer.setWasRunningWhenInterrupted(true, true); //pause and save!
//                interruptOrInstantTask.setTaskInterrupted(currentTimer.getTimedItemN()); //only set if timer was actually running, otherwise does not qualify as an interrupt
//            }
//        }
//        interruptOrInstantTask.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
//        TimerInstance newTimer = new TimerInstance(interruptOrInstantTask);
//        if (newTimer.isThereATaskToTime()) {
//            activeTimers.add(newTimer);
//            newTimer.save();
//        } else {
////            showNoMoreTasksDialogWhenRelevant(timerStack.currEntry.sourceItemOrProject, timerStack.currEntry.itemList);
////            showNoMoreTasksDialogWhenRelevant(interruptOrInstantTask, null); //NB Shown from within TimerInstance
//        }
//        if (MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//            launchTimerImpl(interruptOrInstantTask, null, previousForm, true, false);
//        }
//    }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * update
     *
     * @param itemStatus
     */
//    private void updateTimedTaskSetStatusAndGotoNextXXX(ItemStatus itemStatus) {
//        if (false) {
//            TimerInstance timerInstance = getCurrentTimerInstanceN();
//            if (timerInstance != null) {
//                Item timedItem = timerInstance.getTimedItemN();
////        if (false && timedItem != null) {
////            if (false) {
////                timerInstance.stopTimer(true);                //stop this timer, save item,
////            }//                status.setStatus(ItemStatus.DONE, false);
//////                timedItem.setStatus(status.getStatus());
////            if (itemStatus != null) {
////                timedItem.setStatus(itemStatus);
////            }
////        }
//                if (itemStatus != null) {
//                    timedItem.setStatus(itemStatus);
//                }
//                timerInstance.stopTimerOnTimedItemAndUpdateActualsAndSave(true);
//                moveToNextTask();
//                refreshOrShowUIOnTimerChange();
////            refreshFormOnTimerUpdate();
//            }
//        }
//        Item timedItemN = getCurrentlyTimedItemN();
//        if (timedItemN != null && itemStatus != null) {
//            timedItemN.setStatus(itemStatus); //changing the status will automatically update the timer via the call to updateTimerOnItemStatusChange()
//        }
//    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * if one of the timers is timing item, then stop it, and go to the next
     * item, and start timer on that (as if done via Timer UI). Will
     *
     * @param item
     * @return true if a timer was found and stopped
     */
//    boolean stopTimerXXX(Item item) {
////        for (TimerInstance timer : activeTimers) {
//        Iterator<TimerInstance> it = activeTimers.iterator();
//        while (it.hasNext()) {
//            TimerInstance timer = it.next();
//            if (timer.getTimedProject().getObjectIdP().equals(item.getObjectIdP())) {
//
//            }
//            if (timer.getTimedItemN().getObjectIdP().equals(item.getObjectIdP())) {
//                //stop timer?!
////                if (isMinimumThresholdPassed(timer.getElapsedTime())) {
////                    item.setActualEffort(item.getActualEffortProjectTaskItself() + timer.getElapsedTime());
////                }
//                addTimerElapsedTimeToItemActualEffort(item, timer.getElapsedTime());
////                    activeTimers.remove(timer);
//                Item nextItem = timer.updateToNextTimerItem();
//                if (nextItem == null) { //
//                    it.remove();
//                    timer.deleteInstance();
//                    return true;
//                    break; //no need to iterate over other timers since an item can only be timed once
//                } else {
//
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
    /**
     * called when an Item changes status (Done/Cancelled/Deleted) and stops the
     * corresponding timer if one is currently running/paused/Interrupted for
     * this item, updates the timed item with elapsed time and moves to next
     * item (or removes the Timer instance if no next task). * OLD invalid text:
     * Tests if the current item is being set to DONE/CANCELLED from within the
     * timer to update infinite loops if the timer is setting the item to Done
     * Updates the item actuals with the timer value. Does not start the timer
     * on a next item (like normally done when within the timer. Does everything
     * normally done when exiting the timer (eg pops the timer stack). DOES
     * currently NOT store any other values edited in the timer What happens
     * next time the timer is called?? return true if a Timer was running for
     * the task.
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean stopTimerIfActiveOnThisItemAndGotoNextOLDXXX(Item item) { //boolean continueWithNextItem //
//        int timerIndex = activeTimers.size() - 1;
//        Item nextTimedItem = null;
//        while (nextTimedItem == null && timerIndex >= 0) { //ccheck starting with most recent interrupted timer
//            TimerInstance timerInstance = activeTimers.get(timerIndex);
//            if (timerInstance.getTimedItemN() == item) {
//                //update item with elapsed time
//                updateActuelWithElapsedTime(item, timerInstance.getElapsedTime());
//                //find next item to run timer on, most likely either next in current list/project, or the one interrupted
////                moveToNextTimedItem(timerInstance, true);
////                    Item nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
////                GetNextItemStatus nextTimedItemStatus = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
//                GetNextItemStatus nextTimedItemStatus = timerInstance.updateToNextTimerItem(true); //get next timed item if any, also update project and save
//                if (nextTimedItemStatus.previouslTimedItemNotFound) {
//
//                } else {
//
//                    //if none found remove from TimerStack
//                    if (nextTimedItem == null) {
//                        activeTimers.remove(timerInstance); //no need to save timerStack, it is build from TimerInstances on the server
//                        timerInstance.deleteInstance();
//                    }
//                }
//                //we've found the item, stop looking
//                return true;
//            }
//            timerIndex--;
//            //don't save item, it'll be done from whereever this method was called
//        }
//        return false;
//    }
//    private boolean stopTimerIfActiveOnThisItemAndGotoNextXXX(Item item) { //boolean continueWithNextItem //
//        return stopTimerIfActiveOnThisItemAndGotoNext(item, true, true, true);
//    }
//</editor-fold>
//    public boolean stopTimerIfActiveOnThisItemAndGotoNextXXX(Item item, boolean saveUpdatedItem, boolean saveUpdatedTimerInstance, boolean gotoNextItem) { //boolean continueWithNextItem // 
//        if (item != null && item == getCurrentlyTimedItemN()) {
////            stopTimerForCurrentItemAndGotoNextNEW();
//            TimerInstance timerInstanceN = getCurrentTimerInstanceN();
//            timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(saveUpdatedItem, saveUpdatedTimerInstance); //stop this timer, save item, 
//            if (gotoNextItem) {
//                moveToNextTask();
//            }
//            refreshOrShowUIOnTimerChange();
//            return true;
//        }
//        return false;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean stopTimerIfActiveOnThisItemAndGotoNextOLD(Item item) { //boolean continueWithNextItem //
////        if (!isATimerActiveFor(item)) //if no timer active (whether runnning, paused or interrupted), then just return
////            return false;
//
////        if (item.equals(getTimedItemN())) { //if it's currently timed task...
//        if (item == getCurrentlyTimedItemN()) { //if it's currently timed task...
//            moveToNextTask(); //simply move to next one
//            refreshOrShowTimerUI();
//            return true;
//        } else { //see if any interrupted timer was timing the item
//            int timerIndex = activeTimers.size() - 1;
//            timerIndex--; //skip current one since we dealt with it above
//            while (timerIndex >= 0) { //ccheck starting with most recent interrupted timer
//                TimerInstance timerInstance = activeTimers.get(timerIndex);
//                if (timerInstance.getTimedItem() == item) {
//                    //update item with elapsed time
//                    item.setActual(item.getActualForProjectTaskItself() + timerInstance.getElapsedTime(), false);
//                    //find next item to run timer on, most likely either next in current list/project, or the one interrupted
////                moveToNextTimedItem(timerInstance, true);
////                    Item nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
//                    GetNextItemStatus nextTimedItemStatus = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
//                    if (nextTimedItemStatus.previouslTimedItemNotFound) {
//
//                    } else {
//
//                        //if none found remove from TimerStack
//                        if (nextTimedItem == null) {
//                            activeTimers.remove(timerInstance); //no need to save timerStack, it is build from TimerInstances on the server
//                            timerInstance.deleteInstance();
//                        }
//                    }
//                    //we've found the item, stop looking
//                    return true;
//                }
//                timerIndex--;
//                //don't save item, it'll be done from whereever this method was called
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void updateTimerWhenItemListIsDeletedOrModifiedOLD(ItemAndListCommonInterface element, boolean isDeleted, boolean isModified) {
//        TimerInstance currentActiveTimerInstance = getCurrentTimerInstanceN();
//
//        for (TimerInstance timerInstance : activeTimers) {
//            if (element == timerInstance.getTimedItem()) {
//                if (isDeleted) {
//                    moveToNextTask(); //simply move to next one
//                }
//                refreshOrShowTimerUI(); //refresh wheter deleted or modified
//                return;
//            } else if (element == timerInstance.getTimedProject()) { // || element.equals(timerInstance.getTimedItem())
//                //if element is a project (in which the currently timed item may be a subtask, then do nothing (
//                refreshOrShowTimerUI(); //since the project name etc may be displayed in timer
//                return;
//            } else if (element == timerInstance.getTimedItemList())
//                if (isDeleted) {
//
//                } else if (isModified) {
//                    //do nothing
//                }
//            if (element == getCurrentlyTimedItemN()) { //if it's currently timed task...
//                moveToNextTask(); //simply move to next one
//                refreshOrShowTimerUI();
//                return true;
//            } else { //see if any interrupted timer was timing the item
//                int timerIndex = activeTimers.size() - 1;
//                timerIndex--; //skip current one since we dealt with it above
//                while (timerIndex >= 0) { //ccheck starting with most recent interrupted timer
//                    TimerInstance timerInstance = activeTimers.get(timerIndex);
//                    if (timerInstance.getTimedItem() == element) {
//                        //update item with elapsed time
//                        element.setActual(element.getActualForProjectTaskItself() + timerInstance.getElapsedTime(), false);
//                        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
////                moveToNextTimedItem(timerInstance, true);
//                        Item nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
//                        //if none found remove from TimerStack
//                        if (nextTimedItem == null) {
//                            activeTimers.remove(timerInstance); //no need to save timerStack, it is build from TimerInstances on the server
//                            timerInstance.deleteInstance();
//                        }
//                        //we've found the item, stop looking
//                        return true;
//                    }
//                    timerIndex--;
//                    //don't save item, it'll be done from whereever this method was called
//                }
//            }
//        }
//    }
//    public boolean stopTimerIfRunningOnThisItemOnStartTimerOnNextOLD(Item item) { //boolean continueWithNextItem // 
//        if (!isATimerActiveFor(item)) //if no timer active (whether runnning, paused or interrupted), then just return
//            return false;
//
//        boolean wasTimerRunningForTheTask = false;
//        if (item == null || getCurrentTimerInstanceN() == null) {
////            return false;
//            return wasTimerRunningForTheTask;
//        }
//
////        if (item.equals(getCurrentTimerInstanceN().getTimedItemN())) { //if it's currently timed task...
//        if (item.equals(getCurrentlyTimedItemN())) { //if it's currently timed task...
//            wasTimerRunningForTheTask = true;
//            moveToNextTask(); //simply move to next one
//            refreshOrShowTimerUI();
//        } else { //see if interrupted timers were timing item
//            int timerIndex = activeTimers.size() - 1;
//            timerIndex--; //skip current one since we dealt with it above
//            while (timerIndex >= 0) { //ccheck starting with most recent interrupted timer
//                TimerInstance timerInstance = activeTimers.get(timerIndex);
//
////                if (item.equals(timerInstance.getTimedItemN())) { //if timer is (or was if down in the stack) running on this specific task
//                if (item.equals(getCurrentlyTimedItemN())) { //if timer is (or was if down in the stack) running on this specific task
//                    wasTimerRunningForTheTask = true;
//                    boolean timerWasRunning = timerInstance.isInterruptedWhileRunning();
//                    if (true || timerWasRunning) {
//                        //update item with elapsed time 
//                        item.setActual(item.getActualForProjectTaskItself() + timerInstance.getElapsedTime(), false);
//                    }
////                item.setActualEffort(timerInstance.isTimerShowActualTotal() ? timerInstance.getElapsedTime()
////                        : item.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime());
//                    //don't save item, it'll be done from whereever this method was called
//
//                    //start next or exit timer if no more available
////                return goToNextTimedItem(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
////if no more items to time on this timer instance, then remove it (scenario: last task T in project/list is interrupted, then T is marked Done manually in a screen (while interrupt is timed), when timer f interrupt is stopped, 
////                goToNextTimedItem(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
//                    moveToNextTask(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
//                    refreshOrShowTimerUI();
////                refreshOrShowTimerUI();
//                }
//            }
////        return false;
//        }
//        return wasTimerRunningForTheTask;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void stopTimerForCurrentItemAndGotoNextNEWXXX() {
//        TimerInstance timerInstanceN = getCurrentTimerInstanceN();
//        if (timerInstanceN != null) {
//            Item timedItemN = timerInstanceN.getTimedItemN();
//            if (timedItemN != null) {
//                timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(true); //stop this timer, save item,
//                moveToNextTask();
//                refreshOrShowUIOnTimerChange();
////                refreshFormOnTimerUpdate();
//            }
//        }
//    }
//    public void updateTimerOnItemStatusChange(ItemStatus oldStatus, ItemStatus newStatus, boolean startTimingNextItem, boolean saveItem) {
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void gotoNextXXX(TimerInstance timerInstance, Item timedItem, ItemStatus itemStatus) {
//        if (timedItem != null) {
//            if (false) timerInstance.stopTimer(true);                //stop this timer, save item,
////                status.setStatus(ItemStatus.DONE, false);
////                timedItem.setStatus(status.getStatus());
//            if (itemStatus != null)
//                timedItem.setStatus(itemStatus);
//        }
//        timerInstance.stopTimerOnTimedItemAndUpdateActualsAndSave(true);
//        moveToNextTask();
//        refreshOrShowTimerUI();
//        refreshFormOnTimerUpdate();
//    }
//
////    private void gotoNext(TimerInstance timerInstance) {
////        gotoNext(timerInstance, null, null);
////    }
//    private void gotoNextXXX() {
////        gotoNext(getCurrentTimerInstanceN(), null, null);
//        gotoNextXXX(getCurrentTimerInstanceN(), getCurrentTimerInstanceN().getTimedItemN(), null);
//    }
//</editor-fold>
    void stopAllTimers() {
        //exit timer altogether: save each timed item, pop/delete all timers
//        while (TimerStack.getInstance().activeTimers.size() > 0) {
        if (activeTimers.size() > 0) {
            while (activeTimers.size() > 0) {
                TimerInstance2 timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
                timerInstance.stopTimerOnTimedItemAndUpdateActualsAndSave(false, true);//dont't save timerInstance since deleted below
                timerInstance.deleteInstance();
            }
            fireChangedEvent();
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     *
     * @param contentPane
     * @param fullScreenTimer
     */
//    public void startTimerOnNextOrExitIfNone(TimerInstance timerInstanceXXX, Container contentPane) {
//    public void startTimerOnNextOrExitIfNone(Container contentPane, boolean fullScreenTimer) {
//    private void resetTimerSmallContainerXXX() {
//        setSmallContainer(null);
//    }
//    private void setSmallContainer(Container smallContainer) {
//        this.smallContainer = smallContainer;
//    }
//    private static Container getContentPaneSouth() {
//        return getContentPaneSouth(Display.getInstance().getCurrent());
//    }
//    private static Container getContentPaneSouthXXX(Form form) {
////        Form form = Display.getInstance().getCurrent();
//        if (form != null) {
//            Container formContentPane = form.getContentPane();
//            if (!(form instanceof ScreenTimer6)) {
//                Layout contentPaneLayout = formContentPane.getLayout();
//                if (contentPaneLayout instanceof MyBorderLayout) {
//                    Component southComponent = ((MyBorderLayout) contentPaneLayout).getSouth();
//                    if (southComponent instanceof Container) {
//                        return (Container) southComponent;
//                    }
//                }
//            }
//        }
//        return null;
//    }
    /**
     * return the container into which the smallTimer should be interted. Return
     * null if no smallTimer should be shown. Override to disable smallTimer or
     * place it somewhere else.
     *
     * @return
     */
//    static protected Container getContainerForSmallTimerXXX() {
//        return getContainerForSmallTimerXXX(Display.getInstance().getCurrent());
//    }
//    static protected Container getContainerForSmallTimerXXX(Form form) {
//        Container timerContainer = null;
////        Form form = this;
////        if (form instanceof ScreenListOfItemLists || form instanceof ScreenListOfItems
////                || form instanceof ScreenStatistics
////                || form instanceof ScreenListOfWorkSlots || form instanceof ScreenListOfCategories) {
//        if (form instanceof MyForm && !(form instanceof ScreenCategoryPicker
//                //                || form instanceof ScreenListOfAlarms //DO show timer in alarmHandler, since likely that alarms will interrupt when timing a task, so should not risk to forget timer is running
//                || form instanceof ScreenLogin || form instanceof ScreenObjectPicker
//                || form instanceof ScreenRepair || form instanceof ScreenTimer6)) {
////        }else {
//            Container formContentPane = form.getContentPane();
//            Layout contentPaneLayout = formContentPane.getLayout();
//            if (contentPaneLayout instanceof MyBorderLayout) {
////                timerContainer = getContentPaneSouth(form);
//                Component southComponent = ((MyBorderLayout) contentPaneLayout).getSouth();
//                if (southComponent instanceof Container) {
//                    timerContainer = (Container) southComponent;
//                } else if (southComponent == null) {
//                    Container newCont = new Container(BoxLayout.y());
//                    formContentPane.add(MyBorderLayout.SOUTH, newCont);
//                    timerContainer = newCont;
//                }
//            }
//        }
//        return timerContainer;
//    }
//    private static void refreshFormOnTimerUpdateXXX() {
//        Form form = Display.getInstance().getCurrent();
//        if (form instanceof MyForm && !(form instanceof ScreenTimer6)) { //don't do when showing full screen timer
//            ((MyForm) form).refreshAfterEdit();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container getSmallContainerXXX() {
////        return smallContainer;
////        Container south = getContentPaneSouth();
//        Container south = MyForm.getContainerForSmallTimer();
//        if (south != null && south.getComponentCount() > 0) {
//            ASSERT.that(south.getComponentCount() == 1, "too many components in SmallTimerContainer");
//            Component smallTimerContainer = south.getComponentAt(0);
//            if (smallTimerContainer instanceof Container) {
//                return (Container) smallTimerContainer;
//            }
//        }
//        return null;
//    }
//    private boolean isSmallContainerCurrentlyShown() {
//        Container smallContainer = getSmallContainer();
//        if (smallContainer == null || smallContainer.getParent() == null || smallContainer.getComponentForm() == null || smallContainer.getComponentForm() != Display.getInstance().getCurrent()) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    //    private  Container getOrMakeSmallContainer() {
    //        Container smallContainer = getSmallContainer();
    //        if (smallContainer == null) {
    //            TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
    //            if (timerInstance != null) {
    ////                timerInstance.setTimerContainer(new Container());
    //                timerInstance.setTimerFullScreen(false);
    //                smallContainer = new Container();
    ////                buildContentPane(smallContainer, timerInstance, timerInstance.isTimerFullScreen(), null); //false=>small timer container
    //                buildContentPane(smallContainer, timerInstance, false, null); //false=>small timer container
    //                setSmallContainer(smallContainer);
    //            }
    //        }
    //        return smallContainer;
    //    }
    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * called when using Back from full screen timer UI or when starting any
     * screen that will show the small timer ui. after being added the small
     * timer UI will update itself via the UITimer callbacks
     *
     * @param form add to the SOUTH container of this form, if form is null then
     * add to Display.get
     */
//    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form) {
////        return addSmallTimerWindowIfTimerIsRunning(form, getInstance().getSmallContainer());
////        return addSmallTimerWindowIfTimerIsRunning(form, buildContentPaneSmall(form)); //make a new container each time
//        return addSmallTimerWindowIfTimerIsRunning(form, null); //make a new container each time
//
//    }
//
//    public static boolean addSmallTimerWindowIfTimerIsRunningXXX(Form form, Container timerContainer) {
////        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
////        if (timerInstance != null) {
//////            Container timerForContainer = MyForm.getContainerForSmallTimer();
////            Container timerForContainer = MyForm.getContainerForSmallTimer(form);
////            if (timerForContainer != null) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container timerForContainer = getContainerForSmallTimer(form);
//            if (timerForContainer != null) { //if no container, do nothing (avoid e.g. the case where ScreenLogin tries to load timers before login completed)
//                if (timerContainer == null) {
//                    timerContainer = buildContentPaneSmall(form);
//                }
//                if (timerContainer != null) { //buildContentPaneSmall returns null if no timer active
////            Container timerForContainer = MyForm.getContainerForSmallTimer();
////                if (timerContainer != null && timerContainer.getParent() != null) {
////                    if (timerContainer.getParent() != null) {
////                        timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
////                    }
//                    timerContainer.remove();
////                    if (TimerStack.getInstance().smallContainer!=null )
////                        TimerStack.getInstance().smallContainer.remove();
//                    TimerStack.getInstance().removeTimerSmallContainer();
//                    timerForContainer.add(timerContainer);
//                    TimerStack.getInstance().smallContainer = timerContainer;
////                if (form.get)
//                    if (false) {
//                        form.animateLayout(300); //this call may be creating index out of bound in toolbar if called before the form is shown
//                    }
//                    return true;
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static boolean addSmallTimerWindowIfTimerIsRunningXXX(Form form, Container timerContainer) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container formContentPane = form.getContentPane();
//            if (!(form instanceof ScreenTimer6)) {
//                Layout contentPaneLayout = formContentPane.getLayout();
//                if (contentPaneLayout instanceof BorderLayout) {
//                    Component southComponent = getContentPaneSouth(form);
//                    ASSERT.that(southComponent == null, "SOUTH should be empty in all screens where we add a small timer");
//                    if (southComponent == null) { //if south container is not already used for something else //TODO!!! shouldn't happen, but need to check
//                        if (timerContainer != null) {
//                            if (timerContainer.getParent() != null) {
//                                timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
//                            }
//                            formContentPane.add(BorderLayout.SOUTH, timerContainer);
//                            TimerStack.getInstance().smallContainer = timerContainer;
//                            form.animateLayout(300);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static boolean addSmallTimerWindowIfTimerIsRunningOLD(Form form, Container timerContainer) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
//            Container formContentPane = form.getContentPane();
//            if (!(form instanceof ScreenTimer6)) {
//                Layout contentPaneLayout = formContentPane.getLayout();
//                if (contentPaneLayout instanceof BorderLayout) {
//                    Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
//                    ASSERT.that(southComponent == null, "SOUTH should be empty in all screens where we add a small timer");
//                    if (southComponent == null) { //if south container is not already used for something else //TODO!!! shouldn't happen, but need to check
////                    Container timerContainer = getInstance().getSmallContainer(); //timerInstance.getTimerContainer();
//                        if (timerContainer != null) {
//                            if (timerContainer.getParent() != null) {
//                                timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
//                            }
////                        formContentPane.add(CN.SOUTH, timerContainer);
//                            formContentPane.add(BorderLayout.SOUTH, timerContainer);
//                            form.animateLayout(300);
//                            return true;
//                        }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
//    private void removeTimerSmallContainerXXX() {
////         removeTimerSmallContainer(smallContainer);
////remove small timer UI
////        Container smallContainer = getSmallContainer();
////        if (smallContainer != null) {
////            Form form = smallContainer.getComponentForm();
////            Container parent = smallContainer.getParent();
////            if (parent != null) {
////                parent.removeComponent(smallContainer); //remove small timer window from its parent
////            }
////            if (form != null) {
////                form.animateLayout(300); //animate form to remove timerPane
////            } else if (parent != null) {
////                parent.animateLayout(300); //animate form to remove timerPane
////            }
////            if (false) {
////                setSmallContainer(null);
////            }
////        }
////        if (smallContainer != null && smallContainer.getParent() != null) {
////            smallContainer.getParent().removeComponent(smallContainer);
////            smallContainer = null;
////        }
//        if (smallContainer != null) {
//            smallContainer.remove();
//        }
//        smallContainer = null;
//    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUIOLD(MyForm myCurrentForm) {//, boolean showBigTimer) {
//        /**
//         * different situations to account for: No (more) timers, remove any
//         * shown (remove smallTimer or Back from bigTimer). Replay (with show
//         * smallTimer or not) - do not show timer until last screen. Refresh
//         * (either small or big timer, which is already shown) when timer has
//         * changed. Show timer for first timer = add smallTimer or show
//         * BigTimer.
//         */
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//        if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
//            if ((myCurrentForm instanceof ScreenTimer6)) {
//                //if (false) myCurrentForm.showPreviousScreen(true); //exit to previous screen //DON'T exit here, will be done by commands in Big Timer
//                myCurrentForm.showPreviousScreen(true); //exit to previous screen
//            } else {
//                myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
//            }
//        } else { //there is an active timer
//            if (myCurrentForm instanceof ScreenTimer6) { //if full screen Timer is active, refresh it
//                if (false) {
//                    myCurrentForm.refreshAfterEdit(); //already done in ScreenTimer6
//                }
//            } else { //other form than ScreenTimer6
//                if (!timerInstance.isFullScreen() && MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                    timerInstance.setFullScreen(false); //set true in case we moved to full screen because of the settings
//                    timerInstance.saveMe();
//                    myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                    Container smallTimer = buildContentPaneSmallN(myCurrentForm); //refresh the small container
//                    if (smallTimer != null) {
//                        myCurrentForm.addSmallTimerCont(smallTimer);
//                        if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null) {
//                            myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                        }
//                    }
//                } else {
////                    timerInstance.setFullScreen(true); //NOW donw in ScreenTimer6 //set true in case we moved to full screen because of the settings
////                    DAO.getInstance().saveInBackground(timerInstance);
//                    new ScreenTimer6(myCurrentForm, timerInstance).show();
//                }
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    void refreshOrShowTimerUIXXX(Form currentForm) {
//        /**
//         * different situations to account for: No (more) timers, remove any
//         * shown (remove smallTimer or Back from bigTimer). Replay (with show
//         * smallTimer or not) - do not show timer until last screen. Refresh
//         * (either small or big timer, which is already shown) when timer has
//         * changed. Show timer for first timer = add smallTimer or show
//         * BigTimer.
//         */
//        if (false && ReplayLog.getInstance().isReplayInProgress() && !ReplayLog.getInstance().isReplayAtLastCommand()) {
//            return; //don't add small Timers to any replayed screens
//        }
////        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
////        if ()
////        Form currentForm = Display.getInstance().getCurrent();
//        MyForm myCurrentForm = null;
//        if (currentForm instanceof MyForm) {
//            myCurrentForm = (MyForm) currentForm;
//        } else {
//            ASSERT.that(false, "Form not instanceof MyForm: " + currentForm);
//            return;
//        }
//        if (timerInstance == null) { //no (or no more) timers, removeSmall or exit from big timer
//            if (myCurrentForm instanceof ScreenTimer6) {
//                if (false) {
//                    myCurrentForm.showPreviousScreen(true); //exit to previous screen //DON'T exit here, will be done by commands in Big Timer
//                }
//            } else {
////                if (myCurrentForm.removeSmallTimerCont()) //remove old smallTimer (if there is one)
////                    myCurrentForm.revalidateWithAnimationSafety();
//                myCurrentForm.removeSmallTimerCont(); //NO need to revalidate, done in refreshAfterEdit which calls refreshShowTimerUI //remove old smallTimer (if there is one)
//            }
//        } else { //there is an active timer
//            if (myCurrentForm instanceof ScreenTimer6) { //if full screen Timer is active, refresh it
//                if (false) {
//                    myCurrentForm.refreshAfterEdit();
//                }
////                myCurrentForm.revalidateWithAnimationSafety(); //NOT sufficient, need to rebuild timer screen with commands related to new item
////            } else if (timerInstance.isFullScreen()) { //if running timer was running in FullScreen, start up in full screen again (on app relaunch)
////                new ScreenTimer6(myCurrentForm, timerInstance).show();
//            } else { //other form than ScreenTimer6
////<editor-fold defaultstate="collapsed" desc="comment">
////                Container previousSmallTimer = myCurrentForm.getSmallTimerCont();
////                if (previousSmallTimer != null) { //smallTimer already visible
//////                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
////                    myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
////                    smallTimer = buildContentPaneSmall(myCurrentForm); //refresh the small container
////                    myCurrentForm.addSmallTimerCont(smallTimer); //remove old smallTimer (if there is one)
//////                    myCurrentForm.revalidateWithAnimationSafety();
////                    if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null)
////                        myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
////                } else { //show timer for first time
////                    if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
////                        myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
////                        myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
////                        smallTimer = buildContentPaneSmall(myCurrentForm); //refresh the small container
////                        myCurrentForm.addSmallTimerCont(smallTimer); //remove old smallTimer (if there is one)
//////                        myCurrentForm.revalidateWithAnimationSafety();
////                        if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null)
////                            myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
////                    } else {
//////                        new ScreenTimer6(previousForm, timerInstance).show();
////                        new ScreenTimer6(myCurrentForm, timerInstance).show();
////                    }
////                }
////</editor-fold>
//                myCurrentForm.removeSmallTimerCont(); //remove old smallTimer (if there is one)
//                if (//timerInstance.isFullScreen() ||
//                        !(MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean()
//                        && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
////                        new ScreenTimer6(previousForm, timerInstance).show();
//                    timerInstance.setFullScreen(true); //set true in case we moved to full screen because of the settings
//                    new ScreenTimer6(myCurrentForm, timerInstance).show();
//                } else {//if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                    myCurrentForm.setKeepPos(new KeepInSameScreenPosition()); //is this necessary if smallTimer is in South container??
//                    Container smallTimer = buildContentPaneSmallN(myCurrentForm); //refresh the small container
//                    if (smallTimer != null) {
//                        myCurrentForm.addSmallTimerCont(smallTimer);
//                        if (smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING) != null) {
//                            myCurrentForm.setStartEditingAsyncTextArea((TextArea) smallTimer.getClientProperty(SMALL_TIMER_TEXT_AREA_TO_START_EDITING)); //on interrupt, start editing the text area
//                        }//                    myCurrentForm.revalidateWithAnimationSafety();
//                    }
//                }
//            }
//        }
//    }
//
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    static void updateCurrentlyTimedItemWithEditedElapsedTimeXXX(TimerInstance2 timerInstance, long elapsedTime, boolean setTotalTime) {
//        //                TimerInstance2 timerInstance2 = TimerStack.getInstance().getCurrentTimerInstanceN();
////            TimerInstance timerInstance2 = getCurrentTimerInstanceN();
//        //UI: OK to longPress a running timer (no need to stop it, will simply adjust timer on Done
//        if (timerInstance != null) {
//            Item timedItem = timerInstance.getTimedItemN();
//
//            long oldElapsedTime = timerInstance.getElapsedTime();
//            long oldElapsedTotalDisplayedTime = timerInstance.getElapsedTimeToDisplay();
//
//            if (MyPrefs.timerShowTotalActualInTimer.getBoolean() || setTotalTime) {
//                //adjust elapsed time
////                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
//                long editedElapsedTime = elapsedTime;
//                long diff = editedElapsedTime - oldElapsedTotalDisplayedTime; //NB can also be negative if timer was increased
//                long newElapsedTime = Math.max(0, oldElapsedTime + diff);
////                        timerInstance2.setElapsedTime(newElapsedTime);
//                timerInstance.updateElapsedTime(newElapsedTime);
//
//                //since we have edited the total (elapsed+old actual) we may also need to reduce the old actual
////                            long itemActualReduction = -diff > oldElapsedTime ? -diff - oldElapsedTime : 0;
//                long itemActualReduction = oldElapsedTime + diff < 0 ? -(oldElapsedTime + diff) : 0;
//                if (itemActualReduction > 0) {
//                    long itemOldActual = timedItem.getActualForTaskItself();
//                    long itemNewActual = itemOldActual - itemActualReduction;
//                    timedItem.setActualForTaskItself(itemNewActual, false);
//                }
//            } else { //enough to just adjust the elapsed time
////                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
//                long editedElapsedTime = elapsedTime;
////                        timerInstance2.setElapsedTime(editedElapsedTime);
//                timerInstance.updateElapsedTime(editedElapsedTime);
//            }
//            DAO.getInstance().saveToParseNow(timedItem);
//        }
//    }
//</editor-fold>
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object actionSource = evt.getSource();
        if (actionSource instanceof TimerInstance2) {
            int keyEvent = evt.getKeyEvent(); //use the keyEvent to carry the actual event

            if (keyEvent == TimerInstance2.ACTION_EVENT_REMOVED) {
                TimerInstance2 currentTimerInstance = getCurrentTimerInstanceN();
                activeTimers.remove(actionSource); //no need to save anything since timerInstance is deleted in Parse server and won't get reloaded if app is restarted
                if (currentTimerInstance != null) {
                    if (actionSource == currentTimerInstance) {
                        TimerInstance2 newTimerInstance = getCurrentTimerInstanceN();
                        if (newTimerInstance!=null&&newTimerInstance.isWasInterruptedWhileRunning()) {
                            newTimerInstance.startTimer(true, true); //restart previously interrupted timer and save timerInstance and timedItem (with timeStamp)
                        }
                    } else {
                        //nothing: if previously interrupted timer was not running, leave it paused in the updated UI
                    }
//            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_REMOVED));//inform the stack
                    fireChangedEvent(); //inform the UI to refresh (either 
                } else {
//                    fireDeletedEvent(); //no more timerInstances
                    fireChangedEvent(); //no more timerInstances NB no need for deletedEvent since the UI will know there is nothing more to time when calling getCurrentInstance
                }
            } else if (keyEvent == TimerInstance2.ACTION_EVENT_CHANGED) {
//            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_CHANGED));//inform the stack
                fireChangedEvent(); //inform the UI to refresh
            }
        } else {
            ASSERT.that("TimerStack2.actionPerformed received from something other than TimerInstance2, source=" + actionSource);
        }
    }

    /**
     * if possiblyTimedItem is being timed by a TimerInstance, return the
     * elapsed time (whether the Timer is interrupted/onStack, or paused or not)
     *
     * @param possiblyTimedItem
     * @return
     */
    public long getOngoingTimerDurationFor(Item possiblyTimedItem) {
        for (TimerInstance2 timerInstance : activeTimers) { //activeTimers can never be null
            if (Objects.equals(possiblyTimedItem, timerInstance.getTimedItemN())) {
                return timerInstance.getElapsedTime();
            }
        }
        return 0;
    }

}
