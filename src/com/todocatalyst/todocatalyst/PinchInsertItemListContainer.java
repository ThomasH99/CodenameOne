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
import com.codename1.ui.layouts.MyBorderLayout;
import com.parse4cn1.ParseObject;

/**
 *
 * @author Thomas
 */
public class PinchInsertItemListContainer extends PinchInsertContainer {

    private MyTextField2 textEntryField;
    private MyForm myForm;
    private ItemList refItemList;
    private ItemListList itemOrItemListForNewItemLists;
    private ItemList newItemList;
    private boolean insertBeforeRefElement;
    private Command editNewCmd;
//    private Container cont=new Container(new BorderLayout());

//    private final static String ENTER_ITEMLIST = "New " + ItemList.ITEM_LIST; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_ITEMLIST = "Add " + ItemList.ITEM_LIST; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
     * @param refItemList
     */
    public PinchInsertItemListContainer(MyForm form, ItemList refItemList, ItemListList ownerList, boolean insertBeforeRefElement) {
//        this(myForm, ItemListList.getInstance(), refItemList, insertBeforeRefElement);
//    }
//
//    public InlineInsertNewItemListContainer(MyForm myForm, ItemList itemList2, ItemList refItemList, boolean insertBeforeRefElement) {
        this.myForm = form;
        this.refItemList = refItemList;
        ASSERT.that(refItemList != null, () -> "why itemOrItemListForNewTasks2==null here?");
//        this.itemOrItemListForNewItemLists = (ItemListList) refItemList.getOwner();

        if (ownerList != null) {
            this.itemOrItemListForNewItemLists = ownerList;
        } else if (refItemList != null) {
            this.itemOrItemListForNewItemLists = (ItemListList) refItemList.getOwner();
        }

        this.insertBeforeRefElement = insertBeforeRefElement;

//        Container cont = new Container(new MyBorderLayout(MyBorderLayout.SIZE_EAST_BEFORE_WEST));
        Container cont = new Container(new BorderLayout());
        cont.setUIID("InlineInsertListCont");

        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setUIID("ListPinchInsertTextField");
        textEntryField.putClientProperty("iosHideToolbar", Boolean.TRUE); //hide toolbar and only show Done button for ios virtual keyboard

        textEntryField.setHint(ENTER_ITEMLIST);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
//        taskTextEntryField2.addActionListener((ev) -> {
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newItemList = createNewItemList(); //store new category for use when recreating next insert container
                if (newItemList != null) {
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    myForm.setKeepPos(new KeepInSameScreenPosition(refItemList, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : element, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    insertNewItemListAndSaveChanges(newItemList);
                    closePinchContainer(true); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
                } else {
//                    closePinchContainer(false); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
                    closePinchContainer(true); //MUST do *after* insertNewItemListAndSaveChanges() to remove the locally stored values correctly(??!)
                }
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT) != null) {
            textEntryField.setText((String) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT));
        }
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

//        contForTextEntry.add(BorderLayout.CENTER, textEntryField);
        cont.add(MyBorderLayout.CENTER, textEntryField);

        //close insert container
//        cont.add(MyBorderLayout.WEST, westCont);
        if (itemOrItemListForNewItemLists != null && itemOrItemListForNewItemLists.getSize() > 0) { //only add close button if in a non-empty list
            Button closeButton = new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//                myForm.lastInsertNewElementContainer = null;
                closePinchContainer(true);
            }));
            closeButton.setUIID("ListPinchInsertTextCloseButton");
            cont.add(MyBorderLayout.WEST, closeButton);
        }

//        editNewCmd = CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
        editNewCmd = MyReplayCommand.create("PinchCreateItemList", "", Icons.iconEdit, (ev) -> {
            if ((newItemList = createNewItemList()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen, 
                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
                new ScreenItemListProperties(newItemList, (MyForm) getComponentForm(), () -> {
                    insertNewItemListAndSaveChanges(newItemList);
                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //marker to indicate that the inlineinsert container launched edit of the task
                    closePinchContainer(true);
//                    if(false)myForm.refreshAfterEdit();
                }).show();
            } else {
                ASSERT.that(false, "Something went wrong here, what to do? ...");
            }
        });
        //Enter full screen edit of the new Category:
        Button editItemFullScreen = new Button(editNewCmd);
        editItemFullScreen.setUIID("ListPinchInsertTextEditButton");
        cont.add(BorderLayout.EAST, editItemFullScreen);
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
    private ItemList createNewItemList() {
//        return createNewItemList(false);
//    }
//
//    private ItemList createNewItemList(boolean createEvenIfNoTextInField) {
        String text = textEntryField.getText();
        text = MyUtil.removeTrailingPrecedingSpacesNewLinesEtc(text);
        if (text.length() > 0) {

//        if (createEvenIfNoTextInField || (text != null && text.length() > 0)) {
//        if (ScreenItemListProperties.checkItemListIsValidForSaving(text)) {
            int count = 0;
            String fixedCatName = text;
            while (ScreenItemListProperties.checkItemListIsValidForSaving(fixedCatName, itemOrItemListForNewItemLists, false) != null) {
                count++;
                fixedCatName = text + "<" + count + ">";
            }
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            ItemList newItemList = new ItemList(fixedCatName, false); //true: interpret textual values
            return newItemList;
        } else {
            return null;
        }
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newItemList
     * @return
     */
    private void insertNewItemListAndSaveChanges(ItemList newItemList) {
        //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
//<editor-fold defaultstate="collapsed" desc="comment">
//        int index = itemOrItemListForNewItemLists.getItemIndex(refItemList);
//        if (index > -1) {
//            itemOrItemListForNewItemLists.addToList(index + (insertBeforeRefElement ? 0 : 1), newItemList); //add after item
//        } else {
//            itemOrItemListForNewItemLists.addToList(newItemList); //if item is null or not in orgList, insert at beginning of (potentially empty) list
//        }
//</editor-fold>
        itemOrItemListForNewItemLists.addToList(newItemList, refItemList, !insertBeforeRefElement); //add after item
        ASSERT.that(myForm.previousValues.get(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) == null,
                "old value left for SAVE_LOCALLY_INSERT_BEFORE_REF_ELT=" + myForm.previousValues.get(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT));
//        DAO.getInstance().saveNew((ParseObject)newItemList, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItemList.getObjectIdP()));
//        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewItemLists,true);
//        DAO.getInstance().saveNew((ParseObject) newItemList);
//        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewItemLists);
//        DAO.getInstance().saveNewTriggerUpdate();
        DAO.getInstance().saveToParseNow((ParseObject) newItemList);
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItemList.getObjectIdP());
        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_GUID_KEY, newItemList.getGuid());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT,false); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
    }

//    public void closePinchCont(boolean newListInserted) {
    public void closePinchContainer(boolean stopAddingInlineContainers) {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParentN(this);
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
        if (true || stopAddingInlineContainers) {
//            if(false)
            myForm.setPinchInsertContainer(null); //remove this as inlineContainer
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
            myForm.previousValues.removePinchInsertKeys(); //delete the marker on exit
//            ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated
        }
        if (parent != null && stopAddingInlineContainers) { //only animate if container was closed (otherwise slow update will mean 
            parent.animateLayout(MyForm.ANIMATION_TIME_DEFAULT); //parent of parent since pinchcontainer is kept inside a variable height container
        }
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
    public PinchInsertContainer make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
        //no big need to create multiple categories in a row
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
