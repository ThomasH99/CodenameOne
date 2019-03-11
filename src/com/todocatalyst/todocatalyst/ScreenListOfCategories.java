/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Toolbar;
import com.codename1.ui.Button;
import com.codename1.ui.Label;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_EXPANDED;
import java.util.HashSet;
import java.util.List;
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
public class ScreenListOfCategories extends MyForm {
    //TODO 
    //TODO when creating new tasks in categories, add the category to them
    //TODO implement manual sorting of categories
    //TODO show only number/count of undone items in category (not getSize() as now)
    //DONE add sum of effort of subtasks in each category

    public final static String SCREEN_TITLE = "Categories";
    private CategoryList categoryList;
    private MyTree2 dt;
//    protected static String FORM_UNIQUE_ID = "ScreenListOfCategories"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics

    /**
     * edit a list of categories
     *
     * @param title
     * @param category
     * @param previousForm
     * @param category
     */
//    ScreenListOfCategories(String title, Category category, MyForm previousForm) { 
//        this(title==null?screenTitle:title, category, previousForm, 
//                 (cat) -> {
//                            cat.setList(cat.getList());
//                            DAO.getInstance().save(cat);
//                        });
//    }
    ScreenListOfCategories(CategoryList categoryList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        super(title == null ? SCREEN_TITLE : title, previousForm, () -> updateItemListOnDone.update(categoryList));
        super(SCREEN_TITLE, previousForm, () -> updateItemListOnDone.update(categoryList));
        setUniqueFormId("ScreenListOfCategories");
//        setUpdateItemListOnDone(updateItemListOnDone);
        this.categoryList = categoryList;
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.previousForm = previousForm;
//        this.updateItemListOnDone = updateItemListOnDone;

//        // we initialize the main form and add the favorites command so we can navigate there
//        // we use border layout so the list will take up all the available space
//        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
//        setToolbar(new Toolbar());
//        setTitle(title);
//        addCommandsToToolbar(getToolbar(), theme);
//        setScrollable(false); //disable scrolling of form, necessary to let lists handle their own scrolling
//        getContentPane().setScrollableY(true);
//</editor-fold>
        setScrollable(false);
        if (!(getLayout() instanceof BorderLayout)) {
            setLayout(new BorderLayout());
        }
        setPinchInsertEnabled(true);
        expandedObjects = new ExpandedObjects(getUniqueFormId());
        addCommandsToToolbar(getToolbar());
        if (false) getToolbar().addSearchCommand((e) -> {
                String text = (String) e.getSource();
                Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
                boolean showAll = text == null || text.length() == 0;
                for (int i = 0, size = this.categoryList.getSize(); i < size; i++) {
                    //TODO!!! compare same case (upper/lower)
                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
                    compList.getComponentAt(i).setHidden(((Category) categoryList.get(i)).getText().toLowerCase().indexOf(text) < 0);
                }
//            getContentPane().animateLayout(150);
//            compList.animateLayout(150);
                animateMyForm();
            });
        getToolbar().addSearchCommand(makeSearchFunctionSimple(categoryList));

//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForListOfItems(this.categoryList));
        refreshAfterEdit();
    }

    protected void animateMyForm() {
        ((Container) ((BorderLayout) getContentPane().getLayout()).getCenter()).animateLayout(150);
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
        categoryList.resetWorkTimeDefinition();
        Container cont = buildContentPaneForItemList(categoryList);
        getContentPane().add(BorderLayout.CENTER, cont);
        if (cont instanceof MyTree2) {
//            setStartEditingAsync(((MyTree2)cont).getInlineInsertField().getTextArea());
            InsertNewElementFunc insertNewElementFunc = ((MyTree2) cont).getInlineInsertField();
            if (insertNewElementFunc != null) {
                setStartEditingAsyncTextArea(insertNewElementFunc.getTextArea());
                setInlineInsertContainer(insertNewElementFunc);
            }
        }
//        revalidate();
////        if (this.keepPos != null) {
////            this.keepPos.setNewScrollYPosition();
////        }
//        restoreKeepPos();
        super.refreshAfterEdit();
    }

    static Command makeNewCategoryCmd(CategoryList categoryOwnerList, MyForm previousForm, MyForm.Action refreshOnItemEdits) { //static since reused in other screens
        //NEW CATEGORY
        return MyReplayCommand.create("CreateNewCategory", "", Icons.iconNewToolbarStyle(), (e) -> {
            Category category = new Category();
//                new ScreenCategory(category, ScreenListOfCategories.this, () -> {
            previousForm.setKeepPos(new KeepInSameScreenPosition());
            new ScreenCategory(category, previousForm, () -> {
                if (category.hasSaveableData()) { //UI: do nothing for an empty category, allows user to add category and immediately return if regrests or just pushed wrong button
                    category.setOwner(categoryOwnerList); //TODO should store ordered list of categories
                    DAO.getInstance().save(category); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                    categoryOwnerList.addItemAtIndex(category, 0);
                    DAO.getInstance().save(categoryOwnerList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject //TODO reactivate when implemented storing list of categories
//                        previousForm.revalidate(); //refresh list to show new items(??)
                    refreshOnItemEdits.launchAction();
                }
            }).show();
        }
        );
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //NEW CATEGORY
        toolbar.addCommandToRightBar(makeNewCategoryCmd(categoryList, ScreenListOfCategories.this, () -> refreshAfterEdit()));
//<editor-fold defaultstate="collapsed" desc="comment">
//                new Command("", iconNew) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                Category category = new Category();
//                new ScreenCategory(category, ScreenListOfCategories.this, () -> {
//                    category.setOwner(categoryList); //TODO should store ordered list of categories
//                    DAO.getInstance().save(category); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    categoryList.addItemAtIndex(category, 0);
//                    DAO.getInstance().save(categoryList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject //TODO reactivate when implemented storing list of categories
//                }).show();
//            }
//        });
//</editor-fold>

        //BACK
//        toolbar.addCommandToLeftBar(makeDoneCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle())));
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
//<editor-fold defaultstate="collapsed" desc="comment">
//                new Command("", iconDone) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                updateItemListOnDone.update(categoryList); //should never be null
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
//            }
//        });

//CANCEL - not relevant, all edits are done immediately so not possible to cancel
//</editor-fold>
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
    static Container buildCategoryContainer(Category category, CategoryList categoryList) {
        return buildCategoryContainer(category, categoryList, null);
    }

    static Container buildCategoryContainer(Category category, CategoryList categoryList, KeepInSameScreenPosition keepPos) {
        return buildCategoryContainer(category, categoryList, keepPos, null);
    }

    static Container buildCategoryContainer(Category category, CategoryList categoryList, KeepInSameScreenPosition keepPos, MyForm.Action refreshOnItemEdits) {

        Container mainCont = new Container(new BorderLayout());
        mainCont.setUIID("CategoryContainer");
        if (Config.TEST) mainCont.setName("CatCont-" + category.getText());

        Container swipCont = new MyDragAndDropSwipeableContainer(null, null, mainCont) {

//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//                return draggedObject.getDragAndDropObject() instanceof Category
//                        || draggedObject.getDragAndDropObject() instanceof Item;
//            }
//            @Override
//            public ItemAndListCommonInterface getDragAndDropList() {
//                return categoryList;
//            }
//            @Override
//            public List getDragAndDropSubList() {
////                return category.getList();
////                return null; //should never be used whend dropping onto a Category
//                return category; //should never be used whend dropping onto a Category
//            }
//</editor-fold>
            @Override
            public ItemAndListCommonInterface getDragAndDropObject() {
                return category;
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(categoryList);
//            }
//</editor-fold>

            public Category getDragAndDropCategory() {
                return null;
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//            public Object removeFromOwner() {
//                categoryList.remove(category);
//                return categoryList;
//            }
//
//            public Object insertIntoOwnerAtPositionOf(Movable element) {
//                categoryList.add(categoryList.indexOf(this), element);
//                DAO.getInstance().save(categoryList);
//                return categoryList;
//            }
//
//            public Object insertBelow(Movable element) {
//                return insertIntoOwnerAtPositionOf(element);
//            }
//</editor-fold>
        };
        if (Config.TEST) swipCont.setName("CatSwip-" + category.getText());
//        swipCont.putClientProperty(ScreenListOfItems.DISPLAYED_ELEMENT, category);

        if (keepPos != null) {
            keepPos.testItemToKeepInSameScreenPosition(category, swipCont);
        }

//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public void drop(Component dragged, int x, int y) {
//                if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
//                    return;
//                }
//                if (dragged instanceof MyDragAndDropSwipeableContainer) {
//                    Object dropTarget = getDragAndDropObject();
//                    Object draggedObject = ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject();
//                    List insertList = null;
//                    int index = -1;
//                    if (dropTarget instanceof Category) {
//                        if (draggedObject instanceof Category) {
//                            insertList = getDragAndDropList(); //insert items into the list of categories
//                            index = getDragAndDropList().indexOf(getDragAndDropObject());
//                        } else if (draggedObject instanceof Item) {
//                            insertList = getDragAndDropSubList(); //insert items into the Category itself
//                            index = 0; //insert in head of list
//                        }
//                    } else if (dropTarget instanceof ItemList) {
//                        if (draggedObject instanceof ItemList) {
//                            insertList = getDragAndDropList(); //insert items into the list of categories
//                            index = getDragAndDropList().indexOf(getDragAndDropObject());
//                        } else if (draggedObject instanceof Item) {
//                            insertList = getDragAndDropSubList(); //insert items into the Category itself
//                            index = 0; //insert in head of list
//                        }
//                    } else if (dropTarget instanceof Item) {
//                        if (draggedObject instanceof ItemList || draggedObject instanceof Category) {
////                            refreshAfterDrop(); //TODO need to removeFromCache for a drop which doesn't change anything??
//                            return; //UI: dropping an ItemList onto an Item not allowed
//                        } else if (draggedObject instanceof Item) {
//                            if (x < this.getWidth() / 3 * 2) {
//                                insertList = getDragAndDropList(); //insert item as subtask of dropTarget Item
//                                index = getDragAndDropList().indexOf(getDragAndDropObject());
//                            } else {
//                                insertList = getDragAndDropSubList(); //insert item at the position of the dropTarget Item
//                                index = 0; //insert as first sub task
//                            }
//                        }
//                    }
////                    assert insertList
//                    ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList().remove(((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
////            DAO.getInstance().save(()((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList());
////                        if (x > this.getWidth() / 3 * 2) {
//////                        getDragAndDropSubList().add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                            insertList.add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        } else {
////                            getDragAndDropList().add(getDragAndDropList().indexOf(getDraggedObject()), ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                        }
//                    insertList.add(index, ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropObject());
//                    //SAVE both
//                    saveDragged();
//                    ((MyDragAndDropSwipeableContainer) dragged).saveDragged();
//                    dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
//                    dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
////            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
//                    refreshAfterDrop();
//                }
//            }
//
//        };
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        cont.setLayout(new BorderLayout());
//        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
//EDIT items in category
//        Button editItemButton = new Button(new Command(category.getText()) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenListOfItems(category, ScreenListOfCategories.this,
//                        (itemList) -> {
//                            category.setList(itemList.getList());
//                            DAO.getInstance().save(category);
//                        }
//                ).show();
//            }
//        });
//        editItemButton.setUIID("Label");
//        cont.addComponent(BorderLayout.CENTER, editItemButton);
//        cont.addComponent(BorderLayout.CENTER, new Label(category.getText()));
//</editor-fold>
        Button expandCategorySubTasksButton = new Button();
        WorkSlotList wSlots = category.getWorkSlotListN(false);
//        MyButtonInitiateDragAndDrop categoryLabel = new MyButtonInitiateDragAndDrop(category.getText(), swipCont, () -> true); //D&D
        MyButtonInitiateDragAndDrop categoryLabel = new MyButtonInitiateDragAndDrop(category.getText() + (Config.TEST && wSlots != null && wSlots.size() > 0 ? "[W]" : ""), swipCont, () -> {
            boolean enabled = ((MyForm) mainCont.getComponentForm()).isDragAndDropEnabled();
            if (enabled && expandCategorySubTasksButton != null) {
                Object e = swipCont.getClientProperty(KEY_EXPANDED);
                if (e != null && e.equals("true")) { //                            subTasksButton.getCommand().actionPerformed(null);
                    expandCategorySubTasksButton.pressed();//simulate pressing the button
                    expandCategorySubTasksButton.released(); //trigger the actionLIstener to collapse
                }
            }
            return enabled;
        }); //D&D
        if (Config.TEST) expandCategorySubTasksButton.setName("CatExpand-" + category.getText());
        mainCont.addComponent(BorderLayout.CENTER, categoryLabel);

        Button editItemPropertiesButton = new Button();
        editItemPropertiesButton.setCommand(MyReplayCommand.create("EditCategory-", category.getObjectIdP(), "", Icons.iconEditPropertiesToolbarStyle, (e) -> {
//                new ScreenCategory(category, ScreenListOfCategories.this, 
//                ()-> {}
//                ).show();
            if (false) {
                DAO.getInstance().getAllItemsInCategory(category);
            }
            ASSERT.that(category.isDataAvailable(), "Category \"" + category + "\" data not available");

//                new ScreenListOfItems(category, ScreenListOfCategories.this,
            ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(category, mainCont)); //mainCont right container to use here??
            new ScreenListOfItems(category.getText(), () -> category, (MyForm) swipCont.getComponentForm(), (itemsInCategory) -> {
                ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(category, swipCont));
                if (false) { // I don't think this makes any sense, all edits to items within the category should be updated directly (eg Item.softdelete should remove it from category, edit Item to remove the category should also update/save the category, ...)
                    category.setList(itemsInCategory.getListFull()); //should probably be full, to check if re-activating this code
                    DAO.getInstance().save(category);
                }
//                    refreshAfterEdit();
                refreshOnItemEdits.launchAction(); //refresh when items have been edited
            }, 0).show();
        }
        ));
        if (Config.TEST) editItemPropertiesButton.setName("CatEditItem-" + category.getText());

        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        Container east = new Container(BoxLayout.x());
        mainCont.addComponent(BorderLayout.EAST, east);
//        Button subTasksButton = new Button();
        if (category.getSize() != 0) {
//            east.addComponent(new Label("[" + category.getSize() + "]"));
//            Button subTasksButton = new Button("[" + category.getSize() + "]");
//            Button subTasksButton = new Button("[" + category.getNumberOfUndoneItems(false) + "]");
            expandCategorySubTasksButton.setText("[" + category.getNumberOfUndoneItems(false) + "]");
//            subTasksButton.setIcon(Icons.get().iconShowMoreLabelStyle);
            expandCategorySubTasksButton.setUIID("Label");
            east.addComponent(expandCategorySubTasksButton);
//            cont.putClientProperty("subTasksButton", subTasksButton);
//            cont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, expandCategorySubTasksButton);
        }
        east.addComponent(new Label(MyDate.formatDurationStd(category.getRemaining()))); //TODO reactivate this once caching of sum of effort in category is implemented

        east.addComponent(editItemPropertiesButton);

        if (MyPrefs.showCategoryDescriptionInCategoryList.getBoolean() && !category.getComment().equals("")) {
            mainCont.addComponent(BorderLayout.SOUTH,
                    new Container(BoxLayout.x()).add(
                            new Label("(" + category.getComment() + ")")));
        }

//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
//        return cont;
//TODO any swipeable actions on category list??
        return swipCont;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    class TreeItemList extends TreeInitialCollapse {
//
//        private int myDepthIndent = 15;
////            Tree dt = new Tree(listOfItemLists) {
//
//        TreeItemList(ItemList listOfItemLists, boolean collapseTopLevelNode) {
//            super(listOfItemLists, collapseTopLevelNode);
//            setNodeIcon(null);
//            setFolderOpenIcon(Icons.get().iconShowLessLabelStyle);
//            setFolderIcon(Icons.get().iconShowMoreLabelStyle);
//        }
//
//        @Override
//        protected Component createNode(Object node, int depth) {
//            Container cmp = null;
////            if (node instanceof ItemList) {
////                cmp = buildItemListContainer((ItemList) node, itemListList);
////            } else if (node instanceof Item) {
////                cmp = Container.encloseIn(BoxLayout.y(), new Label(((Item) node).getText())); //TODO!!! replace by appropriate container
////            } else {
////                assert false : "unknown type of node" + node;
////            }
//            if (node instanceof Item) {
////                cmp = ItemContainer.buildTreeOrSingleItemContainer((Item) node, (ItemList) treeParent);
////                cmp = ItemContainer.buildItemContainer((Item) node, (ItemList) treeParent);
//                cmp = ScreenListOfItems.buildItemContainer((Item) node, category, () -> true;() -> {
//                }
//              );
//            } else if (node instanceof Category) {
//                cmp = buildCategoryContainer((Category) node); //, (ItemList) treeParent);
//            } else {
//                assert false : "treeParent should only be Item or ItemList: treeParent=" + treeParent;
//            }
//
////                cmp.setUIID("TreeNode"); cmp.setTextUIID("TreeNode"); if(model.isLeaf(node)) {cmp.setIcon(nodeImage);} else {cmp.setIcon(folder);}
//            cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
//            cmp.getUnselectedStyle().setMargin(LEFT, depth * myDepthIndent);
//            cmp.getPressedStyle().setMargin(LEFT, depth * myDepthIndent);
//            cmp.setScrollable(false); //to avoid nested scrolling, http://stackoverflow.com/questions/36044418/how-to-extend-infinitecontainer-with-the-capability-of-expanding-the-nodes-in-th
//            return cmp;
//        }
//
//        @Override
//        protected void bindNodeListener(ActionListener l, Component node) {
////            Object expandCollapseButton = node.getClientProperty("subTasksButton");
////            if (expandCollapseButton != null && expandCollapseButton instanceof Button) //            ((Button) (((Container) node).getClientProperty("subTasksButton"))).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
////            {
////                ((Button) (expandCollapseButton)).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
////            }
//            Object expandCollapseButton = node.getClientProperty("subTasksButton");
//            if (expandCollapseButton != null && expandCollapseButton instanceof Button) {
//                ((Button) (expandCollapseButton)).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
//                ((Button) (expandCollapseButton)).putClientProperty("TreeContainer", node);
//            }
//
//        }
//
//        @Override
//        protected void setNodeIcon(Image icon, Component node) {
//            Object expandCollapseButton = node.getClientProperty("subTasksButton");
////            ((Button) (((Container) node).getClientProperty("subTasksButton"))).setIcon(icon); //in a tree of ItemLists there shall always be a subTasksButton
//            if (expandCollapseButton != null && expandCollapseButton instanceof Button) {
//                ((Button) (expandCollapseButton)).setIcon(icon); //in a tree of ItemLists there shall always be a subTasksButton
//            }
//        }
//    };
//</editor-fold>

    protected Container buildContentPaneForItemList(ItemList itemLists) {
        parseIdMapReset();
//<editor-fold defaultstate="collapsed" desc="comment">
//        InfiniteContainer cl = new InfiniteContainer(20) {
//            @Override
//            public Component[] fetchComponents(int index, int amount) {
//
//                java.util.List<ItemList> list = itemLists.subList(index, index + Math.min(amount, itemLists.size() - index));
//                if (list.isEmpty()) {
//                    return null;
//                }
//                Component[] comps = new Component[list.size()];
//                for (int i = 0, size = list.size(); i < size; i++) {
////                    comps[i] = buildCategoryContainer((Category) list.get(i)); //for lists of lists, we'll always use Tree (since few lists will be empty)
//                    comps[i] = new MyTree((Category) list.get(i), true); //for lists of lists, we'll always use Tree (since few lists will be empty)
//                }
//                return comps;
//            }
//        };
//        return cl;
//</editor-fold>
        dt = new MyTree2(itemLists, expandedObjects) {
            Category category;

//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            protected Component createNode(Object node, int depth) {
//                return createNode(node, depth, null);
//            }
//</editor-fold>
            @Override
            protected Component createNode(Object node, int depth, Category cat) {
                Container cmp = null;
                if (node instanceof Item) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> dt.removeFromCache(),
//                            false, //selectionMode not allowed for Categories??
//                            null); //TODO any reason to support operations on multiple selected categories?
//TODO!!! store expanded itemLists:
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> refreshAfterEdit(),
////                    cmp = ScreenListOfItems.buildItemContainer((Item) node, () -> true, () -> refreshAfterEdit(),
//                            false, //selectionMode not allowed for list of itemlists //TODO would some actions make sense on multiple lists at once??
//                            null, //selected objects
////                            category, keepPos, expandedObjects, ()->animateMyForm(), false); //hack: get access to the latest category (the one above the items in the Tree list)
//                            category, keepPos, expandedObjects, ()->animateMyForm(), false, false); //hack: get access to the latest category (the one above the items in the Tree list)
//</editor-fold>
                    cmp = ScreenListOfItems.buildItemContainer(ScreenListOfCategories.this, (Item) node, null, cat); //hack: get access to the latest category (the one above the items in the Tree list)
                } else if (node instanceof Category) {
                    cmp = buildCategoryContainer((Category) node, categoryList, keepPos, () -> refreshAfterEdit()); //, (ItemList) treeParent);
                    category = (Category) node; //huge hack: store the category of the latest category container for use when constructing the following
                } else {
                    assert false : "should only be Item or ItemList, was:" + node;
                }
                setIndent(cmp, depth);
                return cmp;
            }

        };
        return dt;
//<editor-fold defaultstate="collapsed" desc="comment">
//        MyTree dt = new MyTree(itemList) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = buildCategoryContainer((Category)node);
//                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
//                return cmp;
//            }
//        };
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont.setScrollableY(true);
//        cont.add(dt);
////        cont.setDraggable(true);
//        dt.setDropTarget(true);
//        return cont;
//</editor-fold>
    }
}
