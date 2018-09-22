/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * the Work time allocated from a series of WorkSlots to a task/project. Not
 * stored for now but calculated dynamically in memory.
 *
 * @author thomashjelm
 */
public class WorkTimeSlices {

    //TODO freeze start/finnishTime
    //TODO is sorting really necessary? Yes, since work time coming from different sources may not be sequential
//    private List<WorkSlot> workSlots = null;
    private List<WorkSlotSlice> workSlotSlicesSortedOnStartTime = new ArrayList(); //null;
//    Date startTime; 
//    Date finnishTime; //0 if not reached due to insufficient wrkslots
//    private long startTime = Long.MIN_VALUE; //time when WorkTimeSlices starts inside first workSlot
//    private long finishTime = Long.MIN_VALUE; //time when WorkTimeSlices starts inside lst workSlot
//    private long remainingDuration = 0; //if this WorkTimeSlices does not cover the needed duration, this is set to the missing
//    private WorkTimeSlices nextWorkTime = null; //additional daisy chained WorkTimeSlices (n

    public String toString() {
        String res = "";
        String sep="";
        for (WorkSlotSlice workSlot : workSlotSlicesSortedOnStartTime) {
            res += sep+workSlot.toString() ;
            sep=", ";
        }
        if (workSlotSlicesSortedOnStartTime.size() == 0) {
            res = "WorkTime empty";
        }
        return res;
    }

    public List<WorkSlotSlice> getWorkSlotSlices() {
        return workSlotSlicesSortedOnStartTime;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getRemainingDuration() {
//        return remainingDuration;
//    }
//
//    public void setRemainingDuration(long uncoveredTime) {
//        this.remainingDuration = uncoveredTime;
//    }
//    public int getLastWorkSlotIndex() {
//        return lastWorkSlotIndex;
//    }
//
//    public void setLastWorkSlotIndex(int lastWorkSlotIndex) {
//        this.lastWorkSlotIndex = lastWorkSlotIndex;
//    }
//</editor-fold>
//    WorkTimeSlices() {
//
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices(List workSlots, long startTime, long finishTime, long remainingDuration) {
//        this(workSlots, startTime, finishTime, remainingDuration, null, -1);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices(List<WorkSlot> workSlots, long startTime, long finishTime, long remainingDuration, WorkTimeSlices nextWorkTime, int lastWorkSlotIndex) {
////        if (workSlots != null && workSlots.size() > 0) {
//        this.workSlots = workSlots;
////        }
//        this.startTime = startTime;
//        this.finishTime = finishTime;
//        this.remainingDuration = remainingDuration;
//        this.nextWorkTime = nextWorkTime;
//        this.lastWorkSlotIndex = lastWorkSlotIndex;
//    }
//    WorkTimeSlices(List<WorkSlotSlice> workSlotSlicesSortedOnStartTime, long remainingDuration) {
//</editor-fold>
//    WorkTimeSlices(WorkSlotSlice workSlice) {
//        this.workSlotSlicesSortedOnStartTime = new ArrayList<>();
//        workSlotSlicesSortedOnStartTime.add(new WorkSlotSlice(workSlice));
//    }
    WorkTimeSlices(List<WorkSlotSlice> workSlotSlices) {
//        if (workSlots != null && workSlots.size() > 0) {
//        if (workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0) {
        this.workSlotSlicesSortedOnStartTime = workSlotSlices;
        sortWorkSlotSlices();
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.workSlots = workSlots;
////        }
//        this.startTime = startTime;
//        this.finishTime = finishTime;
//        this.remainingDuration = remainingDuration;
//        this.nextWorkTime = nextWorkTime;
//        this.lastWorkSlotIndex = lastWorkSlotIndex;
//</editor-fold>
    }

    WorkTimeSlices(WorkSlotSlice workSlotSlice) {
//        if (workSlots != null && workSlots.size() > 0) {
//        if (workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0) {
//        this.workSlotSlicesSortedOnStartTime = new ArrayList<>();
        workSlotSlicesSortedOnStartTime.add(workSlotSlice);
        sortWorkSlotSlices();
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.workSlots = workSlots;
////        }
//        this.startTime = startTime;
//        this.finishTime = finishTime;
//        this.remainingDuration = remainingDuration;
//        this.nextWorkTime = nextWorkTime;
//        this.lastWorkSlotIndex = lastWorkSlotIndex;
//</editor-fold>
    }

//    WorkTimeSlices(WorkSlotList workSlots) {
//        
//    }
    /**
    
    @param workSlots
    @param now ensure the same 'now' is used everywhere during the calculation
     */
//    WorkTimeSlices(WorkSlotList workSlots, long now) {
    WorkTimeSlices(WorkSlotList workSlots) {
        if (workSlots != null && workSlots.size() > 0) {
//            for (WorkSlot workSlot : workSlots) {
            long now = workSlots.getNow();
            WorkSlot workSlot;
            for (int i = 0, size = workSlots.size(); i < size; i++) {
                workSlot = workSlots.get(i);
                workSlotSlicesSortedOnStartTime.add(new WorkSlotSlice(workSlot, now));
            }
            sortWorkSlotSlices();
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * a hack since above WorkTime(List<WorkSlot> workSlots) and
     * WorkTime(List<WorkSlotSlice> workSlotSlicesSortedOnStartTime) have same erasure
     *
     * @param list
     * @param listIsSlides
     */
//    WorkTimeXXX(List list, boolean listIsSlides) {
//        if (listIsSlides) {
//            if (workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0) {
//                this.workSlotSlicesSortedOnStartTime = workSlotSlicesSortedOnStartTime;
//            }
//        } else {
//            if (list != null && list.size() > 0) {
//                for (Object workSlot : list) {
//                    workSlotSlicesSortedOnStartTime.add(new WorkSlotSlice((WorkSlot) workSlot));
//                }
//            }
//
//        }
//    }
//</editor-fold>
    WorkTimeSlices() {
    }

    private void sortWorkSlotSlices(){
        Collections.sort(workSlotSlicesSortedOnStartTime, (slice1, slice2) -> {
                return FilterSortDef.compareLong(slice1.getStartTime(), slice2.getStartTime());
            });
    }
    
    /**
     * combines two workTime into one, eg when acquired from two different
     * workTimeProviders.
     *
     * @param additionalWorkTime must come from a lower priority
     * workTimeProvider (from another provider, but timing-wise can at be any
     * time compared to existing workTime (before, after, overlapping,
     * intertwined). This will ensure that workTime allocated to subtasks will
     * prioritize the higher priority workTimeProvider.
     */
    void addWorkTime(WorkTimeSlices additionalWorkTime) {
        if (additionalWorkTime != null) {
            workSlotSlicesSortedOnStartTime.addAll(additionalWorkTime.workSlotSlicesSortedOnStartTime);
        }
        sortWorkSlotSlices();
    }

    /**
     * return the total amount of time allocated in the different workslots
     *
     * @return
     */
    public long getAllocatedDuration() {
        return getAllocatedDuration(true);
    }

    public long getAllocatedDuration(boolean totalDuration) {
        long allocatedTime = 0;
        //sum up the time allocated from each workslot. NB. First and last may only be partially allocated
//        for (WorkSlotSlice slice : workSlotSlicesSortedOnStartTime) {
//        WorkSlotSlice slice;
        for (WorkSlotSlice slice : workSlotSlicesSortedOnStartTime) {
//        for (int i = 0, size = workSlotSlicesSortedOnStartTime.size(); i < size; i++) {
//            slice = workSlotSlicesSortedOnStartTime.get(i);
            allocatedTime += slice.getDuration();
        }
        return allocatedTime;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getAllocatedDuration(boolean totalDuration) {
//        long allocatedTime = 0;
//        //sum up the time allocated from each workslot. NB. First and last may only be partially allocated
//        for (WorkSlot workSlot : workSlots) {
//            allocatedTime += workSlot.getDurationAdjusted(startTime, finishTime); //acount for partially allocated workslots
//        }
//        if (nextWorkTime != null && totalDuration) {
//            allocatedTime += nextWorkTime.getAllocatedDuration(); //daisy chain
//        }
//        return allocatedTime;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getAllocatedTimeD() {
//        return new Date(getAllocatedTime());
//    }
    /**
     * return all allocated workslots, even from different providers, as a
     * single list. NO - doesn't work since the nextWorkTime slots may not be
     * allocated fully (may have a start and endTime).
     *
     * @return
     */
//    public List<WorkSlot> getWorkSlots() {
////        return nextWorkTime==null?workSlots:((new ArrayList(workSlots)).addAll(nextWorkTime.getWorkSlots()));
//if (nextWorkTime == null) {
//    return workSlots;
//} else {
//    List<WorkSlot> tempList = new ArrayList(workSlots);
//    tempList.addAll(nextWorkTime.getWorkSlots());
//    return tempList;
//}
//    }
//</editor-fold>
//    public void setWorkSlots(ArrayList<WorkSlot> workSlots) {
//        this.workSlots = workSlots;
//    }
    /**
     * returns allocated startTime (NOT adjusted to 'now' - the adjustment is done in WorkSlot.getStartTimeAdjusted()
     * @return MyDate.MAX_DATE if no slices available
     */
    public long getStartTime() {
//        return Math.max(startTime, workSlots != null && workSlots.size() > 0 ? workSlots.get(0).getStartAdjusted() : Long.MIN_VALUE);
//        return nextWorkTime == null ? startTime : Math.min(startTime, nextWorkTime.getStartTime());
//        return workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(0).getStartTime() : MyDate.MIN_DATE;
//        return workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(0).getStartTime() : MyDate.MAX_DATE;
        return workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(0).getStartTime() : MyDate.MAX_DATE;
    }

    /**
     *
     * @return MyDate.MIN_DATE if no slides available
     */
    public Date getStartTimeD() {
        return new Date(getStartTime());
    }

//    public void setStartTime(long startTime) {
//        this.startTime = startTime;
//    }
    /**
     *
     * @return MyDate.MAX_DATE if no slices available (meaning no defined/calculated finish time)
     */
    public long getFinishTime() {
//        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
//        if (Test.DEBUG) {
//            ASSERT.that(workSlotSlicesSortedOnStartTime != null, "workSlotSlicesSortedOnStartTime==null in WorkTimeSlices:" + this);
//        }
//        if (Config.WORKTIME_TEST) {
////            ASSERT.that(true || workSlotSlicesSortedOnStartTime == null || workSlotSlicesSortedOnStartTime.size() > 0, "workSlotSlicesSortedOnStartTime.size()==0 in WorkTimeSlices:" + this); //normal case with empty list, e.g. ??
//            assert workSlotSlicesSortedOnStartTime == null || workSlotSlicesSortedOnStartTime.size() > 0: "workSlotSlicesSortedOnStartTime.size()==0 in WorkTime:" + this; //normal case with empty list, e.g. ??
//        }
//        return workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1).getEndTime() : MyDate.MAX_DATE;
        return workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1).getEndTime() : MyDate.MAX_DATE;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getFinishTimeWithNullsXXX() {
////        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
////        return workSlotSlicesSortedOnStartTime != null && workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1).getEndTime() : MyDate.MIN_DATE;
//        int i = workSlotSlicesSortedOnStartTime.size() - 1;
//        while (i >= 0) {
//            WorkSlotSlice slice = workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1);
//            if (slice != null) {
//                return slice.getEndTime();
//            } else {
//                i--;
//            }
////        if( workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1).getEndTime() : MyDate.MIN_DATE;
//        }
//        return MyDate.MAX_DATE; //
//    }
//</editor-fold>

    /**
     *
     * @return MyDate.MIN_DATE if no slides available
     */
    public Date getFinishTimeD() {
        return new Date(getFinishTime());
    }

    /**
     *
     * @return MyDate.MIN_DATE if no slides available
     */
    public long getRemainingDuration() {
//        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
        return workSlotSlicesSortedOnStartTime.size() > 0 ? workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1).getMissingDuration() : 0;
//    }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setFinishTime(long finishTime) {
//        this.finishTime = finishTime;
//    }
//    public WorkTimeSlices getNextWorkTime() {
//        return nextWorkTime;
//    }
//
//    public void setNextWorkTime(WorkTimeSlices nextWorkTime) {
//        this.nextWorkTime = nextWorkTime;
//    }
//</editor-fold>
    WorkTimeSlices getWorkTime(long remainingDuration) {
//        return getWorkTime(getStartTime(), remainingDuration); //default is startTime of this WorkTimeSlices
        return getWorkTime(getStartTime(), remainingDuration); //default is startTime of this WorkTimeSlices
    }

    /**
     * get a new WorkTimeSlices based on workslots in this worktime which starts at time startTime with duration remainingDuration.  Special case  if
     * remainingDuration is 0 (either Done tasks, in the middle or end of a subtask list, or tasks with zero estimate) then return an empty slice of the last(!)
     * workSlotSlice (to ensure that zero-duration tasks don't break the
     * continuation of the allocated time (the current algorithm depends on
     * this). If there are no
     *
     * @param startTime when should workTime start (== when did workTime for
     * previous task end)
     * @param remainingDuration duration required
     * @return
     */
    WorkTimeSlices getWorkTime(long startTime, long remainingDuration) {
        //special case if requestedDuration is zero: 
        if (false && remainingDuration == 0 && workSlotSlicesSortedOnStartTime.size() > 0) {
            WorkSlotSlice lastSlice = workSlotSlicesSortedOnStartTime.get(workSlotSlicesSortedOnStartTime.size() - 1);
            WorkSlotSlice newSlice = lastSlice.getSlice(startTime, 0); //allocate empty slice of last workslot, see reason in javadoc above
//            newWorkSlotSlices.add(newSlice);
            return new WorkTimeSlices(newSlice);
        }

        List<WorkSlotSlice> newWorkSlotSlices = new ArrayList<WorkSlotSlice>();

        int index = 0;
        int size = workSlotSlicesSortedOnStartTime.size();
        //find first slice with appropriate startTime (skip slides that start *after* startTime or end *before* startTime (<=> no overlap)
//        while (index < size && startTime < workSlotSlicesSortedOnStartTime.get(index).getStartTime() && startTime > workSlotSlicesSortedOnStartTime.get(index).getEndTime()) {
        while (index < size && !(startTime >= workSlotSlicesSortedOnStartTime.get(index).getStartTime() && startTime <= workSlotSlicesSortedOnStartTime.get(index).getEndTime())) {
            index++;
        }

        WorkSlotSlice slice;
        while (index < size) { //while there are non-zero workslots... don't test on remainingDuration==0, since we styill need to allocate a zero-length workslot to those
            slice = workSlotSlicesSortedOnStartTime.get(index);
            WorkSlotSlice newSlice = slice.getSlice(startTime, remainingDuration);
            newWorkSlotSlices.add(newSlice);
            remainingDuration = newSlice.missingDuration; //new remaining is what was not allocated in the last slot
            if (remainingDuration == 0) {
                break; //break after allocation of first slice when duration is zero (avoids having multiple zeru duration slices allocated)
            }
            startTime = newSlice.endTime; //new startTime is the end of the allocated workSlotSlice
            index++;
        }
//        return usedWorkSlotSlices != null && usedWorkSlotSlices.size() > 0 ? new WorkTimeSlices(usedWorkSlotSlices) : null;
        return new WorkTimeSlices(newWorkSlotSlices);
//<editor-fold defaultstate="collapsed" desc="comment">
//if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) {
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
//            if (workSlotIndex >= workSlots.size() && workTime.nextWorkTime != null) {
//                WorkTimeSlices newWT = new WorkTimeSlices();
//                workTimeResult.nextWorkTime = newWT;
//                workTimeResult = newWT;
//                workTime = workTime.nextWorkTime;
//                workSlots = workTime.workSlots;
//            }
//        }
//        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices getWorkTimeOLD3(long startTime, long remainingDuration) {
////        long startTime = Long.MIN_VALUE;
//
//        WorkSlotSlice slice;
//        List<WorkSlotSlice> usedWorkSlotSlices = new ArrayList<WorkSlotSlice>();
//        int index = 0;
//        int size = workSlotSlicesSortedOnStartTime.size();
//        do {
//            //if first workslot ends before startTime, or it empty, skip to next one
//            slice = workSlotSlicesSortedOnStartTime.get(index);
////            if (slice.hasTime(startTime, remainingDuration)) {
//            WorkSlotSlice newSlice = slice.getSlice(startTime, remainingDuration);
////            if (newSlice != null) {
////            if (usedWorkSlotSlices == null) {
////                usedWorkSlotSlices = new ArrayList<WorkSlotSlice>();
////            }
//            usedWorkSlotSlices.add(newSlice);
//            remainingDuration = newSlice.missingDuration; //new remaining is what was not allocated in the last slot
//            startTime = newSlice.endTime; //new startTime is the end of the allocated workSlotSlice
////            }
////            }
////            else {
////                index++;
////            }
//            index++;
//        } while (remainingDuration > 0 && index < size);
////        return usedWorkSlotSlices != null && usedWorkSlotSlices.size() > 0 ? new WorkTimeSlices(usedWorkSlotSlices) : null;
//        return new WorkTimeSlices(usedWorkSlotSlices);
////<editor-fold defaultstate="collapsed" desc="comment">
////if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) {
////                //UI: 0-duration workslots will NOT be included in list for a task
////                workSlotIndex++;
////                continue; //continue with next workSLot
////            }
////            if (!startTimeCheckedAgainstFirstValidWorkSLot && workSlot.getStartAdjusted() > startTime) { //if next workSlot starts *after* startTime, adjust startTime
////                startTime = workSlot.getStartAdjusted();
////                startTimeCheckedAgainstFirstValidWorkSLot = true;
////            }
//////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTimeSlices
////            if (startTime + remainingDuration <= workSlot.getEndTime()) { //duration is fully covered within current workSlot
////                endTime = startTime + remainingDuration;
////                remainingDuration = 0;
////            } else {
////                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted(startTime);
////                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
////                workSlotIndex++;
////            }
////            if (usedWorkSlots == null) { //lazy init
////                usedWorkSlots = new ArrayList();
////            }
////            usedWorkSlots.add(workSlot);
////            if (workSlotIndex >= workSlots.size() && workTime.nextWorkTime != null) {
////                WorkTimeSlices newWT = new WorkTimeSlices();
////                workTimeResult.nextWorkTime = newWT;
////                workTimeResult = newWT;
////                workTime = workTime.nextWorkTime;
////                workSlots = workTime.workSlots;
////            }
////        }
////        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices getWorkTime_NOLD2(long startTime, long remainingDuration) {
////        long startTime = Long.MIN_VALUE;
//
//        WorkSlotSlice slice;
//        List<WorkSlotSlice> usedWorkSlotSlices = null;
//        int index = 0;
//        int size = workSlotSlicesSortedOnStartTime.size();
//        while (remainingDuration > 0 && index < size) {
//            //if first workslot ends before startTime, or it empty, skip to next one
//            slice = workSlotSlicesSortedOnStartTime.get(index);
////            if (slice.hasTime(startTime, remainingDuration)) {
//            WorkSlotSlice newSlice = slice.getSlice(startTime, remainingDuration);
//            if (newSlice != null) {
//                if (usedWorkSlotSlices == null) {
//                    usedWorkSlotSlices = new ArrayList<WorkSlotSlice>();
//                }
//                usedWorkSlotSlices.add(newSlice);
//                remainingDuration = newSlice.missingDuration; //new remaining is what was not allocated in the last slot
//                startTime = newSlice.endTime; //new startTime is the end of the allocated workSlotSlice
//            }
////            }
////            else {
////                index++;
////            }
//            index++;
//        }
//        return usedWorkSlotSlices != null && usedWorkSlotSlices.size() > 0 ? new WorkTimeSlices(usedWorkSlotSlices) : null;
////<editor-fold defaultstate="collapsed" desc="comment">
////if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) {
////                //UI: 0-duration workslots will NOT be included in list for a task
////                workSlotIndex++;
////                continue; //continue with next workSLot
////            }
////            if (!startTimeCheckedAgainstFirstValidWorkSLot && workSlot.getStartAdjusted() > startTime) { //if next workSlot starts *after* startTime, adjust startTime
////                startTime = workSlot.getStartAdjusted();
////                startTimeCheckedAgainstFirstValidWorkSLot = true;
////            }
//////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTimeSlices
////            if (startTime + remainingDuration <= workSlot.getEndTime()) { //duration is fully covered within current workSlot
////                endTime = startTime + remainingDuration;
////                remainingDuration = 0;
////            } else {
////                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted(startTime);
////                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
////                workSlotIndex++;
////            }
////            if (usedWorkSlots == null) { //lazy init
////                usedWorkSlots = new ArrayList();
////            }
////            usedWorkSlots.add(workSlot);
////            if (workSlotIndex >= workSlots.size() && workTime.nextWorkTime != null) {
////                WorkTimeSlices newWT = new WorkTimeSlices();
////                workTimeResult.nextWorkTime = newWT;
////                workTimeResult = newWT;
////                workTime = workTime.nextWorkTime;
////                workSlots = workTime.workSlots;
////            }
////        }
////        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
////</editor-fold>
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices getWorkTime_NOLD(long startTime, long remainingDuration) {
////        long startTime = Long.MIN_VALUE;
//
//        WorkSlotSlice slice;
//        List<WorkSlotSlice> usedWorkSlotSlices = null;
//        int index = 0;
//        int size = workSlotSlicesSortedOnStartTime.size();
//        while (remainingDuration > 0 && index < size) {
//            //if first workslot ends before startTime, or it empty, skip to next one
//            slice = workSlotSlicesSortedOnStartTime.get(index);
//            if (slice.hasTime(startTime, remainingDuration)) {
//                WorkSlotSlice newSlice = slice.getSlice(startTime, remainingDuration);
//                if (usedWorkSlotSlices == null) {
//                    usedWorkSlotSlices = new ArrayList<WorkSlotSlice>();
//                }
//                usedWorkSlotSlices.add(newSlice);
//                remainingDuration = newSlice.missingDuration;
//                startTime = newSlice.endTime;
//            }
////            else {
////                index++;
////            }
//            index++;
//        }
//        return usedWorkSlotSlices != null && usedWorkSlotSlices.size() > 0 ? new WorkTimeSlices(usedWorkSlotSlices) : null;
////<editor-fold defaultstate="collapsed" desc="comment">
////if (workSlot.getEndTime() <= startTime || workSlot.getDurationAdjusted() == 0) {
////                //UI: 0-duration workslots will NOT be included in list for a task
////                workSlotIndex++;
////                continue; //continue with next workSLot
////            }
////            if (!startTimeCheckedAgainstFirstValidWorkSLot && workSlot.getStartAdjusted() > startTime) { //if next workSlot starts *after* startTime, adjust startTime
////                startTime = workSlot.getStartAdjusted();
////                startTimeCheckedAgainstFirstValidWorkSLot = true;
////            }
//////            startTime = Math.max(startTime, workSlot.getStartAdjusted()); //not needed! If workSlot.startTime is smaller than startTime, it will simply be ignored in WorkTimeSlices
////            if (startTime + remainingDuration <= workSlot.getEndTime()) { //duration is fully covered within current workSlot
////                endTime = startTime + remainingDuration;
////                remainingDuration = 0;
////            } else {
////                remainingDuration -= workSlot.getEndTime() - workSlot.getStartAdjusted(startTime);
////                endTime = workSlot.getEndTime(); //store this workslot'e endtime in case we've reached the last workslot (if not, endTime will get overwritten in next iteration)
////                workSlotIndex++;
////            }
////            if (usedWorkSlots == null) { //lazy init
////                usedWorkSlots = new ArrayList();
////            }
////            usedWorkSlots.add(workSlot);
////            if (workSlotIndex >= workSlots.size() && workTime.nextWorkTime != null) {
////                WorkTimeSlices newWT = new WorkTimeSlices();
////                workTimeResult.nextWorkTime = newWT;
////                workTimeResult = newWT;
////                workTime = workTime.nextWorkTime;
////                workSlots = workTime.workSlots;
////            }
////        }
////        return new WorkTimeSlices(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
////</editor-fold>
//    }
//</editor-fold>

}
