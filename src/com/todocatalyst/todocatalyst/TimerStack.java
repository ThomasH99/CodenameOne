/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanButton;
import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Button;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.LayeredLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.util.UITimer;
import static com.todocatalyst.todocatalyst.ScreenTimer6.showPreviousScreenOrDefault;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
class TimerStack {
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
    private List<TimerInstance> activeTimers;
    Container smallContainer = null;
//    private List<TimerStackEntry> previouslyRunningTimers;
//    TimerStackEntry currEntry;

//    private static List<TimerInstance> INSTANCE;
    private static TimerStack INSTANCE;
    final static String TIMER_REPLAY = "StartTimer-";

    public static TimerStack getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new TimerInstance();
//            DAO.getInstance().getTemplateList();
            INSTANCE = new TimerStack(DAO.getInstance().getTimerInstanceList());
        }
        return INSTANCE;
    }

    private TimerStack(List<TimerInstance> timerList) {
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

    TimerInstance getCurrentTimerInstance() {
        int size = activeTimers.size();
        if (size > 0) {
            return activeTimers.get(size - 1); //return last timer
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void pushCurrentTimerEntryAndCalcNextTask(TimerStackEntry newEntry) {
//        previouslyRunningTimers.add(newEntry);
//        currEntry = newEntry;
////            updateEntry();
//        save();
//    }
//
//    void popCurrentTimerEntry() {
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (previouslyRunningTimers.size() > 0) {
////                TimerStackEntry entry = previouslyRunningTimers.remove(previouslyRunningTimers.size() - 1);
////                save();
////                return entry;
////            } else {
////                return null;
////            }
////</editor-fold>
//        previouslyRunningTimers.remove(previouslyRunningTimers.size() - 1);
//        if (previouslyRunningTimers.size() >= 1) {
//            currEntry = previouslyRunningTimers.get(previouslyRunningTimers.size() - 1);
//        } else {
//            currEntry = null;
//        }
////            updateEntry();
//        save();
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * removes all entries from TimerStack. Used when exiting Timer to clean
//     * up
//     */
////        void removeAllEntries() {
////            for (Object o : previouslyRunningTimers) {
////                previouslyRunningTimers.remove(o);
////            }
////            entry=null;
////            save();
////        }
//    /**
//     * saves an already running item (if there is one, otherwise nothing
//     * happens)
//     */
////        void pushItemXXX(Item item, long elapsedTime) {
////            pushItemXXX(item, elapsedTime, false);
////        }
////        void pushItemXXX(Item item, long elapsedTime, boolean running) {
////            boolean savedLocally = false;
////            if (item.getObjectId() == null) { //item has not been saved before (e.g. interrupt task)
////                DAO.getInstance().save(item);
////                savedLocally = true;
////            }
////            previouslyRunningTimers.add(new PreviouslyRunningTimerEntry(item.getObjectId(), elapsedTime, running, savedLocally));
////            save();
////        }
//    /**
//     * save new item
//     */
////        void pushNewItemWithElapsedTimeAndRunningStateXXX(Item item) {
////            pushItemXXX(item, getTimerElapsedTimeMillis(), isTimerRunning());
////        }
////</editor-fold>
//    int size() {
//        return previouslyRunningTimers.size();
//    }
//
//    boolean timerEntryExists() {
//        return previouslyRunningTimers.size() >= 1;
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns last (current) entry in TimerStack
//     *
//     * @return
//     */
//    Item getInterruptedItem() {
////            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 2); //entry before last
//        if (previouslyRunningTimers.size() >= 2) {
//            return previouslyRunningTimers.get(previouslyRunningTimers.size() - 2).timedItem; //entry before last
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
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
//    void save() {
//        Storage.getInstance().writeObject(TIMER_STACK_ID, previouslyRunningTimers);
//    }
    /**
    show a popup about no more tasks in project or itemList (if both are defined, use the name of the itemList). Depends on setting!
    @param project
    @param itemList 
     */
    private static void showNoMoreTasksNotificationWhenRelevant(Item project, ItemList itemList) {
        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
            if ((project != null && project.isProject()) || itemList != null) { //only show if item or itemList are defined
                String itemOrListName = (itemList != null ? itemList.getText() : project.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
                //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
                Dialog.show("´Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
            }
        }
    }

    /**
    don't show for Interrupt/InstantTasks
    @param project
    @param itemList 
     */
    private static void showTimerAlreadyRunningOnListOrProjectCloseFirstNotification(Item project, ItemList itemList) {
//        if (MyPrefs.timerShowPopupDialogWhenNoMoreTasksInProjectOrItemList.getBoolean()) {
        if ((project != null && project.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = (itemList != null ? itemList.getText() : project.getText()); //UI: if arrived at last subtask in a project which is the last in a list, use the name of the list!
            //                Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\", click OK to return", "OK", null);
            Dialog.show("´Timer", "Timer already active for \"" + itemOrListName + "\". Stop before starting on another ", "OK", null);
        }
    }

    private static void showTimerWasAlreadyRunningForTaskTimeSavedNotification(Item timedItem, long elapsedTimeMillis, Item project, ItemList itemList) {
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
    private static void showTimerCannotBeStarted(boolean interruptInterruptingInterrupt, Item timedItem) {
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    void deleteTimerInfoInStorage() {
//        if (Storage.getInstance().exists(TIMER_STACK_ID)) {
//            Storage.getInstance().deleteStorageFile(TIMER_STACK_ID);
//        }
//    }
////    public void launchTimer() {
////        //check if timer is running on another device
////        //pause previous running timer (pause if running, record whether was running or already paused
////        //save previously
////        ScreenTimer2 timer = new ScreenTimer2();
////        timers.add(timer);
////        DAO.getInstance().saveInBackground(INSTANCE);
////        timer.show();
//    }
//
//    /**
//    only interrupt timers can be launched multiple times, a normal timer going through a list can only be launched once.
//     */
//    public void launchNewInterruptTimer() {
//        //pause previous running timer (pause if running, record whether was running or already paused
//        ScreenTimer2 timer = new ScreenTimer2();
//        timers.add(timer);
//        DAO.getInstance().saveInBackground(INSTANCE); //save change TimerStack to server
//        timer.show();
//    }
//
//    public void popTimer() {
//        if (timers.size() > 0) {
//            ScreenTimer2 timer = timers.remove(timers.size() - 1);
//            timer.showPreviousScreenOrDefault(true);
//            DAO.getInstance().saveInBackground(INSTANCE);
//        }
//    }
//
//    private void deleteTimer() {
//        if (timers.size() > 0) {
//            ScreenTimer2 timer = timers.remove(timers.size() - 1);
//            timer.showPreviousScreenOrDefault(true);
//            DAO.getInstance().saveInBackground(INSTANCE);
//        }
//    }
//
//    public void showCurrentTimer() {
//        if (timers.size() > 0) {
//            ScreenTimer2 timer = timers.remove(timers.size() - 1);
//            DAO.getInstance().saveInBackground(INSTANCE);
//
//            timer.show();
//        }
//    }
//</editor-fold>
    /**
    if the timer stack has been updated on the server, set INSTANCE to this latest version and make necessary updates to the UI (e.g. show that
    a timer is running now if not the case before (either in Timer main UI or in mini-timer shown in other screens), or if no timers are running anymore, remove mini-Timer
    or refresh main timer UI (to show other running timer, or ?? if no more timers are running?? - exit Timer UI), or if another timer is running refresh. 
    @return true if changes were made to know that UI should be refreshed. 
     */
    public void refreshTimersFromParseServer() {
        //TODO!!! fetch in background, and launch any necessary UI updates via runSerially on EDT. 
//        Object fresh = DAO.getInstance().fetchIfChangedOnServer(this);
//        if (fresh != null) {
//            INSTANCE = (TimerInstance) fresh;
        INSTANCE = new TimerStack(DAO.getInstance().getTimerInstanceList());
//            return true;
//        }
//        return false;
    }

    //**************** PUBLIC INTERFACE ****************
    private void addNewTimerInstance(TimerInstance newTimerInstance) {
        activeTimers.add(newTimerInstance); //add last as definitely the most recent instance
        DAO.getInstance().saveInBackground(newTimerInstance);
    }

    /**
    stop timer for item, and find the next item, and start timer on that (as if done via Timer UI). Will 
    @param item
    @return true if a timer was found and stopped
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean stopTimerXXX(Item item) {
////        for (TimerInstance timer : activeTimers) {
//        Iterator<TimerInstance> it = activeTimers.iterator();
//        while (it.hasNext()) {
//            TimerInstance timer = it.next();
//            if (timer.getTimedProject().getObjectIdP().equals(item.getObjectIdP())) {
//
//            }
//            if (timer.getTimedItem().getObjectIdP().equals(item.getObjectIdP())) {
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
    public boolean stopTimerIfRunningOnThisItemOnStartTimerOnNext(Item item) { //boolean continueWithNextItem // 
        if (item == null) {
            return false;
        }
//        for (TimerInstance timer:activeTimers) {
        Iterator<TimerInstance> it = activeTimers.iterator();
        while (it.hasNext()) {
            TimerInstance timerInstance = it.next();

            if (item.equals(timerInstance.getTimedItem())) { //if timer is (or was if down in the stack) running on this specific task
                boolean timerWasRunning = timerInstance.isRunning();
                if (timerWasRunning) {
                    //stop, update this item
                    timerInstance.stopTimer(false);
                }
                //update item with elapsed time 
                item.setActualEffort(timerInstance.isTimerShowActualTotal() ? timerInstance.getElapsedTime()
                        : item.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime());
                //don't save item, it'll be done from whereever this method was called

                //start next or exit timer if no more available
                return startTimerOnNext(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
            }
        }
        return false;
    }

    /**
     * starts a timer for item (possibly as an interrupt) or itemlist (. If timer is already running, the
     * previous item is pushed and will continue afterwards, otherwise the timer
     * will be started normally on item (not as an interrupt). 
     *
     * @param timedItemOrProject
     * @param previousForm
     * @param doneAction
     */
    private void startTimer(Item timedItemOrProject, ItemList itemList, MyForm previousForm, boolean interruptOrInstantTask) {
        //stop and push previously timed item+context
//        startInterrupt(item, previousForm, true, true);
//        launchTimerImpl(item, null, null, previousForm, true);
        ASSERT.that(!interruptOrInstantTask || itemList == null, "cannot have an interrupt task with a list");
        boolean previousTaskWasInterrupted = false;

        TimerInstance previousTimerInstance = getCurrentTimerInstance();
        if (previousTimerInstance != null) {
            if ((previousTimerInstance.getTimedItem().isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) //interrupting an interrupt enable
                    || (!previousTimerInstance.getTimedItem().isInteruptOrInstantTask() && !interruptOrInstantTask
                    && MyPrefs.timerItemOrItemListCanInterruptExistingItemOrItemList.getBoolean())) { //normal item/itemList interrupt other item/itemList
                if (previousTimerInstance.isRunning()) { //pause current timer if running
                    previousTaskWasInterrupted = true;
                    previousTimerInstance.setWasInterruptedWhileRunning(true, true); //pause and save!
                    if (interruptOrInstantTask) {
                        timedItemOrProject.setTaskInterrupted(previousTimerInstance.getTimedItem()); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
                    }
                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItem().getText() + "\", will continue after this "
                            + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
                }
            } else {
                if (previousTimerInstance.getTimedItem().isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
                    showTimerCannotBeStarted(previousTimerInstance.getTimedItem().isInteruptOrInstantTask(), previousTimerInstance.getTimedItem());
                    return;
                }
            } //not a valid 
        }

        assert !interruptOrInstantTask || timedItemOrProject.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task

        TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, itemList, MyPrefs.timerAutomaticallyStartTimer.getBoolean(), MyPrefs.timerAutomaticallyGotoNextTask.getBoolean());
        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
        Item timedItem = newTimerInstance.getTimedItem();
        if (timedItem != null) {
            addNewTimerInstance(newTimerInstance);
            refreshOrShowTimerUI();
        } else { //nothing to time
            //no save of new TimerInstance if not tasks to time
            showNoMoreTasksNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getItemList());
        }
    }

    public void startTimerOnItem(Item timedItem, MyForm previousForm) {
        startTimer(timedItem, null, previousForm, false);
    }

    public void startTimerOnItemList(ItemList itemList, MyForm previousForm) {
        startTimer(null, itemList, previousForm, false);
        //TODO set autostart!
    }

    public void startInterruptOrInstantTask(Item interruptOrInstantTask, MyForm previousForm) {
        ASSERT.that(interruptOrInstantTask.isInteruptOrInstantTask());
        startTimer(interruptOrInstantTask, null, previousForm, true);
        //TODO set autostart!
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void startInterruptOrInstantTask(Item interruptOrInstantTask, MyForm previousForm) {
//        //stop and push previously timed item+context
////        startInterrupt(item, previousForm, true, true);
////        launchTimerImpl(item, null, null, previousForm, true);
//        TimerInstance currentTimer = getCurrentTimerInstance();
//        if (currentTimer != null) {
//            if (currentTimer.isRunning()) { //pause current timer if running
//                currentTimer.setWasInterruptedWhileRunning(true, true); //pause and save!
//                interruptOrInstantTask.setTaskInterrupted(currentTimer.getTimedItem()); //only set if timer was actually running, otherwise does not qualify as an interrupt
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
    /**
    
    @param contentPane
    @param fullScreenTimer 
     */
//    public void startTimerOnNextOrExitIfNone(TimerInstance timerInstanceXXX, Container contentPane) {
//    public void startTimerOnNextOrExitIfNone(Container contentPane, boolean fullScreenTimer) {
    private Item findNextTimedItem() {
        TimerInstance timerInstance = getCurrentTimerInstance();

        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
        Item nextTimedItem;
        nextTimedItem = timerInstance.updateToNextTimerItem(false, false); //get next timed item without making any changes
        //if none found try rest of timerstack (if any)
        int index = activeTimers.size() - 2; //init to second last timerInstance
        while (nextTimedItem == null && index >= 0 && index < TimerStack.getInstance().activeTimers.size()) { //as long as there are valid timerInstances not tried yet
            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
            timerInstance = activeTimers.get(index); //pop last timerInstance since it has no more tasks
            nextTimedItem = timerInstance.updateToNextTimerItem(false, false); //also update project and save
            index--;
        }
        return nextTimedItem;
    }

    /**
    find next suitable for timer and (re-)start the timer if it was running when interrupted
    returns true if timer was started on another item. 
     */
    private boolean startTimerOnNext() {
//        TimerInstance timerInstance = activeTimers.get(activeTimers.size() - 1);
        TimerInstance timerInstance = getCurrentTimerInstance();

        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
        Item nextTimedItem;
//        TimerInstance lastValidTimerInstance = timerInstance;
        nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
        //if none found try rest of timerstack (if any)
        while (nextTimedItem == null && TimerStack.getInstance().activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
//            getInstance().removelasttimer(); 
            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            if (timerInstance != null) {
//                lastValidTimerInstance = timerInstance;
//            }
            timerInstance.deleteInstance();
            if (activeTimers.size() > 0) { //try with (new)  timerInstance (if any)
                timerInstance = activeTimers.get(activeTimers.size() - 1);
                if (timerInstance != null) {
                    nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //also update project and save
                }
            }
        }

        //if a next item was found
        if (nextTimedItem != null) {
            if (timerInstance.wasInterruptedWhileRunning() || timerInstance.isAutostart()) {
                timerInstance.startTimer(timerInstance.wasInterruptedWhileRunning());
                timerInstance.setWasInterruptedWhileRunning(false, true); //un-pause this timer for next time (needed?)
            }
            refreshOrShowTimerUI();
            return true;
        } else { //no more tasks, show popup (or Toastbar?)
//            if (lastValidTimerInstance.getTimedProject() != null || lastValidTimerInstance.getItemList() != null) {
//                showNoMoreTasksNotificationWhenRelevant(lastValidTimerInstance.getTimedProject(), lastValidTimerInstance.getItemList());
            if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
                showNoMoreTasksNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
                //remove timer UI
                removeTimerSmallContainer();
            } //else: inbterrupt, so don't display any messages
            return false;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void startTimerOnNextOrExitIfNoneXXX() {
//        TimerInstance timerInstance = activeTimers.get(activeTimers.size() - 1);
//        Container contentPane = timerInstance.getTimerContainer(); //get contentPane to reuse it
//        boolean fullScreenTimer = timerInstance.isTimerFullScreen();
//
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//        Item nextTimedItem;
//        TimerInstance lastValidTimerInstance = timerInstance;
//        nextTimedItem = timerInstance.updateToNextTimerItem(true, true);
//        while (nextTimedItem == null && TimerStack.getInstance().activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
////            getInstance().removelasttimer();
//            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
//            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            if (timerInstance != null) {
//                lastValidTimerInstance = timerInstance;
//            }
//            timerInstance.deleteInstance();
//            if (activeTimers.size() > 0) { //try with (new)  timerInstance (if any)
//                timerInstance = activeTimers.get(activeTimers.size() - 1);
//                if (timerInstance != null) {
//                    nextTimedItem = timerInstance.updateToNextTimerItem(true, true);
//                }
//            }
//        }
//
//        //if a next item was found
//        if (nextTimedItem != null) {
//            timerInstance.startTimer(timerInstance.isAutostart());
//            buildContentPane(contentPane, timerInstance, fullScreenTimer, null); //build UI for new task in same container
//            contentPane.getComponentForm().revalidate(); //refresh UI
//        } else { //no more tasks, show popup (or Toastbar?)
//            if (lastValidTimerInstance.getTimedProject() != null || lastValidTimerInstance.getItemList() != null) {
//                showNoMoreTasksNotificationWhenRelevant(lastValidTimerInstance.getTimedProject(), lastValidTimerInstance.getItemList());
//                //remove timer UI
//                Form form = contentPane.getComponentForm();
//                if (form instanceof ScreenTimer6) {
//                    ((MyForm) form).showPreviousScreenOrDefault(fullScreenTimer); //exit full screen Timer UI, return to previous screen
//                } else {
//                    Container parent = contentPane.getParent();
//                    if (parent != null) {
//                        parent.removeComponent(contentPane); //remove small timer window from its parent
//                    }
//                    if (form != null) {
//                        form.animateLayout(300); //animate form to remove timerPane
//                    }
//                }
//            } //else: inbterrupt, so don't display any messages
//        }
//
//    }
//</editor-fold>
    /**
    stop and delete all ongoing timers, quitting the timer completely (on this and consequently on other devices)
     */
//    private void deleteAllActiveTimersOnExit() {
    private void exitTimer() {
        while (TimerStack.getInstance().activeTimers.size() > 0) {
//            getInstance().removelasttimer(); 
            TimerInstance timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks

            if (timerInstance.getElapsedTime() > 0) {
                Item timedItem = timerInstance.getTimedItem(); //get the item that is/was timed
                timedItem.setActualEffort(timerInstance.isTimerShowActualTotal() //update actual
                        ? timerInstance.getElapsedTime()
                        : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
                DAO.getInstance().saveInBackground(timedItem);
            }
            timerInstance.deleteInstance();
        }
//        removeTimerSmallContainer();
        refreshOrShowTimerUI();
    }

//    private void addSmallTimerUIToCurrentForm() {
//        addSmallTimerWindowIfTimerIsRunning(Display.getInstance().getCurrent());
//    }
    /**
    refresh timer UI if timers change for some reason (e.g. change on parse server, change because a timed item was deleted elsewhere, ...) or create a new UI if none was shown previously
     */
//    public void refreshOrAddTimerUIToCurrentFormXXX() {
//        smallContainer = null;
//        if (MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//            getSmallContainer();
//            addSmallTimerWindowIfTimerIsRunning(previousForm);
//            refreshOrAddTimerUI();
//        } else { //show full screen timer
//            Container contentPane = buildContentPane(previousForm, newTimerInstance, interruptOrInstantTask, null);
//            newTimerInstance.setTimerContainer(contentPane);
//            new ScreenTimer6(previousForm, newTimerInstance).show();
//        }
//    }
    private void resetTimerSmallContainer() {
        setSmallContainer(null);
    }

    private void setSmallContainer(Container smallContainer) {
        this.smallContainer = smallContainer;
    }

    private Container getSmallContainer() {
        return smallContainer;
    }

    private boolean isSmallContainerCurrentlyShown() {
        Container smallContainer = getSmallContainer();
        if (smallContainer == null || smallContainer.getParent() == null || smallContainer.getComponentForm() == null || smallContainer.getComponentForm() != Display.getInstance().getCurrent()) {
            return false;
        } else {
            return true;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private  Container getOrMakeSmallContainer() {
//        Container smallContainer = getSmallContainer();
//        if (smallContainer == null) {
//            TimerInstance timerInstance = getInstance().getCurrentTimerInstance();
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
    /**
    called when using Back from full screen timer UI or when starting any screen that will show the small timer ui. after being added the small timer
    UI will update itself via the UITimer callbacks
    @param form add to the SOUTH container of this form, if form is null then add to Display.get
     */
    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form) {
        return addSmallTimerWindowIfTimerIsRunning(form, getInstance().getSmallContainer());
    }

    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form, Container timerContainer) {
        TimerInstance timerInstance = getInstance().getCurrentTimerInstance();
        if (timerInstance != null) {
            Container formContentPane = form.getContentPane();
            Layout contentPaneLayout = formContentPane.getLayout();
            if (contentPaneLayout instanceof BorderLayout) {
                Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
                ASSERT.that(southComponent == null, "SOUTH should be empty in all screens where we add a small timer");
                if (southComponent == null) { //if south container is not already used for something else //TODO!!! shouldn't happen, but need to check
//                    Container timerContainer = getInstance().getSmallContainer(); //timerInstance.getTimerContainer();
                    if (timerContainer != null) {
                        if (timerContainer.getParent() != null) {
                            timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
                        }
                        formContentPane.add(CN.SOUTH, timerContainer);
                        form.animateLayout(300);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void removeTimerSmallContainer() {
//         removeTimerSmallContainer(smallContainer);
//remove small timer UI
        Container smallContainer = getSmallContainer();
        if (smallContainer != null) {
            Form form = smallContainer.getComponentForm();
            Container parent = smallContainer.getParent();
            if (parent != null) {
                parent.removeComponent(smallContainer); //remove small timer window from its parent
            }
            if (form != null) {
                form.animateLayout(300); //animate form to remove timerPane
            }
            setSmallContainer(null);
        }
//        smallContainer = null;
    }

    /**
    update the UI based on current timer (if any). Either add (or remove!) a small timer to current screen or launch the full screen timer UI
     */
    private void refreshOrShowTimerUI() {

        Form form = Display.getInstance().getCurrent();
        if (form == null) {
            return;
        }
        if (form instanceof ScreenTimer6) {
            //full screen timer currently shown, so refresh it
            ((ScreenTimer6) form).refreshAfterEdit();
        } else {
            TimerInstance timerInstance = getInstance().getCurrentTimerInstance();
            if (timerInstance == null) {
                //no timer to show, so remove any UI still shown (only relevant for smallTimer since timer screen will remove itself)
                removeTimerSmallContainer();
                return;
            }

            if (MyPrefs.timerEnableSmallTimerWindow.getBoolean() && (isSmallContainerCurrentlyShown() || MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
                Container small = new Container();
                buildContentPaneSmall(form, small); //refresh the small container
                if (addSmallTimerWindowIfTimerIsRunning(form, small)) {
                    setSmallContainer(small); //if successfully added, then save it
                }
            } else { //show full screen timer
                Form f = Display.getInstance().getCurrent();
                if (f instanceof MyForm) {
                    resetTimerSmallContainer();
                    new ScreenTimer6((MyForm) f, timerInstance).show();
                }
            }
        }
    }

    //**************** BUILD THE UI ****************
    /**
     * build the user interface of the Timer
     */
    protected static void buildContentPane(Container contentPane, TimerInstance timerInstance, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {

    }

    protected static void buildContentPaneFullScreen(Form form, Container contentPane, SaveEditedValuesLocally formPreviousValues) {
        buildContentPane(form, contentPane, true, formPreviousValues);
    }

    protected static void buildContentPaneSmall(Form form, Container contentPane) {
        buildContentPane(form, contentPane, false, null);
    }

    private static void buildContentPane(Form form, Container contentPane, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {

        ASSERT.that(form != null, "form cannot be null since it's needed for the UITimer");
        contentPane.removeAll();
//        contentPane.setLayout(fullScreenTimer ? BoxLayout.y() : BorderLayout.center());
        contentPane.setLayout(BoxLayout.y());

//        assert currEntry != null : "entry must always be defined";
        TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstance();
        Item timedItem = timerInstance.getTimedItem(); //currEntry.timedItem;
        ItemList itemList = timerInstance.getItemList();

        Button elapsedTimeButton = new Button("", "TimerTimer");
        MyTextArea description;
        MyCheckBox status = new MyCheckBox(timedItem.getStatus());
//    private Button status;
        MyTextArea comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
//    private Container commentCont;
        MyDurationPicker remainingEffort = new MyDurationPicker();
        MyDurationPicker effortEstimate = new MyDurationPicker();
        Label totalActualEffort = new Label();
        Button editItemButton;
        Button timerStartStopButton;
        SpanButton gotoNextTaskButtonWithItemText;
//        Picker elapsedTimePicker;
//        UITimer timer;
//        UITimer buzzerTimer;
        Map<Object, MyForm.UpdateField> parseIdMap2 = new HashMap<Object, MyForm.UpdateField>(); //create a new hashmap for this item

        ActionListener refreshTotalActualEffort = (e) -> {
            long totalEffort = MyPrefs.timerShowTotalActualInTimer.getBoolean()
                    ? timerInstance.getElapsedTime()
                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself();
            totalActualEffort.setText(MyDate.formatTimeDuration(totalEffort)); //false=don't show seconds in Total
            totalActualEffort.repaint();
        };
        ActionListener refreshElapsedTime = (e) -> {
            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
        };
        Runnable updateTimerDisplay = () -> {
//                new Runnable() {   public void run() {
            // refreshDisplayedTimerInfo();  called on regular updates of the timer screen, as well as on app relaunch
            // setTaskStatusOngoingWhenMinimumThresholdPassed();
            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
                    && timerInstance.getElapsedTime() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
                status.setStatus(ItemStatus.ONGOING); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> timedItem.setStartedOnDate(System.currentTimeMillis()
                        - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS));
            }
//            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
            refreshElapsedTime.actionPerformed(null);
            refreshTotalActualEffort.actionPerformed(null);
            Log.p("RefreshTimer");
        };

//        timerContainer.setLeadComponent(elapsedTimePicker); //ensure that the button behaves like a Picker
//        timerContainer.add(elapsedTimeButton, elapsedTimePicker);
//        elapsedTimePicker.setUIID("TimerTimer");
        elapsedTimeButton.setCommand(Command.create(null, null, (e) -> {
            Picker elapsedTimePicker = new Picker();
            elapsedTimePicker.setType(Display.PICKER_TYPE_TIME);
//            elapsedTimePicker.setHidden(true);
            elapsedTimePicker.setTime((int) timerInstance.getElapsedTime() / MyDate.MINUTE_IN_MILLISECONDS);
            elapsedTimePicker.addActionListener((ev) -> {
                timerInstance.setElapsedTime(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS);
                refreshTotalActualEffort.actionPerformed(null);
                refreshElapsedTime.actionPerformed(null);
                if (false) {
                    elapsedTimePicker.stopEditing(null); //probably the actionListener above is enough
                }
            });
            elapsedTimePicker.startEditingAsync();
        }));

        parseIdMap2.put("ElapsedTime", () -> {
            timedItem.setActualEffort(timerInstance.isTimerShowActualTotal()
                    ? timerInstance.getElapsedTime()
                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
        });

        final int BUZZER_DURATION = 300;
        UITimer timerTimer = new UITimer(updateTimerDisplay);
        UITimer buzzerTimer = new UITimer(() -> {
            Display.getInstance().vibrate(BUZZER_DURATION);
            Log.p("Buzz");
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

        SaveEditedValuesLocally previousValues = formPreviousValues != null ? formPreviousValues : new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());

        if (Display.getInstance().isScreenSaverDisableSupported()) {
            Display.getInstance().setScreenSaverEnabled(!MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
        }

        // ********************************* COMMANDS **************************
        //
        Command cmdStartNextTask = new Command("StartNextTask", null) { //stop and save current task and move to next (autostart if set)
            @Override
            public void actionPerformed(ActionEvent evt) {
                timerInstance.stopTimer(true);                //stop this timer, save item, //DON'T change status (is normally ONGOING)

                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                TimerStack.getInstance().startTimerOnNext();
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (TimerStack.getInstance().getCurrentTimerInstance() != null) {
//                    buildContentPane(contentPane, fullScreenTimer, previousValues); //rebuild
//                    contentPane.getParent().revalidate();
//                    contentPane.getComponentForm().animateLayout(300);
//                } else {
//                    removeTimerSmallContainer(contentPane);
//                }
//</editor-fold>
            }
        };

        /**
         * update/save current task (ask if update Remaining) and either move to
         * next task, and start the timer, or exit if no more tasks
         */
//        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
//        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconCheckboxOngoing) {
        Command cmdStopTimerAndGotoNextTaskOrExit = new Command("Next", Icons.iconTimerNextTask) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                timerInstance.stopTimer(true);                //stop this timer, save item, 
//                status.setStatus(ItemStatus.DONE, false);

                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                if (timerInstance.isAutoGotoNextTask()) {
//                    cmdStartNextTask.actionPerformed(null);
                    TimerStack.getInstance().startTimerOnNext();
                }
            }
        };

        Command cmdSaveAndExit = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
                timerInstance.stopTimer(false); //no need to save since about to be deleted

                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                TimerStack.getInstance().exitTimer();  //remove this timerInstance

//<editor-fold defaultstate="collapsed" desc="comment">
//                //either move back from full screen timer to previous screen, or close small timer window
//                Form form = contentPane.getComponentForm();
//                if (form instanceof ScreenTimer6) { //we may have launched
//                    assert fullScreenTimer; //TODO we should also be able to test on this simply
//                    showPreviousScreenOrDefault(((ScreenTimer6) form).previousForm, fullScreenTimer);
//                } else {
//                    Container parent = contentPane.getParent();
//                    if (parent != null) {
//                        parent.removeComponent(contentPane);
//                        parent.animateLayout(300);
//                    }
//                }
//</editor-fold>
            }
        };

        Command cmdSetCompletedAndGotoNextTaskOrExit = new Command("Completed", Icons.iconCheckboxDone) {
            @Override
            public void actionPerformed(ActionEvent evt) {

                timerInstance.stopTimer(true);                //stop this timer, save item, 
                status.setStatus(ItemStatus.DONE, false);

                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                if (timerInstance.isAutoGotoNextTask()) {
//                    cmdStartNextTask.actionPerformed(null);
                    TimerStack.getInstance().startTimerOnNext();
                }
            }
        };

        Command cmdSetTaskWaitingAndGotoNextTaskOrExit = new Command("Wait", Icons.iconCheckboxWaiting) {
            @Override
            public void actionPerformed(ActionEvent evt) {

                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.WAITING, false);

                MyForm.showDialogUpdateRemainingTime(remainingEffort);

                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                if (timerInstance.isAutoGotoNextTask()) {
//                    cmdStartNextTask.actionPerformed(null);
                    TimerStack.getInstance().startTimerOnNext();
                }
            }
        };

        Command cmdSetTaskCancelledAndGotoNextTaskOrExit = new Command("Cancel", Icons.iconCheckboxCancelled) {
            @Override
            public void actionPerformed(ActionEvent evt) {

                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.CANCELLED, false);

                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
                DAO.getInstance().saveInBackground(timedItem);
                previousValues.deleteFile();

                if (timerInstance.isAutoGotoNextTask()) {
//                    cmdStartNextTask.actionPerformed(null);
                    TimerStack.getInstance().startTimerOnNext();
                }
            }
        };

//Show next tasks
        //TODO optimization: only construct nextTask containers etc if there is one and it is shown
        //TODO show button to select auto-start Timer on task or not
        Item nextItem = TimerStack.getInstance().findNextTimedItem();
        if (nextItem != null) {
            gotoNextTaskButtonWithItemText = MyPrefs.timerShowNextTask.getBoolean()
                    ? new SpanButton("Next: \"" + nextItem.getText() + "\""
                            + (MyPrefs.timerShowRemainingForNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(nextItem.getRemainingEffort()) + "]") : ""))
                    : new SpanButton(""); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
            gotoNextTaskButtonWithItemText.setCommand(cmdStartNextTask);

            Command cmdGotoFullScreenTimer = null;
            if (!fullScreenTimer) {
//                cmdGotoFullScreenTimer = new Command("FullScreenTimer", Icons.iconEditSymbolLabelStyle) {
                cmdGotoFullScreenTimer = new Command("", Icons.iconEditSymbolLabelStyle) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        //save edited values //TODO!!!!
                        new ScreenTimer6((MyForm) contentPane.getComponentForm(), timerInstance).show();
                    }
                };
            }

            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                gotoNextTaskButtonWithItemText.setUIID("Label");
                gotoNextTaskButtonWithItemText.setTextUIID("Label");
            }
            gotoNextTaskButtonWithItemText.setHidden(!MyPrefs.timerShowNextTask.getBoolean());

            status.setStatusChangeHandler((oldStatus, newStatus) -> {/*//TODO call commands*/
                if (newStatus != oldStatus) {
                    switch (newStatus) {
                        case DONE:
                            cmdSetCompletedAndGotoNextTaskOrExit.actionPerformed(null);
                            break;
                        case WAITING:
                            cmdSetTaskWaitingAndGotoNextTaskOrExit.actionPerformed(null);
                            break;
                        case CANCELLED:
//                    processCurrentItemAndLaunchNextOrExit(null, false, true, false, false); //TODO!!! when cancelling a task, should we still store the time stored?
                            cmdSetTaskCancelledAndGotoNextTaskOrExit.actionPerformed(null);
                            break;
                        case ONGOING:
                        //do nothing - should already be set to Ongoing when starting timer
                        case CREATED:
                        //do nothing - user forces status back to empty checkbox
                    }
                }
            });

            contentPane.removeAll(); //clear before rebuilding

            Container cont = new Container(BoxLayout.y());
            cont.setScrollableY(true);
//            contentPane.add(BorderLayout.CENTER, cont);

            if (fullScreenTimer) {

//                comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
                comment.addActionListener((e) -> {
                    timedItem.setComment(comment.getText());
                    DAO.getInstance().saveInBackground(timedItem);
                });

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
                        buttonShowItemHierarchy.getParent().animateLayout(300);
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
//                MyForm.makeField(Item.PARSE_TEXT, description, () -> timedItem.getText(),
//                        (t) -> timedItem.setText((String) t), () -> description.getText(), (t) -> description.setText((String) t), previousValues, parseIdMap2);
                description.addActionListener((e) -> {
                    timedItem.setText(description.getText());
                    DAO.getInstance().saveInBackground(timedItem);
                });
                if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                }
            } else {
                description = new MyTextArea(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
                description.setColumns(100);
                description.setActAsLabel(true);
                description.setUIID("Label");
                description.setEditable(true); //true=editable (but will look like a label until clicked), false=not editable in small container
                description.setText(timedItem.getText());
            }

//            MyForm.makeField(Item.PARSE_STATUS, status, () -> timedItem.getStatus(), (t) -> timedItem.setStatus((ItemStatus) t),
//                    () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t), previousValues, parseIdMap2);
            status.addActionListener((e) -> {
                timedItem.setStatus(status.getStatus());
                DAO.getInstance().saveInBackground(timedItem);
            });
            editItemButton = new Button(MyReplayCommand.create("EditItem", "", Icons.iconEditSymbolLabelStyle, (e) -> {
//                MyForm.putEditedValues2(parseIdMap2, timedItem); //first update Item with any values changed in Timer
                ScreenItem2 screenItem = new ScreenItem2(timedItem, (MyForm) contentPane.getComponentForm(), () -> {
                    //TODO!!!!!! if item values like description or comment were edited in Timer, they must be shown when editing the item. Simply pass previousValues?!
                    DAO.getInstance().saveInBackground(timedItem);
                    description.setText(timedItem.getText());
                    status.setStatus(timedItem.getStatus());
                    comment.setText(timedItem.getComment());
                    effortEstimate.setTime((int) timedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
                    remainingEffort.setTime((int) timedItem.getRemainingEffort(false) / MyDate.MINUTE_IN_MILLISECONDS);
                    refreshTotalActualEffort.actionPerformed(null);
//                ScreenTimer6.this.revalidate();
                    ((MyForm) contentPane.getComponentForm()).revalidate();
                }, false, previousValues); //previousValues: pass locally edited value to ScreenItem
                screenItem.show();
            }
            ));

            if (fullScreenTimer) {
                cont.add(BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton));
            }

            ActionListener startTimers = (e) -> {
                timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, form); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
//                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, form);
                }
            };

//            elapsedTimeButton = new Button("TimerTimer");
            ActionListener stopTimers = (e) -> {
                timerTimer.cancel(); //stop timer as first thing
                if (buzzerTimer != null) { //buzzer is optional
                    buzzerTimer.cancel();
                }
            };

//            elapsedTimeButton = new Button("TimerTimer");
            //If go to nexttask
            timerStartStopButton = new Button();
            Command timerStartStopCmd = Command.create(null, null, (e) -> {
//            if (timer == null) {
                if (!timerInstance.isRunning()) {                    //start Timer
                    //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                    setTaskStatusOngoingWhenMinimumThresholdPassed(); //done when updating the display
                    timerInstance.startTimer();
                    elapsedTimeButton.setEnabled(false); //disable while running
                    timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
                    timerStartStopButton.repaint(); //this is enough to update the value on the screen

//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, contentPane.getComponentForm()); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, form); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
//                        buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
//                    }
                    startTimers.actionPerformed(null);
//    }
                } else {
                    timerInstance.stopTimer();
//                    timerTimer.cancel(); //stop timer as first thing
//                    buzzerTimer.cancel();
                    stopTimers.actionPerformed(null);
                    elapsedTimeButton.setEnabled(true); //enable the picker when timer is paused
                    timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
                    timerStartStopButton.repaint(); //this is enough to update the value on the screen
                }
            });
            timerStartStopButton.setCommand(timerStartStopCmd);

            if (timerInstance.isRunning()) {
//                timerStartStopCmd.actionPerformed(null); //start the UI timer
                startTimers.actionPerformed(null); //start the UI timer
            }

            timerStartStopButton.setIcon(timerInstance.isRunning() ? Icons.iconTimerPauseLabelStyle : Icons.iconTimerStartLabelStyle);

            if (fullScreenTimer) { //smallContainer

//            remainingEffort = new MyDurationPicker();
                remainingEffort.setShowZeroValueAsZeroDuration(true); //show "0:00"

//                MyForm.makeField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
//                        () -> timedItem.getRemainingEffort(false), (l) -> timedItem.setRemainingEffort((long) l),
//                        () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l), previousValues, parseIdMap2);
//                effortEstimate = new MyDurationPicker();
                effortEstimate.setShowZeroValueAsZeroDuration(true); //show "0:00"
//                MyForm.makeField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate, () -> timedItem.getEffortEstimate(), (l) -> timedItem.setEffortEstimate((long) l),
//                        () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l), previousValues, parseIdMap2);
                effortEstimate.addActionListener((e) -> {
                    timedItem.setEffortEstimate(effortEstimate.getDuration());
                    DAO.getInstance().saveInBackground(timedItem);
                });
                remainingEffort.addActionListener((e) -> {
                    timedItem.setRemainingEffort(remainingEffort.getDuration());
                    DAO.getInstance().saveInBackground(timedItem);
                });

//            totalActualEffort = new Label(); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");
                refreshTotalActualEffort.actionPerformed(null);

                //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set. 
                ActionListener estimateAndRemainingUpdater = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //update estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
                        if (remainingEffort.getTime() != 0 && timedItem.getEffortEstimate() == 0 && effortEstimate.getTime() == 0) { //if remaining is changed AND effortEstimate is not...
                            effortEstimate.setTime(remainingEffort.getTime());
                            effortEstimate.repaint();
                        }
                        //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
                        if (effortEstimate.getTime() != 0 && timedItem.getRemainingEffort(false) == 0 && remainingEffort.getTime() == 0) {
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
                            contentPane.getComponentForm().animateLayout(300);
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

                MyForm.makeField(Item.PARSE_COMMENT, comment, () -> timedItem.getComment(), (t) -> timedItem.setComment((String) t),
                        () -> comment.getText(), (t) -> comment.setText((String) t), previousValues, parseIdMap2);
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
//            } else if (currEntry.nextItem == null) {
                } else if (gotoNextTaskButtonWithItemText == null) {
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
//                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
                    cont.add(gotoNextTaskButtonWithItemText);
                }
            } else { //small timer container
                contentPane.setUIID("SmallTimerContainer");
                boolean interruptTask = false;
                Button nextTask = new Button(cmdStopTimerAndGotoNextTaskOrExit);
                nextTask.setText("");
                Button fullScreenTimerButton = new Button(cmdGotoFullScreenTimer);
                Container timerContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
                timerContainer.addAll(elapsedTimeButton, timerStartStopButton);

                contentPane.add(BorderLayout.centerCenterEastWest(
                        //                        /*Center*/interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description,
                        /*Center*/
                        //                        BorderLayout.west(status).add(BorderLayout.CENTER, interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description).add(BorderLayout.EAST, editItemButton),
                        BorderLayout.center(interruptTask
                                ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description)
                                //                                : description).add(BorderLayout.EAST, editItemButton),
                                : description),
                        /*East*/
                        BoxLayout.encloseXNoGrow(timerContainer, nextTask, fullScreenTimerButton),
                        /*West*/
                        status
                ));
            }

//        return contentPane;
        }
    }

}
