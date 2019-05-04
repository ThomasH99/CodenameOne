/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import static com.todocatalyst.todocatalyst.Item.PARSE_DELETED_DATE;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Thomas
 */
public class FilterSortDef extends ParseObject {

//    private static FilterSortDef DEFAULT_FILTER = null; //no good to use an (editable) filter with singleton, since edits will change the original. Must use separate instances and equal() to compare
    //TODO save filters to parse (with the list, not the view?!). Save each field to make it easy to understand what filters users use)
    //TODO save filter either as for the screen (applied to all lists shown) or specific to the list (later option since less intuitive)
    //TODO filter on 'leaf tasks' only
    //TODO introduce default FilterSortDef (definable by end-user) so that every new filter (e.g. a new list of tasks) get the desired settings
//<editor-fold defaultstate="collapsed" desc="static strings">
    public static String CLASS_NAME = "FilterSortDef";
    private static String PARSE_SORT_FIELD = "sortField";
    private static String PARSE_SORT_DESCENDING = "sortDescending";
    private static String PARSE_SORT_ON = "sortActive";
    private static String PARSE_FILTER_OPTIONS = "filterOptions";
    static String PARSE_SCREEN_ID = "ScreenId";
    static String PARSE_FILTERED_OBJECT_ID = "filteredObjectId";
    static String PARSE_FILTERED_OBJECT = "filteredObject";
    private static String PARSE_FILTER_NAME = "name";
    private static String PARSE_FILTER_DESCRIPTION = "description";
//    public static String PARSE_SORT_FILTER_ID = "filterId"; //name of screen and objectId of object displayed
//    final static String PARSE_DELETED = "deleted"; //has this object been deleted on some device?
//    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?

    @Override
    public boolean equals(Object obj) {
        if (this == obj) 
            return true;
        if (!(obj instanceof FilterSortDef)) 
            return false; //covers obj==null
        
        FilterSortDef filterSortDef = (FilterSortDef) obj;
        if (!Objects.equals(this.getObjectIdP(), filterSortDef.getObjectIdP())) 
            return false;
        if (!Objects.equals(getSortFieldId(), filterSortDef.getSortFieldId())) 
            return false;
        if (!Objects.equals(isSortDescending(), filterSortDef.isSortDescending())) 
            return false;
        if (isSortOn() != filterSortDef.isSortOn()) 
            return false;
        if (!Objects.equals(getFilterName(), filterSortDef.getFilterName())) 
            return false;
        if (!Objects.equals(getDescription(), filterSortDef.getDescription())) 
            return false;
        return false;
    }

    public static String FILTER_SHOW_NEW_TASKS = "showNewTasks";
    public static String FILTER_SHOW_ONGOING_TASKS = "showOngoingTasks";
    public static String FILTER_SHOW_WAITING_TASKS = "showWaitingTasks";
    public static String FILTER_SHOW_DONE_TASKS = "showDoneTasks";
    public static String FILTER_SHOW_CANCELLED_TASKS = "showCancelledTasks";
    public static String FILTER_SHOW_BEFORE_HIDE_UNTILDATE = "showBeforeHideUntilDate";
    public static String FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS = "hideDependingOnTasks";
    public static String FILTER_SHOW_EXPIRES_ON_DATE = "showExpiresOnDate";
    public static String FILTER_SHOW_PROJECTS_ONLY = "showProjectsOnly";
    public static String FILTER_SHOW_INTERRUPT_TASKS_ONLY = "showInterruptTasksOnly";
    public static String FILTER_SHOW_WITHOUT_ESTIMATES_ONLY = "showWithoutEstimatesOnly";
    public static String FILTER_SHOW_WITH_ACTUALS_ONLY = "showWithActualsOnly";
    public static String FILTER_SHOW_ALL = "showAll";

    public final static String FILTER_SORT_TODAY_VIEW = "TODAY_VIEW";
//    public final static String FILTER_SORT_OWNER = "SORT_ON_OWNER";

    private boolean showInitialized = false;
    private boolean showAll = true;
    private boolean showDefault;// = true;
    private boolean showNewTasks;// = true;
    private boolean showOngoingTasks;// = true;
    private boolean showWaitingTasks;
    private boolean showDoneTasks;
    private boolean showCancelledTasks;
    private boolean showBeforeHideUntilDate;
    private boolean showDependingOnUndoneTasks;
    private boolean showExpiresOnDate;
    private boolean showProjectsOnly;
    private boolean showInterruptTasksOnly;
    private boolean showWithoutEstimatesOnly;
    private boolean showWithActualsOnly;
//</editor-fold>

    public FilterSortDef() {
        super(CLASS_NAME);
//        setDefaults();
//        assert sortOptions.length == sortField.length: "error different number items in sortOptions and softField"; //already done in ScreenFilter
    }

//    public FilterSortDef(String screenId, String filteredObjectId) {
//    public FilterSortDef(String screenId, ParseObject filteredObject) {
//        this();
//        setScreenId(screenId);
//        setFilteredObjectId(filteredObject.getObjectIdP());
//        setFilteredObject(filteredObject);
//    }
    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortDescending) {
        this();
        setSortFieldId(sortParseFieldId);
        setSortOn(sortParseFieldId != null && !sortParseFieldId.equals(""));
        if (filterOptions != null) {
            extractAndSetFilterOptions(filterOptions);
            putFilterOptions();
        } else {
            getFilterOptions();
        }
        setSortDescending(sortDescending);
    }

    /**
     * sets default filter: no sorting, show all tasks
     */
    void setDefaults() {
        setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
        setSortOn(false); //don't sort by default, 
        extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS + FILTER_SHOW_DONE_TASKS + FILTER_SHOW_CANCELLED_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
        putFilterOptions();
        setSortDescending(false);
    }

    /**
    return default task filter, unsorted, hiding Done/Cancelled tasks. 
    @return 
     */
    static FilterSortDef getDefaultFilter() {
//        if (DEFAULT_FILTER == null) {
        FilterSortDef filter = new FilterSortDef();
        filter.setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
        filter.setSortOn(false); //don't sort by default, 
        filter.setSortDescending(false);
        filter.extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
        filter.putFilterOptions();
//            DEFAULT_FILTER = filter;
//        }
//        return DEFAULT_FILTER;
        return filter;
    }

    /**
    return default task filter, unsorted, hiding Done/Cancelled tasks. 
    @return 
     */
    static FilterSortDef getDefaultFilterNuetral() {
        FilterSortDef filter = new FilterSortDef();
//            filter.setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
//            filter.setSortOn(false); //don't sort by default, 
//            filter.setSortDescending(false);
//            filter.extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
//            filter.putFilterOptions();
        return filter;
    }

//    static String[] sortOptions = new String[]{
//        "Priority", "Due date", "Remaining time",
//        "Difficulty", "Fun", "Value",
//        "Start by date", "Date work on tasks started",
//        "Date last modified", "Date created", "Date completed",
//        "Task text", "Importance/Urgency", "Status"};
    private static String[] sortOptions = new String[]{
        Item.PRIORITY, Item.DUE_DATE, Item.EFFORT_REMAINING,
        Item.EFFORT_ESTIMATE, Item.EFFORT_ACTUAL,
        Item.CHALLENGE, Item.FUN_DREAD, Item.EARNED_VALUE,
        Item.START_BY_TIME, Item.STARTED_ON_DATE, Item.UPDATED_DATE,
        Item.CREATED_DATE, Item.COMPLETED_DATE, Item.WAIT_UNTIL_DATE,
        Item.DESCRIPTION,
        Item.IMPORTANCE_URGENCY, Item.STATUS};

    private static String[] sortField = new String[]{
        Item.PARSE_PRIORITY, Item.PARSE_DUE_DATE, Item.PARSE_REMAINING_EFFORT,
        Item.PARSE_EFFORT_ESTIMATE, Item.PARSE_ACTUAL_EFFORT,
        Item.PARSE_CHALLENGE, Item.PARSE_DREAD_FUN_VALUE, Item.PARSE_EARNED_VALUE,
        Item.PARSE_START_BY_DATE, Item.PARSE_STARTED_ON_DATE, Item.PARSE_UPDATED_AT,
        Item.PARSE_CREATED_AT, Item.PARSE_COMPLETED_DATE, Item.PARSE_WAITING_TILL_DATE,
        Item.PARSE_TEXT,
        Item.PARSE_IMPORTANCE_URGENCY, Item.PARSE_STATUS};

    /**
     * fetches a filter from Parse or returns default filter if none is stored
     *
     * @param screenId
     * @param itemList
     * @return
     */
    static FilterSortDef fetchFilterSortDefXXX(String screenId, ParseObject itemList, FilterSortDef defaultFilter) {
        FilterSortDef filterSortDef = null;
        if (itemList instanceof ParseObject && ((ParseObject) itemList).getObjectIdP() != null) {
//            filterSortDef = DAO.getInstance().getFilterSortDef(screenId, ((ParseObject) itemList).getObjectId());
            filterSortDef = DAO.getInstance().getFilterSortDefXXX(((ParseObject) itemList).getObjectIdP());
        }
        if (filterSortDef == null) {
//            filterSortDef = new FilterSortDef(screenId, itemList);
//            DAO.getInstance().save(filterSortDef);
            filterSortDef = defaultFilter;
        }
        return filterSortDef;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    FilterPredicate filterPredicate = new FilterPredicate();
//    MyHashMap<String, Object> filterMap = new MyHashMap();
//    private String sortFieldId = Item.PARSE_DUE_DATE; //UI: default sort on DueDate (??)
//    private boolean sortAscending = true;
//    private boolean sortOn = false;
//    public String getScreenId() {
//        return getString(PARSE_SCREEN_ID);
//    }
//
//    public void setScreenId(String screenId) {
//        put(PARSE_SCREEN_ID, screenId);
//    }
//    public String getFilteredObjectId() {
//        String objId = getString(PARSE_FILTERED_OBJECT_ID);
//        if (objId != null) {
//            return objId;
//        } else {
//            return "";
//        }
//    }
//
//    public void setFilteredObjectId(String filteredObjectId) {
//        if (filteredObjectId != null) {
//            put(PARSE_FILTERED_OBJECT_ID, filteredObjectId);
//        } else {
//            remove(PARSE_FILTERED_OBJECT_ID);
//        }
//    }
//
//    public ParseObject getFilteredObject() {
//        Object parseObject = get(PARSE_FILTERED_OBJECT);
//        if (parseObject != null && parseObject instanceof ParseObject) {
//            return (ParseObject) parseObject;
//        } else {
//            return null;
//        }
//    }
//
//    public void setFilteredObject(ParseObject filteredObject) {
//        if (filteredObject != null) {
//            put(PARSE_FILTERED_OBJECT, filteredObject);
//        } else {
//            remove(PARSE_FILTERED_OBJECT);
//        }
//    }
//</editor-fold>
    public String getFilterName() {
        return getString(PARSE_FILTER_NAME);
    }

    public void setFilterName(String filterName) {
        put(PARSE_FILTER_NAME, filterName);
    }

    public String getDescription() {
        return getString(PARSE_FILTER_DESCRIPTION);
    }

    public void setDescription(String description) {
        put(PARSE_FILTER_DESCRIPTION, description);
    }

    /**
     * @return the sortOn
     */
    public boolean isSortOn() {
        if (getBoolean(PARSE_SORT_ON) != null) {
            return getBoolean(PARSE_SORT_ON);
        } else {
            return false;
        }
    }

    /**
     * returns tru if the filter does not have any effect (no sorting, no
     * filtering)
     *
     * @return
     */
    public boolean isNeutral() {
        boolean neutral
                = showNewTasks
                && showOngoingTasks
                && showWaitingTasks
                && showDoneTasks
                && showCancelledTasks
                && showBeforeHideUntilDate
                && showDependingOnUndoneTasks
                && showExpiresOnDate
                && !showProjectsOnly
                && !showInterruptTasksOnly
                && !showWithoutEstimatesOnly
                && !showWithActualsOnly;

        return !isSortOn() && (showAll || neutral);
    }

    /**
     * @param sortOn the sortOn to set
     */
    public void setSortOn(boolean sortOn) {
        if (sortOn) {
            put(PARSE_SORT_ON, sortOn);
        } else {
            remove(PARSE_SORT_ON);
        }
    }

    public boolean isSortDescending() {
        if (getBoolean(PARSE_SORT_DESCENDING) != null) {
            return getBoolean(PARSE_SORT_DESCENDING);
        } else {
            return false;
        }
    }

    /**
     * @param sortOn the sortOn to set
     */
    public void setSortDescending(boolean sortDescending) {
        if (sortDescending) {
            put(PARSE_SORT_DESCENDING, sortDescending);
        } else {
            remove(PARSE_SORT_DESCENDING);
        }
    }

    /**
     * @return the sortFieldId
     */
    public String getSortFieldId() {
        if (getString(PARSE_SORT_FIELD) != null) {
            return getString(PARSE_SORT_FIELD);
        } else {
//            return Item.PARSE_DUE_DATE;
            return ""; //default no sorting
        }
    }

    /**
     * @param sortFieldId the sortFieldId to set
     */
    public void setSortFieldId(String sortFieldId) {
        if (sortFieldId != null && !sortFieldId.equals("")) {
            put(PARSE_SORT_FIELD, sortFieldId);
        } else {
            remove(PARSE_SORT_FIELD);
        }
    }

//    public void getFilterOptions() {
//        getFilterOptions(null);
//    }
    private void extractAndSetFilterOptions(String filterOptions) {
//        showNewTasks = filterOptions.indexOf("showNewTasks") != -1;
//        showOngoingTasks = filterOptions.indexOf("showOngoingTasks") != -1;
//        showWaitingTasks = filterOptions.indexOf("showWaitingTasks") != -1;
//        showDoneTasks = filterOptions.indexOf("showDoneTasks") != -1;
//        showCancelledTasks = filterOptions.indexOf("showCancelledTasks") != -1;
//        showBeforeHideUntilDate = filterOptions.indexOf("showBeforeHideUntilDate") != -1;
//        showExpiresOnDate = filterOptions.indexOf("showExpiresOnDate") != -1;
//        showProjectsOnly = filterOptions.indexOf("showProjectsOnly") != -1;
//        showInterruptTasksOnly = filterOptions.indexOf("showInterruptTasksOnly") != -1;
//        showWithoutEstimatesOnly = filterOptions.indexOf("showWithoutEstimatesOnly") != -1;
//        showWithActualsOnly = filterOptions.indexOf("showWithActualsOnly") != -1;
        if (filterOptions == null || filterOptions.length() == 0) {
            return;
        }

        showAll = filterOptions.indexOf(FILTER_SHOW_ALL) != -1; // NB. String.contains not implemented for CN1

        showNewTasks = filterOptions.indexOf(FILTER_SHOW_NEW_TASKS) != -1 || showAll;
        showOngoingTasks = filterOptions.indexOf(FILTER_SHOW_ONGOING_TASKS) != -1 || showAll;
        showWaitingTasks = filterOptions.indexOf(FILTER_SHOW_WAITING_TASKS) != -1 || showAll;
        showDoneTasks = filterOptions.indexOf(FILTER_SHOW_DONE_TASKS) != -1 || showAll;
        showCancelledTasks = filterOptions.indexOf(FILTER_SHOW_CANCELLED_TASKS) != -1 || showAll;
        showBeforeHideUntilDate = filterOptions.indexOf(FILTER_SHOW_BEFORE_HIDE_UNTILDATE) != -1 || showAll;
        showDependingOnUndoneTasks = filterOptions.indexOf(FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS) != -1 || showAll;
        showExpiresOnDate = filterOptions.indexOf(FILTER_SHOW_EXPIRES_ON_DATE) != -1 || showAll;

        showProjectsOnly = filterOptions.indexOf(FILTER_SHOW_PROJECTS_ONLY) != -1;
        showInterruptTasksOnly = filterOptions.indexOf(FILTER_SHOW_INTERRUPT_TASKS_ONLY) != -1;
        showWithoutEstimatesOnly = filterOptions.indexOf(FILTER_SHOW_WITHOUT_ESTIMATES_ONLY) != -1;
        showWithActualsOnly = filterOptions.indexOf(FILTER_SHOW_WITH_ACTUALS_ONLY) != -1;
    }

//    public void getFilterOptions(String filterOptions) {
    public String getFilterOptions() {
//        if (filterOptions == null) {
        String filterOptions = getString(PARSE_FILTER_OPTIONS);
        extractAndSetFilterOptions(filterOptions);
        return filterOptions;
//        }
    }

    private void initFilterOptions() {
        if (!showInitialized) {
            getFilterOptions();
            showInitialized = true;
        }
    }

    private void putFilterOptions() {
        String filterOptions
                = (showNewTasks ? FILTER_SHOW_NEW_TASKS + " " : "")
                + (showOngoingTasks ? FILTER_SHOW_ONGOING_TASKS + " " : "")
                + (showWaitingTasks ? FILTER_SHOW_WAITING_TASKS + " " : "")
                + (showDoneTasks ? FILTER_SHOW_DONE_TASKS + " " : "")
                + (showCancelledTasks ? FILTER_SHOW_CANCELLED_TASKS + " " : "")
                + (showBeforeHideUntilDate ? FILTER_SHOW_BEFORE_HIDE_UNTILDATE + " " : "")
                + (showDependingOnUndoneTasks ? FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS + " " : "")
                + (showExpiresOnDate ? FILTER_SHOW_EXPIRES_ON_DATE + " " : "")
                + (showProjectsOnly ? FILTER_SHOW_PROJECTS_ONLY + " " : "")
                + (showInterruptTasksOnly ? FILTER_SHOW_INTERRUPT_TASKS_ONLY + " " : "")
                + (showWithoutEstimatesOnly ? FILTER_SHOW_WITHOUT_ESTIMATES_ONLY + " " : "")
                + (showWithActualsOnly ? FILTER_SHOW_WITH_ACTUALS_ONLY + " " : "");
        put(PARSE_FILTER_OPTIONS, filterOptions);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    class MyHashMap<K, V> extends HashMap {
//        
//        final String FILTER_HIDE_TASKS_NEW = "FILTER_SHOW_TASKS_NEW";
//        final String FILTER_SHOW_TASKS_DONE = "FILTER_SHOW_TASKS_DONE";
//        final String FILTER_SHOW_TASKS_CANCELLED = "FILTER_SHOW_TASKS_CANCELLED";
//        final String FILTER_SHOW_TASKS_WAITING = "FILTER_SHOW_TASKS_WAITING";
//        final String FILTER_HIDE_TASKS_ONGOING = "FILTER_HIDE_TASKS_ONGOING";
//        final String FILTER_SHOW_HIDE_UNTIL_DATE = "FILTER_SHOW_HIDE_UNTIL_DATE";
//        final String FILTER_SHOW_EXPIRES_ON_DATE = "FILTER_SHOW_EXPIRES_ON_DATE";
//        final String FILTER_SHOW_PROJECTS_ONLY = "FILTER_SHOW_PROJECTS_ONLY";
//        final String FILTER_SHOW_INTERRUPT_TASKS_ONLY = "FILTER_SHOW_INTERRUPT_TASKS_ONLY";
//        final String FILTER_SHOW_WITHOUT_ESTIMATES_ONLY = "FILTER_SHOW_WITHOUT_ESTIMATES_ONLY";
//        final String FILTER_SHOW_WITH_ACTUALS_ONLY = "FILTER_SHOW_WITH_ACTUALS";
//        
//        void putOrRemove(K key, V value, boolean addIfTrueElseRemove) {
//            if (addIfTrueElseRemove) {
//                put(key, value);
//            } else {
//                remove(key);
//            }
//        }
//        
//        void defaultQuery(ParseQuery query) {
////            put(FILTER_HIDE_TASKS_NEW, true);
////            put(FILTER_SHOW_TASKS_ONGOING, true);
////            put(FILTER_SHOW_TASKS_WAITING, false);
////            put(FILTER_SHOW_TASKS_DONE, false);
////            put(FILTER_SHOW_TASKS_CANCELLED, false);
////            put(FILTER_SHOW_HIDE_UNTIL_DATE, new Date());
////            put(FILTER_SHOW_EXPIRES_ON_DATE, new Date());
//        }
//        
//        /**
//         * add the filtering and sorting defined by the screen to the given
//         * query
//         *
//         * @param query
//         */
//        void updateQueryOld(ParseQuery query) {
//            if (!containsKey(FILTER_HIDE_TASKS_NEW)) {
//                query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CREATED.toString());
//            }
//            if (!containsKey(FILTER_SHOW_TASKS_DONE)) {
//                query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString());
//            }
//            if (!containsKey(FILTER_SHOW_TASKS_CANCELLED)) {
//                query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString());
//            }
//            if (!containsKey(FILTER_SHOW_TASKS_WAITING)) {
//                query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString());
//            }
//            if (!containsKey(FILTER_HIDE_TASKS_ONGOING)) {
//                query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.ONGOING.toString());
//            }
//            if (!containsKey(FILTER_SHOW_HIDE_UNTIL_DATE)) {
//                query.whereGreaterThanOrEqualTo(Item.PARSE_HIDE_UNTIL_DATE, get(FILTER_SHOW_HIDE_UNTIL_DATE));
//            }
//            if (!containsKey(FILTER_SHOW_EXPIRES_ON_DATE)) {
//                query.whereLessThan(Item.PARSE_EXPIRES_ON_DATE, new Date());
//            }
//            if (containsKey(FILTER_SHOW_PROJECTS_ONLY)) {
//                query.whereExists(Item.PARSE_SUBTASKS);
//                query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //show only top-level projects
//            }
//            if (containsKey(FILTER_SHOW_INTERRUPT_TASKS_ONLY)) {
//                query.whereEqualTo(Item.PARSE_INTERRUPT_TASK, true);
//            }
//            if (containsKey(FILTER_SHOW_WITHOUT_ESTIMATES_ONLY)) {
//                query.whereDoesNotExist(Item.PARSE_EFFORT_ESTIMATE);
//            }
//            if (containsKey(FILTER_SHOW_WITH_ACTUALS_ONLY)) {
//                query.whereExists(Item.PARSE_ACTUAL_EFFORT); //Choice for task status will determine which tasks are shown
//            }
//        }
//    }
//</editor-fold>
//    class FilterPredicate { //implements Predicate {
//        boolean apply(Object item) {
//        @Override
    /**
     * returns true if t should be shown
     *
     * @param t
     * @return
     */
    public boolean test(Object t) {
        Item item = (Item) t;
        ItemStatus status = item.getStatus();
        return showAll
        ||((status != ItemStatus.CREATED || showNewTasks)
                && (status != ItemStatus.ONGOING || showOngoingTasks)
                && (status != ItemStatus.WAITING || showWaitingTasks)
                && (status != ItemStatus.DONE || showDoneTasks)
                && (status != ItemStatus.CANCELLED || showCancelledTasks)
                //all the following conditions must ALL be met to show the item (or vice-versa if either is false, don't show)
                //TODO!!!! hideUntilDate should only compare on calendar date, not absolute time (unless the date stored 
                && (showBeforeHideUntilDate || item.getHideUntilDateD().getTime() == 0 || MyDate.currentTimeMillis() >= item.getHideUntilDateD().getTime()) //before now <=> hideUntil date is already passed so show the item
                && (showDependingOnUndoneTasks || item.isDependingOnTasksDone())
                && (showExpiresOnDate || item.getExpiresOnDate() == 0 || item.getExpiresOnDate() < MyDate.currentTimeMillis()) //before now <=> hideUntil date is already passed so show the item
                && (!showProjectsOnly || item.isProject()) //before now <=> hideUntil date is already passed so show the item
                && (!showInterruptTasksOnly || item.isInteruptOrInstantTask()) //before now <=> hideUntil date is already passed so show the item
                && (!showWithoutEstimatesOnly || !item.has(Item.PARSE_EFFORT_ESTIMATE)) //before now <=> hideUntil date is already passed so show the item
                && (!showWithActualsOnly || item.has(Item.PARSE_ACTUAL_EFFORT)) //before now <=> hideUntil date is already passed so show the item
                );

//            return false;
    }

    /**
     * add the filtering and sorting defined by the screen to the given query
     *
     * @param query
     */
//        void updateQuery(FilterPredicate fp, ParseQuery query) {
    void updateQuery(ParseQuery query) {
        FilterSortDef fp = this;
        if (!fp.showNewTasks) {
            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CREATED.toString());
        }
        if (!fp.showDoneTasks) {
            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString());
        }
        if (!fp.showCancelledTasks) {
            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString());
        }
        if (!fp.showWaitingTasks) {
            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString());
        }
        if (!fp.showOngoingTasks) {
            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.ONGOING.toString());
        }
        if (!fp.showBeforeHideUntilDate) {
            query.whereGreaterThanOrEqualTo(Item.PARSE_HIDE_UNTIL_DATE, new Date()); //new Date() <=> now
        }
        if (!fp.showDependingOnUndoneTasks) {
            //TODO!!!!! doesn't work 
            query.whereExists(Item.PARSE_DEPENDS_ON_TASK);
            query.whereGreaterThanOrEqualTo(Item.PARSE_DEPENDS_ON_TASK, new Date()); //new Date() <=> now
        }
        if (!fp.showExpiresOnDate) {
            query.whereLessThan(Item.PARSE_EXPIRES_ON_DATE, new Date());
        }
        if (fp.showProjectsOnly) {
            query.whereExists(Item.PARSE_SUBTASKS);
            query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //show only top-level projects
        }
        if (fp.showInterruptTasksOnly) {
            query.whereEqualTo(Item.PARSE_INTERRUPT_OR_INSTANT_TASK, true);
        }
        if (fp.showWithoutEstimatesOnly) {
            query.whereDoesNotExist(Item.PARSE_EFFORT_ESTIMATE);
        }
        if (fp.showWithActualsOnly) {
            query.whereExists(Item.PARSE_ACTUAL_EFFORT); //Choice for task status will determine which tasks are shown
        }
    }

    final static int compareDate(Date d1, Date d2) {
//        return (int) (d1.getTime() - d2.getTime());
        return compareLong(d1.getTime(), d2.getTime());
    }

    final static int compareInt(int d1, int d2) {
//            if (d1 < d2) {
//                return -1;
//            } else if (d2 < d1) {
//                return 1;
//            }
//            return 0;
        return d1 < d2 ? -1 : (d2 < d1 ? 1 : 0);
    }

    final static int compareDouble(double d1, double d2) {
//            if (d1 < d2) {
//                return -1;
//            } else if (d2 < d1) {
//                return 1;
//            }
//            return 0;
        return d1 < d2 ? -1 : (d2 < d1 ? 1 : 0);
    }

    final static int compareLong(long d1, long d2) {
//            if (d1 < d2) {
//                return -1;
//            } else if (d2 < d1) {
//                return 1;
//            }
//            return 0;
        return d1 < d2 ? -1 : (d2 < d1 ? 1 : 0);
//        return (int) (d1 - d2);
    }

    Comparator<Item> getSortingComparator() {
        return getSortingComparator(getSortFieldId(), isSortDescending());
    }

    /**
     * returns a comparator that compares on *multiple* comparators (up to 3
     * different)
     *
     * @param getSortFieldId
     * @param sortDescending
     * @return
     */
    static Comparator<Item> getMultipleComparator(String[] getSortFieldId, boolean[] sortDescending) {
        assert getSortFieldId.length >= 1 && getSortFieldId.length == sortDescending.length : "must be same length";
//        for (int i = 0, size = getSortFieldId.length; i < size; i++) {
//            Comparator<Item> comp1 = getSortingComparator(getSortFieldId[0], sortDescending[0]);
//        }
        Comparator<Item> comp1 = getSortFieldId.length >= 1 ? getSortingComparator(getSortFieldId[0], sortDescending[0]) : null;
        Comparator<Item> comp2 = getSortFieldId.length >= 2 ? getSortingComparator(getSortFieldId[1], sortDescending[1]) : null;
        Comparator<Item> comp3 = getSortFieldId.length >= 3 ? getSortingComparator(getSortFieldId[2], sortDescending[2]) : null;

        return (i1, i2) -> {
            int res1 = comp1.compare(i1, i2);
            if (res1 != 0) {
                return res1;
            } else {
                if (comp2 == null) {
//                    return 0; //TODO!!!! should compare eg objectId to ensure a consistent ordering on every sort
                    return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                } else {
                    int res2 = comp2.compare(i1, i2);
                    if (res2 != 0) {
                        return res2;
                    } else {
                        if (comp3 == null) {
//                            return 0;
                            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                        } else {
                            int res3 = comp3.compare(i1, i2);
                            if (res3 != 0) {
                                return res3;
                            } else {
//                                return 0;
                                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
                            }
                        }
                    }
                }
            }
        };
    }

    /**
     * compare DreadFun values which may be null. A null value is always smaller
     * than a defined value (to show last in sorted list)
     *
     * @param d1
     * @param d2
     * @return
     */
    private static int compareDreadFunValue(Item.DreadFunValue d1, Item.DreadFunValue d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (d2 == null) {
                return 1;
            } else {
                return (d1.compareTo(d2));
            }
        }
    }

    /**
     * compare items based on their categories. Categories are sorted relative
     * to each other based on their position in CategoryList. If an item has
     * multiple categories selected, then it is sorted on the first category in
     * the list. Items with no categories are sorted last.
     *
     * @param c1
     * @param c2
     * @return
     */
    private static int compareCategories(List<Category> c1, List<Category> c2) {
        if (c1.isEmpty()) {
            if (c2.isEmpty()) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (c2.isEmpty()) {
                return 1;
            } else {
                Category cat1 = c1.get(0);
                Category cat2 = c2.get(0);
                //compare the *first* category selected for each item
                //TODO!!!! instead of comparing by the order in the CategoryList, compare the categories that are sefirst in CategoryList (lets you set the 'importance' of categories when multiple are selected)
                return (compareInt(CategoryList.getInstance().indexOf(cat1), CategoryList.getInstance().indexOf(cat2)));
            }
        }
    }

    /**
     * compare items based on their owner ItemList. Owner ItemLists are sorted
     * relative to each other based on their position in ItemListList. items
     * with no owner ItemList are sorted last.
     *
     * @param ownerList1
     * @param ownerList2
     * @return
     */
    private static int compareOwnerList(ItemAndListCommonInterface ownerList1, ItemAndListCommonInterface ownerList2) {
        if (!(ownerList1 instanceof ItemList)) {
            if (!(ownerList2 instanceof ItemList)) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (!(ownerList2 instanceof ItemList)) {
                return 1;
            } else {
                ItemList ownerLst1 = (ItemList) ownerList1;
                ItemList ownerLst2 = (ItemList) ownerList2;
                //compare the *first* category selected for each item
                //TODO!!!! instead of comparing the first in the list, compare the categories that are first in CategoryList (lets you set the 'importance' of categories when multiple are selected)
                return (compareInt(ItemListList.getInstance().indexOf(ownerLst1), ItemListList.getInstance().indexOf(ownerLst2)));
            }
        }
    }

    private static int compareTopLevelProjectAlphabetically(Item ownerProject1, Item ownerProject2) {
        if (ownerProject1 == null) {
            if (ownerProject2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (ownerProject2 == null) {
                return 1;
            } else {
//                return (ownerProject1.getText().compareToIgnoreCase(ownerProject2.getText()));
                return (ownerProject1.getText().compareTo(ownerProject2.getText()));
            }
        }
    }

    private static int compareNullValueLast(Object d1, Object d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else {
                return -1;
            }
        } else {
            if (d2 == null) {
                return 1;
            } else if (d1 instanceof Comparable && d2 instanceof Comparable) {
                return ((Comparable) d1).compareTo((Comparable) d2);
            } else {
                return 0; //one or other is not Comparable
            }
        }
    }

    static Comparator<Item> getSortingComparator(String getSortFieldId, boolean sortDescending) {
//        boolean sortDescending = isSortDescending();
//        switch (getSortFieldId()) {
        switch (getSortFieldId) {
//                case Item.PARSE_PRIORITY:
//                    return sortAscending?(i1, i2) -> Integer.compare(i1.getPriority(), i2.getPriority()):(i1, i2) -> Integer.compare(i2.getPriority(), i1.getPriority());
            case "":
                return (i1, i2) -> 0; //no sorting
            case Item.PARSE_STATUS:
                return sortDescending
                        ? (i1, i2) -> i1.getStatus().compareTo(i2.getStatus())
                        : (i1, i2) -> i2.getStatus().compareTo(i1.getStatus());
            case Item.PARSE_PRIORITY:
                return sortDescending
                        ? (i1, i2) -> compareInt(i1.getPriority(), i2.getPriority()) //show highest on top
                        : (i1, i2) -> compareInt(i2.getPriority(), i1.getPriority());
            case Item.PARSE_DUE_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getDueDate(), i1.getDueDate())
                        : (i1, i2) -> compareLong(i1.getDueDate(), i2.getDueDate());
            case Item.PARSE_REMAINING_EFFORT:
                return sortDescending
                        ? (i1, i2) -> compareLong(i1.getRemaining(), i2.getRemaining()) //show lowest at top
                        : (i1, i2) -> compareLong(i2.getRemaining(), i1.getRemaining());
            case Item.PARSE_EFFORT_ESTIMATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i1.getEstimate(), i2.getEstimate()) //show lowest at top
                        : (i1, i2) -> compareLong(i2.getEstimate(), i1.getEstimate());
            case Item.PARSE_CHALLENGE:
                return sortDescending
                        //                        ? (i1, i2) -> i1.getChallengeN().compareTo(i2.getChallengeN())
                        //                        : (i1, i2) -> i2.getChallengeN().compareTo(i1.getChallengeN());
                        ? (i1, i2) -> compareNullValueLast(i1.getChallengeN(), i2.getChallengeN())
                        : (i1, i2) -> compareNullValueLast(i2.getChallengeN(), i1.getChallengeN());
            case Item.PARSE_DREAD_FUN_VALUE:
                return sortDescending
                        //                        ? (i1, i2) -> i1.getDreadFunValueN().compareTo(i2.getDreadFunValueN())
                        //                        : (i1, i2) -> i2.getDreadFunValueN().compareTo(i1.getDreadFunValueN());
                        //                        ? (i1, i2) -> compareDreadFunValue(i1.getDreadFunValueN(), i2.getDreadFunValueN())
                        //                        : (i1, i2) -> compareDreadFunValue(i2.getDreadFunValueN(), i1.getDreadFunValueN());
                        ? (i1, i2) -> compareNullValueLast(i1.getDreadFunValueN(), i2.getDreadFunValueN())
                        : (i1, i2) -> compareNullValueLast(i2.getDreadFunValueN(), i1.getDreadFunValueN());
            case Item.PARSE_EARNED_VALUE:
                return sortDescending
                        ? (i1, i2) -> compareDouble(i1.getEarnedValue(), i2.getEarnedValue()) //show highest on top
                        : (i1, i2) -> compareDouble(i2.getEarnedValue(), i1.getEarnedValue());
            case Item.PARSE_START_BY_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getStartByDateD().getTime(), i1.getStartByDateD().getTime())
                        : (i1, i2) -> compareLong(i1.getStartByDateD().getTime(), i2.getStartByDateD().getTime());
            case Item.PARSE_STARTED_ON_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getStartedOnDate(), i1.getStartedOnDate())
                        : (i1, i2) -> compareLong(i1.getStartedOnDate(), i2.getStartedOnDate());
            case Item.PARSE_WAITING_TILL_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getWaitingTillDateD().getTime(), i1.getWaitingTillDateD().getTime())
                        : (i1, i2) -> compareLong(i1.getWaitingTillDateD().getTime(), i2.getWaitingTillDateD().getTime());
            case Item.PARSE_UPDATED_AT:
                return sortDescending
                        ? (i1, i2) -> compareDate(i2.getUpdatedAt(), i1.getUpdatedAt())
                        : (i1, i2) -> compareDate(i1.getUpdatedAt(), i2.getUpdatedAt());
            case Item.PARSE_CREATED_AT:
                return sortDescending
                        ? (i1, i2) -> compareDate(i1.getCreatedAt(), i2.getCreatedAt()) //oldest first
                        : (i1, i2) -> compareDate(i2.getCreatedAt(), i1.getCreatedAt());
            case Item.PARSE_COMPLETED_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i1.getCompletedDate(), i2.getCompletedDate()) //oldest first
                        : (i1, i2) -> compareLong(i2.getCompletedDate(), i1.getCompletedDate());
            case Item.PARSE_TEXT:
                return sortDescending
                        ? (i1, i2) -> i2.getText().compareTo(i1.getText()) //show alphabetically, lowest value at top
                        : (i1, i2) -> i1.getText().compareTo(i2.getText());
            case Item.PARSE_IMPORTANCE_URGENCY:
                return sortDescending
                        ? (i1, i2) -> compareInt(i1.getImpUrgPrioValue(), i2.getImpUrgPrioValue()) //show highest values at top
                        : (i1, i2) -> compareInt(i2.getImpUrgPrioValue(), i1.getImpUrgPrioValue());
            case FILTER_SORT_TODAY_VIEW:
//                                return (i1, i2) -> 0; //no sorting, arrive sorted from parse query
                return (i1, i2) -> i1.getTodaySortOrder().compareTo(i2.getTodaySortOrder());
            case Item.PARSE_CATEGORIES:
                return sortDescending
                        ? (i1, i2) -> compareCategories(i1.getCategories(), i2.getCategories()) //show highest values at top
                        : (i1, i2) -> compareCategories(i2.getCategories(), i1.getCategories());
            case Item.PARSE_OWNER_LIST:
                return sortDescending
                        ? (i1, i2) -> compareOwnerList(i1.getOwner(), i2.getOwner()) //show highest values at top
                        : (i1, i2) -> compareOwnerList(i2.getOwner(), i1.getOwner());
            case Item.PARSE_OWNER_ITEM:
                return sortDescending
                        ? (i1, i2) -> compareTopLevelProjectAlphabetically(i1.getOwnerTopLevelProject(), i2.getOwnerTopLevelProject()) //show highest values at top
                        : (i1, i2) -> compareTopLevelProjectAlphabetically(i2.getOwnerTopLevelProject(), i1.getOwnerTopLevelProject());
//            case FILTER_SORT_OWNER:
//                return (i1, i2) -> compareInt(DAO.getInstance().getItemListList().indexOf(i1.getO),DAO.getInstance().getItemListList().indexOf(i2)); //sort lists on position in ItemListList
            default:
                assert false : "Sort on unsupported field";
        }
        return null;
    }

//    }
//    public static ArrayList<Item> filter(Collection<Item> target, FilterPredicate predicate) {
//    public static ArrayList<Item> filter(Collection<Item> target, FilterSortDef predicate ) {
//    public ArrayList<Item> filter(Collection<Item> target) {
    public ArrayList<Item> filter(Collection<ItemAndListCommonInterface> target) {
        FilterSortDef predicate = this;
        ArrayList filteredCollection = new ArrayList();
//        for (Item t : target) {
        for (ItemAndListCommonInterface t : target) {
            if (t instanceof Item) { //can also be a WorkSlot in Today view, then simply don't apply any filter
                if (predicate.test(t)) {
                    filteredCollection.add(t);
                }
            } else {
                filteredCollection.add(t);
            }
        }
        return filteredCollection;
    }

//    public List filterAndSortList(List orgList) {
////        orgList.sort(null);
//        List<Item> filteredAndSorted = filter(orgList, filterPredicate);
//        if (isSortOn()) {
//            Collections.sort(filteredAndSorted, filterPredicate.getSortingComparator());
//        }
////         Collections.sort(filteredAndSorted);
//        return filteredAndSorted;
////        return orgList;
//    }
    /**
     * returns the same list sorted inline (NB. So never use to sort manually
     * sorted lists)
     *
     * @param filteredList
     * @return
     */
    public List sortItemListInLine(List filteredList) {
        Comparator comp = getSortingComparator();
//        Collections.sort(filteredList, filterPredicate.getSortingComparator());
        if (comp != null) { //skip sort if no sorting set (e.g. Manual)
            Collections.sort(filteredList, comp);
        }
        return filteredList; //
    }

    /**
     * returns a filtered copy of the list
     *
     * @param orgList
     * @return
     */
    public List filterItemList(List orgList) {
//        List<Item> filteredList = new ArrayList(filter(orgList.getList())); //filter the underlying list and create a new ItemList
        List<Item> filteredList = new ArrayList(filter(orgList)); //filter the underlying list and create a new ItemList
//        filteredList.setSourceItemList(orgList);
        return filteredList;
    }

    /**
     * returns a filtered and sorted *copy* of the list
     *
     * @param orgList
     * @return
     */
//    public List<? extends ItemAndListCommonInterface> filterAndSortItemList(List orgList) {
    public List<ItemAndListCommonInterface> filterAndSortItemList(List orgList) {
//        ItemList<Item> filteredAndSorted = new ItemList(filter(orgList, filterPredicate));
//        Collections.sort(filteredAndSorted, filterPredicate.getSortingComparator());
//        filterAndSortList(filteredAndSorted);
        if (isSortOn()) {
            return sortItemListInLine(filterItemList(orgList));
        } else {
            return filterItemList(orgList);
        }
//        return filteredAndSorted;
    }

    @Override
    public void save() throws ParseException {
        putFilterOptions();
        super.save();
    }

    /**
     * @return the showAll
     */
    public boolean isShowAll() {
        initFilterOptions();
        return showAll;
    }

    /**
     * @param showAll the showAll to set
     */
    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    /**
     * @return the showDefault
     */
    public boolean isShowDefault() {
        initFilterOptions();
        return showDefault;
    }

    /**
     * @param showDefault the showDefault to set
     */
    public void setShowDefault(boolean showDefault) {
        this.showDefault = showDefault;
        putFilterOptions();
    }

    /**
     * @return the showNewTasks
     */
    public boolean isShowNewTasks() {
        initFilterOptions();
        return showNewTasks;
    }

    /**
     * @param showNewTasks the showNewTasks to set
     */
    public void setShowNewTasks(boolean showNewTasks) {
        this.showNewTasks = showNewTasks;
        putFilterOptions();
    }

    /**
     * @return the showOngoingTasks
     */
    public boolean isShowOngoingTasks() {
        initFilterOptions();
        return showOngoingTasks;
    }

    /**
     * @param showOngoingTasks the showOngoingTasks to set
     */
    public void setShowOngoingTasks(boolean showOngoingTasks) {
        this.showOngoingTasks = showOngoingTasks;
        putFilterOptions();
    }

    /**
     * @return the showWaitingTasks
     */
    public boolean isShowWaitingTasks() {
        initFilterOptions();
        return showWaitingTasks;
    }

    /**
     * @param showWaitingTasks the showWaitingTasks to set
     */
    public void setShowWaitingTasks(boolean showWaitingTasks) {
        this.showWaitingTasks = showWaitingTasks;
        putFilterOptions();
    }

    /**
     * @return the showDoneTasks
     */
    public boolean isShowDoneTasks() {
        initFilterOptions();
        return showDoneTasks;
    }

    /**
     * @param showDoneTasks the showDoneTasks to set
     */
    public void setShowDoneTasks(boolean showDoneTasks) {
        this.showDoneTasks = showDoneTasks;
        putFilterOptions();
    }

    /**
     * @return the showCancelledTasks
     */
    public boolean isShowCancelledTasks() {
        initFilterOptions();
        return showCancelledTasks;
    }

    /**
     * @param showCancelledTasks the showCancelledTasks to set
     */
    public void setShowCancelledTasks(boolean showCancelledTasks) {
        this.showCancelledTasks = showCancelledTasks;
        putFilterOptions();
    }

    /**
     * @return the showBeforeHideUntilDate
     */
    public boolean isShowBeforeHideUntilDate() {
        initFilterOptions();
        return showBeforeHideUntilDate;
    }

    /**
     * @param showBeforeHideUntilDate the showBeforeHideUntilDate to set
     */
    public void setShowBeforeHideUntilDate(boolean showBeforeHideUntilDate) {
        this.showBeforeHideUntilDate = showBeforeHideUntilDate;
        putFilterOptions();
    }

    /**
     * @return the showDependingOnUndoneTasks
     */
    public boolean isShowDependingOnUndoneTasks() {
        initFilterOptions();
        return showDependingOnUndoneTasks;
    }

    /**
     * @param showDependingOnUndoneTasks the showDependingOnUndoneTasks to set
     */
    public void setShowDependingOnUndoneTasks(boolean showDependingOnUndoneTasks) {
        this.showDependingOnUndoneTasks = showDependingOnUndoneTasks;
        putFilterOptions();
    }

    /**
     * @return the showExpiresOnDate
     */
    public boolean isShowExpiresOnDate() {
        initFilterOptions();
        return showExpiresOnDate;
    }

    /**
     * @param showExpiresOnDate the showExpiresOnDate to set
     */
    public void setShowExpiresOnDate(boolean showExpiresOnDate) {
        this.showExpiresOnDate = showExpiresOnDate;
        putFilterOptions();
    }

    /**
     * @return the showProjectsOnly
     */
    public boolean isShowProjectsOnly() {
        initFilterOptions();
        return showProjectsOnly;
    }

    /**
     * @param showProjectsOnly the showProjectsOnly to set
     */
    public void setShowProjectsOnly(boolean showProjectsOnly) {
        this.showProjectsOnly = showProjectsOnly;
        putFilterOptions();
    }

    /**
     * @return the showInterruptTasksOnly
     */
    public boolean isShowInterruptTasksOnly() {
        initFilterOptions();
        return showInterruptTasksOnly;
    }

    /**
     * @param showInterruptTasksOnly the showInterruptTasksOnly to set
     */
    public void setShowInterruptTasksOnly(boolean showInterruptTasksOnly) {
        this.showInterruptTasksOnly = showInterruptTasksOnly;
        putFilterOptions();
    }

    /**
     * @return the showWithoutEstimatesOnly
     */
    public boolean isShowWithoutEstimatesOnly() {
        initFilterOptions();
        return showWithoutEstimatesOnly;
    }

    /**
     * @param showWithoutEstimatesOnly the showWithoutEstimatesOnly to set
     */
    public void setShowWithoutEstimatesOnly(boolean showWithoutEstimatesOnly) {
        this.showWithoutEstimatesOnly = showWithoutEstimatesOnly;
        putFilterOptions();
    }

    /**
     * @return the showWithActualsOnly
     */
    public boolean isShowWithActualsOnly() {
        initFilterOptions();
        return showWithActualsOnly;
    }

    /**
     * @param showWithActualsOnly the showWithActualsOnly to set
     */
    public void setShowWithActualsOnly(boolean showWithActualsOnly) {
        this.showWithActualsOnly = showWithActualsOnly;
        putFilterOptions();
    }

    /**
     * @return the sortOptions
     */
    public static String[] getSortOptions() {
        return sortOptions;
    }

    /**
     * @param aSortOptions the sortOptions to set
     */
    public static void setSortOptions(String[] aSortOptions) {
        sortOptions = aSortOptions;
    }

    /**
     * @return the sortField
     */
    public static String[] getSortField() {
        return sortField;
    }

    /**
     * @param aSortField the sortField to set
     */
    public static void setSortField(String[] aSortField) {
        sortField = aSortField;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
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
//        return (date == null) ? new Date(0) : date;
        return date; //return null to indicate NOT deleted
    }

    public boolean isDeleted() {
        return getDeletedDate() != null;
    }

    public boolean softDelete(boolean removeReferences) {
        setDeletedDate(new Date());
        DAO.getInstance().saveInBackground(this);
        return true;
    }

    public boolean softDelete() {
        return softDelete(true);
    }
}
