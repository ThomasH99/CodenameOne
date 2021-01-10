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
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;

/**
 *
 * @author Thomas
 */
public class PinchInsertCategoryContainer extends PinchInsertContainer  {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
    private Category category;
    private MyForm myForm;
    private ItemAndListCommonInterface categoryList;
    private Category newCategory;
    private boolean insertBeforeElement;
    private Command editNewCmd;
//    private Container cont=new Container(new BorderLayout());

//    private final static String ENTER_CATEGORY = "New " + Category.CATEGORY; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_CATEGORY = "Add " + Category.CATEGORY; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
    public PinchInsertCategoryContainer(MyForm myForm, Category category, boolean insertBeforeElement) {
        this(myForm, category, (CategoryList) category.getOwner(), insertBeforeElement);
    }

    public PinchInsertCategoryContainer(MyForm form, Category category2, CategoryList categoryList, boolean insertBeforeElement) {
        this.myForm = form;
        this.category = category2;
        ASSERT.that(categoryList != null, "why itemOrItemListForNewTasks2==null here?");
        this.categoryList = categoryList;
        this.insertBeforeElement = insertBeforeElement;
        Container cont = new Container(new BorderLayout());
        cont.setUIID("InlineInsertCategoryCont");

//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
//        add(swipC);
        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_CATEGORY);
        textEntryField.setUIID("CatPinchInsertTextField");
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
//        taskTextEntryField2.addActionListener((ev) -> {
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
//                if (textEntryField.getText().length() > 0) {
                newCategory = createNewCategory(); //store new category for use when recreating next insert container
                if (newCategory != null) {
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition(newCategory, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                this.categoryList.addToList(newCategory);
                    insertNewCategoryAndSaveChanges(newCategory);
                    closePinchContainer(true); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
                } else {
                    closePinchContainer(false);
                }
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT) != null) {
            textEntryField.setText((String) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT));
        }
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

        cont.add(BorderLayout.CENTER, textEntryField);

        //close insert container
        if (categoryList != null && categoryList.getSize() > 0) { //only add close button if in a non-empty list
//            westCont.add(new Button(Command.create(null, Icons.iconCloseCircleLabelSty, (ev) -> {
            Button closeButton = new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer. NO, too detailed...
//                myForm.lastInsertNewElementContainer = null;
                //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                closePinchContainer(false);
            }));
            closeButton.setUIID("CatPinchInsertTextCloseButton");
            cont.add(BorderLayout.WEST, closeButton);
        }

//        editNewCmd = CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
        editNewCmd = MyReplayCommand.create("PinchCreateCategory", "", Icons.iconEdit, (ev) -> {
            if ((newCategory = createNewCategory()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                myForm.setKeepPos(new KeepInSameScreenPosition(newCategory, this, -1)); //if editing the new task in separate screen, 
                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
                new ScreenCategoryProperties(newCategory, (MyForm) getComponentForm(), () -> {
                    insertNewCategoryAndSaveChanges(newCategory);
                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //marker to indicate that the inlineinsert container launched edit of the task
                    closePinchContainer(true);
                    if(false)myForm.refreshAfterEdit();
                }).show();
            } else {
                ASSERT.that(false, "Something went wrong here, what to do? ...");
            }
        });
        //Enter full screen edit of the new Category:
        Button editButton = new Button(editNewCmd);
        editButton.setUIID("CatPinchInsertTextEditButton");
        cont.add(BorderLayout.EAST, editButton);

        add(cont);
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
//        return createNewCategory(false);
//    }
//
//    private Category createNewCategory(boolean createEvenIfNoTextInField) {
        String text = textEntryField.getText();
        text = MyUtil.removeTrailingPrecedingSpacesNewLinesEtc(text);
        if (text.length() > 0) {
//        if (ScreenCategoryProperties.checkCategoryIsValidForSaving(text, category,false)) {
            int count = 0;
            String fixedCatName = text;
            while (ScreenCategoryProperties.checkCategoryIsValidForSaving(fixedCatName, category, false) != null) {
                count++;
                fixedCatName = text + " <" + count + ">";
            }
            Category newCategory = new Category(fixedCatName); //true: interpret textual values
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            return newCategory;
        } else {
            return null;
        }
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
//        DAO.getInstance().saveNew((ParseObject)newCategory, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newCategory.getObjectIdP()));
//        DAO.getInstance().saveNew((ParseObject) categoryList, true);
//        DAO.getInstance().saveNew((ParseObject) newCategory);
//        DAO.getInstance().saveNew((ParseObject) categoryList);
        DAO.getInstance().saveToParseNow((ParseObject) newCategory);
//        DAO.getInstance().saveNewTriggerUpdate();
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newCategory.getObjectIdP());
        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_GUID_KEY, newCategory.getGuid());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
    }

//    closePinchContainer(boolean stopAddingInlineContainers)
    public void closePinchContainer(boolean stopAddingInlineContainers) {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(this);
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
        if (true || stopAddingInlineContainers) {
            if(false)myForm.setPinchInsertContainer(null); //remove this as inlineContainer
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
            myForm.previousValues.removePinchInsertKeys(); //delete the marker on exit
            
//            ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated
        }
        if (parent != null && stopAddingInlineContainers) {
            parent.animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
        }
    }

    /**
     * if the textEntry field is in Form f, then it is set to editOnShow
     *
     * @param f
     */
//    public void setTextFieldEditableOnShowXXX(Form f) {
//        if (textEntryField != null) {
//            if (false) {
//                textEntryField.requestFocus();
//            } else {
//                textEntryField.startEditingAsync();
//            }
//        }
//    }
    @Override
    public PinchInsertContainer make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
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
