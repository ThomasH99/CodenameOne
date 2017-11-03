/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Split btw WorkTimeDef and ItemList: WTD calculates work slots and finds
 * date/time corresponding to a workload sum; recalculated if work slots change.
 * IL adds an additional structure to calculate sum of workload for each item,
 * indexed by index. IL adds a convenience method to get start/endWorkTime for
 * each item (based on Index). IL can calculate this for subItems
 * (Projects)/Sublists/Categories.
 *
 * Maintains a list of sorted, non-overlapping slots. The list is created by
 * combining manually defined slots (dated or not, possibly repeating) and slots
 * imported from Calendar. The basic function is to look up with a certain
 * amount of effort and then get the time/date back at which point this sum can
 * be achieved. E.g. if work load sum of the tasks preceding a taskA is 130h,
 * then the result can be that the task can start 30min into workSlotB, which
 * could be June12, 14:35. If the effort of taskA is 3h, the work load sum where
 * it can finish is 133h and it may finish 45min into workSlotC which could be
 * June13, 9:45. The key operations are: recalculate slots when slots are edited
 * or added/removed (infrequent); look up (very frequent since used to display
 * lists of tasks). Implementation phases: define basic (possibly overlapping)
 * slots: recalculate completely from invalidated slot. . Possibly to find an
 * algorithm to avoid recalculating everything *after* an added/removed/changed
 * slot? E.g. once the sum is calculated, adding/removing/changing simply Time 1
 * 3 8 12 1719 26 30 AAA BBBBB CCC DDDDD Sum 0 3 4 7 8910 11 15
 *
 * Lookup(sum=6) => (time=10;slot=slotB;starts-into-slot=2) Lookup(sum=9) =>
 * (time=18;slot=slotC;starts-into-slot=1)
 *
 *
 * Simple algorithm (prioritizing the slot starting the earliest, no negative
 * slots): sort by starting time, ignore slots with start or end time before NOW
 * (remove them from WorkDef as they're detected?!), to find sum, iterate over
 * slots, add up sum, if overlapping slots, use max(slotA-end-time,
 * slotB-start-time) which will ignore the part of following slots that started
 * before max-time. Should work both for multiple overlapping slots, and very
 * long slots overlapping many following ones.
 *
 * Another (lazy) algorithm: assuming that you (mainly) scroll back&forth in the
 * same list (and usually start scrolling from the top, so can calculate lazily
 * as you scroll down) is to simply keep the already looked up values with
 * results to quickly serve them back. E.g. in example above, store sum=6 and
 * sum=9 with the given results. Assumes any change to the work definition can
 * be reliably detected to invalidate such stored results.
 *
 * Each workslot can have a name to give feedback to the user on in which slot a
 * task is being started or finished. Use 'Interval Trees' for more efficient
 * search: https://en.wikipedia.org/wiki/Interval_tree
 * http://algs4.cs.princeton.edu/93intersection/IntervalST.java.html,
 *
 * Indexed tree map: * https://code.google.com/p/indexed-tree-map/ Java 8 Sorted
 * List:
 * http://docs.oracle.com/javase/8/javafx/api/javafx/collections/transformation/SortedList.html
 *
 * How are slots calculated when iterating through a vector with many slot
 * definitions: ____ add ... keep
 *
 * ... discard ____ keep
 *
 * ________ combine both and keep ________ ________ do ___
 *
 * ....... combine both and keep ...... ....... do ...
 *
 * ________ add first part .... keep
 *
 * ________ keep last part .... keep
 *
 * ........ keep ____ discard
 *
 * ........ keep ____ discard
 *
 * Processing of undated slots u (---): ___ ____ s1 s2 --- u.end = s2.start
 *
 * __ ____ s1 s2 -- -- u1.end = u2.end = s2.start maintains a list of work slots
 * (time intervals in which work can be done). Overlapping positive slots are
 * not double-counted. Negative slots always overrule positive slots. Adjacent
 * slots (next starting at exactly the time the previous stops) are combined
 *
 *
 *
 * @author Thomas
 */
public class WorkTimeDefinitionOLD { //implements Externalizable { //extends ItemList {

    //TODO only calculate finish time for tasks that New/Ongoing/Waiting (not Done or Cancelled) - introduce a method on Item to determine if electable for calculation, e.g. isCalculateWorkTime()?)
    /**
     * initialized with a list of workslots and a list of elements (Item or
     * ItemList). Will calculate (ideally lazily) the start and finish time of
     * each element and store this.
     *
     * contains the source slots (either defined manually or imported from PIM).
     * Ordered by startDate. From these the available time slots are calculated
     * and added to the WorkSlotList (and then available via
     */
//    private List<WorkSlot> workSlots;// = new ItemList(); //lazy
    private WorkSlotList workSlots;// = new ItemList(); //lazy
    private List<Item> elementList;
    private List<Item> items;
//    private ItemAndListCommonInterface orgItemOrList;

    private List<WorkTimeInfo> workSlotInfoList; // = new ArrayList();
    private List<WorkTime> workSlotInfoListNew; // = new ArrayList();
//    private Hashtable<Item, WorkTimeInfo> workSlotInfoHashTable; // = new Hashtable();
    private Hashtable<ItemAndListCommonInterface, WorkTimeInfo> workSlotInfoHashTable; // = new Hashtable();

    private class WorkTimeInfo {

        /**
         * when does the task with this index start
         */
        private long startTime;
        /**
         * when does the task with this index finish
         */
        private long finishTime;
        /**
         * in which workSlot does the task with this index start
         */
        private WorkSlot firstWorkSlot;
        /**
         * in which workSlot does the task with this index finish
         */
        private WorkSlot lastWorkSlot;
        /**
         * the index of the workSlot in which the task with this index finishes
         */
        private int indexOfLastWorkSlot;
        /**
         * how much time is left of the last workSlot
         */
        private long remainingWorkSlotDuration;

        WorkTimeInfo() {
            startTime = 0;
            finishTime = 0;
            firstWorkSlot = null;
            lastWorkSlot = null;
            indexOfLastWorkSlot = -1;
            remainingWorkSlotDuration = 0;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeDefinition(List<WorkSlot> workSlotList, List listOfItemsOrItemLists) {
//        if (workSlotList != null) {
//            this.workSlots = workSlotList;
//        } else {
//            this.workSlots = new ArrayList<WorkSlot>();
//        }
//        this.elementList = listOfItemsOrItemLists;
////        refresh();
//    }
//</editor-fold>
//    WorkTimeDefinition(List listOfItemsOrItemListsFilteredSorted, ItemAndListCommonInterface orgItemOrList) {
//    WorkTimeDefinition(List listOfItemsOrItemListsFilteredSorted, List<WorkSlot> workSlots) {
    WorkTimeDefinitionOLD(List listOfItemsOrItemListsFilteredSorted, WorkSlotList workSlots) {
        this.elementList = listOfItemsOrItemListsFilteredSorted;
//        this.orgItemOrList = orgItemOrList;
        this.workSlots = workSlots;
//        init();
        workSlotInfoList = new ArrayList();
        workSlotInfoHashTable = new Hashtable();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * reset the list from the given index, eg if an element at index was
     * changed, making the existing calculation for the rest of the list
     * invalid.
     *
     * @param indexFromWhichToResetTheList
     */
    private void refresh(int indexFromWhichToResetTheList) {
        workSlotInfoList.subList(indexFromWhichToResetTheList, workSlotInfoList.size()).clear(); //to refresh simply clear the part of the list to force recalculation
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void refresh() {
//        refresh(0);
//    }
    /**
     * resets
     */
//    private void init(boolean reloadWorkSlots) {
//    private void init() {
////        if (workSlots == null || reloadWorkSlots) {
////            workSlots = DAO.getInstance().getWorkSlots(orgItemOrList);
////        }
//        workSlotInfoList = new ArrayList();
//        workSlotInfoHashTable = new Hashtable();
//        refresh(0);
//    }
//    public void reset() {
//        reset(true);
//    }
//</editor-fold>
    /**
     * returns the finishtime of the element (Item/ItemList) at index. 0 if
     * there is no workslot in which the task can be finished.
     *
     * @param index
     * @return
     */
//    public long getFinishTime(int index) {
////        return calculateWorkTime(index).finishTime;
//        WorkTimeInfo workTimeInfo = calculateWorkTime(index);
//        if (workTimeInfo != null) {
//            return workTimeInfo.finishTime;
//        } else {
//            return 0;
//        }
//    }

//    public long getFinishTime(Item item) {
//    public long getFinishTime(ItemAndListCommonInterface elt) {
//        if (elt == null) {
//            return 0;
//        }
//        WorkTimeInfo workTimeInfo;
//        if ((workTimeInfo = workSlotInfoHashTable.get(elt)) != null) {
//            return workTimeInfo.finishTime;
//        } else {
//            return getFinishTime(elementList.indexOf(elt));
//        }
//    }

    /**
     * returns the start time of the element (Item/ItemList) at index. 0 if
     * there is no workslot in which the task can start.
     *
     * @param index
     * @return
     */
//    public long getStartTime(int index) {
////        return calculateWorkTime(index).startTime;
//        WorkTimeInfo workTimeInfo = calculateWorkTime(index);
//        if (workTimeInfo != null) {
//            return workTimeInfo.startTime;
//        } else {
//            return 0;
//        }
//    }

    /**
     * returns the workslot in which the task will start, null if there is no
     * workslot (getStartTime will then return 0)
     *
     * @param index
     * @return
     */
//    public WorkSlot getFirstWorkSlot(int index) {
////        return calculateWorkTime(index).firstWorkSlot;
//        WorkTimeInfo workTimeInfo = calculateWorkTime(index);
//        if (workTimeInfo != null) {
//            return workTimeInfo.firstWorkSlot;
//        } else {
//            return null;
//        }
//    }

    /**
     * returns the workslot in which the task will finish, null if there is no
     * workslot (getFinishTime will then return 0)
     *
     * @param index
     * @return
     */
//    public WorkSlot getLastWorkSlot(int index) {
////        return calculateWorkTime(index).lastWorkSlot;
//        WorkTimeInfo workTimeInfo = calculateWorkTime(index);
//        if (workTimeInfo != null) {
//            return workTimeInfo.lastWorkSlot;
//        } else {
//            return null;
//        }
//    }

    /**
     * set a new list of WorkSlots, call refresh() after setting to calculate
     * new values for getFinishTime and other functions.
     *
     * @param workSlots
     */
//    private void setWorkSlotListXX(List<WorkSlot> workSlots) {
    private void setWorkSlotListXX(WorkSlotList workSlots) {
        this.workSlots = workSlots;
    }

//    private List<WorkSlot> getWorkSlotList() {
    private WorkSlotList getWorkSlotList() {
        return workSlots;
    }

    public List getElementList() {
        return elementList;
    }

    /**
     * set a new list of elements (Items/ItemList), call refresh() after setting
     * to calculate new values for getFinishTime and other functions.
     *
     * @param workSlots is a list of either Items or ItemLists //TODO: create an
     * interface for extracting remainingEffort
     */
//    public void setElementListXXX(List listOfItemsOrItemLists) {
//        this.elementList = listOfItemsOrItemLists;
////        refresh(true);
//    }
    /**
     * will get the next workSlot to use, starting at workSlotIndex. Will skip
     * workSlots that start at same time and only use the longest (this assumes
     * that workSlots is sorted by duration). Returns null if no more WorkSLots.
     *
     * @param workSlotIndex
     * @return
     */
    private WorkSlot getNextWorkSlot(Integer workSlotIndex) {
        WorkSlot workSlot = null;
        if (workSlotIndex < workSlots.size()) {
            //more workslots available
            workSlot = workSlots.get(workSlotIndex);
            //if several workSlots start at the same time, skip to the last (assuming that they are sorted by duration, so the longest is last)
            while (workSlotIndex + 1 < workSlots.size() && workSlots.get(workSlotIndex + 1).getStartTimeD() == workSlot.getStartTimeD()) {
                workSlotIndex++;
                workSlot = workSlots.get(workSlotIndex);
            }
        }
        return workSlot;
    }

    /**
     * for a given duration (starting from earliest available workslot [after
     * NOW]), find the date on which that duration is reached. TODO: replace
     * with more efficient algorithm (index on sum of slots).
     *
     * WorkSlots can overlap. We always start with the earliest workSlot, and
     * use all its duration first. So later slots overlapping will have their
     * duration reduced by the overlap with the earlier ones. If several
     * WorkSlots start at the same time, we use the one with the longest
     * duration and ignore the other ones.
     *
     * @param duration
     */
//    private WorkTimeInfo calculateWorkTimeNew(WorkTime availableWorkTime, ItemAndListCommonInterface elt) {
//
//        if (indexOfTask < 0 || workSlots.size() == 0) {
//            return null;
//        }
//        if (indexOfTask < workSlotInfoListNew.size()) {
//            return workSlotInfoListNew.get(indexOfTask); //if this entry is already calculated return it
//        } else {
//            ItemAndListCommonInterface elt = elementList.get(indexOfTask);
//            if (elt.getWorkTimeDefinition() != this) {
//                return null; //don't calculate if elt belongs to another workTime owner
//            } else {
////                WorkTimeInfo newWorkTimeInfo = new WorkTimeInfo(); //new workTimeInfo to build for the task at indexOfTask
//                WorkTime newWorkTimeInfo = new WorkTime(); //new workTimeInfo to build for the task at indexOfTask
//                WorkSlot workSlot; //TODO move all this init code down below else below
//                int workSlotIndex;
//                long remainingWorkSlotDuration;
//                long lastFinishTime;
////                WorkTimeInfo prevWorkTimeInfo;
//                WorkTime prevWorkTimeInfo;
//                long now = new Date().getTime(); //use the same now in all adjustments of startTime below //TODO should be set globally instead of each iteration** of call
//
////            if (indexOfTask == -1) { //TODO: remove (already tested above)
////                return null; //null indicates that we've reached the end of the recursion
////<editor-fold defaultstate="collapsed" desc="comment">
////create first (artificial) workSlotInfo to initiate the recursive calculation
////                if (workSlots != null && workSlots.size() > 0) {
////                    //use first WorkSlot
////                    newWorkTimeInfo.indexOfLastWorkSlot = 0;
////                    WorkSlot wSlot = workSlots.get(newWorkTimeInfo.indexOfLastWorkSlot);
////                    newWorkTimeInfo.firstWorkSlot = wSlot;
////                    newWorkTimeInfo.lastWorkSlot = wSlot;
////                    newWorkTimeInfo.finishTime = wSlot.getStartAdjusted(now);
////                    newWorkTimeInfo.remainingWorkSlotDuration = wSlot.getDurationAdjusted(now);
////                }
////else //return the default values of newWorkTimeInfo is there are no workSlots in the list
////            workSlotInfoList.add(newWorkTimeInfo); // DON'T store since artificial to initiate the recursive
////                return newWorkTimeInfo;
////</editor-fold>
////            } else {
//                //get previous workSlotInfo
//                prevWorkTimeInfo = calculateWorkTime(indexOfTask - 1); //will return the previous workSlotInfo (and recursively calculate earlier ones and add them to workSlotInfoList
//                workSlotInfoListNew.add(newWorkTimeInfo); // add the new workTimeInfo to the list (NB! do after the call to calculateWorkTime to make sure recursively calculated entries are added first)
////            Item elt = elementList.get(indexOfTask);
////                ItemAndListCommonInterface elt = elementList.get(indexOfTask);
//                workSlotInfoHashTable.put(elt, newWorkTimeInfo);
//                if (prevWorkTimeInfo == null) {
//                    //if now previous workTimeInfo, initialize with first workSlot
//                    workSlotIndex = 0;
//                    workSlot = workSlots.get(workSlotIndex);
//                    lastFinishTime = workSlot.getStartAdjusted(now);
//                    remainingWorkSlotDuration = workSlot.getDurationAdjusted(now);
//                } else {
//                    //initialize with values from previous workTimeInfo
//                    workSlotIndex = prevWorkTimeInfo.indexOfLastWorkSlot;
//                    workSlot = prevWorkTimeInfo.lastWorkSlot;
//                    remainingWorkSlotDuration = prevWorkTimeInfo.remainingWorkSlotDuration;
//                    lastFinishTime = prevWorkTimeInfo.finishTime;
//                }
//
//                //get duration of element
////                Object elt = elementList.get(indexOfTask);
////                long remainingDuration = (elt instanceof Item) ? ((Item) elt).getRemainingEffort() : ((ItemList) elt).getRemainingEffort(); //TODO generalize to ItemInterface
////            long remainingDuration = ((ItemAndListCommonInterface) elt).getRemainingEffort(); //TODO generalize to ItemInterface
//                long remainingDuration = elt.getRemainingEffort(); //TODO generalize to ItemInterface
//
//                //iterate as long as there are workSlots, break the loop by returning newWorkTimeInfo as soon as enough workSlots are found to complete the task
//                while (workSlotIndex < workSlots.size()) { //duration can be 0, the task should still get a start and finish time
//                    //initialize with first workslot that has remaning timeif there is time remaining
////                    if (newWorkTimeInfo.firstWorkSlot == null && remainingWorkSlotDuration > 0) {
//                    if (remainingWorkSlotDuration > 0) {
////                        newWorkTimeInfo.firstWorkSlot = workSlot;
//                        newWorkTimeInfo.workSlots.add(workSlot);
//                    }
//                    //if there is remaining time, then we know that the task can start here (remainingTime could be 0 if used up completely by a previous task, or if the duration of the workSlot is 0)
//                    if (newWorkTimeInfo.startTime == 0 && remainingWorkSlotDuration > 0) {
//                        newWorkTimeInfo.startTime = lastFinishTime;
//                    }
//                    //if duration<remaining, then deduct duration; if remaining<duration, then deduct remaining (<=> set remaining to 0)
//                    long deduct = Math.min(remainingWorkSlotDuration, remainingDuration);
//                    remainingWorkSlotDuration -= deduct; //deduct the smallest
//                    remainingDuration -= deduct;
//
//                    if (remainingDuration == 0) { // <=> is completely covered (reduced to 0, or 0 from the start)
//                        //we can complete the task completely within this workSlot 
////                        newWorkTimeInfo.lastWorkSlot = workSlot;
//                        newWorkTimeInfo.indexOfLastWorkSlot = workSlotIndex;
//                        newWorkTimeInfo.remainingWorkSlotDuration = remainingWorkSlotDuration; //store the remainingTime of this workslot
//                        newWorkTimeInfo.finishTime = workSlot.getEndTime() - remainingWorkSlotDuration;
////                        return newWorkTimeInfo;
//                        elt.setWorkTime(newWorkTimeInfo);
//                    } else {
//                        //cannot complete the task within this workSlot (still duration to cover), try to get additional workSlot(s)
//                        WorkSlot prevWorkSlot = workSlot; //keep prev WorkSlot to know if its endTime is higher than the startTime of the next WorkSlot
//                        workSlotIndex++;
//                        workSlot = null;
////                        workSlot = getNextWorkSlot(workSlotIndex); //will increment workSlotIndex
//                        if (workSlotIndex < workSlots.size()) {
//                            //more workslots available, get next
//                            workSlot = workSlots.get(workSlotIndex);
//                            //if several workSlots start at the same time, skip to the last (assuming that they are sorted by duration, so the longest is last)
//                            while (workSlotIndex + 1 < workSlots.size() && workSlots.get(workSlotIndex + 1).getStartTimeD() == workSlot.getStartTimeD()) {
//                                workSlotIndex++;
//                                workSlot = workSlots.get(workSlotIndex);
//                            }
//                        }
//                        if (workSlot != null) {
//                            //if a new slot is found
//                            remainingWorkSlotDuration += workSlot.getDurationAdjusted(Math.max(prevWorkSlot.getEndTime(), now)); //add new WorkSlot's duration
//                        }
//                    } //end else //still duration to cover
//                } //while
////            }
//                newWorkTimeInfo.indexOfLastWorkSlot = workSlotIndex; //store just in case
//                newWorkTimeInfo.remainingWorkSlotDuration = remainingWorkSlotDuration; //store remaining time of last workslot (will always be zero?)
//                //else : no more workslots available
//                //we didn't manage to complete the task within the available workslots
//                return newWorkTimeInfo; //initialized with default values in WorkTimeInfo constructor
//            }
//        }
//    }

//    private WorkTimeInfo calculateWorkTime(int indexOfTask) {
//
//        if (indexOfTask < 0 || workSlots.size() == 0) {
//            return null;
//        }
//        if (indexOfTask < workSlotInfoList.size()) {
//            return workSlotInfoList.get(indexOfTask); //if this entry is already calculated return it
//        } else {
//            ItemAndListCommonInterface elt = elementList.get(indexOfTask);
//            if (elt.getWorkTimeDefinition() != this) {
//                return null; //don't calculate if elt belongs to another workTime owner
//            } else {
//                WorkTimeInfo newWorkTimeInfo = new WorkTimeInfo(); //new workTimeInfo to build for the task at indexOfTask
//                WorkSlot workSlot; //TODO move all this init code down below else below
//                int workSlotIndex;
//                long remainingWorkSlotDuration;
//                long lastFinishTime;
//                WorkTimeInfo prevWorkTimeInfo;
//                long now = new Date().getTime(); //use the same now in all adjustments of startTime below //TODO should be set globally instead of each iteration** of call
//
////            if (indexOfTask == -1) { //TODO: remove (already tested above)
////                return null; //null indicates that we've reached the end of the recursion
////<editor-fold defaultstate="collapsed" desc="comment">
////create first (artificial) workSlotInfo to initiate the recursive calculation
////                if (workSlots != null && workSlots.size() > 0) {
////                    //use first WorkSlot
////                    newWorkTimeInfo.indexOfLastWorkSlot = 0;
////                    WorkSlot wSlot = workSlots.get(newWorkTimeInfo.indexOfLastWorkSlot);
////                    newWorkTimeInfo.firstWorkSlot = wSlot;
////                    newWorkTimeInfo.lastWorkSlot = wSlot;
////                    newWorkTimeInfo.finishTime = wSlot.getStartAdjusted(now);
////                    newWorkTimeInfo.remainingWorkSlotDuration = wSlot.getDurationAdjusted(now);
////                }
////else //return the default values of newWorkTimeInfo is there are no workSlots in the list
////            workSlotInfoList.add(newWorkTimeInfo); // DON'T store since artificial to initiate the recursive
////                return newWorkTimeInfo;
////</editor-fold>
////            } else {
//                //get previous workSlotInfo
//                prevWorkTimeInfo = calculateWorkTime(indexOfTask - 1); //will return the previous workSlotInfo (and recursively calculate earlier ones and add them to workSlotInfoList
//                workSlotInfoList.add(newWorkTimeInfo); // add the new workTimeInfo to the list (NB! do after the call to calculateWorkTime to make sure recursively calculated entries are added first)
////            Item elt = elementList.get(indexOfTask);
////                ItemAndListCommonInterface elt = elementList.get(indexOfTask);
//                workSlotInfoHashTable.put(elt, newWorkTimeInfo);
//                if (prevWorkTimeInfo == null) {
//                    //if now previous workTimeInfo, initialize with first workSlot
//                    workSlotIndex = 0;
//                    workSlot = workSlots.get(workSlotIndex);
//                    lastFinishTime = workSlot.getStartAdjusted(now);
//                    remainingWorkSlotDuration = workSlot.getDurationAdjusted(now);
//                } else {
//                    //initialize with values from previous workTimeInfo
//                    workSlotIndex = prevWorkTimeInfo.indexOfLastWorkSlot;
//                    workSlot = prevWorkTimeInfo.lastWorkSlot;
//                    remainingWorkSlotDuration = prevWorkTimeInfo.remainingWorkSlotDuration;
//                    lastFinishTime = prevWorkTimeInfo.finishTime;
//                }
//
//                //get duration of element
////                Object elt = elementList.get(indexOfTask);
////                long remainingDuration = (elt instanceof Item) ? ((Item) elt).getRemainingEffort() : ((ItemList) elt).getRemainingEffort(); //TODO generalize to ItemInterface
////            long remainingDuration = ((ItemAndListCommonInterface) elt).getRemainingEffort(); //TODO generalize to ItemInterface
//                long remainingDuration = elt.getRemainingEffort(); //TODO generalize to ItemInterface
//
//                //iterate as long as there are workSlots, break the loop by returning newWorkTimeInfo as soon as enough workSlots are found to complete the task
//                while (workSlotIndex < workSlots.size()) { //duration can be 0, the task should still get a start and finish time
//                    //initialize with first workslot that has remaning timeif there is time remaining
//                    if (newWorkTimeInfo.firstWorkSlot == null && remainingWorkSlotDuration > 0) {
//                        newWorkTimeInfo.firstWorkSlot = workSlot;
//                    }
//                    //if there is remaining time, then we know that the task can start here (remainingTime could be 0 if used up completely by a previous task, or if the duration of the workSlot is 0)
//                    if (newWorkTimeInfo.startTime == 0 && remainingWorkSlotDuration > 0) {
//                        newWorkTimeInfo.startTime = lastFinishTime;
//                    }
//                    //if duration<remaining, then deduct duration; if remaining<duration, then deduct remaining (<=> set remaining to 0)
//                    long deduct = Math.min(remainingWorkSlotDuration, remainingDuration);
//                    remainingWorkSlotDuration -= deduct; //deduct the smallest
//                    remainingDuration -= deduct;
//
//                    if (remainingDuration == 0) { // <=> is completely covered (reduced to 0, or 0 from the start)
//                        //we can complete the task completely within this workSlot 
//                        newWorkTimeInfo.lastWorkSlot = workSlot;
//                        newWorkTimeInfo.indexOfLastWorkSlot = workSlotIndex;
//                        newWorkTimeInfo.remainingWorkSlotDuration = remainingWorkSlotDuration; //store the remainingTime of this workslot
//                        newWorkTimeInfo.finishTime = workSlot.getEndTime() - remainingWorkSlotDuration;
//                        return newWorkTimeInfo;
//                    } else {
//                        //cannot complete the task within this workSlot (still duration to cover), try to get additional workSlot(s)
//                        WorkSlot prevWorkSlot = workSlot; //keep prev WorkSlot to know if its endTime is higher than the startTime of the next WorkSlot
//                        workSlotIndex++;
//                        workSlot = null;
////                        workSlot = getNextWorkSlot(workSlotIndex); //will increment workSlotIndex
//                        if (workSlotIndex < workSlots.size()) {
//                            //more workslots available, get next
//                            workSlot = workSlots.get(workSlotIndex);
//                            //if several workSlots start at the same time, skip to the last (assuming that they are sorted by duration, so the longest is last)
//                            while (workSlotIndex + 1 < workSlots.size() && workSlots.get(workSlotIndex + 1).getStartTimeD() == workSlot.getStartTimeD()) {
//                                workSlotIndex++;
//                                workSlot = workSlots.get(workSlotIndex);
//                            }
//                        }
//                        if (workSlot != null) {
//                            //if a new slot is found
//                            remainingWorkSlotDuration += workSlot.getDurationAdjusted(Math.max(prevWorkSlot.getEndTime(), now)); //add new WorkSlot's duration
//                        }
//                    } //end else //still duration to cover
//                } //while
////            }
//                newWorkTimeInfo.indexOfLastWorkSlot = workSlotIndex; //store just in case
//                newWorkTimeInfo.remainingWorkSlotDuration = remainingWorkSlotDuration; //store remaining time of last workslot (will always be zero?)
//                //else : no more workslots available
//                //we didn't manage to complete the task within the available workslots
//                return newWorkTimeInfo; //initialized with default values in WorkTimeInfo constructor
//            }
//        }
//    }

    private static WorkTime getWorkTimeFromSlots(List<WorkSlot> workSlots, Integer workSlotStartIndex, long startTime, long remainingDuration) {
//        long startTime = Long.MIN_VALUE;
        long endTime = Long.MIN_VALUE;
        List<WorkSlot> usedWorkSlots = new ArrayList();
        while (remainingDuration > 0 && workSlotStartIndex < workSlots.size()) {
            WorkSlot workSlot = workSlots.get(workSlotStartIndex); //get next workSlot
            if (workSlot.getEndTime() <= startTime) { //if first workslot ends before startTime, skip to next one
                workSlotStartIndex++;
                continue;
            }
            startTime = workSlot.getStartAdjusted();
            if (startTime + remainingDuration <= workSlot.getEndTime()) {
                remainingDuration = 0;
                endTime = startTime + remainingDuration;
            } else {
                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted();
                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
                workSlotStartIndex++;
            }
            usedWorkSlots.add(workSlot);
        }
        return new WorkTime(workSlots, startTime, endTime, remainingDuration);
    }

    public void resetCachedValues(WorkSlot changedWorkSlot) {
        //find 
    }

    public void resetCachedValuesOnWorkSlotChange(int indexOfChangedWorkSlot) {
        //find 
    }

    public void resetCachedValues(ItemAndListCommonInterface item) {

    }

    /**
     * call whenever an Item changes its remaining time (eg gets Done or
     * estimates updated) or if order of items change in list (drag and drop),
     * new item inserted into list, ...
     *
     * @param itemIndex index for item that has changed or first index where a
     * new item has been inserted
     */
    public void resetCachedValuesOnItemChange(int itemIndex) {
        workTimeCache.removeAll(workTimeCache.subList(itemIndex, workTimeCache.size() - 1));
    }

    List<WorkTime> workTimeCache = new ArrayList();

//    private WorkTime getWorkTimeFromSlotsNew( int itemIndex, List<WorkSlot> workSlots) {
//        if (itemIndex < workTimeCache.size()) {
//            return workTimeCache.get(itemIndex);
//        } else {
//            WorkTime workTime = getWorkTimeFromSlotsNew(items, itemIndex, workSlots);
//            workTimeCache.add(workTime);
//            return workTime;
//        }
//    }

    private static WorkTime getWorkTimeFromSlotsNew(List<ItemAndListCommonInterface> items, int itemIndex, List<WorkSlot> workSlots) {
//        long startTime = Long.MIN_VALUE;
        //get cached values
//        WorkTime workTime;
//        if (itemIndex < workTimeCache.size()) {
//            return workTimeCache.get(itemIndex);
//        }
        //calculate new values
//        ItemAndListCommonInterface item = items.get(itemIndex);
        long finishTime; // = MyDate.MIN_DATE;
        Integer startWorkSlotIndex; // = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index

        if (itemIndex > 0) {
            WorkTime prevWorkTime = getWorkTimeFromSlotsNew(items, itemIndex - 1, workSlots);
            finishTime = prevWorkTime.getFinishTime();
            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
        } else {
            finishTime = MyDate.MIN_DATE;
            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
        }

        return getWorkTimeFromSlots(workSlots, startWorkSlotIndex, finishTime, items.get(itemIndex).getRemainingEffort());

//        long endTime = Long.MIN_VALUE;
//        List<WorkSlot> usedWorkSlots = new ArrayList();
//        while (remainingDuration > 0 && workSlotStartIndex < workSlots.size()) {
//            WorkSlot workSlot = workSlots.get(workSlotStartIndex); //get next workSlot
//            if (workSlot.getEndTime() <= startTime) { //if first workslot ends before startTime, skip to next one
//                workSlotStartIndex++;
//                continue;
//            }
//            startTime = workSlot.getStartAdjusted();
//            if (startTime + remainingDuration <= workSlot.getEndTime()) {
//                remainingDuration = 0;
//                endTime = startTime + remainingDuration;
//            } else {
//                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted();
//                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
//                workSlotStartIndex++;
//            }
//            usedWorkSlots.add(workSlot);
//        }
//        return new WorkTime(workSlots, startTime, endTime, remainingDuration);
    }

    private static WorkTime getWorkTime(List<WorkSlot> workSlots, List<ItemAndListCommonInterface> items, int itemIndex) {
        return getWorkTimeFromSlots(workSlots, 0, Long.MIN_VALUE, items.get(itemIndex).getActualEffort());
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeInfo calculateWorkTimeV3(int indexOfTask) {
////        if (workSlots == null || workSlots.size() == 0) {
////            return null;
////        }
////        if (workSlotInfoList.size() > 0 && workSlotInfoList.get(indexOfTask).calculated) {
//        // if workSlotInfo ia already calculated return it
//        WorkTimeInfo newWorkTimeInfo = new WorkTimeInfo(); //new workTimeInfo to build for the task at indexOfTask
//        WorkSlot workSlot;
//        int workSlotIndex;
//        long remainingWorkSlotDuration;
//        long lastFinishTime;
//        WorkTimeInfo prevWorkTimeInfo;
//        long now = new Date().getTime(); //use the same now in all adjustments of startTime below
//
//        if (indexOfTask < workSlotInfoList.size()) { // > 0 && workSlotInfoList.get(indexOfTask).calculated) {
//            return workSlotInfoList.get(indexOfTask); //if this entry is already calculated return it
//        } else if (indexOfTask == -1) {
//            //create first workSlotInfo to initiate the list
//            if (workSlots == null || workSlots.size() == 0) {
//                //if there are no workslots available
//                newWorkTimeInfo.indexOfLastWorkSlot = -1;
//                newWorkTimeInfo.firstWorkSlot = null;
//                newWorkTimeInfo.lastWorkSlot = null;
//                newWorkTimeInfo.finishTime = 0;
//                newWorkTimeInfo.remainingWorkSlotDuration = 0;
//            } else {
//                //use first WorkSlot
//                newWorkTimeInfo.indexOfLastWorkSlot = 0;
//                WorkSlot wSlot = workSlots.get(newWorkTimeInfo.indexOfLastWorkSlot);
//                newWorkTimeInfo.firstWorkSlot = wSlot;
//                newWorkTimeInfo.lastWorkSlot = wSlot;
//                newWorkTimeInfo.finishTime = wSlot.getStartAdjusted(now);
//                newWorkTimeInfo.remainingWorkSlotDuration = wSlot.getDurationAdjusted(now);
//            }
////            workSlotInfoList.add(newWorkTimeInfo); // DON'T store since artificial to initiate the recursive
//            return newWorkTimeInfo;
//        } else {
//            //get previous workSlotInfo
//            prevWorkTimeInfo = calculateWorkTime(indexOfTask - 1); //will return the previous workSlotInfo (and calculate earlier ones and add them to workSlotInfoList
//            workSlotInfoList.add(newWorkTimeInfo); // add the new workTimeInfo to the list (NB! do after the call to calculateWorkTime to make sure previous entries are added)
//            workSlotIndex = prevWorkTimeInfo.indexOfLastWorkSlot;
////            workSlot = workSlots.get(workSlotIndex);
//            workSlot = prevWorkTimeInfo.lastWorkSlot;
//            remainingWorkSlotDuration = prevWorkTimeInfo.remainingWorkSlotDuration;
//            lastFinishTime = prevWorkTimeInfo.finishTime;
//
////            workSlotIndex = 0;
//////            workSlot = workSlots.get(workSlotIndex);
////            workSlot = getNextWorkSlot(workSlotIndex);
////            remainingWorkSlotDuration = workSlot.getDurationAdjusted(now);
////            lastFinishTime = workSlot.getStartAdjusted(now);
////        if (!workSlotInfoList.get(indexOfTask).calculated) {
////            calculateWorkTime(indexOfTask - 1); //if previous entry is not calculated, calculate it (recursively)
////            workSlotInfoList.add(newWorkTimeInfo); // add the new workTimeInfo to the list
////            if (indexOfTask == 0) {
////                //if it is the first task, we must take the first workSlot
////                workSlotIndex = 0;
//////            workSlot = workSlots.get(workSlotIndex);
////                workSlot = getNextWorkSlot(workSlotIndex);
////                remainingWorkSlotDuration = workSlot.getDurationAdjusted(now);
////                lastFinishTime = workSlot.getStartAdjusted(now);
////            } else {
////                //if not first task, then we initialize with the workSlot of the previous task
////                WorkTimeInfo prevorkTimeInfo = workSlotInfoList.get(indexOfTask - 1);
////                workSlot = prevorkTimeInfo.lastWorkSlot;
////                remainingWorkSlotDuration = prevorkTimeInfo.remainingWorkSlotDuration;
////                workSlotIndex = prevorkTimeInfo.indexOfLastWorkSlot;
////                lastFinishTime = prevorkTimeInfo.finishTime;
////            }
//            //get duration of element
//            Object elt = elementList.get(indexOfTask);
//            long remainingDuration = (elt instanceof Item) ? ((Item) elt).getRemainingEffort() : ((ItemList) elt).getRemainingEffort();
//
//            while (workSlotIndex < workSlots.size()) { //duration can be 0, the task should still get a start and finish time
//                //initialize with first workslot that has remaning timeif there is time remaining
//                if (newWorkTimeInfo.firstWorkSlot == null && remainingWorkSlotDuration > 0) {
//                    newWorkTimeInfo.firstWorkSlot = workSlot;
//                }
//                //if there is remaining time, then we know that the task can start here (remainingTime could be 0 if used up completely by a previous task, or if the duration of the workSlot is 0)
//                if (newWorkTimeInfo.startTime == 0 && remainingWorkSlotDuration > 0) {
//                    newWorkTimeInfo.startTime = lastFinishTime;
//                }
//                //if duration<remaining, then deduct duration; if remaining<duration, then deduct remaining (<=> set remaining to 0)
//                long deduct = Math.min(remainingWorkSlotDuration, remainingDuration);
//                remainingWorkSlotDuration -= deduct; //deduct the smallest
////            duration -= deduct;
//                remainingDuration -= deduct;
//
////            if (remainingDuration == deduct) { // <=> remainingDuration==0 / is completely covered (reduced to 0)// prevWorkTimeInfo.remainingWorkSlotDuration >= 0) {
//                if (remainingDuration == 0) { // <=> is completely covered (reduced to 0)
//                    //we can complete the task completely within this workSlot
//                    newWorkTimeInfo.lastWorkSlot = workSlot;
//                    newWorkTimeInfo.indexOfLastWorkSlot = workSlotIndex;
////                remainingDuration -= deduct; //adjust remainingDuration for what was allocated to the task's duration
//                    newWorkTimeInfo.remainingWorkSlotDuration = remainingWorkSlotDuration; //store the remainingTime of this workslot
//                    newWorkTimeInfo.finishTime = workSlot.getEndTime() - remainingWorkSlotDuration;
////                    newWorkTimeInfo.calculated = true;
//                    return newWorkTimeInfo;
//                } else { //still duration to cover, try to get additional workSlot
//                    //we cannot complete the task within this workSlot, so we need additional one(s)
////                remainingDuration -= deduct; //adjust duration for what was allocated to the previous workSlot
//                    WorkSlot prevWorkSlot = workSlot; //keep prev WorkSlot to know if its endTime is higher than the startTime of the next WorkSlot
//                    workSlotIndex++;
//                    workSlot = getNextWorkSlot(workSlotIndex);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (workSlotIndex < workSlots.size()) {
////                    //more workslots available
////                    workSlot = workSlots.get(workSlotIndex);
////                    //if several workSlots start at the same time, skip to the last (assuming that they are sorted by duration, so the longest is last)
////                    while (workSlotIndex + 1 < workSlots.size() && workSlots.get(workSlotIndex + 1).getStartTime() == workSlot.getStartTime()) {
////                        workSlotIndex++;
////                        workSlot = workSlots.get(workSlotIndex);
////                    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (workSlot.getStartAdjusted(now) < prevWorkSlot.getEndTime()) {
////                    //if next workSlot overlaps with previous, reduce its duration accordingly
////                    remainingWorkSlotDuration = workSlot.getDurationAdjusted(Math.max(prevWorkSlot.getStartAdjusted()+prevWorkSlot.getDuration(), now));
////                } else {
////                    remainingWorkSlotDuration = workSlot.getDurationAdjusted(now);
////                }
////</editor-fold>
//                    if (workSlot != null) {
//                        remainingWorkSlotDuration += workSlot.getDurationAdjusted(Math.max(prevWorkSlot.getEndTime(), now)); //add new WorkSlot's duration
//                    }
//                } //end else //still duration to cover
//            } //while
//        }
//        //else : no more workslots available
//        //we didn't manage to complete the task within the available workslots
////        newWorkTimeInfo.finishTime = 0;
////        newWorkTimeInfo.lastWorkSlot = null;
////        newWorkTimeInfo.indexOfLastWorkSlot = -1;
////        newWorkTimeInfo.remainingWorkSlotDuration = 0;
////        newWorkTimeInfo.calculated = true;
//        return newWorkTimeInfo;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeInfo calculateWorkTimeV2(int indexOfTask) {
//        if (wSlotInfoList.get(indexOfTask).calculated) {
//            return wSlotInfoList.get(indexOfTask); //if this entry is already calculated return it
//        }//        if (indexOfTask>=elementList.size()) return null; //DON'T test for this, better to fail
//
//        if (!wSlotInfoList.get(indexOfTask).calculated) {
//            calculateWorkTime(indexOfTask - 1); //if previous entry is not calculated, calculate it (recursively)
//        }
//        long now = new Date().getTime(); //use the same now in all adjustments of startTime below
//        WorkTimeInfo workTimeInfo = new WorkTimeInfo(); //new workTimeInfo to build for the task at indexOfTask
//        WorkTimeInfo prevWorkTimeInfo = null;
//        if (indexOfTask == 0) {
//            //for the first task, we must initiate the first entry
//            prevWorkTimeInfo = new WorkTimeInfo();
//            prevWorkTimeInfo.firstWorkSlot = workSlots.get(0);
//            prevWorkTimeInfo.lastWorkSlot = prevWorkTimeInfo.firstWorkSlot;
//            prevWorkTimeInfo.remainingWorkSlotDuration = prevWorkTimeInfo.firstWorkSlot.getDurationAdjusted(now);
//            prevWorkTimeInfo.indexOfLastWorkSlot = 0;
//            prevWorkTimeInfo.finishTime = prevWorkTimeInfo.firstWorkSlot.getStartAdjusted(now) - 1; //-1 to ensure that next task starts correctly, see below
////            prevWorkTimeInfo.startTime=0;//NOT necessary to initialize because not used in later calculations
//            wSlotInfoList.add(prevWorkTimeInfo);
//        } else {
//            prevWorkTimeInfo = wSlotInfoList.get(indexOfTask - 1);
//        }
//
//        //get duration of element
//        Object elt = elementList.get(indexOfTask);
//        long duration = (elt instanceof Item) ? ((Item) elt).getRemainingEffort() : ((ItemList) elt).getRemainingEffort();
//
//        int workSlotIndex = prevWorkTimeInfo.indexOfLastWorkSlot;
//        WorkSlot workSlot = prevWorkTimeInfo.lastWorkSlot;
//        long remainingWorkSlotDuration = prevWorkTimeInfo.remainingWorkSlotDuration;
//
//        if (prevWorkTimeInfo.remainingWorkSlotDuration > 0) {
//            workTimeInfo.firstWorkSlot = prevWorkTimeInfo.firstWorkSlot;
//        } else if (workSlotIndex == 0) {
//            workTimeInfo.firstWorkSlot = workSlots.get(0);
//        }
//
////        while (duration >0 && workSlotIndex<workSlots.size()) {
//        while (workSlotIndex < workSlots.size()) { //duration can be 0, the task should still get a start and finish time
//            if (prevWorkTimeInfo.remainingWorkSlotDuration > 0) {
//                //if duration<remaining, then deduct duration; if remaining<duration, then deduct remaining (<=> set remaining to 0)
//                long deduct = Math.min(prevWorkTimeInfo.remainingWorkSlotDuration, duration);
//                prevWorkTimeInfo.remainingWorkSlotDuration -= deduct; //deduct the smallest
////                duration -= deduct;
//                workTimeInfo.firstWorkSlot = prevWorkTimeInfo.lastWorkSlot;
//                workTimeInfo.startTime = prevWorkTimeInfo.finishTime + 1;
//
//                if (prevWorkTimeInfo.remainingWorkSlotDuration >= 0) {
//                    //we can complete the task completely within this workSlot
//                    workTimeInfo.lastWorkSlot = prevWorkTimeInfo.lastWorkSlot;
//                    workTimeInfo.finishTime = workTimeInfo.startTime + duration;
//                    workTimeInfo.indexOfLastWorkSlot = prevWorkTimeInfo.indexOfLastWorkSlot;
//                } else {
//                    //we cannot complete the task within this workSlot, so we need additional one(s)
//                    duration -= deduct;
//                    workTimeInfo.indexOfLastWorkSlot++; //point to next workSlot
//                    if (prevWorkTimeInfo.indexOfLastWorkSlot >= workSlots.size() - 1) {
//                        //no more workslots available
//                        workTimeInfo.finishTime = 0;
//                        workTimeInfo.lastWorkSlot = null;
//                        workTimeInfo.indexOfLastWorkSlot = -1;
//                    } else {
//                        workSlotIndex = workTimeInfo.indexOfLastWorkSlot + 1; //take next WorkSlot
//                        WorkSlot nextWorkSlot = workSlots.get(workSlotIndex);
//                        workTimeInfo.remainingWorkSlotDuration = nextWorkSlot.getDurationAdjusted(now);
//                        workTimeInfo.lastWorkSlot = nextWorkSlot;
//                        workTimeInfo.indexOfLastWorkSlot = workSlotIndex;
//                        workTimeInfo.remainingWorkSlotDuration = nextWorkSlot.getDurationAdjusted(now);
//                    }
//                }
//            }
//        }
//
////                long oldDuration = duration;
////                workTimeInfo.firstWorkSlot=prevWorkTimeInfo.lastWorkSlot;
////                workTimeInfo.startTime=prevWorkTimeInfo.finishTime+1;
////                duration -= prevWorkTimeInfo.remainingWorkSlotDuration;
////                prevWorkTimeInfo.remainingWorkSlotDuration=0;
////                if (prevWorkTimeInfo.indexOfLastWorkSlot>=workSlots.size()-1) {
////                    //no more workslots available
////                    workTimeInfo.finishTime=0;
////                    workTimeInfo.lastWorkSlot=null;
////                } else {
////                    //previous workslot is used up (no duration left), take the next one
////                    workSlotIndex++; //take next WorkSlot
////                    if (workSlotIndex>=list.size())
////                        return; //no more workSlots
////                    do {
////                    WorkSlot workSlot = workSlots.get(prevWorkTimeInfo.indexOfLastWorkSlot+1);
////                } while duration >0;
////
////                }
////
////            }
////        }
////        workTimeInfo.calculated = true;
////        list.add(workTimeInfo);
////
////        indexOfWorkSlot=0;
////        currentSlot = workSlots.get(indexOfWorkSlot);
//////        firstSlot = currentSlot;
//////        lastSlot = currentSlot;
////        endTimeOfLastTask = Math.max(workSlots.get(0).getStartTime().getTime(), now);
////        endTimeOfLastWorkSlot = Math.max(workSlots.get(0).getStartTime().getTime(), now) + workSlots.get(0).getDuration();
////        remainingWorkSlotDuration = firstSlot.getDurationAdjusted(now);
////
////        for (int itemIndex = 0, size = elementList.size(); itemIndex < size; itemIndex++) {
////            Object element = elementList.get(itemIndex);
////            long duration;
////            if (element instanceof Item) {
////                duration = ((Item) element).getRemainingEffort();
////            } else if (element instanceof ItemList) {
////                duration = ((ItemList) element).getSumOfRemainingEffort(i);
////            }
//////            finishTime[itemIndex] = getNextEndTime(duration);
//////            lastWorkSlot[itemIndex] = lastSlot;
////            long remainingDuration = duration;
////            //as long as we haven't found workSlots enough to cover all of the duration
////            while (remainingWorkSlotDuration > 0 || indexOfWorkSlot < workSlots.size()) {
////                if (remainingWorkSlotDuration > 0) {
////                    firstWorkSlot[itemIndex] = workSlots[indexOfWorkSlot];
////
////                    remainingWorkSlotDuration -= remainingDuration; //deduct duration
////                    //we need another workslot to fulfill the duration
////                }
////                if (remainingWorkSlotDuration > remainingDuration) { //duration fits within the duration remaining from the latest WorkSlot
////                    endTimeOfLastTask += remainingDuration; //update endTime with the just consumed time
//////                duration = 0;
////                    firstSlot = lastSlot; //update both to let
////                    return endTimeOfLastTask;
////                } else { //we need more WorkSlots
////                    duration -= remainingWorkSlotDuration; //deduct whatever time was remaining from earlier workslot from duration
////                    firstSlot = lastSlot; //update both to let
////
////                    indexOfWorkSlot++; //get next WorkSlot
////                    lastSlot = workSlots.get(indexOfWorkSlot);
////                    long slotDuration = lastSlot.getDurationAdjusted(now);
////                    long slotStartTime = lastSlot.getStartTime().getTime();
////
////                    //check if any slots have same startTime, if so, keep the one with the longest duration and discard the others
////                    for (int j = indexOfWorkSlot + 1; j < workSlots.size(); j++) {
////                        //if next WorkSlot has same startTime
////                        if (workSlots.get(j).getStartTime().getTime() == slotStartTime) {
////
////                        }
////                    }
////
////                    //now we have the right next WorkSlot
////                    //check if next WorkSlot overlaps previous one and adjustthe startTime of new slot to be the endTime of the previous workslot
////                    if (endTimeOfLastWorkSlot > lastSlot.getStartTime().getTime()) {
//////                endTimeOfLastTask = slot.getStartTime().getTime(); //NOT necessary, we keep the earlier endTime
////                        remainingWorkSlotDuration = lastSlot.getDuration() - (Math.max(now, lastSlot.getStartTime().getTime()) - endTimeOfLastWorkSlot); //deduct the overlapping part of the next workslot, adjust for Now
////                        remainingWorkSlotDuration = lastSlot.getDuration() - (lastSlot.getStartAdjusted(now) - endTimeOfLastWorkSlot); //deduct the overlapping part of the next workslot, adjust for Now
////                    } else {
////                        //if no overlap
//////                    endTimeOfLastTask = Math.max(now, lastSlot.getStartTime().getTime()); //use start of next workslot
////                        endTimeOfLastTask = lastSlot.getStartAdjusted(now); //use start of next workslot
////                        remainingWorkSlotDuration = lastSlot.getDuration(); //all of the next workslot's duration is available
////                    }
////                    endTimeOfLastWorkSlot = slotStartTime + slotDuration; //update to endTime of new found slot
////                }
////            } //continue with next slot
////            //return 0 if no endTime found (that is, the task
////            lastSlot = null;
////            firstSlot = null;
////            return 0;
//    }
//    private long getNextEndTimeV1(long duration) {
//        //initialize now the first time we start for end time (to avoid that now shifts slightly as we move down the list of WorkSlots)
////        if (indexOfNextWorkSlot == 0) {
////            now = new Date().getTime();
////        }
////        if (duration < remainingDuration) { //duration fits within latest WorkSlow
////            remainingDuration -= duration; //deduct duration
////            endTimeOfLastTask += duration; //update endTime with the just consumed time
////            return endTimeOfLastTask;
//        //as long as we haven't found workSlots enough to cover all of the duration
//        while (duration > 0 && indexOfWorkSlot < workSlots.size()) {
//            if (remainingWorkSlotDuration > duration) { //duration fits within the duration remaining from the latest WorkSlot
//                remainingWorkSlotDuration -= duration; //deduct duration
//                endTimeOfLastTask += duration; //update endTime with the just consumed time
////                duration = 0;
//                firstSlot = lastSlot; //update both to let
//                return endTimeOfLastTask;
//            } else { //we need more WorkSlots
//                duration -= remainingWorkSlotDuration; //deduct whatever time was remaining from earlier workslot from duration
//                firstSlot = lastSlot; //update both to let
////                endTimeOfLastTask += remainingWorkSlotDuration; //NOT necessary since we'll be using the time of the next workSlot
////                remainingWorkSlotDuration = 0; // NOT necessary since assigned with new value later
////            endTimeOfLastTask += remainingWorkSlotDuration; //update endTime with the just consumed time //NOT needed since the next WorkSlot will determine the endTime
//
////                WorkSlot lastSlot;
//                indexOfWorkSlot++; //get next WorkSlot
//                lastSlot = workSlots.get(indexOfWorkSlot);
//                long slotDuration = lastSlot.getDurationAdjusted(now);
//                long slotStartTime = lastSlot.getStartTime().getTime();
//
////                //check if any slots have same startTime, if so, keep the one with the longest duration and discard the others
////                for (int j = indexOfNextWorkSlot + 1; j < workSlots.size(); j++) {
////                    //if next WorkSlot has same startTime
////                    if (workSlots.get(j).getStartTime().getTime() == slotStartTime) {
////                        //if next slot has *longer* duration
////                        if (workSlots.get(j).getDurationAdjusted(now) > slotDuration) {
////                            // use next (longer) slot
////                            indexOfNextWorkSlot = j; //update index to point to this one
////                            lastSlot = workSlots.get(indexOfNextWorkSlot);
////                            slotDuration = lastSlot.getDurationAdjusted(now);
////
////                        } else {
////                            //continue to try next WorkSlot which may (also) have same startTime
////                            continue;
////                        }
////                    } else {
////                        //as soon we encounter first WorkSlot with a different StartTime we can stop the iteration
////                        break;
////                    }
////                }
//                //check if any slots have same startTime, if so, keep the one with the longest duration and discard the others
//                for (int j = indexOfWorkSlot + 1; j < workSlots.size(); j++) {
//                    //if next WorkSlot has same startTime
//                    if (workSlots.get(j).getStartTime().getTime() == slotStartTime) {
//
//                    }
//                }
//
//                //now we have the right next WorkSlot
//                //check if next WorkSlot overlaps previous one and adjustthe startTime of new slot to be the endTime of the previous workslot
//                if (endTimeOfLastWorkSlot > lastSlot.getStartTime().getTime()) {
////                endTimeOfLastTask = slot.getStartTime().getTime(); //NOT necessary, we keep the earlier endTime
//                    remainingWorkSlotDuration = lastSlot.getDuration() - (Math.max(now, lastSlot.getStartTime().getTime()) - endTimeOfLastWorkSlot); //deduct the overlapping part of the next workslot, adjust for Now
//                    remainingWorkSlotDuration = lastSlot.getDuration() - (lastSlot.getStartAdjusted(now) - endTimeOfLastWorkSlot); //deduct the overlapping part of the next workslot, adjust for Now
//                } else {
//                    //if no overlap
////                    endTimeOfLastTask = Math.max(now, lastSlot.getStartTime().getTime()); //use start of next workslot
//                    endTimeOfLastTask = lastSlot.getStartAdjusted(now); //use start of next workslot
//                    remainingWorkSlotDuration = lastSlot.getDuration(); //all of the next workslot's duration is available
//                }
//                endTimeOfLastWorkSlot = slotStartTime + slotDuration; //update to endTime of new found slot
//            }
//        } //continue with next slot
//        //return 0 if no endTime found (that is, the task
//        lastSlot = null;
//        firstSlot = null;
//        return 0;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    long getDate(long totalElapsedTime) {
////        WorkSlot slot;
////        long previousSlotEndTime;
//        long slotDuration;
//        long slotStartTime;
//        WorkSlot slot;
////        for (WorkSlot slot : workSlots) {
//        if (endDateOfEarlierWorkSlots)
//        for (int indexOfNextWorkSlot = 0, size = workSlots.size(); indexOfNextWorkSlot < size; indexOfNextWorkSlot++) {
//            slot = workSlots.get(indexOfNextWorkSlot);
//            slotDuration = slot.getDurationAdjusted();
//            slotStartTime = slot.getStartAdjusted();
//            //check if any slots have same startTime, if so, keep the one with the longest duration and discard the others
//            for (int j=indexOfNextWorkSlot+1; j<size; j++) {
//                if (workSlots.get(j).getStartTime().getTime()==slotStartTime) { //next WorkSlot has same startTime
//                    if (workSlots.get(j).getDurationAdjusted()>slotDuration) {//if next slot has longer duration
//                        slot = workSlots.get(indexOfNextWorkSlot + 1); // use next (longer) slot
//                        slotDuration = slot.getDurationAdjusted();
//                        indexOfNextWorkSlot=j; //update index to point to this one
//                    } else continue;
//                } else break;
//            }
//        }
//                (i < size - 1 && workSlots.get(i + 1).getStartTime().getTime() > slotStartTime + slotDuration) {
//            if (i < size - 1 && workSlots.get(i + 1).getStartTime().getTime() > slotStartTime + slotDuration) {
//
//            }
//            if (totalElapsedTime > sumOfEarlierWorkSlots + slotDuration) {
//                sumOfEarlierWorkSlots += slotDuration;
//                continue;
//            } else { //totalElapsedTime falls within this workSlot
//                return slot.getStartAdjusted() + (totalElapsedTime - sumOfEarlierWorkSlots); //date of totalElapsedTime is startTime of relevant workSlot + the part of the totalElapsedTime that 'stetches' into this workslot
//            }
//        }
//        return 0;
//    }
//</editor-fold>
}
