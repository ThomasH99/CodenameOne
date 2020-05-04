/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.parse4cn1.ParseObject;
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

    static ItemOperation moveTo(ItemAndListCommonInterface newProject) {
        return (item) -> {
            item.removeFromOwner();
//<editor-fold defaultstate="collapsed" desc="comment">
//            List list = newProject.getList();
//            if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
//                list.add(0, item);
//            } else {
//                list.add(item);
//            }
//</editor-fold>
            newProject.addToList(item, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
//            newProject.setList(list);
        };
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static ItemOperation moveTo(ItemList newItemList) {
//        return (item) -> {
//            item.removeFromOwner();
//            List list = newItemList.getList();
//            if (MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists)) {
//                list.add(0, item);
//            } else {
//                list.add(item);
//            }
//            newItemList.setList(list);
//        };
//    }
//</editor-fold>
//    static ItemOperation moveToTopOfList(ItemList newItemList) {
    static ItemOperation moveToTopOfList(ItemAndListCommonInterface newItemList) {
        return (item) -> {
//            item.removeFromOwner();
//            List list = newItemList.getList();
//            list.remove(item);
//            list.add(0, item);
//            newItemList.setList(list);
            newItemList.addToList(item, false);
        };
    }

    static ItemOperation delete() {
        return (item) -> {
//            item.delete();
//            DAO.getInstance().delete(item);
//            item.softDelete();
            DAO.getInstance().delete(item, true, true);
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
        //TODO!!! replace this by CopyInto?!
        return (item) -> {
            Item ref = itemWithValuesToSet;
            if (ref.getAlarmDate().getTime() != 0) {
                item.setAlarmDate(new MyDate(ref.getAlarmDate().getTime()));
            }
            if (ref.getComment() != null && !ref.getComment().equals("")) {
                item.setComment(Item.addToCommentDefaultPosition(item.getComment(), ref.getComment()));
            }
            if (ref.isStarred()) {
                item.setStarred(true); //TODO only works when setting, not unsetting
            }
            if (ref.getCategories() != null && ref.getCategories().size() > 0) {
                List<Category> prevCategories = item.getCategories();
                for (Category cat : ref.getCategories()) {
                    ASSERT.that(!cat.contains(ref)); //the ref should normally(?!) be a temporary item
                    if (!prevCategories.contains(cat)) {
                        prevCategories.add(cat);
//                        if (false) {
//                            cat.addItemAtIndex(item, MyPrefs.insertNewCategoriesForItemsInStartOfIList.getBoolean() ? 0 : cat.getSize());
//                        }
                        cat.addItemToCategory(item, true);
                        ASSERT.that(item.getObjectIdP() != null); //otherwise the save of cat below will fail
                        DAO.getInstance().saveNew((ParseObject) cat,false); //no trigger of save since categories will be saved when item is saved
                    }
                }
                item.setCategories(prevCategories);
            }
            if (ref.getEstimate() != 0) {
                item.setEstimate(ref.getEstimate());
            }
            if (ref.getRemaining() != 0) {
                item.setRemaining(ref.getRemaining());
            }
            if (ref.getActual() != 0) {
                item.setActual(ref.getActual(), false);
            }
            if (ref.getHideUntilDateD().getTime() != 0) {
                item.setHideUntilDate(ref.getHideUntilDateD());
            }
            if (ref.getStartByDateD().getTime() != 0) {
                item.setStartByDate(ref.getStartByDateD().getTime());
            }
            if (ref.getPriority() != 0) {
                item.setPriority(ref.getPriority());
            }
            if (ref.getImportanceN() != null) {
                item.setImportance(ref.getImportanceN());
            }
            if (ref.getUrgencyN() != null) {
                item.setUrgency(ref.getUrgencyN());
            }
            if (ref.getChallengeN() != null) {
                item.setChallenge(ref.getChallengeN());
            }
            if (ref.getDreadFunValueN() != null) {
                item.setDreadFunValue(ref.getDreadFunValueN());
            }
            if (ref.getEarnedValue() != 0) {
                item.setEarnedValue(ref.getEarnedValue());
            }
            if (ref.getStartedOnDate() != 0) {
                item.setStartedOnDate(ref.getStartedOnDateD());
            }
            if (ref.getCompletedDateD().getTime() != 0) {
//                item.setCompletedDate(ref.getCompletedDateD(), true); //true=>update status, e.g. in case ONLY completed date is set
                item.setCompletedDate(ref.getCompletedDateD(), true, true); //true=>update status, e.g. in case ONLY completed date is set, true=>force this date instead of latest subtask date
            }
            if (ref.isInteruptOrInstantTask()) {
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
//    static void performOnAll(List<Item> items, ItemOperation operation) {
//    static void performOnAll(ListSelector<Item> items, ItemOperation operation) {
    static void performOnAllAndSave(List<Item> items, ItemOperation operation) {
        for (Item item : items) {
//        for (int i=0, size=items.size(); i<size;i++) {
//            Item item=items.get(i);
            operation.execute(item);
//            DAO.getInstance().save(item);
            DAO.getInstance().saveNew(item, false); //don't save until all are updated
        }
        DAO.getInstance().triggerParseUpdate();
    }

//    static void performMultipleOperationsOnAll(ListSelector<Item> items, List<ItemOperation> operations) {
    static void performMultipleOperationsOnAllAndSave(List<Item> items, List<ItemOperation> operations) {
//        for (Item item : items) {
//        for (int i = 0, size = items.size(); i < size; i++) {
//            Item item = items.get(i);
        for (Item item : items) {
            for (ItemOperation operation : operations) {
                operation.execute(item);
            }
            DAO.getInstance().saveNew(item, false); //don't save until all are updated. appropriate here (or triggered where this method is called)
        }
        DAO.getInstance().triggerParseUpdate();
    }

}
