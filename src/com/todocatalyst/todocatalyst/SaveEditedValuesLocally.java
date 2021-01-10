/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.util.UITimer;
import com.parse4cn1.ParseObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * if initiated with an empty filename, e.g. for a new Item with ObjectId==null,
 * also store values so not lost on app restart.
 *
 * @author thomashjelm
 */
public class SaveEditedValuesLocally {//extends HashMap {

//    protected void setPreviousValuesFilename(String filename) {
//        previousValuesFilename = filename;
//    }
    private HashMap<Object, Object> previousValues = new HashMap<Object, Object>();
    private final static String PREFIX = "SAVED-EDITS-";
    final static String SCROLL_VALUE_KEY = "SCROLL-Y-VAL";
    private final static int SCROLL_VALUE_TIMEOUT_MS = 300; //how long after last scroll should the scroll value be saved
    private String filename;
    private MyForm myForm;
    private int scrollY;
    private UITimer saveScrollTimer;
    private Component lastScrollableComponent;
    private ScrollListener lastScrollListener;
    private boolean scrollListenerActive;
    private final static String EDIT_SESSION_START_TIME = "EditSessionStartTime"; //how long after last scroll should the scroll value be saved
//    private Date editSessionStartTime;

    SaveEditedValuesLocally(MyForm myForm, String filename, boolean activateScrollPositionSave) {
        if (Config.TEST) {
            ASSERT.that(filename == null || filename.length() > 0, "empty filename");
        }
//         previousValues = new HashMap<Object, Object>(); //implicit
        this.myForm = myForm;
        
        this.scrollListenerActive = activateScrollPositionSave;
        if (scrollListenerActive && this.myForm != null) {
            saveScrollTimer = new UITimer(() -> {
                if (false) {
                    Log.p("SaveEditedValuesLocally.saveScrollTimer: SAVING scroll Y=" + scrollY);
                }
//                previousValues.put(SCROLL_VALUE_KEY, scrollY); //only save scroll position after timeout
                put(SCROLL_VALUE_KEY, scrollY); //only save scroll position after timeout
//                saveFile();
            });
            
            Component scrollable = this.myForm.findScrollableContYChild();
            if (scrollable != null) {
//<editor-fold defaultstate="collapsed" desc="comment">
                //                scrollable.addScrollListener((newX, newY, oldX, oldY) -> {
                //                    if (newY != oldY) {
                //                        scrollY = newY;
                //                        saveScrollTimer.schedule(SCROLL_VALUE_TIMEOUT_MS, false, this.myForm);
                //                    }
                //                });
                //</editor-fold>
                SaveEditedValuesLocally.this.setListenToYScrollComponent(scrollable);
            }
        }
        if (filename != null) {
//        if (filename != null && filename.length() > 0) {
            if (false && (filename == null || filename.length() == 0)) {
                filename = "NewItem";
            }
//            String uniquePostFix = "";
//        ASSERT.that(filename != null && !filename.isEmpty());
            this.filename = PREFIX + filename;
            if (Storage.getInstance().exists(this.filename)) {
//            while (Storage.getInstance().exists(this.filename)) {
                previousValues.putAll((Map) Storage.getInstance().readObject(this.filename)); //merge values
//                uniquePostFix += "+1";
//                this.filename = PREFIX + filename + uniquePostFix;
            }
        }
        put(EDIT_SESSION_START_TIME, new MyDate());
    }
    
    SaveEditedValuesLocally(String filename) {
        this(null, filename, false);
    }
    
    SaveEditedValuesLocally() {
        this(null, null, false);
    }
    
    public String toString() {
        return "Hash={" + previousValues + "}, file=" + filename;
    }
    
    public void setListenToYScrollComponent(Component scrollableN) {
        if (lastScrollableComponent != null && lastScrollListener != null) {
            lastScrollableComponent.removeScrollListener(lastScrollListener);
        }
//        Component scrollable = this.myForm.findScrollableContYChild();
        if (scrollListenerActive && scrollableN != null) {
            lastScrollableComponent = scrollableN;
            lastScrollListener = (newX, newY, oldX, oldY) -> {
                if (false && Config.TEST) {
                    Log.p("SaveEditedValuesLocally.lastScrollListener: called with "
                            + ((newY != oldY) ? "changed Y=" + newY + " (scheduling save)" : "same Y=" + newY));
                }
                if (newY != oldY) {
                    scrollY = newY;
                    saveScrollTimer.schedule(SCROLL_VALUE_TIMEOUT_MS, false, myForm);
                }
            };
            scrollableN.addScrollListener(lastScrollListener);
        }
    }
    
    public void setListenToYScrollComponent(MyForm form) {
        setListenToYScrollComponent(form.findScrollableContYChild());
    }
    
    public void setListenToYScrollComponent() {
//        if (myForm!=null)
        setListenToYScrollComponent(myForm.findScrollableContYChild());
    }

//    public Integer getScrollYXXX() {
//        return (Integer) get(SCROLL_VALUE_KEY);
//    }
    /**
     * will scroll the form to the last stored scrollY position (if any) and
     * delete the scrollY position so this is only done once
     *
     * @param scrollableComp
     */
//    public void scrollToSavedYOnFirstShow(MyForm myForm) {
    public void scrollToSavedYOnFirstShow(ContainerScrollY scrollableComp) {
//        Integer scrollY = (Integer) previousValues.get(SCROLL_VALUE_KEY);
        Integer scrollY = (Integer) get(SCROLL_VALUE_KEY);
        if (scrollY != null) {
//            ContainerScrollY scrollableComp = this.myForm.findScrollableContYChild(myForm.getContentPane());
            if (scrollableComp != null) { //not sure why it can bcome null but it has happened
                scrollableComp.setScrollYPublic(scrollY);
            }
//            previousValues.remove(SCROLL_VALUE_KEY); //we only scroll to this value once, on first show of screen after 
            if (false) {
                remove(SCROLL_VALUE_KEY); //we only scroll to this value once, on first show of screen after 
            }
            if (false && Config.TEST) {
                Log.p("SaveEditedValuesLocally: Scroll to Y=" + scrollY);
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void initLocalSaveOfEditedValues(String filename) {
//        previousValuesFilename = PREFIX+filename;
//        previousValues = new HashMap<Object, Object>() {
//            void saveFile() {
////            Storage.getInstance().writeObject("ScreenItem-" + item.getObjectIdP(), this); //save
//                Storage.getInstance().writeObject(previousValuesFilename, this); //save
//            }
//
//            public Object put(Object key, Object value) {
//                Object previousValue = super.put(key, value);
//                saveFile();
//                return previousValue;
//            }
//
//            public Object remove(Object key) {
//                Object previousValue = super.remove(key);
//                saveFile();
//                return previousValue;
//            }
//
//        };
//        if (Storage.getInstance().exists(previousValuesFilename)) {
//            previousValues.putAll((Map) Storage.getInstance().readObject(previousValuesFilename));
//        }
//    }
//</editor-fold>
    public void saveFile() {
//            Storage.getInstance().writeObject("ScreenItem-" + item.getObjectIdP(), this); //save 
        if (previousValues != null && filename != null) {
            Storage.getInstance().writeObject(filename, previousValues); //save 
        }
    }

    /**
     * add a locally stored value (or remove it if value is null - useful for
     * storing eg template values which may be null)
     *
     * @param key
     * @param value value to be stored (or null to remove)
     * @return
     */
    public Object put(Object key, Object value) {
        if (false && Config.TEST) {
            ASSERT.that(value != null, "SaveEditedValuesLocally: put key=\"" + key + "\" with null value - missing objectIdP??");
        }
        if (true || previousValues != null) { //should never bu null, or error if so
            Object previousValue;
            if (value == null) {
                previousValue = previousValues.remove(key); // value.toString());
            } else {
                previousValue = previousValues.put(key, value); // value.toString());
            }
            saveFile();
            return previousValue;
        } else {
            return null;
        }
    }
    
    public void putNotZero(Object key, long value) {
        if (value != 0) {
            put(key, value);
        }
    }
    
    public void putNotZero(Object key, String value) {
        if (value != null && value.length() != 0) {
            put(key, value);
        }
    }
    
    public void putNotZero(Object key, double value) {
        if (value != 0) {
            put(key, value);
        }
    }
    
    public void putNotZero(Object key, boolean value) {
        if (value) {
            put(key, value);
        }
    }
    
    public void putNotZero(Object key, Date value) {
        if (value.getTime() != 0) {
            put(key, value);
        }
    }
    
    public void putNotZero(Object key, Object value) {
        if (value != null) {
            put(key, value);
        }
    }
    
    public Object get(Object key) {
        if (previousValues != null) {
            return previousValues.get(key);
        } else {
            return null;
        }
    }
    
    public Object get(Object key, Object defaultValue) {
        if (previousValues != null) {
            Object val = previousValues.get(key);
            return val != null ? val : defaultValue;
        } else {
            return null;
        }
    }
    
    public Object remove(Object key) {
        if (previousValues != null) {
            Object previousValue = previousValues.remove(key);
            saveFile();
            return previousValue;
        } else {
            return null;
        }
    }
    
    public void removePinchInsertKeys() {
        remove(MyForm.SAVE_LOCALLY_REF_ELT_GUID_KEY); //delete the marker on exit
        remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //delete the marker on exit
        remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK); //delete the marker on exit
        remove(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS); //delete the marker on exit
        remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
    }
    
    public boolean containsKey(Object key) {
        if (previousValues != null) {
            return previousValues.containsKey(key);
        } else {
            return false;
        }
    }
    
    public void deleteFile() {
//        if (previousValues != null && filename != null) {
        if (filename != null) {
            //<editor-fold defaultstate="collapsed" desc="comment">
            //            Storage.getInstance().deleteStorageFile("ScreenItem-" + item.getObjectIdP());
            //        if (previousValuesFilename != null && !previousValuesFilename.isEmpty()) {
            //        if (scrollableCont != null && scrollListener != null) { //first remove ScrollListener
            //            scrollableCont.removeScrollListener(scrollListener);
            //        }
            //        if (saveScrollYTimer != null) { //stop timer if running
            //            saveScrollYTimer.cancel();
            //        }
            //</editor-fold>
            Storage.getInstance().deleteStorageFile(filename);
        }
//        clear();
        previousValues = null; //provoke a crash if used after file was deleted
//        }
    }

    /**
     * clear data (use e.g. if previousValues are passed from Timer to
     * ScreenItem which then applies and saves the item after which the old
     * saved values are meaningless and should be deleted.
     */
    private void clear() {
        if (previousValues == null) {
            return;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//            Storage.getInstance().deleteStorageFile("ScreenItem-" + item.getObjectIdP());
//        if (previousValuesFilename != null && !previousValuesFilename.isEmpty()) {
//        if (scrollableCont != null && scrollListener != null) { //first remove ScrollListener
//            scrollableCont.removeScrollListener(scrollListener);
//        }
//        if (saveScrollYTimer != null) { //stop timer if running
//            saveScrollYTimer.cancel();
//        }
//</editor-fold>
        previousValues.clear();
        saveFile();
//        }
    }
    
    private HashMap<Object, Object> getValues() {
        return previousValues;
    }

    /**
     * will add the values defined in predefinedValues to those already stored.
     * Used to set predetermined values used for editing. If any key is already
     * defined, it will currently be overwritten (this could be changed to
     * merging the values if needed someday)
     *
     * @param predefinedValuesN
     */
    public void addAndOverwrite(SaveEditedValuesLocally predefinedValuesN) {
        if (predefinedValuesN == null) {
            return;
        }
//        for(Map.Entry<String, HashMap> entry : selects.entrySet()) {
//    String key = entry.getKey();
//    HashMap value = entry.getValue();
//        for (Map.Entry<Object, Object> entry : getValues().entrySet()) {
        for (Map.Entry<Object, Object> entry : predefinedValuesN.getValues().entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
//            Object oldValue = previousValues.put(key, value);
            Object oldValue = put(key, value);
            if (Config.TEST) {
                ASSERT.that(oldValue == null || oldValue.equals(value) || key.equals(EDIT_SESSION_START_TIME), this.getClass() + ".addAndOverwrite: key=" + key + ", overwriting exiting value=" + oldValue + ", with different value=" + value);
            }
        }
//        saveFile(); //saved in put above
    }

    /**
     * add a set of values that are NOT already defined, e.g. to merge in a
     * template to an Item that may already have certain values changed
     *
     * @param predefinedValues
     */
    public void addIfNotAlreadyExisting(SaveEditedValuesLocally predefinedValues) {
        if (predefinedValues == null) {
            return;
        }
//        for (Map.Entry<Object, Object> entry : predefinedValues.previousValues.entrySet()) {
        for (Map.Entry<Object, Object> newEntry : predefinedValues.getValues().entrySet()) {
            Object key = newEntry.getKey();
            Object value = newEntry.getValue();
            Object oldValue = get(key);
            if (oldValue == null) {
                put(key, value); //add a previously undefined value
            }
            if (false && Config.TEST) {
                ASSERT.that(oldValue == null || oldValue.equals(value), this.getClass() + ".addAndOverwrite: key=" + key + ", overwriting exiting value=" + oldValue + ", with different value=" + value);
            }
        }
//        saveFile(); //saved in put above
    }
    
//    public void putCategories(List<Category> categories) {
////        Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
//        if (categories == null || categories.size() == 0) {
//            remove(Item.PARSE_CATEGORIES);
//        }
////        put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(categories));
//        put(Item.PARSE_CATEGORIES, ItemAndListCommonInterface.convListToObjectIdList((List) categories));
//    }
//    
//    public List<Category> getCategories() {
//        if (get(Item.PARSE_CATEGORIES) != null) {
////            return Item.convCatObjectIdsListToCategoryList((List<String>) get(Item.PARSE_CATEGORIES));
//            return DAO.getInstance().convCatObjectIdsListToCategoryListN((List<String>) get(Item.PARSE_CATEGORIES));
//        } else {
//            return new ArrayList();
//        }
//    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void putOwnerXXX(ItemAndListCommonInterface owner) {
////        Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
////        if (owner == null || owner.size() == 0)
////            previousValues.remove(Item.PARSE_CATEGORIES);
////        previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(owner));
//        if (owner == null) {
////            previousValues.remove(Item.PARSE_OWNER_ITEM);
//            remove(Item.PARSE_OWNER_ITEM);
//        } else {
//            put(Item.PARSE_OWNER_ITEM, owner.getObjectIdP());
//        }
//    }
//</editor-fold>
//    private void putListOfElements(String key, List<ItemAndListCommonInterface> list) {
//        if (list == null) {
//            remove(key);
//        } else {
//            List<String> objIdList = new ArrayList();
//            for (ItemAndListCommonInterface e : list) {
////                objIdList.add(e.getObjectIdP());
//                objIdList.add(e.getGuid());
//            }
//            put(key, objIdList);
//        }
//    }
    
//    public void putOwners(List<ItemAndListCommonInterface> owners) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (owners == null) {
////            remove(Item.PARSE_OWNER_ITEM);
////        } else {
////            List ids = new ArrayList();
////            for (ItemAndListCommonInterface e : owners) {
////                ids.add(e.getObjectIdP());
////            }
////            put(Item.PARSE_OWNER_ITEM, ids);
////        }
////</editor-fold>
//        putListOfElements(Item.PARSE_OWNER_ITEM, owners);
//    }
    
//    public void putOwner(ItemAndListCommonInterface owner) {
//        putOwners(Arrays.asList(owner));
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemAndListCommonInterface getOwnerXXX() {
////        if (previousValues.get(Item.PARSE_CATEGORIES) != null)
////            return Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES));
////        else return new ArrayList();
//        return get(Item.PARSE_OWNER_ITEM) != null && ((List) get(Item.PARSE_OWNER_ITEM)).size() > 0
//                ? DAO.getInstance().fetchItemOwner(((List<String>) get(Item.PARSE_OWNER_ITEM)).get(0)) //fetch the actual owner
//                : null;
//    }
//</editor-fold>

    /**
     * returns null if no list was previously defined, otherwise an empty list
     * if previous owner was removed, or a list containing the previously
     * selected new owner. Necessary to distinguish the three cases when editing
     * the owner of an object: 1) no change, 2) unselected previous owner 3)
     * selected new owner. Without a list, case 1) and 2) cannot be
     * distinguished!!
     *
     * @return
     */
//    public List<ItemAndListCommonInterface> getListOfElementsNXXX(String key) {
//        Object eltList = get(key);
//        if (eltList != null) {
//            List<String> ids = (List) eltList;
//            List<ItemAndListCommonInterface> elements = new ArrayList();
//            for (String id : ids) {
//                ItemAndListCommonInterface owner = DAO.getInstance().fetchItemOwner(id);
//                elements.add(owner);
//            }
//            return elements;
//        } else {
//            return null;
//        }
//    }
//    public List<ItemAndListCommonInterface> getOwnersN() {
//        if (get(Item.PARSE_OWNER_ITEM) != null) {
//            List<String> ids = (List) get(Item.PARSE_OWNER_ITEM);
//            List<ItemAndListCommonInterface> owners = new ArrayList();
//            for (String id : ids) {
//                ItemAndListCommonInterface owner = DAO.getInstance().fetchItemOwner(id);
//                owners.add(owner);
//            }
//            return owners;
//        } else {
//            return null;
//        }
////        return getListOfElementsN(Item.PARSE_OWNER_ITEM);
//    }

    /**
     * shortcut to convert a list of subtasks/Items to a list of their ObjectIds
     * and put that
     *
     * @param subtasksObjIds
     */
//    public Object putSubtaskList(List<Item> subtasks) {
    public void putSubtaskList(List<Item> subtasks) {
//        putListOfElements(Item.PARSE_SUBTASKS, subtasks);
        if (true) {
            if (subtasks == null) {
                remove(Item.PARSE_SUBTASKS);
//                return null;
            } else {
                List<String> listOfObjIds = ItemAndListCommonInterface.convListToObjectIdList((List) subtasks);
                put(Item.PARSE_SUBTASKS, listOfObjIds);
//                return listOfObjIds;
            }
        } else {
            if (subtasks == null) {
                remove(Item.PARSE_SUBTASKS);
//                return null;
            } else {
//                ParseObject.setExternalizeAllState(true);
                put(Item.PARSE_SUBTASKS, subtasks);
//                ParseObject.setExternalizeAllState(false);
            }
        }
    }
    
//    public void putSubtaskObjdIdsListxxx(List<String> subtasksObjIds) {
//        if (subtasksObjIds == null) {
//            remove(Item.PARSE_SUBTASKS);
//        } else {
//            put(Item.PARSE_SUBTASKS, subtasksObjIds);
//        }
//    }

    /**
     * returns null if no edits were done, and empty list if all subtasks were
     * deleted!!
     *
     * @return
     */
//    public List<Item> getSubtaskListN() {
////        List<Item> subtasks = (List<Item>) get(Item.PARSE_SUBTASKS);
////        return subtasks;
//        List<String> subtasksObjIds = (List<String>) get(Item.PARSE_SUBTASKS);
//        return DAO.getInstance().convItemObjectIdsListToItemListN(subtasksObjIds);
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (ids != null) {
////            List<Item> items = new ArrayList();
////            for (String id : ids) {
////                Item item = DAO.getInstance().fetchItem(id);
////                items.add(item);
////            }
////            return items;
////        } else {
////            return null;
////        }
////        return getListOfElementsN(Item.PARSE_SUBTASKS);
////</editor-fold>
//    }

    /**
     * returns null if no edits were done, and empty list if all subtasks were
     * deleted!!
     *
     * @return
     */
//    public List<String> getSubtaskObjIdsListNxxx() {
//        List<String> ids = (List) get(Item.PARSE_SUBTASKS);
////        return ids != null ? ids : new ArrayList();
//        return ids;
//    }

//    public void removeOwnerXXX() {
//        remove(Item.PARSE_OWNER_ITEM);
//    }
//    public void removeOwners() {
//        remove(Item.PARSE_OWNER_ITEM);
//    }
    
    public Date getEditSessionStartTime() {
        return (Date) get(EDIT_SESSION_START_TIME);
    }
    ParseObject element ;
    public void setElementToSaveLocally(ParseObject element) {
        this.element=element;
    }
    public void saveElementToSaveLocally() {
        put("Element",element);
    }
    public ParseObject getElementToSaveLocally( ) {
        return (ParseObject)get("Element");
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean updateWithEditedValues() {
//    protected static void putEditedValues2(Map<Object, UpdateField> parseIdMap2) {
//        Log.p("putEditedValues2 - saving edited element, parseIdMap2=" + parseIdMap2);
//        ASSERT.that(parseIdMap2 != null);
//            UpdateField repeatRule = previousValues.remove(Item.PARSE_REPEAT_RULE); //set a repeatRule aside for execution last (after restoring all fields)
//            for (Object parseId : parseIdMap2.keySet()) {
////            put(parseId, parseIdMap.get(parseId).saveEditedValueInParseObject());
//                parseIdMap2.get(parseId).update();
//            }
//            if (repeatRule != null) {
//                repeatRule.update();
//            }
//    }
//    }
//</editor-fold>
}
