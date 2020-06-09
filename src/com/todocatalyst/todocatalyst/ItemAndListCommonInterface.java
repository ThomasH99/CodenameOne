/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.util.EventDispatcher;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.ItemList.PARSE_WORKSLOTS;
import static com.todocatalyst.todocatalyst.MyUtil.eql;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author THJ
 */
public interface ItemAndListCommonInterface<E extends ItemAndListCommonInterface> extends MyTreeModel, Iterable {

    /**
     * returns true if the task or project is completed, that is either Done
     * (task or subtasks completed) or cancelled or soft-deleted.
     *
     * @return
     */
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
    public long getRemaining();

    public long getEstimate();

    public long getActual();
//    public long getWorkTimeSum();

//    public Date getRemainingEffortD(); //TODO use getRemainingEffortD everywhere instead of getRemainingEffort
    /**
     * returns null if no workslots
     *
     * @return
     */
//    public WorkSlotList getWorkSlotListN(boolean refreshWorkSlotListFromDAO);
    public List<WorkSlot> getWorkSlotsFromParseN();

    /**
     *
     * @return
     */
    default public WorkSlotList getWorkSlotListN() {
        List<WorkSlot> workslots = getWorkSlotsFromParseN();
        if (workslots != null) {
//            WorkSlotList workSlotList = new WorkSlotList(this, workslots, true); //true=already sorted
            WorkSlotList workSlotList = new WorkSlotList(this, workslots, false); //TODO optimization: normally workshots should always be kept sorted, so can use true. //true=already sorted
//            setWorkSlotList(workSlotList);
            return workSlotList;
        } else {
            return null;
        }
    }
//    public WorkSlotList getWorkSlotListN();

    /**
     * set the list of workslots in Parse
     *
     * @param workSlots
     */
//    public void setWorkSlots(List<WorkSlot> workSlots);
    public void setWorkSlotsInParse(List workSlots);

    /**
     * get list of current & future workslots (exclude all that have expired ie
     * endTime is in the past)
     *
     * @param workSlotList
     * @return
     */
//    default public WorkSlotList getWorkSlotListN() {
////        return getWorkSlotListN(true);
//        return getWorkSlotListN();
//    }
//    public void setWorkSlotList(WorkSlotList workSlotList);
    default public void setWorkSlotListXXX(WorkSlotList workSlotList) {
        //TODO currently not stored in ItemList but get from DAO
//        workSlotListBuffer = null;
//        workSlotListBuffer = workSlotList;
//        workTimeAllocator = null;
//        if (workSlotList != null && workSlotList.getWorkSlotListFull().size() > 0) {
//            put(PARSE_WORKSLOTS, workSlotList.getWorkSlots());
        WorkSlot.sortWorkSlotList(workSlotList.getWorkSlotListFull());
        if (Config.TEST) {
            ASSERT.that(workSlotList != null || workSlotList.getWorkSlotListFull().size() == 0 || MyUtil.isSorted(workSlotList.getWorkSlotListFull(),
                    (ws1, ws2) -> ((int) ((((WorkSlot) ws2).getStartTime()) - ((WorkSlot) ws1).getStartTime()))),
                    () -> "setting list of workslots which is NOT sorted: " + workSlotList.getWorkSlotListFull());
        }
        workSlotList.setOwner(this);
        setWorkSlotsInParse(workSlotList.getWorkSlotListFull());
    }

    /**
     * return overlapping workslots, or null if none
     *
     * @param workSlot
     * @return
     */
    default public List<WorkSlot> getOverlappingWorkSlots(WorkSlot workSlot) {
        WorkSlotList workSlotList = getWorkSlotListN();
        if (workSlotList == null || workSlot == null) {
            return null;
        }
        List<WorkSlot> overlapping = new ArrayList<>();
//        for (WorkSlot ws:(List<WorkSlot>)workSlotList) {
        for (WorkSlot ws : workSlotList.getWorkSlotListFull()) {
            if ((ws != workSlot || !ws.equals(workSlot)) && workSlot.overlappingDuration(ws) > 0) {
                overlapping.add(ws);
            }
        }
        return overlapping.isEmpty() ? null : overlapping;
    }

    /**
     * add a new list of workslots to the list and setOwner(this) for them
     *
     * @param workSlot
     */
    default public void addWorkSlots(List<WorkSlot> workSlots) {
        WorkSlotList workSlotList = getWorkSlotListN();
        if (workSlotList == null) {
//            workSlotList = new WorkSlotList();
//            workSlotList = new WorkSlotList(this, workSlots, true);
            workSlotList = new WorkSlotList(this, null, true); //true to avoid unnecessary call to sort
//            workSlotList.setOwner(this);
        } //else {//else needed since new workSlot already added in WorkSlotList constructor above
        for (WorkSlot workSlot : workSlots) {
            workSlotList.add(workSlot); //adds *sorted*!
            workSlot.setOwner(this);
        }
//        setWorkSlotList(workSlotList);
    }

    /**
     * add a new list of workslots to the list and setOwner(this) for them
     *
     * @param workSlot
     */
    default public void removeWorkSlots(List<WorkSlot> workSlots) {
        WorkSlotList workSlotList = getWorkSlotListN();
        if (workSlotList == null) {
            for (WorkSlot workSlot : workSlots) {
                workSlotList.remove(workSlot); //adds *sorted*!
            }
        }
//        setWorkSlotList(workSlotList);
    }

    default public void addWorkSlot(WorkSlot workSlot) {
//        WorkSlotList workSlotList = getWorkSlotListN();
//        if (workSlotList == null) {
////            workSlotList = new WorkSlotList();
//            workSlotList = new WorkSlotList(this, Arrays.asList(workSlot), true);
////            workSlotList.setOwner(this);
//        } else {//else needed since new workSlot already added in WorkSlotList constructor above
//            workSlotList.add(workSlot);
//            workSlot.setOwner(this);
//        }
//        setWorkSlotList(workSlotList);
        addWorkSlots(new ArrayList(Arrays.asList(workSlot)));
    }

    /**
     * remove the workSlot from its owner, and set the workSlot's owner=null
     *
     * @param workSlot
     */
    default public void removeWorkSlot(WorkSlot workSlot) {
        if (workSlot != null) {
            WorkSlotList workSlotList = getWorkSlotListN();
            if (workSlotList != null) {
                workSlotList.remove(workSlot);
                workSlot.setOwner(null);
//                setWorkSlotList(workSlotList);
            }
        }
    }

    /**
     *
     * @param countLeafTasks
     * @return
     */
    public int getNumberOfUndoneItems(boolean countLeafTasks);

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

//    public boolean isExpandable();
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
     * will return the list that owns a task/project, or the task that owns a
     * subtask
     *
     * @return
     */
    default public List<? extends ItemAndListCommonInterface> getOwnersList() {
        return getOwner().getListFull(); //
    }

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
    public void setStatus(ItemStatus itemStatus);

    /**
     * returns the size of the *full* unfiltered list of items owned (subtasks
     * for an Item, items for an ItemList) CANNOT be used since ItemLists needs
     * to intercept the changes to handle sublists (for meta-categories)
     *
     * @return
     */
    default public int getSize() {
        return getListFull().size();
    }

    default public int size() {
        return getList().size();
    }

    /**
     * adds subitem to the beginning or end of the list according to the setting
     * . Makes this Item/ItemList the owner of the inserted element (except for
     * Categories!). Checks that owner must be null before insert to catch any
     * duplicated inserts!
     *
     * @param subItemOrList
     * @return
     */
    public boolean addToList(ItemAndListCommonInterface subItemOrList);

    public boolean addToList(ItemAndListCommonInterface subItemOrList, boolean addToEndOfList);

    public boolean addToList(ItemAndListCommonInterface subItemOrList, boolean addToEndOfList, boolean addAsOwner);

    /**
     * add subItemOrList to the list (for an ItemList or Category) or the list
     * of subtasks (for a project Item) at position index and setsubItemOrList's
     * owner to 'this'. Index is for the full, unfiltered and unsorted, list as
     * it is stored in Parse Server. adds subitem to the list (gets the list
     * from Parse, adds the element, sets the list). Makes this Item/ItemList
     * the owner of the inserted element. Owner must be null before insert!
     *
     * @param index
     * @param subItemOrList
     * @return
     * @ deprecated TOO dangerous to use since index may come from filtered or
     * unfiltered list!
     */
//    public boolean addToList(int index, ItemAndListCommonInterface subItemOrList);
    public boolean addToList(int index, ItemAndListCommonInterface subtask, boolean addAsOwner);

    /**
     *
     * @param newElement new item
     * @param refElement the reference item item inserted before/after this one
     * @param addAfterRefEltOrEndOfList if true, add subItemOrList *after* the
     * position of item
     * @return
     */
    public boolean addToList(ItemAndListCommonInterface newElement, ItemAndListCommonInterface refElement, boolean addAfterRefEltOrEndOfList);

//    public boolean addToList(ItemAndListCommonInterface newElement, int index, boolean addAfterRefEltOrEndOfList);
    /**
     *
     * @param item
     * @param itemRef if null, item is inserted at the end of the list
     * @param insertAfterRefOrEndOfList if true, insert after itemRef. If
     * itemRef is null, then true => insert at end of list, false => insert at
     * head of list
     */
    default public void moveToPositionOf(E item, E itemRef, boolean insertAfterRefOrEndOfList) {
        //NB. Since just reshuffling the list, no impact on any bags
        List listFull = getListFull();
        int oldPos = listFull.indexOf(item);
        ASSERT.that(oldPos >= 0, "error: item=" + item + " moved within list/proj=" + this + ", but not found in list");
        ASSERT.that(item != itemRef, "error: item=" + item + " and itemRef=" + itemRef + " are the same, when moving within list/proj=" + this);

        int newPos = itemRef == null ? (insertAfterRefOrEndOfList ? listFull.size() : 0) : (listFull.indexOf(itemRef) + (insertAfterRefOrEndOfList ? 1 : 0));
        listFull.remove(oldPos); //must remove *before* adding (and remove(item) won't work if added (again) first)
        if (oldPos < newPos) {
            newPos--;
        }
//        int oldPos = listFull.indexOf(item);
        if (newPos >= 0) {
            listFull.add(newPos, item); //insert *before* remove so that removing the item doesn't impact the insert index
        } else {
            listFull.add(item); //insert *before* remove so that removing the item doesn't impact the insert index
        }
        setList(listFull);
//        fireDataChangedEvent(DataChangedListener.CHANGED, newPos);
    }

    /**
     * remove the subitem from the list (gets the list from Parse, removes the
     * element, sets the list, sets Owner for subItemOrList to null)
     *
     * @param subItemOrList
     * @param removeReferences
     * @return
     */
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList, boolean removeReferences);

    default public boolean removeFromList(ItemAndListCommonInterface subItemOrList) {
        return removeFromList(subItemOrList, true);
    }

    /**
     * move item from its current position within the ItemList to after/before
     * the refItem's position (or if refItem not found, to beginning/end of list
     * depending on addAfterRefEltOrEndOfList). If item not already in the list,
     * it will be inserted into the given position.
     *
     * @param item
     * @param refItem
     * @param addCategoryToItem
     * @param addAfterRefEltOrEndOfList
     */
    default public void moveOrAddItemInList(Item item, Item refItem, boolean addAfterRefEltOrEndOfList) {
//        if (removeFromList(item, false)) { //only add if already there
//            addToList(item, refItem, addAfterRefEltOrEndOfList);
//        }
        removeFromList(item, false); //remove in case it is already ther (but it may no be)
        addToList(item, refItem, addAfterRefEltOrEndOfList);
    }

    /**
     * remove this from its owner and set this.owner=null; returns owner (eg so
     * it can be saved)
     *
     * @return
     */
    default public ItemAndListCommonInterface removeFromOwner() {
        ItemAndListCommonInterface owner = getOwner();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (owner != null) {
//            List ownerList = getOwner().getListFull();
////        getOwnerList().removeItem(this);
//            ownerList.remove(this);
//            getOwner().setList(ownerList);
//        }
//</editor-fold>
        if (owner != null) {
            owner.removeFromList(this);
            setOwner(null);
            return owner;
        }
        return null;
    }

    /**
     * returns the index of the item or subtask in the *full* unfiltered list of
     * items/subtasks
     *
     * @param subItemOrList
     * @return
     */
    public int getItemIndex(ItemAndListCommonInterface subItemOrList);

    /**
     * returns the filtered and sorted list of sub-Objects (tasks). Never
     * returns a null list.
     *
     * @return never null
     */
//    public List<? extends ItemAndListCommonInterface> getList();
    public List<E> getList();

    /**
     * return the full, unsorted and unfiltered list of
     *
     * @return
     */
//    public List<? extends ItemAndListCommonInterface> getListFull();
    public List<E> getListFull();

    default public boolean containsFull(ItemAndListCommonInterface elt) {
        List elements = getListFull();
        return elements.contains(elt);
    }

    /**
     * sets the *full* list of items owned - MUST never be called with a
     * (filtered) list retrieved via getList() since that would effectively
     * remove all filtered elements definitively!
     *
     * @param listOfSubObjects
     */
//    public void setList(List<? extends ItemAndListCommonInterface> listOfSubObjects);
    public void setList(List<E> listOfSubObjects);

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
//    public WorkTimeAllocator getWorkTimeAllocatorN(boolean reset);
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
//    default public WorkTimeAllocator getWorkTimeAllocatorN() {
//        return getWorkTimeAllocatorN(false);
//    }
    public WorkTimeAllocator getWorkTimeAllocatorN();

    /**
     *
     * @param elt
     * @return
     */
//    public WorkTimeAllocator getWorkTimeAllocator();
    public void setWorkTimeAllocator(WorkTimeAllocator workTimeAllocator);

//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices allocateNew(ItemAndListCommonInterface elt);
//    default public WorkTimeSlices allocate2(ItemAndListCommonInterface elt) {
//        List<ItemAndListCommonInterface> potentialProviders = null;
//        WorkSlotList workSlotList;
//        WorkTimeSlices workTimeSlices = null;
//        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocator();
//        //if no workTimeAllocator already there (cached), if there are workSlots or owners with workTime get as much as needed or available
//        if (workTimeAllocator == null && ((workSlotList = getWorkSlotListN(false)) != null || (potentialProviders = getPotentialWorkTimeProvidersInPrioOrder()) != null)) {
//            long remaining = getWorkTimeRequiredFromProvider(this); //calculate how much time is needed from this' subtasks
////            workTimeAllocator = new WorkTimeAllocator(null, null);
//            if (workSlotList != null) {
//                workTimeAllocator = new WorkTimeAllocator(workTimeSlices, this); //first add own workTime
//            }
//            long availWorktime = workTimeAllocator.getAvailableTime();
//            if (remaining > availWorktime) { //if need additional workTime
//                remaining -= availWorktime; //if need additional workTime
//
////          List<ItemAndListCommonInterface> potentialProviders = getPotentialWorkTimeProvidersInPrioOrder();
//                for (ItemAndListCommonInterface prov : potentialProviders) {
//                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////<editor-fold defaultstate="collapsed" desc="comment">
////                    WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(reset);
////                    if (workTimeAllocator != null) {
////                        WorkTimeSlices allocatedWorkTime = workTimeAllocator.getAllocatedWorkTime(this, remaining);
////                        if (allocatedWorkTime != null) {
////                            if (workTimeSlices == null) {
////                                workTimeSlices = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
////                            }
////                            workTimeSlices.addWorkTime(allocatedWorkTime);
////                            workTimeAllocator.addWorkTimeSlices(prov.allocateNew(this));
////                            remaining = allocatedWorkTime.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
////                            if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
////                                break;
////                            }
////                        }
////                    }
////</editor-fold>
//                    workTimeAllocator.addWorkTimeSlices(prov.allocate2(this));
//                    remaining = workTimeAllocator.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//        setWorkTimeAllocator(workTimeAllocator); //set the updated version
//        //if we managed to allocate some worktime above
//        if (workTimeAllocator != null) {
//            return workTimeAllocator.getAllocatedWorkTimeNew(elt);
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
    /**
     * reset/refresh the WorkTimeDefinition, must be called whenever work slots
     * or items have been changed.
     */
    public void resetWorkTimeDefinition();

    /**
     *
     * @return
     */
    public String getObjectIdP();

    /**
     *
     * @return
     */
//    public String getClassName();
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
    default public List<ItemAndListCommonInterface> getOtherPotentialWorkTimeProvidersInPrioOrderN() {
//        WorkSlotList workSlots = getWorkSlotListN();
//        if (workSlots == null || !workSlots.hasComingWorkSlots()) {
//            return null;
//        } else {
//            List<ItemAndListCommonInterface> providers = new ArrayList();
//            providers.add(this); //only possible provider for an ItemList or Category is itself
//            return providers;
//        }
        return null; //return no other workTime providers for an ItemList/Category since its own workTime is already allocated directly when creating the WorkTimeAllocator
    }

    /**
     * returns true if has future workTime associated with it. either from own
     * workSlots or allocated WorkTimeSlices.
     *
     * @return
     */
    default public boolean mayProvideWorkTime() {
        WorkSlotList workSlots = getWorkSlotListN();
        if (workSlots != null && workSlots.hasComingWorkSlots()) {
            return true;
        }

        ItemAndListCommonInterface owner = getOwner();
        if (owner != null && owner.mayProvideWorkTime()) { //NB - yes, do iterate up the hierarchy since any owner (e.g. project) at a higher level may recursively provide worktime
            return true;
        }
        return false;
    }
//<editor-fold defaultstate="collapsed" desc="comment">

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
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * time allocated to this item (from all workTimeProviders: own workslots,
     * categories, owner)
     *
     * @param elt
     * @return null if no WorkTimeSlices allocated
     */
//    default public WorkTimeSlices getAllocatedWorkTimeN() {
//        throw new Error("Not supported yet."); //should not be called for ItemLists and Categories (or WorkSlots)
//    }
//    public WorkTimeSlices getAllocatedWorkTimeN();
//    public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface elt);
//</editor-fold>
    /**
     * return the WorkTimeSlices allocated to elt
     *
     * @param elt
     * @return
     */
//    default public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface elt) {
    default public WorkTimeSlices getAllocatedWorkTimeN(Item elt) {
        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocatorN();
        if (workTimeAllocator != null) {
            return workTimeAllocator.getAllocatedWorkTimeN(elt);
        } else {
            return null;
        }
    }

    /**
     * return the element's own allocated WorkTimeSlices. May come from own
     * worktime, from owner's worktime or from categories... (therefore simplest
     * to always use a WorkTimeAllocator
     *
     * @return
     */
    default public WorkTimeSlices getAllocatedWorkTimeN() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null) {
//            return owner.getAllocatedWorkTimeN(this);
//        } else {
//            return null;
//        }
//</editor-fold>
//            return getAllocatedWorkTimeN(this);
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemAndListCommonInterface owner = getOwner();
//if (owner != null)
//        if (isProject()) {
//
//        } else
//            return owner.getAllocatedWorkTimeN(this);
//</editor-fold>
    }
//<editor-fold defaultstate="collapsed" desc="comment">
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
//    default public WorkTimeSlices allocateWorkTimeXXX(ItemAndListCommonInterface itemOrList) {
////        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(itemOrList);
//        WorkTimeAllocator wt = getWorkTimeAllocatorN();
//        return wt != null ? wt.allocateWorkTimeXXX(itemOrList) : null;
//    }
    /**
     * allocate workTime from this element's WorkTimeDefinition to itemOrList
     * with duration remainingDuration.
     *
     * @param itemOrList
     * @param remainingDuration
     * @return
     */
//    default public WorkTimeSlices allocateWorkTime(ItemAndListCommonInterface itemOrList, long remainingDuration) {
//    default public WorkTimeSlices allocateWorkTime(Item itemOrList, long remainingDuration) {
////        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(itemOrList, remainingDuration);
//        WorkTimeAllocator wt = getWorkTimeAllocatorN();
//        return wt != null ? wt.getAllocatedWorkTime(itemOrList, remainingDuration) : null;
//    }
//</editor-fold>
    /**
     * returns the finish time of item based on the owner's work time
     *
     * @param item
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    default public Date getFinishTimeXXX(ItemAndListCommonInterface item) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
////            return new Date(workTimeDef.getFinishTime(item));
//            return workTimeDef.getAllocatedWorkTimeN(item).getFinishTimeD();
//        } else {
//            return new Date(0);
//        }
//    }
//    default public long getAllocatedWorkTimeN() {
//        return getAllocatedWorkTimeN().getFinishTime();
//    }
//</editor-fold>
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
        return new MyDate(getFinishTime());
    }

    /**
     * returns the calculated finishTime for this item or MyDate.MAX_DATE if
     * none
     *
     * @return finishTime or MyDate.MAX_DATE if no workTime was
     * available/allocated, or if or insufficient workTime to finish the task
     * was allocated
     */
    default public long getFinishTime() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        long finishTime = MyDate.MAX_DATE;
//        assert false:"getFinishTime on ItemListCommonInterface should never be called";
//        return getFinishTime(System.currentTimeMillis());
//    }
//
//    default public long getFinishTime(long now) {
//        return getAllocatedWorkTimeN().getFinishTime();
//        if (true) {
//            return allocateNew(this).getFinishTime();
//        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocatorN();
//        WorkTimeSlices workTimeSlices;
//        if (workTimeAllocator != null && ((workTimeSlices = workTimeAllocator.getAllocatedWorkTimeN(this)) != null))
//            return workTimeSlices.getFinishTime();
//        else
//            return MyDate.MAX_DATE;
//</editor-fold>
        WorkTimeSlices workTimeSlices = getAllocatedWorkTimeN();
        if (workTimeSlices != null) {
            return workTimeSlices.getFinishTime();
        } else {
            return MyDate.MAX_DATE;
        }

//        } 
//<editor-fold defaultstate="collapsed" desc="comment">
//        else {
//            WorkTimeSlices workTime = getAllocatedWorkTimeN();
////        long finishTime = MyDate.MAX_DATE;
//            if (workTime != null) {
//                if (Config.WORKTIME_DETAILED_LOG) {
//                    Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (workTime != null ? workTime.toString() : "<null>") + ", returning=" + new Date(workTime.getFinishTime()));
//                }
//                if (workTime.getRemainingDuration() == 0) {
////                return workTime.getFinishTime();
//                    finishTime = workTime.getFinishTime();
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////            else {
////                return MyDate.MAX_DATE;
////            }
////        } else {
////            return MyDate.MAX_DATE; //cannot allocate enough time
////</editor-fold>
//            }
//            if (false && Config.WORKTIME_TEST) {
//                long remainingEffort = getRemaining();
//                long allocated = workTime.getAllocatedDuration();
//                assert workTime == null || !(allocated > remainingEffort) : "allocated too much time";
//            }
////        return MyDate.MAX_DATE; // returning MAX, means cannot allocate enough time
//            return finishTime; //cannot allocate enough time
//        }
//</editor-fold>
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    default public long getFinishTimeOLD2() {
////        return getAllocatedWorkTimeN().getFinishTime();
//        WorkTimeSlices wt = getAllocatedWorkTimeN();
////        long finishTime = MyDate.MAX_DATE;
//        if (wt != null) {
//            long finishTime = wt.getFinishTime();
//            if (Config.WORKTIME_DETAILED_LOG) {
//                Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (wt != null ? wt.toString() : "<null>") + ", returning=" + new Date(finishTime));
//            }
//
////        return wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
////        ASSERT.that(wt.getAllocatedDuration() == getRemainingEffort() || wt.getAllocatedDuration() < getRemainingEffort(), "allocated too much time");
////        if (wt != null) {
//            long remainingEffort = getRemaining();
//            long allocatedDuration = wt.getAllocatedDuration();
//            if (Config.WORKTIME_TEST) {
//                ASSERT.that(wt == null || !(allocatedDuration > remainingEffort), "allocated too much time");
//            }
////        return wt != null &&  allocatedDuration>= remainingEffort ? finishTime : MyDate.MAX_DATE;
//            if (allocatedDuration >= remainingEffort) {
//                return finishTime;
//            } else {
//                return MyDate.MAX_DATE;
//            }
//        } else {
//            return MyDate.MAX_DATE; //cannot allocate enough time
//        }
//    }
//
//    default public long getFinishTimeOLD() {
////        return getAllocatedWorkTimeN().getFinishTime();
//        WorkTimeSlices wt = getAllocatedWorkTimeN();
//        long finishTime = wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p("ItemAndListCI \"" + this + "\".getFinishTime(), workTime=" + (wt != null ? wt.toString() : "<null>") + ", returning=" + new Date(finishTime));
//        }
//
////        return wt != null ? wt.getFinishTime() : MyDate.MAX_DATE;
////        ASSERT.that(wt.getAllocatedDuration() == getRemainingEffort() || wt.getAllocatedDuration() < getRemainingEffort(), "allocated too much time");
//        if (wt != null) {
//            long remainingEffort = getRemaining();
//            long allocatedDuration = wt.getAllocatedDuration();
//            if (Config.WORKTIME_TEST) {
//                assert wt == null || !(allocatedDuration > remainingEffort) : "allocated too much time";
//            }
////        return wt != null &&  allocatedDuration>= remainingEffort ? finishTime : MyDate.MAX_DATE;
//            return allocatedDuration >= remainingEffort ? finishTime : MyDate.MAX_DATE;
//        } else {
//            return MyDate.MAX_DATE; //cannot allocate enough time
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * called to indicate the workTime needs to be udpated/refreshed. E.g. by an
     * Item if status or remaining time changes, or by a workslot if duration or
     * startTime change. This assume that work time calculations are cached. It
     * should invalidate as few items as possible, e.g. typically only items
     * that come later than the changed one.
     */
//    public void refreshWorkTime();
    //TODO!!!!! must store separately for cache!! Same as
//</editor-fold>
    /**
     * returns how much workTime this element requires from the provider. If the
     * element has other higher prioritized providers they are asked how much
     * they can provide and only the remaining is returned for this provider.
     *
     * @param provider
     * @return
     */
    default public long getWorkTimeRequiredFromProvider(ItemAndListCommonInterface provider) {
//        throw new Error("Not supported yet."); //should never be called for ItemLists/Categories?!
//    }
//    default public long getWorkTimeRequiredFromOwner() {
//        return getRemainingEffort(); //for lists and categories, we use the standard remaining, for Items it's a special impl
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

//    public void forceCalculationOfWorkTime();
//        @Override
    /**
     * force the calculation of worktime for every subtask - is this really
     * necessary??
     */
    default public void forceCalculationOfWorkTimeXXX() {
        List<? extends ItemAndListCommonInterface> subtasks = getList();
        if (subtasks != null) {
            int size = subtasks.size();
            for (int i = size; i < size && i >= 0; i--) {
                ItemAndListCommonInterface elt = subtasks.get(i);
                if (!elt.isDone()) { //for the last unDone element
                    elt.getFinishTime(); //getFinishTime to force calculation of  workTime for all tasks
                    return;
                }
            }
        }
    }

    
    default boolean isUseDefaultFilter() {
        return true;
    }

    default void setUseDefaultFilter(boolean useDefaultFilter) {
        
    }
    /**
     *
     * @return
     */
    default public FilterSortDef getDefaultFilterSortDef() {
//            new FilterSortDef(Item.PARSE_DUE_DATE, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, true)
//        FilterSortDef hardcodedFilter = new FilterSortDef(null, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false); //no sorting, //TODO!! Move this filter to FilterSortDef.getDeafultFilter() and reuse everywhere
        FilterSortDef hardcodedFilter = FilterSortDef.getDefaultFilter();
        return hardcodedFilter;
    }

    public void setFilterSortDef(FilterSortDef filterSortDef);

    /**
     * return a defined filter, null if none defined, for Items, returns a
     * filter for the subtasks. Will reuse a filter defined by the owner of an
     * item (either its project or list), or the default filter defined at the
     * highest level.
     *
     * @return
     */
    public FilterSortDef getFilterSortDefN();

    /**
     * returns a default filter if active and one is defined
     * @return 
     */
    default public FilterSortDef getFilterSortDef() {
        return getFilterSortDef(isUseDefaultFilter());
    }

    default public FilterSortDef getFilterSortDef(boolean returnDefaultFilterIfNoneDefined) {
        FilterSortDef filterSortDef = getFilterSortDefN();
        if (filterSortDef == null && returnDefaultFilterIfNoneDefined) {
            return FilterSortDef.getDefaultFilter();
        }
        return filterSortDef;
    }
    
    default public Iterator iterator() {
        return new Iterator() {
//            public class MyIterator <T> implements Iterator<T> {
            int index = 0;

            public boolean hasNext() {
                return index < getSize();
            }

            public ItemAndListCommonInterface next() {
                return (ItemAndListCommonInterface) getList().get(index++); //getList() to only return visible tasks
            }

            public void remove() {
                //implement... if supported.
                throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * for Items: return subtask
     *
     * @param index
     * @return
     */
    public Object get(int index);

    public void setSoftDeletedDate(Date deletedDate);

    public Date getSoftDeletedDateN();

    default public boolean isSoftDeleted() {
        return getSoftDeletedDateN() != null;
    }

    /**
     *
     * @param removeReferences if true, will remove references in the element
     * (to eg owner, categories, repeatRules ...) before soft-deleting it. Not
     * sure this is really useful?!
     * @return true if delete succeeded
     */
//    public boolean delete(boolean removeReferences,  boolean hardDelete);
    /**
     * deletes this instance
     */
//    public void delete() throws ParseException;
//    default public boolean softDelete() {
//        return delete(false,false);
//    }
    /**
     * 'hard' delete eg repeatInstances with no purpose or reason to keep
     *
     * @return
     */
//    default public boolean hardDelete() {
////        boolean delOK = softDelete(true,true);
////        DAO.getInstance().deleteInBackground((ParseObject) this);
////        return delOK;
//        return delete(true,true);
//    }
    /**
     * prepare for deletion (remove references to the element etc) but don't
     * actually delete it in Parse server
     *
     * @param deletedDate
     * @return
     */
    public boolean deletePrepare(Date deletedDate);

    default void checkOwners(List<ItemAndListCommonInterface> list) {
        if (list != null) {
            for (ItemAndListCommonInterface elt : list) {
                if (elt instanceof Item) {
                    Item item = (Item) elt;
                    ItemAndListCommonInterface owner = elt.getOwner();
                    ASSERT.that(owner == this
                            || owner == null
                            || this.isNoSave(),
                            () -> ("ERROR in owner: Item="
                            + (item.toString(false)
                            + (owner instanceof ItemList
                                    ? (", item.owner=" + ((ItemList) owner).toString(false))
                                    : (", item.owner=" + ((Item) owner).toString(false)))
                            + (this instanceof ItemList
                                    ? (", but in list=" + ((ItemList) this).toString(false))
                                    : (", but in list=" + ((Item) this).toString(false))))));
//                        + ", should be=" + this.toString(false))));
                } else if (elt instanceof WorkSlot) {
                    WorkSlot workSlot = (WorkSlot) elt;
                    ItemAndListCommonInterface owner = elt.getOwner();
                    ASSERT.that(owner == this
                            || owner == null
                            || this.isNoSave(),
                            () -> ("ERROR in owner: WorkSlot="
                            + (workSlot.toString()
                            + (owner instanceof ItemList
                                    ? (", item.owner=" + ((ItemList) owner).toString(false))
                                    : (", item.owner=" + ((Item) owner).toString(false)))
                            + (this instanceof ItemList
                                    ? (", but in list=" + ((ItemList) this).toString(false))
                                    : (", but in list=" + ((Item) this).toString(false))))));
                }
            }
        }
    }

    /**
     * returns true if inheritance the value is inherited from it's owner
     * (returns false if the value could be inherited but owner does not define
     * any value)
     *
     * @return
     */
    default public boolean isInherited(Object ownValue, Object potentiallyInheritedValue, boolean inheritanceEnabledForField) {
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()
        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && inheritanceEnabledForField
                //                && Objects.equals(ownValue, potentiallyInheritedValue);
                && eql(ownValue, potentiallyInheritedValue);
    }

    default public String hasReferencesToUnsavedParseObjects() {
        return "";
    }

    /**
     * update at the very last moment, just before saving, so that all other
     * changes to an element have been effectuated
     */
    default public void updateBeforeSave() {
//        assert false; //Do nothing unless specified by specialized object
    }

    /**
     * return true if subtask is a (first-level) task within this list/project
     * (it doesn't not go through entire project tree, only first level directly
     * within list/project)
     *
     * @param subtask
     * @return
     */
    default public boolean isSubtaskTo(Item subtask) {
        return getListFull().contains(subtask);
    }

    default public void clear() {
        setList(null);
    }

    default public boolean addAll(Collection<? extends E> c) {
        boolean changed = false;
        for (E e : c) {
            if (addToList(e)) {
                changed = true;
            }
        }
        return changed;
    }

    static public List<String> convListToObjectIdList(List<ItemAndListCommonInterface> eltList) {
        List<String> eltIds = new ArrayList();
        if (eltList != null) {
            for (ItemAndListCommonInterface e : eltList) {
                eltIds.add(e.getObjectIdP());
            }
        }
        return eltIds;
    }
/**
 * add the objectId of elements in eltsToAddList to the list objIdStringList of ObjectIds (Strings) if they are not already there 
 * @param objIdStringList
 * @param eltsToAddList
 * @return 
 */
    static public List<String> addListToObjectIdList(List<String> objIdStringList, List<ItemAndListCommonInterface> eltsToAddList) {
//        List<String> objIdStringList = new ArrayList();
        if (eltsToAddList != null) {
            for (ItemAndListCommonInterface e : eltsToAddList) {
                if (!objIdStringList.contains(e.getObjectIdP())) {
                    objIdStringList.add(e.getObjectIdP());
                }
            }
        }
        return objIdStringList;
    }

    default public List<ItemAndListCommonInterface> getAdded(List newList, List oldList) {
        List<ItemAndListCommonInterface> added = new ArrayList(newList);//make a copy of the edited list 
        added.removeAll(oldList); //remove all that were already set of the item to get only the newly added categories
        return added;
    }

}
