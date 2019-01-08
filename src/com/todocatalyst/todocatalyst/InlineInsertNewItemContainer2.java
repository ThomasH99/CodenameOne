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
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import com.todocatalyst.todocatalyst.MyForm.Action;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewItemContainer2 extends Container implements InsertNewElementFunc {

    private final static String ENTER_SUBTASK = "New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK = "New task, ->for subtask)"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_TASK_NO_SWIPE_RIGHT = "New task"; //"Task (swipe right: subtask)"

    private boolean insertAsSubtask; //true if the user has selected to insert new task as a subtask of the preceding task
    private MyTextField2 textEntryField2;
    private ItemAndListCommonInterface element;
    private Category category2;
    private MyForm myForm2;
    private ItemAndListCommonInterface itemOrItemListForNewElements;
    private ItemAndListCommonInterface lastCreatedItem;
    private boolean insertBeforeElement = false;
    private Action closeAction = null;
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
    public InlineInsertNewItemContainer2(MyForm myForm2, ItemAndListCommonInterface item2) {
        this(myForm2, item2, item2.getOwner(), null);
    }

    public InlineInsertNewItemContainer2(MyForm myForm2, ItemAndListCommonInterface item2, boolean insertBeforeElement) {
        this(myForm2, item2, item2.getOwner(), null, insertBeforeElement, null);
    }

    public InlineInsertNewItemContainer2(MyForm myForm2, ItemAndListCommonInterface item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
        this(myForm2, item2, itemOrItemListForNewTasks2, null);
    }

    public InlineInsertNewItemContainer2(MyForm myForm2, ItemAndListCommonInterface item2, ItemAndListCommonInterface itemOrItemListForNewTasks2, Category category2) {
        this(myForm2, item2, itemOrItemListForNewTasks2, category2, false);
    }

    /**
     * create a new Container and a new Item
     *
     * @param myForm 
     * @param item item after which to add new task or under which to add new
     * subtask
     * @param itemOrItemListForNewTasks list into which add Items
     * @param category optional category
     * @param insertBeforeElement if true, insert *before* item, instead of, as default, after
     */
    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks, Category category, boolean insertBeforeElement) {
        this(myForm, item, itemOrItemListForNewTasks, category, insertBeforeElement, null);
    }

    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks,
            Category category, boolean insertBeforeElement, Action closeAction) {
        this.myForm2 = myForm;
//        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
        this.element = item; //new Item();
        this.category2 = category; //new Item();
        ASSERT.that(itemOrItemListForNewTasks != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewElements = itemOrItemListForNewTasks;
        this.insertBeforeElement = insertBeforeElement;
        this.closeAction = closeAction;
        if (Config.TEST) {
            setName("InlineInsertNewItemContainer2"); //for debugging
        }
        Container contForTextEntry = new Container(new BorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

        textEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField2.setHint(element == null ? ENTER_TASK_NO_SWIPE_RIGHT : ENTER_TASK); //if no item, then don't show hint about swipe right for subtask
        textEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        myForm.setEditOnShowOrRefresh(textEntryField2); //ensure fields enters edit mode after show() or refresh
        if (element != null) {
            swipC.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon

                insertAsSubtask = swipC.isOpenedToRight();
                textEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK); //item!=null to avoid 
                InlineInsertNewItemContainer2.this.setUIID(insertAsSubtask ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
                ev.consume();
                swipC.close();
                if (insertAsSubtask && !myForm.expandedObjects.contains(element)) {
                    //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
                    myForm.expandedObjects.add(element); //expand to show subtasks
                    myForm.refreshAfterEdit();
                } else {
                    textEntryField2.repaint(); //update with new hint
                    revalidate(); //ensure refresh?! TODO necessary??!!
                }
            });
        }

        //DONE listener - create and insert new task
        textEntryField2.setDoneListener(
                (ev) -> { //When pressing ENTER, insert new task
                    if (!ev.isConsumed() && !swipC.isOpen()) {
                        Item newItem = createNewTask(); //store new task for use when recreating next insert container
//                        if (continueAddingNewItems) {
//                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
//                        } else {
//                            lastCreatedItem = null; //store new task for use when recreating next insert container
//                        }
                        if (newItem != null) {
                            lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
                            insertNewTaskAndSaveChanges(newItem);
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (lastCreatedItem != null) {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                        } else {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
//                        }
//</editor-fold>
                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : element, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
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
                            closeInsertNewItemContainer();
//                            if (continueAddingNewItems) {
//                                lastCreatedItem = newItem; //ensures that MyTree2 will create a new insertContainer after newTask
//                            }
//                            myForm.animateMyForm();
                            myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
                        } else { //if no new item created, remove the container like with Close (x)
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this, new Label(), null);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
                            closeInsertNewItemContainer();
                            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
//                            parent.animateLayout(300); //not necesssary with replace?
                        }
                    }
                }
        );
        contForTextEntry.add(BorderLayout.CENTER, textEntryField2);

        Container westCont = new Container(BoxLayout.x());
        contForTextEntry.add(BorderLayout.WEST, westCont);

        //CLOSE button, only add if in a non-empty list
        if (itemOrItemListForNewElements != null && itemOrItemListForNewElements.size() > 0) {
            westCont.add(new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
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
                closeInsertNewItemContainer();
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
        contForTextEntry.add(BorderLayout.EAST,
                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    Item newTaskTemp = createNewTask();
                    Item newItem = newTaskTemp != null ? newTaskTemp : new Item();
                    lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
                    //TODO!!!! create even if no text was entered into field
                    myForm.setKeepPos(new KeepInSameScreenPosition(element, this)); //if Cancel, keep the current item in place 
//                        new ScreenItem(lastCreatedItem, (MyForm) getComponentForm(), () -> {
                    new ScreenItem(newItem, myForm, () -> {
                        //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
//                            DAO.getInstance().save(newTask);
                        insertNewTaskAndSaveChanges(newItem);
//                        if (false && myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//                            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
//                        }
//                        myForm.setKeepPos(new KeepInSameScreenPosition(newItem));
                        lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
//                        Container parent = getParent();
//                        parent.removeComponent(InlineInsertNewItemContainer2.this);
                        //replace the insert container with the created item, NOT GOOD approach since refrehsAfterEdit will rebuild, and not needed??!!
                        if (false) {
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
                            Container parent = getParent();
                            parent.replace(InlineInsertNewItemContainer2.this,
                                    //                                ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
                                    ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks, null), null, null, 300); //
                        }
                        myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen, 
                        myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new 
                    }).show();
                }
                )));
    }

    /**
     * closes the container with animation
     *
     * @param newItem
     * @param refreshScreen
     */
    private void closeContainerXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
        if (continueAddingNewItems) {
            lastCreatedItem = newItem; //store new task for use when recreating next insert container
        } else {
            lastCreatedItem = null; //store new task for use when recreating next insert container
        }
        if (lastCreatedItem != null) {
            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
        } else {
            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
        }
        Container parent = getParent();
        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
        if (refreshScreen) {
            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
        } else {
            parent.animateLayout(300);
//            myForm.animateMyForm();
        }
    }

    private void setRefreshXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
        if (continueAddingNewItems) {
            lastCreatedItem = newItem; //store new task for use when recreating next insert container
        } else {
            lastCreatedItem = null; //store new task for use when recreating next insert container
        }
        if (lastCreatedItem != null) {
            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
        } else {
            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
        }
        Container parent = getParent();
        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
        if (refreshScreen) {
            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
        } else {
            parent.animateLayout(300);
//            myForm.animateMyForm();
        }
    }

    private void insertNewElementXXX(ItemAndListCommonInterface element) {
        insertNewTaskAndSaveChanges(element);
//        if (myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
//        }
        getParent().removeComponent(InlineInsertNewItemContainer2.this);
        if (continueAddingNewItems) {
            lastCreatedItem = element; //ensures that MyTree2 will create a new insertContainer after newTask
        }
        myForm2.refreshAfterEdit();
    }

    /**
     *
     * @return new task if created (meaning some text was entered), otherwise null
     */
    private Item createNewTask() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        return createNewTask(false);
//    }
//
//    private Item createNewTask(boolean createEvenIfNoTextInField) {
//</editor-fold>
        String taskText = textEntryField2.getText();
        Item newItem;
//        if (createEvenIfNoTextInField || (taskText != null && taskText.length() > 0)) {
        if ((taskText != null && taskText.length() > 0)) {
            textEntryField2.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newItem = new Item(taskText, true); //true: interpret textual values
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
    private void insertNewTaskAndSaveChanges(ItemAndListCommonInterface newItem) {
        DAO.getInstance().save((ParseObject) newItem); //need to save first, other DAO.fetchListElementsIfNeededReturnCachedIfAvail called by getList will complain that no ObjectId
        if (insertAsSubtask) { //add as subtask to previous task, and keep the subtask level
            if (element != null) {
                element.addToList(newItem); //add to end of subtask list (depending on setting for add to beginning/end of lists)
                if (false) {
                    DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) element);
                }
                insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
                myForm2.expandedObjects.add(element); //UI: expand the item to show newly added subtask
            } else {
//                Dialog.show("Internal error", "Could not insert subtask", "OK", null);
                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only inserted into inbox
            }
        } else if (itemOrItemListForNewElements != null && !itemOrItemListForNewElements.isNoSave()) {
            if (category2 != null) {
                ((Item) newItem).addCategoryToItem(category2, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
            }
            //make a sistertask (insert in same list as item, after item)
            //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
            if (element == null) {
                itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
            } else {
                int index = itemOrItemListForNewElements.getItemIndex(element);
                if (index > -1) {
                    itemOrItemListForNewElements.addToList(index + (insertBeforeElement ? 0 : 1), newItem); //add after item, unless insertBeforeElement is true, then insert *before* element
                } else {
                    itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                }
            }
            if (false) {
                DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) itemOrItemListForNewElements);
            } else {
                DAO.getInstance().saveInBackground((ParseObject) itemOrItemListForNewElements);
            }
        } else {
            if (false) {
                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only 'inserted' into inbox, no need to save here, already done above
            }
        }
//        return newItem;
    }

    private void closeInsertNewItemContainer() {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(this);
        if (closeAction != null) {
            closeAction.launchAction();
        }
        parent.animateLayout(300);
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
    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList) {
        if (element == this.element) {
//                return new InlineInsertNewItemContainer2((MyForm)getComponentForm(), this.element, this.itemOrItemListForNewElements);
            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), element, targetList);
        }
        return null;
    }

    /**
     * close the container and save a new element if text has been entered (used
     * to deal properly with an existing inlineContainer when a new
     * inlineContainer is created somewhere else)
     *
     * @param saveAnyEnteredElement
     * @return
     */
//    @Override
    private void closeXXX(boolean saveAnyEnteredElement) {
//        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        if (saveAnyEnteredElement) {
            Item newItem = createNewTask(); //store new task for use when recreating next insert container
            if (newItem != null) {
                lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
                insertNewTaskAndSaveChanges(newItem);
            }
            Container parent = getParent();
//            parent.replace(InlineInsertNewItemContainer2.this,
//                    newItem != null ? ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewElements, null) : new Label(), MorphTransition.create(300));
            parent.removeComponent(InlineInsertNewItemContainer2.this);
            parent.animateHierarchy(300);
//        return newItem;
        }
    }

    @Override
    public TextArea getTextArea() {
        return textEntryField2;
    }

}
