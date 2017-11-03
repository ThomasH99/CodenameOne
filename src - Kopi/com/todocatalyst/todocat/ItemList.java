package com.todocatalyst.todocat;

//package todo;
import com.codename1.io.Log;
import com.codename1.ui.events.DataChangedListener;
import com.codename1.ui.events.SelectionListener;
import com.codename1.ui.tree.TreeModel;
import com.codename1.ui.util.EventDispatcher;
import com.parse4cn1.ParseObject;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
//import java.util.List;

/**
 * ItemList implements a ListModel. It extends the ListModel with operations
 * addItemAtIndex and moveSteps. It is an extension of BaseItem so it can be
 * stored etc. Implemented inspired by DefaultListModel source code from LWUIT.
 *
 * @author Thomas
 * @param <E>
 */
public class ItemList<E extends ItemAndListCommonInterface> extends ParseObject
        implements /*ItemListModel,*/
        TreeModel, Collection, SumField, ItemAndListCommonInterface, Iterable { //, DataChangedListener {
    //optimization: lazy creation of items Vector?? (to avoid empty lists in e.g. Items taking up lots of space
    //optimization: for long lists (e.g. size>100) add a hashmap to find the index faster than linear search in getItemIndex

    final static String CLASS_NAME = "ItemList";

//    protected Vector<E> itemVector; // initialized in constructors //STORED
    protected List<E> itemList = new ArrayList(); // initialized in constructors //STORED

    final static String PARSE_TEXT = "description";
    final static String PARSE_COMMENT = "comment";

    public ItemList() {
        super(CLASS_NAME);
    }

    public ItemList(String className) {
        super(className);
    }

    public ItemList(List list) {
        this();
        itemList = new ArrayList(list);
    }

    /**
     * create a new ItemList that is a copy of source
     */
    public ItemList(ItemAndListCommonInterface source) {
        this();
        source.copyMeInto(this);
    }

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
    protected SumVector remainingEffortSumVector
            = new SumVector(
                    () -> ItemList.this.getSize(),
                    (index) -> ((Item) getItemAt(index)).getRemainingEffort());
    private int selectedIndex;// = 0;
    /**
     * contains the list of workslots (if defined, otherwise null)
     */
    private WorkTimeDefinition workTimeDefinition; //STORED inline
    private EventDispatcher dataListener; // = new EventDispatcher();
    private EventDispatcher selectionListener; // = new EventDispatcher();
    /**
     * the object that contains, or 'owns', this object. An object can have no
     * more than one owner. When the owner is deleted, so is the object.
     */
    private ItemAndListCommonInterface owner;
    /**
     * autoAddRemoveListAsListenerOnInsertedItems can be used to turn of the
     * changeListener for the (few) ItemLists where this is not desired (e.g.
     * the ItemToCategoryConsistency ItemList)
     */
    private boolean autoAddRemoveListAsListenerOnInsertedItems; // = true; //STORED
    /**
     * stores the list of baseTypes that this list accepts
     */
    private Vector baseTypes;// = null; //new Vector(); //STORED
    /**
     * if false, then items will only be added once, e.g. if an item being added
     * is already in the list, then nothing is done. Used when an ItemList is
     * used to store the items in a category where we don't want multiple
     * instances of the same item.
     */
    private boolean storeOnlySingleInstanceOfItems; //= true; //STORED??
    /**
     * if true, then inserted BaseBaseItems with no Owner (getOwner()==null)
     * will automatically get this list as owner (except Category since we don't
     * want them to own items)
     */
    private boolean makeItemListOwnerOfInsertedUnownedItems = true;
    /**
     * list of all the sources / sublists of this list, e.g. can be a mix of
     * Projects, Categories, ...
     */
    private List<ItemList<E>> sourceLists; // = new ItemList(this, false);
    /**
     * list of all lists for which this list is a sublist. Used to keep track of
     * which lists to update when this list changes. Is updated by the
     * metalists, e.g. when a list is added to a metalist, it also adds itself
     * to the metaLists of its sublist.
     */
    private List<ItemList> metaLists; // = new ItemList(this, false);
    /**
     * when the ItemList has sourcelists, itemBag counts how many times each
     * item has been added to the ItemList either directly or via sourcelists.
     * If there are no sourceLists, itemBag is null.
     */
    private Bag<E> itemBag; // = new HashtableAddedDirectly();
    final static int FIELD_DESCRIPTION = 0;
    final static int FIELD_COMMENT = 1;
    private final static FieldDef[] FIELDS = {
        //        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_BOOLEAN),
        new FieldDef(FIELD_DESCRIPTION, "Name", Expr.VALUE_FIELD_TYPE_STRING),
        new FieldDef(FIELD_COMMENT, "Comment", Expr.VALUE_FIELD_TYPE_STRING)
    };
    /**
     * list of elements that are selected in this list. WHY is it put here with
     * list??
     */
    SelectedItems selection;

    public SelectedItems getSelection() {
        return selection;
    }

    public void setSelection(SelectedItems selection) {
        this.selection = selection;
    }

    public List<E> getInternalList() {
        return itemList;
    }

    /**
     * default false: meaning that only the guid of the items held in the list
     * are written. If true, then the full items are written
     */
    boolean writeListItemsGuidOnly = true; //if true, write guid only (normal case)

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
//        this.itemVector = items; // createVector creates a new Vector
//    }
    public WorkTimeDefinition getWorkTimeDefinition() {
        if (!hasWorkTimeDefinition()) {
//            workTimeDefinition = new WorkTimeDefinition(this);
            workTimeDefinition = new WorkTimeDefinition();
//            changed(); // changed not necessary since workTimeDefinition will send back change events if it is actually changed after creation
        }
        return workTimeDefinition;
    }

    /**
     * use this to check if a WorkTimeDefinition exists *before* calling
     * getWorkTimeDefinition() since otherwise it will create a new
     * WorkTimeDefinition
     *
     * @return true if a WorkTimeDefinition is defined in this list
     */
    boolean hasWorkTimeDefinition() {
        return workTimeDefinition != null;
    }

    /**
     * returns remaining effort for the entire list
     *
     * @return
     */
    public long getRemainingEffort() {
        return getSumOfRemainingEffort(getSize());
    }

    /**
     *
     * @param subSum is the sum of the sub-tasks
     * @return
     */
    public long getFinishTime(E subtask, long subSum) {
        long newSubSum = this.getSumOfRemainingEffort(subtask) - subtask.getRemainingEffort() + subSum;
        if (workTimeDefinition != null) {
            return workTimeDefinition.getItemFinishDate(newSubSum);
        } else {
            Object owner = getOwner();
            if (owner != null) {
                if (owner instanceof Item) {
                    return ((Item) owner).getFinishTime(newSubSum);
                } else if (owner instanceof ItemList) {
                    ItemList ownerList = (ItemList) owner;
                    return ownerList.getFinishTime(this, newSubSum);
                }
            } else {
                return 0;
            }
        }
        return 0;
    }

//    public long getFinishTime(ItemAndListCommonInterface itemOrList) {
//        return getFinishTime(itemOrList, itemOrList.getRemainingEffort());
//    }
    public int size() {
        return getSize();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public boolean add(Object e) {
        addItem((E) e);
        return true;

    }

    public Object get(int index) {
        return getItemAt(index);
    }

    public Object set(int index, Object element) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    public void add(int index, Object element) {
        addItemAtIndex((E) element, index);
    }

    public Object remove(int index) {
        Object obj = getItemAt(index);
        removeItem(index);
        return obj;
    }

    public int indexOf(Object o) {
        return getItemIndex((E) o);
    }

    public int lastIndexOf(Object o) {
        return getItemIndex((E) o); //assuming there will only be a single instance of each object in the list
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

    /**
     * returns sources (may be null if not previously set)
     *
     * @return
     */
    public List<ItemList<E>> getSubLists() {
        return sourceLists;
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
        if (sourceLists == null) {
            sourceLists = new LinkedList<ItemList<E>>();
            itemBag = new Bag<E>();
            //when initiating subLists, add all items already in this list
            itemBag.addAll(itemList);
        }
        if (!sourceLists.contains(subList)) {
            if (subList.circularReferenceTo(this) == null) {
                sourceLists.add(subList);
//                itemBag.addAll(subList);
//                itemVector.addAll(subLists); //UI: add all new items to the end of the list
                for (Iterator it = subList.iterator(); it.hasNext();) {
                    E item = (E) it.next();
                    if (itemBag.getCount(item) == 0) { //only add if not already added
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
    public void removeSubList(ItemList<E> itemList) {
        if (itemList == null || sourceLists == null || !sourceLists.contains(itemList)) {
            return;
        } else {
//            itemBag.removeAll(subList);
            for (Object item : itemList) {
                if (itemBag.remove((E) item)) { //remove returns true if 
                    removeItem((E) item);
                }
            }
            sourceLists.remove(itemList);
            itemList.removeMetaList(this); //remove the reference from SourceList to this 
            if (sourceLists.size() == 0) {
                itemBag = null;
                sourceLists = null; //help GC
            }
        }
    }

    /**
     * returns true if there are sources (sublists) defined
     *
     * @return
     */
    public boolean hasSubLists() {
        return sourceLists != null && sourceLists.size() > 0;
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
    public boolean updateListWithDifferences(List newList) {
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

    public void addMetaList(ItemList metaList) {
        if (metaLists == null) {
            metaLists = new LinkedList<ItemList>();
        }
        metaLists.add(metaList);
    }

    /**
     * returns sources (may be null if not previously set)
     *
     * @return
     */
    public void removeMetaList(ItemList metaList) {
        if (metaLists == null || !metaLists.contains(metaList)) {
            return;
        } else {
            metaLists.remove(metaList);
        }
    }

    /**
     * returns the int sum of all the Items' remaining effort, up to and
     * including index. Returns 0 if list is empty, if there are no Items in the
     * list. Useful eg to sum of the total effort of elements in the list.
     *
     * @param index
     * @return
     */
    public long getSumOfRemainingEffort(int index) {
//            return getSumAt(index, Item.FIELD_EFFORT_REMAINING);
        return remainingEffortSumVector.getSumAt(index);
    }

    /**
     * returns the int sum of all the Items' remaining effort, up to and
     * including item. Returns 0 if list is empty, if there are no Items in the
     * list. Useful eg to sum of the total effort of elements in the list.
     *
     * @param index
     * @return
     */
    public long getSumOfRemainingEffort(E item) {
//        int itemIndex = getItemIndex(item);
//        if (itemIndex >= 0) {
//            return remainingEffortSumVector.getSumAt(itemIndex);
//        } else {
//            return 0;
//        }
        return getSumOfRemainingEffort(getItemIndex(item));
    }

    /**
     * returns the first item where the sum of remaining effort for previous
     * items in the list + remaining effort of this item adds up to less or
     * equal to sum. (SUM(previous items)<sum) AND (SUM(previous
     * items)+remaining effort <= sum)
     *
     * @param sum
     * @return
     */
    public E getItemAtSumOfRemainingEffort(long sum) {
//            return remainingEffortSumVector.getItemAtSum(sum);
        return getItemAt(remainingEffortSumVector.getIndexAtSum(sum));
    }

    /**
     * returns the index of the first Object that has a sum value greater than
     * or equal to sum. If no such element is found, e.g. sum is bigger than
     * total sum of all items in list, then -1 is returned.
     *
     * @param targetSum
     * @return index or -1
     */
    public int getIndexAtSumOfRemainingEffort(long sum) {
        return remainingEffortSumVector.getIndexAtSum(sum);
    }

    public boolean isExpandable() {
        return getSize() > 0;
    }

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

    void setItemList(ItemList itemList) {
        itemList.copyMeInto(this);
    }

    public boolean isStoreOnlySingleInstanceOfItems() {
        return storeOnlySingleInstanceOfItems;
    }

    public void setStoreOnlySingleInstanceOfItems(boolean storeOnlySingleInstanceOfItems) {
        this.storeOnlySingleInstanceOfItems = storeOnlySingleInstanceOfItems;
    }

//    public boolean isAutoAddRemoveListAsListenerOnInsertedItems() {
//        return autoAddRemoveListAsListenerOnInsertedItems;
//    }
//    public void setAutoAddRemoveListAsListenerOnInsertedItems(boolean autoAddRemoveListAsListenerOnInsertedItems) {
//        this.autoAddRemoveListAsListenerOnInsertedItems = autoAddRemoveListAsListenerOnInsertedItems;
//    }
    public boolean isWriteListItemsGuidOnly() {
        return writeListItemsGuidOnly;
    }

    public void setWriteListItemsGuidOnly(boolean writeListItemsGuidOnly) {
        this.writeListItemsGuidOnly = writeListItemsGuidOnly;
    }

    /**
     * returns the Vector with the content of the list
     *
     * @return
     */
    public Vector getVector() {
        return new Vector(itemList);
    }

    /**
     * set the baseTypes as accepted types of this list. Previously defined
     * baseTypes are removed.
     *
     * @param baseType
     */
    final private void setBaseTypesImpl(int[] baseTypes) {
        if (baseTypes != null) {
            this.baseTypes = new Vector(baseTypes.length, 1); //reset baseTypes
            for (int i = 0, size = baseTypes.length; i < size; i++) {
                this.baseTypes.addElement(new Integer(baseTypes[i]));
            }
        }
    }

    public void setBaseTypes(int[] baseTypes) {
        if (baseTypes != null) {
            setBaseTypesImpl(baseTypes);
//            changed(ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE);
        }
    }

    /**
     * adds the already defined list of baseTypes as accepted types of this
     * list.
     *
     * @param baseType
     */
    public void addBaseTypes(int[] baseTypes) {
        if (baseTypes != null) {
            if (this.baseTypes != null) {
                this.baseTypes = new Vector(baseTypes.length, 1);
            }
            for (int i = 0, size = baseTypes.length; i < size; i++) {
                this.baseTypes.addElement(new Integer(baseTypes[i]));
            }
//            changed(ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE);
        }
    }

    /**
     * add the baseType (as defined in BaseItemTypes) as an accepted type of
     * this list. BaseItemTypes.NULL means
     *
     * @param baseType
     */
//    public void addBaseType(int baseType) {
//        if (baseType != BaseItemTypes.NOTYPE) {
//            if (baseTypes == null) {
//                baseTypes = new Vector(1, 1);
//            }
////            ASSERT.that(baseTypes.size() == 0, "Multiple BaseTypes currently not supported for ItemList, existing type="
////                    +baseTypes!=null&&baseTypes.size()>0?BaseItemTypes.toString(((Integer)baseTypes.elementAt(0)).intValue()):"<null>"
////                    +" new type="+BaseItemTypes.toString(baseType));
//            this.baseTypes.addElement(new Integer(baseType));
//        }
//    }
    /**
     * returns the baseType with index index (in case there are several
     * baseTypes defined (not implemented yet)
     */
    private int getBaseType(int index) {
        return ((Integer) baseTypes.elementAt(index)).intValue();
    }

    /**
     * check if baseType is in this ItemList's baseTypes. Always returns true if
     * ItemList has no baseTypes defined.
     *
     * @param baseType
     * @return
     */
    public boolean isBaseType(int baseType) {
        if (isTyped()) { //baseTypes != null && baseTypes.size() != 0) {
            for (int i = 0, size = baseTypes.size(); i < size; i++) {
                if (getBaseType(i) == baseType) {
                    return true;
                }
            }
            return false;
//        return baseTypes.contains(new Integer(baseType)); //-doesn't work since each Integer is a new object (??)
        }
        return true;
    }

    /**
     * returns true if any baseTypes are defined for this list
     *
     * @return
     */
    public boolean isTyped() {
        return (baseTypes != null && baseTypes.size() > 0);
    }

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
    /**
     * @inherit
     */
    public ItemList cloneMe() {
        ItemList newCopy = new ItemList();
//        copyMeInto(newCopy);
//        super.copyMeInto(newCopy);
        // optimization: select the most efficient way of copying vectors (always using the head of lists?)
        for (int i = 0, size = getSize(); i < size; i++) {
//            itemList.addItem(((BaseItem) getItemAt(i)).clone());
            newCopy.addItem(getItemAt(i).cloneMe());
        }
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
//        super.copyMeInto(destinyItemList);
        // optimization: select the most efficient way of copying vectors (always using the head of lists?)
//        System.arraycopy(...); //TODO!!!: use arraycopy for faster copy of vector
        for (int i = 0, size = getSize(); i < size; i++) {
//            itemList.addItem(((BaseItem) getItemAt(i)).clone());
//            itemList.addItem(((BaseItem) getItemAt(i)));
            destinyItemList.addItem((/*
                     * (BaseItem)
                     */getItemAt(i))); //- no need to case to BaseItem, since we only copy references to the contained objects
        }
//        destinyItemList.setText(getText()); //-done in super.copyMeInto(destinyItemList) above
//        itemList.baseTypes=baseTypes;
//        destinyItemList.addBaseType(getBaseType());
//        destinyItemList.addBaseType(getBaseType(0));
        if (baseTypes != null) {
            destinyItemList.baseTypes = new Vector(baseTypes.size());
            for (int i = 0, size = baseTypes.size(); i < size; i++) {
                destinyItemList.baseTypes.addElement(baseTypes.elementAt(i));
            }
        }
//        destinyItemList.baseTypes = baseTypes.(getBaseType(0));
        destinyItemList.storeOnlySingleInstanceOfItems = storeOnlySingleInstanceOfItems;
//        destinyItemList.autoAddRemoveListAsListenerOnInsertedItems = autoAddRemoveListAsListenerOnInsertedItems;
//        destinyItemList.sumFieldGetter = sumFieldGetter;
        destinyItemList.writeListItemsGuidOnly = writeListItemsGuidOnly;
    }

//    final static int TOSTRING_COMMA_SEPARATED_LIST =1;
//    final static int TOSTRING_DEFAULT =2;
    @Override
    public String toString(ToStringFormat format) {
        String str = "";
        String sepStr = "";
        switch (format) {
            case TOSTRING_COMMA_SEPARATED_LIST:
                for (int i = 0; i < getSize(); i++) {
                    E baseItem = getItemAt(i);
                    if (baseItem != null) { //necessary when printing lists where change events are removing them (gives null pointerexception
//                    str += sepStr + ((BaseItem) getItemAt(i)).shortString();
                        str += sepStr + getItemAt(i).toString(ToStringFormat.TOSTRING_DEFAULT);
                        sepStr = ", ";
                    }
                }
                return str;
            default:
                return ""; //super.toString(format);
        }
    }

    public String shortString() {
//        String str = super.shortString();
        String str = "";
        str += "{" + getItemIdStr() + "}";
        str += listItemsToString(true);
        return str;
    }

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
    public ItemAndListCommonInterface getOwner() {
        return owner;
    }

    /**
     * sets owner
     *
     * @param owner
     */
    public ItemAndListCommonInterface setOwner(ItemAndListCommonInterface owner) {
        ItemAndListCommonInterface previousOwner = this.owner;
        this.owner = owner;
        return previousOwner;
    }

    /**
     * returns a string that identifies the item. chosen in the following order:
     * getText() owner.getText() getLogicalName()
     *
     * @return
     */
    public String getItemIdStr() {
        String str = "";
        if (getOwner() != null && getOwner() instanceof Item) {
            if (((Item) getOwner()).getItemListSize() != 0 && ((Item) getOwner()).getItemList() == this) {
                return "sublist:" + getOwner().getItemIdStr();
            } else if (((Item) getOwner()).getCategoriesSize() != 0 && ((Item) getOwner()).getCategories() == this) {
                return "cats:" + getOwner().getItemIdStr();
            }
        }
//        return super.getItemIdStr();
        return "";
    }
//#enddebug

    private String listItemsToString(boolean shortVersion) {
        String str = "";
        str += "[";
        String sepStr = "";
//        str += "(size:" + getSize() + ")=";
        if (itemList == null || getSize() == 0) {
            str += "<empty>";
        } else {
            for (int i = 0; i < getSize(); i++) {
//                if (getItemAt(i) instanceof E) {
                E baseItem = getItemAt(i);
                if (baseItem != null) { //necessary when printing lists where change events are removing them (gives null pointerexception
                    if (shortVersion) {
                        str += sepStr + ((E) getItemAt(i)).shortString();
                    } else {
                        str += sepStr + ((E) getItemAt(i)).shortString();
                    }
//                    if (isSelected(getItemAt(i))) {
//                        str += "[S]";
//                    }
                    sepStr = "|";
                }
//                } else {
//                    str += getItemAt(i);
//                }
            }
        }
        str += "]";
        return str;
    }

    public String fullString() {
//        String str = super.fullString();
        String str = "";
        str += listItemsToString(false);
        return str;
    }

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
    /**
     * By default an ItemList does not delete the items in its list since they
     * are only references. Only the when an ItemList is used to store 'owned'
     * items, like the sub-tasks of a task, should the sub-items be deleted.
     *
     */
    public void delete() {
//        super.delete(); //informs all listeners on this ItemList this is has been deleted so they can remove it as a listener

        deleteAllItemsInList(true); //by default, delete only items owned by this list
        itemList = null; //help garbage collector
        remainingEffortSumVector = null;
        workTimeDefinition = null; //help garbage collector
        dataListener = null; //help GC
        selectionListener = null; //help GC
    }

    /**
     * deletes() all the items in the list, even if they're not 'owned' by the
     * list. Does not delete the list itself
     */
    public void deleteAllItemsInList() {
        deleteAllItemsInList(false);
    }

    public void deleteAllItemsInList(boolean deleteOnlyItemsOwnedByList) {
        E item;
        while (getSize() > 0) { //use while since callback events may delete other items, e.g. subtasks that were also added to same category as project
            item = getItemAt(0);
            if (!deleteOnlyItemsOwnedByList
                    || item.getOwner() == this) {
                removeItem(0); //remove item first to avoid call back to this list
                item.delete(); //delete the item and remove from any lists 
            }
        }
    }

    /**
     * By default deleting an ItemList only deletes the owned items. This call
     * forces a deletion of all items in the list. Used e.g. to delete all
     * repeat instances, no matter what list owns them.
     */
    public void deleteItemListAndAllItsItems() {
//        super.delete(); //first inform all listeners on this item that it's being deleted (so they stop listening to the events from deleting the list's items)
//        uncommit(); //avoid saving list after each deletion //-uncommit is done by delete()
        deleteAllItemsInList();
        itemList = null; //help garbage collector
        workTimeDefinition = null; //help garbage collector
        dataListener = null; //help GC
        selectionListener = null; //help GC
    }

    /**
     * @inheritDoc
     */
    public int getSize() {
        return (itemList == null) ? 0 : itemList.size();
    }

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

//    protected void xenforceConsistencyBetweenItemsAndTheirCategories() {
//        super.enforceConsistencyBetweenItemsAndTheirCategories(); //currently does nothing - just called in case
//        for (int i = 0, size = getSize(); i < size; i++) {
////            ((Item) getItemAt(i)).setEnsureItemCategoryAutoConsistency(true);
////            ((BaseItem) getItemAt(i)).setEnsureItemCategoryAutoConsistency(true);
//            ((BaseItemOrList) getItemAt(i)).enforceConsistencyBetweenItemsAndTheirCategories(); //could be both Items and ItemLists (in case of lists of lists
//        }
//    }
    /**
     * Adding an item to list at given index. OK to add to a position *after*
     * the last element (at position getSize())
     *
     * @param item - the item to add
     * @param index - the index position in the list
     */
    public void addItemAtIndex(E item, int index) {
        if (hasSubLists() && itemBag.getCount(item) > 0) { //if there are sublists and item has already been added at least once (so appears in list)
            itemBag.add(item); //then don't add to list, but just add to bag to keep track of how many times added
        } else //else add normally
        //only add items if either storeOnlySingleInstanceOfItems OR if the item is not already in the list
        {
            if (!storeOnlySingleInstanceOfItems || getItemIndex(item) == -1) {
//            if (index <= getSize()) { // shouldn't make this check since it might make us miss some errors
//                itemList.insertElementAt(item, index);
                itemList.add(index, item);
//                if (selectedIndex >= index && selectedIndex < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
//                    selectedIndex++;
//                }
                int selIdx = getSelectedIndex();
                if (selIdx >= index && selIdx < getSize()) { //<getSize() to avoid that an initial 0 value for empty list remains larger than list //TODO: should initial value of selectedIndex be -1 instead of 0??
                    setSelectedIndex(selIdx + 1);
                }
                fireDataChangedEvent(DataChangedListener.ADDED, index);
            }
        }
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
        ASSERT.that((position != Settings.INSERT_BEFORE_REFERENCE_ITEM && position != Settings.INSERT_AFTER_REFERENCE_ITEM) || referencePositionItem != null, "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referencePositionItem==null, list=" + this);
        ASSERT.that((position != Settings.INSERT_BEFORE_REFERENCE_ITEM && position != Settings.INSERT_AFTER_REFERENCE_ITEM) || getItemIndex(referencePositionItem) != -1, "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referencePositionItem=" + referencePositionItem + " not in list=" + this);
//        ASSERT.that((position != INSERT_BEFORE_REFERENCE_ITEM && position != INSERT_AFTER_REFERENCE_ITEM) || (referenceIndex>=0 && referenceIndex<getSize()), "addItemAtSpecialPosition called with Insert_BEFORE/AFTER and referenceIndex="+referenceIndex+" outside bounds of list="+this);
//#enddebug
        if (position == Settings.INSERT_AT_HEAD_OF_LIST) {
            addItemAtIndex(item, 0); //UI: insert new repeatInstance at the end of the list
        } else if (position == Settings.INSERT_AT_END_OF_LIST) {
            addItem(item); //UI: insert new repeatInstance at the end of the list
        } //add new created repeatInstance just before the just finished one
        else //if (referencePositionItem!=null && (index=getItemIndex(referencePositionItem))!=-1)
         if (position == Settings.INSERT_BEFORE_REFERENCE_ITEM) {
                addItemAtIndex(item, getItemIndex(referencePositionItem)); //UI: insert new repeatInstance at position of previous
            } else if (position == Settings.INSERT_AFTER_REFERENCE_ITEM) {
                addItemAtIndex(item, getItemIndex(referencePositionItem) + 1); //UI: insert new repeatInstance at position of previous
            }
    }

    public void addItem(E item) {
        addItemAtIndex(item, getSize());
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
        if (itemList == null) {
            return;
        }
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
    /**
     * returns the (first!) index of item in the list or -1 if the item is not
     * in the list, the list is empty, or item is null.
     *
     * @param item to search for in the list
     * @return
     */
    public int getItemIndex(E item) {
        for (int i = 0, size = getSize(); i < size; i++) { //optimization!!! more efficient algorithm!!!
            //if (getItemAt(i).equals(item)) {
            if (getItemAt(i) == item) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @inheritDoc
     */
    public E getItemAt(int index) {
//        if (index < itemVector.size() && index >= 0) {
//            return itemVector.elementAt(index);
        if (index < size() && index >= 0) {
            return itemList.get(index);
        }
        return null;
    }

    /**
     * returns and removes item at position index. Returns null if no item.
     *
     * @param index
     * @return
     */
    public E getAndRemoveItemAt(int index) {
        E item = getItemAt(index);
        if (item != null) {
            removeItem(index);
        }
        return item;
    }

    /**
     * @inheritDoc
     */
    public void removeItem(int index) {

        E item = getItemAt(index);
        if (hasSubLists() && itemBag.getCount(item) > 1) { //if there are sublists, only remove item from list if it has been added one single time
            itemBag.remove(item);
        } else { //else remove normally

            int listSize = getSize();

            if (0 <= index && index < listSize) {
                // if (index < listSize && index >= 0) {
                E baseItem = getItemAt(index);
//            itemVector.removeElementAt(index);
//                super.remove(index);
                itemList.remove(index);

                if (baseItem.getOwner() == this) {
                    baseItem.setOwner(null); //if removed BaseItem has this list as parent, then reset parent to null
                }

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
     *
     * @param item to remove
     */
    public void removeItem(E item) {
        int index = getItemIndex(item);
        if (index != -1) {
            removeItem(index); //informing changelisteners etc done in removeImte(index)
        }
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
    public int getNumberOfUndoneItems(boolean includeSubTasks) {
        int countUndone = 0;
//        for (Object item : itemList) {
        for (E item : itemList) {
//            if (!((ItemAndListCommonInterface) item).isDone()) {
            if (item instanceof Item && !(((Item) item).isDone())) {
                countUndone++;
            }
            if (includeSubTasks) {
                countUndone += ((ItemAndListCommonInterface) item).getNumberOfUndoneItems();
            }
        }
        return countUndone;
    }

    public int getNumberOfUndoneItems() {
        return getNumberOfUndoneItems(false); //by default, only count direct subtasks (how many remaining subtasks *this* project has)
    }

    /**
     * set all items which are not already in state done to or itemlists in this
     * list Done
     */
    public void setAllItemsDone(boolean done, boolean recurse) {
        for (int i = 0, size = getSize(); i < size; i++) {
            Object obj = getItemAt(i);
            if (obj instanceof Item) {
                Item item = (Item) obj;
                if (item.isDone() != done) {
                    item.setDone(done);
                }
            } else if (obj instanceof ItemList && recurse) {
                ((ItemList) obj).setAllItemsDone(done, recurse);
            }
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
    public void moveItem(int fromPos, int toPos/*
             * boolean keepMovedItemSelected
     */) { //, boolean before) {

        if (fromPos == toPos || fromPos < 0 || toPos < 0 || fromPos >= getSize() || toPos >= getSize()) { //fromPos == toPos <=> no need to do anything; other exps: illegal move, ignore
            return;
        }
//        if (fromPos >= 0 || fromPos < getSize() && toPos >= 0 && toPos <= getSize()) {
//        if (fromPos >= 0 || fromPos < getSize() && toPos >= 0 && toPos <= getSize()) {

        if (fromPos < toPos) {
            // A B C - move(0,1): B A C;
//            itemList.insertElementAt(itemList.elementAt(fromPos), toPos + 1);
            itemList.add(toPos + 1, itemList.get(fromPos));
        } else {
//            itemList.insertElementAt(itemList.elementAt(fromPos), toPos);
            itemList.add(toPos, itemList.get(fromPos));
        }
//            else
//                items.insertElementAt(items.elementAt(fromPos), toPos+1);

        if (toPos < fromPos) {
//            itemList.removeElementAt(fromPos + 1);
            itemList.remove(fromPos + 1);
        } else {
//            itemList.removeElementAt(fromPos);
            itemList.remove(fromPos);
        }

        /*
         * logic to update selectedIndex: if fromPos == selectedIndex (most
         * frequent case), then selectedIndex=toPos. If both to&from are less
         * than or bigger than selectedIndex, then no need to update (the move
         * does not change the position of the at item at selectedIndex). If
         * toPos<=selectedIndex (and fromPos>selectedIndex), then selected item
         * is pushed one down the list so selectedIndex++. If
         * fromPos<selectedIndex (and toPos>selectedIndex), then
         * selectedIndex--.
         */
//            if (keepMovedItemSelected) {
        int oldSelectedIndex = selectedIndex;
        if (selectedIndex == fromPos) {
            selectedIndex = toPos;
        } else if (toPos <= selectedIndex && fromPos > selectedIndex) {
            selectedIndex++;
        } else if (fromPos < selectedIndex && toPos > selectedIndex) {
            selectedIndex--;
        } // else: toPos and fromPos are both either smaller than selectedIndex or bigger than selectedIndex, so no need to update selectedIndex
        if (oldSelectedIndex != selectedIndex) {
            fireSelectionEvent(selectedIndex, oldSelectedIndex);
        }
//            }

        int lowestChangedPos = Math.min(toPos, fromPos); //we use the lowest changed index since it may affect the rest of the list (eg calculated sums)
        fireDataChangedEvent(DataChangedListener.CHANGED, lowestChangedPos);
//        changed(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, lowestChangedPos, itemVector.elementAt(lowestChangedPos), triggerEvent));
//        }
    }

    /**
     * used by quicksort (only)
     */
    private void swap(int i, int j) {
//        Object tmp = expanded.elementAt(i);
//        E tmp = itemList.elementAt(i);
        E tmp = itemList.get(i);
//        expanded.setElementAt(expanded.elementAt(j), i);
//        itemList.setElementAt(itemList.elementAt(j), i);
        itemList.set(i, itemList.get(j));
//        expanded.setElementAt(tmp, j);
        itemList.set(j, tmp);
    }

//    private ComparatorInterface sortDef;
    private Comparator sortDef;

    /**
     * to compare WorkSlots, us: new ComparatorInterface() { public int
     * compare(Object o1, Object o2) { return
     * Item.compareLong(((WorkSlot)o1).getStart(), ((WorkSlot)o1).getStart()); }
     * });
     *
     * @param sortDef
     */
//    public void quicksort(ComparatorInterface sortDef) {
    public void quicksort(Comparator sortDef) {
        if (itemList.size() < 2 || sortDef == null) {
            return;
        }
        this.sortDef = sortDef;
        quicksort(0, itemList.size() - 1); //, sortDef);
        //optimization: set a flag whenever the list is sorted (and reset whenever an item is added in a non-sorted way)
    }

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
    private void quicksort(/*
             * Vector v,
             *//*
             * ExpandedList expanded,
             */int left, int right) {//, SortDefinition sortDef) {
        //optimization: move parameters to global variables
        int i, last;

        if (left >= right) { // do nothing if array size < 2
            return;
        }
        swap(left, (left + right) / 2);
        last = left;
        for (i = left + 1; i <= right; i++) {
//            IComparable ic = (IComparable) expanded.getObject(i); //v.elementAt(i);
//            IComparable ic = (IComparable) getItemAt(i); //v.elementAt(i);
//            IComparable icleft = (IComparable) expanded.getObject(left); //v.elementAt(left);
//            IComparable icleft = (IComparable) getItemAt(left); //v.elementAt(left);
//            int res = ss.getView().getSortDef().compare(ic, icleft);
//            int res = sortDef.compare(ic, icleft);
            int res = sortDef.compare(getItemAt(i), getItemAt(left));
//            if (ascending && ic.compareTox(icleft, sortItemSortField) < 0) {
            if (res < 0) {
                swap(++last, i);
            }
            //TODO!!: remove duplicates, e.g. if a list of categories where several categories contain the same item
            // THHJ: my quick guess at how to remove duplicates:
            // else { // equal values

        }
        swap(left, last);
//        quicksort(expanded, left, last - 1);
//        quicksort(expanded, last + 1, right);
        quicksort(left, last - 1); //, sortDef);
        quicksort(last + 1, right); //, sortDef);
    }

    /**
     * inserts item into an already sorted list and maintains it sorted (should
     * be implemented using binary but for the use case with workSlots where
     * small items are inserted into the beginning of the list the current
     * linear search should be OK)
     *
     * @param item
     */
    public void insertSortedIntoSortedList(E item, Comparator comparator) {
        for (int i = 0, size = getSize(); i < size; i++) {
//            if (comparator.compare(item, getItemAt(i))==-1) {
            if (comparator.compare(item, getItemAt(i)) <= 0) { //0 => insert new item *before* items of same value
                addItemAtIndex(item, i);
                return;
            }
        }
        addItem(item); //add to end of list if list is empty or no smaller element found
//        int size=getSize();
//        int top=size;
//        if (size==0) {
//            addItem(item);
//            return;
//        }
//        int mid = size/2;
//        if (getItemAt(mid) < item) while (false) {
//            int i=i;
//        }
    }

    public void insertSortedIntoSortedListBin(E item, Comparator comparator) {
        for (int i = 0, size = getSize(); i < size; i++) {
//            if (comparator.compare(item, getItemAt(i))==-1) {
            if (comparator.compare(item, getItemAt(i)) <= 0) { //0 => insert new item *before* items of same value
                addItemAtIndex(item, i);
                return;
            }
        }
        addItem(item); //add to end of list if list is empty or no smaller element found

        if (getSize() == 0) {
            return;
        }
        int bottom = 0;
        int top = getSize() - 1;
        int mid;
        while (top > bottom) {
            mid = (top - bottom) / 2;
            //invariant: item <=
            if (comparator.compare(item, getItemAt(mid)) <= 0) { // <=0 : item smaller than or equal to mid
                top = mid;
            } else {
                bottom = mid == 0 ? 1 : mid; //cover the case with just 2 items in array (
            }
        }
        //top==bottom
        if (comparator.compare(item, getItemAt(top)) <= 0) { // <=0 : item smaller than or equal to mid
            addItemAtIndex(item, top);
        } else {
            addItemAtIndex(item, top + 1);
        }
//        int size=getSize();
//        int top=size;
//        if (size==0) {
//            addItem(item);
//            return;
//        }
//        int mid = size/2;
//        if (getItemAt(mid) < item) while (false) {
//            int i=i;
//        }
    }

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
        return getAddedItems(itemList, newList);
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
            if (!orgList.contains(newList.get(i))) // -1 means item in new list not found in old list
            {
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
        return getAddedItems(newList, itemList);
    }

    /**
     * returns an composite state for a project based on the state of all it's
     * subtasks/subprojects Rules: - all tasks Done or Cancelled => Done; - one
     * or more tasks Ongoing => Ongoing; - if none Ongoing, then if one or more
     * tasks are Waiting => Waiting; - else: Created
     *
     * @return
     */
    protected Item.ItemStatus getListStatus() {
        Item.ItemStatus projectStatus;
        Bag<Item.ItemStatus> statusCount = new Bag<Item.ItemStatus>();
        int listSize = getSize();
        for (int i = 0, size = listSize; i < size; i++) {
            statusCount.add(getItemAt(i).getStatus());
        }
        if (statusCount.getCount(Item.ItemStatus.STATUS_CANCELLED) == listSize) {//all subtasks are cancelled
            projectStatus = Item.ItemStatus.STATUS_CANCELLED;
//        if (statusCount[BaseItemOrList.STATUS_CREATED] == listSize) {//all subtasks are created
//                projectStatus = BaseItemOrList.STATUS_CREATED; 
        } else if (statusCount.getCount(Item.ItemStatus.STATUS_DONE) + statusCount.getCount(Item.ItemStatus.STATUS_CANCELLED) == listSize) {//all subtasks are either done or cancelled
            projectStatus = Item.ItemStatus.STATUS_DONE;
        } else if (statusCount.getCount(Item.ItemStatus.STATUS_ONGOING) > 0
                || (statusCount.getCount(Item.ItemStatus.STATUS_DONE) > 0
                && statusCount.getCount(Item.ItemStatus.STATUS_CREATED) > 0
                && statusCount.getCount(Item.ItemStatus.STATUS_WAITING) == 0)) { //the whole project is ongoing if just one task is ongoing, or if there if some, but not all, tasks are completed
            projectStatus = Item.ItemStatus.STATUS_ONGOING;
        } else if (statusCount.getCount(Item.ItemStatus.STATUS_WAITING) > 0) { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
//        } else { //if there are no ongoing tasks, then just one waiting tasks makes the whole project waiting (other tasks are either Cancelled or Created
            projectStatus = Item.ItemStatus.STATUS_WAITING;
        } else { //if (statusCount[BaseItemOrList.STATUS_CREATED]>0) // if there are no ongoing and no waiting, then the only possible state for remaining subtasks is Created
            projectStatus = Item.ItemStatus.STATUS_CREATED;
        }
        return projectStatus;
    }

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
    public Item.ItemStatus getStatus() {
        return getListStatus();
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
        return (item != null && getVector().contains(item)); //
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
                Object temp;
                for (int i = 0, size = getSize(); i < size; i++) {
                    temp = getItemAt(i);
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
    @Override
    public int getRemainingTime() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Object o) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterator iterator() {
        return new Iterator() {
//            public class MyIterator <T> implements Iterator<T> {
            int index = 0;

            public boolean hasNext() {
                //implement...
                return index < getSize();
            }

            public ItemAndListCommonInterface next() {
                //implement...;
                return (ItemAndListCommonInterface) getItemAt(index++);
            }

            public void remove() {
                //implement... if supported.
                throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector getChildren(Object itemInThisList) {
//        return new Vector(getInternalList());
        if (itemInThisList == null) {
//            return new Vector(itemList); //see JavaDoc of getChildren: null should return the tree roots
            List l = getInternalList();
            if (l == null) {
                return new Vector();
            } else {
                return new Vector(getInternalList()); //see JavaDoc of getChildren: null should return the tree roots
            }
        } else {
            return new Vector(((TreeModel) itemInThisList).getChildren(null));
//            if (item instanceof Item) {
//                ItemList childrenList = ((Item) item).getItemList();
////                if (childrenList != null && childrenList.size() > 0) { // tested by isLeaf(node)
//                    return new Vector(childrenList);
////                }
//            } else if (item instanceof ItemList)
//                return new Vector(getInternalList());
//            else return null; //TODO??
        }
    }

    @Override
    public boolean isLeaf(Object itemInThisList) {
//        Vector v = getChildren(itemInThisList);
//        return v == null || v.size() == 0;
        if (itemInThisList instanceof Item) {
            ItemList subtasks = ((Item) itemInThisList).getItemList();
            return subtasks == null || subtasks.getSize() == 0;
        } else if (itemInThisList instanceof Category) {
//            Category category = ((Category) itemInThisList).getSubLists();
            List subCategories = ((Category) itemInThisList).getSubLists();
            return subCategories == null || subCategories.size() == 0;
        } else if (itemInThisList instanceof ItemList) {
            ItemList itemList = ((Item) itemInThisList).getItemList();
            return itemList == null || itemList.getSize() == 0;
        } else {
            try {
                throw new Exception();
            } catch (Exception ex) {
                Log.e(ex);
            }
            return true;
        }
//        if (node == null) {
//            return true;
//        } else if (node instanceof Item && (((Item) node).getItemList() == null || ((Item) node).getItemList().size() == 0)) {
//            return true;
//        } else {
//            return false;
//        }
    }

    @Override
    public Object[] toArray() {
        return itemList.toArray(); //TODO: need to load itemList from cloud first??
    }

    @Override
    public Object[] toArray(Object[] a) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean remove(Object o) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean removeAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean retainAll(Collection c) {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clear() {
        throw new RuntimeException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStatus(Item.ItemStatus itemStatus) {
//        ItemList subtasks = getItemList();
        for (E itemOrList : itemList) {
//            ((ItemAndListCommonInterface) itemOrList).setStatus(newStatus);
            itemOrList.setStatus(itemStatus);
        }
    }

} // Class ItemList

