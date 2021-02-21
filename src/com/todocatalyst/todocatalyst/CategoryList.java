/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.parse4cn1.ParseObject;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.todocatalyst.todocatalyst.ItemList.PARSE_ITEMS;

/**
 * stores the (manually sorted) list of categories defined by the user. Updated
 * whenever a Category is created or deleted or if the order of categories is
 * changed by the user.
 *
 * @author Thomas
 */
public class CategoryList extends ItemList {

    public static String CLASS_NAME = "CategoryList";

    final static String PARSE_CATEGORY_LIST = ItemList.PARSE_ITEMS; //reuse column name from ItemList to ensure any non-overwritten calls (notably getListFull()) works, was: "categoryList";
    private static CategoryList INSTANCE = null;

    /**
     * don't call this!
     */
    public CategoryList() {
        super(CLASS_NAME);
        if (Config.TEST) setText("CategoryList");
    }

    static synchronized public CategoryList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getCategoryList();
//            if (INSTANCE == null) {
//                INSTANCE = new CategoryList();
//            }
        }
        return INSTANCE;
    }

//    @Override
//    public List<Category> getList() {
////        return (Category) getParseObject(PARSE_CATEGORY_LIST);
//        List<Category> list = getList(PARSE_CATEGORY_LIST);
//        if (list != null) {
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list); //optimization: cache the list (BUT: how to keep sync'ed with parse server?!)
//            return list;
//        } else {
//            return new ArrayList();
//        }
//    }
    public List<Category> getList() {
        return super.getList();
    }

    @Override
    public List<Category> getListFull() {
//        return getList();
//        List<Category> list = getList(PARSE_CATEGORY_LIST);
//        if (list != null) {
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list); //optimization: cache the list (BUT: how to keep sync'ed with parse server?!)
//            return list;
//        } else {
//            return new ArrayList();
//        }
        return super.getListFull();
    }

    @Override
    public void setList(List categoryList) {
//        if (has(PARSE_CATEGORY_LIST) || categoryList != null) {
//            put(PARSE_CATEGORY_LIST, categoryList);
//        }
//        if (categoryList != null && !categoryList.isEmpty()) {
//            put(PARSE_CATEGORY_LIST, categoryList);
//        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
//            remove(PARSE_CATEGORY_LIST); //if setting a list to null or setting an empty list, then simply delete the field
//        }
        super.setList(categoryList);
    }

    /**
     * returns an already existing category with this name, otherwise null
     *
     * @param categoryName
     * @return
     */
    Category findCategoryWithName(String categoryName, boolean ignoreCase) {
        if (categoryName == null || categoryName.length() == 0) {
            return null;
        }
        String search=categoryName;
        if (ignoreCase)
            search=search.toLowerCase();
        for (Category cat : getListFull()) {
            if (cat.getText().toLowerCase().equals(categoryName.toLowerCase())) {
                return cat;
            }
        }
        return null;
    }

//    public synchronized boolean reloadFromParseXXX(boolean forceLoadFromParse, Date startDate, Date endDate) {
////        CategoryList t = INSTANCE;
////        INSTANCE = null; //next call to getInstance() will re-initiate/refresh the instance
////        return t;
////        INSTANCE.add(temp);
////        INSTANCE.setList(temp.getList()); //NO good because shortcircuts the addItem logic (bags etc)
//        CategoryList temp = DAO.getInstance().getCategoryList();//forceLoadFromParse); //, startDate, endDate);
//        if (temp != null) {
//            CategoryList current = getInstance();
////            INSTANCE.clear(); //this is to avoid that an already cached instance get recreated (like the above code did)
//            current.clear(); //this is to avoid that an already cached instance get recreated (like the above code did)
//            for (ItemAndListCommonInterface elt : temp.getList()) {
////                INSTANCE.addItem(elt);
//                current.addItem(elt);
//            }
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(this); //
//            return true;
//        }
//        return false;
//    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
    }

}
