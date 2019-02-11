/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.parse4cn1.ParseObject;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewElementContainer extends Container {

//    private Container oldNewTaskCont=null;
    private boolean insertAsSubtask = false; //true if the user has selected to insert new task as a subtask of the preceding task
    private MyTextField2 taskTextEntryField2;
    private Item item;
    private MyForm myForm;
    private ItemAndListCommonInterface itemOrItemListForNewTasks;
//    static InlineInsertNewTaskContainer lastInsertNewTaskContainer = null; //global static variable to store the last container to be able to remove if a new is created //CANNOT be globally static since a subscreen may also be using such field (eg ScreenItem called from ScreenListOfItems), so moved to variable in screen
//    MyForm myForm;
    private Item newItem;
//    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_SUBTASK = "New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK = "New task, ->for subtask)"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_TASK_NO_SWIPE_RIGHT = "New task"; //"Task (swipe right: subtask)"

    /**
     * will create a new element to insert (Item, ItemList, Category, ...)
     */
//    interface InsertNewElementFunc {
//
////        Component make(Item item);
//        /**
//         * checks on element and if it corresponds to the previous element created by the Insert function, returns a new container to enter another element inline
//         * @param element the element which determines if a new insertContainer should be inserted *after* the container for element
//         * @param targetList list into which a new element will be inserted
//         * @return 
//         */
//        Component make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList);
//    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    final static String LAST_INSERTED_NEW_TASK_CONTAINER = "lastInsertTask"; //"Task (swipe right: subtask)"
//     buildAddNewTaskContainer(Item item, ItemList orgList, SwipeableContainer swipCont, MyForm.Action refreshOnItemEdits) {
//    public InsertNewTaskContainer(Item item, ItemList orgList, SwipeableContainer swipCont, MyForm.Action refreshOnItemEdits) {
//        cont=new Container(new BorderLayout());
//        this(item, orgList, swipCont, refreshOnItemEdits, cont);
//    }
//    public InsertNewTaskContainer(Item item, ItemList orgList, SwipeableContainer swipCont, MyForm.Action refreshOnItemEdits, Container cont) {
//    public InsertNewTaskContainer(Item item, ItemList orgList, MyForm myForm, Item newItem) {
//    public InsertNewTaskContainer(Item item, ItemList orgList, MyForm myForm) {
//    public InsertNewTaskContainer(Item item2, ItemAndListCommonInterface itemOrItemList2, MyForm myForm2) {
//</editor-fold>
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
//    public InsertNewTaskContainer(Item item2, ItemAndListCommonInterface itemOrItemList2, MyForm myForm) {
//    public InlineInsertNewTaskContainer(Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
//        
//    }
    /**
     * create a new Container and a new Item 
     * @param myForm
     * @param itemOrItemListForNewTasks2 
     */
    public InlineInsertNewElementContainer(MyForm myForm, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
        this(myForm, new Item(), itemOrItemListForNewTasks2);
    }
    
    public InlineInsertNewElementContainer(MyForm myForm, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
//        this.myForm = myForm;
////        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
//        this.item = item2;
//        ASSERT.that(itemOrItemListForNewTasks2 != null, "why itemOrItemListForNewTasks2==null here?");
//        this.itemOrItemListForNewTasks = itemOrItemListForNewTasks2;
//
////        InsertNewTaskContainer lastInsertNewTaskContainer = (InsertNewTaskContainer) ((ScreenListOfItems) myForm).getClientProperty(LAST_INSERTED_NEW_TASK_CONTAINER);
//        if (this.myForm.lastInsertNewElementContainer != null) {
//            newItem = this.myForm.lastInsertNewElementContainer.insertNewTask(); //if text field is non-empty, create a task before closing it
////            lastInsertNewTaskContainer.closeInsertNewTaskContainer(true, false, false);
//            this.myForm.lastInsertNewElementContainer.closeInsertNewTaskContainer();
//            this.myForm.lastInsertNewElementContainer = null; //NOT necessary
//        }
////        ((ScreenListOfItems) myForm).putClientProperty(LAST_INSERTED_NEW_TASK_CONTAINER, this);
//        this.myForm.lastInsertNewElementContainer = this;
////        this.myForm = myForm2;
//        Container contForTextEntry = new Container(new BorderLayout());
//
//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
//        add(swipC);
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        Object oldNewTaskCont = swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER);
////        if (oldNewTaskCont != null && oldNewTaskCont instanceof Container) { //if there is a previous newTask container remove it
////        if (oldNewTaskCont != null) { //if there is a previous newTask container remove it
//////            Container oldNewTaskContC = (Container) oldNewTaskCont;
////            if (oldNewTaskCont.getParent() != null) {
////                oldNewTaskCont.getParent().removeComponent(oldNewTaskCont); //should be animated away at same time as adding new one below
////            }
//////            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null); //delete when closing the insertTask container
//////            swipCont.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //delete when closing the insertTask container
////            oldNewTaskCont = null;
////            insertAsSubtask = false;
////        }
////        Container cont = new Container(new BorderLayout());
////        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Super task"), cont);
////        SwipeableContainer swipC = this;
////        newTaskContainer = swipC;
////        swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, swipC);
////        oldNewTaskCont = swipC;
////        swipC.addSwipeOpenListener((ev) -> {
////            if (false) {
////                if (swipC.isOpenedToLeft()) {
//////                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null);
//////                oldNewTaskCont = null;
////                    insertAsSubtask = false;
////                    //TOD: indent or show visually that it is subtask
////                } else if (swipC.isOpenedToRight()) {
//////                            swipC.putClientProperty(SUBTASK_LEVEL_KEY, false);
//////                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, true);
////                    insertAsSubtask = true;
////                    //TOD!!!! update formatting of swipC to show it's now inserting a subtask (eg indent it)
////                    //reuse same margin indent (or simply same style?) as item container in swipCont?!
////                }
////            }
////            if (false) {
////                insertAsSubtask = swipC.isOpenedToRight();
////                swipC.close();
////            }
////            insertAsSubtask = swipC.isOpenedToRight();
////            taskTextEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK);
//////            if (swipC.isOpenedToLeft()) {
//////                insertAsSubtask = true;
//////                taskTextEntryField2.setHint(ENTER_SUBTASK);
//////            } else { //isOpenedToLeft()
//////                insertAsSubtask = false;
//////                taskTextEntryField2.setHint(ENTER_TASK);
//////            }
////            swipC.close();
//////                taskTextEntryField2.repaint(); //TOD: enough to update the field?
//////            swipC.animateHierarchy(300);//TOD: enough to update the field?
////            myForm.animateHierarchy(300);//TOD: enough to update the field?
////        });
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        final Command closeInsertNewTask = Command.create(null, Icons.iconCloseCircle, (ev) -> {
////            //close the container
//////            Form parentForm = swipC.getComponentForm();
//////            swipC.getParent().removeComponent(swipC);
////////            newTaskContainer = null;
////////            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
////////            insertAsSubtask = false;
//////            parentForm.animateHierarchy(300);
//////            Form parentForm = swipC.getComponentForm();
////            //UI: if no text entered, close the text field
//////<editor-fold defaultstate="collapsed" desc="comment">
//////            if (swipC.getParent() != null) {
//////                swipC.getParent().removeComponent(swipC);
//////            }
////////            newTaskContainer = null;
////////                    swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
////////                    swipC.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
////////            if (parentForm != null) {
////////                parentForm.animateHierarchy(300);
////////            }
//////            ((ScreenListOfItems) myForm).lastInsertNewTaskContainer = null;
//////            if (myForm != null) {
//////                myForm.animateHierarchy(300);
//////            }
//////</editor-fold>
////            closeInsertNewTaskContainer(contForTextEntry, (ScreenListOfItems) myForm);
//////                    oldNewTaskCont = null;
////        });
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////Text entry field
////        MyTextField2 taskTextEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
////        taskTextEntryField2.setHint("Enter task (swipe right: subtask");
////</editor-fold>
//        taskTextEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
//        taskTextEntryField2.setHint(item != null ? ENTER_TASK : ENTER_TASK_NO_SWIPE_RIGHT);
//        taskTextEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            contForTextEntry.getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
////        }
////        taskTextEntryField2.requestFocus(); //enter edit mode??
////Label swipeIconLabel =new Label(Icons.iconIndentExdendInsertNewTask);
////</editor-fold>
//        Container westCont = new Container(BoxLayout.x());
//
//        if (item != null) { //only add RIGHT swipe to create a subtask if the insertNew is below an Item (eg not if only element on the screen)
////            if (false) {Label swipeIconLabel = new Label(Icons.iconInsertNewTaskIndent);}
//            swipC.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
//                if (item != null) {
////                    insertAsSubtask = swipC.isOpenedToRight(); //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
////                    taskTextEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK); //item!=null to avoid 
////                    swipeIconLabel.setIcon(insertAsSubtask ? Icons.iconInsertNewTaskExdend : Icons.iconInsertNewTaskIndent); //change arrow 
//                    if (swipC.isOpenedToRight()) { //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
//                        if (insertAsSubtask) {
//                            //if already a subtask, do nothing
//                        } else {
//                            insertAsSubtask = false; //make 
//                        }
//                    } else { //SWIPE LEFT
//                        if (insertAsSubtask) {
//                            insertAsSubtask = false; //cancel subtask
//                        } else {
//                            //was do nothing
//                        }
//                    }
//                    taskTextEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK); //item!=null to avoid 
////                    if (false) swipeIconLabel.setIcon(insertAsSubtask ? Icons.iconInsertNewTaskExdend : Icons.iconInsertNewTaskIndent); //change arrow 
////                this.repaint();  //refresh InsertNewTaskContainer after changing hint and icon
//                }
//                ev.consume();
//                swipC.close();
//                if (false) {
//                    this.repaint(); //refresh with new hint (works??)
//                }//<editor-fold defaultstate="collapsed" desc="comment">
////                taskTextEntryField2.repaint(); //TODO: enough to update the field?
////            swipC.animateHierarchy(300);//TODO: enough to update the field?
////            myForm.animateHierarchy(300);//TODO: enough to update the field?
////            InsertNewTaskContainer.this.getComponentForm().animateHierarchy(300);//TODO: enough to update the field?
////            getComponentForm().animateHierarchy(300);//TODO: enough to update the field?
////</editor-fold>
////            getComponentForm().animateLayout(300);//TODO: enough to update the field?
//                if (false) {
//                    getComponentForm().animateLayout(300);//TODO: enough to update the field?
//                }
//                revalidate(); //ensure removeFromCache?!
//            });
////            westCont.add(swipeIconLabel);
//        }
//
//        //DONE listener - create and insert new task
////        taskTextEntryField2.addActionListener((ev) -> {
//        taskTextEntryField2.setDoneListener((ev) -> { //When pressing ENTER, insert new task
//            if (!ev.isConsumed() && !swipC.isOpen()) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                String taskText = taskTextEntryField2.getText();
////                taskTextEntryField2.setText(""); //clear text
////                if (taskText != null && taskText.length() > 0) {
//////                    Item newItem = new Item(taskText);
////                    newItem = new Item(taskText);
//////                    newItem.setText(taskText);
//////                refreshOnItemEdits.launchAction();
////                    DAO.getInstance().save(newItem);
//////                    if (swipC.getComponentForm().getClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY) == null) { //|| ((Boolean) swipC.getClientProperty(SUBTASK_LEVEL_KEY)) == false) { //add task after previous
////                    if (!insertAsSubtask) { //|| ((Boolean) swipC.getClientProperty(SUBTASK_LEVEL_KEY)) == false) { //add task after previous
////                        //make a sistertask (insert in same list as item, after item)
////                        //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
////                        if (itemOrItemList != null) {
//////                            if (item != null && itemOrItemList.indexOf(item) != -1) {
////                            if (item != null && itemOrItemList.getItemIndex(item) != -1) {
//////                                int index = itemOrItemList.indexOf(item);
////                                int index = itemOrItemList.getItemIndex(item);
//////                                itemOrItemList.addItemAtIndex(newItem, index + 1);
////                                itemOrItemList.addToList(index + 1, newItem);
////                            } else {
//////                                itemOrItemList.addItemAtIndex(newItem, 0); //if item is null or not in orgList, insert at beginning of (potentially empty) list
////                                itemOrItemList.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
////                            }
////                            DAO.getInstance().save((ParseObject) itemOrItemList);
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                            swipCont.close(); //close swipe container after swipe action
//////                            swipC.close(); //close swipe container after swipe action
//////                            refreshOnItemEdits.launchAction();
//////</editor-fold>
////                        } //else: task only inserted into inbox
////                    } else { //add as subtask to previous task, and keep the subtask level
////                        //make a subtask
////                        if (item != null) {
//////                            item.addToList(0, newItem);
////                            item.addToList(newItem); //add to end of subtask list (depending on setting for add to beginning/end of lists)
////                            DAO.getInstance().save(item);
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                        swipCont.close(); //close swipe container after swipe action
//////                        swipC.close(); //close swipe container after swipe action
//////                            refreshOnItemEdits.launchAction();
//////                        swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //remove the subtask property so next task does not become a subtask to the subtask
//////</editor-fold>
////                            insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
////                            myForm.expandedObjects.add(item); //UI: expand the item to show newly added subtask
////                        }
////                    }
////</editor-fold>
////                MyForm form = ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm()); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////                if (insertNewTask()) {
//                newItem = insertNewTask(); //store new task for use when recreating next insert container
////                closeInsertNewTaskContainer(false, true, false);
////                MyForm myForm = ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm()); //MUST do before closeInsert to keep attachment to MyFOrm
//                if (newItem != null) {
//                    myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                }
//                closeInsertNewTaskContainer();
//
////<editor-fold defaultstate="collapsed" desc="comment">
////                    myForm.refreshAfterEdit();
////                } else {
////                    closeInsertNewTaskContainer(false, true);
////                }
////                ((MyForm) InsertNewTaskContainer.this.getComponentForm()).refreshAfterEdit();
////</editor-fold>
//                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////<editor-fold defaultstate="collapsed" desc="comment">
////                } else { //empty text box, close
////<editor-fold defaultstate="collapsed" desc="comment">
////                    Form parentForm = swipC.getComponentForm();
////                    //UI: if no text entered, close the text field
////                    if (swipC.getParent() != null) {
////                        swipC.getParent().removeComponent(swipC);
////                    }
//////            newTaskContainer = null;
//////                    swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//////                    swipC.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
////                    if (parentForm != null) {
////                        parentForm.animateHierarchy(300);
////                    }
//////                    oldNewTaskCont = null;
////</editor-fold>
////                closeInsertNewTask.actionPerformed(null);
////                closeInsertNewTaskContainer(contForTextEntry, (ScreenListOfItems)myForm);
////                }
////</editor-fold>
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            } else { //swiped
////                if (false) {
////                    if (swipC.isOpenedToLeft()) {
////                        insertAsSubtask = true;
////                        taskTextEntryField2.setHint(ENTER_SUBTASK);
////                    } else { //isOpenedToLeft()
////                        insertAsSubtask = false;
////                        taskTextEntryField2.setHint(ENTER_TASK);
////                    }
////                    swipC.close();
//////                taskTextEntryField2.repaint(); //TODO: enough to update the field?
////                    swipC.animateHierarchy(300);//TODO: enough to update the field?
////                }
////</editor-fold>
//        });
//
////        cont.add(BorderLayout.WEST, new Label(Icons.iconCheckboxCreated));
//        contForTextEntry.add(BorderLayout.CENTER, taskTextEntryField2);
////<editor-fold defaultstate="collapsed" desc="comment">
////        Container westCont = BoxLayout.encloseX(
////                new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
//////                    closeInsertNewTaskContainer(contForTextEntry, (ScreenListOfItems) myForm);
////                    closeInsertNewTaskContainer();
////                })),
////                new Label(Icons.iconIndentExdendInsertNewTask));
////</editor-fold>
//        //close insert container
//        contForTextEntry.add(BorderLayout.WEST, westCont);
//        if (itemOrItemListForNewTasks != null && itemOrItemListForNewTasks.size() > 0) { //only add close button if in a non-emptp list
//            westCont.add(new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
//                //TODO!!! Replay: store the state/position of insertContainer 
////                    closeInsertNewTaskContainer(contForTextEntry, (ScreenListOfItems) myForm);
////                closeInsertNewTaskContainer(false, false, true);
//                myForm.lastInsertNewElementContainer = null;
////                MyForm myForm = ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm());
//                closeInsertNewTaskContainer(myForm); //close without inserting new task
//                if (false) { //not needed when just closing container with changing the list
//                    myForm.refreshAfterEdit();
//                }
//            })));
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (item != null) { //only add indent button if below an item
////            westCont.add(swipeIconLabel);
////        }
////</editor-fold>
//
//        //Full screen edit of the new task:
//        contForTextEntry.add(BorderLayout.EAST, 
//                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
////lable just serves as handle to swipe container left/right
////            if (taskTextEntryField2.getText().length() > 0) {
////            Item newItem;
//            if ((newItem = insertNewTask()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
////                MyForm myForm = ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm());
//                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen, 
//                new ScreenItem(newItem, (MyForm) getComponentForm(), () -> {
//                    //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
//                    DAO.getInstance().save(newItem);
////                        ((MyForm) getComponentForm()).refreshAfterEdit();
//                    myForm.refreshAfterEdit();
//                }).show();
//            } else {
//                ASSERT.that(false, "Something went wrong here, what to do? ...");
//            }
////            }
//        })));
    }

    public MyTextField2 getTextFieldXXX() {
//        getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        return taskTextEntryField2; //UI: start editing this field, only if empty (to avoid keyboard popping up)
//        taskTextEntryField2.requestFocus(); //enter edit mode??
    }

//    public void insertNewTask(ItemAndListCommonInterface itemOrItemList, ScreenListOfItems myForm) {
    /**
     *
     * @return true if a task was created
     */
    private Item insertNewTask() {
        String taskText = taskTextEntryField2.getText();
        Item newItem;
        if (taskText != null && taskText.length() > 0) {
            taskTextEntryField2.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newItem = new Item(taskText, true); //true: interpret textual values
            if (insertAsSubtask) { //add as subtask to previous task, and keep the subtask level
                if (item != null) {
                    item.addToList(newItem); //add to end of subtask list (depending on setting for add to beginning/end of lists)
                    DAO.getInstance().save(newItem);
                    DAO.getInstance().save(item);
                    insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
//                    myForm.expandedObjects.add(item); //UI: expand the item to show its newly added subtask
//                    ((MyForm) getComponentForm()).expandedObjects.add(item); //UI: expand the item to show newly added subtask
                    myForm.expandedObjects.add(item); //UI: expand the item to show newly added subtask
                }
            } else if (itemOrItemListForNewTasks != null && !itemOrItemListForNewTasks.isNoSave()) {
                //make a sistertask (insert in same list as item, after item)
                //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
                int index = itemOrItemListForNewTasks.getItemIndex(item);
                if (item != null && index != -1) {
//                        int index = itemOrItemList.getItemIndex(item);
                    itemOrItemListForNewTasks.addToList(index + 1, newItem); //add after item
                } else {
                    itemOrItemListForNewTasks.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                }
                DAO.getInstance().save(newItem); //need to save again since itemOrItemList is added as owner
                DAO.getInstance().saveInBackground((ParseObject) itemOrItemListForNewTasks);
//                } //else: task only inserted into inbox
            } else {
                DAO.getInstance().save(newItem);
            }
            return newItem;
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void closeInsertNewTaskContainer(Container swipC, ScreenListOfItems myForm) {
//    public void closeInsertNewTaskContainer() {
//        closeInsertNewTaskContainer(false);
//    }
//
//    public void closeInsertNewTaskContainer(boolean dontCreateNewInsertContainer) {
//        closeInsertNewTaskContainer(true, dontCreateNewInsertContainer);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void closeInsertNewTaskContainer(boolean saveAnyUnsavedItem, boolean createNewInsertContainer) {
//        closeInsertNewTaskContainer(saveAnyUnsavedItem, createNewInsertContainer, false);
//    }
//    private void closeInsertNewTaskContainer(boolean saveAnyUnsavedItem, boolean createNewInsertContainer, boolean refreshForm) {
//    private void closeInsertNewTaskContainer(boolean refreshForm) {
//</editor-fold>
    private void closeInsertNewTaskContainer() {
        closeInsertNewTaskContainer(null);
    }

    private void closeInsertNewTaskContainer(MyForm f) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (saveAnyUnsavedItem) {
//            insertNewTask(); //if text field is non-empty, create a task before closing it
//        }
//        if (!createNewInsertContainer) {
////            ((ScreenListOfItems) getComponentForm()).lastInsertNewTaskContainer = null; //set to null so no new is created in MyTree2
////            ((ScreenListOfItems) getComponentForm()).putClientProperty(LAST_INSERTED_NEW_TASK_CONTAINER, null);
//            lastInsertNewTaskContainer = null;
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container oldInsertCont = ((ScreenListOfItems) myForm).lastInsertNewTaskContainer;
//        if (oldInsertCont != null && oldInsertCont.getParent() != null) {
//            oldInsertCont.getParent().removeComponent(oldInsertCont); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//        }
//</editor-fold>
        //UI: close the text field
        Container parent = getParent();
        if (parent != null) {
//            MyForm f = (MyForm) InlineInsertNewTaskContainer.this.getComponentForm(); //get Form before removing this from parent/form
            parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//            parent.animateHierarchy(300);
//            ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm()).animateHierarchy(300);
//            ((MyForm) InlineInsertNewTaskContainer.this.getComponentForm()).animateMyForm();
            if (f != null) {
                f.animateMyForm();
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        ((MyForm)getComponentForm()).refreshAfterEdit();
//        if (refreshForm && parent != null) {
////MyForm f = ((MyForm) InsertNewTaskContainer.this.getComponentForm());
//            MyForm f = ((MyForm) parent.getComponentForm());
//            if (f != null) { //form my be null when called from constructor to close a previous container
//                f.refreshAfterEdit(); //this is
//            }
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">

//UI: if no text entered, close the text field
//        if (swipC.getParent() != null) {
//            swipC.getParent().removeComponent(swipC);
//        }
//            newTaskContainer = null;
//                    swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//                    swipC.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//            if (parentForm != null) {
//                parentForm.animateHierarchy(300);
//            }
//        ((ScreenListOfItems) myForm).lastInsertNewTaskContainer = null;
//        if (myForm != null) {
//            myForm.animateHierarchy(300);
//        }
//</editor-fold>
    }
//<editor-fold defaultstate="collapsed" desc="comment">
    //    public InsertNewTaskContainer make(Item item, ItemAndListCommonInterface itemOrItemList, MyForm myForm) {

//    private InlineInsertNewTaskContainer make(Item item, ItemAndListCommonInterface itemOrItemList) {
//        if (item == newItem) {
////            return new InsertNewTaskContainer(newItem, itemOrItemList, myForm);
//            return new InlineInsertNewTaskContainer(newItem, itemOrItemList);
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//    public static InsertNewTaskContainer getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList, MyForm myForm) {
    /**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
    public InlineInsertNewElementContainer getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList) {
//        if (lastInsertNewTaskContainer == null) { //TODO Optimization: called for every container, replace by local variable?
//            return null;
//        } else {
//            if (item == lastInsertNewTaskContainer.newItem) {
//                return new InlineInsertNewTaskContainer(lastInsertNewTaskContainer.newItem, itemOrItemList);
//            } else {
//                return null;
//            }
//        }
//        if (myForm.lastInsertNewElementContainer == null) { //TODO Optimization: called for every container, replace by local variable?
//            return null;
//        } else {
//            if (item == myForm.lastInsertNewElementContainer.newItem) {
//                return new InlineInsertNewElementContainer(myForm, myForm.lastInsertNewElementContainer.newItem, itemOrItemList);
//            } else {
//                return null;
//            }
//        }
                return null;
    }

    /**
     * if the textEntry field is in Form f, then it is set to editOnShow
     *
     * @param f
     */
    public void setTextFieldEditableOnShow(Form f) {
//        if (lastInsertNewTaskContainer != null && f.equals(lastInsertNewTaskContainer.getComponentForm())
        if (taskTextEntryField2 != null) {
//            f.setEditOnShow(lastInsertNewTaskContainer.taskTextEntryField2);
            if (false)taskTextEntryField2.requestFocus();
            else taskTextEntryField2.startEditingAsync();
        }
    }

    public static void setTextFieldEditableOnShowStatic(MyForm f) {
//        f.addShowListener((e)->taskTextEntryField2.startEditingAsync());
//        if (lastInsertNewTaskContainer != null && f.equals(lastInsertNewTaskContainer.getComponentForm())
//        if (lastInsertNewTaskContainer != null && lastInsertNewTaskContainer.taskTextEntryField2 != null) {
//        if (f.lastInsertNewElementContainer != null) {
////            f.setEditOnShow(lastInsertNewTaskContainer.taskTextEntryField2);
//            f.lastInsertNewElementContainer.setTextFieldEditableOnShow(f);
//        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public void paint(Graphics g) {
//        Form f = getComponentForm();
//        if (f != null) {
//            f.setEditOnShow(taskTextEntryField2);
//        }
//        super.paint(g);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Container recreateNextInsertNewContainer(Item current, ItemAndListCommonInterface parent) {
//                    //check if a new insertNewTask container should be created for current and if so insert it:
//                     InsertNewTaskContainer insertNewTask=(InsertNewTaskContainer)getComponentForm().getClientProperty(LAST_INSERTED_NEW_TASK_CONTAINER);
////            if (insertNewTask != null && current instanceof Item && model instanceof ItemAndListCommonInterface) {
//            if (insertNewTask != null && current instanceof Item && parent instanceof ItemAndListCommonInterface) {
////                Component insertNewTsk = insertNewTask.make((Item) current, (ItemAndListCommonInterface) model);
////                InsertNewTaskContainer insertNewTsk = insertNewTask.make((Item) current, (ItemAndListCommonInterface) parent);
//                InsertNewTaskContainer insertNewTsk = insertNewTask.make( current, (ItemAndListCommonInterface) parent);
//                if (insertNewTsk != null) {
//                    destination.add(insertNewTsk);
//                    destination.getComponentForm().setEditOnShow(insertNewTsk.getTextField()); //UI: set for edit
//                }
//            }
//    }
//</editor-fold>
}
