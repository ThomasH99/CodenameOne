/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import ca.weblite.codename1.json.JSONException;
import ca.weblite.codename1.json.JSONObject;
import com.codename1.io.Log;

import com.codename1.ui.events.DataChangedListener;
import com.codename1.util.StringUtil;
import com.parse4cn1.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Thomas
 */
public class Category extends ItemList { //Flatten { //implements ExpandableInterface { //Sublist {

    final static String CLASS_NAME = "Category";

//    private String description;
//    private CategoryToItemConsistency items = new CategoryToItemConsistency(this);
//    private ItemListAutosaveParent items = null; //new ItemListAutosaveParent(this);
//    private ItemListSaveInline items = new ItemListSaveInline(this); //null; //new ItemListAutosaveParent(this);
    /**
     * if true, the Category's items are stored 'inline' when the Category is
     * saved
     */
//    final static boolean storeItemListInline = false;
//    private ItemList items = new ItemList(BaseItemTypes.ITEM, this, storeItemListInline, false, false); //null; //new ItemListAutosaveParent(this);
    /**
     * stores the guid of the category's items list. Used to enable lazy
     * loading. Once loaded the items != null. NOT USED CURRENTLY!
     */
//    private long itemsGuid = 0;
    //private ItemList subCategories = new ItemList();
    /**
     * when true, changes to this Category's list of items will automatically
     * trigger that this Category is added/removed from Items's list of
     * Categories. It should be set false before an Item updates the list to
     * avoid infinite loops!
     */
    /**
     * words that when appearing in a task description automatically makes this
     * category added to it
     */
    private String autoWords;
    private static boolean initialized = false;
//    final static int CATEGORY_OFFSET = 100;
//    final static int FIELD_CATEGORY_NAME = 0 + CATEGORY_OFFSET;
////    final static int FIELD_DESCRIPTION = 0 + CATEGORY_OFFSET;
////    final static int FIELD_COMMMENT = Item.FIELD_COMMMENT;
//    final static int FIELD_COMMENT = 1 + CATEGORY_OFFSET;
//    final static int FIELD_SUBCATEGORIES = 2 + CATEGORY_OFFSET;
//    private final static FieldDef[] FIELDS = {
//        //        new FieldDef(FIELD_DESCRIPTION, "Description", Expr.VALUE_FIELD_TYPE_BOOLEAN),
//        new FieldDef(FIELD_COMMENT, "Comment", Expr.VALUE_FIELD_TYPE_STRING),
//        new FieldDef(FIELD_SUBCATEGORIES, "Subcategories", Expr.VALUE_FIELD_TYPE_STRING, "Tasks added to subcategories are automatically added to this category"),
//        new FieldDef(FIELD_CATEGORY_NAME, "Name", Expr.VALUE_FIELD_TYPE_STRING)};
//    private final static String[] FIELDNAMES = new String[FIELDS.length];
//    private final static int[] FIELDIDS = new int[FIELDS.length];
//    private final static int[] FIELDTYPES = new int[FIELDS.length];

    final static String DESCRIPTION = "Description";
    //        final static String FIELD_DONE = "Done", Expr.VALUE_FIELD_TYPE_STRING),
    final static String CATEGORY = "Category";

    public Category() {
        super(CLASS_NAME);
//        setTypeId(BaseItemTypes.CATEGORY);
        if (!initialized) {
//            for (int i = 0, size = FIELDS.length; i < size; i++) {
//                FIELDIDS[i] = FIELDS[i].id;
//                FIELDNAMES[i] = FIELDS[i].name;
//                FIELDTYPES[i] = FIELDS[i].type;
//            }
            initialized = true;
        }
    }

    /**
     * Creates new category string
     *
     * @param string name of category (used in lists etc)
     */
    public Category(String string) {
        this();
        setText(string);
//        description = string;
        //setSave(true); // always save created categories //-no, keep same interface everywhere
        //itemList = new ItemList();
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public Category(DataInputStream dis)  throws IOException {
    //        this();
    //        readObject(999, dis);
    //        setCommitted(true);
    //    }
    //</editor-fold>
    public Category(Category source) {
        this();
        source.copyMeInto(this);
        //copy(source);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns an Object containing the value of the given field (Object, since
//     * this is used in Expr for filtering)
//     *
//     * @param fieldId
//     * @return
//     */
////    public Object getFilterField(int fieldId) {
////        switch (fieldId) {  //optimization: organize this list so most frequntly used fields appear left in switch
////            case FIELD_CATEGORY_NAME:
////                return getText(); //optimization: replace by variable description directly
////            case FIELD_COMMENT:
////                return getComment();
////            case FIELD_SUBCATEGORIES:
////                return getSubLists();
////            default:
////                ASSERT.that("Category.getFilterField: Field Identifier not defined " + fieldId);
////        }
////        return null;
////    }
//
////    public void setFilterField(int fieldId, Object fieldValue) {
////        switch (fieldId) {
////            case FIELD_CATEGORY_NAME:
////                setText((String) fieldValue); //optimization: replace by variable description directly
////                break;
////            case FIELD_COMMENT:
////                setComment((String) fieldValue);
////                break;
////            case FIELD_SUBCATEGORIES:
//////                setSubLists((ItemList) fieldValue);
////                setSubLists((List) fieldValue);
////                break;
////            default:
////                ASSERT.that("Category.setFilterField: Field Identifier not defined " + fieldId);
////        }
////    }
////
////    public static FieldDef[] getFields() {
////        return FIELDS;
////    }
////
////    public static int[] getFieldIds() {
////        return FIELDIDS;
////    }
////
////    public static int[] getFieldTypes() {
////        return FIELDTYPES;
////    }
////
////    public static String[] getFieldNames() {
////        return FIELDNAMES;
////    }
////
////    public static FieldDef getFieldDef(int fieldId) {
////        for (int i = 0, size = FIELDS.length; i < size; i++) {
////            if (FIELDS[i].id == fieldId) {
////                return FIELDS[i];
////            }
////        }
////        ASSERT.that("ItemListFlatten.getFieldName(" + fieldId + ") - value not defined");
////        return null;
////    }
////
////    public static String getFieldName(int fieldId) {
////        for (int i = 0, size = FIELDS.length; i < size; i++) {
////            if (FIELDS[i].id == fieldId) {
////                return FIELDS[i].name;
////            }
////        }
////        ASSERT.that("Category.getFieldName(" + fieldId + ") - value not defined");
////        return "<undefined fieldId=" + fieldId + ">";
////    }
////
////    public static int getFieldType(int fieldId) {
////        for (int i = 0, size = FIELDS.length; i < size; i++) {
////            if (FIELDS[i].id == fieldId) {
////                return FIELDS[i].type;
////            }
////        }
////        return -1;
////    }
//</editor-fold>
    /**
     * returns the help dreadFunNames for the field, or "" if none define
     *
     * @param fieldId
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public static String getFieldHelpText(int fieldId) {
////        FieldDef[] FIELDS = getFields();
////        return FIELDS[fieldId].help != null ? FIELDS[fieldId].help : "";
//        for (int i = 0, size = FIELDS.length; i < size; i++) {
//            if (FIELDS[i].id == fieldId) {
//                return FIELDS[i].help;
//            }
//        }
//        ASSERT.that("Category.getFieldName(" + fieldId + ") - value not defined");
//        return "";
//    }
//</editor-fold>
    public String getAutoWords() {
        return autoWords;
    }

    public void setAutoWords(String autoWords) {
        if (autoWords != null && !autoWords.equals(this.autoWords)) {
            this.autoWords = autoWords;
//            changed();
        }
    }

    //just starting to play with this
    private void processAutoWords() {
        String autoWordsStr = getAutoWords();
        List<String> tokens = StringUtil.tokenize(autoWordsStr, " ,;");
        HashMap<String, List<Category>> autoWordSet = new HashMap();
        for (String word : tokens) {
            if (autoWordSet.containsKey(word)) {
                List catList = autoWordSet.get(word);
                catList.add(this); //add category for this word
            } else {
                List<Category> newCatList = new ArrayList();
                newCatList.add(this);
                autoWordSet.put(word, newCatList);
            }
        }
    }

    private Set<Category> getAutoCategories(String str) {
        HashMap<String, List<Category>> autoWordSet = new HashMap();
        HashSet<Category> newCatSet = new HashSet();
        List<String> tokens = StringUtil.tokenize(str, " ,;"); //do this *after* any regexp in QuickTasks
        List<Category> catList;
        for (String word : tokens) {
            if ((catList = autoWordSet.get(word)) != null) {
                for (Category cat : catList) {
                    newCatSet.add(cat);
                }
            }
        }
        return newCatSet;
    }

//    boolean autoUpdateItems = true;
    final static int EXPAND_ITEMS = 0; //expand the Category's subItems
    final static int EXPAND_SUBCATEGORIES = 1; // expand the Category's subCategories
//    final static int TOSTRING_DESCRIPTION = 1;

//    public boolean isExpandable() {
//        return getSize() > 0;
//    }
// <editor-fold defaultstate="collapsed" desc="comment">
    /**
     * @inherit
     */
//    public BaseItem clone() {
//        Category newCopy = new Category();
//        copyMeInto(newCopy);
//        return newCopy;
//    }
//
//    /** @inherit */
//    public void copyMeInto(BaseItem destiny) {
//        Category category = (Category) destiny;
//        super.copyMeInto(category);
////        category.setText(new String(getText())); //TODO!!: is the new necessary to create a copy of the string
////        category.setText(getText());
//    }
//    public void xxxcopy(Category source) {
//        super.xxxcopy(source);
//        setText(source.getText());
//    }
//    public void commit() {
////        Categories.getInstance().addItem(this); //Category adds itself to Categories when committed
//        items.commit();
//        super.commit();
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void commit() {
//        //normally there should never be any items associated with a new Category, not even if copied, but just in case...
//        int size = getItems().getSize();
//        if (size != 0) {
//            ItemListModel items = getItems();
//            for (int i = 0; i < size; i++) {
//                // send a receiveChangeEvent event to categori - this triggers the category to add this item to its lists of items
//                ((Item) items.getItemAt(i)).changedItem(ItemChangeListener.I_CHANGED, this);
//            }
//        }
//        setCommitted(true); // MUST be set true before the below, otherwise no consistency is ensured
//        receiveChangeEvent();
//    }
//    public Object clone() {
//        Category category = (Category) super.clone();
//        category.setText(description.toString());
//        return category;
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void delete() {
//        super.delete();
////        Categories.getInstance().removeItem(this); //Category removes itself from Categories when deleted
//
////        ItemListModel myItems = getItems();
////        for (int i = 0, size = myItems.getSize(); i < size; i++) {
////            // get the category's item's list of categories and remove this category from the item's list of categories:
////            // NOT necessary since the delete event to all the affected items will ensure that they remove the category from their lists
//////            ((Item) items.getItemAt(i)).getCategories().removeItem(this);
////            //remove all items from the category's list (enables GC) - also removes the category as listener on the items
////            myItems.removeItem(0); //(i); // remove the first item each time, removeItem(i) will fail when i increases since the list is shortened with each removal
////        }
//
//        // remove the Category from the master list of categories
////        Categories.getInstance().removeItem(this); //-NO: done automatically via deleted event??!!
//        // if subcategories are implemented, then also remove the category from all its mother categories
////        while (getItems().getSize() > 0) { //TODO: is this necessary?? //-NO, since calling delete() on the list itself will remove all elements
////            getItems().removeItem(0); //remove items to ensure that this list is removed as listener on them
////        }
//        ((ItemList) getItems()).delete(); //delete the list itself (in case it's stored separately in RMS
//        items = null; //help GC
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
    /**
     * used to return the sublist to expand in ItemListCombined.
     *
     * @param listId the Id for the list to expand
     * @ref ITEMS
     * @return returns the sublist to expand
     */
//    public ItemListModel xgetExpandList(int listId) {
//        if (listId == ITEMS) {
//            return getItems();
//        }
////        if (listId == SUBCATEGORIES) {
////            return subCategories;
////        }
//        return null;
//    }
    /**
     * extension of ItemList used to ensure consistency between items added to
     * this Category and the items' lists of categories.
     */
//    public class ItemListCategoryConsistency extends ItemList {
//
//        public void addItemAtIndex(Object item, int index, boolean consistency) {
//            if (super.getItemIndex(item) == -1) // only add items to categories ONCE
//            {
//                super.addItemAtIndex(item, index); //UI: add new items to beginning of list
//            }
//            if (consistency) {
//                //((Item)item).addCategory(Category.this, false);
//                ConsistencyServer.getInstance().categoryAddedItem(Category.this, (Item) item);
//            }
//        //update();
//        }
//
//        public void addItemAtIndex(Object item, int index) {
//            addItemAtIndex(item, index, consistency);
//        }
//
//        /**
//         * items are only added ONCE (if an item is already in the list when added it is ignored)
//         */
//        public void addItem(Object item) {
//            addItem(item, consistency);
//        }
//
//        public void addItem(Object item, boolean consistency) {
//            addItemAtIndex(item, 0, consistency); //UI: add new items to beginning of list
//        }
//
//        public void removeItem(int index) {
//            removeItem(index, consistency);
//        }
//
//        public void removeItem(int index, boolean consistency) {
//            if (consistency) {
//                //((Item)getItemList().getItemAt(index)).removeCategory(Category.this, false);
//                ConsistencyServer.getInstance().categoryRemovedItem(Category.this, (Item) this.getItemAt(index));
//            }
//            super.removeItem(index);
//        //update();
//        }
//
//        public void setItem(int index, Object item) {
//            super.setItem(index, item);
//        //TODO: should anything more be done when an item has been updated??!
//        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public int hashCode() {
//        int hash = 7;
//        hash = 79 * hash + (this.getText() != null ? this.getText().hashCode() : 0);
//        return hash;
//    }
//
//    public boolean equals(Object o) {
//        if (o == null) {
//            return false;
//        }
//
//        if (o == this) {
//            return true;
//        }
//
//        if (this.getClass() != o.getClass()) {
//            return false;
//        }
//
//        Category category = (Category) o;
//        if (!this.getText().equals(category.getText())) {
//            return false;
//        }
//
//        if (!this.getItems().equals(o)) {
//            return false;
//        }
//
//        if (super.equals(o)) {
//            return true;
//        }
//
//        return true;
//    }
//    public void writeObject(DataOutputStream dos) {
//        try {
//            //dos =
//            super.writeObject(dos); //use when overwriting this method in specialized classes
//            //TODO: if (settings.version == xxx)
////            dos.writeUTF(description);
////            items.writeObject(dos);
//            ItemList.saveListInlineOrAsGuid(dos, items, storeItemListInline);
////            if (items != null) {
////                if (items.isStoreInline()) {
////                    items.writeObject(dos);
////                } else {
////                    //nothing: the referenced objects stores themselves when changes are made (and their mother objects are committed)
////                }
////            } else {
////                dos.writeLong(0); //0 means 'null' guid
////            }
////            items.writeObjectToDosOrRms(dos);
////            if (items != null) { // LAZY implementation:
////                dos.writeLong(items.getGuid()); //NB. Assumes that items is not lazyly created (must never be null)
////            } else {
////                dos.writeLong(0);
////            }
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
////return dos;
//
//    }
//    public void readObject(DataInputStream dis) {
//        try {
//            //TODO: if (settings.version == xxx)
//            //dis =
//            super.readObject(dis); //use when overwriting this method in specialized classes
////            description = dis.readUTF();
//            //subItemList = new ItemListCategoryConsistency(dis);
////            items = new CategoryToItemConsistency(this);
////            items = new ItemListAutosaveParent(this);
////            items.readObject(dis);
////            items = ItemList.readListInlineOrAsGuid(dis, this, storeItemListInline);
//            setItems(ItemList.readListInlineOrAsGuid(dis, this, storeItemListInline));
////            ItemList.readListInlineOrAsGuid(dis, items);
////            long itemsGuid = dis.readLong();
////            if (itemsGuid != 0) {
////                items = (ItemList) BaseItemDAO.getInstance().getBaseItem(itemsGuid);
////            } else {
////                //do nothing, since items is initialized to null // items = null;
////            }
////            items = new ItemListSaveInline(this);
////            items.readObject(dis);
//
//            //            itemsGuid = dis.readLong(); // LAZY version
//            //itemList = new ItemList(dis);
//            //itemList = (ItemList) (BaseItemDAO.getBaseItem(itemListGuid));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
////return dis;
//    }
//    public String toString() {
//        return getText();
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public String toString(int format) {
////        String str = "";
////        String sepStr="";
//        switch (format) {
//            case BaseItem.TOSTRING_DEFAULT:
////            case TOSTRING_DESCRIPTION:
//                return getText();
//            default:
//                return super.toString(format);
//        }
//    }
//    public String shortString() {
////        return typeString() + " \"" + getText() + "\"";
//        return super.shortString();
//    }
//
//    public String fullString() {
//        String s = super.fullString();
//</editor-fold>
//        //<editor-fold defaultstate="collapsed" desc="comment">
//        //        String s = shortString();
//        //        String s = "";
//        //        s += typeString();
//        //        s += " \"" + getText() + "\"";
//        //        s += "\" itemsGui=";
//        //        s += (getItems() != null ? "|itemsGui=" + getItems().getGuid() : "|items=null");
//        //        s += "|autoUpd=" + (autoUpdateItems ? "T" : "F");
//        //        s += super.fullString();
//
//        //        ItemList subItems = getItemList();
//        //        if (subItems != null && subItems.getSize() > 0) {
//        //            s = s + "- subitems = [";
//        //            for (int i = 0; i < subItems.getSize(); i++) {
//        //                s = s + subItems.getItemAt(i).toString() + "; ";
//        //            }
//        //            s = s + "], ";
//        //        }
//        //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        return s;
//    }
//    public void addItemAtIndexNoAutoConsistency(Object item, int index) {
//        super.addItemAtIndex(item, index);
//    }
//
//</editor-fold>
//    //<editor-fold defaultstate="collapsed" desc="comment">
//    //    public void xaddItemAtIndex(Object item, int index) {
//    ////        if (item instanceof Item) { //- //-should always ONLY be Items added here, so don't check otherwise problems might not be caught
//    ////        if (getEnsureItemCategoryAutoConsistency()) { //- no need to check since whenever an Item is added to a Cateogyr, the Category will also be added to the Item (it's only the other way we may choose to not maintain consistency
//    ////#mdebug
//    //        if (((Item) item).getCategories().getItemIndex(this) != -1) //only print below message when auto-update will really be done by below call to addItem(this)
//    //        {
//    //            Log.l("  autoUpdating in Category.addItemAtIndex: ADDING " + this + " TO " + item + "(in Category.addItemAtIndex(), at index=" + index);
//    //        }
//    ////#enddebug
//    //        ((ItemListCategoryConsistency) ((Item) item).getCategories()).addItemNoAutoConsistency(this); //add this Category to the item's list
//    ////        }
//    //        super.addItemAtIndex(item, index);
//    //    }
//    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void addItemNoAutoConsistency(Object item) {
//        super.addItem(item);
//    }
//
//</editor-fold>
//    //<editor-fold defaultstate="collapsed" desc="comment">
//    //    public void xaddItem(Object item) {
//    ////        if (item instanceof Item) { //-should always ONLY be categories added here, so don't check otherwise problems might not be caught
//    ////#mdebug
//    //        if (((Item) item).getCategoriesSize() > 0 && ((Item) item).getCategories().getItemIndex(this) != -1) //only print below message when auto-update will really be done by below call to addItem(this)
//    //        {
//    //            Log.l("  autoUpdating in Category.addItem: ADDING " + this + " TO " + item + "(in Category.addItem()");
//    //        }
//    ////#enddebug
//    //        ((ItemListCategoryConsistency) ((Item) item).getCategories()).addItemNoAutoConsistency(this); //add this Category to the item's list
//    ////        }
//    //        super.addItem(item);
//    //    }
//    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void removeItemNoAutoConsistency(int index) {
//        super.removeItem(index);
//    }
//
//    public void removeItemNoAutoConsistency(Object item) {
//        super.removeItem(super.getItemIndex(item));
//    }
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
//        ASSERT.that(changeEvent.getSource() instanceof Item || changeEvent.getSource() instanceof ItemList, "Category.receiveChangeEvent: change event from object other than Item/ItemList! ChangeEvent=" + changeEvent);
//        if ((changeEvent.getSource() instanceof Item)) {
//            Object changedItem = changeEvent.getSource();
//            if (ChangeValue.isSet(changeEvent.getChangeId(), ChangeValue.CHANGED_BASEITEM_DELETED)) {
//                removeItem(changedItem); //remove the deleted item from the category
//            } else {
//                if (derivedSumVector != null) {// && changeId != ChangeValue.CHANGED_NO_CHANGE /** && changedItem instanceof BaseItem*/) {
//                    int index = getItemIndex(changedItem);
//                    derivedSumVector.invalidate(index);
//                    fireChangeEvent(new ChangeEvent(this, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, index, changedItem, changeEvent));
////                    fireDataChangedEvent(DataChangedListener.CHANGED, index);
//                    fireDataChangedEvent(DataChangedListener.CHANGED, index);
//                }
//            }
//        } else { //source is ItemList
//            super.receiveChangeEvent(changeEvent); //let ItemListFlatten process it
//        }
//    }
//</editor-fold>
    public Set getSetOfSubCategoriesContainingThisCategoryDirectlyOrIndirectly(Object category) {
        HashSet listOfContainingLists = new HashSet();
//        if (cat == null) {
//            return listOfContainingLists;
//        }
//        listOfContainingLists.add(category); //cannot choose the category itself either
//        for (Object cat : itemList) {
        for (Object cat : getList()) {
            listOfContainingLists.addAll((((Category) cat).getSetOfSubCategoriesContainingThisCategoryDirectlyOrIndirectly(category)));
        }
        return listOfContainingLists;
    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public void xremoveItem(int index) {
    ////        if (getItemAt(index) instanceof Item) { //-should always ONLY be categories added here, so don't check otherwise problems might not be caught
    ////#mdebug
    //        Log.l("  autoUpdating in Category.removeItem: REMOVING " + this + " from " + getItemAt(index) + "(in Category.removeItem(), index=" + index);
    ////#enddebug
    ////            ((ItemListCategoryConsistency)((Item) getItemAt(index)).getCategories()).removeItemNoAutoConsistency(this); //remove this Category from the item's list
    //        ((ItemListCategoryConsistency) ((Item) getItemAt(index)).getCategories()).removeItemNoAutoConsistency(this); //remove this Category from the item's list
    ////        }
    //        super.removeItem(index);
    //    }
    //</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public String getText() {
//        return description;
//    }
//
//    public void setText(String description) {
//        if (!this.description.equals(description)) {
//            this.description = description;
//            changed();
//        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    /** returns the number of sub-tasks not done (or 0 if no sub-tasks) */
//-not necessary here since Category is an ItemList which already supports this function
//    public int getNumberOfUndoneItems() {
//        int countUndone = 0;
//            ItemList itemList = getItems();
//            for (int i = 0, size = itemList.getSize(); i < size; i++) {
//                if (itemList.getItemAt(i) instanceof Item) { // && !((Item)itemList.getItemAt(i)).isDone()) {
//                    countUndone += ((Item) itemList.getItemAt(i)).getNumberOfUndoneItems();
//                } else if (itemList.getItemAt(i) instanceof Category) { // && !((Item)itemList.getItemAt(i)).isDone()) {
//            }
//        }
//        return countUndone;
//    }// </editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    //    public ItemList getItems() {
    ////        return items;
    ////        if (items == null && itemsGuid != 0) { // LAZY version:
    ////            items = (ItemList) BaseItemDAO.getInstance().getBaseItem(itemsGuid);
    ////        }
    //
    ////        if (items == null) {
    ////            items = new ItemListSaveInline(this);
    ////        }
    ////        return items;
    //        return this;
    //    }
    //    public void updateAndEnsureConsistencyJustBeforeSaving() {
    //            **
    //    }
    //</editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
//        triggerEvent = changeEvent;
//
//            switch (changeEvent.getChangeType()) {
//                case ChangeEvent.IL_CHANGED:
//                case ChangeEvent.I_CHANGED:
////                    fireChangeEvent(new ChangeEvent(this, ChangeEvent.I_CHANGED), changeEvent); // this Category has now receiveChangeEvent
//                    fireChangeEvent(new ChangeEvent(this, ChangeEvent.IL_CHANGED, -1, changeEvent.getSource(), changeEvent)); // this Category has now receiveChangeEvent
//                    break;
//                case ChangeEvent.I_DELETED:
//                    removeItem(changeEvent.getSource());
//                    break;
//                default:
//                    Log.l("ERROR: Category.changeItemList called with unhandled changeType, changeEvent=" + changeEvent);
//            }
//        triggerEvent = null;
////        super.receiveChangeEvent(changeEvent);
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void xreceiveChangeEvent(ChangeEvent changeEvent) {
//        triggerEvent = changeEvent;
//
//        int itemListChangeType = changeEvent.getChangeType();
//        ItemList list = (ItemList) changeEvent.getSource();
//        int index = changeEvent.getListIndex();
//        BaseItem changedItem = (BaseItem) changeEvent.getListObject();
//
////        if (list == getItems() && changedItem instanceof Item) {
//        if (list == getItems() && changedItem instanceof Item) {
//            Item item = (Item) changedItem;
//            switch (itemListChangeType) {
////                case ItemListChangeListener.IL_ADDED:
//                case ChangeEvent.IL_ADDED:
//                    //optimization: below test () shouldn't be necessary
//                    //an item has been added to this Category
////                    if (item.getCategories().getItemIndex(this) == -1) { //- this test not necessary since we'll always add the category to the item's list (check against multiple copies done when adding)
//                    //I'm NOT in item's list of categories
////                        if (item.getEnsureItemCategoryAutoConsistency()) { //-not necessary since we'll never have the case that an item is added to a Category without us wanting the category to be added to the item as well
//                    Log.l("  autoUpdating in Category: ADDING " + this + " to " + item + "(in Category.changedItemList()");
//                    item.getCategories().addItem(this); //add this Category to the item's list
////                    fireChangedItemEvent(ItemChangeListener.I_CHANGED); // this Category has now receiveChangeEvent
//                    fireChangeEvent(new ChangeEvent(this, ChangeEvent.I_CHANGED)); // this Category has now receiveChangeEvent
////                    }
////                    } else {
//                    //I'm NOT in item's list of categories
//                    // do other processing related to changes to list of items
////#mdebug
////                        Log.l("----->> [1] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
////                    }
//                    break;
////                case ItemListChangeListener.IL_REMOVED:
//                case ChangeEvent.IL_REMOVED:
//                    Log.l("  autoUpdating in Category: REMOVING " + this + " from " + item + "(in Category.changedItemList()");
//                    item.getCategories().removeItem(this); //remove this Category from the item's list
////                    fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                    fireChangeEvent(new ChangeEvent(this, ChangeEvent.I_CHANGED));
//                    break;
////                case ItemListChangeListener.IL_CHANGED:
//                case ChangeEvent.IL_CHANGED:
//                    // an item in this cateogry's list has receiveChangeEvent in some way, doesn't influence the category so no cheange event
//                    break;
//                default:
//                    Log.l("ERROR: Category.changeItemList called with unhandled changeType, changeType=" + itemListChangeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
//            }
//        } else {
////#mdebug
//            Log.l("----->> [3] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + itemListChangeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
//        }
//        triggerEvent = null;
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void xchangedItemList(int itemListChangeType, ItemListModel list, int index, BaseItem changedItem) {
//        if (list == getItems() && changedItem instanceof Item) {
//            Item item = (Item) changedItem;
//            switch (itemListChangeType) {
//                case ItemListChangeListener.IL_ADDED:
//                    //optimization: below test () shouldn't be necessary
//                    //an item has been added to this Category
////                    if (item.getCategories().getItemIndex(this) == -1) { //- this test not necessary since we'll always add the category to the item's list (check against multiple copies done when adding)
//                    //I'm NOT in item's list of categories
////                        if (item.getEnsureItemCategoryAutoConsistency()) { //-not necessary since we'll never have the case that an item is added to a Category without us wanting the category to be added to the item as well
//                    Log.l("  autoUpdating in Category: ADDING " + this + " to " + item + "(in Category.changedItemList()");
//                    item.getCategories().addItem(this); //add this Category to the item's list
//                    fireChangedItemEvent(ItemChangeListener.I_CHANGED); // this Category has now receiveChangeEvent
////                    }
////                    } else {
//                    //I'm NOT in item's list of categories
//                    // do other processing related to changes to list of items
////#mdebug
////                        Log.l("----->> [1] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
////                    }
//                    break;
//                case ItemListChangeListener.IL_REMOVED:
//                    Log.l("  autoUpdating in Category: REMOVING " + this + " from " + item + "(in Category.changedItemList()");
//                    item.getCategories().removeItem(this); //remove this Category from the item's list
//                    fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                    break;
//                case ItemListChangeListener.IL_CHANGED:
//                    // an item in this cateogry's list has receiveChangeEvent in some way, doesn't influence the category so no cheange event
//                    break;
//                default:
//                    Log.l("ERROR: Category.changeItemList called with unhandled changeType, changeType=" + itemListChangeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
//            }
//        } else {
////#mdebug
//            Log.l("----->> [3] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + itemListChangeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
//        }
//    }// </editor-fold>
// <editor-fold defaultstate="collapsed" desc="comment">
//    public void xchangedItemList(int changeType, ItemListModel list, int index, BaseItem changedItem) {
//        if (list == getItems() && changedItem instanceof Item) {
//            Item item = (Item) changedItem;
//            switch (changeType) {
//                case ItemListChangeListener.IL_ADDED:
//                    //optimization: below test () shouldn't be necessary
//                    if (item.getCategories().getItemIndex(this) != -1) {
//                        //I'm in item's list of categories
////                        getItems().addItem(item); // add the item to my list //-already done that's the reason for the callback - duh ;-) !!
////                        fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                        item.getCategories().changedItem(ItemChangeListener.I_CHANGED, this);
////                        fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                    } else {
//                        //I'm NOT in item's list of categories
//                        // do other processing related to changes to list of items
////#mdebug
//                        Log.l("----->> [1] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
//                    }
//                    break;
//                case ItemListChangeListener.IL_REMOVED:
//                    fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                    break;
//                case ItemListChangeListener.IL_CHANGED:
//                    if (index != -1) {
//                        // item IS in my list of items
//                        if (item.getCategories().getItemIndex(this) != -1) {
//                            // I'm also in item's list
//                            //other changes to items in my list, e.g. that some other category was added
//                            //normally no processing done here...
//                            //do other processing related to changes to list of items
////                            Log.l("----->> [1] Unexpected call to changedItemList in Category, changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
//                            fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                        } else {
//                            // I'm NOT in item's list
//                            // meaning the CHANGED event is because the item has removed me from its list
//                            getItems().removeItem(index); // remove the item from my list
//                            fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                        }
//                    } else { // index == -1
//                        // item NOT in my list
//                        //called when an Item has added this cateogry to its list of categories
//                        if (item.getCategories().getItemIndex(this) != -1) {
//                            //I'm in item's list
//                            if (item.addToCategory()) {
//                            }
//                            getItems().addItem(item); // add the item to my list
//                            fireChangedItemEvent(ItemChangeListener.I_CHANGED);
//                        } else {
//                            //I'm NOT in item's list
//                            // do other processing related to changes to list of items
////#mdebug
//                            Log.l("----->> [2] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
//                        }
//                    }
//            }
//        } else {
////#mdebug
//            Log.l("----->> [3] Category: Unexpected call to changedItemList=" + getText() + ", changeType=" + changeType + " list=" + list + " index=" + index + " changedItem=" + changedItem);
////#enddebug
//        }
//    }// </editor-fold>

//    @Override
//    public String toString() {
//        return getText();
//    }
    @Override
    public void delete() throws ParseException { //throws ParseException {
//<editor-fold defaultstate="collapsed" desc="comment">
//        super.delete(); //informs all listeners on this ItemList this is has been deleted so they can remove it as a listener

//        for (Item item : (List<Item>) getItemList()) {
//            try {
//                item.fetchIfNeeded();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//            item.getCategories().remove(this); //remove references to this item from the category before deleting it
//            item.save();
//        }
//</editor-fold>
        DAO.getInstance().deleteCategoryFromAllItems(this);

        //remove category from meta-categories
        List<Category> listOfCategories = DAO.getInstance().getAllCategoriesIncludingThis(this);
        for (Category category : listOfCategories) {
            //retrieve the sourceList, remove this list from it, and store it again
//            itemList.put(ItemList.PARSE_SOURCE_LISTS, ((ArrayList) itemList.getList(ItemList.PARSE_SOURCE_LISTS)).remove(this));
            category.removeSubList(this); //remove this category as a subCategory (as well as any items added to the meta-lists)
//<editor-fold defaultstate="collapsed" desc="comment">
//            try {
//                category.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//</editor-fold>
            DAO.getInstance().save(category);
        }

        CategoryList categoriesList = DAO.getInstance().getCategoryList();
        categoriesList.remove(this);
        DAO.getInstance().save(categoriesList);

//        super.delete();
        put(Item.PARSE_DELETED_DATE, new Date());
        DAO.getInstance().save(this);
//<editor-fold defaultstate="collapsed" desc="comment">
//        deleteAllItemsInList(true); //by default, delete only items owned by this list
//        itemList = null; //help garbage collector
//        remainingEffortSumVector = null;
//        workTimeDefinition = null; //help garbage collector
//        dataListener = null; //help GC
//        selectionListener = null; //help GC
//</editor-fold>
    }

    public void addItemToCategory(Item item, boolean addCategoryToItem) {
        addItemToCategory(item, size(), addCategoryToItem); //add to end of category list by default
    }

    /**
     * add item to this category
     *
     * @param item
     * @param index position at which to insert the item (eg for drag & drop in
     * category list)
     * @param addCategoryToItem if true, also add the category to the item
     */
    public void addItemToCategory(Item item, int index, boolean addCategoryToItem) {
        if (item != null) {
//            addItem(item);
            addItemAtIndex(item, index);
            if (addCategoryToItem) {
                item.addCategoryToItem(this, false);
            }
        }
    }

    public void removeItemFromCategory(Item item, boolean removeCategoryFromItem) {
        if (item != null) {
            removeItem(item);
            if (removeCategoryFromItem) {
                item.removeCategoryFromItem(this, false);
            }
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

    public String toString() {
        return getText() + " [" + getList().size() + "]";
    }

    /**
     * checks if the owner (recursively) is in the list in a position *before*
     * item. Used to check for recursive dependencies in work time allocation
     * for categories. E.g. cat= [proj, ..., subtask] where subtask is a subtask
     * of proj (directly or recursively). Here, to know how much time proj needs
     * to allocate time to subtask, we need to know how much time subtask
     * requires from cat, which can only be determined once we know how is left
     * in cat after proj has gotten what it needs from cat, which requires proj
     * to know how much subtask requires from proj!!
     *
     * @param list
     * @param itemIndex
     * @return
     */
    static public boolean isOwnerOfItemInListBeforeItem(List<ItemAndListCommonInterface> list, int itemIndex, ItemAndListCommonInterface item) {
//        ItemAndListCommonInterface owner = list.get(itemIndex).getOwner();
        ItemAndListCommonInterface owner = item.getOwner();
        if (owner != null && itemIndex > 0) {
//            int itemIndex = list.indexOf(itemIndex);
            List<ItemAndListCommonInterface> catSubList = list.subList(0, itemIndex - 1); //get the part of the list that is *before* the position of item
            return catSubList.indexOf(owner) != -1 || isOwnerOfItemInListBeforeItem(catSubList, owner);
        }
        return false;
    }

    static public boolean isOwnerOfItemInListBeforeItem(List<ItemAndListCommonInterface> list, int itemIndex) {
        if (itemIndex < 0 || itemIndex >= list.size()) {
            return false;
        }
        return isOwnerOfItemInListBeforeItem(list, itemIndex, list.get(itemIndex));
    }

    static public boolean isOwnerOfItemInListBeforeItem(List<ItemAndListCommonInterface> list, ItemAndListCommonInterface item) {
        return isOwnerOfItemInListBeforeItem(list, list.indexOf(item));
    }

    public boolean isOwnerOfItemInCategoryBeforeItem(ItemAndListCommonInterface item) {
        return isOwnerOfItemInListBeforeItem(getList(), item);
    }

    public boolean isOwnerOfItemInCategoryBeforeItem(int itemIndex) {
        return isOwnerOfItemInListBeforeItem(getList(), itemIndex);
    }

} // end Category

