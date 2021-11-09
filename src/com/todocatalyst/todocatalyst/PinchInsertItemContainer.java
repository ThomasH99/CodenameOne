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
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.parse4cn1.ParseObject;
import com.todocatalyst.todocatalyst.MyForm.Action;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Thomas
 */
public class PinchInsertItemContainer extends PinchInsertContainer {

    //insert special characters on Mac keyboard: Cmd-ctrl-spacebar. NB ? and ? shows as question marks on Simulator. On iOS? on Android?
//    private final static String ENTER_NORMAL_TASK_NO_SWIPE = "Insert task"; //"New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_NORMAL_TASK_NO_SWIPE = "Add task"; //"New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK = "Insert subtask. Swipe <- to insert level above"; //"New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
    private final static String ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK = "Swipe -> to insert as subtask"; //"Swipe right to insert subtask", "New task ->for subtask"; //"New task, swipe right for subtask)"; //"Task (swipe right: subtask)", "New task, ->for subtask)"
    private final static String ENTER_SUBTASK_SWIPE_LEFT_OR_RIGHT = "Task <- swipe -> Subtask"; //"New subtask, <-for task"; //"New subtask, swipe left for task"; //"Enter subtask (swipe left: cancel)"; "New subtask, <-for task"
//    private final static String ENTER_TASK_HINT_NORMAL_TASK = "New task"; //"Task (swipe right: subtask)"
    private final static String ENTER_TASK_HINT_NORMAL_TASK = Item.DESCRIPTION_HINT; //"Add task"; //"Task (swipe right: subtask)"
//    private final static String ENTER_TASK_HINT_SUBTASK = "New subtask"; //"Task (swipe right: subtask)"
    private final static String ENTER_TASK_HINT_SUBTASK_XXX = "Add subtask"; //"Task (swipe right: subtask)"

    final static String REPLAY_CMD_ID = "PinchCreateItem"; //"Task (swipe right: subtask)"
    final static String REPLAY_CMD_EDIT_ITEM_FULL_SCREEN_ID = "PinchEditItemFullScreen"; //"Task (swipe right: subtask)"

//    protected static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertText"; //used to save inline text from within the InlineInsert container
//    private boolean insertAsSubt = false; //true if the user has selected to insert new task as a subtask of the preceding task, set by Swipe action!
//    private int insertLevel = 0; //true if the user has selected to insert new task as a subtask of the preceding task, set by Swipe action!
    private MyTextField2 textEntryField;
    private boolean alreadyClosed; //track if a pinchConainer has already been closed (in another listener)
    private Label hintLabel = new Label(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK, null, "PinchInsertHintLabel");
//    private ItemAndListCommonInterface refItem;
    /**
     * the initial reference item, used when right-swiping to insert subtasks,
     * and then left-swiping back to the original level where a category may
     * need to be added
     */
//    private Item orgItem;
//    private Item refItem;
//    private String refEltGuid2;
    private Category category2N;
//    private MyForm myForm;
//    private MyReplayCommand editNewCmd;
    private CommandTracked editNewCmd;
//    private MyForm myForm2;
    private ItemAndListCommonInterface itemOrItemListForNewElements;
//    private ItemAndListCommonInterface lastCreatedItem;
//    private boolean insertBeforeElement2 = false;
    private Label swipeRightSubtask = new Label("Subtask");
    private Label swipeLeftTask = new Label("Task");
    private Container swipeTopCont;
    MyDragAndDropSwipeableContainer swipC;
//    private Action closeAction = null;

    private Item getRefItemN() {
//        return DAO.getInstance().fetchItemN(refEltGuid2);
        return DAO.getInstance().fetchItemN(getRefGuid());
    }

    /**
     * used to determine if we're inserting a new subtask at top-level of a
     * category so we must add the task to the category, or not, in which case
     * the task is just added as a subtask
     *
     * @return
     */
    private int getInsertLevel() {
//             myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_LEVEL) != null
        Integer insertLevel = (Integer) myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_LEVEL);
        if (myForm.previousValues != null && insertLevel != null) {
//            return (int) myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_LEVEL);
            return insertLevel;
        } else {
            return 0;
        }
    }

    private void setInsertLevel(int insertLevel) {
        if (myForm.previousValues != null && insertLevel != 0) {
            myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_LEVEL);
        }
    }

//    private void setSwipeLeftRightXXX(MyDragAndDropSwipeableContainer swipC) {
//        if (true) { //disabled for now, not working
//            swipC.setOpenToRightEnabled(!refEltGuid2.isEmpty() && !isInsertAsSubt()); //insert as subtask
//            swipC.setOpenToLeftEnabled(isInsertAsSubt());
//        }
//    }
    /**
     * if true, a new insertContainer will be added each time a new item is
     * added. This is done in MyTree which will compare each displayed item with
     * the just generated and add a new insertContainer just after the
     * previously created item
     */
//    private boolean continueAddingNewItems = true;
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
     * @param refEltGuid item after which to add new task or under which to, or
     * empty string if inserting first element in ist add new subtask
     * @param itemOrItemListForNewTasks list into which add Items
     * @param categoryN optional category
     * @param insertBeforeElementXXX if true, insert *before* item, instead of,
     * as default, after
     */
//    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks, Category category, boolean insertBeforeElement) {
//    public PinchInsertItemContainer(MyForm form, Item referenceItem, ItemAndListCommonInterface itemOrItemListForNewTasks,
//            Category category, boolean insertBeforeElement) {//, boolean insertAsSubtask) {
    public PinchInsertItemContainer(MyForm form, String refEltGuid, Category categoryN, boolean insertBeforeElementXXX) {//, boolean insertAsSubtask) {
        this(form, refEltGuid, null, categoryN, insertBeforeElementXXX);
    }

    /**
     * make the apprope´iate hint label:
     *
     * @param refEltGuid
     * @return
     */
//    private void updateHintLabelXXX(String refEltGuid) { //Label hintLabel,String refEltGuid) {
////        String refEltGuid = getRefGuid();
//        if (!refEltGuid.isEmpty()) {
//            if (isInsertAsSubt()) {
////                hintLabel = new Label(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK, null, "PinchInsertHintLabel");
//                hintLabel.setText(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK);
//            } else {
////                hintLabel = new Label(ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK, null, "PinchInsertHintLabel");
//                hintLabel.setText(ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK);
//            }
//        } else if (this.insertBeforeElement2) {
////            hintLabel = new Label(ENTER_NORMAL_TASK_NO_SWIPE, null, "PinchInsertHintLabel");
//            hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
////        } else if (!this.insertBeforeElement) {
////            hintLabel = new Label(ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK, null, "PinchInsertHintLabel");
//        } else {
//            //if no item, then don't show hint about swipe right for subtask
//            hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
//        }
////        return hintLabel;
//    }
    private boolean isLastSubtask(Item subtask) {
        ItemAndListCommonInterface ownerItem = subtask.getOwner();
        if (ownerItem instanceof Item) {
            return (ownerItem.getList().indexOf(subtask) == ownerItem.getList().size() - 1);
        }
        return false; //if ownerItem is not instanceof Item, then owner is e.g. a list and we can swipe left
    }

    private void updatePinchCont() {
//        String refEltGuid = getRefGuid();
//        boolean insertAsSubtask = isInsertAsSubt();
//<editor-fold defaultstate="collapsed" desc="comment">
//        updateHintLabel(refEltGuid);
//        if (false) {
//            if (!refEltGuid.isEmpty()) {
//                if (insertAsSubtask) {
//                    hintLabel.setText(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK);
//                } else {
//                    hintLabel.setText(ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK);
//                }
//            } else if (this.insertBeforeElement2) {
//                hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
//            } else {
//                //if no item, then don't show hint about swipe right for subtask
//                hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
//            }
//        }
//</editor-fold>
//        swipeRightSubtask.setHidden(!(refEltGuid.isEmpty() && !insertAsSubtask));

        Item refElt = DAO.getInstance().fetchItemN(getRefGuid());
        if (refElt == null) {
            swipeLeftTask.setHidden(true);
            swipeRightSubtask.setHidden(true);
        } else { // (refElt != null) {
            if (isInsertAsSubt()) { //|| refElt.getOwner() instanceof ItemList) {
                swipeRightSubtask.setHidden(true);
//                if (refElt.getOwner() != myForm.getTopLevelList()) {
//                    swipeLeftTask.setHidden(false);
//                } else {
                swipeLeftTask.setHidden(false); //we can always swipe left if we've already activated insertAsSubtask
//                }
            } else { // !insertAsSubtask
                swipeRightSubtask.setHidden(isInsertBeforeRefElt()); //if not already swiped right to insert as subtask, you can always swipe right to insert as subtask
//                if (isLastSubtask(refElt) && refElt.getOwner() != ((MyForm) getComponentForm()).getTopLevelList()) {
                if (!isInsertBeforeRefElt() && isLastSubtask(refElt) && refElt.getOwner() != myForm.getTopLevelList()) {
                    //when pinchCont is inserted after the last subtask in a list, AND refElt is not a top-level task, we can swipe left to insert one level higher
                    swipeLeftTask.setHidden(false);
                } else {
                    swipeLeftTask.setHidden(true);
                }
            }
        }

        if (swipeLeftTask.isHidden() && swipeRightSubtask.isHidden()) {
            hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
        } else if (!swipeLeftTask.isHidden() && !swipeRightSubtask.isHidden()) {
            hintLabel.setText(ENTER_SUBTASK_SWIPE_LEFT_OR_RIGHT);
        } else if (!swipeLeftTask.isHidden()) {
            hintLabel.setText(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK);
        } else if (!swipeRightSubtask.isHidden()) {
            hintLabel.setText(ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK);
        } else {
            ASSERT.that("shouldn't happen?!");
            //if no item, then don't show hint about swipe right for subtask
            hintLabel.setText(ENTER_NORMAL_TASK_NO_SWIPE);
        }

        if (!swipeRightSubtask.isHidden() || isInsertBeforeRefElt() || (swipeLeftTask.isHidden() && swipeRightSubtask.isHidden())) { //can swipe right to insertAsSubtask
            //currently inserting as a normal task
//            setInsertAsSubt(true);
//            setInsertLevel(getInsertLevel() + 1);
//            setUIID("InlineInsertItemAsSubtask");
            swipeTopCont.setUIID("InlineInsertItemAsTask"); //if we can swipe right to insert as a subtask, it is currently a normal task!
            if (Config.TEST) {
                textEntryField.setHint(ENTER_TASK_HINT_NORMAL_TASK + (isInsertAsSubt() ? "\\" : "-") + (refElt != null ? refElt.getText() : "noRefElt"));
            } else {
                textEntryField.setHint(ENTER_TASK_HINT_NORMAL_TASK);
            }
        } else {
            //currently inserting as a subtask
//            setInsertAsSubt(false);
//            setInsertLevel(getInsertLevel() - 1);
            swipeTopCont.setUIID("InlineInsertItemAsSubtask");
//            textEntryField.setHint(ENTER_TASK_HINT_SUBTASK_XXX);
            if (Config.TEST) {
                textEntryField.setHint(ENTER_TASK_HINT_NORMAL_TASK + (isInsertAsSubt() ? "\\" : "-") + (refElt != null ? refElt.getText() : "noRefElt"));
            } else {
                textEntryField.setHint(ENTER_TASK_HINT_NORMAL_TASK);
            }
        }
//        textEntryField.setHint(insertAsSubtask ? ENTER_TASK_HINT_SUBTASK : ENTER_TASK_HINT_NORMAL_TASK);

    }

    public PinchInsertItemContainer(MyForm form, String refEltGuid, ItemAndListCommonInterface ownerOfRefElt, Category categoryN, boolean insertBeforeElementXXX) {//, boolean insertAsSubtask) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this(myForm, item, itemOrItemListForNewTasks, category, insertBeforeElement, null);
//    }
//
////    public InlineInsertNewItemContainer2(MyForm myForm, ItemAndListCommonInterface item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//    public InlineInsertNewItemContainer2(MyForm myForm, Item item, ItemAndListCommonInterface itemOrItemListForNewTasks,
//            Category category, boolean insertBeforeElement, Action closeAction) {
//        this.myForm2 = myFormXXX;
//        ASSERT.that(item2 != null, "why item==null here?"); //Can be null when an empty insertNewTaskContainer is created in an empty list
//ItemAndListCommonInterface itemOrItemListForNewTasks;
//        if (Config.TEST) {
//            ASSERT.that(!(itemOrItemListForNewTasks instanceof Category), "InlineInsertNewItemContainer2 called with Category as ownerList");
//        }
//        this.orgItem = referenceItem; //new Item();
//        this.refItem = orgItem; //new Item();
//        this.refItem = referenceItem; //new Item();
//</editor-fold>
        super();
        this.myForm = form;
        ASSERT.that(Objects.equals(refEltGuid, getRefGuid()), "Should be equal(?!): refEltGuid=" + refEltGuid + " getRefGuid()=" + getRefGuid());
//        this.refEltGuid2 = refEltGuid != null ? refEltGuid : ""; //new Item();
        this.category2N = categoryN; //new Item();
//        ASSERT.that(itemOrItemListForNewTasks != null || category != null, "why itemOrItemListForNewTasks2==null here?");
        this.itemOrItemListForNewElements = ownerOfRefElt;
        ASSERT.that(itemOrItemListForNewElements != null, "error - itemOrItemListForNewElements==null");
        ASSERT.that((itemOrItemListForNewElements instanceof ItemList && !(itemOrItemListForNewElements instanceof Category)) || (itemOrItemListForNewElements instanceof Item), () -> "Pinch owner that is neither Item/ItemList, owner=" + itemOrItemListForNewElements);

//        this.insertBeforeElement2 = insertBeforeElement;
//        this.closeAction = closeAction;
//        continueAddingNewItems = MyPrefs.itemContinueAddingInlineItems.getBoolean();
        if (Config.TEST) {
            setName("InlineInsertNewItemContainer2"); //for debugging
        }
//        Container contForTextEntry = new Container(new MyBorderLayout(MyBorderLayout.SIZE_EAST_BEFORE_WEST));
        swipeTopCont = new Container(new BorderLayout());
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.setUIID("InlineInsertNewItemContainer2"); //set below
//        MyDragAndDropSwipeableContainer swipC;
//        swipC = new SwipeableContainer(refItem instanceof Item && !isInsertAsSubt() ? new Label("Subtask") : null, isInsertAsSubt() ? new Label("Task") : null, cont);
//        if (false) {
////            if (refItem instanceof Item) {
//            if (!this.refEltGuid.isEmpty()) {
////                swipC = new SwipeableContainer(new Label("Subtask"), new Label("Task"), cont);
//                swipC = new MyDragAndDropSwipeableContainer(xxx, cont);
//            } else {
////            swipC = contForTextEntry;
//                swipC = new MyDragAndDropSwipeableContainer(null, null, cont);
//            }
//        }
//</editor-fold>
        swipC = new MyDragAndDropSwipeableContainer(swipeRightSubtask, swipeLeftTask, swipeTopCont);
//        swipC.setOpenToRight(refItem instanceof Item && !isInsertAsSubt()); //insert as subtask
//        swipC.setOpenToLeft(isInsertAsSubt());xxx;

        add(swipC);

//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, item.getObjectIdP());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS, item.getClassName());
//        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, insertBeforeElement);
        textEntryField = new MyTextField2(1000); //TODO!!!! need field to enter edit mode //UI: 100 width of text field (to avoid showing a small one on eg tablet
        textEntryField.setUIID("PinchInsertTextField");
        textEntryField.putClientProperty("iosHideToolbar", Boolean.TRUE); //hide toolbar and only show Done button for ios virtual keyboard
        textEntryField.setName("inlineItemEditFieldAsync");
//        textEntryField.setHint(refItem == null || !(refItem instanceof Item) ? ENTER_TASK_NO_SWIPE_RIGHT : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //if no item, then don't show hint about swipe right for subtask
//        textEntryField.setHint(ENTER_TASK_HINT_NORMAL_TASK); //if no item, then don't show hint about swipe right for subtask
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (refItem instanceof Item) {
//        if (false) {
//            if (!this.refEltGuid.isEmpty()) {
//                if (isInsertAsSubt()) {
//                    hintLabel = new Label(ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK, null, "PinchInsertHintLabel");
//                } else {
////                hintLabel = new Label(refItem instanceof Item && !this.insertBeforeElement ? ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK : ENTER_NORMAL_TASK_NO_SWIPE, null, "PinchInsertHintLabel");
//                    hintLabel = new Label(!this.insertBeforeElement ? ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK : ENTER_NORMAL_TASK_NO_SWIPE, null, "PinchInsertHintLabel");
//                }
//                cont.add(BorderLayout.SOUTH, hintLabel);
//            } else {// else //if no item, then don't show hint about swipe right for subtask
////            hintLabel = new Label("", null, "PinchInsertHintLabel");
//                cont.add(BorderLayout.SOUTH, new Label(ENTER_NORMAL_TASK_NO_SWIPE, null, "PinchInsertHintLabel")); //add empty label to keep same size of container
//            }
//        }
//</editor-fold>
//        makeHintLabel(hintLabel, refEltGuid);
//        updateHintLabel();
        updatePinchCont();
        swipeTopCont.add(BorderLayout.SOUTH, hintLabel);
        textEntryField.setConstraint(TextField.INITIAL_CAPS_SENTENCE); //UI: automatically set caps sentence (first letter uppercase)
//<editor-fold defaultstate="collapsed" desc="comment">
//        textEntryField.setNextFocusDown(null); //HAS no effect (on avoiding that pressing keyboard's Done jumpt to activate time Picker in smallTimer

//        if (myForm.previousValues != null && myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_TEXT) != null) {
//            textEntryField.setText((String) myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_TEXT));
//        }
//</editor-fold>
        textEntryField.setText(getPreviousTextStr());
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK) != null) {
////            insertAsSubtask = (Boolean) myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//            insertAsSubtask =  myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK)!=null;
//        }
//        insertAsSubt = myForm.previousValues != null && myForm.previousValues.get(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK) != null;
//        insertLevel = myForm.previousValues != null && myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_LEVEL) != null
//                ? (int) myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_LEVEL) : 0;
//        this.insertAsSubt = insertAsSubtask;
//        setUIID(insertAsSubt ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
//</editor-fold>
//        cont.setUIID(isInsertAsSubt() ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!

//          AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField2, item, 1000, () -> item.setText(textEntryField2.getText())); //normal that this appear as non-used!
        AutoSaveTimer descriptionSaveTimer = new AutoSaveTimer(myForm, textEntryField, SAVE_LOCALLY_INLINE_INSERT_TEXT); //normal that this appear as non-used! Activate *after* setting textField to save initial value

//        myForm.setEditOnShowOrRefresh(textEntryField2); //ensure fields enters edit mode after show() or removeFromCache
//        if (!this.refEltGuid2.isEmpty() && swipC instanceof SwipeableContainer) {
        if (!getRefGuid().isEmpty() && swipC instanceof SwipeableContainer) {
            SwipeableContainer swipable = ((SwipeableContainer) swipC);
//<editor-fold defaultstate="collapsed" desc="comment">
//            swipable.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
//                boolean oldInsertAsSubtask = isInsertAsSubt();
//                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
//                if (swipable.isOpenedToRight()) { //right-swipae <=> insert as subtask (if possible)
////                    if (!isInsertAsSubt() && !this.insertBeforeElement && refItem instanceof Item) {
//                    if (!isInsertAsSubt() && !this.insertBeforeElement && !this.refEltGuid.isEmpty()) {
//                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask");
//                        setUIID("InlineInsertItemAsSubtask"); //TODO!!!
////                        insertAsSubt = true;
////                        myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK, true);
//                        setInsertAsSubt(true);
////                        insertLevel++;
////                        myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
//                        setInsertLevel(getInsertLevel() + 1);
//                        updatePinchCont();
//                    }
//                    swipable.close(); //close here and not below since it may close before having finished and calling the swipe listener
//                } else { //left-swipe <=> stop inserting as subtask (if possible)
//                    ASSERT.that(swipable.isOpenedToLeft(), "Oups, sth's wrong, refItemGuid=" + this.refEltGuid);
//                    if (isInsertAsSubt()) {
////                        ASSERT.that(!this.refEltGuid.isEmpty(), "Oups, sth's wrong, trying to stop inserting as subtask and refItem not an Item, refItem=" + this.refEltGuid);
//                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, refItem NOT changed=" + this.refEltGuid);
//                        if (false) {
//                            setUIID("InlineInsertItemAsTask"); //TODO!!!
//                        } else {
//                            cont.setUIID(isInsertAsSubt() ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask");
//                        }
////                        insertAsSubt = false;
////                        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//                        setInsertAsSubt(false);
////<editor-fold defaultstate="collapsed" desc="comment">
////                        insertLevel--;
////                        if (getInsertLevel() == 0) {
////                            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_LEVEL);
////                        } else {
////                            myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
////                        }
////</editor-fold>
//                        setInsertLevel(getInsertLevel() - 1);
////                    } else if (refItem != null) {
//                    } ////<editor-fold defaultstate="collapsed" desc="comment">
//                    //                    else if (false && (getInsertLevel() > 0 || refItem.getOwner() instanceof Item)) {
//                    //                        ItemAndListCommonInterface refOwnerObj = refItem.getOwner();
//                    //                        if (refOwnerObj instanceof Item) {
//                    //                            Item refOwner = (Item) refOwnerObj;
//                    //                            if (refOwner.getList().indexOf(refItem) == refOwner.getList().size() - 1) { //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                    ////                            if (!(myForm.getDisplayedElement() instanceof Item) || myForm.getDisplayedElement() != refOwner) { //and if owner is NOT the current form's top-level element (avoid swipe-left in screen displaying a projects subtasks)
//                    //                                if (refOwner != myForm.getDisplayedElement()) { //and if owner is NOT the current form's top-level element (avoid swipe-left in screen displaying a projects subtasks)
//                    ////                                    insertLevel--;
//                    ////                                    myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_LEVEL, insertLevel);
//                    //                                    setInsertLevet(getInsertLevel() - 1);
//                    ////                            if (refOwner!= myForm.getIrefOwner.getList().indexOf(refItem) == ((Item) refItem.getOwner()).getList().size() - 1 //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                    ////                    if (insertAsSubtask && refItem.getOwner() instanceof Item
//                    ////                            && ((Item) refItem.getOwner()).getList().indexOf(refItem) == ((Item) refItem.getOwner()).getList().size() - 1 //if refItem is *last* in its owner's list, then we can left-swipe the inline insert cont
//                    ////                            && !(MyDragAndDropSwipeableContainer.getParentScrollYContainer(this) instanceof MyTree2) //avoid
//                    ////                            ) {
//                    ////                                    ASSERT.that(refItem instanceof Item, "Oups, sth's wrong, trying to stop inserting as subtask and refItem not an Item, refItem=" + refItem);
//                    ////                                    refItem = ((Item) refItem.getOwner());
//                    ////                                    myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//                    ////                                    if (insertAsSubt && refItem != null && refItem.getOwner() instanceof Item) {
//                    ////                                        refItem = (Item) refItem.getOwner(); //revert to previous refItem
//                    ////                                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, setting back to refItem=" + refItem);
//                    ////                                    } else
//                    ////                                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, refItem NOT changed=" + refItem);
//                    ////                                    setUIID("InlineInsertItemAsTask"); //TODO!!!
//                    ////                                    insertAsSubt = false;
//                    //                                    refItem = refOwner; //revert to previous refItem
//                    //                                }
//                    //                            }
//                    //                        }
//                    //                    }
//                    ////</editor-fold>
//                    else {
//                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=+" + isInsertAsSubt() + ", refItem NOT changed=" + this.refEltGuid);
//                    }
//                    swipable.close(); //close here and not below since it may close before having finished and calling the swipe listener
//                }
//                if (isInsertAsSubt() != oldInsertAsSubtask) {
//                    cont.setUIID(isInsertAsSubt() ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
////                    textEntryField.setHint(insertAsSubt ? ENTER_SUBTASK : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK); //item!=null to avoid
////                    if (hintLabel != null) {
////                        hintLabel.setText(isInsertAsSubt() ? ENTER_SUBTASK_SWIPE_LEFT_FOR_NORMAL_TASK : ENTER_TASK_SWIPE_RIGHT_FOR_SUBTASK);
////                    }
////                    makeHintLabel(hintLabel, refEltGuid);
//                    updateHintLabel();
////                    textEntryField.setHint(isInsertAsSubt() ? ENTER_TASK_HINT_SUBTASK : ENTER_TASK_HINT_NORMAL_TASK);
//                    updatePinchCont();
//                }
//                setSwipeLeftRight(swipC); //enable/disable swipe left and/or right
////<editor-fold defaultstate="collapsed" desc="comment">
////                InlineInsertNewItemContainer2.this.setUIID(insertAsSubtask ? "InlineInsertItemAsSubtask" : "InlineInsertItemAsTask"); //TODO!!!
////                if (false) swipable.setSwipeActivated(insertAsSubt);
////                ev.consume();
////                swipable.close();
////                if (false) {
////                    myForm.setEditOnShow(textEntryField);
//////                textEntryField.
//////                swipC.getParent().revalidateWithAnimationSafety();//update with new hint
////                    swipC.revalidateWithAnimationSafety();//update with new hint
////                    textEntryField.repaint();
////                }
////                    myForm.revalidate();
////                    textEntryField.requestFocus();
////                    textEntryField.startEditingAsync();
////                textEntryField.startEditing(); //NOT good: left-swipe gets textField stuck on the left
////                textEntryField2.repaint(); //update with new hint
////                if (insertAsSubtask && !myForm.expandedObjects.contains(refItem)) {
////                    //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
////                    myForm.expandedObjects.add(refItem); //expand to show subtasks
////                    myForm.refreshAfterEdit();
////                } else {
////                    textEntryField2.repaint(); //update with new hint
////                    revalidate(); //ensure removeFromCache?! TODO necessary??!!
////                }
////</editor-fold>
//            });
//</editor-fold>
            swipable.addSwipeOpenListener((ev) -> { //Swipe RIGHT/LEFT for subtask/task
                //swipe right == make it a subtask //TODO!!!! use button/icon instead to mark it subtask eg [S] or indented subtask icon
                if (swipable.isOpenedToRight()) { //right-swipae <=> insert as subtask (if possible)
//                    if (!isInsertAsSubt() && !this.insertBeforeElement && refItem instanceof Item) {
//                    if (!isInsertAsSubt() && !this.insertBeforeElement && !this.refEltGuid.isEmpty()) {
                    if (getRefItemN() != null) {
//    List<Item> subtasks = getRefItemN().getListFull(); //full list since we can insert after even filtered (not currently visible) subtasks
                        List<Item> subtasks = getRefItemN().getList(); //MUST be getList() since the refElt must be visible (not filtered in this view) for replay of PinchInsert to work
                        if (subtasks.size() > 0) {
                            myForm.expandedObjects.add(getRefItemN()); //expand subtasks to make the hierarchy into which we pinchInsert is visible
                            setRefElt(subtasks.get(subtasks.size() - 1));
                            setInsertLevel(getInsertLevel() + 1);
                        } else { //there are no (further) expandable subtasks we can insert after (use as refElt), so we mark that new task should be inserted as subtask to previous task
                            setInsertAsSubt(true);
                            setInsertLevel(getInsertLevel() + 1);
                        }
                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask");
                    }
//                    if (false && !isInsertAsSubt() && !this.refEltGuid2.isEmpty()) {
                    if (false && !isInsertAsSubt() && !getRefGuid().isEmpty()) {
                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask");
                        setInsertAsSubt(true);
                        setInsertLevel(getInsertLevel() + 1);
                    }
                    swipable.close(); //close here and not below since it may close before having finished and calling the swipe listener
                    updatePinchCont();
                } else { //left-swipe <=> stop inserting as subtask (if possible) or insert as task at higher level
                    ASSERT.that(swipable.isOpenedToLeft(), "Oups, sth's wrong, refItemGuid=" + getRefGuid()); //this.refEltGuid2);
//                    if (true || isInsertAsSubt()) {
                    ASSERT.that(!getRefGuid().isEmpty(), "if inserting as subtask, refEltGuid should never be empty");
                    Log.p("InlineInsertNewItemContainer2: insertAsSubtask=false, refItem NOT changed=" + getRefGuid());
                    if (isInsertAsSubt()) {
                        setInsertAsSubt(false); //may already be false
//                            if (getInsertLevel() > 0) {
                        ASSERT.that(getInsertLevel() > 0, "whenever we were previously inserting as subtask, insertLevel should be >0, insertLevel=" + getInsertLevel());
                        setInsertLevel(getInsertLevel() - 1);
                    } else if (getRefItemN() != null && getRefItemN().getOwner() != null && getRefItemN().getOwner() != myForm.getTopLevelList()) {
                        if (Config.TEST) {
                            Item refItem = getRefItemN();
                            ItemAndListCommonInterface refItemOwner = refItem.getOwner();
                            int insertLevel = getInsertLevel();
                            setRefElt(getRefItemN().getOwner());
                            setInsertLevel(getInsertLevel() - 1);
                            moveDropPlaceholderToAppropriateParentCont(getRefItemN().getGuid(), this);
                        } else {
                            setRefElt(getRefItemN().getOwner());
                            setInsertLevel(getInsertLevel() - 1);
                            moveDropPlaceholderToAppropriateParentCont(getRefItemN().getGuid(), this);
                        }
                    }
//<editor-fold defaultstate="collapsed" desc="comment">
//                        setInsertLevel(getInsertLevel() - 1);
//                        Log.p("InlineInsertNewItemContainer2: insertAsSubtask=+" + isInsertAsSubt() + ", refItem NOT changed=" + this.refEltGuid2);
//                        swipable.close(); //close here and not below since it may close before having finished and calling the swipe listener
//</editor-fold>
                    updatePinchCont();
                    swipable.close(); //close here and not below since it may close before having finished and calling the swipe listener
                }
            });
        }

        ActionListener closeListener = (ev) -> { //When pressing ENTER, insert new task. //UI:CloseListener also called if exiting a screen *without* closing the textArea!! so leaving a non-empty pinchCont when exiting a screen will create the corresponding item
            if (!alreadyClosed && getComponentForm() != null) { //can be called twice as DoneListener and as CloseListener
                alreadyClosed = true;
                if (Config.TEST) {
                    Log.p("ev.isConsumed()" + ev.isConsumed() + (swipC instanceof SwipeableContainer ? (", swipC.isOpen()=" + ((SwipeableContainer) swipC).isOpen()) : ", no SwipeableContainer"));
                }
                if (!ev.isConsumed() && (!(swipC instanceof SwipeableContainer) || !((SwipeableContainer) swipC).isOpen())) {
                    createNewItemInsertSaveClosePinch();
                }
            }
        };
        //DONE listener - create and insert new task
        textEntryField.setDoneListener(closeListener);
        textEntryField.addCloseListener(closeListener); //needed on Android?!
        swipeTopCont.add(BorderLayout.CENTER, textEntryField);
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container westCont = new Container(BoxLayout.x());
//        contForTextEntry.add(BorderLayout.WEST, westCont);
//        if (itemOrItemListForNewElements != null && itemOrItemListForNewElements.getSize() > 0) {
//            westCont.add(new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
//</editor-fold>
        //CLOSE button, only add if in a non-empty list
        Button closeButton = new Button(Command.createMaterial(null, Icons.iconCloseCircle, (ev) -> {
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
//            if ((getRefItemN() != null && getRefItemN().getOwner() != null
//                    && getRefItemN().getOwner() != null && getRefItemN().getOwner().size() > 0)
//                    || (itemOrItemListForNewElements != null && itemOrItemListForNewElements.size() > 0)) { //UI: only allow closing if there are other elements in the list (to avoid a completely empty screen)
            if (true) {
                closePinchContainer(true);
//                remove();
                myForm.refreshAfterEdit(); //optimization: enough to just revalidate the existing list
            }
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
        }));
        closeButton.setUIID("PinchInsertTextCloseButton");
        swipeTopCont.add(BorderLayout.WEST, closeButton);
//<editor-fold defaultstate="collapsed" desc="comment">
//        }

//        if (false && refItem != null) { //only show icon when insert container is after/below a task
//            Label subtaskSupertaskIconsLabel = new Label();
//            subtaskSupertaskIconsLabel.setMaterialIcon(Icons.iconIndentExdendInsertNewTask);
//            subtaskSupertaskIconsLabel.setUIID("Icon");
////            westCont.add(subtaskSupertaskIconsLabel);
//            subtaskSupertaskIconsLabel.setVisible(refItem != null);
//        }
//        westCont.add(textEntryField);
        //Full screen edit of the new task:
        //                                new Button(Command.create(null, Icons.iconEditSymbolLabelStyle, (ev) -> {
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
//        editNewCmd = CommandTracked.create(null, Icons.iconEdit, (ev) -> {
        //creating the ReplayCommand here will add it to the Form so it will be available for next Replay (where PinchInsertCont is created based on locally stored values)
//</editor-fold>
        editNewCmd = MyReplayCommand.create(REPLAY_CMD_EDIT_ITEM_FULL_SCREEN_ID, "", Icons.iconEdit, (ev) -> {
            Item refItem = getRefItemN();
//<editor-fold defaultstate="collapsed" desc="comment">
//            Item newItem = new Item(textEntryField.getText(), true); //true: interpret textual values, ok to create item even if no text entered (user just wants to create task here, but enter text+details in full sreen edit)
//            newItem.setRemainingDefaultValueIfNone();
//            if (false) {
//                textEntryField.setText(""); //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
//            }            //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
//            newItem.setOwner(insertAsSubt && refItem instanceof Item ? refItem : itemOrItemListForNewElements);
//</editor-fold>
            Item newItem = makeNewItemN(true, refItem); //UI: OK to create empty task since it'll be edited in ScreenItem2
//<editor-fold defaultstate="collapsed" desc="comment">
//            SaveEditedValuesLocally prevValues = null; //a 'hack' to make Category appear in editing and still make it possible to remove while editing
//            if (category2 != null) {
//                prevValues = new SaveEditedValuesLocally();
//                prevValues.putCategories(new ArrayList(Arrays.asList(category2)));
//            }
//            boolean isTemplate = false;
//            if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
//                isTemplate = true;
//                newItem.setTemplate(true);
//            }
//            if (false) {
//                lastCreatedItem = null; //reset value (in case ScreenItem does a Cancel meaning no more inserts) //UI: NO, for now, Cancel in ScreenItem2 will just bring you back to unchanged insert container
//            }
//            if (false) {
//                myForm.setKeepPos(new KeepInSameScreenPosition(refItem, this)); //if Cancel, keep the current item in place 
//            }            //DONE!!!! How to support that Cancel in ScreenItem2 will delete/remove the just inserted new task??!!
//            SaveEditedValuesLocally predefinedValues = new SaveEditedValuesLocally();
//            if (this.category2 != null) {
////                predefinedValues = new SaveEditedValuesLocally();
////                predefinedValues.put(Item.PARSE_CATEGORIES, Item.convCategoryListToObjectIdList(new ArrayList(Arrays.asList(this.category2))));
//                predefinedValues.putCategories(new ArrayList(Arrays.asList(category2)));
//            }
////            predefinedValues.put(Item.PARSE_OWNER_ITEM, new ArrayList(Arrays.asList(
////                    (insertAsSubt ? refItem : ((ItemAndListCommonInterface) itemOrItemListForNewElements)).getObjectIdP()))); //store objectId of new owner
//            predefinedValues.putOwner(insertAsSubt ? refItem : itemOrItemListForNewElements);
            //insert BEFORE editing to make sure owner and category are already set/visible when editing the task details
//            if (false) {
//                if (isInsertAsSubt()) {
//                    refItem.addToList(newItem, true);
//                } else if (this.category2 != null) {
//                    category2.addItemToCategory(newItem, refItem, true, false); //should insert newItem into Inbox automatically
//                } else {
//                    itemOrItemListForNewElements.addToList(newItem, refItem, true);
//                }
//            }
//</editor-fold>
            alreadyClosed = true; //hack to ignore the call to textEntryField.closeListener which is automatic when the textField loses focus
//            insertNewTask(newItem);
//            insertNewTaskAndSaveChanges(newItem); //also need to save (at least to local cache) for Replay to work - this is equivalent to finishing the insertion and then editing the just created task

//            myForm.previousValues.put(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true); //marker to indicate that the inlineinsert container launched edit of the task
            setFullScreenActive(true);
//            if (false) {
//                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_TEXT); //NO, text string will now be locally saved/stored in ScreenItem2 so we can remove it here
//            }
            ScreenItem2 screenItem2 = new ScreenItem2(newItem, myForm, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    textEntryField.setText(""); //NOT needed since container never reused, leave text fo debugging. //clear text now that new item is created
//                }
//                if (false) {
////                textEntryField.removeCloseListener(closeListener);
//                    insertNewTaskAndSaveChanges(newItem);
//                    if (newItem.hasSaveableData()) {
////                    lastCreatedItem = continueAddingNewItems ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
////                        lastCreatedItem = MyPrefs.itemContinueAddingInlineItems.getBoolean() ? newItem : null; //ensures that MyTree2 will create a new insertContainer after newTask
////<editor-fold defaultstate="collapsed" desc="comment">
////                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                    DAO.getInstance().saveNew(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                    DAO.getInstance().saveNewTriggerUpdate(); //need to save synchrnouesly to get objId to store for recreating new pinchinsert on exit
////                    saveKeys(newItem);
////</editor-fold>
////                    myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
//                        setRefGuid(newItem.getGuid());
//                        if (false && category2 != null && newItem.getCategories().contains(category2)) {//contains(): only move item within category if the user has not removed the category during manual editing
//                            category2.moveItemInCategory(newItem, refItem, false, this.insertBeforeElement); //if category was not removed during manual editing, then move the item to the right position wrt refItem
////                        DAO.getInstance().saveNew((ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                        }
//
//                        //ensure that any manuale edits to the item's preset owner are taken into account when saving it
//                        if (isInsertAsSubt() && newItem.getOwner() == refItem) { //add as subtask to previous task, and keep the subtask level
//                            if (myForm.expandedObjects != null) {
//                                myForm.expandedObjects.add(refItem); //If inserted as subtask, expand the project to keep it visible
//                            }
////                        DAO.getInstance().saveNew((ParseObject) refItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
////                        insertAsSubt = false; //remove the subtask property so next task does not become a subtask to the subtask
////                        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
//                            setInsertAsSubt(false);
//                        }
//
//                        //the item may have been inserted into a (new) owner on exit, if so do nothing. If already added, e.g. to end of Inbox, then move to right place. Otherwise insert
//                        if (itemOrItemListForNewElements != null && newItem.getOwner() == itemOrItemListForNewElements) {
//                            itemOrItemListForNewElements.moveOrAddItemInList(newItem, refItem, !this.insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
////                        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
//                        }
////                myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
////                    myForm.previousValues.remove(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
//                        setInsertBeforeRefElt(false); //always insert *after* just created inline item
////                    myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
//                        setPreviousTextStr("");
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false) {
////                    myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this, -1)); //if editing the new task in separate screen,
////                }
////                myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
////                if (false) {
////                    myForm.refreshAfterEdit();  //OK? NOT good, refreshAfterEdit will remove the new
////                }
////            }, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, false), isTemplate, predefinedValues);
////                if (true) {
////</editor-fold>
//                        //need to close *this* container, and then let the previous form create a new one!
//                        closePinchContainer(false); //NO, keep iserting even when returning from editing details of new item
//                    }
////                }
////                    DAO.getInstance().saveNewTriggerUpdate();
////                    DAO.getInstance().saveToParseNow(newItem,(ParseObject) category2,(ParseObject) itemOrItemListForNewElements);
////                    DAO.getInstance().saveToParseNow(newItem);
//                } else
//</editor-fold>
                {
//                    insertNewTaskAndSaveChanges(newItem);
                    finishPinch(newItem);
                    closePinchContainer(false);
                    //no need to save, taken care of when exiting ScreenItem2
                }
//            }, () -> myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE), newItem.isTemplate(), predefinedValues);
//            }, () -> myForm.previousValues.remove(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE), newItem.isTemplate(), null);
            }, () -> setFullScreenActive(false), newItem.isTemplate(), null);

            screenItem2.show();
            if (false) { //try to avoid this kind of hacks
                myForm.setIgnoreNextActionEvent(true); //ignore the actionEvent sent to ScreenListOfItems when the displayed list is updated (to avoid closing the 
            }
//            insertNewTaskAndSaveChangesXXX(newItem); //do *after* showing next screen to avoid unnecessary refresh. Also need to save (at least to local cache) for Replay to work - this is equivalent to finishing the insertion and then editing the just created task
            insertNewTask(newItem, refItem); //do *after* showing next screen to avoid unnecessary refresh event. Also need to save (at least to local cache) for Replay to work - this is equivalent to finishing the insertion and then editing the just created task

        }); //, "InlineEditItem");

        //Enter full screen edit of the new Item:
//        contForTextEntry.add(BorderLayout.EAST, new Button(editNewCmd));
        Button editItemFullScreen = new Button(editNewCmd);
        editItemFullScreen.setUIID("PinchInsertTextEditButton");
        swipeTopCont.add(BorderLayout.EAST, editItemFullScreen);
//        add(contForTextEntry);
    }
    //<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * insert newItem in the right place (either subtask of item or task in
     * itemOrItemListForNewTasks
     *
     * @param newItem
     * @return
     */
    //    private void insertNewTaskAndSaveChangesXXX(Item newItem) {
    ////        DAO.getInstance().save((ParseObject) newItem); //need to save first, other DAO.fetchListElementsIfNeededReturnCachedIfAvail called by getList will complain that no ObjectId
    //        if (isInsertAsSubt()) { //add as subtask to previous task, and keep the subtask level
    //            if (this.refEltGuid != null) {
    //                if (((ParseObject) refItem).getObjectIdP() == null) { //save if refItem has not already been saved //UI: if you start adding inlineInsert subtasks to a new project, the project will be saved
    //                    //                    DAO.getInstance().saveAndWait(refItem);
    ////                    DAO.getInstance().saveInBackground(refItem);
    //                }
    //                refItem.addToList(newItem, true); //UI: add to *end* of subtask list (if already exists)
    //                //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                myForm.expandedObjects.add(refItem); //expand to show subtasks
    ////                DAO.getInstance().saveNew(newItem, () -> {
    ////                    saveKeys(newItem);
    ////                    myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
    ////                }, false);
    ////                DAO.getInstance().saveNew(refItem, true);
    ////</editor-fold>
    ////                DAO.getInstance().saveNew(newItem);
    ////                DAO.getInstance().saveNew(refItem);
    ////                DAO.getInstance().saveNewTriggerUpdate();
    ////                DAO.getInstance().saveToParseNow(newItem,refItem);
    //                DAO.getInstance().saveToParseNow(newItem);
    //
    //                myForm.expandedObjects.add(refItem); //expand to show subtasks
    ////                saveKeys(newItem);
    ////                myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
    //                setRefGuid(newItem.getGuid());
    ////                myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
    ////                myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
    //
    //                refItem = newItem; //now start inserting below just added subtask
    ////                insertAsSubt = false; //remove the subtask property so next task does not become a subtask to the subtask
    ////                myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
    //                setInsertAsSubt(false);
    //            }
    //        } else {
    //            if (category2 != null && getInsertLevel() == 0) { //if category defined,  means we're inserting into list of category items, so the ItemList owning the refItem should be ignored!! insertLevel==0 => only add Category if inserting at initial level below the category
    ////                ((Item) newItem).addCategoryToItem(category2, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
    //                if (refItem == null) {
    //                    category2.addItemToCategory(newItem, true); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
    //                } else {
    //                    int index = category2.getItemIndex(refItem);
    //                    if (index > -1) {
    //                        category2.addItemToCategory(newItem, index + (insertBeforeElement ? 0 : 1), true); //add after item, unless insertBeforeElement is true, then insert *before* element
    //                    } else {
    //                        category2.addItemToCategory(newItem, true); //if item is null or not in orgList, insert at beginning of (potentially empty) list
    //                    }
    //                }
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                category2.addItemToCategory(newItem, false); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
    ////                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                DAO.getInstance().saveNew((ParseObject) category2, true); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////</editor-fold>
    ////                DAO.getInstance().saveNew(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                DAO.getInstance().saveNew((ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                DAO.getInstance().saveNewTriggerUpdate();
    ////                DAO.getInstance().saveToParseNow(newItem,(ParseObject) category2); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    //                DAO.getInstance().saveToParseNow(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                saveKeys(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
    //                setRefGuid(newItem.getGuid());
    //                //UI: if inserting tasks directly into a category, they will be added to the Inbox list
    //            } else if (true || getInsertLevel() == 0) { //itrue=always insert below itemOrItemListForNewElements (may be the owner of a subtask) after refItem
    //                if (itemOrItemListForNewElements != null && !itemOrItemListForNewElements.isNoSave()) {
    //                    //make a sistertask (insert in same list as item, after item)
    //                    //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
    //                    if (refItem == null) { //TODO: when could refItem==null???
    //                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
    //                    } else {
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                    int index = itemOrItemListForNewElements.getItemIndex(refItem);
    ////                    if (index > -1) {
    ////                        itemOrItemListForNewElements.addToList(index + (insertBeforeElement ? 0 : 1), newItem); //add after item, unless insertBeforeElement is true, then insert *before* element
    ////                    } else {
    ////                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
    ////                    }
    ////</editor-fold>
    //                        itemOrItemListForNewElements.addToList(newItem, refItem, !insertBeforeElement); //add after item, unless insertBeforeElement is true, then insert *before* element
    //                    }
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////                DAO.getInstance().saveInBackground((ParseObject) newItem, (ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                DAO.getInstance().saveInBackground(newItem, () -> myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP())); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    if (itemOrItemListForNewElements.getObjectIdP() != null) { //only save here if owner is already saved (otherwise it is subtasks for a new project, so  subtasks will be saved w project)
    ////                        DAO.getInstance().saveNew(newItem, () -> saveKeys(newItem)); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                        DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements, true); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    }
    ////</editor-fold>
    ////                    DAO.getInstance().saveNew(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    DAO.getInstance().saveNew((ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    DAO.getInstance().saveNewTriggerUpdate();
    ////                    DAO.getInstance().saveToParseNow(newItem,(ParseObject) itemOrItemListForNewElements); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    //                    DAO.getInstance().saveToParseNow(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    saveKeys(newItem); //need to save both since newItem has gotten its owner set to itemOrItemListForNewElements
    ////                    myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
    //                    setRefGuid(newItem.getGuid());
    //                } else {
    //                    ASSERT.that(false, "pinchInsert with no category and no itemList??!! Saving item in Inbox");
    ////                DAO.getInstance().saveInBackground((ParseObject) newItem); //task only 'inserted' into inbox, no need to save here, already done above //UI: in xx case, inserted into Inbox
    //                }
    //            } else {
    //                refItem.addToList(newItem, true); //add to end of refItem's subtasks (or as first element if no subtasks)
    //            }
    ////            if ((refItem != null && refItem.isTemplate()) || itemOrItemListForNewElements == TemplateList.getInstance()) {
    ////                newItem.setTemplate(true);
    ////            }
    //        }
    //
    ////        myForm.previousValues.put(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, false); //always insert *after* just created inline item
    ////        myForm.previousValues.remove(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //always insert *after* just created inline item
    //        setInsertBeforeRefElt(false); //always insert *after* just created inline item
    ////        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
    //        setPreviousTextStr("");
    //    }
    //</editor-fold>
    private void finishPinch(Item newItem) {
        if (true || isInsertAsSubt()) { //NO need to test since should never remain true after first subtask insertion. add as subtask to previous task, and keep the subtask level
            setInsertAsSubt(false);
        }
//        setRefGuid(newItem.getGuid());
//        setRefGuid(newItem.getGuid());
        setRefElt(newItem);
        setInsertBeforeRefElt(false); //always insert *after* just created inline item
//        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
        setPreviousTextStr("");
    }

    private void insertNewTask(Item newItem, Item refItemN) {
//at this point, when inserting new task, we need to get the refItem which should have been saved by now
        if (isInsertAsSubt()) { //add as subtask to previous task, and keep the subtask level
//            if (refItem != null) {
//                refItem.addToList(newItem, true); //UI: add to *end* of subtask list (if already exists)
            if (refItemN != null) {
                refItemN.addToList(newItem, true); //UI: add to *end* of subtask list (if already exists)
                //if adding as subtask, expand mother task and place container the right place in the hierarcy => or rather eliminate swipe support and always create right type of insertContainer on pinchOut?!
                myForm.expandedObjects.add(refItemN); //expand to show subtasks
//                setRefGuid(newItem.getGuid());
//                setInsertAsSubt(false); //remove the subtask property so next task does not become a subtask to the subtask
            }
        } else {
            if (category2N != null && getInsertLevel() == 0) { //if category defined,  means we're inserting into list of category items, so the ItemList owning the refItem should be ignored!! insertLevel==0 => only add Category if inserting at initial level below the category
                if (refItemN == null) {
                    category2N.addItemToCategory(newItem, true); //add newItem to cateogory (but NOT category to newItem since that is done below in itemOrItemListForNewTasks.addToList(newItem))
                } else {
                    category2N.addItemToCategory(newItem, refItemN, true, !isInsertBeforeRefElt()); //add after item, unless insertBeforeElement is true, then insert *before* element
                }
//                setRefGuid(newItem.getGuid());
                //UI: if inserting tasks directly into a category, they will be added to the Inbox list
            } else { //else always insert below itemOrItemListForNewElements (may be the owner of a subtask) after refItem
                ItemAndListCommonInterface itemOrItemListForNewElements = refItemN != null ? refItemN.getOwner() : this.itemOrItemListForNewElements;
                ASSERT.that(itemOrItemListForNewElements != null);
                ASSERT.that(!itemOrItemListForNewElements.isNoSave());
                if (itemOrItemListForNewElements != null && !itemOrItemListForNewElements.isNoSave()) {
                    //make a sistertask (insert in same list as item, after item)
                    //TODO!!!! if list is sorted used sortOn value and value in previous (rather the next!) item to detect the values of newItem to keep it in (roughly) the same place
                    if (refItemN == null) { //TODO: when could refItem==null???
                        itemOrItemListForNewElements.addToList(newItem); //if item is null or not in orgList, insert at beginning of (potentially empty) list
                    } else {
//                        itemOrItemListForNewElements.addToList(newItem, refItemN, !insertBeforeElement2); //add after item, unless insertBeforeElement is true, then insert *before* element
                        itemOrItemListForNewElements.addToList(newItem, refItemN, !isInsertBeforeRefElt()); //add after item, unless insertBeforeElement is true, then insert *before* element
                    }
//                    setRefGuid(newItem.getGuid());
                } else {
                    ASSERT.that(false, "pinchInsert with no category and no itemList??!! Saving item in Inbox");
                }
            }
        }

//        if (isInsertAsSubt()) { //add as subtask to previous task, and keep the subtask level
//            setInsertAsSubt(false);
//        }
//        setRefGuid(newItem.getGuid());
//        setInsertBeforeRefElt(false); //always insert *after* just created inline item
////        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //task now created for text, so remove locally saved text
//        setPreviousTextStr("");
        finishPinch(newItem);
    }

//    private void insertNewTaskAndSaveChangesXXX(Item newItem) {
//        insertNewTask(newItem);
//        DAO.getInstance().saveToParseNow(newItem);
//    }
    private Item makeNewItemN(boolean createEvenIfTextEmpty, Item refItemN) {
        if ((textEntryField.getText() != null && !textEntryField.getText().isEmpty()) || createEvenIfTextEmpty) {
            Item newItem = new Item(textEntryField.getText(), true); //true: interpret textual values, ok to create item even if no text entered (user just wants to create task here, but enter text+details in full sreen edit)
//            newItem.setRemainingDefaultValueIfNone(); //now done when interpreting the text entry in the line above
            //must set owner here to display correctly if going to full screen edit of item (and if there is a repeatRule)
//            newItem.setOwner(isInsertAsSubt() && refItem instanceof Item ? refItem : itemOrItemListForNewElements);
//            newItem.setOwner(isInsertAsSubt() && refItem instanceof Item ? refItem : (itemOrItemListForNewElements!=null?itemOrItemListForNewElements:refItem.getOwner()));
            newItem.setOwner(isInsertAsSubt() && refItemN instanceof Item ? refItemN : itemOrItemListForNewElements);
//<editor-fold defaultstate="collapsed" desc="comment">
//            SaveEditedValuesLocally prevValues = null; //a 'hack' to make Category appear in editing and still make it possible to remove while editing
//            if (category2 != null) {
//                prevValues = new SaveEditedValuesLocally();
//                prevValues.putCategories(new ArrayList(Arrays.asList(category2)));
//            }
//</editor-fold>
            if (refItemN != null && (refItemN.isTemplate() || refItemN.getOwner() == TemplateList.getInstance())) {
                newItem.setTemplate(true);
            }
//            newItem.setEditedDateToNow(); //now done when creating Item
            return newItem;
        } else {
            return null;
        }
    }

    public void createNewItemInsertSaveClosePinch() {
        Item refItemN = getRefItemN(); //at this point, when inserting new task, we need to get the refItem which should have been saved by now

//                            if (Config.TEST) Log.p("ev.isConsumed()" + ev.isConsumed() + (swipC instanceof SwipeableContainer ? (", swipC.isOpen()=" + ((SwipeableContainer) swipC).isOpen()) : ", no SwipeableContainer"));
//                    if (!ev.isConsumed() && (!(swipC instanceof SwipeableContainer) || !((SwipeableContainer) swipC).isOpen())) {
//                        MyForm myForm = (MyForm) getComponentForm();
//                        Item newItem = createNewTaskForInlineInsert(false); //store new task for use when recreating next insert container
//        Item newItem = null;
//        String taskText = textEntryField.getText();
//        if (taskText != null && taskText.length() > 0) {
//            if (false) {
//                textEntryField.setText(""); //NO need to clear, leave for debugging. //clear text, YES, necessary to avoid duplicate insertion when closing a previously open container
//            }
//            newItem = new Item(taskText, true); //true: interpret textual values
//            newItem.setRemainingDefaultValueIfNone();
//        }
        Item newItem = makeNewItemN(false, refItemN);
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (continueAddingNewItems) {
//                            lastCreatedItem = newItem; //store new task for use when recreating next insert container
//                        } else {
//                            lastCreatedItem = null; //store new task for use when recreating next insert container
//                        }
//</editor-fold>
        if (newItem != null) {
//            lastCreatedItem = continueAddingNewItems ? newItem : null; //store new task for use when recreating next insert container
//            lastCreatedItem = MyPrefs.itemContinueAddingInlineItems.getBoolean() ? newItem : null; //store new task for use when recreating next insert container
//            insertNewTaskAndSaveChanges(newItem);
            insertNewTask(newItem, refItemN);
//                            ASSERT.that(newItem.getOwner() != null); //owner will be set to Inbox on saving the item
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (lastCreatedItem != null) {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem, this, -1)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                        } else {
//                            myForm.setKeepPos(new KeepInSameScreenPosition(element, this, -1)); //otherwise keep same position of mother-item
//                        }
//            if (false) {
//                myForm.setKeepPos(new KeepInSameScreenPosition(newItem, this)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//            } else {
//                if (false) {
//                    myForm.setKeepPos(new KeepInSameScreenPosition(lastCreatedItem != null ? lastCreatedItem : refItem, this, 0)); //if editing the new task in separate screen. -1: keep newItem in same pos as container just before insertTaskCont (means new items will scroll up while insertTaskCont stays in place)
//                }
//            }
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
            closePinchContainer(false);
            if (category2N instanceof Category) { //BIG HACK: no chgEvent is send to Category for new //not necessary, called via changeEvent on list
                myForm.refreshAfterEdit(); //need to store form before possibly removing the insertNew in closeInsertNewTaskContainer
            }
            DAO.getInstance().saveToParseNow(newItem); //save *after* close since save triggers refresh of form
        } else { //if no new item created, remove the container like with Close (x)
//<editor-fold defaultstate="collapsed" desc="comment">
//                            Container parent = getParent();
//                            Container parent = MyDragAndDropSwipeableContainer.getParentScrollYContainer(InlineInsertNewItemContainer2.this);
////                            parent.removeComponent(InlineInsertNewItemContainer2.this);
////                            parent.replace(InlineInsertNewItemContainer2.this, new Label(), null);
//                            MyDragAndDropSwipeableContainer.removeFromParentScrollYContainer(InlineInsertNewItemContainer2.this);
//                            parent.animateHierarchy(300);
//</editor-fold>
            closePinchContainer(true);
            myForm.refreshAfterEdit();
//                            myForm.setInlineInsertContainer(null); //remove this as inlineContainer
//                            parent.animateLayout(300); //not necesssary with replace?
        }
    }

//    private void saveKeysXXX(Item newItem) {
////        myForm.previousValues.put(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY, newItem.getObjectIdP());
////        myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, newItem.getGuid());
//        setRefGuid(newItem.getGuid());
//        ASSERT.that(Item.CLASS_NAME.equals(myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS)),
//                "wrong/changed pinch class name: old locally saved=" + myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS) + "; new=" + Item.CLASS_NAME); //already saved and can't change during an insert 'session'
////        if (false) {
////            myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_PARSE_CLASS, ((ParseObject) newItem).getClassName()); //already saved and can't change during an insert 'session'
////        }
//    }
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
    public void closePinchContainer(boolean stopAddingInlineContainers) {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParentN(this);
        ASSERT.that((textEntryField.getText() != null && !textEntryField.getText().isEmpty()) || parent != null,
                "if parent==null it would mean this inlineCont was not removed! inlineCont=" + this);
        if (parent == null) {
            this.remove(); //remove this in case it wasn't in a ContY (like when it's the first inserted in an empty list)
        }
//        myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //clean up any locally saved text in the inline container

        if (stopAddingInlineContainers) {
//            if (false) 
            myForm.setPinchInsertContainer(null); //remove this as inlineContainer
//<editor-fold defaultstate="collapsed" desc="comment">
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_OBJID_KEY); //delete the marker on exit
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //delete the marker on exit
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK); //delete the marker on exit
//
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_REF_ELT_PARSE_CLASS); //delete the marker on exit
//            myForm.previousValues.remove(MyForm.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
//</editor-fold>
            removePinchInsertKeys(myForm.previousValues); //delete the history/markera on exit
//            if (false) {
//                ReplayLog.getInstance().popCmd(); //pop the replay command added when InlineInsert container was activated //NOT necessary anymore?!
//            }
        } else {
            setPreviousTextStr("");
//            myForm.setKeepPos();
        }
        myForm.setKeepPos(); //always store scroll position
        if (false) {
            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_LEVEL); //delete the marker on exit
        }
        if (parent != null) {//MUST animate to remove previous contaier?? && stopAddingInlineContainers) {//only animate if closing container and not adding another!? TODO!!! edge case where inlineinsert is inserted into empty list (no previous elements in list), so seems it doesn't get a scrollY parent - to investigate
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
//    @Override
////    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
//    public PinchInsertContainer makeXXX(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
////        if (element == this.refItem && element instanceof Item) {
////this comparison does not require that lastCreatedItem has been saved (yet - as it may happen in background)
//        if (element == lastCreatedItem && element instanceof Item) {
////                return new InlineInsertNewItemContainer2((MyForm)getComponentForm(), this.element, this.itemOrItemListForNewElements);
////            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, null);
////            return new InlineInsertNewItemContainer2((MyForm) getComponentForm(), (Item) element, targetList, category, false);
////            return new InlineInsertNewItemContainer2(null, (Item) element, targetList, category, false);
//            return new PinchInsertItemContainer(myForm, (Item) element, targetList instanceof Category ? null : targetList, category, false); //targetList instanceof Category?null: otherwise we may use category as ownerlist
//        }
//        return null;
//    }
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
