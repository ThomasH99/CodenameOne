/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Thomas
 */
public class MultipleSelection {

    interface ItemOperation {

        void execute(Item item);
    }

//    ItemOperation setStatus = (item) -> item.setStatus(ItemStatus.CREATED);
//    ItemOperation setDueDate = (item) -> item.setStatus(ItemStatus.CREATED);

    static ItemOperation setStatus(ItemStatus itemStatus) {
        return (item) -> item.setStatus(itemStatus);
    }

    static ItemOperation setDueDate(Date dueDate) {
        return (item) -> item.setDueDate(dueDate);
    }

    static ItemOperation setStarred(boolean starred) {
        return (item) -> item.setStarred(starred);
    }

    static ItemOperation moveTo(Item newProject) {
        return (item) -> {
            item.removeFromOwner();
            List list = newProject.getList();
            if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
                list.add(0, item);
            } else {
                list.add(item);
            }
            newProject.setList(list);
        };
    }

    static ItemOperation moveTo(ItemList newItemList) {
        return (item) -> {
            item.removeFromOwner();
            List list = newItemList.getList();
            if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
                list.add(0, item);
            } else {
                list.add(item);
            }
            newItemList.setList(list);
        };
    }

    static ItemOperation moveToTopOfList(ItemList newItemList) {
        return (item) -> {
//            item.removeFromOwner();
            List list = newItemList.getList();
            list.remove(item);
            list.add(0, item);
            newItemList.setList(list);
        };
    }

    static ItemOperation delete() {
        return (item) -> {
//            item.delete();
//            DAO.getInstance().delete(item);
            item.delete();
        };
    }

    /**
     * will update the item with any set/defined value in itemWithValuesToSet.
     * Will add item to selected categories. Will add a defined comment to the
     * existing comments. Will only update boolean values (Starred, Interrupt)
     * if they are set (so cannot unset these values).
     *
     * @param itemWithValuesToSet
     * @return
     */
    static ItemOperation setAnything(Item itemWithValuesToSet) {
        return (item) -> {
            Item itm = itemWithValuesToSet;
            if (itm.getAlarmDate() != 0) {
                item.setAlarmDate(itm.getAlarmDate());
            }
            if (itm.getComment() != null && !itm.getComment().equals("")) {
                item.setComment(Item.addToCommentDefaultPosition(item.getComment(), itm.getComment()));
            }
            if (itm.isStarred()) {
                item.setStarred(true); //TODO only works when setting, not unsetting
            }
            if (itm.getCategories() != null && itm.getCategories().size() > 0) {
                List<Category> prevCategories = item.getCategories();
                for (Category cat : itm.getCategories()) {
                    prevCategories.add(cat);
                    cat.addItemAtIndex(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : cat.getSize());
                    DAO.getInstance().save(cat);
                }
                item.setCategories(prevCategories);
            }
            if (itm.getEffortEstimate() != 0) {
                item.setEffortEstimate(itm.getEffortEstimate());
            }
            if (itm.getRemainingEffort() != 0) {
                item.setRemainingEffort(itm.getRemainingEffort());
            }
            if (itm.getActualEffort() != 0) {
                item.setActualEffort(itm.getActualEffort());
            }
            if (itm.getHideUntilDateD().getTime() != 0) {
                item.setHideUntilDate(itm.getHideUntilDateD());
            }
            if (itm.getStartByDateD().getTime() != 0) {
                item.setStartByDate(itm.getStartByDateD().getTime());
            }
            if (itm.getPriority() != 0) {
                item.setPriority(itm.getPriority());
            }
            if (itm.getImportanceN() != null) {
                item.setImportance(itm.getImportanceN());
            }
            if (itm.getUrgencyN() != null) {
                item.setUrgency(itm.getUrgencyN());
            }
            if (itm.getChallenge() != null) {
                item.setChallenge(itm.getChallenge());
            }
            if (itm.getDreadFunValueN() != null) {
                item.setDreadFunValue(itm.getDreadFunValueN());
            }
            if (itm.getEarnedValue() != 0) {
                item.setEarnedValue(itm.getEarnedValue());
            }
            if (itm.getStartedOnDate() != 0) {
                item.setStartedOnDate(itm.getStartedOnDate());
            }
            if (itm.getCompletedDate() != 0) {
                item.setCompletedDate(itm.getCompletedDate(), true);
            }
            if (itm.isInteruptOrInstantTask()) {
                item.setInteruptOrInstantTask(true); //TODO only works when setting, not unsetting
            }
        };
    }

    /**
     * executes all the operations on the items and saves each item afterwards
     * (whether effectively changed or not)
     *
     * @param items
     * @param operations
     */
    static void performOnAll(List<Item> items, ItemOperation operation) {
        for (Item item : items) {
//        for (int i=0, size=items.size(); i<size;i++) {
//            Item item=items.get(i);
            operation.execute(item);
//            DAO.getInstance().save(item);
        }
    }

    static void performMultipleOperationsOnAll(List<Item> items, List<ItemOperation> operations) {
//        for (Item item : items) {
        for (int i=0, size=items.size(); i<size;i++) {
            Item item=items.get(i);
            for (ItemOperation operation : operations) {
                operation.execute(item);
            }
            DAO.getInstance().save(item);
        }
    }

}
