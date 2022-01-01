/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.UITimer;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.Item.PARSE_OWNER_ITEM;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import static com.todocatalyst.todocatalyst.TimerStack2.getValidItemForTimerCondition;
import static com.todocatalyst.todocatalyst.TimerStack2.isValidItemForTimer;

/**
 * stores each timer (the 'stack' is defined by which is created(??) the latest)
 *
 * @author thomashjelm
 */
public class TimerInstance2 extends ParseObject implements ActionListener {
    //TODO re-read timer stack from server regularly to check (in particular) if a timer has been started on another device so to update UI
    //TODO save timer stack fo server, in background, and removeFromCache
    //TODO when starting app, check if timer stack is non-empty, and show if so (e.g. show little timer window at bottom of screen, even for paused timer)

    public static String CLASS_NAME = "Timer";

    final static String PARSE_PROJECT = "project";
    final static String PARSE_LIST = "list";
    final static String PARSE_CATEGORY = "category";
    final static String PARSE_TIMED_ITEM = "item";
    final static String PARSE_NEXT_TIMED_ITEM = "next";
    final static String PARSE_TIMER_START_TIME = "startTime"; //only != 0 when timer is actually running
    final static String PARSE_TIMER_ELAPSED_TIME = "elapsedMillis"; //only != 0 when timer is paused/stopped
    final static String PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED = "interrupted"; //timer was interrupted while running so should automatically restart when interrupt is over 
    final static String PARSE_TIMER_TIME_EVEN_INVALID_ITEMS_XXX = "timeInvalidTasks"; //time eg Done/Cancelled tasks, used when launching timer via leftSwipe directly on any tasks, to timer will continue on (equally) invalid subtasks etc
    final static String PARSE_TIMER_FULL_SCREEN_XXX = "fullScreen"; //timer was interrupted while running so should automatically restart when interrupt is over 
    final static String PARSE_TIMER_TASK_STATUS = Item.PARSE_STATUS; //timer was interrupted while running so should automatically restart when interrupt is over 
    final static String PARSE_TIMER_AUTO_NEXT = "autoNext";
    final static String PARSE_TIMER_AUTO_START = "autoStart";
    /**
     * the timerInstance has changed (start/stop or moved to new item)
     */
    final static int ACTION_EVENT_CHANGED = DataChangedListener.CHANGED;
    /**
     * no more items to time, so TimerInstance removed
     */
    final static int ACTION_EVENT_REMOVED = DataChangedListener.REMOVED;

    TimerInstance2() { //used to instantiate internalized timers
        super(CLASS_NAME);
    }

//    TimerInstance(Item item, ItemList itemList, boolean autoStartTimer, boolean autoGotoNextTask) {
    /**
     *
     * @param itemOrProjectN either the item or project on which the timer is
     * directly started (eg swipe button), or the first suitable item in the
     * itemList (selected *before* creating the timer)
     * @param itemListOrCategoryN can be null if Timer started on a single item,
     * like an interrupt
     * @param timeEvenInvalidItem
     */
//    TimerInstance2(Item itemOrProject, ItemList itemListOrCategoryN, boolean timeEvenInvalidItem) {
    TimerInstance2(Item itemOrProjectN, ItemAndListCommonInterface itemListOrCategoryN) {
        this();
//<editor-fold defaultstate="collapsed" desc="comment">
//        setTimeEvenInvalidItemOrProjects(timeEvenInvalidItem);
//        TimerInstance2.this.setTimerSource(itemListOrCategoryN, false, false);
//        ASSERT.that (itemOrProjectN != null||itemListOrCategoryN!=null,"not both item and source can be null!");
//
//        if (itemOrProjectN == null) { //only source is defined
//            setTimerSource(itemListOrCategoryN, true); //even if started directly on project, use first appropriate subtask
//        } else { //item defined
//            if (itemListOrCategoryN == null) { //no source
//                if (itemOrProjectN.isProject()) {
//                    setTimerSource(itemOrProjectN, true); //even if started directly on project, use first appropriate subtask
//                } else { //item NOT a project
//                    setTimedItems(itemOrProjectN, false); //even if started directly on project, use first appropriate subtask
//                }
//            } else { //source defined
//                if (itemOrProjectN.isProject()) {
//                    setTimerSource(itemOrProjectN, true); //even if started directly on project, use first appropriate subtask
//                } else { //item NOT a project
//                    setTimerSource(itemListOrCategoryN, false); //even if started directly on project, use first appropriate subtask
//                    setTimedItems(itemOrProjectN, true); //even if started directly on project, use first appropriate subtask
//                }
//            }
//        }
//</editor-fold>
        setup(itemOrProjectN, itemListOrCategoryN);
        setAutoGotoNextTask(MyPrefs.timerAutomaticallyGotoNextTask.getBoolean()); //use setting as default value
        setAutoStartTimer(MyPrefs.timerAutomaticallyStartTimer.getBoolean());
        ASSERT.that(itemOrProjectN != null, "Timer started on null item, itemListOrCategoryN.guid=" + (itemListOrCategoryN != null ? itemListOrCategoryN.getGuid() : "<null>")); //A timer must not be created if there is no item to time
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            if (itemOrProject != null) { //        setTimedItem(itemOrProject);
//                List<Item> projectLeafTasks = itemOrProject.getLeafTasksAsListN((item) -> isValidItemForTimer(item)); //will return item itself or first subtask if a project
////            setTimedItem(itemOrProject);
//                setTimedItem(projectLeafTasks.get(0));
//            } else { //itemOrProject == null
////            List<Item> projectLeafTasks = itemListOrCategoryN.getLeafTasksAsList(false, (item) -> isValidItemForTimer(item));
//                List<Item> projectLeafTasks = itemListOrCategoryN.getLeafTasksAsListN((item) -> isValidItemForTimer(item));
//                if (projectLeafTasks != null && projectLeafTasks.size() >= 1) { //        setTimedItem(itemOrProject);
//                    setTimedItem(projectLeafTasks.get(0));
//                } else {
//                    ASSERT.that("Timer started on null item and no suitable item in itemListOrCategoryN=" + (itemListOrCategoryN != null ? itemListOrCategoryN.toString() : "<null>")); //A timer must not be created if there is no item to time
//                }
//            }
//        }
//        setTimedItem(findTimedItemNXXX(itemOrProject, itemListOrCategoryN));
//        if (itemOrProject != null) {
////            List<Item> projectLeafTasks = itemOrProject.getLeafTasksAsListN((item) -> isValidItemForTimer(item)); //will return item itself or first subtask if a project
//            List<Item> projectLeafTasks = itemOrProject.getLeafTasksForTimerN(); //will return item itself or first subtask if a project
//            setTimedItem(projectLeafTasks.get(0), true); //true: updates nextComing
//        } else { //initialize timedItem (and nextItem) from source if not defined
//            goToNextTimerItemNEW(false);
//        }
//        updateNEW2(false, false, false, false);
//</editor-fold>
        updateNEW2(false); //will also start Timer if autoStart is true
    }

    private UITimer reloadTimersFromParseServer; //used to determine how often to check if the timer state on the Parse Server has changed (e.g. 
    private EventDispatcher listeners;
    private boolean ignoreItemSourceEvents;

    /**
     * will ignore callbacks until the timerInstance has finished updating and
     * sends its own change event, used to avoid unnecessary updates due to
     * changes done by TimerInstance itself to the timerInstance elements
     * (timedItem, nextItem, source)
     *
     * @param ignore
     */
    private void setIgnoreItemSourceEvents(boolean ignore) {
        ignoreItemSourceEvents = ignore;
    }

    /**
     * only one timer can listen at a time
     *
     * @param obj
     */
    public void setActionListener(ActionListener obj) {
        ASSERT.that(obj instanceof TimerStack2);
        if (listeners == null) {
            listeners = new EventDispatcher();
        } else if (listeners.hasListeners()) { //remove any previous listeners
            Collection<ActionListener> oldListeners = new ArrayList((List<ActionListener>) listeners.getListenerCollection());
//            for (ActionListener l : (List<ActionListener>) listeners.getListenerCollection()) {
            for (ActionListener l : oldListeners) {
//            Iterator<ActionListener> it = listeners.getListenerCollection().iterator();
//                listeners.removeListener(l);
//            while (it.hasNext()) {
//                listeners.removeListener(it.next());
                listeners.removeListener(l);
            }
        }
        listeners.addListener((ActionListener) (e) -> {
            if (!ignoreItemSourceEvents) {
                obj.actionPerformed(e);
            }
        });
    }

    public void removeActionListener(ActionListener obj) {
        if (listeners != null) {
            listeners.removeListener(obj);
        }
    }

    private void fireChangedEvent() {
        if (ignoreItemSourceEvents) {
            return;
        }
        if (listeners != null) {
            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_CHANGED));
            ignoreItemSourceEvents = false;

        } else {
            if (false) {
                ASSERT.that("TimerStack2.fireChangedEvent, but NO listeners!!");
            }
        }
    }

    private void fireDeletedEvent() {
        if (listeners != null) {
            listeners.fireActionEvent(new ActionEvent(this, ACTION_EVENT_REMOVED));
            ignoreItemSourceEvents = false;
        } else {
            ASSERT.that("TimerStack2.fireDeletedEvent, but NO listeners!!");
        }
    }

    public String toString() {
//        return ((getTimedItemListN() != null ? " List:" + getTimedItemListN().getText() : "")
//                + (getTimedProject() != null ? "; Proj:" + getTimedProject().getText() : "")
//                + (getTimedItemN() != null ? "; Task:" + getTimedItemN().getText() : "")
//                + (getStartTimeD().getTime() != 0 ? "; Start:" + getStartTimeD() : "")
//                + (getElapsedTime() != 0 ? "; Duration:" + MyDate.formatDurationShort(getElapsedTime()) : "")
//                + (isRunning() ? "; Running" : "; Stopped")
//                + (isFullScreen() ? "; BIG" : "; SMALL")
//                + " [" + getObjectIdP() + "]");
////                +(isAutostart());
        return ((getTimerSourceN() != null ? (ItemAndListCommonInterface.getTypeString(getTimerSourceN()) + ": " + getTimerSourceN().getText()) : "")
                //                + (getTimedProject() != null ? "; Proj:" + getTimedProject().getText() : "")
                + ("; Task:" + (getTimedItemN() != null ? getTimedItemN().getText() : "<null>"))
                + ("; Next:" + (getNextTimedItemN() != null ? getNextTimedItemN().getText() : "<null>"))
                + (isRunning() ? "; Run" : "; Stop")
                + (getStartTimeD().getTime() != 0 ? "; Start:" + getStartTimeD() : "")
                + (getElapsedTime() != 0 ? "; Dur:" + MyDate.formatDurationShort(getElapsedTime()) : "")
                //                + (isFullScreen() ? "; BIG" : "; SMALL")
                + " [" + getObjectIdP() != null ? getObjectIdP() : "null" + "]");
//                +(isAutostart());
    }

//    private List<ScreenTimer2> timers = new ArrayList();
//    @Override
//    public void saveMe() {
//        xxx; //check when saveMe is called, always save timed Item also!
//        if (false) {
//            DAO.getInstance().saveTimerInstanceInBackground(this);
//        } else {
//            DAO.getInstance().saveToParseLater(this);
//        }
//    }
    /**
     * set the new timed item, reset start/elapsed time for new items.
     *
     * @param timedItem
     */
    public void setTimedItemInParse(Item timedItem) {
//        setTimedItem(timedItem, false);
        Item previousItem = getTimedItemN();
        if (timedItem != null && !Objects.equals(timedItem, previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            put(PARSE_TIMED_ITEM, timedItem);
        } else {
            remove(PARSE_TIMED_ITEM);
        }
    }

//    public void setTimedItem(Item timedItem, boolean updateNextComingItem) {
//        setTimedItem(timedItem, updateNextComingItem, true);
//    }
//
//    public void setTimedItem(Item timedItem, boolean updateNextComingItem, boolean fireChangeEvent) {
//
//    }
//    public void setTimedItemXX(Item timedItem, boolean updateNextComingItem, boolean fireChangeEvent, boolean wrapAround) {
//        Item previousItem = getTimedItemN();
//        ASSERT.that(previousItem == null || (!isRunning() && !previousItem.isDirty()), () -> "setting a new timedItem, but timer still running for previous timed item (not saved)" + previousItem);
////        if ((timedItem != null && !timedItem.equals(previousItem))                ||) {
////        if (!Objects.equals(timedItem, previousItem)) {
//        //definition from Objects.equals() => return (a == b) || (a != null && a.equals(b))
////        if (!((timedItem == previousItem) || timedItem != null && timedItem.equals(previousItem))) { //do not update if timedItem is the same as before (including if was null before and after) changed
////        if (!(timedItem == previousItem || timedItem != null && timedItem == previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
//        if (!Objects.equals(timedItem, previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
//            //reset both when changing timedItem
//            if (false) { //will stop the timer if previously started
//                setStartTime(0);
//                setElapsedTime(0);
//            }
//            if (timedItem != null) {
//                if (previousItem != null && !Objects.equals(previousItem, getNextTimedItemN())) {
//                    previousItem.removeActionListener(this);
//                }
//                timedItem.addActionListener(this);
//                put(PARSE_TIMED_ITEM, timedItem);
//                setItemStatus(timedItem.getStatus());
//            } else {
//                if (previousItem != null && !Objects.equals(previousItem, getNextTimedItemN())) {
//                    previousItem.removeActionListener(this);
//                }
//                setItemStatus(null);
//                remove(PARSE_TIMED_ITEM);
//            }
//
//            if (updateNextComingItem) {
//                setNextTimedItem(findNextTimerItem(timedItem, getTimerSourceN(), wrapAround));
//            }
//
//            if (fireChangeEvent) {
//                fireChangedEvent();
//            }
//        }
//    }
//    public void setTimedItems(Item timedItem, Item nextComingItem, boolean fireChangeEvent) {
    public void setTimedItems(Item timedItem, Item nextTimedItem) {
        Item previousItem = getTimedItemN();
//        ASSERT.that(previousItem == null || (!isRunning() && !previousItem.isDirty()), () -> "setting a new timedItem ("+nextTimedItem+"), but timer still running for previous timed item (not saved)" + previousItem);
        ASSERT.that(previousItem == null || (!isRunning()), () -> "setting a new timedItem (" + nextTimedItem + "), but timer still running for previous timed item=" + previousItem);
        ASSERT.that(nextTimedItem == null || timedItem != null, "cannot have nextTimedItem!=null (" + nextTimedItem + ") if timedItem==null");
//        if ((timedItem != null && !timedItem.equals(previousItem))                ||) {
//        if (!Objects.equals(timedItem, previousItem)) {
        //definition from Objects.equals() => return (a == b) || (a != null && a.equals(b))
//        if (!((timedItem == previousItem) || timedItem != null && timedItem.equals(previousItem))) { //do not update if timedItem is the same as before (including if was null before and after) changed
//        if (!(timedItem == previousItem || timedItem != null && timedItem == previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
        if (!Objects.equals(timedItem, previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            //reset both when changing timedItem
            if (false) { //NO/false since will stop the timer if previously started
                setStartTime(0);
                setElapsedTime(0);
            }
            if (timedItem != null) {
//                if (previousItem != null && !Objects.equals(previousItem, getNextTimedItemN())) {
//                    previousItem.removeActionListener(this);
//                }
                timedItem.addActionListener(this);
                put(PARSE_TIMED_ITEM, timedItem);
                setItemStatus(timedItem.getStatus());
            } else {
//                if (previousItem != null && !Objects.equals(previousItem, getNextTimedItemN())) {
//                    previousItem.removeActionListener(this);
//                }
                setItemStatus(null);
                remove(PARSE_TIMED_ITEM);
            }
            if (previousItem != null) {
                previousItem.removeActionListener(this);
            }
        }

//            setNextTimedItem(nextTimedItem);
        Item previousNextItem = getNextTimedItemN();

        if (!Objects.equals(nextTimedItem, previousNextItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            if (nextTimedItem != null) {
                put(PARSE_NEXT_TIMED_ITEM, nextTimedItem);
//                    if (previousNextItem != null && !Objects.equals(previousNextItem, timedItem)) { //previousItem may be the same as the (just set) timedItem, in which case we do NOT want to remove this TimerInstance as a listener
//                        previousNextItem.removeActionListener(this);
//                    }
                nextTimedItem.addActionListener(this);
            } else {
                remove(PARSE_NEXT_TIMED_ITEM);
            }
            if (previousNextItem != null && !Objects.equals(previousNextItem, timedItem)) { //NB! Don't remove listener if previous item is new timedItem!
                previousNextItem.removeActionListener(this);
            }
        }
    }

    public void setTimedItems(Item timedItem, boolean setNextTimedItemFromSource) {
        Item nextItem = null;
        if (setNextTimedItemFromSource) {
            ItemAndListCommonInterface source = getTimerSourceN();
            if (source != null) {
                List leafs = source.getLeafTasksForTimerN();
                nextItem = findNextTimerItem(timedItem, leafs, isWrapAround());
            }
        }
        setTimedItems(timedItem, nextItem);
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
     * don't call directly, go via timerStack to ensure the available item is
     * returned
     *
     * @return
     */
    Item getTimedItemN() {
        return getTimedItemN(false);
    }

    Item getTimedItemN(boolean doNotTestForMissingListener) {
        Item timedItem = (Item) getParseObject(PARSE_TIMED_ITEM);
        if (timedItem != null) {
            timedItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(timedItem);
        }
//        timedItem.addActionListener(this); //add when timedItem is fetched from Parse/DAO
        if (!doNotTestForMissingListener && Config.TEST) {
            ASSERT.that(timedItem == null || (timedItem.listeners != null && timedItem.listeners.hasListeners()), "Nolistener assigned f timedItem=" + (timedItem != null ? timedItem : "<null>"));
        }
        return timedItem;
    }

    /**
     * set the next item to time, necessary since the currently timed item may
     * disappear (eg be filtered) in the list before the timer is stopped,
     * making it impossible to find the next item as the next in the list after
     * the currently timed one
     *
     * @param nextTimedItem
     */
//    private void setNextTimedItemOLD(Item nextTimedItem) {
//        Item previousItem = getNextTimedItemN();
//        if (!Objects.equals(nextTimedItem, previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
//            if (nextTimedItem != null) {
//                put(PARSE_NEXT_TIMED_ITEM, nextTimedItem);
//                if (previousItem != null && !Objects.equals(previousItem, getTimedItemN())) { //previousItem may be the same as the (just set) timedItem, in which case we do NOT want to remove this TimerInstance as a listener
//                    previousItem.removeActionListener(this);
//                }
//                nextTimedItem.addActionListener(this);
////                setItemStatus(nextTimedItem.getStatus());
//            } else {
//                remove(PARSE_NEXT_TIMED_ITEM);
//                if (previousItem != null && !Objects.equals(previousItem, getTimedItemN())) {
//                    previousItem.removeActionListener(this);
//                }
////                setItemStatus(null);
//            }
//        }
//    }
    public void setNextTimedItemInParse(Item nextTimedItem) {
        Item previousItem = getNextTimedItemN();
        if (nextTimedItem != null && !Objects.equals(nextTimedItem, previousItem)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            put(PARSE_NEXT_TIMED_ITEM, nextTimedItem);
        } else {
            remove(PARSE_NEXT_TIMED_ITEM);
        }
    }

    /**
     * don't call directly, go via timerStack to ensure the available item is
     * returned
     *
     * @return
     */
    Item getNextTimedItemN() {
        Item timedItem = (Item) getParseObject(PARSE_NEXT_TIMED_ITEM);
        if (timedItem != null) {
            timedItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(timedItem);
        }
        return timedItem;
    }

    public void setFullScreenXXX(boolean fullScreen) {
        if (fullScreen) {
            put(PARSE_TIMER_FULL_SCREEN_XXX, true);
        } else {
            remove(PARSE_TIMER_FULL_SCREEN_XXX);
        }
    }

    public boolean isFullScreen() {
        Boolean fullScreen = getBoolean(PARSE_TIMER_FULL_SCREEN_XXX);
        return fullScreen != null;
    }

    /**
     * set new source (and stop timer if running for another source)
     *
     * @param timerSourceN
     */
//    public void setTimerSourceNXXX(ItemAndListCommonInterface timerSourceN) {
//        setTimerSource(timerSourceN, false, true, false);
//    }
    /**
     * set timersource, find and set first element in source, and fire a
     * changedEvent
     *
     * @param timerSourceN
     * @param fireChangedEvent
     */
//    public boolean setTimerSource(ItemAndListCommonInterface timerSourceN, boolean fireChangedEvent) {
//        return setTimerSource(timerSourceN, false, fireChangedEvent);
//    }
//    public boolean setTimerSource(ItemAndListCommonInterface timerSourceN, boolean setTimedItem, boolean fireChangedEvent) {
//        return setTimerSource(timerSourceN, setTimedItem, fireChangedEvent, false);
//    }
    public void setTimerSourceInParse(ItemAndListCommonInterface timerSourceN) {
        ItemAndListCommonInterface previousSource = getTimerSourceN();
        if (timerSourceN != null && !Objects.equals(timerSourceN, previousSource)) { //do not update if timedItem is the same as before (including if was null before and after) changed
            if (timerSourceN instanceof Item) {
                put(PARSE_PROJECT, timerSourceN);
                remove(PARSE_CATEGORY);
                remove(PARSE_LIST);
            } else if (timerSourceN instanceof Category) {
                put(PARSE_CATEGORY, timerSourceN);
                remove(PARSE_PROJECT);
                remove(PARSE_LIST);
            } else if (timerSourceN instanceof ItemList) {
                put(PARSE_LIST, timerSourceN);
                remove(PARSE_PROJECT);
                remove(PARSE_CATEGORY);
            }
        } else { //if null
            ASSERT.that(timerSourceN == null);
            remove(PARSE_PROJECT);
            remove(PARSE_LIST);
            remove(PARSE_CATEGORY);
        }
    }

//    public boolean setTimerSource(ItemAndListCommonInterface timerSourceN, boolean setTimedItem, boolean fireChangedEvent, boolean saveTimerInstance) {
    public boolean setTimerSource(ItemAndListCommonInterface timerSourceN, boolean setTimedItem) {
        ItemAndListCommonInterface previousSource = getTimerSourceN();
        if (!Objects.equals(timerSourceN, previousSource)) { //do not update if timedItem is the same as before (including if was null before and after) changed
//            if (isRunning()) { //stop previous timer
//                stopTimer(false,true);
//            }
            if (false) {
                stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save currently timed item (if any) but not timerInstance
            }
            if (timerSourceN instanceof Item) {
                put(PARSE_PROJECT, timerSourceN);
                remove(PARSE_LIST);
                remove(PARSE_CATEGORY);
            } else if (timerSourceN instanceof Category) {
                remove(PARSE_PROJECT);
                remove(PARSE_LIST);
                put(PARSE_CATEGORY, timerSourceN);
            } else if (timerSourceN instanceof ItemList) {
                remove(PARSE_PROJECT);
                put(PARSE_LIST, timerSourceN);
                remove(PARSE_CATEGORY);
            } else { //if null
                ASSERT.that(timerSourceN == null);
                remove(PARSE_PROJECT);
                remove(PARSE_LIST);
                remove(PARSE_CATEGORY);
            }

            if (previousSource != null) {
                previousSource.removeActionListener(this);
            }
            if (timerSourceN != null) {
                timerSourceN.addActionListener(this);
            }

            if (setTimedItem) {
//                setTimedItem(findTimedItemN(null, timerSourceN));
//                goToNextTimerItemNEW(false);
//                updateNEW2(false, false, false, false);
                updateNEW2(false);
            }

//            if (false && fireChangedEvent) {
//                fireChangedEvent();
//            }
            return true;
        }
        return false;
    }

    /**
     * source can be list, category, project(!)
     *
     * @return
     */
    public ItemAndListCommonInterface getTimerSourceN() {
        ItemAndListCommonInterface timerSourceN = (ItemAndListCommonInterface) getParseObject(PARSE_PROJECT);
        if (timerSourceN == null) {
            timerSourceN = (ItemAndListCommonInterface) getParseObject(PARSE_LIST);
        }
        if (timerSourceN == null) {
            timerSourceN = (ItemAndListCommonInterface) getParseObject(PARSE_CATEGORY);
        }
        return (ItemAndListCommonInterface) DAO.getInstance().fetchIfNeededReturnCachedIfAvail((ParseObject) timerSourceN); //returns null if timerSource is null
    }

//    private void setTimeEvenInvalidItemOrProjectsXXX(boolean timeEvenInvalidItemsOrProjects) {
//        if (timeEvenInvalidItemsOrProjects) {
//            put(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS, timeEvenInvalidItemsOrProjects);
//        } else {
//            remove(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS);
//        }
//    }
//
//    public boolean isTimeEvenInvalidItemOrProjectsXXX() {
//        Boolean timeEvenInvalidItemsOrProjects = getBoolean(PARSE_TIMER_TIME_EVEN_INVALID_ITEMS);
////        if (i
//        return timeEvenInvalidItemsOrProjects == null ? false : true;
//    }
    private final void setStartTime(Date start) {
        if ((start != null && start.getTime() != 0)) {
            put(PARSE_TIMER_START_TIME, start);
        } else {
            remove(PARSE_TIMER_START_TIME);
        }

    }

    /**
     * when startTime != 0, it means the timer is running, if ==0, then
     * elapsesTime indicates time elapsed (0 if new timer, >0 if paused timer)
     *
     * @param start
     */
    private final void setStartTime(long start) {
//        }
        setStartTime(new MyDate(start));
    }

    private final void setStartTimeToNowAndStartNow() {
        setStartTime(new MyDate());
    }

    public Date getStartTimeD() {
        Date startTime = getDate(PARSE_TIMER_START_TIME);
        return (startTime == null) ? new MyDate(0) : startTime;
    }

    public long getStartTime() {
        return getStartTimeD().getTime();
    }

    public final void setElapsedTime(long timerDuration) {
        if (timerDuration != 0) {
            put(PARSE_TIMER_ELAPSED_TIME, timerDuration);
        } else {
            remove(PARSE_TIMER_ELAPSED_TIME);
        }
    }

    /**
     * updates elapsed time after the user editing it, will either update
     * startTime if currently running, or elapsed time if stopped/paused
     *
     * @param newElapsedTime
     */
    public final void updateElapsedTime(long newElapsedTime) {
        if (isRunning()) {
            setStartTime(MyDate.currentTimeMillis() - newElapsedTime); // + addPreviousActual;
        } else {
            setElapsedTime(newElapsedTime);
        }
    }

    /**
     * elapsed time in milliseconds, this is the actual time the timer has been
     * running in this timing session (previous Actual values NOT added - that
     * is done in getElapsedTotalTime())
     *
     * @return
     */
    public long getElapsedTime() {
        long duration;
        if (isRunning()) {
            duration = MyDate.currentTimeMillis() - getStartTime(); // + addPreviousActual;
        } else {
            Long elapsedTime = getLong(PARSE_TIMER_ELAPSED_TIME);
            duration = ((elapsedTime == null) ? 0L : elapsedTime); // + addPreviousActual;
        }
        if (duration < 0) {
            duration = Long.MAX_VALUE;
        }
        return duration;
    }

    /**
     * this is the total Actual time (current timer elapsed + any previous
     * Actual recorded for the task)
     *
     * @return
     */
    public long getElapsedTotalTime() {
//        return getElapsedTime() + (getTimedItemN() != null ? getTimedItemN().getActualForTaskItself() : 0); //optimization!!! getTimedItem() will load item from cache!
        return getElapsedTime() + (getTimedItemN() != null ? getTimedItemN().getActualTotal() : 0); //getActualTotal() to ensure even projects with subtasks with registered time are set ongoing
    }

    /**
     * value to show in timer (total or just elapsed this timing session
     * depending on setting)
     *
     * @return
     */
    public long getElapsedTimeToDisplay() {
        if (MyPrefs.timerShowTotalActualInTimer.getBoolean()) {
            return getElapsedTotalTime(); //optimization!!! getTimedItem() will load item from cache!
        } else {
            return getElapsedTime();
        }
    }

    private final void setInterruptedWhileRunning(boolean interrupted) {
        if (interrupted) {
            put(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED, interrupted);
        } else {
            remove(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED);
        }
    }

    public boolean isWasInterruptedWhileRunning() {
        Boolean paused = getBoolean(PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED);
        return (paused == null) ? false : paused;
    }

    /**
     * return tru, if no value has been set, return default setting
     *
     * @return
     */
    public boolean isAutoGotoNextTask() {
        Boolean autoNext = getBoolean(PARSE_TIMER_AUTO_NEXT);
        if (autoNext == null) {
            return MyPrefs.timerAutomaticallyGotoNextTask.getBoolean(); //when nothing's set use default)
        } else {
            return autoNext;
        }
    }

    public void setAutoGotoNextTask(boolean autoNext) {
        Boolean oldAutoNext = getBoolean(PARSE_TIMER_AUTO_NEXT);
        if (oldAutoNext == null || autoNext != oldAutoNext) {
//            if (autoNext) {
//                put(PARSE_TIMER_AUTO_NEXT, autoNext);
//            } else {
//                remove(PARSE_TIMER_AUTO_NEXT);
//            }
            put(PARSE_TIMER_AUTO_NEXT, autoNext); //always set value explicitly (avoid null <=> false)
        }
    }

    public boolean isAutoStartTimer() {
        Boolean autoStart = getBoolean(PARSE_TIMER_AUTO_START);
        if (autoStart == null) {
            return false;//MyPrefs.timerAutomaticallyStartTimer.getBoolean(); //w?
        } else {
            return autoStart;
        }
    }

    public void setAutoStartTimer(boolean autoStart) {
        Boolean oldAutoStart = getBoolean(PARSE_TIMER_AUTO_START);
        if (oldAutoStart == null || autoStart != oldAutoStart) {
            put(PARSE_TIMER_AUTO_START, autoStart); //always set value explicitly (avoid null <=> false)
        }
    }

    /**
     * return the time the timer has been running, whether currently running or
     * paused
     *
     * @return
     */
    public boolean isRunning() {
        return getStartTime() != 0;
    }

    /**
     * start the timer (if paused, otherwise no effect) and add a timestamp to
     * the task
     *
     * @param save
     */
    public synchronized void startTimer(boolean saveTimerInstanceXXX, boolean saveTimedItemXXX) {
        synchronized (TimerStack2.TIMER_LOCK) { //TODO! is it (still/really) necessary to lock?
            Item timedItemN = getTimedItemN();
            if (!isRunning() && timedItemN != null) { //do nothing if already running
//                long now = MyDate.currentTimeMillis();
                MyDate now = new MyDate();
                setStartTime(now.getTime() - getElapsedTime()); //set a 'virtual' start time elapsed seconds before 'now'
                setElapsedTime(0); //reset elapsed to 0 while timer is running NB! Do *after* setStartTime since elapsed time is used there
                ASSERT.that(timedItemN != null);
                timedItemN.addTimerStartTimestamp(now); //use same now to ensure exact same time results
//                DAO.getInstance().saveToParseNow(saveTimerInstance, this, saveTimedItem, timedItemN);
                DAO.getInstance().saveToParseLater(this, timedItemN);
            }
        }
    }

    /**
     * stop the timer (if running, otherwise no effect) and add a timestamp to
     * the task.
     */
    public void pauseTimer(boolean saveTimerInstanceXXX, boolean saveTimedItemXXX) {
        synchronized (TimerStack2.TIMER_LOCK) {
            Item timedItemN = getTimedItemN();
            if (Config.TEST) {
                ASSERT.that(timedItemN != null || !isRunning(), "Timer is running but no timed item! start=" + getStartTimeD());
            }
            if (timedItemN != null && isRunning()) { //getStartTime() != 0) {
                MyDate now = new MyDate();
                setElapsedTime(now.getTime() - getStartTime());
                setStartTime(0);
                timedItemN.addTimerStopTimestamp(now);
//                ASSERT.that(timedItemN != null);
//                DAO.getInstance().saveToParseNow(saveTimerInstance, this, saveTimedItem, timedItemN);
                DAO.getInstance().saveToParseLater(this, timedItemN);
            }
        }
    }

//    public void stopTimerXXX() {
////        synchronized (TimerStack.TIMER_LOCK) {
////            if (isRunning()) {
////                setElapsedTime(MyDate.currentTimeMillis() - getStartTime());
////                setStartTime(0);
////                Item timedItem = getTimedItemN();
////                ASSERT.that(timedItem != null);
////                timedItem.addTimerStopTimestamp();
////            }
////        }
//        stopTimerXXX(true, true);
//    }
    static boolean showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(Item item, ItemAndListCommonInterface projectOrItemList) {
        String typeStr = ItemAndListCommonInterface.getTypeString(projectOrItemList).toLowerCase();
        return Dialog.show("Timer", "The timed task \"" + item.getText()
                + "\" is no longer in the " + typeStr + " \"" + projectOrItemList.getText()
                + "\". \n\n Please restart the Timer", null, "OK"); //OK/Cancel inversed to ensure OK exits timer
//        if (projectOrItemList instanceof ItemList) {
//            return Dialog.show("Timer", "The timed task \"" + item.getText()
//                    + "\" is no longer in the list \"" + projectOrItemList.getText()
//                    //                    + "\". \n\n Start over from the start of the list or stop timer?", "Start over", "Stop Timer");
//                    + "\". \n\n Please restart the Timer", null, "OK"); //OK/Cancel inversed to ensure OK exits timer
//        } else { //if (projectOrItemList instanceof Item)
//            ASSERT.that(projectOrItemList instanceof Item, "projectOrItemList NOT instance of Item, projectOrItemList=" + projectOrItemList);
//            return Dialog.show("Timer", "The timed task \"" + item.getText()
//                    + "\" is no longer in the project \"" + projectOrItemList.getText()
//                    //                    + "\". \n\n Start over from the start of the project or stop Timer?", "Start over", "Stop Timer");
//                    + "\". \n\n Please restart the Timer", null, "OK");
//        }
    }

    /**
     * find the appropriate item to time, either the itemOrProjectN itself if a
     * leaf task, or the first leaf task if a project, or the first leaf task in
     * itemListOrCategoryN
     *
     * @param itemOrProjectN
     * @param itemListOrCategoryN
     * @return the item to time or null if none found
     */
    private static Item findTimedItemNXXX(Item itemOrProjectN, ItemAndListCommonInterface itemListOrCategoryN) {
        Item newTimedItem;
        if (itemOrProjectN != null) { //        setTimedItem(itemOrProject);
//            List<Item> projectLeafTasks = itemOrProjectN.getLeafTasksAsListN((item) -> isValidItemForTimer(item)); //will return item itself or first subtask if a project
            List<Item> projectLeafTasks = itemOrProjectN.getLeafTasksForTimerN(); //will return item itself or first subtask if a project
//            setTimedItem(itemOrProject);
//            setTimedItem(projectLeafTasks.get(0));
            newTimedItem = projectLeafTasks.get(0);
        } else if (itemListOrCategoryN != null) { //itemOrProject == null
//            List<Item> projectLeafTasks = itemListOrCategoryN.getLeafTasksAsList(false, (item) -> isValidItemForTimer(item));
//            List<Item> projectLeafTasksN = itemListOrCategoryN.getLeafTasksAsListN((item) -> isValidItemForTimer(item));
            List<Item> projectLeafTasksN = itemListOrCategoryN.getLeafTasksForTimerN();
            if (projectLeafTasksN != null && projectLeafTasksN.size() >= 1) { //        setTimedItem(itemOrProject);
//                setTimedItem(projectLeafTasks.get(0));
                newTimedItem = projectLeafTasksN.get(0);
            } else {
                ASSERT.that("Timer started on null item and no suitable item in itemListOrCategoryN=" + (itemListOrCategoryN != null ? itemListOrCategoryN.toString() : "<null>")); //A timer must not be created if there is no item to time
                newTimedItem = null;
            }
        } else {
            newTimedItem = null;
        }
        return newTimedItem;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * sets the sources of timed items, and find the first valid start the timer
     * if defined
     *
     * @param itemOrProject first item to to time - if not valid, and not
     * launched specifically
     * @param itemList
     */
//    private void setSourcesXXX(Item itemOrProject, ItemList itemList) {
//        ASSERT.that(itemOrProject != null || itemList != null);
//        setItemListXXX(itemList);
//        if (itemOrProject != null && itemOrProject.isProject()) {
//            setTimedProjectXXX(itemOrProject);
//        } else {
//            setTimedItem(itemOrProject);
//        }
//        if (itemOrProject == null || itemOrProject.isProject()) { //don't call updateToNext if only one task
////            updateToNextTimerItem(true, false); //initialize to first element to time (itemOrProject; first subtask in itemOrProject; or first item in itemList or first subtask in first project in itemList)
//            updateToNextTimerItem(true); //initialize to first element to time (itemOrProject; first subtask in itemOrProject; or first item in itemList or first subtask in first project in itemList)
//        }
//    }
    /**
     * go to next timeable item from source (or pick first item on first call).
     * If NB! The currently time item may have disappeared from the list or
     * project (filtered, or a list like Today may have been refreshed), so
     * there is not guarantee that the item is found, in that case, either
     * return false
     *
     * @param updateAndSave
     * @return
     */
//    void goToNextTimerItem() {
//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item nextTimedItemN;
//        Item nextAgainTimedItemN = null;
//        Item currentlyTimedItem = getTimedItemN(); //may return null on first call, in which case the very first subtask will be returned
//
//        if (source == null || source.size() == 0) { //if source undefined or an empty list
//            if (currentlyTimedItem != null) {
//                nextTimedItemN = null; //if only one item was set, then moving to next should remove it, leaving null as value
//            }
//        } else { //we have a non-empty source
//            List<Item> sourceLeafTasks = source.getLeafTasksToList();
//            int currItemIndex;
//            if (currentlyTimedItem == null) { //first time (or wrapping around), use first leaf element
//                currItemIndex = 0; //if no currently time
//            } else {
//                currItemIndex = sourceLeafTasks.indexOf(currentlyTimedItem);
//            }
//            if (currItemIndex < 0) { //currentlyTimedItem is no longer in list
//                if (MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                        && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean() && sourceLeafTasks.size() > 0) {
//                    nextTimedItemN = sourceLeafTasks.get(0);
//                }
//            } else if (currItemIndex == sourceLeafTasks.size() - 1) { //if reached end of list
//                if (MyPrefs.enableTimerToRestartOnLists.getBoolean() && sourceLeafTasks.size() > 0
//                        //                        && ScreenTimer7.showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItem, source)) {
//                        && showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItem, source)) {
//                    nextTimedItemN = sourceLeafTasks.get(0);
//                }
//            } else {
//                ASSERT.that(currItemIndex >= 0 && currItemIndex + 1 < sourceLeafTasks.size());
//                nextTimedItemN = sourceLeafTasks.get(currItemIndex + 1);
//            }
//        }else { //source==null => cannot find a next
//            nextTimedItemN = null;
//        }
//        setTimedItem(nextTimedItemN);
//        saveMe();
//    }
//    Item updateToNextTimerItemOLD(boolean updateAndSave) {
//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item nextTimedItemN = null;
//        Item nextAgainTimedItemN = null;
//
//        Item previousTimedItem = getTimedItemN(); //may return null on first call, in which case the very first subtask will be returned
//        if (source != null && previousTimedItem != null) {
//            List<Item> sourceLeafTasks = source.getLeafTasksToList();
//            int prevItemIndex = sourceLeafTasks.indexOf(previousTimedItem);
//            if (prevItemIndex < 0) {
//                if (MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                        && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean() && sourceLeafTasks.size() > 0) {
//                    nextTimedItemN = sourceLeafTasks.get(0);
//                }
//            } else if (prevItemIndex == sourceLeafTasks.size() - 1) { //if reached end of list
//                if (MyPrefs.enableTimerToRestartOnLists.getBoolean() && sourceLeafTasks.size() > 0
//                        && ScreenTimer7.showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(previousTimedItem, source)) {
//                    nextTimedItemN = sourceLeafTasks.get(0);
//                }
//            } else {
//                ASSERT.that(prevItemIndex >= 0 && prevItemIndex + 1 < sourceLeafTasks.size());
//                nextTimedItemN = sourceLeafTasks.get(prevItemIndex + 1);
//            }
//        }
//
//        if (updateAndSave) {
//            setTimedItem(nextTimedItemN);
//            saveMe();
//        }
//        return nextTimedItemN;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private ItemAndListCommonInterface getNextTaskAfter(List<ItemAndListCommonInterface> list, ItemAndListCommonInterface current,
//            boolean returnFirstElementOnNotFound, boolean restartIfReachedEndOfList) {
//        if (list == null || list.isEmpty() || current == null) {
//            return null;
//        }
//        ItemAndListCommonInterface nextTimedItemN = null;
//        int index = list.indexOf(current);
//        if (index < 0) {
//            if (returnFirstElementOnNotFound && list.size() > 0) {
//                nextTimedItemN = list.get(0);
//            }
//        } else if (index == list.size() - 1) { //if reached end of list
//            if (restartIfReachedEndOfList && list.size() > 0) {
//                nextTimedItemN = list.get(0);
//            }
//        } else {
//            ASSERT.that(index >= 0 && index + 1 < list.size());
//            nextTimedItemN = list.get(index + 1);
//        }
//        return nextTimedItemN;
//    }
//</editor-fold>
    static private int getNextIndex(List list, int index, boolean restartIfReachedEndOfList) {
        if (list.size() > 0) {
            if (index >= 0 && index + 1 < list.size()) { //if there is an entry after index
                return index + 1;
            } else if (index < 0 || index >= list.size() - 1) { //if index not found or reached end of list 
                if (restartIfReachedEndOfList && list.size() > 1) { //list.size() > 1: avoid returning the same element from a list of just 1
                    return 0;
                }
            }
        }
        return -1;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void goToNextTimerItemNEWOLD(boolean saveTimerInstance) {
//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item currentlyTimedItemN = getTimedItemN();
//        Item currNextTimedItemN = getNextTimedItemN();
//        Item newTimedItemN;
//        Item newNextTimedItemN;
//        boolean wrapAround = false;
//        if (currentlyTimedItemN != null) {
//            stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save the timed item, not the timerInstance (done below)
//        }
//
//        if (source == null) {
//            newTimedItemN = null;
//            newNextTimedItemN = null;
//        } else {
////            List<Item> sourceLeafTasks = source.getLeafTasksAsListN();
//            List<Item> sourceLeafTasks = source.getLeafTasksForTimerN();
//            if (sourceLeafTasks.isEmpty()) {
//                newTimedItemN = null;
//                newNextTimedItemN = null;
//            } else {
//                int currentItemIndex;
//                if (currentlyTimedItemN != null) {
//                    currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);
////                } else if (!sourceLeafTasks.isEmpty() && MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                } else if (sourceLeafTasks.size() > 1 && MyPrefs.enableTimerToRestartOnLists.getBoolean() //>1: only wrap around if more items than current in the list (otherwise it would 'wrap' around to the same item)
//                        && (MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean()
//                        || (MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean() && showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source)))) {
//                    currentItemIndex = 0;
//                    wrapAround = true;
//                } else {
//                    currentItemIndex = -1;
//                }
//                if (currentItemIndex != -1) { // current task still in list, get (possibly updated) next
////                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAround);
//                    if (nextIndex != -1) {
//                        newTimedItemN = sourceLeafTasks.get(nextIndex);
//                    } else {
//                        newTimedItemN = null;
//                    }
//                    int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                    if (nextAgainIndex != -1) {
//                        newNextTimedItemN = sourceLeafTasks.get(nextAgainIndex);
//                    } else {
//                        newNextTimedItemN = null;
//                    }
//                } else { // index == -1 <=> current task no longer in list
//                    int nextIndex;
//                    if (currNextTimedItemN != null && (nextIndex = sourceLeafTasks.indexOf(currNextTimedItemN)) != -1) {
//                        //if nextItem is defined and (still) in the list, use that
//                        newTimedItemN = currNextTimedItemN;
//                        int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                        newNextTimedItemN = (nextAgainIndex == -1) ? null : sourceLeafTasks.get(nextAgainIndex);
//                    } else { //index==-1 <=> if not, propose to start-over etc
//                        if (!sourceLeafTasks.isEmpty() && MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                                && (MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean()
//                                || (MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean() && showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source)))) {
//                            newTimedItemN = sourceLeafTasks.get(0);
//                            nextIndex = getNextIndex(sourceLeafTasks, 0, true);
//                            newNextTimedItemN = (nextIndex == -1) ? null : sourceLeafTasks.get(nextIndex);
//                        } else { //index==-1 <=> if not, propose to start-over etc
//                            newTimedItemN = null;
//                            newNextTimedItemN = null;
//                        }
//                    }
//                }
//            }
//        }
//        setTimedItem(newTimedItemN, true);
////        setNextTimedItem(newNextTimedItemN);
//        if (saveTimerInstance) {
//            DAO.getInstance().saveToParseNow(this);
//        }
//    }
//</editor-fold>
//    void goToNextTimerItemNEW(boolean saveTimerInstance) {
//        goToNextTimerItemNEW(saveTimerInstance, MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean(),
//                MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean());
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    void goToNextTimerItemNEW(boolean saveTimerInstance, boolean wrapAroundToStartOfSource, boolean askIfWrapAround) {
//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item currentlyTimedItemN = getTimedItemN();
//        Item currNextTimedItemN = getNextTimedItemN();
//        Item newTimedItemN;
//        Item newNextTimedItemN;
////        boolean wrappedAround = false;
//        if (currentlyTimedItemN != null) {
//            stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save the timed item, not the timerInstance (done below)
//        }
//
//        if (source == null) {
//            newTimedItemN = null;
//            newNextTimedItemN = null;
//        } else {
////            List<Item> sourceLeafTasks = source.getLeafTasksAsListN();
//            List<Item> sourceLeafTasks = source.getLeafTasksForTimerN();
//            if (sourceLeafTasks.isEmpty()) {
//                newTimedItemN = null;
//                newNextTimedItemN = null;
//            } else { //not empty
//                int currentItemIndex;
//                if (currentlyTimedItemN == null) {
//                    currentItemIndex = 0;
//                } else {
//                    currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);
////                } else if (!sourceLeafTasks.isEmpty() && MyPrefs.enableTimerToRestartOnLists.getBoolean()
////                } else if (sourceLeafTasks.size() > 1 && MyPrefs.enableTimerToRestartOnLists.getBoolean() //>1: only wrap around if more items than current in the list (otherwise it would 'wrap' around to the same item)
////                        && (MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean()
////                        || (MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean() && showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source)))) {
//                }
//
//                //if currentItem is no longer found, wrap around to start of list if set
//                if (false && currentItemIndex == -1
//                        &&//sourceLeafTasks.size() > 1 && //&& MyPrefs.enableTimerToRestartOnLists.getBoolean() //>1: only wrap around if more items than current in the list (otherwise it would 'wrap' around to the same item)
//                        (wrapAroundToStartOfSource
//                        && (!askIfWrapAround || showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source)))) {
//                    currentItemIndex = 0;
////                    wrappedAround = true;
//                }
//
//                if (currentItemIndex != -1) { // current task still in list, get (possibly updated) next
////                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
////                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrappedAround);
//                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAroundToStartOfSource);
//                    if (nextIndex != -1) {
//                        newTimedItemN = sourceLeafTasks.get(nextIndex);
//                    } else {
//                        newTimedItemN = null;
//                    }
////                    int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                    int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, wrapAroundToStartOfSource);
//                    if (nextAgainIndex != -1) {
//                        newNextTimedItemN = sourceLeafTasks.get(nextAgainIndex);
//                    } else {
//                        newNextTimedItemN = null;
//                    }
//                } else { // index == -1 <=> current task no longer in list
//                    int nextIndex;
//                    //if nextItem is defined and (still) in the list, use that and if possible, find next item after that again
//                    if (currNextTimedItemN != null && (nextIndex = sourceLeafTasks.indexOf(currNextTimedItemN)) != -1) {
//                        newTimedItemN = currNextTimedItemN;
////                        int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                        int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, wrapAroundToStartOfSource);
//                        newNextTimedItemN = (nextAgainIndex == -1) ? null : sourceLeafTasks.get(nextAgainIndex);
//                    } else { //nextItem either not defined or no longer in list => if wrapAround start from start of list, otherwise no elements could be found
////                        if (!sourceLeafTasks.isEmpty() && MyPrefs.enableTimerToRestartOnLists.getBoolean()
////                                && (MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean()
////                                || (MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean() && showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source)))) {
//                        if (wrapAroundToStartOfSource
//                                && (!askIfWrapAround || showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source))) {
//                            newTimedItemN = sourceLeafTasks.get(0);
//                            nextIndex = getNextIndex(sourceLeafTasks, 0, true);
//                            newNextTimedItemN = (nextIndex == -1) ? null : sourceLeafTasks.get(nextIndex);
//                        } else { //index==-1 <=> if not, propose to start-over etc
//                            newTimedItemN = null;
//                            newNextTimedItemN = null;
//                        }
//                    }
//                }
//            }
//        }
//
//        setTimedItem(newTimedItemN, true);
////        setNextTimedItem(newNextTimedItemN);
//
//        if (saveTimerInstance) {
//            DAO.getInstance().saveToParseNow(this);
//        }
//    }
//</editor-fold>
    /**
     * initialize the timer when started/created with a source and/or a task or
     * project. If started with a source and a project, will pick the first
     * appropriate subtask from the project, ensuring that a later update will
     * continue the whatever task comes after this subtask
     *
     * @param itemOrProjectN
     * @param itemListOrCategoryN
     */
    private void setup(Item itemOrProjectN, ItemAndListCommonInterface itemListOrCategoryN) {
        ASSERT.that(itemOrProjectN != null || itemListOrCategoryN != null, "not both item and source can be null!");

        if (itemOrProjectN == null) { //only source is defined
            setTimerSource(itemListOrCategoryN, true); //even if started directly on project, use first appropriate subtask
        } else { //item defined
            if (itemListOrCategoryN == null) { //no source
                if (itemOrProjectN.isProject()) {
                    setTimerSource(itemOrProjectN, true); //even if started directly on project, use first appropriate subtask
                } else { //item NOT a project
                    setTimedItems(itemOrProjectN, false); //false since no nextItem exists
                }
            } else { //source also defined
                if (itemOrProjectN.isProject()) {
                    List<Item> sourceLeafTasks = itemOrProjectN.getLeafTasksForTimerN();
                    if (itemOrProjectN.size() > 0) {
//                        setTimedItems(sourceLeafTasks.get(0), true); //even if started directly on project, use first appropriate subtask
                        setTimerSource(itemListOrCategoryN, true); // if n appropriate tasks in project, use first appropriate task in source (if any)
                    } else {
                        setTimerSource(itemListOrCategoryN, true); // if n appropriate tasks in project, use first appropriate task in source (if any)
                    }
                } else { //item NOT a project
                    setTimerSource(itemListOrCategoryN, false); //even if started directly on project, use first appropriate subtask
                    setTimedItems(itemOrProjectN, true); //even if started directly on project, use first appropriate subtask
                }
            }
        }
    }

    /**
     *
     * @param moveToNext
     * @param saveTimerInstanceXXX
     * @param wrapAroundToStartOfSource
     * @param askIfWrapAround
     * @return
     */
    /**
     * will update timedItem and nextTimedItem, wrapping around the list if
     * settings set, set null values if no next values. If no timedItem is
     * already set (eg on first call), will find the first suitable in Source.
     * NB! if the timedItem is no longer in the list it will automatically use
     * the nextTimedItem already set previously, iff it is still in the list. If
     * neither are the the list will set both to null to indicate no appropriate
     * values were found.
     *
     * @param saveTimerInstance
     * @param wrapAroundToStartOfSource
     * @param askIfWrapAround
     * @return false if no change was made (may be true even if no change was
     * made)
     */
    boolean updateNEW2(boolean forceMoveToNextEvenIfStillValid, boolean saveTimerInstanceXXX, boolean wrapAroundToStartOfSource, boolean askIfWrapAround) {
        boolean changeWasMade = true;
        ItemAndListCommonInterface source = getTimerSourceN();
        Item currentlyTimedItemN = getTimedItemN();
        Item currNextTimedItemN = getNextTimedItemN();
        Item newTimedItemN;
        Item newNextTimedItemN;
//        boolean wrappedAround = false;
        if (false && currentlyTimedItemN != null) {
            stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save the timed item, not the timerInstance (done below) //NO, do below only if moving to a new item
        }

        int nextIndex;
        if (source == null) {
//            nextIndex = -1;
            newTimedItemN = currentlyTimedItemN; //eg when creating a task inside Timer
            newNextTimedItemN = null;
        } else { //source has elements
            List<Item> sourceLeafTasks = source.getLeafTasksForTimerN();

            if (sourceLeafTasks.isEmpty()) {
                nextIndex = -1;
                newTimedItemN = null;
//                stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveTimerInstanceXXX);
//                newNextTimedItemN = null;
            } else { //source not empty
                int currentItemIndex;
                if (currentlyTimedItemN == null) {
                    nextIndex = 0; //start with first item on first call
                    newTimedItemN = sourceLeafTasks.get(nextIndex);
                } else {
                    currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);

                    if (currentItemIndex == -1 && currentlyTimedItemN.isValidItemForTimer() && currentlyTimedItemN.isProject()
                            && forceMoveToNextEvenIfStillValid) { //special case: new subtask added to timed item directly in Timer, so continue with first subtask
                        nextIndex = 0;
                        newTimedItemN = sourceLeafTasks.get(nextIndex);
                    } else if (currentItemIndex == -1 || !currentlyTimedItemN.isValidItemForTimer() || forceMoveToNextEvenIfStillValid) { // current task still in list, get (possibly updated) next
                        //MOVE TO NEXT: if no longer in list OR  no longer valid (e.g. Done) OR user chose to force-move to next task:
//                        stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveTimerInstanceXXX);
                        if (currentItemIndex == -1) { //currently timed item no longer in list, so use next if it is still in the list
                            if (currNextTimedItemN != null && (nextIndex = sourceLeafTasks.indexOf(currNextTimedItemN)) != -1) {
                                newTimedItemN = currNextTimedItemN;
                            } else { //currNextTimedItemN == null || sourceLeafTasks.indexOf(currNextTimedItemN) == -1
                                //wrap around if defined
                                if (wrapAroundToStartOfSource && sourceLeafTasks.size() > 1 //>1: only wrap around if list contains more than the current item
                                        && (!askIfWrapAround || showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source))) {
                                    nextIndex = 0;
                                    newTimedItemN = sourceLeafTasks.get(nextIndex);
                                } else {
                                    nextIndex = -1;
                                    newTimedItemN = null;
                                }
                            }
                        } else { //currently timed item is STILL in list
                            nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAroundToStartOfSource);
                            if (nextIndex != -1) {
                                newTimedItemN = sourceLeafTasks.get(nextIndex);
                            } else if (wrapAroundToStartOfSource && sourceLeafTasks.size() > 1 //>1: only wrap around if list contains more than the current item
                                    && (!askIfWrapAround || showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source))) {
                                nextIndex = 0;
                                newTimedItemN = sourceLeafTasks.get(nextIndex);
                            } else {
                                newTimedItemN = null;
                            }
                        }
//                        newNextTimedItemN = findNextTimerItem(newTimedItemN, nextIndex, sourceLeafTasks, wrapAroundToStartOfSource);
//                        newNextTimedItemN = findNextTimerItem(nextIndex, sourceLeafTasks, wrapAroundToStartOfSource);
                    } else { //(currentItemIndex != -1 && currNextTimedItemN.isValidItemForTimer() &&! forceMoveToNextEvenIfStillValid)
                        //item still in list and still valid for timer (and not forcing move to next) <=> refresh nextItem
                        newTimedItemN = currentlyTimedItemN;
                        nextIndex = currentItemIndex;
//                        newNextTimedItemN = findNextTimerItem(nextIndex, sourceLeafTasks, wrapAroundToStartOfSource);
                    }
                }
            }
            newNextTimedItemN = findNextTimerItem(nextIndex, sourceLeafTasks, wrapAroundToStartOfSource);
        }

        //if we were timing an item before and it has changed, then stop and save timer for that item before changing just below in setTimedItems
        if (currentlyTimedItemN != null & !Objects.equals(currentlyTimedItemN, newTimedItemN)) {
            stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveTimerInstanceXXX);
        }

        changeWasMade = newTimedItemN != currentlyTimedItemN || newNextTimedItemN != currNextTimedItemN; //change made if either has cahnged
        if (changeWasMade) {
            setTimedItems(newTimedItemN, newNextTimedItemN); //NB: set new timed items *before* calling start below (otherwise restarts old timed item)

            if (isAutoStartTimer()) {
                startTimer(false, false);
            }
//        fireChangedEvent();

//        if (true || saveTimerInstanceXXX) {
            if (true || saveTimerInstanceXXX) {
//            DAO.getInstance().saveToParseNow(this);
                DAO.getInstance().saveToParseLater(this);
            }
        }
        return changeWasMade;
    }

    boolean updateNEW2(boolean forceMoveToNextEvenIfStillValid) {
        return updateNEW2(forceMoveToNextEvenIfStillValid, true, isWrapAround(), MyPrefs.timerAskBeforeRestartingOnList.getBoolean());
    }
//    boolean updateNEW2(boolean forceMoveToNextEvenIfStillValid, boolean saveTimerInstanceXXX) {
//        return updateNEW2(forceMoveToNextEvenIfStillValid, saveTimerInstanceXXX, isWrapAround(), MyPrefs.timerAskBeforeRestartingOnList.getBoolean());
//    }

    boolean isWrapAround() {
        //TODO: make this an option the user can change for each individual timerInstance(??)
        return MyPrefs.timerEnableRestartOnLists.getBoolean()
                && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean();
    }

//    void updateNextTimerItemNEW(boolean saveTimerInstance) {
//        updateNextTimerItemNEW(saveTimerInstance, MyPrefs.enableTimerToRestartOnLists.getBoolean()
//                && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean(),
//                MyPrefs.timerAskBeforeStartingOnNewElement.getBoolean());
//    }
    /**
     * called whenever a change event from source is received, updates current
     * and next items (if current no longer in list, tries if next is still
     * there and starts from there, otherwise starts over if wrap-around setting
     * is active)
     */
//    private void updateOnSourceChange() {
//        updateNextTimerItemNEW(true);
////        fireChangedEvent();
//    }
    /**
     * update/refresh timedItem and nextItem when source changes: if timedItem
     * still in list keep that and update nextItem if changed, if timedItem no
     * longer in list, then use nextItem if still in list, otherwise wrap around
     * if activated. Special case: if list was empty before? no shouldn't
     * happen.
     *
     * @param saveTimerInstance
     * @param wrapAroundToStartOfSource
     * @param askIfWrapAround
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    void updateNextTimerItemNEW(boolean saveTimerInstance, boolean wrapAroundToStartOfSource, boolean askIfWrapAround) {
//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item currentlyTimedItemN = getTimedItemN();
//        Item currNextTimedItemN = getNextTimedItemN();
//
//        ASSERT.that(source != null && source.size() > 0, "source should never be null or empty when refreshing");
//        ASSERT.that(currentlyTimedItemN != null, "currentItem should never be null");
//
//        List<Item> sourceLeafTasks = source.getLeafTasksForTimerN();
//        if (sourceLeafTasks.isEmpty()) {
//            currentlyTimedItemN = null;
//            currNextTimedItemN = null;
//        } else { //not empty
//            int currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);
//
//            if (currentItemIndex != -1) { // current task still in list, get (possibly updated) next
////                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, MyPrefs.enableTimerToRestartOnLists.getBoolean() && MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
//                int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAroundToStartOfSource);
//                if (nextIndex != -1) {
//                    currNextTimedItemN = sourceLeafTasks.get(nextIndex);
//                } else {
//                    currNextTimedItemN = null;
//                }
//
//            } else { // index == -1 <=> current task no longer in list
//
//                stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save the timed item, not the timerInstance (done below)
//
//                int nextIndex;
//                //if nextItem is defined and (still) in the list, use that and if possible, find next item after that again
//                if (currNextTimedItemN != null && (nextIndex = sourceLeafTasks.indexOf(currNextTimedItemN)) != -1) {
//                    currentlyTimedItemN = currNextTimedItemN;
//                    int nextAgainIndex = getNextIndex(sourceLeafTasks, nextIndex, wrapAroundToStartOfSource);
//                    currNextTimedItemN = (nextAgainIndex == -1) ? null : sourceLeafTasks.get(nextAgainIndex);
//                } else { //neither current, nor next, in in list => if wrapAround start from start of list, otherwise no elements could be found
//                    if (wrapAroundToStartOfSource
//                            && (!askIfWrapAround || showDialogPreviouslyTimedItemNoLongerInProjectOrListStartOver(currentlyTimedItemN, source))) {
//                        currentlyTimedItemN = sourceLeafTasks.get(0);
//                        nextIndex = getNextIndex(sourceLeafTasks, 0, false);
//                        currNextTimedItemN = (nextIndex == -1) ? null : sourceLeafTasks.get(nextIndex);
//                    } else { //index==-1 <=> if not, propose to start-over etc
//                        currentlyTimedItemN = null;
//                        currNextTimedItemN = null;
//                    }
//                }
//            }
//        }
//
//        setTimedItem(currentlyTimedItemN, true);
////        setNextTimedItem(currNextTimedItemN);
//
//        if (saveTimerInstance) {
//            DAO.getInstance().saveToParseNow(this);
//        }
//    }
//</editor-fold>
    static Item findNextTimerItem(Item currentlyTimedItemN, ItemAndListCommonInterface source) {
        return findNextTimerItem(currentlyTimedItemN, source, MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean());
    }

    static Item findNextTimerItemXXX(Item currentlyTimedItemN, ItemAndListCommonInterface source, boolean wrapAround) {
//        ASSERT.that(currentlyTimedItemN == getTimedItemN());

//        ItemAndListCommonInterface source = getTimerSourceN();
//        Item currentlyTimedItemN = getTimedItemN();
        Item nextTimedItemN;
//        boolean wrapAround = false;

        if (source == null || currentlyTimedItemN == null) {
            nextTimedItemN = null;
        } else {
//            List<Item> sourceLeafTasks = source.getLeafTasksAsListN();
            List<Item> sourceLeafTasks = source.getLeafTasksForTimerN();
            if (sourceLeafTasks.isEmpty()) {
                nextTimedItemN = null;
            } else {
                int currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);
                if (currentItemIndex != -1) { // current task still in list, get (possibly updated) next
                    int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAround);
                    if (nextIndex != -1) {
                        nextTimedItemN = sourceLeafTasks.get(nextIndex);
                    } else {
                        nextTimedItemN = null;
                    }
                } else { // index == -1 <=> current task no longer in list
                    nextTimedItemN = null;
                    assert false;
                }
            }
        }
        return nextTimedItemN;
    }

    static Item findNextTimerItem(Item currentlyTimedItemN, ItemAndListCommonInterface source, boolean wrapAround) {
        return findNextTimerItem(currentlyTimedItemN, source.getLeafTasksForTimerN(), wrapAround);
    }

    static Item findNextTimerItem(Item currentlyTimedItemN, List<Item> sourceLeafTasks, boolean wrapAround) {
        return findNextTimerItem(sourceLeafTasks.indexOf(currentlyTimedItemN), sourceLeafTasks, wrapAround);
    }

    static Item findNextTimerItem(int currentItemIndex, List<Item> sourceLeafTasks, boolean wrapAround) {
        Item nextTimedItemN;

        if (sourceLeafTasks == null || sourceLeafTasks.size() <= 1) {
            nextTimedItemN = null;
        } else {
//                int currentItemIndex = sourceLeafTasks.indexOf(currentlyTimedItemN);
            if (currentItemIndex != -1) { // current task still in list, get (possibly updated) next
                int nextIndex = getNextIndex(sourceLeafTasks, currentItemIndex, wrapAround);
                if (nextIndex != -1) {
                    nextTimedItemN = sourceLeafTasks.get(nextIndex);
                } else {
                    nextTimedItemN = null;
                }
            } else { // index == -1 <=> no next index found (eg reached end of list or current task no longer in list)
                nextTimedItemN = null;
//                assert false;
            }
        }
        return nextTimedItemN;
    }

    /**
     * stop timing the current timed item (if any, otherwise do nothing) and
     * update and save the item
     *
     * @param saveTimerInstanceXXX
     * @param saveTimedItemXXX
     */
    public void stopTimerOnTimedItemAndUpdateActualsAndSave(boolean saveTimerInstanceXXX, boolean saveTimedItemXXX) {//TimerInstance timerInstance) {
        pauseTimer(false, false);
        Item timedItemN = getTimedItemN(); //get the item that is/was timed
        if (timedItemN != null && getElapsedTime() > 0) {
            timedItemN.setActualForTaskItself(timedItemN.getActualForTaskItself() + getElapsedTime(), false); //false: don't auto-update startedOn time (done when status is set?!)
            setElapsedTime(0); //reset elapsed time since it's now been added to Item's actual & saved
            if (false) {
                ASSERT.that(timedItemN.getActualForTaskItself() + getElapsedTime() == timedItemN.getTotalTimerDurationOfTimedIntervals());
            }
//            DAO.getInstance().saveToParseNow(saveTimerInstance, this, saveTimedItem, timedItemN);
            DAO.getInstance().saveToParseLater(this, timedItemN);
        }
//        if (false && saveTimerInstance) {
//            DAO.getInstance().saveToParseNow(this);
//        }
    }

    /**
     * stop timing the currently timed item
     *
     * @param currentItem
     */
//    private void goToNextAndStartIfAutoStartXXX(boolean saveTimerInstance, boolean saveNewTimedItem) {
////        boolean wasRunning = isRunning();
//        goToNextTimerItemNEW(false);
//        Item currentItem = getTimedItemN();
//        if (currentItem != null) {
//            if (isAutoStartTimer()) { //whether the current active timer or one paused in the stack, move timerInstance to next task and remove it if there are no more tasks
//                startTimer(saveTimerInstance, saveNewTimedItem);
//                fireChangedEvent();
//            } else {
//                fireChangedEvent();
//                if (saveTimerInstance) {
//                    DAO.getInstance().saveToParseNow(this); //save items and timer that may have changed
//                }
//            }
//        } else { //no more items to time
//            fireDeletedEvent();
//            DAO.getInstance().deleteLater(this, true); //delete the timer
//        }
//    }
//    private void stopTimingAndGoToNext() {
//        stopTimingAndGoToNext(false,false);
//    }
//    private void stopTimingAndGoToNext() {
//        stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //save currently timed item
//        goToNextAndStartIfAutoStart(true, true); //save timerInstance and new timed item
//    }
//    private void stopTimingAndGoToNextXXX(boolean saveTimerInstance, boolean saveTimedItem, boolean saveNewTimedItem) {
//        stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveTimedItem); //save currently timed item
//        goToNextAndStartIfAutoStartXXX(saveTimerInstance, saveNewTimedItem); //save timerInstance and new timed item
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * update Timer when time list changes, e.g. elements reordered (D&D),
     * element completed/deleted/cancelled
     */
//    DataChangedListener timedListChanged = (oldVal, newVal) -> {
//        //test if calling list is the timed one!
//        //if list changed
//        //if currently tiimed element is changed (Completed/Cancelled/Deleted (soft OR hard for RepeatRule instances)), then update Actual and update to next timed task as usual
//        //if currently timed element is still there, then no need to change
//        //if currently timed element is NO LONGER in the list (moved, list updated, ...), then ??
//        //examples: if TODAY list is updated with next days elements, then (don't stop timer on current element X, if you're timing sth it should continue), but msg "X no longer in list Y, please
//        //examples: if timed list is reordered, then refresh TimerScreen to get correct next task
//        //examples: if currently time item X is moved to another list, then when timer is stopped on X: "X no longer in the timed list "Y", timer exits, please select list to be timed"
//        //make it an option what to do if timed elt no longer in timed list: "Start timer from beginning of new list or Exit timer?"
//
//    };
//    DataChangedListener timedItemChanged = (oldVal, newVal) -> {
//        //if currently tiimed element is changed (Completed/Cancelled/Deleted (soft OR hard for RepeatRule instances)), then update Actual and update to next timed task as usual
//
//    };
    /**
     * returns next item in to start the timer on (either the Item itself, the
     * first subtasks if the Item is a Project, or from ItemList, or null if
     * there are no more. Different situations: 1) A single task is timed (==no
     * ItemList, ==no Project/no subtasks) 2) A list with multiple
     * tasks/projects timed: 2a) A non-project task is timed => continue with
     * next in list, 2b) a project is timed, if there's a following subtask,
     * continue with that, otherwise with next task in list. 3) Only a Project
     * is timed (==no ItemList): get next subtask (if any) 4) A Task/project has
     * had new subtasks added since? 4a) after the last timed subtasks => no pb,
     * will be found 4b) *before* the last timed subtask => cannot be
     * distinguished from an earlier subtask that was skipped over
     */
//</editor-fold>
    public void setWasRunningWhenInterrupted(boolean interrupted, boolean saveTimerInstance) {
        boolean wasInterrupted = isWasInterruptedWhileRunning();
        setInterruptedWhileRunning(interrupted);
        if (saveTimerInstance && wasInterrupted != interrupted) {
//            saveMe();
//            DAO.getInstance().saveToParseNow(this); //save items and timer that may have changed
            DAO.getInstance().saveToParseLater(this); //save items and timer that may have changed

        }
    }

    public void removeFromListeners() {
//        setTimedItem(null);
//        setNextTimedItem(null);
        setTimedItems(null, null);
//        setTimerSource(null, false, false, false); //no chg event
        setTimerSource(null, false); //no chg event
    }

    /**
     * delete this timer ('later') when it's done
     */
    public void deleteMe() {
        //remove listeners
        removeFromListeners();
//        fireDeletedEvent();
//        DAO.getInstance().delete(this, true, true);
        DAO.getInstance().deleteLater(this, true);
    }

    /**
     * save the status of the timed item when timer was started (to reliable
     * identify when an Item's status has changed since Timer started)
     *
     * @param newStatus
     */
    private void setItemStatus(ItemStatus newStatus) {
        if (false && Config.TEST) {
            ASSERT.that(newStatus != null, "status should never be reset to CREATED by storing a null status");
        }
        ItemStatus oldVal = getItemStatusN();
        if (newStatus != null) {
            if (!Objects.equals(oldVal, newStatus)) {
                put(PARSE_TIMER_TASK_STATUS, newStatus.toString());
            }
        } else {
            remove(PARSE_TIMER_TASK_STATUS);
        }
    }

    /**
     * return the status of the timed item when timer was started (to reliable
     * identify when an Item's status has changed)
     *
     * @return
     */
    public ItemStatus getItemStatusN() {
        String status = getString(PARSE_TIMER_TASK_STATUS);
//        return (status == null) ? ItemStatus.CREATED : ItemStatus.valueOf(status); //Created is initial value
        return (status == null) ? null : ItemStatus.valueOf(status); //Created is initial value
    }

    void updateCurrentlyTimedItemWithEditedElapsedTime(long elapsedTime, boolean setTotalTime) {
        TimerInstance2 timerInstance = this;
        //                TimerInstance2 timerInstance2 = TimerStack.getInstance().getCurrentTimerInstanceN();
//            TimerInstance timerInstance2 = getCurrentTimerInstanceN();
        //UI: OK to longPress a running timer (no need to stop it, will simply adjust timer on Done
        if (true || timerInstance != null) {
            Item timedItem = timerInstance.getTimedItemN();

            long oldElapsedTime = timerInstance.getElapsedTime();
            long oldElapsedTotalDisplayedTime = timerInstance.getElapsedTimeToDisplay();

            if (MyPrefs.timerShowTotalActualInTimer.getBoolean() || setTotalTime) {
                //adjust elapsed time
//                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
                long editedElapsedTime = elapsedTime;
                long diff = editedElapsedTime - oldElapsedTotalDisplayedTime; //NB can also be negative if timer was increased
                long newElapsedTime = Math.max(0, oldElapsedTime + diff);
//                        timerInstance2.setElapsedTime(newElapsedTime);
                timerInstance.updateElapsedTime(newElapsedTime);

                //since we have edited the total (elapsed+old actual) we may also need to reduce the old actual 
//                            long itemActualReduction = -diff > oldElapsedTime ? -diff - oldElapsedTime : 0;
                long itemActualReduction = oldElapsedTime + diff < 0 ? -(oldElapsedTime + diff) : 0;
                if (itemActualReduction > 0) {
                    long itemOldActual = timedItem.getActualForTaskItself();
                    long itemNewActual = itemOldActual - itemActualReduction;
                    timedItem.setActualForTaskItself(itemNewActual, false);
                }
            } else { //enough to just adjust the elapsed time
//                        long editedElapsedTime = hiddenElapsedTimePicker.getDuration();
                long editedElapsedTime = elapsedTime;
//                        timerInstance2.setElapsedTime(editedElapsedTime);
                timerInstance.updateElapsedTime(editedElapsedTime);
            }
//            DAO.getInstance().saveToParseNow(timedItem);
            DAO.getInstance().saveToParseLater(timedItem);
        }
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        boolean changeWasMade;
        Object actionSource = evt.getSource();
//        ActionEvent.Type eventType = evt.getEventType();
        int keyEvent = evt.getKeyEvent(); //use the keyEvent to carry the actual event

        //from ITEM
        if (actionSource instanceof Item) {
            //check if the item is currently being timed (should be the case since we're listening9
            if (actionSource == getTimedItemN()) {
                ASSERT.that(keyEvent == Item.ACTION_EVENT_CHANGED || keyEvent == Item.ACTION_EVENT_REMOVED);
                Item item = (Item) actionSource;
                //if item is deleted, or status has changed to a state where timer shouldn't run (CANCEL/DONE/WAITING), then stop it
                if ((keyEvent == Item.ACTION_EVENT_CHANGED
                        && (item.getStatus() != getItemStatusN() && (!isValidItemForTimer(item) || item.getStatus() == ItemStatus.WAITING))) //WAITING=>so that clicking Set Waiting in timer will advance to next task even if Waiting is valid for Timer)
                        || keyEvent == Item.ACTION_EVENT_REMOVED) { //UI: this means that changing from one invalid state (if timer started eg on Done) will stop the timer, which is most likely what is wanted
//<editor-fold defaultstate="collapsed" desc="comment">
//                    && !timerInstanceN.isTimeEvenInvalidItemOrProjects()) {
//                timerInstanceN.stopTimerOnTimedItemAndUpdateActualsAndSave(false, saveItem); //stop this timer, save item,
//                    if (false) {
//                        boolean wasRunning = isRunning();
//                        stopTimerOnTimedItemAndUpdateActualsAndSave();
//                        if (isAutoGotoNextTask()) {
//                            updateToNextTimerItemNEW();
////whether the current active timer or one paused in the stack, move timerInstance to next task and remove it if there are no more tasks
//                            Item nextTimedItem = getTimedItemN();
//                            if (nextTimedItem != null) {
//                                if (wasRunning && isAutoGotoNextTask()) {
//                                    startTimer();
//                                }
//                                listeners.fireActionEvent(new ActionEvent(this, TimerInstance2.ACTION_EVENT_CHANGED));//
//                            } else { //no more items to time
//                                listeners.fireActionEvent(new ActionEvent(this, TimerInstance2.ACTION_EVENT_REMOVED));//inform the stack
//                                DAO.getInstance().deleteLater(this, true); //delete the timer
//                            }
//                            DAO.getInstance().saveToParseLater(nextTimedItem); //save items and timer that may have changed
//                        }
//                        if (this == TimerStack2.getInstance().getCurrentTimerInstanceN()) { //if the timerInstance is the active one, inform the timer
//                            listeners.fireActionEvent(new ActionEvent(this, TimerInstance2.ACTION_EVENT_CHANGED));//
//                        }
//                        DAO.getInstance().saveToParseNow(item, this); //save items and timer that may have changed
//                    }
//</editor-fold>
//                    stopTimingAndGoToNext(item);
//                    stopTimingAndGoToNext();
//                    stopTimingAndGoToNextXXX(true, false, true); //save timer and new item, but not old because actionPerformed is called when current item is updated/saved
//                    if (updateNEW2(false, true)) {
//                    if (updateNEW2(false)) {
                    updateNEW2(false);
                    fireChangedEvent();
                    DAO.getInstance().saveToParseLater(this);
                    DAO.getInstance().triggerParseUpdate();
                }
            } else if (actionSource == getNextTimedItemN()) {
                ASSERT.that(keyEvent == Item.ACTION_EVENT_CHANGED || keyEvent == Item.ACTION_EVENT_REMOVED);
                Item item = (Item) actionSource;
                //if next item is deleted, or status has changed to a state where timer shouldn't run (CANCEL/DONE/WAITING), then stop it
                if ((keyEvent == Item.ACTION_EVENT_CHANGED && (item.getStatus() != getItemStatusN() && !isValidItemForTimer(item)))
                        || keyEvent == Item.ACTION_EVENT_REMOVED) { //UI: this means that changing from one invalid state (if timer started eg on Done) will stop the timer, which is most likely what is wanted
                    if (false) {
//                        setNextTimedItem(findNextTimerItem(getTimedItemN(), item, MyPrefs.timerAlwaysRestartTimerOnListOrProjectIfTimedTaskNotFoundInListOrProject.getBoolean())); //UI: true: if nextTimedItem is changed, always wrap around list without asking (too complex UI to ask)
//                        fireChangedEvent(); //updated Timer with new nextTimedItem
                    } else {
//                        updateOnSourceChange();
//                        if (updateNEW2(false, true)) {
//                        if (updateNEW2(false)) {
                        updateNEW2(false);
                        fireChangedEvent();//, true, false);
                        DAO.getInstance().saveToParseLater(this);
                        DAO.getInstance().triggerParseUpdate();
                    }
                }
            }
            //from ITEMLIST/CATEGORY
        } else if (actionSource instanceof ItemList) {
            ASSERT.that(keyEvent == ItemList.ACTION_EVENT_CHANGED || keyEvent == ItemList.ACTION_EVENT_REMOVED);
            if (actionSource == getTimerSourceN()) {
                ItemList itemListOrCategory = (ItemList) actionSource;
                if (keyEvent == ItemList.ACTION_EVENT_CHANGED) { //this can also happen for an interrupted timerInstance
                    //if the currently timed task is removed from the list:
//                    if (false) {
//                        if (!itemListOrCategory.contains(getTimedItemN())) { ////if no longer in list, update and save
////                        stopTimerOnTimedItemAndUpdateActualsAndSave(); 
////                        stopTimingAndGoToNext();
//                            stopTimingAndGoToNext(true, true, true);
//                            fireChangedEvent();
//                        } else { //update NextComingItem if necessary
//                            Item next = findNextTimerItem(getTimedItemN(), itemListOrCategory, true);
//                            setNextTimedItem(next); //No effect if the same item as before
//                        }
//                    }
//                    updateOnSourceChange();
                    if (updateNEW2(false)) { //only trigger event below if actually changed (otherwise the onSave will trigger infinite loop
                        fireChangedEvent();//, true, false);
                        DAO.getInstance().saveToParseLater(this);
                        DAO.getInstance().triggerParseUpdate();
                    }
                }//                    listeners.fireActionEvent(new ActionEvent(this, TimerInstance2.ACTION_EVENT_CHANGED));//inform the stack
            } else if (keyEvent == ItemList.ACTION_EVENT_REMOVED) {
                //if the list is deleted, simply stop timer and save the timer
                stopTimerOnTimedItemAndUpdateActualsAndSave(false, true); //don't save timerInstance since source is removed
//                    listeners.fireActionEvent(new ActionEvent(this, TimerInstance2.ACTION_EVENT_REMOVED));//inform the stack
                fireDeletedEvent();
                deleteMe();
//                    DAO.getInstance().delete(this, true, true); //delete the timer
//                    DAO.getInstance().deleteNow(this, true); //delete the timer
//                    DAO.getInstance().deleteLater(this, true); //delete the timer
            } else {
                ASSERT.that("Unexpected keyEvent=" + keyEvent + " received");
            }
        } else {
//            ASSERT.that("Unexpected ItemList actionSource=" + actionSource + " keyEvent=" + keyEvent + " received");
            ASSERT.that("Unexpected type of actionSource=" + actionSource + " keyEvent=" + keyEvent + " received");
        }
    }

    @Override
    public void save() throws ParseException {
        if (Config.TEST) {
            ASSERT.that(getTimerSourceN() != null || (getTimedItemN() != null && getTimedItemN().isInteruptOrInstantTask()),
                    "saving TimerInstance but w/o a TimerSouce and TimedItem NOT an interrupt, TimerInstance=" + this);
        }
        super.save();
        if (Config.TEST) {
            ASSERT.that(getACL() != null, "TimerInstance ACL is null?! TimerInstance=" + this);
        }
    }
}
