/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import ca.weblite.codename1.json.JSONObject;
import com.codename1.components.InfiniteProgress;
import com.codename1.io.CacheMap;
import com.codename1.io.Log;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.util.UITimer;
import com.codename1.util.EasyThread;
import com.codename1.util.RunnableWithResult;
import com.parse4cn1.ParseBatch;
import com.parse4cn1.ParseBatch.EBatchOpType;
import com.parse4cn1.ParseException;
import com.parse4cn1.ParseObject;
import com.parse4cn1.ParseQuery;
import com.parse4cn1.ParseRole;
import com.parse4cn1.ParseUser;
import com.parse4cn1.callback.GetCallback;
import com.parse4cn1.util.Logger;
import static com.todocatalyst.todocatalyst.Item.PARSE_OWNER_ITEM;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 *
 * @author Thomas
 */
public class DAO {

    private static DAO INSTANCE;
//    private static final int QUERY_LIMIT = 10000;
    private static final String FILE_DATE_FOR_LAST_CACHE_REFRESH = "TDC_cacheRefreshDate"; //marker for last local cache removeFromCache - it stored only updated parseObjects will be cached

    public static DAO getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DAO();

//            int cacheDynamicSize = MyPrefs.getInt(MyPrefs.cacheDynamicSize);
//            INSTANCE.cache.setCacheSize(cacheDynamicSize); //persist cached elements
//
//            int cacheLocalStorageSize = MyPrefs.getInt(MyPrefs.cacheLocalStorageSize);
//            if (cacheLocalStorageSize > 0) {
//                INSTANCE.cache.setAlwaysStore(true); //persist cached elements
//                INSTANCE.cache.setStorageCacheSize(cacheLocalStorageSize); //persist cached elements
//            }
//            INSTANCE.createNewCache();
//            if (false) INSTANCE.initAndConfigureCache();
//            INSTANCE.cacheLoadDataChangedOnServer(); //NO, done on start up
            //            INSTANCE.cache.setCachePrefix("$TDC$Cache"); //persist cached elements
        }
        return INSTANCE;
    }

    private DAO() {
//        initCache(); //NO, done on start up (to control exactly when in the startup flow the cache is updated with changed objects
        initAndConfigureCache();
    }

//    CacheMap<String, ParseObject> cache = new CacheMap();
//    CacheMap cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
//    MyCacheMap cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
    MyCacheMapHash cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
//    MyCacheMap cacheWorkSlots;// = new CacheMap(); //optimize speed when searching only for WorkSlots
    MyCacheMapHash cacheWorkSlots;// = new CacheMap(); //optimize speed when searching only for WorkSlots
    Date latestCacheUpdateDate = new Date(MyDate.MIN_DATE); //start wtih minimal date
//    Object temp;

//    private void cachePut(ParseObject o) {
//        cache.put(o.getObjectId(), o);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void createNewCacheForWorkSlotsXXX(boolean forceCreationOfNewCache) {
//        if (cacheWorkSlots == null || forceCreationOfNewCache) {
//            cacheWorkSlots = new CacheMap("WS");
//            initNewCache(cacheWorkSlots, MyPrefs.getInt(MyPrefs.cacheDynamicSizeWorkSlots), MyPrefs.getInt(MyPrefs.cacheLocalStorageSizeWorkSlots));
////        int cacheDynamicSize2 = MyPrefs.getInt(MyPrefs.cacheDynamicSizeWorkSlots);
////        cacheWorkSlots.setCacheSize(cacheDynamicSize2); //persist cached elements
////
////        int cacheLocalStorageSize2 = MyPrefs.getInt(MyPrefs.cacheLocalStorageSizeWorkSlots);
////        if (cacheLocalStorageSize2 > 0) {
////            cacheWorkSlots.setAlwaysStore(true); //persist cached elements
////            cacheWorkSlots.setStorageCacheSize(cacheLocalStorageSize2); //persist cached elements
////        }
//        }
//    }
//</editor-fold>
    /**
     * will cache a parseObject unless it has already been cached (to avoid
     * duplicate instances if the same parseObject is fetched multiple times)
     *
     * @param parseObject must be a non-null, valid ParseObject
     */
//    private void cachePut(String objectId, ParseObject parseObject) {
    private synchronized void cachePut(ParseObject parseObject) {
//        if (cache.get(objectId)==null)
        //cache the singleton lists:
//        if (parseObject instanceof CategoryList) {
////            cache.put(CategoryList.CLASS_NAME, parseObject.getObjectIdP());
//            cache.put(CategoryList.CLASS_NAME, parseObject);
//        } else if (parseObject instanceof ItemListList) {
////            cache.put(ItemListList.CLASS_NAME, parseObject.getObjectIdP());
//            cache.put(ItemListList.CLASS_NAME, parseObject);
//        } else if (parseObject instanceof TemplateList) {
////            cache.put(TemplateList.CLASS_NAME, parseObject.getObjectIdP());
//            cache.put(TemplateList.CLASS_NAME, parseObject);
//        } else if (parseObject instanceof Inbox) {
////            cache.put(TemplateList.CLASS_NAME, parseObject.getObjectIdP());
//            cache.put(Inbox.CLASS_NAME, parseObject);
//        }

        //above just caches the singletons, so now also cache other objects, with a special treatment for workSlots
        if (parseObject instanceof WorkSlot) {
            cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject);
        } else {
            cache.put(parseObject.getObjectIdP(), parseObject); //will override any previously put object with same ojectId
        }
        ASSERT.that(parseObject instanceof ParseObject, () -> "trying to store non-ParseObject in cache: parseObject=" + parseObject);
    }

//    private void cacheList(List<ParseObject> list) {
//    private void cacheList(List<ParseObject> list) {
    private void cacheList(List list) {
        if (list == null) {
            return;
        }
        for (Object o : list) {
//            if (true || o instanceof ParseObject) { // && o.isDataAvailable() //NOT necessary
//                if (o instanceof WorkSlot) {
//                    cacheWorkSlots.put(((ParseObject) o).getObjectIdP(), o);
//                } else {
//                    cache.put(((ParseObject) o).getObjectIdP(), o);
//                }
            cachePut((ParseObject) o);
//            }
        }
    }

    private synchronized ParseObject cacheGet(String parseObjectId) {
        Object temp;
        if ((temp = cache.get(parseObjectId)) != null || (temp = cacheWorkSlots.get(parseObjectId)) != null) {
//            if (temp instanceof String) {
//                //handle named key like CategoryList.CLASS_NAME where the named key points to the Parse ObjectId so need a second get to fecth actual parseObject
//                return (ParseObject) cache.get(temp);
//            } else {
            if (Config.TEST) {
//                ASSERT.that(temp instanceof ParseObject, "getting a non-ParseObject from cache: returned obj=" + temp + ", objectId=" + parseObjectId);
//            }
                assert temp instanceof ParseObject : "getting a non-ParseObject from cache: returned obj=" + temp + ", objectId=" + parseObjectId;
            }
            return (ParseObject) temp;
//            }
        } else {
            return null;
        }
    }

    private synchronized ParseObject cacheGet(ParseObject parseObject) {
        return cacheGet(parseObject.getObjectIdP());
    }

    private synchronized void cacheDelete(ParseObject parseObject) {
        if (parseObject.getObjectIdP() == null) {
            return;
        }
        if (parseObject instanceof WorkSlot) {
            cacheWorkSlots.delete(parseObject.getObjectIdP());
        } else {
            cache.delete(parseObject.getObjectIdP());
            //delete objects cached with named key like CategoryList.CLASS_NAME
            if (parseObject instanceof CategoryList) {
                cache.delete(CategoryList.CLASS_NAME);
            } else if (parseObject instanceof ItemListList) {
                cache.delete(ItemListList.CLASS_NAME);
            } else if (parseObject instanceof TemplateList) {
                cache.delete(TemplateList.CLASS_NAME);
            } else if (parseObject instanceof Inbox) {
                cache.delete(Inbox.CLASS_NAME);
            }
        }
    }

    /**
     * remove parseObject from cache, if sub
     *
     * @param parseObject
     */
    public synchronized void removeFromCache(ParseObject parseObject) {
        if (parseObject instanceof WorkSlot) {
            cacheWorkSlots.delete(parseObject.getObjectIdP());
//        } else if (parseObject instanceof ParseObject) {
        } else {
            if (parseObject != null) {
                ItemAndListCommonInterface elt = (ItemAndListCommonInterface) parseObject;
//            for (ItemAndListCommonInterface subelt : elt.getList()) {
                for (Object subelt : elt.getListFull()) {
                    removeFromCache((ParseObject) subelt);
                }
            }
            cache.delete(parseObject.getObjectIdP());
        }
//        else {
//            ASSERT.that("trying to delete non-ParseObject =" + parseObject);
//        }
    }

    /**
     * will fetch the latest version of a parseObject on the server and return
     * the latest version if it has a new updatedAt date than the original
     * element, otherwise it will return null
     *
     * @param originalElement
     * @return
     */
//    public ParseObject fetchIfChangedOnServer(String parseObjectClassName, String parseObjectId, ParseObject originalElement) {
//    public ParseObject fetchIfChangedOnServerXXX(ParseObject originalElement) {
//        if (originalElement.getObjectIdP() == null) {
//            return originalElement; //if orgElt was never saved, it cannot be changed on server so just return it
//        }
//        ParseObject fetchedObject = null;
//        try {
//            fetchedObject = ParseObject.fetch(originalElement.getClassName(), originalElement.getObjectIdP());
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        if (fetchedObject != null && fetchedObject.getUpdatedAt().getTime() > originalElement.getUpdatedAt().getTime()) {
//            return fetchedObject;
//        } else {
//            return originalElement;
//        }
//    }
    /**
     * fetches the Item with objectId. Returns null if no such Item.
     *
     * @param objectId
     * @return
     */
    public Item fetchItem(String objectId) {
        Item item;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((item = (Item) cacheGet(objectId)) != null) {
            return item;
        }
//        Item item = null;
        try {
            item = ParseObject.fetch(Item.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) item).isDeleted();
            }
//            cache.put(objectId, item);
            cachePut(item);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return item;
    }

    public List<Item> fetchAllItemsOwnedByItemList(ItemList itemList) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.whereEqualTo(Item.PARSE_OWNER_LIST, itemList);
        try {
            List items = query.find();
            return items;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public List<Item> fetchAllItemsWithThisCategory(Category category) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.whereEqualTo(Item.PARSE_CATEGORIES, category);
        try {
            List items = query.find();
            return items;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public WorkSlot fetchWorkSlot(String objectId) {
        WorkSlot workSlot;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((workSlot = (WorkSlot) cacheGet(objectId)) != null) {
            return workSlot;
        }
//        Item item = null;
        try {
            workSlot = ParseObject.fetch(WorkSlot.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) workSlot).isDeleted();
            }
//            cache.put(objectId, item);
            cachePut(workSlot);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return workSlot;
    }

    /**
     * special case to fetch the Owner of an item, which may be either an Item
     * or an ItemList (for now). Used in ScreenItem.
     *
     * @param objectId
     * @return
     */
    public ItemAndListCommonInterface fetchItemOwner(String objectId) {
        ItemAndListCommonInterface elt = null;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((elt = (ItemAndListCommonInterface) cacheGet(objectId)) != null) {
            return elt;
        }
        try {
            elt = ParseObject.fetch(Item.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) elt).isDeleted();
            }
            if (elt instanceof ItemAndListCommonInterface) {
                cachePut((ParseObject) elt);
                return elt;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        try {
            elt = ParseObject.fetch(ItemList.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) elt).isDeleted();
            }
            if (elt instanceof ItemAndListCommonInterface) {
                cachePut((ParseObject) elt);
                return elt;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return elt;
    }

    public Category fetchCategory(String objectId) {
        Category category;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((category = (Category) cacheGet(objectId)) != null) {
            return category;
        }
//        Item item = null;
        try {
            category = ParseObject.fetch(Category.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) category).isDeleted();
            }
//            cache.put(objectId, item);
            cachePut(category);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return category;
    }

    public RepeatRuleParseObject fetchRepeatRule(String objectId) {
        RepeatRuleParseObject repeatRule;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((repeatRule = (RepeatRuleParseObject) cacheGet(objectId)) != null) {
            return repeatRule;
        }
//        Item item = null;
        try {
            repeatRule = ParseObject.fetch(RepeatRuleParseObject.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((RepeatRuleParseObject) repeatRule).isDeleted();
            }
//            cache.put(objectId, item);
            cachePut(repeatRule);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return repeatRule;
    }

    /**
     *
     * @param objectId
     * @return
     */
    public ItemAndListCommonInterface fetchFromCacheOnly(String objectId) {
        ItemAndListCommonInterface elt;
        if (objectId == null || objectId.length() == 0) {
            return null;
        }
//        if ((elt = (ItemAndListCommonInterface) cache.get(objectId)) != null) {
        if ((elt = (ItemAndListCommonInterface) cacheGet(objectId)) != null) {
            return elt;
        }
//        Item item = null;
//        try {
//            item = ParseObject.fetchFromCacheOnly(Item.CLASS_NAME, objectId);
//            cache.put(objectId, item);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
        return null;
    }

    /**
     * fetches the Item with objectId. Returns null if no such Item.
     *
     * @param objectId
     * @return
     */
    public ItemList fetchItemList(String objectId) {
        ItemList itemList;
//        if ((itemList = (ItemList) cache.get(objectId)) != null) {
        if ((itemList = (ItemList) cacheGet(objectId)) != null) {
            return itemList;
        }
        try {
            itemList = ParseObject.fetch(ItemList.CLASS_NAME, objectId);
            if (Config.TEST) {
                assert !((ItemAndListCommonInterface) itemList).isDeleted();
            }
            cachePut(itemList);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return itemList;
    }

    /**
     * will run through list and look up every element in cache (same
     * ParseObjectId) and if found replace with cached element. If an element is
     * not found in cache, will fetch it from Parse and cache it.
     *
     * @param list
     */
    public List fetchListElementsIfNeededReturnCachedIfAvailUNCLEANED(List list) {
        assert (list != null) : "updating null list from cache";
        for (int i = 0, size = list.size(); i < size; i++) {
//            Object cachedObject;
            Object val = list.get(i);
            if (val == null || val == JSONObject.NULL) {
//                ASSERT.that((list.get(i) != null) , "entry nb=" + i + " in list  with size" + size+", name="+(list instanceof ItemList ? ((ItemList) list).getText() : "") + " == null");
                if (Config.TEST) {
                    int i2 = i;
                    int size2 = size;
                    ASSERT.that((val != null), ()
                            -> "entry nb=" + i2 + " is null! In list  with size=" + size2
                            + ", name=" + (list instanceof ItemAndListCommonInterface ? ((ItemAndListCommonInterface) list).getText() : "<none>")
                            + ", parseId=" + (list instanceof ParseObject ? ((ParseObject) list).getObjectIdP() : "<none>")
                            + ", toString=" + list.toString());
                    ASSERT.that((val != JSONObject.NULL), () -> "entry nb=" + i2 + " in list  with size" + size2 + ", name="
                            + (list instanceof ItemList ? ((ItemList) list).getText() : "")
                            + ", parseId=" + (list instanceof ParseObject ? ((ParseObject) list).getObjectIdP() : "") + " == JSONObject.NULL");
                }
//                ASSERT.that((list.get(i) != JSONObject.NULL), "entry nb=" + i + " in list " + (list instanceof ItemList ? ((ItemList) list).getText() : "") + " == JSONObject.NULL");
                list.remove(i); //UI: clean up elements that don't exist anymore
                i--; //neutralize the i++ in the loop
                size--; //update size to match actual size of loop
            } else {
//<editor-fold defaultstate="collapsed" desc="comment">
//                ParseObject listElt = (ParseObject) list.get(i);
//                String objId = listElt.getObjectIdP();
////            if (objId != null && (cachedObject = cache.get(objId)) != null && cachedObject != p) {
////                if (objId != null && (cachedObject = cacheGet(objId)) != null && cachedObject != p) {
//                if ((cachedObject = cacheGet(listElt)) != null && cachedObject != listElt) {
//                    list.set(i, cachedObject);
//                } else {
//                    cachedObject = fetchIfNeededReturnCachedIfAvail(listElt); //NB! will possibly replace the parseObjects in the list with cached ones
//                    cachePut(listElt); //put new
//                    list.set(i, cachedObject);
//                }
//</editor-fold>
//                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) list.get(i)));
                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) val));
            }
        }
        return list;
    }

    public List fetchListElementsIfNeededReturnCachedIfAvail(List list) {
        if (Config.TEST) {
            ASSERT.that((list != null), "updating null list from cache");
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            Object val = list.get(i);
            if (val == null && Config.TEST) {
                ASSERT.that(val != null, "null element in list from cache:" + list);
            }
            list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) val));
            if (Config.TEST) {
                ASSERT.that(list.get(i) != null, "null returned from cache for object=" + val + "; for list=" + list);
            }
        }
        return list;
    }

    public List<Item> fetchListOfItemsFromListOfObjectIds(List<String> list) {
        if (Config.TEST) {
            ASSERT.that((list != null), "updating null list from cache");
        }
        ArrayList<Item> items = new ArrayList<>();
        for (String objectId : list) {
            items.add(fetchItem(objectId));
        }
        return items;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededReturnCachedIfAvailOLDXXX(ParseObject parseObject) {
//        if (parseObject == null) {
//            return null;
//        }
//        try {
//            ParseObject temp;
//            if (parseObject != null && parseObject.getObjectIdP() != null) {
//                if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
//                    return temp;
//                } else if ((temp = (ParseObject) cacheWorkSlots.get(parseObject.getObjectIdP())) != null) {
//                    return temp;
//                } else {
//                    parseObject.fetchIfNeeded();
//                    cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                    return parseObject;
//                }
//            }
//        } catch (ParseException ex) {
////            Log.e(ex);
//            return null;
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededXXX(ParseObject parseObject) {
//        try {
//            ParseObject fetched = parseObject.fetchIfNeeded();
//            if (fetched instanceof WorkSlot) {
//                cacheWorkSlots.put(parseObject.getObjectIdP(), fetched);
//            } else {
//                cache.put(parseObject.getObjectIdP(), fetched);
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededOrgXXX(ParseObject parseObject) {
//        try {
//            if (parseObject != null) {
//                parseObject.fetchIfNeeded();
//                cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                return parseObject;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    //    public void fetchAllElementsInSublist(ParseObject listOrCategory, boolean recursively) {
//        try {
//            listOrCategory.fetchIfNeeded();
//            fetchAllElementsInSublist(listOrCategory, recursively);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
//    public void fetchAllElementsInSublist(ParseObject listOrCategory, boolean recursively) {
//</editor-fold>
    /**
     * NB! Not the usual semantic of fetchFromCacheOnly, since it may return an
     * existing instance of the parseObject instead of simply fetching the data
     * for the passed parseObject (to avoid multiple parallel copies of the same
     * ParseObject, e.g. when using getOnwerList, a new instance of the
     * ownerList is returned and any changes to this will not be reflected until
     * the list is saved and fetched again).
     *
     * @param parseObject
     * @return null if object does not (or no longer) exist on server
     */
    public ParseObject fetchIfNeededReturnCachedIfAvail(ParseObject parseObject) {
        if (parseObject == null) {
            return null;
        }
        if (parseObject.getObjectIdP() == null || parseObject.getObjectIdP().equals("")) {
            //this happens for example when getting elements from **
//            cachePut(parseObject); //NO, can't cache without a objectId [//cache it to avoid duplicates (rellay necessary?)]
            return parseObject; //for not yet saved parseObjects (pending saving), return the object itself
        }
        ParseObject temp;
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (parseObject.getObjectIdP() != null) {
//            if (parseObject instanceof WorkSlot && (obj = cacheWorkSlots.get(parseObject.getObjectIdP())) != null) {
////            if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
//                return (WorkSlot) obj;
//            } else if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
//                return temp;
//            } else
//</editor-fold>
        if ((temp = (ParseObject) cacheGet(parseObject)) != null) {
            return temp;
//        } else if (parseObject instanceof WorkSlot) {

        } else {
            try {
                parseObject.fetchIfNeeded();
                if (Config.TEST) {
                    if (parseObject instanceof ItemAndListCommonInterface) {
                        ASSERT.that(!((ItemAndListCommonInterface) parseObject).isDeleted(), "DAO.fetch of deleted object:" + parseObject);
                    } else if (parseObject instanceof FilterSortDef) {
                        //                        assert !((FilterSortDef) parseObject).isDeleted();
                        ASSERT.that(!((FilterSortDef) parseObject).isDeleted(), "DAO.fetch of deleted object:" + parseObject);
                    } else if (parseObject instanceof RepeatRuleParseObject) {
                        assert !((RepeatRuleParseObject) parseObject).isDeleted();
                        ASSERT.that(!((RepeatRuleParseObject) parseObject).isDeleted(), "DAO.fetch of deleted object:" + parseObject);
                    }
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                    if (parseObject instanceof WorkSlot) {
////                        cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                        cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject);
//                    } else {
////                        cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                        cache.put(parseObject.getObjectIdP(), parseObject);
//                    }
//</editor-fold>
                cachePut(parseObject);
                //NO need to fetch lists within the object, they are updated when they are used first time (in getList() using fetchListElementsIfNeededReturnCachedIfAvail()...)
//                if (parseObject instanceof ItemAndListCommonInterface) {
//                    List list;
//                    if ((list = ((ItemAndListCommonInterface) parseObject).getList()) != null) {
//                        fetchAllElementsInSublist(list);
//                    }
//                }
                return parseObject;
            } catch (ParseException ex) {
//            Log.e(ex);
                return null;
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededReturnCachedIfAvailXXXCANNOT_RETURN_APRSEOBJECT(String parseObjectId) {
//        if (parseObjectId == null||parseObjectId.length()==0) {
//            return null;
//        }
//        ParseObject temp;
//        if ((temp = (ParseObject) cacheGet(parseObjectId)) != null) {
//            return temp;
//        } else {
//            try {
//                ParseObject.fetchIfNeeded();
//                cachePut(parseObject);
//                return parseObject;
//            } catch (ParseException ex) {
//                return null;
//            }
//        }
//    }
//</editor-fold>
//    public void fetchAllElementsInSublistXXX(ItemAndListCommonInterface itemOrItemListOrCategoryOrList) {
//        fetchAllElementsInSublistXXX((ParseObject) itemOrItemListOrCategoryOrList, false);
//    }
//    public void fetchAllElementsInSublist(List itemOrItemListOrCategoryOrList) {
//        fetchAllElementsInSublist(itemOrItemListOrCategoryOrList, false);
//    }
//    public void fetchAllElementsInSublistXXX(ParseObject itemOrItemListOrCategoryOrList, boolean recursively) {
//        assert itemOrItemListOrCategoryOrList != null : "fetchAllItemsIn called with null list";
////        List<ParseObject> list = null;
//        List list = null;
////        List<ItemAndListCommonInterface> list = null;
//        try {
////            ParseBatch batch = ParseBatch.create();
//            //TODO!!! find more efficient way to fetchFromCacheOnly all the objects - use ParseBatch once switched to new version of parse4cn1!!
//
//            if (false && itemOrItemListOrCategoryOrList instanceof ParseObject && ((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP() != null) {
////                 if ((temp = cache.get(((ParseObject) itemOrListOrCategory).getObjectId())) != null) {
////            return temp;
////        }
//                //assume that object has been fully fetched:
//                ((ParseObject) itemOrItemListOrCategoryOrList).fetchIfNeeded(); //fetch the top-level object if needed (may be the case when fetching recursively)
//                if (Config.TEST) assert !((ItemAndListCommonInterface) itemOrItemListOrCategoryOrList).isDeleted();
////                if (itemOrItemListOrCategoryOrList instanceof WorkSlot) {
////                    cacheWorkSlots.put(((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP(), itemOrItemListOrCategoryOrList);
////                } else {
////                    cache.put(((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP(), itemOrItemListOrCategoryOrList);
////                }
//                cachePut((ParseObject) itemOrItemListOrCategoryOrList);
//            }
//
//            if (itemOrItemListOrCategoryOrList instanceof ItemAndListCommonInterface) {
//                list = ((ItemAndListCommonInterface) itemOrItemListOrCategoryOrList).getList();
//                for (int i = 0, size = list.size(); i < size; i++) {
//                    list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
////                    if (recursively) {
////                        fetchAllElementsInSublist((ParseObject) list.get(i), recursively);
////                    }
//                }
//            } else {
//                ASSERT.that(false, "tried to fetch sublist of elements from wrong type");
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (itemOrItemListOrCategoryOrList instanceof Item) {
//////                list = (List<Item>)((Item) itemOrItemListOrCategoryOrList).getList();
////                list = ((Item) itemOrItemListOrCategoryOrList).getList();
//////                list = ((List<Item>) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof Category) {
////                list = ((Category) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof ItemList) {
////                list = ((ItemList) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof List) {
////                list = (List) itemOrItemListOrCategoryOrList;
////            } else {
////                assert false : "unknow type of element to fetch=" + itemOrItemListOrCategoryOrList;
////            }
//////            for (ParseObject item : list) {
////            for (int i = 0, size = list.size(); i < size; i++) {
//////                ((Item) item).fetchIfNeeded();
//////                item.fetchIfNeeded();
//////                fetchIfNeeded(item);
////                list.set(i, fetchIfNeededReturnCachedIfAvail(list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
////                if (recursively) {
////                    fetchAllElementsInSublist(list.get(i), recursively);
////                }
////            }
////</editor-fold>
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        if (itemOrItemListOrCategoryOrList instanceof Item) {
//            ((Item) itemOrItemListOrCategoryOrList).setList(list);
//        } else if (itemOrItemListOrCategoryOrList instanceof Category) {
//            ((Category) itemOrItemListOrCategoryOrList).setList(list);
//        } else if (itemOrItemListOrCategoryOrList instanceof ItemList) {
//            ((ItemList) itemOrItemListOrCategoryOrList).setList(list);
//        };
//    }
    public int getItemCount(boolean onlyDone) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        int count = 0;
        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        if (onlyDone) {
            query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString());
            try {
                count = query.count();
            } catch (ParseException ex) {
//                Logger.getLogger(DAO.class.getName()).log(Level.SEVERE, null, ex);
                Log.e(ex);
            }
        } else {
            try {
                count = query.count();
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
        return count;
    }

    private List removeDuplicates(List list) {
        List noDups = new ArrayList();
        for (Object obj : list) {
            if (!list.contains(obj)) {
                noDups.add(obj);
            }
        }
        return noDups;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private ParseQuery<Item> getDueAndOrWaitingTodayQuery(boolean includeWaiting, boolean includeStartingToday) {
//    private ParseQuery<Item> getDueAndOrWaitingTodayQuery(boolean includeWaiting, boolean includeStartingToday) {
//        Date startDate = new Date(System.currentTimeMillis() - MyPrefs.includeOverdueFromThisManyPastDays.getInt() * MyDate.DAY_IN_MILLISECONDS);
//
//        Date startOfToday = MyDate.getStartOfDay(startDate);
////        Date endOfToday = MyDate.getEndOfDay(now);
//        Date startOfTomorrow = new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS);
//
//        List<ParseQuery> queries = new ArrayList<>();
//
//        ParseQuery<Item> query = null;
//
//        ParseQuery<Item> queryDueToday = ParseQuery.getQuery(Item.CLASS_NAME);
//        queryDueToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday);
//        queryDueToday.whereLessThan(Item.PARSE_DUE_DATE, startOfTomorrow);
////        queryDueToday.whereContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.CREATED.toString(), ItemStatus.ONGOING.toString(), ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED
//        queryDueToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////        setupItemQueryNoTemplatesLimit1000(queryDueToday);
//        queryDueToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
//        queryDueToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        ParseQuery<Item> queryWaitingExpiresToday = null;
//        if (includeWaiting) {
//            queryWaitingExpiresToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_WAITING_TILL_DATE, startOfToday);
//            queryWaitingExpiresToday.whereLessThan(Item.PARSE_WAITING_TILL_DATE, startOfTomorrow);
//            if (false) {
//                queryWaitingExpiresToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday); //don't get Waiting tasks that are due today
//                queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfTomorrow); //don't get Waiting tasks that are due today
//            }
//            queryWaitingExpiresToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED
////        queryDueToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), ItemStatus.ONGOING.toString(), ItemStatus.CREATED.toString()))); //item that are NOT DONE or CANCELLED
////        setupItemQueryNoTemplatesLimit1000(queryWaitingExpiresToday);
//            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
//            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        }
//
//        ParseQuery<Item> queryStartToday = null;
//        if (includeStartingToday) {
//            queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, startOfToday);
//            queryStartToday.whereLessThan(Item.PARSE_START_BY_DATE, startOfTomorrow);
//            if (false) {
//                queryStartToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday); //don't get Waiting tasks that are due today
//                queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfTomorrow); //don't get Waiting tasks that are due today
//            }
////            queryStartToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED
////            queryStartToday.whereContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.CREATED.toString(), ItemStatus.ONGOING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides
//            queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides
//            queryStartToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
//            queryStartToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
////        setupItemQueryNoTemplatesLimit1000(queryStartToday);
//        }
//
//        //TODO!!!! include tasks fitting into WorkSlots scheduled for today
//        try {
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (includeWaiting && includeStartingToday) {
//////                query = ParseQuery.getOrQuery(Arrays.asList(queryDueToday, queryWaitingExpiresToday, queryStartToday)); //check if return order is same as order of OR queries
////                query = ParseQuery.getOrQuery(Arrays.asList(queryStartToday, queryWaitingExpiresToday, queryDueToday)); //check if return order is same as order of OR queries
////            } else if (includeWaiting) {
////                query = ParseQuery.getOrQuery(Arrays.asList(queryWaitingExpiresToday, queryDueToday));
////            } else if (includeStartingToday) {
////                query = ParseQuery.getOrQuery(Arrays.asList(queryStartToday, queryDueToday));
////            } else {
////                query = queryDueToday;
////            }
////</editor-fold>
//            query = ParseQuery.getOrQuery(queries);
//            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        setupItemQueryNoTemplatesLimit1000(query); //NO, putting a query at this level breaks the request(???)
//        return query;
//    }
//</editor-fold>
//    public List<ItemAndListCommonInterface> getTodayDueAndOrWaitingOrWorkSlotsItems(boolean includeWaiting, boolean includeStartingToday) {
    public List<ItemAndListCommonInterface> getToday() {
//        ParseQuery<Item> query = getDueAndOrWaitingTodayQuery(includeWaiting, includeStartingToday);

        List<ParseQuery> queries = new ArrayList<>();

        ParseQuery<Item> query = null;

        Date startOfToday = MyDate.getStartOfDay(new MyDate());
        Date startOfTomorrow = new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS);

        Date startDate = new Date(MyDate.currentTimeMillis() - MyPrefs.todayViewIncludeOverdueFromThisManyPastDays.getInt() * MyDate.DAY_IN_MILLISECONDS);
        ParseQuery<Item> queryDueToday = ParseQuery.getQuery(Item.CLASS_NAME);
        queryDueToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startDate);
        queryDueToday.whereLessThan(Item.PARSE_DUE_DATE, startOfTomorrow);

        queryDueToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED

        queryDueToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetch any templates
        queryDueToday.whereDoesNotExist(Item.PARSE_DELETED_DATE); //or deleted
        queries.add(queryDueToday);

        if (MyPrefs.todayViewIncludeWaitingExpiringToday.getBoolean()) {
            ParseQuery<Item> queryWaitingExpiresToday = ParseQuery.getQuery(Item.CLASS_NAME);
            queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_WAITING_TILL_DATE, startOfToday);
            queryWaitingExpiresToday.whereLessThan(Item.PARSE_WAITING_TILL_DATE, startOfTomorrow);
            if (false) { //TODO!!! find a way to eliminate duplicates, e.g. a task which is both due today, waiting till today and has an alarm today...
                queryWaitingExpiresToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday); //don't get Waiting tasks that are due today
                queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfTomorrow); //don't get Waiting tasks that are due today
            }
            queryWaitingExpiresToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED

            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
            queries.add(queryWaitingExpiresToday);
        }

        if (MyPrefs.todayViewIncludeStartingToday.getBoolean()) {
            ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
            queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, startOfToday);
            queryStartToday.whereLessThan(Item.PARSE_START_BY_DATE, startOfTomorrow);
            queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides

            queryStartToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
            queryStartToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
            queries.add(queryStartToday);
        }

        if (MyPrefs.todayViewIncludeAlarmsExpiringToday.getBoolean()) {
            ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
            queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_ALARM_DATE, startOfToday);
            queryStartToday.whereLessThan(Item.PARSE_ALARM_DATE, startOfTomorrow);
            queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides

            queryStartToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
            queryStartToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
            queries.add(queryStartToday);
        }

        List allTodayElements = new ArrayList();

        //get all items
        try {
            query = ParseQuery.getOrQuery(queries);
            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
            List result = query.find();
            allTodayElements.addAll(result);
        } catch (ParseException ex) {
            Log.e(ex);
        }

        //get workslots
        //WORKSLOTS - get workslots starting today
        if (MyPrefs.todayViewIncludeWorkSlotsCoveringToday.getBoolean()) { //fetch WorkSLots that have workTime between now and end of today

            ParseQuery<WorkSlot> queryWorkSlots = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
            queryWorkSlots.whereGreaterThan(WorkSlot.PARSE_END_TIME, new MyDate()); //slots that end *after* *now*
            queryWorkSlots.whereLessThan(WorkSlot.PARSE_START_TIME, startOfTomorrow); //and starts before tomorrow

            queryWorkSlots.whereDoesNotExist(Item.PARSE_DELETED_DATE);
            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

            try {
                List<WorkSlot> resultsWorkSlots = queryWorkSlots.find();
                allTodayElements.addAll(resultsWorkSlots); //real hack: disguise workslot as task... TODO!!!! No good, because treats workslot as task (e.g can edit task fields, cannot edit workslot!!
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }

        removeDuplicates(allTodayElements); //TODO!!!: will remove duplicate tasks, but not tasks in workslots, not sure this is an issue?!
        fetchListElementsIfNeededReturnCachedIfAvail(allTodayElements);
        return allTodayElements;
    }

    public List<Item> getOverdue() {
        Date startOfToday = MyDate.getStartOfToday();
        Date startOfOverdueInterval = new Date(startOfToday.getTime() - MyPrefs.overdueLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);

        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfOverdueInterval);
        query.whereLessThan(Item.PARSE_DUE_DATE, startOfToday);
        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            List<Item> results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return new ArrayList();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public int getDueAndOrWaitingTodayCount(boolean includeWaiting, boolean includeStartingToday) {
//        //DONE!!!! include Waiting expiring today (OrQuery)
////        int count = 0;
////TODO!!! this is called on app exit/stop(), don't send a query then
//        ParseQuery<Item> query = getDueAndOrWaitingTodayQuery(includeWaiting, includeStartingToday);
//        try {
//            int count = query.count();
//            return count;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return 0;
//    }
//</editor-fold>
    /**
     * returns future Undone tasks with a future StartDate or DueDate
     *
     * @return
     */
    public List<Item> getCalendar() {
//        Calendar cal = Calendar.getInstance();
//        cal.set(Calendar.HOUR_OF_DAY, 0);
//        cal.set(Calendar.MINUTE, 0);
//        cal.set(Calendar.SECOND, 0);
//        cal.set(Calendar.MILLISECOND, 0);

        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);
//        queryDue.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
//        queryDue.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        queryDue.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, cal.getTime());
//        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, MyDate.getMidnightOfDay(new Date()));
        Date startOfToday = MyDate.getStartOfToday();
        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS));
        query.whereLessThan(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS * (1 + MyPrefs.nextInterval.getInt()))); //+1 since Next always start tomorrow
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        if (false) {
//            ParseQuery<Item> queryStart = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupStandardItemQuery(queryStart);
//            queryStart.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
//            queryStart.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
//            queryStart.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, cal.getTime());
        }

//        ParseQuery<Item> queryOr = ParseQuery.getOrQuery(new ArrayList({queryDue, queryStart}));
        List<Item> results = null;
        try {
            if (false) {
//                ParseQuery<Item> queryOr = ParseQuery.getOrQuery(Arrays.asList(queryDue, queryStart));
//                queryOr.orderByDescending(Item.PARSE_DUE_DATE);
//                results = queryOr.find();
            } else {
                results = query.find();
//                fetchAllElementsInSublist(results, false);
                fetchListElementsIfNeededReturnCachedIfAvail(results);
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    /**
     * return number of undone tasks due today (between midnigth and midnight)
     *
     * @param onlyDone
     * @return
     */
    public int getBadgeCount(boolean includeWaiting, boolean includeStartingToday) {
        //UI: badgecount includes all elements shown in Today view (counting leaf-tasks for Projects!)
//        return getDueAndOrWaitingTodayCount(includeWaiting, includeStartingToday);
        List<ItemAndListCommonInterface> all = getToday();
        int count = 0;
        for (ItemAndListCommonInterface elt : all) {
            if (elt instanceof WorkSlot) {
                count += ((WorkSlot) elt).getItemsInWorkSlot().size();
            } else if (elt instanceof Item) {
                Item item = (Item) elt;
                if (item.isProject()) {
                    count += item.getLeafTasksAsList((itm) -> !itm.isDone()).size();
                }
            } else {
                count++;
            }
        }
        return count;
    }

//<editor-fold defaultstate="collapsed" desc="//<editor-fold defaultstate="collapsed" desc="comment">
//    private int getDueAndOrWaitingTodayCountOLD(boolean includeWaiting) {
//        //TODO!!!! include Waiting expiring today (OrQuery)
//        int count = 0;
//        Date now = new Date();
//
//        Date midnightToday = MyDate.getStartOfDay(now);
//        Date endOfToday = MyDate.getEndOfDay(now);
//
//        ParseQuery<ParseObject> query;
//
//        ParseQuery<ParseObject> queryDueToday = ParseQuery.getQuery(Item.CLASS_NAME);
//        queryDueToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, midnightToday);
//        queryDueToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, endOfToday);
//        queryDueToday.whereContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.CREATED.toString(), ItemStatus.ONGOING.toString()))); //item that are NOT DONE or CANCELLED
//
//        ParseQuery<ParseObject> queryWaitingExpiresToday = null;
//        if (includeWaiting) {
//            queryWaitingExpiresToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_WAITING_TILL_DATE, midnightToday);
//            queryWaitingExpiresToday.whereLessThanOrEqualTo(Item.PARSE_WAITING_TILL_DATE, endOfToday);
//            queryWaitingExpiresToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED
//        }
//        try {
//            if (includeWaiting) {
//                query = ParseQuery.getOrQuery(Arrays.asList(queryWaitingExpiresToday, queryDueToday));
//            } else {
//                query = queryDueToday;
//            }
//            return query.count();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return count;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private int getDueTodayCountOLD(boolean includeWaiting) {
//        //TODO!!!! include Waiting expiring today (OrQuery)
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        int count = 0;
//        Date now = new Date();
//
//        Date midnightToday = MyDate.getStartOfDay(now);
//        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, midnightToday);
//
//        Date endOfToday = MyDate.getEndOfDay(now);
//        query.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, endOfToday);
//
////        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString());
////        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString());
//        if (!includeWaiting) {
//            query.whereNotContainedIn(Item.PARSE_STATUS,
//                    new ArrayList(Arrays.asList(ItemStatus.WAITING.toString(), ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////            query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString());
//        } else {
//            query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        }
//        try {
//            count = query.count();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return count;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List getAll(String parseClassName) {
//        ParseQuery query = ParseQuery.getQuery(parseClassName);
//        List<ParseObject> results = null;
//        try {
//            results = query.find();
//            //TODO!!!! need to split getAll into a 'refreshCache' version and a getAllCached
//            for (ParseObject o : results) {
//                if (true || o.isDataAvailable()) { //NB. Always cache, even empty objects!!
////                    if (o instanceof WorkSlot) {
////                        cacheWorkSlots.put(o.getObjectIdP(), o);
////                    } else {
////                        cache.put(o.getObjectIdP(), o);
////                    }
//                    cachePut(o);
//                }
//            }
////            cacheList(results);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
    /**
     * returns list of all defined categories (or empty list if none)
     *
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public CategoryList getCategoryListFromParse() {
//    public CategoryList getCategoryList() {
//        return getCategoryList(false);
//    }
//
//    private CategoryList getCategoryList(boolean forceFromParse) {
//        return getCategoryList(forceFromParse, null, null); //new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
//    }
//</editor-fold>
    public CategoryList getCategoryList() {
//        CategoryList categoryList = null;
        List<CategoryList> results = null;
        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = query.find();
            //if no categoryList already saved, initialize it with existing categories
            if (results.size() > 0) {
                int size = results.size();
                ASSERT.that(results.size() <= 1, () -> "error: more than one CategoryList element (" + size + ")"); //TODO create error log for this 
                CategoryList categoryList = results.get(0); //return first element
                CategoryList cachedCategoryList = (CategoryList) cacheGet(categoryList);
                if (cachedCategoryList != null) {
                    categoryList = cachedCategoryList;
                } else {
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
                    fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements  
                    cachePut(categoryList);
                }
                return categoryList;
            } else {
                CategoryList categoryList2 = new CategoryList(); //need a separate instance because use in lambda below
//                categoryList.addAll(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                saveAndWait((ParseObject) categoryList); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
//                cachePut(categoryList); //always save so new lists can be assigned to it
//                saveInBackground((ParseObject) categoryList2, () -> cachePut(categoryList2)); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
                saveInBackground((ParseObject) categoryList2); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
                return categoryList2;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (categoryList != null) {
//            cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        CategoryList categoryListTmp = (CategoryList) DAO.getInstance().fetchFromCacheOnly(categoryList.getObjectIdP()); //use same object if already cached
//        if (categoryListTmp != null)
//            categoryList = categoryListTmp;
//        else {
//            fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements
//            cachePut(categoryList); //cache list
//        }
//</editor-fold>
//        return categoryList;
        assert false;
        return null;
    }

    public CategoryList getCategoryList(boolean forceLoadFromParse, Date startDate, Date endDate) {
        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");

        CategoryList categoryList = null;
        List<CategoryList> results = null;
        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
        if (!forceLoadFromParse) {
            if (startDate != null) {
                query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
            }
            if (endDate != null) {
                query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
            }
        }
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = query.find();
            //if no categoryList already saved, initialize it with existing categories
            if (results.size() > 0) {
                int size = results.size();
                ASSERT.that(results.size() <= 1, () -> "error: more than one CategoryList element (" + size + ")"); //TODO create error log for this 
                categoryList = results.get(0); //return first element
                return categoryList;
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//            else {
//                categoryList = new CategoryList();
//                categoryList.addAll(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                saveInBackground((ParseObject) categoryList); //always save so new lists can be assigned to it
//                cachePut(categoryList); //always save so new lists can be assigned to it
//            }
//</editor-fold>
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (categoryList != null) {
//            cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        CategoryList categoryListTmp = (CategoryList) DAO.getInstance().fetchFromCacheOnly(categoryList.getObjectIdP()); //use same object if already cached
//        if (categoryListTmp != null)
//            categoryList = categoryListTmp;
//        else {
//            fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements
//            cachePut(categoryList); //cache list
//        }
//</editor-fold>
        return categoryList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public CategoryList getCategoryListXXX(boolean forceFromParse, Date startDate, Date endDate) {
//        CategoryList categoryList = null;
////        if (!forceFromParse && (categoryList = (CategoryList) cache.get(CategoryList.CLASS_NAME)) != null) {
//        if (!forceFromParse && (categoryList = (CategoryList) cacheGet(CategoryList.CLASS_NAME)) != null) {
//            return categoryList;
//        }
//        List<CategoryList> results = null;
////        ParseUser parseUser = ParseUser.getCurrent();
////        if (parseUser != null) {
//        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
////                query.selectKeys(new ArrayList()); //just get search result, no data (these are cached) //NOOO: gets an empty list
//        if (!forceFromParse) {
//            if (startDate != null) {
//                query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
//            }
//            if (endDate != null) {
//                query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
//            }
//        }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////            query.setLimit(1); //if ever there is more than one list, get only first one
////            if (false) {
////                query.include(CategoryList.PARSE_CATEGORY_LIST);
////                //get only strict minimum: name of Category and number items in each category:
////                query.include(CategoryList.PARSE_CATEGORY_LIST + "." + Category.CLASS_NAME);
////                query.include(CategoryList.PARSE_CATEGORY_LIST + "." + Category.CLASS_NAME + "." + Category.PARSE_ITEMLIST);
////            }
////</editor-fold>
//        if (false) {
//            query.selectKeys(new ArrayList(Arrays.asList(CategoryList.PARSE_CATEGORY_LIST))); //just get search result, no data (these are cached) //NOOO: gets an empty list
//        }//<editor-fold defaultstate="collapsed" desc="comment">
////            query.include(CategoryList.PARSE_CATEGORY_LIST+"."+Category.CLASS_NAME);
////            query.include(CategoryList.PARSE_CATEGORY_LIST+"."+Category.PARSE_ITEMLIST);
////            query.include(CategoryList.PARSE_CATEGORY_LIST + "." + Category.PARSE_TEXT); //get categories and their name field
////            query.selectKeys(null); //just get search result, no data (these are cached)
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        try {
//            results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
//            if (results.size() > 0) {
//                int size = results.size();
//                ASSERT.that(results.size() <= 1, () -> "error: more than one CategoryList element (" + size + ")"); //TODO create error log for this
//                categoryList = results.get(0); //return first element
//                if (false) {
//                    fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                }
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //NOT necessary since categoryList.getList() will fetch the items
//            } else {//if (results.size() == 0) {
////            if (startDate == null && endDate == null) { //avoid initializing the CategoryList if we limited the time interval to only get list if recently changed
//                categoryList = new CategoryList();
//                categoryList.addAll(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
////                categoryList.setList(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
//                if (false) {
//                    fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                }
////                getInstance().save(categoryList); //always save so new lists can be assigned to it
//                saveInBackground((ParseObject) categoryList); //always save so new lists can be assigned to it
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        }
////ASSERT: categoryList should never become null
//        if (categoryList != null) {
//            cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
//        }
//        return categoryList;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean cacheCategoryList(Date startDate, Date now) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        boolean result = false;
////        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
////        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
////        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
////        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
////        try {
////            List<CategoryList> results = query.find();
////            //if no categoryList already saved, initialize it with existing categories
////            if (results.size() > 0) {
////                CategoryList categoryList = results.get(0); //return first element
//////                fetchAllElementsInSublist(categoryList); //NOT necessary since categoryList.getList() will fetch the items
//////                cache.put(categoryList.getObjectIdP(), categoryList); //may fetchFromCacheOnly by objectId via getOwner
//////                cache.put(CategoryList.CLASS_NAME, categoryList);
////                cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
////                result = true;
////            }
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////        return result;
////</editor-fold>
////        return getCategoryList(true, startDate, now) != null;
//        return getCategoryList(true) ;
//    }
//</editor-fold>
    /**
     * get ItemItemList for the singleton, either from cache or parse server, or
     * a new list if first time
     *
     * @return
     */
    public ItemListList getItemListList() {
//        return getItemListList(false);
//        ItemListList itemListList = null;
//        if ((itemListList = (ItemListList) cacheGet(ItemListList.CLASS_NAME)) != null) {
//            return itemListList;
//        }
        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            List<ItemListList> results = query.find();
            //if no categoryList already saved, initialize it with existing categories
            if (results.size() > 0) {
                ASSERT.that(results.size() <= 1, () -> "error: more than one ItemItemList element (" + results.size() + ")");
                ItemListList itemListList = results.get(0); //return first element
//                fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements  
//                cachePut(itemListList);
                ItemListList cachedItemListList = (ItemListList) cacheGet(itemListList);
                if (cachedItemListList != null) {
                    itemListList = cachedItemListList;
                } else {
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
                    fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements  
                    cachePut(itemListList);
                }
                return itemListList;
            } else {
                ItemListList itemListList2 = new ItemListList();
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) { //if forceload and no results, mean we need to initialize
//                    itemListList.addAll(getAllItemListsFromParse());
//                    fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
//                }
//</editor-fold>
//                saveAndWait((ParseObject) itemListList); //always save so new lists can be assigned to it
//                cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
//                saveInBackground((ParseObject) itemListList2, () -> cachePut(itemListList2)); //always save so new lists can be assigned to it
                saveInBackground((ParseObject) itemListList2); //SAVEINBACKGROUND automatically adds to cache //always save so new lists can be assigned to it
                return itemListList2;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        assert false;
//        return itemListList;
        return null;
    }

//    public ItemListList getItemListList(boolean forceFromParse) {
//        return getItemListList(forceFromParse, null, null); //new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
//    }
//
//    public ItemListList getItemListList(Date startDate, Date endDate) {
//        return getItemListList(false, startDate, endDate);
//    }
    /**
     * call ONLY from Singleton to ensure it is always up-to-date, *either*
     * forceload, disregarding dates, *or* reload if changed in the indicated
     * time interval, return null if no element exists on Parse or not updated
     * in the given interval. does not cache the element, that should be done by
     * the caller.
     *
     * @param forceLoadFromParse reload from Parse server
     * @param startDate reload if changed in the given date interval
     * @param endDate
     * @return
     */
    public ItemListList getItemListList(boolean forceLoadFromParse, Date startDate, Date endDate) {
        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");
        ItemListList itemListList = null;
        List<ItemListList> results;// = null;
        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
        if (!forceLoadFromParse) { //if forceloading, ignore any dates given
            if (startDate != null) {
                query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
            }
            if (endDate != null) {
                query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
            }
        }
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //don't load deleted objects (shouldn't be relevant for now since cannot delete ItemListList)
        try {
            results = query.find();
            //if no categoryList already saved, initialize it with existing categories
            if (results.size() > 0) {
                ASSERT.that(results.size() <= 1, () -> "error: more than one ItemItemList element (" + results.size() + ")");
                itemListList = results.get(0); //return first element
                return itemListList;
//                fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements   //NB. DONE below 
            }
//            cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemListList itemListListTmp = (ItemListList) DAO.getInstance().fetchFromCacheOnly(itemListList.getObjectIdP()); //use same object if already cached
////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
//        if (itemListListTmp != null)
//            itemListList = itemListListTmp;
//        else {
//            fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
//            //        return TemplateList.getInstance();
//            cachePut(itemListList); //cache list
//        }
//</editor-fold>
        return itemListList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean cacheItemListList(Date startDate, Date now) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        boolean result = false;
////        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
////        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
////        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
////        try {
////            List<ItemListList> results = query.find();
////            //if no categoryList already saved, initialize it with existing categories
////            if (results.size() > 0) {
////                ItemListList itemListList = results.get(0); //return first element
//////                fetchAllElementsInSublist(itemListList);//NOT necessary since categoryList.getList() will fetch the items
//////                cache.put(itemListList.getObjectIdP(), itemListList); //may fetchFromCacheOnly by objectId via getOwner
//////                cache.put(ItemListList.CLASS_NAME, itemListList);
////                cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
////                result = true;
////            }
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////        return result;
////</editor-fold>
////        return getItemListList(true, startDate, now) != null;
////        return getItemListList(true, null,null) != null;
//        return ItemListList.getInstance().reloadFromParse(true, null, null);
//    }
//
//    public boolean cacheTemplateList(Date startDate, Date endDate) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        boolean result = false;
////        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
////        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
////        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
////        try {
////            List<TemplateList> results = query.find();
////            //if no categoryList already saved, initialize it with existing categories
////            if (results.size() > 0) {
////                TemplateList templateList = results.get(0); //return first element
//////                fetchAllElementsInSublist(templateList); //NOT necessary since categoryList.getList() will fetch the items
//////                cache.put(templateList.getObjectIdP(), templateList); //may fetchFromCacheOnly by objectId via getOwner
//////                cache.put(TemplateList.CLASS_NAME, templateList);
////                cachePut(templateList); //may fetchFromCacheOnly by objectId via getOwner
////                result = true;
////            }
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////        return result;
////</editor-fold>
//        return getTemplateList(true);
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private final static String ALL_TEMPLATES_KEY = "ALL_TEMPLATES_KEY";
//    public TemplateList getTemplateList() {
//        return getTemplateList(false);
//    }
//
//    public TemplateList getTemplateList(boolean forceLoadFromParse) {
//        return getTemplateList(forceLoadFromParse, null, null);
//    }
//</editor-fold>
    public TemplateList getTemplateList() {
//        TemplateList templateList = null;
        List<TemplateList> results = null;
        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = query.find();
            int s = results.size();
            if (s > 0) {
                ASSERT.that(s <= 1, () -> "error: more than one TemplateList element (" + s + ")"); //TODO create error log for this 
                TemplateList templateList = results.get(0); //return first element
//                fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements  
//                cachePut(templateList);
//                return templateList;
                TemplateList cachedTemplateList = (TemplateList) cacheGet(templateList);
                if (cachedTemplateList != null) {
                    templateList = cachedTemplateList;
                } else {
//                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
                    fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements  
                    cachePut(templateList);
                }
                return templateList;
            } else { //if (results.size() == 0) 
                TemplateList templateList2 = new TemplateList();
//<editor-fold defaultstate="collapsed" desc="comment">
//                templateList.addAll(getAllTemplatesByQuery()); //must use addAll to set pre-existing templates
////                templateList.setList(getAllTemplatesByQuery());
////                if (false) fetchListElementsIfNeededReturnCachedIfAvail(templateList);
//                fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
//</editor-fold>
//                saveAndWait((ParseObject) templateList); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                cachePut(templateList); //cache list 
//                saveInBackground((ParseObject) templateList2, () -> cachePut(templateList2)); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
                saveInBackground((ParseObject) templateList2); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
                return templateList2;
            }
//            cache.put(templateList.getObjectIdP(), templateList);
//            cache.put(TemplateList.CLASS_NAME, templateList);
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchFromCacheOnly(templateList.getObjectIdP()); //use same object if already cached
////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
//        if (templateListTmp != null)
//            templateList = templateListTmp;
//        else {
//            fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
//            //        return TemplateList.getInstance();
//            cachePut(templateList); //cache list
//        }
//</editor-fold>
//        return templateList;
        assert false;
        return null;
    }

    public TemplateList getTemplateList(boolean forceLoadFromParse, Date startDate, Date endDate) {
        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");
        TemplateList templateList = null;
        List<TemplateList> results = null;
        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
        if (startDate != null) {
            query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
        }
        if (endDate != null) {
            query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            query.include(TemplateList.PARSE_ITEMLIST_LIST); //NO - fetches an additional copy of the templates
//        }//        query.selectKeys(null); //just get search result, no data (these are cached)
//        if (false) {
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached) //NOOO: gets an empty list
//        }
//        query.selectKeys(new ArrayList(Arrays.asList(TemplateList.PARSE_ITEMLIST_LIST))); //just get search result, no data (these are cached) //No need, no superflous data in this list
//</editor-fold>
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = query.find();
            int s = results.size();
            if (s > 0) {
                ASSERT.that(s <= 1, () -> "error: more than one TemplateList element (" + s + ")"); //TODO create error log for this 
                templateList = results.get(0); //return first element
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) fetchListElementsIfNeededReturnCachedIfAvail(templateList); //replace references to templates with instances from cache //NOT needed since first getList() will do this //optimization?!
//                cache.put(templateList.getObjectId(), templateList); //TODO not really needed?
//                cache.put(TemplateList.CLASS_NAME, templateList);
//</editor-fold>
                return templateList; //return first element
//                fetchListElementsIfNeededReturnCachedIfAvail(templateList);
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchFromCacheOnly(templateList.getObjectIdP()); //use same object if already cached
////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
//        if (templateListTmp != null)
//            templateList = templateListTmp;
//        else {
//            fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
//            //        return TemplateList.getInstance();
//            cachePut(templateList); //cache list
//        }
//</editor-fold>
        return null;
    }

//    public TimerInstance getTimerInstanceList(boolean forceLoadFromParse) {
    public List<TimerInstance> getTimerInstanceList() {
//        if (!forceLoadFromParse && (timerStack = (TimerInstance) cacheGet(TemplateList.CLASS_NAME)) != null) {
//            return timerStack;
//        }
        List<TimerInstance> results = null;
        ParseQuery<TimerInstance> query = ParseQuery.getQuery(TimerInstance.CLASS_NAME);
//        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
//        query.orderByDescending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    /**
     * get a named list, used for special lists like "Inbox" which are NOT
     * stored in ItemListList
     *
     * @param name
     * @return
     */
    public ItemList getSpecialNamedItemListFromParse(String name, String visibleName) {
//        if (cache.get(name) != null) {
//            return (ItemList) cache.get(cache.get(name)); //cahce: name -> objectIdP
//        } else {
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
        query.whereEqualTo(ItemList.PARSE_TEXT, name);
        query.whereDoesNotExist(ItemList.PARSE_OWNER); //only get lists that do not belong to the ItemListList
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.whereExists(ItemList.PARSE_SYSTEM_LIST);
        List<ItemList> results = null;
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        int s = results.size();
        ASSERT.that(results.size() <= 1, () -> "too many lists (" + s + ") with reserved name: " + name);
        if (s > 0) {
            results.get(0).setText(visibleName);
//            cache.put(name, results.get(0).getObjectIdP());
            return results.get(0);
        } else {
            return null;
        }
//        }
    }

    public ItemList getInbox(String name, String visibleName) {
//        if (!forceFromParse && (inbox = (Inbox) cacheGet(Inbox.CLASS_NAME)) != null) {
//            return inbox;
//        } //NO need for caching since this is done in the instance/singleton
        if (cache.get(name) != null) {
            return (ItemList) cache.get(cache.get(name)); //cahce: name -> objectIdP
        } else {
            ItemList inbox = getSpecialNamedItemListFromParse(name, visibleName);
            if (inbox == null) { //if no Inbox already saved, initialize it with existing motherless tasks
                ItemList newInbox = new ItemList();
                newInbox.setSystemList(true);
                newInbox.setText(name);
                List itemsWithoutOwners = getAllItemsWithoutOwners();
                for (Item item : (List<Item>) itemsWithoutOwners) {
                    newInbox.addToList(item);
                }
//                saveAndWait((ParseObject) inbox); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
                saveInBackground((ParseObject) newInbox, () -> {
                    cache.put(name, newInbox.getObjectIdP());
                    if (false) {
                        cachePut(newInbox); //may fetchFromCacheOnly by objectId via getOwner
                    }
                }); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
                if (false) {
                    saveInBackground(itemsWithoutOwners); //save all items who now have their Inbox owner assigned
                }//                fetchListElementsIfNeededReturnCachedIfAvail(inbox);
//            saveInBackground((ParseObject)inbox); //always save so new lists can be assigned to it
//            saveAndWait((ParseObject)inbox); //always save so new lists can be assigned to it
                return newInbox;
            } else {
                cache.put(name, inbox.getObjectIdP());
                cachePut(inbox); //may fetchFromCacheOnly by objectId via getOwner
                return inbox;
            }
        }
    }

    public final static String OVERDUE = "Overdue";
    public final static String TODAY = "Today";
    public final static String NEXT = "Next";
    public final static String LOG = "Log";
    public final static String DIARY = "Diary";
    public final static String TOUCHED = "Touched";
    private final static String[] RESERVED_LIST_NAMES = {OVERDUE, TODAY, NEXT, LOG, DIARY, TOUCHED};//new ArrayList

//    public ItemList getNamedItemList(String name) {
//        return getNamedItemList(name, name); //use fixed name as default
//    }
//
//    public ItemList getNamedItemList(String name, String visibleName) {
//        return getNamedItemList(name, visibleName, null);
//    }
    public ItemList getNamedItemList(String name, String visibleName, FilterSortDef filterSortDef) {
        ParseObject temp = null;
//<editor-fold defaultstate="collapsed" desc="comment">
//        String objId;
//        if (filterSortDef != null) {
//            ((ItemList) temp).setFilterSortDef(filterSortDef);
//            saveInBackground(temp);
//        }
//</editor-fold>
        if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) { //temp.getUpdatedAt() == null: in case it hasn't been saved to Parse server yet
//           //temp is cached and updated today
            return (ItemList) temp;
        } else {
            // !((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) <=>
            // ((temp = cacheGet(name)) == null || !(temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) <=>
            // ((temp = cacheGet(name)) == null || (temp.getUpdatedAt() != null && !MyDate.isToday(temp.getUpdatedAt())))
//            List<ItemAndListCommonInterface> updatedList;
            List updatedList = null;
            switch (name) {
                case OVERDUE:
                    updatedList = getOverdue();
//<editor-fold defaultstate="collapsed" desc="comment">
////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
//////                    return (ItemList) temp;
////                    } else {
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getOverdue()); //refresh the list (important to just update existing list so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getOverdue());
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (filterSortDef != null) {
////                            saveInBackground(filterSortDef);
////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
////                        }
////                        saveAndWait(temp); //NB. MUST be saveAndWait so an pbjectId is creating before caching based on on name
////                        cache.put(name, temp);
////                    return (ItemList) temp;
////</editor-fold>
//                    }
//</editor-fold>
//                    }
                    break; //unreachable statement!!
                case TODAY:
                    updatedList = getToday();
//<editor-fold defaultstate="collapsed" desc="comment">
////                if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt())))
////                    if ((temp = cacheGet(name)) != null && (MyDate.isToday(temp.getUpdatedAt()))) {
//////                    return (ItemList) temp;
////                    } else {
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getToday()); //refresh the list (important so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getToday());
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (filterSortDef != null) {
////                            saveInBackground(filterSortDef);
////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
////                        }
//////                    saveInBackground(temp);
////                        //optimization: does this in background:
////                        saveAndWait(temp); //NB! MUST use saveAndWait, so the list is stored in Cache with an updatedAt date to make the check above work correctly!!
////                        cache.put(name, temp);
////                    return (ItemList) temp;
////</editor-fold>
//                    }
////                    }
//</editor-fold>
                    break; //unreachable statement!!
                case NEXT:
                    updatedList = getCalendar();
//<editor-fold defaultstate="collapsed" desc="comment">
////<editor-fold defaultstate="collapsed" desc="comment">
////                if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt())))
////                    return (ItemList) temp;
////                else
////                    temp = new ItemList(visibleName, getCalendar());
////                if (temp == null) {
////                    temp = new ItemList(visibleName, false);
////                    saveInBackground(temp);
////                    cache.put(name, temp);
////                } else {
////                    cache.put(name, temp);
////                }
////                return (ItemList) temp;
////
////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
//////                    return (ItemList) temp;
////                    } else {
////</editor-fold>
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getCalendar()); //refresh the list (important so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getCalendar());
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (filterSortDef != null) {
////                            saveInBackground(filterSortDef);
////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
////                        }
////                        saveAndWait(temp);
////                        cache.put(name, temp);
//////                    return (ItemList) temp;
////</editor-fold>
//                    }
////                    }
//</editor-fold>
                    break; //unreachable statement!!
                case LOG:
                    updatedList = getCompletionLog();
//<editor-fold defaultstate="collapsed" desc="comment">
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
//////                    return (ItemList) temp;
////                    } else {
////</editor-fold>
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getCompletionLog()); //refresh the list (important so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getCompletionLog());
////<editor-fold defaultstate="collapsed" desc="comment">
////                        if (filterSortDef != null) {
////                            saveInBackground(filterSortDef);
////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
////                        }
////                        saveAndWait(temp);
////                        cache.put(name, temp);
////</editor-fold>
//                    }
//                    }
//</editor-fold>
                    break;
                case DIARY:
                    updatedList = getCreationLog();
//<editor-fold defaultstate="collapsed" desc="comment">
////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
////                    } else {
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getCreationLog()); //refresh the list (important so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getCreationLog());
//                        saveAndWait(temp);
//                        cache.put(name, temp);
//                    }
////                    }
//</editor-fold>
                    break;
                case TOUCHED:
                    updatedList = getTouchedLog();
//<editor-fold defaultstate="collapsed" desc="comment">
////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
////                    } else {
//                    if (temp != null) {
//                        ((ItemAndListCommonInterface) temp).setList(getTouchedLog()); //refresh the list (important so eg Timer continues on same list!!)
//                        saveInBackground(temp);
//                    } else {
//                        temp = new ItemList(visibleName, getTouchedLog());
//                        saveAndWait(temp);
//                        cache.put(name, temp);
//                    }
////                    }
//</editor-fold>
                    break;
//                default:
//                    break;
            }
//            if (temp.getObjectIdP() == null) { //new created list
            if (temp == null) { //create new list (only done on first use or after refreshing cache)
                temp = new ItemList(visibleName, getTouchedLog());
                ((ItemList) temp).setSystemList(true);
                ((ItemList) temp).setList(updatedList);
                if (filterSortDef != null) {
                    saveInBackground(filterSortDef);
                    ((ItemList) temp).setFilterSortDef(filterSortDef);
                }
//                saveAndWait(temp); //NB. MUST be saveAndWait so an objectId is created before caching based on name 
//                cache.put(name, temp);
                ParseObject lambdaCopy = temp;
//                saveInBackground(temp, () -> cache.put(name, lambdaCopy)); //NB. MUST be saveAndWait so an objectId is created before caching based on name 
                saveInBackground(temp); //NB. MUST be saveAndWait so an objectId is created before caching based on name -> now done in backgroundsavethread after call to saveInBackground
//                cache.put(name, temp);
            } else {
                ((ItemAndListCommonInterface) temp).setList(updatedList); //refresh the list (important to just update existing list so eg Timer continues on same list!!)
                saveInBackground(temp);
            }

//            if (temp != null) {
//                return (ItemList) temp;
//            } else {
//                ASSERT.that("error: unknown type of named list,name=" + name + ", visibleName=" + visibleName);
//                return null;
//            }
            if (temp == null) {
                ASSERT.that("error: unknown type of named list,name=" + name + ", visibleName=" + visibleName);
            }
            return (ItemList) temp;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            ItemList inbox = getSpecialNamedItemListFromParse(name, visibleName);
//            if (inbox == null) {
//                //if no Inbox already saved, initialize it with existing categories
//                inbox = new ItemList();
//                inbox.setText(name);
//                List itemWithoutOwners = getAllItemsWithoutOwners();
//                for (Item item : (List<Item>) itemWithoutOwners) {
//                    inbox.addToList(item);
//                }
//                saveAndWait((ParseObject) inbox); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
//                saveInBackground(itemWithoutOwners); //save all items who now have their Inbox owner assigned
////                fetchListElementsIfNeededReturnCachedIfAvail(inbox);
////            saveInBackground((ParseObject)inbox); //always save so new lists can be assigned to it
////            saveAndWait((ParseObject)inbox); //always save so new lists can be assigned to it
//            }
//            cachePut(inbox); //may fetchFromCacheOnly by objectId via getOwner
//            return inbox;
//        }
//</editor-fold>
    }

//    private final static String ALL_TEMPLATES_KEY = "ALL_TEMPLATES_KEY";
    public List<Item> getTopLevelTemplatesFromParse() {
        List<Item> results = null;
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //only top-level templates
        query.whereExists(Item.PARSE_TEMPLATE);

//        query.whereExists(Item.PARSE_TEMPLATE); //fetch only templates
//        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all subtasks
//        query.orderByDescending(Item.PARSE_TEXT); //order alphabetically
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        setupItemQueryNoTemplatesLimit1000(query);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        try {
            results = query.find();
//            cacheList(results);
//            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return fetchListElementsIfNeededReturnCachedIfAvail(results);
    }

//    private final static String ALL_TEMPLATES_KEY = "ALL_TEMPLATES_KEY";
    public List<Item> getAllTemplateTasksFromParseXXX() {
        List<Item> results = null;
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //only top-level templates
        query.whereExists(Item.PARSE_TEMPLATE);

        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        try {
            results = query.find();
//            cacheList(results);
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return new ArrayList();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Category> getAllCategoriesOld() {
//        List<Category> results = null;
//        ParseQuery<Category> query2 = ParseQuery.getQuery(Category.CLASS_NAME);
////        query2.include(Category.PARSE_TEXT);
////        query2.include(Category.PARSE_TEXT); //can only include another object, not a field
//        query2.include(Category.PARSE_ITEMLIST); //get all tasks in all categories //TODO optimize this (implement local storage)
//        try {
//            results = query2.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
////        return (List<Category>) getAll(Category.CLASS_NAME);
//    }
//</editor-fold>
    ParseBatch batchToFetchAllLevelsOfHierarchyInOneGo;

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void fetchAllItemsInNOT_WORKING(Object itemOrItemListOrCategoryOrList, boolean recursively) {
//        batchToFetchAllLevelsOfHierarchyInOneGo = ParseBatch.create();
//        fetchAllItemsInImplNOT_WORKING(itemOrItemListOrCategoryOrList, recursively);
//        try {
//            if (!batchToFetchAllLevelsOfHierarchyInOneGo.execute()) {
//                Log.p("Error when batch-fetching hierarchy of stuff for object=" + itemOrItemListOrCategoryOrList + ", recursively=" + recursively);
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void fetchAllItemsInImplNOT_WORKING(Object itemOrItemListOrCategoryOrList, boolean recursively) {
//        try {
////            ParseBatch batch = ParseBatch.create();
//            //TODO!!! find more efficient way to fetchFromCacheOnly all the objects - use ParseBatch once switched to new version of parse4cn1!!
//
//            if (itemOrItemListOrCategoryOrList instanceof ParseObject) {
////                 if ((temp = cache.get(((ParseObject) itemOrListOrCategory).getObjectId())) != null) {
////            return temp;
////        }
//                ((ParseObject) itemOrItemListOrCategoryOrList).fetchIfNeeded();
//                cache.put(((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP(), itemOrItemListOrCategoryOrList);
//            }
//
//            List<ParseObject> list = null;
//            if (itemOrItemListOrCategoryOrList instanceof Item) {
//                list = ((Item) itemOrItemListOrCategoryOrList).getList();
//            } else if (itemOrItemListOrCategoryOrList instanceof Category) {
//                list = ((Category) itemOrItemListOrCategoryOrList).getList();
//            } else if (itemOrItemListOrCategoryOrList instanceof ItemList) {
//                list = ((ItemList) itemOrItemListOrCategoryOrList).getList();
//            } else if (itemOrItemListOrCategoryOrList instanceof List) {
//                list = (List) itemOrItemListOrCategoryOrList;
//            } else {
//                assert false : "unknow type of element to fetch=" + itemOrItemListOrCategoryOrList;
//            }
//
////            ParseBatch batch = ParseBatch.create();
//            ParseObject temp;
////            for (ParseObject item : list) {
//            for (int i = 0, size = list.size(); i < size; i++) {
////                ((Item) item).fetchIfNeeded();
////                item.fetchIfNeeded();
////                fetchIfNeeded(item);
//                if ((temp = (ParseObject) cache.get(list.get(i).getObjectIdP())) != null) {
//                    list.set(i, temp); //NB! will possibly replace the parseObjects in the list with cached ones
//                } else {
//                    batchToFetchAllLevelsOfHierarchyInOneGo.addObject(list.get(i), ParseBatch.EBatchOpType.UPDATE); //NO BATCH operation to fetchFromCacheOnly (fetchAllIfNeeded exists but requires merge: https://github.com/ParsePlatform/parse-php-sdk/issues/75)
//                }
////                list.set(i, fetchIfNeeded(list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
//                if (recursively) {
//                    fetchAllItemsInImplNOT_WORKING(list.get(i), recursively);
//                }
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
//</editor-fold>
    public void getAllItemsInCategory(Category category) {
        try {
            category.fetchIfNeeded();
            List<Item> list = category.getListFull();
            //TODO!!! find more efficient way to fetchFromCacheOnly all the objects
            for (int i = 0, size = list.size(); i < size; i++) {
                list.set(i, (Item) fetchIfNeededReturnCachedIfAvail(list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
//            for (Object item : category.getList()) {
//                ((Item) item).fetchIfNeeded();
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
    }

    /**
     * ensures all the right includes are set when fetching an Item. NEVER
     * returns an items marked as templates.
     *
     * @param query
     */
    private static void setupItemQueryNotTemplateNotDeletedLimit10000(ParseQuery<Item> query) {
        setupItemQueryNotTemplateNotDeletedLimit10000(query, false);
    }

    private static void setupItemQueryNotTemplateNotDeletedLimit10000(ParseQuery<Item> query, boolean excludeDoneAndCancelled) {
        assert query.getClassName().equals(Item.CLASS_NAME) : "only use this on queries to retrieve Items";
//        //TODO are the below includes really necessary? Or add additional includes to actually fetchFromCacheOnly needed info like Category names?
//        query2.include(Item.PARSE_SUBTASKS); //
//        query2.include(Item.PARSE_CATEGORIES);
//        query2.include(Item.PARSE_OWNER_ITEM); //ensure we fetchFromCacheOnly the owner (eg for drag & drop)
//        query2.include(Item.PARSE_OWNER_LIST); //ensure we fetchFromCacheOnly the ownerList (eg for drag & drop)
//        query2.include(Item.PARSE_REPEAT_RULE); //TODO!!! cache all ParseRules and access that instead!
        query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //don't fetchFromCacheOnly any deleted items
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());

        if (excludeDoneAndCancelled) {
            query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        }
    }

    /**
     * gets all Items but no templates
     *
     * @return
     */
    public List<Item> getAllItems() {
        return getAllItems(false);
    }

    /**
     * return all and every task (including projects and subprojects)
     *
     * @param includeTemplates
     * @return
     */
    public List<Item> getAllItems(boolean includeTemplates) {
        return getAllItems(includeTemplates, false, false);
    }

//    public List<Item> getAllItems(boolean includeTemplates, boolean onlyLeafTasks) {
//        return getAllItems(includeTemplates, onlyLeafTasks, false);
//    }
    public List<Item> getAllItems(boolean includeTemplates, boolean onlyLeafTasks, boolean onlyWithoutOwner) {
        return getAllItems(includeTemplates, onlyLeafTasks, onlyWithoutOwner, false);
    }

    public List<Item> getAllItems(boolean includeTemplates, boolean onlyLeafTasks, boolean onlyWithoutOwner, boolean fetchFromScratch) {

        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        if (!includeTemplates) {
            query.whereDoesNotExist(Item.PARSE_TEMPLATE); //exclude if has subtasks
        }
        if (onlyLeafTasks) {
            query.whereDoesNotExist(Item.PARSE_SUBTASKS); //exclude if has subtasks
        }//        query2.include(Item.PARSE_TEXT);
        if (onlyWithoutOwner) {
            query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude if has subtasks
            query.whereDoesNotExist(Item.PARSE_OWNER_LIST); //exclude if has subtasks
            query.whereDoesNotExist(Item.PARSE_OWNER_TEMPLATE_LIST); //exclude if has subtasks
        }
//        query2.include(Item.PARSE_SUBTASKS);
//        query2.include(Item.PARSE_CATEGORIES);
//        query2.include(Item.PARSE_OWNER_ITEM); //ensure we fetchFromCacheOnly the owner (eg for drag & drop)
//        query2.include(Item.PARSE_OWNER_LIST); //ensure we fetchFromCacheOnly the ownerList (eg for drag & drop)
//        setupStandardItemQuery(query2);
//        query2.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all subtasks
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<Item> results = null;
        try {
            results = query.find();
//            if (results != null) {
//                for (ParseObject o : results) {
//                    if (o.isDataAvailable()) {
//                        cache.put(o.getObjectId(), o);
//                    }
//                }
//            }
            if (false) {
                cacheList(results); //DON'T cache here
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
//        if (fetchFromScratch) {
//            cacheList(results);
//        } else {
        fetchListElementsIfNeededReturnCachedIfAvail(results);
//        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    /**
     * gets all Items but no templates
     *
     * @return
     */
    public List<Item> getAllItemsWithoutOwners() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);
        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM);
        query.whereDoesNotExist(Item.PARSE_OWNER_LIST);
        query.whereDoesNotExist(Item.PARSE_OWNER_TEMPLATE_LIST);
        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        List<Item> results = null;
        try {
            results = query.find();
//            for (ParseObject o : results) {
//                if (o.isDataAvailable()) {
//                    cache.put(o.getObjectId(), o);
//                }
//            }
//            cacheList(results);
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    public List<Item> getAllItemsForRepeatRule(RepeatRuleParseObject repeatRule) {
        if (repeatRule != null && repeatRule.getObjectIdP() != null) {
            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupItemQueryNoTemplatesLimit1000(query);
            query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//            query.whereEqualTo(Item.PARSE_REPEAT_RULE, repeatRule.getObjectId());
            query.whereEqualTo(Item.PARSE_REPEAT_RULE, repeatRule);
            query.orderByDescending(Item.PARSE_DUE_DATE);
            query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
            List<Item> results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
        return new ArrayList();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<WorkSlot> getAllIWorkSlotsForRepeatRuleXXX(RepeatRuleParseObject repeatRule) {
//        if (repeatRule != null && repeatRule.getObjectIdP() != null) {
//            ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//            query.whereEqualTo(WorkSlot.PARSE_REPEAT_RULE, repeatRule.getObjectIdP());
//            query.orderByDescending(WorkSlot.PARSE_START_TIME);
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//            List<WorkSlot> results = null;
//            try {
//                results = query.find();
//                fetchAllElementsInSublist(results);
//                return results;
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
//        return new ArrayList();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> debugGetItemsInMultipleListsXXX() {
//        ParseQuery<Item> query2 = ParseQuery.getQuery(Item.CLASS_NAME);
////        query2.include(Item.PARSE_TEXT);
////        query2.include(Item.PARSE_SUBTASKS);
////        query2.include(Item.PARSE_CATEGORIES);
////        query2.include(Item.PARSE_OWNER_ITEM); //ensure we fetchFromCacheOnly the owner (eg for drag & drop)
////        query2.include(Item.PARSE_OWNER_LIST); //ensure we fetchFromCacheOnly the ownerList (eg for drag & drop)
//        setupStandardItemQuery(query2);
////        query2.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all subtasks
//        query2.orderByDescending(Item.PARSE_UPDATED_AT);
//        List<Item> results = null;
//        try {
//            results = query2.find();
//            for (ParseObject o : results) {
//                if (o.isDataAvailable()) {
//                    cache.put(o.getObjectId(), o);
//                }
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    /**
     * gets all top-level templates
     *
     * @return
     */
    private List<Item> getAllTemplatesByQuery() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQuery(query2);
        if (false) {
            query.include(Item.PARSE_SUBTASKS);
            query.include(Item.PARSE_CATEGORIES);
            query.include(Item.PARSE_OWNER_ITEM); //ensure we fetchFromCacheOnly the owner (eg for drag & drop)
        }
//        query2.include(Item.PARSE_OWNER_LIST); //ensure we fetchFromCacheOnly the ownerList (eg for drag & drop)
        query.whereExists(Item.PARSE_TEMPLATE); //fetch only templates
        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all template subtasks
        query.orderByDescending(Item.PARSE_TEXT); //order alphabetically
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        List<Item> results = null;
        try {
            results = query.find();
//<editor-fold defaultstate="collapsed" desc="comment">
//            for (ParseObject o : results) {
//            for (int i = 0, size = results.size(); i < size; i++) {
//                Item item = results.get(i);
//                Item temp;
////                if (item.isDataAvailable()) { //WILL always be the case for the items
////                    cache.put(item.getObjectId(), item);
////                } else
//                if ((temp = (Item) cache.get(item.getObjectId())) != null) {
//                    results.set(i, temp);
//                } else {
////                    item = (Item) fetchIfNeeded(item);
//                    fetchIfNeeded(item);
////                    item = (Item) fetchIfNeededReturnCachedIfAvail(item);
////                    cache.put(item.getObjectId(), item); //cached in fetchedIfNeeded()
//                }
//            }
//</editor-fold>
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    /**
     * only returns top-level projects (not subprojects)
     *
     * @return
     */
    public List<Item> getAllProjects() {
        return getAllProjects(false);
    }

    public List<Item> getAllProjects(boolean includeSubProjects) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query2.include(Item.PARSE_TEXT);
//        query2.include(Item.PARSE_SUBTASKS);
//        query2.include(Item.PARSE_CATEGORIES);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
        query.whereExists(Item.PARSE_SUBTASKS); //include if has subtaskss
        if (!includeSubProjects) {
            query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude if owned by another item (is a subtasks)
        }
        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //exclude Done if has subtaskss
        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
        query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates

        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        List<Item> results = null;
        try {
            results = query.find();
//            for (ParseObject o : results) {
//                if (o.isDataAvailable()) {
//                    cache.put(o.getObjectId(), o);
//                }
//            }
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    /**
     * returns completed tasks by date completed
     *
     * @return
     */
    public List<Item> getCompletionLog() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss

        query.orderByDescending(Item.PARSE_COMPLETED_DATE);
        query.whereGreaterThanOrEqualTo(Item.PARSE_COMPLETED_DATE, new Date(MyDate.currentTimeMillis() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    /**
     * returns completed tasks from and including startDate up to and including
     * endDate
     *
     * @return
     */
    public List<Item> getCompletedItems(Date startDate, Date endDate) {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss

//        query.orderByDescending(Item.PARSE_COMPLETED_DATE);
        query.orderByAscending(Item.PARSE_COMPLETED_DATE);
        query.whereGreaterThanOrEqualTo(Item.PARSE_COMPLETED_DATE, startDate);
        query.whereLessThanOrEqualTo(Item.PARSE_COMPLETED_DATE, endDate);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    public List<Item> getDeletedItems() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        query.orderByAscending(Item.PARSE_UPDATED_AT);
        query.whereExists(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    /**
     * returns completed tasks by date completed
     *
     * @return
     */
    public List<Item> getTouchedLog() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query2.include(Item.PARSE_TEXT);
//        query2.include(Item.PARSE_SUBTASKS);
//        query2.include(Item.PARSE_CATEGORIES);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
//        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, new Date(MyDate.currentTimeMillis() - MyPrefs.touchedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
//        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, MyDate.getStartOfToday()); //NOT needed since cannot be touched in future
        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    public List<Item> getTouched24hLog() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
//        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, new Date(MyDate.currentTimeMillis() - MyDate.DAY_IN_MILLISECONDS));
//        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, MyDate.getStartOfToday()); //NOT needed since cannot be touched in future
        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    /**
     * returns list of all tasks by date created (should be grouped/collapsed by
     * e.g. week or month)
     *
     * @return
     */
    public List<Item> getCreationLog() {
        //TODO!!! implement getting in batches of less than 1000
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query2.include(Item.PARSE_TEXT);
//        query2.include(Item.PARSE_SUBTASKS);
//        query2.include(Item.PARSE_CATEGORIES);
//        query.setLimit(QUERY_LIMIT);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);
        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

        query.orderByDescending(Item.PARSE_CREATED_AT);
        query.whereGreaterThanOrEqualTo(Item.PARSE_CREATED_AT, new Date(MyDate.currentTimeMillis() - MyPrefs.creationLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemListList getItemListListXXX(boolean forceLoadFromParse, Date startDate, Date endDate) {
//        ItemListList itemListList = null;
//        if (!forceLoadFromParse && (itemListList = (ItemListList) cacheGet(ItemListList.CLASS_NAME)) != null) {
//            return itemListList;
//        }
//        List<ItemListList> results;// = null;
////        ParseUser parseUser = ParseUser.getCurrent();
////        if (parseUser != null) {
//        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
//        if (startDate != null) {
//            query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
//        }
//        if (endDate != null) {
//            query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
//        }
////        query.setLimit(1); //if ever there is more than one list, get only first one
//        if (false) {
//            query.selectKeys(new ArrayList(Arrays.asList(ItemListList.PARSE_ITEMLIST_LIST))); //just get search result, no data (these are cached) //NOOO: gets an empty list
//        }//<editor-fold defaultstate="collapsed" desc="comment">
////            query.include(ItemListList.PARSE_ITEMLIST_LIST);
////            query.include(ItemListList.PARSE_ITEMLIST_LIST + "." + ItemList.CLASS_NAME); //TODO!!!! not necessary to fetchFromCacheOnly this since coming from cache
////            query.include(ItemListList.PARSE_ITEMLIST_LIST + "." + ItemList.CLASS_NAME + "." + Item.CLASS_NAME);
////            if (false) {
////                query.include(ItemListList.PARSE_ITEMLIST + "." + ItemList.CLASS_NAME); //TODO!!!! not necessary to fetchFromCacheOnly this since coming from cache
////                query.include(ItemListList.PARSE_ITEMLIST + "." + ItemList.CLASS_NAME + "." + Item.CLASS_NAME);
////            }
////            query.include(ItemListList.PARSE_ITEMLIST_LIST+"."+ItemList.PARSE_ITEMLIST+"."+Item.PARSE_STATUS);
////            query.include(CategoryList.PARSE_CATEGORY_LIST + "." + Category.PARSE_TEXT); //get categories and their name field
////</editor-fold>
//        try {
//            results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
////        if (results != null) {
//            if (results.size() > 0) {
//                ASSERT.that(results.size() <= 1, () -> "error: more than one ItemItemList element (" + results.size() + ")");
//                itemListList = results.get(0); //return first element
////                fetchAllElementsInSublist(itemListList); //NOT necessary since categoryList.getList() will fetch the items
////                fetchListElementsIfNeededReturnCachedIfAvail(itemListList);
////                    return itemListList;
//            } else { //if (results.size() == 0) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    //first time only
////                    //TODO!!! remove this code since shouldn't be necessary anymore
////                    List<ItemListList> itemLists = null;
////                    ParseQuery<ItemListList> query2 = ParseQuery.getQuery(ItemList.CLASS_NAME);
////                    try {
////                        itemLists = query2.find();
////                    } catch (ParseException ex) {
////                        Log.e(ex);
////                    }
////</editor-fold>
//                itemListList = new ItemListList();
//                itemListList.setList(getAllItemListsFromParse());
////                    itemListList.setList(itemLists);
//////                if (itemLists.size() > 0) {
//                getInstance().save(itemListList); //always save so new lists can be assigned to it
////                    cache.put(ItemListList.CLASS_NAME, itemListList);
//////                }
//            }
////            cache.put(itemListList.getObjectIdP(), itemListList); //may fetchFromCacheOnly by objectId via getOwner
////            cache.put(ItemListList.CLASS_NAME, itemListList);
//            cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        } else {
////            itemListList = null;
////        }
////        return itemListList;
////</editor-fold>
//        return itemListList;
////        return (List<Category>) getAll(Category.CLASS_NAME);
//    }
//</editor-fold>
    public List<RepeatRuleParseObject> getAllRepeatRulesFromParse() {
//        List<ItemList> results = null;
        ParseQuery<RepeatRuleParseObject> query = ParseQuery.getQuery(RepeatRuleParseObject.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadAfterThisDate);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpToAndIncludingThisDate);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            List<RepeatRuleParseObject> results = query.find();
//            cacheList(results);
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
//        return null;
        return new ArrayList();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<ItemList> getAllItemListsOld() {
//        ParseQuery<ItemList> query2 = ParseQuery.getQuery(ItemList.CLASS_NAME);
////        query2.include(Item.PARSE_TEXT);
//        query2.include(ItemList.PARSE_ITEMLIST);
//        List<ItemList> results = null;
//        try {
//            results = query2.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    public void deleteCategoryFromAllItemsXXXNOT_NECESSARY(Category cat) {
//        try {
//            cat.fetchIfNeeded();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereContainedIn(Item.PARSE_CATEGORIES, cat);
        query.whereEqualTo(Item.PARSE_CATEGORIES, cat); //TODO!!: right expression to get all items with cat in Categories??
        query.include(Item.PARSE_CATEGORIES);
//        query.include(Item.PARSE_CATEGORIES+"."+Category.PARSE_TEXT); //only fetchFromCacheOnly category name
        List<Item> results = null;
        try {
            results = (List<Item>) query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        for (Item item : results) {
//            Set<Category> cats = item.getCategories();
//            List<Category> itemCats = item.getCategories();
//            itemCats.remove(cat);
//            item.setCategories(itemCats);
            item.removeCategoryFromItem(cat, false);
            try {
                item.save();
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
//        cache.delete(cat.getObjectIdP());
        cacheDelete(cat);
//        return (List<Item>) getAll(Item.CLASS_NAME);
    }

    public int deleteAll(String ParseClassName) {
//        final int BATCH_SIZE = 500;
        Log.p("DELETING ALL " + ParseClassName);
        final int BATCH_SIZE = 1000000;
        int skip = 0;
        int deletedObjectsCount = 0;
        List<ParseObject> results = null;
//        do {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassName);
        Set<String> targetKeys = new HashSet<String>();
//            targetKeys.add("playerName");
        query.selectKeys(targetKeys); // will this avoid retrieving any data but just the objects themselves??
//            query.setLimit(BATCH_SIZE).setSkip(skip);
        query.setLimit(BATCH_SIZE);
//            skip = skip + BATCH_SIZE;
        try {
            results = (List<ParseObject>) query.find();
            // Run a batch to persist the objects to Parse
            ParseBatch batch = ParseBatch.create();
//            for (ParseObject obj : results) {
//                batch.addObject(obj, ParseBatch.EBatchOpType.DELETE);
//            }
            batch.addObjects(results, EBatchOpType.DELETE);
            if (batch.execute()) {
                // Batch operation succeeded
                deletedObjectsCount += results.size();
            } else {
                Log.p("deleteAll(" + ParseClassName + ") batch.execute failed, results.size()=" + results.size());
//                    return 0;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        Log.p("  elements deleted " + deletedObjectsCount);

//        } while (results.size() == BATCH_SIZE);
        return deletedObjectsCount;
    }

    public int deleteAllUserDataOnParseServerCannotBeUndone(boolean removeUserAccount) {
        int deletedObjectsCount = 0;
        deletedObjectsCount += deleteAll(Category.CLASS_NAME);
        deletedObjectsCount += deleteAll(CategoryList.CLASS_NAME);
        deletedObjectsCount += deleteAll(FilterSortDef.CLASS_NAME);
        deletedObjectsCount += deleteAll(Item.CLASS_NAME);
        deletedObjectsCount += deleteAll(ItemList.CLASS_NAME);
        deletedObjectsCount += deleteAll(ItemListList.CLASS_NAME);
        deletedObjectsCount += deleteAll(RepeatRuleParseObject.CLASS_NAME);
        deletedObjectsCount += deleteAll(TemplateList.CLASS_NAME);
        deletedObjectsCount += deleteAll(TimerInstance.CLASS_NAME);
        deletedObjectsCount += deleteAll(WorkSlot.CLASS_NAME);
        deletedObjectsCount += deleteAll(WorkSlot.CLASS_NAME);
        ParseUser parseUser = ParseUser.getCurrent();
        if (removeUserAccount)
            try {
            parseUser.delete();
            deletedObjectsCount++;
        } catch (ParseException ex) {
            Log.p("could not delete user");
        }
        //TODO!!!! update to cover all classes
        return deletedObjectsCount;
    }

    public boolean deleteAllLocalStorage() {
        Storage.getInstance().clearStorage();
        Storage.getInstance().clearCache();
        return true;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void deleteItemFromAllCategoriesXXX(Item item) {
//        if (true) {
//
//        } else {
//            ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
////        query.whereContainedIn(Category.PARSE_ITEMLIST, (new ArrayList().add(item)));
////        query.whereContainedIn(Category.PARSE_ITEMLIST, Arrays.asList(new Item[]{item}));
////        query.whereContainedIn(Category.PARSE_ITEMLIST, Arrays.asList(item));
//            query.whereEqualTo(Category.PARSE_ITEMLIST, item);
////        query.include(Category.PARSE_ITEMLIST); //shouldn't be necessary
//            List<Category> results = null;
//            try {
//                results = (List<Category>) query.find();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//            for (Category cat : results) {
////            cat.getInternalList().remove(cat);
//                cat.removeItemFromCategory(item, false);
////            try {
////                cat.save();
//                save(cat);
////            } catch (ParseException ex) {
////                Log.e(ex);
////            }
//            }
//        }
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    public List<Category> getAllCategoriesContainingItemXXXNOT_NECESSARY(Item item) {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
        query.whereEqualTo(Category.PARSE_ITEMLIST, item);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<Category> results = null;
        try {
            List<Category> results = query.find();
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> getAllItemsOwnedBy(ItemList itemList) {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQuery(query);
//
//        query.whereEqualTo(Item.PARSE_OWNER_LIST, itemList);
//        List<Item> results = null;
//        try {
//            results = (List<Item>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> getAllItemsOwnedBy(Item item) {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQuery(query);
//        query.whereEqualTo(Item.PARSE_OWNER_ITEM, item);
//        List<Item> results = null;
//        try {
//            results = (List<Item>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    List<Item> getAllItemsBelongingToCategory(Category category) {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereEqualTo(Item.PARSE_OWNER_ITEM, category);
//        List<Item> results = null;
//        try {
//            results = (List<Item>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
    /**
     * /**
     * return a list of all ItemLists that has this ParseObject included in
     * source lists
     *
     * @param someIncludedParseObject
     * @return
     */
    public List<ItemList> getAllItemListsIncludingThis(ParseObject someIncludedParseObject) {
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
        query.whereEqualTo(ItemList.PARSE_SOURCE_LISTS, someIncludedParseObject);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<ItemList> results = null;
        try {
            results = (List<ItemList>) query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    /**
     * return a list of all categories that has this subCategory as sub-category
     * (included in source lists)
     *
     * @param subCategory
     * @return
     */
    public List<Category> getAllCategoriesIncludingThis(Category subCategory) {
        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
        query.whereEqualTo(Category.PARSE_SOURCE_LISTS, subCategory);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<Category> results = null;
        try {
            results = (List<Category>) query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * returns item which has timer running, or null if none. Throws a runtime
//     * error if more than one item have a timer running.
//     *
//     * @return
//     */
//    public Item getItemWithTimerRunning() {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereExists(Item.PARSE_TIMER_STARTED);
//        List<Item> results = null;
//        try {
//            results = (List<Item>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        if (results.size() > 1) {
//            throw new RuntimeException("More than one item has a timer running: " + results);
//        }
//        if (results != null && results.size() > 0) {
//            return results.get(0);
//        } else {
//            return null;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * save anyParseObject.
     *
     * @param anyParseObject
     * @param saveToCache if true save anyParseObject to cache
     */
//    private void saveImplXXX(ParseObject anyParseObject, boolean saveToCache) {
//        Log.p("SAVE-DAO.saveImpl, obj=" + anyParseObject + " saveToCache=" + saveToCache);
//        if (anyParseObject != null && anyParseObject instanceof ParseObject) {
//            if (anyParseObject instanceof ItemList && ((ItemList) anyParseObject).isNoSave()) { //TODO!! shouldn't be necessary - yes, needed for lists like Today that are generated on the fly
//                return;
//            }
////            Display.getInstance().callSeriallyOnIdle(() -> saveAndCacheImpl(anyParseObject, saveToCache));
////            Display.getInstance().callSeriallyOnIdle(() -> saveAndCacheImpl(anyParseObject, saveToCache));
////            Display.getInstance().callSerially(() -> saveAndCacheImpl(anyParseObject, saveToCache));
//            saveAndCacheImpl(anyParseObject, saveToCache);
////<editor-fold defaultstate="collapsed" desc="comment">
//////                ((ParseObject) anyParseObject).save();
////            if (anyParseObject.getObjectIdP() == null) { //not saved before, so MUST save to Parse to get an objectID before proceding
//////                anyParseObject.save();
////                saveImpl(anyParseObject,saveToCache);
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (saveToCache) {
////////                    if (anyParseObject instanceof WorkSlot) {
////////                        cacheWorkSlots.put(anyParseObject.getObjectIdP(), anyParseObject); //cache it in case it is the first time this object is saved
////////                    } else {
////////                        cache.put(anyParseObject.getObjectIdP(), anyParseObject); //cache it in case it is the first time this object is saved
////////                    }
//////                    cachePut(anyParseObject); //cache it in case it is the first time this object is saved
//////                }
//////</editor-fold>
////            } else {
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if (saveToCache) { //TODO Optimization: don't put if already cached (any substantial savings??)
////////                    if (anyParseObject instanceof WorkSlot) {
////////                        cacheWorkSlots.put(anyParseObject.getObjectIdP(), anyParseObject); //cache it in case it is the first time this object is saved
////////                    } else {
////////                        cache.put(anyParseObject.getObjectIdP(), anyParseObject); //cache it in case it is the first time this object is saved
////////                    }
//////                    cachePut(anyParseObject); //cache it in case it is the first time this object is saved
//////                }
//////                saveInBackground(anyParseObject); //CANNOT save in background, can create race conditions eg if updating item in Overdue to new date and then immediately doing a query for new Overdue items
//////</editor-fold>
////                saveImpl(anyParseObject,saveToCache); //CANNOT save in background, can create race conditions eg if updating item in Overdue to new date and then immediately doing a query for new Overdue items
////            }
////        }
////</editor-fold>
//        }
//    }
    /**
     * save any type of ParseObject. Encapsulates the exception handling. Can be
     * called with null objects.
     *
     * @param anyParseObject
     */
//    public void saveAndWaitXXX(ParseObject anyParseObject) {
//        saveImplXXX(anyParseObject, true);
//    }
//    public void saveInBackgroundOnTimeoutXXX(ParseObject anyParseObject) { //TODO!!!! Implemented timed save (delay save by eg 200ms to catch all updates before sending saves on their way
//        DAO.this.saveInBackground(anyParseObject);
//    }
    /**
     * let background task save to Parse after the objects have been saved to
     * cache
     *
     * @param anyParseObject
     */
//    private void saveToParseOnlyNoCachingXXX(ParseObject anyParseObject) {
//        try {
//            anyParseObject.save();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
//    private EasyThread backgroundSaveThread = null; //EasyThread.start("DAO.backgroundSave"); //null; //thread with background task
//</editor-fold>
    private Thread backgroundSaveThread = null; //EasyThread.start("DAO.backgroundSave"); //null; //thread with background task
    private final Object LOCK_SAVELIST = new Object();
//    private Vector backgroundSavePendingList = new Vector(); //Vector is thread safe
    private Vector vector = new Vector();
//    private List<ParseObject> saveList = new ArrayList();
//    List<Object> saveList = new ArrayList();
    private Vector saveList = new Vector();
//    private List<ParseObject> batchSaveList = null;
    private Timer t;
//    private TimerTask timerTask = null;

//<editor-fold defaultstate="collapsed" desc="comment">
//    private EasyThread backgroundSaveQueueThread = EasyThread.start("DAO.backgroundQueueSave"); //thread with background task
//    private List<ParseObject> backgroundSaveQueue = new ArrayList<>();
//    private Object backgroundSaveQueueLock = new Object();
//
//    /**
//    not sure how often this will be useful, mainly when saving multiple generated repeat instances at once, rest of the time, a user will only
//    create a single new object at a time (so background save is the most important, followed by sequential background save. NO, will also be useful
//    when
//    @param parseObjects
//     */
//    public void saveInBackgroundSequentialBatchXXX(List<ParseObject> parseObjects) {
//        if (parseObjects.size() == 0) {
//            return;
//        }
//        synchronized (backgroundSaveQueueLock) {
//            backgroundSaveQueue.addAll(parseObjects);
//        }
//        if (backgroundSaveThread == null) {
//            backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
//        }
//        cacheList(parseObjects); //first cache all objects
//        backgroundSaveThread.run(() -> {
//            String className = null;
//            List batchQueue = new ArrayList();
//            EBatchOpType batchOpType = null;
//            for (ParseObject parseObject : backgroundSaveQueue) {
//                String thisClassName = parseObject.getClassName();
//                if (className == null) {
//                    className = thisClassName;
//                }
//                if (batchOpType == null) {
//                    batchOpType = parseObject.getObjectIdP() == null ? ParseBatch.EBatchOpType.CREATE : ParseBatch.EBatchOpType.UPDATE;
//                }
//
//                EBatchOpType thisBatchOpType = parseObject.getObjectIdP() == null ? ParseBatch.EBatchOpType.CREATE : ParseBatch.EBatchOpType.UPDATE;
//
//                if (className.equals(thisClassName) && batchOpType.equals(thisBatchOpType)) {
//                    batchQueue.add(parseObject); //add all ParseObjects with same class and same need (CREATE/UPDATE) to batchQueue
//                } else {
//                    try {
//                        ParseBatch parseBatch = ParseBatch.create();
//                        parseBatch.addObjects(batchQueue, batchOpType);
//                        parseBatch.execute();
//                        className = null;
//                        batchOpType = null;
//                        batchQueue = new ArrayList();
//                    } catch (ParseException ex) {
//                        Log.e(ex);
//                    }
//                }
//            }
//            //if anything left in queue, save it:
//            if (batchQueue != null && !(batchQueue.isEmpty())) {
//                try {
//                    ParseBatch parseBatch = ParseBatch.create();
//                    parseBatch.addObjects(batchQueue, batchOpType);
//                    parseBatch.execute();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//            }
//        });
//    }
//</editor-fold>
    /**
     * save any type of ParseObject. Encapsulates the exception handling. Can be
     * called with null objects, but all ParseObjects MUST be of same ParseClass
     *
     * @param listCopyOfParseObjectsToBatchSave must be a *COPY* of the list to
     * be saved to avoid ConcurrentModificationException
     * @param anyParseObject
     */
//    static public void saveBatch(List<ParseObject> listOfParseObjectsToBatchSave) {
    public void saveBatch(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean saveToCache) {
//        Display.getInstance().callSerially(() -> {
        if (!listCopyOfParseObjectsToBatchSave.isEmpty()) {
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    for (ParseObject p : listCopyOfParseObjectsToBatchSave) { //LEADS to ConcurrentModificationException
//                        if (!p.isDirty()) { //don't waste bandwidth/time on saving not changed objects
//                            listCopyOfParseObjectsToBatchSave.remove(p);
//                        }
//                    }
//                }
//</editor-fold>
            Log.p("SAVE-DAO.saveBatch() saving: " + listCopyOfParseObjectsToBatchSave);
            try {
                ParseBatch parseBatch = ParseBatch.create();
                parseBatch.addObjects(listCopyOfParseObjectsToBatchSave, ParseBatch.EBatchOpType.UPDATE);
                parseBatch.execute();
            } catch (ParseException ex) {
                Log.e(ex);
            }
            if (saveToCache) {
                for (ParseObject o : listCopyOfParseObjectsToBatchSave) {
                    cachePut(o);
                }
            }
        }
//        });
//        save(anyParseObject, true);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveBatch(List<ParseObject> listCopyOfParseObjectsToBatchSave) {
//        saveBatch(listCopyOfParseObjectsToBatchSave, true);
//    }
    /**
     * check if the list of parsePobjects are all of same type and can be saved
     * as a batch, if yes do so and return true, otherwise do nothing and return
     * false
     *
     * @param parseObjects
     * @return
     */
//    private boolean batchSave(List<ParseObject> parseObjects) {
//        String type = null;
//        for (ParseObject parseObject : parseObjects) {
//            if (type == null) {
//                type = parseObject.getClassName(); //store type of first object
//            } else if (!type.equals(parseObject.getClassName())) {
//                return false; //if any subsequent object has a different type, return false
//            }
//        }
//        //all objects have same type, save them as a batch operation
//        saveBatch(parseObjects);
//        return true;
//    }
//    private void saveImpl(ParseObject anyParseObject) {
//        saveImpl(anyParseObject, true);
//    }
//</editor-fold>
    private synchronized void saveAndCacheImpl(ParseObject anyParseObject, boolean saveToCache) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        saveAndCacheImpl(anyParseObject, saveToCache, false);
//    }
//
//    private synchronized void saveAndCacheImpl(ParseObject anyParseObject, boolean saveToCache, boolean wait) {
//        if (wait) {
//            Log.p(">>>SAVING--->>>DAO.saveAndCacheImpl() saving: " + anyParseObject);
//
//            if (Config.TEST && anyParseObject instanceof ItemAndListCommonInterface) {
//                String refErrorStr = ((ItemAndListCommonInterface) anyParseObject).hasReferencesToUnsavedParseObjects();
//                if (!refErrorStr.isEmpty()) {
//                    Log.p("    BUT SAVE-DAO.saveAndCacheImpl: anyParseObject=\"" + anyParseObject + "\" has references to unsaved parseObjects:" + refErrorStr);
//                }
//            }
//            try {
//                anyParseObject.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//            Log.p("    DONE SAVING--->>>DAO.saveAndCacheImpl() saving: " + anyParseObject);
//            if (anyParseObject.getDate(Item.PARSE_DELETED_DATE) != null) {//if an object is soft-deleted, then remove it from cache (shouldn't strictly be necessary as all pointers should have been removed, but also cleans up local cache)
//                cacheDelete(anyParseObject);
//            } else if (saveToCache) {
//                cachePut(anyParseObject); //must save first to get the objectId before saving to local cache/storage
//            }
//        } else {
//            Display.getInstance().callSerially(() -> {
//</editor-fold>

        if (Config.TEST_BACKGR && anyParseObject instanceof ItemAndListCommonInterface) {
            String refErrorStr = ((ItemAndListCommonInterface) anyParseObject).hasReferencesToUnsavedParseObjects();
            if (!refErrorStr.isEmpty()) {
                Log.p("    BUT SAVE-DAO.saveAndCacheImpl: anyParseObject=\"" + anyParseObject + "\" has references to unsaved parseObjects:" + refErrorStr);
            }
        }
        try {
            if (Config.TEST_BACKGR) {
                Log.p(">>>>>>> ------ executing parseObject.save()" + anyParseObject);
            }
            anyParseObject.save();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        if (Config.TEST_BACKGR) {
            Log.p(">>>>>>> ------ DONE executing parseObject.save()" + anyParseObject);
        }
        if (anyParseObject.getDate(Item.PARSE_DELETED_DATE) != null) {//if an object is soft-deleted, then remove it from cache (shouldn't strictly be necessary as all pointers should have been removed, but also cleans up local cache)
            cacheDelete(anyParseObject);
        } else if (saveToCache) {
            cachePut(anyParseObject); //must save first to get the objectId before saving to local cache/storage
        }
    }

    /**
     * saves the list of ParseObjects in the background but in sequential order
     * so that each list is saved before the next one (to ensure that references
     * to Parse ObjectIds are resolved). Will only save unsaved or dirty objects
     * and will group together objects of same parseClass into a batch save.
     *
     * @param postSaveAction NOT currently supported! TODO!!!! one way to
     * support: add Runnable to saveVector and have thread run it serially after
     * the preceding objects have been saved
     * @param parseObjects non-null (but possibly empty) list
     */
    private void addToSaveQueue(List<ParseObject> parseObjects, Runnable postSaveAction) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        saveImpl(anyParseObject);
//        Log.p("Calling DAO.saveInBackground() with list=" + parseObjects + ", postSaveAction defined=" + ((Boolean) (postSaveAction != null)).toString());
//        if (false) {
//            Log.p("SAVE-Calling DAO.addToSaveInBackgroundQueue() with list=" + parseObjects + ", postSaveAction=" + postSaveAction);
//        }
//        if (false && (parseObjects == null || parseObjects.size() == 0)) {
////            Log.p("DAO.saveInBackground() - RETURN doing nothing");
//            ASSERT.that(true, "SAVE-DAO.saveInBackground() - RETURN doing nothing");
//            return;
//        }
//        for (ParseObject p : parseObjects) {
//            if (p instanceof ItemAndListCommonInterface) {
//                ((ItemAndListCommonInterface) p).updateBeforeSave();
//            }
//        }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (backgroundSaveThread == null) {
//            backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
//        }
//        if (false) {
////        Vector vector = new Vector(); //make copy
//            List<ParseObject> vector = new ArrayList(); //make copy
//            for (ParseObject p : parseObjects) {
//                if ((p.getObjectIdP() == null || p.isDirty())) {// && !vector.contains(p)) //only save unsaved or dirty objects, and only add object if not already in list
//                    boolean alreadyInList = false;
//                    for (ParseObject x : vector) {
//                        if (x == p) alreadyInList = true;
//                    }
//                    if (!alreadyInList)
//                        vector.add(p);
//                }
//            }
////        if (false) {
//        } else {
//        List<ParseObject> saveList = new ArrayList();
//        List<Object> saveList = new ArrayList();
        //copy all objects into save list:
//        Log.p("BACKGROUND GETTING LOCK objects=[" + parseObjects + "]");
//        synchronized (LOCK_SAVELIST) {
//            Log.p("BACKGROUND ADDING objects=[" + parseObjects + "]");
//        Log.p("BACKGROUND GOT Lock");
//</editor-fold>
        if (Config.TEST_BACKGR) {
            Log.p("==========>>>    addToSaveQueue(" + parseObjects + ", " + postSaveAction + ")");
        }
        if (parseObjects != null) {
            for (ParseObject p : parseObjects) {
//                if (p instanceof ItemAndListCommonInterface)
//                    ((ItemAndListCommonInterface)p).updateBeforeSave();
//                if ((p.getObjectIdP() == null || p.isDirty()) && !saveList.contains(p)) {//only save unsaved or dirty objects
                if (p instanceof ItemList && ((ItemList) p).isNoSave()) { //only save unsaved or dirty objects
                    //do nothing   
                } else if (p.getObjectIdP() == null) { //only save unsaved or dirty objects
                    if (saveList.contains(p)) {
                        if (Config.TEST_BACKGR) {
                            Log.p("==========>>>    ObjId==null, BUT already in savelist(" + saveList.size() + ")=" + saveList);
                        }
                    } else {
                        saveList.add(p);
                        if (Config.TEST_BACKGR) {
                            Log.p("==========>>>    ObjId==null, added to savelist(" + saveList.size() + ")=" + saveList);
                        }
                    }
                } else if (p.isDirty()) {//only save unsaved or dirty objects
                    if (saveList.contains(p)) {
                        if (Config.TEST_BACKGR) {
                            Log.p("==========>>>    dirty, BUT already in savelist(" + saveList.size() + ")=" + saveList);
                        }
                    } else {
                        saveList.add(p);
                        if (Config.TEST_BACKGR) {
                            Log.p("==========>>>    dirty, added to savelist(" + saveList.size() + ")=" + saveList);
                        }
                    }
                } else if (saveList.contains(p)) {//only save unsaved or dirty objects
                    if (Config.TEST_BACKGR) {
                        Log.p("==========>>>    !dirty, HAS ObjdId, AND already in savelist(" + saveList.size() + ")=" + saveList);
                    }
                } else {
                    if (Config.TEST_BACKGR) {
                        Log.p("==========>>>    ERROR !dirty, HAS ObjdId, but NOT already in savelist, p=" + p + " list=(" + saveList.size() + ")=" + saveList);
                    }
                }
            }
        }

        if (postSaveAction != null) {
            if (Config.TEST_BACKGR) {
                Log.p("==========>>>    adding postSaveAction[" + postSaveAction + "] to savelist(" + saveList.size() + ")=" + saveList);
            }
            saveList.add(postSaveAction);
        }
        if (false) {
            LOCK_SAVELIST.notifyAll();
        }

        if (backgroundSaveThread == null) {
//            backgroundSaveThread = EasyThread.start("DAO.backgroundSave"); //initialize on first use
//            backgroundSaveThread.run(() -> {
            backgroundSaveThread = new Thread(() -> {
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (false) {
//                ParseObject parseObject;
//                while (!vector.isEmpty()) {
//                    parseObject = (ParseObject) vector.remove(0);
//                    if (parseObject instanceof ItemList && ((ItemList) parseObject).isNoSave())
//                        Log.p("BACKGROUND saving IGNORE: " + parseObject);
//                    else {
////                    Log.p("BACKGROUND saving: " + parseObject);
//                        ParseObject parseObject2 = parseObject;
//                        Display.getInstance().callSerially(() -> saveImpl((ParseObject) parseObject2, true)); //must run on EDT to avoid conflicts with other ParseObject operations
//                    }
//                }
//                if (postSaveAction != null)
//                    Display.getInstance().callSerially(postSaveAction);
//            } else { //copied from CN1.EasyThread
//</editor-fold>
                if (Config.TEST) {
                    Log.p(">>>>>>> BACKGROUND1 new THREAD starting");
                }
                while (true) {
                    synchronized (LOCK_SAVELIST) {
                        if (Config.TEST_BACKGR) {
                            Log.p(">>>>>>> BACKGROUND2 backgroundSaveThread - LOCK acquired***********");
                        }
                        while (!saveList.isEmpty()) {
                            if (saveList.get(0) instanceof Runnable) { //execute Runnables
                                Runnable runnable = (Runnable) saveList.remove(0);
                                if (Config.TEST_BACKGR) {
                                    Log.p(">>>>>>> BACKGROUND3 - executing Runnable [" + runnable + "]");
                                }
//                                Display.getInstance().callSeriallyAndWait(runnable);
                                Display.getInstance().callSerially(runnable);
//                                runnable.run();
                            } else if (saveList.get(0) instanceof ParseObject) {
                                if (((ParseObject) saveList.get(0)).getObjectIdP() == null) { //if previously unsaved, then save alone (not batch ensure references are OK)
                                    ParseObject parseObject = (ParseObject) saveList.remove(0);
                                    if (Config.TEST_BACKGR) {
                                        Log.p(">>>>>>> BACKGROUND4 - saveAndWait parseObject=" + parseObject);
                                    }
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    Display.getInstance().callSerially(() -> saveAndCacheImpl((ParseObject) saveList.remove(0), true)); //must save to cache (in case there are new created objects)
//                                    Display.getInstance().callSeriallyAndWait(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
//                                    Display.getInstance().invokeAndBlock(() -> saveAndCacheImpl(parseObject, true)); //NOT good, BLOCKS the app completely!!!
//                                    Display.getInstance().callSeriallyAndWait(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
//                                    saveAndCacheImpl(parseObject, true); //must save to cache (in case there are new created objects)
//</editor-fold>
//                                    Display.getInstance().callSerially(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
                                    Display.getInstance().callSeriallyAndWait(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    saveAndCacheImpl(parseObject, true, true); //must save to cache (in case there are new created objects)
//                                    while (parseObject.getObjectIdP() == null) {
//                                    if (false) {
//                                        int i = 0;
//                                        do { //use do while instead of while () since it is unlikely that the save above will succeed immediately
//                                            try {
//                                                if (Config.TEST && i % 125 == 0) {
//                                                    Log.p("SAVE-BACKGROUN- backgroundSaveThread - SLEEP waiting for full save of new object=" + parseObject);
////                                                Log.p("SAVE-BACKGROUN- backgroundSaveThread - EDT.hasNoSerialCallsPending()=" + Display.getInstance().hasNoSerialCallsPending()+", isProcessingSerialCalls()="+Display.getInstance().isProcessingSerialCalls());
//                                                }
////<editor-fold defaultstate="collapsed" desc="comment">
////                                            try {
////                                                LOCK_SAVELIST.wait();
////                                            } catch (InterruptedException e) {
////                                            }
////</editor-fold>
//                                                Thread.sleep(200); //the additional delay due to this sleep will on average be half, ie 50ms => 25ms
//                                                i++;
//                                            } catch (InterruptedException ex) {
//                                                ex.printStackTrace();
//                                            }
//                                        } while (parseObject.getObjectIdP() == null);
//                                    }
//</editor-fold>
                                } else { //already saved so has its objId already
                                    List<ParseObject> batchSaveList = new ArrayList();
                                    //automatically group together all ParseObjects of same parseClass for batch save
//<editor-fold defaultstate="collapsed" desc="comment">
//                                    String parseClass = ((ParseObject) saveList.get(0)).getClassName();
//                                    while (!saveList.isEmpty() && parseClass.equals(((ParseObject) saveList.get(0)).getClassName())) { //loop condition always met for first element => will remove at least one elt from saveList, so loop termination guaranteed
//                                        ParseObject p = (ParseObject) saveList.remove(0);
//</editor-fold>
                                    String parseClass = ((ParseObject) saveList.get(0)).getClassName();
                                    //batch together all saves of same parseClass, for already saved objects only
                                    while (!saveList.isEmpty() && saveList.get(0) instanceof ParseObject
                                            && parseClass.equals(((ParseObject) saveList.get(0)).getClassName())
                                            && ((ParseObject) saveList.get(0)).getObjectIdP() != null) { //loop condition always met for first element => will remove at least one elt from saveList, so loop termination guaranteed
                                        ParseObject p = (ParseObject) saveList.remove(0);
                                        if (true || p.isDirty()) { //don't save non-dirty elements (already tested in parse4cn1.batchsave?)
                                            batchSaveList.add(p);
                                        }
                                    }

                                    if (batchSaveList.size() > 1) { //there are multiple parseObjects with same class to batch save
                                        if (Config.TEST_BACKGR) {
                                            Log.p(">>>>>>> BACKGROUND5 - batchsaveList(" + batchSaveList.size() + ")=" + batchSaveList);
                                        }
//                                        Display.getInstance().callSeriallyAndWait(() -> saveBatch(batchSaveList, true)); //must save to cache (in case there are new created objects)
                                        Display.getInstance().callSerially(() -> saveBatch(batchSaveList, true)); //must save to cache (in case there are new created objects)
//                                        saveBatch(batchSaveList, true); //must save to cache (in case there are new created objects)
                                    } else if (batchSaveList.size() == 1) { //there is only ONE parseObject to save
                                        ParseObject parseObject = batchSaveList.remove(0);
                                        if (false && parseObject instanceof ItemList && ((ItemList) parseObject).isNoSave()) {
                                        } else {
                                            if (Config.TEST_BACKGR) {
                                                Log.p(">>>>>>> BACKGROUND6 - batchsaveList(" + batchSaveList.size() + ")=" + parseObject);
                                            }
//<editor-fold defaultstate="collapsed" desc="comment">
//                                            Display.getInstance().callSerially(() -> saveImpl((ParseObject) batchSaveList.get(0), true)); //must save to cache (in case there are new created objects)
//                                            Display.getInstance().callSeriallyAndWait(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
//                                            saveAndCacheImpl(parseObject, true); //must save to cache (in case there are new created objects)
//</editor-fold>
                                            Display.getInstance().callSerially(() -> saveAndCacheImpl(parseObject, true)); //must save to cache (in case there are new created objects)
//                                            saveAndCacheImpl(parseObject, true); //must save to cache (in case there are new created objects)
                                        }
                                    } // else (batchSaveList.size() ==0) <=> do nothing
                                }
                            } else { //ERROR: object other than Runnable or ParseObject in list
                                Object x = saveList.remove(0); //ensure to empty the saveList
                                assert false : ">>>>>>> BACKGROUND7 - UNKNOWN type in saveList, object=" + x + ", saveList(" + saveList.size() + ")=" + saveList;
                            }
                        } //while (!saveList.isEmpty())
                        if (Config.TEST_BACKGR) {
                            Log.p(">>>>>>> BACKGROUND8 - LOCK.wait() ************");
                        }
                        try {
                            LOCK_SAVELIST.wait(); //saveList is empty, so wait
                        } catch (InterruptedException e) {
                            if (Config.TEST_BACKGR) {
                                Log.p(">>>>>>> BACKGROUND9 - InterruptedException = " + e);
                            }
                        }
                    }
                }
            }, "SAVE-DAO.backgroundSave"); //initialize on first use
            backgroundSaveThread.start();
        }

//        synchronized (LOCK_SAVELIST) { //prevent the timer from executing run will the timer is being restarted (seems to lead to t.schedule(timerTask, 1000); getting a nullPointer exception)
        //start a timer to only initiate save after a certain delay to ensure all updates to e.g. an Item have been done, e.g. if inheriting multiple values
        if (t != null) {
            t.cancel();
        }
        t = new Timer(); //"BackgroundSaveTimer");

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (Config.TEST_BACKGR) {
                    Log.p(">>>>>>> TimerTask-run(): trying to get LOCK");
                }
                synchronized (LOCK_SAVELIST) {
                    if (t != null) {
                        t.cancel();
                        t = null;
                    }
                    if (Config.TEST_BACKGR) {
                        Log.p(">>>>>>> TimerTask: got LOCK, calling LOCK.notify(). saveList=" + saveList);
                    }
                    LOCK_SAVELIST.notifyAll();
                }
            }
        };
        if (Config.TEST_BACKGR) {
            Log.p(">>>>>>> TimerTask.schedule(timerTask, 200)");
        }
        t.schedule(timerTask, 400); //wait 200ms before initiate saving to 'batch' together as many saves as possible
//        }
    }

    private void addToSaveQueue(List<ParseObject> parseObjects) {
        addToSaveQueue(parseObjects, null);
    }

    private void addToSaveQueue(ParseObject parseObject, Runnable postSaveAction) {
        addToSaveQueue(new ArrayList(Arrays.asList(parseObject)), postSaveAction);
    }

    private void addToSaveQueue(ParseObject parseObject) {
        addToSaveQueue(parseObject, null);
    }

    boolean isSavesPending() {
        return (saveList != null && !saveList.isEmpty());
    }

    private String testShowMissingRefs(Object elt) {
        String s = "";
        if (elt instanceof Item) {
            s += "ITEM: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((Item) elt).getObjectIdP() != null ? ((Item) elt).getObjectIdP() : "<null>");
            s += "; owner=" + (((Item) elt).getOwner() != null ? ((Item) elt).getOwner() : "<null>; ");
            s += "; repeatRule=" + (((Item) elt).getRepeatRule() != null ? ((Item) elt).getRepeatRule() : "<null>");
            s += "; subtasks={" + (((Item) elt).getListFull() + "}");
        } else if (elt instanceof RepeatRuleParseObject) {
            s += "REPEATRULE: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((RepeatRuleParseObject) elt).getObjectIdP() != null ? ((RepeatRuleParseObject) elt).getObjectIdP() : "<null>");
            s += "; instances={" + (((RepeatRuleParseObject) elt).getListOfUndoneRepeatInstances() + "}");
        } else if (elt instanceof Category) {
            s += "CATEGORY: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((Category) elt).getObjectIdP() != null ? ((Category) elt).getObjectIdP() : "<null>");
            s += "; owner=" + (((Category) elt).getOwner() != null ? ((Category) elt).getOwner() : "<null>");
            s += "; subtasks={" + (((Category) elt).getListFull() + "}");
        } else if (elt instanceof ItemList) {
            s += "ITEMLIST: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((ItemList) elt).getObjectIdP() != null ? ((ItemList) elt).getObjectIdP() : "<null>");
            s += "; owner=" + (((ItemList) elt).getOwner() != null ? ((ItemList) elt).getOwner() : "<null>");
            s += "; subtasks={" + (((ItemList) elt).getListFull() + "}");
        } else if (elt instanceof WorkSlot) {
            s += "WORKSLOT: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((WorkSlot) elt).getObjectIdP() != null ? ((WorkSlot) elt).getObjectIdP() : "<null>");
            s += "; owner=" + (((WorkSlot) elt).getOwner() != null ? ((WorkSlot) elt).getOwner() : "<null>");
        }
        return s;
    }

    private void saveWorkSlotInBackground(WorkSlot workSlot, Runnable postSaveAction) {
        if (workSlot == null) {
            return;
        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveWorkSlotInBackground(" + testShowMissingRefs(workSlot) + "), ");
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemAndListCommonInterface owner = workSlot.getOwner();
//        if (owner != null && owner.getObjectIdP() == null) {
//            saveInBackground((ParseObject) owner, () -> {
//                //Handle RepeatRule (NB. the code works even ir RR is not defined/null)
//                RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
//                if (repeatRule != null && repeatRule.getObjectIdP() == null) {
//                    workSlot.setRepeatRuleInParse(null); //temporarily remove repeatRule
//                }
//                if (repeatRule != null) {
//                    saveInBackground(repeatRule, () -> {
//                        //then add the now saved repeatRule and subtasks and save the project again:
//                        workSlot.setRepeatRuleInParse(repeatRule);
//                        //finally, save the project again, now with its refs to subtasks and repeatrules
//                        addToSaveQueue(workSlot, postSaveAction);
//                    });
//                } else {
//                    //finally, save the project again, now with its refs to subtasks and repeatrules
//                    addToSaveQueue(workSlot, postSaveAction);
//                }
////                addToSaveQueue((ParseObject) workSlot, postSaveAction);
//            });
//        } else {
//            RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
//            if (repeatRule != null && repeatRule.getObjectIdP() == null) {
//                workSlot.setRepeatRuleInParse(null); //temporarily remove repeatRule
//            }
//            if (repeatRule != null) {
//                saveInBackground(repeatRule, () -> {
//                    //then add the now saved repeatRule and subtasks and save the project again:
//                    workSlot.setRepeatRuleInParse(repeatRule);
//                    //finally, save the project again, now with its refs to subtasks and repeatrules
//                    addToSaveQueue(workSlot, postSaveAction);
//                });
//            } else {
//                //finally, save the project again, now with its refs to subtasks and repeatrules
//                addToSaveQueue(workSlot, postSaveAction);
//            }
//            addToSaveQueue((ParseObject) workSlot, postSaveAction);
//        }
//</editor-fold>
        if (workSlot.getObjectIdP() == null) { //if top-level item not saved yet, do so before saving the subtasks/repeatRule that reference it
            //Handle RepeatRule (NB. the code works even ir RR is not defined/null)
            RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
            if (repeatRule != null && repeatRule.getObjectIdP() == null) {
                workSlot.setRepeatRuleInParse(null); //temporarily remove repeatRule
                //save workSlot first
                addToSaveQueue((ParseObject) workSlot, () -> {
                    //NB. Must run this as postsave, otherwise the subtasks/repeatRule is added again before the item without them has had time to be saved)
                    saveInBackground(repeatRule, () -> {
                        //then add the now saved repeatRule and subtasks and save the project again:
                        workSlot.setRepeatRuleInParse(repeatRule);
                        //finally, save the project again, now with its refs to subtasks and repeatrules
                        addToSaveQueue(workSlot, postSaveAction);
                    });
                });
            } else { //repeatRule == null || repeatRule.getObjectIdP() != null
                //finally, save the workSlot again, now with its refs to repeatrules
//                addToSaveQueue(workSlot, postSaveAction);
                saveInBackground(repeatRule,
                        () -> addToSaveQueue(workSlot, postSaveAction));
            }
        } else { //workSlot.getObjectIdP() != null
            //if project has already been saved, we just need to make sure to save the subtasks first:
            //repeatRule should be saved from whereever it is created?!)
            saveInBackground(workSlot.getRepeatRule(),
                    () -> addToSaveQueue(workSlot, postSaveAction));
        }
    }

    private void saveRepeatRuleInBackground(RepeatRuleParseObject repeatRule, Runnable postSaveAction) {
        if (repeatRule == null) {
            return;
        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveRepeatRuleInBackground(" + testShowMissingRefs(repeatRule) + "), ");
        }
        // saving a (completely new) repeatRule requires saving in a particular order to avoid problems with references to unsaved parseObjects:
        //save project first, *without* the potentially unsaved subtasks
        List undoneRepeatInstances = repeatRule.getListOfUndoneRepeatInstances();
        repeatRule.setListOfUndoneRepeatInstances(null);

        //first save repeatRule (so repeatInstances can reference it)
        addToSaveQueue(repeatRule, () -> {
            //then save all referenced repeat instances (recursively)
            if (undoneRepeatInstances != null && !undoneRepeatInstances.isEmpty()) {
                saveInBackground(undoneRepeatInstances, () -> {//); //don't save here (won't work because eg the initiator item may reference RR and not be saved yet!)
                    repeatRule.setListOfUndoneRepeatInstances(undoneRepeatInstances);
                    addToSaveQueue((ParseObject) repeatRule, postSaveAction);
                });
            }
        });
    }

    /**
     * save a (potentially just) created project with (potentially just created
     * or modified) subtasks avoiding issues with circular references (project
     * referring to unsaved subtasks, subtasks referring to unsaved project)
     *
     * @param projectOrItem the owner must have been saved before (so that a
     * reference from projectOrItem to its owner can be saved)
     * @return
     */
//    public void saveProjectInBackground(Item projectOrItem) {
//    private void saveInBackground(Runnable postSaveAction, Item projectOrItem) {
//        saveItemInBackground(postSaveAction, projectOrItem);
//    }
    private void saveItemInBackground(Item projectOrItem, Runnable postSaveAction) {
        if (projectOrItem == null) {
            return;
        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveItemInBackground(elt=" + testShowMissingRefs(projectOrItem) + ")");
        }
        //if filter not saved, simply save it first and then re-save the projectOrItem (simple approach since filter has no references to projectOrItem)
        if (projectOrItem.getFilterSortDef() != null && !projectOrItem.getFilterSortDef().isNoSave() && projectOrItem.getFilterSortDef().getObjectIdP() == null) {
//                && !(projectOrItem.getFilterSortDef().equals(FilterSortDef.getDefaultFilter()))) { //do not save default filters --> now checked in saveInBackground
            saveInBackground(projectOrItem.getFilterSortDef(), () -> saveItemInBackground(projectOrItem, postSaveAction));
        } else {
            //Filter has no references back to Item, so can always be saved
            saveInBackground(projectOrItem.getFilterSortDef());
            if (projectOrItem.getObjectIdP() == null) { //if top-level item not saved yet, do so before saving the subtasks/repeatRule that reference it

                //Handle RepeatRule (NB. the code works even ir RR is not defined/null)
                RepeatRuleParseObject repeatRule = projectOrItem.getRepeatRule();
                if (repeatRule != null && repeatRule.getObjectIdP() == null) {
                    projectOrItem.setRepeatRuleInParse(null); //temporarily remove repeatRule
                }
                //Handle subtasks (or sub-projects)
                List subtasks = projectOrItem.getListFull();
                projectOrItem.setList(null); //temporarily remove subtasks (which may be unsaved)
//                List categories = projectOrItem.getCategories();
//                projectOrItem.setCategories(null); //temporarily remove subtasks (which may be unsaved)

                //save project first, *without* the potentially unsaved subtasks or repeatrule
                addToSaveQueue(projectOrItem, () -> {
                    //NB. Must run this as postsave, otherwise the subtasks/repeatRule is added again before the item without them has had time to be saved)
                    //then save all subtasks (recursively)
//<editor-fold defaultstate="collapsed" desc="comment">
//                    saveInBackground(subtasks); //recursive until reaching leaf tasks (handled above in if(!isProject))
//                    projectOrItem.setList(subtasks);
//                    if (repeatRule != null && repeatRule.getObjectIdP() == null) {
//                        saveRepeatRuleInBackground(repeatRule, () -> {
//                            //then add the now saved repeatRule and subtasks and save the project again:
//                            projectOrItem.setRepeatRuleInParse(repeatRule);
//                            //finally, save the project again, now with its refs to subtasks and repeatrules
//                            addToSaveQueue(projectOrItem, postSaveAction);
//                        });
//                    } else {
//
//                    }
//</editor-fold>
                    //save project first, *without* the potentially unsaved subtasks
                    addToSaveQueue((ParseObject) projectOrItem, () -> {
                        //NB. Must run this as postsave, otherwise the subtasks/repeatRule is added again before the item without them has had time to be saved)
                        //then save all subtasks (recursively)
//                addToSaveInBackgroundQueue(subtasks); //recursive until reaching leaf tasks (handled above in if(!isProject))
                        saveInBackground(subtasks, () -> { //recursive until reaching leaf tasks (handled above in if(!isProject))
                            projectOrItem.setList(subtasks);
//                            if (categories != null && !categories.isEmpty()) {
//                                saveInBackground(categories, () -> {
                            if (repeatRule != null) {
                                saveInBackground(repeatRule, () -> {
                                    //then add the now saved repeatRule and subtasks and save the project again:
                                    projectOrItem.setRepeatRuleInParse(repeatRule);
                                    //finally, save the project again, now with its refs to subtasks and repeatrules
                                    addToSaveQueue(projectOrItem, postSaveAction);
                                });
//                                    } else {
//                                        //finally, save the project again, now with its refs to subtasks and repeatrules
//                                        addToSaveQueue(projectOrItem, postSaveAction);
//                                    }
//                                });
                            } else if (repeatRule != null) {
                                saveInBackground(repeatRule, () -> {
                                    //then add the now saved repeatRule and subtasks and save the project again:
                                    projectOrItem.setRepeatRuleInParse(repeatRule);
                                    //finally, save the project again, now with its refs to subtasks and repeatrules
                                    addToSaveQueue(projectOrItem, postSaveAction);
                                });
                            } else {
                                //finally, save the project again, now with its refs to subtasks and repeatrules
                                addToSaveQueue(projectOrItem, postSaveAction);
                            }
                        });
                    });
                });
            } else {
                //if project has already been saved, we just need to make sure to save all the references elements first:
                saveInBackground(projectOrItem.getRepeatRule(),
                        () -> saveInBackground(projectOrItem.getListFull(),
                                //                                () -> saveInBackground((List) projectOrItem.getCategories(),
                                () -> addToSaveQueue(projectOrItem, postSaveAction))
                );
//                );
            }
        }
    }

    private void saveItemListInBackground(ItemList itemList, Runnable postSaveAction) {
        if (itemList == null) {
            return;
        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveItemListInBackground(elt=" + testShowMissingRefs(itemList) + ")");
        }
        //save a filter before saving the element referencing it
        if (itemList.getFilterSortDef() != null && !itemList.getFilterSortDef().isNoSave() && itemList.getFilterSortDef().getObjectIdP() == null) {
            saveInBackground(itemList.getFilterSortDef(), () -> saveItemListInBackground(itemList, postSaveAction));
        } else {
            //filter does not have owner so can save immediately
            saveInBackground(itemList.getFilterSortDef());
            if (itemList.getObjectIdP() == null) { //if top-level item not saved yet, do so before saving the subtasks/repeatRule that reference it
                //Handle subtasks (or sub-projects)
                List<ParseObject> subtasks = itemList.getListFull();
                itemList.setList(null); //temporarily remove subtasks (which may be unsaved)

                //save project first, *without* the potentially unsaved subtasks
                addToSaveQueue((ParseObject) itemList, () -> {
                    //NB. Must run this as postsave, otherwise the subtasks/repeatRule is added again before the item without them has had time to be saved)
                    //then save all subtasks (recursively)
//                addToSaveInBackgroundQueue(subtasks); //recursive until reaching leaf tasks (handled above in if(!isProject))
                    saveInBackground(subtasks, () -> { //recursive until reaching leaf tasks (handled above in if(!isProject))
                        itemList.setList(subtasks);
                        addToSaveQueue((ParseObject) itemList, postSaveAction);
                    });
                });
            } else {
                //if project has already been saved, we just need to make sure to save the subtasks first:
//                List<ParseObject> subtasks = itemList.getListFull();
                saveInBackground(itemList.getListFull(), () -> { //recursive until reaching leaf tasks (handled above in if(!isProject))
                    addToSaveQueue((ParseObject) itemList, postSaveAction);
                });
            }
        }
    }

    private void saveCategoryInBackground(Category category, Runnable postSaveAction) {
        if (category == null) {
            return;
        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveCategoryInBackground(elt=" + testShowMissingRefs(category) + ")");
        }
        if (category.getFilterSortDef() != null && !category.getFilterSortDef().isNoSave() && category.getFilterSortDef().getObjectIdP() == null) {
            saveInBackground(category.getFilterSortDef(), () -> saveCategoryInBackground(category, postSaveAction));
        } else {
            saveInBackground(category.getFilterSortDef());
            if (category.getObjectIdP() == null) { //if top-level item not saved yet, do so before saving the subtasks/repeatRule that reference it
                //Handle subtasks (or sub-projects)
                List<ParseObject> categoryItems = category.getListFull();
                category.setList(null); //temporarily remove subtasks (which may be unsaved)

                //save project first, *without* the potentially unsaved subtasks
                addToSaveQueue((ParseObject) category, () -> {
                    //NB. Must run this as postsave, otherwise the subtasks/repeatRule is added again before the item without them has had time to be saved)
                    //then save all subtasks (recursively)
                    saveInBackground(categoryItems, () -> { //recursive until reaching leaf tasks (handled above in if(!isProject))
                        category.setList(categoryItems);
                        addToSaveQueue((ParseObject) category, postSaveAction);
                    });
                });
            } else {
                //if project has already been saved, we just need to make sure to save the subtasks first (so category doesn't reference unsaved items):
                saveInBackground(category.getListFull(), () -> { //recursive until reaching leaf tasks (handled above in if(!isProject))
                    addToSaveQueue((ParseObject) category, postSaveAction);
                });
            }
        }
    }

    /**
     * returns immediately, save takes place in background so not guaranteed
     * that the object has been saved on return from this call. Multiple calls
     * will ensure that each is saved before the next (all saves happens on same
     * thread).
     *
     * @param anyParseObject
     */
    public void saveInBackground(List<ParseObject> parseObjects, Runnable postSaveAction) {
        if (parseObjects == null || parseObjects.size() == 0) {
//            assert postSaveAction==null; 
            if (postSaveAction != null) {
                addToSaveQueue((List<ParseObject>) null, postSaveAction);
            }
            if (Config.TEST_BACKGR) {
                Log.p("==========>>> DAO.saveCategoryInBackground() SKIPPING null/empty objects =" + parseObjects);
            }
            return;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        for (ParseObject p : parseObjects) {
//            if (p instanceof ItemAndListCommonInterface) {
//                ((ItemAndListCommonInterface) p).updateBeforeSave();
//            }
//        }
//</editor-fold>
        for (ParseObject anyParseObject : parseObjects) {
//            if (anyParseObject == null || (anyParseObject.getObjectIdP() != null && !anyParseObject.isDirty())
//                    && (!(anyParseObject instanceof ItemAndListCommonInterface) || ((ItemAndListCommonInterface) anyParseObject).isNoSave())) {
//                if (postSaveAction != null) {
//                    addToSaveQueue((List<ParseObject>) null, postSaveAction);
//                }
//                continue; //skip all unchanged or noSave objects
//            }
            if (anyParseObject != null
                    && ((anyParseObject.getObjectIdP() == null || anyParseObject.isDirty())
                    && (!(anyParseObject instanceof ItemAndListCommonInterface) || !((ItemAndListCommonInterface) anyParseObject).isNoSave()))) {

                if (anyParseObject instanceof ItemAndListCommonInterface) {
                    ((ItemAndListCommonInterface) anyParseObject).updateBeforeSave();
                }

                if (anyParseObject instanceof Item) {
                    saveItemInBackground((Item) anyParseObject, postSaveAction);
                } else if (anyParseObject instanceof Category) {
                    saveCategoryInBackground((Category) anyParseObject, postSaveAction);
                } else if (anyParseObject instanceof ItemList) { //also covers ItemListList and CategoryList
                    saveItemListInBackground((ItemList) anyParseObject, postSaveAction);
                } else if (anyParseObject instanceof WorkSlot) { //e.g. Category, WorkSlot
                    saveWorkSlotInBackground((WorkSlot) anyParseObject, postSaveAction);
                } else if (anyParseObject instanceof RepeatRuleParseObject) {
                    saveRepeatRuleInBackground((RepeatRuleParseObject) anyParseObject, postSaveAction);
                } else if (anyParseObject instanceof FilterSortDef) { //e.g. Category, WorkSlot
                    if (!((FilterSortDef) anyParseObject).isNoSave()) {
                        addToSaveQueue((FilterSortDef) anyParseObject, postSaveAction);
                    }
                } else { //e.g. Category, WorkSlot
                    addToSaveQueue(anyParseObject, postSaveAction);
                }
            } else {
                if (postSaveAction != null) {
                    addToSaveQueue((List<ParseObject>) null, postSaveAction);
                }
            }
        }
    }

    public void saveInBackground(List<ParseObject> parseObjects) {
        saveInBackground(parseObjects, null);
    }

    public void saveInBackground(ParseObject anyParseObject, Runnable postSaveAction) {
        saveInBackground(new ArrayList(Arrays.asList(anyParseObject)), postSaveAction);
    }

    public void saveInBackground(ParseObject anyParseObject) {
        saveInBackground(anyParseObject, null);
    }

    //<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveInBackgroundOLD(Runnable postSaveAction, List<ParseObject> parseObjects) {
////        saveImpl(anyParseObject);
//        if (parseObjects == null || parseObjects.size() == 0) {
//            return;
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (backgroundSaveThread == null) {
////            backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
////        }
////        if (false) {
//////        Vector vector = new Vector(); //make copy
////            List<ParseObject> vector = new ArrayList(); //make copy
////            for (ParseObject p : parseObjects) {
////                if ((p.getObjectIdP() == null || p.isDirty())) {// && !vector.contains(p)) //only save unsaved or dirty objects, and only add object if not already in list
////                    boolean alreadyInList = false;
////                    for (ParseObject x : vector) {
////                        if (x == p) alreadyInList = true;
////                    }
////                    if (!alreadyInList)
////                        vector.add(p);
////                }
////            }
//////        if (false) {
////        } else {
////</editor-fold>
//        synchronized (LOCK) {
//            List<ParseObject> saveList = new ArrayList();
//            //copy all objects into save list:
//            Log.p("BACKGROUND adding objects to saveList: " + parseObjects);
//            for (ParseObject p : parseObjects) {
//                if (p.getObjectIdP() == null || p.isDirty()) {//only save unsaved or dirty objects
//                    boolean alreadyInList = false;
//                    for (ParseObject x : saveList) {
//                        if (p == x) //only use ==, more efficient than equals
//                            alreadyInList = true;
//                    }
//                    if (!alreadyInList)
//                        saveList.add(p);
////                            LOCK.notify();
//                }
//            }
//            if (parseObjects.size() > 0) {
//                //start a timer to only initiate save
//                if (t != null)
//                    t.cancel();
//                t = new Timer();
////        t.schedule(()->{}, 200);
//                TimerTask timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        synchronized (LOCK) {
//                            Log.p("BACKGROUND SAVE timer calling Timer.run()");
//                            if (saveList.size() > 0) {
//                                Log.p("BACKGROUND SAVE timer AND calling LOCK.notify()");
//                                LOCK.notify();
//                            }
//                        }
//                    }
//                };
//                Log.p("BACKGROUND timer schedule timerTask:");
//                t.schedule(timerTask, 1000); //wait 200ms before initiate saving to 'batch' together as many saves as possible
//            }
////            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        v.addAll(parseObjects);
////        for (ParseObject parseObject : parseObjects) {
//////            backgroundSavePendingList.add(parseObject);
////        }
////</editor-fold>
//        backgroundSaveThread.run(() -> {
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (false) {
////                ParseObject parseObject;
////                while (!vector.isEmpty()) {
////                    parseObject = (ParseObject) vector.remove(0);
////                    if (parseObject instanceof ItemList && ((ItemList) parseObject).isNoSave())
////                        Log.p("BACKGROUND saving IGNORE: " + parseObject);
////                    else {
//////                    Log.p("BACKGROUND saving: " + parseObject);
////                        ParseObject parseObject2 = parseObject;
////                        Display.getInstance().callSerially(() -> saveImpl((ParseObject) parseObject2, true)); //must run on EDT to avoid conflicts with other ParseObject operations
////                    }
////                }
////                if (postSaveAction != null)
////                    Display.getInstance().callSerially(postSaveAction);
////            } else { //copied from CN1.EasyThread
////</editor-fold>
////            ParseObject parseObject;
//            List<ParseObject> batchSaveList = new ArrayList(); //null;
//
//            while (true) {
//                synchronized (LOCK) {
//                    Log.p("BACKGROUND SAVE thread, saveList=" + saveList);
////                        if (saveVector.size() > 0) {
//                    while (!saveList.isEmpty()) {
//                        if (((ParseObject) saveList.get(0)).getObjectIdP() == null) { //always save new parseObjects first
//                            ParseObject parseObject = (ParseObject) saveList.remove(0);
//                            Display.getInstance().callSerially(
//                                    () -> {
//                                        Log.p("BACKGROUND SAVE thread, saving single unsaved parseObject=" + parseObject);
//                                        saveAndCacheImpl((ParseObject) parseObject, true);
//                                    }
//                            );
//                        } else {
//                            String parseClass = ((ParseObject) saveList.get(0)).getClassName();
////                            ParseObject parseObject;
//                            while (!saveList.isEmpty()
//                                    && parseClass.equals(((ParseObject) saveList.get(0)).getClassName())
//                                    && ((ParseObject) saveList.get(0)).getObjectIdP() == null) { //loop condition always met for first element => will remove at least one elt from saveList, so loop termination guaranteed
////                                if (batchSaveList == null)
////                                    batchSaveList = new ArrayList();
//                                ParseObject parseObject = (ParseObject) saveList.remove(0);
//                                batchSaveList.add(parseObject);
//                            }
////                            if (batchSaveList != null && batchSaveList.size() > 1) {
//                            if (batchSaveList.size() > 1) { //there are multiple parseObjects with same class to batch save
//                                Log.p("BACKGROUND BACTH saving +" + batchSaveList.size() + " elements: " + batchSaveList);
//                                Display.getInstance().callSerially(
//                                        () -> {
//                                            Log.p("BACKGROUND SAVE thread, BATCH saving batchSaveList=" + batchSaveList);
//                                            saveBatch(batchSaveList, true); //must save to cache (in case there are new created objects)
//                                            batchSaveList.clear();
//                                        });
//                            } else { //there is only ONE parseObject to save
//                                ParseObject parseObject2 = batchSaveList.remove(0);
//                                if (parseObject2 instanceof ItemList && ((ItemList) parseObject2).isNoSave())
//                                    Log.p("BACKGROUND saving IGNORE: " + parseObject2);
//                                else {
//                                    Log.p("BACKGROUND saving single object: " + parseObject2);
//                                    Display.getInstance().callSerially(
//                                            () -> {
//                                                saveAndCacheImpl((ParseObject) parseObject2, true);
//                                                batchSaveList.clear();
//                                            }
//                                    ); //must save to cache (in case there are new created objects)
//                                }
//                            }
////                            batchSaveList = null;
////                            batchSaveList.clear();
//                        }
//                    }
//                    Util.wait(LOCK);
//                }
//            }
////            }
//        });
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveInBackground(List<ParseObject> parseObjects, Runnable postSaveAction) {
//        for (ParseObject parseObject : parseObjects) {
//            saveInBackground(parseObject);
//        }
//        saveInBackground(postSaveAction);
//    }
    //    public void saveInBackgroundXXX(List<ParseObject> parseObjects) {
    ////        saveImpl(anyParseObject);
    //        if (parseObjects == null || parseObjects.size() == 0) {
    //            return;
    //        }
    //        if (backgroundSaveThread == null) {
    //            backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
    //            backgroundSaveThread.run(() -> {
    ////            synchronized (backgroundSaveLOCK) {
    //                ParseObject parseObject;
    //                synchronized (LOCK) {
    //                    while (true) {
    //                        com.codename1.io.Util.wait(LOCK);
    //                        while (!backgroundSavePendingList.isEmpty()) {
    //                            parseObject = (ParseObject) backgroundSavePendingList.remove(0);
    //                            saveImpl(parseObject, true);
    //                        }
    //                    }
    //                }
    //                //<editor-fold defaultstate="collapsed" desc="comment">
    ////            for (ParseObject parseObject : parseObjects) {
    //////                if (parseObject.getObjectIdP()==null) { //if not previously saved, save firs to get the ObjectId used by cache
    ////                saveImpl(parseObject, true);
    //////                cachePut(parseObject); //first cache all objects
    //////                } else
    //////                saveToParseOnlyNoCaching(parseObject);
    ////            }
    ////            }
    ////</editor-fold>
    //            });
    //        }
    //
    //        synchronized (LOCK) {
    //            for (ParseObject parseObject : parseObjects) {
    //                backgroundSavePendingList.add(parseObject);
    //            }
    //            LOCK.notify();
    //        }
    //    }
    //</editor-fold>
    private void saveInBackground(Runnable postSaveAction, ParseObject... parseObjects) {
        saveInBackground(new ArrayList(Arrays.asList(parseObjects)), postSaveAction);
    }

    public void saveInBackground(ParseObject... parseObjects) {
        saveInBackground(null, parseObjects);
    }

    public void saveInBackground(Runnable postSaveAction) {
        saveInBackground(new ArrayList(), postSaveAction);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveInBackground(ParseObject anyParseObject) {
//        if (anyParseObject != null) {
//            saveInBackground(anyParseObject, null);
//        }
//    }
//    private List<Item> saveTemplateCopyWithSubtasksInBackgroundImpl(List<Item> tasksInSaveOrder, Item projectOrItem) {
//    private List saveTemplateCopyWithSubtasksInBackgroundImplXXX(List tasksInSaveOrder, Item projectOrItem) {
//        if (!projectOrItem.isProject() && (projectOrItem.getObjectIdP() == null || projectOrItem.isDirty())) {
//            tasksInSaveOrder.add(projectOrItem);
//            return tasksInSaveOrder;
//        } else {
//            //first add all subtasks (so they are saved and given an ObjectId before their project is saved)
//            for (Object obj : projectOrItem.getListFull()) {
//                Item item = (Item) obj;
//                tasksInSaveOrder.addAll(saveTemplateCopyWithSubtasksInBackgroundImplXXX(tasksInSaveOrder, item));
//            }
//            tasksInSaveOrder.add(projectOrItem);
//        }
//        return tasksInSaveOrder;
//    }
//</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * save eg a template copy in the right order (all leaf subtasks before
     * their owner project, so all are given objectiIds before saving references
     * to them)
     *
     * @param projectOrItem
     */
//    public void saveTemplateCopyWithSubtasksInBackgroundXXX(Item projectOrItem) {
////        saveInBackground((List<ParseObject>)saveTemplateCopyWithSubtasksInBackgroundImpl(new ArrayList<Item>(), projectOrItem));
//        saveInBackground(saveTemplateCopyWithSubtasksInBackgroundImplXXX(new ArrayList(), projectOrItem));
//    }
    //    /**
    //     * return all categories for this item
    //     *
    //     * @param item
    //     * @return
    //     */
    //    public List<Category> getCategories(Item item) {
    //        List<Category> results = null;
    //        if (item.getObjectId() != null) { //Parse doesn't support queries on just created (not saved) objects
    //            ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
    //            query.whereEqualTo(Category.PARSE_ITEMLIST, item);
    ////        if (item.)
    //            try {
    ////             results = (Set<Category>) query.find();
    //                results = (List<Category>) query.find();
    //            } catch (ParseException ex) {
    //                Log.e(ex);
    //            }
    //        }
    //        return results;
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public List<WorkSlot> getWorkSlotsN(ParseObject any) {
    //        return null;
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * get the workslots for an item
     *
     * @param item
     * @return
     */
    //    public List<WorkSlot> getWorkSlotsXXX(Item item) {
    //        List<WorkSlot> results = null;
    //        if (item.getObjectId() != null) { //Parse doesn't support queries on just created (not saved) objects
    //            ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
    //            query.whereEqualTo(WorkSlot.PARSE_OWNER_ITEM, item);
    //            query.addAscendingOrder(WorkSlot.PARSE_START_TIME);
    ////            query.include(WorkSlot.)
    //            try {
    //                results = (List<WorkSlot>) query.find();
    //                //no point in caching workslots, they're only used for a single List/Item and caching will only help the second time displaying them
    //            } catch (ParseException ex) {
    //                Log.e(ex);
    //            }
    //        }
    //        return results;
    //    }
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="comment">
    //    public List<WorkSlot> getWorkSlotsN(Item item, int index, int amount) {
    //        List<WorkSlot> results = null;
    //        if (item.getObjectId() != null) { //Parse doesn't support queries on just created (not saved) objects
    //            ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
    //            query.whereEqualTo(WorkSlot.PARSE_OWNER_ITEM, item);
    //            query.setSkip(index).setLimit(amount);
    ////            query.include(WorkSlot.)
    //            try {
    //                results = (List<WorkSlot>) query.find();
    //            } catch (ParseException ex) {
    //                Log.e(ex);
    //            }
    //        }
    //        return results;
    //    }
    //</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @param itemWithWorkSlots
     * @return null if no workslots defined
     */
    //    public List<WorkSlot> getWorkSlotsN(ItemList itemList) {
    //    public List<WorkSlot> getWorkSlotsN(ItemAndListCommonInterface itemWithWorkSlots) {
//    private WorkSlotList getWorkSlotsNXXX(ItemAndListCommonInterface itemWithWorkSlots) {
//        return DAO.this.getWorkSlotsNXXX(itemWithWorkSlots, true);
//    }
//
//    private WorkSlotList getWorkSlotsNXXX(ItemAndListCommonInterface itemWithWorkSlots, boolean onlyReturnFutureWorkSlots) {
//        if (itemWithWorkSlots == null || (itemWithWorkSlots instanceof ParseObject && ((ParseObject) itemWithWorkSlots).getObjectIdP() == null)) {
//            return null;
//        }
//
//        WorkSlot workSlot;
////        List<WorkSlot> results = null;
//        WorkSlotList results = null;
////        long now = System.currentTimeMillis();
//        boolean reload = true; //if one workSlot has expired, we'll need to start over loading since it may regenerate several future
//        while (reload) {
//            reload = false;
////            results = new ArrayList(); //reset list if need for reload
//            results = new WorkSlotList(); //reset list if need for reload
//            if (true || cacheWorkSlots != null) {
//                Vector workSlotKeys = cacheWorkSlots.getKeysInCache();
////                Vector keys = cache.getKeysInCache();
//
//                for (Object key : workSlotKeys) {
//                    workSlot = (WorkSlot) cacheWorkSlots.get(key);
////<editor-fold defaultstate="collapsed" desc="comment">
////                Object o = cache.get(key);
////                if (o instanceof WorkSlot) {
////                workSlot = (WorkSlot) cacheWorkSlots.get(key);
////                    workSlot = (WorkSlot) o;
////                    ASSERT.that(workSlot != null, "workSlot key " + key + " return null from cache");
////                if (workSlot != null) {
////</editor-fold>
//                    ASSERT.that(workSlot != null, () -> "WorkSlot in cache for key=\"" + key + "\" is null. Key type=" + (key instanceof String ? "String" : (key instanceof Integer ? "Integer" : (key instanceof Long ? "Long" : "other"))));
//                    Object owner = workSlot.getOwner();
//                    //only return workslots with itemWithWorkSlots as owner and where endTime is in the past
//                    if (owner != null && owner.equals(itemWithWorkSlots)) {
//                        RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
////                        repeatRule=(RepeatRuleParseObject)DAO.getInstance().fetchIfNeededReturnCachedIfAvail(repeatRule); //not necessary, dont in workSlot.getRepeatRule()
//                        if (repeatRule != null && workSlot.getEndTime() < System.currentTimeMillis() && repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (workSlot.getRepeatRule()!=null) { //if repeating workslot
////                        RepeatRuleParseObject repeatRule =workSlot.getRepeatRule();
////                        //If workslot fully in the past, but still in list of active instances, then it has expired since last loading
////                        if (workSlot.getEndTime()<now && repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
////                            RepeatRuleObjectInterface newWorkSlot = repeatRule.updateRepeatInstancesOnDoneCancelOrDelete(workSlot);
////                            workSlot=(WorkSlot)newWorkSlot;
////                        }
////                    }
////                    workSlot = workSlot.checkIfExpiredAndReturnNewRepeatInstance(now);
////                        if (workSlot.getEndTime() < System.currentTimeMillis()) { //if workSlot has expired
////                            RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
////                            if (repeatRule != null) { //if repeating workslot
//                            //DONE!!!! continue generating new instances until we get enough future ones as defined by repeatRule
////                            if (repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
////</editor-fold>
//                            repeatRule.updateRepeatInstancesOnCancelDeleteOrExpired(workSlot); //if needed, will generate all slots until the right number of current workSlots
//                            reload = true;
//                            break; //exit the for loop to reload workslots
//                        } else if (!onlyReturnFutureWorkSlots || workSlot.getEndTime() > System.currentTimeMillis()) {
//                            results.add(workSlot);
//                        }
//                    }
//                }
//            }
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////            else {
////                Vector keys = cache.getKeysInCache();
////                for (Object key : keys) {
//////                workSlot = (WorkSlot) cacheWorkSlots.get(key);
////                    Object o = cache.get(key);
////                    if (o instanceof WorkSlot) {
//////                workSlot = (WorkSlot) cacheWorkSlots.get(key);
////                        workSlot = (WorkSlot) o;
//////                    ASSERT.that(workSlot != null, "workSlot key " + key + " return null from cache");
//////                if (workSlot != null) {
////                        Object owner = workSlot.getOwner();
////                        //only return workslots with itemWithWorkSlots as owner and where endTime is in the past
////                        if (owner != null && owner.equals(itemWithWorkSlots)) {
////                            RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
////                            if (repeatRule != null
////                                    && workSlot.getEndTime() < System.currentTimeMillis()
////                                    && repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if (workSlot.getRepeatRule()!=null) { //if repeating workslot
//////                        RepeatRuleParseObject repeatRule =workSlot.getRepeatRule();
//////                        //If workslot fully in the past, but still in list of active instances, then it has expired since last loading
//////                        if (workSlot.getEndTime()<now && repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
//////                            RepeatRuleObjectInterface newWorkSlot = repeatRule.updateRepeatInstancesOnDoneCancelOrDelete(workSlot);
//////                            workSlot=(WorkSlot)newWorkSlot;
//////                        }
//////                    }
//////                    workSlot = workSlot.checkIfExpiredAndReturnNewRepeatInstance(now);
//////</editor-fold>
//////                        if (workSlot.getEndTime() < System.currentTimeMillis()) { //if workSlot has expired
//////                            RepeatRuleParseObject repeatRule = workSlot.getRepeatRule();
//////                            if (repeatRule != null) { //if repeating workslot
////                                //DONE!!!! continue generating new instances until we get enough future ones as defined by repeatRule
//////                            if (repeatRule.isRepeatInstanceInListOfActiveInstances(workSlot)) {
////                                repeatRule.updateRepeatInstancesOnCancelDeleteOrExpired(workSlot); //if needed, will generate all slots until the right number of current workSlots
////                                reload = true;
////                                break; //exit the for loop to reload workslots
//////                            }
//////                            }
////                            } else if (!onlyReturnFutureWorkSlots || workSlot.getEndTime() > System.currentTimeMillis()) {
////                                results.add(workSlot);
////                            }
////                        }
////                    }
//////                }
////                }
////            }
////</editor-fold>
////        }
//        results.sortWorkSlotList();
//        //TODO!!! any way/need to check if workslots have been cached?! (empty results cannot be used since there may simply be no workslots)
////<editor-fold defaultstate="collapsed" desc="comment">
//
//        if (false && (itemWithWorkSlots instanceof ParseObject
//                && ((ParseObject) itemWithWorkSlots).getObjectIdP() != null)) { //Parse doesn't support queries on just created (not saved) objects
//            ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//            query.whereEqualTo(WorkSlot.PARSE_OWNER_LIST, itemWithWorkSlots);
////            query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_START_TIME, new Date());
//            query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_END_TIME, new Date()); //enough to search for endTime later than Now
//            query.addAscendingOrder(WorkSlot.PARSE_START_TIME); //sort on startTime
//            if (false) {
//                query.addAscendingOrder(WorkSlot.PARSE_DURATION); //sort on duration to have smallest slots first (to have WorkSlotDefinition ignore all with same start time except the last
//
//            }
////            ParseQuery<WorkSlot> query2 = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
////            query2.whereEqualTo(WorkSlot.PARSE_OWNER_LIST, itemList);
////            query2.whereGreaterThanOrEqualTo(WorkSlot.PARSE_END_TIME, new Date());
////            ParseQuery<WorkSlot> queryOr = ParseQuery.getOrQuery(new ArrayList({query, query2}));
//            try {
////                ParseQuery<WorkSlot> queryOr = ParseQuery.getOrQuery(Arrays.asList(query, query2));
////                results = (List<WorkSlot>) query.find();
////                results = (List<WorkSlot>) query.find();
////                results = (WorkSlotList) query.find();
//                results = new WorkSlotList(query.find());
////no point in caching workslots, they're only used for a single List/Item and caching will only help the second time displaying them
//
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
////</editor-fold>
////        }
//        if (results == null || results.size() == 0) {
//            return null;
//        } else {
//            return results;
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<WorkSlot> initWorkSlotListsForWorkSlotOwnersXXX() { //only previously used in Repair
//        WorkSlot workSlot;
//        WorkSlotList results = new WorkSlotList(); //reset list if need for reload
//        List<WorkSlot> unallocated = new ArrayList<>(); //reset list if need for reload
//        HashSet<ItemAndListCommonInterface> owners = new HashSet<>(); //list of every owner with workslots
//        Vector workSlotKeys = cacheWorkSlots.getKeysInCache();
//        for (Object key : workSlotKeys) {
//            workSlot = (WorkSlot) cacheWorkSlots.get(key);
//            ASSERT.that(workSlot != null, () -> "WorkSlot in cache for key=\"" + key + "\" is null. Key type=" + (key instanceof String ? "String" : (key instanceof Integer ? "Integer" : (key instanceof Long ? "Long" : "other"))));
//            ItemAndListCommonInterface owner = workSlot.getOwner();
//            ASSERT.that(owner != null, "WorkSlot " + workSlot + " has no owner - should be deleted");
//
//            if (owner == null) {
//                unallocated.add(workSlot);
//            } else {
//                WorkSlotList workslots = owner.getWorkSlotListN();
//                if (workslots == null) {
//                    workslots = new WorkSlotList();
//                }
//                if (!workslots.contains(workSlot)) {
//                    workslots.add(workSlot);
//                }
//                owner.setWorkSlotList(workslots);
//                owners.add(owner); //collect list of owners
//            }
//        }
//        //now all workslots have been added to their owner
//        for (ItemAndListCommonInterface owner : owners) {
////            WorkSlotList sortedList = owner.getWorkSlotListN();
//            owner.setWorkSlotList(owner.getWorkSlotListN().sortWorkSlotList()); //sort list
////            sortedList.sortWorkSlotList();
////            owner.setWorkSlotList(sortedList);
//            saveInBackground((ParseObject) owner); //save owner with updated list
//        }
////        saveBatch(Arrays.asList(owners.toArray()));
//        return unallocated;
//    }
//</editor-fold>
    /**
     * get all workslots that have at least some available time within the
     * interval between startDate and endDate. after* startDate and NOT after
     * endTime. E.g. slot.endDate is bigger or equial to startDate and
     * slot.startTime is smaller than endDate.
     *
     * @param startDate
     * @param endDate
     * @return
     */
//    public WorkSlotList getWorkSlotsN(Date startDate, Date endDate) {
//    public WorkSlotList getWorkSlots(Date startDate) {
    public List<WorkSlot> getWorkSlots(Date startDate) {
        return getWorkSlots(startDate, new Date(MyDate.MAX_DATE));
    }

//    private WorkSlotList getWorkSlots(Date startDate, Date endDate) {
    /**
     * return sorted on startDate
     *
     * @param startDate
     * @param endDate
     * @return
     */
    private List<WorkSlot> getWorkSlots(Date startDate, Date endDate) {

        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//        query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_START_TIME, startDate); //enough to search for endTime later than Now
        query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_END_TIME, startDate); //enough to search for endTime later than Now
//        query.whereLessThan(WorkSlot.PARSE_START_TIME, endDate);
        query.whereLessThan(WorkSlot.PARSE_START_TIME, endDate);
        query.addAscendingOrder(WorkSlot.PARSE_START_TIME); //sort on startTime
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        WorkSlotList results = null;// = new WorkSlotList();
        List<WorkSlot> list = null;

        try {
            list = query.find();
//            results = new WorkSlotList(list);
//            results = new WorkSlotList(list);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        list = fetchListElementsIfNeededReturnCachedIfAvail(list);
        return list;
//        return results;
//        return new ArrayList();
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<WorkSlot> getWorkSlotsInThePast() {
//    public WorkSlotList getWorkSlotsInThePastXXX() {
//        return getWorkSlotsInThePast(new Date(System.currentTimeMillis() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
//    }
//    public List<WorkSlot> getWorkSlotsInThePast(Date startDate) {
//    public WorkSlotList getWorkSlotsInThePastXXX(Date startDate) {
////        List<WorkSlot> results = new ArrayList();
//        WorkSlotList results = new WorkSlotList();
////        WorkSlot workSlot;
//        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
////            query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_END_TIME, new Date(System.currentTimeMillis() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS)); //enough to search for endTime later than Now
//        query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_START_TIME, startDate); //enough to search for endTime later than Now
//        query.whereLessThan(WorkSlot.PARSE_START_TIME, new Date());
//        query.addAscendingOrder(WorkSlot.PARSE_START_TIME); //sort on startTime
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
////            results = (List<WorkSlot>) query.find();
//            results = new WorkSlotList(query.find());
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
    /**
     *
     * get all workslots (including past/expired) ones
     *
     * @return null if no workslots defined
     */
//    public List<WorkSlot> getAllWorkSlotsFromParse() {
    WorkSlotList getAllWorkSlotsFromParse() {
        List<WorkSlot> results = null;
//        WorkSlotList results = null;
        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
        query.addAscendingOrder(WorkSlot.PARSE_START_TIME);
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        try {
//            results = new WorkSlotList(null, query.find(), true);
            results = (List<WorkSlot>) query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return new WorkSlotList(null, fetchListElementsIfNeededReturnCachedIfAvail(results), true);
    }

    /**
     *
     * get all filterSortDefs (including past/expired) ones
     *
     * @return empty list if no filterSortDefs defined
     */
    public List<FilterSortDef> getAllFilterSortDefsFromParse() {
        List<FilterSortDef> results = null;
        ParseQuery<FilterSortDef> query = ParseQuery.getQuery(FilterSortDef.CLASS_NAME);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            results = (List<FilterSortDef>) query.find();
//            cacheList(results);
//            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return fetchListElementsIfNeededReturnCachedIfAvail(results);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * return a list of all WorkSlots that has this ItemList as owner.
//     *
//     * @param itemList
//     * @return
//     */
////    List<WorkSlot> getWorkSlotsN(ItemList itemList) {
////        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
////        query.whereEqualTo(WorkSlot.PARSE_OWNER_LIST, itemList);
////        List<WorkSlot> results = null;
////        try {
////            results = (List<WorkSlot>) query.find();
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////        return results;
////    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private ParseQuery setupAlarmQuery(ParseQuery queryAlarm, String parseAlarmField, Date timeAfterWhichToFindNextItem, Date lastTimeForNextAlarm) {
//        return setupAlarmQuery(queryAlarm, parseAlarmField, timeAfterWhichToFindNextItem, lastTimeForNextAlarm, 0);
//    }
    /**
     * sets the standard query conditions when searching for an item with alarms
     * in a given interval. Always excludes DONE and CANCELLED items.
     *
     * @param queryAlarm
     * @param parseAlarmField
     * @param timeAfterWhichToFindNextItem
     * @param lastTimeForNextAlarm if different from 0, this will set the upper
     * inclusive limit for the search (less than or equal)
     * @param queryLimit limits the number of return results, 0 means no limit
     * (=>1000)
     * @return
     */
//    private ParseQuery setupAlarmQueryXXX(ParseQuery queryAlarm, String parseAlarmField, Date timeAfterWhichToFindNextItem, Date lastTimeForNextAlarm, int queryLimit) {
////        queryAlarm.whereGreaterThanOrEqualTo(parseAlarmField, timeAfterWhichToFindNextItem);
//        queryAlarm.whereGreaterThan(parseAlarmField, timeAfterWhichToFindNextItem);
//        if (lastTimeForNextAlarm.getTime() != 0) {
//            queryAlarm.whereLessThanOrEqualTo(parseAlarmField, lastTimeForNextAlarm);
//        }
//        queryAlarm.addAscendingOrder(parseAlarmField); //sort on the alarm field
//        if (queryLimit > 0) {
//            queryAlarm.setLimit(queryLimit); //only return queryLimit first results (the queryLimit smallest alarms)
//        }//        query.whereGreaterThanOrEqualTo(Item.PARSE_ALARM_DATE_ARRAY, earliestTimeForNextAlarm); //fetch next-coming alarm in an array(??)
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE or CANCELLED
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE or CANCELLED
//        queryAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        queryAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        return queryAlarm;
//    }
    /**
     * returns a number of Items with alarms set *after*
     * timeAfterWhichToFindNextItem. Does not return the full Item, just the
     * fields needed for setting the alarms (Item.PARSE_TEXT,
     * Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE,
     * Item.PARSE_WAITING_ALARM_DATE). Does not replace the items with their
     * cached instances (since this function may be called when
     *
     * @param timeAfterWhichToFindNextItemWithAlarm last time up to and
     * *including* which alarms were previously set, new alarms are found
     * strictly *after* this time (Greater Than, but not Equal)
     * @param lastTimeForNextAlarm
     * @return
     */
//    public static List<Item> getItemsWithNormalAlarmsXXX(int maxNumberItemsToRetrieve, Date timeAfterWhichToFindNextItemWithAlarm) {
//        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
//        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)??
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
//        query.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItemWithAlarm);
//        query.whereLessThan(Item.PARSE_ALARM_DATE, new Date(timeAfterWhichToFindNextItemWithAlarm.getTime() + MyPrefs.alarmDaysAheadToFetchFutureAlarms.getInt() * MyDate.DAY_IN_MILLISECONDS)); //don't search more than 30 days ahead in the future
//        query.addAscendingOrder(Item.PARSE_ALARM_DATE); //sort on the alarm field
//        query.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.setLimit(maxNumberItemsToRetrieve);
//        try {
//            List<Item> results = query.find();
////            fetchAllElementsInSublist(results); //NO - this may be called while app is not active, so cahce not loaded
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//    public static List<Item> getItemsWithWaitingAlarmsXXX(int maxNumberItemsToRetrieve,
//            Date timeAfterWhichToFindNextItemWithAlarm
//    ) {
//        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
//        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)??
//
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
//        query.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItemWithAlarm);
//        query.whereLessThan(Item.PARSE_WAITING_ALARM_DATE, new Date(timeAfterWhichToFindNextItemWithAlarm.getTime() + MyPrefs.alarmDaysAheadToFetchFutureAlarms.getInt() * MyDate.DAY_IN_MILLISECONDS)); //don't search more than 30 days ahead in the future
//        query.addAscendingOrder(Item.PARSE_WAITING_ALARM_DATE); //sort on the alarm field
//        query.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.setLimit(maxNumberItemsToRetrieve);
//        try {
//            List<Item> results = query.find();
////            fetchAllElementsInSublist(results); //NO - this may be called while app is not active, so cahce not loaded
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//
//    }
//</editor-fold>
    /**
     * retrieve items with future alarms. NB. The future alarm field may be
     * outdated if the item has not been updated after time caught up with a
     * previous future alarm. So, every item must be tried
     *
     * @param maxNumberItemsToRetrieve
     * @return only future dates, sorted
     */
    public List<Item> getItemsWithNextcomingAlarms(int maxNumberItemsToRetrieve) {
        return getItemsWithNextcomingAlarms(maxNumberItemsToRetrieve, false);
    }

    public List<Item> getItemsWithNextcomingAlarms(int maxNumberItemsToRetrieve, boolean backgroundFetch) {
        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)??
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
        if (false) {
            query.whereLessThan(Item.PARSE_NEXTCOMING_ALARM, new Date(MyDate.currentTimeMillis() + MyPrefs.alarmDaysAheadToFetchFutureAlarms.getInt() * MyDate.DAY_IN_MILLISECONDS)); //don't search more than 30 days ahead in the future // NO real reason to limit the number 
        } else {
            query.whereExists(Item.PARSE_NEXTCOMING_ALARM);
            query.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on date
        }
        if (false) {
            query.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on the alarm field
        }
        if (backgroundFetch) {
            query.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE, Item.PARSE_SNOOZE_DATE))); // just fetchFromCacheOnly the data needed to set alarms //TODO!! investigate if only fetching the relevant fields is a meaningful optimziation (check that only the fetched fields are used!)
        } else {
            query.selectKeys(new ArrayList()); // just fetch minimu
        }
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.setLimit(maxNumberItemsToRetrieve);
        List<Item> results;
        int numberOfItemsRetrieved;

        do { //repeat until we have at least some future results (if all retrieved results had nextcoming data in the past)
//        List<Item> results = null;
            results = null;
            try {
                results = query.find();
//            fetchAllElementsInSublist(results); //NO - this may be called while app is not active, so cahce not loaded
//            return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            if (!backgroundFetch) {
                fetchListElementsIfNeededReturnCachedIfAvail(results); //only get in cache when not started in background
            }
            numberOfItemsRetrieved = results.size();

            //remove all items where getNextcomingAlarmDateD is no longer valid (time has passed and getNextcomingAlarm() returns another later value or null - meaning no more alarms)
            List<Item> expired = new ArrayList();
            //Solution from http://stackoverflow.com/questions/122105/what-is-the-best-way-to-filter-a-java-collection :
            Iterator<Item> it = results.iterator();
            Date firstDate;
//            Date futureDate;
            Date lastAlarmDate = new Date(MyDate.MIN_DATE); //time of the last alarm received from Parse server
            while (it.hasNext()) {
                Item item = it.next();
                firstDate = item.getNextcomingAlarmDateD();
                if (firstDate != null && firstDate.getTime() > lastAlarmDate.getTime()) {
                    lastAlarmDate = firstDate; //gather latest alarm date as we iterate through the items
                }
                Date futureDate = item.getNextcomingAlarm();
                if (futureDate == null || futureDate.getTime() != firstDate.getTime()) { //if there's no longer a future alarm, or it has changed, add to expired for an update
                    expired.add(item);
                    it.remove();
                }
            }

            //if any of the items with expired alarms have a new alarm that is within the range of the other items, then keep it (add it back in the list)
//            Date lastAlarmDate = results.get(results.size() - 1).getNextcomingAlarmDateD();
            List<ParseObject> updated = new ArrayList();
            for (int i = 0, size = expired.size(); i < size; i++) {
                //TODO! move this processing into the loop above
                Item expItem = expired.get(i);
//            Date firstFutureAlarm = expItem.getNextcomingAlarm(); //optimization: this statement and next both call Item.getAllFutureAlarmRecordsSorted() which is a bit expensive
//            expItem.updateNextcomingAlarm();//update the first alarm to new value (or null if no more alarms). NB! Must update even when no first alarm (firstFutureAlarm returns null)
                expItem.updateNextcomingAlarm();//update the first alarm to new value (or null if no more alarms). NB! Must update even when no first alarm (firstFutureAlarm returns null)
                Date newFirstAlarm = expItem.getNextcomingAlarmDateD(); //optimization: this statement and next both call Item.getAllFutureAlarmRecordsSorted() which is a bit expensive
                updated.add(expItem); //save for a ParseServer update whether now null or with new value
                if (newFirstAlarm != null && newFirstAlarm.getTime() <= lastAlarmDate.getTime()) {
                    results.add(expItem); //add the updated Items which do have a future alarm within the same interval as the other alarms retrieved (less than lastAlarmDate)
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
// save the updated Items in a batch //optimization: do this as background task to avoid blocking the event thread
//            if (!updated.isEmpty()) {
//                try {
//                    ParseBatch parseBatch = ParseBatch.create();
//                    parseBatch.addObjects(updated, ParseBatch.EBatchOpType.UPDATE);
//                    parseBatch.execute();
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                }
//            }
//</editor-fold>
//            saveBatch(updated);
            saveInBackground(updated);
        } while (!backgroundFetch && (results.isEmpty() && maxNumberItemsToRetrieve == numberOfItemsRetrieved)); //repeat in case every retrieved alarm was expired and we retrieved the maximum number (meaning there are like more alarms to retrieve)
//        results.addAll(updated); //add the updated ones to results
        //sort the results
        if (results != null && !results.isEmpty()) {
            Collections.sort(results, (object1, object2) -> {
                if (((Item) object1).getNextcomingAlarmDateD() == null) {
                    return -1;
                }
                if (((Item) object2).getNextcomingAlarmDateD() == null) {
                    return 1;
                }
                return FilterSortDef.compareDate(object1.getNextcomingAlarmDateD(), object2.getNextcomingAlarmDateD());
            });
        }
        return results;
    }

    public List<Item> getItemsWithAlarms(int maxNumberItemsToRetrieve, Date timeAfterWhichToFindNextItemWithAlarm, Date timeAfterWhichToFindNextItemWithWaitingAlarm, Date timeAfterWhichToFindNextItemWithSnoozedAlarm, int daysAhead) {
        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)??

        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(queryReminderAlarm, true);
        queryReminderAlarm.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItemWithAlarm);
//        queryReminderAlarm.whereLessThan(Item.PARSE_ALARM_DATE, new Date(timeAfterWhichToFindNextItemWithAlarm.getTime() + MyDate.DAY_IN_MILLISECONDS * daysAhead)); //don't search more than 30 days ahead in the future
//        queryReminderAlarm.whereLessThan(Item.PARSE_ALARM_DATE, new Date(timeAfterWhichToFindNextItemWithAlarm.getTime() + MyDate.DAY_IN_MILLISECONDS * daysAhead)); //don't search more than 30 days ahead in the future
        queryReminderAlarm.addAscendingOrder(Item.PARSE_ALARM_DATE); //sort on the alarm field
        queryReminderAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingAlarm, true);
        queryWaitingAlarm.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItemWithWaitingAlarm);
//        queryWaitingAlarm.whereLessThan(Item.PARSE_WAITING_ALARM_DATE, new Date(timeAfterWhichToFindNextItemWithWaitingAlarm.getTime() + MyDate.DAY_IN_MILLISECONDS * daysAhead)); //don't 
        queryWaitingAlarm.addAscendingOrder(Item.PARSE_WAITING_ALARM_DATE); //sort on the alarm field
        queryWaitingAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        ParseQuery<Item> querySnoozedAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(querySnoozedAlarm, true);
        querySnoozedAlarm.whereGreaterThan(Item.PARSE_SNOOZE_DATE, timeAfterWhichToFindNextItemWithSnoozedAlarm);
//        querySnoozedAlarm.whereLessThan(Item.PARSE_SNOOZE_DATE, new Date(timeAfterWhichToFindNextItemWithSnoozedAlarm.getTime() + MyDate.DAY_IN_MILLISECONDS * daysAhead)); //don't 
        querySnoozedAlarm.addAscendingOrder(Item.PARSE_SNOOZE_DATE); //sort on the alarm field
        querySnoozedAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        try {
            ParseQuery<Item> queryGetAllItemsWithAlarms = ParseQuery.getOrQuery(new ArrayList(Arrays.asList(queryReminderAlarm, queryWaitingAlarm, querySnoozedAlarm)));

            if (false) {
                queryGetAllItemsWithAlarms.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
            }
            queryGetAllItemsWithAlarms.setLimit(maxNumberItemsToRetrieve);
            List<Item> results = queryGetAllItemsWithAlarms.find();
//            fetchAllElementsInSublist(results); //NO - this may be called while app is not active, so cahce not loaded
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public List<Item> getItemsWithAlarmsInInterval(Date timeAfterWhichToFindNextItem, Date lastTimeForNextAlarm, int alarmMaxNumberItems) {
        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)?
//        int alarmQueryLimit = 32;
        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupStandardItemQuery(queryReminderAlarm);
        queryReminderAlarm.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
//<editor-fold defaultstate="collapsed" desc="comment">
//        queryReminderAlarm.setLimit(MyPrefs.alarmMaxNumberItemsForWhichToSetupAlarms.getInt());
//        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//also need to avoid items that are cancelled
//        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
//        queryReminderAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem, lastTimeForNextAlarm);
//</editor-fold>
        queryReminderAlarm.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem);
        queryReminderAlarm.whereLessThanOrEqualTo(Item.PARSE_ALARM_DATE, lastTimeForNextAlarm);
//        queryAlarm.addAscendingOrder(parseAlarmField); //sort on the alarm field //NOT necessary to sort?! (only reason could be to if we get more than the limit number back in which case some would be ignored)
        queryReminderAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        queryReminderAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        queryReminderAlarm.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
//<editor-fold defaultstate="collapsed" desc="comment">
//        setupItemQueryNoTemplatesLimit1000(queryWaitingAlarm);
//        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//also need to avoid items that are cancelled
//        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
//        queryWaitingAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem, lastTimeForNextAlarm);
//</editor-fold>
        queryWaitingAlarm.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem);
        queryWaitingAlarm.whereLessThanOrEqualTo(Item.PARSE_WAITING_ALARM_DATE, lastTimeForNextAlarm);
        queryWaitingAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        queryWaitingAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

//        ParseQuery<Item> queryGetAllItemsWithAlarms = null;
        try {
            //        ParseQuery<Item> queryOr = ParseQuery<Item>.getOrQuery(new ArrayList(){queryAlarm, queryWaitingAlarm});
            ParseQuery<Item> queryGetAllItemsWithAlarms = ParseQuery.getOrQuery(new ArrayList(Arrays.asList(queryReminderAlarm, queryWaitingAlarm)));

            queryGetAllItemsWithAlarms.selectKeys(new ArrayList(Arrays.asList(
                    Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms

//            queryGetAllItemsWithAlarms.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
            queryGetAllItemsWithAlarms.setLimit(alarmMaxNumberItems); //item that are NOT DONE or CANCELLED
//<editor-fold defaultstate="collapsed" desc="comment">
//            queryGetAllItemsWithAlarms.selectKeys(null); //just get search result, no data (these are cached)
//            setupItemQuery(queryGetAllItemsWithAlarms);
//            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//            //also need to avoid items that are cancelled
//            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
//            //TODO: need also to avoid items that are Waiting? No, it can be waiting, but you still want any alarm to work normally
//            queryGetAllItemsWithAlarms.addAscendingOrder(Item.PARSE_ALARM_DATE);
//            ArrayList<Item> results = new ArrayList<Item>(queryGetAllItemsWithAlarms.find());
//</editor-fold>
            List<Item> results = queryGetAllItemsWithAlarms.find();
//            fetchAllElementsInSublist(results);
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        query.setLimit(1);
//        ArrayList<Item> results = null;
//        try {
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//</editor-fold>
        return null;
    }

    /**
     * get the next item with an alarm (normal, waiting) strictly after (greater
     * than) the indicated time limit. Use with time of previous item alarm to
     * get the next one. Only called when cache is initialized.
     *
     * @param timeAfterWhichToFindNextItem
     * @return
     */
    public Item getNextItemWithAlarm(Date timeAfterWhichToFindNextItem) {
        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(queryReminderAlarm, true);
//        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
        queryReminderAlarm.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem);
        queryReminderAlarm.setLimit(1); //only return queryLimit first results (the queryLimit smallest alarms)
//        queryReminderAlarm.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
        queryReminderAlarm.selectKeys(new ArrayList()); // just fetchFromCacheOnly the objectId - assumes items are cached already
        queryReminderAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingAlarm, true);
//        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
        queryWaitingAlarm.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem);
        queryWaitingAlarm.setLimit(1); //only return queryLimit first results (the queryLimit smallest alarms)
//        queryWaitingAlarm.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
        queryWaitingAlarm.selectKeys(new ArrayList()); // just fetchFromCacheOnly the objectId - assumes items are cached already
        queryWaitingAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        Item nextItemWithAlarm = null;

        try {
            List<Item> resultsNextAlarm = queryReminderAlarm.find();
            List<Item> resultsNextWaiting = queryWaitingAlarm.find();

            if (resultsNextAlarm != null && resultsNextAlarm.size() >= 1) {
                if (resultsNextWaiting != null && resultsNextWaiting.size() >= 1) {
                    if (resultsNextAlarm.get(0).getAlarmDateD().getTime() < resultsNextWaiting.get(0).getWaitingAlarmDateD().getTime()) {
//                        return resultsNextAlarm.get(0);
                        nextItemWithAlarm = resultsNextAlarm.get(0);

                    } else {
//                        return resultsNextWaiting.get(0);
                        nextItemWithAlarm = resultsNextWaiting.get(0);
                    }
                } else {
//                    return resultsNextAlarm.get(0);
                    nextItemWithAlarm = resultsNextAlarm.get(0);
                }
            } else if (resultsNextWaiting != null && resultsNextWaiting.size() >= 1) {
//                return resultsNextWaiting.get(0);
                nextItemWithAlarm = resultsNextWaiting.get(0);
            };

        } catch (ParseException ex) {
            Log.e(ex);
        }
        ASSERT.that(cache != null, "cache must be initialized");

        if (nextItemWithAlarm != null) {
//            nextItemWithAlarm = (Item) cache.get(nextItemWithAlarm.getObjectIdP());
            nextItemWithAlarm = (Item) cacheGet(nextItemWithAlarm.getObjectIdP());
        }
        return nextItemWithAlarm;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Item getNextItemWithAlarmNOGOOD(Date timeAfterWhichToFindNextItem) {
//        ParseQuery<Item> queryAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryAlarm);
////        setupAlarmQuery(queryAlarm, Item.PARSE_NEXTCOMING_ALARM, timeAfterWhichToFindNextItem, new Date(0), 1);
//        queryAlarm.whereGreaterThan(Item.PARSE_NEXTCOMING_ALARM, timeAfterWhichToFindNextItem);
////        queryAlarm.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on the alarm field - NOT necessary since get max 1
//        queryAlarm.setLimit(1); //only return queryLimit first results (the queryLimit smallest alarms)
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE or CANCELLED
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE or CANCELLED
//        queryAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//
//        try {
//            List<Item> results = queryAlarm.find();
//            if (results != null && results.size() >= 1) {
//                return results.get(0);
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//
//    public Item getNextItemWithAlarmOLD(Date timeAfterWhichToFindNextItem) {
//        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryReminderAlarm);
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//        //also need to avoid items that are cancelled
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
////        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, earliestTimeForNextAlarm, new Date(Long.MAX_VALUE));
////        queryReminderAlarm.setLimit(1); //only fetchFromCacheOnly the first one with the smallest alarm
////        queryReminderAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
//
//        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingAlarm);
////        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//        //also need to avoid items that are cancelled
////        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
////        queryWaitingAlarm.setLimit(1); //only fetchFromCacheOnly the first one with the smallest alarm
////        queryWaitingAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
//
////        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
////        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, earliestTimeForNextAlarm, new Date(Long.MAX_VALUE));
////
////        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
////        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, earliestTimeForNextAlarm, new Date(Long.MAX_VALUE));
//        try {
//            //        ParseQuery<Item> queryOr = ParseQuery<Item>.getOrQuery(new ArrayList(){queryAlarm, queryWaitingAlarm});
//            ParseQuery<Item> queryGetAllItemsWithAlarms = ParseQuery.getOrQuery(Arrays.asList(queryReminderAlarm, queryWaitingAlarm));
//            queryGetAllItemsWithAlarms.setLimit(1);
//            queryGetAllItemsWithAlarms.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
////            setupItemQuery(queryGetAllItemsWithAlarms);
////            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
//            //also need to avoid items that are cancelled
////            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
//            //UI: DONE: need also to avoid items that are Waiting? No, it can be waiting, but you still want any alarm to work normally
////            queryGetAllItemsWithAlarms.addAscendingOrder(Item.PARSE_ALARM_DATE);
////            queryGetAllItemsWithAlarms.setLimit(2);
//
////            ArrayList<Item> results = new ArrayList<Item>(queryGetAllItemsWithAlarms.find());
//            List<Item> results = queryGetAllItemsWithAlarms.find();
//            if (results != null && results.size() >= 1) {
//                if (results.size() == 1) {
//                    return results.get(0); //if only one result, return that
//                } else if (results.size() > 1) { //if more than one result, return the one with the smallest alarm
//                    Item itemWithSmallestAlarm = results.get(0); //assume first is smallest
//                    long smallestTime = Math.min(itemWithSmallestAlarm.getAlarmDate(), itemWithSmallestAlarm.getWaitingAlarmDate()); //find the smallest of the values
//                    Item item2 = results.get(1);
//                    if (item2.getAlarmDate() < smallestTime || item2.getWaitingAlarmDate() < smallestTime) { //if item2 has any smaller alarm, make item2 the smallest
//                        itemWithSmallestAlarm = item2;
//                    }
//                    return itemWithSmallestAlarm;
//                }
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @param screenId
     * @param objectId
     * @param filterName optional name in case multiple filters are saved for
     * same screen
     * @return
     */
//    public FilterSortDef getFilterSortDefXXX(String screenId, String objectId) { //, String filterName) {
//        ParseQuery<FilterSortDef> query = ParseQuery.getQuery(FilterSortDef.CLASS_NAME);
//        query.whereEqualTo(FilterSortDef.PARSE_SCREEN_ID, screenId);
//        query.whereEqualTo(FilterSortDef.PARSE_FILTERED_OBJECT_ID, objectId);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<FilterSortDef> results = null;
//
//        try {
//            results = query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        return results != null && results.size() > 0 ? results.get(0) : null;
//        FilterSortDef filterSortDef = results != null && results.size() > 0 ? results.get(0) : null;
//
//        if (filterSortDef != null) {
//            filterSortDef.getFilterOptions(); //update all the filter values
//        }
//        return filterSortDef;
//    }
//    public FilterSortDef getFilterSortDefXXX(String objectId) {//, String filterName) {
//        //TODO!! change the use of objectId as a string to usual Parse pattern
//        FilterSortDef filterSortDef = null;
//
//        try {
////            Object res = cache.get(objectId);
//            Object res = cacheGet(objectId);
//            if (res != null) {
//                filterSortDef = (FilterSortDef) res;
//            } else {
//                filterSortDef = new FilterSortDef();
//                filterSortDef.setObjectId(objectId);
//                filterSortDef.fetchIfNeeded();
//            }
//        } catch (ParseException ex) {
//            Log.e(ex); //TODO proper error handling
//        }
//        return filterSortDef;
//    }
//</editor-fold>
    /**
     * delete the parseObject. Ignore if null
     *
     * @param anyParseObject
     */
    public void deleteAndWait(ParseObject anyParseObject) {
        if (anyParseObject == null) {
            return;
        }
        String objId = anyParseObject.getObjectIdP();

        cacheDelete(anyParseObject);
        try {
            anyParseObject.delete();
        } catch (ParseException ex) {
            Log.e(ex);
        }

//        if (objId != null) {
//            cacheDelete(anyParseObject); //By here, the objId has been deleted from the parseobject
//        }
    }

//    public void deleteInBackground(TimerInstance anyParseObject) {
    public void deleteInBackground(ParseObject anyParseObject) {
        if (anyParseObject == null) {
            return;
        }
        String objId = anyParseObject.getObjectIdP();

//        if (backgroundSaveThread == null) {
//            backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
//        }
        EasyThread backgroundSaveThread = EasyThread.start("DAO.backgroundSave");
        if (Config.TEST) {
            ASSERT.that(anyParseObject != null && objId != null, "deleteInBackground with null pointer or no ObjectId");
        }

        cacheDelete(anyParseObject);
        backgroundSaveThread.run(() -> {
            try {
                anyParseObject.delete();
            } catch (ParseException ex) {
                Log.e(ex);
            }
        });

//        if (objId != null) {
//            cacheDelete(anyParseObject);
//        }
    }

    public void deleteBatch(List<ParseObject> listOfParseObjectsToBatchDelete) {
        if (!listOfParseObjectsToBatchDelete.isEmpty()) {
            try {
                ParseBatch parseBatch = ParseBatch.create();
                parseBatch.addObjects(listOfParseObjectsToBatchDelete, ParseBatch.EBatchOpType.DELETE);
                parseBatch.execute();
            } catch (ParseException ex) {
                Log.e(ex);
            }
            for (ParseObject o : listOfParseObjectsToBatchDelete) {
                cachePut(o);
            }
        }
//        save(anyParseObject, true);
    }

    //--------  CACHE DATA  -----------------------------------------------
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void cacheItemXXX(Item item) {
//        if (item.isProject()) {
//            List<Item> subtasks = item.getList();
//            Item temp;
////            for (Item subtask:subtasks) {
//            for (int i = 0, size = subtasks.size(); i < size; i++) {
//                Item subtask = subtasks.get(i);
//                if (!subtask.isDataAvailable() && (temp = (Item) cache.get(subtask.getObjectId())) != null) {
//                    subtasks.set(i, temp); //replace unloaded item with buffered instance
//                    cacheItemXXX(temp); //recurse on next level of subtasks
//                }
//            }
//        }
//        if (item.getOwner() instanceof Item && (temp = (Item) cache.get(item.getOwner())) != null) {
//            item.setOwner((Item) temp);
//        }
//        if (item.getTaskInterrupted() instanceof Item && (temp = (Item) cache.get(item.getOwner())) != null) {
//            item.setTaskInterrupted((Item) temp);
//        }
//    }
//</editor-fold>
    private void cacheAllItemsFromParse() {
//        cacheAllItemsFromParse(new Date(RepeatRuleParseObject.MIN_DATE));
        cacheAllItemsFromParse(new Date(MyDate.MIN_DATE), new MyDate());
    }

    private List<Item> getAllItemsFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<Item> results = null;
        try {
            results = query.find();
            Log.p("-------------->>>> Items fetched from Parse = " + results.size());
            //TODO!!!! show spinner
        } catch (ParseException ex) {
            Log.e(ex);
        }
        //do this update AFTER having cached all items to be able to update all subtask and ownerItem references to the cached instances
        return results;
    }

    private boolean cacheAllItemsFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<Item> results = getAllItemsFromParse(reloadUpdateAfterThis, now);
        cacheList(results);
        return !results.isEmpty();
    }

    private boolean cacheAllWorkSLotsFromParse() {
        return cacheAllWorkSlotsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
    }

//    private List<WorkSlot> getAllWorkSlotsFromParse(Date afterDate,
//    private WorkSlotList getAllWorkSlotsFromParse(Date afterDate, Date beforeDate) {
    private List<WorkSlot> getAllWorkSlotsFromParse(Date afterDate, Date beforeDate) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, afterDate);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, beforeDate);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<WorkSlot> results = null;

        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
//        return new WorkSlotList(results);
        return results;
    }

    private boolean cacheAllWorkSlotsFromParse(Date afterDate, Date beforeDate) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<WorkSlot> results = getAllWorkSlotsFromParse(afterDate, beforeDate);
//        WorkSlotList results = getAllWorkSlotsFromParse(afterDate, beforeDate);
        cacheList(results);
        return !results.isEmpty();
    }

//    private boolean cacheAllWorkSlotsFromParseXXX(Date afterDate, Date beforeDate) {
//        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
//        boolean result = false;
//        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, afterDate);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, beforeDate);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<WorkSlot> results = null;
//        try {
//            results = query.find();
////<editor-fold defaultstate="collapsed" desc="comment">
////            for (ParseObject o : results) {
//////                ASSERT.that(o.isDataAvailable(), "isDataAvailable() false for WorkSlot ObjId=" + o.getObjectId());
//////                cache.put(o.getObjectId(), o);
//////                ASSERT.that(o instanceof WorkSlot);
//////                if (cacheWorkSlots != null) {
//////                    cacheWorkSlots.put(o.getObjectIdP(), o); //cache WorkSlots in both caches (to avoid any weird edge cases)
//////                } else {
//////                    cache.put(o.getObjectIdP(), o);
//////                }
////                cachePut(o);
////                result = true;
////            }
////</editor-fold>
//            cacheList(results);
//            result = !results.isEmpty();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        //do this update AFTER having cached all items to be able to update all subtask and ownerItem references to the cached instances
////        for (ParseObject o : results) {
////            cacheItem((Item) o);
////        }
//        return result;
//    }
    private void cacheAllRepeatRulesFromParse() {
        cacheAllRepeatRulesFromParse(new Date(MyDate.MIN_DATE
        ), new Date(MyDate.MAX_DATE
        ));

    }

    private List<RepeatRuleParseObject> getAllRepeatRulesFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<RepeatRuleParseObject> query = ParseQuery.getQuery(RepeatRuleParseObject.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<RepeatRuleParseObject> results = null;

        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    private boolean cacheAllRepeatRulesFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<RepeatRuleParseObject> results = getAllRepeatRulesFromParse(reloadUpdateAfterThis, now);
        cacheList(results);

        return !results.isEmpty();
    }

//    private boolean cacheAllRepeatRulesFromParseXXX(Date reloadUpdateAfterThis, Date now) {
//        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
//        boolean result = false;
//        ParseQuery<RepeatRuleParseObject> query = ParseQuery.getQuery(RepeatRuleParseObject.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<RepeatRuleParseObject> results = null;
//
//        try {
//            results = query.find();
////            for (ParseObject o : results) {
//////                ASSERT.that(o.isDataAvailable(), "RepeatRule with no data ObjId"+o.getObjectId());
//////                cache.put(o.getObjectId(), o);
//////                cache.put(o.getObjectIdP(), o);
////                cachePut(o);
////                result = true;
//////                cacheWorkSlots.put(o.getObjectId(), o); //cache WorkSlots in both caches (to avoid any weird edge cases)
////            }
//            cacheList(results);
//            result = !results.isEmpty();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        //do this update AFTER having cached all items to be able to update all subtask and ownerItem references to the cached instances
////        for (ParseObject o : results) {
////            cacheItem((Item) o);
////        }
//        return result;
//    }
    private boolean cacheAllFilterSortDefsFromParse() {
        return cacheAllFilterSortDefsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
    }

    private List<FilterSortDef> getAllFilterSortDefsFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<FilterSortDef> query = ParseQuery.getQuery(FilterSortDef.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<FilterSortDef> results = null;
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    private boolean cacheAllFilterSortDefsFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<FilterSortDef> results = getAllFilterSortDefsFromParse(reloadUpdateAfterThis, now);
        cacheList(results);
        return !results.isEmpty();
    }

//    private boolean cacheAllFilterSortDefsFromParseXXX(Date reloadUpdateAfterThis, Date now) {
//        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
//        boolean result = false;
//        ParseQuery<FilterSortDef> query = ParseQuery.getQuery(FilterSortDef.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<FilterSortDef> results = null;
//        try {
//            results = query.find();
////            for (ParseObject o : results) {
//////                assert (o.isDataAvailable());
//////                cache.put(o.getObjectIdP(), o);
////                cachePut(o);
////                result = true;
////            }
//            cacheList(results);
//            result = !results.isEmpty();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        //do this update AFTER having cached all items to be able to update all subtask and ownerItem references to the cached instances
////        for (ParseObject o : results) {
////            cacheItem((Item) o);
////        }
//        return result;
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * replace all items in cateogry by their cached instances
//     *
//     * @param cat
//     */
////    private void cacheItemListOrCategoryXXX(ItemList cat) {
////        List<Item> catItems = cat.getList();
////        Item temp;
//////            for (Item subtask:subtasks) {
////        for (int i = 0, size = catItems.size(); i < size; i++) {
////            Item subtask = catItems.get(i);
////            if (!subtask.isDataAvailable() && (temp = (Item) cache.get(subtask.getObjectIdP())) != null) {
////                catItems.set(i, temp); //replace unloaded item with buffered instance
////            }
////        }
//////        if (item.getOwner() instanceof Item && (temp=(Item)cache.get(item.getOwner()))!=null)
//////            item.setOwner((Item)temp);
////    }
//</editor-fold>
    private boolean cacheAllCategoriesFromParse() {
        return cacheAllCategoriesFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
    }

    public List<Category> getAllCategoriesFromParse() {
//        return getAllCategoriesFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
        return getAllCategoriesFromParse(null, null);

    }

//    public List<Category> getAllCategoriesFromParseXXX(Date reloadUpdateAfterThisDate, Date reloadUpdateBeforeOrOnThisDate) {
//        List<Category> results = null;
//        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThisDate);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpdateBeforeOrOnThisDate);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
//            results = query.find();
//            cacheList(results);
////            fetchAllElementsInSublist(results); //replace with cached classes
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
    public List<Category> getAllCategoriesFromParse(Date reloadUpdateAfterThis, Date reloadUpdateBeforeOrOnThisDate) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
        if (reloadUpdateAfterThis != null) {
            query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        }
        if (reloadUpdateBeforeOrOnThisDate != null) {
            query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpdateBeforeOrOnThisDate);
        }
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        List<Category> results = null;

        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        results = fetchListElementsIfNeededReturnCachedIfAvail(results);
        return results;
    }

    private boolean cacheAllCategoriesFromParse(Date reloadUpdateAfterThis,
            Date now
    ) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<Category> results = getAllCategoriesFromParse(reloadUpdateAfterThis, now);
        cacheList(results);
        return !results.isEmpty();

    }

//    private boolean cacheAllCategoriesFromParseXXX(Date reloadUpdateAfterThis, Date now) {
//        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
//        boolean result = false;
//        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<Category> results = null;
//        try {
//            results = query.find();
////            for (ParseObject o : results) {
//////                assert (o.isDataAvailable());
////                cache.put(o.getObjectIdP(), o);
////                result = true;
//////                assert (o instanceof Category);
//////                cacheItemListOrCategory((ItemList) o);
////            }
//            cacheList(results);
//            result = !results.isEmpty();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return result;
//    }
    private boolean cacheAllItemListsFromParse() {
        return cacheAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
    }

    public List<ItemList> getAllItemListsFromParse() {
        return getAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
    }

//    public List<ItemList> getAllItemListsFromParseXXX(Date reloadAfterThisDate, Date reloadUpToAndIncludingThisDate) {
////        List<ItemList> results = null;
//        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadAfterThisDate);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpToAndIncludingThisDate);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        try {
//            List<ItemList> results = query.find();
//            cacheList(results);
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
    public List<ItemList> getAllItemListsFromParse(Date reloadUpdateAfterThis, Date reloadUpToAndIncludingThisDate) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpToAndIncludingThisDate);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);

        List<ItemList> results = null;
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return fetchListElementsIfNeededReturnCachedIfAvail(results);
    }

    private boolean cacheAllItemListsFromParse(Date reloadUpdateAfterThis, Date now) {
        List<ItemList> results = getAllItemListsFromParse(reloadUpdateAfterThis, now);
        cacheList(results);
        return !results.isEmpty();
    }

//    private boolean cacheAllItemListsFromParseXXX(Date reloadUpdateAfterThis, Date now) {
//        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
//        boolean result = false;
//        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
//        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
//        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!    
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        List<ItemList> results = null;
//        try {
//            results = query.find();
////<editor-fold defaultstate="collapsed" desc="comment">
////            for (ParseObject o : results) {
////                if (false) {
//////                    assert (o.isDataAvailable()) : "Cache pb: no data available for ParseObject=" + o + " ObjId=" + o.getObjectId();
////                }
//////                if (o.isDataAvailable()) {
////                cache.put(o.getObjectIdP(), o);
////                result = true;
//////                }
//////                assert (o instanceof Category);
//////                cacheItemListOrCategory((ItemList) o); //replaced by a call fetchListElementsIfNeededReturnCachedIfAvail inside the getList() methods of Categories/ItemLists
////            }
////</editor-fold>
//            cacheList(results);
//            result = !results.isEmpty();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return result;
//    }
    /**
     * run through the element in the list and update the elements to point to
     * the cached elements, fetching the elements from Parse if not already
     * cached.
     *
     * @param results list of already cached Category instances
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void cacheUpdateAllCategoryItemReferencesXXX(List<Category> results) {
//        for (Category cat : results) {
//            List<Item> catItems = cat.getList();
//            for (Item itemInCat : catItems) {
//                List<Category> itemCategories = itemInCat.getCategories();
//                int index;
//                if ((index = itemCategories.indexOf(cat)) != -1) {
//                    itemCategories.set(index, cat); //replace category instance with cached one
//                }
//            }
//        }
//    }
//    private void cacheUpdateAllItemListItemReferencesXXX(List<ItemList> results) {
//        for (ItemList itemList : results) {
//            List<Item> listItems = itemList.getList();
//            ItemList owner;
//            for (Item itemInItemList : listItems) {
//                if (itemInItemList.getOwner() instanceof ItemList && (owner = (ItemList) cache.get(itemInItemList.getObjectIdP())) != null) {
//                    itemInItemList.setOwner(owner); //replace //TODO!!!! bad practice since it will make every
//                }
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void cacheUpdateListToCachedObjectsOLDXXX(List list) {
////        for (Item o:list) {
//        assert (list != null) : "updating null list from cache";
////        if (list == null) {
////            return;
////        }
//        for (int i = 0, size = list.size(); i < size; i++) {
////            ParseObject cachedObject;
//            Object cachedObject;
////            ASSERT.that((list.get(i) != null) && (list.get(i) != JSONObject.NULL), "entry nb=" + i + " in list " + list + " is null");
//            ASSERT.that((list.get(i) != null) && (list.get(i) != JSONObject.NULL), "entry nb=" + i + " in list " + (list instanceof ItemList ? ((ItemList) list).getText() : "") + " is null");
////            if (((ParseObject) list.get(i)).getObjectId() != null && (cachedObject = (ParseObject) cache.get(((ParseObject) list.get(i)).getObjectId())) != null) {
//            if (list.get(i) == null || list.get(i) == JSONObject.NULL) {
//                list.remove(i); //UI: clean up elements that don't exist anymore
//                i--;
//                size--;
//            } else {
//                ParseObject listElt = (ParseObject) list.get(i);
//                String objId = listElt.getObjectIdP();
////            if (objId != null && (cachedObject = cache.get(objId)) != null && cachedObject != p) {
////                if (objId != null && (cachedObject = cacheGet(objId)) != null && cachedObject != p) {
//                if ((cachedObject = cacheGet(listElt)) != null && cachedObject != listElt) {
//                    list.set(i, cachedObject);
//                } else {
//                    cachedObject = fetchIfNeededReturnCachedIfAvail(listElt); //NB! will possibly replace the parseObjects in the list with cached ones
//                    cachePut(listElt); //put new
//                    list.set(i, cachedObject);
//                }
////                else {
////                    list.remove(i); //UI: clean up elements that don't exist anymore
////                    i--;size--;
////                }
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * will run through list and look up every element in cache (same
//     * ParseObjectId) and if found replace with cached element. If an element is
//     * not found in cache, will fetch it from Parse and cache it.
//     *
//     * @param list
//     */
//    public void fetchListElementsIfNeededReturnCachedIfAvail(List list) {
//        assert (list != null) : "updating null list from cache";
//        for (int i = 0, size = list.size(); i < size; i++) {
////            Object cachedObject;
//            if (list.get(i) == null || list.get(i) == JSONObject.NULL) {
//                ASSERT.that((list.get(i) != null) && (list.get(i) != JSONObject.NULL), "entry nb=" + i + " in list " + (list instanceof ItemList ? ((ItemList) list).getText() : "") + " is null");
//                list.remove(i); //UI: clean up elements that don't exist anymore
//                i--;
//                size--;
//            } else {
////<editor-fold defaultstate="collapsed" desc="comment">
////                ParseObject listElt = (ParseObject) list.get(i);
////                String objId = listElt.getObjectIdP();
//////            if (objId != null && (cachedObject = cache.get(objId)) != null && cachedObject != p) {
//////                if (objId != null && (cachedObject = cacheGet(objId)) != null && cachedObject != p) {
////                if ((cachedObject = cacheGet(listElt)) != null && cachedObject != listElt) {
////                    list.set(i, cachedObject);
////                } else {
////                    cachedObject = fetchIfNeededReturnCachedIfAvail(listElt); //NB! will possibly replace the parseObjects in the list with cached ones
////                    cachePut(listElt); //put new
////                    list.set(i, cachedObject);
////                }
////</editor-fold>
//                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) list.get(i)));
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededReturnCachedIfAvailOLDXXX(ParseObject parseObject) {
//        if (parseObject == null) {
//            return null;
//        }
//        try {
//            ParseObject temp;
//            if (parseObject != null && parseObject.getObjectIdP() != null) {
//                if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
//                    return temp;
//                } else if ((temp = (ParseObject) cacheWorkSlots.get(parseObject.getObjectIdP())) != null) {
//                    return temp;
//                } else {
//                    parseObject.fetchIfNeeded();
//                    cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                    return parseObject;
//                }
//            }
//        } catch (ParseException ex) {
////            Log.e(ex);
//            return null;
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededXXX(ParseObject parseObject) {
//        try {
//            ParseObject fetched = parseObject.fetchIfNeeded();
//            if (fetched instanceof WorkSlot) {
//                cacheWorkSlots.put(parseObject.getObjectIdP(), fetched);
//            } else {
//                cache.put(parseObject.getObjectIdP(), fetched);
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededOrgXXX(ParseObject parseObject) {
//        try {
//            if (parseObject != null) {
//                parseObject.fetchIfNeeded();
//                cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
//                return parseObject;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
    //    public void fetchAllElementsInSublist(ParseObject listOrCategory, boolean recursively) {
//        try {
//            listOrCategory.fetchIfNeeded();
//            fetchAllElementsInSublist(listOrCategory, recursively);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
//    public void fetchAllElementsInSublist(ParseObject listOrCategory, boolean recursively) {
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    /**
//     * NB! Not the usual semantic of fetchFromCacheOnly, since it may return an
//     * existing instance of the parseObject instead of simply fetching the data
//     * for the passed parseObject (to avoid multiple parallel copies of the same
//     * ParseObject, e.g. when using getOnwerList, a new instance of the
//     * ownerList is returned and any changes to this will not be reflected until
//     * the list is saved and fetched again).
//     *
//     * @param parseObject
//     * @return null if object does not (or no longer) exist on server
//     */
//    public ParseObject fetchIfNeededReturnCachedIfAvail(ParseObject parseObject) {
//        if (parseObject == null || parseObject.getObjectIdP() == null) {
//            return null;
//        }
//        ParseObject temp;
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (parseObject.getObjectIdP() != null) {
////            if (parseObject instanceof WorkSlot && (obj = cacheWorkSlots.get(parseObject.getObjectIdP())) != null) {
//////            if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
////                return (WorkSlot) obj;
////            } else if ((temp = (ParseObject) cache.get(parseObject.getObjectIdP())) != null) {
////                return temp;
////            } else
////</editor-fold>
//        if ((temp = (ParseObject) cacheGet(parseObject)) != null) {
//            return temp;
//        } else {
//            try {
//                parseObject.fetchIfNeeded();
////<editor-fold defaultstate="collapsed" desc="comment">
////                    if (parseObject instanceof WorkSlot) {
//////                        cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
////                        cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject);
////                    } else {
//////                        cache.put(parseObject.getObjectIdP(), parseObject.fetchIfNeeded());
////                        cache.put(parseObject.getObjectIdP(), parseObject);
////                    }
////</editor-fold>
//                cachePut(parseObject);
//                //NO need to fetch lists within the object, they are updated when they are used first time (in getList() using fetchListElementsIfNeededReturnCachedIfAvail()...)
////                if (parseObject instanceof ItemAndListCommonInterface) {
////                    List list;
////                    if ((list = ((ItemAndListCommonInterface) parseObject).getList()) != null) {
////                        fetchAllElementsInSublist(list);
////                    }
////                }
//                return parseObject;
//            } catch (ParseException ex) {
////            Log.e(ex);
//                return null;
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void fetchAllElementsInSublist(ItemAndListCommonInterface itemOrItemListOrCategoryOrList) {
//        fetchAllElementsInSublist((ParseObject) itemOrItemListOrCategoryOrList, false);
//    }
//
////    public void fetchAllElementsInSublist(List itemOrItemListOrCategoryOrList) {
////        fetchAllElementsInSublist(itemOrItemListOrCategoryOrList, false);
////    }
//    public void fetchAllElementsInSublist(ParseObject itemOrItemListOrCategoryOrList, boolean recursively) {
//        assert itemOrItemListOrCategoryOrList != null : "fetchAllItemsIn called with null list";
////        List<ParseObject> list = null;
//        List list = null;
////        List<ItemAndListCommonInterface> list = null;
//        try {
////            ParseBatch batch = ParseBatch.create();
//            //TODO!!! find more efficient way to fetchFromCacheOnly all the objects - use ParseBatch once switched to new version of parse4cn1!!
//
//            if (false && itemOrItemListOrCategoryOrList instanceof ParseObject && ((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP() != null) {
////                 if ((temp = cache.get(((ParseObject) itemOrListOrCategory).getObjectId())) != null) {
////            return temp;
////        }
//                //assume that object has been fully fetched:
//                ((ParseObject) itemOrItemListOrCategoryOrList).fetchIfNeeded(); //fetch the top-level object if needed (may be the case when fetching recursively)
////                if (itemOrItemListOrCategoryOrList instanceof WorkSlot) {
////                    cacheWorkSlots.put(((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP(), itemOrItemListOrCategoryOrList);
////                } else {
////                    cache.put(((ParseObject) itemOrItemListOrCategoryOrList).getObjectIdP(), itemOrItemListOrCategoryOrList);
////                }
//                cachePut((ParseObject) itemOrItemListOrCategoryOrList);
//            }
//
//            if (itemOrItemListOrCategoryOrList instanceof ItemAndListCommonInterface) {
//                list = ((ItemAndListCommonInterface) itemOrItemListOrCategoryOrList).getList();
//                for (int i = 0, size = list.size(); i < size; i++) {
//                    list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
////                    if (recursively) {
////                        fetchAllElementsInSublist((ParseObject) list.get(i), recursively);
////                    }
//                }
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (itemOrItemListOrCategoryOrList instanceof Item) {
//////                list = (List<Item>)((Item) itemOrItemListOrCategoryOrList).getList();
////                list = ((Item) itemOrItemListOrCategoryOrList).getList();
//////                list = ((List<Item>) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof Category) {
////                list = ((Category) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof ItemList) {
////                list = ((ItemList) itemOrItemListOrCategoryOrList).getList();
////            } else if (itemOrItemListOrCategoryOrList instanceof List) {
////                list = (List) itemOrItemListOrCategoryOrList;
////            } else {
////                assert false : "unknow type of element to fetch=" + itemOrItemListOrCategoryOrList;
////            }
//////            for (ParseObject item : list) {
////            for (int i = 0, size = list.size(); i < size; i++) {
//////                ((Item) item).fetchIfNeeded();
//////                item.fetchIfNeeded();
//////                fetchIfNeeded(item);
////                list.set(i, fetchIfNeededReturnCachedIfAvail(list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
////                if (recursively) {
////                    fetchAllElementsInSublist(list.get(i), recursively);
////                }
////            }
////</editor-fold>
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        if (itemOrItemListOrCategoryOrList instanceof Item) {
//            ((Item) itemOrItemListOrCategoryOrList).setList(list);
//        } else if (itemOrItemListOrCategoryOrList instanceof Category) {
//            ((Category) itemOrItemListOrCategoryOrList).setList(list);
//        } else if (itemOrItemListOrCategoryOrList instanceof ItemList) {
//            ((ItemList) itemOrItemListOrCategoryOrList).setList(list);
//        };
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void cacheAllDataOLDXXX() {
//        cacheAllItemsFromParse();
//        cacheAllCategoriesFromParse();
//        cacheAllItemListsFromParse();
//        cacheAllWorkSLotsFromParse();
//        cacheAllFilterSortDefsFromParse();
//        getAllCategoriesFromParse();
//        getAllItemListsFromParse();
//        cacheAllRepeatRulesFromParse();
//        getCategoryList(); //will cache the list of Categories
//        getItemListList(); //will cache the list of ItemLists
//        getTemplateList(); //will cache the list of Templates
////        cacheUpdateAllCategoryItemReferences(categoryList);
////        cacheUpdateAllItemListItemReferences(itemListLists);
////        cacheUpdateAllItemReferences();
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void initNewCacheXXX(CacheMap cache, int cacheDynamicSize, int cacheLocalStorageSize) {
////        int cacheDynamicSize = MyPrefs.getInt(MyPrefs.cacheDynamicSize);
//cache.setCacheSize(cacheDynamicSize); //persist cached elements
//
////        int cacheLocalStorageSize = MyPrefs.getInt(MyPrefs.cacheLocalStorageSize);
//if (cacheLocalStorageSize > 0) {
//    cache.setAlwaysStore(true); //persist cached elements
//    cache.setStorageCacheSize(cacheLocalStorageSize); //persist cached elements
//}
//    }
//</editor-fold>
//    private void initAndConfigureCache() {
//        initAndConfigureCache(true);
//    }
    /**
     * initialize cache. Creates a new memory cache but will keep/access
     * elements cached in local storage. Can be called if changing the settings
     * for size of cache (this will reset memory cache). if createNewCache is
     * true a new cahce will be created (this is not necessary if just changing
     * the size the of the cahce)
     */
//    private void initAndConfigureCache(boolean createNewInMemoryCache, boolean resetAndDeleteLocallyCachedData) {
//    private void initAndConfigureCache(boolean forceCreationOfNewInMemoryCache) {
    private void initAndConfigureCache() {
//        if (cache == null || forceCreationOfNewInMemoryCache) {
        if (true || cache == null) { //NO reason to keep old cache, even if cleaned??!
//            cache = null; //force GC
//            cache = new MyCacheMap("ALL");
            cache = new MyCacheMapHash("ALL$");
        }
        cache.setCacheSize(MyPrefs.cacheDynamicSize.getInt()); //persist cached elements
        //activate or de-activate local storage
        cache.setAlwaysStore(MyPrefs.cacheLocalStorageSize.getInt() > 0); //persist cached elements
        cache.setStorageCacheSize(MyPrefs.cacheLocalStorageSize.getInt()); //persist cached elements //TODO!!!! will this automatically too many locally cached elements?? At first look, seems not
//        if (resetAndDeleteLocallyCachedData) cache.clearAllCache();
//        if (resetAndDeleteLocallyCachedData) {
//            cache.clearStorageCache();
//        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        }
//        createNewCacheForWorkSlots(true);

        if (cacheWorkSlots == null) { // || forceCreationOfNewCache) {
            cacheWorkSlots = new MyCacheMapHash("WS$"); //prefix neccessary to not confuse locally cached items
            //                    = new MyCacheMap("WS"); //prefix neccessary to not confuse locally cached items
            cacheWorkSlots.setCacheSize(MyPrefs.cacheDynamicSizeWorkSlots.getInt()); //persist cached elements
            //activate or de-activate local storage
            cacheWorkSlots.setAlwaysStore(MyPrefs.cacheLocalStorageSizeWorkSlots.getInt() > 0); //persist cached elements
            cacheWorkSlots.setStorageCacheSize(MyPrefs.cacheLocalStorageSizeWorkSlots.getInt()); //persist cached elements //TODO!!!! will this automatically too many locally cached elements?? At first look, seems not
//            initNewCache(cacheWorkSlots, MyPrefs.getInt(MyPrefs.cacheDynamicSizeWorkSlots), MyPrefs.getInt(MyPrefs.cacheLocalStorageSizeWorkSlots));
        }
//        int cacheDynamicSize = MyPrefs.getInt(MyPrefs.cacheDynamicSize);
//        cache.setCacheSize(cacheDynamicSize); //persist cached elements
//
//        int cacheLocalStorageSize = MyPrefs.getInt(MyPrefs.cacheLocalStorageSize);
//        if (cacheLocalStorageSize > 0) {
//            cache.setAlwaysStore(true); //persist cached elements
//            cache.setStorageCacheSize(cacheLocalStorageSize); //persist cached elements
//        }
//</editor-fold>
    }

    /**
     * reset and removeFromCache cache (from repair menu)
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void cacheClearAndRefreshAllDataXXX() {
//        cache.clearAllCache();
////        createNewCache();
////        cacheAllData();
//        cacheAllData(new Date(MyDate.MIN_DATE), new Date()); //TODO!!! removed locally cached data in case CN1 cache is corrupted
//    }
//
//    public void cacheLoadDataChangedOnServerAndInitIfNecessaryXXX() {
//        DAO.this.cacheLoadDataChangedOnServerAndInitIfNecessary(false);
//    }
//</editor-fold>
    public void resetAndDeleteAndReloadAllCachedData() {
        Dialog ip = new InfiniteProgress().showInfiniteBlocking();
        Storage.getInstance().deleteStorageFile(FILE_DATE_FOR_LAST_CACHE_REFRESH); //delete date so all data will be reloaded in cacheLoadDataChangedOnServer()

        if (cache != null) {
//            cache.clearStorageCache(); //delete any locally cached data/files
//            cache.clearAllCache(); //clear any cached data (even in memory to make sure we get a completely fresh copy)
            cache.clearAllCache(RESERVED_LIST_NAMES); //clear any cached data (even in memory to make sure we get a completely fresh copy)
            cache = null;  //force GC and creation of new cache in initAndConfigureCache()
        }
        if (cacheWorkSlots != null) {
//            cache.clearStorageCache(); //delete any locally cached data/files
//            cache.clearAllCache(); //clear any cached data (even in memory to make sure we get a completely fresh copy)
            cacheWorkSlots.clearAllCache(RESERVED_LIST_NAMES); //clear any cached data (even in memory to make sure we get a completely fresh copy)
            cacheWorkSlots = null;  //force GC and creation of new cache in initAndConfigureCache()
        }
//        initAndConfigureCache(true);
        initAndConfigureCache();
        cacheLoadDataChangedOnServer(true, false);
        ip.dispose();
    }

    public void updateCacheWhenSettingsChangeNOT_TESTED() {
        if (MyPrefs.cacheLocalStorageSize.getInt() <= 0) {
            Storage.getInstance().deleteStorageFile(FILE_DATE_FOR_LAST_CACHE_REFRESH);
        }
//        initAndConfigureCache(false); //if cache size increases, additional objects will automatically be cached
        initAndConfigureCache(); //if cache size increases, additional objects will automatically be cached

    }

//    public void loadCacheToMemoryXXX() {
//        cache.loadCacheToMemory();
//    }
    /**
     * will initialize (or reset if resetAndDeleteAllCachedData) the cache and
     * update cache with objects that have been changed on Parse server since
     * last update. First time called will cache everything. Assumes that local
     * cache is large enough to hold everything, if not, ???
     */
//    public void cacheLoadDataChangedOnServer() {
//        cacheLoadDataChangedOnServer(true);
//    }
    /**
     *
     * @param loadingChangedDataFromParseServerForTesting skip checking server
     * for updates to optimize app startup time during testing. will initialize
     * (or reset if resetAndDeleteAllCachedData) the cache and update cache with
     * objects that have been changed on Parse server since last update. First
     * time called will cache everything. Assumes that local cache is large
     * enough to hold everything, if not, ???
     */
//    public boolean cacheLoadDataChangedOnServer(boolean loadingChangedDataFromParseServerForTesting) {
//        
//    }
    public boolean cacheLoadDataChangedOnServer(boolean loadingChangedDataFromParseServerForTesting, boolean inBackground) {
        //TODO!!!! what happens if cache is too small??? WIll it drop oldest objects?
//        initAndConfigureCache(); //now done in DAO constructor
////\        loadCacheToMemory(); //first load 

        cache.loadCacheToMemory(); //first load to memory

        if (loadingChangedDataFromParseServerForTesting) {
            Dialog ip = null;
            if (false && !inBackground) { //false since this creates a new Form just to show the infinte progress
                ip = new InfiniteProgress().showInfiniteBlocking();
            }
            Date now = new MyDate(); //UI: only cache data that was already changed when update was launched
            Date lastCacheRefreshDate = new Date(MyDate.MIN_DATE);

            if (true || MyPrefs.cacheLocalStorageSize.getInt() > 0) { //only store if local cache is active -> test doesn't make sense since app cannot currently function without cache
                //get date when local cache was last updated
                if (Storage.getInstance().exists(FILE_DATE_FOR_LAST_CACHE_REFRESH)) {
                    lastCacheRefreshDate = (Date) Storage.getInstance().readObject(FILE_DATE_FOR_LAST_CACHE_REFRESH); //read in when initializing the Timer - from here on it is only about saving updates
                }
                Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date
            }
            boolean result = cacheAllData(lastCacheRefreshDate, now);
            Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date only *after* caching is done, in case app is interrupted (so it will restart from same place next time)!
            if (ip != null) {
                ip.dispose();
            }
            return result;
        }
        return false;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean cacheLoadDataChangedOnServerInBackgroundXXX(boolean loadChangedDataFromParseServer) {
//        //TODO!!!! what happens if cache is too small??? WIll it drop oldest objects?
////        initAndConfigureCache(); //now done in DAO constructor
//////\        loadCacheToMemory(); //first load
//        cache.loadCacheToMemory(); //first load
//
//        if (loadChangedDataFromParseServer) {
//            Date now = new MyDate(); //UI: only cache data that was already changed when update was launched
//            Date lastCacheRefreshDate = new Date(MyDate.MIN_DATE);
//            if (MyPrefs.cacheLocalStorageSize.getInt() > 0) { //only store if local cache is active
//                //get date when local cache was last updated
//                if (Storage.getInstance().exists(FILE_DATE_FOR_LAST_CACHE_REFRESH)) {
//                    lastCacheRefreshDate = (Date) Storage.getInstance().readObject(FILE_DATE_FOR_LAST_CACHE_REFRESH); //read in when initializing the Timer - from here on it is only about saving updates
//                }
//                Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date
//            }
//
//            //TODO!!!! run all these queries in parallel and continue when they've all returned
//            Log.p("Caching Items");
//            List<Item> items = getAllItemsFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching Categories");
//            List<Category> categories = getAllCategoriesFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching ItemLists");
//            List<ItemList> itemLists = getAllItemListsFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching WorkSlots");
//            List<WorkSlot> workSlots = getAllWorkSlotsFromParse(lastCacheRefreshDate, now);
////            WorkSlotList workSlots = getAllWorkSlotsFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching Filters");
//            List<FilterSortDef> filters = getAllFilterSortDefsFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching RepeatRules");
//            List<RepeatRuleParseObject> repeatRules = getAllRepeatRulesFromParse(lastCacheRefreshDate, now);
//            Log.p("Caching CategoryList");
////            CategoryList categoryList = getCategoryList(true); //will cache the list of Categories
////            cacheDelete(CategoryList.getInstance().reloadFromParse()); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
//            CategoryList.getInstance().reloadFromParse(false, lastCacheRefreshDate, now); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
//            Log.p("Caching ItemListList");
////            ItemListList itemListList = getItemListList(true); //will cache the list of ItemLists
////            cacheDelete(ItemListList.getInstance().resetInstance()); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
//            ItemListList.getInstance().reloadFromParse(false, lastCacheRefreshDate, now); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
//            Log.p("Caching TemplateList");
////            TemplateList templateList = getTemplateList(true); //will cache the list of Templates
////            cacheDelete(TemplateList.getInstance().reloadFromParse()); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
//            TemplateList.getInstance().reloadFromParse(false, lastCacheRefreshDate, now); //reset and remove old instance from cache, next call to getInstance() will removeFromCache an update cache
////            Log.p("cacheAllData FINISHED updating cache" + (somethingWasLoaded ? " NEW DATA LOADED" : " no data loaded"));
//
//            Display.getInstance().callSerially(() -> {
//                cacheList(items);
//                cacheList(categories);
//                cacheList(itemLists);
//                cacheList(workSlots);
//                cacheList(filters);
//                cacheList(repeatRules);
////                cacheList(categoryList);
////                cacheList(itemListList);
////                cacheList(inbox);
//                Form f = Display.getInstance().getCurrent();
//
//                if (f instanceof MyForm) {
//                    Log.p("Refreshing current form after reload of cached lists");
//                    ((MyForm) f).refreshAfterEdit(); //update with new values //TODO!!! show a spinner or sth: "Updating with new data"
//                }
//            });
//
//            return cacheAllData(lastCacheRefreshDate, now);
//        }
//        return false;
//
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void cacheLoadDataChangedOnServerAndInitIfNecessaryOLD(boolean resetAndDeleteAllCachedData) {
//        //TODO!!!! what happens if cache is too small??? WIll it drop oldest objects?
////        cacheAllData(new Date(MyDate.MIN_DATE), new Date());
//        if (resetAndDeleteAllCachedData || !Storage.getInstance().exists(FILE_DATE_FOR_LAST_CACHE_REFRESH)) {
//            Storage.getInstance().deleteStorageFile(FILE_DATE_FOR_LAST_CACHE_REFRESH);
////            cacheClearAndRefreshAllData();
////            if (cache!=null) cache.clearAllCache();
////            initAndConfigureCache(true, true);
////            initAndConfigureCache(true);
//            initAndConfigureCache();
//            Date now = new Date();
//            cacheAllData(new Date(MyDate.MIN_DATE), now); //TODO!!! remove locally cached data in case CN1 cache is corrupted
//            if (MyPrefs.cacheLocalStorageSize.getInt() > 0) { //only store if local cache is active
//                Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date
//            }
//        } else {
//            Date lastCacheRefreshDate;
//            //get date when cache was last updated
//            if (Storage.getInstance().exists(FILE_DATE_FOR_LAST_CACHE_REFRESH)) {
//                lastCacheRefreshDate = (Date) Storage.getInstance().readObject(FILE_DATE_FOR_LAST_CACHE_REFRESH); //read in when initializing the Timer - from here on it is only about saving updates
//            } else {
//                lastCacheRefreshDate = new Date(MyDate.MIN_DATE);
////            initCache();
//            }
//            Date now = new Date();
////            initAndConfigureCache(true, false);
////            initAndConfigureCache(true);
//            initAndConfigureCache();
//            cacheAllData(lastCacheRefreshDate, now);
//            Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date
//        }
//    }
//</editor-fold>
    public boolean cacheAllData(Date afterDate, Date beforeDate) {
        boolean somethingWasLoaded;

        Log.p("Caching Items");
        somethingWasLoaded = cacheAllItemsFromParse(afterDate, beforeDate);
        Log.p("Caching Categories");
        somethingWasLoaded = cacheAllCategoriesFromParse(afterDate, beforeDate) || somethingWasLoaded;

        Log.p("Caching ItemLists");
        somethingWasLoaded = cacheAllItemListsFromParse(afterDate, beforeDate) || somethingWasLoaded;

        Log.p("Caching WorkSlots");
        somethingWasLoaded = cacheAllWorkSlotsFromParse(afterDate, beforeDate) || somethingWasLoaded;

        Log.p("Caching Filters");
        somethingWasLoaded = cacheAllFilterSortDefsFromParse(afterDate, beforeDate) || somethingWasLoaded;
//        getAllCategoriesFromParse();
//        getAllCategoriesFromParse(reloadUpdateAfterThis, now);
//        getAllItemListsFromParse();
//        getAllItemListsFromParse(reloadUpdateAfterThis, now);

        Log.p("Caching RepeatRules");
        somethingWasLoaded = cacheAllRepeatRulesFromParse(afterDate, beforeDate) || somethingWasLoaded;
//        getCategoryList(); //will cache the list of Categories
//        getItemListList(); //will cache the list of ItemLists
//        getTemplateList(); //will cache the list of Templates

        if (true) {
            Log.p("Caching CategoryList");
//        somethingWasLoaded = cacheCategoryList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Categories
//        CategoryList.getInstance().reloadFromParse();
//        CategoryList.getInstance();
            somethingWasLoaded = CategoryList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
//            cachePut(CategoryList.getInstance());

            Log.p("Caching ItemListList");
//        somethingWasLoaded = cacheItemListList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of ItemLists
            somethingWasLoaded = ItemListList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
//            cachePut(ItemListList.getInstance());

            Log.p("Caching TemplateList");
//        somethingWasLoaded = cacheTemplateList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Templates
            somethingWasLoaded = TemplateList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
//            cachePut(TemplateList.getInstance());
        }
//        cacheUpdateAllCategoryItemReferences(categoryList);
//        cacheUpdateAllItemListItemReferences(itemListLists);
//        cacheUpdateAllItemReferences();
        Log.p("cacheAllData FINISHED updating cache" + (somethingWasLoaded ? " NEW DATA LOADED" : " no data loaded"));
        return somethingWasLoaded;
    }

    public void cleanUpAllBadObjectReferences(boolean executeCleanup) {
        CleanUpDataInconsistencies clean = new CleanUpDataInconsistencies(this);
        clean.cleanUpAllBadObjectReferences(executeCleanup);
    }

    boolean cleanUpItemListOrCategory(ItemList itemListOrCategory, boolean executeCleanup) {
        return new CleanUpDataInconsistencies(this).cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, false);
    }

    public void cleanUpTemplateListInParse(boolean executeCleanup) {
//        cleanUpTemplateList(DAO.getInstance().getTemplateList(), getTopLevelTemplatesFromParse(), true);
//        cleanUpTemplateList(TemplateList.getInstance(), dao.getTopLevelTemplatesFromParse(), executeCleanup);
        new CleanUpDataInconsistencies(this).cleanUpTemplateListInParse(executeCleanup);
    }

    void cleanUpWorkSlots(boolean executeCleanup) {
        new CleanUpDataInconsistencies(this).cleanUpWorkSlots(executeCleanup);
    }

    void cleanUpItemsWithNoValidOwner(boolean executeCleanup) {
        new CleanUpDataInconsistencies(this).cleanUpItemsWithNoValidOwner(executeCleanup);
    }

    boolean cleanUpItemListOrCategory(ItemList itemListOrCategory, boolean executeCleanup, boolean cleanupItems) {
        return new CleanUpDataInconsistencies(this).cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, cleanupItems);
    }
}
