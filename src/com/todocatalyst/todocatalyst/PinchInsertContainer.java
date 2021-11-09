/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Form;
import com.codename1.ui.TextArea;
import com.codename1.ui.geom.Dimension;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.parse4cn1.ParseObject;

/**
 * generic root class to easily recognize all InlineInsertNewContainers
 *
 * @author thomashjelm
 */
abstract public class PinchInsertContainer extends Container {//implements InsertNewElementFunc {
//    protected static final String SAVE_LOCALLY_REF_ELT_GUID_KEY = "InlineInsertElementOBJID";

    static final String SAVE_LOCALLY_REF_ELT_GUID_KEY = "InlineInsertElementGUID"; //the ref element after (or before if BEFORE_REF is true) which to insert PinchInsertCont
    static final String SAVE_LOCALLY_REF_ELT_PARSE_CLASS = "InlineInsertEltParseCLASS";
    static final String SAVE_LOCALLY_INSERT_BEFORE_REF_ELT = "InlineInsertBEFORERefElt";
    static final String SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK = "InlineInsertSavedSUBTASK"; //used to save inline text from within the InlineInsert container
    static final String SAVE_LOCALLY_INLINE_INSERT_LEVEL = "InlineInsertSavedLEVEL"; //used to save inline text from within the InlineInsert container

    static final String SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE = "InlineInsertEditTaskACTIVE";
    static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertSavedTEXT"; //used to save inline text from within the InlineInsert container

    protected MyForm myForm;
    protected boolean ignoreCLoseEvent; //set to true to ignore

    protected boolean isInsertAsSubt() {
        return myForm.previousValues != null && myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK) != null;
    }

    protected void setInsertAsSubt(boolean insertAsSubt) {
        if (myForm.previousValues != null && insertAsSubt) {
            myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK, true);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK);
        }
    }

    protected boolean isInsertBeforeRefElt() {
        return myForm.previousValues != null && myForm.previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null;
    }

    protected void setInsertBeforeRefElt(boolean insertBeforeRefElt) {
        if (myForm.previousValues != null && insertBeforeRefElt) {
            myForm.previousValues.put(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT, true);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT);
        }
    }

    protected boolean isFullScreenActive() {
        return myForm.previousValues != null && myForm.previousValues.get(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null;
    }

    protected void setFullScreenActive(boolean fullScreenActive) {
        if (myForm.previousValues != null && fullScreenActive) {
            myForm.previousValues.put(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE, true);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE);
        }
    }

    protected String getPreviousTextStr() {
        return myForm.previousValues != null ? (myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_TEXT) != null ? (String) myForm.previousValues.get(SAVE_LOCALLY_INLINE_INSERT_TEXT) : "") : "";
    }

    protected void setPreviousTextStr(String textStr) {
        if (myForm.previousValues != null && textStr != null && !textStr.isEmpty()) {
            myForm.previousValues.put(SAVE_LOCALLY_INLINE_INSERT_TEXT, textStr);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT);
        }
    }

    protected String getRefGuid() {
        return myForm.previousValues != null ? (myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_GUID_KEY) != null ? (String) myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_GUID_KEY) : "") : "";
    }

    protected void setRefGuid(String guid) {
        if (myForm.previousValues != null && guid != null && !guid.isEmpty()) {
            myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_GUID_KEY, guid);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_REF_ELT_GUID_KEY);
        }
    }

    protected String getRefClass() {
        return myForm.previousValues != null ? (myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS) != null
                ? (String) myForm.previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS) : "") : "";
    }

    protected void setRefClass(String classType) {
        if (myForm.previousValues != null && classType != null && !classType.isEmpty()) {
            myForm.previousValues.put(SAVE_LOCALLY_REF_ELT_PARSE_CLASS, classType);
        } else {
            myForm.previousValues.remove(SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
        }
    }

    protected void setRefElt(ItemAndListCommonInterface refElt) {
//        ASSERT.that(refElt!=null);
        setRefGuid(refElt.getGuid());
        setRefClass(((ParseObject) refElt).getClassName());
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public InsertNewElementFunc make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category) {
//    abstract public PinchInsertContainer make(ItemAndListCommonInterface element, ItemAndListCommonInterface targetList, Category category);
//    {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }
//    @Override
//    public TextArea getTextArea() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }
//</editor-fold>
    abstract public TextArea getTextArea();

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Command getEditTaskCmd() {
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        return null;
//    }
//</editor-fold>
    abstract public Command getEditTaskCmd();
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean removePinchContainer() {
//        Form f = getComponentForm();
//        if (f instanceof MyForm) {
//             SaveEditedValuesLocally prevVals = ((MyForm)f).previousValues;
//             if (prevVals!=null) {
//                 prevVals
//             }
//        }
//                    MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParent(this);
//
//    }
//</editor-fold>

    abstract public void closePinchContainer(boolean stopAddingInlineContainers);

//    abstract public void done();
//    protected static final String SAVE_LOCALLY_INLINE_INSERT_TEXT = "InlineInsertSavedText"; //used to save inline text from within the InlineInsert container
    /**
     * makes this container 'pinchable' by adjusting its size based on the
     * pinchDistance (btw two fingers), will also reduce an already inserted
     * pinchContainer with the same amount to make a smooth transition
     *
     * @return the 'faked' preferred size
     */
    @Override
    public Dimension getPreferredSize() {
        MyForm myForm = (MyForm) getComponentForm();
        if (myForm != null) {
            Dimension orgPrefSize = super.getPreferredSize();
            //TODO!! do like Clear app: if pinching further out than the size show some 'elastic' empty space around the container 
//<editor-fold defaultstate="collapsed" desc="comment">
//                    MyForm myForm = (MyForm) pinchContainer.getComponentForm();
//                    if (myForm.pinchContainer == this) { //I'm the ongoing
//                    Log.p("+++++++++++++ this inside wrapInPinchableContainer = "+this);
//</editor-fold>
            if (this == myForm.newPinchContainer) { //I'm the newly created pinchContainer (NB. this refers to the sourrounding Container!)
//                    int h = Math.max(0, orgPrefSize.getHeight()+pinchDistance); //distance negative if pinching in, max(0 to avoid negative) //TODO!! what happens if pinching out beyond the preferred height (does it just grow or leave white space??)
                int h = Math.max(0, Math.min(myForm.pinchDistance, orgPrefSize.getHeight())); //min:cannot become bigger than preferredHeight of the component, max: can't get negative
                return new Dimension(orgPrefSize.getWidth(), h); //Math.max(0, since pinch distance may become negative when fingers cross vertically
            } else if (this == myForm.currentPinchContainer) { //I'm the previously inserted container
                if (myForm.newPinchContainer != null) { //the other pinchContainer may be pinching me down in size
                    int h = Math.max(0, Math.min(orgPrefSize.getHeight() - myForm.pinchDistance, orgPrefSize.getHeight())); //cannot become bigger than preferredHeight of the component
                    return new Dimension(orgPrefSize.getWidth(), h); //Math.max(0, since pinch distance may become negative when fingers cross vertically
                }
            } //else { //not sure if/why this happens (was it due to overriding calcPreferredSize() instead of getPreferredSize()??
            return orgPrefSize;
        } else {
            return super.getPreferredSize();
        }
    }

    public Dimension getOrgPreferredSize() {
        return super.getPreferredSize();
    }

    /**
     * delete all the pinchInsert state once the (last) pinch is closed
     *
     * @param previousValues
     */
    static public void removePinchInsertKeys(SaveEditedValuesLocally previousValues) {
        previousValues.remove(SAVE_LOCALLY_REF_ELT_GUID_KEY); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_REF_ELT_PARSE_CLASS); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_AS_SUBTASK); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_LEVEL); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE); //delete the marker on exit
        previousValues.remove(SAVE_LOCALLY_INLINE_INSERT_TEXT); //delete the marker on exit
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void createAndAddInsertContainerXXX(String refEltObjId, String eltParseClass, boolean insertBeforeRefElement) {
//        ItemAndListCommonInterface refElement = null;
//        switch (eltParseClass) {
//            case Item.CLASS_NAME:
////                Item aboveItem = DAO.getInstance().fetchItem(refEltObjId);
//                refElement = DAO.getInstance().fetchItem(refEltObjId);
//                break;
//            case ItemList.CLASS_NAME:
////                ItemList aboveItemList = DAO.getInstance().fetchItemList(refEltObjId);
//                refElement = DAO.getInstance().fetchItemList(refEltObjId);
//                break;
//            case Category.CLASS_NAME:
////                ItemList aboveCategory = DAO.getInstance().fetchCategory(refEltObjId);
//                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                break;
//            case WorkSlot.CLASS_NAME:
////                ItemList aboveWorkSlot = DAO.getInstance().fetchCategory(refEltObjId);
//                refElement = DAO.getInstance().fetchCategory(refEltObjId);
//                break;
//            default:
//                if (Config.TEST) ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + eltParseClass);
//        }
////        MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane(), refEltObjId);
//        MyDragAndDropSwipeableContainer myDDContN = findMyDDContWithObjIdN(getContentPane().getChildrenAsList(true), refEltObjId);
//        if (Config.TEST) ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltObjId + ", eltParseClass=" + eltParseClass + ", insertAfter=" + insertBeforeRefElement);
////        createAndAddInsertContainer(myDDContN, refElement, myDDContN.getDragAndDropCategory(), insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
//        createAndAddInsertContainer(myDDContN, refElement, insertBeforeRefElement); //NB: createAndAddInsertContainer checks for null values
//    }
//    private static void createAndInsertInlineInsertContainerEmptyScreenXXX(MyForm myForm) {
//        if (myForm instanceof ScreenListOfItems) {
//            ItemAndListCommonInterface ownerList = ((ScreenListOfItems) myForm).itemListOrItemOrg;
//            if (ownerList instanceof Category) {
//                createAndAddPinchInsertIntoEmptyContainer(myForm, myForm.getContentContainer(), new Item(false), null, (Category) ownerList); //NB: createAndAddInsertContainer checks for null values
//            } else if (ownerList instanceof ItemList || ownerList instanceof Item) {
//                createAndAddPinchInsertIntoEmptyContainer(myForm, myForm.getContentContainer(), new Item(false), ownerList, null); //NB: createAndAddInsertContainer checks for null values
//            }
//        } else if (myForm instanceof ScreenListOfItemLists) {
//            createAndAddPinchInsertIntoEmptyContainer(myForm, myForm.getContentContainer(), new ItemList(), ItemListList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
//        } else if (myForm instanceof ScreenListOfCategories) {
//            createAndAddPinchInsertIntoEmptyContainer(myForm, myForm.getContentContainer(), new Category(), CategoryList.getInstance(), null); //NB: createAndAddInsertContainer checks for null values
//        } else if (myForm instanceof ScreenListOfWorkSlots) {
//            createAndAddPinchInsertIntoEmptyContainer(myForm, myForm.getContentContainer(), new WorkSlot(), ((ScreenListOfWorkSlots) myForm).workSlotListOwner, null); //NB: createAndAddInsertContainer checks for null values
//        } else if (Config.TEST) {
//            ASSERT.that("pinchinsert into empty screen not handled for this screen=" + myForm);
//        }
//    }
//</editor-fold>
    private static ItemAndListCommonInterface fetchRefElt(String refClass, String refEltGuid) {
        ItemAndListCommonInterface refElement;// = null;
        if (refClass != null) {
            switch (refClass) {
                case Item.CLASS_NAME:
//                Item aboveItem = DAO.getInstance().fetchItem(refEltObjId);
                    refElement = DAO.getInstance().fetchItemN(refEltGuid);
                    break;
                case ItemList.CLASS_NAME:
                    refElement = DAO.getInstance().fetchItemList(refEltGuid);
                    break;
                case Category.CLASS_NAME:
                    refElement = DAO.getInstance().fetchCategory(refEltGuid);
                    break;
                case WorkSlot.CLASS_NAME:
                    refElement = DAO.getInstance().fetchWorkSlotFromParseByGuid(refEltGuid);
                    break;
                default: {
                    if (Config.TEST) {
                        ASSERT.that(false, "Error in createAndAddInsertContainer: wrong element ParseClass=" + refClass);
                    }
                    refElement = null;  //needed to only initialize once to use in lambda expression below
                }
            }
        } else {
            refElement = null; //needed to only initialize once to use in lambda expression below
        }
        return refElement;
    }

    static PinchInsertContainer recreateInlineInsertContainerNXXX(MyForm myForm, Category category) {
        if (myForm.previousValues != null) {
            //if inlineInsert was left active when app was last active, then re-insert the container again
            if (myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY) != null) { //check if there were an earlier inline container
                String refEltGuid = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY);
                String refClass = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
                boolean insertBeforeRefElement = myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;
                if (refEltGuid != null && !refEltGuid.isEmpty()) {
                    MyDragAndDropSwipeableContainer myDDContN = MyForm.findMyDDContWithGuidN(myForm.getContentPane().getChildrenAsList(true), refEltGuid);
                    if (Config.TEST) {
                        ASSERT.that(true || myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltGuid
                                + "; refElement=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBeforeRefElement);
                    }
                    if (refEltGuid != null && !refEltGuid.isEmpty() && !insertBeforeRefElement) {
//                        return createAndAddPinchInsertContainer(myForm, refClass, myDDContN, refEltGuid, insertBefore); //NB: createAndAddInsertContainer checks for null values
//                        return         PinchInsertContainer . createInsertContainer(myForm, refClass, refEltGuid,  null, insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
                        return PinchInsertContainer.createInsertContainer(myForm, refClass, refEltGuid, category, insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner

                    } else {
                        ASSERT.that("refElement not found for refEltObjId=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBeforeRefElement);
                    }
                }
            }
        }
        return null;
    }

    /**
     * if inline insert was active in previous session
     * (SAVE_LOCALLY_REF_ELT_OBJID_KEY points to, then
     *
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    static void recreateInlineInsertContainerAndReplayCmdIfNeededOLD(MyForm myForm) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (previousValues != null && (previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY) != null)) {
////            createAndAddInsertContainer((String) previousValues.get(SAVE_LOCALLY_REF_ELT_OBJID_KEY),
////                    (String) previousValues.get(SAVE_LOCALLY_REF_ELT_PARSE_CLASS),
////                    (Boolean) previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT));
////            makeInlineInsertReplayCmd().actionPerformed(null);
////            makeInlineInsertReplayCmd();
////</editor-fold>
//        if (myForm.previousValues != null) {
//            //if inlineInsert was left active when app was last active, then re-insert the container again
//            if (myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY) != null) { //check if there were an earlier inline container
//                String refEltGuid = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY);
//                String refClass = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
////                boolean insertBefore = (Boolean) previousValues.get(SAVE_LOCALLY_INSERT_BEFORE_REF_ELT); //!=null;
//                boolean insertBefore = myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;
////                createAndAddInsertContainer(refEltObjId, refClass, insertBefore);
//
////                ItemAndListCommonInterface refElement = fetchRefElt(refClass, refEltGuid);
////                if (refElement != null) {
//                if (refEltGuid != null && !refEltGuid.isEmpty()) {
//                    MyDragAndDropSwipeableContainer myDDContN = MyForm.findMyDDContWithGuidN(myForm.getContentPane().getChildrenAsList(true), refEltGuid);
//                    if (Config.TEST) {
//                        ASSERT.that(true || myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltGuid
//                                + "; refElement=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBefore);
//                    }
////                    if (myDDContN != null && refElement != null) {
////                    if (myDDContN != null && refEltGuid != null && !refEltGuid.isEmpty()) {
//                    if (refEltGuid != null && !refEltGuid.isEmpty() && !insertBefore) {
////                        createAndAddPinchInsertContainer(myForm, myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values
////                        createAndAddPinchInsertContainer(myForm, myDDContN, refEltGuid, insertBefore); //NB: createAndAddInsertContainer checks for null values
//                        if (true) {
//                            createAndAddPinchInsertContainer(myForm, refClass, myDDContN, refEltGuid, insertBefore); //NB: createAndAddInsertContainer checks for null values
//                        } else {//                        createInsertContainer(myForm, myDDContN, refEltGuid, myDDContN.getDragAndDropCategory(), insertBefore); //NB: createAndAddInsertContainer checks for null values
////                            PinchInsertContainer pinchCnt = createInsertContainer(myForm, refClass, refEltGuid, myDDContN.getDragAndDropCategory(), insertBefore); //NB: createAndAddInsertContainer checks for null values
////                            addPinchInsertContainer(myForm, pinchCnt, myDDContN, insertBefore);
////                            myForm.setPinchInsertContainer(pinchCnt);
//                        }
////<editor-fold defaultstate="collapsed" desc="comment">
////if full screen edit was launched from inline container, then do so here:
////                    if (false && myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
////                        if (false && myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_INLINE_FULLSCREEN_EDIT_ACTIVE) != null) {
//////                        if (false) {
//////                            InsertNewElementFunc inlineCont = getPinchInsertContainer();
//////                            inlineCont.getEditTaskCmd().actionPerformed(null);
//////                        }
////                            if (false && myDDContN != null && refElement != null) { //don't need this, the ReplayCmd is created in InlineInsert
//////                        makeAndAddCreatePinchContCommand(myDDContN, refElement, insertBefore); //inserts the command into Screen's cmds for Replay
////                                MyReplayCommand.create(PinchInsertItemContainer.REPLAY_CMD_ID, null, (e) -> {
//////                                createAndAddInsertContainer(myDDContN, refElement, insertBefore); //NB: createAndAddInsertContainer checks for null values
////                                    if (myForm.getPinchInsertContainer() != null) {
////                                        myForm.getPinchInsertContainer().getEditTaskCmd().actionPerformed(null); //run the cmd to edit task in screen
////                                    }
////                                });
////                            }
////                        }
//                    }
////                        else if (false) {
////                        createAndInsertInlineInsertContainerEmptyScreen(myForm);
////                    }
////</editor-fold>
//                } else {
//                    ASSERT.that("refElement not found for refEltObjId=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBefore);
//                    if (false) { //false: shouldn't be necessary if pinch works correctly?!
//                        removePinchInsertKeys(myForm.previousValues); //if guid can be found sth went wrong, so clean up all pinchInsert state info
//                    }
//                }
//            }
//        }
////        }
//    }
//</editor-fold>
    static void recreateInlineInsertContainerAndReplayCmdIfNeeded(MyForm myForm) {
        if (myForm.previousValues != null) {
            //if inlineInsert was left active when app was last active, then re-insert the container again
            if (myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY) != null) { //check if there were an earlier inline container
                String refEltGuid = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_GUID_KEY);
                String refClass = (String) myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_REF_ELT_PARSE_CLASS);
                boolean insertBefore = myForm.previousValues.get(PinchInsertContainer.SAVE_LOCALLY_INSERT_BEFORE_REF_ELT) != null; //!=null;

                if (refEltGuid != null && !refEltGuid.isEmpty()) {
                    if (Config.TEST) {
//                        Item refElt = DAO.getInstance().fetchItemN(refEltGuid);
                        ItemAndListCommonInterface refElt = fetchRefElt(refClass, refEltGuid);
                        Log.p("refElt=" + refElt);
                    }

                    MyDragAndDropSwipeableContainer myDDContN = MyForm.findMyDDContWithGuidN(myForm.getContentPane().getChildrenAsList(true), refEltGuid);
                    if (Config.TEST) {
                        ASSERT.that(myDDContN != null, "no MyDragAndDropSwipeableContainer found for refEltObjId=" + refEltGuid
                                + "; refElement=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBefore);
                    }
                    if (refEltGuid != null && !refEltGuid.isEmpty() && myDDContN != null) {// && !insertBefore) {
//                        myForm.newPinchContainer = createAndAddPinchInsertContainer(myForm, refEltGuid, refClass, myDDContN, insertBefore); //NB: createAndAddInsertContainer checks for null values
                        myForm.currentPinchContainer = createAndAddPinchInsertContainer(myForm, refEltGuid, refClass, myDDContN, insertBefore); //NB: createAndAddInsertContainer checks for null values
//                        myForm.currentPinchContainer.getTextArea().startEditingAsync();
                    } else {
                        ASSERT.that("refElement not found for refEltObjId=" + refEltGuid + ", eltParseClass=" + refClass + ", insertBefore=" + insertBefore);
                        if (false) { //false: shouldn't be necessary if pinch works correctly?!
                            removePinchInsertKeys(myForm.previousValues); //if guid can be found sth went wrong, so clean up all pinchInsert state info
                        }
                    }
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * either list or category
     *
     * @param refElement
     * @param ownerList
     * @param typeElement just used to indicate the type of element to create
     * since we don't have an actual object to work from
     * @param categoryN
     * @param insertBeforeRefElementXXX
     * @return
     */
//    private InsertNewElementFunc createInsertContainer(ItemAndListCommonInterface typeElement, ItemAndListCommonInterface refElement,
//    private static PinchInsertContainer createInsertContainer(MyForm myForm, ItemAndListCommonInterface typeElement, ItemAndListCommonInterface refElement,
//            ItemAndListCommonInterface ownerList, Category category, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        return createInsertContainer(refElement, list, insertBeforeRefElement, null);
////    }
////
////    private Container createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface list, boolean insertBeforeRefElement, Action closeAction) {
////        ASSERT.that(!insertBeforeRefElement, "not implemented yet");
////        InsertNewElementFunc insertContainer = null;
//        PinchInsertContainer insertContainer = null;
////</editor-fold>
//        if (refElement instanceof Item || typeElement instanceof Item) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (ownerList instanceof Category) {
//////            Item newItem = new Item();
//////            newItem.addCategoryToItem((Category)list, false); //add category in InlineInsertNewItemContainer2
////                return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, null, (Category) ownerList, insertBeforeRefElement)); //don't insert into any list, just add to Category
////            } else if (ownerList instanceof ItemList) {
////                if (((ItemList) ownerList).isNoSave()) {
////                    return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement));
////                } else {
////                    return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement)); //null=> don't insert into any list, only 'inbox'
////                }
////            } else if (ownerList instanceof Item) { //NB! inserting refElement into a Project (as a subtask)!
////                return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, insertBeforeRefElement));
////            } else {
////                ASSERT.that(false, () -> "error1 in createInsertContainer: refElt=" + (refElement == null ? "<null>" : refElement) + "; list=" + (ownerList == null ? "<null>" : ownerList) + "; insertBefore=" + (insertBeforeRefElement));
////            }
////            return wrapInPinchableContainer(new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, ownerList, category, insertBeforeRefElement));
////</editor-fold>
////            insertContainer = new InlineInsertNewItemContainer2(MyForm.this, (Item) refElement, ownerList, category, insertBeforeRefElement);
////            insertContainer = new PinchInsertItemContainer(myForm, (Item) refElement, ownerList, category, insertBeforeRefElement);
//            insertContainer = new PinchInsertItemContainer(myForm, refElement, ownerList, category, insertBeforeRefElement);
//
//        } else if (refElement instanceof Category || typeElement instanceof Category) {
////            return wrapInPinchableContainer(new InlineInsertNewCategoryContainer(MyForm.this, (Category) refElement, insertBeforeRefElement));
//            insertContainer = new PinchInsertCategoryContainer(myForm, (Category) refElement, (CategoryList) ownerList, insertBeforeRefElement);
//        } else if (refElement instanceof ItemList || typeElement instanceof ItemList) {
////            return wrapInPinchableContainer(new InlineInsertNewItemListContainer(MyForm.this, (ItemList) refElement, insertBeforeRefElement));
//            insertContainer = new PinchInsertItemListContainer(myForm, (ItemList) refElement, (ItemListList) ownerList, insertBeforeRefElement);
//        } else if (refElement instanceof WorkSlot || typeElement instanceof WorkSlot) {
////            WorkSlotList workSlotList = ((WorkSlot) refElement).getWorkSlotListN();
////            insertContainer = new InlineInsertNewWorkSlotContainer(MyForm.this, (WorkSlot) refElement, workSlotList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
//            insertContainer = new PinchInsertWorkSlotContainer(myForm, (WorkSlot) refElement, ownerList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
//        } else {
//            ASSERT.that(false, () -> "error2 in createInsertContainer: refElt=" + refElement + "; list=" + ownerList + "; insertBefore=" + insertBeforeRefElement);
//        }
////        return null;
//        return insertContainer;
//    }
//</editor-fold>
//    private static PinchInsertContainer createInsertContainer(MyForm myForm, String typeElement, String refGuid,
//            ItemAndListCommonInterface ownerList, Category category, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
    private static PinchInsertContainer createInsertContainer(MyForm myForm, String typeElement, String refGuid, Category categoryN, boolean insertBeforeRefElementXXX) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        return createInsertContainer(refElement, list, insertBeforeRefElement, null);
//    }
//
//    private Container createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface list, boolean insertBeforeRefElement, Action closeAction) {
//        ASSERT.that(!insertBeforeRefElement, "not implemented yet");
//        InsertNewElementFunc insertContainer = null;
//</editor-fold>
        PinchInsertContainer insertContainer = null;
        if (typeElement.equals(Item.CLASS_NAME)) {
            ItemAndListCommonInterface itemListOrItemOrg;
            if (myForm instanceof ScreenListOfItems) {
                itemListOrItemOrg = ((ScreenListOfItems) myForm).itemListOrItemOrg;
                if (itemListOrItemOrg instanceof Category) { //if screen shows category, replace by Inbox as default ownerList for new items
                    itemListOrItemOrg = Inbox.getInstance();
                }
            } else {
                ASSERT.that("should never happen?!");
                itemListOrItemOrg = Inbox.getInstance();
            }
            insertContainer = new PinchInsertItemContainer(myForm, refGuid, itemListOrItemOrg, categoryN, insertBeforeRefElementXXX);
        } else if (typeElement.equals(Category.CLASS_NAME)) {
//            return wrapInPinchableContainer(new InlineInsertNewCategoryContainer(MyForm.this, (Category) refElement, insertBeforeRefElement));
            insertContainer = new PinchInsertCategoryContainer(myForm, DAO.getInstance().fetchCategory(refGuid), insertBeforeRefElementXXX);
        } else if (typeElement.equals(ItemList.CLASS_NAME)) {
//            return wrapInPinchableContainer(new InlineInsertNewItemListContainer(MyForm.this, (ItemList) refElement, insertBeforeRefElement));
//                insertContainer = new PinchInsertItemListContainer(myForm, (ItemList) refGuid, (ItemListList) ownerList, insertBeforeRefElement);
            insertContainer = new PinchInsertItemListContainer(myForm, DAO.getInstance().fetchItemList(refGuid), insertBeforeRefElementXXX);
        } else if (typeElement.equals(WorkSlot.CLASS_NAME)) {
//            WorkSlotList workSlotList = ((WorkSlot) refElement).getWorkSlotListN();
//            insertContainer = new InlineInsertNewWorkSlotContainer(MyForm.this, (WorkSlot) refElement, workSlotList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
//                insertContainer = new PinchInsertWorkSlotContainer(myForm, (WorkSlot) refGuid, ownerList, insertBeforeRefElement); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
            insertContainer = new PinchInsertWorkSlotContainer(myForm, DAO.getInstance().fetchWorkSlot(refGuid), insertBeforeRefElementXXX); //TODO!!!!! implement pinch insert of new WorkSlots, require adapting InlineContainer!
        } else {
            ASSERT.that(false, () -> "error2 in createInsertContainer: refElt=" + refGuid + "; insertBefore=" + insertBeforeRefElementXXX);
        }
//        return null;
        return insertContainer;
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private InsertNewElementFunc createInsertContainer(ItemAndListCommonInterface refElement, ItemAndListCommonInterface ownerList,
    //    private static PinchInsertContainer createInsertContainer(MyForm myForm, ItemAndListCommonInterface refElement, ItemAndListCommonInterface ownerList,
    //            Category category, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
    //        return createInsertContainer(myForm, null, refElement, ownerList, category, insertBeforeRefElement);
    //    }

    /**
     * returns true if x is an insertNewContainer or is inside one
     *
     * @param x the component to start the search for insertNewContainer from
     * @return
     */
    //    private boolean isInsertNewContainer(Component comp) {
    //        return (comp instanceof PinchInsertItemContainer
    //                || comp instanceof PinchInsertCategoryContainer
    //                || comp instanceof PinchInsertItemListContainer
    //                || comp instanceof PinchInsertWorkSlotContainer);
    //    }
    //    private boolean isOrPartOfInsertNewContainer(Component x) {
    //        if (isInsertNewContainer(x)) {
    //            return true;
    //        }
    //        Container parent = x.getParent();
    //        while (parent != null) {
    //            if (isInsertNewContainer(x)) {
    //                return true;
    //            }
    //            parent = parent.getParent();
    //        }
    //        return false;
    //    }
    //    protected static void createAndAddPinchInsertContainerXXX(MyForm myForm, MyDragAndDropSwipeableContainer refComponentN, ItemAndListCommonInterface itemElt,
    //            boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
    ////        if (false && (itemElt == null || refComponentN == null)) { //both can be null when inserting first element into a list
    //////            return null;
    ////            pinchContainer = null;
    ////            return;
    ////        }
    ////        MyDragAndDropSwipeableContainer refComponent = null;xx;
    ////        Category category = refComponentN.getDragAndDropCategory();
    ////        InsertNewElementFunc insertContainer = createInsertContainer(itemElt, itemElt.getOwner(), category, insertBeforeRefElement, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
    ////        if (pinchContainer == null) { //NO, done in InlineInsertCont when closing the previous container (ensure latest scroll pos is used)// store scrollPos on first insert
    ////            setKeepPos();
    ////        }
    ////        InsertNewElementFunc insertContainer = createInsertContainer(itemElt, itemElt.getOwner(), refComponentN.getDragAndDropCategory(), insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
    //        PinchInsertContainer pinchInsertContainer = createInsertContainer(myForm, null, itemElt, itemElt.getOwner(), refComponentN.getDragAndDropCategory(), insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
    ////        Container wrappedInsertContainer = wrapInPinchableContainer(pinchInsertContainer);
    ////        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(refComponentN, wrappedInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
    //        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(refComponentN, pinchInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
    ////        setPinchInsertContainer(insertContainer, false); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
    //        myForm.setPinchInsertContainer(pinchInsertContainer); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
    ////        return wrappedInsertContainer;
    ////        pinchContainer = wrappedInsertContainer;
    ////        pinchContainer = pinchInsertContainer;
    //    }
    //</editor-fold>
    /**
     * insert the dropPlaceholder into the parent container of refComp at the
     * right relative position wrt refComp
     *
     * @param refCompN the component relative to which the dropPlaceholder
     * should be inserted (dropPh is inserted either before or after refComp)
     * @param contParent the parent which is either a ContY or has a ContY as
     * parent
     * @param dropPh dropPlaceholder component
     * @param relativeIndex relative position where to insert dropPh, +1: after
     * refComp, 0: at refComp's position, Integer.MAX_VALUE: at the end of the
     * container list to which refComp belongs, -1: before refComp (not used?!)
     */
    //    private static boolean addDropPlaceholderToAppropriateParentCont(Component refComp, Component dropPh, int relativeIndex) {
    //     static boolean addDropPlaceholderToAppropriateParentCont(MyDragAndDropSwipeableContainer refComp, Component dropPh, int relativeIndex) {
    static boolean addDropPlaceholderToContYParent(Component refCompN, Container contParent, Component dropPh, int relativeIndex) {

//        ASSERT.that(!(refComp instanceof MyTree2) && !(refComp instanceof ContainerScrollY));
//        Container dropCont =getParentScrollYContainer(refComp); //NOT possible to use getParentScrollYContainer because we need the refCompComp below to find the index
//        Container dropContParent = refCompN != null ? refCompN.getParent() : contParent; //treeList = the list in which to insert the dropPlaceholder
//        Container dropContParent = contParent != null ? contParent : refCompN.getParent(); //treeList = the list in which to insert the dropPlaceholder
        if (refCompN == null) {
            ASSERT.that(contParent != null, "contParent should never be null here, refCompN=" + refCompN);
            ContainerScrollY contScrollY = ((MyForm) contParent.getComponentForm()).findScrollableContYChild(contParent);
            contScrollY.add(dropPh);
            return true;
        } else {
            Container dropContParent = contParent; //treeList = the list in which to insert the dropPlaceholder
            if (Config.TEST) {
                ASSERT.that(dropContParent != null, "parent to refComp=" + refCompN + " is null!");
            }
            Component refCompComp = refCompN; //the containing container of refComp contained in dropCont
            while (!(dropContParent instanceof ContainerScrollY) && dropContParent != null) {
                refCompComp = dropContParent;
                dropContParent = dropContParent.getParent();
            }

            ASSERT.that(dropContParent == null || (dropContParent instanceof ContainerScrollY), "dropCont not correct type, dropCont=" + (dropContParent != null ? dropContParent.toString() : "<null>"));
            if (dropContParent != null) {
//            return (Container) dropCont;
//            ( (Container) dropCont).addComponent(dropCont.getC, refComp);
                if (relativeIndex == Integer.MAX_VALUE || refCompComp == null) { //null on first inset into empty list
//                ((Container) dropCont).addComponent(((Container) dropCont).getComponentCount(), dropPh);
                    dropContParent.addComponent(dropPh);
                } else {
                    int index = dropContParent.getComponentIndex(refCompComp);
                    dropContParent.addComponent(index + relativeIndex, dropPh);
                }
                return true;
            } else {
                assert false;
                return false;
            }
        }
    }

    /**
     * used in MyDragAndDropSwipeableContainer to insert DropPlaceholder in
     * right place
     *
     * @param refCompN
     * @param dropPh
     * @param relativeIndex
     * @return
     */
    static boolean addDropPlaceholderToContYParent(Component refCompN, Component dropPh, int relativeIndex) {
        return PinchInsertContainer.addDropPlaceholderToContYParent(refCompN, refCompN.getParent(), dropPh, relativeIndex);
    }

    boolean moveDropPlaceholderToAppropriateParentCont(String refEltGuid, Component dropPh) {
        MyDragAndDropSwipeableContainer refCompN = MyForm.findMyDDContWithGuidN(getComponentForm().getContentPane().getChildrenAsList(true), refEltGuid);
        ContainerScrollY contParent = MyDragAndDropSwipeableContainer.findParentScrollYContainerN(refCompN);
        dropPh.remove(); //first remove from old place
        boolean result = addDropPlaceholderToContYParent(refCompN, dropPh, 1);
        getComponentForm().revalidateLater(); //must refresh after moving the pinchcontainer
//        if(false)contParent.animateLayout(MyForm.ANIMATION_TIME_FAST); //this works in MyTree2 expand, OK here? //xxxxx
//        contParent.animateLayout(MyForm.ANIMATION_TIME_FAST); //this works in MyTree2 expand, OK here? //xxxxx
//        contParent.animateLayout(MyForm.ANIMATION_TIME_FAST);
        return result;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static void addPinchInsertContainerXXX(MyForm myForm, PinchInsertContainer pinchInsertContainer, MyDragAndDropSwipeableContainer refComponentN, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
//        addDropPlaceholderToContYParent(refComponentN, pinchInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
//        myForm.setPinchInsertContainer(pinchInsertContainer); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
//    }
//</editor-fold>
    protected static PinchInsertContainer createAndAddPinchInsertContainer(MyForm myForm, String itemElt, String type,
            MyDragAndDropSwipeableContainer refComponentN, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
        return createAndAddPinchInsertContainer(myForm, itemElt, type, refComponentN, refComponentN.getParent(), insertBeforeRefElement);
    }

    protected static PinchInsertContainer createAndAddPinchInsertContainer(MyForm myForm, String itemElt, String type,
            MyDragAndDropSwipeableContainer refComponentN, Container contParent, boolean insertBeforeRefElement) {//, boolean insertAsSubtask) {
        PinchInsertContainer pinchInsertContainer = createInsertContainer(myForm, type, itemElt, refComponentN != null ? refComponentN.getDragAndDropCategory() : null, insertBeforeRefElement);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//        PinchInsertContainer.addDropPlaceholderToContYParent(refComponentN, myForm.mainContentContainer, pinchInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
//        PinchInsertContainer.addDropPlaceholderToContYParent(refComponentN, pinchInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
        boolean added = PinchInsertContainer.addDropPlaceholderToContYParent(refComponentN, contParent, pinchInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
        ASSERT.that(pinchInsertContainer.getParent() != null, "pinchInsertContainer not correctly added, parent==null");
        pinchInsertContainer.getComponentForm().scrollComponentToVisible(pinchInsertContainer); //UI: 
//        pinchInsertContainer.getTextArea().startEditingAsync();
        pinchInsertContainer.startEditingAsync();
//        myForm.setPinchInsertContainer(pinchInsertContainer); //NOW done in pinchInsertFinished // call this *after* inserting the new container to ensure that text field starts in editing mode
        return pinchInsertContainer;
    }

    /**
     * this container is replaced by another one, close it (go quietly)
     */
    public void discardPinchContainer() {
        //UI: close the text field
        Container parent = MyDragAndDropSwipeableContainer.removeFromParentScrollYAndReturnParentN(this);
        if (parent == null) {
            this.remove(); //remove this in case it wasn't in a ContY (like when it's the first inserted in an empty list)
        }
    }

    @Override
    public void startEditingAsync() {
        if (MyPrefs.pinchInsertActivateEditing.getBoolean() && getTextArea() != null) {
            getTextArea().startEditingAsync();
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private static void createAndAddPinchInsertIntoEmptyContainerXXX(MyForm myForm, Container insertIntoContainer, ItemAndListCommonInterface typeElement,
//            ItemAndListCommonInterface owner, Category category) {
////        InsertNewElementFunc insertContainer = createInsertContainer(typeElement, null, owner, category, false);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
//        PinchInsertContainer insertContainer = createInsertContainer(myForm, typeElement, null, owner, category, false);//, insertAsSubtask); //if Item: can only be list of items (not in list of category or itemList), if ItemList/Category: owner
////        Container wrappedInsertContainer = wrapInPinchableContainer(insertContainer);
////        MyDragAndDropSwipeableContainer.addDropPlaceholderToAppropriateParentCont(refComponentN, wrappedInsertContainer, insertBeforeRefElement ? 0 : 1); //insert insertContainer at position of dropComponentBelow
//        insertIntoContainer.removeAll(); //remove any previous content (eg label with "Insert new task with +...")
////        insertIntoContainer.addComponent(wrappedInsertContainer);
//        insertIntoContainer.addComponent(insertContainer);
////        setPinchInsertContainer(insertContainer, true); //call this *after* inserting the new container to ensure that text field starts in editing mode
//        myForm.setPinchInsertContainer(insertContainer); //call this *after* inserting the new container to ensure that text field starts in editing mode
////        pinchContainer = wrappedInsertContainer;
////        pinchContainer = insertContainer;
//    }
//</editor-fold>
}
