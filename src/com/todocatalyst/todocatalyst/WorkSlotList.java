/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import static com.todocatalyst.todocatalyst.FilterSortDef.getSortingComparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class WorkSlotList {//extends ArrayList<WorkSlot> {

    private long now;//=-1; //ensure a single value of now is used for the work slots
    private ArrayList<WorkSlot> workslotList = new ArrayList<>();

    public WorkSlotList() {
        super();
//        if (now==-1) 
        now = System.currentTimeMillis();
    }

    /**
    add workSlot *sorted*
    @param workSlot 
     */
    public void add(WorkSlot workSlot) {
//        workslotList.add(workSlot);
//        sortWorkSlotList(); //TODO!!! //optimization: insert sorted instead of do entire bubblesort
        for (int i = workslotList.size() - 1; i >= 0; i--) {
            if (workslotList.get(i).getStartTimeD().getTime() < workSlot.getStartTimeD().getTime()) {
                workslotList.add(i + 1, workSlot);
                return;
            }
        }
        //if no earlier slot found above, then insert at start of (possibly empty) list
        workslotList.add(0, workSlot);
        //simple solution: new workSlots are likely be the most recent, so simply search from end of list to find where to insert
    }

    public WorkSlot get(int index) {
        return workslotList.get(index);
    }

    public boolean contains(WorkSlot workSlot) {
        return workslotList.contains(workSlot);
    }

    public List<WorkSlot> getWorkSlotList() {
        return workslotList;
    }

    public WorkSlotList(List<WorkSlot> list, boolean alreadySorted, boolean removeExpiredWorkSlots) {
        this();
//<editor-fold defaultstate="collapsed" desc="comment">
        //http://stackoverflow.com/questions/8441664/how-do-i-copy-the-contents-of-one-arraylist-into-another
        //http://stackoverflow.com/questions/17036405/how-to-copy-a-array-into-another-array-that-already-has-data-in-it
//        if (false) {
//            if (false) {
//                int size = list.size();
//                workslotList.ensureCapacity(size);
//                System.arraycopy(list.toArray(), 0, this, 0, size);
//            } else {
////            if (workslots==null)
////                workslots= new ArrayList<>();
//                if (workslotList.isEmpty()) {
//                    workslotList.addAll(list); //optimization
//                } else {
//                    for (WorkSlot ws : list) {
//                        if (!workslotList.contains(ws)) {
//                            workslotList.add(ws); //optimization
//                        }
//                    }
//                }
//            }
//        }
//</editor-fold>
        if (!alreadySorted) {
            sortWorkSlotList();
        }
        if (removeExpiredWorkSlots) {
            workslotList.addAll(removePastWorkSlots(list, now)); //optimization - this makes a copy of the list, is it really necessary??
        } else {
            workslotList.addAll(list); //optimization - this makes a copy of the list, is it really necessary??
        }//        this.copyOf(list);
    }

    public WorkSlotList(List<WorkSlot> list, boolean alreadySorted) {
        this(list, alreadySorted, true);
    }

    public WorkSlotList(List<WorkSlot> list) {
        this(list, false);
    }

    public long getNow() {
        return now;
    }
//    static public long getWorkTimeSum(List<WorkSlot> list) {

    public long getWorkTimeSum(List<WorkSlot> list) {
//        List<WorkSlot> list = getWorkSlotListN(false);
        long sum = 0;
//        long now = System.currentTimeMillis();
        for (WorkSlot workSlot : list) {
            sum += workSlot.getDurationAdjusted(now);
        }
        return sum;
    }

    static public long getWorkTimeSum(List<WorkSlot> list, long fromTime, long toTime) {
//        long fromTime = fromDate.getTime();
//        long toTime = toDate.getTime();

        long sum = 0;
        for (WorkSlot workSlot : list) {
            sum += workSlot.getDurationAdjusted(fromTime, toTime);
        }
        return sum;
    }

    static public long getWorkTimeSum(List<WorkSlot> list, Date fromDate, Date toDate) {
        return getWorkTimeSum(list, fromDate.getTime(), toDate.getTime());
    }

    public long getWorkTimeSum(Date fromDate, Date toDate) {
//        return getWorkTimeSum(this, fromDate, toDate);
        return getWorkTimeSum(workslotList, fromDate.getTime(), toDate.getTime());
    }

    /**
     * return the remaining work time (from now till eternity)
     *
     * @return
     */
    public long getWorkTimeSum() {
//        return getWorkTimeSum(this, new Date(), new Date(Long.MAX_VALUE));
        return getWorkTimeSum(workslotList, new Date(), new Date(Long.MAX_VALUE));
    }

    /**
     * sort on WorkSLot startTime
     */
    public WorkSlotList sortWorkSlotList() {
        return sortWorkSlotList(false);
    }

    static Comparator<WorkSlot> getMultipleComparator(Comparator<WorkSlot>[] comparators) {
        Comparator<WorkSlot> comp1 = comparators.length >= 1 ? comparators[0] : null;
        Comparator<WorkSlot> comp2 = comparators.length >= 2 ? comparators[1] : null;
        Comparator<WorkSlot> comp3 = comparators.length >= 3 ? comparators[2] : null;

        return (i1, i2) -> {
            //http://stackoverflow.com/questions/23981199/java-comparator-for-objects-with-multiple-fields            
            int res1 = comp1.compare(i1, i2);
            if (res1 != 0) {
                return res1;
            }
            if (comp2 == null) {
                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
            }
            res1 = comp2.compare(i1, i2);
            if (res1 != 0) {
                return res1;
            }
            if (comp3 == null) {
                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
            }
            res1 = comp3.compare(i1, i2);
            if (res1 != 0) {
                return res1;
            }
            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
        };
    }

    static Comparator<WorkSlot> getMultipleComparatorOLD(Comparator<WorkSlot>[] comparators) {
//        assert comparators.length >= 1 && comparators.length == sortDescending.length : "must be same length";
//        for (int i = 0, size = getSortFieldId.length; i < size; i++) {
//            Comparator<Item> comp1 = getSortingComparator(getSortFieldId[0], sortDescending[0]);
//        }
//        Comparator<Item> comp1 = getSortFieldId.length >= 1 ? getSortingComparator(getSortFieldId[0], sortDescending[0]) : null;
//        Comparator<Item> comp2 = getSortFieldId.length >= 2 ? getSortingComparator(getSortFieldId[1], sortDescending[1]) : null;
//        Comparator<Item> comp3 = getSortFieldId.length >= 3 ? getSortingComparator(getSortFieldId[2], sortDescending[2]) : null;
        Comparator<WorkSlot> comp1 = comparators.length >= 1 ? comparators[0] : null;
        Comparator<WorkSlot> comp2 = comparators.length >= 2 ? comparators[1] : null;
        Comparator<WorkSlot> comp3 = comparators.length >= 3 ? comparators[2] : null;

        return (i1, i2) -> {
            int res1 = comp1.compare(i1, i2);
            if (res1 != 0) {
                return res1;
            } else {
                if (comp2 == null) {
//                    return 0; //TODO!!!! should compare eg objectId to ensure a consistent ordering on every sort
                    return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                } else {
                    int res2 = comp2.compare(i1, i2);
                    if (res2 != 0) {
                        return res2;
                    } else {
                        if (comp3 == null) {
//                            return 0;
                            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                        } else {
                            int res3 = comp3.compare(i1, i2);
                            if (res3 != 0) {
                                return res3;
                            } else {
//                                return 0;
                                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                            }
                        }
                    }
                }
            }
        };
    }

    public WorkSlotList sortWorkSlotList(boolean sortOnEndTime) {
//        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
        if (sortOnEndTime) {
            Collections.sort(workslotList, (i1, i2) -> FilterSortDef.compareLong(i1.getEndTime(), i2.getEndTime()));
        } else {
            Collections.sort(workslotList, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
        }
        return this;
    }

    public void sortWorkSlotList2(boolean sortOnEndTime) {
//        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
        if (sortOnEndTime) {
            Collections.sort(workslotList, getMultipleComparator(new Comparator[]{
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()),
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD())
            }));
        } else {
            Collections.sort(workslotList, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
        }
    }

    /**
     * returns a list with all the workSlots that has some work time between startDate and
     * endDate (e.g. includes workslots that start *before* startDate but which
     * endsfor date. assumes that workslotsSortedOnStartDate has not dates
     * *earlier* than date (ie either the first workslot starts on date, or it
     * starts on a later date). Doesn't remove the return WorkSlots for the
     * WorkSlotList.
     *
     * NB! the returned WorkSlots may start *before* the startDate or end
     * *after* the endDate, so each workSlot must be restricted to the given
     * interval when used in calculations
     *
     * @param workslots
     * @param startDate
     * @param endDate
     * @param includeFullDay will extend the start/endDates to the start or end
     * of the indicated day (midnigth)
     * @param isSorted indicates the workSlots list is already sorted on
     * startDate
     * @return
     */
    public WorkSlotList getWorkSlotsInInterval(Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
        return removeWorkSlotsInInterval(this, startDate, endDate, includeFullDay, isSorted);
    }

    public int size() {
        return workslotList.size();
    }

    /**
    remove workSlots that are expired, used to only calculate finishTime based on current workslots
    @param workSlotList
    @param now
    @return 
     */
    public static List<WorkSlot> removePastWorkSlots(List<WorkSlot> workSlots, long now) {
        final int WORKSLOT_LIMIT = 200;

        List<WorkSlot> result = new ArrayList<>();

//        ASSERT.that(!workSlots.isSorted(), "workSlots must be sorted for this algo to work");
//        long nowLong = now.getTime();
//        List<WorkSlot>  workSlots =workSlotList.getWorkSlotList();
        for (int i = workSlots.size() - 1; i >= 0; i--) {
            if (workSlots.get(i).getEndTime()<= now) {
                result = workSlots.subList(i, workSlots.size() - 1);
                return result;
            }
        }
        //if we get to here, no workslots had endTime in the past, so we don't have to remove
        return workSlots;
    }

    public static WorkSlotList removeWorkSlotsInInterval(WorkSlotList workSlotList, Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
        final int WORKSLOT_LIMIT = 200;
        long startTime = includeFullDay ? MyDate.getStartOfDay(startDate).getTime() : startDate.getTime();
        long endTime = includeFullDay ? MyDate.getEndOfDay(endDate).getTime() : endDate.getTime();

//        WorkSlotList result = new WorkSlotList();
        ArrayList<WorkSlot> result = new ArrayList<>();

//        if (workSlotList.size() > WORKSLOT_LIMIT) { //if more than 20 elements
        if (!isSorted) {
            workSlotList.sortWorkSlotList(false);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            Iterator<WorkSlot> it = workSlotList.iterator();
//            WorkSlot workSlot = null;
//            //for all elements, efficient when elements are removed (it skips going through elements that that start *after* endTime)
//            while (it.hasNext() && (workSlot = it.next()).getStartTimeD().getTime() < endTime) { //if workSlot.getStartTime() > endTime, then the workSlot is definitely outside the desired interval
//                if (workSlot.hasDurationInInterval(startTime, endTime)) {
//                    result.add(workSlot);
//                    if (false) { //don't remove since some workSlots may fall within different intervals, eg if stretching over midnight
//                        it.remove();
//                    }
//                }
//            } //skip all elements that end *before* startTime, stops nu with workSlot
//</editor-fold>
        //skip all elements that end *before* startTime, stops nu with workSlot
        for (WorkSlot ws : workSlotList.workslotList) { //optimization: optimize for fact that workslotList is now sorted
            if (ws.getStartTimeD().getTime() < endTime && ws.hasDurationInInterval(startTime, endTime)) {
                result.add(ws);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else {
//            //go through every workslot (less efficient if many, but doesn't require them to be sorted)
//            Iterator<WorkSlot> it = workSlotList.iterator();
//            while (it.hasNext()) {
//                WorkSlot workSlot = it.next();
//                if (workSlot.hasDurationInInterval(startTime, endTime)) {
//                    result.add(workSlot);
//                    if (false && workSlot.getDurationAdjusted(startTime, endTime) == workSlot.getDurationInMillis()) { //do not remove if some of the workSlots falls outside the interval (meaning it should also be return for the other interval)
//                        it.remove();
//                    }
//                }
//            }
//        }
//</editor-fold>
        return new WorkSlotList(result);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static WorkSlotList removeWorkSlotsInInterval(WorkSlotList workSlots, Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
//        final int WORKSLOT_LIMIT = 200;
//        long startTime = includeFullDay ? MyDate.getStartOfDay(startDate).getTime() : startDate.getTime();
//        long endTime = includeFullDay ? MyDate.getEndOfDay(endDate).getTime() : endDate.getTime();
//
//        WorkSlotList result = new WorkSlotList();
//
//        if (workSlots.size() > WORKSLOT_LIMIT) { //if more than 20 elements
//            if (!isSorted) {
//                workSlots.sortWorkSlotList(false);
//            }
//            Iterator<WorkSlot> it = workSlots.iterator();
//            WorkSlot workSlot = null;
//            //for all elements, efficient when elements are removed (it skips going through elements that that start *after* endTime)
//            while (it.hasNext() && (workSlot = it.next()).getStartTimeD().getTime() < endTime) { //if workSlot.getStartTime() > endTime, then the workSlot is definitely outside the desired interval
//                if (workSlot.hasDurationInInterval(startTime, endTime)) {
//                    result.add(workSlot);
//                    if (false) { //don't remove since some workSlots may fall within different intervals, eg if stretching over midnight
//                        it.remove();
//                    }
//                }
//            } //skip all elements that end *before* startTime, stops nu with workSlot
//        } else {
//            //go through every workslot (less efficient if many, but doesn't require them to be sorted)
//            Iterator<WorkSlot> it = workSlots.iterator();
//            while (it.hasNext()) {
//                WorkSlot workSlot = it.next();
//                if (workSlot.hasDurationInInterval(startTime, endTime)) {
//                    result.add(workSlot);
//                    if (false && workSlot.getDurationAdjusted(startTime, endTime) == workSlot.getDurationInMillis()) { //do not remove if some of the workSlots falls outside the interval (meaning it should also be return for the other interval)
//                        it.remove();
//                    }
//                }
//            }
//        }
//        return result;
//    }
//
//</editor-fold>
    /**
     * returns true if there are future workslots (the list may contain expired
     * workslots). The use of this function to test find the workslots to use to
     * calculate finishTime also means that TDC will 'fall back' to using other
     * workslots. E.g. if a subproject has defined workTime which then expired,
     */
    public boolean hasComingWorkSlots() {
//        return hasComingWorkSlots(System.currentTimeMillis());
        return hasComingWorkSlots(now);
    }

    public boolean hasComingWorkSlots(long now) {
        if (size() == 0) {
            return false; //return size()>0; //TODO!!!!!
        }
//        long now = System.currentTimeMillis();
//        for (WorkSlot workSlot : workslotList) {
//            if (workSlot.getEndTime() > now) {
//                return true;
//            }
//        }
        for (int i = workslotList.size() - 1; i >= 0; i--) {
            if (workslotList.get(i).getEndTime() > now) {
                return true;
            }
        }
        return false;
    }

}
