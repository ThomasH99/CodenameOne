package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Log;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.TextArea;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.util.EventDispatcher;
import com.codename1.util.regex.RE;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.AlarmType.notification;
import static com.todocatalyst.todocatalyst.AlarmType.waiting;
import com.todocatalyst.todocatalyst.MyDate;
import static com.todocatalyst.todocatalyst.MyForm.REPEAT_RULE_KEY;
import static com.todocatalyst.todocatalyst.MyForm.getListAsCommaSeparatedString;
import com.todocatalyst.todocatalyst.MyPrefs;
import static com.todocatalyst.todocatalyst.MyUtil.removeTrailingPrecedingSpacesNewLinesEtc;
import java.io.OutputStreamWriter;
import java.io.Writer;
//import com.todocatalyst.todocatalyst.MyTree.MyTreeModel;
//import com.todocatalyst.todocat.AlarmServer.AlarmObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import com.todocatalyst.todocatalyst.MyUtil;
import static com.todocatalyst.todocatalyst.MyUtil.eql;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;
//import sun.security.acl.OwnerImpl;
//import todo.TodoMidlet43.Categories;

/**
 *
 * @author Thomas
 */
public class Item /* extends BaseItemOrList */ extends ParseObject implements
        MyTreeModel, ItemAndListCommonInterface, IComparable,
        RepeatRuleObjectInterface, Externalizable { //Externalizable, AlarmObject, SumField,  {
    //          ItemListChangeListener,
    //        FilterableObject, 
    //TODO!!!! when deleting an item, also needs to remove it from cache (cached lists, categories, projects, ...) - are cached copies not updated automatically when saving?!
    //TODO support 'star' marking (add/remove, sort, multi-selection operation?no)
    //TODO should remaining time be set 0 when task is done (you loose information and filtering on tasks should be done on state, not remaining time)?
    //TODO calculate a numerical value for Importance/Urgency and support sorting on it (like in first version)
    //TODO move Remaining time to first tab
    //TODO implement support for auto-cancel tasks (how to trigger recurring actions like this?? - daily timer, run on first activation?)
    //TODO set startedOn date when one of the subtasks is started (require subtasks to call up to owner when defined)
    //TODO set Difficulty default value to undefined
    //TODO set Fun/Dread default value to undefined
    //TODO add new calculated value Value/Remaining to calculate for which tasks there is the most value to be closed by finishing them (in addition tof value per hour which uses Actual+Remaining)

    public final static String CLASS_NAME = "Item";
    /**
     * allows to store actions that are only executed once the Item has been
     * saved and thus received its objectId. Uses HashMap to ensure that only
     * one (the latest) action is stored for each field.
     */
//    private Map<String, MyForm.Action> afterSaveActions = new HashMap();
//    private final static String AFTER_SAVE_TEXT_UPDATE = "Text";
//    private final static String AFTER_SAVE_ALARM_UPDATE = "AlarmDate";
//    private final static String ALARM_UPDATE = "UpdateAlarm";
    private boolean mustUpdateAlarms = false;
    private boolean noSave = false;
    private FilterSortDef hardcodedFilter = null;

    /**
     * Copied from CN1 OnOffSwitch.java
     *
     */
    private EventDispatcher dispatcher = new EventDispatcher();
    private EventDispatcher listeners = new EventDispatcher();

//    private List<WorkSlot> workSlotListBuffer;
//    private WorkSlotList workSlotListBuffer;
//    private static WorkTimeDefinition wtd; //calculated when needed
    private WorkTimeAllocator workTimeAllocator; //calculated when needed
//    private WorkTimeSlices workTime;// = new ItemList(); //lazy
    private boolean neverCacheWorkTimeAllocator = false;// = new ItemList(); //lazy

//    private WorkTimeDefinition workTimeDefinitionBuffer;
    public Item() {
        super(CLASS_NAME);
//        setRemainingForProjectTaskItselfInParse(getRemainingDefaultValue());
        setRemainingForProjectTaskItselfInParse(getRemainingDefaultValue()); //UI: only set remaining, NOT estimate, since this is only a default value (and it may be a way to distinguish default values from user-entered?!)
    }

//    public Item(Item source) {
//        this();
//        source.copyMeInto(this);
//    }
    public Item(Item owner) {
        this();
        updateValuesInheritedFromOwner(owner);
    }

    public Item(String text) {
        this();
        setText(text);
    }

    public Item(String text, boolean interpretTextValues) {
        this();
        if (interpretTextValues) {
            setText(parseTaskTextForProperties(this, text));
        } else {
            setText(text);
        }
    }

    /**
     * constructor for test values
     *
     * @param text
     */
    public Item(String taskText, int remainingEffortInMinutes) {
        this(taskText, remainingEffortInMinutes, null);
    }

    public Item(String taskText, int remainingEffortInMinutes, Date dueDate) {
//        this(taskText, remainingEffortInMinutes, dueDate, false);
        this();
        setText(taskText);
        Item.this.setRemaining(((long) remainingEffortInMinutes) * MyDate.MINUTE_IN_MILLISECONDS, true);
        setDueDate(dueDate);
    }

    public Item(String taskText, int remainingEffortInMinutes, Date dueDate, boolean saveToDAO) {
        this();
        setText(taskText);
        Item.this.setRemaining(((long) remainingEffortInMinutes) * MyDate.MINUTE_IN_MILLISECONDS, true);
        setDueDate(dueDate);
        if (saveToDAO) {
            DAO.getInstance().saveInBackground(this);
        }
    }

    @Override
    public String toString(ToStringFormat format) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//, ExpandableInterface {

    @Override
    public void copyMeInto(ItemAndListCommonInterface destiny) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public List getChildrenList() {
//        List itemList = getList();
//        DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//        return itemList;
//    }
//    @Override
//    public boolean isLeaf() {
//        List itemList = getList();
//        return itemList == null || itemList.size() == 0;
//    }
    @Override
    public List getChildrenList(Object subTaskInThisProject) {
        if (subTaskInThisProject == null) {
//            List l = getList();
//            if (l == null) {
//                return new ArrayList();
//            } else {
            List itemList = getList(); //not full list, only show unfiltered
//                DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//                DAO.getInstance().fetchAllElementsInSublist(itemList);
            return itemList; //see JavaDoc of getChildren: null should return the tree roots
//            }
        } else {
//            List itemList = ((Item) parent).getList();
//            DAO.getInstance().fetchAllElementsInSublist(itemList, false);
////            return new Vector(itemList);
//            return itemList;
            return ((MyTreeModel) subTaskInThisProject).getChildrenList(null);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List getChildrenListOLD(Object parent) {
//        if (parent == null) {
//            List list = new ArrayList();
//            list.add(this);
//            return list;
//        } else {
//            List itemList = ((Item) parent).getList();
////            DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(itemList);
////            return new Vector(itemList);
//            return itemList;
//        }
//    }
//    @Override
//    public Vector getChildren(Object parent) {
//        if (parent == null) {
//            Vector vector = new Vector();
//            vector.add(this);
//            return vector;
//        } else {
//            List itemList = ((Item) parent).getList();
//            DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//            return new Vector(itemList);
//        }
//    }
//</editor-fold>
    @Override
    public boolean isLeaf(Object node) {
//        return getItemList() == null || getItemList().size() == 0;
        Item item = (Item) node;
//        ItemList itemList = item.getItemList();
        List itemList = item.getList(); //not full list since we only want to expand is there are unfiltered items
        return itemList == null || itemList.size() == 0;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Object removeFromOwnerXXX() {
////        ItemAndListCommonInterface owner = getOwner(); //TODO simplify code by adding getOwner to ItemAndListCommonInterface
//        ItemList ownerList = getOwnerList();
//        if (ownerList != null) {
//            boolean successfullyRemoved = ownerList.remove(this);
//            assert successfullyRemoved : "item " + this + " not successfully removed from ownerList " + ownerList;
//            DAO.getInstance().save(ownerList);
//            return ownerList;
//        } else {
//            Item ownerItem = getOwnerItem();
//            if (ownerItem != null) {
////                ownerItem.getItemList().remove(this);
//                ownerItem.getList().remove(this);
//                DAO.getInstance().save(ownerItem);
//                return ownerItem;
//            }
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List getInsertNewRepeatInstancesIntoListXXX() {
//        Object owner = getOwner();
//        if (owner instanceof ItemList) {
//            return (ItemList) owner;
//        } else if (owner instanceof Item) {
//            return getList();
//        } else {
//            ASSERT.that(false, "Wrong type of owner = " + owner);
//            return null;
//        }
////        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    public void saveInsertListXXX() {
//        Object owner = getOwner();
//        if (owner instanceof ItemList) {
//            DAO.getInstance().save((ItemList) owner);
//        } else if (owner instanceof Item) {
//            DAO.getInstance().save((Item) owner);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void insertIntoListAndSaveListAndInstanceOLD(RepeatRuleObjectInterface newRepeatRuleInstance) {
////        DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save first to be able to reference from other objects
////        RepeatRuleObjectInterface orgInstance = this;
//        Object owner = getOwner();
////        int index;
//        if (owner instanceof ItemList) {
//            ItemList ownerList = (ItemList) owner;
////            assert orgInstance != null;
//            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()
//                    //                    && (index = ownerList.indexOf(this)) != -1) {
//                    && (ownerList.indexOf(this)) != -1) {
////                ownerList.add(index + 1, newRepeatRuleInstance); //+1: insert *after* orgInstance
////                ownerList.addToList(index + 1, (ItemAndListCommonInterface) newRepeatRuleInstance); //+1: insert *after* orgInstance
//                ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); //+1: insert *after* orgInstance
//            } else {
////<editor-fold defaultstate="collapsed" desc="comment">
////                ownerList.addToList(MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : ownerList.size(), (ItemAndListCommonInterface) newRepeatRuleInstance);
////                ownerList.addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : ownerList.size(), (ItemAndListCommonInterface) newRepeatRuleInstance);
////                if (MyPrefs.insertNewItemsInStartOfLists.getBoolean())
////                    ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, ownerList.getItemAt(0), false);
////                else
////                    ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, ownerList.getItemAt(ownerList.size()), true);
////</editor-fold>
//                ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance);
//            }
////            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
////            DAO.getInstance().save(ownerList); //TODO!!!! optimization: when generating multiple repeat instances, do the save of the list at the end
//            DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance, ownerList); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        } else if (owner instanceof Item) {
//            Item itemOwner = (Item) owner;
//            List subtaskList = itemOwner.getList();
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (subtaskList != null && (index = subtaskList.indexOf(newRepeatRuleInstance)) != -1) {
////                subtaskList.add(index + 1, newRepeatRuleInstance);
////            } else {
////                subtaskList.add(MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : subtaskList.size(), newRepeatRuleInstance);
////            }
////</editor-fold>
//            assert subtaskList != null; //should never get null
//            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()
//                    //                    && (index = subtaskList.indexOf(newRepeatRuleInstance)) != -1) {
//                    //                    && (index = subtaskList.indexOf(this)) != -1) {
//                    && (subtaskList.indexOf(this)) != -1) {
////                itemOwner.addToList(index + 1, (ItemAndListCommonInterface) newRepeatRuleInstance); //add just after
//                itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); //add just after
//            } else {
////<editor-fold defaultstate="collapsed" desc="comment">
////                itemOwner.addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : subtaskList.size(), (ItemAndListCommonInterface) newRepeatRuleInstance);
////                if (MyPrefs.insertNewItemsInStartOfLists.getBoolean())
////                    itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) itemOwner.get(0), false);
////                else
////                    itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, (ItemAndListCommonInterface) itemOwner.get(itemOwner.size()), true);
////</editor-fold>
//                itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance);
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            item.setList(subtaskList); //done in addToList
////            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
////            DAO.getInstance().save(itemOwner);
////</editor-fold>
//            DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance, itemOwner); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        } else { //no owner, save for 'Inbox'
////            assert false;
//            DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        }
//    }
//</editor-fold>
    @Override
    public ItemAndListCommonInterface insertIntoList(RepeatRuleObjectInterface newRepeatRuleInstance) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Object owner = getOwner();
//        if (owner instanceof ItemList) {
//            ItemList ownerList = (ItemList) owner;
//            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
//                ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
//            } else {
//                ownerList.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
//            }
//            DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance, ownerList); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        } else if (owner instanceof Item) {
//            Item itemOwner = (Item) owner;
//            List subtaskList = itemOwner.getList();
//            assert subtaskList != null; //should never get null
//            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()){// && (subtaskList.indexOf(this)) != -1) {
//                itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); //add just after
//            } else {
//                itemOwner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance);
//            }
//            DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance, itemOwner); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        } else { //no owner, save for 'Inbox'
//            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
//        }
//</editor-fold>
        ItemAndListCommonInterface owner = getOwner();
        if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()) {// && (ownerList.indexOf(this)) != -1) {
            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, this, true); ///NB. no need to check if 'this' is in list since this insert defaults normal add if not
        } else {
            owner.addToList((ItemAndListCommonInterface) newRepeatRuleInstance, !MyPrefs.insertNewItemsInStartOfLists.getBoolean());
        }
//        DAO.getInstance().saveInBackground((ParseObject) newRepeatRuleInstance, (ParseObject) owner); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
        return owner;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Object insertBelow(Movable element) {
//        List list = getList();
////            if (list!=null)
//        list.add(0, element);
//        setItemList(list);
//        DAO.getInstance().save(this);
////            else assert false:"";
//        return list;
//    }
//    public boolean insertIntoOwner(Movable element, int index) {
//    @Override
//    public Object insertIntoOwnerAtPositionOf(Movable elementToInsert) {
//        Movable elementAtInsertPosition = this;
//        List ownerList = null;
//        if (elementToInsert instanceof Item) {
//            ItemAndListCommonInterface owner = getOwner();
//            if (owner != null && owner instanceof ItemAndListCommonInterface) {
//                ownerList = ((ItemAndListCommonInterface) owner).getList();
//                ownerList.add(ownerList.indexOf(elementAtInsertPosition), elementToInsert);
//                ((ItemAndListCommonInterface) owner).setList(ownerList);
////                if (owner instanceof Item) {
////                    ownerList = ((Item) owner).getList();
////                    ownerList.add(ownerList.indexOf(elementAtInsertPosition), elementToInsert);
////                    ((Item) owner).setItemList(ownerList);
////                } else if (owner instanceof ItemList) {
////                    ownerList = ((ItemList) owner).getList();
////                    ownerList.add(ownerList.indexOf(elementAtInsertPosition), elementToInsert);
////                    ((ItemList) owner).setList(ownerList);
//                DAO.getInstance().save(owner);
//                return ownerList;
//            }
//        }
//        return null;
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////            ItemList ownerList = getOwnerList();
////            if (ownerList != null) {
////                ownerList.addItemAtIndex((Item) elementToInsert, ownerList.getItemIndex(elementAtInsertPosition));
////                DAO.getInstance().save(ownerList);
////                return ownerList;
////            } else {
////                Item ownerItem = getOwnerItem();
////                if (ownerItem != null) {
//////                    ownerItem.getItemList().addItemAtIndex((ItemAndListCommonInterface) element, ownerItem.getItemList().getItemIndex(elementAtInsertPosition));
////                    List ownerSubtasks = ownerItem.getList();
////                    ownerItem.getList().add(ownerSubtasks.indexOf(elementAtInsertPosition), (ItemAndListCommonInterface) elementToInsert);
////                    ownerItem.setItemList(ownerSubtasks);
////                    DAO.getInstance().save(ownerItem);
////                    return ownerItem;
////                }
////            }
////        }
////        return null;
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    }
//    public Object insertIntoOwnerAtPositionOfXXX(Movable elementToInsert, Movable elementAtInsertPosition) {
//        if (elementToInsert instanceof Item) {
//            ItemList ownerList = getOwnerList();
//            if (ownerList != null) {
//                ownerList.addItemAtIndex((Item) elementToInsert, ownerList.getItemIndex(elementAtInsertPosition));
//                DAO.getInstance().save(ownerList);
//                return ownerList;
//            } else {
//                Item ownerItem = getOwnerItem();
//                if (ownerItem != null) {
////                    ownerItem.getItemList().addItemAtIndex((ItemAndListCommonInterface) element, ownerItem.getItemList().getItemIndex(elementAtInsertPosition));
//                    ownerItem.getList().add(ownerItem.getList().indexOf(elementAtInsertPosition), (ItemAndListCommonInterface) elementToInsert);
//                    DAO.getInstance().save(ownerItem);
//                    return ownerItem;
//                }
//            }
//        }
//        return null;
//    }
//    public enum ItemStatus {
//        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
//        CREATED("Created"),
//        /**
//         * work has started / is currently being worked on
//         */
//        ONGOING("In progress"),
//        /**
//         * work has started, but has been put on hold or is waiting for
//         * something from the outside (remaining can't do work on it until some
//         * external event or input happens)
//         */
//        WAITING("Waiting"),
//        DONE("Done"),
//        CANCELLED("Cancelled");
//
//        ItemStatus(String description) {
//            this.description = description;
//        }
//
//        String getDescription() {
//            return description;
//        }
//
//        static String[] getDescriptionList() {
//            return new String[]{
//                CREATED.getDescription(), ONGOING.getDescription(), WAITING.getDescription(), DONE.getDescription(), CANCELLED.getDescription()};
//        }
//
//        static int[] getDescriptionValues() {
//            return new int[]{
//                CREATED.ordinal(), ONGOING.ordinal(), WAITING.ordinal(), DONE.ordinal(), CANCELLED.ordinal()};
//        }
//
//        private final String description;
//    }
//
//    public static String getStatusName(ItemStatus status) {
////        return STATUS_NAMES[status];
//        return status.getDescription();
//    }
//
//    public static String[] getStatusNames() {
////        return STATUS_NAMES;
//        return ItemStatus.getDescriptionList();
//    }
//
//    public static int[] getStatusValues() {
////        return STATUS_VALUES;
//        return ItemStatus.getDescriptionValues();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * the object that contains, or 'owns', this object. An object can have no
     * more than one owner. When the owner is deleted, so is the object.
     */
//    private ItemAndListCommonInterface owner;
//    private String description; //STORED
//    private String comment = "";
//    private String description = "";
    //private ItemList subitems = new ItemList();
//    private ItemList<Item> subitems; // = new ItemListAutosaveParent(this); //optimization: avoid creating this list until/unless it is actually needed 8save memory)
    //private ItemList categories = new ItemList(); //Categories.getInstance().someValues();
//    private CategoryListConsistency categories = new CategoryListConsistency(); //Categories.getInstance().someValues();
//    private ItemToCategoryConsistency categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
    //private ItemList categories2 = new CategoryListConsistency(); //Categories.getInstance().someValues();
//    private ItemListSaveInline categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
//    private ItemListCategoryConsistency categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
//    private ItemList categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
//    private ItemStatus status = ItemStatus.STATUS_CREATED;
    /**
     * first(!) date when the item was set to Ongoing
     */
//    private long startedOnDate;
    /**
     * last date when item was set to Done state (or when it was cancelled!)
     */
//    private long completedDate;
//    private long createdDate;
    /**
     * date until which the task is put into status Waiting
     */
//    private long waitingTillDate;
    /**
     * date when the task was *last* set to status Waiting - allows you to not
     * set a waitingTillDate and still see which tasks have been waiting the
     * longest time
     */
//    private long waitingLastActivatedDate;
//    private long dueDate;
//    private long showFromDate; //TODO!!!: update all fields
//    //private boolean alarmSet; //TODO: necessary or just use zero-value for dates?
//    private long alarmDate;
    /**
     * date for Waiting reminder
     */
//    private long waitingAlarmDate;
//    private long snoozeDate; //is not saved
    /**
     * initial effort estimate kept for history and later accuracy evaluations,
     * not receiveChangeEvent automatically later. Can either be set
     * automatically to the same as the very left estimate entered, or can be
     * set explicitly to reflect the current estimate, or can be updated
     * manually??
     */
//    private long effortEstimate;// = 0;
    /**
     * latest estimate of remaining effort in milliseconds
     */
//    private long remainingEffort;// = 0;
    /**
     * unsaved variable storing sum of effort of subitems. -1 means not
     * initialized
     */
//</editor-fold>
    /**
     * actual effort in milliseconds
     */
//    private long actualEffort;// = 0;
    interface UpdaterInterface {

        Object getValue();
    }

    /**
     * buffers a calculated valuate, to ensure that intensive calculations are
     * only done when needed. First time it is called the value will always be
     * calculated by calling the updater.getValue(). After that the
     * updater.getValue() will only be called to refresh the buffered value if
     * reset() has been called.
     */
//    class BufferedValueXXX {
//
//        BufferedValueXXX(UpdaterInterface updater) {
//            this.updater = updater;
//        }
//        boolean needsRecalculation = true;
//        Object value;
//        UpdaterInterface updater;
//
//        void reset() {
//            needsRecalculation = true;
//        }
//
//        Object getValue() {
//            if (needsRecalculation) {
//                value = updater.getValue();
//                needsRecalculation = false;
//            }
//            return value;
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private BufferedValueXXX derivedRemainingEffortSubItemsSumBuffered = new BufferedValueXXX(new UpdaterInterface() {
//        public Object getValue() {
//            long subItemSum = 0;
////            for (int item: getItemList()) {
//            for (int i = 0, size = getItemListSize(); i < size; i++) {
////                Item item = ((Item) getItemList().getItemAt(i));
//                Item item = (Item) getList().get(i);
//                if (!item.isDone()) { // /** || includeDone */) {
//                    subItemSum += item.getRemainingEffort();
//                }
//            }
//            return new Long(subItemSum);
//        }
//    });
//    private BufferedValueXXX derivedEstimateEffortSubItemsSumBuffered = new BufferedValueXXX(new UpdaterInterface() {
//        public Object getValue() {
//            long subItemSum = 0;
//            for (int i = 0, size = getItemListSize(); i < size; i++) {
////                Item item = ((Item) getItemList().getItemAt(i));
//                Item item = (Item) getList().get(i);
//                if (!item.isDone()) { // /** || includeDone */) {
//                    subItemSum += item.getEffortEstimate();
//                }
//            }
//            return new Long(subItemSum);
//        }
//    });
//    private BufferedValueXXX derivedActualEffortSubItemsSumBuffered = new BufferedValueXXX(new UpdaterInterface() {
//        public Object getValue() {
//            long subItemSum = 0;
//            for (int i = 0, size = getItemListSize(); i < size; i++) {
////                Item item = ((Item) getItemList().getItemAt(i));
//                Item item = (Item) getList().get(i);
//                if (!item.isDone() /*
//                         * || includeDone
//                         */) {
//                    subItemSum += item.getActualEffort();
//                }
//            }
//            return new Long(subItemSum);
//        }
//    });
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * last date when the item was modified (or created) - only real changes
     * should count, not viewing, or editing and canceling.
     */
//    private long lastModDate; //
//    private int priority; // = 1; //
//    private Priority priority = new Priority(); // = 1; //
//    private Priority priority = new PriorityImpUrgencyPair(this); // = 1; //TODO!!!: move this to constructor and use a setting for default type of priority (should be possible to use different types of prioirities per list/category/task??!! - isolate in a specific editor where you can choose your principle!)
    /**
     * "Worth" how many 'points' or "Thomas dollar" does this task give. Can be
     * used to calculate "ROI": points/effort, as well as an alternative
     * priority, e.g. sort on points to show most 'valuable' tasks
     */
//    private double earnedValue; // = 12.5; //
//    private RepeatRuleParseObject repeatRule; // = 1; //
    /**
     * this task was created as an 'interupt task' - makes it easy to find these
     * later on and get a view of them. TODO: would it be easier to
     * search/filter etc. on this type of tasks if another field was used for
     * the information? E.g. Status?
     */
//    private boolean interuptTask; //
    /**
     * is this a fun/enjoyable/"I love"/"I'd like to do this" or dread/chore/"I
     * hate"/"I don't like this kind of task" task?
     */
//    private int dreadFunValue;
    /**
     * the date on which a task automatically expires (is deleted if in state
     * Created)
     */
//    private long expiresOnDate;
    /**
     * date on which work can or should start - like in OmniFocus
     */
//    private long startByDate;
//</editor-fold>
    /**
     * is this a an easy/difficult task? difficult ones you do when you're
     * fresh/alert/concentrated, easy ones you do when you're tired, not in
     * shape, to rest your mind, ... Word to describe it: Challenge, Heavy,
     * Demanding, *Difficulty*, Challenging, Mental challenge, Intellectual,
     */
//    private int challenge;
    final static int CLASS_NONE_DEFINED = -1; //=
    final static int CLASS_CONFIDENTIAL = 1; //=
    final static int CLASS_PRIVATE = 2;
    final static int CLASS_PUBLIC = 3;
    /**
     * equivalent to ToDo.Class: CONFIDENTIAL, PRIVATE, PUBLIC
     */
//    private int classAccessibility = Settings.getInstance().getDefaultClassAccessibility(); //
    final static int COMPARE_DESCRIPTION = 1;
    final static int COMPARE_COMPLETED = 2;
    final static int COMPARE_COMPLETED_DATE = 3;
    final static int COMPARE_CREATED_DATE = 4;
    final static int COMPARE_DUE_DATE = 5;
//    final static int COMPARE_SHOWFROM_DATE = 6;
    final static int COMPARE_HIDE_UNTIL_DATE = 6;
    final static int COMPARE_ALARM_DATE = 7;
    final static int COMPARE_EFFORTESTIMATE = 8;
    final static int COMPARE_ACTUALEFFORT = 9;
    final static int COMPARE_LASTMOD_DATE = 10;
    final static int COMPARE_PRIORITY = 11;
    final static int EXPAND_SUBITEMS = 1; // expand the subitems of the list

    final static int FIELD_DESCRIPTION = 0;
    final static int FIELD_DONE = 1;
    final static int FIELD_DUE_DATE = 2;
    final static int FIELD_MODIFIED_DATE = 3;
    final static int FIELD_COMPLETED_DATE = 4;
    final static int FIELD_CREATED_DATE = 5;
    final static int FIELD_COMMENT = 6;
    final static int FIELD_EFFORT_ESTIMATE = 7;
    final static int FIELD_EFFORT_ACTUAL = 8;
    final static int FIELD_EFFORT_REMAINING = 9;
    final static int FIELD_STATUS = 10;
    final static int FIELD_PRIORITY = 11;
    final static int FIELD_EARNED_POINTS = 12;
    final static int FIELD_EARNED_POINTS_PER_HOUR = 13;
    final static int FIELD_ALARM_DATE = 14;
    final static int FIELD_WAIT_DATE = 15;
    final static int FIELD_WAITING_ALARM_DATE = 16;
    final static int FIELD_STARTED_ON_DATE = 17;
//    final static int FIELD_ROI = 18;
    final static int FIELD_START_WORK_TIME = 19;
    final static int FIELD_FINISH_WORK_TIME = 20;
    final static int FIELD_TIMESPAN = 21;
    final static int FIELD_EFFORT_TOTAL = 22;
    final static int FIELD_CATEGORIES = 23;
    final static int FIELD_INTERRUPT = 24;
    final static int FIELD_PRIORITY_IMPORTANCE = 25;
    final static int FIELD_PRIORITY_URGENCY = 26;
    final static int FIELD_OWNER = 27;
    final static int FIELD_REPEAT_RULE = 28;
    final static int FIELD_EXPIRES_ON_DATE = 29;
    final static int FIELD_DREAD_FUN = 30;
    final static int FIELD_START_BY_TIME = 31;
    final static int FIELD_CHALLENGE = 32;
//    private final static FieldDef[] FIELDS = {
//        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_STRING, "Help text for Description"),
//        //        new FieldDef(FIELD_DONE, "Done", Expr.VALUE_FIELD_TYPE_STRING),
//        new FieldDef(FIELD_DONE, "Done", Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        new FieldDef(FIELD_DUE_DATE, "Due", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_MODIFIED_DATE, "Modified", Expr.VALUE_FIELD_TYPE_DATE, "Date when this task was last modified"),
//        new FieldDef(FIELD_COMPLETED_DATE, "Completed", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_CREATED_DATE, "Created", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_COMMENT, "Comment", Expr.VALUE_FIELD_TYPE_STRING),
//        new FieldDef(FIELD_EFFORT_ESTIMATE, "Effort estimate", Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_EFFORT_ACTUAL, "Actual effort", Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_EFFORT_REMAINING, "Remaining effort", Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_STATUS, "Task status", Expr.VALUE_FIELD_TYPE_ENUM),
//        new FieldDef(FIELD_PRIORITY, "Priority", Expr.VALUE_FIELD_TYPE_INTEGER),
//        new FieldDef(FIELD_EARNED_POINTS, "Value", Expr.VALUE_FIELD_TYPE_INTEGER),
//        new FieldDef(FIELD_EARNED_POINTS_PER_HOUR, "Value/Effort", Expr.VALUE_FIELD_TYPE_INTEGER, "Set Work time to calculate Value per hour of effort"),
//        new FieldDef(FIELD_ALARM_DATE, "Alarm date", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_WAIT_DATE, "Waiting until", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_WAITING_ALARM_DATE, "Wait until date", Expr.VALUE_FIELD_TYPE_DATE), //"Waiting reminder", "Wait until date"
//        new FieldDef(FIELD_STARTED_ON_DATE, "Started", Expr.VALUE_FIELD_TYPE_DATE),
//        //        new FieldDef(FIELD_ROI, "ROI", Expr.VALUE_FIELD_TYPE_INTEGER), //- double definition of FIELD_EARNED_POINTS_PER_HOUR
//        new FieldDef(FIELD_START_WORK_TIME, "Start work", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_FINISH_WORK_TIME, "Finish work", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_TIMESPAN, "Timespan", Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_EFFORT_TOTAL, "Total effort", Expr.VALUE_FIELD_TYPE_DURATION),
//        new FieldDef(FIELD_CATEGORIES, "Categories", Expr.VALUE_FIELD_TYPE_STRING),//TODO!!!!! should categories appear here??
//        new FieldDef(FIELD_INTERRUPT, "Interrupt task", Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        //        new FieldDef(FIELD_PRIORITY_IMPORTANCE, "Importance", Expr.VALUE_FIELD_TYPE_INTEGER),
//        new FieldDef(FIELD_PRIORITY_IMPORTANCE, "Importance", Expr.VALUE_FIELD_TYPE_ENUM),
//        //        new FieldDef(FIELD_PRIORITY_URGENCY, "Urgency", Expr.VALUE_FIELD_TYPE_INTEGER),
//        new FieldDef(FIELD_PRIORITY_URGENCY, "Urgency", Expr.VALUE_FIELD_TYPE_ENUM),
//        new FieldDef(FIELD_OWNER, "Owner", Expr.VALUE_FIELD_TYPE_STRING), //TODO!!!!: what field type to use for Owner? String to return the owner's name
//        new FieldDef(FIELD_REPEAT_RULE, "Repeat", Expr.VALUE_FIELD_TYPE_STRING), //TODO: what field type to use for RepeatRule?
//        new FieldDef(FIELD_EXPIRES_ON_DATE, "Expires", Expr.VALUE_FIELD_TYPE_DATE),
//        new FieldDef(FIELD_START_BY_TIME, "Start", Expr.VALUE_FIELD_TYPE_DATE),
//        //        new FieldDef(FIELD_DREAD_FUN, "Fun", Expr.VAaLUE_FIELD_TYPE_INTEGER),
//        new FieldDef(FIELD_DREAD_FUN, "Fun", Expr.VALUE_FIELD_TYPE_ENUM),
//        //        new FieldDef(FIELD_CHALLENGE, "Difficulty", Expr.VALUE_FIELD_TYPE_INTEGER),};
//        new FieldDef(FIELD_CHALLENGE, "Difficulty", Expr.VALUE_FIELD_TYPE_ENUM),};

    final static String TASK = "TASK"; //Task";
    final static String TASK_HELP = "TASK_HELP";// "Describe your task here";
    final static String DESCRIPTION = "DESCRIPTION"; //"Description"; // "Task text"
    final static String DESCRIPTION_HELP = "DESCRIPTION_HELP"; //"Description"; // "Task text"
    final static String DESCRIPTION_HINT = "New task"; //DESCRIPTION_HINT"; //Enter New task"; // "Task text"
    //        final static String FIELD_DONE = "Done", Expr.VALUE_FIELD_TYPE_STRING),
    final static String DONE = "DONE"; //"Done";
    final static String DUE_DATE = "Due"; //"DUE_DATE"; //"Due";
    final static String DUE_DATE_HELP = "DUE_DATE_HELP"; //"Due";
    final static String UPDATED_DATE = "Modified"; //"Modified"; "Date last modified", "Update", "Last modified"
    final static String UPDATED_DATE_SUBTASKS = "Modified subtasks"; //"Modified"; "Date last modified", "Update", "Last modified"
    final static String UPDATED_DATE_HELP = "The time this task was last modified. Set automatically. Cannot be modified by user."; //"Modified"; "Date last modified", "Update", "Last modified"
    final static String COMPLETED_DATE = "Completed"; //"Completed by"; //"Completed"; "Date completed", "Completed on"
//    final static String COMPLETED_DATE_HELP = "The time this task was completed. Set automatically when a task is marked " + ItemStatus.DONE + " or " + ItemStatus.CANCELLED; //"Completed by"; //"Completed"; "Date completed", "Completed on"
    final static String COMPLETED_DATE_HELP = "The time this task was completed. Set automatically when a task is marked [DONE] or [CANCELLED]"; //"Completed by"; //"Completed"; "Date completed", "Completed on", "Set automatically when a task is completed"
    final static String CREATED_DATE = "Created"; //"Date created"
    final static String CREATED_DATE_HELP = "The time this task was created. Set automatically. Cannot be modified by user."; //"Date created"
    final static String COMMENT = "Notes";
//    final static String COMMENT_HELP = "Use " + COMMENT + " for any additional notes, e.g. indicate progress. You can also automatically add a note when setting a task " + ItemStatus.WAITING + "  depending on the Setting. You can use the DateTimeStamp on the right to easily insert today's date and/or time based on the Setting. Maximum size in free version is XXX**";
    final static String COMMENT_HELP = "Use [COMMENT] for any additional notes, e.g. indicate progress. You can also automatically add a note when setting a task [WAITING] depending on the Setting. You can use the DateTimeStamp on the right to easily insert today's date and/or time based on the Setting. Maximum size in free version is XXX**";
    final static String COMMENT_HINT = "Notes";//"Enter your notes"
    final static String EFFORT_ESTIMATE = "Estimate"; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_ESTIMATE_SHORT = "Estimate"; //"Estimate" // "Effort estimate";
//    final static String EFFORT_ESTIMATE_HELP = "Estimates let you compare your expected amount of work with your actual amount of work. Is set automatically the first time you enter " + Item.EFFORT_REMAINING + "."; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_ESTIMATE_HELP = "Estimates let you compare your expected amount of work with your actual amount of work. Is set automatically the first time you enter [EFFORT_REMAINING]."; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_REMAINING = "Remaining effort"; //"Remaining effort"; "Remaining time"
    final static String EFFORT_REMAINING_SHORT = "Remaining"; //"Remaining effort";
    final static String EFFORT_REMAINING_HELP = "The amount of effort in hours:minutes that is remaining on this task. You can update it for partially finished tasks. The Timer can be set to prompt you to update it every time you move to another task without finishing the current one (Setting)."; //"Remaining effort"; "Remaining time"
    final static String EFFORT_ESTIMATE_SUBTASKS = "Estimate effort, subtasks"; //"Effort estimate";"Estimated time (subtasks)"
//    final static String EFFORT_ESTIMATE_SUBTASKS_HELP = "The sum of the " + EFFORT_ESTIMATE + " of all subtasks"; //"Effort estimate";"Estimated time (subtasks)"
    final static String EFFORT_ESTIMATE_SUBTASKS_HELP = "The sum of the [EFFORT_ESTIMATE] of all subtasks"; //"Effort estimate";"Estimated time (subtasks)"
    final static String EFFORT_ACTUAL = "Time worked"; //"Actual effort";"Time spent"
//    final static String EFFORT_ACTUAL_HELP = "The amount of " + EFFORT_ACTUAL + " you have spend on this task. It is updated automatically when you use the Timer while working on tasks."; //"Actual effort";"Time spent"
    final static String EFFORT_ACTUAL_HELP = "The amount of [EFFORT_ACTUAL] you have worked on this task. Updated automatically when you use the Timer while working on tasks."; //"Actual effort";"Time spent"
//        String actualExplanation = "Setting '" + Item.EFFORT_ACTUAL + "' will automatically set " + Item.STATUS + " to " + ItemStatus.ONGOING;
    final static String EFFORT_ACTUAL_SUBTASKS = "Time worked, subtasks";//"Worked effort, subtasks"; //"Actual effort";, "Time spent (subtasks)"
//    final static String EFFORT_ACTUAL_SUBTASKS_HELP = "The sum of the " + EFFORT_ACTUAL + " of all subtasks"; //"Actual effort";, "Time spent (subtasks)"
//    final static String EFFORT_ACTUAL_SUBTASKS_HELP = "The total amount of work done on this [TASK] (sum of work done on this task and any subtasksthe [EFFORT_ACTUAL] of all subtasks"; //"Actual effort";, "Time spent (subtasks)"
    final static String EFFORT_ACTUAL_SUBTASKS_HELP = "The total amount of work done on this [TASK] (sum of work done on this task and any subtasks"; //"Actual effort";, "Time spent (subtasks)"
    final static String EFFORT_ACTUAL_PROJECT_TASK_ITSELF = "Time worked, this task";//"Worked effort, project"; //"Actual effort";"Time spent (project)"
//    final static String EFFORT_ACTUAL_PROJECT_TASK_ITSELF_HELP = EFFORT_ACTUAL+" for the project. You can use this to capture "+EFFORT_ACTUAL+" that is not captured on the individual subtasks."; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_ACTUAL_PROJECT_TASK_ITSELF_HELP = "[EFFORT_ACTUAL] for the project. You can use this to capture [EFFORT_ACTUAL] that is not captured on the individual subtasks."; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_REMAINING_SUBTASKS = "Remaining effort, subtasks"; //"Remaining effort";"Remaining time (subtasks)"
//    final static String EFFORT_REMAINING_SUBTASKS_HELP = "The sum of the " + EFFORT_REMAINING+" of all subtasks"; //"Remaining effort";"Remaining time (subtasks)"
    final static String EFFORT_REMAINING_SUBTASKS_HELP = "The sum of the [EFFORT_REMAINING] of all subtasks"; //"Remaining effort";"Remaining time (subtasks)"
    final static String EFFORT_ESTIMATE_PROJECT = "Estimated effort, this task";//project"; //"Effort estimate";"Estimated time (project)"
//    final static String EFFORT_ESTIMATE_PROJECT_HELP = EFFORT_ESTIMATE+" for the project. You can use this to indicate a total estimate for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_ESTIMATE_PROJECT_HELP = "[EFFORT_ESTIMATE] for the project. You can use this to indicate a total estimate for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_REMAINING_PROJECT = "Remaining effort, this task"; //"Remaining effort";"Remaining time (project)"
//    final static String EFFORT_REMAINING_PROJECT_HELP = EFFORT_REMAINING+" for the project. You can use this to **?? indicate a total for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_REMAINING_PROJECT_HELP = "[EFFORT_REMAINING] for the project. You can use this to **?? indicate a total for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_TOTAL_SHORT = "Total effort"; //total effort in Timer (previous actual + timer elapsed time)
    final static String STATUS = "Status"; //"Status""Task status"
    final static String STATUS_HELP = "Status"; //"Status""Task status"
    final static String PRIORITY = "Priority";
    final static String PRIORITY_HELP = "Priority";
    final static String PROJECT = "Project";
    final static String EARNED_VALUE_PER_HOUR = "Value/hour"; //"Value per hour (based on Estimated time)"; //"Value/Effort"; "Value per hour"
    //Item.EARNED_POINTS_PER_HOUR + " is calculated as " + Item.EARNED_VALUE + " divided by " + Item.EFFORT_ESTIMATE + ", and once work has started by the sum of " + Item.EFFORT_REMAINING + " and " + Item.EFFORT_ACTUAL + "."
    final static String EARNED_VALUE_PER_HOUR_HELP = "Value/hour**"; //"Value per hour (based on Estimated time)"; //"Value/Effort"; "Value per hour"
    final static String EARNED_VALUE = "Value";
//    final static String EARNED_VALUE_HELP = "Indicate any number that represents the value of this task or project. It can be a monetary value or your own scale for value. Used to calculate "+EARNED_POINTS_PER_HOUR+" which for example allows you to prioritize the tasks with the highest return on investment in terms of value by hour.";
    final static String EARNED_VALUE_HELP = "Indicate any number that represents the value of this task or project. It can be a monetary value or your own scale for value. Used to calculate [EARNED_POINTS_PER_HOUR] which for example allows you to prioritize the tasks with the highest return on investment in terms of value by hour.";
    //"Earned value (in currency or points)"
    final static String ALARM_DATE = "Reminder"; // "Alarm date", "Alarm"
    final static String ALARM_DATE_HELP = "Set a Reminder for this task. Shown as a local notification on your phone or device when the app is not running, or as a reminder ** when you are using the app."; // "Alarm date", "Alarm"
    final static String WAIT_UNTIL_DATE = "Wait until"; // "Wait until date" "Wait until" "Waiting until"
    final static String WAIT_UNTIL_DATE_HELP = "**Waiting until"; // "Wait until date" "Wait until"
    final static String DATE_WHEN_SET_WAITING = "Waiting since"; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting"
//    final static String DATE_WHEN_SET_WAITING_HELP = "The time when this task was set "+ItemStatus.WAITING+". Is automatically set. "; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting"
    final static String DATE_WHEN_SET_WAITING_HELP = "The time when this task was set [WAITING]. Is automatically set. "; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting", "Set automatically when a task is set Waiting"
    final static String WAITING_ALARM_DATE = "Waiting reminder"; // "Waiting alarm"
    final static String WAITING_ALARM_DATE_HELP = "**Waiting alarm help"; // 
    final static String STARTED_ON_DATE = "Started"; //"Started",  "Date work on tasks started", "Task started on"
    final static String STARTED_ON_DATE_HELP = "The time this task was started. The Timer automatically sets this date the first time work starts, but it can also be set manually. Useful to track tasks that are not finished immediately or find tasks that have been left partially finished."; //"Started",  "Date work on tasks started", "Task started on", "Set automatically when using the timer"
    final static String ROI = "ROI";
    final static String START_WORK_TIME = "Start work??used??**";
    final static String START_WORK_TIME_HELP = "??used??**Used to indicate the time when work on this task should start. Tasks can be automatically hidden until this date using Filters.";
    final static String FINISH_WORK_TIME = "Finish work";
    final static String FINISH_WORK_TIME_HELP = "Finish work indicates when the task can be finished based on the indicated work time.**";
    final static String TIMESPAN = "Timespan";
    final static String EFFORT_TOTAL = "Total effort";
    final static String CATEGORIES = "Categories";
    final static String INTERRUPT_TASK = "Interrupt task";
    final static String INTERRUPT_TASK_HELP = "Interrupt task indicates that this task interrupted another one. Is automatically set when using InstantTask** while Timer is running for another task.";
    final static String INTERRUPT_TASK_INTERRUPTED = "Interrupted task";
    final static String INTERRUPT_TASK_INTERRUPTED_HELP = "Interrupted task indicates the task that this task interrupted. This information can be useful to see if for example a particular type of tasks tend to be interrupted more frequently.";
    final static String INTERRUPT_OR_INSTANT_TASK = "Interrupt or Instant task";
    final static String INSTANT_TASK = "Instant task";
    final static String PRIORITY_IMPORTANCE = "**used??Importance";
    final static String PRIORITY_IMPORTANCE_HELP = "Indicates the Importance of this task, according to the ** principle.";
    final static String PRIORITY_URGENCY = "**used??Urgency";
    final static String PRIORITY_URGENCY_HELP = "Indicates the Urgency of this task, according to the ** principle.";
    final static String OWNER = "Owner";
    final static String REPEAT_RULE = "Repeat";
    final static String REPEAT_RULE_HELP = "Define how the task repeats";
    final static String EXPIRES_ON_DATE = "**used??Expires";
    final static String EXPIRES_ON_DATE_HELP = "**used??Lets you define a date when this task automatically Expires (is Cancelled). This can help automatically clean up tasks, for example with repeating tasks or tasks that have an 'expiry' date after which they are no longer relevant.";
    final static String START_BY_TIME = "Start by"; // "Start"
    final static String START_BY_TIME_HELP = "Used to indicate the time when work on this task should start. Tasks can be automatically hidden until this date using Filters."; // "Start"
    final static String HIDE_UNTIL = "Hide until";
    final static String HIDE_UNTIL_HELP = "**Hide until";
    final static String AUTOCANCEL_BY = "Auto-cancel by"; //"Auto-cancel task on"
    final static String AUTOCANCEL_BY_HELP = "Lets you define a date when this task is automatically Cancelled. This can help automatically clean up tasks, for example with repeating tasks or tasks that have an 'expiry' date after which they are no longer relevant."; //"Auto-cancel task on"
    final static String IMPORTANCE = "Importance";
    final static String URGENCY = "Urgency";
//    final static String IMPORTANCE_HELP = "Indicates the Importance of this task, according to the ** principle. Prioritizing using "+IMPORTANCE+" and "+URGENCY+" can help overcome a tendency to over-prioritize urgent tasks over important tasks.";
    final static String IMPORTANCE_HELP = "Indicates the Importance of this task, according to the ** principle. Prioritizing using [IMPORTANCE] and [URGENCY] can help overcome a tendency to over-prioritize urgent tasks over important tasks.";
//    final static String URGENCY_HELP = "Indicates the Importance of this task, according to the ** principle. Prioritizing using "+IMPORTANCE+" and "+URGENCY+" can help overcome a tendency to over-prioritize urgent tasks over important tasks.";
    final static String URGENCY_HELP = "Indicates the Importance of this task, according to the ** principle. Prioritizing using [IMPORTANCE] and [URGENCY] can help overcome a tendency to over-prioritize urgent tasks over important tasks.";
    final static String IMPORTANCE_URGENCY = "Importance/Urgency"; // "Importance/Urgency"
    final static String FUN_DREAD = "Fun";
    final static String FUN_DREAD_HELP = "Is this a task you'd love to work on or not? Helps pick tasks on a low-energy day";
    final static String CHALLENGE = "Challenge";
    final static String CHALLENGE_HELP = "Challenge";
    final static String BELONGS_TO = "List/Project"; //"Owner List/Project" "Belongs to";
    final static String BELONGS_TO_HELP = "Indicates the List or Project this task belongs to. Change to move the task to another List or Project or delete to move to Inbox";
    final static String DEPENDS_ON = "Depends on";
    final static String DEPENDS_ON_HELP = "Indicates that this task depends on another task. Dependent tasks can automatically be hidden until the task they depend on is completed.";
    final static String SOURCE = "Copy of"; //Template or Task that this one is a copy of, "Task copy of"
    final static String SOURCE_HELP = "Shows the task was copied from. E.g. for tasks created using templates, automatically repeating tasks or copy/paste. Can be useful for example to find all instances of a given template. "; //Template or Task that this one is a copy of, "Task copy of"
    final static String OBJECT_ID = "Id"; //"Unique id"
    final static String OBJECT_ID_HELP = "An internal unique identifier. This may be useful if requesting support"; //"Unique id"
    final static String STARRED = "Starred"; //"Unique id"
    final static String STARRED_HELP = "Tasks can be marked with a Star to emphasize them**"; //"Unique id"
    final static String TEMPLATE = "Template";
    final static String SUBTASKS = "Subtasks";
    final static String SUBTASKS_HELP = "**";

    final static int ITEM_CHANGED_ALARM_DATE = 0;

    final static String PARSE_TEXT = "description";
    final static String PARSE_COMMENT = "comment";
    final static String PARSE_SUBTASKS = "subtasks";
    final static String PARSE_DREAD_FUN_VALUE = "dreadFunValue";
    final static String PARSE_CHALLENGE = "challenge";
    final static String PARSE_EXPIRES_ON_DATE = "expiresOnDate";
    final static String PARSE_INTERRUPT_OR_INSTANT_TASK = "interuptTask";
    final static String PARSE_ALARM_DATE = "alarmDate";
    final static String PARSE_WAITING_ALARM_DATE = "waitingAlarmDate";
    final static String PARSE_REPEAT_RULE = "repeatRule";
    final static String PARSE_STARTED_ON_DATE = "startedOnDate";
    final static String PARSE_STATUS = "status";
    final static String PARSE_DUE_DATE = "dueDate";
    final static String PARSE_HIDE_UNTIL_DATE = "hideUntilDate";
    final static String PARSE_START_BY_DATE = "startByDate";
    final static String PARSE_WAITING_TILL_DATE = "waitingTillDate";
//    final static String PARSE_WAITING_LAST_ACTIVATED_DATE = "waitingLastActivatedDate";
    final static String PARSE_DATE_WHEN_SET_WAITING = "dateWhenSetWaiting";
    final static String PARSE_EFFORT_ESTIMATE = "effortEstimate";
    final static String PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF = "effortEstimateProjectTask";
    final static String PARSE_REMAINING_EFFORT = "remainingEffort";
    final static String PARSE_REMAINING_EFFORT_PROJECT_TASK_ITSELF = "remainingEffortProjectTask";
    final static String PARSE_ACTUAL_EFFORT = "actualEffort";
    final static String PARSE_ACTUAL_EFFORT_PROJECT_TASK_ITSELF = "actualEffortProjectTask";
//    final static String PARSE_SHOW_FROM_DATE = "showFromDate";
    final static String PARSE_CATEGORIES = "categories";
    final static String PARSE_PRIORITY = "priority";
    final static String PARSE_STARRED = "starred";
    final static String PARSE_EARNED_VALUE = "earnedValue";
    final static String PARSE_EARNED_VALUE_PER_HOUR = "earnedValuePrHour";
    final static String PARSE_COMPLETED_DATE = "completedDate";
    final static String PARSE_IMPORTANCE = "importance";
    final static String PARSE_URGENCY = "urgency";
    final static String PARSE_OWNER_LIST = "ownerList";
    final static String PARSE_OWNER_ITEM = "ownerItem";
    final static String PARSE_OWNER_TEMPLATE_LIST = "templateList"; //TODO!!!!! change to ownerTemplate (before production version)
    final static String PARSE_OWNER_CATEGORY = "ownerCategory";
    final static String PARSE_TIMER_STARTED = "timerStart";
    final static String PARSE_TIMER_PAUSED = "timerPaused";
    final static String PARSE_INTERRUPTED_TASK = "taskInterrupted"; //for an interrupt task: which task was interrupted (taken from Timer)
    final static String PARSE_DEPENDS_ON_TASK = "taskDependsOn"; //for an interrupt task: which task was interrupted (taken from Timer)
    final static String PARSE_TEMPLATE = "template"; //is this task a template?
    final static String PARSE_UPDATED_AT = "updatedAt"; //cannot be edited (PARSE set field)
    final static String PARSE_CREATED_AT = "createdAt"; //cannot be edited (PARSE set field)
    final static String PARSE_IMPORTANCE_URGENCY = "impUrgValue"; //cannot be edited (PARSE set field)
    final static String PARSE_ORIGINAL_SOURCE = "source"; //for an interrupt task: which task was interrupted (taken from Timer)
    final static String PARSE_NEXTCOMING_ALARM = "nextAlarm"; //first-coming/next-coming alarm (to allow easy search in Parse)
    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?
    final static String PARSE_SNOOZE_DATE = "snoozeDate"; //date until which the 
    final static String PARSE_SNOOZED_TYPE = "snoozeType"; //date until which the 
    final static String PARSE_FILTER_SORT_DEF = "filterSort";
    final static String PARSE_WORKSLOTS = "workslots";
    final static String PARSE_RESTART_TIMER = "restartTimer"; //should the Timer be re-started from first element if the currently timed is no longer in the list?
//    final static String PARSE_FINISH_TIME = "finishTimexx"; //NOT a parse field, just used to store the field with
//    final static String PARSE_ = "";

    /**
     * returns an Object containing the value of the given field (Object, since
     * this is used in Expr for filtering)
     *
     * @param fieldId
     * @return
     */
//    public Object getFilterFieldXXX(int fieldId) {
//        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
//            case FIELD_DESCRIPTION:
//                return getText(); //optimization: replace by variable description directly
//            case FIELD_CATEGORIES:
//                return getCategories(); //optimization: replace by variable description directly
//            case FIELD_DONE:
//                return isDone();
//            case FIELD_STATUS:
////                return new Integer(getStatus());
//                return getStatus();
//            case FIELD_DUE_DATE:
//                return getDueDate();
//            case FIELD_MODIFIED_DATE:
//                return getLastModifiedDate();
//            case FIELD_COMPLETED_DATE:
//                return getCompletedDate();
//            case FIELD_CREATED_DATE:
//                return getCreatedDate();
//            case FIELD_COMMENT:
//                return getComment();
//            case FIELD_EFFORT_ESTIMATE:
////                return new Long(getEffortEstimate());
//                return new Duration(getEstimate());
//            case FIELD_EFFORT_ACTUAL:
////                return new Long(getActualEffort());
//                return new Duration(getActual());
//            case FIELD_EFFORT_REMAINING:
////                return new Long(getRemainingEffort());
//                return new Duration(getRemaining());
//            case FIELD_PRIORITY:
//                return getPriority();
//            case FIELD_EARNED_POINTS:
//                return getEarnedValue();
//            case FIELD_EARNED_POINTS_PER_HOUR:
//                return getEarnedValuePerHour();
//            case FIELD_STARTED_ON_DATE:
//                return getStartedOnDate();
//            case FIELD_START_BY_TIME:
//                return getStartByDateD().getTime();
////            case FIELD_START_WORK_TIME:
////                return getStartTime();
//            case FIELD_FINISH_WORK_TIME:
////                return new Long(getFinishTime());
//                return null;
//            case FIELD_EFFORT_TOTAL:
//                return getTotalExpectedEffort();
//            case FIELD_TIMESPAN:
//                return getTimeSpan();
//            case FIELD_DREAD_FUN:
//                return getDreadFunValueN();
//            case FIELD_CHALLENGE:
//                return getChallengeN();
//            default:
//                ASSERT.that("Item: Field Identifier not defined " + fieldId);
//        }
//        return null;
//    }
//    public void setFilterFieldXXX(int fieldId, Object fieldValue) {
//        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
//            case FIELD_DESCRIPTION:
//                setText((String) fieldValue); //optimization: replace by variable description directly
//                break;
//            case FIELD_CATEGORIES:
//                setCategories((List) fieldValue); //optimization: replace by variable description directly
//                break;
//            case FIELD_DONE:
//                setDone(((Boolean) fieldValue));
//                break;
//            case FIELD_STATUS:
////                setStatus(((Integer) fieldValue).intValue());
//                setStatus((ItemStatus) fieldValue);
//                break;
//            case FIELD_DUE_DATE:
////                setDueDate(((Date) fieldValue).getTime());
//                setDueDate(((Date) fieldValue));
//                break;
////            case FIELD_MODIFIED_DATE:
//////                setLastModifiedDate(((Date)fieldValue).getTime());
////                setLastModifiedDate(((Long) fieldValue));
////                break;
//            case FIELD_COMPLETED_DATE:
////                setCompletedDate(((Date)fieldValue).getTime());
//                setCompletedDate(((Long) fieldValue));
//                break;
////            case FIELD_CREATED_DATE:
//////                setCreatedDate(((Date)fieldValue).getTime());
////                setCreatedDate(((Long) fieldValue));
////                break;
//            case FIELD_COMMENT:
//                setComment((String) fieldValue);
//                break;
//            case FIELD_EFFORT_ESTIMATE:
////                setEstimate(((Duration)fieldValue).getDays());
//                setEstimate(((Long) fieldValue), false);
//                break;
//            case FIELD_EFFORT_ACTUAL:
////                setActualEffort(((Duration)fieldValue).getDays());
//                setActual(((Long) fieldValue), false);
//                break;
//            case FIELD_EFFORT_REMAINING:
////                setRemainingEffortXXX(((Duration)fieldValue).getDays());
//                setRemaining(((Long) fieldValue), false);
//                break;
//            case FIELD_PRIORITY:
//                setPriority(((Integer) fieldValue));
//                break;
//            case FIELD_EARNED_POINTS:
//                setEarnedValue(((Double) fieldValue));
//                break;
//            case FIELD_EARNED_POINTS_PER_HOUR:
//                //do nothing, value is calculated and cannot be set
//                ASSERT.that("Should not call setFilterField for FIELD_EARNED_POINTS_PER_HOUR");
//                break;
//            case FIELD_STARTED_ON_DATE:
////                setStartedOnDate(((Date)fieldValue).getTime());
//                setStartedOnDate(((Long) fieldValue));
//            case FIELD_START_BY_TIME:
////                setStartByDate(((Date)fieldValue).getTime());
//                setStartByDate(((Long) fieldValue));
//            case FIELD_START_WORK_TIME:
//                //do nothing, value is calculated and cannot be set
//                ASSERT.that("Should not call setFilterField for FIELD_START_WORK_TIME");
//                break;
//            case FIELD_FINISH_WORK_TIME:
//                //do nothing, value is calculated and cannot be set
//                ASSERT.that("Should not call setFilterField for FIELD_FINISH_WORK_TIME");
//                break;
//            case FIELD_EFFORT_TOTAL:
//                //do nothing, value is calculated and cannot be set
//                ASSERT.that("Should not call setFilterField for FIELD_EFFORT_TOTAL");
//                break;
//            case FIELD_TIMESPAN:
//                //do nothing, value is calculated and cannot be set
//                ASSERT.that("Should not call setFilterField for FIELD_EFFORT_TOTAL");
//                break;
//            case FIELD_DREAD_FUN:
//                setDreadFunValue(((DreadFunValue) fieldValue));
//                break;
//            case FIELD_CHALLENGE:
//                setChallenge(((Challenge) fieldValue));
//                break;
//            default:
//                ASSERT.that("Item.setFilterField: Field Identifier not defined " + fieldId);
//        }
//    }
    @Override
    public boolean isDone() {
        ItemStatus status = getStatus();
        return status == ItemStatus.DONE || status == ItemStatus.CANCELLED; // || getDeletedDateN() != null;  //CANCELLED since this makes a Cancelled task flip back to Created when clicked in a list
    }

    public boolean areAnySubtasksOngoingOrDone() {
        return getCountOfSubtasksWithStatus(true, Arrays.asList(ItemStatus.DONE, ItemStatus.ONGOING)) > 0;
    }

    /**
     * returns true if work on this task has actually started (no matter what
     * ItemStatus is returned, e.g. actualEffort>0 or some subtasks are marked
     * done
     *
     * @return
     */
    public boolean hasWorkStarted() {
        return getActual() > 0 || (isProject() && areAnySubtasksOngoingOrDone()); //TODO!!! ensure that ONGOING is true whenever there is actualeffort or subtasks done
    }

    /**
     * returns true if status==WAITING and WaitingTillDate is not reached yet
     * (if date is defined). Serves to hide waiting items until the date is
     * reached.
     *
     * @return
     */
    public boolean isWaiting() {
//        return getStatus() == ItemStatus.WAITING && (System.currentTimeMillis() < getWaitingTillDateD().getTime() || getWaitingTillDateD().getTime() == 0); //UI: once the waiting date is reached, even if status is (still) Waiting, it will appear in lists etc as not waiting
        return getStatus() == ItemStatus.WAITING && (MyDate.getNow() < getWaitingTillDateD().getTime() || getWaitingTillDateD().getTime() == 0); //UI: once the waiting date is reached, even if status is (still) Waiting, it will appear in lists etc as not waiting
//    ItemStatus status = getStatus();
//        if (statusreturn getStatus() == ItemStatus.WAITING && (System.currentTimeMillis()<getWaitingTillDate()||getWaitingTillDate()==0 && ); 
    }

//    @Override
//    public boolean isNoLongerRelevant() {
//        return isDone();
//    }
//    @Override
    public ItemList getOwnerItemList() {
//        return owner; //TODO: Parsify
//        ParseObject owner = getParseObject(PARSE_OWNER);
        ItemList ownerList = (ItemList) getParseObject(PARSE_OWNER_LIST);
        if (ownerList != null) {
//        if (ownerList == null && getOwnerItem() != null) {
//            return getOwnerItem().getOwnerList();
//        } else {
            ownerList = (ItemList) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
        }
        return ownerList;
//        return (status == null) ? ItemStatus.STATUS_CREATED : ItemStatus.valueOf(status); //Created is initial value
    }

//    @Override
    private void setOwnerItemList(ItemList ownerList) {
//        setOwnerDirectly(owner);
//        ItemAndListCommonInterface oldOwner = getOwner();
//        this.owner = owner; //TODO: Parsify
//        return oldOwner;
//        }
//        if (has(PARSE_OWNER_LIST) || ownerList != null) {
//            put(PARSE_OWNER_LIST, ownerList);
//        }
        if (ownerList != null) {
            put(PARSE_OWNER_LIST, ownerList);
        } else {
            remove(PARSE_OWNER_LIST);
        }
//        return ownerList;
    }

    private TemplateList getOwnerTemplateList() {
        TemplateList ownerList = (TemplateList) getParseObject(PARSE_OWNER_TEMPLATE_LIST);
        ownerList = (TemplateList) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
        return ownerList;
    }

    private void setOwnerTemplateList(TemplateList templateList) {
        if (templateList != null) {
            put(PARSE_OWNER_TEMPLATE_LIST, templateList);
        } else {
            remove(PARSE_OWNER_TEMPLATE_LIST);
        }
    }

    /**
     * Owner of an item can be either a list or a project (an item in a task's
     * sublist).
     *
     * @return null if no owner
     */
//    @Override
//    final public ItemAndListCommonInterface getOwner() {
    private Item getOwnerItem() {
        Item ownerItem = (Item) getParseObject(PARSE_OWNER_ITEM);
        ownerItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerItem);
        return ownerItem;
    }

    /**
     * returns the top-level project for this task or null if none. Iterates up
     * the ownerItem hierarchy to find the highest level task that isn't owned
     * by another task.
     *
     * @return
     */
    private Item getTopLevelProject() {
//        Item ownerItem = (Item) getParseObject(PARSE_OWNER_ITEM);
        Item topLevelProject = (Item) getOwnerItem();
//        topLevelProject = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(topLevelProject); //NO, done in getOwnerItem
        while (topLevelProject != null && topLevelProject.getOwnerItem() != null) {
            topLevelProject = topLevelProject.getOwnerItem();
//            topLevelProject = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(topLevelProject);
        }
        return topLevelProject;
    }

    /**
     * returns the hierarchy of ownerItems for a subtask or leaftask. ProjectX
     * -> SubprojectY -> SubprojectZ -> leafTaskT will return a list {Z, Y, X}
     * when called on leafTaskT. So list.get(0) is the immediate owner of the
     * item. Returns an empty list if no owners.
     *
     * @return
     */
    public static List<Item> getOwnerHierarchy(Item ownerItem, List<Item> hierarchy) {
        if (ownerItem instanceof ParseObject) {
            ownerItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail((ParseObject) ownerItem);
        }
        if (ownerItem != null) {
            hierarchy.add(ownerItem);
            ownerItem.getOwnerHierarchyImpl(hierarchy);
        }
        return hierarchy;
    }

    public List<Item> getOwnerHierarchy() {
        List<Item> hierarchy = new ArrayList<Item>();
        Item ownerItem = getOwnerItem();
        return getOwnerHierarchy(ownerItem, hierarchy);
    }

    public static String getOwnerHierarchyAsString(List projectHierarchyList) {
        String hierarchyStr = "";
        String sep = "";
        for (int i = 0, size = projectHierarchyList.size(); i < size; i++) {
//                    hierarchy = hierarchy + sep + ((Item) hierarchyList.get(i)).getText();
            hierarchyStr = "\"" + ((Item) projectHierarchyList.get(i)).getText() + "\"" + sep + hierarchyStr;
            sep = " / ";
            //TODO indent margin (i*15)
        }
//        if (projectHierarchyList.size() <= 1) {
//            hierarchyStr = "Project: " + hierarchyStr;
//        } else {
//            hierarchyStr = "Project hierarchy: " + hierarchyStr; //format "directOnwer / nextLevelOwner / Top-levelProject
//        }

        return hierarchyStr.isEmpty() ? null : hierarchyStr;
    }

    /**
     * returns null if no owner
     *
     * @return
     */
    public String getOwnerHierarchyAsString() {
        if (getOwner() instanceof Item) {
            return getOwnerHierarchyAsString(getOwnerHierarchy());
        } else {
            return null; //"";
        }
    }

    private void getOwnerHierarchyImpl(List<Item> hierarchy) {
        Item ownerItem = getOwnerItem();
        if (ownerItem != null) {
            ownerItem = (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerItem);
            hierarchy.add(ownerItem);
            ownerItem.getOwnerHierarchyImpl(hierarchy);
        }
    }

    /**
     *
     * @param newOwnerItem
     * @param updateInheritedValues if true, update all potentially inherited
     * values. Should be true eg when copy/paste a task/-sub-project to a new
     * owner or if moving/D&D within projects or between projects/lists
     */
    public void setOwnerItem(Item newOwnerItem, boolean updateInheritedValues, boolean removeFromOldOwnerList) {
//        if (has(PARSE_OWNER_ITEM) || ownerItem != null) {
//            put(PARSE_OWNER_ITEM, ownerItem);
//        }
        Item oldOwner = getOwnerItem();
        if (oldOwner != null) {
//            Item oldOwner = getOwnerItem();
//            opsOnSubtasks.add((x) -> { //NOT an operation on subtasks
//                removeValuesInheritedFromOwner(oldOwner); //nothing's done if oldOwner is null
//                if (oldOwner != null) //can be null if first assignment of owner
//                    oldOwner.removeFromList(this);
//                updateValuesInheritedFromOwner(newOwnerItem);
//                return true;
//            });
            if (updateInheritedValues) {
                removeValuesInheritedFromOwner(oldOwner); //nothing's done if oldOwner is null
            }//            if (removeFromOldOwnerList && oldOwner != null)
//                oldOwner.removeFromList(this, true);
            if (removeFromOldOwnerList) //do this *after* setting newOwner to avoid infinite loop?!
            {
                oldOwner.removeFromList(this, false); //false since this.owner is set below when assigning the new owner
            }
        }
        if (newOwnerItem != null) {
//        ((Item) subtask).updateValuesInheritedFromOwner(this, (oldOwner instanceof Item) ? (Item) oldOwner : null);
//            if (false && updateInheritedValues) //NO need to update inherited values here
            if (updateInheritedValues) //YES, must update inherited values here (the individual fields will then be updated via the changes stored in opsOnSubtasks
            {
                updateValuesInheritedFromOwner(newOwnerItem);
            }
            put(PARSE_OWNER_ITEM, newOwnerItem);

        } else {
            remove(PARSE_OWNER_ITEM);
        }
    }

    public void setOwnerItem(Item newOwnerItem, boolean updateInheritedValues) {
        setOwnerItem(newOwnerItem, updateInheritedValues, true);
    }

    //    @Override
    public void setOwnerItem(Item newOwnerItem) {
        //updateInherited:default value is true so eg move/copyPaste will update to new owner's inherited values, while ScreenItem2 will not (since invididual fields are set explicitly)
        //removeFromOwnerList: default is true to avoid removing eg 
        setOwnerItem(newOwnerItem, true, true);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemAndListCommonInterface setOwnerAndMoveFromOldOwnerXXX(ItemAndListCommonInterface owner) {
//        return setOwnerAndMoveFromOldOwnerXXX(owner, true);
//    }
//
//    private ItemAndListCommonInterface setOwnerAndMoveFromOldOwnerXXX(ItemAndListCommonInterface owner, boolean saveChangedItems) {
//        ItemAndListCommonInterface oldOwner = getOwner();
//        if (oldOwner != null) {
//            oldOwner.removeFromList(this); //remove item from previous owner's list (remove first to remove Owner)
//            if (saveChangedItems) {
////            DAO.getInstance().saveInBackground((ParseObject) oldOwner); //save the list from which subtask was removed
//                DAO.getInstance().saveInBackground((ParseObject) oldOwner); //save the list from which subtask was removed
//            }
//        }
//        if (owner != null) {
//            owner.addToList(this); //insert at head of sublist
//            if (saveChangedItems) {
//                DAO.getInstance().saveInBackground((ParseObject) owner); //save project onto which subtask was dropped
//            }
//        }
//        if (false && saveChangedItems) { //false: no need to save Item since will be only be used when saving an edited item
//            DAO.getInstance().saveInBackground((ParseObject) this);
//        }
//        return oldOwner;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public void setOwnerCategoryXXX(Category ownerCategory) {
////        if (has(PARSE_OWNER_CATEGORY) || ownerCategory != null) {
////            put(PARSE_OWNER_CATEGORY, ownerCategory);
////        }
//        if (ownerCategory != null) {
//            put(PARSE_OWNER_CATEGORY, ownerCategory);
//        } else {
//            remove(PARSE_OWNER_CATEGORY);
//        }
//    }
//    public Category getOwnerCategoryXXX() {
//        return (Category) getParseObject(PARSE_OWNER_CATEGORY);
//    }
//</editor-fold>
    public void setOwner(ItemAndListCommonInterface owner, boolean updateInheritedValues) {
        if (false) {
            ASSERT.that(owner == null || (owner instanceof ParseObject && ((ParseObject) owner).getObjectIdP() != null), () -> "Setting owner that is not ParseObject or without ObjectId for item=" + this + ", owner=" + owner);
        }
        if (Config.TEST && !(owner == null || (owner instanceof ParseObject && ((ParseObject) owner).getObjectIdP() != null))) {
            Log.p("Setting owner that is not ParseObject or without ObjectId for item=" + this + ", owner=" + owner);
        }
//        if (owner instanceof Category) {
//            setOwnerCategory((Category) owner);
//        } else 
        if (owner instanceof TemplateList) {
            setOwnerItemList(null);
            setOwnerItem(null);
            setOwnerTemplateList((TemplateList) owner);
        } else if (owner instanceof ItemList) {
            setOwnerItem(null);
            setOwnerTemplateList(null);
            setOwnerItemList((ItemList) owner);
        } else if (owner instanceof Item) {
            setOwnerItemList(null);
            setOwnerTemplateList(null);
            setOwnerItem((Item) owner, updateInheritedValues);
        } else if (owner == null) {
            setOwnerItemList(null);
            setOwnerItem(null);
            setOwnerTemplateList(null);
        } else {
            ASSERT.that(false, () -> "unknown owner type for " + owner);
        }
    }

    @Override
    public void setOwner(ItemAndListCommonInterface owner) {
        setOwner(owner, true); //be default, update inherited values
    }

//    public ParseObject getOwner() {
    @Override
    public ItemAndListCommonInterface getOwner() {
        ItemAndListCommonInterface owner;
        if ((owner = getOwnerItem()) != null) { //NB! ownerItem must be tested first, since ownerList now returns top-level owner list!!
            return owner;
        } else if ((owner = getOwnerItemList()) != null) {
            return owner;
//        } else if ((owner = getOwnerCategory()) != null) {
//            return owner;
        } else if ((owner = getOwnerTemplateList()) != null) {
            return owner;
        } else {
            return null;
        }
    }

    /**
     * returns owner with a prefix accordinfg to its type, e.g. "Project: xxx"
     * if owner is an Item
     */
    public String getOwnerFormatted() {
        Object ownerObj = getOwner();
        String ownerText = ""; // = item.getOwner() != null ? ((ItemAndListCommonInterface) item.getOwner()).getText() : ""; //TODO 
        if (ownerObj != null) {
            if (ownerObj instanceof Item) {
                ownerText = Item.PROJECT + ": " + ((Item) ownerObj).getText(); //TODO only call top-level projects for "Project"? 
            } else if (ownerObj instanceof Category) {
                ownerText = Category.CATEGORY + ": " + ((Category) ownerObj).getText();
            } else if (ownerObj instanceof ItemList) {
                ownerText = ItemList.ITEM_LIST + ": " + ((ItemList) ownerObj).getText();
            } else {
                ASSERT.that(false, () -> "unknown type of owner for Item:" + this + ", owner:" + ownerObj);
            }
        }
        return ownerText;
    }

    /**
     * returns the top-level project for a subtask (or null if none)
     */
    public Item getOwnerTopLevelProject() {
        Item owner;
        if ((owner = getOwnerItem()) != null) {
            Item ownersOwner = owner.getOwnerTopLevelProject();
            return ownersOwner == null ? owner : ownersOwner;
        } else {
            return null;
        }
    }

    /**
     * returns the key text string for the subtypes of BaseItem, e.g. Item
     * returns Description, Category categoryName, ... getText() in BaseItem is
     * not supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    @Override
    public String getText() {
        String s = getString(PARSE_TEXT);
        if (Config.TEST_SHOW_ITEM_TEXT_AS_OBJECTID && (s == null || s.isEmpty())) {// && getObjectIdP() != null) {
            return "<" + getObjectIdP() + ">";
        }
        return (s == null) ? "" : s;
//        return (description != null ? description : "");
    }

    /**
     * sets the key text string for the subtypes of BaseItem, e.g. for Item
     * Description, for Category categoryName, ... setText() in BaseItem is not
     * supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    public void setText(String text, boolean interpretInlineValues) {
        if (interpretInlineValues) {
            text = parseTaskTextForProperties(this, text);
        }

        if (Config.TEST) //use for testing to ensure all generated Items are unique with a date/time stamp
        {
            text = MyUtil.replaceSubstring(text, "##", MyDate.formatDateTimeNew(new MyDate()));
        }

        setText(text);
    }

    @Override
    public void setText(String text) {
//        if ((has(PARSE_TEXT) || !text.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
//            AlarmHandler.getInstance().updateNotificationText(this);
//            put(PARSE_TEXT, text);
//        }
        if (MyPrefs.itemRemoveTrailingPrecedingSpacesAndNewlines.getBoolean()) {
            text = removeTrailingPrecedingSpacesNewLinesEtc(text);
        }

        boolean textChanged = !getText().equals(text);
        if (text != null && !text.equals("")) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_TEXT, text);
        } else {
            remove(PARSE_TEXT);
        }
//        if (text != null && !text.equals(description)) {
//            this.description = text;
//        }
        if (textChanged) {
//            afterSaveActions.put(AFTER_SAVE_TEXT_UPDATE, () -> AlarmHandler.getInstance().updateNotificationText(this));
            mustUpdateAlarms = true;
        }
    }

    @Override
    public String getComment() {
        String s = getString(PARSE_COMMENT);
        if (s == null) {
            return "";
        } else {
            return s;
        }
//        return comment;
    }

    @Override
    public void setComment(String comment) {
//        if (val != null && (has("comment") || !val.equals(""))) {
//        if ((has(PARSE_COMMENT) || !comment.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
//            put(PARSE_COMMENT, comment);
//        }
        if (comment != null && !comment.equals("")) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_COMMENT, comment);
        } else {
            remove(PARSE_COMMENT);
        }
//        if (!this.comment.equals(val)) {
//            this.comment = val;
//        }
    }

//    @Override
//    public boolean isExpandable() {
////        return getItemListSize() > 0;
//        return getList().size() > 0; //use getList, and not getListFull, since we don't want to show expand button if all subtasks are filtered
//    }
    public enum CopyMode {
        /**
         * full copy
         */
        COPY_ALL_FIELDS,
        /**
         * copy a project (or a template) into a template, keeping only what
         * makes sense for a template
         */
        COPY_TO_TEMPLATE,
        /**
         * create a repeat copy
         */
        COPY_TO_REPEAT_INSTANCE,
        /**
         * create a copy when doing copy/paste, or duplicating an item
         */
        COPY_TO_COPY_PASTE,
        /**
         * creating an item by copying from a template (anything to leave out
         * here?)
         */
        COPY_FROM_TEMPLATE;
    }

    /**
     * user options for what to include/exclude in a copy
     */
    final static int COPY_EXCLUDE_COMMENT = 1;
    final static int COPY_EXCLUDE_DUE_DATE = COPY_EXCLUDE_COMMENT * 2;
    final static int COPY_EXCLUDE_EFFORT_ESTIMATE = COPY_EXCLUDE_DUE_DATE * 2;
    final static int COPY_EXCLUDE_CATEGORIES = COPY_EXCLUDE_EFFORT_ESTIMATE * 2;
    final static int COPY_EXCLUDE_SUBTASKS = COPY_EXCLUDE_CATEGORIES * 2;
    final static int COPY_EXCLUDE_PRIORITY = COPY_EXCLUDE_SUBTASKS * 2;
    final static int COPY_EXCLUDE_IMP_URG = COPY_EXCLUDE_PRIORITY * 2;
    final static int COPY_EXCLUDE_EARNED_VALUE = COPY_EXCLUDE_IMP_URG * 2;
    final static int COPY_EXCLUDE_START_BY_DATE = COPY_EXCLUDE_EARNED_VALUE * 2;
//    final static int COPY_EXCLUDE_SHOW_FROM_DATE = COPY_EXCLUDE_START_BY_DATE * 2;
    final static int COPY_EXCLUDE_HIDE_UNTIL_DATE = COPY_EXCLUDE_START_BY_DATE * 2;
    final static int COPY_EXCLUDE_ALARM_DATE = COPY_EXCLUDE_HIDE_UNTIL_DATE * 2;
    final static int COPY_EXCLUDE_EXPIRES_ON_DATE = COPY_EXCLUDE_ALARM_DATE * 2;
    final static int COPY_EXCLUDE_REPEAT_RULE = COPY_EXCLUDE_EXPIRES_ON_DATE * 2;
    final static int COPY_EXCLUDE_CHALLENGE = COPY_EXCLUDE_REPEAT_RULE * 2;
    final static int COPY_EXCLUDE_DREAD_FUN = COPY_EXCLUDE_CHALLENGE * 2;
    final static int COPY_EXCLUDE_STARRED = COPY_EXCLUDE_DREAD_FUN * 2;
    final static int COPY_EXCLUDE_USE_ACTUALS_AS_NEW_ESTIMATE = COPY_EXCLUDE_STARRED * 2; //SPECIAL case: use previous Actuals as new Estimate // TODO!!

    private final static CopyMode COPY_FIELD_DEFINITION_DEFAULT = CopyMode.COPY_ALL_FIELDS;

    /**
     * @return @inherit
     */
    @Override
    public Item cloneMe() {
//        Item newCopy = new Item();
//        copyMeInto(newCopy);
//        return newCopy;
        return cloneMe(COPY_FIELD_DEFINITION_DEFAULT, 0);
    }

    public Item cloneMe(CopyMode copyFieldDefintion) {
        return cloneMe(copyFieldDefintion, 0);
    }

    public Item cloneMe(CopyMode copyFieldDefintion, int copyExclusions) {
        return cloneMe(copyFieldDefintion, copyExclusions, false);
    }

    public Item cloneMe(CopyMode copyFieldDefintion, int copyExclusions, boolean setRepeatRuleWithoutUpdate) {
        Item newCopy = new Item();
        copyMeInto(newCopy, copyFieldDefintion, copyExclusions, setRepeatRuleWithoutUpdate);
        return newCopy;
    }

    /**
     * @param destiny
     * @inherit
     */
    public Item copyMeInto(Item destiny) {
        return copyMeInto(destiny, CopyMode.COPY_ALL_FIELDS);
    }

    Item copyMeInto(Item destination, CopyMode copyFieldDefintion) {
        return copyMeInto(destination, copyFieldDefintion, 0);
    }

    /**
     * deep copy
     *
     * @param destination
     * @param copyFieldDefintion defines type of type
     * @param copyExclusions set to COPY_EXCLUDE_COMMENT | COPY_EXCLUDE_PRIORITY
     * to exlude fields from copy. Applied recursively
     */
//    void copyMeInto(Item destination, CopyMode copyFieldDefintion, int copyExclusions) {
//        copyMeInto(destination, copyFieldDefintion, copyExclusions, true);
//    }
    Item copyMeInto(Item destination, CopyMode copyFieldDefintion, int copyExclusions) {
        return copyMeInto(destination, copyFieldDefintion, copyExclusions, false);
    }

    Item copyMeInto(Item destination, CopyMode copyFieldDefintion, int copyExclusions, boolean setRepeatRuleWithoutUpdate) {

        /**
         * copy for all types of copies
         */
        boolean defAll = (copyFieldDefintion == CopyMode.COPY_ALL_FIELDS);
        /**
         * copy for Repeat Instances
         */
        boolean defToRepeatInst = (copyFieldDefintion == CopyMode.COPY_TO_REPEAT_INSTANCE);
        /**
         * copy for Templates
         */
        boolean defToTempl = (copyFieldDefintion == CopyMode.COPY_TO_TEMPLATE);
        /**
         * copy for Templates
         */
        boolean defFromTempl = (copyFieldDefintion == CopyMode.COPY_FROM_TEMPLATE);
        /**
         * copy for Copy/Paste copies (same as for templates??)
         */
        boolean defCopyPaste = (copyFieldDefintion == CopyMode.COPY_TO_COPY_PASTE);

        if (defToTempl) {
            destination.setTemplate(true); //set template first since it may impact eg repeatRules
        }

//        super.copyMeInto(destination, !fromTempl || (fromTempl && destination.getText().equals(""))); //don't overwrite destination with template dreadFunNames, unless destination dreadFunNames is empty
//        destiny.storedFormatVersion = storedFormatVersion;
//        destiny.setTypeId(getTypeId());
//        if (copyText) {
//            destiny.setText(new String(getText())); //make a copy of text string (to be able to edit new string separately)
//        }
        //NB! when copying categories, the new list should refer to the categoreis themselves,
        //NOT copies of them,
//        item.setCategories((ItemList) (getCategories().clone()));
        if (defAll || defToRepeatInst || defToTempl || defFromTempl || defCopyPaste) {

            //TEXT
//            if ((copyExclusions & COPY_EXCLUDE_TEXT) == 0) { //UI: DOESN'T make sense to not copy task description (especially with projects)
            if (destination.getText().equals("")) { //copy from template, iff nothing's already set for item
                destination.setText(getText());
            }
//            }

            //EFFORT ESTIMATE
//            destination.setEstimate(getEffortEstimate());
            if ((copyExclusions & COPY_EXCLUDE_EFFORT_ESTIMATE) == 0) {
                if (destination.getEstimate() == 0) { //copy from template, iff nothing's already set for item
//                    destination.setEstimate(getEffortEstimate(), fromTempl || toRepeatInst, true); //ensure remaining is set
//                    destination.setEstimate(getEffortEstimate(), fromTempl || toRepeatInst); //TODO!!! WHY auto-update Remaining if (and only if) fromTempl || toRepeatInst????!!
//                    destination.setEstimate(getEstimate(), false); //TODO!!! WHY auto-update Remaining if (and only if) fromTempl || toRepeatInst????!!
                    destination.setEstimate(getEstimate(), defFromTempl || defToRepeatInst); //auto-update Remaining if fromTempl || toRepeatInst to ensure that Remaining gets set
                }
            }
            //CHALLENGE
            if ((copyExclusions & COPY_EXCLUDE_CHALLENGE) == 0) {
                if (destination.getChallengeN() == null) { //copy from template, iff nothing's already set for item
                    destination.setChallenge(getChallengeN());
                }
            }
            //DREAD / FUN
            if ((copyExclusions & COPY_EXCLUDE_DREAD_FUN) == 0) {
                if (destination.getDreadFunValueN() == null) { //copy from template, iff nothing's already set for item
                    destination.setDreadFunValue(getDreadFunValueN());
                }
            }
            //IMPORTANCE / URGENCY
            if ((copyExclusions & COPY_EXCLUDE_IMP_URG) == 0) {
                if (destination.getImportanceN() == null) { //copy from template, iff nothing's already set for item
                    destination.setImportance(getImportanceN());
//                    destination.setUrgency(getUrgencyN());split;
                }
                if (destination.getUrgencyN() == null) { //copy from template, iff nothing's already set for item
                    destination.setUrgency(getUrgencyN());
                }
            }
            //COMMENT
            if ((copyExclusions & COPY_EXCLUDE_COMMENT) == 0) {
                destination.setComment(Item.addToComment(destination.getComment(), getComment(), true)); //UI: add template's comment to the end(?!) of the comment, with a newline
            }
//            if ((copyExclusions & COPY_EXCLUDE_PRIORITY) == 0) {
//                destination.setPriority(getPriority());
//            }

            //PRIORITY
            if ((copyExclusions & COPY_EXCLUDE_PRIORITY) == 0) {
//                if (destination.getPriority() == Settings.getInstance().getDefaultPriority()) { //only change priority to template's value if the item has default value (assuming no value has been set - TODO!!!! what if the user wanted the default value??)
                if (destination.getPriority() == 0) { //only change priority to template's value if the item has default value (assuming no value has been set - TODO!!!! what if the user wanted the default value??)
                    destination.setPriority(getPriority());
                }
            }
            //EARNED VALUE
            if ((copyExclusions & COPY_EXCLUDE_EARNED_VALUE) == 0) {
                if (destination.getEarnedValue() == 0) {
                    destination.setEarnedValue(getEarnedValue());
                }
            }
//            getCategories().copyMeInto((ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
//            destination.setCategories(new ArrayList(getCategories()));//(ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
//            destination.setCategories(new HashSet(getCategories()));//(ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
//            destination.setCategories(new ArrayList(getCategories()));//(ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
//            if (getCategoriesSize() > 0) {
//                //if copying *from* a template into an item, *add* the template's categories (if any) instead of overwriting, to ensure already set categories are not lost
//                destination.getCategories().addAll(getCategories()); //just add the same categories
//            }
//            if (false && destination.getObjectIdP() == null) { //now done in ScreenItem2
//                DAO.getInstance().saveInBackground(destination); //need to save destination before we can save copies of subtasks with it as owner or add it to categories
//            }

            //CATEGORIES - always only ADD categories, to avoid removing any manually set before adding the template
            if ((copyExclusions & COPY_EXCLUDE_CATEGORIES) == 0) {
                if (defToTempl) { //only set the categories in the template, but DON'T update the categories 
//                    destination.updateCategories(new ArrayList(getCategories()), fromTempl); //when fromTempl: only add additional categories from the template, don't remove any manually added before
//                    destination.updateCategories(new ArrayList(getCategories()), true); //when fromTempl: only add additional categories from the template, don't remove any manually added before
                    destination.updateCategories(getCategories());
                } else if (defFromTempl) { //always only ADD additional categories set in the template, to avoid removing any manually set before adding the template
                    List<Category> newCatList = new ArrayList(destination.getCategories());

//                    newCatList.addAll(getCategories());
                    for (Category cat : getCategories()) {
                        if (!newCatList.contains(cat)) {
                            newCatList.add(cat); //ensure no double copies of categories in list
                        }
                    }
                    destination.updateCategories(newCatList, true);
                } else { //CATEGORIES - always only ADD categories, to avoid removing any manually set before adding the template
                    destination.updateCategories(getCategories()); //when fromTempl: only add additional categories from the template, don't remove any manually added before
                }
            }

            //SUBTASKS
            if ((copyExclusions & COPY_EXCLUDE_SUBTASKS) == 0) {
//                List<Item> subtaskCopy = new ArrayList();
                List<Item> subtaskCopy;
                if (defFromTempl) {
                    subtaskCopy = destination.getListFull(); //if copying from a template, *add* template subtasks to any existing subtasks! //full to include ALL tasks
                } else {
                    subtaskCopy = new ArrayList();
                }
                List<Item> orgSubtasks = getListFull();
//                DAO.getInstance().fetchAllElementsInSublist(orgSubtasks, true);

                for (int i = 0, size = orgSubtasks.size(); i < size; i++) {
//                    Item copy = orgSubtasks.get(i).cloneMe(copyFieldDefintion, copyExclusions);
                    Item copy = new Item();
                    copy.setOwnerItem(destination, false); //set owner for subtask copy (MUST be done before to ensure repeatCopies are inserted in right place)
                    orgSubtasks.get(i).copyMeInto(copy, copyFieldDefintion, copyExclusions);
                    //TODO!!!!! how to avoid saving subtasks, so we can Cancel the creation of a template instance??? (it is not acceptable to accumulate dangling subtasks which would be visible to the user in some view)!
                    if (false) {
                        DAO.getInstance().saveInBackground(copy); //need to save copies as we go along, otherwise cannot save owner (Porject) due to "unable to encode an association with an unsaved ParseObject" //DAO now saves a new project correctly wrt references
                    }                    //Keep everything in memory and add a special lambda function to save everything created from the template *if* it is saved!
                    subtaskCopy.add(copy);
                }
//                destination.setList(subtaskCopy); //NO, OVERWRITES any existing subtasks!
//                List updatedSubtaskList = destination.getList();
//                updatedSubtaskList.addAll(subtaskCopy);
//                destination.setList(updatedSubtaskList);
                destination.setList(subtaskCopy);
//                if (getItemListSize() > 0) {
////                destination.getList().addAllItems(getList().cloneMe()); //clone the list AND clone the subtasks //TODO(?) clone the tasks
//                    destination.getList().addAll(getList()); //clone the list AND clone the subtasks //TODO(?) clone the tasks
//                }
            }

            //SOURCE OF COPY
            if (true) { //since this is the originator of the values in the copy, link to that instead (possible to trace back to very first originator if needed, but actual originator would be lost if using the original originator)
                destination.setSource(this);
            } else {
                if (getSource() != null) {
                    destination.setSource(getSource()); //link to the very first originator/source if available
                } else {
                    destination.setSource(this); //otherwise (this is first copy) use this
                }
            }

        }

        if (defAll || defToRepeatInst) { //repeat instances will always have same owner, and it needs to be set so that when creating multiple instances, they get inserted into the owner's list
            destination.setOwner(getOwner());
        }

        //optimization: bundle all 'all' copies together in a single if statement
        if (defAll || defCopyPaste) {
            //None of these fields are normally copied
            destination.setStatus(getStatus());
            destination.setStartedOnDate(getStartedOnDate());
            destination.setCompletedDate(getCompletedDate());
//            destination.setCreatedDate(getCreatedDate());
            destination.setWaitingTillDate(getWaitingTillDateD().getTime());
            destination.setDateWhenSetWaiting(getDateWhenSetWaiting());
//            destination.setRemaining(getRemaining(), false);
            destination.setRemaining(getRemaining(), false);
            destination.setActual(getActual(), false);
//            destination.setLastModifiedDate(getLastModifiedDate());
//            destination.setEarnedValue(getEarnedValue());
            destination.setInteruptOrInstantTask(isInteruptOrInstantTask());
//        }
//
//        if (all || copyPaste) {
            //TODO!!! consider to copy dates DUE/SHOW_FROM/ALARM etc once relative dates are implemented?

            //STARRED
            if ((copyExclusions & COPY_EXCLUDE_STARRED) == 0) {
                if (!destination.isStarred()) { //copy from template, iff nothing's already set for item
                    destination.setStarred(isStarred());
                }
            }
            //DUE
            if ((copyExclusions & COPY_EXCLUDE_DUE_DATE) == 0) {
                destination.setDueDate(getDueDateD());
            }
            //START BY DATE
            if ((copyExclusions & COPY_EXCLUDE_START_BY_DATE) == 0) {
                destination.setStartByDate(getStartByDateD().getTime());
            }
            //HIDE UNTIL DATE (NOT:SHOW FROM DATE)
//            if ((copyExclusions & COPY_EXCLUDE_SHOW_FROM_DATE) == 0) {
//                destination.setShowFromDate(getShowFromDate());
//            }
            if ((copyExclusions & COPY_EXCLUDE_HIDE_UNTIL_DATE) == 0) {
                destination.setHideUntilDate(getHideUntilDateD());
            }
            if ((copyExclusions & COPY_EXCLUDE_ALARM_DATE) == 0) {
                destination.setAlarmDate(getAlarmDate());
            }
            if ((copyExclusions & COPY_EXCLUDE_EXPIRES_ON_DATE) == 0) {
                destination.setExpiresOnDate(getExpiresOnDate());
            }
        }

        //TODO support copying alarmDate/startByDate/showFromDate/expiresOnDate relative to a user-defined due date
        //REPEAT RULE - MUST be done after the entire Item AND subtask hierarchy have been cloned
        if ((copyExclusions & COPY_EXCLUDE_REPEAT_RULE) == 0) {
//            assert false: "TODO";
            if (defAll || defFromTempl) { //- UI: do NOT copy RepeatRules
                if ((RepeatRuleParseObject) getRepeatRule() != null && destination.getRepeatRule() == null) {
                    //TODO!!!! how to trigger a repeat rule on a new instance of a template??
                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //create a new repeat rule
//                destination.getRepeatRule().generateRepeatInstances(destination, destination.getOwnerItemList()); //- this should be done at commit() of the copy, not when it's generated, in this way the list for the copies is also known
                }
            } else if (defToTempl) {
                if (getRepeatRule() != null) {
                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
                }
            } else if (defToRepeatInst) {
                if (setRepeatRuleWithoutUpdate) {
                    destination.setRepeatRuleInParseAndSave(getRepeatRule()); //point to existing repeat rule
                } else if (getRepeatRule() != null) {
                    RepeatRuleParseObject repeatRuleCopy = getRepeatRule().cloneMe();
                    DAO.getInstance().saveInBackground(repeatRuleCopy); //save new repeatRule before the new item copy referring it is saved
//                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
                    destination.setRepeatRule(repeatRuleCopy); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
                }
            }
        }
        return destination;
    }

    /**
     * updates this item's Due/Alarm/ShowFrom dates etc. based on the
     * referenceItem's defined dates + deltaTime (deltaTime= how much later is
     * the referenceTime than the referenceItem's dueDate)
     */
    private void setRepeatInstanceRelativeDates(Item referenceItem, Date newDueDateTime) {
        long delta = 0;

        if (referenceItem.getDueDate() != 0) { //only update if a value is already defined (also for due date since it may not be set, eg for repeat on completed)
            delta = newDueDateTime.getTime() - referenceItem.getDueDate(); //how much later is the referenceTime than the referenceItem's dueDate?
        }

//        if (true || referenceItem.getDueDate() != 0) { //only update if a value is already defined (also for due date since it may not be set, eg for repeat on completed)
////            setDueDate(referenceItem.getDueDate() + deltaTime);
//            setDueDate(newDueDateTime);
//        }
        setDueDate(newDueDateTime);

        if (delta != 0 && MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
            //TODO!!!!: what if no DueDate is set, but only an alarmDate or showFromDate? Can this happen (what would the repeatReference date then be?)
            if (referenceItem.getAlarmDate() != 0) { //only update if a value was defined for the referenceItem
                setAlarmDate(referenceItem.getAlarmDate() + delta);
            }
//            if (referenceItem.getShowFromDate() != 0) { //only update if a value was defined for the referenceItem
//                setShowFromDate(referenceItem.getShowFromDate() + deltaTime);
//            }
            if (referenceItem.getHideUntilDateD().getTime() != 0) { //only update if a value was defined for the referenceItem
                setHideUntilDate(new Date(referenceItem.getHideUntilDateD().getTime() + delta));
            }
            if (referenceItem.getStartByDateD().getTime() != 0) { //only update if a value was defined for the referenceItem
                setStartByDate(referenceItem.getStartByDateD().getTime() + delta);
            }
            if (referenceItem.getExpiresOnDate() != 0) { //only update if a value was defined for the referenceItem
                setExpiresOnDate(referenceItem.getExpiresOnDate() + delta);
            }
        }
    }

    /**
     * update the Item's relative dates to have the same time between the new
     * due date as they had to the old due date
     *
     * @param newDueDateTime
     */
    public void updateRepeatInstanceRelativeDates(Date newDueDateTime) {
        ASSERT.that(newDueDateTime.getTime() != 0);
        Date oldDueDate = getDueDateD();
        long newDueDate = newDueDateTime.getTime();
        long delta = 0;
//        if (oldDueDate != 0 && newDueDate != 0) {
        if (oldDueDate.getTime() != 0) {
            delta = newDueDate - oldDueDate.getTime();
        }

        setDueDate(newDueDateTime);

        if (delta != 0 && MyPrefs.repeatSetRelativeFieldsWhenCreatingRepeatInstances.getBoolean()) {
            //TODO!!!!: what if no DueDate is set, but only an alarmDate or showFromDate? Can this happen (what would the repeatReference date then be?)
            if (getAlarmDate() != 0) { //only update if a value was defined for the referenceItem
//                setAlarmDate(newDueDate - (oldDueDate - getAlarmDate()));
                setAlarmDate(getAlarmDate() + delta);
            }
            if (getHideUntilDateD().getTime() != 0) { //only update if a value was defined for the referenceItem
//                setHideUntilDate(new Date(newDueDate - (oldDueDate - getHideUntilDateD().getTime())));
                setHideUntilDate(new Date(getHideUntilDateD().getTime() + delta));
            }
            if (getStartByDateD().getTime() != 0) { //only update if a value was defined for the referenceItem
//                setStartByDate(newDueDate - (oldDueDate - getStartByDate()));
                setStartByDate(getStartByDateD().getTime() + delta);
            }
            if (getExpiresOnDate() != 0) { //only update if a value was defined for the referenceItem
//                setExpiresOnDate(newDueDate - (oldDueDate - getExpiresOnDate()));
                setExpiresOnDate(getExpiresOnDate() + delta);
            }
        }
    }

    /**
     * create a copy of this item, to be used as a repeatRule instance
     */
    @Override
    public RepeatRuleObjectInterface createRepeatCopy(Date referenceTime) {
//        Item newRepeatCopy = new Item();
//        copyMeInto(newRepeatCopy, COPY_TO_REPEAT_INSTANCE);
        Item newRepeatCopy = (Item) this.cloneMe(CopyMode.COPY_TO_REPEAT_INSTANCE, 0, true);
//        newRepeatCopy.updateDatesFromReference(this, referenceTime - getDueDate()); //the delta time to add to copies are the new due time - the due date of the reference item
        newRepeatCopy.setRepeatInstanceRelativeDates(this, referenceTime); //the delta time to add to copies are the new due time - the due date of the reference item
        return newRepeatCopy;
    }

    @Override
//    public void setRepeatStartTime(long repeatStartTime) {
    public void setRepeatStartTime(Date repeatStartTime) {
        setDueDate(repeatStartTime);
    }

    @Override
    public Date getRepeatStartTime(boolean fromCompletedDate) {
        if (fromCompletedDate) {
            return getCompletedDateD();
        } else {
            return getDueDateD();
        }
    }

    static int compareDate(Date d1, Date d2) {
//        if (d1.getTime() < d2.getTime()) {
//            return -1;
//        } else if (d2.getTime() < d1.getTime()) {
//            return 1;
//        }
//        return 0;
        return compareLong(d1.getTime(), d2.getTime());
    }

    static int compareInt(int d1, int d2) {
        if (d1 < d2) {
            return -1;
        } else if (d2 < d1) {
            return 1;
        }
        return 0;
    }

    static int compareLong(long d1, long d2) {
        if (d1 < d2) {
            return -1;
        } else if (d2 < d1) {
            return 1;
        }
        return 0;
    }

    @Override
    public int compareTo(IComparable ic, int compareOn) {
//    public int compareTox(IComparable ic, Vector compareOnVector) {
//        int compareOn = (byte)((Integer)compareOnVector.elementAt(0)).intValue();
        if (ic instanceof Item) {
            Item c = (Item) ic;
            switch (compareOn) {
                case Item.COMPARE_DESCRIPTION:
                    //TOTO: comparing on multiple fields is same as comparing on a single, bigger, key
                    //TODO: implement comparing mulitple fields, e.g. if compare for left field is EQUAL, then return compare value on right field
//                    return getDescription().compareTo(c.getDescription());
                    return getText().compareTo(c.getText());
                case Item.COMPARE_COMPLETED:
                    return isDone() ? (c.isDone() ? 0 : 1) : (c.isDone() ? -1 : 0); // done < not done
                case Item.COMPARE_COMPLETED_DATE:
                    return compareLong(getCompletedDate(), c.getCompletedDate());
                case Item.COMPARE_CREATED_DATE:
                    return compareLong(getCreatedDate(), c.getCreatedDate());
                case Item.COMPARE_DUE_DATE:
                    return compareLong(getDueDate(), c.getDueDate());
                case Item.COMPARE_HIDE_UNTIL_DATE:
                    return compareLong(getHideUntilDateD().getTime(), c.getHideUntilDateD().getTime());
                case Item.COMPARE_ALARM_DATE:
                    return compareLong(getAlarmDate(), c.getAlarmDate());
                case Item.COMPARE_EFFORTESTIMATE:
                    return compareLong(getRemaining(), c.getRemaining());
                case Item.COMPARE_ACTUALEFFORT:
                    return compareLong(getActual(), c.getActual());
                case Item.COMPARE_LASTMOD_DATE:
                    return compareLong(getLastModifiedDate(), c.getLastModifiedDate());
                case Item.COMPARE_PRIORITY:
                    return compareInt(getPriority(), c.getPriority());
                default:
            }
        }
        return 0;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public String typeString() {
//        return BaseItemTypes.toString(typeId);
//    }
//    public String fullString() {
////        return typeString() + " \"" + description + "\""
//        return //typeString() + " \"" + getText() + "\"" +
//                //                + (completed ? "[V]" : "[ ]")
//                //                + " | completed = " + completed
//                //                + "|subItems=" + (subitems == null ? "null" : /*("Gui" + subitems.getGuid() +*/ (getItemListSize() == 0 ? "[<empty>]" : getItemList().toString()))
//                "|subItems=" + (subitems == null ? "[null]" : /*
//                 * ("Gui" + subitems.getGuid() +
//                         */ (getItemListSize() == 0 ? "[<empty>]" : getItemList().shortString()))
//                //                + "|categories=" + (categories == null ? "null" : /*("Gui" + categories.getGuid() +*/ (getCategoriesSize() == 0 ? "[<empty>]" : getCategories().toString()))
//                + "|categories=" + (categories == null ? "[null]" : /*
//                 * ("Gui" + categories.getGuid() +
//                         */ (getCategoriesSize() == 0 ? "[<empty>]" : getCategories().toString())) //.shortString()))
//                //                + "|categories=" + (categories != null ? "Gui" + categories.getGuid() : "null") + ((getCategoriesSize() == 0) ? "[<empty>]" : getCategories().toString())
//                + "|" //+ super.fullString()
//                //                + " | completedDate = " + completedDate
//                //                + "|createdDate=" + createdDate //                + " | dueDate = " + dueDate
//                //                + " | showFromDate = " + showFromDate
//                //                + " | effortEstimate = " + effortEstimate
//                //                + " | actualEffort = " + actualEffort
//                //                + " | lastModDate = " + lastModDate
//                //                + " | comment = " + comment
//                //                + " | priority = " + priority;
//                ;
//    }
//    @Override
//    public String shortString() {
////        return "typeId=" + BaseItemTypes.toString(typeId)+ "|guid=" + guid;
////        return BaseItemTypes.toString(getTypeId()) + (getText().length() != 0 ? "\"" + getText() + "\"" : "Gui" + getGuid());
//        return ""; //BaseItemTypes.toString(getTypeId()) + "\"" + getText() + "\"";
//    }
//</editor-fold>
    private void softDeleteImpl(Date deleteDate, boolean removeRefs) {
        softDeleteImpl(deleteDate, removeRefs, false);
    }
    private void softDeleteImpl(Date deleteDate, boolean removeRefs, boolean deleteAllRepeatInstances) {
        setDeletedDate(deleteDate);

        //DELETE SUBTASKS - delete all subtasks (since they are owned by this item)
        List<Item> itemsSubtasksOfThisItem = getListFull();
        for (Item item : itemsSubtasksOfThisItem) {
            item.softDeleteImpl(deleteDate, removeRefs); //let each item delete itself properly, will recurse down the project hierarchy
        }

        //TODO!!! (?)anything to do to handle case where subtasks are created and saved, but where the new mother task is finally not saved?
        //DELETE IN CATEGORIES
        // remove item from all categories before deleting it
        for (Category cat : getCategories()) {
            ((Category) cat).removeItemFromCategory(this, removeRefs); //remove references to this item from the category before deleting it (false: but keep the item's categories)
            DAO.getInstance().saveInBackground((ParseObject) cat);
        }

        //DELETE IN OWNERS/PROJECTS
        ItemAndListCommonInterface owner = getOwner();
//        if (owner instanceof Item) {
//            ((Item) owner).removeFromList(this, removeRefs); //
//            DAO.getInstance().saveInBackground((ParseObject) owner);
//        } else {
//            owner.removeFromList(this);
//            DAO.getInstance().saveInBackground((ParseObject) owner);
//        }
//        if (owner!=null) //don't test, better to get a null ref error if an object doesnt' have an owner
        owner.removeFromList(this, removeRefs); //

        //handle repeatrule
        //TODO!!!! handle repeatRules when deleting an Item
        RepeatRuleParseObject myRepeatRule = getRepeatRule();
        if (myRepeatRule != null) {
            if(deleteAllRepeatInstances){
//                myRepeatRule.deleteAllInstances();
                myRepeatRule.softDelete(false);
            }else
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we 
//            myRepeatRule.deleteThisRepeatInstanceFromRepeatRuleListOfInstances(this);
            myRepeatRule.updateRepeatInstancesOnDoneCancelOrDelete(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
            //NB. We don't delete the item's refs to repeatrule
        }

        //TODO!!!! remove item from OriginalSource field (from copies of this task) - all these links are one way, so need to search in ParseServer.
        //if deleting them, maybe replace the ObjectId link by the text of the original task??
        //or better simply to leave them and add a mechanism to ignore soft-deleted sources?? The mechanism would likely have to check the server each time, so 
        //this could make it very slow!!!!
        //ALARMS remove any active alarms
        AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //may have to be called *after* deleting the item from Parse to remove any scheduled app alarms
    }

    /**
     * delete the item, as well as sub-tasks. Remove it from categories and
     * alarm server. Stop timer if running. For items with repeatrules, user
     * must decide if only to delete this instance or all instances.
     *
     */
    @Override
    public boolean softDelete() { //throws ParseException {
        return softDelete(false); //false=leave references from softDeleted objects to other elements (Categories, owner Item/ItemList etc) - easier to restore
    }

    @Override
    public boolean softDelete(boolean removeRefs) { //throws ParseException {

//<editor-fold defaultstate="collapsed" desc="comment">
//        //DELETE SUBTASKS - delete all subtasks (since they are owned by this item)
//        List<Item> itemsSubtasksOfThisItem = getListFull();
//        for (Item item : itemsSubtasksOfThisItem) {
//            item.softDelete(removeRefs); //let each item delete itself properly, will recurse down the project hierarchy
//        }
//
//        //TODO!!! (?)anything to do to handle case where subtasks are created and saved, but where the new mother task is finally not saved?
//        //DELETE IN CATEGORIES
//        // remove item from all categories before deleting it
//        for (Category cat : getCategories()) {
//            ((Category) cat).removeItemFromCategory(this, removeRefs); //remove references to this item from the category before deleting it (false: but keep the item's categories)
//            DAO.getInstance().saveInBackground((ParseObject) cat);
//        }
//
//        //DELETE IN OWNERS/PROJECTS
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner instanceof Item) {
//            ((Item) owner).removeFromList(this, removeRefs); //
//            DAO.getInstance().saveInBackground((ParseObject) owner);
//        } else {
//            owner.removeFromList(this);
//            DAO.getInstance().saveInBackground((ParseObject) owner);
//        }
//
//        //handle repeatrule
//        //TODO!!!! handle repeatRules when deleting an Item
//        RepeatRuleParseObject myRepeatRule = getRepeatRule();
//        if (myRepeatRule != null) {
////            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we
////            myRepeatRule.deleteThisRepeatInstanceFromRepeatRuleListOfInstances(this);
//            myRepeatRule.updateRepeatInstancesOnDoneCancelOrDelete(this); //UI: if you delete (like if you cancel) a repeating task, new instances will be generated as necessary (just like if it is marked done) - NB. Also necessary to ensure that the repeatrule 'stays alive' and doesn't go stall because all previously generated instances were cancelled/deleted...
//            //NB. We don't delete the item's refs to repeatrule
//        }
//
//        //TODO!!!! remove item from OriginalSource field (from copies of this task) - all these links are one way, so need to search in ParseServer.
//        //if deleting them, maybe replace the ObjectId link by the text of the original task??
//        //or better simply to leave them and add a mechanism to ignore soft-deleted sources?? The mechanism would likely have to check the server each time, so
//        //this could make it very slow!!!!
//        //ALARMS remove any active alarms
//        AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //may have to be called *after* deleting the item from Parse to remove any scheduled app alarms
//</editor-fold>
        softDeleteImpl(new MyDate(), true);
//        setDeletedDate(new Date());
        DAO.getInstance().saveInBackground(this);

        return true;
        //TODO: any other references to an item??
/////////////////////////////////////////////////////
//<editor-fold defaultstate="collapsed" desc="comment">
////        super.deleteRuleAndAllRepeatInstancesExceptThis(); //delete the list in DAO, call changelisteners
//
//        TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: make TimerScreen a listener on the timed item to be notified if its deleted
//
//        //do nothing for RepeatRules since they get a callback when an item is deleted
//        MyRepeatRule myRepeatRule = getRepeatRule();
//        if (myRepeatRule != null) {
////            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this, (ItemList) getOwner());
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we
//        }
//        if (subitems != null) {
//            subitems.deleteAllItemsInList(pushToCalendardOnCompleted);
//            subitems = null; //help GC
//        }
//        // TODO: remove any active alarms!!
//        AlarmServer.getInstance().remove(this); //TODO!!!! should happen automatically via delete change event?
//</editor-fold>
    }

    public void deleteXXX() throws ParseException {
        softDeleteImpl(new MyDate(), false); //ONLY soft-delete is supported, softDeleteImpl() won't handle a deep hard-delete!
        super.delete();
    }

    /**
     * use this call to check if a subitems list has been created, rather than
     * getItemList.getSize() which will just create an unnecessary empty list
     *
     * @return
     */
//    public int getItemListSize() {
////        return (getItemList().size());
//        return (getList().size());
////        return (subitems == null || subitems.getSize() == 0) ? 0 : subitems.getSize();
//    }
    /**
     * returns list of subitems/sub-tasks. If no list previously existed, the
     * list is created. Use getItemListSize() to check if a list already exists
     * to avoid creating unnecessary sublists.
     *
     * @return (never null)
     */
//    List<Item> getList() {
//    @Override
    @Override
//    public List<Item> getList() {
    public List getListFull() {
        List<Item> list = getList(PARSE_SUBTASKS);

        if (list != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
//            if (Config.CHECK_OWNERS && list != null) {
//                for (Item item : list) {
//                    ASSERT.that(item.getOwner() == this||(item.getOwner() instanceof ItemList && item.getOwner().isNoSave()), "ERROR in owner: Item=" + item.toString(false)
//                            + ", item.owner="
//                            + (item.getOwner() instanceof ItemList
//                            ? (", item.owner=" + ((ItemList) item.getOwner()).toString(false))
//                            : (", item.owner=" + ((Item) item.getOwner()).toString(false)))
//                            + ", should be=" + this.toString(false));
//                }
//            }
            if (Config.CHECK_OWNERS) {
                checkOwners(list);
            }
            return list;
        } else {
            return new ArrayList();
        }
    }

    @Override
//    public List<Item> getList() {
    public List getList() {
        return getListFull(); //TODO!!!! implement filter/sort
    }

    /**
     * returns true if subtask is a subtask of this Item
     *
     * @param subtask
     * @return
     */
    public boolean hasAsSubtask(Item subtask) {
        return getListFull().indexOf(subtask) >= 0;
    }

//    @Override
//    public int getSize() { //
//        return getList().size();
//    }
    /**
     * NB. The index must be in the full (unfiltered) list! Hence recommended to
     * use addToList(ItemAndListCommonInterface item, ItemAndListCommonInterface
     * subtask, boolean addAfterItem) which calculates right index for subtask
     *
     * @param index
     * @param subtask
     * @return
     */
//    @Override
    private boolean addToList(int index, ItemAndListCommonInterface subtask) {
        boolean status = true;
        List listFull = getListFull();
//        List list = getList();
//        if (listFull.size() == 0)
//            listFull.add(subtask);
//        else {
//            if (index >= list.size())
//                listFull.add(subtask);
//            else {
//                int indexFull = listFull.indexOf(list.get(index)); //Math.min: if adding to end of list; convert index in filtered list to index in full list NB! doesn't work if there should be multiple instances one day/somewhere!!
//                listFull.add(indexFull, subtask);
//            }
//        }
        listFull.add(index, subtask);
        ASSERT.that(subtask.getOwner() == null || this == subtask.getOwner(), "subItemOrList owner not null when adding to list, SUBTASK=" + subtask + ", OLD OWNER=" + subtask.getOwner() + ", NEW OWNER=" + this);
        ItemAndListCommonInterface oldOwner = subtask.getOwner();
        subtask.setOwner(this);
        if (false) { //now done in setOwner() above
            ((Item) subtask).removeValuesInheritedFromOwner(oldOwner);
//        ((Item) subtask).updateValuesInheritedFromOwner(this, (oldOwner instanceof Item) ? (Item) oldOwner : null);
            ((Item) subtask).updateValuesInheritedFromOwner(this);
        }
        setList(listFull);
        return status;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList, boolean addToEndOfList) {
        List listFull = getListFull();
//        listFull.add(addToEndOfList ? listFull.size() : 0, subItemOrList);
//        setList(listFull);
        addToList(addToEndOfList ? listFull.size() : 0, subItemOrList);
        return true;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        List subtasks = getList();
//        boolean status = subtasks.add(subtask);
//        setList(subtasks);
//        return status;
//        return addToList(MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : getList().size(), subItemOrList); //TODO!!! UI: consider if it makes sense to insert at beginning of a list of subtasks just because of this setting
//        return addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : getList().size(), subItemOrList); //DONE!!! UI: consider if it makes sense to insert at beginning of a list of subtasks just because of this setting
//        if (MyPrefs.insertNewItemsInStartOfLists.getBoolean())
//            return addToList((ItemAndListCommonInterface) subItemOrList, (ItemAndListCommonInterface) getListFull().get(0), false);
//        else
//            return addToList((ItemAndListCommonInterface) subItemOrList, (ItemAndListCommonInterface) getListFull().get(getListFull().size()), true);
//</editor-fold>
        return addToList((ItemAndListCommonInterface) subItemOrList, MyPrefs.insertNewItemsInStartOfLists.getBoolean());
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface newItem, ItemAndListCommonInterface referenceItem, boolean addAfterItemOrEndOfList) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        List subtasks = getListFull();
//        boolean status = true;
////        try {
//        int index = subtasks.indexOf(item);
//        subtasks.add(index + (addAfterItem ? 1 : 0), subItemOrList);
//        assert subItemOrList.getOwner() == null : "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this;
//        subItemOrList.setOwner(this);
////        } catch (Exception e) {
////            status = false;
////        }
//        setList(subtasks);
//        return status;
//        int index = subtasks.indexOf(item);
//</editor-fold>
        List listFull = getListFull();
        int indexFull = referenceItem == null ? (addAfterItemOrEndOfList ? listFull.size() : 0) : listFull.indexOf(referenceItem) + (addAfterItemOrEndOfList ? 1 : 0);
//        if (indexFull < 0) {
//            ASSERT.that(false, "REFERENCE item not found in addToList(newItem,refItem), refItem=" + referenceItem + ", newItem=" + newItem);
//            return addToList(newItem); //UI: else add to end of list //TODO! should this depend on a setting?
//        } else
//            return addToList(indexFull + (addAfterItemOrEndOfList ? 1 : 0), newItem);
        return addToList(indexFull, newItem);
    }

//    @Override
//    public boolean removeFromList(ItemAndListCommonInterface subItemOrList,boolean removeReferences)) {
//        return removeFromList(subItemOrList, true);
//    }
    @Override
//    public boolean removeFromList(ItemAndListCommonInterface subItemOrList, boolean removeOwner) {
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList, boolean removeOwner) {
        List subtasks = getListFull();
        boolean status = subtasks.remove(subItemOrList);
        setList(subtasks);
        assert subItemOrList.getOwner() == this : "list not owner of removed subtask, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this;
        if (removeOwner)// && getOwner() == this)
        {
            subItemOrList.setOwner(null);
        }
        ((Item) subItemOrList).removeValuesInheritedFromOwner(this);
        return status;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getItemList() {
    /**
     * returns a list with the current subtasks, or an empty list if none are
     * defined.
     *
     * @return always a list
     */
//    public ItemList getItemList() {
////        List subitemslist = getList(PARSE_SUBTASKS);
//        List subitemslist = getList();
//        return subitemslist == null ? new ItemList() : new ItemList(subitemslist);
////        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        Item.
////        query.include(PARSE_SUBTASKS);
////        query.include(PARSE_SUBTASKS+"."+Item.CLASS_NAME);
////        query.include(Item.PARSE_SUBTASKS + "." + Item.PARSE_TEXT);
////        List subitemslist = null;
////        try {
////            subitemslist = query.find();
////        } catch (ParseException ex) {
//////            switch (ex.)
////            Log.e(ex);
////        }
////        if (subitemslist == null) {
//////            new LinkedList<Item>();
////            subitems = new ItemList();
////        } else {
////            subitems = new ItemList(subitemslist);
////        }
//////        if (subitems == null) {
//////            subitems = new ItemList(BaseItemTypes.ITEM, this, false, isEnsureItemCategoryAutoConsistency(), true, true); //ItemListSaveInline(this); //addMultipleInstances=false(even though it gives an expensive test on each insertion it is safer so no insertLink can make a subtask appear several times)
//////        }
////        return subitems;
//    }
//
//    public void setItemList(ItemList itemList) {
//        setList(itemList.getList());
//    }
//</editor-fold>
    /**
     * returns the index of the subitem/subtask in the list of subtasks
     *
     * @param subitem
     * @return
     */
    @Override
    public int getItemIndex(ItemAndListCommonInterface subitem) {
        return getList().indexOf(subitem);
    }

    /**
     * sets this Item's list of subitems to be the same as itemList. This is
     * done by comparing the two lists and adding new items in itemList to the
     * subitem list, or by removing the items not in itemList from the subitem
     * list. It is done in this way to avoid creating unnecessary change events
     * from adding/removing items which are already in/not in the subitem list.
     *
     * @param listOfSubtasks
     */
//    public void setItemList(ItemList itemList) {
//    @Override
//    public void setList(List itemList) {
//        setItemList(itemList);
//    }
//    public void setItemList(List itemList) {
    @Override
    public void setList(List listOfSubtasks) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (has(PARSE_SUBTASKS) && itemList != null && itemList.isEmpty()) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//        {
//            remove(PARSE_SUBTASKS);
//        } else {
//        if (has(PARSE_SUBTASKS) || ((itemList != null && !itemList.isEmpty()))) {// this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//            if (itemList != null && itemList.isEmpty()) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//            {
//                remove(PARSE_SUBTASKS); //if set to empty, remove instead
//            } else {
//                put(PARSE_SUBTASKS, itemList);
//            }
//        }
//        if (listOfSubtasks != null || ((listOfSubtasks != null && !listOfSubtasks.isEmpty()))) {// this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//            put(PARSE_SUBTASKS, listOfSubtasks);
//        } else {
//            remove(PARSE_SUBTASKS); //if set to empty, remove instead
//        }
//</editor-fold>
        if (listOfSubtasks == null || listOfSubtasks.isEmpty()) {// this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//            if (!getList().isEmpty()) { //if no more subtasks, then switch actual back again
//                setActualEffortInParse(getActualEffortProjectTaskItself()); //store project task's own Actual separately
//                setActualEffortProjectTaskItselfInParse(0); //delete old value
//            }
            remove(PARSE_SUBTASKS); //if set to empty, remove instead
        } else {
//            if (getList().isEmpty()) { //first subtask added
//                setActualEffortProjectTaskItselfInParse(getActualEffortFromParse()); //store project task's own Actual separately
//            }
            put(PARSE_SUBTASKS, listOfSubtasks);
        }
        updateValuesDerivedFromSubtasksWhenSubtaskListChange(); //update eg if added first subtasks, meaning ActualEffort must be updated
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (getItemListSize() == 0 && itemList.isEmpty()) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//        {
//            return; //do  nothing if both lists are empty
//        }
//        //TODO!! risk that this call will create unnecessary empoty sublists - replace an empty list with a locally (in this method) created empty list instead
////        ListCompared.updateListWithDifferences(getItemList(), itemList);
//        getItemList().updateListWithDifferences(itemList);
//</editor-fold>
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void timerStart(Date startTime) {
//        Date timerStarted = getDate(PARSE_TIMER_STARTED);
//        if (timerStarted != null) {
//            throw new RuntimeException("Timer started while timer already running");
//        }
//        //TODO: handle an already running timer: simply stop it and update the running item correctly, possibly with a pop-up information dialog
//        put(PARSE_TIMER_STARTED, startTime); //set started time
//        remove(PARSE_TIMER_PAUSED); //remove any paused time when timer is started
//    }
//
//    public void timerPause(Date pauseTime) {
//        Date timerStarted = getDate(PARSE_TIMER_STARTED);
//        if (timerStarted != null) {
//            throw new RuntimeException("Paused when no timer was running");
//        }
//        put(PARSE_TIMER_PAUSED, pauseTime); //set timer to now
//    }
//
//    public boolean timerIsPaused() {
//        return get(PARSE_TIMER_PAUSED) != null; //set timer to now
//    }
//    public long timerRunningTime() {
//        Date timerStarted = getDate(PARSE_TIMER_STARTED);
//        Date timePaused = getDate(PARSE_TIMER_PAUSED);
//        long timeSpent = timerStarted == null ? 0 : (timerStarted.getTime() - (timePaused == null ? (new Date().getTime()) : timePaused.getTime()));
//        return timeSpent;
//    }
//    public void timerStopAndUpdateActualEffortXXX() {
//        long timeSpent = timerRunningTime();
//        remove(PARSE_TIMER_STARTED);
//        remove(PARSE_TIMER_PAUSED);
//        setActualEffort(getActualEffort() + timeSpent); //store new actual effort
//    }
//</editor-fold>
    public void setStarred(boolean starred) {
//        if (starred) {
//        if (starred
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean())
//                || getOwnerItem() == null || !getOwnerItem().isStarred())) {
        if (starred) {
            put(PARSE_STARRED, true);
        } else {
            remove(PARSE_STARRED); //no value set means not starred
        }
    }

    public boolean isStarInheritedFrom(Boolean potentiallyInheritedValue) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        Boolean starred = getBoolean(PARSE_STARRED);
//        Boolean starred = isStarred();
//        return starred == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()
//                && potentiallyInheritedValue == starred;
//</editor-fold>
//        return isInherited(isStarred(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerStarredProperties.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().isStarred(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerStarredProperties.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isStarInherited() {
//<editor-fold defaultstate="collapsed" desc="comment">
////        Boolean starred = getBoolean(PARSE_STARRED);
//        Boolean starred = isStarred();
////        return starred == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().isStarred() == starred;
//</editor-fold>
//        return getOwnerItem() != null ? isStarInheritedFrom(getOwnerItem().isStarred()) : false;
        return isStarInheritedFrom(isStarred());
    }

    public boolean isStarred() {
//        return isStarred(true);
//    }
//
//    public boolean isStarred(boolean useInheritedValue) {
//        Boolean b = getBoolean(PARSE_STARRED);
//        if (b == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().isStarred();
//            }
//        }
        return getBoolean(PARSE_STARRED) != null;
    }

    public int getPriority() {
//        return getPriority(true);
//    }
//
//    public int getPriority(boolean useInheritedValue) {
        Integer i = getInt(PARSE_PRIORITY);
//        if (i == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getPriority();
//            }
//        }
        return i == null ? 0 : i;
////        return priority;
//        return priority.getPriority();
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isPriorityInherited(Integer potentiallyInheritedValue) {
//        Integer i = getInt(PARSE_PRIORITY);
//        Integer i = getPriority();
//        return i == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getPriority() != 0;
//        return isInherited(getPriority(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectPriority.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getPriority(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectPriority.getBoolean());
        } else {
            return false;
        }

    }

    public boolean isPriorityInherited() {
//        Integer i = getInt(PARSE_PRIORITY);
//        Integer i = getPriority();
//        return i == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getPriority() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getPriority() == i;
//        return getOwnerItem() != null ? isPriorityInheritedFrom(getOwnerItem().getPriority()) : false;
        return isPriorityInherited(getPriority());
    }

    public Priority getPriorityObject() {
//        return getPriorityObject(true);
//    }
//
//    public Priority getPriorityObject(boolean useInheritedValue) {
//        return priority;
//        return new Priority(getPriority(useInheritedValue));
        return new Priority(getPriority());
    }

    public void setPriority(int prio) {
//        if (prio != 0
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getPriority() != prio)) {
        int oldVal = getPriority();
        if (MyUtil.neql(prio, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getPriority(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setPriority(prio);
                    return true;
                }
                return false;
            });
        }
        if (prio != 0) {
            put(PARSE_PRIORITY, prio);
        } else {
            remove(PARSE_PRIORITY);
        }
////        this.priority = val;
////        if (this.priority != val) {
//        if (this.priority.getPriority() != prio) {
////            this.priority = val;
//            this.priority.setPriority(prio);
//        }
    }
//    public static String[] getChallengeStringArray() {
//        return challengeNames;
//    }
//
//    public static int[] getChallengeValuesArray() {
//        return challengeValues;
//    }

    public Challenge getChallengeN() {
//        return getChallengeN(true);
//    }
//
//    public Challenge getChallengeN(boolean useInheritedValue) {
//        String challenge = getString(PARSE_CHALLENGE);
//        return (challenge == null) ? Challenge.AVERAGE.getDescription() : challenge;
//        String challenge = getString(PARSE_CHALLENGE);
//        return (challenge == null) ? "" : challenge;
        String challenge = getString(PARSE_CHALLENGE);
//        if (challenge == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getChallengeN();
//            }
//        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (challenge == null) ? null : Challenge.valueOf(challenge); //Created is initial value
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isChallengeInherited(Challenge potentiallyInheritedValue) {
////        String challenge = getString(PARSE_CHALLENGE);
//        Challenge challenge = getChallengeN();
////        return challenge == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()
//                && getOwnerItem() != null && MyUtil.eql(getOwnerItem().getChallengeN(), challenge);
//        return isInherited(getChallengeN(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectChallenge.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getChallengeN(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectChallenge.getBoolean());
        } else {
            return false;
        }

    }

    public boolean isChallengeInherited() {
////        String challenge = getString(PARSE_CHALLENGE);
//        Challenge challenge = getChallengeN();
////        return challenge == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()
//                && getOwnerItem() != null && MyUtil.eql(getOwnerItem().getChallengeN(), challenge);
//        return getOwnerItem() != null ? isChallengeInheritedFrom(getOwnerItem().getChallengeN()) : false;
        return isChallengeInherited(getChallengeN());
    }

    public void setChallenge(Challenge challenge) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (has(PARSE_CHALLENGE)) { // || !challenge.equals(Challenge.AVERAGE.getDescription())) { //no need to save a value since a null pointer is interprested as zero
//            put(PARSE_CHALLENGE, challenge);
//        }
//        if (challenge != null && !challenge.equals("")) {
//            put(PARSE_CHALLENGE, challenge);
//        } else {
//            remove(PARSE_CHALLENGE);
//        }
//        if (challenge != null) {// && !dreadFunValue.equals("")) {
// A=> B <=> !A or B: inherit => getOwnerItem().getChallengeN() != challenge
//inherit && hasOwner && newValue!=oldValue
//        if (challenge != null
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getChallengeN() != challenge)) {
//</editor-fold>
        Challenge oldVal = getChallengeN();
        if (MyUtil.neql(challenge, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getChallengeN(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setChallenge(challenge);
                    return true;
                }
                return false;
            });
        }
        if (challenge != null) {
            put(PARSE_CHALLENGE, challenge.toString());
        } else {
            //remove value if (inherits && hasOwner && newValue==inheritedValue) <=> 
            //store newValue if !remove <=> !inherits || !hasOwner || newValue!=inheritedValue
            //where inherits == MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean() (both must be true)
            // <=> !(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()) ||
            //     getOwnerItem() == null || getOwnerItem().getChallengeN() != challenge
            remove(PARSE_CHALLENGE);
        }

    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private final static String[] dreadFunNames = new String[]{"Love it", "Neither", "Hate it"};
//    private final static String[] dreadFunNames = new String[]{"Fun", "Neutral", "Dread"};
//    private final static int[] dreadFunValues = new int[]{0, 1, 2};
//
//    public static String[] getDreadFunStringArray() {
//        return dreadFunNames;
//    }
//
//    public static int[] getDreadFunValuesArray() {
//        return dreadFunValues;
//    }
//    public int getDreadFunValueN() {
//        Integer dreadFunValue = getInt(PARSE_DREAD_FUN_VALUE);
//        return (dreadFunValue == null) ? 0 : dreadFunValue;
////        return dreadFunValue;
//    }
//
//    public void setDreadFunValue(int dreadFunValue) {
//        if (has(PARSE_DREAD_FUN_VALUE) || dreadFunValue != 0) {
//            put(PARSE_DREAD_FUN_VALUE, dreadFunValue);
//        }
////        if (dreadFunValue != this.dreadFunValue) {
////            this.dreadFunValue = dreadFunValue;
////        }
//    }
//</editor-fold>
//    public String getDreadFunValueN() {
    /**
     * returns null if no value is defined
     *
     * @return
     */
    public DreadFunValue getDreadFunValueN() {
//        return getDreadFunValueN(true);
//    }
//
//    public DreadFunValue getDreadFunValueN(boolean useInheritedValue) {
        String dreadFunValue = getString(PARSE_DREAD_FUN_VALUE);
//        if (dreadFunValue == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getDreadFunValueN();
//            }
//        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (dreadFunValue == null) ? null : DreadFunValue.valueOf(dreadFunValue); //Created is initial value
    }

    /*returns true if the value is inherited from it's owner (returns false if the value could be inherited but owner does not define any value)
    @return 
     */
    public boolean isDreadFunInherited(DreadFunValue potentiallyInheritedValue) {
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getDreadFunValueN(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isDreadFunInherited() {
        return isDreadFunInherited(getDreadFunValueN());
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
//    public boolean isDreadFunInheritedXXX() {
//        String dreadFunValue = getString(PARSE_DREAD_FUN_VALUE);
//        return dreadFunValue == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getDreadFunValueN() != null;
//    }
    /**
     * removes the value if called with null (useful when no default value is
     * defined)
     *
     * @param dreadFunValue
     */
    public void setDreadFunValue(DreadFunValue dreadFunValue) {
//        if (dreadFunValue != this.dreadFunValue) {
//            this.dreadFunValue = dreadFunValue;
//        }
//        if (has(PARSE_DREAD_FUN_VALUE)) { // || dreadFunValue.equals("") ) {
//            put(PARSE_DREAD_FUN_VALUE, dreadFunValue);
//        }
//        if (dreadFunValue != null) {// && !dreadFunValue.equals("")) {
        DreadFunValue oldVal = getDreadFunValueN();
        if (MyUtil.neql(dreadFunValue, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getDreadFunValueN(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setDreadFunValue(dreadFunValue);
                    return true;
                }
                return false;
            });
        }
//        if (dreadFunValue != null
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getDreadFunValueN() != dreadFunValue)) {
        if (dreadFunValue != null) {
            put(PARSE_DREAD_FUN_VALUE, dreadFunValue.toString());
        } else {
            remove(PARSE_DREAD_FUN_VALUE);
        }
    }

    interface UpdateItem {

        boolean update(Item item); //, Object oldValue, Object newValue);
    }

    private List<UpdateItem> opsToUpdateSubtasks = new ArrayList(); //list of operations to execute 
    private List<Runnable> opsAfterSubtaskUpdates = new ArrayList(); //operations to run once all changes to Item's fields have been made, e.g. repeatRules
//    private List<Runnable> opsPreSave = new ArrayList();
    HashMap<String, Object> saveOps = new HashMap<String, Object>();

//    void setImportance(HighMediumLow importance) {
    void setImportance(HighMediumLow importance) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (has(PARSE_IMPORTANCE) || importance != HighMediumLow.LOW) {
//        if (has(PARSE_IMPORTANCE) || !importance.equals(HighMediumLow.LOW.toString())) {
//        if (has(PARSE_IMPORTANCE)) { // || !importance.equals(HighMediumLow.LOW.toString())) {
//            put(PARSE_IMPORTANCE, importance.toString());
//        if (importance != null && !importance.equals("")) {
//            put(PARSE_IMPORTANCE, importance);
//        } else {
//            remove(PARSE_IMPORTANCE);
//        }
//        if (importance != null) {// && !dreadFunValue.equals("")) {
// if a value is defined, and inheritance
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean()
//                && MyPrefs.itemInheritOwnerProjectImportance.getBoolean())
//        if (importance != null
//                && (!MyPrefs.itemInheritOwnerProjectProperties.getBoolean()
//                || !MyPrefs.itemInheritOwnerProjectImportance.getBoolean()
//                || getOwnerItem() == null
//                || getOwnerItem().getImportanceN() != importance)) {
//equals: (a == null) ? (a == b) : a.equals(b) <=> (a == null) ? b == null : a.equals(b)  -->> https://stackoverflow.com/questions/1402030/compare-two-objects-with-a-check-for-null
//!equals: (a == null) ? (a == b) : a.equals(b) <=> (a == null) ? b == null : a.equals(b)  -->> https://stackoverflow.com/questions/1402030/compare-two-objects-with-a-check-for-null
//</editor-fold>
        HighMediumLow oldVal = getImportanceN();
        if (MyUtil.neql(importance, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getImportanceN(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setImportance(importance);
                    return true;
                }
                return false;
            });
        }
        if (importance != null) {
            put(PARSE_IMPORTANCE, importance.toString());
        } else {
            remove(PARSE_IMPORTANCE);
        }
//        }
    }

//    public HighMediumLow getImportanceN() {
//    public HighMediumLow getImportanceN() {
//        return getImportanceN(true);
//    }
//    public HighMediumLow getImportanceN(boolean useInheritedValue) {
    public HighMediumLow getImportanceN() {
//        String importance = getString(PARSE_IMPORTANCE);
////        return (importance == null) ? HighMediumLow.LOW : HighMediumLow.valueOf(importance); //Created is initial value
////        return (importance == null) ? HighMediumLow.LOW.toString() : importance; //Created is initial value
//        return (importance == null) ? "" : importance; //Created is initial value
        String imp = getString(PARSE_IMPORTANCE);
//        if (imp == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectImportance.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getImportanceN();
//            }
//        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (imp == null) ? null : HighMediumLow.valueOf(imp); //Created is initial value
    }

    /*returns true if the value is inherited from it's owner (returns false if the value could be inherited but owner does not define any value)
    @return 
     */
    public boolean isImportanceInherited(HighMediumLow potentiallyInheritedValue) {
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getImportanceN(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectImportance.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isImportanceInherited() {
        return isImportanceInherited(getImportanceN());
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
//    public boolean isImportanceInheritedXXX() {
//        String imp = getString(PARSE_IMPORTANCE);
//        return imp == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectImportance.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getImportanceN() != null;
//    }
//    void setUrgency(HighMediumLow urgency) {
    void setUrgency(HighMediumLow urgency) {
//        if (has(PARSE_URGENCY) || urgency != HighMediumLow.LOW) {
//        if (has(PARSE_URGENCY)) { // || !urgency.equals(HighMediumLow.LOW.toString())) {
//            put(PARSE_URGENCY, urgency.toString());
//        if (urgency != null && !urgency.equals("")) {
//            put(PARSE_URGENCY, urgency);
//        if (urgency != null) {// && !dreadFunValue.equals("")) {
        HighMediumLow oldVal = getUrgencyN();
        if (MyUtil.neql(urgency, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getUrgencyN(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setUrgency(urgency);
                    return true;
                }
                return false;
            });
        }
//        if (urgency != null
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectUrgency.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getUrgencyN() != urgency)) {
        if (urgency != null) {
            put(PARSE_URGENCY, urgency.toString());
        } else {
            remove(PARSE_URGENCY);
        }
    }

//    public HighMediumLow getUrgencyN() {
    public HighMediumLow getUrgencyN() {
//        return getUrgencyN(true);
//    }
//
//    public HighMediumLow getUrgencyN(boolean useInheritedValue) {
//        String urgency = getString(PARSE_URGENCY);
////        return (status == null) ? HighMediumLow.LOW : HighMediumLow.valueOf(status); //Created is initial value
////        return (status == null) ? HighMediumLow.LOW.toString() : status; //Created is initial value
//        return (urgency == null) ? "" : urgency; //Created is initial value
        String urgency = getString(PARSE_URGENCY);
//        if (urgency == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectUrgency.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getUrgencyN();
//            }
//        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (urgency == null) ? null : HighMediumLow.valueOf(urgency); //Created is initial value
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isUrgencyInherited(HighMediumLow potentiallyInheritedValue) {
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getUrgencyN(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectUrgency.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isUrgencyInherited() {
        return isUrgencyInherited(getUrgencyN());
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
//    public boolean isUrgencyInheritedXXX() {
//        String urgency = getString(PARSE_URGENCY);
//        return urgency == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectImportance.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getUrgencyN() != null;
//    }
    /**
     * returns a numerical value of the Importance/Urgency pair, or zero if only
     * one is defined
     *
     * @return
     */
    public int getImpUrgPrioValue() {
        //TODO define numerical priority of Imp/Urg pair when only one of them is defined, e.g. Imp=H/M/L=>9/7/3, Urg=H/M/L=>
//<editor-fold defaultstate="collapsed" desc="comment">
//            matrixVector.addElement(new PriorityPair(HIGH, HIGH, 8));
//            matrixVector.addElement(new PriorityPair(HIGH, MED, 7));
//            matrixVector.addElement(new PriorityPair(MED, HIGH, 6));
//            matrixVector.addElement(new PriorityPair(MED, MED, 5));
//            matrixVector.addElement(new PriorityPair(HIGH, LOW, 4));
//            matrixVector.addElement(new PriorityPair(MED, LOW, 3));
//            matrixVector.addElement(new PriorityPair(LOW, HIGH, 2));
//            matrixVector.addElement(new PriorityPair(LOW, MED, 1));
//            matrixVector.addElement(new PriorityPair(LOW, LOW, Settings.getInstance().getDefaultPriority())); //Must be 0 or less!!

//        String impStr = getImportanceN();
//        String urgStr = getUrgencyN();
//        if (!impStr.equals("") && !urgStr.equals("")) {
////            ItemStatus itemTest = ItemStatus.valueOf(ItemStatus.STATUS_ONGOING.getDescription()); //TODO test code, remove
////            ItemStatus.valueOf(status);
////            HighMediumLow imptst = HighMediumLow.valueOf(HighMediumLow.HIGH.getDescription()); //TODO test code, remove
//            HighMediumLow imp = HighMediumLow.valueOf(impStr);
//            HighMediumLow urg = HighMediumLow.valueOf(urgStr);
//    HighMediumLow[][] prio = new HighMediumLow[3][3] {{},{},{}};
        /**
         * indexed by
         */
//    int[HighMediumLow][HighMediumLow] prio2 = {{1,2,3},{4,6,7},{5,8,9}};
//</editor-fold>
        HighMediumLow imp = getImportanceN();
        HighMediumLow urg = getUrgencyN();
        int[][] prio = {{1, 2, 3}, {4, 6, 7}, {5, 8, 9}}; //allow editing of the prio values for the pairs - TODO how to avoid indexing by ordinals and use the enum constants directly, EnumMap in two dimensions?
//        if (imp != null && urg != null) {
//            return prio[imp.ordinal()][urg.ordinal()];
//        } else {
//            return 0;
//        }
        if (imp != null && urg != null) {
            switch (imp) {
                case HIGH:
                    switch (urg) {
                        case HIGH:
                            return 9;
                        case MEDIUM:
                            return 8;
                        case LOW:
                            return 5;
                    }
                case MEDIUM:
                    switch (urg) {
                        case HIGH:
                            return 7;
                        case MEDIUM:
                            return 6;
                        case LOW:
                            return 4;
                    }
                case LOW:
                    switch (urg) {
                        case HIGH:
                            return 3;
                        case MEDIUM:
                            return 2;
                        case LOW:
                            return 1;
                    }
            }
        }
        return 0;
    }

    /**
     * returns the string to display for the Imp/Urg pair in a list of items,
     * empty string if no values defined
     *
     * @return
     */
    public String getImpUrgPrioValueAsString() {
//        return getImpUrgPrioValue() != 0 ? getImportanceN().getDescription().substring(0, 1) + "/" + getUrgencyN().getDescription().substring(0, 1) : getPriority() != 0 ? getPriority() + "" : " ";
//        return getImpUrgPrioValue() != 0 ? getImportanceN().getDescription().substring(0, 1) + "/" + getUrgencyN().getDescription().substring(0, 1) : "";
        String impStr = getImportanceN() != null ? getImportanceN().getDescription().substring(0, 1) : "";
        String urgStr = getUrgencyN() != null ? getUrgencyN().getDescription().substring(0, 1) : "";
        if (impStr.length() > 0 || urgStr.length() > 0) {
            return (impStr.length() > 0 ? impStr : "-") + "/" + (urgStr.length() > 0 ? urgStr : "-");
        } else {
            return "";
        }
    }

    public double getEarnedValue() {
        Double earnedVal = getDouble(PARSE_EARNED_VALUE); //Parse doesn't store doubles, //TODO!!!! fix issue in parse to store reals
//        Double earnedVal = getInt(PARSE_EARNED_VALUE);
        return (earnedVal == null) ? 0 : earnedVal;
//        return earnedValue;
    }

    public void setEarnedValue(double earnedVal) {
        if (earnedVal != 0) {
            put(PARSE_EARNED_VALUE, earnedVal);
        } else {
            remove(PARSE_EARNED_VALUE);
        }
        updateEarnedValuePerHour();
    }

    private void updateEarnedValuePerHour() {
        setEarnedValuePerHour(calculateEarnedValuePerHour(getTotalExpectedEffort(), getEarnedValue()));
    }

    private void setEarnedValuePerHour(double earnedValPerHour) { //private since set automatically
        if (earnedValPerHour != 0) {
            put(PARSE_EARNED_VALUE_PER_HOUR, earnedValPerHour);
        } else {
            remove(PARSE_EARNED_VALUE_PER_HOUR);
        }
    }

    /**
     * returns earned points/ROI per hour, that is the number of earned points
     * divided by the effort. If remaining effort is defined then that + actual
     * effort is used, otherwise effort estimate. To avoid rounding errors
     * effort is calculated in minutes, and points are multiplied by 60. UI: If
     * no effort is defined, returns 0 (which means that
     *
     * returns the earned points per hour of work (=remaining+actual), or 0 if
     * no remaining or actual effort is defined (due to division by0)
     *
     * @return
     */
    public static double calculateEarnedValuePerHour(long totalEffort, double earnedPoints) {
//        int effortInHours = (int) totalEffort / MyDate.HOUR_IN_MILISECONDS; //divide by HOUR_IN first to minimize loss of precision
        if (totalEffort > 0) {
            return ((earnedPoints * MyDate.HOUR_IN_MILISECONDS) / totalEffort);
        } else {
            return 0;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public double getEarnedValuePerHour() {
////TODO introduce a setting to always calculate valuePerHour based on Estimate (to avoid it changes when once has started, e.g. if remaining is not set). MAYBE: now,it only used Remaining+Actual when Remaining is set to sth.
//        return calculateEarnedValuePerHour(getTotalExpectedEffort(), getEarnedValue());
////        long totalEffort = getTotalExpectedEffort();
////        if (totalEffort > 0) {
////            return ((getEarnedValue() * MyDate.HOUR_IN_MILISECONDS) / totalEffort);
////        } else {
////            return 0;
////        }
//
//    }
//</editor-fold>
    /**
     * returns earned points/ROI per hour, that is the number of earned points
     * divided by the effort. If remaining effort is defined then that + actual
     * effort is used, otherwise effort estimate. To avoid rounding errors
     * effort is calculated in minutes, and points are multiplied by 60. UI: If
     * no effort is defined, returns 0 (which means that
     *
     * returns the earned points per hour of work (=remaining+actual), or 0 if
     * no remaining or actual effort is defined (due to division by0)
     *
     * @return
     */
    public double getEarnedValuePerHour() {
        Double earnedValPerHour = getDouble(PARSE_EARNED_VALUE_PER_HOUR); //Parse doesn't store doubles, //TODO!!!! fix issue in parse to store reals
        return (earnedValPerHour == null) ? 0 : earnedValPerHour;
    }

    public long getExpiresOnDate() {
//        return expiresOnDate;
//        Date date = getDate(PARSE_EXPIRES_ON_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getExpiresOnDateD().getTime();
    }

    public Date getExpiresOnDateD() {
//        return new Date(getExpiresOnDate());
        Date date = getDate(PARSE_EXPIRES_ON_DATE);
        return (date == null) ? new Date(0) : date;
    }

    public void setExpiresOnDate(Date expiresOnDate) {
        setExpiresOnDateInParse(expiresOnDate);
    }

    public void setExpiresOnDateInParse(Date expiresOnDate) {
//        if (isProject()) {
//            Date oldDate = getDate(PARSE_EXPIRES_ON_DATE);
//            for (Item subtask : (List<Item>) getList()) {
//                Date subtaskDate = subtask.getExpiresOnDateD();
////<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
////                if (oldDate == null) {
////                    if (subtaskDate == null) {
////                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                    } else {
////                        //do nothing: subtask already had a date set before project got one
////                    }
////                } else { //oldDate!=null - a previous project date was set
////                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
////                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
////                        //do nothing (subtask had a defined date already and it was different from old project date)
////                    }
////                }
////                if (oldDate == null && subtaskDate == null) {
////                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
//////oldDate!=null - a previous project date was set
////                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                }
////</editor-fold>
//                //above expressions are equivalent to this reduced/simplified version:
//                if ((oldDate == null && subtaskDate == null) || (subtaskDate == null || subtaskDate.equals(oldDate))) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                    subtask.setExpiresOnDate(expiresOnDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//            }
//        }
        Date oldVal = getExpiresOnDateD();
        if (MyUtil.neql(expiresOnDate, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getExpiresOnDateD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setExpiresOnDate(expiresOnDate);
                    return true;
                }
                return false;
            });
        }
        if (expiresOnDate != null && expiresOnDate.getTime() != 0) {
            put(PARSE_EXPIRES_ON_DATE, expiresOnDate);
        } else {
            remove(PARSE_EXPIRES_ON_DATE);
        }
//        update();
    }

    public void setExpiresOnDate(long expiresOnDate) {
        setExpiresOnDate(new Date(expiresOnDate));
//        if (has(PARSE_EXPIRES_ON_DATE) || expiresOnDate != 0) {
//            put(PARSE_EXPIRES_ON_DATE, new Date(expiresOnDate));
//        }
    }

//    public void setExpiresOnDateD(Date expiresOnDate) {
//        setExpiresOnDate(expiresOnDate.getTime());
//    }
    public boolean isInteruptOrInstantTask() {
        Boolean interruptTask = getBoolean(PARSE_INTERRUPT_OR_INSTANT_TASK);
        return (interruptTask == null) ? false : interruptTask;
//        return interuptTask;
    }

    public void setInteruptOrInstantTask(boolean interuptOrInstantTask) {
//        if (has(PARSE_INTERRUPT_TASK) || interuptTask) {
//            put(PARSE_INTERRUPT_TASK, interuptTask); //only store true values (null corresponds to False)
//        }

        if (interuptOrInstantTask) {
            put(PARSE_INTERRUPT_OR_INSTANT_TASK, true);
        } else {
            remove(PARSE_INTERRUPT_OR_INSTANT_TASK);
        }
    }

    public Item getTaskInterrupted() {
//        return (Item) getParseObject(PARSE_INTERRUPTED_TASK);       
        Item interrupted = (Item) getParseObject(PARSE_INTERRUPTED_TASK);
//        return (Item) getParseObject(PARSE_ORIGINAL_SOURCE);
        return (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(interrupted);

    }

    /**
     * store the task that this interrupt task interrupted. Only set for
     * interrupt tasks. Done like this to enable a task to be interrupted by
     * many interrupt tasks (whereas an interrupt task can normally only
     * interrupt one single task)
     *
     * @param taskThatThisInterruptTaskInterrupted
     */
//    public void setTaskInterrupted(Item taskInterruptedByThisInterruptTask) {
    public void setTaskInterrupted(Item taskThatThisInterruptTaskInterrupted) {
//        if (has(PARSE_TASK_INTERRUPTED) || taskInterruptedByThisInterruptTask != null) {
//            put(PARSE_TASK_INTERRUPTED, taskInterruptedByThisInterruptTask);
//        }
        if (taskThatThisInterruptTaskInterrupted != null) {
            put(PARSE_INTERRUPTED_TASK, taskThatThisInterruptTaskInterrupted);
        } else {
            remove(PARSE_INTERRUPTED_TASK);
        }
    }

    public Item getDependingOnTask() {
//        return (Item) getParseObject(PARSE_DEPENDS_ON_TASK);
        Item dependingOn = (Item) getParseObject(PARSE_DEPENDS_ON_TASK);
//        return (Item) getParseObject(PARSE_ORIGINAL_SOURCE);
        return (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(dependingOn);

    }

    /**
     * store a task that this task depends on. The task this one depends on must
     * be Done before isDependingOnTasksDone returns true. This enables
     * filtering on (hiding) all tasks that are depending on other tasks until
     * they are done. In a first time, only dependency on a single task is
     * supported.
     *
     * @param taskThatThisTaskDependsOn
     */
//    public void setTaskInterrupted(Item taskInterruptedByThisInterruptTask) {
    public void setDependingOnTask(Item taskThatThisTaskDependsOn) {
//        if (has(PARSE_TASK_INTERRUPTED) || taskInterruptedByThisInterruptTask != null) {
//            put(PARSE_TASK_INTERRUPTED, taskInterruptedByThisInterruptTask);
//        }
        if (taskThatThisTaskDependsOn != null) {
            put(PARSE_DEPENDS_ON_TASK, taskThatThisTaskDependsOn);
        } else {
            remove(PARSE_DEPENDS_ON_TASK);
        }
    }

    public Item getSource() {
        Item source = (Item) getParseObject(PARSE_ORIGINAL_SOURCE);
//        return (Item) getParseObject(PARSE_ORIGINAL_SOURCE);
        return (Item) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(source);
    }

    /**
     * store a task that this task depends on. The task this one depends on must
     * be Done before isDependingOnTasksDone returns true. This enables
     * filtering on (hiding) all tasks that are depending on other tasks until
     * they are done. In a first time, only dependency on a single task is
     * supported.
     *
     * @param originalTaskThisOneIsACopyOf
     */
//    public void setTaskInterrupted(Item taskInterruptedByThisInterruptTask) {
    public void setSource(Item originalTaskThisOneIsACopyOf) {
//        if (has(PARSE_TASK_INTERRUPTED) || taskInterruptedByThisInterruptTask != null) {
//            put(PARSE_TASK_INTERRUPTED, taskInterruptedByThisInterruptTask);
//        }
        if (originalTaskThisOneIsACopyOf != null) {
            put(PARSE_ORIGINAL_SOURCE, originalTaskThisOneIsACopyOf);
        } else {
            remove(PARSE_ORIGINAL_SOURCE);
        }
    }

    /**
     * returns true if all tasks this task depends on are done (or if this task
     * doesn't depend on other tasks)
     *
     * @return
     */
    public boolean isDependingOnTasksDone() {
        Item dependsOnTask = getDependingOnTask();
        return dependsOnTask == null || dependsOnTask.isDone();
    }
//  

    public Date getAlarmDateD() {
        Date date = getDate(PARSE_ALARM_DATE);
//        return (date == null || isDone()) ? new Date(0) : date; //WHY? return no AlarmDate when Done? May prevent canceleling of alarms?
        return (date == null) ? new Date(0) : date;
//        return alarmDate;
    }

    public long getAlarmDate() {
//        Date date = getDate(PARSE_ALARM_DATE);
//        return (date == null) ? 0 : date.getTime();
        return getAlarmDateD().getTime();
//        return alarmDate;
    }

    /**
     * returns all alarms, after or equal afterDate (if defined, otherwise all),
     * sorted by date if sorted is true. Returns empty list if no alarms defined
     * after onOrAfterDate. Returns dates whether Item is Done or not.
     *
     * @return
     */
    public List<AlarmRecord> getAllAlarmRecords(Date onOrAfterDate, boolean sorted) {
        //TODO //optimization: keep track of whether alarms have been changed to avoid recalculating this at every  call of getNextcomingAlarm() //NO, probably very little to be gained
        List<AlarmRecord> list = new ArrayList();
        AlarmRecord alarmRecord;
        Date date;
//        if ((date = getSnoozeDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
        if ((alarmRecord = getSnoozeAlarmRecord()) != null && (alarmRecord == null || alarmRecord.alarmTime.getTime() >= onOrAfterDate.getTime())) {
//            list.add(new AlarmRecord(date, com.todocatalyst.todocatalyst.AlarmType.snooze));
            list.add(alarmRecord);
        }
//        if ((date = getAlarmDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
        if ((date = getAlarmDateD()).getTime() != 0 && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
            list.add(new AlarmRecord(date, notification));
        }
//        if ((date = getWaitingAlarmDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
        if ((date = getWaitingAlarmDateD()).getTime() != 0 && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
            list.add(new AlarmRecord(date, waiting));
        }
        if (sorted && list.size() > 1) {
            Collections.sort(list, (object1, object2) -> {
                return FilterSortDef.compareDate(object1.alarmTime, object2.alarmTime);
            });
        }
        return list;
    }

    public List<AlarmRecord> getAllFutureAlarmRecordsSorted() {
        return getAllAlarmRecords(new MyDate(), true);
    }

    public AlarmRecord getNextcomingAlarmRecord() {
        List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * returns first-coming future alarm (larger than or equal to now) or null
     * if no alarms are defined.
     *
     * @return
     */
    public Date getNextcomingAlarm() {
//        return getAllAlarmRecords(new Date(), true).get(0).alarmTime;
        List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        return list.isEmpty() ? null : list.get(0).alarmTime;
    }

    public void updateNextcomingAlarm() {
        Date date = getNextcomingAlarm(); //List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        if (date == null || date.getTime() == 0) { //list.isEmpty()) {
            remove(PARSE_NEXTCOMING_ALARM);
        } else {
            put(PARSE_NEXTCOMING_ALARM, date); //list.get(0).alarmTime);
        }
    }
///<editor-fold defaultstate="collapsed" desc="comment">
//    private void updateFirstAlarmOLD() {
//        Date alarm = getDate(PARSE_ALARM_DATE);
//        Date waiting = getDate(PARSE_WAITING_ALARM_DATE);
//        if (alarm != null) {
//            if (waiting != null) {
//                if (alarm.getTime() < waiting.getTime()) {
//                    put(PARSE_NEXTCOMING_ALARM, alarm);
//                } else {
//                    put(PARSE_NEXTCOMING_ALARM, waiting);
//                }
//            } else {
//                put(PARSE_NEXTCOMING_ALARM, alarm);
//            }
//        } else if (waiting != null) { //alarm==null
//            put(PARSE_NEXTCOMING_ALARM, waiting);
//        } else { // alarm==null && waiting==null
//            remove(PARSE_NEXTCOMING_ALARM);
//        }
//    }
//</editor-fold>

    /**
     * returns null if no date is defined
     */
    public Date getNextcomingAlarmDateD() {
        Date date = getDate(PARSE_NEXTCOMING_ALARM);
        return date; //(date == null) ? new Date(0) : date;
    }

    public void setNextcomingAlarmDate(Date firstAlarmDate) {
        if (firstAlarmDate != null && firstAlarmDate.getTime() != 0) {
            put(PARSE_NEXTCOMING_ALARM, firstAlarmDate);
        } else {
            remove(PARSE_NEXTCOMING_ALARM);
        }
    }

    public void setAlarmDate(Date alarmDate) {
//        if (has(PARSE_ALARM_DATE) || alarmDate.getTime() != 0) {
//            AlarmHandler.getInstance().updateReminderAlarm(this, getAlarmDateD(), alarmDate);
//            put(PARSE_ALARM_DATE, alarmDate);
//        }
//        Date oldAlarmDate = getAlarmDateD();
//        Date oldAlarmDate = getDate(PARSE_ALARM_DATE);
        Date oldAlarmDate = getAlarmDateD();
        if (alarmDate != null && alarmDate.getTime() != 0) {
            put(PARSE_ALARM_DATE, alarmDate);
        } else {
            remove(PARSE_ALARM_DATE);
        }
        if (false) {
            updateNextcomingAlarm(); //done in save() now
        }//        if (!isTemplate() && oldAlarmDate != null && alarmDate.getTime() != 0) {
        if (!isTemplate() && !oldAlarmDate.equals(alarmDate)) {
//            afterSaveActions.put(AFTER_SAVE_ALARM_UPDATE, () -> AlarmHandler.getInstance().updateReminderAlarm(this, oldAlarmDate, alarmDate));
            mustUpdateAlarms = true;
        }
//        AlarmServer.getInstance().update(this);
////        this.alarmDate = alarmDate;
//        if (this.alarmDate != alarmDate) {
//            this.alarmDate = alarmDate;
//            AlarmServer.getInstance().update(this);
////            changed(ChangeValue.CHANGED_XX_ITEM_CHANGED_ALARM_DATE);
//        }
    }

    public void setAlarmDate(long alarmDate) {
        setAlarmDate(new Date(alarmDate));
//        if (has(PARSE_ALARM_DATE) || alarmDate != 0) {
//            AlarmHandler.getInstance().updateReminderAlarm(this, getAlarmDateD(), new Date(alarmDate));
//            put(PARSE_ALARM_DATE, new Date(alarmDate));
//        }
//        AlarmServer.getInstance().update(this);
////        this.alarmDate = alarmDate;
//        if (this.alarmDate != alarmDate) {
//            this.alarmDate = alarmDate;
//            AlarmServer.getInstance().update(this);
////            changed(ChangeValue.CHANGED_XX_ITEM_CHANGED_ALARM_DATE);
//        }
    }

////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * // * returns the first (smallest) alarm for this item which is bigger
     * than or // * equal to time (this allows to avoid getting alarms in the
     * past). if there // * are no suitable alarms (none set or set earlier than
     * // * smallestTimeForAlarm), returns null. ---Date(Long.MAX_VALUE) (to
     * avoid // * that the absence of appropriate alarms will return a smaller
     * value than a // * currently set timer)--- // * // * @param time //
     *
     *
     * @return //
     */
//    private Date getFirstAlarmGreaterThanOrEqualUSING_FIRSTALARM(Date time) {
//        Date firstAlarm = getNextcomingAlarmDateD();
//        if (firstAlarm.getTime() >= time.getTime()) {
//            return firstAlarm;
//        } else {
////            return new Date(Long.MAX_VALUE);
//            return null;
//        }
//    }
//
//    private Date getFirstAlarmGreaterThanOrEqual(Date time) {
//        Date alarm = getAlarmDateD();
//        Date waiting = getWaitingAlarmDateD();
////        if (getAlarmDateD().getTime() >= time.getTime() && getAlarmDateD().getTime() > getWaitingAlarmDateD().getTime()) { //if alarm>time and alarm
//        if (alarm.getTime() != 0 && alarm.getTime() >= time.getTime()) { //if alarm defined AND in future
//            if (waiting.getTime() != 0 && waiting.getTime() >= time.getTime()) { //if waiting defined AND in future
//                //return smallest of the two
//                if (alarm.getTime() <= waiting.getTime()) {
//                    return alarm;
//                } else {
//                    return waiting;
//                }
//            } else { //waiting either not defined or not in future, return alarm
//                return alarm;
//            }
//        } else if (waiting.getTime() != 0 && waiting.getTime() >= time.getTime()) { //alarm not defined or not in future, but waiting defined AND in future, return waiting
//            return waiting;
//        }
////        return new Date(Long.MAX_VALUE);
//        return null; //if neither are defined or in future, return null
//    }
//
//    private Date getFirstAlarmGreaterThanOrEqualOLD(Date time) {
////        if (getAlarmDateD().getTime() >= time.getTime() && getAlarmDateD().getTime() > getWaitingAlarmDateD().getTime()) { //if alarm>time and alarm
//        if (getAlarmDateD().getTime() != 0 && getAlarmDateD().getTime() >= time.getTime()) {
//            if (getWaitingAlarmDateD().getTime() != 0 || getAlarmDateD().getTime() < getWaitingAlarmDateD().getTime()) { //if alarm>time and alarm
//                return getAlarmDateD();
//            }
//        } else if (getWaitingAlarmDateD().getTime() != 0 && getWaitingAlarmDateD().getTime() >= time.getTime()) {
//            return getWaitingAlarmDateD();
//        }
//        return new Date(Long.MAX_VALUE);
////            return null;
//    }
//
//    /**
//     * will set multiple alarms and sort them, so that PARSE_ALARM_DATE always
//     * is the first
//     *
//     * @param alarmDate
//     */
//    public boolean setAdditionalAlarmDateXXX(long alarmDate) {
//        boolean alarmDateAlreadySet = false;
//        if (!has(PARSE_ALARM_DATE)) {
//            //single alarm, set directly
//            setAlarmDate(alarmDate);
//            alarmDateAlreadySet = true;
//        } else {
//            int count = 0;
//            List<Date> list = new ArrayList();
//            String countString = PARSE_ALARM_DATE;
//            //get all previously set alarms
//            while (has(countString)) {
//                Date aDate = getDate(countString);
//                if (aDate.equals(alarmDate)) {
//                    alarmDateAlreadySet = true; //skip if same as alarmDate
//                } else {
//                    list.add(getDate(countString));
//                    count++;
//                }
//                countString = PARSE_ALARM_DATE + "-" + count;
//            }
//            //add new one
//            list.add(new Date(alarmDate));
//            //sort list
//            Collections.sort(list, (date1, date2) -> compareDate(date1, date2));
//            //PUT all alarms now, will overwrite any previous values set for eg PARSE_ALARM_DATE-2 so they are all stored sorted
//            countString = PARSE_ALARM_DATE;
//            for (int i = 0, size = list.size(); i < size; i++) {
//                put(countString, new Date(alarmDate));
//                countString = PARSE_ALARM_DATE + "-" + i;
//            }
//        }
//        return alarmDateAlreadySet;
//    }
//
//    /**
//     * returns list of all set alarms, are always stored sorted
//     *
//     * @return
//     */
//    public List<Date> getAllAlarmDatesXXX() {
//        List<Date> list = new ArrayList();
//        int count = 0;
//        String countString = PARSE_ALARM_DATE;
//        //get all previously set alarms
//        while (has(countString)) {
//            list.add(getDate(countString));
//            count++;
//            countString = PARSE_ALARM_DATE + "-" + count;
//        }
//        return list;
//    }
//
//    /**
//     * remove one (only) alarm with alarmDate. If no alarm set, does nothing. If
//     * multiple alarms with same date exists, removes only the first.
//     *
//     * @return true if an alarmDate was removed, otherwise false
//     */
//    public boolean deleteAlarmDateXXX(long alarmDate) {
//        List<Date> list = new ArrayList();
//        int count = 0;
//        String countString = PARSE_ALARM_DATE;
//        //get all previously set alarms
//        while (has(countString)) {
//            list.add(getDate(countString));
//            count++;
//            countString = PARSE_ALARM_DATE + "-" + count;
//        }
//        //if alarmDate was already set remove it (no need to sort since they are already sorted) and rewrite remaining values
//        if (list.remove(alarmDate)) {
//            remove(PARSE_ALARM_DATE + "-" + (count - 1)); //remove last alarm (so that when rewriting remaining, all old values will be overwritten
//            //PUT all alarms now, will overwrite any previous values set for eg PARSE_ALARM_DATE-2 so they are all stored sorted
//            countString = PARSE_ALARM_DATE;
//            for (int i = 0, size = list.size(); i < size; i++) {
//                put(countString, new Date(alarmDate));
//                countString = PARSE_ALARM_DATE + "-" + i;
//            }
//            return true;
//        }
//        return false;
//    }
//</editor-fold>
    public long getWaitingAlarmDate() {
//        return waitingAlarmDate;
//        Date waitingAlarmDate = getDate(PARSE_WAITING_ALARM_DATE);
//        return (waitingAlarmDate == null) ? 0 : waitingAlarmDate.getTime();
        return getWaitingAlarmDateD().getTime();
    }

    public Date getWaitingAlarmDateD() {
//        return new Date(getWaitingAlarmDate());
        Date waitingAlarmDate = getDate(PARSE_WAITING_ALARM_DATE);
//        return (waitingAlarmDate == null || isDone()) ? new Date(0) : waitingAlarmDate; //return 0 is task is Done
        return waitingAlarmDate == null ? new Date(0) : waitingAlarmDate; //return 0 is task is Done -NO, this prevents seeing alarmDates when editing a done task!! And maybe if copying done tasks. Instead, test on Done must be done at a higher level!!
    }

    public void setWaitingAlarmDate(long waitingAlarmDate) {
        setWaitingAlarmDate(new Date(waitingAlarmDate));
    }

    public void setWaitingAlarmDate(Date waitingAlarmDate) {
//        if (has(PARSE_WAITING_ALARM_DATE) || waitingAlarmDate != 0) {
//            AlarmHandler.getInstance().updateWaitingAlarm(this, new Date(getWaitingAlarmDate()), new Date(waitingAlarmDate));
//            put(PARSE_WAITING_ALARM_DATE, new Date(waitingAlarmDate));
//        }
        Date oldAlarmDate = getWaitingAlarmDateD();

        if (waitingAlarmDate != null && waitingAlarmDate.getTime() != 0) {
            put(PARSE_WAITING_ALARM_DATE, waitingAlarmDate);
        } else {
            remove(PARSE_WAITING_ALARM_DATE);
        }
        if (false) {
            updateNextcomingAlarm(); //done in save() now
        }
        if (!isTemplate() && !oldAlarmDate.equals(waitingAlarmDate)) {
//            afterSaveActions.put("WaitingAlarmDate", () -> AlarmHandler.getInstance().updateWaitingAlarm(this, oldAlarmDate, waitingAlarmDate));
            mustUpdateAlarms = true;
//            AlarmHandler.getInstance().updateAlarmsOrTextForItem(this); //update any existing alarms to the new value
        }

//        this.alarmDate = alarmDate;
//        if (this.waitingAlarmDate != waitingAlarmDate) {
//            this.waitingAlarmDate = waitingAlarmDate;
////            AlarmServer.getInstance().update(this);
//        }
    }

    /**
     * returns the RepeatRule or null if none is defined.
     *
     * @return
     */
    public RepeatRuleParseObject getRepeatRule() {
//        return repeatRule;
        RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) getParseObject(PARSE_REPEAT_RULE);
        repeatRule = (RepeatRuleParseObject) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(repeatRule);
//        return (repeatRule == null) ? new RepeatRuleParseObject() : repeatRule;
//        return repeatRule; //return null if no repeatRule exists
//        return (repeatRule == null) ? new RepeatRuleParseObject() : repeatRule;
//        return (repeatRule == null) ? null : repeatRule;
        return repeatRule;

    }

    /**
     * sets the repeatRule but does NOT alter or update repeat instances. Used
     * when creating new repeat instances.
     *
     * @param repeatRule
     */
    void setRepeatRuleInParse(RepeatRuleParseObject repeatRule) {
        if (repeatRule != null) {
            put(PARSE_REPEAT_RULE, repeatRule);
        } else {
            remove(PARSE_REPEAT_RULE);
        }
    }

    void setRepeatRuleInParseAndSave(RepeatRuleParseObject repeatRule) {
        setRepeatRuleInParse(repeatRule);
        if (repeatRule != null && (repeatRule.isDirty() || repeatRule.getObjectIdP() == null)) {
            if (false&&Config.TEST) { //don't test - normal that repeatRule is not saved (eg when new item+new RR is created)
                ASSERT.that(repeatRule.getObjectIdP() != null, "Item.setRepeatRuleInParse with new unsaved repeatRule=" + repeatRule + ", item=" + this);
            }
            if(false) DAO.getInstance().saveInBackground(repeatRule); //save the (possibly new or changed) repeatRule --> do it in DAO now
        }
    }

    /**
     * sets or updates the repeatRule and ensures that additional or no longer
     * needed repeat instances are created/deleted, as well as deleting an old
     * RR if de-activated
     *
     * @param newRepeatRule a new RR, null, or an edited *copy* of an existing
     * RR
     */
    public void setRepeatRule(RepeatRuleParseObject newRepeatRule) {
        RepeatRuleParseObject oldRepeatRule = getRepeatRule();
        boolean notTemplate = !isTemplate();

        if (oldRepeatRule == null) { //setting a RR for the first time
            if (newRepeatRule != null && newRepeatRule.getRepeatType() != RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) {
//                if (newRepeatRule.isDirty() || newRepeatRule.getObjectIdP() == null)
//                    DAO.getInstance().saveAndWait(newRepeatRule); //must save to get an ObjectId before creating repeat instances (so they can refer to the objId)
                setRepeatRuleInParseAndSave(newRepeatRule); //MUST set repeat rule *before* creating repeat instances in next line to ensure repeatInstance copies point back to the repeatRule
                if (notTemplate) { // newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this, true);
                    opsAfterSubtaskUpdates.add(() -> newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this, true));
                }
            } else {
                //setting null or NO_REPEAT when already null - do nothing
            }
        } else { //oldRepeatRule != null
            if (newRepeatRule == null || newRepeatRule.getRepeatType() == RepeatRuleParseObject.REPEAT_TYPE_NO_REPEAT) { //deleting the existing RR
//                if (oldRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this))
                //                    DAO.getInstance().deleteInBackground(oldRepeatRule); //DONE in deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis (if no references)
                setRepeatRuleInParseAndSave(null);
                opsAfterSubtaskUpdates.add(() -> oldRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this));
            } else { //newRepeatRule != null and possibly modified (eg. click Edit Rule, then Back
                if (!newRepeatRule.equals(oldRepeatRule)) { //do nothing if rule is not edited!!
                    oldRepeatRule.updateToValuesInEditedRepeatRule(newRepeatRule); //update existing rule with updated values
                    if (notTemplate) {
                        opsAfterSubtaskUpdates.add(() -> oldRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this, false)); //
                    }
                    setRepeatRuleInParseAndSave(oldRepeatRule);
//                setRepeatRuleInParse(oldRepeatRule); //must set again to save?? NO, not necessary, only if the *reference* changes in Item
                    DAO.getInstance().saveInBackground(oldRepeatRule); //must save to get an ObjectId before creating repeat instances (so they can refer to the objId)
                }
            }
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setRepeatRuleOLD(RepeatRuleParseObject newRepeatRule) {
////        if (this.repeatRule != repeatRule) {
////            this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
////        }
//        RepeatRuleParseObject oldRepeatRule = getRepeatRule();
//        if (newRepeatRule != null) {
////            if (!isTemplate() && !repeatRule.equals(getRepeatRule())) { //do not activate/update repeat rules for templates
////            repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this, getOwner(), MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : getOwner().size());
////                boolean newRepeatRule = getRepeatRule() == null;
//            setRepeatRuleInParse(newRepeatRule); //MUST set repeat rule *before* creating repeat instances in next line to ensure repeatInstance copies point back to the repeatRule
////                repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//            if (!isTemplate()) { //do not activate/update repeat rules for templates, NOT possible to compare using !repeatRule.equals(getRepeatRule()) since it may the same object
//                if (oldRepeatRule == null) {
////                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreated(this);
//                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//                } else { //if (!oldRepeatRule.equals(newRepeatRule)) {
////                    newRepeatRule.updateRepeatInstancesWhenRuleWasEdited(this);
//                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
//                }
//            } else { //template
//
//            }
//        } else if (oldRepeatRule != null) { //if the user deleted the repeatRule, /* repeatRule == null && */
////            getRepeatRule().delete();
//            getRepeatRule().deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this);
//            setRepeatRuleInParse(newRepeatRule);
//        } //else both old and new RR are null, do nothing
//    }
//</editor-fold>

    @Override
    public void setRepeatRuleForRepeatInstance(RepeatRuleParseObject repeatRule) {
        setRepeatRuleInParseAndSave(repeatRule);
    }

    public long getStartedOnDate() {
//        return startedOnDate;
//        Date date = getDate(PARSE_STARTED_ON_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getStartedOnDateD().getTime();
    }

    protected static Date getSubtasksStartedOnDateD(Item item) {
        List<Item> subtasks = item.getListFull(); //full to include even done/filtered tasks
        long startTime = 0;
        if (subtasks.size() > 0) {
            for (Item it : subtasks) {
//                Date startT = it.getStartedOnDateD();
                Date startT = it.getSubtasksStartedOnDateD(it);
                if (startT != null && startT.getTime() != 0) {
                    startTime = Math.min(startTime, startT.getTime());
                }

            }
//            if (startTime != 0) {
//                return new Date(startTime);
//            } 
        }
//        return null;
        return new Date(startTime);
    }

    public Date getStartedOnDateD() {
        Date date = getStartedOnDateDInParse();
        if (date.getTime() == 0 && MyPrefs.itemProjectPropertiesDerivedFromSubtasks.getBoolean()) { //UI: for projects, startedOn date is the date the first subtask was started (if any), the date can be overwritten by setting any desired date for the mother project
            return getSubtasksStartedOnDateD(this);
        }
        return date;
    }

    private Date getStartedOnDateDInParse() {
//        return new Date(getStartedOnDate());
        Date date = getDate(PARSE_STARTED_ON_DATE);
        return (date == null) ? new Date(0) : date;
    }

    /**
     * startedOnDate is automatically set when status is changed to Ongoing or
     * Completed, or ActualEffort is set to > 0. Once it is set, it cannot be
     * changed to a later date.
     *
     * @param date
     */
    public void setStartedOnDate(long startedOnDate) {
//        if (this.startedOnDate != 0 && this.startedOnDate != date) {
//            this.startedOnDate = date;
//        }
//        if (has(PARSE_STARTED_ON_DATE) || startedOnDate != 0) {
//            put(PARSE_STARTED_ON_DATE, new Date(startedOnDate));
//        }
//        if (startedOnDate != 0) {
//            put(PARSE_STARTED_ON_DATE, new Date(startedOnDate));
//        } else {
//            remove(PARSE_STARTED_ON_DATE);
//        }
        setStartedOnDate(new Date(startedOnDate));
    }

    public void setStartedOnDate(Date startedOnDate) {
        setStartedOnDate(startedOnDate, false);
    }

    public void setStartedOnDate(Date startedOnDate, boolean forceToNewValue) {
        if (forceToNewValue || getStartedOnDate() == 0) { //only set it once, don't overwrite it later
//            if (startedOnDate != null && startedOnDate.getTime() != 0) {
//                put(PARSE_STARTED_ON_DATE, startedOnDate);
////            setStatus(ItemStatus.ONGOING);  //Better move this into the UI
//            } else {
//                remove(PARSE_STARTED_ON_DATE);
////            if (getActualEffort() == 0) { //TODO!!!! this won't work if actualEffort was edited in the UI but not set yet!! Need to ensure this consistency in ScreenItem
////                setStatus(ItemStatus.CREATED);
////            }
//            }
            setStartedOnDateInParse(startedOnDate);
        }

    }

    public void setStartedOnDateInParse(Date startedOnDate) {
        if (false && isProject()) { //subtasks should never inherit their project's start date??
            Date oldProjectDate = getDate(PARSE_STARTED_ON_DATE);
            for (Item subtask : (List<Item>) getList()) {
                Date oldSubtaskDate = subtask.getStartedOnDateD();
//<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
//                if (oldDate == null) {
//                    if (subtaskDate == null) {
//                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
//                    } else {
//                        //do nothing: subtask already had a date set before project got one
//                    }
//                } else { //oldDate!=null - a previous project date was set
//                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
//                        //do nothing (subtask had a defined date already and it was different from old project date)
//                    }
//                }
//                if (oldDate == null && subtaskDate == null) {
//                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
//                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
////oldDate!=null - a previous project date was set
//                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//</editor-fold>
                //above expressions are equivalent to this reduced/simplified version:
                if ((oldProjectDate == null && oldSubtaskDate == null)
                        || oldSubtaskDate == null
                        || oldSubtaskDate.equals(oldProjectDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
                    subtask.setStartedOnDate(startedOnDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
                }
            }
        }
        if (startedOnDate != null && startedOnDate.getTime() != 0) {
//            setStatus(ItemStatus.ONGOING,false, false, false);
            put(PARSE_STARTED_ON_DATE, startedOnDate);
        } else {
            remove(PARSE_STARTED_ON_DATE);
        }
//        update();
    }

//    public void setStartedOnDateUpdateStatus(Date startedOnDate) {
////        setStartedOnDate(startedOnDate.getTime());
//        if (startedOnDate != null && startedOnDate.getTime() != 0) {
//            put(PARSE_STARTED_ON_DATE, startedOnDate);
//            setStatus(ItemStatus.ONGOING);  //Better move this into the UI
//        } else {
//            remove(PARSE_STARTED_ON_DATE);
//            if (getActual() == 0) { //TODO!!!! this won't work if actualEffort was edited in the UI but not set yet!! Need to ensure this consistency in ScreenItem
//                setStatus(ItemStatus.CREATED);
//            }
//        }
//    }
    public long getLastModifiedDate() {
//        return lastModDate;
        Date date = getUpdatedAt();
        return date == null ? 0 : getUpdatedAt().getTime();
    }

    /**
     * automatically set by Parse, not to be used
     *
     * @param lastModDate
     */
//    public void setLastModifiedDatexx(long lastModDate) {
//////        this.lastModDate = val;
////        if (this.lastModDate != val) {
////            this.lastModDate = val;
//////            changed();
////        }
//        if (has("lastModDate") || lastModDate != 0) {
//            put("lastModDate", new Date(lastModDate));
//        }
//    }
    /**
     * implements the user-visible logic of setting a task Waiting, and notably,
     * unsetting the Waiting state
     */
    public void setWaiting(boolean waiting) {
//        this.completed = val;
        if (waiting) {
            setStatus(ItemStatus.WAITING);
//            receiveChangeEvent();
        } else {
//            setStatus(STATUS_CREATED);
//            setReasonableStateAfterWaiting();
            setStatus(ItemStatus.ONGOING); //when reverting from WAITING it is fair to assume that some work has been done on the task, the ONGOING is the right state
//            setWaitingTillDate(0); //reset waitingTill date
//            if (getWaitingAlarmDate() != 0) { //automatically turn off
//                setWaitingAlarmDate(0);
//            }
        }
    }

    /**
     * returns true if this Item is a Project, that is, it has sub-tasks
     */
    public boolean isProject() {
//        return getItemListSize() > 0;
        return getListFull().size() > 0; //does a project stay a project if e.g. all subtasks are filtered or Done? Yes, most probably
        //UI: even if a project is fully done (all subtasks potentially filtered), it stays a project! 
    }

    /**
     * returns true if this Item is a top-level Project, that is, it has
     * sub-tasks but does not belong to another task/project (which means it is
     * a sub-project)
     */
    public boolean isProjectTopLevel() {
        return isProject() && !(getOwner() instanceof Item);
    }

    /**
     * If this Item is a leaf task, then returns this Item if it meets condition
     * AND is different from previousItem.
     *
     * If this Item is a project, returns the first undone (not DONE, not
     * CANCELLED) leaf item *after* previousItem (to be able to continue with a
     * later Item). If this Item is an Item (a leaf task or an interrupt, ie no
     * subtasks) the item itself is returned. Returns first leaf task if
     * previousItem is null.
     *
     * previousItem is also used to ensure that earlier items that were not
     * marked Done are skipped as the Timer progresses through the lists.
     *
     * For this algorithm to work, the previousItem must not disappear (e.g. be
     * deleted) from the list.
     *
     * @param previousItem previous Item (to continue with next leaf item after
     * previousItem), or null if no previous item was selected
     * @param excludeWaiting if true, Item in Waiting state are also excluded
     * from the results
     * @return
     */
//    public Item getNextLeafItem(Item previousItem, boolean excludeWaiting) {
    public Item getNextLeafItem(Item previousItem) {
//        return getNextLeafItemMeetingConditionImpl(previousItem, excludeWaiting, false);
        List<Item> leafTaskList = getLeafTasksAsList(null);

        int nextIndex;
        if (previousItem == null) {
            nextIndex = 0;
        } else {
            nextIndex = leafTaskList.indexOf(previousItem) + 1;
        }
        if (nextIndex >= 0 && nextIndex < leafTaskList.size()) { //prevIndex<list.size()-1 => there is at least one item after the previous one
            return leafTaskList.get(nextIndex);
        }
        return null;
    }

    /**
     * this won't work if previousItem doesn't meet condition since it will then
     * be excluded from the leaf list
     *
     * @param previousItem
     * @param condition
     * @return
     */
    public Item getNextLeafItem(Item previousItem, Condition condition) {
//        return getNextLeafItemMeetingConditionImpl(previousItem, excludeWaiting, false);
//        return getNextLeafItemMeetingConditionImpl(previousItem, condition, new boolean[]{previousItem == null});
        return getNextLeafItemMeetingConditionImpl(previousItem, condition, new Boolean(previousItem == null));

    }

    interface Condition {

        boolean meets(Item item);
    }

    /**
     * will return first subtask if previousItem is null
     *
     * @param previousItem
     * @param condition
     * @param previousItemAlreadyFound must be set true if previousItem==null!!
     * (optimization)
     * @return
     */
//    private Item getNextLeafItemMeetingConditionImpl(Item previousItem, boolean excludeWaiting, boolean previousItemAlreadyFound) {
//    Item getNextLeafItemMeetingConditionImpl(Item previousItem, Condition condition, boolean[] previousItemAlreadyFound) {
    Item getNextLeafItemMeetingConditionImpl(Item previousItem, Condition condition, Boolean previousItemAlreadyFound) {
        List<Item> leafSubtaskList = getLeafTasksAsList(condition);
        int index = leafSubtaskList.indexOf(previousItem);
        if (previousItem == null && leafSubtaskList.size() > 0) { //if called w null, return first item (if any)
            return leafSubtaskList.get(0);
        } else if (index >= 0 && index + 1 < leafSubtaskList.size()) {
            return leafSubtaskList.get(index + 1);
        } else {
            return null;
        }
    }

    /**
     * old implementation which doesn't construct a full list of leaf tasks each
     * time (and doesn't check condition on each subtask)
     *
     * @param previousItem
     * @param condition
     * @param previousItemAlreadyFound
     * @return
     */
    Item getNextLeafItemMeetingConditionImplOLDButOptimized(Item previousItem, Condition condition, Boolean previousItemAlreadyFound) {
//        previousItemAlreadyFound[0] = previousItem==null;
//        assert previousItem != null || previousItemAlreadyFound[0] : "getNextLeafItemMeetingConditionImpl called with previousItem==null and previousItemAlreadyFound not set true";
        assert previousItem != null || previousItemAlreadyFound : "getNextUndoneLeafItemImpl called with previousItem==null and previousItemAlreadyFound not set true";
//        if (itemList == null || itemList.size() == 0) {
        if (!isProject()) { //LEAF
            //if no subtasks, previousItem found or null, and item meets the condition, then return the Item itself
//            if ((previousItem == null || previousItemAlreadyFound[0]) && condition.meets(this)) {
//            if (previousItemAlreadyFound[0] && condition.meets(this)) {
            if (this.equals(previousItem)) {
//                previousItemAlreadyFound[0] = true;
                previousItemAlreadyFound = true;
            }
            if (condition.meets(this) && (previousItem == null || !previousItem.equals(this))) { //previousItem!=null => !previousItem.equals(this)
//                previousItemAlreadyFound[0] = true; //NO NEED since this is a single task
                return this;
            } else {
                return null;
            }
        } else {
            //Project
            List itemList = getListFull();
//            boolean previousItemFoundHere = (previousItem == null); //set to true if previousItem is null
            for (int i = 0, size = itemList.size(); i < size; i++) {
                Item subTask = (Item) itemList.get(i);
                if (subTask.isProject()) {
                    //try to find an appropriate subtask to this project
//                    subTask = subTask.getNextLeafItemMeetingConditionImpl(previousItem, condition, previousItemFoundHere || previousItemAlreadyFound[0]);
                    subTask = subTask.getNextLeafItemMeetingConditionImplOLDButOptimized(previousItem, condition, previousItemAlreadyFound);
//                    if (item != null && previousItemFoundHere) {
                    //if a subtask meeting the conditions is found, return it
                    if (subTask != null) {
                        return subTask;
                    }
                    //else continue with next subtask
                } else //                    previousItemFoundHere = previousItemFoundHere || (previousItem != null && subTask.equals(previousItem));
                //                    previousItemAlreadyFound[0] = previousItemAlreadyFound[0] || (previousItem != null && subTask.equals(previousItem)); //set true when found and keep true
                //                if (!(previousItemAlreadyFound[0])) {
                if (!(previousItemAlreadyFound)) {
//                    previousItemAlreadyFound[0] = (previousItem != null && subTask.equals(previousItem)); //set true when found and keep true
//                    previousItemAlreadyFound[0] = (subTask.equals(previousItem)); //set true when found and keep true
                    previousItemAlreadyFound = (subTask.equals(previousItem)); //set true when found and keep true
//                    if (!(previousItemAlreadyFound || previousItemFoundHere)) {
                    continue; //as long as we've not found the previous item, skip to next item
                } else if (condition.meets(subTask)) {
                    return subTask;
                }
            }
        }
        return null; //item; //if we get to here, it means no suitable item was found
    }

    /**
     * returns a sequential list of all leaf tasks that meet the Condition,
     * starting with the first subtask of the first subtask etc.
     *
     * @param condition condition or null (will match all items)
     * @return null if no matching items
     */
//    List<ItemAndListCommonInterface> getLeafTasksAsList(Condition condition) {
    List<Item> getLeafTasksAsList(Condition condition) {
//        assert previousItem != null || previousItemAlreadyFound[0] : "getNextLeafItemMeetingConditionImpl called with previousItem==null and previousItemAlreadyFound not set true";
        if (!isProject()) { //LEAF
            if (condition == null || condition.meets(this)) {
                return new ArrayList(Arrays.asList(this));// list.add(this);
            } else {
                return new ArrayList(); //null; //always return a list (no null pointers)
            }
        } else { //Project
            List<Item> subtasks = getListFull(); //full to ensure every subtask matching condition is returned
            List<Item> leaftasks;
            int size = subtasks.size();
            List<Item> result = null;
            if (size > 0) {
                result = new ArrayList();
                for (int i = 0; i < size; i++) {
                    leaftasks = subtasks.get(i).getLeafTasksAsList(condition);
                    result.addAll(leaftasks);
                }
            }
            return result;
        }
    }

    /**
     * returns the number of Items that are not done. If the list contains
     * something else than Items, returns zero
     *
     * @param includeSubTasks
     * @return
     */
//    public int getNumberOfUndoneItems() {
////        return getNumberOfSubItemsWithStatus(false, ItemStatus.STATUS_DONE, true);
//        return isDone() ? 0 : 1;
//    }
    @Override
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
//        return ItemList.getNumberOfUndoneItems(getList(), false); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
//        int count = isProject() ? 0 : (isDone() ? 0 : 1); //don't count a project, only it's subtasks
//        if (isProject()) {
////            count += ItemList.getNumberOfUndoneItems(getList(), true); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
//            return ItemList.getNumberOfUndoneItems(getList(), includeSubTasks); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
//        } else {
//            return isDone() ? 0 : 1;
//        }
        return ItemList.getNumberOfUndoneItems(getListFull(), includeSubTasks); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
    }

    @Override
    public int getNumberOfItems(boolean onlyUndone, boolean countLeafTasks) {
        List list = getListFull();
        if (list.size() == 0) {
            return onlyUndone && isDone() ? 0 : 1; //this is a leafTask, so always return 1 if undone
        }
        int count = 0;
        for (Object obj : list) {
            count += ((ItemAndListCommonInterface) obj).getNumberOfItems(onlyUndone, countLeafTasks);
        }
        //!(onlyUndone && isDone()) <=> !onlyUndone || !isDone()
        return count + (countLeafTasks ? 0 : (onlyUndone && isDone() ? 0 : 1)); //if !countLeafTasks, then add 1 for this Item
    }

    @Override
    public int getNumberOfSubtasks(boolean onlyUndone, boolean countLeafTasks) {
        int count = 0;
        for (Object obj : getListFull()) { //full: count every subtask
            count += ((ItemAndListCommonInterface) obj).getNumberOfItems(onlyUndone, countLeafTasks);
        }
        return count; //if !countLeafTasks, then add 1 for this Item
    }

    /**
     * returns the appropriate value of status when actual is being changed -
     * used in the UI to update status when actual is changing. If no reason to
     * change status, returns newStatus (already set status => no change)
     *
     * @param orgItemActualMillis old value (item value before editing) //TODO:
     * not used
     * @param editedActualMillis new value (possibly manually edited by user)
     * @param orgItemStatus old value (item value before editing) //TODO: not
     * used
     * @param editedStatus new value (possibly manually edited by user)
     * @return
     */
    static public ItemStatus updateStatusOnActualChange(long orgItemActualMillis, long editedActualMillis, ItemStatus orgItemStatus, ItemStatus editedStatus, boolean areAnySubtasksOngoing) {
//        if (orgItemActualMillis == editedActualMillis) {
//            return editedStatus; //return if no change
//        } else 
        if (editedActualMillis > 0) { //if user has changed actual
            return ItemStatus.ONGOING;
        } else {//if (orgItemStatus == ItemStatus.ONGOING && editedStatus == orgItemStatus) {
            if (areAnySubtasksOngoing) //if some subtasks are done
            {
                return ItemStatus.ONGOING;
            } else {
                return ItemStatus.CREATED; //if setting actual to 0, set status back to Created
            }
        }
    }

//    static public ItemStatus updateStatusOnActualChangeXXX(long oldActualMillis, long newActualMillis, ItemStatus oldStatus, ItemStatus newStatus, boolean areAnySubtasksOngoing) {
//        if (oldActualMillis == newActualMillis) {
//            return oldStatus; //return if no change
//        } else if (areAnySubtasksOngoing) { //if some subtasks are done
//            return ItemStatus.ONGOING;
//        } else if (newActualMillis > 0) { //if user has changed actual
//            if (oldStatus == ItemStatus.CREATED /*don't change WAITING/CANCELLED/ONGOING*/
//                    && (newStatus == oldStatus/*status not manually changed*/
//                    || oldStatus == ItemStatus.ONGOING /*setting back to existing value*/)) {
//                return ItemStatus.ONGOING;
//            } //else 
//        } else if (oldStatus == ItemStatus.ONGOING && newStatus == oldStatus) {
//            return ItemStatus.CREATED; //if setting actual to 0, set status back to Created
//        }
//        return newStatus; //if no need to change status, just return already set value
//    }
    /**
     * returns true if the status of this task should be changed to the
     * newStatus. Used to count the number of subtasks that may have their
     * status changed when changing the status of the mother task/project, or
     * task-list. Old -> New: Created/Ongoing/Waiting -> Done; Created/Waiting
     * -> Ongoing; Created/Ongoing -> Waiting; Created/Ongoing/Waiting ->
     * Cancelled [don't cancel Done tasks]; Ongoing/Waiting/Done -> Created
     *
     * when all tasks are Done/Cancelled, and newStatus is
     * Created/Ongoing/Waiting, the transitions are different. Only Cancelled
     * tasks will remain cancelled. Other tasks will return to Created (if no
     * Actual), Ongoing (if Actual), or Waiting (if Actual)??
     *
     *
     * @param newStatus
     * @param oldStatus
     * @return
     */
//    public static boolean changeSubtaskStatus(int newStatus, int oldStatus) {
    public static boolean shouldTaskStatusChange(ItemStatus newStatus, ItemStatus oldStatus) {
        return shouldTaskStatusChange(newStatus, oldStatus, false);
    }

    /**
     *
     * @param newStatus
     * @param oldStatus
     * @param changingFromDone changing all subtasks of a Done project back to
     * some other state is special since CANCELLED tasks should remain cancelled
     * @return
     */
    public static boolean shouldTaskStatusChange(ItemStatus newStatus, ItemStatus oldStatus, boolean changingFromDone) {
        if (changingFromDone) {
            return ((newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.WAITING || newStatus == ItemStatus.CREATED)
                    && (oldStatus != ItemStatus.CANCELLED)); //if project is DONE, then all tasks are either CANCELLED or DONE, so
        } else {
            return (newStatus == ItemStatus.DONE && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING)) //NOT: DONE, CANCELLED
                    //                || (newStatus == ItemStatus.ONGOING && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.WAITING)) //NOT: ONGOING, DONE, CANCELLED
                    || (newStatus == ItemStatus.ONGOING && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.WAITING)) //NOT: ONGOING, DONE, CANCELLED
                    || (newStatus == ItemStatus.WAITING && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING)) //NOT: WAITING, DONE, CANCELLED
                    || (newStatus == ItemStatus.CANCELLED && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING)) //NOT: DONE, CANCELLED
                    || (newStatus == ItemStatus.CREATED && (oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING || oldStatus == ItemStatus.DONE));  //NOT: CANCELLED
        }
    }

    /**
     * when all tasks are Done/Cancelled, and newStatus is
     * Created/Ongoing/Waiting, the transitions are different. Only Cancelled
     * tasks will remain cancelled. Other tasks will return to Created (if no
     * Actual), Ongoing (if Actual), or Waiting (if Actual)??
     *
     * @param newStatus
     * @param oldStatus
     * @return
     */
//    public static boolean shouldSubtaskStatusChangeWhenMarkingUndone(ItemStatus newStatus, ItemStatus oldStatus) {
//        return ((newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.WAITING || newStatus == ItemStatus.CREATED)
//                && (oldStatus != ItemStatus.CANCELLED));
//    }
//    public boolean shouldTaskStatusChange(ItemStatus newStatus) {
//        return shouldTaskStatusChange(newStatus, getStatus());
//    }
    @Override
    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus, boolean changingFromDone) {
        List list = getListFull();
        if (list == null || list.size() == 0) {
            return shouldTaskStatusChange(newStatus, getStatus()) ? 1 : 0;
        } else {
            return ItemList.getNumberOfItemsThatWillChangeStatus(list, recurse, newStatus, changingFromDone);
        }
    }

    @Override
    public int getCountOfSubtasksWithStatus(boolean recurse, List statuses) {
        List list = getListFull();
        if (list == null || list.size() == 0) {
            return statuses.indexOf(getStatus()) >= 0 ? 1 : 0;
        } else {
            return ItemList.getCountOfSubtasksWithStatus(list, recurse, statuses);
        }
    }

    /**
     * actually updates the saved value of status
     *
     * @param newStatus
     */
    private void setStatusInParse(ItemStatus newStatus) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        setStatusInParse(newStatus, true);
//    }
//
//    private void setStatusInParse(ItemStatus newStatus, boolean updateSubAndSuperTasks) {
//        put(PARSE_STATUS, newStatus);
//        put(PARSE_STATUS, newStatus.toString());
//        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmationXXX.getBoolean(), true);
//        if (getRepeatRule() != null && newStatus == ItemStatus.DONE && newStatus == ItemStatus.CANCELLED) { //update repeats on DONE/CANCEL
//            getRepeatRule().updateRepeatInstancesWhenItemIsDoneOrCancelled(this, getOwner(), MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : getOwner().size());
//        }
//        if (newStatus != null && newStatus != ItemStatus.CREATED) { //MUST store every status incl. CREATED to be able to query on it
////            put(PARSE_CATEGORIES, new ArrayList(categories));
//            put(PARSE_STATUS, newStatus.toString());
//        } else { //categories == null || categories.isEmpty()
//            remove(PARSE_STATUS);
//        }
//        if (isProject()) {
//            ItemStatus oldStatus = getStatusFromParse();
//            for (Item subtask : (List<Item>) getList()) {
//                ItemStatus subtaskStatus = subtask.getStatus();
////<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
////                if (oldDate == null) {
////                    if (subtaskDate == null) {
////                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                    } else {
////                        //do nothing: subtask already had a date set before project got one
////                    }
////                } else { //oldDate!=null - a previous project date was set
////                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
////                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
////                        //do nothing (subtask had a defined date already and it was different from old project date)
////                    }
////                }
////                if (oldDate == null && subtaskDate == null) {
////                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
//////oldDate!=null - a previous project date was set
////                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                }
////</editor-fold>
//                //above expressions are equivalent to this reduced/simplified version:
//                if ((oldStatus == null && subtaskStatus == ItemStatus.CREATED) || (subtaskStatus == ItemStatus.CREATED || subtaskStatus.equals(oldStatus))) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                    subtask.setStartedOnDate(startedOnDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//            }
//        }
//</editor-fold>
        if (Config.TEST) {
            ASSERT.that(newStatus != null, "status should never be reset to CREATED by storing a null status");
        }
        if (newStatus != null) {
            put(PARSE_STATUS, newStatus.toString());
        } else {
            remove(PARSE_STATUS);
        }
//        update();
    }

    private static void updateFieldsDependingOnStatus(Item item, ItemStatus previousStatus, ItemStatus newStatus, Date now) {
        //<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (forceSubtasksToStatus && getItemListSize() > 0) {
////                    if (askConfirmation) {
////                        int nbSubTasksDiffFromStatus = countTasksWhereStatusWillBeChanged(newStatus, forceSubtasksToStatus);
////                        int nbSubtasksToChangeWithoutConfirmation = Settings.getInstance().nbSubtasksToChangeStatusWithoutConfirmation.getInt();
////                        if (nbSubTasksDiffFromStatus > 0 && (nbSubTasksDiffFromStatus <= nbSubtasksToChangeWithoutConfirmation
////                                || (Dialog.show("Set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status) + "?",
////                                        "Are you sure you want to set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status)
////                                        + "?\nThis cannot be undone.", "OK", "Cancel")))) {
////                            getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                        }
////                    } else { //no need to ask for confirmation
////                        getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                    }
//////            if (obj instanceof BaseItemOrList) {
//////                ((BaseItemOrList) obj).setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                    obj.setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//////            }
////                }
////</editor-fold>
//            } else {
////                    public void setStatusImpl(boolean topLevelTask, Item.ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
////                        for (int i = 0, size = getSize(); i < size; i++) {
////                ItemList subtasks = getItemList();
//                List subtasks = getList();
//                for (Object itemOrList : subtasks) {
//                    ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
//                }
//            }
//</editor-fold>
        //StartedOnDate SET:
        if (item.getStartedOnDate() == 0 && (newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.DONE)) {//|| newStatus == ItemStatus.WAITING)) { UI: setting Waiting => task was started?! No, not really
//                setStartedOnDate(MyDate.getNow());
//            setStartedOnDate(System.currentTimeMillis());
            item.setStartedOnDate(now);
        }
        //UI: StartedOnDate RESET: not relevant, always keep the first set StartedOnDate (unless manually deleted)

        //CompletedDate: SET set to Now if changing to Done/Cancelled from other state
        //UI: also use Completed date to store date when task was cancelled (for historical data)
        if (item.getCompletedDate() == 0 && (previousStatus != ItemStatus.DONE && previousStatus != ItemStatus.CANCELLED)
                && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
            //TODO!!!! should actual effort be reduced to zero?? No, any effort spend should be kept even for Cancelled tasks
            item.setCompletedDate(now); //UI: also use Completed date to store date when task was cancelled (for historical data)
//            if (item.getRepeatRule() != null) { //should NOT only be done if we update dependent fields, but in every case!!
//                item.getRepeatRule().updateRepeatInstancesOnDoneCancelOrDelete(item);
//            }
        }
        //CompletedDate: RESET if changing from Done/Cancelled to other state
        //CompletedDate: set if changing to Done/Cancelled from other state, set to Now if changing to Done/Cancelled
        if (item.getCompletedDate() != 0 && (previousStatus == ItemStatus.DONE || previousStatus == ItemStatus.CANCELLED)
                && (newStatus != ItemStatus.DONE && newStatus != ItemStatus.CANCELLED)) {
            //if item changes from Done/Cancelled to some other value, then reset CompletedDate
            item.setCompletedDate(0L);
        }

        //WaitingActivatedDate:
        if (previousStatus != ItemStatus.WAITING && newStatus == ItemStatus.WAITING) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
            item.setDateWhenSetWaiting(now); //always save
        }
//<editor-fold defaultstate="collapsed" desc="deactivated updates">
        if (false && (previousStatus == ItemStatus.WAITING && newStatus != ItemStatus.WAITING && item.getWaitingTillDateD().getTime() != 0L)) { //reset WaitingTillDate
            //UI: KEEP setWaitingDate as a marker the task was set waiting sometime and to keep the date WHEN it was said waiting. TODO: must never use waitingDate!=0 as indication task is waiting
            item.setWaitingTillDate(0); //reset waitingTill date
            if (item.getWaitingAlarmDateD().getTime() != 0) { //automatically turn off
                item.setWaitingAlarmDate(null);
            }
        }
//RemainingEffort: NO reason to delete remaining effort because a task is cancelled or Done
//reset Alarms for Done/Cancelled tasks //TODO shouldn't be necessary to reset alarmDate when using Parse to find relevant next alarmdate
        if (false && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
            item.setAlarmDate(0); //Cancel any set alarms 
//TODO: to support reverting when a task is marked Done, the alarm time should be kept, but not activated (AlarmServer should ignore alarms for Done tasks)
//UI: if a Done task is set undone, any old future alarms should be re-actviated in save()
        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//reset any running timers //NO, done in SCreentTimer now
//            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
////                TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: new way of cheking if timer is running for done item
//            }
//</editor-fold>
    }

    /**
     *
     * @param topLevelTask
     * @param newStatus
     * @param askConfirmation
     * @param forceSubtasksToStatus
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setStatusImpl(boolean topLevelTask, ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
//    public void setStatusImpl(boolean topLevelTask, ItemStatus newStatus) {
//    public void setStatusImplXXX(ItemStatus newStatus) {
//        ItemStatus previousStatus = getStatus();
//        if (previousStatus != newStatus) { //if status has receiveChangeEvent:
////<editor-fold defaultstate="collapsed" desc="comment">
////            ItemStatus oldStatus = this.status;
////            ItemStatus oldStatus = getStatus();
////            if (topLevelTask || shouldTaskStatusChange(newStatus, previousStatus, topLevelTask && previousStatus == ItemStatus.DONE)) {
////</editor-fold>
//            if (shouldTaskStatusChange(newStatus, previousStatus, previousStatus == ItemStatus.DONE)) {
////                status = newStatus;
//                if (newStatus == ItemStatus.CREATED && getActualEffort() > 0) {
//                    setStatusInParse(ItemStatus.ONGOING);
//                } else { //if (newStatus == ItemStatus.ONGOING && getActualEffort() > 0) {
//                    setStatusInParse(newStatus);
//                }
//            }
//            updateFieldsDependingOnStatus(previousStatus, newStatus, new Date()); //new Date() =>> use exact time for all date updates
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setStatusImplOLD(boolean topLevelTask, ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
//        ItemStatus previousStatus = getStatus();
//        if (previousStatus != newStatus) { //if status has receiveChangeEvent:
////            ItemStatus oldStatus = this.status;
////            ItemStatus oldStatus = getStatus();
//            if (topLevelTask || shouldTaskStatusChange(newStatus, previousStatus)) {
////                status = newStatus;
//                setStatusSaveValue(newStatus);
//            }
//            if (false) {
////                if (forceSubtasksToStatus && getItemListSize() > 0) {
////                    if (askConfirmation) {
////                        int nbSubTasksDiffFromStatus = countTasksWhereStatusWillBeChanged(newStatus, forceSubtasksToStatus);
////                        int nbSubtasksToChangeWithoutConfirmation = Settings.getInstance().nbSubtasksToChangeStatusWithoutConfirmation.getInt();
////                        if (nbSubTasksDiffFromStatus > 0 && (nbSubTasksDiffFromStatus <= nbSubtasksToChangeWithoutConfirmation
////                                || (Dialog.show("Set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status) + "?",
////                                        "Are you sure you want to set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status)
////                                        + "?\nThis cannot be undone.", "OK", "Cancel")))) {
////                            getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                        }
////                    } else { //no need to ask for confirmation
////                        getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                    }
//////            if (obj instanceof BaseItemOrList) {
//////                ((BaseItemOrList) obj).setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////                    obj.setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//////            }
////                }
//            } else {
////                    public void setStatusImpl(boolean topLevelTask, Item.ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
////                        for (int i = 0, size = getSize(); i < size; i++) {
////                ItemList subtasks = getItemList();
//                List subtasks = getList();
//                for (Object itemOrList : subtasks) {
//                    ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
//                }
//            }
//
//            //StartedOnDate SET:
//            if (getStartedOnDate() == 0 && (newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.DONE)) {
////                setStartedOnDate(MyDate.getNow());
//                setStartedOnDate(System.currentTimeMillis());
//            }
//            //StartedOnDate RESET: not relevant, always keep the first set StartedOnDate
//
//            //CompletedDate: SET set to Now if changing to Done/Cancelled from other state
//            if ((previousStatus != ItemStatus.DONE && previousStatus != ItemStatus.CANCELLED)
//                    && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
//                //TODO!!!! should actual effort be reduced to zero?? No, any effort spend should be kept even for Cancelled tasks
////                setCompletedDate(MyDate.getNow()); //UI: also use Completed date to store date when task was cancelled (for historical data)
//                setCompletedDate(System.currentTimeMillis()); //UI: also use Completed date to store date when task was cancelled (for historical data)
//            }
//            //CompletedDate: RESET if changing from Done/Cancelled to other state
//            //CompletedDate: set if changing to Done/Cancelled from other state, set to Now if changing to Done/Cancelled
//            if (getCompletedDate() != 0 && (previousStatus == ItemStatus.DONE || previousStatus == ItemStatus.CANCELLED)
//                    && (newStatus != ItemStatus.DONE && newStatus != ItemStatus.CANCELLED)) {
//                //if item changes from Done/Cancelled to some other value, then reset CompletedDate
//                setCompletedDate(0L);
//            }
//
//            //WaitingActivatedDate:
//            if (previousStatus != ItemStatus.WAITING && newStatus == ItemStatus.WAITING /*
//                     * && getWaitingLastActivatedDate() == 0L
//                     */) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
////                setDateWhenSetWaiting(MyDate.getNow()); //always save
//                setDateWhenSetWaiting(System.currentTimeMillis()); //always save
//            } else if (previousStatus == ItemStatus.WAITING && newStatus != ItemStatus.WAITING /*
//                     * && getWaitingLastActivatedDate() == 0L
//                     */) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
////                setDateWhenSetWaiting(MyDate.getNow()); //always save
//                setDateWhenSetWaiting(0); //always save
//            }
//            if (previousStatus == ItemStatus.WAITING && newStatus != ItemStatus.WAITING && getWaitingTillDateD().getTime() != 0L) { //reset WaitingTillDate
//                setWaitingTillDate(0); //reset waitingTill date
//                if (getWaitingAlarmDate() != 0) { //automatically turn off
//                    setWaitingAlarmDate(0);
//                }
////                setWaitingLastActivatedDate(0); //-waitingActivateDate is not changed (until the task is possibly set waiting again)
//            }
//
//            //RemainingEffort: set to zero for Done/Cancelled tasks
//            if (newStatus == ItemStatus.DONE) {// || newStatus == ItemStatus.STATUS_CANCELLED) { //NO reason to delete remaining effort because a task is cancelled
//                setRemainingEffortXXX(0L); //reset Remaining when marked done
//            }
//
//            //reset Alarms for Done/Cancelled tasks
//            //TODO shouldn't be necessary to reset alarmDate when using Parse to find relevant next alarmdate
//            if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
//                setAlarmDate(0); //Cancel any set alarms //TODO: to support reverting when a task is marked Done, the alarm time should be kept, but not activated (AlarmServer should ignore alarms for Done tasks)
//            }
//
//            //reset any running timers //NO, done in SCreentTimer now
////            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
//////                TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: new way of cheking if timer is running for done item
////            }
//        }
//    }
//</editor-fold>
    protected void updateStatusOnSubtaskStatusChange(Item subtask, ItemStatus oldStatus, ItemStatus newStatus, Date now) {
        ItemStatus subtaskStatus = getStatusFromSubtasks();
        setStatus(subtaskStatus, false, true, true, now);
    }

    /**
     * sets the status of the item and any subtasks. Makes other necessary
     * updates, such as stopping timers, cancelling alarms, setting completed
     * date.
     *
     * @param newStatus
     */
    @Override
    public void setStatus(ItemStatus newStatus) {
        setStatus(newStatus, true, true, true, new MyDate());
    }

    public void setStatus(ItemStatus newStatus, boolean updateDependentFields) {
        setStatus(newStatus, true, true, updateDependentFields, new MyDate());
    }

    /**
     *
     * @param newStatus
     * @param updateSubtasks propagate status change to subtasks
     * @param updateSupertasks update owner task if status for this task changes
     * @param updateDependentFields false if fields are set explicitly as in
     * ScreenItemEdit
     * @param now reference time to ensure all fields get exactly the same time
     * (using System time would create some micro seconds different <=> not
     * exactly the same time)
     */
//    public void setStatusOLD(ItemStatus newStatus, boolean updateSubtasks, boolean updateSupertasks, boolean updateDependentFields, Date now) {
//        ItemStatus oldStatus = getStatusFromParse();
//        if (newStatus == oldStatus) {
//            return;
//        }
//
//        if (newStatus == ItemStatus.CREATED && getActual() > 0) {  //convert CREATED to ONGOING if actual effort is recorded
//            newStatus = ItemStatus.ONGOING;
//        }
//
//        //if setting Waiting, ask if set 
//        if (false && newStatus == ItemStatus.WAITING && oldStatus != ItemStatus.WAITING) {
//            MyForm.dialogSetWaitingDateAndAlarm(this); //only call if we're changing TO Waiting status
//        }
//        //if setting Done, ask if set actual
//        if (false && newStatus == ItemStatus.WAITING && oldStatus != ItemStatus.WAITING) { //don't activate here, activate in UI part
//            MyForm.dialogUpdateActualTime(this); //only call if we're changing TO Waiting status
//        }
//
//        if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
//            mustUpdateAlarms = true; //update alarms
//        }
//
//        if (updateSubtasks && isProject()) {
//            //when changing the status of a project, only the status of the subtasks are changed(??)
////            ItemStatus oldStatus = getStatus();
////            if (newStatus != oldStatus) { //only check how many subtasks may be impacted if overall status is being changed
//            boolean doneProject = (oldStatus == ItemStatus.DONE);
////            int nbUndone = getNumberOfUndoneItems(true);
//            int nbChgStatus = getNumberOfItemsThatWillChangeStatus(true, newStatus, doneProject);
//            if (nbChgStatus <= MyPrefs.itemMaxNbSubTasksToChangeStatusForWithoutConfirmation.getInt()
//                    //                    || Dialog.show("INFO", "Changing status to " + newStatus.getDescription() + " for " + nbChgStatus + " subtasks", "OK", "Cancel")) {
//                    || Dialog.show("INFO", "Change " + nbChgStatus + " subtasks to " + newStatus.getDescription(), "OK", "Cancel")) {
//                List subtasksToSave = new ArrayList();
////                    List<Item> subtasks = getList();s
////                    for (int i = 0, size = subtasks.size(); i < size; i++) {
//                for (Item item : (List<Item>) getListFull()) {
////                    if (!subtasks.get(i).isDone()) {
////                        ItemStatus oldSubtaskStatus = subtasks.get(i).getStatus();
//                    ItemStatus oldSubtaskStatus = item.getStatus();
//                    if (shouldTaskStatusChange(newStatus, oldSubtaskStatus, doneProject)) { //only change status when transition is allowed
////                            Item item = subtasks.get(i);
//                        item.setStatus(newStatus, true, false, true, now); //always update dependent fields for subtasks
//                        subtasksToSave.add(item);
////                            DAO.getInstance().saveInBackgroundSequential(item);
//                    }
//                }
////                DAO.getInstance().saveInBackgroundSequential(subtasksToSave);
//                DAO.getInstance().saveInBackground(subtasksToSave);
//            } else
//                return; //UI: do nothing it user does not want to change all subtasks!
////            }
//        }
//
//        setStatusInParse(newStatus); //must set *before* updating supertasks
//
//        if (updateDependentFields) {
//            updateFieldsDependingOnStatus(this, oldStatus, newStatus, now);
//        }
//
//        if (updateSupertasks) {
//            Item owner = getOwnerItem();
//            if (owner != null) {// && owner.getStatus() != newStatus) {
//                owner.updateStatusOnSubtaskStatusChange(this, oldStatus, newStatus, now);
////                owner.setStatus(newStatus, false, true, now);
//                DAO.getInstance().saveInBackground(owner);
//            }
//        }
//
////        setStatusInParse(newStatus);
////        else {
////<editor-fold defaultstate="collapsed" desc="comment">
////        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmationXXX.getBoolean(), true);
////        setStatusImpl(true, newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmationXXX.getBoolean(), !Settings.getInstance().neverChangeProjectsSubtasksWhenChangingProjectStatus.getBoolean());
////            setStatusImpl(true, newStatus, !MyPrefs.changeSubtasksStatusWithoutConfirmationXXX.getBoolean(), MyPrefs.neverChangeProjectsSubtasksWhenChangingProjectStatusXXX.getBoolean());
////            setStatusImpl(true, newStatus);
////</editor-fold>
////        setStatusImpl(newStatus);
////        }
//    }
    /**
     * returns true if OK to update status of subtasks (based on new status for
     * mother task). Returns true if either the number of impacted subtasks is
     * below the limit defined by the settings or if the user confirms that
     * change if above the settings limit.
     *
     * @param oldStatus
     * @param newStatus
     * @return
     */
    public boolean confirmUpdateOfSubtasks(ItemStatus oldStatus, ItemStatus newStatus) {
        boolean doneProject = (oldStatus == ItemStatus.DONE);
        int nbChgStatus = getNumberOfItemsThatWillChangeStatus(true, newStatus, doneProject);
        return (nbChgStatus <= MyPrefs.itemMaxNbSubTasksToChangeStatusForWithoutConfirmation.getInt()
                || Dialog.show("INFO", "Change " + nbChgStatus + " subtasks to " + newStatus.getDescription() + "?", "OK", "Cancel"));
    }

    public void setStatus(final ItemStatus status, boolean updateSubtasks, boolean updateSupertasks, boolean updateDependentFields, Date now) {
        ItemStatus oldStatus = getStatusFromParse();

        ItemStatus newStatus = status == ItemStatus.CREATED && getActual() > 0 ? ItemStatus.ONGOING : status; //convert CREATED to ONGOING if actual effort is recorded
        if (newStatus == oldStatus) {
            return;
        }

        if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
            mustUpdateAlarms = true; //update alarms
        }

        if (updateSubtasks && isProject()) {
            //when changing the status of a project, only the status of the subtasks are changed(??)
//            boolean doneProject = (oldStatus == ItemStatus.DONE);
//            int nbChgStatus = getNumberOfItemsThatWillChangeStatus(true, newStatus, doneProject);
//            if (nbChgStatus <= MyPrefs.itemMaxNbSubTasksToChangeStatusForWithoutConfirmation.getInt()
//                    || Dialog.show("INFO", "Change " + nbChgStatus + " subtasks to " + newStatus.getDescription() + "?", "OK", "Cancel")) {
            opsToUpdateSubtasks.add((subtask) -> {
                ItemStatus oldSubtaskStatus = subtask.getStatus();
                if (shouldTaskStatusChange(newStatus, oldSubtaskStatus, oldStatus == ItemStatus.DONE)) { //only change status when transition is allowed
                    subtask.setStatus(newStatus, true, false, true, now); //always update dependent fields for subtasks
                    return true;
                } else {
                    return false; //UI: do nothing it user does not want to change all subtasks!
                }
            });
            //UI: else do nothing it user does not want to change all subtasks!
//            }
        }

        setStatusInParse(newStatus); //must set *before* updating supertasks

        if (updateDependentFields) { //must call this *before* creating repeat instances to e.g. set CompletedDate for repeatFromCompleted
            updateFieldsDependingOnStatus(this, oldStatus, newStatus, now);
        }

        if ((oldStatus != ItemStatus.DONE && oldStatus != ItemStatus.CANCELLED)
                && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) && getRepeatRule() != null) {
            getRepeatRule().updateRepeatInstancesOnDoneCancelOrDelete(this);
        }

        if (updateSupertasks) { //must call *after* creating repeat instances to update project correctly based on new subtasks
            Item owner = getOwnerItem();
            if (owner != null) {// && owner.getStatus() != newStatus) {
                owner.updateStatusOnSubtaskStatusChange(this, oldStatus, newStatus, now);
                DAO.getInstance().saveInBackground(owner);
            }
        }
    }

    /**
     * sets the Done status of the Item. if current status is not Done, then
     * Created, Ongoing, Waiting, then set to completed. TODO!!!! check this
     * works If current status is Cancelled, then set to Created, or Ongoing if
     * Actual!=0. If current status is Done, then set to Created, or Ongoing if
     * Actual!=0.
     *
     * @param done
     */
    public void setDone(boolean done) {
        if (done) {
            if (!isDone()) { //only change status if not already Done (or Cancelled)
                setStatus(ItemStatus.DONE);
            }
        } else {//if Done was set before and now is unset then reset completedDate //TODO: avoid extra call to receiveChangeEvent() in setCompletedDate()
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (getActualEffort() != 0) {
//            //if effort is recorded, then the right state to revert to is ONGOING;
//            //TODO!!!!: are there other indicators that previous state should be other than CREATED (or should we simply store the previous state and use that??!!)
//            setStatus(ItemStatus.ONGOING);
//        } else {
//</editor-fold>
            setStatus(ItemStatus.CREATED);
        }
    }

    /**
     * returns an composite state for a list of tasks (list of leaf(!) subtasks
     * or ItemList) based on the state of all its subtasks/subprojects Rules: -
     * all tasks Done or Cancelled => Done; - all tasks Cancelled => Cancelled;
     * - if all that are not Done/Cancelled are Waiting => Waiting (//UI: a
     * project is only waiting if ALL undone tasks are waiting - meaning no work
     * to do currently); - if at least one task is Ongoing (and some
     * Cancelled/Waiting/Done) => Ongoing; - else: Created
     *
     * @return
     */
    public ItemStatus getStatusFromSubtasks() {
        return getStatus(getListFull());
    }

    public static ItemStatus getStatus(List<Item> list) {
        //TODO optimization: only calculate/update a project's status when a subtask changes and then simply return the project's status here instead of calculating dynamically
        Bag<ItemStatus> statusCount = new Bag<ItemStatus>();
        int listSize = list.size();

//        getStatusImplProjectLeaftasks(list, statusCount);
        getStatusImplDirectSubTasksOnly(list, statusCount);

        if (statusCount.getCount(ItemStatus.CANCELLED) == listSize) {//all subtasks are cancelled
            return ItemStatus.CANCELLED;
//        if (statusCount[BaseItemOrList.STATUS_CREATED] == listSize) {//all subtasks are created
//                projectStatus = BaseItemOrList.STATUS_CREATED; 
        } else if (statusCount.getCount(ItemStatus.DONE) + statusCount.getCount(ItemStatus.CANCELLED) == listSize) {//all subtasks are either done or cancelled
            return ItemStatus.DONE;
        } else if (statusCount.getCount(ItemStatus.DONE) + statusCount.getCount(ItemStatus.CANCELLED) + statusCount.getCount(ItemStatus.WAITING) == listSize) {//all subtasks are either done or cancelled
            return ItemStatus.WAITING;
        } else if (statusCount.getCount(ItemStatus.ONGOING) >= 1 || statusCount.getCount(ItemStatus.DONE) >= 1) {//if at least one subtasks is ongoing or one is Done
            return ItemStatus.ONGOING;
//<editor-fold defaultstate="collapsed" desc="comment">
//        } else if (statusCount.getCount(ItemStatus.ONGOING) > 0
//                || (statusCount.getCount(ItemStatus.DONE) > 0
//                && statusCount.getCount(ItemStatus.CREATED) > 0
//                && statusCount.getCount(ItemStatus.WAITING) == 0)) { //the whole project is ongoing if just one task is ongoing, or if there if some, but not all, tasks are completed
//            projectStatus = ItemStatus.ONGOING;
//        } else if (statusCount.getCount(ItemStatus.WAITING) > 0) { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
////        } else { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
//            projectStatus = ItemStatus.WAITING;
//</editor-fold>
        } else { //if (statusCount[BaseItemOrList.STATUS_CREATED]>0) // if there are no ongoing and no waiting, then the only possible state for remaining subtasks is Created
            return ItemStatus.CREATED;
        }
    }

    /**
     * gets the status of a list of tasks from the status of its top-level tasks
     * only (contrary to getStatusImplProjectLeaftasks). Not sure if this will
     * give the same as based on leaf-tasks (which might be more precise??) but
     * is more logical for end-user since easier to visually check project tasks
     * against its first-level sub-tasks (at any level of the project hierarchy)
     *
     * @param list
     * @param statusCount counts the number of times each status appears
     */
    private static void getStatusImplDirectSubTasksOnly(List<Item> list, Bag<ItemStatus> statusCount) {
//    private static void getStatusImpl(List<ItemAndListCommonInterface> list, Bag<ItemStatus> statusCount) {
//    protected ItemStatus getListStatus() {
        for (int i = 0, size = list.size(); i < size; i++) {
//            statusCount.add(list.get(i).getStatus());
//            statusCount.add(list.get(i).getStatus());
//            ItemAndListCommonInterface item = list.get(i);
            Item item = list.get(i);
//            if (item.isProject()) {
//                getStatusImplDirectSubTasksOnly(item.getList(), statusCount);
//            } else {
            statusCount.add(item.getStatus());
//            }
        }
    }

    /**
     * gets the status of a list of tasks based on the status of its leaf-tasks
     * (contrary to getStatusImplProjectLeaftasks) counts all leaf tasks
     *
     * @param list
     * @param statusCount
     */
//    private static void getStatusImplProjectLeaftasksXXX(List<Item> list, Bag<ItemStatus> statusCount) {
////    protected ItemStatus getListStatus() {
//        for (int i = 0, size = list.size(); i < size; i++) {
////            statusCount.add(list.get(i).getStatus());
////            statusCount.add(list.get(i).getStatus());
////            ItemAndListCommonInterface item = list.get(i);
//            Item item = list.get(i);
//            if (item.isProject()) {
//                getStatusImplProjectLeaftasksXXX(item.getListFull(), statusCount);
//            } else {
//                statusCount.add(list.get(i).getStatus());
////                statusCount.add(item.getStatus());
//            }
//        }
//    }
    private ItemStatus getStatusFromParse() {
        String status = getString(PARSE_STATUS);
        return (status == null) ? ItemStatus.CREATED : ItemStatus.valueOf(status); //Created is initial value
    }

    public ItemStatus getStatus() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (isProject()) {
//            return getStatus(getList());
//        } else {
//            return getStatusFromParse();
//        }
//</editor-fold>
        return getStatusFromParse();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemStatus getStatusOLD() {
////        return status;
////        Object status = (ItemStatus) get(PARSE_STATUS);
//        String status = getString(PARSE_STATUS);
//        return (status == null) ? ItemStatus.CREATED : ItemStatus.valueOf(status); //Created is initial value
//    }
    /**
     * refresh all values that are derived (or depend on other fields) and
     * stored in Parse AND used for queries. Eg all project values that depend
     * on the subtasks are in this case. EG a project's Remaining time changes
     * each time the remaining of a subtask changes. Projects can occur in
     * multiple layers (subprojects within projects etc). Derived values can be
     * refreshed and saved whenever an impacting data changes. Or they can be
     * marked 'dirty' and everything recalculated 'later' (determining when may
     * be a challenges) One optimization challenge is if there are many
     * concurrent changes in subtasks, e.g. marking them all Done, since the
     * project hierarchy would be resaved when each subtask is updated. One way
     * to avoid this is to update the project's status *before* updating the
     * subtasks. Eg if setting a project Done, and marking all subtasks Done.
     * Other than that, will all other changes be bottom-up? The simplest,
     * though not most efficient, approach is to have a 'refresh' method on an
     * Item that will re-calculcate all derived values and store them in Parse.
     * with this approach the calculation dynamics will be moved out of the
     * getters (gaining some efficiency during display) Each subtask must then
     * call it's owner (recurseing up the hierarchy) and recalculate and save
     * all changes along the way. So, the normal case where a subtask is updated
     * will update all its owners and save them to Parse. To implement this,
     * each impacting data must all refresh!!
     *
     * When subtasks inherit from their owner: whenever owner changes value, run
     * through all subtasks (those with no own, overriding value) and update and
     * save, whenever addding new subtasks, update them, , //**TO ANALYSE** //
     * //**Impacting** data (derived from subtasks): //PARSE_REMAINING_EFFORT,
     * PARSE_ACTUAL_EFFORT -> //PARSE_STATUS //PARSE_STARTED_ON_DATE -> the date
     * of the first subtask started //PARSE_COMPLETED_DATE -> the date of the
     * last subtask completed // //***Inherited from owner*** //PARSE_DUE_DATE
     * -> subtasks can inherit due date from parent? TODO
     * //PARSE_EXPIRES_ON_DATE -> all subtasks are impacted if their project
     * experies, BUT is it enough to act at the project-level? Yes, setting a
     * Proje t CANCELLED will/should also cancel subtasks!! (
     * //PARSE_HIDE_UNTIL_DATE -> also hide all subtasks!!
     * //PARSE_WAITING_TILL_DATE -> if a project is waiting, all its subtasks
     * are waiting //PARSE_DATE_WHEN_SET_WAITING -> hmm, yes //PARSE_PRIORITY ->
     * //PARSE_TEMPLATE -> //PARSE_START_BY_DATE -> if a project should start on
     * a certain date, it is most likely the case for all its subtasks
     * //PARSE_FILTER_SORT_DEF -> filtering, you expect to see the same view for
     * subprojects as for project. You could override but too complex to
     * manage?! // //**NOT** derived: //PARSE_EFFORT_ESTIMATE -> to keep
     * original project estimate, TODO: makes sense to ALSO have sum of subtask
     * estimates! //PARSE_EARNED_VALUE -> same as PARSE_EFFORT_ESTIMATE
     * //PARSE_TEXT -> each has its own name //PARSE_COMMENT -> each has own
     * comments //PARSE_SUBTASKS //PARSE_INTERRUPT_OR_INSTANT_TASK -> a project
     * may have Interrupt subtasks, but it doesn't make it an Interrupt project.
     * If a project starts as Interrupt, later subtasks aren't.
     * //PARSE_DREAD_FUN_VALUE, PARSE_CHALLENGE -> individual to each tasks
     * (TODO a Dread projec //PARSE_ALARM_DATE //PARSE_WAITING_ALARM_DATE
     * //PARSE_REPEAT_RULE -> specific to either subtasks, or the whole project
     * will repeat //PARSE_CATEGORIES -> each its own categories //PARSE_STARRED
     * -> a property of an invidual task //PARSE_IMPORTANCE //PARSE_URGENCY ->
     * currently all these are invidual (so you can pick subtasks)
     * //PARSE_OWNER_LIST, PARSE_OWNER_ITEM, PARSE_OWNER_TEMPLATE_LIST,
     * PARSE_OWNER_CATEGORY //PARSE_INTERRUPTED_TASK -> same logic as
     * PARSE_INTERRUPT_OR_INSTANT_TASK //PARSE_DEPENDS_ON_TASK -> subtasks can
     * depend on other subtasks //PARSE_UPDATED_AT, PARSE_CREATED_AT -> managed
     * by Parse //PARSE_ORIGINAL_SOURCE -> points to individual tasks
     * //PARSE_DELETED_DATE -> individual //PARSE_FIRST_ALARM ->> same logic as
     * alarms (NOT USED yet?!) // //**NOT USED?!** //PARSE_TIMER_STARTED
     * //PARSE_TIMER_PAUSED // // //</editor-fold>
     *
     */
    /**
     * called in every setter for values that are either inherited by subtasks
     * or which impacts/changes the owner's values
     */
//    private void updateSubtasks() {
//        updateInheritedValuesInSubtasks(); //first update impacted subtasks
//    }
//    private void updateOwner() {
//        Item owner = getOwnerItem();
//        if (owner != null) {
//            owner.updateValuesDerivedFromSubtasks(); //update owner hierarcy
//        }
//    }
    /**
     * if this Item is a Project, then refresh all derived values (values
     * depending on its subtasks' values) stored in Parse. called by subtasks
     * owner.updateDerivedValues() whenever a field that affects the owner is
     * modified (e.g.
     */
    private void updateValuesDerivedFromSubtasksWhenSubtaskListChange() {
        //**Impacting** data (derived/depending on/calculated based on from subtasks):
        //PARSE_STATUS
        //PARSE_REMAINING_EFFORT, PARSE_ACTUAL_EFFORT -> 
        //PARSE_STARTED_ON_DATE -> the date of the first subtask started
        //PARSE_COMPLETED_DATE -> the date of the last subtask completed

        if (isProject()) {

            ItemStatus currentProjectStatusFromSubtasks = getStatusFromSubtasks();
            ItemStatus currentTaskStatusInParse = getStatusFromParse();
            if (currentProjectStatusFromSubtasks != currentTaskStatusInParse
                    && !(currentProjectStatusFromSubtasks == ItemStatus.CREATED && currentTaskStatusInParse == ItemStatus.ONGOING)) { //do not set a project which is Ongoing to Created just because the subtasks are all created (e.g. Actual could have been captured on project task already, or it could have been set Ongoing manually)
                setStatusInParse(currentProjectStatusFromSubtasks);
            }

            ////////////// ActualEffort
//            long currentProjectActualEffortFromSubtasks = getActualForSubtasks();
//            long currentProjectActualEffortProjectTaskItself = getActualForProjectTaskItself();
//            long currentTotal = getActualFromParse();
//            long newTotalActual = currentProjectActualEffortFromSubtasks + currentProjectActualEffortProjectTaskItself;
////            if (currentProjectActualEffortFromSubtasks != currentProjectActualEffortProjectTaskItself) {
//            if (newTotalActual != currentTotal) {
////                setActualEffortProjectTaskItselfInParse(currentProjectActualEffortProjectTaskItself); //store old Actual for project task itself
//                setActualImpl(newTotalActual, false);
//            }
//            updateActualOnSubtaskChange();
            updateActualOnSubtaskChange();

            ////////////// RemainingEffort
//            long oldTotalRemaining = getRemaining();
////            long currentRemainingEffortSubtasks = getRemainingEffortFromSubtasks();
////            long currentProjectRemainingEffortInParse = getRemainingEffortProjectTaskItself();
////            long newTotalRemaining = currentProjectRemainingEffortInParse + currentRemainingEffortSubtasks;
//            long newTotalRemaining = getRemainingForProjectTaskItself() + getRemainingForSubtasks();
//            if (newTotalRemaining != oldTotalRemaining) {
//                setRemaining(newTotalRemaining);
//            }
//            setRemaining(getRemainingForProjectTaskItself(), true); //this should be enough to refresh the total remaining with updated subtasks
            updateRemainingOnSubtaskChange();

            ////////////// EffortEstimate
//            long oldProjectEffortEstimate = getEstimate();
//            long currentProjectEffortEstimateProjectItself = getEstimateForProjectTaskItself();
//            long currentProjectEffortSutasks = getEstimateForSubtasks();
//            long newTotalEffortEstimate = currentProjectEffortEstimateProjectItself + currentProjectEffortSutasks;
//            if (newTotalEffortEstimate != oldProjectEffortEstimate) {
//                setEstimateImpl(newTotalEffortEstimate);
//            }
            updateEstimateOnChangeInSubtasks();

            ////////////// CompletedDate
            if (isDone()) {
                Date currentProjectCompletedDate = getCompletedDateD();
                Date currentProjectCompletedDateInParse = getCompletedDateDInParse();
                if (currentProjectCompletedDate.getTime() != currentProjectCompletedDateInParse.getTime()) {
                    setCompletedDateInParse(currentProjectCompletedDate);
                }
            }

            Date currentProjectStartedOnDate = getStartedOnDateD();
            Date currentProjectStartedOnInParse = getStartedOnDateDInParse();
            if (currentProjectStartedOnDate.getTime() != currentProjectStartedOnInParse.getTime()) {
                setStartedOnDateInParse(currentProjectStartedOnDate);
            }

            if (false) {
                DAO.getInstance().saveInBackground(this); //NOT needed sine updateValues is (at least currently) only called when setting the subtask list, in which case it needs to be saved at a higher level anyway
            }
        }
    }

//    private void updateValuesInheritedFromOwnerXXX() {
//        updateValuesInheritedFromOwner(getOwnerItem());
//    }
    /**
     * update with values from newOwner if either no values was previously set,
     * or if the previously set value was inherited from the previous owner.
     * call
     *
     * @paramnewOwnerNnewOwner if null, nothing's done
     * @param oldOwner null or previous owner
     */
    void updateValuesInheritedFromOwner(ItemAndListCommonInterface newOwnerN) {
        //***Inherited from owner***
        //PARSE_DUE_DATE -> subtasks can inherit due date from parent? TODO
        //PARSE_EXPIRES_ON_DATE -> all subtasks are impacted if their project experies, BUT is it enough to act at the project-level? Yes, setting a Proje t CANCELLED will/should also cancel subtasks!! (
        //PARSE_HIDE_UNTIL_DATE -> also hide all subtasks!!
        //PARSE_WAITING_TILL_DATE -> if a project is waiting, all its subtasks are waiting
        //PARSE_DATE_WHEN_SET_WAITING -> hmm, yes
        //PARSE_PRIORITY ->
        //PARSE_START_BY_DATE -> if a project should start on a certain date, it is most likely the case for all its subtasks
        //PARSE_TEMPLATE ->
        //PARSE_FILTER_SORT_DEF -> filtering, you expect to see the same view for subprojects as for project. You could override but too complex to manage?!
//        Item owner = getOwnerItem();
//        Item owner = null;
//        if (owner instanceof Item)
//            owner = (Item) owner;
//        else return;
        //NB!! we need to distinguish when an owner has changed value (so need before/after value!) => the subtasks must be updated
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (newOwner != null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean()) {
//
//            if (MyPrefs.itemInheritOwnerProjectDueDate.getBoolean() && ((oldOwner != null && oldOwner.getDueDateD().equals(getDueDateD())) || getDueDateD().getTime() == 0)) { //getDueDateD().getTime() == 0 =>> only set inherited value if no value has been set manually already
//                setDueDate(newOwner.getDueDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectExpiresOnDate.getBoolean() && ((oldOwner != null && oldOwner.getExpiresOnDateD().equals(getExpiresOnDateD())) || getExpiresOnDateD().getTime() == 0)) {
//                setExpiresOnDate(newOwner.getExpiresOnDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean() && ((oldOwner != null && oldOwner.getWaitingTillDateD().equals(getWaitingTillDateD())) || getWaitingTillDateD().getTime() == 0)) {
//                setWaitingTillDate(newOwner.getWaitingTillDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectHideUntilDate.getBoolean() && ((oldOwner != null && oldOwner.getHideUntilDateD().equals(getHideUntilDateD())) || getHideUntilDateD().getTime() == 0)) {
//                setHideUntilDate(newOwner.getHideUntilDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectDateWhenSetWaiting.getBoolean() && ((oldOwner != null && oldOwner.getDateWhenSetWaitingD().equals(getDateWhenSetWaitingD())) || getDateWhenSetWaitingD().getTime() == 0)) {
//                setDateWhenSetWaiting(newOwner.getDateWhenSetWaitingD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectPriority.getBoolean() && ((oldOwner != null && oldOwner.getPriority() == getPriority()) || getPriority() == 0)) {
//                setPriority(newOwner.getPriority());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectUrgency.getBoolean() && ((oldOwner != null && oldOwner.getUrgencyN() == getUrgencyN()) || getUrgencyN() == null)) {
//                setUrgency(newOwner.getUrgencyN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectImportance.getBoolean() && ((oldOwner != null && oldOwner.getImportanceN() == getImportanceN()) || getImportanceN() == null)) {
//                setImportance(newOwner.getImportanceN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectChallenge.getBoolean() && ((oldOwner != null && oldOwner.getChallengeN() == getChallengeN()) || getChallengeN() == null)) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
//                setChallenge(newOwner.getChallengeN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean() && ((oldOwner != null && oldOwner.getDreadFunValueN() == getDreadFunValueN()) || getDreadFunValueN() == null)) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
//                setDreadFunValue(newOwner.getDreadFunValueN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean() && ((oldOwner != null && oldOwner.getStartByDateD().equals(getStartByDateD())) || getStartByDateD().getTime() == 0)) {
//                setStartByDate(newOwner.getStartByDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectTemplate.getBoolean()) {
//                setTemplate(newOwner.isTemplate());
//            }
//        }
//</editor-fold>
        if (newOwnerN instanceof Item && MyPrefs.itemInheritOwnerProjectProperties.getBoolean()) {

            Item newOwnerItem = (Item) newOwnerN;

            if (MyPrefs.itemInheritOwnerProjectDueDate.getBoolean() && getDueDateD().getTime() == 0) { //getDueDateD().getTime() == 0 =>> only set inherited value if no value has been set manually already
                setDueDate(newOwnerItem.getDueDateD());
            }

            if (MyPrefs.itemInheritOwnerProjectExpiresOnDate.getBoolean() && getExpiresOnDateD().getTime() == 0) {
                setExpiresOnDate(newOwnerItem.getExpiresOnDateD());
            }

            if (MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean() && getWaitingTillDateD().getTime() == 0) {
                setWaitingTillDate(newOwnerItem.getWaitingTillDateD());
            }

            if (MyPrefs.itemInheritOwnerProjectHideUntilDate.getBoolean() && getHideUntilDateD().getTime() == 0) {
                setHideUntilDate(newOwnerItem.getHideUntilDateD());
            }

            if (MyPrefs.itemInheritOwnerProjectDateWhenSetWaiting.getBoolean() && getDateWhenSetWaitingD().getTime() == 0) {
                setDateWhenSetWaiting(newOwnerItem.getDateWhenSetWaitingD());
            }

            if (MyPrefs.itemInheritOwnerProjectPriority.getBoolean() && getPriority() == 0) {
                setPriority(newOwnerItem.getPriority());
            }

            if (MyPrefs.itemInheritOwnerProjectUrgency.getBoolean() && getUrgencyN() == null) {
                setUrgency(newOwnerItem.getUrgencyN());
            }

            if (MyPrefs.itemInheritOwnerProjectImportance.getBoolean() && getImportanceN() == null) {
                setImportance(newOwnerItem.getImportanceN());
            }

            if (MyPrefs.itemInheritOwnerProjectChallenge.getBoolean() && getChallengeN() == null) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
                setChallenge(newOwnerItem.getChallengeN());
            }

            if (MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean() && getDreadFunValueN() == null) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
                setDreadFunValue(newOwnerItem.getDreadFunValueN());
            }

            if (MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean() && getStartByDateD().getTime() == 0) {
                setStartByDate(newOwnerItem.getStartByDateD());
            }

            if (MyPrefs.itemInheritOwnerProjectTemplate.getBoolean()) {
                setTemplate(newOwnerItem.isTemplate());
            }
        }
    }

    /**
     * when removing a subtask from its project, reset all inherited values
     * (where previousOwnerItem has same value as project) back to 'undefined')
     *
     * @param previousOwnerN if null nothing's done
     */
    void removeValuesInheritedFromOwner(ItemAndListCommonInterface previousOwnerN) {
        //***Inherited from owner***
        //PARSE_DUE_DATE -> subtasks can inherit due date from parent? TODO
        //PARSE_EXPIRES_ON_DATE -> all subtasks are impacted if their project experies, BUT is it enough to act at the project-level? Yes, setting a Proje t CANCELLED will/should also cancel subtasks!! (
        //PARSE_HIDE_UNTIL_DATE -> also hide all subtasks!!
        //PARSE_WAITING_TILL_DATE -> if a project is waiting, all its subtasks are waiting
        //PARSE_DATE_WHEN_SET_WAITING -> hmm, yes
        //PARSE_PRIORITY ->
        //PARSE_START_BY_DATE -> if a project should start on a certain date, it is most likely the case for all its subtasks
        //PARSE_TEMPLATE ->
        //PARSE_FILTER_SORT_DEF -> filtering, you expect to see the same view for subprojects as for project. You could override but too complex to manage?!
//        Item owner = getOwnerItem();

        //NB!! we need to distinguish when an owner has changed value (so need before/after value!) => the subtasks must be updated
        if (previousOwnerN instanceof Item && MyPrefs.itemInheritOwnerProjectProperties.getBoolean()) {

            Item previousOwnerItem = (Item) previousOwnerN;

            if (MyPrefs.itemInheritOwnerProjectDueDate.getBoolean() && getDueDateD().equals(previousOwnerItem.getDueDateD())) { //getDueDateD().getTime() == 0 =>> only set inherited value if no value has been set manually already
                setDueDate(new Date(0));
            }

            if (MyPrefs.itemInheritOwnerProjectExpiresOnDate.getBoolean() && getExpiresOnDateD().equals(previousOwnerItem.getExpiresOnDateD())) {
                setExpiresOnDate(0);
            }

            if (MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean() && getWaitingTillDateD().equals(previousOwnerItem.getWaitingTillDateD())) {
                setWaitingTillDate(0);
            }

            if (MyPrefs.itemInheritOwnerProjectHideUntilDate.getBoolean() && getHideUntilDateD().equals(previousOwnerItem.getHideUntilDateD())) {
                setHideUntilDate(0);
            }

            if (MyPrefs.itemInheritOwnerProjectDateWhenSetWaiting.getBoolean() && getDateWhenSetWaitingD().equals(previousOwnerItem.getDateWhenSetWaitingD())) {
                setDateWhenSetWaiting(0);
            }

            if (MyPrefs.itemInheritOwnerProjectPriority.getBoolean() && getPriority() == previousOwnerItem.getPriority()) {
                setPriority(0);
            }

            if (MyPrefs.itemInheritOwnerProjectUrgency.getBoolean() && MyUtil.eql(getUrgencyN(), previousOwnerItem.getUrgencyN())) {
                setUrgency(null);
            }

            if (MyPrefs.itemInheritOwnerProjectImportance.getBoolean() && MyUtil.eql(getImportanceN(), previousOwnerItem.getImportanceN())) {
                setImportance(null);
            }

            if (MyPrefs.itemInheritOwnerProjectChallenge.getBoolean() && MyUtil.eql(getChallengeN(), previousOwnerItem.getChallengeN())) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
                setChallenge(null);
            }

            if (MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean() && MyUtil.eql(getDreadFunValueN(), previousOwnerItem.getDreadFunValueN())) { //false=> challenge is not making a lot of sense to inherit for each subtask (a challenging project may have simply subtasks)
                setDreadFunValue(null);
            }

            if (MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean() && MyUtil.eql(getStartByDateD(), previousOwnerItem.getStartByDateD())) {
                setStartByDate(0);
            }

            if (MyPrefs.itemInheritOwnerProjectTemplate.getBoolean()) {//&&previousOwner.isTemplate()) {
                setTemplate(false);
            }
        }
    }

    void removeValuesInheritedFromOwner() {
        removeValuesInheritedFromOwner(getOwnerItem());
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * If this Item is a subtask, then update its values inherited from is
     * Project (owner)
     */
//    private void updateInheritedValuesInSubtasks() {
//        if (isProject()) {
////            List<Item> subtasks = getList();
//            List subtasks = getList();
//            for (Object item : subtasks) {
//                ((Item) item).updateMyValuesInheritedFromOwnerXXX();
//            }
//            DAO.getInstance().saveBatch(subtasks); //save every subtask, Parse will only save those with actual changes(checked, but really working??)
//        }
//    }
    /**
     * must be called before the owner updates
     */
//    private void updateMyValuesInheritedFromOwnerXXX() {
//    private void updateValuesInheritedFromOwnerXXX() {
//        //***Inherited from owner***
//        //PARSE_DUE_DATE -> subtasks can inherit due date from parent? TODO
//        //PARSE_EXPIRES_ON_DATE -> all subtasks are impacted if their project experies, BUT is it enough to act at the project-level? Yes, setting a Proje t CANCELLED will/should also cancel subtasks!! (
//        //PARSE_HIDE_UNTIL_DATE -> also hide all subtasks!!
//        //PARSE_WAITING_TILL_DATE -> if a project is waiting, all its subtasks are waiting
//        //PARSE_DATE_WHEN_SET_WAITING -> hmm, yes
//        //PARSE_PRIORITY ->
//        //PARSE_START_BY_DATE -> if a project should start on a certain date, it is most likely the case for all its subtasks
//        //PARSE_TEMPLATE ->
//        //PARSE_FILTER_SORT_DEF -> filtering, you expect to see the same view for subprojects as for project. You could override but too complex to manage?!
//        Item owner = getOwnerItem();
//
//        //NB!! we need to distinguish when an owner has changed value (so need before/after value!) => the subtasks must be updated
//        if (owner != null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean()) {
//
//            if (MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()) {
//                if (getDueDateD().getTime() == 0) {
//                    setDueDate(owner.getDueDateD());
//                }
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectExpiresOnDate.getBoolean()) {
//                if (getExpiresOnDateD().getTime() == 0) {
//                    setExpiresOnDate(owner.getExpiresOnDateD());
//                }
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()) {// && owner.getWaitingTillDateD().getTime() != getWaitingTillDateD().getTime()) {
//                if (getWaitingTillDateD().getTime() == 0) {
//                    setWaitingTillDate(owner.getWaitingTillDateD());
//                }
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectHideUntilDate.getBoolean()) {
//                setHideUntilDate(owner.getHideUntilDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectDateWhenSetWaiting.getBoolean()) {
//                setDateWhenSetWaiting(owner.getDateWhenSetWaitingD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectPriority.getBoolean()) {
//                setPriority(owner.getPriority());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectUrgency.getBoolean()) {
//                setUrgency(owner.getUrgencyN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectImportance.getBoolean()) {
//                setImportance(owner.getImportanceN());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()) {
//                setStartByDate(owner.getStartByDateD());
//            }
//
//            if (MyPrefs.itemInheritOwnerProjectTemplate.getBoolean()) {
//                setTemplate(owner.isTemplate());
//            }
//        }
//
//    }
//</editor-fold>
    public long getDueDate() {
//        return dueDate;
//        Date date = getDate(PARSE_DUE_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getDueDateD().getTime();
    }

    private Date getDueDateDFromParse() {
        Date date = getDate(PARSE_DUE_DATE);
        return (date == null) ? new Date(0) : date;
    }

    public Date getDueDateD() {
//        return getDueDateD(true);
//    }
//    public Date getDueDateD(boolean useInheritedValue) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        return dueDate;
//        Date date = getDate(PARSE_DUE_DATE);
//        if (date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getDueDateD();
//            }
//        }
//</editor-fold>
        Date date = getDueDateDFromParse();
//        if (date.getTime() == 0 && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getDueDateD();
//            }
//        }
//        return (date == null) ? new Date(0) : date;
        return date;
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isDueDateInherited(Date potentiallyInheritedValue) {
////        Date date = getDate(PARSE_DUE_DATE);
//        Date date = getDueDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getDueDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getDueDateD().equals(date); //
//          return isInherited(getDueDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectDueDate.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getDueDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectDueDate.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isDueDateInherited() {
////        Date date = getDate(PARSE_DUE_DATE);
//        Date date = getDueDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getDueDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getDueDateD().equals(date); //
//            return getOwnerItem() != null ? isStarInheritedFrom(getOwnerItem().getDueDateD()) : false;
        return isDueDateInherited(getDueDateD());

    }
//    private void setDueDateInParse(Date dueDate) {

    public void setDueDate(Date dueDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (isProject()) {
//            Date oldDate = getDate(PARSE_DUE_DATE);
//            for (Item subtask : (List<Item>) getList()) {
//                Date subtaskDate = subtask.getDueDateD();
////<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
////                if (oldDate == null) {
////                    if (subtaskDate == null) {
////                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                    } else {
////                        //do nothing: subtask already had a date set before project got one
////                    }
////                } else { //oldDate!=null - a previous project date was set
////                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
////                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
////                        //do nothing (subtask had a defined date already and it was different from old project date)
////                    }
////                }
////                if (oldDate == null && subtaskDate == null) {
////                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
//////oldDate!=null - a previous project date was set
////                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                }
////</editor-fold>
//                //above expressions are equivalent to this reduced/simplified version:
//                if ((oldDate == null && subtaskDate == null) || (subtaskDate == null || subtaskDate.equals(oldDate))) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//            }
//        }
//</editor-fold>
        Date oldVal = getDueDateD();
        if (MyUtil.neql(dueDate, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getDueDateD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setDueDate(dueDate);
                    return true;
                }
                return false;
            });
        }
        if (dueDate != null && dueDate.getTime() != 0) {
            put(PARSE_DUE_DATE, dueDate);
        } else {
            remove(PARSE_DUE_DATE);
        }
//        update();
    }

    /**
     * @return The last time this object was updated on the server.
     */
    public Date getUpdatedAt() {
        Date date = super.getUpdatedAt();
        return date != null ? date : new Date(0);
//        if(date != null){
////        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //THJ
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")); //THJ
//        cal.setTime(date);
//        cal.setTimeZone(value);
//        return cal.getTime();
//        } else return new Date(0);
    }

    /**
     * @return The first time this object was saved on the server.
     */
    public Date getCreatedAt() {
        Date date = super.getCreatedAt();
        return date != null ? date : new Date(0);
    }

////<editor-fold defaultstate="collapsed" desc="comment">
//    public void setDueDateXXX(Date dueDate) {
////        this.dueDate = val;
////        if (this.dueDate != val) {
////            this.dueDate = val;
////        }
////        if (has(PARSE_DUE_DATE) || dueDate.getTime() != 0) {
////            put(PARSE_DUE_DATE, dueDate);
////        }
////        if (dueDate != null && dueDate.getTime() != 0) {
////        if (dueDate != null && dueDate.getTime() != 0
////                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean())
////                || getOwnerItem() == null || getOwnerItem().getDueDateD().getTime() != dueDate.getTime())) {
////            put(PARSE_DUE_DATE, dueDate);
////        } else {
////            remove(PARSE_DUE_DATE);
////        }
////        updateMyValuesInheritedFromOwner();
//        setDueDateInParse(dueDate);
//    }
//    private void setDueDateXXX(long dueDate) {
//        this.dueDate = val;
//        if (this.dueDate != val) {
//            this.dueDate = val;
//        }
//        if (has(PARSE_DUE_DATE) || dueDate != 0) {
//            put(PARSE_DUE_DATE, new Date(dueDate));
//        }
//        setDueDate(new Date(dueDate));
//    }
//
//</editor-fold>
    public Date getHideUntilDateD() {
        Date date = getDate(PARSE_HIDE_UNTIL_DATE);
        return (date == null) ? new Date(0) : date;
    }

    public void setHideUntilDate(long hideUntil) {
        setHideUntilDate(new Date(hideUntil));
    }

    public void setHideUntilDate(Date hideUntil) {
//        if (has(PARSE_HIDE_UNTIL_DATE) || hideUntil.getTime() != 0) {
//            put(PARSE_HIDE_UNTIL_DATE, hideUntil);
//        }
        Date oldVal = getHideUntilDateD();
        if (MyUtil.neql(hideUntil, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getHideUntilDateD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setHideUntilDate(hideUntil);
                    return true;
                }
                return false;
            });
        }
        if (hideUntil != null && hideUntil.getTime() != 0) {
            put(PARSE_HIDE_UNTIL_DATE, hideUntil);
        } else {
            remove(PARSE_HIDE_UNTIL_DATE);
        }
    }

    public boolean isHideUntilDate(Date potentiallyInheritedValue) {
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getHideUntilDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectHideUntilDate.getBoolean());
        } else {
            return false;
        }
    }

//    public long getStartByDate() {
////        return startByDate;
////        Date date = getDate(PARSE_START_BY_DATE);
////        return (date == null) ? 0L : date.getTime();
//        return getStartByDateD().getTime();
//    }
    public Date getStartByDateD() {
//        return getStartByDateD(true);
//    }
//
//    public Date getStartByDateD(boolean useInheritedValue) {
//        return new Date(getStartByDate());
        Date date = getDate(PARSE_START_BY_DATE);
//        if (date == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getStartByDateD();
//            }
//        }
        return (date == null) ? new Date(0) : date;
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isStartByDateInherited(Date potentiallyInheritedValue) {
////        Date date = getDate(PARSE_START_BY_DATE);
//        Date date = getStartByDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getStartByDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getStartByDateD().equals(date);
//        return isInherited(getStartByDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getStartByDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean());
        } else {
            return false;
        }

    }

    public boolean isStartByDateInherited() {
////        Date date = getDate(PARSE_START_BY_DATE);
//        Date date = getStartByDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getStartByDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getStartByDateD().equals(date);
//        return getOwnerItem() != null ? isStartByDateInheritedFrom(getOwnerItem().isStarred()) : false;
        return isStartByDateInherited(getStartByDateD());

    }

    public void setStartByDate(long startByDate) {
//        this.dueDate = val;
//        if (this.startByDate != startByDate) {
//            this.startByDate = startByDate;
//        }
//        if (has(PARSE_START_BY_DATE) || startByDate != 0) {
//            put(PARSE_START_BY_DATE, new Date(startByDate));
//        }
        setStartByDate(new Date(startByDate));
    }

    public void setStartByDate(Date startByDate) {
//        setStartByDate(startByDate.getTime());
//        if (startByDate != null && startByDate.getTime() != 0
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartByDate.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getStartByDateD().getTime() != startByDate.getTime())) {
        Date oldVal = getStartByDateD();
        if (MyUtil.neql(startByDate, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getStartByDateD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setStartByDate(startByDate);
                    return true;
                }
                return false;
            });
        }
        if (startByDate != null && startByDate.getTime() != 0) {
            put(PARSE_START_BY_DATE, startByDate);
        } else {
            remove(PARSE_START_BY_DATE);
        }
    }

//    public Date getSnoozeDateD() {
//        Date date = getDate(PARSE_SNOOZE_DATE);
//        return (date == null) ? new Date(0) : date;
//    }
//
//    public void setSnoozeDate(Date snoozeUntilDate) {
//        if (snoozeUntilDate != null && snoozeUntilDate.getTime() != 0) {
//            put(PARSE_SNOOZE_DATE, snoozeUntilDate);
//        } else {
//            remove(PARSE_SNOOZE_DATE);
//        }
//    }
    public AlarmRecord getSnoozeAlarmRecord() {
        Date date = getDate(PARSE_SNOOZE_DATE);
        String type = getString(PARSE_SNOOZED_TYPE);
        if (Config.TEST && type == null) {
            type = AlarmType.snoozedNotif.toString();
        }
        return (date != null) ? new AlarmRecord(date, AlarmType.valueOf(type)) : null;
    }

    public void setSnoozeAlarmRecord(AlarmRecord snooze) {
        if (snooze.alarmTime != null && snooze.alarmTime.getTime() != 0) {
            put(PARSE_SNOOZE_DATE, snooze.alarmTime);
            put(PARSE_SNOOZED_TYPE, snooze.type.toString());

        } else {
            remove(PARSE_SNOOZE_DATE);
            remove(PARSE_SNOOZED_TYPE);
        }
    }

    public Date getWaitingTillDateD() {
//        return getWaitingTillDateD(true);
//    }
//
//    public Date getWaitingTillDateD(boolean useInheritedValue) {
        Date date = getDate(PARSE_WAITING_TILL_DATE);
//        if (date == null && useInheritedValue && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()) {
//            if (getOwnerItem() != null) {
//                return getOwnerItem().getWaitingTillDateD();
//            }
//        }
        return (date == null) ? new Date(0) : date;
    }

    /**
     * returns true if the value is inherited from it's owner (returns false if
     * the value could be inherited but owner does not define any value)
     *
     * @return
     */
    public boolean isWaitingTillInherited(Date potentiallyInheritedValue) {
////        Date date = getDate(PARSE_WAITING_TILL_DATE);
//        Date date = getWaitingTillDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getWaitingTillDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getWaitingTillDateD().equals(date);
//        return isInherited(getWaitingTillDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean());
        if (getOwnerItem() != null) {
            return isInherited(getOwnerItem().getWaitingTillDateD(), potentiallyInheritedValue, MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean());
        } else {
            return false;
        }
    }

    public boolean isWaitingTillInherited() {
////        Date date = getDate(PARSE_WAITING_TILL_DATE);
//        Date date = getWaitingTillDateD();
////        return date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()
////                && getOwnerItem() != null && getOwnerItem().getWaitingTillDateD().getTime() != 0;
//        return MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()
//                && getOwnerItem() != null && getOwnerItem().getWaitingTillDateD().equals(date);
//        return getOwnerItem() != null ? isWaitingTillInherited(getOwnerItem().getWaitingTillDateD()) : false;
        return isWaitingTillInherited(getWaitingTillDateD());

    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getWaitingTillDate() {
////        return waitingTillDate;
////        Date date = getDate(PARSE_WAITING_TILL_DATE);
////        return (date == null) ? 0L : date.getTime();
//        return getWaitingAlarmDateD().getTime();
//    }
//</editor-fold>

    public void setWaitingTillDate(long waitingTillDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.dueDate = val;
//        if (this.waitingTillDate != val) {
//            this.waitingTillDate = val;
////            setWaitingLastActivatedDate(MyDate.getNow()); //automatically
//        }
//        if (has(PARSE_WAITING_TILL_DATE) || waitingTillDate != 0) {
//            put(PARSE_WAITING_TILL_DATE, new Date(waitingTillDate));
//        }
//        if (has(PARSE_WAITING_TILL_DATE) || waitingTillDate != 0) {
//            put(PARSE_WAITING_TILL_DATE, new Date(waitingTillDate));
//        }
//</editor-fold>
        setWaitingTillDate(new Date(waitingTillDate));
    }

    public void setWaitingTillDateInParse(Date waitingTillDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (isProject()) {
//            Date oldDate = getDate(PARSE_WAITING_TILL_DATE);
//            for (Item subtask : (List<Item>) getList()) {
//                Date subtaskDate = subtask.getWaitingTillDateD();
////<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
////                if (oldDate == null) {
////                    if (subtaskDate == null) {
////                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                    } else {
////                        //do nothing: subtask already had a date set before project got one
////                    }
////                } else { //oldDate!=null - a previous project date was set
////                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
////                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
////                        //do nothing (subtask had a defined date already and it was different from old project date)
////                    }
////                }
////                if (oldDate == null && subtaskDate == null) {
////                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
////                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
//////oldDate!=null - a previous project date was set
////                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
////                }
////</editor-fold>
//                //above expressions are equivalent to this reduced/simplified version:
//                if ((oldDate == null && subtaskDate == null) || (subtaskDate == null || subtaskDate.equals(oldDate))) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                    subtask.setWaitingTillDate(waitingTillDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//            }
//        }
//</editor-fold>
        Date oldVal = getWaitingTillDateD();
        if (MyUtil.neql(waitingTillDate, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getWaitingTillDateD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setWaitingTillDate(waitingTillDate);
                    return true;
                }
                return false;
            });
        }
        if (waitingTillDate != null && waitingTillDate.getTime() != 0) {
            put(PARSE_WAITING_TILL_DATE, waitingTillDate);
        } else {
            remove(PARSE_WAITING_TILL_DATE);
        }
//        update();
    }

    public void setWaitingTillDate(Date waitingTillDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (has(PARSE_WAITING_TILL_DATE) || waitingTillDate != 0) {
//            put(PARSE_WAITING_TILL_DATE, new Date(waitingTillDate));
//        }
//        if (waitingTillDate.getTime() != 0
//                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean())
//                || getOwnerItem() == null || getOwnerItem().getWaitingTillDateD().getTime() != waitingTillDate.getTime())) {
//            put(PARSE_WAITING_TILL_DATE, waitingTillDate);
//        } else {
//            remove(PARSE_WAITING_TILL_DATE);
//        }
//</editor-fold>
        setWaitingTillDateInParse(waitingTillDate);
    }

    public Date getDateWhenSetWaitingD() {
        Date date = getDate(PARSE_DATE_WHEN_SET_WAITING);
        return (date == null) ? new Date(0) : date;
    }

    public long getDateWhenSetWaiting() {
//        return waitingLastActivatedDate;
//        Date date = getDate(PARSE_DATE_WHEN_SET_WAITING);
//        return (date == null) ? 0L : date.getTime();
        return getDateWhenSetWaitingD().getTime();
    }

    public void setDateWhenSetWaiting(long waitingLastActivatedDate) {
//        this.dueDate = val;
//        if (this.waitingLastActivatedDate != val) {
//            this.waitingLastActivatedDate = val;
//        }
//        if (has(PARSE_DATE_WHEN_SET_WAITING) || waitingLastActivatedDate != 0) {
//            put(PARSE_DATE_WHEN_SET_WAITING, new Date(waitingLastActivatedDate));
//        }
        setDateWhenSetWaiting(new Date(waitingLastActivatedDate));
    }

    public void setDateWhenSetWaiting(Date waitingLastActivatedDate) {
//        this.dueDate = val;
//        if (this.waitingLastActivatedDate != val) {
//            this.waitingLastActivatedDate = val;
//        }
//TODO!!! check that date is set when status is set Waiting
        Date oldVal = getDateWhenSetWaitingD();
        if (MyUtil.neql(waitingLastActivatedDate, oldVal)) {
            opsToUpdateSubtasks.add((subtask) -> {
//                if (eql(getImportanceN(), subtask.getImportanceN())) { //if old project value equals current subtask value, then update subtasks value to project's new value
                if (MyUtil.eql(subtask.getDateWhenSetWaitingD(), oldVal)) { //if old project value equals current subtask value, then update subtasks value to project's new value
                    subtask.setDateWhenSetWaiting(waitingLastActivatedDate);
                    return true;
                }
                return false;
            });
        }
        if (waitingLastActivatedDate != null && waitingLastActivatedDate.getTime() != 0) {
            put(PARSE_DATE_WHEN_SET_WAITING, waitingLastActivatedDate);
        } else {
            remove(PARSE_DATE_WHEN_SET_WAITING);
        }
    }

    ////////////// ESTIMATE ///////////////
    private void setEstimateInParseImpl(long effortEstimateMillis) {
        if (effortEstimateMillis != 0) {
            put(PARSE_EFFORT_ESTIMATE, effortEstimateMillis); //update first 
        } else {
            remove(PARSE_EFFORT_ESTIMATE);
        }
        updateEarnedValuePerHour();
    }

    private void setEstimateForProjectTaskItselfInParse(long effortEstimateMillis) {
        if (effortEstimateMillis != 0) {
            put(PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF, effortEstimateMillis); //update first 
        } else {
            remove(PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF);
        }
    }

    private void setEstimateImpl(long effortEstimateTotalMillis) {
        long currentProjectEffortEstimate = getEstimate();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (effortEstimateMillis != 0) {
//            put(PARSE_EFFORT_ESTIMATE, effortEstimateMillis); //update first
//        } else {
//            remove(PARSE_EFFORT_ESTIMATE);
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        setEffortEstimateProjectTaskItselfInParse(effortEstimateTotalMillis);
//calc new total
//        long totalEffortEstimate;
//        if (isProject()) {
//            long effortEstimateSubtasks = getEffortEstimateForSubtasks();
//            if (effortEstimateSubtasks > 0 && !MyPrefs.estimateEffortEstimateOnlyUseSubtasksEstimates.getBoolean()) {
//                totalEffortEstimate = effortEstimateSubtasks + effortEstimateTotalMillis;
//            } else {
//                totalEffortEstimate = effortEstimateSubtasks;
//            }
//        } else {
//            totalEffortEstimate = effortEstimateTotalMillis;
//        }
//        setEffortEstimateInParseImpl(totalEffortEstimate);
//</editor-fold>
        setEstimateInParseImpl(effortEstimateTotalMillis);

//        update();
        //signal to owner if total has changed
        Item owner = getOwnerItem();
        if (owner != null) {
//            long currentProjectEffortEstimateInParse = owner.getEffortEstimateFromParse();
            if (effortEstimateTotalMillis != currentProjectEffortEstimate) {
//                owner.setEffortEstimateInParse(currentProjectEffortEstimate);
//                owner.updateOnEstimateChangeInSubtask(currentProjectEffortEstimate, effortEstimateTotalMillis);
                owner.updateEstimateOnChangeInSubtasks();
//                DAO.getInstance().saveInBackgroundOnTimeout(this);
//                DAO.getInstance().saveInBackground(this);
                DAO.getInstance().saveInBackground(owner);
//                DAO.getInstance().saveInBackground(owner);
            }
        }
    }

    /**
     * effort estimate is handled differently from Actual and Remaining since
     * the project estimate remains (is not updated to include the subtask
     * estimates) to make it possible to compare an original estimate with the
     * total Actual.
     *
     * @param effortEstimateProjectTaskItselfMillis
     * @param autoUpdateRemainingEffort
     */
//    public void setEstimate(long effortEstimateMillis, boolean autoUpdateRemainingEffort, boolean forProjectTaskItself) {
    public void setEstimate(long effortEstimateProjectTaskItselfMillis, boolean autoUpdateRemainingEffort) {

        ASSERT.that(effortEstimateProjectTaskItselfMillis >= 0, "EffortEstimate cannot be negative");

//                long oldEffortTotalSubtasks = getEffortEstimateForSubtasks();
        long oldEffortEstimate = getEstimate();
//<editor-fold defaultstate="collapsed" desc="comment">
//        long effortSubtasks = getEffortEstimateForSubtasks();
//        long newEffortTotal = effortSubtasks + effortEstimateMillis;
//        long prevRemainingProjectTask = getRemainingEffortProjectTaskItself();
//</editor-fold>
        //auto-update Remaining
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (autoUpdateRemainingEffort && effortEstimateProjectTaskItselfMillis > 0
//                && MyPrefs.automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining.getBoolean()
//                && getRemainingForProjectTaskItselfFromParse() == 0) {
//            setRemaining(effortEstimateProjectTaskItselfMillis - getActualForProjectTaskItself(), false); //TODO actualEffort should be set *before* effort estimate for this to work
//        } else { // *increase* remaining //UI:
//            if (autoUpdateRemainingEffort
//                    && MyPrefs.automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual.getBoolean()
//                    && effortEstimateProjectTaskItselfMillis > getRemainingFromParse() + getActual()) {
//                setRemaining(effortEstimateProjectTaskItselfMillis - getActual(), false); //false to avoid circular updates between setEstimate() and setRemaining()
//            }
//        }
//</editor-fold>
        if (autoUpdateRemainingEffort) {
            if (MyPrefs.automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining.getBoolean()
                    && effortEstimateProjectTaskItselfMillis > 0
                    && getRemainingForProjectTaskItselfFromParse() == 0) {
                setRemaining(effortEstimateProjectTaskItselfMillis - getActualForProjectTaskItself(), false); //TODO actualEffort should be set *before* effort estimate for this to work
            } else if (MyPrefs.automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual.getBoolean()
                    && effortEstimateProjectTaskItselfMillis > getRemainingFromParse() + getActual()) { // *increase* remaining //UI:
                setRemaining(effortEstimateProjectTaskItselfMillis - getActual(), false); //false to avoid circular updates between setEstimate() and setRemaining()
            }
        }

        setEstimateForProjectTaskItselfInParse(effortEstimateProjectTaskItselfMillis);

        //calc new total
        long totalEffortEstimate;
        if (isProject()) {
            long effortEstimateSubtasks = getEstimateForSubtasks();
            if (effortEstimateSubtasks > 0 && !MyPrefs.estimateEffortEstimateOnlyUseSubtasksEstimates.getBoolean()) {
                totalEffortEstimate = effortEstimateProjectTaskItselfMillis + effortEstimateSubtasks;
            } else {
                totalEffortEstimate = effortEstimateSubtasks;
            }
        } else {
            totalEffortEstimate = effortEstimateProjectTaskItselfMillis;
        }

//        setERemainingEffortProjectTaskInParse(effortEstimateMillis);
        if (totalEffortEstimate != oldEffortEstimate) {
//            setEffortEstimateTotalInParse(effortEstimateProjectTaskItselfMillis);// + actualEffortMillis); //TODO!!! move below the test below if effortEstimateMillis != oldEffortEstimate??
            setEstimateImpl(totalEffortEstimate);
//<editor-fold defaultstate="collapsed" desc="comment">
//            update(); //DONE in setEffortEstimateInParse()
//            Item owner = getOwnerItem();
//            if (owner != null) {
//                long currentProjectEffortEstimate = owner.getEffortEstimate();
//                long currentProjectEffortEstimateInParse = owner.getEffortEstimateFromParse();
//                if (currentProjectEffortEstimate != currentProjectEffortEstimateInParse) {
//                    owner.setEffortEstimateInParse(currentProjectEffortEstimate);
//                    DAO.getInstance().saveInBackground(owner);
//                }
//            }
//</editor-fold>
        }
    }

    public void setEstimate(long effortEstimateProjectTaskItselfMillis) {
        setEstimate(effortEstimateProjectTaskItselfMillis, false); //false=> don't update the other field by default, only if explicitly defined
    }

//    protected void updateOnEstimateChangeInSubtaskXXX(long oldEstimate, long newEstimate) {
////                    long currentProjectEffortEstimate = owner.getEffortEstimate();
////            long currentProjectEffortEstimateInParse = owner.getEffortEstimateFromParse();
//        long effortEstimate = getEstimate();
////            if (currentProjectEffortEstimate != currentProjectEffortEstimateInParse) {
//        setEstimateImpl(effortEstimate - oldEstimate + newEstimate);
//    }
    protected void updateEstimateOnChangeInSubtasks() {
        setEstimateImpl(getEstimateForProjectTaskItself());
    }

//    public void setEffortEstimateInMinutes(int val) {
//        setEstimate(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
//    private long getEffortEstimateFromParse() {
//        Long effortEstimate = getLong(PARSE_EFFORT_ESTIMATE);
//        return (effortEstimate == null) ? 0L : effortEstimate;
//    }
    @Override
    public long getEstimate() {
////        return getEffortEstimateFromParse();
//        Long effortEstimate = getLong(PARSE_EFFORT_ESTIMATE);
//        if (effortEstimate == null) {
////            return getEffortEstimateForSubtasks();
//            return 0L;
//        } else {
//            return effortEstimate;
//        }
        Long effortEstimate = getLong(PARSE_EFFORT_ESTIMATE);
        return (effortEstimate == null) ? 0L : effortEstimate;

    }

    public long getEstimateForProjectTaskItself() {
        Long effortEstimate = getLong(PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF);
        return (effortEstimate == null) ? 0L : effortEstimate;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getEffortEstimateProjectTaskFromParse() {
//        Long effortEstimate = getLong(PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF);
//        return (effortEstimate == null) ? 0L : effortEstimate;
//    }
//
//    public long getEffortEstimateProjectTask() {
//        if (isProject()) {
//            return getEffortEstimateProjectTaskFromParse();
//        } else {
//            return getEffortEstimateFromParse();
//        }
//    }
//</editor-fold>
    /**
     * returns effort estimate. If no estimate was set (value 0) AND there are
     * subitems, then return the sum of the estimates of the subitems.
     *
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getEffortEstimateXXX(boolean forSubtasks) {
////        if (effortEstimate == 0 && getItemListSize() != 0) {
//        if (forSubtasks && isProject()) {
////            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ESTIMATE); //optimization: store sum in intermediate variable to avoid recalculating each time
//            long subItemSum = 0;
//            for (int i = 0, size = getItemListSize(); i < size; i++) {
////                Item item = (Item) getItemList().getItemAt(i);
//                Item item = (Item) getList().get(i);
//                if (!item.isDone()) { // /** || includeDone */) {
//                    subItemSum += item.getEffortEstimate(forSubtasks);
//                }
//            }
//            return subItemSum;
////            return ((Long) derivedEstimateEffortSubItemsSumBuffered.getValue()).longValue();
//        } else {
////            return effortEstimate;
////            Long effort = getLong(PARSE_EFFORT_ESTIMATE);
//////            if (MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() != 0 && (effort == null || effort == 0)) {
//////                return MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS;
//////            } else {
////            return (effort == null) ? 0L : effort;
//            return getEffortEstimateFromParse();
////            }
//        }
//    }
//</editor-fold>
    public long getEstimateForSubtasks() {
        long subItemSum = 0;
        for (Item item : (List<Item>) getListFull()) { //full to avoid that hidden (but not done) subtasks are not counted
//            if (true || !item.isDone()) { //NO, get estimates for ALL subtasks, whether done or not
            subItemSum += item.getEstimate();
//            }
        }
        return subItemSum;
    }

    ////////////// REMAINING ///////////////
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getEffortEstimate() {
//        long sum = 0;
//        for (Object o : getList()) {
//            if (o instanceof ItemAndListCommonInterface) {
//                sum += ((ItemAndListCommonInterface) o).getEffortEstimate();
//            }
//        }
//        return sum;
//    }
//</editor-fold>
//    public int getEffortEstimateInMinutes() {
//        return (int) getEffortEstimate() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
    private void setRemainingInParse(long remainingEffortTotalMillis) {
        if (remainingEffortTotalMillis > 0) {
            put(PARSE_REMAINING_EFFORT, remainingEffortTotalMillis); //update first 
        } else {
            remove(PARSE_REMAINING_EFFORT);
        }
        updateEarnedValuePerHour();
    }

    /**
     *
     * @param remainingEffortProjectTaskItselfMillis
     * @param autoUpdateEffortEstimate
     * @param forProjectTaskItself set the effort for this task even though it
     * is a project with subtasks
     */
    private void setRemainingForProjectTaskItselfInParse(long remainingEffortProjectTaskItselfMillis) {
        if (remainingEffortProjectTaskItselfMillis != 0) {
            put(PARSE_REMAINING_EFFORT_PROJECT_TASK_ITSELF, remainingEffortProjectTaskItselfMillis); //update first 
        } else {
            remove(PARSE_REMAINING_EFFORT_PROJECT_TASK_ITSELF);
        }
//        updateEarnedValuePerHour(); //NO, only update earnedVlauePerHour when the total effort is changed (done automatically when remainingForProjectTask is updated)
//        update();
    }

    private void setRemainingImpl(long remainingEffortProjectTaskItselfMillis) {
        long oldRemaining = getRemaining();

        setRemainingForProjectTaskItselfInParse(remainingEffortProjectTaskItselfMillis);

        long totalRemainingEffort;
        if (isProject()) {
            long subtasksRemaining = getRemainingForSubtasks();
            if (subtasksRemaining > 0 && MyPrefs.estimateRemainingOnlyUseSubtasksRemaining.getBoolean()) {
                totalRemainingEffort = subtasksRemaining; //getRemainingEffortFromSubtasks();
            } else {
                totalRemainingEffort = remainingEffortProjectTaskItselfMillis + subtasksRemaining; //getRemainingEffortFromSubtasks();
            }
        } else {
            totalRemainingEffort = remainingEffortProjectTaskItselfMillis;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (totalRemainingEffort != 0) {
//            put(PARSE_REMAINING_EFFORT, totalRemainingEffort); //update first
//        } else {
//            remove(PARSE_REMAINING_EFFORT);
//        }
//</editor-fold>
        setRemainingInParse(totalRemainingEffort);
//        update();
        Item owner = getOwnerItem();
        if (owner != null && oldRemaining != totalRemainingEffort) {
//            owner.updateRemaining(oldRemaining, totalRemainingEffort);
            owner.updateRemainingOnSubtaskChange();
            DAO.getInstance().saveInBackground(owner);
//<editor-fold defaultstate="collapsed" desc="comment">
//            long currentProjectRemainingEffort = owner.getRemainingEffort();
//            long currentProjectRemainingEffortInParse = owner.getRemainingEffortFromParse();
//            if (currentProjectRemainingEffort != currentProjectRemainingEffortInParse) {
//                owner.setRemainingEffortInParse(currentProjectRemainingEffort);
//                DAO.getInstance().saveInBackground(owner);
//            }
//</editor-fold>
        }
    }

    /**
     * set remaining effort the the project task itself (it doesn't make sense
     * to set effort for subtasks at project level)
     *
     * @param remainingEffortProjectTaskItselfMillis
     * @param autoUpdateEffortEstimate
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setRemainingEffortXXX(long remainingEffortMillis, boolean autoUpdateEffortEstimate, boolean forProjectTaskItself) {
////#mdebug
////        ASSERT.that(remainingEffort >= 0, "RemainingEffort cannot be negative");
//        assert remainingEffortMillis >= 0 : "RemainingEffort cannot be negative";
////#enddebug
////        this.effortEstimate = val;
//        if (forProjectTaskItself || !isProject()) { //don't save for projects
//            long prevRemaining = getRemainingEffort();
////            if (remainingEffortMillis != 0) {
////                put(PARSE_REMAINING_EFFORT, remainingEffortMillis); //update first
////            } else {
////                remove(PARSE_REMAINING_EFFORT);
////            }
//            setRemainingEffortInParse(remainingEffortMillis);
//            if (prevRemaining != remainingEffortMillis) {
////            if (autoUpdateEffortEstimate && prevRemaining == 0) {
////            if (getEffortEstimate() == 0 && Settings.getInstance().isAlwaysSetFirstEstimateToInitialEstimate()) { //if no previous estimate, use Remaining
//                //UI: if no effort estimate has been set, then use Remaining+Actual as historical estimate (
//                //TODO: this requires Estimate and Actual to be set *before* setting Remaining
////                if (getEffortEstimate() == 0 && Settings.getInstance().alwaysUseRemainingAsEstimateWhenActualIsZero()) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
//                if (autoUpdateEffortEstimate && prevRemaining == 0 && getEffortEstimate() == 0
//                        && MyPrefs.getBoolean(MyPrefs.automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero)) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
//                    setEffortEstimateXXX(remainingEffortMillis + getActualEffort(false), false, forProjectTaskItself); //since we test for 0, no problem if setting Estimate both here and direct
//                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (getActualEffort() == 0 && Settings.getInstance().alwaysUseRemainingAsEstimateWhenActualIsZero()) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
////                    setEstimate(remainingEffort, false); //since we test for 0, no problem if setting Estimate both here and direct
////                }
////            }
////            if (remainingEffort == 0 && !isDone() && Settings.getInstance().markDoneIfRemainingReducedToZero()) {
////                setDone(true); //UI: reducing Remaining to 0 takes precedence over setting Done status to other (non-Done) values
////            }
////            this.remainingEffort = val;
////            if (has(PARSE_REMAINING_EFFORT) || remainingEffort != 0) {
//////                put(PARSE_REMAINING_EFFORT, new Date(remainingEffort));
////                put(PARSE_REMAINING_EFFORT, remainingEffort);
////            }
////</editor-fold>
//            }
//        }
//    }
//</editor-fold>
    public void setRemaining(long remainingEffortProjectTaskItselfMillis, boolean autoUpdateEffortEstimate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        long oldEffortTotal = getRemainingEffort();
////        long prevRemaining = getRemainingEffortFromParse();
//        long effortSubtasks = getRemainingEffortFromSubtasks();
//        long newEffortTotal = effortSubtasks + remainingEffortMillis;
//        long prevRemainingProjectTask = getRemainingEffortProjectTaskItself();
//</editor-fold>
        if (autoUpdateEffortEstimate && getRemainingForProjectTaskItselfFromParse() == 0 //if first time we set Remaining
                && MyPrefs.automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero.getBoolean()
                && getEstimate() == 0 //and no effort estimate already set
                ) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
            //since we test for 0, no problem if setting Estimate both here and direct
            setEstimate(remainingEffortProjectTaskItselfMillis + getActualForProjectTaskItself(), false); //false to avoid circular updates between setEstimate() and setRemaining()
        }

//        setRemainingEffortInParse(newEffortTotal);// + actualEffortMillis);
        setRemainingImpl(remainingEffortProjectTaskItselfMillis);// + actualEffortMillis);
//<editor-fold defaultstate="collapsed" desc="comment">
//        setRemainingEffortProjectTaskItselfInParse(remainingEffortMillis);

//        if (newEffortTotal != oldEffortTotal) {
//            update();
//        }
//</editor-fold>
    }

    public void setRemaining(long remainingEffortProjectTaskItselfMillis) {
//        setRemaining(remainingEffortProjectTaskItselfMillis, true);
        setRemaining(remainingEffortProjectTaskItselfMillis, false); //false=> don't update the other field by default, only if explicitly defined
    }

    /**
     * called by subtasks when their remaining change
     *
     * @param oldRemainingSubtaskEffort
     * @param newRemainingSubtaskEffort
     */
    protected void updateRemaining(long oldRemainingSubtaskEffort, long newRemainingSubtaskEffort) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        long currentProjectRemainingEffort = getRemainingEffortProjectTaskFromParse();
//         currentProjectRemainingEffort = getRemainingEffortProjectTaskItself();
//        long currentProjectRemainingEffortInParse = owner.getRemainingEffortFromParse();
//        if (currentProjectRemainingEffort != currentProjectRemainingEffortInParse) {
//            owner.setRemainingEffortInParse(currentProjectRemainingEffort);
//            DAO.getInstance().saveInBackground(owner);
//        }
//        long subtaskRemainingEffort = getRemainingEffortFromSubtasks();
//</editor-fold>
        long currentRemainingEffort = getRemaining();
//        if (currentProjectRemainingEffort != currentProjectRemainingEffortInParse) {
//        setRemainingEffortInParse(currentProjectRemainingEffort + subtaskRemainingEffort);
//        setRemaining(currentRemainingEffort - oldRemainingSubtaskEffort + newRemainingSubtaskEffort);
        setRemainingInParse(currentRemainingEffort - oldRemainingSubtaskEffort + newRemainingSubtaskEffort);
        //DAO.getInstance().saveInBackground(this); //save is done in the task calling updateRemaining
    }

    protected void updateRemainingOnSubtaskChange() {
        setRemaining(getRemainingForProjectTaskItselfFromParse(), true);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setRemainingEffortXXX(long remainingEffortMillis) {
//        setRemainingEffortXXX(remainingEffortMillis, false, false);
//    }
//    public void setRemainingEffortInMinutes(int val) {
//        setRemainingEffortXXX(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
//    public int getRemainingEffortInMinutes() {
//        return (int) getRemainingEffort() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
//    public long getRemainingEffort(boolean forSubtasks) {
//        return getRemainingEffort(forSubtasks, true);
//    }
//</editor-fold>
    /**
     * returns the TOTAL remaining effort (subtasks+project task itself)
     *
     * @return
     */
    private long getRemainingFromParse() {
        Long remainingEffortTotal = getLong(PARSE_REMAINING_EFFORT);
        return (remainingEffortTotal == null) ? 0L : remainingEffortTotal;
    }

//    private long getRemainingEffortProjectTaskFromParse() {
    public long getRemainingForProjectTaskItselfFromParse() {
        Long remainingProjectTaskItselfEffort = getLong(PARSE_REMAINING_EFFORT_PROJECT_TASK_ITSELF);
        return (remainingProjectTaskItselfEffort == null) ? 0L : remainingProjectTaskItselfEffort;
    }

    public long getRemainingForProjectTaskItself(boolean useDefaultEstimateForZeroEstimates) {
        long effort = getRemainingForProjectTaskItselfFromParse();
        if (useDefaultEstimateForZeroEstimates && effort == 0) {
            return ((long) MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS;
        } else {
            return effort;
        }
    }

    public long getRemainingForProjectTaskItself() {
        return getRemainingForProjectTaskItself(true); //by default, 
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getRemainingEffortProjectTaskItself() {
////        if (isProject()) {
////            return getRemainingEffortProjectTaskFromParse();
////        } else {
////            return getRemainingEffortFromParse();
////        }
//        return getRemainingEffortProjectTaskFromParse();
//    }
//</editor-fold>

    public long getRemainingDefaultValue() {
        return ((long) MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS;
    }

    public long getRemaining(boolean useDefaultEstimateForZeroEstimates, boolean returnZeroForDoneTasks) {
        if (returnZeroForDoneTasks && isDone()) {
            return 0;
        }
        long effort = getRemainingFromParse();
//<editor-fold defaultstate="collapsed" desc="comment">
////        if (forSubtasks && getItemListSize() > 0) {
//        if ( isProject()) {
////            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_REMAINING); //optimization: store sum in intermediate variable to avoid recalculating each time
////            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue()).longValue();
////            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue());
////            long subItemSum = 0;
//////            for (Object i : subitems) {
//////            for (Object i : getItemList()) {
////            for (Object i : getList()) {
////                Item item = (Item) i;
////                if (!item.isDone()) {
//////                    subItemSum += item.getRemainingEffort(forSubtasks);
////                    subItemSum += item.getRemainingEffort(forSubtasks, useDefaultEstimateForZeroEstimates);
////                }
////            }
////            return subItemSum;
//            return getRemainingEffortFromParse()+getRemainingEffortFromSubtasks();
//        } else {
////            return remainingEffort;
//            Long remainingEffort = getLong(PARSE_REMAINING_EFFORT);
//            if (useDefaultEstimateForZeroEstimates //&& MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() != 0 //OK to use default value even if zero (gives 0 in any case)
//                    && (remainingEffort == null || remainingEffort == 0)) {
//                return ((long) MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS;
//            } else {
//                return (remainingEffort == null) ? 0L : remainingEffort;
//            }
//        }
//</editor-fold>
        if (useDefaultEstimateForZeroEstimates && effort == 0) {
//            return ((long) MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS;
            return getRemainingDefaultValue();
        } else {
            return effort;
        }
    }

//    public long getRemainingEffort(boolean forSubtasks, boolean useDefaultEstimateForZeroEstimates) {
    public long getRemaining(boolean useDefaultEstimateForZeroEstimates) {
        return getRemaining(useDefaultEstimateForZeroEstimates, true);
    }

    /**
     * return total remaining effort for this task (remaining of all undone
     * subtasks and this project task itself).
     *
     * @return
     */
    @Override
    public long getRemaining() {
//        if (isDone()) {
//            return 0;
//        }
//        return getRemainingEffortFromParse();
        return getRemaining(true, true);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getRemainingEffortNoDefault() {
////        if (isDone()) {
////            return 0;
////        }
//        return getRemainingEffort(false);
//    }
//</editor-fold>

    public long getRemainingForSubtasks() {
        long effort = 0;
        for (Item item : (List<Item>) getListFull()) {
            effort += item.getRemaining(); //remainingEffort  returns complete actual effort for subtasks (including their own effort and that of any of their subtasks
        }
        return effort;
    }

    public boolean isRemainingDefaultValue() {
        return getRemainingForProjectTaskItself() == getRemainingDefaultValue();
    }

    ////////////// ACTUAL ///////////////
    private void setActualForProjectTaskItselfInParse(long actualEffortProjectTaskItselfMillis) {
//        long rounded = ((actualEffortProjectTaskItselfMillis + 500) / 1000) * 1000; //https://stackoverflow.com/questions/20385067/how-to-round-off-timestamp-in-milliseconds-to-nearest-seconds
        long rounded = actualEffortProjectTaskItselfMillis; //https://stackoverflow.com/questions/20385067/how-to-round-off-timestamp-in-milliseconds-to-nearest-seconds
        if (rounded != 0) {
            put(PARSE_ACTUAL_EFFORT_PROJECT_TASK_ITSELF, rounded);
        } else {
            remove(PARSE_ACTUAL_EFFORT_PROJECT_TASK_ITSELF);
        }
//        update(); //DONE at the caller
    }

//    private void setActualEffortTotalInParse(long actualEffortMillis) {
//        setActualEffortTotalInParse(actualEffortMillis, true);
//    }
    private void setActualInParse(long actualEffortTotalMillis) {
//        long rounded = ((actualEffortTotalMillis + 500) / 1000) * 1000; //https://stackoverflow.com/questions/20385067/how-to-round-off-timestamp-in-milliseconds-to-nearest-seconds
        long rounded = actualEffortTotalMillis; //no need to round, picker now handles millis - https://stackoverflow.com/questions/20385067/how-to-round-off-timestamp-in-milliseconds-to-nearest-seconds
        if (rounded > 0) {
            put(PARSE_ACTUAL_EFFORT, rounded);
        } else {
            remove(PARSE_ACTUAL_EFFORT);
        }
        updateEarnedValuePerHour();
    }

    private void setActualImpl(long actualEffortTotalMillis, boolean autoUpdateStatusAndStartedOnDate) {

        long oldActualEffort = getActual();
//<editor-fold defaultstate="collapsed" desc="comment">
//        long actualEffortSubtasks = getActualEffortFromSubtasks();
//        long totalActualEffort = actualEffortMillis + actualEffortSubtasks;

//        if (totalActualEffort != 0) {
//        if (actualEffortMillis > 0) {
//            put(PARSE_ACTUAL_EFFORT, actualEffortMillis);
//            //if setting Actual to a positive value (larger than zero) for the first time, and status==Created, set to Ongoing
////            if (getActualEffort() == 0 && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
//            if (autoUpdateStatusAndStartedOnDate && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
//                setStatus(ItemStatus.ONGOING); //automatically set to Ongoing as soon as time is spent on the task; setStatus set startedOn date
//            }
//        } else {
//            remove(PARSE_ACTUAL_EFFORT);
//            //if Actual is reduced to zero then set status back to Created and reset StartedOn date
////            if (actualEffortMillis == 0 && getActualEffort() > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
//            if (autoUpdateStatusAndStartedOnDate && getStatus() == ItemStatus.ONGOING && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
//                setStatus(ItemStatus.CREATED);
//            }
//        }
//</editor-fold>
        if (autoUpdateStatusAndStartedOnDate) {
            if (actualEffortTotalMillis > 0) {
                //if setting Actual to a positive value (larger than zero) for the first time, and status==Created, set to Ongoing
//            if (getActualEffort() == 0 && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
//            if (autoUpdateStatusAndStartedOnDate && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
                if (getStatus() == ItemStatus.CREATED) {
                    setStatus(ItemStatus.ONGOING); //automatically set to Ongoing as soon as time is spent on the task; setStatus set startedOn date
                }
            } else {
                //if Actual is reduced to zero then set status back to Created and reset StartedOn date
//            if (actualEffortMillis == 0 && getActualEffort() > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
//            if (autoUpdateStatusAndStartedOnDate && getStatus() == ItemStatus.ONGOING && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
                if (getStatus() == ItemStatus.ONGOING && oldActualEffort > 0) { //TODO replace use of Settings by MyPrefs, only change status back from ongoing to created if an actual effort was already set
                    setStatus(ItemStatus.CREATED);
                }
            }
        }

        setActualInParse(actualEffortTotalMillis);

//        if (actualEffortMillis != oldActualEffort) {
        Item ownerProject = getOwnerItem(); //only relevant to update owners when it is a project (only projects keep total of subtasks)
//            update();
        if (ownerProject != null && actualEffortTotalMillis != oldActualEffort) {
            long actualIncrease = actualEffortTotalMillis - oldActualEffort;
//            ((Item) ownerProject).updateActualOnSubtaskChange(oldActualEffort, actualEffortTotalMillis);
            ((Item) ownerProject).updateActualOnSubtaskChange(actualIncrease);
//            ((Item) ownerProject).updateActualOnSubtaskChange();
            DAO.getInstance().saveInBackground(ownerProject);
        }
//        }
//        update(); //DONE at the caller
    }

    //TODO! consider the use of autoUpdateStatusAndStartedOnDate (all effort methods!)
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void setActualEffortXXX(long actualEffortMillis, boolean autoUpdateStatusAndStartedOnDate, boolean forProjectTaskItself) {
//        long oldActualEffortProjectTaskItself = getActualForProjectTaskItself();
////        ASSERT.that(actualEffort >= 0, "ActualEffort cannot be negative");
//        ASSERT.that(actualEffortMillis >= 0, "ActualEffort cannot be negative");
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (forProjectTaskItself || !isProject()) {
////            if (getActualEffort() != actualEffortMillis) {
////                if (autoUpdateStatusAndStartedOnDate) {
////                    if (actualEffortMillis > 0) {
////                        //if setting Actual to a positive value (larger than zero) for the first time, and status==Created, set to Ongoing
////                        if (getActualEffort() == 0 && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
////                            setStatus(ItemStatus.ONGOING); //automatically set to Ongoing as soon as time is spent on the task; setStatus set startedOn date
////                        }
////                    } else {//if Actual is reduced to zero then set status back to Created and reset StartedOn date
////                        if (actualEffortMillis == 0 && getActualEffort() > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
////                            setStatus(ItemStatus.CREATED);
////                        }
////                    }
////                }
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (has(PARSE_ACTUAL_EFFORT) || actualEffort != 0) {
//////                    put(PARSE_ACTUAL_EFFORT, actualEffort);
//////                }
//////                if (actualEffortMillis != 0) {
//////                    put(PARSE_ACTUAL_EFFORT, actualEffortMillis);
//////                } else {
//////                    remove(PARSE_ACTUAL_EFFORT);
//////                }
//////</editor-fold>
////                setActualEffortTotalInParse(getActualEffort() - oldActualEffortProjectTaskItself + actualEffortMillis,autoUpdateStatusAndStartedOnDate); //adjust totoal effort, avoids
////                setActualEffortProjectTaskItselfInParse(actualEffortMillis);
////            }
////        }
////</editor-fold>
//        setActualImpl(getActual() - oldActualEffortProjectTaskItself + actualEffortMillis, autoUpdateStatusAndStartedOnDate); //adjust totoal effort, avoids
//        setActualForProjectTaskItselfInParse(actualEffortMillis);
//    }
//</editor-fold>
    public void setActual(long actualEffortProjectTaskItselfMillis, boolean autoUpdateStatusAndStartedOnDate) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        setActualEffort(actualEffortMillis, false, false);
//        if (isProject()) {
//            setActualEffortProjectTaskItselfInParse(actualEffortMillis);
//            setActualEffortInParse(getActualEffortFromSubtasks());// + actualEffortMillis);
//        } else {
//            setActualEffortInParse(actualEffortMillis);
//        }
//        long oldActualEffortTotal = getActual();
//        long oldActualEffortProjectTaskItself = getActualForProjectTaskItself();
//        setActualImpl(oldActualEffortTotal - oldActualEffortProjectTaskItself + actualEffortProjectTaskItselfMillis, true); //adjust totoal effort, avoids recalculating sum of subtasks
//        setActualForProjectTaskItselfInParse(actualEffortProjectTaskItselfMillis);
//</editor-fold>
        setActualForProjectTaskItselfInParse(actualEffortProjectTaskItselfMillis);
        setActualImpl(actualEffortProjectTaskItselfMillis + getActualForSubtasks(), true); //adjust totoal effort, avoids recalculating sum of subtasks
    }

//    private void updateActualOnSubtaskChange(long oldTotalActual, long newTotalActual) {
//        setActualImpl(getActual() - oldTotalActual + newTotalActual, true);
//    }
    /**
     * update actual for a project when Actual for one single subtask change
     *
     * @param subTaskIncrease
     */
    private void updateActualOnSubtaskChange(long subTaskIncrease) {
        setActualImpl(getActual() + subTaskIncrease, true);
    }

    /**
     * recalculate actual for a project, eg when there any kind of change in the
     * subtasks
     */
    private void updateActualOnSubtaskChange() {
        setActualImpl(getActualForProjectTaskItself() + getActualForSubtasks(), true);
    }
//    private void updateActualOnSubtaskChange() {
//        setActualImpl(getActualForProjectTaskItself(), true);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void updateActualEffortOnSubtaskChangeXXX() {
//        long currentProjectActualEffortFromSubtasks = getActualEffortFromSubtasks();
//        long currentProjectActualEffortProjectTaskItself = getActualEffortProjectTaskItself();
//        long currentTotal = getActualEffortFromParse();
//        long newTotal = currentProjectActualEffortFromSubtasks + currentProjectActualEffortProjectTaskItself;
////            if (currentProjectActualEffortFromSubtasks != currentProjectActualEffortProjectTaskItself) {
//        if (newTotal != currentTotal) {
////                setActualEffortProjectTaskItselfInParse(currentProjectActualEffortProjectTaskItself); //store old Actual for project task itself
////            setActualEffortTotalInParseXXX(newTotal);
//        }
//    }
//</editor-fold>

    public long getActualForProjectTaskItself() {
        Long actualEffort = getLong(PARSE_ACTUAL_EFFORT_PROJECT_TASK_ITSELF);
        return (actualEffort == null) ? 0L : actualEffort;
    }

    @Override
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getActual() {
////        return getActualEffort(true);
//        return getActualFromParse();
//    }

//    public long getActualFromParse() {
//</editor-fold>
    public long getActual() {
        Long actualEffort = getLong(PARSE_ACTUAL_EFFORT);
        return (actualEffort == null) ? 0L : actualEffort;
    }

    /**
     * adds elapsedTime to the Actual of this Item
     *
     * @param elapsedTime
     */
//    public void addElapsedTimerTimeToActualXXX(long elapsedTime) {
//        setActual(getActualForProjectTaskItself() + elapsedTime, false);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * return the effort for the project task itself, that is, if this is not a
     * project, return actual effort PARSE_ACTUAL_EFFORT, if it *is* a project,
     * return the PARSE_ACTUAL_EFFORT_PROJECT_TASK_ITSELF
     *
     * @return
     */
//    public long getActualEffortProjectTaskItself() {
////        if (isProject()) {
////            return getActualEffortProjectTaskItself();
////        } else {
////            return getActualEffortFromParse();
////        }
//        return getActualEffortProjectTaskItself();
//    }
//    public void setActualEffortInMinutes(int val) {
//        setActualEffort(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
//</editor-fold>
    /**
     * returns actual time worked on task or project. For a project, it is the
     * sum of actual for subtasks PLUS actual for project itself (in case work
     * was done on it before adding subtasks)
     *
     * @param forSubtasks
     * @return
     */
    public long getActualForSubtasks() {
        long actual = 0;//getActualEffortProjectTaskItselfFromParse();
//        List subtasks = getList();
//        for (int i = 0, size = subtasks.size(); i < size; i++) {
//            Item item = (Item) subtasks.get(i);
        for (Item item : (List<Item>) getListFull()) {
//            if (true || !item.isDone()) { // /** || includeDone */) { //ALWAYS include actual even for Done tasks so project Actual is exhaustive
//                actual += item.getActualEffortFromSubtasks();
            actual += item.getActual(); //actuelEffort now returns complete actual effort for subtasks (including their own effort and that of any of their subtasks
//            }
        }
        return actual;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getActualEffort(boolean forSubtasks) {
//        return getActualEffortFromParse();
//    }
//    public long getActualEffortOLD(boolean forSubtasks) {
////        Long actual = getLong(PARSE_ACTUAL_EFFORT);
////        if (actual == null) {
////            actual = 0L;
////        }
//        Long actual = getActualEffortFromParse();
//        if (forSubtasks && isProject()) {
////            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ACTUAL); //optimization: store sum in intermediate variable to avoid recalculating each time
////            long subItemSum = 0;
////            long subItemSum = getActualEffort(false);
//            for (int i = 0, size = getItemListSize(); i < size; i++) {
////                Item item = (Item) getItemList().getItemAt(i);
//                Item item = (Item) getList().get(i);
//                if (true || !item.isDone()) { // /** || includeDone */) { //ALWAYS include actual even for Done tasks so project Actual is exhaustive
////                    subItemSum += item.getActualEffort(forSubtasks);
//                    actual += item.getActualEffortOLD(forSubtasks);
//                }
//            }
////            return subItemSum + getActualEffort(false); //UI: add any effort registered on the project (e.g. before it became a project, or by manual editing)
////            return ((Long) derivedActualEffortSubItemsSumBuffered.getValue()).longValue();
//        }
////        else {
//////            return actualEffort;
//////            Long actual = getLong(PARSE_ACTUAL_EFFORT);
////            return (actual == null) ? 0L : actual;
////        }
//        return actual;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public int getActualEffortInMinutes() {
//        return (int) getActualEffort() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
//    static private long getManualUpdateValueXXX(String textStr, String fieldName, long initValue) {
//        Duration duration = new Duration(initValue);
////        ScreenDurationPicker screenDurationPicker = new ScreenDurationPicker("Update Remaining?", duration, true, 1, "Update", textStr);
//////        screenDurationPicker.edit();
//////        screenDurationPicker.display();
//        return duration.getDuration();
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * if remaining effort is unchanged, and actual effort increased, then ask
     * if Actual should be auto-increased with the reduction as default value.
     * Returns value of new Remaining effort to set. Or -1 if not supposed to
     * set new value.
     */
//    static public long getIfAutoDecreaseRemainingEffortWhenActualIncreased(long newActualEffort, long oldActualEffort, long newRemainingEffort, long oldRemainingEffort) {
//        long res = -1;
//        if (newRemainingEffort == oldRemainingEffort && newActualEffort > oldActualEffort && Settings.getInstance().askToUpdateRemainingWhenActualIncreased()) {
//            long actualIncrease = newActualEffort - oldActualEffort;
//            long updatedRemaining = oldRemainingEffort - actualIncrease;
////            int res = getManualUpdateValue("You have increased " + Item.getFieldName(Item.FIELD_EFFORT_ACTUAL) /*+ " without updating " + Item.getFieldName(Item.FIELD_EFFORT_REMAINING)*/ + ". Update " + Item.getFieldName(Item.FIELD_EFFORT_REMAINING) + "?",
////            res = getManualUpdateValue(getFieldName(Item.FIELD_EFFORT_ACTUAL) + " increased. Reduce " + getFieldName(Item.FIELD_EFFORT_REMAINING) + " with " + Duration.formatDuration(actualIncrease, true) + "?", //TODO!!!: add "Reduce Remaining with 0:10 to [1:35]?"
//            res = getManualUpdateValue(getFieldName(Item.FIELD_EFFORT_ACTUAL) + " increased by " + Duration.formatDuration(actualIncrease, true)
//                    + ". Reduce " + getFieldName(Item.FIELD_EFFORT_REMAINING) + " by " + Duration.formatDuration(actualIncrease, true) + " to " + Duration.formatDuration(updatedRemaining, true) + "?", //TODO!!!: add "Reduce Remaining with 0:10 to [1:35]?"
//                    getFieldName(Item.FIELD_EFFORT_REMAINING), updatedRemaining);
////            if (res != -1) {
////            setRemainingEffortXXX(res);
////            }
//        }
//        return res;
//    }
    /**
     * if remaining effort is unchanged, and actual effort increased, then ask
     * if Actual should be auto-increased with the reduction as default value
     */
//    public void askIfAutoDecreaseRemainingEffortWhenActualIncreased(long newActualEffort, long oldActualEffort, long newRemainingEffort, long oldRemainingEffort) {
//        long remaining = getIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, oldActualEffort, newRemainingEffort, oldRemainingEffort);
//        if (remaining != -1) {
//            setRemainingEffortXXX(remaining);
//        }
//    }
    /**
     * encapsulates all the logic to do intelligent / automatic updates of
     * interrelated fields
     */
//    public void setEffortFieldsAndStatus(long newEffortEstimate, long newActualEffort, long newRemainingEffort, ItemStatus newStatus) {
//        if (newStatus == getStatus()) { //status unchanged
//            if (newEffortEstimate == getEffortEstimate()) { //only actual or remaining effort have changed
//                if (newRemainingEffort == getRemainingEffort()) { //only actual effort may have changed
////                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort);
//                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, getActualEffort(), newRemainingEffort, getRemainingEffort());
//                    setActualEffort(newActualEffort, false, false); //contains the logic to update
//                } else //remaining effort has changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only remaining effort may have changed
////                        askIfAutoUpdateActualEffortWhenRemainingReduced(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort); //-don't: it's counterintuitive - use increaseActual to record actuals, not decreaseRemaining
//                        setRemainingEffortXXX(newRemainingEffort);
//                    } else { //both actual and remaining effort have changed
//                        setRemainingEffortXXX(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
//                        setActualEffort(newActualEffort, false, false);
//                    }
//                }
//            } else //effort estimate has changed
//            {
//                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
//                    setEstimate(newEffortEstimate); //contains the logic to update
//                } else //remaining effort or actual effort have changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
//                        setEstimate(newEffortEstimate, false, false);
//                        setRemainingEffortXXX(newRemainingEffort, false, false);
//                    } else { //both actual and remaining effort have changed
//                        setEstimate(newEffortEstimate, false, false);
//                        setRemainingEffortXXX(newRemainingEffort, false, false);
//                        setActualEffort(newActualEffort, true, false); //do autoupdate status
//                    }
//                }
//            }
//        } else //status is modified
//        {
//            if (newEffortEstimate == getEffortEstimate()) { //only actual or remaining effort have changed
//                if (newRemainingEffort == getRemainingEffort()) { //only actual effort may have changed
////                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort);
//                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, getActualEffort(), newRemainingEffort, getRemainingEffort());
//                    setActualEffort(newActualEffort, false, false); //contains the logic to update
//                } else //remaining effort has changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only remaining effort may have changed
////                        askIfAutoUpdateActualEffortWhenRemainingReduced(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort); //-don't: it's counterintuitive - use increaseActual to record actuals, not decreaseRemaining
//                        setRemainingEffortXXX(newRemainingEffort);
//                    } else { //both actual and remaining effort have changed
//                        setRemainingEffortXXX(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
//                        setActualEffort(newActualEffort, false, false); //don't autoupdate status
//                    }
//                }
//            } else //effort estimate has changed
//            {
//                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
//                    setEstimate(newEffortEstimate); //contains the logic to update
//                } else //remaining effort or actual effort have changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
//                        setEstimate(newEffortEstimate, false, false);
//                        setRemainingEffortXXX(newRemainingEffort, false, false);
//                    } else { //both actual and remaining effort have changed
//                        setEstimate(newEffortEstimate, false, false);
//                        setRemainingEffortXXX(newRemainingEffort, false, false);
//                        setActualEffort(newActualEffort, false, false);
//                    }
//                }
//            }
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getShowFromDateXXX() {
////        return showFromDate;
//        Date date = getDate(PARSE_SHOW_FROM_DATE);
//        return (date == null) ? 0L : date.getTime();
//    }
//
//    public void setShowFromDateXXX(long showFromDate) {
////        this.showFromDate = val;
////        if (this.showFromDate != showFromDate) {
////            this.showFromDate = showFromDate;
////        }
////        if (has(PARSE_SHOW_FROM_DATE) || showFromDate != 0) {
////            put(PARSE_SHOW_FROM_DATE, new Date(showFromDate));
////        }
//        setShowFromDate(new Date(showFromDate));
//    }
//
//    public void setShowFromDateXXX(Date showFromDate) {
////        if (has(PARSE_SHOW_FROM_DATE) || showFromDate != 0) {
////            put(PARSE_SHOW_FROM_DATE, new Date(showFromDate));
////        }
//        if (showFromDate.getTime() != 0) {
//            put(PARSE_SHOW_FROM_DATE, showFromDate);
//        } else {
//            remove(PARSE_SHOW_FROM_DATE);
//        }
//    }
//</editor-fold>
//    public int getCategoriesSizeXXX() {
//        if (getCategories() == null) {
//            return 0;
//        } else {
//            return getCategories().size();
//        }
////        if (categories == null) { //) || categories.getSize() == 0) {
////            return 0;
////        } else {
////            return categories.getSize();
////        }
//    }
//</editor-fold>
    public List<Category> getCategories() {
//        List<Category> categories = DAO.getInstance().getCategories(this);
//        return (categories == null) ? new HashSet() : new HashSet(categories);
////        return getList("categories");
        List<Category> categories = getList(PARSE_CATEGORIES);
//        return (categories == null) ? new ArrayList() : categories;
//        return (categories == null) ? new HashSet() : new HashSet(categories);
//        return (categories == null) ? new ArrayList() : categories;
        if (categories != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(categories);
            return categories;
        } else {
            return new ArrayList();
        }
////        if (categories == null) {
////            categories = new ItemList(BaseItemTypes.CATEGORY, this, false, isEnsureItemCategoryAutoConsistency(), true, true); //ItemListSaveInline(this);
////        }
////        return categories;
    }

    static public List<String> convCategoryListToObjectIdList(List<Category> categoryList) {
        List<String> catIds = new ArrayList();
        if (categoryList != null) {
            for (Category c : categoryList) {
                catIds.add(c.getObjectIdP());
            }
        }
        return catIds;
    }

    static public List<Category> convCatObjectIdsListToCategoryList(List<String> categoryIdList) {
        List<Category> categories = new ArrayList();
        if (categoryIdList != null) {
            for (String c : categoryIdList) {
                categories.add(DAO.getInstance().fetchCategory(c));
            }
        }
        return categories;
    }

    /**
     * add category ids NOT already in the list
     *
     * @param categoryIdList
     * @return
     */
    static public List<String> addCatObjectIdsListToCategoryList(List<String> categoryIdList, List<Category> newCategoryList) {
        if(categoryIdList==null)
            categoryIdList = new ArrayList<>();
//        if (categoryIdList != null && newCategoryList != null) {
        if (newCategoryList != null) {
            for (Category c : newCategoryList) {
                if (!categoryIdList.contains(c.getObjectIdP())) {
                    if (Config.TEST) {
                        ASSERT.that(c.getObjectIdP() != null, "Adding category=" + c + ", for which objId=null");
                    }
                    categoryIdList.add(c.getObjectIdP());
                }
            }
        }
        return categoryIdList;
    }

    /**
     * updates this Item's categories (adds new ones,
     * deleteRuleAndAllRepeatInstancesExceptThis unselected ones) AVOID TO USE??
     * - only change categories via getCategories().update/remove...
     */
//    public void setCategories(Set<Category> categories) {
    public void setCategories(List<Category> categories) {
//        getCategories().updateListWithDifferences(categories);
//        put("categories", categories);
//        if (has(PARSE_CATEGORIES) || categories != null) {
//            put(PARSE_CATEGORIES, new ArrayList(categories));
//        }
        if (categories != null && !categories.isEmpty()) {
//            put(PARSE_CATEGORIES, new ArrayList(categories));
            put(PARSE_CATEGORIES, categories);
        } else { //categories == null || categories.isEmpty()
            remove(PARSE_CATEGORIES);
        }
    }

    /**
     * returns the first selected category (//TODO!!!! UI: the first one
     * selected or the first in the fixed visible list of categories???). used
     * to sort/group items in Statistics view
     *
     * @return
     */
    public Category getFirstCategory() {
        List<Category> categories = getCategories();
        if (categories.size() > 0) {
            return categories.get(0);
        } else {
            return null;
        }
    }

    /**
     * Called when updating an item after editing the categories locally.
     * Updates the set of categories for this Item and adds/removes this Item to
     * new/removed categories (and does NOT save the categories).
     *
     * @param locallyEditedCategories if null nothing is done (if empty all
     * categories are removed)
     * @param onlyAddNewCatsDontRemoveAny special option using when copying a
     * template into an existing item to avoid removing any already manually
     * added categories. Do NOT save the updated categories (since the item may
     * not be saved at the time the category is set)
     */
    public List<Category> updateCategories(List<Category> locallyEditedCategories, boolean onlyAddNewCatsDontRemoveAny) {
//        if (locallyEditedCategories == null || locallyEditedCategories.size() == 0) {
        if (locallyEditedCategories == null || locallyEditedCategories.isEmpty()) {
            return new ArrayList();
        }
        Item item = this;

//        Set<Category> addedCats = new HashSet(locallyEditedCategories);//make a copy of the edited set of categories
        List<Category> addedCats = new ArrayList(locallyEditedCategories);//make a copy of the edited set of categories
        addedCats.removeAll(item.getCategories()); //remove all that were already set of the item to get only the newly added categories
        for (Category cat : addedCats) {
//            cat.addItemAtIndex(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : cat.getSize());
            cat.addItemToCategory(item, false);
//            DAO.getInstance().saveInBackground((ParseObject) cat);
        }

        List<Category> unSelectedCats = null;
        if (!onlyAddNewCatsDontRemoveAny) {
//            Set<Category> unSelectedCats = new HashSet(item.getCategories());
//            unSelectedCats = new ArrayList(Arrays.asList(item.getCategories()));
            unSelectedCats = new ArrayList(item.getCategories());
            unSelectedCats.removeAll(locallyEditedCategories); //remove the categories that are still selected after editing. Those remaining in unSelectedCats have been unselected by user and should be removed
            for (Category cat : unSelectedCats) {
//                cat.remove(item);
                cat.removeItemFromCategory(item, false);
//                DAO.getInstance().saveInBackground((ParseObject) cat);
            }
            item.setCategories(new ArrayList(locallyEditedCategories)); //set the item's categories as the locally edited ones
        } else {
            //only set the item's categories to the old ones + the newly added
            ArrayList<Category> existingCatsPlusAdded = new ArrayList(item.getCategories());
//            existingCatsPlusAdded.addAll(addedCats);
            for (Category cat : addedCats) {
                if (!existingCatsPlusAdded.contains(cat)) {
                    existingCatsPlusAdded.add(cat);
                }
            }
            item.setCategories(existingCatsPlusAdded); //set the item's categories as the old ones + newly added ones
        }
        addedCats.addAll(unSelectedCats);
        return addedCats;
    }

    /**
     *
     * @param locallyEditedCategories
     * @return returns list of changed (added or removed) categories, eg to
     * easily save only changed categories
     */
    public List<Category> updateCategories(List<Category> locallyEditedCategories) {
        return updateCategories(locallyEditedCategories, false);
    }

    /**
     * adds category to this item's categories if not already there (no
     * duplicates). Does not save category
     */
    public void addCategoryToItem(Category category, boolean addItemToCategory) {
        if (category != null) {
            List<Category> cats = getCategories();
            if (!cats.contains(category)) {
                cats.add(category);
                setCategories(cats);
            }
            if (addItemToCategory) {
                category.addItemToCategory(this, false);
            }
        }
//        this.addUniqueToArrayField(PARSE_CATEGORIES, category); //TODO: will addUniqueToArrayField create the list if not already existing?
    }

    /**
     * adds category to this item's categories if not already there (no
     * duplicates)
     *
     * @param category
     */
    public void removeCategoryFromItem(Category category, boolean removeItemFromCategory) {
        if (category != null) {
            List<Category> cats = getCategories();
//            cats.remove(this);
            cats.remove(category);
            setCategories(cats);
            if (removeItemFromCategory) {
                category.removeItemFromCategory(this, false);
            }
        }
//        this.removeFromArrayField(PARSE_CATEGORIES, category);
    }

    private static <T> Set<T> diff(final Set<? extends T> s1, final Set<? extends T> s2) {
        Set<T> symmetricDiff = new HashSet<T>(s1);
        symmetricDiff.addAll(s2);
        Set<T> tmp = new HashSet<T>(s1);
        tmp.retainAll(s2);
        symmetricDiff.removeAll(tmp);
        return symmetricDiff;
    }

    /**
     * called when the selected categories for a task has been changed to but
     * add/remove the task to the respective categories and update the saved
     * categories for the task.
     */
//    public void updateCategoriesXXX(Set<Category> newCategoriesSet) {
//        Set<Category> oldCategoriesSetCopy = new HashSet(getCategories());
//        Set<Category> newCategoriesSetCopy = new HashSet(newCategoriesSet);
//    }
    /**
     * if commentString is non-empty, then add it to comment. If comment is
     * non-empty, then add a newline to separate the old comment and the
     * commentString. If addToEnd is true, then commentString is added to the
     * end, otherwise to the beginning.
     */
    static public String addToComment(String comment, String addString, boolean addToEnd) {
//        better not to test for null to catch this error condition
//        if (commentString == null | commentString.equals("")) {
        if (addString.equals("")) {
            return comment;
        } else //            String comment = getComment();
        if (addToEnd) {
//                setComment(comment + (comment.equals("") ? "" : "\n") + addString); //only add newline if comment already contains text
            //TODO!! check if existing comment ends with a newline and don't add one if so (to avoid having an empty line)
            //TODO option: create a setting to add *two* newlines to separate entries from each other
            return comment + (comment.equals("") ? "" : "\n") + addString; //only add newline if comment already contains text
        } else {
//                setComment(addString + (comment.equals("") ? "" : "\n") + comment);
            return addString + (comment.equals("") ? "" : "\n") + comment;
        }
//        if (!commentString.equals("")) {
//            String oldComment = getComment();
//            if (addToEnd) {
//                setComment(oldComment + (oldComment.equals("") ? "" : "\n") + commentString);
//            } else {
//                setComment(commentString + (oldComment.equals("") ? "" : "\n") + oldComment);
//            }
//        }
    }

    /**
     * adds a comment to the default position (End/Beginning based on user
     * preference)
     *
     * @param comment
     * @param addString
     * @return
     */
    static public String addToCommentDefaultPosition(String comment, String addString) {
        return addToComment(comment, addString, !MyPrefs.getBoolean(MyPrefs.commentsAddToBeginningOfComment));
    }

    static public String addTimeToComment(String comment) {
//        return addToComment(comment, MyDate.formatDateL10NShort(System.currentTimeMillis(), 
//                MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime)) + ": ", !MyPrefs.getBoolean(MyPrefs.commentsAddToBeginningOfComment));
        return addToComment(comment,
                //                MyDate.formatDateL10NShort(System.currentTimeMillis()) + (MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime) ? " " + MyDate.formatTime(System.currentTimeMillis()) : "") + ": ",
                //below: % MyDate.DAY_IN_MILLISECONDS quick hack to to only show time for hours/minutes
                //                MyDate.formatDateL10NShort(System.currentTimeMillis()) + (MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime) ? " " + MyDate.formatTimeOfDay(System.currentTimeMillis() % MyDate.DAY_IN_MILLISECONDS) : "") + ": ",
                //                MyDate.formatDateNew(System.currentTimeMillis()) + (MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime) ? " " + MyDate.formatTimeOfDay(System.currentTimeMillis() % MyDate.DAY_IN_MILLISECONDS) : "") + ": ",
                MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime) ? MyDate.formatDateTimeNew(new MyDate()) + ": "
                : MyDate.formatDateNew(new MyDate()) + ": ",
                !MyPrefs.getBoolean(MyPrefs.commentsAddToBeginningOfComment));
    }

    public long getCompletedDate() {
//        return completedDate;
//        Date date = getDate(PARSE_COMPLETED_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getCompletedDateD().getTime();
    }

    private Date getCompletedDateDInParse() {
        Date date = getDate(PARSE_COMPLETED_DATE);
        return (date == null) ? new Date(0) : date;
    }

    /**
     * returns the completed date. For completed (sub-)projects, this is the
     * latest completed date of any subprojects or tasks. Or Date(0) for
     * (sub-)projects not completed yet.
     *
     * @return
     */
    public Date getCompletedDateD() {
        //TODO!! whenever saving a (sub-)project, calculate the CompletedDate and save it to allow queries. Really needed? Maybe enough to query on actual leaf-subtasks?
//        return new Date(getCompletedDate());
        if (!isProject()) {
//            Date date = getDate(PARSE_COMPLETED_DATE);
//            return (date == null) ? new Date(0) : date;
            return getCompletedDateDInParse();
        } else { //isProject
            if (isDone()) {
                //TODO!!! //optimization: normally, the project should be set Completed when the *last* subtask is completed, so iterating through subtasks as below shouldn't be necessary?!
//                Date latestSubTaskCompletedDate = new Date(MyDate.MIN_DATE);
                Date latestSubTaskCompletedDate = new Date(0);
                for (Object item : getListFull()) { //full to ensure we get the latest date, no matter status
                    if (item instanceof Item && ((Item) item).getCompletedDateD().getTime() > latestSubTaskCompletedDate.getTime()) {
                        latestSubTaskCompletedDate = ((Item) item).getCompletedDateD();
                    }
                }
                return latestSubTaskCompletedDate;
            } else {
                return new Date(0);
            }
        }
    }

    /**
     * manually set a completedDate, e.g. if you've set a task to done later
     * than you actually finished it and want the right date for statistics.
     * Will set the task to Done it not already the case.
     *
     * @param completedDate
     */
    public void setCompletedDate(long completedDate) {
//        this.completedDate = val;
//        if (this.completedDate != completedDate) {
//            long oldVal = this.completedDate;
//            this.completedDate = completedDate;
//        if (has(PARSE_COMPLETED_DATE) || completedDate != 0) {
////            ASSERT.that(completedDate != 0); //OK to set completed to 0 if a task is set undone and there is no actualtime
//            put(PARSE_COMPLETED_DATE, new Date(completedDate));
//        }
//        if (completedDate == 0 && isDone()) { 
////ignore setting completedDate to 0 unless status!= Done
//        }else { //
//        if (completedDate == 0 && isDone()) { 
//        if (completedDate != 0 || !isDone()) { //ignore setting completedDate to 0 unless status!= Done
//            setCompletedDateImpl(completedDate);
//        } // else isDone() || 
//        //TODO: set task to FIELD_DONE if completedDate was 0 before
//        if (completedDate != 0L && !isDone()) {
/////** oldVal == 0L && */
////                completedDate != 0L && getStatus() != ItemStatus.STATUS_DONE && getStatus() != ItemStatus.STATUS_CANCELLED) {
//            setDone(true);
//        }
        setCompletedDate(new Date(completedDate));
    }

    public void setCompletedDate(long completedDate, boolean updateStatus) {
        setCompletedDate(new Date(completedDate), updateStatus);
    }

    public void setCompletedDate(Date completedDate, boolean updateStatus) {
//        if (has(PARSE_COMPLETED_DATE) || completedDate != 0) {
//            if (completedDate == 0) {
//                remove(PARSE_COMPLETED_DATE); //delete when setting to default value
//            } else {
//                put(PARSE_COMPLETED_DATE, new Date(completedDate));
//            }
//        }
//        if (completedDate.getTime() != 0) {
//            put(PARSE_COMPLETED_DATE, completedDate);
//            if (!isDone()) {
//                setDone(true);
//            }
//        } else {
//            remove(PARSE_COMPLETED_DATE); //delete when setting to default value
//            if (isDone()) {
//                setDone(false);
//            }
//        }
//        if (completedDate.getTime() != 0) {
        if (updateStatus) {
            setDone(completedDate.getTime() != 0);
        }
        setCompletedDate(completedDate);
    }

    public void setCompletedDate(Date completedDate) {
        setCompletedDateInParse(completedDate);
    }

    public void setCompletedDateInParse(Date completedDate) {
        if (isProject()) {
            Date oldProjectDate = getDate(PARSE_COMPLETED_DATE);
            for (Item subtask : (List<Item>) getListFull()) { //full set even for hidden subtasks
                Date oldSubtaskDate = subtask.getCompletedDateD();
//<editor-fold defaultstate="collapsed" desc="fully developed decision tree for when to update subtasks">
//                if (oldDate == null) {
//                    if (subtaskDate == null) {
//                        subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
//                    } else {
//                        //do nothing: subtask already had a date set before project got one
//                    }
//                } else { //oldDate!=null - a previous project date was set
//                    if (oldDate.equals(subtaskDate) || subtaskDate == null) { //subtaskDate==null => maybe inheritance has just been turned on?!
//                        subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                    } else { // !oldDate.equals(subtaskDate) && subtaskDate!=null
//                        //do nothing (subtask had a defined date already and it was different from old project date)
//                    }
//                }
//                if (oldDate == null && subtaskDate == null) {
//                    subtask.setDueDate(dueDate); //no previous project date was set, no subtask date was set, so update subtask to new (inherited) project date
//                } else if (subtaskDate == null || subtaskDate.equals(oldDate)) { //subtaskDate==null => maybe inheritance has just been turned on?!
////oldDate!=null - a previous project date was set
//                    subtask.setDueDate(dueDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
//                }
//</editor-fold>
                //above expressions are equivalent to this reduced/simplified version:
                if ((oldProjectDate == null && oldSubtaskDate == null) || (oldSubtaskDate == null || oldSubtaskDate.equals(oldProjectDate))) { //subtaskDate==null => maybe inheritance has just been turned on?!
                    subtask.setCompletedDate(completedDate); //if dueDate==null (meanining project date is deleted, then all subtask dates equal to old project date will also be deleted
                }
            }
        }

        if (completedDate != null && completedDate.getTime() != 0) {
            put(PARSE_COMPLETED_DATE, completedDate);
        } else {
            remove(PARSE_COMPLETED_DATE); //delete when setting to default value
        }
//        update();
    }

    @Override
    public void setDeletedDate(Date dateDeleted) {
        if (dateDeleted != null && dateDeleted.getTime() != 0) {
//            if (isProject()) //NO, not here, is done in softDeleteImpl()
//                for (Item subtask : (List<Item>) getListFull()) { //full set even for hidden subtasks
//                    subtask.setDeletedDate(dateDeleted);
//                }
            put(PARSE_DELETED_DATE, dateDeleted);
        } else {
            remove(PARSE_DELETED_DATE); //delete when setting to default value
        }
    }

    @Override
    public Date getDeletedDateN() {
        Date date = getDate(PARSE_DELETED_DATE);
//        return (date == null) ? new Date(0) : date;
        return date; //return null to indicate NOT deleted
    }

//    public void setCompletedDateD(Date completedDate) {
//        setCompletedDate(completedDate.getTime());
//    }
    public long getCreatedDate() {
//        return createdDate;
//        return getCreatedAt() == null ? 0 : getCreatedAt().getTime(); //createdAt returns null for just created object
        return getCreatedDateD().getTime();
    }

    public Date getCreatedDateD() {
//        return getCreatedAt();
        return getCreatedAt() == null ? new Date(0) : getCreatedAt(); //createdAt returns null for just created object
    }

    /**
     * set automatically by PaRSE, shouldn't be called
     *
     * @param val
     */
    public void setCreatedDatexx(long val) {
//        this.createdDate = val;
//        if (this.createdDate != val) {
//            this.createdDate = val;
//        }
    }

    public void setCreatedDateNoChangeCall(long val) {
//        this.createdDate = val;
//        if (this.createdDate != val) {
//            this.createdDate = val;
////            changed();
//        }
    }

//    @Override
//    public ItemList getListForNewCreatedRepeatInstances() { //TODO: replace with getParent()
////        if (this.getParent() instanceof ItemList) {
////            return (ItemList) this.getParent();
////        } else {
////            return null;
////        }
//        return (ItemList) getOwnerList(); //should always be a list?! Otherwise best to catch with via wrong typecasting
//    }
//    @Override
//    public void deleteRepeatInstance() {
////        delete();
//        DAO.getInstance().delete(this);
//    }
//    public long getAlarmDateXXX(int alarmId) {
//        if (alarmId == Item.FIELD_ALARM_DATE) {
//            return getAlarmDate();
//        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
//            return getWaitingAlarmDateD() != null ? getWaitingAlarmDateD().getTime() : 0;
//        } else {
//            return 0;
//        }
////        RuntimeException     ("Not supported yet.");
//    }
//
//    public String getAlarmTextXXX(int alarmId) {
//        if (alarmId == Item.FIELD_ALARM_DATE) {
//            return getText();
//        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
//            return getText();
//        } else {
//            return "";
//        }
////        ASSERT.that("Not supported yet.");
//    }
//    public String getAlarmIdText(int alarmId) {
//        if (alarmId == Item.FIELD_ALARM_DATE) {
//            return getFieldName(alarmId);
//        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
//            return getFieldName(alarmId);
//        } else {
//            return "";
//        }
////        ASSERT.that("Not supported yet.");
//    }
    /**
     * returns the int value used by getSumAt() to calculate the sum of lists.
     * Can be overwritten by eg. ItemLists to return some meaningful value to
     * add up in lists of lists
     */
//    @Override
//    public long getSumField(int fieldId) {
//        Object itemField;
//        if ((itemField = (((Item) this).getFilterField(fieldId))) instanceof Long) {
//            return ((Long) itemField).longValue();
//        } else {
//            return 0;
//        }
//    }
//
//    @Override
//    public long getSumField() {
//        return getSumField(FIELD_EFFORT_REMAINING);
//    }
//    @Override
//    public boolean ignoreSumField() {
//        return isDone() || getStatus() == ItemStatus.CANCELLED;  //ignore the sum of Done Items
//    }
    /**
     * returns expected total effort for this task. If the task is done, and
     * actual<>0, returns Actual, otherwise Estimate (ensures ROI can be
     * calculated for done tasks). If task is NOT done: Normally returns already
     * registered actual + remaining. If Remaining is zero (not used), then it
     * returns Max(Actual, Estimate) because assumption is that estimate is
     * still the most reliable measure for total effort as long as the task is
     * being worked on.
     *
     * @return
     */
    static long getTotalExpectedEffort(long remainingEffortMillis, long actualEffortMillis, long effortEstimateMillis) {
//        return getRemainingEffort() + getActualEffort();
        long totEff;// = 0;
        if (remainingEffortMillis != 0) {
            totEff = remainingEffortMillis + actualEffortMillis; //whether actual is zero or not, and whether estimate is larger or not
        } else {
            totEff = Math.max(actualEffortMillis, effortEstimateMillis);
        }
//            if (actualEffort > effortEstimate) {
//            totEff = actualEffort;
//        } else {
//            totEff = effortEstimate;
//        }
        return totEff;
    }

    long getTotalExpectedEffort() {
        return getTotalExpectedEffort(getRemaining(), getActual(), getEstimate());
    }

    /**
     * returns the time span of the task, that is the calendar time between it
     * was started and completed. If not completed, returns 0 (assuming that
     * startedDate then also is set). Useful for sorting completed tasks to
     * understand which tasks take the longest to complete. IS THIS REALLY
     * USEFUL? Only for statistics!
     *
     * @return
     */
    long getTimeSpan() {
        if (getCompletedDate() != 0) {
            return getCompletedDate() - getStartedOnDate();
        } else {
            return 0;
        }
    }

    /**
     * returns the time span of the task divided by total effort (dividing by
     * total effort gives a more significant view on whether the time span was
     * long). Useful for sorting completed tasks to understand which tasks take
     * the longest to complete. For example a task A require 7 days to complete
     * 2h of work, whereas task B took 1 day to complete 1h or work: A: 3,5, B:
     * 1 so A was harder to finish than B. Use of this: TODO!
     *
     * @return
     */
    long getTimeSpanDividedByEffort() {
        if (getTotalExpectedEffort() != 0) {
            return getTimeSpan() / getTotalExpectedEffort();
        } else {
            return 0;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * TODO!!!: extend to work for lists. Ff the owner of this
     * item/list/mergeList/category has a WorkTimeDefinition, then return the
     * finish time (based on the order of items in the owners list of tasks). If
     * the owner is a Project: if the project has work time defined, then
     * calculate the finish time of the project's subtasks based on the
     * sub-tasks relative position within the project (to do this it is
     * necessary to get the subtask's finish time directly from the
     * workTimeDefinition using start as Project's starttime+subtask's start
     * within project, and end as Project's starttime+subtask's end time within
     * project. If no owner or owner has no work time, then return 0;
     *
     * @return
     */
//    public long getFinishTime(long subSum) {
//        Object owner = getOwner();
//        if (owner != null && owner instanceof ItemList) {
//            return ((ItemList) owner).getFinishTime(this, subSum);
//        } else {
//            return 0;
//        }
//    }
    /**
     * returns the parent (or other list this Item belongs) which defines
     * WorkTime. Can be owner, category. If multiple lists have WorkTime
     * defined, chooses in order: Owner, Categories (according to order they
     * were added to task [INVISIBLE to user???] Returns null if no lists with
     * WorkTime are found.
     *
     * @return
     */
//    Object getWorkTimeReference() {
//        if (getOwner() instanceof ItemList && ((ItemList) getOwner()).hasWorkTimeDefinition()) {
//            return getOwner();
//        } else if (getCategoriesSize() > 0) {
//            Collection categories = getCategories();
//            for (int i = 0, size = categories.getSize(); i < size; i++) {
//                if (((Category) categories.getItemAt(i)).hasWorkTimeDefinition()) {
//                    return categories.getItemAt(i);
//                }
//            }
//        } //TODO!!!: check all lists that the Item belongs to (via call back list?!)
//        return null;
//    }
    /**
     * if the Item's owner (or if not, one of its categories) has a
     * WorkTimeDefinition associated with it, then returns the time when this
     * task can be finished. If no owner, or owner is not an ItemList, or owner
     * does not have a WorkTimeDefinition, or the WorkTimeDefinition is too
     * short to include the Item, then returns 0L.
     *
     * Works both for items belonging to a list (category, named lise), AND
     * project tasks since the project task will store the subtasks in an
     * ItemList which will also store the WorkTimeDefinition
     *
     * @return
     */
//    public long getFinishTime() {
//        Object workTimeReference = getWorkTimeReference();
//        if (workTimeReference != null && workTimeReference instanceof ItemList) {
//            return ((ItemList) workTimeReference).getFinishTime(this, this.getRemainingEffort());
//        } else {
//            return 0;
//        }
//    }
//</editor-fold>
    /**
     * if earliest=true returns the earlist finish time for this Item (assuming
     * it belongs to several different lists each with WorkTime defined). If
     * earliest false, returns the latest finish time.
     *
     * @param earliest
     * @return
     */
//    public long getFinishTimeTODO(boolean earliest) {
//        return 0;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * if the Item's owner has a WorkTimeDefinition associated with it, then
     * returns the time when this task can start. If no owner, or owner is not
     * an ItemList, or owner does not have a WorkTimeDefinition, or the
     * WorkTimeDefinition is too short to include the Item, then returns 0L.
     *
     * Works both for items belonging to a list (category, named lise), AND
     * project tasks since the project task will store the subtasks in an
     * ItemList which will also store the WorkTimeDefinition
     *
     * @return
     */
//    long getStartTime() {
//        Object owner = getOwner();
//        if (owner != null && owner instanceof ItemList) {
//            return ((ItemList) owner).getFinishTime(this, 0);
//        }
//        return 0L;
//    }
//    public String getFilterFieldFormatted(int fieldId) {
//        switch (getFieldType(fieldId)) {
//            case Expr.VALUE_FIELD_TYPE_STRING:
//                return ((String) getFilterField(fieldId));
//            case Expr.VALUE_FIELD_TYPE_DATE:
//                if (((Long) getFilterField(fieldId)) == 0L) {
//                    return "Undated"; //Internationalize!
//                } else {
//                    return (new MyDate(((Long) getFilterField(fieldId)))).formatDate();
//                }
//            case Expr.VALUE_FIELD_TYPE_INTEGER:
//                return "" + ((Integer) getFilterField(fieldId));
//            case Expr.VALUE_FIELD_TYPE_DURATION:
//                return Duration.formatDuration(((Long) getFilterField(fieldId)));
//            case Expr.VALUE_FIELD_TYPE_ENUM:
//                if (fieldId == FIELD_STATUS) {
////                    return STATUS_NAMES[getStatus()];
////                    return getStatusName(getStatus().getDescription());
//                    return getStatus().getDescription();
//                } else {
//                    return "ERROR in getFilterFieldFormatted, fieldId=" + fieldId;
//                }
//        }
//        return "";
//    }
//    public static FieldDef[] getFields() {
//        return FIELDS;
//    }
    /**
     * returns the list of enum values that can be chosen for the field fieldId.
     * E.g. for Item.Status returns int[]{DONE, CANCELLED, ...}
     *
     * @param fieldId
     * @return
     */
//    public static int[] getFieldEnumValues(int fieldId) {
//        switch (fieldId) {
//            case Item.FIELD_STATUS:
//                //            return STATUS_VALUES; // int[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
//                return getStatusValues(); // int[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
//            case Item.FIELD_PRIORITY_IMPORTANCE:
//            case Item.FIELD_PRIORITY_URGENCY:
//                return PriorityImpUrgencyPair.getImpUrgIntArray();
//            default:
//                //            return new int[]{};
//                return null;
//        }
//    }
//    public static String[] getFieldEnumNames(int fieldId) {
//        switch (fieldId) {
//            case Item.FIELD_STATUS:
//                //            return STATUS_NAMES; //new String[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
//                return getStatusNames(); //new String[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
//            case Item.FIELD_PRIORITY_IMPORTANCE:
//            case Item.FIELD_PRIORITY_URGENCY:
//                return PriorityImpUrgencyPair.getImpUrgStringArray();
//            default:
//                //            return new String[]{};
//                return null;
//        }
//    }
    /**
     * returns the ids of all fields with the specific typeId. Used e.g. in
     * filter expressions when selecting which fields a field can be compared
     * with.
     *
     * @param typeId
     * @return
     */
//    public static int[] getFieldValuesOfType(int typeId) {
//        FieldDef[] FIELDS = getFields();
//        Vector fieldsOfTypeVector = new Vector();
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            if (FIELDS[i].type == typeId) {
//                fieldsOfTypeVector.add(FIELDS[i].id);
//            }
//        }
//        int[] fieldsOfType = new int[fieldsOfTypeVector.size()];
//        for (int i = 0, size = fieldsOfTypeVector.size(); i < size; i++) {
//            fieldsOfType[i] = ((Integer) fieldsOfTypeVector.elementAt(i));
//        }
//        return fieldsOfType;
//    }
//
//    public static String[] getFieldNamesOfType(int typeId) {
//        FieldDef[] FIELDS = getFields();
//        Vector fieldsOfTypeVector = new Vector();
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            if (FIELDS[i].type == typeId) {
//                fieldsOfTypeVector.add(FIELDS[i].name);
//            }
//        }
//        String[] fieldsOfType = new String[fieldsOfTypeVector.size()];
//        for (int i = 0, size = fieldsOfTypeVector.size(); i < size; i++) {
//            fieldsOfType[i] = (String) fieldsOfTypeVector.elementAt(i);
//        }
//        return fieldsOfType;
//    }
//</editor-fold>
    /**
     * the order of the fields in this String array determines the order of
     * fields displayed in the editor screens, e.g. FilterDefScreen
     *
     * @return
     */
//    public static String[] getAllFieldNamesInDisplayOrder() {
////        return FIELDNAMES; //optimization: return FIELDNAMES directly if UI becomes too slow
//        return getFieldNames(getAllFieldIdsinDisplayOrder());
//    }
    /**
     * returns the list of displayable field names for the given array of
     * fieldIds
     *
     * @param fieldIds
     * @return
     */
//    public static String[] getFieldNames(int[] fieldIds) {
//        String[] fieldNameArr = new String[fieldIds.length];
//        for (int i = 0, size = fieldIds.length; i < size; i++) {
//            fieldNameArr[i] = getFieldName(fieldIds[i]);
//        }
//        return fieldNameArr;
//    }
//    public static FieldDef[] getFields() {
//        return null;
//    }
//    public static int[] getAllFieldIdsinDisplayOrder() {
//        int[] FIELDIDS = new int[FIELDS.length];
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            FIELDIDS[i] = FIELDS[i].id;
//        }
//        return FIELDIDS;
//    }
//    public static FieldDef getFieldDef(int fieldId) {
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            if (FIELDS[i].id == fieldId) {
//                return FIELDS[i];
//            }
//        }
//        ASSERT.that("ItemListFlatten.getFieldName(" + fieldId + ") - value not defined");
//        return null;
//    }
    /**
     * returns the displayable name for a given field
     *
     * @param fieldId
     * @return
     */
//    public static String getFieldName(int fieldId) {
//        FieldDef fieldDef = getFieldDef(fieldId);
//        return fieldDef.name;
//    }
    /**
     * returns the help dreadFunNames for the field, or "" if none define
     *
     * @param fieldId
     * @return
     */
//    public static String getFieldHelpText(int fieldId) {
//        FieldDef fieldDef = getFieldDef(fieldId);
//        return fieldDef.help;
//    }
    /**
     * returns the FieldType (eg Expr.VALUE_FIELD_TYPE_STRING,
     * Expr.VALUE_FIELD_TYPE_BOOLEAN, Expr.VALUE_FIELD_TYPE_DATE) for the given
     * fieldId
     *
     * @param fieldId e.g. FIELD_DESCRIPTION, FIELD_DONE
     * @return
     */
//    public static int getFieldType(int fieldId) {
//        FieldDef fieldDef = getFieldDef(fieldId);
//        return fieldDef.type;
//    }
    /**
     * call to save the item
     */
//    public void commitXXX() throws ParseException {
//        this.save();//ParseObject.save()
//    }
//    public int countTasksWhereStatusWillBeChanged(ItemStatus newStatus, boolean recurseOverSubtasks) {
//        return (changeSubtaskStatus(newStatus, getStatus()) ? 1 : 0)
//                + ((recurseOverSubtasks && getItemListSize() > 0)
//                        ? getItemList().countTasksWhereStatusWillBeChanged(newStatus, recurseOverSubtasks)
//                        : 0);
//    }
    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean showSubtasks) {
//        return getText();
//        return getText().length() != 0 ? getText()+" ("+getObjectId()+")" : getObjectId();
        return getText() + " (" + getObjectIdP() + ")"
                + (isDone() ? " [DONE]" : (getRemaining() > 0 ? MyDate.formatDurationShort(getRemaining()) : ""))
                + (showSubtasks ? (getListFull().size() == 0 ? "" : " subtasks={" + getListAsCommaSeparatedString(getListFull()) + "}") : "");
    }

    /**
     * only tests based on strict equality (either same object or same objectId)
     *
     */
//    @Override
    public boolean equalsXXX(Object obj) {
        //TODO!!! extend to complete coverage of all fields (although never used)?
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Item)) {
            return false;
        }
        Item item = (Item) obj;
//        return ((Item) obj).getObjectId().equals(getObjectId());
        if (getObjectIdP() != null && item.getObjectIdP() != null) {
            //compare isDirty in case we have two instances of the same 
//            return getObjectId().equals(((Item) obj).getObjectId()) && isDirty()==((Item) obj).isDirty();
            if (Config.CHECK_OWNERS) {
                ASSERT.that(!getObjectIdP().equals(item.getObjectIdP()) || isDirty() == item.isDirty(), () -> "comparing dirty and not dirty instance of same object=" + this);
            }
            if (getObjectIdP().equals(item.getObjectIdP())) {
                return true;
            }
        }
        return false; //if not the same objectId, then by definition not equal, this == (Item) obj;
    }
    
    

    /**
     * Adds a listener to the switch which will cause an event to dispatch on
     * click.
     *
     * Copied from CN1 OnOffSwitch.java
     *
     * @param l implementation of the action listener interface
     */
    public void addActionListener(ActionListener l) {
        dispatcher.addListener(l);
        dispatcher.addListener(l);
    }

    /**
     * Removes the given action listener from the switch
     *
     * Copied from CN1 OnOffSwitch.java
     *
     * @param l implementation of the action listener interface
     */
    public void removeActionListener(ActionListener l) {
        dispatcher.removeListener(l);
    }

    /**
     * Returns a vector containing the action listeners for this button
     *
     * @return the action listeners
     * @deprecated use the version that returns a collection
     */
    public Vector getActionListeners() {
        return dispatcher.getListenerVector();
    }

    /**
     * Returns a collection containing the action listeners for this button
     *
     * @return the action listeners Copied from CN1 OnOffSwitch.java
     *
     */
    public Collection getListeners() {
        return dispatcher.getListenerCollection();
    }

    /**
     * Copied from CN1 OnOffSwitch.java
     *
     *
     */
    private void fireActionEvent() {
        dispatcher.fireActionEvent(new ActionEvent(this, ActionEvent.Type.PointerPressed));
        Display d = Display.getInstance();
        if (d.isBuiltinSoundsEnabled()) {
            d.playBuiltinSound(Display.SOUND_TYPE_BUTTON_PRESS);
        }
    }

    /**
     * examples of DataChangedListener: if(isImmediateInputMode(inputMode)) {
     * commitChange(); fireDataChanged(DataChangedListener.ADDED, pos); } else {
     * if(pressCount == 1) { fireDataChanged(DataChangedListener.ADDED, pos); }
     * else { fireDataChanged(DataChangedListener.CHANGED, pos); }
     *
     */
    /**
     * Adds a listener for data change events it will be invoked for every
     * change made to the text field, notice most platforms will invoke only the
     * DataChangedListener.CHANGED event
     *
     * @param d the listener
     */
    public void addDataChangeListener(DataChangedListener d) {
        listeners.addListener(d);
    }

    /**
     * Removes the listener for data change events
     *
     * @param d the listener
     */
    public void removeDataChangeListener(DataChangedListener d) {
        listeners.removeListener(d);
    }

    /**
     * Alert the TextField listeners the text has been changed on the TextField
     *
     * @param type the event type: Added, Removed or Change
     * @param index cursor location of the event
     */
    public void fireDataChanged(int type, int index) {
        if (listeners != null) {
            listeners.fireDataChangeEvent(index, type);
        }
    }

    /**
     * when saving, first update all values and save all, THEN create repeat
     * instances.
     *
     * @throws ParseException
     */
    public void updateBeforeSave() {

        List<Item> subtasks = getListFull();
        if (subtasks != null && subtasks.size() > 0) {
            if (getObjectIdP() == null) { //if project was not saved before
                //save project first, *without* the potentially unsaved subtasks
                setList(null);
                DAO.getInstance().saveInBackground((ParseObject) this);

                //then save all subtasks (recursively) (they will all have the now saved Project as owner)
                if (false) {
                    for (Item subtask : subtasks) {
                        DAO.getInstance().saveInBackground(subtask); //recursive until reaching leaf tasks (handled above in if(!isProject))
                    }
                }
            }

            //if there are any dependencies btw a task/subtask and another unsaved, then ensure the dependent-on task is saved first!!
            //update any subtasks that may be affected by project changes to inherited fields:
            if (opsToUpdateSubtasks != null && opsToUpdateSubtasks.size() > 0) {
                List<Item> updatedSubtasks = new ArrayList<>();
                for (Item subtask : subtasks) {
                    if (!subtask.isDone() || MyPrefs.itemInheritEvenDoneSubtasksInheritOwnerValues.getBoolean()) { //UI: don't update inherited values for finished subtasks! (leave them in the same state as when they were closed, even if projet changes
                        for (UpdateItem f : opsToUpdateSubtasks) {
                            if (f.update(subtask)) {
                                updatedSubtasks.add(subtask);
                            }
                        }
                    }
                }
                if (false) {
                    DAO.getInstance().saveInBackground((List) updatedSubtasks); //optimization: batch up all subtasks in saveInBackground
                }
                opsToUpdateSubtasks = null; //reset after all subtasks have updated
                opsToUpdateSubtasks = new ArrayList<>(); //reset after all subtasks have updated
                //then save all subtasks (recursively) (they will all have the now saved Project as owner)
                for (Item subtask : subtasks) {
                    DAO.getInstance().saveInBackground(subtask); //recursive until reaching leaf tasks (handled above in if(!isProject))
                }
            }
            //then add the now saved subtasks and save the project again:
            setList(subtasks);
        }

        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
            Inbox.getInstance().addToList(this);
            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance()); //will/should only be executed *after* this Item is saved (to avoid ObjId reference errors)
        }

        if (isDone() || getDeletedDateN() != null) {
            TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this); //stop timer *before* saving to ensure updates to Actual are saved
        }
        if (false) {
            DAO.getInstance().saveInBackground((ParseObject) this); //WHY save again?? We're already saving 'this'
        }
        //handling of change in owner: if set in ScreenItem2: all values updated there; if this is a copy: all values can be assumed to already have been correctly set; if subtask inserted into new project: 
//        Runnable repeatRule = (Runnable) saveOps.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)
//        repeatRule.run(); //create or update any repeatInstances 
        if (opsAfterSubtaskUpdates != null) {
            for (Runnable f : opsAfterSubtaskUpdates) {
                f.run();
            }
        }
        opsAfterSubtaskUpdates=null; //must delete 

        //save dirty Categories:
//        for (Category cat : getCategories()) {
//            if (cat.isDirty())
//                DAO.getInstance().saveInBackground((ParseObject) cat);
//        }
//        DAO.getInstance().saveBatch((List) getCategories()); //save *after* saving this item to avoid reference errors. saveBatch will remove non-changed categories
//        DAO.getInstance().saveBatch(new ArrayList(getCategories())); //save *after* saving this item to avoid reference errors. saveBatch will remove non-changed categories. Work on copy of list to avoid java.util.ConcurrentModificationException
        if (false&&getCategories().size() > 0) {
            DAO.getInstance().saveInBackground(new ArrayList(getCategories())); //save *after* saving this item to avoid reference errors. saveBatch will remove non-changed categories. Work on copy of list to avoid java.util.ConcurrentModificationException
        }
        if (isDirty()) {
            listeners.fireDataChangeEvent(DataChangedListener.CHANGED, -1); //TODO optimize and only send change even on relevant changes (e.g. status change, remaining/actual/effort changes)
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (afterSaveActions.containsKey(AFTER_SAVE_ALARM_UPDATE)) {
//            afterSaveActions.remove(AFTER_SAVE_TEXT_UPDATE); //if we're updating alarms due to time change, we can ignore any changed to the text
//        }
//        for (MyForm.Action action : afterSaveActions.values()) {
//            action.launchAction();
////            afterSaveActions.remove(action); //NOT allowed, throws java.util.ConcurrentModificationException, see eg http://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
//        }
//        afterSaveActions.clear();
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="comment">
//        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
//            Inbox.getInstance().addToList(this);
//            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
//            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
//        } else {
//            super.save();
//        }
//</editor-fold>
        if (isDone()) {
            AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //remove any future alarms for a Done/Cancelled task
            TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this);
        } else {
            updateNextcomingAlarm();
            if (mustUpdateAlarms) { 
                AlarmHandler.getInstance().updateAlarmsOrTextForItem(this);
                mustUpdateAlarms = false;
            }
        }

        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
            Inbox.getInstance().addToList(this);
//            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
        }
    }

    @Override
    public void save() throws ParseException {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            List<Item> subtasks = getListFull();
//            if (subtasks != null && subtasks.size() > 0) {
//                if (getObjectIdP() == null) { //if project was not saved before
//                    //save project first, *without* the potentially unsaved subtasks
//                    setList(null);
//                    DAO.getInstance().saveInBackground((ParseObject) this);
//
//                    //then save all subtasks (recursively) (they will all have the now saved Project as owner)
//                    if (false) {
//                        for (Item subtask : subtasks) {
//                            DAO.getInstance().saveInBackground(subtask); //recursive until reaching leaf tasks (handled above in if(!isProject))
//                        }
//                    }
//                }
//
//                //if there are any dependencies btw a task/subtask and another unsaved, then ensure the dependent-on task is saved first!!
//                //update any subtasks that may be affected by project changes to inherited fields:
//                if (opsOnSubtasks != null && opsOnSubtasks.size() > 0) {
//                    List<Item> updatedSubtasks = new ArrayList<>();
//                    for (Item subtask : subtasks) {
//                        if (!subtask.isDone() || MyPrefs.itemInheritEvenDoneSubtasksInheritOwnerValues.getBoolean()) { //UI: don't update inherited values for finished subtasks! (leave them in the same state as when they were closed, even if projet changes
//                            for (UpdateItem f : opsOnSubtasks) {
//                                if (f.update(subtask)) {
//                                    updatedSubtasks.add(subtask);
//                                }
//                            }
//                        }
//                    }
//                    if (false) {
//                        DAO.getInstance().saveInBackground((List) updatedSubtasks); //optimization: batch up all subtasks in saveInBackground
//                    }
//                    opsOnSubtasks = null; //reset after all subtasks have updated
//                    opsOnSubtasks = new ArrayList<>(); //reset after all subtasks have updated
//                    //then save all subtasks (recursively) (they will all have the now saved Project as owner)
//                    for (Item subtask : subtasks) {
//                        DAO.getInstance().saveInBackground(subtask); //recursive until reaching leaf tasks (handled above in if(!isProject))
//                    }
//                }
//                //then add the now saved subtasks and save the project again:
//                setList(subtasks);
//            }
//
//            if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
//                Inbox.getInstance().addToList(this);
//                DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance()); //will/should only be executed *after* this Item is saved (to avoid ObjId reference errors)
//            }
//
//            if (isDone() || getDeletedDateN() != null) {
//                TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this); //stop timer *before* saving to ensure updates to Actual are saved
//            }
//            if (false) {
//                DAO.getInstance().saveInBackground((ParseObject) this); //WHY save again?? We're already saving 'this'
//            }
//            //handling of change in owner: if set in ScreenItem2: all values updated there; if this is a copy: all values can be assumed to already have been correctly set; if subtask inserted into new project:
////        Runnable repeatRule = (Runnable) saveOps.remove(REPEAT_RULE_KEY); //set a repeatRule aside for execution last (after restoring all fields)
////        repeatRule.run(); //create or update any repeatInstances
//            if (opsPostUpdateFields != null) {
//                for (Runnable f : opsPostUpdateFields) {
//                    f.run();
//                }
//            }
//
//            //save dirty Categories:
////        for (Category cat : getCategories()) {
////            if (cat.isDirty())
////                DAO.getInstance().saveInBackground((ParseObject) cat);
////        }
////        DAO.getInstance().saveBatch((List) getCategories()); //save *after* saving this item to avoid reference errors. saveBatch will remove non-changed categories
//            DAO.getInstance().saveBatch(new ArrayList(getCategories())); //save *after* saving this item to avoid reference errors. saveBatch will remove non-changed categories. Work on copy of list to avoid java.util.ConcurrentModificationException
//
//            if (isDirty()) {
//                listeners.fireDataChangeEvent(DataChangedListener.CHANGED, -1); //TODO optimize and only send change even on relevant changes (e.g. status change, remaining/actual/effort changes)
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (afterSaveActions.containsKey(AFTER_SAVE_ALARM_UPDATE)) {
////            afterSaveActions.remove(AFTER_SAVE_TEXT_UPDATE); //if we're updating alarms due to time change, we can ignore any changed to the text
////        }
////        for (MyForm.Action action : afterSaveActions.values()) {
////            action.launchAction();
//////            afterSaveActions.remove(action); //NOT allowed, throws java.util.ConcurrentModificationException, see eg http://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
////        }
////        afterSaveActions.clear();
////</editor-fold>
//
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
////            Inbox.getInstance().addToList(this);
////            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
////            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
////        } else {
////            super.save();
////        }
////</editor-fold>
//            if (isDone()) {
//                AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //remove any future alarms for a Done/Cancelled task
//                TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this);
//            } else {
//                updateNextcomingAlarm();
//                if (mustUpdateAlarms) {
//                    AlarmHandler.getInstance().updateAlarmsOrTextForItem(this);
//                    mustUpdateAlarms = false;
//                }
//            }
//
//            if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
//                Inbox.getInstance().addToList(this);
////            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
//                DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
//            }
////        else {
////            super.save();
////        }
//        }
//</editor-fold>
        super.save();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveORG() throws ParseException {
//        //update any subtasks that may be affected by project changes to inherited fields:
//        if (opsOnSubtasks != null && opsOnSubtasks.size() > 0) {
//            List<Item> updatedSubtasks = new ArrayList<>();
//            for (Item subtask : (List<Item>) getListFull()) {
//                boolean subtaskUpdated = false;
//                if (!subtask.isDone() || MyPrefs.itemInheritEvenDoneSubtasksInheritOwnerValues.getBoolean()) { //UI: don't update inherited values for finished subtasks! (leave them in the same state as when they were closed, even if projet changes
//                    for (UpdateItem f : opsOnSubtasks) {
////                        subtaskUpdated = f.update(subtask) || subtaskUpdated;
//                        if (f.update(subtask)) {
//                            updatedSubtasks.add(subtask);
//                        }
//                    }
//                }
////                if (subtaskUpdated) {
////                    DAO.getInstance().saveInBackground(subtask); //optimization: batch up all subtasks in saveInBackground
////                }
//            }
//            DAO.getInstance().saveInBackground((List) updatedSubtasks); //optimization: batch up all subtasks in saveInBackground
//            opsOnSubtasks = null; //reset after all subtasks have updated
//            opsOnSubtasks = new ArrayList<>(); //reset after all subtasks have updated
//        }
//
//        if (isDirty()) {
//            listeners.fireDataChangeEvent(DataChangedListener.CHANGED, -1); //TODO optimize and only send change even on relevant changes (e.g. status change, remaining/actual/effort changes)
//        }
//
////        if (afterSaveActions.containsKey(AFTER_SAVE_ALARM_UPDATE)) {
////            afterSaveActions.remove(AFTER_SAVE_TEXT_UPDATE); //if we're updating alarms due to time change, we can ignore any changed to the text
////        }
////        for (MyForm.Action action : afterSaveActions.values()) {
////            action.launchAction();
//////            afterSaveActions.remove(action); //NOT allowed, throws java.util.ConcurrentModificationException, see eg http://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
////        }
////        afterSaveActions.clear();
//        if (isDone() || getDeletedDateN() != null) {
//            TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this);
//        }
//
////        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
////            Inbox.getInstance().addToList(this);
////            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
////            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
////        } else {
////            super.save();
////        }
//        if (isDone()) {
//            AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //remove any future alarms for a Done/Cancelled task
//            TimerStack.getInstance().stopTimerIfActiveOnThisItemAndGotoNext(this);
//        } else {
//            updateNextcomingAlarm();
//            if (mustUpdateAlarms) {
//                AlarmHandler.getInstance().updateAlarmsOrTextForItem(this);
//                mustUpdateAlarms = false;
//            }
//        }
//
//        if (getOwner() == null) { //UI: if a task does not have an owner, then always add it to inbox (also if eg created inline in a Category list of items!)
//            Inbox.getInstance().addToList(this);
//            super.save(); //in case item was not saved earlier, must save and get the objectId before saving the Inbox
//            DAO.getInstance().saveInBackground((ParseObject) Inbox.getInstance());
//        } else {
//            super.save();
//        }
//
//    }
//</editor-fold>
    /**
     * templates (or their subtasks): should not show up when searching on tasks
     * <=> require avoiding them when searching). Templates can be edited like
     * normal tasks. Should be possible to distinguish template subtasks from
     * other subtasks. When deleting a category it should be removed from
     * templates. Solutions: mark every project/subtask as template during the
     * copy? Create a separate ParseObject class to store completely separately?
     *
     * @param template
     */
    /**
     * templates (or their subtasks): should not show up when searching on tasks
     * <=> require avoiding them when searching). Templates can be edited like
     * normal tasks. Should be possible to distinguish template subtasks from
     * other subtasks. When deleting a category it should be removed from
     * templates. Solutions: mark every project/subtask as template during the
     * copy? Create a separate ParseObject class to store completely separately?
     *
     * @param template
     */
    public void setTemplate(boolean template) {
        if (template) {
            put(PARSE_TEMPLATE, true);
        } else {
            remove(PARSE_TEMPLATE);
        }
    }

    public boolean isTemplate() {
        Boolean template = getBoolean(PARSE_TEMPLATE);
        return (template == null) ? false : template;
    }

    /**
     * returns null if no workslots defined
     *
     * @return
     */
//    public List<WorkSlot> getWorkSlotListN() {
    @Override
    public WorkSlotList getWorkSlotListN(boolean refreshWorkSlotListFromDAO) {
//        if (workSlotListBuffer == null || refreshWorkSlotListFromDAO) {
//            workSlotListBuffer = DAO.getInstance().getWorkSlotsN(this);
//        }
//        return workSlotListBuffer;
        List<WorkSlot> workslots = getList(PARSE_WORKSLOTS);

//        if (Config.CHECK_OWNERS && workslots != null) {
//            for (WorkSlot workSlot : workslots) {
//                ASSERT.that(workSlot.getOwner() == this, "ERROR in owner: WorkSlot=" + workSlot + ", workSlot.owner=" + workSlot.getOwner() + ", should be=" + this);
//            }
//        }
        if (workslots != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(workslots);
            if (Config.CHECK_OWNERS) {
                checkOwners(workslots);
            }
            boolean updated = false;//&& RepeatRuleParseObject.updateWorkSlotListXXX(workslots);
            WorkSlotList workSlotList = new WorkSlotList(this, workslots, true); //true=already sorted
            if (updated) {
                setWorkSlotList(workSlotList);
            }
            return workSlotList;
        } else {
            return null;//new WorkSlotList();
        }

    }

//    @Override
//    public WorkSlotList getWorkSlotListN() {
//        if (workSlotListBuffer == null) {
//            workSlotListBuffer = DAO.getInstance().getWorkSlotsN(this);
//        }
//        return workSlotListBuffer;
//    }
    @Override
//    public void setWorkSlotList(List<WorkSlot> workSlotList) {
    public void setWorkSlotList(WorkSlotList workSlotList) {
        //TODO currently not stored in ItemList but get from DAO
//        workSlotListBuffer = null;
//        workSlotListBuffer = workSlotList;
//        workTimeAllocator = null;
//        if (workSlotList != null && workSlotList.size() > 0) {
        if (workSlotList != null && workSlotList.getWorkSlotListFull().size() > 0) {
//            put(PARSE_WORKSLOTS, workSlotList.getWorkSlots());
            workSlotList.setOwner(this);
            ASSERT.that(MyUtil.isSorted(workSlotList.getWorkSlotListFull(),
                    (ws1, ws2) -> ((int) ((((WorkSlot) ws2).getStartTime()) - ((WorkSlot) ws1).getStartTime()))),
                    "setting list of workslots which is NOT sorted: " + workSlotList.getWorkSlotListFull());
            put(PARSE_WORKSLOTS, workSlotList.getWorkSlotListFull());
        } else {
            remove(PARSE_WORKSLOTS);
        }
    }

    /**
     * call to refresh/update the WorkTimeDefinition. Should be done whenever
     */
//    public void refreshWorkTimeDefinition() {
//        workSlotListBuffer = null;
//        workTimeDefinitionBuffer = null; //consequently reset the WorkTimeDefinition
//    }
    /**
     * defines the sort order of tasks in the Today view. Can be extended or
     * modified together with getTodaySortOrder() to change the Today view.
     */
    public enum TodaySortOrder {
        DUE_TODAY_ONGOING, DUE_TODAY_WAITING, DUE_TODAY_CREATED, WAITING_TODAY,
        STARTING_TODAY_ONGOING, STARTING_TODAY_WAITING, STARTING_TODAY_CREATED
    }

    public TodaySortOrder getTodaySortOrder() {
        long startOfToday = MyDate.getStartOfToday().getTime();
        long startOfTomorrow = startOfToday + MyDate.DAY_IN_MILLISECONDS;
        long time;
        ItemStatus status = getStatus();
        time = getDueDateD().getTime();
        if (time >= startOfToday && time < startOfTomorrow) {
            if (status == status.ONGOING) {
                return TodaySortOrder.DUE_TODAY_ONGOING;
            } else if (status == ItemStatus.WAITING) {
                return TodaySortOrder.DUE_TODAY_WAITING;
            } else {
                ASSERT.that(status == ItemStatus.CREATED);
                return TodaySortOrder.DUE_TODAY_CREATED;
            }
        } else {
            time = getWaitingTillDateD().getTime();
            if (time >= startOfToday && time < startOfTomorrow) {
                return TodaySortOrder.WAITING_TODAY;
            } else {
                time = getStartByDateD().getTime();
                ASSERT.that(time >= startOfToday && time < startOfTomorrow, ()
                        -> "getStartByDateD should always be Today, item=" + this
                        + " startBy=" + MyDate.formatDateNew(getStartByDateD())
                        + " due=" + MyDate.formatDateNew(getDueDateD())
                        + " waiting=" + MyDate.formatDateNew(getWaitingTillDateD()));
                if (status == status.ONGOING) {
                    return TodaySortOrder.STARTING_TODAY_ONGOING;
                } else if (status == ItemStatus.WAITING) {
                    return TodaySortOrder.STARTING_TODAY_WAITING;
                } else {
                    ASSERT.that(status == ItemStatus.CREATED);
                    return TodaySortOrder.STARTING_TODAY_CREATED;
                }
            }
        }
    }

    /**
     * returns false if this new task does not contain relevant data making it
     * worth saving it. E.g. no description, no notes, no due date, no actual
     * time, no remaining, no subtasks.
     *
     * @return
     */
    public boolean hasSaveableData() {
        return getText().length() != 0
                || getComment().length() != 0
                || getDueDateD().getTime() != 0
                || getActual() != 0
                || getRemaining() != 0
                || getCategories().size() != 0
                || getListFull().size() != 0;
    }

    static class EstimateResult {

        int minutes;
        String cleaned;

        public EstimateResult(int minutes, String cleaned) {
            this.minutes = minutes;
            this.cleaned = cleaned;
        }
    }

    static EstimateResult getEffortEstimateFromTaskText(String t) {
        return getEffortEstimateFromTaskText(t, false);
    }

    static EstimateResult getEffortEstimateFromTaskText(String t, boolean keepOrgTextUnchanged) {
        int minutes = 0;
        int hours = 0;
        String txt = t;
//        if (MyPrefs.itemExtractRemainingEstimateFromStringInTaskText.getBoolean()) {
        RE minutes_hours_RE = new RE("\\b([0-9]+)(?:h|\\:)([0-5][0-9]|[0-9])(?:m(in)?)?\\b"); //HHhMM. OK: 0h17, 10h00 2h17m, 199h. NOK: 1h65, Not allowed to start with '0' 
        if (minutes_hours_RE.match(txt)) {
            hours = MyUtil.getIntFromTextString(txt, minutes_hours_RE.getParenStart(1), minutes_hours_RE.getParenLength(1));
            minutes = MyUtil.getIntFromTextString(txt, minutes_hours_RE.getParenStart(2), minutes_hours_RE.getParenLength(2)) + 60 * hours;
            if (!keepOrgTextUnchanged && !MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
                txt = MyUtil.deleteSubstring(txt, minutes_hours_RE.getParenStart(0), minutes_hours_RE.getParenLength(0));
            }
//                System.out.println("ValStr=\"" + valStr + "\" val=" + val + "\tcleaned=\"" + txt + "\"\n");
        } else {
            RE minutes_RE = new RE("\\s*\\b(([1-9][0-9]+)|([1-9]))m(?:in)?\\b"); //MINUTES not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
            if (minutes_RE.match(txt)) {
                minutes = MyUtil.getIntFromTextString(txt, minutes_RE.getParenStart(1), minutes_RE.getParenLength(1));
                if (!keepOrgTextUnchanged && !MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
                    txt = MyUtil.deleteSubstring(txt, minutes_RE.getParenStart(0), minutes_RE.getParenLength(0));
                }
            } else {
                RE hours_RE = new RE("\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"); //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
                if (hours_RE.match(txt)) {
                    minutes = MyUtil.getIntFromTextString(txt, hours_RE.getParenStart(1), hours_RE.getParenLength(1)) * 60;
                    if (!keepOrgTextUnchanged && !MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
                        txt = MyUtil.deleteSubstring(txt, hours_RE.getParenStart(0), hours_RE.getParenLength(0));
                    }
                }
//                }
            }
        }
//        return new Item().new EstimateResult(minutes, txt);
        return new Item.EstimateResult(minutes, txt);
    }

    //make them static to only initialize once for the whole app
    private static RE priority = new RE("[p|P][19]"); //eg "p1", "P9" but not "P0"
    private static RE urgImp = new RE("[HH|HM|HL|MH|MM|ML|LH|LM|LL]"); //eg "HM"
    private static RE value = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
    private static RE challengeXXX = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
    private static RE funXXX = new RE("v|V[19]+[09]*"); //eg "ffun" "ddread" or "/fun" - but slower to type on iPhone since "/" is other keybaord and requires going back to letters afterwards
    private static RE notesXXX = new RE("//text"); //eg "task1 //these are notes"

    //DATES
    private static RE dueXXX = new RE("//text"); //eg "tomorrow"

    //RELATIVE DATES (+/- wrt Due date)
    private static RE alarmXXX = new RE("//text"); //eg "a-5h" "a-5d"
    private static RE waitUntilXXX = new RE("//text"); //eg "w-5h" "w-5d"
    private static RE startByXXX = new RE("//text"); //eg "s-5h" "s-5d2h" or "s:tomorrow", s:12/6", "s:7jun" "s:7jun17" "s:7jun2018"

    //CATEGORIES
    private static RE categoryXXX = new RE("//text"); //eg "/cat1"

    public static String parseTaskTextForProperties(Item item, String txt) {
        //TODO!!! remove whitespace before (include preceding whitespace in RE)
        //TODO!!! also remove whitespace after if end of text (eg "xxx 10m  ") - but not if other text afterwards (eg "xxx 10m yyy")
        //TODO!!! add regexps for understanding Siri words like "estimate five/5 minutes"
//                String minutes = "\\b(([1-9][0-9]+)|([1-9]))m(in)?\\b"; //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//        String hours = "\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"; //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//<editor-fold defaultstate="collapsed" desc="comment">
//if (false){
//        RE priority = new RE("[p|P][19]"); //eg "p1", "P9" but not "P0"
//        RE urgImp = new RE("[HH|HM|HL|MH|MM|ML|LH|LM|LL]"); //eg "HM"
//        RE value = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
//        RE challengeXXX = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
//        RE funXXX = new RE("v|V[19]+[09]*"); //eg "ffun" "ddread" or "/fun" - but slower to type on iPhone since "/" is other keybaord and requires going back to letters afterwards
//        RE notesXXX = new RE("//text"); //eg "task1 //these are notes"
//
//        //DATES
//        RE dueXXX = new RE("//text"); //eg "tomorrow"
//
//        //RELATIVE DATES (+/- wrt Due date)
//        RE alarmXXX = new RE("//text"); //eg "a-5h" "a-5d"
//        RE waitUntilXXX = new RE("//text"); //eg "w-5h" "w-5d"
//        RE startByXXX = new RE("//text"); //eg "s-5h" "s-5d2h" or "s:tomorrow", s:12/6", "s:7jun" "s:7jun17" "s:7jun2018"
//
//        //CATEGORIES
//        RE categoryXXX = new RE("//text"); //eg "/cat1"
//}
//</editor-fold>

//ESTIMATE
//        int minutes = 0;
//        int hours = 0;
        if (MyPrefs.itemExtractRemainingEstimateFromStringInTaskText.getBoolean()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            RE minutes_hours_RE = new RE("\\s*\\b([0-9]+)(?h|:)([0-5][0-9]))(?m(in)?)?\\b"); //HHhMM. OK: 0h17, 10h00 2h17m, 199h. NOK: 1h65, Not allowed to start with '0'
//            if (minutes_hours_RE.match(txt)) {
//                hours = getIntFromTextString(txt, minutes_hours_RE.getParenStart(1), minutes_hours_RE.getParenLength(1));
//                minutes = getIntFromTextString(txt, minutes_hours_RE.getParenStart(2), minutes_hours_RE.getParenLength(2)) + 60 * hours;
//                if (!MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
//                    txt = deleteSubstring(txt, minutes_hours_RE.getParenStart(0), minutes_hours_RE.getParenLength(0));
//                }
////                System.out.println("ValStr=\"" + valStr + "\" val=" + val + "\tcleaned=\"" + txt + "\"\n");
//            } else {
//                RE minutes_RE = new RE("\\s*\\b(([1-9][0-9]+)|([1-9]))m(?in)?\\b"); //MINUTES not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//                if (minutes_RE.match(txt)) {
//                    minutes = getIntFromTextString(txt, minutes_RE.getParenStart(1), minutes_RE.getParenLength(1));
//                    if (!MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
//                        txt = deleteSubstring(txt, minutes_RE.getParenStart(0), minutes_RE.getParenLength(0));
//                    }
//                } else {
//                    RE hours_RE = new RE("\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"); //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//                    if (hours_RE.match(txt)) {
//                        minutes = getIntFromTextString(txt, hours_RE.getParenStart(1), hours_RE.getParenLength(1));
//                        if (!MyPrefs.itemKeepRemainingEstimateStringInTaskText.getBoolean()) {
//                            txt = deleteSubstring(txt, hours_RE.getParenStart(0), hours_RE.getParenLength(0));
//                        }
//                    }
//                }
//            }
//</editor-fold>
            EstimateResult res = getEffortEstimateFromTaskText(txt);
            String cleanedTxt = res.cleaned;
            //TODO!!! if EffortEstimate is also set in text, then DON'T autoupdate it based on Remaining
            boolean effortEstimateNotDefinedInTextInput = true;
//            item.setEstimate(((long) res.minutes) * MyDate.MINUTE_IN_MILLISECONDS, true); //update remaining, set for project-level
            item.setRemaining(((long) res.minutes) * MyDate.MINUTE_IN_MILLISECONDS, effortEstimateNotDefinedInTextInput); //update remaining, set for project-level
            return cleanedTxt;
        }
        return txt;
    }

    /**
     * will return the title text to use for a local or inApp notification
     *
     * @param alarmType
     * @return
     */
    String makeNotificationTitleText(AlarmType alarmType) {
        return getText();
    }

    /**
     * will return the notification body text to use for a local or inApp
     * notification
     *
     * @param alarmType
     * @return
     */
    String makeNotificationBodyText(AlarmType alarmType) {
        Date due = getDueDateD();
        if (MyPrefs.alarmShowDueTimeAtEndOfNotificationText.getBoolean() && due.getTime() != 0) {
            return "Due: " + MyDate.formatDateTimeNew(due);
        } else {
            return "";
        }
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
    }

    /**
     * takes
     *
     * @param fromCSV
     * @return
     */
    public String[] convertToFromCSV(boolean toCSV) {
        ArrayList list = new ArrayList();
        int i = 1;
        Object val = null;
//        boolean write = true;
        String s = "";
        switch (s) {
            case PARSE_TEXT:
                if (toCSV) {
                    list.add(getText());
                } else {
                    setText((String) val);
                }
                break;
            case PARSE_COMMENT:
                if (toCSV) {
                    list.add(getComment());
                } else {
                    setComment((String) val);
                }
                break;
            case PARSE_SUBTASKS:
                //TODO: how to convert subtasks to sth meaningful? At least so they can be imported, e.g. "task text [guid]"
                //TODO and write leaf subtasks to file first, so they have been read in when reading in their project, so they can be found via the guid
                if (toCSV) {
                    list.add(getStatus().toString());
                } else {
                    setStatus((ItemStatus) val);
                }
                break;
            case PARSE_DREAD_FUN_VALUE:
                if (toCSV) {
                    list.add(getDreadFunValueN().getDescription());
                } else {
                    setDreadFunValue(DreadFunValue.getValue((String) val));
                }
                break;
            case PARSE_CHALLENGE:
                if (toCSV) {
                    list.add(getChallengeN().getDescription());
                } else {
                    setChallenge(Challenge.getValue((String) val));
                }
                break;
            case PARSE_EXPIRES_ON_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getExpiresOnDateD()));
                } else {
                    setExpiresOnDate((Date) val);
                }
                break;
            case PARSE_INTERRUPT_OR_INSTANT_TASK:
                if (toCSV) {
                    list.add(((Boolean) isInteruptOrInstantTask()).toString());
                } else {
                    setInteruptOrInstantTask(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_ALARM_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getAlarmDateD()));
                } else {
                    setAlarmDate((Date) val);
                }
                break;
            case PARSE_WAITING_ALARM_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getWaitingAlarmDateD()));
                } else {
                    setWaitingAlarmDate((Date) val);
                }
                break;
            case PARSE_REPEAT_RULE:
                if (toCSV) {
                    list.add(getRepeatRule().toString());
                } else {
                    Log.p("Cannot import " + Item.REPEAT_RULE);
                }
                break;
            case PARSE_STARTED_ON_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getStartedOnDateD()));
                } else {
                    setStartedOnDate((Date) val, true); //force the explicit date (even if indirectly set via other field updates). Set dependent fields here? Could make sense if not set explicitly to ensure consistency, e.g. with status
                }
                break;
            case PARSE_STATUS:
                if (toCSV) {
                    list.add(getStatus().getDescription());
                } else {
                    setStatus(ItemStatus.getValue((String) val));
                }
                break;
            case PARSE_DUE_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getDueDateD()));
                } else {
                    setDueDate((Date) val);
                }
                break;
            case PARSE_HIDE_UNTIL_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getHideUntilDateD()));
                } else {
                    setHideUntilDate((Date) val);
                }
                break;
            case PARSE_START_BY_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getStartByDateD()));
                } else {
                    setStartByDate((Date) val);
                }
                break;
            case PARSE_WAITING_TILL_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getWaitingTillDateD()));
                } else {
                    setWaitingTillDate((Date) val);
                }
                break;
            case PARSE_DATE_WHEN_SET_WAITING:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getDateWhenSetWaitingD()));
                } else {
                    setDateWhenSetWaiting((Date) val);
                }
                break;
            case PARSE_EFFORT_ESTIMATE:
                if (toCSV) {
                    list.add((MyDate.formatDuration(getEstimate())));
                } else {
                    setEstimate((Long) val, false);
                }
                break;
            case PARSE_REMAINING_EFFORT:
                if (toCSV) {
                    list.add((MyDate.formatDuration(getRemainingFromParse())));
                } else {
                    setRemaining((Long) val, false); //UI: import of project estimates
                }
                break;
            case PARSE_ACTUAL_EFFORT:
                if (toCSV) {
                    list.add((MyDate.formatDuration(getActualForProjectTaskItself())));
                } else {
                    setActual((Long) val, false); //UI: import of project estimates
                }
                break;
            case PARSE_CATEGORIES:
                //TODO!!!!search for category based on text string, if not existing, create it and add this task
                break;
            case PARSE_PRIORITY:
                if (toCSV) {
                    list.add(((Integer) getPriority()).toString());
                } else {
                    setPriority(Integer.parseInt((String) val)); //TODO!!! more resistant parsing of val (illegal formats, null values)
                }
                break;
            case PARSE_STARRED:
                if (toCSV) {
                    list.add(((Boolean) isStarred()).toString());
                } else {
                    setStarred(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_EARNED_VALUE:
                if (toCSV) {
                    list.add(((Double) getEarnedValue()).toString());
                } else {
                    setEarnedValue(Double.parseDouble((String) val));//TODO!!! more resistant parsing of val (illegal formats, null values)
                }
                break;
            case PARSE_COMPLETED_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getCompletedDateD()));
                } else {
                    setCompletedDate((Date) val);
                }
                break;
            case PARSE_IMPORTANCE:
                if (toCSV) {
                    list.add(getImportanceN().getDescription());
                } else {
                    setImportance(HighMediumLow.getValue((String) val));
                }
                break;
            case PARSE_URGENCY:
                if (toCSV) {
                    list.add(getUrgencyN().getDescription());
                } else {
                    setUrgency(HighMediumLow.getValue((String) val));
                }
                break;
            case PARSE_OWNER_LIST:
                //TODO!!! search for owner list based on text string, if not existing, create it (!only when importing from another app!)
                break;
            case PARSE_OWNER_ITEM:
                //TODO!!! search for task/project based on text string, if not existing, create it (?=> means need to check if already existing when importing/creating tasks) and add this task as sub-task
                break;
            case PARSE_INTERRUPTED_TASK:
                //TODO!!! search for task/project based on text string, if not existing, create it (?=> means need to check if already existing when importing/creating tasks) and add this task as sub-task
                break;
            case PARSE_DEPENDS_ON_TASK:
                //TODO!!! search for task/project based on text string, if not existing, create it (?=> means need to check if already existing when importing/creating tasks) and add this task as sub-task
                break;
            case PARSE_TEMPLATE:
                if (toCSV) {
                    list.add(((Boolean) isTemplate()).toString());
                } else {
                    setTemplate(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_UPDATED_AT:
                list.add(MyDate.formatDateNew(getUpdatedAt()));
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getUpdatedAt()));
                } else {
                    Log.p("Cannot import " + Item.UPDATED_DATE);
                }
                break;
            case PARSE_CREATED_AT:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getCreatedDateD()));
                } else {
                    Log.p("Cannot import " + Item.CREATED_DATE);
                }
                break;
            case PARSE_ORIGINAL_SOURCE:
                //TODO!!! search for task/project based on text string, if not existing, create it (?=> means need to check if already existing when importing/creating tasks) and add this task as sub-task
                break;
            case PARSE_DELETED_DATE:
                if (toCSV) {
                    list.add(MyDate.formatDateNew(getDeletedDateN()));
                } else {
                    setDeletedDate((Date) val);
                }
                break;
        }
//Writer fw = new OutputStreamWriter(fos, "UTF-8");
////        Writer w = new Writer();
//        return CSVHelper.writeLine(fw, list);
        return null;
    }

    /**
     * returns true if this list should NOT be saved to parse, e.g. because it
     * is a temporary lists, e.g. wrapping a parse search results
     *
     * @return
     */
    @Override
    public boolean isNoSave() {
        return noSave;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public List<ItemAndListCommonInterface> getPotentialWorkTimeProvidersInPrioOrderOLD() {
//        boolean includeOwner = true;
//        List<ItemAndListCommonInterface> potentialProviders = new ArrayList();
//
//        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
////UI: if an item has BOTH own workSlots AND a category with WorkSLots, then it will first try to get workTime from its own workslots (otherwise, why would it have them?!)
//        WorkSlotList ownWorkSlotList = getWorkSlotListN();
//        if (ownWorkSlotList != null && ownWorkSlotList.size() > 0) {//size() returns number of *future* workslots only!
//            potentialProviders.add(this);
//        }
//
////        ItemAndListCommonInterface owner = false && includeOwner ? getOwner() : null;
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null && !MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.mayProvideWorkTime()) {
//            if (true || !potentialProviders.contains(owner)) { //no need to test here(?)
//                potentialProviders.add(owner);
//            }
//        }
//
//        List<Category> categories = getCategories();
//        for (Category cat : categories) {
//            if (cat.mayProvideWorkTime()) {
//                if (true || !potentialProviders.contains(cat)) { //no need to test here(?) since categories should never have duplicates
//                    potentialProviders.add(cat);
//                }
//            }
//        }
//
//        if (owner != null && MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.mayProvideWorkTime()) {
//            if (true || !potentialProviders.contains(owner)) {
//                potentialProviders.add(owner);
//            }
//        }
//
//        return potentialProviders;
//    }
//</editor-fold>
    public List<ItemAndListCommonInterface> getOtherPotentialWorkTimeProvidersInPrioOrderN() {
        List<ItemAndListCommonInterface> potentialProviders = null;// = new ArrayList();

        ItemAndListCommonInterface owner = getOwner();
        if (owner != null && !MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.mayProvideWorkTime()) {
            if (potentialProviders == null) {
                potentialProviders = new ArrayList(); //only allocate if/when actually needed (likely to be relatively rare)
            }
            potentialProviders.add(owner);
        }

        List<Category> categories = getCategories();
        for (Category cat : categories) {
            if (cat.mayProvideWorkTime()) {
                if (potentialProviders == null) {
                    potentialProviders = new ArrayList(); //only allocate if/when actually needed (likely to be relatively rare)
                }
                potentialProviders.add(cat);
            }
        }

        if (owner != null && MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.mayProvideWorkTime()) {
            if (potentialProviders == null) {
                potentialProviders = new ArrayList(); //only allocate if/when actually needed (likely to be relatively rare)
            }
            potentialProviders.add(owner);
        }

        return potentialProviders; //potentialProviders.size()>=0?potentialProviders:null;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getNeededWorkTime() {
//        return getRemainingEffort();
//    }
//    public List<WorkTime> getAllocatedWorkTimeN() {
//    public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface subtask) {
//    public WorkTimeSlices getAllocatedWorkTimeN() {
//        long neededWorkTime = getNeededWorkTime();
//        int subtaskIndex = getList().indexOf(subtask);
//        WorkTimeSlices availableWorkTime;
//        WorkTimeSlices newWorkTime;
//        if (subtaskIndex < 0) {
//            return new WorkTimeSlices();
//        } else if (subtaskIndex == 0) {
//            while (neededWorkTime > 0) {
//                newWorkTime.setStartTime(availableWorkTime.getStartTime());
//                WorkSlot w = availableWorkTime.getWorkSlotsN().get(0);
//                newWorkTime.addWorkSlot(w);
//                if (w.getDurationAdjusted(availableWorkTime.getStartTime()), subtaskIndex
//
//                {
//                    ))
//                }
//            }else
//        long
//
//        }
//        neededWorkTime = getNeededWorkTime();
//        WorkTimeSlices workTime = null;
//        for (ItemAndListCommonInterface workTimeProvider : getWorkTimeProviders()) {
////                if (workTimeProvider==this) return Arrays.asList(new WorkTimeSlices(getWorkSlotListN(),0,Long.MAX_VALUE));
//            if (workTimeProvider == this) {
//                return new WorkTimeSlices(getWorkSlotListN(), 0, Long.MAX_VALUE);
//            }
//            if (workTime == null) {
//                workTime = workTimeProvider.getAllocatedWorkTimeN(this);
//            } else {
//                workTime.setNextWorkTime(new WorkTimeSlices(workTime, 0, 0, workTime));
//            }
//            neededWorkTime -= workTime.getAllocatedTime();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public WorkTimeSlices getAllocatedWorkTimeN() {
////        long neededWorkTime = getNeededWorkTime();
//        long neededWorkTime = getRemainingEffort();
//        WorkTimeSlices workTime = null;
//        WorkTimeSlices lastWorkTime;
////        for (ItemAndListCommonInterface workTimeProvider : getWorkTimeProviders()) {
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            if (workTime == null) {
//                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
//                workTime = lastWorkTime;
//            } else if (workTime.getRemainingDuration() > 0) {
////                workTime=workTimeProviders.getAllocatedWorkTimeN(workTime,workTimeProvider.getAllocatedWorkTimeN(this, workTime.getUncoveredTime()));
//                lastWorkTime = workTimeProvider.getWorkTimeAllocatorN().getAllocatedWorkTimeN(this, workTime.getRemainingDuration());
//                workTime.setNextWorkTime(lastWorkTime);
//            } else {
//                lastWorkTime = null;
//                assert false;
//            }
////            if (lastWorkTime != null) {
//            neededWorkTime -= lastWorkTime.getAllocatedDuration(false); //only deduct
////            }
//        }
//        return workTime;
//    }
//    @Override
//    public WorkTimeDefinition getWorkTimeAllocatorN() {
//        return getWorkTimeAllocatorN(false);
//    }
//</editor-fold>

    @Override
//    public long getWorkTimeRequiredFromOwner() {
    public long getWorkTimeRequiredFromProvider(ItemAndListCommonInterface provider) {
        if (false) {
            Log.p("getWorkTimeRequiredFromProvider(provider=" + provider + ") for item=" + this);
        }
        if (isDone()) {
            return 0;
        }
        if (Config.WORKTIME_DETAILED_LOG) {
            Log.p("-> .getWorkTimeRequiredFromProvider(" + provider + ") for Item \"" + this + "\"");
        }

        long requiredCalc = 0;
        if (Config.TEST) {
            //get the amount of worktime the subtasks require from this (their mother project)
            if (isProject()) {
                List<ItemAndListCommonInterface> subtasks = getList(); //getListFull(); //NOT full list since elsewhere we only calculate finishTime for what is seen!
                for (ItemAndListCommonInterface subtask : subtasks) { //starts from the *last* element in the list!!!
//                    requiredCalc += subtask.getRemaining();
                    requiredCalc += subtask.getWorkTimeRequiredFromProvider(this);
                }
                requiredCalc += getRemainingForProjectTaskItself(false); //false=> no default effort (for a project with subtasks, only subtasks should have default values)
            } else { //leaf task
                requiredCalc = getRemaining(); //how much total workTime is required?
            }
        }
        long required = getRemaining(true); //includes subtasks and own time
        if (false && Config.TEST) {
            ASSERT.that(required == requiredCalc, this + " -getWorkTimeRequiredFromProvider(): wrong Remaining, calculated="
                    + MyDate.formatDurationShort(requiredCalc, true) + ", getRemaining()=" + MyDate.formatDurationShort(required, true));
        }

        WorkSlotList ownWorkSlots = getWorkSlotListN();
        if (ownWorkSlots != null) {
            required -= ownWorkSlots.getWorkTimeSum(); //deduct own worktime since that's always used first
        }
        if (required > 0) {
            //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
            List<ItemAndListCommonInterface> providers = getOtherPotentialWorkTimeProvidersInPrioOrderN();
            if (providers != null) {
                for (ItemAndListCommonInterface prov : providers) {
                    if (prov == provider || prov.equals(provider)) { //prov == provider) {
                        if (Config.WORKTIME_DETAILED_LOG) {
                            ASSERT.that(prov == provider && prov.equals(provider), () -> "Two instances of same provider!! prov=" + prov + ", provider=" + provider);
                        }
                        if (Config.WORKTIME_DETAILED_LOG) {
                            Log.p("   .getWorkTimeRequiredFromProvider - break since reached prov=\"" + prov + "\"");
                        }
                        return required; // stop iteration when we get to provider itself and return what is remaining for provider to deliver
//                break; // stop iteration when we get to provider itself and return what is remaining for provider to deliver
                    } else {
                        if (Config.WORKTIME_DETAILED_LOG) {
                            ASSERT.that(!prov.equals(provider), () -> "duplicate object instances for prov=" + prov + ", this=" + this);
                        }
                        if (prov instanceof Category && ((Category) prov).isOwnerOfItemInCategoryBeforeItem(this)) { //special case to avoid infinite loops
                            if (Config.WORKTIME_DETAILED_LOG) {
                                Log.p("-> .getWorkTimeRequiredFromProvider - trying to get worktime from Category (" + prov + ") AND isOwnerOfItemInCategoryBeforeItem(" + this + "\") is true");
                            }
                            return 0;
                        }
///<editor-fold defaultstate="collapsed" desc="comment">
//                WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(false);
//                if (workTimeAllocator != null) {
//                    WorkTimeSlices wt = workTimeAllocator.getAllocatedWorkTime(this, required);
//                    if (wt != null) {
//                        required = wt.getRemainingDuration(); //required = wt != null ? wt.getRemainingDuration() : required; //set remaining to any duration that could not be allocated by this provider
//                        if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getWorkTimeRequiredFromProvider - got workTime from (" + prov + ") allocated==" + MyDate.formatDuration(wt.getAllocatedDuration()) + ", remaining==" + MyDate.formatDuration(required));
//                    } else {
//                        if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getWorkTimeRequiredFromProvider - got a NULL workTime from (" + prov + ") allocated==" + MyDate.formatDuration(wt.getAllocatedDuration()) + ", remaining==" + MyDate.formatDuration(required));
//                    }
//                }
//                WorkTimeSlices wt=prov.getAllocatedWorkTimeN(this);
//</editor-fold>
                        WorkTimeSlices wt = prov.getAllocatedWorkTimeN(this);
                        if (wt != null) //                        required = wt.getRemainingDuration(); //required = wt != null ? wt.getRemainingDuration() : required; //set remaining to any duration that could not be allocated by this provider
                        {
                            required -= wt.getAllocatedDuration(); //required = wt != null ? wt.getRemainingDuration() : required; //set remaining to any duration that could not be allocated by this provider
                        }
                    }
                    if (Config.WORKTIME_DETAILED_LOG) {
                        ASSERT.that(required >= 0, "required has become negative=" + required + ", Item=" + this + ", providers=" + providers);
                    }
                    if (required == 0) {
                        return 0; //other higher prio providers allocated all required worktime so return here, don't go through other providers
                    }
                }
            }
            if (Config.WORKTIME_DETAILED_LOG) {
                Log.p("<  .getWorkTimeRequiredFromProvider(" + provider + ") for Item \"" + this + "\" returning " + required);
            }
        }
        return required; //whatever was not supplied by one of the 
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getWorkTimeRequiredFromProviderOLD(ItemAndListCommonInterface provider) {
//        if (false) {
//            Log.p("getWorkTimeRequiredFromProvider(provider=" + provider + ") for item=" + this);
//        }
//
//        if (isDone()) {
//            return 0;
//        }
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p("-> .getWorkTimeRequiredFromProvider(" + provider + ") for Item \"" + this + "\"");
//        }
//
//        //get the amount of worktime the subtasks require from this (their mother project)
//        long required = 0;
////        if (subtasks != null && subtasks.size() > 0) {
//        if (isProject()) {
//            List<ItemAndListCommonInterface> subtasks = getListFull();
//            for (ItemAndListCommonInterface subtask : subtasks) { //starts from the *last* element in the list!!!
////                required += subtask.getWorkTimeRequiredFromProvider(this);
//                required += subtask.getRemaining();
//            }
//        } else { //leaf task
//            required = getRemaining(); //how much total workTime is required?
//        }
//
//        //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//        List<ItemAndListCommonInterface> providers = getPotentialWorkTimeProvidersInPrioOrder();
////        if (providers != null) {
////            ItemAndListCommonInterface prov;
////            for (int i = 0, size = providers.size(); i < size; i++) {
//        for (ItemAndListCommonInterface prov : providers) {
////                prov = providers.get(i);
//            if (prov.equals(provider)) { //prov == provider) {
//                if (Config.WORKTIME_DETAILED_LOG) {
//                    Log.p("   .getWorkTimeRequiredFromProvider - break since reached prov=\"" + prov + "\"");
//                }
//                break; // stop iteration when we get to provider itself and return what is remaining for provider to deliver
//            } else {
//                ASSERT.that(!prov.equals(provider), () -> "duplicate object instances for prov=" + prov + ", this=" + this);
//                if (prov instanceof Category && ((Category) prov).isOwnerOfItemInCategoryBeforeItem(this)) {
//                    if (Config.WORKTIME_DETAILED_LOG) {
//                        Log.p("-> .getWorkTimeRequiredFromProvider - trying to get worktime from Category (" + prov + ") AND isOwnerOfItemInCategoryBeforeItem(" + this + "\") is true");
//                    }
//                    return 0;
//                }
//                WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(false);
//                if (workTimeAllocator != null) {
//                    WorkTimeSlices wt = workTimeAllocator.getAllocatedWorkTime(this, required);
//                    if (wt != null) {
//                        required = wt.getRemainingDuration(); //required = wt != null ? wt.getRemainingDuration() : required; //set remaining to any duration that could not be allocated by this provider
//                        if (Config.WORKTIME_DETAILED_LOG) {
//                            Log.p("-> .getWorkTimeRequiredFromProvider - got workTime from (" + prov + ") allocated==" + MyDate.formatDuration(wt.getAllocatedDuration()) + ", remaining==" + MyDate.formatDuration(required));
//                        }
//                    } else {
//                        if (Config.WORKTIME_DETAILED_LOG) {
//                            Log.p("-> .getWorkTimeRequiredFromProvider - got a NULL workTime from (" + prov + ") allocated==" + MyDate.formatDuration(wt.getAllocatedDuration()) + ", remaining==" + MyDate.formatDuration(required));
//                        }
//                    }
//                }
//            }
//
//            assert required >= 0;
//            if (required == 0) { //other higher prio providers allocated all required worktime
//                return 0;//required; //return here, don't go through other providers
//            }
//        }
////        }
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p("<  .getWorkTimeRequiredFromProvider(" + provider + ") for Item \"" + this + "\" returning " + required);
//        }
//
//        return required;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
////    public long getWorkTimeRequiredFromOwner() {
//    public long getWorkTimeRequiredFromOwner() {
//        long remaining = 0;
//        List<ItemAndListCommonInterface> subtasks = getList();
//        if (subtasks != null && subtasks.size() > 0) {
//            for (ItemAndListCommonInterface elt : subtasks) {
////                remaining += elt.getWorkTimeRequiredFromOwner(provider);
//                remaining += elt.getWorkTimeRequiredFromOwner();
//            }
//        } else { //leaf task
//            remaining = getRemainingEffort(); //how much total workTime is required?
//
//            List<ItemAndListCommonInterface> workTimeProviderList = getPotentialWorkTimeProvidersInPrioOrder(false);
//            if (workTimeProviderList != null) {
////                Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
//                for (ItemAndListCommonInterface prov : getPotentialWorkTimeProvidersInPrioOrder(false)) {
////                while (workTimeProviders.hasNext()) {
//                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////                    ItemAndListCommonInterface prov = workTimeProviders.next();
////                if (prov == provider) {
////                    return remaining;
////                } else {
////                    remaining -= prov.getAllocatedWorkTimeN(this).getAllocatedDuration(); //deduct time allocated by higher prioritized provider
//                    remaining = prov.getAllocatedWorkTimeN(this, remaining).getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
////                    remaining = 0;
////                    return remaining;
////                }
//                }
//            }
//        }
//        return remaining;
////<editor-fold defaultstate="collapsed" desc="comment">
////    }
////        else {
////            remaining = 0;
////            for (ItemAndListCommonInterface elt : subtasks) {
////                remaining += elt.getWorkTimeRequiredFromThisProvider(provider);
////            }
////            return remaining;
////        }
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public long getWorkTimeRequiredFromOwner(ItemAndListCommonInterface provider) {
//        long remaining = 0;
//        List<ItemAndListCommonInterface> subtasks = getList();
//        if (subtasks != null && subtasks.size() > 0) {
//            for (ItemAndListCommonInterface elt : subtasks) {
//                remaining += elt.getWorkTimeRequiredFromOwner(provider);
//            }
//        } else { //leaf task
//            remaining = getRemainingEffort(); //how much total workTime is required?
//
////            List<ItemAndListCommonInterface> workTimeProviderList = getPotentialWorkTimeProvidersInPrioOrder();
////            Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
//            for (ItemAndListCommonInterface prov : getPotentialWorkTimeProvidersInPrioOrder()) {
////            while (workTimeProviders.hasNext()) {
//                //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//                //                ItemAndListCommonInterface prov = workTimeProviders.next();
//                if (prov == provider) {
//                    return remaining;
//                } else {
//                    remaining -= prov.getAllocatedWorkTimeN(this).getAllocatedDuration(); //deduct time allocated by higher prioritized provider
////                    remaining = 0;
////                    return remaining;
//                }
//            }
//        }
//        return remaining;
////<editor-fold defaultstate="collapsed" desc="comment">
////    }
////        else {
////            remaining = 0;
////            for (ItemAndListCommonInterface elt : subtasks) {
////                remaining += elt.getWorkTimeRequiredFromThisProvider(provider);
////            }
////            return remaining;
////        }
////</editor-fold>
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getAllocatedWorkTimeOLD() {
//        boolean reset = false;
////        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
//        WorkTimeSlices workTime = null;
//        ItemAndListCommonInterface owner = getOwner();
////        long remaining = getRemainingEffort();
//        long remaining = -9999;//getWorkTimeRequiredFromThisProvider();
//
//        if (owner != null && remaining > 0 && !MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean()) {
//            if (owner != null) {
////                WorkTimeSlices ownerWT = owner.getAvailableWorkTime();
//                WorkTimeDefinition ownerWTD = owner.getWorkTimeAllocatorN(reset);
//                if (ownerWTD != null) {
//                    workTime = ownerWTD.getAllocatedWorkTimeN(this, remaining);
//                    if (workTime != null) {
//                        remaining -= workTime.getRemainingDuration();
//                    }
//                }
//            }
//        }
//
//        if (remaining > 0) {
//            List<Category> categories = getCategories(); //TODO optimize: get only categories with workTimeDefs
//            for (Category cat : categories) { //UI: workTime allocated in the order that categories are defined/added??
//                WorkTimeDefinition catWTD = cat.getWorkTimeAllocatorN(reset);
//                if (catWTD != null) {
//                    WorkTimeSlices newWorkTime = catWTD.getAllocatedWorkTimeN(this, remaining);
//                    if (workTime == null) {
//                        workTime = newWorkTime;
//                    } else {
//                        workTime.addWorkTime(newWorkTime); //OK if newWorkTime is null
//                    }
//                    if (newWorkTime != null) {
//                        remaining = newWorkTime.getRemainingDuration(); //use the new remaining duration
//                    }
//                }
//            }
//        }
//
//        if (owner != null && remaining > 0 && MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean()) {
//            WorkTimeDefinition ownerWTD = owner.getWorkTimeAllocatorN(reset);
//            if (ownerWTD != null) {
//                WorkTimeSlices newWorkTime = ownerWTD.getAllocatedWorkTimeN(this, remaining);
//                if (workTime == null) {
//                    workTime = newWorkTime;
//                } else {
//                    workTime.addWorkTime(newWorkTime); //OK if newWorkTime is null
//                }
//                if (newWorkTime != null) {
//                    remaining = newWorkTime.getRemainingDuration(); //use the new remaining duration
//                }
//            }
//        }
//
//        return workTime;
//    }
//</editor-fold>
    @Override
    public boolean mayProvideWorkTime() {
        if (ItemAndListCommonInterface.super.mayProvideWorkTime()) { //if has own workslots, or an owner (or owner's owner etc...)
            return true;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        WorkSlotList workSlots = getWorkSlotListN();
//        if (workSlots != null && workSlots.size() > 0) {
//            return true;
//        }
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null && owner.mayProvideWorkTime()) {
//            return true;
//        }
//</editor-fold>
        for (Category cat : getCategories()) {
            if (cat.mayProvideWorkTime()) {
                return true;
            }
        }
        return false;
//        return getPotentialWorkTimeProvidersInPrioOrder() != null; //TODO optimization - is there a more efficient way?
    }

    public WorkTimeSlices getAllocatedWorkTimeN() {
        return getAllocatedWorkTimeN(this);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public WorkTimeAllocator getWorkTimeAllocatorNXXX(boolean reset) {
//        if (neverCacheWorkTimeAllocator || workTimeAllocator == null || reset) {
//            if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getWorkTimeAllocator(" + reset + ") for Item \"" + this + "\"");
//
////            WorkTimeSlices availableWorkTime = getAvailableWorkTime();
//            WorkTimeSlices availableWorkTime = getAllocatedWorkTimeN();
//            if (availableWorkTime != null) {
////                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), availableWorkTime);
////                wtd = new WorkTimeAllocator(getList(), availableWorkTime, this);
//                workTimeAllocator = new WorkTimeAllocator(availableWorkTime, this);
//            }
//        }
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("<  .getWorkTimeAllocator(" + reset + ") for Item \"" + this + "\"returning=" + workTimeAllocator.toString());
//        return workTimeAllocator;
//    }
//
//    public WorkTimeAllocator getWorkTimeAllocatorN_newXXX(boolean reset) {
//        if (neverCacheWorkTimeAllocator || workTimeAllocator == null || reset) {
//            if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getWorkTimeAllocator(" + reset + ") for Item \"" + this + "\"");
//
////            WorkTimeSlices availableWorkTime = getAvailableWorkTime();
//            WorkTimeSlices availableWorkTime = getAllocatedWorkTimeN();
//            if (availableWorkTime != null) {
////                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), availableWorkTime);
////                wtd = new WorkTimeAllocator(getList(), availableWorkTime, this);
//                workTimeAllocator = new WorkTimeAllocator(availableWorkTime, this);
//            }
//        }
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("<  .getWorkTimeAllocator(" + reset + ") for Item \"" + this + "\"returning=" + workTimeAllocator.toString());
//        return workTimeAllocator;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeDefinition getWorkTimeAllocatorN(boolean reset) {
//        if (wtd != null && !reset) {
//            return wtd;
//        } else { //get right workSlots
//            WorkSlotList workSlots = getWorkSlotListN();
//            if (workSlots != null && workSlots.hasComingWorkSlots()) {
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), workSlots);
//                return wtd;
//            } else {
//                WorkTimeDefinition workTimeDef;
//                ItemAndListCommonInterface owner = getOwner();
//                if (owner != null) {
//                    workTimeDef = owner.getWorkTimeAllocatorN(reset);
//                    if (workTimeDef != null) {
//                        return workTimeDef;
//                    }
//                }
//                List<Category> categories = getCategories();
//                for (Category cat : categories) {
//                    workTimeDef = cat.getWorkTimeAllocatorN(reset);
//                    if (workTimeDef != null) {
//                        return workTimeDef;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeDefinition getWorkTimeDefinitionOLD(boolean reset) {
//        if (wtd == null || reset) { //            wtd = new WorkTimeDefinition(itemListOrg.getWorkSlotListN(true), itemListFilteredSorted);
////            wtd = new WorkTimeDefinition(getList(), this);
////            wtd = new WorkTimeDefinition(getList(), getWorkSlotListN());
//            WorkSlotList workSlots = getWorkSlotListN();
//            if (workSlots != null && !workSlots.isEmpty()) {
////            wtd = new WorkTimeDefinition(getLeafTaskSAsList(item -> !item.isDone()) , getWorkSlotListN());
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), workSlots);
//            }
//        }
//        return wtd;
//    }
//</editor-fold>
    /**
     * forces a recalculation of workTime
     */
    @Override
    public void resetWorkTimeDefinition() {
        workTimeAllocator = null;
        for (Object subtask : getListFull()) {
            if (subtask instanceof Item) {// && ((Item)subtask).isProject()) {
                ((Item) subtask).resetWorkTimeDefinition();
            } else {
                assert false;
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    WorkTimeSlices allocate2(long remaining) {
//        WorkTimeAllocator workTimeAllocator = null;
//        if (workTimeAllocator == null)
//            workTimeAllocator = new WorkTimeAllocator(new WorkTimeSlices(getWorkSlotListN(false)), this);
//        return null;
//    }
//
//    WorkTimeSlices allocate(long remaining) {
//        boolean reset = false;
//        WorkTimeSlices workTime = null;
//        //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocatorN(reset);
//        if (workTimeAllocator != null) {
////                WorkTimeSlices allocatedWorkTimeSlices = workTimeAllocator.getAllocatedWorkTime(this, remaining);
//            return workTimeAllocator.getAllocatedWorkTime(this, remaining);
////                if (allocatedWorkTimeSlices != null) {
////                    if (workTime == null) {
////                        workTime = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
////                    }
////                    workTime.addWorkTime(allocatedWorkTimeSlices);
////                    remaining = allocatedWorkTimeSlices.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
////                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
////                        return workTime;
////                    } else
////                }
////            }
//        }
//        return null;
//    }
//</editor-fold>
    public WorkTimeAllocator getWorkTimeAllocatorN() {
        if (workTimeAllocator == null && mayProvideWorkTime()) {
            workTimeAllocator = new WorkTimeAllocator(this);
        }
        return workTimeAllocator;
    }

    public void setWorkTimeAllocator(WorkTimeAllocator workTimeAllocator) {
        this.workTimeAllocator = workTimeAllocator;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices allocate2(ItemAndListCommonInterface elt) {
//        List<ItemAndListCommonInterface> potentialProviders = null;
//        WorkSlotList workSlotList;
//        WorkTimeSlices workTimeSlices = null;
//        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocator();
//        //if no workTimeAllocator already there (cached), if there are workSlots or owners with workTime get as much as needed or available
//        if (workTimeAllocator == null && ((workSlotList = getWorkSlotListN(false)) != null || (potentialProviders = getPotentialWorkTimeProvidersInPrioOrder()) != null)) {
//            long remaining = getWorkTimeRequiredFromProvider(this); //calculate how much time is needed from this' subtasks
////            workTimeAllocator = new WorkTimeAllocator(null, null);
//            if (workSlotList != null) {
//                workTimeAllocator = new WorkTimeAllocator(workTimeSlices, this); //first add own workTime
//            }
//            long availWorktime = workTimeAllocator != null ? workTimeAllocator.getAvailableTime() : 0;
//            if (remaining > availWorktime) { //if need additional workTime
//                remaining -= availWorktime; //if need additional workTime
//
////          List<ItemAndListCommonInterface> potentialProviders = getPotentialWorkTimeProvidersInPrioOrder();
//                for (ItemAndListCommonInterface prov : potentialProviders) {
//                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////<editor-fold defaultstate="collapsed" desc="comment">
////                    WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(reset);
////                    if (workTimeAllocator != null) {
////                        WorkTimeSlices allocatedWorkTime = workTimeAllocator.getAllocatedWorkTime(this, remaining);
////                        if (allocatedWorkTime != null) {
////                            if (workTimeSlices == null) {
////                                workTimeSlices = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
////                            }
////                            workTimeSlices.addWorkTime(allocatedWorkTime);
////                            workTimeAllocator.addWorkTimeSlices(prov.allocateNew(this));
////                            remaining = allocatedWorkTime.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
////                            if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
////                                break;
////                            }
////                        }
////                    }
////</editor-fold>
//                    workTimeAllocator.addWorkTimeSlices(prov.allocate2(this));
//                    remaining = workTimeAllocator.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//        setWorkTimeAllocator(workTimeAllocator); //set the updated version
//        //if we managed to allocate some worktime above
//        if (workTimeAllocator != null) {
//            return workTimeAllocator.getAllocatedWorkTimeNew(elt);
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//    public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface elt) {
//        WorkTimeAllocator workTimeAllocator = getWorkTimeAllocator();
//        if (workTimeAllocator != null) {
//            return workTimeAllocator.getAllocatedWorkTime(elt);
//        } else {
//            return null;
//        }
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeSlices getAllocatedWorkTimeOLD() {
////        boolean reset = false;
////        return getWorkTimeAllocatorN().getAllocatedWorkTimeN(this);
////        ItemAndListCommonInterface owner = getOwner();
////        long remaining = getRemainingEffort();
//
//        WorkTimeSlices workTime = null;
//        WorkTimeSlices newWorkTime = null;
//        ItemAndListCommonInterface provider;
//
////        long requiredWT = getWorkTimeRequiredFromOwner(this); //only get the workTime required from this provider (e.g. subtasks with own categories may have been served directly at their level)
//        long requiredWT = getWorkTimeRequiredFromProvider(this); //only get the workTime required from this provider (e.g. subtasks with own categories may have been served directly at their level)
//
//        List<ItemAndListCommonInterface> workTimeProviderList = getPotentialWorkTimeProvidersInPrioOrder(); //only allocate from own workSlots or categories to ripple up what owner must allocate
////        if (workTimeProviderList != null) {
//        Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
//        while (requiredWT > 0 && workTimeProviders.hasNext()) {
//            //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//            provider = workTimeProviders.next();
////            newWorkTime = provider.getAllocatedWorkTimeN(this);
////            newWorkTime = provider.getAllocatedWorkTimeN(this);
//            newWorkTime = provider.allocateWorkTime(this, requiredWT);
//            if (workTime == null) {
//                workTime = newWorkTime;
//            } else {
//                workTime.addWorkTime(newWorkTime);
//            }
//            requiredWT = newWorkTime.getRemainingDuration();
//        }
////        }
//        return workTime;
//    }
//</editor-fold>
//    public WorkTimeSlices getAllocatedWorkTimeN() {
//        return getAllocatedWorkTimeN(false);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns workTime for the item at index itemIndex and for an effort of
//     * remainingTime. NB. The work time may have been allocated from several different sources.
//     *
//     * @return null if no workTime
//     */
////    public WorkTimeSlices getAllocatedWorkTimeN(boolean reset) {
//    @Override
//    public WorkTimeSlices getAllocatedWorkTimeN() {
//        boolean reset = false;
//
//        WorkTimeSlices workTime = null;
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getAllocatedWorkTime(" + reset + ") for Item \"" + this + "\"");
//
//        //Calculate how much time this task requires (either simply Remaining, or sum of remaining that the subtasks require from this - their project (knowing that subtasks in categories may get worktime from there as well))
//        long remaining = 0;
//        List<? extends ItemAndListCommonInterface> subtasks = getListFull();
//        if (subtasks != null && subtasks.size() > 0) { //I'm a project
//            for (ItemAndListCommonInterface subt : subtasks) {
//                remaining += subt.getWorkTimeRequiredFromProvider(this); //remaining of last subtask will hold any unallocated required worktime
//            }
//            remaining += getRemainingForProjectTaskItself(true); //add the project task's own work time (if any)
//        } else { //I'm a leaf task
//            remaining = getRemaining(); //how much total workTime is required?
//        }
//        //now iterate over workTimeProviders (returned by prio order) and get as much as possible from each
//        List<ItemAndListCommonInterface> potentialProviders = getPotentialWorkTimeProvidersInPrioOrder();
//        for (ItemAndListCommonInterface prov : potentialProviders) {
//            //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//            WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(reset);
//            if (workTimeAllocator != null) {
//                WorkTimeSlices allocatedWorkTime = workTimeAllocator.getAllocatedWorkTime(this, remaining);
//                if (allocatedWorkTime != null) {
//                    if (workTime == null) {
//                        workTime = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
//                    }
//                    workTime.addWorkTime(allocatedWorkTime);
//                    remaining = allocatedWorkTime.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("<  .getAllocatedWorkTime(" + reset + ") for Item \"" + this.toString() + "\" returning " + (workTime != null ? workTime.toString() : "<null>"));
//        return workTime;
//    }
//
//    public WorkTimeSlices getAllocatedWorkTimeN_new() {
//        boolean reset = false;
//
//        WorkTimeSlices workTime = null;
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("-> .getAllocatedWorkTime(" + reset + ") for Item \"" + this + "\"");
//
//        //Calculate how much time this task requires (either simply Remaining, or sum of remaining that the subtasks require from this - their project (knowing that subtasks in categories may get worktime from there as well))
//        long remaining = 0;
//        List<? extends ItemAndListCommonInterface> subtasks = getListFull();
//        if (subtasks != null && subtasks.size() > 0) { //I'm a project
//            for (ItemAndListCommonInterface subt : subtasks) {
//                remaining += subt.getWorkTimeRequiredFromProvider(this); //remaining of last subtask will hold any unallocated required worktime
//            }
//            remaining += getRemainingForProjectTaskItself(true); //add the project task's own work time (if any)
//        } else { //I'm a leaf task
//            remaining = getRemaining(); //how much total workTime is required?
//        }
//
//        //now iterate over workTimeProviders (returned by prio order) and get as much as possible from each
//        List<ItemAndListCommonInterface> potentialProviders = getPotentialWorkTimeProvidersInPrioOrder();
//        for (ItemAndListCommonInterface prov : potentialProviders) {
//            //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//            WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(reset);
//            if (workTimeAllocator != null) {
//                WorkTimeSlices allocatedWorkTime = workTimeAllocator.getAllocatedWorkTime(this, remaining);
//                if (allocatedWorkTime != null) {
//                    if (workTime == null) {
//                        workTime = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
//                    }
//                    workTime.addWorkTime(allocatedWorkTime);
//                    remaining = allocatedWorkTime.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//        if (Config.WORKTIME_DETAILED_LOG) Log.p("<  .getAllocatedWorkTime(" + reset + ") for Item \"" + this.toString() + "\" returning " + (workTime != null ? workTime.toString() : "<null>"));
//        return workTime;
//    }
//
//    public WorkTimeSlices getAllocatedWorkTimeNOLD() {
//        boolean reset = false;
//        WorkTimeSlices workTime = null;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false && !(workTime == null || forceWorkTimeCalculation || reset)) { //true: don't cache values in Item, only cache WorkTimeAllocator
////            return workTime;
////        } else {
////</editor-fold>
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p("-> .getAllocatedWorkTime(" + reset + ") for Item \"" + this + "\"");
//        }
//        //Calculate how much time this task requires (either simply Remaining, or sum of remaining that the subtasks require from this - their project (knowing that subtasks in categories may get worktime from there as well))
//        long remaining = 0;
//        List<? extends ItemAndListCommonInterface> subtasks = getListFull();
//        if (subtasks != null && subtasks.size() > 0) { //I'm a project
//            for (ItemAndListCommonInterface elt : subtasks) {
////                remaining += elt.getAllocatedWorkTimeN().getRemainingDuration(); //remaining of last subtask will hold any unallocated required worktime
//                remaining += elt.getWorkTimeRequiredFromProvider(this); //remaining of last subtask will hold any unallocated required worktime
//            }
//        } else { //I'm a leaf task
//            remaining = getRemaining(); //how much total workTime is required?
//        }
//        //now iterate over workTimeProviders (returned by prio order) and get as much as possible from each
//        List<ItemAndListCommonInterface> potentialProviders = getPotentialWorkTimeProvidersInPrioOrder();
////            if (providers != null) {
//        for (ItemAndListCommonInterface prov : potentialProviders) {
//            //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////                if (prov != owner) {
//            if (false && prov == this) {
//                //UI: if a task allocates its own workslots to itself, still only allocate the exact time needed so that finishTime gets right. !!!TODO how to show if there is unallocated workTime?
//            }
//            WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocatorN(reset);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (true ||wtd != null) {
////                        ASSERT.that(wtd!=null,"WTD should never ne null for a workTimeProvider");
////                    WorkTimeSlices wt = prov.getWorkTimeAllocatorN(reset).getAllocatedWorkTimeN(this, remaining);
////</editor-fold>
//            if (workTimeAllocator != null) {
//                WorkTimeSlices allocatedWorkTime = workTimeAllocator.getAllocatedWorkTime(this, remaining);
////                        remaining = wt != null ? wt.getRemainingDuration() : remaining; //set remaining to any duration that could not be allocated by this provider
////                        if (forceWorkTimeCalculation || workTime == null) {
//                if (allocatedWorkTime != null) {
//                    if (workTime == null) {
//                        workTime = new WorkTimeSlices(); //only allocate WorkTimeSlices if there is actually some workTime to return
//                    }
//                    workTime.addWorkTime(allocatedWorkTime);
////<editor-fold defaultstate="collapsed" desc="comment">
////                    }
//////                    remaining = (wt != null) ? wt.getRemainingDuration() : remaining; //set remaining to any duration that could not be allocated by this provider
////                    if (wt != null) {
////                    }
////                    if (wt != null && remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
////</editor-fold>
//                    remaining = allocatedWorkTime.getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
//                    if (remaining == 0) { //only stop allocating workTime if remaining is 0 *after* some workTime wt was allocated
//                        break;
//                    }
//                }
//            }
//        }
//        if (Config.WORKTIME_DETAILED_LOG) {
//            Log.p("<  .getAllocatedWorkTime(" + reset + ") for Item \"" + this.toString() + "\" returning " + (workTime != null ? workTime.toString() : "<null>"));
//        }
//        return workTime;
//    }
//</editor-fold>
    /**
     * set workTime, especially reset to null to force recalculation.
     *
     * @param workTime
     */
//    public void setAllocatedWorkTime(WorkTimeSlices workTime) {
//        this.workTime = workTime;
//    }
    private long getLatestSubtaskFinishTime() {
        long latestFinishTime = MyDate.MIN_DATE;
        Item subtask;
        for (Object subt : getListFull()) {
            if (subt instanceof Item && !(subtask = (Item) subt).isDone()) { //AndListCommonInterface) {
//                     subtask = (Item) subt;
                long finishT = subtask.getFinishTime();
//                    if (finishT > latestFinishTime && !subtask.isDone()) {
                if (finishT > latestFinishTime) {
                    latestFinishTime = finishT;
                }
            }
        }
        return latestFinishTime;
    }

    @Override
    public long getFinishTime() {
//        long latestFinishTime = MyDate.MIN_DATE;
        if (isProject()) { //UI: for projects, finishTime is ALWAYS latest finishTime for subtasks (or undefined if all subtasks are Done)
//<editor-fold defaultstate="collapsed" desc="comment">
//            Item subtask;
//            for (Object subt : getList()) {
//                if (subt instanceof Item && !(subtask = (Item) subt).isDone()) { //AndListCommonInterface) {
////                     subtask = (Item) subt;
//                    long finishT = subtask.getFinishTime();
////                    if (finishT > latestFinishTime && !subtask.isDone()) {
//                    if (finishT > latestFinishTime) {
//                        latestFinishTime = finishT;
//                    }
//                }
//            }
//</editor-fold>
            long latestFinishTime = getLatestSubtaskFinishTime(); //super.getFinishTime() is the project task's own finishTime in case it has its own Remaining
            long projTaskFinishTime = ItemAndListCommonInterface.super.getFinishTime(); //super.getFinishTime() is the project task's own finishTime in case it has its own Remaining
            if (projTaskFinishTime != MyDate.MAX_DATE && projTaskFinishTime > latestFinishTime) {
                latestFinishTime = projTaskFinishTime;
            }
//            if (latestFinishTime != MyDate.MIN_DATE) { //only return if we actually have a date (all subtasks may be Done)
            return latestFinishTime == MyDate.MIN_DATE ? MyDate.MAX_DATE : latestFinishTime;
//            }
        } else {
            return ItemAndListCommonInterface.super.getFinishTime();
        }
    }

    /**
     * returns the latest date the project or any subtask was updated
     *
     * @return
     */
    public Date getLastModifiedDateProjectOrSubtasks() {
        long lastSubtaskUpdate = getLastModifiedDate(); //if ever project task itself is updated the last, return that date. 
        long temp;
        if (isProject()) {
            for (Object o : getListFull()) {
                if (o instanceof Item) {
                    temp = ((Item) o).getLastModifiedDateProjectOrSubtasks().getTime(); //cache since potentially heavy operation
                    if (temp > lastSubtaskUpdate) {
                        lastSubtaskUpdate = temp;
                    }
                }
            }
        }
        return new Date(lastSubtaskUpdate);
//        return getUpdatedAt();
    }

//public void setNewFieldValue(Item newItem, String field, Item itemBefore, Item itemAfter) {
    @Override
    public void setNewFieldValue(String fieldParseId, Object objectBefore, Object objectAfter) {
        Item newItem = (Item) this;
        Item itemBefore = (Item) objectBefore;
        Item itemAfter = (Item) objectAfter;
        switch (fieldParseId) {
            case Item.PARSE_CHALLENGE:
                newItem.setChallenge(itemBefore.getChallengeN()); //UI: same prio as item just before
                break;
            case Item.PARSE_PRIORITY:
//            int diffPrio = itemAfter.getPriority() -itemBefore.getPriority() ;
//            newItem.setPriority(itemBefore.getPriority()+diffPrio/2);
                newItem.setPriority(itemBefore.getPriority()); //UI: same prio as item just before
                break;
            case Item.PARSE_DREAD_FUN_VALUE:
                newItem.setDreadFunValue(itemBefore.getDreadFunValueN()); //UI: same prio as item just before
                break;
            case Item.PARSE_IMPORTANCE:
                newItem.setImportance(itemBefore.getImportanceN()); //UI: same prio as item just before
                break;
            case Item.PARSE_URGENCY:
                newItem.setUrgency(itemBefore.getUrgencyN()); //UI: same prio as item just before
                break;
            case Item.PARSE_EARNED_VALUE:
                newItem.setEarnedValue(itemBefore.getEarnedValue()); //UI: same prio as item just before
                break;
            case Item.PARSE_EFFORT_ESTIMATE:
                newItem.setEstimate(itemBefore.getEstimate()); //UI: same prio as item just before
                break;
            case Item.PARSE_ACTUAL_EFFORT:
                newItem.setActual(itemBefore.getActual(), false); //UI: same prio as item just before
                break;
            case Item.PARSE_REMAINING_EFFORT:
                newItem.setRemaining(itemBefore.getRemaining(), false); //UI: same prio as item just before
                break;
            case Item.PARSE_INTERRUPTED_TASK:
                newItem.setInteruptOrInstantTask(itemBefore.isInteruptOrInstantTask()); //UI: same prio as item just before
                break;
            case Item.PARSE_STARRED:
                newItem.setStarred(itemBefore.isStarred()); //UI: same prio as item just before
                break;
            case Item.PARSE_STATUS:
                newItem.setStatus(itemBefore.getStatus()); //UI: same prio as item just before
                break;
            case Item.PARSE_ALARM_DATE: //TODO!!! do same for all date fields
                newItem.setAlarmDate(itemBefore.getAlarmDateD()); //UI: same prio as item just before
                break;
        }
    }

    public boolean isFilterSortDefInherited(FilterSortDef filterSortDef) {
        return filterSortDef != null && getOwner() != null && filterSortDef.equals(getOwner().getFilterSortDef());
    }

    /**
     * set and save filter (and resets the filtered/sorted list)
     *
     * @param filterSortDef
     */
    @Override
    public void setFilterSortDef(FilterSortDef filterSortDef) {
//        if (filterSortDef != null && !filterSortDef.equals(getDefaultFilterSortDef())) { //only save filter for subtasks if modified!
        if (//filterSortDef == null || //if filter is deleted 
                filterSortDef.equals(getDefaultFilterSortDef()) //if new filter is 'just' default filter
                || isFilterSortDefInherited(filterSortDef)) { //or if filter is the same as the inherited filter (either edited back to same value or just not changed)
            remove(PARSE_FILTER_SORT_DEF);
        } else {
//            if (!isNoSave())  //otherwise temporary filters for e.g. Overdue will be saved //NO, now Overdue will be a saved (temporarily) list, but other lists (Statistics?) may still be temporary
            if (filterSortDef.getObjectIdP() == null && !isNoSave()) {
                DAO.getInstance().saveInBackground(filterSortDef); //
            }
            put(PARSE_FILTER_SORT_DEF, filterSortDef);
        }
    }

    @Override
    public FilterSortDef getFilterSortDef() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null) {
//            return filterSortDef;
//        } else {
//            return null;
//        }
//</editor-fold>
        FilterSortDef filterSortDef = (FilterSortDef) getParseObject(PARSE_FILTER_SORT_DEF);
        if (filterSortDef != null) {
            filterSortDef = (FilterSortDef) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(filterSortDef);
            return filterSortDef;
        } else {
            ItemAndListCommonInterface owner = getOwner();
            FilterSortDef filter;
            if (owner != null && (filter = owner.getFilterSortDef()) != null) {
                return filter;
            } else if (MyPrefs.useDefaultFilterInItemsWhenNoneDefined.getBoolean()) {
                return getDefaultFilterSortDef();
            }
        }
        return null;
    }

    public Object get(int index) {
        return getList().get(index);
    }

    private EventDispatcher changeDispatcher;

    /**
     * Adds a listener to the switch which will cause an event on change
     *
     * @param l implementation of the action listener interface
     */
    public void addChangeListener(ActionListener l) {
        changeDispatcher.addListener(l);
    }

    /**
     * Removes the given change listener from the switch
     *
     * @param l implementation of the action listener interface
     */
    public void removeChangeListener(ActionListener l) {
        changeDispatcher.removeListener(l);
    }

    void fireChangeEvent() {
        changeDispatcher.fireActionEvent(new ActionEvent(this, ActionEvent.Type.Change));
    }

    public String hasReferencesToUnsavedParseObjects() {
        String str = "";
        String sep = "";

        if (getOwner() == null) {
            str += "No Owner";
        } else if (getOwner().getObjectIdP() == null) {
            str += " | No ObjId for owner:" + getOwner();
        }

        if (getCategories() != null && getCategories().size() > 0) {
            for (Category cat : getCategories()) {
                if (cat.getObjectIdP() == null) {
                    str += " | No ObjId for Category:" + cat;
                }
            }
        }
        if (getListFull() != null && getListFull().size() > 0) {
            for (Item subtask : (List<Item>) getListFull()) {
                if (subtask.getObjectIdP() == null) {
                    str += "No ObjId for subtask:" + subtask;
                }
            }
        }
        if (getRepeatRule() != null && getRepeatRule().getObjectIdP() == null) {
            str += " | No ObjId for RepeatRule:" + getRepeatRule();
        }
        if (getTaskInterrupted() != null && getTaskInterrupted().getObjectIdP() == null) {
            str += " | No ObjId for TaskInterrupted:" + getTaskInterrupted();
        }
        if (getSource() != null && getSource().getObjectIdP() == null) {
            str += " | No ObjId for Source:" + getSource();
        }

        return str;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Date getFinishTime(ItemAndListCommonInterface subtask) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
//            return new Date(workTimeDef.getFinishTime(subtask));
//        } else {
//            return new Date(0);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getFinishTime() {
//        if (hasWorkTimeDefinition()) {
//            return WorkTimeDefinitionNew()
//        } else {
//
//        }
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
//            return new Date(workTimeDef.getFinishTime(this));
//        } else {
//            return new Date(0);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getFinishTimeOLD() {
//        Object owner = getOwner();
//        
//        if (owner instanceof ItemList) {
//            ItemList itemList = (ItemList) owner;
//            Date finish = itemList.getFinishTime(this);
//            if (finish != null && finish.getTime() != 0) {
//                return finish;
//            }
//        } else {
//            //TODO!!! add support for lists of lists
//        }
//        return new Date(0);
//    }
//</editor-fold>
}
