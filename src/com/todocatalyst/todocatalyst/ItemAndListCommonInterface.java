/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author THJ
 */
public interface ItemAndListCommonInterface extends MyTreeModel {

    public boolean isDone();
//    boolean isTemplate();
//    void setTemplate(boolean on);

    public void setDone(boolean done);

    public ItemStatus getStatus();

//    public long getRemainingTime();
    /**
     * return the remaining time for this Item or ItemList. For Items with
     * subtasks the sum of the subtasks is returned, for leaf-tasks their own
     * remaining is returned, for ItemLists, the sum of their elements is
     * returned.
     *
     * @return
     */
    public long getRemainingEffort();

    public long getEffortEstimate();

    public long getActualEffort();
//    public long getWorkTimeSum();

    public Date getRemainingEffortD(); //TODO use getRemainingEffortD everywhere instead of getRemainingEffort

    /**
     * returns null if no workslots
     *
     * @return
     */
    public WorkSlotList getWorkSlotListN();

    public WorkSlotList getWorkSlotListN(boolean refreshWorkSlotListFromDAO);

    public int getNumberOfUndoneItems(boolean includeSubTasks);

    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus, boolean changingFromDone);
    
    public int getCountOfSubtasksWithStatus(boolean recurse, List<ItemStatus> statuses);

//    public int getNumberOfDoneItems(boolean includeSubTasks);
//    public int getNumberOfItemsWithStatus(ItemStatus status, boolean includeSubTasks);
    /**
     * counts the number of items, including the item itself
     *
     * @param onlyUndone only count Undone (Created, Ongoing, Waiting) tasks. If
     * false, counts total number of tasks
     * @param countLeafTasks count leaf-tasks (ignore intermediate levels of
     * tasks)
     * @return
     */
    public int getNumberOfItems(boolean onlyUndone, boolean countLeafTasks);

    /**
     * counts only the number of subtasks (so a non-project or leaftask will
     * return 0)
     *
     * @param onlyUndone
     * @param countLeafTasks
     * @return
     */
    public int getNumberOfSubtasks(boolean onlyUndone, boolean countLeafTasks);

//    public int getNumberOfUndoneItems();
    public String getComment();

    public void setComment(String val);

    /* returns the key text string for the subtypes of BaseItem, e.g. Item
     * returns Description, Category categoryName, ... getText() in BaseItem is
     * not supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    public String getText();

    /**
     * sets the key text string for the subtypes of BaseItem, e.g. for Item
     * Description, for Category categoryName, ... setText() in BaseItem is not
     * supposed to be used directly but must be overwritten by subtypes.
     *
     * @param text
     * @return
     */
    public void setText(String text);

    public boolean isExpandable();

//    public ParseObject getOwner();
//    public java.util.List getItemList();
//    public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner);
//    public ParseObject setOwner(ParseObject owner);
//    public void setOwner(ParseObject owner);
    public void setOwner(ItemAndListCommonInterface owner);

    /**
     * returns the previous value of owner or null if none
     *
     * @param owner
     * @return
     */
    public ItemAndListCommonInterface getOwner();

    /**
     * will return the list that owns a task/project, or the task that owns a subtask
     * @return
     */
    default public List<? extends ItemAndListCommonInterface> getOwnerList() {
        return getOwner().getList();
    }

    ;
//    public ParseObject getOwner();

    /**
     * returns a printable string that uniquely identifies this item
     *
     * @return
     */
//    public String getItemIdStr();
    public enum ToStringFormat {
        TOSTRING_COMMA_SEPARATED_LIST,
        TOSTRING_DEFAULT;
    }

    public String toString(ToStringFormat format);

    /**
     * creates a complete (deep) copy of this item. Only elements that are
     * 'owned' by the item are copied. Elements that are only referenced, like
     * categories, are NOT copied. Lists are special case: copyMeInto will
     * create a new list referencing the same elements, whereas clone() will
     * also clone the elements in the list.
     */
    public ItemAndListCommonInterface cloneMe(); //TODO!!! remove, to dangerous if forgetting right parameters

    public ItemAndListCommonInterface cloneMe(Item.CopyMode copyFieldDefintion);

    public ItemAndListCommonInterface cloneMe(Item.CopyMode copyFieldDefintion, int copyExclusions);

//        BaseItem newCopy = new BaseItem();
//        copyMeInto(newCopy);
//        return newCopy;
//    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    /** returns the int value used by getSumAt() to calculate the sum of lists. Can be overwritten by eg. ItemLists to return some meaningful value to add up in
    //    lists of lists */
    //    int getSumField(int fieldId) {
    //        Object itemField;
    //        if (this instanceof Item && (itemField = (((Item) this).getFilterField(fieldId))) instanceof Integer) {
    //            return ((Integer) itemField).intValue();
    //        } else {
    //            return 0;
    //        }
    //    }
    //</editor-fold>
    /**
     * copy this object into the given destiny object
     *
     * @param destiny
     */
//    public void copyMeInto(ItemAndListCommonInterface destiny, boolean copyText);
////        super.copyMeInto(destiny); //- no fields in BaseBaseItem
//        //super.copy(source);
//        //destiny.setGuid(0); //must NOT clone guid //- not necessary since guid is always initialized to 0
//        //destiny.setRmsIdx(getRmsIdx()); //- must NOT be cloned since the clone would then be written to same RMS position (overwriting the original item)
////        destiny.setOwner(getOwner()); //-don't reuse owner
////        destiny.setLogicalName(getLogicalName()); //-don't reuse Logical name
//        destiny.storedFormatVersion = storedFormatVersion;
//        destiny.setTypeId(getTypeId());
//        if (copyText) {
//            destiny.setText(new String(getText())); //make a copy of text string (to be able to edit new string separately)
//        }
//        //setCommitted(false); // do not set autocommit to true for cloned objects, it should only be set once they are actively saved to avoid inconsistencies btw RMS and memory //-should be default for new objects
//        //xxxsetConsistency(false); // do not set consistency to true for cloned objects, it should only be set once they are actively saved to avoid inconsistencies btw RMS and memory //-should be default for new objects
//    }
    /**
     * copy this object into the given destiny object
     *
     * @param destiny
     */
    public void copyMeInto(ItemAndListCommonInterface destiny);
//        copyMeInto(destiny, true);
//    }

    /**
     * returns a short string for displaying to the user the item in a list
     *
     * @return
     */
//    public String shortString();
////        return "typeId=" + BaseItemTypes.toString(typeId)+ "|guid=" + guid;
////        return BaseItemTypes.toString(getTypeId()) + (getText().length() != 0 ? "\"" + getText() + "\"" : "Gui" + getGuid());
//        return BaseItemTypes.toString(getTypeId()) + "\"" + getText() + "\"";
//    }
    /**
     * deletes this instance
     */
    public void delete() throws ParseException;

    public void setStatus(ItemStatus itemStatus);

    /**
     * returns the list of items owned (subtasks for an Item, items for an
     * ItemList) CANNOT be used since ItemLists needs to intercept the changes
     * to handle sublists (for meta-categories)
     *
     * @return
     */
    public int size();

    /**
     * adds subitem to the list (gets the list from Parse, adds the element,
     * sets the list). Makes this Item/ItemList the owner of the inserted
     * element. Owner must be null before insert!
     *
     * @param subItemOrList
     * @return
     */
    public boolean addToList(ItemAndListCommonInterface subItemOrList);

    /**
     * add subItemOrList to the list of subtasks at position index and setsubItemOrList's owner to this.
     * adds subitem to the list (gets the list from Parse, adds the element,
     * sets the list). Makes this Item/ItemList the owner of the inserted
     * element. Owner must be null before insert!
     *
     * @param index
     * @param subItemOrList
     * @return 
     */
    public boolean addToList(int index, ItemAndListCommonInterface subItemOrList);

    /**
     * 
     * @param item
     * @param subItemOrList
     * @param addAfterItem if true, add subItemOrList *after* the position of item
     * @return 
     */
    public boolean addToList(ItemAndListCommonInterface item, ItemAndListCommonInterface subItemOrList, boolean addAfterItem);

    /**
     * remove the subitem from the list (gets the list from Parse, removes the
     * element, sets the list, sets Owner for subItemOrList to null)
     *
     * @param subItemOrList
     * @return
     */
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList);

    /**
     * remove this from its owner and set this.owner=null;
     */
    default public void removeMeFromOwner() {
        List ownerList = getOwner().getList();
//        getOwnerList().removeItem(this);
        ownerList.remove(this);
        getOwner().setList(ownerList);
        setOwner(null);
    }

    /**
     * returns the index of the subitem/subtask in the list of subtasks, or the
     * index in the list of items for an itemList
     *
     * @param subItemOrList
     * @return
     */
    public int getItemIndex(ItemAndListCommonInterface subItemOrList);

    /**
     * returns the filtered and sorted list of sub-Objects (tasks). Never returns a null list.
     *
     * @return never null
     */
    public List<? extends ItemAndListCommonInterface> getList();

    /**
     * sets the list of items owned
     *
     * @param listOfSubObjects
     */
    public void setList(List listOfSubObjects);

    /**
     * returns true if this list or Item should NOT be saved to parse, e.g.
     * because it is a temporary instance, e.g. wrapping a parse search results
     *
     * @return
     */
    public boolean isNoSave();

    /**
     * will return the (first) WorkTimeDefinition for this item. The workTime is
     * found by first checking if the item itself has work time defined (eg a
     * project or subproject), if not whether its owner has (owner may be either
     * an Item/project or the owner list) or finally, if one of the categories
     * has workTime defined (categories are searched in order by which they were
     * selected)
     *
     * @param reset will reload the workSlots, e.g. if edited or time has passed
     * @return null if no worktime is defined
     */
//    public WorkTimeDefinition getWorkTimeAllocatorN(boolean reset);
    public WorkTimeAllocator getWorkTimeAllocatorN(boolean reset);

//<editor-fold defaultstate="collapsed" desc="comment">
//default    public WorkTimeDefinition getWorkTimeAllocatorN(boolean reset) {
//        if (wtd != null && !reset) {
//            return wtd;
//        } else { //get right workSlots
//            WorkSlotList workSlots = getWorkSlotListN();
//            if (workSlots != null && workSlots.hasComingWorkSlots()) {
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), new WorkTimeSlices(workSlots));
//                return wtd;
//            } else {
//                WorkTimeSlices workTime = getAvailableWorkTime();
//                if (workTime != null) {
//                    return new WorkTimeDefinition(workTime);
//                }
//            }
//        }
//        return null;
//    }
//</editor-fold>
    /**
     * will return the (first) WorkTimeDefinition for this item. The workTime is
     * found by first checking if the item itself has work time defined (eg a
     * project or subproject), if not whether its owner has (owner may be either
     * an Item/project or the owner list) or finally, if one of the categories
     * has workTime defined (categories are searched in order by which they were
     * selected)
     *
     * @return null if no worktime is defined
     */
//    public WorkTimeDefinition getWorkTimeAllocatorN();
    default public WorkTimeAllocator getWorkTimeAllocatorN() {
        return getWorkTimeAllocatorN(false);
    }

    /**
     * reset/refresh the WorkTimeDefinition, must be called whenever work slots
     * or items have been changed.
     */
    public void resetWorkTimeDefinition();

    /**
     * returns the finish time of item based on the owner's work time
     *
     * @param item
     * @return
     */
//    default public Date getFinishTimeXXX(ItemAndListCommonInterface item) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
////            return new Date(workTimeDef.getFinishTime(item));
//            return workTimeDef.getAllocatedWorkTimeN(item).getFinishTimeD();
//        } else {
//            return new Date(0);
//        }
//    }
    public String getObjectIdP();

    /**
     * sets the workTime for the item (transitory for now, calculated
     * dynamically in memory and not stored)
     *
     * @param workTime
     */
//    public void setWorkTime(WorkTimeSlices workTime);
    /**
     * return the list of work time providers in priority order. will for
     * example return in priority order: (first) Category with worktime, then
     * owner which can be a Project or a List. For ItemLists or Categories, will
     * just return WorkSlotList.
     *
     * @return null if no providers
     */
    default public List<ItemAndListCommonInterface> getPotentialWorkTimeProvidersInPrioOrder() {
        WorkSlotList workSlots = getWorkSlotListN();
        if (workSlots == null) {
            return null;
        } else {
            List<ItemAndListCommonInterface> res = new ArrayList();
            res.add(this);
            return res;
        }
    }

    /**
     * returns true if has future workTime associated with it. either from own
 workSlots or allocated WorkTimeSlices.
     *
     * @return
     */
    default public boolean mayProvideWorkTime() {
        WorkSlotList workSlots = getWorkSlotListN();
//        return workSlots != null && workSlots.size() > 0; //||getAllocatedWorkTimeN()!=null;
//        return (workSlots != null && workSlots.size() > 0) || getAvailableWorkTime() != null;
//        return (workSlots != null && workSlots.size() > 0) || getAllocatedWorkTimeN() != null;
        if (workSlots != null && workSlots.size() > 0) {
            return true;
        }

        ItemAndListCommonInterface owner = getOwner();
//        if (false && owner != null && owner.mayProvideWorkTime()) { //NB - do not iterate up the hierarchy
        if (owner != null && owner.mayProvideWorkTime()) { //NB - yes, do iterate up the hierarchy since any owner (e.g. project) at a higher level may recursively provide worktime
            return true;
        }
//        for (Category cat:getCa|| getAllocatedWorkTimeN() != null;
        return false;
//        return getPotentialWorkTimeProvidersInPrioOrder() != null; //TODO optimization - is there a more efficient way?
    }

    /**
     * return the available work time. A list since can come from different work
     * time providers for example if a category does not allocate enough time to
     * completely finish a task, the rest may come from the owner. Will combine
     * workTime from different sources into one.
     *
     * @return workSlots and possibly workTime allocated by owner. null if no
     * WorkTime available
     */
//    public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface itemOrList);
//<editor-fold defaultstate="collapsed" desc="comment">
//    default public WorkTimeSlices getAvailableWorkTimeXXX() {
//        WorkTimeSlices workTime = null; // = new WorkTimeSlices();
//
//        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
//        List<ItemAndListCommonInterface> providers = getPotentialWorkTimeProvidersInPrioOrder();
////        if (providers != null) {
//        for (ItemAndListCommonInterface prov : getPotentialWorkTimeProvidersInPrioOrder()) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            ItemAndListCommonInterface prov;
////            List<ItemAndListCommonInterface> providers = getPotentialWorkTimeProvidersInPrioOrder(true);
////            for (int i = 0, size = providers.size(); i < size; i++) {
////                while (workTimeProviders.hasNext()) {
////process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////                    ItemAndListCommonInterface prov = workTimeProviders.next();
////                prov = providers.get(i);
////                if (prov == provider) {
////                    break; // return remaining;
////                } else if (prov == this) {
////</editor-fold>
//            WorkTimeAllocator wtd = prov.getWorkTimeAllocatorN();
//            if (wtd != null) {
//                WorkTimeSlices wt = wtd.allocateWorkTimeXXX(this);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (workTime != null) {
////                        workTime.addWorkTime(wt);
////                    } else {
////                        workTime = wt;
////                    }
////</editor-fold>
//                workTime.addWorkTime(wt);
//            }
//        }
////        }
//        return workTime;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    default public WorkTimeSlices getAvailableWorkTime() {
//        WorkTimeSlices workTime = null; // = new WorkTimeSlices();
//
//        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
//        WorkSlotList workSlots = getWorkSlotListN();
//        if (workSlots != null) {
////            workTime.addWorkTime(workSlots);
//            workTime = new WorkTimeSlices(workSlots);
//        }
//
////        WorkTimeSlices allocated = getAllocatedWorkTimeN();
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null) {
//            WorkTimeDefinition wtd = owner.getWorkTimeAllocatorN();
//            if (wtd != null) {
//                WorkTimeSlices allocated = wtd.getAllocatedWorkTimeN(this);
////        WorkTimeSlices allocated = getAllocatedWorkTimeN();
//                if (allocated != null) {
//                    if (workTime != null) {
//                        workTime.addWorkTime(allocated);
//                    } else {
//                        workTime = allocated;
//                    }
//                }
//            }
//        }
//        return workTime;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//    default public WorkTimeSlices getAvailableWorkTimeOLD() {
//        WorkTimeSlices workTime = null;
//        WorkTimeSlices lastWorkTime;
//        long neededWorkTime = getRemainingEffort();
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            neededWorkTime = this.getWorkTimeRequiredFromThisProvider(workTimeProvider);
//            WorkTimeSlices newWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this, neededWorkTime);
//            if (workTime == null) {
////                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
//                workTime = newWorkTime; //workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this,neededWorkTime);
////                workTime = lastWorkTime;
//            } else {
//                workTime.addWorkTime(newWorkTime);
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            else if (workTime.getRemainingDuration() > 0) {
//////                workTime=workTimeProviders.getAllocatedWorkTimeN(workTime,workTimeProvider.getAllocatedWorkTimeN(this, workTime.getUncoveredTime()));
////                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this, workTime.getRemainingDuration());
////                workTime.setNextWorkTime(lastWorkTime);
////            } else {
////                lastWorkTime = null;
////                assert false;
////            }
////            if (lastWorkTime != null) {
////            neededWorkTime -= lastWorkTime.getAllocatedDuration(false); //only deduct
////</editor-fold>
//            neededWorkTime = workTime.getRemainingDuration(); //last allocated workTimeSlice always contains the missing duration
////            }
//        }
//        return workTime;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    default public WorkTimeSlices getWorkTimeOLD() {
//        long neededWorkTime = getRemainingEffort();
//        WorkTimeSlices workTime = null;
//        WorkTimeSlices lastWorkTime;
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            if (workTime == null) {
//                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
//                workTime = lastWorkTime;
//            } else if (workTime.getRemainingDuration() > 0) {
////                workTime=workTimeProviders.getAllocatedWorkTimeN(workTime,workTimeProvider.getAllocatedWorkTimeN(this, workTime.getUncoveredTime()));
//                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this, workTime.getRemainingDuration());
//                workTime.setNextWorkTime(lastWorkTime);
//            } else {
//                lastWorkTime = null;
//                assert false;
//            }
////            if (lastWorkTime != null) {
//            neededWorkTime -= lastWorkTime.getAllocatedDuration(false); //only deduct
////            }
//        }
//        return workTime;
//    }
//</editor-fold>
    /**
     * time allocated to this item (from all workTimeProviders: own workslots,
     * categories, owner)
     *
     * @return null if no WorkTimeSlices allocated
     */
    default public WorkTimeSlices getAllocatedWorkTimeN() {
        throw new Error("Not supported yet."); //should not be called for ItemLists and Categories (or WorkSlots)
    }

//     {
////        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
//        throw new Error("Not supported yet."); //not supported by WorkSlot
//    }
    /**
     * allocate workTime from this element's WorkTimeDefinition to itemOrList
     * with duration remainingDuration.
     *
     * @param itemOrList
     * @param remainingDuration
     * @return
     */
    default public WorkTimeSlices allocateWorkTimeXXX(ItemAndListCommonInterface itemOrList) {
//        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(itemOrList);
        WorkTimeAllocator wt = getWorkTimeAllocatorN();
        return wt != null ? wt.allocateWorkTimeXXX(itemOrList) : null;
    }

    /**
     * allocate workTime from this element's WorkTimeDefinition to itemOrList
     * with duration remainingDuration.
     *
     * @param itemOrList
     * @param remainingDuration
     * @return
     */
    default public WorkTimeSlices allocateWorkTime(ItemAndListCommonInterface itemOrList, long remainingDuration) {
//        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(itemOrList, remainingDuration);
        WorkTimeAllocator wt = getWorkTimeAllocatorN();
        return wt != null ? wt.getAllocatedWorkTime(itemOrList, remainingDuration) : null;
    }

//    default public long getAllocatedWorkTimeN() {
//        return getAllocatedWorkTimeN().getFinishTime();
//    }
    default public Date getFinishTimeD() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        WorkTimeSlices workTime = getAvailableWorkTime();
//        if (workTime != null) {
////            return new Date(workTimeDef.getFinishTime(item));
//            return workTime.getFinishTimeD();
//        } else {
//            return new Date(0);
//        }
//</editor-fold>
//        WorkTimeSlices wt = getAllocatedWorkTimeN();
//        return wt != null ? wt.getFinishTimeD() : new Date(MyDate.MIN_DATE);
        return new Date(getFinishTime());
    }

    /**
     * returns the calculated finishTime for this item
     *
     * @return finishTime or MyDate.MAX_DATE if no workTime was available/allocated, or if or insufficient workTime to finish the task was allocated
     */
    default public long getFinishTime() {
        long finishTime=MyDate.MAX_DATE;
//<editor-fold defaultstate="collapsed" desc="comment">
//        assert false:"getFinishTime on ItemListCommonInterface should never be called";
//        return getFinishTime(System.currentTimeMillis());
//    }
//
//    default public long getFinishTime(long now) {
//        return getAllocatedWorkTimeN().getFinishTime();
//</editor-fold>
        WorkTimeSlices workTime = getAllocatedWorkTimeN();
//        long finishTime = MyDate.MAX_DATE;
        if (workTime != null) {
            if (Config.WORKTIME_DETAILED_LOG) {
                Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (workTime != null ? workTime.toString() : "<null>") + ", returning=" + new Date(workTime.getFinishTime()));
            }
            if (workTime.getRemainingDuration() == 0) {
//                return workTime.getFinishTime();
                finishTime= workTime.getFinishTime();
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            else {
//                return MyDate.MAX_DATE;
//            }
//        } else {
//            return MyDate.MAX_DATE; //cannot allocate enough time
//</editor-fold>
        }
        if (false &&Config.WORKTIME_TEST) {
            long remainingEffort = getRemainingEffort();
            long allocated=workTime.getAllocatedDuration();
            assert workTime == null || !(allocated > remainingEffort): "allocated too much time";
        }
//        return MyDate.MAX_DATE; // returning MAX, means cannot allocate enough time
        return finishTime; //cannot allocate enough time
    }

    default public long getFinishTimeOLD2() {
//        return getAllocatedWorkTimeN().getFinishTime();
        WorkTimeSlices wt = getAllocatedWorkTimeN();
//        long finishTime = MyDate.MAX_DATE;
        if (wt != null) {
            long finishTime = wt.getFinishTime();
            if (Config.WORKTIME_DETAILED_LOG) {
                Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (wt != null ? wt.toString() : "<null>") + ", returning=" + new Date(finishTime));
            }

//        return wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
//        ASSERT.that(wt.getAllocatedDuration() == getRemainingEffort() || wt.getAllocatedDuration() < getRemainingEffort(), "allocated too much time");
//        if (wt != null) {
            long remainingEffort = getRemainingEffort();
            long allocatedDuration = wt.getAllocatedDuration();
            if (Config.WORKTIME_TEST) {
                ASSERT.that(wt == null || !(allocatedDuration > remainingEffort), "allocated too much time");
            }
//        return wt != null &&  allocatedDuration>= remainingEffort ? finishTime : MyDate.MAX_DATE;
            if (allocatedDuration >= remainingEffort) {
                return finishTime;
            } else {
                return MyDate.MAX_DATE;
            }
        } else {
            return MyDate.MAX_DATE; //cannot allocate enough time
        }
    }

    default public long getFinishTimeOLD() {
//        return getAllocatedWorkTimeN().getFinishTime();
        WorkTimeSlices wt = getAllocatedWorkTimeN();
        long finishTime = wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
        if (Config.WORKTIME_DETAILED_LOG) {
            Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (wt != null ? wt.toString() : "<null>") + ", returning=" + new Date(finishTime));
        }

//        return wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
//        ASSERT.that(wt.getAllocatedDuration() == getRemainingEffort() || wt.getAllocatedDuration() < getRemainingEffort(), "allocated too much time");
        if (wt != null) {
            long remainingEffort = getRemainingEffort();
            long allocatedDuration = wt.getAllocatedDuration();
            if (Config.WORKTIME_TEST) {
                assert wt == null || !(allocatedDuration > remainingEffort): "allocated too much time";
            }
//        return wt != null &&  allocatedDuration>= remainingEffort ? finishTime : MyDate.MAX_DATE;
            return allocatedDuration >= remainingEffort ? finishTime : MyDate.MAX_DATE;
        } else {
            return MyDate.MAX_DATE; //cannot allocate enough time
        }
    }

    /**
     * called to indicate the workTime needs to be udpated/refreshed. E.g. by an
     * Item if status or remaining time changes, or by a workslot if duration or
     * startTime change. This assume that work time calculations are cached. It
     * should invalidate as few items as possible, e.g. typically only items
     * that come later than the changed one.
     */
//    public void refreshWorkTime();
    //TODO!!!!! must store separately for cache!! Same as 
    /**
     * returns how much workTime this element requires from the provider. If the
     * element has other higher prioritized providers they are asked how much
     * they can provide and only the remaining is returned for this provider.
     *
     * @param provider
     * @return
     */
    default public long getWorkTimeRequiredFromProvider(ItemAndListCommonInterface provider) {
        throw new Error("Not supported yet."); //should never be called for ItemLists/Categories?!
    }

    /**
     * sets an appropriate new value for this object based on the values for the
     * field in the objects before and after. Used when inserting a new object
     * into a sorted list to insert it (approximately) in the right place. The
     * new field value may be the same as the before object (simplest solution)
     * or be calculated in some other way based on the values before/after, e.g.
     * as the median value between the two.
     *
     * @param newObject
     * @param fieldParseId
     * @param objectBefore
     * @param objectAfter
     */
    public void setNewFieldValue(String fieldParseId, Object objectBefore, Object objectAfter);

//    default public long getWorkTimeRequiredFromOwner() {
////        return getRemainingEffort(); //for lists and categories, we use the standard remaining, for Items it's a special impl
//        throw new Error("Not supported yet."); //should never be called for ItemLists/Categories?!
//    }
}
