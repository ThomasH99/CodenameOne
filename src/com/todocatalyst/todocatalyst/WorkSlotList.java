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
public class WorkSlotList extends ArrayList<WorkSlot> {

    public WorkSlotList() {
        super();
    }

    public WorkSlotList(List<WorkSlot> list) {
        //http://stackoverflow.com/questions/8441664/how-do-i-copy-the-contents-of-one-arraylist-into-another
        //http://stackoverflow.com/questions/17036405/how-to-copy-a-array-into-another-array-that-already-has-data-in-it
        if (false) {
            int size = list.size();
            ensureCapacity(size);
            System.arraycopy(list.toArray(), 0, this, 0, size);

        } else {
            addAll(list); //optimization
        }
//        this.copyOf(list);
    }

    static public long getWorkTimeSum(List<WorkSlot> list) {
//        List<WorkSlot> list = getWorkSlotList(false);
        long sum = 0;
        long now = System.currentTimeMillis();
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
        return getWorkTimeSum(this, fromDate, toDate);
    }

    /**
     * return the remaining work time (from now till eternity)
     *
     * @return
     */
    public long getWorkTimeSum() {
        return getWorkTimeSum(this, new Date(), new Date(Long.MAX_VALUE));
    }

    /**
     * sort on WorkSLot startTime
     */
    public void sortWorkSlotList() {
        sortWorkSlotList(false);
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

    public void sortWorkSlotList(boolean sortOnEndTime) {
//        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
        if (sortOnEndTime) {
            Collections.sort(this, (i1, i2) -> FilterSortDef.compareLong(i1.getEndTime(), i2.getEndTime()));
        } else {
            Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
        }
    }

    public void sortWorkSlotList2(boolean sortOnEndTime) {
//        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
        if (sortOnEndTime) {
            Collections.sort(this, getMultipleComparator(new Comparator[]{
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()),
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD())
            }));
        } else {
            Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
        }
    }

    /**
     * returns all the workSlots that has some work time between startDate and
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

    public static WorkSlotList removeWorkSlotsInInterval(WorkSlotList workSlots, Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
        final int WORKSLOT_LIMIT = 200;
        long startTime = includeFullDay ? MyDate.getStartOfDay(startDate).getTime() : startDate.getTime();
        long endTime = includeFullDay ? MyDate.getEndOfDay(endDate).getTime() : endDate.getTime();

        WorkSlotList result = new WorkSlotList();

        if (workSlots.size() > WORKSLOT_LIMIT) { //if more than 20 elements
            if (!isSorted) {
                workSlots.sortWorkSlotList(false);
            }
            Iterator<WorkSlot> it = workSlots.iterator();
            WorkSlot workSlot = null;
            //for all elements, efficient when elements are removed (it skips going through elements that that start *after* endTime)
            while (it.hasNext() && (workSlot = it.next()).getStartTimeD().getTime() < endTime) { //if workSlot.getStartTime() > endTime, then the workSlot is definitely outside the desired interval
                if (workSlot.hasDurationInInterval(startTime, endTime)) {
                    result.add(workSlot);
                    if (false) { //don't remove since some workSlots may fall within different intervals, eg if stretching over midnight
                        it.remove();
                    }
                }
            } //skip all elements that end *before* startTime, stops nu with workSlot
        } else {
            //go through every workslot (less efficient if many, but doesn't require them to be sorted)
            Iterator<WorkSlot> it = workSlots.iterator();
            while (it.hasNext()) {
                WorkSlot workSlot = it.next();
                if (workSlot.hasDurationInInterval(startTime, endTime)) {
                    result.add(workSlot);
                    if (false && workSlot.getDurationAdjusted(startTime, endTime) == workSlot.getDurationInMillis()) { //do not remove if some of the workSlots falls outside the interval (meaning it should also be return for the other interval)
                        it.remove();
                    }
                }
            }
        }
        return result;
    }

    /**
     * returns true if there are future workslots (the list may contain expired
     * workslots). The use of this function to test find the workslots to use to
     * calculate finishTime also means that TDC will 'fall back' to using other
     * workslots. E.g. if a subproject has defined workTime which then expired,
     */
    public boolean hasComingWorkSlots() {
        if (size() == 0) {
            return false; //return size()>0; //TODO!!!!!
        }
        long now = System.currentTimeMillis();
        for (WorkSlot workSlot : this) {
            if (workSlot.getEndTime() > now) {
                return true;
            }
        }
        return false;
    }

}
