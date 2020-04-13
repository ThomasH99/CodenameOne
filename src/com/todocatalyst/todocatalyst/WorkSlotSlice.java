/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 *
 * @author thomashjelm
 */
/**
 * in which workSlot does the task with this index finish
 */
//    private int lastWorkSlotIndex = -1;
class WorkSlotSlice implements Work {

    WorkSlot workSlot;

    private long startTime;// = Long.MIN_VALUE;
    private long endTime;// = Long.MAX_VALUE;
//    long missingDuration;// = 0;
    private long now; //DEBUG: keep for the ASSERT statements
    private ItemAndListCommonInterface allocatedToXXX;

    public WorkSlot getWorkSlot() {
        return workSlot;
    }

    public void setWorkSlot(WorkSlot workSlot) {
        this.workSlot = workSlot;
    }

    @Override
    public String toString() {
        return "Slice[" + MyDate.formatDateTimeNew(new MyDate(startTime)) + "-" + MyDate.formatTimeNew(new MyDate(endTime))
                + " " + MyDate.formatDurationShort(endTime - startTime, true)
                //                + " Mis=" + MyDate.formatDurationShort(missingDuration, true)
                + " WS:" + (workSlot != null ? workSlot.toString() : "NONE?!")
                + " O:" + (workSlot != null && workSlot.getOwner() != null ? workSlot.getOwner().getText() : "<null>")
                + " AllTo:" + (allocatedToXXX != null ? allocatedToXXX.getText() : "<null>")
                //                + (allocatedToXXX != null ? ( " AllocTo:" +allocatedToXXX.getText() ): "")
                + "]";
//                + " of "
//                + (workSlot != null ? new Date(workSlot.getDurationAdjusted()) : "null") + "-"
//                + (workSlot != null ? new Date(workSlot.getEndTime()) : "null");
    }

//    WorkSlotSlice(WorkSlot workSlot, long startTime, long endTime, long missingDuration) {
    WorkSlotSlice(WorkSlot workSlot, long startTime, long endTime) {
        this.workSlot = workSlot;
//            this.startTime = startTime < workSlot.getStartAdjusted() ? workSlot.getStartAdjusted() : startTime;
//        this.startTime = Math.max(startTime, workSlot.getStartAdjusted()); //use the larger value (knowing that startTime must never be smaller than getStartAdjusted())
        this.startTime = startTime; //use the larger value (knowing that startTime must never be smaller than getStartAdjusted()) //DONT add getStartAdjusted, may interfere with calculation
//            this.endTime = endTime;
//            this.endTime = workSlot.getEndTime() < endTime ? workSlot.getEndTime() : endTime; //endTime < workSlot.getEndTime() ? endTime : workSlot.getEndTime();
//        this.endTime = Math.min(workSlot.getEndTime(), endTime); //use the smaller value (knowing that endTime must never be larger than getEndTime())
        this.endTime = endTime; //use the smaller value (knowing that endTime must never be larger than getEndTime())
//        this.missingDuration = missingDuration;
//        ASSERT.that(startTime >= workSlot.getStartAdjusted(), "startTime:" + new Date(startTime) + " must be greater than or equal to workSlot.getStartAdjusted():" + new Date(workSlot.getStartAdjusted()));
        if (Config.WORKTIME_TEST) {
            ASSERT.that(startTime < this.endTime, "0 duration workSlice!!: startTime:" + new MyDate(startTime) + ", endTime=" + new MyDate(endTime) + ", workSlot.getStartAdjusted():" + new MyDate(workSlot.getStartAdjusted(now)));
        }
        if (Config.WORKTIME_TEST) {
            ASSERT.that(startTime >= workSlot.getStartAdjusted(now), "startTime:" + new MyDate(startTime) + " must be greater than or equal to workSlot.getStartAdjusted():" + new MyDate(workSlot.getStartAdjusted(now)));
        }
        if (Config.WORKTIME_TEST) {
            ASSERT.that(endTime <= workSlot.getEndTime(), "endTime:" + new MyDate(endTime) + "must be less than workSlot.getEndTime():" + new MyDate(workSlot.getEndTime()));
        }
        if (Config.WORKTIME_TEST) {
            ASSERT.that(endTime >= startTime, "endTime < startTime!!, WorkSlotSlice=" + this);
        }

//        ASSERT.that(startTime != endTime, "zero duration workSlotSlice not allowed, startTime:" + new Date(startTime) + " endTime:" + new Date(endTime));
    }

//    WorkSlotSlice(WorkSlot workSlot, long startTime, long endTime) {
//        this(workSlot, startTime, endTime, 0);
//    }
//    WorkSlotSlice(WorkSlot workSlot) {
//        
//    }
    /**
     * create a workslice with the entire (available, starting from now) of the
     * workslot
     *
     * @param workSlot
     * @param now ensure the same 'now' is used everywhere during the
     * calculation
     */
    WorkSlotSlice(WorkSlot workSlot, long now) {
//            this.workSlot = workSlot;
//            this(workSlot, Long.MIN_VALUE, Long.MAX_VALUE);
        this(workSlot, workSlot.getStartAdjusted(now), workSlot.getEndTime());
        if (Config.WORKTIME_TEST) {
            ASSERT.that(endTime >= startTime, "endTime < startTime!!, WorkSlotSlice=" + this);
        }

//        if (Config.WORKTIME_TEST) {
//            this.now = now;
//        }
    }

//    private WorkSlotSlice getSlice(long startTime, long duration, ItemAndListCommonInterface allocatedTo) {
    private WorkSlotSlice getSliceN(long startTime, long duration, Item allocatedTo) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (startTime == MyDate.MIN_DATE) {
//                startTime = workSlot.getStartAdjusted();
//            }
//        ASSERT.that(duration != 0, "duration must be !=0, is=" + duration);
//        ASSERT.that(startTime >= workSlot.getStartAdjusted(), "startTime:" + new Date(startTime) + " must be >= workSlot.getStartAdjusted():" + new Date(workSlot.getStartAdjusted()));
//        ASSERT.that(startTime <= workSlot.getEndTime(), "startTime:" + new Date(startTime) + " must be <= workSlot.getEndTime():" + new Date(workSlot.getEndTime()));
//            ASSERT.that(endTime <= workSlot.getEndTime(), "endTime:" + new Date(endTime) + "must be less than workSlot.getEndTime():" + new Date(workSlot.getEndTime()));

//            return new WorkSlotSlice(workSlot, startTime, Math.min(startTime + duration, endTime), Math.max(0, (startTime + duration) - endTime));
//if (duration>0 && )
//        if (duration > 0 && startTime >= this.startTime && startTime < this.endTime) { //TODO optimization: simplify/optimize epxression
//        if (startTime >= this.startTime && startTime < this.endTime) { //TODO optimization: simplify/optimize epxression
//</editor-fold>
        //if either duration==0 or startTime==endTime, an empty slide will be allocated
        this.allocatedToXXX = allocatedTo;
//        workSlot.addItemWithSlice(allocatedTo);
        long actualStartTime = Math.max(startTime, this.startTime);
//<editor-fold defaultstate="collapsed" desc="comment">
//        long actualSliceEndTime = Math.min(startTime + duration, endTime); //endTime: only allocate to endTime if slice is too small to allocate full duration
//        return new WorkSlotSlice(workSlot, startTime,
//                actualSliceEndTime,
//                //                Math.max(0, (startTime + duration) - Math.min(startTime + duration, endTime))); //missing = desiredEndTime - actualEndTime
//                (startTime + duration) - actualSliceEndTime); //missing = desiredEndTime - actualEndTime
//</editor-fold>
        long actualSliceEndTime = Math.min(actualStartTime + duration, endTime); //endTime: only allocate to endTime if slice is too small to allocate full duration

        if (actualSliceEndTime > actualStartTime) { //if non-zero time was allocated
            workSlot.addItemWithSlice(allocatedTo);
//            return new WorkSlotSlice(workSlot, actualStartTime, actualSliceEndTime);
//        } else {
//            return null;
        }
            return new WorkSlotSlice(workSlot, actualStartTime, actualSliceEndTime); //NB. Alog doesn't work if returning null slices!!
        //                Math.max(0, (startTime + duration) - Math.min(startTime + duration, endTime))); //missing = desiredEndTime - actualEndTime
        //                (actualStartTime + duration) - actualSliceEndTime); //missing = desiredEndTime - actualEndTime
//        ); //missing = desiredEndTime - actualEndTime
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//        else {
//            return null;
//        }
//</editor-fold>
    }

    /**
     * return a slice of this slice's workslot, starting at startTime
     *
     * @param startTime (must be within the slice's interval) or MyDate.MIN_DATE
     * in which case the workSlots startTime is used
     * @param duration if zero, then allocate a zero duration slice (stores
     * where in a workSlot a zero duration task gets done)
     * @return
     */
    WorkSlotSlice getSliceN(long startTime, long duration) {
        return WorkSlotSlice.this.getSliceN(startTime, duration, null);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkSlotSlice getSliceXX(long startTime, long duration) {
////            if (startTime == MyDate.MIN_DATE) {
////                startTime = workSlot.getStartAdjusted();
////            }
//        ASSERT.that(duration != 0, "duration must be !=0, is=" + duration);
//        ASSERT.that(startTime >= workSlot.getStartAdjusted(), "startTime:" + new Date(startTime) + " must be >= workSlot.getStartAdjusted():" + new Date(workSlot.getStartAdjusted()));
//        ASSERT.that(startTime <= workSlot.getEndTime(), "startTime:" + new Date(startTime) + " must be <= workSlot.getEndTime():" + new Date(workSlot.getEndTime()));
////            ASSERT.that(endTime <= workSlot.getEndTime(), "endTime:" + new Date(endTime) + "must be less than workSlot.getEndTime():" + new Date(workSlot.getEndTime()));
//
////            return new WorkSlotSlice(workSlot, startTime, Math.min(startTime + duration, endTime), Math.max(0, (startTime + duration) - endTime));
//        return new WorkSlotSlice(workSlot, startTime,
//                Math.min(startTime + duration, endTime), //endTime: only allocate to endTime is slice is too small to allocate full duration
//                Math.max(0, (startTime + duration) - Math.min(startTime + duration, endTime))); //missing = desiredEndTime - actualEndTime
//    }
//    WorkSlotSlice getSlice(long duration) {
////        return getSlice(workSlot.getStartAdjusted(), duration);
//        return WorkSlotSlice.this.getSlice(startTime, duration);
//    }
//</editor-fold>
    /**
     * returns true if the WorkTimeSlice has (any amount of) working time that
     * overlaps with [startTime - startTime+duration], which can be allocated
     * towards duration. return false if duration==0.
     *
     * @param startTime if equal to MyDate.MIN_DATE returns true if the
     * WorkTimeSlice has a duration larger than duration
     * @param duration
     * @return
     */
    boolean hasTime(long startTime, long duration) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (startTime == MyDate.MIN_DATE) {
//                return getEndTime() - getStartTime() >= duration;
//            } else {
//                return startTime >= getEndTime() || startTime >= getStartTime() || getDurationInMillis() == 0; //TODO optimization: simplify/optimize epxression
//            }
//            return Math.max(0, getEndTime()-getStartTime());
//            return (startTime >= getStartTime() && startTime < getEndTime()) || getDurationInMillis() == 0; //TODO optimization: simplify/optimize epxression
//</editor-fold>
        return duration > 0 && (startTime >= getStartTime() && startTime <= getEndTime()) && getDurationInMillis() > 0; //TODO optimization: simplify/optimize epxression
//            return getEndTime() - getStartTime() >= duration;
    }

    @Override
    public long getStartTime() {
//<editor-fold defaultstate="collapsed" desc="comment">
//            return Math.max(startTime, workSlot.getStartAdjusted());
//            if (startTime == MyDate.MIN_DATE) {
//                return workSlot.getStartAdjusted();
//            } else {
//                return startTime;
//            }
//</editor-fold>
        return startTime;
    }

//    @Override
//    public Date getEndTimeD() {
//        return new Date(getEndTime());
//    }
    @Override
    public long getEndTime() {
//<editor-fold defaultstate="collapsed" desc="comment">
//            return Math.min(endTime, workSlot.getEndTime());
//            if (endTime == MyDate.MAX_DATE) {
//                return workSlot.getEndTime();
//            } else {
//                return endTime;
//            }
//</editor-fold>
        return endTime;
    }

//    long getMissingDuration() {
//        return missingDuration;
//    }
    @Override
    public long getDurationInMillis() {
        ASSERT.that(endTime >= startTime, "endTime < startTime!!, WorkSlotSlice=" + this);
//            return Math.max(0, getEndTime() - getStartTime());
//            return Math.max(0, endTime - startTime);
        return endTime - startTime; //should always be positive, otherwise error elsewhere
    }

    public long getDuration(long actualStart) {
//            return Math.max(0, getEndTime() - getStartTime());
//            return Math.max(0, endTime - startTime);
        long actStart = Math.max(actualStart, startTime);
        if (Config.WORKTIME_DETAILED_LOG) {
            ASSERT.that(endTime - actStart >= 0, "error in WorkSlotSlice.getDuration, duration gets negative, workSlotSlice=" + this);
        }
//        return endTime - startTime; //should always be positive, otherwise error elsewhere
        return endTime - actStart; //should always be positive, otherwise error elsewhere
    }

//    public ItemAndListCommonInterface getAllocatedToXXX() {
//        return allocatedToXXX;
//    }
//    public void setAllocatedToXXX(ItemAndListCommonInterface allocatedTo) {
//        this.allocatedToXXX = allocatedTo;
//    }
    private static Comparator<WorkSlot> getMultipleComparator(Comparator<WorkSlot>[] comparators) {
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

    /**
     * sort on startTime, then on duration (put longest timeslots first if
     * several starting at same time),
     *
     * @param sortOnEndTime
     * @return
     */
    public static void sortWorkSlotList(List<WorkSlot> sortedWorkslotList) {
//        boolean sortOnEndTime            }) {
//        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
//        if (sortOnEndTime) {
//            Collections.sort(sortedWorkslotList, (i1, i2) -> FilterSortDef.compareLong(i1.getEndTime(), i2.getEndTime()));
//        } else 
        {
//            Collections.sort(sortedWorkslotList, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
            Collections.sort(sortedWorkslotList,
                    //                    (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
                    getMultipleComparator(new Comparator[]{
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()),
                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareLong(i2.getDurationInMillis(), i1.getDurationInMillis()),
                (Comparator<WorkSlot>) (i1, i2) -> i1.getObjectIdP().compareTo(i2.getObjectIdP()), //sort equal workslots on objectId to make it deterministic
            }));
        }
//        return sortedWorkslotList;
    }

}
