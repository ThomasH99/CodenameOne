/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Display;
import java.util.List;

/**
 *
 * @author thomashjelm
 */
public class TimerStackModel {

    private List<TimerInstance> activeTimers; //is NOT saved since it is set by reading the list of saved TimerInstances
    private static TimerStackModel INSTANCE = null;
    final static Object TIMER_LOCK = new Object(); //lock operations on Timer such as updating/saving instances or refreshing Timers from Server

    public static TimerStackModel getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = new TimerInstance();
//            DAO.getInstance().getTemplateList();
            INSTANCE = new TimerStackModel(DAO.getInstance().getTimerInstanceList());
        }
        return INSTANCE;
    }

    private TimerStackModel(List<TimerInstance> timerList) {
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
    move this particular timerInstance to next task or remove from stack if no more. Used when a task is marked Done/Cancelled/Deleted elsewhere (in lists, outside the timer)
     */
    public Item moveToNextTimedItemXXX(TimerInstance timerInstance, boolean update) {

        if (timerInstance == null) {
            return null;
        }
        //find next item to run timer on, most likely either next in current list/project, or the one interrupted
        Item nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
        //if none found remove from TimerStack
        if (nextTimedItem == null) {
            activeTimers.remove(timerInstance); //no need to save timerStack, it is build from TimerInstances on the server
            timerInstance.deleteInstance();
        }
        return nextTimedItem;
    }

    /**
    find next suitable for timer and (re-)start the timer if it was running when interrupted
    returns true if timer was started on another item. 
     */
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
//<editor-fold defaultstate="collapsed" desc="comment">
//            activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
//            timerInstance.deleteInstance();
//            if (!activeTimers.isEmpty()) { //try with (new)  timerInstance (if any)
//                timerInstance = getCurrentTimerInstanceN();
//</editor-fold>
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
    private StartTimerResult moveToNextTask() {
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
//            refreshOrShowTimerUI();
//            return true;
            return StartTimerResult.started;
        } else { //no more tasks, show popup (or Toastbar?)
            if (timerInstance != null) {
                if (timerInstance.getTimedProject() != null || timerInstance.getItemList() != null) {
                    showNoTasksToWorkOnNotificationWhenRelevant(timerInstance.getTimedProject(), timerInstance.getItemList());
                    return StartTimerResult.noTask;
                } //else: inbterrupt, so don't display any messages
            }
//            refreshOrShowTimerUI();
//            return false;
return StartTimerResult.noTask;
        }
    }

    private Item getTheNextComingTimedItem() {
        return nextTimedItemImpl(false);
    }

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
            INSTANCE = new TimerStackModel(DAO.getInstance().getTimerInstanceList());
        }
        List<TimerInstance> timers = DAO.getInstance().getTimerInstanceList();
        for (TimerInstance timerInstance : timers) {
            //if timer is stopped on server, but running here, then stop it here and if another is running on server start that one
            //if timer is running on server, but not here
        }
        INSTANCE = new TimerStackModel(timers);
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
     * called when an Item changes status (Done/Cancelled/Deleted) and stops the corresponding timer if one is currently running/paused/Interrupted for this item, updates the timed item with 
    elapsed time and moves to next item (or removes the Timer instance if no next task). 
    
    OLD invalid text: Tests if the
     * current item is being set to DONE/CANCELLED from within the timer to
     * update infinite loops if the timer is setting the item to Done Updates
     * the item actuals with the timer value. Does not start the timer on a next
     * item (like normally done when within the timer. Does everything normally
     * done when exiting the timer (eg pops the timer stack).
     * DOES currently NOT store any other values edited in the timer What
     * happens next time the timer is called??
    return true if a Timer was running for the task. 
     */
    public  StartTimerResult stopTimerIfRunningOnThisItemAndStartTimerOnNext(Item item) { //boolean continueWithNextItem // 
        if (!isATimerActiveFor(item)) //if no timer active (whether runnning, paused or interrupted), then just return
//            return false;
            return StartTimerResult.timerNotActive;

//        if (item.equals(getTimedItemN())) { //if it's currently timed task...
        if (item == getTimedItemN()) { //if it's currently timed task...
            moveToNextTask(); //simply move to next one
            refreshOrShowTimerUI();
//            return true;
            return StartTimerResult.started;
        } else { //see if any interrupted timer was timing the item
            int timerIndex = activeTimers.size() - 1;
            timerIndex--; //skip current one since we dealt with it above
            while (timerIndex >= 0) { //ccheck starting with most recent interrupted timer
                TimerInstance timerInstance = activeTimers.get(timerIndex);
                if (timerInstance.getTimedItem() == item) {
                    //update item with elapsed time 
                    item.setActual(item.getActualForProjectTaskItself() + timerInstance.getElapsedTime(), false);
                    //find next item to run timer on, most likely either next in current list/project, or the one interrupted
//                moveToNextTimedItem(timerInstance, true);
                    Item nextTimedItem = timerInstance.updateToNextTimerItem(true, true); //get next timed item if any, also update project and save
                    //if none found remove from TimerStack
                    if (nextTimedItem == null) {
                        activeTimers.remove(timerInstance); //no need to save timerStack, it is build from TimerInstances on the server
                        timerInstance.deleteInstance();
                    }
                    //we've found the item, stop looking
//                    return true;
                    return StartTimerResult.started;
                }
                timerIndex--;
                //don't save item, it'll be done from whereever this method was called
            }
        }
//        return false;
        return StartTimerResult.noTask;
    }

    /**
    is timedItem the one currently timed by the active timer (whether timer is running or paused)?
    @param timedItem
    @return 
     */
    public boolean isTimerTimingThisNow(Item timedItem) {
        TimerInstance currentTimer = getCurrentTimerInstanceN();
        return (currentTimer != null && (currentTimer.getTimedItem() == timedItem || currentTimer.getTimedItem().equals(timedItem)));
    }

    /**
    is a timer active (whether 
    @param timedItemOrProject
    @return 
     */
    public boolean isATimerActiveFor(Item timedItemOrProject) {
        if (timedItemOrProject != null) return false;
        for (TimerInstance timerInstance : activeTimers) {
            if (timedItemOrProject == timerInstance.getTimedItem() || timedItemOrProject.equals(timerInstance.getTimedItem())) {
                return true;
            }
            if (Config.TEST) ASSERT.that(!timedItemOrProject.equals(timerInstance.getTimedItem()), "a copy of an item is being timed!! Will lead to errors in updates");
        }
        return false;
    }

    public boolean isATimerActiveForThisProject(Item project) {
        if (project != null) return false;
        for (TimerInstance timerInstance : activeTimers) {
            if (project == timerInstance.getTimedProject()) {
                return true;
            }
            if (Config.TEST) ASSERT.that(!project.equals(timerInstance.getTimedProject()), "a copy of an item is being timed!! Will lead to errors in updates");
        }
        return false;
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

    private StartTimerResult pauseCurrentTimerIfNeeded(Item newTimedItemOrProject, boolean interruptOrInstantTask) {
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
                    
//                    return;
                    return StartTimerResult.notAllowed;
                }
            } //not a valid 
        }
    }

    enum StartTimerResult {
        started, noTask, timerNotActive, notAllowed
    }

    private StartTimerResult startTimer(Item timedItemOrProject, ItemList itemList, MyForm previousForm,
            boolean interruptOrInstantTask, boolean startedOnIndividualItemOrProject) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && ReplayLog.getInstance().isReplayInProgress()) {
////            refreshOrShowTimerUI(previousForm);
//            refreshOrShowTimerUI();
//            return;
//        }
//</editor-fold>
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
//            refreshOrShowTimerUI(previousForm, interruptOrInstantTask);
//            refreshOrShowTimerUI();
            return StartTimerResult.started;
        } else { //nothing to time
            //no save of new TimerInstance if not tasks to time
//            showNoTasksToWorkOnNotificationWhenRelevant(newTimerInstance.getTimedProject(), newTimerInstance.getItemList());
            return StartTimerResult.noTask;
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

    public void startTimerOnItemOrItemList(ItemAndListCommonInterface itemOrItemList, MyForm previousForm) {
        if (itemOrItemList instanceof Item)
            startTimerOnItem((Item) itemOrItemList, previousForm);
        else if (itemOrItemList instanceof ItemList)
            startTimerOnItemList((ItemList) itemOrItemList, previousForm);
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

    private StartTimerResult gotoNext(TimerInstance timerInstance, Item timedItem, ItemStatus itemStatus) {
        if (timedItem != null) {
            timerInstance.stopTimer(true);                //stop this timer, save item, 
//                status.setStatus(ItemStatus.DONE, false);
//                timedItem.setStatus(status.getStatus());
            timedItem.setStatus(itemStatus);
        }
        timerInstance.stopTimerUpdateTimedTaskActualsAndSave(true);
        moveToNextTask();
        refreshOrShowTimerUI();
        refreshScreenOnTimerUpdate();
        return StartTimerResult.started; xx;
    }

    private StartTimerResult gotoNext(TimerInstance timerInstance) {
        return gotoNext(timerInstance, null, null);
    }

    private void exitTimer() {
        //exit timer altogether: save each timed item, pop/delete all timers
        while (activeTimers.size() > 0) {
            TimerInstance timerInstance = activeTimers.remove(activeTimers.size() - 1); //pop last timerInstance since it has no more tasks
            timerInstance.stopTimerUpdateTimedTaskActualsAndSave(false); //no save since deleted below
            timerInstance.deleteInstance();
        }
        TimerStack.getInstance().refreshOrShowTimerUI();

    }

}
