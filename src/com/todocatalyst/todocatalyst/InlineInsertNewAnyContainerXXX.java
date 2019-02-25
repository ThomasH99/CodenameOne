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
import java.util.List;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewAnyContainerXXX extends InlineInsertNewContainer {

    private MyTextField2 textEntryField;
    private ItemAndListCommonInterface refItemList;
    private ItemAndListCommonInterface itemOrItemListForNewItemLists;
    private ItemList newItemList;
    private boolean insertBeforeRefElement;
//    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_ITEMLIST = "New list"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param refElement
     */
    public InlineInsertNewAnyContainerXXX(MyForm myForm, ItemAndListCommonInterface refElement, boolean insertBeforeRefElement) {
//        this(myForm, ItemListList.getInstance(), refItemList, insertBeforeRefElement);
//    }
//
//    public InlineInsertNewItemListContainer(MyForm myForm, ItemList itemList2, ItemList refItemList, boolean insertBeforeRefElement) {
        this.refItemList = refElement;
        ASSERT.that(refElement != null, ()->"why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewItemLists = (ItemListList)refElement.getOwner();
        this.insertBeforeRefElement = insertBeforeRefElement;

        Container contForTextEntry = new Container(new BorderLayout());

        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_ITEMLIST);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
//        taskTextEntryField2.addActionListener((ev) -> {
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newItemList = createNewElement(); //store new category for use when recreating next insert container
                if (newItemList != null) {
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                    myForm.setKeepPos(new KeepInSameScreenPosition(refElement, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : element, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                }
                closeInsertNewContainer();
                insertNewItemListAndSaveChanges(newItemList);
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        contForTextEntry.add(BorderLayout.CENTER, textEntryField);

        //close insert container
        contForTextEntry.add(BorderLayout.WEST, westCont);
        if (itemOrItemListForNewItemLists != null && itemOrItemListForNewItemLists.size() > 0) { //only add close button if in a non-empty list
            westCont.add(new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//                myForm.lastInsertNewElementContainer = null;
                closeInsertNewContainer();
            })));
        }

        //Enter full screen edit of the new Category:
        contForTextEntry.add(BorderLayout.EAST,
                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    if ((newItemList = createNewElement()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                        myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen, 
                        new ScreenItemListProperties(newItemList, (MyForm) getComponentForm(), () -> {
                            insertNewItemListAndSaveChanges(newItemList);
                            myForm.refreshAfterEdit();
                        }).show();
                    } else {
                        ASSERT.that(false, "Something went wrong here, what to do? ...");
                    }
                })));
        add(contForTextEntry);
    }

//    public void insertNewTask(ItemAndListCommonInterface itemOrItemList, ScreenListOfItems myForm) {
    /**
     *
     * @return true if a task was created
     */
    private ItemList createNewElement() {
        String text = textEntryField.getText();
        ItemList newItemList;
        if ( (text != null && text.length() > 0)) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newItemList = new ItemList(text,false); //true: interpret textual values
            return newItemList;
        }
        return null;
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
        int index = itemOrItemListForNewItemLists.getItemIndex(refItemList);
        if (index > -1) {
            itemOrItemListForNewItemLists.addToList(index + (insertBeforeRefElement ? 0 : 1), newItemList); //add after item
        } else {
            itemOrItemListForNewItemLists.addToList(newItemList); //if item is null or not in orgList, insert at beginning of (potentially empty) list
        }
        DAO.getInstance().saveInBackground(newItemList, (ParseObject) itemOrItemListForNewItemLists);
    }

    private void closeInsertNewContainer() {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnCont(this);
        if (parent != null) {
            parent.animateLayout(300);
        }
    }
}
