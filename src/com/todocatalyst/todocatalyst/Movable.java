/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 *
 * @author Thomas
 */
public interface Movable {
    
    /**
     * remove this element from its owner (e.g. an Item or an ItemList) and returns the owner. 
     * Returns null if not belonging to owner. Also saves the updated owner. 
     * @return the owner (either an Item for a sub-task, or an ItemList)
     */
//    public Object removeFromOwner();
   
    /**
     * add element to the owner of the Movable (e.g. an Item or an ItemList) at the position of the object in question (pushing elementAtInsertPosition and following
     * elements one step down the list). Returns owner if inserted successfully, otherwise null. 
     * Also saves the updated owner. 
     * @param element
     * @return the owner or null if no list existed or element was of wrong type
     */
//    public Object insertIntoOwnerAtPositionOf(Movable element);
//    public Object insertIntoOwnerAtPositionOf(Movable element, Movable elementAtInsertPosition);

    /**
     * add element below the Movable (at first element of its sublist, eg as first subtask or first element in sublist). Returns sublist if inserted successfully, otherwise null. 
     * Also saves the updated owner. 
     * @param element 
     * @param elementAtInsertPosition
     * @return the owner
     */
//    public Object insertBelow(Movable element);
    
    
}
