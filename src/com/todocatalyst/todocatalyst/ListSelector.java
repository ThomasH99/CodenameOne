/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import static com.codename1.io.Util.readObject;
import static com.codename1.io.Util.writeObject;
import com.parse4cn1.ParseObject;

/**
 * manages selection of objects. Possible to set how many may be selected, as
 * well as behavior of selecting too many (remove first selected or ignore).
 *
 * @author thomashjelm
 * @param <E>
 */
public class ListSelector<E> {//implements Externalizable { //implements Collection {

    private int maxNbSelectedObjects;// = Integer.MAX_VALUE;
    private boolean removeFirstAddedObjectIfMoreThanMaxAreAdded;
    private boolean warnIfMoreThanMaxAreAdded; //TODO show pop up to warn that more than max is trying to selected and that you need to deselect some other ones first
    private boolean allowNoSelection; //allow user to unselect all elements (have zero selected elements)
    private List<E> selectedObjects;// = new ArrayList();
    private SelectionUpdate selectionUpdate;
    private Collection referenceSet;
    public static String CLASS_NAME = "ListSelector";

// Now just externalized as a list of objectId strings    
//    @Override
//    public void externalize(DataOutputStream out) throws IOException {
//        out.writeInt(selectedObjects.size());
//        for (E elt : selectedObjects) {
//            out.writeUTF(((ParseObject)elt).getObjectIdP());
//        }
//    }
//
//    @Override
//    public void internalize(int version, DataInputStream in) throws IOException {
//        selectedObjects=new ArrayList();
//        int size = in.readInt();
//        String objectId ;
//        E elt;
//        for (int i = 0; i < size; i++) {
//            objectId = in.readUTF();
//            elt = (E)DAO.getInstance().fetchItem(objectId);
//            selectedObjects.add(elt);
//        }
//    }
//
//    @Override
//    public int getVersion() {
//        return 0;
//    }
//
//    @Override
//    public String getObjectId() {
//        return CLASS_NAME;
//    }
    interface SelectionUpdate {

        void update(Object obj, boolean selected);
    }

    /**
     *
     * @param selectedObjects
     * @param createCopyOfSelectedObjects
     * @param maxNbSelectedObjects
     * @param removeFirstAddedObjectIfMoreThanMaxAreAdded
     * @param selectionUpdate
     * @param allowNoSelection
     */
    ListSelector(List<E> selectedObjects, boolean createCopyOfSelectedObjects, int maxNbSelectedObjects, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded,
            SelectionUpdate selectionUpdate, boolean allowNoSelection, Collection selectableObjects) {
        if (selectedObjects == null) {
            this.selectedObjects = new ArrayList();
        } else {
            this.selectedObjects = createCopyOfSelectedObjects ? new ArrayList(selectedObjects) : selectedObjects;
        }
        this.maxNbSelectedObjects = maxNbSelectedObjects;
        this.removeFirstAddedObjectIfMoreThanMaxAreAdded = removeFirstAddedObjectIfMoreThanMaxAreAdded;
        this.selectionUpdate = selectionUpdate;
        if (this.selectionUpdate == null) {
            this.selectionUpdate = (a, b) -> {
            };
        }
        this.allowNoSelection = allowNoSelection;
        setReferenceSetAndRefreshSelection(selectableObjects);
    }

    ListSelector(List<E> selectedObjects, boolean createCopyOfSelectedObjects, int maxNbSelectedObjects, boolean removeFirstAddedObjectIfMoreThanMaxAreAdded,
            SelectionUpdate selectionUpdate, boolean allowNoSelection) {
        this(selectedObjects, createCopyOfSelectedObjects, maxNbSelectedObjects, removeFirstAddedObjectIfMoreThanMaxAreAdded, selectionUpdate, allowNoSelection, null);
    }

    /**
     * listSelector allowing any number of selections (from 0 to all)
     */
    public ListSelector() {
        this(new ArrayList<E>(), false, Integer.MAX_VALUE, true, null, true);
    }

    /**
     * listSelector allowing one selection
     *
     * @param allowNoSelection if true possible to not select any item (0
     * selections)
     */
    ListSelector(boolean allowNoSelection) {
        this(new ArrayList<E>(), false, 1, true, null, allowNoSelection);
    }

    /**
     * create a simply listSelector
     *
     * @param maxNbSelectedObjects
     * @param allowNoSelection
     */
    ListSelector(int maxNbSelectedObjects, boolean allowNoSelection) {
        this(new ArrayList<E>(), false, maxNbSelectedObjects, true, null, allowNoSelection);
    }

    /**
     * set (new) referenceSet, and update the selection accordingly (remove any
     * previously selected objects that are not in referenceSet(
     *
     * @param referenceSet null to reset
     */
    public void setReferenceSetAndRefreshSelection(Collection referenceSet) {
        this.referenceSet = referenceSet;
        if (referenceSet != null) {
            refresh(this.referenceSet);
        }
    }

    /**
     * ensure that any previously selected objects are consistent with (a subset
     * of) the selectable
     *
     * @param selectableObjects if null, no effect
     */
    private void refresh(Collection referenceSet) {
        if (referenceSet != null) {
            selectedObjects.retainAll(referenceSet);
        }
//        for (E e:selectedObjects) 
//            if (!selectableObjects.contains(e)) selectedObjects.remove(e);
    }

    /**
     * inverse the selection of obj
     *
     * @param obj
     * @return true if flip succeeded, false if not (eg too many objects already
     * selected)
     */
    boolean flipSelection(E obj) {
        if (referenceSet != null && !referenceSet.contains(obj)) {
            return false; //only allow to flip for objects in the referenceSet
        }
        if (!selectedObjects.contains(obj)) { //obj NOT selected
            //SELECT
            if (selectedObjects.size() >= maxNbSelectedObjects) { //too many already selected
                if (removeFirstAddedObjectIfMoreThanMaxAreAdded) {
                    while (selectedObjects.size() >= maxNbSelectedObjects) {
                        E o = selectedObjects.get(0);
                        if (selectionUpdate != null) {
                            selectionUpdate.update(o, false);
                        }
                        selectedObjects.remove(o); //remove first selected
                    }
                    selectedObjects.add(obj);
                    if (selectionUpdate != null) {
                        selectionUpdate.update(obj, true);
                    }
                    return true;
                } else {
                    //nothing, can't select object
                    return false;
                }
            } else {
                selectedObjects.add(obj);
                if (selectionUpdate != null) {
                    selectionUpdate.update(obj, true);
                }
                return true;
            }
        } else { //UNSELECT
//            if (allowNoSelection) {
////                selectionUpdate.update(selectedObjects.get(0), false);
//                selectedObjects.remove(obj);
//                selectionUpdate.update(obj, false);
//                return true;
//            } else {
            if (allowNoSelection || selectedObjects.size() > 1) { //if not allowNoSelection, only allow unselect if more than 1 object selected
                selectedObjects.remove(obj);
                if (selectionUpdate != null) {
                    selectionUpdate.update(obj, false);
                }
                return true;
            } else {
                if (selectionUpdate != null) {
                    selectionUpdate.update(obj, true); //ensure object stays selected despite the unsuccesful deselection
                }
                return false;
            }
//            }
        }
    }

    boolean isSelected(Object obj) {
        return selectedObjects.contains(obj);
    }

    boolean isSelectable(Object obj) {
        return referenceSet.contains(obj);
    }

    /**
     * selects obj
     *
     * @param obj
     * @return true if not previously selected and successfully selected,
     * otherwise false
     */
    boolean select(E obj) {
        if (!isSelected(obj)) {
            return flipSelection(obj);
        } else {
            return true; //already selected
        }
    }

    /**
     * unselects obj
     *
     * @param obj
     * @return true if previously selected, false it not previously selected but
     * could not be selected
     */
    public boolean unselect(E obj) {
        if (isSelected(obj)) {
            return flipSelection(obj);
        } else {
            return false; //already selected
        }
    }

    public List<E> getSelected() {
        return selectedObjects;
    }

    /**
     * serialize objectIds for local storage (to persist selection locally)
     *
     * @return
     */
    public List<String> getSelectedObjIds() {
        List objIds = new ArrayList();
        for (Object o : selectedObjects) {
            objIds.add(((ParseObject) o).getObjectIdP());
        }
        return objIds;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<String> selectItemsInObjIdList(List<String> objIds) {
//        for (String objId : objIds) {
//            if (<E> instanceof Item)
//            objIds.add(((ParseObject) o).getObjectIdP());
//            select(DAO.getInstance().fetchItem(objId));
//        }
//        return objIds;
//    }
//</editor-fold>

    public int getNumberSelected() {
        return selectedObjects.size();
    }

    public boolean isMaxNumberSelected() {
        return selectedObjects.size() >= maxNbSelectedObjects;
    }

    boolean invertSelection(Collection<E> fullSetOfObjects) {
        boolean sucessfullyAddedAllPreviouslyUnselectedObjects = true;
        List oldSelectedObjects = selectedObjects;
        selectedObjects = new ArrayList<E>();
        for (E obj : fullSetOfObjects) {
            if (!oldSelectedObjects.contains(obj)) {
                if (!select(obj)) {
                    sucessfullyAddedAllPreviouslyUnselectedObjects = false;
                    break; //no reason to continue selecting more objects if selection is full
                };
            }
        }
        oldSelectedObjects = null;
        return sucessfullyAddedAllPreviouslyUnselectedObjects;
    }

    /**
     * invert the selection wrt to a preset reference set of items
     *
     * @return
     */
    public boolean invertSelection() {
        if (referenceSet != null) {
            return invertSelection(referenceSet);
        } else {
            return false;
        }
    }

    public void unselectAll() {
        for (E obj : selectedObjects) { //unselect all objects
            if (selectionUpdate != null) {
                selectionUpdate.update(obj, false);
            }
        }
        selectedObjects.clear();
    }

    public boolean selectAll(Collection<E> fullSetOfObjects) {
        boolean sucessfullyAddedAllObjects = true;
        for (E obj : fullSetOfObjects) {
            if (!select(obj)) {
                sucessfullyAddedAllObjects = false;
            } else {
                if (selectionUpdate != null) {
                    selectionUpdate.update(obj, true);
                }
            }
        }
        return sucessfullyAddedAllObjects;
    }

    public boolean selectAll() {
        if (referenceSet != null) {
            return selectAll(referenceSet);
        } else {
            return false;
        }
    }

    public E getFirstSelected() {
        if (selectedObjects != null && selectedObjects.size() >= 1) {
            return selectedObjects.get(0);
        } else {
            return null;
        }
    }

    public int size() {
        return selectedObjects.size();
    }

    public E get(int index) {
        return selectedObjects.get(index);
    }

    public void clear() {
        unselectAll();
    }

}
