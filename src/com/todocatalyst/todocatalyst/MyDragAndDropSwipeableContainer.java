/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.AnimationManager;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
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

    int draggedWidth = -1;
    int draggedHeight = -1;
//    boolean dropSucceeded = false; //keeps track of whether drop succeeded (to add the dragged container if not)
//    int draggedOrgIndex = -1; //index of dragged container to insert it at right position if drop fails
//    Container draggedOrgParent = null; //store the original parent of the dragged component to reinsert it the right place if the drop fails
    private Image dragImage2 = null;
    private Component dropPlaceholder = null;
//    private int dropPlaceholderOldIndex = -1;
    private Component lastDraggedOver = null;

    interface Call {

        void call();
    }
    private Call dropActionCall; //function containing the drop action to execute on a normal drop
    private Call dropAsSubtaskActionCall; //function containing the drop action to execute when dropping as a subtask

    /**
     * move between ItemLists or Items (projects)
     *
     * @param oldList
     * @param newList
     * @param item
     * @param newPos
     */
    private void moveItemAndSave(ItemAndListCommonInterface oldList, ItemAndListCommonInterface newList, ItemAndListCommonInterface item, int newPos) {
        if (oldList == newList) {
            if (oldList.getItemIndex(item) <= newPos) {
                oldList.removeFromList(item);
                newList.addToList(newPos - 1, item);
            } else {
                oldList.removeFromList(item);
                newList.addToList(newPos, item);
            }
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) item);
        } else {
            oldList.removeFromList(item);
            newList.addToList(newPos, item);
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) newList, (ParseObject) item);
        }
    }

    /**
     * move the position of Category within CategoryList
     *
     * @param categoryList
     * @param newList
     * @param category
     * @param newPos
     */
    private void moveCategoryAndSave(ItemAndListCommonInterface categoryList, Category category, int newPos) {
        if (categoryList.getItemIndex(category) <= newPos) {
            categoryList.removeFromList(category);
            categoryList.addToList(newPos - 1, category);
        } else {
            categoryList.removeFromList(category);
            categoryList.addToList(newPos, category);
        }
        DAO.getInstance().saveInBackgroundSequential((ParseObject) categoryList, (ParseObject) category);
    }

    private void moveItemBetweenCategoriesAndSave(Category oldList, Category newList, Item item, int newPos) {
        if (oldList == newList) {
            if (oldList.getItemIndex(item) <= newPos) {
                oldList.removeItemFromCategory(item, false); //false: keep the position of the category in the item's list of categories
                newList.addItemToCategory(item, newPos - 1, false); //false: keep the position of the category in the item's list of categories
            } else {
                oldList.removeItemFromCategory(item, true);
                newList.addItemToCategory(item, newPos, true);
            }
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) item); //only save list once
        } else {
            oldList.removeItemFromCategory(item, true);
            newList.addItemToCategory(item, newPos, true);
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) newList, (ParseObject) item);
        }
    }

    MyDragAndDropSwipeableContainer(Component bottomLeft, Component bottomRight, Component top) {
        super(bottomLeft, bottomRight, top);
        setDropTarget(true); //containers are both dropTargets and draggable
        setDraggable(false); //set false by default to allow scrolling. LongPress will activate, drop will deactivate it
        setUIID("MyDragAndDropSwipeableContainer");
        //NB!!! dragOverListener is called on the **DRAGGED** object (not the dropTarget as I originally assumed)
        addDragOverListener((e) -> {
//            Component drag = e.getDraggedComponent();
            Component drag = (Component) e.getSource();
            Component dropTarget = e.getDropTarget();
//            Component dropTarget = this;

//            Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------");
//            MyDragAndDropSwipeableContainer dragged = null;
            if (!(drag instanceof MyDragAndDropSwipeableContainer)) {
//                Log.p("***addDragOverListener: drag (" + drag.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
                return;
            } //else {
            if (!(dropTarget instanceof MyDragAndDropSwipeableContainer)) {
//                Log.p("***addDragOverListener: dropTarget (" + dropTarget.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
                return;
            } //else {
            if (false) {
                e.consume();
            }
            MyDragAndDropSwipeableContainer dragged = (MyDragAndDropSwipeableContainer) drag;
//            if (dragged == dropTarget || dragged.lastDraggedOver == dropTarget) { //over myself or same as on last dropActionCall to listener
            if (dragged.lastDraggedOver == dropTarget) { //over myself or same as on last dropActionCall to listener
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dragged.lastDraggedOver == MyDragAndDropSwipeableContainer.this) {
//                    e.consume();
//                Log.p("addDragOverListener: IGNORE DragOverListener, dropTarget == dragged.lastDraggedOver, dropTarget=" + dropTarget.getName() + ", dragged=" + dragged.getName());
////                    getComponentForm().revalidate();
//                Log.p("---------------- END MyDragAndDropSwipeableContainer() ------------------------------------");
//</editor-fold>
                return; //skip if still over the same 
            } else {
                //over a new container
                Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------");
                int oldDropPlaceholderIndex = -1; //used to keep track of whether we drag upwards or downwards (to insert new dropPlaceholder in right position)
                //need to set treeList *before* removing dragged from its parent:
                Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
                Component dropParent = dropTarget;
                //first time we drag over another element than dragged:
                if (!dragged.isHidden()) { //dragged.draggedWidth == -1) {// && dragged!=dragged.lastDraggedOver) {
//                    oldDropPlaceholderIndex=dragged.getParent().getComponentIndex(dragged); //initialize oldIndex to position of dragged
                    dragged.draggedWidth = dragged.getWidth();
                    dragged.draggedHeight = dragged.getHeight();
                    dragImage2 = getDragImage(); //save dragImage before hiding
                    Log.p("addDragOverListener: dragged over NEW dropTarget (" + dropTarget.getName() + "), STORING dragged.getWidth/getHeigth, w=" + dragged.draggedWidth + ", h=" + dragged.draggedHeight + ", HIDING dragged=" + dragged.getName());
                    dragged.setHidden(true); //only hide once we've dragged on top of another object than dragged. Once we have the Height/Width, hide the component
//                    dragged.draggedOrgParent = drag.getParent();
//                    dragged.draggedOrgIndex = dragged.draggedOrgParent.getComponentIndex(drag);
//                    dragged.draggedOrgParent.removeComponent(drag); //remove the dragged component
                }

                if (true || dragged.lastDraggedOver != dropTarget) { // implicitly: || dragged.lastDraggedOver==null 
                    Log.p("addDragOverListener: initialize dragged.lastDraggedOver to dropTarget(" + dropTarget.getName() + "), dragged=" + dragged.getName());
                    dragged.lastDraggedOver = dropTarget; //store every time we're above a new object
//                return;
                }
//<editor-fold defaultstate="collapsed" desc="comment">
// dragged.lastDraggedOver != dropTarget <=>
//            Log.p("addDragOverListener: dragged.lastDraggedOver (" + (dragged.lastDraggedOver != null ? dragged.lastDraggedOver.getName() : "null")
//                    + ") *!=* dropTarget (" + dropTarget.getName() + ") so setting dragged.lastDraggedOver=dropTarget, dragged=" + dragged.getName());
//                    dragged.lastDraggedOver = MyDragAndDropSwipeableContainer.this; //store first time we're over this
//                    if (dragged.lastDraggedOver == null) {
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dragged.draggedWidth == -1) {
//                    dragged.draggedWidth = dragged.getWidth();
//                    dragged.draggedHeight = dragged.getHeight();
//                    Log.p("addDragOverListener: dragged dragged.getWidth/getHeigth, w=" + dragged.draggedWidth + ", h=" + dragged.draggedHeight + ", for dragged=" + dragged.getName());
//                }
//                if (dragged.lastDraggedOver != null && dragged.lastDraggedOver != dragged && !dragged.isHidden()) {
//                    dragged.setHidden(true); //only hide once we've dragged on top of another object than dragged. Once we have the Height/Width, hide the component
//                    Log.p("addDragOverListener: dragged=" + dragged.getName() + " now HIDDEN");
//                }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                Log.p("DragOverListener, dragged=" + dragged.getName() + ", over=" + MyDragAndDropSwipeableContainer.this.getName() + ", dropTarget=" + e.getDropTarget().getName());
//                if (dragged == this) {
//                    dragged.draggedWidth = dragged.getWidth();
//                    dragged.draggedHeight = dragged.getHeight();
//                    setHidden(true); //once we have the Height/Width, hide the component
//                }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                //Create new dropPlaceholder
//                Label newDropPlaceholder = new Label() {
//                    int draggedWidth = dragged.draggedWidth;
//                    int draggedHeight = dragged.draggedHeight;
////                    Dimension prefSize = dragged.getPreferredSize();
//                    Component dropTarget1 = dropTarget;
//
////                    @Override
//                    public int getWidth() {
//                        if (draggedWidth <= 0) {
//                            Log.p("Comp.getWidth()==" + draggedWidth + "!!!");
//                        }
//                        return draggedWidth;
//                    }
//
////                    @Override
//                    public int getHeight() {
//                        if (draggedHeight <= 0) {
//                            Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                        }
//                        return draggedHeight;
//                    }
//
//                    @Override
//                    public Dimension getPreferredSize() {
//                        if (draggedHeight <= 0) {
//                            Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                        }
//                        return new Dimension(draggedWidth, draggedHeight);
////                        return prefSize;
//                    }
//
//                    @Override
//                    public void drop(Component drag1, int x, int y) {
//                        Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName());
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                        MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        if (false) {
////                            MyDragAndDropSwipeableContainer.this.drop(drag1, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        }
////                        dropTarget.drop(dragged, x, y);
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
////                        dropTarget1.drop(drag1, x, y);
//                        if (getDragAndDropObject() instanceof Category) {
//                            MyDragAndDropSwipeableContainer beforeCont;
//                            MyDragAndDropSwipeableContainer afterCont;
//                            dropCategory(before, after, (Category) getDragAndDropObject());
//                        }
//                    }
//
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                    protected Image getDragImageXXX() {
////                        if (!isHidden()) {
////                            ASSERT.that(!isHidden(), "***Calling getDragImage() on " + getName() + " while it's hidden"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        } else {
////                            Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        }
////                        if (dragImage == null) {
////                            dragImage = super.getDragImage();
////                        }
////                        return dragImage;
////                    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                    @Override
////                    protected int getDragRegionStatus(int x, int y) {
////                        return DRAG_REGION_LIKELY_DRAG_XY;
////                    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                };
//                Label newDropPlaceholderOLD = new Label() {
//                    int draggedWidth = dragged.draggedWidth;
//                    int draggedHeight = dragged.draggedHeight;
////                    Dimension prefSize = dragged.getPreferredSize();
//                    Component dropTarget1 = dropTarget;
//
////                    @Override
//                    public int getWidth() {
//                        if (draggedWidth <= 0) {
//                            Log.p("Comp.getWidth()==" + draggedWidth + "!!!");
//                        }
//                        return draggedWidth;
//                    }
//
////                    @Override
//                    public int getHeight() {
//                        if (draggedHeight <= 0) {
//                            Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                        }
//                        return draggedHeight;
//                    }
//
//                    @Override
//                    public Dimension getPreferredSize() {
//                        if (draggedHeight <= 0) {
//                            Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                        }
//                        return new Dimension(draggedWidth, draggedHeight);
////                        return prefSize;
//                    }
//
//                    @Override
//                    public void drop(Component drag1, int x, int y) {
//                        Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName());
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                        MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        if (false) {
////                            MyDragAndDropSwipeableContainer.this.drop(drag1, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        }
////                        dropTarget.drop(dragged, x, y);
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                    protected Image getDragImageXXX() {
////                        if (!isHidden()) {
////                            ASSERT.that(!isHidden(), "***Calling getDragImage() on " + getName() + " while it's hidden"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        } else {
////                            Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        }
////                        if (dragImage == null) {
////                            dragImage = super.getDragImage();
////                        }
////                        return dragImage;
////                    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                    @Override
////                    protected int getDragRegionStatus(int x, int y) {
////                        return DRAG_REGION_LIKELY_DRAG_XY;
////                    }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                };
//                newDropPlaceholder.setUIID("DropTargetPlaceholder");
//                newDropPlaceholder.setDropTarget(true);
//                newDropPlaceholder.setText("DropPlaceholder (" + dropTarget.getName() + ")");
//                if (Test.DEBUG) {
//                    newDropPlaceholder.setName("Comp.dropPlaceholder for " + dropTarget.getName() + ", w=" + newDropPlaceholder.getWidth() + ", h=" + newDropPlaceholder.getHeight());
//                }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//if (dragged.dropPlaceholder != null) {
//    if (dragged.dropPlaceholder.getParent() != null) {
//        Log.p("addDragOverListener: dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder)");
//        dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder);
//    } else {
//        Log.p("addDragOverListener: **should remove old dropPlaceholder, but it has no parent, dropPlaceholder=" + dragged.dropPlaceholder.getName());
//    }
//} else {
//    Log.p("addDragOverListener: dragged.dropPlaceholder==null!!");
//}
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                dropActionCall = null; //reset
//                dropAsSubtaskActionCall = null; //reset
//                if (getDragAndDropObject() instanceof Category) {
//                    if (afterCont.getDragAndDropObject() instanceof Category) { //can always drop a Category before another Category
//                        dropActionCall = () -> {
//                            ((Category) afterCont.getDragAndDropObject()).getOwner().removeFromList((ItemAndListCommonInterface) afterCont.getDragAndDropObject());
//                            ((Category) afterCont.getDragAndDropObject()).getOwner().addToList((ItemAndListCommonInterface) afterCont.getDragAndDropObject(), (Category) getDragAndDropObject(), true);
//                        };
//                    } else if (afterCont.getDragAndDropObject() == null) {//or at the end of the list (after either a Catgory and any expanded Item)
//                        dropActionCall = () -> {
//                            ((Category) afterCont.getDragAndDropObject()).getOwner().removeFromList((ItemAndListCommonInterface) afterCont.getDragAndDropObject());
//                            ((Category) afterCont.getDragAndDropObject()).getOwner().addToList((Category) getDragAndDropObject()); //add to end of CategoryList
//                        };
//                    }
//                } else if (getDragAndDropObject() instanceof Item) {
//                    if (getDragAndDropCategory() != null) { //dragged object belongs to a category
//                        //we can always drop *after* another Item which also has a Category (doesn't affect expanded subtasks since they return null for getDragAndDropCategory())
//                        if (beforeCont.getDragAndDropObject() instanceof Item && beforeCont.getDragAndDropCategory() != null) {
//                            dropActionCall = () -> {
//                                boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                beforeCont.getDragAndDropCategory().addToList((Item) beforeCont.getDragAndDropObject(), (Item) getDragAndDropObject(), true);
//                            };
//                            //dropping item right after a Category
//                        } else if (beforeCont.getDragAndDropObject() instanceof Category) {
//                            dropActionCall = () -> {
//                                boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                ((Category) beforeCont.getDragAndDropObject()).addToList(0, (Item) getDragAndDropObject()); //insert item at beginning of category's list of items
//                            };
//                            //we drop *before* another Item in a Category (eg when the subtasks of the previous item in the cateogry are expanded)
//                        }
//                        if (afterCont.getDragAndDropObject() instanceof Item && afterCont.getDragAndDropCategory() != null) {
//                            dropActionCall = () -> {
//                                boolean sameCategory = getDragAndDropCategory() == afterCont.getDragAndDropCategory();
//                                getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the
//                            };
//                        }
//                    } else { //dragging an item that does NOT belong to a category, only dropping as subtasks (not under categories since the logic is really unintutive!) //TODO!!! consider adding this again after all
//                        //assert:
//                        //dropping after an Item
//                        if (beforeCont.getDragAndDropObject() instanceof Item) {// && ((Item)beforeCont.getDragAndDropObject()).getOwner()==((Item)getDragAndDropObject()).getOwner())
//                            dropActionCall = () -> {
//                                ((Item) getDragAndDropObject()).removeFromList((Item) getDragAndDropObject()); //remove item from old owner list
//                                //Insert after the previous/before Item
//                                ((Item) beforeCont.getDragAndDropObject()).getOwner().addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the
//                            };
//                            dropAsSubtaskActionCall = () -> {
////                                getDragAndDropList().removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                ((Item) getDragAndDropObject()).removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
////                                afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the
//                                ((Item) beforeCont.getDragAndDropObject()).addToList(0, (Item) getDragAndDropObject()); //insert as the first subtask of the item before
//                            };
//
//                        }
//                    }
//                }
//</editor-fold>
                //Insert new dropPlaceholder:
//                Container parent = getParent().getParent();
//                Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
//                Component dropParent = dropTarget;
                while (!(treeList instanceof MyTree2) && treeList != null) {
                    dropParent = treeList;
                    treeList = treeList.getParent();
                }
//<editor-fold defaultstate="collapsed" desc="comment">
                //remove old dropPlaceholder
//                if (dragged.dropPlaceholder != null) {
//                    if (dragged.dropPlaceholder.getParent() != null) {
//                        Log.p("addDragOverListener: REMOVING old dragged.dropPlaceholder (" + dragged.dropPlaceholder.getName() + ")");
//                        oldDropPlaceholderIndex = dragged.dropPlaceholder.getParent().getComponentIndex(dragged.dropPlaceholder);
//                        dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder);
//                    } else {
//                        Log.p("addDragOverListener: **should remove old dropPlaceholder, but it has no parent, dropPlaceholder=" + dragged.dropPlaceholder.getName());
//                    }
//                } else {
//                    oldDropPlaceholderIndex = treeList.getComponentIndex(dropParent); //initilize old index to position of dragged (when no value defined for dropPlaceholder)
//                    Log.p("addDragOverListener: dragged.dropPlaceholder==null!!");
//                }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                Container dropParent = dropTarget.getParent();
//                if (dropParent != null) {
//                    treeList = dropParent.getParent();
//</editor-fold>
                if (treeList != null) {
                    MyDragAndDropSwipeableContainer beforeCont;
                    MyDragAndDropSwipeableContainer afterCont;

                    oldDropPlaceholderIndex = dragged.dropPlaceholder != null
                            ? dragged.dropPlaceholder.getParent().getComponentIndex(dragged.dropPlaceholder)
                            : treeList.getComponentIndex(dropParent);

//                int index = parent.getComponentIndex(this); //get my current position
//                int index = parent.getComponentIndex(dropTarget); //get my current position
//                        int index = treeList.getComponentIndex(dropParent); //get my current position
                    int index = treeList.getComponentIndex(dropParent); //get my current position
                    boolean draggingUpwards = index <= oldDropPlaceholderIndex; //NB. Also works for dropPlaceholder 'under' dragged when initiating drag
                    if (draggingUpwards) {
                        beforeCont = (MyDragAndDropSwipeableContainer) dropTarget;
                        afterCont = index == 0 ? null : findDropTargetIn((Container) treeList.getComponentAt(index - 1));
                    } else {
                        afterCont = (MyDragAndDropSwipeableContainer) dropTarget;
                        beforeCont = index >= treeList.getComponentCount() ? null : findDropTargetIn((Container) treeList.getComponentAt(index));
                    }
                    /**
                     * DOCUMENTATION OF WHERE INSERTS/DROPS ARE POSSIBLE Cat1
                     * Cat2 Cat3 * Cat1 T1 T2 Cat2 T3
                     *
                     */

                    dropActionCall = null; //reset
                    dropAsSubtaskActionCall = null; //reset
                    if (getDragAndDropObject() instanceof Category) { //dragging a Category
                        if (afterCont.getDragAndDropObject() instanceof Category || afterCont.getDragAndDropObject() == null) { //can always drop a Category before another Category
                            dropActionCall = () -> {
                                ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) getDragAndDropObject()).getOwner();
//                                categoryOwnerList.removeFromList((ItemAndListCommonInterface) afterCont.getDragAndDropObject());
//                                categoryOwnerList.addToList((ItemAndListCommonInterface) afterCont.getDragAndDropObject(), (Category) getDragAndDropObject(), true);
//                                DAO.getInstance().saveInBackground((ParseObject) categoryOwnerList);
                                int indexCatList = afterCont.getDragAndDropObject() != null
                                        ? categoryOwnerList.getItemIndex((ItemAndListCommonInterface) afterCont.getDragAndDropObject())
                                        : categoryOwnerList.size();
                                moveCategoryAndSave(categoryOwnerList, (Category) getDragAndDropObject(), indexCatList);
                            };
                        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                        else if (afterCont.getDragAndDropObject() == null) {//or at the end of the list (after either a Catgory and any expanded Item)
//                            dropActionCall = () -> {
//                                ItemAndListCommonInterface categoryOwnerList = ((Category) afterCont.getDragAndDropObject()).getOwner();
//                                categoryOwnerList.removeFromList((ItemAndListCommonInterface) afterCont.getDragAndDropObject());
//                                categoryOwnerList.addToList((Category) getDragAndDropObject()); //add to end of CategoryList
//                                DAO.getInstance().saveInBackground((ParseObject) categoryOwnerList);
//                            };
//                        }
//</editor-fold>
                    } else if (getDragAndDropObject() instanceof Item) {
                        if (getDragAndDropCategory() != null) { //dragged object belongs to a category
                            //we can always drop *after* another 'before' Item which also has a Category (isn't triggered for expanded subtasks since they return null for getDragAndDropCategory())
                            if (beforeCont.getDragAndDropObject() instanceof Item && beforeCont.getDragAndDropCategory() != null) {
                                dropActionCall = () -> {
//                                    boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    beforeCont.getDragAndDropCategory().addToList((Item) beforeCont.getDragAndDropObject(), (Item) getDragAndDropObject(), true);
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), beforeCont.getDragAndDropCategory(), (ParseObject) getDragAndDropObject());
                                    int indexItem = ((ItemAndListCommonInterface) beforeCont.getDragAndDropCategory()).getItemIndex((Item) beforeCont.getDragAndDropObject()) + 1;
                                    moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeCont.getDragAndDropCategory(), (Item) getDragAndDropObject(), indexItem);
                                };
                                //dropping item right after a Category => move to that category
                            } else if (beforeCont.getDragAndDropObject() instanceof Category) {
                                dropActionCall = () -> {
//                                    boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    ((Category) beforeCont.getDragAndDropObject()).addToList(0, (Item) getDragAndDropObject()); //insert item at beginning of category's list of items
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), (ParseObject) beforeCont.getDragAndDropObject(), (ParseObject) getDragAndDropObject());
                                    int indexItem = 0; //insert item at beginning of category's list of items
                                    moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeCont.getDragAndDropObject(), (Item) getDragAndDropObject(), indexItem);
                                };
                                //we drop *before* another Item in a Category (eg when the subtasks of the previous item in the cateogry are expanded)
                            } else if (afterCont.getDragAndDropObject() instanceof Item && afterCont.getDragAndDropCategory() != null) {
                                dropActionCall = () -> {
//                                    boolean sameCategory = getDragAndDropCategory() == afterCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the 
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), (ParseObject) afterCont.getDragAndDropCategory(), (ParseObject) getDragAndDropObject());
                                    int indexItem = ((ItemAndListCommonInterface) afterCont.getDragAndDropCategory()).getItemIndex((Item) afterCont.getDragAndDropObject());
                                    moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeCont.getDragAndDropCategory(), (Item) getDragAndDropObject(), indexItem);
                                };
                            }
                        } else { //dragging an item that does NOT belong to a category, only dropping as subtasks (not under categories since the logic is really unintutive!) //TODO!!! consider adding this again after all
                            //assert: 
                            //dropping *after* a 'before' Item
                            if (beforeCont.getDragAndDropObject() instanceof Item) {// && ((Item)beforeCont.getDragAndDropObject()).getOwner()==((Item)getDragAndDropObject()).getOwner())
                                dropActionCall = () -> {
////                                    ((Item) getDragAndDropObject()).removeFromList((Item) getDragAndDropObject()); //remove item from old owner list
////                                    ((Item) getDragAndDropObject()).getOwner().removeFromList((Item) getDragAndDropObject()); //remove item from old owner list
////                                    ((Item) getDragAndDropObject()).removeMeFromOwner(); //remove item from old owner list
//                                    //NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
//                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    ItemAndListCommonInterface newOwner = ((Item) beforeCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    int index2 = newOwner.getItemIndex(((Item) beforeCont.getDragAndDropObject())); //get index before removing since oldOwner and newOwner may be the same list
//                                    oldOwner.removeFromList((Item) getDragAndDropObject());
//                                    //Insert after the previous/before Item
////                                    ((Item) beforeCont.getDragAndDropObject()).getOwner().addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the 
////                                    newOwner.addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), false); //insert *after* the 
//                                    newOwner.addToList(index2, (Item) getDragAndDropObject()); //insert *after* the 
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
                                    ItemAndListCommonInterface newOwner = ((Item) beforeCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
                                    int indexI = newOwner.getItemIndex(((Item) beforeCont.getDragAndDropObject())); 
                                    moveItemAndSave(((Item) getDragAndDropObject()).getOwner(), newOwner, (Item)getDragAndDropObject(), indexI);
                                };
                                dropAsSubtaskActionCall = () -> {
////                                getDragAndDropList().removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
////                                    ((Item) getDragAndDropObject()).getOwner().removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
////                                    ((Item) getDragAndDropObject()).removeMeFromOwner(); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    //NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
//                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    ItemAndListCommonInterface newOwner = ((Item) beforeCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    oldOwner.removeFromList((Item) getDragAndDropObject());
////                                afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the 
//                                    newOwner.addToList(0, (Item) getDragAndDropObject()); //insert as the first subtask of the item before
////                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) ((Item) getDragAndDropObject()).getOwner(),
////                                            (ParseObject) ((Item) beforeCont.getDragAndDropObject()).getOwner(), ((Item) beforeCont.getDragAndDropObject()), (ParseObject) getDragAndDropObject());
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
                                    ItemAndListCommonInterface newOwnerPrj = ((Item) beforeCont.getDragAndDropObject()); 
                                    int indexI = 0;
                                    moveItemAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item)getDragAndDropObject(), indexI);
                                };

                            } else if (afterCont.getDragAndDropObject() instanceof Item) { //dropping *before* an item
                                dropActionCall = () -> {
                                    //NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
//                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    ItemAndListCommonInterface newOwner = ((Item) afterCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    oldOwner.removeFromList((Item) getDragAndDropObject());
//                                    //Insert after the previous/before Item
////                                    ((Item) beforeCont.getDragAndDropObject()).getOwner().addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the 
//                                    newOwner.addToList(((Item) afterCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the 
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
                                    ItemAndListCommonInterface newOwnerB = ((Item) afterCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
                                    int indexBef = newOwnerB.getItemIndex(((Item) afterCont.getDragAndDropObject())); 
                                    moveItemAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerB, (Item)getDragAndDropObject(), indexBef);
                                };
//                                dropAsSubtaskActionCall = () -> {}; //CANNOT drop as subtask *before* an item
                            }
                        }
                    }
                    //remove old dropPlaceholder (if any) - remove even if no new dropPosition since we don't want the old dropPlaceholder to hang as we drag further one
                    if (dragged.dropPlaceholder != null) {
                        dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder);
                    }

                    //if a drop action is possible at this position
                    if (dropActionCall != null || dropAsSubtaskActionCall != null) {
                        //Create new dropPlaceholder
                        Label newDropPlaceholder = new Label() {
                            Component dropTarget1 = dropTarget;

                            @Override
                            public int getWidth() {
                                return dragged.draggedWidth;
                            }

                            @Override
                            public int getHeight() {
                                return dragged.draggedHeight;
                            }

                            @Override
                            public Dimension getPreferredSize() {
                                return new Dimension(dragged.draggedWidth, dragged.draggedHeight);
                            }

                            @Override
                            public void drop(Component drag1, int x, int y) {
                                Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName());
                                if (insertBelow(x) && dropAsSubtaskActionCall != null) {
                                    dropAsSubtaskActionCall.call();
                                } else {
                                    dropActionCall.call();
                                }
//                                dragged.dropSucceeded = true;
                                getComponentForm().animateHierarchy(300);

                                dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
                                dragged.setFocusable(false); //set focusable false once the drop (activated by longPress) is completed
                                ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //simply keep same position as whereto the list was scrolled during the drag, then inserted element should 'stay in place'
                                super.drop(dragged, x, y); //Container.drop implements the first quick move of the container itself
                                ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw

                            }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    protected Image getDragImageXXX() {
//                        if (!isHidden()) {
//                            ASSERT.that(!isHidden(), "***Calling getDragImage() on " + getName() + " while it's hidden"
//                                    + ", w=" + getWidth() + ", h=" + getHeight());
//                        } else {
//                            Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
//                                    + ", w=" + getWidth() + ", h=" + getHeight());
//                        }
//                        if (dragImage == null) {
//                            dragImage = super.getDragImage();
//                        }
//                        return dragImage;
//                    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    @Override
//                    protected int getDragRegionStatus(int x, int y) {
//                        return DRAG_REGION_LIKELY_DRAG_XY;
//                    }
//</editor-fold>
                        };
//<editor-fold defaultstate="collapsed" desc="comment">
//                        Label newDropPlaceholderOLD = new Label() {
//                            int draggedWidth = dragged.draggedWidth;
//                            int draggedHeight = dragged.draggedHeight;
////                    Dimension prefSize = dragged.getPreferredSize();
//                            Component dropTarget1 = dropTarget;
//
////                    @Override
//                            public int getWidth() {
//                                if (draggedWidth <= 0) {
//                                    Log.p("Comp.getWidth()==" + draggedWidth + "!!!");
//                                }
//                                return draggedWidth;
//                            }
//
////                    @Override
//                            public int getHeight() {
//                                if (draggedHeight <= 0) {
//                                    Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                                }
//                                return draggedHeight;
//                            }
//
//                            @Override
//                            public Dimension getPreferredSize() {
//                                if (draggedHeight <= 0) {
//                                    Log.p("Comp.getHeight()=" + draggedHeight + "!!!");
//                                }
//                                return new Dimension(draggedWidth, draggedHeight);
////                        return prefSize;
//                            }
//
//                            @Override
//                            public void drop(Component drag1, int x, int y) {
//                                Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName());
////<editor-fold defaultstate="collapsed" desc="comment">
////                        MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        if (false) {
////                            MyDragAndDropSwipeableContainer.this.drop(drag1, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                        }
////                        dropTarget.drop(dragged, x, y);
////</editor-fold>
//                                dropTarget1.drop(drag1, x, y);
//                            }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////                    protected Image getDragImageXXX() {
////                        if (!isHidden()) {
////                            ASSERT.that(!isHidden(), "***Calling getDragImage() on " + getName() + " while it's hidden"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        } else {
////                            Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
////                                    + ", w=" + getWidth() + ", h=" + getHeight());
////                        }
////                        if (dragImage == null) {
////                            dragImage = super.getDragImage();
////                        }
////                        return dragImage;
////                    }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                    @Override
////                    protected int getDragRegionStatus(int x, int y) {
////                        return DRAG_REGION_LIKELY_DRAG_XY;
////                    }
////</editor-fold>
//                        };
//</editor-fold>
                        newDropPlaceholder.setUIID("DropTargetPlaceholder");
                        newDropPlaceholder.setDropTarget(true);
                        newDropPlaceholder.setText("DropPlaceholder (" + dropTarget.getName() + ")");
                        if (Test.DEBUG) {
                            newDropPlaceholder.setName("Comp.dropPlaceholder for " + dropTarget.getName() + ", w=" + newDropPlaceholder.getWidth() + ", h=" + newDropPlaceholder.getHeight());
                        }

                        //insert new dropPlaceholder
                        treeList.addComponent(index, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (index <= oldDropPlaceholderIndex) {
//                        treeList.addComponent(index, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//                        Log.p("addDragOverListener: treeList: INSERT newDropPlaceholder=" + newDropPlaceholder.getName() + " at index   =" + index + " for dropTarget=" + dropTarget.getName());
//                    } else {
////                        treeList.addComponent(index + 1, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//                        treeList.addComponent(index, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//                        Log.p("addDragOverListener: treeList: INSERT newDropPlaceholder=" + newDropPlaceholder.getName() + " at index+1 =" + index + 1 + " for dropTarget=" + dropTarget.getName());
//                    }
//</editor-fold>
                        dragged.dropPlaceholder = newDropPlaceholder; //save new placeholder
//                    getComponentForm().revalidate();
//                        dropTarget.getComponentForm().animateLayout(300);
//                        newDropPlaceholder.getComponentForm().animateLayout(300); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
                        newDropPlaceholder.getComponentForm().animateHierarchy(300); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//                        Log.p("addDragOverListener: new dropPlaceholder=" + newDropPlaceholder.getName() + ", inserted position=" + index + ", for dropTarget=" + dropTarget.getName());
                    } else {
                        Log.p("addDragOverListener: treeList==null!!! for dropTarget=" + dropTarget.getName());
//                        ASSERT.that(treeList instanceof MyTree2, "treeList not instanceof MyTree2, treelist="
//                                + (treeList != null ? treeList.getName() : "nullx") + ", for dropTarget=" + dropTarget.getName());
//                        Log.p("addDragOverListener: treeList==null for dropTarget=" + dropTarget.getName());
                    }
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                } else {
//                    Log.p("addDragOverListener: treeList: dropParent == null!! for dropTarget=" + dropTarget.getName());
//                }
//                } else {
//                    getParent().addComponent(getParent().getComponentIndex(this), newDropPlaceholder);
//                }
//                if (false) {
//                    dragged.dropPlaceholder = newDropPlaceholder; //save new placeholder
//                    getComponentForm().revalidate();
////                e.consume(); //consume the event, otherwise it repeats(?)
//                }
//            }
//</editor-fold>
                Log.p("---------------- END MyDragAndDropSwipeableContainer() ------------------------------------");
            }
        });
    }

    private static MyDragAndDropSwipeableContainer findDropTargetIn(Container cont) {
        int count = cont.getComponentCount();
        for (int i = count - 1; i >= 0; i--) {
            Component cmp = cont.getComponentAt(i);
//            if (cmp.isDropTarget()) {
            if (cmp instanceof MyDragAndDropSwipeableContainer) {
                return (MyDragAndDropSwipeableContainer) cmp;
            }
            if (cmp instanceof Container) {
                MyDragAndDropSwipeableContainer component = findDropTargetIn((Container) cmp);
                if (component != null) {
                    return component;
                }
            }
        }
        return null;
    }

    private int getContainerIndexInTreeListXXX(MyDragAndDropSwipeableContainer dropTarget) {
        Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
        Component dropParent = dropTarget;
        while (!(treeList instanceof MyTree2) && treeList != null) {
            dropParent = treeList;
            treeList = treeList.getParent();
        }
        return treeList != null ? treeList.getComponentIndex(dropParent) : -1;
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void refreshAfterDrop() {
//        ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//    }
//</editor-fold>

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

//<editor-fold defaultstate="collapsed" desc="comment">
    public boolean dropItemInCategoryView(ItemAndListCommonInterface before, ItemAndListCommonInterface after, ItemAndListCommonInterface dragged,
            Category beforeCategory, Category afterCategory, Item draggedItem, boolean insertBelow) {

        if (beforeCategory == null) {

        } else if (afterCategory == null) {
            //Cat1
            // T1
            //  t11
            //    t111
            //    t112
            //  t12
            //T2
            //T3        
        }
        return true;
    }

    public boolean dropCategory(ItemAndListCommonInterface before, ItemAndListCommonInterface after, Category dragged) {
        //TODO support subcategories
        if (before == null && after instanceof Category) { //inserting at the very top of the list (dropTargetContainer is *before* the first element on the screen)
            //insert in the same list as after
            after.getOwner().addToList(0, dragged);
        } else if (after == null && before instanceof Category) { //inserting at the very end of the list (dropTargetContainer is *after* the last element on the screen)
            //insert in the same list as before
            before.getOwner().addToList(dragged);
        } else if (before instanceof Category && after instanceof Category) {
            //else insert after 'after' (e.g. before is subtask11 (a subtask to Task1) and after is Task2, so added is added *after* subtask11)
            int index = before.getOwner().getList().indexOf(before);
            before.getOwner().addToList(index + 1, dragged);
        } else if (before instanceof Category) {
            int index = before.getOwner().getList().indexOf(before);
            before.getOwner().addToList(index + 1, dragged);
        } else if (after instanceof Category) {
            int index = after.getOwner().getList().indexOf(after);
            after.getOwner().addToList(index, dragged);
        } else {
            //dropping Catgory in between something other than categories (eg between two Items)
            return false;
        }
        return true;
    }

    public boolean dropBetweenItems(Item before, Item after, Item dragged, boolean insertBelow) {

        if (before == null) { //inserting at the very top of the list (dropTargetContainer is *before* the first element on the screen)
            //insert in the same list as after
            after.getOwner().addToList(0, dragged);
        } else if (after == null) { //inserting at the very end of the list (dropTargetContainer is *after* the last element on the screen)
            if (!insertBelow) {
                //insert in the same list as before
                before.getOwner().addToList(dragged);
            } else {

            }
        } else {
            if (before.getOwner() == after.getOwner() || after.getOwner() == before) { //before and after belong to same owner, insert in between them (<=> after before)
                if (!insertBelow) {
                    int index = before.getOwner().getList().indexOf(after);
                    before.getOwner().addToList(index, dragged);
                } else {

                }
            } else { //else insert after 'after' (e.g. before is subtask11 (a subtask to Task1) and after is Task2, so added is added *after* subtask11)
                if (!insertBelow) {
                    int index = after.getOwner().getList().indexOf(after);
                    after.getOwner().addToList(index + 1, dragged);
                } else {

                }
            }
        }
        return true;
    }

    @Override
    public void drop(Component dragged, int x, int y) {
        //do nothing if dropped on this container
    }

    public void dropXXX(Component dragged, int x, int y) {
//        Log.p("drop-MyDragAndD");
        Log.p("MyDragAndD.drop, dropTarget=" + getName() + ", dragged=" + dragged.getName());

        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself //TODO remove isValidDropTarget checks as code below should check valid conditions and do nothing if not
//            dragged.setHidden(false); // was set hidden in dragEnter
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

                    } else if (getDragAndDropCategory() != null) { //D&D item1 from category1 onto an item2 in category1 OR category2  => insert item1 into category2 at item2's position
                        //TODO!!!! if both items in same Category, just move
//                        if (targetItem)
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
            dragged.setFocusable(false); //set focusable false once the drop (activated by longPress) is completed
            if (dropExecuted) {
                ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //simply keep same position as whereto the list was scrolled during the drag, then inserted element should 'stay in place'

                //moved the dragged container to new position to quickly refresh the screen  
                if (false) {
                    Container draggedParentContainer = dragged.getParent();
                    draggedParentContainer.removeComponent(this);
                    int droppedParentIndex = getParent().getComponentIndex(this);
                    getParent().addComponent(droppedParentIndex, dragged);
                    getComponentForm().animateHierarchy(400);
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                dragged.setHidden(false); //set hidden in dragEnter
//ALL this done in dragFinished!
//                draggedCont.oldParent = null; //reset on successful drop
//                draggedCont.oldComp = null; //reset on successful drop
//                draggedCont.oldPos = -1; //reset
//                if (draggedCont.dropPlaceholder != null&&draggedCont.dropPlaceholder.getParent()!=null) {
//                    draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder); //remove the old placeholder on successful drop
//                    draggedCont.dropPlaceholder = null;
//                }
//</editor-fold>
                super.drop(dragged, x, y); //Container.drop implements the first quick move of the container itself
//                removePlaceholder(); //do *before* setting dropPlaceholder=null!
//                dropPlaceholder = null; //reset placeholder
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) { //now done in dragFinished()
//            removePlaceholder(); //do *before* setting dropPlaceholder=null!
//            refreshAfterDrop();
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * This method returns an image representing the dragged component, it can
     * be overriden by subclasses to customize the look of the image, the image
     * will be overlaid on top of the form during a drag and drop operation.
     * THJ: NB. image is cached during drag, so getDragImage cannot be used to
     * *dynamically* change the dragged image.
     *
     * @return an image
     */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
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
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
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
////                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                }
////            };
////            dropPlaceholder.setUIID("DropTargetPlaceholder");
////            //TODO!!! change to look like a subtask when moving to the right
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    }
//    private boolean orgHiddenState = false;
    /**
     * Invoked on the focus component to let it know that drag has started on
     * the parent container for the case of a component that doesn't support
     * scrolling. THJ:
     */
//    protected void dragInitiatedXXX() { //only hide in dragEnter to avoid hiding invisible container
////         if (dragged == this) {
//        Log.p("MyDragAndD.dragInitiated for=" + getName() + ", w=" + getWidth() + ", h=" + getHeight());
//
//        draggedWidth = getWidth();
//        draggedHeight = getHeight();
//        setHidden(true); //once we have the Height/Width, hide the component
//        super.dragInitiated();
////                }
////        orgHiddenState = isHidden();
////        setHidden(true); //hide the original container (CN1 just makes it invisible to leave a blank space in original position)
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void drawDraggedImageXXX(Graphics g) {
////        if (dragImage == null) {
////            dragImage = getDragImage();
////        }
////        drawDraggedImage(g, dragImage, draggedx, draggedy);
//    }
//</editor-fold>
    @Override
    protected Image getDragImage() {
        if (!isHidden()) {
            ASSERT.that(!isHidden(), "***Calling getDragImage() on " + getName() + " while it's hidden"
                    + ", w=" + getWidth() + ", h=" + getHeight());
        } else {
            Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
                    + ", w=" + getWidth() + ", h=" + getHeight());
        }
        if (dragImage2 == null) {
            dragImage2 = super.getDragImage();
        }
        return dragImage2;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * Draws the given image at x/y, this method can be overriden to draw
//     * additional information such as positive or negative drop indication
//     *
//     * @param g the graphics context
//     * @param img the image
//     * @param x x position
//     * @param y y position
//     */
//    protected void drawDraggedImageXXX(Graphics g, Image img, int x, int y) {
////        g.drawImage(img, x - getWidth() / 2, y - getHeight() / 2);
//        g.drawImage(img, x, y);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    int dropPlaceholderIndex = -1;
//    int oldDropPlaceholderIndex = -1;
//    Container dropPlaceholderCont;
//    Component dropOriginalDropTarget;
//    private int draggedWidth;
//    private int draggedHeigth;
//    private int oldPos = -1;
//    private Container oldParent;
//    private Component oldComp;
//    private boolean oldDraggedContVisible = true;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component makeDragPlaceholderXXX() {
//        Component dropPlaceholder = new Component() {
//            @Override
//            public int getWidth() {
//                return MyDragAndDropSwipeableContainer.this.getWidth(); //placeholder takes same size as dragged element
//            }
//
//            @Override
//            public int getHeight() {
//                return MyDragAndDropSwipeableContainer.this.getHeight();
//            }
//
//            @Override
//            public void drop(Component dragged, int x, int y) {
//                MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
//            }
//        };
//        dropPlaceholder.setUIID("DropTargetPlaceholder");
//        //TODO!!! change to look like a subtask when moving to the right
////            wasHidden = isHidden();
////            setHidden(true); //completely hide the container for the dragged element (default is to set it invisible in Component.pointerDragged(final int x, final int y)
//        return dropPlaceholder;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void removeOldPlaceholderXXX(MyDragAndDropSwipeableContainer dropPlaceholder) {
////        if (oldDropPlaceholderIndex != -1 && dropPlaceholderCont != null) {
////            dropPlaceholderCont.removeComponent(dropPlaceholder);
////        }
////        oldDropPlaceholderIndex = -1;
////        dropPlaceholderCont = null;
//        if (dropPlaceholder != null && dropPlaceholder.getParent() != null) {
//            dropPlaceholder.getParent().removeComponent(dropPlaceholder);
//            ASSERT.that("removing placeholder from parent");
//        }
//        dropPlaceholder = null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * This callback method indicates that a component drag has just entered
     * this component.
     *
     * THJ:called on Component.dropTarget in pointerDragged(x,y) and in
     * dragFinishedImpl(x,y) (itself called from pointerReleased(int x, int y))
     *
     * @param dragged the component being dragged
     */
//    @Override
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
////                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
////                }
////            };
////            dropPlaceholder.setUIID("DropTargetPlaceholder");
////            //TODO!!! change to look like a subtask when moving to the right
////        }
////</editor-fold>
////        Log.p("dragEnter-MyDragAndD");
//        Log.p("MyDragAndD.dragEnter, element=" + getName());
//
//        if (dragged instanceof MyDragAndDropSwipeableContainer) {
////            if (dragged == this) {
////                this.setHidden(true);
////            }
//            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
////<editor-fold defaultstate="collapsed" desc="comment">
////            draggedWidth = dragged.getWidth();
////            draggedHeigth = dragged.getHeight();
////            if (false && draggedCont == this) {// && !draggedCont.isHidden()) {
////                //the first time we start dragging, the component will enter itself
//////                draggedWidth = dragged.getWidth();
//////                draggedHeigth = dragged.getHeight();
//////                draggedCont.orgHiddenState = draggedCont.isHidden();
////                dragged.setHidden(true); //this prevents us from ever enter the dragged container again
////            }
////            draggedCont.setDragPlaceholder();
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////            while (!(parent instanceof MyTree2) && parent.getParent() != null) {
////                element = parent;
////                parent = parent.getParent();
////            }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////remove old placeholder - do in dragExit()
////            if (false && draggedCont.dropPlaceholder != null
////                    && draggedCont.dropPlaceholder.getParent() != null
////                    && draggedCont.dropPlaceholder.getParent() != actuallyDraggedElementsParent) {
////                draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
////                Log.p("removing placeholder from parent");
////            }
////</editor-fold>
//            //remove old dropPlaceholder before creating and inserting a new one below
//            if (draggedCont.dropPlaceholder != null //                    && draggedCont.dropPlaceholder.getParent() != null //shouldnt happen
//                    //                    && draggedCont.dropPlaceholder.getParent() != draggedParent
//                    ) {
////                Log.p("dragEnter-removeOldDropPlaceholder-MyDragAndD");
//                Log.p("MyDragAndD.dragEnter (removing oldDropPlaceholder), element=" + draggedCont.dropPlaceholder.getName());
//                draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
//                draggedCont.dropPlaceholder = null;
//            }
//
//            //<editor-fold defaultstate="collapsed" desc="comment">
//            //remove old placeholder (even if no new place found) //TODO!!! How to remove when eg dropping back on original position?
////            if (actuallyDraggedElementsParent != null && actuallyDraggedElement != null) {
////                if (dropPlaceholderIndex > -1) {
////set new placholder
////                    draggedCont.dropPlaceholder=makeDragPlaceholder();
////</editor-fold>
////            draggedCont.dropPlaceholder = new Component() {
//            Component newDropPlaceholder = new Component() {
//
//                @Override
//                public int getWidth() {
////<editor-fold defaultstate="collapsed" desc="comment">
////                            return MyDragAndDropSwipeableContainer.this.getWidth(); //placeholder takes same size as dragged element
////                            return MyDragAndDropSwipeableContainer.this.getDragImage().getWidth(); //placeholder takes same size as dragged element
////                            return dragged.getWidth(); //placeholder takes same size as dragged element
////                            return draggedWidth; //placeholder takes same size as dragged element
////</editor-fold>
//                    return draggedCont.getDragImage().getWidth(); //placeholder takes same size as dragged element
////                    return dragged.getWidth(); //placeholder takes same size as dragged element
//                }
//
//                @Override
//                public int getHeight() {
////<editor-fold defaultstate="collapsed" desc="comment">
////                            return MyDragAndDropSwipeableContainer.this.getDragImage().getHeight();
////                            return dragged.getHeight();
////                            return draggedHeigth;
////</editor-fold>
////                    return dragged.getHeight();
//                    return draggedCont.getDragImage().getHeight(); //placeholder takes same size as dragged element
//                }
//
//                @Override
//                public void drop(Component dragged, int x, int y) {
////                    Log.p("drop-Comp");
//                    Log.p("Comp.drop, element=" + getName());
//                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
//                }
//
//                @Override
//                protected void dragEnter(Component dragged) {
//                    //do nothing when drag enters the placeholder container
////<editor-fold defaultstate="collapsed" desc="comment">
////                            if (this == draggedCont.dropPlaceholder) {
////                                return;
////                            } else {
////                            MyDragAndDropSwipeableContainer.this.dragEnter(dragged);
////                            }
////</editor-fold>
////                    Log.p("dragEnter-Comp");
//                    Log.p("Comp.dragEnter, element=" + getName());
//                }
//
//                @Override
//                protected void dragExit(Component dragged) {
////        if (dropPlaceholder!=null)
//                    if (dragged instanceof MyDragAndDropSwipeableContainer) {
//                        MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
//                        if (draggedCont.dropPlaceholder != null) {// && draggedCont.dropPlaceholder.getParent() != null) {
//                            draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
//                            draggedCont.dropPlaceholder = null;
//                        }
//                    }
//                    Log.p("Comp.dragExit, element=" + getName());
//                }
//
//            };
//            newDropPlaceholder.setUIID("DropTargetPlaceholder");
//            newDropPlaceholder.setDropTarget(true);
//            if (Test.DEBUG) {
//                newDropPlaceholder.setName(MyDragAndDropSwipeableContainer.this.getName());
//            }
////            newDropPlaceholder.setDropTarget(true);
//            //TODO!!! change to look like a subtask when moving to the right
//
////            Component actuallyDraggedElement = null; //stores the element for which to find index in parent
////            Container actuallyDraggedElementsParent = null; //.getParent();
////            int dropPlaceholderIndex = actuallyDraggedElementsParent.getComponentIndex(actuallyDraggedElement);
////            actuallyDraggedElementsParent.addComponent(dropPlaceholderIndex, draggedCont.dropPlaceholder); //insert new one
//            if (draggedCont == this) {
////                Log.p("draggedCont == this");
//                Log.p("MyDragAndD.dragEnter, draggedCont == this, element=" + getName());
//                //get the Container holding the list of elements
//                //The dragged container is wrapped into a Container at NORTH SOUTH being used to show expanded children
//                Component actuallyDraggedElement = draggedCont.getParent(); //the parent of MyDragAndDrop (NB!! This is because MyTree2 wraps MyDragAndDrop inside a BorderLayout)
//                Container actuallyDraggedElementsParent = actuallyDraggedElement.getParent();
//                //the first time we start dragging, the component will enter itself
//                draggedCont.dragImage = getDragImage(); //store image before hiding
////                draggedCont.setHidden(true); //this prevents us from ever enter the dragged container again
//                Log.p("MyDragAndD.dragEnter: hiding dragged element=" + draggedCont.getName());
//
//                draggedCont.oldComp = actuallyDraggedElement; //dragged.getParent();
//                draggedCont.oldParent = actuallyDraggedElementsParent; //dragged.getParent();
//                draggedCont.oldPos = actuallyDraggedElementsParent.getComponentIndex(actuallyDraggedElement); //dragged.getParent().getComponentIndex(dragged);
//                draggedCont.oldDraggedContVisible = true;
////                        dragged.getParent().removeComponent(dragged); //this prevents us from ever enter the dragged container again
////                actuallyDraggedElementsParent.removeComponent(actuallyDraggedElement); //this prevents us from ever enter the dragged container again
////                actuallyDraggedElementsParent.addComponent(draggedCont.oldPos, draggedCont.dropPlaceholder); //insert new one
////                actuallyDraggedElementsParent.animateLayout(150);
//            } else { //normal case - the drag enters another element
////                Log.p("draggedCont != this");
//                Log.p("MyDragAndD.dragEnter, draggedCont != this, this=" + getName() + ", dragged=" + draggedCont.getName());
//
//                Container actualDropTargetElement = this.getParent(); //as above: get the container wrapped around the MyDragAndDrop...
//                Container actualDropTargetElementParent = actualDropTargetElement.getParent();
//                int dropPlaceholderIndex = actualDropTargetElementParent.getComponentIndex(actualDropTargetElement);
//                draggedCont.dropPlaceholder = newDropPlaceholder;
//                if (draggedCont.dropPlaceholder != null && draggedCont.dropPlaceholderOldIndex != -1 && draggedCont.dropPlaceholderOldIndex <= dropPlaceholderIndex) {
//                    dropPlaceholderIndex += 1; //add +1 when dragging down
//                }
//                draggedCont.dropPlaceholderOldIndex = dropPlaceholderIndex; //save index for next time - to track if we're dragging up or down
//
//                actualDropTargetElementParent.addComponent(dropPlaceholderIndex, draggedCont.dropPlaceholder); //insert new one
//                if (draggedCont.oldDraggedContVisible) {
////                    draggedCont.oldParent.removeComponent(draggedCont.oldComp); //this prevents us from ever enter the dragged container again
////                    draggedCont.oldComp.setHidden(true);
//                    draggedCont.oldDraggedContVisible = false;
//                }
////                        parent.revalidate();
////            actuallyDraggedElementsParent.animateLayout(150);
//                if (getAnimationManager().isAnimating()) {
//                    getAnimationManager().flushAnimation(() -> {
//                        actualDropTargetElementParent.animateLayout(300);
//                    });
//                }
////                getComponentForm().animateHierarchy(300);
//            }
////            Log.p("parent=" + actuallyDraggedElementsParent + ", " + actuallyDraggedElementsParent.getComponentCount() + ", index=" + actuallyDraggedElementsParent.getComponentIndex(this));
////        }
////            }
//        }
//    }
//</editor-fold>
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
////                    MyDragAndDropSwipeableContainer.this.drop(dragged, x, y); //when dropping on placeholder, dropActionCall drop on original droptarget
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
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * This callback method provides an indication for a drop target that a drag
     * operation is exiting the bounds of this component and it should clear all
     * relevant state if such state exists. E.g. if a component provides drop
     * indication visualization in draggingOver this visualization should be
     * cleared..
     *
     * @param dragged the component being dragged
     */
//    @Override
//    protected void dragExitXXX(Component dragged) {
////        if (dropPlaceholder!=null)
//        if (false && dragged instanceof MyDragAndDropSwipeableContainer) {
//            MyDragAndDropSwipeableContainer draggedCont = (MyDragAndDropSwipeableContainer) dragged;
//            if (draggedCont.dropPlaceholder != null) {// && draggedCont.dropPlaceholder.getParent() != null) {
//                draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder);
//                draggedCont.dropPlaceholder = null;
//            }
////            Log.p("dragExit");
//            Log.p("MyDragAndD.dragEnter, draggedCont != this, this=" + getName() + ", dragged=" + draggedCont.getName());
//
//        } else {
////            Log.p("dragExit");
//            Log.p("MyDragAndD.dragExit, element=" + getName() + ", dragged=" + dragged.getName());
//
//        }
//    }
//</editor-fold>
    /**
     * Callback indicating that the drag has finished either via drop or by
     * releasing the component. THJ: called in last line of
     * Component.dragFinishedImpl(int x, int y) on the component that was
     * dragged (not on the drop target). If this component is removed from the
     * Form, the dragged component will be set visible before and nothing else.
     *
     * @param x the x location
     * @param y the y location
     */
    @Override
    protected void dragFinished(int x, int y) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        removePlaceholder(); //do *before* setting dropPlaceholder=null!
//        setHidden(wasHidden);
//        dropPlaceholder.getParent().removeComponent(dropPlaceholder);
//        setHidden(orgHiddenState); //restore hidden state
//        setHidden(false); //restore hidden state - do here in addition to in drop to ensure a failed drop doesn't lead to hiding it forever
//        if (false) { //done in drop()
//            if (dropPlaceholder != null && dropPlaceholder.getParent() != null) {
//                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
//            }
//        }
//</editor-fold>
//        Log.p("dragFinished-MyDragAndD");
        Log.p("MyDragAndD.dragFinished for element=" + getName() + ", UNHIDE, remove dropPlaceholder=" + dropPlaceholder != null ? dropPlaceholder.getName() : "nullx");
//<editor-fold defaultstate="collapsed" desc="comment">
//        boolean animate = false;
//        //if drag failed, restore the old dragged container in its original position
//        if (false && oldParent != null && !oldDraggedContVisible) { //set to null if drop succeeded, so !=null means failed, so we reinsert the old container at its old position
////            oldParent.addComponent(oldPos, oldComp); //reinsert
//            oldComp.setHidden(false);
//            oldParent = null;
//            oldComp = null;
//            oldPos = -1;
//            oldDraggedContVisible = true;
//            animate = true;
//        }
//</editor-fold>
        //if there is still a dropPlaceholder inserted, remove it
        if (dropPlaceholder != null) {
            if (dropPlaceholder.getParent() != null) {//remove the old placeholder ( not done in successful drop)
                Log.p("MyDragAndD.dragFinished: removing dropPlaceholder=" + dropPlaceholder.getName() + " from parent=" + dropPlaceholder.getParent().getName());
                dropPlaceholder.getParent().removeComponent(dropPlaceholder); //remove the old placeholder ( not done in successful drop)
            } else {
                Log.p("MyDragAndD.dragFinished: no parent() for dropPlaceholder=" + dropPlaceholder.getName());
            }
            dropPlaceholder = null;
//            animate = true;
        }

//        if (!dropSucceeded) {
//            draggedOrgParent.addComponent(draggedOrgIndex, this); //drop failed, insert dragged component again
//        }
        lastDraggedOver = null;
        draggedHeight = -1;
        draggedWidth = -1;
        dragImage2 = null;
        setHidden(false); //unhide (set hidden in dragEnter/dragListener) //done in Component.dragFinishedImpl(int x, int y)
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && animate) {
//            getComponentForm().animateHierarchy(400);
//        }
//        refreshAfterDrop();
//        getComponentForm().animateLayout(300);
//</editor-fold>
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
