/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Date;

/**
 *
 * @author thomashjelm
 */
/**
 * in which workSlot does the task with this index finish
 */
//    private int lastWorkSlotIndex = -1;
class WorkSlotSlice {
    
    WorkSlot workSlot;
    long startTime;// = Long.MIN_VALUE;
    long endTime;// = Long.MAX_VALUE;
    long missingDuration;// = 0;
    long now; //DEBUG: keep for the ASSERT statements
    private ItemAndListCommonInterface allocatedToXXX;
    
    @Override
    public String toString() {
        return "Slice[" + MyDate.formatDateTimeNew(new Date(startTime)) + "-" + MyDate.formatTimeNew(new Date(endTime))
                +" dur="+MyDate.formatTimeDuration(getDuration())
                +" miss="+MyDate.formatTimeDuration(missingDuration)
                + " Owner:" + (workSlot != null && workSlot.getOwner() != null ? workSlot.getOwner().getText() : "<null>")
//                + (allocatedToXXX != null ? ( " AllocTo:" +allocatedToXXX.getText() ): "")
                + "]";
//                + " of "
//                + (workSlot != null ? new Date(workSlot.getDurationAdjusted()) : "null") + "-"
//                + (workSlot != null ? new Date(workSlot.getEndTime()) : "null");
    }
    
    WorkSlotSlice(WorkSlot workSlot, long startTime, long endTime, long missingDuration) {
        this.workSlot = workSlot;
//            this.startTime = startTime < workSlot.getStartAdjusted() ? workSlot.getStartAdjusted() : startTime;
//        this.startTime = Math.max(startTime, workSlot.getStartAdjusted()); //use the larger value (knowing that startTime must never be smaller than getStartAdjusted())
        this.startTime = startTime; //use the larger value (knowing that startTime must never be smaller than getStartAdjusted()) //DONT add getStartAdjusted, may interfere with calculation
//            this.endTime = endTime;
//            this.endTime = workSlot.getEndTime() < endTime ? workSlot.getEndTime() : endTime; //endTime < workSlot.getEndTime() ? endTime : workSlot.getEndTime();
//        this.endTime = Math.min(workSlot.getEndTime(), endTime); //use the smaller value (knowing that endTime must never be larger than getEndTime())
        this.endTime = endTime; //use the smaller value (knowing that endTime must never be larger than getEndTime())
        this.missingDuration = missingDuration;
//        ASSERT.that(startTime >= workSlot.getStartAdjusted(), "startTime:" + new Date(startTime) + " must be greater than or equal to workSlot.getStartAdjusted():" + new Date(workSlot.getStartAdjusted()));
        if (Config.WORKTIME_TEST) {
            assert startTime >= workSlot.getStartAdjusted(now): "startTime:" + new Date(startTime) + " must be greater than or equal to workSlot.getStartAdjusted():" + new Date(workSlot.getStartAdjusted(now));
        }
        if (Config.WORKTIME_TEST) {
            assert endTime <= workSlot.getEndTime(): "endTime:" + new Date(endTime) + "must be less than workSlot.getEndTime():" + new Date(workSlot.getEndTime());
        }
//        ASSERT.that(startTime != endTime, "zero duration workSlotSlice not allowed, startTime:" + new Date(startTime) + " endTime:" + new Date(endTime));
    }
    
    WorkSlotSlice(WorkSlot workSlot, long startTime, long endTime) {
        this(workSlot, startTime, endTime, 0);
    }

//    WorkSlotSlice(WorkSlot workSlot) {
//        
//    }
    /**
    
    @param workSlot
    @param now ensure the same 'now' is used everywhere during the calculation
     */
    WorkSlotSlice(WorkSlot workSlot, long now) {
//            this.workSlot = workSlot;
//            this(workSlot, Long.MIN_VALUE, Long.MAX_VALUE);
        this(workSlot, workSlot.getStartAdjusted(now), workSlot.getEndTime());
//        if (Config.WORKTIME_TEST) {
//            this.now = now;
//        }
    }

    /**
     * return a slice of this slice's workslot, starting at startTime
     *
     * @param startTime (must be within the slice's interval) or MyDate.MIN_DATE
     * in which case the workSlots startTime is used
     * @param duration if zero, then allocate a zero duration slice (stores where in a workSlot a zero duration task gets done)
     * @return
     */
    WorkSlotSlice getSlice(long startTime, long duration) {
        return getSlice(startTime, duration, null);
    }
    
    private WorkSlotSlice getSlice(long startTime, long duration, ItemAndListCommonInterface allocatedTo) {
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
        long actualSliceEndTime = Math.min(startTime + duration, endTime); //endTime: only allocate to endTime if slice is too small to allocate full duration
        return new WorkSlotSlice(workSlot, startTime,
                actualSliceEndTime,
                //                Math.max(0, (startTime + duration) - Math.min(startTime + duration, endTime))); //missing = desiredEndTime - actualEndTime
                (startTime + duration) - actualSliceEndTime); //missing = desiredEndTime - actualEndTime
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//        else {
//            return null;
//        }
//</editor-fold>
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
//</editor-fold>
//    WorkSlotSlice getSlice(long duration) {
////        return getSlice(workSlot.getStartAdjusted(), duration);
//        return WorkSlotSlice.this.getSlice(startTime, duration);
//    }
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
//            if (startTime == MyDate.MIN_DATE) {
//                return getEndTime() - getStartTime() >= duration;
//            } else {
//                return startTime >= getEndTime() || startTime >= getStartTime() || getDurationInMillis() == 0; //TODO optimization: simplify/optimize epxression
//            }
//            return Math.max(0, getEndTime()-getStartTime());
//            return (startTime >= getStartTime() && startTime < getEndTime()) || getDurationInMillis() == 0; //TODO optimization: simplify/optimize epxression
        return duration > 0 && (startTime >= getStartTime() && startTime <= getEndTime()) && getDuration() > 0; //TODO optimization: simplify/optimize epxression
//            return getEndTime() - getStartTime() >= duration;
    }
    
    long getStartTime() {
//            return Math.max(startTime, workSlot.getStartAdjusted());
//            if (startTime == MyDate.MIN_DATE) {
//                return workSlot.getStartAdjusted();
//            } else {
//                return startTime;
//            }
        return startTime;
    }
    
    long getEndTime() {
//            return Math.min(endTime, workSlot.getEndTime());
//            if (endTime == MyDate.MAX_DATE) {
//                return workSlot.getEndTime();
//            } else {
//                return endTime;
//            }
        return endTime;
    }
    
    long getMissingDuration() {
        return missingDuration;
    }
    
    long getDuration() {
//            return Math.max(0, getEndTime() - getStartTime());
//            return Math.max(0, endTime - startTime);
        return endTime - startTime; //should always be positive, otherwise error elsewhere
    }
    
    public ItemAndListCommonInterface getAllocatedToXXX() {
        return allocatedToXXX;
    }
    
    public void setAllocatedToXXX(ItemAndListCommonInterface allocatedTo) {
        this.allocatedToXXX = allocatedTo;
    }
    
}
