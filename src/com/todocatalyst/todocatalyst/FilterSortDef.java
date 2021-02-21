/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
//import static com.todocatalyst.todocatalyst.Item.PARSE_DELETED_DATE;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Requirements: Filter any field and any value => store list of values to show
 * in a text string per field. Create expressions with AND/OR etc (find old
 * code).
 *
 * @author Thomas
 */
public class FilterSortDef extends ParseObject {

    private Comparator nonSavedComparator = null;
    private boolean isNoSave = false;
    private String cmdUniqueId = null;
//    private static FilterSortDef defaultFilter = null;

//    private static FilterSortDef DEFAULT_FILTER = null; //no good to use an (editable) filter with singleton, since edits will change the original. Must use separate instances and equal() to compare
    //TODO save filters to parse (with the list, not the view?!). Save each field to make it easy to understand what filters users use)
    //TODO save filter either as for the screen (applied to all lists shown) or specific to the list (later option since less intuitive)
    //TODO filter on 'leaf tasks' only
    //TODO introduce default FilterSortDef (definable by end-user) so that every new filter (e.g. a new list of tasks) get the desired settings
//<editor-fold defaultstate="collapsed" desc="static strings">
    public static String CLASS_NAME = "FilterSortDef";
    static String PARSE_SORT_FIELD = "sortField";
    static String PARSE_SORT_DESCENDING = "sortDescending";
    static String PARSE_SORT_ON = "sortActive";
    static String PARSE_FILTER_OPTIONS = "filterOptions"; //the specific definition of the filter
//    private static String PARSE_SCREEN_ID = "ScreenId";
//    private static String PARSE_FILTERED_OBJECT_ID = "filteredObjectId";
//    private static String PARSE_FILTERED_OBJECT = "filteredObject";
    static String PARSE_FILTER_NAME = "name"; //name of filter
    static String PARSE_FILTER_DESCRIPTION = "description"; //longer text description the *purpose*/*benefit* of the filter
    static String PARSE_FILTER_HELP = "help"; //help text
//    private static String PARSE_FILTER_DEFINITION = "definition"; //short *definition* (how exactly is it calculate) of the filter
    static String PARSE_SYSTEM_NAME = ItemList.PARSE_SYSTEM_NAME; //systemname for filters for e.g. Next, Inbox, Alltasks, ...
    static String PARSE_DELETED_DATE = Item.PARSE_DELETED_DATE; //systemname for filters for e.g. Next, Inbox, Alltasks, ...
//    private static String PARSE_FILTER_PREDEFINED = "predefined";
//    public static String PARSE_SORT_FILTER_ID = "filterId"; //name of screen and objectId of object displayed
//    final static String PARSE_DELETED = "deleted"; //has this object been deleted on some device?
//    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?

    /**
     * need true equals implementation to detect whether the default filter has
     * been modified (in which case it needs to be saved)
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FilterSortDef)) {
            return false; //covers obj==null
        }
        if (this == obj) {
            return true;
        }
        FilterSortDef filterSortDef = (FilterSortDef) obj;
//        if (!Objects.equals(this.getObjectIdP(), filterSortDef.getObjectIdP())) {
//            return false;
//        }
        if (!Objects.equals(getFilterOptionsFromParse(), filterSortDef.getFilterOptionsFromParse())) {
            return false;
        }
        if (!Objects.equals(getSortFieldId(), filterSortDef.getSortFieldId())) {
            return false;
        }
        if (!Objects.equals(isSortDescending(), filterSortDef.isSortDescending())) {
            return false;
        }
        if (isSortOn() != filterSortDef.isSortOn()) {
            return false;
        }
        if (!Objects.equals(getFilterName(), filterSortDef.getFilterName())) {
            return false;
        }
//        if (!Objects.equals(getDefinition(), filterSortDef.getDefinition())) {
//            return false;
//        }
        if (!Objects.equals(getDescription(), filterSortDef.getDescription())) {
            return false;
        }
        if (!Objects.equals(getHelp(), filterSortDef.getHelp())) {
            return false;
        }
        return true;
    }

    public boolean update(FilterSortDef filterSortDef) {
        boolean updated = false;
        if (filterSortDef == null) {
            return updated;
        }

        if (!Objects.equals(getFilterOptionsFromParse(), filterSortDef.getFilterOptionsFromParse())) {
//            setAndExtractFilterOptions(filterSortDef.getFilterOptionsFromParse());
            setFilterOptionsInParseAndUpdateBools(filterSortDef.getFilterOptionsFromParse());
            updated = true;
        }
        if (!Objects.equals(getSortFieldId(), filterSortDef.getSortFieldId())) {
            setSortFieldId(filterSortDef.getSortFieldId());
            updated = true;
        }
        if (!Objects.equals(isSortDescending(), filterSortDef.isSortDescending())) {
            setSortDescending(filterSortDef.isSortDescending());
            updated = true;
        }
        if (isSortOn() != filterSortDef.isSortOn()) {
            setSortOn(filterSortDef.isSortOn());
            updated = true;
        }
        if (!Objects.equals(getFilterName(), filterSortDef.getFilterName())) {
            setFilterName(filterSortDef.getFilterName());
            updated = true;
        }
//        if (!Objects.equals(getDefinition(), filterSortDef.getDefinition())) {
//            setDefinition(filterSortDef.getDefinition());
//            updated = true;
//        }
        if (!Objects.equals(getDescription(), filterSortDef.getDescription())) {
            setDescription(filterSortDef.getDescription());
            updated = true;
        }
        if (!Objects.equals(getHelp(), filterSortDef.getHelp())) {
            setHelp(filterSortDef.getHelp());
            updated = true;
        }
        return updated;
    }

    @Override
    public int hashCode() {
        int hash = 7;
//<editor-fold defaultstate="collapsed" desc="autogenerated">
//        hash = 23 * hash + Objects.hashCode(this.nonSavedComparator);
//        hash = 23 * hash + (this.showInitialized ? 1 : 0);
//        hash = 23 * hash + (this.showAll ? 1 : 0);
//        hash = 23 * hash + (this.showDefault ? 1 : 0);
//        hash = 23 * hash + (this.showNewTasks ? 1 : 0);
//        hash = 23 * hash + (this.showOngoingTasks ? 1 : 0);
//        hash = 23 * hash + (this.showWaitingTasks ? 1 : 0);
//        hash = 23 * hash + (this.showDoneTasks ? 1 : 0);
//        hash = 23 * hash + (this.showCancelledTasks ? 1 : 0);
//        hash = 23 * hash + (this.showBeforeHideUntilDate ? 1 : 0);
//        hash = 23 * hash + (this.showDependingOnUndoneTasks ? 1 : 0);
//        hash = 23 * hash + (this.showExpiresOnDate ? 1 : 0);
//        hash = 23 * hash + (this.showProjectsOnly ? 1 : 0);
//        hash = 23 * hash + (this.showInterruptTasksOnly ? 1 : 0);
//        hash = 23 * hash + (this.showWithoutEstimatesOnly ? 1 : 0);
//        hash = 23 * hash + (this.showWithActualsOnly ? 1 : 0);
//</editor-fold>
        hash = 23 * hash + Objects.hashCode(getFilterOptionsFromParse());
        hash = 23 * hash + Objects.hashCode(getSortFieldId());
        hash = 23 * hash + (isSortDescending() ? 1 : 0);
        hash = 23 * hash + (isSortOn() ? 1 : 0);
        hash = 23 * hash + Objects.hashCode(getFilterName());
        return hash;
    }

//<editor-fold defaultstate="collapsed" desc="old definitions">
//    public static String FILTER_SHOW_NEW_TASKS = "showNewTasks";
//    public static String FILTER_SHOW_ONGOING_TASKS = "showOngoingTasks";
//    public static String FILTER_SHOW_WAITING_TASKS = "showWaitingTasks";
//    public static String FILTER_SHOW_DONE_TASKS = "showDoneTasks";
//    public static String FILTER_SHOW_CANCELLED_TASKS = "showCancelledTasks";
//    public static String FILTER_SHOW_BEFORE_HIDE_UNTILDATE = "showBeforeHideUntilDate";
//    public static String FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS = "hideDependingOnTasks";
//    public static String FILTER_SHOW_EXPIRES_ON_DATE = "showExpiresOnDate";
//    public static String FILTER_SHOW_PROJECTS_ONLY = "showProjectsOnly";
//    public static String FILTER_SHOW_INTERRUPT_TASKS_ONLY = "showInterruptTasksOnly";
//    public static String FILTER_SHOW_WITHOUT_ESTIMATES_ONLY = "showWithoutEstimatesOnly";
//    public static String FILTER_SHOW_WITH_ACTUALS_ONLY = "showWithActualsOnly";
//    public static String FILTER_SHOW_ALL = "showAll";
//</editor-fold>
    public static String FILTER_SHOW_NEW_TASKS = "New";
    public static String FILTER_SHOW_ONGOING_TASKS = "Ongoing";
    public static String FILTER_SHOW_WAITING_TASKS = "Waiting";
    public static String FILTER_SHOW_DONE_TASKS = "Done";
    public static String FILTER_SHOW_DONE_TILL_MIDNIGHT = "TillMidnight";
    public static String FILTER_SHOW_CANCELLED_TASKS = "Cancelled";

    public static String FILTER_SHOW_BEFORE_HIDE_UNTILDATE = "BeforeHideUntil";
    public static String FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS = "DependingOnTasks";
    public static String FILTER_SHOW_EXPIRES_ON_DATE = "ExpiresOn";

    public static String FILTER_SHOW_PROJECTS_ONLY = "Projects";
    public static String FILTER_SHOW_INTERRUPT_TASKS_ONLY = "InterruptTasks";
    public static String FILTER_SHOW_STARRED_TASKS_ONLY = "Starred";
    public static String FILTER_SHOW_WITHOUT_ESTIMATES_ONLY = "WithoutEstimates";
    public static String FILTER_SHOW_WITH_ACTUALS_ONLY = "WithActuals";
    public static String FILTER_SHOW_WITH_REMAINING_ONLY = "WithRemaining";
    public static String FILTER_SHOW_ALL = "All";

    public final static String FILTER_SORT_TODAY_VIEW = "TODAY_VIEW";
//    public final static String FILTER_SORT_OWNER = "SORT_ON_OWNER";

//    private boolean showInitialized; // = false;
    private boolean initialized; // = true;
    private boolean showAll; // = true;
//    private boolean showDefault;// = true;
    private boolean showNewTasks;// = true;
    private boolean showOngoingTasks;// = true;
    private boolean showWaitingTasks;
    private boolean showDoneTasks;
    private boolean showDoneTillMidnight;
    private boolean showCancelledTasks;
    private boolean showBeforeHideUntilDate;
    private boolean showDependingOnUndoneTasks;
    private boolean showExpiresOnDate;
    private boolean showProjectsOnly;
    private boolean showInterruptTasksOnly;
    private boolean showStarredTasksOnly;
    private boolean showWithoutEstimatesOnly;
    private boolean showWithActualsOnly;
    private boolean showWithRemainingOnly;
    private boolean showChallengeEasy;
    private boolean showChallengeHard;
//</editor-fold>

    /**
     * returns a neutral filter (no filtering or sorting defined)
     */
    public FilterSortDef() {
        super(CLASS_NAME);
//        extractAndSetFilterOptions(); //NO good here, since constructor called when object is created, before any data is loaded. //Need to initialize these whenever a filter is created eg by reading in a cached value
//        initFilterOptions();
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
    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending, String filterName,
            String description, /*String definition,*/ String help) {
        this(sortParseFieldId, filterOptions, sortOn, sortDescending, filterName, description, help, null);
    }

    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending, String filterName,
            String description, /*String definition,*/ String help, String systemName) {
        this();
        setSortFieldId(sortParseFieldId);
//        setSortOn(sortParseFieldId != null && !sortParseFieldId.equals(""));
        setSortOn(sortOn);
        setSortDescending(sortDescending);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterOptions != null) {
////            extractAndSetFilterOptions(filterOptions);
////            saveCurrentlyActiveFilterOptions();
//            setFilterOptions(filterOptions);
//        }
//        else {
//            getFilterOptions();
//        }
//</editor-fold>
//        setAndExtractFilterOptions(filterOptions);
        setFilterOptionsInParseAndUpdateBools(filterOptions);

        setFilterName(filterName);
        setDescription(description);
//        setDefinition(definition);
        setHelp(help);
        setSystemName(systemName);
    }

    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending, String description) {
        this(sortParseFieldId, filterOptions, sortOn, sortDescending, "", description, "");
    }

    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending, boolean isNoSave) {
        this(sortParseFieldId, filterOptions, sortOn, sortDescending, "");
        this.isNoSave = isNoSave;
    }

    public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending) {
        this(sortParseFieldId, filterOptions, sortOn, sortDescending, false);
    }

    public FilterSortDef(String systemName, String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending) {
        this(sortParseFieldId, filterOptions, sortOn, sortDescending, "", "", "", systemName);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public FilterSortDef(Comparator<Item> sorter, String filterOptions, String description) {
//        this(PARSE_SORT_FIELD, filterOptions, showDoneTasks, description);
////        setSortFieldId(sortParseFieldId);
////        setSortOn(sortParseFieldId != null && !sortParseFieldId.equals(""));
//        setSortingComparator(sorter);
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (filterOptions != null) {
//////            extractAndSetFilterOptions(filterOptions);
//////            saveCurrentlyActiveFilterOptions();
////            setFilterOptions(filterOptions);
////        }
////        else {
////            getFilterOptions();
////        }
////        setSortDescending(sortDescending);
////</editor-fold>
//        setAndExtractFilterOptions(filterOptions);
//        setDescription(description);
//    }
//</editor-fold>

    public FilterSortDef(FilterSortDef filterToCopy) {
//        this();
        this(filterToCopy.getSortFieldId(), filterToCopy.getFilterOptionsFromParse(), filterToCopy.isSortOn(), filterToCopy.isSortDescending(), filterToCopy.getFilterName(),
                filterToCopy.getDescription(), filterToCopy.getHelp());
//<editor-fold defaultstate="collapsed" desc="comment">
//        setSortFieldId(filterToCopy.getSortFieldId());
//        setAndExtractFilterOptions(filterToCopy.getFilterOptions());
//        setSortOn(filterToCopy.isSortOn());
//        setSortDescending(filterToCopy.isSortDescending());
//
//        setFilterName(filterToCopy.getFilterName());
//        setDescription(filterToCopy.getDescription());
//        setDefinition(filterToCopy.getDefinition());
//        setHelp(filterToCopy.getHelp());
//</editor-fold>
    }

//    public FilterSortDef(String cmdUniqueId, String definition, String helpText) {
    public FilterSortDef(String filterName, String description, String helpText) {
        this();
//        setCmdUniqueId(cmdUniqueId);
        setFilterName(filterName);
        setDescription(description);
        setHelp(helpText);
    }

    public void setCmdUniqueId(String cmdUniqueId) {
        this.cmdUniqueId = cmdUniqueId;
    }

    public String getCmdUniqueId() {
        return cmdUniqueId;
    }

    @Override
    public String toString() {
        String s = "";
        s += "\"" + getFilterOptionsFromParse() + "\"";
//        s += " [" + (getObjectIdP() != null ? getObjectIdP() : (isNoSave() ? "NoSave" : "null")) + "]";
        s += " [" + (getObjectIdP() != null ? getObjectIdP() : "NoObjId") + "/" + getGuid() + "]";
        s += " SORT:" + getSortFieldId() + (isSortOn() ? "/ON" : "/off") + (isSortDescending() ? "/DESC" : "/ASCEN");
//        s += isNoSave() ? "|NoSave" : "";
        return s;
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public FilterSortDef(Comparator<Item> sorter, String filterOptions, String description) {
//        this();
//        setFilterName(description);
//        setDescription(description);
//    }

    /**
     * sets default filter: no sorting, show all tasks
     */
//    void setDefaultsXXX() {
//        setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
//        setSortDescending(false);
//        setSortOn(false); //don't sort by default,
////        extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS + FILTER_SHOW_DONE_TASKS + FILTER_SHOW_CANCELLED_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
////        saveCurrentlyActiveFilterOptions();
//        setFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS + FILTER_SHOW_DONE_TASKS + FILTER_SHOW_CANCELLED_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
//    }
//    private static FilterSortDef defaultDoneTasksFilter
//            = new FilterSortDef(null, FilterSortDef.FILTER_SHOW_NEW_TASKS + FilterSortDef.FILTER_SHOW_ONGOING_TASKS
//                    + FilterSortDef.FILTER_SHOW_WAITING_TASKS, false); //no sorting, //TODO!! Move this filter to FilterSortDef.getDeafultFilter() and reuse everywhere
//</editor-fold>
    /**
     * return default task filter, unsorted, hiding Done/Cancelled tasks. A new
     * instance each time, so if it is modified (and saved) locally it won't
     * affect the other default filters.
     *
     * @return
     */
    static FilterSortDef getDefaultFilter() {
//        if (DEFAULT_FILTER == null) {
        FilterSortDef filter = new FilterSortDef();
        filter.setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
        filter.setSortDescending(false);
        filter.setSortOn(false); //don't sort by default, 
//        filter.extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
//        filter.saveCurrentlyActiveFilterOptions();
//        filter.setFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS 
//                + FILTER_SHOW_WAITING_TASKS + FILTER_SHOW_DONE_TILL_MIDNIGHT);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
        filter.setShowNewTasks(true);
        filter.setShowOngoingTasks(true);
        filter.setShowDoneTillMidnight(true);
        filter.setShowWaitingTasks(true);
//        public FilterSortDef(String sortParseFieldId, String filterOptions, boolean sortOn, boolean sortDescending, String filterName,
//            String description, /*String definition,*/ String help, String systemName) {
//        return new FilterSortDef(Item.PARSE_DUE_DATE, FILTER_SHOW_NEW_TASKS, false, false, CLASS_NAME, PARSE_SORT_ON, CLASS_NAME, CLASS_NAME);
//            DEFAULT_FILTER = filter;
//        }
//        return DEFAULT_FILTER;
        return filter;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    static FilterSortDef getNeutralFilterXXX() {
//        FilterSortDef filter = new FilterSortDef();
//        filter.setSortFieldId(null); //show sort on DUE as default option *if* setting sortOn
//        filter.setFilterOptions(FILTER_SHOW_ALL);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
//        return filter;
//    }
//    static FilterSortDef getNeutralFilterXXX() {
//        FilterSortDef filter = new FilterSortDef();
//        filter.setSortFieldId(null); //show sort on DUE as default option *if* setting sortOn
//        filter.setFilterOptions(FILTER_SHOW_ALL);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
//        return filter;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * return default task filter, unsorted, hiding Done/Cancelled tasks.
     *
     * @return
     */
//    static FilterSortDef getDefaultFilterNuetral() {
//        FilterSortDef filter = new FilterSortDef();
////            filter.setSortFieldId(Item.PARSE_DUE_DATE); //show sort on DUE as default option *if* setting sortOn
////            filter.setSortOn(false); //don't sort by default,
////            filter.setSortDescending(false);
////            filter.extractAndSetFilterOptions(FILTER_SHOW_NEW_TASKS + FILTER_SHOW_ONGOING_TASKS + FILTER_SHOW_WAITING_TASKS);//when creating filter first time, show all tasks (to avoid that tasks suddenly disappear in the list)
////            filter.putFilterOptions();
//        return filter;
//    }
//    static String[] sortOptions = new String[]{
//        "Priority", "Due date", "Remaining time",
//        "Difficulty", "Fun", "Value",
//        "Start by date", "Date work on tasks started",
//        "Date last modified", "Date created", "Date completed",
//        "Task text", "Importance/Urgency", "Status"};
//</editor-fold>
    private static String[] sortOptions = new String[]{
        Item.DUE_DATE,
        Item.PRIORITY,
        Item.EFFORT_REMAINING,
        Item.EFFORT_ESTIMATE,
        Item.EFFORT_ACTUAL,
        Item.CHALLENGE,
        Item.DREAD_FUN,
        Item.EARNED_VALUE,
        Item.START_BY_TIME,
        Item.STARTED_ON_DATE,
        Item.UPDATED_DATE,
        Item.CREATED_DATE,
        Item.COMPLETED_DATE,
        Item.WAIT_UNTIL_DATE,
        Item.DESCRIPTION,
        Item.IMPORTANCE_URGENCY,
        Item.IMPORTANCE,
        Item.URGENCY,
        Item.STATUS};

    private static String[] sortFields = new String[]{
        Item.PARSE_DUE_DATE,
        Item.PARSE_PRIORITY,
        Item.PARSE_REMAINING_EFFORT_TOTAL,
        Item.PARSE_EFFORT_ESTIMATE,
        Item.PARSE_ACTUAL_EFFORT,
        Item.PARSE_CHALLENGE,
        Item.PARSE_DREAD_FUN_VALUE,
        Item.PARSE_EARNED_VALUE,
        Item.PARSE_START_BY_DATE,
        Item.PARSE_STARTED_ON_DATE,
        Item.PARSE_UPDATED_AT,
        Item.PARSE_CREATED_AT,
        Item.PARSE_COMPLETED_DATE,
        Item.PARSE_WAIT_UNTIL_DATE,
        Item.PARSE_TEXT,
        Item.PARSE_IMPORTANCE_URGENCY_VIRT,
        Item.PARSE_IMPORTANCE,
        Item.PARSE_URGENCY,
        Item.PARSE_STATUS};

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * fetches a filter from Parse or returns default filter if none is stored
     *
     * @param screenId
     * @param itemList
     * @return
     */
//    static FilterSortDef fetchFilterSortDefXXX(String screenId, ParseObject itemList, FilterSortDef defaultFilter) {
//        FilterSortDef filterSortDef = null;
//        if (itemList instanceof ParseObject && ((ParseObject) itemList).getObjectIdP() != null) {
////            filterSortDef = DAO.getInstance().getFilterSortDef(screenId, ((ParseObject) itemList).getObjectId());
//            filterSortDef = DAO.getInstance().getFilterSortDefXXX(((ParseObject) itemList).getObjectIdP());
//        }
//        if (filterSortDef == null) {
////            filterSortDef = new FilterSortDef(screenId, itemList);
////            DAO.getInstance().save(filterSortDef);
//            filterSortDef = defaultFilter;
//        }
//        return filterSortDef;
//    }
//</editor-fold>
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
        String s = getString(PARSE_FILTER_NAME);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    public void setFilterName(String filterName) {
        if (filterName == null || filterName.length() == 0) {
            remove(PARSE_FILTER_NAME);
        } else {
            put(PARSE_FILTER_NAME, filterName);
        }
    }

    public String getSystemName() {
        String s = getString(PARSE_SYSTEM_NAME);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    /**
     * systemname for filters for e.g. Next, Inbox, Alltasks, ...
     *
     * @param systemName
     */
    public void setSystemName(String systemName) {
        if (systemName == null || systemName.length() == 0) {
            remove(PARSE_SYSTEM_NAME);
        } else {
            put(PARSE_SYSTEM_NAME, systemName);
        }
    }

    /**
     * a text description of what the filter does, shorter than help
     *
     * @param description
     */
    public void setDescription(String description) {
        if (description == null || description.length() == 0) {
            remove(PARSE_FILTER_DESCRIPTION);
        } else {
            put(PARSE_FILTER_DESCRIPTION, description);
        }
    }

    public String getDescription() {
        String s = getString(PARSE_FILTER_DESCRIPTION);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

    /**
     * the longest description of what the filter does, used for popup help
     *
     * @param helpTxt
     */
    public void setHelp(String helpTxt) {
        if (helpTxt == null || helpTxt.length() == 0) {
            remove(PARSE_FILTER_HELP);
        } else {
            put(PARSE_FILTER_HELP, helpTxt);
        }
    }

    public String getHelp() {
        String s = getString(PARSE_FILTER_HELP);
        if (s == null) {
            return "";
        } else {
            return s;
        }
    }

//    public void setDefinition(String definition) {
//        if (definition == null || definition.length() == 0) {
//            remove(PARSE_FILTER_DEFINITION);
//        } else {
//            put(PARSE_FILTER_DEFINITION, definition);
//        }
//    }
//
//    public String getDefinition() {
//        String s = getString(PARSE_FILTER_DEFINITION);
//        if (s == null) {
//            return "";
//        } else {
//            return s;
//        }
//    }
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

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns tru if the filter does not have any effect (no sorting, no
     * filtering)
     *
     * @return
     */
//    public boolean isNeutralXXX() {
//        boolean neutral
//                = showNewTasks
//                && showOngoingTasks
//                && showWaitingTasks
//                && showDoneTasks
//                && showCancelledTasks
//                && showBeforeHideUntilDate
//                && showDependingOnUndoneTasks
//                && showExpiresOnDate
//                && !showProjectsOnly
//                && !showInterruptTasksOnly
//                && !showWithoutEstimatesOnly
//                && !showWithActualsOnly;
//
//        return !isSortOn() && (showAll || neutral);
//    }
//</editor-fold>
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
//    private void saveBoolsToParseXXX() {
//        setFilterOptionsInParse(getFilterBoolsAsString());
//    }
    private void setFilterBoolsFromOptionsString(String filterOptions) {
//<editor-fold defaultstate="collapsed" desc="comment">
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
//        if (filterOptions == null || filterOptions.length() == 0) {
//            return;
//        }
//</editor-fold>
//        if (false && showInitialized) { //NB extractAndSetFilterOptions is now called explicitly on every change so no need to check if initialized
//            return;
//        }
//        String filterOptions = getFilterOptionsFromParse();
//        if (filterOptions == null) {
//            filterOptions = ""; //reset all options
//        }

        showAll = filterOptions.contains(FILTER_SHOW_ALL); // NB. String.contains not implemented for CN1

        showNewTasks = filterOptions.contains(FILTER_SHOW_NEW_TASKS) || showAll;
        showOngoingTasks = filterOptions.contains(FILTER_SHOW_ONGOING_TASKS) || showAll;
        showWaitingTasks = filterOptions.contains(FILTER_SHOW_WAITING_TASKS) || showAll;
        showDoneTasks = filterOptions.contains(FILTER_SHOW_DONE_TASKS) || showAll;
        showDoneTillMidnight = filterOptions.contains(FILTER_SHOW_DONE_TILL_MIDNIGHT) || showAll;
        showCancelledTasks = filterOptions.contains(FILTER_SHOW_CANCELLED_TASKS) || showAll;
        showBeforeHideUntilDate = filterOptions.contains(FILTER_SHOW_BEFORE_HIDE_UNTILDATE) || showAll;
        showDependingOnUndoneTasks = filterOptions.contains(FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS) || showAll;
        showExpiresOnDate = filterOptions.contains(FILTER_SHOW_EXPIRES_ON_DATE) || showAll;

        showProjectsOnly = filterOptions.contains(FILTER_SHOW_PROJECTS_ONLY);
        showInterruptTasksOnly = filterOptions.contains(FILTER_SHOW_INTERRUPT_TASKS_ONLY);
        showStarredTasksOnly = filterOptions.contains(FILTER_SHOW_STARRED_TASKS_ONLY);
        showWithoutEstimatesOnly = filterOptions.contains(FILTER_SHOW_WITHOUT_ESTIMATES_ONLY);
        showWithActualsOnly = filterOptions.contains(FILTER_SHOW_WITH_ACTUALS_ONLY);
        showWithRemainingOnly = filterOptions.contains(FILTER_SHOW_WITH_REMAINING_ONLY);

//        showInitialized = true;
    }

//    private void extractAndSetFilterOptions() {
//        if (!showInitialized) {
//            extractAndSetFilterOptions(getFilterOptions());
//        }
//    }
//    public void getFilterOptions(String filterOptions) {
    private String getFilterOptionsFromParse() {
//        if (filterOptions == null) {
        String filterOptions = getString(PARSE_FILTER_OPTIONS);
//        if (false) {
//            extractAndSetFilterOptions();
//        }
        return filterOptions != null ? filterOptions : "";
//        }
    }

    public void setFilterOptionsInParse(String filterOptions) {
        if (filterOptions != null && !filterOptions.isEmpty()) {
            put(PARSE_FILTER_OPTIONS, filterOptions);
        } else { //        extractAndSetFilterOptions(filterOptions);
            remove(PARSE_FILTER_OPTIONS);
        }
//        extractAndSetFilterOptions();
    }

    public void setFilterOptionsInParseAndUpdateBools(String filterOptions) {
//        if (filterOptions != null && !filterOptions.isEmpty()) {
//            put(PARSE_FILTER_OPTIONS, filterOptions);
//        } else { //        extractAndSetFilterOptions(filterOptions);
//            remove(PARSE_FILTER_OPTIONS);
//        }
        setFilterOptionsInParse(filterOptions);
        setFilterBoolsFromOptionsString(getFilterOptionsFromParse());
    }

//    public void setFilterOptionsXXX(String filterOptions) {
//        if (filterOptions == null) {
//            filterOptions = "";
//        }
////        extractAndSetFilterOptions(filterOptions);
//        put(PARSE_FILTER_OPTIONS, filterOptions);
//        extractAndSetFilterOptions(filterOptions);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void initFilterOptions() {
//        if (!showInitialized) {
////            getFilterOptions();
//            extractAndSetFilterOptions();
//            showInitialized = true;
//        }
//    }
//</editor-fold>
    private String getFilterBoolsAsString() {
        String filterOptions
                = (showAll ? FILTER_SHOW_ALL + " " : "")
                + (showNewTasks ? FILTER_SHOW_NEW_TASKS + " " : "")
                + (showOngoingTasks ? FILTER_SHOW_ONGOING_TASKS + " " : "")
                + (showWaitingTasks ? FILTER_SHOW_WAITING_TASKS + " " : "")
                + (showDoneTasks ? FILTER_SHOW_DONE_TASKS + " " : "")
                + (showDoneTillMidnight ? FILTER_SHOW_DONE_TILL_MIDNIGHT + " " : "")
                + (showCancelledTasks ? FILTER_SHOW_CANCELLED_TASKS + " " : "")
                + (showBeforeHideUntilDate ? FILTER_SHOW_BEFORE_HIDE_UNTILDATE + " " : "")
                + (showDependingOnUndoneTasks ? FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS + " " : "")
                + (showExpiresOnDate ? FILTER_SHOW_EXPIRES_ON_DATE + " " : "")
                + (showProjectsOnly ? FILTER_SHOW_PROJECTS_ONLY + " " : "")
                + (showInterruptTasksOnly ? FILTER_SHOW_INTERRUPT_TASKS_ONLY + " " : "")
                + (showStarredTasksOnly ? FILTER_SHOW_STARRED_TASKS_ONLY + " " : "")
                + (showWithoutEstimatesOnly ? FILTER_SHOW_WITHOUT_ESTIMATES_ONLY + " " : "")
                + (showWithActualsOnly ? FILTER_SHOW_WITH_ACTUALS_ONLY + " " : "");
//        put(PARSE_FILTER_OPTIONS, filterOptions);
        return filterOptions;
    }

    private void updateAndSaveFilterOptions() {
//        String filterOptions
//                = (showAll ? FILTER_SHOW_ALL + " " : "")
//                + (showNewTasks ? FILTER_SHOW_NEW_TASKS + " " : "")
//                + (showOngoingTasks ? FILTER_SHOW_ONGOING_TASKS + " " : "")
//                + (showWaitingTasks ? FILTER_SHOW_WAITING_TASKS + " " : "")
//                + (showDoneTasks ? FILTER_SHOW_DONE_TASKS + " " : "")
//                + (showDoneTillMidnight ? FILTER_SHOW_DONE_TILL_MIDNIGHT + " " : "")
//                + (showCancelledTasks ? FILTER_SHOW_CANCELLED_TASKS + " " : "")
//                + (showBeforeHideUntilDate ? FILTER_SHOW_BEFORE_HIDE_UNTILDATE + " " : "")
//                + (showDependingOnUndoneTasks ? FILTER_SHOW_TASKS_THAT_DEPEND_ON_UNDONE_TASKS + " " : "")
//                + (showExpiresOnDate ? FILTER_SHOW_EXPIRES_ON_DATE + " " : "")
//                + (showProjectsOnly ? FILTER_SHOW_PROJECTS_ONLY + " " : "")
//                + (showInterruptTasksOnly ? FILTER_SHOW_INTERRUPT_TASKS_ONLY + " " : "")
//                + (showWithoutEstimatesOnly ? FILTER_SHOW_WITHOUT_ESTIMATES_ONLY + " " : "")
//                + (showWithActualsOnly ? FILTER_SHOW_WITH_ACTUALS_ONLY + " " : "");
//        put(PARSE_FILTER_OPTIONS, filterOptions);
//        setFilterOptionsInParseAndUpdateBools(getFilterBoolsAsString());
        setFilterOptionsInParse(getFilterBoolsAsString());
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
//    public boolean test(Object t) {
    public boolean test(Item item) {
//        Item item = (Item) t;
//        if (false) {
//            saveBoolsToParse();
//        }
        if (!initialized) {
            setFilterBoolsFromOptionsString(getFilterOptionsFromParse());
            initialized = true;
        }
        ItemStatus status = item.getStatus();
        return showAll
                || ((status != ItemStatus.CREATED || showNewTasks)
                && (status != ItemStatus.ONGOING || showOngoingTasks)
                && (status != ItemStatus.WAITING || showWaitingTasks)
                && (status != ItemStatus.DONE || showDoneTasks
                //                || (MyPrefs.keepDoneTasksVisibleTheDayTheyreCompleted.getBoolean() && MyDate.isToday(item.getCompletedDateD())))
                || (showDoneTillMidnight && MyDate.isToday(item.getCompletedDate())))
                && (status != ItemStatus.CANCELLED || showCancelledTasks)
                //all the following conditions must ALL be met to show the item (or vice-versa if either is false, don't show)
                //TODO!!!! hideUntilDate should only compare on calendar date, not absolute time (unless the date stored 
                && (showBeforeHideUntilDate || item.getHideUntilDateD().getTime() == 0 || MyDate.currentTimeMillis() >= item.getHideUntilDateD().getTime()) //before now <=> hideUntil date is already passed so show the item
                && (showDependingOnUndoneTasks || item.isDependingOnTasksDone())
                && (showExpiresOnDate || item.getExpiresOnDate().getTime() == 0 || item.getExpiresOnDate().getTime() < MyDate.currentTimeMillis()) //before now <=> hideUntil date is already passed so show the item
                && (!showProjectsOnly || item.isProject()) //before now <=> hideUntil date is already passed so show the item
                && (!showInterruptTasksOnly || item.isInteruptOrInstantTask()) //before now <=> hideUntil date is already passed so show the item
                && (!showStarredTasksOnly || item.isStarred()) //before now <=> hideUntil date is already passed so show the item
                && (!showWithoutEstimatesOnly || !item.has(Item.PARSE_EFFORT_ESTIMATE)) //before now <=> hideUntil date is already passed so show the item
                && (!showWithActualsOnly || item.has(Item.PARSE_ACTUAL_EFFORT)) //before now <=> hideUntil date is already passed so show the item
                && (!showWithRemainingOnly || item.has(Item.PARSE_REMAINING_EFFORT_TOTAL)) //before now <=> hideUntil date is already passed so show the item
                && (!showChallengeEasy || Objects.equals(item.getChallengeN(), Challenge.EASY))
                && (!showChallengeHard || Objects.equals(item.getChallengeN(), Challenge.HARD))
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
            query.whereGreaterThanOrEqualTo(Item.PARSE_HIDE_UNTIL_DATE, new MyDate()); //new Date() <=> now
        }
        if (!fp.showDependingOnUndoneTasks) {
            //TODO!!!!! doesn't work 
            query.whereExists(Item.PARSE_DEPENDS_ON_TASK);
            query.whereGreaterThanOrEqualTo(Item.PARSE_DEPENDS_ON_TASK, new MyDate()); //new Date() <=> now
        }
        if (!fp.showExpiresOnDate) {
            query.whereLessThan(Item.PARSE_EXPIRES_ON_DATE, new MyDate());
        }
        if (fp.showProjectsOnly) {
            query.whereExists(Item.PARSE_SUBTASKS);
            query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //show only top-level projects
        }
        if (fp.showInterruptTasksOnly) {
            query.whereEqualTo(Item.PARSE_INTERRUPT_OR_INSTANT_TASK, true);
        }
        if (fp.showStarredTasksOnly) {
            query.whereEqualTo(Item.PARSE_STARRED, true);
        }
        if (fp.showWithoutEstimatesOnly) {
            query.whereDoesNotExist(Item.PARSE_EFFORT_ESTIMATE);
        }
        if (fp.showWithActualsOnly) {
            query.whereExists(Item.PARSE_ACTUAL_EFFORT); //Choice for task status will determine which tasks are shown
        }
        if (fp.showWithRemainingOnly) {
            query.whereExists(Item.PARSE_REMAINING_EFFORT_TOTAL); //Choice for task status will determine which tasks are shown
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

    void setSortingComparator(Comparator<Item> sorter) {
        nonSavedComparator = sorter;
    }

    Comparator<Item> getSortingComparator() {
        if (nonSavedComparator != null) {
            return nonSavedComparator;
        } else {
            return getSortingComparator(getSortFieldId(), isSortDescending());
        }
    }

    static Comparator<Item> getMultipleComparatorNew(Comparator[] sortComparator) {
        assert sortComparator.length >= 1 : "at least one sort comparator must be defined";
//        Comparator<Item> comp1 = getSortFieldId.length >= 1 ? getSortingComparator(getSortFieldId[0], sortDescending[0]) : null;
//        Comparator<Item> comp2 = getSortFieldId.length >= 2 ? getSortingComparator(getSortFieldId[1], sortDescending[1]) : null;
//        Comparator<Item> comp3 = getSortFieldId.length >= 3 ? getSortingComparator(getSortFieldId[2], sortDescending[2]) : null;

        Comparator[] comp = sortComparator;

        return (i1, i2) -> {
            int res1 = comp[0].compare(i1, i2);
            if (res1 != 0) {
                return res1;
            } else {
                if (comp.length <= 1) {
//                    return 0; //TODO!!!! should compare eg objectId to ensure a consistent ordering on every sort
                    return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                    return i1.getGuid().compareTo(i2.getGuid()); //compare objectId to ensure a consistent ordering on every sort
                } else {
                    int res2 = comp[1].compare(i1, i2);
                    if (res2 != 0) {
                        return res2;
                    } else {
                        if (comp.length <= 2) {
//                            return 0;
                            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                            return i1.getGuid().compareTo(i2.getGuid()); //compare objectId to ensure a consistent ordering on every sort
                        } else {
                            int res3 = comp[2].compare(i1, i2);
                            if (res3 != 0) {
                                return res3;
                            } else {
//                                return 0;
                                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                                return i1.getGuid().compareTo(i2.getGuid()); //compare objectId to ensure a consistent ordering on every sort
                            }
                        }
                    }
                }
            }
        };
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
        Comparator[] comp = new Comparator[getSortFieldId.length];
        for (int i = 0; i < comp.length; i++) {
            comp[i] = getSortingComparator(getSortFieldId[i], sortDescending[i]);

        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        Comparator<Item> comp1 = getSortFieldId.length >= 1 ? getSortingComparator(getSortFieldId[0], sortDescending[0]) : null;
//        Comparator<Item> comp2 = getSortFieldId.length >= 2 ? getSortingComparator(getSortFieldId[1], sortDescending[1]) : null;
//        Comparator<Item> comp3 = getSortFieldId.length >= 3 ? getSortingComparator(getSortFieldId[2], sortDescending[2]) : null;

//        return (i1, i2) -> {
//            int res1 = comp1.compare(i1, i2);
//            if (res1 != 0) {
//                return res1;
//            } else {
//                if (comp2 == null) {
////                    return 0; //TODO!!!! should compare eg objectId to ensure a consistent ordering on every sort
//                    return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                } else {
//                    int res2 = comp2.compare(i1, i2);
//                    if (res2 != 0) {
//                        return res2;
//                    } else {
//                        if (comp3 == null) {
////                            return 0;
//                            return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                        } else {
//                            int res3 = comp3.compare(i1, i2);
//                            if (res3 != 0) {
//                                return res3;
//                            } else {
////                                return 0;
//                                return i1.getObjectIdP().compareTo(i2.getObjectIdP()); //compare objectId to ensure a consistent ordering on every sort
//                            }
//                        }
//                    }
//                }
//            }
//        };
//</editor-fold>
        return getMultipleComparatorNew(comp);
    }

    private static int compareDreadFunValueOLD(DreadFunValue d1, DreadFunValue d2) {
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
     * compare DreadFun values which may be null. A null value is always smaller
     * than a defined value (to show last in sorted list) return FUN > null >
     * DREAD
     *
     * @param d1
     * @param d2
     * @return
     */
    private static int compareDreadFunValue(DreadFunValue d1, DreadFunValue d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else if (d2 == DreadFunValue.FUN) {
                return -1;
            } else { //(d2==DreadFunValue.DREAD)
                if (Config.TEST) {
                    ASSERT.that(d2 == DreadFunValue.DREAD);
                }
                return 1;
            }
        } else {
            if (d2 == null) {
                if (d1 == DreadFunValue.FUN) {
                    return 1;
                } else { //(d2==DreadFunValue.DREAD)
                    if (Config.TEST) {
                        ASSERT.that(d2 == DreadFunValue.DREAD);
                    }
                    return -1;
                }
            } else {
                return (d1.compareTo(d2));
            }
        }
    }

    private static int compareChallengeValue(Challenge d1, Challenge d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else if (d2 == Challenge.HARD) {
                return -1;
            } else { //(d2==DreadFunValue.DREAD)
                return 1;
            }
        } else {
            if (d2 == null) {
                if (d1 == Challenge.HARD) {
                    return 1;
                } else { //(d2==DreadFunValue.DREAD)
                    if (Config.TEST) {
                        ASSERT.that(d2 == Challenge.EASY);
                    }
                    return -1;
                }
            } else {
                return (d1.compareTo(d2));
            }
        }
    }

    private static int compareHighMediumLow(HighMediumLow d1, HighMediumLow d2) {
        if (d1 == null) {
            if (d2 == null) {
                return 0;
            } else if (d2 == HighMediumLow.HIGH) {
                return -1;
            } else { //(d2==DreadFunValue.DREAD)
                if (Config.TEST) {
                    ASSERT.that(d2 == HighMediumLow.LOW);
                }
                return 1;
            }
        } else {
            if (d2 == null) {
                if (d1 == HighMediumLow.HIGH) {
                    return 1;
                } else { //(d2==DreadFunValue.DREAD)
                    if (Config.TEST) {
                        ASSERT.that(d2 == HighMediumLow.LOW);
                    }
                    return -1;
                }
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
    static int compareCategories(List<Category> c1, List<Category> c2, boolean sortNoCategoryFirst) {
        if (c1 == null || c1.isEmpty()) {
            if (c2 == null || c2.isEmpty()) {
                return 0; //if both empty, no sorting
            } else {
                return sortNoCategoryFirst ? -1 : 1;
            }
        } else { //c1 has categories
            if (c2 == null || c2.isEmpty()) {
                return sortNoCategoryFirst ? 1 : -1;
            } else {
                Category cat1 = c1.get(0);
                Category cat2 = c2.get(0);
                //compare the *first* category selected for each item
                //TODO!!!! instead of comparing by the order in the CategoryList, compare the categories that are sefirst in CategoryList (lets you set the 'importance' of categories when multiple are selected)
                return (compareInt(CategoryList.getInstance().indexOf(cat1), CategoryList.getInstance().indexOf(cat2)));
            }
        }
    }

    static int compareCategoriesNoCatLast(List<Category> c1, List<Category> c2) {
        return compareCategories(c1, c2, false);
    }

    static int compareCategoriesNoCatFirst(List<Category> c1, List<Category> c2) {
        return compareCategories(c1, c2, true);
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
    static int compareOwnerList(ItemAndListCommonInterface ownerList1, ItemAndListCommonInterface ownerList2) {
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

    private static int compareNullValueLastXXX(Object d1, Object d2) {
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

//    private static int compareNullValueMiddle(Object d1, Object d2) {
//        if (d1 == null) {
//            if (d2 == null) {
//                return 0;
//            } else if(d2==)
//                return -1;
//            }
//        } else {
//            if (d2 == null) {
//                return 1;
//            } else if (d1 instanceof Comparable && d2 instanceof Comparable) {
//                return ((Comparable) d1).compareTo((Comparable) d2);
//            } else {
//                return 0; //one or other is not Comparable
//            }
//        }
//    }
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
                        ? (i1, i2) -> compareDate(i2.getDueDate(), i1.getDueDate())
                        : (i1, i2) -> compareDate(i1.getDueDate(), i2.getDueDate());
            case Item.PARSE_REMAINING_EFFORT_TOTAL:
                return sortDescending
                        ? (i1, i2) -> compareLong(i1.getRemainingTotal(), i2.getRemainingTotal()) //show lowest at top
                        : (i1, i2) -> compareLong(i2.getRemainingTotal(), i1.getRemainingTotal());
            case Item.PARSE_EFFORT_ESTIMATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i1.getEstimateTotal(), i2.getEstimateTotal()) //show lowest at top
                        : (i1, i2) -> compareLong(i2.getEstimateTotal(), i1.getEstimateTotal());
            case Item.PARSE_CHALLENGE:
                return sortDescending
                        //                        ? (i1, i2) -> i1.getChallengeN().compareTo(i2.getChallengeN())
                        //                        : (i1, i2) -> i2.getChallengeN().compareTo(i1.getChallengeN());
                        //                        ? (i1, i2) -> compareNullValueLast(i1.getChallengeN(), i2.getChallengeN())
                        //                        : (i1, i2) -> compareNullValueLast(i2.getChallengeN(), i1.getChallengeN());
//                        ? (i1, i2) -> compareChallengeValue(i1.getChallengeN(), i2.getChallengeN())
//                        : (i1, i2) -> compareChallengeValue(i2.getChallengeN(), i1.getChallengeN());
                        ? (i1, i2) -> Challenge.compare(i1.getChallengeN(), i2.getChallengeN())
                        : (i1, i2) -> Challenge.compare(i2.getChallengeN(), i1.getChallengeN());
            case Item.PARSE_DREAD_FUN_VALUE:
                return sortDescending
                        //                        ? (i1, i2) -> i1.getDreadFunValueN().compareTo(i2.getDreadFunValueN())
                        //                        : (i1, i2) -> i2.getDreadFunValueN().compareTo(i1.getDreadFunValueN());
                        //                        ? (i1, i2) -> compareDreadFunValue(i1.getDreadFunValueN(), i2.getDreadFunValueN())
                        //                        : (i1, i2) -> compareDreadFunValue(i2.getDreadFunValueN(), i1.getDreadFunValueN());
                        //                        ? (i1, i2) -> compareNullValueLast(i1.getDreadFunValueN(), i2.getDreadFunValueN())
//                        //                        : (i1, i2) -> compareNullValueLast(i2.getDreadFunValueN(), i1.getDreadFunValueN());
//                        ? (i1, i2) -> compareDreadFunValue(i1.getDreadFunValueN(), i2.getDreadFunValueN())
//                        : (i1, i2) -> compareDreadFunValue(i2.getDreadFunValueN(), i1.getDreadFunValueN());
                        //                        : (i1, i2) -> compareNullValueLast(i2.getDreadFunValueN(), i1.getDreadFunValueN());
                        ? (i1, i2) -> DreadFunValue.compare(i1.getDreadFunValueN(), i2.getDreadFunValueN())
                        : (i1, i2) -> DreadFunValue.compare(i2.getDreadFunValueN(), i1.getDreadFunValueN());
            case Item.PARSE_EARNED_VALUE:
                return sortDescending
                        ? (i1, i2) -> compareDouble(i1.getEarnedValue(), i2.getEarnedValue()) //show highest on top
                        : (i1, i2) -> compareDouble(i2.getEarnedValue(), i1.getEarnedValue());
            case Item.PARSE_EARNED_VALUE_PER_HOUR:
                return sortDescending
                        ? (i1, i2) -> compareDouble(i1.getEarnedValuePerHour(), i2.getEarnedValuePerHour()) //show highest on top
                        : (i1, i2) -> compareDouble(i2.getEarnedValuePerHour(), i1.getEarnedValuePerHour());
            case Item.PARSE_START_BY_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getStartByDateD().getTime(), i1.getStartByDateD().getTime())
                        : (i1, i2) -> compareLong(i1.getStartByDateD().getTime(), i2.getStartByDateD().getTime());
            case Item.PARSE_STARTED_ON_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getStartedOnDate(), i1.getStartedOnDate())
                        : (i1, i2) -> compareLong(i1.getStartedOnDate(), i2.getStartedOnDate());
            case Item.PARSE_WAIT_UNTIL_DATE:
                return sortDescending
                        ? (i1, i2) -> compareLong(i2.getWaitUntilDate().getTime(), i1.getWaitUntilDate().getTime())
                        : (i1, i2) -> compareLong(i1.getWaitUntilDate().getTime(), i2.getWaitUntilDate().getTime());
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
                        //                        ? (i1, i2) -> compareLong(i1.getCompletedDate(), i2.getCompletedDate()) //oldest first
                        ? (i1, i2) -> compareDate(i1.getCompletedDate(), i2.getCompletedDate()) //oldest first
                        //                        : (i1, i2) -> compareLong(i2.getCompletedDate(), i1.getCompletedDate());
                        : (i1, i2) -> compareDate(i2.getCompletedDate(), i1.getCompletedDate());
            case Item.PARSE_TEXT:
                return sortDescending
                        //                        ? (i1, i2) -> i2.getText().compareTo(i1.getText()) //show alphabetically, lowest value at top
                        ? (i1, i2) -> i2.getText().compareToIgnoreCase(i1.getText()) //show alphabetically, lowest value at top
                        : (i1, i2) -> i1.getText().compareToIgnoreCase(i2.getText());
            case Item.PARSE_IMPORTANCE_URGENCY_VIRT:
                return sortDescending
                        ? (i1, i2) -> compareInt(i1.getImpUrgPrioValue(), i2.getImpUrgPrioValue()) //show highest values at top
                        : (i1, i2) -> compareInt(i2.getImpUrgPrioValue(), i1.getImpUrgPrioValue());
            case Item.PARSE_IMPORTANCE:
                return sortDescending
//                        ? (i1, i2) -> compareNullValueLast(i1.getImportanceN(), i2.getImportanceN()) //show highest values at top
//                        : (i1, i2) -> compareNullValueLast(i2.getImportanceN(), i1.getImportanceN());
//                        ? (i1, i2) -> compareHighMediumLow(i1.getImportanceN(), i2.getImportanceN()) //show highest values at top
//                        : (i1, i2) -> compareHighMediumLow(i2.getImportanceN(), i1.getImportanceN());
                        ? (i1, i2) -> HighMediumLow.compare(i1.getImportanceN(), i2.getImportanceN()) //show highest values at top
                        : (i1, i2) -> HighMediumLow.compare(i2.getImportanceN(), i1.getImportanceN());
            case Item.PARSE_URGENCY:
                return sortDescending
                        ? (i1, i2) -> HighMediumLow.compare(i1.getUrgencyN(), i2.getUrgencyN()) //show highest values at top
                        : (i1, i2) -> HighMediumLow.compare(i2.getUrgencyN(), i1.getUrgencyN());
            case FILTER_SORT_TODAY_VIEW:
//                                return (i1, i2) -> 0; //no sorting, arrive sorted from parse query
                return (i1, i2) -> i1.getTodaySortOrder().compareTo(i2.getTodaySortOrder());
            case Item.PARSE_CATEGORIES:
                return sortDescending
                        ? (i1, i2) -> compareCategoriesNoCatLast(i1.getCategories(), i2.getCategories()) //show highest values at top
                        : (i1, i2) -> compareCategoriesNoCatLast(i2.getCategories(), i1.getCategories());
            case Item.PARSE_OWNER_LIST:
                return sortDescending
                        //                        ? (i1, i2) -> compareOwnerList(i1.getOwner(), i2.getOwner()) //show highest values at top
                        //                        : (i1, i2) -> compareOwnerList(i2.getOwner(), i1.getOwner());
                        ? (i1, i2) -> compareOwnerList(i1.getOwnerTopLevelList(), i2.getOwnerTopLevelList()) //show highest values at top
                        : (i1, i2) -> compareOwnerList(i2.getOwnerTopLevelList(), i1.getOwnerTopLevelList());
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
    /**
     * return a filtered copy of the target collection/list
     *
     * @param target
     * @return
     */
//    public ArrayList<Item> filter(Collection<ItemAndListCommonInterface> target) {
    public List<Item> filterItemList(Collection<ItemAndListCommonInterface> target) {
        FilterSortDef predicate = this;
        ArrayList filteredCollection = new ArrayList();
//        for (Item t : target) {
        for (ItemAndListCommonInterface t : target) {
            if (t instanceof Item) { //can also be a WorkSlot in Today view, then simply don't apply any filter
                if (predicate.test((Item) t)) {
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
//    public List filterItemListXXX(List orgList) {
////        List<Item> filteredList = new ArrayList(filter(orgList.getList())); //filter the underlying list and create a new ItemList
////        List<Item> filteredList = new ArrayList(filter(orgList)); //filter the underlying list and create a new ItemList
////        List<Item> filteredList = filter(orgList); //filter the underlying list and create a new ItemList
//        List<Item> filteredList = filterItemList(orgList); //filter the underlying list and create a new ItemList
////        filteredList.setSourceItemList(orgList);
//        return filteredList;
//    }
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
//        saveCurrentlyActiveFilterOptions();
        super.save();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * @return the showAll
     */
//    public boolean isShowAllXXX() {
////        initFilterOptions();
//        extractAndSetFilterOptions();
//        return showAll;
//    }
//
//    /**
//     * @param showAll the showAll to set
//     */
//    public void setShowAllXXX(boolean showAll) {
//        this.showAll = showAll;
//    }
    /**
     * @return the showDefault
     */
//    public boolean isShowDefault() {
////        initFilterOptions();
//        return showDefault;
//    }
//
//    /**
//     * @param showDefault the showDefault to set
//     */
//    public void setShowDefault(boolean showDefault) {
//        this.showDefault = showDefault;
//        saveCurrentlyActiveFilterOptions();
//    }
//</editor-fold>
    /**
     * @return the showNewTasks
     */
    public boolean isShowNewTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showNewTasks;
    }

    /**
     * @param showNewTasks the showNewTasks to set
     */
    public void setShowNewTasks(boolean showNewTasks) {
        this.showNewTasks = showNewTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showOngoingTasks
     */
    public boolean isShowOngoingTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showOngoingTasks;
    }

    /**
     * @param showOngoingTasks the showOngoingTasks to set
     */
    public void setShowOngoingTasks(boolean showOngoingTasks) {
        this.showOngoingTasks = showOngoingTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showWaitingTasks
     */
    public boolean isShowWaitingTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showWaitingTasks;
    }

    /**
     * @param showWaitingTasks the showWaitingTasks to set
     */
    public void setShowWaitingTasks(boolean showWaitingTasks) {
        this.showWaitingTasks = showWaitingTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showDoneTasks
     */
    public boolean isShowDoneTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showDoneTasks;
    }

    /**
     * @param showDoneTasks the showDoneTasks to set
     */
    public void setShowDoneTasks(boolean showDoneTasks) {
        this.showDoneTasks = showDoneTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showDoneTasks
     */
    public boolean isShowDoneTillMidnight() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showDoneTillMidnight;
    }

    /**
     * @param showDoneTasksUntilMidnight the showDoneTasks to set
     */
    public void setShowDoneTillMidnight(boolean showDoneTasksUntilMidnight) {
        this.showDoneTillMidnight = showDoneTasksUntilMidnight;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showCancelledTasks
     */
    public boolean isShowCancelledTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showCancelledTasks;
    }

    /**
     * @param showCancelledTasks the showCancelledTasks to set
     */
    public void setShowCancelledTasks(boolean showCancelledTasks) {
        this.showCancelledTasks = showCancelledTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showBeforeHideUntilDate
     */
    public boolean isShowBeforeHideUntilDate() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showBeforeHideUntilDate;
    }

    /**
     * @param showBeforeHideUntilDate the showBeforeHideUntilDate to set
     */
    public void setShowBeforeHideUntilDate(boolean showBeforeHideUntilDate) {
        this.showBeforeHideUntilDate = showBeforeHideUntilDate;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showDependingOnUndoneTasks
     */
    public boolean isShowDependingOnUndoneTasks() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showDependingOnUndoneTasks;
    }

    /**
     * @param showDependingOnUndoneTasks the showDependingOnUndoneTasks to set
     */
    public void setShowDependingOnUndoneTasks(boolean showDependingOnUndoneTasks) {
        this.showDependingOnUndoneTasks = showDependingOnUndoneTasks;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showExpiresOnDate
     */
    public boolean isShowExpiresOnDate() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showExpiresOnDate;
    }

    /**
     * @param showExpiresOnDate the showExpiresOnDate to set
     */
    public void setShowExpiresOnDate(boolean showExpiresOnDate) {
        this.showExpiresOnDate = showExpiresOnDate;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showProjectsOnly
     */
    public boolean isShowProjectsOnly() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showProjectsOnly;
    }

    /**
     * @param showProjectsOnly the showProjectsOnly to set
     */
    public void setShowProjectsOnly(boolean showProjectsOnly) {
        this.showProjectsOnly = showProjectsOnly;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showInterruptTasksOnly
     */
    public boolean isShowInterruptTasksOnly() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showInterruptTasksOnly;
    }

    /**
     * @param showInterruptTasksOnly the showInterruptTasksOnly to set
     */
    public void setShowInterruptTasksOnly(boolean showInterruptTasksOnly) {
        this.showInterruptTasksOnly = showInterruptTasksOnly;
        updateAndSaveFilterOptions();
    }

    public boolean isShowStarredTasksOnly() {
        return showStarredTasksOnly;
    }

    /**
     * @param showStarredTasksOnly the showInterruptTasksOnly to set
     */
    public void setShowStarredTasksOnly(boolean showStarredTasksOnly) {
        this.showStarredTasksOnly = showStarredTasksOnly;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showWithoutEstimatesOnly
     */
    public boolean isShowWithoutEstimatesOnly() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showWithoutEstimatesOnly;
    }

    /**
     * @param showWithoutEstimatesOnly the showWithoutEstimatesOnly to set
     */
    public void setShowWithoutEstimatesOnly(boolean showWithoutEstimatesOnly) {
        this.showWithoutEstimatesOnly = showWithoutEstimatesOnly;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showWithActualsOnly
     */
    public boolean isShowWithActualsOnly() {
//        initFilterOptions();
//        saveBoolsToParse();
        return showWithActualsOnly;
    }

    /**
     * @param showWithActualsOnly the showWithActualsOnly to set
     */
    public void setShowWithActualsOnly(boolean showWithActualsOnly) {
        this.showWithActualsOnly = showWithActualsOnly;
        updateAndSaveFilterOptions();
    }

    /**
     * @return the showWithRemainingOnly
     */
    public boolean isShowWithRemainingOnly() {
        return showWithRemainingOnly;
    }

    /**
     * @param showWithRemainingOnly the showWithActualsOnly to set
     */
    public void setShowWithRemainingOnly(boolean showWithRemainingOnly) {
        this.showWithRemainingOnly = showWithRemainingOnly;
        updateAndSaveFilterOptions();
    }

    public void setShowAll() {
        boolean showAllTasks = true;
//        this.showAll = showAllTasks;
        this.showBeforeHideUntilDate = showAllTasks;
        this.showCancelledTasks = showAllTasks;
        this.showDependingOnUndoneTasks = showAllTasks;
        this.showDoneTasks = showAllTasks;
//        this.showDoneTillMidnight = showAllTasks;
        this.showExpiresOnDate = showAllTasks;
        this.showOngoingTasks = showAllTasks;
        this.showNewTasks = showAllTasks;
        this.showWaitingTasks = showAllTasks;
        updateAndSaveFilterOptions();
    }

    public void setNoSorting() {
        setSortDescending(false);
        setSortFieldId("");
        setSortOn(false);
//        updateAndSetFilterOptions();
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
    public static String[] getSortFields() {
        return sortFields;
    }

    /**
     * @param sortFields the sortField to set
     */
    public static void setSortField(String[] sortFields) {
        sortFields = sortFields;
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

    public Date getDeletedDateN() {
        Date date = getDate(PARSE_DELETED_DATE);
//        return (date == null) ? new Date(0) : date;
        return date; //return null to indicate NOT deleted
    }

    public boolean isDeleted() {
        return getDeletedDateN() != null;
    }

//    public boolean delete(Date deletedDate) {
//        setDeletedDate(deletedDate);
////        DAO.getInstance().saveNew(this,true);
//        return true;
//    }
//    public boolean softDelete() {
//        return softDelete(true);
//    }
    /**
     * for now, any filter equal to the default will not be saved. If edited, it
     * will be saved
     *
     * @return
     */
    public boolean isNoSave() {
//        return equals(FilterSortDef.getDefaultFilter()) || isNoSave;
        return isNoSave;
    }

    public void internalize(int version, DataInputStream in) throws IOException {
        super.internalize(version, in);
        setFilterBoolsFromOptionsString(getFilterOptionsFromParse()); //setBoolsFromOptions string
    }

}
