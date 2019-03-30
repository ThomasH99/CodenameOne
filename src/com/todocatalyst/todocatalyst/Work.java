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
 * common interface to WorkSlots and WorkTimeSlice
 * @author thomashjelm
 */
public interface Work {

//     static Comparator<Work> getMultipleComparator(Comparator<Work>[] comparators) {
//        Comparator<Work> comp1 = comparators.length >= 1 ? comparators[0] : null;
//        Comparator<Work> comp2 = comparators.length >= 2 ? comparators[1] : null;
//        Comparator<Work> comp3 = comparators.length >= 3 ? comparators[2] : null;
//
//        return (i1, i2) -> {
//            //http://stackoverflow.com/questions/23981199/java-comparator-for-objects-with-multiple-fields            
//            int res1 = comp1.compare(i1, i2);
//            if (res1 != 0 || comp2==null) {
//                return res1;
//            }
////            if (comp2 == null) {
//////                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
////return 
////            }
//            res1 = comp2.compare(i1, i2);
//            if (res1 != 0||comp3==null) {
//                return res1;
//            }
////            if (comp3 == null) {
////                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
////            }
//            res1 = comp3.compare(i1, i2);
////            if (res1 != 0) {
//                return res1;
////            }
////            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//        };
//    }

    default public long getStartTime(){
        return getStartTimeD().getTime();
    };

    default public Date getStartTimeD() {
        return new Date(getStartTime());
    }

    public long getDurationInMillis();

    public long getEndTime();

    default public Date getEndTimeD() {
        return new Date(getEndTime());
    }

    /**
    sort on startTime, then on duration (put longest timeslots first if several starting at same time), 
    @param sortOnEndTime
    @return 
     */
//    public static void sortWorkSlotList(List<Work> sortedWorkslotList) {
////        } else 
//        {
//            Collections.sort(sortedWorkslotList,
//                    //                    (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
//                    getMultipleComparator(new Comparator[]{
//                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()),
//                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareLong(i2.getDurationInMillis(), i1.getDurationInMillis()),
//                (Comparator<WorkSlot>) (i1, i2) -> i1.getObjectIdP().compareTo(i2.getObjectIdP()), //sort equal workslots on objectId to make it deterministic
//            }));
//        }
////        return sortedWorkslotList;
//    }
}
