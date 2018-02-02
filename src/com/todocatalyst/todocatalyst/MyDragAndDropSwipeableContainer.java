/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.Layout;
import com.parse4cn1.ParseObject;
import java.util.List;

/**
 * top-level container used in list to support (activatable) drag and drop
 *
 * @author Thomas
 */
class MyDragAndDropSwipeableContainer extends SwipeableContainer implements Movable2 {

    MyDragAndDropSwipeableContainer(Component bottomLeft, Component bottomRight, Component top) {
        super(bottomLeft, bottomRight, top);
        setDropTarget(true); //containers are both dropTargets and draggable
        setDraggable(false); //set false by default to allow scrolling. LongPress will activate, drop will deactivate it
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//        return true;
//    }
//
//    @Override
//    public ItemAndListCommonInterface getDragAndDropList() {
//        return null;
//    }
//
//    @Override
//    public List getDragAndDropSubList() {
//        return null;
//    }
//
//    @Override
//    public void saveDragged() {
//    }
//
//    @Override
//    public Object getDragAndDropObject() {
//        return null;
//    }
//</editor-fold>
    @Override
    public Category getDragAndDropCategory() {
        Container parent = getParent();
        //iterate up the container hierarchy for an Item to see if the above object is a category
        while (parent != null) {
//            if (parent instanceof MyDragAndDropSwipeableContainer && ((MyDragAndDropSwipeableContainer)parent).getDragAndDropCategory()!=null)
//                return ((MyDragAndDropSwipeableContainer)getParent()).getDragAndDropCategory();
//            else 
//                return null;
            //only move up to first containing MyDragAndDropSwipeableContainer (avoid that eg a subtaskreturns its expaned project's category)
            if (parent instanceof MyDragAndDropSwipeableContainer) {
                return ((MyDragAndDropSwipeableContainer) parent).getDragAndDropCategory();
            } else {
                parent = parent.getParent();
            }
        }
        return null;
    }

    @Override
    public void pointerReleased(int x, int y) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (pointerReleasedListeners != null && pointerReleasedListeners.hasListeners()) {
//            ActionEvent ev = new ActionEvent(this, ActionEvent.Type.PointerReleased, x, y);
//            pointerReleasedListeners.fireActionEvent(ev);
//            if(ev.isConsumed()) {
//                return;
//            }
//        }
//        pointerReleaseImpl(x, y);
//        scrollOpacity = 0xff;
//</editor-fold>
        super.pointerReleased(x, y);
        Log.p("MyDragAndDropSwipeableContainer.pointerReleased (D&D) x=" + x + " y=" + y);
    }

//    public void refreshAfterDrop() {
//        ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//    }
    /**
     * returns true if dropped elements should be inserted *below* the item it
     * is dropped on. Eg if dropped of right-hand third of the dropTarget
     * container.
     *
     * @param x
     * @return
     */
    private boolean insertBelow(int x) {
        return x > this.getWidth() / 3 * 2;
    }

    @Override
    public void drop(Component dragged, int x, int y) {
        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself //TODO remove isValidDropTarget checks as code below should check valid conditions and do nothing if not
            return;
        }
        boolean dropExecuted = false;
        if (dragged instanceof MyDragAndDropSwipeableContainer) {
            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
            Object dropTarget = getDragAndDropObject();
            Object draggedObject = draggedCont.getDragAndDropObject();
            ItemAndListCommonInterface insertIntoList = null;
            ItemAndListCommonInterface removeFromList = null;
            int index = -1;
            if (dropTarget instanceof Item && (draggedObject instanceof ItemList || draggedObject instanceof Category)) {
                return; //do nothing if dragging an ItemList or a Category onto an Item
            }
            if (dropTarget instanceof Category) { //D&D onto a Category1

                if (draggedObject instanceof Category) { //D&D ... of a Category2 => move to position of Category1

                    Category draggedCategory = (Category) draggedObject;
                    Category dropTargetCategory = (Category) dropTarget;
//                    removeFromList = draggedCont.getDragAndDropList();

                    draggedCont.getDragAndDropList().removeFromList(draggedCategory);
//                    insertIntoList = getDragAndDropList(); //UI:
                    index = getDragAndDropList().getItemIndex(dropTargetCategory); //get index where to drop
                    getDragAndDropList().addToList(index, draggedCategory);

                    DAO.getInstance().save((ParseObject) getDragAndDropList()); //save the list of categories
                    dropExecuted = true;

                } else if (draggedObject instanceof Item) { //D&D ... of an Item onto a Category

//                    Category addToCategory = getDragAndDropCategory(); //UI: 
                    Category addToCategory = (Category) dropTarget; //UI: 
                    Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
//                    if (addToCategory != null && removeFromCategory!=null) {
//                    index = addToCategory.getList().indexOf(dropTarget);
                    Item item = ((Item) draggedObject);

                    item.addCategoryToItem(addToCategory, true); //true => add item to category as well
                    if (removeFromCategory != null) { //if dragged is an expanded subtask, it may not be in any category
                        item.removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null, true => remove item from category as well
//                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
                        DAO.getInstance().save(removeFromCategory); //save the categoy (removeFromCategory may be the same as addToCategory, but second save will just be ignored)
                    }

                    DAO.getInstance().save(addToCategory); //save the categoy that item was added to (can be null if no category, eg a subtask dragged onto an item in a category)
                    DAO.getInstance().save(item); //save the dragged Item (the categories are saved below via saveDragged)
                    dropExecuted = true;
                } else {
                    assert false : "shouldn't happen (eg ItemList should never appear in same list as Category)";
//                    index = -1;
                }

            } else if (dropTarget instanceof Item) {

                if (draggedObject instanceof Item) {
                    Item targetItem = ((Item) dropTarget);
                    Item draggedItem = ((Item) draggedObject);

                    if (insertBelow(x)) { //dropping item2 as a subtask of item1
//                        Item targetItem = ((Item) dropTarget);
//                        Item draggedItem = ((Item) draggedObject);
                        ItemAndListCommonInterface owner = draggedCont.getDragAndDropList();
                        if (owner != null && !(owner instanceof Category)) { //check on Category to avoid removing a subtask which is dragged from a Category
//                        draggedCont.getDragAndDropList().removeFromList(draggedItem); //remove subtask from previous list (remove first to remove Owner)
                            owner.removeFromList(draggedItem); //remove subtask from previous list (remove first to remove Owner)
                            DAO.getInstance().save((ParseObject) owner); //save the list from which subtask was removed
                        }
                        targetItem.addToList(0, draggedItem); //insert at head of sublist

                        DAO.getInstance().save(targetItem); //save project onto which subtask was dropped
//                        DAO.getInstance().save((ParseObject) draggedCont.getDragAndDropList()); //save the list from which subtask was removed
                        DAO.getInstance().save(draggedItem); //save the dragged Item (the categories are saved below via saveDragged)
                        dropExecuted = true;

                    } else if (getDragAndDropCategory() != null) { //D&D item1 from category1 onto an item2 in category2 => insert item1 into category2 at item2's position
                        //D&D ... of an item2 in category2 => insert item2 into category1 at item1's position and remove item2 from category2
                        Category addToCategory = getDragAndDropCategory(); //UI: 
                        Item item = ((Item) draggedObject);
                        Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
                        if (removeFromCategory != null) {
                            item.removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null, true => remove item from category as well
                            DAO.getInstance().save(removeFromCategory); //save the categoy (removeFromCategory may be the same as addToCategory, but second save will just be ignored)
//                        item.addCategoryToItem(addToCategory, true); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
                        }
                        index = addToCategory.getList().indexOf(targetItem); //get index *after* removing the item, in case we're D&D inside the same category
//                        addToCategory.add(index, item); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
                        addToCategory.addItemToCategory(item, index, true); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
//                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)

                        DAO.getInstance().save(addToCategory); //save the categoy that item was added to (can be null if no category, eg a subtask dragged onto an item in a category)
                        DAO.getInstance().save(item); //save the dragged Item (the categories are saved below via saveDragged)
                        dropExecuted = true;

                    } else { //'normal' D&D of item2 onto item1: move item2 from previous list and insert into item1's list at item1's position
                        insertIntoList = getDragAndDropList();
                        if (insertIntoList != null) { //eg in a ParseQuery list there may be items without an owner
                            removeFromList = draggedCont.getDragAndDropList();
                            if (removeFromList != null) {
                                removeFromList.removeFromList(draggedItem); //if same list, remove *before* calculating index 
                                index = insertIntoList.getItemIndex(targetItem);

                                if (insertIntoList instanceof Category) {
                                    ((Category) insertIntoList).addItemToCategory(draggedItem, index, true);
                                } else {
                                    insertIntoList.addToList(index, draggedItem);
                                }

                                DAO.getInstance().save((ParseObject) removeFromList);
                                if (insertIntoList != removeFromList) {
                                    DAO.getInstance().save((ParseObject) insertIntoList); //save the dragged Item (the categories are saved below via saveDragged)
                                }
                                DAO.getInstance().save(draggedItem); //save the dragged Item (the categories are saved below via saveDragged)
                                dropExecuted = true;
                            }
                        }
                    }
                } // else do nothing if dragging eg an ItemList or a Category onto an Item. //DONE: consider if dragging a Category onto an item should add the category to it (NO, less intuitive than dragging the item onto the Category)

            } else if (dropTarget instanceof ItemList) {

                if (draggedObject instanceof Item) { //insert dragged item into head of ItemList
                    ItemList dropTargetItemList = (ItemList) dropTarget;
                    Item draggedItem = (Item) draggedObject;

                    dropTargetItemList.addToList(0, draggedItem); // 0 => insert into head of itemList when dropped on the container of the list itself
                    removeFromList = draggedCont.getDragAndDropList();
                    removeFromList.removeFromList(draggedItem); //if same list, remove before index

                    DAO.getInstance().save(dropTargetItemList); //save new list
                    DAO.getInstance().save((ParseObject) removeFromList); //save previous lsit
                    DAO.getInstance().save(draggedItem); //save item, has changed owner

                } else if (draggedObject instanceof ItemList) { //itemList onto ItemList => insert
                    ItemList dropTargetItemList = (ItemList) dropTarget;
                    ItemList draggedItemList = (ItemList) draggedObject;

                    removeFromList = draggedCont.getDragAndDropList();
                    removeFromList.removeFromList(draggedItemList);

                    insertIntoList = getDragAndDropList();
                    index = insertIntoList.getItemIndex(dropTargetItemList);
//                    getDragAndDropList().addToList(index, draggedItemList); // 0 => insert into head of itemList when dropped on the container of the list itself
                    insertIntoList.addToList(index, draggedItemList); // 0 => insert into head of itemList when dropped on the container of the list itself

                    DAO.getInstance().save((ParseObject) insertIntoList); //save updated list of ItemLists
                    dropExecuted = true;

                } else {
                    assert false;
                }
            }

            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
            if (dropExecuted) {
                ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //simply keep same position as whereto the list was scrolled during the drag, then inserted element should 'stay in place'
//                removePlaceholder(); //do *before* setting dropPlaceholder=null!
//                dropPlaceholder = null; //reset placeholder
            }
//            if (false) { //now done in dragFinished()
//            removePlaceholder(); //do *before* setting dropPlaceholder=null!
//            refreshAfterDrop();
            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw

//            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void dropXXX(Component dragged, int x, int y) {
//        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
//            return;
//        }
//        if (dragged instanceof MyDragAndDropSwipeableContainer) {
//            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
//            Object dropTarget = getDragAndDropObject();
//            Object draggedObject = draggedCont.getDragAndDropObject();
//            ItemAndListCommonInterface insertIntoList = null;
//            ItemAndListCommonInterface removeFromList = null;
//            int index = -1;
//            if (dropTarget instanceof Item && (draggedObject instanceof ItemList || draggedObject instanceof Category)) {
//                return; //do nothing if dragging an ItemList or a Category onto an Item
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////else //D&D for categories is a special case
////            if ((dropTarget instanceof Category && draggedObject instanceof Item) || (draggedCont.getDragAndDropCategory()!=null)) {
////            if (getComponentForm() instanceof ScreenListOfCategories) {
////</editor-fold>
//            if (dropTarget instanceof Category) { //D&D onto a Category1
//
//                if (draggedObject instanceof Category) { //D&D ... of a Category2 => move to position of Category1
//
//                    removeFromList = draggedCont.getDragAndDropList();
//                    insertIntoList = getDragAndDropList(); //UI:
//                    index = getDragAndDropList().getItemIndex((ItemAndListCommonInterface) dropTarget);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    ((Item) draggedObject).addCategoryToItem((Category) insertIntoList, false);
////                    ((Item) draggedObject).removeCategoryFromItem((Category) removeFromList, false);
////                    DAO.getInstance().save(((Item) draggedObject)); //save item w changed categories
////</editor-fold>
//
//                } else if (draggedObject instanceof Item) { //D&D ... of an Item
//
////<editor-fold defaultstate="collapsed" desc="comment">
////                    removeFromList = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
////                    insertIntoList = (Category) dropTarget; //UI:
////                    index = 0; //getDragAndDropList().getList().indexOf(dropTarget);
////                    ((Item) draggedObject).addCategoryToItem((Category) insertIntoList, false);
////                    ((Item) draggedObject).removeCategoryFromItem((Category) removeFromList, false); //no effect if (Category)removeFromList == null
////                    DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
////                    // item has now moved category, nothing more to do so setting lists to null
////                    removeFromList = null;
////                    insertIntoList = null;
////</editor-fold>
//                    Category addToCategory = getDragAndDropCategory(); //UI:
//                    index = addToCategory.getList().indexOf(dropTarget);
//                    Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
//                    ((Item) draggedObject).addCategoryToItem(addToCategory, true); //true => add item to category as well
//                    ((Item) draggedObject).removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null, true => remove item from category as well
////                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
//                    DAO.getInstance().save((Item) draggedObject); //save the dragged Item (the categories are saved below via saveDragged)
//                } else {
//                    assert false : "shouldn't happen (eg ItemList should never appear in same list as Category)";
////                    index = -1;
//                }
//
//            } else if (dropTarget instanceof Item) {
//
//                if (draggedObject instanceof Item) {
//
//                    if (insertBelow(x)) { //dropping item2 as a subtask of item1
////<editor-fold defaultstate="collapsed" desc="comment">
////                getDragAndDropSubList().add(draggedObject); //insert items into the list of categories
////                ((Item) draggedObject).getList().add(((Item) draggedObject).getList().indexOf(dropTarget), draggedObject);
////                ((Item) draggedObject).getList().add(0, draggedObject); //insert at head of sublist
////                    ((ItemAndListCommonInterface) dropTarget).getList().add(0, draggedObject); //insert at head of sublist
////</editor-fold>
//                        ((Item) dropTarget).addToList(0, (Item) draggedObject); //insert at head of sublist
//                        insertIntoList = null; //insert handled above
//                        index = -1;
//                        removeFromList = draggedCont.getDragAndDropList();
//
//                    } else if (getDragAndDropCategory() != null) { //D&D item1 from category1 onto an item2 in category2 => insert item1 into category2 at item2's position
//                        //D&D ... of an item2 in category2 => insert item2 into category1 at item1's position and remove item2 from category2
////                        insertIntoList = getDragAndDropCategory(); //UI:
//                        Category addToCategory = getDragAndDropCategory(); //UI:
////                        index = getDragAndDropCategory().getList().indexOf(dropTarget);
////                        index = ((Category) insertIntoList).getList().indexOf(dropTarget);
//                        index = addToCategory.getList().indexOf(dropTarget);
////                        removeFromList = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
////                        removeFromList = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
//                        Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
////                        ((Item) draggedObject).addCategoryToItem((Category) insertIntoList, false);
//                        ((Item) draggedObject).addCategoryToItem(addToCategory, true);
////                        ((Item) draggedObject).removeCategoryFromItem((Category) removeFromList, false); //no effect if (Category)removeFromList == null
//                        ((Item) draggedObject).removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null
////                DAO.getInstance().save(((Item) draggedObject)); //save item w changed categories
////                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
//                        DAO.getInstance().save((Item) draggedObject); //save the dragged Item (the categories are saved below via saveDragged)
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (draggedObject instanceof Item) {
////                    if(draggedCont.getDragAndDropCategory() != null) { //D&D ... of an item2 in category2 => insert item2 into category1 at item1's position and remove item2 from category2
////                        removeFromList = draggedCont.getDragAndDropCategory(); //either a category or null
////                        insertIntoList = getDragAndDropCategory(); //UI:
////                        index = getDragAndDropCategory().getList().indexOf(dropTarget);
////                        ((Item)draggedObject).addCategoryToItem((Category)insertIntoList, false);
////                        ((Item)draggedObject).removeCategoryFromItem((Category)removeFromList, false); //no effect if (Category)removeFromList == null
////                        DAO.getInstance().save(((Item) draggedObject)); //save item w changed categories
////
////                } else if (draggedCont.getDragAndDropCategory() == null) { //D&D ... of an item2 (*not* from a category) onto category1  => just *add* item2 to category1 at position of item1//UI:
////                    removeFromList = null;
////                    insertIntoList = getDragAndDropCategory(); //insert items into the Category itself
////                    index = getDragAndDropCategory().getList().indexOf(dropTarget);
////                    ((Item)draggedObject).addCategoryToItem((Category)insertIntoList, false);
////                    DAO.getInstance().save(((Item) draggedObject)); //save item w changed categories
////                }
////                }
////                } else {
////                    return; //D&D something other than an item
////                }
////
////
////                        if (draggedObject instanceof Item && draggedCont.getDragAndDropCategory() != null) { //D&D item1 from category1 onto an item2 in category2 => insert item1 into category2 at item2's position and remove item1 from category1
////
////            } else if (draggedObject instanceof Item && draggedCont.getDragAndDropCategory() != null
////                        && getDragAndDropObject() instanceof Item && getDragAndDropCategory() != null && !insertBelow(x)) { //D&D item1 from category1 onto an item2 in category2
////                    removeFromList = draggedCont.getDragAndDropCategory();
////                    insertIntoList = getDragAndDropCategory(); //insert items into the Category itself //UI:
////                    index = getDragAndDropList().getList().indexOf(dropTarget);
////                    ((Item) draggedObject).getCategories().add(getDragAndDropCategory()); //add new category to item
////                    ((Item) draggedObject).getCategories().remove(draggedCont.getDragAndDropCategory()); //remove previous category from item
////                    DAO.getInstance().save(((Item) draggedObject)); //save item w changed categories
////                } else if (draggedObject instanceof Item && draggedCont.getDragAndDropCategory() != null && dropTarget instanceof Category) { //D&D item1 in category1 onto category2 //UI:
////                    removeFromList = draggedCont.getDragAndDropCategory();
////                    insertIntoList = getDragAndDropCategory(); //insert items into the Category itself
////                    index = 0;
////                } else if (draggedObject instanceof Item && draggedCont.getDragAndDropCategory() == null && dropTarget instanceof Category) { //D&D item1 (*not* from a category) onto category1  => just *add* item1 to category1 //UI:
////                    removeFromList = null;
////                    insertIntoList = getDragAndDropCategory(); //insert items into the Category itself
////                    index = 0;
////                } else if (draggedObject instanceof Item && draggedCont.getDragAndDropCategory() == null && getDragAndDropCategory() != null && !insertBelow(x)) { //D&D item1 (*not* from a category) onto item2 in category1 => just *add* item1 to category1 //UI: pretty tricky/unintuitive case
////                    removeFromList = null;
////                    insertIntoList = getDragAndDropCategory(); //insert items into the Category itself
////                    index = getDragAndDropList().getList().indexOf(dropTarget);
////                }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                super.drop(dragged,x, y);
////            } else if ((insertBelow(x) && dropTarget instanceof Item && draggedObject instanceof Item  )
////                    || (dropTarget instanceof ItemList &&  draggedObject instanceof Item )) { //insert item as as sub-task of dropTargetItem, or item at head of ItemList
////                } else if (insertBelow(x) && dropTarget instanceof ItemAndListCommonInterface && draggedObject instanceof Item) {
////</editor-fold>
//
//                    } else { //'normal' D&D of item2 onto item1: move item2 from previous list and insert into item1's list at item1's position
//                        insertIntoList = getDragAndDropList();
//                        if (insertIntoList != null) { //eg in a ParseQuery list there may be items without an owner
//                            index = insertIntoList.getItemIndex((Item) dropTarget);
//                            removeFromList = draggedCont.getDragAndDropList();
//                        }
//                    }
//                } // else do nothing if dragging eg an ItemList or a Category onto an Item. //DONE: consider if dragging a Category onto an item should add the category to it (NO, less intuitive than dragging the item onto the Category)
//
//            } else if (dropTarget instanceof ItemList) {
//
//                if (draggedObject instanceof Item) { //insert dragged item into head of ItemList
//                    Log.p("D&D 'else', dragged=" + draggedObject + ", dropTarget=" + dropTarget);
//
////                        if (draggedObject instanceof Item) { //insert draggeg item into list
//                    insertIntoList = ((ItemList) dropTarget);
//                    if (insertIntoList != null) {
//                        index = 0; //insert into head of itemList when dropped on the container of the list itself
//                        removeFromList = draggedCont.getDragAndDropList();
//                    }
//
//                } else if (draggedObject instanceof ItemList) { //itemList onto ItemList => insert
//                    insertIntoList = getDragAndDropList();
//                    if (insertIntoList != null) {
//                        index = insertIntoList.getItemIndex((ItemAndListCommonInterface) dropTarget);
//                        removeFromList = draggedCont.getDragAndDropList();
//                    }
//
//                } else {
//                    assert false;
//                }
//            }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (dropTarget instanceof Item && draggedObject instanceof Item && insertBelow(x)) { //special case for inserting a task as a subtask
////                getDragAndDropSubList().add(draggedObject); //insert items into the list of categories
////            } else {
////                insertIntoList.getList().add(index, draggedObject);
////            }
////</editor-fold>
//            if (insertIntoList == removeFromList && insertIntoList != null && insertIntoList.getItemIndex((ItemAndListCommonInterface) draggedObject) < index) {
//                //dropping into same list: cannot add before removing (duplicates now allowed)
////<editor-fold defaultstate="collapsed" desc="comment">
////                removeFromList.getList().remove(draggedObject);
////                    index = insertIntoList.getItemIndex((ItemAndListCommonInterface) dropTarget);
////                    if (insertIntoList.getItemIndex((ItemAndListCommonInterface) draggedObject) < index) {
////                    }
////                    removeFromList.removeFromList((ItemAndListCommonInterface) draggedObject); //remove last in case of dropping item on the list it's already in
//////                index = insertIntoList.getList().indexOf(dropTarget);
//////                insertIntoList.getList().add(index, draggedObject);
////                    insertIntoList.addToList(index, (ItemAndListCommonInterface) draggedObject);
////</editor-fold>
//                index--; //if dropping in a new position *below* (higher index) than previous position of draggedObject, then reduce the previously calculate index
//            }
//            //MUST remove from list before dropping since duplicates of same item not allowed in the same list
//            if (removeFromList != null) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    subtasks = removeFromList.getList();
////                    removeFromList.getList().remove(draggedObject);
////                    removeFromList.setList(subtasks);
////</editor-fold>
//                removeFromList.removeFromList((ItemAndListCommonInterface) draggedObject);
//            }
//            if (insertIntoList != null) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (index != -1) {
////                    insertIntoList.getList().add(index, draggedObject);
////                } else {
////                    insertIntoList.getList().add(draggedObject);
////                }
////                List subtasks = insertIntoList.getList();
////                subtasks.add(index, draggedObject);
////                insertIntoList.setList(subtasks);
////</editor-fold>
//                insertIntoList.addToList(index, (ItemAndListCommonInterface) draggedObject);
//            }
//            //remove AFTER insertion to avoid that index changes if adding/removing from the same list
//            //SAVE both
//            saveDragged();
//            draggedCont.saveDragged();
//
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
//
//            refreshAfterDrop();
//        }
//    }
//</editor-fold>
    /**
     * This method returns an image representing the dragged component, it can
     * be overriden by subclasses to customize the look of the image, the image
     * will be overlaid on top of the form during a drag and drop operation.
     * THJ: NB. image is cached during drag, so getDragImage cannot be used to
     * *dynamically* change the dragged image.
     *
     * @return an image
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Image getDragImage() {
//        if (true) {
//            return super.getDragImage();
//        }
////        Image draggedImage = Image.createImage(getWidth(), getHeight(), 0x00ff7777);
////        Graphics g = draggedImage.getGraphics();
////
////        g.translate(-getX(), -getY());
////        paintComponentBackground(g);
////        paint(g);
////        if (isBorderPainted()) {
////            paintBorder(g);
////        }
////        g.translate(getX(), getY());
////
////        // remove all occurences of the rare color
////        draggedImage = draggedImage.modifyAlpha((byte) 0x55, 0xff7777);
////        return draggedImage;
//        return null;
//    }
//</editor-fold>
    /**
     * Callback indicating that the drag has finished either via drop or by
     * releasing the component. THJ: called in last line of
     * Component.dragFinishedImpl(int x, int y) on the component that was
     * dragged (not on the drop target)
     *
     * @param x the x location
     * @param y the y location
     */
    protected void dragFinished(int x, int y) {
//        removePlaceholder(); //do *before* setting dropPlaceholder=null!
//        setHidden(wasHidden);
//        dropPlaceholder.getParent().removeComponent(dropPlaceholder);
//        setHidden(orgHiddenState); //restore hidden state
        setHidden(false); //restore hidden state
        if (dropPlaceholder != null && dropPlaceholder.getParent() != null) {
            dropPlaceholder.getParent().removeComponent(dropPlaceholder);
        }
//        refreshAfterDrop();
//        getComponentForm().animateLayout(300);
    }

//    boolean wasHidden = false;
    /**
     * Invoked on the focus component to let it know that drag has started on
     * the parent container for the case of a component that doesn't support
     * scrolling
     */
//    @Override
//    protected void dragInitiated() {
//        super.dragInitiated();
//        setDragPlaceholder();
////<editor-fold defaultstate="collapsed" desc="comment">
////        wasHidden = isHidden();
////        setHidden(true); //completely hide the container for the dragged element (default is to set it invisible in Component.pointerDragged(final int x, final int y)
////        if (true||dropPlaceholder == null) {
////            dropPlaceholder = new Component() {
////                @Override
////                public int getWidth() {
////                    return MyDragAndDropSwipeableContainer.this.getWidth(); //placeholder takes same size as dragged element
////                }
////
////                @Override
////                public int getHeight() {
////                    return MyDragAndDropSwipeableContainer.this.getHeight();
////                }
////
////                public void drop(Component dragged, int x, int y) {
////                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, call drop on original droptarget
////                }
////            };
////            dropPlaceholder.setUIID("DropTargetPlaceholder");
////            //TODO!!! change to look like a subtask when moving to the right
////</editor-fold>
//    }
//    private boolean orgHiddenState = false;
    /**
     * Invoked on the focus component to let it know that drag has started on
     * the parent container for the case of a component that doesn't support
     * scrolling
     */
    protected void dragInitiatedXXX() { //only hide in dragEnter to avoid hiding invisible container
//        orgHiddenState = isHidden();
//        setHidden(true); //hide the original container (CN1 just makes it invisible to leave a blank space in original position)
    }

    void drawDraggedImageXXX(Graphics g) {
//        if (dragImage == null) {
//            dragImage = getDragImage();
//        }
//        drawDraggedImage(g, dragImage, draggedx, draggedy);
    }
    /**
     * Draws the given image at x/y, this method can be overriden to draw
     * additional information such as positive or negative drop indication
     *
     * @param g the graphics context
     * @param img the image
     * @param x x position
     * @param y y position
     */
//    protected void drawDraggedImage(Graphics g, Image img, int x, int y) {
////        g.drawImage(img, x - getWidth() / 2, y - getHeight() / 2);
//        g.drawImage(img, x, y);
//    }
    private Component dropPlaceholder = null;
//    int dropPlaceholderIndex = -1;
//    int oldDropPlaceholderIndex = -1;
//    Container dropPlaceholderCont;
//    Component dropOriginalDropTarget;
    private int draggedWidth;
    private int draggedHeigth;

    private Component makeDragPlaceholder() {
        Component dropPlaceholder = new Component() {
            @Override
            public int getWidth() {
                return MyDragAndDropSwipeableContainer.this.getWidth(); //placeholder takes same size as dragged element
            }

            @Override
            public int getHeight() {
                return MyDragAndDropSwipeableContainer.this.getHeight();
            }

            @Override
            public void drop(Component dragged, int x, int y) {
                MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, call drop on original droptarget
            }
        };
        dropPlaceholder.setUIID("DropTargetPlaceholder");
        //TODO!!! change to look like a subtask when moving to the right
//            wasHidden = isHidden();
//            setHidden(true); //completely hide the container for the dragged element (default is to set it invisible in Component.pointerDragged(final int x, final int y)
        return dropPlaceholder;
    }

    private void removeOldPlaceholder(MyDragAndDropSwipeableContainer dropPlaceholder) {
//        if (oldDropPlaceholderIndex != -1 && dropPlaceholderCont != null) {
//            dropPlaceholderCont.removeComponent(dropPlaceholder);
//        }
//        oldDropPlaceholderIndex = -1;
//        dropPlaceholderCont = null;
        if (dropPlaceholder != null && dropPlaceholder.getParent() != null) {
            dropPlaceholder.getParent().removeComponent(dropPlaceholder);
            ASSERT.that("removing placeholder from parent");
        }
        dropPlaceholder = null;
    }

    /**
     * This callback method indicates that a component drag has just entered
     * this component.
     * 
     * THJ:called on dropTarget in pointerDragged(x,y) and in dragFinishedImpl(x,y) (itself called from pointerReleased(int x, int y))
     *
     * @param dragged the component being dragged
     */
    @Override
    protected void dragEnter(Component dragged) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (dropPlaceholder == null) {
//            dropPlaceholder = new Component() {
//                @Override
//                public int getWidth() {
//                    return dragged.getWidth();
//                }
//
//                @Override
//                public int getHeight() {
//                    return dragged.getHeight();
//                }
//
//                public void drop(Component dragged, int x, int y) {
//                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, call drop on original droptarget
//                }
//            };
//            dropPlaceholder.setUIID("DropTargetPlaceholder");
//            //TODO!!! change to look like a subtask when moving to the right
//        }
//</editor-fold>
        if (dragged instanceof MyDragAndDropSwipeableContainer) {
            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
            if (draggedCont == this && !draggedCont.isHidden()) {
                draggedWidth = dragged.getWidth();
                draggedHeigth = dragged.getHeight();
//                draggedCont.orgHiddenState = draggedCont.isHidden();
                dragged.setHidden(true);
            }
//            draggedCont.setDragPlaceholder();
            Container element = null; //stores the element for which to find index in parent
            Container parent = this; //.getParent();
            //get the Container holding the list of elements
            //The dragged container is wrapped into a Container at NORTH SOUTH being used to show expanded children
//            while (!(parent instanceof MyTree2) && parent.getParent() != null) {
//                element = parent;
//                parent = parent.getParent();
//            }
            element = dragged.getParent();
            parent = element.getParent();

            //remove old placeholder - do in dragExit()
            if (false && draggedCont.dropPlaceholder != null
                    && draggedCont.dropPlaceholder.getParent() != null
                    && draggedCont.dropPlaceholder.getParent() != parent) {
                draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
                Log.p("removing placeholder from parent");
            }

            int dropPlaceholderIndex;
            //remove old placeholder (even if no new place found) //TODO!!! How to remove when eg dropping back on original position?
            if (parent != null && element != null) {
                dropPlaceholderIndex = parent.getComponentIndex(element);
                if (dropPlaceholderIndex != -1) {
                    //set new placholder 
//                    draggedCont.dropPlaceholder=makeDragPlaceholder();
                    draggedCont.dropPlaceholder = new Component() {
                        @Override
                        public int getWidth() {
//                            return MyDragAndDropSwipeableContainer.this.getWidth(); //placeholder takes same size as dragged element
//                            return MyDragAndDropSwipeableContainer.this.getDragImage().getWidth(); //placeholder takes same size as dragged element
//                            return dragged.getWidth(); //placeholder takes same size as dragged element
                            return draggedWidth; //placeholder takes same size as dragged element
                        }

                        @Override
                        public int getHeight() {
//                            return MyDragAndDropSwipeableContainer.this.getDragImage().getHeight();
//                            return dragged.getHeight();
                            return draggedHeigth;
                        }

                        @Override
                        public void drop(Component dragged, int x, int y) {
                            MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, call drop on original droptarget
                        }

                        @Override
                        protected void dragEnter(Component dragged) {
//                            if (this == draggedCont.dropPlaceholder) {
//                                return;
//                            } else {
                                MyDragAndDropSwipeableContainer.this.dragEnter(dragged);
//                            }
                        }

                    };
                    draggedCont.dropPlaceholder.setUIID("DropTargetPlaceholder");
                    //TODO!!! change to look like a subtask when moving to the right

                    parent.addComponent(dropPlaceholderIndex, draggedCont.dropPlaceholder);
//                        parent.revalidate();
                    parent.animateLayout(150);
                    Log.p("parent=" + parent + ", " + parent.getComponentCount() + ", index=" + parent.getComponentIndex(this));
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void dragEnterXXX(Component dragged) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (dropPlaceholder == null) {
////            dropPlaceholder = new Component() {
////                @Override
////                public int getWidth() {
////                    return dragged.getWidth();
////                }
////
////                @Override
////                public int getHeight() {
////                    return dragged.getHeight();
////                }
////
////                public void drop(Component dragged, int x, int y) {
////                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, call drop on original droptarget
////                }
////            };
////            dropPlaceholder.setUIID("DropTargetPlaceholder");
////            //TODO!!! change to look like a subtask when moving to the right
////        }
////</editor-fold>
//        if (dragged instanceof MyDragAndDropSwipeableContainer) {
//            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
//            Object dropTarget = getDragAndDropObject();
//            Object draggedObject = draggedCont.getDragAndDropObject();
//            if (dropTarget instanceof Item) {
//                Item item = (Item) dropTarget;
////                  int index = getDragAndDropList().getItemIndex(dropTarget); //get index where to drop
//
////            Log.p("dragEnter, comp=" + this + ", owner=" + item.getOwnerFormatted() + ", ");
//                Log.p("dragEnter:" + dropTarget + ", owner=" + item.getOwnerFormatted()
//                        + ", index=" + item.getOwner() != null ? item.getOwner().getItemIndex(item) + "" : "[owner==null]");
//            }
//
//            Container element = null; //stores the element for which to find index in parent
//            Container parent = this; //.getParent();
//            //get the MyTree holding the list of elements
//            while (!(parent instanceof MyTree2) && parent.getParent() != null) {
//                element = parent;
//                parent = parent.getParent();
//            }
//            int dropPlaceholderIndex;
//            //remove old placeholder (even if no new place found) //TODO!!! How to remove when eg dropping back on original position?
//            if (parent != null && element != null) {
//                dropPlaceholderIndex = parent.getComponentIndex(element);
////                if ((dropPlaceholderIndex != oldDropPlaceholderIndex && oldDropPlaceholderIndex != -1)
////                        || (dropPlaceholderCont != null && dropPlaceholderCont != parent)) {
//                //if position or list has changed, remove dropPlaceholder from old position and insert in new position
//                if (dropPlaceholderIndex != oldDropPlaceholderIndex || dropPlaceholderCont != parent) {
//                    if (dropPlaceholderCont != null && oldDropPlaceholderIndex != -1) {
//                        dropPlaceholderCont.removeComponent(draggedCont.dropPlaceholder);
//                    }
////                    dropPlaceholderIndex = parent.getComponentIndex(element);
//                    if (dropPlaceholderIndex != -1) {
//                        parent.addComponent(dropPlaceholderIndex, draggedCont.dropPlaceholder);
//                        dropPlaceholderCont = parent;
//                        oldDropPlaceholderIndex = dropPlaceholderIndex;
////                        parent.revalidate();
//                        parent.animateLayout(150);
//                        Log.p("parent=" + parent + ", " + parent.getComponentCount() + ", index=" + parent.getComponentIndex(this));
//                    }
//                }
//            }
//        }
//    }
//</editor-fold>
    /**
     * This callback method provides an indication for a drop target that a drag
     * operation is exiting the bounds of this component and it should clear all
     * relevant state if such state exists. E.g. if a component provides drop
     * indication visualization in draggingOver this visualization should be
     * cleared..
     *
     * @param dragged the component being dragged
     */
    protected void dragExit(Component dragged) {
//        if (dropPlaceholder!=null)
        if (dragged instanceof MyDragAndDropSwipeableContainer) {
            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
            if (draggedCont.dropPlaceholder != null && draggedCont.dropPlaceholder.getParent() != null) {
                draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
            }
        }
        Log.p("dragExit");
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void dropPrev2(Component dragged, int x, int y) {
//        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
//            return;
//        }
//        if (dragged instanceof MyDragAndDropSwipeableContainer) {
//            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
//            Object dropTarget = getDragAndDropObject();
//            Object draggedObject = draggedCont.getDragAndDropObject();
//            ItemAndListCommonInterface insertIntoList = null;
//            ItemAndListCommonInterface removeFromList = null;
//            int index = -1;
//            //D&D for categories is a special case
//            //CATEGORY
//            if (dropTarget instanceof Category) {
////                    || (dropTarget instanceof Item && getDragAndDropCategory() != null)
////                    || (draggedObject instanceof Category && ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropCategory() != null)) {
//                if (draggedObject instanceof Category) {
//                    removeFromList = draggedCont.getDragAndDropList();
//                    insertIntoList = getDragAndDropList(); //insert items into the list of categories
//                    assert removeFromList == insertIntoList : "should be same list (only one CategoryList)";
//                    index = insertIntoList.getList().indexOf(dropTarget);
//                    removeFromList.getList().remove(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//                    insertIntoList.getList().add(index, ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//                    DAO.getInstance().save((CategoryList) insertIntoList); //just save the CategoryList once
//                    return;
//                } else if (draggedObject instanceof Item) {
//                    if (draggedCont.getDragAndDropCategory() != null) { //item moved from one category to another, so remove it from original Category
//                        removeFromList = draggedCont.getDragAndDropCategory(); //if D&D from one category to another, we only remove the item from the original category
//                        insertIntoList = getDragAndDropSubList(); //insert items into the Category itself
//                        index = 0; //insert in head of list
//                    } else { //item moved to Category, but NOT from other category (e.g. an expanded sub-tasks is dropped on a category): only add the task to the category, but don't remove it from where it is now
//                        removeFromList = null;
//                        insertIntoList = getDragAndDropSubList(); //insert items into the Category itself
//                        index = 0; //insert in head of list
//                    }
////                    } else if (getDragAndDropCategory() != null)) { //item moved to Category, but NOT from other category (e.g. an expanded sub-tasks is dropped on a category): only add the task to the category, but don't remove it
//                }
//            } else if (dropTarget instanceof ItemList) {
//                if (draggedObject instanceof ItemList) {
//                    removeFromList = draggedCont.getDragAndDropList();
//                    insertIntoList = getDragAndDropList(); //insert itemList into the list of ItemLists
//                    index = getDragAndDropList().indexOf(getDragAndDropObject());
//                } else if (draggedObject instanceof Item) {
//                    removeFromList = draggedCont.getDragAndDropList();
//                    insertIntoList = getDragAndDropSubList(); //insert items into the ItemList itself
//                    index = 0; //insert in head of list
//                }
//            } else if (dropTarget instanceof Item) {
//                if (draggedObject instanceof ItemList || draggedObject instanceof Category) {
////                            refreshAfterDrop(); //TODO need to refresh for a drop which doesn't change anything??
//                    return; //UI: dropping an ItemList onto an Item not allowed
//                } else //CATEGORY dragging an item1 from a category1 and dropping on the item2 of another category2 => move to new category2 at the position of item2
//                {
//                    if (draggedCont.getDragAndDropCategory() != null && getDragAndDropCategory() != null && x < this.getWidth() / 3 * 2) {
//                        removeFromList = draggedCont.getDragAndDropCategory();
//                        insertIntoList = getDragAndDropCategory(); //insert items into the ItemList itself
//                        index = getDragAndDropList().indexOf(dropTarget);
//                    } else {
//                        assert (draggedObject instanceof Item) : "draggedObject not Item as expected, was: " + draggedObject;
//                        if (x < this.getWidth() / 3 * 2) {
//                            removeFromList = draggedCont.getDragAndDropList();
//                            insertIntoList = getDragAndDropList(); //insert item as subtask of dropTarget Item
//                            index = getDragAndDropList().indexOf(getDragAndDropObject());
//                        } else {
//                            removeFromList = draggedCont.getDragAndDropList();
//                            insertIntoList = getDragAndDropSubList(); //insert item at the position of the dropTarget Item
//                            index = 0; //insert as first sub task
//                        }
//                    }
//                }
//            }
////                    assert insertList
//            if (removeFromList != null) {
//                removeFromList.remove(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//            }
////            else {
////                ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList().remove(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
////            }
////            DAO.getInstance().save(()((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList());
////                        if (x > this.getWidth() / 3 * 2) {
//////                        getDragAndDropSubList().add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                            insertList.add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        } else {
////                            getDragAndDropList().add(getDragAndDropList().indexOf(getDraggedObject()), ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        }
//            insertIntoList.add(index, ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//            //SAVE both
//            saveDragged();
//            ((MyDragAndDropSwipeableContainer) dragged).saveDragged();
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//            refreshAfterDrop();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//
//    public void dropPrev(Component dragged, int x, int y) {
//        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
//            return;
//        }
//        if (dragged instanceof MyDragAndDropSwipeableContainer) {
//            ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList().remove(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
////            DAO.getInstance().save(()((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList());
//            if (x > this.getWidth() / 3 * 2) {
//                getDragAndDropSubList().add(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//            } else {
//                getDragAndDropList().add(getDragAndDropList().indexOf(getDragAndDropObject()), ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//            }
//            saveDragged();
//            ((MyDragAndDropSwipeableContainer) dragged).saveDragged();
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//            refreshAfterDrop();
//        }
//    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
//
//    public void dropPrevious(Component dragged, int x, int y) {
//        if (dragged != this) { //do nothing if dropped on itself
//            Object draggedElement = dragged.getClientProperty(MyTree2.KEY_OBJECT);
//            //don't do anything if there is not both a ownerList and a drop list where to insert the item
//            if (draggedElement != null && draggedElement instanceof Movable) {
//                Object destination = this.getClientProperty(MyTree2.KEY_OBJECT);
////                Object target = this.getClientProperty(MyTree2.KEY_LIST);
////                if (target != null) {
////                    if (target instanceof Item) {
//////                        ((Item) target).insertBelow((Movable) draggedElement);
////                        if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
////                            ((Item) target).insertBelow((Movable) draggedElement);
////                        } else {
////                            ((Item) target).insertIntoOwnerAtPositionOf((Movable) draggedElement);
////                        }
////                    } else if (target instanceof ItemList) {
//////                        ((ItemList) target).insertBelow((Movable) draggedElement);
////                        if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
////                            ((ItemList) target).insertBelow((Movable) draggedElement);
////                        } else {
//////                        ((ItemList) destination).insertIntoOwnerAtPositionOf((Movable) draggedElement);
//////                            list.addItemAtIndex((ItemAndListCommonInterface) element, list.getItemIndex(elementAtInsertPosition));
////                            ((ItemList) target).addItemAtIndex((ItemAndListCommonInterface) draggedElement, ((ItemList) target).getItemIndex(destination));
////                        }
////                    }
////                } else
//                if (destination != null && destination instanceof Movable) {
//                    ((Movable) draggedElement).removeFromOwner();
////                    int width = this.getWidth(); //TODO for debugging, remove
////                    int xval = getX(); //TODO for debugging, remove
////                    int xabs = getAbsoluteX(); //TODO for debugging, remove
////                    int xdrag = getDraggedx(); //TODO for debugging, remove
////                    int xgrid = getGridPosX(); //TODO for debugging, remove
//                    if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
//                        ((Movable) destination).insertBelow((Movable) draggedElement);
//                    } else {
//                        ((Movable) destination).insertIntoOwnerAtPositionOf((Movable) draggedElement);
//                    }
//                }
//            }
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to refresh the entire list)
//            boolean moveContainer = false; //should the existing container be moved - not meaningful if entire tree is redrawn
//            if (moveContainer) {
//                Container oldParent = dragged.getParent(); //BorderLayout.NORTH container
//                if (oldParent != null) {
//                    oldParent.removeComponent(dragged);
//                } else {
//                    assert false; //shouldn't happen
//                }
////            Component pos = getComponentAt(x, y);
////            int i = getComponentIndex(pos);
////            if (i > -1) {
////                addComponent(i, dragged);
////            } else {
////                addComponent(dragged);
////            }
//                Container newParent = getParent(); //BorderLayout.NORTH container
//                int idx = newParent.getComponentIndex(this);
//                newParent.addComponent(idx, dragged);
//                getComponentForm().animateHierarchy(400);
//            } else {
//                ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//            }
//        } //else do nothing if dropped onto itself
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void drop2(Component dragged, int x, int y) {
//        if (dragged != this) { //do nothing if dropped on itself
//
//            List list = dragged.getOrgList();
//            list.removeObject(dragged.getObject);
//            this.getOrgList().insertObjectAtPos(x > this.getWidth() / 3 * 2, this.getOrgList().indexOf(), dragged.getObject());
//
//            dragged.removeFromOrgList();
//            this.insertIntoNewList();
//            Object draggedElement = dragged.getClientProperty(MyTree2.KEY_OBJECT);
//            //don't do anything if there is not both a ownerList and a drop list where to insert the item
//            if (draggedElement != null && draggedElement instanceof Movable) {
//                Object destination = this.getClientProperty(MyTree2.KEY_OBJECT);
////                Object target = this.getClientProperty(MyTree2.KEY_LIST);
////                if (target != null) {
////                    if (target instanceof Item) {
//////                        ((Item) target).insertBelow((Movable) draggedElement);
////                        if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
////                            ((Item) target).insertBelow((Movable) draggedElement);
////                        } else {
////                            ((Item) target).insertIntoOwnerAtPositionOf((Movable) draggedElement);
////                        }
////                    } else if (target instanceof ItemList) {
//////                        ((ItemList) target).insertBelow((Movable) draggedElement);
////                        if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
////                            ((ItemList) target).insertBelow((Movable) draggedElement);
////                        } else {
//////                        ((ItemList) destination).insertIntoOwnerAtPositionOf((Movable) draggedElement);
//////                            list.addItemAtIndex((ItemAndListCommonInterface) element, list.getItemIndex(elementAtInsertPosition));
////                            ((ItemList) target).addItemAtIndex((ItemAndListCommonInterface) draggedElement, ((ItemList) target).getItemIndex(destination));
////                        }
////                    }
////                } else
//                if (destination != null && destination instanceof Movable) {
//                    ((Movable) draggedElement).removeFromOwner();
////                    int width = this.getWidth(); //TODO for debugging, remove
////                    int xval = getX(); //TODO for debugging, remove
////                    int xabs = getAbsoluteX(); //TODO for debugging, remove
////                    int xdrag = getDraggedx(); //TODO for debugging, remove
////                    int xgrid = getGridPosX(); //TODO for debugging, remove
//                    if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
//                        ((Movable) destination).insertBelow((Movable) draggedElement);
//                    } else {
//                        ((Movable) destination).insertIntoOwnerAtPositionOf((Movable) draggedElement);
//                    }
//                }
//            }
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to refresh the entire list)
//            boolean moveContainer = false; //should the existing container be moved - not meaningful if entire tree is redrawn
//            if (moveContainer) {
//                Container oldParent = dragged.getParent(); //BorderLayout.NORTH container
//                if (oldParent != null) {
//                    oldParent.removeComponent(dragged);
//                } else {
//                    assert false; //shouldn't happen
//                }
////            Component pos = getComponentAt(x, y);
////            int i = getComponentIndex(pos);
////            if (i > -1) {
////                addComponent(i, dragged);
////            } else {
////                addComponent(dragged);
////            }
//                Container newParent = getParent(); //BorderLayout.NORTH container
//                int idx = newParent.getComponentIndex(this);
//                newParent.addComponent(idx, dragged);
//                getComponentForm().animateHierarchy(400);
//            } else {
//                ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//            }
//        } //else do nothing if dropped onto itself
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void dropOld2(Component dragged, int x, int y) {
//        if (dragged != this) { //do nothing if dropped on itself
//            Object draggedElement = dragged.getClientProperty(MyTree2.KEY_OBJECT);
//            //don't do anything if there is not both a ownerList and a drop list where to insert the item
//            if (draggedElement != null && draggedElement instanceof Movable) {
//                Object destination = this.getClientProperty(MyTree2.KEY_OBJECT);
//                if (destination != null && destination instanceof Movable) {
//                    ((Movable) draggedElement).removeFromOwner();
//                    int width = this.getWidth(); //TODO for debugging, remove
//                    int xval = getX(); //TODO for debugging, remove
//                    int xabs = getAbsoluteX(); //TODO for debugging, remove
//                    int xdrag = getDraggedx(); //TODO for debugging, remove
//                    int xgrid = getGridPosX(); //TODO for debugging, remove
//                    if (x > this.getWidth() / 3 * 2) { //If dropped on right third of target, then insert as subtask
//                        ((Movable) destination).insertBelow((Movable) draggedElement);
//                    } else {
//                        ((Movable) destination).insertIntoOwnerAtPositionOf((Movable) draggedElement);
//                    }
//                }
//            }
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to refresh the entire list)
//            boolean moveContainer = false; //should the existing container be moved - not meaningful if entire tree is redrawn
//            if (moveContainer) {
//                Container oldParent = dragged.getParent(); //BorderLayout.NORTH container
//                if (oldParent != null) {
//                    oldParent.removeComponent(dragged);
//                } else {
//                    assert false; //shouldn't happen
//                }
////            Component pos = getComponentAt(x, y);
////            int i = getComponentIndex(pos);
////            if (i > -1) {
////                addComponent(i, dragged);
////            } else {
////                addComponent(dragged);
////            }
//                Container newParent = getParent(); //BorderLayout.NORTH container
//                int idx = newParent.getComponentIndex(this);
//                newParent.addComponent(idx, dragged);
//                getComponentForm().animateHierarchy(400);
//            }
//        } //else do nothing if dropped onto itself
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void dropOrg(Component dragged, int x, int y) {
//        int i = getComponentIndex(dragged);
//        if (i > -1) {
////            Component dest = getComponentAt(x, y);
//            Component dest = findDropTargetAt(x, y); //THJ
//            if (dest != dragged) {
//                int destIndex = getComponentIndex(dest);
//                if (destIndex > -1 && destIndex != i) {
//                    removeComponent(dragged);
//                    Object con = getLayout().getComponentConstraint(dragged);
//                    if (con != null) {
//                        addComponent(destIndex, con, dragged);
//                    } else {
//                        addComponent(destIndex, dragged);
//                    }
//                }
//            }
//            animateLayout(400);
//        } else {
//            Container oldParent = dragged.getParent();
//            if (oldParent != null) {
//                oldParent.removeComponent(dragged);
//            }
//            Component pos = getComponentAt(x, y);
//            i = getComponentIndex(pos);
//            if (i > -1) {
//                addComponent(i, dragged);
//            } else {
//                addComponent(dragged);
//            }
//            getComponentForm().animateHierarchy(400);
//        }
//    }
//</editor-fold>
}
