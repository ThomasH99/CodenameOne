/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

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
    public WorkSlotList getWorkSlotList();

    public WorkSlotList getWorkSlotList(boolean refreshWorkSlotListFromDAO);

    public int getNumberOfUndoneItems(boolean includeSubTasks);

    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus);

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

    public ItemList getOwnerList();
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

    public boolean addToList(int index, ItemAndListCommonInterface subItemOrList);

    /**
     * remove the subitem from the list (gets the list from Parse, removes the
     * element, sets the list)
     *
     * @param subItemOrList
     * @return
     */
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList);

    /**
     * returns the index of the subitem/subtask in the list of subtasks, or the
     * index in the list of items for an itemList
     *
     * @param subItemOrList
     * @return
     */
    public int getItemIndex(ItemAndListCommonInterface subItemOrList);

    /**
     * returns the list of subObjects (tasks)
     *
     * @return
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
//    public WorkTimeDefinition getWorkTimeDefinition(boolean reset);
    public WorkTimeDefinition getWorkTimeDefinition(boolean reset);

//<editor-fold defaultstate="collapsed" desc="comment">
//default    public WorkTimeDefinition getWorkTimeDefinition(boolean reset) {
//        if (wtd != null && !reset) {
//            return wtd;
//        } else { //get right workSlots
//            WorkSlotList workSlots = getWorkSlotList();
//            if (workSlots != null && workSlots.hasComingWorkSlots()) {
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), new WorkTime(workSlots));
//                return wtd;
//            } else {
//                WorkTime workTime = getAvailableWorkTime();
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
//    public WorkTimeDefinition getWorkTimeDefinition();
    default public WorkTimeDefinition getWorkTimeDefinition() {
        return getWorkTimeDefinition(false);
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
    default public Date getFinishTime(ItemAndListCommonInterface item) {
        WorkTimeDefinition workTimeDef = getWorkTimeDefinition();
        if (workTimeDef != null) {
//            return new Date(workTimeDef.getFinishTime(item));
            return workTimeDef.getWorkTime(item).getFinishTimeD();
        } else {
            return new Date(0);
        }
    }

    public String getObjectIdP();

    /**
     * sets the workTime for the item (transitory for now, calculated
     * dynamically in memory and not stored)
     *
     * @param workTime
     */
//    public void setWorkTime(WorkTime workTime);
    /**
     * return the list of work time providers in priority order. will for
     * example return in priority order: (first) Category with worktime, then
     * owner which can be a Project or a List. For ItemLists or Categories, will
     * just return WorkSlotList.
     *
     * @return null if no providers
     */
    default public List<ItemAndListCommonInterface> getWorkTimeProvidersInPrioOrder() {
        WorkSlotList workSlots = getWorkSlotList();
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
     * workSlots or allocated WorkTime.
     *
     * @return
     */
    default public boolean hasWorkTime() {
        WorkSlotList workSlots = getWorkSlotList();
//        return workSlots != null && workSlots.size() > 0; //||getWorkTime()!=null;
//        return (workSlots != null && workSlots.size() > 0) || getAvailableWorkTime() != null;
//        return (workSlots != null && workSlots.size() > 0) || getAllocatedWorkTime() != null;
        if (workSlots != null && workSlots.size() > 0) {
            return true;
        }
        ItemAndListCommonInterface owner = getOwner();
        if (owner != null && owner.hasWorkTime()) {
            return true;
        }
//        for (Category cat:getCa|| getAllocatedWorkTime() != null;
        return false;
//        return getWorkTimeProvidersInPrioOrder() != null; //TODO optimization - is there a more efficient way?
    }

    /**
     * return the allocated work time (a list since can come from different work
     * time providers for example if a category does not allocate enough time to
     * completely finish a task, the rest may come from the owner). Will combine
     * workTime from different sources into one.
     *
     * @return null if no WorkTime available
     */
//    public WorkTime getWorkTime(ItemAndListCommonInterface itemOrList);
    default public WorkTime getAvailableWorkTime() {
        WorkTime workTime = null; // = new WorkTime();

        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
        WorkSlotList workSlots = getWorkSlotList();
        if (workSlots != null) {
//            workTime.addWorkTime(workSlots);
            workTime = new WorkTime(workSlots);
        }

        WorkTime allocated = getAllocatedWorkTime();
        if (allocated != null) {
            if (workTime != null) {
                workTime.addWorkTime(allocated);
            } else {
                workTime = allocated;
            }
        }
        return workTime;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    default public WorkTime getAvailableWorkTimeOLD() {
//        WorkTime workTime = null;
//        WorkTime lastWorkTime;
//        long neededWorkTime = getRemainingEffort();
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            neededWorkTime = this.getWorkTimeRequiredFromThisProvider(workTimeProvider);
//            WorkTime newWorkTime = workTimeProvider.getWorkTimeDefinition().getWorkTime(this, neededWorkTime);
//            if (workTime == null) {
////                lastWorkTime = workTimeProvider.getWorkTimeDefinition().getWorkTime(this);
//                workTime = newWorkTime; //workTimeProvider.getWorkTimeDefinition().getWorkTime(this,neededWorkTime);
////                workTime = lastWorkTime;
//            } else {
//                workTime.addWorkTime(newWorkTime);
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            else if (workTime.getRemainingDuration() > 0) {
//////                workTime=workTimeProviders.getWorkTime(workTime,workTimeProvider.getWorkTime(this, workTime.getUncoveredTime()));
////                lastWorkTime = workTimeProvider.getWorkTimeDefinition().getWorkTime(this, workTime.getRemainingDuration());
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
//    default public WorkTime getWorkTimeOLD() {
//        long neededWorkTime = getRemainingEffort();
//        WorkTime workTime = null;
//        WorkTime lastWorkTime;
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            if (workTime == null) {
//                lastWorkTime = workTimeProvider.getWorkTimeDefinition().getWorkTime(this);
//                workTime = lastWorkTime;
//            } else if (workTime.getRemainingDuration() > 0) {
////                workTime=workTimeProviders.getWorkTime(workTime,workTimeProvider.getWorkTime(this, workTime.getUncoveredTime()));
//                lastWorkTime = workTimeProvider.getWorkTimeDefinition().getWorkTime(this, workTime.getRemainingDuration());
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
     * 
     * @return null if no worktime allocated
     */
    public WorkTime getAllocatedWorkTime();
//     {
////        return getWorkTimeDefinition().getWorkTime(this);
//        throw new Error("Not supported yet."); //not supported by WorkSlot
//    }

    default public WorkTime allocateWorkTime(ItemAndListCommonInterface itemOrList) {
        return getWorkTimeDefinition().getWorkTime(itemOrList);
    }

    default public WorkTime allocateWorkTime(ItemAndListCommonInterface itemOrList, long remainingDuration) {
        return getWorkTimeDefinition().getWorkTime(itemOrList, remainingDuration);
    }

//    default public long getAllocatedWorkTime() {
//        return getAllocatedWorkTime().getFinishTime();
//    }
    default public Date getFinishTimeD() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        WorkTimeDefinition workTimeDef = getWorkTimeDefinition();
//        WorkTime workTime = getAvailableWorkTime();
//        if (workTime != null) {
////            return new Date(workTimeDef.getFinishTime(item));
//            return workTime.getFinishTimeD();
//        } else {
//            return new Date(0);
//        }
//</editor-fold>
        WorkTime wt= getAllocatedWorkTime();
        return wt!=null?getAllocatedWorkTime().getFinishTimeD():new Date(MyDate.MIN_DATE);
    }

    /**
     * returns the calculated finishTime for this item
     * @return finishTime or MyDate.MIN_DATE if no workTime was allocated
     */
    default public long getFinishTime() {
//        return getAllocatedWorkTime().getFinishTime();
        WorkTime wt= getAllocatedWorkTime();
        return wt!=null?getAllocatedWorkTime().getFinishTime():MyDate.MIN_DATE;
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
//    default public long getWorkTimeRequiredFromOwner(ItemAndListCommonInterface provider) {
    default public long getWorkTimeRequiredFromOwner(   ) {
        return getRemainingEffort(); //for lists and categories, we use the standard remaining, for Items it's a special impl
    }

}
