/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.compat.java.util.Objects;
import com.codename1.io.Log;
import com.codename1.ui.Dialog;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Some principles: the *owner* checks its elements (e.g. TemplateList or
 * itemLists or projects), including that they point back to them as owners.
 *
 * Dangling elements are checked directly from parse.
 *
 * Elements check their 'owned' references, so e.g. A repeatRule will check that
 * all the done/undoe items refer back to it. An Item will check that it's
 * repeat rule exists.
 *
 * RepeatRules and FilterSoftDefs are checked
 *
 * A certain order needs to be respected: 1) First check that an Item's
 * repeatRule exists and if not, set it to null, and that the RR refers back to
 * it (only way to find such a missing ref), then 2) check that RepeatRules'
 * done/undone refer to them as RR. Check that TemplateList's elements are all
 * templates, then check that other items are NOT templates.
 *
 * @author thomashjelm
 */
public class CleanUpDataInconsistencies {

    private static DAO dao;// = DAO.getInstance();
    private static CleanUpDataInconsistencies INSTANCE;
    private Set<FilterSortDef> filterSortDefReferenced = new HashSet();
    //////////////////////////   CLEAN UP    /////////////////////////////
    //TODO!!!! a subtask may be in the project's subtask list, but have a different owner (e.g. a List) --> fix: if a project has a subtask with another owner, make the project the subtask's owner
//    private boolean badReference(ItemAndListCommonInterface parseObject) {
//        return badReference((ParseObject)parseObject);
//    }

//    CleanUpDataInconsistencies(DAO dao) {
//        this.dao = dao;
//    }
    CleanUpDataInconsistencies() {
    }

    public static CleanUpDataInconsistencies getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CleanUpDataInconsistencies();
            dao = DAO.getInstance();
        }
        return INSTANCE;
    }

    private String logPrefix = "";

    private void log(String s, int logLevel) {
        Log.p("CLEANUP/" + logPrefix + ": " + s, logLevel);
    }

    private void logAction(String s) {
        if (loggingOn) {
            Log.p("CLEANUP/" + logPrefix + "/DONE: " + s, logLevel);
        } else {
            Log.p("CLEANUP/" + logPrefix + "/TODO: " + s, logLevel);
        }
    }

    private void log(String s) {
        log(s, logLevel);
    }

    private void setLogPrefix(String prefix) {
        logPrefix = prefix;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean executeCleanup() {
//        return loggingOn;
//    }
    
//    private boolean executeCleanup(String action) {
//        return loggingOn;
//    }
    
//    private void logError(Object list, ParseObject element) {
//        if (list instanceof ItemAndListCommonInterface) {
////                    Log.p("Bad object ref in class "+list.getClass()+" \"" + ((ItemAndListCommonInterface) list).getText() + "\" to objectId=" + ((ParseObject) list).getObjectId());
//            log("Bad ref in \"" + ((ItemAndListCommonInterface) list).getText() + "\" (objectId=" + ((ParseObject) list).getObjectIdP() + ") to " + element + " objId=" + element.getObjectIdP(), logLevel);
//        } else if (list instanceof ParseObject) {
////            Log.p("Bad object ref in " + list + " to objectId=" + ((ParseObject) list).getObjectId());
//            log("Bad ref in " + list + " (objectId=" + ((ParseObject) list).getObjectIdP() + " to " + element + " objId=" + element.getObjectIdP(), logLevel);
//        }
//    }
    
//    private List removeDuplicatesXXX(List list) {
//        List noDups = new ArrayList();
//        for (Object obj : list) {
//            if (!noDups.contains(obj)) {
//                noDups.add(obj);
//            } else {
//                Log.p("duplicate element=" + obj);
//            }
//        }
//        return noDups;
//    }
//</editor-fold>

    /**
     * returns true if trying to fetch the parseObject from Parse fails (meaning
     * the object does not exists on the server)
     *
     * @param parseObject
     * @return
     */
    private boolean notOnParseServer(ParseObject parseObject) {
        if (parseObject == null) {
            return true; //ensures null pointers are deleted from lists
        }
        ASSERT.that(parseObject.getObjectIdP() != null, "getObjectId==null");
        ASSERT.that(parseObject.getObjectIdP().length() != 0, "getObjectId empty");
        try {
            parseObject.fetchIfNeeded();
        } catch (ParseException ex) {
            return true;
        }
        return false;
    }

    /**
     * check that element refers to CorrectOwner as owner
     *
     * @param element
     * @param correctOwner
     * @param executeCleanup
     * @return
     */
    private boolean checkOwner(ItemAndListCommonInterface element, ItemAndListCommonInterface correctOwner, boolean executeCleanup) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (element.getOwner() == null) {
//                    log(getClassOfElement(correctOwner) + " has a list element with NULL Owner. CorrectOwner= \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
////                log(getClassOfElement(element)+" \"" + element + "\" in list has NULL owner, element= \"" + element + "\" correctOwner="+correctOwner+ "\" list="+list);
//                    logAction("Set owner to " + correctOwner);
//                    if (executeCleanup) {
//                        element.setOwner(correctOwner);
//                        dao.saveNew((ParseObject) element, false);
//                    }
//                } else if (!element.getOwner().equals(correctOwner)) {
////                log(getClassOfElement(element) + " \"" + element + "\" in list does not have list as owner, element= \"" + element + "\" list=null", logLevel);
//                    log(getClassOfElement(correctOwner) + " has a list element with WRONG Owner. Wrong owner= \"" + element.getOwner() + "\", CorrectOwner= \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
//                    logAction("Set owner to " + correctOwner);
//                    if (executeCleanup) {
//                        element.setOwner(correctOwner);
//                        dao.saveNew((ParseObject) element, false);
//                    }
//                }
//</editor-fold>
        if (!Objects.equals(element.getOwner(), correctOwner)) {
            log(getClassOfElement(correctOwner) + " has a list element with incorrect Owner. "
                    + "\" Element= \"" + element + "\""
                    + "; Wrong owner=(" + (element.getOwner() != null ? element.getOwner() : "NULL") + ")"
                    + "; CorrectOwner= \"" + correctOwner + "\"");
//                    + "; list=" + correctOwner.get);
//                log(getClassOfElement(element)+" \"" + element + "\" in list has NULL owner, element= \"" + element + "\" correctOwner="+correctOwner+ "\" list="+list);
            logAction("Set owner to " + correctOwner);
            if (executeCleanup) {
                element.setOwner(correctOwner);
                dao.saveNew((ParseObject) element, false);
                return true;
            }
        }
        return false;
    }

    private boolean loggingOn = false;
    private int logLevel = Log.ERROR; //use Log.ERROR to ensure the log is always done

    //SHARED
    /**
     *
     * @param description
     * @param list
     */
    private void cleanUpCircularReferencesInHierarchyXXX(String description, List list) {

    }

    private String getClassOfElement(ItemAndListCommonInterface element) {
        if (element instanceof Item) {
            return "Item";
        } else if (element instanceof ItemListList) {
            return "ItemListList";
        } else if (element instanceof TemplateList) {
            return "TemplateList";
        } else if (element instanceof ItemList) {
            return "ItemList";
        } else if (element instanceof Category) {
            return "Category";
        }
        return "TYPE?!";
    }

    /**
     * remove elements from the list which are not found on Parse and check they
     * all refer to the correct owner
     *
     * @param correctOwner
     * @param list
     * @param executeCleanup
     * @return true if the list was modified and should be saved by the owner
     */
    private boolean checkListForNotInParseSoftDeletedReferenceOwner(ItemAndListCommonInterface correctOwner, List<ItemAndListCommonInterface> list, boolean executeCleanup) {
        boolean listModified = false;
        int i = 0;
        while (i < list.size()) {
            ItemAndListCommonInterface element = list.get(i);

            if (notOnParseServer((ParseObject) element)) {
                log(getClassOfElement(element) + " not on parseServer, in list of " + getClassOfElement(correctOwner) + " \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
                logAction("Remove element from list");
                if (executeCleanup) {
                    list.remove(element);
                    listModified = true;
                    i--;
                }
            } else {
                if (element.isSoftDeleted()) { //shouldn't be in the owner's list
                    log(getClassOfElement(element) + " is soft-deleted but still in list of " + getClassOfElement(correctOwner) + " \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
                    logAction("Remove element from list");
                    if (executeCleanup) {
                        list.remove(element);
                        listModified = true;
                        i--;
                    }
                } else if (element.getOwner() == null) {
                    log(getClassOfElement(correctOwner) + " has a list element with NULL Owner. CorrectOwner= \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
//                log(getClassOfElement(element)+" \"" + element + "\" in list has NULL owner, element= \"" + element + "\" correctOwner="+correctOwner+ "\" list="+list);
                    logAction("Set owner to " + correctOwner);
                    if (executeCleanup) {
                        element.setOwner(correctOwner);
                        dao.saveNew((ParseObject) element, false);
                    }
                } else if (!element.getOwner().equals(correctOwner)) {
//                log(getClassOfElement(element) + " \"" + element + "\" in list does not have list as owner, element= \"" + element + "\" list=null", logLevel);
                    log(getClassOfElement(correctOwner) + " has a list element with WRONG Owner. Wrong owner= \"" + element.getOwner() + "\", CorrectOwner= \"" + correctOwner + "\", element= \"" + element + "\", list=" + list);
                    logAction("Set owner to " + correctOwner);
                    if (executeCleanup) {
                        element.setOwner(correctOwner);
                        dao.saveNew((ParseObject) element, false);
                    }
                }
            }
            i++;
        }
        return listModified;
    }

    private boolean checkListRemoveElementsNotInParseAndReferenceCategory(Category category, List<Item> list, boolean executeCleanup) {
        boolean listModified = false;
        int i = 0;
        while (i < list.size()) {
            Item item = list.get(i);

            if (notOnParseServer((ParseObject) item)) {
                log("Item not on parseServer, in list of Category \"" + category + "\", Item= \"" + item + "\", list=" + list);
                logAction("Remove element from list");
                if (executeCleanup) {
                    list.remove(item);
                    listModified = true;
                    i--;
                }
            } else {
                if (item.isSoftDeleted()) { //shouldn't be in the Category's list
                    log("Item is soft-deleted but still in Category \"" + category + "\", Item= \"" + item + "\", list=" + list);
                    logAction("Remove element from list");
                    if (executeCleanup) {
                        list.remove(item);
                        listModified = true;
                        i--;
                    }
                } else if (!item.getCategories().contains(category)) {
                    log("Category has an Item which doesn't reference it. Category= \"" + category + "\", Item= \"" + item + "\", list=" + list);
                    logAction("Adding Category to Item");
                    if (executeCleanup) {
                        item.addCategoryToItem(category, false);
                        dao.saveNew(item);
                    }
                }
            }
        }
        return listModified;
    }

    private boolean cleanUpDuplicatesInList(String description, List inputList, boolean executeCleanup) {
        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
        //other lists than ItemList
        ASSERT.that(!(inputList instanceof ItemList));
        ArrayList cleanList = new ArrayList();
        int size = inputList.size();
        for (int i = 0; i < size; i++) {
            Object elt = inputList.get(i);
            if (cleanList.contains(elt)) {
                log(description + " contains duplicate of \"" + elt + "\" at position " + i + " (list= " + inputList + ")");
                logAction("Remove duplicate element");
            } else {
                cleanList.add(elt);
            }
        }
        boolean deletes = inputList.size() != cleanList.size();
        if (executeCleanup) {
            inputList.clear();
            inputList.addAll(cleanList);
        }
        return deletes;
    }

    private boolean cleanUpReferencedFilterSortDef(ItemAndListCommonInterface filterOwner, FilterSortDef filterSortDef, boolean executeCleanup) {
//                FilterSortDef filterSortDef = itemList.getFilterSortDef();
        boolean filterDoesNotExist = false;
        if (filterSortDef != null) {
            if (notOnParseServer((ParseObject) filterSortDef)) {
                log(getClassOfElement(filterOwner) + "  \"" + filterOwner + "\" with bad ref to FilterSortDef= " + filterSortDef);
                logAction("Removing FilterSortDef");
                if (executeCleanup) {
                    filterOwner.setFilterSortDef(null); //remove reference to inexisting RepeatRule
                    filterDoesNotExist = true;
                }
            } else {
                filterSortDefReferenced.add(filterSortDef);
            }
        }
        return filterDoesNotExist;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void cleanUpDuplicatesInListNOTWORKING(String description, List list) {
//        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
//        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
//        for (Iterator iterator = list.iterator(); iterator.hasNext();) {
//            Object elt = iterator.next();
//            List sublist = list.subList(list.indexOf(elt) + 1, list.size());
//            for (Iterator iterator2 = sublist.iterator(); iterator2.hasNext();) {
//                if (iterator2.next().equals(elt)) {
//                    Log.p("CLEANUP: " + description + " - List " + list + " contains duplicate of " + elt + " at position " + (sublist.indexOf(elt) + list.indexOf(elt)), logLevel);
//                    if (executeCleanup) {
//                        sublist.remove(elt);
//                    }
//                }
//            }
//        }
//    }
//    private void cleanUpDuplicatesInListOLD(String description, List list) {
////        for (int i =0, size=list.size(); i<size;i++) {
//        int i = 0;
//        while (i < list.size()) {
////            boolean moveToNextIndex=true;
//            Object elt = list.get(i);
//            int t = i + 1;
//            while (t < list.size() && list.subList(t, list.size()).contains(elt)) {
//                Log.p("CLEANUP: " + description + " - List " + list + " contains duplicate of " + elt + " at position " + (list.subList(t, list.size()).indexOf(elt) + t), logLevel);
//                if (executeCleanup) {
//                    list.subList(t, list.size()).remove(elt);
////                    moveToNextIndex=false;
//                } else {
//                    t++; //since elt isn't removed, we need to advance the pointer to test for further duplicates in the list *after* the first duplicate found
//                }
//            }
////            if (moveToNextIndex)
//            i++;
//        }
////        if (executeCleanup) {
////            DAO.getInstance().save((ParseObject) list);
////        }
//    }
//</editor-fold>
    //ITEMLISTS
    boolean allItemListsCleaned;

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * find and remove any duplicates in a list
//     *
//     * @param description
//     * @param list
//     */
////    private boolean cleanUpDuplicatesInList(String description, List list, boolean executeCleanup) {
//    private boolean cleanUpDuplicatesInItemListXXX(String description, ItemList list, boolean executeCleanup) {
//        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
//        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
//        ArrayList cleanList = new ArrayList();
//        for (int i = 0; i < list.getSize(); i++) {
//            Object elt = list.getItemAt(i);
//            if (cleanList.contains(elt)) {
//                log(description + " contains duplicate of \"" + elt + "\" at position " + i + " (list= " + list + ")");
//                logAction("Remove duplicate element");
//            } else {
//                cleanList.add(elt);
//            }
//        }
//        boolean deletes = list.getSize() != cleanList.size();
//        if (executeCleanup) {
//            list.clear();
//            list.addAll(cleanList);
//        }
//        return deletes;
//    }
//
//    /**
//     * returns true if obj was NOT in the list (to allow the caller to take
//     * action to eg save the owner of the list)
//     *
//     * @param description
//     * @param objWhichShouldBeInList
//     * @param list
//     * @return true if missing in list
//     */
//    private List cleanUpMissingInclusionInListXXX(String description, Object objWhichShouldBeInList, List list) {
////        if (list == null || !list.contains(objWhichShouldBeInList)) {
//        if (list != null && !list.contains(objWhichShouldBeInList)) {
//            log(description, logLevel);
//            if (executeCleanup) {
//                list.add(objWhichShouldBeInList);
//            }
//            return list;
//        }
//        return null;
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * cleans up a Category or ItemList. On the full list. Check it belongs to
     * ItemListList/CategoryList. Check that each item has the list/category as
     * owner. Check that each item in the list is on the server. Check if there
     * are any Items with this list as owner which are NOT in the list. TODO:
     * handle bags (use getItemAt/removeItem etc).
     *
     * @param itemList
     */
//    private void cleanUpBadObjectReferencesItemListXXX(ItemList itemList) {
////        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
//        if (false) {
//            log("number elements in ItemList = " + itemList, logLevel);
//        }
//        int i = 0;
//        List<Item> itemsInList = itemList.getListFull();
//        while (i < itemsInList.size()) {
//            ItemAndListCommonInterface item = itemsInList.get(i);
//            if (notOnParseServer((ParseObject) item)) {
//                logError(itemList, (ParseObject) item);
//                if (executeCleanup) {
//                    itemsInList.remove(i);
////                    i++; //DON'T INCREASE i if bad item ref was removed
//                } else {
//                    i++;
//                }
//            } else {
//                if (item instanceof Item) {
//                    cleanUpBadObjectReferencesItemXXX((Item) item); //we should clean up here, since an Item may only be found via its owner ItemList
//                }//                else if (item instanceof WorkSlot)
////                cleanUpWBadObjectReferencesItem((Item)item); //we should clean up here, since an Item may only be found via its owner ItemList
//                i++;
//            }
//        }
//        if (itemList instanceof ParseObject && executeCleanup) {
//            itemList.setList(itemsInList);
//            dao.saveNew((ParseObject) itemList, false);
//        }
//    }

    /**
     * checks: duplicates in itemList; items in itemList whose owner is not
     * itemList; elements in itemList not in Parse;
     *
     * @param itemList
     */
//    private void cleanUpItemListXXX(ItemList<Item> itemList, boolean executeCleanup) {
//        cleanUpItemList(itemList, itemList.equals(TemplateList.getInstance()), executeCleanup);
//    }
//</editor-fold>

     void cleanUpItemList(ItemList<Item> itemList, boolean makeTemplate, boolean executeCleanup) {

        checkOwner(itemList, ItemListList.getInstance(), executeCleanup);
//        cleanUpBadObjectReferencesItemListXXX(itemList);

        List<Item> items = itemList.getListFull();

        //check duplicate subtasks
        if (cleanUpDuplicatesInList("ItemList " + itemList, items, executeCleanup)) {
            if (executeCleanup) {
                itemList.setList(items);
            }
        }
        
        //check 
        if (checkListForNotInParseSoftDeletedReferenceOwner(itemList, (List)items, executeCleanup)) {
            if (executeCleanup) {
                itemList.setList(items);
            }
        }

        //check invidivudal subtasks
        for (Item item : items) {
            cleanUpItem(item, itemList, makeTemplate, executeCleanup);
        }

        //FILTERSORTDEF
        FilterSortDef filterSortDef = itemList.getFilterSortDef();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null) {
//            if (notOnParseServer((ParseObject) filterSortDef)) {
//                log("ItemList \"" + itemList + "\" with bad ref to FilterSortDef= " + filterSortDef);
//                logAction("Removing FilterSortDef");
//                if (executeCleanup) {
//                    itemList.setFilterSortDef(null); //remove reference to inexisting RepeatRule
//                }
//            } else {
//                filterSortDefReferenced.add(filterSortDef);
//            }
//        };
//</editor-fold>
        if (!cleanUpReferencedFilterSortDef(itemList, filterSortDef, executeCleanup)) {
            filterSortDefReferenced.add(filterSortDef);
        }

        //workslots
        cleanUpWorkSlotList(itemList, executeCleanup);

        if (executeCleanup) {
            dao.saveNew((ParseObject) itemList);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean cleanUpItemListOrCategoryXXX(ItemList itemListOrCategory, boolean executeCleanup) {
//        return cleanUpItemListOrCategoryXXX(itemListOrCategory, executeCleanup, false);
//    }
//    boolean cleanUpItemListOrCategoryXXX(ItemList itemListOrCategory, boolean executeCleanup, boolean cleanupItems) {
//        boolean issuesFound = false;
//        String text = itemListOrCategory.getText();
//        String objectIdP = itemListOrCategory.getObjectIdP();
//        String prefix = "CLEANUP: " + (itemListOrCategory instanceof Category ? "Category" : "ItemList") + " \"" + text + " [" + objectIdP + "]";
//        //check if belongs to CategoryList/ItemListList
//        if (itemListOrCategory instanceof Category && itemListOrCategory.getOwner() != CategoryList.getInstance()) {
//            Log.p(prefix + " not in CategoryList, but in [" + itemListOrCategory.getOwner().getObjectIdP() + "]");
//            if (executeCleanup) {
//                itemListOrCategory.setOwner(CategoryList.getInstance());
//            }
//        } else if (itemListOrCategory instanceof ItemList && itemListOrCategory.getOwner() != ItemListList.getInstance() && itemListOrCategory != Inbox.getInstance()) {
//            Log.p(prefix + " not in ItemListList, but in [" + itemListOrCategory.getOwner().getObjectIdP() + "]");
//            if (executeCleanup) {
//                itemListOrCategory.setOwner(ItemListList.getInstance());
//            }
//        }
//
//        int i = 0;
////        List<Item> items = itemListOrCategory.getListFull();
//        while (i < itemListOrCategory.getSize()) {
//            boolean moveToNextIndex = true; //hack to make sure we don't skip an i when an element in the list is removed
//            Object elt = itemListOrCategory.getItemAt(i);
////            Object elt = itemListOrCategory.get(i);
//
//            if (elt == null) {
//                Log.p(prefix + " refer contains Item which is NULL at index=" + i);
//                if (executeCleanup) {
//                    itemListOrCategory.removeItem(i);
//                    moveToNextIndex = false;
//                }
//            } else if (!(elt instanceof Item)) {
//                boolean remove = false;
//                if (elt instanceof ParseObject) {
//                    Log.p(prefix + " refer to a ParseObject which is not an Item: [" + ((ParseObject) elt).getObjectIdP() + "]");
//                    remove = true;
//                } else {
//                    Log.p(prefix + " refer to an object which is not a ParseObject, toString()=\"" + elt + "\"");
//                    remove = true;
//                }
//                issuesFound = issuesFound || remove;
//                if (remove && executeCleanup) {
//                    itemListOrCategory.removeItem(i);
//                    moveToNextIndex = false;
//                }
//            } else {//an Item
//                //if not on server, simply remove the element
//                if (notOnParseServer((ParseObject) elt)) {
//                    Log.p(prefix + " refer to Item not on server [" + ((ParseObject) elt).getObjectIdP() + "]");
//                    if (executeCleanup) {
//                        itemListOrCategory.removeItem(i);
//                        moveToNextIndex = false;
//                    }
//                    issuesFound = true;
//                } else { //on server
//                    Item item = (Item) elt;
//                    String itemText = itemToString(item); //"\"" + item.getText() + "\" [" + item.getObjectIdP() + "]";
//
//                    //Category refers to elt, but elt does not have Category in its list
//                    if (itemListOrCategory instanceof Category) {
//                        if (!item.getCategories().contains(itemListOrCategory)) { //if item does not reference the category, then add the category
//                            Log.p(prefix + " references Item " + itemText + " but not in its categories (" + item.getCategories() + ")");
//                            if (executeCleanup) {
//                                item.addCategoryToItem((Category) itemListOrCategory, false); //add missing ref
//                                dao.saveNew(item, false);
//                            }
//                            issuesFound = true;
//                        }
//                        //ItemList refers to elt, but elt does not have Category in its list
//                    } else if (itemListOrCategory instanceof ItemList) {
////                    if (item.getOwner() == null || !item.getOwner().equals(itemListOrCategory)) {
//                        if (item.getOwner() == null) { //IF ever an item is referenced from multiple lists or projects, the first one 'wins' and becomes the owner
//                            issuesFound = true;
//                            Log.p(prefix + " references Item " + itemText + " but is has no Owner (owner=null)");
//                            if (executeCleanup) {
//                                item.setOwner((ItemList) itemListOrCategory); //if null, add ItemList as owner
//                                dao.saveNew(item, false);
//                            }
//                        } else if (item.getOwner() != itemListOrCategory) {
//                            Log.p(prefix + " references Item " + itemText + " which has another owner=" + item.getOwner());
////                                itemListOrCategory.remove(i); //if another is owner, remove item from this list
//                            issuesFound = true;
//                            if (executeCleanup) {
////                                itemListOrCategory.removeItem(i); //if another is owner, remove item from this list
//                                item.removeFromOwner(); //if another is owner, remove that one before assigning to this one
//                                itemListOrCategory.addItem(item); //it is more visible to end-user that item is in list, so keep it in this list
//                                dao.saveNew(item, false);
//                                moveToNextIndex = false;
//                            }
//                        }
//                    } // else: if owner is not null, then if the owner is wrong, it will be fixed when fixing the item itself elsewhere
//                    if (cleanupItems) {
//                        cleanUpItem(item, executeCleanup);
//                    }
//                }
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (itemListOrCategory instanceof ParseObject && executeCleanup) {
////            if (executeCleanup) {
////                save((ParseObject) itemListOrCategory);
////            }
////</editor-fold>
//            if (moveToNextIndex) {
//                i++;
//            }
//        }
//
//        cleanUpDuplicatesInItemListOrCategory(itemListOrCategory.getText(), itemListOrCategory, executeCleanup);
//
//        //check if all items on server that reference ItemList as owner, or which has Category in its categoryList are in the ItemList/Category:
//        if (itemListOrCategory instanceof Category) {
//            List<Item> itemsFromParse = dao.fetchAllItemsWithThisCategory((Category) itemListOrCategory);
//            for (Item itm : itemsFromParse) {
//                if (((Category) itemListOrCategory).getItemIndex(itm) < 0) {
//                    Log.p("Item " + itemToString(itm) + " on server has category=" + prefix + " but is not in list");
//                    ((Category) itemListOrCategory).addItemToCategory(itm, false); //add to end
//                    issuesFound = true;
//                }
//            }
//        } else {
//            List<Item> itemsFromParse = dao.fetchAllItemsOwnedByItemList((ItemList) itemListOrCategory);
//            for (Item itm : itemsFromParse) {
//                if (!itemListOrCategory.contains(itm)) {
//                    Log.p("Item " + itemToString(itm) + " on server has owner=" + prefix + " but is not in list");
//                    itemListOrCategory.addItem(itm); //add to end
//                    issuesFound = true;
//                }
//            }
//        }
//        //workslots:
//        issuesFound = cleanUpWorkSlotList(itemListOrCategory, executeCleanup) || issuesFound;
//
//        //TODO calculate all values in the list derived from the elements (currently none in ItemList nor Category)
//        if (issuesFound && executeCleanup) {
//            dao.saveNew((ParseObject) itemListOrCategory, false);
//        }
//        return issuesFound;
//    }
//</editor-fold>
    /**
     * problems detected: itemLists in parse not in ItemListList; itemLists
     * where owner is not ItemListList; duplicates in ItemListList; references
     * to itemLists not in parse; itemLists issues
     */
    private void cleanUpAllItemListsFromParse(boolean executeCleanup) {
        List<ItemList> itemListsFromParse = dao.getAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE), false);
        ItemListList itemListList = ItemListList.getInstance();

        if (false) {
            log("number ItemLists in Parse = " + itemListsFromParse.size(), logLevel);
        }
        //Clean all ItemLists on parse server:
        //add any ItemLists on Parse which are not currently referencing ItemListList
        checkListForNotInParseSoftDeletedReferenceOwner(itemListList, (List) itemListsFromParse, executeCleanup);

        if (executeCleanup) {
            dao.saveNew((ParseObject) itemListList, false);
        }
    }

    private void cleanUpAllItemListsInItemListList(boolean executeCleanup) {
        ItemListList itemListList = ItemListList.getInstance();

        if (false) {
            log("number ItemLists in ItemListList = " + itemListList.size(), logLevel);
        }
        //Clean all ItemLists on parse server:
        List<ItemList> itemLists = itemListList.getListFull();

        checkListForNotInParseSoftDeletedReferenceOwner(itemListList, (List) itemLists, executeCleanup);

        //DUPLICATES
//        cleanUpDuplicatesInItemList("ItemListList", itemListList, executeCleanup);
        if (cleanUpDuplicatesInList("ItemListList", itemLists, executeCleanup)) {
            if (executeCleanup) {
                itemListList.setList(itemLists);
            }
        }

        //INDIVIDUAL ITEMLISTS
        for (int i = 0, size = itemListList.getSize(); i < size; i++) {
            ItemList itemList = (ItemList) itemListList.getItemAt(i);

            if (notOnParseServer((ParseObject) itemList)) {
                log("ItemListList contains ItemList not on ParseServer, ItemList= \"" + itemList + "\"");
                logAction("Remove ItemList from ItemListList");
                if (executeCleanup) {
                    itemListList.remove(i);
                    i--; //hack to nullify the for loop
                }
            } else {
                //at this point, we should only have the 'good' itemLists, worth cleaning up individually
                cleanUpItemList(itemList, false, executeCleanup);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//         ItemListList itemListList = ItemListList.getInstance();
//if executing cleanup, then by now itemListList should have the correct
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(itemListList); //Clean up links to removed Categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists("ItemListList", "ItemList", itemListList); //Clean up links to removed Categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemListsXXX("ItemListList", "ItemList", itemListList); //Clean up links to removed Categories
//</editor-fold>
        if (executeCleanup) {
            dao.saveNew((ParseObject) itemListList, false);
        }
        setLogPrefix("");
    }

    private void cleanUpAllItemLists(boolean executeCleanup) {
        setLogPrefix("ItemLists");
        cleanUpAllItemListsFromParse(executeCleanup);
        cleanUpAllItemListsInItemListList(executeCleanup);
        setLogPrefix("");
        allItemListsCleaned = true;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void cleanUpAllItemListsInParseXXX() {
//        setLogPrefix("ItemLists");
////        ItemListList itemListList = getItemListList();
////        ItemListList itemListList = ItemListList.getInstance();
////        cleanUpAllItemListsFromParse(dao.getAllItemListsFromParse(), itemListList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
////        cleanUpAllItemListsFromParse(dao.getAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE), false), itemListList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//        cleanUpAllItemListsFromParse(); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//
//        setLogPrefix("");
//    }
//
//    /**
//     * clean up duplicates in the (full) ItemList or Category. Will remove any
//     * subsequent duplicates (leaving the first appearence). Execute this
//     * *after* other clean-ups, notably ensuring that all elements are Items
//     *
//     * @param description
//     * @param itemListOrCategory
//     * @param executeCleanup
//     * @return
//     */
//    private boolean cleanUpDuplicatesInItemListOrCategoryXXX(String description, ItemList itemListOrCategory, boolean executeCleanup) {
//        List list = itemListOrCategory.getListFull();
//        List cleanedList = removeDuplicatesXXX(list);
//        if (executeCleanup) {
//            itemListOrCategory.setList(cleanedList);
//        }
//        return list.size() != cleanedList.size();
//    }
//
//    private boolean cleanUpDuplicatesInItemListOrCategoryOLD(String description, ItemList itemListOrCategory, boolean executeCleanup) {
//        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
//        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
//        boolean hasDuplicates = false;
//        ArrayList uniqueItems = new ArrayList(); //items that have already been checked
//        int index = 0;
////        for (int i = 0, size = itemListOrCategory.size(); i < size; i++) {
//        while (index < itemListOrCategory.getSize()) {
//            Item elt = (Item) itemListOrCategory.getItemAt(index);
//            if (uniqueItems.contains(elt)) {
//                //TODO duplicates can be either a second instance of same object, or a copy of it, should check for both and handle separately
//                hasDuplicates = true;
//                log(description + " contains duplicate of \"" + itemToString(elt) + "\" at position " + index + " (list= " + itemListOrCategory + ")", logLevel);
//                if (executeCleanup) {
//                    if (itemListOrCategory instanceof Category) {
////                        ((Category) itemListOrCategory).removeItemFromCategory(elt, false); //
//                        ((Category) itemListOrCategory).removeItem(index); //
//                    } else {
////                        ((ItemList) itemListOrCategory).removeFromList(elt, false); //
//                        ((ItemList) itemListOrCategory).removeItem(index); //
//                    }                    //don't index++ since we've removed the item and next iteration should treat the item now at position index
//                } else {
//                    index++;
//                }
//            } else {
//                uniqueItems.add(elt); //no duplicate so add to list
//                index++;
//            }
//        }
//        return hasDuplicates;
//    }
//
//    //CATEGORIES
//    private void cleanUpBadObjectReferencesCategoryXXX(Category category) {
////        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
//        if (false) {
//            log("number elements in Category = " + category, logLevel);
//        }
//        int i = 0;
//        List<Item> itemsInCategory = category.getListFull();
//        while (i < itemsInCategory.size()) {
//            Item itemInCat = itemsInCategory.get(i);
//            if (notOnParseServer(itemInCat)) {
//                logError(category, itemInCat);
//                if (executeCleanup) {
//                    itemsInCategory.remove(i);
////                    i++; //DON'T INCREASE i if bad item ref was removed
//                } else {
//                    i++;
//                }
//            } else {
//                cleanUpBadObjectReferencesItem(itemInCat); //we should clean up here, since an Item may only be found via its owner ItemList
//                i++;
//            }
//        }
//        if (category instanceof ParseObject && executeCleanup) {
//            category.setList(itemsInCategory);
//            dao.saveNew((ParseObject) category, false);
//        }
//    }
//</editor-fold>
    private boolean allCategoriesCleaned;

    /**
     * cleans up duplicates in the category
     *
     * @param category
     */
     void cleanUpCategory(Category category, boolean executeCleanup) {
//        cleanUpBadObjectReferencesCategory(category);
        checkOwner(category, CategoryList.getInstance(), executeCleanup);

        List itemsInCat = category.getListFull();
//<editor-fold defaultstate="collapsed" desc="comment">
//                    List<Item> categoryItems = cat.getListFull();
//            for (Item item : categoryItems) {
//                if (!item.getCategories().contains(cat)) {
//                    log("Item doesn't reference Category: Item \"" + item + "\" is missing category=" + cat, logLevel);
//                    if (executeCleanup) {
////                        cat.addItemToCategory(item, false);
//                        item.addCategoryToItem(cat, false);
//                        dao.saveNew((ParseObject) item, false);
//                    }
//                }
//            }
//</editor-fold>
    //check duplicates
        if (cleanUpDuplicatesInList("Category " + ((ItemAndListCommonInterface) category).getText(), itemsInCat, executeCleanup)) {
            if (executeCleanup) {
                category.setList(itemsInCat);
            }
        }

        //if an Item is in a Category, but doesn't list Category, then add it (better to add information which is 'half-there' then to delete it)
        if (checkListRemoveElementsNotInParseAndReferenceCategory(category, itemsInCat, executeCleanup)) {
            if (executeCleanup) {
                category.setList(itemsInCat);
            }
        }

        //FILTERSORTDEF
        FilterSortDef filterSortDef = category.getFilterSortDef();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null) {
//            if (notOnParseServer((ParseObject) filterSortDef)) {
//                log("Category \"" + category + "\" with bad ref to FilterSortDef= " + filterSortDef);
//                ogAction("Removing FilterSortDef");
//                if (executeCleanup) {
//                    category.setFilterSortDef(null); //remove reference to inexisting RepeatRule
//                }
//            } else {
//                filterSortDefReferenced.add(filterSortDef);
//            }
//        }
//</editor-fold>
        if (!cleanUpReferencedFilterSortDef(category, filterSortDef, executeCleanup)) {
            filterSortDefReferenced.add(filterSortDef);
        }

        //workslots
        cleanUpWorkSlotList(category, executeCleanup);

        if (executeCleanup) {
            dao.saveNew((ParseObject) category);
        }
    }

    /**
     * cleans up the full list of Categories or ItemLists directly from Parse,
     * repair raw list of Categories first (will attach any (non-empty??)
     * categories to CategoryList before cleaning up those categories
     *
     *
     * @param catOrItemList
     */
//    void cleanUpAllCategoriesFromParse() {
//        CategoryList categoryList = getCategoryList(true);
//        cleanUpAllCategoriesFromParse(getAllCategoriesFromParse(), categoryList);
//    }
//    private void cleanUpAllCategoriesFromParse(List<Category> listOfCategories, CategoryList categoryList) {
    public void cleanUpAllCategories(boolean executeCleanup) {
        setLogPrefix("Categories");
        CategoryList categoryList = CategoryList.getInstance();
        List<Category> categories=categoryList.getListFull();
        List<Category> categoriesInCatList = categoryList.getListFull();
//        categoryList.reloadFromParse(true, null, null); //get latest state

        //check that every category in Parse is in the stored list of categories
        List<Category> listOfCategoriesFromParse = dao.getAllCategoriesFromParse();
        checkListForNotInParseSoftDeletedReferenceOwner(categoryList, (List)listOfCategoriesFromParse, executeCleanup);
        
        if (cleanUpDuplicatesInList("CategoryList", categoriesInCatList, executeCleanup)) {
            if (executeCleanup) {
                categoryList.setList(categoriesInCatList);
                dao.saveNew((ParseObject) categoryList);
            }
        }

        if (checkListForNotInParseSoftDeletedReferenceOwner(categoryList, (List) categoriesInCatList, executeCleanup)) {
            if (executeCleanup) {
                categoryList.setList(categoriesInCatList);
                dao.saveNew((ParseObject) categoryList);
            }
        }

        
        
        for(Category cat:categories)
            cleanUpCategory(cat, executeCleanup);
//<editor-fold defaultstate="collapsed" desc="comment">
//        log("number Categories in Parse = " + listOfCategoriesFromParse.size(), logLevel);
//        log("number Categories in CategoryList = " + categoryList.getSize(), logLevel);
        //check that every category in Parse is in the stored list of categories
//for (int i = 0, size = listOfCategoriesFromParse.size(); i < size; i++) {
//    Category cat = listOfCategoriesFromParse.get(i);
//    if (cat.getOwner() == null) {
//        log("Missing owner (CategoryList) in Category \"" + cat.getText() + "\" ObjId=" + cat.getObjectIdP() + " size=" + cat.getSize() + ", to its owner ListOfCategories (which contains(cat)=" + categoryList.contains(cat) + ")", logLevel);
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false &&  categoryList.contains(cat)) {
////                    if (executeCleanup) {
////                        cat.setOwner(categoryList);
////                        save(cat);
////                    }
//////                } else if (cat.getList().size() == 0) { //a lost category, empty, not visible to user, so probably safe to delete. NO, better to recover it
//////                    if (executeCleanup) {
//////                        delete(cat); //nothing in cateogyr, safe to delete
//////                    }
////                } else //a lost category, with content, so should probably be kept
////</editor-fold>
//if (executeCleanup) {
//    cat.setOwner(categoryList);
//    dao.saveNew((ParseObject) cat, false);
//    if (!cat.isSoftDeleted()) {
//        categoryList.add(cat);
//        dao.saveNew((ParseObject) categoryList, false);
//    }
//}
//    } else if (!categoryList.contains(cat)) { //add missing categories to CategoryList
//        if (!cat.isSoftDeleted()) {
//            log("Category not referenced: Category \"" + cat.getText() + " not in CategoryList" + categoryList, logLevel);
//            
//            if (executeCleanup) {
//                cat.setOwner(categoryList);
//                dao.saveNew((ParseObject) cat, false);
//                categoryList.add(cat);
//                dao.saveNew((ParseObject) categoryList, false);
//            }
//        }
//    }
//    //check that all items in category also reference category
//    cleanUpCategory(cat, executeCleanup);
//}
//        cleanUpBadObjectReferencesInListOfCategories(); //Clean up links to removed Categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists("CategoryList", "Category", dao.getCategoryList());  //Clean up links to removed Categories
//</editor-fold>
        setLogPrefix("");
        allCategoriesCleaned = true;
    }

    //TEMPLATES
    boolean allTemplatesCleaned;

//    private boolean hasTemplateParentXXX(Item item) {
//        ItemAndListCommonInterface owner = item.getOwner();
//        return (owner instanceof Item && (((Item) owner).isTemplate() || owner instanceof TemplateList || hasTemplateParent((Item) owner)));
//    }

    private boolean makeAllSubTaskTemplatesAndRemoveDuplicates(Item template, boolean executeCleanup) {
        boolean changed = false;
        List<Item> subtasks = template.getListFull();
        if (subtasks != null && subtasks.size() > 0) {
            for (Item item : subtasks) {
                if (!item.isTemplate()) {
                    log("non-template \"" + item + "\" inside template \"" + item + " parseId=" + ((ParseObject) item).getObjectIdP());
                    if (executeCleanup) {
                        item.setTemplate(true);
                        dao.saveNew(item, false);
                    }
                }
                makeAllSubTaskTemplatesAndRemoveDuplicates(item, executeCleanup); //iterate down the hierarchy
            }
            if (cleanUpDuplicatesInList("ItemList " + template.getText(), subtasks, executeCleanup) && executeCleanup) {
                template.setList(subtasks);
                changed = true;
            }
        }
        return changed;
    }

    /**
     * clean up all templates in TemplateList, make it a template, clean up
     * duplicates in its subtasks, make all subtasks templates
     *
     * @param template
     * @param executeCleanup
     */
     void cleanUpTemplates(boolean executeCleanup) {

        TemplateList templateList = TemplateList.getInstance();
        List<Item> topLevelTemplatesFromParse = dao.getTopLevelTemplatesFromParse();

//        if (templatesFromParse.size()!=templateList.size()) {
        setLogPrefix("TemplateList");
        if (false) {
            log("number elements in TemplateList = " + templateList.size() + ", number of toplevel templates from Parse=" + topLevelTemplatesFromParse.size(), logLevel);
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        List templates = templateList.getList();
//        for (int i = 0, size = topLevelTemplatesFromParse.size(); i < size; i++) {
//            if (!templates.contains(topLevelTemplatesFromParse.get(i))) {
//                templates.add(topLevelTemplatesFromParse.get(i));
//            }
//        }
//</editor-fold>
        //add any top-level templates from parse not yet in TemplateList
        checkListForNotInParseSoftDeletedReferenceOwner(templateList, (List) topLevelTemplatesFromParse, executeCleanup);

        cleanUpDuplicatesInList("Templates " + ((ItemAndListCommonInterface) templateList).getText(), templateList, executeCleanup);

        //now check all templates, fix all issues in templates and make any subtasks templates
        for (Item topLevelTemplate : (List<Item>) templateList) {
            cleanUpItem(topLevelTemplate, templateList, true, executeCleanup); //true=make templates
        }

        //add any missing stored (top-level) templates to the list
        for (int i = 0, size = topLevelTemplatesFromParse.size(); i < size; i++) {
            if (!templateList.contains(topLevelTemplatesFromParse.get(i))) {
                log("TemplateList \"" + templateList + "\" does not contain top-level template=\"" + topLevelTemplatesFromParse.get(i) + "\"");
                if (executeCleanup) {
                    templateList.add(topLevelTemplatesFromParse.get(i));
                }
            }
        }

        //ensure top-level templates are owned byt templateList
        for (int i = 0, size = templateList.getSize(); i < size; i++) {
            ItemAndListCommonInterface template = templateList.getItemAt(i);
            if (!template.getOwner().equals(templateList)) {
                log("template=\"" + template + "\" in TemplateList does not have it as owner, TemplateList=" + templateList + "\"");
            }
            if (executeCleanup) {
                template.setOwner(templateList);
                dao.saveNew((Item) template, false);
            }
        }

        //for all top-level templates
        int i = 0;
        while (i < templateList.getSize()) {
            Object template = templateList.getItemAt(i);
            //remove any objects not stored on parse server
            if (template instanceof ParseObject && notOnParseServer((ParseObject) template)) {
                log("TemplateList \"" + templateList + "\" bad ref to ObjId \"" + ((ParseObject) template).getObjectIdP());
                if (executeCleanup) {
                    templateList.removeItem(i);
//                    i -= 1;
                    continue;
                }
            }

            //set any items not already a template as templates:
            if (template instanceof Item) { // && !((Item) elt).isTemplate()) {
                //non-template item in TemplateList, 
                if (!((Item) template).isTemplate()) {
                    log("template Item in TemplateList is not a template \"" + template + " objId=" + ((ParseObject) template).getObjectIdP());
                    if (executeCleanup) {
                        ((Item) template).setTemplate(true);
                        dao.saveNew((Item) template, false);
                    }
                }
                //check that full hierarchy of subtasks below top-level template are also marked as templates
                makeAllSubTaskTemplatesAndRemoveDuplicates((Item) template, executeCleanup);
                i++;
            } else { //non-Item in list
                log("TemplateList \"" + templateList + "\" contains non-Item" + template + ", ObjId \"" + (template instanceof ParseObject ? ((ParseObject) template).getObjectIdP() : "<not an ParseObect>"));
                if (executeCleanup) {
                    templateList.removeItem(i);
//                    i -= 1;
                }
            }
        }
//        List items = templateList.getList();
//        if (cleanUpDuplicatesInList("Templates " + ((ItemAndListCommonInterface) templateList).getText(), templateList, executeCleanup) && executeCleanup) {
        if (executeCleanup) {
//            templateList.setList(templateList);
            dao.saveNew((ParseObject) templateList, false);
        }
        setLogPrefix("");
        allTemplatesCleaned = true;
    }

//    public void cleanUpTemplateListInParseXXX(boolean executeCleanup) {
////        cleanUpTemplateList(DAO.getInstance().getTemplateList(), getTopLevelTemplatesFromParse(), true);
//        cleanUpTemplateList(TemplateList.getInstance(), dao.getTopLevelTemplatesFromParse(), executeCleanup);
//    }
    //REPEATRULES
    boolean allRepeatRulesCleaned;
//    private boolean cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(RepeatRuleParseObject repeatRule, List<ItemAndListCommonInterface> instanceList) {

    private boolean cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(RepeatRuleParseObject repeatRule, List<RepeatRuleObjectInterface> instanceList,boolean executeCleanup) {
        int i = 0;
        boolean listUpdated = false;

        while (i < instanceList.size()) {
            Object elt = instanceList.get(i);
            //if not on server, simply remove the element
            if (elt instanceof ParseObject && notOnParseServer((ParseObject) elt)) {
                if (elt instanceof Item) {
                    log("Item RepeatRule not on server: Item \"" + elt + "\" not on server for RepeatRule instance list for RepeatRule " + repeatRule);
                } else if (elt instanceof WorkSlot) {
                    log("WorkSlot Repeatrule not on server: WorkSlot \"" + elt + "\" not on server for RepeatRule instance list for RepeatRule " + repeatRule);
                }
                if (executeCleanup) {
                    instanceList.remove(i);
                    listUpdated = true;
                    i--;
                }
            }
            i++; //continue with next element
        }
//        if (executeCleanup) {  save(instanceList);  }
        return listUpdated;

    }

//    private void cleanUpBadObjectReferencesRepeatRule(RepeatRule repeatRule) {
//        //TODO!!!!! fix problems in RepeatRules (eg ?? references to created undone instances)
//        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
//    }

    /**
     * cleans up duplicates in the category
     *
     * @param category
     */
    private void cleanUpRepeatRules(boolean executeCleanup) {
        setLogPrefix("RepeatRules");
        List<RepeatRuleParseObject> allRepeatRules = dao.getAllRepeatRulesFromParse();
        log("number RepeatRules in Parse= " + allRepeatRules.size(), logLevel);

        //delete repeat rules not referenced by any Item or WorkSlot
        //construct hashmpas to effectively search for elements that point to a filter
        Map<RepeatRuleParseObject, Item> itemsWithRepeatRule = new HashMap<>();

        for (Item item : dao.getAllItems()) {
            if (item.getRepeatRuleN() != null) {
                itemsWithRepeatRule.put(item.getRepeatRuleN(), item);
            }
        }

        Map<RepeatRuleParseObject, WorkSlot> workSlotsWithRepeatRule = new HashMap<>();

//        for (WorkSlot workSlot : dao.getAllWorkSlotsFromParse().getWorkSlotListFull()) {
        List<WorkSlot> workSlots = dao.getAllWorkSlotsFromParse();
        for (WorkSlot workSlot : workSlots) {
            if (workSlot.getRepeatRuleN() != null) {
                workSlotsWithRepeatRule.put(workSlot.getRepeatRuleN(), workSlot);
            }
        }
//        for (int i = 0, size = allRepeatRules.size(); i < size; i++) {
//            RepeatRuleParseObject repeatRule = allRepeatRules.get(i);
        for (RepeatRuleParseObject repeatRule : allRepeatRules) {

            //for every repeatRule, check if it is referenced and if not delete it
            if (itemsWithRepeatRule.get(repeatRule) == null && workSlotsWithRepeatRule.get(repeatRule) == null) { //no refs to repeatRule
                log("RepeatRule (ObjId=" + repeatRule.getObjectIdP() + ") is not referenced by any Item or WorkSlot", logLevel);
                if (executeCleanup) {
                    dao.delete(repeatRule, true, false); //delete filters without ref to both objectId and Screen
//                    allRepeatRules.remove(repeatRule); //NOT necessary since we have a for loop
                }
            } else { //if RR is referenced
                //clean up wrong references in list of repeat instances
//            List<ItemAndListCommonInterface> repeatInstanceList = repeatRule.getListOfUndoneRepeatInstances();
                List<RepeatRuleObjectInterface> undoneInstances = repeatRule.getListOfUndoneInstances();
                cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(repeatRule, undoneInstances,executeCleanup);
                //clean up duplicates in list of repeat instances
                cleanUpDuplicatesInList("RepeatRule instances " + repeatRule, undoneInstances, executeCleanup);
                if (executeCleanup) {
                    repeatRule.setListOfUndoneInstances(undoneInstances);
                    dao.saveNew(repeatRule, false);
                }
            }
        }
        setLogPrefix("");
        allRepeatRulesCleaned = true;
    }

    //ITEMS
    private boolean allItemFromParseCleaned;
//    private void cleanUpBadObjectReferences(List<ParseObject> list) {

    /**
     * check all Items directly from Parse to find any 'dangling' not attached
     * to a list already. If they have an owner, add them to the owner's list,
     * otherwise add to Inbox (or create a separate list?!).
     *
     * @param list list of categories or itemlists
     */
    private void cleanUpItemsFromParse(boolean executeCleanup) {
        ASSERT.that(allItemListsCleaned && allTemplatesCleaned, "run this only after checking all items from parse");
        if(!(allItemListsCleaned && allTemplatesCleaned))
                return;

        setLogPrefix("Items");
        List<Item> list = dao.getAllItems(true);

        if (false) {
            log("number elements in list = " + list.size(), logLevel);
        }

        ItemList danglingItems = new ItemList("Recovered " + new MyDate());

        int i = 0;
        while (i < list.size()) {
            Item item = list.get(i);
            ItemAndListCommonInterface owner = item.getOwner();
            if (owner == null) {
//                cleanUpBadObjectReferencesItem(item);
                cleanUpItem(item, owner, item.isTemplate(), executeCleanup); //normally clean up is requested by the owner, but 
                if (executeCleanup) {
                    danglingItems.addToList(item);
                    dao.saveNew((ParseObject) item);
                }
            } else if (owner.getItemIndex(item) == -1) {
                cleanUpItem(item, owner, item.isTemplate(), executeCleanup);
                if (executeCleanup) {
                    owner.addToList(item, true, false); //add to end of list, don't add owner as owner (already the case)
                    dao.saveNew((ParseObject) owner);
                }
            }
        }

        if (executeCleanup && danglingItems.getSize() > 0) {
            ItemListList.getInstance().add(danglingItems);
            dao.saveNew(ItemListList.getInstance(), danglingItems);
        }
        setLogPrefix("");
        allItemFromParseCleaned = true;
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private void cleanUpBadCategoryReferences(List<Category> list) {
    //        for (int i = 0, size = list.size(); i < size; i++) {
    //            if (badReference((ParseObject) list.get(i))) {
    //                if (list instanceof ItemAndListCommonInterface) {
    //                    Log.p("Bad object ref in \"" + ((ItemAndListCommonInterface) list).getText() + "\" to objectId=" + ((ParseObject) list).getObjectId());
    //                } else {
    //                    Log.p("Bad object ref in " + list + " to objectId=" + ((ParseObject) list).getObjectId());
    //                }
    //                if (list.get(i) instanceof Category) {
    //                    cleanUpBadObjectReferences(((Category) list.get(i)));
    //                }
    //                list.remove(i);
    //            }
    //        }
    //    }
    //</editor-fold>

    /**
     * clean up an Item (leaf-task or project with subtasks). Check subtasks
     * have project as owner. Check if any tasks has project as owner (or
     * another task as owner). Update inherited values and values derived from
     * subtasks. Check that all referenced elements exist (Categories, owners,
     * repeatRules
     *
     * @param item
     */
//    void cleanUpItem(Item item, boolean executeCleanup) {
//        cleanUpItem(item, false, executeCleanup);
//    }
    void cleanUpItem(Item item, ItemAndListCommonInterface correctOwner, boolean makeTemplate, boolean executeCleanup) {
//        boolean checkOwner = true;
        //Check that if an owner is defined, it exists, and that it contains the item in its list. NB! ItemLists must then only check that their items point the themselves and not another list
//        if (checkOwner && item.getOwner() != null) {
        ASSERT.that(allItemFromParseCleaned, "run this only after checking all items from parse");
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemAndListCommonInterface owner = item.getOwner();
//        if (owner != null) {
//            if (notOnParseServer((ParseObject) owner)) {
//                log("Item \"" + item.getText() + "\" with bad ref to Owner objectId=" + ((ParseObject) item.getOwner()).getObjectIdP(), logLevel);
//                if (executeCleanup) {
//                    item.setOwner(null);
//                }
//            } else if (owner.getItemIndex(item) == -1) {
//                log("Not in owner: Item \"" + item.getText() + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
//                if (executeCleanup) {
//                    //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
//                    item.setOwner(null); //hack to avoid that addToList below complains that owner is already defined
////                    item.getOwner().addToList(item);
//                    owner.addToList(item);
////                    item.getOwner().getList(item);
//                }
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (item.getOwner() != null && !item.getOwner().getList().contains(item))
////                    cleanUpMissingInclusionInList("Item \"" + item + "\" owner's \"" + item.getOwner() + "\" does not reference the item (owner's list=" + item.getOwner().getList() + ")",
////                            item, item.getOwner().getList())) {
////                if (executeCleanup) {
////                    DAO.getInstance().save((ParseObject) item.getOwner());
////                }
////            };
////</editor-fold>
//        }
//</editor-fold>

        checkOwner(item, correctOwner, executeCleanup);

        if (item.isTemplate() != makeTemplate) {
            log("Item \"" + item + (makeTemplate ? "\" should be template but is not" : "\" should NOT be template but is"));
            logAction(makeTemplate ? "Set the Item to template" : "Set Item as NOT template");
            if (executeCleanup) {
                item.setTemplate(makeTemplate);
            }
        }

        //Check repeat rule exists
        RepeatRuleParseObject repeatRule = item.getRepeatRuleN();
        if (repeatRule != null) {
            if (notOnParseServer((ParseObject) repeatRule)) {
                log("Item \"" + item + "\" with bad ref to RepeatRule objectId=" + ((ParseObject) item.getRepeatRuleN()).getObjectIdP(), logLevel);
                if (executeCleanup) {
                    item.setRepeatRule(null); //remove reference to inexisting RepeatRule
                }
            } else {
                if (!repeatRule.getListOfDoneInstances().contains(item) && !repeatRule.getListOfUndoneInstances().contains(item)) {
                    if (item.isDone()) {
                        log("Item \"" + item + "\" is Done but not in RepeatRule's Done list,  RepeatRule=" + repeatRule);
                        if (executeCleanup) {
                            List done = repeatRule.getListOfDoneInstances();
                            done.add(item);
                            repeatRule.setListOfDoneInstances(done);
                            dao.saveNew(repeatRule);
                        }
                    } else {
                        repeatRule.getListOfUndoneInstances().add(item);
                        log("Item \"" + item + "\" is Not Done but not in RepeatRule's Undone list,  RepeatRule=" + repeatRule);
                        if (executeCleanup) {
                            List undone = repeatRule.getListOfUndoneInstances();
                            undone.add(item);
                            repeatRule.setListOfUndoneInstances(undone);
                            dao.saveNew(repeatRule);
                        }
                    }
                }
            }
        }

        //FILTERSORTDEF
        FilterSortDef filterSortDef = item.getFilterSortDef();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null) {
//            if (notOnParseServer((ParseObject) filterSortDef)) {
//                log("Item \"" + item + "\" with bad ref to FilterSortDef= " + filterSortDef);
//                if (executeCleanup) {
//                    item.setFilterSortDef(null); //remove reference to inexisting RepeatRule
//                }
//            } else {
//                filterSortDefReferenced.add(filterSortDef);
//            }
//        }
//</editor-fold>
        if (!cleanUpReferencedFilterSortDef(item, filterSortDef, executeCleanup)) {
            filterSortDefReferenced.add(filterSortDef);
        }

        //INterrupted tasks
        if (item.getTaskInterrupted() != null && notOnParseServer((ParseObject) item.getTaskInterrupted())) {
            log("Item \"" + item.getText() + "\" with bad ref to TaskInterrupted, objectId=" + ((ParseObject) item.getTaskInterrupted()).getObjectIdP());
            if (executeCleanup) {
                item.setTaskInterrupted(null); //remove reference to inexisting Item
            }
        }

        //Dependent tasks
        if (item.getDependingOnTask() != null && notOnParseServer((ParseObject) item.getDependingOnTask())) {
            log("Item \"" + item.getText() + "\" with bad ref to DependingOnTask, objectId=" + ((ParseObject) item.getDependingOnTask()).getObjectIdP());
            if (executeCleanup) {
                item.setDependingOnTask(null); //remove reference to inexisting Item
            }
        }

        //Original source (eg when copied from template)
        if (item.getSource() != null && notOnParseServer((ParseObject) item.getSource())) {
            log("Item \"" + item.getText() + "\" with bad ref to Original source, objectId=" + ((ParseObject) item.getSource()).getObjectIdP());
            if (executeCleanup) {
                item.setSource(null); //remove reference to inexisting Item
            }
        }

        //CATEGORIES
//        cleanUpBadObjectReferences(item.getCategories()); 
        //remove links to non-existing Categories
        //check that categories refer back (Categories will check the other way: that they are referenced by their Items)
        //check that templates are NOT referenced in any categories!
        for (Category cat : item.getCategories()) {
            //DON'T test for templates (the template should not be in the category
            if (item.isTemplate() || makeTemplate) {
                if (cat.contains(item)) {
                    log("Template \"" + item + "\" is wrongly referenced in Category \"" + cat + "\"");
                    logAction("Removing template from category");
                    if (executeCleanup) {
                        cat.removeItemFromCategory(item, false);
                        dao.saveNew((ParseObject) cat);
                    }
                }
            } else {
                List catItems = cat.getListFull();
//                if ((list = cleanUpMissingInclusionInList("Item \"" + item + "\" has Category \"" + cat + "\" but category does not reference the item, category's list (" + cat.getList() + ")", item, list2)) != null) {
//                cat.addItemToCategory(item, false); //add item to category //NO, done in cleanupMissing
                if (!catItems.contains(item)) {
                    log("Item not in its Category, item= \"" + item + "\", Category= \"" + cat);
                    logAction("Adding Item to Category");
                    if (executeCleanup) {
                        cat.addItemToCategory(item, false);
                        dao.saveNew((ParseObject) cat);
                    }
                }
            }
        }
        List catItems = item.getCategories();
        if (cleanUpDuplicatesInList("Item \"" + item + "\" (ObjId=" + item.getObjectIdP() + ") list of categories", catItems, executeCleanup)) {
            if (executeCleanup) {
                item.setCategories(catItems);
            }
        }

        //SUBTASKS
        //duplicate subtasks
        List<Item> subtasks = item.getListFull();

        if (checkListForNotInParseSoftDeletedReferenceOwner(item, (List) subtasks, executeCleanup)) {
            if (executeCleanup) {
                item.setList(subtasks); //update in case subtasks were removed
            }
        }

        if (cleanUpDuplicatesInList("Item \"" + item + "\" has duplicated subtask (subtasks=" + subtasks + ")", subtasks, executeCleanup)) {
            if (executeCleanup) {
                item.setList(subtasks); //update in case subtasks were removed
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        int i = 0;
//        while (i < subtasks.size()) {
//            Item subtask = subtasks.get(i);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (subtask.getOwner() == null) {
////                log("Item \"" + item + "\"'s subtask \"" + subtask + "\" has owner==null", logLevel);
////                if (executeCleanup) {
////                    subtask.setOwner(item);
////                    dao.saveNew(subtask, false);
////                }
//////                i++;
//////            } else if (!subtask.getOwner().equals(item)) {
////            } else //            if (false && !subtask.getOwner().equals(item)) { //do not this this for subtasks - incompatible with the check above
////            if (subtask.getOwner() != item) { //subtask ownership 'wins' over other ownerships (e.g. if subtask is also in a list or a project at a higher level
////                log("Item \"" + item + "\"'s subtask \"" + subtask + "\" has another owner==\"" + subtask.getOwner() + "\"", logLevel);
////                if (executeCleanup) {
//////                    subtasks.remove(subtask); //
////                    subtask.setOwner(item); //force owner of subtask to this item
//////                    item.setList(subtasks); //no need to setList since subtask is already in list of subtasks, so no change
////                    dao.saveNew(subtask, false); //save new owner
//////                } else {
////                }
//////                    i++;
//////            } else {
//////                i++;
////            }
////</editor-fold>
//            cleanUpItem(subtask, makeTemplate, executeCleanup); //iterate down the hierarchy
//            i++;
//        }
//</editor-fold>
        for (Item subtask : subtasks) {
            cleanUpItem(subtask, item, makeTemplate, executeCleanup); //iterate down the hierarchy
        }

        //WORKSLOTS : WorkSlots point to their owner, NOT the other way around, so nothing to clean up here 
        cleanUpWorkSlotList(item, executeCleanup);

//finally save
        if (executeCleanup) {
//            item.setList(subtasks);
            dao.saveNew(item);
        }

    }
//<editor-fold defaultstate="collapsed" desc="comment">
    //    private void cleanUpBadObjectReferencesCategory(Category category) {
    
    /**
     *
     * @param item
     * @param checkOwner check if Owner exists and if item is included in the
     * owner's list
     */
//    void cleanUpBadObjectReferencesItemXXX(Item item) { //, boolean checkOwner) {
////        boolean checkOwner = true;
//        //Check that if an owner is defined, it exists, and that it contains the item in its list. NB! ItemLists must then only check that their items point the themselves and not another list
////        if (checkOwner && item.getOwner() != null) {
//        assert allitemListsChecked && templatesChecked;
//        ItemAndListCommonInterface owner = item.getOwner();
//        if (owner != null) {
//            if (owner.getItemIndex(item) == -1) {
////                Log.p("CLEANUP: Not in owner: Item \"" + item.getText() + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
//                log("Not in owner: Item \"" + item + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
//                if (executeCleanup) {
//                    //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
//                    item.setOwner(null); //hack to avoid that addToList below complains that owner is already defined
////                    item.getOwner().addToList(item);
//                    owner.addToList(item);
////                    item.getOwner().getList(item);
//                }
//            }
//        } else {
//            log("No owner: Item \"" + item + "\" owner is null (add to Inbox)", logLevel);
//            if (executeCleanup) {
//                //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
//                Inbox.getInstance().addToList(item);
////                owner.addToList(item);
//                dao.saveNew((ParseObject) Inbox.getInstance(), false);
//            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        //Check repeat rule exists
////        if (item.getRepeatRuleN() != null && notOnParseServer((ParseObject) item.getRepeatRuleN())) {
////            log("Item \"" + item.getText() + "\" with bad ref to RepeatRule objectId=" + ((ParseObject) item.getRepeatRuleN()).getObjectIdP(), logLevel);
////            if (executeCleanup) {
////                item.setRepeatRule(null); //remove reference to inexisting RepeatRule
////            }
////        }
////
////        //INterrupted tasks
////        if (item.getTaskInterrupted() != null && notOnParseServer((ParseObject) item.getTaskInterrupted())) {
////            log("Item \"" + item.getText() + "\" with bad ref to TaskInterrupted, objectId=" + ((ParseObject) item.getTaskInterrupted()).getObjectIdP(), logLevel);
////            if (executeCleanup) {
////                item.setTaskInterrupted(null); //remove reference to inexisting Item
////            }
////        }
////
////        //Dependent tasks
////        if (item.getDependingOnTask() != null && notOnParseServer((ParseObject) item.getDependingOnTask())) {
////            log("Item \"" + item.getText() + "\" with bad ref to DependingOnTask, objectId=" + ((ParseObject) item.getDependingOnTask()).getObjectIdP(), logLevel);
////            if (executeCleanup) {
////                item.setDependingOnTask(null); //remove reference to inexisting Item
////            }
////        }
////
////        //Original source (eg when copied from template)
////        if (item.getSource() != null && notOnParseServer((ParseObject) item.getSource())) {
////            log("Item \"" + item.getText() + "\" with bad ref to Original source, objectId=" + ((ParseObject) item.getSource()).getObjectIdP(), logLevel);
////            if (executeCleanup) {
////                item.setSource(null); //remove reference to inexisting Item
////            }
////        }
////CATEGORIES
////        cleanUpBadObjectReferences(item.getCategories()); //remove links to non-existing Categories
////        for (Category cat : item.getCategories()) {
//////            List list;
////            //DON'T test for templates (the template should not be in the category
////            if (item.isTemplate()) {
////                if (cat.contains(item)) {
////                    log("Template \"" + item.getText() + "\" is wrongly referenced in Category \"" + cat, logLevel);
////                    if (executeCleanup) {
////                        cat.removeItemFromCategory(item, false);
////                    }
////                }
////            } else {
////                List list2 = cat.getListFull();
//////                if ((list = cleanUpMissingInclusionInList("Item \"" + item + "\" has Category \"" + cat + "\" but category does not reference the item, category's list (" + cat.getList() + ")", item, list2)) != null) {
//////                cat.addItemToCategory(item, false); //add item to category //NO, done in cleanupMissing
////                if (list2 != null && !list2.contains(item)) {
////                    log("Item \"" + item
////                            + "\" (ObjId=" + item.getObjectIdP() + ") has Category \"" + cat
////                            + "\" (ObjId=" + cat.getObjectIdP() + ") but category does not reference the item, category's list (" + cat
////                            .getListFull() + ")", logLevel
////                    );
////                    if (executeCleanup) {
////                        list2.add(item);
////                        cat.setList(list2);
////                    }
////                }
////            }
////        }
////        List list3 = item.getCategories();
////        if (cleanUpDuplicatesInList("Item \"" + item + "\" (ObjId=" + item.getObjectIdP() + ") list of categories", list3, executeCleanup) && executeCleanup) {
//////        if (executeCleanup) {
////            item.setCategories(list3);
////        }
////        //SUBTASKS
////        List<Item> subtasks = item.getListFull();
//////        for (Item subtask : subtasks) {
////        int i = 0;
////        while (i < subtasks.size()) {
////            Item subtask = subtasks.get(i);
////            if (subtask.getOwner() == null) {
////                log("Item \"" + item + "\"'s subtask \"" + subtask + "\" has owner==null", logLevel);
////                if (executeCleanup) {
////                    subtask.setOwner(item);
////                    dao.saveNew(subtask, false);
////                }
//////                i++;
//////            } else if (!subtask.getOwner().equals(item)) {
////            }
////            if (false && !subtask.getOwner().equals(item)) { //do not this this for subtasks - incompatible with the check above
////                log("Item \"" + item + "\"'s subtask \"" + subtask + "\" has another owner==\"" + subtask.getOwner() + "\"", logLevel);
////                if (executeCleanup) {
//////                    subtasks.remove(subtask); //
////                    subtask.setOwner(item); //force owner of subtask to this item
////                    item.setList(subtasks);
//////                } else {
////                }
//////                    i++;
//////            } else {
//////                i++;
////            }
////            i++;
////
////        }
////
////        cleanUpDuplicatesInList("Item \"" + item + "\" has duplicated subtask (subtasks=" + subtasks + ")", subtasks, executeCleanup);
////        //finally save
////        if (executeCleanup) {
////            item.setList(subtasks);
////            dao.saveNew(item, false);
////        }
////Workslots : WorkSlots point to their owner, NOT the other way around, so nothing to clean up here
////</editor-fold>
//    }
//</editor-fold>
    private String itemToString(Item item) {
        return "\"" + item.getText() + "\" [" + item.getObjectIdP() + "]";
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean cleanUpItemsWithNoValidOwnerXXX(boolean executeCleanup) {
//        boolean issuesFound = false;
//        List<Item> items = dao.getAllItems(true, false, false, false); //include templates, fetch from cache
//        ItemList lostItems = new ItemList("Recovered items " + MyDate.formatDateTimeNew(new MyDate()), false);
//        for (Item item : items) {
//            if (item.getOwner() == null) { //getOwner also returns null for non-existant owners (e.g. a hard-deleted owner)
//                Log.p("Item " + itemToString(item) + " on server has no valid owner" + (executeCleanup ? ", adding to list \"" + lostItems.getText() + "\"" : ""));
//                if (executeCleanup) {
////                    item.setOwner(lostItems);
//                    lostItems.addToList(item);
////                    dao.saveInBackground(item); //save new owner //CAN'T do here because lostItems list is not saved yet so no ObjId
//                }
//                issuesFound = true;
//            }
//            //TODO: if item has a (non-existant) owner, then create a list named with that ObjectId and store all lost items together there (quite complicated to develop)
//        }
//        if (executeCleanup && lostItems.size() > 0) {
//            dao.saveNew((ParseObject) lostItems, false); //first save new list to have a valid objectId!!
//            dao.saveNew(lostItems.getListFull(), false); //THEN save all updated items
////            ItemListList.getInstance().addToList(0, lostItems); //add to beginning of lists
//            ItemListList.getInstance().addToList(lostItems, false); //add to beginning of lists
//            dao.saveNew((ParseObject) ItemListList.getInstance(), false);
//        }
//        return issuesFound;
//    }
//
//    private void cleanUpBadObjectReferencesInListOfCategoriesOrItemListsXXX(String what, String element, List<List<ParseObject>> listOfLists) {
////        List<List<ParseObject>> listOfList = getCategoryList();
//        if (false) {
//            log("number elements in list = " + listOfLists.size(), logLevel);
//        }
//        cleanUpDuplicatesInItemListOrCategory(element, itemListOrCategory, executeCleanup);
//        //        for (int i = 0, size = listOfList.size(); i < size; i++) {
//        int i = 0;
//        while (i < listOfLists.size()) {
//            //TODO!!!! change for loop to a construction that works if the list is being altered as it is traversed: http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
//            Object catOrItemList = listOfLists.get(i);
//
//            if (notOnParseServer((ParseObject) catOrItemList)) {
////                if (listOfList instanceof ItemAndListCommonInterface) {
////                log("Bad ref in \"" + ((ItemAndListCommonInterface) listOfList).getText() + "\" to objectId=" + ((ParseObject) listOfList).getObjectIdP());
//                log(what + " contains " + element + " not on ParseServer, " + what + "= \"" + listOfLists + "\", " + element + "= \"" + catOrItemList + "\"");
////                } else {
////                    log("Bad ref in " + listOfList + " to objectId=" + ((ParseObject) listOfList).getObjectId(), logLevel);
////                }
//                logAction("Remove " + element + " from " + what);
//                if (executeCleanup) {
//                    listOfLists.remove(i);
//                } else {
//                    i++;
//                }
//            } else {
//                if (((ItemAndListCommonInterface) catOrItemList).getOwner() == null) {
//                    log("Missing ref in ItemList/Category \"" + ((ItemAndListCommonInterface) catOrItemList).getText() + "\" (ObjId=" + ((ParseObject) catOrItemList).getObjectIdP() + ") to its owner ListOfCategories/ListOfItemLists in \"", logLevel);
//                    ((ItemAndListCommonInterface) catOrItemList).setOwner((ItemAndListCommonInterface) listOfLists);
//                }
//                if (catOrItemList instanceof Category) {
//                    cleanUpCategory((Category) catOrItemList);
//                } else if (catOrItemList instanceof ItemList) {
//                    cleanUpItemList((ItemList) catOrItemList);
//                }
//                i++;
//            }
//        }
//        if (executeCleanup) {
//            dao.saveNew((ParseObject) listOfLists);
//        }
//    }
//
////    private void cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(List<List<ParseObject>> listOfList) {
//    private void cleanUpBadObjectReferencesInListOfCategoriesXXX() {
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists("CategoryList", "Category", dao.getCategoryList());
//    }
//</editor-fold>
    //FILTERSORTDEFS
    boolean allFilterSortDefsCleaned;
//    private boolean belongsTo(FilterSortDef filter, )

    /**
     * remove any filters from Parse server which are not pointed by an
     * Item/ItemList/Category (NO (done in each cleanUp: and remove any filters
     * from Item/ItemList/Category which does not exist)
     *
     * @param listOfFilters
     */
    private void cleanUpFilterSortDefs() {
        ASSERT.that(allItemFromParseCleaned && allItemListsCleaned && allCategoriesCleaned);
        setLogPrefix("FilterSortDefs");
        List<FilterSortDef> allFiltersFromParse = dao.getAllFilterSortDefsFromParse();
        allFiltersFromParse.removeAll(filterSortDefReferenced); //only leave unreferenced filters
        dao.deleteAll((List)allFiltersFromParse, true, false); //        delete unreferenced filters;
//<editor-fold defaultstate="collapsed" desc="comment">
//        List<ParseObject> updatedOwners = new ArrayList();
//        if (false) {
//            log("number elements in list = " + filterList.size(), logLevel);
//        }
//
//        HashSet<String> filtersObjIds = new HashSet();
//        for (FilterSortDef f : filterList) {
//            filtersObjIds.add(f.getObjectIdP());
//        }
//
//        //construct hashmpas to effectively search for elements that point to a filter
////        Map<FilterSortDef, Category> catsWithFilter = new HashMap();
//        Map<String, Category> catsWithFilter = new HashMap();
////        for (Category cat : CategoryList.getInstance().getList()) {
//        for (Category cat : CategoryList.getInstance().getListFull()) {
//            FilterSortDef filter = cat.getFilterSortDefN();
//            if (filter != null && filter.getObjectIdP() != null) {
//                catsWithFilter.put(filter.getObjectIdP(), cat);
//                if (!filtersObjIds.contains(filter.getObjectIdP())) { //cat's filter does not exist, so remove it from cat
//                    log("Category (ObjId=" + cat.getObjectIdP() + ") references FilterSortDef (" + filter.getObjectIdP() + ") NOT on Parse server", logLevel);
//                    cat.setFilterSortDef(null);
//                    updatedOwners.add(cat);
//                }
//            }
//        }
//
////        Map<FilterSortDef, ItemList> itemListsWithFilter = new HashMap<>();
//        Map<String, ItemList> itemListsWithFilter = new HashMap<>();
////        for (ItemList itemList : ItemListList.getInstance().getList()) {
//        for (Object o : ItemListList.getInstance().getListFull()) {
//            ItemList itemList = (ItemList) o;
//            FilterSortDef filter = itemList.getFilterSortDefN();
//            if (filter != null && filter.getObjectIdP() != null) {
//                itemListsWithFilter.put(filter.getObjectIdP(), itemList);
//                if (!filtersObjIds.contains(filter.getObjectIdP())) { //cat's filter does not exist, so remove it from cat
//                    log("ItemList (ObjId=" + itemList.getObjectIdP() + ") references FilterSortDef (" + filter.getObjectIdP() + ") NOT on Parse server", logLevel);
//                    itemList.setFilterSortDef(null);
//                    updatedOwners.add(itemList);
//                }
//            }
//        }
//
//        //TODO Items do not implement filters yet
////        Map<FilterSortDef, Item> itemsWithFilter = new HashMap<>();
//        Map<String, Item> itemsWithFilter = new HashMap<>();
//        for (Item item : dao.getAllItems()) {
//            FilterSortDef filter = item.getFilterSortDefN();
//            if (filter != null && filter.getObjectIdP() != null) {
//                itemsWithFilter.put(filter.getObjectIdP(), item);
//                if (!filtersObjIds.contains(filter.getObjectIdP())) { //cat's filter does not exist, so remove it from cat
//                    log("Item (ObjId=" + item.getObjectIdP() + ") references FilterSortDef (" + filter.getObjectIdP() + ") NOT on Parse server", logLevel);
//                    item.setFilterSortDef(null);
//                    updatedOwners.add(item);
//                }
//            }
//        }
//
//        //for every filter, check if it is referenced and if not delete it
//        for (int i = 0, size = filterList.size(); i < size; i++) {
//            FilterSortDef filter = filterList.get(i);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (filter.getFilteredObjectId() == null || filter.getFilteredObjectId().equals("")) {
////                Log.p("CLEANUP: FilterSortDef (ObjId=" + filter.getObjectIdP() + ") without valid ref to FilteredObjectId (" + filter.getFilteredObjectId() + ")", logLevel);
////                if (executeCleanup) {
////                    delete(filter); //delete filters without ref to both objectId and Screen
////                }
////            } else {
////</editor-fold>
//            String filterObjId = filter.getObjectIdP();
//            if (itemListsWithFilter.get(filterObjId) == null
//                    && itemsWithFilter.get(filterObjId) == null
//                    && catsWithFilter.get(filterObjId) == null) { //no refs to filter
//                log("FilterSortDef (ObjId=" + filter.getObjectIdP() + ") is not referenced by any Category/ItemList/Item", logLevel);
//                if (executeCleanup) {
//                    dao.delete(filter, true, false); //delete filters without ref to both objectId and Screen
//                }
//            };
//            if (executeCleanup) {
//                dao.saveNew(updatedOwners); //delete filters without ref to both objectId and Screen
//            }
//        }
//</editor-fold>
        setLogPrefix("");
        allFilterSortDefsCleaned = true;
    }

    //WORKSLOTS
    boolean allWorkSlotsCleaned;

    boolean cleanUpWorkSlot(WorkSlot workSlot, boolean executeCleanup) {
        boolean modified = false;
        //repeatRule
        RepeatRuleParseObject repeatRule = workSlot.getRepeatRuleN();
        if (repeatRule != null) {
            //Check repeat rule exists
            if (notOnParseServer((ParseObject) repeatRule)) {
                log("WorkSlot \"" + workSlot + "\" with bad ref to RepeatRule objectId=" + ((ParseObject) workSlot.getRepeatRuleN()).getObjectIdP());
                if (executeCleanup) {
                    workSlot.setRepeatRule(null); //remove reference to inexisting RepeatRule
                    modified = true;
                    dao.saveNew(workSlot);
                }
            } else {
                if (!repeatRule.getListOfDoneInstances().contains(workSlot) && !repeatRule.getListOfUndoneInstances().contains(workSlot)) {
                    if (workSlot.isInThePast(MyDate.currentTimeMillis())) {
                        log("WorkSlot \"" + workSlot + "\" is Past but not in RepeatRule's Done list,  RepeatRule=" + repeatRule);
                        if (executeCleanup) {
                            List done = repeatRule.getListOfDoneInstances();
                            done.add(workSlot);
                            repeatRule.setListOfDoneInstances(done);
                            dao.saveNew(repeatRule);
                        }
                    } else {
                        log("WorkSlot \"" + workSlot + "\" is Future but not in RepeatRule's Undone list,  RepeatRule=" + repeatRule);
                        if (executeCleanup) {
                            List undone = repeatRule.getListOfUndoneInstances();
                            undone.add(workSlot);
                            repeatRule.setListOfUndoneInstances(undone);
                            dao.saveNew(repeatRule);
                        }
                    }
                }
            }
        }

        //Original source (eg when copied from template)
        if (workSlot.getSource() != null && notOnParseServer((ParseObject) workSlot.getSource())) {
            log("Item \"" + workSlot + "\" with bad ref to Original source, objectId=" + ((ParseObject) workSlot.getSource()).getObjectIdP());
            if (executeCleanup) {
                workSlot.setSource(null); //remove reference to inexisting Item
                modified = true;
                dao.saveNew(workSlot);
            }
        }

        return modified;
    }

    /**
     * check correct owner, remove duplicates, check repeatRule(?)
     *
     * @param owner
     * @param executeCleanup
     * @return true if owner was modified
     */
    boolean cleanUpWorkSlotList(ItemAndListCommonInterface owner, boolean executeCleanup) {
        //TODO check if there are other elements which has a given workSlot in their list -> highly unlikely
        boolean modified = false;
        List<WorkSlot> workslots = owner.getWorkSlotsFromParseN();

        if (cleanUpDuplicatesInList(owner + "/WorkSlots", workslots, executeCleanup)) {
            if (executeCleanup) {
                owner.setWorkSlotsInParse(workslots);
                dao.saveNew((ParseObject) owner);
                modified = true;
            }
        }

        if (checkListForNotInParseSoftDeletedReferenceOwner(owner, (List) workslots, executeCleanup)) {
            if (executeCleanup) {
                owner.setWorkSlotsInParse(workslots);
                dao.saveNew((ParseObject) owner);
                modified = true;
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        WorkSlotList workSlotList = owner.getWorkSlotListN();
//        if (workSlotList == null||workS) {
//            return false;
//        }
//        boolean hasDuplicates = false;
//        List<WorkSlot> workSlots = workSlotList.getWorkSlotListFull();
//        List<WorkSlot> uniques = new ArrayList<>();
//        int i = 0;
//        while (i < workSlots.size()) {
//            WorkSlot workSlot = workSlots.get(i);
//            if (uniques.contains(workSlot)) {
//                hasDuplicates = true;
//                log("WorkSlot (ObjId=" + workSlot.getObjectIdP() + ") without valid ref to OwnerItemList, OwnerItem and RepeatRule. startTime=" + workSlot.getStartTimeD() + ", description=" + workSlot.getText() + ", adj.duration(minutes)=" + workSlot.getDurationInMinutes(), logLevel);
////                workSlots.remove(i);
//                //no i++!
//            } else {
//                uniques.add(workSlot);
//                if (workSlot.getOwner() == null) {
//                    log("WorkSlot (ObjId=" + workSlot.getObjectIdP() + ") without valid ref to OwnerItemList, OwnerItem and RepeatRule. startTime=" + workSlot.getStartTimeD() + ", description=" + workSlot.getText() + ", adj.duration(minutes)=" + workSlot.getDurationInMinutes(), logLevel);
//                    if (executeCleanup) {
//                        workSlot.setOwner(owner);
//                        dao.saveNew(workSlot, false);
//                    }
//                } else if (workSlot.getOwner() != owner) {
//                    log("WorkSlot (ObjId=" + workSlot.getObjectIdP() + ") does not have the Owner that owns it. Owner=" + owner + ", workSLot.owner=" + workSlot.getOwner(), logLevel);
//                    if (executeCleanup) {
//                        workSlot.setOwner(owner);
//                        dao.saveNew(workSlot, false);
//                    }
//                }
//            }
//            cleanUpWorkSlot(workSlot, executeCleanup);
//            i++;
//        }
//</editor-fold>
        for (WorkSlot workSlot : workslots) {
            cleanUpWorkSlot(workSlot, executeCleanup);
        }

        return modified;
    }

    /**
     *
     * @param executeCleanup
     */
    void cleanUpWorkSlots(boolean executeCleanup) {
        setLogPrefix("WorkSlots");
//        WorkSlotList listOfWorkSlots = dao.getAllWorkSlotsFromParse();
        List<WorkSlot> listOfWorkSlots = dao.getAllWorkSlotsFromParse();
        List updatedWorkSlots = new ArrayList<>();
        if (false) {
            log("number elements in list = " + listOfWorkSlots.size(), logLevel);
        }
        for (int i = 0, size = listOfWorkSlots.size(); i < size; i++) {
            WorkSlot workSlotOrg = listOfWorkSlots.get(i);
            WorkSlot workSlot = (WorkSlot) dao.fetchIfNeededReturnCachedIfAvail(workSlotOrg);
            String WSObjId = workSlot.getObjectIdP();
            //test if workSlot has multiple owners (Category, Item, ItemList) - NOT necessary since the priority is defined by getOwner()
//            if (workSlot.getOwner())
            boolean deleteWorkSlot = true; //if all owners are missing, then remove
            boolean noOwner = false; //if all owners are missing, then remove
            boolean repeatRuleNotFound = false; //if all owners are missing, then remove
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (workSlot.getOwnerList() != null) {// || workSlot.getOwnerList().equals("")) {
////                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerList(" + workSlot.getOwnerList() + ")", logLevel);
//                clean = false;
//            }
//            if (workSlot.getOwnerItem() != null) {// || workSlot.getOwnerItem().equals("")) {
////                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
//                clean = false;
//            }
//</editor-fold>
            ItemAndListCommonInterface owner = workSlot.getOwner();
            if (owner == null || notOnParseServer((ParseObject) owner)) {// || workSlot.getOwnerItem().equals("")) {
//                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
//                deleteWorkSlot = false;
                noOwner = true;
                //TODO!!! check if any RepeatRule references the workslot but it doesn't reference the RR
                //TODO!!! check if any Item/ItemList/Category references the workslot but it doesn't reference it back
            } else {
                WorkSlotList workSlotList = owner.getWorkSlotListN(); //true: fetch from cache so contains() below works
                List<WorkSlot> workslots;
                if (workSlotList != null && (workslots = workSlotList.getWorkSlotListFull()) != null && !workslots.contains(workSlot)) {
                    log("WorkSlot \"" + workSlot.toString() + "\" not in owner's list of workSlots, owner=" + owner + ", owner workSlots=" + workSlotList);
                    if (executeCleanup) {
                        owner.addWorkSlot(workSlot);
                        DAO.getInstance().saveNew((ParseObject) owner);
                    }
                }
            }

            //Check repeat rule exists
            RepeatRuleParseObject repeatRule = workSlot.getRepeatRuleN();
            if (repeatRule != null) {
                String objId = repeatRule.getObjectIdP();
                if (notOnParseServer((ParseObject) repeatRule)) {
                    log("WorkSlot \"" + workSlot.toString() + "\" with bad ref to RepeatRule objectId=" + objId, logLevel);
                    if (executeCleanup) {
                        workSlot.setRepeatRule(null); //remove reference to inexisting RepeatRule
                        updatedWorkSlots.add(workSlot);
                    }
                    repeatRuleNotFound = true;
                } //else: other issues cleaned when going through RepeatRule list from Parse
            }

//            if (false && workSlot.getRepeatRuleN() == null) {// || workSlot.getRepeatRule().equals("")) {
////                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
////                deleteWorkSlot = false;
//                repeatRuleNotFound = true;
//            }
//            if (deleteWorkSlot) {
            if (noOwner && repeatRuleNotFound) {
                log("WorkSlot no owner and not referenced from RepeatRule: workSLot=" + workSlot + ") with null as Owner.", logLevel);
//                try {
                if (executeCleanup) {
                    dao.delete(workSlot, true, false); //delete filters without ref to both objectId and Screen
                }//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
            }
        }
        if (executeCleanup) {
            dao.saveNew(updatedWorkSlots);
        }
        setLogPrefix("");
        allWorkSlotsCleaned = true;
    }

    //EVERYTHING
    public void setLog(boolean executeCleanup) {
        this.loggingOn = executeCleanup;
    }

    //--------  NEW cleanup procedures  -----------------------------------------------
    /**
     *
     * @param executeCleanup if false, just list the detected inconsistencies
     * but don't change them
     */
    public void cleanUpEverything(boolean executeCleanup) {
        logLevel = Log.ERROR;
//        int oldParseLogLevel = Logger.getInstance().getLogLevel();

        Logger.getInstance().setLogLevel(Log.ERROR);
        setLog(executeCleanup);

        log("-----------------------------------------------------", logLevel);
        log("STARTING (execute=" + executeCleanup + ") ----------------------------", logLevel);

        log("-----------------------------------------------------", logLevel);
        log("ITEMLISTS", logLevel);
        log("-----------------------------------------------------", logLevel);
        cleanUpAllItemLists(executeCleanup);

        log("-----------------------------------------------------", logLevel);
        log("TEMPLATES", logLevel);
        //TODO!!!: check that tasks in AllTemplates list are all marked as templates!!
        log("-----------------------------------------------------", logLevel);
        cleanUpTemplates(executeCleanup);
//        cleanUpWorkSlots(getAllWorkSlotsFromParse()); //Clean up links to removed ItemLists
//        TemplateList templateList = getTemplateList();
//        cleanUpAllItemListsFromParse(getAllTemplatesByQuery(), templateList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(templateList); //Clean up links to removed Categories
//        Log.p("CLEANUP: NOT DONE YET", logLevel);
//        Log.p("CLEANUP: -----------------------------------------------------", logLevel);

        log("-----------------------------------------------------", logLevel);
        log("CATEGORIES", logLevel);
        log("-----------------------------------------------------", logLevel);
//        CategoryList categoryList = getCategoryList();
//        cleanUpAllCategoriesFromParse(getAllCategoriesFromParse(), categoryList); 
        cleanUpAllCategories(executeCleanup);
//        cleanUpAllCategoriesFromParse(); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(categoryList); //Clean up links to removed Categories

        //clean up items *after* itemlists, templatelist and categories have clean up
        log("-----------------------------------------------------", logLevel);
        log("ITEMS", logLevel);
        log("-----------------------------------------------------", logLevel);
        cleanUpItemsFromParse(executeCleanup); //Clean up all Items and their pointers first, true=includeTemplates

        log("-----------------------------------------------------", logLevel);
        log("REPEATRULES", logLevel);
        log("-----------------------------------------------------", logLevel);
        cleanUpRepeatRules(executeCleanup);

        //Clean up filters *after* cleaning up lists etc since we check if any list/project/category reference the filters
        log("-----------------------------------------------------", logLevel);
        log("FILTERS", logLevel);
        log("-----------------------------------------------------", logLevel);
        cleanUpFilterSortDefs(); //Clean up links to removed ItemLists
//        cleanUpFilterSortDefs(getAllFilterSortDefsFromParse()); //Clean up links to removed ItemLists

        log("-----------------------------------------------------", logLevel);
        log("WORKSLOTS", logLevel);
        log("-----------------------------------------------------", logLevel);
        cleanUpWorkSlots(executeCleanup); //NB! Execute cleanup of WorkSlots *after* clean up of all workslot owners (Item, WorkSlot, RepeatRule) to ensure that have added themselves as owners to WorkSlot (otherwise the workslots might get deleted here)
//        cleanUpWorkSlots(getAllWorkSlotsFromParse()); //Clean up links to removed ItemLists

        if (false&&executeCleanup) {
            DAO.getInstance().triggerParseUpdate(); //do all saves!
        }
        log("-----------------------------------------------------", logLevel);
        log("ALL CLEANUP FINISHED --------------------------------", logLevel);
        log("-----------------------------------------------------", logLevel);
        
        Dialog.show("","Cleanup finished","OK",null);
//        cleanUpWorkSlots(getAllWorkSlots()); //Clean up links to removed ItemLists

//        Logger.getInstance().setLogLevel(oldParseLogLevel);
//        cleanUpBadObjectReferences(getAllProjects()); //handled under Items
    }

}
