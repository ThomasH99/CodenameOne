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
import com.codename1.ui.SwipeableContainer;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.animations.MorphTransition;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import com.todocatalyst.todocatalyst.MyForm.Action;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author Thomas
 */
public class InlineInsertNewItemContainer2 extends InlineInsertNewContainer implements InsertNewElementFunc {

    private final static String ENTER_SUBTASK = "Swipe left for normal task"; //"New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK = "Swipe right to create subtask"; //"Swipe right to insert subtask", "New task ->for subtask"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_TASK_NO_SWIPE_RIGHT = "New task"; //"Task (swipe right: subtask)"

//    protected static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertText"; //used to save inline text from within the InlineInsert container
    private boolean insertAsSubt = false; //true if the user has selected to insert new task as a subtask of the preceding task, set by Swipe action!
    private int insertLevel = 0; //true if the user has selected to insert new task as a subtask of the preceding task, set by Swipe action!
    private MyTextField2 textEntryField;
//    private ItemAndListCommonInterface refItem;
    /**
     * the initial reference item, used when right-swiping to insert subtasks,
     * and then left-swiping back to the original level where a category may
     * need to be added
     */
    private Item orgItem;
    private Item refItem;
    private Category category2;
    private MyForm myForm;
    private Command editNewCmd;
//    private MyForm myForm2;
    private ItemAndListCommonInterface itemOrItemListForNewElements;
    private ItemAndListCommonInterface lastCreatedItem;
    private boolean insertBeforeElement = false;
//    private Action closeAction = null;

    /**
     * if true, a new insertContainer will be added each time a new item is
     * added. This is done in MyTree which will compare each displayed item with
     * the just generated and add a new insertContainer just after the
     * previously created item
     */
    private boolean continueAddingNewItems = true;

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param item2 item after which to add new task or under which to add new
     * subtask (item2's owner becomes the list in which to insert a new item,
     * *after* item2)
     */
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2) {
//        this(myForm2, item2, item2.getOwner(), null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, boolean insertBeforeElement) {
//        this(myForm2, item2, item2.getOwner(), null, insertBeforeElement, null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2) {
//        this(myForm2, item2, itemOrItemListForNewTasks2, null);
//    }
//    public InlineInsertNewItemContainer2(MyForm myForm2, Item item2, ItemAndListCommonInterface itemOrItemListForNewTasks2, Category category2) {
//        this(myForm2, item2, itemOrItemListForNewTasks2, category2, false);
//    }
//</editor-fold>
    /**
     * create a new Container and a new Item
     *
     * @param myForm
     * @param referenceItem item after which to add new task or under which to
     * add new subtask
     * @param itemOrItemListForNewTasks list into which add Items
     * @param category optional category
     * @param insertBeforeElement if true, insert *before* item, instead of, as
     * default, after
     */
//    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks, Category category, boolean insertBeforeElement) {
    public InlineInsertNewItemContainer2(MyForm form, Item referenceItem, ItemAndListCommonInterface itemOrItemListForNewTasks,
            Category category, boolean insertBeforeElement) {//, boolean insertAsSubtask) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this(myForm, item, itemOrItemListForNewTasks, category, insertBeforeElement, null);
//    }
//
////    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//    public InlineInsertNewItemContainer2(MyForm myForm, Item item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//            Category category, boolean insertBeforeElement, Action closeAction) {
//        this.myForm2 = myFormXXX;
//        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
//</editor-fold>
        if (Config.TEST) {
            ASSERT.that(!(itemOrItemListForNewTasks instanceof Category), "InlineInsertNewItemContainer2 called with Category as ownerList");
        }
        this.orgItem = referenceItem; //new Item();
        this.refItem = orgItem; //new Item();
        this.category2 = category; //new Item();
        ASSERT.that(itemOrItemListForNewTasks != null || category != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewElements = itemOrItemListForNewTasks;
        this.insertBeforeElement = insertBeforeElement;
//        this.closeAction = closeAction;
        continueAddingNewItems = MyPrefs.itemContinueAddingInlineItems.getBoolean();

        if (Config.TEST) {
            setName("InlineInsertNewItemContainer2"); //for debugging
        }
        Container contForTextEntry = new Container(new BorderLayout());
        Container swipC;
        if (refItem != null) {
            swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), contForTextEntry);
        } else {
            swipC = contForTextEntry;
        }
        add(swipC);

        this.myForm = form;
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, item.getObjectIdP());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS, item.getClassName());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, insertBeforeElement);

        textEntryField = new MyTextField2(100); //TODO!!!! need field to enter edit mode //UI: 100 width of text field (to avoid showing a small one on eg tablet
        textEntryField.setName("inlineItemEditFieldAsync");
        textEntryField.setNextFocusDown(null);

//          AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField2, item, 1000, () -> item.setText(textEntryField2.getText())); //normal that this appear as non-used!
        if (myForm.previousValues != null && myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT) != null) {
            textEntryField.setText((String) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT));
        }

//        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK) != null) {
////            insertAsSubtask = (Boolean) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//            insertAsSubtask =  myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK)!=null;
//        }
        insertAsSubt = myForm.previousValues != null && myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK) != null;
        insertLevel = myForm.previousValues != null && myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL) != null
                ? (int) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL) : 0;
//        this.insertAsSubt = insertAsSubtask;

        setUIID(insertAsSubt ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!

        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

        if (Config.TEST) {
            textEntryField.setName("InlineInsert text field");
        }
        textEntryField.setHint(refItem == null || !(refItem instanceof Item) ? ENTER_TASK_NO_SWIPE_RIGHT : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //if no item, then don't show hint about swipe right for subtask
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//        myForm.setEditOnShowOrRefresh(textEntryField2); //ensure fields enters edit mode after show() or removeFromCache
        if (refItem != null && swipC instanceof SwipeableContainer) {
            SwipeableContainer swipable = ((SwipeableContainer) swipC);
            swipable.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
                boolean oldInsertAsSubtask = insertAsSubt;
                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
                if (swipable.isOpenedToRight()) { //right-swipae <=> insert as subtask (if possible)
                    if (!insertAsSubt && !insertBeforeElement && refItem instanceof Item) {
                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask");
                        setUIID("InlineInsertItemAsSubtask"); //TODO!!!
                        insertAsSubt = true;
                        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK, true);
                        insertLevel++;
                        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
                    }
                } else { //left-swipe <=> stop inserting as subtask (if possible)
                    if (insertAsSubt) {
                        ASSERT.that(refItem instanceof Item, "Oups, sth's wrong, trying to stop inserting as subtask and refItem not an Item, refItem=" + refItem);
                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, refItem NOT changed=" + refItem);
                        setUIID("InlineInsertItemAsTask"); //TODO!!!
                        insertAsSubt = false;
                        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
                        insertLevel--;
                        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
//                    } else if (refItem != null) {
                    } else if (insertLevel > 0 || refItem.getOwner() instanceof Item) {
                        ItemAndListCommonInterface refOwnerObj = refItem.getOwner();
                        if (refOwnerObj instanceof Item) {
                            Item refOwner = (Item) refOwnerObj;
                            if (refOwner.getList().indexOf(refItem) == refOwner.getList().size() - 1) { //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                            if (!(myForm.getDisplayedElement() instanceof Item) || myForm.getDisplayedElement() != refOwner) { //and if owner is NOT the current form's top-level element (avoid swipe-left in screen displaying a projects subtasks)
                                if (refOwner != myForm.getDisplayedElement()) { //and if owner is NOT the current form's top-level element (avoid swipe-left in screen displaying a projects subtasks)
                                    insertLevel--;
                                    myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
//<editor-fold defaultstate="collapsed" desc="comment">
//                            if (refOwner!= myForm.getIrefOwner.getList().indexOf(refItem) == ((Item) refItem.getOwner()).getList().size() - 1 //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                    if (insertAsSubtask && refItem.getOwner() instanceof Item
//                            && ((Item) refItem.getOwner()).getList().indexOf(refItem) == ((Item) refItem.getOwner()).getList().size() - 1 //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                            && !(MyDragAndDropSwipeableContainer.getParentScrollYContainer(this) instanceof MyTree2) //avoid
//                            ) {
//                                    ASSERT.that(refItem instanceof Item, "Oups, sth's wrong, trying to stop inserting as subtask and refItem not an Item, refItem=" + refItem);
//                                    refItem = ((Item) refItem.getOwner());
//                                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//                                    if (insertAsSubt && refItem != null && refItem.getOwner() instanceof Item) {
//                                        refItem = (Item) refItem.getOwner(); //revert to previous refItem
//                                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, setting back to refItem=" + refItem);
//                                    } else
//                                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, refItem NOT changed=" + refItem);
//                                    setUIID("InlineInsertItemAsTask"); //TODO!!!
//                                    insertAsSubt = false;
//</editor-fold>
                                    refItem = refOwner; //revert to previous refItem
                                }
                            }
                        }
                    } else {
                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=+" + insertAsSubt + ", refItem NOT changed=" + refItem);
                    }
                }
                if (insertAsSubt != oldInsertAsSubtask) {
                    setUIID(insertAsSubt ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
                    textEntryField.setHint(insertAsSubt ? ENTER_SUBTASK : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //item!=null to avoid 
                }
//                InlineInsertNewItemContainer2.this.setUIID(insertAsSubtask ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
//                if (false) swipable.setSwipeActivated(insertAsSubt);
                ev.consume();
                swipable.close();
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    myForm.setEditOnShow(textEntryField);
////                textEntryField.
////                swipC.getParent().revalidateWithAnimationSafety();//update with new hint
//                    swipC.revalidateWithAnimationSafety();//update with new hint
//                    textEntryField.repaint();
//                }
//                    myForm.revalidate();
//                    textEntryField.requestFocus();
//                    textEntryField.startEditingAsync();
//                textEntryField.startEditing(); //NOT good: left-swipe gets textField stuck on the left
//                textEntryField2.repaint(); //update with new hint
//                if (insertAsSubtask && !myForm.expandedObjects.contains(refItem)) {
//                    //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
//                    myForm.expandedObjects.add(refItem); //expand to show subtasks
//                    myForm.refreshAfterEdit();
//                } else {
//                    textEntryField2.repaint(); //update with new hint
//                    revalidate(); //ensure removeFromCache?! TODO necessary??!!
//                }
//</editor-fold>
            });
        }

        //DONE listener - create and insert new task
        textEntryField.setDoneListener(
                (ev) -> { //When pressing ENTER, insert new task
                    if (Config.TEST) {
                        Log.p("ev.isConsumed()" + ev.isConsumed() + (swipC instanceof SwipeableContainer ? (", swipC.isOpen()=" + ((SwipeableContainer) swipC).isOpen()) : ", no SwipeableContainer"));
                    }
                    if (!ev.isConsumed() && (!(swipC instanceof SwipeableContainer) || !((SwipeableContainer) swipC).isOpen())) {
//<editor-fold defaultstate="collapsed" desc="comment">
////                        MyForm myForm = (MyForm) getComponentForm();
////                        Item newItem = createNewTaskForInlineInsert(false); //store new task for use when recreating next insert container
//                        Item newItem = null;
//                        String taskText = textEntryField.getText();
//                        if (taskText != null && taskText.length() > 0) {
//                            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
//                            newItem = new Item(taskText, true); //true: interpret textual values
//                        }
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (continueAddingNewItems) {
////                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
////                        } else {
////                            lastCreatedItem = null; //store new task for use when recreating next insert container
////                        }
////</editor-fold>
//                        if (newItem != null) {
//                            lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
//                            insertNewTaskAndSaveChanges(newItem);
////                            ASSERT.that(newItem.getOwner() != null); //owner will be set to Inbox on saving the item
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (lastCreatedItem != null) {
////                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////                        } else {
////                            myForm.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
////                        }
////</editor-fold>
//                            if (false) myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                            else myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : refItem, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////<editor-fold defaultstate="collapsed" desc="comment">
////                        closeInsertNewTaskContainer();
////                            getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
////                            Container parent = getParent();
////                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
//////                        parent.removeComponent(InlineInsertNewItemContainer2.this);
//////                            parent.replace(InlineInsertNewItemContainer2.this,
//////                                    ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
//////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.animateHierarchy(300);
////                            if (continueAddingNewItems) {
////                                lastCreatedItem = newItem; //ensures that MyTree2 will create a new insertContainer after newTask
////                            }
////                            myForm.animateMyForm();
////</editor-fold>
//                            closeInsertContainer(false);
//                            myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
//                        } else { //if no new item created, remove the container like with Close (x)
////<editor-fold defaultstate="collapsed" desc="comment">
////                            Container parent = getParent();
////                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
//////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
//////                            parent.replace(InlineInsertNewItemContainer2.this, new Label(), null);
////                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.animateHierarchy(300);
////</editor-fold>
//                            closeInsertContainer(true);
////                            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
////                            parent.animateLayout(300); //not necesssary with replace?
//                        }
//</editor-fold>
                        done();
                    }
                }
        );
        if (false) {
            contForTextEntry.add(BorderLayout.CENTER, textEntryField);
        }

        Container westCont = new Container(BoxLayout.x());
        contForTextEntry.add(BorderLayout.WEST, westCont);

        //CLOSE button, only add if in a non-empty list
        if (itemOrItemListForNewElements != null && itemOrItemListForNewElements.getSize() > 0) {
            westCont.add(new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
                //TODO!!! Replay: store the state/position of insertContainer 
//<editor-fold defaultstate="collapsed" desc="comment">
//                myForm.lastInsertNewTaskContainer = null;
//                if (myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//                    myForm.setEditOnShowOrRefresh(null);
//                }
//                Container parent = InlineInsertNewItemContainer2.this.getParent();
//                Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
//
////                parent.replace(InlineInsertNewItemContainer2.this, new Label(), new ); //TODO!!! set a transformation
//                MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this); //TODO!!! add smooth transformation like in ??
//                parent.animateLayout(300);
//                MyForm myForm = (MyForm) getComponentForm();
//</editor-fold>
                closeInsertContainer(true);
//                remove();
                myForm.refreshAfterEdit();
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (myForm.getInlineInsertContainer() == this) {
//                myForm.setInlineInsertContainer(null);
//                myForm.revalidate(); //necessary?!
//                }
//                closeInsertNewTaskContainer(myForm); //close without inserting new task
//all below now done in MyForm.setInlineInsertContainer(null):
//                Container parent = getParent();
//                parent.removeComponent(this);
//                lastCreatedItem = null; //needed?
////                myForm.animateMyForm();
//                parent.animateLayout(300);
//</editor-fold>
            })));
        }
        if (false && refItem != null) { //only show icon when insert container is after/below a task
            Label subtaskSupertaskIconsLabel = new Label();
            subtaskSupertaskIconsLabel.setMaterialIcon(Icons.iconIndentExdendInsertNewTask);
            subtaskSupertaskIconsLabel.setUIID("Icon");
            westCont.add(subtaskSupertaskIconsLabel);
            subtaskSupertaskIconsLabel.setVisible(refItem != null);
        }
        westCont.add(textEntryField);

        //Full screen edit of the new task:
        //                                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//        editNewCmd = CommandTracked.create(null, Icons.iconEdit, (ev) -> {
////                new Button(MyReplayCommand.create("CreateNewItemInline-" + item.getObjectIdP(), "", Icons.iconEdit, (ev) -> {
////                new Button(MyReplayCommand.create("CreateNewItemInline", "", Icons.iconEdit, (ev) -> { //CANNOT use getObjectId since item is not saved yet (but also not needed to refer new item since all entered date will be stored locally)
//            Item newItem = createNewTaskForInlineInsert(true);
////                    Item newItem = newTaskTemp != null ? newTaskTemp : new Item();
//            lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts)
//            //TODO!!!! create even if no text was entered into field
////                            MyForm myForm = (MyForm) getComponentForm();
//            myForm.setKeepPos(new KeepInSameScreenPosition(refItem, this)); //if Cancel, keep the current item in place
//            if (false) insertNewTaskAndSaveChanges(newItem); //NO, only do when exiting ScreenItem2. need to insert task before editing in ScreenItem2 since a repeatRule may be added which needs to know where to add repeat instances!
//            //TODO!!!! How to support that Cancel in ScreenItem2 will delete/remove the just inserted new task??!!
////                        new ScreenItem(lastCreatedItem, (MyForm) getComponentForm(), () -> {
////            myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP()); //NB Saved in insertNewTaskAndSaveChanges
//            SaveEditedValuesLocally predefinedValues = new SaveEditedValuesLocally();
//            if (this.category2 != null)
//                predefinedValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(Arrays.asList(this.category2)));
//            predefinedValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(
//                    (insertAsSubtask ? refItem : ((ItemAndListCommonInterface) itemOrItemListForNewElements)).getObjectIdP()))); //store objectId of new owner
//
//            myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //text string will now be locally saved/stored in ScreenItem2 so we can remove it here
//            ScreenItem2 screenItem2 = new ScreenItem2(newItem, myForm, () -> {
//                //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
////                            DAO.getInstance().save(newTask);
//                if (false) insertNewTaskAndSaveChanges(newItem);
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (false && myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
////                            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
////                        }
////                        myForm.setKeepPos(new KeepInSameScreenPosition(newItem));
////</editor-fold>
//                lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
////<editor-fold defaultstate="collapsed" desc="comment">
////                        Container parent = getParent();
////                        parent.removeComponent(InlineInsertNewItemContainer2.this);
////replace the insert container with the created item, NOT GOOD approach since refrehsAfterEdit will rebuild, and not needed??!!
////                        if (false) {
//////                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            Container parent = getParent();
////                            parent.replace(InlineInsertNewItemContainer2.this,
////                                    //                                ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
////                                    ScreenListOfItems.buildItemContainer(myFormXXX, newItem, itemOrItemListForNewTasks, null), null, null, 300); //
////                        }
////</editor-fold>
//                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//
//                if (category2 != null && newItem.getCategories().contains(category2)) {//contains(): only move item within category if the user has not removed the category during manual editing
//                    category2.moveItemInCategory(newItem, refItem, false, insertBeforeElement); //if category was not removed during manual editing, then move the item to the right position wrt refItem
//                    DAO.getInstance().saveInBackground((ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                }
//
//                //ensure that any manuale edits to the item's preset owner are taken into account when saving it
//                if (insertAsSubtask && newItem.getOwner() == refItem) {//add as subtask to previous task, and keep the subtask level
//                    if (myForm.expandedObjects != null)
//                        myForm.expandedObjects.add(refItem); //If inserted as subtask, expand the project to keep it visible
//                    DAO.getInstance().saveInBackground((ParseObject) refItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                    insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
//                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//                }
//
//                if (itemOrItemListForNewElements != null && newItem.getOwner() == itemOrItemListForNewElements) {
//                    itemOrItemListForNewElements.moveItemInList(newItem, refItem, !insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
//                    DAO.getInstance().saveInBackground((ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (category2 != null && newItem.getCategories().contains(category2)) {//contains(): only move item within category if the user has not removed the category during manual editing
////                    category2.moveItemInCategory(newItem, refItem, false, insertBeforeElement); //if category was not removed during manual editing, then move the item to the right position wrt refItem
////                    DAO.getInstance().saveInBackground((ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                }
////                if (insertAsSubtask && newItem.getOwner() == refItem && myForm.expandedObjects != null) {//add as subtask to previous task, and keep the subtask level
////                    myForm.expandedObjects.add(refItem); //If inserted as subtask, expand the project to keep it visible
////                    DAO.getInstance().saveInBackground((ParseObject) refItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                    insertAsSubtask = false; //remove the subtask property so next task does not become a subtask to the subtask
////                }
////
////                if (itemOrItemListForNewElements != null && newItem.getOwner() == itemOrItemListForNewElements) {
////
////                    itemOrItemListForNewElements.moveItemInList(newItem, refItem, !insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
////                }
////                DAO.getInstance().saveInBackground((ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//////            } else {
//////                ASSERT.that(false, "pinchInsert with no category and no itemList??!! Saving item in Inbox");
////////                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only 'inserted' into inbox, no need to save here, already done above //UI: in xx case, inserted into Inbox
//////            }
////        }
////</editor-fold>
//                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
//                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
//
//                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen,
//                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, false); //delete the marker on exit
//                myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new
//            }, false, null);
//            screenItem2.show();
//        }, "InlineEditItem");
//</editor-fold>
        editNewCmd = CommandTracked.create(null, Icons.iconEdit, (ev) -> {

            Item newItem = new Item(textEntryField.getText(), true); //true: interpret textual values
            newItem.setRemainingDefaultValue();
            if (false) {
                textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            }            //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
            newItem.setOwner(insertAsSubt && refItem instanceof Item ? refItem : itemOrItemListForNewElements);

            SaveEditedValuesLocally prevValues = null;
            if (category2 != null) {
                prevValues = new SaveEditedValuesLocally();
                prevValues.putCategories(new ArrayList(Arrays.asList(category2)));
            }

            boolean isTemplate = false;
            if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
                isTemplate = true;
                newItem.setTemplate(true);
            }

            if (false) {
                lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts) //UI: NO, for now, Cancel in ScreenItem2 will just bring you back to unchanged insert container
            }
            myForm.setKeepPos(new KeepInSameScreenPosition(refItem, this)); //if Cancel, keep the current item in place 
            //DONE!!!! How to support that Cancel in ScreenItem2 will delete/remove the just inserted new task??!!
            SaveEditedValuesLocally predefinedValues = new SaveEditedValuesLocally();
            if (this.category2 != null) {
//                predefinedValues = new SaveEditedValuesLocally();
                predefinedValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(new ArrayList(Arrays.asList(this.category2))));
            }
            predefinedValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(
                    (insertAsSubt ? refItem : ((ItemAndListCommonInterface) itemOrItemListForNewElements)).getObjectIdP()))); //store objectId of new owner

            myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //text string will now be locally saved/stored in ScreenItem2 so we can remove it here
            ScreenItem2 screenItem2 = new ScreenItem2(newItem, myForm, () -> {
                textEntryField.setText(""); //clear text now that new item is created

                lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
//                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements

                if (category2 != null && newItem.getCategories().contains(category2)) {//contains(): only move item within category if the user has not removed the category during manual editing
                    category2.moveItemInCategory(newItem, refItem, false, this.insertBeforeElement); //if category was not removed during manual editing, then move the item to the right position wrt refItem
                    DAO.getInstance().saveNew((ParseObject) category2, false); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                }

                //ensure that any manuale edits to the item's preset owner are taken into account when saving it
                if (insertAsSubt && newItem.getOwner() == refItem) { //add as subtask to previous task, and keep the subtask level
                    if (myForm.expandedObjects != null) {
                        myForm.expandedObjects.add(refItem); //If inserted as subtask, expand the project to keep it visible
                    }
                    DAO.getInstance().saveNew((ParseObject) refItem, false); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                    insertAsSubt = false; //remove the subtask property so next task does not become a subtask to the subtask
                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
                }

                if (itemOrItemListForNewElements != null && newItem.getOwner() == itemOrItemListForNewElements) {
                    itemOrItemListForNewElements.moveItemInList(newItem, refItem, !this.insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
                    DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements, false); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                }
//                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text

                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen, 
                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
                if (false) {
                    myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new 
                }//            }, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, false), isTemplate, predefinedValues);
                if (false) {
                    closeInsertContainer(false); //NO, keep iserting even when returning from editing details of new item
                }
                DAO.getInstance().triggerParseUpdate();
            }, () -> myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE), isTemplate, predefinedValues);
            screenItem2.show();
        }, "InlineEditItem");
        contForTextEntry.add(BorderLayout.EAST, new Button(editNewCmd));
    }

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newItem
     * @return
     */
    private void insertNewTaskAndSaveChanges(Item newItem) {
//        DAO.getInstance().save((ParseObject) newItem); //need to save first, other DAO.fetchListElementsIfNeededReturnCachedIfAvail called by getList will complain that no ObjectId
        if (insertAsSubt) { //add as subtask to previous task, and keep the subtask level
            if (refItem != null) {
                if (((ParseObject) refItem).getObjectIdP() == null) //save if refItem has not already been saved //UI: if you start adding inlineInsert subtasks to a new project, the project will be saved
                //                    DAO.getInstance().saveAndWait(refItem);
                {
//                    DAO.getInstance().saveInBackground(refItem);
                }
                refItem.addToList(newItem, true); //UI: add to *end* of subtask list (if already exists)
                //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
//                myForm.expandedObjects.add(refItem); //expand to show subtasks
//                DAO.getInstance().saveNew(newItem, () -> {
//                    saveKeys(newItem);
//                    myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
//                }, false);
//                DAO.getInstance().saveNew(refItem, true);
                DAO.getInstance().saveNew(newItem);
                DAO.getInstance().saveNew(refItem);
                DAO.getInstance().saveNewExecuteUpdate();
                
                myForm.expandedObjects.add(refItem); //expand to show subtasks
                saveKeys(newItem);
                myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
                
                refItem = newItem; //now start inserting below just added subtask
                insertAsSubt = false; //remove the subtask property so next task does not become a subtask to the subtask
                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
            }
        } else {
            if (category2 != null && insertLevel == 0) { //if category defined,  means we're inserting into list of category items, so the ItemList owning the refItem should be ignored!! insertLevel==0 => only add Category if inserting at initial level below the category
//                ((Item) newItem).addCategoryToItem(category2, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                if (refItem == null) {
                    category2.addItemToCategory(newItem, true); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                } else {
                    int index = category2.getItemIndex(refItem);
                    if (index > -1) {
                        category2.addItemToCategory(newItem, index + (insertBeforeElement ? 0 : 1), true); //add after item, unless insertBeforeElement is true, then insert *before* element
                    } else {
                        category2.addItemToCategory(newItem, true); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                    }
                }
//                category2.addItemToCategory(newItem, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
//                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                DAO.getInstance().saveNew((ParseObject) category2, true); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                DAO.getInstance().saveNew(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                DAO.getInstance().saveNew((ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                DAO.getInstance().saveNewExecuteUpdate();
                saveKeys(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                //UI: if inserting tasks directly into a category, they will be added to the Inbox list
            } else if (true || insertLevel == 0) { //itrue=always insert below itemOrItemListForNewElements (may be the owner of a subtask) after refItem
                if (itemOrItemListForNewElements != null && !itemOrItemListForNewElements.isNoSave()) {
                    //make a sistertask (insert in same list as item, after item)
                    //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
                    if (refItem == null) { //TODO: when could refItem==null???
                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                    } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    int index = itemOrItemListForNewElements.getItemIndex(refItem);
//                    if (index > -1) {
//                        itemOrItemListForNewElements.addToList(index + (insertBeforeElement ? 0 : 1), newItem); //add after item, unless insertBeforeElement is true, then insert *before* element
//                    } else {
//                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
//                    }
//</editor-fold>
                        itemOrItemListForNewElements.addToList(newItem, refItem, !insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
                    }
//                DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                    if (itemOrItemListForNewElements.getObjectIdP() != null) { //only save here if owner is already saved (otherwise it is subtasks for a new project, so  subtasks will be saved w project)
//                        DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements, true); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                    }
                        DAO.getInstance().saveNew(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                        DAO.getInstance().saveNewExecuteUpdate();
                        saveKeys(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
                } else {
                    ASSERT.that(false, "pinchInsert with no category and no itemList??!! Saving item in Inbox");
//                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only 'inserted' into inbox, no need to save here, already done above //UI: in xx case, inserted into Inbox
                }
            } else {
                refItem.addToList(newItem, true); //add to end of refItem's subtasks (or as first element if no subtasks)
            }
            if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
                newItem.setTemplate(true);
            }
        }

//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
    }

    public void done() {
//                            if (Config.TEST) Log.p("ev.isConsumed()" + ev.isConsumed() + (swipC instanceof SwipeableContainer ? (", swipC.isOpen()=" + ((SwipeableContainer) swipC).isOpen()) : ", no SwipeableContainer"));
//                    if (!ev.isConsumed() && (!(swipC instanceof SwipeableContainer) || !((SwipeableContainer) swipC).isOpen())) {
//                        MyForm myForm = (MyForm) getComponentForm();
//                        Item newItem = createNewTaskForInlineInsert(false); //store new task for use when recreating next insert container
        Item newItem = null;
        String taskText = textEntryField.getText();
        if (taskText != null && taskText.length() > 0) {
            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
            newItem = new Item(taskText, true); //true: interpret textual values
            newItem.setRemainingDefaultValue();
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (continueAddingNewItems) {
//                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
//                        } else {
//                            lastCreatedItem = null; //store new task for use when recreating next insert container
//                        }
//</editor-fold>
        if (newItem != null) {
            lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
            insertNewTaskAndSaveChanges(newItem);
//                            ASSERT.that(newItem.getOwner() != null); //owner will be set to Inbox on saving the item
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (lastCreatedItem != null) {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                        } else {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
//                        }
//</editor-fold>
            if (false) {
                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
            } else {
                myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : refItem, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
            }//<editor-fold defaultstate="collapsed" desc="comment">
//                        closeInsertNewTaskContainer();
//                            getParent().removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                        parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this,
////                                    ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewTasks2, null), MorphTransition.create(300));
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
//                            if (continueAddingNewItems) {
//                                lastCreatedItem = newItem; //ensures that MyTree2 will create a new insertContainer after newTask
//                            }
//                            myForm.animateMyForm();
//</editor-fold>
            closeInsertContainer(false);
            myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
        } else { //if no new item created, remove the container like with Close (x)
//<editor-fold defaultstate="collapsed" desc="comment">
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this, new Label(), null);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
//</editor-fold>
            closeInsertContainer(true);
//                            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
//                            parent.animateLayout(300); //not necesssary with replace?
        }
//                    }
    }

    private void saveKeys(Item newItem) {
        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
        if (false) {
            myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS, ((ParseObject) newItem).getClassName()); //already saved and can't change during an insert 'session'
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @return new task if created (meaning some text was entered), otherwise
     * null
     */
//    public Item createNewTaskForInlineInsertXXX() {
//        String taskText = textEntryField.getText();
////        Item newItem;
////        if (createEvenIfNoTextInField || (taskText != null && taskText.length() > 0)) {
//        if (taskText != null && taskText.length() > 0) {
//            textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
//            Item newItem = new Item(taskText, true); //true: interpret textual values
////            if (itemOrItemListForNewElements instanceof Item)
//            if (false) {
//                newItem.updateValuesInheritedFromOwner((insertAsSubt && refItem instanceof Item) ? (Item) refItem : (itemOrItemListForNewElements instanceof Item ? (Item) itemOrItemListForNewElements : null));
//                if (category2 != null) {
//                    newItem.addCategoryToItem(category2, false); //we don't add item to category here (done in xx) since we may still cancel
//                }
////            if (itemOrItemListForNewElements != null) {
//                ASSERT.that(itemOrItemListForNewElements != null, "InlineInsert: no owner list defined!");
//                newItem.setOwner(insertAsSubt ? refItem : itemOrItemListForNewElements); //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
////            }
//                if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
//                    newItem.setTemplate(true);
//                }
//            }
//            return newItem;
//        }
//        return null;
//    }
    /**
     *
     * @return new task if created (meaning some text was entered), otherwise
     * null
     */
//    private Item createNewTaskForInlineEditScreenXXX() {
//        Item newItem = new Item(textEntryField.getText(), true); //true: interpret textual values
//        textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
//        //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
//        newItem.setOwner(insertAsSubt && refItem instanceof Item ? refItem : itemOrItemListForNewElements);
//
//        SaveEditedValuesLocally prevValues = null;
//        if (category2 != null) {
//            prevValues = new SaveEditedValuesLocally();
//            prevValues.putCategories(new ArrayList(Arrays.asList(category2)));
//        }
//
//        if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
//            newItem.setTemplate(true);
//        }
//
//        ASSERT.that(itemOrItemListForNewElements != null, "InlineInsert: no owner list defined!");
//        return newItem;
//    }
//</editor-fold>
    private void closeInsertContainer(boolean stopAddingInlineContainers) {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYContAndReturnScrollYCont(this);
        myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container
        if (stopAddingInlineContainers) {
            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //delete the marker on exit
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK); //delete the marker on exit
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_LEVEL); //delete the marker on exit

            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS); //delete the marker on exit
            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
            if (false) {
                ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated //NOT necessary anymore?!
            }
        }
        if (parent != null) {//TODO!!! edge case where inlineinsert is inserted into empty list (no previous elements in list), so seems it doesn't get a scrollY parent - to investigate
            parent.animateLayout(300); //this call might be what pushes the effect of refreshAfterEdit as an animation
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
//    public InlineInsertNewItemContainer2 getInsertNewTaskContainerFromForm(Item item, ItemAndListCommonInterface itemOrItemList) {
//        if (myForm.lastInsertNewElementContainer == null) { //TODO Optimization: called for every container, replace by local variable?
//            return null;
//        } else {
//            if (item == myForm.lastInsertNewElementContainer.newItem) {
//                return new InlineInsertNewItemContainer2(myForm, myForm.lastInsertNewElementContainer.newItem, itemOrItemList);
//            } else {
//                return null;
//            }
//        }
//    }
//</editor-fold>
    /**
     * used from MyTree2 to construct and insert an insert container in the
     * right place in a list (after item)
     *
     * @param item
     * @param itemOrItemList
     * @return
     */
    @Override
    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
//        if (element == this.refItem && element instanceof Item) {
//this comparison does not require that lastCreatedItem has been saved (yet - as it may happen in background)
        if (element == lastCreatedItem && element instanceof Item) {
//                return new InlineInsertNewItemContainer2((MyForm)getComponentForm(), this.element, this.itemOrItemListForNewElements);
//            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, null);
//            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, category, false);
//            return new InlineInsertNewItemContainer2(null, (Item) element, targetList, category, false);
            return new InlineInsertNewItemContainer2(myForm, (Item) element, targetList instanceof Category ? null : targetList, category, false); //targetList instanceof Category?null: otherwise we may use category as ownerlist
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * close the container and save a new element if text has been entered (used
//     * to deal properly with an existing inlineContainer when a new
//     * inlineContainer is created somewhere else)
//     *
//     * @param saveAnyEnteredElement
//     * @return
//     */
////    @Override
////    private void closeXXX(boolean saveAnyEnteredElement) {
//////        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
////        if (saveAnyEnteredElement) {
////            Item newItem = createNewTask(); //store new task for use when recreating next insert container
////            if (newItem != null) {
////                lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
////                insertNewTaskAndSaveChanges(newItem);
////            }
////            Container parent = getParent();
//////            parent.replace(InlineInsertNewItemContainer2.this,
//////                    newItem != null ? ScreenListOfItems.buildItemContainer(myForm, newItem, itemOrItemListForNewElements, null) : new Label(), MorphTransition.create(300));
////            parent.removeComponent(InlineInsertNewItemContainer2.this);
////            parent.animateHierarchy(300);
//////        return newItem;
////        }
////    }
//
//    /**
//     * closes the container with animation
//     *
//     * @param newItem
//     * @param refreshScreen
//     */
////    private void closeContainerXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
////        if (continueAddingNewItems) {
////            lastCreatedItem = newItem; //store new task for use when recreating next insert container
////        } else {
////            lastCreatedItem = null; //store new task for use when recreating next insert container
////        }
////        if (lastCreatedItem != null) {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////        } else {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
////        }
////        Container parent = getParent();
////        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
////        if (refreshScreen) {
////            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////        } else {
////            parent.animateLayout(300);
//////            myForm.animateMyForm();
////        }
////    }
//
////    private void setRefreshXXX(ItemAndListCommonInterface newItem, boolean refreshScreen) {
////        if (continueAddingNewItems) {
////            lastCreatedItem = newItem; //store new task for use when recreating next insert container
////        } else {
////            lastCreatedItem = null; //store new task for use when recreating next insert container
////        }
////        if (lastCreatedItem != null) {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
////        } else {
////            myForm2.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
////        }
////        Container parent = getParent();
////        parent.removeComponent(this); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
////        if (refreshScreen) {
////            myForm2.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
////        } else {
////            parent.animateLayout(300);
//////            myForm.animateMyForm();
////        }
////    }
//
////    private void insertNewElementXXX(ItemAndListCommonInterface element) {
////        insertNewTaskAndSaveChanges(element);
//////        if (myForm.getEditFieldOnShowOrRefresh() == textEntryField2) {
//////            myForm.setEditOnShowOrRefresh(null); //reset the previous editField
//////        }
////        getParent().removeComponent(InlineInsertNewItemContainer2.this);
////        if (continueAddingNewItems) {
////            lastCreatedItem = element; //ensures that MyTree2 will create a new insertContainer after newTask
////        }
////        myForm2.refreshAfterEdit();
////    }
//</editor-fold>
}
