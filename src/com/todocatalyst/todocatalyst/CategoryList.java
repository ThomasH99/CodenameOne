/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import com.codename1.io.Externalizable;
import com.codename1.io.Util;
import com.parse4cn1.ParseObject;
import static com.todocatalyst.todocatalyst.ItemList.PARSE_ITEMLIST;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * stores the (manually sorted) list of categories defined by the user. Updated
 * whenever a Category is created or deleted or if the order of categories is
 * changed by the user.
 *
 * @author Thomas
 */
public class CategoryList extends ItemList {

    public static String CLASS_NAME = "CategoryList";

    final static String PARSE_CATEGORY_LIST = ItemList.PARSE_ITEMLIST; //reuse column name from ItemList to ensure any non-overwritten calls (notably getListFull()) works, was: "categoryList";
    private static CategoryList INSTANCE = null;

    /**
     * don't call this!
     */
    public CategoryList() {
        super(CLASS_NAME);
    }

    static public CategoryList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getCategoryList();
//            if (INSTANCE == null) {
//                INSTANCE = new CategoryList();
//            }
        }
        return INSTANCE;
    }

    @Override
    public List<Category> getList() {
//        return (Category) getParseObject(PARSE_CATEGORY_LIST);
        List<Category> list = getList(PARSE_CATEGORY_LIST);
        if (list != null) {
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list); //optimization: cache the list (BUT: how to keep sync'ed with parse server?!)
            return list;
        } else {
            return new ArrayList();
        }
    }

    @Override
    public List<Category> getListFull() {
        return getList();
    }

    public void setList(List categoryList) {
//        if (has(PARSE_CATEGORY_LIST) || categoryList != null) {
//            put(PARSE_CATEGORY_LIST, categoryList);
//        }
        if (categoryList != null && !categoryList.isEmpty()) {
            put(PARSE_CATEGORY_LIST, categoryList);
        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
            remove(PARSE_CATEGORY_LIST); //if setting a list to null or setting an empty list, then simply delete the field
        }
    }

    /**
     * returns an already existing category with this name, otherwise null
     *
     * @param categoryName
     * @return
     */
    Object findCategoryWithName(String categoryName) {
        if (categoryName == null || categoryName.length() == 0) {
            return null;
        }
        for (Category cat : getList()) {
            if (cat.getText().equals(categoryName)) {
                return cat;
            }
        }
        return null;
    }

    public CategoryList resetInstance() {
        CategoryList t = INSTANCE;
        INSTANCE = null; //next call to getInstance() will re-initiate/refresh the instance
        return t;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public String getObjectId() {
        return CLASS_NAME;
    }

}
