/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Storage;
import static com.codename1.io.Util.readObject;
import static com.codename1.io.Util.writeObject;
import com.codename1.ui.Component;
import com.parse4cn1.ParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author thomashjelm
 */
/**
    encapsulates the list of expanded objects and saves it to local storage on each modification
 */
class ExpandedObjects {//implements Externalizable {//extends HashSet {

    public static String CLASS_NAME = "ExpandedObjects_";
    private static String EXPANDED_OBJECTS_FILENAME_PREFIX = "ExpandedObjects";

    private String filename;
    private HashSet<String> expandedObjects; // = new HashSet(); //TODO!! save expandedObjects for this screen and the given list. NB visible to allow to expland items when subtasks are added

    /**
    if filename is null or "", then no persistence will be done (e.g. when editing subtask lists)
    @param uniqueIdForFilename 
     */
    ExpandedObjects(String uniqueIdForFilename) {
//        assert uniqueIdForFilename != null && !uniqueIdForFilename.isEmpty();
////        filename = ExpandedObjectsFilePrefix + screenId + "_" + (parseObject == null ? "NoParseObject" : parseObject.getObjectIdP());
//        filename = ExpandedObjectsFilePrefix + screenId + "_" + uniqueIdForFilename;
        expandedObjects = new HashSet();
        if (isValidFilename(uniqueIdForFilename)) {
            filename = EXPANDED_OBJECTS_FILENAME_PREFIX + "_" + uniqueIdForFilename;
            if (Storage.getInstance().exists(filename)) {
//            expandedObjects = ((ArrayList<String>) Storage.getInstance().readObject(filename)).;
                Object res = Storage.getInstance().readObject(filename);
                if (res instanceof HashSet) { //necessary since when res is read from cache, CN1 returns it as a hashset, but when read from storage, returns it as ArrayList
                    expandedObjects = ((HashSet<String>) res);
                } else if (res instanceof ArrayList) {
                    expandedObjects.addAll((ArrayList<String>) res);
                }
//            expandedObjects= ((HashSet<String>) Storage.getInstance().readObject(filename));
            }
        }
    }

//        ExpandedObjects(String screenId, String objectId) {
    /**
    just a helper to construct all composed filenames in a simpler fashion and with consistent format
    @param screenId
    @param uniqueExtensionId 
     */
    ExpandedObjects(String screenId, String uniqueExtensionId) {
        this(screenId + uniqueExtensionId);
        assert screenId != null;
    }

    /**
    @param screenId
    @param parseObject may be null in screens which are 'singletons' (exist only once and are not used with different items)
     */
    ExpandedObjects(String screenId, ParseObject parseObject) {
        this(screenId, (parseObject == null || parseObject.getObjectIdP() == null || parseObject.getObjectIdP().isEmpty() ? "NoParseObject" : parseObject.getObjectIdP()));
    }

    private boolean isValidFilename(String filename) {
        return filename != null && !filename.isEmpty();
    }

    private void save() {
        if (isValidFilename(filename))
            Storage.getInstance().writeObject(filename, expandedObjects);
    }

//        @Override
    /**
    only add a single instance of each element
    @param element
    @return 
     */
    public boolean add(Object element) {
//            boolean result = super.add(((ItemAndListCommonInterface) element).getObjectIdP());
//        boolean result = false;
//        if (!expandedObjects.contains(element)) { //don't add if already there (to avoid having to 
        boolean result = false;
        if (Config.TEST) ASSERT.that(element instanceof ItemAndListCommonInterface, "should only expand ItemAndListCommonInterface, not elt=" + element);
        if (Config.TEST && element instanceof ItemAndListCommonInterface) ASSERT.that(((ItemAndListCommonInterface) element).getObjectIdP() != null,"ERROR expanding an element with no objectId, elt="+element);
        if (element instanceof ItemAndListCommonInterface) //a hashset so no need to check if already added
            result = expandedObjects.add(((ItemAndListCommonInterface) element).getObjectIdP()); //a hashset so no need to check if already added
        else if (element instanceof WorkSlotList)
            result = expandedObjects.add(((WorkSlotList) element).getOwner().getObjectIdP()); //store owner id for WorkSlotLists (which are temporary/dynamically calculate)
        if (result) save(); //only save if modified
//        }
        return result;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean addAll(Collection<String> elements) {
////            boolean result = super.add(((ItemAndListCommonInterface) element).getObjectIdP());
//        boolean result = expandedObjects.addAll(elements);
//        save();
//        return result;
//    }
//</editor-fold>
    public void deleteFile() {
        Storage.getInstance().deleteStorageFile(filename);
    }

//        @Override
    public boolean remove(Object element) {
//            boolean result = super.remove(((ItemAndListCommonInterface) element).getObjectIdP());
        boolean result = expandedObjects.remove(((ItemAndListCommonInterface) element).getObjectIdP());
        save();
        return result;
    }

//        @Override
    public boolean contains(Object element) {
//            return super.contains(((ItemAndListCommonInterface) element).getObjectIdP());
        return expandedObjects.contains(((ItemAndListCommonInterface) element).getObjectIdP());
    }

    public void updateExpandedUIID(Component comp, Object element, String normalUIId, String expandedUIID) {
//            return super.contains(((ItemAndListCommonInterface) element).getObjectIdP());
        if (expandedObjects.contains(((ItemAndListCommonInterface) element).getObjectIdP())) {
            comp.setUIID(expandedUIID);
        } else
            comp.setUIID(normalUIId);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//
//    @Override
//    public int getVersion() {
//        return 0;
//    }
//
//    @Override
//    public void externalize(DataOutputStream out) throws IOException {
//        writeObject(expandedObjects, out);
//    }
//
//    @Override
//    public void internalize(int version, DataInputStream in) throws IOException {
//        expandedObjects = (HashSet) readObject(in);
//    }
//
//    @Override
//    public String getObjectId() {
//        return CLASS_NAME;
//    }
//</editor-fold>
}
