/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;

/**
each list or item which has workTime, or worktime allocated, has its own WTA which is used to allocate
workTime to its (sub-)tasks, itself (if the project task has its own Remaining) or its items (if its a list). 

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
public class WorkTimeAllocator { //implements Externalizable { //extends ItemList {

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
//    private WorkSlotList workSlots;// = new ItemList(); //lazy
    private WorkTimeSlices workTimeSlices;// = new ItemList(); //lazy
//    private List<ItemAndListCommonInterface> itemsSortedFiltered; //**
//    private List<ItemAndListCommonInterface> itemsSortedFiltered; //**
    private List<Item> itemsSortedFiltered; //**
//    List<WorkTimeSlices> workTimeCache; // = new ArrayList(); //list of workTimeSlices allocated to each item in itemsSortedFiltered, in order
    HashMap<ItemAndListCommonInterface, WorkTimeSlices> workTimeCache; // = new ArrayList(); //list of workTimeSlices allocated to each item in itemsSortedFiltered, in order
//    private boolean cacheActiveXXX = true;
//    private ItemAndListCommonInterface ownerOrCategory;// = new ItemList(); //lazy
    private ItemAndListCommonInterface ownerItemItemListOrCategory;// = new ItemList(); //lazy
//    private int largestOngoingIndex = Integer.MAX_VALUE; //use MAX to avoid that first call falsely looks like a illegal recursion
//    private boolean log = false;
//    private int lastIndex = -1; //use MAX to avoid that first call falsely looks like a illegal recursion

//    private Hashtable<ItemAndListCommonInterface, WorkTimeInfo> workSlotInfoHashTable; // = new Hashtable();
//    WorkTimeDefinition(List<ItemAndListCommonInterface> listOfItemsOrItemListsFilteredSorted, WorkSlotList workSlots) {
//    WorkTimeAllocator(List<ItemAndListCommonInterface> listOfItemsOrItemListsFilteredSorted, WorkTimeSlices workTime, ItemAndListCommonInterface owner) {
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeAllocator(WorkTimeSlices workTime, ItemAndListCommonInterface ownerOrCategory) {
////        this.items = listOfItemsOrItemListsFilteredSorted;
////        this.itemsSortedFiltered = (List<ItemAndListCommonInterface>) ownerOrCategory.getList();
//        this.itemsSortedFiltered = (List<ItemAndListCommonInterface>) ownerOrCategory.getList();
////        this.orgItemOrList = orgItemOrList;
//        this.workTimeSlices = workTime;
//        this.ownerOrCategory = ownerOrCategory;
////        init();
////        workSlotInfoList = new ArrayList();
////        workSlotInfoHashTable = new Hashtable();
//    }
//</editor-fold>
//    WorkTimeAllocator(ItemAndListCommonInterface ownerOrCategory) {
    WorkTimeAllocator(ItemAndListCommonInterface ownerOrCategory) {
//        this.items = listOfItemsOrItemListsFilteredSorted;
//        this.itemsSortedFiltered = (List<ItemAndListCommonInterface>) ownerOrCategory.getList();
        this.ownerItemItemListOrCategory = ownerOrCategory;
//        this.orgItemOrList = orgItemOrList;
//        WorkSlotList workSlotList = this.ownerOrCategory.getWorkSlotListN();
//        this.workTimeSlices = new WorkTimeSlices(this.ownerOrCategory.getWorkSlotListN()); //workSlotList != null ? new WorkTimeSlices(this.ownerOrCategory.getWorkSlotListN()) : null;
//        this.itemsSortedFiltered = (List<ItemAndListCommonInterface>) this.ownerOrCategory.getList();
//
////        long requiredWorkTime = ownerOrCategory.getWorkTimeRequiredFromProvider(ownerOrCategory); //calculate how much time is needed from this' subtasks
//        long requiredWorkTime = this.ownerOrCategory.getRemaining(); //calculate how much time is needed from all subtasks
        initAndReset();
//<editor-fold defaultstate="collapsed" desc="comment">
//        long availWorktime = getAvailableTime();
//        if (requiredWorkTime > availWorktime) { //if need additional workTime
//            requiredWorkTime -= availWorktime; //reduce to needed additional workTime
//            List<ItemAndListCommonInterface> potentialProviders = this.ownerOrCategory.getOtherPotentialWorkTimeProvidersInPrioOrderN();
//            if (potentialProviders != null) {
//                for (ItemAndListCommonInterface prov : potentialProviders) {
//                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////                addWorkTimeSlices(prov.allocate2(ownerOrCategory));
//                    addWorkTimeSlices(prov.getAllocatedWorkTimeN(this.ownerOrCategory));
//                    requiredWorkTime = getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (requiredWorkTime == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//</editor-fold>
    }

    private void initAndReset() {
        workTimeCache = null;
//          WorkSlotList workSlotList = this.ownerOrCategory.getWorkSlotListN();
        this.workTimeSlices = new WorkTimeSlices(this.ownerItemItemListOrCategory.getWorkSlotListN()); //workSlotList != null ? new WorkTimeSlices(this.ownerOrCategory.getWorkSlotListN()) : null;
//        this.itemsSortedFiltered = (List<ItemAndListCommonInterface>) this.ownerOrCategory.getList();
        this.itemsSortedFiltered = (List<Item>) this.ownerItemItemListOrCategory.getList();

//        long requiredWorkTime = ownerOrCategory.getWorkTimeRequiredFromProvider(ownerOrCategory); //calculate how much time is needed from this' subtasks
        long requiredWorkTime = this.ownerItemItemListOrCategory.getRemaining(); //calculate how much time is needed from all subtasks
//        long availWorktime = getAvailableTime();
        long availWorktime = workTimeSlices.getAvailableTime();
        if (requiredWorkTime > availWorktime) { //if need additional workTime
            requiredWorkTime -= availWorktime; //reduce to needed additional workTime
            List<ItemAndListCommonInterface> potentialProviders = this.ownerItemItemListOrCategory.getOtherPotentialWorkTimeProvidersInPrioOrderN();
            if (potentialProviders != null) {
                for (ItemAndListCommonInterface prov : potentialProviders) {
                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//                addWorkTimeSlices(prov.allocate2(ownerOrCategory));
//                    addWorkTimeSlices(prov.getAllocatedWorkTimeN(this.ownerOrCategory));
                    WorkTimeSlices slices = prov.getAllocatedWorkTimeN((Item) ownerItemItemListOrCategory); //(Item) since we should only allocate to items
                    if (slices != null) {
                        workTimeSlices.addWorkTime(slices);
//                    requiredWorkTime = getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    requiredWorkTime = workTimeSlices.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
                        requiredWorkTime -= slices.getAllocatedDuration(); //set remaining to any duration that could not be allocated by this provider
                    }
                    if (requiredWorkTime == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
                        break;
                    }
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void addWorkTimeSlices(WorkTimeSlices workTime) {
//        if (workTime == null) return;
//        if (workTimeSlices == null)
//            workTimeSlices = workTime;
//        else {
//            workTimeSlices.addWorkTime(workTime);
////            resetCache();
//        }
//    }
//    long getRemainingDuration() {
////        if (workTimeSlices.getWorkSlotSlices().size()>0)
//        return workTimeSlices.getRemainingDuration();
////        else return 0;
//    }
//</editor-fold>
    public String toString() {
        String sep = "";
        String sliceStr = "";
        if (workTimeCache != null) for (WorkTimeSlices slice : workTimeCache.values()) {
                sliceStr += sep + slice.toString();
            }
        return "WorkTimeAllocator for:"
                + (ownerItemItemListOrCategory != null ? (ownerItemItemListOrCategory.getText()) : "NONE??")
                + "Got:" + (workTimeSlices != null ? workTimeSlices.getAvailableTime() : "<none>")
                + ";\nWorkTime= " + (workTimeSlices != null ? workTimeSlices.toString() : "<none>")
                + ";\nAllocations: " + (sliceStr.length() > 0 ? sliceStr : "<none>"); //workTimeCache.toString(); Item.
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    long getAvailableTime() {
////<editor-fold defaultstate="collapsed" desc="comment">
////        long time = 0;
////        if (workTimeSlices != null && workTimeSlices.getWorkSlotSlices() != null)
////            for (WorkSlotSlice slice : workTimeSlices.getWorkSlotSlices()) {
////                time += slice.getDuration();
////            }
////        return time;
////</editor-fold>
//        if (workTimeSlices != null)
//            return workTimeSlices.getAvailableTime();
//        else
//            return 0;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
    returns the list of items that have a slice of this workSlot allocated to them, in allocation order based on order of the list of items in itemsSortedFiltered
    @param workSlot
    @return
     */
//    public List<Item> getItemsInWorkSlot(WorkSlot workSlot) { xxx
//        List<Item> itemsInWorkSlot = new ArrayList();
//        for (int i = 0, size = itemsSortedFiltered.size(); i < size; i++) {
//            WorkTimeSlices wts = workTimeCache.get(i);
//            if (wts.hasSlicesOf(workSlot)) {
//                itemsInWorkSlot.add((Item) itemsSortedFiltered.get(i));
//            }
//        }
//        return itemsInWorkSlot;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeDefinition(WorkTimeSlices workTime) {
//        this.items = listOfItemsOrItemListsFilteredSorted;
////        this.orgItemOrList = orgItemOrList;
//        this.workSlots = workSlots;
////        init();
////        workSlotInfoList = new ArrayList();
////        workSlotInfoHashTable = new Hashtable();
//    }
//</editor-fold>
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
     * @param item
     * @param remainingDuration
     * @param duration
     * @return 
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeSlices calculateWorkTime(WorkTimeSlices prevWorkTime) {
//        return calculateWorkTime(prevWorkTime.getLastWorkSlotIndex(), prevWorkTime.getFinishTime(), prevWorkTime.getRemainingDuration());
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeSlices calculateWorkTime(int workSlotIndex, long startTime, long remainingDuration) {
////        long startTime = Long.MIN_VALUE;
//        long endTime = Long.MIN_VALUE;
//        List<WorkSlot> usedWorkSlots = null;
//        boolean startTimeCheckedAgainstFirstValidWorkSLot = false; //first valid workslot must be checked to see if it *starts* later than startTime (no overlap w previous workslot), so startTime can be updated (bot should only be done once!)
//
//        while (remainingDuration > 0 && workSlotIndex < workSlots.size()) {
//            WorkSlot workSlot = workSlots.get(workSlotIndex); //get next workSlot
//            if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) { //if first workslot ends before startTime, or it empty, skip to next one
//                //UI: 0-duration workslots will NOT be included in list for a task
//                workSlotIndex++;
//                continue; //continue with next workSLot
//            }
//            if (!startTimeCheckedAgainstFirstValidWorkSLot && workSlot.getStartAdjusted() > startTime) { //if next workSlot starts *after* startTime, adjust startTime
//                startTime = workSlot.getStartAdjusted();
//                startTimeCheckedAgainstFirstValidWorkSLot = true;
//            }
////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTimeSlices
//            if (startTime + remainingDuration <= workSlot.getEndTime()) { //duration is fully covered within current workSlot
//                endTime = startTime + remainingDuration;
//                remainingDuration = 0;
//            } else {
//                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted(startTime);
//                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
//                workSlotIndex++;
//            }
//            if (usedWorkSlots == null) { //lazy init
//                usedWorkSlots = new ArrayList();
//            }
//            usedWorkSlots.add(workSlot);
//        }
//        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeSlices calculateWorkTimeOLD(int workSlotIndex, long startTime, long remainingDuration) {
////        long startTime = Long.MIN_VALUE;
//        long endTime = Long.MIN_VALUE;
//        List<WorkSlot> usedWorkSlots = null;
//        boolean startTimeCheckedAgainstFirstValidWorkSLot = false; //first valid workslot must be checked to see if it *starts* later than startTime (no overlap w previous workslot), so startTime can be updated (bot should only be done once!)
//
//        while (remainingDuration > 0 && workSlotIndex < workSlots.size()) {
//            WorkSlot workSlot = workSlots.get(workSlotIndex); //get next workSlot
//            if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) { //if first workslot ends before startTime, or it empty, skip to next one
//                //UI: 0-duration workslots will NOT be included in list for a task
//                workSlotIndex++;
//                continue; //continue with next workSLot
//            }
//            if (!startTimeCheckedAgainstFirstValidWorkSLot && workSlot.getStartAdjusted() > startTime) { //if next workSlot starts *after* startTime, adjust startTime
//                startTime = workSlot.getStartAdjusted();
//                startTimeCheckedAgainstFirstValidWorkSLot = true;
//            }
////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTimeSlices
//            if (startTime + remainingDuration <= workSlot.getEndTime()) { //duration is fully covered within current workSlot
//                endTime = startTime + remainingDuration;
//                remainingDuration = 0;
//            } else {
//                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted(startTime);
//                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
//                workSlotIndex++;
//            }
//            if (usedWorkSlots == null) { //lazy init
//                usedWorkSlots = new ArrayList();
//            }
//            usedWorkSlots.add(workSlot);
//        }
//        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeSlices getWorkTimeImpl(int itemIndex, long finishTime) {
////        long finishTime; // = MyDate.MIN_DATE;
//        Integer startWorkSlotIndex; // = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
//
//        if (itemIndex > 0) {
//            WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1); //recurse
//            finishTime = prevWorkTime.getFinishTime();
//            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
//        } else {
//            finishTime = MyDate.MIN_DATE;
//            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
//        }
//
////        return calculateWorkTime(startWorkSlotIndex, finishTime, items.get(itemIndex).getRemainingEffort());
//        return workTime.getAllocatedWorkTimeN(startWorkSlotIndex, finishTime, items.get(itemIndex).getRemainingEffort());
////<editor-fold defaultstate="collapsed" desc="comment">
////        long endTime = Long.MIN_VALUE;
////        List<WorkSlot> usedWorkSlots = new ArrayList();
////        while (remainingDuration > 0 && workSlotStartIndex < workSlots.size()) {
////            WorkSlot workSlot = workSlots.get(workSlotStartIndex); //get next workSlot
////            if (workSlot.getEndTime() <= startTime) { //if first workslot ends before startTime, skip to next one
////                workSlotStartIndex++;
////                continue;
////            }
////            startTime = workSlot.getStartAdjusted();
////            if (startTime + remainingDuration <= workSlot.getEndTime()) {
////                remainingDuration = 0;
////                endTime = startTime + remainingDuration;
////            } else {
////                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted();
////                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
////                workSlotStartIndex++;
////            }
////            usedWorkSlots.add(workSlot);
////        }
////        return new WorkTimeSlices(workSlots, startTime, endTime, remainingDuration);
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getAllocatedWorkTimeXXX(Item item, long remainingDuration) {
//        int itemIndex = itemsSortedFiltered.indexOf(item);
//        if (false && (itemIndex < 0 || Config.TEST)) {
//            assert itemIndex >= 0 : "WorkTimeAllocator.getAllocatedWorkTime: item=" + item
//                    + " not found, owner=" + ownerOrCategory + (ownerOrCategory instanceof Category ? "[CAT]" : "")
//                    //                    + ", itemsSortedFiltered=" + MyForm.getListAsCommaSeparatedString((List<? extends ItemAndListCommonInterface>)itemsSortedFiltered, true)
//                    + ", itemsSortedFiltered=" + MyForm.getListAsCommaSeparatedString(itemsSortedFiltered, true)
//                    + " owner.getList=" + MyForm.getListAsCommaSeparatedString((List<ItemAndListCommonInterface>) ownerOrCategory.getList(), true);
//        }
//        if (itemIndex >= 0) {
//            return getAllocatedWorkTimeXXX(itemIndex, remainingDuration); //if workTiemSlices already calculated, return them
//        } else if (item == ownerOrCategory) {
//            return new WorkTimeSlices(item.getWorkSlotListN()); //if not calculated return
//        } else {
//            return new WorkTimeSlices();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns workTime for the remaining effort of the item
     *
     * @param item
     * @return
     */
//    public WorkTimeSlices allocateWorkTimeXXX(ItemAndListCommonInterface item) {
//        int itemIndex = itemsSortedFiltered.indexOf(item);
////        return getAllocatedWorkTimeN(itemIndex, item.getRemainingEffort());
//        return getAllocatedWorkTime(itemIndex, item.getWorkTimeRequiredFromProvider(ownerOrCategory));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getAllocatedWorkTimeN(int itemIndex) {
////        ItemAndListCommonInterface item = items.get(itemIndex);
////        return getAllocatedWorkTimeN(itemIndex, items.get(itemIndex).getRemainingEffort());
////        return WorkTimeAllocator.this.getAllocatedWorkTimeN(itemIndex, items.get(itemIndex).getWorkTimeRequiredFromProvider(owner));
//        return getAllocatedWorkTimeN(itemIndex, items.get(itemIndex).getWorkTimeRequiredFromProvider(owner));
//    }
//</editor-fold>
    /**
     * returns allocated workTime for the item at index itemIndex and for an
     * effort of desiredDuration, may return less than desired even 0. Call with project task itself to get the time allocated to it. 
     *
     * @param itemIndex must be a valid itemIndex
     * @param desiredDuration
     * @return null if no workTime allocated
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTimeSlices getAllocatedWorkTimeOLD(int itemIndex, long desiredDuration) {
//        if (Config.WORKTIME_TEST) {
//            assert itemIndex >= 0 : "WorkTimeAllocator.getAllocatedWorkTime called with negative itemIndex";
//        }
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p(ownerOrCategory + "." + "getAllocatedWorkTime(itemIndex=" + itemIndex + ", duration=" + desiredDuration + "), item=" + itemsSortedFiltered.get(itemIndex));
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (!cacheActive) return new WorkTimeSlices();
////if allocated work time is already calculated/cached return the old value
////        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
////        if (itemIndex < workTimeCache.size()) {
////</editor-fold>
//        if (itemIndex < workTimeCache.size()) { //already calculated the workTime for the element at this position
//            return workTimeCache.get(itemIndex);
//        } else {//        if (itemIndex >= workTimeCache.size()) { //need to calculate first
////<editor-fold defaultstate="collapsed" desc="comment">
////            return workTimeCache.get(itemIndex);
////        } else {
////</editor-fold>
//            WorkTimeSlices workTS = null;
//            Item item;
//            long itemDuration;
//            for (int i = workTimeCache.size(), size = itemIndex; i <= size; i++) { //start missing cache item at workTimeCache.size()==last one already calculated
//                item = (Item) itemsSortedFiltered.get(i);
//                if (i == itemIndex) {
//                    itemDuration = desiredDuration; //TODO!!! weird that desiredDuration is taken from parameter insted of using item.getWorkTimeRequiredFromProvider(owner)!!!
////                    int itemDuration2 = desiredDuration; //TODO!!! weird that desiredDuration is taken from parameter insted of using item.getWorkTimeRequiredFromProvider(owner)!!!
//                } else {
//                    itemDuration = item.getWorkTimeRequiredFromProvider(ownerOrCategory);
//                }
//                if (i == 0) {
////                    workT = workTime.getWorkTime(desiredDuration);
//                    workTS = workTimeSlices.getWorkTime(itemDuration);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (cacheActive && workTimeCache == null) {
////                        workTimeCache = new ArrayList<>();
////                    }
////</editor-fold>
//                } else {
////                    WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
//                    Item previousItem = (Item) itemsSortedFiltered.get(i - 1);
//                    WorkTimeSlices prevWorkTime = getAllocatedWorkTimeOLD(i - 1, previousItem.getWorkTimeRequiredFromProvider(ownerOrCategory)); //recurse
//                    workTS = workTimeSlices.getWorkTime(prevWorkTime.getFinishTime(), itemDuration); //get slice! starting from end of previous slice
//                }
////                if (cacheActive && workT != null) { //must cache even null values since cache is an array
//                workTimeCache.add(workTS); //added in right, increasing order thanks to contruction of for loop
////                }
//            }
//        }
//        return null;
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (workT == null) {
////                workT = new WorkTimeSlices();
////            }
////            return workT; //returns the last value calculated (which is for itemIndex)
////        }
//////        WorkTimeSlices workT;
////        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
////            return workTimeCache.get(itemIndex);
////        } else {
////            if (itemIndex > 0) {
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
////                workT = workTime.getWorkTime(prevWorkTime.getFinishTime(), desiredDuration); //get slice! starting from end of previous slice
////            } else { //itemIndex == 0, allocate time to very first element in list and end recursion
////                workT = workTime.getWorkTime(desiredDuration);
////                if (workTimeCache == null && cacheActive) {
////                    workTimeCache = new ArrayList<>();
////                }
////            }
////            if (cacheActive) { //must cache even null values since cache is an array
////                workTimeCache.add(workT);
////            }
////            return workT;
////        }
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface item) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (Config.WORKTIME_TEST) {
////            assert itemIndex >= 0 : "WorkTimeAllocator.getAllocatedWorkTime called with negative itemIndex";
////        }
////        if (Config.WORKTIME_DETAILED_LOG) {
////            Log.p(ownerOrCategory + "." + "getAllocatedWorkTime(itemIndex=" + itemIndex + ", duration=" + desiredDuration + "), item=" + itemsSortedFiltered.get(itemIndex));
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (!cacheActive) return new WorkTimeSlices();
////if allocated work time is already calculated/cached return the old value
////        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
////        if (itemIndex < workTimeCache.size()) {
////        long desiredDuration = item.getWorkTimeRequiredFromProvider(ownerOrCategory);
////        for (int i = 0, size = itemsSortedFiltered.size(); i < size; i++) {
////</editor-fold>
//        if (workTimeCache == null)
//            workTimeCache = new ArrayList<>(itemsSortedFiltered.size() + 1); //pre-allocate one for each item, +1 for the item itself
//        for (int i = 0, size = itemsSortedFiltered.size(); i < size; i++) {
//            ItemAndListCommonInterface elt = itemsSortedFiltered.get(i);
//            if (i < workTimeCache.size()) {
//                if (elt == item || elt.equals(item)) {
//                    if (Config.TEST) ASSERT.that(!(elt.equals(item)) || elt == item, "element dupliicated item=" + item + ", elt=" + elt);
//                    return workTimeCache.get(i);
//                }
//                //else: do nothing since already calculated
//            } else { //not calculated
//                long eltDuration = elt.getWorkTimeRequiredFromProvider(ownerOrCategory);
//                WorkTimeSlices newWorkTS = null;
//                if (i == 0) {
//                    newWorkTS = workTimeSlices.getWorkTime(eltDuration, elt);
//                } else {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    Item previousItem = (Item) itemsSortedFiltered.get(i - 1);
//////                    WorkTimeSlices prevWorkTime = getAllocatedWorkTime(i - 1, previousItem.getWorkTimeRequiredFromProvider(ownerOrCategory)); //recurse
////                    WorkTimeSlices prevWorkTime = getAllocatedWorkTime(previousItem); //recurse
////                    workTS = workTimeSlices.getWorkTime(prevWorkTime.getFinishTime(), eltDuration); //get slice! starting from end of previous slice
////</editor-fold>
//                    newWorkTS = workTimeSlices.getWorkTime(workTimeCache.get(i - 1).getFinishTime(), eltDuration, elt); //get slice! starting from end of previous slice
//                }
////                if (cacheActive && workT != null) { //must cache even null values since cache is an array
//                workTimeCache.add(newWorkTS); //added in right, increasing order thanks to contruction of for loop
//            }
//            if (elt == item)
//                return workTimeCache.get(i); //return the just calculated value (and lazy: stop calculation here for now)
//        }
//        //if called with the project task itself, we'll 'fall through' to the following statements to calculate/return the time allocated to the project task itself
//        //UII: if the project task itself has its own Remaining, allocate that last (after all its subtasks have had their time allocated)
//        //TODO add a setting to allocate to project task *first*?
//        if (item == ownerOrCategory && ownerOrCategory instanceof Item) {
//            Item itemOwner = (Item) ownerOrCategory;
//            long remainingPrjTask = itemOwner.getRemainingForProjectTaskItself(!itemOwner.isProject()); //isProject(): use default estimates for leaf-tasks, not for Project/mother tasks
//            if (remainingPrjTask > 0) {
//                int cacheSize = workTimeCache.size();
//                //if there's already one extra allocated, it is the project task itself
//                if (cacheSize > itemsSortedFiltered.size())
//                    return workTimeCache.get(cacheSize - 1);
//                else {
//                    WorkTimeSlices newWorkTS = null;
//                    if (cacheSize == 0) //if cacheSize==0 means no subtasks (time has been allocatd to all subtasks when we arrive here)
//                        newWorkTS = workTimeSlices.getWorkTime(remainingPrjTask, ownerOrCategory);
//                    else {
//                        newWorkTS = workTimeSlices.getWorkTime(workTimeCache.get(cacheSize - 1).getFinishTime(), remainingPrjTask, ownerOrCategory); //get slice! starting from end of previous slice
//                        workTimeCache.add(newWorkTS); //add the
//                    }
//                    return newWorkTS;
//                }
//            }
//        }
//        return null;
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (workT == null) {
////                workT = new WorkTimeSlices();
////            }
////            return workT; //returns the last value calculated (which is for itemIndex)
////        }
//////        WorkTimeSlices workT;
////        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
////            return workTimeCache.get(itemIndex);
////        } else {
////            if (itemIndex > 0) {
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
////                workT = workTime.getWorkTime(prevWorkTime.getFinishTime(), desiredDuration); //get slice! starting from end of previous slice
////            } else { //itemIndex == 0, allocate time to very first element in list and end recursion
////                workT = workTime.getWorkTime(desiredDuration);
////                if (workTimeCache == null && cacheActive) {
////                    workTimeCache = new ArrayList<>();
////                }
////            }
////            if (cacheActive) { //must cache even null values since cache is an array
////                workTimeCache.add(workT);
////            }
////            return workT;
////        }
////</editor-fold>
//    }
//</editor-fold>
//    WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface item) {
    WorkTimeSlices getAllocatedWorkTimeN(Item item) {
        if (workTimeCache == null) {
            workTimeCache = new HashMap(itemsSortedFiltered.size() + 1); //pre-allocate one for each item, +1 for the item itself in case it's an Item
            for (Item itm : itemsSortedFiltered) {
                long eltDuration = itm.getWorkTimeRequiredFromProvider(ownerItemItemListOrCategory);
                if (itm.isDone()) {
                    workTimeCache.put(itm, null); //null to indicate a Done task with no workTime, contrary to zero-duration tasks which get a slice w a starttime
                } else {
                    WorkTimeSlices newWorkTS = workTimeSlices.getWorkTime(eltDuration, itm); //get workTime in order, slices keep track of up to where slices are already reserved
                    workTimeCache.put(itm, newWorkTS); //added by increasing index in itemsSortedFiltered
                }
            }
            //UII: if the project task itself has its own Remaining, allocate that last (after all its subtasks have had their time allocated)
            //TODO add a setting to allocate to project task *first*? 
//            if (item == ownerItemItemListOrCategory) {// && ownerOrCategory instanceof Item) {
            if (ownerItemItemListOrCategory instanceof Item) {
                Item itemOwner = (Item) ownerItemItemListOrCategory;
                boolean notAProject = !itemOwner.isProject();
                long remainingPrjTask = itemOwner.getRemainingForProjectTaskItself(notAProject); //isProject(): use default estimates for leaf-tasks, not for Project/mother tasks
                if (remainingPrjTask > 0 ||notAProject) {
                    WorkTimeSlices newWorkTS = workTimeSlices.getWorkTime(remainingPrjTask, ownerItemItemListOrCategory);
                    workTimeCache.put(itemOwner, newWorkTS); //add the 
                }
            }
//            }
        }
        return workTimeCache.get(item);
    }

    WorkTimeSlices getAllocatedWorkTimeNOLD1(ItemAndListCommonInterface item) {
//        HashMap<ItemAndListCommonInterface, WorkTimeSlices> workTimeCache = new HashMap();
        if (workTimeCache == null)
            workTimeCache = new HashMap(itemsSortedFiltered.size() + 1); //pre-allocate one for each item, +1 for the item itself

        ItemAndListCommonInterface elt = null;

        if (workTimeCache.get(item) != null) //already calculated
            return workTimeCache.get(item);
        else {
            //not calculated
            //first first element with allocated time
            for (int i = workTimeCache.size(), size = itemsSortedFiltered.size(); i < size; i++) {
                elt = itemsSortedFiltered.get(i);
                long eltDuration = elt.getWorkTimeRequiredFromProvider(ownerItemItemListOrCategory);
//                WorkTimeSlices newWorkTS = null;
//                if (i == 0) {
//                    newWorkTS = workTimeSlices.getWorkTime(eltDuration, elt);
//                } else {
//                    newWorkTS = workTimeSlices.getWorkTime(workTimeCache.get(itemsSortedFiltered.get(i - 1)).getFinishTime(), eltDuration, elt); //get slice! starting from end of previous slice
//                }
                WorkTimeSlices newWorkTS = workTimeSlices.getWorkTime(eltDuration, elt); //get workTime in order, slices keep track of up to where slices are already reserved
                workTimeCache.put(elt, newWorkTS); //added by increasing index in itemsSortedFiltered
                if (elt == item)
                    return newWorkTS;
            }
        }
        ASSERT.that(itemsSortedFiltered == null || itemsSortedFiltered.size() == 0
                || (elt != null && itemsSortedFiltered.get(itemsSortedFiltered.size() - 1) == elt),
                "not at last element, elt=" + elt + ", " + itemsSortedFiltered); //if there are subtasks, then elt is assigned to last element - used below

        if (Config.TEST) ASSERT.that(itemsSortedFiltered.size() == workTimeCache.size(), "error - not all subtasks got allocated a slice, owner=" + ownerItemItemListOrCategory + ", item=" + item);
        //if called with the project task itself, we'll 'fall through' to the following statements to calculate/return the time allocated to the project task itself
        //UII: if the project task itself has its own Remaining, allocate that last (after all its subtasks have had their time allocated)
        //TODO add a setting to allocate to project task *first*? 
        if (item == ownerItemItemListOrCategory && ownerItemItemListOrCategory instanceof Item) {
            Item itemOwner = (Item) ownerItemItemListOrCategory;
            boolean isProject = (elt != null); //itemOwner.isProject();
            long remainingPrjTask = itemOwner.getRemainingForProjectTaskItself(!isProject); //isProject(): use default estimates for leaf-tasks, not for Project/mother tasks
            if (remainingPrjTask > 0) {
//                WorkTimeSlices newWorkTS = null;
//                if (isProject) {
//                    newWorkTS = workTimeSlices.getWorkTime(workTimeCache.get(elt).getFinishTime(), remainingPrjTask, ownerOrCategory); //get slice! starting from end of previous slice
//                } else {
//                    // no subtasks (time has been allocatd to all subtasks when we arrive here)
//                    newWorkTS = workTimeSlices.getWorkTime(remainingPrjTask, ownerOrCategory);
//                }
                WorkTimeSlices newWorkTS = workTimeSlices.getWorkTime(remainingPrjTask, ownerItemItemListOrCategory);
                workTimeCache.put(itemOwner, newWorkTS); //add the 
                return newWorkTS;
            }
        }
        return null;
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (workT == null) {
//                workT = new WorkTimeSlices();
//            }
//            return workT; //returns the last value calculated (which is for itemIndex)
//        }
////        WorkTimeSlices workT;
//        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
//            return workTimeCache.get(itemIndex);
//        } else {
//            if (itemIndex > 0) {
//                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
//                workT = workTime.getWorkTime(prevWorkTime.getFinishTime(), desiredDuration); //get slice! starting from end of previous slice
//            } else { //itemIndex == 0, allocate time to very first element in list and end recursion
//                workT = workTime.getWorkTime(desiredDuration);
//                if (workTimeCache == null && cacheActive) {
//                    workTimeCache = new ArrayList<>();
//                }
//            }
//            if (cacheActive) { //must cache even null values since cache is an array
//                workTimeCache.add(workT);
//            }
//            return workT;
//        }
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getAllocatedWorkTimeOLD4(int itemIndex, long desiredDuration) {
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p(ownerOrCategory + "." + "getAllocatedWorkTime(itemIndex=" + itemIndex + ", duration=" + desiredDuration + "), item=" + itemsSortedFiltered.get(itemIndex));
//        }
//        WorkTimeSlices workT;
//        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
//            return workTimeCache.get(itemIndex);
//        } else {
////<editor-fold defaultstate="collapsed" desc="comment">
//            //            int prevLargestOngoingIndex = -1;
//            //            if (itemIndex >= largestOngoingIndex) { //if same WTD is called recursively for same or higher index, we'll get an infinite loop, eg if a subtask is in same category as a mother task
////        if (owner instanceof Category && ((Category) owner).isOwnerOfItemInCategoryBeforeItem(itemIndex)) {
////            ASSERT.that(false, "allocateWorkTime called with itemIndex=" + itemIndex + " with startIndex=" + largestOngoingIndex + " for owner=" + owner);
//////                return null;
//////                return new WorkTimeSlices(xxx
//////            WorkTimeSlices pWorkTime = itemIndex > 0 ? getAllocatedWorkTimeN(itemIndex - 1) : workTime.getWorkTime(desiredDuration); //recurse
//////            desiredDuration = 0; //force duration to 0 to allocate an empty workslice at the right place
//////            workT = workTime.getWorkTime(pWorkTime.getFinishTime(), 0); //get empty slice starting from end of previous slice
//////                return workTime.getWorkTime(finishTime, 0);
////            workT = null; //null will make WorkTimeAllocator return finishTime of previous
//////                }
////        } else //            else {
////</editor-fold>
//            //<editor-fold defaultstate="collapsed" desc="comment">
//            //                if (itemIndex == lastIndex) {
//            //                    return null;
//            //                } else {
//            //                    prevIndex = lastIndex;
//            //                    lastIndex = itemIndex;
//            //                }
//            //                if (largestOngoingIndex == Integer.MAX_VALUE||itemIndex>largestOngoingIndex) { //ensure startIndex is only set the first time called (when it is -1)
//            //                if (itemIndex > largestOngoingIndex || largestOngoingIndex == Integer.MAX_VALUE) { //ensure startIndex is only set the first time called (when it is -1)
//            //                    prevLargestOngoingIndex = largestOngoingIndex;
//            //                    largestOngoingIndex = itemIndex;
//            //                };//else if (itemIndex>=largestOngoingIndex == Integer.MAX_VALUE)
//            //</editor-fold>
//            if (itemIndex > 0) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1); //recurse
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getRemainingEffort()); //recurse
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromOwner(owner)); //recurse
////                    WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (desiredDuration == 0) {
////                    WorkTimeSlices prevWorkTime_N = getAllocatedWorkTimeN(itemIndex - 1, 0); //recurse
////                    return new WorkTimeSlices() //                    WorkTimeSlices prevWorkTime_N = WorkTimeAllocator.this.getAllocatedWorkTimeN(itemIndex - 1); //recurse
////                WorkTimeSlices prevWorkTime = WorkTimeAllocator.this.getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromProvider(owner)); //recurse
////</editor-fold>
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1); //recurse
//                WorkTimeSlices prevWorkTime = getAllocatedWorkTime(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getWorkTimeRequiredFromProvider(ownerOrCategory)); //recurse
////<editor-fold defaultstate="collapsed" desc="comment">
////                    long finishTime = prevWorkTime_N != null ? prevWorkTime_N.getFinishTime() : 0;
////                    long finishTime;
////                    if (prevWorkTime != null) {
////                        long
////                    }
////                    finishTime =  ? prevWorkTime.getFinishTime() : 0;
////</editor-fold>
//                workT = workTime.getWorkTime(prevWorkTime.getFinishTime(), desiredDuration); //get slice! starting from end of previous slice
////                return workT;
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (false) {
////                        workT = workTime.getWorkTime(getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getRemainingEffort()).getFinishTime(), desiredDuration); //one line version
////                    }
////            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
////</editor-fold>
//            } else { //itemIndex == 0, allocate time to very first element in list and end recursion
////                finishTime = MyDate.MIN_DATE;
////                workT = workTime.getAllocatedWorkTimeN(MyDate.MIN_DATE, desiredDuration);
//                workT = workTime.getWorkTime(desiredDuration);
////                return workT;
////            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
////                if (workTimeCache == null && cacheActive) {
//                if (workTimeCache == null && cacheActiveXXX) {
//                    workTimeCache = new ArrayList<>();
//                }
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            WorkTimeSlices workT = workTime.getAllocatedWorkTimeN(itemIndex, remainingDuration);
////                if (workT!=null&&workTimeCache == null && cacheActive) {
////            if (workTimeCache != null) {
////                if (workT!=null&&cacheActive) {
////</editor-fold>
//            if (cacheActiveXXX) { //must cache even null values since cache is an array
//                workTimeCache.add(workT);
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (largestOngoingIndex == itemIndex) {
////                    largestOngoingIndex = Integer.MAX_VALUE;
////                }
////                lastIndex = prevIndex;
////                if (prevLargestOngoingIndex != -1) {
////                    largestOngoingIndex = prevLargestOngoingIndex;
////                }
////</editor-fold>
//            return workT;
////        }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getWorkTimeOLD(int itemIndex, long desiredDuration) {
//        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
//            return workTimeCache.get(itemIndex);
//        } else {
//            if (itemIndex > largestOngoingIndex) { //if same WTD is called recursively for a higher index, we'll get an infinite loop
//                ASSERT.that(false, "");
//                return null;
//            } else {
//                if (largestOngoingIndex == -1) {
//                    largestOngoingIndex = itemIndex;
//                }
////            WorkTimeSlices workTime = getWorkTimeImpl(itemIndex, remainingDuration);
////            long finishTime;
//                WorkTimeSlices workT;
//                if (itemIndex > 0) {
////            WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1); //recurse
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getRemainingEffort()); //recurse
////                WorkTimeSlices prevWorkTime = getAllocatedWorkTimeN(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromOwner(owner)); //recurse
//                    WorkTimeSlices prevWorkTime = WorkTimeAllocator.this.getAllocatedWorkTime(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getWorkTimeRequiredFromProvider(ownerOrCategory)); //recurse
//                    long finishTime = prevWorkTime.getFinishTime();
//                    workT = workTime.getWorkTime(finishTime, desiredDuration);
//                    if (false) {
//                        workT = workTime.getWorkTime(WorkTimeAllocator.this.getAllocatedWorkTime(itemIndex - 1, itemsSortedFiltered.get(itemIndex - 1).getRemainingEffort()).getFinishTime(), desiredDuration); //one line version
//                    }
////            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
//                } else {
////                finishTime = MyDate.MIN_DATE;
////                workT = workTime.getAllocatedWorkTimeN(MyDate.MIN_DATE, desiredDuration);
//                    workT = workTime.getWorkTime(desiredDuration);
////            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
//                }
////            WorkTimeSlices workT = workTime.getAllocatedWorkTimeN(itemIndex, remainingDuration);
//                if (workTimeCache == null && cacheActiveXXX) {
//                    workTimeCache = new ArrayList<>();
//                }
////            if (workTimeCache != null) {
//                if (cacheActiveXXX) {
//                    workTimeCache.add(workT);
//                }
//                if (largestOngoingIndex == itemIndex) {
//                    largestOngoingIndex = -1;
//                }
//                return workT;
//            }
//        }
//    }
//</editor-fold>
    /**
     * call whenever an Item changes its remaining time (eg gets Done or
     * estimates updated) or if order of items change in list (drag and drop),
     * new item inserted into list, ...
     *
     * @param itemIndex index for item that has changed or first index where a
     * new item has been inserted
     */
    private void resetCache() {
//        resetCache(0); //TODO
//        workTimeCache = null;
        initAndReset();
    }

//    private void resetCacheXXX(int fromIndex) {
//        if (workTimeCache != null && workTimeCache.size() > 0 && fromIndex >= 0) {
////            workTimeCache.removeAll(workTimeCache.subList(fromIndex, workTimeCache.size() - 1));
////            while (workTime.workSlotSlices.size() > fromIndex) {
////                workTime.workSlotSlices.remove(fromIndex);
////            }
////            resetCache(fromIndex);
//            workTimeCache.subList(fromIndex, workTimeCache.size() - 1).clear();
//        }
//    }
    /**
     * reset cache starting from the first item with time allocated from
     * changedWorkSlot. to use when a workSlot has changed.
     *
     * @param changedWorkSlot
     */
//    public void resetCachedValuesXXX(WorkSlot changedWorkSlot) {
//        //find first workTime that contains changedWorkSlot and delete/reset that and all following
//        if (workTimeCache != null) {
//            for (int i = 0, size = workTimeCache.size(); i < size; i++) {
//                WorkTimeSlices workTime = workTimeCache.get(i);
//                for (int i2 = 0, size2 = workTime.getWorkSlotSlices().size(); i2 < size2; i2++) {
//                    if (workTime.getWorkSlotSlices().get(i2).workSlot == changedWorkSlot) {
////                        while (workTime.workSlotSlices.size() > i2) {
////                            workTime.workSlotSlices.remove(i2);
////                        }
//                        resetCacheXXX(i2);
//                    }
//                }
//            }
//        }
//    }
    /**
     * reset cache for all values starting with changedItem. to use when an item
     * has changed
     *
     * @param changedItem
     */
//    public void resetCachedValuesXXX(ItemAndListCommonInterface changedItem) {
//        if (workTimeCache != null) {
//            int index = workTimeCache.indexOf(changedItem);
////            if (index >= 0) {
////                while (workTime.workSlotSlices.size() > index) {
////                    workTime.workSlotSlices.remove(index);
////                }
//            resetCacheXXX(index);
////            }
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void resetCachedValuesOnWorkSlotChange(int indexOfChangedWorkSlot) {
//        if (workTimeCache != null) {
//            for (int i = 0, size = workTimeCache.size(); i < size; i++) {
//                WorkTimeSlices workTime = workTimeCache.get(i);
//                if (workTime.getLastWorkSlotIndex() >= indexOfChangedWorkSlot) {
//                    resetCache(workTime.getLastWorkSlotIndex());
//                    break;
//                }
//            }
//        }
//    }
//</editor-fold>
    /**
     * completely reset cache. Use on every time a screen is refreshed for now
     * (optimize later).
     */
//    private void resetCachedValuesOnItemChangeXXX(int itemIndex) {
//        resetCacheXXX(itemIndex);
//    }
}
