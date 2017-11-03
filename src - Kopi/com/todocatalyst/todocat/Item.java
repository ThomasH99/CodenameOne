package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.codename1.ui.tree.TreeModel;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
//import com.todocatalyst.todocat.AlarmServer.AlarmObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import sun.security.acl.OwnerImpl;
//import todo.TodoMidlet43.Categories;

/**
 *
 * @author Thomas
 */
public class Item /* extends BaseItemOrList /* */
        extends ParseObject
        implements
        //          ItemListChangeListener,
        TreeModel, ItemAndListCommonInterface, IComparable,
        //        FilterableObject, 
        RepeatRuleObjectInterface, SumField { //AlarmObject,  {

    public static String CLASS_NAME = "Item";

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

    @Override
    public int getRemainingTime() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getItemIdStr() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString(ToStringFormat format) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//, ExpandableInterface {

    @Override
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void copyMeInto(ItemAndListCommonInterface destiny) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getChildren(Object parent) {
        return new Vector(getItemList());
    }

    @Override
    public boolean isLeaf(Object node) {
        return getItemList() == null || getItemList().size() == 0;
    }

    public enum ItemStatus {
        //internationalize: http://programmers.stackexchange.com/questions/256806/best-approach-for-multilingual-java-enum
        STATUS_CREATED("Created"),
        /**
         * work has started / is currently being worked on
         */
        STATUS_ONGOING("In progress"),
        /**
         * work has started, but has been put on hold or is waiting for
         * something from the outside (remaining can't do work on it until some
         * external event or input happens)
         */
        STATUS_WAITING("Waiting"),
        STATUS_DONE("Done"),
        STATUS_CANCELLED("Cancelled");

        ItemStatus(String description) {
            this.description = description;
        }

        String getDescription() {
            return description;
        }

        static String[] getDescriptionList() {
            return new String[]{
                STATUS_CREATED.getDescription(),
                STATUS_ONGOING.getDescription(),
                STATUS_WAITING.getDescription(),
                STATUS_DONE.getDescription(),
                STATUS_CANCELLED.getDescription()};
        }

        static int[] getDescriptionValues() {
            return new int[]{
                STATUS_CREATED.ordinal(),
                STATUS_ONGOING.ordinal(),
                STATUS_WAITING.ordinal(),
                STATUS_DONE.ordinal(),
                STATUS_CANCELLED.ordinal()};
        }

        private final String description;
    }

    public static String getStatusName(ItemStatus status) {
//        return STATUS_NAMES[status];
        return status.getDescription();
    }

    public static String[] getStatusNames() {
//        return STATUS_NAMES;
        return ItemStatus.getDescriptionList();
    }

    public static int[] getStatusValues() {
//        return STATUS_VALUES;
        return ItemStatus.getDescriptionValues();
    }

    /**
     * the object that contains, or 'owns', this object. An object can have no
     * more than one owner. When the owner is deleted, so is the object.
     */
    private ItemAndListCommonInterface owner;
//    private String description; //STORED

//    private String comment = "";
//    private String description = "";
    //private ItemList subitems = new ItemList();
    private ItemList<Item> subitems; // = new ItemListAutosaveParent(this); //optimization: avoid creating this list until/unless it is actually needed 8save memory)
    //private ItemList categories = new ItemList(); //Categories.getInstance().someValues();
//    private CategoryListConsistency categories = new CategoryListConsistency(); //Categories.getInstance().someValues();
//    private ItemToCategoryConsistency categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
    //private ItemList categories2 = new CategoryListConsistency(); //Categories.getInstance().someValues();
//    private ItemListSaveInline categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
//    private ItemListCategoryConsistency categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
    private ItemList categories; // = new ItemCategoryConsistency(this); //Categories.getInstance().someValues();  //optimization: avoid creating this list until/unless it is actually needed 8save memory)
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

    private BufferedValue derivedRemainingEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
        public Object getValue() {
            long subItemSum = 0;
//            for (int item: getItemList()) {
            for (int i = 0, size = getItemListSize(); i < size; i++) {
//                Item item = ((Item) getItemList().getItemAt(i));
                Item item = (Item) getItemList().getItemAt(i);
                if (!item.isDone()) { // /** || includeDone */) {
                    subItemSum += item.getRemainingEffort();
                }
            }
            return new Long(subItemSum);
        }
    });
    private BufferedValue derivedEstimateEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
        public Object getValue() {
            long subItemSum = 0;
            for (int i = 0, size = getItemListSize(); i < size; i++) {
//                Item item = ((Item) getItemList().getItemAt(i));
                Item item = (Item) getItemList().getItemAt(i);
                if (!item.isDone()) { // /** || includeDone */) {
                    subItemSum += item.getEffortEstimate();
                }
            }
            return new Long(subItemSum);
        }
    });
    private BufferedValue derivedActualEffortSubItemsSumBuffered = new BufferedValue(new UpdaterInterface() {
        public Object getValue() {
            long subItemSum = 0;
            for (int i = 0, size = getItemListSize(); i < size; i++) {
//                Item item = ((Item) getItemList().getItemAt(i));
                Item item = (Item) getItemList().getItemAt(i);
                if (!item.isDone() /*
                         * || includeDone
                         */) {
                    subItemSum += item.getActualEffort();
                }
            }
            return new Long(subItemSum);
        }
    });
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
    final static int COMPARE_SHOWFROM_DATE = 6;
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
    private final static FieldDef[] FIELDS = {
        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_STRING, "Help text for Description"),
        //        new FieldDef(FIELD_DONE, "Done", Expr.VALUE_FIELD_TYPE_STRING),
        new FieldDef(FIELD_DONE, "Done", Expr.VALUE_FIELD_TYPE_BOOLEAN),
        new FieldDef(FIELD_DUE_DATE, "Due", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_MODIFIED_DATE, "Modified", Expr.VALUE_FIELD_TYPE_DATE, "Date when this task was last modified"),
        new FieldDef(FIELD_COMPLETED_DATE, "Completed", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_CREATED_DATE, "Created", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_COMMENT, "Comment", Expr.VALUE_FIELD_TYPE_STRING),
        new FieldDef(FIELD_EFFORT_ESTIMATE, "Effort estimate", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_EFFORT_ACTUAL, "Actual effort", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_EFFORT_REMAINING, "Remaining effort", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_STATUS, "Task status", Expr.VALUE_FIELD_TYPE_ENUM),
        new FieldDef(FIELD_PRIORITY, "Priority", Expr.VALUE_FIELD_TYPE_INTEGER),
        new FieldDef(FIELD_EARNED_POINTS, "Value", Expr.VALUE_FIELD_TYPE_INTEGER),
        new FieldDef(FIELD_EARNED_POINTS_PER_HOUR, "Value/Effort", Expr.VALUE_FIELD_TYPE_INTEGER, "Set Work time to calculate Value per hour of effort"),
        new FieldDef(FIELD_ALARM_DATE, "Alarm date", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_WAIT_DATE, "Waiting until", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_WAITING_ALARM_DATE, "Wait until date", Expr.VALUE_FIELD_TYPE_DATE), //"Waiting reminder", "Wait until date"
        new FieldDef(FIELD_STARTED_ON_DATE, "Started", Expr.VALUE_FIELD_TYPE_DATE),
        //        new FieldDef(FIELD_ROI, "ROI", Expr.VALUE_FIELD_TYPE_INTEGER), //- double definition of FIELD_EARNED_POINTS_PER_HOUR
        new FieldDef(FIELD_START_WORK_TIME, "Start work", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_FINISH_WORK_TIME, "Finish work", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_TIMESPAN, "Timespan", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_EFFORT_TOTAL, "Total effort", Expr.VALUE_FIELD_TYPE_DURATION),
        new FieldDef(FIELD_CATEGORIES, "Categories", Expr.VALUE_FIELD_TYPE_STRING),//TODO!!!!! should categories appear here??
        new FieldDef(FIELD_INTERRUPT, "Interrupt task", Expr.VALUE_FIELD_TYPE_BOOLEAN),
        //        new FieldDef(FIELD_PRIORITY_IMPORTANCE, "Importance", Expr.VALUE_FIELD_TYPE_INTEGER),
        new FieldDef(FIELD_PRIORITY_IMPORTANCE, "Importance", Expr.VALUE_FIELD_TYPE_ENUM),
        //        new FieldDef(FIELD_PRIORITY_URGENCY, "Urgency", Expr.VALUE_FIELD_TYPE_INTEGER),
        new FieldDef(FIELD_PRIORITY_URGENCY, "Urgency", Expr.VALUE_FIELD_TYPE_ENUM),
        new FieldDef(FIELD_OWNER, "Owner", Expr.VALUE_FIELD_TYPE_STRING), //TODO!!!!: what field type to use for Owner? String to return the owner's name
        new FieldDef(FIELD_REPEAT_RULE, "Repeat", Expr.VALUE_FIELD_TYPE_STRING), //TODO: what field type to use for RepeatRule?
        new FieldDef(FIELD_EXPIRES_ON_DATE, "Expires", Expr.VALUE_FIELD_TYPE_DATE),
        new FieldDef(FIELD_START_BY_TIME, "Start", Expr.VALUE_FIELD_TYPE_DATE),
        //        new FieldDef(FIELD_DREAD_FUN, "Fun", Expr.VALUE_FIELD_TYPE_INTEGER),
        new FieldDef(FIELD_DREAD_FUN, "Fun", Expr.VALUE_FIELD_TYPE_ENUM),
        //        new FieldDef(FIELD_CHALLENGE, "Difficulty", Expr.VALUE_FIELD_TYPE_INTEGER),};
        new FieldDef(FIELD_CHALLENGE, "Difficulty", Expr.VALUE_FIELD_TYPE_ENUM),};

    final static int ITEM_CHANGED_ALARM_DATE = 0;

    final static String PARSE_TEXT = "description";
    final static String PARSE_COMMENT = "comment";
    final static String PARSE_SUBTASKS = "subtasks";
    final static String PARSE_DREAD_FUN_VALUE = "dreadFunValue";
    final static String PARSE_CHALLENGE = "challenge";
    final static String PARSE_EXPIRES_ON_DATE = "expiresOnDate";
    final static String PARSE_INTERRUPT_TASK = "interuptTask";
    final static String PARSE_ALARM_DATE = "alarmDate";
    final static String PARSE_WAITING_ALARM_DATE = "waitingAlarmDate";
    final static String PARSE_REPEAT_RULE = "repeatRule";
    final static String PARSE_STARTED_ON_DATE = "startedOnDate";
    final static String PARSE_STATUS = "status";
    final static String PARSE_DUE_DATE = "dueDate";
    final static String PARSE_START_BY_DATE = "startByDate";
    final static String PARSE_WAITING_TILL_DATE = "waitingTillDate";
    final static String PARSE_WAITING_LAST_ACTIVATED_DATE = "waitingLastActivatedDate";
    final static String PARSE_EFFORT_ESTIMATE = "effortEstimate";
    final static String PARSE_REMAINING_EFFORT = "remainingEffort";
    final static String PARSE_ACTUAL_EFFORT = "actualEffort";
    final static String PARSE_SHOW_FROM_DATE = "showFromDate";
    final static String PARSE_CATEGORIES = "categories";
    final static String PARSE_PRIORITY = "priority";
    final static String PARSE_EARNED_VALUE = "earnedValue";
    final static String PARSE_COMPLETED_DATE = "completedDate";
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
            case FIELD_START_WORK_TIME:
                return getStartTime();
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
                setCategories((Set) fieldValue); //optimization: replace by variable description directly
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
                setDreadFunValue(((Integer) fieldValue));
                break;
            case FIELD_CHALLENGE:
                setChallenge(((Integer) fieldValue));
                break;
            default:
                ASSERT.that("Item.setFilterField: Field Identifier not defined " + fieldId);
        }
    }

    public boolean isDone() {
        return getStatus() == ItemStatus.STATUS_DONE || getStatus() == ItemStatus.STATUS_CANCELLED;  //CANCELLED since this makes a Cancelled task flip back to Created when clicked in a list
    }

    @Override
    public boolean isNoLongerRelevant() {
        return isDone();
    }

    /**
     * Owner of an item can be either a list or a project (an item in a task's
     * sublist).
     *
     * @return
     */
    @Override
    final public ItemAndListCommonInterface getOwner() {
        return owner; //TODO: Parsify
    }

    @Override
    final public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner) {
//        setOwnerDirectly(owner);
        ItemAndListCommonInterface oldOwner = getOwner();
        this.owner = owner; //TODO: Parsify
        return oldOwner;
//        }
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
        if (s == null) {
            return "";
        } else {
            return s;
        }
//        return (description != null ? description : "");
    }

    /**
     * sets the key text string for the subtypes of BaseItem, e.g. for Item
     * Description, for Category categoryName, ... setText() in BaseItem is not
     * supposed to be used directly but must be overwritten by subtypes.
     *
     * @return
     */
    @Override
    public void setText(String text) {
        if ((has(PARSE_TEXT) || !text.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_TEXT, text);
        }
//        if (text != null && !text.equals(description)) {
//            this.description = text;
//        }
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
    public void setComment(String val) {
//        if (val != null && (has("comment") || !val.equals(""))) {
        if ((has(PARSE_COMMENT) || !val.equals(""))) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_COMMENT, val);
        }
//        if (!this.comment.equals(val)) {
//            this.comment = val;
//        }
    }

    @Override
    public boolean isExpandable() {
        return getItemListSize() > 0;
    }

    public enum CopyOption {
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
     * @param destiny
     * @inherit
     */
    public void copyMeInto(Item destiny) {
        copyMeInto(destiny, CopyOption.COPY_ALL_FIELDS);
    }

    void copyMeInto(Item destiny, CopyOption copyFieldDefintion) {

        Item destination = (Item) destiny;
        /**
         * copy for all types of copies
         */
        boolean all = (copyFieldDefintion == CopyOption.COPY_ALL_FIELDS);
        /**
         * copy for Repeat Instances
         */
        boolean repInst = (copyFieldDefintion == CopyOption.COPY_TO_REPEAT_INSTANCE);
        /**
         * copy for Templates
         */
        boolean templ = (copyFieldDefintion == CopyOption.COPY_TO_TEMPLATE);
        /**
         * copy for Templates
         */
        boolean fromTempl = (copyFieldDefintion == CopyOption.COPY_FROM_TEMPLATE);
        /**
         * copy for Copy/Paste copies (same as for templates??)
         */
        boolean copyPaste = (copyFieldDefintion == CopyOption.COPY_TO_COPY_PASTE);

//        super.copyMeInto(destination, !fromTempl || (fromTempl && destination.getText().equals(""))); //don't overwrite destination with template dreadFunNames, unless destination dreadFunNames is empty
//        destiny.storedFormatVersion = storedFormatVersion;
//        destiny.setTypeId(getTypeId());
//        if (copyText) {
//            destiny.setText(new String(getText())); //make a copy of text string (to be able to edit new string separately)
//        }
        //NB! when copying categories, the new list should refer to the categoreis themselves,
        //NOT copies of them,
//        item.setCategories((ItemList) (getCategories().clone()));
        if (all || repInst || templ || copyPaste) {
//            getCategories().copyMeInto((ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
//            destination.setCategories(new ArrayList(getCategories()));//(ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
            destination.setCategories(new HashSet(getCategories()));//(ItemList) destination.getCategories()); //NB: typecast to (ItemList) is needed to make the result of getCategories() a supertype of BaseItem and not interface ItemListModel (which is not a supertype of BaseItem)
            if (getItemListSize() > 0) {
//                destination.setItemList((ItemList) getItemList().cloneMe());
                destination.setItemList(new ArrayList(getItemList().getInternalList()));
            }
            destination.setEffortEstimate(getEffortEstimate());
            destination.setComment(getComment());
            destination.setPriority(getPriority());
        } else if (fromTempl) {
            if (getCategoriesSize() > 0) {
                //if copying *from* a template into an item, *add* the template's categories (if any) instead of overwriting, to ensure already set categories are not lost
                destination.getCategories().addAll(getCategories()); //just add the same categories
            }
            if (getItemListSize() > 0) {
                destination.getItemList().addAllItems(getItemList().cloneMe()); //clone the list AND close the subtasks
            }
            if (destination.getEffortEstimate() == 0) { //copy estimate from template, iff nothing's already set for item
                destination.setEffortEstimate(getEffortEstimate());
            }
            destination.addToComment(destination.getComment(), true); //UI: add template's comment to the end(?!) of the comment, with a newline
            if (destination.getPriority() == Settings.getInstance().getDefaultPriority()) { //only change priority to template's value if the item has default value (assuming no value has been set - TODO!!!! what if the user wanted the default value??)
                destination.setPriority(getPriority());
//                destination.priority = (Priority)priority.clone();
            }
        }

        //optimization: bundle all 'all' copies together in a single if statement
        if (all) {
            destination.setStatus(getStatus());
            destination.setStartedOnDate(getStartedOnDate());
            destination.setCompletedDate(getCompletedDate());
//            destination.setCreatedDate(getCreatedDate());
            destination.setWaitingTillDate(getWaitingTillDate());
            destination.setWaitingLastActivatedDate(getWaitingLastActivatedDate());
            destination.setRemainingEffort(getRemainingEffort());
            destination.setActualEffort(getActualEffort());
//            destination.setLastModifiedDate(getLastModifiedDate());
            destination.setEarnedValue(getEarnedValue());
            destination.setInteruptTask(isInteruptTask());
        }
        if (all || copyPaste) {
            destination.setDueDate(getDueDate());
            destination.setStartByDate(getStartByDate());
            destination.setShowFromDate(getShowFromDate());
            destination.setAlarmDate(getAlarmDate());
            destination.setExpiresOnDate(getExpiresOnDate());
        }

        if (all || fromTempl) { //- UI: do NOT copy RepeatRules
            if ((RepeatRuleParseObject) getRepeatRule() != null) {
                destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //create a new repeat rule
//                destination.getRepeatRule().generateRepeatInstances(destination, destination.getOwnerItemList()); //- this should be done at commit() of the copy, not when it's generated, in this way the list for the copies is also known
            }
        } else if (repInst) {
            destination.setRepeatRuleNoUpdate(getRepeatRule()); //point to existing repeat rule
        } else if (templ) {
            if (getRepeatRule() != null) {
                destination.setRepeatRule((RepeatRuleParseObject) getRepeatRule().cloneMe()); //for templates, make a copy of the RepeatRule, but do NOT create repeat instances
            }
        }
    }

    /**
     * @inherit
     */
    @Override
    public Item cloneMe() {
        Item newCopy = new Item();
        copyMeInto(newCopy);
        return newCopy;
    }

    public Item cloneMe(CopyOption copyFieldDefintion) {
        Item newCopy = new Item();
        copyMeInto(newCopy, copyFieldDefintion);
        return newCopy;
    }

    /**
     * updates this item's Due/Alarm/ShowFrom dates etc. based on the
     * referenceItem's defined dates + deltaTime (deltaTime= how much later is
     * the referenceTime than the referenceItem's dueDate)
     */
    private void updateDatesFromReference(Item referenceItem, long dueDateTime) {
        long deltaTime = dueDateTime - referenceItem.getDueDate(); //how much later is the referenceTime than the referenceItem's dueDate?
        if (referenceItem.getDueDate() != 0) { //only update if a value is already defined (also for due date since it may not be set, eg for repeat on completed)
//            setDueDate(referenceItem.getDueDate() + deltaTime);
            setDueDate(dueDateTime);
        }
        //TODO!!!!: what if no DueDate is set, but only an alarmDate or showFromDate? Can this happen (what would the repeatReference date then be?)
        if (referenceItem.getShowFromDate() != 0) { //only update if a value was defined for the referenceItem
            setShowFromDate(referenceItem.getShowFromDate() + deltaTime);
        }
        if (referenceItem.getAlarmDate() != 0) { //only update if a value was defined for the referenceItem
            setAlarmDate(referenceItem.getAlarmDate() + deltaTime);
        }
    }

    /**
     * create a copy of this item, to be used as a repeatRule instance
     */
    @Override
    public RepeatRuleObjectInterface createRepeatCopy(long referenceTime) {
//        Item newRepeatCopy = new Item();
//        copyMeInto(newRepeatCopy, COPY_TO_REPEAT_INSTANCE);
        Item newRepeatCopy = (Item) this.cloneMe(CopyOption.COPY_TO_REPEAT_INSTANCE);
//        newRepeatCopy.updateDatesFromReference(this, referenceTime - getDueDate()); //the delta time to add to copies are the new due time - the due date of the reference item
        newRepeatCopy.updateDatesFromReference(this, referenceTime); //the delta time to add to copies are the new due time - the due date of the reference item
        return newRepeatCopy;
    }

    @Override
    public void setRepeatStartTime(long repeatStartTime) {
        setDueDate(repeatStartTime);
    }

    @Override
    public long getRepeatStartTime(boolean fromCompletedDate) {
        if (fromCompletedDate) {
            return getCompletedDate();
        } else {
            return getDueDate();
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
                case Item.COMPARE_SHOWFROM_DATE:
                    return compareLong(getShowFromDate(), c.getShowFromDate());
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

//    public String typeString() {
//        return BaseItemTypes.toString(typeId);
//    }
    public String fullString() {
//        return typeString() + " \"" + description + "\""
        return //typeString() + " \"" + getText() + "\"" +
                //                + (completed ? "[V]" : "[ ]")
                //                + " | completed = " + completed
                //                + "|subItems=" + (subitems == null ? "null" : /*("Gui" + subitems.getGuid() +*/ (getItemListSize() == 0 ? "[<empty>]" : getItemList().toString()))
                "|subItems=" + (subitems == null ? "[null]" : /*
                 * ("Gui" + subitems.getGuid() +
                         */ (getItemListSize() == 0 ? "[<empty>]" : getItemList().shortString()))
                //                + "|categories=" + (categories == null ? "null" : /*("Gui" + categories.getGuid() +*/ (getCategoriesSize() == 0 ? "[<empty>]" : getCategories().toString()))
                + "|categories=" + (categories == null ? "[null]" : /*
                 * ("Gui" + categories.getGuid() +
                         */ (getCategoriesSize() == 0 ? "[<empty>]" : getCategories().toString())) //.shortString()))
                //                + "|categories=" + (categories != null ? "Gui" + categories.getGuid() : "null") + ((getCategoriesSize() == 0) ? "[<empty>]" : getCategories().toString())
                + "|" //+ super.fullString()
                //                + " | completedDate = " + completedDate
                //                + "|createdDate=" + createdDate //                + " | dueDate = " + dueDate
                //                + " | showFromDate = " + showFromDate
                //                + " | effortEstimate = " + effortEstimate
                //                + " | actualEffort = " + actualEffort
                //                + " | lastModDate = " + lastModDate
                //                + " | comment = " + comment
                //                + " | priority = " + priority;
                ;
    }

    @Override
    public String shortString() {
//        return "typeId=" + BaseItemTypes.toString(typeId)+ "|guid=" + guid;
//        return BaseItemTypes.toString(getTypeId()) + (getText().length() != 0 ? "\"" + getText() + "\"" : "Gui" + getGuid());
        return ""; //BaseItemTypes.toString(getTypeId()) + "\"" + getText() + "\"";
    }

    /**
     * delete the item, as well as sub-tasks. Remove it from categories and
     * alarm server. Stop timer if running. For items with repeatrules, user
     * must decide if only to delete this instance or all instances.
     *
     */
    @Override
    public void delete() {
        //first delete from Timer if running
//        TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: make TimerScreen a listener on the timed item to be notified if its deleted
        //delete all subtasks (since they are owned by this item)
        for (Object item : getItemList()) {
            ((Item) item).delete();
        }
        //TODO: (?)anything to do to handle case where subtasks are created and saved, but where the new mother task is finally not saved?
        //remove item from all categories before deleting it
        for (Object cat : getCategories()) {
            ((Category) cat).removeItem(this); //remove references to this item from the category before deleting it
        }
        //handle repeatrule
        RepeatRuleParseObject myRepeatRule = getRepeatRule();
        if (myRepeatRule != null) {
            myRepeatRule.deleteAskIfDeleteRuleAndAllOtherInstancesExceptThis(this); //if we 
        }
        // remove any active alarms
//        AlarmServer.getInstance().removeItem(this);
        try {
            //finally delete the item itself
            super.delete();
        } catch (ParseException ex) {
//            Log.getLogger(Item.class.getName()).log(Level.SEVERE, null, ex); //TODO!!!!!!
            Log.p(Item.class.getName() + "SEVERE " + ex); //TODO!!!!!!
        }
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
        return (getItemList().size());
//        return (subitems == null || subitems.getSize() == 0) ? 0 : subitems.getSize();
    }

    /**
     * returns list of subitems/sub-tasks. If no list previously existed, the
     * list is created. Use getItemListSize() to check if a list already exists
     * to avoid creating unnecessary sublists.
     *
     * @return (never null)
     */
    public ItemList getItemList() {
        List subitemslist = getList(PARSE_SUBTASKS);
        if (subitemslist == null) {
//            new LinkedList<Item>();
            subitems = new ItemList();
        } else {
            subitems = new ItemList(subitemslist);
        }
//        if (subitems == null) {
//            subitems = new ItemList(BaseItemTypes.ITEM, this, false, isEnsureItemCategoryAutoConsistency(), true, true); //ItemListSaveInline(this); //addMultipleInstances=false(even though it gives an expensive test on each insertion it is safer so no insertLink can make a subtask appear several times)
//        }
        return subitems;
    }

    public void setItemList(ItemList itemList) {
        setItemList(itemList.getInternalList());
    }
    /**
     * sets this Item's list of subitems to be the same as itemList. This is
     * done by comparing the two lists and adding new items in itemList to the
     * subitem list, or by removing the items not in itemList from the subitem
     * list. It is done in this way to avoid creating unnecessary change events
     * from adding/removing items which are already in/not in the subitem list.
     *
     * @param itemList
     */
//    public void setItemList(ItemList itemList) {
    public void setItemList(List itemList) {
        if (has(PARSE_SUBTASKS) || (itemList != null && !itemList.isEmpty())) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
        {
            put(PARSE_SUBTASKS, itemList);
        }
//        if (getItemListSize() == 0 && itemList.isEmpty()) // this test is also done in updateListWithDifferences,but here it uses getItemListSize() to avoid creating a new list
//        {
//            return; //do  nothing if both lists are empty
//        }
//        //TODO!! risk that this call will create unnecessary empoty sublists - replace an empty list with a locally (in this method) created empty list instead
////        ListCompared.updateListWithDifferences(getItemList(), itemList);
//        getItemList().updateListWithDifferences(itemList);
    }

//    private final static String[] dreadFunNames = new String[]{"Love it", "Neither", "Hate it"};
    private final static String[] dreadFunNames = new String[]{"Fun", "Neutral", "Dread"};
    private final static int[] dreadFunValues = new int[]{0, 1, 2};

    public static String[] getDreadFunStringArray() {
        return dreadFunNames;
    }

    public static int[] getDreadFunValuesArray() {
        return dreadFunValues;
    }

    public int getDreadFunValue() {
        Integer dreadFunValue = getInt(PARSE_DREAD_FUN_VALUE);
        return (dreadFunValue == null) ? 0 : dreadFunValue;
//        return dreadFunValue;
    }

    public void setDreadFunValue(int dreadFunValue) {
        if (has(PARSE_DREAD_FUN_VALUE) || dreadFunValue != 0) {
            put(PARSE_DREAD_FUN_VALUE, dreadFunValue);
        }
//        if (dreadFunValue != this.dreadFunValue) {
//            this.dreadFunValue = dreadFunValue;
//        }
    }
    private final static String[] challengeNames = new String[]{"Easy", "Med", "Difficult"};
    private final static int[] challengeValues = new int[]{0, 1, 2};

    public static String[] getChallengeStringArray() {
        return challengeNames;
    }

    public static int[] getChallengeValuesArray() {
        return challengeValues;
    }

    public int getChallenge() {
        Integer challenge = getInt(PARSE_CHALLENGE);
        return (challenge == null) ? 0 : challenge;
//        return challenge;
    }

    public void setChallenge(int challenge) {
        if (has(PARSE_CHALLENGE) || challenge != 0) { //no need to save a value since a null pointer is interprested as zero
            put(PARSE_CHALLENGE, challenge);
        }
    }

    public long getExpiresOnDate() {
        Date date = getDate(PARSE_EXPIRES_ON_DATE);
        return (date == null) ? 0L : date.getTime();
//        return expiresOnDate;
    }

    public void setExpiresOnDate(long expiresOnDate) {
        if (has(PARSE_EXPIRES_ON_DATE) || expiresOnDate != 0) {
            put(PARSE_EXPIRES_ON_DATE, new Date(expiresOnDate));
        }
    }

    public boolean isInteruptTask() {
        Boolean interruptTask = getBoolean(PARSE_INTERRUPT_TASK);
        return (interruptTask == null) ? false : interruptTask;
//        return interuptTask;
    }

    public void setInteruptTask(boolean interuptTask) {
        if (has(PARSE_INTERRUPT_TASK) || !interuptTask) {
            put(PARSE_INTERRUPT_TASK, interuptTask); //only store true values (null corresponds to False)
        }
    }

    public Date getAlarmDateD() {
        Date date = getDate(PARSE_ALARM_DATE);
        return (date == null) ? new Date(0) : date;
//        return alarmDate;
    }

    public void setAlarmDateD(Date alarmDate) {
        if (has(PARSE_ALARM_DATE) || alarmDate.getTime() != 0) {
            put(PARSE_ALARM_DATE, alarmDate);
        }
//        AlarmServer.getInstance().update(this);
////        this.alarmDate = alarmDate;
//        if (this.alarmDate != alarmDate) {
//            this.alarmDate = alarmDate;
//            AlarmServer.getInstance().update(this);
////            changed(ChangeValue.CHANGED_XX_ITEM_CHANGED_ALARM_DATE);
//        }
    }

    public long getAlarmDate() {
        Date date = getDate(PARSE_ALARM_DATE);
        return (date == null) ? 0 : date.getTime();
//        return alarmDate;
    }

    public void setAlarmDate(long alarmDate) {
        if (has(PARSE_ALARM_DATE) || alarmDate != 0) {
            put(PARSE_ALARM_DATE, new Date(alarmDate));
        }
//        AlarmServer.getInstance().update(this);
////        this.alarmDate = alarmDate;
//        if (this.alarmDate != alarmDate) {
//            this.alarmDate = alarmDate;
//            AlarmServer.getInstance().update(this);
////            changed(ChangeValue.CHANGED_XX_ITEM_CHANGED_ALARM_DATE);
//        }
    }

    public long getWaitingAlarmDate() {
        Date waitingAlarmDate = getDate(PARSE_WAITING_ALARM_DATE);
        return (waitingAlarmDate == null) ? 0 : waitingAlarmDate.getTime();
//        return waitingAlarmDate;
    }

    public void setWaitingAlarmDate(long waitingAlarmDate) {
        if (has(PARSE_WAITING_ALARM_DATE) || waitingAlarmDate != 0) {
            put(PARSE_WAITING_ALARM_DATE, new Date(waitingAlarmDate));
        }
//        this.alarmDate = alarmDate;
//        if (this.waitingAlarmDate != waitingAlarmDate) {
//            this.waitingAlarmDate = waitingAlarmDate;
////            AlarmServer.getInstance().update(this);
//        }
    }

    public RepeatRuleParseObject getRepeatRule() {
//        return repeatRule;
        RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) getParseObject(PARSE_REPEAT_RULE);
        return (repeatRule == null) ? new RepeatRuleParseObject() : repeatRule;
    }

    /**
     * sets the repeatRule but does NOT alter or update repeat instances. Used
     * when creating new repeat instances.
     *
     * @param repeatRule
     */
    public void setRepeatRuleNoUpdate(RepeatRuleParseObject repeatRule) {
//        this.repeatRule = repeatRule;
        if (has(PARSE_REPEAT_RULE) || repeatRule != null) { //no need to save a value since a null pointer is interprested as zero
            put(PARSE_REPEAT_RULE, repeatRule);
        }
    }

    /**
     * sets or updates the repeatRule and ensures that additional or no longer
     * needed repeat instances are created/deleted
     *
     * @param repeatRule
     */
    public void setRepeatRule(RepeatRuleParseObject repeatRule) {
//        if (this.repeatRule != repeatRule) {
//            this.repeatRule = repeatRule; //TODO!!!!: shouldn't repeat instances be calculated/updated whenever a repeat rule sit set?! (Currently done in copyMeInto)
//        }
        setRepeatRuleNoUpdate(repeatRule);
    }

    public long getStartedOnDate() {
//        return startedOnDate;
        Date date = getDate(PARSE_STARTED_ON_DATE);
        return (date == null) ? 0L : date.getTime();
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
        if (has(PARSE_STARTED_ON_DATE) || startedOnDate != 0) {
            put(PARSE_STARTED_ON_DATE, new Date(startedOnDate));
        }
    }

    public long getLastModifiedDate() {
//        return lastModDate;
        return getUpdatedAt().getTime();
    }

    /**
     * automatically set by Parse, not to be used
     *
     * @param lastModDate
     */
    public void setLastModifiedDatexx(long lastModDate) {
////        this.lastModDate = val;
//        if (this.lastModDate != val) {
//            this.lastModDate = val;
////            changed();
//        }
        if (has("lastModDate") || lastModDate != 0) {
            put("lastModDate", new Date(lastModDate));
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
//        this.completed = val;
        if (done) {
            setStatus(ItemStatus.STATUS_DONE);
//            receiveChangeEvent();
        } else //            if (isDone()) setCompletedDate(0L); //if Done was set before and now is unset then reset completedDate //TODO: avoid extra call to receiveChangeEvent() in setCompletedDate()
        //            setStatus(STATUS_CREATED);
        //            setReasonableStateAfterDone();
        {
            if (getActualEffort() != 0) {
                setStatus(ItemStatus.STATUS_ONGOING); //if effort is recorded, then the right state to revert to is ONGOING; TODO!!!!: are there other indicators that previous state should be other than CREATED (or should we simply store the previous state and use that??!!)
            } else {
                setStatus(ItemStatus.STATUS_CREATED);
            }
        }
    }

    /**
     * implements the user-visible logic of setting a task Waiting, and notably,
     * unsetting the Waiting state
     */
    public void setWaiting(boolean waiting) {
//        this.completed = val;
        if (waiting) {
            setStatus(ItemStatus.STATUS_WAITING);
//            receiveChangeEvent();
        } else {
//            setStatus(STATUS_CREATED);
//            setReasonableStateAfterWaiting();
            setStatus(ItemStatus.STATUS_ONGOING); //when reverting from WAITING it is fair to assume that some work has been done on the task, the ONGOING is the right state
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
     * returns the number of Items that are not done. If the list contains
     * something else than Items, returns zero
     */
    public int getNumberOfUndoneItems() {
//        return getNumberOfSubItemsWithStatus(false, ItemStatus.STATUS_DONE, true);
        return isDone() ? 0 : 1;
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
            if (topLevelTask || changeSubtaskStatus(newStatus, previousStatus)) {
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
                ItemList subtasks = getItemList();
                for (Object itemOrList : subtasks) {
                    ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
                }
            }

            //StartedOnDate SET:
            if (newStatus == ItemStatus.STATUS_ONGOING || newStatus == ItemStatus.STATUS_DONE) {
                setStartedOnDate(MyDate.getNow());
            }
            //StartedOnDate RESET: not relevant, always keep the first set StartedOnDate

            //CompletedDate: SET set to Now if changing to Done/Cancelled from other state
            if ((previousStatus != ItemStatus.STATUS_DONE && previousStatus != ItemStatus.STATUS_CANCELLED)
                    && (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED)) {
                //TODO!!!! should actual effort be reduced to zero?? No, any effort spend should be kept even for Cancelled tasks
                setCompletedDate(MyDate.getNow()); //UI: also use Completed date to store date when task was cancelled (for historical data)
            }
            //CompletedDate: RESET if changing from Done/Cancelled to other state
            //CompletedDate: set if changing to Done/Cancelled from other state, set to Now if changing to Done/Cancelled
            if ((previousStatus == ItemStatus.STATUS_DONE || previousStatus == ItemStatus.STATUS_CANCELLED)
                    && (newStatus != ItemStatus.STATUS_DONE && newStatus != ItemStatus.STATUS_CANCELLED)) {
                //if item changes from Done/Cancelled to some other value, then reset CompletedDate
                setCompletedDate(0L);
            }

            //WaitingActivatedDate:
            if (newStatus == ItemStatus.STATUS_WAITING /*
                     * && getWaitingLastActivatedDate() == 0L
                     */) { //UI: always only save the last time the task was set Waiting //-only save the first setWaitingDate (TODO!!!: or is it more intuitive that it's the last, eg it set waiting by mistake?)
                setWaitingLastActivatedDate(MyDate.getNow()); //always save
            }
            if (previousStatus == ItemStatus.STATUS_WAITING && getWaitingTillDate() != 0L) { //reset WaitingTillDate
                setWaitingTillDate(0); //reset waitingTill date
                if (getWaitingAlarmDate() != 0) { //automatically turn off
                    setWaitingAlarmDate(0);
                }
//                setWaitingLastActivatedDate(0); //-waitingActivateDate is not changed (until the task is possibly set waiting again)
            }

            //RemainingEffort: set to zero for Done/Cancelled tasks
            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
                setRemainingEffort(0L); //reset Remaining when marked done
            }

            //reset Alarms for Done/Cancelled tasks
            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
                setAlarmDate(0); //Cancel any set alarms //TODO: to support reverting when a task is marked Done, the alarm time should be kept, but not activated (AlarmServer should ignore alarms for Done tasks)
            }

            //reset any running timers
            if (newStatus == ItemStatus.STATUS_DONE || newStatus == ItemStatus.STATUS_CANCELLED) {
//                TimerServer.getInstance().stopTimerIfRunningForThisItem(this); //TODO!!!!: new way of cheking if timer is running for done item
            }
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
//        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), true); 
        setStatusImpl(true, newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), !Settings.getInstance().neverChangeProjectsSubtasksWhenChangingProjectStatus.getBoolean());
    }

    /**
     * actually updates the saved value of status
     *
     * @param newStatus
     */
    private void setStatusSaveValue(ItemStatus newStatus) {
//        put(PARSE_STATUS, newStatus);
        put(PARSE_STATUS, newStatus.toString());
//        setStatus(newStatus, !Settings.getInstance().changeSubtasksStatusWithoutConfirmation.getBoolean(), true); 
    }

    public ItemStatus getStatus() {
//        return status;
//        Object status = (ItemStatus) get(PARSE_STATUS);
        String status = getString(PARSE_STATUS);
        return (status == null) ? ItemStatus.STATUS_CREATED : ItemStatus.valueOf(status); //Created is initial value
    }

    public long getDueDate() {
//        return dueDate;
        Date date = getDate(PARSE_DUE_DATE);
        return (date == null) ? 0L : date.getTime();
    }

    public Date getDueDateD() {
//        return dueDate;
        Date date = getDate(PARSE_DUE_DATE);
        return (date == null) ? new Date(0) : date;
    }

    public void setDueDateD(Date dueDate) {
//        this.dueDate = val;
//        if (this.dueDate != val) {
//            this.dueDate = val;
//        }
        if (has(PARSE_DUE_DATE) || dueDate.getTime() != 0) {
            put(PARSE_DUE_DATE, dueDate);
        }
    }

    public void setDueDate(long dueDate) {
//        this.dueDate = val;
//        if (this.dueDate != val) {
//            this.dueDate = val;
//        }
        if (has(PARSE_DUE_DATE) || dueDate != 0) {
            put(PARSE_DUE_DATE, new Date(dueDate));
        }
    }

    public long getStartByDate() {
//        return startByDate;
        Date date = getDate(PARSE_START_BY_DATE);
        return (date == null) ? 0L : date.getTime();
    }

    public void setStartByDate(long startByDate) {
//        this.dueDate = val;
//        if (this.startByDate != startByDate) {
//            this.startByDate = startByDate;
//        }
        if (has(PARSE_START_BY_DATE) || startByDate != 0) {
            put(PARSE_START_BY_DATE, new Date(startByDate));
        }
    }

    public long getWaitingTillDate() {
//        return waitingTillDate;
        Date date = getDate(PARSE_WAITING_TILL_DATE);
        return (date == null) ? 0L : date.getTime();
    }

    public void setWaitingTillDate(long waitingTillDate) {
//        this.dueDate = val;
//        if (this.waitingTillDate != val) {
//            this.waitingTillDate = val;
////            setWaitingLastActivatedDate(MyDate.getNow()); //automatically
//        }
        if (has(PARSE_WAITING_TILL_DATE) || waitingTillDate != 0) {
            put(PARSE_WAITING_TILL_DATE, new Date(waitingTillDate));
        }
    }

    public long getWaitingLastActivatedDate() {
//        return waitingLastActivatedDate;
        Date date = getDate(PARSE_WAITING_LAST_ACTIVATED_DATE);
        return (date == null) ? 0L : date.getTime();
    }

    public void setWaitingLastActivatedDate(long waitingLastActivatedDate) {
//        this.dueDate = val;
//        if (this.waitingLastActivatedDate != val) {
//            this.waitingLastActivatedDate = val;
//        }
        if (has(PARSE_WAITING_LAST_ACTIVATED_DATE) || waitingLastActivatedDate != 0) {
            put(PARSE_WAITING_LAST_ACTIVATED_DATE, new Date(waitingLastActivatedDate));
        }
    }

    public void setEffortEstimate(long effortEstimate, boolean autoUpdateRemainingEffort) {
//#mdebug
        ASSERT.that(effortEstimate >= 0, "EffortEstimate cannot be negative");
//#enddebug
//        this.effortEstimate = val;
//        if (this.effortEstimate != val) {
//            this.effortEstimate = val;
        if (has(PARSE_EFFORT_ESTIMATE) || effortEstimate != 0) {
            put(PARSE_EFFORT_ESTIMATE, effortEstimate);
            if (autoUpdateRemainingEffort && Settings.getInstance().alwaysUpdateRemainingToEffortMinusActualWhenEffortIsUpdated()) {
//                if (this.remainingEffort + this.actualEffort < effortEstimate) { //UI: if currently set remaining effort + actual is less than estimate, then update Remaining so it corresponds to Estimate-Actual
                if (getRemainingEffort() + getActualEffort() < effortEstimate) { //UI: if currently set remaining effort + actual is less than estimate, then update Remaining so it corresponds to Estimate-Actual
//                    setRemainingEffort(effortEstimate - this.actualEffort, false); //
                    setRemainingEffort(effortEstimate - getActualEffort(), false); //
                }
            }
        }
    }

    public void setEffortEstimate(long val) {
        setEffortEstimate(val, true);
    }

    /**
     * returns effort estimate. If no estimate was set (value 0) AND there are
     * subitems, then return the sum of the estimates of the subitems.
     *
     * @return
     */
    public long getEffortEstimate(boolean forProject) {
//        if (effortEstimate == 0 && getItemListSize() != 0) {
        if (forProject && isProject()) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ESTIMATE); //optimization: store sum in intermediate variable to avoid recalculating each time
            return ((Long) derivedEstimateEffortSubItemsSumBuffered.getValue()).longValue();
        } else {
//            return effortEstimate;
            Integer effort = getInt(PARSE_EFFORT_ESTIMATE);
            return (effort == null) ? 0L : effort;
        }
    }

    public long getEffortEstimate() {
        return getEffortEstimate(true);
    }

    public void setRemainingEffort(long remainingEffort, boolean autoUpdateEffortEstimate) {
//#mdebug
        ASSERT.that(remainingEffort >= 0, "RemainingEffort cannot be negative");
//#enddebug
//        this.effortEstimate = val;
        if (getRemainingEffort() != remainingEffort) {
            if (autoUpdateEffortEstimate) {
//            if (getEffortEstimate() == 0 && Settings.getInstance().isAlwaysSetFirstEstimateToInitialEstimate()) { //if no previous estimate, use Remaining
                if (getActualEffort() == 0 && Settings.getInstance().alwaysUseRemainingAsEstimateWhenActualIsZero()) { //UI: as long as work hasn't started (Actual==0), use Remaining as historical estimate
                    setEffortEstimate(remainingEffort, false);
                }
            }
            if (remainingEffort == 0 && Settings.getInstance().markDoneIfRemainingReducedToZero()) {
                setDone(true); //UI: reducing Remaiing to 0 takes precedence over setting Done status to other (non-Done) values
            }
//            this.remainingEffort = val;
            if (has(PARSE_REMAINING_EFFORT) || remainingEffort != 0) {
                put(PARSE_REMAINING_EFFORT, new Date(remainingEffort));
            }
        }
    }

    public void setRemainingEffort(long val) {
        setRemainingEffort(val, true);
    }

    /**
     * return effort estimate for this task. If subtasks exist, and the sum of
     * their effort estimates is different from zero, then return that value,
     * otherwise return the value for this task. Only tasks that are NOT marked
     * as Done are included. If this task
     *
     * @return
     */
    public long getRemainingEffort() {
//        if (isDone()) {
//            return 0;
//        }
        return getRemainingEffort(true);
    }

    public long getRemainingEffort(boolean forProject) {
        if (forProject && getItemListSize() > 0) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_REMAINING); //optimization: store sum in intermediate variable to avoid recalculating each time
//            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue()).longValue();
//            return ((Long) derivedRemainingEffortSubItemsSumBuffered.getValue());
            long subItemSum = 0;
//            for (Object i : subitems) {
            for (Object i : getItemList()) {
                Item item = (Item) i;
                if (!item.isDone()) {
                    subItemSum += item.getRemainingEffort();
                }
            }
            return subItemSum;
        } else {
//            return remainingEffort;
            Date date = getDate(PARSE_REMAINING_EFFORT);
            return (date == null) ? 0L : date.getTime();
        }
    }

    public void setActualEffort(long actualEffort, boolean autoUpdateStatusAndStartedOnDate) {
//        this.actualEffort = val;
//#mdebug
        ASSERT.that(actualEffort >= 0, "ActualEffort cannot be negative");
//#enddebug
//        if (this.actualEffort != actualEffort) {
        if (getActualEffort() != actualEffort) {
            if (autoUpdateStatusAndStartedOnDate) {
                if (actualEffort > 0) { //if setting Actual to a positive value
//                    if (/*getActualEffort() == 0 &&*/getStatus() == STATUS_CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
//                        setStatus(STATUS_ONGOING); //automatically set to Ongoing as soon as time is spent on the task
//                    }
//                    //            setStartedOnDateIfNotAlreadySet(); //-now done in setStatus(STATUS_ONGOING)
//                    if (this.actualEffort == 0) {
//                        setStartedOnDateIfNotAlreadySet(); //set startedOnDate even if status is not set to Ongoing //TODO: are there usecases making this really necessary??
//                    }
//                    if (this.actualEffort == 0 && getStatus() == ItemStatus.STATUS_CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
                    if (getActualEffort() == 0 && getStatus() == ItemStatus.STATUS_CREATED && Settings.getInstance().setStatusOngoingWhenActualEffortSetFirstTime()) {
                        setStatus(ItemStatus.STATUS_ONGOING); //automatically set to Ongoing as soon as time is spent on the task
                    }
//                } else if (val == 0 && this.actualEffort > 0) { //if Actual is reduced to zero then reset StartedOn date
//                    setStartedOnDate(0);
//                    if (getStatus() == STATUS_ONGOING && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) {
//                        setStatus(STATUS_CREATED); //automatically set to Ongoing as soon as time is spent on the task
//                    }
//                } else if (actualEffort == 0 && this.actualEffort > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //if Actual is reduced to zero then set status to Created and reset StartedOn date
                } else if (actualEffort == 0 && getActualEffort() > 0 && Settings.getInstance().setStatusToCreatedIfActualReducedToZero()) { //if Actual is reduced to zero then set status to Created and reset StartedOn date
                    setStatus(ItemStatus.STATUS_CREATED);
                }
            }
//            this.actualEffort = actualEffort;
            if (has(PARSE_ACTUAL_EFFORT) || actualEffort != 0) {
                put(PARSE_ACTUAL_EFFORT, new Date(actualEffort));
            }
        }
    }

    public void setActualEffort(long val) {
        setActualEffort(val, true);
    }

    public long getActualEffort(boolean forProject) {
        if (forProject && isProject()) {
//            return sumUpEffortForSubItemsBuffered(FIELD_EFFORT_ACTUAL); //optimization: store sum in intermediate variable to avoid recalculating each time
            return ((Long) derivedActualEffortSubItemsSumBuffered.getValue()).longValue();
        } else {
//            return actualEffort;
            Date date = getDate(PARSE_ACTUAL_EFFORT);
            return (date == null) ? 0L : date.getTime();
        }
    }

    public long getActualEffort() {
        return getActualEffort(true);
    }

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
    static public long getIfAutoDecreaseRemainingEffortWhenActualIncreased(long newActualEffort, long oldActualEffort, long newRemainingEffort, long oldRemainingEffort) {
        long res = -1;
        if (newRemainingEffort == oldRemainingEffort && newActualEffort > oldActualEffort && Settings.getInstance().askToUpdateRemainingWhenActualIncreased()) {
            long actualIncrease = newActualEffort - oldActualEffort;
            long updatedRemaining = oldRemainingEffort - actualIncrease;
//            int res = getManualUpdateValue("You have increased " + Item.getFieldName(Item.FIELD_EFFORT_ACTUAL) /*+ " without updating " + Item.getFieldName(Item.FIELD_EFFORT_REMAINING)*/ + ". Update " + Item.getFieldName(Item.FIELD_EFFORT_REMAINING) + "?",
//            res = getManualUpdateValue(getFieldName(Item.FIELD_EFFORT_ACTUAL) + " increased. Reduce " + getFieldName(Item.FIELD_EFFORT_REMAINING) + " with " + Duration.formatDuration(actualIncrease, true) + "?", //TODO!!!: add "Reduce Remaining with 0:10 to [1:35]?" 
            res = getManualUpdateValue(getFieldName(Item.FIELD_EFFORT_ACTUAL) + " increased by " + Duration.formatDuration(actualIncrease, true)
                    + ". Reduce " + getFieldName(Item.FIELD_EFFORT_REMAINING) + " by " + Duration.formatDuration(actualIncrease, true) + " to " + Duration.formatDuration(updatedRemaining, true) + "?", //TODO!!!: add "Reduce Remaining with 0:10 to [1:35]?" 
                    getFieldName(Item.FIELD_EFFORT_REMAINING), updatedRemaining);
//            if (res != -1) {
//            setRemainingEffort(res);
//            }
        }
        return res;
    }

    /**
     * if remaining effort is unchanged, and actual effort increased, then ask
     * if Actual should be auto-increased with the reduction as default value
     */
    public void askIfAutoDecreaseRemainingEffortWhenActualIncreased(long newActualEffort, long oldActualEffort, long newRemainingEffort, long oldRemainingEffort) {
        long remaining = getIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, oldActualEffort, newRemainingEffort, oldRemainingEffort);
        if (remaining != -1) {
            setRemainingEffort(remaining);
        }
    }

    /**
     * encapsulates all the logic to do intelligent / automatic updates of
     * interrelated fields
     */
    public void setEffortFieldsAndStatus(long newEffortEstimate, long newActualEffort, long newRemainingEffort, ItemStatus newStatus) {
        if (newStatus == getStatus()) { //status unchanged
            if (newEffortEstimate == getEffortEstimate()) { //only actual or remaining effort have changed
                if (newRemainingEffort == getRemainingEffort()) { //only actual effort may have changed
//                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort);
                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, getActualEffort(), newRemainingEffort, getRemainingEffort());
                    setActualEffort(newActualEffort, false); //contains the logic to update
                } else //remaining effort has changed
                {
                    if (newActualEffort == getActualEffort()) { //only remaining effort may have changed
//                        askIfAutoUpdateActualEffortWhenRemainingReduced(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort); //-don't: it's counterintuitive - use increaseActual to record actuals, not decreaseRemaining
                        setRemainingEffort(newRemainingEffort);
                    } else { //both actual and remaining effort have changed
                        setRemainingEffort(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
                        setActualEffort(newActualEffort, false);
                    }
                }
            } else //effort estimate has changed
            {
                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
                    setEffortEstimate(newEffortEstimate); //contains the logic to update
                } else //remaining effort or actual effort have changed
                {
                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
                        setEffortEstimate(newEffortEstimate, false);
                        setRemainingEffort(newRemainingEffort, false);
                    } else { //both actual and remaining effort have changed
                        setEffortEstimate(newEffortEstimate, false);
                        setRemainingEffort(newRemainingEffort, false);
                        setActualEffort(newActualEffort, true); //do autoupdate status
                    }
                }
            }
        } else //status is modified
        {
            if (newEffortEstimate == getEffortEstimate()) { //only actual or remaining effort have changed
                if (newRemainingEffort == getRemainingEffort()) { //only actual effort may have changed
//                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort);
                    askIfAutoDecreaseRemainingEffortWhenActualIncreased(newActualEffort, getActualEffort(), newRemainingEffort, getRemainingEffort());
                    setActualEffort(newActualEffort, false); //contains the logic to update
                } else //remaining effort has changed
                {
                    if (newActualEffort == getActualEffort()) { //only remaining effort may have changed
//                        askIfAutoUpdateActualEffortWhenRemainingReduced(newActualEffort, this.actualEffort, newRemainingEffort, this.remainingEffort); //-don't: it's counterintuitive - use increaseActual to record actuals, not decreaseRemaining
                        setRemainingEffort(newRemainingEffort);
                    } else { //both actual and remaining effort have changed
                        setRemainingEffort(newRemainingEffort); //no contradicting side effects between updating Remaining and Actual
                        setActualEffort(newActualEffort, false); //don't autoupdate status
                    }
                }
            } else //effort estimate has changed
            {
                if (newRemainingEffort == getRemainingEffort() && newActualEffort == getActualEffort()) { //only effort estimate may have changed
                    setEffortEstimate(newEffortEstimate); //contains the logic to update
                } else //remaining effort or actual effort have changed
                {
                    if (newActualEffort == getActualEffort()) { //only estimate and remaining effort may have changed
                        setEffortEstimate(newEffortEstimate, false);
                        setRemainingEffort(newRemainingEffort, false);
                    } else { //both actual and remaining effort have changed
                        setEffortEstimate(newEffortEstimate, false);
                        setRemainingEffort(newRemainingEffort, false);
                        setActualEffort(newActualEffort, false);
                    }
                }
            }
        }
    }

    public long getShowFromDate() {
//        return showFromDate;
        Date date = getDate(PARSE_SHOW_FROM_DATE);
        return (date == null) ? 0L : date.getTime();
    }

    public void setShowFromDate(long showFromDate) {
//        this.showFromDate = val;
//        if (this.showFromDate != showFromDate) {
//            this.showFromDate = showFromDate;
//        }
        if (has(PARSE_SHOW_FROM_DATE) || showFromDate != 0) {
            put(PARSE_SHOW_FROM_DATE, new Date(showFromDate));
        }
    }

    public int getCategoriesSize() {
        if (getCategories() == null) {
            return 0;
        } else {
            return getCategories().size();
        }
//        if (categories == null) { //) || categories.getSize() == 0) {
//            return 0;
//        } else {
//            return categories.getSize();
//        }
    }

    public Set<Category> getCategories() {
//        return getList("categories");
        List categories = getList(PARSE_CATEGORIES);
//        return (categories == null) ? new ArrayList() : categories;
        return (categories == null) ? new HashSet() : new HashSet(categories);
//        if (categories == null) {
//            categories = new ItemList(BaseItemTypes.CATEGORY, this, false, isEnsureItemCategoryAutoConsistency(), true, true); //ItemListSaveInline(this);
//        }
//        return categories;
    }

    /**
     * updates this Item's categories (adds new ones,
     * deleteRuleAndAllRepeatInstancesExceptThis unselected ones) AVOID TO USE??
     * - only change categories via getCategories().update/remove...
     */
    public void setCategories(Set<Category> categories) {
//        getCategories().updateListWithDifferences(categories);
//        put("categories", categories);
        if (has(PARSE_CATEGORIES) || categories != null) {
            put(PARSE_CATEGORIES, new ArrayList(categories));
        }
    }

    /**
     * adds category to this item's categories if not already there (no
     * duplicates)
     */
    public void addCategory(Category category) {
        category.addItem(this);
        this.addUniqueToArrayField(PARSE_CATEGORIES, category); //TODO: will addUniqueToArrayField create the list if not already existing?
    }

    /**
     * adds category to this item's categories if not already there (no
     * duplicates)
     *
     * @param category
     */
    public void removeCategory(Category category) throws Exception {
        category.removeItem(this);
        this.removeFromArrayField(PARSE_CATEGORIES, category);
    }

    /**
     * if commentString is non-empty, then add it to comment. If comment is
     * non-empty, then add a newline to separate the old comment and the
     * commentString. If addToEnd is true, then commentString is added to the
     * end, otherwise to the beginning.
     */
    public void addToComment(String commentString, boolean addToEnd) {
//        better not to test for null to catch this error condition
//        if (commentString == null | commentString.equals("")) {
        if (commentString.equals("")) {
            return;
        } else {
            String oldComment = getComment();
            if (addToEnd) {
                setComment(oldComment + (oldComment.equals("") ? "" : "\n") + commentString); //only add newline if comment already contains text
            } else {
                setComment(commentString + (oldComment.equals("") ? "" : "\n") + oldComment);
            }
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

    public int getPriority() {
        Integer i = getInt(PARSE_PRIORITY);
        if (i == null) {
            return 0;
        } else {
            return i;
        }
////        return priority;
//        return priority.getPriority();
    }

    public Priority getPriorityObject() {
//        return priority;
        return new Priority(getPriority());
    }

    public void setPriority(int prio) {
        if (prio != 0) {
            put(PARSE_PRIORITY, prio);
        }
////        this.priority = val;
////        if (this.priority != val) {
//        if (this.priority.getPriority() != prio) {
////            this.priority = val;
//            this.priority.setPriority(prio);
//        }
    }

    public double getEarnedValue() {
        Double earnedVal = getDouble(PARSE_EARNED_VALUE);
        return (earnedVal == null) ? 0 : earnedVal;
//        return earnedValue;
    }

    public void setEarnedValue(double earnedVal) {
//                if (!has("earnedValue") &&  earnedVal == 0) return; //equivalent 
        if (has(PARSE_EARNED_VALUE) || earnedVal != 0) {
            put(PARSE_EARNED_VALUE, earnedVal);
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
    private double calculateEarnedValuePerHour(long totalEffort, double earnedPoints) {
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
        return calculateEarnedValuePerHour(getTotalExpectedEffort(), getEarnedValue());
    }

    public long getCompletedDate() {
//        return completedDate;
        Date date = getDate(PARSE_COMPLETED_DATE);
        return (date == null) ? 0L : date.getTime();
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
        if (has(PARSE_COMPLETED_DATE) || completedDate != 0) {
            put(PARSE_COMPLETED_DATE, new Date(completedDate));
        }
        //TODO: set task to FIELD_DONE if completedDate was 0 before
        if (///** oldVal == 0L && */
                completedDate != 0L && getStatus() != ItemStatus.STATUS_DONE && getStatus() != ItemStatus.STATUS_CANCELLED) {
            setDone(true);
        }
    }

    public long getCreatedDate() {
//        return createdDate;
        return getCreatedAt().getTime();
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

    @Override
    public ItemList getListForNewCreatedRepeatInstances() { //TODO: replace with getParent()
//        if (this.getParent() instanceof ItemList) {
//            return (ItemList) this.getParent();
//        } else {
//            return null;
//        }
        return (ItemList) getOwner(); //should always be a list?! Otherwise best to catch with via wrong typecasting
    }

    @Override
    public void deleteRepeatInstance() {
        delete();
    }

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

    public String getAlarmIdText(int alarmId) {
        if (alarmId == Item.FIELD_ALARM_DATE) {
            return getFieldName(alarmId);
        } else if (alarmId == FIELD_WAITING_ALARM_DATE) {
            return getFieldName(alarmId);
        } else {
            return "";
        }
//        ASSERT.that("Not supported yet.");
    }

    /**
     * returns the int value used by getSumAt() to calculate the sum of lists.
     * Can be overwritten by eg. ItemLists to return some meaningful value to
     * add up in lists of lists
     */
    @Override
    public long getSumField(int fieldId) {
        Object itemField;
        if ((itemField = (((Item) this).getFilterField(fieldId))) instanceof Long) {
            return ((Long) itemField).longValue();
        } else {
            return 0;
        }
    }

    @Override
    public long getSumField() {
        return getSumField(FIELD_EFFORT_REMAINING);
    }

    @Override
    public boolean ignoreSumField() {
        return isDone() || getStatus() == ItemStatus.STATUS_CANCELLED;  //ignore the sum of Done Items
    }

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
    public long getFinishTime(long subSum) {
        Object owner = getOwner();
        if (owner != null && owner instanceof ItemList) {
            return ((ItemList) owner).getFinishTime(this, subSum);
        } else {
            return 0;
        }
    }

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
    /**
     * if earliest=true returns the earlist finish time for this Item (assuming
     * it belongs to several different lists each with WorkTime defined). If
     * earliest false, returns the latest finish time.
     *
     * @param earliest
     * @return
     */
    public long getFinishTimeTODO(boolean earliest) {
        return 0;
    }

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
    long getStartTime() {
        Object owner = getOwner();
        if (owner != null && owner instanceof ItemList) {
            return ((ItemList) owner).getFinishTime(this, 0);
        }
        return 0L;
    }

    public String getFilterFieldFormatted(int fieldId) {
        switch (getFieldType(fieldId)) {
            case Expr.VALUE_FIELD_TYPE_STRING:
                return ((String) getFilterField(fieldId));
            case Expr.VALUE_FIELD_TYPE_DATE:
                if (((Long) getFilterField(fieldId)) == 0L) {
                    return "Undated"; //Internationalize!
                } else {
                    return (new MyDate(((Long) getFilterField(fieldId)))).formatDate();
                }
            case Expr.VALUE_FIELD_TYPE_INTEGER:
                return "" + ((Integer) getFilterField(fieldId));
            case Expr.VALUE_FIELD_TYPE_DURATION:
                return Duration.formatDuration(((Long) getFilterField(fieldId)));
            case Expr.VALUE_FIELD_TYPE_ENUM:
                if (fieldId == FIELD_STATUS) {
//                    return STATUS_NAMES[getStatus()];
                    return getStatusName(getStatus());
                } else {
                    return "ERROR in getFilterFieldFormatted, fieldId=" + fieldId;
                }
        }
        return "";
    }

    public static FieldDef[] getFields() {
        return FIELDS;
    }

    /**
     * returns the list of enum values that can be chosen for the field fieldId.
     * E.g. for Item.Status returns int[]{DONE, CANCELLED, ...}
     *
     * @param fieldId
     * @return
     */
    public static int[] getFieldEnumValues(int fieldId) {
        switch (fieldId) {
            case Item.FIELD_STATUS:
                //            return STATUS_VALUES; // int[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
                return getStatusValues(); // int[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
            case Item.FIELD_PRIORITY_IMPORTANCE:
            case Item.FIELD_PRIORITY_URGENCY:
                return PriorityImpUrgencyPair.getImpUrgIntArray();
            default:
                //            return new int[]{};
                return null;
        }
    }

    public static String[] getFieldEnumNames(int fieldId) {
        switch (fieldId) {
            case Item.FIELD_STATUS:
                //            return STATUS_NAMES; //new String[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
                return getStatusNames(); //new String[] {STATUS_CREATED, STATUS_ONGOING, STATUS_WAITING, STATUS_DONE};
            case Item.FIELD_PRIORITY_IMPORTANCE:
            case Item.FIELD_PRIORITY_URGENCY:
                return PriorityImpUrgencyPair.getImpUrgStringArray();
            default:
                //            return new String[]{};
                return null;
        }
    }

    /**
     * returns the ids of all fields with the specific typeId. Used e.g. in
     * filter expressions when selecting which fields a field can be compared
     * with.
     *
     * @param typeId
     * @return
     */
    public static int[] getFieldValuesOfType(int typeId) {
        FieldDef[] FIELDS = getFields();
        Vector fieldsOfTypeVector = new Vector();
        for (int i = 0, size = FIELDS.length; i < size; i++) {
            if (FIELDS[i].type == typeId) {
                fieldsOfTypeVector.add(FIELDS[i].id);
            }
        }
        int[] fieldsOfType = new int[fieldsOfTypeVector.size()];
        for (int i = 0, size = fieldsOfTypeVector.size(); i < size; i++) {
            fieldsOfType[i] = ((Integer) fieldsOfTypeVector.elementAt(i));
        }
        return fieldsOfType;
    }

    public static String[] getFieldNamesOfType(int typeId) {
        FieldDef[] FIELDS = getFields();
        Vector fieldsOfTypeVector = new Vector();
        for (int i = 0, size = FIELDS.length; i < size; i++) {
            if (FIELDS[i].type == typeId) {
                fieldsOfTypeVector.add(FIELDS[i].name);
            }
        }
        String[] fieldsOfType = new String[fieldsOfTypeVector.size()];
        for (int i = 0, size = fieldsOfTypeVector.size(); i < size; i++) {
            fieldsOfType[i] = (String) fieldsOfTypeVector.elementAt(i);
        }
        return fieldsOfType;
    }

    /**
     * the order of the fields in this String array determines the order of
     * fields displayed in the editor screens, e.g. FilterDefScreen
     *
     * @return
     */
    public static String[] getAllFieldNamesInDisplayOrder() {
//        return FIELDNAMES; //optimization: return FIELDNAMES directly if UI becomes too slow
        return getFieldNames(getAllFieldIdsinDisplayOrder());
    }

    /**
     * returns the list of displayable field names for the given array of
     * fieldIds
     *
     * @param fieldIds
     * @return
     */
    public static String[] getFieldNames(int[] fieldIds) {
        String[] fieldNameArr = new String[fieldIds.length];
        for (int i = 0, size = fieldIds.length; i < size; i++) {
            fieldNameArr[i] = getFieldName(fieldIds[i]);
        }
        return fieldNameArr;
    }

//    public static FieldDef[] getFields() {
//        return null;
//    }
    public static int[] getAllFieldIdsinDisplayOrder() {
        int[] FIELDIDS = new int[FIELDS.length];
        for (int i = 0, size = FIELDS.length; i < size; i++) {
            FIELDIDS[i] = FIELDS[i].id;
        }
        return FIELDIDS;
    }

    public static FieldDef getFieldDef(int fieldId) {
        for (int i = 0, size = FIELDS.length; i < size; i++) {
            if (FIELDS[i].id == fieldId) {
                return FIELDS[i];
            }
        }
        ASSERT.that("ItemListFlatten.getFieldName(" + fieldId + ") - value not defined");
        return null;
    }

    /**
     * returns the displayable name for a given field
     *
     * @param fieldId
     * @return
     */
    public static String getFieldName(int fieldId) {
        FieldDef fieldDef = getFieldDef(fieldId);
        return fieldDef.name;
    }

    /**
     * returns the help dreadFunNames for the field, or "" if none define
     *
     * @param fieldId
     * @return
     */
    public static String getFieldHelpText(int fieldId) {
        FieldDef fieldDef = getFieldDef(fieldId);
        return fieldDef.help;
    }

    /**
     * returns the FieldType (eg Expr.VALUE_FIELD_TYPE_STRING,
     * Expr.VALUE_FIELD_TYPE_BOOLEAN, Expr.VALUE_FIELD_TYPE_DATE) for the given
     * fieldId
     *
     * @param fieldId e.g. FIELD_DESCRIPTION, FIELD_DONE
     * @return
     */
    public static int getFieldType(int fieldId) {
        FieldDef fieldDef = getFieldDef(fieldId);
        return fieldDef.type;
    }

    /**
     * call to save the item
     */
    public void commit() throws ParseException {
        this.save();//ParseObject.save()

    }

    /**
     * returns true if the status of this task should be changed to the
     * newStatus. Used to count the number of subtasks that may have their
     * status changed when changing the status of the mother task/project, or
     * task-list.
     *
     * @param newStatus
     * @param oldStatus
     * @return
     */
//    public static boolean changeSubtaskStatus(int newStatus, int oldStatus) {
    public static boolean changeSubtaskStatus(ItemStatus newStatus, ItemStatus oldStatus) {
        return (newStatus == ItemStatus.STATUS_DONE && (oldStatus == ItemStatus.STATUS_CREATED
                || oldStatus == ItemStatus.STATUS_ONGOING || oldStatus == ItemStatus.STATUS_WAITING))
                || (newStatus == ItemStatus.STATUS_ONGOING && (oldStatus == ItemStatus.STATUS_CREATED
                || oldStatus == ItemStatus.STATUS_WAITING))
                || (newStatus == ItemStatus.STATUS_WAITING && (oldStatus == ItemStatus.STATUS_CREATED
                || oldStatus == ItemStatus.STATUS_ONGOING))
                || (newStatus == ItemStatus.STATUS_CANCELLED
                && (oldStatus == ItemStatus.STATUS_CREATED || oldStatus == ItemStatus.STATUS_ONGOING
                || oldStatus == ItemStatus.STATUS_WAITING))
                || (newStatus == ItemStatus.STATUS_CREATED && (oldStatus == ItemStatus.STATUS_ONGOING
                || oldStatus == ItemStatus.STATUS_WAITING || oldStatus == ItemStatus.STATUS_DONE));
    }

//    public int countTasksWhereStatusWillBeChanged(ItemStatus newStatus, boolean recurseOverSubtasks) {
//        return (changeSubtaskStatus(newStatus, getStatus()) ? 1 : 0)
//                + ((recurseOverSubtasks && getItemListSize() > 0)
//                        ? getItemList().countTasksWhereStatusWillBeChanged(newStatus, recurseOverSubtasks)
//                        : 0);
//    }
    
    @Override
    public String toString() {
        return getText();
    }
}
