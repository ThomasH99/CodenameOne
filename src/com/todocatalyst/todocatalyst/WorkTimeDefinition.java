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
public class WorkTimeDefinition { //implements Externalizable { //extends ItemList {

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
    private WorkTime workTime;// = new ItemList(); //lazy
    private List<ItemAndListCommonInterface> items;
    List<WorkTime> workTimeCache = new ArrayList();
    private boolean cacheActive = false;
    private ItemAndListCommonInterface owner;// = new ItemList(); //lazy

//    private Hashtable<ItemAndListCommonInterface, WorkTimeInfo> workSlotInfoHashTable; // = new Hashtable();
//    WorkTimeDefinition(List<ItemAndListCommonInterface> listOfItemsOrItemListsFilteredSorted, WorkSlotList workSlots) {
    WorkTimeDefinition(List<ItemAndListCommonInterface> listOfItemsOrItemListsFilteredSorted, WorkTime workTime, ItemAndListCommonInterface owner) {
        this.items = listOfItemsOrItemListsFilteredSorted;
//        this.orgItemOrList = orgItemOrList;
        this.workTime = workTime;
        this.owner = owner;
//        init();
//        workSlotInfoList = new ArrayList();
//        workSlotInfoHashTable = new Hashtable();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeDefinition(WorkTime workTime) {
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
     * @param duration
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTime calculateWorkTime(WorkTime prevWorkTime) {
//        return calculateWorkTime(prevWorkTime.getLastWorkSlotIndex(), prevWorkTime.getFinishTime(), prevWorkTime.getRemainingDuration());
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTime calculateWorkTime(int workSlotIndex, long startTime, long remainingDuration) {
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
////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTime
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
//        return new WorkTime(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTime calculateWorkTimeOLD(int workSlotIndex, long startTime, long remainingDuration) {
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
////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTime
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
//        return new WorkTime(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private WorkTime getWorkTimeImpl(int itemIndex, long finishTime) {
////        long finishTime; // = MyDate.MIN_DATE;
//        Integer startWorkSlotIndex; // = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
//
//        if (itemIndex > 0) {
//            WorkTime prevWorkTime = getWorkTime(itemIndex - 1); //recurse
//            finishTime = prevWorkTime.getFinishTime();
//            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
//        } else {
//            finishTime = MyDate.MIN_DATE;
//            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
//        }
//
////        return calculateWorkTime(startWorkSlotIndex, finishTime, items.get(itemIndex).getRemainingEffort());
//        return workTime.getWorkTime(startWorkSlotIndex, finishTime, items.get(itemIndex).getRemainingEffort());
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
////        return new WorkTime(workSlots, startTime, endTime, remainingDuration);
////</editor-fold>
//    }
//</editor-fold>
    public WorkTime getWorkTime(ItemAndListCommonInterface item, long remainingDuration) {
        int itemIndex = items.indexOf(item);
        return getWorkTime(itemIndex, remainingDuration);
    }

    /**
     * returns workTime for the remaining effort of the item
     *
     * @param item
     * @return
     */
    public WorkTime getWorkTime(ItemAndListCommonInterface item) {
        int itemIndex = items.indexOf(item);
        return getWorkTime(itemIndex, item.getRemainingEffort());
    }

    public WorkTime getWorkTime(int itemIndex) {
//        ItemAndListCommonInterface item = items.get(itemIndex);
        return getWorkTime(itemIndex, items.get(itemIndex).getRemainingEffort());
    }

    /**
     * returns workTime for the item at index itemIndex and for an effort of
     * remainingTime
     *
     * @param itemIndex
     * @param desiredDuration
     * @return
     */
    public WorkTime getWorkTime(int itemIndex, long desiredDuration) {
        if (workTimeCache != null && itemIndex >= 0 && itemIndex < workTimeCache.size()) {
            return workTimeCache.get(itemIndex);
        } else {
//            WorkTime workTime = getWorkTimeImpl(itemIndex, remainingDuration);
//            long finishTime;
            WorkTime workT;
            if (itemIndex > 0) {
//            WorkTime prevWorkTime = getWorkTime(itemIndex - 1); //recurse
//                WorkTime prevWorkTime = getWorkTime(itemIndex - 1, items.get(itemIndex - 1).getRemainingEffort()); //recurse
//                WorkTime prevWorkTime = getWorkTime(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromOwner(owner)); //recurse
                WorkTime prevWorkTime = getWorkTime(itemIndex - 1, items.get(itemIndex - 1).getWorkTimeRequiredFromOwner()); //recurse
                long finishTime = prevWorkTime.getFinishTime();
                workT = workTime.getWorkTime(finishTime, desiredDuration);
                if (false) {
                    workT = workTime.getWorkTime(getWorkTime(itemIndex - 1, items.get(itemIndex - 1).getRemainingEffort()).getFinishTime(), desiredDuration); //one line version
                }
//            startWorkSlotIndex = prevWorkTime.getLastWorkSlotIndex();
            } else {
//                finishTime = MyDate.MIN_DATE;
//                workT = workTime.getWorkTime(MyDate.MIN_DATE, desiredDuration);
                workT = workTime.getWorkTime(desiredDuration);
//            startWorkSlotIndex = 0; //Integer so that getWorkTimeFromSlots can return update workSlots index
            }
//            WorkTime workT = workTime.getWorkTime(itemIndex, remainingDuration);
            if (workTimeCache == null && cacheActive) {
                workTimeCache = new ArrayList<>();
            }
//            if (workTimeCache != null) {
            if (cacheActive) {
                workTimeCache.add(workT);
            }
            return workT;
        }
    }

    /**
     * call whenever an Item changes its remaining time (eg gets Done or
     * estimates updated) or if order of items change in list (drag and drop),
     * new item inserted into list, ...
     *
     * @param itemIndex index for item that has changed or first index where a
     * new item has been inserted
     */
    private void resetCache(int fromIndex) {
        if (workTimeCache != null && fromIndex >= 0) {
//            workTimeCache.removeAll(workTimeCache.subList(fromIndex, workTimeCache.size() - 1));
//            while (workTime.workSlotSlices.size() > fromIndex) {
//                workTime.workSlotSlices.remove(fromIndex);
//            }
            resetCache(fromIndex);
        }
    }

    public void resetCachedValues(WorkSlot changedWorkSlot) {
        //find first workTime that contains changedWorkSlot and delete/reset that and all following
        if (workTimeCache != null) {
            for (int i = 0, size = workTimeCache.size(); i < size; i++) {
                WorkTime workTime = workTimeCache.get(i);
                for (int i2 = 0, size2 = workTime.getWorkSlotSlices().size(); i2 < size2; i2++) {
                    if (workTime.getWorkSlotSlices().get(i2).workSlot == changedWorkSlot) {
//                        while (workTime.workSlotSlices.size() > i2) {
//                            workTime.workSlotSlices.remove(i2);
//                        }
                        resetCache(i2);
                    }
                }
            }
        }
    }

    public void resetCachedValues(ItemAndListCommonInterface changedItem) {
        if (workTimeCache != null) {
            int index = workTimeCache.indexOf(changedItem);
//            if (index >= 0) {
//                while (workTime.workSlotSlices.size() > index) {
//                    workTime.workSlotSlices.remove(index);
//                }
            resetCache(index);
//            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void resetCachedValuesOnWorkSlotChange(int indexOfChangedWorkSlot) {
//        if (workTimeCache != null) {
//            for (int i = 0, size = workTimeCache.size(); i < size; i++) {
//                WorkTime workTime = workTimeCache.get(i);
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
    public void resetCache() {
        resetCache(0);
    }

    private void resetCachedValuesOnItemChange(int itemIndex) {
        resetCache(itemIndex);
    }

}
