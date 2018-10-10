/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
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
public class InlineInsertNewCategoryContainer extends Container {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
    private Category category;
//    private MyForm myForm;
    private ItemAndListCommonInterface itemOrItemListForNewTasks;
    private Category newCategory;
    private boolean insertBeforeElement;
//    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_CATEGORY = "New category"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

    /**
     *
     * When swiping left, create a new container below with a textEntry field
     * (and a status?), and a (x) to close it w/o creating a task.
     *
     * When pressing Enter, add a new task (using keepPos) and create a new
     * container below to add another task.
     *
     * When swiping this container right, create a subtasks to the preceding
     * task.
     *
     * When swiping this container left, if the container corresponds to a
     * subtask, change it to a top task.
     *
     * @param item2 if null, new items will be inserted into
     * itemOrItemListForNewTasks2 and swipe right to insert as subtask will not
     * be enabled
     * @param itemOrItemListForNewTasks2 if null
     * @param myForm
     */
    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param itemOrItemListForNewTasks2
     */
//    public InlineInsertNewCategoryContainer(MyForm myForm, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeElement) {
    public InlineInsertNewCategoryContainer(MyForm myForm, Category category, boolean insertBeforeElement) {
        this(myForm, category, category.getOwner(), insertBeforeElement);
    }

    private InlineInsertNewCategoryContainer(MyForm myForm, Category category2, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeElement) {
        this.category = category2;
        ASSERT.that(itemOrItemListForNewTasks2 != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewTasks = itemOrItemListForNewTasks2;
        this.insertBeforeElement = insertBeforeElement;
        Container contForTextEntry = new Container(new BorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_CATEGORY);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)

        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
//        taskTextEntryField2.addActionListener((ev) -> {
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newCategory = createNewCategory(); //store new category for use when recreating next insert container
                if (newCategory != null) {
                    myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                }
                closeInsertNewCategoryContainer();

                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        contForTextEntry.add(BorderLayout.CENTER, textEntryField);

        //close insert container
        contForTextEntry.add(BorderLayout.WEST, westCont);
        if (itemOrItemListForNewTasks != null && itemOrItemListForNewTasks.size() > 0) { //only add close button if in a non-empty list
            westCont.add(new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer. NO, too detailed...
                myForm.lastInsertNewElementContainer = null;
                //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                closeInsertNewCategoryContainer();
            })));
        }

        //Enter full screen edit of the new Category:
        contForTextEntry.add(BorderLayout.EAST,
                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    if ((newCategory = createNewCategory()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                        myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen, 
                        new ScreenCategory(newCategory, (MyForm) getComponentForm(), () -> {
                            insertNewCategoryAndSaveChanges(newCategory);
                            myForm.refreshAfterEdit();
                        }).show();
                    } else {
                        ASSERT.that(false, "Something went wrong here, what to do? ...");
                    }
                })));
    }

    public MyTextField2 getTextField() {
        return textEntryField; //UI: start editing this field, only if empty (to avoid keyboard popping up)
    }

//    public void insertNewTask(ItemAndListCommonInterface itemOrItemList, ScreenListOfItems myForm) {
    /**
     *
     * @return true if a task was created
     */
    private Category createNewCategory() {
        return createNewCategory(false);
    }

    private Category createNewCategory(boolean createEvenIfNoTextInField) {
        String text = textEntryField.getText();
        Category newCategory;
        if (createEvenIfNoTextInField || (text != null && text.length() > 0)) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newCategory = new Category(text); //true: interpret textual values
            return newCategory;
        }
        return null;
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newCategory
     * @return
     */
    private void insertNewCategoryAndSaveChanges(Category newCategory) {
        //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
        int index = itemOrItemListForNewTasks.getItemIndex(category);
        if (index > -1) {
            itemOrItemListForNewTasks.addToList(index + (insertBeforeElement ? 0 : 1), newCategory); //add after item
        } else {
            itemOrItemListForNewTasks.addToList(newCategory); //if item is null or not in orgList, insert at beginning of (potentially empty) list
        }
        DAO.getInstance().saveInBackgroundSequential(newCategory, (ParseObject) itemOrItemListForNewTasks);
    }

    private void closeInsertNewCategoryContainer() {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(this);
        parent.animateLayout(300);
    }

    /**
     * if the textEntry field is in Form f, then it is set to editOnShow
     *
     * @param f
     */
    public void setTextFieldEditableOnShow(Form f) {
        if (textEntryField != null) {
            if (false) {
                textEntryField.requestFocus();
            } else {
                textEntryField.startEditingAsync();
            }
        }
    }

}
