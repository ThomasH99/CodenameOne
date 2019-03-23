/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Form;
import com.codename1.ui.Label;
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import java.util.Date;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewWorkSlotContainer extends Container implements InsertNewElementFunc {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
//    private WorkSlotList workSlotList;
    private ItemAndListCommonInterface workSlotListOwner;
    private MyForm myForm;
    private WorkSlot refWorkSlot;
    private WorkSlot newWorkSlot;
    private boolean insertBeforeRefElement;
    private boolean continueAddingNewWorkSlots = true;
    private ItemAndListCommonInterface lastCreatedWorkSlot;
    //    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_WORKSLOT = "New " + WorkSlot.WORKSLOT; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
     * @param refWorkSlot2
     */
//    public InlineInsertNewWorkSlotContainer(MyForm myForm, WorkSlot refWorkSlot2, boolean insertBeforeRefElement) {
//        this(myForm, new ItemList(), refWorkSlot2, insertBeforeRefElement);
//    }
//    public InlineInsertNewWorkSlotContainer(MyForm myForm, ItemList itemList2, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeRefElement) {
    public InlineInsertNewWorkSlotContainer(MyForm myForm, WorkSlot refWorkSlot2, boolean insertBeforeRefElement) {
        this.myForm = myForm;
//        this.workSlotList = workSlotList2;
        this.workSlotListOwner = refWorkSlot2.getOwner();
//        WorkSlotList workSlotList = workSlotListOwner.getWorkSlotListN();
        this.refWorkSlot = refWorkSlot2;
        ASSERT.that(refWorkSlot != null, "why itemOrItemListForNewTasks2==null here?");
        this.insertBeforeRefElement = insertBeforeRefElement;
        continueAddingNewWorkSlots = MyPrefs.workSlotContinueAddingInlineWorkslots.getBoolean();

        Container contForTextEntry = new Container(new BorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_WORKSLOT);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newWorkSlot = createNewWorkSlot(); //store new category for use when recreating next insert container
                if (newWorkSlot != null) {
                    myForm.setKeepPos(new KeepInSameScreenPosition(newWorkSlot, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    lastCreatedWorkSlot = continueAddingNewWorkSlots ? newWorkSlot : null; //store new task for use when recreating next insert container

                }
                closeInsertNewWorkSlotContainer();
                insertNewWorkSlotAndSaveChanges(newWorkSlot);
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        contForTextEntry.add(BorderLayout.CENTER, textEntryField);

        //close insert container
        contForTextEntry.add(BorderLayout.WEST, westCont);
//        if (refWorkSlot != null && refWorkSlot.size() > 0) { //only add close button if in a non-empty list
        if (refWorkSlot != null) { //only add close button if in a non-empty list, which is the case if there is a refWorkSlot
            westCont.add(new Button(CommandTracked.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//                myForm.lastInsertNewElementContainer = null;
//                closeInsertNewCategoryContainer(myForm); //close without inserting new task
//                getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                this.remove(); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                myForm.animateLayout(300);
            }, "EditItemFromInsertNewContainer")));
        }

        //Enter full screen edit of the new WorkSlot:
        contForTextEntry.add(BorderLayout.EAST,
                new Button(CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    if ((newWorkSlot = createNewWorkSlot()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                        lastCreatedWorkSlot = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
                        myForm.setKeepPos(new KeepInSameScreenPosition(newWorkSlot, this, -1)); //if editing the new task in separate screen, 
                        new ScreenWorkSlot(newWorkSlot, workSlotListOwner, (MyForm) getComponentForm(), () -> {
                            insertNewWorkSlotAndSaveChanges(newWorkSlot);
                            lastCreatedWorkSlot = continueAddingNewWorkSlots ? newWorkSlot : null; //ensures that MyTree2 will create a new insertContainer after newTask
                            myForm.refreshAfterEdit();
                        }).show();
                    } else {
                        ASSERT.that(false, "Something went wrong here, what to do? ...");
                    }
                }, "EditItemListProperties")));
    }

    public MyTextField2 getTextField() {
//        getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        return textEntryField; //UI: start editing this field, only if empty (to avoid keyboard popping up)
//        taskTextEntryField2.requestFocus(); //enter edit mode??
    }

    /**
     *
     * @return true if a task was created
     */
    private WorkSlot createNewWorkSlot() {
//        return createNewWorkSlot(false);
//    }
//
//    private WorkSlot createNewWorkSlot(boolean createEvenIfNoTextInField) {
        String text = textEntryField.getText();
        WorkSlot newWorkSlot = new WorkSlot(); //true: interpret textual values
        newWorkSlot.setText(text); //will interpret a textual duration like "5m" as 5 minutes
        if (newWorkSlot.getDurationInMillis()==0)
        newWorkSlot.setDurationInMinutes(MyPrefs.workSlotDefaultDurationInMinutes.getInt()); //UI: if no textual definition, use normal default value
//        if (true || createEvenIfNoTextInField || (text != null && text.length() > 0)) {
        if (refWorkSlot != null && refWorkSlot.getStartTimeD() != null) {
            if (insertBeforeRefElement)
                newWorkSlot.setStartTime(new Date(refWorkSlot.getStartTimeD().getTime() - newWorkSlot.getDurationInMillis())); //UI: set pinchInserted workslot to start 'duration' before the startTime of the next workslot
            else
                newWorkSlot.setStartTime(refWorkSlot.getEndTimeD()); //UI: set pinchInserted workslot to start at the end of the previous
        } else
            newWorkSlot.setStartTime(new Date()); //UI: set pinchInserted workslot to start now

        if (ScreenWorkSlot.checkWorkSlotIsValidForSaving(workSlotListOwner, refWorkSlot.getStartTimeD(), refWorkSlot.getDurationInMillis())) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            return newWorkSlot;
        } else return null;
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newWorkSlot
     * @return
     */
    private void insertNewWorkSlotAndSaveChanges(WorkSlot newWorkSlot) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        int index = workSlotList.getItemIndex(refWorkSlot);
//        if (index > -1) {
////            itemOrItemListForNewTasks.addToList(index + (insertBeforeRefElement ? 0 : 1), newItemList); //add after item
//            workSlotList.addToList(index + 1, newWorkSlot); //add after item
//        } else {
//            workSlotList.addToList(newWorkSlot); //if item is null or not in orgList, insert at beginning of (potentially empty) list
//        }
//</editor-fold>
//        workSlotList.add(newWorkSlot); //no need to insert sorted, workSlotLists are sorted by workSlot.startDate
        workSlotListOwner.addWorkSlot(newWorkSlot); //no need to insert sorted, workSlotLists are sorted by workSlot.startDate
//        DAO.getInstance().saveInBackground(newWorkSlot, (ParseObject) refWorkSlot);
        DAO.getInstance().saveInBackground(newWorkSlot, (ParseObject) workSlotListOwner);
    }

    private void closeInsertNewWorkSlotContainer() {
        closeInsertNewWorkSlotContainer(null);
    }

    private void closeInsertNewWorkSlotContainer(MyForm f) {
        //UI: close the text field
        Container parent = getParent();
        if (parent != null) {
            parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
            if (f != null) {
                f.animateMyForm();
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * if the textEntry field is in Form f, then it is set to editOnShow
     *
     * @param f
     */
//    public void setTextFieldEditableOnShow(Form f) {
////        if (lastInsertNewTaskContainer != null && f.equals(lastInsertNewTaskContainer.getComponentForm())
//        if (textEntryField != null) {
////            f.setEditOnShow(lastInsertNewTaskContainer.taskTextEntryField2);
//            if (false) {
//                textEntryField.requestFocus();
//            } else {
//                textEntryField.startEditingAsync();
//            }
//        }
//    }
//</editor-fold>
    @Override
    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
        if (element == lastCreatedWorkSlot && element instanceof WorkSlot) {
            return new InlineInsertNewWorkSlotContainer(null, (WorkSlot) lastCreatedWorkSlot, false); //element == lastCreatedWorkSlot, so both are the previously created (now reference) element
        }
        return null;
    }

    @Override
    public TextArea getTextArea() {
        return textEntryField;
    }

}
