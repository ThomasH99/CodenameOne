/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.ui.Container;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.events.ScrollListener;
import com.codename1.ui.util.UITimer;
import com.todocatalyst.todocatalyst.MyForm.UpdateField;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author thomashjelm
 */
public class SaveEditedValuesLocally {//extends HashMap {

//    protected void setPreviousValuesFilename(String filename) {
//        previousValuesFilename = filename;
//    }
    private Map<Object, Object> previousValues;
    private static String PREFIX = "SAVED-EDITS-";
    private static String SCROLL_VALUE_KEY = "SCROLL-Y-VAL";
    private static int SCROLL_VALUE_TIMEOUT_MS = 500; //how long after last scroll should the scroll value be saved
    private String previousValuesFilename;

    SaveEditedValuesLocally(String filename) {
        ASSERT.that(filename != null && !filename.isEmpty());
        previousValuesFilename = PREFIX + filename;
        previousValues = new HashMap<Object, Object>(); //implicit
        if (Storage.getInstance().exists(previousValuesFilename)) {
            previousValues.putAll((Map) Storage.getInstance().readObject(previousValuesFilename));
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
        Storage.getInstance().writeObject(previousValuesFilename, previousValues); //save 
    }

    public Object put(Object key, Object value) {
        Object previousValue = previousValues.put(key, value);
        saveFile();
        return previousValue;
    }

    public Object get(Object key) {
        return previousValues.get(key);
    }

    public Object get(Object key, Object defaultValue) {
        Object val = previousValues.get(key);
        return val != null ? val : defaultValue;
    }

    public Object remove(Object key) {
        Object previousValue = previousValues.remove(key);
        saveFile();
        return previousValue;
    }

    public boolean containsKey(Object key) {
        return previousValues.containsKey(key);
    }

    private UITimer saveScrollYTimer = null;
    private Form f = null;
    private Container scrollableCont = null;
    private ScrollListener scrollListener = null;

    void setSaveScrollPosition(Container scrollableCont) {
        this.scrollableCont = scrollableCont;
        scrollListener = (newX, newY, oldX, oldY) -> {
            if (saveScrollYTimer == null) { //create new UITimer
                saveScrollYTimer = new UITimer(() -> {
                    int scrollY = this.scrollableCont.getScrollY();
                    previousValues.put(SCROLL_VALUE_KEY, scrollY); //only save scroll position after timeout
                });
            }
            if (f == null) {
                f = Display.getInstance().getCurrent(); //only call getCurrent once
            }
            if (f != null) {
                saveScrollYTimer.schedule(SCROLL_VALUE_TIMEOUT_MS, false, f); //restart timer on each scroll
            }
        };
        scrollableCont.addScrollListener(scrollListener);
    }

    int getScrollY() {
        Integer scrollY = (Integer) previousValues.get(SCROLL_VALUE_KEY);
        if (scrollY != null) {
            return scrollY;
        } else {
            return -1;
        }
    }

    public void deleteFile() {
//            Storage.getInstance().deleteStorageFile("ScreenItem-" + item.getObjectIdP());
//        if (previousValuesFilename != null && !previousValuesFilename.isEmpty()) {
        if (scrollableCont != null && scrollListener != null) { //first remove ScrollListener
            scrollableCont.removeScrollListener(scrollListener);
        }
        if (saveScrollYTimer != null) { //stop timer if running
            saveScrollYTimer.cancel();
        }
        Storage.getInstance().deleteStorageFile(previousValuesFilename);
//        }
    }

    /**
    clear data (use e.g. if previousValues are passed from Timer to ScreenItem which then applies and saves the item after which the old saved values are meaningless and should be deleted. 
     */
    public void clear() {
//            Storage.getInstance().deleteStorageFile("ScreenItem-" + item.getObjectIdP());
//        if (previousValuesFilename != null && !previousValuesFilename.isEmpty()) {
        if (scrollableCont != null && scrollListener != null) { //first remove ScrollListener
            scrollableCont.removeScrollListener(scrollListener);
        }
        if (saveScrollYTimer != null) { //stop timer if running
            saveScrollYTimer.cancel();
        }
        previousValues.clear();
//        }
    }

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
}
