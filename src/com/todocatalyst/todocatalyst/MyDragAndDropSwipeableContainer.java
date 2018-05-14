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
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.Layout;
import com.parse4cn1.ParseObject;
import java.util.List;

/**
 top-level container used in list to support (activatable) drag and drop

 @author Thomas
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
    private Call dropActionCall = null; //function containing the drop action to execute on a normal drop
    private Call dropAsSubtaskActionCall = null; //function containing the drop action to execute when dropping as a subtask (right-hand side of screen)
    private Call dropAsSuperTaskActionCall = null; //function containing the drop action to execute when dropping as a super/meta-task  (left-hand side of screen)

    interface InsertComp {

//        void insert(Component dropPlaceholder);//, int index);
        void insert(Component dropPlaceholder);
//        void insert(Component dropPlaceholder, boolean asSubtask);
    }
    private InsertComp insertDropPlaceholder = null; //function containing the drop action to execute on a normal drop
    private InsertComp insertDropPlaceholderForSubtask = null; //function containing the drop action to execute on a normal drop
    private InsertComp insertDropPlaceholderForSupertask = null; //function containing the drop action to execute on a normal drop
    private int lastY = -1; //x value for last component this was dragged over

    public enum InsertAsType {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        NORMAL,
        SUBTASK,
        SUPERTASK
    };

    InsertAsType lastInsertAsType = null;

    private InsertAsType insertType(int x, Component dropTarget) {
//        int borderDropZoneWidthInPercent = 15;
        int borderDropZoneWidthInPercent = MyPrefs.dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask.getInt();
//        return x > this.getWidth() / 3 * 2;
//        int screenWidthPixelsPercent = Display.getInstance().getDisplayWidth() / 100;
        int dropTargetWidthPixelsPercent = dropTarget.getWidth() / 100; //works for both portrait and landscape?!
        if (x <= dropTargetWidthPixelsPercent * borderDropZoneWidthInPercent) { //position in left 15%
            return InsertAsType.SUPERTASK;
        }
        if (x >= dropTargetWidthPixelsPercent * (100 - borderDropZoneWidthInPercent)) { //position in right 15%
            return InsertAsType.SUBTASK;
        } else {
            return InsertAsType.NORMAL; //position in middle 70%
        }
    }

    /**
    if x position has changed into a new zone where the dragged task will be inserted differently, then move the dropPlaceholder to the corresponding position (if defined)
    @param newXPos 
     */
//    private void refreshDropPlaceholderContainer(int newXPos) {
    private void refreshDropPlaceholderContainer(InsertAsType oldInsertType, InsertAsType newInsertType) {
//        InsertAsType newInsertAsType = insertType(newXPos);
        if (newInsertType != oldInsertType) {
            if (newInsertType == InsertAsType.NORMAL && insertDropPlaceholder != null) {
                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
                insertDropPlaceholder.insert(dropPlaceholder);
            } else if (newInsertType == InsertAsType.SUBTASK && insertDropPlaceholderForSubtask != null) {
                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
                insertDropPlaceholderForSubtask.insert(dropPlaceholder);
            } else if (newInsertType == InsertAsType.SUPERTASK && insertDropPlaceholderForSupertask != null) {
                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
                insertDropPlaceholderForSupertask.insert(dropPlaceholder);
            }
        }
    }

    /**
     move between ItemLists or Items (projects)

     @param oldList
     @param newList
     @param itemOrItemList
     @param newPos
     */
    private void moveItemOrItemListAndSave(ItemAndListCommonInterface oldList, ItemAndListCommonInterface newList, ItemAndListCommonInterface itemOrItemList, int newPos) {
//<editor-fold defaultstate="collapsed" desc="comment">
//Container draggedParent;
//        MyDragAndDropSwipeableContainer dragged = null;
//        int indexAdjustment = 0;
//        if (dragged != null && dragged.getParent() == newList) {
//            int draggedIndex = dragged.getParent().getComponentIndex(dragged);
//            if (newPos > draggedIndex) {
//                indexAdjustment = 1; //need to
//            }
//        }
//</editor-fold>
        if (oldList == newList) {
            if (newPos > oldList.getItemIndex(itemOrItemList)) {
                oldList.removeFromList(itemOrItemList);
                newList.addToList(newPos - 1, itemOrItemList);
            } else { //newPos <= oldList.getItemIndex(itemOrItemList)
                oldList.removeFromList(itemOrItemList);
                newList.addToList(newPos, itemOrItemList);
            }
//            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) itemOrItemList);
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList); //no need to save itemOrItemList since owner is the same
        } else {
            oldList.removeFromList(itemOrItemList);
            newList.addToList(newPos, itemOrItemList);
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldList, (ParseObject) newList, (ParseObject) itemOrItemList);
        }
    }

    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newList, ItemAndListCommonInterface itemOrItemList, int newPos) {
        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newList, itemOrItemList, newPos);
    }

    /**
    insert at *end of* newList
    @param newList
    @param itemOrItemList 
     */
    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newList, ItemAndListCommonInterface itemOrItemList) {
        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newList, itemOrItemList, Integer.MAX_VALUE);
    }

    /**
     move the position of Category within CategoryList

     @param categoryList
     @param newList
     @param category
     @param newPos
     */
    private void moveCategoryAndSave(ItemAndListCommonInterface categoryList, Category category, int newPos) {
        if (newPos > categoryList.getItemIndex(category)) {
            categoryList.removeFromList(category);
            categoryList.addToList(newPos - 1, category);
        } else {
            categoryList.removeFromList(category);
            categoryList.addToList(newPos, category);
        }
        DAO.getInstance().saveInBackgroundSequential((ParseObject) categoryList, (ParseObject) category);
    }

    private void moveItemBetweenCategoriesAndSave(Category oldCategory, Category newCategory, Item item, int newPos) {
        if (oldCategory == newCategory) {
            if (newPos > oldCategory.getItemIndex(item)) {
                oldCategory.removeItemFromCategory(item, false); //false: keep the position of the category in the item's list of categories
                newCategory.addItemToCategory(item, newPos - 1, false); //false: keep the position of the category in the item's list of categories
            } else {
                oldCategory.removeItemFromCategory(item, true);
                newCategory.addItemToCategory(item, newPos, true);
            }
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldCategory, (ParseObject) item); //only save list once
        } else {
            oldCategory.removeItemFromCategory(item, true);
            newCategory.addItemToCategory(item, newPos, true);
            DAO.getInstance().saveInBackgroundSequential((ParseObject) oldCategory, (ParseObject) newCategory, (ParseObject) item);
        }
    }

    /**
    insert the dropPlaceholder into the parent container of refComp at the right relative position wrt refComp
    @param refComp the component relative to which the dropPlaceholder should be inserted
    @param dropPh dropPlaceholder component
    @param relativeIndex  relative position where to insert dropPh, +1: after refComp, 0: at refComp's position, 
    Integer.MAX_VALUE: at the end of the container list to which refComp belongs, -1: before refComp (not used?!)
     */
    private static boolean addDropPlaceholderToAppropriateParentCont(Component refComp, Component dropPh, int relativeIndex) {
        ASSERT.that(!(refComp instanceof MyTree2) && !(refComp instanceof ContainerScrollY));
        Container dropCont = refComp.getParent(); //treeList = the list in which to insert the dropPlaceholder
        Component refCompComp = refComp; //the containing container of refComp contained in dropCont
        while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
            refCompComp = dropCont;
            dropCont = dropCont.getParent();
        }
        ASSERT.that(dropCont == null || (dropCont instanceof ContainerScrollY), "dropCont not correct type, dropCont=" + dropCont != null ? dropCont.toString() : "<null>");
        if (dropCont != null) {
//            return (Container) dropCont;
//            ( (Container) dropCont).addComponent(dropCont.getC, refComp);
            if (relativeIndex == Integer.MAX_VALUE) {
//                ((Container) dropCont).addComponent(((Container) dropCont).getComponentCount(), dropPh);
                dropCont.addComponent(dropPh);
            } else {
                dropCont.addComponent(dropCont.getComponentIndex(refCompComp) + relativeIndex, dropPh);
            }
            return true;
        } else {
            assert false;
            return false;
        }
    }

    private boolean movingUpwardsOrOverDragged(MyDragAndDropSwipeableContainer newDropTarget) {
//        boolean upwardsOrOverDragged = false;
        int newY = newDropTarget.getAbsoluteY();
        Log.p("dragDirection: newDropTarget.getAbsoluteY()=" + newDropTarget.getAbsoluteY() + ", getY=" + newDropTarget.getY() + " getScrollY=" + newDropTarget.getScrollY());
        Log.p("dragDirection: lastY=" + lastY + ", newY=" + newY + " over=" + newDropTarget.getName());
        if (lastY < 0 || newY <= lastY) {
            if (lastY != newY) {
                Log.p("dragDirection: Now dragging UP");
            }
            lastY = newY;
            return true;
        } else {
            if (lastY != newY) {
                Log.p("dragDirection: Now dragging DOWN");
            }
            lastY = newY;
            return false;
        }
    }

    private boolean movingUpwardsOrOverDraggedXXX(MyDragAndDropSwipeableContainer over) {
//        boolean upwardsOrOverDragged = false;
        Log.p("dragDirection: lastX=" + lastY + ", over.x=" + over.getX() + " over=" + over.getName());
        if (lastY < 0 || over.getX() <= lastY) {
            if (lastY != over.lastY) {
                Log.p("Now dragging UP");
            }
            lastY = over.lastY;
            return true;
        } else {
            if (lastY != over.lastY) {
                Log.p("Now dragging DOWN");
            }
            lastY = over.lastY;
            return false;
        }
    }

    /**
    get the MyDragAndDropSwipeableContainer of the parent task of the subtask contained in cont. 
    This method serves to encapsulate the structure of the expanded subtask containers
    @param cont
    @return or null if none found
     */
    private MyDragAndDropSwipeableContainer getParentMyDDCont(MyDragAndDropSwipeableContainer cont) {
//        Component beforeParent = cont;
        Container beforeParentParent = cont.getParent(); //treeList = the list in which to insert the dropPlaceholder

        Component north = null;
        Layout layout = null;
//        while (beforeParentParent != null) {
        //iterate up the container hierarchy to find BorderLayout container with a MyDragAndDropSwipeableContainer in the North position
        while (beforeParentParent != null
                && !((layout = beforeParentParent.getLayout()) instanceof BorderLayout
                && (north = ((BorderLayout) layout).getNorth()) instanceof MyDragAndDropSwipeableContainer)) {
//                beforeParent = beforeParentParent;
            beforeParentParent = beforeParentParent.getParent();
        }
//        }
        if (north instanceof MyDragAndDropSwipeableContainer) { //north could contain some other container than MyDragAndDropSwipeableContainer
            return (MyDragAndDropSwipeableContainer) north;
        } else {
            return null;
        }
    }

    /**
    Get the container with the expanded sub-elements. This method serves to encapsulate the structure of the expanded subtask containers
    
    @param myDDCont
    @return null if none
     */
    private ContainerScrollY getParentScrollYContainer(MyDragAndDropSwipeableContainer myDDCont) {
        Container draggedParent = myDDCont.getParent();
//        while (draggedParent != null) {
        //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
        while (draggedParent != null && !(draggedParent instanceof ContainerScrollY)) {
            draggedParent = draggedParent.getParent();
        }
//        }
        return (ContainerScrollY) draggedParent; //here either draggedParent is a ContainerScrollY or null
    }

    /**
    returns true if the two containers are (different) siblings, meaning they belong to the same ScrollYContainer, 
    false they're the same container or either is null
    @param myDDCont1
    @param myDDCont2
    @return 
     */
    private boolean isSibling(MyDragAndDropSwipeableContainer myDDCont1, MyDragAndDropSwipeableContainer myDDCont2) {
//        return myDDCont1.getParent().getParent() == myDDCont2.getParent().getParent();
        if (myDDCont1 != myDDCont2 || myDDCont1 == null) {
            return getParentScrollYContainer(myDDCont1) == getParentScrollYContainer(myDDCont2);
        } else {
            return false;
        }
    }

    /**
    find the first sibling in the list which is before the ref container. A dropped item is added *after* the returned container. 
    @param dragged
    @return null if none
     */
    private MyDragAndDropSwipeableContainer findSiblingUpwardsInHierarchy(MyDragAndDropSwipeableContainer dragged, MyDragAndDropSwipeableContainer before) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container draggedParent = dragged.getParent();
//        while (draggedParent != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//            while (!(draggedParent instanceof ContainerScrollY) && draggedParent != null) {
//                draggedParent = draggedParent.getParent();
//            }
//        }
//        ContainerScrollY parentScrollableY = (ContainerScrollY) draggedParent;
//        ContainerScrollY parentScrollableY = getParentScrollYContainer(dragged);
////        ContainerScrollY beforeParentScrollableY = getParentScrollYContainer(before);
//        Component beforeParent = before;
//        Component beforeParentScrollableY = getParentScrollYContainer(before); //treeList = the list in which to insert the dropPlaceholder
//
//        //special case: if the component we start with is the sibling return it directly
//        if (beforeParentScrollableY == parentScrollableY && beforeParentScrollableY != null) {
//            return before;
//        }
//</editor-fold>
        MyDragAndDropSwipeableContainer sibling = before;
//<editor-fold defaultstate="collapsed" desc="comment">
//        ContainerScrollY siblingScrollableY = getParentScrollYContainer(sibling); //treeList = the list in which to insert the dropPlaceholder

//        if (siblingScrollableY == parentScrollableY && siblingScrollableY != null) {
//            return sibling;
//        }
//        while (siblingScrollableY != null) {
//</editor-fold>
        while (sibling != null) {
            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//             if (siblingScrollableY == parentScrollableY && siblingScrollableY != null) {
            if (isSibling(dragged, sibling)) {
                return sibling;
            }
            sibling = getParentMyDDCont(sibling);
        }
        return null;
    }

    private ContainerScrollY getScrollYContainerWithSubtasks(MyDragAndDropSwipeableContainer cont) {
        Layout layout;
        Component center;
        Container contParent = cont.getParent();

        if (contParent != null && (layout = contParent.getLayout()) instanceof BorderLayout
                && (center = ((BorderLayout) layout).getCenter()) instanceof ContainerScrollY
                && (((ContainerScrollY) center)).getComponentCount() > (0 + getPositionInContainerScrollY(((ContainerScrollY) center), this) >= 0 ? 1 : 0)) {
            //if dragged hidden container is the only one in center, then there must be at least 2 elements in container to return it, this is ensured by this expression: getPositionInContainerScrollY(((ContainerScrollY) center), this) >= 0 ? 1 : 0)
            return (ContainerScrollY) center;
        }
        return null;
    }

    /**
    get the MyDragAndDropSwipeableContainer from a container coming from the ContainerScrollY container
    @param cont
    @return 
     */
    private MyDragAndDropSwipeableContainer getTaskContainer(Container cont) {
        Layout layout;
        Component north;
//        if (cont != null && (layout = cont.getParent().getLayout()) instanceof BorderLayout
        if (cont != null && (layout = cont.getLayout()) instanceof BorderLayout
                && (north = ((BorderLayout) layout).getNorth()) instanceof MyDragAndDropSwipeableContainer) {
            return (MyDragAndDropSwipeableContainer) north;
        }
        return null;
    }

//    private int indexForHiddenDraggedCont(ContainerScrollY scrollYContainer, MyDragAndDropSwipeableContainer cont) {
//        return scrollYContainer.getComponentIndex(cont.getParent());
//
//    }
    private int getPositionInContainerScrollY(ContainerScrollY scrollYContainerWithSubtask, MyDragAndDropSwipeableContainer eltCont) {
        return scrollYContainerWithSubtask.getComponentIndex(eltCont.getParent());
    }

    /**
    find and return the *last* MyDragAndDropSwipeableContainer in the hierarchy of expanded subtasks inside/below cont
    @param cont (can be null)
    @return cont itself if there are no expanded subtasks under it
     */
    private MyDragAndDropSwipeableContainer findLastDDContainer(MyDragAndDropSwipeableContainer cont) {
        ContainerScrollY scrollYContainerWithSubtask = getScrollYContainerWithSubtasks(cont);
        int indexOfHiddenDraggedCont = getPositionInContainerScrollY(scrollYContainerWithSubtask, this); //this==dragged
        if (scrollYContainerWithSubtask != null) {
            int compCount = scrollYContainerWithSubtask.getComponentCount();
            int adjIndex = indexOfHiddenDraggedCont == compCount - 1 ? compCount - 2 : compCount - 1; //if the last container is the dragged hidden one, take the one just before
//            return findLastDDContainer(getTaskContainer(scrollYContainerWithSubtask.getComponentAt(scrollYContainerWithSubtask.getComponentCount()-1)));
            Component res = scrollYContainerWithSubtask.getComponentAt(adjIndex);
            if (res instanceof Container) {
                return findLastDDContainer(getTaskContainer((Container) res));
            }
        }
        return cont;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (cont.getLayout() instanceof BorderLayout) {
//            if (((BorderLayout) cont.getLayout()).getCenter() instanceof ContainerScrollY
//                    && ((Container) ((BorderLayout) cont.getLayout()).getCenter()).getComponentCount() > 0) { //if no-empty list
//                Container centerCont = (Container) ((BorderLayout) cont.getLayout()).getCenter();
//                return findLastDDContainer((Container) centerCont.getComponentAt(centerCont.getComponentCount() - 1)); //return last container of last element
//            } else if (((BorderLayout) cont.getLayout()).getNorth() instanceof MyDragAndDropSwipeableContainer) { //if no list of expanded subtasks, return task itself
//                return (MyDragAndDropSwipeableContainer) ((BorderLayout) cont.getLayout()).getNorth();
//            }
//        }

//        else if (cont instanceof ContainerScrollY) {
//            return findLastDDContainer((Container) cont.getComponentAt(cont.getComponentCount() - 1)); //return the last element
//        }
//        return null;
//</editor-fold>
    }

    private MyDragAndDropSwipeableContainer findPrecedingDDCont(MyDragAndDropSwipeableContainer cont) {
        //find a preceding sibling if any
//        MyDragAndDropSwipeableContainer precedingSibling = findSiblingUpwardsInHierarchy(cont, cont);
        ContainerScrollY parentScrollYContainer = getParentScrollYContainer(cont);
        //Examples of lists with (H)idden element: H T1 T2: preceding(T1)=null (a), preceding(T2)=T1 (b); T1 H T2: preceding(T2)=T1 (c)
        //                                        idx(H)=0            idx=1
//        if (parentScrollYContainer != null && (index = getPositionInContainerScrollY(parentScrollYContainer, cont)) > 0) {
        int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYContainer, this); //this==dragged
        int index = -1;
        if (parentScrollYContainer != null) {
            index = getPositionInContainerScrollY(parentScrollYContainer, cont);
            if (index == indexOfHiddenDraggedCont) { //if index points to the hidden container, take the one before the hidden as in case (b) and (c) (or index becomes negative in case (a) above)
                index = index - 1 - 1;
            } else {
                index = index - 1;
            }
        }
        if (index >= 0) {
            //if it has an earlier subelements, find and return the preceding one
//            Component c = parentScrollYContainer.getComponentAt(index - 1);
            Component c = parentScrollYContainer.getComponentAt(index);
//            if (c instanceof Container) {
            return findLastDDContainer(getTaskContainer((Container) c)); //return the very last element (eg last expanded subtask at deepest level of expansion)
//            }
        }
        //if there are no preceding items in the same list, the preceding element is necessarily the expanded parent task
        return getParentMyDDCont(cont);
//<editor-fold defaultstate="collapsed" desc="comment">
//        dropTargetTopLevelParent = cont;
//        Container dropCont = dropTargetTopLevelParent.getParent();
//        while (dropCont != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent();
//            }
//
//            if (dropCont != null && dropCont.getComponentIndex(dropTargetTopLevelParent) > 0) { //>0 means there's a previous element in the list
//                Container previous = (Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) - 1);
//                return findLastDDContainer(previous);
//            }
//            if (dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//            }
//        }
//        return null;
//</editor-fold>
    }

    /**
    returns the first MyDD after comp, or null if none. The next element can be either the first expanded subtask, 
    the next sibling task, or (complex case) the next sibling of one of its parents up the hierarchy
    @param comp
    @return 
     */
    private MyDragAndDropSwipeableContainer findNextDDCont(MyDragAndDropSwipeableContainer comp) {
        //first check if there are expanded subtasks then return the first one
        int index;
        int indexOfHiddenDraggedCont;
        int adjIndex;

        ContainerScrollY scrollYContainerWithSubtask = getScrollYContainerWithSubtasks(comp);
        indexOfHiddenDraggedCont = getPositionInContainerScrollY(scrollYContainerWithSubtask, this); //this==dragged
        adjIndex = indexOfHiddenDraggedCont != 0 ? 1 : 0;
        if (scrollYContainerWithSubtask != null && scrollYContainerWithSubtask.getComponentCount() > 0 + adjIndex) { //must container at least 2 elements if the hidden is one of them
            //if it has expanded subtasks, rturn the first (unless it is the hidden one)
            adjIndex = indexOfHiddenDraggedCont == 0 ? 1 : 0; //if hidden is first element, take the one after
            Container c = (Container) scrollYContainerWithSubtask.getComponentAt(0 + adjIndex); //get first element in the list
            return getTaskContainer(c);
        } else {
            //if there is a following sibling, return it
            ContainerScrollY parentScrollYContainer = getParentScrollYContainer(comp);
            if (parentScrollYContainer != null) {
                //Examples of lists with (H)idden element: T1 H T2: next(T1)=T2 (a); T1 T2 H: next(T2)=null (c)
                indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYContainer, this); //this==dragged
                index = getPositionInContainerScrollY(parentScrollYContainer, comp);
                adjIndex = index + 1 + (index + 1 == indexOfHiddenDraggedCont ? 1 : 0); //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//            if (parentScrollYContainer != null
//                    && (index = getPositionInContainerScrollY(parentScrollYContainer, comp)) < parentScrollYContainer.getComponentCount() - 1) {
                if (adjIndex <= parentScrollYContainer.getComponentCount() - 1) {
//                    Container c = (Container) parentScrollYContainer.getComponentAt(index + 1); //get next element in the list
                    Container c = (Container) parentScrollYContainer.getComponentAt(adjIndex); //get next element in the list
                    return getTaskContainer(c); //return the element
                }
            }

            //if no expanded subtask, and no following sibling, then iterate up the hierarchy to find a task that follows
            //this is a really tricky case!!!
            // example: T1 -> S1, S2 -> S21, S22, S3, T2
            // T1
            //   S1
            //   S2
            //     S21
            //     S22
            // T2
            // next(S22)==T2
            // 
            // T1
            //   S1
            //   S2
            //     S21
            //     S22
            //   S3
            // T2
            // next(S22)==S3, next(S3)==T2
            MyDragAndDropSwipeableContainer parentComp = comp;
            ContainerScrollY parentScrollYCont = getParentScrollYContainer(parentComp);
//            ContainerScrollY contY=getParentScrollYContainer(comp);
            while (parentScrollYCont != null) {
                indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYCont, this); //this==dragged
                index = getPositionInContainerScrollY(parentScrollYCont, comp);
                adjIndex = index + 1 + (index + 1 == indexOfHiddenDraggedCont ? 1 : 0); //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//                if ((index = getPositionInContainerScrollY(parentScrollYCont, parentComp)) < parentScrollYCont.getComponentCount() - 1) {
                if (adjIndex <= parentScrollYCont.getComponentCount() - 1) {
//                    return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex + 1));
                    return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex));
                }
                parentComp = getParentMyDDCont(parentComp);
                parentScrollYCont = getParentScrollYContainer(parentComp);
            }
        }
        return null;
//<editor-fold defaultstate="collapsed" desc="comment">
//            //if parent has a non-empty center container (with expanded subtasks)
//            if (dropCont.getLayout() instanceof BorderLayout //&& ((BorderLayout) dropCont.getLayout()).getCenter() != null
//                    && (((BorderLayout) dropCont.getLayout()).getCenter() instanceof ContainerScrollY)
//                    && ((Container) ((BorderLayout) dropCont.getLayout()).getCenter()).getComponentCount() > 0) {
//                //expanded subtasks are always in the Center container of a BorderLayout
//                return findFirstDDContainer((Container) ((Container) ((BorderLayout) dropCont.getLayout()).getCenter()).getComponentAt(0)); //find MyDD in the *first* element in the list
//            } else {
//                while (dropCont != null) {
//                    //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//                    while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                        parentComp = dropCont;
//                        dropCont = parentComp.getParent();
//                    }
//
//                    if (dropCont != null && dropCont.getComponentIndex(parentComp) + 1 < dropCont.getComponentCount()) { //if there's another element in the list after the current one
////                    return findDropTargetIn((Container) dropCont.getComponentAt(0));
////                    return findDropTargetIn((Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) + 1));
////                    Container nextCont = ((Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) + 1));
//                        //return first MyDD
//                        return findFirstDDContainer((Container) dropCont.getComponentAt(dropCont.getComponentIndex(parentComp) + 1));
//                    }
//                    if (dropCont != null) {
//                        parentComp = dropCont;
//                        dropCont = parentComp.getParent(); //treeList = the list in which to insert the dropPlaceholder
//                    }
//                }
//            }
//            return null;
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyDragAndDropSwipeableContainer findPreviousDDContainerOLDXXX(MyDragAndDropSwipeableContainer cont) {
//        Component dropTargetTopLevelParent = cont;
//        Container dropCont = dropTargetTopLevelParent.getParent();
//        while (dropCont != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent();
//            }
//
//            if (dropCont != null && dropCont.getComponentIndex(dropTargetTopLevelParent) > 0) { //>0 means there's a previous element in the list
//                Container previous = (Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) - 1);
//                return findPreviousDDContainerOLDXXX((MyDragAndDropSwipeableContainer) previous);
//            }
//            if (dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//            }
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyDragAndDropSwipeableContainer findFirstDDContainerXXX(MyDragAndDropSwipeableContainer cont) {
//        if (cont.getLayout() instanceof BorderLayout && ((BorderLayout) cont.getLayout()).getNorth() instanceof MyDragAndDropSwipeableContainer) { //if no list of expanded subtasks, return task itself
//            return (MyDragAndDropSwipeableContainer) ((BorderLayout) cont.getLayout()).getNorth();
//        } else if (cont instanceof Container && ((Container) cont).getComponentCount() > 0) {
//            return findFirstDDContainer((MyDragAndDropSwipeableContainer) cont.getComponentAt(0)); //return the last element
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    returns true of cont is the last element in an (expanded) list of elements
//    @param cont
//    @return
//     */
//    private boolean isNotLastElementXXX(MyDragAndDropSwipeableContainer cont) {
//        Component dropTargetTopLevelParent = cont;
//        Container dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//
//        while (dropCont != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent();
//            }
//            if (dropCont != null) {
//                int index = dropCont.getComponentIndex(dropTargetTopLevelParent);
//                int size = dropCont.getComponentCount();
//                return index + 1 < size;
//            }
//        }
//        assert false : "should never happen";
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    when a MyDragAndDropSwipeableContainer is embedded in another container, go through this container's children and find and return the MyDragAndDropSwipeableContainer
//    @param cont
//    @return
//     */
//    private static MyDragAndDropSwipeableContainer findDropTargetInXXX(Container cont) {
//
//        int count = cont.getComponentCount();
//        for (int i = count - 1; i >= 0; i--) {
//            Component cmp = cont.getComponentAt(i);
////            if (cmp.isDropTarget()) {
//            if (cmp instanceof MyDragAndDropSwipeableContainer) {
//                return (MyDragAndDropSwipeableContainer) cmp;
//            } else if (cmp instanceof Container) {
//                return findDropTargetInXXX((Container) cmp);
////                MyDragAndDropSwipeableContainer component = findDropTargetIn((Container) cmp);
////                if (component != null) {
////                    return component;
////                }
//            }
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    return a sibling to draggedItem that (recursively) owns beforeItem. Used when dragging elements within an expanded sublist.
//    @param draggedItem
//    @param beforeItem
//    @return
//     */
//    private static ItemAndListCommonInterface findPrecedingSiblingXXX(ItemAndListCommonInterface draggedItem, ItemAndListCommonInterface beforeItem) {
//        ItemAndListCommonInterface draggedItemOwner = draggedItem.getOwner();
//        ItemAndListCommonInterface precedingSibling = beforeItem;
//        ItemAndListCommonInterface topLevelOwner = null;
//        do {
//            if (precedingSibling.getOwner() == draggedItemOwner) {
//                return precedingSibling;
//            }
//            topLevelOwner = precedingSibling;
//            precedingSibling = precedingSibling.getOwner();
//        } while (precedingSibling != null);
//        return topLevelOwner;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    returns the MyTree2 or ContainerScrollY which contains the list of elements.
//    @param comp
//    @return
//     */
//    private Container findParentContForDropPlaceholderXXX(MyDragAndDropSwipeableContainer comp) {
////        if (comp instanceof MyTree2) {
////            return (Container) comp;
////        } else {
//        Container dropCont = comp.getParent(); //treeList = the list in which to insert the dropPlaceholder
//        Component dropTargetTopLevelParent = comp;
//        while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//            dropTargetTopLevelParent = dropCont;
//            dropCont = dropCont.getParent();
//        }
//        return (Container) dropCont;
////        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    get the 'absolute' index of comp, that is, the number visible container on the screen including expanded subtasks etc.
//    @param comp
//    @return index starting with 0 for very first container in list, -1 if
//     */
//    private int getAbsoluteIndexXXX(MyDragAndDropSwipeableContainer comp) {
//        int absoluteIndex = -1;
//        Container dropCont = comp.getParent(); //treeList = the list in which to insert the dropPlaceholder
//        Component dropTargetTopLevelParent = comp;
//        while (dropCont != null) {
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropCont.getParent();
//            }
//            if (dropCont != null) {
//                if (absoluteIndex == -1) {
//                    absoluteIndex = 0;
//                }
//                absoluteIndex = absoluteIndex + dropCont.getComponentIndex(dropTargetTopLevelParent) + 1;
//            }
//        }
//        return absoluteIndex;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    find 'before' or 'after' container.
//
//    @param comp
//    @param getContainerBeforeComp if true, will return the container 'before' comp, otherwise the one after
//    @return
//     */
//    private MyDragAndDropSwipeableContainer findContXXXX(MyDragAndDropSwipeableContainer comp, boolean getContainerBeforeComp) {
//        if (getContainerBeforeComp) {
//            return findPrecedingDDCont(comp);
//        } else {
//            return findNextDDCont(comp);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyDragAndDropSwipeableContainer findContXXX(MyDragAndDropSwipeableContainer comp, boolean getContainerBeforeComp) {
//        
//        Component dropTargetTopLevelParent = comp;
//        Container dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//        
//        while (dropCont != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent();
//            }
//            
//            if (dropCont != null) {
//                if (getContainerBeforeComp) {
//                    if (dropCont.getComponentIndex(dropTargetTopLevelParent) > 0) {
////                        return findDropTargetIn((Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) - 1));
//return findLastDDContainer((Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) - 1));
//                    } //else {                        return null;                    }
//                } else { //getAfter
//                    if (dropCont.getComponentIndex(dropTargetTopLevelParent) < dropCont.getComponentCount() - 1) {
//                        return findDropTargetInXXX((Container) dropCont.getComponentAt(dropCont.getComponentIndex(dropTargetTopLevelParent) + 1));
//                    } //else {                        return null;                    }
//                }
//            }
//            dropTargetTopLevelParent = dropCont;
//            dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
////<editor-fold defaultstate="collapsed" desc="comment">
////
////            if (treeList != null) {
////                MyDragAndDropSwipeableContainer beforeMyDDCont; //top-level container in treelist, *before* the dropPlaceholder (null if over first element in list)
////                MyDragAndDropSwipeableContainer afterMyDDCont; //top-level container in treelist *after* the dropPlaceholder (null if over last element in list)
////
////                oldDropPlaceholderIndex = dragged.dropPlaceholder != null && dragged.dropPlaceholder.getParent() != null
////                        ? dragged.dropPlaceholder.getParent().getComponentIndex(dragged.dropPlaceholder) //assumes that dropPlaceholder is inserted into treeList!!
////                        : treeList.getComponentIndex(dropTargetTopLevelParent);
////
//////                int index = parent.getComponentIndex(this); //get my current position
//////                int index = parent.getComponentIndex(dropTarget); //get my current position
//////                        int index = treeList.getComponentIndex(dropParent); //get my current position
////                int dropPlaceholderInsertionIndex = treeList.getComponentIndex(dropTargetTopLevelParent); //get my current position
////                boolean draggingUpwardsOrOverInitialDraggedEltPosition = dropPlaceholderInsertionIndex <= oldDropPlaceholderIndex; //NB. Also works for dropPlaceholder 'under' dragged when initiating drag
//////                boolean draggingDownwards = !draggingUpwardsOrOverInitialDraggedEltPosition;
////                if (draggingUpwardsOrOverInitialDraggedEltPosition) {
////                    beforeMyDDCont
////                            = dropPlaceholderInsertionIndex == 0 ? null : findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex - 1));
////                    afterMyDDCont
////                            = (MyDragAndDropSwipeableContainer) dropTarget;
////                } else { //dragging downwards
////                    beforeMyDDCont
////                            = (MyDragAndDropSwipeableContainer) dropTarget;
////                    afterMyDDCont
////                            //                            = dropPlaceholderInsertionIndex >= treeList.getComponentCount() ? null : findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex + 1));
////                            = dropPlaceholderInsertionIndex < treeList.getComponentCount() ? findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex)) : null;
////                }
////            }
////</editor-fold>
//        }
//        return null;  //null <=> no container found before/after comp
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean insertIntoPreviousWithSameOwnerXXX(Container cont, MyDragAndDropSwipeableContainer dragged) {
//        ItemAndListCommonInterface draggedItem = dragged.getDragAndDropObject();
//        ItemAndListCommonInterface draggedOwner = draggedItem.getOwner();
//        if (cont.getLayout() instanceof BorderLayout) {
//            Component north = ((BorderLayout) cont.getLayout()).getNorth();
//            Container center = (Container) ((BorderLayout) cont.getLayout()).getCenter();
//            if (north instanceof MyDragAndDropSwipeableContainer
//                    && ((MyDragAndDropSwipeableContainer) north).getDragAndDropObject().getOwner() == draggedOwner) { //if same owner, insert after this one
//                ItemAndListCommonInterface dropTargetItem = ((MyDragAndDropSwipeableContainer) north).getDragAndDropObject();
//                int index = draggedOwner.getItemIndex(dropTargetItem) + 1; //+1: insert *after* the found item
//                moveItemOrItemListAndSave(draggedOwner, draggedItem, index);
//                return true;
//            } else if (center instanceof ContainerScrollY && ((Container) center).getComponentCount() > 0) { //if non-empty sublist
//                return insertIntoPreviousWithSameOwnerXXX((Container) center.getComponentAt(center.getComponentCount() - 1), dragged); //return last container of last element
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private MyDragAndDropSwipeableContainer getPreviousWithSameOwnerXXX(Container cont, MyDragAndDropSwipeableContainer dragged) {
//        ItemAndListCommonInterface draggedItem = dragged.getDragAndDropObject();
//        ItemAndListCommonInterface draggedOwner = draggedItem.getOwner();
//
//        if (cont.getLayout() instanceof BorderLayout) {
//            Component north = ((BorderLayout) cont.getLayout()).getNorth();
//            Container center = (Container) ((BorderLayout) cont.getLayout()).getCenter();
//            if (north instanceof MyDragAndDropSwipeableContainer
//                    && ((MyDragAndDropSwipeableContainer) north).getDragAndDropObject().getOwner() == draggedOwner) { //if same owner, insert after this one
//                ItemAndListCommonInterface dropTargetItem = ((MyDragAndDropSwipeableContainer) north).getDragAndDropObject();
//                int index = draggedOwner.getItemIndex(dropTargetItem) + 1; //+1: insert *after* the found item
//                moveItemOrItemListAndSave(draggedOwner, draggedItem, index);
//                return true;
//            } else if (center instanceof ContainerScrollY && ((Container) center).getComponentCount() > 0) { //if non-empty sublist
//                return insertIntoPreviousWithSameOwner((Container) center.getComponentAt(center.getComponentCount() - 1), dragged); //return last container of last element
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    if dragged belongs to the hierarchy of the element *before* dropDD, then insert at under the common owner at the end of its list
//    @param dropDD
//    @param dragged
//    @return true if inserted, false otherwise
//     */
//    private void insertBeforeXXX(MyDragAndDropSwipeableContainer dropDD, MyDragAndDropSwipeableContainer dragged) {
//        Component dropTargetTopLevelParent = dropDD;
//        Container dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//
//        while (dropCont != null) {
//            //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//            while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent();
//            }
//            if (dropCont != null) {
//                int idx = dropCont.getComponentIndex(dropTargetTopLevelParent);
//                if (idx > 0) { //if dropTarget is NOT first element in the list (idx>0)
//                    Container prevCont = (Container) dropCont.getComponentAt(idx - 1); //get previous element
////                    return insertIntoPreviousWithSameOwner(prevCont, dragged); //try to insert
//                    if (insertIntoPreviousWithSameOwnerXXX(prevCont, dragged)) { //try to insert
//                        return;
//                    }
//                }
//            }
//        }
//        //else no previous so intert at top of list
//        Item dropTargetItem = (Item) dropDD.getDragAndDropObject();
//        ItemAndListCommonInterface newOwnerPrj = dropTargetItem.getOwner();
//        int insertIndex = newOwnerPrj.getItemIndex(dropTargetItem);
//        moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
//
////        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean insertIntoNextWithSameOwnerXXX(Container cont, MyDragAndDropSwipeableContainer dragged) {
//        ItemAndListCommonInterface draggedItem = dragged.getDragAndDropObject();
//        ItemAndListCommonInterface draggedOwner = draggedItem.getOwner();
//        if (cont.getLayout() instanceof BorderLayout) {
//            Component north = ((BorderLayout) cont.getLayout()).getNorth();
//            Container center = (Container) ((BorderLayout) cont.getLayout()).getCenter();
//            if (center instanceof ContainerScrollY && ((Container) center).getComponentCount() > 0) { //if subtasks expanded, insert at start of the list
//                dropActionCall = () -> {
//                    ItemAndListCommonInterface dropTargetItem = ((MyDragAndDropSwipeableContainer) north).getDragAndDropObject();
//                    moveItemOrItemListAndSave(dropTargetItem, draggedItem, 0);
//                };
//                insertDropPlaceholder = (dropPlaceholder) -> {
//                };
//                return true;
//            } else if (center instanceof ContainerScrollY && ((Container) center).getComponentCount() > 0) { //if non-empty sublist
//                return insertIntoPreviousWithSameOwnerXXX((Container) center.getComponentAt(center.getComponentCount() - 1), dragged); //return last container of last element
//            }
//        }
//        return false;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//    if dragged belongs to the hierarchy of the element *after* dropDD, then insert at under the common owner at the end of its list.
//    Different cases: (* => dropTarget)
//    1) dropTarget's subtasks are expanded, T1* -> S1, S2
//    2) dropTarget's subtasks are NOT expanded and dropTarget is either last element in a list or the next element is at the same level (same parent): T1*;T2 or T0;T1*
//    3) the next element is a higher level element T1 -> S1, S2 -> S21;S22*; T2
//    3)
//    @param dropDD
//    @param dragged
//    @return true if inserted, false otherwise
//     */
//    private void insertAfterXXX(MyDragAndDropSwipeableContainer dropDD, MyDragAndDropSwipeableContainer dragged) {
//        ItemAndListCommonInterface draggedElt = dragged.getDragAndDropObject();
//        ItemAndListCommonInterface dropTargetElt = dropDD.getDragAndDropObject();
//        Component dropTargetTopLevelParent = dropDD;
//        Container dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//
//        ContainerScrollY subtaskCont;
//        if ((subtaskCont = getSubtaskContainerFromTaskContainerXXX(dropDD)) != null) {
//            //if subtasks are expanded, insert at the top of the list
//            dropActionCall = () -> {
//                getTaskContainer((Container) subtaskCont.getComponentAt(0)).getDragAndDropObject().addToList(0, draggedElt);
//                moveItemOrItemListAndSave(draggedElt, draggedElt, CENTER);
//            };
//            insertDropPlaceholder = (dropPlaceholder) -> {
//                subtaskCont.addComponent(0, dropPlaceholder); //insert dropPlaceholder at top of list of expanded subtasks
//            };
//            return;
//        } else if (isNotLastElementXXX(this)) {
//            // insert *after* the dropTarget (and before the following element in the list)
//            dropActionCall = () -> {
//                ItemAndListCommonInterface newOwner = dropTargetElt.getOwner();
//                int insertIndex = newOwner.getItemIndex(dropTargetElt);
//                moveItemOrItemListAndSave(newOwner, draggedElt, insertIndex);
//            };
//            insertDropPlaceholder = (dropPlaceholder) -> {
//            };
//            return;
//        } else {
//            //insert into common owner
//            while (dropCont != null) {
//                //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
//                while (!(dropCont instanceof ContainerScrollY) && dropCont != null) {
//                    dropTargetTopLevelParent = dropCont;
//                    dropCont = dropTargetTopLevelParent.getParent();
//                }
//                if (dropCont != null) {
//                    final Container dropCont2 = dropCont;
//                    final Component dropTargetTopLevelParent2 = dropTargetTopLevelParent;
//                    dropActionCall = () -> {
//                        int idx = dropCont2.getComponentIndex(dropTargetTopLevelParent2);
//                        if (idx + 1 >= dropCont2.getComponentCount()) { //if dropTarget is the last element in the list (idx==size)
//                            Container prevCont = (Container) dropCont2.getComponentAt(idx - 1); //get previous element
//                            if (insertIntoNextWithSameOwnerXXX(prevCont, dragged)) {//try to insert
//                                return;
//                            }
//                        }
//                    };
//                    insertDropPlaceholder = (dropPlaceholder) -> {
//
//                    };
//                } else {
//                    //???
//                }
//            }
//        }
////        else
//        dropActionCall = () -> {
//            Item dropTargetItem = (Item) dropDD.getDragAndDropObject();
//            ItemAndListCommonInterface newOwnerPrj = dropTargetItem.getOwner();
//            int insertIndex = newOwnerPrj.getItemIndex(dropTargetItem) + 1;
//            moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
//        };
//        insertDropPlaceholder = (dropPlaceholder) -> {
//
//        };
//        return;//false;
//    }
//</editor-fold>
    private InsertAsType insertTypeXXX(int x) {
        return insertType(x, Display.getInstance().getCurrent());
    }

    /**
     returns true if dropped elements should be inserted *below* the item it
     is dropped on. Eg if dropped of right-hand third of the dropTarget
     container.

     @param x
     @return
     */
    private boolean insertBelowXXX(int x) {
//        return x > this.getWidth() / 3 * 2;
        return insertTypeXXX(x) == InsertAsType.SUBTASK;
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
            int x = e.getX();
            int y = e.getY();
//            Component dropTarget = this;

//            Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------");
//            MyDragAndDropSwipeableContainer dragged = null;
            if (false && !(drag instanceof MyDragAndDropSwipeableContainer)) {
//                Log.p("***addDragOverListener: drag (" + drag.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
                return;
            } //else {
            if (false && !(dropTarget instanceof MyDragAndDropSwipeableContainer)) {
//                Log.p("***addDragOverListener: dropTarget (" + dropTarget.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
                return;
            } //else {
            if (false) {
                e.consume();
            }

            //update drop position (NORMAL, SUBTASK, SUPERTASK):
            InsertAsType newInsertAsType = insertType(x, dropTarget);
            refreshDropPlaceholderContainer(lastInsertAsType, newInsertAsType);
            if (lastInsertAsType != newInsertAsType) {
//                Log.p("InsertAsType now = " + newInsertAsType+" (before="+(lastInsertAsType!=null?lastInsertAsType.toString().toLowerCase():"<null>")+")", Log.DEBUG);
                Log.p("InsertAsType now = " + newInsertAsType, Log.DEBUG);
                lastInsertAsType = newInsertAsType;
            }

            MyDragAndDropSwipeableContainer dragged = (MyDragAndDropSwipeableContainer) drag;
//            if (dragged == dropTarget || dragged.lastDraggedOver == dropTarget) { //over myself or same as on last dropActionCall to listener
            if (dropTarget == dragged.lastDraggedOver) { //over myself or same as on last dropActionCall to listener
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dragged.lastDraggedOver == MyDragAndDropSwipeableContainer.this) {
//                    e.consume();
//                Log.p("addDragOverListener: IGNORE DragOverListener, dropTarget == dragged.lastDraggedOver, dropTarget=" + dropTarget.getName() + ", dragged=" + dragged.getName());
////                    getComponentForm().revalidate();
//                Log.p("---------------- END MyDragAndDropSwipeableContainer() ------------------------------------");
//</editor-fold>
//                refreshDropPlaceholderContainer(x);
                return; //skip if still over the same 
            }
            if (!(dropTarget instanceof MyDragAndDropSwipeableContainer)) { //if we're over a dropTarget store that as lastDraggedOver
                Log.p("addDragOverListener: dragged.lastDraggedOver = " + dropTarget.getName(), Log.DEBUG);
                dragged.lastDraggedOver = dropTarget;
//                refreshDropPlaceholderContainer(x);
                return;
            } //else {
            //over a new container
            Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------", Log.DEBUG);
//            int oldDropPlaceholderIndex = -1; //used to keep track of whether we drag upwards or downwards (to insert new dropPlaceholder in right position)
            //first time we drag over another element than dragged:
            if (!dragged.isHidden()) { //dragged.draggedWidth == -1) {// && dragged!=dragged.lastDraggedOver) {
//                    oldDropPlaceholderIndex=dragged.getParent().getComponentIndex(dragged); //initialize oldIndex to position of dragged
                dragged.draggedWidth = dragged.getWidth();
                dragged.draggedHeight = dragged.getHeight();
                dragImage2 = getDragImage(); //save dragImage before hiding
                Log.p("dragging of " + dragged.getName() + " started (setHidden);  dropTarget= " + dropTarget.getName(), Log.DEBUG);
                dragged.setHidden(true); //only hide once we've dragged on top of another object than dragged. Once we have the Height/Width, hide the component
//                    dragged.draggedOrgParent = drag.getParent();
//                    dragged.draggedOrgIndex = dragged.draggedOrgParent.getComponentIndex(drag);
//                    dragged.draggedOrgParent.removeComponent(drag); //remove the dragged component
            }

            if (true || dragged.lastDraggedOver != dropTarget) { // implicitly: || dragged.lastDraggedOver==null 
                Log.p("addDragOverListener: new dropTarget = " + dropTarget.getName() + ", old dropTarget="
                        + dragged != null && dragged.lastDraggedOver != null ? dragged.lastDraggedOver.getName() : "<null>", Log.DEBUG);
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
//<editor-fold defaultstate="collapsed" desc="comment">
//Insert new dropPlaceholder:
//                Container parent = getParent().getParent();
//                Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
//                Component dropParent = dropTarget;
//need to set treeList *before* removing dragged from its parent:
//            Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
//            Component dropTargetTopLevelParent = dropTarget;
//            while (!(treeList instanceof MyTree2) && treeList != null) {
//                dropTargetTopLevelParent = treeList;
//                treeList = treeList.getParent();
//            }
//</editor-fold>
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
            //remove old dropPlaceholder (if any) - remove even if no new dropPosition since we don't want the old dropPlaceholder to hang as we drag further one
            //remove *before* finding the before/after MyDDConts
            if (dragged.dropPlaceholder != null) {
                dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder);
            }

            boolean draggingUpwardsOrOverInitialDraggedEltPosition;
//            if (treeList != null) {
            MyDragAndDropSwipeableContainer beforeMyDDCont; //top-level container in treelist, *before* the dropPlaceholder (null if over first element in list)
            MyDragAndDropSwipeableContainer afterMyDDCont; //top-level container in treelist *after* the dropPlaceholder (null if over last element in list)
//<editor-fold defaultstate="collapsed" desc="comment">
//                oldDropPlaceholderIndex = dragged.dropPlaceholder != null && dragged.dropPlaceholder.getParent() != null
//                        ? dragged.dropPlaceholder.getParent().getComponentIndex(dragged.dropPlaceholder) //assumes that dropPlaceholder is inserted into treeList!!
//                        : treeList.getComponentIndex(dropTargetTopLevelParent);
//
////                int index = parent.getComponentIndex(this); //get my current position
////                int index = parent.getComponentIndex(dropTarget); //get my current position
////                        int index = treeList.getComponentIndex(dropParent); //get my current position
//                int dropPlaceholderInsertionIndex = treeList.getComponentIndex(dropTargetTopLevelParent); //get my current position
//                draggingUpwardsOrOverInitialDraggedEltPosition = dropPlaceholderInsertionIndex <= oldDropPlaceholderIndex; //NB. Also works for dropPlaceholder 'under' dragged when initiating drag
////                boolean draggingDownwards = !draggingUpwardsOrOverInitialDraggedEltPosition;
//                if (draggingUpwardsOrOverInitialDraggedEltPosition) {
//                    beforeMyDDCont
//                            = dropPlaceholderInsertionIndex == 0 ? null : findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex - 1));
//                    afterMyDDCont
//                            = (MyDragAndDropSwipeableContainer) dropTarget;
//                } else { //dragging downwards
//                    beforeMyDDCont
//                            = (MyDragAndDropSwipeableContainer) dropTarget;
//                    afterMyDDCont
//                            //                            = dropPlaceholderInsertionIndex >= treeList.getComponentCount() ? null : findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex + 1));
//                            = dropPlaceholderInsertionIndex < treeList.getComponentCount() ? findDropTargetIn((Container) treeList.getComponentAt(dropPlaceholderInsertionIndex)) : null;
//                }
//                int addOneOnDownwardsDrag = (draggingUpwardsOrOverInitialDraggedEltPosition) ? 0 : 1; //adjust index by +1 when dragging downwards
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
/*
DOCUMENTATION OF WHERE INSERTS/DROPS ARE POSSIBLE
Cat1
Cat2
Cat3

Cat1
  T1
  T2
Cat2
T3
             */
//</editor-fold>
            draggingUpwardsOrOverInitialDraggedEltPosition = movingUpwardsOrOverDragged((MyDragAndDropSwipeableContainer) dropTarget);

            if (draggingUpwardsOrOverInitialDraggedEltPosition) {
                afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                beforeMyDDCont = findCont(afterMyDDCont, true);
                beforeMyDDCont = findPrecedingDDCont(afterMyDDCont);
                Log.p("---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                Log.p("---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
            } else { //dragging downwards
                beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                afterMyDDCont = findCont(beforeMyDDCont, false);
                afterMyDDCont = findNextDDCont(beforeMyDDCont);
                Log.p("---before      = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                Log.p("---after (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
            }

            ItemAndListCommonInterface afterElement = afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : null;
            ItemAndListCommonInterface beforeElement = beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : null;
            ItemAndListCommonInterface draggedElement = dragged.getDragAndDropObject();

//            MyDragAndDropSwipeableContainer dropDD = ((MyDragAndDropSwipeableContainer) dropTarget);
//<editor-fold defaultstate="collapsed" desc="comment">
//            MyDragAndDropSwipeableContainer beforeMyDDCont;
//            MyDragAndDropSwipeableContainer afterMyDDCont;
//            if (draggingUpwardsOrOverInitialDraggedEltPosition) {
//                beforeMyDDCont = findPrecedingDDCont(dropDD);
//                Log.p("---findPreviousDDContainer(dropDD)= " + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                afterMyDDCont = dropDD;
//            } else {
//                beforeMyDDCont = dropDD;
//                afterMyDDCont = findNextDDCont(dropDD);
//                Log.p("---findNextDDContainer(dropDD)= " + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//            }
//</editor-fold>
            dropActionCall = null; //reset
            dropAsSubtaskActionCall = null; //reset
            dropAsSuperTaskActionCall = null; //reset
            insertDropPlaceholder = null; //reset
            insertDropPlaceholderForSupertask = null; //reset
            insertDropPlaceholderForSubtask = null; //reset
            if (draggedElement instanceof Category) { //dragging a Category
//<editor-fold defaultstate="collapsed" desc="Category dragged">
//                        if (afterCont != null && (afterCont.getDragAndDropObject() instanceof Category || afterCont.getDragAndDropObject() == null)) { //can always drop a Category before another Category
//                if ((beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Category)) { //can always drop a Category before another Category
                if (beforeElement instanceof Category) { //can always drop a Category before another Category
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) getDragAndDropObject()).getOwner();
                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) draggedElement).getOwner();
//                        int indexCatList = categoryOwnerList.getItemIndex((ItemAndListCommonInterface) beforeMyDDCont.getDragAndDropObject()) + 1;
                        int insertIndex = categoryOwnerList.getItemIndex(beforeElement) + 1;
//                        moveCategoryAndSave(categoryOwnerList, (Category) getDragAndDropObject(), indexCatList); //+ addOneOnDownwardsDrag
                        moveCategoryAndSave(categoryOwnerList, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            afterCont.getParent().addComponent(afterCont.getParent().getComponentIndex(afterCont), dropPh);
//                            findParentContForDropPlaceholder(afterCont).addComponent(afterCont.getParent().getComponentIndex(afterCont), dropPh);
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };
//                    } else if (afterMyDDCont == null) {
//                } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Category) { //drop *before* an ItemList
                } else if (afterElement instanceof Category) { //drop *before* an ItemList
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) getDragAndDropObject()).getOwner();
                        ItemAndListCommonInterface categoryOwnerList = ((Category) draggedElement).getOwner();
//                        int indexCatList = categoryOwnerList.getItemIndex(((ItemList) afterMyDDCont.getDragAndDropObject()));
                        int insertIndex = categoryOwnerList.getItemIndex(afterElement);
//                        moveCategoryAndSave(categoryOwnerList, (Category) getDragAndDropObject(), indexCatList); //+ addOneOnDownwardsDrag
                        moveCategoryAndSave(categoryOwnerList, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            this.getParent().addComponent(this.getParent().getComponentCount(), dropPh); //add at the end of the container with the list of categories
                        addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                    };
                } else if (afterMyDDCont == null) {
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
                        int insertIndex = listOwner.size();
//                        moveCategoryAndSave(listOwner, (Category) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
                        moveCategoryAndSave(listOwner, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
                        addDropPlaceholderToAppropriateParentCont(this, dropPh, Integer.MAX_VALUE); //'this' because we cannot get the container holding the categories from neither before, nor after
                    };
                }
//</editor-fold>
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
//            } else if (getDragAndDropObject() instanceof ItemList) {
            } else if (draggedElement instanceof ItemList) {
//<editor-fold defaultstate="collapsed" desc="ItemList dragged">
                //NB dropping as sub or superelement does not make sense for ItemLists (currently - TODO!! this will change once meta-lists are introduced)
//                    if (afterCont != null && afterCont.getDragAndDropObject() instanceof ItemList) { //drop *before* an ItemList
//                if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof ItemList) { //drop *after* an ItemList
                if (beforeElement instanceof ItemList) { //drop *after* an ItemList //TODO!!!! even if expanded and showing subtasks???!!!
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemAndListCommonInterface) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        int insertIndex = listOwner.getItemIndex(beforeElement) + 1; //+1 drop at position *after* beforeItem
//                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemAndListCommonInterface) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };

//                    } else if (afterCont == null) { //drop after last element in list (even if it's an Item from an expanded ItemList)
//                } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof ItemList) { //drop *before* an ItemList
                } else if (afterElement instanceof ItemList) { //drop *before* an ItemList
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = ((ItemList) draggedElement).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
//                        int indexItem = listOwner.getItemIndex(((ItemList) afterMyDDCont.getDragAndDropObject()));
                        int insertIndex = listOwner.getItemIndex(afterElement);
//                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemList) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            addParentContForDropPlaceholder(this, dropPh, Integer.MAX_VALUE);
                        addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                    };
                } else if (afterMyDDCont == null) { //drop at the end of the list (even if beforeElement is not an ItemList)
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
                        int insertIndex = listOwner.size();
//                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemList) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
                        addDropPlaceholderToAppropriateParentCont(this, dropPh, Integer.MAX_VALUE);
                    };
                }
//</editor-fold>
//            } else if (getDragAndDropObject() instanceof Item) {
            } else if (draggedElement instanceof Item) {
                if (getDragAndDropCategory() != null) { //dragged object belongs to a category, so drag may move it to another category, but can also only reposition it within the same category
//<editor-fold defaultstate="collapsed" desc="if dragging an Item inside a Category list">
                    //we can always drop *after* another 'before' Item which also has a Category (isn't triggered for expanded subtasks since they return null for getDragAndDropCategory())
//                    if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null) {
                    if (beforeElement instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null) {
                        dropActionCall = () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    beforeCont.getDragAndDropCategory().addToList((Item) beforeCont.getDragAndDropObject(), (Item) getDragAndDropObject(), true);
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), beforeCont.getDragAndDropCategory(), (ParseObject) getDragAndDropObject());
//</editor-fold>
                            Category beforeCategory = beforeMyDDCont.getDragAndDropCategory();
//                            int indexItem = ((ItemAndListCommonInterface) beforeMyDDCont.getDragAndDropCategory()).getItemIndex((Item) beforeMyDDCont.getDragAndDropObject()) + 1; //+1 drop at position *after* beforeItem
                            int insertIndex = beforeCategory.getItemIndex(beforeElement) + 1; //+1 drop at position *after* beforeItem
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeMyDDCont.getDragAndDropCategory(), (Item) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeCategory, (Item) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                        };
//                    } else if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Category) {
                    } else if (beforeElement instanceof Category) {
                        //dropping item right after a Category => move to that category
                        dropActionCall = () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    ((Category) beforeCont.getDragAndDropObject()).addToList(0, (Item) getDragAndDropObject()); //insert item at beginning of category's list of items
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), (ParseObject) beforeCont.getDragAndDropObject(), (ParseObject) getDragAndDropObject());
//</editor-fold>
//DONE!! if category is NOT expanded, should an item be inserted at the beginning of the subtask list or at the end?? At the beginning like if it was expanded? Create an setting
//insert item at beginning of category's list of items (or at the end if setting is false
//                            boolean insertAtHeadOfSubitemList = afterMyDDCont.getDragAndDropObject() instanceof Item //<=> category is expanded and afterCont is an Item
                            boolean insertAtHeadOfSubitemList = afterElement instanceof Item //<=> category is expanded and afterCont is an Item
                                    || MyPrefs.dropItemAtBeginningOfUnexpandedCategorySubtaskList.getBoolean();
//                            int indexItem = insertAtHeadOfSubitemList ? 0 : ((Category) beforeMyDDCont.getDragAndDropObject()).getList().size() + 1;
                            int insertIndex = insertAtHeadOfSubitemList ? 0 : ((Category) beforeElement).getList().size();
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeMyDDCont.getDragAndDropObject(), (Item) getDragAndDropObject(), indexItem);
                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeElement, (Item) draggedElement, insertIndex);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                        };
//                    } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item && afterMyDDCont.getDragAndDropCategory() != null) {
                    } else if (afterElement instanceof Item && afterMyDDCont.getDragAndDropCategory() != null) {
                        //we drop *before* another Item in a Category (eg when the subtasks of the previous item in the cateogry are expanded)
                        dropActionCall = () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    boolean sameCategory = getDragAndDropCategory() == afterCont.getDragAndDropCategory();
//                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//                                    afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the
//                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) getDragAndDropCategory(), (ParseObject) afterCont.getDragAndDropCategory(), (ParseObject) getDragAndDropObject());
//</editor-fold>
//                            int insertIndex = ((ItemAndListCommonInterface) afterMyDDCont.getDragAndDropCategory()).getItemIndex((Item) afterMyDDCont.getDragAndDropObject());
                            Category draggedCategory = getDragAndDropCategory();
                            Category afterCategory = afterMyDDCont.getDragAndDropCategory();
                            int insertIndex = afterCategory.getItemIndex(afterElement);
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), afterMyDDCont.getDragAndDropCategory(), (Item) getDragAndDropObject(), insertIndex);
                            moveItemBetweenCategoriesAndSave(draggedCategory, afterCategory, (Item) draggedElement, insertIndex);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
                    }
//</editor-fold>
                } else { //dragging an Item that does NOT belong to a category, only dropping as subtasks (not under categories since the logic is really unintutive!) 
//<editor-fold defaultstate="collapsed" desc="dropping *before* the first item">
                    //TODO!!! consider adding this again after all
                    //dropping *before* the first item (eg at the very top of a list). beforeElt is either null or not an Item
//                    if (beforeMyDDCont == null && afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item) {
                    if (beforeElement == null && afterElement instanceof Item) {
                        dropActionCall = () -> {
//                            ItemAndListCommonInterface newOwnerPrj = ((Item) afterMyDDCont.getDragAndDropObject()).getOwner();
                            ItemAndListCommonInterface newOwnerPrj = afterElement.getOwner();
                            int insertIndex = 0; //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
//                            moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
//<editor-fold defaultstate="collapsed" desc="comment">
//                                               if (beforeMyDDCont == null) {
//                            //insert at top of list
//                            dropActionCall = () -> {
////                                item.getOwner().addToList(dragged.getDragAndDropObject(), item, false);
//                                moveItemOrItemListAndSave(afterElement.getOwner(), draggedElement, 0);
//                            };
//                            insertDropPlaceholder = (dropPh) -> {
//                                addDropPlaceholderToRightParentCont(afterMyDDCont, dropPh, 0);
//                            };
//                        } else
//</editor-fold>
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
                        //dropping *after* the last item (eg at the very end of a list). beforeElt is an Item
//                    } else if (afterMyDDCont == null && beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item) {
//                    } else if (afterMyDDCont == null && beforeElement instanceof Item) {
//                        dropActionCall = () -> {
//                            ItemAndListCommonInterface newOwnerPrj = ((Item) beforeMyDDCont.getDragAndDropObject()).getOwner();
//                            int insertIndex = newOwnerPrj.size(); //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
//                            moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
//                        };
//                        dropAsSubtaskActionCall = () -> {
//                            ItemAndListCommonInterface newOwnerPrj = ((Item) beforeMyDDCont.getDragAndDropObject());
//                            int insertIndex = newOwnerPrj.size(); //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
//                            moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
//                        };
//                        insertDropPlaceholder = (dropPh) -> {
//                            addDropPlaceholderToRightParentCont(beforeMyDDCont, dropPh, Integer.MAX_VALUE);
//                        };
//                    } else if (dropDD.getDragAndDropObject() instanceof Item) {
//</editor-fold>
                    } else if (beforeElement instanceof Item) { //dropping an item *after* another item
//<editor-fold defaultstate="collapsed" desc="comment">
//                        Item dropTargetItem = (Item) dropDD.getDragAndDropObject();
//                        if (false) {
////                            MyDragAndDropSwipeableContainer before;
////                            MyDragAndDropSwipeableContainer after;
//                            if (draggingUpwardsOrOverInitialDraggedEltPosition) {
//                                beforeMyDDCont = dropDD;
//                                afterMyDDCont = findNextDDCont(dropDD);
//                            } else {
//                                beforeMyDDCont = findPrecedingDDCont(dropDD);
//                                afterMyDDCont = dropDD;
//                            }
//                        }
//</editor-fold>
//dropping either between two siblings or between an expanded subtask (any level of depth) and a following task at a lower level of depth, e.g. T1; S1; > T2;
//                            if (beforeMyDDCont.getDragAndDropObject().getList().size() == 1 && beforeMyDDCont.getDragAndDropObject().getList().contains(dragged.getDragAndDropObject())) {
                        if (beforeElement.getList().size() == 1 && beforeElement.getList().contains(dragged.getDragAndDropObject())) {
                            //first treat special case of dropping a single subtask back under the task it tasks it belonged to before starting dragging it around:
                            dropActionCall = () -> {
                            };//do nothing since it is already a subtask of previous
                            insertDropPlaceholder = (dropPh) -> {
                                //insert as a subtask container
                                BorderLayout borderLayout = (BorderLayout) beforeMyDDCont.getLayout(); //if dragged is already a subtask of previous, then we can assume the center container for subtasks already exists
                                Container centerCont = (Container) borderLayout.getCenter();
                                centerCont.addComponent(dropPh); //add to end of center list (to avoid changing the index of original hidden container)
                            };

                            dropAsSubtaskActionCall = null; // doesn't make sense here since after is already a subtask

                            dropAsSuperTaskActionCall = () -> {
                                // can drop left to insert as a sibling task instead of a subtask
                                ItemAndListCommonInterface newOwnerPrj = beforeElement.getOwner();
                                int insertIndex = newOwnerPrj.getItemIndex(beforeElement) + 1;
                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
                            };
                            insertDropPlaceholderForSupertask = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 0);
                            };

                        } else if (beforeElement == afterElement.getOwner()) {
                            //inserting between a task and its expanded subtask => always insert as subtask
                            dropActionCall = () -> {
//                                    ItemAndListCommonInterface newOwnerPrj = beforeElement.getOwner();
//                                    int insertIndex = newOwnerPrj.getItemIndex(beforeElement) + 1;
//                                    moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
                                moveItemOrItemListAndSave(beforeElement, draggedElement, 0);
                            };
                            insertDropPlaceholder = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                            };
                            dropAsSubtaskActionCall = null; // doesn't make sense here since dropping already make dragged a subtask
                            dropAsSuperTaskActionCall = null; //does not make sense 
                        } else {
                            MyDragAndDropSwipeableContainer sibling = findSiblingUpwardsInHierarchy(dragged, beforeMyDDCont);
                            if (sibling != null) {
                                assert sibling.getDragAndDropObject().getOwner() == draggedElement.getOwner() : "sibling does not have same owner as draggedItem";
                                //if there is a sibling up the hierarchy, then insert after that
                                dropActionCall = () -> {
                                    ItemAndListCommonInterface siblingElement = sibling.getDragAndDropObject();
                                    ItemAndListCommonInterface newOwner = siblingElement.getOwner();
                                    int insertIndex = newOwner.getItemIndex(siblingElement) + 1; //+1: insert after sibling
                                    moveItemOrItemListAndSave(newOwner, draggedElement, insertIndex);
                                };
                                insertDropPlaceholder = (dropPh) -> {
                                    addDropPlaceholderToAppropriateParentCont(sibling, dropPh, 1);
                                };
                            } else {
                                //no sibling found, so simply insert after 'before' (after the subtask)
                                dropActionCall = () -> {
                                    ItemAndListCommonInterface newOwnerPrj = beforeElement.getOwner();
                                    moveItemOrItemListAndSave(newOwnerPrj, draggedElement);
                                };
                                insertDropPlaceholder = (dropPh) -> {
                                    addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                                };
                            }
                            //in any case, support insert as subtask or supertask
                            dropAsSubtaskActionCall = () -> { // doesn't make sense here since after is already a subtask
                                ItemAndListCommonInterface newOwnerPrj = beforeElement;
                                int insertIndex = newOwnerPrj.getItemIndex(afterElement);
                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, 0); //UI: 0 means insert at head of subtask list

                            };
                            insertDropPlaceholderForSubtask = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                            };

                            //drop left to insert as a super task at same level as 'after'
                            dropAsSuperTaskActionCall = () -> {
                                ItemAndListCommonInterface newOwnerPrj = afterElement.getOwner();
                                int insertIndex = newOwnerPrj.getItemIndex(afterElement); //insert just before the afterElement
                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
                            };
                            insertDropPlaceholderForSupertask = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                            };
                        }
                    }
                }
//<editor-fold defaultstate="collapsed" desc="old algos">
                //                        if (draggingUpwardsOrOverInitialDraggedEltPosition) {
                //                            insertBefore(dropDD, dragged);
                ////                            dropActionCall = () -> {
                ////                                if (!insertBefore(dropDD, dragged)) {
                ////                                    ItemAndListCommonInterface newOwnerPrj = dropTargetItem.getOwner();
                ////                                    int insertIndex = newOwnerPrj.getItemIndex(dropTargetItem);
                ////                                    moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
                ////                                }
                ////
                ////                            };
                //                        } else {
                //                            insertAfter(dropDD, dragged);
                ////                            dropActionCall = () -> {
                ////                                //insertAfter
                ////                                if (!insertAfter(dropDD, dragged)) {
                ////                                    ItemAndListCommonInterface newOwnerPrj = dropTargetItem.getOwner();
                ////                                    int insertIndex = newOwnerPrj.getItemIndex(dropTargetItem) + 1;
                ////                                    moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
                ////                                }
                ////                            };
                //                        }
                //
                //                    } else if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item) {
                //                        Item beforeItem = (Item) beforeMyDDCont.getDragAndDropObject();
                //                        //dropping *between* two Items
                //                        //if dropping beteen an item and its expanded subtask, insert before subtask
                //                        if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item) {
                //                            if (((Item) afterMyDDCont.getDragAndDropObject()).getOwner() == beforeItem) {
                //                                Log.p("1. beteen an item and its expanded subtask");
                //                                dropActionCall = () -> {
                //                                    ItemAndListCommonInterface newOwnerPrj = ((Item) afterMyDDCont.getDragAndDropObject()).getOwner();
                //                                    int indexI = 0; //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
                //                                    moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), indexI);
                //                                };
                //                                insertDropPlaceholder = (dropPh, asSubtask) -> {
                //                                    addDropPlaceholderToRightParentCont(afterMyDDCont, dropPh, 0);
                //                                };
                //                                //dropping between two items with same owner <=> siblings
                //                            } else if (((Item) afterMyDDCont.getDragAndDropObject()).getOwner() == beforeItem.getOwner()) {
                //                                Log.p("2. between two items with same owner");
                //                                dropActionCall = () -> {
                //                                    Item afterItem = (Item) afterMyDDCont.getDragAndDropObject();
                //                                    ItemAndListCommonInterface newOwnerPrj = afterItem.getOwner();
                //                                    int indexI = newOwnerPrj.getItemIndex(afterItem); //insert at the position of the second item
                //                                    moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), indexI);
                //                                };
                //                                dropAsSubtaskActionCall = () -> {
                //                                    ItemAndListCommonInterface newOwnerPrj = beforeItem;
                //                                    int indexI = beforeItem.size(); //when dropping as subtask on an time, always insert at end of list (otherwise insert idrectly in expanded subtask list
                //                                    moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), indexI);
                //                                };
                //                                insertDropPlaceholder = (dropPh, asSubtask) -> {
                //                                    addDropPlaceholderToRightParentCont(afterMyDDCont, dropPh, 0);
                //                                };
                //                            } else {
                //                                //dropping between a subtask and following task at a higher level (only option left at this point in the code?!)
                //                                Log.p("3. between a subtask and following task at a higher level");
                //                                Log.p("3. beforeItem=" + beforeItem + "; afterItem=" + afterMyDDCont.getDragAndDropObject());
                //                                Log.p("3. beforeItem.owner=" + beforeItem.getOwner() + "; afterItem.owner=" + ((ItemAndListCommonInterface) afterMyDDCont.getDragAndDropObject()).getOwner());
                //
                //                                dropActionCall = () -> {
                //                                    ItemAndListCommonInterface precedingSibling = findPrecedingSibling((Item) getDragAndDropObject(), beforeItem);
                //                                    if (true || precedingSibling != null) {
                //                                        ItemAndListCommonInterface owner = precedingSibling.getOwner(); //remove item from old owner list
                //                                        int indexBef = owner.getItemIndex(precedingSibling);
                //                                        moveItemOrItemListAndSave(owner, (Item) getDragAndDropObject(), indexBef);
                //                                    }
                //                                };
                //                                dropAsSubtaskActionCall = () -> {
                //                                    ItemAndListCommonInterface newOwnerPrj = ((Item) beforeMyDDCont.getDragAndDropObject());
                //                                    int indexI = newOwnerPrj.size(); //UI: when dropping as subtask, add as last subtask
                //                                    moveItemOrItemListAndSave(newOwnerPrj, (Item) getDragAndDropObject(), indexI);
                //                                };
                //                                insertDropPlaceholder = (dropPh, asSubtask) -> {
                //                                    addDropPlaceholderToRightParentCont(beforeMyDDCont, dropPh, Integer.MAX_VALUE);
                //                                };
                //                            }
                //</editor-fold>
            } else {
                Log.p("4. !!!!!after an Item, but no suitable if statement");
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//                                dropActionCall = () -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
////                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    ItemAndListCommonInterface newOwner = ((Item) afterCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    oldOwner.removeFromList((Item) getDragAndDropObject());
////                                    //Insert after the previous/before Item
//////                                    ((Item) beforeCont.getDragAndDropObject()).getOwner().addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the
////                                    newOwner.addToList(((Item) afterCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the
////                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
////</editor-fold>
//                                    ItemAndListCommonInterface newOwnerB = ((Item) afterMyDDCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
//                                    int indexBef = newOwnerB.getItemIndex(((Item) afterMyDDCont.getDragAndDropObject()));
//                                    moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerB, (Item) getDragAndDropObject(), indexBef);
//                                };
////                                dropAsSubtaskActionCall = () -> {}; //CANNOT drop as subtask *before* an item
//                                insertDropPlaceholder = (dropPh, asSubtask) -> {
//                                    addDropPlaceholderToRightParentCont(this, dropPh, Integer.MAX_VALUE);
//                                };
//                            } else if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item) {
//
//                            }
//
//                            if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item) {
//                                dropAsSubtaskActionCall = () -> {
//                                    ItemAndListCommonInterface newOwnerPrj = ((Item) beforeMyDDCont.getDragAndDropObject());
//                                    int indexI = 0; //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
//                                    moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), indexI);
//                                };
//
//                                //else: situation not handled, do nothing (and do not show a dropPlaceholder container at this position either)
//                            }
//                            if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item) {// && ((Item)beforeCont.getDragAndDropObject()).getOwner()==((Item)getDragAndDropObject()).getOwner())
//                                dropActionCall = () -> {
////<editor-fold defaultstate="collapsed" desc="comment">
//////                                    ((Item) getDragAndDropObject()).removeFromList((Item) getDragAndDropObject()); //remove item from old owner list
//////                                    ((Item) getDragAndDropObject()).getOwner().removeFromList((Item) getDragAndDropObject()); //remove item from old owner list
//////                                    ((Item) getDragAndDropObject()).removeMeFromOwner(); //remove item from old owner list
////                                    //NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
////                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    ItemAndListCommonInterface newOwner = ((Item) beforeCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    int index2 = newOwner.getItemIndex(((Item) beforeCont.getDragAndDropObject())); //get index before removing since oldOwner and newOwner may be the same list
////                                    oldOwner.removeFromList((Item) getDragAndDropObject());
////                                    //Insert after the previous/before Item
//////                                    ((Item) beforeCont.getDragAndDropObject()).getOwner().addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), true); //insert *before* the
//////                                    newOwner.addToList(((Item) beforeCont.getDragAndDropObject()), (Item) getDragAndDropObject(), false); //insert *after* the
////                                    newOwner.addToList(index2, (Item) getDragAndDropObject()); //insert *after* the
////                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
////</editor-fold>
//                                    ItemAndListCommonInterface newOwner = ((Item) beforeMyDDCont.getDragAndDropObject()).getOwner();
//                                    int indexI = newOwner.getItemIndex(((Item) beforeMyDDCont.getDragAndDropObject())) + 1; //+1 drop *after* beforeItem
//                                    moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwner, (Item) getDragAndDropObject(), indexI);
//                                };
//                                dropAsSubtaskActionCall = () -> {
////<editor-fold defaultstate="collapsed" desc="comment">
//////                                getDragAndDropList().removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//////                                    ((Item) getDragAndDropObject()).getOwner().removeFromList((Item) getDragAndDropObject()); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
//////                                    ((Item) getDragAndDropObject()).removeMeFromOwner(); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
////                                    //NB: get the two owners first, since object may be dropped on itself in which case removing it from its old owner will be a pb
////                                    ItemAndListCommonInterface oldOwner = ((Item) getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    ItemAndListCommonInterface newOwner = ((Item) beforeCont.getDragAndDropObject()).getOwner(); //remove item from old owner list
////                                    oldOwner.removeFromList((Item) getDragAndDropObject());
//////                                afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the
////                                    newOwner.addToList(0, (Item) getDragAndDropObject()); //insert as the first subtask of the item before
//////                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) ((Item) getDragAndDropObject()).getOwner(),
//////                                            (ParseObject) ((Item) beforeCont.getDragAndDropObject()).getOwner(), ((Item) beforeCont.getDragAndDropObject()), (ParseObject) getDragAndDropObject());
////                                    DAO.getInstance().saveInBackgroundSequential((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) getDragAndDropObject());
////</editor-fold>
//                                    ItemAndListCommonInterface newOwnerPrj = ((Item) beforeMyDDCont.getDragAndDropObject());
//                                    int indexI = 0; //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
//                                    moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), indexI);
//                                };
//                                insertDropPlaceholder = (dropPh, asSubtask) -> {
//                                    addDropPlaceholderToRightParentCont(beforeMyDDCont, dropPh, 1);
//                                };
//
//                            }
//                        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            //remove old dropPlaceholder (if any) - remove even if no new dropPosition since we don't want the old dropPlaceholder to hang as we drag further one
//            if (dragged.dropPlaceholder != null) {
//                dragged.dropPlaceholder.getParent().removeComponent(dragged.dropPlaceholder);
//            }
//if a drop action is possible at this position
//            if ((dropActionCall != null || dropAsSubtaskActionCall != null || dropAsSuperTaskActionCall != null)
//                    && (insertDropPlaceholder != null || insertDropPlaceholderForSubtask != null || insertDropPlaceholderForSupertask != null)) {
//</editor-fold>
            if ((dropActionCall != null && insertDropPlaceholder != null)
                    || (dropAsSubtaskActionCall != null && insertDropPlaceholderForSubtask != null)
                    || (dropAsSuperTaskActionCall != null && insertDropPlaceholderForSupertask != null)) {
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
                        Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
//                        if (insertBelow(x) && dropAsSubtaskActionCall != null) {
//                            dropAsSubtaskActionCall.call();
//                        } else {
//                            dropActionCall.call();
//                        }
                        if (lastInsertAsType == InsertAsType.NORMAL && dropActionCall != null) {
                            dropActionCall.call();
                        } else if (lastInsertAsType == InsertAsType.SUBTASK && dropAsSubtaskActionCall != null) {
                            dropAsSubtaskActionCall.call();
                        } else if (lastInsertAsType == InsertAsType.SUPERTASK && dropAsSuperTaskActionCall != null) {
                            dropAsSuperTaskActionCall.call();
                        }
//                                dragged.dropSucceeded = true;
//                                getComponentForm().animateHierarchy(300);
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
//                    treeList.addComponent(dropPlaceholderInsertionIndex, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//                insertDropPlaceholder.insert(newDropPlaceholder, false && dropAsSubtaskActionCall != null && insertBelow(x));
                if (lastInsertAsType == InsertAsType.NORMAL && insertDropPlaceholder != null) {
                    insertDropPlaceholder.insert(newDropPlaceholder);
                } else if (lastInsertAsType == InsertAsType.SUBTASK && insertDropPlaceholderForSubtask != null) {
                    insertDropPlaceholderForSubtask.insert(newDropPlaceholder);
                } else if (lastInsertAsType == InsertAsType.SUPERTASK && insertDropPlaceholderForSupertask != null) {
                    insertDropPlaceholderForSupertask.insert(newDropPlaceholder);
                } //else no drop action possible here
                Log.p(dropActionCall != null ? "dropActionCall " : "" + dropAsSubtaskActionCall != null ? "dropAsSubtaskActionCall " : "" + dropAsSuperTaskActionCall != null ? "dropAsSuperTaskActionCall " : "");
                Log.p(insertDropPlaceholder != null ? "insertDropPlaceholder " : "" + insertDropPlaceholderForSubtask != null ? "insertDropPlaceholderForSubtask " : "" + insertDropPlaceholderForSupertask != null ? "insertDropPlaceholderForSupertask " : "");
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
//                        newDropPlaceholder.getComponentForm().animateHierarchy(300); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
                newDropPlaceholder.getComponentForm().revalidate(); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//                        Log.p("addDragOverListener: new dropPlaceholder=" + newDropPlaceholder.getName() + ", inserted position=" + index + ", for dropTarget=" + dropTarget.getName());
            } else {
                Log.p("no drop action, addDragOverListener: treeList==null!!! for dropTarget=" + dropTarget.getName());
//                        ASSERT.that(treeList instanceof MyTree2, "treeList not instanceof MyTree2, treelist="
//                                + (treeList != null ? treeList.getName() : "nullx") + ", for dropTarget=" + dropTarget.getName());
//                        Log.p("addDragOverListener: treeList==null for dropTarget=" + dropTarget.getName());
            }
//            }
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

            Log.p(
                    "---------------- END MyDragAndDropSwipeableContainer() ------------------------------------", Log.DEBUG);
//            }
//                }
//            }
        }
        ); //end of dragOverListener
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

//    @Override
    public void pointerReleasedxxx(int x, int y) {
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

//<editor-fold defaultstate="collapsed" desc="comment">
    private boolean dropItemInCategoryViewXXX(ItemAndListCommonInterface before, ItemAndListCommonInterface after, ItemAndListCommonInterface dragged,
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

    private boolean dropCategoryXXX(ItemAndListCommonInterface before, ItemAndListCommonInterface after, Category dragged) {
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean dropBetweenItemsXXX(Item before, Item after, Item dragged, boolean insertBelow) {
//
//        if (before == null) { //inserting at the very top of the list (dropTargetContainer is *before* the first element on the screen)
//            //insert in the same list as after
//            after.getOwner().addToList(0, dragged);
//        } else if (after == null) { //inserting at the very end of the list (dropTargetContainer is *after* the last element on the screen)
//            if (!insertBelow) {
//                //insert in the same list as before
//                before.getOwner().addToList(dragged);
//            } else {
//
//            }
//        } else {
//            if (before.getOwner() == after.getOwner() || after.getOwner() == before) { //before and after belong to same owner, insert in between them (<=> after before)
//                if (!insertBelow) {
//                    int index = before.getOwner().getList().indexOf(after);
//                    before.getOwner().addToList(index, dragged);
//                } else {
//
//                }
//            } else { //else insert after 'after' (e.g. before is subtask11 (a subtask to Task1) and after is Task2, so added is added *after* subtask11)
//                if (!insertBelow) {
//                    int index = after.getOwner().getList().indexOf(after);
//                    after.getOwner().addToList(index + 1, dragged);
//                } else {
//
//                }
//            }
//        }
//        return true;
//    }
//</editor-fold>
    @Override
    public void drop(Component dragged, int x, int y) {
        //do nothing if dropped on this container
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void dropXXX(Component dragged, int x, int y) {
////        Log.p("drop-MyDragAndD");
//        Log.p("MyDragAndD.drop, dropTarget=" + getName() + ", dragged=" + dragged.getName());
//
//        if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself //TODO remove isValidDropTarget checks as code below should check valid conditions and do nothing if not
////            dragged.setHidden(false); // was set hidden in dragEnter
//            return;
//        }
//        boolean dropExecuted = false;
//
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
//            if (dropTarget instanceof Category) { //D&D onto a Category1
//
//                if (draggedObject instanceof Category) { //D&D ... of a Category2 => move to position of Category1
//
//                    Category draggedCategory = (Category) draggedObject;
//                    Category dropTargetCategory = (Category) dropTarget;
////                    removeFromList = draggedCont.getDragAndDropList();
//
//                    draggedCont.getDragAndDropList().removeFromList(draggedCategory);
////                    insertIntoList = getDragAndDropList(); //UI:
//                    index = getDragAndDropList().getItemIndex(dropTargetCategory); //get index where to drop
//                    getDragAndDropList().addToList(index, draggedCategory);
//
//                    DAO.getInstance().save((ParseObject) getDragAndDropList()); //save the list of categories
//                    dropExecuted = true;
//
//                } else if (draggedObject instanceof Item) { //D&D ... of an Item onto a Category
//
////                    Category addToCategory = getDragAndDropCategory(); //UI:
//                    Category addToCategory = (Category) dropTarget; //UI:
//                    Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
////                    if (addToCategory != null && removeFromCategory!=null) {
////                    index = addToCategory.getList().indexOf(dropTarget);
//                    Item item = ((Item) draggedObject);
//
//                    item.addCategoryToItem(addToCategory, true); //true => add item to category as well
//                    if (removeFromCategory != null) { //if dragged is an expanded subtask, it may not be in any category
//                        item.removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null, true => remove item from category as well
////                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
//                        DAO.getInstance().save(removeFromCategory); //save the categoy (removeFromCategory may be the same as addToCategory, but second save will just be ignored)
//                    }
//
//                    DAO.getInstance().save(addToCategory); //save the categoy that item was added to (can be null if no category, eg a subtask dragged onto an item in a category)
//                    DAO.getInstance().save(item); //save the dragged Item (the categories are saved below via saveDragged)
//                    dropExecuted = true;
//                } else {
//                    assert false : "shouldn't happen (eg ItemList should never appear in same list as Category)";
////                    index = -1;
//                }
//
//            } else if (dropTarget instanceof Item) {
//
//                if (draggedObject instanceof Item) {
//                    Item targetItem = ((Item) dropTarget);
//                    Item draggedItem = ((Item) draggedObject);
//
//                    if (insertBelow(x)) { //dropping item2 as a subtask of item1
////                        Item targetItem = ((Item) dropTarget);
////                        Item draggedItem = ((Item) draggedObject);
//                        ItemAndListCommonInterface owner = draggedCont.getDragAndDropList();
//                        if (owner != null && !(owner instanceof Category)) { //check on Category to avoid removing a subtask which is dragged from a Category
////                        draggedCont.getDragAndDropList().removeFromList(draggedItem); //remove subtask from previous list (remove first to remove Owner)
//                            owner.removeFromList(draggedItem); //remove subtask from previous list (remove first to remove Owner)
//                            DAO.getInstance().save((ParseObject) owner); //save the list from which subtask was removed
//                        }
//                        targetItem.addToList(0, draggedItem); //insert at head of sublist
//
//                        DAO.getInstance().save(targetItem); //save project onto which subtask was dropped
////                        DAO.getInstance().save((ParseObject) draggedCont.getDragAndDropList()); //save the list from which subtask was removed
//                        DAO.getInstance().save(draggedItem); //save the dragged Item (the categories are saved below via saveDragged)
//                        dropExecuted = true;
//
//                    } else if (getDragAndDropCategory() != null) { //D&D item1 from category1 onto an item2 in category1 OR category2  => insert item1 into category2 at item2's position
//                        //TODO!!!! if both items in same Category, just move
////                        if (targetItem)
//                        //D&D ... of an item2 in category2 => insert item2 into category1 at item1's position and remove item2 from category2
//                        Category addToCategory = getDragAndDropCategory(); //UI:
//                        Item item = ((Item) draggedObject);
//                        Category removeFromCategory = draggedCont.getDragAndDropCategory(); //either a category or null (eg if an expanded subtask)
//                        if (removeFromCategory != null) {
//                            item.removeCategoryFromItem(removeFromCategory, true); //no effect if (Category)removeFromList == null, true => remove item from category as well
//                            DAO.getInstance().save(removeFromCategory); //save the categoy (removeFromCategory may be the same as addToCategory, but second save will just be ignored)
////                        item.addCategoryToItem(addToCategory, true); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
//                        }
//                        index = addToCategory.getList().indexOf(targetItem); //get index *after* removing the item, in case we're D&D inside the same category
////                        addToCategory.add(index, item); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
//                        addToCategory.addItemToCategory(item, index, true); //true => add item to category as well //NO GOOD, since doesn't add as position 'index'
////                        DAO.getInstance().save(draggedCont.getDragAndDropCategory()); //save category that item was removed from (can be null if no category)
//
//                        DAO.getInstance().save(addToCategory); //save the categoy that item was added to (can be null if no category, eg a subtask dragged onto an item in a category)
//                        DAO.getInstance().save(item); //save the dragged Item (the categories are saved below via saveDragged)
//                        dropExecuted = true;
//
//                    } else { //'normal' D&D of item2 onto item1: move item2 from previous list and insert into item1's list at item1's position
//                        insertIntoList = getDragAndDropList();
//                        if (insertIntoList != null) { //eg in a ParseQuery list there may be items without an owner
//                            removeFromList = draggedCont.getDragAndDropList();
//                            if (removeFromList != null) {
//                                removeFromList.removeFromList(draggedItem); //if same list, remove *before* calculating index
//                                index = insertIntoList.getItemIndex(targetItem);
//
//                                if (insertIntoList instanceof Category) {
//                                    ((Category) insertIntoList).addItemToCategory(draggedItem, index, true);
//                                } else {
//                                    insertIntoList.addToList(index, draggedItem);
//                                }
//
//                                DAO.getInstance().save((ParseObject) removeFromList);
//                                if (insertIntoList != removeFromList) {
//                                    DAO.getInstance().save((ParseObject) insertIntoList); //save the dragged Item (the categories are saved below via saveDragged)
//                                }
//                                DAO.getInstance().save(draggedItem); //save the dragged Item (the categories are saved below via saveDragged)
//                                dropExecuted = true;
//                            }
//                        }
//                    }
//                } // else do nothing if dragging eg an ItemList or a Category onto an Item. //DONE: consider if dragging a Category onto an item should add the category to it (NO, less intuitive than dragging the item onto the Category)
//
//            } else if (dropTarget instanceof ItemList) {
//
//                if (draggedObject instanceof Item) { //insert dragged item into head of ItemList
//                    ItemList dropTargetItemList = (ItemList) dropTarget;
//                    Item draggedItem = (Item) draggedObject;
//
//                    dropTargetItemList.addToList(0, draggedItem); // 0 => insert into head of itemList when dropped on the container of the list itself
//                    removeFromList = draggedCont.getDragAndDropList();
//                    removeFromList.removeFromList(draggedItem); //if same list, remove before index
//
//                    DAO.getInstance().save(dropTargetItemList); //save new list
//                    DAO.getInstance().save((ParseObject) removeFromList); //save previous lsit
//                    DAO.getInstance().save(draggedItem); //save item, has changed owner
//
//                } else if (draggedObject instanceof ItemList) { //itemList onto ItemList => insert
//                    ItemList dropTargetItemList = (ItemList) dropTarget;
//                    ItemList draggedItemList = (ItemList) draggedObject;
//
//                    removeFromList = draggedCont.getDragAndDropList();
//                    removeFromList.removeFromList(draggedItemList);
//
//                    insertIntoList = getDragAndDropList();
//                    index = insertIntoList.getItemIndex(dropTargetItemList);
////                    getDragAndDropList().addToList(index, draggedItemList); // 0 => insert into head of itemList when dropped on the container of the list itself
//                    insertIntoList.addToList(index, draggedItemList); // 0 => insert into head of itemList when dropped on the container of the list itself
//
//                    DAO.getInstance().save((ParseObject) insertIntoList); //save updated list of ItemLists
//                    dropExecuted = true;
//
//                } else {
//                    assert false;
//                }
//            }
//
//            dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//            dragged.setFocusable(false); //set focusable false once the drop (activated by longPress) is completed
//            if (dropExecuted) {
//                ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //simply keep same position as whereto the list was scrolled during the drag, then inserted element should 'stay in place'
//
//                //moved the dragged container to new position to quickly refresh the screen
//                if (false) {
//                    Container draggedParentContainer = dragged.getParent();
//                    draggedParentContainer.removeComponent(this);
//                    int droppedParentIndex = getParent().getComponentIndex(this);
//                    getParent().addComponent(droppedParentIndex, dragged);
//                    getComponentForm().animateHierarchy(400);
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                dragged.setHidden(false); //set hidden in dragEnter
////ALL this done in dragFinished!
////                draggedCont.oldParent = null; //reset on successful drop
////                draggedCont.oldComp = null; //reset on successful drop
////                draggedCont.oldPos = -1; //reset
////                if (draggedCont.dropPlaceholder != null&&draggedCont.dropPlaceholder.getParent()!=null) {
////                    draggedCont.dropPlaceholder.getParent().removeComponent(draggedCont.dropPlaceholder); //remove the old placeholder on successful drop
////                    draggedCont.dropPlaceholder = null;
////                }
////</editor-fold>
//                super.drop(dragged, x, y); //Container.drop implements the first quick move of the container itself
////                removePlaceholder(); //do *before* setting dropPlaceholder=null!
////                dropPlaceholder = null; //reset placeholder
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) { //now done in dragFinished()
////            removePlaceholder(); //do *before* setting dropPlaceholder=null!
////            refreshAfterDrop();
////</editor-fold>
//            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//
////            }
//        }
//    }
//</editor-fold>
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
//            //remove NORMAL insertion to avoid that index changes if adding/removing from the same list
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
     This method returns an image representing the dragged component, it can
     be overriden by subclasses to customize the look of the image, the image
     will be overlaid on top of the form during a drag and drop operation.
     THJ: NB. image is cached during drag, so getDragImage cannot be used to
     * *dynamically* change the dragged image.

     @return an image
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
     Invoked on the focus component to let it know that drag has started on
     the parent container for the case of a component that doesn't support
     scrolling
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
     Invoked on the focus component to let it know that drag has started on
     the parent container for the case of a component that doesn't support
     scrolling. THJ:
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
     This callback method indicates that a component drag has just entered
     this component.

     THJ:called on Component.dropTarget in pointerDragged(x,y) and in
     dragFinishedImpl(x,y) (itself called from pointerReleased(int x, int y))

     @param dragged the component being dragged
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
     This callback method provides an indication for a drop target that a drag
     operation is exiting the bounds of this component and it should clear all
     relevant state if such state exists. E.g. if a component provides drop
     indication visualization in draggingOver this visualization should be
     cleared..

     @param dragged the component being dragged
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
     Callback indicating that the drag has finished either via drop or by
     releasing the component. THJ: called in last line of
     Component.dragFinishedImpl(int x, int y) on the component that was
     dragged (not on the drop target). If this component is removed from the
     Form, the dragged component will be set visible before and nothing else.

     @param x the x location
     @param y the y location
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
        Log.p("MyDragAndD.dragFinished for element=" + (getName() != null ? getName() : "") + ", UNHIDE, remove dropPlaceholder="
                + ((dropPlaceholder != null
                && dropPlaceholder.getName() != null)
                ? dropPlaceholder.getName() : "nullx"));
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
                Form f = dropPlaceholder.getComponentForm();
                Log.p("MyDragAndD.dragFinished: removing dropPlaceholder=" + dropPlaceholder.getName() + " from parent=" + dropPlaceholder.getParent().getName());
                dropPlaceholder.getParent().removeComponent(dropPlaceholder); //remove the old placeholder ( not done in successful drop)
                if (f != null) {
                    f.animateHierarchy(300); //refresh after hiding dropPlaceholder
                }
            } else {
                Log.p("***MyDragAndD.dragFinished: no parent() for dropPlaceholder=" + dropPlaceholder.getName());
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
        lastY = -1;
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
