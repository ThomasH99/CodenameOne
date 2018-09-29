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
    private static String ExpandedObjectsFilePrefix = "ExpandedObjects";

    private String filename;
    private HashSet<String> expandedObjects; // = new HashSet(); //TODO!! save expandedObjects for this screen and the given list. NB visible to allow to expland items when subtasks are added

//        ExpandedObjects(String screenId, String objectId) {
    /**
    
    @param screenId
    @param parseObject may be null in screens which are 'singletons' (exist only once and are not used with different items)
     */
    ExpandedObjects(String screenId, ParseObject parseObject) {
        assert screenId != null;
        filename = ExpandedObjectsFilePrefix + screenId + "_" + (parseObject == null ? "NoParseObject" : parseObject.getObjectIdP());
        expandedObjects = new HashSet();
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

    private void save() {
        Storage.getInstance().writeObject(filename, expandedObjects);
    }

//        @Override
    public boolean add(Object element) {
//            boolean result = super.add(((ItemAndListCommonInterface) element).getObjectIdP());
        boolean result = expandedObjects.add(((ItemAndListCommonInterface) element).getObjectIdP());
        save();
        return result;
    }

    public boolean addAll(Collection<String> elements) {
//            boolean result = super.add(((ItemAndListCommonInterface) element).getObjectIdP());
        boolean result = expandedObjects.addAll(elements);
        save();
        return result;
    }

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
}
