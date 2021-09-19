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
import java.util.List;

/**
 *
 * @author Thomas
 */
public class PinchInsertWorkSlotContainer extends PinchInsertContainer  {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
//    private WorkSlotList workSlotList;
    private ItemAndListCommonInterface workSlotListOwner;
    private MyForm myForm;
    private WorkSlot refWorkSlotN;
    private WorkSlot newWorkSlot;
    private boolean insertBeforeRefElement;
    private boolean continueAddingNewWorkSlots = true;
    private ItemAndListCommonInterface lastCreatedWorkSlot;
    private Command editNewCmd;
    //    private Container cont=new Container(new BorderLayout());

//    private final static String ENTER_WORKSLOT = "New " + WorkSlot.WORKSLOT; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_WORKSLOT = "Add " + WorkSlot.WORKSLOT; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
     * @param refWorkSlot2N
     */
//    public InlineInsertNewWorkSlotContainer(MyForm myForm, WorkSlot refWorkSlot2, boolean insertBeforeRefElement) {
//        this(myForm, new ItemList(), refWorkSlot2, insertBeforeRefElement);
//    }
//    public InlineInsertNewWorkSlotContainer(MyForm myForm, ItemList itemList2, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeRefElement) {
    public PinchInsertWorkSlotContainer(MyForm form, WorkSlot refWorkSlot2N, ItemAndListCommonInterface workSlotListOwner, boolean insertBeforeRefElement) {
        this.myForm = form;
//        this.workSlotList = workSlotList2;
//        ASSERT.that(refWorkSlot2N != null, "why itemOrItemListForNewTasks2==null here?");
        if (workSlotListOwner != null) {
            this.workSlotListOwner = workSlotListOwner;
        } else if (refWorkSlot2N != null) {
            this.workSlotListOwner = refWorkSlot2N.getOwner();
        }
//        WorkSlotList workSlotList = workSlotListOwner.getWorkSlotListN();
        this.refWorkSlotN = refWorkSlot2N;
        this.insertBeforeRefElement = insertBeforeRefElement;
        continueAddingNewWorkSlots = MyPrefs.workSlotContinueAddingInlineWorkslots.getBoolean();

        Container cont = new Container(new BorderLayout());
        cont.setUIID("InlineInsertWorkSlotCont");

//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), cont);
//        add(swipC);
        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_WORKSLOT);
        textEntryField.setUIID("WorkSlotPinchInsertTextField");
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newWorkSlot = createNewWorkSlot(); //store new category for use when recreating next insert container
                if (newWorkSlot != null) {
                    this.myForm.setKeepPos(new KeepInSameScreenPosition(newWorkSlot, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    lastCreatedWorkSlot = continueAddingNewWorkSlots ? newWorkSlot : null; //store new task for use when recreating next insert container

                }
                insertNewAndSaveChanges(newWorkSlot);
                closePinchContainer(true); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
                this.myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT) != null) {
            textEntryField.setText((String) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT));
        }
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

        cont.add(BorderLayout.CENTER, textEntryField);

//        if (refWorkSlot != null && refWorkSlot.size() > 0) { //only add close button if in a non-empty list
        if (refWorkSlotN != null) { //only add close button if in a non-empty list, which is the case if there is a refWorkSlot
            Button closeButton = new Button(Command.createMaterial("", Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//                myForm.lastInsertNewElementContainer = null;
//                closeInsertNewCategoryContainer(myForm); //close without inserting new task
//                getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                this.remove(); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                this.myForm.animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
                closePinchContainer(true); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
            }));
            closeButton.setUIID("WorkSlotPinchInsertTextCloseButton");
            //close insert container
            cont.add(BorderLayout.WEST, closeButton);
        }

//        editNewCmd = CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
        editNewCmd = MyReplayCommand.create("InlineEditWorkSlot","", Icons.iconEdit, (ev) -> {
            if ((newWorkSlot = createNewWorkSlot()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                lastCreatedWorkSlot = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
                this.myForm.setKeepPos(new KeepInSameScreenPosition(newWorkSlot, this, -1)); //if editing the new task in separate screen,
                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
                new ScreenWorkSlot(newWorkSlot, workSlotListOwner, (MyForm) getComponentForm(), () -> {
                    insertNewAndSaveChanges(newWorkSlot);
                    lastCreatedWorkSlot = continueAddingNewWorkSlots ? newWorkSlot : null; //ensures that MyTree2 will create a new insertContainer after newTask
                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //marker to indicate that the inlineinsert container launched edit of the task
                    closePinchContainer(true);
//                    if(false)this.myForm.refreshAfterEdit();
                }).show();
            } else {
                ASSERT.that(false, "Something went wrong here, what to do? ...");
            }
        });
        //Enter full screen edit of the new WorkSlot:
        Button editButton = new Button(editNewCmd);
        editButton.setUIID("WorkSlotPinchInsertTextEditButton");
        cont.add(BorderLayout.EAST, editButton);
        add(cont);
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
        if (newWorkSlot.getDurationInMillis() == 0) {
            newWorkSlot.setDurationInMinutes(MyPrefs.workSlotDefaultDurationInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS); //UI: if no textual definition, use normal default value
        }//        if (true || createEvenIfNoTextInField || (text != null && text.length() > 0)) {
        if (refWorkSlotN != null && refWorkSlotN.getStartTimeD() != null) {
            if (insertBeforeRefElement) {
                newWorkSlot.setStartTime(new MyDate(refWorkSlotN.getStartTimeD().getTime() - newWorkSlot.getDurationInMillis())); //UI: set pinchInserted workslot to start 'duration' before the startTime of the next workslot
            } else {
                newWorkSlot.setStartTime(refWorkSlotN.getEndTimeD()); //UI: set pinchInserted workslot to start at the end of the previous
            }
        } else {
            newWorkSlot.setStartTime(new MyDate()); //UI: set pinchInserted workslot to start now
        }
        //UI: ensure no overlap with *following* workslot:
        if (refWorkSlotN != null && refWorkSlotN.getOwner() != null && refWorkSlotN.getOwner().getWorkSlotListN() != null) {
            List workslots = refWorkSlotN.getOwner().getWorkSlotListN().getWorkSlotListFull();
            int refIndex = refWorkSlotN.getOwner().getWorkSlotListN().getWorkSlotListFull().indexOf(refWorkSlotN);
            if (refIndex >= 0 && refIndex + 1 < workslots.size()) {
                WorkSlot nextWorkSlot = (WorkSlot) workslots.get(refIndex + 1);
                if (newWorkSlot.getStartTime() > nextWorkSlot.getEndTime()) {
                    newWorkSlot.setEndTime(nextWorkSlot.getStartTimeD(),false); //UI: reduce a pinchinserted workslot overlapping with the next one, to end when the next one starts
                }
            }
        }
//        if (ScreenWorkSlot.checkWorkSlotIsValidForSaving(workSlotListOwner, newWorkSlot, refWorkSlot.getStartTimeD(), refWorkSlot.getDurationInMillis())) {
//        if (ScreenWorkSlot.checkWorkSlotIsValidForSaving(workSlotListOwner, null, newWorkSlot.getStartTimeD(), newWorkSlot.getDurationInMillis())) {
        if (ScreenWorkSlot.checkWorkSlotIsValidForSaving(refWorkSlotN.getOwner(), null, newWorkSlot.getStartTimeD(), newWorkSlot.getDurationInMillis())) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newWorkSlot.setEditedDateToNow();
            return newWorkSlot;
        } else {
            return null;
        }
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newWorkSlot
     * @return
     */
    private void insertNewAndSaveChanges(WorkSlot newWorkSlot) {
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
//        DAO.getInstance().saveNew(newWorkSlot, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newWorkSlot.getObjectIdP()));
//        DAO.getInstance().saveNew((ParseObject) workSlotListOwner, true);
//        DAO.getInstance().saveNew(newWorkSlot);
//        DAO.getInstance().saveNew((ParseObject) workSlotListOwner);
//        DAO.getInstance().saveNewTriggerUpdate();
        DAO.getInstance().saveToParseNow(newWorkSlot);
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_GUID_KEY, newWorkSlot.getObjectIdP());
        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_GUID_KEY, newWorkSlot.getGuid());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT,false); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
//        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
    }

    @Override
    public void closePinchContainer(boolean stopAddingInlineContainers) {
//        closeInsertNewWorkSlotContainer(null);
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParentN(this);
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
        if (stopAddingInlineContainers) {
//            if(false)
                myForm.setPinchInsertContainer(null); //remove this as inlineContainer
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
            myForm.previousValues.removePinchInsertKeys(); //delete the marker on exit
            
            if(false)ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated
        }
        if (parent != null) {
            parent.animateLayout(MyForm.ANIMATION_TIME_DEFAULT);
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void closeInsertNewWorkSlotContainer(MyForm f) {
//        //UI: close the text field
//        Container parent = getParent();
//        if (parent != null) {
//            parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//            if (f != null) {
//                f.animateMyForm();
//            }
//        }
//    }
//</editor-fold>
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
    public PinchInsertContainer make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
        if (element == lastCreatedWorkSlot && element instanceof WorkSlot) {
            return new PinchInsertWorkSlotContainer(myForm, (WorkSlot) lastCreatedWorkSlot, workSlotListOwner, false); //element == lastCreatedWorkSlot, so both are the previously created (now reference) element
        }
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
