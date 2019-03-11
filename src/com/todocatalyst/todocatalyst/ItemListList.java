/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

//import static com.todocatalyst.todocatalyst.CategoryList.PARSE_CATEGORY_LIST;
import java.util.ArrayList;
import java.util.List;

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
//    final static String PARSE_ITEMLIST_LIST = ItemList.PARSE_ITEMLIST; //reuse column name from ItemList to ensure any non-overwritten calls (notably getListFull()) works, was: "categoryList";
    ;
//    final static String PARSE_ITEMLIST_LIST = "itemList";

    private static ItemListList INSTANCE = null;

    public ItemListList() {
        super(CLASS_NAME);
    }

    static synchronized public ItemListList getInstance() { //syncrhonized to avoid clashing with reload below
        if (INSTANCE == null) {
            INSTANCE = DAO.getInstance().getItemListList();
        }
        return INSTANCE;
    }

//    @Override
//    public List<Category> getList() {
////        return (Category) getParseObject(PARSE_CATEGORY_LIST);
//        List<Category> list = getList(PARSE_ITEMLIST_LIST);
//        if (list != null) {
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
//            return list;
//        } else {
//            return new ArrayList();
//        }
//    }
    @Override
    public List<ItemList> getListFull() {
//        return getList();
//        List<ItemList> list = getList(PARSE_ITEMLIST_LIST);
//        if (list != null) {
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
//            return list;
//        } else {
//            return new ArrayList();
//        }
        return super.getListFull();
    }
    
        public List<ItemList> getList() {
        return super.getList();
    }

    @Override
    public void setList(List list) {
//        if (has(PARSE_CATEGORY_LIST) || categoryList != null) {
//            put(PARSE_CATEGORY_LIST, categoryList);
//        }
//        if (list != null && !list.isEmpty()) {
//            put(PARSE_ITEMLIST_LIST, list);
//        } else { // !has(PARSE_ITEMLIST) && ((itemList == null || itemList.isEmpty()))
//            remove(PARSE_ITEMLIST_LIST); //if setting a list to null or setting an empty list, then simply delete the field
//        }
        super.setList(list);
    }

    public synchronized void reloadFromParse() {
//        ItemListList t= INSTANCE;
//        INSTANCE=null; //next call to getInstance() will re-initiate/refresh the instance
//        return t;
//        ItemListList temp = DAO.getInstance().getItemListList();
//        INSTANCE.clear(); //this is to avoid that an already cached instance get recreated (like the above code did)
////        for(ItemList l: temp)
//        INSTANCE.setList(temp.getList());
           ItemListList temp = DAO.getInstance().getItemListList();
        INSTANCE.clear(); //this is to avoid that an already cached instance get recreated (like the above code did)
        for (ItemAndListCommonInterface elt : temp.getListFull()) {
            INSTANCE.addItem(elt);
        }
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
//            DAO.getInstance().fetchListElementsIfNeededReturnCachedIfAvail(list);
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
