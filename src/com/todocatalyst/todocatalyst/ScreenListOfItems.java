/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.components.ToastBar;
import com.codename1.io.Log;
import com.codename1.l10n.L10NManager;
import com.codename1.ui.CN;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.Component;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_EXPANDED;
import static com.todocatalyst.todocatalyst.MyTree2.KEY_OBJECT;
import com.todocatalyst.todocatalyst.MyTree2.ListAndIndex;
import java.util.ArrayList;
import java.util.Date;
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
public class ScreenListOfItems extends MyForm {

    //TODO 
    //TODO!!! 'quick task entry' - add interpreting  to define textually estimate, due date etc
    //TODO on small screens (even with high resolution?) make better use of the width, eg w Imp/Urg, subtasks, due date, there are only three chars left for the task name
    //TODO make button in titlebar use less whitespace between them (e.g. interrupt, +, ...) - put some in overflow menu on small screens??
    //TODO swipe left to reveal details of a task (or click some other button?)
    //DONE!! Multiple selections: for MoveToTop: change selectedObjects from hashset to arraylist to keep order of selection and move selected items to top in selected order (actually inverse: first selected is moved to top last to be at head of li´st)
    //TODO!! Multiple selections: make enable multiple selections a separate button to easily toggle it off
    //TODO!! Multiple selections: show available operations in a separate toolbar at the bottom of the screen
    //TODO use ComponentGroup for nice formatting when expanding the subtasks of a list?
    //TODO implement Pull to Refresh
    //TODO show smart dates ('today')
    //TODO how to support move-mode? Set all containers with Draggable/Focusable
    //TODO!! error: cannot expand subtasks in subtask tab (pointer error)
    //TODO when coming back to main list after editing an expanded subtask, the subtask is no longer indended (new container not generated with indend?? or is it because it should be replaced directly in its own container, not the main container)
    //TODO refresh entire tree container, eg when an expanded subtask is changed (marked done)
    //TODO redduce white space between task description and buttons
    //TODO refresh mother
    //TODO swipe two containers away from each other to create/insert a new task
    //TODO make DueDate and other fields shown active edit buttons to directly edit the info (same pop-up as in ScreenItem?)
    //TODO when sorting on a specific field (eg Dread/Fun) - show the field either in item-line or as floating section header
    //TODO error: when expanding 2nd level of sub-task hierarchy, it repeats the same subtasks as the first level of subtasks    
    //TODO Update Sort tasks menu to Sort ON/OFF -> separate definition of Sort and Filter?
    //TODO updating the list after editing an (expanded) subtask doesn't work (the subtask replaces a higher level task)!!!
    //TODO if no tasks shown from list, display sth like "<all tasks filtered>" or "<no tasks in this list, use [+] to add>"
    //TODO store expanded state of items (as persisted set of List/Items) - or at least find a way to keep when returning from editing a task in the list
    //TODO replace H/L for ImpUrg by symbols (e.g. with H and L in super and subscript)?
    //TODO optimize: avoid fetching WorkSlots more than once, or when edited (store in ItemList)
    //TODO Introduce Move mode where all items are in drag&drop mode
    //DONE!!! add a 'quick task entry' to the bottom of the screen to add tasks quickly. Add button to edit details [>]. Include interpreting  to define textually estimate, due date etc
    //DONE!!! add a menu to Show Details (show SOUTH container in the list). Hide the container when there is nothing to show
    //DONE Add Search to toolbar
    //DONEerror: after sorting, edit [>] disappears??
    //DONE implement swipe Timer
    //DONE show description of list at the top of the screen (in lower part of title bar?)???
    //DONE Sorting on DueDate is not working
    //DONE update display ofRemaining time from 75min to 1:15m
    //DONE number of subtasks include Done tasks
    //DONE refresh each item container after editing the item
    //DONE don't refresh the entire list after editing one item, make sure the list stays in same place, and only the edited item's container is updated
    final static String SCREEN_ID = "ScreenListOfItems";
//    final static String INSERT_NEW_TASK_AS_SUBTASK_KEY = "SubtaskLevel";
//    final static String EXISTING_NEW_TASK_CONTAINER = "NewTaskContainer"; //stores the current newSubtask container to allow to automatically close one if a new one is created elsewhere
//    private static String screenTitle = "Tasks";
//    private ItemList<Item> itemListOrg;
    private ItemList itemListOrg;
    private GetItemListFct getItemListFct;
//    private ItemList itemListFilteredSorted;
//    private java.util.List workSlotList;
//    private WorkTimeDefinition wtd;
    private FilterSortDef filterSortDef;
//    private Container dragAndDropContainer;
//    private boolean draggableMode = false;
//    private Command sortOnOff = null;
//    private Command draggableOnOff = null;
//    HashSet expandedObjects = new HashSet(); //TODO!! save expandedObjects for this screen and the given list
//    boolean selectionMode = false; //is selectionMode on
//    private HashSet selectedObjects; //selected objects
    ArrayList selectedObjects = new ArrayList(); //selected objects
//    private ArrayList oldSelectedObjects; //store selection after deactivating
//    private static SwipeableContainer newTaskContainer = null; //stores the current newSubtask container to allow to automatically close one if a new one is created

//    private static WorkTimeDefinition wtd;
    boolean projectEditMode = false; //indicates if projectEditMode is on (eg new tasks automatically added to end of list)
    private MyTree2.StickyHeaderGenerator stickyHeaderGen;

    /**
     * set to true if a fixed filter is passed to the screen, if true user is
     * not allowed to modify the filter, nor will it be saved
     */
    final static int OPTION_TEMPLATE_EDIT = 1;
    private boolean optionTemplateEditMode;
    final static int OPTION_NO_TIMER = OPTION_TEMPLATE_EDIT * 2;
    private boolean optionNoTimer;
    final static int OPTION_NO_INTERRUPT = OPTION_NO_TIMER * 2;
    private boolean optionNoInterrupt;
    final static int OPTION_NO_NEW_BUTTON = OPTION_NO_INTERRUPT * 2;
    private boolean optionNoNewButton;
    final static int OPTION_NO_WORK_TIME = OPTION_NO_NEW_BUTTON * 2;
    private boolean optionNoWorkTime;
    final static int OPTION_NO_EDIT_LIST_PROPERTIES = OPTION_NO_WORK_TIME * 2;
    private boolean optionNoEditListProperties;
    final static int OPTION_NO_MODIFIABLE_FILTER = OPTION_NO_EDIT_LIST_PROPERTIES * 2;
    private boolean optionUnmodifiableFilter;
    final static int OPTION_NO_SELECTION_MODE = OPTION_NO_MODIFIABLE_FILTER * 2;
    private boolean optionNoMultipleSelectionMode;
    final static int OPTION_DISABLE_DRAG_AND_DROP = OPTION_NO_SELECTION_MODE * 2;
    private boolean optionDisableDragAndDrop;
    final static int OPTION_SINGLE_SELECT_MODE = OPTION_DISABLE_DRAG_AND_DROP * 2;
    private boolean optionSingleSelectMode;
    final static int OPTION_NO_NEW_FROM_TEMPLATE = OPTION_SINGLE_SELECT_MODE * 2;
    private boolean optionNoNewFromTemplate;
    final static int OPTION_NO_TASK_DETAILS = OPTION_NO_NEW_FROM_TEMPLATE * 2;
    private boolean optionNoTaskDetails;
//    boolean optionSingleSelectMode;
//    final static int OPTION_MULTIPLE_SELECT_MODE = OPTION_SINGLE_SELECT_MODE * 2;
//    private boolean optionMultipleSelectMode;

    private void setOptions(int options) {
        this.optionTemplateEditMode = (options & OPTION_TEMPLATE_EDIT) != 0;
        this.optionNoTimer = (options & OPTION_NO_TIMER) != 0;
        this.optionNoInterrupt = (options & OPTION_NO_INTERRUPT) != 0;
        this.optionNoNewButton = (options & OPTION_NO_NEW_BUTTON) != 0;
        this.optionNoWorkTime = (options & OPTION_NO_WORK_TIME) != 0;
        this.optionNoEditListProperties = (options & OPTION_NO_EDIT_LIST_PROPERTIES) != 0;
        this.optionUnmodifiableFilter = (options & OPTION_NO_MODIFIABLE_FILTER) != 0;
        this.optionNoMultipleSelectionMode = (options & OPTION_NO_SELECTION_MODE) != 0;
        this.optionDisableDragAndDrop = (options & OPTION_DISABLE_DRAG_AND_DROP) != 0;
        this.optionSingleSelectMode = (options & OPTION_SINGLE_SELECT_MODE) != 0;
        this.optionNoNewFromTemplate = (options & OPTION_SINGLE_SELECT_MODE) != 0;
        this.optionNoTaskDetails = (options & OPTION_SINGLE_SELECT_MODE) != 0;
//        this.optionMultipleSelectMode = (options & OPTION_MULTIPLE_SELECT_MODE) != 0;

    }
    /**
     * stores the overall tree for this list
     */
//    private static MyTree2 myTree; //static to be able to animate within 
    private MyTree2 myTree; //static to be able to animate within 

//    InsertNewTaskContainer lastInsertNewTaskContainer; //stores the last container, until closed by user
//    int indexOfSwipContParentParent = -1;
//    Item lastInsertSwiptCreatedItem;
//<editor-fold defaultstate="collapsed" desc="comment">
//    boolean shouldContinue(Component[] cmps) {
//        if(amountSet) {
//            return cmps.length == amount;
//        } else {
//            return cmps != null && cmps.length > 0;
//        }
//    }
//
//    Runnable refreshTask = new Runnable() {
//
//            public void run() {
//
//                Display.getInstance().invokeAndBlock(new Runnable() {
//
//                    public void run() {
//                        requestingResults = true;
//                        Component[] components = fetchComponents(0, amount);
//                        if (components == null) {
//                            components = new Component[0];
//                        }
//                        final Component[] cmps = components;
//                        Display.getInstance().callSerially(new Runnable() {
//
//                            public void run() {
//                                removeAll();
//                                InfiniteScrollAdapter.addMoreComponents(InfiniteContainer.this, cmps, shouldContinue(cmps));
//                                requestingResults = false;
//                            }
//                        });
//                    }
//                });
//            }
//        };
//</editor-fold>
    /**
     * edit a list of categories
     *
     * @param title
     * @param category
     * @param previousForm
     * @param category
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    ScreenListOfItemLists(String title, Category category, MyForm previousForm) {
//        this(title==null?screenTitle:title, category, previousForm,
//                 (cat) -> {
//                            cat.setList(cat.getList());
//                            DAO.getInstance().save(cat);
//                        });
//    }
//</editor-fold>
//    ScreenListOfItems(ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone) {
//        this(itemList.getText(), itemList, previousForm, updateItemListOnDone);
//    }
//
//    ScreenListOfItems(String title, ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
////        this(title, itemList, previousForm, updateItemListOnDone, null, true);
//        this(title, itemList, previousForm, updateItemListOnDone, 0);
//    }
    /**
     *
     * @param title
     * @param itemList list containing the items to display
     * @param previousForm
     * @param updateItemListOnDone
     * @param filterSortDef used if defined, if null, default filter is used
     * @param filterCanBeModified if true, user cannot modify the given filter
     * (used e.g. in Log)
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    ScreenListOfItems(String title, ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone, FilterSortDef filterSortDef, boolean filterCanBeModified) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        this(title, itemList, previousForm, updateItemListOnDone, filterSortDef, filterCanBeModified, false);
//    }
//
//    ScreenListOfItems(String title, ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone, FilterSortDef filterSortDef, boolean filterCanBeModified, boolean templateEditMode) {
//        this(title, itemList, previousForm, updateItemListOnDone, filterSortDef, (filterCanBeModified ? 0 : OPTION_NO_MODIFIABLE_FILTER) | (templateEditMode ? OPTION_TEMPLATE_EDIT : 0));
//    }
//
//    ScreenListOfItems(String title, ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone, FilterSortDef filterSortDef, int options) {
//
//    }
//</editor-fold>
    ScreenListOfItems(String title, ItemList itemList, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone, int options) {
        this(title, () -> itemList, previousForm, updateItemListOnDone, options);
    }

    ScreenListOfItems(String title, GetItemListFct itemListFct, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone, int options) {
        this(title, itemListFct, previousForm, updateItemListOnDone, options, null);
    }

    ScreenListOfItems(String title, GetItemListFct itemListFct, MyForm previousForm, UpdateItemListAfterEditing updateItemListOnDone,
            int options, MyTree2.StickyHeaderGenerator stickyHeaderGen) {
        super(title, previousForm, () -> updateItemListOnDone.update(itemListFct.getUpdatedItemList()));
        setScrollVisible(true); //UI: show scrollbar(?)
//        super(title, previousForm, null);
        setOptions(options);
        getComponentForm().getLayeredPane().setLayout(new BorderLayout()); //needed for StickyHeaderMod
//        MyDragAndDropSwipeableContainer.dragEnabled = false; //always disable before setting up a new screen
//        this.itemListOrg = itemList;
        this.getItemListFct = itemListFct;
        this.itemListOrg = itemListFct.getUpdatedItemList(); //TODO!!!! Optimization: double call - also called in refreshAfterEdit first time screen is shown!!!
        this.updateActionOnDone = () -> {
            this.itemListOrg = itemListFct.getUpdatedItemList();
            updateItemListOnDone.update(this.itemListOrg);
            refreshAfterEdit();
        };
//        workSlotList = itemListOrg.getWorkSlotListN(); //expensive operation, only do once for the screen, or after editing WorkSlots

        setScrollable(false); //don't set form scrollable when containing a (scrollable) list: https://github.com/codenameone/CodenameOne/wiki/The-Components-Of-Codename-One#important---lists--layout-managers
        setLayout(new BorderLayout());

//<editor-fold defaultstate="collapsed" desc="comment">
//        addShowListener((e) -> { //DOESN'T WORK since animate doesn't trigger onShow
//            if (InsertNewTaskContainer.lastInsertNewTaskContainer != null) {
//                setEditOnShow(InsertNewTaskContainer.lastInsertNewTaskContainer.getTextField());
//            }
//        });
//        getContentPane().add(SOUTH, makeQuickAddBox(projectEditMode));
//        if (filterSortDef != null) {
//            this.filterSortDef = filterSortDef;
//        } else {
////            filterSortDef = FilterSortDef.fetchFilterSortDef(SCREEN_ID, itemList, new FilterSortDef(SCREEN_ID, itemList));
////            this.filterSortDef = FilterSortDef.fetchFilterSortDef(SCREEN_ID, itemListOrg, null); //if no filter previously defined/saved, then don't create one until the user either edits the filter or turns Sort on
//            this.filterSortDef = itemListOrg.getFilterSortDef(); //if no filter previously defined/saved, then don't create one until the user either edits the filter or turns Sort on
//        }
//        this.filterSortDef = itemListOrg.getFilterSortDef(); //if no filter previously defined/saved, then don't create one until the user either edits the filter or turns Sort on
//        this.filterSortDef = filterSortDef;
//        optionUnmodifiableFilter = !filterCanBeModified;
//</editor-fold>
//        expandedObjects = new HashSet();
//        expandedObjects = new ExpandedObjects("ScreenListOfItems", itemListOrg);
        expandedObjects = new ExpandedObjects("ScreenListOfItems", itemListOrg.getObjectIdP()==null?getTitle():itemListOrg.getObjectIdP());
        this.stickyHeaderGen = stickyHeaderGen;
//        refreshItemListFilterSort();
        addCommandsToToolbar(getToolbar());
        getToolbar().addSearchCommand((e) -> {
            String text = (String) e.getSource();
            Container compList = null;
            compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
            if (compList != null) {
//            boolean showAll = text == null || text.length() == 0;
                int labelCount = 0;
                int nonLabelCount = 0;
//                int prevLabelPos = 0;
                boolean searchOnLowerCaseOnly;
                Label lastLabel = null;
                boolean hide;
//                for (int i = 0, size = this.itemListFilteredSorted.size(); i < size; i++) {
                for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                    //TODO!!! compare same case (upper/lower)
                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
                    searchOnLowerCaseOnly = text.equals(text.toLowerCase()); //if search string is all lower case, then search on lower case only, otherwise search on 

                    Component comp = compList.getComponentAt(i);
                    if (comp instanceof Label || comp instanceof StickyHeader) {
                        if (lastLabel != null) {
//                            if (nonLabelCount == 0) {
//                                lastLabel.setHidden(true); //hide previous label if nothing is shown after it
//                            } else {
//                                lastLabel.setHidden(false); //hide previous label if nothing is shown after it
//                            }
                            lastLabel.setHidden(nonLabelCount == 0); //hide previous label if nothing is shown after it
                        }
//                        lastLabel.setHidden(nonLabelCount == 0 && lastLabel != null); //hide previous label if nothing is shown after it
                        nonLabelCount = 0; //reset count on every Label
                        labelCount++; //hack: StickyHeaders are Labels, so count them and add to count
//                        prevLabelPos=i;
                        lastLabel = (Label) comp;
                    } else {
//                        nonLabelCount++;
                        if (searchOnLowerCaseOnly) {
//                            compList.getComponentAt(i).setHidden(((String) list.get(i)).toLowerCase().indexOf(txt) < 0);
//                            comp.setHidden(((Item) itemListFilteredSorted.get(i - labelCount)).getText().toLowerCase().indexOf(text) < 0);
//                            hide = ((Item) itemListFilteredSorted.get(i - labelCount)).getText().toLowerCase().indexOf(text) < 0;
                            hide = ((Item) itemListOrg.get(i - labelCount)).getText().toLowerCase().indexOf(text) < 0;
                        } else {
//                            compList.getComponentAt(i).setHidden(((String) list.get(i)).indexOf(txt) < 0);
//                            comp.setHidden(((Item) itemListFilteredSorted.get(i - labelCount)).getText().indexOf(text) < 0);
//                            hide = ((Item) itemListFilteredSorted.get(i - labelCount)).getText().indexOf(text) < 0;
                            hide = ((Item) itemListOrg.get(i - labelCount)).getText().indexOf(text) < 0;
                        }
                        comp.setHidden(hide);
                        if (!hide) {
                            nonLabelCount++;
                        }
                    }
                }
                if (nonLabelCount == 0 && lastLabel != null) {
                    lastLabel.setHidden(true); //hide previous label if nothing is shown after it
                }
            } else {
//                compList = getContentPane();
                for (int i = 0, size = compList.getComponentCount(); i < size; i++) {
                    //TODO!!! compare same case (upper/lower)
                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                compList.getComponentAt(i).setHidden(((Item) itemListFilteredSorted.get(i)).getText().toLowerCase().indexOf(text) < 0);
                    Component comp = compList.getComponentAt(i);
//                    Object sourceObj = comp.getClientProperty(SOURCE_OBJECT);
                    Object sourceObj = comp.getClientProperty(KEY_OBJECT);
                    comp.setHidden(sourceObj != null && sourceObj instanceof Item && ((Item) sourceObj).getText().toLowerCase().indexOf(text) < 0);
                }
            }
            if (compList != null) {
                compList.animateLayout(150);
            }
        });
//        if (itemList instanceof ParseObject && itemList.getObjectId() != null) {
//            filterSortDef = DAO.getInstance().getFilterSortDef(SCREEN_ID, itemList.getObjectId());
//        }
//        if (filterSortDef == null) {
//            filterSortDef = new FilterSortDef(SCREEN_ID, itemList.getObjectId());
//            DAO.getInstance().save(filterSortDef);
//        }
//        dragAndDropContainer = buildContentPaneForItemList(this.itemList);
//        getContentPane().add(CENTER, dragAndDropContainer);
//        setupList();
        refreshAfterEdit();
    }

    @Override
    protected void animateMyForm() {
//        myTree.animateLayout(150);
        ((Container) ((BorderLayout) getContentPane().getLayout()).getCenter()).animateLayout(150); //fragile!!
    }

    /**
     * (only!) call when itemListOrg has changed, or filter has been changed
     */
//    private void refreshItemListFilterSortXXX() {
//        if (filterSortDef != null) {
//            this.itemListFilteredSorted = filterSortDef.filterAndSortItemList(itemListOrg);
////            setupList();  //refresh
//        } else {
//            this.itemListFilteredSorted = itemListOrg;
//        }
//    }
    @Override
    public void refreshAfterEdit() {
//    public void setupList() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again

        getContentPane().removeAll();

//        if (false) {
//            getContentPane().add(SOUTH, makeQuickAddBox(projectEditMode));
//            getContentPane().add(SOUTH, new QuickAddItemContainer(projectEditMode));
//        }
        this.itemListOrg = getItemListFct.getUpdatedItemList();
        itemListOrg.resetWorkTimeDefinition(); //TODO!!!!! find a way to automatically reset wtd each time a list or its elements have been modified -> itemList.save(), or items call update/refresh on owner (and categories!)
//        getContentPane().scrollComponentToVisible(this);
//        refreshItemListFilterSort();
        filterSortDef = itemListOrg.getFilterSortDef();
//        if (filterSortDef != null && !filterSortDef.isNeutral()) {
//            this.itemListFilteredSorted = filterSortDef.filterAndSortItemList(itemListOrg);
////            setupList();  //refresh
//        } else {
//            this.itemListFilteredSorted = itemListOrg;
//        }
//
//        wtd = new WorkTimeDefinition(itemListOrg.getWorkSlotListN(true), itemListFilteredSorted);
//        itemListOrg.refreshWorkTimeDefinition();

//        getContentPane().add(CENTER, buildContentPaneForItemList(this.itemListFilteredSorted));
        parseIdMapReset();
        Container scrollableContainer = buildContentPaneForItemList(this.itemListOrg);
        getContentPane().add(CENTER, scrollableContainer);
        setTitleAnimation(scrollableContainer);

        revalidate(); //TODO: needed? YES
//        if (this.keepPos != null) {
//            this.keepPos.setNewScrollYPosition();
//        }
        restoreKeepPos();
//        InlineInsertNewElementContainer.setTextFieldEditableOnShowStatic(this); //if there is a InlineInsertNewTaskContainer then focus the input field

//        revalidate(); //TODO: needed? YES
//        animateHierarchy(300); not good since it visibly refreshes the screen
        super.refreshAfterEdit();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private static void addNewTaskXXX(Item item, int pos, ItemList itemListOrg, boolean optionTemplateEditMode) {
//        item.setTemplate(optionTemplateEditMode); //template or not
//        boolean addToList = (itemListOrg != null && itemListOrg.getObjectIdP() != null && !(itemListOrg instanceof Category)); //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
//        if (addToList) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
//            itemListOrg.addToList(pos, item); //UI: add to top of list
//        }
//        DAO.getInstance().save(item); //must save item since adding it to itemListOrg changes its owner
//        if (addToList) {
//            DAO.getInstance().save(itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//        }
//    }
//    @Override
//    public void refreshAfterEdit() {
//        setupList();
//    }
//</editor-fold>
    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //NEW ITEM
//        Command newCmd = new Command("OldCmd", Icons.iconNewToolbarStyle) {
        if (!optionNoNewButton) {
            if (itemListOrg.isNoSave()) {
                toolbar.addCommandToRightBar(newItemSaveToInboxCmd());
            } else {
//            Command newCmd = MyReplayCommand.create("CreateNewItem", "", Icons.iconNewToolbarStyle(), (e) -> {
                Command newCmd = MyReplayCommand.create("CreateNewItem", "", Icons.iconNewTaskToolbarStyle(), (e) -> {
                    Item item = new Item();
//                    item.setOwner(itemListOrg); //necessary to have an owner when creating repeatInstances (item will be added to itemListOrg upon acceptance/exit from screen)
                    item.setTemplate(optionTemplateEditMode);
                    addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg); //necessary to add to owner when creating repeatInstances (item will be added to itemListOrg upon acceptance/exit from screen)
//                    ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                    setKeepPos(new KeepInSameScreenPosition());
//                DAO.getInstance().fetchAllElementsInSublist(item, true);
                    new ScreenItem(item, ScreenListOfItems.this, () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
////                    DAO.getInstance().save(item); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        boolean addToList = (itemListOrg != null && itemListOrg.getObjectId() != null && !(itemListOrg instanceof Category)); //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
////                    if (itemListOrg != null && itemListOrg.getObjectId() != null && !(itemListOrg instanceof Category)) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
//                        if (addToList) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
////                        item.setOwnerList(itemListOrg);
////                        item.setOwner(itemListOrg); //works for any type of owner
////                        itemListOrg.addItemAtIndex(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize()); //UI: add to top of list
//                            itemListOrg.addToList(MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), item); //UI: add to top of list
////=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        }
//                        DAO.getInstance().save(item); //must save item since adding it to itemListOrg changes its owner
//                        if (addToList) {
//                            DAO.getInstance().save(itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        }//                    } else {
//                        if (!(!item.needToSaveNewTask() && !Dialog.show("INFO", "No key data in this task, save anyway?", "Save","Don't save"))) {
//</editor-fold>
                        if (item.hasSaveableData() || Dialog.show("INFO", "No key data in this task, save anyway?", "Save", "Don't save")) {
                            //TODO!!!! this test is not in the right place - it should be tested inside ScreenItem before exiting
                            //only save if data (don't save if no relevant data)
                            if (false) {
                                item.setTemplate(optionTemplateEditMode);
                                addNewTaskToListAndSave(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), itemListOrg);
                            }
                            DAO.getInstance().save(item); //must save item since adding it to itemListOrg changes its owner
                            refreshAfterEdit(); //TODO!!! scroll to where the new item was added (either beginning or end of list)
//                        assert false : "should not happen: itemListOrg == null || itemListOrg.getObjectId()==null";
//                    }
                        } else {
                            itemListOrg.removeFromList(item); //if no saveable data, undo the 
                            //TODO!!!! how to remove from eg Categories if finally the task is not saved??
                        }
                    }, optionTemplateEditMode).show();
                }
                );
                toolbar.addCommandToRightBar(newCmd);
            }
        }

        if (false) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            Button oldCmdButton = toolbar.findCommandComponent(newCmd);
//            Button newCmdButton = new Button(newCmd) {
//                @Override
//                public void longPointerPress(int x, int y) {
//                    super.longPointerPress(x, y);
////                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
//                    Item selectedTemplate = pickTemplate();
//                    if (selectedTemplate != null) { //null if user cancelled
//                        Item newTemplateInstantiation = new Item();
//                        selectedTemplate.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
//                        new ScreenItem(newTemplateInstantiation, ScreenListOfItems.this, () -> {
//                            if (itemListOrg != null && itemListOrg.getObjectId() != null) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
//                                itemListOrg.addToList(MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize(), newTemplateInstantiation); //UI: add to top of list
//                                DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner
//                                DAO.getInstance().save(itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            } else {
//                                assert false : "should not happen: itemListOrg == null || itemListOrg.getObjectId()==null";
//                            }
//                        }).show();
//                    }
//                }
//            };
//            newCmdButton.setText("NewCmd");
//            newCmdButton.putClientProperty("TitleCommand", Boolean.TRUE);
//            toolbar.replace(oldCmdButton, newCmdButton, null);
//            toolbar.repaint();
//</editor-fold>
        }
//        ScreenListOfItems.this.revalidate(); //necessary to see/activate new buttons?

        //NEW ITEM from TEMPLATE
        //TODO!!! create template by longpress on (+)
        if (true || optionTemplateEditMode) {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.create("NewFromTemplate", "New from template", null, (e) -> {
                //select appropriate template
//<editor-fold defaultstate="collapsed" desc="comment">
//                List<Item> templateList = DAO.getInstance().getAllTemplates();
//                Picker templatePicker = new Picker();
////                templatePicker.s;
//                String[] stringArray = new String[templateList.size()];
//                for (int i = 0, size = templateList.size(); i < size; i++) {
//                    stringArray[i] = templateList.get(i).getText();
//                }
//                templatePicker.setType(Display.PICKER_TYPE_STRINGS);
//                templatePicker.setStrings(stringArray);
//                templatePicker.pressed();
//                templatePicker.released(); //simulate pressing the key to make the Picker pop up without a physical key
//                String s = templatePicker.getSelectedString();
//                Item selectedTemplate = null;
//                if (s != null) {
//                    for (int i = 0, size = templateList.size(); i < size; i++) {
//                        if (s.equals(stringArray[i])) {
//                            selectedTemplate = templateList.get(i);
//                            break;
//                        }
//                    }
//                }
//</editor-fold>
//                Item selectedTemplate = pickTemplateOLD();
                Item selectedTemplate = null; //pickTemplate();
                if (selectedTemplate != null) { //null if user cancelled
                    Item newTemplateInstantiation = new Item();
                    if (itemListOrg instanceof ParseObject) { //itemListOrg can be a temporary list like Today
                        newTemplateInstantiation.setOwner(itemListOrg); //necessary to have an owner when creating repeatInstances (item will be added to itemListOrg upon acceptance/exit from screen)
                    }
//                        setKeepPos(new KeepInSameScreenPosition()); //NO, move to position of new item
                    selectedTemplate.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
                    new ScreenItem(newTemplateInstantiation, ScreenListOfItems.this, () -> {
//                        DAO.getInstance().save(newTemplateInstance); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            newTemplateInstance.setOwner(itemListOrg); //works for any type of owner
//                            DAO.getInstance().save(newTemplateInstance); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                            itemListOrg.addItemAtIndex(newTemplateInstance, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : itemListOrg.getSize()); //UI: add to top of list
                        if (false) {
                            DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner
                            if (itemListOrg != null && itemListOrg.getObjectIdP() != null && !optionTemplateEditMode) { //if no itemList is defined (e.g. if editing list of tasks obtained directly from server
                                //if creating new template instances within the TemplateList, then don't save it into template list
                                itemListOrg.addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : itemListOrg.getSize(), newTemplateInstantiation); //UI: add to top of list
                                DAO.getInstance().save(itemListOrg); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                            } else {
                                assert false : "should not happen: itemListOrg == null || itemListOrg.getObjectId()==null";
                            }
                        }
                        newTemplateInstantiation.setTemplate(optionTemplateEditMode);
                        addNewTaskToListAndSave(newTemplateInstantiation, MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : itemListOrg.getSize(), itemListOrg);
                        setKeepPos(new KeepInSameScreenPosition(newTemplateInstantiation)); //scroll to position of new item, whereever it is inserted
                        refreshAfterEdit();
                    }).show();
                }
            }
            ));
        }

        //EDIT PROPERTIES OF LIST
        if (!optionTemplateEditMode && !optionNoEditListProperties) {
            String txt = itemListOrg instanceof Category ? "Category Properties" : "List Properties";
            toolbar.addCommandToOverflowMenu(MyReplayCommand.create("EditCatListProps", txt, null, (e) -> {
//                ItemList itemList = new ItemList();
//                    setKeepPos(new KeepInSameScreenPosition()); //not needed
                new ScreenItemListProperties(itemListOrg, ScreenListOfItems.this, () -> {
                    DAO.getInstance().save(itemListOrg);
                    setTitle(itemListOrg.getText()); //refrehs title of screen after edit of list name
//                    previousForm.revalidate(); //refresh list to show new items(??)
                }).show();
            }
            ));
        }

        //EDIT WORKSLOTS
        if (!optionTemplateEditMode && !optionNoWorkTime) {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.create("EditWorkTime", "Work time", Icons.iconSettingsApplicationLabelStyle, (e) -> {
                setKeepPos(new KeepInSameScreenPosition());
                new ScreenListOfWorkSlots(itemListOrg.getText(), itemListOrg.getWorkSlotListN(),
                        itemListOrg, ScreenListOfItems.this, (iList) -> {
//                    itemList.setWorkSLotList(iList); //NOT necessary since each slot will be saved individually
                            //DONE!!! reload/recalc workslots
                            itemListOrg.resetWorkTimeDefinition(); //ensure workTime is recalculated
                            ScreenListOfItems.this.refreshAfterEdit();
                        }, null, false).show();
            }
            ));
        }

        //SHOW DETAILS
        toolbar.addCommandToOverflowMenu(new Command("Task detailsXXX", Icons.iconCmdShowTaskDetails) {
            @Override
            public void actionPerformed(ActionEvent evt) {
//                showDetailsForAllTasks = !showDetailsForAllTasks;
                MyPrefs.flipBoolean(MyPrefs.showDetailsForAllTasks);
//                setupList(); //TODO optimize the application of a filter?
                //TODO!!!! scroll to 'same' place in list after expansion, e.g. keep top-most visible item before expand visible in same place, use Container.getClosestComponentTo(x, y) with upper-left-most position of contentPane?! c.getVisibleBounds(), c.getComponentAt(x,y)?
                refreshAfterEdit(); //TODO optimize the application of a filter?
                //TODO!!! animate each detail container (e.g. make visible for each task and animate)
            }

            @Override
            public String getCommandName() {
                return "Task details " + (MyPrefs.getBoolean(MyPrefs.showDetailsForAllTasks) ? "OFF" : "ON"); //if its already on, show OFF
            }
        }
        );

        //FILTER / SORT
        if (!optionTemplateEditMode && !optionUnmodifiableFilter) {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.create("Filter/Sort settings", Icons.iconSettingsLabelStyle, (e) -> {
//                    if (filterSortDef == null) {
////                        filterSortDef = FilterSortDef.fetchFilterSortDef(SCREEN_ID, itemListOrg, new FilterSortDef(SCREEN_ID, itemListOrg));
//                        filterSortDef = itemListOrg.getFilterSortDef();
//                        if (filterSortDef == null) {
//                            filterSortDef = new FilterSortDef();
//                        }
//                    }
                filterSortDef = itemListOrg.getFilterSortDef();
                if (filterSortDef == null) {
                    filterSortDef = new FilterSortDef();
                }
                setKeepPos(new KeepInSameScreenPosition());
                new ScreenFilter(filterSortDef, ScreenListOfItems.this, () -> {
//                    itemList = filterSortDef.filterAndSortItemList(itemListOrg);
                    DAO.getInstance().save(filterSortDef);
                    itemListOrg.setFilterSortDef(filterSortDef);
                    DAO.getInstance().save(itemListOrg);
//                        refreshItemListFilterSort();
//                        setupList(); //TODO optimize the application of a filter?
                    //TODO any way to scroll to a meaningful place after applying a filter/sort? Probably not!
                    refreshAfterEdit(); //TODO optimize the application of a filter? 
                }).show();
            }
            ));

//            toolbar.addCommandToOverflowMenu(sortOnOff = new Command("Sort ON/OFF", Icons.iconCmdSortOnOff) { //this title never shown
            toolbar.addCommandToOverflowMenu(new Command("Sort XXX", Icons.iconCmdSortOnOff) { //this title never shown
                @Override
                public void actionPerformed(ActionEvent evt) {
//                    if (filterSortDef == null) {
//                        filterSortDef = FilterSortDef.fetchFilterSortDef(SCREEN_ID, itemListOrg, new FilterSortDef(SCREEN_ID, itemListOrg));
//                    }
//                    if (filterSortDef == null) {
//                        filterSortDef = itemListOrg.getFilterSortDef();
//                        if (filterSortDef == null) {
//                            filterSortDef = new FilterSortDef();
//                        }
//                    }
                    filterSortDef = itemListOrg.getFilterSortDef();
                    filterSortDef.setSortOn(!filterSortDef.isSortOn());
//                itemList = filterSortDef.filterAndSortItemList(itemListOrg); //refresh list
//                    refreshItemListFilterSort();
//                    setupList(); //TODO optimize the application of a filter?
                    refreshAfterEdit(); //TODO optimize the application of a filter?
//                sortOnOff.setCommandName(filterSortDef.isSortOn() ? "Manual sort" : "Sort tasks");
//                sortOnOff.setCommandName("Sort"+(filterSortDef.isSortOn() ? " OFF" : " ON"));
                }

                @Override
                public String getCommandName() {
//                    return "Sort " + ((filterSortDef == null || !filterSortDef.isSortOn()) ? "ON" : "OFF");
                    return "Sort " + ((itemListOrg.getFilterSortDef() == null || !itemListOrg.getFilterSortDef().isSortOn()) ? "ON" : "OFF");
                }
                //TODO always get right command name by overriding getName
            });
        }

        //SELECTION MODE
        if (!optionTemplateEditMode && !optionNoMultipleSelectionMode) {

            Command cmdInvertSelection = new Command("Invert selection", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (isSelectionMode()) {
                        HashSet invertedSelection = new HashSet();
                        //for all visible (top-level - not expanded subtasks)
//                        for (Object item : itemListFilteredSorted) {
                        for (Object item : itemListOrg) {
                            if (item instanceof Item) {
                                if (!selectedObjects.contains(item)) {
                                    invertedSelection.add(item);
                                }
                            }
                        }
                        selectedObjects.clear();
                        selectedObjects.addAll(invertedSelection);
//                    ScreenListOfItems.this.revalidate();
//                    setupList(); //TODO optimize the application of a filter?
                        refreshAfterEdit(); //TODO optimize the application of a filter?
//                    myTree.animateLayout(300);
                    }
                }
            };

            Command cmdSelectAll = new Command("Select All", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (isSelectionMode()) {
//                    selectedObjects=new HashSet(itemListFilteredSorted);
//                        selectedObjects.addAll(itemListFilteredSorted);
                        selectedObjects.addAll(itemListOrg);
//                    ScreenListOfItems.this.revalidate();
//                    setupList(); //TODO optimize the application of a filter?
                        refreshAfterEdit(); //TODO optimize the application of a filter?
//                    myTree.animateLayout(300);
                    }
                }
            };

            Command cmdUnselectAll = new Command("Unselect All", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (isSelectionMode()) {
                        selectedObjects.clear();
//                    ScreenListOfItems.this.revalidate();
//                    setupList(); //TODO optimize the application of a filter?
                        refreshAfterEdit(); //TODO optimize the application of a filter?
//                    myTree.animateLayout(300);
                    }
                }
            };

            Command cmdSetAnything = new Command("Set multiple fields", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    if (isSelectionMode()) {
                        Item itemWithNewValues = new Item();
                        setKeepPos(new KeepInSameScreenPosition());
                        new ScreenItem(itemWithNewValues, ScreenListOfItems.this, () -> {
//                        MultipleSelection.performOnAll(itemListFilteredSorted, MultipleSelection.setAnything(itemWithNewValues));
                            MultipleSelection.performOnAll(selectedObjects, MultipleSelection.setAnything(itemWithNewValues));
                            refreshAfterEdit();
                        }).show();
                    }
                }
            };

            Command cmdDeleteSelected = new Command("Delete selected", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //TODO!!! warning that this cannot be undone!!
                    if (isSelectionMode()) {
                        setKeepPos(new KeepInSameScreenPosition());
                        MultipleSelection.performOnAll(selectedObjects, MultipleSelection.delete());
                        selectedObjects.clear(); //remove deleted items from the selection
//                    setupList(); //TODO optimize the application of a filter?
                        refreshAfterEdit(); //TODO optimize the application of a filter?
                    }
                }
            };

            Command cmdMoveSelectedToTopOfList = new Command("Move to top", Icons.iconSelectedLabelStyle) {
                @Override
                public void actionPerformed(ActionEvent evt) {
                    //TODO!!! warning that this cannot be undone!!
//                    if (isSelectionMode()&&!isSortOn()) {
                    if (isSelectionMode()) {
//                        setKeepPos(new KeepInSameScreenPosition()); //NOT needed, natural to move to top of list
                        MultipleSelection.performOnAll(selectedObjects, MultipleSelection.moveToTopOfList(itemListOrg));
//                    selectedObjects.clear(); //DO NOT unselect objects to allow for additional operations on selection
//                    setupList(); //TODO optimize the application of a filter?
                        DAO.getInstance().save(itemListOrg); //save after having moved items around
                        refreshAfterEdit(); //TODO optimize the application of a filter?
                    } else {
//                        ToastBar.showErrorMessage("Move to top not possible when list is shown sorted");
                    }
                    if (isSortOn()) {
                        ToastBar.showErrorMessage("[Move to top] moves tasks to top of manually sorted list, turn Sort off to see this"); //TODO 
                    }
                }
            };

            if (true) {
//            Button draggableOnOff = new Button();
                Command draggableOnOff = new Command("Move ON", Icons.iconMoveUpDownToolbarStyle) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        Container cont = getComponentForm().getContentPane();
                        for (int i = 0, size = cont.getComponentCount(); i < size; i++) {
                            Object o = cont.getComponentAt(i);
                            if (o instanceof Component) {
                                Component comp = (Component) o;
                                comp.setDraggable(!comp.isDraggable());
                                comp.setFocusable(!comp.isFocusable());
                            }
                        }
                        setCommandName(getCommandName().equals("Move ON") ? "Move OFF" : "Move ON");
                    }
                };
                toolbar.addCommandToOverflowMenu(draggableOnOff);

                toolbar.addCommandToOverflowMenu(new Command("Selection mode ON", Icons.iconSelectedLabelStyle) {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        if (!isSelectionMode()) {
//                    isSelectionMode() = true;
//                    selectedObjects = new HashSet(); //TODO keep a previous selection?
                            setSelectionMode(true);
                            setCommandName("Selection mode OFF");
                            //TODO!! put the selectionCommands into a separate menu (like overflow menu, with same icon as the selection symbol?)
                            toolbar.addCommandToOverflowMenu(cmdSetAnything);
                            toolbar.addCommandToOverflowMenu(cmdMoveSelectedToTopOfList);
                            toolbar.addCommandToOverflowMenu(cmdSelectAll);
                            toolbar.addCommandToOverflowMenu(cmdUnselectAll);
                            toolbar.addCommandToOverflowMenu(cmdInvertSelection);
                            toolbar.addCommandToOverflowMenu(cmdDeleteSelected);
                            ScreenListOfItems.this.refreshAfterEdit(); //TODO!!!! keep same position, OR: simply make existing containers expand by traversing the list and adding the selectionButton and removing afterwards (faster!)
//                        Component componentForm = getComponentForm();
//                        Component parent = getParent();
//                        if (parent != null) {
//                            Component componentForm2 = getParent().getComponentForm();
//                        }
                        } else {
//                    isSelectionMode() = false;
//                    selectedObjects = null;
                            setSelectionMode(false);
                            setCommandName("Selection mode ON");
                            //TODO!!!! use Toolbar.removeOverflowCommand(Command) once added (see http://stackoverflow.com/questions/39200432/how-to-remove-commands-added-to-overflow-menu)
//                    ToolbarSideMenu  menuBar = (ToolbarSideMenu )getToolbar().getMenuBar();
//                    menuBar.removeOverflowCommand(cmdSetAnything);
                            if (false) {
                                getToolbar().removeComponent(myTree); //TODO!!!! WHY was this code here??
                            }//                    ScreenListOfItems.this.removeCommand(cmdSetAnything);
//                    ScreenListOfItems.this.removeCommand(cmdMoveSelectedToTopOfList);
//                    ScreenListOfItems.this.removeCommand(cmdSelectAll);
//                    ScreenListOfItems.this.removeCommand(cmdUnselectAll);
//                    ScreenListOfItems.this.removeCommand(cmdInvertSelection);
//                    ScreenListOfItems.this.removeCommand(cmdDeleteSelected);
                            Toolbar toolbar = getToolbar();
                            toolbar.removeOverflowCommand(cmdSetAnything);
                            toolbar.removeOverflowCommand(cmdMoveSelectedToTopOfList);
                            toolbar.removeOverflowCommand(cmdSelectAll);
                            toolbar.removeOverflowCommand(cmdUnselectAll);
                            toolbar.removeOverflowCommand(cmdInvertSelection);
                            toolbar.removeOverflowCommand(cmdDeleteSelected);
                            ScreenListOfItems.this.revalidate(); //needed to make the commands actually disappear??
                            ScreenListOfItems.this.refreshAfterEdit();
                        }
                    }
                });
            }

        }
        //BACK
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());

        //TIMER
//        toolbar.addCommandToLeftBar(makeTimerCommand(itemList)); //use filtered/sorted ItemList for Timer //NO: doesn't work when itemList is updated
        if (!optionTemplateEditMode && !optionNoTimer) {
//            toolbar.addCommandToLeftBar(MyReplayCommand.create("ScreenTimer", "", Icons.iconTimerSymbolToolbarStyle, (e) -> {
            toolbar.addCommandToLeftBar(MyReplayCommand.create("ScreenTimer", "", FontImage.createMaterial(FontImage.MATERIAL_TIMER, UIManager.getInstance().getComponentStyle("TitleCommand")), (e) -> {
//                ScreenTimerNew.getInstance().startTimerOnItemList(itemListFilteredSorted, ScreenListOfItems.this);
//                    ScreenTimer.getInstance().startTimerOnItemList(itemListOrg, filterSortDef, ScreenListOfItems.this); //itemListOrg because Timer stores the original Parse objects and does its own filter/sort
//                    ScreenTimer.getInstance().startTimerOnItemList(itemListOrg, itemListOrg.getFilterSortDef(), ScreenListOfItems.this); //itemListOrg because Timer stores the original Parse objects and does its own filter/sort
                ScreenTimer.getInstance().startTimerOnItemList(itemListOrg, ScreenListOfItems.this); //itemListOrg because Timer stores the original Parse objects and does its own filter/sort
            }
            ));
        }

        //INTERRUPT TASK
        if (!optionTemplateEditMode && !optionNoInterrupt) {
//            toolbar.addCommandToRightBar(makeInterruptCommand());
            toolbar.addCommandToLeftBar(makeInterruptCommand());
        }

        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
    }

    /**
     * @param motherItemList the list from which item originates (null if none)
     * @param motherItem the item (project) list from which item originates / of
     * which it is a subtask (null if none)
     * @return
     */
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    protected Container buildItemContainer(Item item, ItemList motherItemList) {
    //        return buildItemContainer(item, motherItemList, null);
    //    }
    //
    //    protected Container buildItemContainer(Item item, Item motherItem) { //, ItemList itemList, ScreenListOfItems thisScreen) {
    //        return buildItemContainer(item, null, motherItem);
    //    }
    //
    //    protected Container buildItemContainer(Item item, ItemList motherItemList, Item motherItem) { //, ItemList itemList, ScreenListOfItems thisScreen) {
    //
    //        Container mainCont = new Container(new BorderLayout());
    //        Container bottomLeft = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    //
    //        MyDropContainer swipCont = new MyDropContainer(item, motherItemList, motherItem, bottomLeft, null, mainCont, ()-> draggableMode); //use filtered/sorted ItemList for Timer
    //        Label itemLabel = new Label(item.getText());
    //        mainCont.addComponent(CENTER, itemLabel);
    ////        MyDropHandler dropHandler = new MyDropHandler(item.getText(), item, motherItemList, motherItem, cont);
    ////        cont.setLeadComponent(dropHandler); // let the drophandler handle all events (to test if getLeadComponent() return null; works
    ////        cont.addComponent(CENTER, dropHandler);
    //
    //        //EDIT Item in list
    //        Button editItemButton = new Button() {
    //            @Override
    //            public void longPointerPress(int x, int y) {
    //                super.longPointerPress(x, y);
    //                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
    //            }
    //        };
    ////        Command editCmd = new Command(item.getText()) {
    //        Command editCmd = new Command("", iconEditSymbol) {
    //            @Override
    //            public void actionPerformed(ActionEvent evt) {
    ////                Item item = (Item) editItemButton.getClientProperty("item");
    //                Item item = (Item) mainCont.getClientProperty("item");
    //                new ScreenItem(item, ScreenListOfItems.this, () -> {
    //                    DAO.getInstance().save(item);
    ////                    dragAndDropContainer.replace(swipCont, buildItemContainer(item, motherItemList, motherItem), null); //update the container with edited content
    //                    dragAndDropContainer.replace(swipCont, buildTreeOrSingleItemContainer(item, motherItemList), null); //update the container with edited content
    //                }).show();
    ////                new ScreenItem(item, thisScreen).show();
    //            }
    //        };
    //        editItemButton.setCommand(editCmd);
    //        mainCont.putClientProperty("item", item);
    //        editItemButton.setUIID("Label");
    ////        editItemButton.setGrabsPointerEvents(true);
    //
    ////        cont.addComponent(CENTER, editItemButton);
    ////        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    ////        if (item.getPriority() != 0) {
    ////            west.add(new Label(item.getPriority() + ""));
    ////        } else {
    ////            west.add(new Label(" "));
    ////        }
    //        //WEST
    //        CheckBox itemStatusCheckBox = new CheckBox() {
    //            @Override
    //            public void longPointerPress(int x, int y) {
    //                super.longPointerPress(x, y);
    //                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
    //            }
    //        };
    //        itemStatusCheckBox.addActionListener((e) -> {
    ////                super.addActionListener(l); //To change body of generated methods, choose Tools | Templates.
    //            item.setDone(!item.isDone()); //invert status
    //            DAO.getInstance().save(item);
    //        });
    //
    //        itemStatusCheckBox.setSelected(item.isDone());
    //        Container west = Container.encloseIn(BoxLayout.x(),
    //                itemStatusCheckBox,
    //                item.getPriority() != 0 ? new Label(item.getPriority() + "") : new Label(" ")
    //        );
    //        mainCont.addComponent(WEST, west);
    //
    //        //EAST
    //        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
    //
    //        //EDIT subtasks in Item
    //        Button subTasksButton = new Button(iconShowMore) {
    ////            @Override
    ////            public Component getLeadComponent() {
    ////                return null;
    ////            }
    //        };
    //        if (item.getItemListSize() != 0) {
    //            Command expandSubTasks = new Command("[" + item.getItemListSize() + "]");// {
    //            subTasksButton.setCommand(expandSubTasks);
    //            swipCont.putClientProperty("subTasksButton", subTasksButton);
    //            subTasksButton.setUIID("Label");
    //            east.addComponent(subTasksButton);
    //        }
    //
    //        if (item.getDueDateD().getTime() != 0) {
    //            east.addComponent(new Label("D:" + L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
    //        }
    //
    //        //ALARM SET icon
    //        east.addComponent(new Label(item.getAlarmDate() != 0 ? iconAlarmSet : null));
    //
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////        long finishTime;
    ////        int idx;
    ////        if (motherItemList != null) {
    ////            idx = motherItemList.getItemIndex(item); //TODO optimize by passing index to here
    ////        } else {
    ////            idx = motherItem.getItemList().getItemIndex(item); //TODO optimize by passing index to here
    ////        }
    ////        ASSERT.that(idx>=0, "Item ["+item+"] not found in ItemList ["+itemList+"] as expected");
    ////        if ((finishTime = itemList.getFinishTime(idx)) != 0) {
    ////        WorkTimeDefinition wtd = itemList.getWorkTimeAllocatorN();
    ////        java.util.List workSlotList = itemListOrg.getWorkSlotListN();
    ////        if (workSlotList != null && workSlotList.size() > 0) {
    ////            WorkTimeDefinition wtd = new WorkTimeDefinition(itemListOrg.getWorkSlotListN(), itemList); //use possibly filtered/sorted list here. UI: finishTime is calcuated based on current view/filter/sort. E.g. if you always work by priority and not manual sorting
    ////        if (false) {
    ////            if (wtd != null && (finishTime = wtd.getFinishTime(idx)) != 0) {
    ////                east.addComponent(new Label("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime))));
    ////            }
    ////        }
    ////</editor-fold>
    //        east.addComponent(editItemButton);
    //
    //        mainCont.addComponent(EAST, east);
    //
    //        Container south = Container.encloseIn(BoxLayout.x(),
    //                new Label("(R:" + item.getRemainingEffortInMinutes() + "/A:" + item.getActualEffortInMinutes() + ")"),
    //                item.getHideUntilDateD().getTime() != 0 ? new Label("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD())) : new Label("")
    //        );
    //        if (motherItemList != null) {
    //            long finishTime;
    //            int idx = motherItemList.getItemIndex(item); //TODO optimize by passing index to here
    //            WorkTimeDefinition wtd = motherItemList.getSourceItemList().getWorkTimeAllocatorN();
    //            if (wtd != null && (finishTime = wtd.getFinishTime(idx)) != 0) {
    //                south.addComponent(new Label("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime))));
    //            }
    //        }
    //
    //        mainCont.addComponent(SOUTH, south);
    //
    //        bottomLeft.add(new Button("X")); //Create new task below
    //        if (false) {
    //            bottomLeft.add(new Button(iconTimerSymbol)); //Start Timer on this
    //            bottomLeft.add(new Button("Z")); //Move to top of list or 'start' or select(?)
    //            bottomLeft.add(new Button("Z")); //Set Waiting/Cancel/...
    //            bottomLeft.add(new Button("Z")); //Postpone due date?
    //            bottomLeft.add(new Button("Z")); //See details of task?
    //            bottomLeft.add(new Button("X")); //Edit(?) -> use [>] instead
    //        }
    //
    ////<editor-fold defaultstate="collapsed" desc="comment">
    ////        if (true) {
    ////        cont.setDraggable(true);
    ////        cont.setDropTarget(true);
    ////            return cont;//ignore Swipeable for the moment
    ////        } else {
    ////            SwipeableContainer swip = new SwipeableContainer(bottom, cont);
    ////            swip.addSwipeOpenListener(new ActionListener() {
    ////                @Override
    ////                public void actionPerformed(ActionEvent evt) {
    ////                    if (swip.isOpenedToRight()) {
    ////                        item.setDone(true);
    ////                    }
    ////                }
    ////            });
    ////            swip.setDraggable(true);
    ////            return swip;
    ////        }
    ////</editor-fold>
    ////        return cont;//ignore Swipeable for the moment
    //        return swipCont;//ignore Swipeable for the moment
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    class TreeItemList extends TreeInitialCollapse {
    //
    //        private int myDepthIndent = 15;
    ////        private TreeModel treeParent;
    ////            Tree dt = new Tree(listOfItemLists) {
    //
    ////        TreeItemList(ItemList listOfItemLists, boolean collapseTopLevelNode) {
    //        TreeItemList(TreeModel listOrItemOrListOfItemLists, TreeModel treeParent, boolean collapseTopLevelNode) {
    //            super(listOrItemOrListOfItemLists, treeParent, collapseTopLevelNode);
    ////            this.treeParent = treeParent;
    //            setNodeIcon(null);
    //            setFolderOpenIcon(iconShowLess);
    //            setFolderIcon(iconShowMore);
    //        }
    //
    //        @Override
    //        protected Component createNode(Object node, int depth) {
    //            Container cmp = null;
    //            Item item = (Item) node;
    ////            if (item.getItemListSize() > 0) {
    ////            cmp = buildItemContainer((Item) node, (Item) getModel());
    ////            if (treeParent != null) {
    //            if (treeParent instanceof Item) {
    //                cmp = buildItemContainer((Item) node, (Item) treeParent);
    //            } else if (treeParent instanceof ItemList) {
    //                cmp = buildItemContainer((Item) node, (ItemList) treeParent);
    //            } else {
    //                assert false : "treeParent should only be Item or ItemList: treeParent=" + treeParent;
    //            }
    ////            }
    ////            } else if (node instanceof Item) {
    ////                cmp = Container.encloseIn(BoxLayout.y(), new Label(((Item) node).getText())); //TODO!!! replace by appropriate container
    ////            } else {
    ////                assert false : "unknown type of node" + node;
    ////            }
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
    //            Object expandCollapseButton = node.getClientProperty("subTasksButton");
    ////            assert node.getClientProperty("subTasksButton") != null : "no subTaskButton defined";
    //            if (expandCollapseButton != null && expandCollapseButton instanceof Button) //            ((Button) (((Container) node).getClientProperty("subTasksButton"))).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
    //            {
    //                ((Button) (expandCollapseButton)).addActionListener(l); //in a tree of ItemLists there shall always be a subTasksButton
    //                ((Button) (expandCollapseButton)).putClientProperty("TreeContainer", node);
    //            }
    //        }
    //
    //        @Override
    //        protected void setNodeIcon(Image icon, Component node) {
    //            Object expandCollapseButton = node.getClientProperty("subTasksButton");
    //            if (expandCollapseButton != null && expandCollapseButton instanceof Button) //            ((Button) (((Container) node).getClientProperty("subTasksButton"))).setIcon(icon); //in a tree of ItemLists there shall always be a subTasksButton
    //            {
    //                ((Button) (expandCollapseButton)).setIcon(icon); //in a tree of ItemLists there shall always be a subTasksButton
    //            }
    //        }
    //    };
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    private Container buildTreeOrSingleItemContainer(Item item, ItemList listOfItems) {
    //        if (item.getItemListSize() > 0) {
    //            return new TreeItemList(item, listOfItems, true); //for a project, we'll always use Tree (since few lists will be empty)
    //        } else {
    //            return buildItemContainer(item, listOfItems);
    //        }
    //
    //    }
    //</editor-fold>
    /**
     * returns true if the list is displayed sorted (meaning eg that drag and
     * drop should not be enabled)
     *
     * @return
     */
    private boolean isSortOn() {
//        return filterSortDef == null || !filterSortDef.isSortOn();
//        return filterSortDef != null && filterSortDef.isSortOn();
//        return itemListFilteredSorted != itemListOrg; //NO - since list may be filtered and it's still possible to drag & drop
//        return filterSortDef != null && filterSortDef.isSortOn(); //
        return itemListOrg.getFilterSortDef() != null && itemListOrg.getFilterSortDef().isSortOn(); //
    }

    @Override
    protected boolean isDragAndDropEnabled() {
//        return !isSortOn();
//        return itemListOrg != null && filterSortDef.isSortOn(); //don't drag and drop in query lists with no underlying list //NOT relevant, itemListOrg will always be defined
//        return !optionDisableDragAndDrop && (filterSortDef == null || !filterSortDef.isSortOn()); //
        return !optionDisableDragAndDrop && !isSortOn(); //
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    Component makeNewInsertNewTaskContainerXXX(Item item, ItemList itemList) {
//        if (lastInsertNewTaskContainer != null && item == lastInsertNewTaskContainer.newItem) {
//            return new InsertNewTaskContainer(item, itemList);
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
    /**
     *
     * @param item
     * @param itemList
     * @param isDragAndDropEnabled
     * @param refreshOnItemEdits
     * @param selectionModeAllowed if true, show selection icons instead of task
     * check boxes
     * @param selectedObjects
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, HashSet<Item> selectedObjects) {
//        return buildItemContainer(item, itemList, isDragEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, null);
//    }
    //TODO remove this constructor, no longer used
//    public static Container buildItemContainer(Item item, ItemList itemList, MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, ArrayList<Item> selectedObjects) {
//        return buildItemContainer(item, itemList, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, null, null, null, null, false, false);
//    }
//
//    public static Container buildItemContainer(Item item) {
//        return buildItemContainer(item, null, () -> false, null, false, null, null, null, null, null, false, false);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, HashSet<Item> selectedObjects) {
//    private static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragAndDropEnabled,
//            MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category, KeepInSameScreenPosition keepPos) {
////    public static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, HashSet<Item> selectedObjects) {
////        return buildItemContainer(item, itemList, null, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, keepPos);
//        return buildItemContainer(item, itemList, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, keepPos);
//    }
//    public static Container buildItemContainer(Item item, List itemList, ItemList orgList, MyForm.GetBoolean isDragAndDropEnabled,
//            MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category, KeepInSameScreenPosition keepPos) {
//        return buildItemContainer(item, itemList, orgList, isDragAndDropEnabled, refreshOnItemEdits,
//                selectionModeAllowed, selectedObjects, category, keepPos, null);
//    }
//    public static Container buildItemContainer(Item item, List itemList, ItemList orgList,
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects) {
//        return buildItemContainer(item, itemList, orgList, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, keepPos, expandedObjects, null);
//    }
//    public static Container buildItemContainer(Item item, List itemList, ItemList orgList,
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator) {
//        return buildItemContainer(item, itemList, orgList, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, keepPos, expandedObjects, animator, false);
//    }
//    public static Container buildItemContainer(Item item, List itemList, ItemList orgList,
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator, boolean projectEditMode) {
//        return buildItemContainer(item, itemList, orgList, isDragAndDropEnabled, refreshOnItemEdits, selectionModeAllowed, selectedObjects, category, keepPos, expandedObjects, animator, projectEditMode, false);
//    }
//    public static Container buildItemContainer(Item item, List itemList, ItemList orgList, //TODO!!! remove orgList
//</editor-fold>
    {
    }

    interface SubtaskButtonFct {

        void action();
    }
//    public static Button makeSubtaskButton(Item item, Container swipCont) {

    public static Button makeSubtaskButton(Item item, SubtaskButtonFct longPressFunction) {
        int numberUndoneSubtasks = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
        int totalNumberSubtasks = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
//        int totalNumberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks; //true: get subtasks, always necessary for a project
        if (numberUndoneSubtasks > 0 || totalNumberSubtasks > 0) {
            Button subTasksButton = new Button() {
                @Override
                public void longPointerPress(int x, int y) {
                    super.longPointerPress(x, y);
                    if (longPressFunction != null) {
                        longPressFunction.action();
                    }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (swipCont != null) {
//                        //if event comes from eg a button inside the original node, get the original node
////            if (topContainer.getClientProperty(MyTree2.KEY_TOP_NODE) != null) {
////                topContainer = (Container) topContainer.getClientProperty(MyTree2.KEY_TOP_NODE);
////            };
//                        putClientProperty("LongPress", Boolean.TRUE); //is unset in MyTree2.Handler.actionPerformed()
//                        Log.p("longPointerPress");
//                        Object e = swipCont.getClientProperty(MyTree2.KEY_EXPANDED);
//                        MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(swipCont);
//                        if (e != null && e.equals("true")) {
////                        myTree.collapsePathNode(topContainer, true);
//                            myTree.collapseNode(swipCont, true);
//                        } else {
////                        myTree.expandPathNode(isInitialized(), topContainer, true);
//                            myTree.expandNode(false, swipCont, true);
//                        }
//                    }
//</editor-fold>
                }
            };
//            subTasksButton.setUIID("Label");
            subTasksButton.setUIID("ListOfItemsSubtasks");
//            subTasksButton.setGrabsPointerEvents(true); //TODO!!! does this work to avoid 
//            Command expandSubTasks = new Command("[" + numberUndoneSubtasks + "/" + totalNumberDoneSubtasks + "]");// {
//            Command expandSubTasks = new Command("[" + numberUndoneSubtasks + "/" + totalNumberSubtasks + "]");// {
            Command expandSubTasks = new Command(numberUndoneSubtasks + "/" + totalNumberSubtasks);// {
            subTasksButton.setCommand(expandSubTasks);
//            topContainer.putClientProperty("subTasksButton", subTasksButton);
//            if (swipCont != null) {
//                swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
//            }
            return subTasksButton;
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static Container buildItemContainer(final MyForm myForm, Item item) {
//        return buildItemContainer(myForm, item, null, null);
//    }
//
//    public static Container buildItemContainer(final MyForm myForm, Item item, ItemAndListCommonInterface orgList) {
//        return buildItemContainer(myForm, item, orgList, null);
//    }
//    public static Container buildItemContainer(final ScreenListOfItems myForm, Item item, ItemList orgList, //TODO!!! remove orgList
//    ItemAndListCommonInterface owner = (node instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) node).getOwner() != null)
//                        ? ((ItemAndListCommonInterface) node).getOwner() : listOfItems;
//                return createNode(node, depth, owner, category);
//            }
//
//            @Override
//            protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
////                    Container cmp = null;
////                    if (node instanceof Item) {
////                    Container cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItems.this, (Item) node, itemOrItemList, category);
////                    Container cmp = ScreenListOfItems.buildItemContainer((MyForm) this.getComponentForm(), (Item) node, itemOrItemList, category);
//                Container cmp = buildItemContainer(myForm, (Item) node, itemOrItemList, category);
//    public static Container buildItemContainer(final MyForm myForm, Item item, Category category){
//    ItemAndListCommonInterface owner = (item instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) item).getOwner() != null)
//                        ? ((ItemAndListCommonInterface) item).getOwner() : listOfItems;
//return buildItemContainer(myForm, item, owner, category);
//}
//</editor-fold>
    /**
     *
     * @param myForm MyForm used to provide display options and selected objects
     * etc - shortcut to avoid adding too many parameters to buildItemContainer
     * @param item the Item to display
     * @param ownerItemOrItemList the list to which the items belongs, can be
     * null
     * @param category the category, only need when building drag&drop
     * containers when displaying items in categories
     * @return
     */
    public static Container buildItemContainer(final MyForm myForm, Item item, ItemAndListCommonInterface ownerItemOrItemList, Category category) //<editor-fold defaultstate="collapsed" desc="comment">
    //            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
    //            boolean selectionModeAllowed, ArrayList<Item> selectedObjects,
    //            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator, boolean projectEditMode, boolean singleSelectionMode
    //</editor-fold>
    {
        ScreenListOfItems myFormScreenListOfItems = null;
        if (myForm instanceof ScreenListOfItems) {
            myFormScreenListOfItems = (ScreenListOfItems) myForm;
        }
        boolean oldFormat = false;
        boolean showInDetails = true;
        boolean isDone = item.isDone();
//    public static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, HashSet<Item> selectedObjects) {
        Container mainCont = new Container(new BorderLayout());
        mainCont.setUIID("ItemContainer");

        Container swipeActionContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        Container buttonSwipeContainer = null;
//        Container buttonSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
        Container buttonSwipeContainer = new Container(BoxLayout.x()); //must grow buttons to fill entire space
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (true) {
//            swipeActionContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        }
//        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, rightSwipeContainer, contWithAddNewTaskCont) {
//        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(swipeActionContainer, buttonSwipeContainer, contWithAddNewTaskCont) {
//</editor-fold>
        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(swipeActionContainer, buttonSwipeContainer, mainCont) {

//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//                return draggedObject.getDragAndDropObject() instanceof Item;
//            }
//            @Override
//            public ItemAndListCommonInterface getDragAndDropList() {
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (ownerList != null) {
////                    return ownerList;
////                } else {
//////                return ((Item)getDragAndDropObject()).getOwnerList(); //returns the owner of
////                    return item.getOwner(); //returns the owner of
////                }
////</editor-fold>
//                if (ownerItemOrItemList != null && ownerItemOrItemList instanceof Category) { //special case to allow drag&drop
//                    return ownerItemOrItemList;
//                } else {
//                    return item.getOwner(); //returns the owner of
//                }
//            }
//            @Override
//            public List getDragAndDropSubList() {
////                return ((Item) getDragAndDropObject()).getList(); //returns the list of subtasks
//                return item.getList(); //returns the list of subtasks
//            }
//</editor-fold>
            @Override
            public ItemAndListCommonInterface getDragAndDropObject() {
                return item;
            }

//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(item);
//            }
            @Override
            public Category getDragAndDropCategory() {
                return category;
//                return null;
            }

        }; //D&D
        if (Config.TEST) {
            swipCont.setName("Swipe|" + item.getText());
        }
        swipCont.setGrabsPointerEvents(true); //when swiping on task description, it also activated the button to show tasks details
        if (Config.TEST) {
            swipCont.setName(item.getText());
        }

        if (myForm.keepPos != null) {
            myForm.keepPos.testItemToKeepInSameScreenPosition(item, swipCont);
        }
//        if (keepPos!=null) keepPos.

        Container west = new Container(BoxLayout.x());
        if (oldFormat) {
            mainCont.addComponent(CN.WEST, west);
        }

        Container southDetailsContainer = new Container(new FlowLayout());
        southDetailsContainer.setUIID("ItemDetails");
        if (Config.TEST) {
            southDetailsContainer.setName("ItemDetails");
        }
//        boolean showDetails = MyPrefs.getBoolean(MyPrefs.showDetailsForAllTasks) || (myForm.expandedObjects != null && myForm.expandedObjects.contains(item)); //hide details by default
        boolean showDetails = MyPrefs.getBoolean(MyPrefs.showDetailsForAllTasks) || (myForm.showDetails != null && myForm.showDetails.contains(item)); //hide details by default
//        south.setHidden(!showDetailsForAllTasks || (tasksWithDetailsShown!=null && !tasksWithDetailsShown.contains(item))); //hide details by default
        southDetailsContainer.setHidden(!showDetails); //hide details by default
        if (oldFormat) {
            mainCont.addComponent(CN.SOUTH, southDetailsContainer);
        }

        Button expandSubTasksButton;// = null;//= new Button(); //null;
        //EXPAND subtasks in Item
//        Button subTasksButton = new Button(); //null;
//        if (true) {
//        int numberUndoneSubtasks = item.getNumberOfUndoneItems(true); //true: get subtasks, always necessary for a project
        int numberUndoneSubtasks = item.getNumberOfSubtasks(true, true); //true: get subtasks, always necessary for a project
        int totalNumberSubtasks = item.getNumberOfSubtasks(false, true); //true: get subtasks, always necessary for a project
//        int totalNumberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks; //true: get subtasks, always necessary for a project
//            if (numberUndoneSubtasks > 0 || totalNumberSubtasks > 0) {
        expandSubTasksButton = numberUndoneSubtasks > 0 || totalNumberSubtasks > 0 ? new Button() {
            @Override
            public void longPointerPress(int x, int y) {
                super.longPointerPress(x, y);
                //if event comes from eg a button inside the original node, get the original node
//            if (topContainer.getClientProperty(MyTree2.KEY_TOP_NODE) != null) {
//                topContainer = (Container) topContainer.getClientProperty(MyTree2.KEY_TOP_NODE);
//            };
                putClientProperty("LongPress", Boolean.TRUE); //is unset in MyTree2.Handler.actionPerformed()
                Log.p("longPointerPress");
                Object e = swipCont.getClientProperty(MyTree2.KEY_EXPANDED);
                MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(swipCont);
                if (e != null && e.equals("true")) {
//                        myTree.collapsePathNode(topContainer, true);
                    myTree.collapseNode(swipCont, true);
                } else {
//                        myTree.expandPathNode(isInitialized(), topContainer, true);
                    myTree.expandNode(false, swipCont, true);
                }
            }
//        } : new Button();
        } : null;
//            subTasksButton.setUIID("Label");
        if (expandSubTasksButton != null) {
            expandSubTasksButton.setUIID("ListOfItemsShowSubtasks");
//            subTasksButton.setGrabsPointerEvents(true); //TODO!!! does this work to avoid
//            Command expandSubTasks = new Command("[" + numberUndoneSubtasks + "/" + totalNumberDoneSubtasks + "]");// {
//            Command expandSubTasks = new Command("[" + numberUndoneSubtasks + "/" + totalNumberSubtasks + "]");// {
//                Command expandSubTasks = new Command(numberUndoneSubtasks + "/" + totalNumberSubtasks);// {
//                subTasksButton.setCommand(expandSubTasks);
            expandSubTasksButton.setText(numberUndoneSubtasks + "/" + totalNumberSubtasks);
//            topContainer.putClientProperty("subTasksButton", subTasksButton);
            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, expandSubTasksButton);
//            east.addComponent(subTasksButton);
            if (oldFormat) {
//                    east.addComponent(subTasksButton);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else {
////        Button showSubtasks = makeSubtaskButton(item, swipCont);
//            showSubtasksXXX = makeSubtaskButton(item, () -> {
//                if (swipCont != null) {
//                    //if event comes from eg a button inside the original node, get the original node
////                    this.putClientProperty("LongPress", Boolean.TRUE); //is unset in MyTree2.Handler.actionPerformed()
//                    Log.p("longPointerPress");
//                    Object e = swipCont.getClientProperty(MyTree2.KEY_EXPANDED);
//                    MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(swipCont);
//                    if (e != null && e.equals("true")) {
//                        myTree.collapseNode(swipCont, true);
//                    } else {
//                        myTree.expandNode(false, swipCont, true);
//                    }
//                }
//            });
//            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, showSubtasksXXX);
//
//            if (showSubtasksXXX != null) {
//                if (oldFormat) {
//                    east.addComponent(showSubtasksXXX);
//                }
//            }
//        }
//</editor-fold>
        //ITEM TEXT
        WorkSlotList wSlots = item.getWorkSlotListN(false);
        MyButtonInitiateDragAndDrop itemLabel = new MyButtonInitiateDragAndDrop(
                item.getText()
                + (((Config.TEST && MyPrefs.showDebugInfoInLabelsEtc.getBoolean()) && item.getRepeatRule() != null ? "*" : "")
                + ((Config.TEST && MyPrefs.showDebugInfoInLabelsEtc.getBoolean()) && item.isInteruptOrInstantTask() ? "<" : "")
                + ((Config.TEST && MyPrefs.showDebugInfoInLabelsEtc.getBoolean()) && wSlots != null && wSlots.size() > 0 ? "[W]" : "")
                //if showing Item
                //                + (item.getOwner() != null && !(item.getOwner().equals(orgList)) ? " /[" + item.getOwner().getText() + "]" : ""
                + (Config.TEST && MyPrefs.showDebugInfoInLabelsEtc.getBoolean() && item.getOwner() != null && item.getOwner() instanceof Item ? "^" : "" //show subtask with '^'
                )), swipCont, () -> {
                    boolean enabled = myForm.isDragAndDropEnabled();
                    if (enabled && expandSubTasksButton != null) {
                        Object e = swipCont.getClientProperty(KEY_EXPANDED);
                        if (e != null && e.equals("true")) { //                            subTasksButton.getCommand().actionPerformed(null);
                            expandSubTasksButton.pressed();//simulate pressing the button
                            expandSubTasksButton.released(); //trigger the actionLIstener to collapse
                        }
                    }
                    return enabled;
                }); //D&D
        itemLabel.setTextUIID(item.isDone() ? "ListOfItemsTextDone" : "ListOfItemsText");

        ActionListener showDetailsListener = (e) -> {
            //if showDetails is already true, run the listener immediately
            //if details container exists, remove it/make invisible
            int detailLevel = 0;
            if (detailLevel == 1) {

            }
        };

        itemLabel.addActionListener(new ActionListener() { //UI: touch task name to show/hide details
//        itemLabel.actualButton.addActionListener(new ActionListener() { //UI: touch task name to show/hide details
            @Override
            public void actionPerformed(ActionEvent evt) {
                southDetailsContainer.setHidden(!southDetailsContainer.isHidden()); //toggle hidden details
//                if (!south.isHidden()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (tasksWithDetailsShown == null) {
//                        tasksWithDetailsShown = new HashSet();
//                    }
//                    tasksWithDetailsShown.add(item);
//                } else if (tasksWithDetailsShown != null) {
//                    tasksWithDetailsShown.remove(item);
//                }
//</editor-fold>
                if (myForm.showDetails != null) {
                    if (southDetailsContainer.isHidden()) {
                        myForm.showDetails.remove(item);
                    } else {
                        myForm.showDetails.add(item);
                    }
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                swipCont.animateLayout(300); //moves center container down a bit, then redraws, NOT good
//                south.animateLayout(300); //no animation, simply redraws list with details added
//                mainCont.animateLayout(300); //NOT good
//                south.animateLayout(300); //NON
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                myTree.animateLayout(300); //YES! Pb with null pointer
//                if (animator != null) {
//                    animator.launchAction();
//                } else {
//                    mainCont.animateHierarchy(300);
//                };
//</editor-fold>
//                if (southDetailsContainer.getParent() != null) {
//                    southDetailsContainer.getParent().animateLayout(300);
//                }
                myForm.animateMyForm();
            }
        });
//        itemLabel.setUIID("ListOfItemsText");
        if (oldFormat) {
            mainCont.addComponent(CENTER, itemLabel);
        }

        //STATUS or SELECTED
//        if (isSelectionMode()) {
        Button selected = new Button(); //null;
        selected.setUIID("ListOfItemsSelected");
        if (myForm.selectedObjects != null) {
//            RadioButton selected = new RadioButton();
//            selected = new Button();
//            selected.setIcon(myForm.selectedObjects.contains(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
            selected.setIcon(myForm.selectedObjects.isSelected(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
            if (Config.TEST) {
                selected.setName("SelectBox");
            }
            selected.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent evt) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (myForm.singleSelectionMode) {
//                    if (myForm instanceof ScreenListOfItems && ((ScreenListOfItems)myForm).optionSingleSelectMode) {
//                        myForm.selectedObjects.clear(); //UI: add in order of selection
//                        myForm.selectedObjects.add(item); //UI: add in order of selection
//                    } else {
//                        if (myForm.selectedObjects.contains(item)) {
//                            myForm.selectedObjects.remove(item);
//                        } else {
//                            //TODO make it a setting whether operations on selected items are performed in order of selection or in order of appearance in the list (eg Move to top), with ArrayList: in order of selection
//                            myForm.selectedObjects.add(item); //UI: add in order of selection
//                        }
//                    }
//                    selected.setIcon(myForm.selectedObjects.contains(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
//</editor-fold>
                    myForm.selectedObjects.flipSelection(item);
                    selected.setIcon(myForm.selectedObjects.isSelected(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
                    selected.repaint();
                }
            });
            if (oldFormat) {
                west.add(selected);
            }
        } //else {
//        if (item.isTemplate()) {
//            west.add(new Label(Icons.iconTemplateStatusSymbolLabelStyle)); //NO, not nice with this symbol
//        } else 
        MyCheckBox status = null;
        if (myForm instanceof ScreenListOfItems && ((ScreenListOfItems) myForm).projectEditMode) {
            //TODO!!!! mark this task selected (and others unselected), or unselected if already selected
            //store the task somewhere to can be used to add subtasks to or sibling tasks after
        } else {
            status = new MyCheckBox(item.getStatus(), (oldStatus, newStatus) -> {
                if (newStatus != oldStatus) {
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont)); //keepPos since may be filtered after status change
                    myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont)); //keepPos since may be filtered after status change
                    item.setStatus(newStatus);
//                        if (refreshOnItemEdits != null) {
//                            refreshOnItemEdits.launchAction();
//                        }
                    myForm.refreshAfterEdit();
                    DAO.getInstance().save(item);
                    //TODO!!! optimize! Right now, refreshes entire Tree when anything in the tree changes
//                    item.addDataChangeListener((type, index) -> {if (type == DataChangedListener.CHANGED) {ItemContainer.TreeItemList2.getMyTreeTopLevelContainer(topContainer.getParent()).refresh();}});
                }
            }
            //                    , () -> {
            //                return item.hasWorkStarted(); //item.getActualEffort() > 0;
            //            }
            );
            status.setUIID("ListOfItemsMyCheckBox");
            if (Config.TEST) {
                status.setName("CheckBox");
            }
            if (oldFormat) {
                west.add(status);
            }
        }
//        }

        //EAST
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//        Container east = new Container(BoxLayout.x());
        FlowLayout eastLayout = new FlowLayout();
        eastLayout.setAlign(Component.RIGHT);
        eastLayout.setValign(Component.CENTER);
        eastLayout.setValignByRow(false);
        Container east = new Container(eastLayout);

        //STARRED
        final Button starButton = new Button(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
        final Button starredSwipeableButton = new Button(null, item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);

        final Button setDueDateToToday = new Button(null, Icons.iconSetDueDateToToday());

        starButton.setHidden(!item.isStarred() || isDone); //UI: hide star if task is done
        starButton.addActionListener((e) -> {
            item.setStarred(!item.isStarred());
            starButton.setHidden(true); //can only hide/unselect star in the line (need Swipe to set)
            starButton.repaint();
            starredSwipeableButton.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
            DAO.getInstance().save(item);
//            starred.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
        });
        if (oldFormat) {
            east.add(starButton);
        }

        long finishTime = item.getFinishTime();
//        if (!item.isDone() && finishTime != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself

        Container eastDateEffortCont = new Container(BoxLayout.y());

        //REMAINING EFFORT / ACTUAL EFFORT
        final Label actualEffortLabel = new Label(); //must be final for use in lambda, null;
        Label finishTimeLabel = null; //new Label(); //null;
        Label dueDateLabel = new Label(); //null;
        Label completedDateLabel = new Label(); //null;
        Label remainingEffortLabel = new Label(); //null;
        Button showSubtasksXXX = new Button(); //null;
//        Label remainingEffortLabel = null;
        if (true || isDone) {
            long actualEffort = item.getActualEffort();
            if (actualEffort != 0) {
//                east.addComponent(actualEffortLabel = new Label(MyDate.formatTimeDuration(actualEffort)));
//                actualEffortLabel = new Label();
//                actualEffortLabel.setText("A:" + MyDate.formatTimeDuration(actualEffort));
                actualEffortLabel.setText(MyDate.formatTimeDuration(actualEffort));
                actualEffortLabel.setUIID("ListOfItemsActualEffort");
                if (oldFormat) {
                    east.addComponent(actualEffortLabel);
                }
            }
            completedDateLabel = new Label("C:" + MyDate.formatDateNew(item.getCompletedDate()), "ListOfItemsCompletedDate");
            if (oldFormat) {
                east.addComponent(completedDateLabel);
            }
//        } else {
            long due = item.getDueDate();
            if (finishTime != MyDate.MAX_DATE) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
                finishTimeLabel = new Label("F:" + MyDate.formatDateSmart(new Date(finishTime)),
                        due != 0 && finishTime > due ? "ListOfItemsFinishTimeOverdue" : "ListOfItemsFinishTime");
                if (oldFormat) {
                    east.add(finishTimeLabel);
                }
            } //else 
            {
                if (due != 0) {
                    dueDateLabel = new Label("D:" + MyDate.formatDateSmart(new Date(due)),
                            due < System.currentTimeMillis() ? "ListOfItemsDueDateOverdue" : "ListOfItemsDueDate");
                    if (item.isDueDateInherited()) {
                        dueDateLabel.setUIID("ListOfItemsDueDateInherited");
                    }
                    dueDateLabel.setName("Due");
                    if (oldFormat) {
                        east.add(dueDateLabel);
                    }
                }
                long remainingEffort = item.getRemainingEffort();
                if (remainingEffort != 0 || MyPrefs.itemListShowRemainingEvenIfZero.getBoolean()) {
//                    east.addComponent(remainingEffortLabel = new Label(MyDate.formatTimeDuration(remainingEffort), "ListOfItemsRemaining"));
                    remainingEffortLabel = new Label(MyDate.formatTimeDuration(remainingEffort), "ListOfItemsRemaining");
                    remainingEffortLabel.setName("Remaining");
                    if (oldFormat) {
                        east.addComponent(remainingEffortLabel);
                    }
                }
            }
        }

        //TODO!!!! define lambde functions to refresh the parts of an Item container that may change when the 
//<editor-fold defaultstate="collapsed" desc="comment">
//        ActionListener t = (e) -> {
//            //Actual effort may be
//            if (actualEffortLabel != null) {
//                actualEffortLabel.setText(MyDate.formatTimeDuration(item.getActualEffort()));
//            }
////            if (remainingEffortLabel != null) {
////                remainingEffortLabel.setText(MyDate.formatTimeDuration(item.getRemainingEffort()));
////            }
//        };
//        if (keepPos != null) {
//            keepPos.testItemToKeepInSameScreenPosition(item, swipCont);
//        }
////        if (keepPos!=null) keepPos.
//</editor-fold>
//EDIT Item in list
        Button editItemButton = new Button() {
            @Override
            public void longPointerPress(int x, int y) {
                super.longPointerPress(x, y);
                //TODO activate drag&drop/move mode
                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
            }
        };

        final Image editItemIcon = FontImage.createMaterial(FontImage.MATERIAL_CHEVRON_RIGHT, UIManager.getInstance().getComponentStyle("ListOfItemsEditItemIcon"));

        Command editItemCmd = MyReplayCommand.create("EditItem-" + item.getObjectIdP(), "", editItemIcon, (e) -> {
            //TODO!!!! if same item appears in category, both as top-level item (added directly to category) AND as expanded subtask, two identical commands get created
//                Item item = (Item) mainCont.getClientProperty("item"); //TODO!!!! is this needed, why notjust access 'item'??
//                ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
            myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont));
            new ScreenItem(item, (MyForm) swipCont.getComponentForm(), () -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    KeepInSameScreenPosition keepPos = new KeepInSameScreenPosition(null, swipCont); //TODO!!!!!! porblem with access to this
//                    KeepInSameScreenPosition keepPos = new KeepInSameScreenPosition(); //TODO!!!!!! porblem with access to this
//                    Form f = new Form(item.getText());
//</editor-fold>
                //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
                if (false && item.isDirty()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                        myTree.refresh();
//                        refreshOnItemEdits.launchAction(); //refresh when item edited, eg update anything derived from estimates, subtasks, ...
//                        ((ScreenListOfItems) mainCont.getComponentForm()).refreshAfterEdit(keepPos);
//                        ((ScreenListOfItems) mainCont.getComponentForm()).refreshAfterEdit(new KeepInSameScreenPosition(item, swipCont));
//                        ((ScreenListOfItems) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//</editor-fold>
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                    myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                }
                DAO.getInstance().save(item);
//<editor-fold defaultstate="collapsed" desc="comment">
//NB. replacing swipCont will work even if swipCont is not updated since each container that replaces creates its own new
//                    swipCont.getParent().replace(swipCont, buildTreeOrSingleItemContainer(item, motherItemList, isDragEnabled), null); //update the container with edited content
//                    MyTree2.getMyTreeTopLevelContainer( swipCont).refresh(); //NOT necessary, done by changeListener on item
//                    refreshAfterEdit();
//                    if (refreshOnItemEdits != null) {
//                        refreshOnItemEdits.launchAction();
//                    }
//</editor-fold>
                myForm.refreshAfterEdit();
            }).show();
//                new ScreenItem(item, thisScreen).show();
        }
        );
        editItemButton.setCommand(editItemCmd);

//        mainCont.putClientProperty("item", item);
        swipCont.putClientProperty("element", item);
//        editItemButton.setUIID("IconEdit");
        editItemButton.setUIID("ListOfItemsEditItemIcon");
        editItemButton.setName("EditTask");
//        editItemButton.setGrabsPointerEvents(true);
        if (oldFormat) {
            east.addComponent(editItemButton);
        }

        if (oldFormat) {
            mainCont.addComponent(CN.EAST, east);
        }

        //SOUTH
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (motherListWithWorkTimeDef != null && motherListWithWorkTimeDef instanceof ItemList) {
//            long finishTime;
////            int idx = motherItemList.getItemIndex(item); //TODO optimize by passing index to here
//            int idx = motherListWithWorkTimeDef.indexOf(item); //TODO!!! optimization by passing index to here
////            WorkTimeDefinition wtd = motherItemList.getSourceItemList().getWorkTimeAllocatorN();
//            WorkTimeDefinition wtd = ((ItemList) motherListWithWorkTimeDef).getSourceItemList().getWorkTimeAllocatorN();
//            if (wtd != null && (finishTime = wtd.getFinishTime(idx)) != 0) {
//                south.add("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime)));
////                south.add("F:" + MyDate.formatDateCasual(new Date(finishTime)));
//            }
//        }
//        if (wtd != null && itemList != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) {
//        if (wtd != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) {
//        if (!item.isDone() && wtd != null && itemList != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            long finishTime;
////            if (wtd != null && !item.isDone() && orgItemOrItemList != null && (finishTime = wtd.getFinishTime(orgItemOrItemList.indexOf(item))) != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
//            if (wtd != null && !item.isDone() && orgItemOrItemList != null && (finishTime = wtd.getFinishTime(orgItemOrItemList.getItemIndex(item))) != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
////                south.add("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime)));
//                southDetailsContainer.add("F:" + MyDate.formatDateNew(finishTime));
//            }
//        }
//</editor-fold>
        //WORK TIME
//<editor-fold defaultstate="collapsed" desc="comment">
//        long finishTime = item.getFinishTimeD().getTime();
//        long finishTime = item.getFinishTime();
//        if (!item.isDone() && finishTime != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
//</editor-fold>
        if (false && !isDone && finishTime != MyDate.MAX_DATE) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
//            south.add("F:" + L10NManager.getInstance().formatDateTimeShort(item.getFinishTime()));
            southDetailsContainer.add("F:" + MyDate.formatDateTimeNew(new Date(finishTime)));
        }
        //PRIORITY
        Label priorityLabel = new Label();
//        priorityLabel.setUIID("ItemDetailsLabel");
        if (Config.TEST) {
            priorityLabel.setName("Priority");
        }
        if (item.getPriority() != 0) {
            priorityLabel.setText("P" + item.getPriority());
            if (false && showInDetails) {
                southDetailsContainer.add(priorityLabel);
            }
            priorityLabel.setUIID("ListOfItemsPrio");
        }
        //IMPORTANCE/URGENCY
        Label impUrgLabel; // = new Label();
        impUrgLabel = new Label(item.getImpUrgPrioValueAsString(), "ListOfItemsImpUrg");
        if (Config.TEST) {
            impUrgLabel.setName("ImpUrg");
        }
//        impUrgLabel.setUIID("ListOfItemsImpUrg");
        if (false && showInDetails) {
            southDetailsContainer.add(impUrgLabel);
        }

        //DREAD/FUN
        if (item.getDreadFunValue() != null) {
            Label funDreadLabel; // = new Label();
            funDreadLabel = new Label(item.getDreadFunValue().toString(), "ItemDetailsLabel");
            if (Config.TEST) {
                funDreadLabel.setName("FunDread");
            }
            if (showInDetails) {
                southDetailsContainer.add(funDreadLabel);
            }
        }

        //CHALLENGE
        if (item.getChallenge() != null) {
            Label challengeLabel; // = new Label();
            challengeLabel = new Label(item.getChallenge().toString(), "ItemDetailsLabel");
            if (Config.TEST) {
                challengeLabel.setName("Challenge");
            }
            if (showInDetails) {
                southDetailsContainer.add(challengeLabel);
            }
        }

        //DUE DATE or COMPLETED DATE
        if (false && !isDone) {
            if (item.getDueDateD().getTime() != 0) {
//            south.addComponent(new Label("D:" + L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
//            south.addComponent(new Label("D:" + MyDate.formatDateNatural(new MyDate(new Date(item.getDueDate())),MyDate.FORMAT_CASUAL, false)));
//                south.addComponent(new Label("D:" + MyDate.formatDateNatural(new MyDate(item.getDueDate()), new MyDate(), MyDate.FORMAT_CASUAL, false)));
                Label dueLabel = new Label("D:" + MyDate.formatDateNew(item.getDueDate()),
                        item.getDueDate() < System.currentTimeMillis() ? "ListOfItemsDueDateOverdue" : "ListOfItemsDueDate");
                dueLabel.setName("DueDate");
                southDetailsContainer.addComponent(dueLabel);
            }
        }
//        else {
////            south.addComponent(new Label("C:" + MyDate.formatDateNatural(new MyDate(item.getCompletedDate()), new MyDate(), MyDate.FORMAT_CASUAL, false)));
//            southDetailsContainer.addComponent(new Label("C:" + MyDate.formatDateNew(item.getCompletedDate()), "CompletedDate"));
//        }

        //ALARM SET icon
        Style s = UIManager.getInstance().getComponentStyle("ListOfItems");
//        final Image iconAlarmSetLabelStyle = FontImage.createMaterial(FontImage.MATERIAL_ALARM_ON, s);
        Label alarmLabel = null;
        if (item.getAlarmDate() != 0) {
//            south.addComponent(new Label((Image) (item.getAlarmDate() != 0 ? Icons.get().iconAlarmSetLabelStyle : null)));
            alarmLabel = new Label(MyDate.formatDateTimeNew(item.getAlarmDateD()), (Image) Icons.get().iconAlarmSetLabelStyle, "ItemDetailsLabel");
            if (Config.TEST) {
                alarmLabel.setName("Alarm");
            }
//            alarmLabel.getStyle().setAlignment(Component.RIGHT);
            if (showInDetails) {
                southDetailsContainer.addComponent(alarmLabel);
            }
        }

        //                new Label("(R:" + item.getRemainingEffortInMinutes() + "/A:" + item.getActualEffortInMinutes() + ")"),
        //HIDE UNTIL
        Label hideUntilLabel = new Label();
        if (item.getHideUntilDateD().getTime() != 0 && MyPrefs.itemListAlwaysShowHideUntilDate.getBoolean()) {
//            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
            hideUntilLabel = new Label("H:" + MyDate.formatDateNew(item.getHideUntilDateD()), "ItemDetailsLabel");
            if (Config.TEST) {
                hideUntilLabel.setName("HideUntil");
            }
            if (showInDetails) {
                southDetailsContainer.addComponent(hideUntilLabel);
            }
        }
        //START BY
        Label startByLabel = new Label();
        if (item.getStartByDateD().getTime() != 0 && MyPrefs.itemListAlwaysShowStartByDate.getBoolean()) {
//            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
            startByLabel = new Label("S:" + MyDate.formatDateNew(item.getStartByDateD()), "ItemDetailsLabel");
            if (Config.TEST) {
                startByLabel.setName("StartBy");
            }
            if (showInDetails) {
                southDetailsContainer.addComponent(startByLabel);
            }
        }
        //EXPIRE BY
        Label expireByLabel = new Label();
        if (item.getExpiresOnDateD().getTime() != 0 && MyPrefs.itemListExpiresByDate.getBoolean()) {
//            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
            expireByLabel = new Label("E:" + MyDate.formatDateNew(item.getExpiresOnDateD()), "ItemDetailsLabel");
            if (Config.TEST) {
                expireByLabel.setName("ExpireBy");
            }
            if (showInDetails) {
                southDetailsContainer.addComponent(expireByLabel);
            }
        }
        //WAITING
        Label waitingTillLabel = new Label();
        if (item.getWaitingTillDateD().getTime() != 0 && MyPrefs.itemListWaitingTillDate.getBoolean()) {
//            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
            waitingTillLabel = new Label("W:" + MyDate.formatDateNew(item.getWaitingTillDateD()), "ItemDetailsLabel");
            if (Config.TEST) {
                waitingTillLabel.setName("WaitingTill");
            }
            if (showInDetails) {
                southDetailsContainer.addComponent(waitingTillLabel);
            }
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//ACTUAL
//        long actual = item.getActualEffort();
//        if (actual != 0) {
//            String s;
//            if (actual / MyDate.MINUTE_IN_MILLISECONDS > 0) {
//                s = MyDate.formatTimeDuration(item.getActualEffort());
//            } else {
//                s = MyDate.formatTimeDuration(item.getActualEffort(), true);
//            }
//            actualEffortLabel=new Label("A:" + s));
//            if (oldFormat) southDetailsContainer.addComponent(new Label("A:" + s));
//        }
//CATEGORIES
//                            categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "<set>")); //"<click to set categories>"
//</editor-fold>
        List cats = item.getCategories();
        if (cats != null && cats.size() > 0) {
            SpanLabel catsLabel = new SpanLabel("Cat: " + getListAsCommaSeparatedString(cats), "ItemDetailsLabel");
            if (Config.TEST) {
                catsLabel.setName("Categories");
            }
            southDetailsContainer.addComponent(catsLabel);
        }

        Component effortCont = isDone ? actualEffortLabel : BoxLayout.encloseX(remainingEffortLabel, actualEffortLabel);
        Component dateCont = isDone ? completedDateLabel : (finishTimeLabel != null ? finishTimeLabel : dueDateLabel);
        Component prioCont = BoxLayout.encloseX(priorityLabel, impUrgLabel);
        Component expandSubsCont = expandSubTasksButton != null ? expandSubTasksButton : new Label();
        //BUILD CONTAINER
        Container itemContent = new Container(new BorderLayout());
        Container bottomContent = new Container(new BorderLayout());
//        Container mainItemCont = new Container(new BorderLayout())
        mainCont
                .add(CN.WEST, BoxLayout.encloseX(selected, status))
                .add(CN.EAST, editItemButton)
                .add(CENTER,
                        itemContent.add(CENTER, BorderLayout.west(itemLabel)) //item text + expand subtasks
                                .add(CN.SOUTH,
                                        bottomContent
                                                //                                                .add(WEST, BorderLayout.centerEastWest(null, null, BoxLayout.encloseX(prioCont, dateCont,effortCont) ))
                                                .add(CN.WEST, BoxLayout.encloseX(prioCont, dateCont, effortCont))
                                                .add(CN.EAST, expandSubsCont)
                                                .add(CN.SOUTH, southDetailsContainer)));
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container eastCont = new Container(BoxLayout.x())
//                .add(BorderLayout.east(BoxLayout.encloseX(
//                        BoxLayout.encloseY(
//                                BorderLayout.east(isDone ? actualEffortLabel : BoxLayout.encloseX(remainingEffortLabel, actualEffortLabel)),
//                                //                                finishTimeLabel != null ? finishTimeLabel : (isDone ? completedDateLabel : dueDateLabel)),
//                                BorderLayout.east(isDone ? completedDateLabel : (finishTimeLabel != null ? finishTimeLabel : dueDateLabel))),
//                        //                        BoxLayout.encloseY(starButton, subTasksButton),
//                        expandSubTasksButton != null ? expandSubTasksButton : new Label(),
//                        editItemButton))
//                );

//        Container topBorderCont = new Container(new BorderLayout());
//        topBorderCont.add(BorderLayout.WEST, statusSelectionCont)
//                .add(CENTER, itemLabel)
//                .add(EAST, eastCont)
//                .add(SOUTH, southDetailsContainer);
//        mainCont.add(CENTER, topBorderCont);
//        //BUILD CONTAINER
//        Container statusSelectionCont = new Container(new BorderLayout())
//                .add(WEST, selected)
//                .add(EAST, status);
//        Container eastCont = new Container(BoxLayout.x())
//                .add(BorderLayout.east(BoxLayout.encloseX(
//                        BoxLayout.encloseY(
//                                BorderLayout.east(isDone ? actualEffortLabel : BoxLayout.encloseX(remainingEffortLabel, actualEffortLabel)),
//                                //                                finishTimeLabel != null ? finishTimeLabel : (isDone ? completedDateLabel : dueDateLabel)),
//                                BorderLayout.east(isDone ? completedDateLabel : (finishTimeLabel != null ? finishTimeLabel : dueDateLabel))),
//                        //                        BoxLayout.encloseY(starButton, subTasksButton),
//                        expandSubTasksButton != null ? expandSubTasksButton : new Label(),
//                        editItemButton))
//                );
//
//        Container topBorderCont = new Container(new BorderLayout());
//        topBorderCont.add(WEST, statusSelectionCont)
//                .add(CENTER, itemLabel)
//                .add(EAST, eastCont)
//                .add(SOUTH, southDetailsContainer);
//
//        mainCont.add(CENTER, topBorderCont);
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
/*
private Component makeQuickAddBox(boolean projectEditMode) {

cont.add(EAST, new Button(Command.create(null, Icons.get().iconEditSymbolLabelStyle, (e) -> {
//            ((MyForm) getParent().getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
String taskText = taskTextEntryField.getText();
if (taskText != null && taskText.length() > 0) {
Item newItem = new Item(taskText);
new ScreenItem(newItem, (MyForm) getComponentForm(), () -> {
DAO.getInstance().save(newItem);
//TODO!!!! if a task is selected in projectEditMode, add this new task *after* it

addNewTaskSetTemplateAddToListAndSave(newItem, (MyPrefs.insertNewItemsInStartOfLists.getBoolean()) && (!projectEditMode) ? 0 : itemListOrg.getSize());
refreshAfterEdit();
}).show();
}
})));
        //TODO!!!! add button to add as subtasks to the selected tasks (if any), otherwise add in default position for new tasks
        return cont;
    }
         */
//</editor-fold>
        //SWIPEABLE INSERT TASK/SUBTASK
//        if (myForm instanceof ScreenItem || myForm instanceof ScreenListOfItems) { //TODO!!!! only activate in manually sorted (and persisted) lists
        boolean insertSwipeNewTaskCont = (myForm instanceof ScreenListOfItems && !((ScreenListOfItems) myForm).isSortOn())
                || (myForm instanceof ScreenItem); //TODO!!!! only activate in manually sorted (and persisted) lists
        if (false && insertSwipeNewTaskCont) { //TODO!!!! only activate in manually sorted (and persisted) lists
//            buttonSwipeContainer.add(new Label("  "));
            swipeActionContainer.add(new Label("  "));
            //TODO!!!! can projectEditMode only be used/launched on a top-level project or also on a sub-project?? (if sub-project, must find another way to test if trying to move the top-level)
            swipCont.addSwipeOpenListener((e) -> {
                if (swipCont.isOpenedToRight()) { //Insert newTask container
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (newTaskContainer != null ) { //if there is a previous newTask container remove it
//                        if (newTaskContainer.getParent()!=null)newTaskContainer.getParent().removeComponent(newTaskContainer); //should be animated away at same time as adding new one below
//                        newTaskContainer = null;
//                    }
//                    Object oldNewTaskCont = swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER);
//                    if (oldNewTaskCont != null && oldNewTaskCont instanceof Container) { //if there is a previous newTask container remove it
//                        Container oldNewTaskContC = (Container) oldNewTaskCont;
//                        if (oldNewTaskContC.getParent() != null) {
//                            oldNewTaskContC.getParent().removeComponent(oldNewTaskContC); //should be animated away at same time as adding new one below
//                        }
//                        swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null); //delete when closing the insertTask container
//                        swipCont.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //delete when closing the insertTask container
//                    }
//                    Log.p("Swipe left");
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (false) {
//                        //When swiping left, create a new container below with a textEntry field (and a status?), and a (x) to close it w/o creating a task.
//                        //When pressing Enter, add a new task (using keepPos) and create a new container below to add another task
//                        //When swiping this container right, create a subtasks to the preceding task
//                        //When swiping this container left, if the container corresponds to a subtask, change it to a top task
//                        Container cont = new Container(new BorderLayout());
//
//                        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Super task"), cont);
//                        newTaskContainer = swipC;
//
//                        swipC.addSwipeOpenListener((ev) -> {
//                            if (swipC.isOpenedToLeft()) {
//                                swipC.putClientProperty(SUBTASK_LEVEL_KEY, true);
//                            } else if (swipC.isOpenedToRight()) {
////                            swipC.putClientProperty(SUBTASK_LEVEL_KEY, false);
//                                swipC.putClientProperty(SUBTASK_LEVEL_KEY, null);
//                            }
//                        });
//
//                        //Text
//                        MyTextField2 taskTextEntryField2 = new MyTextField2();
//                        taskTextEntryField2.setHint("New task");
//                        taskTextEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE);
//                        taskTextEntryField2.addActionListener((ev) -> {
//                            //When pressing ENTER
//                            String taskText = taskTextEntryField2.getText();
//                            taskTextEntryField2.setText(""); //clear text
//                            if (taskText != null && taskText.length() > 0) {
//                                Item newItem = new Item(taskText);
//                                refreshOnItemEdits.launchAction();
//                                DAO.getInstance().save(newItem);
//                                if (((Boolean) swipC.getClientProperty(SUBTASK_LEVEL_KEY)) == true) { //add task after previous
//                                    //make a sistertask (insert in same list as item, after item)
//                                    if (orgList != null && orgList.indexOf(item) != -1) {
//                                        int index = orgList.indexOf(item);
//                                        orgList.addItemAtIndex(newItem, index + 1);
//                                        swipCont.close(); //close swipe container after swipe action
//                                        refreshOnItemEdits.launchAction();
//                                    }
//                                } else { //add as subtask to previous task, and keep the subtask level
//                                    //make a subtask
//                                    item.addToList(0, newItem);
//                                    swipCont.close(); //close swipe container after swipe action
//                                    refreshOnItemEdits.launchAction();
//                                    swipC.putClientProperty(SUBTASK_LEVEL_KEY, null); //remove the subtask property so next task does not become a subtask to the subtask
//                                }
//                            }
//                        });
//
//                        cont.add(WEST, new Label(Icons.iconCheckboxCreated));
//                        cont.add(CENTER, taskTextEntryField2);
//                        cont.add(EAST, new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
//                            //close the container
//                            swipC.getParent().removeComponent(swipC);
//                            newTaskContainer = null;
//                            swipC.getComponentForm().animateHierarchy(300);
//                        })));
//                    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    //find place to insert container
//                    Container swipContParent = swipCont.getParent();
////                    Container swipContParentParent = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    Container contForInsertTask = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
////                    Container contForInsertTask = swipContParentParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    ASSERT.that(contForInsertTask instanceof MyTree2, "ERROR - not right place to insert insertTaskContainer, cont=" + contForInsertTask);
//                    ((ScreenListOfItems) myForm).indexOfSwipContParentParent = contForInsertTask.getComponentIndex(swipContParent);
//                    Container contWithAddNewTaskCont = new Container(new BorderLayout()); //container to hold the AddNewTask container after the item
//                    contWithAddNewTaskCont.add(CENTER, mainCont);

//                    SwipeableContainer swipC = buildAddNewTaskContainer(item, orgList, swipCont, refreshOnItemEdits);
//                     myForm.lastInsertSwiptCreatedItem = new Item();
//                    if (myForm.getClientProperty(EXISTING_NEW_TASK_CONTAINER) != null) {
//                        ((Container) swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER)).getParent().removeComponent((Container) swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER));
//                    int previousIndex = 0;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (((ScreenListOfItems) myForm).lastInsertNewTaskContainer != null) {
////                        myFormScreenListOfItems.lastInsertNewTaskContainer.getParent().removeComponent(myFormScreenListOfItems.lastInsertNewTaskContainer); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                        //NB! remove old container before calculating index, otherwise it may be one off if old container was before new one!!
////                        previousIndex = ((ScreenListOfItems) myForm).indexOfSwipContParentParent;
////                        ((ScreenListOfItems) myForm).lastInsertNewTaskContainer.getParent().removeComponent(((ScreenListOfItems) myForm).lastInsertNewTaskContainer); //if there is a previous container somewhere (not removed/closed by user), then remove when creating a new one
//                        ((ScreenListOfItems) myForm).lastInsertNewTaskContainer.closeInsertNewTaskContainer();
//                    }
//                    InsertNewTaskContainer insertNewTaskContainer = new InsertNewTaskContainer(item, orgItemOrItemList, myForm);
//</editor-fold>
//                    InlineInsertNewElementContainer insertNewTaskContainer = new InlineInsertNewElementContainer(myForm, item, ownerItemOrItemList);
                    InsertNewElementFunc insertNewTaskContainer = new InlineInsertNewItemContainer2(myForm, item, ownerItemOrItemList);
//                    ((ScreenListOfItems) myForm).lastInsertNewTaskContainer = insertNewTaskContainer;
//<editor-fold defaultstate="collapsed" desc="comment">
//find place to insert container
//                    Container swipContParent = swipCont.getParent();
////                    Container swipContParentParent = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    Container contForInsertTask = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
////                    Container contForInsertTask = swipContParentParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    ASSERT.that(contForInsertTask instanceof MyTree2, "ERROR - not right place to insert insertTaskContainer, cont=" + contForInsertTask);
//                    Container contForInsertTask = MyTree2.getListContainer(swipCont);
//</editor-fold>
                    ListAndIndex listAndIndex = MyTree2.getListContainer(swipCont);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    int indexOfSwipContParentParent = contForInsertTask.getComponentIndex(swipContParent);
//                    myForm.putClientProperty(EXISTING_NEW_TASK_CONTAINER, swipC);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    Container parent = swipCont.getParent();
//                    int indexOfSwipContParentParent = parent.getComponentIndex(swipCont);
//                    parent.addComponent(index+1, swipC);
//                    contWithAddNewTaskCont.add(SOUTH, swipC);
//                    int adjustPos = ((ScreenListOfItems) myForm).lastInsertNewTaskContainer != null
//                            ((ScreenListOfItems) myForm).lastInsertNewTaskContainer.contForInsertTask.addComponent(((ScreenListOfItems) myForm).indexOfSwipContParentParent + 1, newTaskContainer);
//                    contForInsertTask.addComponent(indexOfSwipContParentParent + 1, newTaskContainer);
//</editor-fold>
//                    ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//                    ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//</editor-fold>
                    myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                    listAndIndex.list.addComponent(listAndIndex.index + 1, (Component) insertNewTaskContainer);
//                    myForm.setEditOnShow(insertNewTaskContainer.getTextField());
//<editor-fold defaultstate="collapsed" desc="comment">
//                    ((ScreenListOfItems) myForm).indexOfSwipContParentParent = newTaskContainer;
//                    contWithAddNewTaskCont.getComponentForm().animateHierarchy(300);
//                    contWithAddNewTaskCont.getParent().animateHierarchy(300);
//                    contForInsertTask.animateHierarchy(300);
//                    contForInsertTask.animateHierarchy(300);
//                    res.list.animateHierarchy(300);
//</editor-fold>
//                    myForm.animateHierarchy(300);
                    if (false) {
                        myForm.animateMyForm(); //DOES this redraw the entire screen?
                    }
//                    insertNewTaskContainer.setTextFieldEditableOnShow(myForm);
                    myForm.restoreKeepPos(); //THIS IS NEEDED to keep the position
//                    myForm.scrollComponentToVisible(insertNewTaskContainer); //necessary in the (rare) case that an expanded project w many subtasks is swiped so insertTask is added after the subtasks, outside the visible screen
//                    myForm.show(); //TODO: need to 
                }
//                else if (swipCont.isOpenedToRight()) {
//                    Log.p("Swipe right");
//                }
            });
        }
//        } //else {
        //TIMER
//<editor-fold defaultstate="collapsed" desc="comment">
//only add swipe buttons when NOT in projectEditMode
//        bottomLeft.add(new Button(Icons.get().iconTimerSymbolLabelStyle)); //Start Timer on this
//            if (!item.isTemplate() && !item.isDone()) {
//</editor-fold>
        if (true || myFormScreenListOfItems == null || !myFormScreenListOfItems.projectEditMode) {
//                    buttonSwipeContainer.add(new Button(new Command(null, Icons.iconTimerSymbolToolbarStyle) {
//            Button startTimer = new Button(MyReplayCommand.create("StartTimer-" + item.getObjectIdP(), null, Icons.iconTimerSymbolToolbarStyle, (ev) -> {
            Button startTimer = new Button(MyReplayCommand.create("StartTimer-" + item.getObjectIdP(), null, Icons.iconTimerSymbolToolbarStyle(), (ev) -> {
//                        @Override
//                        public void actionPerformed(ActionEvent evt) {
//                ScreenTimerNew.getInstance().startTimerOnItemList(itemListFilteredSorted, ScreenListOfItems.this);
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont));
                ScreenTimer.getInstance().startTimerOnItem(item, (MyForm) swipCont.getComponentForm(), true);
//                        }
            }));
            startTimer.setUIID("SwipeButtonTimer");
            buttonSwipeContainer.add(startTimer);
        } else { // item.isTemplate()
            Button newFromTemplate = new Button(MyReplayCommand.create("NewItemFromTemplate", null, Icons.iconNewItemFromTemplate, (e) -> {
                Item newTemplateInstantiation = new Item();
                item.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
                new ScreenItem(newTemplateInstantiation, (MyForm) swipCont.getComponentForm(), () -> {
                    DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner, saved to 'inbox'
                    ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(newTemplateInstantiation));
//                            refreshOnItemEdits.launchAction(); //NOT necessary, since item not saved in list of templates
                }).show();
            }));
//                    newFromTemplate.setUIID("SwipeButton");
            newFromTemplate.setUIID("SwipeButtonNewFromTemplate");
            buttonSwipeContainer.add(newFromTemplate);
//            }
        }

        if (true || !((ScreenListOfItems) myForm).projectEditMode) {
            //STARRED
            if (!item.isTemplate() && !isDone) {
//            Button starredSwipeable = new Button(null, item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
                starredSwipeableButton.addActionListener((e) -> {
                    item.setStarred(!item.isStarred()); //flip the starred value
                    //update the starred button
                    starButton.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
                    starButton.setHidden(!item.isStarred());
                    starredSwipeableButton.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//            starred.getParent().revalidate();
                    //update and save the item
                    //update the starredSwipeable button
//            starredSwipeable.repaint();
                    swipCont.close(); //close before save 
//                        myForm.revalidate();
                    swipCont.revalidate();
                    DAO.getInstance().save(item);
                });
                starredSwipeableButton.setUIID("SwipeButtonStar");
                buttonSwipeContainer.add(starredSwipeableButton);
            }
        }

        //UPDATE DUE DATE
        if (!item.isTemplate() && !isDone) {
            setDueDateToToday.addActionListener((e) -> {
//                myForm.setKeepPos(new KeepInSameScreenPosition(item, swipCont)); //NB keeping the position of item doesn't make sense since this command can make it disappear (e.g. in Overdue screen)
//                myForm.setKeepPos(new KeepInSameScreenPosition(swipCont)); //ASSERT since swipCont not ScrollableY
                myForm.setKeepPos(new KeepInSameScreenPosition()); //try to keep same scroll position as before 
                Date tomorrow = new Date(new Date().getTime() + MyDate.DAY_IN_MILLISECONDS);
                if (MyDate.isToday(item.getDueDateD())) {
//                        item.setDueDate(MyDate.setDateToDefaultTimeOfDay(new Date(item.getDueDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
                    item.setDueDate((new Date(item.getDueDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
                } else if (MyDate.isToday(item.getWaitingTillDateD())) {
                    item.setWaitingTillDate((new Date(item.getWaitingTillDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if WaitingTillDate is today, then set to tomorrow
                } else if (MyDate.isToday(item.getStartByDateD())) {
                    item.setStartByDate((new Date(item.getStartByDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
                } else {
//                        item.setDueDate(MyDate.setDateToDefaultTimeOfDay(new Date())); //UI: if due is NOT already today, then set due day to today
                    item.setDueDate(MyDate.setDateToTodayKeepTime(item.getDueDateD())); //UI: if due is NOT already today, then set due day to today
                }                    //update and save the item
                DAO.getInstance().save(item);
                swipCont.close();
//                    refreshOnItemEdits.launchAction(); //optimize, eg ?? (is likely to affect work time)
                myForm.refreshAfterEdit();//optimize, eg ?? (is likely to affect work time)
            });
            setDueDateToToday.setUIID("SwipeButtonSetDueToToday");
            buttonSwipeContainer.add(setDueDateToToday);
        }
//        }

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false && projectEditMode) {
//            //make a task a subtask by swiping right, and a sister task by swiping left - //TODO!!! not currently usted
//            //TODO!!!! can projectEditMode only be used/launched on a top-level project or also on a sub-project?? (if sub-project, must find another way to test if trying to move the top-level)
//            swipCont.addSwipeOpenListener((e) -> {
//                if (swipCont.isOpenedToRight()) {
//                    Log.p("Swipe right");
//                    //make a subtask
//                    //TODO!!!!
//                    if (item.getOwner() instanceof Item) {
//                        Item motherItem = ((Item) item.getOwner());
//                        int indexOfItem = motherItem.getList().indexOf(item);
//                        if (indexOfItem >= 1) { //if there is task before that can become new mother task
//                            item.setOwner(null);
//                            ((Item) motherItem.getList().get(indexOfItem - 1)).addToList(item);
//                            swipCont.close(); //close swipe container after swipe action
//                            refreshOnItemEdits.launchAction();
//                        }
//                    }
//                } else if (swipCont.isOpenedToLeft()) {
//                    Log.p("Swipe left");
//                    //make a sistertask
//                    //TODO!!!!
//                    if (item.getOwner() instanceof Item) {
//                        Item motherItem = ((Item) item.getOwner());
//                        if (motherItem.getOwner() instanceof Item) {
//                            Item motherOwner = ((Item) motherItem.getOwner());
//                            int indexOfMotherItem = motherOwner.getList().indexOf(motherItem);
//                            item.setOwner(null);
//                            motherOwner.addToList(indexOfMotherItem + 1, item); //add *after* the mother item
//                            swipCont.close(); //close swipe container after swipe action
//                            refreshOnItemEdits.launchAction();
//                        }
//                    }
//                }
//            });
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
////                new Button("X")); //Create new task below
//            buttonSwipeContainer.add(new Button("Z")); //DONE Postpone due date?
//
//            buttonSwipeContainer.add(new Button("Z")); //Move to top of list? No, better with multiple select
//            buttonSwipeContainer.add(new Button("X")); //Edit(?) -> use [>] instead
//            buttonSwipeContainer.add(new Button("Z")); //Set Waiting/Cancel/... NO, do using checkbox (or hide checkbox completely to get cleanest possible look?)
//            buttonSwipeContainer.add(new Button("Z")); //Select
//            buttonSwipeContainer.add(new Button("Z")); //See details of task? On Left sweep?
//            //SWIPE LEFT: add details
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (true) {
////        cont.setDraggable(true);
////        cont.setDropTarget(true);
////            return cont;//ignore Swipeable for the moment
////        } else {
////            SwipeableContainer swip = new SwipeableContainer(bottom, cont);
////            swip.addSwipeOpenListener(new ActionListener() {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
////                    if (swip.isOpenedToRight()) {
////                        item.setDone(true);
////                    }
////                }
////            });
////            swip.setDraggable(true);
////            return swip;
////        }
////</editor-fold>
//        }
//        }
//</editor-fold>
//        swipCont.putClientProperty(SOURCE_OBJECT, item); //store source item for use in Search in screens
        return swipCont;//ignore Swipeable for the moment
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public static Container buildItemContainerOLD(Item item, ItemList orgList, //TODO!!! remove orgList
//            MyForm.GetBoolean isDragAndDropEnabled, MyForm.Action refreshOnItemEdits,
//            boolean selectionModeAllowed, ArrayList<Item> selectedObjects, Category category,
//            KeepInSameScreenPosition keepPos, HashSet expandedObjects, MyForm.Action animator, boolean projectEditMode, boolean singleSelectionMode) {
//
////    public static Container buildItemContainer(Item item, List itemList, MyForm.GetBoolean isDragEnabled, MyForm.Action refreshOnItemEdits, boolean selectionModeAllowed, HashSet<Item> selectedObjects) {
//        Container mainCont = new Container(new BorderLayout());
//
//        Container swipeActionContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        Container buttonSwipeContainer = null;
//        Container buttonSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        if (true) {
////            swipeActionContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        }
////        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, rightSwipeContainer, contWithAddNewTaskCont) {
////        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(swipeActionContainer, buttonSwipeContainer, contWithAddNewTaskCont) {
//        SwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(swipeActionContainer, buttonSwipeContainer, mainCont) {
//
//            @Override
//            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
//                return draggedObject.getDragAndDropObject() instanceof Item;
//            }
//
//            @Override
//            public ItemAndListCommonInterface getDragAndDropList() {
////                if (ownerList != null) {
////                    return ownerList;
////                } else {
//////                return ((Item)getDragAndDropObject()).getOwnerList(); //returns the owner of
////                    return item.getOwner(); //returns the owner of
////                }
//                if (orgList != null && orgList instanceof Category) { //special case to allow drag&drop
//                    return orgList;
//                } else {
//                    return item.getOwner(); //returns the owner of
//                }
//            }
//
//            @Override
//            public List getDragAndDropSubList() {
////                return ((Item) getDragAndDropObject()).getList(); //returns the list of subtasks
//                return item.getList(); //returns the list of subtasks
//            }
//
//            @Override
//            public Object getDragAndDropObject() {
//                return item;
//            }
//
//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(item);
//            }
//
//            public Category getDragAndDropCategory() {
//                return category;
////                return null;
//            }
//
//        }; //D&D
//        swipCont.setGrabsPointerEvents(true); //when swiping on task description, it also activated the button to show tasks details
//
//        if (keepPos != null) {
//            keepPos.testItemToKeepInSameScreenPosition(item, swipCont);
//        }
////        if (keepPos!=null) keepPos.
//
//        Container west = new Container(BoxLayout.x());
//        mainCont.addComponent(WEST, west);
//
//        Container south = new Container(new FlowLayout());
//        boolean showDetails = MyPrefs.getBoolean(MyPrefs.showDetailsForAllTasks) || (expandedObjects != null && expandedObjects.contains(item)); //hide details by default
////        south.setHidden(!showDetailsForAllTasks || (tasksWithDetailsShown!=null && !tasksWithDetailsShown.contains(item))); //hide details by default
//        south.setHidden(!showDetails); //hide details by default
//        mainCont.addComponent(SOUTH, south);
//
//        //ITEM TEXT
//        Button itemLabel = new MyButtonInitiateDragAndDrop(
//                item.getText()
//                + ((item.getRepeatRule() != null ? "*" : "")
//                + (item.isInteruptOrInstantTask() ? "<" : "")
//                //if showing Item
//                //                + (item.getOwner() != null && !(item.getOwner().equals(orgList)) ? " /[" + item.getOwner().getText() + "]" : ""
//                + (item.getOwner() != null && item.getOwner() instanceof Item ? "^" : "" //show subtask with '^'
//                )), swipCont, isDragAndDropEnabled); //D&D
//        itemLabel.addActionListener(new ActionListener() { //UI: touch task name to show/hide details
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                south.setHidden(!south.isHidden()); //toggle hidden details
////                if (!south.isHidden()) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (tasksWithDetailsShown == null) {
////                        tasksWithDetailsShown = new HashSet();
////                    }
////                    tasksWithDetailsShown.add(item);
////                } else if (tasksWithDetailsShown != null) {
////                    tasksWithDetailsShown.remove(item);
////                }
////</editor-fold>
//                if (expandedObjects != null) {
//                    if (south.isHidden()) {
//                        expandedObjects.remove(item);
//                    } else {
//                        expandedObjects.add(item);
//                    }
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                swipCont.animateLayout(300); //moves center container down a bit, then redraws, NOT good
////                south.animateLayout(300); //no animation, simply redraws list with details added
////                mainCont.animateLayout(300); //NOT good
////                south.animateLayout(300); //NON
////</editor-fold>
////                myTree.animateLayout(300); //YES! Pb with null pointer
//                if (animator != null) {
//                    animator.launchAction();
//                } else {
//                    mainCont.animateHierarchy(300);
//                };
//            }
//        });
////        itemLabel.setUIID("Label");
//        mainCont.addComponent(CENTER, itemLabel);
//
//        //STATUS or SELECTED
////        if (isSelectionMode()) {
//        if (selectedObjects != null) {
////            RadioButton selected = new RadioButton();
//            Button selected = new Button();
//            selected.setIcon(selectedObjects.contains(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
//            selected.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent evt) {
//                    if (singleSelectionMode) {
//                        selectedObjects.clear(); //UI: add in order of selection
//                        selectedObjects.add(item); //UI: add in order of selection
//                    } else {
//                        if (selectedObjects.contains(item)) {
//                            selectedObjects.remove(item);
//                        } else {
//                            //TODO make it a setting whether operations on selected items are performed in order of selection or in order of appearance in the list (eg Move to top), with ArrayList: in order of selection
//                            selectedObjects.add(item); //UI: add in order of selection
//                        }
//                    }
//                    selected.setIcon(selectedObjects.contains(item) ? Icons.iconSelectedLabelStyle : Icons.iconUnselectedLabelStyle);
//                    selected.repaint();
//                }
//            });
//            west.add(selected);
//        } //else {
////        if (item.isTemplate()) {
////            west.add(new Label(Icons.iconTemplateStatusSymbolLabelStyle)); //NO, not nice with this symbol
////        } else
//        {
//            if (projectEditMode) {
//                //TODO!!!! mark this task selected (and others unselected), or unselected if already selected
//                //store the task somewhere to can be used to add subtasks to or sibling tasks after
//            } else {
//                west.add(new MyCheckBox(item.getStatus(), (oldStatus, newStatus) -> {
//                    if (newStatus != oldStatus) {
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont)); //keepPos since may be filtered after status change
//                        item.setStatus(newStatus);
//                        if (refreshOnItemEdits != null) {
//                            refreshOnItemEdits.launchAction();
//                        }
//                        DAO.getInstance().save(item);
//                        //TODO!!! optimize! Right now, refreshes entire Tree when anything in the tree changes
////                    item.addDataChangeListener((type, index) -> {if (type == DataChangedListener.CHANGED) {ItemContainer.TreeItemList2.getMyTreeTopLevelContainer(topContainer.getParent()).refresh();}});
//                    }
//                }, () -> {
//                    return item.getActualEffort() > 0;
//                })
//                );
//            }
//        }
////        }
//
//        //EAST
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//
//        //STARRED
//        final Button starred = new Button(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        final Button starredSwipeable = new Button(null, item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        final Button setDueDateToToday = new Button(null, Icons.iconSetDueDateToToday);
//
//        starred.setHidden(!item.isStarred() || item.isDone()); //UI: hide star if task is done
//        starred.addActionListener((e) -> {
//            item.setStarred(!item.isStarred());
//            starred.setHidden(true); //can only hide/unselect star in the line (need Swipe to set)
//            starred.repaint();
//            starredSwipeable.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//            DAO.getInstance().save(item);
////            starred.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//        });
//        east.add(starred);
//
//        //REMAINING EFFORT / ACTUAL EFFORT
//        Label actualEffortLabel = new Label();
//        Label remainingEffortLabel = null;
//        if (item.isDone()) {
//            long actualEffort = item.getActualEffort();
//            if (actualEffort != 0) {
////                east.addComponent(actualEffortLabel = new Label(MyDate.formatTimeDuration(actualEffort)));
//                actualEffortLabel.setText(MyDate.formatTimeDuration(actualEffort));
//                east.addComponent(actualEffortLabel);
//            }
//        } else {
//            long remainingEffort = item.getRemainingEffort();
//            if (remainingEffort != 0) {
//                east.addComponent(remainingEffortLabel = new Label(MyDate.formatTimeDuration(remainingEffort)));
//            }
//        }
//
//        //TODO!!!! define lambde functions to refresh the parts of an Item container that may change when the
//        ActionListener t = (e) -> {
//            //Actual effort may be
//            if (actualEffortLabel != null) {
//                actualEffortLabel.setText(MyDate.formatTimeDuration(item.getActualEffort()));
//            }
////            if (remainingEffortLabel != null) {
////                remainingEffortLabel.setText(MyDate.formatTimeDuration(item.getRemainingEffort()));
////            }
//        };
//
//        //EXPAND subtasks in Item
////        int numberUndoneSubtasks = item.getNumberOfUndoneItems(true); //true: get subtasks, always necessary for a project
//        int numberUndoneSubtasks = item.getNumberOfItems(true, true); //true: get subtasks, always necessary for a project
//        int totalNumberSubtasks = item.getNumberOfItems(false, true); //true: get subtasks, always necessary for a project
//        int totalNumberDoneSubtasks = totalNumberSubtasks - numberUndoneSubtasks; //true: get subtasks, always necessary for a project
//        if (numberUndoneSubtasks > 0 || totalNumberSubtasks > 0) {
//            Button subTasksButton = new Button() {
//                @Override
//                public void longPointerPress(int x, int y) {
//                    super.longPointerPress(x, y);
//                    //if event comes from eg a button inside the original node, get the original node
////            if (topContainer.getClientProperty(MyTree2.KEY_TOP_NODE) != null) {
////                topContainer = (Container) topContainer.getClientProperty(MyTree2.KEY_TOP_NODE);
////            };
//                    putClientProperty("LongPress", Boolean.TRUE); //is unset in MyTree2.Handler.actionPerformed()
//                    Log.p("longPointerPress");
//                    Object e = swipCont.getClientProperty(MyTree2.KEY_EXPANDED);
//                    MyTree2 myTree = MyTree2.getMyTreeTopLevelContainer(swipCont);
//                    if (e != null && e.equals("true")) {
////                        myTree.collapsePathNode(topContainer, true);
//                        myTree.collapseNode(swipCont, true);
//                    } else {
////                        myTree.expandPathNode(isInitialized(), topContainer, true);
//                        myTree.expandNode(false, swipCont, true);
//                    }
//                }
//
//            };
//            subTasksButton.setUIID("Label");
////            subTasksButton.setGrabsPointerEvents(true); //TODO!!! does this work to avoid
//            Command expandSubTasks = new Command("[" + numberUndoneSubtasks + "/" + totalNumberDoneSubtasks + "]");// {
//            subTasksButton.setCommand(expandSubTasks);
////            topContainer.putClientProperty("subTasksButton", subTasksButton);
//            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
//            east.addComponent(subTasksButton);
//        }
//
////        if (keepPos != null) {
////            keepPos.testItemToKeepInSameScreenPosition(item, swipCont);
////        }
//////        if (keepPos!=null) keepPos.
////EDIT Item in list
//        Button editItemButton = new Button() {
//            @Override
//            public void longPointerPress(int x, int y) {
//                super.longPointerPress(x, y);
//                Log.p("longPointerPress x=" + x + ", y=" + y + " on [" + this + "]");
//            }
//        };
//        Command editItemCmd = new Command("", Icons.get().iconEditSymbolLabelStyle) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                Item item = (Item) mainCont.getClientProperty("item"); //TODO!!!! is this needed, why notjust access 'item'??
//                ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//                new ScreenItem(item, (MyForm) swipCont.getComponentForm(), () -> {
//
////                    KeepInSameScreenPosition keepPos = new KeepInSameScreenPosition(null, swipCont); //TODO!!!!!! porblem with access to this
////                    KeepInSameScreenPosition keepPos = new KeepInSameScreenPosition(); //TODO!!!!!! porblem with access to this
////                    Form f = new Form(item.getText());
//                    //TODO!!! replace isDirty() with more fine-grained check on what has been changed and what needs to be refreshed
//                    if (false && item.isDirty()) {
////                        myTree.refresh();
////                        refreshOnItemEdits.launchAction(); //refresh when item edited, eg update anything derived from estimates, subtasks, ...
////                        ((ScreenListOfItems) mainCont.getComponentForm()).refreshAfterEdit(keepPos);
////                        ((ScreenListOfItems) mainCont.getComponentForm()).refreshAfterEdit(new KeepInSameScreenPosition(item, swipCont));
////                        ((ScreenListOfItems) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//                    }
//                    DAO.getInstance().save(item);
//                    //NB. replacing swipCont will work even if swipCont is not updated since each container that replaces creates its own new
////                    swipCont.getParent().replace(swipCont, buildTreeOrSingleItemContainer(item, motherItemList, isDragEnabled), null); //update the container with edited content
////                    MyTree2.getMyTreeTopLevelContainer( swipCont).refresh(); //NOT necessary, done by changeListener on item
////                    refreshAfterEdit();
//                    if (refreshOnItemEdits != null) {
//                        refreshOnItemEdits.launchAction();
//                    }
//                }).show();
////                new ScreenItem(item, thisScreen).show();
//            }
//        };
//        editItemButton.setCommand(editItemCmd);
//
//        mainCont.putClientProperty("item", item);
//        editItemButton.setUIID("Label");
////        editItemButton.setGrabsPointerEvents(true);
//        east.addComponent(editItemButton);
//
//        mainCont.addComponent(EAST, east);
//
//        //SOUTH
//        //WORK TIME
////        if (motherListWithWorkTimeDef != null && motherListWithWorkTimeDef instanceof ItemList) {
////            long finishTime;
//////            int idx = motherItemList.getItemIndex(item); //TODO optimize by passing index to here
////            int idx = motherListWithWorkTimeDef.indexOf(item); //TODO!!! optimization by passing index to here
//////            WorkTimeDefinition wtd = motherItemList.getSourceItemList().getWorkTimeAllocatorN();
////            WorkTimeDefinition wtd = ((ItemList) motherListWithWorkTimeDef).getSourceItemList().getWorkTimeAllocatorN();
////            if (wtd != null && (finishTime = wtd.getFinishTime(idx)) != 0) {
////                south.add("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime)));
//////                south.add("F:" + MyDate.formatDateCasual(new Date(finishTime)));
////            }
////        }
////        if (wtd != null && itemList != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) {
////        if (wtd != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) {
////        if (!item.isDone() && wtd != null && itemList != null && (finishTime = wtd.getFinishTime(itemList.indexOf(item))) != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
//        if (false) {
//            long finishTime;
//            if (wtd != null && !item.isDone() && orgList != null && (finishTime = wtd.getFinishTime(orgList.indexOf(item))) != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
////                south.add("F:" + L10NManager.getInstance().formatDateTimeShort(new Date(finishTime)));
//                south.add("F:" + MyDate.formatDateNew(finishTime));
//            }
//        }
//        if (!item.isDone() && item.getFinishTime().getTime() != 0) { //TODO optimization: get index as a parameter instead of calculating each time, or index w hashtable on item itself
////            south.add("F:" + L10NManager.getInstance().formatDateTimeShort(item.getFinishTime()));
//            south.add("F:" + MyDate.formatDateNew(item.getFinishTime()));
//        }
//        //PRIORITY
//        if (item.getPriority() != 0) {
//            south.add(new Label("P" + item.getPriority()));
//        }
//        //IMPORTANCE/URGENCY
//        south.add(new Label(item.getImpUrgPrioValueAsString()));
//
//        //DUE DATE or COMPLETED DATE
//        if (!item.isDone()) {
//            if (item.getDueDateD().getTime() != 0) {
////            south.addComponent(new Label("D:" + L10NManager.getInstance().formatDateShortStyle(new Date(item.getDueDate()))));
////            south.addComponent(new Label("D:" + MyDate.formatDateNatural(new MyDate(new Date(item.getDueDate())),MyDate.FORMAT_CASUAL, false)));
////                south.addComponent(new Label("D:" + MyDate.formatDateNatural(new MyDate(item.getDueDate()), new MyDate(), MyDate.FORMAT_CASUAL, false)));
//                south.addComponent(new Label("D:" + MyDate.formatDateNew(item.getDueDate())));
//            }
//        } else {
////            south.addComponent(new Label("C:" + MyDate.formatDateNatural(new MyDate(item.getCompletedDate()), new MyDate(), MyDate.FORMAT_CASUAL, false)));
//            south.addComponent(new Label("C:" + MyDate.formatDateNew(item.getCompletedDate())));
//        }
//
//        //ALARM SET icon
//        if (item.getAlarmDate() != 0) {
////            south.addComponent(new Label((Image) (item.getAlarmDate() != 0 ? Icons.get().iconAlarmSetLabelStyle : null)));
//            Label alarmLabel = new Label(MyDate.formatDateTimeNew(item.getAlarmDateD()), (Image) Icons.get().iconAlarmSetLabelStyle);
//            alarmLabel.getStyle().setAlignment(Component.RIGHT);
//            south.addComponent(alarmLabel);
//        }
//
//        //                new Label("(R:" + item.getRemainingEffortInMinutes() + "/A:" + item.getActualEffortInMinutes() + ")"),
//        //HIDE UNTIL
//        if (item.getHideUntilDateD().getTime() != 0 && MyPrefs.itemListAlwaysShowHideUntilDate.getBoolean()) {
////            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
//            south.addComponent(new Label("H:" + MyDate.formatDateNew(item.getHideUntilDateD())));
//        }
//        //START BY
//        if (item.getStartByDateD().getTime() != 0 && MyPrefs.itemListAlwaysShowStartByDate.getBoolean()) {
////            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
//            south.addComponent(new Label("S:" + MyDate.formatDateNew(item.getStartByDateD())));
//        }
//        //EXPIRE BY
//        if (item.getExpiresOnDateD().getTime() != 0 && MyPrefs.itemListExpiresByDate.getBoolean()) {
////            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
//            south.addComponent(new Label("E:" + MyDate.formatDateNew(item.getExpiresOnDateD())));
//        }
//        //WAITING
//        if (item.getWaitingTillDateD().getTime() != 0 && MyPrefs.itemListWaitingTillDate.getBoolean()) {
////            south.add("H:" + L10NManager.getInstance().formatDateTimeShort(item.getHideUntilDateD()));
//            south.addComponent(new Label("W:" + MyDate.formatDateNew(item.getWaitingTillDateD())));
//        }
//        //ACTUAL
//        long actual = item.getActualEffort();
//        if (actual != 0) {
//            String s;
//            if (actual / MyDate.MINUTE_IN_MILLISECONDS > 0) {
//                s = MyDate.formatTimeDuration(item.getActualEffort());
//            } else {
//                s = MyDate.formatTimeDuration(item.getActualEffort(), true);
//            }
//            south.addComponent(new Label("A:" + s));
//        }
//        //CATEGORIES
////                            categoriesButton.setText(getDefaultIfStrEmpty(getListAsCommaSeparatedString(locallyEditedCategories), "<set>")); //"<click to set categories>"
//        List cats = item.getCategories();
//        if (cats != null && cats.size() > 0) {
//            south.addComponent(new SpanLabel("Cat: " + getListAsCommaSeparatedString(cats)));
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
///*
//private Component makeQuickAddBox(boolean projectEditMode) {
//
//cont.add(EAST, new Button(Command.create(null, Icons.get().iconEditSymbolLabelStyle, (e) -> {
////            ((MyForm) getParent().getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
//((MyForm) getComponentForm()).setKeepPos(new KeepInSameScreenPosition());
//String taskText = taskTextEntryField.getText();
//if (taskText != null && taskText.length() > 0) {
//Item newItem = new Item(taskText);
//new ScreenItem(newItem, (MyForm) getComponentForm(), () -> {
//DAO.getInstance().save(newItem);
////TODO!!!! if a task is selected in projectEditMode, add this new task *after* it
//
//addNewTaskSetTemplateAddToListAndSave(newItem, (MyPrefs.insertNewItemsInStartOfLists.getBoolean()) && (!projectEditMode) ? 0 : itemListOrg.getSize());
//refreshAfterEdit();
//}).show();
//}
//})));
//        //TODO!!!! add button to add as subtasks to the selected tasks (if any), otherwise add in default position for new tasks
//        return cont;
//    }
//         */
////</editor-fold>
//        //SWIPEABLE
//        if (true) { //TODO!!!! only activate in manually sorted (and persisted) lists
////            buttonSwipeContainer.add(new Label("  "));
//            swipeActionContainer.add(new Label("  "));
//            //TODO!!!! can projectEditMode only be used/launched on a top-level project or also on a sub-project?? (if sub-project, must find another way to test if trying to move the top-level)
//            swipCont.addSwipeOpenListener((e) -> {
//                if (swipCont.isOpenedToRight()) {
////                    if (newTaskContainer != null ) { //if there is a previous newTask container remove it
////                        if (newTaskContainer.getParent()!=null)newTaskContainer.getParent().removeComponent(newTaskContainer); //should be animated away at same time as adding new one below
////                        newTaskContainer = null;
////                    }
////                    Object oldNewTaskCont = swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER);
////                    if (oldNewTaskCont != null && oldNewTaskCont instanceof Container) { //if there is a previous newTask container remove it
////                        Container oldNewTaskContC = (Container) oldNewTaskCont;
////                        if (oldNewTaskContC.getParent() != null) {
////                            oldNewTaskContC.getParent().removeComponent(oldNewTaskContC); //should be animated away at same time as adding new one below
////                        }
////                        swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null); //delete when closing the insertTask container
////                        swipCont.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //delete when closing the insertTask container
////                    }
////                    Log.p("Swipe left");
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (false) {
////                        //When swiping left, create a new container below with a textEntry field (and a status?), and a (x) to close it w/o creating a task.
////                        //When pressing Enter, add a new task (using keepPos) and create a new container below to add another task
////                        //When swiping this container right, create a subtasks to the preceding task
////                        //When swiping this container left, if the container corresponds to a subtask, change it to a top task
////                        Container cont = new Container(new BorderLayout());
////
////                        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Super task"), cont);
////                        newTaskContainer = swipC;
////
////                        swipC.addSwipeOpenListener((ev) -> {
////                            if (swipC.isOpenedToLeft()) {
////                                swipC.putClientProperty(SUBTASK_LEVEL_KEY, true);
////                            } else if (swipC.isOpenedToRight()) {
//////                            swipC.putClientProperty(SUBTASK_LEVEL_KEY, false);
////                                swipC.putClientProperty(SUBTASK_LEVEL_KEY, null);
////                            }
////                        });
////
////                        //Text
////                        MyTextField2 taskTextEntryField2 = new MyTextField2();
////                        taskTextEntryField2.setHint("New task");
////                        taskTextEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE);
////                        taskTextEntryField2.addActionListener((ev) -> {
////                            //When pressing ENTER
////                            String taskText = taskTextEntryField2.getText();
////                            taskTextEntryField2.setText(""); //clear text
////                            if (taskText != null && taskText.length() > 0) {
////                                Item newItem = new Item(taskText);
////                                refreshOnItemEdits.launchAction();
////                                DAO.getInstance().save(newItem);
////                                if (((Boolean) swipC.getClientProperty(SUBTASK_LEVEL_KEY)) == true) { //add task after previous
////                                    //make a sistertask (insert in same list as item, after item)
////                                    if (orgList != null && orgList.indexOf(item) != -1) {
////                                        int index = orgList.indexOf(item);
////                                        orgList.addItemAtIndex(newItem, index + 1);
////                                        swipCont.close(); //close swipe container after swipe action
////                                        refreshOnItemEdits.launchAction();
////                                    }
////                                } else { //add as subtask to previous task, and keep the subtask level
////                                    //make a subtask
////                                    item.addToList(0, newItem);
////                                    swipCont.close(); //close swipe container after swipe action
////                                    refreshOnItemEdits.launchAction();
////                                    swipC.putClientProperty(SUBTASK_LEVEL_KEY, null); //remove the subtask property so next task does not become a subtask to the subtask
////                                }
////                            }
////                        });
////
////                        cont.add(WEST, new Label(Icons.iconCheckboxCreated));
////                        cont.add(CENTER, taskTextEntryField2);
////                        cont.add(EAST, new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
////                            //close the container
////                            swipC.getParent().removeComponent(swipC);
////                            newTaskContainer = null;
////                            swipC.getComponentForm().animateHierarchy(300);
////                        })));
////                    }
////</editor-fold>
//                    //find place to insert container
//                    Container swipContParent = swipCont.getParent();
////                    Container swipContParentParent = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    Container contForInsertTask = swipContParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
////                    Container contForInsertTask = swipContParentParent.getParent(); //swipCont is inserted into a Container in a Container in the Tree list
//                    ASSERT.that(contForInsertTask instanceof MyTree2, "ERROR - not right place to insert insertTaskContainer, cont=" + contForInsertTask);
//                    int indexOfSwipContParentParent = contForInsertTask.getComponentIndex(swipContParent);
////                    Container contWithAddNewTaskCont = new Container(new BorderLayout()); //container to hold the AddNewTask container after the item
////                    contWithAddNewTaskCont.add(CENTER, mainCont);
//
//                    SwipeableContainer swipC = buildAddNewTaskContainer(item, orgList, swipCont, refreshOnItemEdits);
//                    if (myForm.getClientProperty(EXISTING_NEW_TASK_CONTAINER) != null) {
//                        ((Container) swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER)).getParent().removeComponent((Container) swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER));
//                    }
//                    myForm.putClientProperty(EXISTING_NEW_TASK_CONTAINER, swipC);
////                    Container parent = swipCont.getParent();
////                    int index = parent.getComponentIndex(swipCont);
////                    parent.addComponent(index+1, swipC);
////                    contWithAddNewTaskCont.add(SOUTH, swipC);
//                    contForInsertTask.addComponent(indexOfSwipContParentParent + 1, swipC);
//
////                    contWithAddNewTaskCont.getComponentForm().animateHierarchy(300);
////                    contWithAddNewTaskCont.getParent().animateHierarchy(300);
////                    contForInsertTask.animateHierarchy(300);
//                    contForInsertTask.animateHierarchy(300);
//                }
////                else if (swipCont.isOpenedToRight()) {
////                    Log.p("Swipe right");
////                }
//            });
//        } //else {
//        //only add swipe buttons when NOT in projectEditMode
////        bottomLeft.add(new Button(Icons.get().iconTimerSymbolLabelStyle)); //Start Timer on this
//        //TIMER
////            if (!item.isTemplate() && !item.isDone()) {
//        if (!projectEditMode) {
//            if (!item.isTemplate()) {
//                buttonSwipeContainer.add(new Button(new Command(null, Icons.iconTimerSymbolToolbarStyle) {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
////                ScreenTimerNew.getInstance().startTimerOnItemList(itemListFilteredSorted, ScreenListOfItems.this);
//                        ((MyForm) mainCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(item, swipCont));
//                        ScreenTimer.getInstance().startTimerOnItem(item, (MyForm) swipCont.getComponentForm(), true);
//                    }
//                }));
//            } else { // item.isTemplate()
//                buttonSwipeContainer.add(new Button(new Command(null, Icons.iconNewItemFromTemplate) {
//                    @Override
//                    public void actionPerformed(ActionEvent evt) {
//                        Item newTemplateInstantiation = new Item();
//                        item.copyMeInto(newTemplateInstantiation, Item.CopyMode.COPY_FROM_TEMPLATE);
//                        new ScreenItem(newTemplateInstantiation, (MyForm) swipCont.getComponentForm(), () -> {
//                            DAO.getInstance().save(newTemplateInstantiation); //must save item since adding it to itemListOrg changes its owner, saved to 'inbox'
////                            refreshOnItemEdits.launchAction(); //NOT necessary, since item not saved in list of templates
//                        }).show();
//                    }
//                }
//                ));
//            }
//        }
//
//        //STARRED
//        if (!projectEditMode) {
//            if (!item.isTemplate() && !item.isDone()) {
////            Button starredSwipeable = new Button(null, item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//                starredSwipeable.addActionListener((e) -> {
//                    item.setStarred(!item.isStarred()); //flip the starred value
//                    //update the starred button
//                    starred.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
//                    starred.setHidden(!item.isStarred());
//                    starredSwipeable.setIcon(item.isStarred() ? Icons.iconStarSelectedLabelStyle : Icons.iconStarUnselectedLabelStyle);
////            starred.getParent().revalidate();
//                    //update and save the item
//                    //update the starredSwipeable button
////            starredSwipeable.repaint();
//                    DAO.getInstance().save(item);
//                    swipCont.close();
//                });
//                buttonSwipeContainer.add(starredSwipeable);
//
//                setDueDateToToday.addActionListener((e) -> {
//                    Date tomorrow = new Date(new Date().getTime() + MyDate.DAY_IN_MILLISECONDS);
//                    if (MyDate.isToday(item.getDueDateD())) {
////                        item.setDueDate(MyDate.setDateToDefaultTimeOfDay(new Date(item.getDueDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
//                        item.setDueDate((new Date(item.getDueDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
//                    } else if (MyDate.isToday(item.getWaitingTillDateD())) {
//                        item.setWaitingTillDate((new Date(item.getWaitingTillDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
//                    } else if (MyDate.isToday(item.getStartByDateD())) {
//                        item.setStartByDate((new Date(item.getStartByDateD().getTime() + MyDate.DAY_IN_MILLISECONDS))); //UI: if due is already today, then set due day to tomorrow
//                    } else {
////                        item.setDueDate(MyDate.setDateToDefaultTimeOfDay(new Date())); //UI: if due is NOT already today, then set due day to today
//                        item.setDueDate(MyDate.setDateToTodayKeepTime(item.getDueDateD())); //UI: if due is NOT already today, then set due day to today
//                    }                    //update and save the item
//                    DAO.getInstance().save(item);
//                    swipCont.close();
//                    refreshOnItemEdits.launchAction(); //optimize, eg ?? (is likely to affect work time)
//                });
//                buttonSwipeContainer.add(setDueDateToToday);
//            }
//        }
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && projectEditMode) {
////            //make a task a subtask by swiping right, and a sister task by swiping left - //TODO!!! not currently usted
////            //TODO!!!! can projectEditMode only be used/launched on a top-level project or also on a sub-project?? (if sub-project, must find another way to test if trying to move the top-level)
////            swipCont.addSwipeOpenListener((e) -> {
////                if (swipCont.isOpenedToRight()) {
////                    Log.p("Swipe right");
////                    //make a subtask
////                    //TODO!!!!
////                    if (item.getOwner() instanceof Item) {
////                        Item motherItem = ((Item) item.getOwner());
////                        int indexOfItem = motherItem.getList().indexOf(item);
////                        if (indexOfItem >= 1) { //if there is task before that can become new mother task
////                            item.setOwner(null);
////                            ((Item) motherItem.getList().get(indexOfItem - 1)).addToList(item);
////                            swipCont.close(); //close swipe container after swipe action
////                            refreshOnItemEdits.launchAction();
////                        }
////                    }
////                } else if (swipCont.isOpenedToLeft()) {
////                    Log.p("Swipe left");
////                    //make a sistertask
////                    //TODO!!!!
////                    if (item.getOwner() instanceof Item) {
////                        Item motherItem = ((Item) item.getOwner());
////                        if (motherItem.getOwner() instanceof Item) {
////                            Item motherOwner = ((Item) motherItem.getOwner());
////                            int indexOfMotherItem = motherOwner.getList().indexOf(motherItem);
////                            item.setOwner(null);
////                            motherOwner.addToList(indexOfMotherItem + 1, item); //add *after* the mother item
////                            swipCont.close(); //close swipe container after swipe action
////                            refreshOnItemEdits.launchAction();
////                        }
////                    }
////                }
////            });
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
//////                new Button("X")); //Create new task below
////            buttonSwipeContainer.add(new Button("Z")); //DONE Postpone due date?
////
////            buttonSwipeContainer.add(new Button("Z")); //Move to top of list? No, better with multiple select
////            buttonSwipeContainer.add(new Button("X")); //Edit(?) -> use [>] instead
////            buttonSwipeContainer.add(new Button("Z")); //Set Waiting/Cancel/... NO, do using checkbox (or hide checkbox completely to get cleanest possible look?)
////            buttonSwipeContainer.add(new Button("Z")); //Select
////            buttonSwipeContainer.add(new Button("Z")); //See details of task? On Left sweep?
////            //SWIPE LEFT: add details
//////<editor-fold defaultstate="collapsed" desc="comment">
//////        if (true) {
//////        cont.setDraggable(true);
//////        cont.setDropTarget(true);
//////            return cont;//ignore Swipeable for the moment
//////        } else {
//////            SwipeableContainer swip = new SwipeableContainer(bottom, cont);
//////            swip.addSwipeOpenListener(new ActionListener() {
//////                @Override
//////                public void actionPerformed(ActionEvent evt) {
//////                    if (swip.isOpenedToRight()) {
//////                        item.setDone(true);
//////                    }
//////                }
//////            });
//////            swip.setDraggable(true);
//////            return swip;
//////        }
//////</editor-fold>
////        }
////        }
////</editor-fold>
////        swipCont.putClientProperty(SOURCE_OBJECT, item); //store source item for use in Search in screens
//        return swipCont;//ignore Swipeable for the moment
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private static SwipeableContainer buildAddNewTaskContainer(Item item, ItemList orgList, SwipeableContainer swipCont, MyForm.Action refreshOnItemEdits) {
//        //When swiping left, create a new container below with a textEntry field (and a status?), and a (x) to close it w/o creating a task.
//        //When pressing Enter, add a new task (using keepPos) and create a new container below to add another task
//        //When swiping this container right, create a subtasks to the preceding task
//        //When swiping this container left, if the container corresponds to a subtask, change it to a top task
//
//        Object oldNewTaskCont = swipCont.getComponentForm().getClientProperty(EXISTING_NEW_TASK_CONTAINER);
//        if (oldNewTaskCont != null && oldNewTaskCont instanceof Container) { //if there is a previous newTask container remove it
//            Container oldNewTaskContC = (Container) oldNewTaskCont;
//            if (oldNewTaskContC.getParent() != null) {
//                oldNewTaskContC.getParent().removeComponent(oldNewTaskContC); //should be animated away at same time as adding new one below
//            }
//            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null); //delete when closing the insertTask container
//            swipCont.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //delete when closing the insertTask container
//        }
//
//        Container cont = new Container(new BorderLayout());
//
//        SwipeableContainer swipC = new SwipeableContainer(new Label("Subtask"), new Label("Super task"), cont);
////        newTaskContainer = swipC;
//        swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, swipC);
//
//        swipC.addSwipeOpenListener((ev) -> {
//            if (swipC.isOpenedToLeft()) {
//                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null);
//                //TODO: indent or show visually that it is subtask
//            } else if (swipC.isOpenedToRight()) {
////                            swipC.putClientProperty(SUBTASK_LEVEL_KEY, false);
//                swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, true);
//                //TODO!!!! update formatting of swipC to show it's now inserting a subtask (eg indent it)
//                //reuse same margin indent (or simply same style?) as item container in swipCont?!
//
//            }
//            swipC.close();
//        });
//
//        //Text entry field
//        MyTextField2 taskTextEntryField2 = new MyTextField2(); //TODO!!!! need field to enter edit mode
//        taskTextEntryField2.setHint("Enter task (swipe right: subtask");
//        taskTextEntryField2.setConstraint(TextField.INITIAL_CAPS_SENTENCE);
//        if (false) {
//            cont.getComponentForm().setEditOnShow(taskTextEntryField2); //UI: start editing this field, only if empty (to avoid keyboard popping up)
//        }
//        taskTextEntryField2.requestFocus(); //enter edit mode??
//
//        taskTextEntryField2.addActionListener((ev) -> {
//            if (!swipC.isOpen()) {
//                //When pressing ENTER
//                String taskText = taskTextEntryField2.getText();
//                taskTextEntryField2.setText(""); //clear text
//                if (taskText != null && taskText.length() > 0) {
//                    Item newItem = new Item(taskText);
////                refreshOnItemEdits.launchAction();
//                    DAO.getInstance().save(newItem);
//                    if (swipC.getComponentForm().getClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY) == null) { //|| ((Boolean) swipC.getClientProperty(SUBTASK_LEVEL_KEY)) == false) { //add task after previous
//                        //make a sistertask (insert in same list as item, after item)
//                        if (orgList != null && orgList.indexOf(item) != -1) {
//                            int index = orgList.indexOf(item);
//                            orgList.addItemAtIndex(newItem, index + 1);
//                            DAO.getInstance().save(orgList);
//                            swipCont.close(); //close swipe container after swipe action
//                            refreshOnItemEdits.launchAction();
//                        }
//                    } else { //add as subtask to previous task, and keep the subtask level
//                        //make a subtask
//                        item.addToList(0, newItem);
//                        DAO.getInstance().save(item);
//                        swipCont.close(); //close swipe container after swipe action
//                        refreshOnItemEdits.launchAction();
//                        swipC.getComponentForm().putClientProperty(INSERT_NEW_TASK_AS_SUBTASK_KEY, null); //remove the subtask property so next task does not become a subtask to the subtask
//                    }
//                } else {
//                    //UI: if no text entered, close the text field
//                    Form parentForm = swipC.getComponentForm();
//                    swipC.getParent().removeComponent(swipC);
////            newTaskContainer = null;
////                    swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//                    swipC.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//                    parentForm.animateHierarchy(300);
//                }
//            } else { //swiped
//                if (swipC.isOpenedToRight()) {
//                    taskTextEntryField2.setHint("Enter task (swipe right: cancel subtask");
//
//                } else { //isOpenedToLeft()
//                    taskTextEntryField2.setHint("Enter task (swipe left: subtask");
//
//                }
//            }
//        });
//
////        cont.add(WEST, new Label(Icons.iconCheckboxCreated));
//        cont.add(CENTER, taskTextEntryField2);
//        cont.add(WEST, new Button(Command.create(null, Icons.iconCloseCircle, (ev) -> {
//            //close the container
//            Form parentForm = swipC.getComponentForm();
//            swipC.getParent().removeComponent(swipC);
////            newTaskContainer = null;
//            swipCont.getComponentForm().putClientProperty(EXISTING_NEW_TASK_CONTAINER, null);
//            parentForm.animateHierarchy(300);
//        })));
//        return swipC;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void processNewQuickTask(String text, ActionEvent e) {
//if (e.)
//        Item newItem = new Item(taskText);
//                DAO.getInstance().save(newItem);
//                addNewTask(newItem, (MyPrefs.insertNewItemsInStartOfLists.getBoolean()) && (!projectEditMode) ? 0 : itemListOrg.getSize()); //in projectEditMode, always insert at end of list
//
//    }
//    protected Container buildContentPaneForItemList(ItemList listOfItems) {
//    protected Container buildContentPaneForItemList(ItemAndListCommonInterface listOfItems) {
//        return buildContentPaneForItemList(listOfItems, expandedObjects, itemListOrg, ScreenListOfItems.this,  keepPos);
//    }
//    protected static
//</editor-fold>
    Container buildContentPaneForItemList(ItemAndListCommonInterface listOfItems //, HashSet expandedObjects, ItemAndListCommonInterface itemListOrg, MyForm myForm, KeepInSameScreenPosition keepPos
    ) {
//        parseIdMapReset();
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
//
//            @Override
//            public Component[] fetchComponents(int index, int amount) {
//
////                if (index+amount>listOfItems.size())
////                    amount = amount - (listOfItems.size()-index);
////                java.util.List<Item> list = listOfItems.subList(index, index + Math.min(amount, listOfItems.size() - index)); //need to use listOfItems since it may filter out itemsindex;
//                List<Item> list = listOfItems.subList(Math.min(index, listOfItems.size() - 1), index + Math.min(amount, listOfItems.size() - index));
//                if (list.isEmpty()) {
//                    return null;
//                }
//                Component[] comps = new Component[list.size()];
//                for (int i = 0, size = list.size(); i < size; i++) {
//                    Item item = list.get(i);
////                    comps[i] = ItemContainer.buildTreeOrSingleItemContainer(item, listOfItems, () -> {return filterSortDef == null || !filterSortDef.isSortOn();}); //TODO enable drag&drop
//                    comps[i] = new TreeItemList2(item, expandedObjects); //TODO enable drag&drop
//                }
//                return comps;
//            }
//        };
//        cl.addPullToRefresh(task); //TODO
//        return cl;
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
////        cont.setScrollableY(true);
////        cont.add(dt);
//        cont.add(cl);
////        cont.setDraggable(true);
////        dt.setDropTarget(true);
//        return cont;
//</editor-fold>
        if (listOfItems != null && listOfItems.size() > 0) {
//        myTree = new MyTree2(listOfItems, expandedObjects, filterSortDef) {
//            MyTree2 myTree = new MyTree2(listOfItems, expandedObjects, itemListOrg.getFilterSortDef(), (item, itemOrItemList) -> InsertNewTaskContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList)) //<editor-fold defaultstate="collapsed" desc="comment">
//            MyTree2 myTree = new MyTree2(listOfItems, expandedObjects, (item, itemOrItemList) -> InlineInsertNewTaskContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList)) //<editor-fold defaultstate="collapsed" desc="comment">
            MyTree2 myTree = new MyTree2(listOfItems, expandedObjects, getInlineInsertContainer(), stickyHeaderGen) //                    lastInsertNewElementContainer != null ? 
            //            (getInlineInsertContainer() != null ? 
            ////                            (item, itemOrItemList) -> lastInsertNewElementContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList)
            ////                            (item, itemOrItemList) -> lastInsertNewElementContainer.make(item, itemOrItemList)
            //                            (item, itemOrItemList) -> getInlineInsertContainer().make(item, itemOrItemList)
            //                            : null)) //<editor-fold defaultstate="collapsed" desc="comment">
            //
            ////                if (lastInsertNewTaskContainer == null) {
            ////                InsertNewTaskContainer lastInsertNewTaskContainer = (InsertNewTaskContainer) getClientProperty(InsertNewTaskContainer.LAST_INSERTED_NEW_TASK_CONTAINER);
            ////                if (lastInsertNewTaskContainer == null) { //TODO Optimization: called for every container, replace by local variable?
            ////                    return null;
            ////                } else {
            ////                    return lastInsertNewTaskContainer.make(item, itemOrItemList, ScreenListOfItems.this);
            ////                }
            ////                return InsertNewTaskContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList, this);
            //                return InsertNewTaskContainer.getInsertNewTaskContainerFromForm(item, itemOrItemList);
            //            })
            //</editor-fold>
            {
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Container cmp = buildItemContainer((Item) node, itemListFilteredSorted, () -> isDragAndDropEnabled(), () -> {
//                }, true, selectedObjects);
//                setIndent(cmp, depth);
//                return cmp;
//            }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            @Override
//            protected Component createNode(Object node, int depth) {
//                return createNode(node, depth, null);
//            }
//</editor-fold>
                @Override
                protected Component createNode(Object node, int depth, Category category) {
//                    return createNode(node, depth, itemListOrg, category);
                    ItemAndListCommonInterface owner = (node instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) node).getOwner() != null) ? ((ItemAndListCommonInterface) node).getOwner() : itemListOrg;
                    return createNode(node, depth, owner, category);
                }

                @Override
                protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
                    Container cmp = null;
                    if (node instanceof Item) {
//                    assert ((Item) node).isDataAvailable() : "Item \"" + node + "\" data not available";
                        if (false) {
                            ASSERT.that(((Item) node).getObjectIdP() == null || ((Item) node).isDataAvailable(), () -> "Item \"" + node + "\" objId=(" + ((Item) node).getObjectIdP() + ") data not available");
                        }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, null, () -> true, () -> dt.refresh(),
//                            false, //selectionMode not allowed for Categories??
//                            null); //TODO any reason to support operations on multiple selected categories?
//TODO!!! store expanded itemLists:
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, itemListFilteredSorted, itemListOrg, () -> !optionDisableDragAndDrop, () -> refreshAfterEdit(),
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, itemListFilteredSorted, itemListOrg, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(),
//                    cmp = ScreenListOfItems.buildItemContainer((Item) node, itemListOrg, itemListOrg, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(),
//</editor-fold>
//                    cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItems.this, (Item) node, itemListOrg, category);
//                        cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItems.this, (Item) node, itemOrItemList, category);
                        cmp = ScreenListOfItems.buildItemContainer(ScreenListOfItems.this, (Item) node, itemOrItemList, category);
//<editor-fold defaultstate="collapsed" desc="comment">
//                    cmp = ScreenListOfItems.buildItemContainerOLD((Item) node, itemListOrg, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(),
//                            false, //selectionMode not allowed for list of itemlists //TODO would some actions make sense on multiple lists at once??
//                            selectedObjects,
//                            category, keepPos, expandedObjects, () -> animateMyForm(), false, optionSingleSelectMode); //hack: get access to the latest category (the one above the items in the Tree list)
//</editor-fold>
                    } else if (node instanceof WorkSlot) {
//                        cmp = ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) node, () -> {}, keepPos);
                        cmp = ScreenListOfWorkSlots.buildWorkSlotContainer((WorkSlot) node, ScreenListOfItems.this, keepPos, false, true);
                    } else {
                        assert false;
                    }
//<editor-fold defaultstate="collapsed" desc="comment">
//                        if (node instanceof Category) {
//                        assert false : "should not be called (no reason there is a Category in a list of Items)??";
//                        cmp = ScreenListOfCategories.buildCategoryContainer((Category) node, (CategoryList) null, keepPos, () -> refreshAfterEdit()); //, (ItemList) treeParent);
//                        category = (Category) node; //huge hack: store the category of the latest category container for use when constructing the following
//                    } else if (node instanceof ItemList) {
//                        assert false : "should only be Item or ItemList, was:" + node;
//                        cmp = ScreenListOfItemLists.buildItemListContainer((ItemList) node, keepPos, true); //, (ItemList) treeParent);
//                        category = (Category) node; //huge hack: store the category of the latest category container for use when constructing the following
//                    } else {
//                        assert false : "should only be Item or ItemList, was:" + node;
//                    }
//</editor-fold>
                    setIndent(cmp, depth);
//                    cmp.setUIID("ContainerListElement");
                    return cmp;
                }
            };
            return myTree;
        } else {
//            return new InsertNewTaskContainer(null, listOfItems, ScreenListOfItems.this);
//            return new InlineInsertNewElementContainer(this, null, listOfItems);
            return new InlineInsertNewItemContainer2(this, null, listOfItems);
        }

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Container xxxxbuildContentPaneForItemListOld(ItemList itemList) {
//        MyTree dt = new MyTree(itemList) {
//            @Override
//            protected Component createNode(Object node, int depth) {
//                Component cmp = buildItemContainer((Item) node, itemList);
//                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
////                setScrollable(false);
//                return cmp;
//            }
//
////            @Override // copied from CN1: Container.drop
//            public void dropx(Component dragged, int x, int y) {
//                //move the dragged item to the right place in the underlying list
////                int draggedIdx = itemListOrg.getItemIndex((Item) dragged.getClientProperty("item"));
//                Item draggedItem = (Item) dragged.getClientProperty("item");
//                int draggedIdx = itemListOrg.getItemIndex(draggedItem);
////                int droppedIdx = itemListOrg.getItemIndex((Item) findDropTargetAt(x, y).getClientProperty("item"));
//                Component dropTargetComp = findDropTargetAt(x, y);
//                Item dropTargetItem = (Item) dropTargetComp.getClientProperty("item");
//                int droppedIdx = itemListOrg.getItemIndex(dropTargetItem);
////                Item draggedItem = (Item) itemListOrg.remove(draggedIdx);
//                itemListOrg.remove(draggedIdx);
//                itemListOrg.addItemAtIndex(draggedItem, droppedIdx);
//                DAO.getInstance().save(itemListOrg);
//
//                //move the dragged container to the new place in the visible
//                super.drop(dragged, x, y);
//                dragged.setDraggable(false);
//            }
//        };
//
////        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
////        dt.setScrollableY(true);
////        cont.add(dt);
////        cont.setDraggable(true);
////        dt.setDropTarget(true);
////        return cont;
//        return dt;
//    }
//</editor-fold>
}
