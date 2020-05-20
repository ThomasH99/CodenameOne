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
 * encapsulates a complete list of workSlots for one ItemList/Category or
 * Item/Project
 *
 * @author Thomas
 */
public class WorkSlotList implements MyTreeModel {//extends ArrayList<WorkSlot> {

    private long now;//=-1; //ensure a single value of now is used for the work slots
    private List<WorkSlot> sortedOnStartTimeWorkslotList = new ArrayList<>(); //full list of workslots, always assumed to be sorted
//    private List<WorkSlot> validworkslotList = new ArrayList<>(); //
    private boolean showAlsoExpiredWorkSlotsFOR_TESTING_ONLY;
    private ItemAndListCommonInterface owner;

//    public WorkSlotList() {
//        super();
////        if (now==-1) 
//        now = System.currentTimeMillis(); //MyDate.MIN_DATE; //System.currentTimeMillis(); //for now, use a 'local' (object specific) value for now (should be global and coming from Screen/UI
//        updateRepeatingWorkSlots();
//    }
//    public WorkSlotList(List<WorkSlot> list, boolean alreadySorted, boolean removeExpiredWorkSlots) {
    public WorkSlotList() {

    }

    public WorkSlotList(ItemAndListCommonInterface owner, List<WorkSlot> list, boolean alreadySorted) {
//        this();
//        super();
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
//        if (!alreadySorted) {
//            sortWorkSlotList();
//        }
//        if (removeExpiredWorkSlots) {
//            workslotList.addAll(removePastWorkSlots(list, now)); //optimization - this makes a copy of the list, is it really necessary??
//        } else {
//            workslotList.addAll(list); //optimization - this makes a copy of the list, is it really necessary??
//        }//        this.copyOf(list);
        this();
        this.owner = owner;
        if (list != null) {
            sortedOnStartTimeWorkslotList.addAll(list); //optimization - this makes a copy of the list, is it really necessary??
        }
        if (!alreadySorted) {
            sortWorkSlotList();
//            Work.sortWorkSlotList(sortedWorkslotList);
        }
//        now = System.currentTimeMillis(); //MyDate.MIN_DATE; //System.currentTimeMillis(); //for now, use a 'local' (object specific) value for now (should be global and coming from Screen/UI
//        updateRepeatingWorkSlots();
        setNow(MyDate.currentTimeMillis()); //MyDate.MIN_DATE; //System.currentTimeMillis(); //for now, use a 'local' (object specific) value for now (should be global and coming from Screen/UI

    }

//    public WorkSlotList(List<WorkSlot> list, boolean alreadySorted) {
//        this(null, list, alreadySorted);
//    }
//    public WorkSlotList(List<WorkSlot> list, boolean alreadySorted) {
//        this(list, alreadySorted, true);
//    }
//    public WorkSlotList(List<WorkSlot> list) {
//        this(null, list, false);
//    }
//    public WorkSlotList(ItemAndListCommonInterface owner, List<WorkSlot> list) {
//        this(owner, list, false);
//    }
    public void setOwner(ItemAndListCommonInterface owner) {
        this.owner = owner;
    }

    public ItemAndListCommonInterface getOwner() {
        return owner;
    }

    public void setNow(long now) {
        this.now = now;
//        if (false)
//            updateRepeatingWorkSlots();
    }

    public long getNow() {
        return now;
    }

    /**
     * for testing purposes
     *
     * @param showAlsoExpiredWorkSlots
     */
    public void setIncludeExpiredWorkSlots(boolean showAlsoExpiredWorkSlots) {
        this.showAlsoExpiredWorkSlotsFOR_TESTING_ONLY = showAlsoExpiredWorkSlots;
    }

    public String toString() {
        String workSlots = "WorkSlots: ";
        String sep = "";
        for (int i = 0, size = sortedOnStartTimeWorkslotList.size(); i < size; i++) {
            workSlots += sep + sortedOnStartTimeWorkslotList.get(i).toString();
            sep = "; ";
        }
        return workSlots;
    }

    /**
     * add workSlot *sorted
     *
     *
     * @param workSlot
     */
    public void add(WorkSlot workSlot) {
////        workslotList.add(workSlot);
////        sortWorkSlotList(); //DONE //optimization: insert sorted instead of do entire bubblesort
//        for (int i = sortedOnStartTimeWorkslotList.size() - 1; i >= 0; i--) {
//            if (sortedOnStartTimeWorkslotList.get(i).getStartTimeD().getTime() <= workSlot.getStartTimeD().getTime()) {
//                sortedOnStartTimeWorkslotList.add(i + 1, workSlot);
//                return;
//            }
//        }
//        //if no earlier slot found above, then insert at start of (possibly empty) list
//        sortedOnStartTimeWorkslotList.add(0, workSlot);
        sortedOnStartTimeWorkslotList.add(workSlot);
        WorkSlot.sortWorkSlotList(sortedOnStartTimeWorkslotList); //ONLY sure way to always keep sorted on startTime, then duration (if multiple with same startTime)
        owner.setWorkSlotsInParse(sortedOnStartTimeWorkslotList); //save updated list
        //simple solution: new workSlots are likely be the most recent, so simply search from end of list to find where to insert
    }

    public void remove(WorkSlot workSlot) {
        sortedOnStartTimeWorkslotList.remove(workSlot);
        owner.setWorkSlotsInParse(sortedOnStartTimeWorkslotList); //save updated list
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    add workSlot *sorted*
//    @param workSlot
//     */
//    public void addToList(WorkSlot workSlot) {
//        WorkSlotList workSlotList = this;
//                    if (workSlotList == null) {
//                        workSlotList = new WorkSlotList();
//                    }
//                    add(workSlot);
//                    setWorkSlotList(workSlotList);
//    }
//
//</editor-fold>
    /**
     * update repeating workSlots, done every time the time ('now') changes.
     *
     * @return
     */
//    private boolean updateRepeatingWorkSlots() {
//        RepeatRuleParseObject repeatRule;
//        boolean updated = false;
//        for (WorkSlot ws : sortedWorkslotList) { //test workslots from the end of list, more efficient if many expired workslots are present
////                WorkSlot ws = sortedWorkslotList.get(i);
//            if ((repeatRule = ws.getRepeatRule()) != null) {
//                if (repeatRule.updateWorkslots(ws))
//                    updated = true;
//            }
//        }
//        return updated;
//    }
    private List<WorkSlot> getWorkSlots(long now) {
        //optimization: cache the filtered list (result) and only check if some of the earlier slots have become invalid and should be removed, becomes interesting with many past workslots
        if (showAlsoExpiredWorkSlotsFOR_TESTING_ONLY) {
            return sortedOnStartTimeWorkslotList;
        } else {

            //first deal with any repeating workslots (remove expired and add new repeat instances
            List<RepeatRuleParseObject> workSlotsWithRepeatRules = new ArrayList<>();
            for (WorkSlot ws : sortedOnStartTimeWorkslotList) {
                //NB! workSlot list can contain both individual workslots and (many) repeats of same workSlot -> 
                //gather all repeatRules to update in batch:
                RepeatRuleParseObject repeatRule = ws.getRepeatRuleN();
                if (repeatRule != null && !(workSlotsWithRepeatRules.contains(repeatRule))) { //contains(): keep only one instance of each rule if multiple workslots originate from same rule
                    workSlotsWithRepeatRules.add(repeatRule);
                }
            }
            for (RepeatRuleParseObject repeatRuleParseObject : workSlotsWithRepeatRules) {
//                repeatRuleParseObject.updateIfExpiredOrDeletedWorkslots(null); //will update based on owner() in internal list of generated instances
                repeatRuleParseObject.updateIfExpiredWorkslots(); //will update based on owner() in internal list of generated instances
            }

            //then filter on expired 
            List<WorkSlot> workSlotsWithFutureWorkTime = new ArrayList<>();
//            int size =sortedOnStartTimeWorkslotList.size();
//            for (int i = size - 1; i >= 0; i--) { //test workslots from the end of list, more efficient if many expired workslots are present
            for (WorkSlot ws : sortedOnStartTimeWorkslotList) { //test workslots from the end of list, more efficient if many expired workslots are present
//                WorkSlot ws = sortedOnStartTimeWorkslotList.get(i);
                if (ws.getEndTimeD().getTime() > now) {
//                    result.add(0, ws); //insert at the head of the list to keep same order as sortedWorkslots
                    workSlotsWithFutureWorkTime.add(ws); //insert at the end of the list to keep same order as sortedWorkslots
                }
                //TODO!!!! Optimization: when reading sortedWorkslotList (sorted on *start* time) from the end, we cannot besure that some earlier item in the list has an end time which is >now, so we MUST iterate through entire array. Only way to avoid it is to sort on endTime, keep the ones with valid time and then re-sort on startTime!!!
//                else 
//                    break; //stop iterating back once the first expired workslot is reached
//            return updatedWorkslotList;
//            return sortedOnStartTimeWorkslotList; //may be updated by the repeatRuleParseObject.updateIfExpiredWorkslots()
            }
            return workSlotsWithFutureWorkTime; //may be updated by the repeatRuleParseObject.updateIfExpiredWorkslots()
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private List<WorkSlot> getWorkSlotsXXX(long now) {
//        //optimization: cache the filtered list (result) and only check if some of the earlier slots have become invalid and should be removed, becomes interesting with many past workslots
//        if (showAlsoExpiredWorkSlotsFOR_TESTING_ONLY) {
////            now = MyDate.MIN_DATE;
//            return sortedWorkslotList;
//        } else {
////            now = System.currentTimeMillis();
//            List<WorkSlot> updatedWorkslotList = new ArrayList<>();
////            for (WorkSlot ws : sortedWorkslotList) {
//            int size = sortedWorkslotList.size();
//            //NB! workSlot liist can contain both individual workslots and (many) repeats of same workSlot ->
//
//            for (int i = size - 1; i >= 0; i--) { //test workslots from the end of list, more efficient if many expired workslots are present
//                WorkSlot ws = sortedWorkslotList.get(i);
//                if (ws.getEndTimeD().getTime() > now) {
//                    updatedWorkslotList.add(0, ws); //insert at the head of the list to keep same order as sortedWorkslots
//                } else { //ws is expired, has repeatRule and needs updated (and not updated on server yet) - to avoid delay from server: create new instances and return them, while in background check exist on server??
//                    RepeatRuleParseObject repeatRule;
//                    if ((repeatRule = ws.getRepeatRule()) != null) {
//                        repeatRule.updateWorkslots(ws, false);
//                    }
//                }
//            }
//            return updatedWorkslotList;
//        }
////        return null;
//    }
//</editor-fold>
    /**
     * return valid (future) workslots, meaning they end *after* the 'now' value
     * set for this WorkSlotList
     *
     * @return
     */
    public List<WorkSlot> getWorkSlots() {
//        return getWorkSlots(getNow());
        return getWorkSlots(now);
    }

    public List<WorkSlot> getWorkSlotListFull() {
//        return getWorkSlots(MyDate.MIN_DATE); //too slow
        return sortedOnStartTimeWorkslotList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static public long getWorkTimeSum(List<WorkSlot> list) {
//    public long getWorkTimeSum(List<WorkSlot> list) {
////        List<WorkSlot> list = getWorkSlotListN(false);
//        long sum = 0;
////        long now = System.currentTimeMillis();
//        for (WorkSlot workSlot : list) {
//            sum += workSlot.getDurationAdjusted(now);
//        }
//        return sum;
//    }
//</editor-fold>
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
        return getWorkTimeSum(sortedOnStartTimeWorkslotList, fromDate.getTime(), toDate.getTime());
    }

    /**
     * return the remaining work time (from now till eternity)
     *
     * @return
     */
    public long getWorkTimeSum() {
//        return getWorkTimeSum(this, new Date(), new Date(Long.MAX_VALUE));
//        return getWorkTimeSum(workslotList, new Date(), new Date(Long.MAX_VALUE));
//        return getWorkTimeSum(getWorkSlots(), new Date(), new Date(Long.MAX_VALUE));
        long sum = 0;
        for (WorkSlot workSlot : getWorkSlots()) { //getWorkSlots() <=> only sum up workslots with future worktime
//            sum += workSlot.getDurationAdjusted(getNow(), MyDate.MAX_DATE);
//            sum += workSlot.getDurationAdjusted(getNow()); //, MyDate.MAX_DATE);
            sum += workSlot.getDurationAdjusted(now); //, MyDate.MAX_DATE);
        }
        return sum;
    }

    private void sortWorkSlotList() {
        WorkSlot.sortWorkSlotList(sortedOnStartTimeWorkslotList);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * sort on WorkSLot startTime
     */
//    public WorkSlotList sortWorkSlotList() {
//        return sortWorkSlotList(false);
//    }
//    private void sortWorkSlotListOLD(boolean sortOnEndTime) {
////        Collections.sort(this, (i1, i2) -> FilterSortDef.compareDate(((WorkSlot) i1).getStartTimeD(), ((WorkSlot) i2).getStartTimeD()));
//        if (sortOnEndTime) {
//            Collections.sort(sortedOnStartTimeWorkslotList, getMultipleComparator(new Comparator[]{
//                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()),
//                (Comparator<WorkSlot>) (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD())
//            }));
//        } else {
//            Collections.sort(sortedOnStartTimeWorkslotList, (i1, i2) -> FilterSortDef.compareDate(i1.getStartTimeD(), i2.getStartTimeD()));
//        }
//    }
//</editor-fold>
    /**
     * returns a list with all the workSlots that has some work time between
     * startDate and endDate (e.g. includes workslots that start *before*
     * startDate but which endsfor date. assumes that workslotsSortedOnStartDate
     * has not dates *earlier* than date (ie either the first workslot starts on
     * date, or it starts on a later date). Doesn't remove the return WorkSlots
     * for the WorkSlotList.
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
    public static List<WorkSlot> getWorkSlotsInInterval( List<WorkSlot>sortedOnStartTimeWorkslotList, Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
//        return removeWorkSlotsInInterval(this, startDate, endDate, includeFullDay, isSorted);
        //skip all elements that end *before* startTime, stops nu with workSlot
        ArrayList<WorkSlot> result = new ArrayList<>();
        long endTime = endDate.getTime();
        long startTime = startDate.getTime();
        for (WorkSlot ws : sortedOnStartTimeWorkslotList) { //optimization: optimize for fact that workslotList is now sorted
            if (ws.getStartTimeD().getTime() < endTime && ws.hasDurationInInterval(startTime, endTime)) {
                result.add(ws);
            }
        }
//        return new WorkSlotList(owner, result, true);
        return result;
    }

    public WorkSlotList getWorkSlotsInInterval(Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
//        return removeWorkSlotsInInterval(this, startDate, endDate, includeFullDay, isSorted);
        //skip all elements that end *before* startTime, stops nu with workSlot
//        ArrayList<WorkSlot> result = new ArrayList<>();
//        long endTime = endDate.getTime();
//        long startTime = startDate.getTime();
//        for (WorkSlot ws : sortedOnStartTimeWorkslotList) { //optimization: optimize for fact that workslotList is now sorted
//            if (ws.getStartTimeD().getTime() < endTime && ws.hasDurationInInterval(startTime, endTime)) {
//                result.add(ws);
//            }
//        }
        return new WorkSlotList(owner, getWorkSlotsInInterval(sortedOnStartTimeWorkslotList, startDate, endDate, includeFullDay, isSorted), true);
    }

    public int size() {
//        return workslotList.size();
        return getWorkSlots().size();
    }

    public WorkSlot get(int index) {
//        return sortedWorkslotList.get(index);
        return getWorkSlots().get(index);
    }

    public boolean contains(WorkSlot workSlot) {
//        return sortedWorkslotList.contains(workSlot);
        return getWorkSlots().contains(workSlot);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * remove workSlots that are expired, used to only calculate finishTime
     * based on current workslots
     *
     * @param workSlotList
     * @param now
     * @return
     */
//    public static List<WorkSlot> removePastWorkSlotsXXX(List<WorkSlot> workSlots, long now) {
//        final int WORKSLOT_LIMIT = 200;
//
//        List<WorkSlot> result = new ArrayList<>();
//
////        ASSERT.that(!workSlots.isSorted(), "workSlots must be sorted for this algo to work");
////        long nowLong = now.getTime();
////        List<WorkSlot>  workSlots =workSlotList.getWorkSlots();
//        for (int i = workSlots.size() - 1; i >= 0; i--) {
//            if (workSlots.get(i).getEndTime() <= now) {
//                result = workSlots.subList(i, workSlots.size() - 1);
//                return result;
//            }
//        }
//        //if we get to here, no workslots had endTime in the past, so we don't have to remove
//        return workSlots;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static WorkSlotList removeWorkSlotsInIntervalXXX(WorkSlotList workSlotList, Date startDate, Date endDate, boolean includeFullDay, boolean isSorted) {
//        final int WORKSLOT_LIMIT = 200;
//        long startTime = includeFullDay ? MyDate.getStartOfDay(startDate).getTime() : startDate.getTime();
//        long endTime = includeFullDay ? MyDate.getEndOfDay(endDate).getTime() : endDate.getTime();
//
////        WorkSlotList result = new WorkSlotList();
//        ArrayList<WorkSlot> result = new ArrayList<>();
//
////        if (workSlotList.size() > WORKSLOT_LIMIT) { //if more than 20 elements
//        if (!isSorted) {
////            workSlotList.sortWorkSlotList(false);
////            workSlotList.sortWorkSlotList();
//            sortWorkSlotList(workSlotList.getWorkSlotListFull());
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////            Iterator<WorkSlot> it = workSlotList.iterator();
////            WorkSlot workSlot = null;
////            //for all elements, efficient when elements are removed (it skips going through elements that that start *after* endTime)
////            while (it.hasNext() && (workSlot = it.next()).getStartTimeD().getTime() < endTime) { //if workSlot.getStartTime() > endTime, then the workSlot is definitely outside the desired interval
////                if (workSlot.hasDurationInInterval(startTime, endTime)) {
////                    result.add(workSlot);
////                    if (false) { //don't remove since some workSlots may fall within different intervals, eg if stretching over midnight
////                        it.remove();
////                    }
////                }
////            } //skip all elements that end *before* startTime, stops nu with workSlot
////</editor-fold>
//        //skip all elements that end *before* startTime, stops nu with workSlot
//        for (WorkSlot ws : workSlotList.sortedWorkslotList) { //optimization: optimize for fact that workslotList is now sorted
//            if (ws.getStartTimeD().getTime() < endTime && ws.hasDurationInInterval(startTime, endTime)) {
//                result.add(ws);
//            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        } else {
////            //go through every workslot (less efficient if many, but doesn't require them to be sorted)
////            Iterator<WorkSlot> it = workSlotList.iterator();
////            while (it.hasNext()) {
////                WorkSlot workSlot = it.next();
////                if (workSlot.hasDurationInInterval(startTime, endTime)) {
////                    result.add(workSlot);
////                    if (false && workSlot.getDurationAdjusted(startTime, endTime) == workSlot.getDurationInMillis()) { //do not remove if some of the workSlots falls outside the interval (meaning it should also be return for the other interval)
////                        it.remove();
////                    }
////                }
////            }
////        }
////</editor-fold>
//        return new WorkSlotList(result);
//    }
//</editor-fold>
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
//        return hasComingWorkSlots(getNow());
        return hasComingWorkSlots(now);
    }

    public boolean hasComingWorkSlots(long now) {
//        if (size() == 0) {
//            return false;
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        long now = System.currentTimeMillis();
//        for (WorkSlot workSlot : workslotList) {
//            if (workSlot.getEndTime() > now) {
//                return true;
//            }
//        }
//</editor-fold>
        for (int i = sortedOnStartTimeWorkslotList.size() - 1; i >= 0; i--) {
            if (sortedOnStartTimeWorkslotList.get(i).getEndTime() > now) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List getChildrenList(Object itemInThisList) {
        if (itemInThisList == null) {
            List itemList = getWorkSlots();
            return itemList; //see JavaDoc of getChildren: null should return the tree roots
        } else if (itemInThisList instanceof MyTreeModel) {
            return ((MyTreeModel) itemInThisList).getChildrenList(null);
        } else {
            return new ArrayList();
        }
    }

    @Override
    public boolean isLeaf(Object itemInThisList) {
//        return node.getWorkSlots().size() == 0;
        if (itemInThisList instanceof WorkSlot) {
            List subtasks = ((WorkSlot) itemInThisList).getItemsInWorkSlot();
            return subtasks == null || subtasks.size() == 0;
        }
        if (itemInThisList instanceof Item) {
            List subtasks = ((Item) itemInThisList).getList(); //getList since we use this call to know if there are visible (unfiltered) tasks to expand/show
            return subtasks == null || subtasks.size() == 0;
        } else {
            return true;
        }
    }

}
