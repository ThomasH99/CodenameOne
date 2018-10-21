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

/**
 * stores the (manually sorted) list of categories defined by the user. Updated
 * whenever a Category is created or deleted or if the order of categories is
 * changed by the user.
 *
 * @author Thomas
 */
public class TemplateList extends ItemList {

    public static String CLASS_NAME = "TemplateList";
    final static String PARSE_ITEMLIST_LIST = "templateList";
    private static TemplateList INSTANCE = null;

    public  TemplateList() {
        super(CLASS_NAME);
    }

    static public TemplateList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getTemplateList();
        }
        return INSTANCE;
    }

    public List<ItemList> getList() {
//        return (ItemList) getParseObject(PARSE_ITEMLIST_LIST);
        List<ItemList> list = getList(PARSE_ITEMLIST_LIST);
        if (list != null) {
           DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);            
            return list;
        } else {
            return new ArrayList();
        }
    }
    public List<ItemList> getListFull() {
        return getList();
    }

    public void setList(List itemListList) {
//        if (has(PARSE_ITEMLIST_LIST) || itemListList != null) {
//            put(PARSE_ITEMLIST_LIST, itemListList);
//        }
        if (itemListList != null && !itemListList.isEmpty()) {
            put(PARSE_ITEMLIST_LIST, itemListList);
        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
            remove(PARSE_ITEMLIST_LIST); //if setting a list to null or setting an empty list, then simply delete the field
        }
    }
    
    public TemplateList resetInstance() {
        TemplateList t= INSTANCE;
        INSTANCE=null; //next call to getInstance() will re-initiate/refresh the instance
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
