/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.todocatalyst.todocat;

import com.codename1.ui.list.ListModel;


/**
 * Not supposed to be used - use ItemList instead!!
 * @author Thomas
 */
public interface ItemListModel<E> extends ListModel /*, ItemChangeListener, ItemListChangeListener, ItemListChangeSender*/ {

    /**
     * Adding an item to list at given index
     * @param item - the item to add
     * @param index - the index position in the list
     */
    public void addItemAtIndex(E item, int index);
    public int getSize();

    /**
     * Update the item at the index position
     * @param index
     * @param item
     */
//    public void setItem(int index, Object item);

    /**
     * @inheritDoc
     */
    public Object getItemAt(int index);

    /**
     * @inheritDoc
     */
    public int getItemIndex(E item); //TODO: consider if this function should be added, could be useful for expand/collapse

    /**
     * @inheritDoc
     */
    public void removeItem(E item);

    /**
     * see implementation in ItemList
     */
    public void moveItem(int fromPos,  int toPos);

    /**
     * see doc in BaseItem
     * @param destiny
     */
    public void copyMeInto(E destiny);
    /**
     * see doc in BaseItem
     * @return
     */
    public ItemAndListCommonInterface cloneMe();

    public boolean addItemAtIndexTyped(E item, int index);
    public boolean addItemTyped(E item);

    public boolean isTyped();

    public int getBaseType();
    
    public boolean contains(E item);

    /**
     * returns the item at index, and removes it at the same time in one atomic operation (no updates).
     * @param index
     * @return
     */
    //public Object getAndRemoveItemAtIndex(int index); //- not needed
}
