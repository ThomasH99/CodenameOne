/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

/**
 * stores the (manually sorted) list of ItemLists defined by the user. Updated
 * whenever a ItemList is created or deleted or if the order of categories is
 * changed by the user.
 *
 * @author Thomas
 */
//public class ItemListList extends ItemList<ItemList> {
public class ItemListList extends ItemList {

    public static String CLASS_NAME = "ItemListList";
//    public static String CLASS_NAME = "ItemItemList"; //DONE!!!!! change to "ItemListList" before production

//    final static String PARSE_ITEMLIST_LIST = "itemListList";
//    final static String PARSE_ITEMLIST_LIST = "itemList";

    private static ItemListList INSTANCE = null;

    public ItemListList() {
        super(CLASS_NAME);
    }

    static public ItemListList getInstance() {
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getItemListList();
        }
        return INSTANCE;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<ItemList> getList() {
//    @Override
//    public List getListXXX() {
////        return (ItemList) getParseObject(PARSE_ITEMLIST_LIST);
////        List<ItemList> list = getList(PARSE_ITEMLIST_LIST);
////        List<ItemList> list = getList(PARSE_ITEMLIST);
//        List list = getList(PARSE_ITEMLIST);
//        if (list != null) {
//            DAO.getInstance().cacheUpdateListToCachedObjects(list);
//            return list;
//        } else {
//            return new ArrayList();
//        }
//    }
    
//    @Override
//    public void setListXXX(List itemListList) {
////        if (has(PARSE_ITEMLIST_LIST) || itemListList != null) {
////            put(PARSE_ITEMLIST_LIST, itemListList);
////        }
//        if (itemListList != null && !itemListList.isEmpty()) {
////            put(PARSE_ITEMLIST_LIST, itemListList);
//            put(PARSE_ITEMLIST, itemListList);
//        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
////            remove(PARSE_ITEMLIST_LIST); //if setting a list to null or setting an empty list, then simply delete the field
//            remove(PARSE_ITEMLIST); //if setting a list to null or setting an empty list, then simply delete the field
//        }
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


}
