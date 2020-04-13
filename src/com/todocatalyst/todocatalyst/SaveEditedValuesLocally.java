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
import java.util.ArrayList;
import java.util.Arrays;
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
    private Component lastScrollableComponent = null;
    private ScrollListener lastScrollListener = null;
    private boolean scrollListenerActive;

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
                previousValues.put(SCROLL_VALUE_KEY, scrollY); //only save scroll position after timeout
                saveFile();
            });

            Component scrollable = this.myForm.findScrollableContYChild();
            if (scrollable != null) //<editor-fold defaultstate="collapsed" desc="comment">
            //                scrollable.addScrollListener((newX, newY, oldX, oldY) -> {
            //                    if (newY != oldY) {
            //                        scrollY = newY;
            //                        saveScrollTimer.schedule(SCROLL_VALUE_TIMEOUT_MS, false, this.myForm);
            //                    }
            //                });
            //</editor-fold>
            {
                setScrollComponent(scrollable);
            }
        }
        if (filename != null) {
//        if (filename != null && filename.length() > 0) {
            if (false && (filename == null || filename.length() == 0)) {
                filename = "NewItem";
            }
            String uniquePostFix = "";
//        ASSERT.that(filename != null && !filename.isEmpty());
            this.filename = PREFIX + filename ;
//            if (Storage.getInstance().exists(this.filename)) {
            while (Storage.getInstance().exists(this.filename)) {
//                previousValues.putAll((Map) Storage.getInstance().readObject(this.filename)); //merge values
                uniquePostFix += "+1";
                this.filename = PREFIX + filename + uniquePostFix;
            }
        }
    }

    SaveEditedValuesLocally(String filename) {
        this(null, filename, false);
    }

    SaveEditedValuesLocally() {
        this(null, null, false);
    }

    public void setScrollComponent(Component scrollableN) {
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

    public Integer getScrollY() {
        return (Integer) get(SCROLL_VALUE_KEY);
    }

    /**
     * will scroll the form to the last stored scrollY position (if any) and
     * delete the scrollY position so this is only done once
     *
     * @param scrollableComp
     */
//    public void scrollToSavedYOnFirstShow(MyForm myForm) {
    public void scrollToSavedYOnFirstShow(ContainerScrollY scrollableComp) {
        Integer scrollY = (Integer) previousValues.get(SCROLL_VALUE_KEY);
        if (scrollY != null) {
//            ContainerScrollY scrollableComp = this.myForm.findScrollableContYChild(myForm.getContentPane());
            if (scrollableComp != null) { //not sure why it can bcome null but it has happened
                scrollableComp.setScrollYPublic(scrollY);
            }
            previousValues.remove(SCROLL_VALUE_KEY); //we only scroll to this value once, on first show of screen after 
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

    public Object put(Object key, Object value) {
        if (false && Config.TEST) {
            ASSERT.that(value != null, "SaveEditedValuesLocally: put key=\"" + key + "\" with null value - missing objectIdP??");
        }
        if (previousValues != null) {
            Object previousValue = previousValues.put(key, value);
            saveFile();
            return previousValue;
        } else {
            return null;
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

    public boolean containsKey(Object key) {
        if (previousValues != null) {
            return previousValues.containsKey(key);
        } else {
            return false;
        }
    }

    public void deleteFile() {
        if (previousValues != null && filename != null) {
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
        clear();
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
     * @param predefinedValues
     */
    public void addAndOverwrite(SaveEditedValuesLocally predefinedValues) {
        if (predefinedValues == null) {
            return;
        }
//        for(Map.Entry<String, HashMap> entry : selects.entrySet()) {
//    String key = entry.getKey();
//    HashMap value = entry.getValue();
//        for (Map.Entry<Object, Object> entry : getValues().entrySet()) {
        for (Map.Entry<Object, Object> entry : predefinedValues.previousValues.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Object oldValue = previousValues.put(key, value);
            ASSERT.that(oldValue == null || oldValue.equals(value), this.getClass() + ".addAndOverwrite: key=" + key + ", overwriting exiting value=" + oldValue + ", with different value=" + value);
        }
        saveFile();
    }

    public void putCategories(List<Category> categories) {
//        Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
        if (categories == null || categories.size() == 0) {
            remove(Item.PARSE_CATEGORIES);
        }
        put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(categories));
    }

    public List<Category> getCategories() {
        if (get(Item.PARSE_CATEGORIES) != null) {
            return Item.convCatObjectIdsListToCategoryList((List<String>) get(Item.PARSE_CATEGORIES));
        } else {
            return new ArrayList();
        }
    }

    public void putOwner(ItemAndListCommonInterface owner) {
//        Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES))
//        if (owner == null || owner.size() == 0)
//            previousValues.remove(Item.PARSE_CATEGORIES);
//        previousValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(owner));
        if (owner == null) {
            previousValues.remove(Item.PARSE_OWNER_ITEM);
        } else {
            put(Item.PARSE_OWNER_ITEM, owner.getObjectIdP());
        }
    }

    public ItemAndListCommonInterface getOwner() {
//        if (previousValues.get(Item.PARSE_CATEGORIES) != null)
//            return Item.convCatObjectIdsListToCategoryList((List<String>) previousValues.get(Item.PARSE_CATEGORIES));
//        else return new ArrayList();
        return get(Item.PARSE_OWNER_ITEM) != null && ((List) get(Item.PARSE_OWNER_ITEM)).size() > 0
                ? DAO.getInstance().fetchItemOwner(((List<String>) get(Item.PARSE_OWNER_ITEM)).get(0)) //fetch the actual owner 
                : null;
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
