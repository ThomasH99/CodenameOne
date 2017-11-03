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
    private List<WorkSlot> workSlots = null;
//    Date startTime; 
//    Date finnishTime; //0 if not reached due to insufficient wrkslots
    private long startTime = Long.MIN_VALUE; //time when WorkTime starts inside first workSlot
    private long finishTime = Long.MIN_VALUE; //time when WorkTime starts inside lst workSlot
    private long remainingDuration = 0; //if this WorkTime does not cover the needed duration, this is set to the missing
    private WorkTime nextWorkTime = null; //additional daisy chained WorkTime (n
    /**
     * in which workSlot does the task with this index finish
     */
    private int lastWorkSlotIndex = -1;

    public long getRemainingDuration() {
        return remainingDuration;
    }

    public void setRemainingDuration(long uncoveredTime) {
        this.remainingDuration = uncoveredTime;
    }

    public int getLastWorkSlotIndex() {
        return lastWorkSlotIndex;
    }

    public void setLastWorkSlotIndex(int lastWorkSlotIndex) {
        this.lastWorkSlotIndex = lastWorkSlotIndex;
    }

    WorkTime() {

    }

    WorkTime(List workSlots, long startTime, long finishTime, long remainingDuration) {
        this(workSlots, startTime, finishTime, remainingDuration, null, -1);
    }

    WorkTime(List workSlots, long startTime, long finishTime, long remainingDuration, WorkTime nextWorkTime, int lastWorkSlotIndex) {
//        if (workSlots != null && workSlots.size() > 0) {
        this.workSlots = workSlots;
//        }
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.remainingDuration = remainingDuration;
        this.nextWorkTime = nextWorkTime;
        this.lastWorkSlotIndex = lastWorkSlotIndex;
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
        for (WorkSlot workSlot : workSlots) {
            allocatedTime += workSlot.getDurationAdjusted(startTime, finishTime); //acount for partially allocated workslots
        }
        if (nextWorkTime != null && totalDuration) {
            allocatedTime += nextWorkTime.getAllocatedDuration(); //daisy chain
        }
        return allocatedTime;
    }

//    public Date getAllocatedTimeD() {
//        return new Date(getAllocatedTime());
//    }

    public List<WorkSlot> getWorkSlots() {
//        return nextWorkTime==null?workSlots:((new ArrayList(workSlots)).addAll(nextWorkTime.getWorkSlots()));
        if (nextWorkTime == null) {
            return workSlots;
        } else {
            List<WorkSlot> tempList = new ArrayList(workSlots);
            tempList.addAll(nextWorkTime.getWorkSlots());
            return tempList;
        }
    }

    public void setWorkSlots(ArrayList<WorkSlot> workSlots) {
        this.workSlots = workSlots;
    }

    public long getStartTime() {
//        return Math.max(startTime, workSlots != null && workSlots.size() > 0 ? workSlots.get(0).getStartAdjusted() : Long.MIN_VALUE);
        return nextWorkTime == null ? startTime : Math.min(startTime, nextWorkTime.getStartTime());
    }

    public Date getStartTimeD() {
        return new Date(getStartTime());
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getFinishTime() {
        return nextWorkTime == null ? finishTime : Math.max(finishTime, nextWorkTime.getFinishTime()); //must call getFinishTime() to get value recursively (although very rarely needed)
    }

    public Date getFinishTimeD() {
        return new Date(getFinishTime());
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public WorkTime getNextWorkTime() {
        return nextWorkTime;
    }

    public void setNextWorkTime(WorkTime nextWorkTime) {
        this.nextWorkTime = nextWorkTime;
    }

}
