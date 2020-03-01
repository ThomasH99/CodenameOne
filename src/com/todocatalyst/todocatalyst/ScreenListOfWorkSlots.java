/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.components.SpanLabel;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.MyBorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.Date;
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
public class ScreenListOfWorkSlots extends MyForm {
    //TODO!! make the workSlots editable directly inline in the list (more natural to edit in an overview of all workslots

    static String SCREEN_TITLE = "Work time";
//    private ItemList workSlotList;
//    private Object owner;
    ItemAndListCommonInterface workSlotListOwner;
    private long now = new MyDate().getTime();
//    private List<WorkSlot> workSlotList;
//    private WorkSlotList workSlotList;
//    private KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
//    private FetchWorkSlotList refreshWorkSlotList;
    private boolean showOwner; //true if show owner of workslots inline in list of workslots
    private String stickyStrKeep;
    private MyTree2.StickyHeaderGenerator stickyHeaderGen = MyTree2.makeStickyHeaderGen(WorkSlot.PARSE_START_TIME, () -> stickyStrKeep, (s) -> stickyStrKeep = s); //group workSlots by eg day or week
    private boolean showAlsoExpiredWorkSlots;
    private boolean enableAddWorkSlots;

//    protected static String FORM_UNIQUE_ID = "ScreenListOfWorkSlots"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
    /**
     * edit a list of categories
     *
     * @param nameOfOwner
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
//    ScreenListOfWorkSlots(String nameOfOwner, List<WorkSlot> workSlotList, ParseObject owner, MyForm previousForm, GetWorkSlotList updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//    ScreenListOfWorkSlots(String nameOfOwner, WorkSlotList workSlotList, ParseObject owner, MyForm previousForm, GetWorkSlotList updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//    ScreenListOfWorkSlots(String nameOfOwner, WorkSlotList workSlotList, ItemAndListCommonInterface owner, MyForm previousForm, GetWorkSlotList updateItemListOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        this(nameOfOwner, workSlotList, owner, previousForm, updateItemListOnDone, null);
//    }
    /**
     *
     * @param nameOfOwner
     * @param workSlotList
     * @param owner
     * @param previousForm
     * @param updateItemListOnDone called to update the workTime for the tasks
     * after the workSlots have been edited to removeFromCache the workTime,
     * e.g. if new slots were added/changed/removed
     * @param refreshWorkSlotList
     */
//    ScreenListOfWorkSlots(String nameOfOwner, WorkSlotList workSlotList, ItemAndListCommonInterface owner, MyForm previousForm,
    ScreenListOfWorkSlots(ItemAndListCommonInterface owner, MyForm previousForm, FetchWorkSlotList refreshWorkSlotList, Runnable updateActionOnDone,
            boolean showOwner, boolean enableAddWorkSlots) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        super("Work time for " + nameOfOwner, previousForm, () -> updateItemListOnDone.update(workSlotList));
//        super(SCREEN_TITLE + ((nameOfOwner != null && nameOfOwner.length() > 0) ? " for " + nameOfOwner : ""), previousForm,
        super(SCREEN_TITLE + ((owner.getText() != null && owner.getText().length() > 0) ? " for " + owner.getText() : ""), previousForm, updateActionOnDone);
        setUniqueFormId("ScreenListOfWorkSlots");
//                () -> {
//                    if (updateItemListOnDone != null) {
////                        updateItemListOnDone.update(workSlotList);
////                        updateItemListOnDone.update(workSlotList.getWorkSlots());
//                        updateItemListOnDone.update(owner.getWorkSlotListN().getWorkSlots());
//                    }
//                });
//        setUpdateItemListOnDone(updateItemListOnDone);
//        this.workSlotList = workSLotList;
        this.workSlotListOwner = owner;
//        this.workSlotList = workSlotList;
//        this.refreshWorkSlotList = refreshWorkSlotList;
        this.showOwner = showOwner;
        setPinchInsertEnabled(true);
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
        if (!(getLayout() instanceof MyBorderLayout)) { //enable small timer?!
            setLayout(new MyBorderLayout());
        }
        getContentPane().setScrollableY(false);
        expandedObjects = new ExpandedObjects(getUniqueFormId(), (ParseObject) owner); //no persistance if filename and is empty (e.g. like with list of project subtasks)
        this.enableAddWorkSlots = enableAddWorkSlots;
        addCommandsToToolbar(getToolbar());
//        setScrollableY(true);
//        setLayout(BoxLayout.y());
//        getContentPane().setScrollableY(true);

//        getContentPane().add(buildContentPaneForItemList(workSlotList));
        refreshAfterEdit();
    }

    ScreenListOfWorkSlots(ItemAndListCommonInterface owner, MyForm previousForm, FetchWorkSlotList refreshWorkSlotList, Runnable updateActionOnDone, boolean showOwner) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        this(owner, previousForm, refreshWorkSlotList, updateActionOnDone, showOwner, true);
    }

    ScreenListOfWorkSlots(ItemAndListCommonInterface owner, MyForm previousForm, Runnable updateActionOnDone, boolean showOwner) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        this(owner, previousForm, null, updateActionOnDone, showOwner);
    }

    @Override
    public void refreshAfterEdit() {
        if (getKeepPos() == null) { //if no position set before, try to keep same scroll position
            setKeepPos(new KeepInSameScreenPosition());
        }
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
//        getContentPane().removeAll();
//<editor-fold defaultstate="collapsed" desc="comment">
//        List<WorkSlot> wsList = null;
//        if (false) { //below code is a hack
//            WorkSlotList wsList = null;
////        wsList = DAO.getInstance().getWorkSlotsN( owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////        if (owner instanceof ItemList) {
////            wsList = DAO.getInstance().getWorkSlotsN((ItemList) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////        } else if (owner instanceof Item) {
////            wsList = DAO.getInstance().getWorkSlotsN((Item) owner);
////        }
////        if (owner instanceof ItemAndListCommonInterface) {
////            wsList = DAO.getInstance().getWorkSlotsN((ItemList) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//            if (workSlotListOwner != null) {
//                wsList = DAO.getInstance().getWorkSlotsN(workSlotListOwner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//            } else if (refreshWorkSlotList != null) {
//                wsList = new WorkSlotList(refreshWorkSlotList.getUpdatedWorkSlotList(workSlotList));
//            }
//        }
//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForWorkSlotList(wsList));
//        getContentPane().add(buildContentPaneForWorkSlotList(wsList));
//</editor-fold>
        workSlotListOwner.resetWorkTimeDefinition(); //force recalculation on each update of list to show tasks in workslots correctly
        WorkSlotList workSlotListN = workSlotListOwner.getWorkSlotListN();
        if (workSlotListN != null) {
            workSlotListN.setNow(MyDate.currentTimeMillis()); //refresh now everytime the list is displayed again
            workSlotListN.setIncludeExpiredWorkSlots(showAlsoExpiredWorkSlots);
        }
        Container contentContainer = buildContentPaneForWorkSlotList(workSlotListN);
        if (contentContainer instanceof MyTree2) {
            setInlineInsertContainer(((MyTree2) contentContainer).getInlineInsertField()); //save for next update
        }
        getContentPane().add(MyBorderLayout.CENTER, contentContainer);
//        if (getInlineInsertContainer()!= null)
//            setStartEditingAsyncTextArea(getInlineInsertContainer().getTextArea()); //set to ensure it starts up in edit-model

        setTitleAnimation(contentContainer); //MUST do this here since we create a new container on each refresh

//        if (this.keepPos != null) {
//            this.keepPos.setNewScrollYPosition();
//        }
        //check if there was an insertContainer active earlier
        recreateInlineInsertContainerIfNeeded();

        super.refreshAfterEdit();
//        revalidate();
//        restoreKeepPos();
    }

    @Override
    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        super.addCommandsToToolbar(toolbar);
        //NEW WORKSLOT
        if (enableAddWorkSlots) { //TODO!!!! disable until possible to select owner
//            toolbar.addCommandToRightBar(MyReplayCommand.createKeep("NewWorkSlot", "", Icons.iconNewToolbarStyle(), (e) -> {
//            toolbar.addCommandToRightBar(MyReplayCommand.createKeep("NewWorkSlot", "", Icons.iconNew, (e) -> {
            toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("NewWorkSlot", "", Icons.iconNew, (e) -> {
                WorkSlot newWorkSlot = new WorkSlot();
                newWorkSlot.setOwner(workSlotListOwner); //MUST set owner before editing to ensure a possible RepeatRule will insert workslot repeatInstances in right owner list
                setKeepPos(new KeepInSameScreenPosition());
                new ScreenWorkSlot(newWorkSlot, workSlotListOwner, ScreenListOfWorkSlots.this, () -> {
                    if (newWorkSlot.hasSaveableData()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                    workSlot.setOwner(owner);
//                    //set Owner of new workSlot
//                    if (owner instanceof Category) {
//                        workSlot.setOwnerCategory((Category) owner);
//                    } else if (owner instanceof ItemList) {
//                        workSlot.setOwnerList((ItemList) owner);
//                    } else if (owner instanceof Item) {
//                        workSlot.setOwnerItem((Item) owner);
////                    } else { //TODO: add flagging of new/unknown owner for workslots
////                        throw RuntimeException("Unknown type of owner");
//                    } else assert false: "should never happen";
//</editor-fold>
                        //save new workSlot
//                        DAO.getInstance().saveNew(newWorkSlot); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//<editor-fold defaultstate="collapsed" desc="comment">
//                    workSlotList.addItemAtIndex(workSlot, 0);
//save updated owner of workslot //TODO not necessary to save the owners (they are not modified for workslots since these are fetched via a Parse query)??!!
//                    if (false) {
//                        if (workSlotListOwner instanceof ItemList) {
//                            DAO.getInstance().save((ItemList) workSlotListOwner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                        } else if (workSlotListOwner instanceof Item) {
//                            DAO.getInstance().save((Item) workSlotListOwner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                    } else { //TODO:
////                        throw RuntimeException("Unknown type of owner");
//                        }
//                    }
//                    if (false) { //shouldn't be necessary since workSlotList will be re-read in refreshAfterEdit()
//                        workSlotList.add(newWorkSlot);
////                            WorkSlot.sortWorkSlotList(workSlotList);
//                        workSlotList.sortWorkSlotList();
//                    }
//</editor-fold>
//                    WorkSlotList workSlotList = workSlotListOwner.getWorkSlotListN();
//                    if (workSlotList == null) {
//                        workSlotList = new WorkSlotList();
//                    }
//                    workSlotList.add(newWorkSlot);
//                    workSlotListOwner.setWorkSlotList(workSlotList);
                        workSlotListOwner.addWorkSlot(newWorkSlot);
                        DAO.getInstance().saveNew(true, newWorkSlot, (ParseObject) workSlotListOwner);

                        refreshAfterEdit();
                    }
                }).show();
            }
            ));
        }

        //BACK
//        toolbar.addCommandToLeftBar(makeDoneCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle())));
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true));
        toolbar.addCommandToOverflowMenu(new Command("", null) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                showAlsoExpiredWorkSlots = !showAlsoExpiredWorkSlots;
                refreshAfterEdit();
            }

            @Override
            public String getCommandName() {
                return showAlsoExpiredWorkSlots ? "Hide past" : "Show past";
            }
        });
//<editor-fold defaultstate="collapsed" desc="comment">
//                new Command("", iconDone) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                updateItemListOnDone.update(categoryList); //should never be null
//                previousForm.refreshAfterEdit();
//                previousForm.showBack();
//            }
//        });
//</editor-fold>
        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @param content
     * @return
     */
//    protected Container buildWorkSlotContainer(WorkSlot workSlot) {
//        return buildWorkSlotContainer(workSlot, null);
//    }
//    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm.Action refreshOnItemEdits, KeepInSameScreenPosition keepPos) {
//        return buildWorkSlotContainer(workSlot, refreshOnItemEdits, keepPos, false);
//    }
//
//    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm.Action refreshOnItemEdits, KeepInSameScreenPosition keepPos, boolean expandItemsInWorkSlot) {
//        return buildWorkSlotContainer(workSlot, (MyForm)null, keepPos, expandItemsInWorkSlot);
//    }
//    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm myForm, KeepInSameScreenPosition keepPos, boolean expandItemsInWorkSlot) {
//        return buildWorkSlotContainer(workSlot, myForm, keepPos, expandItemsInWorkSlot, true);
//    }
//</editor-fold>
    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm myForm, KeepInSameScreenPosition keepPos, boolean expandItemsInWorkSlot, boolean showOwner) {
        return buildWorkSlotContainer(workSlot, myForm, keepPos, expandItemsInWorkSlot, showOwner, MyDate.currentTimeMillis());
    }

    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm myForm, KeepInSameScreenPosition keepPos,
            boolean expandItemsInWorkSlot, boolean showOwner, long now) {
        Container cont = new Container();
        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(null, null, cont) {
            @Override
            public ItemAndListCommonInterface getDragAndDropObject() {
                return workSlot;
            }
        };
        if (Config.TEST) {
            cont.setName("WorkSlotCont:" + workSlot);
        }
        if (Config.TEST) {
            swipCont.setName("WSltMyDD:" + workSlot);
        }
        cont.setLayout(new MyBorderLayout());
//        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
        //EDIT items in category
//        Button editItemButton = new Button(new Command(workSlot.getText()) {
//        Button editWorkSlotButton = new Button(Icons.iconEditSymbolLabelStyle);
        Button editWorkSlotButton = new Button(Icons.iconEditSymbol );
//<editor-fold defaultstate="collapsed" desc="comment">
//        editWorkSlotButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                keepPos.setKeepPos(new KeepInSameScreenPosition());
//                ((MyForm) cont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(workSlot, cont));
////                new ScreenWorkSlot(workSlot, ScreenListOfWorkSlots.this, () -> {
//                new ScreenWorkSlot(workSlot, (MyForm) cont.getComponentForm(), () -> {
//                    //TODO!!! add same check as when creating a new WorkSlot (if both StartDate and Duration deleted, delete the workslot)??
////                            workSlot.setList(itemList.getList());
//                    DAO.getInstance().save(workSlot);
////                    refreshAfterEdit();
//                    refreshOnItemEdits.launchAction();
//                }).show();
//            }
//        });
//</editor-fold>
//        editWorkSlotButton.setCommand(MyReplayCommand.create("EditWorkSLot-", workSlot.getObjectIdP(), null, Icons.iconEditSymbolLabelStyle, (e) -> {
        editWorkSlotButton.setCommand(MyReplayCommand.create("EditWorkSLot-", workSlot.getObjectIdP(), null, Icons.iconEditSymbol, (e) -> {
//                keepPos.setKeepPos(new KeepInSameScreenPosition());
//            ((MyForm) cont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(workSlot, cont));
            myForm.setKeepPos(new KeepInSameScreenPosition(workSlot, cont));
//                new ScreenWorkSlot(workSlot, ScreenListOfWorkSlots.this, () -> {
//            new ScreenWorkSlot(workSlot, (MyForm) cont.getComponentForm(), () -> {
            new ScreenWorkSlot(workSlot, workSlot.getOwner(), myForm, () -> {
                //TODO!!! add same check as when creating a new WorkSlot (if both StartDate and Duration deleted, delete the workslot)??
//                            workSlot.setList(itemList.getList());
                DAO.getInstance().saveNew(workSlot, true);
//                    refreshAfterEdit();
//                refreshOnItemEdits.launchAction();
                if (false) {
                    myForm.refreshAfterEdit(); //not needed anymore since always called on screen refresh
                }
            }).show();
        }
        ));
//        editItemButton.setUIID("Label");
        cont.addComponent(MyBorderLayout.EAST, editWorkSlotButton);

//<editor-fold defaultstate="collapsed" desc="comment">
//        Button editItemPropertiesButton = new Button();
//        editItemPropertiesButton.setCommand(new Command("", iconEditProperties) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
//                new ScreenCategory(workSlot, ScreenListOfWorkSlots.this,
//                        () -> {
//                        }
//                ).show();
//            }
//        });
//        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//</editor-fold>
        Container west = new Container(new BoxLayout(BoxLayout.X_AXIS));
//<editor-fold defaultstate="collapsed" desc="comment">
//        Button subTasksButton = new Button();
//        if (!workSlot.getComment().equals("")) {
//            Label description = new Label(" (" + workSlot.getComment() + ")");
//            east.addComponent(description);
//        }

//        if (workSlot.getSize() != 0) {
//        Label nbTasks = new Label("[" + workSlot.getDurationAdjusted(now) + "]");
//        west.add(MyDate.formatDateL10NShort(workSlot.getStartTime()));
//        west.add(MyDate.formatDateL10NShort(workSlot.getStartTime().getTime()));
//        west.add(L10NManager.getInstance().formatDateTimeShort(workSlot.getStartTime()));
//        west.add(MyDate.formatDateTimeNew(workSlot.getStartTimeD())+" - "+MyDate.formatDateTimeNew(new Date(workSlot.getEndTime())));
//        String startTimeStr = (workSlot.getStartAdjusted(now) != workSlot.getStartTimeD().getTime())
//                ? "Now"
//                : MyDate.formatDateTimeNew(new Date(workSlot.getStartAdjusted(now))); //UI: for ongoing workSlot, show 'now' instead of startTime
//        startTimeStr += "-" + MyDate.formatTimeNew(new Date(workSlot.getEndTime()))
//                + (workSlot.getRepeatRule() != null ? "*" : ""); //                + " " + MyDate.formatTimeDuration(workSlot.getDurationInMillis())// + ")"
//        String startTimeStr = MyDate.formatDateTimeNew(new Date(workSlot.getStartAdjusted(now))); //UI: for ongoing workSlot, show 'now' instead of startTime
//</editor-fold>
        String startTimeStr = MyDate.formatDateSmart(new Date(workSlot.getStartAdjusted(now))); //UI: for ongoing workSlot, show 'now' instead of startTime
        Label startTimeLabel = new Label(startTimeStr,
                workSlot.getStartAdjusted(now) != workSlot.getStartTimeD().getTime() ? "WorkSlotStartTimeNow" : "WorkSlotStartTime");
 startTimeLabel.setMaterialIcon(Icons.iconWorkSlot);
//        String endTimeStr = "-" + MyDate.formatTimeNew(new Date(workSlot.getEndTime()))
        String endTimeStr = " " + MyDate.formatDurationShort(workSlot.getDurationAdjusted(now))
                + (workSlot.getRepeatRule() != null ? "*" : ""); //                + " " + MyDate.formatTimeDuration(workSlot.getDurationInMillis())// + ")"
        Label endTimeLabel = new Label(endTimeStr, "WorkSlotEndTime");

//        west.add(startTimeStr);
        west.add(startTimeLabel).add(endTimeLabel);
//<editor-fold defaultstate="collapsed" desc="comment">
//            static String formatDateNew(Date date, boolean useYesterdayTodayTomorrow, boolean includeDate, boolean includeTimeOfDay, boolean includeDayOfWeek, boolean useUSformat) {

//        east.addComponent(new Label("[" + workSlot.getDurationAdjustedInMinutes(now) + "]"));
//        west.add(MyDate.formatTime(workSlot.getDurationInMillis()));
//        west.add("(" + MyDate.formatTimeDuration(workSlot.getDurationInMillis()) + ")");
//        cont.addComponent(BorderLayout.CENTER, new Label(workSlot.getText() + (workSlot.getRepeatRule() != null ? "*" : "")));
//</editor-fold>
        if (showOwner && workSlot.getOwner() != null) {
            cont.addComponent(MyBorderLayout.CENTER, new Label(workSlot.getOwner().getText()));
        }
//        }
//        east.addComponent(editItemPropertiesButton);

        List<Item> items = workSlot.getItemsInWorkSlot();
        if (items != null && items.size() > 0) {
            Button showItemsInWorkSlotButton = new Button(new Command("[" + items.size() + "]"));
//            showItemsInWorkSlotButton.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, showItemsInWorkSlotButton);
            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, showItemsInWorkSlotButton);
            west.addComponent(showItemsInWorkSlotButton);
        }

//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
        cont.addComponent(MyBorderLayout.WEST, west);
        Container south = new Container(BoxLayout.y());
        cont.addComponent(MyBorderLayout.SOUTH, south);

//        if (workSlot.getOwner() != null) {
//            if(showOwner)south.addComponent(new Label("For: " + workSlot.getOwner().getText()));
        if (workSlot.getText() != null && workSlot.getText().length() > 0) {
            south.addComponent(new Label(("\"" + workSlot.getText() + "\"")));
        }
//        }
//        cont.putClientProperty(ScreenListOfItems.DISPLAYED_ELEMENT, workSlot);

//        return cont;
        return swipCont;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Container buildContentPaneForItemList(List<WorkSlot> workSlotList) {
//    protected Container buildContentPaneForItemListOLD(WorkSlotList workSlotList) {
//        parseIdMapReset();
////        MyTree dt = new MyTree(workSlotList) {
////            @Override
////            protected Component createNode(Object node, int depth) {
////                Component cmp = buildWorkSlotContainer((WorkSlot) node);
////                cmp.getSelectedStyle().setMargin(LEFT, depth * myDepthIndent);
////                return cmp;
////            }
////        };
////TODO!!! replace InfiniteContainer
//        InfiniteContainer cl = new InfiniteContainer() {
//            @Override
//            public Component[] fetchComponents(int index, int amount) {
//
////                List<WorkSlot> list = workSlotList.subList(index, Math.min(amount,workSlotList.size()-index-1));
//                List<WorkSlot> list = workSlotList.subList(index, index + Math.min(amount, workSlotList.size() - index));
//                if (list.isEmpty()) {
//                    return null;
//                }
//                Component[] comps = new Component[list.size()];
//                for (int i = 0, size = list.size(); i < size; i++) {
////                    comps[i] = buildWorkSlotContainer(list.get(i));
////                    comps[i] = buildWorkSlotContainer(list.get(i), () -> refreshAfterEdit(), keepPos);
//                    comps[i] = buildWorkSlotContainer(list.get(i), () -> refreshAfterEdit(), keepPos);
//                }
//                return comps;
//            }
//        };
//
//        Container cont = new Container(new BoxLayout(BoxLayout.Y_AXIS));
//        cont.setScrollableY(true);
//        cont.add(cl);
////        cont.setDraggable(true);
////        cl.setDropTarget(true);
//        return cont;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Container buildContentPaneForWorkSlotListXXX(WorkSlotList workSlotList) {
//        parseIdMapReset();
//        Container cont = new ContainerScrollY(BoxLayout.y());
//        cont.setScrollableY(true);
////        long now = System.currentTimeMillis();
//        if (workSlotList != null) {
//            long now = workSlotList.getNow();
////            for (WorkSlot workSlot : workSlotList) {
//            for (WorkSlot workSlot : workSlotList.getWorkSlots()) {
//
////                if (Test.DEBUG || workSlot.getEndTime() <= now) { //ignore workSlots in the past
//                if (Config.WORKTIME_TEST || workSlot.getEndTime() >= now) { //ignore workSlots in the past
////                    cont.add(buildWorkSlotContainer(workSlot, () -> refreshAfterEdit(), keepPos));
////                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, keepPos, false));
//                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, null, false, showOwner, now));
//                    if (keepPos != null) {
//                        keepPos.testItemToKeepInSameScreenPosition(workSlot, cont);
//                    }
//                }
//            }
//        }
//        return cont;
//    }
//</editor-fold>
    protected Container buildContentPaneForWorkSlotList(WorkSlotList workSlotListN) {
//    protected Container buildContentPaneForWorkSlotList(List<WorkSlot> workSlotList) {
        parseIdMap2.parseIdMapReset();

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false){
//        Container cont = new ContainerScrollY(BoxLayout.y());
//        cont.setScrollableY(true);
////        long now = System.currentTimeMillis();
//        if (workSlotList != null) {
//            long now = workSlotList.getNow();
////            for (WorkSlot workSlot : workSlotList) {
//            for (WorkSlot workSlot : workSlotList.getWorkSlots()) {
//
////                if (Test.DEBUG || workSlot.getEndTime() <= now) { //ignore workSlots in the past
//                if (Config.WORKTIME_TEST || workSlot.getEndTime() >= now) { //ignore workSlots in the past
////                    cont.add(buildWorkSlotContainer(workSlot, () -> refreshAfterEdit(), keepPos));
////                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, keepPos, false));
//                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, null, false, showOwner, now));
//                    if (keepPos != null) {
//                        keepPos.testItemToKeepInSameScreenPosition(workSlot, cont);
//                    }
//                }
//            }
//        }
//        return cont;}
//</editor-fold>
        if (workSlotListN != null && workSlotListN.size() > 0) {
            MyTree2 myTree = new MyTree2(workSlotListN, expandedObjects, getInlineInsertContainer(), stickyHeaderGen) {//                    lastInsertNewElementContainer != null ? 
                @Override
                protected Component createNode(Object node, int depth, ItemAndListCommonInterface itemOrItemList, Category category) {
                    Container cmp = null;
                    if (node instanceof WorkSlot) {
                        cmp = buildWorkSlotContainer((WorkSlot) node, ScreenListOfWorkSlots.this, null, false, showOwner, now);
                        return cmp;
                    } else if (node instanceof Item) {
                        cmp = ScreenListOfItems.buildItemContainer(ScreenListOfWorkSlots.this, (Item) node, null, null);
                    } else {
                        assert false;
                    }
                    setIndent(cmp, depth);
                    return cmp;
                }
            };
            return myTree;
        } else {
//                setInlineInsertContainer(new InlineInsertNewItemContainer2(this, null, workSlotList, null, false)); //UI: in an empty list you can insert a new task via the inlineInsert container
//                return (Container) getInlineInsertContainer(); //UI: in an empty list you can insert a new task via the inlineInsert container
//            return BoxLayout.encloseY(new Label("Add a " + WorkSlot.WORKSLOT + " using +"));
            return BorderLayout.centerCenter(new SpanLabel("Add a " + WorkSlot.WORKSLOT + " using +"));
        }

    }

}
