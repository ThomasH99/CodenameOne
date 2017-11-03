/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * the same itemList can have different selections active at the same time. E.g.
 * the list of categories can be edited within an Item,
 *
 * @author Thomas
 */
public class SelectedItems { //implements ChangeEventListener/*, ItemListChangeListener*/ {

//    ItemListModel list;
    /**
     * the list for which items can be initiallySelectedItems. Used to listen to
     * events from the list about deleted items, so the initiallySelectedItems
     */
//    Set listY = new HashSet();
//    ItemList list;
    /**
     * the list of selected items- initially holds the pre-selected items,
     * returns the
     */
//    ItemList initiallySelectedItems;
    /**
     * hashtable contains the selected elements in the list (stored as pairs of
     * (o,o) since hashtables require pairs and Java ME doesn't have 'sets')
     */
//    Hashtable selectedItems;// = null;
    HashSet selectedItems = new HashSet();// = null;
    /**
     * stores the type that can be selected (e.g. to avoid selecting items in an
     * expanded Category list)
     */
//    int allowedSelectionType = -1;
    /**
     * if true, only a single object can be selected
     */
    boolean singleSelection;
    /**
     * list of objects that can NOT be selected (are disableb for selection)
     */
    UnselectableCalculator unselectableCalculator;
//    Vector unselectableItems;

    /**
     * if true, return the
     */
//    boolean returnSelectedItemsInInitiallySelectedItemList;

//    class UnselectableCalculator{
//        Vector getUnselectable() {
//            return null;
//        }
//    }
//    SelectedItems(ItemListModel list, ItemListModel selectedItems) {
//        setReferenceList(list);
////        setActive(); //needed in the case where selectedItems list is empty, in which case subsequent calls to isActive would return false
//        setSelected(selectedItems);
//    }
    /**
     * returns
     *
     * @param list the list for which items can be initiallySelectedItems. Used
     * to listen to events from the list about deleted items, so the deleted
     * items are also deleted from selectedItems (to ensure consistency)
     * @param initiallySelectedItems list of initially selected items - if null,
     * treated as no initially selected items
     * @param singleSelection if true, only a single item can be
     * initiallySelectedItems at any time
     * @param allowedSelectionType only BaseItemType objects of this type can be
     * initiallySelectedItems (-1 for no restrictions)
     * @param returnSelectedItemsInInitiallySelectedItemList if true, then
     * getSelected() returns the selected items, in the list
     * initiallySelectedItems
     *
     */
//    SelectedItems(ItemList list, ItemList initiallySelectedItems, boolean singleSelection, int allowedSelectionType, boolean returnSelectedItemsInInitiallySelectedItemList, Vector unselectableItems) {
//    SelectedItems(List list, ItemList initiallySelectedItems, boolean singleSelection, int allowedSelectionType, boolean returnSelectedItemsInInitiallySelectedItemList, UnselectableCalculator unselectableCalculator) {
//        setReferenceList(list);
//        this.initiallySelectedItems = initiallySelectedItems;
//        setSelected(initiallySelectedItems);
//        this.singleSelection = singleSelection;
//        this.returnSelectedItemsInInitiallySelectedItemList = returnSelectedItemsInInitiallySelectedItemList;
////        this.unselectableItems=unselectableItems;
//        setUnselectableCalculator(unselectableCalculator);
//        setAllowedSelectionType(allowedSelectionType);
//    }
    SelectedItems(Collection initiallySelectedItems) {
//        this.initiallySelectedItems = initiallySelectedItems;
        selectAll(initiallySelectedItems);
    }

//    SelectedItems(ItemList list, ItemList initiallySelectedItems, boolean singleSelection, int allowedSelectionType, boolean returnSelectedItemsInInitiallySelectedItemList) {
//        this(list, initiallySelectedItems, singleSelection, allowedSelectionType, returnSelectedItemsInInitiallySelectedItemList, null);
//    }
//    SelectedItems(ItemList list, BaseItem selected, boolean singleSelection, int allowedSelectionType) {
//        this(list, new ItemList(new BaseItem[]{selected}), singleSelection, allowedSelectionType, false, null);
////        setReferenceList(list);
////        select(initiallySelectedItems);
////        this.singleSelection = singleSelection;
////        setAllowedSelectionType(allowedSelectionType);
//    }
//    SelectedItems(ItemList list, ItemList initiallySelectedItems, boolean singleSelection, int allowedSelectionType, UnselectableCalculator unselectableCalculator) {
//        this(list, initiallySelectedItems, singleSelection, allowedSelectionType, false, unselectableCalculator);
//    }
//    SelectedItems(ItemList list, ItemList initiallySelectedItems, boolean singleSelection, int allowedSelectionType) {
//        this(list, initiallySelectedItems, singleSelection, allowedSelectionType, null);
//    }
    /**
     * create a selection list,
     *
     * @param list list of potentially selectable items
     * @param initiallySelectedItems the items initiallySelectedItems by
     * default. Can be null. If select is a single object (not instanceof
     * ItemListModel) then that object is initiallySelectedItems and a
     * single-selection list is created (where max one item at a time may be
     * initiallySelectedItems )
     */
    SelectedItems() {
//        setReferenceList(new ItemList());
//        this(new ItemList(), new ItemList(), true, -1);
        this(new HashSet());
    }

    interface UnselectableCalculator {

        boolean isUnselectable(Object o);
    }

    /**
     * define a function that determines if an element can be selected or not.
     * If called after some items have been selected, any already selected
     * elements that are unselectable will be unselected.
     *
     * @param unselectableCalculator
     */
    public void setUnselectableCalculator(UnselectableCalculator unselectableCalculator) {
        this.unselectableCalculator = unselectableCalculator;
        if (unselectableCalculator != null) {
            //unselect any previously 
            for (Object o : selectedItems) {
                if (this.unselectableCalculator.isUnselectable(o)) {
                    selectedItems.remove(o);
                }
            }
//            Vector unselectableItems = unselectableCalculator.isUnselectable();
//            for (int i = 0, size = unselectableItems.size(); i < size; i++) {
//                if (isSelected(unselectableItems.elementAt(i))) {
//                    unSelect(unselectableItems.elementAt(i));
//                }
//            }
        }
    }

    public void setSingleSelection(boolean setSingleSelection) {
        this.singleSelection = setSingleSelection;
        if (selectedItems.size() > 1) {
            throw new Error("Error: setting single selection with more than one selection already defined."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    public boolean isSingleSelection() {
        return singleSelection;
    }

    //TODO!!! for select etc: change implementation so only valid index (or ojbects actually in the list) can be initiallySelectedItems. Otherwise risk of logical errors. Do this by storing the index in the hashtable, and checking the index is legal. What about if a initiallySelectedItems item is removed - how to ensure consistency??!!?
    /**
     * set the object o as initiallySelectedItems. if o is not in the list, it
     * is not initiallySelectedItems (TODO: maybe change so o can be added does
     * not have to be in the list, however if it is initiallySelectedItems, and
     * then later added it will become initiallySelectedItems then.
     * Precondition: Checks that o is actually in the list (this is maybe not
     * necessary since wrong objects wouldn't harm the behaviour?).
     *
     * @param o
     */
    public void select(Object o) {
//        if (o == null) {
//            return;
//        }
//        if (o instanceof BaseItem && allowedSelectionType != -1 && ((BaseItem) o).getTypeId() != allowedSelectionType) {
//            return;
//        }
//        if (selectedItems == null) {
////            selectedItems = new Hashtable();
//            initialize();
//        }
        if (singleSelection && selectedItems.size() == 1) {
            return;
//            selectedItems = null; //help GC
//            selectedItems = new Hashtable();
//            reset(); //- no reason to stop listening to list
        }
//        if (getItemIndex(o) != -1) {//only select items actually in the list TODO: more efficient implementation!
//        if (unselectableCalculator == null || !unselectableCalculator.getUnselectable().contains(o)) {
        if (unselectableCalculator == null || !unselectableCalculator.isUnselectable(o)) {
            selectedItems.add(o);//new Integer(2));
        }//        }//        for (int i = 0, size = itemVector.size(); i < size; i++) { // optimization: faster way of finding the object??
//            if (itemVector.elementAt(i) == o) { //optimization: store elementAt in a temporary variable
//                selectedItems.put(o, o);//new Integer(2));
//                return;
//            }
//        }
    }
// <editor-fold defaultstate="collapsed" desc="comment">

    /**
     * removes an object from selectedItems which has been removed from the
     * itemVector. Used to ensure that the selectedItems list is kept consistent
     * with the actual itemVector (otherwise eg getSelectedItem() could return
     * items which are no longer in the list)
     */
//    private void removeSelected(Object o) {
//        if (selectedItems != null) {
//        selectedItems.remove(o);
//        }
//    }
    /**
     * set item at index initiallySelectedItems
     *
     * @param index of item to be initiallySelectedItems
     */
//    public void select(int index) {
//        select(list.getItemAt(index));
////        if (selectedItems == null)
////            selectedItems = new Hashtable();
////        Object o2 = getItemAt(index);
////        selectedItems.put(o2, o2);
//    }// </editor-fold>
    /**
     * make the selection 'active'
     */
//    private void initialize() {
//        selectedItems = new HashSet();
//    }
//    private boolean isActive() {
//        return selectedItems != null;
//    }
    /**
     * select all items in selectList. Already initiallySelectedItems will
     * remain initiallySelectedItems. Preconditions: selectList empty: OK; null:
     * OK. selectlist contains elements not in list: OK (since they won't be set
     * by select(o2); list itself empty: OK (since they won't be set by
     * select(o2)
     *
     * @param selectedList
     */
//    public void addSelected(ItemListModel selectList) {
    public void selectAll(Collection selectList) {
        if (selectList == null) {
            return;
        }

//        this.selectedItems.addAll(selectList); //NOT good if we have singleSelection on
        for (Object o : selectList) {
            select(o);
        }
//        for (int i = 0, size = selectList.size(); i < size; i++) { // optimization: faster way of finding the object??
//            select(selectList.get(i)); // hashtable.put(o2, o2);//new Integer(3));
//            Object o2 = selectList.getItemAt(i);
//            select(o2); // hashtable.put(o2, o2);//new Integer(3));
//        }
    }

    /**
     * selects exactly the items in selectList (if any items were previously
     * initiallySelectedItems, they are unselected). selectList can be empty or
     * null, in which case no items are initiallySelectedItems. Selectlist may
     * contain elements that are not in list. list itself empty: OK (since they
     * won't be set by select(o2)
     *
     * @param selectedList
     */
//    public void setSelected(ItemListModel selectList) {
    public void setSelected(Collection selectList) {
//        reset();
        selectedItems = null;
        selectAll(selectList);
    }

//    public void setReferenceList(ItemList list) {
//        this.list = list;
////        list.addItemListChangeListener(this); //start listening to changes to the list    }
//        list.addChangeEventListener(this); //start listening to changes to the list    }
//    }
//    public void setAllowedSelectionType(int baseItemType) {
//        allowedSelectionType = baseItemType;
//    }
    /**
     * unselect all and remove as changeListener on the list
     */
    public void reset() {
        selectedItems = new HashSet(); //null; // help garbage collector
//        list.removeItemListChangeListener(this); //remove myself as change listener
//        list.removeChangeEventListener(this); //remove myself as change listener
//        list = null;
//        allowedSelectionType = -1;
//        selectedItems = new Hashtable();
    }

    /**
     * unselects o (if not in list, nothings happens)
     *
     * @param o
     */
    public void unSelect(Object o) {
//        if (selectedItems != null) {
        selectedItems.remove(o);
//        }
//        if (selectedItems == null) {
//            return;
//        }
//        selectedItems.remove(o);
//        for (int i = 0, size = items.size(); i<size; i++) { // optimization: faster way of finding the object??
//            if (items.elementAt(i) == o) { //optimization: store elementAt in a temporary variable
//                hashtable.remove(o);
//                return;
//            }
//        }
    }
// <editor-fold defaultstate="collapsed" desc="comment">

    /**
     * set the currently initiallySelectedItems item (at index
     * getSelectedItem()) as initiallySelectedItems
     *
     * @param index of item to be initiallySelectedItems
     */
//    public void select() {
//        select(getSelectedIndex());
//    }
//    public void unSelect(int index) {
////        if (selectedItems== null) return;
//        unSelect(getItemAt(index));
////        selectedItems.remove(o2);
//    }
//    public void unSelect() {
////        if (selectedItems== null) return;
//        unSelect(getSelectedIndex());
////        selectedItems.remove(o2);
//    }
    /**
     * changes the currently initiallySelectedItems item (at index
     * getSelectedItem()) from initiallySelectedItems to unselected or
     * vice-versa.
     *
     * @param index of item to be initiallySelectedItems
     */
//    public void flipSelected() {
//        if (isSelected()) {
//            unSelect();
//        } else {
//            select();
//        }
//    }// </editor-fold>
    public void flipSelection(Object o) {
        if (isSelected(o)) {
            unSelect(o);
        } else {
            select(o);
        }
    }

    public boolean isSelected(Object o) {
//        return selectedItems.containsKey(o);
//        if (selectedItems == null || o == null) {
        if (o == null) {
            return false;
        } else {
            return selectedItems.contains(o); // && getItemIndex(o) != -1; //only return true for items actually in the list
        }
//        return (selectedItems!=null || selectedItems.containsKey(o));
    }

// <editor-fold defaultstate="collapsed" desc="comment">
    /**
     * checks if item at index is initiallySelectedItems
     *
     * @param index of initiallySelectedItems item
     * @return true if item is initiallySelectedItems, otherwise false
     */
//    public boolean isSelected(int index) {
////        return selectedItems.containsKey(getItemAt(index));
//        return (isSelected(getItemAt(index))); //selectedItems.containsKey(getItemAt(index)));
//    }
//    public boolean isSelected() {
////        return selectedItems.containsKey(getItemAt(index));
//        return (isSelected(getSelectedIndex())); //selectedItems.containsKey(getItemAt(index)));
//    }// </editor-fold>
    /**
     * unselect all items in unselectList (if they are initiallySelectedItems,
     * then unselect them) Preconditions: unselectList contains items that are
     * not initiallySelectedItems: OK (since unset
     *
     * @param unselectList
     */
    public void unSelect(Collection unselectList) {
        for (Object o : unselectList) {
            unSelect(o);
        }
//        for (int i = 0, size = unselectList.size(); i < size; i++) { // optimization: faster way of finding the object??
//            Object o2 = unselectList.get(i);
//            unSelect(o2); // hashtable.remove(o2);
//        }
    }

    /**
     * Returns a copy of the set of selected elements WRONG: returns an ItemList
     * containing (only) the initiallySelectedItems elements in the list (in no
     * particular order - due to use of Hashtable). List is empty if no elements
     * are initiallySelectedItems. Preconditions: no initiallySelectedItems
     * elements: OK (returns empty list).
     *
     * @return
     */
//    public ItemList getSelected() {
    public Set getSelected() {
//        ItemList initiallySelectedItems = new ItemList(false,false); //- don't return a copy of the initial ItemList with initiallySelectedItems items, but update the list
        return new HashSet(selectedItems);
//        ItemList returnList;
//        if (returnSelectedItemsInInitiallySelectedItemList) {
////            if (selectedItems == null || selectedItems.isEmpty()) { //if no selections were made, return the empty list
//            if (selectedItems.isEmpty()) { //if no selections were made, return the empty list
//                return initiallySelectedItems; //new ItemList();
//            } else {
//                if (initiallySelectedItems == null) {
//                    initiallySelectedItems = new ItemList(false, false);
//                } else {
//                    initiallySelectedItems.removeAllItems();
//                }
//                returnList = initiallySelectedItems;
//            }
//        } else { // !returnSelectedItemsInInitiallySelectedItemList
//            returnList = new ItemList(false, false); //- don't return a copy of the initial ItemList with initiallySelectedItems items, but update the list
//            if (selectedItems == null) { //if no selections were made, return the empty list
//                return returnList;
//            }
//        }
//        Enumeration selectedEnumeration = selectedItems.elements();
//        while (selectedEnumeration.hasMoreElements()) {
//            Object item = selectedEnumeration.nextElement();
//            if (isSelected(item)) {
//                returnList.addItem(item);
//            }
//        }
//        return returnList;
    }

    /**
     * returns a single initiallySelectedItems element (assuming that
     * singleSelection mode is chosen, otherwise returns a random
     * initiallySelectedItems element). If no selected elements, returns null.
     */
    public Object getSelectedElement() {
//        if (selectedItems == null || selectedItems.size() == 0) {
//            return null;
//        } else {
//            return selectedItems.elements().nextElement();
        Iterator it = selectedItems.iterator();
        if (it.hasNext()) {
            return it.next();
        } else {
            return null;
        }
//        }
    }

    /**
     * returns a Vector containing (only) the initiallySelectedItems elements in
     * the list (in no particular order - due to use of Hashtable). Vector is
     * empty if no elements are initiallySelectedItems. Preconditions: no
     * initiallySelectedItems elements: OK (returns empty list).
     *
     * @return
     */
//    public Vector getSelectedAsVector() {
//        Vector selectedList = new Vector();
//        if (selectedItems == null) {
//            return selectedList; //new ItemList();
//        }
//        Enumeration selected = selectedItems.elements();
//        while (selected.hasMoreElements()) {
//            Object item = selected.nextElement();
//            if (isSelected(item)) {
//                selectedList.addElement(item);
//            }
//        }
//        return selectedList;
//    }
//    public void xsetSelectionInUse(boolean on) {
////        selectedItems = null; // help garbage collector
//        if (on) {
//            selectedItems = new Hashtable(); //creating ane empty hashtable ensure that isSelectionEmpty() returns true - this is used by UniversalRenderer to know it needs to show selections
//        }
//    }
//    public boolean isSelectionActive() {
//        return (selectedItems != null ); //selectedItems.containsKey(getItemAt(index)));
//    }
    /**
     * returns true if nothing is initiallySelectedItems (or if selection is not
     * initialized)
     */
    public boolean isSelectionEmpty() {
        return (selectedItems == null || (selectedItems != null && !selectedItems.isEmpty())); //selectedItems.containsKey(getItemAt(index)));
    }

//    public void changedItemList(int itemListChangeType, ItemListModel list, int index, BaseItem changedItem) {
//        if (itemListChangeType == ItemListChangeListener.IL_REMOVED) {
//            if (selectedItems != null) { //check that selectedItems is defined to avoid unnecessary calls to getItemAt(index)
//                unSelect(changedItem);
//            }
//        }
//    }
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
//        int itemListChangeType = changeEvent.getChangeId();
//        ItemList list = (ItemList) changeEvent.getSource();
//        int index = changeEvent.getListIndex();
//        BaseItem changedItem = (BaseItem) changeEvent.getListObject();
//
////        if (itemListChangeType == ItemListChangeListener.IL_REMOVED) {
//        if (ChangeValue.isSet(itemListChangeType, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED)) {
//            if (selectedItems != null) { //check that selectedItems is defined to avoid unnecessary calls to getItemAt(index)
//                unSelect(changedItem);
//            }
//        }
//        if (ChangeValue.isSet(itemListChangeType, ChangeValue.CHANGED_BASEITEM_DELETED)) {
//            list.removeChangeEventListener(this); //stop listening to deleted list
//        }
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns a list of all defined categories with the ones in the list
     * itemCategories set to initiallySelectedItems
     *
     * @param itemList the list of categories to be set as
     * initiallySelectedItems (e.g. list coming from an item's list of
     * categories)
     * @return list with all categories, where the ones in itemCategories have
     * been set to initiallySelectedItems
     */
//    public ItemListSelected getSelectedList(ItemListModel itemList) {
//        ItemListSelected selectedCategories = new ItemListSelected(itemList, false);
//        selectedCategories.select(itemList);
////       for (int i = 0, size = itemCategories.getSize(); i<size; i++) {
////           selectedCategories.select(itemCategories.getItemAt(i));
////       }
//        return selectedCategories;
//    }
//TODO!!! for select etc: change implementation so only valid index (or ojbects actually in the list) can be initiallySelectedItems. Otherwise risk of logical errors. Do this by storing the index in the hashtable, and checking the index is legal. What about if a initiallySelectedItems item is removed - how to ensure consistency??!!?
//    /**
//     * set the object o as initiallySelectedItems. if o is not in the list, it is not initiallySelectedItems (TODO: maybe change so o can be added does not have to be in the list, however if it is initiallySelectedItems, and then
//     * later added it will become initiallySelectedItems then.
//     * Precondition: Checks that o is actually in the list (this is maybe not necessary since wrong objects
//     * wouldn't harm the behaviour?).
//     * @param o
//     */
//    public void select(Object o) {
//        if (o instanceof BaseItem && allowedSelectionType != -1 && ((BaseItem) o).getTypeId() != allowedSelectionType) {
//            return;
//        }
//        if (selectedItems == null) {
//            selectedItems = new Hashtable();
//        }
//        if (list.getItemIndex(o) != -1) {//only select items actually in the list TODO: more efficient implementation!
//            selectedItems.put(o, o);//new Integer(2));
//        }//        for (int i = 0, size = itemVector.size(); i < size; i++) { // optimization: faster way of finding the object??
////            if (itemVector.elementAt(i) == o) { //optimization: store elementAt in a temporary variable
////                selectedItems.put(o, o);//new Integer(2));
////                return;
////            }
////        }
//    }
//
//    /** removes an object from selectedItems which has been removed from the itemVector.
//     * Used to ensure that the selectedItems list is kept consistent with the actual itemVector
//     * (otherwise eg getSelectedItem() could return items which are no longer in the list)
//     */
//    private void removeSelected(Object o) {
//        selectedItems.remove(o);
//    }
//
//    /**
//     * set item at index initiallySelectedItems
//     * @param index of item to be initiallySelectedItems
//     */
//    public void setSelected(int index) {
//        select(list.getItemAt(index));
////        if (selectedItems == null)
////            selectedItems = new Hashtable();
////        Object o2 = getItemAt(index);
////        selectedItems.put(o2, o2);
//    }
//
//    /**
//     * unselects o (if not in list, nothings happens)
//     * @param o
//     */
//    public void unsetSelected(Object o) {
//        if (selectedItems == null) {
//            return;
//        }
//        selectedItems.remove(o);
////        for (int i = 0, size = items.size(); i<size; i++) { // optimization: faster way of finding the object??
////            if (items.elementAt(i) == o) { //optimization: store elementAt in a temporary variable
////                hashtable.remove(o);
////                return;
////            }
////        }
//    }
//
//    /**
//     * set the currently initiallySelectedItems item (at index getSelectedItem()) as initiallySelectedItems
//     * @param index of item to be initiallySelectedItems
//     */
//    public void setSelected() {
//        setSelected(list.getSelectedIndex());
//    }
//
//    public void unsetSelected(int index) {
////        if (selectedItems== null) return;
//        unsetSelected(list.getItemAt(index));
////        selectedItems.remove(o2);
//    }
//
//    public void unsetSelected() {
////        if (selectedItems== null) return;
//        unsetSelected(list.getSelectedIndex());
////        selectedItems.remove(o2);
//    }
//
//    /**
//     * changes the currently initiallySelectedItems item (at index getSelectedItem()) from initiallySelectedItems to unselected or vice-versa.
//     * @param index of item to be initiallySelectedItems
//     */
//    public void flipSelected() {
//        if (isSelected()) {
//            unsetSelected();
//        } else {
//            setSelected();
//        }
//    }
//
//    public boolean isSelected(Object o) {
////        return selectedItems.containsKey(o);
//        if (selectedItems == null) {
//            return false;
//        } else {
//            return selectedItems.containsKey(o) && list.getItemIndex(o) != -1; //only return true for items actually in the list
//        }
////        return (selectedItems!=null || selectedItems.containsKey(o));
//    }
//
//    /**
//     * checks if item at index is initiallySelectedItems
//     * @param index of initiallySelectedItems item
//     * @return true if item is initiallySelectedItems, otherwise false
//     */
//    public boolean isSelected(int index) {
////        return selectedItems.containsKey(getItemAt(index));
//        return (isSelected(list.getItemAt(index))); //selectedItems.containsKey(getItemAt(index)));
//    }
//
//    public boolean isSelected() {
////        return selectedItems.containsKey(getItemAt(index));
//        return (isSelected(list.getSelectedIndex())); //selectedItems.containsKey(getItemAt(index)));
//    }
//
//    /**
//     * select all items in selectList. Already initiallySelectedItems will remain initiallySelectedItems.
//     * Preconditions:
//     * selectList empty: OK;
//     * selectlist contains elements not in list: OK (since they won't be set by select(o2);
//     * list itself empty: OK (since they won't be set by select(o2)
//     * @param selectedList
//     */
//    public void addSelected(ItemListModel selectList) {
//        for (int i = 0, size = selectList.getSize(); i < size; i++) { // optimization: faster way of finding the object??
//            select(selectList.getItemAt(i)); // hashtable.put(o2, o2);//new Integer(3));
////            Object o2 = selectList.getItemAt(i);
////            select(o2); // hashtable.put(o2, o2);//new Integer(3));
//        }
//    }
//
//    /**
//     * unselect all previously initiallySelectedItems items and select exactly the items in selectList.
//     * selectList empty: OK;
//     * selectlist contains elements not in list: OK (since they won't be set by select(o2);
//     * list itself empty: OK (since they won't be set by select(o2)
//     * @param selectedList
//     */
//    public void setSelected(ItemListModel selectList) {
//        unsetAll();
//        addSelected(selectList);
//    }
//
//    public void setAllowedSelectionType(int baseItemType) {
//        allowedSelectionType = baseItemType;
//    }
//
//    /**
//     * unselect all items in unselectList (if they are initiallySelectedItems, then unselect them)
//     * Preconditions:
//     * unselectList contains items that are not initiallySelectedItems: OK (since unset
//     * @param unselectList
//     */
//    public void unsetSelected(ItemList unselectList) {
//        for (int i = 0, size = unselectList.getSize(); i < size; i++) { // optimization: faster way of finding the object??
//            Object o2 = unselectList.getItemAt(i);
//            unsetSelected(o2); // hashtable.remove(o2);
//        }
//    }
//
//    /**
//     * returns an ItemList containing (only) the initiallySelectedItems elements in the list
//     * Preconditions:
//     * no initiallySelectedItems elements: OK (returns empty list).
//     * @return
//     */
//    public ItemList getAllSelected() {
//        ItemList selectedList = new ItemList();
//        if (selectedItems == null) {
//            return selectedList; //new ItemList();
//        }
//        Enumeration initiallySelectedItems = selectedItems.elements();
//        while (initiallySelectedItems.hasMoreElements()) {
//            Object item = initiallySelectedItems.nextElement();
//            if (isSelected(item)) {
//                selectedList.addItem(item);
//            }
//        }
//        return selectedList;
//    }
//
//    public Vector getAllSelectedAsVector() {
//        Vector selectedList = new Vector();
//        if (selectedItems == null) {
//            return selectedList; //new ItemList();
//        }
//        Enumeration initiallySelectedItems = selectedItems.elements();
//        while (initiallySelectedItems.hasMoreElements()) {
//            Object item = initiallySelectedItems.nextElement();
//            if (isSelected(item)) {
//                selectedList.addElement(item);
//            }
//        }
//        return selectedList;
//    }
//
//    /**
//     * unselect all
//     */
//    public void unsetAll() {
//        selectedItems = null; // help garbage collector
//        allowedSelectionType = -1;
////        selectedItems = new Hashtable();
//    }
//
//    public void xsetSelectionInUse(boolean on) {
////        selectedItems = null; // help garbage collector
//        if (on) {
//            selectedItems = new Hashtable(); //creating ane empty hashtable ensure that isSelectionEmpty() returns true - this is used by UniversalRenderer to know it needs to show selections
//        }
//    }
//
//    public boolean isSelectionEmpty() {
//        return (selectedItems != null); //selectedItems.containsKey(getItemAt(index)));
//    }
//
//    public void changedItemList(int changeType, ItemListModel list, int index, BaseItem changedItem) {
//        if (changeType == ItemListChangeListener.IL_REMOVED) {
//            if (selectedItems != null) { //check that selectedItems is defined to avoid unnecessary calls to getItemAt(index)
//                unSelect(changedItem);
//            }
//        }
//    }
//    /**
//     * returns a list of all defined categories with the ones in the list itemCategories set
//     * to initiallySelectedItems
//     * @param itemList the list of categories to be set as initiallySelectedItems (e.g. list coming from an item's list of categories)
//     * @return list with all categories, where the ones in itemCategories have been set to initiallySelectedItems
//     */
////    public ItemListSelected getSelectedList(ItemListModel itemList) {
////        ItemListSelected selectedCategories = new ItemListSelected(itemList, false);
////        selectedCategories.select(itemList);
//////       for (int i = 0, size = itemCategories.getSize(); i<size; i++) {
//////           selectedCategories.select(itemCategories.getItemAt(i));
//////       }
////        return selectedCategories;
////    }// </editor-fold>
}
