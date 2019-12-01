/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.util.UITimer;
import com.parse4cn1.ParseObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * stores each timer (the 'stack' is defined by which is created(??) the latest)
 * @author thomashjelm
 */
public class TimerInstance extends ParseObject {
    //TODO re-read timer stack from server regularly to check (in particular) if a timer has been started on another device so to update UI
    //TODO save timer stack fo server, in background, and removeFromCache
    //TODO when starting app, check if timer stack is non-empty, and show if so (e.g. show little timer window at bottom of screen, even for paused timer)
    //TODO 
    //TODO 
    //TODO 
    //TODO 

    public static String CLASS_NAME = "Timer";

    final static String PARSE_LIST = "list";
    final static String PARSE_TIMED_ITEM = "item";
    final static String PARSE_PROJECT = "project";
//    final static String PARSE_TIMER_RUNNING = "running";
    final static String PARSE_TIMER_START_TIME = "startTime"; //only != 0 when timer is actually running
    final static String PARSE_TIMER_ELAPSED_TIME = "elapsedMillis"; //only != 0 when timer is paused/stopped
//    final static String PARSE_STACK_INDEX = "index"; //the index in the 'virtual' stack of this timer, e.g. first Timer.index==0, next Timer (interrupt).index==1
//    final static String PARSE_AUTOSTART_TIMER = "autostart"; //automatically startTimer the timer on the next item 
//    final static String PARSE_TIMER_PAUSED = "paused"; //timer is only paused (e.g. by interrupt) so shoud automatically restart when interrupt is over 
    final static String PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED = "interrupted"; //timer was interrupted while running so should automatically restart when interrupt is over 
    final static String PARSE_TIMER_TIME_EVEN_INVALID_ITEMS = "timeInvalidTasks"; //time eg Done/Cancelled tasks, used when launching timer via leftSwipe directly on any tasks, to timer will continue on (equally) invalid subtasks etc
    final static String PARSE_TIMER_FULL_SCREEN = "fullScreen"; //timer was interrupted while running so should automatically restart when interrupt is over 
//    final static String PARSE_TIMER_SHOWS_TOTAL_ACTUAL = "showTotal"; //should Timer show total time spend on task, or only time spend during this timing sessions?
//    final static String PARSE_TIMER_AUTO_GOTO_NEXT_TASK = "autoNextTask"; //should Timer show total time spend on task, or only time spend during this timing sessions?
//    private boolean timeEvenInvalidItem = false; //TODO: no need to persist as long as it only supports timing of the specific swipeleft started task (and e.g. invalid subtasks)

//    private Container timerContainer; //the container build for this timer instane, used to add to different forms as the use navigates
//    private boolean timerIsFullScreen; //the container build for this timer instane, used to add to different forms as the use navigates
    TimerInstance() { //used to instantiate internalized timers
        super(CLASS_NAME);
    }

//    TimerInstance(Item item, ItemList itemList, boolean autoStartTimer, boolean autoGotoNextTask) {
    TimerInstance(Item item, ItemList itemList, boolean timeEvenInvalidItem) {
//        super(CLASS_NAME);
        this();
        setTimeEvenInvalidItemOrProjects(timeEvenInvalidItem);
//        setAutostart(autoStartTimer);
//        setAutoGotoNextTask(autoGotoNextTask);
//        if (false) {
//            setShowTotalActualXXX(MyPrefs.timerShowTotalActualInTimer.getBoolean());
//        }
        setSources(item, itemList);
    }

//    TimerInstance(Item item) {
//        this(item, null, MyPrefs.timerAutomaticallyStartTimer.getBoolean(),MyPrefs.timerAutomaticallyGotoNextTask.getBoolean());
//    }
//
//    TimerInstance(ItemList itemList) {
//        this(null, itemList, MyPrefs.timerAutomaticallyStartTimer.getBoolean(),MyPrefs.timerAutomaticallyGotoNextTask.getBoolean());
//    }
    private UITimer reloadTimersFromParseServer; //used to determine how often to check if the timer state on the Parse Server has changed (e.g. 

    public String toString() {
        return ((getTimedItemList() != null ? " List:" + getTimedItemList().getText() : "")
                + (getTimedProject() != null ? " Proj:" + getTimedProject().getText() : "")
                + (getTimedItemN() != null ? "Task:" + getTimedItemN().getText() : "")
                + (getStartTimeD().getTime() != 0 ? " Start:" + getStartTimeD() : "")
                + (getElapsedTime() != 0 ? " Duration:" + MyDate.formatDurationShort(getElapsedTime()) : "")
                + (isRunning() ? " Running" : " Stopped")
                + (isFullScreen()? " BIG" : " SMALL")
                + " [" + getObjectIdP() + "]");
//                +(isAutostart());
    }

//    private List<ScreenTimer2> timers = new ArrayList();
//    @Override
    public void saveMe() {
        DAO.getInstance().saveInBackground(this);
    }

    private void showNoMoreTasksDialogWhenRelevant(Item item, ItemList itemList) {
        if ((item != null && item.isProject()) || itemList != null) { //only show if item or itemList are defined
            String itemOrListName = item != null ? item.getText() : itemList.getText();
            Dialog.show("Timer", "No tasks to work on in \"" + itemOrListName + "\"", "OK", null);
        }
    }

    private boolean showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(Item item, ItemAndListCommonInterface projectOrItemList) {
        if (projectOrItemList instanceof ItemList)
            return Dialog.show("Timer", "The timed task \"" + item.getText()
                    + "\" is no longer in the list \"" + projectOrItemList.getText()
                    + "\". \n\n Start over from the start of the list or stop timer?", "Start over", "Stop Timer");
        else //if (projectOrItemList instanceof Item)
            return Dialog.show("Timer", "The timed task \"" + item.getText()
                    + "\" is no longer in the project \"" + projectOrItemList.getText()
                    + "\". \n\n Start over from the start of the project or stop Timer?", "Start over", "Stop Timer");
    }

    /**
    set the new timed item, reset start/elapsed time for new items. 
    @param timedItem 
     */
    private void setTimedItem(Item timedItem) {
        Item previousItem = getTimedItemN();
//        if ((timedItem != null && !timedItem.equals(previousItem))                ||) {
//        if (!Objects.equals(timedItem, previousItem)) {
        //definition from Objects.equals() => return (a == b) || (a != null && a.equals(b))
//        if (!((timedItem == previousItem) || timedItem != null && timedItem.equals(previousItem))) { //do not update if timedItem is the same as before (including if was null before and after) changed
        if (!((timedItem == previousItem) || timedItem != null && timedItem==previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            //reset both when changing timedItem
            setStartTime(0);
            setElapsedTime(0);
            if (timedItem != null) {
                put(PARSE_TIMED_ITEM, timedItem);
            } else {
                remove(PARSE_TIMED_ITEM);
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setTimedItemXXX(Item timedItem) {
//        Item previousItem = getTimedItemN();
//        if (false) {
////            Item previousItem = getTimedItemN();
//            if (previousItem != null && isRunning()) {
//                stopTimer(false); //don't save because saved once below
//                addTimerElapsedTimeToItemActualEffort(previousItem, getElapsedTime());
//                DAO.getInstance().saveInBackground(previousItem);
//            }
//        }
////        if (previousItem != null && !previousItem.equals(timedItem)) {
//        if (timedItem != null && !timedItem.equals(previousItem)) {
//            //reset both when changing timedItem
//            setStartTime(0);
//            setElapsedTime(0);
//        }
//        if (timedItem != null) {
//            put(PARSE_TIMED_ITEM, timedItem);
//        } else {
//            remove(PARSE_TIMED_ITEM);
//        }
//        if (false) {
//            if (isAutostart()) {
//                setStartTimeToNowAndStartNow();
////            startTimer(false);
//            }
//            saveMe();
////        if (previousItem != null && !previousItem.equals(timedItem)) {
////            setStartTime();
////        }
//        }
//    }
//</editor-fold>
    /**
    don't call directly, go via timerStack to ensure the available item is returned
    @return 
     */
    Item getTimedItemN() {
        Item timedItem = (Item) getParseObject(PARSE_TIMED_ITEM);
        if (timedItem != null) {
            timedItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(timedItem);
        }
        return timedItem;
    }

    private void setTimedProject(Item timedItem) {
        if (timedItem != null) {
            put(PARSE_PROJECT, timedItem);
        } else {
            remove(PARSE_PROJECT);
        }
    }

    public Item getTimedProject() {
        Item timedItem = (Item) getParseObject(PARSE_PROJECT);
//        if (timedItem != null) {
        timedItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(timedItem);
//        }
        return timedItem;
    }

    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            put(PARSE_TIMER_FULL_SCREEN, true);
        } else {
            remove(PARSE_TIMER_FULL_SCREEN);
        }
    }

    public boolean isFullScreen() {
        Boolean fullScreen = getBoolean(PARSE_TIMER_FULL_SCREEN);
//        if (fullScreen != null)
//            return true;
//        else 
//            return false;
        return fullScreen != null;
    }

    private void setItemList(ItemList itemList) {
        if (itemList != null) {
            put(PARSE_LIST, itemList);
        } else {
            remove(PARSE_LIST);
        }
    }

    public ItemList getTimedItemList() {
        ItemList itemList = (ItemList) getParseObject(PARSE_LIST);
//        if (itemList != null) {
        itemList = (ItemList) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(itemList);
//        }
        return itemList;
    }

    private void setTimeEvenInvalidItemOrProjects(boolean timeEvenInvalidItemsOrProjects) {
        if (timeEvenInvalidItemsOrProjects) {
            put(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS, timeEvenInvalidItemsOrProjects);
        } else {
            remove(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS);
        }
    }

    public boolean isTimeEvenInvalidItemOrProjects() {
        Boolean timeEvenInvalidItemsOrProjects = getBoolean(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS);
//        if (i
        return timeEvenInvalidItemsOrProjects == null ? false : true;
    }

    private final void setStartTime(Date start) {
        if ((start != null && start.getTime() != 0)) {
            put(PARSE_TIMER_START_TIME, start);
//            if (false) {
//                setShowTotalActualXXX(MyPrefs.timerShowTotalActualInTimer.getBoolean()); //Capture if startTime is articially earlier 
//            }
        } else {
            remove(PARSE_TIMER_START_TIME);
//            if (false) {
//                setShowTotalActualXXX(false);
//            }
        }
//        setElapsedTime(0);

    }

    /**
    when startTime != 0, it means the timer is running, if ==0, then elapsesTime indicates time elapsed (0 if new timer, >0 if paused timer)
    @param start 
     */
    private final void setStartTime(long start) {
//        if ((startTimer != null && startTimer.getTime() != 0)) {
//        if (startTimer != 0) {
//            put(PARSE_TIMER_START_TIME, startTimer);
//        } else {
//            remove(PARSE_TIMER_START_TIME);
//        }
        setStartTime(new Date(start));
//        setElapsedTime(0);
    }

    private final void setStartTimeToNowAndStartNow() {
        setStartTime(new MyDate());
    }

    public Date getStartTimeD() {
        Date startTime = getDate(PARSE_TIMER_START_TIME);
        return (startTime == null) ? new Date(0) : startTime;
    }

    public long getStartTime() {
//        Long startTime = getLong(PARSE_TIMER_START_TIME);
//        return (startTime == null) ? 0L : startTime;
        return getStartTimeD().getTime();
    }

    public final void setElapsedTime(long timerDuration) {
        if (timerDuration != 0) {
            put(PARSE_TIMER_ELAPSED_TIME, timerDuration);
        } else {
            remove(PARSE_TIMER_ELAPSED_TIME);
        }
//        setStartTime(new Date(0));
    }

    /**
    updates elapsed time after the user editing it, will either update startTime if currently running, or elapsed time if stopped/paused
    @param newElapsedTime 
     */
    public final void updateElapsedTime(long newElapsedTime) {
        if (isRunning()) {
            setStartTime(MyDate.currentTimeMillis() - newElapsedTime); // + addPreviousActual;
        } else {
            setElapsedTime(newElapsedTime);
        }
    }

    /**
    elapsed time in milliseconds, this is the actual time the timer has been running 
    in this timing session (previous Actual values NOT added - that is done in getElapsedTotalTime())
     * @return 
     */
    public long getElapsedTime() {
//        long addPreviousActual = 0;
//        if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
//            addPreviousActual = getTimedItemN().getActualEffortProjectTaskItself();
//        }
        if (isRunning()) {
            return MyDate.currentTimeMillis() - getStartTime(); // + addPreviousActual;
        }
        Long elapsedTime = getLong(PARSE_TIMER_ELAPSED_TIME);
        return ((elapsedTime == null) ? 0L : elapsedTime); // + addPreviousActual;
    }

    /**
    this is the total Actual time (current timer elapsed + any previous Actual recorded for the task)
    @return 
     */
    public long getElapsedTotalTime() {
        return getElapsedTime() + (getTimedItemN() != null ? getTimedItemN().getActualForProjectTaskItself() : 0); //optimization!!! getTimedItem() will load item from cache!
    }

    /**
    value to show in timer (total or just elapsed this timing session depending on setting)
    @return 
     */
    public long getElapsedTimeToDisplay() {

        if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
//            return getElapsedTime() + getTimedItem().getActualEffortProjectTaskItself(); //optimization!!! getTimedItem() will load item from cache!
            return getElapsedTotalTime(); //optimization!!! getTimedItem() will load item from cache!
        } else {
            return getElapsedTime();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private final void setAutostart(boolean autostart) {
////        if (autostart) {
////            put(PARSE_AUTOSTART_TIMER, autostart);
////        } else {
////            remove(PARSE_AUTOSTART_TIMER);
////        }
//    }
//</editor-fold>
    private final void setInterruptedWhileRunning(boolean interrupted) {
        if (interrupted) {
            put(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED, interrupted);
        } else {
            remove(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED);
        }
    }

    public boolean isInterruptedWhileRunning() {
//        Boolean paused = getBoolean(PARSE_TIMER_PAUSED);
        Boolean paused = getBoolean(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED);
        return (paused == null) ? false : paused;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
    show actual affects the state so cannot be changed for an already started timer
    @param showTotalActual
     */
//    public void setShowTotalActualXXX(boolean showTotalActual) {
//        if (showTotalActual) {
//            put(PARSE_TIMER_SHOWS_TOTAL_ACTUAL, showTotalActual);
//        } else {
//            remove(PARSE_TIMER_SHOWS_TOTAL_ACTUAL);
//        }
//    }
//    public boolean isTimerShowActualTotal() {
//        Boolean showTotalActual = getBoolean(PARSE_TIMER_SHOWS_TOTAL_ACTUAL);
//        return (showTotalActual == null) ? false : showTotalActual;
//    }
//    final void setAutoGotoNextTaskXXX(boolean autoGotoNextTask) {
//        if (autoGotoNextTask) {
//            put(PARSE_TIMER_AUTO_GOTO_NEXT_TASK, autoGotoNextTask);
//        } else {
//            remove(PARSE_TIMER_AUTO_GOTO_NEXT_TASK);
//        }
//    }
//</editor-fold>
    public boolean isAutoGotoNextTask() {
//        Boolean autoGotoNextTask = getBoolean(PARSE_TIMER_AUTO_GOTO_NEXT_TASK);
//        return (autoGotoNextTask == null) ? false : autoGotoNextTask;
        return MyPrefs.timerAutomaticallyGotoNextTask.getBoolean();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Container getTimerContainer() {
//        return timerContainer;
//    }
//
//    public void setTimerContainer(Container timerContainer) {
//        this.timerContainer = timerContainer;
//    }
//    public void setTimerFullScreen(boolean timerIsFullScreen) {
//        this.timerIsFullScreen = timerIsFullScreen;
//    }
//
//    public boolean isTimerFullScreen() {
//        return timerIsFullScreen;
//    }
    /**
    return the time the timer has been running, whether currently running or paused
    @return
     */
//    public long getTimeXXX() {
//        if (getElapsedTime() > 0) {
//            return getElapsedTime();
//        } else {
//            return System.currentTimeMillis() - getStartTime();
//        }
//    }
//</editor-fold>
    /**
    return the time the timer has been running, whether currently running or paused
    @return 
     */
    public boolean isRunning() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (getStartTime() != 0) {
//            return true;
//        } else {
//            return false;
//        }
//</editor-fold>
        return getStartTime() != 0;
    }

//    private boolean isThereATaskToTime() {
//        return getTimedItem()!= null;
//    }
//    private final Object TIMER_LOCK = new Object();
    /**
    start the timer, does nothing if already running
    @param save 
     */
    public synchronized void startTimer(boolean save) {
//        if (getStartTimeD().getTime()==0){
        synchronized (TimerStack.TIMER_LOCK) { //TODO! is it (still/really) necessary to lock?
//            if (getStartTime() == 0) { //do nothing if already running
            if (!isRunning()) { //do nothing if already running
//            setStartTime(new Date(new Date().getTime() - getElapsedTime()));
//            setStartTime(new Date(new Date().getTime() - getElapsedTime()));
                setStartTime(MyDate.currentTimeMillis() - getElapsedTime()); //set a 'virtual' start time elapsed seconds before 'now'
                setElapsedTime(0); //reset elapsed to 0 while timer is running
                if (save) {
                    saveMe(); //update server
                }
            }
        }
    }

    /**
    start the timer. If it is already running, this has no effect
     */
//    public void startTimerXXX() {
//        startTimer(true);
//    }
    public void stopTimer(boolean save) {
        synchronized (TimerStack.TIMER_LOCK) {
            if (isRunning()) { //getStartTime() != 0) {
//            setElapsedTime(new Date().getTime() - new Date().getTime());
                setElapsedTime(MyDate.currentTimeMillis() - getStartTime());
                setStartTime(0);
//                setStartTime(0);
                if (save) {
                    saveMe(); //update server
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            else {
//                setElapsedTime(0);
//            }
//            setStartTime(0);
//            if (save) {
//                saveMe(); //update server
//            }
//</editor-fold>
        }
    }

    /**
    stopTimer the timer if it is running, if not, no effect
     */
    public void stopTimer() {
        stopTimer(true);
    }

    /**
    
    @param timerInstance the 
     */
    public void stopTimerOnTimedItemAndUpdateActualsAndSave(boolean saveUpdatedTimerInstance) {//TimerInstance timerInstance) {
        stopTimer(false); //false: saved below
//        TimerInstance timerInstance = this;
//        if (timerInstance.getElapsedTime() > 0) {
        if (getElapsedTime() > 0) {
//            Item timedItem = timerInstance.getTimedItem(); //get the item that is/was timed
            Item timedItem = getTimedItemN(); //get the item that is/was timed
//                timedItem.setActualEffort(timerInstance.isTimerShowActualTotal() //update actual
//                        ? timerInstance.getElapsedTime()
//                        : timerInstance.getElapsedTime() + timedItem.getActualEffortProjectTaskItself());
//            timedItem.setActual(timerInstance.getElapsedTime() + timedItem.getActualForProjectTaskItself(), false);
            timedItem.setActual(timedItem.getActualForProjectTaskItself() + getElapsedTime(), false);
            DAO.getInstance().saveInBackground(timedItem);
//            timerInstance.setElapsedTime(0); //reset elapsed time since it's now been added to Item's actual & saved
            setElapsedTime(0); //reset elapsed time since it's now been added to Item's actual & saved
        }
        if (saveUpdatedTimerInstance) {
            saveMe(); //update server
        }
    }

//    public void setTimerStateXXX(boolean startTimer) {
//        if (startTimer) {
//            startTimer(true);
//        } else {
//            stopTimer(true);
//        }
//    }
    /**
    sets the sources of timed items, and find the first valid  start the timer if defined
    @param itemOrProject first item to to time - if not valid, and not launched specifically
    @param itemList 
     */
    private void setSources(Item itemOrProject, ItemList itemList) {
        ASSERT.that(itemOrProject != null || itemList != null);
        setItemList(itemList);
        setTimedItem(itemOrProject);
        if (itemOrProject == null || itemOrProject.isProject()) { //don't call updateToNext if only one task
//            updateToNextTimerItem(true, false); //initialize to first element to time (itemOrProject; first subtask in itemOrProject; or first item in itemList or first subtask in first project in itemList)
            updateToNextTimerItem(true); //initialize to first element to time (itemOrProject; first subtask in itemOrProject; or first item in itemList or first subtask in first project in itemList)
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) { //don't update here, first access to TiemrStack.getTimedItemN will do that
//            if (TimerStack.isValidItemForTimer(itemOrProject)) {
//                setTimedItem(itemOrProject);
//            } else {
//                if (itemList != null) {
//                    //get first item in list - NO: this will not check for validitem
//                    updateToNextTimerItem(true, false); //saved elsewhere
//                }
//            }
//        }
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void setSourcesOLD(Item itemOrProject, ItemList itemList) {
//        ASSERT.that(itemOrProject != null || itemList != null);
//        setItemList(itemList);
//
//        if (itemOrProject == null) {
//            if (itemList != null) {
//                //get first item in list - NO: this will not check for validitem
////            item = (Item) itemList.getNextItemAfter(null, true); //item may become null
//                updateToNextTimerItem(true, false); //saved elsewhere
//            } //else  {
//
////<editor-fold defaultstate="collapsed" desc="comment">
//            //
//            //        if (item != null) {
//            //            Item leafTask;
//            //            if (item.isProject() && (leafTask = item.getNextLeafItem(null)) != null) { //if there's a leaf task pick that
//            //                setTimedProject(item); //item is a project with valid subtasks
//            //                setTimedItem(leafTask);
//            //                return;
//            //            } else {
//            //                setTimedProject(null); //no project
//            //                setTimedItem(item);
//            //                return;
//            //            }
//            //        }
////</editor-fold>
//        } else { //itemOrProject != null
//            if (!TimerStack.isValidItemForTimer(itemOrProject)) {
//                setTimedItem(itemOrProject); //no valid item found, item==null && itemList did not contain a valid task or project with valid leaf task
//            }
////        save();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns true if the minimum Timer threshold for when to update status and
     * save time is passed
     *
     * @return
     */
//    private boolean isMinimumThresholdPassed(long elapsedTime) {
////        return status.getStatus() != ItemStatus.CREATED //if status already other than created we store any time -NO, since this will also later create a timer Start/Stop recording
////                && timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.getInt(MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActuals) * MyDate.SECOND_IN_MILLISECONDS;
////        return timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
//        return elapsedTime >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
//    }
//
//    private void addTimerElapsedTimeToItemActualEffort(Item item, long actualEffort) {
//        if (isMinimumThresholdPassed(actualEffort)) {
//            item.setActualEffort(item.getActualEffortProjectTaskItself() + actualEffort);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void refreshTimerUIWithNewItem() {
//        Container currentContainer = getTimerContainer();
//        Form form = currentContainer.getComponentForm();
//        if (form instanceof ScreenTimer6) {
//            ((MyForm) form).showPreviousScreenOrDefault(fullScreenTimer); //exit full screen Timer UI, return to previous screen
//        } else {
//            Container parent = currentContainer.getParent();
//            if (parent != null) {
//                parent.removeComponent(currentContainer); //remove small timer window from its parent
//            }
//            if (form != null) {
//                form.animateLayout(300); //animate form to remove timerPane
//            }
//        }
//    }
//</editor-fold>
    /**
    find next item to time for this timerInstance (e.g. next subtask or next task/project in a list)
    @return 
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    Item findNextItemXXX() {
////        return findNextItem(false); //just find and return the next item (if any)
////    }
////
////    Item findNextItem(boolean updateProject) {
//        Item previousTimedItem = getTimedItemN();
//        if (previousTimedItem == null) { //no previousTimerItem, start wtih *first* item (if any)
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && isRunning()) {
////            stopTimer();
////            addTimerElapsedTimeToItemActualEffort(previousTimedItem, getElapsedTime());
////            DAO.getInstance().saveInBackground(previousTimedItem);
////        }
////</editor-fold>
//        Item nextTimedItem = null;// = null;
//
//        Item project = getTimedProject();
//        if (project != null) {
//            nextTimedItem = project.getNextLeafItem(previousTimedItem);
//        }
//
//        if (nextTimedItem == null) {
//            //no project or leaf task found above, let's try the itemList
//            ItemList itemList = getItemList();
//            if (itemList == null) {
//                nextTimedItem = null; //no itemList, so no more sources to explore for tasks
////            return null; //no itemList, so no more sources to explore for tasks
//            } else {
////                while (nextTimedItem == null) { //iterate through the list until finding a task or a project with a subtask
//                nextTimedItem = (Item) itemList.getNextItemAfter(previousTimedItem, false); //false=> UI: don't expect to circle around a list and start on previous items (need to restart timer on the list for that)
//                if (nextTimedItem != null) {
//                    if (nextTimedItem.isProject()) { //temList.getNextItemAfte) should only return a project if it is not Done, meaning it must have a subtask
//                        nextTimedItem = nextTimedItem.getNextLeafItem(null); //get first subtasks
////                            if (nextTimedItem != null && updateProject) {
////                                setTimedProject(nextTimedItem); //set project
////                            }
//                    } //else { //not a project
////                            if (updateProject) {
////                                setTimedProject(null); //set project
////                            }
////                        }
//                }
////                }
//            }
////            if (nextTimedItem == null) {
////                showNoMoreTasksDialogWhenRelevant(getTimedProject(), getItemList());
////            }
//        }
////        setTimedItem(nextTimedItem);
////        save();
//        return nextTimedItem;
//    }
//</editor-fold>
//    Item findNextItemXXX() {
//        return updateToNextTimerItem(false, false);
//    }
    class GetNextItemStatus {

        Item nextItem; //found item or null
        boolean previouslTimedItemNotFound; //true if the (previously) timed item was not found, e.g. ItemList or Project has been modified so it's no longer there)

        GetNextItemStatus(Item item, boolean timedItemNotFound) {
            this.nextItem = item;
            this.previouslTimedItemNotFound = timedItemNotFound;
        }
    }

    /**
    update. NB! The currently time item may have disappeared from the list or project (filtered, 
     or a list like Today may have been refreshed), so there is not guarantee that
    the item is found, in that case, either return false
    @param update 
    @param save
    @return 
     */
//    GetNextItemStatus updateToNextTimerItem(boolean update, boolean save) {
    GetNextItemStatus updateToNextTimerItem(boolean updateAndSave) {
        Item previousTimedItem = getTimedItemN(); //may return null on first call, in which case the very first subtask will be returned
        Item nextTimedItem = null;
        Item project = getTimedProject();
        ItemList itemList = null;
        boolean alwaysStartOverOnFirstElement
                = MyPrefs.enableTimerToRestartOnLists.getBoolean()
                && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean(); //restartTimerFromFirstElementIfCurrentEltNotFound

        Item lookForItemAfterThis = previousTimedItem; //the (now previous) item to look for in the lists to identify the subsequent/consecutive item
        do {
//<editor-fold defaultstate="collapsed" desc="comment">
//            Item previousSubtask = previousTimedItem;
//            while (project != null && nextTimedItem == null) {
//                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //getNextLeafItem will only return valid subtasks (matching condition), or null
//                if (nextTimedItem == null) {
//                    project = null; //this project has no more leaf tasks so removed
//                    previousTimedItem = null; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll task the first task/subtask in the next tasks/project
//                }
//            }
//</editor-fold>
            if (project != null) {
                List<Item> leafTasks = project.getLeafTasksAsList(null); //getNextLeafItem will only return valid subtasks (matching condition), or null

//                if (!alwaysStartOverOnFirstElement && !leafTasks.contains(lookForItem))
//                    if (showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(lookForItem, project)) {
                if (!alwaysStartOverOnFirstElement && previousTimedItem!=null && !leafTasks.contains(previousTimedItem))
                    if (showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(previousTimedItem, project)) {
                        alwaysStartOverOnFirstElement = true;
                    } else {
//                        stopCurrentTimerInstanceContinueWithPrevious();
//                        return null;
                        return new GetNextItemStatus(null, true);
                    }

                do {
//                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //return valid subtasks (matching condition), or null
                    //NB! getNextLeafItem CANNOT be used because will only work if previousTimedItem matches the condition
//                        nextTimedItem = ItemList.getNextItemAfter(leafTasks, lookForItem, false);
                    nextTimedItem = ItemList.getNextItemAfter(leafTasks, lookForItemAfterThis, alwaysStartOverOnFirstElement);
                    if (nextTimedItem != null) {
                        if (!TimerStack.isValidItemForTimer(nextTimedItem) && !isTimeEvenInvalidItemOrProjects()) {
                            lookForItemAfterThis = nextTimedItem; //look for next item *after* the (invalid) nextTimedItem
                            nextTimedItem = null; //skip invalid items
                        }
                    } else { //didn't find any suitable items, continue looking in itemList (if any) 
                        previousTimedItem = project; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll use project as the first task/subtask in the next tasks/project
                        project = null; //this project has no more leaf tasks so removed
                    }
                } while (nextTimedItem == null && project != null); // !(nextTimedItem == null && project != null) <=> (nextTimedItem != null || project == null)
            }
            //post-condition: either a nextTimedItem is found or project==null

            if (nextTimedItem == null) { //if no suitable subtask found in project, continue with next in list
                itemList = getTimedItemList();

                alwaysStartOverOnFirstElement = MyPrefs.enableTimerToRestartOnLists.getBoolean()
                        && (itemList.isRestartTimerOnNotFound()
                        || MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());

                if (itemList != null) {
//                    if (!alwaysStartOverOnFirstElement && lookForItem != null && !itemList.contains(lookForItem)) {
//                        if (showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(lookForItem, project)) {
                    if (!alwaysStartOverOnFirstElement && previousTimedItem != null && !itemList.contains(previousTimedItem)) {
                        if (showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(previousTimedItem, project)) {
                            alwaysStartOverOnFirstElement = true;
                        } else {
//                            stopCurrentTimerInstanceContinueWithPrevious();
//                            return null;
                            return new GetNextItemStatus(null, true); //previousIte not found
                        }
                    }

                    while (nextTimedItem == null && project == null && itemList != null) { //iterate through list until nextItem or Project is found
//                        nextTimedItem = (Item) itemList.getNextItemAfter(previousTimedItem, false); //if previousTimedItem==null, return first element! false=> UI: don't expect start from start of list when last one's past
//                        nextTimedItem = (Item) itemList.getNextItemAfter(previousTimedItem, false); //if previousTimedItem==null, return first element! false=> UI: don't expect start from start of list when last one's past
//                        alwaysStartOverOnFirstElement = MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                                && (itemList.isRestartTimerOnNotFound()
//                                || MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
                        ItemAndListCommonInterface nextElement = itemList.getNextItemAfter(previousTimedItem, alwaysStartOverOnFirstElement); //if previousTimedItem==null, return first element! false=> UI: don't expect start from start of list when last one's past
//                        if (nextTimedItem != null) {
                        if (nextElement instanceof Item) {
                            nextTimedItem = (Item) nextElement;
                            if (TimerStack.isValidItemForTimer(nextTimedItem) || isTimeEvenInvalidItemOrProjects()) {
                                previousTimedItem = null; //reset previous since we've now found the following item and don't want next iteration to search for an item after previous
                                if (nextTimedItem.isProject()) {
                                    project = nextTimedItem; //set project to search for a subtask
                                    nextTimedItem = null; //force to repeat do while to check if there's a suitable subtask
                                } //else: we've found the next item!
//<editor-fold defaultstate="collapsed" desc="comment">
//                                else { //else: a project, so we'll see if it is valid in the while(isValidItemForTimer...)
////                                    if (!TimerStack.isValidItemForTimer(nextTimedItem)) {
//                                    previousTimedItem = nextTimedItem;// get the next item *after* the nextTimeItem already found
//                                    nextTimedItem = null;
////                                    }
//                                }
//</editor-fold>
                            } else { //not Valid
//                                previousTimedItem = nextTimedItem;// get the next item *after* the nextTimedItem already found
                                lookForItemAfterThis = nextTimedItem;// get the next item *after* the nextTimedItem already found
                                nextTimedItem = null;
                            }
                        } else if (nextElement instanceof WorkSlot) { //treat WorkSlot like a project, that is run the timer on the tasks 'inside' the workslot
                            if (TimerStack.isValidItemForTimer(nextTimedItem) || isTimeEvenInvalidItemOrProjects()) {
//                                previousTimedItem = null; //reset previous since we've now found the following item and don't want next iteration to search for an item after previous
                                lookForItemAfterThis = null; //reset previous since we've now found the following item and don't want next iteration to search for an item after previous
                                if (nextTimedItem.isProject()) {
                                    project = nextTimedItem; //set project to search for a subtask
                                    nextTimedItem = null; //force to repeat do while to check if there's a suitable subtask
                                } //else: we've found the next item!
//<editor-fold defaultstate="collapsed" desc="comment">
//                                else { //else: a project, so we'll see if it is valid in the while(isValidItemForTimer...)
////                                    if (!TimerStack.isValidItemForTimer(nextTimedItem)) {
//                                    previousTimedItem = nextTimedItem;// get the next item *after* the nextTimeItem already found
//                                    nextTimedItem = null;
////                                    }
//                                }
//</editor-fold>
                            } else { //not Valid
//                                previousTimedItem = nextTimedItem;// get the next item *after* the nextTimedItem already found
                                lookForItemAfterThis = nextTimedItem;// get the next item *after* the nextTimedItem already found
                                nextTimedItem = null;
                            }
                        } 
//<editor-fold defaultstate="collapsed" desc="comment">
//                        else if (previousTimedItem instanceof Item) { // nextTimedItem == null
//                            if (Dialog.show("", "The last timed task \"" + previousTimedItem.getText()
//                                    + "\"is no longer in the list. Do you want to exit the timer or restart timing from the first task?",
//                                    "Exit", "Restart")) {
//                                //EXIT
//                                itemList = null; //no more elements in the list, stop the do while loop
//                            } else { //RESTART
//
//                            }
//                        }
//</editor-fold>
                    }
                }
            }
//        } while ((nextTimedItem == null || !TimerStack.isValidItemForTimer(nextTimedItem)) && (project != null || itemList != null));
        } while ((nextTimedItem == null) && (project != null || itemList != null));

//        if (update) {
        if (updateAndSave) {
            setTimedItem(nextTimedItem);
            if (project instanceof Item)
                setTimedProject(project); //set project
//            else if (project instanceof WorkSlot)
//            setTimedProject(project); //set project
//        }
//        if (save) {
            saveMe();
        }
//        return nextTimedItem;
        return new GetNextItemStatus(nextTimedItem, false);
    }

    /**
    update Timer when time list changes, e.g. elements reordered (D&D), element completed/deleted/cancelled
     */
    DataChangedListener timedListChanged = (oldVal, newVal) -> {
        //test if calling list is the timed one!
        //if list changed
        //if currently tiimed element is changed (Completed/Cancelled/Deleted (soft OR hard for RepeatRule instances)), then update Actual and update to next timed task as usual
        //if currently timed element is still there, then no need to change
        //if currently timed element is NO LONGER in the list (moved, list updated, ...), then ??
        //examples: if TODAY list is updated with next days elements, then (don't stop timer on current element X, if you're timing sth it should continue), but msg "X no longer in list Y, please 
        //examples: if timed list is reordered, then refresh TimerScreen to get correct next task
        //examples: if currently time item X is moved to another list, then when timer is stopped on X: "X no longer in the timed list "Y", timer exits, please select list to be timed"
        //make it an option what to do if timed elt no longer in timed list: "Start timer from beginning of new list or Exit timer?"

    };
    DataChangedListener timedItemChanged = (oldVal, newVal) -> {
        //if currently tiimed element is changed (Completed/Cancelled/Deleted (soft OR hard for RepeatRule instances)), then update Actual and update to next timed task as usual

    };

//<editor-fold defaultstate="collapsed" desc="comment">
//    Item updateToNextTimerItemXXX(boolean update, boolean save) {
//        Item previousTimedItem = getTimedItemN(); //may return null on first call, in which case the very first subtask will be returned
//        Item nextTimedItem = null;
//        Item project = getTimedProject();
//        ItemList itemList = null;
//        do {
////            Item previousSubtask = previousTimedItem;
////            while (project != null && nextTimedItem == null) {
////                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //getNextLeafItem will only return valid subtasks (matching condition), or null
////                if (nextTimedItem == null) {
////                    project = null; //this project has no more leaf tasks so removed
////                    previousTimedItem = null; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll task the first task/subtask in the next tasks/project
////                }
////            }
//            if (project != null) {
//                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //getNextLeafItem will only return valid subtasks (matching condition), or null
//                if (nextTimedItem == null) {
//                    previousTimedItem = project; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll use project as the first task/subtask in the next tasks/project
//                    project = null; //this project has no more leaf tasks so removed
//                }
//            }
//
//            if (nextTimedItem == null) { //if no suitable subtask found in project, continue with next in list
//                itemList = getItemList();
//                if (itemList != null) {
//                    while (nextTimedItem == null && project == null && itemList != null) {
//                        nextTimedItem = (Item) itemList.getNextItemAfter(previousTimedItem, false); //if previousTimedItem==null, return first element! false=> UI: don't expect start from start of list when last one's past
//                        if (nextTimedItem != null) {
//                            previousTimedItem = null; //reset previous since we've now found the following item and don't want next iteration to search for an item after previous
//                            if (nextTimedItem.isProject()) {
//                                project = nextTimedItem; //set project
//                                nextTimedItem = null; //force to repeat do while to check if there's a suitable subtask
//                            } else { //else: not a project, so we'll see if it is valid in the while(isValidItemForTimer...)
//                                if (!TimerStack.isValidItemForTimer(nextTimedItem)) {
//                                    previousTimedItem = nextTimedItem;// get the next item *after* the nextTimeItem already found
//                                    nextTimedItem = null;
//                                }
//                            }
//                        } else {
//                            itemList = null; //no more elements in the list, stop the do while loop
//                        }
//                    }
//                }
//            }
////        } while ((nextTimedItem == null || !TimerStack.isValidItemForTimer(nextTimedItem)) && (project != null || itemList != null));
//        } while ((nextTimedItem == null) && (project != null || itemList != null));
//
//        if (update) {
//            setTimedItem(nextTimedItem);
//            setTimedProject(project); //set project
//        }
//        if (save) {
//            saveMe();
//        }
//        return nextTimedItem;
//    }
//</editor-fold>
    /**
     * returns next item in to start the timer on (either the Item itself, the
     * first subtasks if the Item is a Project, or from ItemList, or null if
     * there are no more.
    Different situations: 
    1) A single task is timed (==no ItemList, ==no Project/no subtasks)
    2) A list with multiple tasks/projects timed: 
        2a) A non-project task is timed => continue with next in list, 
        2b) a project is timed, if there's a following subtask, continue with that, otherwise with next task in list. 
    3) Only a Project is timed (==no ItemList): get next subtask (if any)
    4) A Task/project has had new subtasks added since? 
        4a) after the last timed subtasks => no pb, will be found
        4b) *before* the last timed subtask => cannot be distinguished from an earlier subtask that was skipped over
     */
    //        private void updateToNextTimerItem(Item.Condition condition) {
//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean updateAndSaveCurrentTimedItemXXX() {
//        if (isRunning()) {
//            stopTimer();
//            Item current = getTimedItemN();
//            addTimerElapsedTimeToItemActualEffort(current, getElapsedTime());
//            DAO.getInstance().saveInBackground(current);
//            return true;
//        }
//        return false;
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private Item updateToNextTimerItemXXX() {
    //        return updateToNextTimerItem(true);
    //    }
    //</editor-fold>
    public void setWasRunningWhenInterrupted(boolean interrupted, boolean save) {
        boolean wasInterrupted = isInterruptedWhileRunning();
//        setTimerPaused(paused);
        setInterruptedWhileRunning(interrupted);
        if (save && wasInterrupted != interrupted) {
            saveMe();
        }
    }

//    public void pauseTimerXXX(boolean paused) {
//        setWasRunningWhenInterrupted(paused, true);
//    }
    /**
    delete this timer when it's done
     */
    public void deleteInstance() {
        DAO.getInstance().deleteInBackground(this);
    }
}
