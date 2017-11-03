package com.todocatalyst.todocat;

import com.codename1.io.Log;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import static com.parse4cn1.ParseObject.fetch;
import com.parse4cn1.ParseQuery;
import java.io.DataInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package todo;
/**
 * singleton managing the list of Categories
 * @author Thomas
 */
//public class Categories extends ItemList {
public class Categories { //extends ParseObject {

//    private static String filename = "Categories";
//    private static String filename = Settings.categoriesFileName;
//    private static Categories INSTANCE;
    private static Categories INSTANCE;
    private ParseObject categories;
//    private static HashSet<Category> categories; // = new HashSet();
    private static List<Category> categoriesList; // = new HashSet();

    protected Categories() {
//        super("Categories");
//        super(true);
//        super(null, null, false, true, false, false, true);
//        super(new int[]{BaseItemTypes.CATEGORY}, null, false, true, false, false, true);
//        super(new int[]{BaseItemTypes.CATEGORY}, null, false, true, true, true, true, true);
//        super();
//        setTypeId(BaseItemTypes.CATEGORIES);
//        setTypeId(BaseItemTypes.CATEGORIES);
    }

    public static Categories getInstance() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new Categories(); //fetch("Categories", "categoryList");
                categoriesList = fetch("Categories", "categoryList");
            } catch (ParseException ex) {
                switch (ex.getCode()) {
                    case ParseException.INVALID_CLASS_NAME:
                    case ParseException.OBJECT_NOT_FOUND:
                        
                    case -1:
                    case ParseException.CONNECTION_FAILED:
                        //no existing Category list, create and save one
//                        Logger.getLogger(Categories.class.getName()).log(Level.SEVERE, null, ex);
//                        com.codename1.io.Log.p(Categories.class.getName()).log(Level.SEVERE, null, ex);
                        Log.p(Categories.class.getName()+" connection failed", Log.ERROR);
                        INSTANCE = new Categories(); //only store categories, set autoConsistency true
//                        INSTANCE.put("Categories", "categoryList");
                        break;
                    case ParseException.OBJECT_TOO_LARGE:
                    default:
                    //TODO!!!! handle network errors
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * return the list of categories
     * @return 
     */
    List<Category> getCategoryList() throws ParseException {
        ParseQuery query = ParseQuery.getQuery("Category");
        categoriesList = query.find();
        return categoriesList;
    }
    
    /**
     * returns an already existing category with this name, otherwise null
     * @param categoryName
     * @return 
     */
    Object findCategoryWithName(String categoryName) {
        for (Category cat: categoriesList) {
            if (cat.getText().equals(categoryName)) {
                return cat;
            }
        }
        return null;
    }
    
//    void addNewCategory(Category newCategory) {
    void addItem(Category newCategory) {
        categoriesList.add(newCategory);
        categories.put( "categoryList", categoriesList);
        categories.addToArrayField("categoryList", newCategory);
    }

    /**
     * reset the instance of this singleton, so that on next access, it rereads
     * from storage.
     */
//    public void set() {
//        INSTANCE = (Categories) BaseItemDAO.getInstance().getBaseItem(filename);
//    }
//
//    public void reset() {
//        INSTANCE = null;
//    }

//    /**
//     * returns a list of all defined categories with the ones in the list itemCategories set
//     * to selected
//     * @param itemCategories the list of categories to be set as selected (e.g. list coming from an item's list of categories)
//     * @return list with all categories, where the ones in itemCategories have been set to selected
//     */
//    public ItemListSelected getSelectedList(ItemListModel itemCategories) {
//        ItemListSelected selectedCategories = new ItemListSelected(INSTANCE, false);
//        selectedCategories.select(itemCategories);
////       for (int i = 0, size = itemCategories.getSize(); i<size; i++) {
////           selectedCategories.select(itemCategories.getItemAt(i));
////       }
//        return selectedCategories;
//    }
//    public void receiveChangeEvent(ChangeEvent changeEvent) {
////        super.receiveChangeEvent(changeEvent); //would calling this make any sense?
//
//        if (changeEvent.getSource() instanceof Category) {
//            Category category = (Category) changeEvent.getSource();
//            Item item = (Item) changeEvent.getListObject();
//            int itemListChangeType = changeEvent.getChangeId();
////            int index = changeEvent.getListIndex();
//
////                case ItemListChangeListener.IL_ADDED:
////                case ChangeEvent.IL_ADDED:
//            if (ChangeValue.isSet(itemListChangeType, ChangeValue.CHANGED_BASEITEM_DELETED)) {
//                item.getCategories().removeItem(category); //remove the deleted Category from the list of categories
//                return;
//            }
////                    break;
//            if (ChangeValue.isSetEither(itemListChangeType, ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, ChangeValue.CHANGED_ITEMLIST_ITEM_OTHER_CHANGE, ChangeValue.CHANGED_BASEBASEITEM_UNKNOWN_CHANGE)) {
//                //ignore these changes
//                //CHANGED_BASEBASEITEM_SOME_CHANGE means e.g. a new Category
//            }
////#mdebug
//            if (ChangeValue.isSetNeither(itemListChangeType, ChangeValue.CHANGED_BASEITEM_DELETED, ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED, ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED, ChangeValue.CHANGED_BASEBASEITEM_UNKNOWN_CHANGE)) {
//                ASSERT.that("Unexpected change event received in #item=" + this + " event=" + changeEvent);
//            }
////#enddebug
////                    break;
////                default:
//            //<editor-fold defaultstate="collapsed" desc="comment">
//            //            switch (itemListChangeType) {
//            ////                case ItemListChangeListener.IL_ADDED:
//            ////                case ChangeEvent.IL_ADDED:
//            //                case ChangeValue.CHANGED_BASEITEM_DELETED:
//            //                    item.getCategories().removeItem(category); //remove the deleted Category from the list of categories
//            //                    return;
//            ////                    break;
//            //                case ChangeValue.CHANGED_ITEMLIST_ITEM_ADDED:
//            ////                    ((ItemListCategoryConsistency) ((Item) item).getCategories()).addItemNoAutoConsistency(this); //add this Category to the item's list
//            ////                    item.getCategories().addItem(category); //add this Category to the item's list of categories
//            ////                    break;
//            //                case ChangeValue.CHANGED_ITEMLIST_ITEM_REMOVED:
//            ////                    item.getCategories().removeItem(category); //add this Category to the item's list of categories
//            //                case ChangeValue.CHANGED_ITEMLIST_ITEM_CHANGED:
//            ////                case ChangeEvent.IL_CHANGED:
//            //                    //ignore these changes
//            //                    return;
//            ////                    break;
//            ////                default:
//            //            }
//            //</editor-fold>
//        } else {
//            ASSERT.that("Unexpected ChangeEvent received in Categories: " + changeEvent);
//        }
//    }
    /**
     * returns the list of categories that are, directly or indirectly,
     * subcategories of category. They must not be selected as subcategories of
     * category since it would create circular dependencies.
     *
     * @param category
     * @return
     */
    public static Set getListOfCategoriesWithSubCategory(Category category) {
        HashSet listOfContainingLists = new HashSet();
        if (category == null) {
            return listOfContainingLists;
        }
        listOfContainingLists.add(category); //cannot choose the category itself either
        for (Category cat : categoriesList) {
                if (cat.getSubLists().contains(cat)) {
                    listOfContainingLists.add(cat);
                }else{
                    listOfContainingLists.addAll(cat.getSetOfSubCategoriesContainingThisCategoryDirectlyOrIndirectly(category));
//                    listOfContainingLists.add(cat.get);
//                    || addSourceListsContainingObj(listOfContainingLists, category, true);
                }
        }
        return listOfContainingLists;
//            return getListOfContainingItemLists(cat, true);
    }
}
