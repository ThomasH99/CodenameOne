/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
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
    //TODO save timer stack fo server, in background, and refresh
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
    final static String PARSE_AUTOSTART_TIMER = "autostart"; //automatically startTimer the timer on the next item 
    final static String PARSE_TIMER_PAUSED = "paused"; //timer is only paused (e.g. by interrupt) so shoud automatically restart when interrupt is over 
    final static String PARSE_TIMER_SHOWS_TOTAL_ACTUAL = "showTotal"; //should Timer show total time spend on task, or only time spend during this timing sessions?
    final static String PARSE_TIMER_AUTO_GOTO_NEXT_TASK = "autoNextTask"; //should Timer show total time spend on task, or only time spend during this timing sessions?

//    private Container timerContainer; //the container build for this timer instane, used to add to different forms as the use navigates
//    private boolean timerIsFullScreen; //the container build for this timer instane, used to add to different forms as the use navigates

    TimerInstance() { //used to instantiate internalized timers
        super(CLASS_NAME);
    }

//    TimerInstance(Item item, ItemList itemList, boolean autoStartTimer, boolean autoGotoNextTask) {
    TimerInstance(Item item, ItemList itemList) {
        super(CLASS_NAME);
//        setAutostart(autoStartTimer);
//        setAutoGotoNextTask(autoGotoNextTask);
        if (false) {
            setShowTotalActual(MyPrefs.timerShowTotalActualInTimer.getBoolean());
        }
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
        return (getTimedItem() != null ? "Task:" + getTimedItem().getText() : "")
                + (getTimedProject() != null ? " Proj:" + getTimedProject().getText() : "")
                + (getItemList() != null ? " List:" + getItemList().getText() : "")
                + ("; Start:" + getStartTimeD() + "; Duration:" + MyDate.formatTimeDuration(getElapsedTime()))
                + (isRunning() ? " Running" : " Stopped");
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

    /**
    set the new timed item and auto-start timer if autostart is true
    @param timedItem 
     */
    private void setTimedItem(Item timedItem) {
        if (false) {
            Item previousItem = getTimedItem();
            if (previousItem != null && isRunning()) {
                stopTimer(false); //don't save because saved once below
                addTimerElapsedTimeToItemActualEffort(previousItem, getElapsedTime());
                DAO.getInstance().saveInBackground(previousItem);
            }
        }
        if (timedItem != null) {
            put(PARSE_TIMED_ITEM, timedItem);
        } else {
            remove(PARSE_TIMED_ITEM);
        }
        if (false) {
            if (isAutostart()) {
                setStartTimeToNowAndStartNow();
//            startTimer(false);
            }
            saveMe();
//        if (previousItem != null && !previousItem.equals(timedItem)) {
//            setStartTime();
//        }
        }
    }

    public Item getTimedItem() {
        Item timedItem = (Item) getParseObject(PARSE_TIMED_ITEM);
//        if (timedItem != null) {
            timedItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(timedItem);
//        }
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

    private void setItemList(ItemList itemList) {
        if (itemList != null) {
            put(PARSE_LIST, itemList);
        } else {
            remove(PARSE_LIST);
        }
    }

    public ItemList getItemList() {
        ItemList itemList = (ItemList) getParseObject(PARSE_LIST);
//        if (itemList != null) {
            itemList = (ItemList) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(itemList);
//        }
        return itemList;
    }

    private final void setStartTime(Date start) {
        if ((start != null && start.getTime() != 0)) {
            put(PARSE_TIMER_START_TIME, start);
            if (false) {
                setShowTotalActual(MyPrefs.timerShowTotalActualInTimer.getBoolean()); //Capture if startTime is articially earlier 
            }
        } else {
            remove(PARSE_TIMER_START_TIME);
            if (false) {
                setShowTotalActual(false);
            }
        }
//        setElapsedTime(0);

    }

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
        setStartTime(new Date());
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

    public long getElapsedTime() {
//        long addPreviousActual = 0;
//        if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
//            addPreviousActual = getTimedItem().getActualEffortProjectTaskItself();
//        }
        if (isRunning()) {
            return System.currentTimeMillis() - getStartTime(); // + addPreviousActual;
        }
        Long startTime = getLong(PARSE_TIMER_ELAPSED_TIME);
        return ((startTime == null) ? 0L : startTime); // + addPreviousActual;
    }

    public long getElapsedTotalTime() {

        if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
            return getElapsedTime() + getTimedItem().getActualEffortProjectTaskItself();
        } else {
            return getElapsedTime();
        }
    }

    private final void setAutostart(boolean autostart) {
//        if (autostart) {
//            put(PARSE_AUTOSTART_TIMER, autostart);
//        } else {
//            remove(PARSE_AUTOSTART_TIMER);
//        }
    }

    public boolean isAutostart() {
//        Boolean autostart = getBoolean(PARSE_AUTOSTART_TIMER);
//        return (autostart == null) ? false : autostart;
        return MyPrefs.timerAutomaticallyStartTimer.getBoolean();
    }

    private final void setTimerPaused(boolean paused) {
        if (paused) {
            put(PARSE_TIMER_PAUSED, paused);
        } else {
            remove(PARSE_TIMER_PAUSED);
        }
    }

    public boolean wasInterruptedWhileRunning() {
        Boolean paused = getBoolean(PARSE_TIMER_PAUSED);
        return (paused == null) ? false : paused;
    }

    /**
    show actual affects the state so cannot be changed for an already started timer
    @param showTotalActual 
     */
    public void setShowTotalActual(boolean showTotalActual) {
        if (showTotalActual) {
            put(PARSE_TIMER_SHOWS_TOTAL_ACTUAL, showTotalActual);
        } else {
            remove(PARSE_TIMER_SHOWS_TOTAL_ACTUAL);
        }
    }

    public boolean isTimerShowActualTotal() {
        Boolean showTotalActual = getBoolean(PARSE_TIMER_SHOWS_TOTAL_ACTUAL);
        return (showTotalActual == null) ? false : showTotalActual;
    }

    final void setAutoGotoNextTask(boolean autoGotoNextTask) {
        if (autoGotoNextTask) {
            put(PARSE_TIMER_AUTO_GOTO_NEXT_TASK, autoGotoNextTask);
        } else {
            remove(PARSE_TIMER_AUTO_GOTO_NEXT_TASK);
        }
    }

    public boolean isAutoGotoNextTask() {
//        Boolean autoGotoNextTask = getBoolean(PARSE_TIMER_AUTO_GOTO_NEXT_TASK);
//        return (autoGotoNextTask == null) ? false : autoGotoNextTask;
        return MyPrefs.timerAutomaticallyGotoNextTask.getBoolean();
    }

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
    public long getTimeXXX() {
        if (getElapsedTime() > 0) {
            return getElapsedTime();
        } else {
            return System.currentTimeMillis() - getStartTime();
        }
    }

    /**
    return the time the timer has been running, whether currently running or paused
    @return 
     */
    public boolean isRunning() {
//        if (getStartTime() != 0) {
//            return true;
//        } else {
//            return false;
//        }
        return getStartTime() != 0;
    }

    public boolean isThereATaskToTime() {
        return getTimedItem() != null;
    }

//    private final Object TIMER_LOCK = new Object();
    public synchronized void startTimer(boolean save) {
//        if (getStartTimeD().getTime()==0){
        synchronized (TimerStack.TIMER_LOCK) {
            if (getStartTime() == 0) {
//            setStartTime(new Date(new Date().getTime() - getElapsedTime()));
//            setStartTime(new Date(new Date().getTime() - getElapsedTime()));
                setStartTime(System.currentTimeMillis() - getElapsedTime());
                setElapsedTime(0); //reset elapsed to 0 while timer is running
            }
            if (save) {
                saveMe(); //update server
            }
        }
    }

    /**
    start the timer. If it is already running, this has no effect
     */
    public void startTimer() {
        startTimer(true);
    }

    public void stopTimer(boolean save) {
        synchronized (TimerStack.TIMER_LOCK) {
            if (isRunning()) { //getStartTime() != 0) {
//            setElapsedTime(new Date().getTime() - new Date().getTime());
                setElapsedTime(System.currentTimeMillis() - getStartTime());
//                setStartTime(0);
            } else {
                setElapsedTime(0);
            }
            setStartTime(0);
            if (save) {
                saveMe(); //update server
            }
        }
    }

    /**
    stopTimer the timer if it is running, if not, no effect
     */
    public void stopTimer() {
        stopTimer(true);
    }

    public void setTimerStateXXX(boolean startTimer) {
        if (startTimer) {
            startTimer(true);
        } else {
            stopTimer(true);
        }
    }

    /**
    sets the sources of timed items, and will start the timer if defined
    @param item
    @param itemList 
     */
    private void setSources(Item item, ItemList itemList) {
        ASSERT.that(item != null || itemList != null);
        setItemList(itemList);

        if (item == null && itemList != null) {
            //get first item in list
            item = (Item) itemList.getNextItemAfter(null, true); //item may become null
        }

        if (item != null) {
            Item leafTask;
            if (item.isProject() && (leafTask = item.getNextLeafItem(null)) != null) { //if there's a leaf task pick that
                setTimedProject(item); //item is a project with valid subtasks
                setTimedItem(leafTask);
                return;
            } else {
                setTimedProject(null); //no project
                setTimedItem(item);
                return;
            }
        } else {
            setTimedItem(null); //no valid item found, item==null && itemList did not contain a valid task or project with valid leaf task
        }
//        save();
    }

    /**
     * returns true if the minimum Timer threshold for when to update status and
     * save time is passed
     *
     * @return
     */
    private boolean isMinimumThresholdPassed(long elapsedTime) {
//        return status.getStatus() != ItemStatus.CREATED //if status already other than created we store any time -NO, since this will also later create a timer Start/Stop recording
//                && timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.getInt(MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActuals) * MyDate.SECOND_IN_MILLISECONDS;
//        return timerStack.currEntry.getTimerDurationInMillis() >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
        return elapsedTime >= MyPrefs.timerMinimumTimeRequiredToSetTaskOngoingAndToUpdateActualsInSeconds.getInt() * MyDate.SECOND_IN_MILLISECONDS;
    }

    private void addTimerElapsedTimeToItemActualEffort(Item item, long actualEffort) {
        if (isMinimumThresholdPassed(actualEffort)) {
            item.setActualEffort(item.getActualEffortProjectTaskItself() + actualEffort);
        }
    }

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
//        Item previousTimedItem = getTimedItem();
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
    Item findNextItemXXX() {
        return updateToNextTimerItem(false, false);
    }

    Item updateToNextTimerItem(boolean update, boolean save) {
        Item previousTimedItem = getTimedItem(); //may return null on first call, in which case the very first subtask will be returned
        Item nextTimedItem = null;
        Item project = getTimedProject();
        ItemList itemList = null;
        do {
//            Item previousSubtask = previousTimedItem;
//            while (project != null && nextTimedItem == null) {
//                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //getNextLeafItem will only return valid subtasks (matching condition), or null
//                if (nextTimedItem == null) {
//                    project = null; //this project has no more leaf tasks so removed
//                    previousTimedItem = null; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll task the first task/subtask in the next tasks/project
//                }
//            }
            if (project != null) {
                nextTimedItem = project.getNextLeafItem(previousTimedItem, item -> TimerStack.isValidItemForTimer(item)); //getNextLeafItem will only return valid subtasks (matching condition), or null
                if (nextTimedItem == null) {
                    previousTimedItem = project; //the previousTimedTask *was* in the timedProject, but there were no more suitable subtasks, set previousTimedItem=null so we'll use project as the first task/subtask in the next tasks/project
                    project = null; //this project has no more leaf tasks so removed
                }
            }

            if (nextTimedItem == null) { //if no suitable subtask found in project, continue with next in list
                itemList = getItemList();
                if (itemList != null) {
                    while (nextTimedItem == null && project == null && itemList != null) {
                        nextTimedItem = (Item) itemList.getNextItemAfter(previousTimedItem, false); //if previousTimedItem==null, return first element! false=> UI: don't expect start from start of list when last one's past
                        if (nextTimedItem != null) {
                            previousTimedItem = null; //reset previous since we've now found the following item and don't want next iteration to search for an item after previous
                            if (nextTimedItem.isProject()) {
                                project = nextTimedItem; //set project
                                nextTimedItem = null; //force to repeat do while to check if there's a suitable subtask
                            } else { //else: not a project, so we'll see if it is valid in the while(isValidItemForTimer...)
                                if (!TimerStack.isValidItemForTimer(nextTimedItem)) {
                                    previousTimedItem = nextTimedItem;// get the next item *after* the nextTimeItem already found
                                    nextTimedItem = null;
                                }
                            }
                        } else {
                            itemList = null; //no more elements in the list, stop the do while loop
                        }
                    }
                }
            }
//        } while ((nextTimedItem == null || !TimerStack.isValidItemForTimer(nextTimedItem)) && (project != null || itemList != null));
        } while ((nextTimedItem == null) && (project != null || itemList != null));

        if (update) {
            setTimedItem(nextTimedItem);
            setTimedProject(project); //set project
        }
        if (save) {
            saveMe();
        }
        return nextTimedItem;
    }

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
    boolean
            updateAndSaveCurrentTimedItemXXX() {
        if (isRunning()) {
            stopTimer();
            Item current = getTimedItem();
            addTimerElapsedTimeToItemActualEffort(current, getElapsedTime());
            DAO.getInstance().saveInBackground(current);
            return true;
        }
        return false;
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private Item updateToNextTimerItemXXX() {
    //        return updateToNextTimerItem(true);
    //    }
    //</editor-fold>

    public void setWasInterruptedWhileRunning(boolean paused, boolean save) {
        boolean wasPaused = wasInterruptedWhileRunning();
        setTimerPaused(paused);
        if (save && wasPaused != paused) {
            saveMe();
        }
    }

    public void pauseTimerXXX(boolean paused) {
        setWasInterruptedWhileRunning(paused, true);
    }

    /**
    delete this timer when it's done
     */
    public void deleteInstance() {
        DAO.getInstance().delete(this);
    }
}
