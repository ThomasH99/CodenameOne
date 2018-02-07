/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewItemContainer2 extends Container implements InsertNewElementFunc {

    private final static String ENTER_SUBTASK = "New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK = "New task, ->for subtask)"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_TASK_NO_SWIPE_RIGHT = "New task"; //"Task (swipe right: subtask)"

    private boolean insertAsSubtask; //true if the user has selected to insert new task as a subtask of the preceding task
    private MyTextField2 taskTextEntryField2;
    private Item item;
    private MyForm myForm;
    private ItemAndListCommonInterface itemOrItemListForNewTasks;
    private Item lastCreatedItem;
    /**
     * if true, a new insertContainer will be added each time a new item is
     * added. This is done in MyTree which will compare each displayed item with
     * the just generated and add a new insertContainer just after the
     * previously created item
     */
    private boolean continueAddingNewItems;


    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param item2 item under which to add subtasks
     * @param itemOrItemListForNewTasks2 list into which add Items
     */
    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
        this.myForm = myForm2;
//        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
        this.item = item2; //new Item();
        ASSERT.that(itemOrItemListForNewTasks2 != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewTasks = itemOrItemListForNewTasks2;
        Container contForTextEntry = new Container(new BorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

        taskTextEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
        taskTextEntryField2.setHint(item == null ? ENTER_TASK_NO_SWIPE_RIGHT : ENTER_TASK); //if no item, then don't show hint about swipe right for subtask
        taskTextEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
        myForm.setEditOnShowOrRefresh(taskTextEntryField2); //ensure fields enters edit mode after show() or refresh
        if (item != null) {
            swipC.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
                insertAsSubtask = swipC.isOpenedToRight();
                taskTextEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK); //item!=null to avoid 
                ev.consume();
                swipC.close();
                taskTextEntryField2.repaint(); //update with new hint
                revalidate(); //ensure refresh?! TODO necessary??!!
            });
        }

        //DONE listener - create and insert new task
        taskTextEntryField2.setDoneListener(
                (ev) -> { //When pressing ENTER, insert new task
                    if (!ev.isConsumed() && !swipC.isOpen()) {
                        Item newItem = createNewTask(); //store new task for use when recreating next insert container
                        if (continueAddingNewItems) {
                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
                        } else {
                            lastCreatedItem = null; //store new task for use when recreating next insert container
                        }
                        insertNewTaskAndSaveChanges(newItem);
                        if (lastCreatedItem != null) {
                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                        } else {
                            myForm.setKeepPos(new KeepInSameScreenPosition(item, this, -1)); //otherwise keep same position of mother-item
                        }
//                        closeInsertNewTaskContainer();
                        getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                        myForm.animateMyForm();

                        myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
                    }
                }
        );
        contForTextEntry.add(BorderLayout.CENTER, taskTextEntryField2);

        Container westCont = new Container(BoxLayout.x());
        contForTextEntry.add(BorderLayout.WEST, westCont);
        if (itemOrItemListForNewTasks != null && itemOrItemListForNewTasks.size() > 0) { //only add close button if in a non-emptp list
            westCont.add(new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//                myForm.lastInsertNewTaskContainer = null;
                if (myForm.getEditFieldOnShowOrRefresh() == taskTextEntryField2) {
                    myForm.setEditOnShowOrRefresh(null);
                }
//                closeInsertNewTaskContainer(myForm); //close without inserting new task
                getParent().removeComponent(this);
                lastCreatedItem = null; //needed?
                myForm.animateMyForm();
            })));
        }

        //Full screen edit of the new task:
        contForTextEntry.add(BorderLayout.EAST,
                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    Item newTaskTemp = createNewTask();
                    Item newTask = newTaskTemp != null ? newTaskTemp : new Item();
                    lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
                    //TODO!!!! create even if no text was entered into field
                    myForm.setKeepPos(new KeepInSameScreenPosition(newTask, this, -1)); //if editing the new task in separate screen, 
//                        new ScreenItem(lastCreatedItem, (MyForm) getComponentForm(), () -> {
                    new ScreenItem(newTask, myForm, () -> {
                        //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
//                            DAO.getInstance().save(newTask);
                        insertNewTaskAndSaveChanges(newTask);
                        if (myForm.getEditFieldOnShowOrRefresh() == taskTextEntryField2) {
                            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
                        }
                        getParent().removeComponent(InlineInsertNewItemContainer2.this);
                        if (continueAddingNewItems) {
                            lastCreatedItem = newTask; //ensures that MyTree2 will create a new insertContainer after newTask
                        }
                        myForm.refreshAfterEdit();
                    }).show();
                }
                )));
    }

    /**
     *
     * @return new task if created, otherwise null
     */
    private Item createNewTask() {
        return createNewTask(false);
    }

    private Item createNewTask(boolean createEvenIfNoTextInField) {
        String taskText = taskTextEntryField2.getText();
        Item newItem;
        if (createEvenIfNoTextInField || (taskText != null && taskText.length() > 0)) {
            taskTextEntryField2.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
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
    private void insertNewTaskAndSaveChanges(Item newItem) {
        if (insertAsSubtask) { //add as subtask to previous task, and keep the subtask level
            if (item != null) {
                item.addToList(newItem); //add to end of subtask list (depending on setting for add to beginning/end of lists)
                DAO.getInstance().saveInBackgroundSequential(newItem, item);
                insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
                myForm.expandedObjects.add(item); //UI: expand the item to show newly added subtask
            } else {
//                Dialog.show("Internal error", "Could not insert subtask", "OK", null);
                DAO.getInstance().saveInBackground(newItem); //task only inserted into inbox
            }
        } else if (itemOrItemListForNewTasks != null && !itemOrItemListForNewTasks.isNoSave()) {
            //make a sistertask (insert in same list as item, after item)
            //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
            if (item == null) {
                itemOrItemListForNewTasks.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
            } else {
                int index = itemOrItemListForNewTasks.getItemIndex(item);
                if (index != -1) {
                    itemOrItemListForNewTasks.addToList(index + 1, newItem); //add after item
                } else {
                    itemOrItemListForNewTasks.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                }
            }
            DAO.getInstance().saveInBackgroundSequential(newItem, (ParseObject) itemOrItemListForNewTasks);
        } else {
            DAO.getInstance().saveInBackground(newItem); //task only inserted into inbox
        }
//        return newItem;
    }

/**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
    public InlineInsertNewItemContainer2 getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList) {
        if (myForm.lastInsertNewTaskContainer == null) { //TODO Optimization: called for every container, replace by local variable?
            return null;
        } else {
            if (item == myForm.lastInsertNewTaskContainer.newItem) {
                return new InlineInsertNewItemContainer2(myForm, myForm.lastInsertNewTaskContainer.newItem, itemOrItemList);
            } else {
                return null;
            }
        }
    }


}
