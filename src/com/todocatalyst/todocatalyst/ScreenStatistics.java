/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.components.SpanLabel;
import com.codename1.ui.Toolbar;
import com.codename1.ui.Container;
import com.codename1.ui.Component;
import com.codename1.ui.layouts.BorderLayout;
import static com.todocatalyst.todocatalyst.MyTree2.setIndent;
import java.util.List;
import java.util.Collections;
import java.util.Date;
//import com.java4less.rchart.*;
//import javax.microedition.io.ConnectionNotFoundException;
//import javax.microedition.io.PushRegistry;
//import javax.microedition.m3g.Background;
//import org.joda.time.base.BaseInterval;

/**
 *
 * @author Thomas
 */
public class ScreenStatistics extends MyForm {
    //TODO 

//    private static final String screenTitle = "Lists";
// protected static String FORM_UNIQUE_ID = "ScreenStatistics"; //unique id for each form, used to name local files for each form+ParseObject, and for analytics
//    private ItemList itemListList;
//    private boolean draggableMode = false;
//    Command sortOnOff = null;
//    Command draggableOnOff = null;
    List<Item> doneItemsFromParseSortedOnDate;
    ItemList itemListStats;
    List<WorkSlot> workSlots;
    MySearchCommand mySearchCmd;

    /**
     * edit a list of statistics over recently done tasks
     *
     */
    ScreenStatistics(String screenTitle, MyForm previousForm, Runnable updateActionOnDone) { //, GetUpdatedList updateList) { //throws ParseException, IOException {
        super(screenTitle, previousForm, updateActionOnDone);
//        this.itemListList = itemListList;
        setUniqueFormId("ScreenStatistics");
        setScrollable(false);
        if (!(getLayout() instanceof BorderLayout)) {
            setLayout(new BorderLayout());
        }
//        expandedObjects = new HashSet();
//        expandedObjects = new ExpandedObjects(getUniqueFormId()); //,null);
        expandedObjectsInit(""); //,null);
        addCommandsToToolbar(getToolbar()); //since Search refers to itemListStatus which is rebuild everytime, search must also be updated in refreshAfterEdit

        reloadData();
//        getContentPane().add(BorderLayout.CENTER, buildContentPaneForListOfItems(this.itemListList));
        refreshAfterEdit();
    }

    @Override
    public void refreshAfterEdit() {
        getContentPane().removeAll();
//        SortStatsOnXXX sortOn = SortStatsOnXXX.valueOfDefault(MyPrefs.statisticsSortBy.getString());
//        sortItemsXXX(doneItemsFromParseSortedOnDate, sortOn);
        itemListStats = buildStatisticsSortedByTime(doneItemsFromParseSortedOnDate, workSlots);
        getContentPane().add(BorderLayout.CENTER, buildContentPane(itemListStats));

        //refresh searchCmd on new list
        if (mySearchCmd != null) {
            getToolbar().removeCommand(mySearchCmd);
        }
        mySearchCmd = new MySearchCommand(getContentPane(), makeSearchFunctionUpperLowerStickyHeaders(itemListStats));
        getToolbar().addCommandToRightBar(mySearchCmd);

        revalidate();
        restoreKeepPos();
        super.refreshAfterEdit();
    }

    private void sortItemsXXX() {
        sortItemsXXX(doneItemsFromParseSortedOnDate, SortStatsOnXXX.dateAndTime);
    }

    private static void sortItemsXXX(List<Item> itemList, SortStatsOnXXX sortOn) {
//        Comparator comparator;
        switch (sortOn) {
            case dateAndTime:
            default:
                Collections.sort(itemList, FilterSortDef.getSortingComparator(Item.PARSE_COMPLETED_DATE, MyPrefs.statisticsShowMostRecentFirst.getBoolean()));
                break;
            case dateThenLists:
                Collections.sort(itemList, FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_COMPLETED_DATE, Item.PARSE_OWNER_LIST}, new boolean[]{false, false}));
                break;
            case dateThenCategories:
                Collections.sort(itemList, FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_COMPLETED_DATE, Item.PARSE_CATEGORIES}, new boolean[]{false, false}));
                break;
            case listsThenDates:
                Collections.sort(itemList, FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_OWNER_LIST, Item.PARSE_COMPLETED_DATE}, new boolean[]{false, false}));
                break;
            case categoriesThenDate:
                Collections.sort(itemList, FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_CATEGORIES, Item.PARSE_COMPLETED_DATE}, new boolean[]{false, false})); //sort on completed date
                break;
        }
    }

    private void reloadData() {
        Date startDate = new MyDate(MyDate.currentTimeMillis() - MyPrefs.statisticsScreenNumberPastDaysToShow.getInt() * MyDate.DAY_IN_MILLISECONDS);
        Date endDate = new MyDate();
//        workSlots = DAO.getInstance().getWorkSlotsN(startDate, endDate);
//        workSlots = new WorkSlotList(null, DAO.getInstance().getWorkSlots(startDate), true); //true=already sorted
        workSlots = DAO.getInstance().getWorkSlots(startDate); //true=already sorted
        doneItemsFromParseSortedOnDate = DAO.getInstance().getCompletedItems(startDate, endDate, true);
//        sortItems(itemsSortedOnDate, SortStatsOn.valueOf(MyPrefs.statisticsSortBy.getString()) );
    }

    public void addCommandsToToolbar(Toolbar toolbar) {//, Resources theme) {

        super.addCommandsToToolbar(toolbar);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            getToolbar().addSearchCommand((e) -> {
//                String text = (String) e.getSource();
//                Container compList = (Container) ((BorderLayout) getContentPane().getLayout()).getCenter();
//                boolean showAll = text == null || text.length() == 0;
//                for (int i = 0, size = this.doneItemsFromParseSortedOnDate.size(); i < size; i++) {
//                    //TODO!!! compare same case (upper/lower)
//                    //https://www.codenameone.com/blog/toolbar-search-mode.html:
//                    compList.getComponentAt(i).setHidden(((Item) doneItemsFromParseSortedOnDate.get(i)).getText().toLowerCase().indexOf(text) < 0);
//                }
//                compList.animateLayout(ANIMATION_TIME_FAST);
//            }, MyPrefs.defaultIconSizeInMM.getFloat());
//        }
//</editor-fold>
        //SEARCH
        if (true) { //TODO!!!: seardh algo crashes on statistics and won't let you exit/remove the search field
//            getToolbar().addSearchCommand(makeSearchFunctionSimple(itemListStats), MyPrefs.defaultIconSizeInMM.getFloat());
//            MySearchBar mySearchBar = new MySearchBar(getToolbar(), makeSearchFunctionSimple(itemListStats));
//            mySearchCmd =   new MySearchCommand(getContentPane(), makeSearchFunctionSimple(itemListStats));
//            getToolbar().addCommandToRightBar(  new MySearchCommand(getContentPane(), makeSearchFunctionSimple(itemListStats)));
        }

        toolbar.addCommandToOverflowMenu(MyReplayCommand.createKeep("Settings", "Settings", Icons.iconSettings, (e) -> {
            int daysInThePast = MyPrefs.statisticsScreenNumberPastDaysToShow.getInt();
            new ScreenSettingsStatistics(ScreenStatistics.this, () -> {
                if (daysInThePast != MyPrefs.statisticsScreenNumberPastDaysToShow.getInt()) {
                    reloadData(); //reload data after (possibly) changing settings (number of days in the past to show)
                }
                if (false) {
                    refreshAfterEdit();
                }
            }).show();
        }
        ));

        //BACK
//        toolbar.setBackCommand(makeDoneUpdateWithParseIdMapCommand());
        addStandardBackCommand();

        //CANCEL - not relevant, all edits are done immediately so not possible to cancel
    }

    /**
     * what are items sorted on?
     */
    enum SortStatsOnXXX {
//        dateAndTime("Dates"), //dateAndTime
//        dateAndTime("Dat", "Date and time"), //dateAndTime
//        //        dateThenLists("Lists"), //dateThenLists Lists
//        dateThenLists("Lis", "Date, then lists"), //dateThenLists Lists
//        //        dateThenCategories("Categories"), //dateThenCategories Categories
//        dateThenCategories("Cat", "Date, then category"), //dateThenCategories Categories
//        //        listsThenDates("Lists-Dates"), //listsThenDates Lists_Date
//        listsThenDates("LisD", "List, then date"), //listsThenDates Lists_Date
//        //        categoriesThenDate("Categories-Dates"); //categoriesThenDate Cat_Date
//        categoriesThenDate("CatD", "Category, then date"); //categoriesThenDate Cat_Date
//        dateAndTime("Date and time"), //dateAndTime
        dateAndTime("Date"), //dateAndTime
        //        dateThenLists("Lists"), //dateThenLists Lists
        dateThenLists("Date, then list"), //dateThenLists Lists
        //        dateThenCategories("Categories"), //dateThenCategories Categories
        dateThenCategories("Date, then category"), //dateThenCategories Categories
        //        listsThenDates("Lists-Dates"), //listsThenDates Lists_Date
        listsThenDates("List, then date"), //listsThenDates Lists_Date
        //        categoriesThenDate("Categories-Dates"); //categoriesThenDate Cat_Date
        categoriesThenDate("Category, then date"); //categoriesThenDate Cat_Date
        String str;
        String displayName;

        SortStatsOnXXX(String longStr) {
//            this.str = stri;
            this.displayName = longStr;
        }
//        SortStatsOn(String stri) {
//            this(stri,stri);
//        }

        public String toString() {
            return displayName;
        }

        public static String[] getNames() {
            return new String[]{dateAndTime.name(), dateThenLists.name(), dateThenCategories.name(), listsThenDates.name(), categoriesThenDate.name()};
        }

        public static String[] getDisplayNames() {
            return new String[]{dateAndTime.displayName, dateThenLists.displayName, dateThenCategories.displayName, listsThenDates.displayName, categoriesThenDate.displayName};
        }

        public static SortStatsOnXXX valueOfDefault(String s) {
            SortStatsOnXXX v = null;
            try {
                v = valueOf(s);
            } catch (Exception e) {
                v = dateAndTime;
            }
            return v;
        }
//        lists, 
//        categories
    };

    /**
     * how coarsely are the grouped in the list?
     */
    public enum ShowGroupedBy {
        none("None"),
        day("Day"),
        week("Week"),
        month("Month");

        String displayName;

        ShowGroupedBy(String name) {
            this.displayName = name;
        }

        public static String[] getNames() {
            return new String[]{none.name(), day.name(), week.name(), month.name()};
        }

        public static String[] getDisplayNames() {
            return new String[]{none.displayName, day.displayName, week.displayName, month.displayName};
        }
    }

    private static boolean newDateGroup(ShowGroupedBy groupBy, Date day, Date prevDate) {
        if (day == null || prevDate == null) {
            return true;
        }
        switch (groupBy) {
            case day:
                return !MyDate.isSameDate(day, prevDate);
            case week:
                return !MyDate.isSameWeekAndYear(day, prevDate);
            case month:
            default:
                return !MyDate.isSameMonthAndYear(day, prevDate);
        }
    }

    /**
     * given a single date within the interval for the group, return either the
     * first or the last time within the interval
     *
     * @param groupBy
     * @param day
     * @param getEndDate
     * @return
     */
    private static Date getDateForGroup(ShowGroupedBy groupBy, Date day, boolean getEndDate) {
        switch (groupBy) {
            case day:
                return getEndDate ? MyDate.getEndOfDay(day) : MyDate.getStartOfDay(day);
            case week:
                return getEndDate ? MyDate.getEndOfWeek(day) : MyDate.getStartOfWeek(day);
            case month:
            default:
                return getEndDate ? MyDate.getEndOfMonth(day) : MyDate.getStartOfMonth(day);
        }
    }

    private static String getDateString(ShowGroupedBy groupBy, Date day) {
        switch (groupBy) {
            case day:
//                return MyDate.formatDateNew(day);
                return MyDate.formatDateNew(day, true, true, false, true, false);
            case week:
                return MyDate.getWeekStr(day);
            case month:
            default:
                return MyDate.getMonthAndYear(day);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private ItemList createProjectList(Item item, Item prevProject) {
//         Item project = item.getOwnerTopLevelProject(); //UI: do not show intermediate subprojects, only leaf tasks
//                if (project != null) {
//                    if (!project.equals(prevProject)) {
//                        projectList = new ItemList(project.getText(), false);
//                        if (listList != null) {
//                            listList.add(project);
//                        } else {
//                            dayList.add(project);
//                        }
//                        prevProject = project;
//                    }
//                } else {
//                    projectList = null; //reset list to null if no project (to store tasks directly in the list)
//                }
//    }
//</editor-fold>
    /**
     *
     * @param itemsSortedOnDate
     * @param makeNewDayListLabel
     * @param makeNewListListLabel
     * @return
     */
    private static ItemList buildStatisticsSortedByTime(List<Item> itemsSortedOnDate, List<WorkSlot> workSlotsSortedByStartDate) {
//        return buildStatisticsSortedByTime(itemsSortedOnDate, workSlotsSortedByStartDate,
//                SortStatsOnXXX.valueOfDefault(MyPrefs.statisticsSortBy.getString()),
//                ShowGroupedBy.valueOf(MyPrefs.statisticsGroupBy.getString()));
return null;
    }

    private static void addWorkSlotsToItemList(ItemList itemList, List<WorkSlot> workSlotsSortedByStartDate, Date startDate, Date endDate) {
//        itemList.setWorkSlotsInParse(workSlotsSortedByStartDate.getWorkSlotsInInterval(startDate, endDate, true, true).getWorkSlotListFull());
        itemList.setWorkSlotsInParse(WorkSlotList.getWorkSlotsInInterval(workSlotsSortedByStartDate, startDate, endDate, true, true));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    private static ItemList buildStatisticsSortedByTime(List<Item> itemsSortedOnDate, List<WorkSlot> workSlots, SortStatsOnXXX sortOn, ShowGroupedBy groupBy) {
//        boolean groupByDate = true || groupBy == ShowGroupedBy.day;
//        boolean groupByDate = true || groupBy == ShowGroupedBy.day;
        boolean groupTopLevelByDate = (groupBy != ShowGroupedBy.none
                && (sortOn == SortStatsOnXXX.dateAndTime || sortOn == SortStatsOnXXX.dateThenLists || sortOn == SortStatsOnXXX.dateThenCategories));
        boolean groupTopLevelByList = (sortOn == SortStatsOnXXX.dateThenLists || sortOn == SortStatsOnXXX.listsThenDates);
        boolean groupTopLevelByCategory = (sortOn == SortStatsOnXXX.dateThenCategories || sortOn == SortStatsOnXXX.categoriesThenDate);

        boolean groupSubtasksByProject = MyPrefs.statisticsGroupTasksUnderTheirProject.getBoolean();

        ItemList<ItemList> topLevelList = new ItemList(true);
        ItemList dateGroupList = null; //list of days
        ItemList ownerList = null; //list of lists (to group tasks by their ItemList)
        ItemList categoryList = null; //list of lists (to group tasks by their ItemList)
        ItemList projectList = null; //list of lists (to group tasks by their ItemList)

//        Date firstDate = new Date(Long.MIN_VALUE); //first Date in the dayList
        Date prevCompletedDate = null;
//        Item prevItem = null;
        ItemList prevOwnerList = null;
        Category prevCategory = null;
        Item prevTopLevelProject = null;

        assert !groupTopLevelByList || !groupTopLevelByCategory; //cannot group by both Owner and Category, either (or both) must be false

        for (Item item : itemsSortedOnDate) {

            switch (sortOn) {
                case dateAndTime:
                    //if new item has a different date
//            if (groupTopLevelByDate) {
                    Date completedDate = item.getCompletedDate();
                    if (prevCompletedDate == null || newDateGroup(groupBy, completedDate, prevCompletedDate)) {
                        //get and add WorkSlots
                        dateGroupList = new ItemList(getDateString(groupBy, completedDate), true, Icons.iconDateRange, false);
//                    dayList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                        addWorkSlotsToItemList(dateGroupList, workSlots, getDateForGroup(groupBy, completedDate, false), getDateForGroup(groupBy, completedDate, true));
                        topLevelList.add(dateGroupList);
                        prevOwnerList = null; //will ensure that we recalc Owner
                        prevCategory = null; //will ensure that we recalc Category
                        prevTopLevelProject = null; //will ensure that we recalc topLevelProject
                        prevCompletedDate = completedDate; //update prevCompletedDate

                        if (groupSubtasksByProject) {
                            Item topLevelProject = item.getOwnerTopLevelProject(); //UI: do not show intermediate subprojects, only leaf tasks
                            if (topLevelProject == null) {
                                projectList = null; //reset list to null if no project (to store tasks directly in the list)
                            } else if (!topLevelProject.equals(prevTopLevelProject)) { //prevTopLevelProject may be null or another project
                                projectList = new ItemList("Project: " + topLevelProject.getText(), true, Icons.iconMainProjects, false);
                                dateGroupList.add(projectList);
                                prevTopLevelProject = topLevelProject;
                            }
                            if (projectList != null) {
                                projectList.add(item);
                            } else {
                                dateGroupList.add(item);
                            }
                        } else {
                            dateGroupList.add(item);
                        }
//            } e
                        break;
                    }

                case listsThenDates:
                    if (groupTopLevelByList) {
//                ItemAndListCommonInterface owner = item.getOwner();
                        ItemList owner = item.getOwnerTopLevelList();
//                if (owner == null || !(owner instanceof ItemList)) {
//                    ownerList = new ItemList("Inbox", true, Icons.iconMainInbox, false);
////                    ownerList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
//                } else
                        if (owner instanceof ItemList) {
                            if (!owner.equals(prevOwnerList)) //                    ownerList = new ItemList("List: " + owner.getText(), true, Icons.iconList);
                            {
                                ownerList = new ItemList(owner.getText(), true, Icons.iconList, false);
                            }
                            topLevelList.add(ownerList);
                            dateGroupList = null;
//                    ownerList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                        } else { //task doesn't belong directly to an ItemList

                        }
//                    else {
//                    //same list as before
//                    assert owner.equals(prevOwnerList);
//                }
                        //add new list
                        if (dateGroupList != null) {
                            dateGroupList.add(ownerList);
                        } else {
                            topLevelList.add(ownerList);
                        }
                        //update prevOwner
                        prevOwnerList = owner instanceof ItemList ? (ItemList) owner : null;
                        //reset other lists
                        prevCategory = null; //will ensure that we recalc Category
                        prevTopLevelProject = null; //will ensure that we recalc topLevelProject
                    }
                    break;
                case dateThenCategories:

                    if (groupTopLevelByCategory) { //prevItem==null => first time round
                        Category category = item.getFirstCategory(); //UI: if an item has several categories, only the first one (in the categoryList order?!) is used
                        if (category == null) {
                            if (prevCategory != null) {
                                categoryList = new ItemList("No category", true, Icons.iconCategory, false);
                            }
//                    categoryList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                        } else if (!category.equals(prevCategory)) {
//                    categoryList = new ItemList("Category: " + category.getText(), true, Icons.iconCategory);
                            categoryList = new ItemList(category.getText(), true, Icons.iconCategory, false);
//                    categoryList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                        } else {
                            assert category.equals(prevCategory);
                        }
//                        if (false) { prevOwner = null; }//will ensure that we recalc Owner
                        //add new list
                        if (dateGroupList != null) {
                            dateGroupList.add(categoryList);
                        } else if (ownerList != null) {
                            ownerList.add(categoryList);
                        } else {
                            topLevelList.add(categoryList);
                        }
                        //update prevCategory
                        prevCategory = category;
                        //reset other lists
                        prevTopLevelProject = null; //will ensure that we recalc topLevelProject
                    }
                    break;
                case categoriesThenDate:

//            if (groupByProject && (prevItem == null || projectList == null || (topLevelProject = item.getOwnerTopLevelProject()) != null && !topLevelProject.equals(prevTopLevelProject))) {
                    if (groupSubtasksByProject) {
                        Item topLevelProject = item.getOwnerTopLevelProject(); //UI: do not show intermediate subprojects, only leaf tasks
                        if (topLevelProject == null) {
                            projectList = null; //reset list to null if no project (to store tasks directly in the list)
                        } else if (!topLevelProject.equals(prevTopLevelProject)) { //prevTopLevelProject may be null or another project
                            projectList = new ItemList("Project: " + topLevelProject.getText(), true, Icons.iconMainProjects, false);
//                    projectList.setFilterSortDef(FilterSortDef.getNeutralFilter());  //neutral is now default
                            //add new list
                            if (categoryList != null) {
                                categoryList.add(projectList);
                            } else if (ownerList != null) {
                                ownerList.add(projectList);
                            } else if (dateGroupList != null) {
                                dateGroupList.add(projectList);
                            } else {
                                topLevelList.add(projectList);
                            }
                        } else {
                            // do nothing: keep existing projectList
                            assert projectList != null;
                        }
                        //update prevTopLevelProject
                        prevTopLevelProject = topLevelProject;
                    }

                    //add item to appropriate list
                    if (projectList != null) {
                        projectList.add(item);
                    } else if (categoryList != null) {
                        categoryList.add(item);
                    } else if (ownerList != null) {
                        ownerList.add(item);
                    } else if (dateGroupList != null) {
                        dateGroupList.add(item);
                    } else if (topLevelList != null) {
                        topLevelList.add(item);
                    }
//                prevItem = item;
            }
        }
        return topLevelList;
    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">

    private static ItemList buildStatisticsSortedByTimeOLD(List<Item> itemsSortedOnDate, List<WorkSlot> workSlots, SortStatsOnXXX sortOn, ShowGroupedBy groupBy) {
//        boolean groupByDate = true || groupBy == ShowGroupedBy.day;
//        boolean groupByDate = true || groupBy == ShowGroupedBy.day;
        boolean groupTopLevelByDate = groupBy != ShowGroupedBy.none && (sortOn == SortStatsOnXXX.dateAndTime || sortOn == SortStatsOnXXX.dateThenLists || sortOn == SortStatsOnXXX.dateThenCategories);
        boolean groupTopLevelByList = (sortOn == SortStatsOnXXX.dateThenLists || sortOn == SortStatsOnXXX.listsThenDates);
        boolean groupTopLevelByCategory = (sortOn == SortStatsOnXXX.dateThenCategories || sortOn == SortStatsOnXXX.categoriesThenDate);

        boolean groupSubtasksByProject = MyPrefs.statisticsGroupTasksUnderTheirProject.getBoolean();

        ItemList<ItemList> topLevelList = new ItemList(true);
        ItemList dateGroupList = null; //list of days
        ItemList ownerList = null; //list of lists (to group tasks by their ItemList)
        ItemList categoryList = null; //list of lists (to group tasks by their ItemList)
        ItemList projectList = null; //list of lists (to group tasks by their ItemList)

//        Date firstDate = new Date(Long.MIN_VALUE); //first Date in the dayList
        Date prevCompletedDate = null;
//        Item prevItem = null;
        ItemList prevOwnerList = null;
        Category prevCategory = null;
        Item prevTopLevelProject = null;

        assert !groupTopLevelByList || !groupTopLevelByCategory; //cannot group by both Owner and Category, either (or both) must be false

        for (Item item : itemsSortedOnDate) {

            //if new item has a different date
            if (groupTopLevelByDate) {
                Date completedDate = item.getCompletedDate();
                if (prevCompletedDate == null || newDateGroup(groupBy, completedDate, prevCompletedDate)) {
                    //get and add WorkSlots
                    dateGroupList = new ItemList(getDateString(groupBy, completedDate), true, Icons.iconDateRange, false);
//                    dayList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                    addWorkSlotsToItemList(dateGroupList, workSlots, getDateForGroup(groupBy, completedDate, false), getDateForGroup(groupBy, completedDate, true));
                    topLevelList.add(dateGroupList);
                    prevOwnerList = null; //will ensure that we recalc Owner
                    prevCategory = null; //will ensure that we recalc Category
                    prevTopLevelProject = null; //will ensure that we recalc topLevelProject
                    prevCompletedDate = completedDate; //update prevCompletedDate
                }
            } else if (groupTopLevelByList) {
//                ItemAndListCommonInterface owner = item.getOwner();
                ItemList owner = item.getOwnerTopLevelList();
//                if (owner == null || !(owner instanceof ItemList)) {
//                    ownerList = new ItemList("Inbox", true, Icons.iconMainInbox, false);
////                    ownerList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
//                } else
                if (owner instanceof ItemList) {
                    if (!owner.equals(prevOwnerList)) //                    ownerList = new ItemList("List: " + owner.getText(), true, Icons.iconList);
                    {
                        ownerList = new ItemList(owner.getText(), true, Icons.iconList, false);
                    }
                    topLevelList.add(ownerList);
                    dateGroupList = null;
//                    ownerList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                } else { //task doesn't belong directly to an ItemList

                }
//                    else {
//                    //same list as before
//                    assert owner.equals(prevOwnerList);
//                }
                //add new list
                if (dateGroupList != null) {
                    dateGroupList.add(ownerList);
                } else {
                    topLevelList.add(ownerList);
                }
                //update prevOwner
                prevOwnerList = owner instanceof ItemList ? (ItemList) owner : null;
                //reset other lists
                prevCategory = null; //will ensure that we recalc Category
                prevTopLevelProject = null; //will ensure that we recalc topLevelProject
            } else if (groupTopLevelByCategory) { //prevItem==null => first time round
                Category category = item.getFirstCategory(); //UI: if an item has several categories, only the first one (in the categoryList order?!) is used
                if (category == null) {
                    if (prevCategory != null) {
                        categoryList = new ItemList("No category", true, Icons.iconCategory, false);
                    }
//                    categoryList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                } else if (!category.equals(prevCategory)) {
//                    categoryList = new ItemList("Category: " + category.getText(), true, Icons.iconCategory);
                    categoryList = new ItemList(category.getText(), true, Icons.iconCategory, false);
//                    categoryList.setFilterSortDef(FilterSortDef.getNeutralFilter()); //neutral is now default
                } else {
                    assert category.equals(prevCategory);
                }
//                        if (false) { prevOwner = null; }//will ensure that we recalc Owner
                //add new list
                if (dateGroupList != null) {
                    dateGroupList.add(categoryList);
                } else if (ownerList != null) {
                    ownerList.add(categoryList);
                } else {
                    topLevelList.add(categoryList);
                }
                //update prevCategory
                prevCategory = category;
                //reset other lists
                prevTopLevelProject = null; //will ensure that we recalc topLevelProject
            }

//            if (groupByProject && (prevItem == null || projectList == null || (topLevelProject = item.getOwnerTopLevelProject()) != null && !topLevelProject.equals(prevTopLevelProject))) {
            if (groupSubtasksByProject) {
                Item topLevelProject = item.getOwnerTopLevelProject(); //UI: do not show intermediate subprojects, only leaf tasks
                if (topLevelProject == null) {
                    projectList = null; //reset list to null if no project (to store tasks directly in the list)
                } else if (!topLevelProject.equals(prevTopLevelProject)) { //prevTopLevelProject may be null or another project
                    projectList = new ItemList("Project: " + topLevelProject.getText(), true, Icons.iconMainProjects, false);
//                    projectList.setFilterSortDef(FilterSortDef.getNeutralFilter());  //neutral is now default
                    //add new list
                    if (categoryList != null) {
                        categoryList.add(projectList);
                    } else if (ownerList != null) {
                        ownerList.add(projectList);
                    } else if (dateGroupList != null) {
                        dateGroupList.add(projectList);
                    } else {
                        topLevelList.add(projectList);
                    }
                } else {
                    // do nothing: keep existing projectList
                    assert projectList != null;
                }
                //update prevTopLevelProject
                prevTopLevelProject = topLevelProject;
            }

            //add item to appropriate list
            if (projectList != null) {
                projectList.add(item);
            } else if (categoryList != null) {
                categoryList.add(item);
            } else if (ownerList != null) {
                ownerList.add(item);
            } else if (dateGroupList != null) {
                dateGroupList.add(item);
            } else if (topLevelList != null) {
                topLevelList.add(item);
            }
//                prevItem = item;
        }
        return topLevelList;
    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private Component layoutStatistics(List<Item> itemsOnSameDate, SortStatsOn sortOn, long totalWorkTime) {
//        Container cont = new Container();
//        Date prevDate = null;
//        long totalActualForDay = 0;
//        long totalEstimatedForDay = 0;
//        switch (sortOn) {
//            case dateAndTime:
////                itemsOnSameDate.sort(FilterSortDef.getSortingComparator(Item.PARSE_COMPLETED_DATE, false)); //sort on completed date
//                Collections.sort(itemsOnSameDate, FilterSortDef.getSortingComparator(Item.PARSE_COMPLETED_DATE, false)); //sort on completed date
//                Container dayHeader = new Container(BoxLayout.y());
//                cont.add(dayHeader);
////                long totalWorkTime=0;
//                for (Item item : itemsOnSameDate) {
//                    cont.add(formatItemForStatisticsList(item));
//                    totalActualForDay += item.getActualEffort(false); //NOT for subtasks to avoid double-counting if onwer tasks were also
//                    totalEstimatedForDay += item.getEffortEstimate();
//                    prevDate = item.getCompletedDateD();
//                    if (prevDate == null || MyDate.isSameDate(item.getCompletedDateD(), prevDate)) {
//                    }
//                }
////                if (totalActualForDay != 0 || totalEstimatedForDay != 0 || totalWorkTime != 0) { //only show if at least one of the values is defined (different from zero)
////                    dayHeader.add(new Label(MyDate.formatTimeDuration(totalActualForDay)
////                            + (totalEstimatedForDay != 0 ? "/" + MyDate.formatTimeDuration(totalEstimatedForDay) : "")
////                            + (totalWorkTime != 0 ? "[" + MyDate.formatTimeDuration(totalEstimatedForDay) + "]" : "")
////                    )); // "1:13/2:15[2:00]"
//                dayHeader.add(formatDayHeaderForStatisticsList(totalActualForDay, totalEstimatedForDay, totalWorkTime)); // "1:13/2:15[2:00]"
////                }
//                break;
//            case lists:
////                itemsOnSameDate.sort(FilterSortDef.getSortingComparator(Item.PARSE_OWNER_LIST, false)); //sort on owner list
////                itemsOnSameDate.sort(FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_OWNER_LIST, Item.PARSE_COMPLETED_DATE}, new boolean[]{false, false})); //sort on owner list //TODO!!! move this outside to the complete list of tasks
//                Collections.sort(itemsOnSameDate, FilterSortDef.getMultipleComparator(new String[]{Item.PARSE_OWNER_LIST, Item.PARSE_COMPLETED_DATE}, new boolean[]{false, false})); //sort on owner list //TODO!!! move this outside to the complete list of tasks
//                ItemList itemList = null;
//                long totalActualForList = 0;
//                long totalEstimatedForList = 0;
//                Container listHeader = null;
//                Object owner = null;
//                for (Item item : itemsOnSameDate) {
//                    long actual = item.getActualEffort();
//                    long estimate = item.getEffortEstimate();
//                    totalActualForDay += actual;
//                    totalEstimatedForDay += estimate;
//                    totalActualForList += actual;
//                    totalEstimatedForList += estimate;
//
//                    owner = item.getOwner();
//                    //if task is from a new list
//                    if (itemList == null || (owner instanceof ItemList && !itemList.equals(owner))) {
//                        itemList = (ItemList) owner;
//                        //add data to existing header
//                        if (listHeader != null) {
//                            listHeader.add(formatListHeaderForStatisticsList(itemList, totalActualForList, totalEstimatedForList));
//                        }
//                        //reset counters
//                        totalActualForList = 0;
//                        totalEstimatedForList = 0;
//                        // create and add new header
//                        listHeader = new Container();
//                        cont.add(listHeader);
//                    }
//                }
//                break;
//            case categories:
//                break;
//        }
//        return cont;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns the sum of work time for the workslot on date
//     *
//     * @param workslotsSortedOnStartDate
//     * @param date
//     * @return
//     */
//    private long getWorkSlotSumForDay(List<WorkSlot> workslotsSortedOnStartDate, Date date) {
//        Date startOfStartDate = MyDate.getStartOfDay(date);
//        Date endOfStartDate = MyDate.getEndOfDay(date);
//        int i = 0;
//        int size = workslotsSortedOnStartDate.size();
//        //skip dates before startDate
//        while (i < size && workslotsSortedOnStartDate.get(0).getStartTimeD().getTime() < startOfStartDate.getTime()) {
//            i++;
//        }
//        List<WorkSlot> dayList = new ArrayList<>();
//        long sum = 0;
//        long now = System.currentTimeMillis();
//        while (i < size && MyDate.isSameDate(date, workslotsSortedOnStartDate.get(i).getStartTimeD())) {
//            sum += workslotsSortedOnStartDate.get(i).getDurationAdjusted(now);
//            i++;
//        };
//        return sum;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Container buildItemContainer(List<Item> itemList, List<WorkSlot> workSlots, SortStatsOn sortOn, long totalWorkTime) {
//        Container cont = new Container(BoxLayout.y());
//
//        while (!itemList.isEmpty()) {
//            Date firstDate = itemList.get(0).getCompletedDateD();
//            List<Item> dayList = new ArrayList<>();
//            Item item = null;
//            do {
//                item = itemList.remove(0);
//                dayList.add(item);
//            } while (MyDate.isSameDate(firstDate, item.getCompletedDateD()));
//
//            switch (sortOn) {
//                case dateAndTime:
//                    cont.add(layoutStatistics(dayList, sortOn, getWorkSlotSumForDay(workSlots, firstDate)));
//                    break;
//                case lists:
//                    break;
//                case categories:
//                    break;
//                default:
//            }
//        }
//        return cont;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     *
//     * @param content
//     * @return
//     */
////    protected Container buildItemListContainer(ItemList itemList, ItemList itemListList) {
//    protected Container buildItemListContainerXXX(ItemList itemList) {
//        Container cont = new Container(new BorderLayout());
//        Container leftSwipeContainer = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
//
////        MyDropContainer swipCont = new MyDropContainer(itemList, itemListList, null, bottomLeft, null, cont, () -> {return draggableMode;}); //use filtered/sorted ItemList for Timer
//        MyDragAndDropSwipeableContainer swipCont = new MyDragAndDropSwipeableContainer(leftSwipeContainer, null, cont) {
////<editor-fold defaultstate="collapsed" desc="comment">
////            @Override
////            public void drop(Component dragged, int x, int y) {
////                if (dragged == this || !isValidDropTarget((MyDragAndDropSwipeableContainer) dragged)) { //do nothing if dropped on itself
////                    return;
////                }
////                if (dragged instanceof MyDragAndDropSwipeableContainer) {
////                    Object dropTarget = getDraggedObject();
////                    Object draggedObject = ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject();
////                    List insertList = null;
////                    int index = -1;
////                    if (dropTarget instanceof Category) {
////                        if (draggedObject instanceof Category) {
////                            insertList = getDragAndDropList(); //insert items into the list of categories
////                            index = getDragAndDropList().indexOf(getDraggedObject());
////                        } else if (draggedObject instanceof Item) {
////                            insertList = getDragAndDropSubList(); //insert items into the Category itself
////                            index = 0; //insert in head of list
////                        }
////                    } else if (dropTarget instanceof ItemList) {
////                        if (draggedObject instanceof ItemList) {
////                            insertList = getDragAndDropList(); //insert items into the list of categories
////                            index = getDragAndDropList().indexOf(getDraggedObject());
////                        } else if (draggedObject instanceof Item) {
////                            insertList = getDragAndDropSubList(); //insert items into the Category itself
////                            index = 0; //insert in head of list
////                        }
////                    } else if (dropTarget instanceof Item) {
////                        if (draggedObject instanceof ItemList || draggedObject instanceof Category) {
//////                            refreshAfterDrop(); //TODO need to removeFromCache for a drop which doesn't change anything??
////                            return; //UI: dropping an ItemList onto an Item not allowed
////                        } else if (draggedObject instanceof Item) {
////                            if (x < this.getWidth() / 3 * 2) {
////                                insertList = getDragAndDropList(); //insert item as subtask of dropTarget Item
////                                index = getDragAndDropList().indexOf(getDraggedObject());
////                            } else {
////                                insertList = getDragAndDropSubList(); //insert item at the position of the dropTarget Item
////                                index = 0; //insert as first sub task
////                            }
////                        }
////                    }
//////                    assert insertList
////                    ((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList().remove(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
//////            DAO.getInstance().save(()((MyDragAndDropSwipeableContainer) dragged).getDragAndDropList());
//////                        if (x > this.getWidth() / 3 * 2) {
////////                        getDragAndDropSubList().add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
//////                            insertList.add(((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
//////                        } else {
//////                            getDragAndDropList().add(getDragAndDropList().indexOf(getDraggedObject()), ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
//////                        }
////                    insertList.add(index, ((MyDragAndDropSwipeableContainer) dragged).getDraggedObject());
////                    //SAVE both
////                    saveDragged();
////                    ((MyDragAndDropSwipeableContainer) dragged).saveDragged();
////                    dragged.setDraggable(false); //set draggable false once the drop (activated by longPress) is completed
////                    dragged.setFocusable(false); //set draggable false once the drop (activated by longPress) is completed
//////            ((MyForm) getComponentForm()).refreshAfterEdit(); //refresh/redraw
////                    refreshAfterDrop();
////                }
////            }
////</editor-fold>
//            @Override
//            public boolean isValidDropTarget(MyDragAndDropSwipeableContainer draggedObject) {
////                return !(draggedObject.getDragAndDropObject() instanceof CategoryList) && draggedObject.getDragAndDropObject() instanceof ItemList
//                return draggedObject.getDragAndDropObject() instanceof ItemList || draggedObject.getDragAndDropObject() instanceof Item;
//            }
//
//            @Override
//            public ItemAndListCommonInterface getDragAndDropList() {
////                return ((ItemList) getDragAndDropObject()).getOwnerList().getList(); //returns the owner of
//                return itemList.getOwner(); //returns the owner of
//            }
//
//            @Override
//            public List getDragAndDropSubList() {
////                return getDragAndDropList(); //returns the list of subtasks
//                return itemList.getList();
//            }
//
//            @Override
//            public Object getDragAndDropObject() {
//                return itemList;
//            }
//
//            @Override
//            public void saveDragged() {
//                DAO.getInstance().save(itemList);
//            }
//
//        }; //use filtered/sorted ItemList for Timer
//
//        if (keepPos != null) {
//            keepPos.testItemToKeepInSameScreenPosition(itemList, swipCont);
//        }
//
//        Accordion accr = new Accordion();
//        accr.setScrollableY(false);
//        accr.addContent("Item3", BoxLayout.encloseY(new Label("Label"), new TextField(),
//                new Button("Button"), new CheckBox("CheckBox")));
//
////        Button itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText()+(itemList.getWorkSlotListN()!=null?"#":""), swipCont, () -> true); //D&D
//        Button itemLabel = new MyButtonInitiateDragAndDrop(itemList.getText(), swipCont, () -> true); //D&D
//
//        cont.addComponent(BorderLayout.CENTER, itemLabel);
//
//        //SHOW/EDIT SUBTASKS OF LIST
//        Button editItemListPropertiesButton = new Button();
////        editItemPropertiesButton.setIcon(iconEdit);
//
//        editItemListPropertiesButton.setCommand(new Command("", Icons.iconEditSymbolLabelStyle) {
//            @Override
//            public void actionPerformed(ActionEvent evt) {
////                DAO.getInstance().fetchAllItemsIn((ItemList) itemList, true); //fetch all subtasks (recursively) before editing this list
//                new ScreenListOfItems(itemList.getText(), itemList, ScreenStatistics.this, (iList) -> {
//                    if (true) {
//                        ((MyForm) swipCont.getComponentForm()).setKeepPos(new KeepInSameScreenPosition(itemList, swipCont));
//                        itemList.setList(iList.getList());
//                        DAO.getInstance().save(itemList); //=> java.lang.IllegalStateException: unable to encode an association with an unsaved ParseObject
////                            swipCont.getParent().replace(swipCont, buildItemListContainer(itemList, itemListList), null); //update the container with edited content
//                        swipCont.getParent().replace(swipCont, buildItemListContainer(itemList), null); //update the container with edited content //TODO!! add animation?
//                    } else {
//
//                    }
//                    refreshAfterEdit();
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
//                }).show();
//            }
//        }
//        );
//
//        Container east = new Container(new BoxLayout(BoxLayout.X_AXIS_NO_GROW));
////        Button subTasksButton = new Button();
//
//        if (false && !itemList.getComment().equals("")) {
////            SpanLabel description = new SpanLabel(" (" + itemList.getComment() + ")");
//            Label description = new Label(" (" + itemList.getComment() + ")");
//            east.addComponent(description);
//        }
//
//        //EXPAND list
//        int numberUndoneItems = itemList.getNumberOfUndoneItems(false);
//        if (numberUndoneItems > 0) {
//            Button subTasksButton = new Button();
//            Command expandSubTasks = new Command("[" + numberUndoneItems + "]");// {
//            subTasksButton.setCommand(expandSubTasks);
////            subTasksButton.setIcon(Icons.get().iconShowMoreLabelStyle);
//            subTasksButton.setUIID("Label");
////            swipCont.putClientProperty("subTasksButton", subTasksButton);
//            swipCont.putClientProperty(MyTree2.KEY_ACTION_ORIGIN, subTasksButton);
//            east.addComponent(subTasksButton);
////            cont.setLeadComponent(subTasksButton); //ensure events generated by button arrives at main container??? WORKS! Makes the button receive every action from the container
////<editor-fold defaultstate="collapsed" desc="comment">
////            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]") {
////                @Override
////                public void actionPerformed(ActionEvent evt) {
////                    super.actionPerformed(evt); //To change body of generated methods, choose Tools | Templates.
////                }
////            }
////            );
////            subTasksButton.setCommand(new Command("[" + itemList.getSize() + "]")); //the Command doesn't have to do anything since Tree binds a listener to the button
////</editor-fold>
//        }
//        long remainingEffort = itemList.getRemainingEffort();
////        if (remainingEffort != 0) {
////            east.addComponent(new Label(MyDate.formatTimeDuration(remainingEffort)));
////        }
//
////        List<WorkSlot> workslots = itemList.getWorkSlotListN();
//        WorkSlotList workslots = itemList.getWorkSlotListN();
//        long sum = 0;
//        long now = System.currentTimeMillis();
////        List<WorkSlot> workslots2 = itemList.getWorkSlotListN();
////        for (WorkSlot ws:workslots2) {
////            sum+=ws.getDuration(ws.getDurationAdjusted(now));
////        }
////        long workTimeSumMillis = WorkSlot.sumWorkSlotList(workslots, now);
//        long workTimeSumMillis = workslots.getWorkTimeSum();
//
//        east.addComponent(new Label((remainingEffort != 0 ? MyDate.formatTimeDuration(remainingEffort) : "")
//                + (workTimeSumMillis != 0 ? ((remainingEffort != 0 ? "/" : "") + MyDate.formatTimeDuration(workTimeSumMillis)) : ""))); //format: "remaining/workTime"
//
//        east.addComponent(editItemListPropertiesButton);
//
////        east.addComponent(new Label(new SimpleDateFormat().format(new Date(itemList.getFinishTime(item, 0)))));
//        cont.addComponent(BorderLayout.EAST, east);
////        cont.setDraggable(true);
////        cont.setDropTarget(true);
//
//        return swipCont;
//    }
//</editor-fold>

    protected Container buildContentPane(ItemList itemListStats) {
        parseIdMap2.parseIdMapReset();
        if ((itemListStats != null && itemListStats.size() > 0)) {
            MyTree2 cl = new MyTree2(itemListStats, expandedObjects, null, null) {
                @Override
                protected Component createNode(Object node, int depth) {
                    Container cmp = null;
                    if (node instanceof Item) {
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> {                    },
//                    cmp = buildItemContainer((Item) node, null, () -> isDragAndDropEnabled(), () -> refreshAfterEdit(), false, //selectionMode not allowed for list of itemlists //TODO would some actions make sense on multiple lists at once??
//                            null, null, keepPos, expandedObjects, () -> animateMyForm(), false, false); //TODO!!! store expanded itemLists
//                    cmp = buildItemContainer(itemList, work);
                        cmp = ScreenListOfItems.buildItemContainer(ScreenStatistics.this, (Item) node, itemListStats, null, expandedObjects);
                    } else if (node instanceof ItemList) {
//                      cmp = buildCategoryContainer((Category) node, categoryList, keepPos, ()->refreshAfterEdit());
//                    cmp = ScreenListOfItemLists.buildItemListContainer((ItemList) node);
//                    cmp = ScreenListOfItemLists.buildItemListContainer((ItemList) node, keepPos);
//                    cmp = ScreenListOfItemLists.buildItemListContainer((ItemList) node, keepPos, true);
//                        cmp = ScreenListOfItemLists.buildItemListContainerStatistics((ItemList) node, null, true,expandedObjects);
                        cmp = ScreenListOfItemLists.buildItemListContainer((ItemList) node, null, true, expandedObjects);
                    } else {
                        assert false : "should only be Item or ItemList";
                    }
                    setIndent(cmp, depth);
                    return cmp;
                }

            };
            return cl;
        } else {
//            if (getShowIfEmptyList() != null)
            return BorderLayout.centerCenter(new SpanLabel("No completed tasks the last " + MyPrefs.statisticsScreenNumberPastDaysToShow.getInt() + " days to show statistics for"));
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private Container formatItemForStatisticsList(Item item) {
//        Container itemCont = new Container();
////                    cont.add(itemCont);
//        itemCont.add(new Label(item.getText()));
//        if (item.getActualEffort() != 0) {
//            itemCont.add(new Label(MyDate.formatTimeDuration(item.getActualEffort())
//                    + (item.getEffortEstimate() != 0 ? "/" + MyDate.formatTimeDuration(item.getActualEffort()) : ""))); // "0:13/0:15"
//        }
//        //show owner (project/list) inside task box
//        if (item.getOwner() instanceof ItemList) {
//            itemCont.add(new Label(((ItemList) item.getOwner()).getText()));
//        } else if (item.getOwner() instanceof Item) {
//            itemCont.add(new Label(((Item) item.getOwner()).getOwnerHierarchyAsString()));
//        }
//
//        return itemCont;
//    }
//
//    /**
//     * "1:13/2:15[2:00]"
//     *
//     * @param dayHeader
//     * @param totalActualForDay
//     * @param totalEstimatedForDay
//     * @param totalWorkTime
//     */
//    private Component formatDayHeaderForStatisticsList(long totalActualForDay, long totalEstimatedForDay, long totalWorkTime) {
//        if (totalActualForDay != 0 || totalEstimatedForDay != 0 || totalWorkTime != 0) { //only show if at least one of the values is defined (different from zero)
////            dayHeader.add(
//            return new Label(MyDate.formatTimeDuration(totalActualForDay)
//                    + (totalEstimatedForDay != 0 ? "/" + MyDate.formatTimeDuration(totalEstimatedForDay) : "")
//                    + (totalWorkTime != 0 ? "[" + MyDate.formatTimeDuration(totalEstimatedForDay) + "]" : ""));
//        }
//        return new Label();
//    }
//
//    private Component formatListHeaderForStatisticsList(ItemList itemList, long totalActualForList, long totalEstimatedForList) {
//        long totalWorkTime = 0; //TODO!!! get workTime for this list for this day
//        if (totalActualForList != 0 || totalEstimatedForList != 0 || totalWorkTime != 0) { //only show if at least one of the values is defined (different from zero)
////            dayHeader.add(
//            return Container.encloseIn(BoxLayout.y(), new Label(itemList.getText()),
//                    new Label(MyDate.formatTimeDuration(totalActualForList)
//                            + (totalEstimatedForList != 0 ? "/" + MyDate.formatTimeDuration(totalEstimatedForList) : "")
//                            + (totalWorkTime != 0 ? "[" + MyDate.formatTimeDuration(totalEstimatedForList) + "]" : "")));
//        }
//        return new Label();
//    }
//</editor-fold>
}
