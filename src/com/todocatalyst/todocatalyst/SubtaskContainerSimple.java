/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Button;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.todocatalyst.todocatalyst.MyForm.UpdateField;
import java.util.List;
import java.util.Map;

/**
 * Shows and edits the subtasks in an Item (Project). Can expand and collapse
 * the subtask list. When no subtasks, only shows the addNewTask line. Each
 * shown subtask allows the same editing facilities as in lists of items (swipe
 * to insert task after etc).
 *
 * @author Thomas
 */
public class SubtaskContainerSimple extends Container {
    
    private String SUBTASK_CONT_ID = "SubtaskContainerSimple";

    /**
     * creates a Container showing the subtasks. creates a Container that
     * contains a header for the subtasks (showing #subtasks and time), the
     * expanded/collapsed list of subtasks (each expandable as usual), and a
     * quickEntry container to add more subtasks
     *
     * @param item
     * @param previousForm
     * @param templateEditMode
     */
    SubtaskContainerSimple(Item item, MyForm previousForm, boolean templateEditMode,  Map<Object, UpdateField> parseIdMap2) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//        ItemList<Item> subtasksItemList = item.getItemList();
        setLayout(new BorderLayout()); //main container

//        Container subtaskSummary = new Container(new BorderLayout());
//        subtaskSummary.add(BorderLayout.WEST, "Subtasks");

        int numberUndoneSubtasks = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
        int totalNumberSubtasks = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
        int numberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks;

        //HEADER - EDIT LIST IN FULL SCREEN MODE
        Button editSubtasksFullScreen = new Button();
        String subtaskStr = (totalNumberSubtasks == 0
                ? "Add subtasks" : ("" + totalNumberSubtasks + " subtasks" + (numberUndoneSubtasks == 0 ? "" : (", " + numberUndoneSubtasks + " remaining"))));
        editSubtasksFullScreen.setCommand(MyReplayCommand.create("EditSubtasks", subtaskStr, Icons.iconEditPropertiesToolbarStyle, (e) -> {
//            ItemList subtaskList = item.getItemList();
//            List<Item> subtaskList = item.getListFull();
//            new ScreenListOfItems("Subtasks of " + item.getText(), () -> new ItemList(item.getListFull(),true), previousForm, (iList) -> {
            new ScreenListOfItems("Subtasks of " + item.getText(), () -> item, previousForm, (iList) -> {
//                item.setItemList(subtaskList);
//                item.setList(subtaskList);
//                item.setList(iList.getListFull());
//                if (false) 
//                    item.setList((iList); //probably not necessary since all operations on the list (insert, D&D, ...) should update the list on each change
//                DAO.getInstance().saveInBackground(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                parseIdMap2.put(SUBTASK_CONT_ID, ()->DAO.getInstance().saveTemplateCopyWithSubtasksInBackground(item));
                previousForm.refreshAfterEdit(); //necessary to update sum of subtask effort
            }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
            ).show();
        }
        ));

        //HEADER - count + expand button
        add(BorderLayout.CENTER_BEHAVIOR_SCALE, editSubtasksFullScreen);
//        add(BorderLayout.NORTH, subtaskSummary);
    }

}
