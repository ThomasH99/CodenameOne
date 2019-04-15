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
 * stores the (manually sorted) list of categories defined by the user.Updated
 whenever a Category is created or deleted or if the order of categories is
 changed by the user.
 *
 * @author Thomas
 * @param <E>
 */
//public class TemplateList<E extends ItemAndListCommonInterface> extends ItemList {
public class TemplateList<T> extends ItemList {

    public static String CLASS_NAME = "TemplateList";
//    final static String PARSE_ITEMLIST_LIST = PARSE_ITEMLIST; //"templateList";
    private static TemplateList INSTANCE = null;

    public TemplateList() {
        super(CLASS_NAME);
    }

    static synchronized public TemplateList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getTemplateList();
        }
        return INSTANCE;
    }

//    @Override
//    public List<Item> getList() {
////        return (ItemList) getParseObject(PARSE_ITEMLIST_LIST);
//        List<Item> list = getList(PARSE_ITEMLIST_LIST);
//        if (list != null) {
//           DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);            
//            return list;
//        } else {
//            return new ArrayList();
//        }
//    }
//    @Override
//    public List<Item> getListFull() {
////        return getList();
////        List<Item> list = getList(PARSE_ITEMLIST_LIST);
////        if (list != null) {
////            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
////            return list;
////        } else {
////            return new ArrayList();
////        }
//        super.getListFull();
//    }
//    @Override
//    public List<Item> getList() {
//        return super.getList();
//    }
//    @Override
//    public void setList(List templatesList) {
////        if (has(PARSE_ITEMLIST_LIST) || itemListList != null) {
////            put(PARSE_ITEMLIST_LIST, itemListList);
////        }
//        if (templatesList != null && !templatesList.isEmpty()) {
//            put(PARSE_ITEMLIST_LIST, templatesList);
//        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
//            remove(PARSE_ITEMLIST_LIST); //if setting a list to null or setting an empty list, then simply delete the field
//        }
//    }
    public synchronized boolean reloadFromParse(boolean forceLoadFromParse, Date startDate, Date endDate) {
//        TemplateList t= INSTANCE;
//        INSTANCE=null; //next call to getInstance() will re-initiate/refresh the instance
//        return t;
        TemplateList temp = DAO.getInstance().getTemplateList(forceLoadFromParse, startDate, endDate);
        if (temp != null) {
            INSTANCE.clear(); //this is to avoid that an already cached instance get recreated (like the above code did)
            for (Object elt : temp.getListFull()) {
                INSTANCE.addItem((ItemAndListCommonInterface) elt);
            }
            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(this); //
            return true;
        }
        return false;
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
