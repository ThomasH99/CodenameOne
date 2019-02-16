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
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.layouts.Layout;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.TableLayout;
//import com.codename1.ui.util.UITimer;
import java.util.HashMap;
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
    private List<TimerInstance> activeTimers; //is NOT saved since it is set by reading the list of saved TimerInstances
    Container smallContainer = null;
//    boolean timeEvenInvalidItem = false;
//    private List<TimerStackEntry> previouslyRunningTimers;
//    TimerStackEntry currEntry;

//    private static List<TimerInstance> INSTANCE;
    private static TimerStack INSTANCE=null;
    final static String TIMER_REPLAY = "StartTimer-";

    final static Object TIMER_LOCK = new Object(); //lock operations on Timer such as updating/saving instances or refreshing Timers from Server

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

    public String toString() {
        String s = "[";
        for (int i = activeTimers.size() - 1; i >= 0; i--) {
            s += activeTimers.get(i).toString() + "; ";
        }
        return s + "]";
    }

    TimerInstance getCurrentTimerInstanceN() {
        int size = activeTimers.size();
        if (size > 0) {
            TimerInstance t = activeTimers.get(size - 1); //return last timer
            return t; //return last timer
        }
        return null;
    }

    /**
    the preferred way to get whatever item is being timed (will also initialize the value if needed).
    On very first access (after starting the timer), several situations: item==null&list!=null, list==null&item!=null
    where item can be valid or invalid, project or not project. 
    @return 
     */
    public Item getTimedItemN() {
        TimerInstance timerInstance = getCurrentTimerInstanceN();
        if (timerInstance != null) {
            //find next item to run timer on, most likely either next in current list/project, or the one interrupted
            Item timedItem = timerInstance.getTimedItem();
            if (timedItem == null) {// || !isValidItemForTimer(timedItem)) { //isValid only relevant if used to initialize a new list
                goToNextTimedItem();
                timerInstance = getCurrentTimerInstanceN();
                if (timerInstance != null) {
                    timedItem = timerInstance.getTimedItem();
                }
            }
            return timedItem;
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="getTimedItemXXX()">
//    private Item getTimedItemXXX() {
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//        Item timedItem = timerInstance.getTimedItemN();
//        if (timedItem == null) {
////            timedItem = timerInstance.updateToNextTimerItem(false, false); //get next timed item without making any changes
//            timedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item without making any changes
//
//            //if none found try rest of timerstack (if any)
//            int index = activeTimers.size() - 2; //init to second last timerInstance
//            while (timedItem == null && index >= 0 && index < TimerStack.getInstance().activeTimers.size()) { //as long as there are valid timerInstances not tried yet
//                //remove the current timer instance (which has run out of tasks, or could be an interrupt)
//                timerInstance = activeTimers.get(index); //pop last timerInstance since it has no more tasks
////                timedItem = timerInstance.updateToNextTimerItem(false, false); //also update project and save
//                if (false) {
//                    timedItem = timerInstance.updateToNextTimerItem(true, true); //also update project and save //NO, do not move to next timer on popped timer (e.g. it may be popped because was interrupted)
//                }
//                timerInstance.getTimedItemN();
//                index--;
//            }
//        }
//        return timedItem;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="findNextTimedItemOLD()">
//    private Item findNextTimedItemOLD() {
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//        Item nextTimedItem;
//        nextTimedItem = timerInstance.updateToNextTimerItem(false, false); //get next timed item without making any changes
//        //if none found try rest of timerstack (if any)
//        int index = activeTimers.size() - 2; //init to second last timerInstance
//        while (nextTimedItem == null && index >= 0 && index < TimerStack.getInstance().activeTimers.size()) { //as long as there are valid timerInstances not tried yet
//            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
//            timerInstance = activeTimers.get(index); //pop last timerInstance since it has no more tasks
//            nextTimedItem = timerInstance.updateToNextTimerItem(false, false); //also update project and save
//            index--;
//        }
//        return nextTimedItem;
//    }
//</editor-fold>
    /**
    find next suitable for timer and (re-)start the timer if it was running when interrupted
    returns true if timer was started on another item. 
     */
//    private boolean startTimerOnNext() {
//    private boolean goToNextTimedItem() {
    private Item nextTimedItemImpl(boolean update) {
        TimerInstance timerInstance = getCurrentTimerInstanceN();

        if (timerInstance == null) {
            return null;
        }
        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
        Item nextTimedItem = timerInstance.updateToNextTimerItem(update, update); //get next timed item if any, also update project and save
        //if none found try rest of timerstack (if any)
//        int currentTimerStackIndex = activeTimers.size() - 2; //start with second-last (before current)
        int currentTimerStackIndex = activeTimers.size() - 1; //start with last timer
//        while (nextTimedItem == null && activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
//        while (nextTimedItem == null && currentTimerStackIndex >= 0) { //if no more tasks in current timer, try earlier ones
        while (nextTimedItem == null && currentTimerStackIndex > 0) { //if no more tasks in current timer, try earlier ones
            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
//            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            timerInstance.deleteInstance();
//            if (!activeTimers.isEmpty()) { //try with (new)  timerInstance (if any)
//                timerInstance = getCurrentTimerInstanceN();
            currentTimerStackIndex--;
            timerInstance = activeTimers.get(currentTimerStackIndex);
//                if (timerInstance != null) {
//            nextTimedItem = timerInstance.updateToNextTimerItem(update, update); //also update project and save
            nextTimedItem = timerInstance.getTimedItem(); //DO NOT update since this is a previous Timer where the current item was interr8pted, so simply continue with that
//                }
//            if (nextTimedItem == null) {
//                currentTimerStackIndex--;
//            }
        }
        //if update,  discard/delete all skipped (no longer having tasks to time) timers:
        if (update) {
            while (activeTimers.size() - 1 > currentTimerStackIndex || (nextTimedItem == null && activeTimers.size() > 0)) {
                timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
                timerInstance.deleteInstance();
            }
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//if a next item was found
//        if (false) {
//            if (nextTimedItem != null) {
//                if (timerInstance.isInterruptedWhileRunning() || timerInstance.isAutostart()) {
//                    timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
//                    if (timerInstance.isInterruptedWhileRunning()) {
//                        timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
//                    }
//                }
//                refreshOrShowTimerUI();
//            } else { //no more tasks, show popup (or Toastbar?)
//                if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
//                    showNoMoreTasksNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
//                } //else: inbterrupt, so don't display any messages
//            }
//        }
//</editor-fold>
        return nextTimedItem;
    }

    private void goToNextTimedItem() {
        nextTimedItemImpl(true);
    }

//<editor-fold defaultstate="collapsed" desc="isAutostartXXX">
//    public boolean isAutostartXXX() {
////        Boolean autostart = getBoolean(PARSE_AUTOSTART_TIMER);
////        return (autostart == null) ? false : autostart;
//        return MyPrefs.timerAutomaticallyStartTimer.getBoolean();
//    }
//</editor-fold>
    /**
    go to next task (if any), start Timer if needed, refresh UI. Called in user commands
    @return true if a next task was found
     */
    private boolean moveToNextTask() {
        goToNextTimedItem();
        Item nextTimedItem = getTimedItemN();
        TimerInstance timerInstance = getCurrentTimerInstanceN(); //NB! Call *after* getTimedItemN() since getTimedItemN() may move to an pushed/earlier/previous timerInstance!
        if (nextTimedItem != null) {
            if (timerInstance.isInterruptedWhileRunning() || MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
                if (timerInstance.isInterruptedWhileRunning()) {
                    timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
                }
            }
            refreshOrShowTimerUI();
            return true;
        } else { //no more tasks, show popup (or Toastbar?)
            if (timerInstance != null) {
                if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
                    showNoTasksToWorkOnNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
                } //else: inbterrupt, so don't display any messages
            }
            refreshOrShowTimerUI();
            return false;
        }
    }

    private Item getTheNextComingTimedItem() {
        return nextTimedItemImpl(false);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void goToNextTimedItemXXX() {
////        TimerInstance timerInstance = activeTimers.get(activeTimers.size() - 1);
//        TimerInstance timerInstance = getCurrentTimerInstanceN();
//
//        if (timerInstance != null) {
//            return;
//        }
//        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//        Item nextTimedItem;
////        TimerInstance lastValidTimerInstance = timerInstance;
//        nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
//        //if none found try rest of timerstack (if any)
////            while (nextTimedItem == null && TimerStack.getInstance().activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
//        while (nextTimedItem == null && activeTimers.size() > 0) { //if no more tasks in current timer, try earlier ones
////            getInstance().removelasttimer();
//            //remove the current timer instance (which has run out of tasks, or could be an interrupt)
//            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            timerInstance.deleteInstance();
////            if (timerInstance != null) {
////                lastValidTimerInstance = timerInstance;
////            }
//            if (!activeTimers.isEmpty()) { //try with (new)  timerInstance (if any)
////                    timerInstance = activeTimers.get(activeTimers.size() - 1);
//                timerInstance = getCurrentTimerInstanceN();
//                if (timerInstance != null) {
//                    nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //also update project and save
////                    if (nextTimedItem != null && timerInstance.isInterruptedWhileRunning()) { //DONE below
////                        timerInstance.startTimer(true); //if previous timer was running, then restart it and save state
////                    }
//                }
//            }
//        }
//
//        //if a next item was found
//        if (nextTimedItem != null) {
//            if (timerInstance.isInterruptedWhileRunning() || timerInstance.isAutostart()) {
//                timerInstance.startTimer(true); //timerInstance.isInterruptedWhileRunning());
//                if (timerInstance.isInterruptedWhileRunning()) {
//                    timerInstance.setWasRunningWhenInterrupted(false, true); //un-pause this timer for next time (needed?)
//                }
//            }
//            refreshOrShowTimerUI();
////                return true;
//        } else { //no more tasks, show popup (or Toastbar?)
////            if (lastValidTimerInstance.getTimedProject() != null || lastValidTimerInstance.getItemList() != null) {
////                showNoMoreTasksNotificationWhenRelevant(lastValidTimerInstance.getTimedProject(), lastValidTimerInstance.getItemList());
//            if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
//                showNoMoreTasksNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
//                //remove timer UI
//                if (false) {
//                    removeTimerSmallContainer();
//                }
//            } //else: inbterrupt, so don't display any messages
////                return false;
//        }
////        }
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
    stop and delete all ongoing timers, quitting the timer completely (on this and consequently on other devices)
     */
//    private void deleteAllActiveTimersOnExit() {
//    private void exitTimerXXX() {
//        while (TimerStack.getInstance().activeTimers.size() > 0) {
////            getInstance().removelasttimer();
//TimerInstance timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//
//if (timerInstance.getElapsedTime() > 0) {
//    Item timedItem = timerInstance.getTimedItemN(); //get the item that is/was timed
////                timedItem.setActualEffort(timerInstance.isTimerShowActualTotal() //update actual
////                        ? timerInstance.getElapsedTime()
////                        : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
//timedItem.setActualEffort(timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
//DAO.getInstance().saveInBackground(timedItem);
//}
//timerInstance.deleteInstance();
//        }
////        removeTimerSmallContainer();
//refreshOrShowTimerUI();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
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
    private static void showNoTasksToWorkOnNotificationWhenRelevant(Item project, ItemList itemList) {
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
        synchronized (TIMER_LOCK) {
            INSTANCE = new TimerStack(DAO.getInstance().getTimerInstanceList());
        }
        List<TimerInstance> timers = DAO.getInstance().getTimerInstanceList();
        for (TimerInstance timerInstance : timers) {
            //if timer is stopped on server, but running here, then stop it here and if another is running on server start that one
            //if timer is running on server, but not here
        }
        INSTANCE = new TimerStack(timers);
//            return true;
//        }
//        return false;
    }

    //**************** PUBLIC INTERFACE ****************
    private void addNewTimerInstance(TimerInstance newTimerInstance) {
        addNewTimerInstance(newTimerInstance, false);
    }

    private void addNewTimerInstance(TimerInstance newTimerInstance, boolean save) {
        activeTimers.add(newTimerInstance); //add last as definitely the most recent instance
//        DAO.getInstance().saveInBackground(newTimerInstance);
        if (save) {
            DAO.getInstance().saveInBackground(newTimerInstance);
        }
//        DAO.getInstance().save(newTimerInstance,false);
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
     * stops the timer if it is currently running for this item. Tests if the
     * current item is being set to DONE/CANCELLED from within the timer to
     * update infinite loops if the timer is setting the item to Done Updates
     * the item actuals with the timer value. Does not start the timer on a next
     * item (like normally done when within the timer. Does everything normally
     * done when exiting the timer (eg pops the timer stack).
     *
     * DOES currently NOT store any other values edited in the timer What
     * happens next time the timer is called??
    return true if Timer was running for the task. 
     */
    public boolean stopTimerIfRunningOnThisItemOnStartTimerOnNext(Item item) { //boolean continueWithNextItem // 
        boolean wasTimerRunningForTheTask = false;
        if (item == null || getCurrentTimerInstanceN() == null) {
//            return false;
            return wasTimerRunningForTheTask;
        }

//        if (item.equals(getCurrentTimerInstanceN().getTimedItemN())) { //if it's currently timed task...
        if (item.equals(getTimedItemN())) { //if it's currently timed task...
            wasTimerRunningForTheTask = true;
            moveToNextTask(); //simply move to next one
        } else { //see if interrupted timers were timing item
            int timerIndex = activeTimers.size() - 1;
            timerIndex--; //skip current one since we dealt with it above
            while (timerIndex >= 0) { //ccheck starting with most recent interrupted timer
                TimerInstance timerInstance = activeTimers.get(timerIndex);

//                if (item.equals(timerInstance.getTimedItemN())) { //if timer is (or was if down in the stack) running on this specific task
                if (item.equals(getTimedItemN())) { //if timer is (or was if down in the stack) running on this specific task
                    wasTimerRunningForTheTask = true;
                    boolean timerWasRunning = timerInstance.isInterruptedWhileRunning();
                    if (true || timerWasRunning) {
                        //update item with elapsed time 
                        item.setActualEffort(item.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime(),false);
                    }
//                item.setActualEffort(timerInstance.isTimerShowActualTotal() ? timerInstance.getElapsedTime()
//                        : item.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime());
                    //don't save item, it'll be done from whereever this method was called

                    //start next or exit timer if no more available
//                return goToNextTimedItem(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
//if no more items to time on this timer instance, then remove it (scenario: last task T in project/list is interrupted, then T is marked Done manually in a screen (while interrupt is timed), when timer f interrupt is stopped, 
//                goToNextTimedItem(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
                    moveToNextTask(); //TODO!!!! this should refreshTimersFromParseServer the UI - how?
//                refreshOrShowTimerUI();
                }
            }
//        return false;
        }
        return wasTimerRunningForTheTask;
    }

    private boolean isItemOrListAlreadyBeingTimed(Item timedItemOrProject, ItemList itemList) {
        boolean itemAlreadyTimed = false;
        boolean itemListAlreadyTimed = false;

        for (TimerInstance timerInstance : activeTimers) {
//if re-launcing timer on already timed Item
            if (timedItemOrProject != null && timedItemOrProject.equals(timerInstance.getTimedItem())) {
                itemAlreadyTimed = true;
            }
//or re-launcing timer on already timed ItemList
            if ((itemList != null && itemList.equals(timerInstance.getItemList()))) {
                itemListAlreadyTimed = true;
            }
        }
        if (itemAlreadyTimed || itemListAlreadyTimed) {
            String s = "";
            if (itemListAlreadyTimed) { //list primes over task
                s = ItemList.ITEM_LIST + " \"" + itemList.getText() + "\"";
            } else if (itemAlreadyTimed) {
                s = Item.TASK + " \"" + timedItemOrProject.getText() + "\"";
            }
//                MyForm.showToastBar("Timer already running for \"" + timedItem.getText() + "\""
//                        + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
            if (Display.getInstance().getCurrent() != null) { //don't show toastbar if no screen => white screen??!
                MyForm.showToastBar("Timer already running for " + s);
            }
        }
        return itemAlreadyTimed || itemListAlreadyTimed;
    }

    /**
    true if interrupt is allowed, depends on settings
    @param newTimedItem
    @param interruptOrInstantTask
    @return 
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

    private void pauseCurrentTimerIfNeeded(Item newTimedItemOrProject, boolean interruptOrInstantTask) {
        Item alreadyTimedItem = getTimedItemN();
        TimerInstance alreadyRunningTimerInstance = getCurrentTimerInstanceN();

        if (alreadyRunningTimerInstance != null) {
            if (isInterruptAllowed(newTimedItemOrProject, interruptOrInstantTask)) {
                if (alreadyRunningTimerInstance.isRunning()) { //pause current timer if running
                    boolean aPreviousTaskWasInterrupted = true;
                    alreadyRunningTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
                    if (interruptOrInstantTask) {
                        newTimedItemOrProject.setTaskInterrupted(alreadyTimedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
                        DAO.getInstance().saveInBackground(newTimedItemOrProject);
                    }
//                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
                    MyForm.showToastBar("Timer paused for \"" + alreadyTimedItem.getText()
                            //                            + "\"\, will continue after this "
                            + "\" while timing this "
                            + (interruptOrInstantTask && aPreviousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
                }
            } else {
                if (alreadyTimedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
                    showTimerCannotBeStarted(alreadyTimedItem.isInteruptOrInstantTask(), alreadyTimedItem);
                    return;
                }
            } //not a valid 
        }
    }

    private void startTimer(Item timedItemOrProject, ItemList itemList, MyForm previousForm,
            boolean interruptOrInstantTask, boolean startedOnIndividualItemOrProject) {

        if (ReplayLog.getInstance().isReplayInProgress()) {
            refreshOrShowTimerUI(previousForm);
            return;
        }

        //stop and push previously timed item+context
        //TODO!!! should timerAutomaticallyGotoNextTask and timerAutomaticallyStartTimer be properties at TimerStack level instead if TimerInstance?? Depends also on whether one list/project may interrupt another
//        this.timeEvenInvalidItem = timeEvenInvalidItem;
        ASSERT.that(!interruptOrInstantTask || itemList == null, "cannot have an interrupt task with a list");

        if (isItemOrListAlreadyBeingTimed(timedItemOrProject, itemList)) { //don't allow to start timer again on an element which is already timer (and could be interrupted by later timers)
            return;
        }

        boolean timeEvenInvalidItemsOrProjects = startedOnIndividualItemOrProject
                && (timedItemOrProject != null && !isValidItemForTimer(timedItemOrProject))
                && MyPrefs.timerCanBeSwipeStartedEvenOnInvalidItem.getBoolean();
        TimerInstance newTimerInstance = new TimerInstance(timedItemOrProject, itemList, timeEvenInvalidItemsOrProjects);
        Item newTimedItem = newTimerInstance.getTimedItem();
        if (newTimedItem != null) { //if there's an item to time in the new list/item/project
//<editor-fold defaultstate="collapsed" desc="comment">
//            Item timedItem = getTimedItemN();
//            TimerInstance previousTimerInstance = getCurrentTimerInstanceN();
//
//            if (previousTimerInstance != null) {
//                if (isItemOrListAlreadyBeingTimed(timedItemOrProject, itemList)) { //don't allow to start timer again on an element which is already timer (and could be interrupted by later timers)
//                    return;
//                } else if (isInterruptAllowed(timedItemOrProject, interruptOrInstantTask)) {
//                    if (previousTimerInstance.isRunning()) { //pause current timer if running
//                        boolean previousTaskWasInterrupted = true;
//                        previousTimerInstance.setWasRunningWhenInterrupted(true, true); //pause and save!
//                        if (interruptOrInstantTask) {
//                            timedItemOrProject.setTaskInterrupted(timedItem); //only set if timer was actually running, otherwise does not qualify as an interrupt but only as an InstantTask
//                            DAO.getInstance().saveInBackground(timedItemOrProject);
//                        }
////                    MyForm.showToastBar("Already running Timer paused for \"" + previousTimerInstance.getTimedItemN().getText() + "\", will continue after this "
//                        MyForm.showToastBar("Timer paused for \"" + timedItem.getText() + "\", will continue after this "
//                                + (previousTaskWasInterrupted ? "interrupt" : "instant task"), 0);
//                    }
//                } else {
//                    if (timedItem.isInteruptOrInstantTask() && MyPrefs.timerInterruptTaskCanInterruptAlreadyRunningInterruptTask.getBoolean()) {
//                        showTimerCannotBeStarted(timedItem.isInteruptOrInstantTask(), timedItem);
//                        return;
//                    }
//                } //not a valid
//            }
//</editor-fold>
//            pauseCurrentTimerIfNeeded(timedItemOrProject, interruptOrInstantTask);
            pauseCurrentTimerIfNeeded(newTimedItem, interruptOrInstantTask);

//            addNewTimerInstance(newTimerInstance,true); //also saves newTimerInstance //NO, no need to save since startTimer will save
//            assert !interruptOrInstantTask || timedItemOrProject.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task
            assert !interruptOrInstantTask || newTimedItem.isInteruptOrInstantTask(); //timedItem.setInteruptOrInstantTask(true); //in any case (whether interrupting another task or not), mark as interrupt OR instant task

            if (MyPrefs.timerAutomaticallyStartTimer.getBoolean()) {
                newTimerInstance.startTimer(true); //saved below in addNewTimerInstance
            }
            addNewTimerInstance(newTimerInstance, true); //also saves newTimerInstance //NO, no need to save since startTimer will save
            refreshOrShowTimerUI(previousForm);
        } else { //nothing to time
            //no save of new TimerInstance if not tasks to time
            showNoTasksToWorkOnNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getItemList());
        }
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
    public void startTimerOnItem(Item timedItem, MyForm previousForm) {
        startTimer(timedItem, null, previousForm, false, true);
    }

    public void startTimerOnItemList(ItemList itemList, MyForm previousForm) {
        startTimer(null, itemList, previousForm, false, false);
        //TODO set autostart!
    }

    public void startInterruptOrInstantTask(Item interruptOrInstantTask, MyForm previousForm) {
        ASSERT.that(interruptOrInstantTask.isInteruptOrInstantTask());
        startTimer(interruptOrInstantTask, null, previousForm, true, false);
        //TODO set autostart!
    }

    /**
    used to check if a found task (or project!) is valid for the timer (eg if the list may contain Done/Cancelled or Waiting tasks that should be skipped).
    Also used in TimerInstance. For a project to be valid, there has to at least one valid subtask: if project is ongoing: OK. Waiting: all subasks are Waiting. 
    
    @param item
    @return 
     */
//    static public boolean isValidItemForTimer(Item item) {
//        return isValidItemForTimer(item, false);
//    }
//    static public boolean isValidItemForTimer(Item item, boolean timeEvenInvalidItem) {
    static public boolean isValidItemForTimer(Item item) {
//        return !item.isDone() || MyPrefs.timerIncludeWaitingTasks.getBoolean() || MyPrefs.timerIncludeDoneTasks.getBoolean();
        return (item != null
                //                && (TimerStack.getInstance().timeEvenInvalidItem
                && ((!item.isDone() && !item.isWaiting()) //valid if not done and not waiting (so this expr is true for all other status values!)
                //TODO!!!! in Timer: check waiting date when skipping, or not, waiting tasks
                || (item.isWaiting() && MyPrefs.timerIncludeWaitingTasks.getBoolean()) ////or if waiting, but settings allow to time waiting tasks
                || (item.isDone() && MyPrefs.timerIncludeDoneTasks.getBoolean()))); //or if done, but settings allow to time done tasks
    }

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
    /**
    
    @param contentPane
    @param fullScreenTimer 
     */
//    public void startTimerOnNextOrExitIfNone(TimerInstance timerInstanceXXX, Container contentPane) {
//    public void startTimerOnNextOrExitIfNone(Container contentPane, boolean fullScreenTimer) {
    private void resetTimerSmallContainer() {
        setSmallContainer(null);
    }

    private void setSmallContainer(Container smallContainer
    ) {
        this.smallContainer = smallContainer;
    }
//    private static Container getContentPaneSouth() {
//        return getContentPaneSouth(Display.getInstance().getCurrent());
//    }

    private static Container getContentPaneSouth(Form form) {
//        Form form = Display.getInstance().getCurrent();
        if (form != null) {
            Container formContentPane = form.getContentPane();
            if (!(form instanceof ScreenTimer6)) {
                Layout contentPaneLayout = formContentPane.getLayout();
                if (contentPaneLayout instanceof BorderLayout) {
                    Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
                    if (southComponent instanceof Container) {
                        return (Container) southComponent;
                    }
                }
            }
        }
        return null;
    }

    /**
    return the container into which the smallTimer should be interted. Return null if no smallTimer should be shown. 
    Override to disable smallTimer or place it somewhere else. 
    @return 
     */
    static protected Container getContainerForSmallTimer() {
        return getContainerForSmallTimer(Display.getInstance().getCurrent());
    }

    static protected Container getContainerForSmallTimer(Form form) {
        Container timerContainer = null;
//        Form form = this;
//        if (form instanceof ScreenListOfItemLists || form instanceof ScreenListOfItems
//                || form instanceof ScreenStatistics
//                || form instanceof ScreenListOfWorkSlots || form instanceof ScreenListOfCategories) {
        if (form instanceof MyForm && !(form instanceof ScreenCategoryPicker
                || form instanceof ScreenListOfAlarms
                || form instanceof ScreenLogin || form instanceof ScreenObjectPicker
                || form instanceof ScreenRepair || form instanceof ScreenTimer6)) {
//        }else {
            Container formContentPane = form.getContentPane();
            Layout contentPaneLayout = formContentPane.getLayout();
            if (contentPaneLayout instanceof BorderLayout) {
//                timerContainer = getContentPaneSouth(form);
                Component southComponent = ((BorderLayout) contentPaneLayout).getSouth();
                if (southComponent instanceof Container) {
                    timerContainer = (Container) southComponent;
                } else if (southComponent == null) {
                    Container newCont = new Container(BoxLayout.y());
                    formContentPane.add(BorderLayout.SOUTH, newCont);
                    timerContainer = newCont;
                }
            }
        }
        return timerContainer;
    }

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
    /**
    called when using Back from full screen timer UI or when starting any screen that will show the small timer ui. after being added the small timer
    UI will update itself via the UITimer callbacks
    @param form add to the SOUTH container of this form, if form is null then add to Display.get
     */
    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form) {
//        return addSmallTimerWindowIfTimerIsRunning(form, getInstance().getSmallContainer());
//        return addSmallTimerWindowIfTimerIsRunning(form, buildContentPaneSmall(form)); //make a new container each time
        return addSmallTimerWindowIfTimerIsRunning(form, null); //make a new container each time

    }

    public static boolean addSmallTimerWindowIfTimerIsRunning(Form form, Container timerContainer) {
//        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//        if (timerInstance != null) {
////            Container timerForContainer = MyForm.getContainerForSmallTimer();
//            Container timerForContainer = MyForm.getContainerForSmallTimer(form);
//            if (timerForContainer != null) {
        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
        if (timerInstance != null) {
            Container timerForContainer = getContainerForSmallTimer(form);
            if (timerForContainer != null) { //if no container, do nothing (avoid e.g. the case where ScreenLogin tries to load timers before login completed)
                if (timerContainer == null) {
                    timerContainer = buildContentPaneSmall(form);
                }
                if (timerContainer != null) { //buildContentPaneSmall returns null if no timer active
//            Container timerForContainer = MyForm.getContainerForSmallTimer();
//                if (timerContainer != null && timerContainer.getParent() != null) {
                    if (timerContainer.getParent() != null) {
                        timerContainer.getParent().removeComponent(timerContainer); //remove from previous parent before adding to new
                    }
                    timerForContainer.add(timerContainer);
                    TimerStack.getInstance().smallContainer = timerContainer;
//                if (form.get)
                    if (false) {
                        form.animateLayout(300); //this call may be creating index out of bound in toolbar if called before the form is shown
                    }
                    return true;
                }
            }
        }
        return false;
    }

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
    private void removeTimerSmallContainer() {
//<editor-fold defaultstate="collapsed" desc="comment">
//         removeTimerSmallContainer(smallContainer);
//remove small timer UI
//        Container smallContainer = getSmallContainer();
//        if (smallContainer != null) {
//            Form form = smallContainer.getComponentForm();
//            Container parent = smallContainer.getParent();
//            if (parent != null) {
//                parent.removeComponent(smallContainer); //remove small timer window from its parent
//            }
//            if (form != null) {
//                form.animateLayout(300); //animate form to remove timerPane
//            } else if (parent != null) {
//                parent.animateLayout(300); //animate form to remove timerPane
//            }
//            if (false) {
//                setSmallContainer(null);
//            }
//        }
//</editor-fold>
        if (smallContainer != null && smallContainer.getParent() != null) {
            smallContainer.getParent().removeComponent(smallContainer);
            smallContainer = null;
        }
    }

    /**
    update the UI based on current timer (if any). Either add (or remove!) a small timer to current screen or launch the full screen timer UI
     */
    void refreshOrShowTimerUI() {
        refreshOrShowTimerUI(null);
    }

    void refreshOrShowTimerUI(MyForm previousForm) {
        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
        Form currentForm = Display.getInstance().getCurrent();
        if (currentForm == null) {
            ASSERT.that(ReplayLog.getInstance().isReplayInProgress(), "should only show new form if replay is ongoing");
            ASSERT.that(previousForm != null, "should only have currentForm==null if previousForm is defined/a valid form");
            new ScreenTimer6(previousForm, timerInstance).show();
            return;
        } else if (!(currentForm instanceof MyForm)) {
            return;
        }

        MyForm myCurrentForm = (MyForm) currentForm;
        if (myCurrentForm instanceof ScreenTimer6) {
            //full screen timer currently shown, so removeFromCache it
            myCurrentForm.refreshAfterEdit();
        } else { //other form than ScreenTimer6
            myCurrentForm.setKeepPos(new KeepInSameScreenPosition());
            removeTimerSmallContainer();
            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
                Container small = buildContentPaneSmall(myCurrentForm); //refresh the small container
                addSmallTimerWindowIfTimerIsRunning(myCurrentForm, small);
                myCurrentForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
            } else { //show full screen timer
                new ScreenTimer6(myCurrentForm, timerInstance).show();
            }
        }
    }

    void refreshOrShowTimerUIXXX() {
        Form form = Display.getInstance().getCurrent();
        boolean isReplayInProgress = ReplayLog.getInstance().isReplayInProgress();
        TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
        if (isReplayInProgress) {
            if (form != null && form instanceof MyForm && timerInstance != null) {
                new ScreenTimer6((MyForm) form, timerInstance).show();
            }
//        } else if ((form == null || !(form instanceof MyForm)) && !isReplayInProgress) {
        } else if ((form == null || !(form instanceof MyForm))) {
            return;
        }
        MyForm myForm = (MyForm) form;
        if (myForm instanceof ScreenTimer6) {
            //full screen timer currently shown, so removeFromCache it
//            ((ScreenTimer6) form).refreshAfterEdit();
            myForm.refreshAfterEdit();
        } else { //other form than ScreenTimer6
//            TimerInstance timerInstance = getInstance().getCurrentTimerInstanceN();
//            if (timerInstance == null) {
//                //no timer to show, so remove any UI still shown (only relevant for smallTimer since timer screen will remove itself)
//                removeTimerSmallContainer();
//                return;
//            }
            removeTimerSmallContainer();
//            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && (isSmallContainerCurrentlyShown() || MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean())) {
            if (MyPrefs.timerEnableShowingSmallTimerWindow.getBoolean() && MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean()) {
//                Container small = new Container();
//                buildContentPaneSmall(form, small); //refresh the small container
                Container small = buildContentPaneSmall(myForm); //refresh the small container
                if (addSmallTimerWindowIfTimerIsRunning(myForm, small)) {
                    if (false) {
                        setSmallContainer(small); //if successfully added, then save it
                    }
                }
                myForm.refreshAfterEdit(); //always removeFromCache, e.g. to update for Done tasks
            } else { //show full screen timer
//                Form form = Display.getInstance().getCurrent();
//                if (form instanceof MyForm || isReplayInProgress) {
//                if (form instanceof MyForm) {
                if (false) {
                    resetTimerSmallContainer();
                }
//                    new ScreenTimer6((MyForm) form, timerInstance).show();
                new ScreenTimer6(myForm, timerInstance).show();
//                }
            }
        }
    }

    //**************** BUILD THE UI ****************
//    protected static Container buildContentPaneFullScreen(Form form, Container contentPane, SaveEditedValuesLocally formPreviousValues) {
    protected static Container buildContentPaneFullScreen(Form form, SaveEditedValuesLocally formPreviousValues) {
//       return buildContentPane(form, contentPane, true, formPreviousValues);
        return buildContentPaneN(form, true, formPreviousValues);
    }

//    protected static Container buildContentPaneSmall(Form form, Container contentPane) {
    protected static Container buildContentPaneSmall(Form form) {
//        return buildContentPane(form, contentPane, false, null);
        return buildContentPaneN(form, false, null);
    }

    /**
     * build the user interface of the Timer
    
    @param form
    @param fullScreenTimer
    @param formPreviousValues
    @return null if no active timer
     */
//    private static Container buildContentPane(Form form, Container contentPane, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {
    private static Container buildContentPaneN(Form form, boolean fullScreenTimer, SaveEditedValuesLocally formPreviousValues) {

        ASSERT.that(form != null, "form cannot be null since it's needed for the UITimer");
//        contentPane.removeAll();
//        Container contentPane = fullScreenTimer?new Container(BoxLayout.y()):new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
//        contentPane.setLayout(fullScreenTimer ? BoxLayout.y() : BorderLayout.center());
//        contentPane.setLayout(BoxLayout.y());

//        assert currEntry != null : "entry must always be defined";
        TimerInstance timerInstance = TimerStack.getInstance().getCurrentTimerInstanceN();
        if (timerInstance == null) {
            return null;
        }

//        Item timedItem = timerInstance.getTimedItemN(); //currEntry.timedItem;
        Item timedItem = TimerStack.getInstance().getTimedItemN(); //currEntry.timedItem;

        if (timedItem == null) {
            return null;
        }

        ItemList itemList = timerInstance.getItemList();

        Button elapsedTimeButton = new Button("", "TimerTimer" + (fullScreenTimer ? "" : "Small"));
        MyTextField description;

        MyCheckBox status = new MyCheckBox(timedItem.getStatus());
//    private Button status;
        MyTextArea comment = new MyTextArea(Item.COMMENT, 20, 2, 4, MyPrefs.commentMaxSizeInChars.getInt(), TextArea.ANY);
//    private Container commentCont;
        MyDurationPicker remainingEffort = new MyDurationPicker();
        MyDurationPicker effortEstimate = new MyDurationPicker();
        Label totalActualEffort = new Label();
        Button editItemButton;
        Picker hiddenElapsedTimePicker = new MyDurationPicker();
        hiddenElapsedTimePicker.setHidden(true); //hiddenElapsedTimePicker must be added to a Form, but we don't want to show it, only activate it via a longpress on the timer button
        MyButtonLongPress timerStartStopButton = new MyButtonLongPress() {
            int maxWidthSoFar = 0;

            @Override
            public Dimension getPreferredSize() {
                Dimension newDimension = super.getPreferredSize();
                maxWidthSoFar = Math.max(maxWidthSoFar, newDimension.getWidth());
                newDimension.setWidth(maxWidthSoFar);
                return newDimension;
            }
        };

//        };
        //TODO!!!!! longpress should activate the duration picker 
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            timerStartStopButton.setCommand(Command.create(null, null, (e) -> {
//                Picker elapsedTimePicker = new Picker();
//                elapsedTimePicker.setHidden(true);
////            elapsedTimeButton.getParent().add(elapsedTimePicker); //must belong to a form
//                timerStartStopButton.getParent().add(elapsedTimePicker); //must belong to a form
//                elapsedTimePicker.setType(Display.PICKER_TYPE_TIME);
////            elapsedTimePicker.setHidden(true);
//                elapsedTimePicker.setTime((int) timerInstance.getElapsedTimeToDisplay() / MyDate.MINUTE_IN_MILLISECONDS);
//                elapsedTimePicker.addActionListener((ev) -> {
//                    timerInstance.setElapsedTime(elapsedTimePicker.getTime() * MyDate.MINUTE_IN_MILLISECONDS);
//                    refreshTotalActualEffort.actionPerformed(null);
//                    refreshElapsedTime.actionPerformed(null);
//                    if (false) {
//                        elapsedTimePicker.stopEditing(null); //probably the actionListener above is enough
//                    }
//                    elapsedTimePicker.getParent().removeComponent(elapsedTimePicker); //remove from form again
//
//                });
//                elapsedTimePicker.startEditingAsync();
//            }));
//        }
//</editor-fold>
        SpanButton gotoNextTaskButtonWithItemText = null;
//        Picker elapsedTimePicker;
//        UITimer timer;
//        UITimer buzzerTimer;
        Map<Object, MyForm.UpdateField> parseIdMap2 = new HashMap<Object, MyForm.UpdateField>(); //create a new hashmap for this item

        ActionListener refreshTotalActualEffort = (e) -> {
//            long totalEffort = MyPrefs.timerShowTotalActualInTimer.getBoolean()
//                    ? timerInstance.getElapsedTime()
//                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself();
            long totalEffort = timerInstance.getElapsedTotalTime();
            totalActualEffort.setText(MyDate.formatTimeDuration(totalEffort, true)); //false=don't show seconds in Total
//            totalActualEffort.repaint();
            totalActualEffort.repaint();
//            totalActualEffort.getParent().revalidate();
        };

        ActionListener refreshElapsedTime = (e) -> {
//            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
//            timerStartStopButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
            timerStartStopButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTimeToDisplay(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            timerStartStopButton.repaint(); //this is enough to update the value on the screen
//            timerStartStopButton.getParent().repaint(); //this is enough to update the value on the screen

            if (timerStartStopButton.getParent() != null) {
//                timerStartStopButton.getParent().revalidate();//this is enough to update the value on the screen
                timerStartStopButton.getParent().revalidateWithAnimationSafety();//this is enough to update the value on the screen
            }
        };

        timerStartStopButton.setLongPressCommand(Command.create("", null, ev -> {//));{
//            @Override
//            public void longPointerPress(int x, int y) {
//                if (false) {
//                    super.longPointerPress(x, y); //seems calling super.longPointerPress will start/stop the timer on longPress
//                }
            hiddenElapsedTimePicker.setDuration(timerInstance.getElapsedTimeToDisplay()); //must edit total elapsed time since that is what is shown
            hiddenElapsedTimePicker.startEditingAsync();
            hiddenElapsedTimePicker.addActionListener((e) -> {
                TimerInstance timerInstance2 = TimerStack.getInstance().getCurrentTimerInstanceN();
                //UI: OK to longPress a running timer (no need to stop it, will simply adjust timer on Done
                if (timerInstance2 != null) {

                    long oldElapsedTime = timerInstance2.getElapsedTime();
                    long oldElapsedTotalDisplayedTime = timerInstance2.getElapsedTimeToDisplay();

                    if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
                        //adjust elapsed time
                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
                        long diff = editedElapsedTime - oldElapsedTotalDisplayedTime; //NB can also be negative if timer was increased
                        long newElapsedTime = Math.max(0, oldElapsedTime + diff);
//                        timerInstance2.setElapsedTime(newElapsedTime);
                        timerInstance2.updateElapsedTime(newElapsedTime);

                        //since we have edited the total (elapsed+old actual) we may also need to reduce the old actual 
//                            long itemActualReduction = -diff > oldElapsedTime ? -diff - oldElapsedTime : 0;
                        long itemActualReduction = oldElapsedTime + diff < 0 ? -(oldElapsedTime + diff) : 0;
                        if (itemActualReduction > 0) {
                            long itemOldActual = timedItem.getActualEffortProjectTaskItself();
                            long itemNewActual = itemOldActual - itemActualReduction;
                            timedItem.setActualEffort(itemNewActual,false);
                        }
                    } else { //enough to just adjust the elapsed time
                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
//                        timerInstance2.setElapsedTime(editedElapsedTime);
                        timerInstance2.updateElapsedTime(editedElapsedTime);
                    }
                }
                hiddenElapsedTimePicker.stopEditing(null);
                refreshElapsedTime.actionPerformed(null);
                refreshTotalActualEffort.actionPerformed(null);
            });
//                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
        }));

        Runnable updateTimerDisplayForCurrentForm = () -> {
//                new Runnable() {   public void run() {
            // refreshDisplayedTimerInfo();  called on regular updates of the timer screen, as well as on app relaunch
            // setTaskStatusOngoingWhenMinimumThresholdPassed();
            if (Display.getInstance().getCurrent() != form) {
                return; //only update for the displayed form
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
//                    //                    && timerInstance.getElapsedTime() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//                    && timerInstance.getElapsedTimeToDisplay() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
//                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                ASSERT.that(timedItem.getStatus() == ItemStatus.CREATED, "timedItem.status is CREATED, while status.getStatus()=" + status.getStatus());
//                status.setStatus(ItemStatus.ONGOING, false); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)
//                timedItem.setStatus(ItemStatus.ONGOING);
//                status.repaint(); //update UI
//                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> timedItem.setStartedOnDate(System.currentTimeMillis()
//                        - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds
//                                .getInt() * MyDate.SECOND_IN_MILLISECONDS
//                ));
//            }
//</editor-fold>
            //UI: It is OK to start timer on a completed task, it will simply add more time to actual
            if (status.getStatus() == ItemStatus.CREATED //DON'T revert eg cancelled/done/waiting task to ongoing just because time is spent on it
                    //use getElapsedTotalTime since we don't want to change only because elpased itself gets bigger than threshold
                    && timerInstance.getElapsedTotalTime()
                    >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS) {
                ASSERT.that(timedItem.getStatus() == ItemStatus.CREATED,
                        "timedItem.status is " + timedItem.getStatus() + " while status.getStatus()=" + status.getStatus());
                status.setStatus(ItemStatus.ONGOING, false); //UI: as soon as Timer is started, task status is set to Ongoing (except if Waiting or Ongoing or Completed)   
                timedItem.setStatus(ItemStatus.ONGOING);
                status.repaint(); //update UI
                parseIdMap2.put("SET_ITEM_STARTED_ON_DATE", () -> timedItem.setStartedOnDate(
                        System.currentTimeMillis() - MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS
                ));
            }
//            elapsedTimeButton.setText(MyDate.formatTimeDuration(timerInstance.getElapsedTime(), MyPrefs.timerShowSecondsInTimer.getBoolean()));
//            elapsedTimeButton.repaint(); //this is enough to update the value on the screen
            refreshElapsedTime.actionPerformed(null);
            refreshTotalActualEffort.actionPerformed(null);
//            Log.p("RefreshTimer");
        };

        final int BUZZER_DURATION = 300;
        MyUITimer timerTimer = new MyUITimer(updateTimerDisplayForCurrentForm);
        MyUITimer buzzerTimer = new MyUITimer(() -> {
            if (Display.getInstance().getCurrent() != form) {
                return; //only update for the displayed form
            }
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

        ActionListener stopUITimers
                = (e) -> {
                    if (timerTimer != null && timerTimer.isScheduled()) {
                        timerTimer.cancel(); //the timers may have been stopped manually when calling the commands
                    }
                    if (buzzerTimer != null && buzzerTimer.isScheduled()) {
                        buzzerTimer.cancel();
                    }
                };
//<editor-fold defaultstate="collapsed" desc="comment">
//            elapsedTimeButton = new Button("TimerTimer");
//        ActionListener stopTimers = (e) -> {
//            if (timerTimer!=null) timerTimer.cancel(); //stop timer as first thing
////                if (buzzerTimer != null) { //buzzer is optional
////                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //buzzer is optional, only cancel if actually started (otherwise nullpointerexception
//            if (buzzerTimer!=null) buzzerTimer.cancel();
////                }
//        };

//        timerContainer.setLeadComponent(elapsedTimePicker); //ensure that the button behaves like a Picker
//        timerContainer.add(elapsedTimeButton, elapsedTimePicker);
//        elapsedTimePicker.setUIID("TimerTimer");
//        elapsedTimeButton.setCommand(Command.create(null, null, (e) -> {
//        timerStartStopButton.setCommand(Command.create(null, null, (e) -> {
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        parseIdMap2.put("ElapsedTime", () -> {
////            timedItem.setActualEffort(timerInstance.isTimerShowActualTotal()
////                    ? timerInstance.getElapsedTime()
////                    : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
////            timedItem.setActualEffort(timerInstance.getElapsedTimeToDisplay());
//            timedItem.setActualEffort(timedItem.getActualEffortProjectTaskItself() + timerInstance.getElapsedTime());
//        });
//        SaveEditedValuesLocally previousValues = formPreviousValues != null ? formPreviousValues : new SaveEditedValuesLocally("Timer-" + timedItem.getObjectIdP());
//        ActionListener saveEditedItem = (evt) -> {
//            MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
//            DAO.getInstance().saveInBackground(timedItem);
//            previousValues.deleteFile();
//        };
//</editor-fold>
        if (Display.getInstance().isScreenSaverDisableSupported()) {
            Display.getInstance().setScreenSaverEnabled(!MyPrefs.getBoolean(MyPrefs.timerKeepScreenAlwaysOnInTimer));
        }

        // ********************************* COMMANDS **************************
        //Cmd used to show next task and go to it when clicked
        Command cmdStartNextTask = new Command("", null) { //"StartNextTask" - stop and save current task and move to next (autostart if set)
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

//                timerInstance.stopTimer(true);                //stop this timer, save item, //DON'T change status (is normally ONGOING)
                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true); //stop this timer, save item, //DON'T change status (is normally ONGOING)
//<editor-fold defaultstate="collapsed" desc="comment">
//                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) { //
//                TimerStack.getInstance().goToNextTimedItem();
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (TimerStack.getInstance().getCurrentTimerInstanceN() != null) {
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
                stopUITimers.actionPerformed(null);

//                timerInstance.stopTimer(true);                //stop this timer, save item, 
                timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
//<editor-fold defaultstate="collapsed" desc="comment">
//                status.setStatus(ItemStatus.DONE, false);

//                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) {
////                    cmdStartNextTask.actionPerformed(null);
//                    TimerStack.getInstance().goToNextTimedItem();
//                }
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
            }
        };

        /**
        exist large Timer
         */
        Command cmdSaveAndExitTimerScreen = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

                //exit timer altogether: save each timed item, pop/delete all timers
                while (TimerStack.getInstance().activeTimers.size() > 0) {
                    TimerInstance timerInstance = TimerStack.getInstance().activeTimers.remove(TimerStack.getInstance().activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
                    timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false); //no save since deleted below
                    timerInstance.deleteInstance();
                }

//                showPreviousScreenOrDefault(((MyForm) Display.getInstance().getCurrent()).previousForm, true);
                ((MyForm) Display.getInstance().getCurrent()).showPreviousScreenOrDefault(true);
            }
        };

        /**
        exist large Timer
         */
        Command cmdSaveAndExitSmallTimer = new Command("Exit", Icons.iconTimerStopExitTimer) { //"Stop/Exit" "Close/Exit" //TODO select icon for Exit from timer
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);

                //exit timer altogether: save each timed item, pop/delete all timers
                while (TimerStack.getInstance().activeTimers.size() > 0) {
                    TimerInstance timerInstance = TimerStack.getInstance().activeTimers.remove(TimerStack.getInstance().activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
                    timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false); //no save since deleted below
                    timerInstance.deleteInstance();
                }
                TimerStack.getInstance().removeTimerSmallContainer();
                TimerStack.getInstance().refreshOrShowTimerUI();
            }
        };

        Command cmdSetCompletedAndGotoNextTaskOrExit = new Command("Completed", Icons.iconCheckboxDone) {
            @Override
            public void actionPerformed(ActionEvent evt) {

                stopUITimers.actionPerformed(null);

                timerInstance.stopTimer(true);                //stop this timer, save item, 
                status.setStatus(ItemStatus.DONE, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                MyForm.putEditedValues2(parseIdMap2);//update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) {
////                    cmdStartNextTask.actionPerformed(null);
//                    TimerStack.getInstance().goToNextTimedItem();
//                }
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
            }
        };

        Command cmdSetTaskWaitingAndGotoNextTaskOrExit = new Command("Wait", Icons.iconCheckboxWaiting) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.WAITING, false);
                MyForm.showDialogUpdateRemainingTime(remainingEffort);
//<editor-fold defaultstate="collapsed" desc="comment">
//                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) {
////                    cmdStartNextTask.actionPerformed(null);
//                    TimerStack.getInstance().goToNextTimedItem();
//                }
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
            }
        };

        Command cmdSetTaskCancelledAndGotoNextTaskOrExit = new Command("Cancel", Icons.iconCheckboxCancelled) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                stopUITimers.actionPerformed(null);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.CANCELLED, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) {
////                    cmdStartNextTask.actionPerformed(null);
//                    TimerStack.getInstance().goToNextTimedItem();
//                }
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
            }
        };

        Command cmdSetTaskOngoingAndGotoNextTaskOrExit = new Command("Ongoing**", Icons.iconCheckboxCancelled) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                timerTimer.cancel();
//                buzzerTimer.cancel();
                stopUITimers.actionPerformed(null);
                timerInstance.stopTimer(true);
                status.setStatus(ItemStatus.ONGOING, false);
//<editor-fold defaultstate="collapsed" desc="comment">
//                MyForm.putEditedValues2(parseIdMap2); //update item with edited/changed values
//                DAO.getInstance().saveInBackground(timedItem);
//                previousValues.deleteFile();
//                if (timerInstance.isAutoGotoNextTask()) {
////                    cmdStartNextTask.actionPerformed(null);
//                    TimerStack.getInstance().goToNextTimedItem();
//                }
//                getInstance().refreshOrShowTimerUI();
//</editor-fold>
                TimerStack.getInstance().moveToNextTask();
            }
        };

//Show next tasks
        //TODO optimization: only construct nextTask containers etc if there is one and it is shown
        //TODO show button to select auto-start Timer on task or not
//        Item nextItem = TimerStack.getInstance().findNextTimedItem();
//        Item nextItem = TimerStack.getInstance().getTimedItemN();
        Item nextComingItem = TimerStack.getInstance().getTheNextComingTimedItem();
        if (nextComingItem != null) {
//            return null;
            gotoNextTaskButtonWithItemText = MyPrefs.timerShowNextTask.getBoolean()
                    ? new SpanButton("Next: \"" + nextComingItem.getText() + "\"" //                            + (MyPrefs.timerShowRemainingForNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(nextComingItem.getRemainingEffort()) + "]") : ""))
                            + (MyPrefs.timerShowRemainingForNextTask.getBoolean() ? (" [" + MyDate.formatTimeDuration(nextComingItem.getRemainingEffortProjectTaskItself()) + "]") : ""))
                    : new SpanButton(""); //gotoNextTask button is hidden unless timerAutomaticallyGotoNextTask is false
            gotoNextTaskButtonWithItemText.setCommand(cmdStartNextTask);
            if (MyPrefs.getBoolean(MyPrefs.timerAutomaticallyGotoNextTask)) {
//                gotoNextTaskButtonWithItemText.setUIID("Label");
                gotoNextTaskButtonWithItemText.setTextUIID("Label");
            }
            gotoNextTaskButtonWithItemText.setHidden(!MyPrefs.timerShowNextTask.getBoolean());
        }

        Command cmdGotoFullScreenTimer = null;

        if (!fullScreenTimer) {
//                cmdGotoFullScreenTimer = new Command("FullScreenTimer", Icons.iconEditSymbolLabelStyle) {
//            cmdGotoFullScreenTimer = new MyReplayCommand(TIMER_REPLAY, "", Icons.iconEditSymbolLabelStyle) {
            cmdGotoFullScreenTimer = MyReplayCommand.create(TIMER_REPLAY, "", Icons.iconEditSymbolLabelStyle, (e) -> {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
                //save edited values //TODO!!!!
//                        new ScreenTimer6((MyForm) contentPane.getComponentForm(), timerInstance).show();
                new ScreenTimer6((MyForm) form, timerInstance).show();
            });
        }

        status.setStatusChangeHandler((oldStatus, newStatus) -> {
            if (newStatus != oldStatus) {
                timedItem.setStatus(status.getStatus()); //must do *before* calling commands, otherwise removeFromCache will show old value!
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
                        //do nothing - should already be set to Ongoing when starting timer //TODO!!! not if long time is set before making 
                        cmdSetTaskOngoingAndGotoNextTaskOrExit.actionPerformed(null);
                    case CREATED:
                    //do nothing - user forces status back to empty checkbox
                }
                //moved from status.actionListener: 
//                timedItem.setStatus(status.getStatus());
                DAO.getInstance().saveInBackground(timedItem);
            }
        });

//            Container contentPane = fullScreenTimer ? new Container(BoxLayout.y()) : new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_SCALE));
        Container contentPane = fullScreenTimer ? new Container(BoxLayout.y()) : new Container(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER));

//            contentPane.removeAll(); //clear before rebuilding
//            Container cont = new Container(BoxLayout.y());
//            cont.setScrollableY(true);
//            Container contentPane = contentPane;
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
                SpanLabel itemHierarchyContainer = new SpanLabel("Project: " + hierarchyStr);
                itemHierarchyContainer.setHidden(!MyPrefs.getBoolean((MyPrefs.timerAlwaysExpandListHierarchy))); //initial state of visibility
                Button buttonShowItemHierarchy = new Button(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
                buttonShowItemHierarchy.addActionListener((e) -> {
                    itemHierarchyContainer.setHidden(!itemHierarchyContainer.isHidden()); //inverse visibility
                    buttonShowItemHierarchy.setIcon(itemHierarchyContainer.isHidden() ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle); //switch icon
                    buttonShowItemHierarchy.getParent().animateLayout(300);
                });

                contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName)))
                        .add(BorderLayout.EAST, buttonShowItemHierarchy).add(BorderLayout.SOUTH, itemHierarchyContainer));

            } else if (listName != null) {
                contentPane.add(BorderLayout.center(FlowLayout.encloseCenter(new SpanLabel(listName))));
            } //else: no context to show, show nothing
        }

        if (fullScreenTimer) {
            //TODO!!! do NOT use item.isInteruptTask() since we may later continue working on a task that was originally created as an interrupt but after that is just treated as a normal task
            description = new MyTextField(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY) {
                @Override
                public void longPointerPress(int x, int y) {
                    Log.p("longPointerPress on Timer text area");
                    //TODO!!! call "Regular tasks"
                }
            };
            description.setUIID("ScreenItemTaskText");
            description.setColumns(100);
            description.setActAsLabel(true);
//                MyForm.initField(Item.PARSE_TEXT, description, () -> timedItem.getText(),
//                        (t) -> timedItem.setText((String) t), () -> description.getText(), (t) -> description.setText((String) t), previousValues, parseIdMap2);
//            description.addActionListener((e) -> {
//                timedItem.setText(description.getText());
//                DAO.getInstance().saveInBackground(timedItem);
//            });

            if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
//                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                form.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
            }
            description.setText(timedItem.getText());
        } else { //smallTimer
            description = new MyTextField(Item.DESCRIPTION_HINT, 100, 1, 3, MyPrefs.taskMaxSizeInChars.getInt(), TextArea.ANY);
            description.setColumns(100);
            description.setActAsLabel(true);
            description.setUIID("Label");
            description.setEditable(true); //true=editable (but will look like a label until clicked), false=not editable in small container
            description.setText(timedItem.getText());
            if (timedItem.isInteruptOrInstantTask() && description.getText().equals("")) {
//                    contentPane.getComponentForm().setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
                form.setEditOnShow(description); //UI: for interrupt/instant tasks or new tasks (no previous text), automatically enter into description field 
            }
        }

        description.addActionListener((e) -> {
            timedItem.setText(description.getText());
            DAO.getInstance().saveInBackground(timedItem);
        });

//            MyForm.initField(Item.PARSE_STATUS, status, () -> timedItem.getStatus(), (t) -> timedItem.setStatus((ItemStatus) t),
//                    () -> status.getStatus(), (t) -> status.setStatus((ItemStatus) t), previousValues, parseIdMap2);
        if (false) {
            status.addActionListener((e) -> { //is NOT called when status Button's text is changed! BUT when clicked, so must keep!
                timedItem.setStatus(status.getStatus());
                DAO.getInstance().saveInBackground(timedItem);
            });
        }
        editItemButton = new Button(MyReplayCommand.create("EditItemFromTimer-" , timedItem.getObjectIdP(), "", Icons.iconEditSymbolLabelStyle,
                (e) -> {
//                MyForm.putEditedValues2(parseIdMap2, timedItem); //first update Item with any values changed in Timer
//                ScreenItem2 screenItem = new ScreenItem2(timedItem, (MyForm) contentPane.getComponentForm(), () -> {
                    ScreenItem2 screenItem
                    = new ScreenItem2(timedItem, (MyForm) form, () -> {
                        //TODO!!!!!! if item values like description or comment were edited in Timer, they must be shown when editing the item. Simply pass previousValues?!
                        DAO.getInstance().saveInBackground(timedItem);
                        description.setText(timedItem.getText());
                        status.setStatus(timedItem.getStatus());
                        comment.setText(timedItem.getComment());
//                        effortEstimate.setTime((int) timedItem.getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS);
                        effortEstimate.setDuration(timedItem.getEffortEstimate());
//                remainingEffort.setTime((int) timedItem.getRemainingEffort(false, false) / MyDate.MINUTE_IN_MILLISECONDS); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
//                        remainingEffort.setTime((int) timedItem.getRemainingEffortProjectTaskItself() / MyDate.MINUTE_IN_MILLISECONDS); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
                        remainingEffort.setDuration((int) timedItem.getRemainingEffortProjectTaskItself()); //don't use 0 for done tasks (if we time a Done task, want to see actual value stored in Remaining)
                        refreshTotalActualEffort.actionPerformed(null);
//                ScreenTimer6.this.revalidate();
//                    ((MyForm) contentPane.getComponentForm()).revalidate();
//                    ((MyForm) form).revalidate();
                        ((MyForm) form).revalidateWithAnimationSafety();
                    }, false, null); //previousValues: pass locally edited value to ScreenItem
                    screenItem.show();
                }
        ));
        if (fullScreenTimer) {
            contentPane.add(BorderLayout.west(status).add(BorderLayout.CENTER, description).add(BorderLayout.EAST, editItemButton));
        }

        ActionListener startFormUpdateTimers = (e) -> {
            ASSERT.that(form != null);
            timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, form); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
////                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
//                    buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, form);
//                }
            //TODO!!! find better solution than activating the buzzerTimer with Integer.MAX
            buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt() != 0 ? MyPrefs.timerBuzzerInterval.getInt() * MyDate.MINUTE_IN_MILLISECONDS : Integer.MAX_VALUE, true, form); //Integer.MAX_VALUE=25days so little risk of unexpceted buzz
        };

//            elapsedTimeButton = new Button("TimerTimer");
        //If go to nexttask
//            timerStartStopButton = new Button();
        Command timerStartStopCmd = Command.create(null, null, (e) -> {
//            if (timer == null) {
            if (!timerInstance.isRunning()) {                    //start Timer
                //UI: It is OK to start timer on a completed task, it will simply add more time to actual
//                    setTaskStatusOngoingWhenMinimumThresholdPassed(); //done when updating the display
//                    timerInstance.startTimer();
                timerInstance.startTimer(true);
                if (false) {
                    elapsedTimeButton.setEnabled(false); //disable while running
                }
                if (false) {
                    timerStartStopButton.setIcon(Icons.iconTimerPauseLabelStyle);
                }
//                    timerStartStopButton.repaint(); //this is enough to update the value on the screen
//                    timerStartStopButton.getParent().revalidate();//this is enough to update the value on the screen
                timerStartStopButton.getParent().revalidateWithAnimationSafety();//this is enough to update the value on the screen

//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, contentPane.getComponentForm()); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    timerTimer.schedule(Math.max(1, MyPrefs.timerUpdateInterval.getInt()) * MyDate.SECOND_IN_MILLISECONDS, true, form); //UI: max(): update at least every second. TODO change to every minute when timer>60s. Make this an option!
//                    if (MyPrefs.timerBuzzerInterval.getInt() != 0) { //Start Buzzer
//                        buzzerTimer.schedule(MyPrefs.timerBuzzerInterval.getInt(), true, contentPane.getComponentForm());
//                    }
//                    timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small")); //iconTimerStartTimer);
                startFormUpdateTimers.actionPerformed(null);
//    }
            } else {
                timerInstance.stopTimer();
//                    timerTimer.cancel(); //stop timer as first thing
//                    buzzerTimer.cancel();
                stopUITimers.actionPerformed(null);

                if (false) {
                    elapsedTimeButton.setEnabled(true); //enable the picker when timer is paused
                }
                //refresh timer whens topped (since update of display may be a bit behind - otherwise a new timer on a different device may show a different value
                refreshElapsedTime.actionPerformed(null);
                refreshTotalActualEffort.actionPerformed(null);
//                    timerStartStopButton.setIcon(Icons.iconTimerStartLabelStyle); //iconTimerStartTimer);
//                    timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small") + "Paused"); //iconTimerStartTimer);
//                    timerStartStopButton.repaint(); //this is enough to update the value on the screen? NOPE: doesn't increase the size as hours are added!
//                    timerStartStopButton.getParent().revalidate();
                timerStartStopButton.getParent().revalidateWithAnimationSafety();
            }
            timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small") + (timerInstance.isRunning() ? "" : "Paused")); //update uiid to display running/paused timer appropriately
        });
        timerStartStopButton.setCommand(timerStartStopCmd);
        timerStartStopButton.setUIID("TimerTimer" + (fullScreenTimer ? "" : "Small") + (timerInstance.isRunning() ? "" : "Paused")); //iconTimerStartTimer);
//            timerStartStopButton.set(); //iconTimerStartTimer);
        refreshElapsedTime.actionPerformed(null); //update timerStartStopButton with initial time

        if (timerInstance.isRunning()) {
//                timerStartStopCmd.actionPerformed(null); //start the UI timer
            startFormUpdateTimers.actionPerformed(null); //start the UI timer
        }

        if (false) {
            timerStartStopButton.setIcon(timerInstance.isRunning() ? Icons.iconTimerPauseLabelStyle : Icons.iconTimerStartLabelStyle);
        }

        if (fullScreenTimer) { //smallContainer
//            remainingEffort = new MyDurationPicker();
            remainingEffort.setShowZeroValueAsZeroDuration(true); //show "0:00"
//                MyForm.initField(Item.PARSE_REMAINING_EFFORT, remainingEffort,
//                        () -> timedItem.getRemainingEffort(false), (l) -> timedItem.setRemainingEffort((long) l),
//                        () -> remainingEffort.getDuration(), (l) -> remainingEffort.setDuration((long) l), previousValues, parseIdMap2);
//                effortEstimate = new MyDurationPicker();
            effortEstimate.setShowZeroValueAsZeroDuration(true); //show "0:00"
//                MyForm.initField(Item.PARSE_EFFORT_ESTIMATE, effortEstimate, () -> timedItem.getEffortEstimate(), (l) -> timedItem.setEffortEstimate((long) l),
//                        () -> effortEstimate.getDuration(), (l) -> effortEstimate.setDuration((long) l), previousValues, parseIdMap2);
//            boolean effortEstimateBeingAutoupdated = false;
//            boolean remainingEstimateBeingAutoupdated = false;

            effortEstimate.addActionListener((e) -> {
//                effortEstimateBeingAutoupdated=true;
                timedItem.setEffortEstimate(effortEstimate.getDuration(), false); //saved immediately on edit
                if (timedItem.getRemainingEffortProjectTaskItself() == 0
                        && remainingEffort.getDuration() == 0
                        && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()) {
//                    timedItem.setRemainingEffort(effortEstimate.getDuration(), false); //NB. not necessary because updating the duration picker will trigger the other actionListener
                    remainingEffort.setDuration(effortEstimate.getDuration());
                    remainingEffort.repaint();
                }
                DAO.getInstance().saveInBackground(timedItem);
//                effortEstimateBeingAutoupdated=false;
            });

            remainingEffort.addActionListener((e) -> {
//                remainingEstimateBeingAutoupdated=true;
                timedItem.setRemainingEffort(remainingEffort.getDuration(), false); //saved immediately on edit
                if (timedItem.getEffortEstimate() == 0 && effortEstimate.getDuration() == 0
                        && MyPrefs.updateRemainingOrEstimateWhenTheOtherIsChangedAndNoValueHasBeenSetManuallyForItem.getBoolean()) {
//                    timedItem.setEffortEstimate(remainingEffort.getDuration(), false);
                    effortEstimate.setDuration(remainingEffort.getDuration());
                    effortEstimate.repaint();
                }
                DAO.getInstance().saveInBackground(timedItem);
//                remainingEstimateBeingAutoupdated=false;
            });

            effortEstimate.setDuration(timedItem.getEffortEstimate());
//            remainingEffort.setDuration(timedItem.getRemainingEffort());
            remainingEffort.setDuration(timedItem.getRemainingEffortProjectTaskItself());

//            totalActualEffort = new Label(); //MyDate.formatTime(calcTotalEffortInMinutes(item, elapsedTimePicker.getTime(), MyPrefs.getBoolean((MyPrefs.timerShowTotalActualInTimer))), showSeconds), "Button");
            refreshTotalActualEffort.actionPerformed(null);

//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) { //Now done in action listeners above on each field
//                //Automatically update Estimate and Remaining when one of them is set (and no value is defined manually). NB. This will only work for the first one being set.
//                ActionListener estimateAndRemainingUpdater = new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        //update estimate based on remaining (only if estimate item.estimate==0 and no value has been set while editing)
//                        if (remainingEffort.getTime() != 0 && timedItem.getEffortEstimate() == 0 && effortEstimate.getTime() == 0) { //if remaining is changed AND effortEstimate is not...
//                            effortEstimate.setTime(remainingEffort.getTime());
//                            effortEstimate.repaint();
//                        }
//                        //update remaining based on estimate(only if item.remaining==0 and no value has been set while editing)
//                        if (effortEstimate.getTime() != 0 && timedItem.getRemainingEffort(false) == 0 && remainingEffort.getTime() == 0) {
//                            remainingEffort.setTime(effortEstimate.getTime());
//                            remainingEffort.repaint();
//                        }
//                    }
//                };
//                remainingEffort.addActionListener(estimateAndRemainingUpdater);
//                effortEstimate.addActionListener(estimateAndRemainingUpdater);
//            }
//</editor-fold>
            TableLayout tl = new TableLayout(2, 3);
            final Container estimateTable = new Container(tl);

            Button showEffortDetailsButton = new Button(Icons.iconShowMoreLabelStyle);

            showEffortDetailsButton.addActionListener((e) -> {
                showEffortDetailsButton.setIcon(showEffortDetailsButton
                        .getIcon() == Icons.iconShowMoreLabelStyle
                                ? Icons.iconShowLessLabelStyle
                                : Icons.iconShowMoreLabelStyle);
                estimateTable.setHidden(!estimateTable.isHidden());
                MyPrefs.flipBoolean(MyPrefs.timerShowEffortEstimateDetails);
//                            contentPane.getComponentForm().animateLayout(300);
                form.animateLayout(300);
            }
            );
//            Container effortDetailsCont =  LayeredLayout.encloseIn(
//                    FlowLayout.encloseRightMiddle(showEffortDetailsButton), //!!: reuse same strings as from ScreenItem!
//                    //                        GridLayout.encloseIn(3, new Label(""), elapsedTimeButton, FlowLayout.encloseIn(timerStartStopButton))
//                    GridLayout.encloseIn(3, new Label(""), timerStartStopButton)
//            );
            Container effortDetailsCont
                    = BorderLayout.centerAbsoluteEastWest(timerStartStopButton, showEffortDetailsButton, new Label());
            contentPane.add(effortDetailsCont);

            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_ESTIMATE_SHORT)); //"Estimate")); //leftalign labels (like the Tickers)
            estimateTable.add(tl.createConstraint().widthPercentage(34).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_TOTAL_SHORT)); //"Total"));
            estimateTable.add(tl.createConstraint().widthPercentage(33).horizontalAlign(Component.CENTER), new Label(Item.EFFORT_REMAINING_SHORT)); //"Remaining"));
            //TODO make the effort Pickers small (size as the time, not as the cell) and centered (and center the labels above again)
            estimateTable.add(effortEstimate).add(totalActualEffort).add(remainingEffort);  //!!: reuse same strings as from ScreenItem!
            estimateTable
                    .setHidden(!MyPrefs.getBoolean(MyPrefs.timerShowEffortEstimateDetails)); //hide initially
            contentPane.add(BorderLayout.center(estimateTable));

            MyForm.initField(Item.PARSE_COMMENT, comment,
                    () -> timedItem.getComment(), (t) -> timedItem.setComment((String) t),
                    () -> comment.getText(), (t) -> comment.setText((String) t), null, parseIdMap2
            );
            Container commentContainer = ScreenItem2.makeCommentContainer(comment);
            contentPane.add(BorderLayout.center(commentContainer)); //TODO add full screen edit for Notes

            //Action buttons
            //Show interrupted tasks
//        if (currEntry.interruptOrInstantTask) {
            int textPos = Button.RIGHT; //BOTTOM;

            contentPane.add(hiddenElapsedTimePicker);
            contentPane.setScrollableY(true); //since the size of the timer may overflow

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
                    contentPane.add(GridLayout.encloseIn(2, c1, c2)); //autofit
                    contentPane.add(GridLayout.encloseIn(1, c3)); //autofit
                    //UI: as long as there is interrupted task(s!) show only those (not next tasks)
                    contentPane.add(new SpanLabel("Interrupted: " + interruptedItem.getText()));
                } else {
//                    assert size() == 1 : "timerStack should always be size==1 when no tasks was interrupted";
//                    Button c4 = new Button(cmdExitTimer); //"Stop"),
                    Button c4 = new Button(cmdSaveAndExitTimerScreen); //"Stop"),
                    c4.setTextPosition(textPos);
                    Button c5 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"),
                    c5.setTextPosition(textPos);
                    Button c6 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                    c6.setTextPosition(textPos);
                    contentPane.add(GridLayout.encloseIn(2, c4, c5));
                    contentPane.add(GridLayout.encloseIn(1, c6));
                }
//            } else if (currEntry.nextItem == null) {
            } else if (gotoNextTaskButtonWithItemText == null) {
                //DONE!!! use task checkbox to mark task Done/Waiting? Only keep Exit/Next to leave it ongoing
                Button c7 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
                c7.setTextPosition(textPos);
                Button c8 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit);
                c8.setTextPosition(textPos);
                Button c9 = new Button(cmdSetCompletedAndGotoNextTaskOrExit); //"Completed")));
                c9.setTextPosition(textPos);
                contentPane.add(GridLayout.encloseIn(2, c7, c8));
                contentPane.add(GridLayout.encloseIn(1, c9));
            } else {
                Button c10 = new Button(cmdSaveAndExitTimerScreen); //"Exit"),
                c10.setTextPosition(textPos);
                Button c11 = new Button(cmdSetTaskWaitingAndGotoNextTaskOrExit); //"Wait"), 
                c11.setTextPosition(textPos);
                Button c12 = new Button(cmdStopTimerAndGotoNextTaskOrExit); //"Stop", "Next", 
                c12.setTextPosition(textPos);
                Button c13 = new Button(cmdSetCompletedAndGotoNextTaskOrExit);
                c13.setTextPosition(textPos);
                contentPane.add(GridLayout.encloseIn(3, c10, c11, c12));
                contentPane.add(GridLayout.encloseIn(1, c13));
//                        nextTaskCont.add(gotoNextTaskButtonWithItemText);
                contentPane.add(gotoNextTaskButtonWithItemText);
            }
        } else { //SMALL TIMER container
//                Container swipeSmallContainer = new SwipeableContainer(nextTask, null, contentPane);
            boolean interruptTask = false;
            Button nextTask = new Button(cmdStopTimerAndGotoNextTaskOrExit); //, Icons.iconTimerNextTask);
            Button exitTimer = new Button(cmdSaveAndExitSmallTimer); //, Icons.iconTimerNextTask);
            exitTimer.setText(""); //remove text in small timer
            nextTask.setText("");
//                Container swipeable = new SwipeableContainer(BoxLayout.encloseX(nextTask,exitTimer), null, contentPane);
            Container swipeable = new SwipeableContainer(nextTask, exitTimer, contentPane);
//                contentPane.setUIID("SmallTimerContainer");
            swipeable.setUIID("SmallTimerContainer");
//                nextTask.setTextPosition(CN.BOTTOM);
            Button fullScreenTimerButton = new Button(cmdGotoFullScreenTimer);
//<editor-fold defaultstate="collapsed" desc="comment">
//                Container timerContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//                timerContainer.addAll(elapsedTimeButton, timerStartStopButton);
//                timerContainer.addAll(timerStartStopButton);

//                contentPane.add(BorderLayout.centerCenterEastWest(
//                contentPane.add(BorderLayout.center(
//                contentPane.add(BorderLayout.CENTER, interruptTask ? BoxLayout.encloseX(new Label(Icons.iconInterruptToolbarStyle), description) : description);
//                        /*Center*/interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description,
/*Center*/
//                        BorderLayout.west(status).add(BorderLayout.CENTER, interruptTask ? BoxLayout.encloseXNoGrow(new Label(Icons.iconInterruptToolbarStyle), description) : description).add(BorderLayout.EAST, editItemButton),
//</editor-fold>
            /*East*/
            contentPane.add(BorderLayout.EAST,
                    BoxLayout.encloseXNoGrow(timerStartStopButton, fullScreenTimerButton));
//<editor-fold defaultstate="collapsed" desc="comment">
//                                : description).add(BorderLayout.EAST, editItemButton),
//                        BoxLayout.encloseXNoGrow(timerContainer, nextTask, fullScreenTimerButton),
//                        BoxLayout.encloseXNoGrow(timerContainer,  fullScreenTimerButton),
//</editor-fold>
            /*West*/
            Container west = BoxLayout.encloseXNoGrow(status);

            if (interruptTask) {
                west.add(new Label(Icons.iconInterruptToolbarStyle));
            }
            if (timedItem.isInteruptOrInstantTask() && (timedItem.getText() == null || timedItem.getText().isEmpty())) {
                west.add(description); //only make task text editable if empty interrupt task
            } else {
                if (Config.TEST) {
                    west.add(new SpanLabel(timedItem.getText() + "->" + (nextComingItem != null ? nextComingItem.getText() : "<>"))); //otherwise just show as label
                } else {
                    west.add(new Label(timedItem.getText())); //otherwise just show as label
                }
            }
            west.add(hiddenElapsedTimePicker);
//                contentPane.add(BorderLayout.WEST, status);
            contentPane.add(BorderLayout.WEST, west);
            return swipeable;
        }
        return contentPane;
//        }
//        return null;
    }
}
