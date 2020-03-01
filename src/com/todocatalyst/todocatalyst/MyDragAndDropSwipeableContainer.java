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
import com.codename1.ui.Font;
import com.codename1.ui.Form;
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionEvent.Type;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.Layout;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyDragAndDropSwipeableContainer.DragDirection.DOWN;
import static com.todocatalyst.todocatalyst.MyDragAndDropSwipeableContainer.DragDirection.UP;
import static com.todocatalyst.todocatalyst.MyDragAndDropSwipeableContainer.InsertPositionType.NONE;
import java.util.List;

/**
 * top-level container used in list to support (activatable) drag and drop
 *
 * @author Thomas
 */
class MyDragAndDropSwipeableContainer extends SwipeableContainer implements Movable2 {//, ActionListener<ActionEvent> {

    private int draggedWidth = -1;
    private int draggedHeight = -1;
//    boolean dropSucceeded = false; //keeps track of whether drop succeeded (to add the dragged container if not)
//    int draggedOrgIndex = -1; //index of dragged container to insert it at right position if drop fails
//    Container draggedOrgParent = null; //store the original parent of the dragged component to reinsert it the right place if the drop fails
    private Image dragImage2 = null;
    private Component dropPlaceholder = null; //store the dropPlaceholder (to move/remove it)
//    private int dropPlaceholderOldIndex = -1;
    private Component lastDropTarget = null;
    private Component lastDraggedOverXXX = null;
//    private MyDragAndDropSwipeableContainer lastDraggedOverMyDD = null;

    interface Call {

        void doDropAction();
    }
    private Call dropActionCall = null; //function containing the drop action to execute on a normal drop
    private Call dropAsSubtaskActionCall = null; //function containing the drop action to execute when dropping as a subtask (right-hand side of screen)
    private Call dropAsSuperTaskActionCall = null; //function containing the drop action to execute when dropping as a super/meta-task  (left-hand side of screen)

    interface InsertComp {

//        void insert(Component dropPlaceholder);//, int index);
        void insertDropPlaceholder(Component dropPlaceholder);
//        void insert(Component dropPlaceholder, boolean asSubtask);
    }
    private InsertComp insertDropPlaceholder = null; //function containing the drop action to execute on a normal drop
    private InsertComp insertDropPlaceholderForSubtask = null; //function containing the drop action to execute on a normal drop
    private InsertComp insertDropPlaceholderForSupertask = null; //function containing the drop action to execute on a normal drop
    private int lastLastY = -1; //previous Y value (to detect duplicate previous values)
    private int lastY = -1; //x value for last component this was dragged over
//    private boolean lastDragDirectionUp = false; //x value for last component this was dragged over
    private DragDirection lastDragDirection = DragDirection.NONE; //x value for last component this was dragged over
//    private Label newDropPlaceholder = null;

    boolean formNeedRefresh = false; //set true if a screen removeFromCache is needed

    public enum InsertPositionType {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        NORMAL,
        SUBTASK,
        SUPERTASK,
        NONE
    };

    private InsertPositionType newInsertPosition = InsertPositionType.NONE; //must be global to accessible in dropPlaceholder
//    private InsertPositionType lastInsertPosition = InsertPositionType.NONE;
    private InsertPositionType lastDropPlaceholderInsertPosition = InsertPositionType.NONE; //where is the current dropPlaceholder situated?
//    private InsertPositionType insertAs = null; //how to insert on the next dropAction

    /**
     *
     * @param evt
     */
//    @Override
//    public void actionPerformed(ActionEvent evt) {
//
//    }
//    private InsertPositionType newInsertPosition(int x, Component dropTarget) {
////        return newInsertPosition(x, dropTarget.getWidth());
//        return newInsertPosition(x);
//    }
    /**
     * based on drop position (middle of screen, or extreme left or right) will
     * determine whether to insert a task normally, or as a subtask or a
     * 'super'task.
     *
     * @param x
     * @param dropWidth
     * @return
     */
    private static InsertPositionType insertPosition(int x) {
//        int borderDropZoneWidthInPercent = 15;
//int screenWidthInPixels = Display.getInstance().getCurrent().getWidth();
        int screenWidthInPixels = Display.getInstance().getDisplayWidth();
        int dropWidthPixels = Display.getInstance().convertToPixels(MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask.getInt());
        if (false && Config.TEST) {
            Log.p("x=" + x + ", screenWidth=" + screenWidthInPixels + ", dropWidth (" + MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask.getInt() + "mm)=" + dropWidthPixels + "=" + ", screenWidth/3=" + screenWidthInPixels / 3 + ", min=" + Math.min(dropWidthPixels, screenWidthInPixels / 3));
        }
        dropWidthPixels = Math.min(dropWidthPixels, screenWidthInPixels / 4); //UI: cannot set a drop zone width larger than one fourth 1/4 of the screen width
        //TODO: calculate some smart *minimum* widht for the drop zone - not obvious, and maybe not needed since minimum zone is 5mm (as defined in settings)
//        int borderDropZoneWidthInPercent = MyPrefs.dropZoneWidthInPercentForDroppingAsSubtaskOrSuperTask.getInt();
//        int borderDropZoneWidthInPercent = MyPrefs.dropZoneWidthInMillimetersForDroppingAsSubtaskOrSuperTask.getInt();
//<editor-fold defaultstate="collapsed" desc="comment">
//        return x > this.getWidth() / 3 * 2;
//        int screenWidthPixelsPercent = Display.getInstance().getDisplayWidth() / 100;
//        int dropTargetWidthPixelsPercent = dropTarget.getWidth() / 100; //works for both portrait and landscape?!
//        int dropTargetWidthPixelsPercent = dropWidth / 100; //works for both portrait and landscape?!
//        if (x <= dropTargetWidthPixelsPercent * borderDropZoneWidthInPercent) { //position in left 15%
//            return InsertPositionType.SUPERTASK;
//        }
//        if (x >= dropTargetWidthPixelsPercent * (100 - borderDropZoneWidthInPercent)) { //position in right 15%
//            return InsertPositionType.SUBTASK;
//        } else {
//            return InsertPositionType.NORMAL; //position in middle 70%
//        }
//</editor-fold>
//        int dropTargetWidthPixelsPercent = screenWidthInPixels / 100; //works for both portrait and landscape?!
        if (x <= dropWidthPixels) { //position in left 15%
//            if (Config.TEST&&newInsertPosition!=InsertPositionType.SUPERTASK) Log.p("InsertPos="+InsertPositionType.SUPERTASK);
            return InsertPositionType.SUPERTASK;
        }
        if (x >= screenWidthInPixels - dropWidthPixels) { //position in right 15%
//            if (Config.TEST&&newInsertPosition!=InsertPositionType.SUBTASK) Log.p("InsertPos="+InsertPositionType.SUBTASK);
            return InsertPositionType.SUBTASK;
        }
        return InsertPositionType.NORMAL; //position in middle 70%
    }

    /**
     * called when x position has changed into a new zone where the dragged task
     * will be inserted differently, then move the dropPlaceholder to the
     * corresponding position (if defined). Does nothing if no dropPlaceholder
     * is defined (e.g. on first call).
     *
     * @param newXPos
     */
    private void refreshDropPlaceholderContainer(InsertPositionType newInsertType, Component dropPlaceholder) {
//        InsertPositionType newInsertAsType = insertType(newXPos);
//        if (newInsertType != oldInsertType && dropPlaceholder != null) { //do nothing if there is no dropPlaceholder defined
        if (true || dropPlaceholder != null) { //do nothing if there is no dropPlaceholder defined
            if (newInsertType == InsertPositionType.NORMAL && insertDropPlaceholder != null) {
//                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
                dropPlaceholder.remove();
//                Log.p("InsertPosition = " + newInsertType + ", inserting normal dropPlaceholder", Log.DEBUG);
                insertDropPlaceholder.insertDropPlaceholder(dropPlaceholder);
            } else if (newInsertType == InsertPositionType.SUBTASK && insertDropPlaceholderForSubtask != null) {
                dropPlaceholder.remove();
//                Log.p("InsertPosition = " + newInsertType + ", inserting SUBtask dropPlaceholder", Log.DEBUG);
                insertDropPlaceholderForSubtask.insertDropPlaceholder(dropPlaceholder);
            } else if (newInsertType == InsertPositionType.SUPERTASK && insertDropPlaceholderForSupertask != null) {
//                dropPlaceholder.getParent().removeComponent(dropPlaceholder);
                dropPlaceholder.remove();
//                Log.p("InsertPosition = " + newInsertType + ", inserting SUPERtask dropPlaceholder", Log.DEBUG);
                insertDropPlaceholderForSupertask.insertDropPlaceholder(dropPlaceholder);
            } else {
                if (Config.TEST) {
                    Log.p("New InsertPosition but no appropropriate dropPlaceholder insert function, Pos=" + newInsertType + ", NOR=" + insertDropPlaceholder + ". SUB=" + insertDropPlaceholderForSubtask + ", SUP=" + insertDropPlaceholderForSupertask);
                }
            }
//            if (false) {
//                lastDropPlaceholderInsertPosition = newInsertType; //keep track of where dropPlaceholder is situated
//            }
        }
    }

    /**
     * move between ItemLists or Items (projects)
     *
     * @param oldOwner
     * @param newOwner
     * @param itemOrItemList
     * @param insertAfterRefItemOrEndOfList
     */
    private void moveItemOrItemListAndSave(ItemAndListCommonInterface oldOwner, ItemAndListCommonInterface newOwner,
            ItemAndListCommonInterface itemOrItemList, ItemAndListCommonInterface refItem, boolean insertAfterRefItemOrEndOfList) {
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
        if (oldOwner == newOwner) {// && oldOwner != null) { //NB oldOwner may be null but only in error cases, so don't test on null, better to get an error
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (newPos > oldOwner.getItemIndex(itemOrItemList)) {
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos - 1, itemOrItemList);
//            } else { //newPos <= oldList.getItemIndex(itemOrItemList)
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos, itemOrItemList);
//            }
//</editor-fold>
            oldOwner.moveToPositionOf(itemOrItemList, refItem, insertAfterRefItemOrEndOfList);
//            DAO.getInstance().saveInBackground((ParseObject) oldList, (ParseObject) itemOrItemList);
            DAO.getInstance().saveNew((ParseObject) oldOwner, false); //no need to save itemOrItemList since owner is the same
        } else {
            oldOwner.removeFromList(itemOrItemList);
//            newOwner.addToList(newPos, itemOrItemList);
            newOwner.addToList(itemOrItemList, refItem, insertAfterRefItemOrEndOfList);
            DAO.getInstance().saveNew(false, (ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) itemOrItemList); //save triggered after drop operation
        }
    }

    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newOwner, ItemAndListCommonInterface itemOrItemList,
            ItemAndListCommonInterface refItem, boolean insertAfterRefItemOrEndOfList) {
        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newOwner, itemOrItemList, refItem, insertAfterRefItemOrEndOfList);
    }

    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newOwner,
            ItemAndListCommonInterface itemOrItemList, boolean insertAfterRefItemOrEndOfList) {
        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newOwner, itemOrItemList, null, insertAfterRefItemOrEndOfList);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void moveItemOrItemListAndSave(ItemAndListCommonInterface oldOwner, ItemAndListCommonInterface newOwner, ItemAndListCommonInterface itemOrItemList, int newPos) {
////<editor-fold defaultstate="collapsed" desc="comment">
////Container draggedParent;
////        MyDragAndDropSwipeableContainer dragged = null;
////        int indexAdjustment = 0;
////        if (dragged != null && dragged.getParent() == newList) {
////            int draggedIndex = dragged.getParent().getComponentIndex(dragged);
////            if (newPos > draggedIndex) {
////                indexAdjustment = 1; //need to
////            }
////        }
////</editor-fold>
//        if (oldOwner == newOwner) {
//            if (newPos > oldOwner.getItemIndex(itemOrItemList)) {
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos - 1, itemOrItemList);
//            } else { //newPos <= oldList.getItemIndex(itemOrItemList)
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos, itemOrItemList);
//            }
//            oldOwner.moveToPositionOf(oldnewOwner, formNeedRefresh);
////            DAO.getInstance().saveInBackground((ParseObject) oldList, (ParseObject) itemOrItemList);
//            DAO.getInstance().saveInBackground((ParseObject) oldOwner); //no need to save itemOrItemList since owner is the same
//        } else {
//            oldOwner.removeFromList(itemOrItemList);
//            newOwner.addToList(newPos, itemOrItemList);
//            DAO.getInstance().saveInBackground((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) itemOrItemList);
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void moveItemOrItemListAndSave(ItemAndListCommonInterface draggedElement, ItemAndListCommonInterface refElement, boolean insertAfterRefElement) {
//
//        ItemAndListCommonInterface oldOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//        ItemAndListCommonInterface newOwner = refElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//
//        int insertIndex = newOwner.getItemIndex(refElement) ; //+1 drop at position *after* beforeItem
////                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemAndListCommonInterface) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
////        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
//        if (oldOwner == newOwner) { //moving within the same list, also means that insertposition must be adjusted if the
//            if (newPos > oldOwner.getItemIndex(itemOrItemList)) {
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos - 1, itemOrItemList);
//            } else { //newPos <= oldList.getItemIndex(itemOrItemList)
//                oldOwner.removeFromList(itemOrItemList);
//                newOwner.addToList(newPos, itemOrItemList);
//            }
////            DAO.getInstance().saveInBackground((ParseObject) oldList, (ParseObject) itemOrItemList);
//            DAO.getInstance().saveInBackground((ParseObject) oldOwner); //no need to save itemOrItemList since owner is the same
//        } else {
//            oldOwner.removeFromList(itemOrItemList);
//            newOwner.addToList(newPos, itemOrItemList);
//            DAO.getInstance().saveInBackground((ParseObject) oldOwner, (ParseObject) newOwner, (ParseObject) itemOrItemList);
//        }
//    }
//</editor-fold>

//    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newOwner, ItemAndListCommonInterface itemOrItemList, int newPos) {
//        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newOwner, itemOrItemList, newPos);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * insert at *end of* newList
     *
     * @param newOwner
     * @param itemOrItemList
     */
//    private void moveItemOrItemListAndSave(ItemAndListCommonInterface newOwner, ItemAndListCommonInterface itemOrItemList) {
//        moveItemOrItemListAndSave(itemOrItemList.getOwner(), newOwner, itemOrItemList, Integer.MAX_VALUE);
//    }
    /**
     * move the position of Category within CategoryList
     *
     * @param categoryList
     * @param newList
     * @param category
     * @param newPos
     */
//    private void moveCategoryAndSave(ItemAndListCommonInterface categoryList, Category category, int newPos) {
//        if (newPos > categoryList.getItemIndex(category)) {
//            categoryList.removeFromList(category);
//            categoryList.addToList(newPos - 1, category);
//        } else {
//            categoryList.removeFromList(category);
//            categoryList.addToList(newPos, category);
//        }
//        DAO.getInstance().saveInBackground((ParseObject) categoryList, (ParseObject) category);
//    }
//</editor-fold>
    /**
     * will update the categories for item, and either move item within the same
     * category, move it from old to new, only insert it into new (if old is
     * null) or only remove it from old (if new is null). Does nothing if both
     * categories are null.
     *
     * @param oldCategory
     * @param newCategory
     * @param item
     * @param newPos
     */
    private void moveItemBetweenCategoriesAndSave(Category oldCategory, Category newCategory, Item item, Item refItem, boolean insertAfterOrEndOfList) {
        if (oldCategory == newCategory && oldCategory != null) { //if within same Category (and categories not null)
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (newPos > oldCategory.getItemIndex(item)) {
//                oldCategory.removeItemFromCategory(item, false); //false: keep the position of the category in the item's list of categories
//                newCategory.addItemToCategory(item, newPos - 1, false); //false: keep the position of the category in the item's list of categories
//            } else {
//                oldCategory.removeItemFromCategory(item, true);
//                newCategory.addItemToCategory(item, newPos, true);
//            }
//</editor-fold>
            oldCategory.moveToPositionOf(item, refItem, insertAfterOrEndOfList);
            DAO.getInstance().saveNew(false, (ParseObject) oldCategory, (ParseObject) item); //only save list once
        } else { //oldCategory != newCategory || oldCategory == null
            if (oldCategory != null && newCategory != null) { //different categories, but both non-null: remove from old and add to new at newPos (~'normal' case)
                oldCategory.removeItemFromCategory(item, true);
//                newCategory.addItemToCategory(item, newPos, true);
                newCategory.addItemToCategory(item, refItem, true, insertAfterOrEndOfList);
                DAO.getInstance().saveNew(false, (ParseObject) oldCategory, (ParseObject) newCategory, (ParseObject) item);
            } else if (newCategory != null) { //item was dragged into a category, but not from another category (e.g. a subtask of a project) (~not very intuitive since the dragged item will be added to the category, but also stay in place where it was before, but I guess OK - "what you do is what you get")
//                newCategory.addItemToCategory(item, newPos, true);
                newCategory.addItemToCategory(item, refItem, true, insertAfterOrEndOfList);
                DAO.getInstance().saveNew(false, (ParseObject) newCategory, (ParseObject) item);
            } else if (false && oldCategory != null) { //item was eg dragged from category into a subtask of an expanded project (in an expanded category)
                //should this case even be supported? Visible effect: drag from an expanded Category into a subtask, and it disappears from the Category
                oldCategory.removeItemFromCategory(item, true);
                DAO.getInstance().saveNew(false, (ParseObject) oldCategory, (ParseObject) item);
            }
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void moveItemBetweenCategoriesAndSaveOLD(Category oldCategory, Category newCategory, Item item, int newPos) {
//        if (oldCategory == newCategory && oldCategory != null) { //if within same Category (and categories not null)
//            if (newPos > oldCategory.getItemIndex(item)) {
//                oldCategory.removeItemFromCategory(item, false); //false: keep the position of the category in the item's list of categories
//                newCategory.addItemToCategory(item, newPos - 1, false); //false: keep the position of the category in the item's list of categories
//            } else {
//                oldCategory.removeItemFromCategory(item, true);
//                newCategory.addItemToCategory(item, newPos, true);
//            }
//            DAO.getInstance().saveInBackground((ParseObject) oldCategory, (ParseObject) item); //only save list once
//        } else { //oldCategory != newCategory || oldCategory == null
//            if (oldCategory != null && newCategory != null) { //different categories, but both non-null: remove from old and add to new at newPos (~'normal' case)
//                oldCategory.removeItemFromCategory(item, true);
//                newCategory.addItemToCategory(item, newPos, true);
//                DAO.getInstance().saveInBackground((ParseObject) oldCategory, (ParseObject) newCategory, (ParseObject) item);
//            } else if (newCategory != null) { //item was dragged into a category, but not from another category (e.g. a subtask of a project) (~not very intuitive since the dragged item will be added to the category, but also stay in place where it was before, but I guess OK - "what you do is what you get")
//                newCategory.addItemToCategory(item, newPos, true);
//                DAO.getInstance().saveInBackground((ParseObject) newCategory, (ParseObject) item);
//            } else if (false && oldCategory != null) { //item was eg dragged from category into a subtask of an expanded project (in an expanded category)
//                //should this case even be supported? Visible effect: drag from an expanded Category into a subtask, and it disappears from the Category
//                oldCategory.removeItemFromCategory(item, true);
//                DAO.getInstance().saveInBackground((ParseObject) oldCategory, (ParseObject) item);
//            }
//        }
//    }

    /**
     * only used for the case where an Item is dropped as a supertask (next to a
     * higher-level mother project) which comes from an expanded category,
     * meaning the does the extraction of element and categories and index
     * calculation before calling moveItemBetweenCategoriesAndSave,
     * can/should(?) be called from everywhere in case a category update is
     * needed
     *
     * @param newSiblingMyDDCont the new sibling container which may have a
     * category to which dragged should be added
     * @param draggedMyDDCont
     */
//    private void insertIntoCategoryOfSiblingItemAndSave(MyDragAndDropSwipeableContainer newSiblingMyDDCont, MyDragAndDropSwipeableContainer draggedMyDDCont) {
//        if (newSiblingMyDDCont.getDragAndDropCategory() != null) { //only update if moving next to an Item already in an expanded Category
//            Item siblingItem = (Item) newSiblingMyDDCont.getDragAndDropObject();
//            Category newSiblingCategoryN = newSiblingMyDDCont.getDragAndDropCategory();
//            Item draggedItem = (Item) draggedMyDDCont.getDragAndDropObject();
//            int catIndex = newSiblingCategoryN.getItemIndex(siblingItem) + 1; //+1 drop at position *after* beforeItem
//            Category oldCategoryN = draggedMyDDCont.getDragAndDropCategory();
//            moveItemBetweenCategoriesAndSave(oldCategoryN, newSiblingCategoryN, draggedItem, catIndex);
//        }
//    }
//</editor-fold>
    /**
     * insert the dropPlaceholder into the parent container of refComp at the
     * right relative position wrt refComp
     *
     * @param refComp the component relative to which the dropPlaceholder should
     * be inserted (dropPh is inserted either before or after refComp)
     * @param dropPh dropPlaceholder component
     * @param relativeIndex relative position where to insert dropPh, +1: after
     * refComp, 0: at refComp's position, Integer.MAX_VALUE: at the end of the
     * container list to which refComp belongs, -1: before refComp (not used?!)
     */
//    private static boolean addDropPlaceholderToAppropriateParentCont(Component refComp, Component dropPh, int relativeIndex) {
//     static boolean addDropPlaceholderToAppropriateParentCont(MyDragAndDropSwipeableContainer refComp, Component dropPh, int relativeIndex) {
    static boolean addDropPlaceholderToAppropriateParentCont(Component refComp, Component dropPh, int relativeIndex) {

//        ASSERT.that(!(refComp instanceof MyTree2) && !(refComp instanceof ContainerScrollY));
//        Container dropCont =getParentScrollYContainer(refComp); //NOT possible to use getParentScrollYContainer because we need the refCompComp below to find the index
        Container dropContParent = refComp.getParent(); //treeList = the list in which to insert the dropPlaceholder
        if (Config.TEST) {
            ASSERT.that(dropContParent != null, "parent to refComp=" + refComp + " is null!");
        }
        Component refCompComp = refComp; //the containing container of refComp contained in dropCont
        while (!(dropContParent instanceof ContainerScrollY) && dropContParent != null) {
            refCompComp = dropContParent;
            dropContParent = dropContParent.getParent();
        }

        ASSERT.that(dropContParent == null || (dropContParent instanceof ContainerScrollY), "dropCont not correct type, dropCont=" + (dropContParent != null ? dropContParent.toString() : "<null>"));
        if (dropContParent != null) {
//            return (Container) dropCont;
//            ( (Container) dropCont).addComponent(dropCont.getC, refComp);
            if (relativeIndex == Integer.MAX_VALUE) {
//                ((Container) dropCont).addComponent(((Container) dropCont).getComponentCount(), dropPh);
                dropContParent.addComponent(dropPh);
            } else {
                int index = dropContParent.getComponentIndex(refCompComp);
                dropContParent.addComponent(index + relativeIndex, dropPh);
            }
            return true;
        } else {
            assert false;
            return false;
        }
    }

    /**
     * insert as first subtask
     *
     * @param refComp
     * @param dropPh
     * @param relativeIndex
     * @return
     */
    private static void expandSubtasks(ItemAndListCommonInterface ownerToExpand) {
        Form f = Display.getInstance().getCurrent();
        if (f instanceof MyForm && ((MyForm) f).expandedObjects != null) {
            ((MyForm) f).expandedObjects.add(ownerToExpand);
        }
    }

    private static boolean addDropPlaceholderAsFirstSubtask(MyDragAndDropSwipeableContainer refComp, Component dropPh, ItemAndListCommonInterface ownerToExpand) {
        MyTree2.insertAtPositionOfFirstSubtask(refComp.getParent(), dropPh);
//        Form f = Display.getInstance().getCurrent();
//        if (f instanceof MyForm && ((MyForm) f).expandedObjects != null) {
//            ((MyForm) f).expandedObjects.add(ownerToExpand);
//        }
        expandSubtasks(ownerToExpand);
        return true;
    }

    enum DragDirection {
        UP, DOWN, NONE
    }
    final static private int MIN_PIXELS_CHG = 0; //disable since this can create inconsistency btw direction and dropTarget. Display.getInstance().convertToPixels(1, false); //1mm in vertical pixels; could be 5;

    /**
     * returns true of newDropTarget is on a higher Y position than lastY (lastY
     *
     * @param lastY
     * @param newDropTarget
     * @return
     */
    private DragDirection calcDragDirection(int newY) {
        DragDirection newDir;
        if (false && newY == lastLastY) { //HACK to ignore duplicate values - to remove if Component.private void pointerDragged(final int x, final int y, final Object currentPointerPress) stops calling run() twice!
            lastLastY = lastY;
            lastY = newY;
            return lastDragDirection; //no change when we ignore a duplicate y value
        }
        lastLastY = lastY;
        int prevY = lastY;
        if (lastY == -1) {
            lastY = newY;
            newDir = DragDirection.NONE;
            Log.p("DragDIRECTION= init lastY= " + lastY);
        }
        if (newY < lastY) {
            lastY = newY;
            newDir = UP;
        } else if (newY > lastY) {
            newDir = DOWN;
        } else {
            newDir = lastDragDirection;
        }

        lastY = newY;
        if (Config.TEST && newDir != lastDragDirection) {
            Log.p("DragDIRECTION= " + newDir + ",   newY=" + newY + ", lastY=" + prevY);
        }
        return newDir;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private DragDirection calcDragDirectionOLD(int newY) {
//        if (lastY == -1) {
//            lastY = newY;
//            Log.p("DragDIRECTION= init lastY= " + lastY);
//        } else if (lastDragDirection == DragDirection.NONE && Math.abs(newY - lastY) >= MIN_PIXELS_CHG) {
//            lastY = newY;
//            Log.p("DragDIRECTION= INIT to= " + (newY - lastY >= 0 ? DragDirection.DOWN : DragDirection.UP) + ",   newY=" + newY + ",   lastY=" + lastY);
//            return newY - lastY >= 0 ? DragDirection.DOWN : DragDirection.UP; //if y is increasing <=> moving Down
//        } else if (lastDragDirection == DOWN) {
//            if (newY >= lastY) //continue to move down, just update lastY
//                lastY = newY;
//            else { //newY < lastY
//                //now started dragging UP
//                if (lastY - newY >= MIN_PIXELS_CHG) { //if moved UP more than MIN pixels, then actually change direction
//                    Log.p("DragDIRECTION= UP  " + ",   newY=" + newY + ", lastY=" + lastY);
//                    lastY = newY;
////            lastDragDirectionUp= DragDirection.UP;
//                    return DragDirection.UP;
//                    //else not moved enough pixels yet, keep old lastY value to keep track of when we've moved enough in opposite direction
//                } else { //we've moved less than MIN_PIXELS_CHG pixels up, so don't change direction yet
////                lastY = newY;
//                }
//            }
//        } else if (lastDragDirection == UP) {
//            if (newY <= lastY)
//                //we're still moving UP, update lastY
//                lastY = newY;
//            else { //newY > lastY
//                if (newY - lastY >= MIN_PIXELS_CHG) {
////            assert newY > lastY+MINIMUM_PIXEL_MOVEMENT_FOR_CHANGED_DIRECTION;
//                    Log.p("DragDIRECTION= DOWN" + ",   newY=" + newY + ",   lastY=" + lastY);
//                    lastY = newY;
////            lastDragDirectionUp= DragDirection.DOWN;
//                    return DragDirection.DOWN;
//                } else { //we've moved less than MIN_PIXELS_CHG pixels down, so don't change direction yet
//
//                }
//            }
//        }
//        //NO CHANGE
//        return lastDragDirection;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private DragDirection movingUpwardsOrOverDraggedXXX(int newY) {
//        if (lastY < 0 || newY + MIN_PIXELS_CHG <= lastY) {
//            Log.p("DragDIRECTION= UP,   lastY=" + lastY + ", newY=" + newY);
//            lastY = newY;
//            return DragDirection.UP;
//        } else if (newY > lastY + MIN_PIXELS_CHG) {
////            assert newY > lastY+MINIMUM_PIXEL_MOVEMENT_FOR_CHANGED_DIRECTION;
//            Log.p("DragDirection= DOWN, lastY=" + lastY + ", newY=" + newY);
//            lastY = newY;
//            return DragDirection.DOWN;
//        } else {
//            return DragDirection.NONE;
//        }
//    }
//    private boolean movingUpwardsOrOverDraggedOLD(MyDragAndDropSwipeableContainer newDropTarget) {
////        boolean upwardsOrOverDragged = false;
//        int newY = newDropTarget.getAbsoluteY();
////        Log.p("dragDirection: newDropTarget.getAbsoluteY()=" + newDropTarget.getAbsoluteY() + ", getY=" + newDropTarget.getY() + " getScrollY=" + newDropTarget.getScrollY());
////        Log.p("dragDirection: lastY=" + lastY + ", newY=" + newY + " over=" + newDropTarget.getName());
//        if (lastY < 0 || newY <= lastY) {
//            lastY = newY;
//            return true;
//        } else {
//            assert newY > lastY;
//            lastY = newY;
//            return false;
//        }
//    }
//
//    private boolean movingUpwardsOrOverDraggedXXX(MyDragAndDropSwipeableContainer over) {
////        boolean upwardsOrOverDragged = false;
//        Log.p("dragDirection: lastX=" + lastY + ", over.x=" + over.getX() + " over=" + over.getName());
//        if (lastY < 0 || over.getX() <= lastY) {
//            if (lastY != over.lastY) {
//                Log.p("Now dragging UP");
//            }
//            lastY = over.lastY;
//            return true;
//        } else {
//            if (lastY != over.lastY) {
//                Log.p("Now dragging DOWN");
//            }
//            lastY = over.lastY;
//            return false;
//        }
//    }
//</editor-fold>
    /**
     * get the MyDragAndDropSwipeableContainer of the parent task of the subtask
     * contained in cont. This method serves to encapsulate the structure of the
     * expanded subtask containers
     *
     * @param cont
     * @return or null if none found
     */
//    private static MyDragAndDropSwipeableContainer getParentMyDDCont(MyDragAndDropSwipeableContainer cont) {
    private static MyDragAndDropSwipeableContainer getParentMyDDCont(Component cont) {
//        Component beforeParent = cont;
        Container beforeParentParent = cont.getParent(); //treeList = the list in which to insert the dropPlaceholder

        Component north = null;
        Layout layout = null;
//        while (beforeParentParent != null) {
        //iterate up the container hierarchy to find BorderLayout container with a MyDragAndDropSwipeableContainer in the North position
//        while (beforeParentParent != null
//                && !((layout = beforeParentParent.getLayout()) instanceof BorderLayout
//                && !((north = ((BorderLayout) layout).getNorth()) instanceof MyDragAndDropSwipeableContainer))) {
        while (beforeParentParent != null) {
            layout = beforeParentParent.getLayout();
            if (layout instanceof BorderLayout) {
                north = ((BorderLayout) layout).getNorth();
                if ((north instanceof MyDragAndDropSwipeableContainer && north != cont) //north != cont special case for when dragged is over itself (otherwise it returns itself)
                        || beforeParentParent instanceof MyTree2) { //top-level MyTree2 is NOT in a north container like lower-level elements
                    break;
                }
            } else if (beforeParentParent instanceof MyTree2) {
                break;
            }
//            } else {
//            {
            beforeParentParent = beforeParentParent.getParent();
            north = null; //reset to avoid accidental reuse of previously assigned value
//            }
        }
//        }
//        if (north instanceof MyDragAndDropSwipeableContainer) { //north could contain some other container than MyDragAndDropSwipeableContainer
//            return (MyDragAndDropSwipeableContainer) north;
//        } else {
//            return null;
//        }
        return (!(beforeParentParent instanceof MyTree2) && north instanceof MyDragAndDropSwipeableContainer)
                ? (MyDragAndDropSwipeableContainer) north : null;
    }

    /**
     * return the MyDD at the highest level (or null if none)
     *
     * @param cont
     * @return
     */
    private static MyDragAndDropSwipeableContainer getTopLevelParentMyDDCont(MyDragAndDropSwipeableContainer cont) {
        Container parent = cont.getParent();
        MyDragAndDropSwipeableContainer topLevelMyDD = null;
        Component north = null;
        Layout layout = null;
        //iterate up the container hierarchy to find BorderLayout container with a MyDragAndDropSwipeableContainer in the North position
        while (parent != null) {
            if ((layout = parent.getLayout()) instanceof BorderLayout) {
                north = ((BorderLayout) layout).getNorth();
                if (north instanceof MyDragAndDropSwipeableContainer) { //north could contain some other container than MyDragAndDropSwipeableContainer
                    topLevelMyDD = (MyDragAndDropSwipeableContainer) north;
                }
            }
            parent = parent.getParent();
        }
        return topLevelMyDD;
    }

    /**
     * Get the container with the expanded sub-elements. This method serves to
     * encapsulate the structure of the expanded subtask containers
     *
     * @param myDDCont
     * @return null if none
     */
//     static ContainerScrollY getParentScrollYContainer(MyDragAndDropSwipeableContainer myDDCont) {
    static ContainerScrollY getParentScrollYContainer(Component myDDCont) {
        if (myDDCont == null) {
            return null;
        }
        Container myDDContParent = myDDCont.getParent();
//        while (draggedParent != null) {
        //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
        while (myDDContParent != null && !(myDDContParent instanceof ContainerScrollY)) {
            myDDContParent = myDDContParent.getParent();
        }
//        }
        return (ContainerScrollY) myDDContParent; //here either draggedParent is a ContainerScrollY or null
    }

    /**
     * starting from comp, iterates up to find the parent container that belongs
     * to a ScrollY container and then removes that parent. Used to ensure that
     * an inserted component, e.g. xx is removed completely
     *
     * @param comp
     */
    static boolean removeFromParentScrollYContainer(Component comp) {
        if (comp == null) {
            return false;
        }
        Container myDDContParent = comp.getParent();
        //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
        while (myDDContParent != null && !(myDDContParent instanceof ContainerScrollY)) {
            comp = myDDContParent;
            myDDContParent = myDDContParent.getParent();
        }
        if (myDDContParent != null) {
            myDDContParent.removeComponent(comp);
            return true;
        } else {
            return false;
        }
    }

    static ContainerScrollY removeFromParentScrollYContAndReturnScrollYCont(Component comp) {
        Container myDDContParent = comp.getParent();
        //iterate up the container hierarchy to find a MyTree2 or ContainerScrollY container which has a task *after* this one
        while (myDDContParent != null && !(myDDContParent instanceof ContainerScrollY)) {
            comp = myDDContParent;
            myDDContParent = myDDContParent.getParent();
        }
        if (myDDContParent instanceof ContainerScrollY) {
            myDDContParent.removeComponent(comp);
            return (ContainerScrollY) myDDContParent;
        } else {
            return null;
        }
    }

    /**
     * returns true if the two containers are (different) siblings, meaning they
     * belong to the same ScrollYContainer, false they're the same container or
     * either is null
     *
     * @param myDDCont1
     * @param myDDCont2
     * @return
     */
    private boolean isSibling(MyDragAndDropSwipeableContainer myDDCont1, MyDragAndDropSwipeableContainer myDDCont2) {
//        return myDDCont1.getParent().getParent() == myDDCont2.getParent().getParent();
        if (myDDCont1 != myDDCont2 && myDDCont1 != null && myDDCont2 != null) {
            return getParentScrollYContainer(myDDCont1) == getParentScrollYContainer(myDDCont2);
        } else {
            return false;
        }
    }

    /**
     * returns first MyDragAndDropContainer found, either comp itself or one of
     * its child containers
     *
     * @param comp
     * @return
     */
    protected static MyDragAndDropSwipeableContainer findMyDDContIn(Component comp) {
        if (comp instanceof MyDragAndDropSwipeableContainer) { //check if comp itself is a MyDragAndDropSwipeableContainer
            return (MyDragAndDropSwipeableContainer) comp;
        } else if (comp instanceof Container) { //search in hierarchy below comp
            Container cont = (Container) comp;
            for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
                Component cmp = cont.getComponentAt(i);
                MyDragAndDropSwipeableContainer myDragAndDrop = findMyDDContIn(cmp);
                if (myDragAndDrop != null) {
                    return myDragAndDrop;
                }
            }
        }
        return null;
    }

    /**
     * find a drop target in container hierarchy below or above comp. Used to
     * start from whatever container is found under a pinch finger and find the
     * corresponding container to get the corresponding Item elements.
     *
     * @param comp
     * @return drop target or null if none found
     */
    protected static MyDragAndDropSwipeableContainer findMyDDContStartingFrom(Component comp) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (comp == null) {
//            return null;
//        }
//</editor-fold>
        while (comp != null && !(comp instanceof Form)) { // when searching starting from e.g. the Ttoolbar, we may reach up to Form, from which the algo will search down and find a (wrong) MyDDCont, e.g. in tests the last element in the lisit
//<editor-fold defaultstate="collapsed" desc="comment">
//        int count = cont.getComponentCount();
//        for (int i = count - 1; i >= 0; i--) {
//            if (comp instanceof MyDragAndDropSwipeableContainer) { //check if comp itself is a MyDragAndDropSwipeableContainer
//                return (MyDragAndDropSwipeableContainer) comp;
//            } else if (comp instanceof Container) { //search in hierarchy below comp
//                Container cont = (Container) comp;
//                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
//                    Component cmp = cont.getComponentAt(i);
//                    MyDragAndDropSwipeableContainer myDragAndDrop = findDropContainerStartingFrom(cmp);
//                    if (myDragAndDrop != null) {
//                        return myDragAndDrop;
//                    }
//</editor-fold>
            MyDragAndDropSwipeableContainer myDragAndDrop = findMyDDContIn(comp);
            if (myDragAndDrop != null) {
                return myDragAndDrop;
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (cmp instanceof MyDragAndDropSwipeableContainer) {
//                        return (MyDragAndDropSwipeableContainer) cmp;
//                    } else if (cmp instanceof Container){
//
//                }
//                //for performance reasons, avoid diving into hierarchy of each sub Container, test top-level first
//                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
//                    Component cmp = cont.getComponentAt(i);
//                    if (cmp instanceof Container) {
//                        MyDragAndDropSwipeableContainer myDragAndDrop = findDropContainerStartingFrom((Container) cmp);
//                        if (myDragAndDrop != null) {
//                            return myDragAndDrop;
//                        }
//                    }
//                }
//</editor-fold>
            } else {
                //search in hierarchy above comp
                //TODO!!! optimization: this will search each container once for each level of the container hierarchy, e.g. 
                comp = comp.getParent();
//<editor-fold defaultstate="collapsed" desc="comment">
//            Container parent = comp.getParent();
//            while (parent != null) {
//                Container cont = parent; //(Container) comp;
//                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
//                    Component cmp = cont.getComponentAt(i);
//                    if (cmp == comp) {
//                        continue;
//                    }
//                    if (cmp instanceof MyDragAndDropSwipeableContainer) {
//                        return (MyDragAndDropSwipeableContainer) cmp;
//                    }
//                }
//                //for performance reasons, avoid diving into hierarchy of each sub Container, test top-level first
//                for (int i = cont.getComponentCount() - 1; i >= 0; i--) {
//                    Component cmp = cont.getComponentAt(i);
//                    if (cmp == comp) {
//                        continue;
//                    }
//                    if (cmp instanceof Container) {
//                        MyDragAndDropSwipeableContainer component = findDropContainerStartingFrom((Container) cmp);
//                        if (component != null) {
//                            return component;
//                        }
//                    }
//                }
//                parent = cont.getParent(); //
//            }
//</editor-fold>
            }
        }
        return null;
    }

    /**
     * find the
     *
     * @param comp
     * @return
     */
//    protected static MyDragAndDropSwipeableContainer findMyDDContAtTopLevelAbove(Component comp) {
    protected static Category findPrecedingCategory(Component comp) {
//        while (comp != null) {
//            if (comp instanceof MyDragAndDropSwipeableContainer && ((MyDragAndDropSwipeableContainer) comp).getDragAndDropCategory() != null) {
//                return ((MyDragAndDropSwipeableContainer) comp).getDragAndDropCategory();
//            } else {
//                comp = comp.getParent();
//            }
//        }
//        return null;
        MyDragAndDropSwipeableContainer categoryCont = findMyDDContAboveHoldingCategory(comp);
        if (categoryCont != null) {
            return (Category) categoryCont.getDragAndDropObject();
        } else {
            return null;
        }
    }

    protected static MyDragAndDropSwipeableContainer findMyDDContAboveHoldingCategory(Component comp) {
        Component c = comp;
        while (c != null) {
            if (c instanceof MyDragAndDropSwipeableContainer
                    && ((((MyDragAndDropSwipeableContainer) c).getDragAndDropObject() instanceof Category)
                    || (((MyDragAndDropSwipeableContainer) c).getDragAndDropCategory() instanceof Category))) {
                return ((MyDragAndDropSwipeableContainer) c);
            } else {
                c = c.getParent();
            }
        }
        return null;
    }

    /**
     * find the first sibling in the list which is before the ref container. A
     * dropped item is added *after* the returned container. A
     *
     * B
     * C
     * D
     * E
     * <- A => since A is as same level as the B (an ancestor to beforeElt/E,
     * and a sibling to dragged/A), insert in A and B's common owner X, after B
     * [F]
     *
     *
     * @param dragged
     * @return null if none
     */
    private MyDragAndDropSwipeableContainer findSiblingUpwardsInHierarchyN(MyDragAndDropSwipeableContainer dragged, MyDragAndDropSwipeableContainer before) {
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

//    private static ContainerScrollY getScrollYContainerWithSubtasks(MyDragAndDropSwipeableContainer cont, MyDragAndDropSwipeableContainer draggedOrNull) {
    private static ContainerScrollY getScrollYContainerWithSubtasks(Component cont, MyDragAndDropSwipeableContainer draggedOrNull) {
        if (cont == null) {
            return null;
        }

        Container contParent = cont.getParent();
        Layout layout;
        Component center;
        if (contParent != null && (layout = contParent.getLayout()) instanceof BorderLayout
                && (center = ((BorderLayout) layout).getCenter()) instanceof ContainerScrollY
                && (((ContainerScrollY) center)).getComponentCount()
                //                > (0 + getPositionInContainerScrollY(((ContainerScrollY) center), dragged) >= 0 ? 1 : 0)) {
                > (0 + (draggedOrNull != null ? getPositionInContainerScrollY(((ContainerScrollY) center), draggedOrNull) : 0) >= 0 ? 1 : 0)) {
            //if dragged hidden container is the only one in center, then there must be at least 2 elements in container to return it, this is ensured by this expression: getPositionInContainerScrollY(((ContainerScrollY) center), this) >= 0 ? 1 : 0)
            return (ContainerScrollY) center;
        }
        return null;
    }

    private static ContainerScrollY getScrollYContainerWithSubtasks(MyDragAndDropSwipeableContainer cont) {
        Layout layout;
        Component center;
        Container contParent = cont.getParent();

        if (contParent != null && (layout = contParent.getLayout()) instanceof BorderLayout
                && (center = ((BorderLayout) layout).getCenter()) instanceof ContainerScrollY
                && (((ContainerScrollY) center)).getComponentCount() > 0) {
            //if dragged hidden container is the only one in center, then there must be at least 2 elements in container to return it, this is ensured by this expression: getPositionInContainerScrollY(((ContainerScrollY) center), this) >= 0 ? 1 : 0)
            return (ContainerScrollY) center;
        }
        return null;
    }

    /**
     * get the MyDragAndDropSwipeableContainer from a container coming from the
     * ContainerScrollY container
     *
     * @param cont
     * @return
     */
    private static MyDragAndDropSwipeableContainer getTaskContainer(Container cont) {
        Layout layout;
        Component north;
//        if (cont != null && (layout = cont.getParent().getLayout()) instanceof BorderLayout
        if (cont != null && (layout = cont.getLayout()) instanceof BorderLayout
                && (north = ((BorderLayout) layout).getNorth()) instanceof MyDragAndDropSwipeableContainer) {
            return (MyDragAndDropSwipeableContainer) north;
//        } else if (cont instanceof MyDragAndDropSwipeableContainer) { //case for list of WorkSlots, will it cause trouble for task lists??
//            return (MyDragAndDropSwipeableContainer) cont;
        } else {
            return findMyDDContIn(cont); //this should even get the right container for the case of BorderLayout above?!
        }//        return null;
    }

//    private int indexForHiddenDraggedCont(ContainerScrollY scrollYContainer, MyDragAndDropSwipeableContainer cont) {
//        return scrollYContainer.getComponentIndex(cont.getParent());
//
//    }
    /**
     * encapsulates how to get the index of a MyDD in the ScrollY container
     * (this may change with the container hierarchy)
     *
     * @param scrollYContainerWithSubtask
     * @param eltContOrNull
     * @return
     */
//    static int getPositionInContainerScrollY(ContainerScrollY scrollYContainerWithSubtask, MyDragAndDropSwipeableContainer eltContOrNull) {
    static int getPositionInContainerScrollY(ContainerScrollY scrollYContainerWithSubtask, Component eltContOrNull) {
        return eltContOrNull != null ? scrollYContainerWithSubtask.getComponentIndex(eltContOrNull.getParent()) : -1;
    }

//    static int getPositionInParentContainerScrollY(MyDragAndDropSwipeableContainer eltContOrNull) {
    static int getPositionInParentContainerScrollY(Container eltContOrNull) {
        if (eltContOrNull != null) {
            ContainerScrollY parentScrollY = getParentScrollYContainer(eltContOrNull);
            if (parentScrollY != null) {
                return getPositionInContainerScrollY(parentScrollY, eltContOrNull);
            }
        }
        return -1;
    }

    /**
     * find and return the *last* MyDragAndDropSwipeableContainer in the
     * hierarchy of expanded subtasks inside/below cont
     *
     * @param cont (can be null)
     * @return cont itself if there are no expanded subtasks under it
     */
    static MyDragAndDropSwipeableContainer findLastDDContainer(MyDragAndDropSwipeableContainer cont, MyDragAndDropSwipeableContainer draggedOrNull) {
        ContainerScrollY scrollYContainerWithSubtask = getScrollYContainerWithSubtasks(cont, draggedOrNull); //OK for dragged to be null
        if (scrollYContainerWithSubtask != null) {
            if (draggedOrNull == null) {
                int adjIndex = scrollYContainerWithSubtask.getComponentCount() - 1; //if the last container is the dragged hidden one, take the one just before
                Component res = scrollYContainerWithSubtask.getComponentAt(adjIndex);
                if (res instanceof Container) {
                    return findLastDDContainer(getTaskContainer((Container) res), draggedOrNull); //iterate down the hierarchy
                }
            } else {
                int indexOfHiddenDraggedCont = getPositionInContainerScrollY(scrollYContainerWithSubtask, draggedOrNull); //this==dragged
                int compCount = scrollYContainerWithSubtask.getComponentCount();
                int adjIndex = (indexOfHiddenDraggedCont == compCount - 1) ? compCount - 2 : compCount - 1; //if the last container is the dragged hidden one, take the one just before
//            return findLastDDContainer(getTaskContainer(scrollYContainerWithSubtask.getComponentAt(scrollYContainerWithSubtask.getComponentCount()-1)));
                Component res = scrollYContainerWithSubtask.getComponentAt(adjIndex);
                if (res instanceof Container) {
                    return findLastDDContainer(getTaskContainer((Container) res), draggedOrNull);
                }
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

//     static MyDragAndDropSwipeableContainer findPrecedingDDCont(MyDragAndDropSwipeableContainer cont) {
//         return findPrecedingDDCont(cont, null);
//     }
//    static MyDragAndDropSwipeableContainer findPrecedingMyDDCont(MyDragAndDropSwipeableContainer cont, MyDragAndDropSwipeableContainer dragged) {
    static MyDragAndDropSwipeableContainer findPrecedingMyDDCont(Component cont, MyDragAndDropSwipeableContainer dragged) {
        //find a preceding sibling if any
        ContainerScrollY parentScrollYContainer = getParentScrollYContainer(cont);
        //Examples of lists with (H)idden element: H T1 T2: preceding(T1)=null (a), preceding(T2)=T1 (b); T1 H T2: preceding(T2)=T1 (c)
        //                                        idx(H)=0            idx=1
        int index = -1;
        if (parentScrollYContainer != null) {
            int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYContainer, dragged); //this==dragged
            index = getPositionInContainerScrollY(parentScrollYContainer, cont);
            if (index == indexOfHiddenDraggedCont && cont != dragged) { //if index points to the hidden container, take the one before the hidden as in case (b) and (c) (or index becomes negative in case (a) above)
                index = index - 1 - 1;
            } else {
                index = index - 1;
            }
//<editor-fold defaultstate="collapsed" desc="documentation">
//to find preceding element: if no expanded subtask, and no following sibling, then iterate up the hierarchy to find a task that follows
// example: below the preceding element (after ->>) for each one in the list, e.g. for S21, the preceding element is S2:
// T1
//   S1
//   S2
//     S21 -> S2
//     S22 -> S21
// T2 -> S22
//
// T1
//   S1
//   S2
//     S21
//     S22
//   S3 -> S22
// T2
// next(S22)==S3, next(S3)==T2
//</editor-fold>
            if (index < 0) {
                return getParentMyDDCont(cont); //if cont is first/only element in a list, then the preceding MyDD will be the expanded parent (e.g. an Item in an expanded Category)
//                return null; //no previous container found
            } else if (index >= 0) {
                //if it has an earlier subelements, find and return the preceding one
//            Component c = parentScrollYContainer.getComponentAt(index - 1);
                Component c = parentScrollYContainer.getComponentAt(index);
                while (!(c instanceof Container) && index >= 0) { //skip over non-element containers (eg inlineInsertCont or dropPlaceholder)
                    index--;
                    c = parentScrollYContainer.getComponentAt(index);
                }
//            if (c instanceof Container) {
                if (Config.TEST) {
                    ASSERT.that(c instanceof Container, "c is NOT instance of Container, c=" + c + "; cont=" + cont + "; dragged=" + dragged);
                }
                MyDragAndDropSwipeableContainer found = findLastDDContainer(getTaskContainer((Container) c), dragged); //return the very last element (eg last expanded subtask at deepest level of expansion)
                return found; //return the very last element (eg last expanded subtask at deepest level of expansion)
//            }
            }
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

    static MyDragAndDropSwipeableContainer findPrecedingMyDDCont(MyDragAndDropSwipeableContainer cont) {
        MyDragAndDropSwipeableContainer dragged;
        //find a preceding sibling if any
        ContainerScrollY parentScrollYContainer = getParentScrollYContainer(cont);
        //Examples of lists with (H)idden element: H T1 T2: preceding(T1)=null (a), preceding(T2)=T1 (b); T1 H T2: preceding(T2)=T1 (c)
        //                                        idx(H)=0            idx=1
        int index = -1;
        if (parentScrollYContainer != null) {
            index = getPositionInContainerScrollY(parentScrollYContainer, cont) - 1;
//<editor-fold defaultstate="collapsed" desc="documentation">
//to find preceding element: if no expanded subtask, and no following sibling, then iterate up the hierarchy to find a task that follows
// example: below the preceding element (after ->>) for each one in the list, e.g. for S21, the preceding element is S2:
// T1
//   S1
//   S2
//     S21 -> S2
//     S22 -> S21
// T2 -> S22
//
// T1
//   S1
//   S2
//     S21
//     S22
//   S3 -> S22
// T2
// next(S22)==S3, next(S3)==T2
//</editor-fold>
            if (index >= 0) {
                //if it has an earlier subelements, find and return the preceding one
//            Component c = parentScrollYContainer.getComponentAt(index - 1);
                Component c = parentScrollYContainer.getComponentAt(index);
//            if (c instanceof Container) {
                return findLastDDContainer(getTaskContainer((Container) c), null); //return the very last element (eg last expanded subtask at deepest level of expansion)
//            }
            }
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

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * return the MyDragAndDropSwipeableContainer on the screen *before* cont
     *
     * @param cont
     * @return
     */
//    static MyDragAndDropSwipeableContainer findPrevDDContainerXXX(MyDragAndDropSwipeableContainer cont) {
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
//                return findPrevDDContainerXXX((MyDragAndDropSwipeableContainer) previous);
//            }
//            if (dropCont != null) {
//                dropTargetTopLevelParent = dropCont;
//                dropCont = dropTargetTopLevelParent.getParent(); //treeList = the list in which to insert the dropPlaceholder
//            }
//        }
//        return null;
//    }
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
    /**
     * returns the first MyDD *after* comp, or null if none. The next element
     * can be either the first expanded subtask, the next sibling task, or
     * (complex case) the next sibling of one of its parents up the hierarchy,
     * or nothing/null if last element
     *
     * @param comp
     * @return
     */
//    private static MyDragAndDropSwipeableContainer findNextDDCont(MyDragAndDropSwipeableContainer comp, MyDragAndDropSwipeableContainer dragged) {
    private static MyDragAndDropSwipeableContainer findNextDDCont(Component comp, MyDragAndDropSwipeableContainer dragged) {
        //first check if there are expanded subtasks then return the first one
//        int index;
//        int indexOfHiddenDraggedCont;

        ContainerScrollY scrollYContainerWithSubtask = getScrollYContainerWithSubtasks(comp, dragged);
        if (scrollYContainerWithSubtask != null) {
            //if there is a subtask container
            int indexOfHiddenDraggedCont = getPositionInContainerScrollY(scrollYContainerWithSubtask, dragged); //this==dragged
            int countOfDragged = indexOfHiddenDraggedCont >= 0 ? 1 : 0;
            //must container at least 2 elements if the hidden is one of them:
            if (scrollYContainerWithSubtask.getComponentCount() >= (1 + countOfDragged)) {
                //if it has expanded subtasks, return the first (unless it is the hidden one)
                int adjIndex = indexOfHiddenDraggedCont == 0 ? 1 : 0; //if hidden is first element, take the one after
                Container c = (Container) scrollYContainerWithSubtask.getComponentAt(0 + adjIndex); //get first element in the list
                return getTaskContainer(c);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        else {
//            //if there are no appropriate expanded subtasks, then if there is a following sibling, return it
//            ContainerScrollY parentScrollYContainer = getParentScrollYContainer(comp);
//            if (parentScrollYContainer != null) {
//                //Examples of lists with (H)idden element: T1 H T2: next(T1)=T2 (a); T1 T2 H: next(T2)=null (c)
//                //Example where comp=dragged (initial situation)
//                int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYContainer, dragged); //this==dragged
//                int index = getPositionInContainerScrollY(parentScrollYContainer, comp);
//                int addOneIfNextIsHidden = (index + 1 == indexOfHiddenDraggedCont) ? 1 : 0; //if next index (index+1) points to the dragged one, then take the next again (+1)
//                int adjIndex = index + 1 + addOneIfNextIsHidden; //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//                if (adjIndex <= parentScrollYContainer.getComponentCount() - 1) { //check that adjIndex is valid (if hidden was last in list, it could point beyond the end of list)
//                    Container c = (Container) parentScrollYContainer.getComponentAt(adjIndex); //get next element in the list
//                    return getTaskContainer(c); //return the element
//                }
//            }
//        }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="documentation">
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
//</editor-fold>
//        MyDragAndDropSwipeableContainer parentComp = comp;
        Component parentComp = comp;
        ContainerScrollY parentScrollYCont = getParentScrollYContainer(parentComp);
//            ContainerScrollY contY=getParentScrollYContainer(comp);
        while (parentScrollYCont != null) {
//            indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYCont, dragged); //this==dragged
//            index = getPositionInContainerScrollY(parentScrollYCont, comp);
//            int adjIndex = index + 1 + (index + 1 == indexOfHiddenDraggedCont ? 1 : 0); //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//                if ((index = getPositionInContainerScrollY(parentScrollYCont, parentComp)) < parentScrollYCont.getComponentCount() - 1) {
            int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYCont, dragged); //this==dragged
            int index = getPositionInContainerScrollY(parentScrollYCont, parentComp);
            int addOneIfNextIsHidden = (index + 1 == indexOfHiddenDraggedCont) ? 1 : 0; //if next index (index+1) points to the dragged one, then take the next again (+1)
            int adjIndex = index + 1 + addOneIfNextIsHidden; //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
            if (adjIndex <= parentScrollYCont.getComponentCount() - 1) {
//                    return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex + 1));
//                return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex));
                Container c = (Container) parentScrollYCont.getComponentAt(adjIndex); //get next element in the list
                return getTaskContainer(c); //return the element
            }
            parentComp = getParentMyDDCont(parentComp);
            parentScrollYCont = parentComp != null ? getParentScrollYContainer(parentComp) : null;
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

    static MyDragAndDropSwipeableContainer findNextDDCont(MyDragAndDropSwipeableContainer comp) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        int index;
//        int indexOfHiddenDraggedCont;
//</editor-fold>
        //first check if there are expanded subtasks then return the first one
        ContainerScrollY scrollYContainerWithSubtask = getScrollYContainerWithSubtasks(comp);
        if (scrollYContainerWithSubtask != null) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            int indexOfHiddenDraggedCont = getPositionInContainerScrollY(scrollYContainerWithSubtask, dragged); //this==dragged
//            int countOfDragged = indexOfHiddenDraggedCont >= 0 ? 1 : 0;
//must container at least 2 elements if the hidden is one of them:
//            if (scrollYContainerWithSubtask.getComponentCount() >= (1 + countOfDragged)) {
//</editor-fold>
            //if there is a subtask container
            if (scrollYContainerWithSubtask.getComponentCount() >= 1) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                int adjIndex = indexOfHiddenDraggedCont == 0 ? 1 : 0; //if hidden is first element, take the one after
//                Container c = (Container) scrollYContainerWithSubtask.getComponentAt(0 + adjIndex); //get first element in the list
//</editor-fold>
                //if it has expanded subtasks, return the first (unless it is the hidden one)
//                Container c = (Container) scrollYContainerWithSubtask.getComponentAt(0); //get first element in the list
                Component c = scrollYContainerWithSubtask.getComponentAt(0); //get first element in the list
                if (c instanceof Container) {
                    Container cont = (Container) scrollYContainerWithSubtask.getComponentAt(0); //get first element in the list
                    return getTaskContainer(cont);
                }
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        else {
//            //if there are no appropriate expanded subtasks, then if there is a following sibling, return it
//            ContainerScrollY parentScrollYContainer = getParentScrollYContainer(comp);
//            if (parentScrollYContainer != null) {
//                //Examples of lists with (H)idden element: T1 H T2: next(T1)=T2 (a); T1 T2 H: next(T2)=null (c)
//                //Example where comp=dragged (initial situation)
//                int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYContainer, dragged); //this==dragged
//                int index = getPositionInContainerScrollY(parentScrollYContainer, comp);
//                int addOneIfNextIsHidden = (index + 1 == indexOfHiddenDraggedCont) ? 1 : 0; //if next index (index+1) points to the dragged one, then take the next again (+1)
//                int adjIndex = index + 1 + addOneIfNextIsHidden; //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//                if (adjIndex <= parentScrollYContainer.getComponentCount() - 1) { //check that adjIndex is valid (if hidden was last in list, it could point beyond the end of list)
//                    Container c = (Container) parentScrollYContainer.getComponentAt(adjIndex); //get next element in the list
//                    return getTaskContainer(c); //return the element
//                }
//            }
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="documentation">
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
//</editor-fold>
//            //if there are no appropriate expanded subtasks, then if there is a following sibling, return it
        MyDragAndDropSwipeableContainer parentComp = comp;
        ContainerScrollY parentScrollYCont = getParentScrollYContainer(parentComp);
//            ContainerScrollY contY=getParentScrollYContainer(comp);
        while (parentScrollYCont != null) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYCont, dragged); //this==dragged
//            index = getPositionInContainerScrollY(parentScrollYCont, comp);
//            int adjIndex = index + 1 + (index + 1 == indexOfHiddenDraggedCont ? 1 : 0); //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//                if ((index = getPositionInContainerScrollY(parentScrollYCont, parentComp)) < parentScrollYCont.getComponentCount() - 1) {
//            int indexOfHiddenDraggedCont = getPositionInContainerScrollY(parentScrollYCont, dragged); //this==dragged
//</editor-fold>
            int index = getPositionInContainerScrollY(parentScrollYCont, parentComp);
//<editor-fold defaultstate="collapsed" desc="comment">
//            int addOneIfNextIsHidden = (index + 1 == indexOfHiddenDraggedCont) ? 1 : 0; //if next index (index+1) points to the dragged one, then take the next again (+1)
//            int adjIndex = index + 1 + addOneIfNextIsHidden; //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
//</editor-fold>
            int adjIndex = index + 1; //if the next cont is the hidden dragged one, then take the next again (or none if hidden is at the end of the list)
            if (adjIndex < parentScrollYCont.getComponentCount()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex + 1));
//                return getTaskContainer((Container) parentScrollYCont.getComponentAt(adjIndex));
//</editor-fold>
                Component c = parentScrollYCont.getComponentAt(adjIndex); //get next element in the list
                if (c instanceof Container) {
//                Container c = (Container) parentScrollYCont.getComponentAt(adjIndex); //get next element in the list
                    return getTaskContainer((Container) c); //return the element
                }
            }
            parentComp = getParentMyDDCont(parentComp);
            parentScrollYCont = parentComp != null ? getParentScrollYContainer(parentComp) : null;
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
//    private InsertPositionType insertTypeXXX(int x) {
//        return newInsertPosition(x, Display.getInstance().getCurrent());
//    }
    /**
     * returns true if dropped elements should be inserted *below* the item it
     * is dropped on. Eg if dropped of right-hand third of the dropTarget
     * container.
     *
     * @param x
     * @return
     */
//    private boolean insertBelowXXX(int x) {
////        return x > this.getWidth() / 3 * 2;
//        return insertTypeXXX(x) == InsertPositionType.SUBTASK;
//    }
//</editor-fold>
    private void updateCategoryOnItemInsert(Item newBeforeItem, Item insertedItem, Category newCategory, Category oldCategory) {

    }

    @Override
    public void close() {
        Form f = getComponentForm();
        if (f instanceof MyForm) {
            if (((MyForm) f).openSwipeContainer != this) {
                ((MyForm) f).openSwipeContainer = null; //ensure last open container is reset if closed normally (e.g. swipe to close)
            }
        }
        super.close();
    }

    MyDragAndDropSwipeableContainer(Component bottomLeft, Component bottomRight, Component top) {
        super(bottomLeft, bottomRight, top);
        setDropTarget(true); //containers are both dropTargets and draggable
        setDraggable(false); //set false by default to allow scrolling. LongPress will activate, drop will deactivate it
        setUIID("MyDragAndDropSwipeableContainer");

        addSwipeOpenListener((e) -> {
            Form f = getComponentForm();
            if (f instanceof MyForm) {
                MyForm myForm = (MyForm) f;
                if (myForm.openSwipeContainer != null && myForm.openSwipeContainer != this && myForm.openSwipeContainer.getComponentForm() != null) { //if there's a previously open Swipetable, then close it
                    myForm.openSwipeContainer.close();
                }
                myForm.openSwipeContainer = this;
            }
        });

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false)
//            addPointerDraggedListener((e) -> {
//                Component drag = (Component) e.getSource();
//                Component dropTarget = e.getDropTarget(); //may be null (in Component.dragFinishedImpl(int x, int y))
//                int x = e.getX();
//                int y = e.getY();
//                //update drop position (NORMAL, SUBTASK, SUPERTASK):
//                InsertPositionType newInsertPosition = newInsertPosition(x);
//                if (newInsertPosition != newInsertPosition) {
////                refreshDropPlaceholderContainer(newInsertPosition, newInsertAsType);
//                    refreshDropPlaceholderContainer(newInsertPosition);
////                formNeedRefresh = true;
////                Log.p("InsertPositionType now = " + newInsertAsType+" (before="+(newInsertPosition!=null?newInsertPosition.toString().toLowerCase():"<null>")+")", Log.DEBUG);
//                    newInsertPosition = newInsertPosition;
//                    Form form = Display.getInstance().getCurrent();
//                    if (form != null) {
//                        form.revalidateWithAnimationSafety();
//                    }
//                    return; //changed the dropContainer, no need to do anything more
//                }
//            });
//        addDragOverListener((e) -> {
//        if (false)
//            addPointerDraggedListener(this);
//        else
//</editor-fold>
        //NB!!! dragOverListener is called on the **DRAGGED** object (not the dropTarget as I originally assumed)
        addDragOverListener((e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
            /**
             * e.getSource() == dragged component e.getComponent() == dropTo
             * component, calculated in Component.pointerDragged(x,y) using
             * findDropTarget(this, x, y)
             */
//            Component drag = e.getDraggedComponent();
            if (false) {
                Type eventType = e.getEventType();
                if (eventType == Type.PointerDrag) {
                    ASSERT.that(e.getDropTarget() == null);
                    return;
                }
            }

//                Component dragged = e.getDraggedComponent(); //(Component) e.getSource()
//</editor-fold>
//            ASSERT.that(false); //trace where dragOverListener is being called from
            ASSERT.that(e.getDraggedComponent() == this);
            Component dropTarget = e.getDropTarget(); //may be null (in Component.dragFinishedImpl(int x, int y))

            int x = e.getX(); //x is absolute! (screen) coordinate
            int yRel = e.getY(); //y is absolute! (screen) coordinate

            Component source = (Component) e.getSource();
//            int sourceAbsY = source.getAbsoluteY();
            //use algo from Component.getAbsoluteY();
            int yAbs;
            if (false) {
                yAbs = yRel - source.getScrollY();
                Container parent = source.getParent();
                if (parent != null) {
                    yAbs += parent.getAbsoluteY();
//                Log.p("e.getX()=" + e.getX() + ", e.getY()=" + e.getY() + ", yAbs=" + yAbs + ", Calculating y: e.getY()=" + e.getY() + ", source.getScrollY()=" + source.getScrollY() + ", parent.getAbsoluteY()=" + parent.getAbsoluteY());
                }
            } else {
                yAbs = yRel;
            }
//            else                 Log.p("e.getX()=" + e.getX() + ", e.getY()=" + e.getY() + ", yAbs=" + yAbs + ", Calculating y: e.getY()=" + e.getY() + ", source.getScrollY()=" + source.getScrollY() + " [source.getParent==null]");

            if (false) {
                e.consume(); //why needed? does this prevent swipeable to work?!
            }
            ASSERT.that(dropTarget != null, "DROPTARGET == null???");
            if (dropTarget == null) {//happens on dragFinished
                System.out.print("dropTarget=null=>return ");
                return;
            }

            //update drop position (NORMAL, SUBTASK, SUPERTASK):
//            InsertPositionType oldInsertPosition = newInsertPosition;
//            lastInsertPosition = newInsertPosition;
//            lastDropPlaceholderInsertPosition = newInsertPosition;
//            InsertPositionType newInsertPosition = insertPosition(x);
            newInsertPosition = insertPosition(x);
            if (false && Config.TEST_DRAG_AND_DROP) {
                Log.p("dropPos=" + newInsertPosition);
            }
            if (newInsertPosition == lastDropPlaceholderInsertPosition && dropTarget == lastDropTarget) {
                return; //if we're still above the same target, and in the same newInsertPosition, do nothing
            }//            lastInsertPosition = newInsertPosition;
            lastDropTarget = dropTarget;

//            DragDirection oldDragDirection = lastDragDirection;
            lastDragDirection = calcDragDirection(yAbs);

//                MyDragAndDropSwipeableContainer draggedMyDDCont = (MyDragAndDropSwipeableContainer) dragged;
            ASSERT.that(e.getDraggedComponent() == this, "dragged is NOT the same as this???");
//                assert draggedMyDDCont == this;

            /*            Since dropPlaceholder may be narrower on left-side (due to indentation for expanded subtasks) than full screen width, 
            the dropTarget may be the Form (or whatever).
            So check if y value is within the upper&lower y-bounds of the dropTarget and if so set the dropTarget directly. 
            TODO!!! Use this approach systematically for simplicity?!                 */
//<editor-fold defaultstate="collapsed" desc="comment">
//<editor-fold defaultstate="collapsed" desc="comment">
//            int tempY = draggedMyDDCont.dropPlaceholder != null ? draggedMyDDCont.dropPlaceholder.getAbsoluteY() : -1;
//            int tempYY = draggedMyDDCont.dropPlaceholder.getY();
//            int tempYAbs = draggedMyDDCont.dropPlaceholder.getAbsoluteY();
//            int tempYInner = draggedMyDDCont.dropPlaceholder.getInnerY();
//            int tempYOuter = draggedMyDDCont.dropPlaceholder.getOuterY();
//            int tempYScroll = draggedMyDDCont.dropPlaceholder.getScrollY();
//            int tempH = draggedMyDDCont.dropPlaceholder.getHeight();
//            int tempYH = tempY + tempH;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false && Config.TEST && draggedMyDDCont.dropPlaceholder != null) Log.p("Drag - "
//                            + ((y >= draggedMyDDCont.dropPlaceholder.getAbsoluteY() && y <= draggedMyDDCont.dropPlaceholder.getAbsoluteY() + draggedMyDDCont.dropPlaceholder.getHeight())
//                            ? "ABS-OVER    dropPlaceholder"
//                            : (y >= draggedMyDDCont.dropPlaceholder.getY() && y <= draggedMyDDCont.dropPlaceholder.getY() + draggedMyDDCont.dropPlaceholder.getHeight())
//                            ? " XY-OVER    dropPlaceholder" : "outside dropPlaceholder")
//                            + "; y=" + y
//                            + "; ph.absY=" + draggedMyDDCont.dropPlaceholder.getAbsoluteY()
//                            + "; ph.absY+H=" + (draggedMyDDCont.dropPlaceholder.getAbsoluteY() + draggedMyDDCont.dropPlaceholder.getHeight())
//                            + "; ph.Y=" + draggedMyDDCont.dropPlaceholder.getY()
//                            + "; ph.Y+H=" + (draggedMyDDCont.dropPlaceholder.getY() + draggedMyDDCont.dropPlaceholder.getHeight())
//                            + "; ph.height=" + draggedMyDDCont.dropPlaceholder.getHeight()
//                    );
//                dropTarget = e.getDropTarget();
//                Component dropTo = findDropTarget(this, x, y);
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
            if (false) {
                Component dropTargetTmp = null;
                Form f = getComponentForm();
                if (f != null) {
                    dropTargetTmp = f.findDropTargetAt(x, yAbs);
                    if (false && Config.TEST && dropTargetTmp != null) {
                        Log.p("DROPTARGET found using f.findDropTargetAt(x, y), =" + dropTargetTmp.getName());
                    }
                    if (dropTargetTmp == null && dropPlaceholder != null
                            && yAbs >= dropPlaceholder.getAbsoluteY()
                            && yAbs <= dropPlaceholder.getAbsoluteY() + dropPlaceholder.getHeight()) {
                        if (Config.TEST) {
                            Log.p("DROPTARGET set to dropPlaceholder based on y coordinates, =" + dropPlaceholder.getName());
                        }
                        dropTargetTmp = dropPlaceholder;
                    }
                }
                dropTarget = dropTargetTmp;
            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dropPlaceholder != null//) {
//                        && y >= dropPlaceholder.getAbsoluteY()
//                        && y <= dropPlaceholder.getAbsoluteY() + dropPlaceholder.getHeight()) {
//                    if (false && Config.TEST) Log.p("DROPTARGET selected based on y coordinate, =" + dropPlaceholder);
//                    dropTarget = dropPlaceholder;
//                } else {
//                    dropTarget = e.getDropTarget();
////                Log.p("DropTarget set to e.getDropTarget()");
//                }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (draggedMyDDCont.dropPlaceholder != null
//                    && draggedMyDDCont.dropPlaceholder.getY() >= y
//                && draggedMyDDCont.dropPlaceholder.getY() <= draggedMyDDCont.dropPlaceholder.getY() + draggedMyDDCont.dropPlaceholder.getHeight()) {
//                dropTarget = draggedMyDDCont.dropPlaceholder;
//            }

//            Component dropTarget = this;
//            Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------");
//            MyDragAndDropSwipeableContainer dragged = null;
//            if (false && !(drag instanceof MyDragAndDropSwipeableContainer)) {
////                Log.p("***addDragOverListener: drag (" + drag.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
//                return;
//            } //else {
//            if (false && !(dropTarget instanceof MyDragAndDropSwipeableContainer)) {
////                Log.p("***addDragOverListener: dropTarget (" + dropTarget.getName() + ") NOT instanceof MyDragAndDropSwipeableContainer");
//                return;
//            } //else {
//            if (false) {
//                e.consume();
//            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            //update drop position (NORMAL, SUBTASK, SUPERTASK):
////            InsertPositionType oldInsertPosition = newInsertPosition;
//            newInsertPosition = newInsertPosition(x);
//                        if (newInsertPosition==lastInsertPosition&& dropTarget == lastDropTarget)
//                return; //if we're still above the same target, and in the samedo nothing
//            lastDropTarget = dropTarget;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                InsertPositionType newInsertPosition = newInsertPosition(x);
//                if (newInsertPosition != newInsertPosition) {
//                    if (Config.TEST) Log.p("InsertPos=" + newInsertPosition);
//
////                refreshDropPlaceholderContainer(newInsertPosition, newInsertAsType);
//                    refreshDropPlaceholderContainer(newInsertPosition);
////                formNeedRefresh = true;
////                Log.p("InsertPositionType now = " + newInsertAsType+" (before="+(newInsertPosition!=null?newInsertPosition.toString().toLowerCase():"<null>")+")", Log.DEBUG);
//                    newInsertPosition = newInsertPosition;
//                    Form form = Display.getInstance().getCurrent();
//                    if (form != null) {
//                        form.revalidateWithAnimationSafety();
//                    }
//                    System.out.print("newInsertPosition=null=>return ");
//                    return; //updated the dropContainer, no need to do anything more
//                }
//            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (dragged == dropTarget || dragged.lastDraggedOver == dropTarget) { //over myself or same as on last call to dragOver listener
            if (false && dropTarget == lastDraggedOverXXX) { //over myself or same as on last dropActionCall to listener
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
            if (false && !(dropTarget instanceof MyDragAndDropSwipeableContainer)) { //if we're over a dropTarget, store that as lastDraggedOver
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("addDragOverListener: dragged.lastDraggedOver = " + (dropTarget != null ? dropTarget.getName() : "<null>"), Log.DEBUG);
                }
                lastDraggedOverXXX = dropTarget;
//                refreshDropPlaceholderContainer(x);
                return;
            } //else {
            if (false && dropTarget instanceof MyDragAndDropSwipeableContainer) { //store last encountered MyDragAndDropSwipeableContainer for use when dragging outside MyDD
//                lastDraggedOverMyDD = (MyDragAndDropSwipeableContainer) dropTarget;
            }
            if (false && Config.TEST_DRAG_AND_DROP && dropTarget != lastDraggedOverXXX) { //store last encountered MyDragAndDropSwipeableContainer for use when dragging outside MyDD
                Log.p("Dragged: new dropTarget=" + dropTarget, Log.DEBUG);
                lastDraggedOverXXX = dropTarget;
            }
//over a new container
            if (false && Config.TEST_DRAG_AND_DROP) {
                Log.p("----------------START MyDragAndDropSwipeableContainer ------------------------------------", Log.DEBUG);
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            int oldDropPlaceholderIndex = -1; //used to keep track of whether we drag upwards or downwards (to insert new dropPlaceholder in right position)
//first time we drag over another element than dragged:
//                if (false && !draggedMyDDCont.isHidden()) { //dragged.draggedWidth == -1) {// && dragged!=dragged.lastDraggedOver) {
////                    oldDropPlaceholderIndex=dragged.getParent().getComponentIndex(dragged); //initialize oldIndex to position of dragged
//                    draggedMyDDCont.draggedWidth = draggedMyDDCont.getWidth();
//                    draggedMyDDCont.draggedHeight = draggedMyDDCont.getHeight();
//                    dragImage2 = getDragImage(); //save dragImage before hiding
//                    if (Config.TEST_DRAG_AND_DROP) Log.p(">>>dragging of " + draggedMyDDCont.getName() + " started (setHidden);  dropTarget= " + dropTarget.getName(), Log.DEBUG);
//                    draggedMyDDCont.setHidden(true); //only hide once we've dragged on top of another object than dragged. Once we have the Height/Width, hide the component
////                    dragged.draggedOrgParent = drag.getParent();
////                    dragged.draggedOrgIndex = dragged.draggedOrgParent.getComponentIndex(drag);
////                    dragged.draggedOrgParent.removeComponent(drag); //remove the dragged component
//                }

//            if (true || dragged.lastDraggedOver != dropTarget) { // implicitly: || dragged.lastDraggedOver==null
//                if (Config.TEST_DRAG_AND_DROP) Log.p("****dropTarget = " + (dropTarget != null ? dropTarget.getName() : "<null>") + ", old dropTarget="
//                            + (draggedMyDDCont != null && lastDraggedOver != null ? lastDraggedOver.getName() : "<null>"), Log.DEBUG);
//</editor-fold>
            if (false && dropTarget != null) {
                lastDraggedOverXXX = dropTarget; //store every time we're above a new object (or NOT above if dropTarget==null)
            }//<editor-fold defaultstate="collapsed" desc="comment">
//<editor-fold defaultstate="collapsed" desc="comment">
//                return;
//            }
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
//            Form form = null;
//remove old dropPlaceholder (if any) - remove even if no new dropPosition since we don't want the old dropPlaceholder to hang as we drag further one
//</editor-fold>
//</editor-fold>

//remove *before* finding the before/after MyDDConts
//            if (false && dropPlaceholder != null) {
//                int dropPlaceholderY = 0;
//                dropPlaceholderY = dropPlaceholder.getAbsoluteY();
////                form = dragged.dropPlaceholder.getComponentForm();
////                draggedMyDDCont.dropPlaceholder.getParent().removeComponent(draggedMyDDCont.dropPlaceholder);
//                dropPlaceholder.remove();
////                    dropPlaceholder = null; //delete old dropPlaceholder
//                formNeedRefresh = true;
//            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                if (dropTarget != null && dropTarget == lastDropTarget) {
//                    System.out.print("dropTarget=lastDropTarget=>return ");
//                    return;
//                } else {
//                    if (Config.TEST_DRAG_AND_DROP) { //store last encountered MyDragAndDropSwipeableContainer for use when dragging outside MyDD
////                        Log.p("Dragging=" + this.getName(), Log.DEBUG);
//                        Log.p("New dropTarget=" + (dropTarget.getName() != null ? dropTarget.getName() : ("NoName=" + dropTarget)) + "; Dragging=" + this.getName(), Log.DEBUG);
//                        lastDropTarget = dropTarget;
//                    }
//                }
//            }
//</editor-fold>
//</editor-fold>
            MyDragAndDropSwipeableContainer beforeMyDDCont; //top-level container in treelist, *before* the dropPlaceholder (null if over first element in list)
            MyDragAndDropSwipeableContainer afterMyDDCont; //top-level container in treelist *after* the dropPlaceholder (null if over last element in list)
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dropTarget == lastDraggedOverXXX) { //test first to avoid that dropTarget == dropPlaceholder is true with both being null
//                    return;
//                } else
//                if (dropTarget == null) { //test first to avoid that dropTarget == dropPlaceholder is true with both being null
////                    beforeMyDDCont = null;
////                    afterMyDDCont = null;
////                    if (Config.TEST_DRAG_AND_DROP) {
////                        Log.p("1---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
////                        Log.p("1---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
////                    }
////                    Log.p(".");
//                    Log.p("dropTarget == null??", Log.DEBUG);
//                    return;
//                } else if (dropTarget == dropPlaceholder) { //we're (still) hovering over the dropPlaceholder, so nothing to do (dragging left/right and inserting as sub/super-task is handled above)
////                    Log.p("=");
//                    return;
////            } else if (dropTarget == dragged) { //initial situation, we're over the dragged element, so hide it an insert the dropPlaceholder
//                } else
//</editor-fold>
            if (dropTarget == this) { //initial situation, we're over the dragged element, so hide it an insert the dropPlaceholder
                ASSERT.that(dropTarget != this || dropPlaceholder == null, "dropPlaceholder should be null here, is=" + dropPlaceholder + "; dropTarget=" + dropTarget); //initial situation, no dropPlaceholder inserted yet
//<editor-fold defaultstate="collapsed" desc="comment">
//                beforeMyDDCont = findPrecedingMyDDCont(draggedMyDDCont, draggedMyDDCont); //<=> (dropTarget, dropTarget)
//                afterMyDDCont = findNextDDCont(draggedMyDDCont, draggedMyDDCont);
//                    beforeMyDDCont = findPrecedingMyDDCont(this, this); //<=> (dropTarget, dropTarget)
//                    afterMyDDCont = findNextDDCont(this, this);
//</editor-fold>
                beforeMyDDCont = findPrecedingMyDDCont(this, this); //<=> (dropTarget, dropTarget)
                afterMyDDCont = findNextDDCont(this, this);
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("I---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                    Log.p("I---after  (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                }

                if (!isHidden()) {
                    draggedWidth = getWidth();
                    draggedHeight = getHeight();
                    dragImage2 = getDragImage(); //save dragImage before hiding
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p(">>>dragging of " + getName() + " started (setHidden);  dropTarget= " + dropTarget.getName(), Log.DEBUG);
                    }
                    setHidden(true); //only hide once we've dragged on top of another object than dragged. Once we have the Height/Width, hide the component
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                } else {
//                    //if outside elements, eg below the last element or above the first, then remove dropPlaceholder
//                    //if outside elements, eg below the last element or above the first
////                if (y <= draggedMyDDCont.lastDraggedOverMyDD.getAbsoluteY()) { //if we're dragging *above* the list
////                if (y <= draggedMyDDCont.lastDraggedOverMyDD.getAbsoluteY()) { //if we're dragging *above* the list
//                    if (false && lastDraggedOver != null) {
//                        if (y <= lastDraggedOver.getAbsoluteY()) { //if we're dragging *above* the list
//                            beforeMyDDCont = null;
////                    afterMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                            afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("N---before (calc)= <null>", Log.DEBUG);
//                                Log.p("N---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            }
//                        } else { //we're dragging *below* the list
////                    beforeMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                            beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                            afterMyDDCont = null;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("0---before       = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                                Log.p("0---after (calc) = <null>", Log.DEBUG);
//                            }
//                        }
//                    }
//                    if (lastDragDirection==UP) {
//                            beforeMyDDCont = null;
////                    afterMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                            afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("N---before (calc)= <null>", Log.DEBUG);
//                                Log.p("N---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            }
//                        } else { //we're dragging *below* the list
////                    beforeMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                            beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                            afterMyDDCont = null;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("0---before       = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                                Log.p("0---after (calc) = <null>", Log.DEBUG);
//                            }
//                        }
//</editor-fold>
            } else if (dropTarget == dropPlaceholder) { //we're over the dropPlaceholder, so only update it if the horizontal drop insertPosition changes (drop as Subtask/SUpertask/Normal)
                ASSERT.that(dropTarget != this || dropPlaceholder == null, "dropPlaceholder should be null here, is=" + dropPlaceholder + "; dropTarget=" + dropTarget); //initial situation, no dropPlaceholder inserted yet
//<editor-fold defaultstate="collapsed" desc="comment">
//                beforeMyDDCont = null;
//                afterMyDDCont = null;
//                if (false) {
//                    beforeMyDDCont = findPrecedingMyDDCont(dropPlaceholder, this); //<=> (dropTarget, dropTarget)
//                    afterMyDDCont = findNextDDCont(dropPlaceholder, this);
//                    if (Config.TEST_DRAG_AND_DROP) {
//                        Log.p("1---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        Log.p("1---after  (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                    }
//                }
//</editor-fold>
                if (newInsertPosition != lastDropPlaceholderInsertPosition) { //due to earlier test in whether insertPosition has changed if we're over placeholder, newInsertPosition != currentDropPlaceholderInsertPosition will always be true here
//                ASSERT.that(newInsertPosition != lastDropPlaceholderInsertPosition);//due to earlier test in whether insertPosition has changed if we're over placeholder, newInsertPosition != currentDropPlaceholderInsertPosition will always be true here
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("InsertPos=" + newInsertPosition);
                    }
                    if (dropPlaceholder != null) {
                        refreshDropPlaceholderContainer(newInsertPosition, dropPlaceholder);
                    }
//                    Log.p("1a--before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                    Log.p("1a--after  (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                    if (true) {
//                        Form form = Display.getInstance().getCurrent();
                        Form form = getComponentForm();
                        if (form != null) {
                            form.revalidateWithAnimationSafety();
                        }
                    } else {
                        dropPlaceholder.getParent().animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
                    }
//                    formNeedRefresh = true;
                    lastDropPlaceholderInsertPosition = newInsertPosition; //keep track of where dropPlaceholder is situated
                }
                return;
            } else { //dropTarget != null && dropTarget != dropPlaceholder && dropTarget != this
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (dropTarget.getAbsoluteY() > dropPlaceholder.getAbsoluteY()) { //no need to use absolute coordinates here (dropTarget.getAbsoluteY()<dropPlaceholder.getAbsoluteY())
//                    if (dropTarget.getAbsoluteY() > dropPlaceholder.getAbsoluteY()) { //no need to use absolute coordinates here (dropTarget.getAbsoluteY()<dropPlaceholder.getAbsoluteY())
//                        dropPlaceholder.remove(); //remove befaure start searching the container hierarchy below
//                        //(new) dropTarget is lower down the screen (higher Y), so dragging down, onto a new component
//                        beforeMyDDCont = findPrecedingMyDDCont((MyDragAndDropSwipeableContainer) dropTarget, draggedMyDDCont); //
//                        afterMyDDCont = dropTarget instanceof MyDragAndDropSwipeableContainer ? (MyDragAndDropSwipeableContainer) dropTarget : null;
//                        if (Config.TEST_DRAG_AND_DROP) {
//                            Log.p("2---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            Log.p("2---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        }
//                    } else {
//                        dropPlaceholder.remove(); //remove befaure start searching the container hierarchy below
//                        //dragging downwards
//                        beforeMyDDCont = dropTarget instanceof MyDragAndDropSwipeableContainer ? (MyDragAndDropSwipeableContainer) dropTarget : null;
////                afterMyDDCont = findCont(beforeMyDDCont, false);
//                        afterMyDDCont = findNextDDCont(beforeMyDDCont, draggedMyDDCont);
//                        if (Config.TEST_DRAG_AND_DROP) {
//                            Log.p("3---before      = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            Log.p("3---after (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        }
//                    }
//</editor-fold>
                if (dropTarget instanceof MyDragAndDropSwipeableContainer) {
                    if (lastDragDirection != DOWN) { //==UP or NONE; no need to use absolute coordinates here (dropTarget.getAbsoluteY()<dropPlaceholder.getAbsoluteY())
                        dropPlaceholder.remove(); //remove before start searching the container hierarchy below
                        formNeedRefresh = true;
                        //(new) dropTarget is lower down the screen (higher Y), so dragging down, onto a new component
                        beforeMyDDCont = findPrecedingMyDDCont((MyDragAndDropSwipeableContainer) dropTarget, this); //
//                        afterMyDDCont = dropTarget instanceof MyDragAndDropSwipeableContainer ? (MyDragAndDropSwipeableContainer) dropTarget : null;
                        afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("2---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                            Log.p("2---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                        }
                    } else {
                        ASSERT.that(lastDragDirection == DOWN, "WRONG dragDirection, dir=" + lastDragDirection + "; dropTarget=" + dropTarget + "; lastDropTarget=" + lastDropTarget);
                        dropPlaceholder.remove(); //remove before start searching the container hierarchy below
                        formNeedRefresh = true;
                        //dragging downwards
//                        beforeMyDDCont = dropTarget instanceof MyDragAndDropSwipeableContainer ? (MyDragAndDropSwipeableContainer) dropTarget : null;
                        beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
//                afterMyDDCont = findCont(beforeMyDDCont, false);
                        afterMyDDCont = findNextDDCont(beforeMyDDCont, this);
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("3---before      = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                            Log.p("3---after (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
                        }
                    }
                } else {
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("4--- dropTarget NOT instanceof MyDragAndDropSwipeableContainer, dropTarget=" + dropTarget, Log.DEBUG);
                    }
                    beforeMyDDCont = null;
                    afterMyDDCont = null;
                }
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//            boolean draggingUpwardsOrOverInitialDraggedEltPosition;
//            DragDirection draggingUpwardsOrOverInitialDraggedEltPosition;
//            if (treeList != null) {
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
//<editor-fold defaultstate="collapsed" desc="comment">
//            draggingUpwardsOrOverInitialDraggedEltPosition = movingUpwardsOrOverDragged((MyDragAndDropSwipeableContainer) dropTarget);
//            draggingUpwardsOrOverInitialDraggedEltPosition = movingUpwardsOrOverDragged(y);
//            if (draggingUpwardsOrOverInitialDraggedEltPosition != DragDirection.NONE
//                    && lastDragDirectionUp != draggingUpwardsOrOverInitialDraggedEltPosition) {
////                if (Config.TEST_DRAG_AND_DROP) Log.p("dragDirection: Now dragging " + (draggingUpwardsOrOverInitialDraggedEltPosition ? "UP" : "DOWN"));
//                if (Config.TEST_DRAG_AND_DROP) Log.p("dragDirection: Now dragging " + draggingUpwardsOrOverInitialDraggedEltPosition);
//                lastDragDirectionUp = draggingUpwardsOrOverInitialDraggedEltPosition;
//            }
//            if (dropTarget == null) {
//                //if outside elements, eg below the last element or above the first
////                if (dragged.lastDraggedOverMyDD != null) { //NOT needed, can never be null here
//                if (y <= draggedMyDDCont.lastDraggedOverMyDD.getAbsoluteY()) { //if we're dragging *above* the list
//                    beforeMyDDCont = null;
//                    afterMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                } else { //we're dragging *below* the list
//                    beforeMyDDCont = (MyDragAndDropSwipeableContainer) draggedMyDDCont.lastDraggedOver;
//                    afterMyDDCont = null;
////                    Component closest = getClosestComponentTo(x, y);
//                }
////                }
//            } else if (draggedMyDDCont == dropTarget) { //initial situation: dragged element is over itself
//                beforeMyDDCont = findPrecedingMyDDCont(draggedMyDDCont, draggedMyDDCont);
//                afterMyDDCont = findNextDDCont(draggedMyDDCont, draggedMyDDCont);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("1---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("1---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//            } else if (draggingUpwardsOrOverInitialDraggedEltPosition) {
//                afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
////                beforeMyDDCont = findCont(afterMyDDCont, true);
//                beforeMyDDCont = findPrecedingMyDDCont(afterMyDDCont, draggedMyDDCont);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("2---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("2---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//            } else { //dragging downwards
//                beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
////                afterMyDDCont = findCont(beforeMyDDCont, false);
//                afterMyDDCont = findNextDDCont(beforeMyDDCont, draggedMyDDCont);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("3---before      = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                if (Config.TEST_DRAG_AND_DROP) Log.p("3---after (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//            }
//
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="collapsed old impl">
//                if (false) {
//                    if (dropTarget == null) {
//                        //if outside elements, eg below the last element or above the first
////                    if (y <= draggedMyDDCont.lastDraggedOverMyDD.getAbsoluteY()) { //if we're dragging *above* the list
//                        if (y <= lastDraggedOver.getAbsoluteY()) { //if we're dragging *above* the list
//                            beforeMyDDCont = null;
//                            afterMyDDCont = (MyDragAndDropSwipeableContainer) lastDraggedOver;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("N---before (calc)= <null>", Log.DEBUG);
//                                Log.p("N---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            }
//                        } else { //we're dragging *below* the list
//                            beforeMyDDCont = (MyDragAndDropSwipeableContainer) lastDraggedOver;
//                            afterMyDDCont = null;
//                            if (Config.TEST_DRAG_AND_DROP) {
//                                Log.p("0---before       = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                                Log.p("0---after (calc) = <null>", Log.DEBUG);
//                            }
//                        }
//                    } else if (dropTarget == draggedMyDDCont) { //initial situation: dragged element is over itself
//                        beforeMyDDCont = findPrecedingMyDDCont(draggedMyDDCont, draggedMyDDCont);
//                        afterMyDDCont = findNextDDCont(draggedMyDDCont, draggedMyDDCont);
//                        if (Config.TEST_DRAG_AND_DROP) {
//                            Log.p("1---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            Log.p("1---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        }
////                 } else if (draggingUpwardsOrOverInitialDraggedEltPosition) {
//                    } else if (dropTarget.getAbsoluteY() < lastDraggedOver.getAbsoluteY()) {
//                        afterMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
////                beforeMyDDCont = findCont(afterMyDDCont, true);
//                        beforeMyDDCont = findPrecedingMyDDCont(afterMyDDCont, draggedMyDDCont);
//                        if (Config.TEST_DRAG_AND_DROP) {
//                            Log.p("2---before (calc)= \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            Log.p("2---after        = \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        }
//                    } else { //dragging downwards
//                        beforeMyDDCont = (MyDragAndDropSwipeableContainer) dropTarget;
////                afterMyDDCont = findCont(beforeMyDDCont, false);
//                        afterMyDDCont = findNextDDCont(beforeMyDDCont, draggedMyDDCont);
//                        if (Config.TEST_DRAG_AND_DROP) {
//                            Log.p("3---before      = \"" + (beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                            Log.p("3---after (calc)= \"" + (afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : "<null>"), Log.DEBUG);
//                        }
//                    }
//                }
//</editor-fold>
            ItemAndListCommonInterface draggedElement = getDragAndDropObject();
            ItemAndListCommonInterface beforeElement = beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropObject() : null;
            ItemAndListCommonInterface afterElement = afterMyDDCont != null ? afterMyDDCont.getDragAndDropObject() : null;

            Category draggedItemsCategory = getDragAndDropCategory();
            Category beforeCategory = beforeMyDDCont != null ? beforeMyDDCont.getDragAndDropCategory() : null; // : (beforeElement instanceof Category ? (Category) beforeElement : null);
            Category afterCategory = afterMyDDCont != null ? afterMyDDCont.getDragAndDropCategory() : null; // : (afterElement instanceof Category ? (Category) afterElement : null);
//<editor-fold defaultstate="collapsed" desc="comment">
//            Category newCategory; //new category for a dragged igem
//            if (beforeCategory != null)
//                newCategory = beforeCategory; //drop right after a Category
////            else if (beforeElement instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null)
////                newCategory = beforeMyDDCont.getDragAndDropCategory();
//            else if (beforeElement instanceof Category) //drop after an Item expanded below a Category, or a subtask of an Item expanded below a Category
//                newCategory = (Category) beforeElement;
//            else if (findPrecedingCategory(beforeMyDDCont) != null) //drop after an Item expanded below a Category, or a subtask of an Item expanded below a Category
//                newCategory = findPrecedingCategory(beforeMyDDCont);
//            else
//                newCategory = null;

            //            MyDragAndDropSwipeableContainer dropDD = ((MyDragAndDropSwipeableContainer) dropTarget);
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

            if (draggedElement instanceof Category) { ////********** dragging a *Category* **********
//<editor-fold defaultstate="collapsed" desc="Category dragged">
//                        if (afterCont != null && (afterCont.getDragAndDropObject() instanceof Category || afterCont.getDragAndDropObject() == null)) { //can always drop a Category before another Category
//                if ((beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Category)) { //can always drop a Category before another Category
                if (beforeElement instanceof Item) {
                    if (afterElement instanceof Category) {
                        //insert a dragged category before a Category (and after and expanded item of the preceding Category
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("-INSERT00a Cat \"" + draggedElement.getText() + "\" before Cat \"" + afterElement + "\"", Log.DEBUG);
                        }
                        dropActionCall = () -> {
                            ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) draggedElement).getOwner();
//                        moveItemOrItemListAndSave(categoryOwnerList, (Category) draggedElement, afterElement, false); 
                            moveItemOrItemListAndSave(categoryOwnerList, (Category) draggedElement, afterElement, false);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
                    } else if (afterMyDDCont == null) {
                        //insert a dragged category after the last element in the list (expanded item of the preceding Category)
                        //insert a dragged category 
                        MyDragAndDropSwipeableContainer beforeCategoryCont = findMyDDContAboveHoldingCategory(beforeMyDDCont);
//                        Category newCat = beforeCategoryCont != null ? (Category) beforeCategoryCont.getDragAndDropObject() : null;
                        Category newCat = beforeCategoryCont != null
                                ? (beforeCategoryCont.getDragAndDropObject() instanceof Category
                                ? (Category) beforeCategoryCont.getDragAndDropObject()
                                : beforeCategoryCont.getDragAndDropCategory()) : null;
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("-INSERT00b Cat \"" + draggedElement.getText() + "\" after Cat \"" + afterElement + "\"", Log.DEBUG);
                        }
                        dropActionCall = () -> {
                            ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) draggedElement).getOwner();
                            moveItemOrItemListAndSave(categoryOwnerList, (Category) draggedElement, newCat, true);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(beforeCategoryCont, dropPh, 1);
                        };
                    }
                } else if (beforeElement instanceof Category && (afterElement instanceof Category || afterMyDDCont == null)) {
//<editor-fold defaultstate="collapsed" desc="drop a Category after another Category if the next is a Category or null (but NOT if next is eg Item!)">
//can drop a Category after another Category if the next is a Category or null (but NOT if next is eg Item!)
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT01 Cat \"" + draggedElement.getText() + "\" after Cat \"" + beforeElement + "\"", Log.DEBUG);
                    }

                    dropActionCall = () -> {
////                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) getDragAndDropObject()).getOwner();
                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) draggedElement).getOwner();
////                        int indexCatList = categoryOwnerList.getItemIndex((ItemAndListCommonInterface) beforeMyDDCont.getDragAndDropObject()) + 1;
//                        int insertIndex = categoryOwnerList.getItemIndex(beforeElement) + 1;
////                        moveCategoryAndSave(categoryOwnerList, (Category) getDragAndDropObject(), indexCatList); //+ addOneOnDownwardsDrag
//                        moveCategoryAndSave(categoryOwnerList, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(categoryOwnerList, (Category) draggedElement, beforeElement, true); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            afterCont.getParent().addComponent(afterCont.getParent().getComponentIndex(afterCont), dropPh);
//                            findParentContForDropPlaceholder(afterCont).addComponent(afterCont.getParent().getComponentIndex(afterCont), dropPh);
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };
//                    } else if (afterMyDDCont == null) {
//                } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Category) { //drop *before* an ItemList
//</editor-fold>
                } else if (afterElement instanceof Category) { //drop *before* an Category (eg if previous category shows expanded subtasks
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT02 Cat \"" + draggedElement.getText() + "\" before Cat \"" + afterElement + "\"", Log.DEBUG);
                    }
                    dropActionCall = () -> {
////                        ItemAndListCommonInterface categoryOwnerList = (ItemAndListCommonInterface) ((Category) getDragAndDropObject()).getOwner();
                        ItemAndListCommonInterface categoryOwnerList = ((Category) draggedElement).getOwner();
////                        int indexCatList = categoryOwnerList.getItemIndex(((ItemList) afterMyDDCont.getDragAndDropObject()));
//                        int insertIndex = categoryOwnerList.getItemIndex(afterElement);
////                        moveCategoryAndSave(categoryOwnerList, (Category) getDragAndDropObject(), indexCatList); //+ addOneOnDownwardsDrag
//                        moveCategoryAndSave(categoryOwnerList, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(categoryOwnerList, (Category) draggedElement, beforeElement, false);
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            this.getParent().addComponent(this.getParent().getComponentCount(), dropPh); //add at the end of the container with the list of categories
                        addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                    };
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                else if (afterMyDDCont == null) {
//                    Log.p("-INSERT Cat \"" + draggedElement.getText() + "\" at end of Category list", Log.DEBUG);
//                    dropActionCall = () -> {
////                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
////                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
//                        int insertIndex = listOwner.size();
////                        moveCategoryAndSave(listOwner, (Category) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
//                        moveCategoryAndSave(listOwner, (Category) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
//                    };
//                    insertDropPlaceholder = (dropPh) -> {
//                        addDropPlaceholderToAppropriateParentCont(this, dropPh, Integer.MAX_VALUE); //'this' because we cannot get the container holding the categories from neither before, nor after
//                    };
//                }
//</editor-fold>
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
            } else if (draggedElement instanceof ItemList) { //********** dragging an *ItemList* **********
//<editor-fold defaultstate="collapsed" desc="ItemList dragged">
                //NB dropping as sub or superelement does not make sense for ItemLists (currently - TODO!! this will change once meta-lists are introduced)
//                    if (afterCont != null && afterCont.getDragAndDropObject() instanceof ItemList) { //drop *before* an ItemList
//                if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof ItemList) { //drop *after* an ItemList
                if (beforeElement instanceof ItemList) { //drop *after* an ItemList //TODO!!!! even if expanded and showing subtasks???!!!
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT03 List \"" + draggedElement.getText() + "\" after List \"" + beforeElement + "\"", Log.DEBUG);
                    }
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemAndListCommonInterface) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
//                        int insertIndex = listOwner.getItemIndex(beforeElement) + 1; //+1 drop at position *after* beforeItem
////                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemAndListCommonInterface) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
//                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, draggedElement, beforeElement, true); //+ addOneOnDownwardsDrag
//                        moveItemOrItemListAndSave(draggedElement, beforeElement, true); //true:insert *after* beforeElement
//                        xx;
                    };
                    insertDropPlaceholder = (dropPh) -> {
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };

//                    } else if (afterCont == null) { //drop after last element in list (even if it's an Item from an expanded ItemList)
//                } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof ItemList) { //drop *before* an ItemList
                } else if (afterElement instanceof ItemList) { //drop *before* an ItemList
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT04 List \"" + draggedElement.getText() + "\" before List \"" + beforeElement + "\"", Log.DEBUG);
                    }
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = ((ItemList) draggedElement).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
////                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
////                        int indexItem = listOwner.getItemIndex(((ItemList) afterMyDDCont.getDragAndDropObject()));
//                        int insertIndex = listOwner.getItemIndex(afterElement);
////                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemList) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
//                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, draggedElement, afterElement, false); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                            addParentContForDropPlaceholder(this, dropPh, Integer.MAX_VALUE);
                        addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                    };
                } else if (afterMyDDCont == null) { //drop at the end of the list (even if beforeElement is not an ItemList)
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT05 List \"" + draggedElement.getText() + "\" at end of ItemList list", Log.DEBUG);
                    }
                    dropActionCall = () -> {
//                        ItemAndListCommonInterface listOwner = ((ItemList) getDragAndDropObject()).getOwner(); //currenly only one single ItemListList to which all ItemLists belong
                        ItemAndListCommonInterface listOwner = draggedElement.getOwner(); //currenly only one single ItemListList to which all ItemLists belong
////                            int indexItem = listOwner.size(); // insert at the end of ItemListList --listOwner.getItemIndex((ItemList) beforeCont.getDragAndDropObject()); //+1 drop at position *after* beforeItem
//                        int insertIndex = listOwner.getSize();
////                        moveItemOrItemListAndSave(listOwner, listOwner, (ItemList) getDragAndDropObject(), indexItem); //+ addOneOnDownwardsDrag
//                        moveItemOrItemListAndSave(listOwner, listOwner, draggedElement, insertIndex); //+ addOneOnDownwardsDrag
                        moveItemOrItemListAndSave(listOwner, draggedElement, true); //+ addOneOnDownwardsDrag
                    };
                    insertDropPlaceholder = (dropPh) -> {
                        addDropPlaceholderToAppropriateParentCont(this, dropPh, Integer.MAX_VALUE);
                    };
                }
//</editor-fold>
//            } else if (getDragAndDropObject() instanceof Item) {
            } else if (draggedElement instanceof Item) { //************************ dragging an *Item* *****************************
                if (beforeElement instanceof Category) {
///<editor-fold defaultstate="collapsed" desc="moving to a new category: dropping item right after a Category (either not expanded (next is not an Item) or last in list => move to that category">
                    //insert as first element in Category
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT06a0 Item \"" + draggedElement.getText() + "\" in Cat \"" + beforeElement + "\", remove from Cat \"" + draggedItemsCategory + "\"", Log.DEBUG);
                    }
                    //dropping item right after a Category => move to that category

                    if (afterElement instanceof Item) { //Category is expanded
                        dropActionCall = () -> {
                            moveItemBetweenCategoriesAndSave(draggedItemsCategory, (Category) beforeElement, (Item) draggedElement, (Item) afterElement, false);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
                    } else {//Category is NOT expanded
                        dropActionCall = () -> {
                            moveItemBetweenCategoriesAndSave(draggedItemsCategory, (Category) beforeElement, (Item) draggedElement, null, !MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean());
                            expandSubtasks(beforeElement); //expand Category to show new element
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement); //expand category beforeElement (if not already the case)
                        };
                    }

//</editor-fold>
                } else if (false && beforeElement instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null) {
///<editor-fold defaultstate="collapsed" desc="moving to after an Item in a category: dropping item right after a Category (either not expanded (next is not an Item) or last in list => move to that category">
//                if (newCategory != null) {
//                        || (beforeElement instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null)
//                        || (beforeElement instanceof Item && findMyDDContStartingFrom(beforeMyDDCont) != null
//                        && findMyDDContStartingFrom(beforeMyDDCont).getDragAndDropCategory() != null)) {// && !(afterElement instanceof Item)) { //WHY check that !(afterElement instanceof Item)??
//<editor-fold defaultstate="collapsed" desc="documentation">
                    /**
                     *
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 and add to Cat2 T1
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X
                     * Cat2 T1 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X A
                     * <- X - Normal: move/reorder X within Cat1 B ----- Cat1 T1
                     * S1 Cat2 T1 <- S1 - Normal: Add into Cat2, and add Cat2 to
                     * S1's categories. But S1 is NOT removed from Cat1 since it
                     * is not in Cat1 but unly a subtask to T1 which is in Cat1
                     * [anything] ----- Moving an item within the same Category,
                     * but possible dropping as subtask (meaning just add Cat1
                     * Cat2 ... X ... T1 S1 <- X - Normal: Add after sibling T1,
                     * stay in Cat2. Sup: not possible (no ??). Sub: make a
                     * subtask of S1, no change to X's existing category. T2 ...
                     * X ... Cat3 [anything] ----- Cat1 X T1 X Cat2 T1 S1 <- X -
                     * Normal: Move from previous owner to T1 subatsk after S1,
                     * no chg for categories. Sup: add to Cat2 (insert after
                     * beforeElt's owner T1) and remove from any previous
                     * category. Sub: make a subtask of S1, no change to X's
                     * existing category. Cat3 [anything] ----- Screen with
                     * tasks in Cat: * -----
                     *
                     */
//</editor-fold>
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT06a1 Item \"" + draggedElement.getText() + "\" in Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\", remove from Cat \"" + draggedItemsCategory + "\"", Log.DEBUG);
                    }
                    //dropping item right after a Category => move to that category
                    dropActionCall = () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//DONE!! //UI: if category is NOT expanded, should an item be inserted at the beginning of the subtask list or at the end?? At the beginning like if it was expanded? Create an setting
//insert item at beginning of category's list of items (or at the end if setting is false
//                            boolean insertAtHeadOfSubitemList = afterMyDDCont.getDragAndDropObject() instanceof Item //<=> category is expanded and afterCont is an Item
//                        boolean insertAtHeadOfSubitemList = afterElement instanceof Item //<=> category is expanded and afterCont is an Item
//                                || MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean();
//                        boolean insertAtEndOfCategory                                 = afterElement instanceof Item //<=> category is expanded and afterCont is an Item
//                                || MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean();
//                        //                            int indexItem = insertAtHeadOfSubitemList ? 0 : ((Category) beforeMyDDCont.getDragAndDropObject()).getList().size() + 1;
//                        int insertIndex = insertAtHeadOfSubitemList ? 0 : ((ItemAndListCommonInterface) beforeElement).getList().size();
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeMyDDCont.getDragAndDropObject(), (Item) getDragAndDropObject(), indexItem);
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeElement, (Item) draggedElement, insertAtHeadOfSubitemList);
//                        moveItemBetweenCategoriesAndSave(draggedCategory, beforeCategory, (Item) draggedElement, null, MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean());
//</editor-fold>
                        moveItemBetweenCategoriesAndSave(draggedItemsCategory, beforeCategory, (Item) draggedElement, null, MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean());
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                        addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };
                    if (true || beforeElement instanceof Item) { //we can insert an item as a subtask below a previous Item
                        //insert as subtask
                        dropAsSubtaskActionCall = () -> {
                            ItemAndListCommonInterface newOwnerPrj = beforeElement;
                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
                            expandSubtasks(newOwnerPrj); //expand to show list of subtasks to avoid that just dropped element 'disappears'
                        };
                        insertDropPlaceholderForSubtask = (dropPh) -> {
                            addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                        };
                    }
//</editor-fold>
                } else if (beforeElement instanceof Item && beforeCategory != null
                        && (!(afterElement instanceof Item) || afterElement.getOwner() != beforeElement)) { //do NOT change category if inserting an item between an Item and its expanded subtask(s) since that must insert it as a subtask
///<editor-fold defaultstate="collapsed" desc="moving an Item after another Item in a category (UNLESS the next element is subtask of that Item in which case the item is inserted as a subtask see below) => insert into that category (and remove from a possible precvious category)">
//<editor-fold defaultstate="collapsed" desc="documentation">
                    /**
                     *
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 and add to Cat2 T1
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X
                     * Cat2 T1 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X A
                     * <- X - Normal: move/reorder X within Cat1 B ----- Cat1 T1
                     * S1 Cat2 T1 <- S1 - Normal: Add into Cat2, and add Cat2 to
                     * S1's categories. But S1 is NOT removed from Cat1 since it
                     * is not in Cat1 but unly a subtask to T1 which is in Cat1
                     * [anything] ----- Moving an item within the same Category,
                     * but possible dropping as subtask (meaning just add Cat1
                     * Cat2 ... X ... T1 S1 <- X - Normal: Add after sibling T1,
                     * stay in Cat2. Sup: not possible (no ??). Sub: make a
                     * subtask of S1, no change to X's existing category. T2 ...
                     * X ... Cat3 [anything] ----- Cat1 X T1 X Cat2 T1 S1 <- X -
                     * Normal: Move from previous owner to T1 subatsk after S1,
                     * no chg for categories. Sup: add to Cat2 (insert after
                     * beforeElt's owner T1) and remove from any previous
                     * category. Sub: make a subtask of S1, no change to X's
                     * existing category. Cat3 [anything] -----
                     *
                     */
//</editor-fold>
                    if (Config.TEST_DRAG_AND_DROP) {
                        ASSERT.that(!(afterElement instanceof Item) || afterCategory == beforeCategory); //if next Item (afterElt) is NOT an expanded subtask, it should have the same category as the beforeElt
                    }
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT06b Item \"" + draggedElement.getText() + "\" in Cat \"" + beforeElement
                                + "\", remove from Cat \"" + getDragAndDropCategory() + "\"", Log.DEBUG);
                    }
                    dropActionCall = () -> {
                        //<editor-fold defaultstate="collapsed" desc="comment">
                        //                                    boolean sameCategory = getDragAndDropCategory() == beforeCont.getDragAndDropCategory();
                        //                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
                        //                                    ((Category) beforeCont.getDragAndDropObject()).addToList(0, (Item) getDragAndDropObject()); //insert item at beginning of category's list of items
                        //                                    DAO.getInstance().saveInBackground((ParseObject) getDragAndDropCategory(), (ParseObject) beforeCont.getDragAndDropObject(), (ParseObject) getDragAndDropObject());
                        //</editor-fold>
                        //DONE!! //UI: if category is NOT expanded, should an item be inserted at the beginning of the subtask list or at the end?? At the beginning like if it was expanded? Create an setting
                        //insert item at beginning of category's list of items (or at the end if setting is false
                        //                            boolean insertAtHeadOfSubitemList = afterMyDDCont.getDragAndDropObject() instanceof Item //<=> category is expanded and afterCont is an Item
                        moveItemBetweenCategoriesAndSave(draggedItemsCategory, beforeCategory, (Item) draggedElement, (Item) beforeElement, true); //true=insert *after* beforeElt
                    };
                    insertDropPlaceholder = (dropPh) -> {
//                        addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                    };

                    //insert as subtask
                    dropAsSubtaskActionCall = () -> {
//                        ItemAndListCommonInterface newOwnerPrj = beforeElement;
                        moveItemOrItemListAndSave(beforeElement, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
                        expandSubtasks(beforeElement); //expand to show list of subtasks to avoid that just dropped element 'disappears'
                    };
                    insertDropPlaceholderForSubtask = (dropPh) -> {
                        addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                    };

//                    } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item && afterMyDDCont.getDragAndDropCategory() != null) {
//</editor-fold>
                } else if (beforeElement instanceof ItemList) {
///<editor-fold defaultstate="collapsed" desc="dropping item right after a ItemList => move to that category/itemList">
//<editor-fold defaultstate="collapsed" desc="documentation">
                    /**
                     *
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 and add to Cat2 T1
                     * ----- Cat1 X Cat2 T1 <- X - Normal: Add into Cat2, and
                     * add Cat2 to T1's categories. Remove X from Cat1 T2 -----
                     * Cat1 X Cat2 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X
                     * Cat2 T1 <- X - Normal: Add into Cat2, and add Cat2 to
                     * T1's categories. Remove X from Cat1 Cat3 ----- Cat1 X A
                     * <- X - Normal: move/reorder X within Cat1 B ----- Cat1 T1
                     * S1 Cat2 T1 <- S1 - Normal: Add into Cat2, and add Cat2 to
                     * S1's categories. But S1 is NOT removed from Cat1 since it
                     * is not in Cat1 but unly a subtask to T1 which is in Cat1
                     * [anything] ----- Moving an item within the same Category,
                     * but possible dropping as subtask (meaning just add Cat1
                     * Cat2 ... X ... T1 S1 <- X - Normal: Add after sibling T1,
                     * stay in Cat2. Sup: not possible (no ??). Sub: make a
                     * subtask of S1, no change to X's existing category. T2 ...
                     * X ... Cat3 [anything] ----- Cat1 X T1 X Cat2 T1 S1 <- X -
                     * Normal: Move from previous owner to T1 subatsk after S1,
                     * no chg for categories. Sup: add to Cat2 (insert after
                     * beforeElt's owner T1) and remove from any previous
                     * category. Sub: make a subtask of S1, no change to X's
                     * existing category. Cat3 [anything] -----
                     *
                     */
//</editor-fold>
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT06c Item \"" + draggedElement.getText() + "\" into ItemList \"" + beforeElement
                                + "\", remove from "
                                + (draggedElement.getOwner() instanceof ItemList ? "ItemList " : (draggedElement.getOwner() instanceof Item ? "Item" : "???"))
                                + " \"" + draggedElement.getOwner() + "\"", Log.DEBUG);
                    }
                    //dropping item right after a ItemList => move to that ItemList
                    if (afterElement instanceof Item) { //ItemList is expanded
                        dropActionCall = () -> {
                            moveItemOrItemListAndSave((ItemList) beforeElement, (Item) draggedElement, afterElement, false); //false: insert before afterElement
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
                    } else {//ItemList is NOT expanded
                        dropActionCall = () -> {
                            moveItemOrItemListAndSave((ItemList) beforeElement, (Item) draggedElement, null, MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean());
                            expandSubtasks(beforeElement); //expand list to show new element
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                        };
                    }

                    dropAsSuperTaskActionCall = null; //does not make sense
//</editor-fold>
//                } else if (false && getDragAndDropCategory() != null) { //dragged object belongs to a category, so drag may move it to another category, but can also only reposition it within the same category
////<editor-fold defaultstate="collapsed" desc="DISABLED ---if dragging an Item inside a Category list">
//<editor-fold defaultstate="collapsed" desc="comment">
//                    //we can always drop *after* another 'before' Item which also has a Category (isn't triggered for expanded subtasks since they return null for getDragAndDropCategory())
////                    if (beforeMyDDCont != null && beforeMyDDCont.getDragAndDropObject() instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null) {
//                    if (beforeElement instanceof Item && beforeMyDDCont.getDragAndDropCategory() != null) {
//                        if (Config.TEST_DRAG_AND_DROP) Log.p("-INSERT07 Item \"" + draggedElement.getText() + "\" in Cat \"" + beforeMyDDCont.getDragAndDropCategory()
//                                    + "\", remove from Cat \"" + getDragAndDropCategory() + "\"", Log.DEBUG);
//                        dropActionCall = () -> {
////                            Category beforeCategory = beforeMyDDCont.getDragAndDropCategory();
//                            int insertIndex = beforeCategory.getItemIndex(beforeElement) + 1; //+1 drop at position *after* beforeItem
//                            moveItemBetweenCategoriesAndSave(draggedCategory, beforeCategory, (Item) draggedElement, insertIndex); //+ addOneOnDownwardsDrag
//                        };
//                        insertDropPlaceholder = (dropPh) -> {
//                            addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
//                        };
//                    } else if (beforeElement instanceof Category) {
//                        if (Config.TEST_DRAG_AND_DROP) Log.p("-INSERT08 Item \"" + draggedElement.getText() + "\" in Cat \"" + beforeElement
//                                    + "\", remove from Cat \"" + getDragAndDropCategory() + "\"", Log.DEBUG);
//                        //dropping item right after a Category => move to that category
//                        dropActionCall = () -> {
//                            //insert item at beginning of category's list of items (or at the end if setting is false
//                            boolean insertAtHeadOfSubitemList = afterElement instanceof Item //<=> category is expanded and afterCont is an Item
//                                    || MyPrefs.dropItemAtBeginningOfUnexpandedCategoryOrItemListSubtaskList.getBoolean();
//                            int insertIndex = insertAtHeadOfSubitemList ? 0 : ((Category) beforeElement).getList().size();
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), (Category) beforeElement, (Item) draggedElement, insertIndex);
//                        };
//                        if (afterElement instanceof Item) { //Category is already expanded and shows its tasks
//                            insertDropPlaceholder = (dropPh) -> {
//                                addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
//                            };
//                        } else { //Category not expanded
//                            insertDropPlaceholder = (dropPh) -> {
//                                addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
//                            };
//                        }
////                    } else if (afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item && afterMyDDCont.getDragAndDropCategory() != null) {
//                    } else if (afterElement instanceof Item && afterMyDDCont.getDragAndDropCategory() != null) {
//                        //we drop *before* another Item in a Category (eg when the subtasks of the previous item in the cateogry are expanded)
//                        if (Config.TEST_DRAG_AND_DROP) Log.p("-INSERT09 Item \"" + draggedElement.getText() + "\" in Cat \"" + afterMyDDCont.getDragAndDropCategory()
//                                    + "\", remove from Cat \"" + getDragAndDropCategory() + "\"", Log.DEBUG);
//                        dropActionCall = () -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////                                    boolean sameCategory = getDragAndDropCategory() == afterCont.getDragAndDropCategory();
////                                    getDragAndDropCategory().removeItemFromCategory((Item) getDragAndDropObject(), !sameCategory); //remove item from old (possibly same) Category, but if move within same category then don't remove the category from the item's list of categories
////                                    afterCont.getDragAndDropCategory().addToList((Item) afterCont.getDragAndDropObject(), (Item) getDragAndDropObject(), false); //insert *before* the
////                                    DAO.getInstance().saveInBackground((ParseObject) getDragAndDropCategory(), (ParseObject) afterCont.getDragAndDropCategory(), (ParseObject) getDragAndDropObject());
////</editor-fold>
////                            int insertIndex = ((ItemAndListCommonInterface) afterMyDDCont.getDragAndDropCategory()).getItemIndex((Item) afterMyDDCont.getDragAndDropObject());
//                            Category draggedCategory = getDragAndDropCategory();
//                            Category afterCategory = afterMyDDCont.getDragAndDropCategory();
//                            int insertIndex = afterCategory.getItemIndex(afterElement);
////                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), afterMyDDCont.getDragAndDropCategory(), (Item) getDragAndDropObject(), insertIndex);
//                            moveItemBetweenCategoriesAndSave(draggedCategory, afterCategory, (Item) draggedElement, insertIndex);
//                        };
//                        insertDropPlaceholder = (dropPh) -> {
//                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
//                        };
//                    }
//</editor-fold>
//</editor-fold>
                } else if (beforeElement == null && afterElement instanceof Item) {
//<editor-fold defaultstate="collapsed" desc="dropping *before* the first item">
//                } else if ((beforeElement == null || !(beforeElement instanceof Item)) && afterElement instanceof Item) {
//dragging an Item that does NOT belong to a category, only dropping as subtasks (not under categories since the logic is really unintutive!) 
                    //TODO!!! consider adding this again after all
                    //dropping *before* the first item (eg at the very top of a list). beforeElt is either null or not an Item
//                    if (beforeMyDDCont == null && afterMyDDCont != null && afterMyDDCont.getDragAndDropObject() instanceof Item) {
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("-INSERT10 \"" + draggedElement.getText() + "\" before \"" + afterElement.getText() + "\"", Log.DEBUG);
                    }
                    dropActionCall = () -> {
//                            ItemAndListCommonInterface newOwnerPrj = ((Item) afterMyDDCont.getDragAndDropObject()).getOwner();
                        ItemAndListCommonInterface newOwnerPrj = afterElement.getOwner();
//                        ASSERT.that(newOwnerPrj != null);
//                        int insertIndex = 0; //when dropping as subtask on an time, always insert at top of list (otherwise insert idrectly in expanded subtask list
////                            moveItemOrItemListAndSave(((Item) getDragAndDropObject()).getOwner(), newOwnerPrj, (Item) getDragAndDropObject(), insertIndex);
//                        moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
                        moveItemOrItemListAndSave(newOwnerPrj, draggedElement, afterElement, false); //false=insert at head of list
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
                    //dropping either between two siblings or between an expanded subtask (any level of depth) and a following task at a lower level of depth, e.g. T1; S1; > T2;
//                            if (beforeMyDDCont.getDragAndDropObject().getList().size() == 1 && beforeMyDDCont.getDragAndDropObject().getList().contains(dragged.getDragAndDropObject())) {
                    if (beforeElement.getList().size() == 1 && beforeElement.getList().contains(draggedElement)) {
//<editor-fold defaultstate="collapsed" desc="dropping a *single* subtask back under the task it belonged to before -> put in same position">
//<editor-fold defaultstate="collapsed" desc="comment">
                        /**
                         * A
                         * B <-dropping B  back where it comes from should leave it unchanged as a subtask, even though viisually, A and C will be at same level, so a normal drop should insert B at same level: A | B | C
                         * C
                         *
                         * otherwise, if list.size>1, dropping either B,C,D back
                         * under A will still insert them as subtasks (siblings)
                         * A B C D E
                         *
                         */
//</editor-fold>
                        //first treat special case of dropping a single subtask back under the task it belonged to before starting dragging it around:
                        //ContainerScrollY subtaskCont=null;
                        //                        if ((subtaskCont=getScrollYContainerWithSubtasks(beforeMyDDCont, dragged))!=null && subtaskCont.getComponentCount()beforeElement.getList().contains(dragged.getDragAndDropObject())) {
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("-INSERT11 \"" + draggedElement.getText() + "\" back under same owner \"" + beforeElement.getText() + "\"", Log.DEBUG);
                        }

                        dropActionCall = () -> {
                            //do nothing since it is already a subtask of previous
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            //insert as a subtask container
                            BorderLayout borderLayout = (BorderLayout) beforeMyDDCont.getParent().getLayout(); //if dragged is already a subtask of previous, then we can assume the center container for subtasks already exists
                            Container centerCont = (Container) borderLayout.getCenter();
                            centerCont.addComponent(dropPh); //add to end of center list (to avoid changing the index of original hidden container)
                        };

                        dropAsSubtaskActionCall = null; // doesn't make sense here since after is already a subtask => attempting to drop as a subtask will just make a normal drop
                        insertDropPlaceholderForSubtask = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//                        dropAsSuperTaskActionCall = () -> {
//                            // can drop left to insert as a sibling task instead of a subtask
//                            ItemAndListCommonInterface newOwnerPrj = beforeElement.getOwner();
////                            int insertIndex = newOwnerPrj.getItemIndex(beforeElement) + 1;
////                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
//                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, beforeElement, true);
//                        };
//                        insertDropPlaceholderForSupertask = (dropPh) -> {
//                            addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
//                        };
//</editor-fold>
                        if (draggedElement.getOwner() == beforeElement) {
                            // insert a subtask to A, as a sibling task after A to give A; S1; ...
//                                ItemAndListCommonInterface beforeOwner = beforeMyDDCont.getDragAndDropObject();
                            ItemAndListCommonInterface beforeOwner = beforeElement.getOwner();
                            dropAsSuperTaskActionCall = () -> {
                                moveItemOrItemListAndSave(beforeOwner, draggedElement, beforeElement, true);
                            };
                            insertDropPlaceholderForSupertask = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                            };
                        }
                        if (false && draggedElement.getOwner() == beforeElement) {
//                            MyDragAndDropSwipeableContainer beforeOwnerDD = getParentMyDDCont(beforeMyDDCont);
                            MyDragAndDropSwipeableContainer beforeOwnerDD = beforeMyDDCont; //the previous container should be the one?!
                            if (beforeOwnerDD != null) {
                                // insert a subtask to A, as a sibling task between A and B to give A; S1; B
                                ItemAndListCommonInterface beforeOwner = beforeOwnerDD.getDragAndDropObject();
                                ASSERT.that(beforeElement.getOwner() == beforeOwner);
                                if (beforeOwner instanceof Item) {

                                    dropAsSuperTaskActionCall = () -> {
                                        moveItemOrItemListAndSave(beforeOwner, draggedElement, beforeElement, true);
                                    };
                                    insertDropPlaceholderForSupertask = (dropPh) -> {
//                                        addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                                    };
                                }
                            }
                        }
//</editor-fold>
                    } else if (afterElement instanceof Item && beforeElement == afterElement.getOwner()) {
//<editor-fold defaultstate="collapsed" desc="inserting between a task and its expanded subtask">
                        //inserting between a task and its expanded subtask => always insert as subtask
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("-INSERT12 \"" + draggedElement.getText() + "\" between \"" + beforeElement.getText() + "\" and expanded subtask \"" + afterElement.getText() + "\"", Log.DEBUG);
                        }
                        dropActionCall = () -> {
//                            moveItemOrItemListAndSave(beforeElement, draggedElement, 0);
                            moveItemOrItemListAndSave(beforeElement, draggedElement, false);
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(afterMyDDCont, dropPh, 0);
                        };
                        dropAsSubtaskActionCall = null; // doesn't make sense here since dropping already make dragged a subtask
                        dropAsSuperTaskActionCall = null; //does not make sense
//</editor-fold>
                    } else if (afterElement instanceof Item && afterElement.getOwner() == beforeElement.getOwner()) {
//<editor-fold defaultstate="collapsed" desc="insert between two siblings (other than if the two siblings are expanded below a Category, handled above)">
//<editor-fold defaultstate="collapsed" desc="documentation">
                        /**
                         * A
                         * B
                         * <- X - normal drop: btw B and C, subtask drop:
                         * subtask to B, supertask drop: doesn't make sense in
                         * this position C
                         */
//</editor-fold>
                        //insert between two siblings at same level, or *before* first element in a list
                        if (Config.TEST_DRAG_AND_DROP) {
                            Log.p("-INSERT13 +NOR+SUB-SUP \"" + draggedElement.getText() + "\" between two siblings with same owner("
                                    + afterElement.getOwner() + ") \"" + beforeElement.getText() + "\" and \"" + afterElement.getText() + "\"", Log.DEBUG);
                        }

                        dropActionCall = () -> {
//                            int insertIndex = beforeElement.getOwner().getItemIndex(beforeElement) + 1; //+1: insert after 'before'
//                            moveItemOrItemListAndSave(beforeElement.getOwner(), draggedElement, insertIndex); //+1 after beforeElement
                            moveItemOrItemListAndSave(beforeElement.getOwner(), draggedElement, beforeElement, true); //true: after beforeElement
//                            moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeMyDDCont.getDragAndDropCategory(), (Item) draggedElement, insertIndex); //Optimization: updating Category separately may lead to a double save of dragged Item
//                            insertIntoCategoryOfSiblingItemAndSave(beforeMyDDCont, draggedMyDDCont); //if we get to this if branch, then neither before or after Items are in a Category
                        };
                        insertDropPlaceholder = (dropPh) -> {
                            addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                        };

                        //insert as subtask
                        dropAsSubtaskActionCall = () -> {
//                            ItemAndListCommonInterface newOwnerPrj = beforeElement;
                            //                                int insertIndex = newOwnerPrj.getItemIndex(beforeElement);
//                            int index = MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean() ? beforeElement.getList().size() : 0;//UI: 0 means insert at head of subtask list
//                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, index);
//                            moveItemOrItemListAndSave(newOwnerPrj, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
                            moveItemOrItemListAndSave(beforeElement, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
//                            expandSubtasks(newOwnerPrj); //expand to show list of subtasks to avoid that just dropped element 'disappears'
                            expandSubtasks(beforeElement); //expand to show list of subtasks to avoid that just dropped element 'disappears'

                        };
                        insertDropPlaceholderForSubtask = (dropPh) -> {
                            addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                        };

                        if (draggedElement.getOwner() == beforeElement) {
                            MyDragAndDropSwipeableContainer beforeOwnerDD = getParentMyDDCont(beforeMyDDCont);
                            if (beforeOwnerDD != null) {
                                // insert a subtask to A, as a sibling task between A and B to give A; S1; B
                                ItemAndListCommonInterface beforeOwner = beforeOwnerDD.getDragAndDropObject();
                                ASSERT.that(draggedElement.getOwner() == beforeOwner);
                                if (beforeOwner instanceof Item) {

                                    dropAsSuperTaskActionCall = () -> {
                                        moveItemOrItemListAndSave(beforeOwner, draggedElement, beforeElement, true);
                                    };
                                    insertDropPlaceholderForSupertask = (dropPh) -> {
//                                        addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                                        addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                                    };
                                }
                            }
                        }
//                    } else if (afterElement == null || afterElement instanceof ItemList) {
//                    } else if (afterElement instanceof Item && afterElement.getOwner() == beforeElement.getOwner()) {
//</editor-fold>
                    } else { //afterElement is null, or a Category or ItemList, or an Item at a higher level (a sibling to a mother-task of beforeElement). In all cases, we only look at beforeElement to determine what to do
                        ASSERT.that(afterElement == null || afterElement instanceof ItemList || afterElement instanceof Item, "afterElement=" + afterElement + ", should normally be null here?!");
//<editor-fold defaultstate="collapsed" desc="documentation of cases">
/*
before getting to here, we've already covered the following cases where both before and after are Items:
                    T1
                        S1
                           <- X - inserting Normal: btw S1 and S2, Sub: subtask to S1
                        S2

                    T1
                        S1
                           <- X - inserting Normal: X as a new subtask to S1, before S22. Not meaningful to insert as a Subtask under S1. Nor as A Supertask
                            S22
                    ------------
                    so that leaves the following cases (where the last element after the insertion point can either be an Item at a higher (less indented) level than beforeElt, it can be an ItemList/Category, or it can be null. ALL cases should be treated the same. 

                    T1
                        S1
                        [S2] <- S2 - inserting S2 where it was before
                    T2
                    ---
                    T0
                    T1
                        S0
                        S1
                        <- T0 - inserting T0 which is sibling to T1 after S2
                    ---
                    T
                        [S0]
                        S1
                        S2
                        <- S3 - inserting S3 after S2
                    ---
                    T
                        S1
                            S11
                        S2
                    <- S11 - inserting S11 before T2
                    T2
                    ---
                    T0
                    T1
                        S1
                            S11
                        S2
                    <- T0 - inserting T0 (coming from anywhere) before T2
                    T2
                         */
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                        ItemAndListCommonInterface ownerOfBeforeElement = beforeElement.getOwner();
//                        int idxOfBeforeEltInOwnersList = ownerOfBeforeElement.getItemIndex(beforeElement);
//                        List beforeOwnerList = ownerOfBeforeElement.getList();
//                        int beforeOwnerListSize = beforeOwnerList.size();
//                        boolean beforeEltIsLastInBeforeEltsOwnersList = (ownerOfBeforeElement.getItemIndex(beforeElement) == beforeOwnerList.size() - 1);
//                        boolean draggedEltIsLastInBeforeEltsOwnersList
//                                = (ownerOfBeforeElement.getList().indexOf(draggedElement) == beforeOwnerList.size() - 1
//                                && idxOfBeforeEltInOwnersList == beforeOwnerList.size() - 2);
//                        boolean beforeEltIsLastInList = (idxOfBeforeEltInOwnersList == beforeOwnerListSize - 1) //beforeElt is last in list (dropping it back to its pre-drag position)
//                                || (beforeOwnerList.indexOf(draggedElement) == beforeOwnerListSize - 1 && idxOfBeforeEltInOwnersList == beforeOwnerListSize - 2);
//</editor-fold>
                        //if dragged is actual last element (although hidden) then before should be second-last
//                            MyDragAndDropSwipeableContainer siblingContainer = findSiblingUpwardsInHierarchyN(draggedMyDDCont, beforeMyDDCont);
                        MyDragAndDropSwipeableContainer siblingContainer = findSiblingUpwardsInHierarchyN(this, beforeMyDDCont);
                        if (siblingContainer != null) {
//<editor-fold defaultstate="collapsed" desc="moving sibling (from above or below) to after other sibling which may have expanded subtasks (insert visually after last expanded/shown subtask, but after the sibling in the parent's list)">
//if 'before' is last in a list AND there is a sibling up the hierarchy, then insert after that sibling (and not after the last expanded subtask)
//defaultstate="collapsed" desc="CASE 1: inserting a sibling to beforeElt or one of it's parents. Normal: insert after the sibling, Sub: insert as subtask (=indent), Super: insert after the beforeElt's owner (=extent)">
//NNB. If there is no sibling, it means that we're moving between lists or between different projects, 
//<editor-fold defaultstate="collapsed" desc="comment">
//                                Log.p("-INSERT \"" + draggedElement.getText() + "\" after sibling \"" + sibling.getDragAndDropObject().getText() + "\"", Log.DEBUG);
//                            if (Config.TEST_DRAG_AND_DROP) Log.p("-INSERT +NOR+SUB"+" \"" + draggedElement.getText() + "\" after sibling \"" + siblingContainer.getDragAndDropObject().getText() + "\""
//                                        + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
//                                        + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
//</editor-fold>
                            ASSERT.that(siblingContainer.getDragAndDropObject().getOwner() == draggedElement.getOwner(),
                                    "sibling does not have same owner as draggedItem, draggedElt=" + draggedElement
                                    + ", draggedElt.owner=" + draggedElement.getOwner()
                                    + ", siblingContainer.getDragAndDropObject()=" + siblingContainer.getDragAndDropObject()
                                    + ", owner=" + siblingContainer.getDragAndDropObject().getOwner());
                            ItemAndListCommonInterface siblingElement = siblingContainer.getDragAndDropObject();
                            MyDragAndDropSwipeableContainer siblingOwnerDD = getParentMyDDCont(siblingContainer);
                            ItemAndListCommonInterface siblingOwner = siblingElement.getOwner();
                            ASSERT.that(siblingOwnerDD == null || siblingOwner == siblingOwnerDD.getDragAndDropObject(),
                                    "inconsistency: not getting the same sibling owner, siblingOwner=" + siblingOwner
                                    + ", siblingOwnerDD.getDragAndDropObject()=" + (siblingOwnerDD == null ? "<null>" : siblingOwnerDD.getDragAndDropObject()));
                            //insert at same level as sibling (visually after it's last expanded subtask):
                            dropActionCall = () -> {
//                                int insertIndex = siblingOwner.getItemIndex(siblingElement) + 1; //+1: insert after sibling
//                                moveItemOrItemListAndSave(siblingOwner, draggedElement, insertIndex);
                                moveItemOrItemListAndSave(siblingOwner, draggedElement, siblingElement, true);
//<editor-fold defaultstate="collapsed" desc="comment">
//                                if (false) { //doesn't make sense since we're moving siblings (so already in same category)
//                                    moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeMyDDCont.getDragAndDropCategory(), (Item) draggedElement, insertIndex); //Optimization: updating Category separately may lead to a double save of dragged Item
//                                }
//</editor-fold>
                            };
                            insertDropPlaceholder = (dropPh) -> { //insert after sibling
                                addDropPlaceholderToAppropriateParentCont(siblingContainer, dropPh, 1);
                            };
                            // dropAsSubtaskActionCall and dropAsSuperTaskActionCall are both defined below
                            //inserting as 'subtask' below a last subtask of sibling should not create a subtask, but simply insert after the last subtask
                            dropAsSubtaskActionCall = () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
////                                ItemAndListCommonInterface newOwnerPrj = beforeElement.getOwner();
////                                int insertIndex = newOwnerPrj.getItemIndex(beforeElement) + 1;
////                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
//                                ItemAndListCommonInterface newOwnerPrj = beforeElement;
////                                int index = MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean() ? beforeElement.getList().size() : 0;//UI: 0 means insert at head of subtask list
////                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, index);
//</editor-fold>
                                ItemAndListCommonInterface newOwnerPrj = beforeElement;
                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
                                expandSubtasks(newOwnerPrj); //UI: expand to show list of subtasks to avoid that just dropped element 'disappears'
                            };
                            insertDropPlaceholderForSubtask = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                            };
                            //insert at (top-level-NO) level *above the sibling*, *if* that is a not the same (always a higher level?!) than the beforeElement's owner (meaning we're already at top-level)
//<editor-fold defaultstate="collapsed" desc="comment">
//                            MyDragAndDropSwipeableContainer topLevelOwnerDD = getTopLevelParentMyDDCont(beforeMyDDCont);
//                            MyDragAndDropSwipeableContainer siblingOwnerDD = getParentMyDDCont(siblingContainer);
//                            ItemAndListCommonInterface siblingOwner = siblingOwnerDD.getDragAndDropObject().getOwner();
//                            MyDragAndDropSwipeableContainer topLevelOwnerDD = getTopLevelParentMyDDCont(siblingOwnerDD);
//                            MyDragAndDropSwipeableContainer siblingOwnerOwnerDD; // = getParentMyDDCont(siblingOwnerDD);
//                            if (siblingOwnerDD != null && (siblingOwnerOwnerDD = getParentMyDDCont(siblingOwnerDD)) != null) {
//                                ItemAndListCommonInterface siblingOwnerOwner = siblingOwnerDD.getDragAndDropObject().getOwner();
//                            MyDragAndDropSwipeableContainer siblingOwnerOwnerDD; // = getParentMyDDCont(siblingOwnerDD);
//                            ItemAndListCommonInterface siblingOwnerOwner = siblingOwnerDD.getDragAndDropObject().getOwner();
//</editor-fold>
//                            ItemAndListCommonInterface siblingOwnerOwner = siblingOwner.getOwner();
//                            if (siblingOwnerOwner != null) {
                            if (siblingOwnerDD != null) {
                                ItemAndListCommonInterface siblingOwnerOwner = siblingOwner.getOwner();
//                                if (siblingOwner != ownerOfBeforeElement) {//if top-level is the same as the sibling's owner, don't allow dropping as super-task
                                if (Config.TEST_DRAG_AND_DROP) {
                                    Log.p("-INSERT14 +NOR+SUB+SUP" + " \"" + draggedElement.getText() + "\" after sibling \"" + siblingElement.getText() + "\""
                                            + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
                                            + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
                                }

                                dropAsSuperTaskActionCall = () -> {//shouldn't be allowed to super-drop a task in ScreenListOfItemLists where lists are top-level
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    ItemAndListCommonInterface topLevelElt = siblingOwnerDD.getDragAndDropObject();
//                                    ItemAndListCommonInterface newOwnerPrj = topLevelElt.getOwner();
//                                    int insertIndex = newOwnerPrj.getItemIndex(topLevelElt) + 1; //insert just after the topLevelElt
//                                    int insertIndex = siblingOwnerOwner.getItemIndex(siblingOwner) + 1; //insert just after the si
//                                    moveItemOrItemListAndSave(siblingOwnerOwner, draggedElement, insertIndex);
//</editor-fold>
                                    Category siblingOwnerCategory = siblingOwnerDD.getDragAndDropCategory();
                                    if (siblingContainer != null && siblingElement instanceof Item && siblingOwnerCategory != null) {
                                        moveItemBetweenCategoriesAndSave(draggedItemsCategory, siblingOwnerCategory, (Item) draggedElement, (Item) siblingElement, true);
                                    } else {
                                        moveItemOrItemListAndSave(siblingOwnerOwner, draggedElement, siblingOwner, true);
                                    }
                                };
                                insertDropPlaceholderForSupertask = (dropPh) -> {
//                                    addDropPlaceholderToAppropriateParentCont(siblingOwnerOwnerDD, dropPh, 1);
                                    addDropPlaceholderToAppropriateParentCont(siblingOwnerDD, dropPh, 1);
                                };
//                                }
                            } else if (Config.TEST_DRAG_AND_DROP) {
                                Log.p("-INSERT15 +NOR+SUB-sup" + " \"" + draggedElement.getText() + "\" after sibling \"" + siblingElement.getText() + "\""
                                        + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
                                        + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
                            }

//</editor-fold>
                        } else { //siblingContainer == null
//<editor-fold defaultstate="collapsed" desc="CASE2; last option: dragged is NOT a sibling to beforeElt or a higher-level parent, so no 'smart' drop, simply insert after 'before' Item (or SUP: before">
                            //no sibling found, so simply insert after 'before' (after the subtask)
                            if (false && Config.TEST_DRAG_AND_DROP) {
                                Log.p("-INSERT16 +NOR+SUB\"" + draggedElement.getText() + "\" after \"" + beforeElement.getText() + "\""
                                        + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
                                        + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
                            }
                            dropActionCall = () -> {
                                ItemAndListCommonInterface newOwner = beforeElement.getOwner();
//                                int insertIndex = newOwner.getItemIndex(beforeElement) + 1; //+1: insert after before element
//                                moveItemOrItemListAndSave(newOwner, draggedElement, insertIndex);
                                moveItemOrItemListAndSave(newOwner, draggedElement, beforeElement, true);
//<editor-fold defaultstate="collapsed" desc="comment">
//                                if (false && beforeMyDDCont.getDragAndDropCategory() != null) { //case of moving within a category is being checked above
//                                    Category beforeCategory = beforeMyDDCont.getDragAndDropCategory();
//                                    int catIndex = beforeCategory.getItemIndex(beforeElement) + 1; //+1 drop at position *after* beforeItem
//                                    moveItemBetweenCategoriesAndSave(getDragAndDropCategory(), beforeMyDDCont.getDragAndDropCategory(), (Item) draggedElement, catIndex);
//                                }
//</editor-fold>
//                                insertIntoCategoryOfSiblingItemAndSave(beforeMyDDCont, draggedMyDDCont); //NOT necessary here, handled above if inserting after an Item which is expanded under a Category
                            };
                            insertDropPlaceholder = (dropPh) -> {
                                addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                            };

                            dropAsSubtaskActionCall = () -> {
                                ItemAndListCommonInterface newOwnerPrj = beforeElement;
                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean());
                                expandSubtasks(newOwnerPrj); //expand to show list of subtasks to avoid that just dropped element 'disappears'
//                                insertIntoCategoryOfSiblingItemAndSave(beforeMyDDCont, draggedMyDDCont); //CAN NEVER belong to a Category
                            };
                            insertDropPlaceholderForSubtask = (dropPh) -> {
                                //  addDropPlaceholderToAppropriateParentCont(beforeMyDDCont, dropPh, 1);
                                addDropPlaceholderAsFirstSubtask(beforeMyDDCont, dropPh, beforeElement);
                            };

                            //insert at level above beforeElt (if exists) == exdent the dropped task
                            MyDragAndDropSwipeableContainer beforeOwnerDD = getParentMyDDCont(beforeMyDDCont);
//<editor-fold defaultstate="collapsed" desc="comment">
//                            if (false && siblingOwnerDD != null) {
//                                dropAsSuperTaskActionCall = () -> {
//                                    ItemAndListCommonInterface newOwner = beforeElement.getOwner();
//                                    ItemAndListCommonInterface newOwnerFromDD = siblingOwnerDD.getDragAndDropObject();
//                                    ASSERT.that(newOwnerFromDD == newOwner, "inconsistent owners");
////                                    ItemAndListCommonInterface topLevelElt = topLevelOwnerDD.getDragAndDropObject();
////                                    ItemAndListCommonInterface newOwnerPrj = topLevelElt.getOwner();
////                                    int insertIndex = newOwner.getItemIndex(beforeElement) + 1; //insert just after the topLevelElt
////                                    moveItemOrItemListAndSave(newOwner, draggedElement, insertIndex);
//                                    moveItemOrItemListAndSave(newOwner, draggedElement, beforeElement, true);
//                                    insertIntoCategoryOfSiblingItemAndSave(siblingOwnerDD, draggedMyDDCont);
//                                };
//                                insertDropPlaceholderForSupertask = (dropPh) -> {
//                                    addDropPlaceholderToAppropriateParentCont(siblingOwnerDD, dropPh, 1);
//                                };
//                            }
//</editor-fold>
//                            MyDragAndDropSwipeableContainer siblingOwnerOwnerDD = getParentMyDDCont(siblingOwnerDD);
                            if (beforeOwnerDD != null) {
                                ItemAndListCommonInterface beforeOwner = beforeOwnerDD.getDragAndDropObject();
                                if (beforeOwner instanceof Item) {
                                    Category beforeOwnerCategory = beforeOwnerDD.getDragAndDropCategory();
                                    if (beforeOwnerCategory != null) { //adding to a category
                                        dropAsSuperTaskActionCall = () -> {//shouldn't be allowed to super-drop a task in ScreenListOfItemLists where lists are top-level
                                            moveItemBetweenCategoriesAndSave(draggedItemsCategory, beforeOwnerCategory, (Item) draggedElement, (Item) beforeOwner, true); //true: insert after 
                                        };
                                        insertDropPlaceholderForSupertask = (dropPh) -> {
                                            addDropPlaceholderToAppropriateParentCont(beforeOwnerDD, dropPh, 1);
                                        };
                                    } else {
                                        dropAsSuperTaskActionCall = () -> {//shouldn't be allowed to super-drop a task in ScreenListOfItemLists where lists are top-level
                                            moveItemOrItemListAndSave(beforeOwner, draggedElement, beforeElement, true);
                                        };
                                        insertDropPlaceholderForSupertask = (dropPh) -> {
                                            addDropPlaceholderToAppropriateParentCont(beforeOwnerDD, dropPh, 1);
                                        };
                                        if (false) {
                                            moveItemOrItemListAndSave(beforeOwner, draggedElement, MyPrefs.insertTasksDroppedAsSubtasksUnderUnexpandedTaskAtEndOfSubtaskList.getBoolean()); //WHY was this code here?!
                                        }
                                    }
                                }
//                                if (siblingOwner != ownerOfBeforeElement) {//if top-level is the same as the sibling's owner, don't allow dropping as super-task
                                if (Config.TEST_DRAG_AND_DROP) {
                                    Log.p("-INSERT17 +NOR+SUB+SUP" + " \"" + draggedElement.getText() + "\" after sibling \""
                                            + (siblingContainer != null && siblingContainer.getDragAndDropObject() != null ? siblingContainer.getDragAndDropObject().getText() : "") + "\""
                                            + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
                                            + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
                                }
                            } else if (Config.TEST_DRAG_AND_DROP) {
                                Log.p("-INSERT18 +NOR+SUB-sup" + " \"" + draggedElement.getText() + "\" after sibling \"" + (siblingContainer != null ? siblingContainer.getDragAndDropObject().getText() : "<null>") + "\""
                                        + (beforeMyDDCont.getDragAndDropCategory() != null ? (" +Cat \"" + beforeMyDDCont.getDragAndDropCategory() + "\"") : "")
                                        + (getDragAndDropCategory() != null ? (" -Cat \"" + getDragAndDropCategory() + "\"") : ""), Log.DEBUG);
                            }

//</editor-fold>
                        }
                        //<editor-fold defaultstate="collapsed" desc="comment">
                        //in both cases, support insert as a supertask

                        //drop left to insert as a super task at same level as 'after'
                        //TODO!!!! need to find the place to insert as the top-level expanded Item using before as starting point
                        //we may not have an 'after' element (eg end of list) so need to start from 'before' and search up to the highest level (NB the there may not be an expanded element at a higher level => no action)
//                        MyDragAndDropSwipeableContainer topLevelOwnerDD = getTopLevelParentMyDDCont(beforeMyDDCont);
//                        if (topLevelOwnerDD != null && topLevelOwnerDD.getDragAndDropObject()) {
//                            dropAsSuperTaskActionCall = () -> {
//                                MyDragAndDropSwipeableContainer topLevelOwnerDD = getTopLevelParentMyDDCont(beforeMyDDCont);
//                                ItemAndListCommonInterface topLevelElt = topLevelOwnerDD.getDragAndDropObject();
//                                ItemAndListCommonInterface newOwnerPrj = topLevelElt.getOwner();
//                                int insertIndex = newOwnerPrj.getItemIndex(topLevelElt) + 1; //insert just after the topLevelElt
//                                moveItemOrItemListAndSave(newOwnerPrj, draggedElement, insertIndex);
//                            };
//                            insertDropPlaceholderForSupertask = (dropPh) -> {
//                                addDropPlaceholderToAppropriateParentCont(topLevelOwnerDD, dropPh, 1);
//                            };
//                        }
//</editor-fold>
                    }
                }
            } else {
                Log.p("4. !!!!!after an Item, but no suitable if statement");
                ASSERT.that(false, "4. !!!!!after an Item, but no suitable if statement");
            }

            //MAKE DROPPLACEHOLDER
            Label newDropPlaceholder;
            if ((dropActionCall != null && insertDropPlaceholder != null)
                    || (dropAsSubtaskActionCall != null && insertDropPlaceholderForSubtask != null)
                    || (dropAsSuperTaskActionCall != null && insertDropPlaceholderForSupertask != null)) {
                Log.p("CREATE NEW insertDropPlaceholder");
                //Create new dropPlaceholder
                newDropPlaceholder = new Label() {
//<editor-fold defaultstate="collapsed" desc="** create Label used as dropPlaceHolder - implements the drop() action **">
//                Label newDropPlaceholder = new Label();
                    Component dropTarget1 = dropTarget;

                    @Override
                    public int getWidth() {
                        return draggedWidth;
                    }

                    @Override
                    public int getHeight() {
                        return draggedHeight;
                    }

                    @Override
                    public Dimension getPreferredSize() {
                        return new Dimension(draggedWidth, draggedHeight); //ensure placeholder has exactly the same size as the dragged element
                    }

                    @Override
                    public void drop(Component drag1, int x, int y) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                        Log.p("**********Comp.drop, dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
//                        if (insertBelow(x) && dropAsSubtaskActionCall != null) {
//                            dropAsSubtaskActionCall.insertDropCont();
//                        } else {
//                            dropActionCall.insertDropCont();
//                        }
//</editor-fold>
//                        InsertPositionType test = lastDropPlaceholderInsertPosition; //for testing
//                        InsertPositionType test2 = newInsertPosition;
//                        if (MyPrefs.dragDropAsSubtaskEnabled.getBoolean() && newInsertPosition == InsertPositionType.SUBTASK && dropAsSubtaskActionCall != null && insertDropPlaceholderForSubtask != null) {
//                        if (MyPrefs.dragDropAsSubtaskEnabled.getBoolean() && newlastDropPlaceholderInsertPosition == InsertPositionType.SUBTASK && dropAsSubtaskActionCall != null && insertDropPlaceholderForSubtask != null) {
                        if (MyPrefs.dragDropAsSubtaskEnabled.getBoolean() && newInsertPosition == InsertPositionType.SUBTASK && dropAsSubtaskActionCall != null && insertDropPlaceholderForSubtask != null) {
                            dropAsSubtaskActionCall.doDropAction();
                            Log.p("**********Comp.drop , SUBTASK dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
                        } else if (MyPrefs.dragDropAsSupertaskEnabled.getBoolean() && lastDropPlaceholderInsertPosition == InsertPositionType.SUPERTASK && dropAsSuperTaskActionCall != null && insertDropPlaceholderForSupertask != null) {
                            dropAsSuperTaskActionCall.doDropAction();
                            Log.p("**********Comp.drop , SUPER dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
//                        } else if (newInsertPosition == InsertPositionType.NORMAL && dropActionCall != null && insertDropPlaceholder != null) {
                        } else if (dropActionCall != null && insertDropPlaceholder != null) {
                            Log.p("**********Comp.drop , NORMAL dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
                            dropActionCall.doDropAction();
                        } else {
                            Log.p("**********Comp.drop , NO ACTION!!! dropTarget=" + dropTarget1.getName() + ", dragged=" + drag1.getName(), Log.DEBUG);
                        }
                        DAO.getInstance().triggerParseUpdate();
//<editor-fold defaultstate="collapsed" desc="comment">
//                                dragged.dropSucceeded = true;
//                                getComponentForm().animateHierarchy(300);
//</editor-fold>
                        setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
                        setFocusable(false); //set focusable false once the drop (activated by longPress) is completed
//<editor-fold defaultstate="collapsed" desc="comment">
//                        ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition(getParentScrollYContainer(beforeMyDDCont))); //simply keep same position of *previous* container (unless null!)
//                        ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //simply keep same position as whereto the list was scrolled during the drag, then inserted element should 'stay in place'
//                        if (newDropPlaceholder != null) {
//                            ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition(draggedElement, newDropPlaceholder)); //whenever possible keep the dropped in same position**
//                        } else {
//                            ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //doesn't work since newDrop may not have been initiazed
//                        }
//</editor-fold>
                        ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition()); //doesn't work since newDrop may not have been initiazed
//                            super.drop(draggedMyDDCont, x, y); //Container.drop implements the first quick move of the container itself
                        super.drop(this, x, y); //Container.drop implements the first quick move of the container itself
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
//                    @Override
//                    protected int getDragRegionStatus(int x, int y) {
//                        return DRAG_REGION_LIKELY_DRAG_XY;
//                    }
//</editor-fold>
                };
                newDropPlaceholder.setUIID("DropTargetPlaceholder");
                newDropPlaceholder.setDropTarget(true);
                if (true && Config.TEST_DRAG_AND_DROP) {
                    newDropPlaceholder.setText("PlcHldr@" + dropTarget.getName() + " " + newInsertPosition.toString());
                    newDropPlaceholder.setName("PlcHldr@" + dropTarget.getName() + " " + newInsertPosition.toString());
                }
//                    if (false && Config.TEST_DRAG_AND_DROP) newDropPlaceholder.setName("DropPlaceholder for " + dropTarget.getName() + ", w=" + newDropPlaceholder.getWidth() + ", h=" + newDropPlaceholder.getHeight());
                if (false && Config.TEST_DRAG_AND_DROP) {
                    newDropPlaceholder.setName("PlcHldr/" + dropTarget.getName());
                }
//</editor-fold>
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

                if (dropPlaceholder != null) {
                    dropPlaceholder.remove(); //remove old placeholder - do BEFORE adding new one
                }                //insert new dropPlaceholder
//                    treeList.addComponent(dropPlaceholderInsertionIndex, newDropPlaceholder); //insert dropPlaceholder at pos of dropTarget (should correctly will 'push down' the target one position)
//                insertDropPlaceholder.insert(newDropPlaceholder, false && dropAsSubtaskActionCall != null && insertBelow(x));
                if (MyPrefs.dragDropAsSubtaskEnabled.getBoolean() && newInsertPosition == InsertPositionType.SUBTASK && insertDropPlaceholderForSubtask != null) {
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("insertingDropPlaceholderForSubtask");
                    }
                    insertDropPlaceholderForSubtask.insertDropPlaceholder(newDropPlaceholder);
                } else if (MyPrefs.dragDropAsSupertaskEnabled.getBoolean() && newInsertPosition == InsertPositionType.SUPERTASK && insertDropPlaceholderForSupertask != null) {
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("insertingDropPlaceholderForSupertask");
                    }
                    insertDropPlaceholderForSupertask.insertDropPlaceholder(newDropPlaceholder);
                } else if (insertDropPlaceholder != null) { //don't test on InsertPositionType since if NORMAL and do drop as sub/super is defined, we'll always drop normally
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("insertingNormalDropPlaceholder");
                    }
                    insertDropPlaceholder.insertDropPlaceholder(newDropPlaceholder);
                } else {
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("no insert for DropPlaceholder defined, setting newDropPlaceholder=null");
                    }
                    newDropPlaceholder = null; //set null to avoid null pointer exceptions
                }//else no drop action possible here
                dropPlaceholder = newDropPlaceholder; //save new placeholder

                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("dropAction " + (dropActionCall != null ? "NORM" : "")
                            + (dropAsSubtaskActionCall != null ? "SUBT" : "") + (dropAsSuperTaskActionCall != null ? "SUPE" : "")
                            + ("insertDrpPlh " + (insertDropPlaceholder != null ? "NORM" : "") + (insertDropPlaceholderForSubtask != null ? "SUBT" : "") + (insertDropPlaceholderForSupertask != null ? "SUPE" : "")));
                }
//                    if (Config.TEST_DRAG_AND_DROP) Log.p("insertDropPlaceholder defined " + (insertDropPlaceholder != null ? "NORMAL " : "") + (insertDropPlaceholderForSubtask != null ? "SUBTASK " : "") + (insertDropPlaceholderForSupertask != null ? "SUPER" : ""));

                ASSERT.that(newDropPlaceholder == null || (dropPlaceholder != null && dropPlaceholder.getParent() != null), () -> "dragged.dropPlaceholder NOT correctly inserted");
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
//<editor-fold defaultstate="collapsed" desc="comment">
//                    getComponentForm().revalidate();
//                        dropTarget.getComponentForm().animateLayout(300);
//                        newDropPlaceholder.getComponentForm().animateLayout(300); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//                        newDropPlaceholder.getComponentForm().animateHierarchy(300); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//</editor-fold>
                formNeedRefresh = newDropPlaceholder != null;
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    newDropPlaceholder.getComponentForm().revalidate(); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//                }//                        Log.p("addDragOverListener: new dropPlaceholder=" + newDropPlaceholder.getName() + ", inserted position=" + index + ", for dropTarget=" + dropTarget.getName());
//</editor-fold>
            } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false && formNeedRefresh) {
//                    getComponentAt(x, y).getComponentForm().revalidate();
////                    dragged.getComponentForm().revalidate(); //CANNOT use dropTarget.getComponentForm() since when dropTarget==dragged, it is removed from its form
//                    formNeedRefresh = false;
//                }
//</editor-fold>
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("no drop action for dropTarget=" + dropTarget.getName());
                }
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("dropActionCall        " + (dropActionCall != null ? "NORMAL " : "") + (dropAsSubtaskActionCall != null ? "SUBTASK " : "") + (dropAsSuperTaskActionCall != null ? "SUPER" : ""));
                }
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("insertDropPlaceholder " + (insertDropPlaceholder != null ? "NORMAL " : "") + (insertDropPlaceholderForSubtask != null ? "SUBTASK " : "") + (insertDropPlaceholderForSupertask != null ? "SUPER" : ""));
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                        ASSERT.that(treeList instanceof MyTree2, "treeList not instanceof MyTree2, treelist="
//                                + (treeList != null ? treeList.getName() : "nullx") + ", for dropTarget=" + dropTarget.getName());
//                        Log.p("addDragOverListener: treeList==null for dropTarget=" + dropTarget.getName());
//</editor-fold>
            }
            if (formNeedRefresh) {
//                    Form form = Display.getInstance().getCurrent();
                Form form = getComponentForm();
                if (form != null) {
                    form.revalidateWithAnimationSafety();
                    if (Config.TEST_DRAG_AND_DROP) {
                        Log.p("form.revalidateWithAnimationSafety()");
                    }
                }
                formNeedRefresh = false;
            }
            if (false) {
                lastDropPlaceholderInsertPosition = newInsertPosition; //keep track of where dropPlaceholder is situated //SHOUDN'T be necessary
            }
//            Form form = getComponentForm();
//            if (formNeedRefresh && form != null)
//                form.revalidateWithAnimationSafety();
            if (false) {
                e.consume();
            }
            //<editor-fold defaultstate="collapsed" desc="comment">
//            }
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
            if (Config.TEST_DRAG_AND_DROP) {
                Log.p("---------------- END MyDragAndDropSwipeableContainer() ------------------------------------", Log.DEBUG);
            }
        }); //end of dragOverListener
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container parent = getParent();
//        //iterate up the container hierarchy for an Item to see if the above object is a category
//        while (parent != null) {
////            if (parent instanceof MyDragAndDropSwipeableContainer && ((MyDragAndDropSwipeableContainer)parent).getDragAndDropCategory()!=null)
////                return ((MyDragAndDropSwipeableContainer)getParent()).getDragAndDropCategory();
////            else
////                return null;
//            //only move up to first containing MyDragAndDropSwipeableContainer (avoid that eg a subtaskreturns its expaned project's category)
//            if (parent instanceof MyDragAndDropSwipeableContainer) {
//                return ((MyDragAndDropSwipeableContainer) parent).getDragAndDropCategory();
//            } else {
//                parent = parent.getParent();
//            }
//        }
//</editor-fold>
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private int getContainerIndexInTreeListXXX(MyDragAndDropSwipeableContainer dropTarget) {
//        Container treeList = dropTarget.getParent(); //treeList = the list in which to insert the dropPlaceholder
//        Component dropParent = dropTarget;
//        while (!(treeList instanceof MyTree2) && treeList != null) {
//            dropParent = treeList;
//            treeList = treeList.getParent();
//        }
//        return treeList != null ? treeList.getComponentIndex(dropParent) : -1;
//    }
//    @Override
//</editor-fold>
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
        if (Config.TEST_DRAG_AND_DROP) {
            Log.p("MyDragAndDropSwipeableContainer.pointerReleased (D&D) x=" + x + " y=" + y);
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void refreshAfterDrop() {
//        ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//<editor-fold defaultstate="collapsed" desc="comment">
//    private boolean dropItemInCategoryViewXXX(ItemAndListCommonInterface before, ItemAndListCommonInterface after, ItemAndListCommonInterface dragged,
//            Category beforeCategory, Category afterCategory, Item draggedItem, boolean insertBelow) {
//
//        if (beforeCategory == null) {
//
//        } else if (afterCategory == null) {
////<editor-fold defaultstate="collapsed" desc="comment">
////Cat1
//// T1
////  t11
////    t111
////    t112
////  t12
////T2
////T3
////</editor-fold>
//
//        }
//        return true;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//
//    private boolean dropCategoryXXX(ItemAndListCommonInterface before, ItemAndListCommonInterface after, Category dragged) {
//        //TODO support subcategories
//        if (before == null && after instanceof Category) { //inserting at the very top of the list (dropTargetContainer is *before* the first element on the screen)
//            //insert in the same list as after
//            after.getOwner().addToList(0, dragged);
//        } else if (after == null && before instanceof Category) { //inserting at the very end of the list (dropTargetContainer is *after* the last element on the screen)
//            //insert in the same list as before
//            before.getOwner().addToList(dragged);
//        } else if (before instanceof Category && after instanceof Category) {
//            //else insert after 'after' (e.g. before is subtask11 (a subtask to Task1) and after is Task2, so added is added *after* subtask11)
//            int index = before.getOwner().getList().indexOf(before);
//            before.getOwner().addToList(index + 1, dragged);
//        } else if (before instanceof Category) {
//            int index = before.getOwner().getList().indexOf(before);
//            before.getOwner().addToList(index + 1, dragged);
//        } else if (after instanceof Category) {
//            int index = after.getOwner().getList().indexOf(after);
//            after.getOwner().addToList(index, dragged);
//        } else {
//            //dropping Catgory in between something other than categories (eg between two Items)
//            return false;
//        }
//        return true;
//    }
//</editor-fold>
//</editor-fold>
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
//                //moved the dragged container to new position to quickly removeFromCache the screen
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
        if (false) {
            if (isHidden()) {
                ASSERT.that(false, () -> "***Calling getDragImage() on " + getName() + " while it's hidden!!!"
                        + ", w=" + getWidth() + ", h=" + getHeight());
            } else {
                if (Config.TEST_DRAG_AND_DROP) {
                    Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
                            + ", w=" + getWidth() + ", h=" + getHeight());
                }
            }
        }
        if (dragImage2 == null) {
            if (Config.TEST_DRAG_AND_DROP) {
                Log.p("Calling getDragImage() on " + getName() + " (not hidden)"
                        + ", w=" + getWidth() + ", h=" + getHeight());
            }
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
        if (Config.TEST_DRAG_AND_DROP) {
            Log.p("MyDragAndD.dragFinished for element=" + (getName() != null ? getName() : "") + ", UNHIDE, remove dropPlaceholder="
                    + ((dropPlaceholder != null
                    && dropPlaceholder.getName() != null)
                    ? dropPlaceholder.getName() : "nullx"));
        }
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
//        Form f = null;
        Container compToAnimate = null;
        //if there is still a dropPlaceholder inserted, remove it
        if (dropPlaceholder != null) {
//            f = dropPlaceholder.getComponentForm();
            compToAnimate = dropPlaceholder.getParent(); //TODO: rather find ScrollY container to animate?!
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (Config.TEST_DRAG_AND_DROP)
//                if (dropPlaceholder.getParent() != null) {//remove the old placeholder ( not done in successful drop)
//                    Log.p("MyDragAndD.dragFinished: removing dropPlaceholder=" + dropPlaceholder.getName() + " from parent=" + dropPlaceholder.getParent().getName());
//                    dropPlaceholder.getParent().removeComponent(dropPlaceholder); //remove the old placeholder ( not done in successful drop)
//                } else {
//                    Log.p("***MyDragAndD.dragFinished: no parent() for dropPlaceholder=" + dropPlaceholder.getName());
//                }
//</editor-fold>
            dropPlaceholder.remove(); //remove the old placeholder ( not done in successful drop)
            dropPlaceholder = null;
//            animate = true;
        }
        //always removeFromCache (eg if dropped outside a droptarget)
//        if (f != null) {
//            f.animateHierarchy(300); //refresh after hiding dropPlaceholder
//        }
        if (compToAnimate != null && compToAnimate.getComponentForm() != null) {
            compToAnimate.animateHierarchy(MyForm.ANIMATION_TIME_DEFAULT); //refresh after hiding dropPlaceholder
        }

//        if (!dropSucceeded) {
//            draggedOrgParent.addComponent(draggedOrgIndex, this); //drop failed, insert dragged component again
//        }
        lastDraggedOverXXX = null;
        lastDropTarget = null;
        draggedHeight = -1;
        draggedWidth = -1;
        lastY = -1;
        dragImage2 = null;
        lastDragDirection = DragDirection.NONE;
        lastDropPlaceholderInsertPosition = InsertPositionType.NONE;
        newInsertPosition = InsertPositionType.NONE;
        setHidden(false); //unhide (set hidden in dragEnter/dragListener) //done in Component.dragFinishedImpl(int x, int y)
        Form f2 = Display.getInstance().getCurrent();
        if (f2 != null) {
            f2.revalidateWithAnimationSafety();
        };
        super.dragFinished(x, y);
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
////                            refreshAfterDrop(); //TODO need to removeFromCache for a drop which doesn't change anything??
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
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to removeFromCache the entire list)
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
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to removeFromCache the entire list)
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
////            getParent().drop(dragged, x, y); //call CN1 built-in drop to move the visible container (without needing to removeFromCache the entire list)
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
