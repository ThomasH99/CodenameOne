/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import static com.todocatalyst.todocatalyst.ScreenListOfItems.buildItemContainer;
import java.util.HashSet;
//import static com.todocatalyst.todocatalyst.MyForm.putEditedValues2;

/**
 * Shows and edits the subtasks in an Item (Project). Can expand and collapse
 * the subtask list. When no subtasks, only shows the addNewTask line. Each
 * shown subtask allows the same editing facilities as in lists of items (swipe
 * to insert task after etc).
 *
 * @author Thomas
 */
public class SubtaskContainer extends Container {
    //TODO add button to filter subtasks (eg hide/show Done tasks)

    HashSet<ItemAndListCommonInterface> expandedObjects = new HashSet();

    @Override
    public Dimension calcPreferredSize() {
        Dimension pref = super.calcPreferredSize();
        int subtaskContMaxHeigth = getParent().getHeight() / 2;
        if (pref.getHeight() >= subtaskContMaxHeigth) {
            pref.setHeight(subtaskContMaxHeigth); //TODO show at least 1 1/2 subtask
        }
        return pref;
    }

    /**
     * make a Tree container for editing tasks (used in ScreenListOfItems and
     * ScreenItem for editing subtasks.
     *
     * @param listOfItems
     * @param expandedObjects
     * @return
     */
//    static public Container makeMyTree2ForSubTasks(ItemList listOfItems, HashSet<ItemAndListCommonInterface> expandedObjects) {
    static public MyTree2 makeMyTree2ForSubTasks(MyForm myForm, ItemAndListCommonInterface listOfItems,
            HashSet<ItemAndListCommonInterface> expandedObjects) {
//        if (listOfItems != null && listOfItems.size() > 0) {
        MyTree2 myTree = new MyTree2(listOfItems, expandedObjects,
                myForm.lastInsertNewTaskContainer != null ? (item, itemOrItemList) -> myForm.lastInsertNewTaskContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList) : null) {
            @Override
            protected Component createNode(Object node, int depth, Category category) {
//                    return createNode(node, depth, itemListOrg, category);
//                    ItemAndListCommonInterface owner = (node instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) node).getOwner() != null) ? ((ItemAndListCommonInterface) node).getOwner() : itemListOrg;
                ItemAndListCommonInterface owner = (node instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) node).getOwner() != null)
                        ? ((ItemAndListCommonInterface) node).getOwner() : listOfItems;
                return createNode(node, depth, owner, category);
            }

            @Override
            protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
//                    Container cmp = null;
//                    if (node instanceof Item) {
//                    Container cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItems.this, (Item) node, itemOrItemList, category);
//                    Container cmp = ScreenListOfItems.buildItemContainer((MyForm) this.getComponentForm(), (Item) node, itemOrItemList, category);
                Container cmp = buildItemContainer(myForm, (Item) node, itemOrItemList, category);
//                    }
                setIndent(cmp, depth);
                return cmp;
            }
        };
        return myTree;
//        } 
//        else {
//            return new InsertNewTaskContainer(null, listOfItems);
//        }
    }

//        private Container createSubtaskContainer(Item item, MyForm screen, ItemList itemListOrg, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//    SubtaskContainer(Item item, ItemAndListCommonInterface itemListOrg, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//        this(item, (MyForm)getComponentForm(), itemListOrg, templateEditMode);
//    }
//    SubtaskContainer(Item item, MyForm myForm, ItemAndListCommonInterface itemListOrg, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//    SubtaskContainer(Item item, MyForm myForm, ItemAndListCommonInterface itemListOrg, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
    /**
     * creates a Container showing the subtasks. creates a Container that
     * contains a header for the subtasks (showing #subtasks and time), the
     * expanded/collapsed list of subtasks (each expandable as usual), and a
     * quickEntry container to add more subtasks
     *
     * @param item
     * @param myForm
     * @param templateEditMode
     */
    SubtaskContainer(Item item, MyForm myForm, boolean templateEditMode) { //    HashSet<ItemAndListCommonInterface> expandedObjects
//        MyForm myForm = (MyForm) getComponentForm();
//SUBTASKS
        ItemList<Item> subtasksItemList = item.getItemList();
        boolean hasSubtasks = subtasksItemList.size() != 0;
        boolean hideSubtasks;
//        boolean showSubtasksExpanded = MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean() && hasSubtasks;

//        Container subtaskContainer = new Container(new BorderLayout()); //main container
        Container subtaskContainer = this;
        subtaskContainer.setLayout(new BorderLayout()); //main container
//        subtaskContainer.setPreferredSize(d);

        //ADD NEW SUBTASK - SOUTH
//        Container quickAddTaskField = new QuickAddItemContainer("Add subtask", item, templateEditMode, () -> revalidate()); //TODO!!!! must refresh Screen when adding first tasks!
        Container quickAddTaskField = new QuickAddItemContainer("Add subtask", item, templateEditMode, () -> {
            myForm.refreshAfterEdit();
            myForm.scrollListToTopOrBottom(!MyPrefs.insertNewItemsInStartOfLists.getBoolean()); //scroll to where new item was inserted
        }, myForm); //TODO!!!! must refresh Screen when adding first tasks!
        quickAddTaskField.setHidden(hasSubtasks && !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()); //hide quickAdd if either has no subtasks or if default setting is to show Subtasks
        subtaskContainer.add(BorderLayout.SOUTH, quickAddTaskField);
//            addSubtaskField.setHidden(!(!hasSubtasks || MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()));

//        if (hasSubtasks) {
        if (true) {
            //SUBTASK LIST
//<editor-fold defaultstate="collapsed" desc="comment">
//            Container subtasks = new Container(BoxLayout.x());
//            subtasks.setScrollableY(false);
////            subtasks.setAlwaysTensile(false);
////            subtasks.setAlwaysTensile(true);
//            subtasks.add(ScreenListOfItems.makeMyTree2ForSubTasks(myForm, itemListOrg, myForm.expandedObjects));
//            subtasks.setHidden(!showSubtasksExpanded); //hasSubtasks);
//</editor-fold>
//            Container subtasks = ScreenListOfItems.makeMyTree2ForSubTasks(myForm, itemListOrg, myForm.expandedObjects);
//            MyTree2 subtasks = ScreenListOfItems.makeMyTree2ForSubTasks(myForm, item, expandedObjects);
            MyTree2 subtasks = makeMyTree2ForSubTasks(myForm, item, expandedObjects);
//<editor-fold defaultstate="collapsed" desc="comment">
//            subtasks.setHidden(!showSubtasksExpanded); //hasSubtasks);
//            subtasks.setHidden(!(MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean() && hasSubtasks)); //hasSubtasks);
//            subtasks.setHidden(!hasSubtasks || !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()); //hasSubtasks);
//</editor-fold>
//            subtasks.setHidden(!hasSubtasks || !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean()); //hasSubtasks);
            hideSubtasks = !hasSubtasks || !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean(); //hasSubtasks);

            //HEADER
//            subtaskContainer.add(BorderLayout.CENTER, subtasks);
            if (!hideSubtasks) {
                subtaskContainer.add(BorderLayout.EAST, subtasks);
            }
            Container subtaskHeader = new Container(new BorderLayout());
//            subtaskHeaderCont.setHidden(!hasSubtasks);
            subtaskHeader.add(BorderLayout.WEST, "Subtasks");
            //expand button to show 
//            Button showSubtasks = new Button();
//            showSubtasks.setCommand(Command.create(null, showSubtasksExpanded ? Icons.iconShowLessLabelStyle : Icons.iconShowMoreLabelStyle, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                showSubtasks.setCommand(Command.create("[" + subtasksItemList.size() + "]", null, (e) -> {
//                    MyPrefs.alwaysShowSubtasksExpandedInScreenItem.flipBoolean();
//                    Boolean hideSubtasks = !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean();
//                    addSubtaskField.setHidden(hideSubtasks);
//                    subtasks.setHidden(hideSubtasks);
////                showSubtasks.setIcon(hideSubtasks ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//                    this.animateLayout(300);
//                }));
//
//                Button editSubtasksFullScreenOLD = new Button();
//                editSubtasksFullScreenOLD.setCommand(Command.create(null, Icons.iconEditPropertiesLabelStyle, (e) -> {
//                    ItemList itemList = item.getItemList();
//                    new ScreenListOfItems(item.getText(), itemList, myForm, (iList) -> {
//                        item.setItemList(itemList);
//                        DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        myForm.refreshAfterEdit(); //necessary to update sum of subtask effort
//                    }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
//                    ).show();
//                }));
//            }
//</editor-fold>
            int numberUndoneSubtasks = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
            int totalNumberSubtasks = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
//        int totalNumberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks; //true: get subtasks, always necessary for a project
            if (numberUndoneSubtasks > 0 || totalNumberSubtasks > 0) {
                Button showSubtasks = new Button() {
                    @Override
                    public void longPointerPress(int x, int y) {
                        super.longPointerPress(x, y);
                        if (true) { //TODO!! Activate longpress to expand all levels of subtasks
                            putClientProperty("LongPress", Boolean.TRUE); //is unset in MyTree2.Handler.actionPerformed()
                            Log.p("longPointerPress");
//                                Object e = swipCont.getClientProperty(MyTree2.KEY_EXPANDED);
                            Object e = subtaskContainer.getClientProperty(MyTree2.KEY_EXPANDED);
//                                MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(swipCont);
                            MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(subtaskContainer);
                            if (e != null && e.equals("true")) {
//                                    myTree.collapseNode(swipCont, true);
                                myTree.collapseNode(subtaskContainer, true);
                            } else {
//                                    myTree.expandNode(false, swipCont, true);
                                myTree.expandNode(false, subtaskContainer, true);
                            }
                        }
                    }
                };
//            subTasksButton.setUIID("Label");
//                showSubtasks.setHidden(!(numberUndoneSubtasks > 0 || totalNumberSubtasks > 0));
                showSubtasks.setUIID("ListOfItemsSubtasks");
                Command expandSubTasks = Command.create(numberUndoneSubtasks + "/" + totalNumberSubtasks, null, (e) -> {
                    MyPrefs.alwaysShowSubtasksExpandedInScreenItem.flipBoolean();
//                    Boolean hideSubtasks = !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean();
                    Boolean hideSubtasksNow = !MyPrefs.alwaysShowSubtasksExpandedInScreenItem.getBoolean();
                    quickAddTaskField.setHidden(hideSubtasksNow);
//                    subtasks.setHidden(hideSubtasksNow);
                    if (hideSubtasksNow) {
                        subtaskContainer.removeComponent(subtasks);
                    } else {
                        subtaskContainer.add(BorderLayout.EAST, subtasks);
                    }
//                showSubtasks.setIcon(hideSubtasks ? Icons.iconShowMoreLabelStyle : Icons.iconShowLessLabelStyle);
//                    this.animateLayout(300);
                    if (false) {
                        subtasks.animateLayout(300);
                        subtaskContainer.animateLayout(300);
                    }
//                    getComponentForm().getContentPane().animateLayout(300);
//                    getComponentForm().getContentPane().animateHierarchy(300);
//                    getComponentForm().animateHierarchy(300); //TODO!!! animation to slide subtask list up/down
                    ((MyForm) getComponentForm()).animateMyForm(); //TODO!!! animation to slide subtask list up/down
//                    subtasks.animateHierarchy(300);
//                    getComponentForm().getContentPane().revalidate();
                });// {
                showSubtasks.setCommand(expandSubTasks);
//                    swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
                subtaskContainer.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, showSubtasks);
//                east.addComponent(subTasksButton);

                //HEADER - EDIT LIST IN FULL SCREEN MODE
//                    Button editSubtasksFullScreen = ScreenListOfItems.makeSubtaskButton(item, null);
                Button editSubtasksFullScreen = new Button();
                editSubtasksFullScreen.setCommand(new MyReplayCommand("EditSubtasks", "", Icons.iconEditPropertiesToolbarStyle) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
//                    Button editSubtasksFullScreenOLD = new Button();
//                    editSubtasksFullScreenOLD.setCommand(Command.create(null, Icons.iconEditPropertiesLabelStyle, (e) -> {
                        ItemList subtaskList = item.getItemList();
                        new ScreenListOfItems("Subtasks of " + item.getText(), subtaskList, myForm, (iList) -> {
                            item.setItemList(subtaskList);
                            DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                            myForm.refreshAfterEdit(); //necessary to update sum of subtask effort
                        }, ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER
                        ).show();
                    }
                });

                //HEADER - count + expand button
                subtaskHeader.add(BorderLayout.EAST, BoxLayout.encloseXNoGrow(
                        //                    new Label(subtasksItemList.size() + ""),
                        new Label(MyDate.formatTimeDuration(subtasksItemList.getRemainingEffort())),
                        showSubtasks, editSubtasksFullScreen));

                subtaskContainer.add(BorderLayout.NORTH, subtaskHeader);

            }
//        return subtaskContainer;
        }
    }

}
