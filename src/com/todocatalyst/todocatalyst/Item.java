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
import com.todocatalyst.todocatalyst.MyDate;
import static com.todocatalyst.todocatalyst.MyForm.getListAsCommaSeparatedString;
import com.todocatalyst.todocatalyst.MyPrefs;
import static com.todocatalyst.todocatalyst.Util.removeTrailingPrecedingSpacesNewLinesEtc;
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

    public static String CLASS_NAME = "Item";
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

    /**
     * Copied from CN1 OnOffSwitch.java
     *
     */
    private EventDispatcher dispatcher = new EventDispatcher();
    private EventDispatcher listeners = new EventDispatcher();

//    private List<WorkSlot> workSlotListBuffer;
    private WorkSlotList workSlotListBuffer;
//    private static WorkTimeDefinition wtd; //calculated when needed
    private WorkTimeAllocator wtd; //calculated when needed
    private WorkTime workTime;// = new ItemList(); //lazy

//    private WorkTimeDefinition workTimeDefinitionBuffer;
    public Item() {
        super(CLASS_NAME);
    }

    public Item(Item source) {
        this();
        source.copyMeInto(this);
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
        setRemainingEffort(((long) remainingEffortInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
        setDueDate(dueDate);
    }

    public Item(String taskText, int remainingEffortInMinutes, Date dueDate, boolean saveToDAO) {
        this();
        setText(taskText);
        setRemainingEffort(((long) remainingEffortInMinutes) * MyDate.MINUTE_IN_MILLISECONDS);
        setDueDate(dueDate);
        if (saveToDAO) {
            DAO.getInstance().save(this);
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
            List itemList = getList();
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

    public List getChildrenListOLD(Object parent) {
        if (parent == null) {
            List list = new ArrayList();
            list.add(this);
            return list;
        } else {
            List itemList = ((Item) parent).getList();
//            DAO.getInstance().fetchAllElementsInSublist(itemList, false);
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(itemList);
//            return new Vector(itemList);
            return itemList;
        }
    }

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
    @Override
    public boolean isLeaf(Object node) {
//        return getItemList() == null || getItemList().size() == 0;
        Item item = (Item) node;
//        ItemList itemList = item.getItemList();
        List itemList = item.getList();
        return itemList == null || itemList.size() == 0;
    }

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
    @Override
//    public void insertIntoListAndSaveListAndInstance(RepeatRuleObjectInterface orgInstance, RepeatRuleObjectInterface repeatRuleObject) {
    public void insertIntoListAndSaveListAndInstance(RepeatRuleObjectInterface newRepeatRuleInstance) {
//        DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save first to be able to reference from other objects
//        RepeatRuleObjectInterface orgInstance = this;
        Object owner = getOwner();
        int index;
        if (owner instanceof ItemList) {
            ItemList ownerList = (ItemList) owner;
//            assert orgInstance != null;
            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()
                    && (index = ownerList.indexOf(this)) != -1) {
//                ownerList.add(index + 1, newRepeatRuleInstance); //+1: insert *after* orgInstance
                ownerList.addToList(index + 1, (ItemAndListCommonInterface) newRepeatRuleInstance); //+1: insert *after* orgInstance
            } else {
//                ownerList.addToList(MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : ownerList.size(), (ItemAndListCommonInterface) newRepeatRuleInstance);
                ownerList.addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : ownerList.size(), (ItemAndListCommonInterface) newRepeatRuleInstance);
            }
            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
            DAO.getInstance().save(ownerList); //TODO!!!! optimization: when generating multiple repeat instances, do the save of the list at the end
        } else if (owner instanceof Item) {
            Item itemOwner = (Item) owner;
            List subtaskList = itemOwner.getList();
//            if (subtaskList != null && (index = subtaskList.indexOf(newRepeatRuleInstance)) != -1) {
//                subtaskList.add(index + 1, newRepeatRuleInstance);
//            } else {
//                subtaskList.add(MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : subtaskList.size(), newRepeatRuleInstance);
//            }
            assert subtaskList != null; //should never get null
            if (MyPrefs.insertNewRepeatInstancesJustAfterRepeatOriginator.getBoolean()
                    //                    && (index = subtaskList.indexOf(newRepeatRuleInstance)) != -1) {
                    && (index = subtaskList.indexOf(this)) != -1) {
                itemOwner.addToList(index + 1, (ItemAndListCommonInterface) newRepeatRuleInstance); //add just after 
            } else {
                itemOwner.addToList(MyPrefs.insertNewItemsInStartOfLists.getBoolean() ? 0 : subtaskList.size(),
                        (ItemAndListCommonInterface) newRepeatRuleInstance);
            }
//            item.setList(subtaskList); //done in addToList
            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
            DAO.getInstance().save(itemOwner);
        } else { //no owner, save for 'Inbox'
//            assert false;
            DAO.getInstance().save((ParseObject) newRepeatRuleInstance); //save before saving ownerList, but *after* adding to list and setting owner, to be able to reference from other objects
        }
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
    public enum HighMediumLow {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        HIGH("High"), MEDIUM("Medium"), LOW("Low");

        HighMediumLow(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        static String[] getDescriptionList() {
            return new String[]{LOW.getDescription(), MEDIUM.getDescription(), HIGH.getDescription()};
        }

        static int[] getDescriptionOrdinals() {
            return new int[]{LOW.ordinal(), MEDIUM.ordinal(), HIGH.ordinal()};
        }

        /**
         * returns the enum corresponding to the description string
         *
         * @param description
         * @return enum or null if no enum value corresponds to the description
         * string
         */
        static HighMediumLow getValue(String description) {
            String[] descList = getDescriptionList();
            for (int i = 0, size = getDescriptionList().length; i < size; i++) {
                if (descList[i].equals(description)) {
                    return HighMediumLow.values()[getDescriptionOrdinals()[i]]; //values() return an array of the ordinals
                }
            }
            return null;
        }

        private final String description;
    }

    public enum DreadFunValue {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        FUN("Fun"), NEUTRAL("Neutral"), DREAD("Dread");

        private final String description;

        DreadFunValue(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        //returns description strings for the enum in display order (which may be different from the sort order which follows the declaration order
        static String[] getDescriptionList() {
            return new String[]{FUN.getDescription(), NEUTRAL.getDescription(), DREAD.getDescription()};
        }

        static int[] getDescriptionValues() {
            return new int[]{FUN.ordinal(), NEUTRAL.ordinal(), DREAD.ordinal()};
        }

        /**
         * returns the enum corresponding to the description string
         *
         * @param description
         * @return enum or null if no enum value corresponds to the description
         * string
         */
        static DreadFunValue getValue(String description) {
            String[] descList = getDescriptionList();
            for (int i = 0, size = getDescriptionList().length; i < size; i++) {
                if (descList[i].equals(description)) {
                    return DreadFunValue.values()[getDescriptionValues()[i]]; //values() return an array of the ordinals
                }
            }
            return null;
        }
    }

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
    class BufferedValue {

        BufferedValue(UpdaterInterface updater) {
            this.updater = updater;
        }
        boolean needsRecalculation = true;
        Object value;
        UpdaterInterface updater;

        void reset() {
            needsRecalculation = true;
        }

        Object getValue() {
            if (needsRecalculation) {
                value = updater.getValue();
                needsRecalculation = false;
            }
            return value;
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private BufferedValue derivedRemainingEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
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
//    private BufferedValue derivedEstimateEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
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
//    private BufferedValue derivedActualEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
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
    final static String DESCRIPTION_HINT = "DESCRIPTION_HINT"; //"New task"; // "Task text"
    //        final static String FIELD_DONE = "Done", Expr.VALUE_FIELD_TYPE_STRING),
    final static String DONE = "DONE"; //"Done";
    final static String DUE_DATE = "DUE_DATE"; //"Due";
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
    final static String COMMENT_HINT = "Enter your notes";
    final static String EFFORT_ESTIMATE = "Estimate"; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_ESTIMATE_SHORT = "Estimate"; //"Estimate" // "Effort estimate";
//    final static String EFFORT_ESTIMATE_HELP = "Estimates let you compare your expected amount of work with your actual amount of work. Is set automatically the first time you enter " + Item.EFFORT_REMAINING + "."; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_ESTIMATE_HELP = "Estimates let you compare your expected amount of work with your actual amount of work. Is set automatically the first time you enter [EFFORT_REMAINING]."; //"Estimate" // "Effort estimate";, "Estimated time"
    final static String EFFORT_ACTUAL = "Time worked"; //"Actual effort";"Time spent"
//    final static String EFFORT_ACTUAL_HELP = "The amount of " + EFFORT_ACTUAL + " you have spend on this task. It is updated automatically when you use the Timer while working on tasks."; //"Actual effort";"Time spent"
    final static String EFFORT_ACTUAL_HELP = "The amount of [EFFORT_ACTUAL] you have worked on this task. It is updated automatically when you use the Timer while working on tasks."; //"Actual effort";"Time spent"
//        String actualExplanation = "Setting '" + Item.EFFORT_ACTUAL + "' will automatically set " + Item.STATUS + " to " + ItemStatus.ONGOING;
    final static String EFFORT_REMAINING = "Remaining effort"; //"Remaining effort"; "Remaining time"
    final static String EFFORT_REMAINING_SHORT = "Remaining"; //"Remaining effort";
    final static String EFFORT_REMAINING_HELP = "The amount of effort in hours:minutes that is remaining on this task. You can update it for partially finished tasks. The Timer can be set to prompt you to update it every time you move to another task without finishing the current one (Setting)."; //"Remaining effort"; "Remaining time"
    final static String EFFORT_ESTIMATE_SUBTASKS = "Estimate effort, subtasks"; //"Effort estimate";"Estimated time (subtasks)"
//    final static String EFFORT_ESTIMATE_SUBTASKS_HELP = "The sum of the " + EFFORT_ESTIMATE + " of all subtasks"; //"Effort estimate";"Estimated time (subtasks)"
    final static String EFFORT_ESTIMATE_SUBTASKS_HELP = "The sum of the [EFFORT_ESTIMATE] of all subtasks"; //"Effort estimate";"Estimated time (subtasks)"
    final static String EFFORT_ACTUAL_SUBTASKS = "Worked effort, subtasks"; //"Actual effort";, "Time spent (subtasks)"
//    final static String EFFORT_ACTUAL_SUBTASKS_HELP = "The sum of the " + EFFORT_ACTUAL + " of all subtasks"; //"Actual effort";, "Time spent (subtasks)"
    final static String EFFORT_ACTUAL_SUBTASKS_HELP = "The sum of the [EFFORT_ACTUAL] of all subtasks"; //"Actual effort";, "Time spent (subtasks)"
    final static String EFFORT_REMAINING_SUBTASKS = "Remaining effort, subtasks"; //"Remaining effort";"Remaining time (subtasks)"
//    final static String EFFORT_REMAINING_SUBTASKS_HELP = "The sum of the " + EFFORT_REMAINING+" of all subtasks"; //"Remaining effort";"Remaining time (subtasks)"
    final static String EFFORT_REMAINING_SUBTASKS_HELP = "The sum of the [EFFORT_REMAINING] of all subtasks"; //"Remaining effort";"Remaining time (subtasks)"
    final static String EFFORT_ESTIMATE_PROJECT = "Estimated effort, project"; //"Effort estimate";"Estimated time (project)"
//    final static String EFFORT_ESTIMATE_PROJECT_HELP = EFFORT_ESTIMATE+" for the project. You can use this to indicate a total estimate for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_ESTIMATE_PROJECT_HELP = "[EFFORT_ESTIMATE] for the project. You can use this to indicate a total estimate for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_ACTUAL_PROJECT = "Work effort, project"; //"Actual effort";"Time spent (project)"
//    final static String EFFORT_ACTUAL_PROJECT_HELP = EFFORT_ACTUAL+" for the project. You can use this to capture "+EFFORT_ACTUAL+" that is not captured on the individual subtasks."; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_ACTUAL_PROJECT_HELP = "[EFFORT_ACTUAL] for the project. You can use this to capture [EFFORT_ACTUAL] that is not captured on the individual subtasks."; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_REMAINING_PROJECT = "Remaining effort, project"; //"Remaining effort";"Remaining time (project)"
//    final static String EFFORT_REMAINING_PROJECT_HELP = EFFORT_REMAINING+" for the project. You can use this to **?? indicate a total for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_REMAINING_PROJECT_HELP = "[EFFORT_REMAINING] for the project. You can use this to **?? indicate a total for a project before defining its subtasks (or even before realizing that it should be a project)"; //"Effort estimate";"Estimated time (project)"
    final static String EFFORT_TOTAL_SHORT = "Total effort"; //total effort in Timer (previous actual + timer elapsed time)
    final static String STATUS = "Status"; //"Status""Task status"
    final static String PRIORITY = "Priority";
    final static String PRIORITY_HELP = "Priority";
    final static String PROJECT = "Project";
    final static String EARNED_POINTS_PER_HOUR = "Value/hour"; //"Value per hour (based on Estimated time)"; //"Value/Effort"; "Value per hour"
    //Item.EARNED_POINTS_PER_HOUR + " is calculated as " + Item.EARNED_POINTS + " divided by " + Item.EFFORT_ESTIMATE + ", and once work has started by the sum of " + Item.EFFORT_REMAINING + " and " + Item.EFFORT_ACTUAL + "."
    final static String EARNED_POINTS_PER_HOUR_HELP = "Value/hour**"; //"Value per hour (based on Estimated time)"; //"Value/Effort"; "Value per hour"
    final static String EARNED_POINTS = "Value";
//    final static String EARNED_POINTS_HELP = "Indicate any number that represents the value of this task or project. It can be a monetary value or your own scale for value. Used to calculate "+EARNED_POINTS_PER_HOUR+" which for example allows you to prioritize the tasks with the highest return on investment in terms of value by hour.";
    final static String EARNED_POINTS_HELP = "Indicate any number that represents the value of this task or project. It can be a monetary value or your own scale for value. Used to calculate [EARNED_POINTS_PER_HOUR] which for example allows you to prioritize the tasks with the highest return on investment in terms of value by hour.";
    //"Earned value (in currency or points)"
    final static String ALARM_DATE = "Reminder"; // "Alarm date", "Alarm"
    final static String ALARM_DATE_HELP = "Set a Reminder for this task. Shown as a local notification on your phone or device when the app is not running, or as a reminder ** when you are using the app."; // "Alarm date", "Alarm"
    final static String WAIT_UNTIL_DATE = "Waiting until"; // "Wait until date" "Wait until"
    final static String WAIT_UNTIL_DATE_HELP = "**Waiting until"; // "Wait until date" "Wait until"
    final static String WAIT_WHEN_SET_WAITING_DATE = "Waiting since"; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting"
//    final static String WAIT_WHEN_SET_WAITING_DATE_HELP = "The time when this task was set "+ItemStatus.WAITING+". Is automatically set. "; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting"
    final static String WAIT_WHEN_SET_WAITING_DATE_HELP = "The time when this task was set [WAITING]. Is automatically set. "; // + ItemStatus.WAITING.toString(); //referencing enum String not allowed "Wait until date" "Wait until" "Date when set Waiting", "Set automatically when a task is set Waiting"
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
    final static String BELONGS_TO = "Owner List/Project"; //"Belongs to";
    final static String BELONGS_TO_HELP = "Indicates the List or Project this task belongs to. Change to move the task to another List or Project or delete to move to Inbox";
    final static String DEPENDS_ON = "Depends on";
    final static String DEPENDS_ON_HELP = "Indicates that this task depends on another task. Dependent tasks can automatically be hidden until the task they depend on is completed.";
    final static String SOURCE = "Copy of"; //Template or Task that this one is a copy of, "Task copy of"
    final static String SOURCE_HELP = "Shows the task was copied from. E.g. for tasks created using templates, automatically repeating tasks or copy/paste. Can be useful for example to find all instances of a given template. "; //Template or Task that this one is a copy of, "Task copy of"
    final static String OBJECT_ID = "Id"; //"Unique id"
    final static String OBJECT_ID_HELP = "An internal unique identifier. This may be useful if requesting support"; //"Unique id"
    final static String STARRED = "Starred"; //"Unique id"
    final static String STARRED_HELP = "Tasks can be marked with a Star to emphasize them**"; //"Unique id"

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
    final static String PARSE_REMAINING_EFFORT = "remainingEffort";
    final static String PARSE_ACTUAL_EFFORT = "actualEffort";
//    final static String PARSE_SHOW_FROM_DATE = "showFromDate";
    final static String PARSE_CATEGORIES = "categories";
    final static String PARSE_PRIORITY = "priority";
    final static String PARSE_STARRED = "starred";
    final static String PARSE_EARNED_VALUE = "earnedValue";
    final static String PARSE_COMPLETED_DATE = "completedDate";
    final static String PARSE_IMPORTANCE = "importance";
    final static String PARSE_URGENCY = "urgency";
    final static String PARSE_OWNER_LIST = "ownerList";
    final static String PARSE_OWNER_ITEM = "ownerItem";
    final static String PARSE_OWNER_TEMPLATE_LIST = "templateList";
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
    final static String PARSE_FIRST_ALARM = "firstAlarm"; //first-coming/next-coming alarm (to allow easy search in Parse)
    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?
    final static String PARSE_SNOOZE_DATE = "snoozeDate"; //date until which the 
//    final static String PARSE_ = "";

    /**
     * returns an Object containing the value of the given field (Object, since
     * this is used in Expr for filtering)
     *
     * @param fieldId
     * @return
     */
    public Object getFilterField(int fieldId) {
        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
            case FIELD_DESCRIPTION:
                return getText(); //optimization: replace by variable description directly
            case FIELD_CATEGORIES:
                return getCategories(); //optimization: replace by variable description directly
            case FIELD_DONE:
                return isDone();
            case FIELD_STATUS:
//                return new Integer(getStatus());
                return getStatus();
            case FIELD_DUE_DATE:
                return getDueDate();
            case FIELD_MODIFIED_DATE:
                return getLastModifiedDate();
            case FIELD_COMPLETED_DATE:
                return getCompletedDate();
            case FIELD_CREATED_DATE:
                return getCreatedDate();
            case FIELD_COMMENT:
                return getComment();
            case FIELD_EFFORT_ESTIMATE:
//                return new Long(getEffortEstimate());
                return new Duration(getEffortEstimate());
            case FIELD_EFFORT_ACTUAL:
//                return new Long(getActualEffort());
                return new Duration(getActualEffort());
            case FIELD_EFFORT_REMAINING:
//                return new Long(getRemainingEffort());
                return new Duration(getRemainingEffort());
            case FIELD_PRIORITY:
                return getPriority();
            case FIELD_EARNED_POINTS:
                return getEarnedValue();
            case FIELD_EARNED_POINTS_PER_HOUR:
                return getEarnedValuePerHour();
            case FIELD_STARTED_ON_DATE:
                return getStartedOnDate();
            case FIELD_START_BY_TIME:
                return getStartByDate();
//            case FIELD_START_WORK_TIME:
//                return getStartTime();
            case FIELD_FINISH_WORK_TIME:
//                return new Long(getFinishTime());
                return null;
            case FIELD_EFFORT_TOTAL:
                return getTotalExpectedEffort();
            case FIELD_TIMESPAN:
                return getTimeSpan();
            case FIELD_DREAD_FUN:
                return getDreadFunValue();
            case FIELD_CHALLENGE:
                return getChallenge();
            default:
                ASSERT.that("Item: Field Identifier not defined " + fieldId);
        }
        return null;
    }

    public void setFilterField(int fieldId, Object fieldValue) {
        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
            case FIELD_DESCRIPTION:
                setText((String) fieldValue); //optimization: replace by variable description directly
                break;
            case FIELD_CATEGORIES:
                setCategories((List) fieldValue); //optimization: replace by variable description directly
                break;
            case FIELD_DONE:
                setDone(((Boolean) fieldValue));
                break;
            case FIELD_STATUS:
//                setStatus(((Integer) fieldValue).intValue());
                setStatus((ItemStatus) fieldValue);
                break;
            case FIELD_DUE_DATE:
                setDueDate(((Date) fieldValue).getTime());
                break;
//            case FIELD_MODIFIED_DATE:
////                setLastModifiedDate(((Date)fieldValue).getTime());
//                setLastModifiedDate(((Long) fieldValue));
//                break;
            case FIELD_COMPLETED_DATE:
//                setCompletedDate(((Date)fieldValue).getTime());
                setCompletedDate(((Long) fieldValue));
                break;
//            case FIELD_CREATED_DATE:
////                setCreatedDate(((Date)fieldValue).getTime());
//                setCreatedDate(((Long) fieldValue));
//                break;
            case FIELD_COMMENT:
                setComment((String) fieldValue);
                break;
            case FIELD_EFFORT_ESTIMATE:
//                setEffortEstimate(((Duration)fieldValue).getDays());
                setEffortEstimate(((Long) fieldValue));
                break;
            case FIELD_EFFORT_ACTUAL:
//                setActualEffort(((Duration)fieldValue).getDays());
                setActualEffort(((Long) fieldValue));
                break;
            case FIELD_EFFORT_REMAINING:
//                setRemainingEffort(((Duration)fieldValue).getDays());
                setRemainingEffort(((Long) fieldValue));
                break;
            case FIELD_PRIORITY:
                setPriority(((Integer) fieldValue));
                break;
            case FIELD_EARNED_POINTS:
                setEarnedValue(((Double) fieldValue));
                break;
            case FIELD_EARNED_POINTS_PER_HOUR:
                //do nothing, value is calculated and cannot be set
                ASSERT.that("Should not call setFilterField for FIELD_EARNED_POINTS_PER_HOUR");
                break;
            case FIELD_STARTED_ON_DATE:
//                setStartedOnDate(((Date)fieldValue).getTime());
                setStartedOnDate(((Long) fieldValue));
            case FIELD_START_BY_TIME:
//                setStartByDate(((Date)fieldValue).getTime());
                setStartByDate(((Long) fieldValue));
            case FIELD_START_WORK_TIME:
                //do nothing, value is calculated and cannot be set
                ASSERT.that("Should not call setFilterField for FIELD_START_WORK_TIME");
                break;
            case FIELD_FINISH_WORK_TIME:
                //do nothing, value is calculated and cannot be set
                ASSERT.that("Should not call setFilterField for FIELD_FINISH_WORK_TIME");
                break;
            case FIELD_EFFORT_TOTAL:
                //do nothing, value is calculated and cannot be set
                ASSERT.that("Should not call setFilterField for FIELD_EFFORT_TOTAL");
                break;
            case FIELD_TIMESPAN:
                //do nothing, value is calculated and cannot be set
                ASSERT.that("Should not call setFilterField for FIELD_EFFORT_TOTAL");
                break;
            case FIELD_DREAD_FUN:
                setDreadFunValue(((DreadFunValue) fieldValue));
                break;
            case FIELD_CHALLENGE:
                setChallenge(((Challenge) fieldValue));
                break;
            default:
                ASSERT.that("Item.setFilterField: Field Identifier not defined " + fieldId);
        }
    }

    public boolean isDone() {
        ItemStatus status = getStatus();
        return status == ItemStatus.DONE || status == ItemStatus.CANCELLED;  //CANCELLED since this makes a Cancelled task flip back to Created when clicked in a list
    }

    /**
     * returns true if status==WAITING and WaitingTillDate is not reached yet
     * (if date is defined). Serves to hide waiting items until the date is
     * reached.
     *
     * @return
     */
    public boolean isWaiting() {
        return getStatus() == ItemStatus.WAITING && (System.currentTimeMillis() < getWaitingTillDate() || getWaitingTillDate() == 0);
//    ItemStatus status = getStatus();
//        if (statusreturn getStatus() == ItemStatus.WAITING && (System.currentTimeMillis()<getWaitingTillDate()||getWaitingTillDate()==0 && ); 
    }

    @Override
    public boolean isNoLongerRelevant() {
        return isDone();
    }

    @Override
    public ItemList getOwnerList() {
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
    private void setOwnerList(ItemList ownerList) {
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
        return hierarchyStr;
    }

    public String getOwnerHierarchyAsString() {
        if (getOwner() instanceof Item) {
            return getOwnerHierarchyAsString(getOwnerHierarchy());
        } else {
            return "";
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

//    @Override
    public void setOwnerItem(Item ownerItem) {
//        if (has(PARSE_OWNER_ITEM) || ownerItem != null) {
//            put(PARSE_OWNER_ITEM, ownerItem);
//        }
        if (ownerItem != null) {
            put(PARSE_OWNER_ITEM, ownerItem);
        } else {
            remove(PARSE_OWNER_ITEM);
        }
    }

    public void setOwnerAndMoveFromOldOwner(ItemAndListCommonInterface owner) {
        setOwnerAndMoveFromOldOwner(owner, true);
    }

    private void setOwnerAndMoveFromOldOwner(ItemAndListCommonInterface owner, boolean saveChangedItems) {
        ItemAndListCommonInterface oldOwner = getOwner();
        if (oldOwner != null) {
            oldOwner.removeFromList(this); //remove item from previous owner's list (remove first to remove Owner)
            if (saveChangedItems) {
//            DAO.getInstance().saveInBackground((ParseObject) oldOwner); //save the list from which subtask was removed
                DAO.getInstance().save((ParseObject) oldOwner); //save the list from which subtask was removed
            }
        }
        if (owner != null) {
            owner.addToList(this); //insert at head of sublist
            if (saveChangedItems) {
                DAO.getInstance().save((ParseObject) owner); //save project onto which subtask was dropped
            }
        }
        if (false && saveChangedItems) { //false: no need to save Item since will be only be used when saving an edited item
            DAO.getInstance().save((ParseObject) this);
        }
    }

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
    @Override
    public void setOwner(ItemAndListCommonInterface owner) {
        ASSERT.that(owner == null || (owner instanceof ParseObject && ((ParseObject) owner).getObjectIdP() != null), "Setting owner that is not ParseObject or without ObjectId for item=" + this + ", owner=" + owner);
//        if (owner instanceof Category) {
//            setOwnerCategory((Category) owner);
//        } else 
        if (owner instanceof TemplateList) {
            setOwnerTemplateList((TemplateList) owner);
        } else if (owner instanceof ItemList) {
            setOwnerList((ItemList) owner);
        } else if (owner instanceof Item) {
            setOwnerItem((Item) owner);
        } else if (owner == null) {
            setOwnerList(null);
            setOwnerItem(null);
            setOwnerTemplateList(null);
        } else {
            ASSERT.that(false, "unknown owner type for " + owner);
        }
    }

//    public ParseObject getOwner() {
    @Override
    public ItemAndListCommonInterface getOwner() {
        ItemAndListCommonInterface owner;
        if ((owner = getOwnerItem()) != null) { //NB! ownerItem must be tested first, since ownerList now returns top-level owner list!!
            return owner;
        } else if ((owner = getOwnerList()) != null) {
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
                ASSERT.that(false, "unknown type of owner for Item:" + this + ", owner:" + ownerObj);
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

    @Override
    public boolean isExpandable() {
        return getItemListSize() > 0;

    }

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
    public void copyMeInto(Item destiny) {
        copyMeInto(destiny, CopyMode.COPY_ALL_FIELDS);
    }

    void copyMeInto(Item destination, CopyMode copyFieldDefintion) {
        copyMeInto(destination, copyFieldDefintion, 0);
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
    void copyMeInto(Item destination, CopyMode copyFieldDefintion, int copyExclusions) {
        copyMeInto(destination, copyFieldDefintion, copyExclusions, false);
    }

    void copyMeInto(Item destination, CopyMode copyFieldDefintion, int copyExclusions, boolean setRepeatRuleWithoutUpdate) {

        /**
         * copy for all types of copies
         */
        boolean all = (copyFieldDefintion == CopyMode.COPY_ALL_FIELDS);
        /**
         * copy for Repeat Instances
         */
        boolean toRepeatInst = (copyFieldDefintion == CopyMode.COPY_TO_REPEAT_INSTANCE);
        /**
         * copy for Templates
         */
        boolean toTempl = (copyFieldDefintion == CopyMode.COPY_TO_TEMPLATE);
        /**
         * copy for Templates
         */
        boolean fromTempl = (copyFieldDefintion == CopyMode.COPY_FROM_TEMPLATE);
        /**
         * copy for Copy/Paste copies (same as for templates??)
         */
        boolean copyPaste = (copyFieldDefintion == CopyMode.COPY_TO_COPY_PASTE);

        if (toTempl) {
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
        if (all || toRepeatInst || toTempl || fromTempl || copyPaste) {

            //TEXT
//            if ((copyExclusions & COPY_EXCLUDE_TEXT) == 0) { //UI: DOESN'T make sense to not copy task description (especially with projects)
            if (destination.getText().equals("")) { //copy from template, iff nothing's already set for item
                destination.setText(getText());
            }
//            }

            //EFFORT ESTIMATE
//            destination.setEffortEstimate(getEffortEstimate());
            if ((copyExclusions & COPY_EXCLUDE_EFFORT_ESTIMATE) == 0) {
                if (destination.getEffortEstimate() == 0) { //copy from template, iff nothing's already set for item
                    destination.setEffortEstimate(getEffortEstimate(), fromTempl || toRepeatInst, true); //ensure remaining is set
                }
            }
            //CHALLENGE
            if ((copyExclusions & COPY_EXCLUDE_CHALLENGE) == 0) {
                if (destination.getChallenge() == null) { //copy from template, iff nothing's already set for item
                    destination.setChallenge(getChallenge());
                }
            }
            //DREAD / FUN
            if ((copyExclusions & COPY_EXCLUDE_DREAD_FUN) == 0) {
                if (destination.getDreadFunValue() == null) { //copy from template, iff nothing's already set for item
                    destination.setDreadFunValue(getDreadFunValue());
                }
            }
            //IMPORTANCE / URGENCY
            if ((copyExclusions & COPY_EXCLUDE_IMP_URG) == 0) {
                if (destination.getImpUrgPrioValue() == 0) { //copy from template, iff nothing's already set for item
                    destination.setImportance(getImportance());
                    destination.setUrgency(getUrgency());
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
            if (destination.getObjectIdP() == null) {
                DAO.getInstance().save(destination); //need to save destination before we can save copies of subtasks with it as owner or add it to categories
            }

            //CATEGORIES - always only ADD categories, to avoid removing any manually set before adding the template
            if ((copyExclusions & COPY_EXCLUDE_CATEGORIES) == 0) {
                if (toTempl) { //only set the categories in the template, but DON'T update the categories 
//                    destination.updateCategories(new ArrayList(getCategories()), fromTempl); //when fromTempl: only add additional categories from the template, don't remove any manually added before
//                    destination.updateCategories(new ArrayList(getCategories()), true); //when fromTempl: only add additional categories from the template, don't remove any manually added before
                    destination.updateCategories(getCategories());
                } else if (fromTempl) { //always only ADD additional categories set in the template, to avoid removing any manually set before adding the template
                    List<Category> newCatList = new ArrayList(destination.getCategories());
                    newCatList.addAll(getCategories());
                    destination.updateCategories(newCatList, true);
                } else { //CATEGORIES - always only ADD categories, to avoid removing any manually set before adding the template
                    destination.updateCategories(getCategories()); //when fromTempl: only add additional categories from the template, don't remove any manually added before
                }
            }

            //SUBTASKS
            if ((copyExclusions & COPY_EXCLUDE_SUBTASKS) == 0) {
//                List<Item> subtaskCopy = new ArrayList();
                List<Item> subtaskCopy;
                if (fromTempl) {
                    subtaskCopy = destination.getList(); //if copying from a template, *add* template subtasks to any existing subtasks!
                } else {
                    subtaskCopy = new ArrayList();
                }
                List<Item> orgSubtasks = getList();
//                DAO.getInstance().fetchAllElementsInSublist(orgSubtasks, true);

                for (int i = 0, size = orgSubtasks.size(); i < size; i++) {
//                    Item copy = orgSubtasks.get(i).cloneMe(copyFieldDefintion, copyExclusions);
                    Item copy = new Item();
                    copy.setOwnerItem(destination); //set owner for subtask copy (MUST be done before to ensure repeatCopies are inserted in right place)
                    orgSubtasks.get(i).copyMeInto(copy, copyFieldDefintion, copyExclusions);
                    //TODO!!!!! how to avoid saving subtasks, so we can Cancel the creation of a template instance??? (it is not acceptable to accumulate dangling subtasks which would be visible to the user in some view)!
                    DAO.getInstance().save(copy); //need to save copies as we go along, otherwise cannot save owner (Porject) due to "unable to encode an association with an unsaved ParseObject"
                    //Keep everything in memory and add a special lambda function to save everything created from the template *if* it is saved!
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
            destination.setSource(this);

        }

        //optimization: bundle all 'all' copies together in a single if statement
        if (all || copyPaste) {
            //None of these fields are normally copied
            destination.setStatus(getStatus());
            destination.setStartedOnDate(getStartedOnDate());
            destination.setCompletedDate(getCompletedDate());
//            destination.setCreatedDate(getCreatedDate());
            destination.setWaitingTillDate(getWaitingTillDate());
            destination.setDateWhenSetWaiting(getDateWhenSetWaiting());
            destination.setRemainingEffort(getRemainingEffort());
            destination.setActualEffort(getActualEffort());
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
                destination.setDueDate(getDueDate());
            }
            //START BY DATE
            if ((copyExclusions & COPY_EXCLUDE_START_BY_DATE) == 0) {
                destination.setStartByDate(getStartByDate());
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
            if (all || fromTempl) { //- UI: do NOT copy RepeatRules
                if ((RepeatRuleParseObject) getRepeatRule() != null && destination.getRepeatRule() == null) {
                    //TODO!!!! how to trigger a repeat rule on a new instance of a template??
                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //create a new repeat rule
//                destination.getRepeatRule().generateRepeatInstances(destination, destination.getOwnerItemList()); //- this should be done at commit() of the copy, not when it's generated, in this way the list for the copies is also known
                }
            } else if (toTempl) {
                if (getRepeatRule() != null) {
                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
                }
            } else if (toRepeatInst) {
                if (setRepeatRuleWithoutUpdate) {
                    destination.setRepeatRuleNoUpdate(getRepeatRule()); //point to existing repeat rule
                } else if (getRepeatRule() != null) {
                    destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
                }
            }
        }
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
            if (referenceItem.getStartByDate() != 0) { //only update if a value was defined for the referenceItem
                setStartByDate(referenceItem.getStartByDate() + delta);
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
        long oldDueDate = getDueDate();
        long newDueDate = newDueDateTime.getTime();
        long delta = 0;
//        if (oldDueDate != 0 && newDueDate != 0) {
        if (oldDueDate != 0) {
            delta = newDueDate - oldDueDate;
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
            if (getStartByDate() != 0) { //only update if a value was defined for the referenceItem
//                setStartByDate(newDueDate - (oldDueDate - getStartByDate()));
                setStartByDate(getStartByDate() + delta);
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
                    return compareLong(getRemainingEffort(), c.getRemainingEffort());
                case Item.COMPARE_ACTUALEFFORT:
                    return compareLong(getActualEffort(), c.getActualEffort());
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
    /**
     * delete the item, as well as sub-tasks. Remove it from categories and
     * alarm server. Stop timer if running. For items with repeatrules, user
     * must decide if only to delete this instance or all instances.
     *
     */
    @Override
    public void delete() throws ParseException {

        //DELETE SUBTASKS - delete all subtasks (since they are owned by this item)
//        List<Item> itemsSubtasksOfThisItem = DAO.getInstance().getAllItemsOwnedBy(this); //best to get owned subtasks directly from DAO since less likely that some may be missed
        List<Item> itemsSubtasksOfThisItem = getList();
        for (Item item : itemsSubtasksOfThisItem) {
//            item.delete(); //let each item delete itself properly, will recurse down the project hierarchy
            DAO.getInstance().delete(item); //let each item delete itself properly, will recurse down the project hierarchy
        }

        //TODO!!! (?)anything to do to handle case where subtasks are created and saved, but where the new mother task is finally not saved?
        //DELETE IN CATEGORIES
        // remove item from all categories before deleting it
//        DAO.getInstance().deleteItemFromAllCategories(this);
        for (Category cat : getCategories()) {
            ((Category) cat).removeItem(this); //remove references to this item from the category before deleting it
            DAO.getInstance().save(cat);
        }
        List<Category> catList = DAO.getInstance().getAllCategoriesContainingItem(this);
        ASSERT.that(catList == null || catList.size() == 0, "some categories still contain item after deleting it from its categories, item=" + this + " categories=" + catList);

        //DELETE IN OWNERS/PROJECTS
        if (getOwnerItem() != null) {
            Item ownerItem = getOwnerItem();
            ownerItem.getList().remove(this);
            DAO.getInstance().save(ownerItem);
        }
        if (getOwnerList() != null) {
            ItemList ownerItemList = getOwnerList();
//            ownerItemList.getList().remove(this);
            ownerItemList.removeItem(this);
            DAO.getInstance().save(ownerItemList);
        }

        //handle repeatrule
        //TODO!!!! handle repeatRules when deleting an Item
        RepeatRuleParseObject myRepeatRule = getRepeatRule();
        if (myRepeatRule != null) {
//            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we 
//            myRepeatRule.deleteThisRepeatInstanceFromRepeatRuleListOfInstances(this);
            myRepeatRule.updateRepeatInstancesOnDoneCancelOrDelete(this);
        }

//        try {
//            //finally delete the item itself
////            DAO.getInstance().delete(this); //WON'T WORK since DAO cannot call super.delete() on Item
//            super.delete();
//        } catch (ParseException ex) {
////            Log.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex); //TODO!!!!!!
//            Log.p(Item.class.getName() + "SEVERE " + ex); //TODO!!!!!!
//        }
        //finally delete the item itself
        //ALARMS remove any active alarms
        AlarmHandler.getInstance().deleteAllAlarmsForItem(this); //may have to be called *after* deleting the item from Parse to remove any scheduled app alarms

//        DAO.getInstance().delete(this);
//        super.delete();
//        put(PARSE_DELETED_DATE, new Date());
        setDeletedDate(new Date());
        DAO.getInstance().save(this);

        //TODO: any other references to an item??
/////////////////////////////////////////////////////
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
    }

    /**
     * use this call to check if a subitems list has been created, rather than
     * getItemList.getSize() which will just create an unnecessary empty list
     *
     * @return
     */
    public int getItemListSize() {
//        return (getItemList().size());
        return (getList().size());
//        return (subitems == null || subitems.getSize() == 0) ? 0 : subitems.getSize();
    }

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
    public List getList() {
        List<Item> list = getList(PARSE_SUBTASKS);
        if (list != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
            return list;
        } else {
            return new ArrayList();
        }
    }

    @Override
    public int size() { //
        return getList().size();
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList) {
//        List subtasks = getList();
//        boolean status = subtasks.add(subtask);
//        setList(subtasks);
//        return status;
//        return addToList(MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : getList().size(), subItemOrList); //TODO!!! UI: consider if it makes sense to insert at beginning of a list of subtasks just because of this setting
        return addToList(getList().size(), subItemOrList); //DONE!!! UI: consider if it makes sense to insert at beginning of a list of subtasks just because of this setting
    }

    @Override
    public boolean addToList(int index, ItemAndListCommonInterface subItemOrList) {
        List subtasks = getList();
        boolean status = true;
//        try {
        subtasks.add(index, subItemOrList);
        assert subItemOrList.getOwner() == null : "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this;
        subItemOrList.setOwner(this);
//        } catch (Exception e) {
//            status = false;
//        }
        setList(subtasks);
        return status;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface item, ItemAndListCommonInterface subItemOrList, boolean addAfterItem) {
        List subtasks = getList();
        boolean status = true;
//        try {
        int index = subtasks.indexOf(item);
        subtasks.add(index + (addAfterItem ? 1 : 0), subItemOrList);
        assert subItemOrList.getOwner() == null : "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this;
        subItemOrList.setOwner(this);
//        } catch (Exception e) {
//            status = false;
//        }
        setList(subtasks);
        return status;
    }

    @Override
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList) {
        List subtasks = getList();
        boolean status = subtasks.remove(subItemOrList);
        setList(subtasks);
        assert subItemOrList.getOwner() == this : "list not owner of removed subtask, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this;
        subItemOrList.setOwner(null);
        return status;
    }

//    public ItemList getItemList() {
    /**
     * returns a list with the current subtasks, or an empty list if none are
     * defined.
     *
     * @return always a list
     */
    public ItemList getItemList() {
//        List subitemslist = getList(PARSE_SUBTASKS);
        List subitemslist = getList();
        return subitemslist == null ? new ItemList() : new ItemList(subitemslist);
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        Item.
//        query.include(PARSE_SUBTASKS);
//        query.include(PARSE_SUBTASKS+"."+Item.CLASS_NAME);
//        query.include(Item.PARSE_SUBTASKS + "." + Item.PARSE_TEXT);
//        List subitemslist = null;
//        try {
//            subitemslist = query.find();
//        } catch (ParseException ex) {
////            switch (ex.)
//            Log.e(ex);
//        }
//        if (subitemslist == null) {
////            new LinkedList<Item>();
//            subitems = new ItemList();
//        } else {
//            subitems = new ItemList(subitemslist);
//        }
////        if (subitems == null) {
////            subitems = new ItemList(BaseItemTypes.ITEM, this, false, isEnsureItemCategoryAutoConsistency(), true, true); //ItemListSaveInline(this); //addMultipleInstances=false(even though it gives an expensive test on each insertion it is safer so no insertLink can make a subtask appear several times)
////        }
//        return subitems;
    }

    public void setItemList(ItemList itemList) {
        setList(itemList.getList());
    }

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
        if (listOfSubtasks == null || listOfSubtasks.isEmpty()) {// this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
            remove(PARSE_SUBTASKS); //if set to empty, remove instead
        } else {
            put(PARSE_SUBTASKS, listOfSubtasks);
        }
//        if (getItemListSize() == 0 && itemList.isEmpty()) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//        {
//            return; //do  nothing if both lists are empty
//        }
//        //TODO!! risk that this call will create unnecessary empoty sublists - replace an empty list with a locally (in this method) created empty list instead
////        ListCompared.updateListWithDifferences(getItemList(), itemList);
//        getItemList().updateListWithDifferences(itemList);
    }

    public void timerStart(Date startTime) {
        Date timerStarted = getDate(PARSE_TIMER_STARTED);
        if (timerStarted != null) {
            throw new RuntimeException("Timer started while timer already running");
        }
        //TODO: handle an already running timer: simply stop it and update the running item correctly, possibly with a pop-up information dialog
        put(PARSE_TIMER_STARTED, startTime); //set started time
        remove(PARSE_TIMER_PAUSED); //remove any paused time when timer is started
    }

    public void timerPause(Date pauseTime) {
        Date timerStarted = getDate(PARSE_TIMER_STARTED);
        if (timerStarted != null) {
            throw new RuntimeException("Paused when no timer was running");
        }
        put(PARSE_TIMER_PAUSED, pauseTime); //set timer to now
    }

    public boolean timerIsPaused() {
        return get(PARSE_TIMER_PAUSED) != null; //set timer to now
    }

    public long timerRunningTime() {
        Date timerStarted = getDate(PARSE_TIMER_STARTED);
        Date timePaused = getDate(PARSE_TIMER_PAUSED);
        long timeSpent = timerStarted == null ? 0 : (timerStarted.getTime() - (timePaused == null ? (new Date().getTime()) : timePaused.getTime()));
        return timeSpent;
    }

    public void timerStopAndUpdateActualEffort() {
        long timeSpent = timerRunningTime();
        remove(PARSE_TIMER_STARTED);
        remove(PARSE_TIMER_PAUSED);
        setActualEffort(getActualEffort() + timeSpent); //store new actual effort

    }

//    private final static String[] challengeNames = new String[]{"Piece of cake", "Easy", "Average", "Tough", "Hard"};
//    private final static int[] challengeValues = new int[]{0, 1, 2, 3, 4};
    public enum Challenge {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        VERY_EASY("Very easy", "V.easy"), EASY("Easy"), AVERAGE("Average", "Avrg"), HARD("Tough"), VERY_HARD("Hard");

        private final String description;
        private final String shortDescription;

        Challenge(String description, String shortDescription) {
            this.description = description;
            this.shortDescription = shortDescription;
        }

        Challenge(String description) {
            this(description, description);
        }

        String getDescription() {
            return description;
        }

        String getDescription(boolean shortLabels) {
            if (shortLabels) {
                return shortDescription;
            } else {
                return getDescription();
            }
        }

        static String[] getDescriptionList() {
//            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
            return new String[]{VERY_EASY.description, EASY.description, AVERAGE.description, HARD.description, VERY_HARD.description};
        }

        static String[] getDescriptionList(boolean shortLabels) {
            if (shortLabels) {
                return new String[]{VERY_EASY.shortDescription, EASY.shortDescription, AVERAGE.shortDescription, HARD.shortDescription, VERY_HARD.shortDescription};
            } else //            return new String[]{VERY_EASY.getDescription(), EASY.getDescription(), AVERAGE.getDescription(), HARD.getDescription(), VERY_HARD.getDescription()};
            {
                return getDescriptionList();
            }
        }

        static int[] getDescriptionValues() {
            return new int[]{VERY_EASY.ordinal(), EASY.ordinal(), AVERAGE.ordinal(), HARD.ordinal(), VERY_HARD.ordinal()};
        }

        /**
         * returns the enum corresponding to the description string
         *
         * @param description
         * @return enum or null if no enum value corresponds to the description
         * string
         */
        static Challenge getValue(String description) {
            return getValue(description, false);
        }

        static Challenge getValue(String description, boolean shortLabels) {
            String[] descList = getDescriptionList(shortLabels);
            for (int i = 0, size = getDescriptionList().length; i < size; i++) {
                if (descList[i].equals(description)) {
                    return Challenge.values()[getDescriptionValues()[i]]; //values() return an array of the ordinals
                }
            }
            return null;
        }
    }

//    public static String[] getChallengeStringArray() {
//        return challengeNames;
//    }
//
//    public static int[] getChallengeValuesArray() {
//        return challengeValues;
//    }
    public Challenge getChallenge() {
//        String challenge = getString(PARSE_CHALLENGE);
//        return (challenge == null) ? Challenge.AVERAGE.getDescription() : challenge;
//        String challenge = getString(PARSE_CHALLENGE);
//        return (challenge == null) ? "" : challenge;
        String challenge = getString(PARSE_CHALLENGE);
        if (challenge == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getChallenge();
            }
        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (challenge == null) ? null : Challenge.valueOf(challenge); //Created is initial value

    }

    public void setChallenge(Challenge challenge) {
//        if (has(PARSE_CHALLENGE)) { // || !challenge.equals(Challenge.AVERAGE.getDescription())) { //no need to save a value since a null pointer is interprested as zero
//            put(PARSE_CHALLENGE, challenge);
//        }
//        if (challenge != null && !challenge.equals("")) {
//            put(PARSE_CHALLENGE, challenge);
//        } else {
//            remove(PARSE_CHALLENGE);
//        }
//        if (challenge != null) {// && !dreadFunValue.equals("")) {
        // A=> B <=> !A or B: inherit => getOwnerItem().getChallenge() != challenge
        //inherit && hasOwner && newValue!=oldValue
        if (challenge != null
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getChallenge() != challenge)) {
            put(PARSE_CHALLENGE, challenge.toString());
        } else {
            //remove value if (inherits && hasOwner && newValue==inheritedValue) <=> 
            //store newValue if !remove <=> !inherits || !hasOwner || newValue!=inheritedValue
            //where inherits == MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean() (both must be true)
            // <=> !(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectChallenge.getBoolean()) ||
            //     getOwnerItem() == null || getOwnerItem().getChallenge() != challenge
            remove(PARSE_CHALLENGE);
        }

    }

    public void setStarred(boolean starred) {
//        if (starred) {
        if (starred
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean())
                || getOwnerItem() == null || !getOwnerItem().isStarred())) {
            put(PARSE_STARRED, true);
        } else {
            remove(PARSE_STARRED); //no value set means not starred
        }
    }

    public boolean isStarred() {
        Boolean b = getBoolean(PARSE_STARRED);
        if (b == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerStarredProperties.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().isStarred();
            }
        }
        return getBoolean(PARSE_STARRED) != null;
    }

    public int getPriority() {
        Integer i = getInt(PARSE_PRIORITY);
        if (i == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getPriority();
            }
        }
        return i == null ? 0 : i;
////        return priority;
//        return priority.getPriority();
    }

    public Priority getPriorityObject() {
//        return priority;
        return new Priority(getPriority());
    }

    public void setPriority(int prio) {
        if (prio != 0
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectPriority.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getPriority() != prio)) {
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
//    public int getDreadFunValue() {
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
//    public String getDreadFunValue() {
    /**
     * returns null if no value is defined
     *
     * @return
     */
    public DreadFunValue getDreadFunValue() {
        String dreadFunValue = getString(PARSE_DREAD_FUN_VALUE);
        if (dreadFunValue == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getDreadFunValue();
            }
        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (dreadFunValue == null) ? null : DreadFunValue.valueOf(dreadFunValue); //Created is initial value
    }

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
        if (dreadFunValue != null
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDreadFun.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getDreadFunValue() != dreadFunValue)) {
            put(PARSE_DREAD_FUN_VALUE, dreadFunValue.toString());
        } else {
            remove(PARSE_DREAD_FUN_VALUE);
        }
    }

//    void setImportance(HighMediumLow importance) {
    void setImportance(HighMediumLow importance) {
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
        if (importance != null
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectImportance.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getImportance() != importance)) {
            put(PARSE_IMPORTANCE, importance.toString());
        } else {
            remove(PARSE_IMPORTANCE);
        }
//        }
    }

//    public HighMediumLow getImportance() {
    public HighMediumLow getImportance() {
//        String importance = getString(PARSE_IMPORTANCE);
////        return (importance == null) ? HighMediumLow.LOW : HighMediumLow.valueOf(importance); //Created is initial value
////        return (importance == null) ? HighMediumLow.LOW.toString() : importance; //Created is initial value
//        return (importance == null) ? "" : importance; //Created is initial value
        String imp = getString(PARSE_IMPORTANCE);
        if (imp == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectImportance.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getImportance();
            }
        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (imp == null) ? null : HighMediumLow.valueOf(imp); //Created is initial value

    }

//    void setUrgency(HighMediumLow urgency) {
    void setUrgency(HighMediumLow urgency) {
//        if (has(PARSE_URGENCY) || urgency != HighMediumLow.LOW) {
//        if (has(PARSE_URGENCY)) { // || !urgency.equals(HighMediumLow.LOW.toString())) {
//            put(PARSE_URGENCY, urgency.toString());
//        if (urgency != null && !urgency.equals("")) {
//            put(PARSE_URGENCY, urgency);
//        if (urgency != null) {// && !dreadFunValue.equals("")) {
        if (urgency != null
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectUrgency.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getUrgency() != urgency)) {
            put(PARSE_URGENCY, urgency.toString());
        } else {
            remove(PARSE_URGENCY);
        }
    }

//    public HighMediumLow getUrgency() {
    public HighMediumLow getUrgency() {
//        String urgency = getString(PARSE_URGENCY);
////        return (status == null) ? HighMediumLow.LOW : HighMediumLow.valueOf(status); //Created is initial value
////        return (status == null) ? HighMediumLow.LOW.toString() : status; //Created is initial value
//        return (urgency == null) ? "" : urgency; //Created is initial value
        String urgency = getString(PARSE_URGENCY);
        if (urgency == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectUrgency.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getUrgency();
            }
        }
//        return (dreadFunValue == null) ? "" : dreadFunValue;
        return (urgency == null) ? null : HighMediumLow.valueOf(urgency); //Created is initial value

    }

    /**
     * returns a numerical value of the Importance/Urgency pair, or zero if only
     * one is defined
     *
     * @return
     */
    public int getImpUrgPrioValue() {
        //TODO define numerical priority of Imp/Urg pair when only one of them is defined, e.g. Imp=H/M/L=>9/7/3, Urg=H/M/L=>
//            matrixVector.addElement(new PriorityPair(HIGH, HIGH, 8));
//            matrixVector.addElement(new PriorityPair(HIGH, MED, 7));
//            matrixVector.addElement(new PriorityPair(MED, HIGH, 6));
//            matrixVector.addElement(new PriorityPair(MED, MED, 5));
//            matrixVector.addElement(new PriorityPair(HIGH, LOW, 4));
//            matrixVector.addElement(new PriorityPair(MED, LOW, 3));
//            matrixVector.addElement(new PriorityPair(LOW, HIGH, 2));
//            matrixVector.addElement(new PriorityPair(LOW, MED, 1));
//            matrixVector.addElement(new PriorityPair(LOW, LOW, Settings.getInstance().getDefaultPriority())); //Must be 0 or less!!

//        String impStr = getImportance();
//        String urgStr = getUrgency();
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
        HighMediumLow imp = getImportance();
        HighMediumLow urg = getUrgency();
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
     * returns the string to display for the Imp/Urg pair in a list of items
     *
     * @return
     */
    public String getImpUrgPrioValueAsString() {

//        return getImpUrgPrioValue() != 0 ? getImportance().getDescription().substring(0, 1) + "/" + getUrgency().getDescription().substring(0, 1) : getPriority() != 0 ? getPriority() + "" : " ";
        return getImpUrgPrioValue() != 0 ? getImportance().getDescription().substring(0, 1) + "/" + getUrgency().getDescription().substring(0, 1) : "";
    }

    public double getEarnedValue() {
        Double earnedVal = getDouble(PARSE_EARNED_VALUE); //Parse doesn't store doubles, //TODO!!!! fix issue in parse to store reals
//        Double earnedVal = getInt(PARSE_EARNED_VALUE);
        return (earnedVal == null) ? 0 : earnedVal;
//        return earnedValue;
    }

    public void setEarnedValue(double earnedVal) {
//                if (!has("earnedValue") &&  earnedVal == 0) return; //equivalent 
//        if (has(PARSE_EARNED_VALUE) || earnedVal != 0) {
//            put(PARSE_EARNED_VALUE, earnedVal);
//        }
        if (earnedVal != 0) {
            put(PARSE_EARNED_VALUE, earnedVal);
        } else {
            remove(PARSE_EARNED_VALUE);
        }

//        if (this.earnedValue != val) {
//            this.earnedValue = val;
//        }
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
//TODO introduce a setting to always calculate valuePerHour based on Estimate (to avoid it changes when once has started, e.g. if remaining is not set). MAYBE: now,it only used Remaining+Actual when Remaining is set to sth. 
        return calculateEarnedValuePerHour(getTotalExpectedEffort(), getEarnedValue());
//        long totalEffort = getTotalExpectedEffort();
//        if (totalEffort > 0) {
//            return ((getEarnedValue() * MyDate.HOUR_IN_MILISECONDS) / totalEffort);
//        } else {
//            return 0;
//        }

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
        if (expiresOnDate != null && expiresOnDate.getTime() != 0) {
            put(PARSE_EXPIRES_ON_DATE, expiresOnDate);
        } else {
            remove(PARSE_EXPIRES_ON_DATE);
        }
    }

    public void setExpiresOnDate(long expiresOnDate) {
        setExpiresOnDate(new Date(expiresOnDate));
//        if (has(PARSE_EXPIRES_ON_DATE) || expiresOnDate != 0) {
//            put(PARSE_EXPIRES_ON_DATE, new Date(expiresOnDate));
//        }
    }

    public void setExpiresOnDateD(Date expiresOnDate) {
        setExpiresOnDate(expiresOnDate.getTime());
    }

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
     * after onOrAfterDate
     *
     * @return
     */
    private List<AlarmRecord> getAllAlarmRecords(Date onOrAfterDate, boolean sorted) {
        //TODO //optimization: keep track of whether alarms have been changed to avoid recalculating this at every  call of getFirstFutureAlarm() //NO, probably very little to be gained
        List<AlarmRecord> list = new ArrayList();
        Date date;
        if ((date = getSnoozeDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
            list.add(new AlarmRecord(date, com.todocatalyst.todocatalyst.AlarmType.snooze));
        }
        if ((date = getAlarmDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
            list.add(new AlarmRecord(date, com.todocatalyst.todocatalyst.AlarmType.notification));
        }
        if ((date = getWaitingAlarmDateD()) != null && (onOrAfterDate == null || date.getTime() >= onOrAfterDate.getTime())) {
            list.add(new AlarmRecord(date, com.todocatalyst.todocatalyst.AlarmType.waiting));
        }
        if (sorted && list.size() > 1) {
            Collections.sort(list, (object1, object2) -> {
                return FilterSortDef.compareDate(object1.alarmTime, object2.alarmTime);
            });
        }
        return list;
    }

    public List<AlarmRecord> getAllFutureAlarmRecordsSorted() {
        return getAllAlarmRecords(new Date(), true);
    }

    public AlarmRecord getFirstFutureAlarmRecord() {
        List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        return list.isEmpty() ? null : getAllFutureAlarmRecordsSorted().get(0);
    }

    /**
     * returns first-coming future alarm (larger than or equal to now) or null
     * if no alarms are defined.
     *
     * @return
     */
    public Date getFirstFutureAlarm() {
//        return getAllAlarmRecords(new Date(), true).get(0).alarmTime;
        List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        return list.isEmpty() ? null : getAllFutureAlarmRecordsSorted().get(0).alarmTime;
    }

    public void updateFirstAlarm() {
        Date date = getFirstFutureAlarm(); //List<AlarmRecord> list = getAllFutureAlarmRecordsSorted();
        if (date == null) { //list.isEmpty()) {
            remove(PARSE_FIRST_ALARM);
        } else {
            put(PARSE_FIRST_ALARM, date); //list.get(0).alarmTime);
        }
    }

    private void updateFirstAlarmOLD() {
        Date alarm = getDate(PARSE_ALARM_DATE);
        Date waiting = getDate(PARSE_WAITING_ALARM_DATE);
        if (alarm != null) {
            if (waiting != null) {
                if (alarm.getTime() < waiting.getTime()) {
                    put(PARSE_FIRST_ALARM, alarm);
                } else {
                    put(PARSE_FIRST_ALARM, waiting);
                }
            } else {
                put(PARSE_FIRST_ALARM, alarm);
            }
        } else if (waiting != null) { //alarm==null
            put(PARSE_FIRST_ALARM, waiting);
        } else { // alarm==null && waiting==null
            remove(PARSE_FIRST_ALARM);
        }
    }

    /**
     * returns null if no date is defined
     */
    public Date getFirstAlarmDateD() {
        Date date = getDate(PARSE_FIRST_ALARM);
        return date; //(date == null) ? new Date(0) : date;
    }

    public void setFirstAlarmDate(Date firstAlarmDate) {
        if (firstAlarmDate != null && firstAlarmDate.getTime() != 0) {
            put(PARSE_FIRST_ALARM, firstAlarmDate);
        } else {
            remove(PARSE_FIRST_ALARM);
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
            updateFirstAlarm(); //done in save() now
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
//        Date firstAlarm = getFirstAlarmDateD();
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
        return (waitingAlarmDate == null || isDone()) ? new Date(0) : waitingAlarmDate; //return 0 is task is Done
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
            updateFirstAlarm(); //done in save() now
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
    void setRepeatRuleNoUpdate(RepeatRuleParseObject repeatRule) {
//        this.repeatRule = repeatRule;
//        if (has(PARSE_REPEAT_RULE) || repeatRule != null) { //no need to save a value since a null pointer is interprested as zero
//            put(PARSE_REPEAT_RULE, repeatRule);
//        }
        DAO.getInstance().save(repeatRule); //save the (possibly new or changed) repeatRule
        if (repeatRule != null) {
            put(PARSE_REPEAT_RULE, repeatRule);
        } else {
            remove(PARSE_REPEAT_RULE);
        }
    }

    /**
     * sets or updates the repeatRule and ensures that additional or no longer
     * needed repeat instances are created/deleted
     *
     * @param newRepeatRule
     */
    public void setRepeatRule(RepeatRuleParseObject newRepeatRule) {
//        if (this.repeatRule != repeatRule) {
//            this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
//        }
        RepeatRuleParseObject oldRepeatRule = getRepeatRule();
        if (newRepeatRule != null) {
//            if (!isTemplate() && !repeatRule.equals(getRepeatRule())) { //do not activate/update repeat rules for templates
//            repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this, getOwner(), MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : getOwner().size());
//                boolean newRepeatRule = getRepeatRule() == null;
            setRepeatRuleNoUpdate(newRepeatRule); //MUST set repeat rule *before* creating repeat instances in next line to ensure repeatInstance copies point back to the repeatRule
//                repeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
            if (!isTemplate()) { //do not activate/update repeat rules for templates, NOT possible to compare using !repeatRule.equals(getRepeatRule()) since it may the same object
                if (oldRepeatRule == null) {
//                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreated(this);
                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
                } else { //if (!oldRepeatRule.equals(newRepeatRule)) {
//                    newRepeatRule.updateRepeatInstancesWhenRuleWasEdited(this);
                    newRepeatRule.updateRepeatInstancesWhenRuleWasCreatedOrEdited(this);
                }
            } else { //template

            }
        } else if (oldRepeatRule != null) { //if the user deleted the repeatRule, /* repeatRule == null && */ 
//            getRepeatRule().delete();
            getRepeatRule().deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this);
            setRepeatRuleNoUpdate(newRepeatRule);
        } //else both old and new RR are null, do nothing
    }

    @Override
    public void setRepeatRuleForRepeatInstance(RepeatRuleParseObject repeatRule) {
        setRepeatRuleNoUpdate(repeatRule);
    }

    public long getStartedOnDate() {
//        return startedOnDate;
//        Date date = getDate(PARSE_STARTED_ON_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getStartedOnDateD().getTime();
    }

    protected static Date getSubtasksStartedOnDateD(Item item) {
        List<Item> subtasks = item.getList();
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
//        return new Date(getStartedOnDate());
        Date date = getDate(PARSE_STARTED_ON_DATE);
        if (date == null && MyPrefs.itemProjectPropertiesDerivedFromSubtasks.getBoolean()) { //UI: for projects, startedOn date is the date the first subtask was started (if any), the date can be overwritten by setting any desired date for the mother project
            return getSubtasksStartedOnDateD(this);
        }
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
//        setStartedOnDate(startedOnDate.getTime());
        if (forceToNewValue || getStartedOnDate() == 0) { //only set it once, don't overwrite it later
            if (startedOnDate != null && startedOnDate.getTime() != 0) {
                put(PARSE_STARTED_ON_DATE, startedOnDate);
//            setStatus(ItemStatus.ONGOING);  //Better move this into the UI
            } else {
                remove(PARSE_STARTED_ON_DATE);
//            if (getActualEffort() == 0) { //TODO!!!! this won't work if actualEffort was edited in the UI but not set yet!! Need to ensure this consistency in ScreenItem
//                setStatus(ItemStatus.CREATED);
//            }
            }
        }
    }

    public void setStartedOnDateUpdateStatus(Date startedOnDate) {
//        setStartedOnDate(startedOnDate.getTime());
        if (startedOnDate != null && startedOnDate.getTime() != 0) {
            put(PARSE_STARTED_ON_DATE, startedOnDate);
            setStatus(ItemStatus.ONGOING);  //Better move this into the UI
        } else {
            remove(PARSE_STARTED_ON_DATE);
            if (getActualEffort() == 0) { //TODO!!!! this won't work if actualEffort was edited in the UI but not set yet!! Need to ensure this consistency in ScreenItem
                setStatus(ItemStatus.CREATED);
            }
        }
    }

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
            if (!isDone()) {
                //only change status if not already Done (or Cancelled)
                setStatus(ItemStatus.DONE);
            }
        } else //if Done was set before and now is unset then reset completedDate //TODO: avoid extra call to receiveChangeEvent() in setCompletedDate()
        if (getActualEffort() != 0) {
            //if effort is recorded, then the right state to revert to is ONGOING; 
            //TODO!!!!: are there other indicators that previous state should be other than CREATED (or should we simply store the previous state and use that??!!)
            setStatus(ItemStatus.ONGOING);
        } else {
            setStatus(ItemStatus.CREATED);
        }
    }

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
        return getItemListSize() > 0;
    }

    /**
     * returns true if this Item is a top-level Project, that is, it has
     * sub-tasks but does not belong to another task/project (which means it is
     * a sub-project)
     */
    public boolean isProjectTopLevel() {
        return getItemListSize() > 0 && !(getOwner() instanceof Item);
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
    public Item getNextLeafItem(Item previousItem, Condition condition) {
//        return getNextUndoneLeafItemImpl(previousItem, excludeWaiting, false);
        return getNextUndoneLeafItemImpl(previousItem, condition, new boolean[]{previousItem == null});

    }

    interface Condition {

        boolean meets(Item item);
    }

    /**
     *
     * @param previousItem
     * @param condition
     * @param previousItemAlreadyFound must be set true if previousItem==null!!
     * (optimization)
     * @return
     */
//    private Item getNextUndoneLeafItemImpl(Item previousItem, boolean excludeWaiting, boolean previousItemAlreadyFound) {
    Item getNextUndoneLeafItemImpl(Item previousItem, Condition condition, boolean[] previousItemAlreadyFound) {
//        previousItemAlreadyFound[0] = previousItem==null;
        assert previousItem != null || previousItemAlreadyFound[0] : "getNextUndoneLeafItemImpl called with previousItem==null and previousItemAlreadyFound not set true";
//        if (itemList == null || itemList.size() == 0) {
        if (!isProject()) { //LEAF
            //if no subtasks, previousItem found or null, and item meets the condition, then return the Item itself
//            if ((previousItem == null || previousItemAlreadyFound[0]) && condition.meets(this)) {
//            if (previousItemAlreadyFound[0] && condition.meets(this)) {
            if (this.equals(previousItem)) {
                previousItemAlreadyFound[0] = true;
            }
            if (condition.meets(this) && (previousItem == null || !previousItem.equals(this))) { //previousItem!=null => !previousItem.equals(this)
//                previousItemAlreadyFound[0] = true; //NO NEED since this is a single task
                return this;
            } else {
                return null;
            }
        } else {
            //Project
            List itemList = getList();
//            boolean previousItemFoundHere = (previousItem == null); //set to true if previousItem is null
            for (int i = 0, size = itemList.size(); i < size; i++) {
                Item subTask = (Item) itemList.get(i);
                if (subTask.isProject()) {
                    //try to find an appropriate subtask to this project
//                    subTask = subTask.getNextUndoneLeafItemImpl(previousItem, condition, previousItemFoundHere || previousItemAlreadyFound[0]);
                    subTask = subTask.getNextUndoneLeafItemImpl(previousItem, condition, previousItemAlreadyFound);
//                    if (item != null && previousItemFoundHere) {
                    //if a subtask meeting the conditions is found, return it
                    if (subTask != null) {
                        return subTask;
                    }
                    //else continue with next subtask
                } else //                    previousItemFoundHere = previousItemFoundHere || (previousItem != null && subTask.equals(previousItem));
                //                    previousItemAlreadyFound[0] = previousItemAlreadyFound[0] || (previousItem != null && subTask.equals(previousItem)); //set true when found and keep true
                if (!(previousItemAlreadyFound[0])) {
//                    previousItemAlreadyFound[0] = (previousItem != null && subTask.equals(previousItem)); //set true when found and keep true
                    previousItemAlreadyFound[0] = (subTask.equals(previousItem)); //set true when found and keep true
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
     * returns a sequential list of all leaf tasks that meet the Condition
     *
     * @param condition condition or null (will match all items)
     * @return
     */
    List<ItemAndListCommonInterface> getLeafTasksAsList(Condition condition) {
//        assert previousItem != null || previousItemAlreadyFound[0] : "getNextUndoneLeafItemImpl called with previousItem==null and previousItemAlreadyFound not set true";
        if (!isProject()) { //LEAF
            if (condition == null || condition.meets(this)) {
//                ArrayList list = new ArrayList(Arrays.asList(this));// list.add(this);
//                return list;
                return new ArrayList(Arrays.asList(this));// list.add(this);
            } else {
                return null;
            }
        } else { //Project
            List<Item> itemList = getList();
            List<ItemAndListCommonInterface> sublist;
            int size = itemList.size();
            List<ItemAndListCommonInterface> result = null;
            if (size > 0) {
                result = new ArrayList();
                for (int i = 0; i < size; i++) {
                    sublist = itemList.get(i).getLeafTasksAsList(condition);
                    if (sublist != null) {
                        result.addAll(sublist);
                    }
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
        return ItemList.getNumberOfUndoneItems(getList(), includeSubTasks); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
    }

    @Override
    public int getNumberOfItems(boolean onlyUndone, boolean countLeafTasks) {
        List list = getList();
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
        for (Object obj : getList()) {
            count += ((ItemAndListCommonInterface) obj).getNumberOfItems(onlyUndone, countLeafTasks);
        }
        return count; //if !countLeafTasks, then add 1 for this Item
    }

    /**
     * returns the appropriate value of status when actual is being changed -
     * used in the UI to update status when actual is changing. If no reason to
     * change status, returns newStatus (already set status => no change)
     *
     * @param oldActual old value (item value before editing)
     * @param newActual new value (possibly manually edited by user)
     * @param oldStatus old value (item value before editing)
     * @param newStatus new value (possibly manually edited by user)
     * @return
     */
    static public ItemStatus updateStatusOnActualChange(long oldActual, long newActual, ItemStatus oldStatus, ItemStatus newStatus) {
        if (oldActual == newActual) {
            return newStatus; //return if no change
        } else if (newActual > 0) { //if user has changed actual
            if (oldStatus == ItemStatus.CREATED /*don't change WAITING/CANCELLED/ONGOING*/ && (newStatus == oldStatus/*status not manually changed*/ || oldStatus == ItemStatus.ONGOING /*setting back to existing value*/)) {
                return ItemStatus.ONGOING;
            } //else 
        } else if (oldStatus == ItemStatus.ONGOING && newStatus == oldStatus) {
            return ItemStatus.CREATED; //if setting actual to 0, set status back to Created
        }
        return newStatus; //if no need to change status, just return already set value
    }

    /**
     * returns true if the status of this task should be changed to the
     * newStatus. Used to count the number of subtasks that may have their
     * status changed when changing the status of the mother task/project, or
     * task-list. Old -> New: Created/Ongoing/Waiting -> Done; Created/Waiting
     * -> Ongoing; Created/Ongoing -> Waiting; Created/Ongoing/Waiting ->
     * Cancelled [don't cancel Done tasks]; Ongoing/Waiting/Done -> Created
     *
     *
     * @param newStatus
     * @param oldStatus
     * @return
     */
//    public static boolean changeSubtaskStatus(int newStatus, int oldStatus) {
    public static boolean shouldSubtaskStatusChange(ItemStatus newStatus, ItemStatus oldStatus) {
        return (newStatus == ItemStatus.DONE && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING)) //NOT: DONE, CANCELLED
                || (newStatus == ItemStatus.ONGOING && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.WAITING)) //NOT: ONGOING, DONE, CANCELLED
                || (newStatus == ItemStatus.WAITING && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING)) //NOT: WAITING, DONE, CANCELLED
                || (newStatus == ItemStatus.CANCELLED && (oldStatus == ItemStatus.CREATED || oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING)) //NOT: DONE, CANCELLED
                || (newStatus == ItemStatus.CREATED && (oldStatus == ItemStatus.ONGOING || oldStatus == ItemStatus.WAITING || oldStatus == ItemStatus.DONE));  //NOT: CANCELLED
    }

    public boolean shouldSubtaskStatusChange(ItemStatus newStatus) {
        return shouldSubtaskStatusChange(newStatus, getStatus());
    }

    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus) {
        List list = getList();
        if (list == null || list.size() == 0) {
            return shouldSubtaskStatusChange(newStatus) ? 1 : 0;
        } else {
            return ItemList.getNumberOfItemsThatWillChangeStatus(list, recurse, newStatus);
        }
    }

    /**
     *
     * @param topLevelTask
     * @param newStatus
     * @param askConfirmation
     * @param forceSubtasksToStatus
     */
    public void setStatusImpl(boolean topLevelTask, ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
        ItemStatus previousStatus = getStatus();
        if (previousStatus != newStatus) { //if status has receiveChangeEvent:
//            ItemStatus oldStatus = this.status;
//            ItemStatus oldStatus = getStatus();
            if (topLevelTask || shouldSubtaskStatusChange(newStatus, previousStatus)) {
//                status = newStatus;
                setStatusSaveValue(newStatus);
            }
            if (false) {
//                if (forceSubtasksToStatus && getItemListSize() > 0) {
//                    if (askConfirmation) {
//                        int nbSubTasksDiffFromStatus = countTasksWhereStatusWillBeChanged(newStatus, forceSubtasksToStatus);
//                        int nbSubtasksToChangeWithoutConfirmation = Settings.getInstance().nbSubtasksToChangeStatusWithoutConfirmation.getInt();
//                        if (nbSubTasksDiffFromStatus > 0 && (nbSubTasksDiffFromStatus <= nbSubtasksToChangeWithoutConfirmation
//                                || (Dialog.show("Set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status) + "?",
//                                        "Are you sure you want to set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status)
//                                        + "?\nThis cannot be undone.", "OK", "Cancel")))) {
//                            getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                        }
//                    } else { //no need to ask for confirmation
//                        getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                    }
////            if (obj instanceof BaseItemOrList) {
////                ((BaseItemOrList) obj).setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                    obj.setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////            }
//                }
            } else {
//                    public void setStatusImpl(boolean topLevelTask, Item.ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
//                        for (int i = 0, size = getSize(); i < size; i++) {
//                ItemList subtasks = getItemList();
                List subtasks = getList();
                for (Object itemOrList : subtasks) {
                    ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
                }
            }

            //StartedOnDate SET:
            if (getStartedOnDate() == 0 && (newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.DONE)) {
//                setStartedOnDate(MyDate.getNow());
                setStartedOnDate(System.currentTimeMillis());
            }
            //StartedOnDate RESET: not relevant, always keep the first set StartedOnDate

            //CompletedDate: SET set to Now if changing to Done/Cancelled from other state
            if ((previousStatus != ItemStatus.DONE && previousStatus != ItemStatus.CANCELLED)
                    && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
                //TODO!!!! should actual effort be reduced to zero?? No, any effort spend should be kept even for Cancelled tasks
//                setCompletedDate(MyDate.getNow()); //UI: also use Completed date to store date when task was cancelled (for historical data)
                setCompletedDate(System.currentTimeMillis()); //UI: also use Completed date to store date when task was cancelled (for historical data)
                ScreenTimer.getInstance().stopTimerIfRunningOnThisItem(this);
                if (getRepeatRule() != null) {
                    getRepeatRule().updateRepeatInstancesOnDoneCancelOrDelete(this);
                }
            }
            //CompletedDate: RESET if changing from Done/Cancelled to other state
            //CompletedDate: set if changing to Done/Cancelled from other state, set to Now if changing to Done/Cancelled
            if (getCompletedDate() != 0 && (previousStatus == ItemStatus.DONE || previousStatus == ItemStatus.CANCELLED)
                    && (newStatus != ItemStatus.DONE && newStatus != ItemStatus.CANCELLED)) {
                //if item changes from Done/Cancelled to some other value, then reset CompletedDate
                setCompletedDate(0L);
            }

            //WaitingActivatedDate:
            if (previousStatus != ItemStatus.WAITING && newStatus == ItemStatus.WAITING /*
                     * && getWaitingLastActivatedDate() == 0L
                     */) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
//                setDateWhenSetWaiting(MyDate.getNow()); //always save
                setDateWhenSetWaiting(System.currentTimeMillis()); //always save
            }
            if (false && (previousStatus == ItemStatus.WAITING && newStatus != ItemStatus.WAITING && getWaitingTillDate() != 0L)) { //reset WaitingTillDate
                setWaitingTillDate(0); //reset waitingTill date
                if (getWaitingAlarmDate() != 0) { //automatically turn off
                    setWaitingAlarmDate(0);
                }
//                setWaitingLastActivatedDate(0); //-waitingActivateDate is not changed (until the task is possibly set waiting again)
            }

            //RemainingEffort: set to zero for Done/Cancelled tasks
            if (false && newStatus == ItemStatus.DONE) {// || newStatus == ItemStatus.STATUS_CANCELLED) { //NO reason to delete remaining effort because a task is cancelled
                setRemainingEffort(0L); //reset Remaining when marked done
            }

            //reset Alarms for Done/Cancelled tasks
            //TODO shouldn't be necessary to reset alarmDate when using Parse to find relevant next alarmdate
            if (false && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
                setAlarmDate(0); //Cancel any set alarms //TODO: to support reverting when a task is marked Done, the alarm time should be kept, but not activated (AlarmServer should ignore alarms for Done tasks)
            }

            //reset any running timers //NO, done in SCreentTimer now
//            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
////                TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: new way of cheking if timer is running for done item
//            }
        }
    }

    public void setStatusImplOLD(boolean topLevelTask, ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
        ItemStatus previousStatus = getStatus();
        if (previousStatus != newStatus) { //if status has receiveChangeEvent:
//            ItemStatus oldStatus = this.status;
//            ItemStatus oldStatus = getStatus();
            if (topLevelTask || shouldSubtaskStatusChange(newStatus, previousStatus)) {
//                status = newStatus;
                setStatusSaveValue(newStatus);
            }
            if (false) {
//                if (forceSubtasksToStatus && getItemListSize() > 0) {
//                    if (askConfirmation) {
//                        int nbSubTasksDiffFromStatus = countTasksWhereStatusWillBeChanged(newStatus, forceSubtasksToStatus);
//                        int nbSubtasksToChangeWithoutConfirmation = Settings.getInstance().nbSubtasksToChangeStatusWithoutConfirmation.getInt();
//                        if (nbSubTasksDiffFromStatus > 0 && (nbSubTasksDiffFromStatus <= nbSubtasksToChangeWithoutConfirmation
//                                || (Dialog.show("Set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status) + "?",
//                                        "Are you sure you want to set " + nbSubTasksDiffFromStatus + " subtasks to " + Item.getStatusName(status)
//                                        + "?\nThis cannot be undone.", "OK", "Cancel")))) {
//                            getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                        }
//                    } else { //no need to ask for confirmation
//                        getItemList().setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                    }
////            if (obj instanceof BaseItemOrList) {
////                ((BaseItemOrList) obj).setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
//                    obj.setStatusImpl(false, newStatus, false, forceSubtasksToStatus);
////            }
//                }
            } else {
//                    public void setStatusImpl(boolean topLevelTask, Item.ItemStatus newStatus, boolean askConfirmation, boolean forceSubtasksToStatus) {
//                        for (int i = 0, size = getSize(); i < size; i++) {
//                ItemList subtasks = getItemList();
                List subtasks = getList();
                for (Object itemOrList : subtasks) {
                    ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
                }
            }

            //StartedOnDate SET:
            if (getStartedOnDate() == 0 && (newStatus == ItemStatus.ONGOING || newStatus == ItemStatus.DONE)) {
//                setStartedOnDate(MyDate.getNow());
                setStartedOnDate(System.currentTimeMillis());
            }
            //StartedOnDate RESET: not relevant, always keep the first set StartedOnDate

            //CompletedDate: SET set to Now if changing to Done/Cancelled from other state
            if ((previousStatus != ItemStatus.DONE && previousStatus != ItemStatus.CANCELLED)
                    && (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED)) {
                //TODO!!!! should actual effort be reduced to zero?? No, any effort spend should be kept even for Cancelled tasks
//                setCompletedDate(MyDate.getNow()); //UI: also use Completed date to store date when task was cancelled (for historical data)
                setCompletedDate(System.currentTimeMillis()); //UI: also use Completed date to store date when task was cancelled (for historical data)
            }
            //CompletedDate: RESET if changing from Done/Cancelled to other state
            //CompletedDate: set if changing to Done/Cancelled from other state, set to Now if changing to Done/Cancelled
            if (getCompletedDate() != 0 && (previousStatus == ItemStatus.DONE || previousStatus == ItemStatus.CANCELLED)
                    && (newStatus != ItemStatus.DONE && newStatus != ItemStatus.CANCELLED)) {
                //if item changes from Done/Cancelled to some other value, then reset CompletedDate
                setCompletedDate(0L);
            }

            //WaitingActivatedDate:
            if (previousStatus != ItemStatus.WAITING && newStatus == ItemStatus.WAITING /*
                     * && getWaitingLastActivatedDate() == 0L
                     */) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
//                setDateWhenSetWaiting(MyDate.getNow()); //always save
                setDateWhenSetWaiting(System.currentTimeMillis()); //always save
            }
            if (previousStatus == ItemStatus.WAITING && newStatus != ItemStatus.WAITING && getWaitingTillDate() != 0L) { //reset WaitingTillDate
                setWaitingTillDate(0); //reset waitingTill date
                if (getWaitingAlarmDate() != 0) { //automatically turn off
                    setWaitingAlarmDate(0);
                }
//                setWaitingLastActivatedDate(0); //-waitingActivateDate is not changed (until the task is possibly set waiting again)
            }

            //RemainingEffort: set to zero for Done/Cancelled tasks
            if (newStatus == ItemStatus.DONE) {// || newStatus == ItemStatus.STATUS_CANCELLED) { //NO reason to delete remaining effort because a task is cancelled
                setRemainingEffort(0L); //reset Remaining when marked done
            }

            //reset Alarms for Done/Cancelled tasks
            //TODO shouldn't be necessary to reset alarmDate when using Parse to find relevant next alarmdate
            if (newStatus == ItemStatus.DONE || newStatus == ItemStatus.CANCELLED) {
                setAlarmDate(0); //Cancel any set alarms //TODO: to support reverting when a task is marked Done, the alarm time should be kept, but not activated (AlarmServer should ignore alarms for Done tasks)
            }

            //reset any running timers //NO, done in SCreentTimer now
//            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
////                TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: new way of cheking if timer is running for done item
//            }
        }
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
        if (newStatus == ItemStatus.WAITING && getStatus() != ItemStatus.WAITING) {
            MyForm.dialogSetWaitingDateAndAlarm(this); //only call if we're changing TO Waiting status
        }
        if (isProject()) {
//            int nbUndone = getNumberOfUndoneItems(true);
            int nbChgStatus = getNumberOfItemsThatWillChangeStatus(true, newStatus);
            if (nbChgStatus <= MyPrefs.itemMaxNbSubTasksToChangeStatusForWithoutConfirmation.getInt()
                    || Dialog.show("INFO", "Changing status for more than " + nbChgStatus + " subtasks", "OK", "No")) {
                List<Item> subtasks = getList();
                for (int i = 0, size = subtasks.size(); i < size; i++) {
//                    if (!subtasks.get(i).isDone()) {
                    if (shouldSubtaskStatusChange(newStatus, subtasks.get(i).getStatus())) { //only change status when transiation is allowed
                        subtasks.get(i).setStatus(newStatus);
                    }
                }
            }
        } else //        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), true); 
        //        setStatusImpl(true, newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), !Settings.getInstance().neverChangeProjectsSubtasksWhenChangingProjectStatus.getBoolean());
        {
            setStatusImpl(true, newStatus, MyPrefs.getBoolean(MyPrefs.changeSubtasksStatusWithoutConfirmation),
                    MyPrefs.getBoolean(MyPrefs.neverChangeProjectsSubtasksWhenChangingProjectStatusXXX));
        }
    }

    /**
     * actually updates the saved value of status
     *
     * @param newStatus
     */
    private void setStatusSaveValue(ItemStatus newStatus) {
//        put(PARSE_STATUS, newStatus);
//        put(PARSE_STATUS, newStatus.toString());
//        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), true); 
//        if (getRepeatRule() != null && newStatus == ItemStatus.DONE && newStatus == ItemStatus.CANCELLED) { //update repeats on DONE/CANCEL
//            getRepeatRule().updateRepeatInstancesWhenItemIsDoneOrCancelled(this, getOwner(), MyPrefs.getBoolean(MyPrefs.insertNewRepeatInstancesInStartOfLists) ? 0 : getOwner().size());
//        }
//        if (newStatus != null && newStatus != ItemStatus.CREATED) { //MUST store every status incl. CREATED to be able to query on it
////            put(PARSE_CATEGORIES, new ArrayList(categories));
//            put(PARSE_STATUS, newStatus.toString());
//        } else { //categories == null || categories.isEmpty()
//            remove(PARSE_STATUS);
//        }
        put(PARSE_STATUS, newStatus.toString());
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
        } else if (statusCount.getCount(ItemStatus.ONGOING) >= 1) {//if at least one subtasks is ongoing
            return ItemStatus.ONGOING;
//        } else if (statusCount.getCount(ItemStatus.ONGOING) > 0
//                || (statusCount.getCount(ItemStatus.DONE) > 0
//                && statusCount.getCount(ItemStatus.CREATED) > 0
//                && statusCount.getCount(ItemStatus.WAITING) == 0)) { //the whole project is ongoing if just one task is ongoing, or if there if some, but not all, tasks are completed
//            projectStatus = ItemStatus.ONGOING;
//        } else if (statusCount.getCount(ItemStatus.WAITING) > 0) { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
////        } else { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
//            projectStatus = ItemStatus.WAITING;
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
    private static void getStatusImplProjectLeaftasks(List<Item> list, Bag<ItemStatus> statusCount) {
//    protected ItemStatus getListStatus() {
        for (int i = 0, size = list.size(); i < size; i++) {
//            statusCount.add(list.get(i).getStatus());
//            statusCount.add(list.get(i).getStatus());
//            ItemAndListCommonInterface item = list.get(i);
            Item item = list.get(i);
            if (item.isProject()) {
                getStatusImplProjectLeaftasks(item.getList(), statusCount);
            } else {
                statusCount.add(list.get(i).getStatus());
//                statusCount.add(item.getStatus());
            }
        }
    }

    public ItemStatus getStatus() {
        if (isProject()) {
            return getStatus(getList());
        } else {
            String status = getString(PARSE_STATUS);
            return (status == null) ? ItemStatus.CREATED : ItemStatus.valueOf(status); //Created is initial value
        }
    }

//    public ItemStatus getStatusOLD() {
////        return status;
////        Object status = (ItemStatus) get(PARSE_STATUS);
//        String status = getString(PARSE_STATUS);
//        return (status == null) ? ItemStatus.CREATED : ItemStatus.valueOf(status); //Created is initial value
//    }
    public long getDueDate() {
//        return dueDate;
//        Date date = getDate(PARSE_DUE_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getDueDateD().getTime();
    }

    public Date getDueDateD() {
//        return dueDate;
        Date date = getDate(PARSE_DUE_DATE);
        if (date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getDueDateD();
            }
        }
        return (date == null) ? new Date(0) : date;
    }

    public void setDueDate(Date dueDate) {
//        this.dueDate = val;
//        if (this.dueDate != val) {
//            this.dueDate = val;
//        }
//        if (has(PARSE_DUE_DATE) || dueDate.getTime() != 0) {
//            put(PARSE_DUE_DATE, dueDate);
//        }
//        if (dueDate != null && dueDate.getTime() != 0) {
        if (dueDate != null && dueDate.getTime() != 0
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectDueDate.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getDueDateD().getTime() != dueDate.getTime())) {
            put(PARSE_DUE_DATE, dueDate);
        } else {
            remove(PARSE_DUE_DATE);
        }
    }

    public void setDueDate(long dueDate) {
//        this.dueDate = val;
//        if (this.dueDate != val) {
//            this.dueDate = val;
//        }
//        if (has(PARSE_DUE_DATE) || dueDate != 0) {
//            put(PARSE_DUE_DATE, new Date(dueDate));
//        }
        setDueDate(new Date(dueDate));
    }

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
        if (hideUntil != null && hideUntil.getTime() != 0) {
            put(PARSE_HIDE_UNTIL_DATE, hideUntil);
        } else {
            remove(PARSE_HIDE_UNTIL_DATE);
        }
    }

    public long getStartByDate() {
//        return startByDate;
//        Date date = getDate(PARSE_START_BY_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getStartByDateD().getTime();
    }

    public Date getStartByDateD() {
//        return new Date(getStartByDate());
        Date date = getDate(PARSE_START_BY_DATE);
        if (date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartDate.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getStartByDateD();
            }
        }
        return (date == null) ? new Date(0) : date;
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
        if (startByDate != null && startByDate.getTime() != 0
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectStartDate.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getStartByDateD().getTime() != startByDate.getTime())) {
            put(PARSE_START_BY_DATE, startByDate);
        } else {
            remove(PARSE_START_BY_DATE);
        }
    }

    public Date getSnoozeDateD() {
        Date date = getDate(PARSE_SNOOZE_DATE);
        return (date == null) ? new Date(0) : date;
    }

    public void setSnoozeDate(Date snoozeUntilDate) {
        if (snoozeUntilDate != null && snoozeUntilDate.getTime() != 0) {
            put(PARSE_SNOOZE_DATE, snoozeUntilDate);
        } else {
            remove(PARSE_SNOOZE_DATE);
        }
    }

    public Date getWaitingTillDateD() {
        Date date = getDate(PARSE_WAITING_TILL_DATE);
        if (date == null && MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean()) {
            if (getOwnerItem() != null) {
                return getOwnerItem().getWaitingTillDateD();
            }
        }
        return (date == null) ? new Date(0) : date;
    }

    public long getWaitingTillDate() {
//        return waitingTillDate;
//        Date date = getDate(PARSE_WAITING_TILL_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getWaitingAlarmDateD().getTime();
    }

    public void setWaitingTillDate(long waitingTillDate) {
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
        setWaitingTillDate(new Date(waitingTillDate));
    }

    public void setWaitingTillDate(Date waitingTillDate) {
//        if (has(PARSE_WAITING_TILL_DATE) || waitingTillDate != 0) {
//            put(PARSE_WAITING_TILL_DATE, new Date(waitingTillDate));
//        }
        if (waitingTillDate.getTime() != 0
                && (!(MyPrefs.itemInheritOwnerProjectProperties.getBoolean() && MyPrefs.itemInheritOwnerProjectWaitingTillDate.getBoolean())
                || getOwnerItem() == null || getOwnerItem().getWaitingTillDateD().getTime() != waitingTillDate.getTime())) {
            put(PARSE_WAITING_TILL_DATE, waitingTillDate);
        } else {
            remove(PARSE_WAITING_TILL_DATE);
        }
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
        if (waitingLastActivatedDate != null && waitingLastActivatedDate.getTime() != 0) {
            put(PARSE_DATE_WHEN_SET_WAITING, waitingLastActivatedDate);
        } else {
            remove(PARSE_DATE_WHEN_SET_WAITING);
        }
    }

    public void setEffortEstimate(long effortEstimate, boolean autoUpdateRemainingEffort, boolean forProjectTaskItself) {
//#mdebug
//        ASSERT.that(effortEstimate >= 0, "EffortEstimate cannot be negative");
        assert effortEstimate >= 0 : "EffortEstimate cannot be negative";
//#enddebug
        if (forProjectTaskItself || !isProject()) {
//        this.effortEstimate = val;
//        if (this.effortEstimate != val) {
//            this.effortEstimate = val;
//            if (has(PARSE_EFFORT_ESTIMATE) || effortEstimate != 0) {
//                put(PARSE_EFFORT_ESTIMATE, effortEstimate);
            if (effortEstimate != 0) {
                put(PARSE_EFFORT_ESTIMATE, effortEstimate);
            } else {
                remove(PARSE_EFFORT_ESTIMATE);
            }
            //TODO!!! check if both "setRemainingEffort(effortEstimate - getActualEffort()" below are needed and consistent
            if (autoUpdateRemainingEffort && effortEstimate != 0
                    && getRemainingEffort() == 0
                    && MyPrefs.getBoolean(MyPrefs.automaticallyUseFirstEffortEstimateMinusActualAsInitialRemaining)) {
                setRemainingEffort(effortEstimate - getActualEffort(), false, forProjectTaskItself); //TODO actualEffort should be set *before* effort estimate for this to work
            }
//            if (autoUpdateRemainingEffort && Settings.getInstance().alwaysUpdateRemainingToEffortMinusActualWhenEffortIsUpdated()) {
            if (autoUpdateRemainingEffort
                    && MyPrefs.getBoolean(MyPrefs.automaticallyIncreaseRemainingIfNewEffortEstimateIsHigherThanPreviousRemainingPlusActual)
                    && effortEstimate > getRemainingEffort() + getActualEffort()) {
//                if (this.remainingEffort + this.actualEffort < effortEstimate) { //UI: if currently set remaining effort + actual is less than estimate, then update Remaining so it corresponds to Estimate-Actual
//                if (getRemainingEffort() + getActualEffort() < effortEstimate) { //UI: if currently set remaining effort + actual is less than estimate, then update Remaining so it corresponds to Estimate-Actual
//                    setRemainingEffort(effortEstimate - this.actualEffort, false); //
                setRemainingEffort(effortEstimate - getActualEffort(), false, forProjectTaskItself); //
//                }
            }
        }
    }

    public void setEffortEstimate(long val) {
        setEffortEstimate(val, false, false);
    }

//    public void setEffortEstimateInMinutes(int val) {
//        setEffortEstimate(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
    /**
     * returns effort estimate. If no estimate was set (value 0) AND there are
     * subitems, then return the sum of the estimates of the subitems.
     *
     * @return
     */
    public long getEffortEstimate(boolean forSubtasks) {
//        if (effortEstimate == 0 && getItemListSize() != 0) {
        if (forSubtasks && isProject()) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ESTIMATE); //optimization: store sum in intermediate variable to avoid recalculating each time
            long subItemSum = 0;
            for (int i = 0, size = getItemListSize(); i < size; i++) {
//                Item item = (Item) getItemList().getItemAt(i);
                Item item = (Item) getList().get(i);
                if (!item.isDone()) { // /** || includeDone */) {
                    subItemSum += item.getEffortEstimate(forSubtasks);
                }
            }
            return subItemSum;
//            return ((Long) derivedEstimateEffortSubItemsSumBuffered.getValue()).longValue();
        } else {
//            return effortEstimate;
            Long effort = getLong(PARSE_EFFORT_ESTIMATE);
//            if (MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() != 0 && (effort == null || effort == 0)) {
//                return MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() * MyDate.MINUTE_IN_MILLISECONDS;
//            } else {
            return (effort == null) ? 0L : effort;
//            }
        }
    }

    @Override
    public long getEffortEstimate() {
        return getEffortEstimate(true);
    }

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
    /**
     *
     * @param remainingEffort
     * @param autoUpdateEffortEstimate
     * @param forProjectTaskItself set the effort for this task even though it
     * is a project with subtasks
     */
    public void setRemainingEffort(long remainingEffort, boolean autoUpdateEffortEstimate, boolean forProjectTaskItself) {
//#mdebug
//        ASSERT.that(remainingEffort >= 0, "RemainingEffort cannot be negative");
        assert remainingEffort >= 0 : "RemainingEffort cannot be negative";
//#enddebug
//        this.effortEstimate = val;
        if (forProjectTaskItself || !isProject()) { //don't save for projects
            long prevRemaining = getRemainingEffort();
            if (remainingEffort != 0) {
                put(PARSE_REMAINING_EFFORT, remainingEffort); //update first 
            } else {
                remove(PARSE_REMAINING_EFFORT);
            }
            if (prevRemaining != remainingEffort) {
//            if (autoUpdateEffortEstimate && prevRemaining == 0) {
//            if (getEffortEstimate() == 0 && Settings.getInstance().isAlwaysSetFirstEstimateToInitialEstimate()) { //if no previous estimate, use Remaining
                //UI: if no effort estimate has been set, then use Remaining+Actual as historical estimate (
                //TODO: this requires Estimate and Actual to be set *before* setting Remaining
//                if (getEffortEstimate() == 0 && Settings.getInstance().alwaysUseRemainingAsEstimateWhenActualIsZero()) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
                if (autoUpdateEffortEstimate && prevRemaining == 0 && getEffortEstimate() == 0 && MyPrefs.getBoolean(MyPrefs.automaticallyUseFirstRemainingPlusActualAsInitialEstimateWhenEffortEstimateIsZero)) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
                    setEffortEstimate(remainingEffort + getActualEffort(false), false, forProjectTaskItself); //since we test for 0, no problem if setting Estimate both here and direct
                }
//                if (getActualEffort() == 0 && Settings.getInstance().alwaysUseRemainingAsEstimateWhenActualIsZero()) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
//                    setEffortEstimate(remainingEffort, false); //since we test for 0, no problem if setting Estimate both here and direct
//                }
//            }
//            if (remainingEffort == 0 && !isDone() && Settings.getInstance().markDoneIfRemainingReducedToZero()) {
//                setDone(true); //UI: reducing Remaining to 0 takes precedence over setting Done status to other (non-Done) values
//            }
//            this.remainingEffort = val;
//            if (has(PARSE_REMAINING_EFFORT) || remainingEffort != 0) {
////                put(PARSE_REMAINING_EFFORT, new Date(remainingEffort));
//                put(PARSE_REMAINING_EFFORT, remainingEffort);
//            }
            }
        }
    }

    public void setRemainingEffort(long val) {
        setRemainingEffort(val, false, false);
    }

//    public void setRemainingEffortInMinutes(int val) {
//        setRemainingEffort(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
    /**
     * return effort estimate for this task. If subtasks exist, and the sum of
     * their effort estimates is different from zero, then return that value,
     * otherwise return the value for this task. Only tasks that are NOT marked
     * as Done are included. If this task
     *
     * @return
     */
    @Override
    public Date getRemainingEffortD() {
        return new Date(getRemainingEffort());
    }

    @Override
    public long getRemainingEffort() {
//        if (isDone()) {
//            return 0;
//        }
        return getRemainingEffort(true, true);
    }

    public long getRemainingEffortNoDefault() {
//        if (isDone()) {
//            return 0;
//        }
        return getRemainingEffort(true, false);
    }

//    public int getRemainingEffortInMinutes() {
//        return (int) getRemainingEffort() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
    public long getRemainingEffort(boolean forSubtasks) {
        return getRemainingEffort(forSubtasks, true);
    }

    public long getRemainingEffort(boolean forSubtasks, boolean useDefaultEstimateForZeroEstimates) {
        if (isDone()) {
            return 0;
        }

//        if (forSubtasks && getItemListSize() > 0) {
        if (forSubtasks && isProject()) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_REMAINING); //optimization: store sum in intermediate variable to avoid recalculating each time
//            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue()).longValue();
//            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue());
            long subItemSum = 0;
//            for (Object i : subitems) {
//            for (Object i : getItemList()) {
            for (Object i : getList()) {
                Item item = (Item) i;
                if (!item.isDone()) {
                    subItemSum += item.getRemainingEffort(forSubtasks);
                }
            }
            return subItemSum;
        } else {
//            return remainingEffort;
            Long remainingEffort = getLong(PARSE_REMAINING_EFFORT);
            if (useDefaultEstimateForZeroEstimates && MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt() != 0 && (remainingEffort == null || remainingEffort == 0)) {
                return ((long) MyPrefs.estimateDefaultValueForZeroEstimatesInMinutes.getInt()) * MyDate.MINUTE_IN_MILLISECONDS;
            } else {
                return (remainingEffort == null) ? 0L : remainingEffort;
            }
        }
    }

    //TODO! consider the use of autoUpdateStatusAndStartedOnDate (all effort methods!)
    public void setActualEffort(long actualEffort, boolean autoUpdateStatusAndStartedOnDate, boolean forProjectTaskItself) {
//        ASSERT.that(actualEffort >= 0, "ActualEffort cannot be negative");
        ASSERT.that(actualEffort >= 0, "ActualEffort cannot be negative");
        if (forProjectTaskItself || !isProject()) {
            if (getActualEffort() != actualEffort) {
                if (autoUpdateStatusAndStartedOnDate) {
                    if (actualEffort > 0) {
                        //if setting Actual to a positive value (larger than zero) for the first time, and status==Created, set to Ongoing
                        if (getActualEffort() == 0 && getStatus() == ItemStatus.CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
                            setStatus(ItemStatus.ONGOING); //automatically set to Ongoing as soon as time is spent on the task; setStatus set startedOn date
                        }
                    } else //if Actual is reduced to zero then set status back to Created and reset StartedOn date
                    {
                        if (actualEffort == 0 && getActualEffort() > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //TODO replace use of Settings by MyPrefs
                            setStatus(ItemStatus.CREATED);
                        }
                    }
                }
//                if (has(PARSE_ACTUAL_EFFORT) || actualEffort != 0) {
//                    put(PARSE_ACTUAL_EFFORT, actualEffort);
//                }
                if (actualEffort != 0) {
                    put(PARSE_ACTUAL_EFFORT, actualEffort);
                } else {
                    remove(PARSE_ACTUAL_EFFORT);
                }
            }
        }
    }

    public void setActualEffort(long val) {
        setActualEffort(val, false, false);
    }

    /**
     * adds val to the current ActualEffort (for the Item at hand whether
     * project or subtask)
     *
     * @param val
     */
    public void addToActualEffort(long val) {
        setActualEffort(getActualEffort(false) + val, false, true); //TODO check use of for subtasks or for project itself
    }

//    public void setActualEffortInMinutes(int val) {
//        setActualEffort(val * MyDate.MINUTE_IN_MILLISECONDS);
//    }
    public long getActualEffort(boolean forSubtasks) {
        if (forSubtasks && isProject()) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ACTUAL); //optimization: store sum in intermediate variable to avoid recalculating each time
            long subItemSum = 0;
            for (int i = 0, size = getItemListSize(); i < size; i++) {
//                Item item = (Item) getItemList().getItemAt(i);
                Item item = (Item) getList().get(i);
                if (true || !item.isDone()) { // /** || includeDone */) { //ALWAYS include actual even for Done tasks so project Actual is exhaustive
                    subItemSum += item.getActualEffort(forSubtasks);
                }
            }
            return subItemSum + getActualEffort(false); //UI: add any effort registered on the project (e.g. before it became a project, or by manual editing)
//            return ((Long) derivedActualEffortSubItemsSumBuffered.getValue()).longValue();
        } else {
//            return actualEffort;
            Long actual = getLong(PARSE_ACTUAL_EFFORT);
            return (actual == null) ? 0L : actual;
        }
    }

    public long getActualEffort() {
        return getActualEffort(true);
    }

//    public int getActualEffortInMinutes() {
//        return (int) getActualEffort() / MyDate.MINUTE_IN_MILLISECONDS;
//    }
    static private long getManualUpdateValue(String textStr, String fieldName, long initValue) {
        Duration duration = new Duration(initValue);
//        ScreenDurationPicker screenDurationPicker = new ScreenDurationPicker("Update Remaining?", duration, true, 1, "Update", textStr);
////        screenDurationPicker.edit();
////        screenDurationPicker.display();
        return duration.getDuration();
    }

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
////            setRemainingEffort(res);
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
//            setRemainingEffort(remaining);
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
//                        setRemainingEffort(newRemainingEffort);
//                    } else { //both actual and remaining effort have changed
//                        setRemainingEffort(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
//                        setActualEffort(newActualEffort, false, false);
//                    }
//                }
//            } else //effort estimate has changed
//            {
//                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
//                    setEffortEstimate(newEffortEstimate); //contains the logic to update
//                } else //remaining effort or actual effort have changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
//                        setEffortEstimate(newEffortEstimate, false, false);
//                        setRemainingEffort(newRemainingEffort, false, false);
//                    } else { //both actual and remaining effort have changed
//                        setEffortEstimate(newEffortEstimate, false, false);
//                        setRemainingEffort(newRemainingEffort, false, false);
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
//                        setRemainingEffort(newRemainingEffort);
//                    } else { //both actual and remaining effort have changed
//                        setRemainingEffort(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
//                        setActualEffort(newActualEffort, false, false); //don't autoupdate status
//                    }
//                }
//            } else //effort estimate has changed
//            {
//                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
//                    setEffortEstimate(newEffortEstimate); //contains the logic to update
//                } else //remaining effort or actual effort have changed
//                {
//                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
//                        setEffortEstimate(newEffortEstimate, false, false);
//                        setRemainingEffort(newRemainingEffort, false, false);
//                    } else { //both actual and remaining effort have changed
//                        setEffortEstimate(newEffortEstimate, false, false);
//                        setRemainingEffort(newRemainingEffort, false, false);
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
     *
     * @param locallyEditedCategories
     */
    public void updateCategories(List<Category> locallyEditedCategories) {
        updateCategories(locallyEditedCategories, false);
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
    public void updateCategories(List<Category> locallyEditedCategories, boolean onlyAddNewCatsDontRemoveAny) {
//        if (locallyEditedCategories == null || locallyEditedCategories.size() == 0) {
        if (locallyEditedCategories == null || locallyEditedCategories.isEmpty()) {
            return;
        }
        Item item = this;

        Set<Category> addedCats = new HashSet(locallyEditedCategories);//make a copy of the edited set of categories
        addedCats.removeAll(item.getCategories()); //remove all that were already set of the item to get only the newly added categories
        for (Category cat : addedCats) {
//            cat.addItemAtIndex(item, MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists) ? 0 : cat.getSize());
            cat.addItemToCategory(item, false);
            DAO.getInstance().save(cat);
        }

        if (!onlyAddNewCatsDontRemoveAny) {
            Set<Category> unSelectedCats = new HashSet(item.getCategories());
            unSelectedCats.removeAll(locallyEditedCategories);
            for (Category cat : unSelectedCats) {
//                cat.remove(item);
                cat.removeItemFromCategory(item, false);
                DAO.getInstance().save(cat);
            }
            item.setCategories(new ArrayList(locallyEditedCategories)); //set the item's categories as the locally edited ones
        } else {
            //only set the item's categories to the old ones + the newly added
            ArrayList<Category> existingCatsPlusAdded = new ArrayList(item.getCategories());
            existingCatsPlusAdded.addAll(addedCats);
            item.setCategories(existingCatsPlusAdded); //set the item's categories as the old ones + newly added ones
        }
    }

    /**
     * adds category to this item's categories if not already there (no
     * duplicates). Does not save category
     */
    public void addCategoryToItem(Category category, boolean addItemToCategory) {
        if (category != null) {
            if (!getCategories().contains(category)) {
                List cats = getCategories();
                cats.add(category);
                setCategories(cats);
            }
        }
        if (addItemToCategory) {
            category.addItemToCategory(this, false);
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
            List cats = getCategories();
            cats.remove(this);
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
                MyPrefs.getBoolean(MyPrefs.commentsAddTimedEntriesWithDateANDTime) ? MyDate.formatDateTimeNew(new Date()) + ": "
                : MyDate.formatDateNew(new Date()) + ": ",
                !MyPrefs.getBoolean(MyPrefs.commentsAddToBeginningOfComment));
    }

    public long getCompletedDate() {
//        return completedDate;
//        Date date = getDate(PARSE_COMPLETED_DATE);
//        return (date == null) ? 0L : date.getTime();
        return getCompletedDateD().getTime();
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
            Date date = getDate(PARSE_COMPLETED_DATE);
            return (date == null) ? new Date(0) : date;
        } else { //isProject
            if (isDone()) {
//                Date latestSubTaskCompletedDate = new Date(MyDate.MIN_DATE);
                Date latestSubTaskCompletedDate = new Date(0);
                for (Object item : getList()) {
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
//        if (has(PARSE_COMPLETED_DATE) || completedDate != 0) {
//            if (completedDate == 0) {
//                remove(PARSE_COMPLETED_DATE); //delete when setting to default value
//            } else {
//                put(PARSE_COMPLETED_DATE, new Date(completedDate));
//            }
//        }
        if (completedDate != null && completedDate.getTime() != 0) {
            put(PARSE_COMPLETED_DATE, completedDate);
//            if (!isDone()) {
//                setDone(true);
//            }
        } else {
            remove(PARSE_COMPLETED_DATE); //delete when setting to default value
//            if (isDone()) {
//                setDone(false);
//            }
        }
    }

    public void setDeletedDate(Date dateDeleted) {
        if (dateDeleted != null && dateDeleted.getTime() != 0) {
            put(PARSE_DELETED_DATE, dateDeleted);
        } else {
            remove(PARSE_DELETED_DATE); //delete when setting to default value
        }
    }

    public Date getDeletedDate() {
        Date date = getDate(PARSE_DELETED_DATE);
        return (date == null) ? new Date(0) : date;
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
    public long getAlarmDate(int alarmId) {
        if (alarmId == Item.FIELD_ALARM_DATE) {
            return getAlarmDate();
        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
            return getWaitingAlarmDate();
        } else {
            return 0;
        }
//        RuntimeException     ("Not supported yet.");
    }

    public String getAlarmText(int alarmId) {
        if (alarmId == Item.FIELD_ALARM_DATE) {
            return getText();
        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
            return getText();
        } else {
            return "";
        }
//        ASSERT.that("Not supported yet.");
    }

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
    static long getTotalExpectedEffort(long remainingEffort, long actualEffort, long effortEstimate) {
//        return getRemainingEffort() + getActualEffort();
        long totEff;// = 0;
        if (remainingEffort != 0) {
            totEff = remainingEffort + actualEffort; //whether actual is zero or not, and whether estimate is larger or not
        } else {
            totEff = Math.max(actualEffort, effortEstimate);
        }
//            if (actualEffort > effortEstimate) {
//            totEff = actualEffort;
//        } else {
//            totEff = effortEstimate;
//        }
        return totEff;
    }

    long getTotalExpectedEffort() {
        return getTotalExpectedEffort(getRemainingEffort(), getActualEffort(), getEffortEstimate());
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
    public void commit() throws ParseException {
        this.save();//ParseObject.save()

    }

//    public int countTasksWhereStatusWillBeChanged(ItemStatus newStatus, boolean recurseOverSubtasks) {
//        return (changeSubtaskStatus(newStatus, getStatus()) ? 1 : 0)
//                + ((recurseOverSubtasks && getItemListSize() > 0)
//                        ? getItemList().countTasksWhereStatusWillBeChanged(newStatus, recurseOverSubtasks)
//                        : 0);
//    }
    @Override
    public String toString() {
//        return getText();
//        return getText().length() != 0 ? getText()+" ("+getObjectId()+")" : getObjectId();
        return getText() + " (" + getObjectIdP() + ")"
                + (isDone() ? "[V]" : (getRemainingEffort() > 0 ? MyDate.formatTimeDuration(getRemainingEffort()) : ""))
                + (getList().size() == 0 ? "" : " subtasks={" + getListAsCommaSeparatedString(getList()) + "}");
    }

    @Override
    /**
     * only tests based on strict equality (either same object or same objectId)
     *
     */
    public boolean equals(Object obj) {
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
//        return ((Item) obj).getObjectId().equals(getObjectId());
        if (getObjectIdP() != null && ((Item) obj).getObjectIdP() != null) {
            //compare isDirty in case we have two instances of the same 
//            return getObjectId().equals(((Item) obj).getObjectId()) && isDirty()==((Item) obj).isDirty();
            ASSERT.that(!getObjectIdP().equals(((Item) obj).getObjectIdP()) || isDirty() == ((Item) obj).isDirty(), "comparing dirty and not dirty instance of same object=" + this);
            if (getObjectIdP().equals(((Item) obj).getObjectIdP())) {
                return true;
            }
        }
        return false; //this == (Item) obj;
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

    @Override
    public void save() throws ParseException {
        if (isDirty()) {
            listeners.fireDataChangeEvent(DataChangedListener.CHANGED, -1); //TODO optimize and only send change even on relevant changes (e.g. status change, remaining/actual/effort changes)
        }
        updateFirstAlarm();
        super.save();
//        if (afterSaveActions.containsKey(AFTER_SAVE_ALARM_UPDATE)) {
//            afterSaveActions.remove(AFTER_SAVE_TEXT_UPDATE); //if we're updating alarms due to time change, we can ignore any changed to the text
//        }
//        for (MyForm.Action action : afterSaveActions.values()) {
//            action.launchAction();
////            afterSaveActions.remove(action); //NOT allowed, throws java.util.ConcurrentModificationException, see eg http://stackoverflow.com/questions/8104692/how-to-avoid-java-util-concurrentmodificationexception-when-iterating-through-an
//        }
//        afterSaveActions.clear();
        if (mustUpdateAlarms) {
            AlarmHandler.getInstance().updateAlarmsOrTextForItem(this);
            mustUpdateAlarms = false;
        }
    }

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
//    public List<WorkSlot> getWorkSlotList() {
    public WorkSlotList getWorkSlotList(boolean refreshWorkSlotListFromDAO) {
        if (workSlotListBuffer == null || refreshWorkSlotListFromDAO) {
            workSlotListBuffer = DAO.getInstance().getWorkSlots(this);
        }
        return workSlotListBuffer;
    }

    @Override
    public WorkSlotList getWorkSlotList() {
        if (workSlotListBuffer == null) {
            workSlotListBuffer = DAO.getInstance().getWorkSlots(this);
        }
        return workSlotListBuffer;
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
                ASSERT.that(time >= startOfToday && time < startOfTomorrow,
                        "getStartByDateD should always be Today, item=" + this
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
                || getActualEffort() != 0
                || getRemainingEffort() != 0
                || getCategories().size() != 0
                || getItemList().size() != 0;
    }

    /**
     * eg ("hamburger",4,3) -> "hamber" eg ("hamburger",4,3) -> "hamber"
     *
     * @param s
     * @param start
     * @param len
     * @return
     */
    public static String deleteSubstring(String s, int start, int len) {
        //"hamburger".substring(4, 8) returns "urge" so endIndex is not included
        return s.substring(0, start)
                + s.substring(start + len);
    }

    private static int getIntFromTextString(String txt, int start, int len) {
        String valStr = txt.substring(start, start + len);
        int val = Integer.parseInt(valStr);
        return val;
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
//        if (MyPrefs.itemEffortEstimateExtractFromStringInTaskText.getBoolean()) {
        RE minutes_hours_RE = new RE("\\b([0-9]+)(?:h|\\:)([0-5][0-9]|[0-9])(?:m(in)?)?\\b"); //HHhMM. OK: 0h17, 10h00 2h17m, 199h. NOK: 1h65, Not allowed to start with '0' 
        if (minutes_hours_RE.match(txt)) {
            hours = getIntFromTextString(txt, minutes_hours_RE.getParenStart(1), minutes_hours_RE.getParenLength(1));
            minutes = getIntFromTextString(txt, minutes_hours_RE.getParenStart(2), minutes_hours_RE.getParenLength(2)) + 60 * hours;
            if (!keepOrgTextUnchanged && !MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
                txt = deleteSubstring(txt, minutes_hours_RE.getParenStart(0), minutes_hours_RE.getParenLength(0));
            }
//                System.out.println("ValStr=\"" + valStr + "\" val=" + val + "\tcleaned=\"" + txt + "\"\n");
        } else {
            RE minutes_RE = new RE("\\s*\\b(([1-9][0-9]+)|([1-9]))m(?:in)?\\b"); //MINUTES not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
            if (minutes_RE.match(txt)) {
                minutes = getIntFromTextString(txt, minutes_RE.getParenStart(1), minutes_RE.getParenLength(1));
                if (!keepOrgTextUnchanged && !MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
                    txt = deleteSubstring(txt, minutes_RE.getParenStart(0), minutes_RE.getParenLength(0));
                }
            } else {
                RE hours_RE = new RE("\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"); //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
                if (hours_RE.match(txt)) {
                    minutes = getIntFromTextString(txt, hours_RE.getParenStart(1), hours_RE.getParenLength(1));
                    if (!keepOrgTextUnchanged && !MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
                        txt = deleteSubstring(txt, hours_RE.getParenStart(0), hours_RE.getParenLength(0));
                    }
                }
//                }
            }
        }
//        return new Item().new EstimateResult(minutes, txt);
        return new Item.EstimateResult(minutes, txt);
    }

    public static String parseTaskTextForProperties(Item item, String txt) {
        //TODO!!! remove whitespace before (include preceding whitespace in RE)
        //TODO!!! also remove whitespace after if end of text (eg "xxx 10m  ") - but not if other text afterwards (eg "xxx 10m yyy")
        //TODO!!! add regexps for understanding Siri words like "estimate five/5 minutes"
//                String minutes = "\\b(([1-9][0-9]+)|([1-9]))m(in)?\\b"; //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//        String hours = "\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"; //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"

        RE priority = new RE("[p|P][19]"); //eg "p1", "P9" but not "P0"
        RE urgImp = new RE("[HH|HM|HL|MH|MM|ML|LH|LM|LL]"); //eg "HM"
        RE value = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
        RE challengeXXX = new RE("v|V[19]+[09]*"); //eg "v10" or "V10,50" ***
        RE funXXX = new RE("v|V[19]+[09]*"); //eg "ffun" "ddread" or "/fun" - but slower to type on iPhone since "/" is other keybaord and requires going back to letters afterwards
        RE notesXXX = new RE("//text"); //eg "task1 //these are notes"

        //DATES
        RE dueXXX = new RE("//text"); //eg "tomorrow"

        //RELATIVE DATES (+/- wrt Due date)
        RE alarmXXX = new RE("//text"); //eg "a-5h" "a-5d"
        RE waitUntilXXX = new RE("//text"); //eg "w-5h" "w-5d"
        RE startByXXX = new RE("//text"); //eg "s-5h" "s-5d2h" or "s:tomorrow", s:12/6", "s:7jun" "s:7jun17" "s:7jun2018"

        //CATEGORIES
        RE categoryXXX = new RE("//text"); //eg "/cat1"

        //ESTIMATE
//        int minutes = 0;
//        int hours = 0;
        if (MyPrefs.itemEffortEstimateExtractFromStringInTaskText.getBoolean()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            RE minutes_hours_RE = new RE("\\s*\\b([0-9]+)(?h|:)([0-5][0-9]))(?m(in)?)?\\b"); //HHhMM. OK: 0h17, 10h00 2h17m, 199h. NOK: 1h65, Not allowed to start with '0'
//            if (minutes_hours_RE.match(txt)) {
//                hours = getIntFromTextString(txt, minutes_hours_RE.getParenStart(1), minutes_hours_RE.getParenLength(1));
//                minutes = getIntFromTextString(txt, minutes_hours_RE.getParenStart(2), minutes_hours_RE.getParenLength(2)) + 60 * hours;
//                if (!MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
//                    txt = deleteSubstring(txt, minutes_hours_RE.getParenStart(0), minutes_hours_RE.getParenLength(0));
//                }
////                System.out.println("ValStr=\"" + valStr + "\" val=" + val + "\tcleaned=\"" + txt + "\"\n");
//            } else {
//                RE minutes_RE = new RE("\\s*\\b(([1-9][0-9]+)|([1-9]))m(?in)?\\b"); //MINUTES not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//                if (minutes_RE.match(txt)) {
//                    minutes = getIntFromTextString(txt, minutes_RE.getParenStart(1), minutes_RE.getParenLength(1));
//                    if (!MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
//                        txt = deleteSubstring(txt, minutes_RE.getParenStart(0), minutes_RE.getParenLength(0));
//                    }
//                } else {
//                    RE hours_RE = new RE("\\b(([1-9][0-9]+)|([1-9]))h(our(s)?)?\\b"); //not allowed to start with '0' //issues: "7min" gives "7m", "07m" gives "7m"
//                    if (hours_RE.match(txt)) {
//                        minutes = getIntFromTextString(txt, hours_RE.getParenStart(1), hours_RE.getParenLength(1));
//                        if (!MyPrefs.itemEffortEstimateKeepStringInTaskText.getBoolean()) {
//                            txt = deleteSubstring(txt, hours_RE.getParenStart(0), hours_RE.getParenLength(0));
//                        }
//                    }
//                }
//            }
//</editor-fold>
            EstimateResult res = getEffortEstimateFromTaskText(txt);
            txt = res.cleaned;
            item.setEffortEstimate(((long) res.minutes) * MyDate.MINUTE_IN_MILLISECONDS, true, true); //update remaining, set for project-level
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

    public String[] toCSV() {
        ArrayList list = new ArrayList();
        int i = 1;
        Object val = null;
        boolean write = true;
        String s = "";
        switch (s) {
            case PARSE_TEXT:
                if (write) {
                    list.add(getText());
                } else {
                    setText((String) val);
                }
                break;
            case PARSE_COMMENT:
                if (write) {
                    list.add(getComment());
                } else {
                    setComment((String) val);
                }
                break;
            case PARSE_SUBTASKS:
                if (write) {
                    list.add(getStatus().toString());
                } else {
                    setStatus((ItemStatus) val);
                }
                break;
            case PARSE_DREAD_FUN_VALUE:
                if (write) {
                    list.add(getDreadFunValue().getDescription());
                } else {
                    setDreadFunValue(DreadFunValue.getValue((String) val));
                }
                break;
            case PARSE_CHALLENGE:
                if (write) {
                    list.add(getChallenge().getDescription());
                } else {
                    setChallenge(Challenge.getValue((String) val));
                }
                break;
            case PARSE_EXPIRES_ON_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getExpiresOnDateD()));
                } else {
                    setExpiresOnDate((Date) val);
                }
                break;
            case PARSE_INTERRUPT_OR_INSTANT_TASK:
                if (write) {
                    list.add(((Boolean) isInteruptOrInstantTask()).toString());
                } else {
                    setInteruptOrInstantTask(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_ALARM_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getAlarmDateD()));
                } else {
                    setAlarmDate((Date) val);
                }
                break;
            case PARSE_WAITING_ALARM_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getWaitingAlarmDateD()));
                } else {
                    setWaitingAlarmDate((Date) val);
                }
                break;
            case PARSE_REPEAT_RULE:
                if (write) {
                    list.add(getRepeatRule().toString());
                } else {
                    Log.p("Cannot import " + Item.REPEAT_RULE);
                }
                break;
            case PARSE_STARTED_ON_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getStartedOnDateD()));
                } else {
                    setStartedOnDate((Date) val);
                }
                break;
            case PARSE_STATUS:
                if (write) {
                    list.add(getStatus().getDescription());
                } else {
                    setStatus(ItemStatus.getValue((String) val));
                }
                break;
            case PARSE_DUE_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getDueDateD()));
                } else {
                    setDueDate((Date) val);
                }
                break;
            case PARSE_HIDE_UNTIL_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getHideUntilDateD()));
                } else {
                    setHideUntilDate((Date) val);
                }
                break;
            case PARSE_START_BY_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getStartByDateD()));
                } else {
                    setStartByDate((Date) val);
                }
                break;
            case PARSE_WAITING_TILL_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getWaitingTillDateD()));
                } else {
                    setWaitingTillDate((Date) val);
                }
                break;
            case PARSE_DATE_WHEN_SET_WAITING:
                if (write) {
                    list.add(MyDate.formatDateNew(getDateWhenSetWaitingD()));
                } else {
                    setDateWhenSetWaiting((Date) val);
                }
                break;
            case PARSE_EFFORT_ESTIMATE:
                if (write) {
                    list.add((MyDate.formatTimeDuration(getEffortEstimate(false))));
                } else {
                    setEffortEstimate((Long) val, false, true);
                }
                break;
            case PARSE_REMAINING_EFFORT:
                if (write) {
                    list.add((MyDate.formatTimeDuration(getRemainingEffort(false))));
                } else {
                    setRemainingEffort((Long) val, false, true); //UI: import of project estimates
                }
                break;
            case PARSE_ACTUAL_EFFORT:
                if (write) {
                    list.add((MyDate.formatTimeDuration(getActualEffort(false))));
                } else {
                    setActualEffort((Long) val, false, true); //UI: import of project estimates
                }
                break;
            case PARSE_CATEGORIES:
                //TODO!!!!search for category based on text string, if not existing, create it and add this task
                break;
            case PARSE_PRIORITY:
                if (write) {
                    list.add(((Integer) getPriority()).toString());
                } else {
                    setPriority(Integer.parseInt((String) val)); //TODO!!! more resistant parsing of val (illegal formats, null values)
                }
                break;
            case PARSE_STARRED:
                if (write) {
                    list.add(((Boolean) isStarred()).toString());
                } else {
                    setStarred(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_EARNED_VALUE:
                if (write) {
                    list.add(((Double) getEarnedValue()).toString());
                } else {
                    setEarnedValue(Double.parseDouble((String) val));//TODO!!! more resistant parsing of val (illegal formats, null values)
                }
                break;
            case PARSE_COMPLETED_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getCompletedDateD()));
                } else {
                    setCompletedDate((Date) val);
                }
                break;
            case PARSE_IMPORTANCE:
                if (write) {
                    list.add(getImportance().getDescription());
                } else {
                    setImportance(HighMediumLow.getValue((String) val));
                }
                break;
            case PARSE_URGENCY:
                if (write) {
                    list.add(getUrgency().getDescription());
                } else {
                    setUrgency(HighMediumLow.getValue((String) val));
                }
                break;
            case PARSE_OWNER_LIST:
                //TODO!!! search for owner list based on text string, if not existing, create it
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
                if (write) {
                    list.add(((Boolean) isTemplate()).toString());
                } else {
                    setTemplate(Boolean.parseBoolean((String) val));
                }
                break;
            case PARSE_UPDATED_AT:
                list.add(MyDate.formatDateNew(getUpdatedAt()));
                if (write) {
                    list.add(MyDate.formatDateNew(getUpdatedAt()));
                } else {
                    Log.p("Cannot import " + Item.UPDATED_DATE);
                }
                break;
            case PARSE_CREATED_AT:
                if (write) {
                    list.add(MyDate.formatDateNew(getCreatedDateD()));
                } else {
                    Log.p("Cannot import " + Item.CREATED_DATE);
                }
                break;
            case PARSE_ORIGINAL_SOURCE:
                //TODO!!! search for task/project based on text string, if not existing, create it (?=> means need to check if already existing when importing/creating tasks) and add this task as sub-task
                break;
            case PARSE_DELETED_DATE:
                if (write) {
                    list.add(MyDate.formatDateNew(getDeletedDate()));
                } else {
                    setDeletedDate((Date) val);
                }
                break;
        }

//        Writer w = new Writer();
//        return CSVHelper.writeLine(w, list);
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

//    public boolean hasWorkTimeDefinition() {
//        return getWorkSlotList() != null;
//    }
    @Override
    public List<ItemAndListCommonInterface> getWorkTimeProvidersInPrioOrder() {
//        return getWorkTimeProvidersInPrioOrder(true);
//    }
//    private List<ItemAndListCommonInterface> getWorkTimeProvidersInPrioOrder(boolean includeOwner) {
        boolean includeOwner = true;
        List<ItemAndListCommonInterface> providers = new ArrayList();

        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
//        if (hasWorkTimeDefinition()) {
//UI: if an item has BOTH own workSlots AND a category with WorkSLots, then it will first try to get workTime from its own workslots (otherwise, why would it have them?!)
        if (getWorkSlotList() != null) {
            if (!providers.contains(this)) {
                providers.add(this);
            }
        }

        ItemAndListCommonInterface owner = includeOwner ? getOwner() : null;
//        if (!MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean()) {
//            if (owner != null) {
//                if (owner.hasWorkTimeDefinition()) {
        if (owner != null && !MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.hasWorkTime()) {
            if (!providers.contains(owner)) {
                providers.add(owner);
            }
        }

        List<Category> categories = getCategories();
        for (Category cat : categories) {
            if (cat.hasWorkTime()) {
                if (!providers.contains(cat)) {
                    providers.add(cat);
                }
            }
        }

//        if (MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean()) {
//            if (owner != null) {
//                if (owner.hasWorkTimeDefinition()) {
        if (owner != null && MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean() && owner.hasWorkTime()) {
            if (!providers.contains(owner)) {
                providers.add(owner);
            }
        }

        if (providers.size() > 0) {
            return providers;
        } else {
            return null;
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getNeededWorkTime() {
//        return getRemainingEffort();
//    }
//    public List<WorkTime> getAllocatedWorkTime() {
//    public WorkTime getAllocatedWorkTime(ItemAndListCommonInterface subtask) {
//    public WorkTime getAllocatedWorkTime() {
//        long neededWorkTime = getNeededWorkTime();
//        int subtaskIndex = getList().indexOf(subtask);
//        WorkTime availableWorkTime;
//        WorkTime newWorkTime;
//        if (subtaskIndex < 0) {
//            return new WorkTime();
//        } else if (subtaskIndex == 0) {
//            while (neededWorkTime > 0) {
//                newWorkTime.setStartTime(availableWorkTime.getStartTime());
//                WorkSlot w = availableWorkTime.getWorkSlots().get(0);
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
//        WorkTime workTime = null;
//        for (ItemAndListCommonInterface workTimeProvider : getWorkTimeProviders()) {
////                if (workTimeProvider==this) return Arrays.asList(new WorkTime(getWorkSlotList(),0,Long.MAX_VALUE));
//            if (workTimeProvider == this) {
//                return new WorkTime(getWorkSlotList(), 0, Long.MAX_VALUE);
//            }
//            if (workTime == null) {
//                workTime = workTimeProvider.getAllocatedWorkTime(this);
//            } else {
//                workTime.setNextWorkTime(new WorkTime(workTime, 0, 0, workTime));
//            }
//            neededWorkTime -= workTime.getAllocatedTime();
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public WorkTime getAllocatedWorkTime() {
////        long neededWorkTime = getNeededWorkTime();
//        long neededWorkTime = getRemainingEffort();
//        WorkTime workTime = null;
//        WorkTime lastWorkTime;
////        for (ItemAndListCommonInterface workTimeProvider : getWorkTimeProviders()) {
//        Iterator<ItemAndListCommonInterface> workTimeProviders = getWorkTimeProviders().iterator();
//        while (workTimeProviders.hasNext() && neededWorkTime > 0 && (workTime == null || workTime.getRemainingDuration() > 0)) {
//            ItemAndListCommonInterface workTimeProvider = workTimeProviders.next();
//            if (workTime == null) {
//                lastWorkTime = workTimeProvider.getWorkTimeAllocator().getAllocatedWorkTime(this);
//                workTime = lastWorkTime;
//            } else if (workTime.getRemainingDuration() > 0) {
////                workTime=workTimeProviders.getAllocatedWorkTime(workTime,workTimeProvider.getAllocatedWorkTime(this, workTime.getUncoveredTime()));
//                lastWorkTime = workTimeProvider.getWorkTimeAllocator().getAllocatedWorkTime(this, workTime.getRemainingDuration());
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
//    public WorkTimeDefinition getWorkTimeAllocator() {
//        return getWorkTimeAllocator(false);
//    }
//</editor-fold>

    @Override
    public WorkTimeAllocator getWorkTimeAllocator(boolean reset) {
        if (true || wtd == null || reset) {
//            WorkTime availableWorkTime = getAvailableWorkTime();
            WorkTime availableWorkTime = getAllocatedWorkTime();
            if (availableWorkTime != null) {
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), availableWorkTime);
                wtd = new WorkTimeAllocator(getList(), availableWorkTime, this);
            }
        }
        return wtd;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTimeDefinition getWorkTimeAllocator(boolean reset) {
//        if (wtd != null && !reset) {
//            return wtd;
//        } else { //get right workSlots
//            WorkSlotList workSlots = getWorkSlotList();
//            if (workSlots != null && workSlots.hasComingWorkSlots()) {
//                wtd = new WorkTimeDefinition(getLeafTasksAsList(item -> !item.isDone()), workSlots);
//                return wtd;
//            } else {
//                WorkTimeDefinition workTimeDef;
//                ItemAndListCommonInterface owner = getOwner();
//                if (owner != null) {
//                    workTimeDef = owner.getWorkTimeAllocator(reset);
//                    if (workTimeDef != null) {
//                        return workTimeDef;
//                    }
//                }
//                List<Category> categories = getCategories();
//                for (Category cat : categories) {
//                    workTimeDef = cat.getWorkTimeAllocator(reset);
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
//        if (wtd == null || reset) { //            wtd = new WorkTimeDefinition(itemListOrg.getWorkSlotList(true), itemListFilteredSorted);
////            wtd = new WorkTimeDefinition(getList(), this);
////            wtd = new WorkTimeDefinition(getList(), getWorkSlotList());
//            WorkSlotList workSlots = getWorkSlotList();
//            if (workSlots != null && !workSlots.isEmpty()) {
////            wtd = new WorkTimeDefinition(getLeafTaskSAsList(item -> !item.isDone()) , getWorkSlotList());
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
        wtd = null;
    }

    @Override
//    public long getWorkTimeRequiredFromOwner() {
    public long getWorkTimeRequiredFromProvider(ItemAndListCommonInterface provider) {
        if (false) {
            Log.p("getWorkTimeRequiredFromProvider(provider=" + provider + ") for item=" + this);
        }

        if (isDone()) {
            return 0;
        }
        long required = 0;
        //get the amount of worktime the subtasks require from me (their mother project)
        List<ItemAndListCommonInterface> subtasks = getList();
        if (subtasks != null && subtasks.size() > 0) {
            for (ItemAndListCommonInterface subtask : subtasks) { //starts from the *last* element in the list!!!
                required += subtask.getWorkTimeRequiredFromProvider(this);
            }
        } else { //leaf task
            required = getRemainingEffort(); //how much total workTime is required?
        }

        //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
        List<ItemAndListCommonInterface> providers = getWorkTimeProvidersInPrioOrder();
        if (providers != null) {
//            ItemAndListCommonInterface prov;
//            for (int i = 0, size = providers.size(); i < size; i++) {
            for (ItemAndListCommonInterface prov : providers) {
//                prov = providers.get(i);
                if (prov.equals(provider)) { //prov == provider) {
                    break; // return what is remaining for provider
                } else {
                    ASSERT.that(!prov.equals(provider), "duplicate object instances for prov=" + prov + ", this=" + this);
                    if (prov instanceof Category && ((Category) prov).isOwnerOfItemInCategoryBeforeItem(this)) {
                        return 0;
                    }
                    WorkTime wt = prov.getWorkTimeAllocator(false).getAllocatedWorkTime(this, required);
//                    if (wt != null) {
                    required = wt.getRemainingDuration(); //required = wt != null ? wt.getRemainingDuration() : required; //set remaining to any duration that could not be allocated by this provider
//                    }
                }
                assert required >= 0;
                if (required == 0) { //other higher prio providers allocated all required worktime
                    return 0;//required; //return here, don't go through other providers
                }
            }
        }
        return required;
    }

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
//            List<ItemAndListCommonInterface> workTimeProviderList = getWorkTimeProvidersInPrioOrder(false);
//            if (workTimeProviderList != null) {
////                Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
//                for (ItemAndListCommonInterface prov : getWorkTimeProvidersInPrioOrder(false)) {
////                while (workTimeProviders.hasNext()) {
//                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
////                    ItemAndListCommonInterface prov = workTimeProviders.next();
////                if (prov == provider) {
////                    return remaining;
////                } else {
////                    remaining -= prov.getAllocatedWorkTime(this).getAllocatedDuration(); //deduct time allocated by higher prioritized provider
//                    remaining = prov.getAllocatedWorkTime(this, remaining).getRemainingDuration(); //set remaining to any duration that could not be allocated by this provider
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
////            List<ItemAndListCommonInterface> workTimeProviderList = getWorkTimeProvidersInPrioOrder();
////            Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
//            for (ItemAndListCommonInterface prov : getWorkTimeProvidersInPrioOrder()) {
////            while (workTimeProviders.hasNext()) {
//                //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//                //                ItemAndListCommonInterface prov = workTimeProviders.next();
//                if (prov == provider) {
//                    return remaining;
//                } else {
//                    remaining -= prov.getAllocatedWorkTime(this).getAllocatedDuration(); //deduct time allocated by higher prioritized provider
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
    @Override
    public WorkTime getAllocatedWorkTime() {
        return getAllocatedWorkTime(false);
    }

    public WorkTime getAllocatedWorkTimeOLD() {
//        boolean reset = false;
//        return getWorkTimeAllocator().getAllocatedWorkTime(this);
//        ItemAndListCommonInterface owner = getOwner();
//        long remaining = getRemainingEffort();

        WorkTime workTime = null;
        WorkTime newWorkTime = null;
        ItemAndListCommonInterface provider;

//        long requiredWT = getWorkTimeRequiredFromOwner(this); //only get the workTime required from this provider (e.g. subtasks with own categories may have been served directly at their level)
        long requiredWT = getWorkTimeRequiredFromProvider(this); //only get the workTime required from this provider (e.g. subtasks with own categories may have been served directly at their level)

        List<ItemAndListCommonInterface> workTimeProviderList = getWorkTimeProvidersInPrioOrder(); //only allocate from own workSlots or categories to ripple up what owner must allocate
        if (workTimeProviderList != null) {
            Iterator<ItemAndListCommonInterface> workTimeProviders = workTimeProviderList.iterator();
            while (requiredWT > 0 && workTimeProviders.hasNext()) {
                //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
                provider = workTimeProviders.next();
//            newWorkTime = provider.getAllocatedWorkTime(this);
//            newWorkTime = provider.getAllocatedWorkTime(this);
                newWorkTime = provider.allocateWorkTime(this, requiredWT);
                if (workTime == null) {
                    workTime = newWorkTime;
                } else {
                    workTime.addWorkTime(newWorkTime);
                }
                requiredWT = newWorkTime.getRemainingDuration();
            }
        }
        return workTime;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkTime getAllocatedWorkTimeOLD() {
//        boolean reset = false;
////        return getWorkTimeAllocator().getAllocatedWorkTime(this);
//        WorkTime workTime = null;
//        ItemAndListCommonInterface owner = getOwner();
////        long remaining = getRemainingEffort();
//        long remaining = -9999;//getWorkTimeRequiredFromThisProvider();
//
//        if (owner != null && remaining > 0 && !MyPrefs.workTimePrioritizeWorkTimeInCategoriesOverOwnerWorkTime.getBoolean()) {
//            if (owner != null) {
////                WorkTime ownerWT = owner.getAvailableWorkTime();
//                WorkTimeDefinition ownerWTD = owner.getWorkTimeAllocator(reset);
//                if (ownerWTD != null) {
//                    workTime = ownerWTD.getAllocatedWorkTime(this, remaining);
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
//                WorkTimeDefinition catWTD = cat.getWorkTimeAllocator(reset);
//                if (catWTD != null) {
//                    WorkTime newWorkTime = catWTD.getAllocatedWorkTime(this, remaining);
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
//            WorkTimeDefinition ownerWTD = owner.getWorkTimeAllocator(reset);
//            if (ownerWTD != null) {
//                WorkTime newWorkTime = ownerWTD.getAllocatedWorkTime(this, remaining);
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
    public boolean hasWorkTime() {
        if (ItemAndListCommonInterface.super.hasWorkTime()) {
            return true;
        }
//        WorkSlotList workSlots = getWorkSlotList();
//        if (workSlots != null && workSlots.size() > 0) {
//            return true;
//        }
//        ItemAndListCommonInterface owner = getOwner();
//        if (owner != null && owner.hasWorkTime()) {
//            return true;
//        }
        for (Category cat : getCategories()) {
            if (cat.hasWorkTime()) {
                return true;
            }
        }
        return false;
//        return getWorkTimeProvidersInPrioOrder() != null; //TODO optimization - is there a more efficient way?
    }

    /**
     * returns workTime for the item at index itemIndex and for an effort of
     * remainingTime
     *
     * @param itemIndex
     * @param desiredDuration
     * @return null if no workTime
     */
    public WorkTime getAllocatedWorkTime(boolean reset) {
        boolean noCache = true;
        if (noCache || workTime == null || reset) { //true: don't cache values in Item, only cache WorkTimeAllocator
//            return workTime;
//        } else {

            //Get remaining time
            long remaining = 0;
            List<? extends ItemAndListCommonInterface> subtasks = getList();
            if (subtasks != null && subtasks.size() > 0) { //I'm a project
                for (ItemAndListCommonInterface elt : subtasks) {
//                remaining += elt.getAllocatedWorkTime().getRemainingDuration(); //remaining of last subtask will hold any unallocated required worktime
                    remaining += elt.getWorkTimeRequiredFromProvider(this); //remaining of last subtask will hold any unallocated required worktime
                }
            } else { //I'm a leaf task
                remaining = getRemainingEffort(); //how much total workTime is required?
            }

            List<ItemAndListCommonInterface> providers = getWorkTimeProvidersInPrioOrder();
            if (providers != null) {
                for (ItemAndListCommonInterface prov : providers) {
                    //process workTimeProviders in priority order to allocate as much time as possible from higher prioritized provider
//                if (prov != owner) {
                    WorkTimeAllocator workTimeAllocator = prov.getWorkTimeAllocator(reset);
//                    if (true ||wtd != null) {
//                        ASSERT.that(wtd!=null,"WTD should never ne null for a workTimeProvider");
//                    WorkTime wt = prov.getWorkTimeAllocator(reset).getAllocatedWorkTime(this, remaining);
                    if (workTimeAllocator != null) {
                        WorkTime wt = workTimeAllocator.getAllocatedWorkTime(this, remaining);
//                        remaining = wt != null ? wt.getRemainingDuration() : remaining; //set remaining to any duration that could not be allocated by this provider
                        if (workTime == null || noCache) {
                            workTime = new WorkTime();
                        }
                        if (wt != null) {
                            workTime.addWorkTime(wt);
                        }
                        remaining = wt != null ? wt.getRemainingDuration() : remaining; //set remaining to any duration that could not be allocated by this provider
                        if (wt != null && remaining == 0) {
                            break;
                        }
                    }
                }
            }
        }
        return workTime;
    }

    /**
     * set workTime, especially reset to null to force recalculation.
     *
     * @param workTime
     */
    public void setAllocatedWorkTime(WorkTime workTime) {
        this.workTime = workTime;
    }

    @Override
    public long getFinishTime() {
        long latestFinishTime = MyDate.MIN_DATE;
        if (isProject()) { //UI: for projects, finishTime is ALWAYS latest finishTime for subtasks (or undefined if all subtasks are Done)
            for (Object subtask : getList()) {
                if (subtask instanceof Item) { //AndListCommonInterface) {
                    Item item = (Item) subtask;
                    long finishT = item.getFinishTime();
                    if (finishT > latestFinishTime && !item.isDone()) {
                        latestFinishTime = finishT;
                    }
                }
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
    public Date getLastModifiedDateSubtasks() {
        long lastSubtaskUpdate = getLastModifiedDate(); //if ever project task itself is updated the last, return that date. 
        long temp;
        if (isProject()) {
            for (Object o : getList()) {
                if (o instanceof Item) {
                    temp = ((Item) o).getLastModifiedDateSubtasks().getTime(); //cache since potentially heavy operation
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
                newItem.setChallenge(itemBefore.getChallenge()); //UI: same prio as item just before
                break;
            case Item.PARSE_PRIORITY:
//            int diffPrio = itemAfter.getPriority() -itemBefore.getPriority() ;
//            newItem.setPriority(itemBefore.getPriority()+diffPrio/2);
                newItem.setPriority(itemBefore.getPriority()); //UI: same prio as item just before
                break;
            case Item.PARSE_DREAD_FUN_VALUE:
                newItem.setDreadFunValue(itemBefore.getDreadFunValue()); //UI: same prio as item just before
                break;
            case Item.PARSE_IMPORTANCE:
                newItem.setImportance(itemBefore.getImportance()); //UI: same prio as item just before
                break;
            case Item.PARSE_URGENCY:
                newItem.setUrgency(itemBefore.getUrgency()); //UI: same prio as item just before
                break;
            case Item.PARSE_EARNED_VALUE:
                newItem.setEarnedValue(itemBefore.getEarnedValue()); //UI: same prio as item just before
                break;
            case Item.PARSE_EFFORT_ESTIMATE:
                newItem.setEffortEstimate(itemBefore.getEffortEstimate()); //UI: same prio as item just before
                break;
            case Item.PARSE_ACTUAL_EFFORT:
                newItem.setActualEffort(itemBefore.getActualEffort()); //UI: same prio as item just before
                break;
            case Item.PARSE_REMAINING_EFFORT:
                newItem.setRemainingEffort(itemBefore.getRemainingEffort()); //UI: same prio as item just before
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Date getFinishTime(ItemAndListCommonInterface subtask) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocator();
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
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocator();
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
