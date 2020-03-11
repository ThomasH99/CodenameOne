/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.util.Logger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
public class CleanUpDataInconsistencies {

    private DAO dao;// = DAO.getInstance();
    //////////////////////////   CLEAN UP    /////////////////////////////
    //TODO!!!! a subtask may be in the project's subtask list, but have a different owner (e.g. a List) --> fix: if a project has a subtask with another owner, make the project the subtask's owner
//    private boolean badReference(ItemAndListCommonInterface parseObject) {
//        return badReference((ParseObject)parseObject);
//    }

    CleanUpDataInconsistencies(DAO dao) {
        this.dao = dao;
    }

    private List removeDuplicates(List list) {
        List noDups = new ArrayList();
        for (Object obj : list) {
            if (!noDups.contains(obj)) {
                noDups.add(obj);
            } else {
                Log.p("duplicate element=" + obj);
            }
        }
        return noDups;
    }

    /**
     * returns true if trying to fetch the parseObject from Parse fails (meaning
     * the object does not exists on the server)
     *
     * @param parseObject
     * @return
     */
    private boolean notOnParseServer(ParseObject parseObject) {
        if (parseObject == null) {
            return false;
        }
        assert parseObject.getObjectIdP() != null : "getObjectId==null";
        assert parseObject.getObjectIdP().length() != 0 : "getObjectId empty";
        try {
            parseObject.fetchIfNeeded();
        } catch (ParseException ex) {
            return true;
        }
        return false;
    }

    private boolean executeCleanup = false;
    private int logLevel = Log.ERROR; //use Log.ERROR to ensure the log is always done

    /**
     *
     * @param description
     * @param list
     */
    private void cleanUpCircularReferencesInHierarchy(String description, List list) {

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
    /**
     * find and remove any duplicates in a list
     *
     * @param description
     * @param list
     */
//    private boolean cleanUpDuplicatesInList(String description, List list, boolean executeCleanup) {
    private boolean cleanUpDuplicatesInList(String description, List inputList, boolean executeCleanup) {
        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
        if (inputList instanceof ItemList) {
            ItemList list = (ItemList) inputList;
            ArrayList cleanList = new ArrayList();
            int size = list.getSize();
            for (int i = 0; i < size; i++) {
                Object elt = list.getItemAt(i);
                if (cleanList.contains(elt)) {
                    Log.p("CLEANUP: " + description + " contains duplicate of \"" + elt + "\" at position " + i + " (list= " + list + ")", logLevel);
                } else {
                    cleanList.add(elt);
                }
            }
            boolean deletes = list.getSize() != cleanList.size();
            if (executeCleanup) {
                list.clear();
                list.addAll(cleanList);
            }
            return deletes;
        } else { //other lists than ItemList
            ArrayList cleanList = new ArrayList();
            int size = inputList.size();
            for (int i = 0; i < size; i++) {
                Object elt = inputList.get(i);

                if (cleanList.contains(elt)) {
                    Log.p("CLEANUP: " + description + " contains duplicate of \"" + elt + "\" at position " + i + " (list= " + inputList + ")", logLevel);
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
    }

    /**
     * returns true if obj was NOT in the list (to allow the caller to take
     * action to eg save the owner of the list)
     *
     * @param description
     * @param objWhichShouldBeInList
     * @param list
     * @return true if missing in list
     */
    private List cleanUpMissingInclusionInList(String description, Object objWhichShouldBeInList, List list) {
//        if (list == null || !list.contains(objWhichShouldBeInList)) {
        if (list != null && !list.contains(objWhichShouldBeInList)) {
            Log.p("CLEANUP: " + description, logLevel);
            if (executeCleanup) {
                list.add(objWhichShouldBeInList);
            }
            return list;
        }
        return null;
    }

    private void cleanUpBadObjectReferencesRepeatRule(RepeatRule repeatRule) {
        //TODO!!!!! fix problems in RepeatRules (eg ?? references to created undone instances)
        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
    }

    /**
     * cleans up a Category or ItemList. On the full list. Check it belongs to
     * ItemListList/CategoryList. Check that each item has the list/category as
     * owner. Check that each item in the list is on the server. Check if there
     * are any Items with this list as owner which are NOT in the list. TODO:
     * handle bags (use getItemAt/removeItem etc).
     *
     * @param itemList
     */
    private void cleanUpBadObjectReferencesItemList(ItemList itemList) {
//        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
        Log.p("CLEANUP: number elements in ItemList = " + itemList, logLevel);
        int i = 0;
        List<Item> itemsInList = itemList.getListFull();
        while (i < itemsInList.size()) {
            Item item = itemsInList.get(i);
            if (notOnParseServer(item)) {
                logError(itemList, item);
                if (executeCleanup) {
                    itemsInList.remove(i);
//                    i++; //DON'T INCREASE i if bad item ref was removed
                } else {
                    i++;
                }
            } else {
                cleanUpBadObjectReferencesItem(item); //we should clean up here, since an Item may only be found via its owner ItemList
                i++;
            }
        }
        if (itemList instanceof ParseObject && executeCleanup) {
            itemList.setList(itemsInList);
            dao.saveNew((ParseObject) itemList, false);
        }

    }

    private void cleanUpBadObjectReferencesCategory(Category category) {
//        ASSERT.that(false, "cleanUpBadObjectReferencesItemListOrCategory not implemented");
        Log.p("CLEANUP: number elements in Category = " + category, logLevel);
        int i = 0;
        List<Item> itemsInCategory = category.getListFull();
        while (i < itemsInCategory.size()) {
            Item itemInCat = itemsInCategory.get(i);
            if (notOnParseServer(itemInCat)) {
                logError(category, itemInCat);
                if (executeCleanup) {
                    itemsInCategory.remove(i);
//                    i++; //DON'T INCREASE i if bad item ref was removed
                } else {
                    i++;
                }
            } else {
                cleanUpBadObjectReferencesItem(itemInCat); //we should clean up here, since an Item may only be found via its owner ItemList
                i++;
            }
        }
        if (category instanceof ParseObject && executeCleanup) {
            category.setList(itemsInCategory);
            dao.saveNew((ParseObject) category, false);
        }

    }

//    private boolean cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(RepeatRuleParseObject repeatRule, List<ItemAndListCommonInterface> instanceList) {
    private boolean cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(RepeatRuleParseObject repeatRule, List<RepeatRuleObjectInterface> instanceList) {
        int i = 0;
        boolean listUpdated = false;

        while (i < instanceList.size()) {
            Object elt = instanceList.get(i);
            //if not on server, simply remove the element
            if (elt instanceof ParseObject && notOnParseServer((ParseObject) elt)) {
                if (elt instanceof Item) {
                    Log.p("CLEANUP: Item RepeatRule not on server: Item \"" + elt + "\" not on server for RepeatRule instance list for RepeatRule " + repeatRule);
                } else if (elt instanceof WorkSlot) {
                    Log.p("CLEANUP: WorkSlot Repeatrule not on server: WorkSlot \"" + elt + "\" not on server for RepeatRule instance list for RepeatRule " + repeatRule);
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

    private void logError(Object list, ParseObject element) {
        if (list instanceof ItemAndListCommonInterface) {
//                    Log.p("Bad object ref in class "+list.getClass()+" \"" + ((ItemAndListCommonInterface) list).getText() + "\" to objectId=" + ((ParseObject) list).getObjectId());
            Log.p("CLEANUP: Bad ref in \"" + ((ItemAndListCommonInterface) list).getText() + "\" (objectId=" + ((ParseObject) list).getObjectIdP() + ") to " + element + " objId=" + element.getObjectIdP(), logLevel);
        } else if (list instanceof ParseObject) {
//            Log.p("Bad object ref in " + list + " to objectId=" + ((ParseObject) list).getObjectId());
            Log.p("CLEANUP: Bad ref in " + list + " (objectId=" + ((ParseObject) list).getObjectIdP() + " to " + element + " objId=" + element.getObjectIdP(), logLevel);
        }
    }

    private boolean hasTemplateParent(Item item) {
        ItemAndListCommonInterface owner = item.getOwner();
        return (owner instanceof Item && (((Item) owner).isTemplate() || hasTemplateParent((Item) owner)));
    }

//    private void cleanUpBadObjectReferences(List<ParseObject> list) {
    /**
     * for every list or category in list, check if a category or itemlist
     * references non-existing items, or items that don't reference it back!
     *
     * @param list list of categories or itemlists
     */
    private void cleanUpBadObjectReferencesInItems(List<Item> list) {
        Log.p("CLEANUP: number elements in list = " + list.size(), logLevel);
        int i = 0;
        while (i < list.size()) {
            Item item = list.get(i);
            if (notOnParseServer(item)) {
                logError(list, item);
                if (executeCleanup) {
                    list.remove(i);
//                    i++; //DON'T INCREASE i if bad item ref was removed
                } else {
                    i++;
                }
            } else {
                cleanUpBadObjectReferencesItem(item);
                if (item.isTemplate() && !hasTemplateParent(item)) {
                    Log.p("CLEANUP: Parent not template: Template \"" + item.getText() + "\" wrongfully marked as Template although no parent task is a template", logLevel);
                    if (executeCleanup) {
                        item.setTemplate(false);
                        dao.saveNew(item, false);
                    }
                }
                i++;
            }
        }
        if (list instanceof ParseObject && executeCleanup) {
            dao.saveNew((ParseObject) list, false);
        }
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

    private void cleanUpItemList(ItemList itemList) {
        cleanUpBadObjectReferencesItemList(itemList);
        List items = itemList.getListFull();
        cleanUpDuplicatesInList("ItemList " + ((ItemAndListCommonInterface) itemList).getText(), items, executeCleanup);
        if (executeCleanup) {
            itemList.setList(items);
            dao.saveNew((ParseObject) itemList, false);
        }
    }

    private boolean makeAllSubTaskTemplatesAndRemoveDuplicates(Item template, boolean executeCleanup) {
        boolean changed = false;
        List<Item> subtasks = template.getListFull();
        if (subtasks != null && subtasks.size() > 0) {
            for (Item item : subtasks) {
                if (!item.isTemplate()) {
                    Log.p("CLEANUP: non-template \"" + item + "\" inside template \"" + item + " parseId=" + ((ParseObject) item).getObjectIdP());
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
    private void cleanUpTemplateList(TemplateList templateList, List<Item> topLevelTemplatesFromParse, boolean executeCleanup) {
//        if (templatesFromParse.size()!=templateList.size()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        List templates = templateList.getList();
//        for (int i = 0, size = topLevelTemplatesFromParse.size(); i < size; i++) {
//            if (!templates.contains(topLevelTemplatesFromParse.get(i))) {
//                templates.add(topLevelTemplatesFromParse.get(i));
//            }
//        }
//</editor-fold>
        //add any missing stored (top-level) templates to the list
        for (int i = 0, size = topLevelTemplatesFromParse.size(); i < size; i++) {
            if (!templateList.contains(topLevelTemplatesFromParse.get(i))) {
                templateList.add(topLevelTemplatesFromParse.get(i));
            }
        }

        //for all top-level templates
        int i = 0;
        while (i < templateList.getSize()) {
            Object template = templateList.get(i);
            //remove any objects not stored on parse server
            if (template instanceof ParseObject && notOnParseServer((ParseObject) template)) {
                Log.p("CLEANUP: TemplateList \"" + templateList + "\" bad ref to ObjId \"" + ((ParseObject) template).getObjectIdP());
                if (executeCleanup) {
                    templateList.remove(i);
//                    i -= 1;
                }
            }

            //set any items not already a template as templates:
            if (template instanceof Item) { // && !((Item) elt).isTemplate()) {
                //non-template item in TemplateList, 
                if (!((Item) template).isTemplate()) {
                    Log.p("CLEANUP: template Item in TemplateList is not a template \"" + template + " objId=" + ((ParseObject) template).getObjectIdP());
                    if (executeCleanup) {
                        ((Item) template).setTemplate(true);
                        dao.saveNew((Item) template, false);
                    }
                }
                //check that full hierarchy of subtasks below top-level template are also marked as templates
                makeAllSubTaskTemplatesAndRemoveDuplicates((Item) template, executeCleanup);
                i++;
            } else { //non-Item in list
                Log.p("CLEANUP: TemplateList \"" + templateList + "\" contains non-Item" + template + ", ObjId \"" + (template instanceof ParseObject ? ((ParseObject) template).getObjectIdP() : "<not an ParseObect>"));
                if (executeCleanup) {
                    templateList.remove(i);
//                    i -= 1;
                }
            }
        }
//        List items = templateList.getList();
//        if (cleanUpDuplicatesInList("Templates " + ((ItemAndListCommonInterface) templateList).getText(), templateList, executeCleanup) && executeCleanup) {
        cleanUpDuplicatesInList("Templates " + ((ItemAndListCommonInterface) templateList).getText(), templateList, executeCleanup);
        if (executeCleanup) {
//            templateList.setList(templateList);
            dao.saveNew((ParseObject) templateList, false);
        }
    }

    public void cleanUpTemplateListInParse(boolean executeCleanup) {
//        cleanUpTemplateList(DAO.getInstance().getTemplateList(), getTopLevelTemplatesFromParse(), true);
        cleanUpTemplateList(TemplateList.getInstance(), dao.getTopLevelTemplatesFromParse(), executeCleanup);
    }

    /**
     * cleans up duplicates in the category
     *
     * @param category
     */
    private void cleanUpCategory(Category category) {
        cleanUpBadObjectReferencesCategory(category);
        List items = category.getListFull();
        cleanUpDuplicatesInList("Category " + ((ItemAndListCommonInterface) category).getText(), items, executeCleanup);
        if (executeCleanup) {
            category.setList(items);
            dao.saveNew((ParseObject) category, false);
        }
    }

//    private void cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(List<List<ParseObject>> listOfList) {
    private void cleanUpBadObjectReferencesInListOfCategories() {
        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(dao.getCategoryList());

    }

    private void cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(List<List<ParseObject>> listOfList) {
//        List<List<ParseObject>> listOfList = getCategoryList();
        Log.p("CLEANUP: number elements in list = " + listOfList.size(), logLevel);
//        for (int i = 0, size = listOfList.size(); i < size; i++) {
        int i = 0;
        while (i < listOfList.size()) {
            //TODO!!!! change for loop to a construction that works if the list is being altered as it is traversed: http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
            Object catOrItemList = listOfList.get(i);

            if (notOnParseServer((ParseObject) catOrItemList)) {
//                if (listOfList instanceof ItemAndListCommonInterface) {
                Log.p("CLEANUP: Bad ref in \"" + ((ItemAndListCommonInterface) listOfList).getText() + "\" to objectId=" + ((ParseObject) listOfList).getObjectIdP(), logLevel);
//                } else {
//                    Log.p("CLEANUP: Bad ref in " + listOfList + " to objectId=" + ((ParseObject) listOfList).getObjectId(), logLevel);
//                }
                if (executeCleanup) {
                    listOfList.remove(i);
                } else {
                    i++;
                }
            } else {
                if (((ItemAndListCommonInterface) catOrItemList).getOwner() == null) {
                    Log.p("CLEANUP: Missing ref in ItemList/Category \"" + ((ItemAndListCommonInterface) catOrItemList).getText() + "\" (ObjId=" + ((ParseObject) catOrItemList).getObjectIdP() + ") to its owner ListOfCategories/ListOfItemLists in \"", logLevel);
                    ((ItemAndListCommonInterface) catOrItemList).setOwner((ItemAndListCommonInterface) listOfList);
                }
                if (catOrItemList instanceof Category) {
                    cleanUpCategory((Category) catOrItemList);
                } else if (catOrItemList instanceof ItemList) {
                    cleanUpItemList((ItemList) catOrItemList);
                }
                i++;
            }
        }
        if (executeCleanup) {
            dao.saveNew((ParseObject) listOfList, false);
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
    public void cleanUpAllCategoriesFromParse() {
        CategoryList categoryList = CategoryList.getInstance();
        categoryList.reloadFromParse(true, null, null); //get latest state

        List<Category> listOfCategoriesFromParse = dao.getAllCategoriesFromParse();

        Log.p("CLEANUP: number Categories in Parse = " + listOfCategoriesFromParse.size(), logLevel);
        Log.p("CLEANUP: number Categories in CategoryList = " + categoryList.getSize(), logLevel);

        //check that every category in Parse is in the stored list of categories
        for (int i = 0, size = listOfCategoriesFromParse.size(); i < size; i++) {
            Category cat = listOfCategoriesFromParse.get(i);
            if (cat.getOwner() == null) {
                Log.p("CLEANUP: Missing owner (CategoryList) in Category \"" + cat.getText() + "\" ObjId=" + cat.getObjectIdP() + " size=" + cat.getSize() + ", to its owner ListOfCategories (which contains(cat)=" + categoryList.contains(cat) + ")", logLevel);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false &&  categoryList.contains(cat)) {
//                    if (executeCleanup) {
//                        cat.setOwner(categoryList);
//                        save(cat);
//                    }
////                } else if (cat.getList().size() == 0) { //a lost category, empty, not visible to user, so probably safe to delete. NO, better to recover it
////                    if (executeCleanup) {
////                        delete(cat); //nothing in cateogyr, safe to delete
////                    }
//                } else //a lost category, with content, so should probably be kept
//</editor-fold>
                if (executeCleanup) {
                    cat.setOwner(categoryList);
                    dao.saveNew((ParseObject) cat, false);
                    categoryList.add(cat);
                    dao.saveNew((ParseObject) categoryList, false);
                }
            } else if (!categoryList.contains(cat)) { //add missing categories to CategoryList
                Log.p("CLEANUP: Category not referenced: Category \"" + cat.getText() + " not in CategoryList" + categoryList, logLevel);

                if (executeCleanup) {
                    cat.setOwner(categoryList);
                    dao.saveNew((ParseObject) cat, false);
                    categoryList.add(cat);
                    dao.saveNew((ParseObject) categoryList, false);
                }
            }
            //check that all items in category also reference category
            List<Item> categoryItems = cat.getListFull();
            for (Item item : categoryItems) {
                if (!item.getCategories().contains(cat)) {
                    Log.p("CLEANUP: Item doesn't reference Category: Item \"" + item + "\" is missing category=" + cat, logLevel);
                    if (executeCleanup) {
//                        cat.addItemToCategory(item, false);
                        item.addCategoryToItem(cat, false);
                        dao.saveNew((ParseObject) item, false);
                    }
                }
            }
        }
    }

    private void cleanUpAllItemListsFromParse(List<ItemList> itemListsFromParse, ItemListList itemListList) {
        Log.p("CLEANUP: number ItemLists in Parse = " + itemListsFromParse.size(), logLevel);
        Log.p("CLEANUP: number ItemLists in ItemListList = " + itemListList.size(), logLevel);
//        Log.p("CLEANUP: number ItemLIsts in ItemListList = " + itemListList.size(), logLevel); // don't call size here, becuase it loads list which may be wrong
        for (int i = 0, size = itemListsFromParse.size(); i < size; i++) {
            ItemList itemList = itemListsFromParse.get(i);
            if (itemList.getOwner() == null) {
                if (itemList.isSystemList()) {
                    if (itemList.getOwner() != null) {
                        itemList.removeFromOwner();
                    }
                } else {
                    if (itemListList.contains(itemList)) {
                        Log.p("CLEANUP: ItemList doesn't reference owner: Missing owner (ItemListList) in ItemList \"" + itemList.getText() + "\" (ObjId=" + itemList.getObjectIdP() + ", size=" + itemList.getSize() + ") to its owner ListOfItemLists (which contains=" + itemListList + ")", logLevel);
                        if (executeCleanup) {
                            itemList.setOwner(itemListList);
                            dao.saveNew((ParseObject) itemList, false);
                        }
                    } else if (itemList.getListFull().size() == 0) { //a lost ItemList, empty, not visible to user, so probably safe to delete
                        if (executeCleanup) {
                            dao.saveNew((ParseObject) itemList, false); //nothing in ItemList, safe to delete
                        }
                    } else if (executeCleanup) { //not in ItemListList, not empty, keep
                        itemList.setOwner(itemListList);
                        itemListList.add(itemList);
                        dao.saveNew(false, itemListList, itemList);
                    }
                }
            } else if (!itemList.getOwner().equals(itemListList) && !itemList.isSystemList()) {
                Log.p("CLEANUP: ItemListList not owner: ItemList \"" + itemList + "\" (ObjId=" + itemList.getObjectIdP() + ", size=" + itemList.getSize() + ") does not have ItemListList as owner but instead \"" + itemList.getOwner() + "\" objId=" + ((ParseObject) itemList.getOwner()).getObjectIdP(), logLevel);
                if (itemListList.contains(itemList)) {
                    if (executeCleanup) { //correct to right owner
                        itemList.setOwner(itemListList);
                        dao.saveNew((ParseObject) itemList, false);
                    }
                } else if (executeCleanup) { //force owner to ItemListList anyway //TODO may not be the right solution if one day ItemLists of ItemLists is supported
                    Log.p("CLEANUP: ItemList wrong owner: ItemList \"" + itemList + "\" (ObjId=" + itemList.getObjectIdP() + ", size=" + itemList.getSize() + ") does not have ItemListList as owner but instead \"" + itemList.getOwner() + "\" objId=" + ((ParseObject) itemList.getOwner()).getObjectIdP(), logLevel);
                    itemList.setOwner(itemListList);
                    dao.saveNew((ParseObject) itemList, false);
                }
            } else if (!itemListList.contains(itemList) && !itemList.isSystemList()) {
                Log.p("CLEANUP: ItemList not in ItemListList: ItemList \"" + itemList + "\" (ObjId=" + itemList.getObjectIdP() + ", size=" + itemList.getSize() + ") has owner ItemListList but ItemListList does not reference it", logLevel);
                if (executeCleanup) {
                    itemListList.add(itemList);
                }
            }
            if (executeCleanup) {
                dao.saveNew((ParseObject) itemListList, false);
            }
        }
    }

    public void cleanUpAllItemListsInParse() {
//        ItemListList itemListList = getItemListList();
        ItemListList itemListList = ItemListList.getInstance();
//        cleanUpAllItemListsFromParse(dao.getAllItemListsFromParse(), itemListList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
        cleanUpAllItemListsFromParse(dao.getAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE), false), itemListList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(itemListList); //Clean up links to removed Categories
    }

//    private boolean belongsTo(FilterSortDef filter, )
    /**
     * remove any filters from Parse server which are not pointing to both a
     *
     * @param listOfFilters
     */
    private void cleanUpFilterSortDefs() {
        List<FilterSortDef> listOfFilters = dao.getAllFilterSortDefsFromParse();
        Log.p("CLEANUP: number elements in list = " + listOfFilters.size(), logLevel);

        //construct hashmpas to effectively search for elements that point to a filter
//        Map<FilterSortDef, Category> catsWithFilter = new HashMap();
        Map<String, Category> catsWithFilter = new HashMap();
//        for (Category cat : CategoryList.getInstance().getList()) {
        for (Object o : CategoryList.getInstance().getListFull()) {
            Category cat = (Category) o;
            if (cat.getFilterSortDef() != null && cat.getFilterSortDef().getObjectIdP() != null) {
                catsWithFilter.put(cat.getFilterSortDef().getObjectIdP(), cat);
            }
        }

//        Map<FilterSortDef, ItemList> itemListsWithFilter = new HashMap<>();
        Map<String, ItemList> itemListsWithFilter = new HashMap<>();
//        for (ItemList itemList : ItemListList.getInstance().getList()) {
        for (Object o : ItemListList.getInstance().getListFull()) {
            ItemList itemList = (ItemList) o;
            if (itemList.getFilterSortDef() != null && itemList.getFilterSortDef().getObjectIdP() != null) {
                itemListsWithFilter.put(itemList.getFilterSortDef().getObjectIdP(), itemList);
            }
        }

        //TODO Items do not implement filters yet
//        Map<FilterSortDef, Item> itemsWithFilter = new HashMap<>();
        Map<String, Item> itemsWithFilter = new HashMap<>();
        for (Item item : dao.getAllItems()) {
            if (item.getFilterSortDef() != null && item.getFilterSortDef().getObjectIdP() != null) {
                itemsWithFilter.put(item.getFilterSortDef().getObjectIdP(), item);
            }
        }

        //for every filter, check if it is referenced and if not delete it
        for (int i = 0, size = listOfFilters.size(); i < size; i++) {
            FilterSortDef filter = listOfFilters.get(i);
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (filter.getFilteredObjectId() == null || filter.getFilteredObjectId().equals("")) {
//                Log.p("CLEANUP: FilterSortDef (ObjId=" + filter.getObjectIdP() + ") without valid ref to FilteredObjectId (" + filter.getFilteredObjectId() + ")", logLevel);
//                if (executeCleanup) {
//                    delete(filter); //delete filters without ref to both objectId and Screen
//                }
//            } else {
//</editor-fold>
            String filterObjId = filter.getObjectIdP();
            if (catsWithFilter.get(filterObjId) == null
                    && itemListsWithFilter.get(filterObjId) == null
                    && itemsWithFilter.get(filterObjId) == null) { //no refs to filter
                Log.p("CLEANUP: FilterSortDef (ObjId=" + filter.getObjectIdP() + ") is not referenced by any Category/ItemList/Item", logLevel);
                if (executeCleanup) {
                    dao.delete(filter); //delete filters without ref to both objectId and Screen
                }
            };
//            }
        }
    }

//    private void cleanUpWorkSlots(List<WorkSlot> listOfWorkSlots) {
//    private void cleanUpWorkSlots(WorkSlotList listOfWorkSlots) {
    void cleanUpWorkSlots(boolean executeCleanup) {
        WorkSlotList listOfWorkSlots = dao.getAllWorkSlotsFromParse();
        Log.p("CLEANUP: number elements in list = " + listOfWorkSlots.size(), logLevel);
        for (int i = 0, size = listOfWorkSlots.size(); i < size; i++) {
            WorkSlot workSlot = listOfWorkSlots.get(i);
            //test if workSlot has multiple owners (Category, Item, ItemList) - NOT necessary since the priority is defined by getOwner()
//            if (workSlot.getOwner())
            boolean deleteWorkSlot = true; //if all owners are missing, then remove
            boolean noOwner = false; //if all owners are missing, then remove
            boolean noRepeatRule = false; //if all owners are missing, then remove
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
            if (workSlot.getOwner() == null) {// || workSlot.getOwnerItem().equals("")) {
//                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
//                deleteWorkSlot = false;
                noOwner = true;
                //TODO!!! check if any RepeatRule references the workslot but it doesn't reference the RR
                //TODO!!! check if any Item/ItemList/Category references the workslot but it doesn't reference it back
            }
            if (false && workSlot.getRepeatRule() == null) {// || workSlot.getRepeatRule().equals("")) {
//                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
//                deleteWorkSlot = false;
                noRepeatRule = true;
            }
//            if (deleteWorkSlot) {
            if (noOwner && noRepeatRule) {
                Log.p("CLEANUP: WorkSlot no owner: workSLot=" + workSlot + ") with null as Owner.", logLevel);
//                try {
                if (executeCleanup) {
                    dao.delete(workSlot, true, false); //delete filters without ref to both objectId and Screen
                }//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
            }
        }
    }

    /**
     * cleans up duplicates in the category
     *
     * @param category
     */
    private void cleanUpRepeatRules() {
        List<RepeatRuleParseObject> allRepeatRules = dao.getAllRepeatRulesFromParse();

        //delete repeat rules not referenced by any Item or WorkSlot
        //construct hashmpas to effectively search for elements that point to a filter
        Map<RepeatRuleParseObject, Item> itemsWithRepeatRule = new HashMap<>();

        for (Item item : dao.getAllItems()) {
            if (item.getRepeatRuleN() != null) {
                itemsWithRepeatRule.put(item.getRepeatRuleN(), item);
            }
        }

        Map<RepeatRuleParseObject, WorkSlot> workSlotsWithRepeatRule = new HashMap<>();

        for (WorkSlot workSlot : dao.getAllWorkSlotsFromParse().getWorkSlotListFull()) {
            if (workSlot.getRepeatRule() != null) {
                workSlotsWithRepeatRule.put(workSlot.getRepeatRule(), workSlot);
            }
        }
//        for (int i = 0, size = allRepeatRules.size(); i < size; i++) {
//            RepeatRuleParseObject repeatRule = allRepeatRules.get(i);
        for (RepeatRuleParseObject repeatRule : allRepeatRules) {

            //for every repeatRule, check if it is referenced and if not delete it
            if (itemsWithRepeatRule.get(repeatRule) == null && workSlotsWithRepeatRule.get(repeatRule) == null) { //no refs to repeatRule
                Log.p("CLEANUP: RepeatRule (ObjId=" + repeatRule.getObjectIdP() + ") is not referenced by any Item or WorkSlot", logLevel);
                if (executeCleanup) {
                    dao.delete(repeatRule); //delete filters without ref to both objectId and Screen
//                    allRepeatRules.remove(repeatRule); //NOT necessary since we have a for loop
                }
            } else { //if RR is referenced
                //clean up wrong references in list of repeat instances
//            List<ItemAndListCommonInterface> repeatInstanceList = repeatRule.getListOfUndoneRepeatInstances();
                List<RepeatRuleObjectInterface> repeatInstanceList = repeatRule.getListOfUndoneInstances();

                cleanUpBadObjectReferencesInListInRepeatRuleInstanceList(repeatRule, repeatInstanceList);

                //clean up duplicates in list of repeat instances
                cleanUpDuplicatesInList("RepeatRule instances " + repeatRule, repeatInstanceList, executeCleanup);

                if (executeCleanup) {
                    repeatRule.setListOfUndoneInstances(repeatInstanceList);
                    dao.saveNew(repeatRule, false);
                }
            }
        }
    }

    public void setExecuteCleanup(boolean executeCleanup) {
        this.executeCleanup = executeCleanup;
    }

    //--------  NEW cleanup procedures  -----------------------------------------------
    /**
     *
     * @param item
     * @param checkOwner check if Owner exists and if item is included in the
     * owner's list
     */
    void cleanUpBadObjectReferencesItem(Item item) { //, boolean checkOwner) {
//        boolean checkOwner = true;
        //Check that if an owner is defined, it exists, and that it contains the item in its list. NB! ItemLists must then only check that their items point the themselves and not another list
//        if (checkOwner && item.getOwner() != null) {
        ItemAndListCommonInterface owner = item.getOwner();
        if (owner != null) {
            if (notOnParseServer((ParseObject) owner)) {
                Log.p("CLEANUP: Non-existant owner: Item \"" + item.getText() + "\" with bad ref to Owner objectId=" + ((ParseObject) item.getOwner()).getObjectIdP(), logLevel);
                if (executeCleanup) {
                    item.setOwner(null);
                }
            } else if (owner.getItemIndex(item) == -1) {
//                Log.p("CLEANUP: Not in owner: Item \"" + item.getText() + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
                Log.p("CLEANUP: Not in owner: Item \"" + item + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
                if (executeCleanup) {
                    //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
                    item.setOwner(null); //hack to avoid that addToList below complains that owner is already defined
//                    item.getOwner().addToList(item);
                    owner.addToList(item);
//                    item.getOwner().getList(item);
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (item.getOwner() != null && !item.getOwner().getList().contains(item))
//                    cleanUpMissingInclusionInList("Item \"" + item + "\" owner's \"" + item.getOwner() + "\" does not reference the item (owner's list=" + item.getOwner().getList() + ")",
//                            item, item.getOwner().getList())) {
//                if (executeCleanup) {
//                    DAO.getInstance().save((ParseObject) item.getOwner());
//                }
//            };
//</editor-fold>
        } else {
            Log.p("CLEANUP: No owner: Item \"" + item + "\" owner is null (add to Inbox)", logLevel);
            if (executeCleanup) {
                //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
                Inbox.getInstance().addToList(item);
//                owner.addToList(item);
                dao.saveNew((ParseObject) Inbox.getInstance(), false);
            }
        }

        //Check repeat rule exists
        if (item.getRepeatRuleN() != null && notOnParseServer((ParseObject) item.getRepeatRuleN())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to RepeatRule objectId=" + ((ParseObject) item.getRepeatRuleN()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setRepeatRule(null); //remove reference to inexisting RepeatRule
            }
        }

        //INterrupted tasks
        if (item.getTaskInterrupted() != null && notOnParseServer((ParseObject) item.getTaskInterrupted())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to TaskInterrupted, objectId=" + ((ParseObject) item.getTaskInterrupted()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setTaskInterrupted(null); //remove reference to inexisting Item
            }
        }

        //Dependent tasks
        if (item.getDependingOnTask() != null && notOnParseServer((ParseObject) item.getDependingOnTask())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to DependingOnTask, objectId=" + ((ParseObject) item.getDependingOnTask()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setDependingOnTask(null); //remove reference to inexisting Item
            }
        }

        //Original source (eg when copied from template)
        if (item.getSource() != null && notOnParseServer((ParseObject) item.getSource())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to Original source, objectId=" + ((ParseObject) item.getSource()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setSource(null); //remove reference to inexisting Item
            }
        }

        //CATEGORIES
//        cleanUpBadObjectReferences(item.getCategories()); //remove links to non-existing Categories
        for (Category cat : item.getCategories()) {
//            List list;
            //DON'T test for templates (the template should not be in the category
            if (item.isTemplate()) {
                if (cat.contains(item)) {
                    Log.p("CLEANUP: Template \"" + item.getText() + "\" is wrongly referenced in Category \"" + cat, logLevel);
                    if (executeCleanup) {
                        cat.removeItemFromCategory(item, false);
                    }
                }
            } else {
                List list2 = cat.getListFull();
//                if ((list = cleanUpMissingInclusionInList("Item \"" + item + "\" has Category \"" + cat + "\" but category does not reference the item, category's list (" + cat.getList() + ")", item, list2)) != null) {
//                cat.addItemToCategory(item, false); //add item to category //NO, done in cleanupMissing
                if (list2 != null && !list2.contains(item)) {
                    Log.p("CLEANUP: Item \"" + item
                            + "\" (ObjId=" + item.getObjectIdP() + ") has Category \"" + cat
                            + "\" (ObjId=" + cat.getObjectIdP() + ") but category does not reference the item, category's list (" + cat
                            .getListFull() + ")", logLevel
                    );
                    if (executeCleanup) {
                        list2.add(item);
                        cat.setList(list2);
                    }
                }
            }
        }
        List list3 = item.getCategories();
        if (cleanUpDuplicatesInList("Item \"" + item + "\" (ObjId=" + item.getObjectIdP() + ") list of categories", list3, executeCleanup) && executeCleanup) {
//        if (executeCleanup) {
            item.setCategories(list3);
        }

        //SUBTASKS
        List<Item> subtasks = item.getListFull();
//        for (Item subtask : subtasks) {
        int i = 0;
        while (i < subtasks.size()) {
            Item subtask = subtasks.get(i);
            if (subtask.getOwner() == null) {
                Log.p("CLEANUP: Item \"" + item + "\"'s subtask \"" + subtask + "\" has owner==null", logLevel);
                if (executeCleanup) {
                    subtask.setOwner(item);
                    dao.saveNew(subtask, false);
                }
//                i++;
//            } else if (!subtask.getOwner().equals(item)) {
            }
            if (false && !subtask.getOwner().equals(item)) { //do not this this for subtasks - incompatible with the check above
                Log.p("CLEANUP: Item \"" + item + "\"'s subtask \"" + subtask + "\" has another owner==\"" + subtask.getOwner() + "\"", logLevel);
                if (executeCleanup) {
//                    subtasks.remove(subtask); //
                    subtask.setOwner(item); //force owner of subtask to this item
                    item.setList(subtasks);
//                } else {
                }
//                    i++;
//            } else {
//                i++;
            }
            i++;

        }

        cleanUpDuplicatesInList("Item \"" + item + "\" has duplicated subtask (subtasks=" + subtasks + ")", subtasks, executeCleanup);
        //finally save
        if (executeCleanup) {
            item.setList(subtasks);
            dao.saveNew(item, false);
        }

        //Workslots : WorkSlots point to their owner, NOT the other way around, so nothing to clean up here 
    }

    /**
     * clean up an Item (leaf-task or project with subtasks). Check subtasks
     * have project as owner. Check if any tasks has project as owner (or
     * another task as owner). Update inherited values and values derived from
     * subtasks. Check that all referenced elements exist (Categories, owners,
     * repeatRules
     *
     * @param item
     */
    void cleanUpItem(Item item, boolean executeCleanup) {
//        boolean checkOwner = true;
        //Check that if an owner is defined, it exists, and that it contains the item in its list. NB! ItemLists must then only check that their items point the themselves and not another list
//        if (checkOwner && item.getOwner() != null) {
        ItemAndListCommonInterface owner = item.getOwner();
        if (owner != null) {
            if (notOnParseServer((ParseObject) owner)) {
                Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to Owner objectId=" + ((ParseObject) item.getOwner()).getObjectIdP(), logLevel);
                if (executeCleanup) {
                    item.setOwner(null);
                }
            } else if (owner.getItemIndex(item) == -1) {
                Log.p("CLEANUP: Not in owner: Item \"" + item.getText() + "\"'s Owner:\"" + owner + "\" does not include item", logLevel);
                if (executeCleanup) {
                    //an item's listed owner takes precedence (so, objects determine their owner, it is not (one of) the owner that changes the item's owner to themselve
                    item.setOwner(null); //hack to avoid that addToList below complains that owner is already defined
//                    item.getOwner().addToList(item);
                    owner.addToList(item);
//                    item.getOwner().getList(item);
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (item.getOwner() != null && !item.getOwner().getList().contains(item))
//                    cleanUpMissingInclusionInList("Item \"" + item + "\" owner's \"" + item.getOwner() + "\" does not reference the item (owner's list=" + item.getOwner().getList() + ")",
//                            item, item.getOwner().getList())) {
//                if (executeCleanup) {
//                    DAO.getInstance().save((ParseObject) item.getOwner());
//                }
//            };
//</editor-fold>
        }

        //Check repeat rule exists
        if (item.getRepeatRuleN() != null && notOnParseServer((ParseObject) item.getRepeatRuleN())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to RepeatRule objectId=" + ((ParseObject) item.getRepeatRuleN()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setRepeatRule(null); //remove reference to inexisting RepeatRule
            }
        }

        //INterrupted tasks
        if (item.getTaskInterrupted() != null && notOnParseServer((ParseObject) item.getTaskInterrupted())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to TaskInterrupted, objectId=" + ((ParseObject) item.getTaskInterrupted()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setTaskInterrupted(null); //remove reference to inexisting Item
            }
        }

        //Dependent tasks
        if (item.getDependingOnTask() != null && notOnParseServer((ParseObject) item.getDependingOnTask())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to DependingOnTask, objectId=" + ((ParseObject) item.getDependingOnTask()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setDependingOnTask(null); //remove reference to inexisting Item
            }
        }

        //Original source (eg when copied from template)
        if (item.getSource() != null && notOnParseServer((ParseObject) item.getSource())) {
            Log.p("CLEANUP: Item \"" + item.getText() + "\" with bad ref to Original source, objectId=" + ((ParseObject) item.getSource()).getObjectIdP(), logLevel);
            if (executeCleanup) {
                item.setSource(null); //remove reference to inexisting Item
            }
        }

        //CATEGORIES
//        cleanUpBadObjectReferences(item.getCategories()); //remove links to non-existing Categories
        for (Category cat : item.getCategories()) {
//            List list;
            //DON'T test for templates (the template should not be in the category
            if (item.isTemplate()) {
                if (cat.contains(item)) {
                    Log.p("CLEANUP: Template \"" + item.getText() + "\" is wrongly referenced in Category \"" + cat, logLevel);
                    if (executeCleanup) {
                        cat.removeItemFromCategory(item, false);
                    }
                }
            } else {
                List list2 = cat.getListFull();
//                if ((list = cleanUpMissingInclusionInList("Item \"" + item + "\" has Category \"" + cat + "\" but category does not reference the item, category's list (" + cat.getList() + ")", item, list2)) != null) {
//                cat.addItemToCategory(item, false); //add item to category //NO, done in cleanupMissing
                if (list2 != null && !list2.contains(item)) {
                    Log.p("CLEANUP: Item \"" + item
                            + "\" (ObjId=" + item.getObjectIdP() + ") has Category \"" + cat
                            + "\" (ObjId=" + cat.getObjectIdP() + ") but category does not reference the item, category's list (" + cat
                            .getListFull() + ")", logLevel
                    );
                    if (executeCleanup) {
                        list2.add(item);
                        cat.setList(list2);
                    }
                }
            }
        }
        List list3 = item.getCategories();
        if (cleanUpDuplicatesInList("Item \"" + item + "\" (ObjId=" + item.getObjectIdP() + ") list of categories", list3, executeCleanup) && executeCleanup) {
//        if (executeCleanup) {
            item.setCategories(list3);
        }

        //SUBTASKS
        List<Item> subtasks = item.getListFull();
//        for (Item subtask : subtasks) {
        int i = 0;
        while (i < subtasks.size()) {
            Item subtask = subtasks.get(i);
            if (subtask.getOwner() == null) {
                Log.p("CLEANUP: Item \"" + item + "\"'s subtask \"" + subtask + "\" has owner==null", logLevel);
                if (executeCleanup) {
                    subtask.setOwner(item);
                    dao.saveNew(subtask, false);
                }
//                i++;
//            } else if (!subtask.getOwner().equals(item)) {
            } else //            if (false && !subtask.getOwner().equals(item)) { //do not this this for subtasks - incompatible with the check above
            if (subtask.getOwner() != item) { //subtask ownership 'wins' over other ownerships (e.g. if subtask is also in a list or a project at a higher level
                Log.p("CLEANUP: Item \"" + item + "\"'s subtask \"" + subtask + "\" has another owner==\"" + subtask.getOwner() + "\"", logLevel);
                if (executeCleanup) {
//                    subtasks.remove(subtask); //
                    subtask.setOwner(item); //force owner of subtask to this item
//                    item.setList(subtasks); //no need to setList since subtask is already in list of subtasks, so no change
                    dao.saveNew(subtask, false); //save new owner
//                } else {
                }
//                    i++;
//            } else {
//                i++;
            }
            cleanUpItem(subtask, executeCleanup); //iterate down the hierarchy
            i++;

        }

        cleanUpDuplicatesInList("Item \"" + item + "\" has duplicated subtask (subtasks=" + subtasks + ")", subtasks, executeCleanup);
        //finally save
        if (executeCleanup) {
            item.setList(subtasks);
            dao.saveNew(item, false);
        }

        //Workslots : WorkSlots point to their owner, NOT the other way around, so nothing to clean up here 
    }
    //    private void cleanUpBadObjectReferencesCategory(Category category) {

    private String itemToString(Item item) {
        return "\"" + item.getText() + "\" [" + item.getObjectIdP() + "]";
    }

    /**
     * check correct owner, remove duplicates, check repeatRule(?)
     *
     * @param owner
     * @param executeCleanup
     * @return
     */
    boolean cleanUpWorkSlotList(ItemAndListCommonInterface owner, boolean executeCleanup) {
        //TODO check if there are other elements which has a given workSlot in their list -> highly unlikely
        WorkSlotList workSlotList = owner.getWorkSlotListN(false);
        if (workSlotList == null) {
            return false;
        }
        boolean hasDuplicates = false;
        List<WorkSlot> workSlots = workSlotList.getWorkSlotListFull();
        List<WorkSlot> uniques = new ArrayList<>();
//        Log.p("CLEANUP: number elements in list = " + listOfWorkSlots.size(), logLevel);
//        Log.p("CLEANUP: number elements in list = " + listOfWorkSlots.size(), logLevel);
//        for (int i = 0, size = workSlots.size(); i < size; i++) {
        int i = 0;
        while (i < workSlots.size()) {
            WorkSlot workSlot = workSlots.get(i);
            if (uniques.contains(workSlot)) {
                hasDuplicates = true;
                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectIdP() + ") without valid ref to OwnerItemList, OwnerItem and RepeatRule. startTime=" + workSlot.getStartTimeD() + ", description=" + workSlot.getText() + ", adj.duration(minutes)=" + workSlot.getDurationInMinutes(), logLevel);
                workSlots.remove(i);
                //no i++!
            } else {
                if (workSlot.getOwner() == null) {
                    Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectIdP() + ") without valid ref to OwnerItemList, OwnerItem and RepeatRule. startTime=" + workSlot.getStartTimeD() + ", description=" + workSlot.getText() + ", adj.duration(minutes)=" + workSlot.getDurationInMinutes(), logLevel);
                    workSlot.setOwner(owner);
                } else if (workSlot.getOwner() != owner) {
                    workSlot.setOwner(owner);
                    dao.saveNew(workSlot, false);
                }
                //repeatRule
                RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
                if (repeatRule != null) {
                    //no relevant checks to do here? RepeatRule should check if all copies refer back to it?!
//                Log.p("CLEANUP: WorkSlot (ObjId=" + workSlot.getObjectId() + ") without valid ref to OwnerItem(" + workSlot.getOwnerList() + ")", logLevel);
                }
                i++;
            }
        }
        owner.setWorkSlotList(workSlotList);
        dao.saveNew((ParseObject) owner, false);
        return hasDuplicates;
    }

    /**
     * clean up duplicates in the (full) ItemList or Category. Will remove any
     * subsequent duplicates (leaving the first appearence). Execute this
     * *after* other clean-ups, notably ensuring that all elements are Items
     *
     * @param description
     * @param itemListOrCategory
     * @param executeCleanup
     * @return
     */
    private boolean cleanUpDuplicatesInItemListOrCategory(String description, ItemList itemListOrCategory, boolean executeCleanup) {
        List list = itemListOrCategory.getListFull();
        List cleanedList = removeDuplicates(list);
        if (executeCleanup) {
            itemListOrCategory.setList(cleanedList);
        }
        return list.size() != cleanedList.size();
    }

    private boolean cleanUpDuplicatesInItemListOrCategoryOLD(String description, ItemList itemListOrCategory, boolean executeCleanup) {
        //http://stackoverflow.com/questions/223918/iterating-through-a-collection-avoiding-concurrentmodificationexception-when-re
        //http://stackoverflow.com/questions/2849450/how-to-remove-duplicates-from-a-list
        boolean hasDuplicates = false;
        ArrayList uniqueItems = new ArrayList(); //items that have already been checked
        int index = 0;
//        for (int i = 0, size = itemListOrCategory.size(); i < size; i++) {
        while (index < itemListOrCategory.getSize()) {
            Item elt = (Item) itemListOrCategory.getItemAt(index);
            if (uniqueItems.contains(elt)) {
                //TODO duplicates can be either a second instance of same object, or a copy of it, should check for both and handle separately
                hasDuplicates = true;
                Log.p("CLEANUP: " + description + " contains duplicate of \"" + itemToString(elt) + "\" at position " + index + " (list= " + itemListOrCategory + ")", logLevel);
                if (executeCleanup) {
                    if (itemListOrCategory instanceof Category) {
//                        ((Category) itemListOrCategory).removeItemFromCategory(elt, false); //
                        ((Category) itemListOrCategory).removeItem(index); //
                    } else {
//                        ((ItemList) itemListOrCategory).removeFromList(elt, false); //
                        ((ItemList) itemListOrCategory).removeItem(index); //
                    }                    //don't index++ since we've removed the item and next iteration should treat the item now at position index
                } else {
                    index++;
                }
            } else {
                uniqueItems.add(elt); //no duplicate so add to list
                index++;
            }
        }
        return hasDuplicates;
    }

    boolean cleanUpItemListOrCategory(ItemList itemListOrCategory, boolean executeCleanup) {
        return cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, false);
    }

    boolean cleanUpItemListOrCategory(ItemList itemListOrCategory, boolean executeCleanup, boolean cleanupItems) {
        boolean issuesFound = false;
        String text = itemListOrCategory.getText();
        String objectIdP = itemListOrCategory.getObjectIdP();
        String prefix = "CLEANUP: " + (itemListOrCategory instanceof Category ? "Category" : "ItemList") + " \"" + text + " [" + objectIdP + "]";
        //check if belongs to CategoryList/ItemListList
        if (itemListOrCategory instanceof Category && itemListOrCategory.getOwner() != CategoryList.getInstance()) {
            Log.p(prefix + " not in CategoryList, but in [" + itemListOrCategory.getOwner().getObjectIdP() + "]");
            if (executeCleanup) {
                itemListOrCategory.setOwner(CategoryList.getInstance());
            }
        } else if (itemListOrCategory instanceof ItemList && itemListOrCategory.getOwner() != ItemListList.getInstance() && itemListOrCategory != Inbox.getInstance()) {
            Log.p(prefix + " not in ItemListList, but in [" + itemListOrCategory.getOwner().getObjectIdP() + "]");
            if (executeCleanup) {
                itemListOrCategory.setOwner(ItemListList.getInstance());
            }
        }

        cleanUpDuplicatesInItemListOrCategory(itemListOrCategory.getText(), itemListOrCategory, executeCleanup);

        int i = 0;
//        List<Item> items = itemListOrCategory.getListFull();
        while (i < itemListOrCategory.getSize()) {
            boolean moveToNextIndex = true; //hack to make sure we don't skip an i when an element in the list is removed
            Object elt = itemListOrCategory.getItemAt(i);
//            Object elt = itemListOrCategory.get(i);

            if (!(elt instanceof Item)) {
                boolean remove = false;
                if (elt instanceof ParseObject) {
                    Log.p(prefix + " refer to a ParseObject which is not an Item: [" + ((ParseObject) elt).getObjectIdP() + "]");
                    remove = true;
                } else {
                    Log.p(prefix + " refer to an object which is not a ParseObject, toString()=\"" + elt + "\"");
                    remove = true;
                }
                issuesFound = issuesFound || remove;
                if (remove && executeCleanup) {
                    itemListOrCategory.removeItem(i);
                    moveToNextIndex = false;
                }
            } else {//an Item
                //if not on server, simply remove the element
                if (notOnParseServer((ParseObject) elt)) {
                    Log.p(prefix + " refer to Item not on server [" + ((ParseObject) elt).getObjectIdP() + "]");
                    if (executeCleanup) {
                        itemListOrCategory.removeItem(i);
                        moveToNextIndex = false;
                    }
                    issuesFound = true;
                } else { //on server
                    Item item = (Item) elt;
                    String itemText = itemToString(item); //"\"" + item.getText() + "\" [" + item.getObjectIdP() + "]";

                    //Category refers to elt, but elt does not have Category in its list
                    if (itemListOrCategory instanceof Category) {
                        if (!item.getCategories().contains(itemListOrCategory)) { //if item does not reference the category, then add the category
                            Log.p(prefix + " references Item " + itemText + " but not in its categories (" + item.getCategories() + ")");
                            if (executeCleanup) {
                                item.addCategoryToItem((Category) itemListOrCategory, false); //add missing ref
                                dao.saveNew(item, false);
                            }
                            issuesFound = true;
                        }
                        //ItemList refers to elt, but elt does not have Category in its list
                    } else if (itemListOrCategory instanceof ItemList) {
//                    if (item.getOwner() == null || !item.getOwner().equals(itemListOrCategory)) { 
                        if (item.getOwner() == null) { //IF ever an item is referenced from multiple lists or projects, the first one 'wins' and becomes the owner
                            issuesFound = true;
                            Log.p(prefix + " references Item " + itemText + " but is has no Owner (owner=null)");
                            if (executeCleanup) {
                                item.setOwner((ItemList) itemListOrCategory); //if null, add ItemList as owner
                                dao.saveNew(item, false);
                            }
                        } else if (item.getOwner() != itemListOrCategory) {
                            Log.p(prefix + " references Item " + itemText + " which has another owner=" + item.getOwner());
//                                itemListOrCategory.remove(i); //if another is owner, remove item from this list
                            issuesFound = true;
                            if (executeCleanup) {
//                                itemListOrCategory.removeItem(i); //if another is owner, remove item from this list
                                item.removeFromOwner(); //if another is owner, remove that one before assigning to this one
                                itemListOrCategory.addItem(item); //it is more visible to end-user that item is in list, so keep it in this list
                                dao.saveNew(item, false);
                                moveToNextIndex = false;
                            }
                        }
                    } // else: if owner is not null, then if the owner is wrong, it will be fixed when fixing the item itself elsewhere
                    if (cleanupItems) {
                        cleanUpItem(item, executeCleanup);
                    }
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (itemListOrCategory instanceof ParseObject && executeCleanup) {
//            if (executeCleanup) {
//                save((ParseObject) itemListOrCategory);
//            }
//</editor-fold>
            if (moveToNextIndex) {
                i++;
            }
        }

        //check if all items on server that reference ItemList as owner, or which has Category in its categoryList are in the ItemList/Category:
        if (itemListOrCategory instanceof Category) {
            List<Item> itemsFromParse = dao.fetchAllItemsWithThisCategory((Category) itemListOrCategory);
            for (Item itm : itemsFromParse) {
                if (((Category) itemListOrCategory).getItemIndex(itm) < 0) {
                    Log.p("Item " + itemToString(itm) + " on server has category=" + prefix + " but is not in list");
                    ((Category) itemListOrCategory).addItemToCategory(itm, false); //add to end
                    issuesFound = true;
                }
            }
        } else {
            List<Item> itemsFromParse = dao.fetchAllItemsOwnedByItemList((ItemList) itemListOrCategory);
            for (Item itm : itemsFromParse) {
                if (!itemListOrCategory.contains(itm)) {
                    Log.p("Item " + itemToString(itm) + " on server has owner=" + prefix + " but is not in list");
                    itemListOrCategory.addItem(itm); //add to end
                    issuesFound = true;
                }
            }
        }
        //workslots:
        issuesFound = cleanUpWorkSlotList(itemListOrCategory, executeCleanup) || issuesFound;

        //TODO calculate all values in the list derived from the elements (currently none in ItemList nor Category)
        if (issuesFound && executeCleanup) {
            dao.saveNew((ParseObject) itemListOrCategory, false);
        }
        return issuesFound;
    }

    boolean cleanUpItemsWithNoValidOwner(boolean executeCleanup) {
        boolean issuesFound = false;
        List<Item> items = dao.getAllItems(true, false, false, false); //include templates, fetch from cache
        ItemList lostItems = new ItemList("Recovered items " + MyDate.formatDateTimeNew(new MyDate()), false);
        for (Item item : items) {
            if (item.getOwner() == null) { //getOwner also returns null for non-existant owners (e.g. a hard-deleted owner)
                Log.p("Item " + itemToString(item) + " on server has no valid owner" + (executeCleanup ? ", adding to list \"" + lostItems.getText() + "\"" : ""));
                if (executeCleanup) {
                    item.setOwner(lostItems);
                    lostItems.addToList(item);
//                    dao.saveInBackground(item); //save new owner //CAN'T do here because lostItems list is not saved yet so no ObjId
                }
                issuesFound = true;
            }
            //TODO: if item has a (non-existant) owner, then create a list named with that ObjectId and store all lost items together there (quite complicated to develop)
        }
        if (executeCleanup && lostItems.size() > 0) {
            dao.saveNew((ParseObject) lostItems, false); //first save new list to have a valid objectId!!
            dao.saveNew(lostItems.getListFull(), false); //THEN save all updated items
//            ItemListList.getInstance().addToList(0, lostItems); //add to beginning of lists
            ItemListList.getInstance().addToList(lostItems, false); //add to beginning of lists
            dao.saveNew((ParseObject) ItemListList.getInstance(), false);
        }
        return issuesFound;
    }

    /**
     *
     * @param executeCleanup if false, just list the detected inconsistencies
     * but don't change them
     */
    public void cleanUpAllBadObjectReferences(boolean executeCleanup) {
        logLevel = Log.ERROR;
//        int oldParseLogLevel = Logger.getInstance().getLogLevel();

        Logger.getInstance().setLogLevel(Log.ERROR);
        this.executeCleanup = executeCleanup;
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: STARTING (execute=" + executeCleanup + ") ----------------------------", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: ITEMS", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpBadObjectReferencesInItems(dao.getAllItems(true)); //Clean up all Items and their pointers first, true=includeTemplates

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: CATEGORIES", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
//        CategoryList categoryList = getCategoryList();
//        cleanUpAllCategoriesFromParse(getAllCategoriesFromParse(), categoryList); 
        cleanUpAllCategoriesFromParse();
//        cleanUpAllCategoriesFromParse(); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(categoryList); //Clean up links to removed Categories
        cleanUpBadObjectReferencesInListOfCategories(); //Clean up links to removed Categories

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: ITEMLISTS", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpAllItemListsInParse();

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: FILTERS", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpFilterSortDefs(); //Clean up links to removed ItemLists
//        cleanUpFilterSortDefs(getAllFilterSortDefsFromParse()); //Clean up links to removed ItemLists

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: WORKSLOTS", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpWorkSlots(executeCleanup); //Clean up links to removed ItemLists
//        cleanUpWorkSlots(getAllWorkSlotsFromParse()); //Clean up links to removed ItemLists

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: TEMPLATES", logLevel);
        //TODO!!!: check that tasks in AllTemplates list are all marked as templates!!
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpTemplateListInParse(executeCleanup);
//        cleanUpWorkSlots(getAllWorkSlotsFromParse()); //Clean up links to removed ItemLists
//        TemplateList templateList = getTemplateList();
//        cleanUpAllItemListsFromParse(getAllTemplatesByQuery(), templateList); //repair raw list of Categories first (will attach any non-empty categories to CategoryList before cleaning up those categories
//        cleanUpBadObjectReferencesInListOfCategoriesOrItemLists(templateList); //Clean up links to removed Categories
//        Log.p("CLEANUP: NOT DONE YET", logLevel);
//        Log.p("CLEANUP: -----------------------------------------------------", logLevel);

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: REPEATRULES", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        cleanUpRepeatRules();

        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
        Log.p("CLEANUP: ALL CLEANUP FINISHED --------------------------------", logLevel);
        Log.p("CLEANUP: -----------------------------------------------------", logLevel);
//        cleanUpWorkSlots(getAllWorkSlots()); //Clean up links to removed ItemLists

//        Logger.getInstance().setLogLevel(oldParseLogLevel);
//        cleanUpBadObjectReferences(getAllProjects()); //handled under Items
    }

}
