/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.ui.Command;
import com.codename1.ui.Toolbar;
import com.codename1.ui.Container;
import com.codename1.ui.Button;
import com.codename1.ui.Label;
import com.codename1.ui.Component;
import com.codename1.ui.Dialog;
import com.codename1.ui.FontImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_EXPANDED;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.HashSet;
import java.util.List;
import static com.todocatalyst.todocatalyst.ScreenListOfItems.buildItemContainer;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 * The list to be edited is passed to this screen, which edits it directly (add
 * new items, delete item, edit items in list). The caller is responsible for
 * saving the updated list. If the list is not saved, created objects (eg tasks,
 * categories, workslots) will not be deleted, leaving dangling tasks, which
 * could be cleaned up or caught in an 'Uncategorized' list.
 *
 * Edit an item with a list of Main screen should contain the following
 * elements: Views - user defined views Jot-list Add new item Categories - see
 * or edit categories People - list of people to assign tasks to Locations -
 * list of locations to assign tasks to Find(?) - or just a menu item in each
 * sublist? Settings Help
 *
 * @author Thomas
 */
public class ScreenListOfItemLists extends MyForm {
    //TODO 
    //TODO ensure same number of tasks are shown in list line "List 1 [5]" as is seen when editing the list (apply same filer)
    //TODO update to same code as ScreenListOfItems
    //TODO!!! show expandSubTAsks button as a button, show pressed when expanded
    //TODO sort lists by: Most recently changed, Number open tasks(?), Amount of effort(?), Alphabetically, 
    //DONE stop saving the list of lists (creating unnamed new lists in Parse)
    //DONE refreshTimersFromParseServer list after adding a new task
    //NONE filter lists by: hide empty lists --> NO, because then you can't add to them
    //DONE show sum of effort of tasks within a list
    //DONE show total number of tasks in a list
    //DONE [>] for lists should show the subtasks (inside ScreenListOfItems for the lists add a menu to edit 'propoerties' of List or Category)
    //DONE error list is not scrollable
    //DONE error: showing too many lists --> pb of saving getAll lists fetched directly
    //DONE error: expand subtasks not working
    //DONE error: in container in list: when description is too long, it hides the name of the list --> not show description
    //DONEerror: exception when saving a new list

    private static String screenTitle = "Lists";
    private ItemListList itemListList;
    private boolean draggableMode = false;
    private Command sortOnOff = null;
    private Command draggableOnOff = null;
//    protected static String FORM_UNIQUE_ID = "ScreenListOfItemLists"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

    /**
     * edit a list of categories
     *
     * @param title
     * @param category
     * @param previousForm
     * @param category
     */
//    ScreenListOfItemLists(String title, Category category, MyForm previousForm) { 
//        this(title==null?screenTitle:title, category, previousForm, 
//                 (cat) -> {
//                            cat.setList(cat.getList());
//                            DAO.getInstance().save(cat);
//                        });
//    }
//    ScreenListOfItemLists(ItemListList itemListList, MyForm previousForm, Runnable updateItemListOnDone) {
//        this(itemListList.getText(), itemListList, previousForm, updateItemListOnDone);
//    }
//    ScreenListOfItemLists(String title, ItemListList itemListList, MyForm previousForm, Runnable updateItemListOnDone, String helpText) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
    ScreenListOfItemLists(ItemListList itemListList, MyForm previousForm, Runnable updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        super(title, previousForm, () -> updateItemListOnDone.update(itemListList));
        super(ScreenType.LISTS, previousForm, updateItemListOnDone);
//        addUpdateActionOnDone(() -> updateItemListOnDone.update2(itemListList));
//        addUpdateActionOnDone(() -> updateItemListOnDone.update2(itemListList));
        setUniqueFormId("ScreenListOfItemLists");
        this.itemListList = itemListList;
//        setScrollable(false);
        if (false && !(getLayout() instanceof BorderLayout)) {
            setLayout(new BorderLayout());
        }
        setPinchInsertEnabled(true);
        this.previousValues = new SaveEditedValuesLocally(getUniqueFormId() + "-" + itemListList.getObjectIdP());

//        expandedObjects = new HashSet();
//        expandedObjects = new ExpandedObjects(getUniqueFormId());
        expandedObjectsInit("");
        addCommandsToToolbar(getToolbar());
//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForListOfItems(this.itemListList));
        refreshAfterEdit();
    }

    @Override
    public boolean isPinchInsertEnabled(ItemAndListCommonInterface refElt, boolean insertBeforeRefElt) {
        boolean ok = refElt instanceof ItemList;
        return ok;
    }

    protected void animateMyForm() {
        if (false) {
            ((Container) ((BorderLayout) getContentPane().getLayout()).getCenter()).animateLayout(ANIMATION_TIME_FAST);
        } else {
            animateLayout(ANIMATION_TIME_FAST);
        }
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommandsNO_EFFECT(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        if (false) {
            getContentPane().removeAll(); //NOT necessary since getContentPane().add() will remove the previous content. AND it will remove components that are added later...
        }
        Container cont = buildContentPaneForItemList(itemListList);
        getContentPane().add(BorderLayout.CENTER, cont);
        if (false&&cont instanceof MyTree2) {
//            setStartEditingAsync(((MyTree2)cont).getInlineInsertField().getTextArea());
//            InsertNewElementFunc insertNewElementFunc = ((MyTree2) cont).getInlineInsertField();
            PinchInsertContainer insertNewElementFunc = ((MyTree2) cont).getInlineInsertField();
//            if (false && insertNewElementFunc != null) {
//                setStartEditingAsyncTextArea(insertNewElementFunc.getTextArea());
//                setPinchInsertContainer(insertNewElementFunc);
//            }
        }

        //check if there was an insertContainer active earlier
//        recreateInlineInsertContainerAndReplayCmdIfNeeded(); //moved to MyForm.refreshAfterEdit()

//        revalidate();
//        revalidateWithAnimationSafety();
////        if (this.keepPos != null) {
////            this.keepPos.setNewScrollYPosition();
////        }
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        super.addCommandsToToolbar(toolbar);
        //BACK
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
        addStandardBackCommand();

        //MOVE mode
        if (false) { //causes a problem in the animation (out of bounds array
//            toolbar.addCommandToOverflowMenu(draggableOnOff = new Command("Move ON", Icons.iconMoveUpDownToolbarStyle) {
            toolbar.addCommandToOverflowMenu(draggableOnOff = new Command("Move ON", Icons.iconMoveUpDown) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    draggableMode = !draggableMode;
                    draggableOnOff.setCommandName(draggableMode ? "Move OFF" : "Move ON");
                }
            });
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                new Command("", iconDone) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                updateItemListOnDone.update(categoryList); //should never be null
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
//            }
//        });
//        addCommandsToToolbar(getToolbar());
//</editor-fold>
        //SEARCH
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            getToolbar().addSearchCommand((e) -> {
//                String text = (String) e.getSource();
//                Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
//                boolean showAll = text == null || text.length() == 0;
//                for (int i = 0, size = this.itemListList.getSize(); i < size; i++) {
//                    //TODO!!! compare same case (upper/lower)
//                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                    compList.getComponentAt(i).setHidden(((ItemList) this.itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
//                }
//                compList.animateLayout(ANIMATION_TIME_FAST);
//            });
//        }
//</editor-fold>
//        getToolbar().addSearchCommand(makeSearchFunctionSimple(this.itemListList),MyPrefs.defaultIconSizeInMM.getFloat());
//        MySearchBar mySearchBar = new MySearchBar(getToolbar(), makeSearchFunctionSimple(this.itemListList));
//        toolbar.addCommandToRightBar(new MySearchCommand(getContentPane(), makeSearchFunctionUpperLowerStickyHeaders(itemListList)));
//        setSearchCmd(new MySearchCommand(this, makeSearchFunctionUpperLowerStickyHeaders(itemListList)));
        setSearchCmd(new MySearchCommand(this, itemListList));
        toolbar.addCommandToRightBar(getSearchCmd());

        //NEW TASK to Inbox
        toolbar.addCommandToOverflowMenu(makeCommandNewItemSaveToInbox());

        //NEW ITEMLIST
//        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("CreateNewList", "New List", Icons.iconNew, (e) -> {
//        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("CreateNewList", "New List", Icons.iconListNew, (e) -> {
        if (false) {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("CreateNewList", "Add List", Icons.iconListNew, (e) -> {
                ItemList itemList = new ItemList();
                setKeepPos(new KeepInSameScreenPosition());
                new ScreenItemListProperties(itemList, ScreenListOfItemLists.this, () -> {
//                    if (itemList.getText().length() > 0||itemList.getComment().length() > 0) {
                    if (itemList.hasSaveableData()) {
//                    itemList.setOwner(itemListList); //NB cannot set an owner which is not saved in parse
//                    DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    itemListList.addItemAtIndex(itemList, 0);
//                    itemListList.addToList(0, itemList);
                        itemListList.addToList(itemList, !MyPrefs.insertNewItemListsInStartOfItemListList.getBoolean()); //TODO: why always add to start of list?! Make it a setting like elsewhere?
//                    DAO.getInstance().saveNew(true,(ParseObject) itemList, (ParseObject) itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    DAO.getInstance().saveNew((ParseObject) itemList, (ParseObject) itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    DAO.getInstance().saveNewTriggerUpdate();
//                    DAO.getInstance().saveToParseNow((ParseObject) itemList, (ParseObject) itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                        DAO.getInstance().saveToParseNow((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    DAO.getInstance().saveInBackground((ParseObject)itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    previousForm.revalidate(); //refresh list to show new items(??)
//                    previousForm.refreshAfterEdit();//refresh list to show new items(??)
//                    if (false) {
//                        refreshAfterEdit();//refresh list to show new items(??)
//                    }
                    }
                }).show();
            }
            ));
        }
        toolbar.addCommandToOverflowMenu(makeCommandNewItemList(itemListList));

        //INTERRUPT TASK
        toolbar.addCommandToOverflowMenu(makeInterruptCommand(true));

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ListOfItemListsSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsListOfItemLists(ScreenListOfItemLists.this, () -> {
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
    }

    @Override
    protected boolean isDragAndDropEnabled() {
        return true; //TODO implement test for when drag and drop is possible (for the moment lists are never sorted)
    }

    /**
     *
     * @param content
     * @return
     */
//    protected Container buildItemListContainer(ItemList itemList, ItemList itemListList) {
//    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos) {
////        return buildItemListContainer(itemList, keepPos, false);
//        return buildItemListContainer(itemList, false, null, null);
//    }
//    }
//    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode, boolean showDetailsByDefault) {
    /**
     * builds a list entry for an ItemList
     *
     * @param itemList list to show (and expand)
     * @param keepPos
     * @param statisticsMode if true, removes edit button and
     * @return
     */
//    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode) {
//        return buildItemListContainer(itemList, keepPos, statisticsMode, null);
//    }
    protected static Container buildItemListContainer(MyForm form, ItemList itemList) {
//        return buildItemListContainer(form,itemList, form.keepPos, false, form.expandedObjects);
        return buildItemListContainer(form, itemList, false);
    }

//    protected static Container buildItemListContainer(MyForm myForm,ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode, ExpandedObjects expandedObjectsXXX) {
    protected static Container buildItemListContainer(final MyForm myForm, final ItemList itemList,  boolean statisticsMode) {
     
//        return buildItemListContainer(itemList, keepPos, statisticsMode, expandedObjects, null);
//    }
//
////    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode, ExpandedObjects expandedObjects, Character materialIcon) {
//    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode, ExpandedObjects expandedObjects) {
        statisticsMode = statisticsMode || (itemList instanceof ItemBucket); //force statistics ode when showing an ItemBucket
        ExpandedObjects expandedObjects = myForm.expandedObjects;
        KeepInSameScreenPosition keepPos = myForm.keepPos;
        Container mainCont = new Container(new BorderLayout());
        mainCont.setName("MainItemListContainer");
        if (itemList instanceof ItemBucket) {
            mainCont.setUIID("StatisticsItemListContainer" + ((ItemBucket) itemList).level);
        } else {
            mainCont.setUIID("ItemListContainer");
        }
        boolean showNumberUndoneTasks;
        boolean showNumberDoneTasks;
        boolean showNumberLeafTasks;
        boolean showRemaining;
        boolean showTotal;
        boolean showWorkTime;
        if (false) {
            showNumberUndoneTasks = false;
            showNumberDoneTasks = false;
            showNumberLeafTasks = false;
            showRemaining = false;
            showTotal = false;
            showWorkTime = false;
        } else if (true) {
            showNumberUndoneTasks = itemList.getShowNumberUndoneTasks();
            showNumberDoneTasks = itemList.getShowNumberDoneTasks();
            showNumberLeafTasks = itemList.getShowNumberLeafTasks();
            showRemaining = itemList.getShowRemaining();
            showTotal = itemList.getShowTotal();
            showWorkTime = itemList.getShowWorkTime();
        } else {
            showNumberUndoneTasks = MyPrefs.listOfItemListsShowNumberUndoneTasks.getBoolean();
            showNumberDoneTasks = MyPrefs.listOfItemListsShowNumberDoneTasks.getBoolean();
            showNumberLeafTasks = MyPrefs.listOfItemListsShowTotalNumberOfLeafTasks.getBoolean();
            showRemaining = MyPrefs.listOfItemListsShowRemainingEstimate.getBoolean();
            showTotal = MyPrefs.listOfItemListsShowTotalTime.getBoolean();
            showWorkTime = MyPrefs.listOfItemListsShowWorkTime.getBoolean();
        }
        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        leftSwipeContainer.setName("ItemListLeftSwipeCont");
        Container rightSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        rightSwipeContainer.setName("ItemListRightSwipeCont");

//        MyDropContainer swipCont = new MyDropContainer(itemList, itemListList, null, bottomLeft, null, cont, () -> {return draggableMode;}); //use filtered/sorted ItemList for Timer
        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, rightSwipeContainer, mainCont) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public void drop(Component dragged, int x, int y) {
//                if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
//                    return;
//                }
//                if (dragged instanceof MyDragAndDropSwipeableContainer) {
//                    Object dropTarget = getDraggedObject();
//                    Object draggedObject = ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject();
//                    List insertList = null;
//                    int index = -1;
//                    if (dropTarget instanceof Category) {
//                        if (draggedObject instanceof Category) {
//                            insertList = getDragAndDropList(); //insert items into the list of categories
//                            index = getDragAndDropList().indexOf(getDraggedObject());
//                        } else if (draggedObject instanceof Item) {
//                            insertList = getDragAndDropSubList(); //insert items into the Category itself
//                            index = 0; //insert in head of list
//                        }
//                    } else if (dropTarget instanceof ItemList) {
//                        if (draggedObject instanceof ItemList) {
//                            insertList = getDragAndDropList(); //insert items into the list of categories
//                            index = getDragAndDropList().indexOf(getDraggedObject());
//                        } else if (draggedObject instanceof Item) {
//                            insertList = getDragAndDropSubList(); //insert items into the Category itself
//                            index = 0; //insert in head of list
//                        }
//                    } else if (dropTarget instanceof Item) {
//                        if (draggedObject instanceof ItemList || draggedObject instanceof Category) {
////                            refreshAfterDrop(); //TODO need to refreshTimersFromParseServer for a drop which doesn't change anything??
//                            return; //UI: dropping an ItemList onto an Item not allowed
//                        } else if (draggedObject instanceof Item) {
//                            if (x < this.getWidth() / 3 * 2) {
//                                insertList = getDragAndDropList(); //insert item as subtask of dropTarget Item
//                                index = getDragAndDropList().indexOf(getDraggedObject());
//                            } else {
//                                insertList = getDragAndDropSubList(); //insert item at the position of the dropTarget Item
//                                index = 0; //insert as first sub task
//                            }
//                        }
//                    }
////                    assert insertList
//                    ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList().remove(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////            DAO.getInstance().save(()((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList());
////                        if (x > this.getWidth() / 3 * 2) {
//////                        getDragAndDropSubList().add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                            insertList.add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        } else {
////                            getDragAndDropList().add(getDragAndDropList().indexOf(getDraggedObject()), ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        }
//                    insertList.add(index, ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
//                    //SAVE both
//                    saveDragged();
//                    ((MyDragAndDropSwipeableContainer) dragged).saveDragged();
//                    dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//                    dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//                    refreshAfterDrop();
//                }
//            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
////                return !(draggedObject.getDragAndDropObject() instanceof CategoryList) && draggedObject.getDragAndDropObject() instanceof ItemList
//                return draggedObject.getDragAndDropObject() instanceof ItemList || draggedObject.getDragAndDropObject() instanceof Item;
//            }

//            @Override
//            public ItemAndListCommonInterface getDragAndDropList() {
////                return ((ItemList) getDragAndDropObject()).getOwnerList().getList(); //returns the owner of
//                return itemList.getOwner(); //returns the owner of
//            }
//            @Override
//            public List getDragAndDropSubList() {
////                return getDragAndDropList(); //returns the list of subtasks
//                return itemList.getList();
//            }
//</editor-fold>
            @Override
            public ItemAndListCommonInterface getDragAndDropObject() {
                return itemList;
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(itemList);
//            }
//</editor-fold>

        }; //use filtered/sorted ItemList for Timer
//        swipCont.putClientProperty(ScreenListOfItems.DISPLAYED_ELEMENT, itemList);
//        swipCont.setUIID("ItemListContainer");

        if (Config.TEST) {
            swipCont.setName("ItemListMyDDSwipCont-" + itemList.getText());
        }

        if (keepPos != null) {
            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
        }

        Button expandItemListSubTasksButton = new Button();
        expandItemListSubTasksButton.setName("ItemListExpandSubtasksButton");

//<editor-fold defaultstate="collapsed" desc="comment">
//EDIT LIST
//        Button editItemButton = new Button();// {
//                Command editItemListCmd = new Command(itemList.getText()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenListOfItems(itemList.getText(), itemList, ScreenListOfItemLists.this,
//                        (iList) -> {
//                            itemList.setList(iList.getList());
//                            DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, itemListList), null); //update the container with edited content
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                                categoryList.addItemAtIndex(category, 0);
////                                DAO.getInstance().save(categoryList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                            itemList.setList(itemList.getList());
////                            DAO.getInstance().save(itemList);
////                        },
////                        (node) -> {
////                            return buildItemListContainer((ItemList) node);
////                        },
////                        (itemList) -> {
////                            ItemList newItemList = new ItemList();
////                            new ScreenItemListProperties(newItemList, ScreenListOfItemLists.this, () -> {
////                                DAO.getInstance().save(newItemList); //save before adding to itemList
////                                itemList.addItem(newItemList);
////                            }).show();
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                        }
//                ).show();
//            }
//        };
//        editItemButton.setCommand(editItemListCmd);
////        editItemButton.putClientProperty("itemList", itemList);
//        editItemButton.setUIID("Label");
//        cont.addComponent(BorderLayout.CENTER, new SpanLabel(itemList.getText()));
//        cont.addComponent(BorderLayout.CENTER, new Label(itemList.getText()));
//</editor-fold>
//        Button itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText()+(itemList.getWorkSlotListN()!=null?"#":""), swipCont, () -> true); //D&D
        WorkSlotList wSlotsN = itemList.getWorkSlotListN();
//        MyButtonInitiateDragAndDrop itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText() + (itemList.getWorkSlotListN(false).size() > 0 ? "%" : ""), swipCont, () -> true); //D&D
        MyButtonInitiateDragAndDrop itemListLabel = new MyButtonInitiateDragAndDrop(itemList.getText()
                + (Config.TEST && wSlotsN != null && wSlotsN.size() > 0 ? "[W]" : ""),
                //                swipCont, () -> true); //D&D
                swipCont, () -> {
//                    boolean enabled = ((MyForm)get.isDragAndDropEnabled();
                    boolean enabled = ((MyForm) mainCont.getComponentForm()).isDragAndDropEnabled();
                    if (enabled && expandItemListSubTasksButton != null) {
                        Object e = swipCont.getClientProperty(KEY_EXPANDED);
                        if (e != null && e.equals("true")) { //                            subTasksButton.getCommand().actionPerformed(null);
                            expandItemListSubTasksButton.pressed();//simulate pressing the button
                            expandItemListSubTasksButton.released(); //trigger the actionLIstener to collapse
                        }
                    }
                    return enabled;
                }); //D&D
//        itemListLabel.setMaterialIcon(' '); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
        if (itemList.getItemListIcon() != null) {
            if (itemList.getItemListIconFont() != null) {
                itemListLabel.setFontIcon(itemList.getItemListIconFont(), itemList.getItemListIcon()); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
            } else {
                itemListLabel.setMaterialIcon(itemList.getItemListIcon()); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
            }
        } else {
            itemListLabel.setMaterialIcon(Icons.iconList); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
        }
        itemListLabel.setUIID("ListOfItemListsTextCont");
        itemListLabel.setTextUIID("ListOfItemListsText");
        itemListLabel.setIconUIID("ListOfItemListsIcon");
        itemListLabel.setName("ItemListsDDCont");

        Button editItemListPropertiesButton = null;
        if (!statisticsMode) { //don't edit properties of the (temporary) ItemLists generated to display the results
            editItemListPropertiesButton = new Button("", "IconEdit");
            editItemListPropertiesButton.setName("ItemListEditButton");
            //SHOW/EDIT SUBTASKS OF LIST
//        editItemPropertiesButton.setIcon(iconEdit);
//            editItemListPropertiesButton.setCommand(MyReplayCommand.create("EditItemList-", itemList.getObjectIdP(), "", Icons.iconEditSymbolLabelStyle, (e) -> {
            editItemListPropertiesButton.setCommand(MyReplayCommand.create("EditItemList-" + itemList.getReplayId(), "", Icons.iconEdit,
                    (ActionEvent e) -> {
                        MyForm f = ((MyForm) mainCont.getComponentForm());
//                        f.setKeepPos(new KeepInSameScreenPosition());
                        f.setKeepPos();
//                DAO.getInstance().fetchAllElementsInSublist((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                new ScreenListOfItems(itemList.getText(), itemList, ScreenListOfItemLists.this, (iList) -> {
                        if (false) {
                            new ScreenListOfItems(itemList.getText(), () -> itemList, (MyForm) mainCont.getComponentForm(),
                                    //                        new ScreenListOfItems( () -> itemList, (MyForm) mainCont.getComponentForm(),
                                    //                        (ItemAndListCommonInterface iList) -> {
                                    () -> {
                                        if (false) {
//                    if (true) {
//                            ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                                            f.setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                                            itemList.setList(itemList.getListFull());
//<editor-fold defaultstate="collapsed" desc="comment">
//                        DAO.getInstance().saveInBackground((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        DAO.getInstance().saveInBackground((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//TODO!!!! how to make below save run in background? (objId is needed eg for EditItemList-ObjId of new list)
//                        DAO.getInstance().saveAndWait((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        DAO.getInstance().saveNew((ParseObject) itemList,true); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        DAO.getInstance().saveNew((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        DAO.getInstance().saveNewTriggerUpdate(); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//</editor-fold>
                                            DAO.getInstance().saveToParseNow((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                                        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, itemListList), null); //update the container with edited content
//                        if (false) {
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, keepPos), null); //update the container with edited content //TODO!! add animation?
//                        }
//                    } else {
//
//                    }
//                        ((MyForm) mainCont.getComponentForm()).refreshAfterEdit();
//                    if (false) {
//                        f.refreshAfterEdit();
//                    }
//                        return true;
//                                categoryList.addItemAtIndex(category, 0);
//                                DAO.getInstance().save(categoryList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            itemList.setList(itemList.getList());
//                            DAO.getInstance().save(itemList);
//                        },
//                        (node) -> {
//                            return buildItemListContainer((ItemList) node);
//                        },
//                        (itemList) -> {
//                            ItemList newItemList = new ItemList();
//                            new ScreenItemListProperties(newItemList, ScreenListOfItemLists.this, () -> {
//                                DAO.getInstance().save(newItemList); //save before adding to itemList
//                                itemList.addItem(newItemList);
//                            }).show();
//</editor-fold>
                                    }, 0).show();
                        }
                        if (false) {
                            if (itemList instanceof CategoryList) {
                                new ScreenListOfCategories((CategoryList) itemList, (MyForm) mainCont.getComponentForm(), null).show();
                            } else if (itemList instanceof ItemListList) {
                                new ScreenListOfItemLists((ItemListList) itemList, (MyForm) mainCont.getComponentForm(), null).show();
                            } else {
                                new ScreenListOfItems(() -> itemList, (MyForm) mainCont.getComponentForm()).show();
                            }
                        }
                        new ScreenListOfItems(() -> itemList, (MyForm) mainCont.getComponentForm()).show();
                    }
            )
            );
        }

//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW)); //NB. NO_GROW to avoid that eg expand sublist [3/5] grows in height
        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS)); //NB. NO_GROW to avoid that eg expand sublist [3/5] grows in height
//        Button subTasksButton = new Button();

        if (false && !itemList.getComment().equals("")) {
//            SpanLabel description = new SpanLabel(" (" + itemList.getComment() + ")");
            Label description = new Label(" (" + itemList.getComment() + ")");
            east.addComponent(description);
        }

        //WORK TIME
//<editor-fold defaultstate="collapsed" desc="comment">
//        long remaining = itemList.getRemainingEffort();
////        long estimated = itemList.getEstimatedEffort();
//        long workTime = itemList.getWorkSlotListN().getWorkTimeSum();
////        east.add(new Label("R"+MyDate.formatTimeDuration(remaining)+"E"+MyDate.formatTimeDuration(estimated)+"W"+MyDate.formatTimeDuration(workTime)));
//        east.add(new Label("R"+MyDate.formatTimeDuration(remaining)+"W"+MyDate.formatTimeDuration(workTime)));
//</editor-fold>
        //EXPAND list
        WorkSlotList workSlotsN = itemList.getWorkSlotListN();
        long workTimeSumMillis = workSlotsN != null ? workSlotsN.getWorkTimeSum() : 0; //optimization: avoid calculating this if setting not activate and not in statisticsMode

        int numberItems = -1;
        if (false && itemList == ItemListList.getInstance()) {
            numberItems = ItemListList.getInstance().size();
        } else if (itemList == TemplateList.getInstance()) {
            numberItems = TemplateList.getInstance().size();
        } else if (showNumberUndoneTasks) {
//            WorkSlotList workSlots = itemList.getWorkSlotListN();
//         workTimeSumMillis = workSlots != null ? itemList.getWorkSlotListN().getWorkTimeSum() : 0;
            if (true || !statisticsMode) {
//                numberItems = statisticsMode ? itemList.getNumberOfItems(false, true) : itemList.getNumberOfUndoneItems(false);
                if (itemList.getShowActual()) {
                    numberItems = itemList.size(); //use size() for TemplateList to get all top-level items
                } else {
                    numberItems = itemList == ItemListList.getInstance() ? ItemListList.getInstance().size() : (statisticsMode
                            ? itemList.getNumberOfItems(false, true)
                            //                                                        : itemList.getNumberOfUndoneItems(MyPrefs.listOfItemListsShowTotalNumberOfLeafTasks.getBoolean()));
//                            : itemList.getNumberOfUndoneItems(itemList.getShowNumberUndoneTasks())); //don't use setting but list value
                            : itemList.getNumberOfUndoneItems(showNumberLeafTasks)); //don't use setting but list value
                }
                ASSERT.that(!statisticsMode || numberItems > 0, "the list should only exist in statistics mode if it is not empty");
////<editor-fold defaultstate="collapsed" desc="comment">
//                if (true || numberItems > 0) { //UI: show '0' number of subtasks for empty lists
////                Button subTasksButton = new Button();
////                Command expandSubTasks = new CommandTracked("[" + numberItems + "]", "ExpandSubtasks");// {
////                    Command expandSubTasks = new CommandTracked("", "ExpandSubtasks");// {
//                    Command expandSubTasksCmd = CommandTracked.create("", null,
//                            (e) -> {
//                                expandItemListSubTasksButton.setUIID(expandItemListSubTasksButton.getUIID().equals("ListOfItemListsShowItemsExpandable")
//                                        ? "ListOfItemListsShowItemsExpanded"
//                                        : "ListOfItemListsShowItemsExpandable");
//                            },
//                            "ListOfItemListsExpandSubtasks");// {
//                    expandItemListSubTasksButton.setCommand(expandSubTasksCmd);
//                    String subTaskStr = numberItems + "";
//                    if (!statisticsMode && showNumberDoneTasks && !itemList.getShowActual()) { //don't show total in statistics since ALL tasks are done
//                        int totalNbTasks = itemList.getNumberOfItems(false, showNumberLeafTasks);
//                        if (totalNbTasks != 0) {
//                            subTaskStr += "/" + totalNbTasks;
//                        }
//                    }
//                    expandItemListSubTasksButton.setText(subTaskStr);
////            subTasksButton.setIcon(Icons.get().iconShowMoreLabelStyle);
//                    expandItemListSubTasksButton.setUIID(expandedObjects != null && expandedObjects.contains(itemList) ? "ListOfItemListsShowItemsExpanded" : "ListOfItemListsShowItemsExpandable");
////            swipCont.putClientProperty("subTasksButton", subTasksButton);
//                    swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, expandItemListSubTasksButton);
//                    if (false) {
//                        east.addComponent(expandItemListSubTasksButton);
//                    }
////            cont.setLeadComponent(subTasksButton); //ensure events generated by button arrives at main container??? WORKS! Makes the button receive every action from the container
////            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]") {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
////                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
////                }
////            }
////            );
////            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]")); //the Command doesn't have to do anything since Tree binds a listener to the button
////</editor-fold>
//                }
            }
        }
        if (numberItems >= 0) {
            Command expandSubTasksCmd = CommandTracked.create("", null,
                    (e) -> {
                        expandItemListSubTasksButton.setUIID(expandItemListSubTasksButton.getUIID().equals("ListOfItemListsShowItemsExpandable")
                                ? "ListOfItemListsShowItemsExpanded"
                                : "ListOfItemListsShowItemsExpandable");
                    },
                    "ListOfItemListsExpandSubtasks");// {
            expandItemListSubTasksButton.setCommand(expandSubTasksCmd);
            String subTaskStr = numberItems + "";
            if (!statisticsMode && showNumberDoneTasks && !itemList.getShowActual()) { //don't show total in statistics since ALL tasks are done
                int totalNbTasks = itemList.getNumberOfItems(false, showNumberLeafTasks);
                if (totalNbTasks != 0) {
                    subTaskStr += "/" + totalNbTasks;
                }
            }
            expandItemListSubTasksButton.setText(subTaskStr);
            expandItemListSubTasksButton.setUIID(expandedObjects != null && expandedObjects.contains(itemList) ? "ListOfItemListsShowItemsExpanded" : "ListOfItemListsShowItemsExpandable");
            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, expandItemListSubTasksButton);
        }

        if (itemList == TemplateList.getInstance()) {
            if (false) { //don't show total of templates, no relevance!
                long estimateTotal = itemList.getEstimateTotal();
                Label estimateTotalLabel = new Label(MyDate.formatDurationStd(estimateTotal));
//            estimateTotalLabel.setFontIcon(Icons.myIconFont, Icons.iconEstimateCust);
                east.addComponent(estimateTotalLabel);
                east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"east.addComponent(editItemListPropertiesButton);
                east.addComponent(editItemListPropertiesButton);
            }
                east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"east.addComponent(editItemListPropertiesButton);
                east.addComponent(editItemListPropertiesButton);
        } else if (!statisticsMode) {
            if (showRemaining) {

                long remainingEffort = itemList.getRemainingTotal();
                long totalEffort = showTotal ? itemList.getEstimateTotal() : 0;
//<editor-fold defaultstate="collapsed" desc="comment">
//        long workTime = itemList.getWorkSlotListN().getWorkTimeSum();
//        if (remainingEffort != 0) {
//            east.addComponent(new Label(MyDate.formatTimeDuration(remainingEffort)));
//        }

//        List<WorkSlot> workslots = itemList.getWorkSlotListN();
//        long workTimeSumMillis = WorkSlot.sumWorkSlotList(workslots);
//</editor-fold>
                String effortStr;
                if (itemList.getShowActual()) {
                    effortStr = MyDate.formatDurationStd(itemList.getActualTotal());
                } else {
                    effortStr = (remainingEffort != 0 || totalEffort != 0 ? MyDate.formatDurationStd(remainingEffort) : "")
                            + (totalEffort != 0 ? ("/" + MyDate.formatDurationStd(totalEffort)) : "");
                    if (workTimeSumMillis != 0 && showWorkTime) {
                        effortStr += ((!effortStr.isEmpty() ? "/" : "") + "[" + MyDate.formatDurationStd(workTimeSumMillis) + "]");
                    }
                }
//                east.addComponent(new Label((remainingEffort != 0 ? MyDate.formatDurationStd(remainingEffort) : "")
//                        + (workTimeSumMillis != 0 ? ((remainingEffort != 0 ? "/" : "") + MyDate.formatDurationStd(workTimeSumMillis)) : ""),"ListOfItemListsRemainingTime")); //format: "remaining/workTime"
                east.addComponent(new Label(effortStr, "ListOfItemListsRemainingTime")); //format: "remaining/workTime"
                east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"
                east.addComponent(editItemListPropertiesButton);
            } else {
                east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"
                east.addComponent(editItemListPropertiesButton);
            }
        } else { //statisticsMode
            long actualTotal = itemList.getActualTotal();
//            long estimatedEffort = itemList.getEffortEstimate();
//            east.addComponent(new Label("Act:" + MyDate.formatDurationStd(actualEffort),Icons.iconActualEffort));
            Label actualTotalLabel = new Label(MyDate.formatDurationStd(actualTotal));
//            actualTotalLabel.setMaterialIcon(Icons.iconActualEffortCust);
            actualTotalLabel.setFontIcon(Icons.myIconFont, Icons.iconActualEffortCust);
            east.addComponent(actualTotalLabel);
//                    + "/E" + MyDate.formatTimeDuration(estimatedEffort)
//                    + "/W" + MyDate.formatTimeDuration(workTimeSumMillis)));
            east.addComponent(expandItemListSubTasksButton); //format: "remaining/workTime"
        }

        //DETAILS
        if (statisticsMode) {
//        Container southDetailsContainer=null;
            ActionListener detailActionListener = (evt) -> {
                Container southCont = (Container) ((BorderLayout) mainCont.getLayout()).getSouth();
                if (southCont == null) {
                    if (itemList instanceof ItemBucket) {
                        ItemBucket itemBucket = (ItemBucket) itemList;
                        //lazy create of details container
                        Container southDetailsContainer = new Container(new FlowLayout(Container.RIGHT));
                        southDetailsContainer.setUIID("StatisticsGroupDetails");
                        southDetailsContainer.setName("southDetailsContainer");

//                        long remainingEffort = itemList.getRemainingEffort();
                        long estimatedEffort = itemList.getEstimate(true);
//                        Label estimateLabel = new Label("Estimate " + MyDate.formatDurationStd(estimatedEffort), "StatisticsDetail");
                        Label estimateLabel = new Label(MyDate.formatDurationStd(estimatedEffort), "StatisticsDetail");
//                        estimateLabel.setMaterialIcon(Icons.iconEstimateMaterial);
                        estimateLabel.setFontIcon(Icons.myIconFont, Icons.iconEstimateCust);
                        estimateLabel.setName("estimateLabel");
//                        if(false) 
                        estimateLabel.setGap(GAP_LABEL_ICON); //setting this gap makes the icon too close to the text - WHY here and not elsewhere?
                        southDetailsContainer.addAll(estimateLabel);

                        long actualEffort = ((ItemBucket) itemList).getActualTotal();
//                        Label estimateLabel = new Label("Estimate " + MyDate.formatDurationStd(estimatedEffort), "StatisticsDetail");
                        Label actualLabel = new Label(MyDate.formatDurationStd(actualEffort), "StatisticsDetail");
//                        estimateLabel.setMaterialIcon(Icons.iconEstimateMaterial);
                        actualLabel.setFontIcon(Icons.myIconFont, Icons.iconActualFinalCust);
                        actualLabel.setName("actualLabel");
//                        if(false) 
                        actualLabel.setGap(GAP_LABEL_ICON); //setting this gap makes the icon too close to the text - WHY here and not elsewhere?
                        southDetailsContainer.addAll(actualLabel);

//                        if (itemBucket.hashValue != null) {
                        if (false && itemBucket.hashValue instanceof ItemAndListCommonInterface) {
                            List<WorkSlot> workslots = ((ItemAndListCommonInterface) itemBucket.hashValue).getWorkSlots(itemBucket.getStartTime(), itemBucket.getEndTime());
                            long workTimeMillis = 0;
                            for (WorkSlot w : workslots) {
                                workTimeMillis += w.getDurationAdjusted(itemBucket.getStartTime().getTime(), itemBucket.getEndTime().getTime());
                            }
//                            Label durationLabel = new Label("Work time " + MyDate.formatDurationStd(workTimeMillis), "StatisticsDetail");
                            Label durationLabel = new Label(MyDate.formatDurationStd(workTimeMillis), "StatisticsDetail");
                            durationLabel.setMaterialIcon(Icons.iconWorkSlot);
                            durationLabel.setName("durationLabel");
                            durationLabel.setGap(GAP_LABEL_ICON);
                            southDetailsContainer.addAll(durationLabel);
                        }

                        Label workTimeLabel = new Label(MyDate.formatDurationStd(WorkSlot.getWorkTimeSum(itemList.getWorkSlotsFromParseN())), "StatisticsDetail");
                        workTimeLabel.setMaterialIcon(Icons.iconWorkSlot);
                        workTimeLabel.setName("durationLabel");
//                        if(false) 
                        workTimeLabel.setGap(GAP_LABEL_ICON_LARGE); //need slightly larger gap since workslot icon is larger
                        southDetailsContainer.addAll(workTimeLabel);
//                        southDetailsContainer.addAll(estimateLabel, durationLabel);
//                        southDetailsContainer.setHidden(!showDetails); //hide details by default
                        mainCont.addComponent(BorderLayout.SOUTH, southDetailsContainer);
                        southCont = southDetailsContainer; //update for use below
                    }
                } else {
//                southDetailsContainer.setHidden(!southDetailsContainer.isHidden()); //toggle hidden details
//                        southCont.setHidden(!southDetailsContainer.isHidden()); //toggle hidden details
                    southCont.setHidden(!southCont.isHidden()); //toggle hidden details
                }
//                MyForm myForm = (MyForm) mainCont.getComponentForm();
                //add/remove itemList to showDetails set
                if (myForm != null) {
                    if (myForm.showDetails != null) {
//                            if (southDetailsContainer.isHidden()) {
                        if (southCont.isHidden()) {
                            myForm.showDetails.remove(itemList);
                        } else {
                            myForm.showDetails.add(itemList);
                        }
                    }
//                        myForm.animateMyForm();
//                        mainCont.getScrollable().ani300); //not working well (
//                    mainCont.getParent().animateLayout(ANIMATION_TIME_DEFAULT); //not working well (
//                    swipCont.getParent().animateLayout(ANIMATION_TIME_DEFAULT); //not working well (
                    swipCont.getComponentForm().animateLayout(ANIMATION_TIME_DEFAULT); //not working well (
                }
            };
            itemListLabel.addActionListener(detailActionListener); //UI: touch task name to show/hide details

            boolean showDetails = MyPrefs.getBoolean(MyPrefs.statisticsShowDetailsForAllLists); // || (myForm.showDetails != null && myForm.showDetails.contains(itemList)); //hide details by default
            if (showDetails) {
                detailActionListener.actionPerformed(null);
            }
        }
//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));

        mainCont.addComponent(BorderLayout.CENTER, itemListLabel);

        mainCont.addComponent(BorderLayout.EAST, BorderLayout.center(east));
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.setDraggable(true);
//        cont.setDropTarget(true);

//        return cont;
//</editor-fold>
//        if (false) { //DONE CANNOT launch Timer on a list without a filter (or will only use the manual sort order which will be counter-intuitive if the user always uses a certain filter)
////            leftSwipeContainer.add(new Button(MyReplayCommand.create(ScreenTimer2.TIMER_REPLAY+itemList.getObjectIdP(),null, Icons.iconNewItemFromTemplate, (e) -> {
////            leftSwipeContainer.add(new Button(MyReplayCommand.create(TimerStack.TIMER_REPLAY, "SwipeLaunchTimerOnItemList", Icons.iconNewItemFromTemplate, (e) -> {
//            leftSwipeContainer.add(new Button(CommandTracked.create("", Icons.iconNewItemFromTemplate, (e) -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    Item newTemplateInstantiation = new Item();
////                    item.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
////                    new ScreenItem(newTemplateInstantiation, (MyForm) swipCont.getComponentForm(), () -> {
////                        DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner
////                    }).show();
////                ScreenTimer.getInstance().startTimerOnItemList(itemList, null, (MyForm) swipCont.getComponentForm());
////</editor-fold>
////                ScreenTimer2.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
//                TimerStack2.getInstance().startTimerAndSave(null, itemList, (MyForm) swipCont.getComponentForm(), false, false);
////            }, () -> !MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
////            }, "InterruptInScreen"+((MyForm) mainCont.getComponentForm()).getUniqueFormId() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
//            }, "InterruptInScreenListOfItemLists" //only push this command if we start with BigTimer (do NOT always start with smallTimer)
//            )));
//        }
        if (!statisticsMode && itemList.getList().size() > 0) {
//            rightSwipeContainer.add(makeTimerSwipeButton(swipCont, itemList, "InterruptInScreenListOfItemLists"));
            rightSwipeContainer.add(makeTimerSwipeButton(swipCont, null, itemList, "SwipeTimerOnListInScreenListOfItemLists"));
        }
        
//           itemList.addActionListener((e)->buildItemListContainer(myForm, itemList, statisticsMode));
        return swipCont;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected static Container buildItemListContainerStatisticsXXX(ItemList itemList, KeepInSameScreenPosition keepPos) {
//        Container mainCont = new Container(new BorderLayout());
////        mainCont.setUIID("ItemListContainer");
//        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//
//        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, null, mainCont) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            @Override
////            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//////                return !(draggedObject.getDragAndDropObject() instanceof CategoryList) && draggedObject.getDragAndDropObject() instanceof ItemList
////                return draggedObject.getDragAndDropObject() instanceof ItemList || draggedObject.getDragAndDropObject() instanceof Item;
////            }
//
////            @Override
////            public ItemAndListCommonInterface getDragAndDropList() {
//////                return ((ItemList) getDragAndDropObject()).getOwnerList().getList(); //returns the owner of
////                return itemList.getOwner(); //returns the owner of
////            }
////            @Override
////            public List getDragAndDropSubList() {
//////                return getDragAndDropList(); //returns the list of subtasks
////                return itemList.getList();
////            }
////</editor-fold>
//            @Override
//            public ItemAndListCommonInterface getDragAndDropObject() {
//                return itemList;
//            }
//
////            @Override
////            public void saveDragged() {
////                DAO.getInstance().save(itemList);
////            }
//        }; //use filtered/sorted ItemList for Timer
//        if (Config.TEST) {
//            swipCont.setName(itemList.getText());
//        }
//
//        if (keepPos != null) {
//            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
//        }
//
//        MyButtonInitiateDragAndDrop itemListLabel = new MyButtonInitiateDragAndDrop(itemList.getText() + (itemList.getWorkSlotListN().size() > 0 ? "%" : ""),
//                swipCont, () -> true); //D&D
//
//        mainCont.addComponent(BorderLayout.CENTER, itemListLabel);
//
//        Button editItemListPropertiesButton = null;
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        //EXPAND list
//        int numberItems;
//        WorkSlotList workSlots = itemList.getWorkSlotListN();
//        long workTimeSumMillis = workSlots != null ? itemList.getWorkSlotListN().getWorkTimeSum() : 0;
//        numberItems = itemList.getNumberOfItems(false, true);
//        if (numberItems > 0) {
//            Button subTasksButton = new Button();
//            Command expandSubTasks = new CommandTracked("[" + numberItems + "]", "ExpandSubtasks");// {
//            subTasksButton.setCommand(expandSubTasks);
//            subTasksButton.setUIID("Label");
//            if (Config.TEST) {
//                subTasksButton.setName("subTasksButton-" + itemList.getText());
//            }
//            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
//            east.addComponent(subTasksButton);
//        }
//
//        //DETAILS
//        ActionListener detailActionListener = new ActionListener() { //UI: touch task name to show/hide details
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Container southCont = (Container) ((BorderLayout) mainCont.getLayout()).getSouth();
//                if (southCont == null) {
//                    //lazy create of details container
//                    Container southDetailsContainer = new Container(new FlowLayout());
//                    southDetailsContainer.setUIID("ItemDetails");
//                    long estimatedEffort = itemList.getEstimate();
//                    southDetailsContainer.addComponent(new Label("Estimate:" + MyDate.formatDurationStd(estimatedEffort)
//                            + " Work time" + MyDate.formatDurationStd(workTimeSumMillis)));
//                    mainCont.addComponent(BorderLayout.SOUTH, southDetailsContainer);
//                    southCont = southDetailsContainer; //update for use below
//                } else {
//                    southCont.setHidden(!southCont.isHidden()); //toggle hidden details
//                }
//                MyForm myForm = (MyForm) mainCont.getComponentForm();
//                //add/remove itemList to showDetails set
//                if (myForm != null) {
//                    if (myForm.showDetails != null) {
////                            if (southDetailsContainer.isHidden()) {
//                        if (southCont.isHidden()) {
//                            myForm.showDetails.remove(itemList);
//                        } else {
//                            myForm.showDetails.add(itemList);
//                        }
//                    }
//                    mainCont.getParent().animateLayout(ANIMATION_TIME_DEFAULT); //not working well (
//                }
//            }
//        };
//        itemListLabel.addActionListener(detailActionListener); //UI: touch task name to show/hide details
//
//        boolean showDetails = MyPrefs.getBoolean(MyPrefs.statisticsShowDetailsForAllLists); // || (myForm.showDetails != null && myForm.showDetails.contains(itemList)); //hide details by default
//        if (showDetails) {
//            detailActionListener.actionPerformed(null);
//        }
//        mainCont.addComponent(BorderLayout.EAST, east);
//        if (true) { //DONE CANNOT launch Timer on a list without a filter (or will only use the manual sort order which will be counter-intuitive if the user always uses a certain filter)
////            leftSwipeContainer.add(new Button(MyReplayCommand.create(ScreenTimer2.TIMER_REPLAY+itemList.getObjectIdP(),null, Icons.iconNewItemFromTemplate, (e) -> {
////            leftSwipeContainer.add(new Button(MyReplayCommand.create(TimerStack.TIMER_REPLAY, null, Icons.iconNewItemFromTemplate, (e) -> {
//            leftSwipeContainer.add(new Button(CommandTracked.create("", Icons.iconNewItemFromTemplate, (e) -> {
////                ScreenTimer2.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
//                TimerStack.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
////            }, () -> !MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
//            }, "InterruptSwipeInScreen" + ((MyForm) mainCont.getComponentForm()).getUniqueFormId() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
//            )));
//        }
//
//        return swipCont;
//    }
//</editor-fold>
    protected Container buildContentPaneForItemList(ItemList listOfItemLists) {
        parseIdMap2.parseIdMapReset();
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyTree dt = new MyTree(itemListList) {
//        Tree dt = new Tree(listOfItemLists) {
//            private int myDepthIndent = 15;
//
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = buildItemListContainer((ItemList) node);
////                cmp.setUIID("TreeNode"); cmp.setTextUIID("TreeNode"); if(model.isLeaf(node)) {cmp.setIcon(nodeImage);} else {cmp.setIcon(folder);}
//                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
//                cmp.getUnselectedStyle().setMargin(LEFT, depth * myDepthIndent);
//                cmp.getPressedStyle().setMargin(LEFT, depth * myDepthIndent);
//                return cmp;
//            }
//
//            @Override
//            protected void bindNodeListener(ActionListener l, Component node) {
//                ((Button) (((Container) node).getClientProperty("subTasksButton"))).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
//            }
//
//            @Override
//            protected void setNodeIcon(Image icon, Component node) {
//                ((Button) (((Container) node).getClientProperty("subTasksButton"))).setIcon(icon); //in a tree of ItemLists there shall always be a subTasksButton
//            }
//        };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        InfiniteContainer cl = new InfiniteContainer(20) {
//            @Override
//            public Component[] fetchComponents(int index, int amount) {
//
//                java.util.List<ItemList> list = listOfItemLists.subList(index, index + Math.min(amount, listOfItemLists.size() - index));
//                if (list.isEmpty()) {
//                    return null;
//                }
//                Component[] comps = new Component[list.size()];
//                for (int i = 0, size = list.size(); i < size; i++) {
////                    ItemList iList = list.get(i);
////                    if (iList.size() > 0) {
//                    comps[i] = new TreeItemList(list.get(i), true); //for lists of lists, we'll always use Tree (since few lists will be empty)
////                    } else {
////                        comps[i] = buildItemListContainer(list.get(i));
////                    }
//                }
//                return comps;
//            }
//        };
//</editor-fold>
        if (listOfItemLists != null && listOfItemLists.getList().size() > 0) {
            MyTree2 cl = new MyTree2(this, listOfItemLists, expandedObjects, null, null) {
//                @Override
//                protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
//                    Container cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItemLists.this, (Item) node, itemOrItemList, category);
//                    setIndent(cmp, depth);
//                    return cmp;
//                }

                @Override
                protected Component createNode(Object node, int depth) {
                    Container cmp = null;
                    if (node instanceof Item) {
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> {                    },
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(),
//                            false, //selectionMode not allowed for list of itemlists //TODO would some actions make sense on multiple lists at once??
//                            null, null, keepPos, expandedObjects, () -> animateMyForm(), false, false); //TODO!!! store expanded itemLists
                        cmp = buildItemContainer(ScreenListOfItemLists.this, (Item) node, null, null);
                        if (Config.TEST) {
                            cmp.setName(((Item) node).getText());
                        }
                    } else if (node instanceof ItemList) {
//                      cmp = buildCategoryContainer((Category) node, categoryList, keepPos, ()->refreshAfterEdit());
//                        cmp = buildItemListContainer((ItemList) node, keepPos, false, expandedObjects);
                        cmp = buildItemListContainer(ScreenListOfItemLists.this, (ItemList) node);
                        if (Config.TEST) {
                            cmp.setName(((ItemList) node).getText());
                        }
                    } else {
                        ASSERT.that(false, "node should only be Item or ItemList: node=" + node);
                    }
                    setIndent(cmp, depth);
                    return cmp;
                }

            };
            return cl;
        } else {
//            return new InsertNewTaskContainer(null, listOfItemLists);
            return BorderLayout.centerCenter(new SpanLabel("Add new lists using +"));
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont.setScrollableY(true);
////        cont.add(dt);
//        cont.add(cl);
//        cont.setDraggable(true);
//        dt.setDropTarget(true);
//        return cont;
//</editor-fold>
    }

}
