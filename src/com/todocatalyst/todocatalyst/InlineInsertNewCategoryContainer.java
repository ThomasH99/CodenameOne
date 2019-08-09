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
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewCategoryContainer extends InlineInsertNewContainer implements InsertNewElementFunc {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
    private Category category;
    private MyForm myForm;
    private ItemAndListCommonInterface categoryList;
    private Category newCategory;
    private boolean insertBeforeElement;
    private Command editNewCmd;
//    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_CATEGORY = "New " + Category.CATEGORY; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
        this(myForm, category, (CategoryList) category.getOwner(), insertBeforeElement);
    }

    private InlineInsertNewCategoryContainer(MyForm form, Category category2, CategoryList categoryList, boolean insertBeforeElement) {
        this.myForm = form;
        this.category = category2;
        ASSERT.that(categoryList != null, "why itemOrItemListForNewTasks2==null here?");
        this.categoryList = categoryList;
        this.insertBeforeElement = insertBeforeElement;
        Container contForTextEntry = new Container(new MyBorderLayout());

//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
//        add(swipC);
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
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                }
                closeInsertNewContainer(true);
//                this.categoryList.addToList(newCategory);
                insertNewCategoryAndSaveChanges(newCategory);
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT) != null)
            textEntryField.setText((String) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT));
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

        contForTextEntry.add(MyBorderLayout.CENTER, textEntryField);

        //close insert container
        contForTextEntry.add(MyBorderLayout.WEST, westCont);
        if (categoryList != null && categoryList.getSize() > 0) { //only add close button if in a non-empty list
//            westCont.add(new Button(Command.create(null, Icons.iconCloseCircleLabelSty, (ev) -> {
            westCont.add(new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer. NO, too detailed...
//                myForm.lastInsertNewElementContainer = null;
                //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                closeInsertNewContainer(true);
            })));
        }

//        editNewCmd = CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
        editNewCmd = CommandTracked.create(null, Icons.iconEdit, (ev) -> {
            if ((newCategory = createNewCategory()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen, 
                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
                new ScreenCategoryProperties(newCategory, (MyForm) getComponentForm(), () -> {
                    insertNewCategoryAndSaveChanges(newCategory);
                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //marker to indicate that the inlineinsert container launched edit of the task
                    myForm.refreshAfterEdit();
                }).show();
            } else {
                ASSERT.that(false, "Something went wrong here, what to do? ...");
            }
        }, "InlineEditCategory");
        //Enter full screen edit of the new Category:
        contForTextEntry.add(MyBorderLayout.EAST, new Button(editNewCmd));
        add(contForTextEntry);
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

        if (ScreenCategoryProperties.checkCategoryIsValidForSaving(text, category)) {
            Category newCategory = new Category(text); //true: interpret textual values
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            return newCategory;
        } else
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
//        int index = categoryList.getItemIndex(category);
//        if (index > -1) {
//            categoryList.addToList(index + (insertBeforeElement ? 0 : 1), newCategory); //add after item
//        } else {
//            categoryList.addToList(newCategory); //if item is null or not in orgList, insert at beginning of (potentially empty) list
//        }
        categoryList.addToList(newCategory, category, !insertBeforeElement); //add after item
        DAO.getInstance().saveInBackground(newCategory, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newCategory.getObjectIdP()));
        DAO.getInstance().saveInBackground((ParseObject) categoryList);
        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
    }

    private void closeInsertNewContainer(boolean stopAddingInlineContainers) {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnScrollYCont(this);
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
        if (stopAddingInlineContainers) {
            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
            ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated
        }
        if (parent != null)
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

    @Override
    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
        return null;
    }

    @Override
    public TextArea getTextArea() {
        return textEntryField;
    }

    @Override
    public Command getEditTaskCmd() {
        return editNewCmd;
    }

}
