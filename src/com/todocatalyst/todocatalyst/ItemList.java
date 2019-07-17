package com.todocatalyst.todocatalyst;

//package todo;
import com.codename1.io.Log;
import com.codename1.ui.Component;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.tree.TreeModel;
import com.codename1.ui.util.EventDispatcher;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.Item.PARSE_DELETED_DATE;
import static com.todocatalyst.todocatalyst.Item.PARSE_RESTART_TIMER;
import static com.todocatalyst.todocatalyst.Item.PARSE_WORKSLOTS;
import static com.todocatalyst.todocatalyst.MyForm.getListAsCommaSeparatedString;
import static com.todocatalyst.todocatalyst.MyUtil.removeTrailingPrecedingSpacesNewLinesEtc;
//import com.todocatalyst.todocatalyst.MyTree.MyTreeModel;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
//import java.util.List;

/**
 * ItemList implements a ListModel. It extends the ListModel with operations
 * addItemAtIndex and moveSteps. It is an extension of BaseItem so it can be
 * stored etc. Implemented inspired by DefaultListModel source code from LWUIT.
 *
 * @author Thomas
 * @param <E>
 */
//public class ItemList<E extends ItemAndListCommonInterface> extends ParseObject
//public class ItemList<E extends ItemAndListCommonInterface> extends ParseObject
public class ItemList<E extends ItemAndListCommonInterface> extends ParseObject
        implements /*ItemListModel,*/
        MyTreeModel, /*Collection,*/ List, SumField, ItemAndListCommonInterface { //, Iterable { //, DataChangedListener {
//        MyTreeModel, /*Collection,*/ List, SumField, Iterable { //, DataChangedListener {
    //TODO implement deep fetchFromCacheOnly to get all tasks and sub-tasks at any depth
    //TODO: implement caching of worksum of sub-tasks (callback from subtasks to all affected owners and categories??)
    //TODO optimization: for long lists (e.g. size>100) add a hashmap to find the index faster than linear search in getItemIndex

    final static String CLASS_NAME = "ItemList";
    final static String ITEM_LIST = "List";

//    protected Vector<E> itemVector; // initialized in constructors //STORED
//    private List<E> itemList; // = new ArrayList(); // initialized in constructors //STORED
    final static String PARSE_TEXT = Item.PARSE_TEXT; //"description";
    final static String PARSE_COMMENT = Item.PARSE_COMMENT; //"comment";
    final static String PARSE_ITEMLIST = "itemList"; //subtasks
    final static String PARSE_ITEM_BAG = "itemBag"; //??
    final static String PARSE_WORKTIME_DEFINITION = "workTimeDef";
    final static String PARSE_OWNER = "owner";
    final static String PARSE_SOURCE_LISTS = "sourceLists"; //for meta-lists, not used yet
    final static String PARSE_META_LISTS = "metaLists";
    final static String PARSE_FILTER_SORT_DEF = Item.PARSE_FILTER_SORT_DEF; //"filterSort";
    final static String PARSE_WORKSLOTS = Item.PARSE_WORKSLOTS; //"filterSort";
//    final static String PARSE_DELETED = "deleted"; //has this object been deleted on some device?
//    final static String PARSE_DELETED_DATE = "deletedDate"; //has this object been deleted on some device?

    private boolean noSave = false; //set to true for temporary lists, e.g. wrapping a parse search results
//    private List<E> filteredSortedList = null;
//    private List<? extends ItemAndListCommonInterface> cachedList = null;
//    private List<E> cachedList = null;
//    private List<? extends ItemAndListCommonInterface> filteredSortedList = null;
//    private List<E> filteredSortedList = null;
    private int selectedIndex;// = 0;
    /**
     * contains the list of workslots (if defined, otherwise null)
     */
//    private WorkTimeDefinition workTimeDefinition; //STORED inline
    private EventDispatcher dataListener; // = new EventDispatcher();
    private EventDispatcher selectionListener; // = new EventDispatcher();
//    private Bag<E> itemBag; // = new HashtableAddedDirectly();
//    private List<WorkSlot> workSlotListBuffer;
//    private WorkSlotList workSlotListBuffer;
//    private WorkTimeDefinition workTimeDefinitionBuffer;
    private WorkTimeAllocator workTimeAllocator; //calculated when needed

    /**
     * used to save the underlying list when ItemList is not a ParseObject
     * itself. Eg.when wrapping the list of item subtasks in a temporary
     * ItemList
     */
//    MySaveFunction mySaveFunction;
    /**
     * thie item of which this list is a sublist
     */
//    Item sourceItem;
    /**
     * when this ItemList is filtered, sourceItemList points to the orginl eg
     * for worktomedefinition
     */
//    private ItemList sourceItemList;
    public ItemList(String listName, boolean temporaryNoSaveList, boolean saveImmediatelyToParse) {
        super(CLASS_NAME);
        setText(listName);
        setNoSave(temporaryNoSaveList);
        if (!temporaryNoSaveList && saveImmediatelyToParse) {
            DAO.getInstance().saveInBackground((ParseObject) this);
        }
    }

    protected ItemList(String PARSE_CLASS_NAME) {
        super(PARSE_CLASS_NAME);
    }

    public ItemList(String listName, boolean temporaryNoSaveList) {
        this(listName, temporaryNoSaveList, false);
    }

    public ItemList(boolean saveImmediatelyToParse, String listName) {
        this(listName, false, saveImmediatelyToParse);
    }

    public ItemList() {
        this("", false);
    }

//    public ItemList(List<E> list, MySaveFunction mySaveFunction) {
    /**
     *
     * @param list
     * @param sourceItem the Item providing the list of tasks to show in this
     * ItemList
     */
//    public ItemList(List<E> list, Item sourceItem) {
//        this();
////        this.mySaveFunction = mySaveFunction;
//        this.sourceItem = sourceItem;
////        itemList = new ArrayList(list);
////        itemList = list;
//        setList(list);
//    }
    public ItemList(List<E> list) {
//        this();
//        itemList = new ArrayList(list);
//        itemList = list;
//        setList(list);
        this(list, false);
    }

    public ItemList(String listName, List<E> list, FilterSortDef filterSortDef, boolean temporaryNoSaveList) {
        this();
        setText(listName);
        setList(list);
        setFilterSortDef(filterSortDef);
        setNoSave(temporaryNoSaveList);
    }

    public ItemList(String listName, List<E> list, boolean temporaryNoSaveList) {
        this(listName, list, null, temporaryNoSaveList);
    }

    public ItemList(String listName, List<E> list) {
        this(listName, list, null, false);
    }

    public ItemList(List<E> list, boolean temporaryNoSaveList) {
        this("", list, temporaryNoSaveList);
    }

    /**
     * create a new ItemList that is a copy of source
     */
    public ItemList(ItemAndListCommonInterface source) {
        this();
        source.copyMeInto(this);
    }

    @Override
    public ItemAndListCommonInterface cloneMe(Item.CopyMode copyFieldDefintion) {
        return cloneMe();
    }

    @Override
    public ItemAndListCommonInterface cloneMe(Item.CopyMode copyFieldDefintion, int copyExclusions) {
        return cloneMe();
    }

    public WorkTimeAllocator getWorkTimeAllocatorN() {
        if (workTimeAllocator == null && mayProvideWorkTime()) {
            workTimeAllocator = new WorkTimeAllocator(this);
        }
        return workTimeAllocator;
    }

    public void setWorkTimeAllocator(WorkTimeAllocator workTimeAllocator) {
        this.workTimeAllocator = workTimeAllocator;
    }

    @Override
    public void setNewFieldValue(String fieldParseId, Object objectBefore, Object objectAfter) {
        throw new Error("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    interface MySaveFunction {

        public boolean save();
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

    /**
     * marks this list as a temporary list that must not be dave do Parse
     *
     * @param temporaryNoSaveList
     */
    public void setNoSave(boolean temporaryNoSaveList) {
        noSave = temporaryNoSaveList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    long[] startTime;
//    long[] finishTime;
//
//    void calculateWorkTimeForItems() {
//        List<E> list = getList();
//        WorkTimeDefinition workList = getWorkTimeAllocatorN();
//        if (list != null && list.size() > 0 && workList != null) {
//            startTime = new long[list.size()];
//            finishTime = new long[list.size()];
//            for (int i = 0, size = list.size(); i < size; i++) {
//                startTime[i]=workList.getNextStartTime();
//                finishTime[i]= workList.getNextEndTime(list.get(i).getRemainingEffort());
//            }
//        }
//    }
//    protected SumVector remainingEffortSumVector
//            = new SumVector(
//                    //                    this,
//                    new SumVector.SumFieldSize() {
//                @Override
//                public int getSize() {
//                    return ItemList.this.getSize();
//                }
//            },
//                    new SumVector.SumFieldGetter() {
//                @Override
//                public long getFieldValue(int index) {
//                    return ((Item) getItemAt(index)).getRemainingEffort();
//                }
//            });
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    protected SumVector remainingEffortSumVector
//            = new SumVector(
//                    () -> ItemList.this.getSize(),
//                    (index) -> ((Item) getItemAt(index)).getRemainingEffort());
//    public void setSourceItemList(ItemList sourceItemList) {
//        this.sourceItemList = sourceItemList;
//    }
    /**
     * returns the original ItemList that this list was constructed from, if
     * any. If none, return this ItemList itself.
     *
     * @return
     */
//    public ItemList getSourceItemList() {
//        if (sourceItemList == null) {
//            return this;
//        } else {
//            return sourceItemList.getSourceItemList(); //recurse up to the original
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * the object that contains, or 'owns', this object. An object can have no
     * more than one owner. When the owner is deleted, so is the object.
     */
//    private ItemAndListCommonInterface owner;
    /**
     * autoAddRemoveListAsListenerOnInsertedItems can be used to turn of the
     * changeListener for the (few) ItemLists where this is not desired (e.g.
     * the ItemToCategoryConsistency ItemList)
     */
//    private boolean autoAddRemoveListAsListenerOnInsertedItems; // = true; //STORED
//</editor-fold>
    /**
     * stores the list of baseTypes that this list accepts
     */
//    private Vector baseTypes;// = null; //new Vector(); //STORED
    /**
     * if false, then items will only be added once, e.g. if an item being added
     * is already in the list, then nothing is done. Used when an ItemList is
     * used to store the items in a category where we don't want multiple
     * instances of the same item.
     */
//    private boolean storeOnlySingleInstanceOfItems; //= true; //STORED??
    /**
     * if true, then inserted BaseBaseItems with no Owner (getOwner()==null)
     * will automatically get this list as owner (except Category since we don't
     * want them to own items)
     */
//    private boolean makeItemListOwnerOfInsertedUnownedItems = true;
    /**
     * list of all the sources / sublists of this list, e.g. can be a mix of
     * Projects, Categories, ...
     */
//    private List<ItemList<E>> sourceLists; // = new ItemList(this, false);
    /**
     * list of all lists for which this list is a sublist. Used to keep track of
     * which lists to update when this list changes. Is updated by the
     * metalists, e.g. when a list is added to a metalist, it also adds itself
     * to the metaLists of its sublist.
     */
//    private List<ItemList<E>> metaLists; // = new ItemList(this, false);
    /**
     * when the ItemList has sourcelists, itemBag counts how many times each
     * item has been added to the ItemList either directly or via sourcelists.
     * If there are no sourceLists, itemBag is null.
     */
    final static int FIELD_DESCRIPTION = 0;
    final static int FIELD_COMMENT = 1;
//    private final static FieldDef[] FIELDS = {
//        //        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        new FieldDef(FIELD_DESCRIPTION, "Name", Expr.VALUE_FIELD_TYPE_STRING),
//        new FieldDef(FIELD_COMMENT, "Comment", Expr.VALUE_FIELD_TYPE_STRING)
//    };
//<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * list of elements that are selected in this list. WHY is it put here with
     * list??
     */
//    SelectedItems selection;
//
//    public SelectedItems getSelection() {
//        return selection;
//    }
//
//    public void setSelection(SelectedItems selection) {
//        this.selection = selection;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<E> getInternalList() {
////        return itemList;
//        return getList();
//    }
    /**
     * default false: meaning that only the guid of the items held in the list
     * are written. If true, then the full items are written
     */
//    boolean writeListItemsGuidOnly = true; //if true, write guid only (normal case)
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
// WHEN ADDING NEW ATTRIBUTES:  update methods: copy(), equals(), hashCode(), writeObject(), readBaseItem(), toString(), delete(), add getters/setters
//    public ItemList(int[] baseTypes, BaseBaseItem owner, boolean setOwnerAsListener, boolean saveDirectly,
//            boolean itemCategoryAutoConsistency, boolean storeOnlySingleInstanceOfItems,
//            boolean writeListItemsGuidOnly, boolean autoAddRemoveListAsListener) {
////        super(owner, setOwnerAsListener, saveDirectly, itemCategoryAutoConsistency);
////        setTypeId(BaseItemTypes.ITEMLIST);
////        this.itemVector = new Vector();
//        this();
//        this.itemList = new ArrayList();
//        setBaseTypesImpl(baseTypes);
//        this.storeOnlySingleInstanceOfItems = storeOnlySingleInstanceOfItems;
//        this.writeListItemsGuidOnly = writeListItemsGuidOnly;
////        this.autoAddRemoveListAsListenerOnInsertedItems = autoAddRemoveListAsListener;
//    }
//    public ItemList(int[] baseTypes, BaseBaseItem owner, boolean setOwnerAsListener, boolean saveDirectly, boolean itemCategoryAutoConsistency, boolean storeOnlySingleInstanceOfItems, boolean writeListItemsGuidOnly) {
//        this(baseTypes, owner, setOwnerAsListener, saveDirectly, itemCategoryAutoConsistency, storeOnlySingleInstanceOfItems, writeListItemsGuidOnly, true);
//    }
//    public ItemList(int baseType, BaseBaseItem owner, boolean setOwnerAsListener, boolean saveDirectly, boolean itemCategoryAutoConsistency, boolean storeOnlySingleInstanceOfItems, boolean writeListItemsGuidOnly, boolean autoAddRemoveListAsListener) {
//        this(new int[]{baseType}, owner, setOwnerAsListener, saveDirectly, itemCategoryAutoConsistency, storeOnlySingleInstanceOfItems, writeListItemsGuidOnly, autoAddRemoveListAsListener);
//    }
//    public ItemList(boolean saveDirectly) {
//        this(null, null, false, saveDirectly, false, false, true);
//    }
//    public ItemList(boolean saveDirectly, boolean autoAddRemoveListAsListener) {
//        this(null, null, false, saveDirectly, false, false, true, autoAddRemoveListAsListener);
//    }
    /**
     * used for temporary throw-away lists. Behaves like a completely ordinary
     * list, nothing fancy ;-) If saved, only stores guids of its elements, not
     * the full items.
     */
//    public ItemList() {
////        this(null, false);
////        this(false);
////        setEnsureItemCategoryAutoConsistency(false);
//        this(null, null, false, false, false, false, true);
//    }
    /**
     * used for temporary throw-away lists storing items of baseType only
     */
//    public ItemList(int baseType) {
////        this();
////        addBaseType(baseType);
//        this(new int[]{baseType}, null, false, false, false, false, true);
//    }
//    public ItemList(int baseType, BaseItem ownerAndListener, boolean saveDirectly) {
//        this(new int[]{baseType}, ownerAndListener, true, saveDirectly, false, false, true);
//
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @param baseType
     * @param ownerAndListener, or null
     * @param saveDirectly
     * @param itemCategoryAutoConsistency
     * @param storeOnlySingleInstanceOfItems
     */
//    public ItemList(int baseType, BaseItem ownerAndListener, boolean saveDirectly, boolean itemCategoryAutoConsistency, boolean storeOnlySingleInstanceOfItems, boolean writeListItemsGuidOnly) {
//        this(new int[]{baseType}, ownerAndListener, ownerAndListener == null ? false : true, saveDirectly, itemCategoryAutoConsistency, storeOnlySingleInstanceOfItems, writeListItemsGuidOnly);
//    }
//    public ItemList(int baseType, BaseItem ownerAndListener, boolean saveDirectly, boolean itemCategoryAutoConsistency, boolean storeOnlySingleInstanceOfItems) {
//        this(new int[]{baseType}, ownerAndListener, ownerAndListener == null ? false : true, saveDirectly, itemCategoryAutoConsistency, storeOnlySingleInstanceOfItems, true);
//    }
//    public ItemList(int baseType, boolean saveDirectly, boolean itemCategoryAutoConsistency) {
//        this(new int[]{baseType}, null, false, saveDirectly, itemCategoryAutoConsistency, false, true);
//    }
//    public ItemList(int baseType, boolean saveDirectly) {
//        this(new int[]{baseType}, null, false, saveDirectly, false, false, true);
//    }
//    public ItemList(E[] items) {
//        this();
//        this.itemVector = createVector(items); // createVector creates a new Vector
//    }
//    public ItemList(Vector items) {
//        this();
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        this.itemVector = items; // createVector creates a new Vector
//    }
    /**
     * returns remaining effort for the entire list
     *
     * @return
     */
//    public long getRemainingEffort() {
//        return getSumOfRemainingEffort(getSize());
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getFinishTime(int index) {
//        if (workTimeDefinition != null) {
////            return workTimeDefinition.getNextEndTime(getItemAt(index).getRemainingEffort());
//            return workTimeDefinition.getFinishTime(index);
//        } else {
//            return 0;
//        }
//    }
//</editor-fold>
    /**
     *
     * @param subSum is the sum of the sub-tasks
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getFinishTimexxx(E subtask, long subSum) {
//        long newSubSum = this.getSumOfRemainingEffort(subtask) - subtask.getRemainingEffort() + subSum;
//        if (workTimeDefinition != null) {
//            return workTimeDefinition.getNextEndTime(newSubSum);
//        } else {
//            Object owner = getOwner();
//            if (owner != null) {
//                if (owner instanceof Item) {
//                    return ((Item) owner).getFinishTime(newSubSum);
//                } else if (owner instanceof ItemList) {
//                    ItemList ownerList = (ItemList) owner;
//                    return ownerList.getFinishTimexxx(this, newSubSum);
//                }
//            } else {
//                return 0;
//            }
//        }
//        return 0;
//    }
//    public long getFinishTime(ItemAndListCommonInterface itemOrList) {
//        return getFinishTime(itemOrList, itemOrList.getRemainingEffort());
//    }
//</editor-fold>
    @Override
    public int size() { //these operation must operate on visible (filtered) list!
//        return getSize();
//        return (getListFull() == null) ? 0 : getListFull().size();
//        return getSize();
        return getList().size();
    }

    @Override
    public boolean isEmpty() {
//        return getSize() == 0;
        return size() == 0;
    }

    @Override
    public boolean add(Object e) {
        addToList((E) e, true);
        return true;
    }

    @Override
    /**
     * return the item in the filtered/sorted list
     */
    public Object get(int index) {
//        return getItemAt(index);
        return getList().get(index);
    }

//    public Object getFull(int index) {
////        return getItemAt(index);
//        return getListFull().get(index);
//    }
    @Override
    public Object set(int index, Object element) {
//        return setItemAtIndex((E)element, index);
        return setToList(index, (E) element);
    }

    @Override
    public void add(int index, Object element) {
//        addItemAtIndex((E) element, index);
        ASSERT.that(false, "check if below works correctly wrt getListFull etc");
//        addToList(index, (E) element);
        ItemAndListCommonInterface refElt = getList().get(index); //find the reference element (NB! won't work if multiple copies of same element in the list!)
        addToList((E) element, refElt, false);
    }

    @Override
    public E remove(int index) {
        assert false : "check if below works correctly wrt getListFull etc";
//        E obj = getItemAt(index);
        E obj = getList().get(index);
//        removeItem(index);
        removeItem(obj);
        return obj;
    }

    @Override
    public int indexOf(Object o) {
//        return getItemIndex((E) o);
        return getList().indexOf((E) o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return indexOf((E) o); //assuming there will only be a single instance of each object in the list
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
//        if (sourceItem != null) {
//            return sourceItem.getText();
//        } else {
        String s = getString(PARSE_TEXT);
        if (s == null) {
            return "";
        } else {
            return s;
        }
//        }
//        return (description != null ? description : "");
    }

    /**
     * sets the key text string for the subtypes of BaseItem, e.g. for Item
     * Description, for Category categoryName, ... setText() in BaseItem is not
     * supposed to be used directly but must be overwritten by subtypes.
     *
     * @param text
     * @return
     */
    @Override
    public void setText(String text) {
        if (MyPrefs.itemRemoveTrailingPrecedingSpacesAndNewlines.getBoolean()) {
            text = removeTrailingPrecedingSpacesNewLinesEtc(text);
        }
        if (text != null && !text.equals("")) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_TEXT, text);
        } else {
            remove(PARSE_TEXT);
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
        if (val != null && !val.equals("")) { //don't test for val != null to avoid silent failure on this error condition
            put(PARSE_COMMENT, val);
        } else {
            remove(PARSE_COMMENT);
        }
//        if (!this.comment.equals(val)) {
//            this.comment = val;
//        }
    }

    /**
     * returns sources (may be null if not previously set)
     *
     * @return
     */
    public List<ItemList<E>> getSubLists() {
//        return sourceLists;
        return getSourceLists();
    }

    public List<ItemList<E>> getSourceLists() {
        if (false) {
            setList(null); //TODO!!!!????
        }
        List<ItemList<E>> list = getList(PARSE_SOURCE_LISTS);
        if (list != null) {
            return list;
        } else {
            return new ArrayList();
        }
    }

    public void setSourceLists(List<ItemList<E>> sourceList) {
        if (sourceList != null && !sourceList.isEmpty()) {
            put(PARSE_SOURCE_LISTS, sourceList);
        } else {
            setItemBag(null); //if no (more) source lists, remove itembags
            remove(PARSE_SOURCE_LISTS);
        }
    }

    /**
     * add sourceList to the list. If sourceList already present, nothing is
     * done. Before adding it should be checked if there is a circular reference
     * from the added subList to this list to handle it in the UI (if not, an
     * exception will be thrown).
     *
     * @return false if sourceList cannot be added since it contains a circular
     * reference back to this list.
     */
    public void addSubList(ItemList<E> subList) throws Exception {
//        if (sourceLists == null) {
//            sourceLists = new LinkedList<ItemList<E>>();
//            itemBag = new Bag<E>();
        //when initiating subLists, add all items already in this list
//            itemBag.addAll(itemList);
//            itemBag.addAll(getList());
        Bag<E> itemBag = getItemBag();
        if (itemBag == null) {
            itemBag = new Bag();
        }
        itemBag.addAll(getListFull());
        setItemBag(itemBag);
//        }
        List<ItemList<E>> sourceLists = getSourceLists();
        if (!sourceLists.contains(subList)) {
            if (subList.circularReferenceTo(this) == null) {
                sourceLists.add(subList);
                setSourceLists(sourceLists);
//                itemBag.addAll(subList);
//                itemVector.addAll(subLists); //UI: add all new items to the end of the list
                for (Iterator it = subList.iterator(); it.hasNext();) {
                    E item = (E) it.next();
                    if (itemBag.getCount(item) == 0) { //only add to this ItemList if *not* already in the list
                        addItem(item); //UI: add all new items to the end of the list. 
                    }
                }
                subList.addMetaList(this);
            } else {
                throw new Exception("Circular reference to " + subList.circularReferenceTo(this) + " not caught before call to addSubList.");
            }
        }
    }

    /**
     * addSourceListsContainingObj returns list of ItemLists (contained in this
     * ItemList) that contains directly (or indirectly!) obj.
     *
     * @param obj
     * @return
     */
    public ItemList circularReferenceTo(ItemList itemList) {
        if (itemList == null || itemList.getSubLists() == null || itemList.getSubLists().size() == 0) {
            return null;
        } else {
            for (Object subList : itemList.getSubLists()) {
                ItemList circularReferenceList = ((ItemList<E>) subList).circularReferenceTo(itemList); //iterate recursively down through the hierarchy
                if (circularReferenceList != null) {
                    return circularReferenceList;
                }
            }
        }
        return null;
    }

    /**
     * remove a source ItemList. This also removes all the itemList's items
     * (unless they have also been added either directly or via another source
     * list)
     *
     * @return
     */
    public boolean removeSubList(ItemList<E> itemList) {
        if (itemList == null || getSourceLists().isEmpty() || !getSourceLists().contains(itemList)) {
            Log.p("Trying to remove itemList=" + itemList + " from SourceLists = " + getSourceLists());
            return false;
        } else {
            List<ItemList<E>> sourceLists = getSourceLists();
            if (sourceLists.contains(itemList)) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            itemBag.removeAll(subList);
//            Bag itemBag = getItemBag();
//            if (itemBag != null) {
//                for (Object item : itemList) {
//                    if (itemBag.remove((E) item)) { //remove returns true if
//                        removeItem((E) item);
//                    }
//                }
//                setItemBag(itemBag);
//            }
//</editor-fold>
                for (E item : itemList.getListFull()) {
                    removeItem(item); //also removes item from itemBag
                }
                sourceLists.remove(itemList);
                setSourceLists(sourceLists);
                itemList.removeMetaList(this); //remove the reference from SourceList to this 

                return true;
            }
        }
        return false;
    }

    /**
     * returns true if there are sources (sublists) defined
     *
     * @return
     */
    public boolean hasSubLists() {
//        return sourceLists != null && sourceLists.size() > 0;
        return has(PARSE_SOURCE_LISTS) && !getSourceLists().isEmpty();
    }

    /**
     * updates the sources to match the new sources list (adds new source lists
     * and removes source lists not in the new source list)
     *
     * @return
     */
    public void setSubLists(List sources) {
//        getSubLists().updateListWithDifferences(sources);
        List addedObjetcs = getAddedItems(sources);
        List removedObjects = getRemovedItems(sources);
//        if (addedObjetcs.getSize() != 0 || removedObjects.getSize() != 0) { //do nothing if no change
        addItems(addedObjetcs);
        removeItems(removedObjects);
//        }
    }

    /**
     * will update list to contain the same elements as newList by
     * adding/deleting the different elements one by one.
     *
     * @param list
     * @param newList
     * @return true if list was receiveChangeEvent by adding or removing
     * elements
     */
//    public static boolean updateListWithDifferences(ItemListModel list, ItemListModel newList) {
    public boolean updateListWithDifferencesXXX(List newList) {
        if (getSize() == 0 && newList.size() == 0) {
            return false;  //do  nothing if both lists are empty
        }
        boolean change = false;
        List addedObjetcs = getAddedItems(newList);
        List removedObjects = getRemovedItems(newList);
        if (addedObjetcs.size() != 0 || removedObjects.size() != 0) {
            addItems(addedObjetcs);
            removeItems(removedObjects);
            change = true;
        }
        return change;
    }

    /**
     * list of all lists for which this list is a sublist. Used to keep track of
     * which lists to update when this list changes. Is updated by the
     * metalists, e.g. when a list is added to a metalist, it also adds itself
     * to the metaLists of its sublist.
     */
    public void addMetaList(ItemList metaList) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (metaLists == null) {
//            metaLists = new LinkedList<ItemList>();
//        }
//        metaLists.add(metaList);
//</editor-fold>
        List<ItemList<E>> metaLists = getMetaList();
        metaLists.add(metaList);
        setMetaList(metaLists);
    }

    /**
     * returns sources (may be null if not previously set)
     *
     * @return
     */
    public void removeMetaList(ItemList metaList) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (metaLists == null || !metaLists.contains(metaList)) {
//            return;
//        } else {
//            metaLists.remove(metaList);
//        }
//</editor-fold>
        List<ItemList<E>> metaLists = getMetaList();
        metaLists.remove(metaList);
        setMetaList(metaLists);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the int sum of all the Items' remaining effort, up to and
     * including index. Returns 0 if list is empty, if there are no Items in the
     * list. Useful eg to sum of the total effort of elements in the list.
     *
     * @param index
     * @return
     */
//    public long getSumOfRemainingEffort(int index) {
////            return getSumAt(index, Item.FIELD_EFFORT_REMAINING);
//        return remainingEffortSumVector.getSumAt(index);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the int sum of all the Items' remaining effort, up to and
     * including item. Returns 0 if list is empty, if there are no Items in the
     * list. Useful eg to sum of the total effort of elements in the list.
     *
     * @param index
     * @return
     */
//    public long getSumOfRemainingEffort(E item) {
////        int itemIndex = getItemIndex(item);
////        if (itemIndex >= 0) {
////            return remainingEffortSumVector.getSumAt(itemIndex);
////        } else {
////            return 0;
////        }
//        return getSumOfRemainingEffort(getItemIndex(item));
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the first item where the sum of remaining effort for previous
     * items in the list + remaining effort of this item adds up to less or
     * equal to sum. (SUM(previous items)<sum) AND (SUM(previous
     * items)+remaining effort <= sum)
     *
     * @param sum
     * @return
     */
//    public E getItemAtSumOfRemainingEffort(long sum) {
////            return remainingEffortSumVector.getItemAtSum(sum);
//        return getItemAt(remainingEffortSumVector.getIndexAtSum(sum));
//    }
    /**
     * returns the index of the first Object that has a sum value greater than
     * or equal to sum. If no such element is found, e.g. sum is bigger than
     * total sum of all items in list, then -1 is returned.
     *
     * @param targetSum
     * @return index or -1
     */
//    public int getIndexAtSumOfRemainingEffort(long sum) {
//        return remainingEffortSumVector.getIndexAtSum(sum);
//    }
//</editor-fold>
//    public boolean isExpandable() {
//        return getSize() > 0;
//    }
    /**
     * returns the item that matches (exactly) Text. Used to check for duplicate
     * definition of e.g. Categories (to avoid that two different categories
     * have the same user-visible name)
     *
     * @param text to search for
     * @param ignoreCase ignore case
     * @param containsString just check if text is contained in longer text
     * string
     * @return
     */
    private E findItemWithText(String text, boolean ignoreCase, boolean containsString) {
        boolean found = false;
        for (int i = 0, size = getSize(); i < size; i++) {
            E item = getItemAt(i);
//            if (item instanceof E) {
            if (containsString) {
                found = ignoreCase ? item.getText().toUpperCase().indexOf(text.toUpperCase()) != -1 : item.getText().indexOf(text) != -1;
            } else { //exact match
                found = ignoreCase ? item.getText().equalsIgnoreCase(text) : item.getText().equals(text);
            }
            if (found) {
                return item;
            }
//            }
        }
        return null;
    }

    /**
     * returns the item that matches text. Used to check for duplicate
     * definition of e.g. Categories (to avoid that two different categories
     * have the same user-visible name)
     *
     * @return
     */
    E findItemWithText(String text) {
        return findItemWithText(text, true, false);
    }

//    void setItemList(ItemList itemList) {
    @Override
    public void setList(List itemList) {
//    public void setList(List<? extends ItemAndListCommonInterface> itemList) {
//        itemList.copyMeInto(this);
//        if (sourceItem != null) {
//            sourceItem.setItemList(itemList);
////        } else if (has(PARSE_ITEMLIST) || (itemList != null && !itemList.isEmpty())) {
//        } else 
        if (itemList != null && !itemList.isEmpty()) {
            put(PARSE_ITEMLIST, itemList);
//            filteredSortedList = null; //reset to use and re-sort new list
        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
            remove(PARSE_ITEMLIST); //if setting a list to null or setting an empty list, then simply delete the field
        }
//        cachedList = null; //reset
//        filteredSortedList = null; //reset
    }

//    List<E> getList(int index, int amount) {
//        DAO.getInstance().get
//    }
//    @Override
    @Override
    public List<E> getList() {
//    public List getList() {
//    public List<? extends ItemAndListCommonInterface> getList() {
//<editor-fold defaultstate="collapsed" desc="comment">
////        List<E> list = getList(PARSE_ITEMLIST);
//
////        if (sourceItem != null) {
////            return sourceItem.getList();
////        } else
//        {
////            assert isDataAvailable():"error, this list should have been fetched previously";
//            if (!isDataAvailable()) {
//                DAO.getInstance().fetchIfNeededReturnCachedIfAvail(this); //WON'T WORK since new impl of fetchIfNeeded(this) may return a previously existing instance instead of simply fetching the data for 'this'
//            }
//            List<E> list = getList(PARSE_ITEMLIST);
//            if (list != null) {
////            for (E o:list) {ParseObject.fetchFromCacheOnly(o)};
////            ParseQuery. //include the linked
//                DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
//                FilterSortDef filterSortDef = getFilterSortDef();
//                if (filterSortDef != null && filteredList == null) { //buffer the sorted list
//                    filteredList = filterSortDef.filterAndSortItemList(list);
//                }
//                if (filteredList != null) { //reuse
//                    list = filteredList;
//                }
//                return list;
//            } else {
////            return null; //returning null would mean every user must check for null and create a list. And returning a new empty ArrayList and saving it doesn't have any side-effect since a new empty list isn't actually saved
//                return new ArrayList();
//            }
//        }
//</editor-fold>
        List<E> list = getListFull();
//        List<? extends ItemAndListCommonInterface> list = getListFull();
//        FilterSortDef filterSortDef = getFilterSortDef();
        FilterSortDef filterSortDef;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null && filteredSortedList == null) { //buffer the sorted list
//        if (filteredSortedList == null && ((filterSortDef = getFilterSortDef()) != null)) { //buffer the sorted list
////            filteredSortedList = (List<? extends ItemAndListCommonInterface>) filterSortDef.filterAndSortItemList(list);
//            filteredSortedList = (List<E>) filterSortDef.filterAndSortItemList(list);
//        }
//        if (filteredSortedList != null) { //reuse
//            list = filteredSortedList;
//        }
//</editor-fold>
        if ((filterSortDef = getFilterSortDef()) != null) { //no buffer for (see code above for buffer version)
            return (List<E>) filterSortDef.filterAndSortItemList(list);
        } else
            return list;
    }

    /**
    returns the full (manually sorted) list, with no sorting or filtering
    @return never null
     */
    public List<E> getListFull() {
//    public List<? extends ItemAndListCommonInterface> getListFull() {
//            if (false && !isDataAvailable()) {
//                DAO.getInstance().fetchIfNeededReturnCachedIfAvail(this); //WON'T WORK since new impl of fetchIfNeeded(this) may return a previously existing instance instead of simply fetching the data for 'this'
//            }
//        if (false&&cachedList != null) {
//            return cachedList;
//        }
        List<E> cachedList = getList(PARSE_ITEMLIST);

//            List<E> list = getList(PARSE_ITEMLIST);
//            if (list != null) {
        if (cachedList != null) {
//                DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(cachedList); //optimization?? heavy operation, any way (OTHER than caching which does work
            if (Config.CHECK_OWNERS) checkOwners(cachedList);
//        {
//            for (E elt : cachedList) {
//                Item item = (Item)elt;
//                ASSERT.that(item.getOwner() == this, () -> ("ERROR in owner: Item="
//                        //<editor-fold defaultstate="collapsed" desc="comment">
//                        //                        + ((Item) item).toString(false)
//                        //                        + ((Item) ((Item) item).getOwner() instanceof Item
//                        //                        ? (", item.owner=" + ((Item) ((Item) item).getOwner()).toString(false))
//                        //                        : (", item.owner=" + ((ItemList) ((Item) item).getOwner()).toString(false)))
//                        //                        + ", should be=" + this.toString(false)));
//                        //</editor-fold>
//                        + (item.toString(false)
//                        + (item.getOwner() instanceof ItemList
//                        ? (", item.owner=" + ((ItemList) item.getOwner()).toString(false))
//                        : (", item.owner=" + ((Item) item.getOwner()).toString(false)))
//                        + ", should be=" + this.toString(false))));
//            }
//        }
//            return list;
        } else {
//            return null; //returning null would mean every user must check for null and create a list. And returning a new empty ArrayList and saving it doesn't have any side-effect since a new empty list isn't actually saved
//                return new ArrayList();
            cachedList = new ArrayList();
        }
        return cachedList;
    }

//    @Override
    private boolean addToList(int index, ItemAndListCommonInterface subItemOrList) {
        addItemAtIndex((E) subItemOrList, index);
        if (Config.TEST) {
            ASSERT.that(subItemOrList.getOwner() == null || subItemOrList.getOwner() == this || subItemOrList.getOwner().equals(this),
                    () -> "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this); //subItemOrList.getOwner()==this may happen when creating repeatInstances
        }//        ASSERT.that( subItemOrList.getOwner() == null , "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this);
        subItemOrList.setOwner(this);
//        DAO.getInstance().save((ParseObject)subtask);
        return true;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface newElement, ItemAndListCommonInterface refElement, boolean addAfterRefEltOrEndOfList) {
//        int index = indexOf(refElement);
        List listFull = getListFull();
        int index = refElement == null ? (addAfterRefEltOrEndOfList ? listFull.size() : 0) : (listFull.indexOf(refElement) + (addAfterRefEltOrEndOfList ? 1 : 0));
//        if (index < 0)
//            addToList(newElement);
//        else
//            addToList(index + (addAfterRefEltOrEndOfList ? 1 : 0), newElement);
        addToList(index, newElement);
//        addItemAtIndex((E) subItemOrList, index + (addAfterItem ? 1 : 0));
//        if (Config.TEST) {
//            ASSERT.that(subItemOrList.getOwner() == null || subItemOrList.getOwner() == this || subItemOrList.getOwner().equals(this), 
//                    () -> "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this); //subItemOrList.getOwner()==this may happen when creating repeatInstances
//        }//        ASSERT.that( subItemOrList.getOwner() == null , "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this);
//        subItemOrList.setOwner(this);
//        DAO.getInstance().save((ParseObject)subtask);
        return true;
    }

    @Override
    public boolean addToList(ItemAndListCommonInterface subItemOrList, boolean addToEndOfList) {
//        addToList( subItemOrList,MyPrefs.getBoolean(MyPrefs.insertNewItemsInStartOfLists));
        addItemAtIndex((E) subItemOrList, addToEndOfList ? getSize() : 0);
        return true;
    }

    public boolean addToList(ItemAndListCommonInterface subItemOrList) {
        addToList(subItemOrList, MyPrefs.insertNewItemsInStartOfLists.getBoolean());
        return true;
    }

    @Override
    public boolean removeFromList(ItemAndListCommonInterface subItemOrList, boolean removeReferences) {
//        List subtasks = getList();
//        boolean status = subtasks.remove(subtask);
//        setList(subtasks);
//        return status;
        removeItem(subItemOrList); //TODO: update removeItem to return boolean
//        assert subItemOrList.getOwner() == this : "list not owner of removed subtask, subItemOrList=" + subItemOrList + ", owner=" + getOwner() + ", list=" + this;
        ASSERT.that(!(this instanceof ItemList) || subItemOrList.getOwner() == this, () -> "list not owner of removed subItemOrList (" + subItemOrList + "), owner=" + getOwner() + ", list=" + this); //
        if (removeReferences)
            subItemOrList.setOwner(null);
        return true;
    }

    private Bag cacheBag;

    public Bag<E> getItemBag() {
        if (cacheBag != null)
            return cacheBag;

        List<E> list = getList(PARSE_ITEM_BAG);
        if (list != null) {
            cacheBag = new Bag(list);;
            return cacheBag;
        } else {
            return null; //new Bag();
        }
    }

    public void setItemBag(Bag<E> itemBag) {
        if (itemBag != null && !itemBag.isEmpty()) {
            put(PARSE_ITEM_BAG, itemBag.toList()); //optimization: 'serializing' the bag on each put (and get above) is costly for large sets, cache a copy?!
            cacheBag = itemBag;
        } else {
            remove(PARSE_ITEM_BAG);
            cacheBag = null;
        }
    }

    public List<ItemList<E>> getMetaList() {
        List<ItemList<E>> list = getList(PARSE_META_LISTS);
        if (list != null) {
            return list;
        } else {
            return new ArrayList();
        }
    }

    public void setMetaList(List<ItemList<E>> metaList) {
        if (metaList != null && !metaList.isEmpty()) {
            put(PARSE_META_LISTS, metaList);
        } else {
            remove(PARSE_META_LISTS);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean isStoreOnlySingleInstanceOfItems() {
//        return storeOnlySingleInstanceOfItems;
//    }
//
//    public void setStoreOnlySingleInstanceOfItems(boolean storeOnlySingleInstanceOfItems) {
//        this.storeOnlySingleInstanceOfItems = storeOnlySingleInstanceOfItems;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean isAutoAddRemoveListAsListenerOnInsertedItems() {
//        return autoAddRemoveListAsListenerOnInsertedItems;
//    }
//    public void setAutoAddRemoveListAsListenerOnInsertedItems(boolean autoAddRemoveListAsListenerOnInsertedItems) {
//        this.autoAddRemoveListAsListenerOnInsertedItems = autoAddRemoveListAsListenerOnInsertedItems;
//    }
//    public boolean isWriteListItemsGuidOnly() {
//        return writeListItemsGuidOnly;
//    }
//
//    public void setWriteListItemsGuidOnly(boolean writeListItemsGuidOnly) {
//        this.writeListItemsGuidOnly = writeListItemsGuidOnly;
//    }
//</editor-fold>
    /**
     * returns the Vector with the content of the list
     *
     * @return
     */
//    public Vector getVector() {
////        return new Vector(itemList);
//        return new Vector(getList());
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * set the baseTypes as accepted types of this list. Previously defined
//     * baseTypes are removed.
//     *
//     * @param baseType
//     */
//    final private void setBaseTypesImpl(int[] baseTypes) {
//        if (baseTypes != null) {
//            this.baseTypes = new Vector(baseTypes.length, 1); //reset baseTypes
//            for (int i = 0, size = baseTypes.length; i < size; i++) {
//                this.baseTypes.addElement(new Integer(baseTypes[i]));
//            }
//        }
//    }
//
//    public void setBaseTypes(int[] baseTypes) {
//        if (baseTypes != null) {
//            setBaseTypesImpl(baseTypes);
////            changed(ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE);
//        }
//    }
//
//    /**
//     * adds the already defined list of baseTypes as accepted types of this
//     * list.
//     *
//     * @param baseType
//     */
//    public void addBaseTypes(int[] baseTypes) {
//        if (baseTypes != null) {
//            if (this.baseTypes != null) {
//                this.baseTypes = new Vector(baseTypes.length, 1);
//            }
//            for (int i = 0, size = baseTypes.length; i < size; i++) {
//                this.baseTypes.addElement(new Integer(baseTypes[i]));
//            }
////            changed(ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE);
//        }
//    }
//
////<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * add the baseType (as defined in BaseItemTypes) as an accepted type of
//     * this list. BaseItemTypes.NULL means
//     *
//     * @param baseType
//     */
////    public void addBaseType(int baseType) {
////        if (baseType != BaseItemTypes.NOTYPE) {
////            if (baseTypes == null) {
////                baseTypes = new Vector(1, 1);
////            }
//////            ASSERT.that(baseTypes.size() == 0, "Multiple BaseTypes currently not supported for ItemList, existing type="
//////                    +baseTypes!=null&&baseTypes.size()>0?BaseItemTypes.toString(((Integer)baseTypes.elementAt(0)).intValue()):"<null>"
//////                    +" new type="+BaseItemTypes.toString(baseType));
////            this.baseTypes.addElement(new Integer(baseType));
////        }
////    }
////</editor-fold>
//    /**
//     * returns the baseType with index index (in case there are several
//     * baseTypes defined (not implemented yet)
//     */
//    private int getBaseType(int index) {
//        return ((Integer) baseTypes.elementAt(index)).intValue();
//    }
//
//    /**
//     * check if baseType is in this ItemList's baseTypes. Always returns true if
//     * ItemList has no baseTypes defined.
//     *
//     * @param baseType
//     * @return
//     */
//    public boolean isBaseType(int baseType) {
//        if (isTyped()) { //baseTypes != null && baseTypes.size() != 0) {
//            for (int i = 0, size = baseTypes.size(); i < size; i++) {
//                if (getBaseType(i) == baseType) {
//                    return true;
//                }
//            }
//            return false;
////        return baseTypes.contains(new Integer(baseType)); //-doesn't work since each Integer is a new object (??)
//        }
//        return true;
//    }
//
//    /**
//     * returns true if any baseTypes are defined for this list
//     *
//     * @return
//     */
//    public boolean isTyped() {
//        return (baseTypes != null && baseTypes.size() > 0);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns the BaseItemType that the list contains. Allows e.g.
     * ItemListCombined to set the appropriate commands, and commands New and
     * NewSubTask to create the right type of element to insert
     */
//    public int getBaseType() {
//        if (isTyped()) {
//            if (this == Categories.getInstance()) {
////                return BaseItemTypes.CATEGORIES;
//                return BaseItemTypes.CATEGORY;
//            } else if (baseTypes.size() == 1) {
//                //if there's only one baseType in the list then return that:
//                return getBaseType(0);
//            } else if (baseTypes.size() == 0) {//TODO!!!: What to do when there are no baseTypes defined?
//                return BaseItemTypes.NOTYPE;
//            } else if (baseTypes.size() > 1) { //TODO!!: return the least permissive type??!!
//                try {
//                    throw new Exception("Multiple BaseTypes currently not supported for ItemList");
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//        return BaseItemTypes.NOTYPE;
//    }
//</editor-fold>
    /**
     * @inherit
     */
    @Override
    public ItemList cloneMe() {
        ItemList newCopy = new ItemList();
//        copyMeInto(newCopy);
//        super.copyMeInto(newCopy);
        // optimization: select the most efficient way of copying vectors (always using the head of lists?)
//        for (int i = 0, size = getSize(); i < size; i++) {
////            itemList.addItem(((BaseItem) getItemAt(i)).clone());
//            newCopy.addItem(getItemAt(i).cloneMe());
//        }
        copyMeInto(newCopy);
        return newCopy;
    }

    /**
     * @inherit
     */
    /**
     * *copies* the elements of this list into destiny. It does NOT create new
     * copies of the elements (clone() does that).
     *
     * @param destiny
     */
    @Override
    public void copyMeInto(ItemAndListCommonInterface destiny) {
        //public void copyMeInto(ItemList destiny) {
        ItemList destinyItemList = (ItemList) destiny;
        ASSERT.that(false, "not sure why ItemList should be cloned?? List=" + getText());
//        super.copyMeInto(destinyItemList);
        // optimization: select the most efficient way of copying vectors (always using the head of lists?)
//        System.arraycopy(...); //TODO!!!: use arraycopy for faster copy of vector
        for (int i = 0, size = getSize(); i < size; i++) {
//            itemList.addItem(((BaseItem) getItemAt(i)).clone());
//            itemList.addItem(((BaseItem) getItemAt(i)));
//            destinyItemList.addItem((getItemAt(i))); //- no need to case to BaseItem, since we only copy references to the contained objects
            destinyItemList.addItem((getItemAt(i).cloneMe())); //- no need to case to BaseItem, since we only copy references to the contained objects
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        destinyItemList.setText(getText()); //-done in super.copyMeInto(destinyItemList) above
//        itemList.baseTypes=baseTypes;
//        destinyItemList.addBaseType(getBaseType());
//        destinyItemList.addBaseType(getBaseType(0));
//        if (baseTypes != null) {
//            destinyItemList.baseTypes = new Vector(baseTypes.size());
//            for (int i = 0, size = baseTypes.size(); i < size; i++) {
//                destinyItemList.baseTypes.addElement(baseTypes.elementAt(i));
//            }
//        }
//        destinyItemList.baseTypes = baseTypes.(getBaseType(0));
//        destinyItemList.storeOnlySingleInstanceOfItems = storeOnlySingleInstanceOfItems;
//        destinyItemList.autoAddRemoveListAsListenerOnInsertedItems = autoAddRemoveListAsListenerOnInsertedItems;
//        destinyItemList.sumFieldGetter = sumFieldGetter;
//        destinyItemList.writeListItemsGuidOnly = writeListItemsGuidOnly;
//</editor-fold>
    }

//    final static int TOSTRING_COMMA_SEPARATED_LIST =1;
//    final static int TOSTRING_DEFAULT =2;
    @Override
    public String toString(ToStringFormat format) {
        String str = "";
        String sepStr = "";
        switch (format) {
            case TOSTRING_COMMA_SEPARATED_LIST:
//                for (int i = 0; i < getSize(); i++) {
//                    E baseItem = getItemAt(i);
//                    if (baseItem != null) { //necessary when printing lists where change events are removing them (gives null pointerexception
                for (ItemAndListCommonInterface elt : getListFull()) {
//                    E baseItem = getItemAt(i);
//                    if (baseItem != null) { //necessary when printing lists where change events are removing them (gives null pointerexception
//                    str += sepStr + ((BaseItem) getItemAt(i)).shortString();
//                        str += sepStr + getItemAt(i).toString(ToStringFormat.TOSTRING_DEFAULT);
//                        str += sepStr + getItemAt(i).toString();
                    str += sepStr + elt.toString();
                    sepStr = " | ";
//                    }
                }
                return str;
            case TOSTRING_DEFAULT:
            default:
                return ""; //super.toString(format);
        }
    }

    @Override
    public String toString() {
        return toString(true);
    }

    public String toString(boolean showSubtasks) {
//        return toString(ToStringFormat.TOSTRING_COMMA_SEPARATED_LIST);
//        return getText().length() != 0 ? getText() : getObjectIdP();
//        return getText() + " [" + getObjectIdP() + "]" + (isNoSave() ? " NoSave!" : "") + showSubtasks ? ((getListFull().size() > 0 ? (" " + getListFull().size() + " items") : "")  : "");
        return getText() + " [" + getObjectIdP() + "]" + (isNoSave() ? " NoSave!" : "")
                + (showSubtasks ? (getListFull().size() > 0 ? (" " + getListFull().size() + " items") : "") : "");
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public String shortString() {
////        String str = super.shortString();
//        String str = "";
//        str += "{" + getItemIdStr() + "}";
//        str += listItemsToString(true);
//        return str;
//    }
    /**
     * set owner without calling changed() and sending change events
     *
     * @param owner
     */
//    final public void setOwnerDirectly(E owner) {
////        if (this.owner != owner) {
//        ASSERT.that(owner == null || this.owner == null || this.owner == owner
//                || (this instanceof Category && owner == Categories.getInstance())
//                || (this!=null&&owner!=null&&this.getGuid()==owner.getGuid()), //Categories are assigned to a new instance when re-reading
//                "trying to overwrite owner #oldOwner=" + this.owner + " #newOwner=" + owner + " #item=" + this);
//        this.owner = owner;
////        changed();
////        }
//    }
//    public ItemAndListCommonInterface getOwner() {
//        return owner;
//    }
//
//    /**
//     * sets owner
//     *
//     * @param owner
//     */
//    public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner) {
//        ItemAndListCommonInterface previousOwner = this.owner;
//        this.owner = owner;
//        return previousOwner;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * Owner of an item can be either a list or a project (an item in a task's
     * sublist).
     *
     * @return null if no owner
     */
//    final public ItemAndListCommonInterface getOwner() {
//    @Override
//    ParseObject getOwnerListXXX() {
////        return owner; //TODO: Parsify
////        ParseObject owner = getParseObject(PARSE_OWNER);
////        ItemListList ownerList = (ItemListList) getParseObject(PARSE_OWNER);
//        ParseObject ownerList = getParseObject(PARSE_OWNER);
////        ownerList = (ItemListList) DAO.getInstance().fetchIfNeeded(ownerList);
//        ownerList = DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
//
////        return (ItemList) getParseObject(PARSE_OWNER);
//        return ownerList;
////        return (status == null) ? ItemStatus.STATUS_CREATED : ItemStatus.valueOf(status); //Created is initial value
//    }
//    @Override
//    private void setOwnerListXXX(ParseObject owner) {
////        setOwnerDirectly(owner);
////        ItemAndListCommonInterface oldOwner = getOwner();
////        this.owner = owner; //TODO: Parsify
////        return oldOwner;
////        }
//        if (owner != null) {
//            put(PARSE_OWNER, owner);
//        } else {
//            remove(PARSE_OWNER);
//        }
////        return owner;
//    }
//</editor-fold>
    @Override
    public void setOwner(ItemAndListCommonInterface owner) {
//<editor-fold defaultstate="collapsed" desc="comment">
////        if (sourceItem != null) {
////            sourceItem.setOwner(owner);
////        } else
//        if (owner instanceof ItemList) {
//            setOwnerList((ItemList) owner);
////        } else if (owner instanceof Item) {
////            setOwnerItem((Item) owner);
////        } else if (owner instanceof Category) {
////            setOwnerCategory((Category) owner);
//        } else if (owner == null) {
//            setOwnerList(null);
//        } else {
//            assert false : "unknown owner type for " + owner;
//        }
//</editor-fold>
        if (owner != null) {
            put(PARSE_OWNER, owner);
        } else {
            remove(PARSE_OWNER);
        }

    }

    @Override
    public ItemAndListCommonInterface getOwner() {
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemAndListCommonInterface owner;
////        if (sourceItem != null) {
////            return sourceItem.getOwner();
////        } else
//        if ((owner = getOwnerList()) != null) {
//            return owner;
////        } else if ((owner = getOwnerItem()) != null) {
////            return owner;
////        } else if ((owner = getOwnerCategory()) != null) {
////            return owner;
//        } else {
//            return null;
//        }
//</editor-fold>
        ParseObject ownerList = getParseObject(PARSE_OWNER);
        if (ownerList != null) {
//            ownerList = DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
            ownerList = DAO.getInstance().fetchIfNeededReturnCachedIfAvail(ownerList);
            return (ItemAndListCommonInterface) ownerList;
        } else {
            return null;
        }

    }

//    @Override
//    public ItemList getOwnerList() {
//        return null; //TODO return useful value?
//    }
    /**
     * set, save and apply filter (and resets the filtered/sorted list)
     *
     * @param filterSortDef
     */
    @Override
    public void setFilterSortDef(FilterSortDef filterSortDef) {
//        if (filterSortDef != null && filterSortDef != FilterSortDef.getDefaultFilter()) { //only save if not the default filter
        if (filterSortDef != null && !filterSortDef.equals(FilterSortDef.getDefaultFilter())) { //only save if changed compared to the default filter
            if (!isNoSave()) { //otherwise temporary filters for e.g. Overdue will be saved
                DAO.getInstance().saveInBackground(filterSortDef); //
            }
            put(PARSE_FILTER_SORT_DEF, filterSortDef);
        } else {
            remove(PARSE_FILTER_SORT_DEF);
        }
//        filteredSortedList = null;
    }

    @Override
    public FilterSortDef getFilterSortDef() {
        FilterSortDef filterSortDef = (FilterSortDef) getParseObject(PARSE_FILTER_SORT_DEF);
        filterSortDef = (FilterSortDef) DAO.getInstance().fetchIfNeededReturnCachedIfAvail(filterSortDef);
        if (filterSortDef == null && MyPrefs.useDefaultFilterInItemListsWhenNoneDefined.getBoolean())
            return FilterSortDef.getDefaultFilter();
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (filterSortDef != null) {
//            return filterSortDef;
//        } else {
//            return null;
//        }
//</editor-fold>
        return filterSortDef;
    }
    //<editor-fold defaultstate="collapsed" desc="comment">

    /**
     * returns a string that identifies the item. chosen in the following order:
     * getText() owner.getText() getLogicalName()
     *
     * @return
     */
    //    public String getItemIdStr() {
    //        String str = "";
    //        if (getOwner() != null && getOwner() instanceof Item) {
    //            if (((Item) getOwner()).getItemListSize() != 0 && ((Item) getOwner()).getItemList() == this) {
    //                return "sublist:" + getOwner().getItemIdStr();
    //            } else if (((Item) getOwner()).getCategoriesSize() != 0 && ((Item) getOwner()).getCategories() == this) {
    //                return "cats:" + getOwner().getItemIdStr();
    //            }
    //        }
    ////        return super.getItemIdStr();
    //        return "";
    //    }
    //#enddebug
    //    private String listItemsToString(boolean shortVersion) {
    //        String str = "";
    //        str += "[";
    //        String sepStr = "";
    ////        str += "(size:" + getSize() + ")=";
    //        if (itemList == null || getSize() == 0) {
    //            str += "<empty>";
    //        } else {
    //            for (int i = 0; i < getSize(); i++) {
    ////                if (getItemAt(i) instanceof E) {
    //                E baseItem = getItemAt(i);
    //                if (baseItem != null) { //necessary when printing lists where change events are removing them (gives null pointerexception
    //                    if (shortVersion) {
    //                        str += sepStr + ((E) getItemAt(i)).shortString();
    //                    } else {
    //                        str += sepStr + ((E) getItemAt(i)).shortString();
    //                    }
    ////                    if (isSelected(getItemAt(i))) {
    ////                        str += "[S]";
    ////                    }
    //                    sepStr = "|";
    //                }
    ////                } else {
    ////                    str += getItemAt(i);
    ////                }
    //            }
    //        }
    //        str += "]";
    //        return str;
    //    }
    //
    //    public String fullString() {
    ////        String str = super.fullString();
    //        String str = "";
    //        str += listItemsToString(false);
    //        return str;
    //    }
    //    Vector createVector(E[] items) {
    //        if (items == null) {
    //            items = new E[]{""};
    //        }
    //        Vector vec = new Vector(items.length);
    //        for (int iter = 0; iter < items.length; iter++) {
    //            vec.addElement(items[iter]);
    //        }
    //        return vec;
    //    }
    //</editor-fold>
    /**
     * By default an ItemList does not delete the items in its list since they
     * are only references. Only the when an ItemList is used to store 'owned'
     * items, like the sub-tasks of a task, should the sub-items be deleted.
     *
     */
    public boolean softDelete(boolean removeRefs) {

        //if a timer was active for this itemList, then remove that (and update any timed item even though it may get soft-deleted below)
        TimerStack.getInstance().updateTimerWhenItemListIsDeleted(this);

        //since we're deleting the list, and thus soft-deleting all its tasks (and their subtasks, recursively), we don't need to remove the list as the tasks' owner!
        List<? extends ItemAndListCommonInterface> tasks = getListFull();
        for (Item item : (List<Item>) getListFull()) {
            item.softDelete(removeRefs);
        }

        //remove itemList from meta-itemLists (all itemLists to which it is a sub-itemList)
        //remove this ItemList from all lists (ItemLists or Categories) which include it as a sourceList
        List<ItemList<E>> metaList = (List<ItemList<E>>) getMetaList();
        if (metaList != null && metaList.size() > 0) {
            List<ParseObject> updatedMetaLists = new ArrayList<>();
            updatedMetaLists.clear();
            for (ItemList itemList : metaList) {
                if (itemList.removeSubList(this)) {
                    updatedMetaLists.add(itemList);
                }
            }
            DAO.getInstance().saveInBackground(updatedMetaLists);
        }

        ItemListList itemListList = ItemListList.getInstance();
        itemListList.remove(this);
        DAO.getInstance().saveInBackground((ParseObject) itemListList);

        FilterSortDef filter = getFilterSortDef();
        if (filter != null)
            filter.softDelete(removeRefs);

        put(Item.PARSE_DELETED_DATE, new Date());
        DAO.getInstance().saveInBackground((ParseObject) this);
        return true;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * deletes() all the items in the list, even if they're not 'owned' by the
     * list. Does not delete the list itself
     */
//    public void deleteAllItemsInList() {
//        deleteAllItemsInList(false);
//    }
//
//    public void deleteAllItemsInList(boolean deleteOnlyItemsOwnedByList) {
//        E item;
//        while (getSize() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
//            item = getItemAt(0);
//            if (!deleteOnlyItemsOwnedByList
//                    || item.getOwner() == this) {
//                removeItem(0); //remove item first to avoid call back to this list
//                item.delete(); //delete the item and remove from any lists
//            }
//        }
//    }
//
//    /**
//     * By default deleting an ItemList only deletes the owned items. This call
//     * forces a deletion of all items in the list. Used e.g. to delete all
//     * repeat instances, no matter what list owns them.
//     */
//    public void deleteItemListAndAllItsItems() {
////        super.delete(); //first inform all listeners on this item that it's being deleted (so they stop listening to the events from deleting the list's items)
////        uncommit(); //avoid saving list after each deletion //-uncommit is done by delete()
//        deleteAllItemsInList();
////        itemList = null; //help garbage collector
//        workTimeDefinition = null; //help garbage collector
//        dataListener = null; //help GC
//        selectionListener = null; //help GC
//    }
//</editor-fold>
    /**
     * @inheritDoc
     */
//    public int getSize() {
////        return (itemList == null) ? 0 : itemList.size();
////return (getListFull() == null) ? 0 : getListFull().size();
////        List l = getListFull();
////        return (l == null) ? 0 : l.size();
////        return getList().size();
//        return getListFull().size();
//    }
    /**
     * @inheritDoc
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * returns true if the current selection is valid (that is: list is
     * non-empty, and selected index is between 0 and list size).
     *
     * @return
     */
    public boolean isSelectionValid() {
        return getSelectedIndex() >= 0 && getSize() > 0 && getSelectedIndex() < getSize();
    }

    /**
     * returns currently selected item. If list is empty, or no valid selection
     * is set returns null
     *
     * @return
     */
    public E getSelectedItem() {
        return isSelectionValid() ? getItemAt(getSelectedIndex()) : null;
//        return getItemAt(getSelectedIndex());
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected void xenforceConsistencyBetweenItemsAndTheirCategories() {
//        super.enforceConsistencyBetweenItemsAndTheirCategories(); //currently does nothing - just called in case
//        for (int i = 0, size = getSize(); i < size; i++) {
////            ((Item) getItemAt(i)).setEnsureItemCategoryAutoConsistency(true);
////            ((BaseItem) getItemAt(i)).setEnsureItemCategoryAutoConsistency(true);
//            ((BaseItemOrList) getItemAt(i)).enforceConsistencyBetweenItemsAndTheirCategories(); //could be both Items and ItemLists (in case of lists of lists
//        }
//    }
//</editor-fold>
    /**
     * Adding an item to list at given index. OK to add to a position *after*
     * the last element (at position getSize()). items will only be added if not already in list!
     *
     * @param item - the item to add
     * @param index - the index position in the list
     */
    public void addItemAtIndex(E item, int index) {

        List listFull = getListFull();
//        List list = getList();

        int indexFull;
//        if (list != listFull) { //optimize for the case where list is not filtered
//            if (index >= 0 && index < list.size())
//                indexFull = listFull.indexOf(list.get(index));
//            else indexFull = 0;
//        } else
        indexFull = index;

        Bag updatedBag = getItemBag(); //TODO: 
//        if (hasSubLists() && updatedBag != null && updatedBag.getCount(item) > 0) { //if there are sublists and item has already been added at least once (so appears in list)
        if (hasSubLists() && updatedBag != null && indexFull != -1) { //if there are sublists and item has already been added at least once (so appears in list)
//            itemBag.add(item); //then don't add to list, but just add to bag to keep track of how many times added
            updatedBag.add(item); //item already in list, so add to bag to keep count
            setItemBag(updatedBag); //then don't add to list, but just add to bag to keep track of how many times added
            //TODO!!! should the next statement be an 'else'?? 
        } else {

//            if (!getListFull().contains(item)) {
            if (!listFull.contains(item)) {//UI: only allow one copy of each item
//<editor-fold defaultstate="collapsed" desc="comment">
//else add normally
//only add items if either storeOnlySingleInstanceOfItems OR if the item is not already in the list
//            if (!storeOnlySingleInstanceOfItems || getItemIndex(item) == -1) {
//            assert getItemIndex(item) == -1 : "should never add same item twice to a list (" + item + " already in list [" + this + "] at pos=" + getItemIndex(item); //if (getItemIndex(item) == -1) {
//</editor-fold>
                assert listFull.indexOf(item) == -1 : "should never add same item twice to a list (" + item + " already in list [" + this + "] at pos=" + getItemIndex(item); //if (getItemIndex(item) == -1) {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (index <= getSize()) { // shouldn't make this check since it might make us miss some errors
//                itemList.insertElementAt(item, index);
//                itemList.add(index, item);
//            List<E> editedList = getListFull();
//            List listCopy = new ArrayList(editedList);
//            editedList.add(index, item);
//                List listCopy = new ArrayList(list);
//                listCopy.add(index, item);
//            list.add(index, item);
//</editor-fold>
//                listFull.add(index, item);
                listFull.add(indexFull, item);
//            assert list.indexOf(item) != -1 : "item NOT in list thouygh just added (" + item + " already in list [" + this + "] at pos=" +list.indexOf(item); //if (getItemIndex(item) == -1) {
                if (Config.TEST) ASSERT.that(listFull.indexOf(item) != -1, () -> "1.item NOT in list though just added (item=" + item + ", list=[" + this + "], pos=" + listFull.indexOf(item)); //if (getItemIndex(item) == -1) {
//            setList(editedList);
                setList(listFull);
                if (Config.TEST) ASSERT.that(listFull.indexOf(item) != -1, () -> "2.item NOT in list though just added (item=" + item + ", list=[" + this + "], pos=" + listFull.indexOf(item)); //if (getItemIndex(item) == -1) {
//                if (selectedIndex >= index && selectedIndex < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
//                    selectedIndex++;
//                }
                int selIdx = getSelectedIndex();
                if (selIdx >= index && selIdx < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
                    setSelectedIndex(selIdx + 1);
                }
                fireDataChangedEvent(DataChangedListener.ADDED, index);
//            }
            }
        }
    }

//    public void moveToPositionOf(E item, E itemRef, boolean insertAfter) {
//        //NB. Since just reshuffling the list, no impact on any bags
//        List listFull = getListFull();
//
//        int newPos = listFull.indexOf(itemRef)+ (insertAfter ? 1 : 0);
//        int oldPos = listFull.indexOf(item) ;
//        
//        listFull.add(newPos, item); //insert *before* remove so that removing the item doesn't impact the insert index
//        listFull.remove(item);
//        
//        setList(listFull);
//        fireDataChangedEvent(DataChangedListener.CHANGED, newPos);
//    }
    public ItemAndListCommonInterface setItemAtIndex(E item, int index) {
//        List<? extends ItemAndListCommonInterface> editedList = getListFull();
        List<E> listFull = getListFull();
        List<E> list = getList();
        ItemAndListCommonInterface oldElement = list.get(index);
        Bag bag = getItemBag();
//        if (hasSubLists() && bag != null && bag.getCount(item) > 0) { //if there are sublists and item has already been added at least once (so appears in list)
        if (hasSubLists() && bag != null) { //if there are sublists and item has already been added at least once (so appears in list)
            bag.remove(oldElement); //no need to test if oldElt is already in list, in either case remove will give right result( item added several timet: count-=1, only once added to list: count=0 (0-1)
//            itemBag.add(item); //then don't add to list, but just add to bag to keep track of how many times added
            if (bag.getCount(item) > 0)
                bag.add(item); //only add to bag if *already* in the list only use bag when an element is added more than once)!
            setItemBag(bag); //then don't add to list, but just add to bag to keep track of how many times added
        } else {//not previously in bag, setif (!getListFull().contains(item)) {
//<editor-fold defaultstate="collapsed" desc="comment">
//else add normally
//only add items if either storeOnlySingleInstanceOfItems OR if the item is not already in the list
//            if (!storeOnlySingleInstanceOfItems || getItemIndex(item) == -1) {
//            assert getItemIndex(item) == -1 : "should never add same item twice to a list (" + item + " already in list [" + this + "] at pos=" + getItemIndex(item); //if (getItemIndex(item) == -1) {
//            if (index <= getSize()) { // shouldn't make this check since it might make us miss some errors
//                itemList.insertElementAt(item, index);
//                itemList.add(index, item);
//</editor-fold>
            listFull.set(index, item);
            setList(listFull);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (selectedIndex >= index && selectedIndex < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
//                    selectedIndex++;
//                }
//            int selIdx = getSelectedIndex();
//            if (selIdx >= index && selIdx < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
//                setSelectedIndex(selIdx + 1);
//            }
//            int selIdx = index;
//</editor-fold>
            fireDataChangedEvent(DataChangedListener.CHANGED, index);
//            }
        }
        return oldElement;
    }

    public boolean setToList(int index, ItemAndListCommonInterface subItemOrList) {
        setItemAtIndex((E) subItemOrList, index);
        ASSERT.that(subItemOrList.getOwner() == null || subItemOrList.getOwner() == this
                || this.equals(subItemOrList.getOwner()), () -> "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this); //subItemOrList.getOwner()==this may happen when creating repeatInstances
//        ASSERT.that( subItemOrList.getOwner() == null , "subItemOrList owner not null when adding to list, subtask=" + subItemOrList + ", owner=" + subItemOrList.getOwner() + ", list=" + this);
        subItemOrList.setOwner(this);
//        DAO.getInstance().save((ParseObject)subtask);
        return true;
    }

    /**
     * inserts item at special position. Used to insert at head/tail of list, or
     * before/after a referenceItem when adding new items to list. If called
     * with INSERT_BEFORE_REFERENCE_ITEM, INSERT_AFTER_REFERENCE_ITEM and
     * currentItem==null, then inserts at
     *
     * @param item item to be inserted at a special position
     * @param referencePositionItem referenceItem if using
     * INSERT_BEFORE_REFERENCE_ITEM, INSERT_AFTER_REFERENCE_ITEM, otherwise null
     * @param position ItemList.INSERT_AT_HEAD_OF_LIST, INSERT_AT_END_OF_LIST,
     * INSERT_BEFORE_REFERENCE_ITEM, INSERT_AFTER_REFERENCE_ITEM
     */
    public void addItemAtSpecialPosition(E item, E referencePositionItem, int position) {
//        int index;
//#mdebug
        ASSERT.that((position != Settings.INSERT_BEFORE_REFERENCE_ITEM && position != Settings.INSERT_AFTER_REFERENCE_ITEM) || referencePositionItem != null,
                () -> "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referencePositionItem==null, list=" + this);
        ASSERT.that((position != Settings.INSERT_BEFORE_REFERENCE_ITEM && position != Settings.INSERT_AFTER_REFERENCE_ITEM) || getItemIndex(referencePositionItem) != -1,
                () -> "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referencePositionItem=" + referencePositionItem + " not in list=" + this);
//        ASSERT.that((position != INSERT_BEFORE_REFERENCE_ITEM && position != INSERT_AFTER_REFERENCE_ITEM) || (referenceIndex>=0 && referenceIndex<getSize()), "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referenceIndex="+referenceIndex+" outside bounds of list="+this);
//#enddebug
        if (position == Settings.INSERT_AT_HEAD_OF_LIST) {
            addItemAtIndex(item, 0); //UI: insert new repeatInstance at the end of the list
        } else if (position == Settings.INSERT_AT_END_OF_LIST) {
            addItem(item); //UI: insert new repeatInstance at the end of the list
        } //add new created repeatInstance just before the just finished one
        else //if (referencePositionItem!=null && (index=getItemIndex(referencePositionItem))!=-1)
        {
            if (position == Settings.INSERT_BEFORE_REFERENCE_ITEM) {
                addItemAtIndex(item, getItemIndex(referencePositionItem)); //UI: insert new repeatInstance at position of previous
            } else if (position == Settings.INSERT_AFTER_REFERENCE_ITEM) {
                addItemAtIndex(item, getItemIndex(referencePositionItem) + 1); //UI: insert new repeatInstance at position of previous
            }
        }
    }

    public void addItem(E item) {
        addItemAtIndex(item, getListFull().size());
        //only add items if either store multiple instances (!storeOnlySingleInstanceOfItems) OR if the item is not already in the list
//        if (!storeOnlySingleInstanceOfItems || getItemIndex(item) == -1) {
////            derivedSumVector.invalidate(getSize()); //not needed to call invalidate here since there will never be a sum value defined for an item added to the end of the itemVector
//            itemVector.addElement(item);
//            if (item instanceof BaseBaseItem) { //TODO!!!! move the changeeventlisteners to BaseBaseItem
//                processJustInsertedBaseBaseItem((BaseBaseItem) item, getSize() - 1);
//
//            }
//            fireDataChangedEvent(DataChangedListener.ADDED, getSize() - 1); //TODO: verify that -1 is appropriate since it is not used in DefaultListModel implementation
//            changed(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, getSize(), item, triggerEvent));
//        }
    }

    /**
     * add all the items in itemList (no check on duplicates, use addItems() for
     * that)
     *
     * @param itemList list of items to add (or null)
     */
    public void addAllItems(ItemList<E> itemList) {
//        if (itemList == null) {
//            return;
//        }
        if (itemList != null)
            for (int i = 0, size = itemList.getSize(); i < size; i++) {
                addItem(itemList.getItemAt(i));
            }
    }

    /**
     * add the items (which are not already in this list) in itemList to the end
     * of this list
     *
     * @param itemList
     * @param addEvenIfAlreadyInList add items even if they're already in the
     * list (cost: check for each item if it is in the list)
     * @param addToStartOfList add to head of list, if false add to end of list
     */
    public void addItems(List<E> itemList, boolean addEvenIfAlreadyInList, boolean addToStartOfList) {
        for (int i = 0, size = itemList.size(); i < size; i++) {
            if (addEvenIfAlreadyInList || getItemIndex(itemList.get(i)) == -1) {
                if (addToStartOfList) {
                    addItemAtIndex(itemList.get(i), 0);
                } else {
                    addItem(itemList.get(i));
                }
            }
        }
    }

    public void addItems(List<E> itemList) {
//        for (int i = 0, size = itemList.getSize(); i < size; i++) {
//            if (getItemIndex(itemList.getItemAt(i)) == -1) {
//                addItem(itemList.getItemAt(i));
//            }
//        }
        addItems(itemList, false, false);
    }

    /**
     * removes the items in itemList which are already in the ItemList. Items in
     * itemList that are not in this list are just ignored.
     *
     * @param itemList
     */
    public void removeItems(List<E> itemList) {
        for (int i = 0, size = itemList.size(); i < size; i++) {
            removeItem(itemList.get(i));
        }
    }

    /**
     * inserts item after the currently selected item. If no valid selection,
     * item is added to the end of the list. This is not so useful for ItemList,
     * but is handy in ItemListCombined to allow to always insert items in the
     * same way, even though the operation is more complex in ItemListCombined
     */
    public void insertAfterSelected(E item) {
        if (isSelectionValid()) {
            addItemAtIndex(item, getSelectedIndex() + 1); //should be +1(?!). selectedIndex+1 should always be a valid position since we check above
        } else {
            addItem(item);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean xaddItemTyped(E item) {
//        if (isBaseType(item.getTypeId())) {
//            addItem(item);
//            return true;
//        }
//        Log.l("wrong type inserted in ItemList >" + this.shortString() + "< addItemTyped(" + item + ")");
//        return false;
//    }
    /**
     * if the type of the inserted item matches the accepted types for the list
     * then it is inserted and true returned. Otherwise nothing is done and
     * false is returned.
     *
     * @param item
     * @param index
     * @return
     */
//    public boolean xaddItemAtIndexTyped(E item, int index) {
//        // just insert normally if no type defind (!isTyped()), otherwise only insert if type matches (isBaseType(((BaseItem) item).getTypeId()))
//        if (/*
//                 * !isTyped() || -test no in isBaseType
//                 */isBaseType(item.getTypeId())) {
//            addItemAtIndex(item, index);
//            return true;
//        }
//        Log.l("wrong type inserted in ItemList " + this.shortString() + "addItemAtIndexTyped(" + item + ", " + index + ")");
//        return false;
//    }
//</editor-fold>
    /**
     * returns the (first!) index of item in the list or -1 if the item is not
     * in the list, the list is empty, or item is null.
     *
     * @param item to search for in the list
     * @return
     */
//    public int getItemIndex(Object item) {
    @Override
    public int getItemIndex(ItemAndListCommonInterface item) {
//        for (int i = 0, size = getSize(); i < size; i++) { //optimization!!! more efficient algorithm!!!
//            if (getItemAt(i).equals(item)) {
////            if (getItemAt(i) == item) {
//                return i;
//            }
//        }
//        return -1;
//        return getListFull().indexOf(item);
//        return getListFull().indexOf(item);
        return getList().indexOf(item);
    }

    /**
     * @inheritDoc
     */
    public E getItemAt(int index) {
//        if (index < itemVector.size() && index >= 0) {
//            return itemVector.elementAt(index);
        if (index < getSize() && index >= 0) {
//            return itemList.get(index);
//            return getListFull().get(index);
            return getList().get(index);
        }
        return null;
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * returns and removes item at position index. Returns null if no item.
     *
     * @param index
     * @return
     */
//    public E getAndRemoveItemAtXXX(int index) {
//        E item = getItemAt(index);
//        if (item != null) {
//            removeItem(index);
//        }
//        return item;
//    }
//</editor-fold>
    /**
     * @inheritDoc
     */
    public void removeItem(int index) {

        E item = getItemAt(index);
        Bag<E> itemBag = getItemBag();
        if (hasSubLists() && itemBag != null && itemBag.getCount(item) > 1) { //if there are sublists, only remove item from list if it has been added one single time
            itemBag.remove(item);
            setItemBag(itemBag);
        } else { //else remove normally

            int listSize = getSize();

            if (0 <= index && index < listSize) {
                // if (index < listSize && index >= 0) {
//                E baseItem = getItemAt(index);
//            itemVector.removeElementAt(index);
//                super.remove(index);
//                itemList.remove(index);
//                List<E> list = getListFull();
                List<E> listFull = getListFull();
//                List<E> list = getList();
                int indexFull = listFull.indexOf(item);
                listFull.remove(indexFull);
                setList(listFull);

//                if (baseItem.getOwnerList() == this) {
//                    baseItem.setOwnerList(null); //if removed BaseItem has this list as parent, then reset parent to null
//                }
                int oldSelectedIndex = selectedIndex;
                if (index < selectedIndex) {
                    selectedIndex--; // (2) update to still point to same element as before, no fireSelectionEvent needed
                } else if (index == selectedIndex) { // selected item is removed (most usual case)
                    if (selectedIndex == listSize - 1) { // (1) if removed and selected element was the last in the list
                        if (listSize != 1) {
                            selectedIndex--; // then update selectedIndex to point to the element preceeding the last
                            fireSelectionEvent(oldSelectedIndex, selectedIndex);
                        } else {
                            // (5) do nothing
                        }
                    } else { // index = selectedIndex && selectedIndex != getSize()-1
                        // (4) do not change selectedIndex since it should point to the element suceeding the selected (and just removed)
                        fireSelectionEvent(oldSelectedIndex, selectedIndex); // since selectedIndex now points to another element than before
                    }

                } // else { // (3) do nothing since selectedIndex < index }
//                if (autoAddRemoveListAsListenerOnInsertedItems) { // && baseItem instanceof BaseItem) { //(baseItem = (BaseItem) getItemAt(index)) instanceof BaseItem) {
//                    baseItem.removeChangeEventListener(this); // remove this list as changeListener on the object
//                }
                fireDataChangedEvent(DataChangedListener.REMOVED, index);
//                changed(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, index, (BaseItem) obj, triggerEvent));
            }
        }
    }

    /**
     * removes item from list. If item is not in list, nothing is done.
    Removes also works with bags/meta-lists
     *
     * @param item to remove
     */
    public boolean removeItem(ItemAndListCommonInterface item) {
        int index = getItemIndex(item);
        if (index != -1) {
            removeItem(index); //informing changelisteners etc done in removeImte(index)
            return true;
        }
        return false;
//        else {
//            throw new Exception();
//        }
//update(); //- not necessary since done in the call to removeItem(getItemIndex(item));
    }

    /**
     * empties the list completely by remove()ing all items
     */
    public void removeAllItems() {
//        for (int i=0, size=getSize(); i<size; i++) {
        while (getSize() > 0) {
            removeItem(0);
        }
    }

    /**
     * @inheritDoc
     */
    public void setSelectedIndex(int index) {
        int oldIndex = this.selectedIndex;
        this.selectedIndex = index;
        fireSelectionEvent(oldIndex, selectedIndex);
    }

    /**
     * returns the number of Items that are not done. If the list contains
     * something else than Items, returns zero
     */
    static public int getNumberOfUndoneItems(List list, boolean recurse) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int countUndone = 0;
//        for (Object item : itemList) {
//        for (E item : itemList) {
        for (Object elt : list) {
//            if (!((ItemAndListCommonInterface) item).isDone()) {
            if (elt instanceof Item && !(((Item) elt).isDone())) { //use Item to optimize (since implementing isDone() on a list would be expensive
                countUndone++;
                //only count undone subtasks if project is not done
//                if (includeSubTasks) {
////                    countUndone += ((ItemAndListCommonInterface) item).getNumberOfUndoneItems();
//                    countUndone += ((Item) item).getNumberOfUndoneItems();
//                }
            } else if (elt instanceof ItemAndListCommonInterface) {
                countUndone += ((ItemAndListCommonInterface) elt).getNumberOfUndoneItems(recurse);
            }
        }
        return countUndone;
    }

    static public int getNumberOfUndoneItemsOLD(List list, boolean recurse) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int countUndone = 0;
//        for (Object item : itemList) {
//        for (E item : itemList) {
        for (Object item : list) {
//            if (!((ItemAndListCommonInterface) item).isDone()) {
            if (item instanceof ItemAndListCommonInterface && !(((ItemAndListCommonInterface) item).isDone())) { //use Item to optimize (since implementing isDone() on a list would be expensive
                countUndone++;
                //only count undone subtasks if project is not done
//                if (includeSubTasks) {
////                    countUndone += ((ItemAndListCommonInterface) item).getNumberOfUndoneItems();
//                    countUndone += ((Item) item).getNumberOfUndoneItems();
//                }
            }
            if (recurse && list instanceof ItemAndListCommonInterface) {
                countUndone += ((ItemAndListCommonInterface) item).getNumberOfUndoneItems(true);
            }
        }
        return countUndone;
    }

    public static int getNumberOfItemsThatWillChangeStatus(List list, boolean recurse, ItemStatus newStatus, boolean changingFromDone) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int nbCountChangeStatus = 0;
        for (Object elt : list) {
//            if (item instanceof Item && !(((Item) item).isDone())) { //use Item to optimize (since implementing isDone() on a list would be expensive
            if (elt instanceof ItemAndListCommonInterface) { // && ((Item) item).shouldTaskStatusChange(newStatus)) { //use Item to optimize (since implementing isDone() on a list would be expensive
//                nbCountChangeStatus += ((ItemAndListCommonInterface) elt).getNumberOfItemsThatWillChangeStatus(true, newStatus);
                nbCountChangeStatus += ((ItemAndListCommonInterface) elt).getNumberOfItemsThatWillChangeStatus(recurse, newStatus, changingFromDone);
            }
//            if (recurse && list instanceof ItemAndListCommonInterface) {
//                nbCountChangeStatus += ((ItemAndListCommonInterface) item).getNumberOfItemsThatWillChangeStatus(true, newStatus);
//            }
        }
        return nbCountChangeStatus;
    }

    public static int getCountOfSubtasksWithStatus(List list, boolean recurse, List<ItemStatus> statuses) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        int nbCountChangeStatus = 0;
        for (Object elt : list) {
            if (elt instanceof ItemAndListCommonInterface) {
                nbCountChangeStatus += ((ItemAndListCommonInterface) elt).getCountOfSubtasksWithStatus(recurse, statuses);
            }
        }
        return nbCountChangeStatus;
    }

//    public int getCountOfSubtasksWithStatus(boolean recurse, List<ItemStatus> statuses) {
    public int getCountOfSubtasksWithStatus(boolean recurse, List statuses) {
        return getCountOfSubtasksWithStatus(getListFull(), recurse, statuses);
    }

//    public static int getNumberOfItemsThatWillChangeStatusOLD(List list, boolean recurse, ItemStatus newStatus) {
//        if (list == null || list.size() == 0) {
//            return 0;
//        }
//        int nbCountChangeStatus = 0;
//        for (Object item : list) {
////            if (item instanceof Item && !(((Item) item).isDone())) { //use Item to optimize (since implementing isDone() on a list would be expensive
//            if (item instanceof Item && ((Item) item).shouldTaskStatusChange(newStatus)) { //use Item to optimize (since implementing isDone() on a list would be expensive
//                nbCountChangeStatus++;
//            }
//            if (recurse && list instanceof ItemAndListCommonInterface) {
//                nbCountChangeStatus += ((ItemAndListCommonInterface) item).getNumberOfItemsThatWillChangeStatus(true, newStatus);
//            }
//        }
//        return nbCountChangeStatus;
//    }
    @Override
    public int getNumberOfItemsThatWillChangeStatus(boolean recurse, ItemStatus newStatus, boolean changingFromDone) {
        return getNumberOfItemsThatWillChangeStatus(getListFull(), recurse, newStatus, changingFromDone);
    }

//    @Override
//    public int getNumberOfUndoneItems() {
//        return getNumberOfUndoneItems(getList(), false); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
//    }
    @Override
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
        return getNumberOfUndoneItems(getListFull(), includeSubTasks); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
    }

    public static int getNumberOfItems(List list, boolean onlyUndone, boolean countLeafTasks) {
        int count = 0;
        for (Object obj : list) {
            if (obj instanceof ItemAndListCommonInterface) {
                count += ((ItemAndListCommonInterface) obj).getNumberOfItems(onlyUndone, countLeafTasks);
            }
        }
        return count;
    }

    @Override
    public int getNumberOfItems(boolean onlyUndone, boolean countLeafTasks) {
        int count = 0;
        for (Object obj : getListFull()) {
            if (obj instanceof ItemAndListCommonInterface) {
                count += ((ItemAndListCommonInterface) obj).getNumberOfItems(onlyUndone, countLeafTasks);
            }
        }
        return count;
    }

    @Override
    public int getNumberOfSubtasks(boolean onlyUndone, boolean countLeafTasks) {
        return getNumberOfItems(onlyUndone, countLeafTasks);
    }

    public int getNumberOfItemsOLD(boolean onlyUndone, boolean countLeafTasks) {
        int count = 0;
        for (Object obj : getListFull()) {
            if (obj instanceof Item) {
                Item item = (Item) obj;
                if (countLeafTasks && item.isProject()) {
                    count += item.getNumberOfItems(onlyUndone, countLeafTasks);
                } else {
//                in else case: !countLeafTasks || !item.isProject()
//                        count += !onlyUndone || !item.isDone() ? 1 : 0;
                    count += onlyUndone && item.isDone() ? 0 : 1;
                }
            }
        }
        return count;
    }

    /**
    
    @param previousItem
    @param returnFirstItemIfPreviousNotFound if previousItem is not found, or is null, then return the first item in the list if such one exists (otherwise return null)
    @return net item or null if no next item
     */
    public static Item getNextItemAfter(List<Item> list, Item previousItem, boolean returnFirstItemIfPreviousNotFound) {
//        return getNextLeafItemMeetingConditionImpl(previousItem, excludeWaiting, false);
//        List<E> list = getList(); //get filtered list
        int prevIndex;
        if (previousItem != null) {
            prevIndex = list.indexOf(previousItem);
        } else {
            prevIndex = -1;
        }
        int nextIndex;
        if (prevIndex < 0 && returnFirstItemIfPreviousNotFound) {
            nextIndex = 0;
        } else {
            nextIndex = prevIndex + 1;
        }
        if (nextIndex >= 0 && nextIndex < list.size()) { //if nextIndex is a valid index
            return (Item) list.get(nextIndex);
        }
        return null;
    }

    /**
    in the case where previousItem is still in the list, but filtered out, this will still attempt to find an unfiltered item that comes after previousItem in the list
    @param previousItem
    @return 
     */
    private E getNextAfterEvenIfFiltered(E previousItem, boolean returnFirstItemIfPreviousNotFound) {
        List<E> list = getList();
        int index = list.indexOf(previousItem);
        if (index >= 0) {
            if (index < list.size() - 1) //if there is an element *after* previousItem in the list
                return getList().get(index + 1);
            else {
                if (returnFirstItemIfPreviousNotFound && list.size() > 0)
                    return list.get(0);
                else
                    return null; //else: previousItem was found in list, but was the last item
            }
        } else { //previousItem was NOT dfound in the list, try to find in unfiltered list and return first unfiltered item after it (if any)
            List<E> listFull = getListFull();
            int indexFull = listFull.indexOf(previousItem);
            if (indexFull >= 0) {
                //run through the elements in listFull and if return the first one which is in listFiltered  (if any)
                for (E elt : listFull.subList(indexFull + 1, listFull.size())) { //only working if no duplicates in list!!
                    if (list.contains(elt))
                        return elt;
                }
            }
            return null;
        }
    }

    public E getNextItemAfter(Item previousItem, boolean returnFirstItemIfPreviousNotFound) {
//        return getNextLeafItemMeetingConditionImpl(previousItem, excludeWaiting, false);
        List<E> list = getList(); //get filtered list
//        int prevIndex;
//        if (previousItem != null) {
//            prevIndex = list.indexOf(previousItem);
//        } else {
//            prevIndex = -1;
//        }
        int prevIndex = previousItem != null ? prevIndex = list.indexOf(previousItem) : -1;
//        int nextIndex;
//        if (prevIndex < 0 && returnFirstItemIfPreviousNotFound) {
//            nextIndex = 0;
//        } else {
//            nextIndex = prevIndex + 1;
//        }
        int nextIndex = prevIndex < 0 && returnFirstItemIfPreviousNotFound ? 0 : prevIndex + 1;
        if (nextIndex >= 0 && nextIndex < list.size()) { //if nextIndex is a valid index
            return list.get(nextIndex);
        }
        return null;
    }

//    public E getNextItemAfter(Item previousItem, boolean returnFirstItemIfPreviousNotFound) {
////     return getNextItemAfter((List<E>)getList(), previousItem, returnFirstItemIfPreviousNotFound);
//     return getNextItemAfter(getList(), previousItem, returnFirstItemIfPreviousNotFound);
//    }
    /**
     * set all items which are not already in state done to or itemlists in this
     * list Done
     */
    public void setAllItemsDone(boolean done, boolean recurse) {
//        for (int i = 0, size = getSize(); i < size; i++) {
//            Object obj = getItemAt(i);
//        for (int i = 0, size = getSize(); i < size; i++) {
        for (ItemAndListCommonInterface item : getListFull()) {
//                ItemAndListCommonInterface item = (ItemAndListCommonInterface) obj;
            if (item instanceof ItemList && recurse) {
                ((ItemList) item).setAllItemsDone(done, recurse);
            } else if (item.isDone() != done) {
                item.setDone(done);
            }
//            } else if (obj instanceof ItemList && recurse) {
//                ((ItemList) obj).setAllItemsDone(done, recurse);
//            }
        }
    }

    public void setAllItemsDone(boolean done) {
        setAllItemsDone(done, true);
    }

    public void setAllItemsDone() {
        setAllItemsDone(true, true);
    }

    /**
     * @inheritDoc
     */
    public void addDataChangedListener(DataChangedListener l) {
        if (dataListener == null) {
            dataListener = new EventDispatcher();
        }
        dataListener.addListener(l);
    }

    /**
     * @inheritDoc
     */
    public void removeDataChangedListener(DataChangedListener l) {
        if (dataListener != null) {
            dataListener.removeListener(l);
        }
    }

    protected void fireDataChangedEvent(final int status, final int index) {
        if (dataListener != null) {
            dataListener.fireDataChangeEvent(index, status);
        }
    }

    /**
     * @inheritDoc
     */
    public void addSelectionListener(SelectionListener l) {
        if (selectionListener == null) {
            selectionListener = new EventDispatcher();
        }
        selectionListener.addListener(l);

    }

    /**
     * @inheritDoc
     */
    public void removeSelectionListener(SelectionListener l) {
        if (selectionListener != null) {
            selectionListener.removeListener(l);
        }
    }

    protected void fireSelectionEvent(int oldIndex, int newIndex) {
        if (selectionListener != null) {
            selectionListener.fireSelectionEvent(oldIndex, newIndex);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * will move the item at position fromPos to position toPos in one 'atomic'
     * operation (instead of using insertAt + removeItem). selectedIndex is
     * updated to still point to the same element. It works by first inserting
     * at the given position, then removing the moved item (makes any
     * difference??!!). A B C - move(0,1): B A C; move(1,0): B A C; move(0,2): B
     * C A; move(0,3): B C A; move(1,1): A B C; move(2,1): A C B; move(2,0): C A
     * B; move(3,x): A B C; move(-1,x): A B C
     *
     * @param fromPos must be in range 0->getSize-1
     * @param toPos must be in range 0-getSize (if ==getSize then item moved to
     * after the last element of the list)
     * @inherit
     */
//    public void moveItem(int fromPos, int toPos/*
//             * boolean keepMovedItemSelected
//     */) { //, boolean before) {
//
//        if (fromPos == toPos || fromPos < 0 || toPos < 0 || fromPos >= getSize() || toPos >= getSize()) { //fromPos == toPos <=> no need to do anything; other exps: illegal move, ignore
//            return;
//        }
////        if (fromPos >= 0 || fromPos < getSize() && toPos >= 0 && toPos <= getSize()) {
////        if (fromPos >= 0 || fromPos < getSize() && toPos >= 0 && toPos <= getSize()) {
//
//        if (fromPos < toPos) {
//            // A B C - move(0,1): B A C;
////            itemList.insertElementAt(itemList.elementAt(fromPos), toPos + 1);
//            itemList.add(toPos + 1, itemList.get(fromPos));
//        } else {
////            itemList.insertElementAt(itemList.elementAt(fromPos), toPos);
//            itemList.add(toPos, itemList.get(fromPos));
//        }
////            else
////                items.insertElementAt(items.elementAt(fromPos), toPos+1);
//
//        if (toPos < fromPos) {
////            itemList.removeElementAt(fromPos + 1);
//            itemList.remove(fromPos + 1);
//        } else {
////            itemList.removeElementAt(fromPos);
//            itemList.remove(fromPos);
//        }
//
//        /*
//         * logic to update selectedIndex: if fromPos == selectedIndex (most
//         * frequent case), then selectedIndex=toPos. If both to&from are less
//         * than or bigger than selectedIndex, then no need to update (the move
//         * does not change the position of the at item at selectedIndex). If
//         * toPos<=selectedIndex (and fromPos>selectedIndex), then selected item
//         * is pushed one down the list so selectedIndex++. If
//         * fromPos<selectedIndex (and toPos>selectedIndex), then
//         * selectedIndex--.
//         */
////            if (keepMovedItemSelected) {
//        int oldSelectedIndex = selectedIndex;
//        if (selectedIndex == fromPos) {
//            selectedIndex = toPos;
//        } else if (toPos <= selectedIndex && fromPos > selectedIndex) {
//            selectedIndex++;
//        } else if (fromPos < selectedIndex && toPos > selectedIndex) {
//            selectedIndex--;
//        } // else: toPos and fromPos are both either smaller than selectedIndex or bigger than selectedIndex, so no need to update selectedIndex
//        if (oldSelectedIndex != selectedIndex) {
//            fireSelectionEvent(selectedIndex, oldSelectedIndex);
//        }
////            }
//
//        int lowestChangedPos = Math.min(toPos, fromPos); //we use the lowest changed index since it may affect the rest of the list (eg calculated sums)
//        fireDataChangedEvent(DataChangedListener.CHANGED, lowestChangedPos);
////        changed(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, lowestChangedPos, itemVector.elementAt(lowestChangedPos), triggerEvent));
////        }
//    }
    /**
     * used by quicksort (only)
     */
//    private void swap(int i, int j) {
////        Object tmp = expanded.elementAt(i);
////        E tmp = itemList.elementAt(i);
//        E tmp = itemList.get(i);
////        expanded.setElementAt(expanded.elementAt(j), i);
////        itemList.setElementAt(itemList.elementAt(j), i);
//        itemList.set(i, itemList.get(j));
////        expanded.setElementAt(tmp, j);
//        itemList.set(j, tmp);
//    }
//    private ComparatorInterface sortDef;
//    private Comparator sortDef;
    /**
     * to compare WorkSlots, us: new ComparatorInterface() { public int
     * compare(Object o1, Object o2) { return
     * Item.compareLong(((WorkSlot)o1).getStart(), ((WorkSlot)o1).getStart()); }
     * });
     *
     * @param sortDef
     */
//    public void quicksort(ComparatorInterface sortDef) {
//    public void quicksort(Comparator sortDef) {
//        if (itemList.size() < 2 || sortDef == null) {
//            return;
//        }
//        this.sortDef = sortDef;
//        quicksort(0, itemList.size() - 1); //, sortDef);
//        //optimization: set a flag whenever the list is sorted (and reset whenever an item is added in a non-sorted way)
//    }
    /**
     * quicksort only sorts the appearance of the list - it does not affect the
     * underlying model used template(?):
     * http://www.cs.berkeley.edu/~yelick/61bf97/lectures/28/QuickSort.java
     * Better inline version with skipping equals:
     * http://www.roseindia.net/java/beginners/arrayexamples/QuickSort.shtml
     * Fixing for duplicates:
     * http://www.angelfire.com/pq/jamesbarbetti/articles/sorting/001_QuicksortIsBroken.htm
     * Consider using multikey quicksort for sorting on several fields, e.g.
     * http://www.itl.nist.gov/div897/sqg/dads/HTML/multikeyQuicksort.html.
     * Ref's: (not sure about original source). But here are some:
     * http://en.wikipedia.org/wiki/Quicksort
     * http://www.cs.princeton.edu/introcs/42sort/QuickSort.java.html -
     * optimizes
     */
//    private void quicksort(/*Vector v,*/ExpandedList expanded, int left, int right, boolean ascending, int sortItemSortField) {
//    private void quicksort(/*
//             * Vector v,
//             *//*
//             * ExpandedList expanded,
//             */int left, int right) {//, SortDefinition sortDef) {
//        //optimization: move parameters to global variables
//        int i, last;
//
//        if (left >= right) { // do nothing if array size < 2
//            return;
//        }
//        swap(left, (left + right) / 2);
//        last = left;
//        for (i = left + 1; i <= right; i++) {
////            IComparable ic = (IComparable) expanded.getObject(i); //v.elementAt(i);
////            IComparable ic = (IComparable) getItemAt(i); //v.elementAt(i);
////            IComparable icleft = (IComparable) expanded.getObject(left); //v.elementAt(left);
////            IComparable icleft = (IComparable) getItemAt(left); //v.elementAt(left);
////            int res = ss.getView().getSortDef().compare(ic, icleft);
////            int res = sortDef.compare(ic, icleft);
//            int res = sortDef.compare(getItemAt(i), getItemAt(left));
////            if (ascending && ic.compareTox(icleft, sortItemSortField) < 0) {
//            if (res < 0) {
//                swap(++last, i);
//            }
//            //TODO!!: remove duplicates, e.g. if a list of categories where several categories contain the same item
//            // THHJ: my quick guess at how to remove duplicates:
//            // else { // equal values
//
//        }
//        swap(left, last);
////        quicksort(expanded, left, last - 1);
////        quicksort(expanded, last + 1, right);
//        quicksort(left, last - 1); //, sortDef);
//        quicksort(last + 1, right); //, sortDef);
//    }
    /**
     * inserts item into an already sorted list and maintains it sorted (should
     * be implemented using binary but for the use case with workSlots where
     * small items are inserted into the beginning of the list the current
     * linear search should be OK)
     *
     * @param item
     */
//    public void insertSortedIntoSortedList(E item, Comparator comparator) {
//        for (int i = 0, size = getSize(); i < size; i++) {
////            if (comparator.compare(item, getItemAt(i))==-1) {
//            if (comparator.compare(item, getItemAt(i)) <= 0) { //0 => insert new item *before* items of same value
//                addItemAtIndex(item, i);
//                return;
//            }
//        }
//        addItem(item); //add to end of list if list is empty or no smaller element found
////        int size=getSize();
////        int top=size;
////        if (size==0) {
////            addItem(item);
////            return;
////        }
////        int mid = size/2;
////        if (getItemAt(mid) < item) while (false) {
////            int i=i;
////        }
//    }
//    public void insertSortedIntoSortedListBin(E item, Comparator comparator) {
//        for (int i = 0, size = getSize(); i < size; i++) {
////            if (comparator.compare(item, getItemAt(i))==-1) {
//            if (comparator.compare(item, getItemAt(i)) <= 0) { //0 => insert new item *before* items of same value
//                addItemAtIndex(item, i);
//                return;
//            }
//        }
//        addItem(item); //add to end of list if list is empty or no smaller element found
//
//        if (getSize() == 0) {
//            return;
//        }
//        int bottom = 0;
//        int top = getSize() - 1;
//        int mid;
//        while (top > bottom) {
//            mid = (top - bottom) / 2;
//            //invariant: item <=
//            if (comparator.compare(item, getItemAt(mid)) <= 0) { // <=0 : item smaller than or equal to mid
//                top = mid;
//            } else {
//                bottom = mid == 0 ? 1 : mid; //cover the case with just 2 items in array (
//            }
//        }
//        //top==bottom
//        if (comparator.compare(item, getItemAt(top)) <= 0) { // <=0 : item smaller than or equal to mid
//            addItemAtIndex(item, top);
//        } else {
//            addItemAtIndex(item, top + 1);
//        }
////        int size=getSize();
////        int top=size;
////        if (size==0) {
////            addItem(item);
////            return;
////        }
////        int mid = size/2;
////        if (getItemAt(mid) < item) while (false) {
////            int i=i;
////        }
//    }
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
////#mdebug
////        Log.l(" --receiveChangeEvent: " + this + "  RECEIVED-> " + changeEvent);
////#enddebug
//        triggerEvent = changeEvent;
//        Object changedItem = changeEvent.getSource();
//        if (changedItem == workTimeDefinition) {
//            //do nothing (worktime is only correlated with tasks when displayed in renderer)
//        } else { //from ItemList's items
////        if (changeEvent.getSource() == workTimeDefinition) {
////        if (changedItem instanceof WorkSlot) {
////        } else
//            int index = getItemIndex(changedItem); //TODO!! optimization: avoid searching through the list to get the index from the object source itself, eg pass the index along when it is known where the change is made
////        int listObject = changeEvent.getListIndex();
//            int changeId = changeEvent.getChangeId();
//
//            if (ChangeValue.isSet(changeId, ChangeValue.CHANGED_BASEITEM_DELETED)) { //someIL_REMOVED if a sublist is removed from the list
////#mdebug
//                ASSERT.that(changedItem instanceof Item, "WARNING:  changeID=" + ChangeValue.toString(changeId) + " received, but item is not of type Item, item=" + changedItem);
////#enddebug
//                removeItem(index); //also calls derivedSumVector.invalidate(index);
//            } //else { //if (changeId == ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED) { //IL_REMOVED if a sublist is removed from the list
//            if (ChangeValue.isSetEither(changeId, ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED,
//                    ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, ChangeValue.CHANGED_BASEBASEITEM_UNKNOWN_CHANGE,
//                    ChangeValue.CHANGED_ITEM_ANY_CHANGE)) {
//                fireChangeEvent(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, index, changedItem, changeEvent));
//                fireDataChangedEvent(DataChangedListener.CHANGED, index);
//            } //else {
//            //#mdebug
//            else { //if (ChangeValue.isSetNeither(changeId, ChangeValue.CHANGED_BASEITEM_DELETED, ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, changeId & ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, changeId & ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, ChangeValue.CHANGED_BASEBASEITEM_UNKNOWN_CHANGE)) {
//                ASSERT.that("ItemList.receiveChangeEvent: Unexpected #changeEvent=" + changeEvent + " received, #item=" + changedItem + " #list=" + this);
//            }
//            triggerEvent = null;
//        }
    /**
     * removes this list as changeListener from all items currently in the list.
     * Does nothing if autoAddRemoveListAsListenerOnInsertedItems is false
     */
//    public void removeAsChangeListenerFromAllItems() {
//        if (autoAddRemoveListAsListenerOnInsertedItems) {
//            for (int i = 0, size = getSize(); i < size; i++) {
//                Object item = getItemAt(i);
//                if (item instanceof E) {
//                    ((E) item).removeChangeEventListener(this);
//                }
//            }
//        }
////        super.removeAllChangeEventListeners();
//    }
    /**
     * compares this list to the list in the argument. Returns true if they
     * contain the same elements, otherwise false.
     *
     * @param list
     * @return
     */
//    public boolean differentElements(ItemList<E> list) {
////        if (getAddedItems(list, list2).getSize() == 0 && getRemovedItems(list, list2).getSize() == 0) {
//        if (getAddedItems(list).getSize() == 0 && getRemovedItems(list).getSize() == 0) {
//            return false;
//        }
//
//        return true;
//    }
//</editor-fold>
    /**
     * Returns the items added to newList compared to this list
     *
     * @param newList
     * @return
     */
    public List<E> getAddedItems(List<E> newList) {
//        List result = new ArrayList();
////        result.autoAddRemoveListAsListenerOnInsertedItems=false; //TODO!!!! always set false for all temporary lists
//        for (int i = 0, size = newList.size(); i < size; i++) {
////            if (list.getItemIndex(newList.getItemAt(i)) == -1) // -1 means item in new list not found in old list
//            if (getItemIndex(newList.get(i)) == -1) // -1 means item in new list not found in old list
//            {
//                result.add(newList.get(i)); // that means it was added, so add to result
//            }
//        }
//        return result;
//        return getAddedItems(itemList, newList);
        return getAddedItems(getListFull(), newList);
    }

    /**
     * return the list of additional items in newList when compared to orgList
     *
     * @param orgList
     * @param newList
     * @return
     */
    public static List getAddedItems(List orgList, List newList) {
        List result = new ArrayList();
        for (int i = 0, size = newList.size(); i < size; i++) {
//            if (list.getItemIndex(newList.getItemAt(i)) == -1) // -1 means item in new list not found in old list
            if (!orgList.contains(newList.get(i))) {// -1 means item in new list not found in old list
                result.add(newList.get(i)); // that means it was added, so add to result
            }
        }
        return result;
    }

    /**
     * Returns the items removed from newList compared to this list
     *
     * @param newList
     * @return
     */
    public List getRemovedItems(List newList) {
//        return getAddedItems(newList, list);
//        return newList.getAddedItems(itemList); //reverse getAddedItems by checking what is 'added' in this list vs newList
//        return getAddedItems(newList, itemList);
        return getAddedItems(newList, getListFull()); //TODO!!! check if getListFull is correct (seems so, but not sure)
    }

    /**
     * returns an composite state for a project based on the state of all it's
     * subtasks/subprojects Rules: - all tasks Done or Cancelled => Done; - one
     * or more tasks Ongoing => Ongoing; - if none Ongoing, then if one or more
     * tasks are Waiting => Waiting; - else: Created
     *
     * @return
     */
    protected ItemStatus getListStatus() {
        ItemStatus projectStatus;
        Bag<ItemStatus> statusCount = new Bag<ItemStatus>();
        int listSize = getSize();
//        for (int i = 0, size = listSize; i < size; i++) {
//            statusCount.add(getItemAt(i).getStatus());
//        }
        for (E item : getListFull()) {
            statusCount.add(item.getStatus());
        }
        if (statusCount.getCount(ItemStatus.CANCELLED) == listSize) {//all subtasks are cancelled
            projectStatus = ItemStatus.CANCELLED;
//        if (statusCount[BaseItemOrList.STATUS_CREATED] == listSize) {//all subtasks are created
//                projectStatus = BaseItemOrList.STATUS_CREATED; 
        } else if (statusCount.getCount(ItemStatus.DONE) + statusCount.getCount(ItemStatus.CANCELLED) == listSize) {//all subtasks are either done or cancelled
            projectStatus = ItemStatus.DONE;
        } else if (statusCount.getCount(ItemStatus.ONGOING) > 0
                || (statusCount.getCount(ItemStatus.DONE) > 0
                && statusCount.getCount(ItemStatus.CREATED) > 0
                && statusCount.getCount(ItemStatus.WAITING) == 0)) { //the whole project is ongoing if just one task is ongoing, or if there if some, but not all, tasks are completed
            projectStatus = ItemStatus.ONGOING;
        } else if (statusCount.getCount(ItemStatus.WAITING) > 0) { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
//        } else { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
            projectStatus = ItemStatus.WAITING;
        } else { //if (statusCount[BaseItemOrList.STATUS_CREATED]>0) // if there are no ongoing and no waiting, then the only possible state for remaining subtasks is Created
            projectStatus = ItemStatus.CREATED;
        }
        return projectStatus;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    protected Item.ItemStatus getListStatus() {
//        Item.ItemStatus projectStatus;
//        int[] statusCount = new int[BaseItemOrList.STATUS_MAX_STATUS_VALUE + 1];
//        int listSize = getSize();
//        for (int i = 0, size = listSize; i < size; i++) {
//            statusCount[((BaseItemOrList) getItemAt(i)).getStatus()]++;
//        }
//        if (statusCount[BaseItemOrList.STATUS_CANCELLED] == listSize) {//all subtasks are cancelled
//            projectStatus = BaseItemOrList.STATUS_CANCELLED;
////        if (statusCount[BaseItemOrList.STATUS_CREATED] == listSize) {//all subtasks are created
////                projectStatus = BaseItemOrList.STATUS_CREATED;
//        } else if (statusCount[BaseItemOrList.STATUS_DONE] + statusCount[BaseItemOrList.STATUS_CANCELLED] == listSize) {//all subtasks are either done or cancelled
//            projectStatus = BaseItemOrList.STATUS_DONE;
//        } else if (statusCount[BaseItemOrList.STATUS_ONGOING] > 0 || (statusCount[BaseItemOrList.STATUS_DONE] > 0 && statusCount[BaseItemOrList.STATUS_CREATED] > 0 && statusCount[BaseItemOrList.STATUS_WAITING] == 0)) { //the whole project is ongoing if just one task is ongoing, or if there if some, but not all, tasks are completed
//            projectStatus = BaseItemOrList.STATUS_ONGOING;
//        } else if (statusCount[BaseItemOrList.STATUS_WAITING] > 0) { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
////        } else { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
//            projectStatus = BaseItemOrList.STATUS_WAITING;
//        } else { //if (statusCount[BaseItemOrList.STATUS_CREATED]>0) // if there are no ongoing and no waiting, then the only possible state for remaining subtasks is Created
//            projectStatus = BaseItemOrList.STATUS_CREATED;
//        }
//        return projectStatus;
//    }
//</editor-fold>
    @Override
    public ItemStatus getStatus() {
        return getListStatus();
//        return Item.getStatus(getList()); //TODO replace by generic Item.getStatus(List)
    }

    public long getSumField() {
        return 0;
    }

    public long getSumField(int fieldId) {
        return getSumField();
    }

    public boolean ignoreSumField() {
        return false;  //never ignore sum of ItemLists (TODO!!!! are there cases where the sum should be ignored?
    }

    /**
     * returns true if item!=null and is contained in ItemList
     */
    public boolean contains(E item) {
//        return (item != null && getVector().contains(item)); //
        return (item != null && getListFull().contains(item)); // is there a risk that the check could be on a filtered item (thus using listFull will give the wrong result)?
    }

    /**
     * returns true if this list has sublist as a direct, or indirect, sublist
     *
     * @param obj
     * @return
     */
    public boolean containsRecurse(E obj) {
        if (obj != null && getSize() > 0) {
            if (contains(obj)) {
                return true;
            } else {
//                Object temp;
//                for (int i = 0, size = getSize(); i < size; i++) {
//                    temp = getItemAt(i);
                for (E temp : getListFull()) {
//                    temp = getItemAt(i);
                    if (temp instanceof ItemList) {
                        if (((ItemList) temp).containsRecurse(obj)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * returns list of ItemLists (contained in this ItemList) that contains
     * directly (or indirectly!) obj.
     *
     * @param obj
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Vector getListOfContainingItemLists(E obj, boolean recurse) {
//        Vector listOfContainingLists = new Vector();
//        if (obj != null && getSize() > 0) {
//            for (int i = 0, size = getSize(); i < size; i++) {
//                if (recurse ? ((ItemList) getItemAt(i)).containsRecurse(obj) : ((ItemList) getItemAt(i)).contains(obj)) {
//                    listOfContainingLists.addElement(getItemAt(i));
//                }
//            }
//        }
//        return listOfContainingLists;
//    }
//</editor-fold>
//    @Override
    public boolean contains(Object o) {
        return getItemIndex((ItemAndListCommonInterface) o) != -1;
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
//            public class MyIterator <T> implements Iterator<T> {
            int index = 0;

            public boolean hasNext() {
                //implement...
//                return index < getSize();
                return index < size();
            }

            public ItemAndListCommonInterface next() {
                //implement...;
//                return (ItemAndListCommonInterface) getItemAt(index++);
                return (ItemAndListCommonInterface) get(index++);
            }

            public void remove() {
                //implement... if supported.
                throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public List getChildren() {
//        List itemList = getList();
//        DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//        return itemList; //see JavaDoc of getChildren: null should return the tree roots
//    }
//
//    @Override
//    public boolean isLeaf() {
//        return getList() == null || getList().size() == 0;
//    }
    @Override
    public List getChildrenList(Object itemInThisList) {
        if (itemInThisList == null) {
//            List l = getList(); //getList to get filtered/sorted list for display
//            if (l == null) {
////                return new Vector();
//                return new ArrayList();
//            } else {
            List itemList = getList();
//                DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//                return new Vector(itemList); //see JavaDoc of getChildren: null should return the tree roots
            return itemList; //see JavaDoc of getChildren: null should return the tree roots
//            }
        } else {
//            return ((MyTreeModel) itemInThisList).getChildrenList(null);
            return ((MyTreeModel) itemInThisList).getChildrenList(null);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Vector getChildrenXXX(Object itemInThisList) {
////        return new Vector(getInternalList());
//        if (itemInThisList == null) {
////            return new Vector(itemList); //see JavaDoc of getChildren: null should return the tree roots
//            List l = getList(); //getList to get filtered/sorted list for display
//            if (l == null) {
//                return new Vector();
//            } else {
//                List itemList = getList();
////                DAO.getInstance().fetchAllElementsInSublist(itemList, false);
//                return new Vector(itemList); //see JavaDoc of getChildren: null should return the tree roots
//            }
//        } else {
//            return new Vector(((TreeModel) itemInThisList).getChildren(null));
////            if (item instanceof Item) {
////                ItemList childrenList = ((Item) item).getItemList();
//////                if (childrenList != null && childrenList.size() > 0) { // tested by isLeaf(node)
////                    return new Vector(childrenList);
//////                }
////            } else if (item instanceof ItemList)
////                return new Vector(getInternalList());
////            else return null; //TODO??
//        }
//    }
//</editor-fold>
    static boolean isLeafStatic(Object itemInThisList) {
        if (itemInThisList instanceof Item) { //TODO replace Item
            List subtasks = ((Item) itemInThisList).getList();
            return subtasks == null || subtasks.size() == 0;
        } else if (itemInThisList instanceof Category) {
            List itemsInCategory = ((Category) itemInThisList).getList(); //getList to get filtered/sorted list for display
            return itemsInCategory == null || itemsInCategory.size() == 0;
        } else if (itemInThisList instanceof ItemList) {
            return itemInThisList == null || ((ItemList) itemInThisList).getSize() == 0;
        } else {
            assert false : "unknown type in ItemList.isLeaf()=" + itemInThisList;
            return true;
        }
    }

    @Override
    public boolean isLeaf(Object itemInThisList) {
        return isLeafStatic(itemInThisList);
//<editor-fold defaultstate="collapsed" desc="comment">
////        Vector v = getChildren(itemInThisList);
////        return v == null || v.size() == 0;
//        if (itemInThisList instanceof Item) { //TODO replace Item
////            ItemList subtasks = ((Item) itemInThisList).getItemList();
//            List subtasks = ((Item) itemInThisList).getList();
//            return subtasks == null || subtasks.size() == 0;
//        } else if (itemInThisList instanceof Category) {
////            Category category = ((Category) itemInThisList).getSubLists();
////            List subCategories = ((Category) itemInThisList).getSubLists();
////            return subCategories == null || subCategories.size() == 0;
//            List itemsInCategory = ((Category) itemInThisList).getList();
//            return itemsInCategory == null || itemsInCategory.size() == 0;
//        } else if (itemInThisList instanceof ItemList) {
////            ItemList itemList = ((ItemList) itemInThisList).getItemList();
//            return itemInThisList == null || ((ItemList) itemInThisList).getSize() == 0;
////            return true;
//        } else {
//            assert false:"unknown type in ItemList.isLeaf()="+itemInThisList;
////        }
////            try {
////                throw new Exception();
////            } catch (Exception ex) {
////                Log.e(ex);
////            }
//            return true;
//        }
////        if (node == null) {
////            return true;
////        } else if (node instanceof Item && (((Item) node).getItemList() == null || ((Item) node).getItemList().size() == 0)) {
////            return true;
////        } else {
////            return false;
////        }
//</editor-fold>
    }

    //TODO remove below definitions
    @Override
    public Object[] toArray() {
//        return itemList.toArray(); //TODO: need to load itemList from cloud first??
        return getList().toArray(); //TODO: need to load itemList from cloud first??
    }

    @Override
    public Object[] toArray(Object[] a) {
        return getList().toArray(a); //TODO: need to load itemList from cloud first??
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        return removeItem((ItemAndListCommonInterface) o);
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
    public boolean addAll(Collection c) {
        boolean success = true;
        for (Object o : c) {
            if (!add(o)) {
                success = false;
            }
        }
        return success;
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
    public boolean removeAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
    public boolean retainAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
    public void clear() {
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        removeAllItems();
    }

    @Override
    public void setStatus(ItemStatus itemStatus) {
//        ItemList subtasks = getItemList();
//        for (E itemOrList : itemList) {
//        for (E itemOrList : getList()) {
//        for (<? extends ItemAndListCommonInterface> itemOrList : getList()) {
        for (ItemAndListCommonInterface itemOrList : getListFull()) { //full to set for every subtask, even hidden ones
//            ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
            itemOrList.setStatus(itemStatus);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
//        if (this.getClass() != o.getClass()) {
//            return false;
//        }
        if (!(obj instanceof ItemList)) {
            return false;
        }

        ItemList itemList = (ItemList) obj;

//        if (!this.getClassName().equals(o2.getClassName())) {
//            return false; //different ParseObject class, different object
//        }
//        if (o2.isNoSave() && isNoSave() && o2.getText().equals(getText())) {
//            return true; //special case to ensure that temporare lists with same name remain expanded in ScreenStatistics
//        }
//        return (this.getObjectIdP() != null && this.getObjectIdP().equals(o2.getObjectIdP()));
        if (false) {
            if (this.getObjectIdP() != null) {
                return this.getObjectIdP().equals(itemList.getObjectIdP());
            } else {
                return (itemList.isNoSave() && isNoSave() && itemList.getText().equals(getText())); //special case to ensure that temporare lists with same name remain expanded in ScreenStatistics
            }
        }

        if (getObjectIdP() != null) {//&& itemList.getObjectIdP() != null) {
            //compare isDirty in case we have two instances of the same 
            if (Config.CHECK_OWNERS) ASSERT.that(!getObjectIdP().equals(itemList.getObjectIdP()) || isDirty() == itemList.isDirty(),
                        () -> "comparing dirty and not dirty instance of same object, this=" + this + ", other=" + obj);
            if (getObjectIdP().equals(itemList.getObjectIdP())) {
                return true;
            }
        }
        return false;

//        if (this.getObjectIdP() != null && this.getObjectIdP().equals(o2.getObjectIdP())) {
//            return true; //same ParseObject, same object
//        }
//        if (false && !this.getText().equals(o2.getText())) { //DON'T compare on name only
//            return false; //different name, different object
//        }
//
//        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
//        hash = 89 * hash + this.getObjectId().hashCode(); //doesn't work for unsaved objects
        hash = 89 * hash + this.getText().hashCode();
        hash = 89 * hash + this.getObjectIdP().hashCode();
//        hash = 89 * hash + Objects.hashCode(this.getText());
//        hash = 89 * hash + this.typeId;
////        hash = 89 * hash + (int) (this.guid ^ (this.guid >>> 32));
//        hash = 89 * hash + (int) (getGuid() ^ (getGuid() >>> 32));
//        hash = 89 * hash + this.rmsIdx;
//        return hash;
//    }

//        hash = 89 * hash + Objects.hashCode(this.itemList);
//        hash = 89 * hash + Objects.hashCode(this.remainingEffortSumVector);
//        hash = 89 * hash + this.selectedIndex;
//        hash = 89 * hash + Objects.hashCode(this.workTimeDefinition);
//        hash = 89 * hash + Objects.hashCode(this.dataListener);
//        hash = 89 * hash + Objects.hashCode(this.selectionListener);
//        hash = 89 * hash + Objects.hashCode(this.owner);
//        hash = 89 * hash + (this.autoAddRemoveListAsListenerOnInsertedItems ? 1 : 0);
//        hash = 89 * hash + Objects.hashCode(this.baseTypes);
//        hash = 89 * hash + (this.storeOnlySingleInstanceOfItems ? 1 : 0);
//        hash = 89 * hash + (this.makeItemListOwnerOfInsertedUnownedItems ? 1 : 0);
//        hash = 89 * hash + Objects.hashCode(this.sourceLists);
//        hash = 89 * hash + Objects.hashCode(this.metaLists);
//        hash = 89 * hash + Objects.hashCode(this.itemBag);
//        hash = 89 * hash + Objects.hashCode(this.selection);
//        hash = 89 * hash + (this.writeListItemsGuidOnly ? 1 : 0);
//        hash = 89 * hash + Objects.hashCode(this.sortDef);
        return hash;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject[] fetchComponents(int index, int amount) {
//        List<E> fullList = getList();
//        int size;
//        if (fullList==null || (size=fullList.size()) == 0 || index>=(size=fullList.size())-1) {
//            return null;
//        int size = fullList.size();
//        return getList().subList(index, Math.min(amount, workSlotList.size() - index - 1));
//        List<WorkSlot> list = workSlotList.subList(index, Math.min(amount, workSlotList.size() - index - 1));
//        }
//</editor-fold>
//}
    @Override
    public boolean addAll(int index, Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator listIterator() {
        return getList().listIterator();
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListIterator listIterator(int index) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List subList(int fromIndex, int toIndex) {
        return getList().subList(fromIndex, toIndex);
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public Date getRemainingEffortD() {
//        return new Date(getRemainingEffort());
//    }
    @Override
    public long getRemaining() {
        long sum = 0;
//        for (int i = 0, size = getSize(); i < size; i++) {
//            Object o = getItemAt(i);
//            if (o instanceof ItemAndListCommonInterface) {
//                sum += ((ItemAndListCommonInterface) o).getRemaining();
//            }
//        }
        for (ItemAndListCommonInterface o : getListFull()) {//use full list to include any hidden elements which still have remaining (eg Waiting)
            sum += o.getRemaining();
        }
        return sum;
    }

    @Override
    public long getEstimate() {
        long sum = 0;
//        for (int i = 0, size = getSize(); i < size; i++) {
//            Object o = getItemAt(i);
//            if (o instanceof ItemAndListCommonInterface) {
//                sum += ((ItemAndListCommonInterface) o).getEstimate();
//            }
//        }
        for (ItemAndListCommonInterface o : getListFull()) {
            sum += o.getEstimate();
        }
        return sum;
    }

    @Override
    public long getActual() {
        long sum = 0;
//        for (int i = 0, size = getSize(); i < size; i++) {
//            Object o = getItemAt(i);
//            if (o instanceof ItemAndListCommonInterface) {
//                sum += ((ItemAndListCommonInterface) o).getActual();
//            }
//        }
        for (ItemAndListCommonInterface o : getListFull()) {
            sum += o.getActual();
        }
        return sum;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public long getWorkTimeSum() {
//        List<WorkSlot> list = getWorkSlotListN(false);
//        long sum = 0;
//        long now = System.currentTimeMillis();
//        for (WorkSlot workSlot : list) {
//            sum += workSlot.getDurationAdjusted(now);
//        }
//        return sum;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public long getRemainingEffortOLD() {
//        long sum = 0;
//        for (int i = 0, size = getSize(); i < size; i++) {
//            Object o = getItemAt(i);
//            if (o instanceof ItemAndListCommonInterface) {
//                if (o instanceof Item && !((Item) o).isDone()) { //replace Item  with general type
//                    sum += ((Item) o).getRemaining(); //only count not done Items
//                } else {
//                    sum += ((ItemAndListCommonInterface) o).getRemaining();
//                }
//            }
//        }
//        return sum;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Object removeFromOwner() {
//        ItemList ownerList = (ItemList) getOwner(); //only lists can own other lists
//        if (ownerList != null) {
//            ownerList.remove(this);
//            DAO.getInstance().save(ownerList);
//            return ownerList;
//        }
//        return null;
//    }
//    @Override
//    public Object insertBelow(Movable element) {
//        List list = getList();
////            if (list!=null)
//        list.add(0, element);
//        DAO.getInstance().save(this);
////            else assert false:"";
//        return list;
//    }
//    @Override
//    public Object insertIntoOwnerAtPositionOf(Movable element) {
//        Movable elementAtInsertPosition = this;
//        if (element instanceof ItemAndListCommonInterface) {
//            ItemList ownerList = (ItemList) getOwner();
//            if (ownerList != null) {
//                ownerList.addItemAtIndex((ItemAndListCommonInterface) element, ownerList.getItemIndex(elementAtInsertPosition));
//                DAO.getInstance().save(ownerList);
//                return ownerList;
//            }
//        }
//        return null;
//    }
//    public Object insertIntoOwnerAtPositionOfXXX(Movable element, Movable elementAtInsertPosition) {
//        if (element instanceof ItemAndListCommonInterface) {
//            ItemList list = (ItemList) getOwner();
//            if (list != null) {
//                list.addItemAtIndex((ItemAndListCommonInterface) element, list.getItemIndex(elementAtInsertPosition));
//                return list;
//            }
//        }
//        return null;
//    }
//</editor-fold>
    @Override
    public boolean isDone() {
        return getListStatus() == ItemStatus.DONE;
    }

    @Override
    public void setDone(boolean done) {
        if (done) {
            setStatus(ItemStatus.DONE);
        } else {
            setStatus(ItemStatus.CREATED); //TODO!!!! check that this sets the tasks in the list as either Created or Ongoing
        }
    }

    @Override
    public void save() throws ParseException {

//        if (mySaveFunction != null) {
//        if (sourceItem != null) {
////            boolean success = mySaveFunction.save();
//            sourceItem.save();
////            assert success : "problem with saving using mySaveFunction for object=" + this;
//        } else 
        {
            super.save();
        }
    }

//    @Override
//    public String getObjectId() {
////        if (sourceItem != null) {
////            return sourceItem.getObjectId();
//////            assert success : "problem with saving using mySaveFunction for object=" + this;
////        } else {
//        return super.getObjectId();
////        }
//    }
    public boolean hasSaveableData() {
//        return getText().length() > 0 || getComment().length() > 0; //UI: 
        return getText().length() > 0; //UI: lists must have names! TODO: if comment is defined, but not name, use part of comment as default name?
    }

    /**
     * call to refresh/update the WorkTimeDefinition. Should be done whenever
     */
//    public void refreshWorkTimeDefinition() {
//        workSlotListBuffer = null;
//        workTimeDefinitionBuffer = null; //consequently reset the WorkTimeDefinition
//    }
    /**
     * returns WorkTimeDefinition for this list. Fetches the workslots. Buffered
     *
     * @return
     */
//    public WorkTimeDefinition getWorkTimeAllocatorN() { //the WorkTimeDef should be created by the screen for the specific (possibly filtered/sorted) list
////        if (!hasWorkTimeDefinition()) {
//////            workTimeDefinition = new WorkTimeDefinition(this);
////            workTimeDefinition = new WorkTimeDefinition();
//////            changed(); // changed not necessary since workTimeDefinition will send back change events if it is actually changed after creation
////        }
////        return workTimeDefinition;
////        return new WorkTimeDefinition(getWorkSlotListN(), getList());
//        if (workTimeDefinitionBuffer == null) {
//            if (getSourceItemList() != null && getSourceItemList() != this) {
//                return getSourceItemList().getWorkTimeAllocatorN();
//            } else {
//                workTimeDefinitionBuffer = new WorkTimeDefinition(getWorkSlotListN(), getList());
//            }
//        }
//        return workTimeDefinitionBuffer;
//    }
    /**
     * returns the future workSlots for this list
     *
     * @param refreshWorkSlotListFromDAO
     * @return null if no workslots defined
     */
//    public List<WorkSlot> getWorkSlotListN() {
//        return getWorkSlotListN(false);
//    }
//    public List<WorkSlot> getWorkSlotListN(boolean refreshWorkSlotListFromDAO) {
//    @Override
//    public WorkSlotList getWorkSlotListN(boolean refreshWorkSlotListFromDAO) {
//        if (workSlotListBuffer == null || refreshWorkSlotListFromDAO) {
//            workSlotListBuffer = DAO.getInstance().getWorkSlotsN(this);
//        }
//        return workSlotListBuffer;
//    }
    /**
     * returns the future workSlots for this list
     *
     * @return
     */
//    public List<WorkSlot> getWorkSlotListN() {
//    @Override
//    public WorkSlotList getWorkSlotListN() {
////        return getWorkSlotListN(true);
//        return getWorkSlotListN(false);
//    }
//    public void setWorkSlotList(List<WorkSlot> workSlotList) {
//    public void setWorkSlotList(WorkSlotList workSlotList) {
//        //TODO currently not stored in ItemList but get from DAO
////        workSlotListBuffer = null;
//        workSlotListBuffer = workSlotList;
//        workTimeAllocator = null;
//    }
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
            if (Config.CHECK_OWNERS) checkOwners(workslots);
            return new WorkSlotList(this, workslots, true);
        } else {
            return null; //new WorkSlotList();
        }
    }

//    public WorkSlotList getWorkSlotListCurrent(boolean refreshWorkSlotListFromDAO) {
//        WorkSlotList workSlots = getWorkSlotListN(refreshWorkSlotListFromDAO);
//        return null;
//    }
//    @Override
//    public WorkSlotList getWorkSlotListN() {
//        if (workSlotListBuffer == null) {
//            workSlotListBuffer = DAO.getInstance().getWorkSlotsN(this);
//        }
//        return workSlotListBuffer;
//    }
//    public void setWorkSlotList(List<WorkSlot> workSlotList) {
    @Override
    public void setWorkSlotList(WorkSlotList workSlotList) {
        //TODO currently not stored in ItemList but get from DAO
//        workSlotListBuffer = null;
//        workSlotListBuffer = workSlotList;
//        workTimeAllocator = null;
        if (workSlotList != null && workSlotList.getWorkSlotListFull().size() > 0) {
//            put(PARSE_WORKSLOTS, workSlotList.getWorkSlots());
            workSlotList.setOwner(this);
            put(PARSE_WORKSLOTS, workSlotList.getWorkSlotListFull());
        } else {
            remove(PARSE_WORKSLOTS);
        }
        resetWorkTimeDefinition(); //need to reset this each time the WorkSlot list is changed
    }

    /**
     * use this to check if a WorkTimeDefinition exists *before* calling
 getWorkTimeAllocatorN() since otherwise it will create a new
 WorkTimeDefinition
     *
     * @return true if a WorkTimeDefinition is defined in this list
     */
//    boolean hasWorkTimeDefinition() {
//        return workTimeDefinition != null;
//    }
//    public WorkTimeDefinition getWorkTimeAllocatorN() {
//        return getWorkTimeAllocatorN(false);
//    }
//    @Override
//    public WorkTimeAllocator getWorkTimeAllocatorN(boolean reset) {
//        if (workTimeAllocator == null || reset) { //            wtd = new WorkTimeDefinition(itemListOrg.getWorkSlotListN(true), itemListFilteredSorted);
////            wtd = new WorkTimeDefinition(getList(), this);
//            WorkSlotList workSlots = getWorkSlotListN();
////            long now = System.currentTimeMillis(); //this is the common value of 'now' used during all the allocations
////            long now = workSlots.getNow(); //this is the common value of 'now' used during all the allocations
//            if (workSlots != null && workSlots.hasComingWorkSlots()) {
////                wtd = new WorkTimeDefinition(((<? extends ItemAndListCommonInterface>)getList(), workSlots);
////                wtd = new WorkTimeAllocator((List<ItemAndListCommonInterface>) getList(), new WorkTimeSlices(workSlots), this);
//                workTimeAllocator = new WorkTimeAllocator(new WorkTimeSlices(workSlots), this);
//            }
//        }
//        return workTimeAllocator;
//    }
//
//         public WorkTimeSlices getAllocatedWorkTimeN() { //shouldn't be necessary because an ItemList does not get workTime allocated, it can only have its own workslots
//        throw new Error("Not supported yet."); //should not be called for ItemLists and Categories (or WorkSlots)
//    }
    /**
     * forces a recalculation of workTime
     */
    @Override
    public void resetWorkTimeDefinition() {
//        workSlotListBuffer = null; //force reload of workslots from server
        workTimeAllocator = null; //recalculate worktime
        for (Object item : getListFull()) { //reset for subtasks (recursively), full to reset even for hidden subtasks!
            if (item instanceof ItemAndListCommonInterface) {
                ((ItemAndListCommonInterface) item).resetWorkTimeDefinition();
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public Date getFinishTime(ItemAndListCommonInterface item) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
//            return new Date(workTimeDef.getFinishTime(item));
//        } else {
//            return new Date(0);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Date getFinishTimeOLD(Item item) {
//        WorkTimeDefinition workTimeDef = getWorkTimeAllocatorN();
//        if (workTimeDef != null) {
//            return new Date(workTimeDef.getFinishTime(item));
//        } else {
//            return new Date(0);
//        }
//    }
//</editor-fold>
    enum sortOn {
        date, category, list
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void buildStatisticList(sortOn sortOn) {
//        ItemList sorted = new ItemList();
//        Item item;
//        Date lastDate = new Date(0);
//        Category cat = null;
//        Category itList = null;
//        ItemList current = null;
////        for (Object it : getListFull()) {
////             item = (Item) it;
//        switch (sortOn) {
//            case date:
//                //sort list on date
//                for (Object it1 : getListFull()) {
//                    item = (Item) it1;
//                    if (current == null || !MyDate.isSameDate(lastDate, item.getCompletedDateD())) {
//                        current = new ItemList(MyDate.formatDateNew(item.getCompletedDateD()));
//                        sorted.add(current);
//                    } else {
//                        current.addItem(item);
//                    }
//                }
//                break;
//            case category: //sort on Category
//                for (Object it2 : getListFull()) {
//                    item = (Item) it2;
//                    if (current == null || (!item.getCategories().isEmpty() && !item.getCategories().get(0).equals(cat))) {
//                        current = new ItemList(item.getCategories().isEmpty() ? "No category**" : item.getCategories().get(0).getText());
//                        sorted.add(current);
//                    } else {
//                        current.addItem(item);
//                    }
//                }
//                break;
//            case list: //sort on Category
//                for (Object it3 : getListFull()) {
//                    item = (Item) it3;
//                    Object owner = item.getOwner();
//                    if (current == null || (owner != null && owner instanceof ItemList && !((ItemList) owner).equals(itList))) {
//                        current = new ItemList(owner == null ? "No list/inbox" : ((ItemList) owner).getText());
//                        sorted.add(current);
//                    } else {
//                        current.addItem(item);
//                    }
//                }
//                break;
//        }
//
////        }
//    }
//</editor-fold>
    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
    }

    @Override
    public void setDeletedDate(Date dateDeleted) {
        if (dateDeleted != null && dateDeleted.getTime() != 0) {
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

    /**
    set true if Timer should restart from the list's first element in case the current one is not found. This can be set per itemList since
    it may be natural/desired to do so for some lists like Today
    @param restartTimerOnNotFound 
     */
    public void setRestartTimerOnNotFound(boolean restartTimerOnNotFound) {
        if (restartTimerOnNotFound) {
            put(PARSE_RESTART_TIMER, true);
        } else {
            remove(PARSE_RESTART_TIMER); //delete when setting to default value
        }
    }

    public boolean isRestartTimerOnNotFound() {
        Boolean restartTimerOnNotFound = getBoolean(PARSE_RESTART_TIMER);
        return restartTimerOnNotFound != null; //return null to indicate NOT deleted
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public List<ItemAndListCommonInterface> getWorkTimeProviders() {
//        List<ItemAndListCommonInterface> providers = new ArrayList();
//
//        //return own (possibly allocated) workTime - enable recursion of alloated workTime down the hierarcy of projects-subprojects-leaftasks
//        if (hasWorkTimeDefinition()) {
//            providers.add(this);
//        }
//        return providers;
//    }
//</editor-fold>
//    @Override
//    public WorkTimeSlices getAllocatedWorkTimeN(ItemAndListCommonInterface elt) {
////        return new WorkTimeSlices(getWorkSlotListN()); //a list can only get workTime allocated via its workslots
//        throw new Error("Not supported yet."); 
//    }
} // Class ItemList

