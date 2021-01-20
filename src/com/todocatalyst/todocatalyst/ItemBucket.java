/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Log;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.ItemList.PARSE_ITEMS;
import static com.todocatalyst.todocatalyst.ItemList.PARSE_OWNER;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * ItemBucket creates a (potential hierarchy of) buckets to sort Items based on
 * their CompletedDate, List (top-lvel list for subtasks) or (first) Category.
 * It can also . The ItemBucket is initialized with a has function that is used
 * for sorting the sub-buckets using the comparator function given. It is used
 * by Statistics. Only the necessary buckets are create, so eg buckets are only
 * created for the categories or Lists actually used by the Items.
 * GroupByProject is a special case: at the lowest level of buckets where Items
 * are finally inserted, this will group Items belonging to the same top-level
 * project together to create a better overview. the functions used are
 * initialized by calling initBucket which will assign the appropriate functions
 * for the given depth of the ItemBucket.
 *
 * @author thomashjelm
 */
public class ItemBucket<E> extends ItemList {//implements ItemAndListCommonInterface{

    interface getName {

        String get(Item elt);
    }

    interface getDate {

        Date get(Date startDate);
    }

    interface getHash {

        Object get(Item elt);
    }

    interface getIcon {

        Character get();
    }

    interface getWorkSlots {

        List<WorkSlot> get(Item item);
    }

    protected Object hashValue; //the hash value stored for this bucket, used to sort them
    protected Comparator comparator; //used to sort the buckets based on the hash value
    protected getName name; //returns the name of a bucket
    protected getDate endDateFct; //returns the name of a bucket
    protected getHash hash; //return the hash value to determine which bucket to place the Item in
//    protected getWorkSlots workSlots; //return the hash value to determine which bucket to place the Item in
    protected char icon; //icon used when creating a bucket (displayed)
    protected boolean initialized; //true once the functions have been initialized
    int level;
    ScreenStatistics2.GroupOn2 groupOn;

    Date startTime;
    Date endTime;

    boolean groupByProject; //group subtasks into (top-level) projects
    boolean mostRecentFirst; //show oldest completed Items first

//    ItemBucket withoutCategoryGroup; //'global' bucket variable, A HACK, but should work since each bucket will
    private final static ItemBucket dummyForSearch = new ItemBucket(); //used for binary search to avoid creating a new bucket each time (efficiency)

    /**
     * only used to create a empty bucket when searching for hashValues
     */
    ItemBucket() {
//        super();
    }
//    String bucketName;

//    ItemBucket(Object hash, String bucketName) {
//        this.hashValue = hash;
////         this.bucketName=bucketName;
//        setText(bucketName);
//    }
    public ItemBucket(String listName, Character itemListIcon, Object hashValue, ItemBucket ownerBucket, List<Item> items) {
        super(listName, true, itemListIcon, false);
        setUseDefaultFilter(false);
        setNoSave(true);
        this.hashValue = hashValue;
        setOwner(ownerBucket);
        initBucket(this, 0); //get the bucket functions for this (level of) bucket
        if (items != null) {
            for (Item i : items) {
                addToBucket(i);
            }
        }
    }

    public ItemBucket(String listName, Character itemListIcon, Object hashValue, ItemBucket ownerBucket) {
        this(listName, itemListIcon, hashValue, ownerBucket, null);
    }

    /**
     * create an ItemBucket with the list of items and the given name
     *
     * @param items
     * @param name
     */
//    ItemBucket(String name, List<Item> items, List<WorkSlot> workSlotsSortedByStartDate) {
    ItemBucket(String name, List<Item> items) {
//        super();
//        setUseDefaultFilter(false);
//        initBucket(this, 0); //get the bucket functions for this (level of) bucket
//        setNoSave(true);
//        setText(name);
//        for (Item i : items) {
//            addToBucket(i);
//        }
        this(name, null, null, null, items);
        //TODO handle workslots
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ItemBucket)) {
            return false;
        }
        if (o instanceof ItemBucket) {
            if (((ItemBucket) o).hashValue == hashValue) {
                return true;
            }
            if (((ItemBucket) o).hashValue == null) {
                return false;
            }
            return (((ItemBucket) o).hashValue.equals(hashValue));
        } else {
            return false;
        }
    }

    private ItemBucket getOwnerBucket() {
//        return (ItemBucket) getOwner();
        ParseObject ownerList = getParseObject(PARSE_OWNER);
        //NB: don't fetch this owner in DAO because ItemBuckets are never saved!
        return (ItemBucket) ownerList;
    }

    Date getStartTime() {
        if (startTime != null) {
            return startTime;
        } else { //
            return getOwnerBucket().getStartTime(); //getOwnerBucket() should never be null
        }
    }

    Date getEndTime() {
        if (endTime != null) {
            return endTime;
        } else { //if eg this bucket is category or list
            return getOwnerBucket().getEndTime();
        }
    }

    /**
     * override to set the bucket functions for a bucket at depth level
     *
     * @param bucket
     * @param depthCount
     * @return
     */
    protected void initBucket(ItemBucket bucket, int depthCount) {
        getOwnerBucket().initBucket(bucket, depthCount + 1);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (!bucket.initialized) {
//            switch (depthCount) {
//                case 1: { //by date
////                    bucket.hash = (item) -> MyDate.getWitem.getCompletedDate();
//                    ShowGroupedBy groupBy = null;
//                    switch (groupBy) {
//                        case day:
//                        default:
//                            bucket.hash = (item) -> MyDate.getStartOfDay(item.getCompletedDate());
//                            break;
//                        case week:
//                            bucket.hash = (item) -> MyDate.getStartOfWeek(item.getCompletedDate());
//                            break;
//                        case month:
//                            bucket.hash = (item) -> MyDate.getStartOfMonth(item.getCompletedDate());
//                            break;
//                    }
//                    bucket.comparator = (Comparator<Date>) (d1, d2) -> Long.compare(d1.getTime(), d2.getTime()); //sort eg by dates, lists/categories
////                    ShowGroupedBy groupBy = null;
//                    switch (groupBy) {
//                        case day:
//                        default:
//                            bucket.name = (item) -> MyDate.formatDateNew(item.getCompletedDate(), true, true, false, true, false);
//                            break;
//                        case week:
//                            bucket.name = (item) -> MyDate.getWeekAndYear(item.getCompletedDate());
//                            break;
//                        case month:
//                            bucket.name = (item) -> MyDate.getMonthAndYear(item.getCompletedDate());
//                            break;
//                    }
//                    bucket.icon = Icons.iconDateRange;
//                    bucket.initialized = true;
//                    break;
//                }
//                case 2: { //by List
//                    bucket.hash = (item) -> item.getOwnerTopLevelList();
////                    bucket.comparator = (Comparator<ItemBucket>)(b1, b2) -> Integer.compare(ItemListList.getInstance().indexOf((ItemList) ((ItemBucket) b1).hashValue),
////                            ItemListList.getInstance().indexOf((ItemList) ((ItemBucket) b2).hashValue));
//                    bucket.comparator = (Comparator<ItemBucket>) (b1, b2) -> Integer.compare(ItemListList.getInstance().indexOf(b1.hashValue), ItemListList.getInstance().indexOf(b2.hashValue));
//                    bucket.name = (item) -> item.getOwnerTopLevelList().getText();
//                    bucket.icon = Icons.iconList;
//                    bucket.initialized = true;
//                    break;
//                }
//                case 3: { //by Category
//                    bucket.hash = (item) -> {
//                        Category firstCat = item.getFirstCategory();
//                        if (firstCat == null) {
//                            if (withoutCategoryGroup == null) {
//                                withoutCategoryGroup = new ItemBucket("No Category", Icons.iconCategory, hashValue, this);
//                            }
//                            return null;
//                        } else {
//                            return firstCat; //if null, will be sorted first/last in list of ItemBuckets for categories
//                        }
//                    };
////                    bucket.comparator = (i1,i2)->FilterSortDef.compareCategories(((Item)i1).getCategories(), ((Item)i2).getCategories()) ; //sort eg by dates, lists/categories
//                    //we can assume that when orting on categories, we will always have
////                    bucket.comparator = (Comparator<ItemBucket>)(b1, b2) -> FilterSortDef.compareCategoriesNoCatLast((Category) ((ItemBucket) b1).hashValue, (Category) ((ItemBucket) b2).hashValue);
//                    bucket.comparator = (Comparator<ItemBucket>) (b1, b2) -> FilterSortDef.compareCategoriesNoCatLast((Category) b1.hashValue, (Category) b2.hashValue);
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (b1 instanceof ItemBucket) {
////                            if (b2 instanceof ItemBucket) {
////                                FilterSortDef.compareCategories((Category) ((ItemBucket) b1).hashValue, (Category) ((ItemBucket) b2).hashValue);
////                            } else {
////                                FilterSortDef.compareCategories((Category) ((ItemBucket) b1).hashValue, (Category) ((ItemBucket) b2).hashValue);
////
////                            }
////                        } else if(b2 instanceof ItemBucket) {
////
////                        }
////                    };
////</editor-fold>
////sort eg by dates, lists/categories
//                    bucket.name = (item) -> item.getFirstCategory() != null ? item.getFirstCategory().getText() : "No Category";
//                    bucket.icon = Icons.iconCategory;
//                    bucket.initialized = true;
//                    break;
//                }
//                case 4: { //by Project
//                    bucket.hash = (item) -> item.getOwnerTopLevelProject();
//                    bucket.comparator = (item1, item2) -> 0; //sort eg by dates, lists/categories
//                    bucket.name = (item) -> item.getOwnerTopLevelList().getText();
//                    bucket.icon = Icons.iconMainProjects; //used??
//                    bucket.initialized = true;
//                    break;
//                }
//            }
//        }
//</editor-fold>
//        return getOwnerBucket().initBucket(bucket, 0);
    }

//    @Override
    public boolean addToBucket(ItemAndListCommonInterface elt) {
//        initializeIfNeeded(); //get the bucket functions for this (level of) bucket
//        initBucket(this, 0); //get the bucket functions for this (level of) bucket
        Item item = (Item) elt;
        boolean status = false;
        if (initialized) {
            if (hash == null) { //if no Bucket grouping at this level, simple add elt as a normal task
                //manually insert
                ItemAndListCommonInterface ownerTopPrj = item.getOwnerTopLevelProject();
                Date complDateNew = item.getCompletedDate();
                List fullList = getListFull();
                int insertIdx = -1;
                int size = fullList.size();
                for (int i = 0; i < size; i++) {
                    Object o = fullList.get(i);
                    if (groupByProject) {
                        if (ownerTopPrj != null) {
                            //the hashvalue for a project bucket is the *first* subtask encountered, so the bucket will be placed based on first subtask in sort order
//                        if (o instanceof ItemBucket && (((ItemBucket) o).hashValue.equals(item.getOwner()))) {
                            if (o instanceof ItemBucket && (((Item) ((ItemBucket) o).hashValue).getOwnerTopLevelProject().equals(item.getOwnerTopLevelProject()))) {
                                return ((ItemBucket) o).addToList(item, true, false); //add Item to Project bucket's list
                            } else if (o instanceof Item) {
                                if (ownerTopPrj.equals(((Item) o).getOwnerTopLevelProject())) {
//                                ItemBucket bucket = new ItemBucket("Project: " + ownerPrj.getText(), Icons.iconMainProjects, hash.get(item), this);
                                    ItemBucket bucket = new ItemBucket("Project: " + ownerTopPrj.getText(), Icons.iconMainProjects, o, this); //o=first (already) inserted subtask
                                    addToList(bucket, true, false); //add new Project bucket to its owner bucket, don't set owner, done above when bucket is created
                                    remove(o); //remove already inserted subtask
                                    bucket.addToList((Item) o, true, false); //add existing subtask
                                    return bucket.addToList(item, true, false); //add new subtask Item to buckets list
                                }
                            }
                        }
                    } //even with groupByProject true, fall through to lines below for the case where groupByProject doesn't create any buckets

//                if(o instanceof ItemBucket)
//                Object x = ((Item) ((ItemBucket) o).hashValue).getCompletedDate();
                    //The project buckets hasValue is the first subtask, so the date of that subtask is used for sorting the project bucket
                    Date complDatePrev = (groupByProject && o instanceof ItemBucket) ? ((Item) ((ItemBucket) o).hashValue).getCompletedDate() : ((Item) o).getCompletedDate();
                    if (insertIdx == -1 && ((!mostRecentFirst && complDatePrev.getTime() >= complDateNew.getTime())
                            || (mostRecentFirst && complDatePrev.getTime() <= complDateNew.getTime()))) {
                        insertIdx = i; //store index of *first* element in list with date *larger* then the one to insert => idx==insert position
                        if (!groupByProject) { //if we've found the insert index and are NOT grouping by project, no reason to iterate through rest of list since insertIdx won't change again
                            break;
                        }
                    }
                }
                if (!mostRecentFirst) {
                    int i = insertIdx == -1 ? 0 : insertIdx;
                    return addToList(i, item, false); //add Item to buckets list, false=DON'T make bucket owner of item!!
                } else {
                    int i = insertIdx == -1 ? size : insertIdx;
                    return addToList(i, item, false);
                }
            } else {
                Object hashValue = this.hash.get(item);
                ASSERT.that(dummyForSearch.hashValue == null);
                dummyForSearch.hashValue = hashValue;
//            ItemBucket bucket = getBucket(hashValue);
                ItemBucket bucket;
                int idx = 0;
//            int idx = Collections.binarySearch(this, hashValue, comparator);
                try {
                    idx = Collections.binarySearch(this, dummyForSearch, comparator);
                } catch (Exception e) {
                    ASSERT.that("");
                }
                dummyForSearch.hashValue = null;
                if (idx >= 0) {
                    bucket = (ItemBucket) getItemAt(idx);
                } else {
//                bucket = new ItemBucket(name.get(item), icon, hashValue, this); //create a new bucket with name given by function and this bucket as owner
                    bucket = new ItemBucket(name.get(item), icon, hashValue, this); //create a new bucket with name given by function and this bucket as owner
//                    if (workSlots != null) {
//                        bucket.addWorkSlots(workSlots.get());
//                    }
//                    if (bucket.endDateFct != null) {
                    if (endDateFct != null) {
                        bucket.startTime = (Date) hashValue;
//                        bucket.endTime = bucket.endDateFct.get((Date) hashValue);
                        bucket.endTime = endDateFct.get((Date) hashValue);
                    }
                    // insert new bucket sorted on hash value
                    int insertIdx = -(idx + 1);
                    addItemAtIndex(bucket, insertIdx, false); //insert new bucket into bucket's owner bucket
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//            ItemBucket bucket = getBucket(hashValue);
//            if (bucket == null) {
////                bucket = new ItemBucket(hash, getBucketName(elt));
//                bucket = new ItemBucket(hashValue, name.get(elt));
//                // insert new bucket sorted on hash value
//                int idx = Collections.binarySearch(this, elt, comparator);
//                int insertIdx = -(idx + 1);
//                addItemAtIndex(elt, insertIdx);
//            }
//            status = bucket.addToList(elt);
//</editor-fold>
                return bucket.addToBucket(item); //add item to appropriate bucket (wll recursively create the hierarchy and type of buckets needed
            }
        } else {
            ASSERT.that("call addToBucket on a not initialized bucket should never happen");
            return false;
        }
    }

    List<WorkSlot> cachedWorkSlots;

//    public List getWorkSlotsFromParseNOLD() {
//        if (level == 0) {
//            if (cachedWorkSlots == null) {
//                cachedWorkSlots = DAO.getInstance().getWorkSlots(getStartTime(), getEndTime());
//            }
//            return cachedWorkSlots;
//        } else if (groupOn == ScreenStatistics2.GroupOn2.lists || groupOn == ScreenStatistics2.GroupOn2.categories) {
//            return ((ItemList) hashValue).getWorkSlots(getStartTime(), getEndTime());
//        } else if ((groupOn == ScreenStatistics2.GroupOn2.day || groupOn == ScreenStatistics2.GroupOn2.week || groupOn == ScreenStatistics2.GroupOn2.month)) {
//            if (level == 1) {
//                if (hash != null) { //hash!=null means we have subbuckets
//                    List<WorkSlot> wslots = new ArrayList();
//                    for (ItemBucket bucket : (List<ItemBucket>) getListFull()) {
//                        List<WorkSlot> ws = ((ItemList) bucket.hashValue).getWorkSlotsFromParseN(); //we're only adding the worksltos previously stored in ItemBucket
//                        if (ws != null) {
//                            wslots.addAll(((ItemList) bucket.hashValue).getWorkSlotsFromParseN()); //we're only adding the worksltos previously stored in ItemBucket
//                        }
//                    }
//                    return wslots;
//                } else { //no sucbuckets, get workSlots from level above with this level's start/end time
//                    return ((ItemList) getOwnerBucket().hashValue).getWorkSlots(getStartTime(), getEndTime());
//                }
//            } else if (level == 2) {
//                return ((ItemList) getOwnerBucket().hashValue).getWorkSlots(getStartTime(), getEndTime());
//            }
//        }
//        return null;
//    }
    @Override
    public List getWorkSlotsFromParseN() {
        if (level == 0) { //I'm the top-level bucket
            if (cachedWorkSlots == null) { //cache to avoid fetching workslots from DAO each time a sub-bucket needs this
                cachedWorkSlots = DAO.getInstance().getWorkSlots(getStartTime(), getEndTime());
            }
            return cachedWorkSlots;
        } else if (hashValue instanceof ItemList) {
            return ((ItemList) hashValue).getWorkSlots(getStartTime(), getEndTime());
        } else if (hashValue instanceof Date) {
            if (hash == null) {// level == 2) { //lowest level
                //no sucbuckets, get workSlots from level above with this level's start/end time
//                return ((ItemList) getOwnerBucket().hashValue).getWorkSlots(getStartTime(), getEndTime());
                return getOwnerBucket().getWorkSlots(getStartTime(), getEndTime());
            } else {//else if (level == 1) {
//            if (hash != null) { //hash!=null => there are sub-buckets so sum up their worktime
                List<WorkSlot> wslots = new ArrayList();
                for (ItemBucket bucket : (List<ItemBucket>) getListFull()) {
                    ASSERT.that(bucket.hashValue instanceof ItemList,
                            () -> "bucket.hashValue NOT instanceof ItemList, hashValue=" + bucket.hashValue);
//                    List<WorkSlot> ws = ((ItemList) bucket.hashValue).getWorkSlotsFromParseN(); //we're only adding the worksltos previously stored in ItemBucket
                    if (bucket.hashValue != null) { //bucket.hashValue may be null, eg for bucket No Category
                        List<WorkSlot> ws = ((ItemList) bucket.hashValue).getWorkSlots(getStartTime(), getEndTime()); //we're only adding the worksltos previously stored in ItemBucket
                        if (ws != null) {
//                        wslots.addAll(((ItemList) bucket.hashValue).getWorkSlotsFromParseN()); //we're only adding the worksltos previously stored in ItemBucket
                            wslots.addAll(ws); //we're only adding the worksltos previously stored in ItemBucket
                        }
                    }
                }
                return wslots;
            }
//            }
        }
        return null;
    }

    public long getWorkSlotSum() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (level == 0) {
//            if (cachedWorkSlots == null) {
//                cachedWorkSlots = DAO.getInstance().getWorkSlots(getStartTime(), getEndTime());
//            }
//            return cachedWorkSlots;
//        } else if (groupOn == ScreenStatistics2.GroupOn2.lists || groupOn == ScreenStatistics2.GroupOn2.categories) {
//            return WorkSlot.getWorkTimeSum(((ItemList) hashValue).getWorkSlots(getStartTime(), getEndTime()));
//        } else if ((groupOn == ScreenStatistics2.GroupOn2.day || groupOn == ScreenStatistics2.GroupOn2.week || groupOn == ScreenStatistics2.GroupOn2.month)) {
//            if (level == 1) {
//                if (hash != null) { //hash!=null means we have subbuckets
//                    long workSlotDuration = 0;
//                    for (ItemBucket bucket : (List<ItemBucket>) getListFull()) {
//                        workSlotDuration += WorkSlot.getWorkTimeSum(((ItemList) bucket.hashValue).getWorkSlotsFromParseN());
//                    }
//                    return workSlotDuration;
//                } else { //no sucbuckets, get workSlots from level above with this level's start/end time
//                    return WorkSlot.getWorkTimeSum(((ItemList) getOwnerBucket().hashValue).getWorkSlots(getStartTime(), getEndTime()));
//                }
//            } else if (level == 2) {
//                return WorkSlot.getWorkTimeSum(((ItemList) getOwnerBucket().hashValue).getWorkSlots(getStartTime(), getEndTime()));
//            }
//        }
//        return 0;
//</editor-fold>
        return WorkSlot.getWorkTimeSum(getWorkSlotsFromParseN());
    }

    public List<E> getListFull() {
        List<E> list = getList(PARSE_ITEMS);
        if (list != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list, true); //optimization?? heavy operation, any way (OTHER than caching which does work
        } else {
            list = new ArrayList();
        }
        return list;
    }
}
