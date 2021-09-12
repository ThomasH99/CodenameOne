/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.todocatalyst.todocatalyst;

import ca.weblite.codename1.json.JSONObject;
import com.codename1.analytics.AnalyticsService;
import com.codename1.components.InfiniteProgress;
import com.codename1.io.CacheMap;
import com.codename1.io.Log;
import static com.codename1.io.Log.e;
import static com.codename1.io.Log.p;
import com.codename1.io.NetworkManager;
import com.codename1.io.Storage;
import com.codename1.io.Util;
import com.codename1.messaging.Message;
import com.codename1.ui.CN;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
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
import com.todocatalyst.todocatalyst.MyForm.ScreenType;
//import static com.todocatalyst.todocatalyst.Item.PARSE_OWNER_ITEM;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import static com.todocatalyst.todocatalyst.MyForm.ScreenType.*;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;

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
//        reloadSaveCache();
    }

//    CacheMap<String, ParseObject> cache = new CacheMap();
//    CacheMap cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
//    MyCacheMap cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
    MyCacheMapHash cache; // = new CacheMap(); //always initialize since DAO may be called before initializing cache via start() (https://www.codenameone.com/javadoc/com/codename1/background/BackgroundFetch.html)
//    MyCacheMap cacheWorkSlots;// = new CacheMap(); //optimize speed when searching only for WorkSlots
    MyCacheMapHash cacheWorkSlots;// = new CacheMap(); //optimize speed when searching only for WorkSlots
//    MyCacheMapHash cacheGuid;// = new CacheMap(); //optimize speed when searching only for WorkSlots
    Date latestCacheUpdateDate = new Date(MyDate.MIN_DATE); //start wtih minimal date
//    Object temp;

    void startUp(boolean refreshDataInBackground) {
        EasyThread thread = EasyThread.start("cacheUpdate");
//<editor-fold defaultstate="collapsed" desc="comment">
//            if (true) {
//                thread.run(() -> {
//                    DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                });
//            } else {
//                DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            }
//            boolean refreshDataInBackground = true;
//        if (false && refreshDataInBackground) {
//            thread.run((success) -> {
////                if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(), true)) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
////                if (DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(),
////                        MyPrefs.reloadChangedDataInBackground.getBoolean())) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                if (DAO.getInstance().cacheLoadDataChangedOnServer(false)) { //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//                    success.onSucess(null);
//                }
//                thread.kill();
////                success.onSucess(success); //CN1 Support: is there an error in CN1 for the run(r,t) call?!!!
//            }, (notUsed) -> {
//                Display.getInstance().callSerially(() -> {
////                if (newDataLoaded) {
//                    Form f = Display.getInstance().getCurrent();
//                    //don't removeFromCache: ScreenLogin (only shown on startup), ScreenMain (no item data shown), ScreenItem (could overwrite manually edited values - not with new version!)
//                    if (f instanceof MyForm && !(f instanceof ScreenLogin) && !(f instanceof ScreenMain)) { // && !(f instanceof ScreenItem)) {
//                        //TODO!!! show "Running" symbyl after like 2 seconds
////                    Display.getInstance().Log.p("refreshing Screen: "+((MyForm) f).getTitle());
//                        ((MyForm) f).refreshAfterEdit(); //to show any new data which may have been loaded
//                        Log.p("Screen " + getComponentForm().getTitle() + " refreshed after loading new data from network");
//                    }
////                    thread.kill();
////                }
//                });
//            });
//        } else {
//            Dialog ip = new InfiniteProgress().showInfiniteBlocking(); //DONE in DAO.cacheLoadDataChangedOnServer
//TODO!!!! show waiting symbol "loading your tasks..."
//            DAO.getInstance().cacheLoadDataChangedOnServer(MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(), true); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            DAO.getInstance().cacheLoadDataChangedOnServer(true || MyPrefs.cacheLoadChangedElementsOnAppStart.getBoolean(),
//                    false && MyPrefs.reloadChangedDataInBackground.getBoolean()); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//</editor-fold>
        DAO.getInstance().cacheLoadDataChangedOnServer(false); //TODO optimization: run in background (in ScreenMain?!) and removeFromCache as data comes in
//            ip.dispose();
//        }
        //ALARMS - initialize
//        AlarmHandler.getInstance().setupAlarmHandlingOnAppStart(); //TODO!!!! optimization: do in background
        AlarmHandler.getInstance().updateLocalNotificationsOnAppStartOrAllAlarmsEnOrDisabled(); //TODO!!!! optimization: do in background
//<editor-fold defaultstate="collapsed" desc="comment">

//TIMER - was running when app was moved to background? - now done with ReplayCommand
//            if (!ScreenTimer.getInstance().isTimerActive()) {
//                new ScreenMain().show(); //go directly to main screen if user already has a session
//            } else {
//                if (!ScreenTimer.getInstance().relaunchTimerOnAppRestart()) {
//                    new ScreenMain().show(); //if pb with Timer relaunch, go to main screen instead
//                }
//            }
//</editor-fold>
    }

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
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>

        ASSERT.that(parseObject instanceof ParseObject, () -> "trying to store non-ParseObject in cache: parseObject=" + parseObject);
//        if (parseObject.getObjectIdP() != null) { //above just caches the singletons, so now also cache other objects, with a special treatment for workSlots
        if (parseObject.getGuid() != null) { //above just caches the singletons, so now also cache other objects, with a special treatment for workSlots
            if (parseObject instanceof WorkSlot) {
//                ASSERT.that(parseObject.getObjectIdP() != null, () -> "cachPut of parseObject with objectIdP==null, parseObject=" + parseObject);
                ASSERT.that(parseObject.getGuid() != null, () -> "cachePut of parseObject with guid==null, parseObject=" + parseObject);
//                cacheWorkSlots.put(parseObject.getObjectIdP(), parseObject);
                cacheWorkSlots.put(parseObject.getGuid(), parseObject);
            } else {
//                ASSERT.that(parseObject.getObjectIdP() != null, () -> "cachPut of parseObject with objectIdP==null, parseObject=" + parseObject);
                if (Config.TEST) {
                    ASSERT.that((parseObject instanceof ItemAndListCommonInterface
                            || parseObject instanceof RepeatRuleParseObject || parseObject instanceof FilterSortDef
                            || parseObject instanceof TimerInstance2));
                }
//                cache.put(parseObject.getObjectIdP(), parseObject); //will override any previously put object with same ojectId
                cache.put(parseObject.getGuid(), parseObject); //will override any previously put object with same ojectId
            }
        }
//        else if (false && parseObject.getGuid() != null) {
//            cacheGuid.put(parseObject.getGuid(), parseObject);
//        }//        ASSERT.that(parseObject instanceof ParseObject, () -> "trying to store non-ParseObject in cache: parseObject=" + parseObject);
    }

    /**
     * cache a special named list, accessible both via its name and (as usual)
     * via its objectId
     *
     * @param name
     * @param parseObject
     */
    private synchronized void cachePut(String name, ParseObject parseObject) {
        if (Config.TEST) {
            Object alreadyInCache = cache.get(name);
            if (alreadyInCache != null && !alreadyInCache.equals(parseObject.getGuid())) {
                ASSERT.that(alreadyInCache == null, () -> "An object already exists in cache for name=" + name + "; oldCached=" + alreadyInCache);
            }
        }
//        cache.put(name, parseObject.getObjectIdP());
        cache.put(name, parseObject.getGuid());
        cachePut(parseObject);
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

    private synchronized ParseObject cacheGet(String guid) {
        Object temp;
//        if ((temp = cache.get(parseObjectId)) != null || (temp = cacheWorkSlots.get(parseObjectId)) != null) {
        if ((temp = cache.get(guid)) != null || (temp = cacheWorkSlots.get(guid)) != null) {
//            if (temp instanceof String) {
//                //handle named key like CategoryList.CLASS_NAME where the named key points to the Parse ObjectId so need a second get to fecth actual parseObject
//                return (ParseObject) cache.get(temp);
//            } else {
            if (Config.TEST) {
//                ASSERT.that(temp instanceof ParseObject, "getting a non-ParseObject from cache: returned obj=" + temp + ", objectId=" + parseObjectId);
//            }
                assert temp instanceof ParseObject : "getting a non-ParseObject from cache: returned obj=" + temp + ", objectId=" + guid;
            }
            return (ParseObject) temp;
//            }
        } else {
            return null;
        }
    }

    private synchronized ParseObject cacheGetNamed(String name) {
        String guidCachedForName = (String) cache.get(name);
//        ASSERT.that(cachedObjIdStr != null);
//        if (cachedObjIdStr != null) 
//        return cachedObjIdStr != null ? (ParseObject) cache.get(cachedObjIdStr) : null; //cahce: name -> objectIdP
//        if (cachedByName instanceof String) {
//            return (ParseObject) cache.get(cachedByName); //cahce: name -> objectIdP
//        } else {
//            return (ParseObject) cachedByName; //cahce: name -> objectIdP
//        }
        if (guidCachedForName != null) {
            return (ParseObject) cache.get(guidCachedForName); //cahce: name -> objectIdP
        } else {
            return null;
        }
    }

    private synchronized ParseObject cacheGet(ParseObject parseObjectWithGuid) {
//        return cacheGet(parseObject.getObjectIdP());
        return cacheGet(parseObjectWithGuid.getGuid());
//        return cacheGet(parseObject.getGuid());
    }

//    private synchronized void cacheDelete(ParseObject parseObject) {
//        if (parseObject.getGuid() == null) {
////        if (parseObject.getObjectIdP() == null) {
//            return;
//        }
//        if (parseObject instanceof WorkSlot) {
//            cacheWorkSlots.delete(parseObject.getObjectIdP());
////            cacheWorkSlots.delete(parseObject.getGuid());
//        } else if (parseObject instanceof ItemAndListCommonInterface) {
//            cache.delete(parseObject.getObjectIdP());
////            cache.delete(parseObject.getGuid());
//            //delete objects cached with named key like CategoryList.CLASS_NAME
//            if (parseObject instanceof CategoryList) {
//                cache.delete(CategoryList.CLASS_NAME);
//            } else if (parseObject instanceof ItemListList) {
//                cache.delete(ItemListList.CLASS_NAME);
//            } else if (parseObject instanceof TemplateList) {
//                cache.delete(TemplateList.CLASS_NAME);
//            } else if (parseObject instanceof Inbox) {
//                cache.delete(Inbox.CLASS_NAME);
//            }
//        }
//    }
    private synchronized void cacheDelete(ParseObject parseObject) {
        if (parseObject.getGuid() == null) {
//        if (parseObject.getObjectIdP() == null) {
            return;
        }
        if (parseObject instanceof WorkSlot) {
            cacheWorkSlots.delete(parseObject.getGuid());
//            cacheWorkSlots.delete(parseObject.getGuid());
        } else if (parseObject instanceof ItemAndListCommonInterface) {
            cache.delete(parseObject.getGuid());
//            cache.delete(parseObject.getGuid());
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    public synchronized void removeFromCache(ParseObject parseObject) {
//        if (parseObject instanceof WorkSlot) {
//            cacheWorkSlots.delete(parseObject.getObjectIdP());
////            cacheWorkSlots.delete(parseObject.getGuid());
////        } else if (parseObject instanceof ParseObject) {
//        } else {
//            if (parseObject != null) {
//                ItemAndListCommonInterface elt = (ItemAndListCommonInterface) parseObject;
////            for (ItemAndListCommonInterface subelt : elt.getList()) {
//                for (Object subelt : elt.getListFull()) {
//                    removeFromCache((ParseObject) subelt);
//                }
//            }
//            cache.delete(parseObject.getObjectIdP());
////            cache.delete(parseObject.getGuid());
//        }
////        else {
////            ASSERT.that("trying to delete non-ParseObject =" + parseObject);
////        }
//    }
//</editor-fold>
    public synchronized void removeFromCache(ParseObject parseObject) {
        if (parseObject instanceof WorkSlot) {
            cacheWorkSlots.delete(parseObject.getGuid());
//            cacheWorkSlots.delete(parseObject.getGuid());
//        } else if (parseObject instanceof ParseObject) {
        } else {
            if (parseObject != null) {
                ItemAndListCommonInterface elt = (ItemAndListCommonInterface) parseObject;
//            for (ItemAndListCommonInterface subelt : elt.getList()) {
                for (Object subelt : elt.getListFull()) {
                    removeFromCache((ParseObject) subelt);
                }
            }
            cache.delete(parseObject.getGuid());
//            cache.delete(parseObject.getGuid());
        }
//        else {
//            ASSERT.that("trying to delete non-ParseObject =" + parseObject);
//        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
    public Item fetchItemFromParseByGuid(String guid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereEqualTo(ParseObject.GUID, guid);
        try {
            List items = query.find();
            ASSERT.that(items.size() <= 1, "multiple Items with same guid = " + guid + "; items=" + items);
//            fetchListElementsIfNeededReturnCachedIfAvail(items); //need to refer to cached items since comparison is done via object identify
            if (items.size() > 0) {
                Item item = (Item) items.get(0);
                cachePut(item);
                return item;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public ItemList fetchItemListFromParseByGuid(String guid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
        query.whereEqualTo(ParseObject.GUID, guid);
        try {
            List items = query.find();
            ASSERT.that(items.size() <= 1, "multiple ItemLists with same guid = " + items);
//            fetchListElementsIfNeededReturnCachedIfAvail(items); //need to refer to cached items since comparison is done via object identify
            if (items.size() > 0) {
                ItemList itemList = (ItemList) items.get(0);
                cachePut(itemList);
                return itemList;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public Category fetchCategoryFromParseByGuid(String guid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Category.CLASS_NAME);
        query.whereEqualTo(ParseObject.GUID, guid);
        try {
            List items = query.find();
            ASSERT.that(items.size() <= 1, "multiple ItemLists with same guid = " + items);
//            fetchListElementsIfNeededReturnCachedIfAvail(items); //need to refer to cached items since comparison is done via object identify
            if (items.size() > 0) {
                Category category = (Category) items.get(0);
                cachePut(category);
                return category;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public WorkSlot fetchWorkSlotFromParseByGuid(String guid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
        query.whereEqualTo(ParseObject.GUID, guid);
        try {
            List items = query.find();
            ASSERT.that(items.size() <= 1, "multiple Items with same guid = " + items);
//            fetchListElementsIfNeededReturnCachedIfAvail(items); //need to refer to cached items since comparison is done via object identify
            if (items.size() > 0) {
                WorkSlot workSlot = (WorkSlot) items.get(0);
                cachePut(workSlot);
                return workSlot;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    public RepeatRuleParseObject fetchRepeatRuleFromParseByGuid(String guid) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(RepeatRuleParseObject.CLASS_NAME);
        query.whereEqualTo(ParseObject.GUID, guid);
        try {
            List items = query.find();
            ASSERT.that(items.size() <= 1, "multiple Items with same guid = " + items);
            if (items.size() > 0) {
                RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) items.get(0);
                cachePut(repeatRule);
                return repeatRule;
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    /**
     * fetches the Item with objectId. Returns null if no such Item.
     *
     * @param objectId
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Item fetchItemOLD(String objectId) {
//        Item item;
//        if (objectId == null || objectId.length() == 0) {
//            return null;
//        }
////        if ((item = (Item) cache.get(objectId)) != null) {
//        if ((item = (Item) cacheGet(objectId)) != null) {
//            return item;
//        }
////        Item item = null;
//        try {
//            item = ParseObject.fetch(Item.CLASS_NAME, objectId);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) item).isSoftDeleted();
//            }
////            cache.put(objectId, item);
//            cachePut(item);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return item;
//    }
//</editor-fold>
    public Item fetchItem(String guid) {
        Item item;
        if (guid == null || guid.length() == 0) {
            ASSERT.that("fetchItem called with no guid");
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((item = (Item) cacheGet(guid)) != null) {
            return item;
        }
        item = fetchItemFromParseByGuid(guid);
        if (Config.TEST) {
            ASSERT.that(item == null || !((ItemAndListCommonInterface) item).isSoftDeleted(), "soft-deletec item fetched by guid, item=" + item);
        }
        return item;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> fetchAllItemsOwnedByItemListXXX(ItemList itemList) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.whereEqualTo(Item.PARSE_OWNER_LIST, itemList);
//        try {
//            List items = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(items); //need to refer to cached items since comparison is done via object identify
//            return items;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> fetchAllItemsWithThisCategory(Category category) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.whereEqualTo(Item.PARSE_CATEGORIES, category);
//        try {
//            List items = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(items);  //need to refer to cached items since comparison is done via object identify
//            return items;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public WorkSlot fetchWorkSlot(String objectId) {
//        WorkSlot workSlot;
//        if (objectId == null || objectId.length() == 0) {
//            return null;
//        }
////        if ((item = (Item) cache.get(objectId)) != null) {
//        if ((workSlot = (WorkSlot) cacheGet(objectId)) != null) {
//            return workSlot;
//        }
////        Item item = null;
//        try {
//            workSlot = ParseObject.fetch(WorkSlot.CLASS_NAME, objectId);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) workSlot).isSoftDeleted();
//            }
////            cache.put(objectId, item);
//            cachePut(workSlot);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return workSlot;
//    }
//</editor-fold>
    /**
     * special case to fetch the Owner of an item, which may be either an Item
     * or an ItemList (for now). Used in ScreenItem.
     *
     * @param guid
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemAndListCommonInterface fetchItemOwner(String guid) {
//        ItemAndListCommonInterface elt = null;
//        if (guid == null || guid.length() == 0) {
//            return null;
//        }
////        if ((item = (Item) cache.get(objectId)) != null) {
//        if ((elt = (ItemAndListCommonInterface) cacheGet(guid)) != null) {
//            return elt;
//        }
//        try {
//            elt = ParseObject.fetch(Item.CLASS_NAME, guid);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) elt).isSoftDeleted();
//            }
//            if (elt instanceof ItemAndListCommonInterface) {
//                cachePut((ParseObject) elt);
//                return elt;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        try {
//            elt = ParseObject.fetch(ItemList.CLASS_NAME, guid);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) elt).isSoftDeleted();
//            }
//            if (elt instanceof ItemAndListCommonInterface) {
//                cachePut((ParseObject) elt);
//                return elt;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return elt;
//    }
//</editor-fold>
    public ItemAndListCommonInterface fetchItemOwner(String guid) {
        ItemAndListCommonInterface elt = null;
        if (guid == null || guid.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((elt = (ItemAndListCommonInterface) cacheGet(guid)) != null) {
            return elt;
        }
        Item item = fetchItemFromParseByGuid(guid);
        if (item != null) {
            return item;
        }
        ItemList itemList = fetchItemListFromParseByGuid(guid);
        return itemList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public Category fetchCategory(String objectId) {
//        Category category;
//        if (objectId == null || objectId.length() == 0) {
//            return null;
//        }
////        if ((item = (Item) cache.get(objectId)) != null) {
//        if ((category = (Category) cacheGet(objectId)) != null) {
//            return category;
//        }
////        Item item = null;
//        try {
//            category = ParseObject.fetch(Category.CLASS_NAME, objectId);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) category).isSoftDeleted();
//            }
////            cache.put(objectId, item);
//            cachePut(category);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return category;
//    }
//</editor-fold>
    public Category fetchCategory(String guid) {
        Category category;
        if (guid == null || guid.length() == 0) {
            return null;
        }
//        if ((item = (Item) cache.get(objectId)) != null) {
        if ((category = (Category) cacheGet(guid)) != null) {
            return category;
        }
        return fetchCategoryFromParseByGuid(guid);
    }

    public ItemAndListCommonInterface fetchElement(String guid) {
        if (guid == null || guid.length() == 0) {
            return null;
        }
        ParseObject element;
        if ((element = cacheGet(guid)) != null) {
            if (element instanceof ItemAndListCommonInterface) {
                return (ItemAndListCommonInterface) element;
            }
        }
        return null;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public RepeatRuleParseObject fetchRepeatRule(String objectId) {
//        RepeatRuleParseObject repeatRule;
//        if (objectId == null || objectId.length() == 0) {
//            return null;
//        }
////        if ((item = (Item) cache.get(objectId)) != null) {
//        if ((repeatRule = (RepeatRuleParseObject) cacheGet(objectId)) != null) {
//            return repeatRule;
//        }
////        Item item = null;
//        try {
//            repeatRule = ParseObject.fetch(RepeatRuleParseObject.CLASS_NAME, objectId);
//            if (Config.TEST) {
//                assert !((RepeatRuleParseObject) repeatRule).isSoftDeleted();
//            }
////            cache.put(objectId, item);
//            cachePut(repeatRule);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return repeatRule;
//    }
//</editor-fold>
//    public RepeatRuleParseObject fetchRepeatRule(String guid) {
//        RepeatRuleParseObject repeatRule;
//        if (guid == null || guid.length() == 0) {
//            return null;
//        }
//        if ((repeatRule = (RepeatRuleParseObject) cacheGet(guid)) != null) {
//            return repeatRule;
//        }
//        return fetchRepeatRuleFromParseByGuid(guid);
//    }
    //<editor-fold defaultstate="collapsed" desc="comment">
    /**
     *
     * @param objectId
     * @return
     */
//    public ItemAndListCommonInterface fetchFromCacheOnly(String guid) {
//        ItemAndListCommonInterface elt;
//        if (guid == null || guid.length() == 0) {
//            return null;
//        }
////        if ((elt = (ItemAndListCommonInterface) cache.get(objectId)) != null) {
//        if ((elt = (ItemAndListCommonInterface) cacheGet(guid)) != null) {
//            return elt;
//        }
////        Item item = null;
////        try {
////            item = ParseObject.fetchFromCacheOnly(Item.CLASS_NAME, objectId);
////            cache.put(objectId, item);
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
//        return null;
//    }
//</editor-fold>
    /**
     * fetches the Item with objectId. Returns null if no such Item.
     *
     * @param guid
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList fetchItemList(String objectId) {
//        ItemList itemList;
////        if ((itemList = (ItemList) cache.get(objectId)) != null) {
//        if ((itemList = (ItemList) cacheGet(objectId)) != null) {
//            return itemList;
//        }
//        try {
//            itemList = ParseObject.fetch(ItemList.CLASS_NAME, objectId);
//            if (Config.TEST) {
//                assert !((ItemAndListCommonInterface) itemList).isSoftDeleted();
//            }
//            cachePut(itemList);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return itemList;
//    }
//</editor-fold>
    public ItemList fetchItemList(String guid) {
        ItemList itemList;
        if ((itemList = (ItemList) cacheGet(guid)) != null) {
            return itemList;
        }
        return fetchItemListFromParseByGuid(guid);
    }

    /**
     * will run through list and look up every element in cache (same
     * ParseObjectId) and if found replace with cached element. If an element is
     * not found in cache, will fetch it from Parse and cache it.
     *
     * @param list
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public List fetchListElementsIfNeededReturnCachedIfAvailUNCLEANED(List list) {
//        assert (list != null) : "updating null list from cache";
//        for (int i = 0, size = list.size(); i < size; i++) {
////            Object cachedObject;
//            Object val = list.get(i);
//            if (val == null || val == JSONObject.NULL) {
////                ASSERT.that((list.get(i) != null) , "entry nb=" + i + " in list  with size" + size+", name="+(list instanceof ItemList ? ((ItemList) list).getText() : "") + " == null");
//                if (Config.TEST) {
//                    int i2 = i;
//                    int size2 = size;
//                    ASSERT.that((val != null), ()
//                            -> "entry nb=" + i2 + " is null! In list  with size=" + size2
//                            + ", name=" + (list instanceof ItemAndListCommonInterface ? ((ItemAndListCommonInterface) list).getText() : "<none>")
//                            + ", parseId=" + (list instanceof ParseObject ? ((ParseObject) list).getObjectIdP() : "<none>")
//                            + ", toString=" + list.toString());
//                    ASSERT.that((val != JSONObject.NULL), () -> "entry nb=" + i2 + " in list  with size" + size2 + ", name="
//                            + (list instanceof ItemList ? ((ItemList) list).getText() : "")
//                            + ", parseId=" + (list instanceof ParseObject ? ((ParseObject) list).getObjectIdP() : "") + " == JSONObject.NULL");
//                }
////                ASSERT.that((list.get(i) != JSONObject.NULL), "entry nb=" + i + " in list " + (list instanceof ItemList ? ((ItemList) list).getText() : "") + " == JSONObject.NULL");
//                list.remove(i); //UI: clean up elements that don't exist anymore
//                i--; //neutralize the i++ in the loop
//                size--; //update size to match actual size of loop
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
////                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) list.get(i)));
//                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) val));
//            }
//        }
//        return list;
//    }
//</editor-fold>
    public List fetchListElementsIfNeededReturnCachedIfAvail(List list) {
        return fetchListElementsIfNeededReturnCachedIfAvail(list, false);
    }

    public List fetchListElementsIfNeededReturnCachedIfAvail(List list, boolean dontFetchFromParse) {
        if (Config.TEST) {
            ASSERT.that((list != null), "updating null list from cache");
        }
//        if (list==null || list.isEmpty()) return list;
        long start = MyDate.currentTimeMillis();
        if (Config.TEST && list.size() > 200) {
            Log.p("calling DAO.fetchListElementsIfNeededReturnCachedIfAvail with list.size >= 200 = " + list.size());
        }
        for (int i = 0, size = list.size(); i < size; i++) {
            Object val = list.get(i);
            if (val == null) {//&& Config.TEST) {
                ASSERT.that(val != null, "NULL ELEMENT IN LIST - REMOVING IT!!!! index=" + i + ", size=" + size + ", List=" + list);
                list.remove(i);
                size--;
            } else {
                list.set(i, fetchIfNeededReturnCachedIfAvail((ParseObject) val, dontFetchFromParse));
                if (Config.TEST) {
                    ASSERT.that(list.get(i) != null, () -> "null returned from cache for object=" + val + "; for list=" + list);
                }
            }
        }
//        if (Config.TEST) Log.p("DAO.fetchListElementsIfNeededReturnCachedIfAvail for list.size()="+list.size()+" took "+MyDate.formatDuration(start-MyDate.currentTimeMillis()));
        if (Config.TEST && (MyDate.currentTimeMillis() - start) > 200) {
            if (false) {
                Log.p("DAO.fetchListElementsIfNeededReturnCachedIfAvail took " + (MyDate.currentTimeMillis() - start) + "ms, for list.size()=" + list.size() + " " + "; list=" + list);
            } else {
                if (false) {
                    ASSERT.that("DAO.fetchListElementsIfNeededReturnCachedIfAvail took " + (MyDate.currentTimeMillis() - start) + "ms, for list.size()=" + list.size());// + " " + "; list=" + list);
                }
            }
        }
        return list;
    }

    public List<Item> fetchListOfItemsFromListOfGuids(List<String> list) {
        if (Config.TEST) {
            ASSERT.that((list != null), "updating null list from cache");
        }
        ArrayList<Item> items = new ArrayList<>();
        for (String guid : list) {
            items.add(fetchItem(guid));
        }
        return items;
    }

    /**
     * gets cached elements
     *
     **
     * @param list
     * @return
     */
//    public List<ItemAndListCommonInterface> fetchListOfItemInterfaceFromListOfObjectIds(List<String> list) {
//        if (Config.TEST) {
//            ASSERT.that((list != null), "updating null list from cache");
//        }
//        ArrayList<ItemAndListCommonInterface> items = new ArrayList<>();
//        for (String objectId : list) {
//            items.add((ItemAndListCommonInterface) cacheGet(objectId));
//        }
//        return items;
//    }
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
     * @param parseObjectWithGuid
     * @return null if object does not (or no longer) exist on server
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ParseObject fetchIfNeededReturnCachedIfAvail(ParseObject parseObject) {
//        if (parseObject == null) {
//            return null;
//        }
//        if (parseObject.getObjectIdP() == null || parseObject.getObjectIdP().equals("")) {
//            //this happens for example when getting elements from **
////            cachePut(parseObject); //NO, can't cache without a objectId [//cache it to avoid duplicates (rellay necessary?)]
//            if (false && parseObject.getGuid() != null) {
//                Object guidCached = cacheGuid.get(parseObject.getGuid());
//                if (guidCached != null) {
//                    return (ParseObject) guidCached;
//                }
//            }
//            return parseObject; //for not yet saved parseObjects (pending saving), return the object itself
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
////        } else if (parseObject instanceof WorkSlot) {
//
//        } else {
//            try {
//                parseObject.fetchIfNeeded();
//                if (Config.TEST) {
//                    if (parseObject instanceof ItemAndListCommonInterface) {
//                        ASSERT.that(!((ItemAndListCommonInterface) parseObject).isSoftDeleted(), () -> "DAO.fetch from Parse of soft-deleted object:" + parseObject);
//                    } else if (parseObject instanceof FilterSortDef) {
//                        //                        assert !((FilterSortDef) parseObject).isDeleted();
//                        ASSERT.that(!((FilterSortDef) parseObject).isDeleted(), () -> "DAO.fetch of deleted object:" + parseObject);
//                    } else if (parseObject instanceof RepeatRuleParseObject) {
////                        assert !((RepeatRuleParseObject) parseObject).isSoftDeleted();
//                        ASSERT.that(!((RepeatRuleParseObject) parseObject).isSoftDeleted(), () -> "DAO.fetch of deleted object:" + parseObject);
//                    }
//                }
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
////<editor-fold defaultstate="collapsed" desc="comment">
////NO need to fetch lists within the object, they are updated when they are used first time (in getList() using fetchListElementsIfNeededReturnCachedIfAvail()...)
////                if (parseObject instanceof ItemAndListCommonInterface) {
////                    List list;
////                    if ((list = ((ItemAndListCommonInterface) parseObject).getList()) != null) {
////                        fetchAllElementsInSublist(list);
////                    }
////                }
////</editor-fold>
//                return parseObject;
//            } catch (ParseException ex) {
////            Log.e(ex);
//                return null;
//            }
//        }
//    }
//</editor-fold>
    public ParseObject fetchIfNeededReturnCachedIfAvail(ParseObject parseObjectWithGuid) {
        return fetchIfNeededReturnCachedIfAvail(parseObjectWithGuid, false);
    }

    /**
     *
     * @param parseObjectWithGuid
     * @param dontFetchFromParse only fetch list elements from cache (e.g. for
     * temporary lists build for Statistics where all elements must already be
     * local or are 'artificial' elements created locally!)
     * @return
     */
    public ParseObject fetchIfNeededReturnCachedIfAvail(ParseObject parseObjectWithGuid, boolean dontFetchFromParse) {
        if (parseObjectWithGuid == null) { //normal with null, for all potential references like to Source which is often undefined/empty
//            ASSERT.that("fetchIfNeededReturnCachedIfAvail called with null");
            return null;
        }
//        ASSERT.that (parseObject.getObjectIdP() != null,()->"trying to fetch a parseObject with NO objId, parseObj="+parseObject);
        ASSERT.that(parseObjectWithGuid.getGuid() != null, () -> "trying to fetch a parseObject with NO guid. obj= " + parseObjectWithGuid);
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            if (parseObjectWithGuid.getObjectIdP() == null || parseObjectWithGuid.getObjectIdP().equals("")) {
//                //this happens for example when getting elements from **
////            cachePut(parseObject); //NO, can't cache without a objectId [//cache it to avoid duplicates (rellay necessary?)]
//                if (false && parseObjectWithGuid.getGuid() != null) {
//                    Object guidCached = cacheGuid.get(parseObjectWithGuid.getGuid());
//                    if (guidCached != null) {
//                        return (ParseObject) guidCached;
//                    }
//                }
//                return parseObjectWithGuid; //for not yet saved parseObjects (pending saving), return the object itself
//            }
//        }
//</editor-fold>
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
        if ((temp = cacheGet(parseObjectWithGuid)) != null) {
            return temp;
//        } else if (parseObject instanceof WorkSlot) {
        } else if (dontFetchFromParse || parseObjectWithGuid.getObjectIdP() == null) { //if new, not yest saved object, just return the object itself //if not in cache and not supposed to fetch in Parse, then simply use the object itself
            return parseObjectWithGuid;
        } else {
            ASSERT.that(parseObjectWithGuid.getObjectIdP() != null,
                    () -> "trying to fetch a parseObject from ParseServer with NO objId!!! guid= " + parseObjectWithGuid.getGuid());
            try {
                parseObjectWithGuid.fetchIfNeeded();
//                ASSERT.that(cacheGet(parseObjectWithGuid) == null, () -> "just fetched parseObjectWithGuid with guid ALREADY in cache, guid=" + parseObjectWithGuid.getGuid()+"; fetched="+parseObjectWithGuid+"; in cache="+cacheGet(parseObjectWithGuid));
                if (Config.TEST) {
                    Object alreadyCached = cacheGet(parseObjectWithGuid);
                    ASSERT.that(alreadyCached == null,
                            () -> "just fetched parseObjectWithGuid with guid ALREADY in cache, guid=" + parseObjectWithGuid.getGuid()
                            + "; fetched=" + ItemAndListCommonInterface.toIdString(parseObjectWithGuid)
                            + "; in cache=" + ItemAndListCommonInterface.toIdString(alreadyCached));
                    if (parseObjectWithGuid instanceof ItemAndListCommonInterface) {
                        ASSERT.that(!((ItemAndListCommonInterface) parseObjectWithGuid).isSoftDeleted(), () -> "DAO.fetch from Parse of soft-deleted object:" + parseObjectWithGuid.getGuid());
                    } else if (parseObjectWithGuid instanceof FilterSortDef) {
                        //                        assert !((FilterSortDef) parseObject).isDeleted();
                        ASSERT.that(!((FilterSortDef) parseObjectWithGuid).isDeleted(), () -> "DAO.fetch of deleted object:" + parseObjectWithGuid.getGuid());
                    } else if (parseObjectWithGuid instanceof RepeatRuleParseObject) {
//                        assert !((RepeatRuleParseObject) parseObject).isSoftDeleted();
                        ASSERT.that(!((RepeatRuleParseObject) parseObjectWithGuid).isSoftDeleted(), () -> "DAO.fetch of deleted object:" + parseObjectWithGuid.getGuid());
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
                cachePut(parseObjectWithGuid);
//<editor-fold defaultstate="collapsed" desc="comment">
//NO need to fetch lists within the object, they are updated when they are used first time (in getList() using fetchListElementsIfNeededReturnCachedIfAvail()...)
//                if (parseObject instanceof ItemAndListCommonInterface) {
//                    List list;
//                    if ((list = ((ItemAndListCommonInterface) parseObject).getList()) != null) {
//                        fetchAllElementsInSublist(list);
//                    }
//                }
//</editor-fold>
                return parseObjectWithGuid;
            } catch (ParseException ex) {
//            Log.e(ex);
//                return parseObjectWithGuid; //in case of error better to return object?! PROBABLY NOT since it may hide some errors, leave returning null for now
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
//<editor-fold defaultstate="collapsed" desc="comment">
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
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
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
        List noDups = new ArrayList(); //optimization: make a hashset? and convert to 
        for (Object obj : list) {
            if (!noDups.contains(obj)) {
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
    private List<ItemAndListCommonInterface> cachedToday;
    private Date cachedExpiryToday;

    /**
     * sort Today list. Sort order: Due > > WaitingTill > Start > Alarm >
     * WorkSlots
     *
     * @param list
     */
    private void sortToday(List<ItemAndListCommonInterface> list) {
//        Collections.sort(list, (i1, i2) -> compareInt(i1.getTodaySortOrder().getSortOrder(), i2.getTodaySortOrder().getSortOrder()));
        Collections.sort(list, (i1, i2) -> Integer.compare(i1.getTodaySortOrder().getSortOrder(), i2.getTodaySortOrder().getSortOrder()));

    }

//    public List<ItemAndListCommonInterface> getTodayDueAndOrWaitingOrWorkSlotsItems(boolean includeWaiting, boolean includeStartingToday) {
    public List<ItemAndListCommonInterface> getToday() {

        if (cachedToday != null && cachedExpiryToday != null && System.currentTimeMillis() <= cachedExpiryToday.getTime()) {
            return cachedToday;
        } else {
            List<ParseQuery> queries = new ArrayList<>();

            Date startOfToday = MyDate.getStartOfDay(new Date());
            Date startOfTomorrow = new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS);

            Date startDate = new Date(MyDate.currentTimeMillis() - MyPrefs.todayViewIncludeOverdueFromThisManyPastDays.getInt() * MyDate.DAY_IN_MILLISECONDS);
            ParseQuery<Item> queryDueToday = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(queryDueToday, true);
            queryDueToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startDate);
            queryDueToday.whereLessThan(Item.PARSE_DUE_DATE, startOfTomorrow);
            if (MyPrefs.todayViewShowLeafTasksInsteadOfProjects.getBoolean()) {
                queryDueToday.whereExists(Item.PARSE_SUBTASKS);
                queryDueToday.whereDoesNotExist(Item.PARSE_OWNER_ITEM);
            } else {
                queryDueToday.whereDoesNotExist(Item.PARSE_SUBTASKS);
            }
            queryDueToday.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
            setupItemQueryWithIndirectAndGuids(queryDueToday);
            queries.add(queryDueToday);

            if (MyPrefs.todayViewIncludeWaitingExpiringToday.getBoolean()) {
                ParseQuery<Item> queryWaitingExpiresToday = ParseQuery.getQuery(Item.CLASS_NAME);
                setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingExpiresToday, false); //false: we only fetch Waiting
                queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_WAIT_UNTIL_DATE, startOfToday);
                queryWaitingExpiresToday.whereLessThan(Item.PARSE_WAIT_UNTIL_DATE, startOfTomorrow);
                if (false) { //TODO!!! find a way to eliminate duplicates, e.g. a task which is both due today, waiting till today and has an alarm today...
                    queryWaitingExpiresToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday); //don't get Waiting tasks that are due today
                    queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfTomorrow); //don't get Waiting tasks that are due today
                }
                queryWaitingExpiresToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED

                queryWaitingExpiresToday.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
                setupItemQueryWithIndirectAndGuids(queryWaitingExpiresToday);
                queries.add(queryWaitingExpiresToday);
            }

            if (MyPrefs.todayViewIncludeStartingToday.getBoolean()) {
                ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
                setupItemQueryNotTemplateNotDeletedLimit10000(queryStartToday, true);
                queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, startOfToday);
                queryStartToday.whereLessThan(Item.PARSE_START_BY_DATE, startOfTomorrow);
                queryStartToday.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
                setupItemQueryWithIndirectAndGuids(queryStartToday);
                queries.add(queryStartToday);
            }

            if (MyPrefs.todayViewIncludeAlarmsExpiringToday.getBoolean()) {
                ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
                setupItemQueryNotTemplateNotDeletedLimit10000(queryStartToday, false);
                queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_ALARM_DATE, startOfToday);
                queryStartToday.whereLessThan(Item.PARSE_ALARM_DATE, startOfTomorrow);
                queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(),
                        ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides
                queryStartToday.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
                setupItemQueryWithIndirectAndGuids(queryStartToday);
                queries.add(queryStartToday);
            }

            List allTodayElements = new ArrayList();

            //get all items
            try {
                ParseQuery<Item> query = ParseQuery.getOrQuery(queries);
                query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
                query.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
                List result = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(result);
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
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
                queryWorkSlots.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
                setupWorkSlotQueryWithIndirectAndGuids(queryWorkSlots);

                try {
                    List<WorkSlot> resultsWorkSlots = queryWorkSlots.find();
                    fetchListElementsIfNeededReturnCachedIfAvail(resultsWorkSlots);
                    allTodayElements.addAll(resultsWorkSlots); //real hack: disguise workslot as task... TODO!!!! No good, because treats workslot as task (e.g can edit task fields, cannot edit workslot!!
                } catch (ParseException ex) {
                    Log.e(ex);
                }
            }

            removeDuplicates(allTodayElements); //TODO!!!: will remove duplicate tasks, but not tasks in workslots, not sure this is an issue?!
            cachedToday = allTodayElements;
            cachedExpiryToday = MyDate.getEndOfToday(); //valid until midnight
            return allTodayElements;
        }
    }

    private void checkAndRefreshToday(ItemAndListCommonInterface element, boolean delete) {
        if (cachedToday != null) {
            if (delete) {
                cachedToday.remove(element);
            } else {
                boolean hasTodayDate = element.hasTodayDates();
                Date firstDate = getCreationLogStartDate();
                if (!cachedToday.contains(element)) {
                    if (hasTodayDate) {
                        cachedToday.add(element);
                        sortToday(cachedToday);
                    }
                } else {
                    if (!hasTodayDate) { //if element no longer has a date Today, then remove it
                        cachedToday.remove(element); //no need to sort, list remaings sorted
                    }
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getTodayXXX(ItemList existingListToUpdate) {
////        ParseQuery<Item> query = getDueAndOrWaitingTodayQuery(includeWaiting, includeStartingToday);
//
//        List<ParseQuery> queries = new ArrayList<>();
//
//        ParseQuery<Item> query = null;
//
//        Date startOfToday = MyDate.getStartOfDay(new Date());
//        Date startOfTomorrow = new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS);
//
//        Date startDate = new Date(MyDate.currentTimeMillis() - MyPrefs.todayViewIncludeOverdueFromThisManyPastDays.getInt() * MyDate.DAY_IN_MILLISECONDS);
//        ParseQuery<Item> queryDueToday = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryDueToday, true);
//        queryDueToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startDate);
//        queryDueToday.whereLessThan(Item.PARSE_DUE_DATE, startOfTomorrow);
//        if (MyPrefs.todayViewShowProjectsInsteadOfLeafTasks.getBoolean()) {
//            queryDueToday.whereExists(Item.PARSE_SUBTASKS);
//            queryDueToday.whereDoesNotExist(Item.PARSE_OWNER_ITEM);
//        } else {
//            queryDueToday.whereDoesNotExist(Item.PARSE_SUBTASKS);
//        }
////        queryDueToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//
////        queryDueToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetch any templates
////        queryDueToday.whereDoesNotExist(Item.PARSE_DELETED_DATE); //or deleted
//        queries.add(queryDueToday);
//
//        if (MyPrefs.todayViewIncludeWaitingExpiringToday.getBoolean()) {
//            ParseQuery<Item> queryWaitingExpiresToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingExpiresToday, false); //false: we only fetch Waiting
//            queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_WAITING_TILL_DATE, startOfToday);
//            queryWaitingExpiresToday.whereLessThan(Item.PARSE_WAITING_TILL_DATE, startOfTomorrow);
//            if (false) { //TODO!!! find a way to eliminate duplicates, e.g. a task which is both due today, waiting till today and has an alarm today...
//                queryWaitingExpiresToday.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, startOfToday); //don't get Waiting tasks that are due today
//                queryWaitingExpiresToday.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfTomorrow); //don't get Waiting tasks that are due today
//            }
//            queryWaitingExpiresToday.whereEqualTo(Item.PARSE_STATUS, ItemStatus.WAITING.toString()); //item that are NOT DONE or CANCELLED
//
////            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////            queryWaitingExpiresToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//            queries.add(queryWaitingExpiresToday);
//        }
//
//        if (MyPrefs.todayViewIncludeStartingToday.getBoolean()) {
//            ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupItemQueryNotTemplateNotDeletedLimit10000(queryStartToday, true);
//            queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, startOfToday);
//            queryStartToday.whereLessThan(Item.PARSE_START_BY_DATE, startOfTomorrow);
////            queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(), ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides
//
////            queryStartToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////            queryStartToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//            queries.add(queryStartToday);
//        }
//
//        if (MyPrefs.todayViewIncludeAlarmsExpiringToday.getBoolean()) {
//            ParseQuery<Item> queryStartToday = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupItemQueryNotTemplateNotDeletedLimit10000(queryStartToday, false);
//            queryStartToday.whereGreaterThanOrEqualTo(Item.PARSE_ALARM_DATE, startOfToday);
//            queryStartToday.whereLessThan(Item.PARSE_ALARM_DATE, startOfTomorrow);
//            queryStartToday.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString(),
//                    ItemStatus.WAITING.toString()))); //item that are NOT DONE or CANCELLED, don't include WAITING since waiting for a later date overrides
//
////            queryStartToday.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////            queryStartToday.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//            queries.add(queryStartToday);
//        }
//
//        List allTodayElements = new ArrayList();
//
//        //get all items
//        try {
//            query = ParseQuery.getOrQuery(queries);
//            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//            List result = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(result);
////            if (existingListToUpdate instanceof ItemList) {
////                //if we already have an existing list (that eg the timer may be running on), then simply update it
////                existingListToUpdate.clear();
////                existingListToUpdate.addAllNotMakingOwner(result);
////            } else {
//            allTodayElements.addAll(result);
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//
//        //get workslots
//        //WORKSLOTS - get workslots starting today
//        if (MyPrefs.todayViewIncludeWorkSlotsCoveringToday.getBoolean()) { //fetch WorkSLots that have workTime between now and end of today
//
//            ParseQuery<WorkSlot> queryWorkSlots = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//            queryWorkSlots.whereGreaterThan(WorkSlot.PARSE_END_TIME, new MyDate()); //slots that end *after* *now*
//            queryWorkSlots.whereLessThan(WorkSlot.PARSE_START_TIME, startOfTomorrow); //and starts before tomorrow
//
//            queryWorkSlots.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//
//            try {
//                List<WorkSlot> resultsWorkSlots = queryWorkSlots.find();
//                fetchListElementsIfNeededReturnCachedIfAvail(resultsWorkSlots);
////                if (existingListToUpdate instanceof ItemList) {
////                    //if we already have an existing list (that eg the timer may be running on), then simply update it
//////                existingListToUpdate.clear(); //Cleared above
////                    existingListToUpdate.addAllNotMakingOwner(resultsWorkSlots);
////                } else {
//                allTodayElements.addAll(resultsWorkSlots); //real hack: disguise workslot as task... TODO!!!! No good, because treats workslot as task (e.g can edit task fields, cannot edit workslot!!
////                }
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
//
//        removeDuplicates(allTodayElements); //TODO!!!: will remove duplicate tasks, but not tasks in workslots, not sure this is an issue?!
////        fetchListElementsIfNeededReturnCachedIfAvail(allTodayElements);
//        if (existingListToUpdate instanceof ItemList) {
//            existingListToUpdate.clear(); //Cleared above
//            existingListToUpdate.addAllNotMakingOwner(allTodayElements);
//            return existingListToUpdate;
//        } else {
//            return new ItemList(allTodayElements);
//        }
//    }
//</editor-fold>
    private Date getOverdueStartDate() {
        return new Date(MyDate.getStartOfToday().getTime() - MyPrefs.overdueLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortOverdue(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getDueDate(), i2.getDueDate())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedOverdue;
    private Date cachedExpiryOverdue;

    public List<Item> getOverdue() {
        if (cachedOverdue != null && cachedOverdue != null && System.currentTimeMillis() <= cachedExpiryOverdue.getTime()) {
            return cachedOverdue;
        } else {
            Date startOfToday = MyDate.getStartOfToday();
//        Date startOfOverdueInterval = new Date(startOfToday.getTime() - MyPrefs.overdueLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
            Date startOfOverdueInterval = getOverdueStartDate();

            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
            setupItemQueryWithIndirectAndGuids(query);
            query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfOverdueInterval);
            query.whereLessThan(Item.PARSE_DUE_DATE, startOfToday);

            try {
                List results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortOverdue(results);
                cachedOverdue = results;
                cachedExpiryOverdue = MyDate.getEndOfToday(); //valid until midnight
                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
        return null;//new ItemList();
    }

    private void checkAndRefreshOverdue(Item element, boolean delete) {
        if (cachedOverdue != null) {
            if (delete) {
                cachedOverdue.remove(element);
            } else {
                Date startDate = getOverdueStartDate();
                Date startOfToday = MyDate.getStartOfToday();
                if (!cachedOverdue.contains(element)) {
                    if (element.getDueDate().getTime() >= startDate.getTime() && element.getDueDate().getTime() < startOfToday.getTime()) {
                        cachedOverdue.add(element);
                        sortOverdue(cachedOverdue);
                    }
                } else if (element.getDueDate().getTime() < startDate.getTime() || element.getDueDate().getTime() > startOfToday.getTime()) {
                    cachedOverdue.remove(element);
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getOverdueXXX(ItemList existingListToUpdate) {
//        Date startOfToday = MyDate.getStartOfToday();
//        Date startOfOverdueInterval = new Date(startOfToday.getTime() - MyPrefs.overdueLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
//
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
//        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, startOfOverdueInterval);
//        query.whereLessThan(Item.PARSE_DUE_DATE, startOfToday);
////        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////        query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
////        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
////        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
//            List<Item> results = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(results);
////            if (existingListToUpdate instanceof ItemList) {
//            //if we already have an existing list (that eg the timer may be running on), then simply update it
//            existingListToUpdate.clear();
//            existingListToUpdate.addAllNotMakingOwner(results);
//            return existingListToUpdate;
//////            } else {
//////                return new ItemList(results);
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;//new ItemList();
//    }
//</editor-fold>
    public static List<ItemAndListCommonInterface> getTodayLeafTaskListZZZ(List<ItemAndListCommonInterface> all) {
        //UI: badgecount includes all elements shown in Today view (counting leaf-tasks for Projects!)
        List<ItemAndListCommonInterface> leafList = new ArrayList();
        for (ItemAndListCommonInterface elt : all) {
            if (elt instanceof WorkSlot) {
//                count += ((WorkSlot) elt).getItemsInWorkSlot().size();
//                leafList.addAll(((WorkSlot) elt).getItemsInWorkSlot());
                List<Item> tasksInWorkSlot = ((WorkSlot) elt).getItemsInWorkSlot();
                for (Item t : tasksInWorkSlot) {
                    leafList.addAll(t.getLeafTasksAsListN((itm) -> !itm.isDone()));
                }
            } else if (elt instanceof Item) {
                Item item = (Item) elt;
                if (item.isProject()) {
//                    count += item.getLeafTasksAsList((itm) -> !itm.isDone()).size();
                    leafList.addAll(item.getLeafTasksAsListN((itm) -> !itm.isDone()));
                }
            } else {
                leafList.add(elt);
            }
        }
        return leafList;
    }

    /**
     * return number of undone tasks due today (between midnigth and midnight)
     *
     * @param onlyDone
     * @return
     */
//    public int getBadgeCount(boolean includeWaiting, boolean includeStartingToday) {
    public int getBadgeCount() {
        //UI: badgecount includes all elements shown in Today view (counting leaf-tasks for Projects!)
//<editor-fold defaultstate="collapsed" desc="comment">
//        return getDueAndOrWaitingTodayCount(includeWaiting, includeStartingToday);
//        if (true) {
//            List<ItemAndListCommonInterface> all = getToday(null);
//            int count = 0;
//            for (ItemAndListCommonInterface elt : all) {
//                if (elt instanceof WorkSlot) {
//                    count += ((WorkSlot) elt).getItemsInWorkSlot().size();
//                } else if (elt instanceof Item) {
//                    Item item = (Item) elt;
//                    if (item.isProject()) {
//                        count += item.getLeafTasksAsList((itm) -> !itm.isDone()).size();
//                    }
//                } else {
//                    count++;
//                }
//            }
//            return count;
//        } else {
//            List todayList = DAO.this.getToday(null);
//            return todayList.size();
//        }
//</editor-fold>
//        return getTodayLeafTaskList(getToday(null)).size();
//        return getTodayLeafTaskListZZZ(getToday()).size();
        return getToday().size();
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
    private Date getNextEndDate() {
        return new MyDate(MyDate.getEndOfToday().getTime() + MyPrefs.nextInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortNext(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getDueDate(), i2.getDueDate())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedNext;
    private Date cachedExpiryNext;

    /**
     * returns future Undone tasks with a future StartDate or DueDate
     *
     * @return
     */
    public List<Item> getNext() {
        if (cachedNext != null && cachedNext != null && System.currentTimeMillis() <= cachedExpiryNext.getTime()) {
            return cachedNext;
        } else {
            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
//            Date startOfToday = MyDate.getStartOfToday();
//            query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS));
//            query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, MyDate.getEndOfToday());
            query.whereGreaterThan(Item.PARSE_DUE_DATE, MyDate.getEndOfToday());
            Date nextEndDate = getNextEndDate();
//            query.whereLessThan(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS * (1 + MyPrefs.nextInterval.getInt()))); //+1 since Next always start tomorrow
            query.whereLessThanOrEqualTo(Item.PARSE_DUE_DATE, nextEndDate); //+1 since Next always start tomorrow
            setupItemQueryWithIndirectAndGuids(query);
            List results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortNext(results);
                cachedNext = results;
                cachedExpiryNext = MyDate.getEndOfToday(); //valid until midnight
                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
        }
        return null;
    }

    private void checkAndRefreshNext(Item element, boolean delete) {
        if (cachedNext != null) {
            if (delete) {
                cachedNext.remove(element);
            } else {
                Date endDate = getNextEndDate();
                if (!cachedNext.contains(element)) {
                    if (element.getDueDate().getTime() >= MyDate.getEndOfToday().getTime() && element.getDueDate().getTime() <= endDate.getTime()) {
                        cachedNext.add(element);
                        sortNext(cachedNext);
                    }
                } else if (element.getDueDate().getTime() > endDate.getTime() || element.getDueDate().getTime() < MyDate.getEndOfToday().getTime()) {
                    cachedNext.remove(element);
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getCalendarXXX(ItemList existingListToUpdate) {
////        Calendar cal = Calendar.getInstance();
////        cal.set(Calendar.HOUR_OF_DAY, 0);
////        cal.set(Calendar.MINUTE, 0);
////        cal.set(Calendar.SECOND, 0);
////        cal.set(Calendar.MILLISECOND, 0);
//
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
////        queryDue.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
////        queryDue.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
////        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////        queryDue.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, cal.getTime());
////        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, MyDate.getMidnightOfDay(new Date()));
//        Date startOfToday = MyDate.getStartOfToday();
//        query.whereGreaterThanOrEqualTo(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS));
//        query.whereLessThan(Item.PARSE_DUE_DATE, new Date(startOfToday.getTime() + MyDate.DAY_IN_MILLISECONDS * (1 + MyPrefs.nextInterval.getInt()))); //+1 since Next always start tomorrow
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        if (false) {
////            ParseQuery<Item> queryStart = ParseQuery.getQuery(Item.CLASS_NAME);
////            setupStandardItemQuery(queryStart);
////            queryStart.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
////            queryStart.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
////            queryStart.whereGreaterThanOrEqualTo(Item.PARSE_START_BY_DATE, cal.getTime());
//        }
//
////        ParseQuery<Item> queryOr = ParseQuery.getOrQuery(new ArrayList({queryDue, queryStart}));
//        List<Item> results = null;
//        try {
//            if (false) {
////                ParseQuery<Item> queryOr = ParseQuery.getOrQuery(Arrays.asList(queryDue, queryStart));
////                queryOr.orderByDescending(Item.PARSE_DUE_DATE);
////                results = queryOr.find();
//            } else {
//                results = query.find();
////                fetchAllElementsInSublist(results, false);
//                fetchListElementsIfNeededReturnCachedIfAvail(results);
////                if (existingListToUpdate instanceof ItemList) {
//                //if we already have an existing list (that eg the timer may be running on), then simply update it
//                existingListToUpdate.clear();
//                existingListToUpdate.addAllNotMakingOwner(results);
//                return existingListToUpdate;
////                } else {
////                    return new ItemList(results);
////                }
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null; //new ItemList();
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
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
        CategoryList listCached = null;

        listCached = (CategoryList) cacheGetNamed(CategoryList.CLASS_NAME);
        if (listCached != null) {// && !forceLoadFromParse) {
            return listCached;
        }

        List<CategoryList> results;
        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
//        query.selectKeys(new ArrayList());
        query.whereDoesNotExist(CategoryList.PARSE_DELETED_DATE); //normally never deleted, but could happen in the future
        query.include(CategoryList.PARSE_CATEGORY_LIST);
        query.selectKeys(Arrays.asList(
                CategoryList.PARSE_CATEGORY_LIST + "." + ParseObject.GUID,
                ParseObject.GUID,
                CategoryList.PARSE_TEXT
        ));

        try {
            results = query.find();
            if (results.size() > 0) {
                if (Config.TEST) {
                    ASSERT.that(results.size() <= 1, () -> "error: more than one TemplateList element (" + results + ")"); //TODO create error log for this 
                }
                listCached = results.get(0); //return first element
//                cachePut(CategoryList.CLASS_NAME, listCached); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
            } else { //initialise the templateList for the first time
                listCached = new CategoryList();
//                saveNew((ParseObject) listCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                saveNewTriggerUpdate();
//                saveToParseNow((ParseObject) listCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
                saveToParseAndWait((ParseObject) listCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                cachePut(CategoryList.CLASS_NAME, listCached); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        cachePut(CategoryList.CLASS_NAME, listCached);
        return listCached;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public CategoryList getCategoryListOLD() {
////        CategoryList categoryList = null;
//        List<CategoryList> results = null;
//        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
////        setupItemQueryNotTemplateNotDeletedLimit10000(query);
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
//            results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
//            if (results.size() > 0) {
//                int size = results.size();
//                ASSERT.that(results.size() <= 1, () -> "error: more than one CategoryList element (" + size + ")"); //TODO create error log for this
//                CategoryList categoryList = results.get(0); //return first element
//                CategoryList cachedCategoryList = (CategoryList) cacheGet(categoryList);
//                if (cachedCategoryList != null) {
//                    categoryList = cachedCategoryList;
//                } else {
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                    cachePut(categoryList); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
//                    fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements
//                }
//                return categoryList;
//            } else {
//                CategoryList categoryList2 = new CategoryList(); //need a separate instance because use in lambda below
////                categoryList.addAll(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
////                saveAndWait((ParseObject) categoryList); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
////                cachePut(categoryList); //always save so new lists can be assigned to it
////                saveInBackground((ParseObject) categoryList2, () -> cachePut(categoryList2)); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
////                saveNew((ParseObject) categoryList2, true); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
//                saveNew((ParseObject) categoryList2); //saveAndWait to ensure an ObjectId is assigned for the cache storage. Always save so new lists can be assigned to it
//                saveNewExecuteUpdate();
//                cachePut(categoryList2);
//                return categoryList2;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (categoryList != null) {
////            cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        CategoryList categoryListTmp = (CategoryList) DAO.getInstance().fetchFromCacheOnly(categoryList.getObjectIdP()); //use same object if already cached
////        if (categoryListTmp != null)
////            categoryList = categoryListTmp;
////        else {
////            fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements
////            cachePut(categoryList); //cache list
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        return categoryList;
//        assert false;
//        return null;
//    }
//    public CategoryList getCategoryListXXX(boolean forceLoadFromParse, Date startDate, Date endDate) {
//        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");
//
//        CategoryList categoryList = null;
//        List<CategoryList> results = null;
//        ParseQuery<CategoryList> query = ParseQuery.getQuery(CategoryList.CLASS_NAME);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        if (!forceLoadFromParse) {
//            if (startDate != null) {
//                query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
//            }
//            if (endDate != null) {
//                query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
//            }
//        }
//        try {
//            results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
//            if (results.size() > 0) {
//                int size = results.size();
//                ASSERT.that(results.size() <= 1, () -> "error: more than one CategoryList element (" + size + ")"); //TODO create error log for this 
//                categoryList = results.get(0); //return first element
//                return categoryList;
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//            }
////            else {
////                categoryList = new CategoryList();
////                categoryList.addAll(getAllCategoriesFromParse()); //add any existing categories - only relevant if categoryList was added to app after creating - normally never needed
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
////                saveInBackground((ParseObject) categoryList); //always save so new lists can be assigned to it
////                cachePut(categoryList); //always save so new lists can be assigned to it
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (categoryList != null) {
////            cachePut(categoryList); //may fetchFromCacheOnly by objectId via getOwner
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        CategoryList categoryListTmp = (CategoryList) DAO.getInstance().fetchFromCacheOnly(categoryList.getObjectIdP()); //use same object if already cached
////        if (categoryListTmp != null)
////            categoryList = categoryListTmp;
////        else {
////            fetchListElementsIfNeededReturnCachedIfAvail(categoryList); //get the right elements
////            cachePut(categoryList); //cache list
////        }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        return categoryList;
//    }
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
//        }//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            query.selectKeys(new ArrayList(Arrays.asList(CategoryList.PARSE_CATEGORY_LIST))); //just get search result, no data (these are cached) //NOOO: gets an empty list
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
    public ItemListList getItemListListFromParse() {//boolean forceLoadFromParse) {
//        return getItemListList(false);
//        ItemListList itemListList = null;
//        if ((itemListList = (ItemListList) cacheGet(ItemListList.CLASS_NAME)) != null) {
//            return itemListList;
//        }
        ItemListList itemListListCached = null;

        itemListListCached = (ItemListList) cacheGetNamed(ItemListList.CLASS_NAME);
        if (itemListListCached != null) {// && !forceLoadFromParse) {
            return itemListListCached;
        }
//<editor-fold defaultstate="collapsed" desc="comment">
//        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
//            List<ItemListList> results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
//            if (results.size() > 0) {
//                ASSERT.that(results.size() <= 1, () -> "error: more than one ItemItemList element (" + results.size() + ")");
//                 itemListList = results.get(0); //return first element
////                fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
////                cachePut(itemListList);
////                ItemListList cachedItemListList = (ItemListList) cacheGet(itemListList);
////                if (cachedItemListList != null) {
////                    itemListList = cachedItemListList;
////                } else {
////                fetchListElementsIfNeededReturnCachedIfAvail(categoryList);
//                    cachePut(ItemListList.CLASS_NAME,itemListList); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
//                    fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
////                }
////                return itemListList;
//            } else {
//                ItemListList itemListList2 = new ItemListList();
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false) { //if forceload and no results, mean we need to initialize
////                    itemListList.addAll(getAllItemListsFromParse());
////                    fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
////                }
////</editor-fold>
////                saveAndWait((ParseObject) itemListList); //always save so new lists can be assigned to it
////                cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
////                saveInBackground((ParseObject) itemListList2, () -> cachePut(itemListList2)); //always save so new lists can be assigned to it
////                saveNew((ParseObject) itemListList2, true); //SAVEINBACKGROUND automatically adds to cache //always save so new lists can be assigned to it
//                saveNew((ParseObject) itemListList2); //SAVEINBACKGROUND automatically adds to cache //always save so new lists can be assigned to it
//                saveNewExecuteUpdate();
//                cachePut(itemListList2);
//                return itemListList2;
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        assert false;
////        return itemListList;
//        return null;
//</editor-fold>
        List<ItemListList> results;
        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
//        query.selectKeys(new ArrayList());
        query.whereDoesNotExist(ItemListList.PARSE_DELETED_DATE); //normally never deleted, but could happen in the future
        query.include(ItemListList.PARSE_ITEMS);
        query.selectKeys(Arrays.asList(
                ItemListList.PARSE_ITEMS + "." + ParseObject.GUID,
                ParseObject.GUID,
                ItemListList.PARSE_TEXT
        ));
//         query.excludeKeys(Arrays.asList()); //test to see if this includes all keys despite having the TemplateList.PARSE_ITEMS + "." + ParseObject.GUID

        try {
            results = query.find();
            if (results.size() > 0) {
                if (Config.TEST) {
                    ASSERT.that(results.size() <= 1, () -> "error: more than one TemplateList element (" + results + ")"); //TODO create error log for this 
                }
//                ItemListList itemListListParse = results.get(0); //return first element
                itemListListCached = results.get(0); //return first element
//                if (itemListListCached != null) {
//                    List l = itemListListCached.getListFull();
//                    l.clear();
//                    l.addAll(itemListListParse.getListFull()); //A *HACK* to refresh the existing 
//                }
//                cachePut(ItemListList.CLASS_NAME, itemListListCached); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
//                if (false) {
//                    fetchListElementsIfNeededReturnCachedIfAvail(itemListListCached); //SHOULDN'T be necessary, done by getList(). //get the right elements
//                }
            } else { //initialise the templateList for the first time
                itemListListCached = new ItemListList();
//                saveNew((ParseObject) itemListListCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                saveNewTriggerUpdate();
//                saveToParseNow((ParseObject) itemListListCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
                saveToParseAndWait((ParseObject) itemListListCached); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        cachePut(ItemListList.CLASS_NAME, itemListListCached);
        return itemListListCached;
    }

////<editor-fold defaultstate="collapsed" desc="comment">
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
//    public ItemListList getItemListListXXX(boolean forceLoadFromParse, Date startDate, Date endDate) {
//        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");
//        ItemListList itemListList = null;
//        List<ItemListList> results;// = null;
//        ParseQuery<ItemListList> query = ParseQuery.getQuery(ItemListList.CLASS_NAME);
//        if (!forceLoadFromParse) { //if forceloading, ignore any dates given
//            if (startDate != null) {
//                query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
//            }
//            if (endDate != null) {
//                query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
//            }
//        }
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //don't load deleted objects (shouldn't be relevant for now since cannot delete ItemListList)
//        try {
//            results = query.find();
//            //if no categoryList already saved, initialize it with existing categories
//            if (results.size() > 0) {
//                ASSERT.that(results.size() <= 1, () -> "error: more than one ItemItemList element (" + results.size() + ")");
//                itemListList = results.get(0); //return first element
//                return itemListList;
////                fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements   //NB. DONE below 
//            }
////            cachePut(itemListList); //may fetchFromCacheOnly by objectId via getOwner
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        ItemListList itemListListTmp = (ItemListList) DAO.getInstance().fetchFromCacheOnly(itemListList.getObjectIdP()); //use same object if already cached
//////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
////        if (itemListListTmp != null)
////            itemListList = itemListListTmp;
////        else {
////            fetchListElementsIfNeededReturnCachedIfAvail(itemListList); //get the right elements
////            //        return TemplateList.getInstance();
////            cachePut(itemListList); //cache list
////        }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//        return itemListList;
//    }
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
//    public TemplateList getTemplateList() {
//        return getTemplateList(false, null, null);
//    }
//</editor-fold>
    public TemplateList getTemplateList() {//boolean forceLoadFromParse) {//, Date startDate, Date endDate) {

        TemplateList templateList = null;

        templateList = (TemplateList) cacheGetNamed(TemplateList.CLASS_NAME);
        if (templateList != null) {// && !forceLoadFromParse) {
            return templateList;
        }

        List<TemplateList> results;
        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
//        query.selectKeys(new ArrayList()); //NO: must fetch list of templates!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //normally never deleted, but could happen in the future

        query.include(TemplateList.PARSE_ITEMS);
        query.selectKeys(Arrays.asList(
                TemplateList.PARSE_ITEMS + "." + ParseObject.GUID,
                ParseObject.GUID,
                TemplateList.PARSE_TEXT
        ));
//        query.excludeKeys(Arrays.asList()); //test to see if this includes all keys despite having the TemplateList.PARSE_ITEMS + "." + ParseObject.GUID

        try {
            results = query.find();
            if (results.size() > 0) {
                if (Config.TEST) {
                    ASSERT.that(results.size() <= 1, () -> "error: more than one TemplateList element (" + results + ")"); //TODO create error log for this 
                }
                templateList = results.get(0); //return first element
//                cachePut(TemplateList.CLASS_NAME, templateList); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
//                fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements  
            } else { //initialise the templateList for the first time
                templateList = new TemplateList();
                templateList.setText(Item.TEMPLATE_LIST);
                templateList.setSystemName(Item.TEMPLATE_LIST);
//                saveNew((ParseObject) templateList); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                saveNewTriggerUpdate();
//                saveToParseNow((ParseObject) templateList); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
                saveToParseAndWait((ParseObject) templateList); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
            }
        } catch (ParseException ex) {
            Log.e(ex);
        }
        cachePut(TemplateList.CLASS_NAME, templateList);
        return templateList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public TemplateList getTemplateListOLD() {
//        TemplateList templateList;
//        templateList = (TemplateList) cacheGetNamed(TemplateList.CLASS_NAME);
//        if (templateList != null) {
//            return templateList;
//        }
//
//        List<TemplateList> results = null;
//        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
//        query.selectKeys(new ArrayList());
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //normally never deleted, but could happen in the future
//        try {
//            results = query.find();
//            int s = results.size();
//            if (s > 0) {
//                ASSERT.that(s <= 1, () -> "error: more than one TemplateList element (" + s + ")"); //TODO create error log for this
//                templateList = results.get(0); //return first element
//                TemplateList cachedTemplateList = (TemplateList) cacheGet(templateList);
//                if (cachedTemplateList != null) {
//                    templateList = cachedTemplateList;
//                } else {
//                    cachePut(templateList); //MUST cache first, otherwise the categoryList's elements won't get the right (this categoryList) as owner
//                    fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
//                }
//                return templateList;
//            } else { //if (results.size() == 0)
//                TemplateList templateList2 = new TemplateList();
//                saveNew((ParseObject) templateList2); //always save so new lists can be assigned to it //CANNOT save in background since must have a parseId assigned before caching!!
//                saveNewExecuteUpdate();
//                cachePut(templateList2);
//                return templateList2;
//            }
////            cache.put(TemplateList.CLASS_NAME, templateList.getO);
////            cachePut(TemplateList.CLASS_NAME, templateList);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchFromCacheOnly(templateList.getObjectIdP()); //use same object if already cached
//////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
////        if (templateListTmp != null)
////            templateList = templateListTmp;
////        else {
////            fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
////            //        return TemplateList.getInstance();
////            cachePut(templateList); //cache list
////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        return templateList;
//        assert false;
//        return null;
//    }
//    public TemplateList getTemplateListXXX(boolean forceLoadFromParse, Date startDate, Date endDate) {
//        ASSERT.that(!(forceLoadFromParse && (startDate != null || endDate != null)), "getItemListList called with both forceReload AND date interval");
//        TemplateList templateList = null;
//        List<TemplateList> results = null;
//        ParseQuery<TemplateList> query = ParseQuery.getQuery(TemplateList.CLASS_NAME);
////        setupItemQueryNotTemplateNotDeletedLimit10000(query);
//        query.selectKeys(new ArrayList());
//        if (startDate != null) {
//            query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, startDate);
//        }
//        if (endDate != null) {
//            query.whereLessThan(Item.PARSE_UPDATED_AT, endDate);
//        }
////        if (false) {
////            query.include(TemplateList.PARSE_ITEMLIST_LIST); //NO - fetches an additional copy of the templates
////        }//        query.selectKeys(null); //just get search result, no data (these are cached)
////        if (false) {
////            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached) //NOOO: gets an empty list
////        }
////        query.selectKeys(new ArrayList(Arrays.asList(TemplateList.PARSE_ITEMLIST_LIST))); //just get search result, no data (these are cached) //No need, no superflous data in this list
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        try {
//            results = query.find();
//            int s = results.size();
//            if (s > 0) {
//                ASSERT.that(s <= 1, () -> "error: more than one TemplateList element (" + s + ")"); //TODO create error log for this 
//                templateList = results.get(0); //return first element
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////                if (false) fetchListElementsIfNeededReturnCachedIfAvail(templateList); //replace references to templates with instances from cache //NOT needed since first getList() will do this //optimization?!
////                cache.put(templateList.getObjectId(), templateList); //TODO not really needed?
////                cache.put(TemplateList.CLASS_NAME, templateList);
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
//                return templateList; //return first element
////                fetchListElementsIfNeededReturnCachedIfAvail(templateList);
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchFromCacheOnly(templateList.getObjectIdP()); //use same object if already cached
//////        TemplateList templateListTmp = (TemplateList) DAO.getInstance().fetchItemList(templateList.getObjectIdP());
////        if (templateListTmp != null)
////            templateList = templateListTmp;
////        else {
////            fetchListElementsIfNeededReturnCachedIfAvail(templateList); //get the right elements
////            //        return TemplateList.getInstance();
////            cachePut(templateList); //cache list
////        }
//        return null;
//    }
//    public TimerInstance getTimerInstanceList(boolean forceLoadFromParse) {
////</editor-fold>
//    public List<TimerInstance> getTimerInstanceList() {
////        if (!forceLoadFromParse && (timerStack = (TimerInstance) cacheGet(TemplateList.CLASS_NAME)) != null) {
////            return timerStack;
////        }
//        List<TimerInstance> results = null;
//        ParseQuery<TimerInstance> query = ParseQuery.getQuery(TimerInstance.CLASS_NAME);
////        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
////        query.orderByDescending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
//        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//
//        query.include(TimerInstance.PARSE_LIST);
//        query.include(TimerInstance.PARSE_PROJECT);
//        query.include(TimerInstance.PARSE_TIMED_ITEM);
//        query.selectKeys(Arrays.asList(
//                TimerInstance.PARSE_LIST + "." + ParseObject.GUID,
//                TimerInstance.PARSE_PROJECT + "." + ParseObject.GUID,
//                TimerInstance.PARSE_TIMED_ITEM + "." + ParseObject.GUID,
//                //
//                ParseObject.GUID,
//                //
//                TimerInstance.PARSE_TIMER_ELAPSED_TIME,
//                TimerInstance.PARSE_TIMER_FULL_SCREEN,
//                TimerInstance.PARSE_TIMER_START_TIME,
//                TimerInstance.PARSE_TIMER_TIME_EVEN_INVALID_ITEMS,
//                TimerInstance.PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED,
//                TimerInstance2.PARSE_TIMER_TASK_STATUS,
//                TimerInstance2.PARSE_TIMER_AUTO_NEXT,
//                TimerInstance2.PARSE_TIMER_AUTO_START
//        ));
//
////        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //not used for Timers
//        try {
//            results = query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
    public List<TimerInstance2> getTimerInstanceList2() {
//        if (!forceLoadFromParse && (timerStack = (TimerInstance) cacheGet(TemplateList.CLASS_NAME)) != null) {
//            return timerStack;
//        }
        List<TimerInstance2> results = null;
        ParseQuery<TimerInstance2> query = ParseQuery.getQuery(TimerInstance2.CLASS_NAME);
//        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
//        query.orderByDescending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
        query.orderByAscending(Item.PARSE_CREATED_AT); //assuming TimerInstances are necessarily created in the order they appear (and interrupt previous tiemrs)
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());

        query.include(TimerInstance2.PARSE_LIST);
        query.include(TimerInstance2.PARSE_CATEGORY);
        query.include(TimerInstance2.PARSE_PROJECT);
        query.include(TimerInstance2.PARSE_TIMED_ITEM);
        query.include(TimerInstance2.PARSE_NEXT_TIMED_ITEM);
        query.selectKeys(Arrays.asList(
                TimerInstance2.PARSE_LIST + "." + ParseObject.GUID,
                TimerInstance2.PARSE_CATEGORY + "." + ParseObject.GUID,
                TimerInstance2.PARSE_PROJECT + "." + ParseObject.GUID,
                TimerInstance2.PARSE_TIMED_ITEM + "." + ParseObject.GUID,
                TimerInstance2.PARSE_NEXT_TIMED_ITEM + "." + ParseObject.GUID,
                //
                ParseObject.GUID,
                //
                TimerInstance2.PARSE_TIMER_ELAPSED_TIME,
                TimerInstance2.PARSE_TIMER_FULL_SCREEN,
                TimerInstance2.PARSE_TIMER_START_TIME,
                TimerInstance2.PARSE_TIMER_TIME_EVEN_INVALID_ITEMS,
                TimerInstance2.PARSE_TIMER_WAS_RUNNING_WHEN_INTERRUPTED,
                TimerInstance2.PARSE_TIMER_TASK_STATUS,
                TimerInstance2.PARSE_TIMER_AUTO_NEXT,
                TimerInstance2.PARSE_TIMER_AUTO_START
        ));

//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE); //not used for Timers
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        //make all timerInstances listen to their items and source
        for (TimerInstance2 timerInstance : results) {
            if (timerInstance.getTimerSourceN() != null) {
                timerInstance.getTimerSourceN().addActionListener(timerInstance);
            }
            if (timerInstance.getTimedItemN(true) != null) {
                timerInstance.getTimedItemN(true).addActionListener(timerInstance);
            }
            if (timerInstance.getNextTimedItemN() != null) {
                timerInstance.getNextTimedItemN().addActionListener(timerInstance);
            }
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
    public ItemList getItemListWithSystemNameFromParse(String name) {//, String visibleName) {
//        if (cache.get(name) != null) {
//            return (ItemList) cache.get(cache.get(name)); //cahce: name -> objectIdP
//        } else {
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
//        query.whereEqualTo(ItemList.PARSE_TEXT, name);
//        query.whereDoesNotExist(ItemList.PARSE_OWNER); //only get lists that do not belong to the ItemListList
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.whereExists(ItemList.PARSE_SYSTEM_LIST);
        query.whereEqualTo(ItemList.PARSE_SYSTEM_NAME, name);
        setupItemListQueryWithIndirectAndGuids(query); //ensure we get Filter, guids and everything!
        List<ItemList> results = null;
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
        int s = results.size();
        ASSERT.that(results.size() <= 1, () -> "too many lists (" + s + ") with reserved name: " + name);
        if (s > 0) {
//            results.get(0).setText(visibleName);
//            cache.put(name, results.get(0).getObjectIdP());
            return results.get(0);
        } else {
            return null;
        }
//        }
    }

    /**
     * get
     *
     * @param systemName
     * @param defaultFilter default filter to use if none was stored in Parse
     * @return
     */
    public FilterSortDef getSystemFilterSortFromParse(ScreenType screenType) {
        return getSystemFilterSortFromParse(screenType.getSystemName(), ItemList.getSystemDefaultFilter(screenType));
    }

    /**
     * returns filter with systemName or defaultFilter if systemName is not
     * defined or no filter with that name is found
     *
     * @param systemNameN
     * @param defaultFilter
     * @return
     */
    public FilterSortDef getSystemFilterSortFromParse(String systemNameN, FilterSortDef defaultFilter) {
        if (systemNameN == null || systemNameN.isEmpty()) {
            return defaultFilter;
        } else {
            ParseQuery<FilterSortDef> query = ParseQuery.getQuery(FilterSortDef.CLASS_NAME);
            ASSERT.that(systemNameN != null && !systemNameN.isEmpty());
            query.whereEqualTo(FilterSortDef.PARSE_SYSTEM_NAME, systemNameN);
            List<FilterSortDef> results = null;
            try {
                results = query.find();
            } catch (ParseException ex) {
                Log.e(ex);
            }
            if (Config.TEST) {
                int s = results.size();
                ASSERT.that(s <= 1, () -> "too many filters (" + s + ") with reserved name: " + systemNameN);
            }
            if (results.size() > 0) {
                return (FilterSortDef) fetchIfNeededReturnCachedIfAvail(results.get(0));
            } else {
                FilterSortDef newSystemFilter;
                if (false) {
                    newSystemFilter = FilterSortDef.getDefaultFilter();
                    newSystemFilter.setSystemName(systemNameN);
                } else {
                    newSystemFilter = defaultFilter;
                    ASSERT.that(defaultFilter == null || defaultFilter.getSystemName() == null || defaultFilter.getSystemName().isEmpty() || Objects.equals(defaultFilter.getSystemName(), systemNameN),
                            () -> "changing systemName, from filter.getSystemName()=" + defaultFilter.getSystemName() + "; to systemNane=" + systemNameN);
                    newSystemFilter.setSystemName(systemNameN);
                }
                return newSystemFilter;
            }
        }
    }

    public ItemList getInbox(String systemName, String visibleName) {
//        if (!forceFromParse && (inbox = (Inbox) cacheGet(Inbox.CLASS_NAME)) != null) {
//            return inbox;
//        } //NO need for caching since this is done in the instance/singleton
//*named* lists are cached twice: as "name"->objectId and as usual: objectId->parseObject
        ItemList temp = (ItemList) cacheGetNamed(systemName); //cahce: name -> objectIdP
//        Object cachedObjIdStr = cache.get(name);
//        if (cachedObjIdStr != null) {
//            return (ItemList) cache.get(cachedObjIdStr); //cahce: name -> objectIdP
        if (temp != null) {
            return temp;
        } else {
            ItemList inbox = getItemListWithSystemNameFromParse(systemName);
            if (inbox == null) { //if no Inbox already saved, initialize it with existing motherless tasks
                ItemList newInbox = new ItemList();
//                newInbox.setSystemList(true);
                newInbox.setSystemName(systemName);
                newInbox.setText(visibleName);
                newInbox.setUseDefaultFilter(true);
                List itemsWithoutOwners = getAllItemsWithoutOwners();
                for (Item item : (List<Item>) itemsWithoutOwners) {
                    newInbox.addToList(item);
                }
//<editor-fold defaultstate="collapsed" desc="comment">
//                saveAndWait((ParseObject) inbox); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
//                saveInBackground((ParseObject) newInbox, () -> {
//                    cache.put(name, newInbox.getObjectIdP());
//                    if (false) {
//                        cachePut(newInbox); //may fetchFromCacheOnly by objectId via getOwner
//                    }
//                }); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
//</editor-fold>
//                saveNew((ParseObject) newInbox, true);
//                saveNew((ParseObject) newInbox);
//                saveNewTriggerUpdate();
//                saveToParseNow((ParseObject) newInbox);
                saveToParseAndWait((ParseObject) newInbox);
                cachePut(systemName, newInbox);
//<editor-fold defaultstate="collapsed" desc="comment">
//                if (false) {
//                    saveInBackground(itemsWithoutOwners); //save all items who now have their Inbox owner assigned
//                }//                fetchListElementsIfNeededReturnCachedIfAvail(inbox);
//            saveInBackground((ParseObject)inbox); //always save so new lists can be assigned to it
//            saveAndWait((ParseObject)inbox); //always save so new lists can be assigned to it
//</editor-fold>
                return newInbox;
            } else { //check if list is already cached, use that object instance instead of the one returned by getSpecialNamedItemListFromParse
                Object temp2 = cacheGet(inbox);
                ASSERT.that(temp2 instanceof ItemList, () -> "Inbox already cached but without systemName??!!, inbox=" + temp2);
                ASSERT.that(temp2 instanceof ItemList || temp2 == null, () -> "cached Inbox is NOT an ItemList, inbox=" + temp2);
                if (temp2 instanceof ItemList) {
                    inbox = (ItemList) temp2; //reuse an already cached list
//                    cache.put(name, inbox.getObjectIdP()); //can't be sure it's already cached under name "Inbox"
                    cachePut(systemName, inbox);
                } else {
//                    cache.put(name, inbox.getObjectIdP());
//                    cachePut(inbox); //may fetchFromCacheOnly by objectId via getOwner
                    cachePut(systemName, inbox);
                }
                return inbox;
            }
        }
    }

//    public final static String SYSTEM_LIST_OVERDUE = ScreenType.OVERDUE.name(); //"Overdue";
//    public final static String SYSTEM_LIST_TODAY = ScreenType.TODAY.name(); //"Today";
//    public final static String SYSTEM_LIST_NEXT = ScreenType.NEXT.name(); //"Next";
//    public final static String SYSTEM_LIST_LOG = ScreenType.COMPLETION_LOG.name(); //"Log";
//    public final static String SYSTEM_LIST_DIARY = ScreenType.CREATION_LOG.name(); //"Diary";
//    public final static String SYSTEM_LIST_TOUCHED = ScreenType.TOUCHED.name(); //"Touched";
    public final static String SYSTEM_LIST_OVERDUE = "Overdue";
    public final static String SYSTEM_LIST_TODAY = "Today";
    public final static String SYSTEM_LIST_NEXT = "Next";
    public final static String SYSTEM_LIST_LOG = "Log";
    public final static String SYSTEM_LIST_DIARY = "Diary";
    public final static String SYSTEM_LIST_TOUCHED = "Touched";
    public final static String SYSTEM_LIST_ALL = "All";
    public final static String SYSTEM_LIST_PROJECTS = "Projects";
    public final static String SYSTEM_LIST_TEMPLATES = "Templates";
    public final static String SYSTEM_LIST_WORKSLOTS = "Workslots";
//                      CategoryList.CLASS_NAME);
//            } else if (parseObject instanceof ItemListList) {
//                cache.delete(ItemListList.CLASS_NAME);
//            } else if (parseObject instanceof TemplateList) {
//                cache.delete(TemplateList.CLASS_NAME);
//            } else if (parseObject instanceof Inbox) {
//                cache.delete(Inbox.CLASS_NAME
//    private final static String[] RESERVED_LIST_NAMES = {OVERDUE, TODAY, NEXT, LOG, DIARY, TOUCHED,
//        CategoryList.CLASS_NAME, ItemListList.CLASS_NAME, TemplateList.CLASS_NAME, Inbox.CLASS_NAME};//new ArrayList
//    private final static String[] RESERVED_LIST_NAMES = {OVERDUE., TODAY., NEXT, LOG, DIARY, TOUCHED,
//        CategoryList.CLASS_NAME, ItemListList.CLASS_NAME, TemplateList.CLASS_NAME, Inbox.CLASS_NAME};//new ArrayList
//<editor-fold defaultstate="collapsed" desc="comment">
//    private final static HashMap<String, ItemList> namedLists = new HashMap<>();

//    public ItemList getNamedItemList(String name) {
//        return getNamedItemList(name, name); //use fixed name as default
//    }
//
//    public ItemList getNamedItemList(String name, String visibleName) {
//        return getNamedItemList(name, visibleName, null);
//    }
//</editor-fold>
    /**
     * named lists need to be saved since Timers can refer to them, but since
     * they are dynamic, they also need to be refreshed from parse each time
     * fetched
     *
     * @param name
     * @param visibleName
     * @param defaultFilterSortDef
     * @return
     */
    public ItemList fetchDynamicNamedItemList(String name, String visibleName, FilterSortDef defaultFilterSortDef, boolean forceReloadFromParse) {
//<editor-fold defaultstate="collapsed" desc="comment">
//        ItemList temp = (ItemList) cacheGetNamed(name); //cahce: name -> objectIdP
//        if (temp == null) {
//            //if not cached, try to get from Parse:
//            temp = getSpecialNamedItemListFromParse(name); //fetch from parse
//            if (temp == null) {
//                //if not on Parse, create a new - this will only be done ONCE for every user for each list
//                temp = new ItemList();
//                temp.setText(visibleName);
//                temp.setSystemName(name);
//                temp.setFilterSortDef(filterSortDef);
////                saveNew(true, filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//                saveNew(filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//                saveNewExecuteUpdate();
////                cache.put(name, temp);
////                cache.put(name, temp.getObjectIdP());
//                cachePut(name, temp);
//            } else { //if list is already cached, use that object instance instead of the one returned by getSpecialNamedItemListFromParse
//                Object temp2 = cacheGet(temp);
//                ASSERT.that(temp2 instanceof ItemList, "cached named list is NOT an ItemList, temp2=" + temp2);
//                if (temp2 instanceof ItemList) {
//                    temp = (ItemList) temp2; //reuse an already cached list
////                    cache.put(name, temp.getObjectIdP());
//                    cachePut(name, temp);
//                }
//            }
//        }
//</editor-fold>
//        List<ItemAndListCommonInterface> updatedList = null;
        List updatedList = null;
        if (forceReloadFromParse || !MyPrefs.cacheDynamicLists.getBoolean()) {
            switch (name) {
                case SYSTEM_LIST_OVERDUE:
                    updatedList = getOverdue();
                    break;
                case SYSTEM_LIST_TODAY:
                    updatedList = getToday();
                    break; //unreachable statement!!
                case SYSTEM_LIST_NEXT:
                    updatedList = getNext();
                    break; //unreachable statement!!
                case SYSTEM_LIST_LOG:
                    updatedList = getCompletionLog();
                    break;
                case SYSTEM_LIST_DIARY:
                    updatedList = getCreationLog();
                    break;
                case SYSTEM_LIST_TOUCHED:
                    updatedList = getTouchedLog();
                    break;
                case SYSTEM_LIST_ALL:
                    updatedList = getAllItems();
                    break;
                case SYSTEM_LIST_PROJECTS:
                    updatedList = getAllProjects();
                    break;
                default:
                    ASSERT.that(false, "unhandled case in DAO.fetchDynamicNamedItemList, name=" + name + ";visibleName=" + visibleName);
            }
        }

        ItemList tempItemList;
//        if (true || MyPrefs.cacheDynamicLists.getBoolean()) {
        tempItemList = (ItemList) cacheGetNamed(name); //cahce: name -> objectIdP
        if (tempItemList != null) {
            tempItemList.updateTo(updatedList);
            tempItemList.setFilterSortDef(defaultFilterSortDef); //hack: override whatever earlier default filter was referenced, but not saved(
//            cachePut(defaultFilterSortDef);
            cachePut(name, tempItemList);
        } else {
//            temp = new ItemList(updatedList);
            tempItemList = new ItemList(updatedList);
            tempItemList.setText(visibleName);
            tempItemList.setSystemName(name);
            tempItemList.setFilterSortDef(defaultFilterSortDef);
            tempItemList.setNoSave(true); //don't save dynamic lists
            if (SYSTEM_LIST_LOG.equals(name)) {
                tempItemList.setShowNumberDoneTasks(true);
                tempItemList.setShowNumberUndoneTasks(false);
                tempItemList.setShowActual(true);
            }
//            saveToParseAndWait(defaultFilterSortDef, tempItemList); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
            if (false) {
                saveToParseAndWait(tempItemList); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
            }
            cachePut(defaultFilterSortDef);
            cachePut(name, tempItemList);
        }
//        } else {
//            tempItemList = new ItemList(updatedList);
//            tempItemList.setText(visibleName);
//            tempItemList.setSystemName(name);
//            tempItemList.setFilterSortDef(defaultFilterSortDef);
//            tempItemList.setNoSave(true); //don't save dynamic lists
//        }
//        saveNew(filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//        saveNewTriggerUpdate();
//        saveToParseNow(filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//        saveToParseAndWait(filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//        cachePut(name, temp);

        return tempItemList;
    }

//    public ItemList getNamedItemList(String name) {
//        switch (name) {
//            case SYSTEM_LIST_OVERDUE:
//                getNamedItemList(name, OVERDUE.getTitle(),
//                        getSystemFilterSortFromParse(OVERDUE.name(), ItemList.getSystemDefaultFilter(OVERDUE)));
//            case SYSTEM_LIST_TODAY:
//                getNamedItemList(SYSTEM_LIST_TODAY, TODAY.getTitle(), null);
//            case SYSTEM_LIST_NEXT:
//                getNamedItemList(SYSTEM_LIST_NEXT, NEXT.getTitle(),
//                        DAO.getInstance().getSystemFilterSortFromParse(NEXT.name(), ItemList.getSystemDefaultFilter(NEXT)));
//        }
//        return null;
//    }
    public ItemList getSystemListXXX(ScreenType screenType) {
        return fetchDynamicNamedItemList(screenType.getSystemName(), screenType.getTitle(), ItemList.getSystemDefaultFilter(screenType), screenType.isFilterEditable());
    }

    public ItemList getNamedItemList(String name, String visibleName, FilterSortDef defaultFilterSortDef) {
        return fetchDynamicNamedItemList(name, visibleName, defaultFilterSortDef, true);
    }

    public ItemList getNamedItemList(ScreenType screenType) {
        return getNamedItemList(screenType, false);
    }

    public ItemList getNamedItemList(ScreenType screenType, boolean fetchFilterSort) {
        ItemList itemList = fetchDynamicNamedItemList(screenType.getSystemName(), screenType.getTitle(),
                fetchFilterSort
                        ? getSystemFilterSortFromParse(screenType.getSystemName(), ItemList.getSystemDefaultFilter(screenType))
                        : ItemList.getSystemDefaultFilter(screenType),
                //                true);
                screenType.isFilterEditable());
        itemList.setItemListIcon(screenType.getIcon());
        itemList.setItemListIconFont(screenType.getFont());
        return itemList;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getNamedItemListOLD(String name, String visibleName, FilterSortDef filterSortDef) {
////<editor-fold defaultstate="collapsed" desc="comment">
////        ItemList temp = null;
////        String objId;
////        if (filterSortDef != null) {
////            ((ItemList) temp).setFilterSortDef(filterSortDef);
////            saveInBackground(temp);
////        }
////        if (false && (temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) { //temp.getUpdatedAt() == null: in case it hasn't been saved to Parse server yet
//////           //temp is cached and updated today
////            return (ItemList) temp;
////        } else {
//        // !((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) <=>
//        // ((temp = cacheGet(name)) == null || !(temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) <=>
//        // ((temp = cacheGet(name)) == null || (temp.getUpdatedAt() != null && !MyDate.isToday(temp.getUpdatedAt())))
////            List<ItemAndListCommonInterface> updatedList;
////        ItemList temp = namedLists.get(name); //ensure lists a
////        ItemList temp = (ItemList) cacheGet(name); //ensure lists are reused
////        Object cachedObjIdStr = cache.get(name);
////        ItemList temp = null;
////        if (cachedObjIdStr != null) {
////            temp = (ItemList) cache.get(cachedObjIdStr); //cahce: name -> objectIdP
////        }
////</editor-fold>
//        ItemList temp = (ItemList) cacheGetNamed(name); //cahce: name -> objectIdP
//        if (temp == null) {
//            //if not cached, try to get from Parse:
//            temp = getSpecialNamedItemListFromParse(name); //fetch from parse
//            if (temp == null) {
//                //if not on Parse, create a new - this will only be done ONCE for every user for each list
//                temp = new ItemList();
//                temp.setText(visibleName);
//                temp.setSystemName(name);
//                temp.setFilterSortDef(filterSortDef);
////                saveNew(true, filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//                saveNew(filterSortDef, temp); //we only save it this once to have a parseId, never later since we'll always fetch the actual list dynamically
//                saveNewExecuteUpdate();
////                cache.put(name, temp);
////                cache.put(name, temp.getObjectIdP());
//                cachePut(name, temp);
//            } else { //if list is already cached, use that object instance instead of the one returned by getSpecialNamedItemListFromParse
//                Object temp2 = cacheGet(temp);
//                ASSERT.that(temp2 instanceof ItemList, "cached named list is NOT an ItemList, temp2=" + temp2);
//                if (temp2 instanceof ItemList) {
//                    temp = (ItemList) temp2; //reuse an already cached list
////                    cache.put(name, temp.getObjectIdP());
//                    cachePut(name, temp);
//                }
//            }
//        }
//
//        List<ItemAndListCommonInterface> updatedList = null;
//        switch (name) {
//            case OVERDUE:
//                updatedList = getOverdue();
////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
////////                    return (ItemList) temp;
//////                    } else {
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getOverdue()); //refresh the list (important to just update existing list so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getOverdue());
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                        if (filterSortDef != null) {
//////                            saveInBackground(filterSortDef);
//////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
//////                        }
//////                        saveAndWait(temp); //NB. MUST be saveAndWait so an pbjectId is creating before caching based on on name
//////                        cache.put(name, temp);
//////                    return (ItemList) temp;
//////</editor-fold>
////                    }
////                    }
////</editor-fold>
//                break;
//            case TODAY:
//                updatedList = getToday();
////<editor-fold defaultstate="collapsed" desc="comment">
//////                if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt())))
//////                    if ((temp = cacheGet(name)) != null && (MyDate.isToday(temp.getUpdatedAt()))) {
////////                    return (ItemList) temp;
//////                    } else {
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getToday()); //refresh the list (important so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getToday());
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                        if (filterSortDef != null) {
//////                            saveInBackground(filterSortDef);
//////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
//////                        }
////////                    saveInBackground(temp);
//////                        //optimization: does this in background:
//////                        saveAndWait(temp); //NB! MUST use saveAndWait, so the list is stored in Cache with an updatedAt date to make the check above work correctly!!
//////                        cache.put(name, temp);
//////                    return (ItemList) temp;
//////</editor-fold>
////                    }
//////                    }
////</editor-fold>
//                break; //unreachable statement!!
//            case NEXT:
//                updatedList = getCalendar();
////<editor-fold defaultstate="collapsed" desc="comment">
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt())))
//////                    return (ItemList) temp;
//////                else
//////                    temp = new ItemList(visibleName, getCalendar());
//////                if (temp == null) {
//////                    temp = new ItemList(visibleName, false);
//////                    saveInBackground(temp);
//////                    cache.put(name, temp);
//////                } else {
//////                    cache.put(name, temp);
//////                }
//////                return (ItemList) temp;
//////
//////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
////////                    return (ItemList) temp;
//////                    } else {
//////</editor-fold>
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getCalendar()); //refresh the list (important so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getCalendar());
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                        if (filterSortDef != null) {
//////                            saveInBackground(filterSortDef);
//////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
//////                        }
//////                        saveAndWait(temp);
//////                        cache.put(name, temp);
////////                    return (ItemList) temp;
//////</editor-fold>
////                    }
//////                    }
////</editor-fold>
//                break; //unreachable statement!!
//            case LOG:
////                updatedList = getCompletionLog(temp);
//                updatedList = getCompletionLog();
////<editor-fold defaultstate="collapsed" desc="comment">
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
////////                    return (ItemList) temp;
//////                    } else {
//////</editor-fold>
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getCompletionLog()); //refresh the list (important so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getCompletionLog());
//////<editor-fold defaultstate="collapsed" desc="comment">
//////                        if (filterSortDef != null) {
//////                            saveInBackground(filterSortDef);
//////                            ((ItemList) temp).setFilterSortDef(filterSortDef);
//////                        }
//////                        saveAndWait(temp);
//////                        cache.put(name, temp);
//////</editor-fold>
////                    }
////                    }
////</editor-fold>
//                break;
//            case DIARY:
//                updatedList = getCreationLog();
////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
//////                    } else {
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getCreationLog()); //refresh the list (important so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getCreationLog());
////                        saveAndWait(temp);
////                        cache.put(name, temp);
////                    }
//////                    }
////</editor-fold>
//                break;
//            case TOUCHED:
//                updatedList = getTouchedLog();
////<editor-fold defaultstate="collapsed" desc="comment">
//////                    if ((temp = cacheGet(name)) != null && (temp.getUpdatedAt() == null || MyDate.isToday(temp.getUpdatedAt()))) {
//////                    } else {
////                    if (temp != null) {
////                        ((ItemAndListCommonInterface) temp).setList(getTouchedLog()); //refresh the list (important so eg Timer continues on same list!!)
////                        saveInBackground(temp);
////                    } else {
////                        temp = new ItemList(visibleName, getTouchedLog());
////                        saveAndWait(temp);
////                        cache.put(name, temp);
////                    }
//////                    }
////</editor-fold>
//                break;
//        }
//        return new ItemList(updatedList);
////<editor-fold defaultstate="collapsed" desc="comment">
////            if (temp.getObjectIdP() == null) { //new created list
////            if (temp == null) { //create new list (only done on first use or after refreshing cache)
//////                temp = new ItemList(visibleName, getTouchedLog((ItemList) temp));
////                temp = new ItemList(visibleName, updatedList);
////                ((ItemList) temp).setSystemList(true);
////                ((ItemList) temp).setList(updatedList);
////                if (filterSortDef != null) {
////                    saveInBackground(filterSortDef);
////                    ((ItemList) temp).setFilterSortDef(filterSortDef);
////                }
//////                saveAndWait(temp); //NB. MUST be saveAndWait so an objectId is created before caching based on name
//////                cache.put(name, temp);
//////                ParseObject lambdaCopy = temp;
//////                saveInBackground(temp, () -> cache.put(name, lambdaCopy)); //NB. MUST be saveAndWait so an objectId is created before caching based on name
////                if (false) saveInBackground(temp); //NB. MUST be saveAndWait so an objectId is created before caching based on name -> now done in backgroundsavethread after call to saveInBackground
//////                cache.put(name, temp);
////            } else {
////                ((ItemAndListCommonInterface) temp).setList(updatedList); //refresh the list (important to just update existing list so eg Timer continues on same list!!)
////                if (false) saveInBackground(temp);
////            }
////
//////            if (temp != null) {
//////                return (ItemList) temp;
//////            } else {
//////                ASSERT.that("error: unknown type of named list,name=" + name + ", visibleName=" + visibleName);
//////                return null;
//////            }
////            if (temp == null) {
////                ASSERT.that("error: unknown type of named list,name=" + name + ", visibleName=" + visibleName);
////            }
////            return (ItemList) temp;
//////        }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////        if (false) {
////            ItemList inbox = getSpecialNamedItemListFromParse(name, visibleName);
////            if (inbox == null) {
////                //if no Inbox already saved, initialize it with existing categories
////                inbox = new ItemList();
////                inbox.setText(name);
////                List itemWithoutOwners = getAllItemsWithoutOwners();
////                for (Item item : (List<Item>) itemWithoutOwners) {
////                    inbox.addToList(item);
////                }
////                saveAndWait((ParseObject) inbox); //save first to set ObjectId (for when adding tasks in for loop, saveAndWait to ensure an objectId is assigned before caching below)
////                saveInBackground(itemWithoutOwners); //save all items who now have their Inbox owner assigned
//////                fetchListElementsIfNeededReturnCachedIfAvail(inbox);
//////            saveInBackground((ParseObject)inbox); //always save so new lists can be assigned to it
//////            saveAndWait((ParseObject)inbox); //always save so new lists can be assigned to it
////            }
////            cachePut(inbox); //may fetchFromCacheOnly by objectId via getOwner
////            return inbox;
////        }
////</editor-fold>
//    }
//</editor-fold>
//    private final static String ALL_TEMPLATES_KEY = "ALL_TEMPLATES_KEY";
    public List<Item> getTopLevelTemplatesFromParse() {
        List<Item> results = null;
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);
        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //only top-level templates
        query.whereExists(Item.PARSE_TEMPLATE);
//<editor-fold defaultstate="collapsed" desc="comment">
//        query.whereExists(Item.PARSE_TEMPLATE); //fetch only templates
//        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all subtasks
//        query.orderByDescending(Item.PARSE_TEXT); //order alphabetically
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        setupItemQueryNoTemplatesLimit1000(query);
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//</editor-fold>
        try {
            results = query.find();
//            cacheList(results);
//            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return fetchListElementsIfNeededReturnCachedIfAvail(results);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private final static String ALL_TEMPLATES_KEY = "ALL_TEMPLATES_KEY";
//    public List<Item> getAllTemplateTasksFromParseXXX() {
//        List<Item> results = null;
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //only top-level templates
//        query.whereExists(Item.PARSE_TEMPLATE);
//
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        try {
//            results = query.find();
////            cacheList(results);
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return new ArrayList();
//    }
//</editor-fold>
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
//    public void getAllItemsInCategory(Category category) {
//        try {
//            category.fetchIfNeeded();
//            List<Item> list = category.getListFull();
//            //TODO!!! find more efficient way to fetchFromCacheOnly all the objects
//            for (int i = 0, size = list.size(); i < size; i++) {
//                list.set(i, (Item) fetchIfNeededReturnCachedIfAvail(list.get(i))); //NB! will possibly replace the parseObjects in the list with cached ones
////            for (Object item : category.getList()) {
////                ((Item) item).fetchIfNeeded();
//            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//    }
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
//        if (false) {
//            query.selectKeys(new ArrayList()); //XXXXjust get search result, no data (these are cached)
//        }
//query.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE))); 
        query.selectKeys(Arrays.asList(ParseObject.GUID));

        if (excludeDoneAndCancelled) {
            query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
        }
    }

    private List<Item> cachedAllTasks;

    /**
     * gets all Items but no templates
     *
     * @return
     */
    public List<Item> getAllItems() {
        if (cachedAllTasks == null) {
            cachedAllTasks = getAllItems(false);
        }
        return cachedAllTasks;
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
        return getAllItems(includeTemplates, onlyLeafTasks, onlyLeafTasks, onlyWithoutOwner, fetchFromScratch);
    }

    public List<Item> getAllItems(boolean includeTemplates, boolean onlyLeafTasks, boolean onlyTopLevelProjects, boolean onlyWithoutOwner, boolean fetchFromScratch) {

        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        if (!includeTemplates) {
            query.whereDoesNotExist(Item.PARSE_TEMPLATE); //exclude if has subtasks
        }
        if (onlyTopLevelProjects) {
            if (Config.TEST) {
                ASSERT.that(!onlyLeafTasks, "incompatible options, onlyTopLevelProjects==true; onlyLeafTasks==true");
            }
            onlyLeafTasks = false;
            query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude subtasks (==owned by an Item)
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
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        query.orderByDescending(Item.PARSE_UPDATED_AT);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.selectKeys(Arrays.asList(ParseObject.GUID)); //just get search result, no data (these are cached)
        setupItemQueryWithIndirectAndGuids(query);
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
        setupItemQueryNotTemplateNotDeletedLimit10000(query, true);
        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM);
        query.whereDoesNotExist(Item.PARSE_OWNER_LIST);
        query.whereDoesNotExist(Item.PARSE_OWNER_TEMPLATE_LIST);
//        query.whereDoesNotExist(Item.PARSE_TEMPLATE);
        query.orderByDescending(Item.PARSE_UPDATED_AT);
//        query.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
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

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> getAllItemsForRepeatRule(RepeatRuleParseObject repeatRule) {
//        if (repeatRule != null && repeatRule.getObjectIdP() != null) {
//            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//            setupItemQueryNotTemplateNotDeletedLimit10000(query);
////            setupItemQueryNoTemplatesLimit1000(query);
////            query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////            query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
////            query.whereEqualTo(Item.PARSE_REPEAT_RULE, repeatRule.getObjectId());
//            query.whereEqualTo(Item.PARSE_REPEAT_RULE, repeatRule);
//            query.orderByDescending(Item.PARSE_DUE_DATE);
////            query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
////            query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//            List<Item> results = null;
//            try {
//                results = query.find();
//                fetchListElementsIfNeededReturnCachedIfAvail(results);
//                return results;
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
//        return new ArrayList();
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * gets all top-level templates
     *
     * @return
     */
//    private List<Item> getAllTemplatesByQueryXXX() {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        setupItemQuery(query2);
//        if (false) {
//            query.include(Item.PARSE_SUBTASKS);
//            query.include(Item.PARSE_CATEGORIES);
//            query.include(Item.PARSE_OWNER_ITEM); //ensure we fetchFromCacheOnly the owner (eg for drag & drop)
//        }
////        query2.include(Item.PARSE_OWNER_LIST); //ensure we fetchFromCacheOnly the ownerList (eg for drag & drop)
//        query.whereExists(Item.PARSE_TEMPLATE); //fetch only templates
//        query.whereDoesNotExist(Item.PARSE_OWNER_ITEM); //exclude all template subtasks
//        query.orderByDescending(Item.PARSE_TEXT); //order alphabetically
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//        List<Item> results = null;
//        try {
//            results = query.find();
////<editor-fold defaultstate="collapsed" desc="comment">
////            for (ParseObject o : results) {
////            for (int i = 0, size = results.size(); i < size; i++) {
////                Item item = results.get(i);
////                Item temp;
//////                if (item.isDataAvailable()) { //WILL always be the case for the items
//////                    cache.put(item.getObjectId(), item);
//////                } else
////                if ((temp = (Item) cache.get(item.getObjectId())) != null) {
////                    results.set(i, temp);
////                } else {
//////                    item = (Item) fetchIfNeeded(item);
////                    fetchIfNeeded(item);
//////                    item = (Item) fetchIfNeededReturnCachedIfAvail(item);
//////                    cache.put(item.getObjectId(), item); //cached in fetchedIfNeeded()
////                }
////            }
////</editor-fold>
//            fetchListElementsIfNeededReturnCachedIfAvail(results);
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
//</editor-fold>
    private void sortAllProjects(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getUpdatedAt(), i2.getUpdatedAt())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedAllProjects;
//    private Date cachedAllProjectsExpiry; //project list is not date dependent so expiry date doesn't make sense

    /**
     * only returns top-level projects (not subprojects)
     *
     * @return
     */
    public List<Item> getAllProjects() {
        return getAllProjects(false);
    }

    public List<Item> getAllProjects(boolean includeSubProjects) {

        if (cachedAllProjects != null) {
            return cachedAllProjects;
        } else {
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
//        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //exclude Done if has subtaskss
//        query.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //include if has subtaskss
//        query.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates

            query.orderByDescending(Item.PARSE_UPDATED_AT);
            setupItemQueryWithIndirectAndGuids(query);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
            List<Item> results = null;
            try {
                results = query.find();
//            for (ParseObject o : results) {
//                if (o.isDataAvailable()) {
//                    cache.put(o.getObjectId(), o);
//                }
//            }
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortAllProjects(results);
                cachedAllProjects = results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return results;
//        return (List<Item>) getAll(Item.CLASS_NAME);
        }
    }

    private void checkAndRefreshAllProjects(Item element, boolean delete) {
        if (cachedAllProjects != null) {
            if (delete) {
                cachedAllProjects.remove(element);
            } else if (!cachedAllProjects.contains(element)) {
                if (element.isProject()) {
                    cachedAllProjects.add(element);
                    sortAllProjects(cachedAllProjects);
                }
            } else if (!element.isProject()) {
                cachedAllProjects.remove(element);
            }
        }
    }

    private Date getCompletionLogStartDate() {
        return new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortCompletionLog(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getUpdatedAt(), i2.getUpdatedAt())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedCompletionLog;
    private Date cachedExpiryCompletionLog;

    /**
     * returns completed tasks by date completed
     *
     * @return
     */
    public List<Item> getCompletionLog() {
        if (cachedCompletionLog != null && cachedExpiryCompletionLog != null && System.currentTimeMillis() <= cachedExpiryCompletionLog.getTime()) {
            return cachedCompletionLog;
        } else {

            //get the start of the day creationLogInterval days back in time
//        Date firstDate = new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
            Date firstDate = getCompletionLogStartDate();

            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query, false);
            //Projects are defined as containing subtasks and not being owner by another Item
            query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
            if (false) {
                query.orderByDescending(Item.PARSE_COMPLETED_DATE);
            }
            query.whereGreaterThanOrEqualTo(Item.PARSE_COMPLETED_DATE, firstDate);
//        query.whereLessThanOrEqualTo(Item.PARSE_COMPLETED_DATE, new MyDate()); //not really necessary
            setupItemQueryWithIndirectAndGuids(query);

            List results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortCompletionLog(results);
                cachedCompletionLog = results;
                cachedExpiryCompletionLog = MyDate.getEndOfToday(); //valid until midnight
                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return null;
        }
    }

    private void checkAndRefreshCompletionLog(Item element, boolean delete) {
        if (cachedCompletionLog != null) {
            if (delete) {
                cachedCompletedLog.remove(element);
            } else {
                Date firstDate = getCompletionLogStartDate();
                if (!cachedCompletionLog.contains(element)) {
                    if (element.getCompletedDate().getTime() >= firstDate.getTime()) {
                        cachedCompletionLog.add(element);
                        sortTouchedLog(cachedCompletionLog);
                    }
                } else if (element.getCompletedDate().getTime() < firstDate.getTime()) {
                    cachedCompletionLog.remove(element);
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList XXX(ItemList existingListToUpdate) {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query, false);
//
//        //Projects are defined as containing subtasks and not being owner by another Item
//        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
//
//        query.orderByDescending(Item.PARSE_COMPLETED_DATE);
//        query.whereGreaterThanOrEqualTo(Item.PARSE_COMPLETED_DATE, new Date(MyDate.currentTimeMillis() - MyPrefs.completionLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
////        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//
//        List<Item> results = null;
//        try {
//            results = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(results);
////            if (existingListToUpdate instanceof ItemList) {
//            //if we already have an existing list (that eg the timer may be running on), then simply update it
//            existingListToUpdate.clear();
//            existingListToUpdate.addAllNotMakingOwner(results);
//            return existingListToUpdate;
////            } else {
////                return new ItemList(results);
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null; //new ItemList();
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    private Date getCompletedLogStartDate() {
        return new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.editedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortCompletedLog(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getEditedDate(), i2.getEditedDate())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedCompletedLog;
    private Date cachedExpiryCompletedLog;
    private Date cachedCompletedLogStartDate;
    private Date cachedCompletedLogEndDate;

    /**
     * used for Statistics, returns completed tasks from and including startDate
     * up to and including endDate
     *
     * @return
     */
    public List<Item> getCompletedItems(Date startDate, Date endDate, boolean onlyLeafTasks) {
        if (cachedCompletedLog != null && cachedExpiryCompletedLog != null
                && System.currentTimeMillis() <= cachedExpiryCompletedLog.getTime()
                && startDate.equals(cachedCompletedLogStartDate) && endDate.equals(cachedCompletedLogEndDate)) {
            return cachedCompletedLog;
        } else {

            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query, false);

            //Projects are defined as containing subtasks and not being owner by another Item
            query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss

//        query.orderByDescending(Item.PARSE_COMPLETED_DATE);
//        query.orderByAscending(Item.PARSE_COMPLETED_DATE); //don't request sorted, do that when showing
            query.whereGreaterThanOrEqualTo(Item.PARSE_COMPLETED_DATE, startDate);
            query.whereLessThanOrEqualTo(Item.PARSE_COMPLETED_DATE, endDate);
            if (onlyLeafTasks) {
                query.whereDoesNotExist(Item.PARSE_SUBTASKS);
            }
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

            List<Item> results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortCompletedLog(results);
                cachedCompletedLog = results;
                cachedExpiryCompletedLog = MyDate.getEndOfToday(); //valid until midnight
                cachedCompletedLogStartDate = startDate;
                cachedCompletedLogEndDate = endDate;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return results;
        }
    }

    private void checkAndRefreshCompletedLog(Item element, boolean delete) {
        if (cachedCompletedLog != null) {
            if (delete) {
                cachedCompletedLog.remove(element);
            } else {
                Date firstDate = getCompletedLogStartDate();
                if (!cachedCompletedLog.contains(element)) {
                    if (element.getCompletedDate().getTime() >= cachedCompletedLogStartDate.getTime()
                            && element.getCompletedDate().getTime() <= cachedCompletedLogEndDate.getTime()) {
                        cachedCompletedLog.add(element);
                        sortCompletedLog(cachedCompletedLog);
                    }
                } else if (element.getCompletedDate().getTime() < cachedCompletedLogStartDate.getTime()
                        || element.getCompletedDate().getTime() > cachedCompletedLogEndDate.getTime()) {
                    cachedCompletedLog.remove(element); //completed date changed to outside this interval
                }
            }
        }
    }

    /**
     * return all soft-deleted tasks
     *
     * @return
     */
    public List<Item> getDeletedItemsZZZ() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereExists(Item.PARSE_DELETED_DATE); //don't fetchFromCacheOnly any deleted items
//        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//        query.selectKeys(Arrays.asList(ParseObject.GUID)); //DO get all data since should not be cached!

        query.orderByAscending(Item.PARSE_UPDATED_AT);

        List<Item> results = null;
        try {
            results = query.find();
            fetchListElementsIfNeededReturnCachedIfAvail(results);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return results;
    }

    private Date getTouchedLogStartDate() {
        return new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.touchedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortTouchedLog(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getUpdatedAt(), i2.getUpdatedAt())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedTouchedLog;
    private Date cachedExpiryTouchedLog;

    /**
     * returns completed tasks by date modified
     *
     * @return
     */
    public List<Item> getTouchedLog() {
        if (cachedTouchedLog != null && cachedExpiryTouchedLog != null && System.currentTimeMillis() <= cachedExpiryTouchedLog.getTime()) {
            return cachedTouchedLog;
        } else {
            //get the start of the day touchedLogInterval days back in time
//        Date firstDate = new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.touchedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
            Date firstDate = getTouchedLogStartDate();

            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query);

            query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, firstDate);
            if (false) {
                query.orderByDescending(Item.PARSE_UPDATED_AT);
            }
            setupItemQueryWithIndirectAndGuids(query);

            List results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortTouchedLog(results);
                cachedTouchedLog = results;
                cachedExpiryTouchedLog = MyDate.getEndOfToday(); //valid until midnight

                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return null;
        }
    }

    private void checkAndRefreshTouchedLog(Item element, boolean delete) {
        if (cachedTouchedLog != null) {
            if (delete) {
                cachedTouchedLog.remove(element);
            } else {
                Date firstDate = getTouchedLogStartDate();
                if (!cachedTouchedLog.contains(element)) {
                    if ((element.getUpdatedAt() == null || element.getUpdatedAt().getTime() >= firstDate.getTime())) {
                        cachedTouchedLog.add(element);
                        sortTouchedLog(cachedTouchedLog);
                    }
                } else if (element.getUpdatedAt() != null && element.getUpdatedAt().getTime() < firstDate.getTime()) {
                    cachedTouchedLog.remove(element);
                }
            }
        }
    }

    private Date getEditedLogStartDate() {
        return new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.editedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortEditedLog(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getEditedDate(), i2.getEditedDate())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedEditedLog;
    private Date cachedExpiryEditedLog;

    /**
     * returns completed tasks by date modified
     *
     * @return
     */
    public List<Item> getEditedLog() {
        if (cachedEditedLog != null && cachedExpiryEditedLog != null && System.currentTimeMillis() <= cachedExpiryEditedLog.getTime()) {
            return cachedEditedLog;
        } else {

            //get the start of the day touchedLogInterval days back in time
//        Date firstDate = new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.touchedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
            Date firstDate = getEditedLogStartDate();

            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query);

            query.whereGreaterThanOrEqualTo(Item.PARSE_EDITED_DATE, firstDate);
            if (false) {
                query.orderByDescending(Item.PARSE_EDITED_DATE);
            }

            List results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortEditedLog(results);
                cachedEditedLog = results;
                cachedExpiryEditedLog = MyDate.getEndOfToday(); //valid until midnight

                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return null;
        }
    }

    private void checkAndRefreshEditedLog(Item element, boolean delete) {
        if (cachedEditedLog != null) {
            if (delete) {
                cachedEditedLog.remove(element);
            } else {
                Date firstDate = getEditedLogStartDate();
                if (!cachedEditedLog.contains(element) && (element.getEditedDate() == null || element.getEditedDate().getTime() >= firstDate.getTime())) {
                    cachedEditedLog.add(element);
                    sortEditedLog(cachedEditedLog);
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getTouchedLogXXX(ItemList existingListToUpdate) {
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        query2.include(Item.PARSE_TEXT);
////        query2.include(Item.PARSE_SUBTASKS);
////        query2.include(Item.PARSE_CATEGORIES);
////        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
//        setupItemQueryNotTemplateNotDeletedLimit10000(query);
//
//        //Projects are defined as containing subtasks and not being owner by another Item
////        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
//        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, new Date(MyDate.currentTimeMillis() - MyPrefs.touchedLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
////        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, MyDate.getStartOfToday()); //NOT needed since cannot be touched in future
//        query.orderByDescending(Item.PARSE_UPDATED_AT);
////        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//
//        List<Item> results = null;
//        try {
//            results = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(results);
////            if (existingListToUpdate instanceof ItemList) {
//            //if we already have an existing list (that eg the timer may be running on), then simply update it
//            existingListToUpdate.clear();
//            existingListToUpdate.addAllNotMakingOwner(results);
//            return existingListToUpdate;
////            } else {
////                return new ItemList(results);
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null; //new ItemList();
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    public List<Item> getTouched24hLog() {
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        setupItemQueryNotTemplateNotDeletedLimit10000(query);

        //Projects are defined as containing subtasks and not being owner by another Item
//        query.whereEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //include if has subtaskss
        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, new Date(MyDate.currentTimeMillis() - MyDate.DAY_IN_MILLISECONDS));
//        query.whereGreaterThanOrEqualTo(Item.PARSE_UPDATED_AT, MyDate.getStartOfToday()); //NOT needed since cannot be touched in future
        query.orderByDescending(Item.PARSE_UPDATED_AT);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)

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

    private Date getCreationLogStartDate() {
        return new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.creationLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
    }

    private void sortCreationLog(List<Item> list) {
//        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt()));
        Collections.sort(list, (i1, i2) -> FilterSortDef.compareDate(i1.getCreatedAt(), i2.getCreatedAt())); //must put null values *first* since it would mean a new item not yet saved
    }

    private List<Item> cachedCreationLog;
    private Date cachedExpiryCreationLog;

    /**
     * on next getCreationLog, force a reload of the list (by removing the cache
     * expiry date)
     */
    public void reloadCompleted() {
        cachedCompletedLog = null; //
    }

    public void reloadCompletionLog() {
        cachedCompletionLog = null; //
    }

    /**
     * on next getCreationLog, force a reload of the list (by removing the cache
     * expiry date)
     */
    public void reloadCreationLog() {
        cachedCreationLog = null; //
    }

    public void reloadEdited() {
        cachedEditedLog = null; //
    }

    /**
     * on next getCreationLog, force a reload of the list (by removing the cache
     * expiry date)
     */
    public void reloadNext() {
        cachedNext = null; //
    }

    public void reloadOverdue() {
        cachedOverdue = null; //
    }

    public void reloadToday() {
        cachedToday = null; //
    }

    public void reloadTouched() {
        cachedTouchedLog = null; //
    }

    public void reloadList(ItemAndListCommonInterface listToReloadFromParse) {
        if (listToReloadFromParse == cacheWorkSlots) {
            cacheWorkSlots = null;
        } else if (listToReloadFromParse == cachedAllProjects) {
            cachedAllProjects = null;
        } else if (listToReloadFromParse == cachedAllTasks) {
            cachedAllTasks = null;
        } else if (listToReloadFromParse == cachedCompletedLog) {
            cachedCompletedLog = null;
        } else if (listToReloadFromParse == cachedCompletionLog) {
            cachedCompletionLog = null;
        } else if (listToReloadFromParse == cachedCreationLog) {
            cachedCreationLog = null;
        } else if (listToReloadFromParse == cachedEditedLog) {
            cachedEditedLog = null;
        } else if (listToReloadFromParse == cachedNext) {
            cachedNext = null;
        } else if (listToReloadFromParse == cachedOverdue) {
            cachedOverdue = null;
        } else if (listToReloadFromParse == cachedToday) {
            cachedToday = null;
        } else if (listToReloadFromParse == cachedTouchedLog) {
            cachedTouchedLog = null;
        }
    }

    /**
     * returns list of all tasks by date created (should be grouped/collapsed by
     * e.g. week or month)
     *
     * @return
     */
    public List<Item> getCreationLog() {
        //get the start of the day creationLogInterval days back in time
//        Date firstDate = new MyDate(MyDate.getStartOfToday().getTime() - MyPrefs.creationLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS);
        Date firstDate = getCreationLogStartDate();
//        if (cachedCreationLog == null || cachedCreationLogExpiry == null || System.currentTimeMillis() > cachedCreationLogExpiry.getTime()) {
        if (cachedCreationLog != null && cachedExpiryCreationLog != null && System.currentTimeMillis() <= cachedExpiryCreationLog.getTime()) {
            return cachedCreationLog;
        } else {
            ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
            setupItemQueryNotTemplateNotDeletedLimit10000(query);

            if (false) {
                query.orderByDescending(Item.PARSE_CREATED_AT);
            }
            query.whereGreaterThanOrEqualTo(Item.PARSE_CREATED_AT, firstDate);
            setupItemQueryWithIndirectAndGuids(query);
            List results = null;
            try {
                results = query.find();
                fetchListElementsIfNeededReturnCachedIfAvail(results);
                sortCreationLog(results);
                cachedCreationLog = results;
                cachedExpiryCreationLog = MyDate.getEndOfToday(); //valid until midnight
                return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            return null;
        }
    }

    private void checkAndRefreshCreationLog(Item element, boolean delete) {
        if (cachedCreationLog != null) {
            if (delete) {
                cachedCreationLog.remove(element);
            } else {
                Date firstDate = getCreationLogStartDate();
                if (!cachedCreationLog.contains(element)) {
                    if (element.getCreatedAt() == null || element.getCreatedAt().getTime() >= firstDate.getTime()) {
                        cachedCreationLog.add(element);
                        sortCreationLog(cachedCreationLog);
                    }
                } else if (element.getCreatedAt() != null && element.getCreatedAt().getTime() < firstDate.getTime()) {
                    cachedCreationLog.remove(element);
                }
            }
        }
    }

    private void checkAndRefreshAllCachedLists(ParseObject element, boolean delete) {
        if (element instanceof Item) {
            Item item = (Item) element;
            checkAndRefreshToday(item, delete);
            checkAndRefreshAllProjects(item, delete);
            checkAndRefreshCompletedLog(item, delete);
            checkAndRefreshCompletionLog(item, delete);
            checkAndRefreshCreationLog(item, delete);
            checkAndRefreshEditedLog(item, delete);
            checkAndRefreshNext(item, delete);
            checkAndRefreshOverdue(item, delete);
            checkAndRefreshTouchedLog(item, delete);
        } else if (element instanceof WorkSlot) {
            checkAndRefreshToday((WorkSlot) element, delete);
        }
    }

    private void checkAndRefreshAllCachedLists(Collection<ParseObject> saveList, Collection<ParseObject> deleteList) {
        for (ParseObject i : deleteList) { //delete first, so there'll be no update of deleted objects below
            checkAndRefreshAllCachedLists(i, true);
        }
        for (ParseObject i : saveList) {
            checkAndRefreshAllCachedLists(i, false);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    List<ItemAndListCommonInterface> cached;
//    List<ItemAndListCommonInterface> cachedToday;
//
//    /**
//     * get a dynamic list. Logic: if not cached, get from Parse and cache. If
//     * cached, check if anything has happened (updates via DAO) that require it
//     * to be reloaded. If not return cached version, otherwise reload and cache
//     * updated version.
//     *
//     * @return
//     */
//    public List<ItemAndListCommonInterface> getDynamicList(String uniqueName) {
//        if (cached == null || cacheExpiryDate == null || System.currentTimeMillis() > cacheExpiryDate.getTime()) {
//
//            ParseObject cachedElt = cacheGetNamed(uniqueName);
//            if (cachedElt == null || needToRefresh(uniqueName)) {
//                //get list from Parse
//                cachePut(uniqueName, cachedElt);
//            }xxx;
//            cacheExpiryDate = new MyDate();
//            return cached;
//        } else {
//            return cached;
//        }
//    }
//
//    private void updateCachedStatus(ItemAndListCommonInterface element) {
//        if (element instanceof Item) {
//            Item item = (Item) element;
//            Date nextDate = item.getNextDate();
//            //reset Today if item has a date that falls on today
//            if (MyDate.isToday(nextDate) && !cachedToday.contains(item)) {
//                cacheExpiryDateForToday = null;
//            }
//            if (nextDate.) {
//
//            }
//        }
//    }
//</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//    public ItemList getCreationLogXXX(ItemList existingListToUpdate) {
//        //TODO!!! implement getting in batches of less than 1000
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        query2.include(Item.PARSE_TEXT);
////        query2.include(Item.PARSE_SUBTASKS);
////        query2.include(Item.PARSE_CATEGORIES);
////        query.setLimit(QUERY_LIMIT);
//        setupItemQueryNotTemplateNotDeletedLimit10000(query);
////        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
//
//        query.orderByDescending(Item.PARSE_CREATED_AT);
//        query.whereGreaterThanOrEqualTo(Item.PARSE_CREATED_AT, new Date(MyDate.currentTimeMillis() - MyPrefs.creationLogInterval.getInt() * MyDate.DAY_IN_MILLISECONDS));
////        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<Item> results = null;
//        try {
//            results = query.find();
//            fetchListElementsIfNeededReturnCachedIfAvail(results);
////            if (existingListToUpdate instanceof ItemList) {
//            //if we already have an existing list (that eg the timer may be running on), then simply update it
//            existingListToUpdate.clear();
//            existingListToUpdate.addAllNotMakingOwner(results);
//            return existingListToUpdate;
////            } else {
////                return new ItemList(results);
////            }
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null; //new ItemList();
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
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
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void deleteCategoryFromAllItemsXXXNOT_NECESSARY(Category cat) {
////        try {
////            cat.fetchIfNeeded();
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
//        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
////        query.whereContainedIn(Item.PARSE_CATEGORIES, cat);
//        query.whereEqualTo(Item.PARSE_CATEGORIES, cat); //TODO!!: right expression to get all items with cat in Categories??
//        query.include(Item.PARSE_CATEGORIES);
////        query.include(Item.PARSE_CATEGORIES+"."+Category.PARSE_TEXT); //only fetchFromCacheOnly category name
//        List<Item> results = null;
//        try {
//            results = (List<Item>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        for (Item item : results) {
////            Set<Category> cats = item.getCategories();
////            List<Category> itemCats = item.getCategories();
////            itemCats.remove(cat);
////            item.setCategories(itemCats);
//            item.removeCategoryFromItem(cat, false);
//            try {
//                item.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        }
////        cache.delete(cat.getObjectIdP());
//        cacheDelete(cat);
////        return (List<Item>) getAll(Item.CLASS_NAME);
//    }
//</editor-fold>
    private int deleteAllParseObjectsOfClass(String ParseClassName) {
//        final int BATCH_SIZE = 500;
        Log.p("DELETING ALL " + ParseClassName);
        final int BATCH_SIZE = 1000000;
        int skip = 0;
        int deletedObjectsCount = 0;
        List<ParseObject> results = null;
//        do {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassName);
//        Set<String> targetKeys = new HashSet<String>();
//            targetKeys.add("playerName");
//        query.selectKeys(targetKeys); // will this avoid retrieving any data but just the objects themselves??
        query.selectKeys(Arrays.asList()); // will this avoid retrieving any data but just the objects themselves??
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
        deletedObjectsCount += deleteAllParseObjectsOfClass(Category.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(CategoryList.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(FilterSortDef.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(Item.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(ItemList.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(ItemListList.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(RepeatRuleParseObject.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(TemplateList.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(TimerInstance2.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(WorkSlot.CLASS_NAME);
        deletedObjectsCount += deleteAllParseObjectsOfClass(WorkSlot.CLASS_NAME);
        ParseUser parseUser = ParseUser.getCurrent();
        if (removeUserAccount)
            try {
            parseUser.delete();
            deletedObjectsCount++;
        } catch (ParseException ex) {
            Log.p("could not delete user");
        }
//            Log.p("Deleted "+deletedObjectsCount+" user objects");
        MyAnalyticsService.event("", "DeletedUserAccount", "NbDeletedObjects", deletedObjectsCount);
        //TODO!!!! update to cover all classes
        return deletedObjectsCount;
    }

//    public boolean deleteAllLocalStorage() {
//        Storage.getInstance().clearStorage();
//        Storage.getInstance().clearCache();
//        return true;
//    }
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
//    public List<Category> getAllCategoriesContainingItemXXXNOT_NECESSARY(Item item) {
//        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
//        query.whereEqualTo(Category.PARSE_ITEMLIST, item);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
////        List<Category> results = null;
//        try {
//            List<Category> results = query.find();
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return null;
//    }
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
//    public List<ItemList> getAllItemListsIncludingThis(ParseObject someIncludedParseObject) {
//        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
//        query.whereEqualTo(ItemList.PARSE_SOURCE_LISTS, someIncludedParseObject);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<ItemList> results = null;
//        try {
//            results = (List<ItemList>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
    /**
     * return a list of all categories that has this subCategory as sub-category
     * (included in source lists)
     *
     * @param subCategory
     * @return
     */
//    public List<Category> getAllCategoriesIncludingThis(Category subCategory) {
//        ParseQuery<Category> query = ParseQuery.getQuery(Category.CLASS_NAME);
//        query.whereEqualTo(Category.PARSE_SOURCE_LISTS, subCategory);
//        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        List<Category> results = null;
//        try {
//            results = (List<Category>) query.find();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        return results;
//    }
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
//    private Vector vector = new Vector();
//    private List<ParseObject> saveList = new ArrayList();
//    List<Object> saveList = new ArrayList();
//    private Vector<ParseObject> saveList = new Vector();
    /**
     * list of objects explicitly requested to save (via saveToParseNNN()) or
     * soft-deleted so need to be saved as well. This list is saved locally
     * until the save to Parse succeeded to be able to continue if no network
     * and app is killed
     */
    private List<ParseObject> toSaveList = new ArrayList<>();//new Vector();
    /**
     * list of objects explicitly requested to delete (via addToBatchDelete()).
     * This list is saved locally until the save to Parse succeeded to be able
     * to continue if no network and app is killed
     */
    Set<ParseObject> toDeleteList = new HashSet<>();
//    private List<ParseObject> saveList = new ArrayList<>();//new Vector();
//    private Vector<ParseObject> deleteList = new Vector();
//    private List<ParseObject> deleteList = new Vector();
    Set<ParseObject> processedList = new HashSet<>();
//    private List<Runnable> afterParseUpdate = new ArrayList<>();
//    private List<Runnable> beforeParseUpdate = new ArrayList<>();

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
////<editor-fold defaultstate="collapsed" desc="comment">
    /**
     * save any type of ParseObject. Encapsulates the exception handling. Can be
     * called with null objects, but all ParseObjects MUST be of same ParseClass
     *
     * @param listCopyOfParseObjectsToBatchSave must be a *COPY* of the list to
     * be saved to avoid ConcurrentModificationException
     * @param anyParseObject
     */
//    static public void saveBatch(List<ParseObject> listOfParseObjectsToBatchSave) {
//    public void saveBatchXXX(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean saveToCache) {
////        Display.getInstance().callSerially(() -> {
//        if (!listCopyOfParseObjectsToBatchSave.isEmpty()) {
////                if (false) {
////                    for (ParseObject p : listCopyOfParseObjectsToBatchSave) { //LEADS to ConcurrentModificationException
////                        if (!p.isDirty()) { //don't waste bandwidth/time on saving not changed objects
////                            listCopyOfParseObjectsToBatchSave.remove(p);
////                        }
////                    }
////                }
////</editor-fold>
//<editor-fold defaultstate="collapsed" desc="comment">
//            Log.p("SAVE-DAO.saveBatch() saving: " + listCopyOfParseObjectsToBatchSave);
//            try {
//                ParseBatch parseBatch = ParseBatch.create();
//                parseBatch.addObjects(listCopyOfParseObjectsToBatchSave, ParseBatch.EBatchOpType.UPDATE);
//                parseBatch.execute();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//            if (saveToCache) {
//                for (ParseObject o : listCopyOfParseObjectsToBatchSave) {
//                    cachePut(o);
//                }
//            }
//        }
//    }
//</editor-fold>
//    private void batchOperation(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean updateCache, ParseBatch.EBatchOpType batchType) {
//        if (listCopyOfParseObjectsToBatchSave == null || listCopyOfParseObjectsToBatchSave.size() == 0) {
//            return;
//        }
//
//        Log.p("SAVE-DAO.saveBatch(): " + batchType + " of " + listCopyOfParseObjectsToBatchSave);
//        if (updateCache) {
//            if (batchType == EBatchOpType.DELETE) {
//                for (ParseObject o : listCopyOfParseObjectsToBatchSave) {
//                    cacheDelete(o);
//                }
//            }
//        }
//
//        try {
//            ParseBatch parseBatch = ParseBatch.create();
//            parseBatch.addObjects(listCopyOfParseObjectsToBatchSave, batchType);
//            parseBatch.execute();
//        } catch (ParseException ex) {
//            Log.e(ex);
//        } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
//            Log.e(ex);
//        }
//
//        if (updateCache) {
//            if (batchType != EBatchOpType.DELETE) {
//                for (ParseObject o : listCopyOfParseObjectsToBatchSave) {
//                    cachePut(o); //ParseObject p;p.delete();
//                }
//            }
//        }
//    }
//    private void batchSave(List<ParseObject> saveList, List<ParseObject> deleteList) throws ParseException {
//        if ((saveList == null || saveList.size() == 0) && (deleteList == null || deleteList.size() == 0)) {
//            return;
//        }
//        List<ParseObject> createList = new ArrayList();
//        List<ParseObject> updateList = new ArrayList();
//
//        for (ParseObject p : saveList) {
////            p.setSaveIsPending(true);
//            if (p.getObjectIdP() == null) {
//                createList.add(p);
//            } else {
//                ASSERT.that(p.isDirty(), () -> "non-dirty parseObj in saveList, p=" + p + "; saveList=" + saveList);
//                updateList.add(p);
//            }
//        }
//
////        try {
//        ParseBatch parseBatch = ParseBatch.create();
//        parseBatch.addObjects(createList, EBatchOpType.CREATE);
//        parseBatch.addObjects(updateList, EBatchOpType.UPDATE);
//        parseBatch.addObjects(deleteList, EBatchOpType.DELETE);
//        //NB. Sseparating encoding (addObjects) and execution will  NOT enable parallelism since execution will reset dirty, which means any additional changes to parseobjects would be lost
//        parseBatch.execute();
////        } catch (ParseException ex) {
////            Log.e(ex);
////        } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
////            Log.e(ex);
////        }
//    }
    private ParseBatch batchSavePrepare(Collection<ParseObject> saveList, Collection<ParseObject> deleteListN) throws ParseException {
//        if ((saveList == null || saveList.size() == 0) && (deleteList == null || deleteList.size() == 0)) {
//            return null;
//        }
        List<ParseObject> createList = new ArrayList();
        List<ParseObject> updateList = new ArrayList();

        if (saveList != null) {
            for (ParseObject p : saveList) {
                //skip any updated elements that are also going to be delete, since deleting and saving in the same batch seems to leave an empty ParseObject in parseServer (notably without any ACL, meaning it is read by everyone making the timer disfunctioon!)
                if (deleteListN == null || !deleteListN.contains(p)) { //skip saving any objects also scheduled for deletion***
//            p.setSaveIsPending(true);
                    if (p.getObjectIdP() == null) {
                        createList.add(p);
                    } else {
                        if (false) {
                            ASSERT.that(p.isDirty(),
                                    () -> "non-dirty parseObj in saveList, p=" + p + "; saveList=" + saveList);
                        }
                        if (p.isDirty()) { //only save in this round if dirty (keep in saveList in case it is modified afterwards)
                            updateList.add(p);
                        }
                    }
                } else {
                    ASSERT.that("ParseObj both updated and deleted in same batch =" + p);
                }
            }
        }

        ParseBatch parseBatch = null;
        if (!createList.isEmpty() || !updateList.isEmpty() || (deleteListN != null && !deleteListN.isEmpty())) {
//        try {
            parseBatch = ParseBatch.create();
            if (!createList.isEmpty()) {
                parseBatch.addObjects(createList, EBatchOpType.CREATE);
            }
            if (!updateList.isEmpty()) {
                parseBatch.addObjects(updateList, EBatchOpType.UPDATE);
            }

            if (deleteListN != null && !deleteListN.isEmpty()) {
                parseBatch.addObjects(deleteListN, EBatchOpType.DELETE);
            }
        }
        //NB. Sseparating encoding (addObjects) and execution will  NOT enable parallelism since execution will reset dirty, which means any additional changes to parseobjects would be lost
//        parseBatch.execute();
        return parseBatch;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
//            Log.e(ex);
//        }
    }

//    private void batchCreateObjects(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean saveToCache) {
//        batchOperation(listCopyOfParseObjectsToBatchSave, saveToCache, ParseBatch.EBatchOpType.CREATE);
//    }
//
//    private void batchUpdateObjects(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean saveToCache) {
//        batchOperation(listCopyOfParseObjectsToBatchSave, saveToCache, ParseBatch.EBatchOpType.UPDATE);
//    }
//
//    private void batchDeleteObjects(List<ParseObject> listCopyOfParseObjectsToBatchSave, boolean saveToCache) {
//        batchOperation(listCopyOfParseObjectsToBatchSave, saveToCache, ParseBatch.EBatchOpType.DELETE);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveItemInBackgroundXXX(Item projectOrItem, Runnable postSaveAction) {
//        if (projectOrItem == null) {
//            return;
//        }
//        if (Config.TEST_BACKGR) {
//            Log.p("==========>>> DAO.saveItemInBackground(elt=" + testShowMissingRefs(projectOrItem) + ")");
//        }
//        FilterSortDef filter = projectOrItem.getFilterSortDef();
//        Item source = (Item) projectOrItem.getSource(); //source of Item is necessarily an Item itself
//        Item dependingOnTask = (Item) projectOrItem.getDependingOnTask();
//        saveInBackgroundXXX(filter);  //Filter has no references back to Item, so can always be saved
//        saveItemInBackgroundXXX(source, null);
//        saveItemInBackgroundXXX(dependingOnTask, null);
//
//        RepeatRuleParseObject repeatRule = projectOrItem.getRepeatRule();
//        List subtasks = projectOrItem.getListFull();
//        List categories = projectOrItem.getCategories();
//
//        if (projectOrItem.getObjectIdP() == null) { //if top-level item not saved yet, do so before saving the subtasks/repeatRule that reference it
//
//            projectOrItem.setRepeatRuleInParse(null); //temporarily remove repeatRule (which is referring back to workslot)
//            projectOrItem.setList(null);
//            projectOrItem.setCategories(null);
//
//            addToSaveQueueXXX(projectOrItem); //first save projectOrItem (so it can be referenced)
//
//            //after projectOrItem has been saved, the references (with potentially unsaved elements) can be added back
//            projectOrItem.setRepeatRuleInParse(repeatRule);
//            projectOrItem.setList(subtasks);
//            projectOrItem.setCategories(categories);
//        }
//
//        saveInBackgroundXXX(repeatRule);
//        saveInBackgroundXXX(subtasks);
//        saveInBackgroundXXX(categories); //an item may have been added to categories that will need saving
//
//        addToSaveQueueXXX(projectOrItem); //first save repeatRule (so repeatInstances can reference it)
//    }
//    private List<ParseObject> batchCreateList = new ArrayList<>();
//    private Set<ParseObject> batchCreateList = new HashSet<>();
//    private List<Runnable> afterParseUpdate = new ArrayList<>();
//    private List<Runnable> beforeParseUpdate = new ArrayList<>();
//    private List<Runnable> lambdasRunAfterSave = new ArrayList<>();
//    List<ParseObject> batchUpdateList = new ArrayList<>();
//    private Set<ParseObject> batchUpdateList = new HashSet<>();
//    List<ParseObject> batchDeleteList = new ArrayList<>();
//    private Set<ParseObject> batchDeleteList = new HashSet<>();
//    private List<ParseObject> batchSaveList = new ArrayList<>();
//    HashMap<ParseObject, Runnable> runAfterSaveXXX = new HashMap<>();
    /**
     * if p is new or dirty, add it to the save list
     *
     * @param p
     */
//    private void addToSaveList(ParseObject p) {
//        if (Config.TEST) {
//            ASSERT.that(p.getObjectIdP() == null);
//        }
//        if (p != null && (p.isDirty() || p.getObjectIdP() == null) && !batchCreateList.contains(p)) {
//            batchSaveList.add(p);
//        }
//    }
//</editor-fold>
    private void addToBatchDelete(ParseObject p) {
        if (Config.TEST) {
            ASSERT.that(p.getObjectIdP() != null);
        }
//        if (!batchDeleteList.contains(p)) {
//            batchDeleteList.add(p);
//        }
        toDeleteList.add(p);
//        toSaveList.remove(p); //remove any previously updated elements since deleting and saving in the same batch seems to leave an empty ParseObject in parseServer (notably without any ACL, meaning it is read by everyone making the timer disfunctioon!) //NOT do here, done when creating batch operation"
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public boolean deleteXXX(ParseObject parseObject, boolean hardDelete, boolean triggerUpdate) {
//        if (parseObject instanceof ItemAndListCommonInterface) {
//            ItemAndListCommonInterface elt = (ItemAndListCommonInterface) parseObject;
//            elt.deletePrepare(new MyDate());
//            if (hardDelete) {
//                if (parseObject.getObjectIdP() != null) { //only hard-delete if already saved
//                    addToBatchDelete((ParseObject) parseObject);
//                }
//            } else {
//                if (parseObject.getObjectIdP() != null) {
//                    if (elt.getSoftDeletedDateN() == null) {
//                        elt.setSoftDeletedDate(new MyDate());
//                    }
//                    addToBatchUpdate((ParseObject) parseObject);
//                }
//            }
//            if (triggerUpdate) {
//                triggerParseUpdate();
//            }
//            return true;
//        } else { //e.g. FilterSortDef
//            if (true || hardDelete) { //true: can't currently soft-delete filters (so addToBatchUpdate doesn't make sense)!
//                addToBatchDelete((ParseObject) parseObject);
//            } else {
//                addToBatchUpdate((ParseObject) parseObject);
//            }
//            if (triggerUpdate) {
//                triggerParseUpdate();
//            }
//            return true;
//        }
//    }
//</editor-fold>
    public boolean delete(ParseObject parseObject, boolean hardDelete, boolean triggerUpdate) {
        return delete(parseObject, hardDelete, triggerUpdate, false);
    }

    public boolean delete(ParseObject parseObject, boolean hardDelete, boolean triggerUpdate, boolean waitForCompletion) {
        if (parseObject instanceof ItemAndListCommonInterface) {
            ItemAndListCommonInterface elt = (ItemAndListCommonInterface) parseObject;

            elt.onDelete(new MyDate());

            if (hardDelete) {
                if (parseObject.getObjectIdP() != null) { //only hard-delete if already saved
                    addToBatchDelete((ParseObject) parseObject);
                }
            } else {
                if (parseObject.getObjectIdP() != null) {
                    if (elt.getSoftDeletedDateN() == null) {
                        elt.setSoftDeletedDate(new MyDate());
                    }
//                    if (true || !toSaveList.contains(parseObject)) {
                    addToSaveList3((ParseObject) parseObject, toSaveList);
//                    }
                }
            }
//            if (triggerUpdate) {
//                triggerParseUpdate(waitForCompletion);
//            }
//            return true;
        } else { //e.g. FilterSortDef
            if (true || hardDelete) { //true: can't currently soft-delete filters (so addToBatchUpdate doesn't make sense)!
                addToBatchDelete((ParseObject) parseObject);
            }
        }
        if (triggerUpdate) {
            triggerParseUpdate(waitForCompletion || !MyPrefs.backgroundSave.getBoolean());
        }
        return true;
    }

    public boolean deleteNow(ParseObject parseObject, boolean hardDelete) {
        return delete(parseObject, hardDelete, true, false);
    }

    public boolean deleteNowAndWait(ParseObject parseObject, boolean hardDelete) {
        return delete(parseObject, hardDelete, true, true);
    }

    public boolean deleteLater(ParseObject parseObject, boolean hardDelete) {
        return delete(parseObject, hardDelete, false, false);
    }

    public boolean deleteLater(Collection<ParseObject> parseObjects, boolean hardDelete) {
        return deleteAll(parseObjects, hardDelete, false);
    }

//    public void deleteTimerInstance(TimerInstance timerInstance) {
//        Display.getInstance().callSerially(() -> {
//            try {
//                timerInstance.delete();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        });
//    }
    public boolean deleteAll(Collection<ParseObject> parseObjects, boolean hardDelete, boolean triggerUpdate) {
        for (ParseObject p : parseObjects) {
            delete(p, hardDelete, false); //do triggerUpdate below
        }
        if (triggerUpdate) {
            triggerParseUpdate(false);
        }
        return true;
    }

    public boolean deleteAllNow(Collection<ParseObject> parseObjects, boolean hardDelete) {
        return deleteAll(parseObjects, hardDelete, true);
    }

//    public void saveTimerInstanceInBackground(TimerInstance timerInstance) {
//        Display.getInstance().callSerially(() -> {
//            try {
//                timerInstance.save();
//            } catch (ParseException ex) {
//                Log.e(ex);
//            }
//        });
//    }
    private String testShowMissingRefs(Object elt) {
        String s = "";
        if (true) {
            return s;
        }
        if (elt instanceof Item) {
            s += "ITEM: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((Item) elt).getObjectIdP() != null ? ((Item) elt).getObjectIdP() : "<null>");
            s += "; owner=" + (((Item) elt).getOwner() != null ? ((Item) elt).getOwner() : "<null>; ");
            s += "; repeatRule=" + (((Item) elt).getRepeatRuleN() != null ? ((Item) elt).getRepeatRuleN() : "<null>");
            s += "; subtasks={" + (((Item) elt).getListFull() + "}");
        } else if (elt instanceof RepeatRuleParseObject) {
            s += "REPEATRULE: ";
            s += "\"" + elt + "\"";
            s += "; objId=" + (((RepeatRuleParseObject) elt).getObjectIdP() != null ? ((RepeatRuleParseObject) elt).getObjectIdP() : "<null>");
            s += "; instances={" + (((RepeatRuleParseObject) elt).getListOfUndoneInstances() + "}");
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

    public void saveList3(List list, List saveList, List<Runnable> afterParseUpdate) {
        saveList3(list, saveList, afterParseUpdate, false);
    }

    /**
     *
     * @param list
     * @param saveList
     * @param stopOnFirstUndirty if true, the first not dirty parseObject
     * encountered when starting from the the end of the list will stop the
     * iteration - used to optimize done/undone lists in RepeatRules
     */
    public void saveList3(List<ParseObject> list, List saveList, List afterParseUpdate, boolean stopOnFirstUndirty) {
        if (list != null) {
//            for (Object o : list) {
            for (int i = list.size() - 1; i >= 0; i--) { //start from end of list to enable exiting loop on first
                ParseObject o = list.get(i);
                if (stopOnFirstUndirty && !o.isDirty()) {
                    break;
                } else if (o instanceof Item) {
                    saveItemNew3((Item) o, saveList, afterParseUpdate);
                } else if (o instanceof WorkSlot) { //do workslots first, because most likely to be repeated(?)
                    saveWorkSlotNew3(((WorkSlot) o), saveList, afterParseUpdate);
                } else if (o instanceof Category) {
                    saveCategoryNew3(((Category) o), saveList, afterParseUpdate);
                } else if (o instanceof ItemList) {
                    saveItemListNew3(((ItemList) o), saveList, afterParseUpdate);
                } else {
                    ASSERT.that(false, () -> "element other than Item/ItemList/WorkSlot in list, list=" + list);
                }
            }
        }
    }

    /**
     * returns true if there is at least one unsaved element
     *
     * @param parseObjects
     * @return
     */
    private boolean listContainsNotCreated(List<ParseObject> parseObjects) {
        return listContainsNotCreated(parseObjects, false);
    }

    private boolean listContainsNotCreated(List<ParseObject> parseObjects, boolean onlyTestLastInList) {
//        for (ParseObject p : parseObjects) {
        for (int i = parseObjects.size() - 1; i >= 0; i--) { //optimization: start from last element, the most likely to not be saved
            ParseObject p = (ParseObject) parseObjects.get(i);
            if (p.isNotCreated()) {
                return true;
            } else if (onlyTestLastInList) {
                return false;
            }
            //DON'T iterate down the hierarchy!
//            else if (p instanceof ItemAndListCommonInterface) {
//                return listContainsUnsaved(((ItemAndListCommonInterface) p).getListFull());
//            }
        }
        return false;
    }

    private boolean itemListContainsUnsaved(ItemList parseObjects) {
//        for (ParseObject p : parseObjects) {
        for (int i = parseObjects.getSize() - 1; i >= 0; i--) { //optimization: start from last element, the most likely to not be saved
            ParseObject p = (ParseObject) parseObjects.getItemAt(i);
            if (p.isNotCreated()) {
                return true;
            }
            //DON'T iterate down the hierarchy!
//            else if (p instanceof ItemAndListCommonInterface) {
//                return listContainsUnsaved(((ItemAndListCommonInterface) p).getListFull());
//            }
        }
        return false;
    }

//    private void addToSaveList3(ParseObject p) {
//        addToSaveList3(p, false, saveList);
//    }
    private static void addToSaveList3(ParseObject p, List saveList) {
        addToSaveList3(p, false, saveList);
    }
//    private void addToSaveList3(ParseObject p, List saveList) {
//        addToSaveList3(p, false);
//    }

//    private void addToSaveList3(ParseObject p, boolean noCheck) {
//        addToSaveList3(p, noCheck, saveList);
//    }
    private static void addToSaveList3(ParseObject p, boolean noCheck, List saveList) {
        ASSERT.that(!(p instanceof ItemAndListCommonInterface) || !((ItemAndListCommonInterface) p).isNoSave(),
                () -> "p is noSave = " + p);
        ASSERT.that(p.isDirty(),
                () -> "p is NOT dirty, =" + p);
        ASSERT.that(!noCheck || !saveList.contains(p), () -> "saveList already contains itemList=" + p + ", saveList=" + saveList);

        if ((noCheck || !saveList.contains(p))) {
//            saveList.add(p);
//            addToSaveList3(p, true, saveList);
            saveList.add(p);
        }
    }

    private void addToSaveListNoChk3(ParseObject p, List saveList) {
//        saveList.add(p);
        addToSaveList3(p, true, saveList);
    }

    /**
     * keep track of which elements have already been processed to avoid
     * infinite recursion
     *
     * @param p
     */
    private void addToProcessed3(ParseObject p) {
        ASSERT.that(!processedList.contains(p),
                () -> "processedList already contains element=" + p + ", processedList=" + processedList);

        if (!processedList.contains(p)) {
            processedList.add(p);
        }
    }

    private void saveTimerInstanceNew3(TimerInstance2 timerInstance, List saveList, List<Runnable> afterParseUpdate) {
//        if (false && timerInstance != null && timerInstance.needsSaving() && !processedList.contains(timerInstance)) {
//            addToProcessed3(timerInstance);
//            addToSaveList3(timerInstance, saveList);
//        }

        if (timerInstance.isDirty() && !processedList.contains(timerInstance)) {//!repeatRule.isUnsaved()) {
            addToProcessed3(timerInstance);
//        if (timerInstance.isDirty()) {//!repeatRule.isUnsaved()) {
////            addToProcessed3(timerInstance);
            boolean isNotCreated = timerInstance.isNotCreated();

            boolean referencesUnsavedParseObjects = false;

            //TIMED ITEM
            {
                Item timedItem = (Item) timerInstance.getTimedItemN();
                if (timedItem != null && timedItem.needsSaving()) {
                    saveItemNew3(timedItem, saveList, afterParseUpdate);
                    if (timedItem.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            timerInstance.setTimedItemInParse(null); //this will remove the listener until after save below, OK?!
                            afterParseUpdate.add(() -> {
                                timerInstance.setTimedItemInParse(timedItem);
                            });
                        }
                    }
                }
            }
            //NEXT ITEM
            {
                Item nextItem = (Item) timerInstance.getNextTimedItemN();
                if (nextItem != null && nextItem.needsSaving()) {
                    saveItemNew3(nextItem, saveList, afterParseUpdate);
                    if (nextItem.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            timerInstance.setNextTimedItemInParse(null);
                            afterParseUpdate.add(() -> {
                                timerInstance.setNextTimedItemInParse(nextItem);
                            });
                        }
                    }
                }
            }
            //TIMER SOURCE
            {
                ItemAndListCommonInterface source = timerInstance.getTimerSourceN();
                if (source != null) {// && owner.needsSaving()) {
                    if (source instanceof Item) {
                        saveItemNew3((Item) source, saveList, afterParseUpdate);
                    } else if (source instanceof Category) {
                        saveCategoryNew3((Category) source, saveList, afterParseUpdate);
                    } else if (source instanceof ItemList) {
                        saveItemListNew3((ItemList) source, saveList, afterParseUpdate);
                    }
                    if (source.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            timerInstance.setTimerSourceInParse(null);
                            afterParseUpdate.add(() -> {
                                timerInstance.setTimerSourceInParse(source);
                            });
                        }
                    }
                }
            }
            savingOrder(timerInstance, isNotCreated, referencesUnsavedParseObjects, saveList, afterParseUpdate);
        }
    }

    private void saveFilterSortDefNew3(FilterSortDef filter, List saveList, List<Runnable> afterParseUpdate) {
        if (filter != null && filter.needsSaving() && !processedList.contains(filter)) {
            addToProcessed3(filter);
//        if (filter != null && filter.needsSaving()) {
//            addToProcessed3(filter);
            addToSaveList3(filter, saveList);
        }
    }

//    private void saveTimerInstanceNew3(TimerInstance timerInstance, List saveList, List<Runnable> afterParseUpdate) {
//        if (timerInstance != null && timerInstance.needsSaving() && !processedList.contains(timerInstance)) {
//            addToProcessed3(timerInstance);
//            addToSaveList3(timerInstance, saveList);
//        }
//    }
    private void saveItemNew3(Item item, List saveList, List<Runnable> afterParseUpdate) {//,List<Runnable>batchRunAfterCreation) {
//        ASSERT.that(item.getObjectIdP() == null, "item should never have an objectId at this point, item=" + item);

//        if (saveList.contains(item)) {
//            return;
//        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveItemInBackground(elt=" + testShowMissingRefs(item) + ")");
        }

//        if (!saveList.contains(item)) {
//            if (!item.isUnsaved()) {
//                //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
//                afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//            } else {
        if (item.isDirty() && !processedList.contains(item)) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(item, saveList);
            addToProcessed3(item);
//        if (item.isDirty()) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(item, saveList);
//            addToProcessed3(item);
            boolean isNotCreated = item.isNotCreated();
//                    if (!saveList.contains(item) && item.needsSaving()) {
//            addToSaveListNoChk3(item, saveList);
////        }

//        boolean itemIsUnsaved = item.isUnsaved(); //no ObjectId yet, so don't save any of this item's owner, categories, repeatRules until it has been saved
            boolean referencesUnsavedParseObjects = false;

            //OWNER
            {
                ItemAndListCommonInterface owner = item.getOwner();
                if (owner != null) {// && owner.needsSaving()) {
//                if (false) {
//                    if (owner instanceof Item) {
//                        saveItemNew3((Item) owner, saveList);
//                    } else {
//                        saveItemListNew3((ItemList) owner, saveList);
//                    }
//                }
                    if (owner instanceof Item) {
                        saveItemNew3((Item) owner, saveList, afterParseUpdate);
                    } else {
                        saveItemListNew3((ItemList) owner, saveList, afterParseUpdate);
                    }
                    if (owner.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            if (owner instanceof Item) {
                                item.setOwnerItem(null, false, false);
                                afterParseUpdate.add(() -> {
                                    item.setOwnerItem((Item) owner, false, false);
                                });
                            } else {
                                if (Config.TEST) {
                                    ASSERT.that(owner instanceof ItemList, "If Owner not an Item it should always be an ItemList, owner=" + owner);
                                }
                                item.setOwner(null);
                                //after owner has been saved, then add it back as owner and save item with owner
                                afterParseUpdate.add(() -> {
                                    item.setOwner(owner);
                                });
                            }
                        }
                    }
                }
            }

            //Filter has no references back to Item, so can always be saved
            {
                FilterSortDef filter = item.getFilterSortDefN();
                if (filter != null && !filter.isNoSave() && filter.needsSaving()) {
                    saveFilterSortDefNew3(filter, saveList, afterParseUpdate);
                    if (filter.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setFilterSortDef(null);
                            afterParseUpdate.add(() -> {
                                item.setFilterSortDef(filter);
                            });
                        }
                    }
                }
            }

            //SOURCE
            {
                Item source = (Item) item.getSource(); //source of Item is necessarily an Item itself
                if (source != null && source.needsSaving()) {
                    saveItemNew3(source, saveList, afterParseUpdate);
                    if (source.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setSource(null);
                            afterParseUpdate.add(() -> {
                                item.setSource(source);
                            });
                        }
                    }
                }
            }

            //DEPENDING ON
            {
                Item dependingOnTask = (Item) item.getDependingOnTask();
                if (dependingOnTask != null && dependingOnTask.needsSaving()) {
                    saveItemNew3(dependingOnTask, saveList, afterParseUpdate);
                    if (dependingOnTask.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setDependingOnTask(null);
                            afterParseUpdate.add(() -> {
                                item.setDependingOnTask(dependingOnTask);
                            });
                        }
                    }
                }
            }

            //REPEATRULE
            {
                RepeatRuleParseObject repeatRule = item.getRepeatRuleN();
                if (repeatRule != null && repeatRule.needsSaving()) {
                    saveRepeatRuleNew3(repeatRule, saveList, afterParseUpdate);
                    if (repeatRule.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setRepeatRuleInParse(null);
                            afterParseUpdate.add(() -> {
                                item.setRepeatRuleInParse(repeatRule);
                            });
                        }
                    }
                }
            }

            //SUBTASKS
            {
                List subtasks = item.getListFull();
                if (subtasks != null && !subtasks.isEmpty()) {
                    saveList3(subtasks, saveList, afterParseUpdate);  //save any subtasks that might not be saved yet
                    if (listContainsNotCreated(subtasks)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setList(null); //optimization: strictly only necessary if any unsaved subtasks (or this Item is not yet saved), but for now always 
                            afterParseUpdate.add(() -> {
                                item.setList(subtasks);
                            });
                        }
                    }
                }
            }

            //CATEGORIES
            {
                List categories = item.getCategories();
                //NOT currently possible to have unsaved categories (they are saved immediately after being created)
                if (true || listContainsNotCreated(categories)) { //save any modified categories (eg an item was added)
                    saveList3(categories, saveList, afterParseUpdate);
                }
                if (Config.TEST) {
                    ASSERT.that(!listContainsNotCreated(categories), () -> "ERROR: Item=" + item + " contains references to unsaved categories=" + categories);
                }
            }

            //WORKSLOTS
            {
                List<WorkSlot> listWorkSlots = item.getWorkSlotsFromParseN();
                if (listWorkSlots != null && !listWorkSlots.isEmpty()) {
                    if (listContainsNotCreated((List) listWorkSlots, true)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            item.setWorkSlotsInParse(null);
                            afterParseUpdate.add(() -> {
                                item.setWorkSlotsInParse(listWorkSlots);
                            });
                        }
                    }
                }
            }

//<editor-fold defaultstate="collapsed" desc="comment">
//                if (referencesUnsavedParseObjects) {
//                    afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//                } else {
//                    addToSaveList3(item, saveList);
//                }
//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3(item, saveList);
//            } else {
//                if (isUnsaved) {
//                    addToSaveList3(item, saveList);
//                }
//                afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//            }
//            if (isNotCreated) {
//                if (referencesUnsavedParseObjects) {
//                    addToSaveList3(item, saveList);
//                    afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//                } else {
//                    addToSaveList3(item, saveList);
//                }
//            } else { //already saved
//                afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//            }
//</editor-fold>
//            if (false) {
//                if (isNotCreated) {
//                    if (referencesUnsavedParseObjects) {
//                        addToSaveList3(item, saveList);
//                        afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//                    } else {
//                        addToSaveList3(item, saveList);
//                    }
//                } else { //already created
//                    if (referencesUnsavedParseObjects) {
//                        afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//                    } else {
//                        addToSaveList3(item, saveList);
//                    }
//                }
//            }
            savingOrder(item, isNotCreated, referencesUnsavedParseObjects, saveList, afterParseUpdate);
        }
//            if (referencesUnsavedParseObjects) {
//                afterParseUpdate.add(() -> addToSaveList3(item, saveList));
//            }
    }

    private void saveItemListNew3(ItemList itemList, List saveList, List<Runnable> afterParseUpdate) {
//        saveItemListNew3(itemList, false, saveList);
//    }
//
//    private void saveItemListNew3(ItemList itemList, boolean saveItemsAlwaysFalse, List saveList) {
        ASSERT.that(!itemList.isNoSave(),
                () -> "itemList is noSave");
//        if (false && itemListContainsUnsaved(itemList)) {
////            saveList.remove(category); //not needed, should never be added to saveList
//            ASSERT.that(!saveList.contains(itemList), () -> "saveList already contains itemList=" + itemList + ", saveList=" + saveList);
//            afterParseUpdate.add(() -> {
//                addToSaveList3((ParseObject) itemList, saveList); //if category references yet unsaved items, add it to saveList in second round (after new parseObjects have been created)
//            });
//            return;
//        }
//        if (saveList.contains(itemList)) {
//            return;
//        }
//        if (!itemList.needsSaving()) {
//            return;
//        }

        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveItemListInBackground(elt=" + testShowMissingRefs(itemList) + ")");
        }

//        addToSaveList3(itemList);
//        if (saveItemsAlwaysFalse) {
//            saveList3(itemList.getListFull(), saveList);
//        }
//        if (!saveList.contains(itemList) && itemList.needsSaving()) {
//            addToSaveList3(itemList, saveList);
//        if (!saveList.contains(itemList)) {
//            if (!itemList.isUnsaved()) {
//                //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
//                afterParseUpdate.add(() -> addToSaveList3(itemList, saveList));
//            } else {
        if (itemList.isDirty() && !processedList.contains(itemList)) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(itemList, saveList);
            addToProcessed3(itemList);
//        if (itemList.isDirty()) {//!repeatRule.isUnsaved()) {
////            addToSaveList3(itemList, saveList);
////            addToProcessed3(itemList);
            boolean isNotCreated = itemList.isNotCreated();
//                    if (!saveList.contains(item) && item.needsSaving()) {
//            addToSaveListNoChk3(item, saveList);
////        }
//        boolean itemIsUnsaved = item.isUnsaved(); //no ObjectId yet, so don't save any of this item's owner, categories, repeatRules until it has been saved
            boolean referencesUnsavedParseObjects = false;

            //OWNER
            ItemAndListCommonInterface owner = itemList.getOwner();
            if (owner != null) {// && owner.needsSaving()) {
                saveItemListNew3((ItemList) owner, saveList, afterParseUpdate);
                if (owner.isNotCreated()) {
                    referencesUnsavedParseObjects = true;
                    if (isNotCreated) {
                        itemList.setOwner(null);
                        //after owner has been saved, then add it back as owner and save item with owner
                        afterParseUpdate.add(() -> {
                            itemList.setOwner(owner);
                        });
                    }
                }
            }

//            {
//                List tasks = itemList.getListFull();
//                if (listContainsUnsaved(tasks)) {
//                    itemList.setList(null);
//                    afterParseUpdate.add(() -> {
//                        itemList.setList(tasks);
//                        addToSaveList3((ParseObject) itemList, saveList); //if category references yet unsaved items, add it to saveList in second round (after new parseObjects have been created)
//                    });
//                }
//            }
            {
                //save a filter before saving the element referencing it
                FilterSortDef filter = itemList.getFilterSortDefN();
                if (filter != null && !filter.isNoSave()) {// && filter.getObjectIdP() == null) {
                    saveFilterSortDefNew3(filter, saveList, afterParseUpdate);
                    if (filter.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            itemList.setFilterSortDef(null);
                            afterParseUpdate.add(() -> {
                                itemList.setFilterSortDef(filter);
                            });
                        }
                    }
                }
            }

            {
                List<ParseObject> listTasks = itemList.getListFull();
                if (listTasks != null && !listTasks.isEmpty()) {
                    saveList3(listTasks, saveList, afterParseUpdate);  //save any subtasks that might not be saved yet
                    if (listContainsNotCreated(listTasks)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            itemList.setList(null);
                            afterParseUpdate.add(() -> {
                                itemList.setList(listTasks);
                            });
                        }
                    }
                }
            }

            {
                List<ParseObject> listWorkSlots = itemList.getWorkSlotsFromParseN();
                if (listWorkSlots != null && !listWorkSlots.isEmpty()) {
                    if (listContainsNotCreated(listWorkSlots, true)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            itemList.setWorkSlotsInParse(null);
                            afterParseUpdate.add(() -> {
                                itemList.setWorkSlotsInParse(listWorkSlots);
                            });
                        }
                    }
                }
            }

//                if (referencesUnsavedParseObjects) {
//                    afterParseUpdate.add(() -> addToSaveList3((ParseObject) itemList, saveList));
//                } else {
//                    addToSaveList3((ParseObject) itemList, saveList);
//                }
//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3(itemList, saveList);
//            } else {
//                if (itemList.isUnsaved()) {
//                    addToSaveList3(itemList, saveList);
//                }
//                afterParseUpdate.add(() -> addToSaveList3(itemList, saveList));
//            }
//            if (isUnsaved) {
//                if (referencesUnsavedParseObjects) {
//                    addToSaveList3(itemList, saveList);
//                    afterParseUpdate.add(() -> addToSaveList3(itemList, saveList));
//                } else {
//                    addToSaveList3(itemList, saveList);
//                }
//            } else { //already saved
//                afterParseUpdate.add(() -> addToSaveList3(itemList, saveList));
//            }
            savingOrder(itemList, isNotCreated, referencesUnsavedParseObjects, saveList, afterParseUpdate);

//            if (referencesUnsavedParseObjects) {
//                afterParseUpdate.add(() -> addToSaveList3(itemList, saveList));
//            }
        }
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveNewImplOld(Collection<ParseObject> parseObjects, boolean triggerSave, boolean waitForCompletion) {
//        if (parseObjects == null || parseObjects.isEmpty()) {
//            if (Config.TEST_BACKGR) {
//                Log.p("==========>>> DAO.saveCategoryInBackground() SKIPPING null/empty objects =" + parseObjects);
//            }
//            return;
//        }
//
//        for (ParseObject p : parseObjects) {
//            if (p == null) {
//                continue;
//            }
//            if (p instanceof ItemAndListCommonInterface) {
//                if (((ItemAndListCommonInterface) p).isNoSave()) {
//                    continue; //skip nosave elements
//                } else {
//                    if (true) {
//                        ((ItemAndListCommonInterface) p).updateBeforeSave();
//                    }
//                }
//            }
//            if (Config.TEST) {
//                testShowMissingRefs(p);
//            }
//
//            if (p instanceof Item) {
//                saveItemNew3((Item) p);
//            } else if (p instanceof Category) { //also covers Category, and ItemListList and CategoryList
//                saveCategoryNew3((Category) p);
//            } else if (p instanceof ItemList) { //also covers Category, and ItemListList and CategoryList
//                saveItemListNew3((ItemList) p);
//            } else if (p instanceof WorkSlot) { //e.g. Category, WorkSlot
//                saveWorkSlotNew3((WorkSlot) p);
//            } else if (p instanceof RepeatRuleParseObject) {
//                saveRepeatRuleNew3((RepeatRuleParseObject) p);
//            } else if (p instanceof FilterSortDef) { //e.g. Category, WorkSlot
//                saveFilterSortDefNew3((FilterSortDef) p);
//            } else { //e.g. Category, WorkSlot
//                if (Config.TEST) {
//                    ASSERT.that(false, () -> "type of object not handled, p=" + p + "; list=" + parseObjects);
//                }
//            }
//        }
//
//        if (triggerSave) {
////            assert false;
//            triggerParseUpdate(waitForCompletion);
//        }
//    }
//
//    private void prepForSaveXXX(Collection<ParseObject> parseObjects, boolean triggerSave, boolean waitForCompletion) {
//        if (parseObjects == null || parseObjects.isEmpty()) {
//            if (Config.TEST_BACKGR) {
//                Log.p("==========>>> DAO.saveCategoryInBackground() SKIPPING null/empty objects =" + parseObjects);
//            }
//            return;
//        }
//
//        for (ParseObject p : parseObjects) {
//            if (p == null) {
//                continue;
//            }
//            if (p instanceof ItemAndListCommonInterface) {
//                if (((ItemAndListCommonInterface) p).isNoSave()) {
//                    continue; //skip nosave elements
//                } else {
//                    if (true) {
//                        ((ItemAndListCommonInterface) p).updateBeforeSave();
//                    }
//                }
//            }
//            if (Config.TEST) {
//                testShowMissingRefs(p);
//            }
//
//            if (p instanceof Item) {
//                saveItemNew3((Item) p);
//            } else if (p instanceof Category) { //also covers Category, and ItemListList and CategoryList
//                saveCategoryNew3((Category) p);
//            } else if (p instanceof ItemList) { //also covers Category, and ItemListList and CategoryList
//                saveItemListNew3((ItemList) p);
//            } else if (p instanceof WorkSlot) { //e.g. Category, WorkSlot
//                saveWorkSlotNew3((WorkSlot) p);
//            } else if (p instanceof RepeatRuleParseObject) {
//                saveRepeatRuleNew3((RepeatRuleParseObject) p);
//            } else if (p instanceof FilterSortDef) { //e.g. Category, WorkSlot
//                saveFilterSortDefNew3((FilterSortDef) p);
//            } else { //e.g. Category, WorkSlot
//                if (Config.TEST) {
//                    ASSERT.that(false, () -> "type of object not handled, p=" + p + "; list=" + parseObjects);
//                }
//            }
//        }
//
//        if (triggerSave) {
////            assert false;
//            triggerParseUpdate(waitForCompletion);
//        }
//    }
//</editor-fold>

    private void saveCategoryNew3(Category category, List saveList, List<Runnable> afterParseUpdate) {
        saveCategoryNew3(category, false, saveList, afterParseUpdate);
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveCategoryNew3OLD(Category category, boolean saveItems, List saveList) {
//        //if not dirty, do nothing, if contains refs to unsaved, save in second round
//        if (itemListContainsUnsaved(category)) {
////            saveList.remove(category); //not needed, should never be added to saveList
//            ASSERT.that(!saveList.contains(category), () -> "saveList already contains category=" + category + ", saveList=" + saveList);
//            afterParseUpdate.add(() -> {
//                addToSaveList3((ParseObject) category, saveList); //if category references yet unsaved items, add it to saveList in second round (after new parseObjects have been created)
//            });
////            return;
//        } else {
//
////        if (!category.needsSaving()) {
////            return;
////        }
////        if (saveList.contains(category)) {
////            return;
////        }
//            if (Config.TEST_BACKGR) {
//                Log.p("==========>>> DAO.saveItemListInBackground(elt=" + testShowMissingRefs(category) + ")");
//            }
//
//            if (!saveList.contains(category) && category.needsSaving()) {
//                addToSaveListNoChk3(category, saveList);
////        }
//
//                {
//                    List tasks = category.getListFull();
//                    if (listContainsUnsaved(tasks)) {
//                        category.setList(null);
//                        afterParseUpdate.add(() -> {
//                            category.setList(tasks);
//                            addToSaveList3((ParseObject) category, saveList); //if category references yet unsaved items, add it to saveList in second round (after new parseObjects have been created)
//                        });
//                    }
//                }
//
////        addToSaveListNoChk3(category);
//                ItemAndListCommonInterface owner = category.getOwner();
//                saveItemListNew3((ItemList) owner, saveList);
//
//                if (saveItems) {
//                    saveList3(category.getListFull(), saveList);
//                }
//
//                //save a filter before saving the element referencing it
//                FilterSortDef filter = category.getFilterSortDefN();
//                if (filter != null && !filter.isNoSave() && filter.needsSaving()) {
//                    if (filter.isUnsaved()) {
////                beforeParseUpdate.add(() -> {
//                        category.setFilterSortDef(null);
////                });
//                        afterParseUpdate.add(() -> {
//                            category.setFilterSortDef(filter);
//                            addToSaveList3((ParseObject) category, saveList);
//                        });
//                    } else {
//                        addToSaveList3(filter, saveList);
//                    }
//                }
//
//                { //WORKSLOTS
//                    List<WorkSlot> listWorkSlots = category.getWorkSlotsFromParseN();
//                    if (listWorkSlots != null && !listWorkSlots.isEmpty()) {
//                        if (listContainsUnsaved((List) listWorkSlots)) {
////                    beforeParseUpdate.add(() -> {
//                            category.setWorkSlotsInParse(null);
////                    });
//                            afterParseUpdate.add(() -> {
//                                category.setWorkSlotsInParse(listWorkSlots);
//                                addToSaveList3((ParseObject) category, saveList);
//                            });
//                        }
//                    }
//                }
//            }
//        }
//    }
//</editor-fold>
    private void saveCategoryNew3(Category category, boolean saveItems, List saveList, List<Runnable> afterParseUpdate) {
//        saveItemListNew3(itemList, false, saveList);
//    }
//
//    private void saveItemListNew3(ItemList itemList, boolean saveItemsAlwaysFalse, List saveList) {
        ASSERT.that(!category.isNoSave(),
                () -> "itemList is noSave");

        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveItemListInBackground(elt=" + testShowMissingRefs(category) + ")");
        }

//        if (!saveList.contains(category)) {
//            if (!category.isUnsaved()) {
//                //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
//                afterParseUpdate.add(() -> addToSaveList3(category, saveList));
//            } else {
        if (category.isDirty() && !processedList.contains(category)) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(category, saveList);
            addToProcessed3(category);
//        if (category.isDirty()) {//!repeatRule.isUnsaved()) {
////            addToSaveList3(category, saveList);
////            addToProcessed3(category);
            boolean isUnsaved = category.isNotCreated();
            boolean referencesUnsavedParseObjects = false;

            { //OWNER
                ItemAndListCommonInterface owner = category.getOwner();
                if (owner != null) {// && owner.needsSaving()) {
                    saveItemListNew3((ItemList) owner, saveList, afterParseUpdate);
                    if (owner.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            category.setOwner(null);
                            //after owner has been saved, then add it back as owner and save item with owner
                            afterParseUpdate.add(() -> {
                                category.setOwner(owner);
                            });
                        }
                    }
                }
            }

            { //FILTER
                //save a filter before saving the element referencing it
                FilterSortDef filter = category.getFilterSortDefN();
                if (filter != null && !filter.isNoSave()) {// && filter.getObjectIdP() == null) {
                    saveFilterSortDefNew3(filter, saveList, afterParseUpdate);
                    if (filter.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            category.setFilterSortDef(null);
                            afterParseUpdate.add(() -> {
                                category.setFilterSortDef(filter);
                            });
                        }
                    }
                }
            }

            { //TASKS
                List<ParseObject> listTasks = category.getListFull();
                if (listTasks != null && !listTasks.isEmpty()) {
                    if (listContainsNotCreated(listTasks)) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            category.setList(null);
                            afterParseUpdate.add(() -> {
                                category.setList(listTasks);
                            });
                        }
                    }
                }
            }

            { //WORKSLOTS
                List<ParseObject> listWorkSlots = category.getWorkSlotsFromParseN();
                if (listWorkSlots != null && !listWorkSlots.isEmpty()) {
                    if (listContainsNotCreated(listWorkSlots, true)) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            category.setWorkSlotsInParse(null);
                            afterParseUpdate.add(() -> {
                                category.setWorkSlotsInParse(listWorkSlots);
                            });
                        }
                    }
                }
            }

//                if (referencesUnsavedParseObjects) {
//                    afterParseUpdate.add(() -> addToSaveList3((ParseObject) category, saveList));
//                } else {
//                    addToSaveList3((ParseObject) category, saveList);
//                }
//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3(category, saveList);
//            } else {
//                if (category.isUnsaved()) {
//                    addToSaveList3(category, saveList);
//                }
//                afterParseUpdate.add(() -> addToSaveList3(category, saveList));
//            }
//            if (isUnsaved) {
//                if (referencesUnsavedParseObjects) {
//                    addToSaveList3(category, saveList);
//                    afterParseUpdate.add(() -> addToSaveList3(category, saveList));
//                } else {
//                    addToSaveList3(category, saveList);
//                }
//            } else { //already saved
//                afterParseUpdate.add(() -> addToSaveList3(category, saveList));
//            }
            savingOrder(category, isUnsaved, referencesUnsavedParseObjects, saveList, afterParseUpdate);
//            if (referencesUnsavedParseObjects) {
//                afterParseUpdate.add(() -> addToSaveList3(category, saveList));
//            }
        }
    }

    private void savingOrder(ParseObject parseObject, boolean isNotCreated, boolean referencesUnsavedParseObjects, List saveList, List<Runnable> afterParseUpdate) {
        if (false) {
            addToSaveList3(parseObject, saveList); //enough to add it here, it will be saved in first round if dirty, and again in second round if still dirty (dirty because references to previously uncreated object were added back again)
        } else {
            if (isNotCreated) {
                if (referencesUnsavedParseObjects) {
                    addToSaveList3(parseObject, saveList);
                    afterParseUpdate.add(() -> addToSaveList3(parseObject, saveList)); //save again after references to uncreated elements have been restored
                } else {
                    addToSaveList3(parseObject, saveList);
                }
            } else { //already created
                if (referencesUnsavedParseObjects) { //save *after* the uncreated referenced objects are created
                    afterParseUpdate.add(() -> addToSaveList3(parseObject, saveList));
                } else {
                    addToSaveList3(parseObject, saveList);
                }
            }
        }

    }
//    private void saveRepeatRuleNew3(RepeatRuleParseObject repeatRule, List saveList) {
//        saveRepeatRuleNew3(repeatRule, true, saveList);
//    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveRepeatRuleNew3(RepeatRuleParseObject repeatRule, boolean saveInstances, List saveList) {
//        //optimization: when there are no new repeat instances, getListOfUndoneInstances will run through the potentially very long list of past instances -> possible to assume that the *last* item will the unsaved one?!
//        if (false && (listContainsUnsaved((List) repeatRule.getListOfUndoneInstances()) || listContainsUnsaved((List) repeatRule.getListOfDoneInstances()))) {
////            saveList.remove(category); //not needed, should never be added to saveList
//            ASSERT.that(!saveList.contains(repeatRule), () -> "saveList already contains repeatRule=" + repeatRule + ", saveList=" + saveList);
//            afterParseUpdate.add(() -> {
//                addToSaveList3((ParseObject) repeatRule, saveList); //if category references yet unsaved items, add it to saveList in second round (after new parseObjects have been created)
//            });
//            return;
//        }
////        if (saveList.contains(repeatRule)) {
////            return;
////        }
////        if (!repeatRule.needsSaving()) {
////            return;
////        }
//
////        addToSaveListNoChk3(repeatRule);
//        if (Config.TEST_BACKGR) {
//            Log.p("==========>>> DAO.saveRepeatRuleInBackground(" + testShowMissingRefs(repeatRule) + "), ");
//        }
//        if (!saveList.contains(repeatRule) && repeatRule.needsSaving()) {
//            addToSaveListNoChk3(repeatRule, saveList);
////        }
//            // saving a (completely new) repeatRule requires saving in a particular order to avoid problems with references to unsaved parseObjects:
//            //save RR first, *without* the potentially unsaved repeat instances
//
//            //UNDONE INSTANCES
//            {
//                List undoneInstances = repeatRule.getListOfUndoneInstances();
//                if (undoneInstances != null && !undoneInstances.isEmpty()) {
//                    if (listContainsUnsaved(undoneInstances, true)) {
//                        if (saveInstances) {
//                            saveList3(undoneInstances, saveList, true);
//                        }
////                    beforeParseUpdate.add(() -> {
//                        repeatRule.setListOfUndoneInstances(null);
////                    });
//                        afterParseUpdate.add(() -> {
//                            repeatRule.setListOfUndoneInstances(undoneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
//                            addToSaveList3(repeatRule, saveList);
//                        });
//                    }
//                }
//            }
//
//            //DONE INSTANCES
//            {
//                List doneInstances = repeatRule.getListOfDoneInstances();
//                if (doneInstances != null && !doneInstances.isEmpty()) {
//                    if (listContainsUnsaved(doneInstances, true)) {
//                        if (saveInstances) {
//                            saveList3(doneInstances, saveList, true);
//                        }
////                    beforeParseUpdate.add(() -> {
//                        repeatRule.setListOfDoneInstances(null);
////                    });
//                        afterParseUpdate.add(() -> {
//                            repeatRule.setListOfDoneInstances(doneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
//                            addToSaveList3(repeatRule, saveList);
//                        });
//                    }
//                }
//            }
//
//        }
//    }
//</editor-fold>
//    private void saveRepeatRuleNew3(RepeatRuleParseObject repeatRule, boolean saveInstances, List saveList) {

    private void saveRepeatRuleNew3(RepeatRuleParseObject repeatRule, List saveList, List<Runnable> afterParseUpdate) {
        boolean saveInstances = true;

        // saving a (completely new) repeatRule requires saving in a particular order to avoid problems with references to unsaved parseObjects:
        //save RR first, *without* the potentially unsaved repeat instances
//        if (!saveList.contains(repeatRule)) {
        Collection<ParseObject> needsSaving = repeatRule.getNeedsSaving(); //TODO!!! move this code to MyParseObject?!
        if (needsSaving != null) {
            for (ParseObject p : needsSaving) {
                addToSaveList3(p, saveList);
            }
        }
        if (repeatRule.isDirty() && !processedList.contains(repeatRule)) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(repeatRule, saveList);
            addToProcessed3(repeatRule);
//        if (repeatRule.isDirty()) {//!repeatRule.isUnsaved()) {
////            addToSaveList3(repeatRule, saveList);
////            addToProcessed3(repeatRule);
            boolean isNotCreated = repeatRule.isNotCreated();
            //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
//                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//            } else {
            boolean referencesUnsavedParseObjects = false;

            { //UNDONE INSTANCES
                List undoneInstances = repeatRule.getListOfUndoneInstances();
                if (undoneInstances != null && !undoneInstances.isEmpty()) {
                    saveList3(undoneInstances, saveList, afterParseUpdate, true);
                    if (listContainsNotCreated(undoneInstances, true)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            repeatRule.setListOfUndoneInstances(null);
                            afterParseUpdate.add(() -> {
                                repeatRule.setListOfUndoneInstances(undoneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
                            });
                        }
                    }
                }
            }

            { //DONE INSTANCES
                List doneInstances = repeatRule.getListOfDoneInstances();
                if (doneInstances != null && !doneInstances.isEmpty()) {
                    saveList3(doneInstances, saveList, afterParseUpdate, true);
                    if (listContainsNotCreated(doneInstances, true)) {
                        referencesUnsavedParseObjects = true;
                        if (isNotCreated) {
                            repeatRule.setListOfDoneInstances(null);
                            afterParseUpdate.add(() -> {
                                repeatRule.setListOfDoneInstances(doneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
                            });
                        }
                    }
                }
            }
//<editor-fold defaultstate="collapsed" desc="comment">
//           if( repeatRule.isUnsaved())
//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3((ParseObject) repeatRule, saveList);
//            } else {
//                afterParseUpdate.add(() -> addToSaveList3((ParseObject) repeatRule, saveList));
//            }
//           if( repeatRule.isUnsaved()&&referencesUnsavedParseObjects) {
//                addToSaveList3((ParseObject) repeatRule, saveList);
//            } else {
//                afterParseUpdate.add(() -> addToSaveList3((ParseObject) repeatRule, saveList));
//            }
//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3(repeatRule, saveList);
//            } else {
//                if (repeatRule.isUnsaved()) {
//                    addToSaveList3(repeatRule, saveList);
//                }
//                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//            }
//</editor-fold>
//            if (false) {
//                if (isNotCreated) {
//                    if (referencesUnsavedParseObjects) {
//                        addToSaveList3(repeatRule, saveList);
//                        afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//                    } else {
//                        addToSaveList3(repeatRule, saveList);
//                    }
//                } else { //already created
//                    if (referencesUnsavedParseObjects) {
//                        afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//                    } else {
//                        addToSaveList3(repeatRule, saveList);
//                    }
////            if (referencesUnsavedParseObjects) {
////                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
////            }
//                }
//            }
            savingOrder(repeatRule, isNotCreated, referencesUnsavedParseObjects, saveList, afterParseUpdate);
//        }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    private void saveRepeatRuleNew3TST(RepeatRuleParseObject repeatRule, List saveList) {
//        boolean saveInstances = true;
//
//        // saving a (completely new) repeatRule requires saving in a particular order to avoid problems with references to unsaved parseObjects:
//        //save RR first, *without* the potentially unsaved repeat instances
////        if (!saveList.contains(repeatRule)) {
//        if (repeatRule.isDirty() && !processedList.contains(repeatRule)) {//!repeatRule.isUnsaved()) {
////            addToSaveList3(repeatRule, saveList);
//            addToProcessed3(repeatRule);
//            boolean isNotCreated = repeatRule.isNotCreated();
//            //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
////                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
////            } else {
//            boolean referencesUnsavedParseObjects = false;
//
//            { //UNDONE INSTANCES
//                List undoneInstances = repeatRule.getListOfUndoneInstances();
//                if (undoneInstances != null && !undoneInstances.isEmpty()) {
//                    saveList3(undoneInstances, saveList, true);
//                    if (isNotCreated) {
//                        if (listContainsNotCreated(undoneInstances, true)) {
//                            repeatRule.setListOfUndoneInstances(null);
//                            addToSaveList3(repeatRule, saveList);
//                            afterParseUpdate.add(() -> {
//                                repeatRule.setListOfUndoneInstances(undoneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
//                                addToSaveList3(repeatRule, saveList);
//                            });
//                        } else {
//                            addToSaveList3(repeatRule, saveList);
//                        }
//                    } else { //isCreated
//                        if (listContainsNotCreated(undoneInstances, true)) {
//                            afterParseUpdate.add(() -> {
//                                addToSaveList3(repeatRule, saveList);
//                            });
//                        } else {
//                            addToSaveList3(repeatRule, saveList);
//                        }
//
//                    }
//                }
//            }
////<editor-fold defaultstate="collapsed" desc="comment">
//            { //DONE INSTANCES
//                List doneInstances = repeatRule.getListOfDoneInstances();
//                if (doneInstances != null && !doneInstances.isEmpty()) {
//                    if (isNotCreated && listContainsNotCreated(doneInstances, true)) {
//                        referencesUnsavedParseObjects = true;
////                        if (saveInstances) {
//                        saveList3(doneInstances, saveList, true);
////                        }
////                    beforeParseUpdate.add(() -> {
//                        repeatRule.setListOfDoneInstances(null);
////                    });
//                        afterParseUpdate.add(() -> {
//                            repeatRule.setListOfDoneInstances(doneInstances); //only set the set when there was a list (handle cxase where one RR instances are set to null multiple times
////                            addToSaveList3(repeatRule, saveList);
//                        });
//                    }
//                }
//            }
////</editor-fold>
////<editor-fold defaultstate="collapsed" desc="comment">
////           if( repeatRule.isUnsaved())
////            if (!referencesUnsavedParseObjects) {
////                addToSaveList3((ParseObject) repeatRule, saveList);
////            } else {
////                afterParseUpdate.add(() -> addToSaveList3((ParseObject) repeatRule, saveList));
////            }
////           if( repeatRule.isUnsaved()&&referencesUnsavedParseObjects) {
////                addToSaveList3((ParseObject) repeatRule, saveList);
////            } else {
////                afterParseUpdate.add(() -> addToSaveList3((ParseObject) repeatRule, saveList));
////            }
////            if (!referencesUnsavedParseObjects) {
////                addToSaveList3(repeatRule, saveList);
////            } else {
////                if (repeatRule.isUnsaved()) {
////                    addToSaveList3(repeatRule, saveList);
////                }
////                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
////            }
////</editor-fold>
//            if (isNotCreated) {
//                if (referencesUnsavedParseObjects) {
//                    addToSaveList3(repeatRule, saveList);
//                    afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//                } else {
//                    addToSaveList3(repeatRule, saveList);
//                }
//            } else { //already created
//                if (referencesUnsavedParseObjects) {
//                    afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//                } else {
//                    addToSaveList3(repeatRule, saveList);
//                }
//            }
////            if (referencesUnsavedParseObjects) {
////                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
////            }
//        }
////        }
//    }
//</editor-fold>
    private void saveWorkSlotNew3(WorkSlot workSlot, List saveList, List<Runnable> afterParseUpdate) {
//        if (saveList.contains(workSlot)) {
//            return;
//        }
        if (Config.TEST_BACKGR) {
            Log.p("==========>>> DAO.saveWorkSlotInBackground(" + testShowMissingRefs(workSlot) + "), ");
        }

//        if (!saveList.contains(workSlot) && workSlot.needsSaving()) {
//            addToSaveListNoChk3(workSlot, saveList);
        if (workSlot.isDirty() && !processedList.contains(workSlot)) {//!repeatRule.isUnsaved()) {
//            addToSaveList3(workSlot, saveList);
            addToProcessed3(workSlot);
//        if (workSlot.isDirty()) {//!repeatRule.isUnsaved()) {
////            addToSaveList3(workSlot, saveList);
////            addToProcessed3(workSlot);
            boolean isUnsaved = workSlot.isNotCreated();
            //save in second round (once we're sure any referenced unsaved elements have been created and can be referenced
//                afterParseUpdate.add(() -> addToSaveList3(repeatRule, saveList));
//            } else {
            boolean referencesUnsavedParseObjects = false;
//        }

//        if (workSlot.isDirty()) {
//            addToSaveList3(workSlot);
//        }
            //OWNER
            ItemAndListCommonInterface owner = workSlot.getOwner();
            if (owner != null) {
                if (owner instanceof Item) {
                    saveItemNew3((Item) owner, saveList, afterParseUpdate);
                } else if (owner instanceof Category) {
                    saveCategoryNew3((Category) owner, saveList, afterParseUpdate);
                } else if (owner instanceof ItemList) {
                    saveItemListNew3((ItemList) owner, saveList, afterParseUpdate);
                }
                if (owner.isNotCreated()) {
                    referencesUnsavedParseObjects = true;
                    if (isUnsaved) {
                        workSlot.setOwner(null);
                        afterParseUpdate.add(() -> {
                            workSlot.setOwner(owner);
                        });
                    }
                }
            }

            //SOURCE
            {
                WorkSlot source = workSlot.getSource(); //source of Item is necessarily an Item itself
                if (source != null && source.needsSaving()) {
                    saveWorkSlotNew3(source, saveList, afterParseUpdate);
                    if (source.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            workSlot.setSource(null);
                            afterParseUpdate.add(() -> {
                                workSlot.setSource(source);
                            });
                        }
                    }
                }
            }

            //REPEATRULE
            {
                RepeatRuleParseObject repeatRule = workSlot.getRepeatRuleN();
                if (repeatRule != null && repeatRule.needsSaving()) {
                    saveRepeatRuleNew3(repeatRule, saveList, afterParseUpdate);
                    if (repeatRule.isNotCreated()) {
                        referencesUnsavedParseObjects = true;
                        if (isUnsaved) {
                            workSlot.setRepeatRuleInParse(null);
                            afterParseUpdate.add(() -> {
                                workSlot.setRepeatRuleInParse(repeatRule);
                            });
                        }
                    }
                }
            }

//            if (!referencesUnsavedParseObjects) {
//                addToSaveList3(workSlot, saveList);
//            } else {
//                if (workSlot.isUnsaved()) {
//                    addToSaveList3(workSlot, saveList);
//                }
//                afterParseUpdate.add(() -> addToSaveList3(workSlot, saveList));
//            }
//            if (isUnsaved) {
//                if (referencesUnsavedParseObjects) {
//                    addToSaveList3(workSlot, saveList);
//                    afterParseUpdate.add(() -> addToSaveList3(workSlot, saveList));
//                } else {
//                    addToSaveList3(workSlot, saveList);
//                }
//            } else { //already saved
//                afterParseUpdate.add(() -> addToSaveList3(workSlot, saveList));
//            }
            savingOrder(workSlot, isUnsaved, referencesUnsavedParseObjects, saveList, afterParseUpdate);

//            if (referencesUnsavedParseObjects) {
//                afterParseUpdate.add(() -> addToSaveList3(workSlot, saveList));
//            }
        }
    }

    private void saveNewImpl(Collection<ParseObject> parseObjects, boolean triggerSave, boolean waitForCompletion) {
        if (parseObjects == null || parseObjects.isEmpty()) {
            if (Config.TEST_BACKGR) {
                Log.p("==========>>> DAO.saveCategoryInBackground() SKIPPING null/empty objects =" + parseObjects);
            }
            return;
        }

        for (ParseObject p : parseObjects) {
//            if ((!(p instanceof ItemAndListCommonInterface) || !((ItemAndListCommonInterface) p).isNoSave()) && !toSaveList.contains(p)) {
//            if (Config.TEST && p instanceof ItemAndListCommonInterface && ((ItemAndListCommonInterface) p).isNoSave()) {
//                ASSERT.that("DAO.saveNewImpl called with noSave object - should any of it's referenced objects be saved?! p=" + p);
//            }
//            if ((!(p instanceof ItemAndListCommonInterface) || !((ItemAndListCommonInterface) p).isNoSave())) {
            if (p instanceof ItemAndListCommonInterface) {
                if (((ItemAndListCommonInterface) p).isNoSave()) {
                    ASSERT.that(!Config.TEST, "DAO.saveNewImpl called with noSave object - should any of it's referenced objects be saved?! p=" + p);
                } else {
//                toSaveList.add(p);
                    ((ItemAndListCommonInterface) p).onSave(); //update any objects impacted byt this one's changeEvents
                    addToSaveList3(p, toSaveList);
                }
            } else {
                addToSaveList3(p, toSaveList);
            }
        }

        if (triggerSave && !toSaveList.isEmpty()) {
            triggerParseUpdate(waitForCompletion || !MyPrefs.backgroundSave.getBoolean());
        }
    }

    void saveNewTriggerUpdate() {
//        triggerParseUpdate();
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    void saveNewTriggerUpdate3XXX() {
//        saveNewTriggerUpdate();
//    }

//    public void saveNew(Collection<ParseObject> parseObjects) {
//        if (parseObjects == null || parseObjects.isEmpty()) {
//            return;
//        }
//        saveNewImpl(parseObjects, null, false);
//    }
//
//    public void saveNew(ParseObject anyParseObject) {
//        if (anyParseObject != null) {
//            saveNew(anyParseObject, null, false);
//        }
//    }
//
//    private void saveNew(Runnable postSaveAction, boolean triggerSave, ParseObject... parseObjects) {
//        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), postSaveAction, triggerSave);
//    }
//
//    public void saveNew(ParseObject... parseObjects) {
//        saveNew(null, false, parseObjects);
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
//    public List<WorkSlot> getWorkSlots(Date startDate) {
    public ItemList getWorkSlots(Date startDate) {
        return getWorkSlots(startDate, new Date(MyDate.MAX_DATE));
    }

    public ItemList getWorkSlots(Date startDate, boolean useCacheIfNonEmpty) {
        return getWorkSlots(startDate, new Date(MyDate.MAX_DATE), useCacheIfNonEmpty);
    }

//    private WorkSlotList getWorkSlots(Date startDate, Date endDate) {
    /**
     * return sorted on startDate
     *
     * @param startDate
     * @param endDate
     * @return
     */
//    public List<WorkSlot> getWorkSlots(Date startDate, Date endDate) {
    public ItemList getWorkSlots(Date startDate, Date endDate) {
        return getWorkSlots(startDate, endDate, false);
    }

    public ItemList getWorkSlots(Date startDate, Date endDate, boolean useCacheIfNonEmpty) {

        List<WorkSlot> list = null;
        if (useCacheIfNonEmpty) {
            list = new ArrayList();
            Hashtable cachedWS = cacheWorkSlots.getCacheContent();
            Enumeration<WorkSlot> listOfWS = cachedWS.elements();
            while (listOfWS.hasMoreElements()) {
                WorkSlot workSlot = listOfWS.nextElement();
                if (workSlot.hasDurationInInterval(startDate.getTime(), endDate.getTime())) {
                    list.add(workSlot);
                }
            }
            if (!list.isEmpty()) {
                WorkSlot.sortWorkSlotList(list); //optimization: sort list *before* filtering and eliminate all outdated/after endDate by removing the sequence
                return new ItemList(WORKSLOTS.getTitle(), list, true);
            }
        }

        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
//        query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_START_TIME, startDate); //enough to search for endTime later than Now
        query.whereGreaterThanOrEqualTo(WorkSlot.PARSE_END_TIME, startDate); //enough to search for endTime later than Now
//        query.whereLessThan(WorkSlot.PARSE_START_TIME, endDate);
        query.whereLessThan(WorkSlot.PARSE_START_TIME, endDate);
        query.addAscendingOrder(WorkSlot.PARSE_START_TIME); //sort on startTime
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt());
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);

//        query.selectKeys(new ArrayList()); //just get search result, no data (these are cached)
        query.selectKeys(Arrays.asList(ParseObject.GUID));
//        WorkSlotList results = null;// = new WorkSlotList();

        try {
            list = query.find();
//            results = new WorkSlotList(list);
//            results = new WorkSlotList(list);
        } catch (ParseException ex) {
            Log.e(ex);
        }
        list = fetchListElementsIfNeededReturnCachedIfAvail(list);
        ItemList workSlotList = new ItemList(list);
        workSlotList.setItemListIcon(ScreenType.WORKSLOTS.getIcon());
//        return new ItemList(list);
        return new ItemList(WORKSLOTS.getTitle(), list, true);
//        return results;
//        return new ArrayList();
    }

    /**
     *
     * get all workslots (including past/expired) ones
     *
     * @return null if no workslots defined
     */
//    public List<WorkSlot> getAllWorkSlotsFromParse() {
    List<WorkSlot> getAllWorkSlotsFromParse() {
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
//        return new WorkSlotList(null, fetchListElementsIfNeededReturnCachedIfAvail(results), true);
        return results;
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

    /**
     * retrieve items with nextComing alarms defined. NB. The next alarm field
     * may be outdated (in the past) if the item has not been updated after time
     * caught up with a previous future alarm. So, every item must be tried
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
//<editor-fold defaultstate="collapsed" desc="comment">
//        if (false) {
//            query.whereLessThan(Item.PARSE_NEXTCOMING_ALARM, new Date(MyDate.currentTimeMillis() + MyPrefs.alarmDaysAheadToFetchFutureAlarms.getInt() * MyDate.DAY_IN_MILLISECONDS)); //don't search more than 30 days ahead in the future // NO real reason to limit the number
//        } else {
//            query.whereExists(Item.PARSE_NEXTCOMING_ALARM);
//            query.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on date
//        }
//        if (false) {
//            query.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on the alarm field
//        }
//        if (false) { //probably no reason not to simply fetch all default fields (and no need for referenced parseObjects' guid?!)
//            if (backgroundFetch) {
//                Log.p("getItemsWithNextcomingAlarms w *backgroundFetch* - is memoryCache empty?! cache.size=" + DAO.getInstance().cache.getCurrentMemoryCacheSize());
//                if (false) { //just get all fields for simplicity (avoid some data is missing //optimization:
//                    query.selectKeys(new ArrayList(Arrays.asList(ParseObject.GUID, Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE,
//                            Item.PARSE_WAITING_ALARM_DATE, Item.PARSE_SNOOZE_DATE))); // just fetchFromCacheOnly the data needed to set alarms //TODO!! investigate if only fetching the relevant fields is a meaningful optimziation (check that only the fetched fields are used!)
//                }
//            } else {
////            query.selectKeys(new ArrayList()); // just fetch minimu
//                query.selectKeys(Arrays.asList(ParseObject.GUID)); // just fetch minimu
//            }
//            query.selectKeys(Arrays.asList(
//                    TemplateList.PARSE_ITEMS + "." + ParseObject.GUID,
//                    TemplateList.PARSE_ITEMS + "." + ParseObject.GUID,
//                    TemplateList.PARSE_ITEMS + "." + ParseObject.GUID
//            ));
//        }
//</editor-fold>
        query.whereExists(Item.PARSE_NEXTCOMING_ALARM);
        query.addAscendingOrder(Item.PARSE_NEXTCOMING_ALARM); //sort on date

        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        query.setLimit(maxNumberItemsToRetrieve);
        List<Item> itemWithNextComingAlarms;
        int numberOfItemsRetrieved;

        do { //repeat until we have at least some future results (if all retrieved results had nextcoming data in the past)
//        List<Item> results = null;
            itemWithNextComingAlarms = null;
            try {
                itemWithNextComingAlarms = query.find();
//            fetchAllElementsInSublist(results); //NO - this may be called while app is not active, so cahce not loaded
//            return results;
            } catch (ParseException ex) {
                Log.e(ex);
            }
            if (true || !backgroundFetch) { //app will always be started and all objects in memory
                fetchListElementsIfNeededReturnCachedIfAvail(itemWithNextComingAlarms); //only get in cache when not started in background
            }
            numberOfItemsRetrieved = itemWithNextComingAlarms.size();

            //remove all items where getNextcomingAlarmDateD is no longer valid (time has passed and getNextcomingAlarm() returns another later value or null - meaning no more alarms)
            List<Item> expired = new ArrayList();
            //Solution from http://stackoverflow.com/questions/122105/what-is-the-best-way-to-filter-a-java-collection :
            Iterator<Item> it = itemWithNextComingAlarms.iterator();
            Date nextAlarmDateInParse;
//            Date futureDate;
            Date latestAlarmDate = new Date(MyDate.MIN_DATE); //time of the last alarm received from Parse server
            while (it.hasNext()) {
                Item itemWithNextComingAlarm = it.next();
                nextAlarmDateInParse = itemWithNextComingAlarm.getNextcomingAlarmFromParseN(); //should never be null because we've fetched items only with next alarms
                ASSERT.that(nextAlarmDateInParse != null, "all items with next alarm should have a defined nextAlarmDate, item=" + itemWithNextComingAlarm);
                //keep track of the latest next alarm in the search result
                if (nextAlarmDateInParse != null && nextAlarmDateInParse.getTime() > latestAlarmDate.getTime()) {
                    latestAlarmDate = nextAlarmDateInParse; //gather latest alarm date as we iterate through the items
                }
                Date nextComingAlarmDateN = itemWithNextComingAlarm.getNextcomingAlarmN(); //may return null if alarm has expired since
//                if (nextComingAlarmDateN == null || (nextAlarmDateInParse == null && nextComingAlarmDateN.getTime() != nextAlarmDateInParse.getTime())) { //if there's no longer a future alarm, or it has changed, add to expired for an update
                if (nextComingAlarmDateN == null || nextComingAlarmDateN.getTime() != nextAlarmDateInParse.getTime()) { //if there's no longer a future alarm, or it has changed, add to expired for an update
                    expired.add(itemWithNextComingAlarm);
                    it.remove();
                }
            }

            if (false) { //not necessary anymore because done above
                fetchListElementsIfNeededReturnCachedIfAvail(expired); //ensure all expired items point to cache before updating and potentially saving them below
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
                Date newFirstAlarm = expItem.getNextcomingAlarmFromParseN(); //optimization: this statement and next both call Item.getAllFutureAlarmRecordsSorted() which is a bit expensive
                updated.add(expItem); //save for a ParseServer update whether now null or with new value
                if (newFirstAlarm != null && newFirstAlarm.getTime() <= latestAlarmDate.getTime()) {
                    itemWithNextComingAlarms.add(expItem); //add the updated Items which do have a future alarm within the same interval as the other alarms retrieved (less than lastAlarmDate)
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
//            saveNew(updated);
//            triggerParseUpdate();
            saveToParseNow(updated); //save in background
        } while (!backgroundFetch && (itemWithNextComingAlarms.isEmpty() && maxNumberItemsToRetrieve == numberOfItemsRetrieved)); //repeat in case every retrieved alarm was expired and we retrieved the maximum number (meaning there are like more alarms to retrieve)
//        results.addAll(updated); //add the updated ones to results
        //sort the results
        if (itemWithNextComingAlarms != null && !itemWithNextComingAlarms.isEmpty()) {
            Collections.sort(itemWithNextComingAlarms, (object1, object2) -> {
                if (((Item) object1).getNextcomingAlarmFromParseN() == null) {
                    return -1;
                }
                if (((Item) object2).getNextcomingAlarmFromParseN() == null) {
                    return 1;
                }
                return FilterSortDef.compareDate(object1.getNextcomingAlarmFromParseN(), object2.getNextcomingAlarmFromParseN());
            });
        }
        return itemWithNextComingAlarms;
    }

    public List<Item> getItemsWithAlarms(int maxNumberItemsToRetrieve, Date timeAfterWhichToFindNextItemWithAlarm, Date timeAfterWhichToFindNextItemWithWaitingAlarm,
            Date timeAfterWhichToFindNextItemWithSnoozedAlarm, int daysAhead) {
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
            fetchListElementsIfNeededReturnCachedIfAvail(results); //YES - seems app is started before calling this!! NO - this may be called while app is not active, so cahce not loaded 
            return results;
        } catch (ParseException ex) {
            Log.e(ex);
        }
        return null;
    }

    private void setupItemQueryWithIndirectAndGuids(ParseQuery<Item> query) {
        query.include(Item.PARSE_OWNER_ITEM);
        query.include(Item.PARSE_OWNER_LIST);
        query.include(Item.PARSE_OWNER_TEMPLATE_LIST);
        query.include(Item.PARSE_SUBTASKS);
        query.include(Item.PARSE_CATEGORIES);
        query.include(Item.PARSE_FILTER_SORT_DEF);
        query.include(Item.PARSE_REPEAT_RULE);
        query.include(Item.PARSE_WORKSLOTS);
        query.include(Item.PARSE_INTERRUPTED_TASK);
        query.include(Item.PARSE_SOURCE);
        query.include(Item.PARSE_DEPENDS_ON_TASK);
        query.selectKeys(Arrays.asList(
                Item.PARSE_OWNER_ITEM + "." + ParseObject.GUID,
                Item.PARSE_OWNER_LIST + "." + ParseObject.GUID,
                Item.PARSE_OWNER_TEMPLATE_LIST + "." + ParseObject.GUID,
                Item.PARSE_SUBTASKS + "." + ParseObject.GUID,
                Item.PARSE_CATEGORIES + "." + ParseObject.GUID,
                Item.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                Item.PARSE_REPEAT_RULE + "." + ParseObject.GUID,
                Item.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                Item.PARSE_INTERRUPTED_TASK + "." + ParseObject.GUID,
                Item.PARSE_SOURCE + "." + ParseObject.GUID,
                Item.PARSE_DEPENDS_ON_TASK + "." + ParseObject.GUID,
                //
                ParseObject.GUID,
                //
                Item.PARSE_ACTUAL_EFFORT,
                Item.PARSE_ACTUAL_EFFORT_TASK_ITSELF,
                Item.PARSE_ALARM_DATE,
                Item.PARSE_CHALLENGE,
                Item.PARSE_COMMENT,
                Item.PARSE_COMPLETED_DATE,
                Item.PARSE_DATE_WHEN_SET_WAITING,
                Item.PARSE_DELETED_DATE,
                Item.PARSE_DREAD_FUN_VALUE,
                Item.PARSE_DUE_DATE,
                Item.PARSE_EARNED_VALUE,
                Item.PARSE_EARNED_VALUE_PER_HOUR,
                Item.PARSE_EDITED_DATE,
                Item.PARSE_EFFORT_ESTIMATE,
                Item.PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF,
                Item.PARSE_EXPIRES_ON_DATE,
                Item.PARSE_HIDE_UNTIL_DATE,
                Item.PARSE_IMPORTANCE,
                Item.PARSE_IMPORTANCE_URGENCY_VIRT,
                Item.PARSE_INHERIT_ENABLED,
                Item.PARSE_INTERRUPT_OR_INSTANT_TASK,
                Item.PARSE_NEXTCOMING_ALARM,
                Item.PARSE_PRIORITY,
                Item.PARSE_REMAINING_EFFORT_FOR_TASK_ITSELF,
                Item.PARSE_REMAINING_EFFORT_TOTAL,
                Item.PARSE_RESTART_TIMER,
                Item.PARSE_SNOOZED_TYPE,
                Item.PARSE_SNOOZE_DATE,
                Item.PARSE_STARRED,
                Item.PARSE_TEMPLATE,
                //                Item.PARSE_UPDATED_AT, //NOT fetched explicitedly since always included
                //                Item.PARSE_CREATED_AT, //NOT fetched explicitedly since always included
                Item.PARSE_STARTED_ON_DATE,
                Item.PARSE_START_BY_DATE,
                Item.PARSE_STATUS,
                Item.PARSE_TEXT,
                //                Item.PARSE_TIMER_PAUSED_XXX,
                //                Item.PARSE_TIMER_STARTED_XXX,
                Item.PARSE_URGENCY,
                Item.PARSE_WAITING_ALARM_DATE,
                Item.PARSE_WAIT_UNTIL_DATE
        ));
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public List<Item> getItemsWithAlarmsInInterval(Date timeAfterWhichToFindNextItem, Date lastTimeForNextAlarm, int alarmMaxNumberItems) {
//        //TODO!!!! should this completely avoid cache to work even when launched when the app is NOT running?? Need to disable caching for backgroundFetch!!
//        //TODO possible to query on items where alarmTimes are stored in an array (e.g. get all items for which at least one alarmTime in the array falls within the searched interval)?
////        int alarmQueryLimit = 32;
//        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
////        setupStandardItemQuery(queryReminderAlarm);
//        queryReminderAlarm.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////<editor-fold defaultstate="collapsed" desc="comment">
////        queryReminderAlarm.setLimit(MyPrefs.alarmMaxNumberItemsForWhichToSetupAlarms.getInt());
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
////also need to avoid items that are cancelled
////        queryAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
////        queryReminderAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem, lastTimeForNextAlarm);
////</editor-fold>
//        queryReminderAlarm.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem);
//        queryReminderAlarm.whereLessThanOrEqualTo(Item.PARSE_ALARM_DATE, lastTimeForNextAlarm);
////        queryAlarm.addAscendingOrder(parseAlarmField); //sort on the alarm field //NOT necessary to sort?! (only reason could be to if we get more than the limit number back in which case some would be ignored)
//        queryReminderAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        queryReminderAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        queryReminderAlarm.whereDoesNotExist(Item.PARSE_TEMPLATE); //don't fetchFromCacheOnly any templates
////<editor-fold defaultstate="collapsed" desc="comment">
////        setupItemQueryNoTemplatesLimit1000(queryWaitingAlarm);
////        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
////also need to avoid items that are cancelled
////        queryWaitingAlarm.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
////        queryWaitingAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
////        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem, lastTimeForNextAlarm);
////</editor-fold>
//        queryWaitingAlarm.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem);
//        queryWaitingAlarm.whereLessThanOrEqualTo(Item.PARSE_WAITING_ALARM_DATE, lastTimeForNextAlarm);
//        queryWaitingAlarm.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//        queryWaitingAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
////        ParseQuery<Item> queryGetAllItemsWithAlarms = null;
//        try {
//            //        ParseQuery<Item> queryOr = ParseQuery<Item>.getOrQuery(new ArrayList(){queryAlarm, queryWaitingAlarm});
//            ParseQuery<Item> queryGetAllItemsWithAlarms = ParseQuery.getOrQuery(new ArrayList(Arrays.asList(queryReminderAlarm, queryWaitingAlarm)));
//
//            queryGetAllItemsWithAlarms.selectKeys(new ArrayList(Arrays.asList(
//                    Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
//
////            queryGetAllItemsWithAlarms.whereNotContainedIn(Item.PARSE_STATUS, new ArrayList(Arrays.asList(ItemStatus.DONE.toString(), ItemStatus.CANCELLED.toString()))); //item that are NOT DONE or CANCELLED
//            queryGetAllItemsWithAlarms.setLimit(alarmMaxNumberItems); //item that are NOT DONE or CANCELLED
////<editor-fold defaultstate="collapsed" desc="comment">
////            queryGetAllItemsWithAlarms.selectKeys(null); //just get search result, no data (these are cached)
////            setupItemQuery(queryGetAllItemsWithAlarms);
////            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.DONE.toString()); //item that are NOT DONE
////            //also need to avoid items that are cancelled
////            queryGetAllItemsWithAlarms.whereNotEqualTo(Item.PARSE_STATUS, ItemStatus.CANCELLED.toString()); //item that are NOT DONE
////            //TODO: need also to avoid items that are Waiting? No, it can be waiting, but you still want any alarm to work normally
////            queryGetAllItemsWithAlarms.addAscendingOrder(Item.PARSE_ALARM_DATE);
////            ArrayList<Item> results = new ArrayList<Item>(queryGetAllItemsWithAlarms.find());
////</editor-fold>
//            List<Item> results = queryGetAllItemsWithAlarms.find();
////            fetchAllElementsInSublist(results);
//            return results;
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
////<editor-fold defaultstate="collapsed" desc="comment">
////        query.setLimit(1);
////        ArrayList<Item> results = null;
////        try {
////        } catch (ParseException ex) {
////            Log.e(ex);
////        }
////</editor-fold>
//        return null;
//    }
//</editor-fold>
    /**
     * get the next item with an alarm (normal, waiting) strictly after (greater
     * than) the indicated time limit. Use with time of previous item alarm to
     * get the next one. Only called when cache is initialized.
     *
     * @param timeAfterWhichToFindNextItem
     * @return
     */
//<editor-fold defaultstate="collapsed" desc="comment">
//    public Item getNextItemWithAlarmXXX(Date timeAfterWhichToFindNextItem) {
//        ParseQuery<Item> queryReminderAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryReminderAlarm, true);
////        setupAlarmQuery(queryReminderAlarm, Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
//        queryReminderAlarm.whereGreaterThan(Item.PARSE_ALARM_DATE, timeAfterWhichToFindNextItem);
//        queryReminderAlarm.setLimit(1); //only return queryLimit first results (the queryLimit smallest alarms)
////        queryReminderAlarm.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
////        queryReminderAlarm.selectKeys(new ArrayList()); // just fetchFromCacheOnly the objectId - assumes items are cached already
////        queryReminderAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        ParseQuery<Item> queryWaitingAlarm = ParseQuery.getQuery(Item.CLASS_NAME);
//        setupItemQueryNotTemplateNotDeletedLimit10000(queryWaitingAlarm, true);
////        setupAlarmQuery(queryWaitingAlarm, Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem, new Date(0), 1);
//        queryWaitingAlarm.whereGreaterThan(Item.PARSE_WAITING_ALARM_DATE, timeAfterWhichToFindNextItem);
//        queryWaitingAlarm.setLimit(1); //only return queryLimit first results (the queryLimit smallest alarms)
////        queryWaitingAlarm.selectKeys(new ArrayList(Arrays.asList(Item.PARSE_TEXT, Item.PARSE_DUE_DATE, Item.PARSE_ALARM_DATE, Item.PARSE_WAITING_ALARM_DATE))); // just fetchFromCacheOnly the data needed to set alarms
////        queryWaitingAlarm.selectKeys(new ArrayList()); // just fetchFromCacheOnly the objectId - assumes items are cached already
////        queryWaitingAlarm.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//
//        Item nextItemWithAlarm = null;
//
//        try {
//            List<Item> resultsNextAlarm = queryReminderAlarm.find();
//            List<Item> resultsNextWaiting = queryWaitingAlarm.find();
//
//            if (resultsNextAlarm != null && resultsNextAlarm.size() >= 1) {
//                if (resultsNextWaiting != null && resultsNextWaiting.size() >= 1) {
//                    if (resultsNextAlarm.get(0).getAlarmDate().getTime() < resultsNextWaiting.get(0).getWaitingAlarmDate().getTime()) {
////                        return resultsNextAlarm.get(0);
//                        nextItemWithAlarm = resultsNextAlarm.get(0);
//
//                    } else {
////                        return resultsNextWaiting.get(0);
//                        nextItemWithAlarm = resultsNextWaiting.get(0);
//                    }
//                } else {
////                    return resultsNextAlarm.get(0);
//                    nextItemWithAlarm = resultsNextAlarm.get(0);
//                }
//            } else if (resultsNextWaiting != null && resultsNextWaiting.size() >= 1) {
////                return resultsNextWaiting.get(0);
//                nextItemWithAlarm = resultsNextWaiting.get(0);
//            };
//
//        } catch (ParseException ex) {
//            Log.e(ex);
//        }
//        ASSERT.that(cache != null, "cache must be initialized");
//
//        if (nextItemWithAlarm != null) {
////            nextItemWithAlarm = (Item) cache.get(nextItemWithAlarm.getObjectIdP());
//            nextItemWithAlarm = (Item) cacheGet(nextItemWithAlarm.getObjectIdP());
//        }
//        return nextItemWithAlarm;
//    }
//</editor-fold>
    //--------  CACHE DATA  -----------------------------------------------
    private List<Item> getAllItemsFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<Item> query = ParseQuery.getQuery(Item.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, now);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
        if (false) {
            query.include(Item.PARSE_OWNER_ITEM);
            query.include(Item.PARSE_OWNER_LIST);
            query.include(Item.PARSE_OWNER_TEMPLATE_LIST);
            query.include(Item.PARSE_SUBTASKS);
            query.include(Item.PARSE_CATEGORIES);
            query.include(Item.PARSE_FILTER_SORT_DEF);
            query.include(Item.PARSE_REPEAT_RULE);
            query.include(Item.PARSE_WORKSLOTS);
            query.include(Item.PARSE_INTERRUPTED_TASK);
            query.include(Item.PARSE_SOURCE);
            query.include(Item.PARSE_DEPENDS_ON_TASK);
            query.selectKeys(Arrays.asList(
                    Item.PARSE_OWNER_ITEM + "." + ParseObject.GUID,
                    Item.PARSE_OWNER_LIST + "." + ParseObject.GUID,
                    Item.PARSE_OWNER_TEMPLATE_LIST + "." + ParseObject.GUID,
                    Item.PARSE_SUBTASKS + "." + ParseObject.GUID,
                    Item.PARSE_CATEGORIES + "." + ParseObject.GUID,
                    Item.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                    Item.PARSE_REPEAT_RULE + "." + ParseObject.GUID,
                    Item.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                    Item.PARSE_INTERRUPTED_TASK + "." + ParseObject.GUID,
                    Item.PARSE_SOURCE + "." + ParseObject.GUID,
                    Item.PARSE_DEPENDS_ON_TASK + "." + ParseObject.GUID,
                    //
                    ParseObject.GUID,
                    //
                    Item.PARSE_ACTUAL_EFFORT,
                    Item.PARSE_ACTUAL_EFFORT_TASK_ITSELF,
                    Item.PARSE_ALARM_DATE,
                    Item.PARSE_CHALLENGE,
                    Item.PARSE_COMMENT,
                    Item.PARSE_COMPLETED_DATE,
                    Item.PARSE_DATE_WHEN_SET_WAITING,
                    Item.PARSE_DELETED_DATE,
                    Item.PARSE_DREAD_FUN_VALUE,
                    Item.PARSE_DUE_DATE,
                    Item.PARSE_EARNED_VALUE,
                    Item.PARSE_EARNED_VALUE_PER_HOUR,
                    Item.PARSE_EDITED_DATE,
                    Item.PARSE_EFFORT_ESTIMATE,
                    Item.PARSE_EFFORT_ESTIMATE_PROJECT_TASK_ITSELF,
                    Item.PARSE_EXPIRES_ON_DATE,
                    Item.PARSE_HIDE_UNTIL_DATE,
                    Item.PARSE_IMPORTANCE,
                    Item.PARSE_IMPORTANCE_URGENCY_VIRT,
                    Item.PARSE_INHERIT_ENABLED,
                    Item.PARSE_INTERRUPT_OR_INSTANT_TASK,
                    Item.PARSE_NEXTCOMING_ALARM,
                    Item.PARSE_PRIORITY,
                    Item.PARSE_REMAINING_EFFORT_FOR_TASK_ITSELF,
                    Item.PARSE_REMAINING_EFFORT_TOTAL,
                    Item.PARSE_RESTART_TIMER,
                    Item.PARSE_SNOOZED_TYPE,
                    Item.PARSE_SNOOZE_DATE,
                    Item.PARSE_STARRED,
                    Item.PARSE_TEMPLATE,
                    //                Item.PARSE_UPDATED_AT, //NOT fetched explicitedly since always included
                    //                Item.PARSE_CREATED_AT, //NOT fetched explicitedly since always included
                    Item.PARSE_STARTED_ON_DATE,
                    Item.PARSE_START_BY_DATE,
                    Item.PARSE_STATUS,
                    Item.PARSE_TEXT,
                    //                    Item.PARSE_TIMER_PAUSED_XXX,
                    //                    Item.PARSE_TIMER_STARTED_XXX,
                    Item.PARSE_URGENCY,
                    Item.PARSE_WAITING_ALARM_DATE,
                    Item.PARSE_WAIT_UNTIL_DATE
            ));
        } else {
            setupItemQueryWithIndirectAndGuids(query);
        }
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
//        if (Config.REPAIR_DATA) {
//            for (Item item : results) {
//                if (item.getOwner()== item) {
//                    item.setOwner(Inbox.getInstance());
//                }
//            }
//        }
        cacheList(results);
        return !results.isEmpty();
    }

    void setupWorkSlotQueryWithIndirectAndGuids(ParseQuery<WorkSlot> query) {
        query.include(WorkSlot.PARSE_ORIGINAL_SOURCE);
        query.include(WorkSlot.PARSE_OWNER_CATEGORY);
        query.include(WorkSlot.PARSE_OWNER_ITEM);
        query.include(WorkSlot.PARSE_OWNER_LIST);
        query.include(WorkSlot.PARSE_REPEAT_RULE);
        query.selectKeys(Arrays.asList(
                WorkSlot.PARSE_ORIGINAL_SOURCE + "." + ParseObject.GUID,
                WorkSlot.PARSE_OWNER_CATEGORY + "." + ParseObject.GUID,
                WorkSlot.PARSE_OWNER_ITEM + "." + ParseObject.GUID,
                WorkSlot.PARSE_OWNER_LIST + "." + ParseObject.GUID,
                WorkSlot.PARSE_REPEAT_RULE + "." + ParseObject.GUID,
                ParseObject.GUID,
                WorkSlot.PARSE_DURATION,
                WorkSlot.PARSE_END_TIME,
                WorkSlot.PARSE_START_TIME,
                WorkSlot.PARSE_TEXT
        ));
    }

    private List<WorkSlot> getAllWorkSlotsFromParse(Date afterDate, Date beforeDate) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<WorkSlot> query = ParseQuery.getQuery(WorkSlot.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, afterDate);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, beforeDate);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(Arrays.asList(ParseObject.GUID));
        if (false) {
            query.include(WorkSlot.PARSE_ORIGINAL_SOURCE);
            query.include(WorkSlot.PARSE_OWNER_CATEGORY);
            query.include(WorkSlot.PARSE_OWNER_ITEM);
            query.include(WorkSlot.PARSE_OWNER_LIST);
            query.include(WorkSlot.PARSE_REPEAT_RULE);
            query.selectKeys(Arrays.asList(
                    WorkSlot.PARSE_ORIGINAL_SOURCE + "." + ParseObject.GUID,
                    WorkSlot.PARSE_OWNER_CATEGORY + "." + ParseObject.GUID,
                    WorkSlot.PARSE_OWNER_ITEM + "." + ParseObject.GUID,
                    WorkSlot.PARSE_OWNER_LIST + "." + ParseObject.GUID,
                    WorkSlot.PARSE_REPEAT_RULE + "." + ParseObject.GUID,
                    ParseObject.GUID,
                    WorkSlot.PARSE_DURATION,
                    WorkSlot.PARSE_END_TIME,
                    WorkSlot.PARSE_START_TIME,
                    WorkSlot.PARSE_TEXT
            ));
        } else {
            setupWorkSlotQueryWithIndirectAndGuids(query);
        }
        //
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

    private void setupRepeatRuleQueryWithIndirectAndGuids(ParseQuery<RepeatRuleParseObject> query) {
        query.include(RepeatRuleParseObject.PARSE_UNDONE_INSTANCES);
        query.include(RepeatRuleParseObject.PARSE_DONE_INSTANCES);
        query.selectKeys(Arrays.asList(
                RepeatRuleParseObject.PARSE_UNDONE_INSTANCES + "." + ParseObject.GUID,
                RepeatRuleParseObject.PARSE_DONE_INSTANCES + "." + ParseObject.GUID,
                //
                ParseObject.GUID,
                //
                RepeatRuleParseObject.PARSE_COUNT,
                //                RepeatRuleParseObject.PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR_XXX,
                //                RepeatRuleParseObject.PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR_XXX,
                //                RepeatRuleParseObject.PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED_XXX,
                RepeatRuleParseObject.PARSE_DATE_ON_COMPLETION_REPEATS,
                RepeatRuleParseObject.PARSE_DAYS_IN_WEEK,
                RepeatRuleParseObject.PARSE_DAY_IN_MONTH,
                RepeatRuleParseObject.PARSE_DAY_IN_YEAR,
                //                RepeatRuleParseObject.PARSE_DONE_INSTANCES,
                RepeatRuleParseObject.PARSE_END_DATE,
                RepeatRuleParseObject.PARSE_FREQUENCY,
                RepeatRuleParseObject.PARSE_INTERVAL,
                RepeatRuleParseObject.PARSE_MONTHS_IN_YEAR,
                RepeatRuleParseObject.PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD,
                RepeatRuleParseObject.PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD,
                RepeatRuleParseObject.PARSE_REPEAT_TYPE,
                //                RepeatRuleParseObject.PARSE_SPECIFIED_START_DATE_XXX,
                RepeatRuleParseObject.PARSE_WEEKDAYS_IN_MONTH,
                RepeatRuleParseObject.PARSE_WEEKS_IN_MONTH
        ));
    }

    private List<RepeatRuleParseObject> getAllRepeatRulesFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<RepeatRuleParseObject> query = ParseQuery.getQuery(RepeatRuleParseObject.CLASS_NAME);
        query.whereGreaterThan(RepeatRuleParseObject.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(RepeatRuleParseObject.PARSE_UPDATED_AT, now);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(RepeatRuleParseObject.PARSE_DELETED_DATE);
//        query.selectKeys(Arrays.asList(ParseObject.GUID));
        if (false) {
            query.include(RepeatRuleParseObject.PARSE_UNDONE_INSTANCES);
            query.include(RepeatRuleParseObject.PARSE_DONE_INSTANCES);
            query.selectKeys(Arrays.asList(
                    RepeatRuleParseObject.PARSE_UNDONE_INSTANCES + "." + ParseObject.GUID,
                    RepeatRuleParseObject.PARSE_DONE_INSTANCES + "." + ParseObject.GUID,
                    //
                    ParseObject.GUID,
                    //
                    RepeatRuleParseObject.PARSE_COUNT,
                    //                RepeatRuleParseObject.PARSE_COUNT_OF_INSTANCES_DONE_SO_FAR_XXX,
                    //                RepeatRuleParseObject.PARSE_COUNT_OF_INSTANCES_GENERATED_SO_FAR_XXX,
                    //                RepeatRuleParseObject.PARSE_DATE_OF_LATEST_COMPLETED_CANCELLED_XXX,
                    RepeatRuleParseObject.PARSE_DATE_ON_COMPLETION_REPEATS,
                    RepeatRuleParseObject.PARSE_DAYS_IN_WEEK,
                    RepeatRuleParseObject.PARSE_DAY_IN_MONTH,
                    RepeatRuleParseObject.PARSE_DAY_IN_YEAR,
                    //                RepeatRuleParseObject.PARSE_DONE_INSTANCES,
                    RepeatRuleParseObject.PARSE_END_DATE,
                    RepeatRuleParseObject.PARSE_FREQUENCY,
                    RepeatRuleParseObject.PARSE_INTERVAL,
                    RepeatRuleParseObject.PARSE_MONTHS_IN_YEAR,
                    RepeatRuleParseObject.PARSE_NUMBER_OF_DAYS_TO_GENERATE_AHEAD,
                    RepeatRuleParseObject.PARSE_NUMBER_SIMULTANEOUS_REPEATS_TO_GENERATE_AHEAD,
                    RepeatRuleParseObject.PARSE_REPEAT_TYPE,
                    //                RepeatRuleParseObject.PARSE_SPECIFIED_START_DATE_XXX,
                    RepeatRuleParseObject.PARSE_WEEKDAYS_IN_MONTH,
                    RepeatRuleParseObject.PARSE_WEEKS_IN_MONTH
            ));
        } else {
            setupRepeatRuleQueryWithIndirectAndGuids(query);
        }
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

    public List<Category> getAllCategoriesFromParse() {
//        return getAllCategoriesFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE));
        return getAllCategoriesFromParse(null, null);
    }

    private void setupCategoriesQueryWithIndirectAndGuids(ParseQuery<Category> query) {
        query.include(Category.PARSE_FILTER_SORT_DEF);
        query.include(Category.PARSE_OWNER);
        query.include(Category.PARSE_ITEMS);
        query.include(Category.PARSE_WORKSLOTS);
        query.selectKeys(Arrays.asList(
                Category.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                Category.PARSE_OWNER + "." + ParseObject.GUID,
                Category.PARSE_ITEMS + "." + ParseObject.GUID,
                Category.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                ParseObject.GUID,
                Category.PARSE_COMMENT,
                Category.PARSE_DELETED_DATE,
                Category.PARSE_EDITED_DATE,
                Category.PARSE_SYSTEM_NAME,
                Category.PARSE_TEXT
        ));
    }

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
//        query.selectKeys(Arrays.asList(ParseObject.GUID));
        if (false) {
            query.include(Category.PARSE_FILTER_SORT_DEF);
            query.include(Category.PARSE_OWNER);
            query.include(Category.PARSE_ITEMS);
            query.include(Category.PARSE_WORKSLOTS);
            query.selectKeys(Arrays.asList(
                    Category.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                    Category.PARSE_OWNER + "." + ParseObject.GUID,
                    Category.PARSE_ITEMS + "." + ParseObject.GUID,
                    Category.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                    ParseObject.GUID,
                    Category.PARSE_COMMENT,
                    Category.PARSE_DELETED_DATE,
                    Category.PARSE_EDITED_DATE,
                    Category.PARSE_SYSTEM_NAME,
                    Category.PARSE_TEXT
            ));
        } else {
            setupCategoriesQueryWithIndirectAndGuids(query);
        }
        //exclude the full data of the following fields (only keep Category's own data=Category.PARSE_COMMENT, Category.PARSE_TEXT, Category.PARSE_SYSTEM_NAME)
//        query.selectKeys(Arrays.asList(Category.PARSE_ITEMLIST,Category.PARSE_FILTER_SORT_DEF, 
//                Category.PARSE_ITEM_BAG, Category.PARSE_META_LISTS, Category.PARSE_SOURCE_LISTS,
//                Category.PARSE_OWNER, Category.PARSE_WORKSLOTS        ));
        List<Category> results = null;

        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
//        if(updateToCachedInstances)
//        results = fetchListElementsIfNeededReturnCachedIfAvail(results);
        return results;
    }

    public static String convCategoryListToString(List<Category> categoryList) {
        if (categoryList == null || categoryList.isEmpty()) {
            return "";
        }
        String catStr = null;
        for (Category c : categoryList) {
            if (catStr == null) {
                catStr = c.getText();
            } else {
                catStr += ", " + c.getText();
            }
        }
        return catStr;
    }

//    public List<Category> convCatObjectIdsListToCategoryListN(List<String> categoryIdList) {
//        if (categoryIdList == null) {
//            return null;
//        }
//        List<Category> categories = new ArrayList();
//        if (categoryIdList != null) {
//            for (String c : categoryIdList) {
//                categories.add(fetchCategory(c));
//            }
//        }
//        return categories;
//    }
//    public List<Item> convItemObjectIdsListToItemListN(List<String> itemObjIdList) {
//        List<Item> items = null;
//        if (itemObjIdList != null) {
//            items = new ArrayList();
//            for (String itmObjId : itemObjIdList) {
//                items.add(fetchItem(itmObjId));
//            }
//        }
//        return items;
//    }
    private boolean cacheAllCategoriesFromParse(Date reloadUpdateAfterThis, Date now) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        List<Category> results = getAllCategoriesFromParse(reloadUpdateAfterThis, now);
        cacheList(results);
        return !results.isEmpty();
    }

    public List<ItemList> getAllItemListsFromParse(boolean includeSystemLists) {
        return getAllItemListsFromParse(new Date(MyDate.MIN_DATE), new Date(MyDate.MAX_DATE), includeSystemLists);
    }

    private void setupItemListQueryWithIndirectAndGuids(ParseQuery<ItemList> query) {
        query.include(ItemList.PARSE_FILTER_SORT_DEF);
        query.include(ItemList.PARSE_ITEMS); //tasks
        query.include(ItemList.PARSE_OWNER);
        query.include(ItemList.PARSE_WORKSLOTS);
        query.selectKeys(Arrays.asList(
                ItemList.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                ItemList.PARSE_ITEMS + "." + ParseObject.GUID,
                ItemList.PARSE_OWNER + "." + ParseObject.GUID,
                ItemList.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                ParseObject.GUID,
                ItemList.PARSE_COMMENT,
                ItemList.PARSE_DELETED_DATE,
                ItemList.PARSE_EDITED_DATE,
                ItemList.PARSE_SYSTEM_NAME,
                ItemList.PARSE_TEXT
        ));
    }

    public List<ItemList> getAllItemListsFromParse(Date reloadUpdateAfterThis, Date reloadUpToAndIncludingThisDate, boolean includeSystemLists) {
        //TODO!!!!! need to implement buffering/skip to avoid hitting the maximum of 1000 objects
        ParseQuery<ItemList> query = ParseQuery.getQuery(ItemList.CLASS_NAME);
        query.whereGreaterThan(Item.PARSE_UPDATED_AT, reloadUpdateAfterThis);
        query.whereLessThanOrEqualTo(Item.PARSE_UPDATED_AT, reloadUpToAndIncludingThisDate);
        query.setLimit(MyPrefs.cacheMaxNumberParseObjectsToFetchInQueries.getInt()); //TODO!!!!
        query.whereDoesNotExist(Item.PARSE_DELETED_DATE);
//        query.selectKeys(Arrays.asList(ParseObject.GUID));
        if (false) {
            query.include(ItemList.PARSE_FILTER_SORT_DEF);
            query.include(ItemList.PARSE_ITEMS); //tasks
            query.include(ItemList.PARSE_OWNER);
            query.include(ItemList.PARSE_WORKSLOTS);
            query.selectKeys(Arrays.asList(
                    ItemList.PARSE_FILTER_SORT_DEF + "." + ParseObject.GUID,
                    ItemList.PARSE_ITEMS + "." + ParseObject.GUID,
                    ItemList.PARSE_OWNER + "." + ParseObject.GUID,
                    ItemList.PARSE_WORKSLOTS + "." + ParseObject.GUID,
                    ParseObject.GUID,
                    ItemList.PARSE_COMMENT,
                    ItemList.PARSE_DELETED_DATE,
                    ItemList.PARSE_EDITED_DATE,
                    ItemList.PARSE_SYSTEM_NAME,
                    ItemList.PARSE_TEXT
            ));
        } else {
            setupItemListQueryWithIndirectAndGuids(query);
        }

        if (!includeSystemLists) {
            query.whereDoesNotExist(ItemList.PARSE_SYSTEM_NAME);
        }

        List<ItemList> results = null;
        try {
            results = query.find();
        } catch (ParseException ex) {
            Log.e(ex);
        }
//        return fetchListElementsIfNeededReturnCachedIfAvail(results);
        return results;
    }

    private boolean cacheAllItemListsFromParse(Date reloadUpdateAfterThis, Date now) {
        List<ItemList> results = getAllItemListsFromParse(reloadUpdateAfterThis, now, true);
        cacheList(results);
        return !results.isEmpty();
    }

    /**
     * initialize cache. Creates a new memory cache but will keep/access
     * elements cached in local storage. Can be called if changing the settings
     * for size of cache (this will reset memory cache). if createNewCache is
     * true a new cahce will be created (this is not necessary if just changing
     * the size the of the cahce)
     */
    private void initAndConfigureCache() {
//        if (cache == null || forceCreationOfNewInMemoryCache) {
        if (true || cache == null) { //NO reason to keep old cache, even if cleaned??!
//            cache = null; //force GC
//            cache = new MyCacheMap("ALL");
            cache = new MyCacheMapHash("ALL");
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
            cacheWorkSlots = new MyCacheMapHash("WS"); //prefix neccessary to not confuse locally cached items
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

    public void clearAllCacheAndStorage(boolean keepCurrentUserSessions) {
        Dialog ip = new InfiniteProgress().showInfiniteBlocking();
        Log.p("Deleting ALL STORAGE (except user token");
        String userToken = ScreenLogin.fetchCurrentUserSessionFromStorage(); //avoid user token being deleted (which would force user to log in again)
        //TODO also reset cache to ensure a complete reset
        Storage.getInstance().clearStorage();
        cache.clearMemoryCache();
        cacheWorkSlots.clearMemoryCache();
        if (keepCurrentUserSessions) {
            ScreenLogin.saveCurrentUserSessionToStorage(userToken);
        }

        ip.dispose();
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
//        Dialog ip = new InfiniteProgress().showInfiniteBlocking();
        if (false) {
            Storage.getInstance().deleteStorageFile(FILE_DATE_FOR_LAST_CACHE_REFRESH); //delete date so all data will be reloaded in cacheLoadDataChangedOnServer()
            if (cache != null) {
//            cache.clearStorageCache(); //delete any locally cached data/files
//            cache.clearAllCache(); //clear any cached data (even in memory to make sure we get a completely fresh copy)
//                cache.clearAllCache(RESERVED_LIST_NAMES); //clear any cached data (even in memory to make sure we get a completely fresh copy)
                cache = null;  //force GC and creation of new cache in initAndConfigureCache()
            }
            if (cacheWorkSlots != null) {
//            cache.clearStorageCache(); //delete any locally cached data/files
                cacheWorkSlots.clearAllCache(); //clear any cached data (even in memory to make sure we get a completely fresh copy)
//            cacheWorkSlots.clearAllCache(RESERVED_LIST_NAMES); //clear any cached data (even in memory to make sure we get a completely fresh copy)
                cacheWorkSlots = null;  //force GC and creation of new cache in initAndConfigureCache()
            }
        } else {
            clearAllCacheAndStorage(true);
        }
//        initAndConfigureCache(true);
        initAndConfigureCache();
        cacheLoadDataChangedOnServer(true); //, false);
        TimerStack2.getInstance().refreshTimersFromParse();
//        ip.dispose();
    }

    public void updateCacheWhenSettingsChangeNOT_TESTED() {
        if (MyPrefs.cacheLocalStorageSize.getInt() <= 0) {
            Storage.getInstance().deleteStorageFile(FILE_DATE_FOR_LAST_CACHE_REFRESH);
        }
//        initAndConfigureCache(false); //if cache size increases, additional objects will automatically be cached
        initAndConfigureCache(); //if cache size increases, additional objects will automatically be cached

    }

    /**
     *
     * @param forceLoadData skip checking server for updates to optimize app
     * startup time during testing. will initialize (or reset if
     * resetAndDeleteAllCachedData) the cache and update cache with objects that
     * have been changed on Parse server since last update. First time called
     * will cache everything. Assumes that local cache is large enough to hold
     * everything, if not, ???
     */
    public void cacheLoadDataChangedOnServer(boolean forceLoadData) {//, boolean inBackground) {
        //TODO!!!! what happens if cache is too small??? WIll it drop oldest objects?
//        initAndConfigureCache(); //now done in DAO constructor
////\        loadCacheToMemory(); //first load 

//        if (false) {
//            cache.loadCacheToMemory(); //first load to memory
//        }
//        if (forceLoadData) {
//            Dialog ip = null;
//            if (false && !inBackground) { //false since this creates a new Form just to show the infinte progress
//                ip = new InfiniteProgress().showInfiniteBlocking();
//            }
//        Date now = new MyDate(); //UI: only cache data that was already changed when update was launched
        Date lastCacheRefreshDate = null; //new Date(MyDate.MIN_DATE);

//        if (true || MyPrefs.cacheLocalStorageSize.getInt() > 0) { //only store if local cache is active -> test doesn't make sense since app cannot currently function without cache
        //get date when local cache was last updated
        if (Storage.getInstance().exists(FILE_DATE_FOR_LAST_CACHE_REFRESH)) {
            lastCacheRefreshDate = (Date) Storage.getInstance().readObject(FILE_DATE_FOR_LAST_CACHE_REFRESH); //read in when initializing the Timer - from here on it is only about saving updates
        }
//        Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, now); //save date
//        }
        if (lastCacheRefreshDate == null || forceLoadData) { //only cache once
            lastCacheRefreshDate = new MyDate(); //force caching all data from ParseServer
            Dialog ip = new InfiniteProgress().showInfiniteBlocking();
            boolean result = cacheAllData(); //lastCacheRefreshDate, lastCacheRefreshDate);
            ip.dispose();
            Storage.getInstance().writeObject(FILE_DATE_FOR_LAST_CACHE_REFRESH, lastCacheRefreshDate); //save date only *after* caching is done, in case app is interrupted (so it will restart from same place next time)!
        }
//            if (ip != null) {
//                ip.dispose();
//            }
//            return result;
//        }
//        return false;
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
    /**
     * load all data from local cache into memory (create all objects in memory
     * as unique instances where all references should point to the same
     * physical object instance)
     *
     * @param firstDateXXX
     * @param lastDateXXX
     * @return
     */
    public boolean cacheAllDataOLD() {//Date firstDateXXX, Date lastDateXXX) {
        if (false) {
            return false;
        }
        boolean somethingWasLoaded = false;

        //ALWAYS for complete reload of all data
        Date firstDateXXX = new MyDate(MyDate.MIN_DATE);
        Date lastDateXXX = new MyDate(MyDate.MAX_DATE);

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching CategoryList");
//        somethingWasLoaded = cacheCategoryList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Categories
//        CategoryList.getInstance().reloadFromParse();
//        CategoryList.getInstance();
//        somethingWasLoaded = CategoryList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            CategoryList.getInstance();
//            ItemListList.getInstance();
//            TemplateList.getInstance();
//            cachePut(CategoryList.getInstance());
        }

        Log.p("Caching Categories");
        somethingWasLoaded = cacheAllCategoriesFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        Log.p("Caching Filters");
        somethingWasLoaded = cacheAllFilterSortDefsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;
//        getAllCategoriesFromParse();
//        getAllCategoriesFromParse(reloadUpdateAfterThis, now);
//        getAllItemListsFromParse();
//        getAllItemListsFromParse(reloadUpdateAfterThis, now);

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching ItemListList");
//        somethingWasLoaded = cacheItemListList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of ItemLists
//        somethingWasLoaded = ItemListList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            ItemListList.getInstance();
//            cachePut(ItemListList.getInstance());
        }

        Log.p("Caching ItemLists");
        somethingWasLoaded = cacheAllItemListsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        Log.p("Caching RepeatRules");
        somethingWasLoaded = cacheAllRepeatRulesFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;
//        getCategoryList(); //will cache the list of Categories
//        getItemListList(); //will cache the list of ItemLists
//        getTemplateList(); //will cache the list of Templates

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching TemplateList");
//        somethingWasLoaded = cacheTemplateList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Templates
//        somethingWasLoaded = TemplateList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            TemplateList.getInstance();
//            cachePut(TemplateList.getInstance());
        }

        Log.p("Caching Items");
        somethingWasLoaded = cacheAllItemsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        Log.p("Caching WorkSlots");
        somethingWasLoaded = cacheAllWorkSlotsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

//        cacheUpdateAllCategoryItemReferences(categoryList);
//        cacheUpdateAllItemListItemReferences(itemListLists);
//        cacheUpdateAllItemReferences();
        Log.p("cacheAllData FINISHED updating cache" + (somethingWasLoaded ? " NEW DATA LOADED" : " no data loaded"));
        return somethingWasLoaded;
    }

    public boolean cacheAllData() {//Date firstDateXXX, Date lastDateXXX) {
        boolean somethingWasLoaded = false;

        //ALWAYS for complete reload of all data
        Date firstDateXXX = new MyDate(MyDate.MIN_DATE);
        Date lastDateXXX = new MyDate(MyDate.MAX_DATE);

        Log.p("Caching Filters");
        somethingWasLoaded = cacheAllFilterSortDefsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching CategoryList");
//        somethingWasLoaded = cacheCategoryList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Categories
//        CategoryList.getInstance().reloadFromParse();
//        CategoryList.getInstance();
//        somethingWasLoaded = CategoryList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            Object load = CategoryList.getInstance(); //load *after* categories
//            ItemListList.getInstance();
//            TemplateList.getInstance();
//            cachePut(CategoryList.getInstance());
        }

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching ItemListList");
//        somethingWasLoaded = cacheItemListList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of ItemLists
//        somethingWasLoaded = ItemListList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            Object load = ItemListList.getInstance();
//            cachePut(ItemListList.getInstance());
        }

        Log.p("Caching Categories");
        somethingWasLoaded = cacheAllCategoriesFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

//        getAllCategoriesFromParse();
//        getAllCategoriesFromParse(reloadUpdateAfterThis, now);
//        getAllItemListsFromParse();
//        getAllItemListsFromParse(reloadUpdateAfterThis, now);
        Log.p("Caching ItemLists");
        somethingWasLoaded = cacheAllItemListsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        Log.p("Caching RepeatRules");
        somethingWasLoaded = cacheAllRepeatRulesFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;
//        getCategoryList(); //will cache the list of Categories
//        getItemListList(); //will cache the list of ItemLists
//        getTemplateList(); //will cache the list of Templates

        if (true) { //for now MUST load before Categories to ensure that cache points to correct instance in memory of CategoryList
            Log.p("Caching TemplateList");
//        somethingWasLoaded = cacheTemplateList(afterDate, beforeDate) || somethingWasLoaded; //will cache the list of Templates
//        somethingWasLoaded = TemplateList.getInstance().reloadFromParse(false, afterDate, beforeDate) || somethingWasLoaded;
            TemplateList.getInstance();
//            cachePut(TemplateList.getInstance());
        }

        Log.p("Caching Items");
        somethingWasLoaded = cacheAllItemsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

        Log.p("Caching WorkSlots");
        somethingWasLoaded = cacheAllWorkSlotsFromParse(firstDateXXX, lastDateXXX) || somethingWasLoaded;

//        cacheUpdateAllCategoryItemReferences(categoryList);
//        cacheUpdateAllItemListItemReferences(itemListLists);
//        cacheUpdateAllItemReferences();
        Log.p("cacheAllData FINISHED updating cache" + (somethingWasLoaded ? " NEW DATA LOADED" : " no data loaded"));
        return somethingWasLoaded;
    }

    public void cleanUpAllBadObjectReferences(boolean executeCleanup) {
//        CleanUpDataInconsistencies clean = new CleanUpDataInconsistencies(this);
//        clean.cleanUpAllBadObjectReferences(executeCleanup);
        CleanUpDataInconsistencies.getInstance().cleanUpEverything(executeCleanup);
    }

    void cleanUpItemList(ItemList itemList, boolean makeTemplate, boolean executeCleanup) {
//        return new CleanUpDataInconsistencies(this).cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, false);
        CleanUpDataInconsistencies.getInstance().cleanUpItemList(itemList, makeTemplate, executeCleanup);
    }

    void cleanUpCategory(Category category, boolean executeCleanup) {
//        return new CleanUpDataInconsistencies(this).cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, false);
        CleanUpDataInconsistencies.getInstance().cleanUpCategory(category, executeCleanup);
    }

    public void cleanUpTemplateListInParse(boolean executeCleanup) {
//        cleanUpTemplateList(DAO.getInstance().getTemplateList(), getTopLevelTemplatesFromParse(), true);
//        cleanUpTemplateList(TemplateList.getInstance(), dao.getTopLevelTemplatesFromParse(), executeCleanup);
//        new CleanUpDataInconsistencies(this).cleanUpTemplateListInParse(executeCleanup);
        CleanUpDataInconsistencies.getInstance().cleanUpTemplates(executeCleanup);
    }

    void cleanUpWorkSlots(boolean executeCleanup) {
//        new CleanUpDataInconsistencies(this).cleanUpWorkSlots(executeCleanup);
        CleanUpDataInconsistencies.getInstance().cleanUpAllWorkSlotsFromParse(executeCleanup);
    }

//    void cleanUpItemsWithNoValidOwner(boolean executeCleanup) {
////        new CleanUpDataInconsistencies(this).cleanUpItemsWithNoValidOwner(executeCleanup);
//        CleanUpDataInconsistencies.getInstance().cleanUpItemsWithNoValidOwner(executeCleanup);
//    }
    void cleanUpItemListOrCategory(ItemList itemListOrCategory, boolean executeCleanup, boolean cleanupItems) {
//        return new CleanUpDataInconsistencies(this).cleanUpItemListOrCategory(itemListOrCategory, executeCleanup, cleanupItems);
        if (itemListOrCategory instanceof Category) {
            CleanUpDataInconsistencies.getInstance().cleanUpCategory((Category) itemListOrCategory, executeCleanup);
        } else {
            CleanUpDataInconsistencies.getInstance().cleanUpItemList(itemListOrCategory, executeCleanup, cleanupItems);
        }
    }

    static void emailLog(ActionEvent evt) {
        p("Exception in " + Display.getInstance().getProperty("AppName", "app") + " version " + Display.getInstance().getProperty("AppVersion", "Unknown"));
        p("OS " + Display.getInstance().getPlatformName());
        if (evt != null) {
            p("Error " + evt.getSource());
        }
        if (Display.getInstance().getCurrent() != null) {
            p("Current Form " + Display.getInstance().getCurrent().getName());
        } else {
            p("Before the first form!");
        }
        if (evt != null && evt.getSource() instanceof Throwable) {
            e((Throwable) evt.getSource());
        }
        byte[] read = new byte[]{(byte) 0xe0};//['a'];
        try {
            //                sendLog();
            read = com.codename1.io.Util.readInputStream(Storage.getInstance().createInputStream("CN1Log__$"));
//                        read.toString();

            Message m = new Message("Body of message"
                    + "DeviceId: " + Log.getUniqueDeviceId()
                    + "\nBuilt by user: " + Display.getInstance().getProperty("built_by_user", "")
                    + "\nPackage name: " + Display.getInstance().getProperty("package_name", "")
                    + "\nAppVersion: " + Display.getInstance().getProperty("AppVersion", "0.1")
                    + "\nLOG:\n---------------------------------\n"
                    //                            + Storage.getInstance().readObject("CN1Log__$")
                    //                            + new String(read)
                    //                            + MyUtil.hexStringToByteArray(read)
                    + new String(read, "BaSE64") // for UTF-8 encoding, https://stackoverflow.com/questions/1536054/how-to-convert-byte-array-to-string-and-vice-versa
            );
//            m.getAttachments().put(textAttachmentUri, "text/plain");
//            m.getAttachments().put(imageAttachmentUri, "image/png");
            Display.getInstance().sendMessage(new String[]{"crashreport@todocatalyst.com"}, "TodoCatalyst crash report", m);
        } catch (IOException ex) {
//                        java.util.logging.Logger.getLogger(TodoCatalyst.class.getName()).log(Level.SEVERE, null, ex);
            Log.p(TodoCatalyst.class.getName(), Log.ERROR);
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveItem3XXX(Item item) {
//        item.updateBeforeSave();
//        saveItemNew3(item); //traverse to find all dirty items and add to save list and build lambda functions
//        afterParseUpdate.add(() -> {
//            item.setOwnerItem((Item) owner, false, false);
//            addToSaveList3(item);
//        });
//    }
//</editor-fold>
    /**
     * save savelist and deleteList to local storage. If any is null, that
     * parameter is not used (ignored, no effect)
     *
     * @param saveList
     * @param deleteList
     */
    private void savePendingParseUpdatesToLocalstorage(Collection<ParseObject> saveList, Collection<ParseObject> deleteList) {
        if (saveList != null) {
            if (false && Config.TEST) {
                List toSaveListTest = (List<ParseObject>) Storage.getInstance().readObject("SaveList");
                int i = 0;
                if (false) {
                    if (Objects.equals(toSaveListTest, saveList)) {
                        Log.p("Lists equals");
                    } else {
                        Log.p("Lists NOT equals, saveList=" + saveList + "; toSaveListTest=" + toSaveListTest);
                    }
                }
            }
            Storage.getInstance().writeObject("SaveList", saveList); //save

        }//        List<String> deleteParseIds = new ArrayList<>();
//        for (ParseObject p : deleteList) {
//            deleteParseIds.add(p.getObjectIdP());
//        }
//        Storage.getInstance().writeObject("DeleteList", deleteParseIds); //save
        if (deleteList != null) {
            if (Config.TEST) {
                Collection deleteListTest = (Collection<ParseObject>) Storage.getInstance().readObject("DeleteList");
                int i = 0;
            }
            Storage.getInstance().writeObject("DeleteList", deleteList); //save
        }
    }

    private void loadPendingParseUpdatesFromLocalstorage() {
        toSaveList = (List<ParseObject>) Storage.getInstance().readObject("SaveList");
//        List<String> deleteParseIds = (List<String>) Storage.getInstance().readObject("DeleteList");
//        for (String  objId : deleteParseIds) {
//            DAO.getInstance().deleteParseIds.add(p.getObjectIdP());
//        }
        toDeleteList = (Set<ParseObject>) Storage.getInstance().readObject("DeleteList");
    }

    private void setSavePending(List<ParseObject> saveList, boolean value) {
        for (ParseObject p : saveList) {
            p.setSaveIsPending(value);
        }
    }

    private boolean testNoUnsaved(List<ParseObject> list) {
        for (ParseObject p : list) {
            if (p.isNotCreated()) {
                return true;
            }
        }
        return false;
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void triggerParseUpdateOld(boolean waitForCompletion) {
//
//        List<ParseObject> saveListCopy = new ArrayList(saveList);
//        List<ParseObject> deleteListCopy = new ArrayList(deleteList);
//        List<Runnable> afterParseUpdateCopy = new ArrayList(afterParseUpdate);
//        Runnable saveRunnable = () -> {
//
//            List<ParseObject> successfullySaved = new ArrayList();
////            while (saveList != null && !saveList.isEmpty()) {
//            while (saveListCopy != null && !saveListCopy.isEmpty()) {
//                try {
////                    batchSave(saveList, deleteList); //add save/create/delete objecs to batch and send it
//                    batchSave(saveListCopy, deleteListCopy); //add save/create/delete objecs to batch and send it
//                } catch (ParseException ex) {
//                    Log.e(ex);
//                } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
//                    Log.e(ex);
//                }
//                ASSERT.that(testNoUnsaved(saveListCopy), "some values were not saved");
//
//                for (ParseObject o : deleteListCopy) {
//                    cacheDelete(o);
//                }
//                deleteListCopy.clear();
//
////                for (ParseObject p : saveList) {
////                    p.setSaveIsPending(false); //need to set false so lambda functions below don't get to wait
////                }
////                setSavePending(saveList, false); //need to set false so lambda functions below don't get to wait
//                setSavePending(saveListCopy, false); //need to set false so lambda functions below don't get to wait
//                for (Runnable lambda : afterParseUpdateCopy) {
//                    lambda.run();
//                }
//                afterParseUpdateCopy.clear();
//
//                List<ParseObject> stillNeedsSaving = new ArrayList();
////                for (ParseObject p : saveList) {
//                for (ParseObject p : saveListCopy) {
//                    if (p.isDirty()) { //if dirty after restoring refs to previously uncreated ParseObjects vai the Runnables in afterParseUpdate
////                        p.setSaveIsPending(true); //DONE in batchsave() //if lambda function modified it, set true again until completely saved
//                        stillNeedsSaving.add(p);
//                    } else {
////                        p.setSaveIsPending(false);
//                        if (p instanceof ItemAndListCommonInterface) {
//                            ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                        }
//                        if (true) {
//                            successfullySaved.add(p); //
//                        }
//                    }
//                }
////                saveList = stillNeedsSaving; //remove all non-dirty
//                saveListCopy.clear();
//                saveListCopy.addAll(stillNeedsSaving); //remove all non-dirty
////                savePendingParseUpdatesToLocalstorage(stillNeedsSaving,deleteList);
//                savePendingParseUpdatesToLocalstorage(stillNeedsSaving, deleteListCopy);
//            }
//            if (false) {
//                for (ParseObject p : successfullySaved) {
//                    if (p instanceof ItemAndListCommonInterface) {
//                        ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                    }
//                }
//            }
//        };
//
//        savePendingParseUpdatesToLocalstorage(saveList, deleteList); //save modified items locally
//
//        //pre-processes: remove all refs to unsaved items and create lmbdas to restablish them
//        for (Runnable r : beforeParseUpdate) {
//            r.run();
//        }
//
//        saveListCopy.addAll(saveList);
//        deleteListCopy.addAll(deleteList);
//        afterParseUpdateCopy.addAll(afterParseUpdate);
//        saveList.clear();
//        deleteList.clear();
//        afterParseUpdate.clear();
//        setSavePending(saveList, true); //set on main thread(not in Runnable which may run in background), now parseObjects are proteved from updates until the save has completed
//
////        while (saveList != null && !saveList.isEmpty()) {
//        if (waitForCompletion) {
////            Display.getInstance().callSeriallyAndWait(saveRunnable);
//            saveRunnable.run();
//        } else {
//            Display.getInstance().callSerially(saveRunnable);
//        }
////        }
//    }
//</editor-fold>
    private void checkNoUnsavedReferences(Item item, String text) {
        if (Config.TEST) {
            ASSERT.that(item.getOwner() == null || !item.getOwner().isNotCreated(), text + "Item=" + item + ", has unsaved owner=" + item.getOwner());
            ASSERT.that(item.getSource() == null || !item.getSource().isNotCreated(), text + "Item=" + item + ", has unsaved source=" + item.getSource());
            ASSERT.that(item.getDependingOnTask() == null || !item.getDependingOnTask().isNotCreated(), text + "Item=" + item + ", has unsaved depndingOn=" + item.getDependingOnTask());
            ASSERT.that(item.getFilterSortDefN() == null || !item.getFilterSortDefN().isNotCreated(), text + "Item=" + item + ", has unsaved filter=" + item.getFilterSortDefN());
            checkNoUnsavedReferences(item.getListFull(), "In Project=" + item + "; Subtasks - ");
            ASSERT.that(item.getRepeatRuleN() == null || !item.getRepeatRuleN().isNotCreated(), text + "Item=" + item + ", has unsaved owner=" + item.getOwner());
            if (item.getWorkSlotListN() != null) {
                checkNoUnsavedReferences((List) item.getWorkSlotListN().getWorkSlotListFull(), "In Project=" + item + "; WorkSlots - ");
            }
        }
    }

    private void checkNoUnsavedReferences(List<ParseObject> list, String text) {
        if (Config.TEST) {
            for (ParseObject p : list) {

                if (p instanceof Item) {
                    Item item = (Item) p;
//                ASSERT.that(!item.getOwner().isUnsaved(), text + "Item=" + item + ", has unsaved owner=" + item.getOwner());
//                ASSERT.that(item.getSource() == null || !item.getSource().isUnsaved(), text + "Item=" + item + ", has unsaved source=" + item.getSource());
//                ASSERT.that(item.getDependingOnTask() == null || !item.getDependingOnTask().isUnsaved(), text + "Item=" + item + ", has unsaved depndingOn=" + item.getDependingOnTask());
//                ASSERT.that(item.getFilterSortDefN() == null || !item.getFilterSortDefN().isUnsaved(), text + "Item=" + item + ", has unsaved filter=" + item.getFilterSortDefN());
//                checkNoUnsavedReferences(item.getListFull(), "In Project=" + item + "; Subtasks - ");
//                ASSERT.that(item.getRepeatRuleN() == null || !item.getRepeatRuleN().isUnsaved(), text + "Item=" + item + ", has unsaved owner=" + item.getOwner());
//                if (item.getWorkSlotListN() != null) {
//                    checkNoUnsavedReferences((List) item.getWorkSlotListN(), "In Project=" + item + "; WorkSlots - ");
//                }
                    checkNoUnsavedReferences(item, text);
                } else if (p instanceof WorkSlot) {
                    WorkSlot workSlot = (WorkSlot) p;
                    ASSERT.that(!workSlot.getOwner().isNotCreated(), text + "WorkSlot=" + workSlot + ", has unsaved owner=" + workSlot.getOwner());
                } else if (p instanceof Category) {
                    Category category = (Category) p;
                    checkNoUnsavedReferences(category.getListFull(), "In Category=" + category + "; ");
                } else if (p instanceof ItemList) {
                    ItemList itemList = (ItemList) p;
                    checkNoUnsavedReferences(itemList.getListFull(), "In ItemList=" + itemList + "; ");
                } else if (p instanceof RepeatRuleParseObject) {
                    RepeatRuleParseObject repeatRule = (RepeatRuleParseObject) p;
                    checkNoUnsavedReferences((List) repeatRule.getListOfDoneInstances(), "In RepeatRule=" + repeatRule + "; Done list - ");
                    checkNoUnsavedReferences((List) repeatRule.getListOfUndoneInstances(), "In RepeatRule=" + repeatRule + "; Undone list - ");
                }
            }
        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void triggerParseUpdateOLD(boolean waitForCompletion) {
//
//        synchronized (toSaveList) {
////        savePendingParseUpdatesToLocalstorage(saveList, deleteList); //save modified items locally
//            savePendingParseUpdatesToLocalstorage(toSaveList, deleteList); //save modified items locally
//
//            //proess toSaveList to create saveList with all updated (incl. indirectly) parseobjects to save
//            for (ParseObject p : toSaveList) {
//                if (p instanceof ItemAndListCommonInterface) {
//                    if (((ItemAndListCommonInterface) p).isNoSave()) {
//                        continue; //skip nosave elements
//                    } else {
//                        if (true) {
//                            ((ItemAndListCommonInterface) p).updateBeforeSave();
//                        }
//                    }
//                }
//                if (false && Config.TEST) {
//                    testShowMissingRefs(p);
//                }
//
//                if (p instanceof Item) {
//                    saveItemNew3((Item) p);
////                    checkNoUnsavedReferences((Item) p,"");
//                } else if (p instanceof Category) { //also covers Category, and ItemListList and CategoryList
//                    saveCategoryNew3((Category) p);
//                } else if (p instanceof ItemList) { //also covers Category, and ItemListList and CategoryList
//                    saveItemListNew3((ItemList) p);
//                } else if (p instanceof WorkSlot) { //e.g. Category, WorkSlot
//                    saveWorkSlotNew3((WorkSlot) p);
//                } else if (p instanceof RepeatRuleParseObject) {
//                    saveRepeatRuleNew3((RepeatRuleParseObject) p);
//                } else if (p instanceof FilterSortDef) { //e.g. Category, WorkSlot
//                    saveFilterSortDefNew3((FilterSortDef) p);
//                } else { //e.g. Category, WorkSlot
//                    if (Config.TEST) {
//                        ASSERT.that(false, () -> "type of object not handled, p=" + p + "; list=" + toSaveList);
//                    }
//                }
//            }
//
//            //take a snapshot of all the lists
////        List<ParseObject> saveListCopy = new ArrayList(saveList);
////        List<ParseObject> deleteListCopy = new ArrayList(deleteList);
////        List<Runnable> afterParseUpdateCopy = new ArrayList(afterParseUpdate);
//            //pre-processes: remove all refs to unsaved items and create lmbdas to restablish them
//            for (Runnable r : beforeParseUpdate) {
//                r.run();
//            }
//            beforeParseUpdate.clear();
//
//            if (Config.TEST) {
//                checkNoUnsavedReferences(saveList, "");
//            }
//
//            //do *after* running the lambdas in beforeParseUpdate sincy they may add additional updated elements to save
//            List<ParseObject> saveListCopy = new ArrayList(saveList);
//            List<ParseObject> saveListCopyFull = new ArrayList(saveList);
//            saveList.clear(); //TODO: still needed with a copy of this list?!
//            List<ParseObject> deleteListCopy = new ArrayList(deleteList);
//            deleteList.clear();
//            List<Runnable> afterParseUpdateCopy = new ArrayList(afterParseUpdate);
////            List<Runnable> afterParseUpdateCopy = afterParseUpdate; //afterParseUpdate is updated when running //NO, updated above in r.run()'s
//            afterParseUpdate.clear();
////        saveListCopy.addAll(saveList);
////        deleteListCopy.addAll(deleteList);
////        afterParseUpdateCopy.addAll(afterParseUpdate);
//
//            setSavePending(saveListCopy, true); //set on main thread(not in Runnable which may run in background), now parseObjects are proteved from updates until the save has completed
//
//            Runnable saveRunnable = () -> {
//
//                List<ParseObject> successfullySaved = new ArrayList();
//                int count = 0;
//                while (saveListCopy != null && !saveListCopy.isEmpty()) {
//                    try {
//                        batchSave(saveListCopy, deleteListCopy); //add save/create/delete objecs to batch and send it
//                    } catch (ParseException ex) {
//                        Log.e(ex);
//                    } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
//                        Log.e(ex);
//                    }
//
//                    for (ParseObject o : deleteListCopy) {
//                        cacheDelete(o);
//                    }
//                    deleteList.removeAll(deleteListCopy); //only remove those actually deleted, in case more have been deleted since
//                    deleteListCopy.clear(); //delete takes only one iteration, where save/create may take two
//
//                    setSavePending(saveListCopy, false); //need to set false so lambda functions below don't get to wait
//
//                    //update to restore relations to now created objects
//                    for (Runnable lambda : afterParseUpdateCopy) {
//                        lambda.run();
//                    }
//                    afterParseUpdateCopy.clear(); //clear so only done once
//
//                    List<ParseObject> stillNeedsSaving = new ArrayList();
////                for (ParseObject p : saveList) {
//                    for (ParseObject p : saveListCopy) {
//                        if (p.isDirty()) { //if dirty after restoring refs to previously uncreated ParseObjects vai the Runnables in afterParseUpdate
////                        p.setSaveIsPending(true); //DONE in batchsave() //if lambda function modified it, set true again until completely saved
//                            stillNeedsSaving.add(p);
//                        } else {
////                            toSaveList.remove(p); //remove now saved item //NO, too early
//                            if (p instanceof ItemAndListCommonInterface) {
//                                ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                            }
//                            if (true) {
//                                successfullySaved.add(p); //for testing
//                            }
//                        }
//                    }
////                saveList = stillNeedsSaving; //remove all non-dirty
//                    saveListCopy.clear();
//                    setSavePending(stillNeedsSaving, true); //need to set true again so don't get overwritten while waiting for save to complete
//                    saveListCopy.addAll(stillNeedsSaving); //remove all non-dirty
//                    if (Config.TEST) {
//                        checkNoUnsavedReferences(saveListCopy, "");
//                    }
//                    count++;
//                } //while
////            toSaveList.clear(); //NO, this might wrongly remove parseobjects added while sending previous edits
////                toSaveList.removeAll(saveListCopyFull);
//                for (ParseObject o : saveListCopyFull) {
//                    if (!o.isDirty()) { //only remove not dirty objects, for the case where a saved object has become dirty again after saving
//                        toSaveList.remove(o);
//                    }
//                }
//                savePendingParseUpdatesToLocalstorage(toSaveList, deleteList); //save lists with any remaining items locally
//            }; //end lambda
//
////        while (saveList != null && !saveList.isEmpty()) {
//            if (waitForCompletion) {
////            Display.getInstance().callSeriallyAndWait(saveRunnable);
//                saveRunnable.run();
//            } else {
//                Display.getInstance().callSerially(saveRunnable);
//            }
//        }
////        }
//    }
//</editor-fold>
    public void triggerParseUpdate() {
        triggerParseUpdate(!MyPrefs.backgroundSave.getBoolean());
    }

    private boolean triggerInitiated;

    public void triggerParseUpdate(boolean waitForCompletion) {
        //approach: on ETP, altern, encode and create and add back references for new objects, then save in background
        //evaluation: OK to edit (eg Check/EditScr2) after creation, but will be blocked until last/2nd save
        //problem: after 1st 'creation-save + restore references to now saved objects' objects will be in expected state, but w unsaved pending changes 
        //any attempt to changing a not-yet-saved object will be held up until it is saved (or block the UI if no network)

//        waitForCompletion = true; //force synchronuous save for now
//        synchronized (toSaveList) {
//        savePendingParseUpdatesToLocalstorage(saveList, deleteList); //save modified items locally
        savePendingParseUpdatesToLocalstorage(toSaveList, toDeleteList); //save modified items locally
        checkAndRefreshAllCachedLists(toSaveList, toDeleteList);

//            //pre-processes: remove all refs to unsaved items and create lmbdas to restablish them
//            for (Runnable r : beforeParseUpdate) {
//                r.run();
//            }
//            beforeParseUpdate.clear();
//            if (Config.TEST) {
//                checkNoUnsavedReferences(saveList, "");
//            }
        processedList.clear();//clear at start of each run
        //make sure all work is done on copies of data
        List<ParseObject> toSaveListCopy = new ArrayList(toSaveList);
        List<ParseObject> saveList = new ArrayList(); //list of all explicitly or indirectly saved objects (e.g. updated via inheritance or aggregation)
        List<ParseObject> deleteList = new ArrayList(this.toDeleteList);
//        List<Runnable> afterParseUpdate = new ArrayList(this.afterParseUpdate);
        List<Runnable> afterParseUpdate = new ArrayList();
//        ASSERT.that(this.afterParseUpdate.isEmpty(), "this.afterParseUpdate is NOT EMPTY - why?!, =" + this.afterParseUpdate);

//        List<ParseObject> stillNeedsSaving = new ArrayList();
//            ASSERT.that(saveList.isEmpty(),()->"saveList NOT empty, content="+saveList);
        Runnable saveRunnable1 = () -> {
            if (Config.TEST) {
                Log.p("saveRunnable1 START");
            }

            for (ParseObject o : deleteList) {
                if (o instanceof ItemAndListCommonInterface) {
                    ((ItemAndListCommonInterface) o).onDelete();
                }
            }
//process toSaveList to create saveList with all updated (incl. indirectly) parseobjects to save
            Set<ParseObject> processedList = new HashSet<>();
            for (ParseObject p : toSaveListCopy) {
                if (p instanceof ItemAndListCommonInterface) {
                    if (((ItemAndListCommonInterface) p).isNoSave()) {
                        continue; //skip nosave elements
                    } else {
                        if (true) {
                            ((ItemAndListCommonInterface) p).onSave();
                        }
                    }
                }

                if (p instanceof Item) {
                    saveItemNew3((Item) p, saveList, afterParseUpdate);
//                    checkNoUnsavedReferences((Item) p,"");
                } else if (p instanceof Category) { //also covers Category, and ItemListList and CategoryList
                    saveCategoryNew3((Category) p, saveList, afterParseUpdate);
                } else if (p instanceof ItemList) { //also covers Category, and ItemListList and CategoryList
                    saveItemListNew3((ItemList) p, saveList, afterParseUpdate);
                } else if (p instanceof WorkSlot) { //e.g. Category, WorkSlot
                    saveWorkSlotNew3((WorkSlot) p, saveList, afterParseUpdate);
                } else if (p instanceof RepeatRuleParseObject) {
                    saveRepeatRuleNew3((RepeatRuleParseObject) p, saveList, afterParseUpdate);
                } else if (p instanceof FilterSortDef) { //e.g. Category, WorkSlot
                    saveFilterSortDefNew3((FilterSortDef) p, saveList, afterParseUpdate);
                } else if (p instanceof TimerInstance2) {
                    saveTimerInstanceNew3((TimerInstance2) p, saveList, afterParseUpdate);
                } else { //e.g. Category, WorkSlot
                    if (Config.TEST) {
                        ASSERT.that(false, () -> "type of object not handled, p=" + p + "; list=" + toSaveListCopy);
                    }
                }
            }
            processedList.clear();

            if (!waitForCompletion) {
                setSavePending(saveList, true); //mark all to-be-saved parseobjects as pending save to prevent modification (via .set()) until saved
            }

            try {
                ParseBatch batchSav = batchSavePrepare(saveList, deleteList); //add save/create/delete objecs to batch and send it
                if (batchSav != null) {
                    batchSav.execute();
                }
            } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
                Log.e(ex);
//                throw ex;
            } catch (ParseException ex) { //encoding issue
                Log.e(ex);
//                throw ex;
            }

            for (ParseObject o : deleteList) {
                if (o.getObjectIdP() == null) { //parseObject.reset is called after succesfull delete, setting objectiId=null
                    cacheDelete(o);
                    toDeleteList.remove(o);
                }
            }
//            deleteList.clear(); //delete takes only one iteration, where save/create may take two
            savePendingParseUpdatesToLocalstorage(null, toDeleteList); //save lists with any remaining items locally

            //update to restore relations to now created objects
            for (Runnable lambda : afterParseUpdate) {
                //any objects 
                lambda.run(); //will update the list saveList passed as argument above (so NO global variable changed!) //global variable toSaveList(!)
            }
            afterParseUpdate.clear(); //clear so only done once

//<editor-fold defaultstate="collapsed" desc="comment">
//            List<ParseObject> stillNeedsSaving = new ArrayList();
//            if (false) {
//                for (ParseObject p : saveList) {
//                    if (p.isDirty()) { //if dirty after restoring refs to previously uncreated ParseObjects vai the Runnables in afterParseUpdate
//                        stillNeedsSaving.add(p);
////                    p.setSaveIsPending(true); //need to set true again so don't get overwritten while waiting for save in background to complete in below lambda function
//                    } else {
//                        if (!waitForCompletion) {
//                            p.setSaveIsPending(false); //need to set true again so don't get overwritten while waiting for save in background to complete in below lambda function
//                        }
//                        if (p instanceof ItemAndListCommonInterface) {
//                            ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                        }
//                        cachePut(p); //update cache once the element is finally saved!
//                    }
//                }
//                Iterator<ParseObject> it = saveList.iterator();
//                while (it.hasNext()) {
//                    ParseObject p = it.next();
//                    if (!p.isDirty()) { //if dirty after restoring refs to previously uncreated ParseObjects vai the Runnables in afterParseUpdate
//                        if (!waitForCompletion) {
//                            p.setSaveIsPending(false); //need to set true again so don't get overwritten while waiting for save in background to complete in below lambda function
//                        }
//                        if (p instanceof ItemAndListCommonInterface) {
//                            ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                        }
//                        cachePut(p); //update cache once the element is finally saved!
//                        it.remove(); //remove from saveList
//                    }
//                }
//            }
//</editor-fold>
            if (Config.TEST) {
                Log.p("saveRunnable1 END");
            }

        };

        Runnable saveRunnable2 = () -> {
            if (Config.TEST) {
                Log.p("saveRunnable2 START");
            }
//            if (!stillNeedsSaving.isEmpty()) {
//            if (!saveList.isEmpty()) {
            try {
//                    ParseBatch batchSav = batchSavePrepare(stillNeedsSaving, null);
                ParseBatch batchSav = batchSavePrepare(saveList, null);
                if (batchSav != null) {
                    batchSav.execute();
                }
            } catch (ParseException ex) { //encoding issue
                Log.e(ex);
            }

//                for (ParseObject p : stillNeedsSaving) {
            for (ParseObject p : saveList) {
                ASSERT.that(!p.isDirty(), () -> "ERROR: parseObject still dirty after 2nd save, parseObject=" + p);
                if (true || !waitForCompletion) { //make sure it is ALWAYS set to false, otherwise may HALT UI
                    p.setSaveIsPending(false);
                }
                if (p instanceof ItemAndListCommonInterface) {
                    ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
                }
                cachePut(p); //update cache once the element is finally saved!
            }
//            }
            //now the complete save-cycle has been completed, remove successfully saved parseObjects from toSaveList and save the cleaned up version
            Iterator<ParseObject> it = toSaveList.iterator();
            while (it.hasNext()) {
                ParseObject o = it.next();
                if (!o.isDirty()) { //only remove not dirty objects, for the case where a saved object has become dirty again after saving
//                        toSaveList.remove(o);
                    it.remove();
                } else {
//                    ASSERT.that(!(o instanceof ItemAndListCommonInterface) && ((ItemAndListCommonInterface) o).isNoSave(), //only noSave should remain dirty
                    ASSERT.that(!(o instanceof ItemAndListCommonInterface) || ((ItemAndListCommonInterface) o).isNoSave(), //only noSave should remain dirty
                            () -> "Unexpected: unsaved element in toSaveList, elt=" + o + "; toSaveList=" + toSaveList);
                }
            }
//            savePendingParseUpdatesToLocalstorage(toSaveList, deleteList); //save lists with any remaining items locally
            savePendingParseUpdatesToLocalstorage(toSaveList, null); //save lists with any remaining items locally
            if (Config.TEST) {
                Log.p("saveRunnable2 END");
            }

        }; //end lambda

        if (waitForCompletion) {
            saveRunnable1.run();
            saveRunnable2.run();
        } else {
            Display.getInstance().callSerially(saveRunnable1);
            Display.getInstance().callSerially(saveRunnable2);
        }
//        ASSERT.that(); //        }//synchronized
        //        }
    }

//<editor-fold defaultstate="collapsed" desc="comment">
//    public void triggerParseUpdateNewXXX(boolean waitForCompletion) {
//        //idea:
//        //at this point, all parseObjects should be created in memory, *including* repeat instances, so the screen has been refreshed
//        //problem: if an object is edited or changed while temporarily altered (references to unsaved objects removed)
//
//        synchronized (toSaveList) {
////        savePendingParseUpdatesToLocalstorage(saveList, deleteList); //save modified items locally
//            savePendingParseUpdatesToLocalstorage(toSaveList, deleteList); //save modified items locally
//            setSavePending(saveList, true); //protect from change until saved
//
//            Runnable saveRunnable = () -> {            //process toSaveList to create saveList with all updated (incl. indirectly) parseobjects to save
//                for (ParseObject p : toSaveList) {
//                    if (p instanceof ItemAndListCommonInterface) {
//                        if (((ItemAndListCommonInterface) p).isNoSave()) {
//                            continue; //skip nosave elements
//                        } else {
//                            if (true) {
//                                assert false;
//                                ((ItemAndListCommonInterface) p).updateBeforeSave();
//                            }
//                        }
//                    }
//
//                    if (p instanceof Item) {
//                        saveItemNew3((Item) p);
////                    checkNoUnsavedReferences((Item) p,"");
//                    } else if (p instanceof Category) { //also covers Category, and ItemListList and CategoryList
//                        saveCategoryNew3((Category) p);
//                    } else if (p instanceof ItemList) { //also covers Category, and ItemListList and CategoryList
//                        saveItemListNew3((ItemList) p);
//                    } else if (p instanceof WorkSlot) { //e.g. Category, WorkSlot
//                        saveWorkSlotNew3((WorkSlot) p);
//                    } else if (p instanceof RepeatRuleParseObject) {
//                        saveRepeatRuleNew3((RepeatRuleParseObject) p);
//                    } else if (p instanceof FilterSortDef) { //e.g. Category, WorkSlot
//                        saveFilterSortDefNew3((FilterSortDef) p);
//                    } else { //e.g. Category, WorkSlot
//                        if (Config.TEST) {
//                            ASSERT.that(false, () -> "type of object not handled, p=" + p + "; list=" + toSaveList);
//                        }
//                    }
//                }
//
//                //pre-processes: remove all refs to unsaved items and create lmbdas to restablish them
//                for (Runnable r : beforeParseUpdate) {
//                    r.run();
//                }
//                beforeParseUpdate.clear();
//
//                if (Config.TEST) {
//                    checkNoUnsavedReferences(saveList, "");
//                }
//
//                try {
//                    ParseBatch batchSav = batchSavePrepare(saveList, deleteList); //add save/create/delete objecs to batch and send it
//                    batchSav.execute();
//                } catch (ParseException ex) { //encoding issue
//                    Log.e(ex);
//                } catch (IllegalStateException ex) { //generated when trying to save reference to unsaved ParseObject
//                    Log.e(ex);
//                }
//
//                for (ParseObject o : deleteList) {
//                    cacheDelete(o);
//                }
//                deleteList.clear(); //delete takes only one iteration, where save/create may take two
//
//                //update to restore relations to now created objects
//                for (Runnable lambda : afterParseUpdate) {
//                    lambda.run();
//                }
//                afterParseUpdate.clear(); //clear so only done once
//
//                List<ParseObject> stillNeedsSaving = new ArrayList();
//                for (ParseObject p : saveList) {
//                    if (p.isDirty()) { //if dirty after restoring refs to previously uncreated ParseObjects vai the Runnables in afterParseUpdate
//                        stillNeedsSaving.add(p);
//                        p.setSaveIsPending(true); //need to set true again so don't get overwritten while waiting for save in background to complete in below lambda function
//                    } else {
//                        if (p instanceof ItemAndListCommonInterface) {
//                            ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                        }
//                    }
//                }
//
//                try {
//                    ParseBatch batchSav = batchSavePrepare(stillNeedsSaving, null);
//                    batchSav.execute();
//                } catch (ParseException ex) { //encoding issue
//                    Log.e(ex);
//                }
//
//                for (ParseObject p : stillNeedsSaving) {
//                    ASSERT.that(!p.isDirty(), () -> "ERROR: parseObject still dirty after 2nd save, parseObject=" + p);
//                    p.setSaveIsPending(false);
//                    if (p instanceof ItemAndListCommonInterface) {
//                        ((ItemAndListCommonInterface) p).updateAfterSave(); //set eg alarms for items (need a objectId to work)
//                    }
//                }
//
//                //now the complete save-cycle has been completed, remove successfully saved parseObjects from toSaveList and save the cleaned up version
//                Iterator<ParseObject> it = toSaveList.iterator();
//                while (it.hasNext()) {
//                    ParseObject o = it.next();
//                    if (!o.isDirty()) { //only remove not dirty objects, for the case where a saved object has become dirty again after saving
//                        toSaveList.remove(o);
//                    } else {
//                        ASSERT.that(false, () -> "Unexpected: unsaved element in toSaveList, elt=" + o + "; toSaveList=" + toSaveList);
//                    }
//                }
//                savePendingParseUpdatesToLocalstorage(toSaveList, deleteList); //save lists with any remaining items locally
//            }; //end lambda
//
//            if (waitForCompletion) {
//                saveRunnable.run();
//            } else {
//                Display.getInstance().callSerially(saveRunnable);
//            }
//        }
////        }
//    }
//</editor-fold>
    public void saveToParseNow(Collection<ParseObject> parseObjects) {
        saveNewImpl(parseObjects, true, false);
    }

    public void saveToParseLater(Collection<ParseObject> parseObjects) {
        saveNewImpl(parseObjects, false, false);
    }
//<editor-fold defaultstate="collapsed" desc="comment">
//    public void saveToParseNow(ParseObject anyParseObject) {
//        if (anyParseObject != null) {
//            saveNewImpl(anyParseObject, true);
//        }
//    }
//    public void saveToParseLater(ParseObject anyParseObject) {
//        if (anyParseObject != null) {
//            saveToParseLater(anyParseObject);
//        }
//    }
//</editor-fold>

    public void saveToParseNow(ParseObject... parseObjects) {
//        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), postSaveAction, triggerSave);
//        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), true, false);
        saveNewImpl(Arrays.asList(parseObjects), true, false);
    }

    public void saveToParseNow(boolean saveObj1, ParseObject parseObject1, boolean saveObj2, ParseObject parseObject2) {
//        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), postSaveAction, triggerSave);
        if (saveObj1 && saveObj2) {
            saveNewImpl(Arrays.asList(parseObject1, parseObject2), true, false);
        } else if (saveObj1) {
            saveNewImpl(Arrays.asList(parseObject1), true, false);
        } else if (saveObj2) {
            saveNewImpl(Arrays.asList(parseObject2), true, false);
        }
    }

    public void saveToParseAndWait(ParseObject... parseObjects) {
        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), true, true);
    }

    public void saveToParseAndWait(Collection<ParseObject> parseObjects) {
        saveNewImpl(parseObjects, true, true);
    }

    public void saveToParseLater(ParseObject... parseObjects) {
        saveNewImpl(new ArrayList(Arrays.asList(parseObjects)), false, false);
    }

    public void saveNew(Object item) {
        //do nothing, this shoudl be handled by eg saveToParseNow(saveList) which should find all linked and modified objects and save them
    }

}
