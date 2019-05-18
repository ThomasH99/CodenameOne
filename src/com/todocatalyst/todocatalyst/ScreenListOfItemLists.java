/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

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
import com.codename1.ui.layouts.MyBorderLayout;
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
    ScreenListOfItemLists(ItemListList itemListList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone) {
        this(itemListList.getText(), itemListList, previousForm, updateItemListOnDone);
    }

    ScreenListOfItemLists(String title, ItemListList itemListList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        super(title, previousForm, () -> updateItemListOnDone.update(itemListList));
        setUniqueFormId("ScreenListOfItemLists");
        this.itemListList = itemListList;
        setScrollable(false);
        if (!(getLayout() instanceof MyBorderLayout)) {
            setLayout(new MyBorderLayout());
        }
        setPinchInsertEnabled(true);
//        expandedObjects = new HashSet();
        expandedObjects = new ExpandedObjects(getUniqueFormId());
        addCommandsToToolbar(getToolbar());
//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForListOfItems(this.itemListList));
        refreshAfterEdit();
    }

    protected void animateMyForm() {
        ((Container) ((MyBorderLayout) getContentPane().getLayout()).getCenter()).animateLayout(150);
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        if (false) getContentPane().removeAll(); //NOT necessary since getContentPane().add() will remove the previous content. AND it will remove components that are added later...

        Container cont = buildContentPaneForItemList(itemListList);
        getContentPane().add(MyBorderLayout.CENTER, cont);
        if (cont instanceof MyTree2) {
//            setStartEditingAsync(((MyTree2)cont).getInlineInsertField().getTextArea());
            InsertNewElementFunc insertNewElementFunc = ((MyTree2) cont).getInlineInsertField();
            if (insertNewElementFunc != null) {
                setStartEditingAsyncTextArea(insertNewElementFunc.getTextArea());
                setInlineInsertContainer(insertNewElementFunc);
            }
        }

//        revalidate();
//        revalidateWithAnimationSafety();
////        if (this.keepPos != null) {
////            this.keepPos.setNewScrollYPosition();
////        }
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        //MOVE mode
        if (false) { //causes a problem in the animation (out of bounds array
            toolbar.addCommandToOverflowMenu(draggableOnOff = new Command("Move ON", Icons.iconMoveUpDownToolbarStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    draggableMode = !draggableMode;
                    draggableOnOff.setCommandName(draggableMode ? "Move OFF" : "Move ON");
                }
            });
        }

//                new Command("", iconDone) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                updateItemListOnDone.update(categoryList); //should never be null
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
//            }
//        });
//        addCommandsToToolbar(getToolbar());
        //SEARCH
        if (false) getToolbar().addSearchCommand((e) -> {
                String text = (String) e.getSource();
                Container compList = (Container) ((MyBorderLayout) getContentPane().getLayout()).getCenter();
                boolean showAll = text == null || text.length() == 0;
                for (int i = 0, size = this.itemListList.getSize(); i < size; i++) {
                    //TODO!!! compare same case (upper/lower)
                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
                    compList.getComponentAt(i).setHidden(((ItemList) this.itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
                }
                compList.animateLayout(150);
            });
        getToolbar().addSearchCommand(makeSearchFunctionSimple(this.itemListList));

        //NEW TASK
        toolbar.addCommandToRightBar(makeCommandNewItemSaveToInbox()); //put all generic (not specific to current screen) icons on the left

        //NEW ITEMLIST
        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("CreateNewList", "New List", Icons.iconNewToolbarStyle(), (e) -> {
            ItemList itemList = new ItemList();
            setKeepPos(new KeepInSameScreenPosition());
            new ScreenItemListProperties(itemList, ScreenListOfItemLists.this, () -> {
//                    if (itemList.getText().length() > 0||itemList.getComment().length() > 0) {
                if (itemList.hasSaveableData()) {
//                    itemList.setOwner(itemListList); //NB cannot set an owner which is not saved in parse
//                    DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    itemListList.addItemAtIndex(itemList, 0);
//                    itemListList.addToList(0, itemList);
                    itemListList.addToList(itemList, false); //TODO: why always add to start of list?! Make it a setting like elsewhere?
                    DAO.getInstance().saveInBackground((ParseObject) itemList, (ParseObject) itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    DAO.getInstance().saveInBackground((ParseObject)itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    previousForm.revalidate(); //refresh list to show new items(??)
//                    previousForm.refreshAfterEdit();//refresh list to show new items(??)
                    refreshAfterEdit();//refresh list to show new items(??)
                }
            }).show();
        }
        ));

        //INTERRUPT TASK
        toolbar.addCommandToLeftBar(makeInterruptCommand());

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("ListOfItemListsSettings", "Settings", Icons.iconSettings, (e) -> {
            new ScreenSettingsListOfItemLists(ScreenListOfItemLists.this, () -> {
                refreshAfterEdit();
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
    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos) {
        return buildItemListContainer(itemList, keepPos, false);
    }

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
    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode) {
        Container mainCont = new Container(new MyBorderLayout());
        mainCont.setUIID("ItemListContainer");
        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        Container rightSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));

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

        if (Config.TEST) {
            swipCont.setName(itemList.getText());
        }

        if (keepPos != null) {
            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
        }

        Button subTasksButton = new Button();
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
        WorkSlotList wSlots = itemList.getWorkSlotListN(false);
//        MyButtonInitiateDragAndDrop itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText() + (itemList.getWorkSlotListN(false).size() > 0 ? "%" : ""), swipCont, () -> true); //D&D
        MyButtonInitiateDragAndDrop itemListLabel = new MyButtonInitiateDragAndDrop(itemList.getText()
                + (Config.TEST && wSlots != null && wSlots.size() > 0 ? "[W]" : ""),
                //                swipCont, () -> true); //D&D
                swipCont, () -> {
//                    boolean enabled = ((MyForm)get.isDragAndDropEnabled();
                    boolean enabled = ((MyForm) mainCont.getComponentForm()).isDragAndDropEnabled();
                    if (enabled && subTasksButton != null) {
                        Object e = swipCont.getClientProperty(KEY_EXPANDED);
                        if (e != null && e.equals("true")) { //                            subTasksButton.getCommand().actionPerformed(null);
                            subTasksButton.pressed();//simulate pressing the button
                            subTasksButton.released(); //trigger the actionLIstener to collapse
                        }
                    }
                    return enabled;
                }); //D&D
        itemListLabel.setMaterialIcon(' '); //FontImage.MATERIAL_LIST); //UI: ' '==blank icon?! Add white space to allow to customize list icons later
        itemListLabel.setUIID("ListOfItemListsTextCont");
        itemListLabel.setTextUIID("ListOfItemListsText");
        itemListLabel.setIconUIID("ListOfItemListsIcon");

        Button editItemListPropertiesButton = null;
        if (!statisticsMode) { //don't edit properties of the (temporary) ItemLists generated to display the results
            editItemListPropertiesButton = new Button("","IconEdit");
            //SHOW/EDIT SUBTASKS OF LIST
//        editItemPropertiesButton.setIcon(iconEdit);
//            editItemListPropertiesButton.setCommand(MyReplayCommand.create("EditItemList-", itemList.getObjectIdP(), "", Icons.iconEditSymbolLabelStyle, (e) -> {
            editItemListPropertiesButton.setCommand(MyReplayCommand.create("EditItemList-" + itemList.getObjectIdP(), "", Icons.iconEdit, (e) -> {
                MyForm f = ((MyForm) mainCont.getComponentForm());
                f.setKeepPos(new KeepInSameScreenPosition());
//                DAO.getInstance().fetchAllElementsInSublist((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                new ScreenListOfItems(itemList.getText(), itemList, ScreenListOfItemLists.this, (iList) -> {
                new ScreenListOfItems(itemList.getText(), () -> itemList, (MyForm) mainCont.getComponentForm(), (iList) -> {
                    if (true) {
//                            ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                        f.setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                        itemList.setList(iList.getListFull());
                        DAO.getInstance().saveInBackground((ParseObject) itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, itemListList), null); //update the container with edited content
                        swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, keepPos), null); //update the container with edited content //TODO!! add animation?
                    } else {

                    }
//                        ((MyForm) mainCont.getComponentForm()).refreshAfterEdit();
                    if (false) f.refreshAfterEdit();
//<editor-fold defaultstate="collapsed" desc="comment">
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
            )
            );
        }

        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
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
        WorkSlotList workSlots = itemList.getWorkSlotListN();
        long workTimeSumMillis = workSlots != null ? itemList.getWorkSlotListN().getWorkTimeSum() : 0; //optimization: avoid calculating this if setting not activate and not in statisticsMode
        if (MyPrefs.listOfItemListsShowNumberUndoneTasks.getBoolean()) {
            int numberItems;
//            WorkSlotList workSlots = itemList.getWorkSlotListN();
//         workTimeSumMillis = workSlots != null ? itemList.getWorkSlotListN().getWorkTimeSum() : 0;
            if (true || !statisticsMode) {
                numberItems = statisticsMode ? itemList.getNumberOfItems(false, true) : itemList.getNumberOfUndoneItems(false);
                assert !statisticsMode || numberItems > 0; // the list should only exist in statistics mode if it is not empty
                if (true || numberItems > 0) { //UI: show '0' number of subtasks for empty lists
//                Button subTasksButton = new Button();
//                Command expandSubTasks = new CommandTracked("[" + numberItems + "]", "ExpandSubtasks");// {
//                    Command expandSubTasks = new CommandTracked("", "ExpandSubtasks");// {
                    Command expandSubTasks = CommandTracked.create("", null,
                            (e) -> {
                                subTasksButton.setUIID(subTasksButton.getUIID().equals("ListOfItemListsShowItems") ? "ListOfItemListsShowItemsExpanded" : "ListOfItemListsShowItems");
                            },
                            "ListOfItemListsExpandSubtasks");// {
                    subTasksButton.setCommand(expandSubTasks);
                    String subTaskStr = numberItems + "";
                    if (MyPrefs.listOfItemListsShowNumberDoneTasks.getBoolean()) {
                        int totalNbTasks = itemList.getNumberOfItems(false, MyPrefs.listOfItemListsShowTotalNumberOfLeafTasks.getBoolean());
                        if (totalNbTasks != 0)
                            subTaskStr += "/" + totalNbTasks;
                    }
                    subTasksButton.setText(subTaskStr);
//            subTasksButton.setIcon(Icons.get().iconShowMoreLabelStyle);
                    subTasksButton.setUIID("ListOfItemListsShowItems");
//            swipCont.putClientProperty("subTasksButton", subTasksButton);
                    swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
                    if (false)east.addComponent(subTasksButton);
//            cont.setLeadComponent(subTasksButton); //ensure events generated by button arrives at main container??? WORKS! Makes the button receive every action from the container
//<editor-fold defaultstate="collapsed" desc="comment">
//            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]") {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
//                }
//            }
//            );
//            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]")); //the Command doesn't have to do anything since Tree binds a listener to the button
//</editor-fold>
                }
            }
        }

        if (!statisticsMode) {
            if (MyPrefs.listOfItemListsShowRemainingEstimate.getBoolean()) {

                long remainingEffort = itemList.getRemaining();
                long totalEffort = MyPrefs.listOfItemListsShowTotalTime.getBoolean() ? itemList.getEstimate() : 0;
//<editor-fold defaultstate="collapsed" desc="comment">
//        long workTime = itemList.getWorkSlotListN().getWorkTimeSum();
//        if (remainingEffort != 0) {
//            east.addComponent(new Label(MyDate.formatTimeDuration(remainingEffort)));
//        }

//        List<WorkSlot> workslots = itemList.getWorkSlotListN();
//        long workTimeSumMillis = WorkSlot.sumWorkSlotList(workslots);
//</editor-fold>
                String effortStr = (remainingEffort != 0 || totalEffort != 0 ? MyDate.formatDurationStd(remainingEffort) : "")
                        + (totalEffort != 0 ? ("/" + MyDate.formatDurationStd(totalEffort)) : "");
                if (workTimeSumMillis != 0)
                    effortStr += ((!effortStr.isEmpty() ? "/" : "") + "[" + MyDate.formatDurationStd(workTimeSumMillis) + "]");
//                east.addComponent(new Label((remainingEffort != 0 ? MyDate.formatDurationStd(remainingEffort) : "")
//                        + (workTimeSumMillis != 0 ? ((remainingEffort != 0 ? "/" : "") + MyDate.formatDurationStd(workTimeSumMillis)) : ""),"ListOfItemListsRemainingTime")); //format: "remaining/workTime"
                east.addComponent(new Label(effortStr, "ListOfItemListsRemainingTime")); //format: "remaining/workTime"
                east.addComponent(subTasksButton); //format: "remaining/workTime"
                east.addComponent(editItemListPropertiesButton);
            } else {
                east.addComponent(subTasksButton); //format: "remaining/workTime"
                east.addComponent(editItemListPropertiesButton);
            }
        } else { //statisticsMode
            long actualEffort = itemList.getActual();
//            long estimatedEffort = itemList.getEffortEstimate();
            east.addComponent(new Label("Act:" + MyDate.formatDurationStd(actualEffort)));
//                    + "/E" + MyDate.formatTimeDuration(estimatedEffort)
//                    + "/W" + MyDate.formatTimeDuration(workTimeSumMillis)));
        }
        
        //DETAILS
        if (statisticsMode) {
//        Container southDetailsContainer=null;
            ActionListener detailActionListener = new ActionListener() { //UI: touch task name to show/hide details
//            itemLabel.addActionListener(new ActionListener() { //UI: touch task name to show/hide details
                @Override
                public void actionPerformed(ActionEvent evt) {
                    Container southCont = (Container) ((MyBorderLayout) mainCont.getLayout()).getSouth();
                    if (southCont == null) {
                        //lazy create of details container
                        Container southDetailsContainer = new Container(new FlowLayout());
                        southDetailsContainer.setUIID("ItemDetails");
//                        long remainingEffort = itemList.getRemainingEffort();
                        long estimatedEffort = itemList.getEstimate();
                        southDetailsContainer.addComponent(new Label("Estimate:" + MyDate.formatDurationStd(estimatedEffort)
                                + " Work time" + MyDate.formatDurationStd(workTimeSumMillis)));
//                        southDetailsContainer.setHidden(!showDetails); //hide details by default
                        mainCont.addComponent(MyBorderLayout.SOUTH, southDetailsContainer);
                        southCont = southDetailsContainer; //update for use below
                    } else {
//                southDetailsContainer.setHidden(!southDetailsContainer.isHidden()); //toggle hidden details
//                        southCont.setHidden(!southDetailsContainer.isHidden()); //toggle hidden details
                        southCont.setHidden(!southCont.isHidden()); //toggle hidden details
                    }
                    MyForm myForm = (MyForm) mainCont.getComponentForm();
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
                        mainCont.getParent().animateLayout(300); //not working well (
                    }
                }
            };
            itemListLabel.addActionListener(detailActionListener); //UI: touch task name to show/hide details

            boolean showDetails = MyPrefs.getBoolean(MyPrefs.statisticsShowDetailsForAllLists); // || (myForm.showDetails != null && myForm.showDetails.contains(itemList)); //hide details by default
            if (showDetails) {
                detailActionListener.actionPerformed(null);
            }
        } //        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));

        mainCont.addComponent(MyBorderLayout.CENTER, itemListLabel);

        mainCont.addComponent(MyBorderLayout.EAST, east);
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.setDraggable(true);
//        cont.setDropTarget(true);

//        return cont;
//</editor-fold>
        if (false) { //DONE CANNOT launch Timer on a list without a filter (or will only use the manual sort order which will be counter-intuitive if the user always uses a certain filter)
//            leftSwipeContainer.add(new Button(MyReplayCommand.create(ScreenTimer2.TIMER_REPLAY+itemList.getObjectIdP(),null, Icons.iconNewItemFromTemplate, (e) -> {
//            leftSwipeContainer.add(new Button(MyReplayCommand.create(TimerStack.TIMER_REPLAY, "SwipeLaunchTimerOnItemList", Icons.iconNewItemFromTemplate, (e) -> {
            leftSwipeContainer.add(new Button(CommandTracked.create("", Icons.iconNewItemFromTemplateMaterial, (e) -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    Item newTemplateInstantiation = new Item();
//                    item.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
//                    new ScreenItem(newTemplateInstantiation, (MyForm) swipCont.getComponentForm(), () -> {
//                        DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner
//                    }).show();
//                ScreenTimer.getInstance().startTimerOnItemList(itemList, null, (MyForm) swipCont.getComponentForm());
//</editor-fold>
//                ScreenTimer2.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
                TimerStack.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
//            }, () -> !MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
//            }, "InterruptInScreen"+((MyForm) mainCont.getComponentForm()).getUniqueFormId() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
            }, "InterruptInScreenListOfItemLists" //only push this command if we start with BigTimer (do NOT always start with smallTimer)
            )));
        }
        rightSwipeContainer.add(makeTimerSwipeButton(swipCont, itemList, "InterruptInScreenListOfItemLists"));
        return swipCont;
    }

    protected static Container buildItemListContainerStatistics(ItemList itemList, KeepInSameScreenPosition keepPos) {
        Container mainCont = new Container(new MyBorderLayout());
        mainCont.setUIID("ItemListContainer");
        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));

        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, null, mainCont) {
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
            @Override
            public ItemAndListCommonInterface getDragAndDropObject() {
                return itemList;
            }

//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(itemList);
//            }
        }; //use filtered/sorted ItemList for Timer
        if (Config.TEST) swipCont.setName(itemList.getText());

        if (keepPos != null) {
            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
        }

        MyButtonInitiateDragAndDrop itemListLabel = new MyButtonInitiateDragAndDrop(itemList.getText() + (itemList.getWorkSlotListN(false).size() > 0 ? "%" : ""),
                swipCont, () -> true); //D&D

        mainCont.addComponent(MyBorderLayout.CENTER, itemListLabel);

        Button editItemListPropertiesButton = null;
        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        //EXPAND list
        int numberItems;
        WorkSlotList workSlots = itemList.getWorkSlotListN();
        long workTimeSumMillis = workSlots != null ? itemList.getWorkSlotListN().getWorkTimeSum() : 0;
        numberItems = itemList.getNumberOfItems(false, true);
        if (numberItems > 0) {
            Button subTasksButton = new Button();
            Command expandSubTasks = new CommandTracked("[" + numberItems + "]", "ExpandSubtasks");// {
            subTasksButton.setCommand(expandSubTasks);
            subTasksButton.setUIID("Label");
            if (Config.TEST) subTasksButton.setName("subTasksButton-" + itemList.getText());
            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
            east.addComponent(subTasksButton);
        }

        //DETAILS
        ActionListener detailActionListener = new ActionListener() { //UI: touch task name to show/hide details
            @Override
            public void actionPerformed(ActionEvent evt) {
                Container southCont = (Container) ((MyBorderLayout) mainCont.getLayout()).getSouth();
                if (southCont == null) {
                    //lazy create of details container
                    Container southDetailsContainer = new Container(new FlowLayout());
                    southDetailsContainer.setUIID("ItemDetails");
                    long estimatedEffort = itemList.getEstimate();
                    southDetailsContainer.addComponent(new Label("Estimate:" + MyDate.formatDurationStd(estimatedEffort)
                            + " Work time" + MyDate.formatDurationStd(workTimeSumMillis)));
                    mainCont.addComponent(MyBorderLayout.SOUTH, southDetailsContainer);
                    southCont = southDetailsContainer; //update for use below
                } else {
                    southCont.setHidden(!southCont.isHidden()); //toggle hidden details
                }
                MyForm myForm = (MyForm) mainCont.getComponentForm();
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
                    mainCont.getParent().animateLayout(300); //not working well (
                }
            }
        };
        itemListLabel.addActionListener(detailActionListener); //UI: touch task name to show/hide details

        boolean showDetails = MyPrefs.getBoolean(MyPrefs.statisticsShowDetailsForAllLists); // || (myForm.showDetails != null && myForm.showDetails.contains(itemList)); //hide details by default
        if (showDetails) {
            detailActionListener.actionPerformed(null);
        }
        mainCont.addComponent(MyBorderLayout.EAST, east);
        if (true) { //DONE CANNOT launch Timer on a list without a filter (or will only use the manual sort order which will be counter-intuitive if the user always uses a certain filter)
//            leftSwipeContainer.add(new Button(MyReplayCommand.create(ScreenTimer2.TIMER_REPLAY+itemList.getObjectIdP(),null, Icons.iconNewItemFromTemplate, (e) -> {
//            leftSwipeContainer.add(new Button(MyReplayCommand.create(TimerStack.TIMER_REPLAY, null, Icons.iconNewItemFromTemplate, (e) -> {
            leftSwipeContainer.add(new Button(CommandTracked.create("", Icons.iconNewItemFromTemplate, (e) -> {
//                ScreenTimer2.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
                TimerStack.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
//            }, () -> !MyPrefs.timerAlwaysStartWithNewTimerInSmallWindow.getBoolean() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
            }, "InterruptSwipeInScreen" + ((MyForm) mainCont.getComponentForm()).getUniqueFormId() //only push this command if we start with BigTimer (do NOT always start with smallTimer)
            )));
        }

        return swipCont;
    }

    protected Container buildContentPaneForItemList(ItemList listOfItemLists) {
        parseIdMapReset();
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
//        if (listOfItemLists != null && listOfItemLists.getList().size() > 0) {
        MyTree2 cl = new MyTree2(listOfItemLists, expandedObjects) {
            @Override
            protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
                Container cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItemLists.this, (Item) node, itemOrItemList, category);
                setIndent(cmp, depth);
                return cmp;
            }

            @Override
            protected Component createNode(Object node, int depth) {
                Container cmp = null;
                if (node instanceof Item) {
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> {                    },
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(),
//                            false, //selectionMode not allowed for list of itemlists //TODO would some actions make sense on multiple lists at once??
//                            null, null, keepPos, expandedObjects, () -> animateMyForm(), false, false); //TODO!!! store expanded itemLists
                    cmp = buildItemContainer(ScreenListOfItemLists.this, (Item) node, null, null);
                    if (Config.TEST) cmp.setName(((Item) node).getText());
                } else if (node instanceof ItemList) {
//                      cmp = buildCategoryContainer((Category) node, categoryList, keepPos, ()->refreshAfterEdit());
                    cmp = buildItemListContainer((ItemList) node, keepPos);
                    if (Config.TEST) cmp.setName(((ItemList) node).getText());
                } else {
                    assert false : "should only be Item or ItemList: node=" + node;
                }
                setIndent(cmp, depth);
                return cmp;
            }

        };
        return cl;
//        } else {
//            return new InsertNewTaskContainer(null, listOfItemLists);
//        }
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
