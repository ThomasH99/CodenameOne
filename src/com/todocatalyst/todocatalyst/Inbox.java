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
public class Inbox extends ItemList {

//    public static String CLASS_NAME = "Inbox";
    private static String INBOX_RESERVED_NAME = "Inbox"; //can put any name becuase the identiication is the absence of ItemListList owner
//    private static String INBOX_RESERVED_NAME = "Inbox"; //NB name is visible e.g. in Timer

//    final static String PARSE_INBOX_LIST = ItemList.PARSE_ITEMLIST; //reuse column name from ItemList to ensure any non-overwritten calls (notably getListFull()) works, was: "categoryList";
    private static ItemList INSTANCE = null;
//    private ItemList inboxItemList;

    public Inbox() {
//        super(CLASS_NAME);
    }

    static public ItemList getInstance() {
        if (INSTANCE == null) {
//            INSTANCE = DAO.getInstance().getSpecialNamedItemListFromParse(INBOX_RESERVED_NAME);
            INSTANCE = DAO.getInstance().getInbox(INBOX_RESERVED_NAME, "Inbox");
//            INSTANCE.setUseDefaultFilter(true); //done in DAO to save the option when new instance is created
//            INSTANCE.inboxItemList = DAO.getInstance().getInbox();
        }
        return INSTANCE;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    @Override
//    public List<Item> getList() {
//        List<Item> list = getList(PARSE_INBOX_LIST);
//        if (list != null) {
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
//            return list;
//        } else {
//            return new ArrayList();
//        }
//    }
//
//    @Override
//    public List<Item> getListFull() {
//        return getList();
//    }
//
//    public void setList(List inboxList) {
//        if (inboxList != null && !inboxList.isEmpty()) {
//            put(PARSE_INBOX_LIST, inboxList);
//        } else {
//            remove(PARSE_INBOX_LIST); //if setting a list to null or setting an empty list, then simply delete the field
//        }
//    }
//
//    public Inbox resetInstance() {
//        Inbox t = INSTANCE;
//        INSTANCE = null; //next call to getInstance() will re-initiate/refresh the instance
//        return t;
//    }
//
//    @Override
//    public int getVersion() {
//        return 0;
//    }
//
//    @Override
//    public String getObjectId() {
//        return CLASS_NAME;
//    }
//</editor-fold>

}
