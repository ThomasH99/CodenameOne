/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * the Work time allocated from a series of WorkSlots to a task/project. Not
 * stored for now but calculated dynamically in memory.
 *
 * @author thomashjelm
 */
public class WorkTime {

    //TODO freeze start/finnishTime
//    private List<WorkSlot> workSlots = null;
    private List<WorkSlotSlice> workSlotSlices = new ArrayList(); //null;
//    Date startTime; 
//    Date finnishTime; //0 if not reached due to insufficient wrkslots
//    private long startTime = Long.MIN_VALUE; //time when WorkTime starts inside first workSlot
//    private long finishTime = Long.MIN_VALUE; //time when WorkTime starts inside lst workSlot
//    private long remainingDuration = 0; //if this WorkTime does not cover the needed duration, this is set to the missing
//    private WorkTime nextWorkTime = null; //additional daisy chained WorkTime (n

    public String toString() {
        String res = "";
        for (WorkSlotSlice workSlot : workSlotSlices) {
            res += workSlot + ", ";
        }
        return res;
    }

    public List<WorkSlotSlice> getWorkSlotSlices() {
        return workSlotSlices;
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
//    WorkTime() {
//
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTime(List workSlots, long startTime, long finishTime, long remainingDuration) {
//        this(workSlots, startTime, finishTime, remainingDuration, null, -1);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTime(List<WorkSlot> workSlots, long startTime, long finishTime, long remainingDuration, WorkTime nextWorkTime, int lastWorkSlotIndex) {
////        if (workSlots != null && workSlots.size() > 0) {
//        this.workSlots = workSlots;
////        }
//        this.startTime = startTime;
//        this.finishTime = finishTime;
//        this.remainingDuration = remainingDuration;
//        this.nextWorkTime = nextWorkTime;
//        this.lastWorkSlotIndex = lastWorkSlotIndex;
//    }
//    WorkTime(List<WorkSlotSlice> workSlotSlices, long remainingDuration) {
//</editor-fold>
    WorkTime(List<WorkSlotSlice> workSlotSlices) {
//        if (workSlots != null && workSlots.size() > 0) {
        if (workSlotSlices != null && workSlotSlices.size() > 0) {
            this.workSlotSlices = workSlotSlices;
        }
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

    WorkTime(WorkSlotList workSlots) {
        if (workSlots != null && workSlots.size() > 0) {
//            for (WorkSlot workSlot : workSlots) {
            WorkSlot workSlot;
            for (int i = 0, size = workSlots.size(); i < size; i++) {
                workSlot = workSlots.get(i);
                workSlotSlices.add(new WorkSlotSlice(workSlot));
            }
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * a hack since above WorkTime(List<WorkSlot> workSlots) and
     * WorkTime(List<WorkSlotSlice> workSlotSlices) have same erasure
     *
     * @param list
     * @param listIsSlides
     */
//    WorkTimeXXX(List list, boolean listIsSlides) {
//        if (listIsSlides) {
//            if (workSlotSlices != null && workSlotSlices.size() > 0) {
//                this.workSlotSlices = workSlotSlices;
//            }
//        } else {
//            if (list != null && list.size() > 0) {
//                for (Object workSlot : list) {
//                    workSlotSlices.add(new WorkSlotSlice((WorkSlot) workSlot));
//                }
//            }
//
//        }
//    }
//</editor-fold>
    WorkTime() {
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
    void addWorkTime(WorkTime additionalWorkTime) {
        if (additionalWorkTime != null) {
            workSlotSlices.addAll(additionalWorkTime.workSlotSlices);
        }
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
//        for (WorkSlotSlice slice : workSlotSlices) {
        WorkSlotSlice slice;
        for (int i = 0, size = workSlotSlices.size(); i < size; i++) {
            slice = workSlotSlices.get(i);
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
    public long getStartTime() {
//        return Math.max(startTime, workSlots != null && workSlots.size() > 0 ? workSlots.get(0).getStartAdjusted() : Long.MIN_VALUE);
//        return nextWorkTime == null ? startTime : Math.min(startTime, nextWorkTime.getStartTime());
        return workSlotSlices != null && workSlotSlices.size() > 0 ? workSlotSlices.get(0).getStartTime() : MyDate.MIN_DATE;
    }

    public Date getStartTimeD() {
        return new Date(getStartTime());
    }

//    public void setStartTime(long startTime) {
//        this.startTime = startTime;
//    }
    public long getFinishTime() {
//        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
        return workSlotSlices != null && workSlotSlices.size() > 0 ? workSlotSlices.get(workSlotSlices.size() - 1).getEndTime() : MyDate.MIN_DATE;
    }

    public Date getFinishTimeD() {
        return new Date(getFinishTime());
    }

    public long getRemainingDuration() {
//        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
        return workSlotSlices != null && workSlotSlices.size() > 0 ? workSlotSlices.get(workSlotSlices.size() - 1).getMissingDuration() : 0;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setFinishTime(long finishTime) {
//        this.finishTime = finishTime;
//    }
//    public WorkTime getNextWorkTime() {
//        return nextWorkTime;
//    }
//
//    public void setNextWorkTime(WorkTime nextWorkTime) {
//        this.nextWorkTime = nextWorkTime;
//    }
//</editor-fold>
    /**
     * get a new WorkTime based on workslots in this worktime.
     *
     * @param startTime when should workTime start (== when did workTime for
     * previous task end)
     * @param remainingDuration duration required
     * @return
     */
    WorkTime getWorkTime(long remainingDuration) {
        return getWorkTime(getStartTime(), remainingDuration); //default is startTime of this WorkTime
    }

    WorkTime getWorkTime(long startTime, long remainingDuration) {
//        long startTime = Long.MIN_VALUE;

        int index = 0;
        WorkSlotSlice slice;

        List<WorkSlotSlice> usedWorkSlotSlices = null;
        int size = workSlotSlices.size();
        while (remainingDuration > 0 && index < size) {
            //if first workslot ends before startTime, or it empty, skip to next one
            slice = workSlotSlices.get(index);
            if (slice.hasTime(startTime, remainingDuration)) {
                WorkSlotSlice newSlice = slice.getSlice(startTime, remainingDuration);
                if (usedWorkSlotSlices == null) {
                    usedWorkSlotSlices = new ArrayList<WorkSlotSlice>();
                }
                usedWorkSlotSlices.add(newSlice);
                remainingDuration = newSlice.missingDuration;
            }
//            else {
//                index++;
//            }
            index++;
        }
        return new WorkTime(usedWorkSlotSlices);
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
//            if (workSlotIndex >= workSlots.size() && workTime.nextWorkTime != null) {
//                WorkTime newWT = new WorkTime();
//                workTimeResult.nextWorkTime = newWT;
//                workTimeResult = newWT;
//                workTime = workTime.nextWorkTime;
//                workSlots = workTime.workSlots;
//            }
//        }
//        return new WorkTime(usedWorkSlots, startTime, endTime, remainingDuration, null, workSlotIndex);
//</editor-fold>
    }

}
