/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.todocatalyst.todocatalyst.Item.Condition;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Thomas
 */
public class TimerStackEntry implements Externalizable {

    final static String CLASS_NAME_TIMER_STACK_ENTRY = "timerStackEntry";

    /**
     * the currently timed item *
     */
    Item timedItem = null;
    /**
     * the item the timer was launched on - which could be a project meaning the
     * timedItem will be project leaf-tasks
     */
    Item sourceItemOrProject = null;
    /**
     * next item to launch timer on. Precalculated
     */
    Item nextItem = null;
    /**
     * the itemList the timer was launched on - which could be a project meaning
     * the timedItem will be project leaf-tasks *
     */
//    ItemList orgItemList = null;
    ItemList itemList = null; //a locally calculated copy applying the filter, cannot/should not be saved so need to be recalculated
//    FilterSortDef filter = null;
    /**
     * stores the Parse ObjectID of an item when saved/pushed
     */
    int itemListIndex = 0;
    /**
     * is this item an interrupt? used ??
     */
//    boolean interruptOrInstantTask; //NOT needed, info is stored in Item
    /**
     * save the (virtual!) starttime when the timer was last started (adjusted
     * whenever restarted so that now-startTime gives correct value). This
     * enables knowing how long time the Timer has been running even if the app
     * has been stopped or the phone turned off
     */
    long virtualTimerStartTimeMillis = 0;
    /**
     * store whether timer was started with preference
     * timerShowTotalActualInTimer to avoid that changes to the settings while
     * timer is running will lead to storing the wrong actual
     */
    boolean timerShowsActualTotal = false; //MyPrefs.timerShowTotalActualInTimer
    /**
     * used to store the last elapsed time when the timer is paused or after it
     * is stopped. If timer shows total Actual time, then initialized to
     * previously recorded actual time.
     */
//        private long timerElapsedTimeSavedDuringPauseMillis = 0;
    private long timerElapsedTimeSavedDuringPauseMillis = 0;

//    long previouslyRunningTimersCountSoFarMillis;
    /**
     * indicates if the timer was running when this task was interrupted by
     * another one. Used to restart the timer after the interrupt has finished.
     */
    private boolean wasTimerRunningWhenInterrupted; // = false; 
    /**
     * was the task saved in Parse only by the Timer (eg an interrupt task) -
     * meaning it must be deleted if the user Cancels
     */
    boolean timedItemSavedLocallyInTimer; // = false; 
    /**
     * seconds from timer (seconds are not supported when editing the time in
     * Picker)
     */
    long savedSecondsFromTimerInMillis; // = false; //was the task saved in Parse only by the Timer (eg an interrupt task) - meaning it must be deleted if the user Cancels

    /**
     * used by CodeNameOne to create classes when reading from storage
     */
    public TimerStackEntry() {

    }

//    TimerStackEntry(Item item, ItemList orgItemList, FilterSortDef filter, boolean interruptOrInstantTask, Condition condition) {
    TimerStackEntry(Item item, ItemList orgItemList, boolean interruptOrInstantTask, Condition condition) {
//        this(item, orgItemList, filter, interruptOrInstantTask, condition, false);
        this(item, orgItemList, interruptOrInstantTask, condition, false);
    }

//    TimerStackEntry(Item item, ItemList orgItemList, FilterSortDef filter, boolean interruptOrInstantTask, Condition condition, boolean forceTimerStartNoMatterStatus) {
    TimerStackEntry(Item item, ItemList orgItemList, boolean interruptOrInstantTask, Condition condition, boolean forceTimerStartNoMatterStatus) {
//        this.timedItem = item; //the currently edited item
        this.sourceItemOrProject = item; //the currently edited item
//        if (item != null && item.getObjectId() != null) {
//            timedItemObjectId = item.getObjectId();
//        }
//        this.orgItemList = itemList;
//        this.filter = filter;
//        if (this.filter != null) {
//            this.itemList = this.filter.filterAndSortItemList(itemList);
//        }
//        setFilteredLists(orgItemList, filter);
//        setFilteredLists(orgItemList);
//        this.orgItemList = orgItemList;
        this.itemList = orgItemList;//        if (itemList != null && itemList.getObjectId() != null) {
//            timeItemListObjectId = itemList.getObjectId();
//        }
//        this.interruptOrInstantTask = interruptOrInstantTask;
        //initialize by calling findNextTimerItem twice to set both timedItem and nextItem 
//        findNextTimerItem(condition);
//        findNextTimerItem(condition);
        initializeTimedItems(condition);
    }

    /**
     * set the lists and if a filter is defined, calculate the filtered list to
     * use in the timer
     *
     * @param orgItemList
     * @param filter
     */
//    private void setFilteredLists(ItemList orgItemList, FilterSortDef filter) {
//    private void setFilteredLists(ItemList orgItemList) {
//        this.orgItemList = orgItemList;
////        this.filter = filter;
////        if (false) {
////            if (this.filter != null) {
////                this.itemList = this.filter.filterAndSortItemList(orgItemList);
////            } else {
////                this.itemList = orgItemList;
////            }
////        }
//        this.itemList = orgItemList;
//    }

    /**
     * reset values before launching the timer on the next subtask/task in list
     * (same stack entry, new item)
     */
    private void resetValuesBeforeNewTimedItem() {
        virtualTimerStartTimeMillis = 0;
        timerElapsedTimeSavedDuringPauseMillis = 0;
        savedSecondsFromTimerInMillis = 0;
        timerShowsActualTotal = false; //MyPrefs.timerShowTotalActualInTimer
    }

    public void setTimerDurationInMillis(long alreadyElapsedTimeInMillis) {
        ASSERT.that(virtualTimerStartTimeMillis == 0, "timer duration set while timer is running");
//        virtualTimerStartTimeMillis = System.currentTimeMillis() - alreadyElapsedTimeInMillis; //set virtual start time as long back into the past as if it had been started alreadyElapsedTime ago
        timerElapsedTimeSavedDuringPauseMillis = alreadyElapsedTimeInMillis; //set virtual start time as long back into the past as if it had been started alreadyElapsedTime ago
//        virtualTimerStartTimeMillis = 0;
    }

    public void updateTimerStartTimeOnTimerStart() {
//        assert timerElapsedTimeSavedDuringPauseMillis!=0:"timer elapsed";
        ASSERT.that(virtualTimerStartTimeMillis == 0, "timer elapsed");
        virtualTimerStartTimeMillis = MyDate.currentTimeMillis() - timerElapsedTimeSavedDuringPauseMillis; //set virtual start time as long back into the past as if it had been started alreadyElapsedTime ago
        timerElapsedTimeSavedDuringPauseMillis = 0;
    }

//    void storeTimerElapsedTimeOnTimerStop() {
////        assert timerElapsedTimeSavedDuringPauseMillis!=0:"timer elapsed";
//        timerElapsedTimeSavedDuringPauseMillis = System.currentTimeMillis() - virtualTimerStartTimeMillis; //set virtual start time as long back into the past as if it had been started alreadyElapsedTime ago
//    }
    /**
     * called when pausing the timer. Will store the elapsed time and set the
     * virtualTimerStartTimeMillis to 0 to indicate that timer is not running.
     */
    public void updateAndStoreElapseTimerDurationOnPause() {
//        virtualTimerStartTimeMillis = System.currentTimeMillis() - virtualTimerStartTimeMillis; //update with duration since last time
        ASSERT.that(timerElapsedTimeSavedDuringPauseMillis == 0, "timer elapsed");
        timerElapsedTimeSavedDuringPauseMillis = MyDate.currentTimeMillis() - virtualTimerStartTimeMillis; //update with duration since last time
        virtualTimerStartTimeMillis = 0;
    }

    public long getTimerDurationInMillis() {
        if (virtualTimerStartTimeMillis != 0) {
            return MyDate.currentTimeMillis() - virtualTimerStartTimeMillis; //timer is running
        } else {
            return timerElapsedTimeSavedDuringPauseMillis;
        }
    }

    public boolean isTimerRunningNow() {
        return virtualTimerStartTimeMillis != 0; //virtualTimerStartTimeMillis should only be diff from 0 when the timer is running
    }

    boolean isWasTimerRunningWhenInterrupted() {
        return wasTimerRunningWhenInterrupted || virtualTimerStartTimeMillis != 0; //test on virtualTimerStartTimeMillis in case the app was brutally exited
    }

    void setWasTimerRunningWhenInterrupted(boolean wasTimerRunningWhenInterrupted) {
        this.wasTimerRunningWhenInterrupted = wasTimerRunningWhenInterrupted;
    }

//    private void setTimedItem(Item timedItem) {
//        this.timedItem = timedItem;
//        resetValuesBeforeNewTimedItem();
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//        public TimerStackEntry(Item item) {
//            this(item, null);
//        }
//
//        public TimerStackEntry(ItemList itemList) {
//            this(null, itemList);
//        }
    /**
     * will search for the next suitable item. Does not update itemListIndex, so
     * will return the same item each time
     *
     * @param previousItem
     * @param condition
     * @return
     */
//    private Item getNextTimerItem(Item previousItem, int itemListCurrentIndex, Condition condition) {
//        Item nextItem;
//        if (item != null) { //Timer is running on a single Item (possibly project)
//            nextItem = item.getNextLeafItem(previousItem, condition);
//        } else if (itemList != null) { //Timer is running on an ItemList
//            //TODO handle that the itemList may change due to editing (not so easy since previously timed leafTask may be anywhere in the hierarchy of subtasks)
////            if (!itemList.getItemAt(itemListIndex).equals(timedItem)) {
////                //the ItemList has changed so the previous item is no longer at the to attempt to find position of previous item
////            }
//            for (int i = itemListCurrentIndex, size = itemList.size(); i < size; i++) {
////                Item tempItem = ((Item) itemList.getItemAt(itemListIndex)).getNextLeafItem(timedItem, item -> isSuitableItemForTimer(item));
//                nextItem = ((Item) itemList.getItemAt(itemListCurrentIndex)).getNextLeafItem(previousItem, condition);
//                if (nextItem != null) {
////                    itemListIndex=i; //store last index for next time
//                    return nextItem;
//                }
//            }
//        }
//        return null; //no next item found
//    }
//</editor-fold>
    /**
     * returns next item in to start the timer on (either the Item itself, the
     * first subtasks if the Item is a Project, or from ItemList, or null if
     * there are no more.
     */
//        private void findNextTimerItem(Item.Condition condition) {
    void findNextTimerItem(Condition condition) {
        timedItem = nextItem; //use previously found item as next
        nextItem = null;
        resetValuesBeforeNewTimedItem();
        if (sourceItemOrProject != null) { //Timer is running on a single Item (possibly project)
//            nextItem = sourceItemOrProject.getNextLeafItem(timedItem, condition); //now timedItem==entry.nextItem
            nextItem = sourceItemOrProject.getNextLeafItem(timedItem); //now timedItem==entry.nextItem
            return;
        } else if (itemList != null) { //Timer is running on an ItemList
            boolean[] previousItemAlreadyFound = new boolean[]{timedItem == null};
            for (int i = itemListIndex, size = itemList.getSize(); i < size; i++) {
//                Item tempItem = ((Item) itemList.getItemAt(i)).getNextLeafItemMeetingConditionImpl(timedItem, condition, previousItemAlreadyFound);
//                Item tempItem = ((Item) itemList.getItemAt(i)).getNextLeafItem(timedItem);
                Item tempItem = ((Item) itemList.get(i)).getNextLeafItem(timedItem); //use get(), not getItemAt(), to only time filtered values
                if (tempItem != null) {
                    nextItem = tempItem; //store nextItem for next time
                    itemListIndex = i;//+1; //next time, continue with next index, NO, continue with same item (can be a project)
                    return;
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    void findNextTimerItemORG(Condition condition) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (timedItem != null && nextItem == null) {
////            timedItem = null; //no next item was found last time, so nothing more to run Timer on
////            return;
////        }
////        entry=this;
//
////            if (timedItem == null && nextItem == null) {
////                //on very first call, we need to initialize both
////                findNextTimerItem(condition);
////            }
////</editor-fold>
////        Item prevTimedItem = timedItem;
//        timedItem = nextItem; //use previously found item as next
//        nextItem = null;
//        resetValuesBeforeNewTimedItem();
////            if (timedItem == null) {
////                return; //no next item was found last time, so nothing more to run Timer on
////            }
//        if (sourceItemOrProject != null) { //Timer is running on a single Item (possibly project)
////            timedItem = item.getNextLeafItem(timedItem, item -> isSuitableItemForTimer(item));
////            nextItem = item.getNextLeafItem(prevTimedItem, condition);
//            nextItem = sourceItemOrProject.getNextLeafItem(timedItem, condition); //now timedItem==entry.nextItem
////            resetValues();
//            return;
//        } else if (itemList != null) { //Timer is running on an ItemList
////            if (!itemList.getItemAt(itemListIndex).equals(timedItem)) {
////                //the ItemList has changed so the previous item is no longer at the to attempt to find position of previous item
////                //TODO
////            }
////DONE!!!  support different algorithms for picking the first --> simply use the sorting order from the previous screen!
//            boolean[] previousItemAlreadyFound = new boolean[]{timedItem == null};
//            for (int i = itemListIndex, size = itemList.size(); i < size; i++) {
////                Item tempItem = ((Item) itemList.getItemAt(itemListIndex)).getNextLeafItem(timedItem, item -> isSuitableItemForTimer(item));
////                Item tempItem = ((Item) itemList.getItemAt(i)).getNextLeafItem(prevTimedItem, condition);
//                Item tempItem = ((Item) itemList.getItemAt(i)).getNextLeafItemMeetingConditionImpl(timedItem, condition, previousItemAlreadyFound);
////                if (tempItem != null && !tempItem.equals(prevTimedItem)) {
//                if (tempItem != null) {
//                    nextItem = tempItem; //store nextItem for next time
//                    itemListIndex = i;//+1; //next time, continue with next index, NO, continue with same item (can be a project)
////                    resetValues();
//                    return;
//                }
//            }
//        }
////        nextItem = null; //no next item found
////        resetValues();
//    }
//</editor-fold>
    /**
     * initializes both timedItem and nextTime (by calling findNextTimerItem
     * twice). NB. Made public to allow instantiation by CN1
     *
     * @param condition
     */
    public void initializeTimedItems(Condition condition) {
        //initialize by calling findNextTimerItem twice to set both timedItem and nextItem 
        findNextTimerItem(condition);
        findNextTimerItem(condition);
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public void externalize(DataOutputStream out) throws IOException {

        if (timedItem != null && timedItem.getObjectIdP() == null) {
            DAO.getInstance().saveInBackground(timedItem); //if needed, save temporary item (interupt task) so it is kept if app is closed and can be recovered on startup (and possibly deleted if Timer exited using Cancelled)
            timedItemSavedLocallyInTimer = true;
        } else {
            timedItemSavedLocallyInTimer = false;
        }

//        out.writeBoolean(timedItem != null && timedItem.getObjectId() != null);
        if (timedItem != null && timedItem.getObjectIdP() != null) {
            out.writeBoolean(true);
            Util.writeUTF(timedItem.getObjectIdP(), out);
        } else {
            out.writeBoolean(false);
        }

//        out.writeBoolean(timedItem != null && timedItem.getObjectId() != null);
        if (nextItem != null && nextItem.getObjectIdP() != null) {
            out.writeBoolean(true);
            Util.writeUTF(nextItem.getObjectIdP(), out);
        } else {
            out.writeBoolean(false);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        out.writeBoolean(item != null && item.getObjectId() != null);
//        if (item != null) {
//            Util.writeObject(item.getObjectId(), out);
//        }
//</editor-fold>
        if (sourceItemOrProject != null && sourceItemOrProject.getObjectIdP() != null) {
            out.writeBoolean(true);
            Util.writeUTF(sourceItemOrProject.getObjectIdP(), out);
        } else {
            out.writeBoolean(false);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        out.writeBoolean(orgItemList != null && orgItemList.getObjectId() != null);
//        if (orgItemList != null) {
//            Util.writeObject(orgItemList.getObjectId(), out);
//        }
//</editor-fold>
//        if (orgItemList != null && orgItemList.getObjectId() != null) {
        if (itemList != null && itemList.getObjectIdP() != null) {
            out.writeBoolean(true);
//            Util.writeUTF(orgItemList.getObjectId(), out);
            Util.writeUTF(itemList.getObjectIdP(), out);
        } else {
            out.writeBoolean(false);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        out.writeBoolean(filter != null && filter.getObjectId() != null);
//        if (filter != null) {
//            Util.writeObject(filter.getObjectId(), out);
//        }
//</editor-fold>
//        if (filter != null && filter.getObjectId() != null) {
//            out.writeBoolean(true);
//            Util.writeUTF(filter.getObjectId(), out);
//        } else {
//            out.writeBoolean(false);
//        }

        out.writeInt(itemListIndex);
//        out.writeBoolean(interruptOrInstantTask);
        out.writeLong(virtualTimerStartTimeMillis);
        out.writeBoolean(timerShowsActualTotal);
        out.writeLong(timerElapsedTimeSavedDuringPauseMillis);
//        out.writeLong(previouslyRunningTimersCountSoFarMillis);
        out.writeBoolean(wasTimerRunningWhenInterrupted);
        out.writeBoolean(timedItemSavedLocallyInTimer);
        out.writeLong(savedSecondsFromTimerInMillis);
    }

    @Override
    public void internalize(int version, DataInputStream in) throws IOException {
        if (in.readBoolean()) {
//            timedItem = DAO.getInstance().getItem(in.readUTF());
            timedItem = DAO.getInstance().fetchItem(Util.readUTF(in));
        }
        if (in.readBoolean()) {
//            timedItem = DAO.getInstance().getItem(in.readUTF());
            nextItem = DAO.getInstance().fetchItem(Util.readUTF(in));
        }
        if (in.readBoolean()) {
            sourceItemOrProject = DAO.getInstance().fetchItem(Util.readUTF(in));
        }
        if (in.readBoolean()) {
//            orgItemList = DAO.getInstance().fetchItemList(Util.readUTF(in));
            itemList = DAO.getInstance().fetchItemList(Util.readUTF(in));
        }

//        if (in.readBoolean()) {
//            filter = DAO.getInstance().getFilterSortDef(Util.readUTF(in));
//        }
        itemListIndex = in.readInt();
//        interruptOrInstantTask = in.readBoolean();
        virtualTimerStartTimeMillis = in.readLong();
        timerShowsActualTotal = in.readBoolean();
        timerElapsedTimeSavedDuringPauseMillis = in.readLong();
//        previouslyRunningTimersCountSoFarMillis = in.readLong();
        wasTimerRunningWhenInterrupted = in.readBoolean();
        timedItemSavedLocallyInTimer = in.readBoolean();
        savedSecondsFromTimerInMillis = in.readLong();

//        setFilteredLists(orgItemList, filter);
//        setFilteredLists(orgItemList);
//        this.orgItemList = orgItemList;
//        this.itemList = orgItemList;//        if (itemList != null && itemList.getObjectId() != null) {
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME_TIMER_STACK_ENTRY;
    }

}
