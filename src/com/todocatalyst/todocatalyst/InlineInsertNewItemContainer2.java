/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.MorphTransition;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import com.todocatalyst.todocatalyst.MyForm.Action;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewItemContainer2 extends InlineInsertNewContainer implements InsertNewElementFunc {

    private final static String ENTER_SUBTASK = "New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK = "New task ->for subtask"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_TASK_NO_SWIPE_RIGHT = "New task"; //"Task (swipe right: subtask)"

    private boolean insertAsSubtask = false; //true if the user has selected to insert new task as a subtask of the preceding task, set by Swipe action!
    private MyTextField2 textEntryField2;
//    private ItemAndListCommonInterface refItem;
    private Item refItem;
    private Category category2;
//    private MyForm myForm2;
    private ItemAndListCommonInterface itemOrItemListForNewElements;
    private ItemAndListCommonInterface lastCreatedItem;
    private boolean insertBeforeElement = false;
//    private Action closeAction = null;

    /**
     * if true, a new insertContainer will be added each time a new item is
     * added. This is done in MyTree which will compare each displayed item with
     * the just generated and add a new insertContainer just after the
     * previously created item
     */
    private boolean continueAddingNewItems = true;

    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param item2 item after which to add new task or under which to add new
     * subtask (item2's owner becomes the list in which to insert a new item,
     * *after* item2)
     */
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2) {
//        this(myForm2, item2, item2.getOwner(), null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, boolean insertBeforeElement) {
//        this(myForm2, item2, item2.getOwner(), null, insertBeforeElement, null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
//        this(myForm2, item2, itemOrItemListForNewTasks2, null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2, Category category2) {
//        this(myForm2, item2, itemOrItemListForNewTasks2, category2, false);
//    }
    /**
     * create a new Container and a new Item
     *
     * @param myFormXXX 
     * @param item item after which to add new task or under which to add new
     * subtask
     * @param itemOrItemListForNewTasks list into which add Items
     * @param category optional category
     * @param insertBeforeElement if true, insert *before* item, instead of, as default, after
     */
//    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks, Category category, boolean insertBeforeElement) {
    public InlineInsertNewItemContainer2(MyForm myFormXXX, Item item, ItemAndListCommonInterface itemOrItemListForNewTasks, Category category, boolean insertBeforeElement) {
//        this(myForm, item, itemOrItemListForNewTasks, category, insertBeforeElement, null);
//    }
//
////    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//    public InlineInsertNewItemContainer2(MyForm myForm, Item item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//            Category category, boolean insertBeforeElement, Action closeAction) {
//        this.myForm2 = myFormXXX;
//        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
        this.refItem = item; //new Item();
        this.category2 = category; //new Item();
        ASSERT.that(itemOrItemListForNewTasks != null || category != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewElements = itemOrItemListForNewTasks;
        this.insertBeforeElement = insertBeforeElement;
//        this.closeAction = closeAction;
        continueAddingNewItems = MyPrefs.itemContinueAddingInlineItems.getBoolean();

        if (Config.TEST) {
            setName("InlineInsertNewItemContainer2"); //for debugging
        }
        Container contForTextEntry = new Container(new MyBorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

        textEntryField2 = new MyTextField2(100); //TODO!!!! need field to enter edit mode //UI: 100 width of text field (to avoid showing a small one on eg tablet
        if (Config.TEST) textEntryField2.setName("InlineInsert text field");
        textEntryField2.setHint(refItem == null || !(refItem instanceof Item) ? ENTER_TASK_NO_SWIPE_RIGHT : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //if no item, then don't show hint about swipe right for subtask
        textEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        myForm.setEditOnShowOrRefresh(textEntryField2); //ensure fields enters edit mode after show() or removeFromCache
        if (refItem != null) {
            swipC.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon

                insertAsSubtask = swipC.isOpenedToRight();
                textEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //item!=null to avoid 
//                InlineInsertNewItemContainer2.this.setUIID(insertAsSubtask ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
                setUIID(insertAsSubtask ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
                swipC.setSwipeActivated(insertAsSubtask);
                ev.consume();
                swipC.close();
                swipC.getParent().revalidateWithAnimationSafety();//update with new hint
//<editor-fold defaultstate="collapsed" desc="comment">
//                textEntryField2.repaint(); //update with new hint
//                if (insertAsSubtask && !myForm.expandedObjects.contains(refItem)) {
//                    //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
//                    myForm.expandedObjects.add(refItem); //expand to show subtasks
//                    myForm.refreshAfterEdit();
//                } else {
//                    textEntryField2.repaint(); //update with new hint
//                    revalidate(); //ensure removeFromCache?! TODO necessary??!!
//                }
//</editor-fold>
            });
        }

        //DONE listener - create and insert new task
        textEntryField2.setDoneListener(
                (ev) -> { //When pressing ENTER, insert new task
                    if (!ev.isConsumed() && !swipC.isOpen()) {
                        MyForm myForm = (MyForm) getComponentForm();
                        Item newItem = createNewTask(false); //store new task for use when recreating next insert container
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (continueAddingNewItems) {
//                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
//                        } else {
//                            lastCreatedItem = null; //store new task for use when recreating next insert container
//                        }
//</editor-fold>
                        if (newItem != null) {
                            lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
                            insertNewTaskAndSaveChanges(newItem);
//                            ASSERT.that(newItem.getOwner() != null); //owner will be set to Inbox on saving the item
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (lastCreatedItem != null) {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                        } else {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
//                        }
//</editor-fold>
                            if (false) myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                            else myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : refItem, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//<editor-fold defaultstate="collapsed" desc="comment">
//                        closeInsertNewTaskContainer();
//                            getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                        parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this,
////                                    ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
//                            if (continueAddingNewItems) {
//                                lastCreatedItem = newItem; //ensures that MyTree2 will create a new insertContainer after newTask
//                            }
//                            myForm.animateMyForm();
//</editor-fold>
                            closeInsertContainer();
                            myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
                        } else { //if no new item created, remove the container like with Close (x)
//<editor-fold defaultstate="collapsed" desc="comment">
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this, new Label(), null);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
//</editor-fold>
                            closeInsertContainer();
                            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
//                            parent.animateLayout(300); //not necesssary with replace?
                        }
                    }
                }
        );
        contForTextEntry.add(MyBorderLayout.CENTER, textEntryField2);

        Container westCont = new Container(BoxLayout.x());
        contForTextEntry.add(MyBorderLayout.WEST, westCont);

        //CLOSE button, only add if in a non-empty list
        if (itemOrItemListForNewElements != null && itemOrItemListForNewElements.getSize() > 0) {
            westCont.add(new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//<editor-fold defaultstate="collapsed" desc="comment">
//                myForm.lastInsertNewTaskContainer = null;
//                if (myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//                    myForm.setEditOnShowOrRefresh(null);
//                }
//                Container parent = InlineInsertNewItemContainer2.this.getParent();
//                Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
//
////                parent.replace(InlineInsertNewItemContainer2.this, new Label(), new ); //TODO!!! set a transformation
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this); //TODO!!! add smooth transformation like in ??
//                parent.animateLayout(300);
//</editor-fold>
                MyForm myForm = (MyForm) getComponentForm();
                closeInsertContainer();
                remove();
                myForm.refreshAfterEdit();
//                if (myForm.getInlineInsertContainer() == this) {
                myForm.setInlineInsertContainer(null);
//                myForm.revalidate(); //necessary?!
//<editor-fold defaultstate="collapsed" desc="comment">
//                }
//                closeInsertNewTaskContainer(myForm); //close without inserting new task
//all below now done in MyForm.setInlineInsertContainer(null):
//                Container parent = getParent();
//                parent.removeComponent(this);
//                lastCreatedItem = null; //needed?
////                myForm.animateMyForm();
//                parent.animateLayout(300);
//</editor-fold>
            })));
        }

        //Full screen edit of the new task:
        //                                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
        contForTextEntry.add(MyBorderLayout.EAST,
                new Button(
                        Command.createMaterial(null, Icons.iconEdit, (ev) -> {
//                new Button(MyReplayCommand.create("CreateNewItemInline-" + item.getObjectIdP(), "", Icons.iconEdit, (ev) -> {
//                new Button(MyReplayCommand.create("CreateNewItemInline", "", Icons.iconEdit, (ev) -> { //CANNOT use getObjectId since item is not saved yet (but also not needed to refer new item since all entered date will be stored locally)
                            Item newItem = createNewTask(true);
//                    Item newItem = newTaskTemp != null ? newTaskTemp : new Item();
                            lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
                            //TODO!!!! create even if no text was entered into field
                            MyForm myForm = (MyForm) getComponentForm();
                            myForm.setKeepPos(new KeepInSameScreenPosition(refItem, this)); //if Cancel, keep the current item in place 
                            insertNewTaskAndSaveChanges(newItem); //need to insert task before editing in ScreenItem2 since a repeatRule may be added which needs to know where to add repeat instances!
                            //TODO!!!! How to support that Cancel in ScreenItem2 will delete/remove the just inserted new task??!!
//                        new ScreenItem(lastCreatedItem, (MyForm) getComponentForm(), () -> {
                            new ScreenItem2(newItem, myForm, () -> {
                                //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
//                            DAO.getInstance().save(newTask);
                                if (false) insertNewTaskAndSaveChanges(newItem);
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (false && myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//                            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
//                        }
//                        myForm.setKeepPos(new KeepInSameScreenPosition(newItem));
//</editor-fold>
                                lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
//<editor-fold defaultstate="collapsed" desc="comment">
//                        Container parent = getParent();
//                        parent.removeComponent(InlineInsertNewItemContainer2.this);
//replace the insert container with the created item, NOT GOOD approach since refrehsAfterEdit will rebuild, and not needed??!!
//                        if (false) {
////                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            Container parent = getParent();
//                            parent.replace(InlineInsertNewItemContainer2.this,
//                                    //                                ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
//                                    ScreenListOfItems.buildItemContainer(myFormXXX, newItem, itemOrItemListForNewTasks, null), null, null, 300); //
//                        }
//</editor-fold>
                                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen, 
                                myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new 
                            }).show();
                        })));
    }

    /**
     *
     * @return new task if created (meaning some text was entered), otherwise null
     */
    public Item createNewTask(boolean createEvenIfNoTextInField) {
        String taskText = textEntryField2.getText();
//        Item newItem;
//        if (createEvenIfNoTextInField || (taskText != null && taskText.length() > 0)) {
        if (createEvenIfNoTextInField || (taskText != null && taskText.length() > 0)) {
            textEntryField2.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            Item newItem = new Item(taskText, true); //true: interpret textual values
//            if (itemOrItemListForNewElements instanceof Item)
            newItem.updateValuesInheritedFromOwner(itemOrItemListForNewElements);
            if (category2 != null) {
                newItem.addCategoryToItem(category2, false); //we don't add item to category here (done in xx) since we may still cancel
            }
//            if (itemOrItemListForNewElements != null) {
            ASSERT.that(itemOrItemListForNewElements != null, "InlineInsert: no owner list defined!");
            newItem.setOwner(itemOrItemListForNewElements); //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
//            }
            if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
                newItem.setTemplate(true);
            }
            return newItem;
        }
        return null;
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newItem
     * @return
     */
    private void insertNewTaskAndSaveChanges(Item newItem) {
//        DAO.getInstance().save((ParseObject) newItem); //need to save first, other DAO.fetchListElementsIfNeededReturnCachedIfAvail called by getList will complain that no ObjectId
        if (insertAsSubtask) { //add as subtask to previous task, and keep the subtask level
            if (refItem != null) {
                refItem.addToList(newItem); //UI: add to end of subtask list (depending on setting for add to beginning/end of lists) //TODO!!! might be more visually intuitive to add to start of list, which then it will appear immediately under the parent task, instead of at the end of a (potentially) long pre-existing list?!
                //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
                MyForm myForm = (MyForm) getComponentForm();
                myForm.expandedObjects.add(refItem); //expand to show subtasks
//                    myForm2.refreshAfterEdit();
                if (((ParseObject) refItem).getObjectIdP() != null) //don't save if refItem has not already been saved (to enable Cancel on mother task which adds subtasks
                    DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) refItem);
                insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
            } else
                //            else {
                ////                Dialog.show("Internal error", "Could not insert subtask", "OK", null);
                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only inserted into inbox
//            }
        } else {
            if (category2 != null) { //if category defined,  means we're inserting into list of category items, so the ItemList owning the refItem should be ignored!!
//                ((Item) newItem).addCategoryToItem(category2, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                if (refItem == null) {
                    category2.addItemToCategory(newItem, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                } else {
                    int index = category2.getItemIndex(refItem);
                    if (index > -1) {
                        category2.addItemToCategory(newItem, index + (insertBeforeElement ? 0 : 1), false); //add after item, unless insertBeforeElement is true, then insert *before* element
                    } else {
                        category2.addItemToCategory(newItem, false); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                    }
                }
//                category2.addItemToCategory(newItem, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                //UI: if inserting tasks directly into a category, they will be added to the Inbox list
            } else if (itemOrItemListForNewElements != null && !itemOrItemListForNewElements.isNoSave()) {
                //make a sistertask (insert in same list as item, after item)
                //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
                if (refItem == null) {
                    itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    int index = itemOrItemListForNewElements.getItemIndex(refItem);
//                    if (index > -1) {
//                        itemOrItemListForNewElements.addToList(index + (insertBeforeElement ? 0 : 1), newItem); //add after item, unless insertBeforeElement is true, then insert *before* element
//                    } else {
//                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
//                    }
//</editor-fold>
                    itemOrItemListForNewElements.addToList(newItem, refItem, !insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
                }
                DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
            } else {
                ASSERT.that(false, "pinchInsert with no category and no itemList??!! Saving item in Inbox");
                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only 'inserted' into inbox, no need to save here, already done above //UI: in xx case, inserted into Inbox
            }
        }
//        return newItem;
    }

    private void closeInsertContainer() {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnScrollYCont(this);
//        if (closeAction != null) {
//            closeAction.launchAction();
//        }
//        if (parent != null && parent.getParent() != null) //TODO!!! edge case where inlineinsert is inserted into empty list (no previous elements in list), so seems it doesn't get a scrollY parent - to investigate
//            parent.getParent().animateLayout(300); //this call might be what pushes the effect of refreshAfterEdit as an animation
        if (parent != null) //TODO!!! edge case where inlineinsert is inserted into empty list (no previous elements in list), so seems it doesn't get a scrollY parent - to investigate
            parent.animateLayout(300); //this call might be what pushes the effect of refreshAfterEdit as an animation
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
//    public InlineInsertNewItemContainer2 getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList) {
//        if (myForm.lastInsertNewElementContainer == null) { //TODO Optimization: called for every container, replace by local variable?
//            return null;
//        } else {
//            if (item == myForm.lastInsertNewElementContainer.newItem) {
//                return new InlineInsertNewItemContainer2(myForm, myForm.lastInsertNewElementContainer.newItem, itemOrItemList);
//            } else {
//                return null;
//            }
//        }
//    }
//</editor-fold>
    /**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
    @Override
    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
//        if (element == this.refItem && element instanceof Item) {
//this comparison does not require that lastCreatedItem has been saved (yet - as it may happen in background)
        if (element == lastCreatedItem && element instanceof Item) {
//                return new InlineInsertNewItemContainer2((MyForm)getComponentForm(), this.element, this.itemOrItemListForNewElements);
//            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, null);
//            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, category, false);
            return new InlineInsertNewItemContainer2(null, (Item) element, targetList, category, false);
        }
        return null;
    }

    @Override
    public TextArea getTextArea() {
        return textEntryField2;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * close the container and save a new element if text has been entered (used
//     * to deal properly with an existing inlineContainer when a new
//     * inlineContainer is created somewhere else)
//     *
//     * @param saveAnyEnteredElement
//     * @return
//     */
////    @Override
////    private void closeXXX(boolean saveAnyEnteredElement) {
//////        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////        if (saveAnyEnteredElement) {
////            Item newItem = createNewTask(); //store new task for use when recreating next insert container
////            if (newItem != null) {
////                lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
////                insertNewTaskAndSaveChanges(newItem);
////            }
////            Container parent = getParent();
//////            parent.replace(InlineInsertNewItemContainer2.this,
//////                    newItem != null ? ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewElements, null) : new Label(), MorphTransition.create(300));
////            parent.removeComponent(InlineInsertNewItemContainer2.this);
////            parent.animateHierarchy(300);
//////        return newItem;
////        }
////    }
//
//    /**
//     * closes the container with animation
//     *
//     * @param newItem
//     * @param refreshScreen
//     */
////    private void closeContainerXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
////        if (continueAddingNewItems) {
////            lastCreatedItem = newItem; //store new task for use when recreating next insert container
////        } else {
////            lastCreatedItem = null; //store new task for use when recreating next insert container
////        }
////        if (lastCreatedItem != null) {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////        } else {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
////        }
////        Container parent = getParent();
////        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
////        if (refreshScreen) {
////            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////        } else {
////            parent.animateLayout(300);
//////            myForm.animateMyForm();
////        }
////    }
//
////    private void setRefreshXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
////        if (continueAddingNewItems) {
////            lastCreatedItem = newItem; //store new task for use when recreating next insert container
////        } else {
////            lastCreatedItem = null; //store new task for use when recreating next insert container
////        }
////        if (lastCreatedItem != null) {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////        } else {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
////        }
////        Container parent = getParent();
////        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
////        if (refreshScreen) {
////            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////        } else {
////            parent.animateLayout(300);
//////            myForm.animateMyForm();
////        }
////    }
//
////    private void insertNewElementXXX(ItemAndListCommonInterface element) {
////        insertNewTaskAndSaveChanges(element);
//////        if (myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//////            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
//////        }
////        getParent().removeComponent(InlineInsertNewItemContainer2.this);
////        if (continueAddingNewItems) {
////            lastCreatedItem = element; //ensures that MyTree2 will create a new insertContainer after newTask
////        }
////        myForm2.refreshAfterEdit();
////    }
//</editor-fold>
}
