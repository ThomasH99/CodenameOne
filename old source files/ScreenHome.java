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
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import static com.todocatalyst.todocatalyst.MyForm.SCREEN_OVERDUE_TITLE;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.HashSet;
import java.util.List;
import static com.todocatalyst.todocatalyst.ScreenListOfItems.buildItemContainer;
import java.util.HashMap;

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
public class ScreenHome extends MyForm { //TODO!!!! remove this unused class
    //TODO persist the expanded state (at least locally on device)

    private static String screenTitle = "TodoCatalyst";
    private ItemList itemListList;
    private boolean draggableMode = false;
    Command sortOnOff = null;
    Command draggableOnOff = null;

    /**
     * edit a list of categories
     *
     * @param title
     * @param category
     * @param previousForm
     * @param category
     */
//    ScreenHome(ItemList itemListList, MyForm previousForm, GetItemList updateItemListOnDone) {
//        this(itemListList.getText(), itemListList, previousForm, updateItemListOnDone);
//    }

    ScreenHome() { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        //TODO enable drag&drop to reorder list
        //TODO enable show/hide sublists
        //TODO create multiple instantions of home screen to have different views, eg work, home-inside, home-outside
        super("TodoCatalyst", null, null);
        this.itemListList = itemListList;
        
        
        HashMap<ItemList, ActionListener> launchScreen = new HashMap(); //actions to launch edit Event ('>') on specific list
        ItemList homeItemList = new ItemList(); //list of lists to show in home screen

        //OVERDUE
        FilterSortDef filterSortOverdue = new FilterSortDef(Item.PARSE_DUE_DATE,
                FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS + FilterSortDef.FILTER_SHOW_WAITING_TASKS, true); //FilterSortDef.FILTER_SHOW_DONE_TASKS
        ItemList overdue = new ItemList(SCREEN_OVERDUE_TITLE, DAO.getInstance().getOverdue(), filterSortOverdue, true);
        homeItemList.add(overdue);
        launchScreen.put(overdue, (e) -> {
            new ScreenListOfItems(SCREEN_OVERDUE_TITLE, overdue, this, (i) -> {
            },
                    ScreenListOfItems.OPTION_NO_EDIT_LIST_PROPERTIES | ScreenListOfItems.OPTION_NO_MODIFIABLE_FILTER | ScreenListOfItems.OPTION_NO_WORK_TIME
            ).show();
        });

        setScrollable(false);
        setLayout(new BorderLayout());
        expandedObjects = new HashSet();
        addCommandsToToolbar(getToolbar());
        getToolbar().addSearchCommand((e) -> {
            String text = (String) e.getSource();
            Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            boolean showAll = text == null || text.length() == 0;
            for (int i = 0, size = this.itemListList.size(); i < size; i++) {
                //TODO!!! compare same case (upper/lower)
                //https://www.codenameone.com/blog/toolbar-search-mode.html:
                compList.getComponentAt(i).setHidden(((ItemList) itemListList.get(i)).getText().toLowerCase().indexOf(text) < 0);
            }
            compList.animateLayout(150);
        });

//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForItemList(this.itemListList));
        refreshAfterEdit();
    }

    protected void animateMyForm() {
        ((Container) ((BorderLayout) getContentPane().getLayout()).getCenter()).animateLayout(150);
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
        getContentPane().add(BorderLayout.CENTER, buildContentPaneForItemList(itemListList));
        revalidate();
//        if (this.keepPos != null) {
//            this.keepPos.setNewScrollYPosition();
//        }
        restoreKeepPos();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //NEW ITEMLIST
        toolbar.addCommandToRightBar(new MyReplayCommand("ListProperties","", Icons.iconNewToolbarStyle) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                ItemList itemList = new ItemList();
                new ScreenItemListProperties(itemList, ScreenHome.this, () -> {
//                    if (itemList.getText().length() > 0||itemList.getComment().length() > 0) {
                    if (itemList.hasSaveableData()) {
//                    itemList.setOwner(itemListList); //NB cannot set an owner which is not saved in parse
//                    DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    itemListList.addItemAtIndex(itemList, 0);
                        itemListList.addToList(0, itemList);
                        DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                        DAO.getInstance().save(itemListList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    previousForm.revalidate(); //refresh list to show new items(??)
//                    previousForm.refreshAfterEdit();//refresh list to show new items(??)
                        refreshAfterEdit();//refresh list to show new items(??)
                    }
                }).show();
            }
        });

        //MOVE mode
        toolbar.addCommandToOverflowMenu(draggableOnOff = new Command("Move ON", Icons.iconMoveUpDownToolbarStyle) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                draggableMode = !draggableMode;
                draggableOnOff.setCommandName(draggableMode ? "Move OFF" : "Move ON");
            }
        });

        //BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
//                new Command("", iconDone) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                updateItemListOnDone.update(categoryList); //should never be null
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
//            }
//        });

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
    protected static Container buildItemListContainer(ItemList itemList, KeepInSameScreenPosition keepPos, boolean statisticsMode) {
        Container mainCont = new Container(new BorderLayout());
        mainCont.setUIID("ItemListContainer");
        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));

//        MyDropContainer swipCont = new MyDropContainer(itemList, itemListList, null, bottomLeft, null, cont, () -> {return draggableMode;}); //use filtered/sorted ItemList for Timer
        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, null, mainCont) {
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
////                            refreshAfterDrop(); //TODO need to refresh for a drop which doesn't change anything??
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
            @Override
            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//                return !(draggedObject.getDragAndDropObject() instanceof CategoryList) && draggedObject.getDragAndDropObject() instanceof ItemList
                return draggedObject.getDragAndDropObject() instanceof ItemList || draggedObject.getDragAndDropObject() instanceof Item;
            }

            @Override
            public ItemAndListCommonInterface getDragAndDropList() {
//                return ((ItemList) getDragAndDropObject()).getOwnerList().getList(); //returns the owner of 
                return itemList.getOwner(); //returns the owner of 
            }

            @Override
            public List getDragAndDropSubList() {
//                return getDragAndDropList(); //returns the list of subtasks
                return itemList.getList();
            }

            @Override
            public Object getDragAndDropObject() {
                return itemList;
            }

            @Override
            public void saveDragged() {
                DAO.getInstance().save(itemList);
            }

        }; //use filtered/sorted ItemList for Timer

        if (keepPos != null) {
            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
        }

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
//        Button itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText()+(itemList.getWorkSlotList()!=null?"#":""), swipCont, () -> true); //D&D
        MyButtonInitiateDragAndDrop itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText(), swipCont, () -> true); //D&D

        mainCont.addComponent(BorderLayout.CENTER, itemLabel);

        Button editItemListPropertiesButton = null;
        if (!statisticsMode) { //don't edit properties of the (temporary) ItemLists generated to display the results
            editItemListPropertiesButton = new Button();
            //SHOW/EDIT SUBTASKS OF LIST
//        editItemPropertiesButton.setIcon(iconEdit);
            editItemListPropertiesButton.setCommand(new MyReplayCommand("NewItemList","", Icons.iconEditSymbolLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    MyForm f = ((MyForm) mainCont.getComponentForm());
//                DAO.getInstance().fetchAllItemsIn((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                new ScreenListOfItems(itemList.getText(), itemList, ScreenListOfItemLists.this, (iList) -> {
                    new ScreenListOfItems(itemList.getText(), itemList, (MyForm) mainCont.getComponentForm(), (iList) -> {
                        if (true) {
//                            ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                            f.setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
                            itemList.setList(iList.getList());
                            DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, itemListList), null); //update the container with edited content
                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, keepPos), null); //update the container with edited content //TODO!! add animation?
                        } else {

                        }
//                        ((MyForm) mainCont.getComponentForm()).refreshAfterEdit();
                        f.refreshAfterEdit();
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
                    }).show();
                }
            }
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
//        long remaining = itemList.getRemainingEffort();
////        long estimated = itemList.getEstimatedEffort();
//        long workTime = itemList.getWorkSlotList().getWorkTimeSum();
////        east.add(new Label("R"+MyDate.formatTimeDuration(remaining)+"E"+MyDate.formatTimeDuration(estimated)+"W"+MyDate.formatTimeDuration(workTime)));
//        east.add(new Label("R"+MyDate.formatTimeDuration(remaining)+"W"+MyDate.formatTimeDuration(workTime)));
        //EXPAND list
        int numberItems;
        WorkSlotList workSlots = itemList.getWorkSlotList();
        long workTimeSumMillis = workSlots != null ? itemList.getWorkSlotList().getWorkTimeSum() : 0;
        if (true || !statisticsMode) {
            numberItems = statisticsMode ? itemList.getNumberOfItems(false, true) : itemList.getNumberOfUndoneItems(false);
            assert !statisticsMode || numberItems > 0; // the list should only exist in statistics mode if it is not empty
            if (numberItems > 0) {
                Button subTasksButton = new Button();
                Command expandSubTasks = new Command("[" + numberItems + "]");// {
                subTasksButton.setCommand(expandSubTasks);
//            subTasksButton.setIcon(Icons.get().iconShowMoreLabelStyle);
                subTasksButton.setUIID("Label");
//            swipCont.putClientProperty("subTasksButton", subTasksButton);
                swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
                east.addComponent(subTasksButton);
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

        if (!statisticsMode) {
            long remainingEffort = itemList.getRemainingEffort();
//        long workTime = itemList.getWorkSlotList().getWorkTimeSum();
//        if (remainingEffort != 0) {
//            east.addComponent(new Label(MyDate.formatTimeDuration(remainingEffort)));
//        }

//        List<WorkSlot> workslots = itemList.getWorkSlotList();
//        long workTimeSumMillis = WorkSlot.sumWorkSlotList(workslots);
            east.addComponent(new Label((remainingEffort != 0 ? MyDate.formatTimeDuration(remainingEffort) : "")
                    + (workTimeSumMillis != 0 ? ((remainingEffort != 0 ? "/" : "") + MyDate.formatTimeDuration(workTimeSumMillis)) : ""))); //format: "remaining/workTime"
            east.addComponent(editItemListPropertiesButton);
        } else { //statisticsMode
            long actualEffort = itemList.getActualEffort();
//            long estimatedEffort = itemList.getEffortEstimate();
            east.addComponent(new Label("Act:" + MyDate.formatTimeDuration(actualEffort)));
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
                    Container southCont = (Container) ((BorderLayout) mainCont.getLayout()).getSouth();
                    if (southCont == null) {
                        Container southDetailsContainer = new Container(new FlowLayout());
                        southDetailsContainer.setUIID("ItemDetails");
//                        long remainingEffort = itemList.getRemainingEffort();
                        long estimatedEffort = itemList.getEffortEstimate();
                        southDetailsContainer.addComponent(new Label("Estimate:" + MyDate.formatTimeDuration(estimatedEffort)
                                + " Work time" + MyDate.formatTimeDuration(workTimeSumMillis)));
//                        southDetailsContainer.setHidden(!showDetails); //hide details by default
                        mainCont.addComponent(BorderLayout.SOUTH, southDetailsContainer);
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
            itemLabel.addActionListener(detailActionListener); //UI: touch task name to show/hide details

            boolean showDetails = MyPrefs.getBoolean(MyPrefs.statisticsShowDetailsForAllLists); // || (myForm.showDetails != null && myForm.showDetails.contains(itemList)); //hide details by default
            if (showDetails) {
                detailActionListener.actionPerformed(null);
            }
        } //        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
        mainCont.addComponent(BorderLayout.EAST, east);
//        cont.setDraggable(true);
//        cont.setDropTarget(true);

//        return cont;
        if (true) { //TODO CANNOT launch Timer on a list without a filter (or will only use the manual sort order which will be counter-intuitive if the user always uses a certain filter)
            leftSwipeContainer.add(new Button(MyReplayCommand.create("SwipeLaunchTimer",null, Icons.iconNewItemFromTemplate, (e) -> {
//                    Item newTemplateInstantiation = new Item();
//                    item.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
//                    new ScreenItem(newTemplateInstantiation, (MyForm) swipCont.getComponentForm(), () -> {
//                        DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner
//                    }).show();
//                ScreenTimer.getInstance().startTimerOnItemList(itemList, null, (MyForm) swipCont.getComponentForm());
                ScreenTimer.getInstance().startTimerOnItemList(itemList, (MyForm) swipCont.getComponentForm());
            }
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
                Container cmp = ScreenListOfItems.buildItemContainer(ScreenHome.this, (Item) node, itemOrItemList, category);
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
                    cmp = buildItemContainer(ScreenHome.this, (Item) node, null, null);
                } else if (node instanceof ItemList) {
//                      cmp = buildCategoryContainer((Category) node, categoryList, keepPos, ()->refreshAfterEdit());
                    cmp = buildItemListContainer((ItemList) node, keepPos);
                } else {
                    assert false : "should only be Item or ItemList";
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
