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

/**
 *
 * @author Thomas
 */
public class InlineInsertNewWorkSlotContainer extends Container {

//    private Container oldNewTaskCont=null;
    private MyTextField2 textEntryField;
    private ItemList itemList;
    private MyForm myForm;
    private ItemAndListCommonInterface itemOrItemListForNewTasks;
    private ItemList newItemList;
    private boolean insertBeforeRefElement;
//    private Container cont=new Container(new BorderLayout());

    private final static String ENTER_CATEGORY = "New category"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"

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
    public InlineInsertNewWorkSlotContainer(MyForm myForm, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeRefElement) {
        this(myForm, new ItemList(), itemOrItemListForNewTasks2, insertBeforeRefElement);
    }

    public InlineInsertNewWorkSlotContainer(MyForm myForm, ItemList itemList2, ItemAndListCommonInterface itemOrItemListForNewTasks2, boolean insertBeforeRefElement) {
        this.myForm = myForm;
        this.itemList = itemList2;
        ASSERT.that(itemOrItemListForNewTasks2 != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewTasks = itemOrItemListForNewTasks2;
        this.insertBeforeRefElement = insertBeforeRefElement;

//        if (this.myForm.lastInsertNewTaskContainer != null) {
//            newCategory = this.myForm.lastInsertNewTaskContainer.insertNewTask(); //if text field is non-empty, create a task before closing it
////            lastInsertNewTaskContainer.closeInsertNewTaskContainer(true, false, false);
//            this.myForm.lastInsertNewTaskContainer.closeInsertNewTaskContainer();
//            this.myForm.lastInsertNewTaskContainer = null; //NOT necessary
//        }
////        ((ScreenListOfItems) myForm).putClientProperty(LAST_INSERTED_NEW_TASK_CONTAINER, this);
//        this.myForm.lastInsertNewTaskContainer = this;
//        this.myForm = myForm2;
        Container contForTextEntry = new Container(new BorderLayout());

        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        add(swipC);

//<editor-fold defaultstate="collapsed" desc="comment">
//        Object oldNewTaskCont = swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER);
//        if (oldNewTaskCont != null && oldNewTaskCont instanceof Container) { //if there is a previous newTask container remove it
//        if (oldNewTaskCont != null) { //if there is a previous newTask container remove it
////            Container oldNewTaskContC = (Container) oldNewTaskCont;
//            if (oldNewTaskCont.getParent() != null) {
//                oldNewTaskCont.getParent().removeComponent(oldNewTaskCont); //should be animated away at same time as adding new one below
//            }
////            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null); //delete when closing the insertTask container
////            swipCont.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //delete when closing the insertTask container
//            oldNewTaskCont = null;
//            insertAsSubtask = false;
//        }
//        Container cont = new Container(new BorderLayout());
//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Super task"), cont);
//        SwipeableContainer swipC = this;
//        newTaskContainer = swipC;
//        swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, swipC);
//        oldNewTaskCont = swipC;
//        swipC.addSwipeOpenListener((ev) -> {
//            if (false) {
//                if (swipC.isOpenedToLeft()) {
////                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null);
////                oldNewTaskCont = null;
//                    insertAsSubtask = false;
//                    //TOD: indent or show visually that it is subtask
//                } else if (swipC.isOpenedToRight()) {
////                            swipC.putClientProperty(SUBTASK_LEVEL_KEY, false);
////                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, true);
//                    insertAsSubtask = true;
//                    //TOD!!!! update formatting of swipC to show it's now inserting a subtask (eg indent it)
//                    //reuse same margin indent (or simply same style?) as item container in swipCont?!
//                }
//            }
//            if (false) {
//                insertAsSubtask = swipC.isOpenedToRight();
//                swipC.close();
//            }
//            insertAsSubtask = swipC.isOpenedToRight();
//            taskTextEntryField2.setHint(insertAsSubtask ? ENTER_SUBTASK : ENTER_TASK);
////            if (swipC.isOpenedToLeft()) {
////                insertAsSubtask = true;
////                taskTextEntryField2.setHint(ENTER_SUBTASK);
////            } else { //isOpenedToLeft()
////                insertAsSubtask = false;
////                taskTextEntryField2.setHint(ENTER_TASK);
////            }
//            swipC.close();
////                taskTextEntryField2.repaint(); //TOD: enough to update the field?
////            swipC.animateHierarchy(300);//TOD: enough to update the field?
//            myForm.animateHierarchy(300);//TOD: enough to update the field?
//        });
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        final Command closeInsertNewTask = Command.create(null, Icons.iconCloseCircle, (ev) -> {
//            //close the container
////            Form parentForm = swipC.getComponentForm();
////            swipC.getParent().removeComponent(swipC);
//////            newTaskContainer = null;
//////            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//////            insertAsSubtask = false;
////            parentForm.animateHierarchy(300);
////            Form parentForm = swipC.getComponentForm();
//            //UI: if no text entered, close the text field
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (swipC.getParent() != null) {
////                swipC.getParent().removeComponent(swipC);
////            }
//////            newTaskContainer = null;
//////                    swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//////                    swipC.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//////            if (parentForm != null) {
//////                parentForm.animateHierarchy(300);
//////            }
////            ((ScreenListOfItems) myForm).lastInsertNewTaskContainer = null;
////            if (myForm != null) {
////                myForm.animateHierarchy(300);
////            }
////</editor-fold>
//            closeInsertNewTaskContainer(contForTextEntry, (ScreenListOfItems) myForm);
////                    oldNewTaskCont = null;
//        });
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//Text entry field
//        MyTextField2 taskTextEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
//        taskTextEntryField2.setHint("Enter task (swipe right: subtask");
//</editor-fold>
        textEntryField = new MyTextField2(); //TODO!!!! need field to enter edit mode
        textEntryField.setHint(ENTER_CATEGORY);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            contForTextEntry.getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
//        }
//        taskTextEntryField2.requestFocus(); //enter edit mode??
//Label swipeIconLabel =new Label(Icons.iconIndentExdendInsertNewTask);
//</editor-fold>
        Container westCont = new Container(BoxLayout.x());

        //DONE listener - create and insert new Category
//        taskTextEntryField2.addActionListener((ev) -> {
        textEntryField.setDoneListener((ev) -> { //When pressing ENTER, insert new task
            if (!ev.isConsumed()) {
                newItemList = createNewItemList(); //store new category for use when recreating next insert container
                if (newItemList != null) {
                    myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
                }
                closeInsertNewCategoryContainer();

                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
        });

        contForTextEntry.add(BorderLayout.CENTER, textEntryField);

        //close insert container
        contForTextEntry.add(BorderLayout.WEST, westCont);
        if (itemOrItemListForNewTasks != null && itemOrItemListForNewTasks.size() > 0) { //only add close button if in a non-empty list
            westCont.add(new Button(CommandTracked.create(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
                myForm.lastInsertNewElementContainer = null;
//                closeInsertNewCategoryContainer(myForm); //close without inserting new task
                getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
                myForm.animateLayout(300);
            },"EditItemFromInsertNewContainer")));
        }

        //Enter full screen edit of the new Category:
        contForTextEntry.add(BorderLayout.EAST,
                new Button(CommandTracked.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
                    if ((newItemList = createNewItemList()) != null) { //if new task successfully inserted... //TODO!!!! create even if no text was entered into field
                        myForm.setKeepPos(new KeepInSameScreenPosition(newItemList, this, -1)); //if editing the new task in separate screen, 
                        new ScreenItemListProperties(newItemList, (MyForm) getComponentForm(), () -> {
                            insertNewItemListAndSaveChanges(newItemList);
                            myForm.refreshAfterEdit();
                        }).show();
                    } else {
                        ASSERT.that(false, "Something went wrong here, what to do? ...");
                    }
                },"EditItemListProperties")));
    }

    public MyTextField2 getTextField() {
//        getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
        return textEntryField; //UI: start editing this field, only if empty (to avoid keyboard popping up)
//        taskTextEntryField2.requestFocus(); //enter edit mode??
    }

//    public void insertNewTask(ItemAndListCommonInterface itemOrItemList, ScreenListOfItems myForm) {
    /**
     *
     * @return true if a task was created
     */
    private ItemList createNewItemList() {
        return createNewItemList(false);
    }

    private ItemList createNewItemList(boolean createEvenIfNoTextInField) {
        String text = textEntryField.getText();
        ItemList newItem;
        if (createEvenIfNoTextInField || (text != null && text.length() > 0)) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newItem = new Category(text); //true: interpret textual values
            return newItem;
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
        int index = itemOrItemListForNewTasks.getItemIndex(itemList);
        if (index > -1) {
            itemOrItemListForNewTasks.addToList(index + (insertBeforeRefElement ? 0 : 1), newItemList); //add after item
        } else {
            itemOrItemListForNewTasks.addToList(newItemList); //if item is null or not in orgList, insert at beginning of (potentially empty) list
        }
        DAO.getInstance().saveInBackground(newItemList, (ParseObject) itemOrItemListForNewTasks);
    }

    private void closeInsertNewCategoryContainer() {
        closeInsertNewCategoryContainer(null);
    }

    private void closeInsertNewCategoryContainer(MyForm f) {
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
//    public InlineInsertNewCategoryContainer getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (lastInsertNewTaskContainer == null) { //TODO Optimization: called for every container, replace by local variable?
////            return null;
////        } else {
////            if (item == lastInsertNewTaskContainer.newItem) {
////                return new InlineInsertNewTaskContainer(lastInsertNewTaskContainer.newItem, itemOrItemList);
////            } else {
////                return null;
////            }
////        }
////</editor-fold>
//        if (myForm.lastInsertNewElementContainer == null) { //TODO Optimization: called for every container, replace by local variable?
//            return null;
//        } else {
//            if (item == myForm.lastInsertNewElementContainer.newItem) {
//                return new InlineInsertNewCategoryContainer(myForm, myForm.lastInsertNewElementContainer.newItem, itemOrItemList);
//            } else {
//                return null;
//            }
//        }
//    }
    /**
     * if the textEntry field is in Form f, then it is set to editOnShow
     *
     * @param f
     */
    public void setTextFieldEditableOnShow(Form f) {
//        if (lastInsertNewTaskContainer != null && f.equals(lastInsertNewTaskContainer.getComponentForm())
        if (textEntryField != null) {
//            f.setEditOnShow(lastInsertNewTaskContainer.taskTextEntryField2);
            if (false) {
                textEntryField.requestFocus();
            } else {
                textEntryField.startEditingAsync();
            }
        }
    }

//    public static void setTextFieldEditableOnShowStatic(MyForm f) {
////        if (lastInsertNewTaskContainer != null && f.equals(lastInsertNewTaskContainer.getComponentForm())
////        if (lastInsertNewTaskContainer != null && lastInsertNewTaskContainer.taskTextEntryField2 != null) {
//        if (f.lastInsertNewElementContainer != null) {
////            f.setEditOnShow(lastInsertNewTaskContainer.taskTextEntryField2);
//            f.lastInsertNewElementContainer.setTextFieldEditableOnShow(f);
//        }
//    }
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
