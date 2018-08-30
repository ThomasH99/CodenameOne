/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import com.codename1.ui.*;
import com.codename1.io.Log;
import com.codename1.ui.Button;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.InfiniteContainer;
import com.codename1.ui.Label;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import static com.todocatalyst.todocatalyst.ScreenListOfItems.makeSubtaskButton;
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
    private ItemAndListCommonInterface owner;
    private long now = new Date().getTime();
//    private List<WorkSlot> workSlotList;
    private WorkSlotList workSlotList;
//    private KeepInSameScreenPosition keepPos; // = new KeepInSameScreenPosition();
    private FetchWorkSlotList refreshWorkSlotList;
    private boolean showOwner; //true if show owner of workslots inline in list of workslots

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
     * @param updateItemListOnDone called to update the workTime for the tasks after the workSlots have been edited to refresh the workTime, e.g. if new slots were added/changed/removed
     * @param refreshWorkSlotList
     */
    ScreenListOfWorkSlots(String nameOfOwner, WorkSlotList workSlotList, ItemAndListCommonInterface owner, MyForm previousForm,
            GetWorkSlotList updateItemListOnDone, FetchWorkSlotList refreshWorkSlotList, boolean showOwner) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
//        super("Work time for " + nameOfOwner, previousForm, () -> updateItemListOnDone.update(workSlotList));
        super(SCREEN_TITLE + ((nameOfOwner != null && nameOfOwner.length() > 0) ? " for " + nameOfOwner : ""), previousForm,
                () -> {
                    if (updateItemListOnDone != null) {
                        updateItemListOnDone.update(workSlotList);
                    }
                });
//        setUpdateItemListOnDone(updateItemListOnDone);
//        this.workSlotList = workSLotList;
        this.owner = owner;
        this.workSlotList = workSlotList;
        this.refreshWorkSlotList = refreshWorkSlotList;
        this.showOwner = showOwner;
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
        addCommandsToToolbar(getToolbar());
//        setScrollableY(true);
        setLayout(BoxLayout.y());
//        getContentPane().setScrollableY(true);
        getContentPane().setScrollableY(false);
//        getContentPane().add(buildContentPaneForItemList(workSlotList));
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        ReplayLog.getInstance().clearSetOfScreenCommands(); //must be cleared each time we rebuild, otherwise same ReplayCommand ids will be used again
        getContentPane().removeAll();
//        List<WorkSlot> wsList = null;
        WorkSlotList wsList = null;
//        wsList = DAO.getInstance().getWorkSlotsN( owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//        if (owner instanceof ItemList) {
//            wsList = DAO.getInstance().getWorkSlotsN((ItemList) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//        } else if (owner instanceof Item) {
//            wsList = DAO.getInstance().getWorkSlotsN((Item) owner);
//        }
//        if (owner instanceof ItemAndListCommonInterface) {
//            wsList = DAO.getInstance().getWorkSlotsN((ItemList) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
        if (owner != null) {
            wsList = DAO.getInstance().getWorkSlotsN(owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
        } else if (refreshWorkSlotList != null) {
            wsList = new WorkSlotList(refreshWorkSlotList.getUpdatedWorkSlotList(workSlotList));
        }
//        }
//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForWorkSlotList(wsList));
        getContentPane().add(buildContentPaneForWorkSlotList(wsList));
//        if (this.keepPos != null) {
//            this.keepPos.setNewScrollYPosition();
//        }
        revalidate();
        restoreKeepPos();
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        //NEW WORKSLOT
        toolbar.addCommandToRightBar(MyReplayCommand.create("NewWorkSlot", "", Icons.iconNewToolbarStyle(), (e) -> {
            WorkSlot workSlot = new WorkSlot();
            workSlot.setOwner(owner); //MUST set owner before editing to ensure a possible RepeatRule will insert workslot repeatInstances in right owner list
            setKeepPos(new KeepInSameScreenPosition());
            new ScreenWorkSlot(workSlot, ScreenListOfWorkSlots.this, () -> {
                if (workSlot.hasSaveableData()) {
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
                    DAO.getInstance().save(workSlot); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    workSlotList.addItemAtIndex(workSlot, 0);
                    //save updated owner of workslot //TODO not necessary to save the owners (they are not modified for workslots since these are fetched via a Parse query)??!!
                    if (false) {
                        if (owner instanceof ItemList) {
                            DAO.getInstance().save((ItemList) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
                        } else if (owner instanceof Item) {
                            DAO.getInstance().save((Item) owner); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
//                    } else { //TODO:
//                        throw RuntimeException("Unknown type of owner");
                        }
                    }
                    if (false) { //shouldn't be necessary since workSlotList will be re-read in refreshAfterEdit()
                        workSlotList.add(workSlot);
//                            WorkSlot.sortWorkSlotList(workSlotList);
                        workSlotList.sortWorkSlotList();
                    }
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        //BACK
//        toolbar.addCommandToLeftBar(makeDoneCommand("", FontImage.createMaterial(FontImage.MATERIAL_ARROW_BACK, toolbar.getStyle())));
        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand(true));
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
    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm myForm, KeepInSameScreenPosition keepPos, boolean expandItemsInWorkSlot, boolean showOwner) {
        return buildWorkSlotContainer(workSlot, myForm, keepPos, expandItemsInWorkSlot, showOwner, System.currentTimeMillis());
    }

    protected static Container buildWorkSlotContainer(WorkSlot workSlot, MyForm myForm, KeepInSameScreenPosition keepPos,
            boolean expandItemsInWorkSlot, boolean showOwner, long now) {
        Container cont = new Container();
        cont.setLayout(new BorderLayout());
//        cont.addComponent(BorderLayout.CENTER, new Button(item.getText()));
        //EDIT items in category
//        Button editItemButton = new Button(new Command(workSlot.getText()) {
        Button editWorkSlotButton = new Button(Icons.iconEditSymbolLabelStyle);
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
        editWorkSlotButton.setCommand(MyReplayCommand.create("Edit WorkSLot:" + workSlot.getObjectIdP(), null, Icons.iconEditSymbolLabelStyle, (e) -> {
//                keepPos.setKeepPos(new KeepInSameScreenPosition());
//            ((MyForm) cont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(workSlot, cont));
            myForm.setKeepPos(new KeepInSameScreenPosition(workSlot, cont));
//                new ScreenWorkSlot(workSlot, ScreenListOfWorkSlots.this, () -> {
//            new ScreenWorkSlot(workSlot, (MyForm) cont.getComponentForm(), () -> {
            new ScreenWorkSlot(workSlot, myForm, () -> {
                //TODO!!! add same check as when creating a new WorkSlot (if both StartDate and Duration deleted, delete the workslot)??
//                            workSlot.setList(itemList.getList());
                DAO.getInstance().save(workSlot);
//                    refreshAfterEdit();
//                refreshOnItemEdits.launchAction();
                myForm.refreshAfterEdit();
            }).show();
        }
        ));
//        editItemButton.setUIID("Label");
        cont.addComponent(BorderLayout.EAST, editWorkSlotButton);

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
//</editor-fold>
        String startTimeStr = (workSlot.getStartAdjusted(now) != workSlot.getStartTimeD().getTime())
                ? "Now"
                : MyDate.formatDateTimeNew(new Date(workSlot.getStartAdjusted(now))); //UI: for ongoing workSlot, show 'now' instead of startTime
        startTimeStr += "-" + MyDate.formatTimeNew(new Date(workSlot.getEndTime()))
                + (workSlot.getRepeatRule() != null ? "*" : ""); //                + " " + MyDate.formatTimeDuration(workSlot.getDurationInMillis())// + ")"
        //                + " " + MyDate.formatTimeDuration(workSlot.getDurationAdjusted())// + ")" //DON'T show duration since end-time is shown
        west.add(startTimeStr);
//            static String formatDateNew(Date date, boolean useYesterdayTodayTomorrow, boolean includeDate, boolean includeTimeOfDay, boolean includeDayOfWeek, boolean useUSformat) {

//        east.addComponent(new Label("[" + workSlot.getDurationAdjustedInMinutes(now) + "]"));
//        west.add(MyDate.formatTime(workSlot.getDurationInMillis()));
//        west.add("(" + MyDate.formatTimeDuration(workSlot.getDurationInMillis()) + ")");
//        cont.addComponent(BorderLayout.CENTER, new Label(workSlot.getText() + (workSlot.getRepeatRule() != null ? "*" : "")));
        if (showOwner && workSlot.getOwner() != null) {
            cont.addComponent(BorderLayout.CENTER, new Label(workSlot.getOwner().getText()));
        }
//        }
//        east.addComponent(editItemPropertiesButton);

        List<Item> items = workSlot.getItemsInWorkSlot();
        if (items != null && items.size() > 0) {
            Button showItemsInWorkSlotButton = new Button(new Command("[" + items.size() + "]"));
            showItemsInWorkSlotButton.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, showItemsInWorkSlotButton);
            west.addComponent(showItemsInWorkSlotButton);
        }

//        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
        cont.addComponent(BorderLayout.WEST, west);
        Container south = new Container(BoxLayout.y());
        cont.addComponent(BorderLayout.SOUTH, south);

//        if (workSlot.getOwner() != null) {
//            if(showOwner)south.addComponent(new Label("For: " + workSlot.getOwner().getText()));
        if (workSlot.getText() != null && workSlot.getText().length() > 0) {
            south.addComponent(new Label(("\"" + workSlot.getText() + "\"")));
        }
//        }
        cont.putClientProperty("element", workSlot);

        return cont;
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
    protected Container buildContentPaneForWorkSlotList(WorkSlotList workSlotList) {
        parseIdMapReset();
        Container cont = new ContainerScrollY(BoxLayout.y());
        cont.setScrollableY(true);
//        long now = System.currentTimeMillis();
        if (workSlotList != null) {
            long now = workSlotList.getNow();
            for (WorkSlot workSlot : workSlotList) {

//                if (Test.DEBUG || workSlot.getEndTime() <= now) { //ignore workSlots in the past
                if (Config.WORKTIME_TEST || workSlot.getEndTime() >= now) { //ignore workSlots in the past
//                    cont.add(buildWorkSlotContainer(workSlot, () -> refreshAfterEdit(), keepPos));
//                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, keepPos, false));
                    cont.add(buildWorkSlotContainer(workSlot, ScreenListOfWorkSlots.this, null, false, showOwner, now));
                    if (keepPos != null) {
                        keepPos.testItemToKeepInSameScreenPosition(workSlot, cont);
                    }
                }
            }
        }
        return cont;
    }

}
