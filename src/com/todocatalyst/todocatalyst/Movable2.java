/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import java.util.List;

/**
 *
 * @author Thomas
 */
public interface Movable2 {

    /**
     * is the Movable2 a valid drop target for the content of the draggedObject.
     * If not, drop action is ignored.
     *
     * @param draggedObject
     * @return
     */
//    default public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//        return true;
//    };

    /**
     * returns the list that the dragged element belongs to
     *
     * @return
     */
//    public List getDragAndDropList();
//    default public ItemAndListCommonInterface getDragAndDropList(){
//        return null;
//    };

    /**
     * return the subList of the element onto which another element is dropped
     *
     * @return
     */
//    default public List getDragAndDropSubList(){
//        return null;
//    };

    /**
     * returns the object in the dragged or dropped
     *
     * @return
     */
    default public Object getDragAndDropObject(){
        return null;
    };

    /**
     * save the underlying list that is changed when an item is removed/added during drag&drop
     * @return
     */
//    default public void saveDragged(){};

    /**
     * returns the Category to which the specific Item container comes from.
     * Enables removing an item from a Category, or adding an item to a category
     * when dropping it on another item in the Category.
     *
     * @return
     */
    default public Category getDragAndDropCategory(){
        return null;
    };

//<editor-fold defaultstate="collapsed" desc="comment">
//            List getDragAndDropMotherList();
//            List getDragAndDropSubList();
//                    getDragAndDropObject();
//            this.getOrgList().insertObjectAtPos(x > this.getWidth() / 3 * 2, this.getOrgList().indexOf(), dragged.getObject());
//
//
//    /**
//     * remove this element from its owner (e.g. an Item or an ItemList) and returns the owner.
//     * Returns null if not belonging to owner. Also saves the updated owner.
//     * @return the owner (either an Item for a sub-task, or an ItemList)
//     */
//    public Object removeFromOwner();
//
//    /**
//     * add element to the owner of the Movable (e.g. an Item or an ItemList) at the position of the object in question (pushing elementAtInsertPosition and following
//     * elements one step down the list). Returns owner if inserted successfully, otherwise null.
//     * Also saves the updated owner.
//     * @param element
//     * @return the owner or null if no list existed or element was of wrong type
//     */
//    public Object insertIntoOwnerAtPositionOf(Movable2 element);
////    public Object insertIntoOwnerAtPositionOf(Movable element, Movable elementAtInsertPosition);
//
//    /**
//     * add element below the Movable (at first element of its sublist, eg as first subtask or first element in sublist). Returns sublist if inserted successfully, otherwise null.
//     * Also saves the updated owner.
//     * @param element
//     * @param elementAtInsertPosition
//     * @return the owner
//     */
//    public Object insertBelow(Movable2 element);
//</editor-fold>
}
