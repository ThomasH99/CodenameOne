/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Container;
import com.codename1.ui.TextField;
import com.codename1.ui.layouts.BorderLayout;

/**
 * field to enter a new task, typically shown at the bottom of the screen.
 * Hitting Enter will create a new task, or hitting [>] will open editing the
 * task in full screen.
 *
 * @author Thomas
 */
public class QuickAddItemContainerXXX extends Container {

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void createItem() {
////            ((MyForm) getParent().getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
//        ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
//        String taskText = taskTextEntryField.getText();
//        if (taskText != null && taskText.length() > 0) {
//            Item newItem = new Item(taskText);
//            new ScreenItem(newItem, (MyForm) getComponentForm(), () -> {
//                DAO.getInstance().save(newItem);
//                //TODO!!!! if a task is selected in projectEditMode, add this new task *after* it
//
//                ScreenListOfItems.addNewTaskSetTemplateAddToListAndSave(newItem, (MyPrefs.insertNewItemsInStartOfLists.getBoolean()) ? 0 : itemListOrg.getSize(), itemListOrg);
//                newItem.setTemplate(optionTemplateEditMode);
//                myForm.refreshAfterEdit();
//            }).show();
//        }
//    }
//    //    public QuickAddItemContainerXXX(String hintText, boolean projectEditMode, ItemList itemListOrg, MyForm myForm, boolean optionTemplateEditMode) {
//    public QuickAddItemContainerXXX(String hintText, ItemList itemListOrg, MyForm myForm, boolean optionTemplateEditMode) {
//</editor-fold>
//    public QuickAddItemContainerXXX(String hintText, ItemAndListCommonInterface itemListOrg, boolean optionTemplateEditMode) {
//        this(hintText, itemListOrg, optionTemplateEditMode, null);
//    }
    public QuickAddItemContainerXXX(String hintText, ItemAndListCommonInterface itemListOrg, boolean optionTemplateEditMode,
            MyForm.Action onNewTaskAction, MyForm myForm) {
        super();

        Container cont = this;
        cont.setLayout(new BorderLayout());

        MyTextField2 taskTextEntryField = new MyTextField2();
        taskTextEntryField.setHint(hintText);
        taskTextEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE);
        cont.add(BorderLayout.WEST, taskTextEntryField);

//        taskTextEntryField.addActionListener((e) -> {
        taskTextEntryField.setDoneListener((e) -> {
            String taskText = taskTextEntryField.getText();
            taskTextEntryField.setText(""); //clear text
            if (taskText != null && taskText.length() > 0) {
                //TODO!!! check for newline and split and add one task per line
                Item newItem = new Item(taskText,true); //TODO!!!! make sure the taskText is interpreted for inline definitions of estimate etc
                newItem.setTemplate(optionTemplateEditMode);
                DAO.getInstance().save(newItem);
                ScreenListOfItems.addNewTaskToListAndSave(newItem, itemListOrg);
                /*
                after insertion of new task, the list should scroll up to show the just created task above the new QuickAddContainer.
                In this way, the quickAddContainer stays in the same relative position on the screen (no risk that it scrolls our of the screen) and new
                tasks nicely appear above as they should.
                 */
                if (false) {
                    getParent().revalidate(); //refresh parent container?
                }//                ((MyForm) getComponentForm()).refreshAfterEdit();
//                ((MyForm) getComponentForm()).scrollListToTopOrBottom(!MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //scroll to where new item was inserted
                if ( onNewTaskAction != null) {
//                    ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition(newItem, cont)); //scroll to where new item was inserted
                    onNewTaskAction.launchAction();
                }
                if (true) {
                    taskTextEntryField.startEditingAsync(); //stay in field
                }
            }
        });

        cont.add(BorderLayout.EAST, new Button(Command.create(null, Icons.get().iconEditSymbolLabelStyle, (e) -> {
            ((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
            String taskText = taskTextEntryField.getText();
            taskTextEntryField.setText(""); //clear text
            if (true || (taskText != null && taskText.length() > 0)) { //true => allow to click [>] to edit task directly
                Item newItem = new Item(taskText,true);
                newItem.setTemplate(optionTemplateEditMode);
                MyForm thisForm = ((MyForm) getComponentForm());
                new ScreenItem2(newItem, (MyForm) getComponentForm(), () -> {
                    DAO.getInstance().save(newItem);
                    ScreenListOfItems.addNewTaskToListAndSave(newItem, itemListOrg, MyPrefs.insertNewSubtasksInScreenItemInStartOfLists.getBoolean()); //UI: always add subtasks to end of list (otherwise not natual)
//                    ((MyForm) getComponentForm()).refreshAfterEdit();
//                    ((MyForm) getComponentForm()).scrollListToTopOrBottom(!MyPrefs.insertNewItemsInStartOfLists.getBoolean());
                    if (onNewTaskAction != null) {
                        onNewTaskAction.launchAction();
                    }
                    thisForm.scrollListToTopOrBottom(!MyPrefs.insertNewItemsInStartOfLists.getBoolean());
                    taskTextEntryField.startEditingAsync(); //stay in field, TODO: will this work since ScreenItem is shown?!
                }).show();
            }
        })));

        if (false) {
            taskTextEntryField.startEditingAsync(); //stay in field
        }
        myForm.setEditOnShow(taskTextEntryField);

        //TODO!!!! add button to add as subtasks to the selected tasks (if any), otherwise add in default position for new tasks
    }

}
